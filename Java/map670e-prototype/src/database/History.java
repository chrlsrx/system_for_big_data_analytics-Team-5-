package database;

import java.util.Date;

public class History {
	private int h_c_id ;
	private int h_d_id ;
	private int h_w_id ;
	private Date h_date ;
	private float h_amount ;
	private String h_data ;
	
	public History(int h_c_id, int h_d_id, int h_w_id) {
		this.h_c_id = h_c_id ;
		this.h_d_id = h_d_id ;
		this.h_w_id = h_w_id ;
	}
	
}