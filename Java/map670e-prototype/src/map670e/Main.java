package map670e;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
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

}
