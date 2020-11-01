package kungRobinson;


import java.util.ArrayList;
import java.util.List;
import database.Types;
import database.Database;
import kungRobinson.Function;
import database.Customer;
import database.District;
import database.History;
import database.Item;
import database.NewOrder;
import database.Order_Line;
import database.Order;
import database.Stock;
import database.Warehouse;


public class Target {

	private Object record;
	private Types record_type;
	
	public Target(Object record, Types record_type) {
		
		this.record = record;
		this.record_type = record_type;
		
	}
	
	public Types get_recordType() {
		return this.record_type;
	}
	
	public Object get_record() {
		return this.record;
	}
	
	
	public void Update(Function function, String[] args) {
		
		this.record = function.doSomething(this, args);
		
	}
	
	public void Commit(Target t) {
		
		switch (record_type) {
		
		case CUSTOMER:
			Customer.Commit((Customer)t.record, (Customer)this.record);
					
		case DISTRICT:
			District.Commit((District)t.record, (District)this.record);

		case WAREHOUSE:
			Warehouse.Commit((Warehouse)t.record, (Warehouse)this.record);
			

			
		default:
		}
	}
	
	public Target Copy() {
	
		switch (record_type) {
				
				case CUSTOMER:
					Customer c = new Customer((Customer) record);
					return new Target(c, record_type);
				
				case DISTRICT:
					District d = new District((District) record);
					return new Target(d, record_type);
		
				case WAREHOUSE:
					Warehouse wh = new Warehouse((Warehouse) record);
					return new Target(wh, record_type);
		
				
				case HISTORY:
					History h = new History((History) record);
					return new Target(h, record_type);
		
				case ITEM:
					Item i = new Item((Item) record);
					return new Target(i, record_type);
		
		
				case NEWORDER:
					NewOrder no = new NewOrder((NewOrder) record);
					return new Target(no, record_type);
		
		
				case ORDER_LINE:
					Order_Line ol = new Order_Line((Order_Line) record);
					return new Target(ol, record_type);
		
		
				case ORDER:
					Order o = new Order((Order) record);
					return new Target(o, record_type);
		
		
				case STOCK:
					Stock s = new Stock((Stock) record);
					return new Target(s, record_type);
		
		
				default:
					return (null);
				}
				
	}
	
}
