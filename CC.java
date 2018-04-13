import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

public class CC
{
	//Execute all given transactions, using locking.
	//Each element in the transactions List represents all operations performed by one transaction, in order.
	//No operation in a transaction can be executed out of order, but operations may be interleaved with other transactions if allowed by the locking protocol.
	//The index of the transaction in the list is equivalent to the transaction ID.
	//Print the log to either the console or a file at the end of the method. Return the new db state after executing the transactions.
	public static int[] executeSchedule(int[] db, List<String> transactions)
	{
		for (int i = 0; i < db.length; i++) {
			System.out.println(db[i]);
		}

		List<List<String>> transaction_list = new ArrayList<ArrayList<String>();
		List<List<String>> transaction_locks = new ArrayList<ArrayList<String>();
		List<Integer> pointers = new ArrayList<Integer>();

		for ( String str : transactions ){
			List<String> transaction = new ArrayList<String>(Arrays.asList(str.split(";")));
			transaction_list.add(transaction);
			transaction_locks.add(new ArrayList<String>() );
			pointers.add(0);
		}

/*
		List<String> t1 = new ArrayList<String>(Arrays.asList(transactions.get(0).split(";")));
		List<String> t2 = new ArrayList<String>(Arrays.asList(transactions.get(1).split(";")));
		List<String> t3 = new ArrayList<String>();

		List<String> t1_locks = new ArrayList<String>();
		List<String> t2_locks = new ArrayList<String>();
		List<String> t3_locks = new ArrayList<String>();

		if (transactions.size() == 3) {
			t3 = new ArrayList<String>(Arrays.asList(transactions.get(2).split(";")));
		}


		int maxSize = -1;

		int t1_size = t1.size();
		int t2_size = t2.size();
		int t3_size = t3.size();

*/
		maxSize = Math.max( Math.max(t1_size, t2_size) , t3_size);

//while none of the commits are done
//have 3 separate ptrs for t1 and t2 and t3

		int ptr = 0;

		while ( ptr < maxSize) {

			if (ptr < t1_size) {

				if ( t1.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t1.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t2_locks.contains(exclusiveLock) && !t3_locks.contains(exclusiveLock) && !t1_locks.contains(sharedLock) && !t1_locks.contains(exclusiveLock) ) {
						t1_locks.add(sharedLock);
						System.out.println( t1.get(ptr));
					}

				}

				//try the write method here and copy this on to t2 and t3
				if ( t1.get(ptr).charAt(0) == 'W') {
					//If the current transaction already has a Shared Lock (and no other transaction has any other locks) 
					//you will upgrade the Shared Lock to an Exclusive lock.

					String s = Character.toString( t1.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 

					if (t1_locks.contains(sharedLock)) {
						//upgrade the shared lock to an exclusive lock
						int index = t1_locks.indexOf(sharedLock);
						t1_locks.set(index, exclusiveLock);
						System.out.println( t1.get(ptr));
					}

					//When a write operation is encountered, attempt to acquire an 
					//Exclusive Lock on that record. An Exclusive Lock can be acquired 
					//only if there is no other lock on that record from another transaction. 
					if ( !t2_locks.contains(sharedLock)  && !t2_locks.contains(exclusiveLock) && !t3_locks.contains(sharedLock) && !t3_locks.contains(exclusiveLock) ) {
						t1_locks.add(exclusiveLock);
						System.out.println( t1.get(ptr));
					}

				}

				if ( t1.get(ptr).charAt(0) == 'C') {
					t1_locks.clear();
				}

			}

			if (ptr < t2_size) {

				if ( t2.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t2.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t1_locks.contains(exclusiveLock) && !t3_locks.contains(exclusiveLock) && !t2_locks.contains(sharedLock) && !t2_locks.contains(exclusiveLock)) {
						t2_locks.add(sharedLock);
						System.out.println( t2.get(ptr));
					}

				}

				//try the write method here and copy this on to t2 and t3
				if ( t2.get(ptr).charAt(0) == 'W') {
					//If the current transaction already has a Shared Lock (and no other transaction has any other locks) 
					//you will upgrade the Shared Lock to an Exclusive lock.

					String s = Character.toString( t2.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 

					if (t2_locks.contains(sharedLock)) {
						//upgrade the shared lock to an exclusive lock
						int index = t2_locks.indexOf(sharedLock);
						t2_locks.set(index, exclusiveLock);
						System.out.println( t2.get(ptr));
					}

					//When a write operation is encountered, attempt to acquire an 
					//Exclusive Lock on that record. An Exclusive Lock can be acquired 
					//only if there is no other lock on that record from another transaction. 
					if ( !t1_locks.contains(sharedLock)  && !t1_locks.contains(exclusiveLock) && !t3_locks.contains(sharedLock) && !t3_locks.contains(exclusiveLock) ) {
						t2_locks.add(exclusiveLock);
						System.out.println( t2.get(ptr));
					}

				}

				if ( t2.get(ptr).charAt(0) == 'C') {
					t2_locks.clear();
				}

				
			}

			if (ptr < t3_size) {

				if ( t3.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t3.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t1_locks.contains(exclusiveLock) && !t2_locks.contains(exclusiveLock) && !t3_locks.contains(sharedLock) && !t3_locks.contains(exclusiveLock)) {
						t3_locks.add(sharedLock);
						System.out.println( t3.get(ptr));
					}

				}

				//try the write method here and copy this on to t2 and t3
				if ( t3.get(ptr).charAt(0) == 'W') {
					//If the current transaction already has a Shared Lock (and no other transaction has any other locks) 
					//you will upgrade the Shared Lock to an Exclusive lock.

					String s = Character.toString( t1.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 

					if (t3_locks.contains(sharedLock)) {
						//upgrade the shared lock to an exclusive lock
						int index = t3_locks.indexOf(sharedLock);
						t3_locks.set(index, exclusiveLock);
						System.out.println( t3.get(ptr));
					}

					//When a write operation is encountered, attempt to acquire an 
					//Exclusive Lock on that record. An Exclusive Lock can be acquired 
					//only if there is no other lock on that record from another transaction. 
					if ( !t1_locks.contains(sharedLock)  && !t1_locks.contains(exclusiveLock) && !t2_locks.contains(sharedLock) && !t2_locks.contains(exclusiveLock) ) {
						t3_locks.add(exclusiveLock);
						System.out.println( t3.get(ptr));
					}

				}

				if ( t3.get(ptr).charAt(0) == 'C') {
					t3_locks.clear();
				}

			
			}

		ptr++;

		}

		System.out.println(t1_locks);
		System.out.println(t2_locks);
		System.out.println(t3_locks);

		return null;

	}
}