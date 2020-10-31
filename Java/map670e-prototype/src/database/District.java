package database;

import java.util.HashMap;
import java.util.Objects;

public class District implements DatabaseConstants {
	
	private final int d_id ;
	private final int d_w_id ;
	private String d_name ;		// useless ? t20
	private String d_street1 ;	// useless ? t20
	private String d_street2 ;	// useless ? t20
	private String d_city ;		// useless ? t20
	private String d_state ;		// useless ? t2
	private String d_zip ;		// useless ? t9
	private double d_tax ;				// useless ?
	private double d_ytd ;				// useless ?
	private int d_next_o_id ;		// useless ?
	
	private HashMap<Integer, Customer> d_clients ;
	
	
	public District(int d_id, int d_w_id) {
		this.d_id = d_id;
		this.d_w_id = d_w_id;
		this.d_clients = new HashMap<Integer, Customer>();
		
		this.d_name = "District" + d_id;
		this.d_street1 = streets1[(int) (Math.random() * 3)];
		this.d_street2 = streets2[(int) (Math.random() * 3)];
		this.d_city = cities[(int) (Math.random() * 3)];
		this.d_state = Integer.toString((int) Math.random() * 99);
		this.d_zip = "9512" + Integer.toString((int) Math.random() * 9);
		this.d_tax = w_tax_min + Math.random() * (w_tax_max - w_tax_min);
		this.d_ytd = Math.random() * 365;
		this.d_next_o_id = (int) Math.random()*10000000;
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
