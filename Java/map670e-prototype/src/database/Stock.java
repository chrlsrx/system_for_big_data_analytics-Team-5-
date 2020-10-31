package database;

import java.util.HashMap;
import java.util.Objects;

public class Stock implements DatabaseConstants{
	private int s_i_id;
	private int s_w_id;
	private double s_quantity;
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
	private double s_ytd;
	private double s_order_cnt;
	private double s_remote_cnt;
	private String s_data;
	private static HashMap<Integer,Integer> stocks;
	
	public Stock(int i, int j)
	{
		this.s_i_id = i;
		this.s_w_id = j;
		this.stocks.put(i,j);
		
		this.s_quantity = Math.random()*1000;
		this.s_dist01 = districts[(int) Math.random()*10];
		this.s_dist02 = districts[(int) Math.random()*10];
		this.s_dist03 = districts[(int) Math.random()*10];
		this.s_dist04 = districts[(int) Math.random()*10];
		this.s_dist05 = districts[(int) Math.random()*10];
		this.s_dist06 = districts[(int) Math.random()*10];
		this.s_dist07 = districts[(int) Math.random()*10];
		this.s_dist08 = districts[(int) Math.random()*10];
		this.s_dist09 = districts[(int) Math.random()*10];
		this.s_dist10 = districts[(int) Math.random()*10];
		this.s_ytd = Math.random()*100000;
		this.s_order_cnt = Math.random()*1000;
		this.s_remote_cnt = Math.random()*1000;
		this.s_data = information[(int) Math.random()*3];
		
		
		
	}

	@Override
	public int hashCode() {
		return Objects.hash(s_i_id, s_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Stock))
			return false;
		Stock other = (Stock) obj;
		return s_i_id == other.s_i_id && s_w_id == other.s_w_id;
	}
	
	
}
