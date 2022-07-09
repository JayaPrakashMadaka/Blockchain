package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

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
  public static List<Pair<String, TransactionBlock>> sort(List<Pair<String, TransactionBlock>> l){
	ArrayList<Pair<String, TransactionBlock>> ans =new ArrayList<Pair<String, TransactionBlock>>();
	int n=l.size();
	for(int i=0;i<n;i++){
		int min=0;
		for(int j=0;j<l.size();j++){
			if(Integer.parseInt(l.get(j).first)<Integer.parseInt(l.get(min).first)){
				min=j;
			}
		}
		ans.add(l.get(min));
		l.remove(min);
	}
	return ans;
  }
  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
	Transaction tobj = new Transaction();
	String cID=mycoins.get(0).first;
	TransactionBlock csrcBlock=mycoins.get(0).second;
	tobj.coinID=cID;
	tobj.Source=this;
	for(int i=0;i<DSobj.memberlist.length;i++){
		if(DSobj.memberlist[i].UID.compareTo(destUID)==0){
			tobj.Destination=DSobj.memberlist[i];
			break;
		}
	}
	tobj.coinsrc_block=csrcBlock;
	DSobj.pendingTransactions.AddTransactions(tobj);
	for(int i=0;i<in_process_trans.length;i++){
		if(in_process_trans[i]==null){
			in_process_trans[i]=tobj;
			break;
		}
	}
	mycoins.remove(0);

  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
	Transaction tobj = new Transaction();
	String cID=mycoins.get(0).first;
	TransactionBlock csrcBlock=mycoins.get(0).second;
	tobj.coinID=cID;
	tobj.Source=this;
	for(int i=0;i<DSobj.memberlist.length;i++){
		if(DSobj.memberlist[i].UID.compareTo(destUID)==0){
			tobj.Destination=DSobj.memberlist[i];
			break;
		}
	}
	tobj.coinsrc_block=csrcBlock;
	DSobj.pendingTransactions.AddTransactions(tobj);
	for(int i=0;i<in_process_trans.length;i++){
		if(in_process_trans[i]==null){
			in_process_trans[i]=tobj;
			break;
		}
	}
	mycoins.remove(0);

  }
  public static boolean check(Transaction t ,TransactionBlock B){
	for(int i=0;i<B.trarray.length;i++){
		if(B.trarray[i].coinID.equals(t.coinID)&&B.trarray[i].Source.UID.equals(t.Source.UID)&&B.trarray[i].Destination.UID.equals(t.Destination.UID)){
			return true;
		}
	}
	return false;
  }
  public static TransactionBlock findBlock(Transaction t, BlockChain_Honest BC){
	TransactionBlock B=BC.lastBlock;
	while(B!=null){
		if(check(t,B)==true){
			return B;
		}
	B=B.previous;
	}
	return B;
  }
  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
	
	CRF c=new CRF(64);
	ArrayList<Pair<String, String>> p1=new ArrayList<Pair<String, String>>();
	ArrayList<Pair<String, String>> p2=new ArrayList<Pair<String, String>>();
	TransactionBlock tB=findBlock(tobj,DSObj.bChain);
	if(tB==null){
		throw new MissingTransactionException();
	}
	else{
		int x=0;
		for(int i=0;i<tB.trarray.length;i++){
			if(tB.trarray[i].coinID.equals(tobj.coinID)&&tB.trarray[i].Source.UID.equals(tobj.Source.UID)&&tB.trarray[i].Destination.UID.equals(tobj.Destination.UID)){
				x=i+1;
			}
		}
		MerkleTree m=tB.Tree;
		int n=tB.trarray.length;
		TreeNode pointer=m.rootnode;
		Pair<String, String> q=new Pair<String, String>(pointer.val,null);
		p1.add(q);
		while(pointer.left!=null||pointer.right!=null){
			if(x<=n/2){
				Pair<String, String> q1=new Pair<String, String>(pointer.left.val,pointer.right.val);
				p1.add(q1);
				pointer=pointer.left;
				n=n/2;
			}
			else{
				Pair<String, String> q2=new Pair<String, String>(pointer.left.val,pointer.right.val);
				p1.add(q2);
				pointer=pointer.right;
				x=x-n/2;
				n=n/2;
			}
		}
		Collections.reverse(p1);
		TransactionBlock curr=DSObj.bChain.lastBlock;
		while(curr!=tB.previous){
			String s=curr.previous.dgst+"#"+curr.trsummary+"#"+curr.nonce;
			Pair<String, String> v1=new Pair<String, String>(curr.dgst,s);
			p2.add(v1);
			curr=curr.previous;
		}
		Pair<String, String> v=new Pair<String, String>(tB.previous.dgst,null);
		p2.add(v);
		Collections.reverse(p2);
	}
	Transaction[] newinprocesstrans=new Transaction[in_process_trans.length];
	int j=0;
	for(int i=0;i<in_process_trans.length;i++){
		if(in_process_trans[i]!=tobj){
			newinprocesstrans[j]=in_process_trans[i];
			j+=1;
		}
	}
	in_process_trans=newinprocesstrans;
	Pair<List<Pair<String, String>>, List<Pair<String, String>>> P = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(p1,p2);
	Pair<String,TransactionBlock> pair=new Pair<String,TransactionBlock>(tobj.coinID,tB);
	tobj.Destination.mycoins.add(pair);
	tobj.Destination.mycoins=sort(tobj.Destination.mycoins);
	return P;
  }
  
  
  public void MineCoin(DSCoin_Honest DSObj) {
	int n=DSObj.bChain.tr_count-1;
	Transaction[] array=new Transaction[n+1];
	int x=0;
	try{
	while(x<n){
		Transaction t=DSObj.pendingTransactions.RemoveTransaction();
		boolean found=false;
		for(int i=0;i<x;i++){
			if(t.coinID.compareTo(array[i].coinID)==0){
				found=true;
				break;
			}
		}
		TransactionBlock curr=t.coinsrc_block;
		boolean ans=false;
		if(t.coinsrc_block==null){
				ans=true;
		}
		else{
        		for(int i=0;i<curr.trarray.length;i++){
				if(t.coinID==curr.trarray[i].coinID && t.Source.UID==curr.trarray[i].Destination.UID){
					ans=true;
					break;
				}
			}
			TransactionBlock curr1=DSObj.bChain.lastBlock;
			while(curr1!=curr){
				for(int i=0;i<curr1.trarray.length;i++){
					if(t.coinID==curr1.trarray[i].coinID){
						ans= false;
						break;
					}
				}
			curr1=curr1.previous;
			}
		}
		if(found==false&&ans==true){
			array[x]=t;
			x++;
		}
	}
	}catch(Exception e){}
	Transaction mreward=new Transaction();
	String s=add(DSObj.latestCoinID);
	mreward.coinID=s;
	DSObj.latestCoinID=s;
	mreward.Source=null;
	mreward.Destination=this;
	mreward.coinsrc_block=null;
	array[n]=mreward;
	TransactionBlock Bnew=new TransactionBlock(array); 
	DSObj.bChain.InsertBlock_Honest(Bnew);
	Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(DSObj.latestCoinID,Bnew);
	mycoins.add(p);
	mycoins=sort(mycoins);
  }  

  public void MineCoin(DSCoin_Malicious DSObj){
	int n=DSObj.bChain.tr_count-1;
	Transaction[] array=new Transaction[n+1];
	int x=0;
	try{
	while(x<n){
		Transaction t=DSObj.pendingTransactions.RemoveTransaction();
		boolean found=false;
		for(int i=0;i<x;i++){
			if(t.coinID.equals(array[i].coinID)){
				found=true;
				break;
			}
		}
		TransactionBlock curr=t.coinsrc_block;
		boolean ans=false;
		if(t.coinsrc_block==null){
				ans=true;
		}
		else{
        		for(int i=0;i<curr.trarray.length;i++){
				if(t.coinID==curr.trarray[i].coinID && t.Source.UID==curr.trarray[i].Destination.UID){
					ans=true;
					break;
				}
			}
			TransactionBlock curr1=DSObj.bChain.FindLongestValidChain();
			while(curr1!=curr){
				for(int i=0;i<curr1.trarray.length;i++){
					if(t.coinID==curr1.trarray[i].coinID){
						ans= false;
						break;
					}
				}
			curr1=curr1.previous;
			}
		}
		if(found==false&&ans==true){
			array[x]=t;
			x++;
		}
	}
	}catch(Exception e){}
	Transaction mreward=new Transaction();
	String s=add(DSObj.latestCoinID);
	mreward.coinID=s;
	DSObj.latestCoinID=s;
	mreward.Source=null;
	mreward.Destination=this;
	mreward.coinsrc_block=null;
	array[n]=mreward;
	TransactionBlock Bnew=new TransactionBlock(array); 
	DSObj.bChain.InsertBlock_Malicious(Bnew);
	Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(DSObj.latestCoinID,Bnew);
	mycoins.add(p);
  }  
}
