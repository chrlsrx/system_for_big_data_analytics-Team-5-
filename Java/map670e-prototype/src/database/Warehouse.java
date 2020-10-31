package database;

import java.util.HashMap;
import java.util.Objects;

public class Warehouse implements DatabaseConstants {
	
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
		this.w_ytd = Math.random() * 365; 
		
		this.w_districts = new HashMap<Integer, District>() ;
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
