package database;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.*;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


public class Customer  {
	
	private static ArrayList<String> Strings = new ArrayList<String>(Arrays.asList("c_first", "c_middle", "c_last", "c_street1", "c_street2", "c_city", "c_zip", "c_phone", "c_credit", "c_data"));
	// The warehouse id is useless : already in district
	private static ArrayList<String> Integers = new ArrayList<String>(Arrays.asList("c_payment_cnt", "c_delivry_cnt"));
	private static ArrayList<String> Reals = new ArrayList<String>(Arrays.asList("c_credit_lim", "c_discount", "c_balance", "c_ytd_payment"));
	
	//private static ArrayList<String> dates = new ArrayList<String>(Arrays.asList("c_since"));
	
	private final int c_id ;
	private final int c_d_id ;
	private final int c_w_id ;
	
	private String c_first = "" ;		// useless ? t16
	private String c_middle = "" ;		// useless ? t2
	private String c_last = "";			// useless ? t16
	private String c_street1 = "";		// useless ? t20
	private String c_street2 = "";		// useless ? t20
	private String c_city = "";			// useless ? t20
	private String c_state = "";		// useless ? t2
	private String c_zip = "";			// useless ? t9
	private String c_phone = "";		// useless ? t16
	private Date c_since = new Date(1980, 1, 1);
	private String c_credit = "";		// size 2 "GC"=good, "BC"=bad
	private Float c_credit_lim = 0.0f;
	private Float c_discount = 0.0f;
	private Float c_balance = 0.0f;
	private Float c_ytd_payment = 0.0f;
	private Integer c_payment_cnt = 0;
	private Integer c_delivry_cnt = 0;
	private String c_data ="";			// useless ? t500
	
	
	
	
	
	private HashMap<Integer, Order> c_orders ;
	
	public Customer(int c_id, int c_d_id, int c_w_id) {
		this.c_id = c_id ;
		this.c_d_id = c_d_id ;
		this.c_w_id = c_w_id ;
		this.c_orders = new HashMap<Integer, Order>() ;
	}
	
	//Copy Constructor
	public Customer(Customer c) {
		
		this(c.c_id, c.c_d_id, c.c_w_id);
		
		this.c_first = c.c_first;		
		this.c_middle = c.c_middle;	
		this.c_last = c.c_last ;		
		this.c_street1 = c.c_street1 ;	
		this.c_street2 = c.c_street2 ;		
		this.c_city = c.c_city;			
		this.c_state = c.c_state;
		this.c_zip = c.c_zip;			
		this.c_phone = c.c_phone ;		
		this.c_since = c.c_since ;
		this.c_credit = c.c_credit ;		
		this.c_credit_lim = c.c_credit_lim;
		this.c_discount = c.c_discount;
		this.c_balance = c.c_balance ;
		this.c_ytd_payment = c.c_ytd_payment;
		this.c_payment_cnt = c.c_payment_cnt;
		this.c_delivry_cnt = c.c_delivry_cnt;
		this.c_data = c.c_data;	
		
		this.c_orders.putAll(c.c_orders);
		
	}
	
	public static void Commit(Customer c_db, Customer c_local) {
		
		
		c_db.c_first = c_local.c_first;		
		c_db.c_middle = c_local.c_middle;	
		c_db.c_last = c_local.c_last ;		
		c_db.c_street1 = c_local.c_street1 ;	
		c_db.c_street2 = c_local.c_street2 ;		
		c_db.c_city = c_local.c_city;			
		c_db.c_state = c_local.c_state;
		c_db.c_zip = c_local.c_zip;			
		c_db.c_phone = c_local.c_phone ;		
		c_db.c_since = c_local.c_since ;
		c_db.c_credit = c_local.c_credit ;		
		c_db.c_credit_lim = c_local.c_credit_lim;
		c_db.c_discount = c_local.c_discount;
		c_db.c_balance = c_local.c_balance ;
		c_db.c_ytd_payment = c_local.c_ytd_payment;
		c_db.c_payment_cnt = c_local.c_payment_cnt;
		c_db.c_delivry_cnt = c_local.c_delivry_cnt;
		c_db.c_data = c_local.c_data;	
		
		c_db.c_orders.putAll(c_local.c_orders);
		
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
			
			else {
				code = String.format("this.%s = Date.parse(\"%s\");", keyValue[0], keyValue[1]);

			}
			
			//System.out.print(code);

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

	
	
	@Override
	public int hashCode() {
		return Objects.hash(c_d_id, c_id, c_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		return c_d_id == other.c_d_id && c_id == other.c_id && c_w_id == other.c_w_id;
	}

	// If we want to avoid the creation in cascade of the instances (otherwise, put in constructor)
	public void populate(int num) {
		for (int i = 0; i < num; i++) {
			this.c_orders.put(i, new Order(i, this.c_id, this.c_d_id, this.c_w_id)) ;
		}
	}

	
	
}