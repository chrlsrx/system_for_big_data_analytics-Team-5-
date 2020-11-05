package map670e;

import java.util.Hashtable;
import java.util.Vector;
import java.time.LocalTime;


public class LockManager {

	// 2 Maps for all classes -> Cons : check type with equals, have to store the
	// "fake" entity in lock
	private Hashtable<Integer, Vector<Lock>> read_locks;
	private Hashtable<Integer, Vector<Lock>> write_locks;

	public LockManager() {
		this.read_locks = new Hashtable<Integer, Vector<Lock>>();
		this.write_locks = new Hashtable<Integer, Vector<Lock>>();
	}

	public synchronized boolean add_lock(Object o, boolean l_read, int transaction_id, LocalTime time) {

		// Replace the boolean with a status (enumeration) : accepted, delayed, aborted
		// ?
		int o_hash = o.hashCode();
		if (l_read) {
			Vector<Lock> keys = this.read_locks.get(o_hash);
			if (keys == null) {
				keys = new Vector<Lock>();
			}
			// We do not check if there's already a read lock on this obj from this
			// transaction.
			// So we can have several locks from this transaction.
			// May change this later (here or when deleting, detele all locks of this
			// trans.)
			Lock new_lock = new Lock(o, l_read, transaction_id, time);
			keys.add(new_lock);
			this.read_locks.put(o_hash, keys);
			return true;

		} else {
			Vector<Lock> keys = this.write_locks.get(o_hash);
			// If the lock list is null, initialize it
			if (keys == null) {
				keys = new Vector<Lock>();
			}
			// Even if list not null, since we know that the object isn't X nor S locked
			// (tested it before calling add_lock)
			Lock new_lock = new Lock(o, l_read, transaction_id, time);
			keys.add(new_lock);
			this.write_locks.put(o_hash, keys);
			return true;
		}
	}

	public synchronized Status isXLocked(Object o, int transaction_id, LocalTime time) {

		int hash = o.hashCode();
		boolean already_locked = false;
		Vector<Lock> keys = this.write_locks.get(hash);
		// If the list of locks is null : there is no lock
		if (keys == null || keys.size() == 0) {
			return Status.ACCEPTED;
		}

		// But if not null, we have to check if there is a lock on o (same class as o)
		int i = 0;
		while (!already_locked && (i < keys.size())) {
			already_locked = o.equals(keys.get(i).getEntity());
			i++;
		}

		// If there is no X lock on this entity
		if (!already_locked) {
			return Status.ACCEPTED;
		}
		// There is the case where we update a value : we added a SLock first, and now
		// we're trying to add a Xlock
		if (keys.get(i - 1).getTransaction() == transaction_id) {
			return Status.ACCEPTED;
		}
				
		// If there is one, apply Wait-Die policy
		if (keys.get(i - 1).hasHigherPrio(time)) {
			//System.out.println("bc X");
			return Status.ABORT;
		}

		

		// This transaction has a higher priority : wait.
		return Status.WAIT;
	}

	public synchronized Status isSLocked(Object o, int transaction_id, LocalTime time) {
		int hash = o.hashCode();
		boolean already_locked = false;
		Vector<Lock> keys = this.read_locks.get(hash);
		// If the list of locks is null : there is no lock
		if (keys == null || keys.size() == 0) {
			return Status.ACCEPTED;
		}

		// But if not null, we have to check if it is a lock on o (same class as o)
		int i = 0;
		while (!already_locked && (i < keys.size())) {
			already_locked = o.equals(keys.get(i).getEntity());
			i++;
		}
		// If there is no S lock on this entity
		if (!already_locked) {
			return Status.ACCEPTED;
		}

		// There is the case where we update a value : we added a SLock first, and now
		// we're trying to add a Xlock
		if (keys.get(i - 1).getTransaction() == transaction_id) {
			return Status.ACCEPTED;
		}
		
		// If there is one, apply Wait-Die policy (If we call isSLocked, it is to write)
		if (keys.get(i - 1).hasHigherPrio(time)) {
			return Status.ABORT;
		}



		return Status.WAIT;
	}

