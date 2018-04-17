import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Stack;
//add pointer to prev log entry

public class CC
{
	//Execute all given transactions, using locking.
	//Each element in the transactions List represents all operations performed by one transaction, in order.
	//No operation in a transaction can be executed out of order, but operations may be interleaved with other transactions if allowed by the locking protocol.
	//The index of the transaction in the list is equivalent to the transaction ID.
	//Print the log to either the console or a file at the end of the method. Return the new db state after executing the transactions.

	//i is start point of dfs
	private static List<Integer> dfs (int i, List<Boolean> visited, List<HashSet<Integer>> wait_for_graph ) {
		Stack<Integer> stack = new Stack<Integer>();
		List<Integer> previous = new ArrayList<Integer>();
		for (Object o : wait_for_graph) {
			previous.add(null);
		}

		stack.push(i);
		while (!stack.empty()) {
			int curr = stack.pop();
			visited.set(curr, true);

			for ( int child : wait_for_graph.get(curr)) {
				if (! visited.get(child) ) {
					stack.push(child);
					previous.set(child, curr);
				} else {
					List<Integer> cycle = new ArrayList<Integer>(); 
					int node = curr;
					while (node != child) {
						cycle.add(node);
						node = previous.get(node);
					}
					cycle.add(child);
					return cycle;
				}
			}

		}
		return null;
	}



	private static Integer otherTransactionsContainLock( List<ArrayList<String>> transaction_locks, String lock, int current ) {
		for (int i = 0 ; i < transaction_locks.size(); i++ ) {
			if ( i == current) {
				continue;
			}

			if ( transaction_locks.get(i).contains(lock) ) {
				return i;
			} 
		}
		return null; 
	}

	private static List<Integer> cycleExists ( List<HashSet<Integer>> wait_for_graph ) {
		

		for (int i = 0; i <wait_for_graph.size(); i++) {
			List<Boolean> visited = new ArrayList<Boolean>();
			for ( int j = 0; j < wait_for_graph.size(); j++) {
				visited.add(false);
			}

			List<Integer> result = dfs(i, visited, wait_for_graph);
			if (result != null) {
				return result;
			} 

		}

		return null;
	}

