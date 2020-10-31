package database;

import java.util.Objects;

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
		return Objects.hash(no_d_id, no_o_id, no_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NewOrder))
			return false;
		NewOrder other = (NewOrder) obj;
		return no_d_id == other.no_d_id && no_o_id == other.no_o_id && no_w_id == other.no_w_id;
	}

	
}
