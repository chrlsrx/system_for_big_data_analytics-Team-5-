package map670e;

import java.util.ArrayList;
import java.util.List;

/* The code bellow has to be adapted, and may not even be need since we'll ahve a newOrderTransaction*/

public class Transaction {
	private int id;
	private List<Operation> operation_list;
	private int operation_iter;
	private boolean finished;

	public Transaction(final int id) {
		this.id = id;
		this.operation_list = new ArrayList<Operation>();
		this.operation_iter = 0;
		this.finished = false;

	}

	public boolean apply_next() {

		if (this.finished) {
			return true; // Transaction has finished, skip
		}

		// Otherwise, run transaction
		Operation current_operation = (Operation) this.operation_list.get(this.operation_iter);
		boolean success = false;

		if (current_operation.get_has_lock()) {
			if (current_operation.get_has_applied()) {
				System.out.println(this.id + "Free lock of current operation" + current_operation.get_id());
				success = current_operation.free_lock();
			} else {
				System.out.println(this.id + "Apply current operation" + current_operation.get_id());
				current_operation.apply();
			}
		} else {
			System.out.println(this.id + "Get lock of current operation" + current_operation.get_id());
			current_operation.get_lock();
		}

		if (success) {
			this.operation_iter++;
			if (this.operation_iter == this.operation_list.size()) {
				this.finished = true;
			}
		}
		return success;
	}
}
