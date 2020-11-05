package map670e;

import java.time.LocalTime;
import java.util.Objects;

public class Lock {
	
	//private final int l_id ;
	private boolean l_on_read ; // Not really useful, but we could check that locks are in the right place
	private int l_transaction_id ; // may not be useful
	private LocalTime l_transaction_ts ; // Deadlock prevention
	private Object l_entity ;
	
	public Lock(Object l_entity, boolean l_on_read, int l_transaction_id, LocalTime ts) {
		//this.l_id = l_id ;
		this.l_on_read = l_on_read ;
		this.l_transaction_id = l_transaction_id ;
		this.l_entity = l_entity ;
		this.l_transaction_ts = ts ;
	}
	
	public Object getEntity() {
		return this.l_entity ;
	}

	public int getTransaction() {
		return this.l_transaction_id ;
	}
	
	public synchronized boolean hasHigherPrio(LocalTime time) {
		return (this.l_transaction_ts.compareTo(time) < 0) ; // < 0 If ts < time, so higher prio
	}
	
	public boolean isRead() {
		return l_on_read ;
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
