package main;

import java.util.Collection;

import data.DB2ConnectionManager;
import data.ItemSet;
import data.Reader;
import data.ReaderDB;

public class startup {

	
	
	public static void main(String[] args){
		
		
		DB2ConnectionManager mgr = new DB2ConnectionManager();
		Reader r1 = new Reader();
		r1.Apriori(10, 0.25f);
		
		//TODO
		/*
		//ReaderDB r2 = new ReaderDB(mgr);
		Collection<ItemSet> x  = r2.generateLargerItemSet();	
		String S = x.toString();
		System.out.println(S);
		*/
		
	}
	
}
