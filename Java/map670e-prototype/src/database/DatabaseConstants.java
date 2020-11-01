package database;

public interface DatabaseConstants {
	
	/*
	 * This interface allows us to tune some parameters quickly
	 * 
	 */
	
	int num_warehouses = 1 ;
	int num_districts = 10 ;
	int num_customers = 30000 ;
	int num_items = 200 000;
	
	int w_tax_min = 0 ;
	int w_tax_max = 5 ;
	String[] streets1 = {"5 Morland Top", "2 Kingsley Drive", "95 Fair Meadows"} ; 
	String[] streets2 = {"17 Robert Street", "20 Ivy Lane", "1 Brookside Close"} ;
	String[] cities = {"London", "Norwich", "Leicester"} ;
	String[] states = {"state1", "state2", "state3"};
	String[] phone_nb = {"+33652458544", "+21693361666", "+9192254855571"};
	String[] credits = {"GC", "BC"};
	
 	
	String[] information = {"information 1", "information 2", "information 3"};
	String[] names_of_items = {"item1", "item2", "item3"}; 
	String[] brands = {"brand1", "brand2", "brand3"};
	String[] districts = {"dist1", "dist2", "dist3", "dist4", "dist5", "dist6", "dist7", "dist8", "dist9", "dist10"};
	
	int nb_w = 50; //the number of warehouses
}
