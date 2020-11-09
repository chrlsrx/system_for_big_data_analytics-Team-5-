package map670e;

import java.util.Vector;

import database.Database;
import database.Types;
import database.Warehouse;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		Database db = new Database();
		System.out.println("Initialized: Database");

		LockManager lockm = new LockManager();
		System.out.println("Initialized: Lockmanager");

		int num_workers = 6;
		Scheduler schedule = new Scheduler(num_workers);
		System.out.println("Initialized: Scheduler, workers:" + num_workers);

		// The only warehouse should have a hash of 31 : retrieve it
		Warehouse wh = (Warehouse) db.getObject(31, Types.WAREHOUSE);
		int wh_hash = wh.hashCode();
		System.out.println("Id of the unique warehouse: " + wh_hash);

		int num_trans = 100000;
		Vector<NewOrderTransactionLock> transactions = new Vector<NewOrderTransactionLock>();
		for (int cnt = 0; cnt < num_trans; cnt++) {
			DataGeneration data = new DataGeneration(wh_hash);
			NewOrderTransactionLock transaction = new NewOrderTransactionLock(cnt, wh.getId(), db, lockm, schedule,
					data);
			transactions.add(transaction);
		}
		schedule.setTransactions(transactions);
		System.out.println("\n" + "\n" + "\n");
		System.out.println("Initialized: Transactions");
		System.out.println("=========================");
		schedule.run();
		System.out.println(lockm);
		lockm.show();

		System.out.println("\n" + "\n" + "\n");
		schedule.retry_aborts();
		lockm.reset();
		schedule.run();
		System.out.println(lockm);
		lockm.show();

	}
}
