package map670e;

import java.util.ArrayList;
import java.util.List;

public class Operation {
	private int id ;
	private Order order ; //target in the example, renamed to fit TPC-C model
	// function -> à voir comment traduire : normalement pas un attribut, faut vraie fonction
	// 			   le mieux sera de créer plusieurs classes Opérations_a,b,... qui héritent d'Opération
	private boolean has_lock ;
	private boolean has_applied ;
	
	public Operation(final int id, Order order) {
		this.id = id ;
		this.order = order ;
		this.has_lock = false ;
		this.has_applied = false ;
	}
	
	public boolean apply() {
		boolean success = this.order.apply(this) ;
		this.has_lock = success ;
		return success ;
	}
	
	public boolean get_lock() {
		boolean success = this.order.lock(this) ;
		this.has_lock = success ;
		return success ;
	}
	
	public boolean free_lock() {
		boolean success = this.order.unlock(this) ;
		this.has_lock =  !success ;
		return success ;
	}
	
	public List<Integer> function(List<Integer> df) {
		df.add(5) ;	// à adapter / modifier
		return df ;
	}
	
	
	public int get_id() {
		return this.id ;
	}
	
	public boolean get_has_lock() {
		return this.has_lock ;
	}
	
	public boolean get_has_applied() {
		return this.has_applied ;
	}
	
	//générer les tostring, hash, equals
}
