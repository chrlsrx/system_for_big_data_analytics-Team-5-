package map670e;

import java.util.HashMap;
import java.time.LocalTime;
import java.util.ArrayList;
import database.*;

public class LockManager {

	// 2 Maps for all classes -> Cons : check type with equals, have to store the
	// "fake" entity in lock
	private HashMap<Integer, ArrayList<Lock>> read_locks;
	private HashMap<Integer, ArrayList<Lock>> write_locks;

	public LockManager() {
		this.read_locks = new HashMap<Integer, ArrayList<Lock>>();
		this.write_locks = new HashMap<Integer, ArrayList<Lock>>();
	}

	public boolean add_lock(Object o, boolean l_read, int transaction_hash, LocalTime time) {

		// Replace the boolean with a status (enumeration) : accepted, delayed, aborted
		// ?
		int o_hash = o.hashCode();

		if (l_read) {
			ArrayList<Lock> keys = this.read_locks.get(o_hash);
			if (keys == null) {
				keys = new ArrayList<Lock>();
			}
			// We do not check if there's already a read lock on this obj from this
			// transaction.
			// So we can have several locks from this transaction.
			// May change this later (here or when deleting, detele all locks of this
			// trans.)
			Lock new_lock = new Lock(o, l_read, transaction_hash, time);
			keys.add(new_lock);
			this.read_locks.put(o_hash, keys);
			return true;

		} else {
			ArrayList<Lock> keys = this.write_locks.get(o_hash);
			// If the lock list is null, initialize it
			if (keys == null) {
				keys = new ArrayList<Lock>();
			}
			// Even if list not null, since we know that the object isn't X nor S locked
			// (tested it before calling add_lock)
			Lock new_lock = new Lock(o, l_read, transaction_hash, time);
			keys.add(new_lock);
			this.write_locks.put(o_hash, keys);
			return true;
		}
	}

	public boolean remove_locks(Object o, boolean l_read, int transaction_hash) {

		int o_hash = o.hashCode();

		boolean removed = false;
		ArrayList<Lock> writes = this.write_locks.get(o_hash);
		int i = 0;
		while (!removed && (i <= writes.size())) {
			Object tmp = writes.get(i).getEntity();
			// We can only have one X lock, no need to check transaction_id
			if (o.equals(tmp)) {
				writes.remove(tmp);
				removed = true;
			}
		}

		ArrayList<Lock> reads = this.read_locks.get(o_hash);
		i = 0;
		// For the reads, we can also remove duplicates (if exist), so no "removed"
		// condition
		while (i <= reads.size()) {
			Lock tmp = reads.get(i);
			;
			// We can have several transactions with a read lock, so need to be careful
			if ((transaction_hash == tmp.getTransaction()) && o.equals(tmp.getEntity())) {
				writes.remove(tmp);
			}
		}

		return true;
	}

	public Status isXLocked(Object o, LocalTime time) {
		int hash = o.hashCode();
		boolean already_locked = false;
		ArrayList<Lock> keys = this.write_locks.get(hash);
		// If the list of locks is null : there is no lock
		if (keys == null) {
			return Status.ACCEPTED;
		}

		// But if not null, we have to check if there is a lock on o (same class as o)
		int i = 0;
		while (!already_locked && (i <= keys.size())) {
			already_locked = o.equals(keys.get(i).getEntity());
		}

		// If there is no X lock on this entity
		if (!already_locked) {
			return Status.ACCEPTED;
		}

		// If there is one, apply Wait-Die policy
		if (keys.get(i).hasHigherPrio(time)) {
			return Status.ABORT;
		}

		// This transaction has a higher priority : wait.
		return Status.WAIT;
	}

	public Status isSLocked(Object o, LocalTime time) {
		int hash = o.hashCode();
		boolean already_locked = false;
		ArrayList<Lock> keys = this.read_locks.get(hash);
		// If the list of locks is null : there is no lock
		if (keys == null) {
			return Status.ACCEPTED;
		}

		// But if not null, we have to check if it is a lock on o (same class as o)
		int i = 0;
		while (!already_locked && (i <= keys.size())) {
			already_locked = o.equals(keys.get(i).getEntity());
		}
		// If there is no S lock on this entity
		if (!already_locked) {
			return Status.ACCEPTED;
		}

		// If there is one, apply Wait-Die policy (If we call isSLocked, it is to write)
		if (keys.get(i).hasHigherPrio(time)) {
			return Status.ABORT;
		}

		return Status.WAIT;
	}

}

/*
 * OLD VERSION OF ADD LOCK public boolean add_lock(Object o, boolean l_read, int
 * transaction_hash) {
 * 
 * // Replace the boolean with a status (enumeration) : accepted, delayed,
 * aborted ? int o_hash = o.hashCode();
 * 
 * if (l_read) { ArrayList<Lock> keys = this.read_locks.get(o_hash); if (keys ==
 * null) { keys = new ArrayList<Lock>(); } // We do not check if there's already
 * a read lock on this obj from this // transaction. // So we can have several
 * locks from this transaction. // May change this later (here or when deleting,
 * detele all locks of this // trans.) Lock new_lock = new Lock(o, l_read,
 * transaction_hash); keys.add(new_lock); this.read_locks.put(o_hash, keys);
 * return true ;
 * 
 * } else { ArrayList<Lock> keys = this.write_locks.get(o_hash); // If the lock
 * list is null, initialize it if (keys == null) { keys = new ArrayList<Lock>();
 * } // Even if list not null, since we know that the object isn't X nor S
 * locked // (tested it before calling add_lock) Lock new_lock = new Lock(o,
 * l_read, transaction_hash); keys.add(new_lock); this.write_locks.put(o_hash,
 * keys); return true ; } }
 */
