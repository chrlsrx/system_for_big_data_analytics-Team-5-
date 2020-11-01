package database;

import java.util.Date;
public class Order_Line {
	private int ol_o_id;
	private int ol_d_id;
	private int ol_w_id;
	private int ol_number;
	private int ol_i_id;
	private int ol_supply_w_id;
	private Date ol_delivery_d;
	private float ol_quantity;
	private float ol_amount;
	private String ol_dist_info;
	public Order_Line(int i, int j, int k, int l)
	{this.ol_o_id = i;
	this.ol_d_id = j;
	this.ol_w_id = k;
	this.ol_number = l;
		
	}
	
	//Copy Constructor
	public Order_Line(Order_Line ol) {
		
		this(ol.ol_o_id, ol.ol_d_id, ol.ol_w_id, ol.ol_number);
		
	}

}
