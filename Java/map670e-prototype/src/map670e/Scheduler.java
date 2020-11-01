package map670e;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Scheduler {
	private final int num_workers ;
	private ArrayList<NewOrderTransactionLock> transactions ;
	private ArrayList<NewOrderTransactionLock> transactions_aborted ;

	public Scheduler(final int num_workers) {
		this.num_workers = num_workers ;
		this.transactions = new ArrayList<NewOrderTransactionLock>() ;
		this.transactions_aborted = new ArrayList<NewOrderTransactionLock>() ;
	}
	
	public void setTransactions(ArrayList<NewOrderTransactionLock> transactions) {
		this.transactions = transactions ;
	}
	
	public void retry_aborts() {
		System.out.println("Retrying " + this.transactions_aborted.size() + " transactions.");
		this.transactions = this.transactions_aborted ;
	}
	
	public void run() throws InterruptedException {

	    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.num_workers);

	    while (this.transactions.size() > 0 ) {
	    	NewOrderTransactionLock transaction = transactions.get(0) ;
	    	transactions.remove(0) ;
	    	executor.execute(transaction);
	    }
	    executor.shutdown();
	    while (!executor.isTerminated()) { }
	    
	    System.out.println("===============================================");
        System.out.println("Each transaction has been completed or aborted.");
        System.out.println("===============================================");
	}
	
}
