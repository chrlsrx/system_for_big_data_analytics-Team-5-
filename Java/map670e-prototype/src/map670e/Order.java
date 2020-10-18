package map670e;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private List<Integer> df ; // find a proper data structure to replace the df (depends on kind of database)
						       // this one only suites to the prototype
	private boolean locked ;
	private Operation op_with_lock ;
	
	public Order() {
		this.locked = false ;
		this.op_with_lock = null ;
	}

	public boolean apply(Operation op) {
		if (this.locked && this.op_with_lock==null) {
			try {
				this.df = op.function(this.df) ;
				return true ;
			} catch (Exception e) {
				// TODO: handle exception
				throw e ;
			}
		} else {
			return false ;
		}
	}
	
	public boolean lock(Operation op) {
		if (!this.locked && this.op_with_lock==null) {
			this.locked = true ;
			this.op_with_lock = op ;
			return true ; // Success
		} else {
			return false ;
		}
	}
	
	public boolean unlock(Operation op) {
		if (this.locked && this.op_with_lock==op) {
			this.locked = false ;
			this.op_with_lock = null ;
			return true ;
		} else {
			return false ;
		}
	}
	

	
	//générer les tostring, hash, equals
}
