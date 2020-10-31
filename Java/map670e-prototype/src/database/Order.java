package database;

import java.util.Date;
import java.util.HashMap;

public class Order {

	// The warehouse & district id is useless : already in district (but may be added later)
	
	private final int o_id;
	private final int o_c_id ;
	private final int o_d_id ;
	private final int o_w_id ;
	private Date o_entry ;
	private int o_ol_cnt ;
	private int o_all_local ;
	private Integer[] o_carriers ;

	public Order(int o_id, int o_c_id, int o_d_id, int o_w_id) {
		this.o_id = o_id ;
		this.o_c_id = o_c_id ;
		this.o_d_id = o_d_id ;
		this.o_w_id = o_w_id ;
		this.o_carriers = null ;
		this.o_ol_cnt = 0;
		this.o_all_local = 1;
	}
	public void set_o_ol_cnt(int num)
	{
		this.o_ol_cnt = num;
	}
	public void set_o_all_local(int n)
	{
		this.o_all_local = n;
	}
	@Override
	public int hashCode() {
		return Objects.hash(o_id, o_c_id,o_d_id, o_w_id);
	}

	//Need to populate carriers.
}



