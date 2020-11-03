package kungRobinson;

import java.util.ArrayList;
import java.util.List;

import database.*;
import map670e.DataGeneration; 

public class Transaction_2 {
	// Constants : phase names
	public static final String phase_READ = "READ";
	public static final String phase_VALIDATE = "VALIDATE";
	public static final String phase_WRITE = "WRITE";
	public static final String phase_FINISH = "FINISH";
	
	
	// Attributes
	private int id;
	private String phase;
	private float ts_start_read;
	private float ts_start_validate;
	private float ts_start_write;
	private float ts_start_finish;
	private ArrayList<Object> all_targets; // Pointers towards all the objects we want to edit
	private ArrayList<Object> all_targets_copy; // Local copies of the targets
	private ArrayList<Types>  all_targets_types;
	private ArrayList<Object> read_set;
	private ArrayList<Object> write_set;
	private int w_id;
	private Database db;
	private DataGeneration query; 
	private NewOrder nwd;
	private Order ord;
	private Order_Line ol;

	public Transaction_2(final int id, int w_id, Database db) {
		this.id = id;
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_start_validate = 0;
		this.ts_start_write = 0;
		this.ts_start_finish = 0;
		this.all_targets=new ArrayList<Object>();
		this.all_targets_copy= new ArrayList<Object>();
		this.all_targets_types= new ArrayList<Types>();
	
		
		
		//Fixed Targets.
		this.w_id = w_id;
		this.query = new DataGeneration(w_id);
		this.db = db;
		
	}

	public boolean is_finished() {
		return this.ts_start_finish > 0;
	}

	public float get_ts_start_read() {
		return this.ts_start_read;
	}

	public float get_ts_start_validate() {
		return this.ts_start_validate;
	}

	public float get_ts_start_write() {
		return this.ts_start_write;
	}

	public float get_ts_start_finish() {
		return this.ts_start_finish;
	}

	public ArrayList<Object> get_write_set(){
		return this.write_set;
	}

	public ArrayList<Object> get_read_set(){
		return this.read_set;
	}

	public ArrayList<Object> list_intersect(ArrayList<Object> list1, ArrayList<Object> list2){
		// return the common elements of list1 and list2
		ArrayList<Object> intersect = new ArrayList<Object>();
		for (int i=0; i<list1.size(); i++){
			// If this list element is in the other list
			if (list2.contains(list1.get(i))){
				intersect.add(list1.get(i));
			}
		}
		return intersect;
	}


