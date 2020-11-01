package map670e;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Math; 
public class DataGeneration {
	private int w_id;
	private int d_id;
	private int c_id;
	private int number_items;
	private int rbk;
	private ArrayList<Integer> items;
	private ArrayList<Integer> ol_suppliers;
	private ArrayList<Integer> ol_identifiers;
	private ArrayList<Double> ol_quantity;
	private Date o_entry ;
	
	public DataGeneration(int w_id)
	{
		this.w_id = w_id;
		this.d_id = (int)(Math.random() * (10)) + 1;
		this.c_id = (((int)(Math.random() * (1023)))|((int)(Math.random() * (3000)) + 1)+(int)(Math.random() * (1023)))%(3000)+1;
		this.number_items = (int)(Math.random() * (10)) + 5;
		this.rbk = (int)(Math.random() * (100)) + 1;
		
		for(int i =0; i <number_items;i++)
		{
			//setting the item number
			int i_num = (((int)(Math.random() * (8191)))|((int)(Math.random() * (100000)) + 1)+(int)(Math.random() * (8191)))%(100000)+1;
			
			if (i < number_items - 1)
			{
				this.ol_identifiers.add(i_num);
			}
			else
			{
				if (this.rbk == 1)
				{
					//set the item to an unused value -> "rolling back the current database transaction" as mentioned in TPC-C file.
				}
				else
				{
					this.ol_identifiers.add(i_num);
				}
			}
			
			//setting the supplying warehouse number 
			int x = (int)(Math.random()*(100))+1;
			if (x != 1)
			{
				this.ol_suppliers.add((int)(Math.random() * (10)) + 1);
			}
			else
			{
				this.ol_suppliers.add(this.w_id);
			}
			
			//setting a quantity
			this.ol_quantity.add((Math.random() * (10)) + 1);
		}
		
		
		this.o_entry =  new Date();
	    
		
		// A quoi correspond cette boucle for ?
		for(int j = 0; j < number_items;j++)
	    {
	    	items.add((((int)(Math.random() * (8191)))|((int)(Math.random() * (100000)) + 1)+(int)(Math.random() * (8191)))%(100000)+1);
	    }
		
		
	}
	public int get_d_id()
	{
		return this.d_id;
	}
	public int get_c_id()
	{
		return this.get_c_id();
	}


}
