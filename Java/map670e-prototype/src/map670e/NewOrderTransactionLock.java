package map670e;

import database.*;

import java.time.LocalTime;
import java.util.Vector;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NewOrderTransactionLock implements Runnable {

	private final int transaction_id;
	private int w_id;
	private Database db;
	private LockManager lockmanager;
	private DataGeneration data;
	private LocalTime ts;
	private Scheduler sch;

	private Vector<Integer> objs_hash;
	private Vector<Boolean> isReads;

	public NewOrderTransactionLock(int transaction_id, int w_id, Database d, LockManager lockmanager, Scheduler sch,
			DataGeneration data) {
		this.transaction_id = transaction_id;
		this.w_id = w_id;
		this.data = new DataGeneration(w_id);
		this.db = d;
		this.lockmanager = lockmanager;
		this.ts = LocalTime.now();
		this.sch = sch;
		this.data = data;

		this.objs_hash = new Vector<Integer>();
		this.isReads = new Vector<Boolean>();
	}

	public void run() {
		try {
			// System.out.println("Transaction " + this.transaction_id + " is starting.");
			boolean ok = this.runTransaction();
			if (!ok) {
				// System.out.println("Transaction " + this.transaction_id + " is aborting.");
			}
		} catch (InterruptedException e) {
			System.out.println("UNEXPECTED ERROR");
			e.printStackTrace();
		}
	}

	public synchronized boolean runTransaction() throws InterruptedException {

		int cnt = 0;
		double total_amount = 0;

		/* ---- WAREHOUSE : READ ---- */

		// Create a fake warehouse with the same key.
		Warehouse fake_wh = new Warehouse(this.w_id);

		// Create a ReadLock, store the lock, and then try to put the lock on the
		// warehouse
		ReadLock read0 = new ReadLock(this.transaction_id, this.db, this.lockmanager, fake_wh.hashCode(), fake_wh,
				Types.WAREHOUSE, this.ts);
		this.store_lock(fake_wh.hashCode(), true);
		Status status = read0.applyLock();

		// If we put the lock : accepted, but otherwise...
		if (status == Status.ABORT) {
			this.sch.retry(this); // The transaction is added to a "failure" list.
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads); // We remove all the
																									// lock previously
																									// acquired
			System.out.println("Transaction " + this.transaction_id + " is aborting (72).");
			return false;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit (72).");
			TimeUnit.MILLISECONDS.sleep(200);
			status = read0.applyLock(); // Try once again.
		}

		// We apply the read, ie we get the TRUE warehouse.
		Warehouse w = (Warehouse) read0.apply();
		if (w == null) {
			System.out.println("ERROR : warehouse does not exist (transaction " + this.transaction_id + ")");
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			return false;
		}

		// We read only the values that we need.
		double tax = w.get_w_tax();
		cnt++; // We count the operations done.

		/* ---- DISTRICT : UPDATE (read & write) ---- */

		int d_id = data.get_d_id(); // we get the district number
		District fake_d = new District(d_id, w_id);
		int d_code = fake_d.hashCode();

		WriteLock update0 = new UpdateLock(this.transaction_id, this.db, this.lockmanager, fake_d, Types.DISTRICT,
				this.ts);
		this.store_lock(d_code, false);
		status = update0.applyLock();
		if (status == Status.ABORT) {
			this.sch.retry(this);
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			System.out.println("Transaction " + this.transaction_id + " is aborting (106).");
			return false;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit (106).");
			TimeUnit.MILLISECONDS.sleep(200);
			status = update0.applyLock();
		}

		District d = (District) ((UpdateLock) update0).applyRetrieve();
		if (d == null) {
			System.out.println("ERROR : district does not exist (transaction " + this.transaction_id + ")");
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			return false;
		}
		double d_tax = d.get_d_tax();
		int d_next_o_id = d.get_d_next_o_id();
		// ((UpdateLock) update0).applyUpdate();
		d.inc_next_o_id();
		cnt++;

		/* ---- CLIENT : READ ---- */

		int c_id = data.get_c_id();
		Customer fake_c = new Customer(c_id, d_id, w_id);
		int c_code = fake_c.hashCode();

		ReadLock read1 = new ReadLock(this.transaction_id, this.db, this.lockmanager, c_code, fake_c, Types.CUSTOMER,
				this.ts);
		this.store_lock(c_code, true);
		status = read1.applyLock();

		if (status == Status.ABORT) {
			System.out.println("Transaction " + this.transaction_id + " is aborting (139).");
			this.sch.retry(this);
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			return false;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit (139).");
			TimeUnit.MILLISECONDS.sleep(200);
			status = read1.applyLock();
		}

		Customer c = (Customer) read1.apply();
		if (c == null) {
			System.out.println("ERROR : customer does not exist (transaction " + this.transaction_id + ")");
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			return false;
		}

		String c_last = c.get_c_last();
		double c_discount = c.get_c_discount();
		String c_credit = c.get_c_credit();
		cnt++;

		/* ---- ORDER & NEWORDER : Creation (before WRITE) ---- */

		NewOrder nwd = new NewOrder(d_next_o_id, d_id, w_id);
		Order ord = new Order(d_next_o_id, c_id, d_id, w_id);
		ord.set_o_ol_cnt(data.get_number_items());
		for (int i = 1; i < data.get_ol_suppliers().size(); i++) {
			if ((data.get_ol_suppliers()).get(i) != (data.get_ol_suppliers()).get(0)) {
				ord.set_o_all_local(0);
				break;
			}
		}

		/* ---- ORDER : WRITE ---- */

		int old = ord.hashCode();

		WriteLock write0 = new WriteLock(this.transaction_id, this.db, this.lockmanager, ord, Types.ORDER, this.ts);
		this.store_lock(ord.hashCode(), false);
		status = write0.applyLock();

		if (status == Status.ABORT) {

			System.out.println(this.lockmanager);
			int neww = ord.hashCode();
			assert (old == neww);
			this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
			System.out.println(this.lockmanager);
			this.sch.retry(this);
			System.out.println("Transaction " + this.transaction_id + " is aborting (193).");
			return false;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit (193).");
			TimeUnit.MILLISECONDS.sleep(200);
			status = write0.applyLock();
		}

		write0.apply();
		cnt++;

		/* ---- NEWORDER : WRITE ---- */

		WriteLock write1 = new WriteLock(this.transaction_id, this.db, this.lockmanager, nwd, Types.NEWORDER, this.ts);
		this.store_lock(nwd.hashCode(), false);
		status = write1.applyLock();

		if (status == Status.ABORT) {
			this.sch.retry(this);
			// this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
			// this.isReads) ;
			System.out.println("Transaction " + this.transaction_id + " is aborting (215).");
			return false;
		} else if (status == Status.WAIT) {
			System.out.println("Transaction " + this.transaction_id + " waits a bit (215).");
			TimeUnit.MILLISECONDS.sleep(200);
			status = write1.applyLock();
		}

		write1.apply();
		cnt++;

		/* ---- NOW WE START THE ORDER : go through our shopping list ---- */

		Vector<Integer> ol_identifiers = data.get_ol_identifiers();
		Vector<Integer> ol_suppliers = data.get_ol_suppliers();
		Vector<Double> quantity = data.get_ol_quantities();
		int number_items = data.get_number_items();

		for (int i = 0; i < number_items - 1; i++) {

			/* ---- ITEM : UPDATE (read part) ---- */

			int item_id = ol_identifiers.get(i);
			int supplier_id = ol_suppliers.get(i);
			if (ol_identifiers.get(i) == null) {
				System.out.println("Value not found");
				return false;
			}

			Item fake_i = new Item(item_id);
			int code_item = fake_i.hashCode();
			WriteLock update1 = new UpdateLock(this.transaction_id, this.db, this.lockmanager, fake_i, Types.ITEM,
					this.ts);
			this.store_lock(code_item, false);
			status = update1.applyLock();

			if (status == Status.ABORT) {
				this.sch.retry(this);
				this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
				System.out.println("Transaction " + this.transaction_id + " is aborting (255).");
				return false;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit (255).");
				TimeUnit.MILLISECONDS.sleep(200);
				status = update1.applyLock();
			}

			Item it = (Item) ((UpdateLock) update1).applyRetrieve();
			if (it == null) {
				System.out.println("ERROR : item n°" + i + " does not exist (transaction " + this.transaction_id + ")");
				this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
				return false;
			}

			double i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();

			/* ---- STOCK : UPDATE (read part) ---- */

			Stock fake_s = new Stock(item_id, supplier_id);
			int code_stock = fake_s.hashCode();
			WriteLock update2 = new UpdateLock(this.transaction_id, this.db, this.lockmanager, fake_s, Types.STOCK,
					this.ts);
			this.store_lock(code_stock, false);
			status = update2.applyLock();

			if (status == Status.ABORT) {
				this.sch.retry(this);
				System.out.println("Transaction " + this.transaction_id + " is aborting (286).");
				this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
				return false;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit (286).");
				TimeUnit.MILLISECONDS.sleep(200);
				status = update2.applyLock();
			}

			Stock s = (Stock) ((UpdateLock) update2).applyRetrieve();
			if (s == null) {
				System.out.println("ERROR : stock does not exist (transaction " + this.transaction_id + ")");
				this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
				return false;
			}

			String s_data = s.get_s_data();
			double s_quantity = s.get_s_quantity();
			String s_dis_id = s.get_s_id(d_id);
			cnt++;

			/* ---- STOCK : UPDATE (write part) ---- */

			// ((UpdateLock) update2).applyUpdate();
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

			/* ---- STOCK : UPDATE (write part) ---- */

			// ((UpdateLock) update1).applyUpdate();
			double ol_amount = quantity.get(i) * i_price;
			if (s_data.indexOf("ORIGINAL") >= 0 && i_data.indexOf("ORIGINAL") >= 0) {
				it.set_i_data("B");
			} else {
				it.set_i_data("G");
			}
			cnt++;

			/* ---- ORDER LINE : WRITE ---- */

			Order_Line ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);

			WriteLock write2 = new WriteLock(this.transaction_id, this.db, this.lockmanager, ol, Types.ORDER_LINE,
					this.ts);
			this.store_lock(ol.hashCode(), false);
			status = write2.applyLock();

			if (status == Status.ABORT) {
				this.sch.retry(this);
				this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash, this.isReads);
				System.out.println("Transaction " + this.transaction_id + " is aborting (352).");
				return false;
			} else if (status == Status.WAIT) {
				System.out.println("Transaction " + this.transaction_id + " waits a bit (352).");
				TimeUnit.MILLISECONDS.sleep(200);
				status = write2.applyLock();
			}

			write2.apply();
			cnt++;

			/* ---- WE UPDATE THE COST OF THE TRANSACTION ---- */
			total_amount += ol_amount * (c_discount - 1) * (1 + tax + d_tax);

			/* ---- WE RELEASE THE LOCK OF THIS LOOP ITERATION ---- */
			this.lockmanager.remove_locks(ol, false, this.transaction_id); // Lock on the OrderLine
			this.lockmanager.remove_locks(fake_i, false, this.transaction_id); // Lock on the Item
			this.lockmanager.remove_locks(fake_s, false, this.transaction_id); // Lock on the Stock
		}

		/* ---- WE RELEASE THE LAST LOCKS ---- */

		// System.out.println("The transaction " + transaction_id + " is now releasing
		// its last locks.");
		this.lockmanager.remove_locks(nwd, false, this.transaction_id); // Lock on the NewOrder
		this.lockmanager.remove_locks(ord, false, this.transaction_id); // Lock on the Order
		this.lockmanager.remove_locks(c, true, this.transaction_id); // Lock on the Client
		this.lockmanager.remove_locks(d, false, this.transaction_id); // Lock on the District
		this.lockmanager.remove_locks(w, true, this.transaction_id); // Lock on the Warehouse

		// System.out.println("The transaction " + transaction_id + " has been completed
		// " + "(" + cnt + " operations, "
		// + total_amount + "€).");
		// TimeUnit.MILLISECONDS.wait(50);

		return true;

	}

	public void refresh_ts() {
		this.ts = LocalTime.now();
	}

	private void store_lock(int obj_hash, boolean isRead) {
		this.objs_hash.add(obj_hash);
		this.isReads.add(isRead);
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

/*
 * 
 * 
 * 
 * int cnt = 0; double total_amount = 0;
 * 
 * // We retrieve the warehouse, and then read the value we need. Warehouse
 * fake_wh = new Warehouse(this.w_id) ;
 * 
 * ReadLock read1 = new ReadLock(this.transaction_id, this.db, this.lockmanager,
 * fake_wh.hashCode(), fake_wh, Types.WAREHOUSE, this.ts);
 * this.store_lock(fake_wh.hashCode(), true) ; Status status = read1.applyLock()
 * ; if (status == Status.ABORT) { this.sch.retry(this) ;
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; System.out.println("Transaction " + this.transaction_id +
 * " is aborting 57."); return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = read1.applyLock() ; }
 * 
 * Warehouse w = (Warehouse) read1.apply(); if (w == null) {
 * System.out.println("ERROR : warehouse does not exist (transaction " +
 * this.transaction_id + ")" );
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } double tax = w.get_w_tax(); // we get the
 * tax cnt++; //this.lockmanager.remove_locks(w, true, this.transaction_id) ; //
 * Lock on the Warehouse4
 * 
 * // We retrieve the district, and read the useful values. int d_id =
 * data.get_d_id(); // we get the district number District fake_d = new
 * District(d_id, w_id) ; int d_code = fake_d.hashCode() ;
 * 
 * ReadLock read2 = new ReadLock(this.transaction_id, this.db, this.lockmanager,
 * d_code, fake_d, Types.DISTRICT, this.ts); this.store_lock(d_code, true) ;
 * status = read2.applyLock() ; if (status == Status.ABORT) {
 * this.sch.retry(this) ; this.lockmanager.remove_all_locks(this.transaction_id,
 * this.objs_hash, this.isReads) ; System.out.println("Transaction " +
 * this.transaction_id + " is aborting 84."); return false ; } else if (status
 * == Status.WAIT) { System.out.println("Transaction " + this.transaction_id +
 * " waits a bit 84"); TimeUnit.MILLISECONDS.sleep(200); status =
 * read2.applyLock() ; }
 * 
 * District d = (District) read2.apply(); if (d == null) {
 * System.out.println("ERROR : district does not exist (transaction " +
 * this.transaction_id + ")" );
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } double d_tax = d.get_d_tax(); int
 * d_next_o_id = d.get_d_next_o_id(); //this.lockmanager.remove_locks(d, true,
 * this.transaction_id) ; // Lock on the Order
 * 
 * WriteLock write0 = new WriteLock(this.transaction_id, this.db,
 * this.lockmanager, d, Types.DISTRICT, this.ts); this.store_lock(d.hashCode(),
 * false) ; status = write0.applyLock() ; if (status == Status.ABORT) {
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; this.sch.retry(this) ; System.out.println("Transaction " +
 * this.transaction_id + " is aborting 117."); return false ; } else if (status
 * == Status.WAIT) { System.out.println("Transaction " + this.transaction_id +
 * " waits a bit 117"); TimeUnit.MILLISECONDS.sleep(200); status =
 * write0.applyLock() ; } write0.apply(); cnt++; d.inc_next_o_id() ; cnt++;
 * //this.lockmanager.remove_locks(d, false, this.transaction_id) ; // Lock on
 * the Order
 * 
 * // We retrieve the client, and read the useful values. int c_id =
 * data.get_c_id(); Customer fake_c = new Customer(c_id, d_id, w_id) ; int
 * c_code = fake_c.hashCode();
 * 
 * ReadLock read3 = new ReadLock(this.transaction_id, this.db, this.lockmanager,
 * c_code, fake_c, Types.CUSTOMER, this.ts); this.store_lock(c_code, true) ;
 * status = read3.applyLock() ; if (status == Status.ABORT) {
 * System.out.println("Transaction " + this.transaction_id +
 * " is aborting 112."); this.sch.retry(this) ;
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = read3.applyLock() ; }
 * 
 * Customer c = (Customer) read3.apply(); if (c == null) {
 * System.out.println("ERROR : customer does not exist (transaction " +
 * this.transaction_id + ")" );
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } String c_last = c.get_c_last(); double
 * c_discount = c.get_c_discount(); String c_credit = c.get_c_credit(); cnt++;
 * 
 * // We place a NewOrder and Order NewOrder nwd = new NewOrder(d_next_o_id,
 * d_id, w_id); Order ord = new Order(d_next_o_id, c_id, d_id, w_id);
 * ord.set_o_ol_cnt(data.get_number_items()); for (int i = 1; i <
 * data.get_ol_suppliers().size(); i++) { if ((data.get_ol_suppliers()).get(i)
 * != (data.get_ol_suppliers()).get(0)) { ord.set_o_all_local(0); break; } }
 * 
 * int old = ord.hashCode() ;
 * 
 * WriteLock write1 = new WriteLock(this.transaction_id, this.db,
 * this.lockmanager, ord, Types.ORDER, this.ts); this.store_lock(ord.hashCode(),
 * false) ; status = write1.applyLock() ; if (status == Status.ABORT) {
 * 
 * System.out.println(this.lockmanager); int neww = ord.hashCode() ; assert(old
 * == neww); this.lockmanager.remove_all_locks(this.transaction_id,
 * this.objs_hash, this.isReads) ; System.out.println(this.lockmanager);
 * this.sch.retry(this) ; System.out.println("Transaction " +
 * this.transaction_id + " is aborting 151."); return false ; } else if (status
 * == Status.WAIT) { System.out.println("Transaction " + this.transaction_id +
 * " waits a bit"); TimeUnit.MILLISECONDS.sleep(200); status =
 * write1.applyLock() ; } write1.apply(); cnt++;
 * 
 * 
 * WriteLock write2 = new WriteLock(this.transaction_id, this.db,
 * this.lockmanager, nwd, Types.NEWORDER, this.ts);
 * this.store_lock(nwd.hashCode(), false) ; status = write2.applyLock() ; if
 * (status == Status.ABORT) { this.sch.retry(this) ;
 * //this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; System.out.println("Transaction " + this.transaction_id +
 * " is aborting 171."); return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = write2.applyLock() ; }
 * write2.apply(); cnt++;
 * 
 * // ??? Vector<Integer> ol_identifiers = data.get_ol_identifiers();
 * Vector<Integer> ol_suppliers = data.get_ol_suppliers(); Vector<Double>
 * quantity = data.get_ol_quantities(); int number_items =
 * data.get_number_items();
 * 
 * 
 * 
 * 
 * for (int i = 0; i < number_items-1; i++) { int item_id =
 * ol_identifiers.get(i); int supplier_id = ol_suppliers.get(i); if
 * (ol_identifiers.get(i) == null) { System.out.println("Value not found");
 * return false; } // We read the item Item fake_i = new Item(item_id) ; int
 * code_item = fake_i.hashCode(); ReadLock read4 = new
 * ReadLock(this.transaction_id, this.db, this.lockmanager, code_item, fake_i,
 * Types.ITEM, this.ts); this.store_lock(code_item, true) ; status =
 * read4.applyLock() ; if (status == Status.ABORT) { this.sch.retry(this) ;
 * //this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; System.out.println("Transaction " + this.transaction_id +
 * " is aborting 210."); return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = read4.applyLock() ; } Item it =
 * (Item) read4.apply(); if (it == null) { System.out.println("ERROR : item n°"
 * + i + " does not exist (transaction " + this.transaction_id + ")" );
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } double i_price = it.get_price(); String
 * i_name = it.get_i_name(); String i_data = it.get_i_data();
 * //this.lockmanager.remove_locks(fake_i, true, this.transaction_id) ; // Lock
 * on the Item
 * 
 * // We read the stock Stock fake_s = new Stock(item_id, supplier_id); int
 * code_stock = fake_s.hashCode(); ReadLock read5 = new
 * ReadLock(this.transaction_id, this.db, this.lockmanager, code_stock, fake_s,
 * Types.STOCK, this.ts); int gogogo = code_stock ; this.store_lock(code_stock,
 * true) ; status = read5.applyLock() ; if (status == Status.ABORT) {
 * this.sch.retry(this) ; System.out.println("Transaction " +
 * this.transaction_id + " is aborting 240.");
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = read5.applyLock() ; } Stock s =
 * (Stock) read5.apply(); if (s == null) {
 * System.out.println("ERROR : stock does not exist (transaction " +
 * this.transaction_id + ")" );
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; return false ; }
 * 
 * String s_data = s.get_s_data(); double s_quantity = s.get_s_quantity();
 * String s_dis_id = s.get_s_id(d_id);
 * 
 * //this.lockmanager.remove_locks(fake_s, true, this.transaction_id) ; // Lock
 * on the Stock
 * 
 * // We update the stock -> to make things easier, we directly change the //
 * attribute we read. // Instead of writing an updated copy of the stock //
 * We'll need to put X locks in the lock-based transaction WriteLock write3 =
 * new WriteLock(this.transaction_id, this.db, this.lockmanager, fake_s,
 * Types.STOCK, this.ts); this.store_lock(fake_s.hashCode(), false) ; status =
 * write3.applyLock() ; if (status == Status.ABORT) { this.sch.retry(this) ;
 * System.out.println("Transaction " + this.transaction_id +
 * " is aborting 266."); this.lockmanager.remove_all_locks(this.transaction_id,
 * this.objs_hash, this.isReads) ; return false ; } else if (status ==
 * Status.WAIT) { System.out.println("Transaction " + this.transaction_id +
 * " waits a bit"); TimeUnit.MILLISECONDS.sleep(200); status =
 * write3.applyLock() ; } if (s_quantity > quantity.get(i) + 10) { s_quantity -=
 * quantity.get(i); } else { s_quantity = s_quantity - quantity.get(i) + 91; }
 * s.change_s_ytd(quantity.get(i)); s.change_s_order_cnt(1); if
 * (ol_suppliers.get(i) != w_id) { s.change_s_remote_cnt(1); }
 * //this.lockmanager.remove_locks(fake_s, false, this.transaction_id) ; // Lock
 * on the Stock cnt++;
 * 
 * // Here we update the item -> need XLock WriteLock write4 = new
 * WriteLock(this.transaction_id, this.db, this.lockmanager, fake_i, Types.ITEM,
 * this.ts); this.store_lock(fake_i.hashCode(), false) ; status =
 * write4.applyLock() ; if (status == Status.ABORT) { this.sch.retry(this) ;
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; System.out.println("Transaction " + this.transaction_id +
 * " is aborting 307."); return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = write4.applyLock() ; } double
 * ol_amount = quantity.get(i) * i_price; if (s_data.indexOf("ORIGINAL") >= 0 &&
 * i_data.indexOf("ORIGINAL") >= 0) { it.set_i_data("B"); } else {
 * it.set_i_data("G"); } //this.lockmanager.remove_locks(fake_i, false,
 * this.transaction_id) ; // Lock on the Item cnt++;
 * 
 * // We add the OL to the DB Order_Line ol = new Order_Line(item_id, d_id,
 * w_id, i + 1); ol.set_ol_amount(ol_amount); ol.set_ol_dist_info(s_dis_id);
 * 
 * WriteLock write5 = new WriteLock(this.transaction_id, this.db,
 * this.lockmanager, ol, Types.ORDER_LINE, this.ts);
 * this.store_lock(ol.hashCode(), false) ; status = write5.applyLock() ; if
 * (status == Status.ABORT) { this.sch.retry(this) ;
 * this.lockmanager.remove_all_locks(this.transaction_id, this.objs_hash,
 * this.isReads) ; System.out.println("Transaction " + this.transaction_id +
 * " is aborting 341."); return false ; } else if (status == Status.WAIT) {
 * System.out.println("Transaction " + this.transaction_id + " waits a bit");
 * TimeUnit.MILLISECONDS.sleep(200); status = write5.applyLock() ; }
 * write5.apply(); cnt++;
 * 
 * total_amount += ol_amount * (c_discount -1) * (1 + tax + d_tax);
 * 
 * // We release the locks of this loop (one by one, because could loop again)
 * this.lockmanager.remove_locks(ol, false, this.transaction_id) ; // Lock on
 * the OrderLine this.lockmanager.remove_locks(fake_i, false,
 * this.transaction_id) ; // Lock on the Item
 * this.lockmanager.remove_locks(fake_s, false, this.transaction_id) ; // Lock
 * on the Stock this.lockmanager.remove_locks(fake_s, true, this.transaction_id)
 * ; // Lock on the Stock this.lockmanager.remove_locks(fake_i, true,
 * this.transaction_id) ; // Lock on the Item }
 * //System.out.println("The transaction " + transaction_id +
 * " is now releasing its last locks."); this.lockmanager.remove_locks(nwd,
 * false, this.transaction_id) ; // Lock on the NewOrder
 * this.lockmanager.remove_locks(ord, false, this.transaction_id) ; // Lock on
 * the Order this.lockmanager.remove_locks(c, true, this.transaction_id) ; //
 * Lock on the client this.lockmanager.remove_locks(d, false,
 * this.transaction_id) ; // Lock on the Order this.lockmanager.remove_locks(d,
 * true, this.transaction_id) ; // Lock on the District
 * this.lockmanager.remove_locks(w, true, this.transaction_id) ; // Lock on the
 * Warehouse
 * 
 * 
 * //System.out.println("The transaction " + transaction_id +
 * " has been completed " + "(" + cnt + " operations, " // + total_amount +
 * "€)."); TimeUnit.MILLISECONDS.wait(50);
 * 
 * return true ;
 * 
 * 
 */