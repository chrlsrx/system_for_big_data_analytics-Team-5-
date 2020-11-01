package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class District implements DatabaseConstants {
	
	
	private static ArrayList<String> Strings = new ArrayList<String>(Arrays.asList("d_name", "d_street1", "d_street2", "d_city", "d_state", "d_zip"));
	// The warehouse id is useless : already in district
	private static ArrayList<String> Integers = new ArrayList<String>(Arrays.asList("d_next_o_id"));
	private static ArrayList<String> Reals = new ArrayList<String>(Arrays.asList("d_tax", "d_ytd"));
	
	private final int d_id ;
	private final int d_w_id ;
	private String d_name ;		// useless ? t20
	private String d_street1 ;	// useless ? t20
	private String d_street2 ;	// useless ? t20
	private String d_city ;		// useless ? t20
	private String d_state ;		// useless ? t2
	private String d_zip ;		// useless ? t9
	private float d_tax ;				// useless ?
	private float d_ytd ;				// useless ?
	private int d_next_o_id ;		// useless ?
	
	private HashMap<Integer, Customer> d_clients ;
	

	
	public District(int d_id, int d_w_id) {
		this.d_id = d_id ;
		this.d_w_id = d_w_id ;
		this.d_clients = new HashMap<Integer, Customer>() ;
	}
	
	//Copy Constructor
	public District(District d) {
		
		this(d.d_id, d.d_w_id);
		
		this.d_name = d.d_name ;		
		this.d_street1 = d.d_street1 ;	
		this.d_street2 = d.d_street2 ;
		this.d_city = d.d_city ;		
		this.d_state = d.d_state ;	
		this.d_zip = d.d_zip;	
		this.d_tax = d.d_tax ;			
		this.d_ytd = d.d_ytd ;			
		this.d_next_o_id = d.d_next_o_id ;	
		
		this.d_clients.putAll(d.d_clients);
		
	}
	
	public static void Commit(District d_db, District d_local) {
		
		
		d_db.d_name = d_local.d_name ;		
		d_db.d_street1 = d_local.d_street1 ;	
		d_db.d_street2 = d_local.d_street2 ;
		d_db.d_city = d_local.d_city ;		
		d_db.d_state = d_local.d_state ;	
		d_db.d_zip = d_local.d_zip;	
		d_db.d_tax = d_local.d_tax ;			
		d_db.d_ytd = d_local.d_ytd ;			
		d_db.d_next_o_id = d_local.d_next_o_id ;	
		
		
		d_db.d_clients.putAll(d_local.d_clients);
		
	}
	
	
	@SuppressWarnings("all")
	public void Update(String[] args) {
		
		    

		    
		    for(String arg : args) {
				
		    	
				//Here the assumption is that args is an array of string of the form
				//key:value, where key is the attribute name and value is its value.
				String[] keyValue = arg.split(":");
				
				String code = "";
				
				

				//This line complies the following code
				//it casts the value of an attribute based on its type
				
				if(Strings.contains(keyValue[0])) {
					code = String.format("this.%s = \"%s\";", keyValue[0], keyValue[1]);
					
				}
				else if (Integers.contains(keyValue[0]))
				{
					code = String.format("this.%s = parseInt(\"%s\");", keyValue[0], keyValue[1]);

				}
				else if (Reals.contains(keyValue[0]))
				{
					code = String.format("this.%s = parseFloat(\"%s\");", keyValue[0], keyValue[1]);

				}
				

				

				ScriptEngineManager mgr = new ScriptEngineManager();
			    ScriptEngine engine = mgr.getEngineByName("JavaScript");
			    
			    try {
					System.out.println(engine.eval(code));
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			   

			   
			    
		    }
			
			

		}
	
	
	public HashMap<Integer, Customer> populate_clients(int num) {
		// If we want to avoid the creation in cascade of the instances
		for (int i = 0; i < num; i++) {
			Customer ctm = new Customer(i, this.d_id, this.d_w_id) ;
			this.d_clients.put(ctm.hashCode(), ctm) ;
		}
		return this.d_clients ;
	}


	@Override
	public int hashCode() {
		return Objects.hash(d_id, d_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof District)) {
			return false;
		}
		District other = (District) obj;
		return d_id == other.d_id && d_w_id == other.d_w_id;
	}
	


}
