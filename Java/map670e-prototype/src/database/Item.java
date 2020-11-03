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
	
	public void Update(Item s) {
		
		//We don't use getters, maybe we need
		this.id = s.id;
		this.i_im_id = s.i_im_id;
		this.i_name = s.i_name;
		this.i_price = s.i_price;
		this.i_data = s.i_data;
}
	//Copy Constructor
	public Item(Item i) {
		
		this(i.id, i.i_im_id);
		
		
	}
	
	

}