	public synchronized boolean remove_locks(Object o, boolean l_read, int transaction_id) {

		int o_hash = o.hashCode();
		boolean removed = false;
		int i = 0;

		if (!l_read) {
			Vector<Lock> writes = this.write_locks.get(o_hash);
			if (writes == null) {
				this.write_locks.remove(o_hash) ;
				return true; // But should never happen
			}
			while (!removed && (i < writes.size())) {
				Lock tmp = writes.get(i);
				// We can only have one X lock, no need to check transaction_id
				if ((transaction_id == tmp.getTransaction()) && o.equals(tmp.getEntity())) {
					//int s1 = this.write_locks.get(o_hash).size() ;
					//writes.remove(i);
					//int s2 = this.write_locks.get(o_hash).size() ;
					//System.out.println(s1 + "," + s2);
					pop_lock(o_hash, i, l_read) ;
					removed = true;
					

					if (this.write_locks.get(o_hash).size() == 0 || this.write_locks.get(o_hash)==null) {
						this.write_locks.remove(o_hash) ;
					} 
				}
				i++;
			}
			return true;
		} else {
			Vector<Lock> reads = this.read_locks.get(o_hash);
			if (reads == null) {
				this.write_locks.remove(o_hash) ;
				return true; // But should never happen
			}
			i = 0;
			// For the reads, we can also remove duplicates (if exist), so no "removed"
			// condition
			while (!removed && i < reads.size()) {
				Lock tmp = reads.get(i);
				// We can have several transactions with a read lock, so need to be careful
				if ((transaction_id == tmp.getTransaction()) && o.equals(tmp.getEntity())) {
					//int s1 = this.write_locks.get(o_hash).size() ;
					//reads.remove(i);
					//int s2 = this.write_locks.get(o_hash).size() ;
					//System.out.println(s1 + "," + s2);
					pop_lock(o_hash, i, l_read) ;

					if (this.read_locks.get(o_hash).size() == 0 || this.read_locks.get(o_hash)==null) {
						this.read_locks.remove(o_hash) ;
					}
					removed = true ;
					
					
					
				}
				i++;
			}
			return true;
		}
	}

	public synchronized boolean rremove_all_locks(int transaction_id, Vector<Integer> objs_hash, Vector<Boolean> isReads) {
		
		boolean ok = true ;
		int size = objs_hash.size() ;
		System.out.println("size " +objs_hash.size());
		
		for (int i = 0; i <= size; i++) {
			System.out.println("haah " + i);
			int obj_hash = objs_hash.get(i);
			boolean isRead = isReads.get(i);

			boolean removed = false;
			if (isRead) {
				Vector<Lock> reads = this.read_locks.get(obj_hash);
				if (reads == null) {
					return true; // But should never happen
				}
				int j = 0;
				while (!removed && (j < reads.size())) {
					int tmp = reads.get(j).getTransaction();
					// We can only have one X lock, no need to check transaction_id
					if (tmp == transaction_id) {
						//int s1 = this.read_locks.get(obj_hash).size() ;
						//reads.remove(j);
						//int s2 = this.read_locks.get(obj_hash).size() ;
						//System.out.println(s1 + "," + s2);
						pop_lock(obj_hash, j, isRead) ;
						//removed = true;

						if (this.read_locks.get(obj_hash).size() == 0 || this.read_locks.get(obj_hash)==null) {
							this.read_locks.remove(obj_hash) ;
						}
					}
					j++;
				}

			} else {
				if (!isRead) {
					Vector<Lock> writes = this.write_locks.get(obj_hash);
					if (writes == null) {
						return true; // But should never happen
					}
					int j = 0;
					while (!removed && (j < writes.size())) {
						int tmp = writes.get(j).getTransaction();
						// We can only have one X lock, no need to check transaction_id
						if (tmp == transaction_id) {
							pop_lock(obj_hash, j, isRead) ;
							//int s1 = this.write_locks.get(obj_hash).size() ;
							//writes.remove(j);
							//int s2 = this.write_locks.get(obj_hash).size() ;
							//System.out.println(s1 + "," + s2);
							//removed = true;
							
							
							if (this.write_locks.get(obj_hash).size() == 0 || this.write_locks.get(obj_hash)==null) {
								this.write_locks.remove(obj_hash) ;
							}
						}
						j++;
					}

				}
			}
		}
		return false;
	}
	
	
public synchronized boolean remove_all_locks(int transaction_id, Vector<Integer> objs_hash, Vector<Boolean> isReads) {
		
		boolean ok = true ;
		int size = objs_hash.size() ;
		//System.out.println("size " + objs_hash.size() + " " + isReads.size());
		
		int i = 0 ;
		while( i < size ) {
			//System.out.println("haah " + i);
			int obj_hash = objs_hash.get(i);
			boolean isRead = isReads.get(i);
			boolean removed = false ;
			
			if (isRead) {
				Vector<Lock> reads = this.read_locks.get(obj_hash);
				if (reads == null) {
					// May happen, since we don't clear the lock_list in the transaction.
				} else {
					int j = 0;
					while (!removed && (j < reads.size())) {
						int tmp = reads.get(j).getTransaction();
						// We can only have one X lock, no need to check transaction_id
						if (tmp == transaction_id) {
							pop_lock(obj_hash, j, isRead);
							//removed = true ;
							if (this.read_locks.get(obj_hash).size() == 0 || this.read_locks.get(obj_hash) == null) {
								this.read_locks.remove(obj_hash);
							}
						}
						j++;
					}
				}
				i++;
			} else {
				Vector<Lock> writes = this.write_locks.get(obj_hash);
				if (writes == null) {
					// May happen, since we don't clear the lock_list in the transaction.
				} else {
					int j = 0;
					while (!removed && (j < writes.size())) {
						int tmp = writes.get(j).getTransaction();
						// We can only have one X lock, no need to check transaction_id
						if (tmp == transaction_id) {
							pop_lock(obj_hash, j, isRead);
							//removed = true ;
							if (this.write_locks.get(obj_hash).size() == 0 || this.write_locks.get(obj_hash) == null) {
								this.write_locks.remove(obj_hash);
							}
						}
						j++;
					}
				}
				i++;
			}
		}
		return false;
	}
	
	