	public static int[] executeSchedule(int[] db, List<String> transactions)
	{

		List<String> log = new ArrayList<String>();

		List<ArrayList<String>> transaction_list = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> transaction_locks = new ArrayList<ArrayList<String>>();
		List<Integer> pointers = new ArrayList<Integer>();

		List<Integer> previous_timestamp = new ArrayList<Integer>();

		List<HashSet<Integer>> wait_for_graph = new ArrayList<HashSet<Integer>>();


		for ( String str : transactions ){
			transaction_list.add(new ArrayList<String>(Arrays.asList(str.split(";"))));
			transaction_locks.add(new ArrayList<String>() );
			pointers.add(0);
			previous_timestamp.add(-1);
			wait_for_graph.add( new HashSet<Integer>() );
			
		}



		boolean completed = false; 
		int timestamp = 0 ;
		

		while ( !completed ) {

			
			completed = true;
			for (int i = 0; i < transactions.size(); i++) {
				if ( pointers.get(i) < transaction_list.get(i).size() ) {
					completed = false;
					break;
				}
			}

			if ( completed ) {
				break;
			}

			//deadlock handling. repeating process to check if deadlock exists by using a while loop

			while(true) {
				List<Integer> result = cycleExists(wait_for_graph);
				if (result == null) {
					break;
				}
				//we have a deadlock
				int max = -1; 
				for (int j = 0; j < result.size(); j++) {
					if (result.get(j ) > max) {
						max = result.get(j);
					}
				}

				//rollback changes
				for (int s = log.size() - 1 ; s >= 0; s--) {
					String entry = log.get(s);
					//For each Write entry encountered, restore the state of the database before that operation 
					if (entry.charAt(0) != 'W') {
						continue;
					}

					String [] parsed = entry.split(",");

					if ( parsed[1].equals( "T" + (max+1) ) ) {
						int record = Integer.parseInt(parsed[2]);
						int old_value = Integer.parseInt(parsed[3]);
						db[old_value] = record;
					}
				}

				wait_for_graph.get(max).clear();

				//remove the aborted transaction from the wait-for graph
				for (int j = 0; j < wait_for_graph.size(); j++ ) {
					//System.out.println("transaction being removed" + wait_for_graph.get(j) );
					wait_for_graph.get(j).remove(max);
					//transaction_list.get(j).remove(max);


				}

				transaction_locks.get(max).clear();
				pointers.set(max, 0);
				
				log.add("A:" + timestamp + "," + "T" + (max+1) + "," + previous_timestamp.get(max) );
				previous_timestamp.set(max, timestamp);
				timestamp++;
			}

		
			//part 1 (round robin or read, write , commit ) and part 2 (syslog)
			for (int i = 0 ; i < transactions.size(); i++) {

				if ( pointers.get(i) < transaction_list.get(i).size() ) {

					List<String> transaction = transaction_list.get(i);
					int ptr = pointers.get(i);

						
					if ( transaction.get(ptr).charAt(0) == 'R') {
						
						String s = Character.toString( transaction.get(ptr).charAt(2) );
						String sharedLock = "S(" + s + ")" ; 
						String exclusiveLock = "X(" + s + ")" ; 

						//check if any other has an exclusive lock on this\
						Integer node = otherTransactionsContainLock ( transaction_locks, exclusiveLock, i ) ;
						if ( node != null ) {
							wait_for_graph.get(i).add( node);
							
							continue;
						}
						
						//acquired a lock if we dont already have one
						if (  !transaction_locks.get(i).contains(sharedLock) && !transaction_locks.get(i).contains(exclusiveLock) ) {
							transaction_locks.get(i).add(sharedLock);
						}

						//processed transaction here
						        //method 1
						int transaction_number = i + 1;
						int db_index = Character.getNumericValue(transaction.get(ptr).charAt(2) ) ;
						int value_read = db[db_index];
        				log.add("R:" + timestamp + ",T" + transaction_number + "," + transaction.get(ptr).charAt(2) + "," + value_read + "," + previous_timestamp.get(i));
        				timestamp++;
        				previous_timestamp.set( i, timestamp - 1);


					}

					
					else if ( transaction.get(ptr).charAt(0) == 'W') {

						
						
						String s = Character.toString( transaction.get(ptr).charAt(2) );
						String sharedLock = "S(" + s + ")" ; 
						String exclusiveLock = "X(" + s + ")" ; 

						//no other transaction contains an exclusive lock or shared lock on our record

						Integer node1 = otherTransactionsContainLock ( transaction_locks, exclusiveLock, i ) ;
						Integer node2 = otherTransactionsContainLock ( transaction_locks, sharedLock, i ) ;

						if ( node1 != null || node2 != null) {

							if (node1 != null) {
								wait_for_graph.get(i).add( node1);
								
							}
							if (node2 != null) {
								wait_for_graph.get(i).add( node2);
								
							}
							continue;
						}



						//upgrade our lock to exclusive lock if we have a shared lock
						if ( transaction_locks.get(i).contains(sharedLock)  ) {
							int index = transaction_locks.get(i).indexOf(sharedLock);
							transaction_locks.get(i).set(index, exclusiveLock);
						}

						//if we dont have an exclusive lock, acquire an exclusive lock
						if (! transaction_locks.get(i).contains(exclusiveLock)) {
							transaction_locks.get(i).add(exclusiveLock);
						}

						//process the transaction
						int db_index = Character.getNumericValue(transaction.get(ptr).charAt(2) ) ;
						int old_value = db[db_index] ;
						db[db_index] = Character.getNumericValue( transaction.get(ptr).charAt(4) ); //new value
						        //method 1

						int transaction_number = i + 1;
       		 			
       		 			log.add("W:" + timestamp + ",T" + transaction_number + "," + transaction.get(ptr).charAt(2) + "," + old_value + "," + db[db_index] + "," + previous_timestamp.get(i));
       		 			timestamp++;
       		 			previous_timestamp.set( i, timestamp - 1);


					}

					else if ( transaction.get(ptr).charAt(0) == 'C') {
						
						//clear all locks
						transaction_locks.get(i).clear();

						wait_for_graph.get(i).clear();

						for (int j = 0; j < wait_for_graph.size(); j++ ) {
							wait_for_graph.get(j).remove(i);
						}
						        //method 1
						int transaction_number = i + 1;
					
						log.add( "C:" + timestamp + ",T" + transaction_number + "," + previous_timestamp.get(i) );
        				timestamp++;
        				previous_timestamp.set( i, timestamp - 1);
					}

					pointers.set( i,  pointers.get(i) + 1);
				}
				
			}
		
		}

		for ( String s : log ) {
			System.out.println(s);
		}

		return db;

	}
}