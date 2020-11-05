package map670e;

import java.time.LocalTime;

import database.Database;
import database.Types;


public class WriteLock extends Write {
	/*
	 * This type of operation allows us to update or add an object in the database.
	 * 
	 * The Object target is the new/updated object that we want to insert in the database
	 * So you need to build it before creating the write operation
	 * This may require the use of set methods of the corresponding class
	 * 
	 * When updating, the main issue with this solution is that we may have to read the object, 
	 * create a copy, modify the fields we want to change, and then put it in the database (by calling apply)
	 * 
	 * An easy solution when we want to update an object is to, in the transaction :
	 * 		-> create an operation read, instead of creating an operation write ;
	 * 		-> update the object that was read (it will also change it in the database) ;
	 * 		-> but make sure to Xlock it before, since it's a disguised write ;
	 */
	
	protected LockManager lockmanager ;
	protected LocalTime time ;
	
	public WriteLock(int id, Database db, LockManager lockm, Object target, Types target_type, LocalTime time) {
		
		super(id, db, target, target_type) ;
		this.lockmanager = lockm ;
		this.time = time ;
		
	}
	
	
	// This isn't clean, because there is also an apply function too
	public synchronized Status applyLock() {
		
		// Check if X locked & deadlock prevention
		Status status = this.lockmanager.isXLocked(this.target, this.transaction_id, this.time) ;
		if (status == Status.WAIT) {
			return Status.WAIT ;	// Wait and retry
		} else if (status == Status.ABORT) {
			return Status.ABORT ;	// Abort
		}
		
		// Check if S locked & deadlock prevention
		status = this.lockmanager.isSLocked(this.target, this.transaction_id, this.time) ;
		if (status == Status.WAIT) {
			return Status.WAIT ;	// Wait and retry
		} else if (status == Status.ABORT) {
			return Status.ABORT ;	// Abort
		}
		
		// Ask a read_lock since status == ACCEPTED
		this.lockmanager.add_lock(this.target, false, this.transaction_id, this.time) ;
		/* OLD VERSION, KEEP IN CASE */
		/*
		// We have the lock, can write.
		this.has_applied = this.db.setObject(target, target_type) ;
		
		// We want to make sure that lock has been added, and that we have read the value
		// Because if not, one of those tasks failed unexpectedly : ABORT
		if (this.has_applied && accepted) {
			status = Status.ABORT ;
		}
		*/
		return Status.ACCEPTED ;
	}
}
