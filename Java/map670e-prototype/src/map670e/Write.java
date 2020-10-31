package map670e;

import database.Database;
import database.Types;

public class Write extends Operation {
	
	/*
	 * This type of operation allows us to update or add an object in the database.
	 * 
	 * The Object target is the new/updated object that we want to insert in the database
	 * So you need to build it before creating the write operation
	 * This may require the use of set methods of the corresponding class
	 * 
	 * When updating, the main issue with this solution is that we may have to read the object, 
	 * create a copy, modify the fields we want to change, and then put it in the database (by calling apply)
	 * 
	 * An easy solution when we want to update an object is to, in the transaction :
	 * 		-> create an operation read, instead of creating an operation write ;
	 * 		-> update the object that was read (it will also change it in the database) ;
	 * 		-> but make sure to Xlock it before, since it's a disguised write ;
	 */
	
	protected Object target ;
	protected Types target_type ;
	
	public Write(int id, Database db, Object target, Types target_type) {
		super(id, db) ;
		this.target = target ;
		this.target_type = target_type ;
	}
	
	public boolean apply() {
		this.has_applied = this.db.setObject(this.target, this.target_type) ;
		return this.has_applied ;
	}

}