package map670e;

import database.Database;
import database.Types;

public class Write extends Operation {
	
	protected Object target ;
	protected Types target_type ;
	
	public Write(int id, Database db, Object target, Types target_type) {
		super(id, db) ;
		this.target = target ;
		this.target_type = target_type ;
	}
	
	public synchronized boolean apply() {
		this.has_applied = this.db.setObject(this.target, this.target_type) ;
		return this.has_applied ;
	}

}