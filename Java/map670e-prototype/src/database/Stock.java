package database;

import java.util.HashMap;

public class Stock {
	private int s_i_id;
	private int s_w_id;
	private float s_quantity;
	private String s_dist01;
	private String s_dist02;
	private String s_dist03;
	private String s_dist04;
	private String s_dist05;
	private String s_dist06;
	private String s_dist07;
	private String s_dist08;
	private String s_dist09;
	private String s_dist10;
	private float s_ytd;
	private float s_order_cnt;
	private float s_remote_cnt;
	private String s_data;
	private static HashMap<Integer,Integer> stocks;
	public Stock(int i, int j)
	{
		this.s_i_id = i;
		this.s_w_id = j;
		this.stocks.put(i,j);
	}
	
	public void Update(Stock s) {
		
			//We don't use getters, maybe we need
			this.s_i_id = s.s_i_id;
			this.s_w_id = s.s_w_id;
			this.s_quantity = s.s_quantity;
			this.s_dist01 = s.s_dist01;
			this.s_dist02 = s.s_dist02;
			this.s_dist03 = s.s_dist03;
			this.s_dist04 = s.s_dist04;
			this.s_dist05 = s.s_dist05;
			this.s_dist06 = s.s_dist06;
			this.s_dist07 = s.s_dist07;
			this.s_dist08 = s.s_dist08;
			this.s_dist09 = s.s_dist09;
			this.s_dist10 = s.s_dist10;
			this.s_ytd=s.s_ytd;
			this.s_order_cnt=s.s_order_cnt;
			this.s_remote_cnt=s.s_remote_cnt;
			this.s_data=s.s_data;
	}
	//Copy Constructor
	public Stock(Stock s) {
		
		this(s.s_i_id, s.s_w_id);
		
		
	}

}
