package map670e;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
	// Constants : phase names
	public static final String phase_READ = "READ";
	public static final String phase_VALIDATE = "VALIDATE";
	public static final String phase_WRITE = "WRITE";
	public static final String phase_FINISH = "FINISH";

	// Attributes
	private int id;
	private List<Operation> operation_list;
	private int operation_iter;
	private String phase;
	private float ts_start_read;
	private float ts_start_validate;
	private float ts_start_write;
	private float ts_start_finish;
	private ArrayList<Target> all_targets; // Pointers towards all the objects we want to edit
	private ArrayList<Target> all_targets_copy; // Local copies of the targets
	private ArrayList<Target> read_set;
	private ArrayList<Target> write_set;

	public Transaction(final int id, ArrayList<Operation> operation_list) {
		this.id = id;
		this.operation_list = operation_list;
		this.operation_iter = 0;
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_start_validate = 0;
		this.ts_start_write = 0;
		this.ts_start_finish = 0;
		// Init the sets of targets
		this.init_all_targets();
		this.init_read_set();
		this.init_write_set();
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

	public ArrayList<Target> get_write_set(){
		return this.write_set;
	}

	public ArrayList<Target> get_read_set(){
		return this.read_set;
	}

	public ArrayList<Target> list_intersect(list1, list2){
		// return the common elements of list1 and list2
		ArrayList<Target> intersect = new ArrayList<Target>();
		for (int i=0; i<list1.size(); i++){
			// If this list element is in the other list
			if list2.contains(list1.get(i)){
				intersect.add(list1.get(i))
			}
		}
		return intersect
	}

	private void init_all_targets() {
		// Fill the all_targets array by looking at each operation targets
		this.all_targets = new ArrayList<Target>(); // reset
		for (int i = 0; i < this.operation_list.size(); i++) {
			// If this operation's target not yet in all_targets
			operation = this.operation_list.get(i);
			if (!this.all_targets.contains(operation.get_target())) {
				// Extend all_targets
				this.all_targets.add(operation.get_target());
			}
		}
	}

	private void init_read_set() {
		// List of the targets of all read operations
		this.read_set = new ArrayList<Target>(); // reset
		for (int i = 0; i < this.operation_list.size(); i++) {
			operation = this.operation_list.get(i);
			if (operation.get_operation_type() == Operation.READ) {
				if (!this.read_set.contains(operation.get_target()))
					// Extend all_targets
					this.read_set.add(operation.get_target());
			}
		}

	}

	private void init_write_set() {
		// List of the targets of all WRITE operations
		this.read_set = new ArrayList<Target>(); // reset
		for (int i = 0; i < this.operation_list.size(); i++) {
			operation = this.operation_list.get(i);
			if (operation.get_operation_type() == Operation.WRITE) {
				if (!this.read_set.contains(operation.get_target()))
					// Extend all_targets
					this.read_set.add(operation.get_target());
			}
		}

	}

	private void init_all_targets_local() {
		// Create copy of all targets
		this.all_local_targets = new ArrayList<Target>(); // reset
		for (int i = 0; i < this.all_targets.size(); i++) {
			// I don't know how to do this operation !
			// this.all_targets_copy.add( COPY_OF(this.all_target.get(i)) )
		}
		// Add pointers to these copies in each Operation
		for (int i = 0; i < this.operation_list.size(); i++) {
			operation = this.operation_list.get(i);
			target_id = this.all_targets.indexOf(operation.get_target());
			operation.set_local_target(this.all_targets_copy.get(target_id));
		}
	}

	private boolean read() {
		// READ phase
		// Reset local copies to be fresh copy of the disk targets
		this.init_all_targets_local();

		success = this.operation_list.get(this.operation_iter).apply();
		if (success) {
			this.operation_iter++;
		}
		// If we read all the operations, READ phase is finished: return true
		if (this.operation_iter >= this.operation_list.size()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validate(ArrayList<Transaction> other_transactions) {
		for (int i = 0; i < other_transactions.size(); i++) {
			transaction = other_transactions.get(i);
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
			any_test_passed = test_1 || test_2 || test_3;
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
			target = this.all_targets.get(i);
			target.update(this.all_targets_copy.get(i));
		}
		return true;
	}

	public boolean apply_next(ArrayList<Transaction> other_transactions) {
		if (this.is_finished()) {
			return true;
		}
		bool success = false;
		if (this.phase == phase_READ) {
			System.out.println("Reading " + this.id);
			success = this.read();
			if (success) {
				this.phase = phase_VALIDATE;
				this.ts_start_validate = System.currentTimeMillis();
			}

		} else if (this.phase == phase_VALIDATE) {
			System.out.println("Validating " + this.id);
			success = this.validate();
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
