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
	@Override
	public int hashCode() {
		return Objects.hash(no_o_id, no_d_id, no_w_id);
	}

}
