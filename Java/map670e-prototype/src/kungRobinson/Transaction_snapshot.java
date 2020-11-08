package kungRobinson;

import java.util.ArrayList;
import java.util.Vector;
import java.util.List;

import database.*;
import map670e.DataGeneration;

public class Transaction_snapshot {
	// Constants : phase names
	public static final String phase_READ = "READ";
	public static final String phase_VALIDATE = "VALIDATE";
	public static final String phase_WRITE = "WRITE";
	public static final String phase_FINISH = "FINISH";

	// Attributes
	private int id;
	private String phase;
	private double ts_start_read;
	private double ts_finish_read;
	private double ts_start_validate;
	private double ts_finish_validate;
	private double ts_start_write;
	private double ts_finish_write;
	private ArrayList<Object> read_set;
	private ArrayList<Object> write_set;
	private int w_id;
	private Database db_original;
	private Database db;// db is a copy of db_original
	private DataGeneration query;
	private int nb_restarts;

	public Transaction_snapshot(final int id, int w_id, Database db_original) {
		this.id = id;
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_finish_read = 0;
		this.ts_start_validate = 0;
		this.ts_finish_validate = 0;
		this.ts_start_write = 0;
		this.ts_finish_write = 0;
		new ArrayList<Object>();
		new ArrayList<Object>();
		new ArrayList<Types>();
		this.read_set = new ArrayList<Object>();
		this.write_set = new ArrayList<Object>();
		this.nb_restarts = 0;

		// Fixed Targets.
		this.w_id = w_id;
		this.query = new DataGeneration(w_id);
		this.db_original = db_original;

	}

	public boolean is_finished() {
		return this.ts_finish_write > 0;
	}

	public double get_ts_start_read() {
		return this.ts_start_read;
	}

	public double get_ts_finish_read() {
		return ts_finish_read;
	}

	public double get_ts_start_validate() {
		return this.ts_start_validate;
	}

	public double get_ts_finish_validate() {
		return ts_finish_validate;
	}

	public double get_ts_start_write() {
		return this.ts_start_write;
	}

	public double get_ts_finish_write() {
		return this.ts_finish_write;
	}

	public ArrayList<Object> get_write_set() {
		return this.write_set;
	}

	public ArrayList<Object> get_read_set() {
		return this.read_set;
	}

	public ArrayList<Object> list_intersect(ArrayList<Object> list1, ArrayList<Object> list2) {
		// return the common elements of list1 and list2
		ArrayList<Object> intersect = new ArrayList<Object>();
		for (int i = 0; i < list1.size(); i++) {
			// If this list element is in the other list
			if (list2.contains(list1.get(i))) {
				intersect.add(list1.get(i));
			}
		}
		return intersect;
	}

