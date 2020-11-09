package map670e;

import java.time.LocalTime;

import database.Database;
import database.Types;

public class UpdateLock extends WriteLock {
	
	public UpdateLock(int id, Database db, LockManager lockm, Object fake_target, Types target_type, LocalTime time) {
		
		super(id, db, lockm, fake_target, target_type, time) ;

	}
	
	public synchronized Object applyRetrieve() {
		Object true_target = this.db.getObject(this.target.hashCode(), this.target_type) ;
		if (true_target != null) {
			this.has_applied = true ;
		}
		return true_target ;
	}
	
	public synchronized boolean applyUpdate() {
		this.has_applied = this.db.setObject(this.target, this.target_type) ;
		return this.has_applied ;
	}
	
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
		
		// Ask a write_lock since status == ACCEPTED
		this.lockmanager.add_lock(this.target, false, this.transaction_id, this.time) ;
		
		return Status.ACCEPTED ;
	}
}
