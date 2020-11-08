package kungRobinson;

import database.Types;

import java.util.ArrayList;
import java.util.Random;

import database.Customer;
import database.Database;


public class Simulation {

		
	public static void main(String[] args) {

		 
		int w_id=0;
		Database db= new Database();
		// We populate an array with 100 transactions
		// error 1 : validation phase doesn't work with 2 or more transactions
		int n_transactions = 32;
		ArrayList<Transaction_optimistic_cc> all_transactions = new ArrayList<Transaction_optimistic_cc>();
		for (int i=0;i<n_transactions;i++) {

			all_transactions.add(new Transaction_optimistic_cc(i,w_id, db));
		}
		
		int random_index = 0;
		Random rd = new Random();
		Transaction_optimistic_cc curr_transaction;
		ArrayList<Transaction_optimistic_cc> other_transactions;
		
		while (all_transactions.size() > 0) {
			random_index = rd.nextInt(all_transactions.size());
			System.out.println("-- Run transaction: " + random_index);
			curr_transaction = all_transactions.get(random_index);
			other_transactions = new ArrayList<Transaction_optimistic_cc>(all_transactions);
			other_transactions.remove(curr_transaction);
			curr_transaction.apply_next(other_transactions);
			if (curr_transaction.is_finished()) {
				System.out.println("-- Remove transaction: " + random_index);
				all_transactions.remove(curr_transaction);
			}
		}
		


						

	}

}
