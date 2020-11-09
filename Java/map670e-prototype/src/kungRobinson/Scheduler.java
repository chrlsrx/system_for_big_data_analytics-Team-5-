package kungRobinson;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Scheduler {
	private final int num_workers;
	private Vector<Transaction> transactions;
	private Vector<Transaction> transactions_aborted;
	private int cnt_aborts;

	public Scheduler(final int num_workers) {
		this.num_workers = num_workers;
		this.transactions = new Vector<Transaction>();
		this.transactions_aborted = new Vector<Transaction>();
		this.cnt_aborts = 0;
	}

	public Vector<Transaction> getTransactions() {
		return this.transactions;
	}

	public synchronized void setTransactions(Vector<Transaction> transactions) {
		this.transactions = transactions;
		this.cnt_aborts = 0;
	}
	
	public int getAborts() {
		return this.cnt_aborts;
	}

	public void run() throws InterruptedException {

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.num_workers);

		int random_index = 0;
		Random rd = new Random();

		while (this.transactions.size() > 0) {
			random_index = rd.nextInt(this.transactions.size());
			Transaction transaction = transactions.get(random_index);
			if (transaction.is_finished()) {
				System.out.println("-- Remove transaction: " + random_index);
				transactions.remove(transaction);
				cnt_aborts += transaction.get_nb_restarts();
			}
			executor.submit(transaction);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}

}
