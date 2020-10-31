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
	}
	public float get_s_quantity()
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

}

