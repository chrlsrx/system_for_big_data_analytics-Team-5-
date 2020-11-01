package database;

public class NewOrder {
	private int no_o_id;
	private int no_d_id;
	private int no_w_id;

	public NewOrder(int i, int j, int k) {
		this.no_o_id = i;
		this.no_d_id = j;
		this.no_w_id = k;

	}
	
	//Copy Constructor
	public NewOrder(NewOrder no) {
		
		this(no.no_o_id, no.no_d_id, no.no_w_id);
		
	}

}
