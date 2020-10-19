package database;

import java.util.Date;

public class History {
	private int h_c_id ;
	// No need for district, warehouse
	private Date h_date ;
	private float h_amount ;
	private String h_data ;
	
	public History(int h_c_id) {
		this.h_c_id = h_c_id ;
	}
	
}