package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
	CRF c=new CRF(64);
	boolean ans1=false;
	if(tB.dgst.substring(0,4).compareTo("0000")==0){
		ans1=true;
	}
	boolean ans2=false;
	if(tB.previous==null){
		String s=c.Fn(start_string+"#"+tB.trsummary+"#"+tB.nonce);
		if(s.compareTo(tB.dgst)==0){
			ans2=true;
		}
	}
	else{
		String s=c.Fn(tB.previous.dgst+"#"+tB.trsummary+"#"+tB.nonce);
		if(s.compareTo(tB.dgst)==0){
			ans2=true;
		}
	}
	boolean ans3=false;
	MerkleTree m=new MerkleTree();
	String val=m.Build(tB.trarray);
	if(val.compareTo(tB.trsummary)==0){
		ans3=true;
	}
	boolean ans4=true;
	for(int i=0;i<tB.trarray.length;i++){
		if(tB.checkTransaction(tB.trarray[i])==false){
			ans4=false;
			break;
		}
	}
	boolean ans=true;
	ans=ans1&&ans2&&ans3&&ans4;
    return ans;
  }
  public static TransactionBlock correct(TransactionBlock B){
	while(B!=null){
		if(checkTransactionBlock(B)==true){
			return B;
		}
	B=B.previous;
	}
	return B;
  }
  public static int lencorrectchain(TransactionBlock B){
	int len=0;
	while(B!=null){
		if(checkTransactionBlock(B)==true){
			len+=1;
		}
		else{
			len=0;
		}
	B=B.previous;
	}
	return len;
  }
  public TransactionBlock FindLongestValidChain () {
		int l=0;
		for(int i=0;i<lastBlocksList.length;i++){
			if(lastBlocksList[i]!=null){
				l++;
			}
			else{
				break;
			}
		}
		int[] longlen =new int[l];
		for(int i=0;i<l;i++){
			longlen[i]=lencorrectchain(lastBlocksList[i]);
		}
		int max=0;
		for(int i=0;i<longlen.length;i++){
			if(longlen[i]>longlen[max]){
				max=i;
			}
		}
		TransactionBlock curr=lastBlocksList[max];
	return correct(curr);
  }
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
  public void InsertBlock_Malicious (TransactionBlock newBlock) {
	CRF c=new CRF(64);
	if(lastBlocksList[0]==null){
		String i="1000000001";
		String s=c.Fn(start_string+"#"+newBlock.trsummary+"#"+i);
		while(s.substring(0,4).compareTo("0000")!=0){
			i=add(i);
			s=c.Fn(start_string+"#"+newBlock.trsummary+"#"+i);
		}
		newBlock.dgst=s;
		newBlock.nonce=i;
		lastBlocksList[0]=newBlock;
	}
	else{
		TransactionBlock lastcorrectBlock=FindLongestValidChain ();
		String i="1000000001";
		String s=c.Fn(lastcorrectBlock.dgst+"#"+newBlock.trsummary+"#"+i);
		while(s.substring(0,4).compareTo("0000")!=0){
			i=add(i);
			s=c.Fn(lastcorrectBlock.dgst+"#"+newBlock.trsummary+"#"+i);
		}
		newBlock.dgst=s;
		newBlock.nonce=i;
		newBlock.previous=lastcorrectBlock;
		boolean ans=false;
		for(int j=0;j<lastBlocksList.length;j++){
			if(lastBlocksList[j]==lastcorrectBlock){
				ans=true;
				lastBlocksList[j]=newBlock;
				break;
			}
		}
		if(ans==false){
			for(int k=0;k<lastBlocksList.length;k++){
				if(lastBlocksList[k]==null){
					lastBlocksList[k]=newBlock;
					break;
				}
			}
		}
	}
  }
}
