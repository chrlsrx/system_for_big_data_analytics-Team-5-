package map670e;

import database.*;

public class Read extends Operation {
	
	//private int id ;
	//private Database db ;
	//private boolean has_applied ;
	
	private int target_hash ;
	private Types target_type ;
	
	public Read(int id, Database db, int target_hash, Types target_type) {
		super(id, db) ;
		//this.id = id ;
		//this.db = db ;
		this.target_hash = target_hash ;
		this.target_type = target_type ;
	}
	
	public Object apply() {
		Object true_target = this.db.getObject(this.target_hash, this.target_type) ;
		if (true_target != null) {
			this.has_applied = true ;
		}
		return true_target ;
	}

	public Database getDb() {
		return db;
	}

	public int getId() {
		return id;
	}


	
	
}
