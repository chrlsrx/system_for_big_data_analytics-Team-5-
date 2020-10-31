package map670e;

import java.time.LocalTime;

import database.*;

public class ReadLock extends Read {

	private LockManager lockmanager;
	private LocalTime time;
	private Object target ; // Required to check if XLocked

	public ReadLock(int id, Database db, LockManager lockm, int target_hash, Object o, Types target_type, LocalTime time) {

		super(id, db, target_hash, target_type);
		this.lockmanager = lockm;
		this.time = time;
		this.target = o ;

	}

	// This isn't clean, because there is also an apply function too
	public Status applyLock() {
			

		// Check if X locked & deadlock prevention
		Status status = this.lockmanager.isXLocked(this.target, this.time) ;
		if (status == Status.WAIT) {
			return Status.WAIT ;	// Wait and retry
		} else if (status == Status.ABORT) {
			return Status.ABORT ;	// Abort
		}
			
			
		// Ask a read_lock since status == ACCEPTED
		boolean accepted = this.lockmanager.add_lock(this.target, true, this.hashCode(), this.time) ;
			
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
		return status ;
	}

}
