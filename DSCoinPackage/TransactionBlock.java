package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
	Transaction[] ans= new Transaction[t.length];
	for(int i=0;i<t.length;i++){
		ans[i]=t[i];
	}
	this.trarray=ans;
	MerkleTree m=new MerkleTree();
	String s=m.Build(this.trarray);
	this.Tree=m;
	this.trsummary=s;
  }

  public boolean checkTransaction (Transaction t) {
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
		int x=0;
		for(int i=0;i<trarray.length;i++){
			if(t.coinID==trarray[i].coinID){
				x++;
			}
		}
		if(x>1){
			return false;
		}
		TransactionBlock curr1=previous;
		while(curr1!=curr){
			for(int i=0;i<curr1.trarray.length;i++){
				if(t.coinID==curr1.trarray[i].coinID){
					return false;
				}
			}
		curr1=curr1.previous;
		}
	}
    return ans;
  }
}