	private synchronized void pop_lock(int obj_hash, int idx, boolean isRead) {
		if (isRead) {
			Vector<Lock> locks = this.read_locks.get(obj_hash);
			locks.remove(idx) ;
			update_Locks(obj_hash, locks, isRead) ;
		} else {
			Vector<Lock> locks = this.write_locks.get(obj_hash);
			locks.remove(idx) ;
			update_Locks(obj_hash, locks, isRead) ;
		}
	}
	
	private synchronized void update_Locks(int obj_hash, Vector<Lock> locks, boolean isRead) {
		if (isRead) {
			this.read_locks.put(obj_hash, locks);

		} else {
			this.write_locks.put(obj_hash, locks);

		}
	}

	
	public void show() {
		for (int i = 0; i < this.write_locks.size(); i++) {
			Vector<Lock> locks = this.write_locks.get(i) ;
			
			if (locks != null) {
				for (int j = 0; j < locks.size(); j++) {
					System.out.println(locks.get(j));
				}
			} else {
				//System.out.println(locks);
			}
		}
		for (int i = 0; i < this.read_locks.size(); i++) {
			Vector<Lock> locks = this.read_locks.get(i) ;
			
			if (locks != null) {
				for (int j = 0; j < locks.size(); j++) {
					System.out.println(locks.get(j));
				}
			} else {
				//System.out.println(locks);
			}
		}
	}
	
	public void reset() {
		this.read_locks = new Hashtable<Integer, Vector<Lock>>();
		this.write_locks = new Hashtable<Integer, Vector<Lock>>();
	}
	
	@Override
	public String toString() {
		return "LockManager [read_locks=" + read_locks.size() + ", write_locks=" + write_locks.size() + "]";
	}

	
}

/*
 * OLD VERSION OF ADD LOCK public boolean add_lock(Object o, boolean l_read, int
 * transaction_hash) {
 * 
 * // Replace the boolean with a status (enumeration) : accepted, delayed,
 * aborted ? int o_hash = o.hashCode();
 * 
 * if (l_read) { Vector<Lock> keys = this.read_locks.get(o_hash); if (keys ==
 * null) { keys = new Vector<Lock>(); } // We do not check if there's already
 * a read lock on this obj from this // transaction. // So we can have several
 * locks from this transaction. // May change this later (here or when deleting,
 * detele all locks of this // trans.) Lock new_lock = new Lock(o, l_read,
 * transaction_hash); keys.add(new_lock); this.read_locks.put(o_hash, keys);
 * return true ;
 * 
 * } else { Vector<Lock> keys = this.write_locks.get(o_hash); // If the lock
 * list is null, initialize it if (keys == null) { keys = new Vector<Lock>();
 * } // Even if list not null, since we know that the object isn't X nor S
 * locked // (tested it before calling add_lock) Lock new_lock = new Lock(o,
 * l_read, transaction_hash); keys.add(new_lock); this.write_locks.put(o_hash,
 * keys); return true ; } }
 */