	private boolean read(Database db) {
		// READ phase
		int cnt = 0;
		double total_amount = 0;
		
		Warehouse fake_w = new Warehouse(this.w_id);
		Warehouse w = (Warehouse) db.getObject(fake_w.hashCode(), Types.WAREHOUSE);
		if (w == null) {
			System.out.println("ERROR : warehouse does not exist (transaction " + this.transaction_id + ")" );
			return false ;
		}
		double tax = w.get_w_tax(); // we get the tax
		this.read_set.add(w);
		cnt++;

		int d_id = this.query.get_d_id();
		District fake_d = new District(d_id, w_id) ;
		int d_code = fake_d.hashCode();
		
		District d = (District) db.getObject(fake_d.hashCode(), Types.DISTRICT);
		if (d == null) {
			System.out.println("ERROR : district does not exist (transaction " + this.transaction_id + ")" );
			return false ;
		}
		double d_tax = d.get_d_tax();
		int d_next_o_id = d.get_d_next_o_id() + 1;
		this.read_set.add(d);
		cnt++;
		
		// We retrieve the client, and read the useful values.
		int c_id = this.query.get_c_id();
		Customer fake_c = new Customer(c_id, d_id, w_id) ;
		
		Customer c = (Customer) db.getObject(fake_c.hashCode(), Types.CUSTOMER);
		if (c == null) {
			System.out.println("ERROR : customer does not exist (transaction " + this.transaction_id + ")" );
			return false ;
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
		//this.has_applied = this.db.setObject(this.target, this.target_type);
		//cnt++ 2 times one for nwd and one for ord
		
		ArrayList<Integer> ol_identifiers = this.query.get_ol_identifiers();
		ArrayList<Integer> ol_suppliers = this.query.get_ol_suppliers();
		ArrayList<Double> quantity = this.query.get_ol_quantities();
		int number_items = this.query.get_number_items();
		
		for (int i = 0; i < number_items; i++) {
			int item_id = ol_identifiers.get(i);
			int supplier_id = ol_suppliers.get(i);
			if (ol_identifiers.get(i) == null) {
				System.out.println("Value not found");
				return false;
			}
			// We read the item
			Item fake_i = new Item(item_id,supplier_id) ;
			int code_item = fake_i.hashCode();
			
			Item it = new Item( (Item) db.getObject(code_item, Types.ITEM));
			this.all_targets.add((Item) db.getObject(code_item, Types.ITEM));
			this.all_targets_copy.add(it);
			this.all_targets_types.add(Types.ITEM);
			this.read_set.add((Item) db.getObject(code_item, Types.ITEM));
			this.write_set.add((Item) db.getObject(code_item, Types.ITEM));
			
			if (it == null) {
				System.out.println("ERROR : item n°" + i + " does not exist (transaction " + this.transaction_id + ")" );
				return false ;
			}
			double i_price = it.get_price();
			String i_name = it.get_i_name();
			String i_data = it.get_i_data();
			cnt++;
			// We read the stock
			Stock fake_s = new Stock(item_id, supplier_id);
			int code_stock = fake_s.hashCode();
			
			Stock s = new Stock( (Stock) db.getObject(code_stock, Types.STOCK));
			this.all_targets.add((Stock) db.getObject(code_stock, Types.STOCK));
			this.all_targets_copy.add(s);
			this.all_targets_types.add(Types.STOCK);
			this.read_set.add((Stock) db.getObject(code_stock, Types.STOCK));
			this.write_set.add((Stock) db.getObject(code_stock, Types.STOCK));
			
			if (s == null) {
				System.out.println("ERROR : stock does not exist (transaction " + this.transaction_id + ")" );
				return false ;
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
			//cnt++;
			
			this.ol = new Order_Line(item_id, d_id, w_id, i + 1);
			ol.set_ol_amount(ol_amount);
			ol.set_ol_dist_info(s_dis_id);

			//we must commit ol to the database
			total_amount += ol_amount * (c_discount -1) * (1 + tax + d_tax);

			
		}
		System.out.println("The transaction " + this.id + " has been completed " + "(" + cnt + " operations, "
				+ total_amount + ").");
		
		return true;
		
		
	}

	private boolean validate(ArrayList<Transaction_2> other_transactions) {
		for (int i = 0; i < other_transactions.size(); i++) {
			Transaction_2 transaction = other_transactions.get(i);
			boolean test_1 = false;
			boolean test_2 = false;
			boolean test_3 = false;
			// Test 1
			if (transaction.is_finished()) {
				test_1 = transaction.get_ts_start_finish() < this.ts_start_finish;
			}

			// Test 3
			if (transaction.get_ts_start_validate() > 0){
				test_2 = transaction.get_ts_start_validate() < this.ts_start_validate && this.list_intersect(transaction.get_write_set(), this.read_set).size() == 0 && this.list_intersect(transaction.get_write_set(), this.write_set).size() == 0;
			}

			// Test 2
			if (transaction.is_finished()){
				// In the slides, it said to check ts_start_write. 
				// We, however, never have a ts_start_write since we are in the validation phase.
				// Consequently, we replace ts_start_write by the current timestamp.
				// That's why we put this test last. 
				test_2 = transaction.get_ts_start_finish() < System.currentTimeMillis() && this.list_intersect(transaction.get_write_set(), this.read_set).size() == 0;
			}

			// Did any test pass ?
			Boolean any_test_passed = test_1 || test_2 || test_3;
			if (!any_test_passed) {
				// Every test failed. There is a conflict. Validation failed. Return False.
				return false;
			}
		}
		// We succeeded in every test. Return true. 
		return true;
	}

	private boolean write() {
		for (int i = 0; i < this.all_targets.size(); i++) {
			
			//We all need to check item and stock because this transaction only write items and stocks
			switch (this.all_targets_types.get(i)) {
			
			case ITEM:
				((Item) this.all_targets.get(i)).Update((Item) this.all_targets_copy.get(i));

			case STOCK:
				((Stock) this.all_targets.get(i)).Update((Stock) this.all_targets_copy.get(i));
				
			default:
				return false;
			}
				
		}
		
		this.db.setObject(this.nwd, Types.NEWORDER);
		this.db.setObject(this.ord, Types.ORDER);
		this.db.setObject(this.ol, Types.ORDER_LINE);
		return true;
	}

	public boolean apply_next(ArrayList<Transaction> other_transactions) {
		if (this.is_finished()) {
			return true;
		}
		Boolean success = false;
		if (this.phase == phase_READ) {
			System.out.println("Reading " + this.id);
			success = this.read();
			if (success) {
				this.phase = phase_VALIDATE;
				this.ts_start_validate = System.currentTimeMillis();
			}

		} else if (this.phase == phase_VALIDATE) {
			System.out.println("Validating " + this.id);
			success = this.validate(other_transactions);
			if (success) {
				this.phase = phase_WRITE;
				this.ts_start_write = System.currentTimeMillis();
			}
			else {
				// Validation failed : restart the transaction. 
				System.out.println("Validation failed for " + this.id);
				this.restart();
			}
		} else if (this.phase == phase_WRITE) {
			System.out.println("Writing " + this.id);
			success = this.write();
			if (success) {
				this.phase = phase_FINISH;
				this.ts_start_finish = System.currentTimeMillis();
			}

		} else {
			System.out.println("Starting " + this.id);
			this.phase = phase_READ;
			this.ts_start_read = System.currentTimeMillis();
			return true;
		}
		return success;
	}

	public void restart() {
		// Restart the operation
		System.out.println("Restarting " + this.id);
		this.operation_iter = 0;
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_start_validate = 0;
		this.ts_start_write = 0;
		this.ts_start_finish = 0;
	}
}
