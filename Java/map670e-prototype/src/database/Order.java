package database;

import java.util.Date;
import java.util.HashMap;

public class Order {

	// The warehouse & district id is useless : already in district (but may be added later)
	
	private final int o_id;
	private final int o_c_id ;
	private Date o_entry ;
	private float o_ol_cnt ;
	private float o_all_local ;
	private Integer[] o_carriers ;

	public Order(int o_id, int o_c_id) {
		this.o_id = o_id ;
		this.o_c_id = o_c_id ;
		this.o_carriers = new Integer[5] ;
	}
	
	//Need to populate carriers.
}



