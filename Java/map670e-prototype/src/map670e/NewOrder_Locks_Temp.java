package map670e;

import database.* ;

/* This class is just temporary (used when building Lock and LockManager)
 * It can still give us a few ideas of how we could build our classes (according to TPC C
 * & the first Python prototype).
 * 
 * 
 * 1. We could have a class NewOrder which implements Transaction
 * 2. Then we would have 3 classes implementing NewOrder (one for each algorithm)
 */


public class NewOrder_Locks_Temp {
	
	private final int id ;
	private LockManager lockmanager ;
	private Database db ;
	
	public NewOrder_Locks_Temp(LockManager lockmanager, Database db, int id) {
		this.id = id ;
		this.lockmanager = lockmanager ;
		this.db = db ;
	}
	
	/* 
	 * This whole method could become an operation
	 * 		-> if "readCustomer" = new class which extends Operation, then also need 3 versions
	 * 		   of it (for each algo) => many classes, but maybe useful, and keep existing structure
	 * 
	 * Or it could be a method of Operation_Locked.
	 * 
	 * Then in NewOrder_Locks, we would just need (following the python prototype) :
	 * 		-> the super() constructor + attribute Lock manager
	 * 		-> a method to add the operations to the operation list/buffer
	 * 		-> a method which executes an operation at each step
	 * 		-> Later (when multi-threading), we'll probably add a Thread attribute in transaction
	 * 
	 * 
	 * 
	 */
	public boolean readCustomer(int c_id, int c_d_id, int c_w_id) {
		
		Customer gosht_customer = new Customer(c_id, c_d_id, c_w_id) ;
		
		// Check wether the customer exists or not.
		int gosht_hash = gosht_customer.hashCode() ;
		if (!this.db.searchCustomer(gosht_hash)) {
			// Abort
			System.out.println("The customer " + gosht_customer.toString() + "was not found");
			return false ;
		}
		
		// Check if X locked (it's a read, so no need to check if S locked)
		if (this.lockmanager.isXLocked(gosht_customer)) {
			// Wait and retry
			return false ;
		}
		
		// Ask a read_lock
		boolean accepted = this.lockmanager.add_lock(gosht_customer, true, this.hashCode()) ;
		
		// We have the lock, can read
		Customer true_customer = this.db.getCustomer(gosht_hash) ;
		
		return true ;
	}
	
	
}
