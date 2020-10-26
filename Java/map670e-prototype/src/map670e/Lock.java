package map670e;

public class Lock {
	
	//private final int l_id ;
	private boolean l_on_read ; // Not really useful, but we could check that locks are in the right place
	private int l_transaction_id ;
	private Object l_entity ;
	
	public Lock(Object l_entity, boolean l_on_read, int l_transaction_id) {
		//this.l_id = l_id ;
		this.l_on_read = l_on_read ;
		this.l_transaction_id = l_transaction_id ;
		this.l_entity = l_entity ;
	}
	
	public Object getEntity() {
		return this.l_entity ;
	}

	public int getTransaction() {
		return l_transaction_id ;
	}
	
}
