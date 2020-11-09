package kungRobinson;

import database.Types;
import java.util.Vector;
import java.util.Random;
import database.Customer;
import database.Database;

public class Simulation {

	public static void main(String[] args) throws InterruptedException {
		// Parameters of the test run
		int n_transactions = 5000;
		int n_workers = 8;
		String algo_name = "optimistic_cc"; // snapshot | optimistic_cc

		Database db = new Database();
		Scheduler scheduler = new Scheduler(n_workers);
		Vector<Transaction> all_transactions = new Vector<Transaction>();
		for (int i = 0; i < n_transactions; i++) {
			if (algo_name == "optimistic_cc") {
				all_transactions.add(new Transaction_optimistic_cc(i, 0, db, scheduler));
			} else if (algo_name == "snapshot") {
				all_transactions.add(new Transaction_snapshot(i, 0, db, scheduler));
			}
		}
		scheduler.setTransactions(all_transactions) ;
		double start_time = System.currentTimeMillis();
		scheduler.run(); //do the magic
		double end_time = System.currentTimeMillis();
		// Display resultsF
		double execution_time = end_time - start_time;
		System.out.println("#######################");
		System.out.println(
				n_transactions + " transactions executed in " + execution_time / 1000 + " s with " + algo_name);
		System.out.println("Total nb restarts : " + scheduler.getAborts());
	}

}
