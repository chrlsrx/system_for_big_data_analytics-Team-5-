package database;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Order {

	// The warehouse & district id is useless : already in district (but may be added later)
	
	private final int o_id;
	private final int o_c_id ;
	private final int o_d_id ;
	private final int o_w_id ;
	private Date o_entry ;
	private double o_ol_cnt ;
	private double o_all_local ;
	private Integer[] o_carriers ;

	public Order(int o_id, int o_c_id, int o_d_id, int o_w_id) {
		this.o_id = o_id ;
		this.o_c_id = o_c_id ;
		this.o_d_id = o_d_id ;
		this.o_w_id = o_w_id ;
		this.o_carriers = new Integer[5] ;
		
		this.o_entry = new Date();
		this.o_ol_cnt = Math.random()*100;
		this.o_all_local = Math.random()*10;
	}

	@Override
	public int hashCode() {
		return Objects.hash(o_d_id, o_id, o_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Order))
			return false;
		Order other = (Order) obj;
		return o_d_id == other.o_d_id && o_id == other.o_id && o_w_id == other.o_w_id;
	}
	
	//Need to populate carriers.
	
	
}



