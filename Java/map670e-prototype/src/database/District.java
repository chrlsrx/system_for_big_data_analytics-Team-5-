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
	private int d_next_o_id ;
	private Key_district key_id;
	                           // useless ?
	
	private HashMap<Integer, Customer> d_clients ;
	
	
	public District(int d_id, int d_w_id) {
		this.d_id = d_id ;
		this.d_w_id = d_w_id ;
		this.key_id = new Key_district(d_w_id,d_id) ;
	}
	
	// If we want to avoid the creation in cascade of the instances (otherwise, put in constructor)
	
	public float get_d_tax()
	{
		return this.d_tax;
	}
	public int get_d_next_o_id()
	{
		return this.d_next_o_id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(d_id,d_w_id) ;
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
