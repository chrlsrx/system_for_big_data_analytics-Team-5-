package kungRobinson;


import java.util.ArrayList;
import java.util.List;
import database.Types;
import database.Database;
import kungRobinson.Function;


public class Operation {
	// Constants
	public static final String READ = "READ";
	public static final String WRITE = "WRITE";

	// Attributes
	private int id;
	// Types Target and Function might have to change !
	private Target target; // pointer towards the object we want to modify
	private Target local_target; // pointer towards a Transaction's local copy of the object
	private Function function; // what we want to do to the target object
	private String operation_type; // read or write
	private boolean has_applied;
	private String[] args;
	private Database db;
	
	
	public Operation(final int id, Target target, String operation_type, Database db) {
		// READ operation
		this.id = id;
		this.target = target;
		this.operation_type = operation_type;
		this.has_applied = false;
		this.db = db;

	}

	public Operation(final int id, Target target, String operation_type, Database db,  Function function) {
		// WRITE operation
		this.id = id;
		this.target = target;
		this.operation_type = operation_type;
		this.has_applied = false;
		this.db = db;
		this.function = function;
		
	}

	public Target get_target() {
		return this.target;
	}

	public void set_local_target(Target local_target) {
		// Transaction should set the local target of an operation before applying it
		this.local_target = local_target;
	}

	public String get_operation_type() {
		return this.operation_type;
	}

	public boolean apply() {
		
		if (this.operation_type == READ) {
			// Do something (simulate a READ)
			this.has_applied = true;
			return true;
		} else if (this.operation_type == WRITE) {
			// Modify the local_target
			// This syntax has to change depending on the Target object !
			this.local_target.Update(function, args);
			this.has_applied = true;
		}
		return this.has_applied;
	}

}
