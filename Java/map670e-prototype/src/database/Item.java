package database;

import java.util.HashMap;


public class Item {
	private  int id;
	private int i_im_id;
	private String i_name;
	private float i_price;
	private String i_data;
	private static HashMap<Integer, Integer> items = new HashMap<Integer,Integer>() ;
	
	public Item(int i,int j)
	{
		this.id  = i;
		this.i_im_id = j;
		this.items.put(i, j);
	}
	
	//Copy Constructor
	public Item(Item i) {
		
		this(i.id, i.i_im_id);
		
		
	}
	
	

}