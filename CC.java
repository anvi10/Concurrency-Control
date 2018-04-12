import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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

		if (transactions.size() == 3) {
			t3 = new ArrayList<String>(Arrays.asList(transactions.get(2).split(";")));
		}

		System.out.println( "t1 " + t1);
		System.out.println( "t2 " + t2);
		System.out.println( "t3 " + t3);

		return null;

	}
}