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
	

}
