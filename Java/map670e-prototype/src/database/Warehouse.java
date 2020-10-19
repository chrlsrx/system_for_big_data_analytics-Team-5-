package database;

import java.util.HashMap;

public class Warehouse {
	
	private final int w_id ;
	private String w_name ;		// useless ? t20
	private String w_street1 ;	// useless ? t20
	private String w_street2 ;	// useless ? t20
	private String w_city ;		// useless ? t20
	private String w_state ;		// useless ? t2
	private String w_zip ;		// useless ? t9
	private int w_tax ;				// useless ?
	private int w_ytd ;				// useless ?
	private HashMap<Integer, District> w_districts ;
	
	public Warehouse(int w_id, int w_tax, int w_ytd) {
		this.w_id = w_id;
		this.w_tax = w_tax ;
		this.w_tax = w_ytd ;
		this.w_districts = new HashMap<Integer, District>() ;
	}
	
	// If we want to avoid the creation in cascade of the instances (otherwise, put in constructor)
	public void populate(int num) {
		for (int i = 0; i < num; i++) {
			this.w_districts.put(i, new District(i, this.w_id)) ; 
		}
	}

}
