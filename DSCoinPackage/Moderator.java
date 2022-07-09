package DSCoinPackage;
import HelperClasses.*;
import java.util.*;
public class Moderator{

  public static String add(String s){
	if(s.compareTo("")==0){
		return "";
	}
	else if(s.substring(s.length()-1,s.length()).compareTo("9")==0){
		return add(s.substring(0,s.length()-1))+"0";
	}
	else{
		int x=Integer.parseInt(s.substring(s.length()-1,s.length()))+1;
		return s.substring(0,s.length()-1)+x;
	}
  } 
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
	int count=DSObj.memberlist.length;
	for(int i=0;i<DSObj.memberlist.length;i++){
		DSObj.memberlist[i].mycoins=new ArrayList<Pair<String,TransactionBlock>>();
		DSObj.memberlist[i].in_process_trans = new Transaction[100];
	}
	Members m=new Members();
	m.UID="Moderator";
	String c="100000";
	String lc="100000";
	for(int i=0;i<coinCount;i++){
		Transaction t=new Transaction();
		t.coinID=c;
		t.Source=m;
		t.Destination=DSObj.memberlist[i%count];
		t.coinsrc_block=null;
		DSObj.pendingTransactions.AddTransactions(t);
		lc=c;
		c=add(c);
	}
	DSObj.latestCoinID=lc;
	int j=coinCount/DSObj.bChain.tr_count;
	try{
	for(int i=0;i<j;i++){
		Transaction[] array=new Transaction[DSObj.bChain.tr_count];
		for(int k=0;k<DSObj.bChain.tr_count;k++){
			Transaction t=DSObj.pendingTransactions.RemoveTransaction();
			array[k]=t;
		}
		TransactionBlock B=new TransactionBlock(array);
		DSObj.bChain.InsertBlock_Honest(B);
		for(int l=0;l<DSObj.bChain.tr_count;l++){
			Pair<String, TransactionBlock> p= new Pair<String, TransactionBlock>(array[l].coinID,B);
			array[l].Destination.mycoins.add(p);
		}
	}
	}catch(Exception e){}
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount){
	int count=DSObj.memberlist.length;
	for(int i=0;i<DSObj.memberlist.length;i++){
		DSObj.memberlist[i].mycoins=new ArrayList<Pair<String,TransactionBlock>>();
		DSObj.memberlist[i].in_process_trans = new Transaction[100];
	}
	Members m=new Members();
	m.UID="Moderator";
	String c="100000";
	String lc="100000";
	for(int i=0;i<coinCount;i++){
		Transaction t=new Transaction();
		t.coinID=c;
		t.Source=m;
		t.Destination=DSObj.memberlist[i%count];
		t.coinsrc_block=null;
		DSObj.pendingTransactions.AddTransactions(t);
		lc=c;
		c=add(c);
	}
	DSObj.latestCoinID=lc;
	int j=coinCount/DSObj.bChain.tr_count;
	try{
	for(int i=0;i<j;i++){
		Transaction[] array=new Transaction[DSObj.bChain.tr_count];
		for(int k=0;k<DSObj.bChain.tr_count;k++){
			Transaction t=DSObj.pendingTransactions.RemoveTransaction();
			array[k]=t;
		}
		TransactionBlock B=new TransactionBlock(array);
		DSObj.bChain.InsertBlock_Malicious(B);
		for(int l=0;l<DSObj.bChain.tr_count;l++){
			Pair<String, TransactionBlock> p= new Pair<String, TransactionBlock>(array[l].coinID,B);
			array[l].Destination.mycoins.add(p);
		}
	}
	}catch(Exception e){}
  }
}
