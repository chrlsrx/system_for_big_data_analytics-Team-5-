package kungRobinson;

import database.Types;
import database.Customer;


public class Simulation {

	public Object updateCustomer(Target customer, String[] args)  {
		
		
		if(customer.get_recordType() != Types.CUSTOMER) {
			System.out.print("Not a customer!");
			return null;
		}
		
		Customer c = (Customer) customer.get_record();
		
		c.Update(args);
		
		
		return new Object();
		
		
		
		
	}
	
	public Function updateCustomer = (Target customer, String[] args) -> {return updateCustomer(customer, args); };
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 	

			Customer trump = new Customer(1, 1, 1);
			
			
			//String[] assignement = {"c_last:trump", "c_discount:100.0"};
			
			String[] assignement = {"c_discount:100.0"};
			trump.Update(assignement);
			
			System.out.println(trump.getClast());

			
			
						

	}

}
