package database;

import java.util.HashMap;

public class District {
	
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
	
	// If we want to avoid the creation in cascade of the instances (otherwise, put in constructor)
	public void populate(int num) {
		for (int i = 0; i < num; i++) {
			this.d_clients.put(i, new Customer(i, this.d_id)) ;
		}
	}

}
