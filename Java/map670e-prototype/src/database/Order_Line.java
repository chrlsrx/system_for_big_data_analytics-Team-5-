package database;

import java.util.Date;
import java.util.Objects;

public class Order_Line implements DatabaseConstants{
	
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

	private double ol_amount;
	private String ol_dist_info;
	
	public Order_Line(int i, int j, int k, int l)
	{
		this.ol_o_id = i;
		this.ol_d_id = j;
		this.ol_w_id = k;
		this.ol_number = l;

		
		this.ol_i_id = (int) Math.random()*200000;
		this.ol_supply_w_id = (int) Math.random()*nb_w;
		this.ol_delivery_d = new Date();
		this.ol_quantity = Math.random()*100;
		this.ol_amount = Math.random()*100000;
		this.ol_dist_info = information[(int) Math.random()*3];
	}

	@Override
	public int hashCode() {
		return Objects.hash(ol_d_id, ol_number, ol_o_id, ol_w_id);
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Order_Line))
			return false;
		Order_Line other = (Order_Line) obj;
		return ol_d_id == other.ol_d_id && ol_number == other.ol_number && ol_o_id == other.ol_o_id
				&& ol_w_id == other.ol_w_id;
	}
	
	
}
