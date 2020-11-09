package kungRobinson;

import database.Types;

import java.util.ArrayList;
import java.util.Random;

import database.Customer;
import database.Database;

public class Simulation_Single_thread {

	public static int run_optimistic_cc(Database db, int n_transactions, boolean random_order_execution) {
		ArrayList<Transaction_optimistic_cc_Single_thread> all_transactions = new ArrayList<Transaction_optimistic_cc_Single_thread>();
		for (int i = 0; i < n_transactions; i++) {
			all_transactions.add(new Transaction_optimistic_cc_Single_thread(i, 0, db));
		}

		int random_index = 0;
		Random rd = new Random();
		Transaction_optimistic_cc_Single_thread curr_transaction;
		ArrayList<Transaction_optimistic_cc_Single_thread> other_transactions;
		int total_nb_restarts = 0;

		while (all_transactions.size() > 0) {
			if (random_order_execution) {
				// Select a transaction at random for execution
				random_index = rd.nextInt(all_transactions.size());
			} else {
				// Select the next transaction available for execution. No randomness.
				if (random_index >= all_transactions.size()) {
					random_index = 0;
				}
			}
			System.out.println("-- Run transaction: " + random_index);
			curr_transaction = all_transactions.get(random_index);
			other_transactions = new ArrayList<Transaction_optimistic_cc_Single_thread>(all_transactions);
			other_transactions.remove(curr_transaction);
			curr_transaction.apply_next(other_transactions);
			if (curr_transaction.is_finished()) {
				System.out.println("-- Remove transaction: " + random_index);
				all_transactions.remove(curr_transaction);
				total_nb_restarts += curr_transaction.get_nb_restarts();
			}
			if (!random_order_execution) {
				// Go to the next transaction.
				random_index++;
			}
		}
		
		return total_nb_restarts;
	}

	public static int run_snapshot(Database db, int n_transactions, boolean random_order_execution) {
		ArrayList<Transaction_snapshot_Single_thread> all_transactions = new ArrayList<Transaction_snapshot_Single_thread>();
		for (int i = 0; i < n_transactions; i++) {
			all_transactions.add(new Transaction_snapshot_Single_thread(i, 0, db));
		}

		int random_index = 0;
		Random rd = new Random();
		Transaction_snapshot_Single_thread curr_transaction;
		ArrayList<Transaction_snapshot_Single_thread> other_transactions;
		int total_nb_restarts = 0;

		while (all_transactions.size() > 0) {
			if (random_order_execution) {
				// Select a transaction at random for execution
				random_index = rd.nextInt(all_transactions.size());
			} else {
				// Select the next transaction available for execution. No randomness.
				if (random_index >= all_transactions.size()) {
					random_index = 0;
				}
			}
			System.out.println("-- Run transaction: " + random_index);
			curr_transaction = all_transactions.get(random_index);
			other_transactions = new ArrayList<Transaction_snapshot_Single_thread>(all_transactions);
			other_transactions.remove(curr_transaction);
			curr_transaction.apply_next(other_transactions);
			if (curr_transaction.is_finished()) {
				System.out.println("-- Remove transaction: " + random_index);
				total_nb_restarts += curr_transaction.get_nb_restarts();
				all_transactions.remove(curr_transaction);
			}
			if (!random_order_execution) {
				// Go to the next transaction.
				random_index++;
			}
		}
		
		return total_nb_restarts;

	}

	public static void main(String[] args) {
		// Parameters of the test run
		int n_transactions = 100;
		boolean random_order_execution = true;
		String algo_name = "optimistic_cc"; // snapshot | optimistic_cc

		Database db = new Database();
		double start_time = System.currentTimeMillis();
		int nb_restarts = 0;
		if (algo_name == "optimistic_cc") {
			nb_restarts = run_optimistic_cc(db, n_transactions, random_order_execution);
		} else if (algo_name == "snapshot") {
			nb_restarts = run_snapshot(db, n_transactions, random_order_execution);
		}
		double end_time = System.currentTimeMillis();
		// Add info about algo execution
		if (random_order_execution) {
			algo_name = algo_name + " (random access)";
		} else {
			algo_name = algo_name + " (deterministic access)";
		}
		// Display resultsF
		double execution_time = end_time - start_time;
		System.out.println("#######################");
		System.out.println(
				n_transactions + " transactions executed in " + execution_time / 1000 + " s with " + algo_name);
		System.out.println("Total nb restarts : " + nb_restarts);
	}

}
