package database;

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
	//private static HashMap<Integer,Integer> stocks;
	
	public Stock(int i, int j)
	{
		this.s_i_id = i;
		this.s_w_id = j;
		//this.stocks.put(i,j);
		
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

	

	public double get_s_quantity()
	{
		return this.s_quantity;
	}
	public String get_s_data()
	{
		return this.s_data;
	}
	public String get_s_id(int d_id)
	{
		String ch = "";
		switch(d_id) {
		case 1:
			ch = s_dist01;
			break;
		case 2:
			ch = s_dist02;
			break;
		case 3:
			ch = s_dist03;
			break;
		case 4:
			ch = s_dist04;
			break;
		case 5:
			ch = s_dist05;
			break;
		case 6:
			ch = s_dist06;
			break;
		case 7:
			ch = s_dist07;
			break;
		case 8:
			ch = s_dist08;
			break;
		case 9:
			ch = s_dist09;
			break;
		case 10:
			ch = s_dist10;
			break;
		
		
		}
		return ch;
			
	}
	public void change_s_ytd(double c)
	{
		this.s_ytd += 1;
	}

	public void change_s_order_cnt(double s)
	{
		this.s_order_cnt += s;
	}
	public void change_s_remote_cnt(double d)
	{
		this.s_remote_cnt+=d;
	}
	@Override
	public int hashCode() {
		return Objects.hash(s_i_id,s_w_id) ;
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

