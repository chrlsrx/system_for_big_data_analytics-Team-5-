package database;


import java.util.HashMap;


public class Item {
	private  int id;
	private int i_im_id;
	private String i_name;
	private float i_price;
	private String i_data;
	
	public Item(int id)
	{
		this.id  = id;
		
    }
	public int get_i_im_id()
	{
		return this.i_im_id;
	}
	public String get_i_name()
	{
		return this.i_name;
	}
	public String get_i_data()
	{
		return this.i_data;
	}
	public float get_price()
	{
        return this.i_price;
	}
	public void set_i_data(String ch)
	{
		this.i_data = ch;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}}