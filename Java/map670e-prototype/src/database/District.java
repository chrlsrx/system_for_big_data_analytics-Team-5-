package database;

import java.util.HashMap;
import java.util.Objects;

public class District implements DatabaseConstants {

	private final int d_id;
	private final int d_w_id;
	private String d_name;
	private String d_street1;
	private String d_street2;
	private String d_city;
	private String d_state;
	private String d_zip;
	private double d_tax;
	private double d_ytd;
	private int d_next_o_id;

	private HashMap<Integer, Customer> d_clients;

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
		this.d_next_o_id = (int) Math.random() * 10000000;

	}

	// If we want to avoid the creation in cascade of the instances (otherwise, put
	// in constructor)
	public HashMap<Integer, Customer> populate_clients(int num) {
		// If we want to avoid the creation in cascade of the instances
		for (int i = 0; i < num; i++) {
			Customer ctm = new Customer(i, this.d_id, this.d_w_id) ;
			this.d_clients.put(ctm.hashCode(), ctm) ;
		}
		return this.d_clients ;
	}
	

	public double get_d_tax() {
		return this.d_tax;
	}

	public int get_d_next_o_id() {
		return this.d_next_o_id;
	}
	
	public void inc_next_o_id() {
		this.d_next_o_id++ ;
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
