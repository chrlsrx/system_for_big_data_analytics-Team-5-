package database;

public interface DatabaseConstants {
	
	/*
	 * This interface allows us to tune some parameters quickly
	 * 
	 */
	
	int num_warehouses = 1 ;
	int num_districts = 10 ;
	int num_customers = 30000 ;
	
	int w_tax_min = 0 ;
	int w_tax_max = 5 ;
	String[] streets1 = {"5 Morland Top", "2 Kingsley Drive", "95 Fair Meadows"} ; 
	String[] streets2 = {"17 Robert Street", "20 Ivy Lane", "1 Brookside Close"} ;
	String[] cities = {"London", "Norwich", "Leicester"} ;
	
	
}
