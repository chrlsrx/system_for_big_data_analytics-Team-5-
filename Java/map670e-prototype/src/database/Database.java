package database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Database implements DatabaseConstants {

	private HashMap<Integer, Customer> customers;
	private HashMap<Integer, District> districts;
	private HashMap<Integer, History> histories;
	private HashMap<Integer, Item> items;
	private HashMap<Integer, NewOrder> neworders;
	private HashMap<Integer, Order_Line> orderlines;
	private HashMap<Integer, Order> orders;
	private HashMap<Integer, Stock> stocks;
	private HashMap<Integer, Warehouse> warehouses;

	public Database() {

		this.warehouses = new HashMap<Integer, Warehouse>();
		this.districts = new HashMap<Integer, District>();
		this.customers = new HashMap<Integer,Customer>();

		for (int i = 0; i < num_warehouses; i++) {
			// Create a new warehouse
			Warehouse wh = new Warehouse(i);
			// Add the new warehouse to the warehouses HashMap
			warehouses.put(wh.hashCode(), wh) ;
			// Populate the warehouses and add the districts to the HashMap
			districts.putAll(wh.populate_district(num_districts)) ;
		}
		
		// Iterate through the districts hashmap, and populate them
		Iterator<Entry<Integer, District>> it = this.districts.entrySet().iterator();
	    while (it.hasNext()) {
	            HashMap.Entry<Integer, District> pair = (HashMap.Entry<Integer, District>) it.next();
	            // Populate and add
	            customers.putAll(pair.getValue().populate_clients(num_customers));
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
 * 4. <Optionnel> Cr�er une m�thode dans Database qui sauvegarde une hashmap en csv (avec les valeurs ET les cl�s*)
 * pour chaque hashmap (un district.csv, un hashmap.csv etc, le tout sauvegarder dans le dossier ressources ?)
 * 
 * 5. <Optionnel> Cr�er une m�thode dans Database qui charge une hashmap et ses entit�s � partir d'un csv. 
 * 
 * 6. <Optionnel> Cr�er une m�thode qui charge UNE entit� � partir d'une ligne du csv : l'id�e serait que lorsqu'on fait un
 * "select" (client), on cr�e un client avec la cl� primaire, on calcule le hashcode, on cherche dans clients.csv
 * la ligne avec le m�me hashmap au d�but, puis on charge le client correspondant � la ligne
 * 
 * 
 */




