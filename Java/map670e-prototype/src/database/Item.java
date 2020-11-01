package database;
import java.util.Objects;


import java.util.HashMap;
import java.util.Objects;


public class Item implements DatabaseConstants{
	
	private  int id;
	private int i_im_id;
	private String i_name;
	private double i_price;
	private String i_data;

	public Item(int id)
	{
		this.id  = id;
		
    }
	public int get_i_im_id()
	{
		return this.i_im_id;
	}
	private static HashMap<Integer, Integer> items = new HashMap<Integer,Integer>() ;
	
	public Item(int i,int j)
	{
		this.id  = i;
		this.i_im_id = j;
		this.items.put(i, j);
		
		this.i_name = names_of_items[(int) Math.random()*3];
		this.i_price = Math.random()*1000;
		this.i_data = brands[(int) Math.random()*3];
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		return id == other.id;

	}
	public String get_i_name()
	{
		return this.i_name;
	}
	public String get_i_data()
	{
		return this.i_data;
	}
	public double get_price()
	{
        return this.i_price;
	}
	public void set_i_data(String ch)
	{
		this.i_data = ch;
	}
}