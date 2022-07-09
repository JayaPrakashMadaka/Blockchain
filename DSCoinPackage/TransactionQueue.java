package DSCoinPackage;

// import DSCoinPackage.Transaction;
// import DSCoinPackage.EmptyQueueException;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
	if(numTransactions==0){
		firstTransaction=transaction;
		lastTransaction=transaction;
		numTransactions+=1;
	}
	else{
		lastTransaction.next=transaction;
		lastTransaction=transaction;
		numTransactions+=1;
	}
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
	Transaction ans=new Transaction();
	if(numTransactions==0){
		throw new EmptyQueueException();
	}
	else{
		ans= firstTransaction;
		firstTransaction=firstTransaction.next;
		numTransactions-=1;
	}
	return ans;
  }

  public int size() {
	return numTransactions;
  }
}
