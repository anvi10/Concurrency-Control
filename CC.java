import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.sql.Timestamp;

public class CC
{
	//Execute all given transactions, using locking.
	//Each element in the transactions List represents all operations performed by one transaction, in order.
	//No operation in a transaction can be executed out of order, but operations may be interleaved with other transactions if allowed by the locking protocol.
	//The index of the transaction in the list is equivalent to the transaction ID.
	//Print the log to either the console or a file at the end of the method. Return the new db state after executing the transactions.

	private static boolean otherTransactionsContainLock( List<ArrayList<String>> transaction_locks, String lock, int current ) {
		for (int i = 0 ; i < transaction_locks.size(); i++ ) {
			if ( i == current) {
				continue;
			}

			if ( transaction_locks.get(i).contains(lock) ) {
				return true;
			} 
		}
		return false; 
	}


	public static int[] executeSchedule(int[] db, List<String> transactions)
	{


		List<ArrayList<String>> transaction_list = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> transaction_locks = new ArrayList<ArrayList<String>>();
		List<Integer> pointers = new ArrayList<Integer>();

		for ( String str : transactions ){
			transaction_list.add(new ArrayList<String>(Arrays.asList(str.split(";"))));
			transaction_locks.add(new ArrayList<String>() );
			pointers.add(0);
		}


//while none of the commits are done
//have 3 separate ptrs for t1 and t2 and t3

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

			
			for (int i = 0 ; i < transactions.size(); i++) {


				if ( pointers.get(i) < transaction_list.get(i).size() ) {

					List<String> transaction = transaction_list.get(i);
					int ptr = pointers.get(i);

						
					if ( transaction.get(ptr).charAt(0) == 'R') {
						
						String s = Character.toString( transaction.get(ptr).charAt(2) );
						String sharedLock = "S(" + s + ")" ; 
						String exclusiveLock = "X(" + s + ")" ; 

						//check if any other has an exclusive lock on this
						if ( otherTransactionsContainLock ( transaction_locks, exclusiveLock, i ) ) {
							continue;
						}
						
						//acquired a lock if we dont already have one
						if (  !transaction_locks.get(i).contains(sharedLock) && !transaction_locks.get(i).contains(exclusiveLock) ) {
							transaction_locks.get(i).add(sharedLock);
						}

						//processed transaction here
						        //method 1
						int transaction_number = i + 1;
        				System.out.println( "R:" + timestamp + ",T" + transaction_number) ;
        				timestamp++;


					}

					
					else if ( transaction.get(ptr).charAt(0) == 'W') {
						
						String s = Character.toString( transaction.get(ptr).charAt(2) );
						String sharedLock = "S(" + s + ")" ; 
						String exclusiveLock = "X(" + s + ")" ; 

						//no other transaction contains an exclusive lock or shared lock on our record
						if (  otherTransactionsContainLock(transaction_locks, exclusiveLock,i) || otherTransactionsContainLock(transaction_locks, sharedLock, i) ) {
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
						db[db_index] = Character.getNumericValue( transaction.get(ptr).charAt(4) );
						        //method 1

						int transaction_number = i + 1;
       		 			System.out.println( "W:" + timestamp + ",T" + transaction_number) ;
       		 			timestamp++;


					}

					else if ( transaction.get(ptr).charAt(0) == 'C') {
						
						//clear all locks
						transaction_locks.get(i).clear();
						        //method 1
						int transaction_number = i + 1;
						System.out.println( "C:" + timestamp + ",T" + transaction_number) ;
        				timestamp++;
					}

					pointers.set( i,  pointers.get(i) + 1);
				}
				
			}
		
		}

		return db;

	}
}