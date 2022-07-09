package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

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
  public void InsertBlock_Honest (TransactionBlock newBlock) {
	CRF c=new CRF(64);
	if(lastBlock==null){
		String i="1000000001";
		String s=c.Fn(start_string+"#"+newBlock.trsummary+"#"+i);
		while(s.substring(0,4).compareTo("0000")!=0){
			i=add(i);
			s=c.Fn(start_string+"#"+newBlock.trsummary+"#"+i);
		}
		newBlock.dgst=s;
		newBlock.nonce=i;
		lastBlock=newBlock;
	}
	else{
		String i="1000000001";
		String s=c.Fn(lastBlock.dgst+"#"+newBlock.trsummary+"#"+i);
		while(s.substring(0,4).compareTo("0000")!=0){
			i=add(i);
			s=c.Fn(lastBlock.dgst+"#"+newBlock.trsummary+"#"+i);
		}
		newBlock.dgst=s;
		newBlock.nonce=i;
		newBlock.previous=lastBlock;
		lastBlock=newBlock;
	}

  }
}
