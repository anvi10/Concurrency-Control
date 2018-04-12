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

		maxSize = Math.max( Math.max(t1_size, t2_size) , t3_size);

		int ptr = 0;
		while ( ptr < maxSize ) {

			if (ptr < t1_size) {

				if ( t1.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t1.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t2_locks.contains(exclusiveLock) && !t3_locks.contains(exclusiveLock) && !t1_locks.contains(sharedLock) && !t1_locks.contains(exclusiveLock) ) {
						t1_locks.add(sharedLock);
					}

				}

				if ( t1.get(ptr).charAt(0) == 'C') {
					t1_locks.clear();
				}

				System.out.println( t1.get(ptr));
			}

			if (ptr < t2_size) {

				if ( t2.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t2.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t1_locks.contains(exclusiveLock) && !t3_locks.contains(exclusiveLock) && !t2_locks.contains(sharedLock) && !t2_locks.contains(exclusiveLock)) {
						t2_locks.add(sharedLock);
					}

				}

				if ( t2.get(ptr).charAt(0) == 'C') {
					t2_locks.clear();
				}

				System.out.println( t2.get(ptr));
			}

			if (ptr < t3_size) {

				if ( t3.get(ptr).charAt(0) == 'R') {
					String s = Character.toString( t3.get(ptr).charAt(2) );
					String sharedLock = "S(" + s + ")" ; 
					String exclusiveLock = "X(" + s + ")" ; 
					
					if (  !t1_locks.contains(exclusiveLock) && !t2_locks.contains(exclusiveLock) && !t3_locks.contains(sharedLock) && !t3_locks.contains(exclusiveLock)) {
						t3_locks.add(sharedLock);
					}

				}

				if ( t3.get(ptr).charAt(0) == 'C') {
					t3_locks.clear();
				}

				System.out.println( t3.get(ptr));
			}

		ptr++;
		}

		System.out.println(t1_locks);
		System.out.println(t2_locks);
		System.out.println(t3_locks);

		return null;

	}
}