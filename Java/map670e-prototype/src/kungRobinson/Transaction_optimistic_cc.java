package kungRobinson;

import java.util.ArrayList;
import java.util.List;
import database.*;
import map670e.DataGeneration;
import java.util.Vector;

public class Transaction_optimistic_cc extends Transaction {

	private ArrayList<Object> all_targets; // Pointers towards all the objects we want to edit
	private ArrayList<Object> all_targets_copy; // Local copies of the targets
	private ArrayList<Types> all_targets_types;
	private NewOrder nwd;
	private Order ord;

	public Transaction_optimistic_cc(final int id, int w_id, Database db, Scheduler scheduler) {
		super(id, w_id, db, scheduler);
		this.all_targets = new ArrayList<Object>();
		this.all_targets_copy = new ArrayList<Object>();
		this.all_targets_types = new ArrayList<Types>();

	}

	@Override
	public boolean read() {
		// READ phase
		this.cnt = 0;
		double total_amount = 0;

		Warehouse fake_w = new Warehouse(this.w_id);
		Warehouse w = (Warehouse) db.getObject(fake_w.hashCode(), Types.WAREHOUSE);
		if (w == null) {
			System.out.println("ERROR : warehouse does not exist (transaction " + this.id + ")");
			return false;
		}
		double tax = w.get_w_tax(); // we get the tax
		this.read_set.add(w);
		cnt++;

		int d_id = this.query.get_d_id();
		District fake_d = new District(d_id, w_id);
		int d_code = fake_d.hashCode();

		District d = (District) db.getObject(fake_d.hashCode(), Types.DISTRICT);
		if (d == null) {
			System.out.println("ERROR : district does not exist (transaction " + this.id + ")");
			return false;
		}
		double d_tax = d.get_d_tax();
		int d_next_o_id = d.get_d_next_o_id() + 1;
		this.get_read_set().add(d);
		cnt++;

		// We retrieve the client, and read the useful values.
		int c_id = this.query.get_c_id();
		Customer fake_c = new Customer(c_id, d_id, w_id);

		Customer c = (Customer) db.getObject(fake_c.hashCode(), Types.CUSTOMER);
		if (c == null) {
			System.out.println("ERROR : customer does not exist (transaction " + this.id + ")");
			return false;
		}

		String c_last = c.get_c_last();
		double c_discount = c.get_c_discount();
		String c_credit = c.get_c_credit();
		this.read_set.add(c);
		cnt++;

		this.nwd = new NewOrder(d_next_o_id, d_id, w_id);
		this.ord = new Order(d_next_o_id, c_id, d_id, w_id);
		this.ord.set_o_ol_cnt(this.query.get_number_items());
		for (int i = 1; i < this.query.get_ol_suppliers().size(); i++) {
			if ((this.query.get_ol_suppliers()).get(i) != (this.query.get_ol_suppliers()).get(0)) {
				this.ord.set_o_all_local(0);
				break;
			}
		}

		// we must commit nwd & ord to the database
		// this.has_applied = this.db.setObject(this.target, this.target_type);
		// cnt++ 2 times one for nwd and one for ord

		Vector<Integer> ol_identifiers = this.query.get_ol_identifiers();
		Vector<Integer> ol_suppliers = this.query.get_ol_suppliers();
		Vector<Double> quantity = this.query.get_ol_quantities();
		int number_items = ol_identifiers.size(); // this.query.get_number_items();

		for (int i = 0; i < number_items; i++) {
			int item_id = ol_identifiers.get(i);
			int supplier_id = ol_suppliers.get(i);
			if (ol_identifiers.get(i) == null) {
				System.out.println("Value not found");
				return false;
			}
			// We read the item
			Item fake_i = new Item(item_id, supplier_id);
			int code_item = fake_i.hashCode();
			if ((Item) db.getObject(code_item, Types.ITEM) == null) {
				System.out.println("ERROR : item n°" + item_id + " for supplier " + supplier_id
						+ " does not exist (transaction " + this.id + ")");
				throw new IllegalArgumentException("Hello");
				// return false ;
			}

			Item it = new Item((Item) db.getObject(code_item, Types.ITEM));
			this.all_targets.add((Item) db.getObject(code_item, Types.ITEM));
			this.all_targets_copy.add(it);
			this.all_targets_types.add(Types.ITEM);
			this.read_set.add((Item) db.getObject(code_item, Types.ITEM));
			this.write_set.add((Item) db.getObject(code_item, Types.ITEM));

			if (it == null) {
				System.out.println("ERROR : item n°" + i + " does not exist (transaction " + this.id + ")");
				return false;
			}
			double i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();
			cnt++;
			// We read the stock
			Stock fake_s = new Stock(item_id, supplier_id);
			int code_stock = fake_s.hashCode();

			Stock s = new Stock((Stock) db.getObject(code_stock, Types.STOCK));
			this.all_targets.add((Stock) db.getObject(code_stock, Types.STOCK));
			this.all_targets_copy.add(s);
			this.all_targets_types.add(Types.STOCK);
			this.read_set.add((Stock) db.getObject(code_stock, Types.STOCK));
			this.write_set.add((Stock) db.getObject(code_stock, Types.STOCK));

			if (s == null) {
				System.out.println("ERROR : stock does not exist (transaction " + this.id + ")");
				return false;
			}

			String s_data = s.get_s_data();
			double s_quantity = s.get_s_quantity();
			String s_dis_id = s.get_s_id(d_id);

			if (s_quantity > quantity.get(i) + 10) {
				s_quantity -= quantity.get(i);
			} else {
				s_quantity = s_quantity - quantity.get(i) + 91;
			}
			s.change_s_ytd(quantity.get(i));
			s.change_s_order_cnt(1);
			if (ol_suppliers.get(i) != w_id) {
				s.change_s_remote_cnt(1);
			}
			cnt++;

			double ol_amount = quantity.get(i) * i_price;
			if (s_data.indexOf("ORIGINAL") >= 0 && i_data.indexOf("ORIGINAL") >= 0) {
				it.set_i_data("B");
			} else {
				it.set_i_data("G");
			}
			// cnt++;

			Order_Line ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);
			this.all_targets.add(ol); // useless
			this.all_targets_copy.add(new Order_Line(ol));
			this.all_targets_types.add(Types.ORDER_LINE);
			this.read_set.add(ol); // useless
			this.write_set.add(ol); // useless

			total_amount += ol_amount * (c_discount - 1) * (1 + tax + d_tax);

		}
		System.out.println("The transaction " + this.id + " has been completed " + "(" + cnt + " operations, "
				+ total_amount + "€).");

		return true;

	}

	@Override
	public boolean write() {
		int nb_targets = this.all_targets.size();
		for (int i = 0; i < nb_targets; i++) {

			// We all need to check item and stock because this transaction only write items
			// and stocks
			switch (this.all_targets_types.get(i)) {

			case ITEM:
				((Item) this.all_targets.get(i)).Update((Item) this.all_targets_copy.get(i));
				cnt++;
				break;

			case STOCK:
				((Stock) this.all_targets.get(i)).Update((Stock) this.all_targets_copy.get(i));
				cnt++;
				break;

			case ORDER_LINE:
				// Order lines don't exist in database, so we create them.
				this.db.setObject(this.all_targets_copy.get(i), Types.ORDER_LINE);
				cnt++;
				break;

			default:
				return false;
			}

		}

		this.db.setObject(this.nwd, Types.NEWORDER);
		this.db.setObject(this.ord, Types.ORDER);
		cnt += 2;
		return true;
	}

	@Override
	public void restart() {
		// Restart the operation
		super.restart();
		this.all_targets = new ArrayList<Object>();
		this.all_targets_copy = new ArrayList<Object>();
		this.all_targets_types = new ArrayList<Types>();
	}

}
