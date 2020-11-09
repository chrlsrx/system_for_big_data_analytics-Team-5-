package map670e;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Common Lock class for X Locks and S Locks.
 * Primary key : (l_on_read, l_transaction_id, l_transaction_ts)
 */

public class Lock {
	
	private boolean l_on_read ; 			// Is it a shared lock ?
	private int l_transaction_id ; 			// Transaction which asked the creation of the lock.
	private LocalTime l_transaction_ts ; 	// Transaction time stamp, for deadlock prevention.
	private Object l_entity ;				// Object that is locked, could replace it with object type enumeration
	
	
	/**
	 * Constructor
	 * @param l_entity : object that will be locked (often a fake one, with the same key).
	 * @param l_on_read : is it a shared lock or an exclusive lock ?
	 * @param l_transaction_id : transaction id.
	 * @param ts : the time stamp of the transaction.
	 */
	public Lock(Object l_entity, boolean l_on_read, int l_transaction_id, LocalTime ts) {
		this.l_on_read = l_on_read ;
		this.l_transaction_id = l_transaction_id ;
		this.l_entity = l_entity ;
		this.l_transaction_ts = ts ;
	}
	
	/**
	 * Method called by LockManager when checking if a lock already exists on an entity.
	 * @return the object locked (if fake : has the same key).
	 */
	public Object getEntity() {
		return this.l_entity ;
	}

	/**
	 * Method called by LockManager when checking if a lock already exists on an entity.
	 * @return the transaction id.
	 */
	public int getTransaction() {
		return this.l_transaction_id ;
	}
	
	/**
	 * Method called by LockManager, used for deadlock prevention (compares 2 time stamps).
	 * @return boolean (true if current lock has a higher priority).
	 */
	public synchronized boolean hasHigherPrio(LocalTime time) {
		return (this.l_transaction_ts.compareTo(time) < 0) ; // < 0 If ts < time, so higher prio
	}
	
	@Override
	public String toString() {
		return "Lock [l_on_read=" + l_on_read + ", l_transaction_id=" + l_transaction_id + ", l_entity=" + l_entity
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(l_entity, l_on_read, l_transaction_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Lock)) {
			return false;
		}
		Lock other = (Lock) obj;
		return Objects.equals(l_entity, other.l_entity) && l_on_read == other.l_on_read
				&& l_transaction_id == other.l_transaction_id;
	}

	
}
