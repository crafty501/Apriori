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
		
		
		ReaderDB r2 = new ReaderDB(mgr);
		System.out.println("DB-Apriori mit cutValue=0.025f");
		r2.Apriori(3, 0.025f);
		
		
	}
	
}
