package map670e;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.Types;
import database.Warehouse;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		Database db = new Database() ;
		System.out.println("Initialized: Database");
		
		LockManager lockm = new LockManager() ;
		System.out.println("Initialized: Lockmanager");
		
		int num_workers =  2 ;
		Scheduler schedule = new Scheduler(num_workers);
		System.out.println("Initialized: Scheduler, workers:" + num_workers);
		
		// The only warehouse should have a hash of 31 : retrieve it
		Warehouse wh = (Warehouse) db.getObject(31, Types.WAREHOUSE) ;
		int wh_hash = wh.hashCode() ;
		System.out.println("Id of the unique warehouse: " + wh_hash);
		
		
		//
		DataGeneration data = new DataGeneration(wh_hash) ;
		
		int num_trans = 5 ;
		ArrayList<NewOrderTransactionLock> transactions  = new ArrayList<NewOrderTransactionLock>();
		for (int cnt = 0; cnt < num_trans; cnt++) {
			NewOrderTransactionLock transaction = new NewOrderTransactionLock(cnt, wh.getId(), db, lockm);
			transactions.add(transaction) ;
		}
		schedule.setTransactions(transactions) ;
		System.out.println("Initialized: Transactions");
		System.out.println("=========================");
		schedule.run() ;
		
		
		// The code bellow is used to test the read/write transactions
		/* On vérifie si ça marche pas trop mal */
		/*
		Database db = new Database() ;
		System.out.println("DB " + db);
		
		Object obj0 = db.getObject(31, Types.WAREHOUSE) ;
		System.out.println("DB "+obj0);
		
		// Tente de read le wh de hash 31"
		Read readc = new Read(0, db, 31, Types.WAREHOUSE) ;
		System.out.println("OP " + readc.getId());
		System.out.println("OP " + readc.getDb());
		System.out.println("OP " + readc.get_has_applied());
		
		Object obj = readc.apply() ;
		System.out.println("11 : " + obj);
		System.out.println(readc.get_has_applied());
		
		// Tente de read le district de hash 31
		Read readw = new Read(0, db, 32, Types.WAREHOUSE) ;
		Object objw = readw.apply() ;
		System.out.println(readw.get_has_applied());
		
		// Tente de read le district de hash 31
		Read readd = new Read(0, db, 31, Types.DISTRICT) ;
		Object objd = readd.apply() ;
		System.out.println(readd.get_has_applied());
		
		// Tente d'update le warehouse
		((Warehouse) obj).setStreet1("AAA") ;
		
		// Tente de relire le wh de hash 31"
		Read readcc = new Read(0, db, 31, Types.WAREHOUSE) ;
		
		Object objj = readcc.apply() ;
		System.out.println("11 : " + objj);
		
		*/
	}
	
	/* Code of python transaction */
	/*
	public void run_transactions(int max_iterations,List<Transaction> T )
	{ 
		int cu_iter = 0;
		int number_T = T.size();
		while((T.size() != 0) && (cu_iter < max_iterations))
		{
			int i = (int)(Math.random()*(number_T+1));
			Transaction current_t = T.get(i);
			current_t.apply_next();
			if (current_t.get_finished())
			{
				T.remove(i);
			}
				
				
			
		}
	}
	*/
}
