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
	private double ol_quantity;
	private double ol_amount = 0;
	private String ol_dist_info = "";
	public Order_Line(int i, int j, int k, int l)
	{this.ol_o_id = i;
	this.ol_d_id = j;
	this.ol_w_id = k;
	this.ol_number = l;
	this.ol_delivery_d = null;  
		
	}
	public void set_ol_amount(double g)
	{
		this.ol_amount = g;
	}
	public void set_ol_dist_info(String s)
	{
		this.ol_dist_info = s;
	}
	@Override
	public int hashCode() {
		return Objects.hash(ol_o_id, ol_d_id, ol_w_id,ol_number);
	}

}
