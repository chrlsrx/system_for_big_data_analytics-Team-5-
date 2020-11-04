package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Warehouse implements DatabaseConstants {
	
	private static ArrayList<String> Strings = new ArrayList<String>(Arrays.asList("w_street2", "w_street1", "w_street2", "w_city", "w_state", "w_zip"));
	// The warehouse id is useless : already in district
	private static ArrayList<String> Integers = new ArrayList<String>(Arrays.asList(""));
	private static ArrayList<String> Reals = new ArrayList<String>(Arrays.asList("w_tax", "w_ytd"));
	

	private final int w_id ;
	private String w_name ;		// useless ? t20
	private String w_street1 ;	// useless ? t20
	private String w_street2 ;	// useless ? t20
	private String w_city ;		// useless ? t20
	private String w_state ;		// useless ? t2
	private String w_zip ;		// useless ? t9
	private double w_tax ;				// useless ?
	private double w_ytd ;				// useless ?
	private HashMap<Integer, District> w_districts ;
	

	
	public Warehouse(int w_id) {
		
		this.w_id = w_id;
		
		// "Useless" attribute are initialized (but important to simulate a real database)
		this.w_name = "Warehouse" + w_id ;
		this.w_street1 = streets1[(int) (Math.random() * 3)] ;
		this.w_street2 = streets2[(int) (Math.random() * 3)] ;
		this.w_city = cities[(int) (Math.random() * 3)] ;
		this.w_state = Integer.toString((int) Math.random() * 99) ;
		this.w_zip = "9512" + Integer.toString((int) Math.random() * 9) ;
		this.w_tax = w_tax_min + Math.random() * (w_tax_max - w_tax_min);
		this.w_ytd = Math.random() * 365; ;
		
		this.w_districts = new HashMap<Integer, District>() ;
	}
	
	
	//Copy Constructor
	public Warehouse(Warehouse w) {
		
		this(w.w_id);
		
		this.w_name = w.w_name;
		this.w_street1 = w.w_street1;
		this.w_street2 = w.w_street2;
		this.w_city = w.w_city;
		this.w_state = w.w_state;
		this.w_zip = w.w_zip;
		this.w_tax = w.w_tax;
		this.w_ytd = w.w_ytd;
		
		
		this.w_districts.putAll(w.w_districts);
		
	}
	
	public void Update(Warehouse w) {
		
		
		this.w_name = w.w_name;
		this.w_street1 = w.w_street1;
		this.w_street2 = w.w_street2;
		this.w_city = w.w_city;
		this.w_state = w.w_state;
		this.w_zip = w.w_zip;
		this.w_tax = w.w_tax;
		this.w_ytd = w.w_ytd;
		
		
		this.w_districts.putAll(w.w_districts);
		
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
	
	
	public HashMap<Integer, District> populate_district(int num) {
		// If we want to avoid the creation in cascade of the instances (otherwise, put in constructor)
		for (int i = 0; i < num; i++) {
			District district = new District(i, this.w_id) ;
			this.w_districts.put(district.hashCode(), district) ; 
		}
		return this.w_districts ;
	}

	//w_id is the primary key
	@Override
	public int hashCode() {
		return Objects.hash(w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Warehouse)) {
			return false;
		}
		Warehouse other = (Warehouse) obj;
		return w_id == other.w_id;
	}
	
	/* TO delete if not needed, only used as an example */
	@Override
	public String toString() {
		return "Warehouse [w_id=" + w_id + ", w_name=" + w_name + ", w_street1=" + w_street1 + ", w_street2="
				+ w_street2 + ", w_city=" + w_city + ", w_state=" + w_state + ", w_zip=" + w_zip + ", w_tax=" + w_tax
				+ ", w_ytd=" + w_ytd + ", w_districts=" + w_districts + "]";
	}
	
	public void setStreet1(String stg) {
		this.w_street1 = stg ;
	}
}
