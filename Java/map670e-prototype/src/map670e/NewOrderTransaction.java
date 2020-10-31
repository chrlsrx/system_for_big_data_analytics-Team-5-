import database.*;
import java.util.ArrayList;
public class NewOrderTransaction {
private int w_id;
private DataGeneration data;
private Database db;
public NewOrderTransaction(int w_id, Database d)
{
	this.w_id = w_id;
	this.data = new DataGeneration(w_id);
	this.db = d;
	
}
public void NewOrderTransactionRun()
{

	Warehouse w = (Warehouse)db.getObject(w_id,Types.WAREHOUSE);//we retrieve the warehouse
	int tax = w.get_w_tax();//we get the tax
	int d_id = data.get_d_id();//we get the district number
	int d_code = (new District(w_id,d_id)).hashCode();
	District d = (District)db.getObject(d_code,Types.DISTRICT);
	float d_tax = d.get_d_tax();
	int d_next_o_id = d.get_d_next_o_id()+1;
	int c_id = data.get_c_id();
	int c_code = (new Customer(c_id,d_id,w_id)).hashCode();
	Customer c = (Customer)db.getObject(c_code, Types.CUSTOMER);
	String c_last = c.get_c_last();
	float c_discount = c.get_c_discount();
	String c_credit = c.get_c_credit();
	NewOrder nwd = new NewOrder(d_next_o_id,d_id, w_id);
	Order ord = new Order(d_next_o_id, c_id,d_id , w_id);
	ord.set_o_ol_cnt(data.get_number_items());
	for (int i = 1; i < data.get_ol_suppliers().size();i++)
	{
		if ((data.get_ol_suppliers()).get(i) != (data.get_ol_suppliers()).get(0))
		{	 ord.set_o_all_local(0);
		break;
		}
	}
	ArrayList<Integer> ol_identifiers = data.get_ol_identifiers();
	ArrayList<Integer> ol_suppliers = data.get_ol_suppliers();
	ArrayList<Double> quantity = data.get_ol_quantities();
	int number_items = data.get_number_items();
	db.setObject(ord, Types.ORDER);
	db.setObject(nwd, Types.NEWORDER);
	double total_amount;
	for (int i = 0; i < number_items; i++)
	{   int item_id = ol_identifiers.get(i);
	 int supplier_id = ol_suppliers.get(i);
	   if(ol_identifiers.get(i) == null)
	   {   System.out.println("Not found value");
		   return;
	   }
		int code_item = (new Item(item_id) ).hashCode();
		Item it = (Item)db.getObject(code_item, Types.ITEM);
		float i_price = it.get_price();
		String i_name = it.get_i_name() ;
		String i_data = it.get_i_data();
		int code_stock = (new Stock(item_id,supplier_id)).hashCode();
		Stock s = (Stock)db.getObject(code_stock, Types.STOCK);
		String s_data = s.get_s_data();
		double s_quantity = s.get_s_quantity();
		String s_dis_id = s.get_s_id(d_id);
		if (s_quantity > quantity.get(i)+10)
		{
			s_quantity -= quantity.get(i);
		}
		else
		{
			s_quantity = s_quantity-quantity.get(i)+91;
		}
		s.change_s_ytd(quantity.get(i));
		s.change_s_order_cnt(1);
		if(ol_suppliers.get(i) != w_id)
		{
			s.change_s_remote_cnt(1);
		}
		double ol_amount = quantity.get(i)*i_price;
		if (s_data.indexOf("ORIGINAL") >= 0 && i_data.indexOf("ORIGINAL") >= 0)
		{
			it.set_i_data("B");
		}
		else
		{
			it.set_i_data("G");
		}
		Order_Line ol = new Order_Line(item_id,d_id,w_id,i+1);
		ol.set_ol_amount(ol_amount);
		ol.set_ol_dist_info(s_dis_id);
		db.setObject(ol, Types.ORDER_LINE);
		total_amount += ol_amount*(1-c_discount)*(1+tax+d_tax);
		
	}
	System.out.println(total_amount);
	
}
}
