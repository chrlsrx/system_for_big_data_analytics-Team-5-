package map670e;
import java.util.Vector;

import database.Database;
import database.Types;
import database.Warehouse;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		long startTime = System.nanoTime();
		
		Database db = new Database() ;
		System.out.println("Initialized: Database");
		
		LockManager lockm = new LockManager() ;
		System.out.println("Initialized: Lockmanager");
		
		int num_workers =  6 ;
		Scheduler schedule = new Scheduler(num_workers);
		System.out.println("Initialized: Scheduler, workers:" + num_workers);
		
		// The only warehouse should have a hash of 31 : retrieve it
		Warehouse wh = (Warehouse) db.getObject(31, Types.WAREHOUSE) ;
		int wh_hash = wh.hashCode() ;
		System.out.println("Id of the unique warehouse: " + wh_hash);
		

		int num_trans = 100000;
		Vector<NewOrderTransactionLock> transactions  = new Vector<NewOrderTransactionLock>();
		for (int cnt = 0; cnt < num_trans; cnt++) {
			DataGeneration data = new DataGeneration(wh_hash) ;
			NewOrderTransactionLock transaction = new NewOrderTransactionLock(cnt, wh.getId(), db, lockm, schedule, data);
			transactions.add(transaction) ;
		}

		schedule.setTransactions(transactions) ;
		
		long elapsedTime = (System.nanoTime() - startTime) ;
		System.out.println("Initialization duration: " + elapsedTime/1000000 + " ms");
		startTime = System.nanoTime();
		
		System.out.println("\n" + "\n" + "\n");
		System.out.println("Initialized: Transactions (" + num_trans + ")");
		System.out.println("=========================");
		schedule.run() ;
		System.out.println(lockm);
		lockm.show() ;
		
		elapsedTime = (System.nanoTime() - startTime) ;
		System.out.println("All transactions duration: " + elapsedTime/1000000 + " ms");
		System.out.println("Single transaction duration: " + elapsedTime/num_trans/1000 + " µs");
		startTime = System.nanoTime();
		
		System.out.println("\n" + "\n" + "\n");
		schedule.retry_aborts() ;
		lockm.reset();
		schedule.run() ;
		System.out.println(lockm);
		lockm.show() ;
		
		elapsedTime = (System.nanoTime() - startTime) ;
		System.out.println("All transactions duration: " + elapsedTime/1000000 + " ms");
		System.out.println("Single transaction duration: " + elapsedTime/num_trans/1000 + " µs");
		
	}
}
