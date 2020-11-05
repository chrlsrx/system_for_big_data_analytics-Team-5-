package map670e;

import database.Database;

public class Operation {
	
	protected int transaction_id ;
	protected Database db ;
	protected boolean has_applied ;
	
	public Operation(final int id, Database db) {
		this.transaction_id = id ;
		this.db = db ;
		this.has_applied = false ;
	}
	
	public boolean get_has_applied() {
		return this.has_applied ;
	}
	
	public int getId() {
		return this.transaction_id;
	}
}

