package kungRobinson;

import kungRobinson.Target;
import database.Customer;

public interface Function {

	//Target is a record with its type..
	Object doSomething(Target record, String[] args);
	
	
}
