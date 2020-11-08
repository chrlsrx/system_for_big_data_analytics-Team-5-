package map670e;

import java.time.LocalTime;

import database.Database;
import database.Types;


public class WriteLock extends Write {
	
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
		
		return Status.ACCEPTED ;
	}
}
