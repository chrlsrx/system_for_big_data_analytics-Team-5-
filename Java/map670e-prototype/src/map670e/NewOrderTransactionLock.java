package map670e;

import database.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NewOrderTransactionLock {

	private final int transaction_id;
	private int w_id;
	private Database db;
	private LockManager lockmanager;
	private DataGeneration data;
	private LocalTime ts;

	public NewOrderTransactionLock(int transaction_id, int w_id, Database d, LockManager lockmanager, LocalTime time) {
		this.transaction_id = transaction_id;
		this.w_id = w_id;
		this.data = new DataGeneration(w_id);
		this.db = d;
		this.lockmanager = lockmanager;
		this.ts = time ;
	}

	
	public void run() {
		try {
            System.out.println("Transaction " + this.transaction_id + " is starting.");
            boolean ok = this.runTransaction() ;
            if (!ok) {
            	 System.out.println("Transaction " + this.transaction_id + " is aborting.");
            }
        } catch (InterruptedException e) {
        	System.out.println("UNEXPECTED ERROR");
            e.printStackTrace();
        }
	}
	
	
	public boolean runTransaction() throws InterruptedException {

		int cnt = 0;

		// We retrieve the warehouse, and then read the value we need.
		Warehouse fake_wh = new Warehouse(this.w_id) ;
		ReadLock read1 = new ReadLock(cnt, this.db, this.lockmanager, this.w_id, fake_wh, Types.WAREHOUSE, this.ts);
		Status status = read1.applyLock() ;
		if (status == Status.ABORT) {
			return false ;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit");
			TimeUnit.MILLISECONDS.sleep(300);
			this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
			status = read1.applyLock() ;
		}
		Warehouse w = (Warehouse) read1.apply();
		int tax = w.get_w_tax(); // we get the tax
		cnt++;

		// Toute la suite est à adapter
		
		// We retrieve the district, and read the useful values.
		int d_id = data.get_d_id(); // we get the district number
		int d_code = (new District(w_id, d_id)).hashCode();
		Read read2 = new Read(cnt, this.db, d_code, Types.DISTRICT);
		District d = (District) read2.apply();
		double d_tax = d.get_d_tax();
		int d_next_o_id = d.get_d_next_o_id() + 1;
		cnt++;

		// We retrive the client, and read the useful values.
		int c_id = data.get_c_id();
		int c_code = (new Customer(c_id, d_id, w_id)).hashCode();
		Read read3 = new Read(cnt, this.db, d_code, Types.DISTRICT);
		Customer c = (Customer) read3.apply();
		String c_last = c.get_c_last();
		double c_discount = c.get_c_discount();
		String c_credit = c.get_c_credit();
		cnt++;

		// We place a NewOrder an Order
		NewOrder nwd = new NewOrder(d_next_o_id, d_id, w_id);
		Order ord = new Order(d_next_o_id, c_id, d_id, w_id);
		ord.set_o_ol_cnt(data.get_number_items());
		for (int i = 1; i < data.get_ol_suppliers().size(); i++) {
			if ((data.get_ol_suppliers()).get(i) != (data.get_ol_suppliers()).get(0)) {
				ord.set_o_all_local(0);
				break;
			}
		}
		Write write1 = new Write(cnt, this.db, ord, Types.ORDER);
		write1.apply();
		cnt++;
		Write write2 = new Write(cnt, this.db, nwd, Types.NEWORDER);
		write2.apply();
		cnt++;

		// ???
		ArrayList<Integer> ol_identifiers = data.get_ol_identifiers();
		ArrayList<Integer> ol_suppliers = data.get_ol_suppliers();
		ArrayList<Double> quantity = data.get_ol_quantities();
		int number_items = data.get_number_items();
		double total_amount;

		for (int i = 0; i < number_items; i++) {
			int item_id = ol_identifiers.get(i);
			int supplier_id = ol_suppliers.get(i);
			if (ol_identifiers.get(i) == null) {
				System.out.println("Value not found");
				return; // Maybe return something, will see later
			}
			// We read the item
			int code_item = (new Item(item_id)).hashCode();
			Read read4 = new Read(cnt, this.db, code_item, Types.ITEM);
			Item it = (Item) read4.apply();
			cnt++;

			float i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();

			// We read the stock
			int code_stock = (new Stock(item_id, supplier_id)).hashCode();
			Read read5 = new Read(cnt, this.db, code_stock, Types.STOCK);
			Stock s = (Stock) read5.apply();
			cnt++;

			String s_data = s.get_s_data();
			double s_quantity = s.get_s_quantity();
			String s_dis_id = s.get_s_id(d_id);

			// We update the stock -> to make things easier, we directly change the
			// attribute we read.
			// Instead of writing an updated copy of the stock
			// We'll need to put X locks in the lock-based transaction
			if (s_quantity > quantity.get(i) + 10) {
				s_quantity -= quantity.get(i);
			} else {
				s_quantity = s_quantity - quantity.get(i) + 91;
			}
			cnt++;
			s.change_s_ytd(quantity.get(i));
			cnt++;
			s.change_s_order_cnt(1);
			cnt++;
			if (ol_suppliers.get(i) != w_id) {
				s.change_s_remote_cnt(1);
				cnt++;
			}

			// Here we update the item.
			double ol_amount = quantity.get(i) * i_price;
			if (s_data.indexOf("ORIGINAL") >= 0 && i_data.indexOf("ORIGINAL") >= 0) {
				it.set_i_data("B");
			} else {
				it.set_i_data("G");
			}
			cnt++;

			// We had the OL to the DB
			Order_Line ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);

			Write write3 = new Write(cnt, this.db, ol, Types.ORDER_LINE);
			write3.apply();
			cnt++;

			total_amount += ol_amount * (1 - c_discount) * (1 + tax + d_tax);

		}
		System.out.println("The transaction " + transaction_id + " has been completed " + "(" + cnt + " operations, "
				+ total_amount + "€).");

	}
}
