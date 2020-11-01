package map670e;

import database.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NewOrderTransactionLock implements Runnable {

	private final int transaction_id;
	private int w_id;
	private Database db;
	private LockManager lockmanager;
	private DataGeneration data;
	private LocalTime ts;

	public NewOrderTransactionLock(int transaction_id, int w_id, Database d, LockManager lockmanager) {
		this.transaction_id = transaction_id;
		this.w_id = w_id;
		this.data = new DataGeneration(w_id);
		this.db = d;
		this.lockmanager = lockmanager;
		this.ts = LocalTime.now() ;
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
		double total_amount = 0;

		// We retrieve the warehouse, and then read the value we need.
		Warehouse fake_wh = new Warehouse(this.w_id) ;
		
		ReadLock read1 = new ReadLock(this.transaction_id, this.db, this.lockmanager, fake_wh.hashCode(), fake_wh, Types.WAREHOUSE, this.ts);
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
		if (w == null) {
			System.out.println("ERROR : warehouse does not exist (transaction " + this.transaction_id + ")" );
			return false ;
		}
		double tax = w.get_w_tax(); // we get the tax
		cnt++;
		
		// We retrieve the district, and read the useful values.
		int d_id = data.get_d_id(); // we get the district number
		District fake_d = new District(d_id, w_id) ;
		int d_code = fake_d.hashCode() ;
		
		ReadLock read2 = new ReadLock(this.transaction_id, this.db, this.lockmanager, d_code, fake_d, Types.DISTRICT, this.ts);
		status = read2.applyLock() ;
		if (status == Status.ABORT) {
			return false ;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit");
			TimeUnit.MILLISECONDS.sleep(300);
			this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
			status = read2.applyLock() ;
		}
		
		District d = (District) read2.apply();
		if (d == null) {
			System.out.println("ERROR : district does not exist (transaction " + this.transaction_id + ")" );
			return false ;
		}
		double d_tax = d.get_d_tax();
		int d_next_o_id = d.get_d_next_o_id() + 1;
		cnt++;

		// We retrieve the client, and read the useful values.
		int c_id = data.get_c_id();
		Customer fake_c = new Customer(c_id, d_id, w_id) ;
		int c_code = fake_c.hashCode();
		
		ReadLock read3 = new ReadLock(this.transaction_id, this.db, this.lockmanager, c_code, fake_c, Types.CUSTOMER, this.ts);
		status = read3.applyLock() ;
		if (status == Status.ABORT) {
			return false ;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit");
			TimeUnit.MILLISECONDS.sleep(300);
			this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
			status = read3.applyLock() ;
		}

		Customer c = (Customer) read3.apply();
		if (c == null) {
			System.out.println("ERROR : customer does not exist (transaction " + this.transaction_id + ")" );
			return false ;
		}
		String c_last = c.get_c_last();
		double c_discount = c.get_c_discount();
		String c_credit = c.get_c_credit();
		cnt++;

		// We place a NewOrder and Order
		NewOrder nwd = new NewOrder(d_next_o_id, d_id, w_id);
		Order ord = new Order(d_next_o_id, c_id, d_id, w_id);
		ord.set_o_ol_cnt(data.get_number_items());
		for (int i = 1; i < data.get_ol_suppliers().size(); i++) {
			if ((data.get_ol_suppliers()).get(i) != (data.get_ol_suppliers()).get(0)) {
				ord.set_o_all_local(0);
				break;
			}
		}

		
		WriteLock write1 = new WriteLock(this.transaction_id, this.db, this.lockmanager, ord, Types.ORDER, this.ts);
		status = write1.applyLock() ;
		if (status == Status.ABORT) {
			return false ;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit");
			TimeUnit.MILLISECONDS.sleep(300);
			this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
			status = write1.applyLock() ;
		}
		write1.apply();
		cnt++;
		
		
		WriteLock write2 = new WriteLock(this.transaction_id, this.db, this.lockmanager, nwd, Types.NEWORDER, this.ts);
		status = write2.applyLock() ;
		if (status == Status.ABORT) {
			return false ;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit");
			TimeUnit.MILLISECONDS.sleep(300);
			//this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
			status = write2.applyLock() ;
		}
		write2.apply();
		cnt++;
		
		// ???
		ArrayList<Integer> ol_identifiers = data.get_ol_identifiers();
		ArrayList<Integer> ol_suppliers = data.get_ol_suppliers();
		ArrayList<Double> quantity = data.get_ol_quantities();
		int number_items = data.get_number_items();
		

		
		
		for (int i = 0; i < number_items; i++) {
			int item_id = ol_identifiers.get(i);
			int supplier_id = ol_suppliers.get(i);
			if (ol_identifiers.get(i) == null) {
				System.out.println("Value not found");
				return false;
			}
			// We read the item
			Item fake_i = new Item(item_id) ;
			int code_item = fake_i.hashCode();
			ReadLock read4 = new ReadLock(this.transaction_id, this.db, this.lockmanager, code_item, fake_i, Types.ITEM, this.ts);
			status = read4.applyLock() ;
			if (status == Status.ABORT) {
				return false ;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit");
				TimeUnit.MILLISECONDS.sleep(300);
				this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
				status = read4.applyLock() ;
			}
			Item it = (Item) read4.apply();
			if (it == null) {
				System.out.println("ERROR : item n°" + i + " does not exist (transaction " + this.transaction_id + ")" );
				return false ;
			}
			double i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();

			// We read the stock
			Stock fake_s = new Stock(item_id, supplier_id);
			int code_stock = fake_s.hashCode();
			ReadLock read5 = new ReadLock(this.transaction_id, this.db, this.lockmanager, code_stock, fake_s, Types.STOCK, this.ts);
			status = read5.applyLock() ;
			if (status == Status.ABORT) {
				return false ;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit");
				TimeUnit.MILLISECONDS.sleep(300);
				this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
				status = read5.applyLock() ;
			}
			Stock s = (Stock) read5.apply();
			if (s == null) {
				System.out.println("ERROR : stock does not exist (transaction " + this.transaction_id + ")" );
				return false ;
			}
			
			String s_data = s.get_s_data();
			double s_quantity = s.get_s_quantity();
			String s_dis_id = s.get_s_id(d_id);

			// We update the stock -> to make things easier, we directly change the
			// attribute we read.
			// Instead of writing an updated copy of the stock
			// We'll need to put X locks in the lock-based transaction
			WriteLock write3 = new WriteLock(this.transaction_id, this.db, this.lockmanager, fake_s, Types.STOCK, this.ts);
			status = write3.applyLock() ;
			if (status == Status.ABORT) {
				return false ;
			} else if (status == Status.WAIT) {
				System.out.println("WESHSS: " + item_id + "," + supplier_id);
				System.out.println("Transaction " + this.transaction_id + " waits a bit");
				TimeUnit.MILLISECONDS.sleep(300);
				this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
				status = write3.applyLock() ;
			}
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

			// Here we update the item -> need XLock
			WriteLock write4 = new WriteLock(this.transaction_id, this.db, this.lockmanager, fake_i, Types.ITEM, this.ts);
			status = write4.applyLock() ;
			if (status == Status.ABORT) {
				return false ;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit");
				TimeUnit.MILLISECONDS.sleep(300);
				this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
				status = write4.applyLock() ;
			}
			double ol_amount = quantity.get(i) * i_price;
			if (s_data.indexOf("ORIGINAL") >= 0 && i_data.indexOf("ORIGINAL") >= 0) {
				it.set_i_data("B");
			} else {
				it.set_i_data("G");
			}
			cnt++;

			// We add the OL to the DB
			Order_Line ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);
			
			WriteLock write5 = new WriteLock(this.transaction_id, this.db, this.lockmanager, ol, Types.ORDER_LINE, this.ts);
			status = write5.applyLock() ;
			if (status == Status.ABORT) {
				return false ;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit");
				TimeUnit.MILLISECONDS.sleep(300);
				this.ts = LocalTime.now() ; // update TimeStamp of Operation -> to check later
				status = write5.applyLock() ;
			}
			write5.apply();
			cnt++;

			total_amount += ol_amount * (1 - c_discount) * (1 + tax + d_tax);
			
			// We release the locks of this loop
			this.lockmanager.remove_locks(ol, false, this.transaction_id) ; // Lock on the OrderLine
			this.lockmanager.remove_locks(fake_i, false, this.transaction_id) ; // Lock on the Item
			this.lockmanager.remove_locks(fake_s, false, this.transaction_id) ; // Lock on the Stock
			this.lockmanager.remove_locks(fake_s, true, this.transaction_id) ; // Lock on the Stock
			this.lockmanager.remove_locks(fake_i, true, this.transaction_id) ; // Lock on the Item
		}
		System.out.println("The transaction " + transaction_id + " is now releasing its last locks.");
		this.lockmanager.remove_locks(nwd, false, this.transaction_id) ; // Lock on the NewOrder
		this.lockmanager.remove_locks(ord, false, this.transaction_id) ; // Lock on the Order
		this.lockmanager.remove_locks(d, true, this.transaction_id) ; // Lock on the District
		this.lockmanager.remove_locks(w, true, this.transaction_id) ; // Lock on the Warehouse
		
		
		System.out.println("The transaction " + transaction_id + " has been completed " + "(" + cnt + " operations, "
				+ total_amount + "€).");

		return true ;

	}


	@Override
	public int hashCode() {
		return Objects.hash(transaction_id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NewOrderTransactionLock)) {
			return false;
		}
		NewOrderTransactionLock other = (NewOrderTransactionLock) obj;
		return transaction_id == other.transaction_id;
	}
}