	public boolean empty_intersect(ArrayList<Object> list1, ArrayList<Object> list2) {
		// Return true if lis1 and list2 have an empty intersection
		for (int i = 0; i < list1.size(); i++) {
			if (list2.contains(list1.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean read() {
		// READ phase
		db = new Database(db_original); // create copy of database

		int cnt = 0;
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
		this.read_set.add(d);
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

		NewOrder nwd = new NewOrder(d_next_o_id, d_id, w_id);
		Order ord = new Order(d_next_o_id, c_id, d_id, w_id);
		ord.set_o_ol_cnt(this.query.get_number_items());
		for (int i = 1; i < this.query.get_ol_suppliers().size(); i++) {
			if ((this.query.get_ol_suppliers()).get(i) != (this.query.get_ol_suppliers()).get(0)) {
				ord.set_o_all_local(0);
				break;
			}
		}

		// we commit nwd & ord to the database
		this.db.setObject(nwd, Types.NEWORDER);
		this.db.setObject(ord, Types.ORDER);
		cnt += 2; // 2 times one for nwd and one for ord

		Vector<Integer> ol_identifiers = this.query.get_ol_identifiers();
		Vector<Integer> ol_suppliers = this.query.get_ol_suppliers();
		Vector<Double> quantity = this.query.get_ol_quantities();
		int number_items = this.query.get_number_items();

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

			Item it = (Item) db.getObject(code_item, Types.ITEM);

			this.read_set.add((Item) db_original.getObject(code_item, Types.ITEM));
			this.write_set.add((Item) db_original.getObject(code_item, Types.ITEM));

			if (it == null) {
				System.out.println("ERROR : item nÂ°" + i + " does not exist (transaction " + this.id + ")");
				return false;
			}
			double i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();
			cnt++;
			// We read the stock
			Stock fake_s = new Stock(item_id, supplier_id);
			int code_stock = fake_s.hashCode();

			Stock s = (Stock) db.getObject(code_stock, Types.STOCK);

			this.read_set.add((Stock) db_original.getObject(code_stock, Types.STOCK));
			this.write_set.add((Stock) db_original.getObject(code_stock, Types.STOCK));

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
			cnt++;

			Order_Line ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);
			cnt++;

			this.db.setObject(ol, Types.ORDER_LINE);

			// we must commit ol to the database
			total_amount += ol_amount * (c_discount - 1) * (1 + tax + d_tax);

		}
		System.out.println("The transaction " + this.id + " has been completed " + "(" + cnt + " operations, "
				+ total_amount + "€).");

		return true;

	}

	private boolean validate(ArrayList<Transaction_snapshot> other_transactions) {
		for (int i = 0; i < other_transactions.size(); i++) {
			Transaction_snapshot transaction = other_transactions.get(i);
			// We only test this transaction for the other transactions that started BEFORE
			// current one
			if (transaction.get_ts_start_read() < this.ts_start_read && transaction.get_ts_start_read() > 0) {
				boolean test_1 = false;
				boolean test_2 = false;
				boolean test_3 = false;
				// Test 1
				if (transaction.is_finished()) {
					test_1 = transaction.get_ts_finish_write() < this.ts_start_read;
					// System.out.println("test_1 " + test_1);
				}

				// Test 3
				if (transaction.get_ts_finish_read() > 0) {
					// This test is complex. We break it down in 3 subtests.
					boolean subtest_3_1 = transaction.get_ts_finish_read() < this.ts_finish_read;
					boolean subtest_3_2 = this.empty_intersect(transaction.get_write_set(), this.read_set);
					boolean subtest_3_3 = this.empty_intersect(transaction.get_write_set(), this.write_set);
					/*
					 * System.out.println("subtest_3_1 " + subtest_3_1);
					 * System.out.println("subtest_3_2 " + subtest_3_2);
					 * System.out.println("subtest_3_3 " + subtest_3_3);
					 */
					test_3 = subtest_3_1 && subtest_3_2 && subtest_3_3;
				}

				// Test 2
				if (transaction.is_finished()) {
					// In the slides, it said to check ts_start_write.
					// We, however, never have a ts_start_write since we are in the validation
					// phase.
					// Consequently, we replace ts_start_write by the current timestamp.
					// That's why we put this test last.
					boolean subtest_2_1 = transaction.get_ts_finish_write() < System.currentTimeMillis();
					boolean subtest_2_2 = this.empty_intersect(transaction.get_write_set(), this.read_set);
					/*
					 * System.out.println("subtest_2_1 " + subtest_2_1);
					 * System.out.println("subtest_2_2 " + subtest_2_2);
					 */
					test_2 = subtest_2_1 && subtest_2_2;
				}

				// Did any test pass ?
				Boolean any_test_passed = test_1 || test_2 || test_3;
				if (!any_test_passed) {
					// Every test failed. There is a conflict. Validation failed. Return False.
					return false;
				}
			}
		}
		// We succeeded in every test. Return true.
		return true;
	}

	private boolean write() {

		this.db_original.Update(this.db);
		return true;
	}

	public boolean apply_next(ArrayList<Transaction_snapshot> other_transactions) {
		if (this.is_finished()) {
			return true;
		}
		Boolean success = false;
		// We look at the current phase and set it to be the next one
		if (this.phase == "") {
			this.phase = phase_READ;
			this.ts_start_read = System.currentTimeMillis();
			System.out.println("Reading " + this.id);
			success = this.read();
			this.ts_finish_read = System.currentTimeMillis();
		} else if (this.phase == phase_READ) {
			this.phase = phase_VALIDATE;
			this.ts_start_validate = System.currentTimeMillis();
			System.out.println("Validating " + this.id);
			success = this.validate(other_transactions);
			if (!success) {
				// Validation failed : restart the transaction.
				System.out.println("Validation failed for " + this.id);
				this.restart();
			}
			this.ts_finish_validate = System.currentTimeMillis();

		} else if (this.phase == phase_VALIDATE) {
			this.phase = phase_WRITE;
			this.ts_start_write = System.currentTimeMillis();
			System.out.println("Writing " + this.id);
			success = this.write();
			this.ts_finish_write = System.currentTimeMillis();

		}
		return success;
	}

	public void restart() {
		// Restart the operation
		System.out.println("Restarting " + this.id);
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_finish_read = 0;
		this.ts_start_validate = 0;
		this.ts_finish_validate = 0;
		this.ts_start_write = 0;
		this.ts_finish_write = 0;
		new ArrayList<Object>();
		new ArrayList<Object>();
		new ArrayList<Types>();
		this.read_set = new ArrayList<Object>();
		this.write_set = new ArrayList<Object>();
		this.nb_restarts++;
		
	}

	public int get_nb_restarts() {
		return this.nb_restarts;
	}
}
