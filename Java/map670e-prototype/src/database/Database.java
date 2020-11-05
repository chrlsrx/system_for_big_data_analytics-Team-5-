package database;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Database implements DatabaseConstants {

	private Hashtable<Integer, Customer> customers;
	private Hashtable<Integer, District> districts;
	private Hashtable<Integer, History> histories;
	private Hashtable<Integer, Item> items;
	private Hashtable<Integer, NewOrder> neworders;
	private Hashtable<Integer, Order_Line> orderlines;
	private Hashtable<Integer, Order> orders;
	private Hashtable<Integer, Stock> stocks;
	private Hashtable<Integer, Warehouse> warehouses;

	public Database() {

		this.warehouses = new Hashtable<Integer, Warehouse>();
		this.districts = new Hashtable<Integer, District>();
		this.customers = new Hashtable<Integer,Customer>();
		this.histories = new Hashtable<Integer,History>();
		this.items = new Hashtable<Integer,Item>();
		this.neworders = new Hashtable<Integer,NewOrder>();
		this.orderlines = new Hashtable<Integer, Order_Line>();
		this.orders = new Hashtable<Integer, Order>() ;
		this.stocks = new Hashtable<Integer, Stock>() ;

		for (int i = 0; i < num_warehouses; i++) {
			// Create a new warehouse
			Warehouse wh = new Warehouse(i);
			// Add the new warehouse to the warehouses Hashtable
			warehouses.put(wh.hashCode(), wh) ;
			// Populate the warehouses and add the districts to the Hashtable
			districts.putAll(wh.populate_district(num_districts)) ;
			// Populate the stocks
			stocks.putAll(wh.populate_stock(num_stocks)) ;
			// Populate the items (here equality because the items are common to the warehouses
			items = wh.populate_item(num_items) ;
			
			// create a set view
		      Set<Integer> keys = districts.keySet();
		      for(Integer key: keys){
		    	  customers.putAll(districts.get(key).populate_clients(num_customers)); ;
		        }
  
		}
		
		  
	    // ATTENTION : DO THE SAME FOR THE OTHER CLASSES

	}
	
	public Object getObject(int target_hash, Types target_type) {

		switch (target_type) {
		
		case CUSTOMER:
			return (this.customers.get(target_hash));
		
		case DISTRICT:
			return (this.districts.get(target_hash));

		case HISTORY:
			return (this.histories.get(target_hash));

		case ITEM:
			return (this.items.get(target_hash));

		case NEWORDER:
			return (this.neworders.get(target_hash));

		case ORDER_LINE:
			return (this.orderlines.get(target_hash));

		case ORDER:
			return (this.orders.get(target_hash));

		case STOCK:
			return (this.stocks.get(target_hash));

		case WAREHOUSE:
			return (this.warehouses.get(target_hash));

		default:
			return (null);
		}
	}
	
	public boolean setObject(Object target, Types target_type) {

		int hash = target.hashCode() ;
		switch (target_type) {
		
		case CUSTOMER:
			this.customers.put(hash, (Customer) target) ;
			return true ;
		
		case DISTRICT:
			this.districts.put(hash, (District) target) ;
			return true ;

		case HISTORY:
			this.histories.put(hash, (History) target) ;
			return true ;

		case ITEM:
			this.items.put(hash, (Item) target) ;
			return true ;

		case NEWORDER:
			this.neworders.put(hash, (NewOrder) target) ;
			return true ;

		case ORDER_LINE:
			this.orderlines.put(hash, (Order_Line) target) ;
			return true ;

		case ORDER:
			this.orders.put(hash, (Order) target) ;
			return true ;

		case STOCK:
			this.stocks.put(hash, (Stock) target) ;
			return true ;

		case WAREHOUSE:
			this.warehouses.put(hash, (Warehouse) target) ;
			return true ;

		default:
			return false;
		}
	}
	
	
}


/* TODO LIST : concernant la BDD
 * 
 * 0. V�rifier que tous les attributs primaires ont bien �t� ajout�s dans chaque classe (j'avais enlev� des
 * bouts de la cl� primaire car je les coryais inutiles par exemple)
 * 
 * 1. Am�liorer/Faire les constructeurs : 
 *  - en argument, uniquement la cl� primaire + la/les collection (ex: voir Customer)
 *  - dans le contenu, initialiser tous les champs "inutiles" pseudo-al�atoirement (ex: voir Warehouse)
 * 
 * 2. G�n�rer les m�thodes HashCode / Equals avec les attributs de la cl� primaire uniquement (ex: Customer)
 * (normalement peut se faire automatiquement avec eclipse)
 * 
 * 3. Poursuivre la g�n�ration en cascade des donn�es, sur le mod�le de ce qui est fait juste au dessus
 * 
 * 4. <Optionnel> Cr�er une m�thode dans Database qui sauvegarde une Hashtable en csv (avec les valeurs ET les cl�s*)
 * pour chaque Hashtable (un district.csv, un Hashtable.csv etc, le tout sauvegarder dans le dossier ressources ?)
 * 
 * 5. <Optionnel> Cr�er une m�thode dans Database qui charge une Hashtable et ses entit�s � partir d'un csv. 
 * 
 * 6. <Optionnel> Cr�er une m�thode qui charge UNE entit� � partir d'une ligne du csv : l'id�e serait que lorsqu'on fait un
 * "select" (client), on cr�e un client avec la cl� primaire, on calcule le hashcode, on cherche dans clients.csv
 * la ligne avec le m�me Hashtable au d�but, puis on charge le client correspondant � la ligne
 * 
 * 
 */




