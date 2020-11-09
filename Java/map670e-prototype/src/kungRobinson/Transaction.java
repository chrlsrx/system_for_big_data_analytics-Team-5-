package kungRobinson;

import java.util.ArrayList;
import java.util.List;
import database.*;
import map670e.DataGeneration;
import java.util.Vector;

public class Transaction implements Runnable {
	// Constants : phase names
	public static final String phase_READ = "READ";
	public static final String phase_VALIDATE = "VALIDATE";
	public static final String phase_WRITE = "WRITE";
	public static final String phase_FINISH = "FINISH";

	// Attributes
	protected int id;
	protected String phase;
	protected double ts_start_read;
	protected double ts_finish_read;
	protected double ts_start_validate;
	protected double ts_finish_validate;
	protected double ts_start_write;
	protected double ts_finish_write;
	protected ArrayList<Object> read_set;
	protected ArrayList<Object> write_set;
	protected int w_id;
	protected Database db;
	protected DataGeneration query;
	protected int cnt;
	protected int nb_restarts;
	protected Scheduler scheduler;

	public Transaction(final int id, int w_id, Database db, Scheduler scheduler) {
		this.id = id;
		this.phase = "";
		this.ts_start_read = 0;
		this.ts_finish_read = 0;
		this.ts_start_validate = 0;
		this.ts_finish_validate = 0;
		this.ts_start_write = 0;
		this.ts_finish_write = 0;
		this.read_set = new ArrayList<Object>();
		this.write_set = new ArrayList<Object>();
		this.nb_restarts = 0;

		// Fixed Targets.
		this.w_id = w_id;
		this.query = new DataGeneration(w_id);
		this.db = db;
		this.scheduler = scheduler;

	}

	public boolean is_finished() {
		return this.ts_finish_write > 0;
	}

	public synchronized int get_id() {
		return this.id;
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

	public boolean empty_intersect(ArrayList<Object> list1, ArrayList<Object> list2) {
		// Return true if lis1 and list2 have an empty intersection
		for (int i = 0; i < list1.size(); i++) {
			if (list2.contains(list1.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean validate() {
		Vector<Transaction> other_transactions = this.scheduler.getTransactions();
		for (int i = 0; i < other_transactions.size(); i++) {
			Transaction transaction = other_transactions.get(i);
			// We only test this transaction for the other transactions that started BEFORE
			// current one
			if (transaction.get_ts_start_read() < this.ts_start_read && transaction.get_ts_start_read() > 0
					&& transaction.get_id() != this.id) {
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

	public String get_phase() {
		return this.phase;
	}

	public boolean read() {
		return true;
	}

	public boolean write() {
		return true;
	}

	public boolean apply_next() throws InterruptedException {
		if (this.is_finished()) {
			return true;
		}
		Boolean success = false;
		// We look at the current phase and set it to be the next one
		if (this.phase == "" && this.ts_start_read==0) {
			this.phase = phase_READ;
			this.ts_start_read = System.currentTimeMillis();
			System.out.println("Reading " + this.id);
			success = this.read();
			this.ts_finish_read = System.currentTimeMillis();
		} else if (this.phase == phase_READ && this.ts_start_validate==0) {
			this.phase = phase_VALIDATE;
			this.ts_start_validate = System.currentTimeMillis();
			System.out.println("Validating " + this.id);
			success = this.validate();
			if (!success) {
				// Validation failed : restart the transaction.
				System.out.println("Validation failed for " + this.id);
				this.restart();
			}
			this.ts_finish_validate = System.currentTimeMillis();

		} else if (this.phase == phase_VALIDATE && this.ts_start_write==0) {
			this.phase = phase_WRITE;
			this.ts_start_write = System.currentTimeMillis();
			System.out.println("Writing " + this.id);
			success = this.write();
			this.ts_finish_write = System.currentTimeMillis();

		}
		return success;
	}

	public  void run() {
		try {
			this.apply_next(); // read
			this.apply_next(); // validate
			this.apply_next(); // write
		} catch (InterruptedException e) {
			System.out.println("UNEXPECTED ERROR");
			e.printStackTrace();
		}

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
		this.read_set = new ArrayList<Object>();
		this.write_set = new ArrayList<Object>();
		this.nb_restarts++;

	}

	public int get_nb_restarts() {
		return this.nb_restarts;
	}
}
