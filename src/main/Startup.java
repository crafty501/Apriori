package main;

import java.util.Collection;

import data.Apriori;
import data.DB2ConnectionManager;
import data.ItemSet;
import data.AprioriNaive;
import data.ReaderDB;

public class Startup {

	
	
	public static void main(String[] args){
		
		
		
		AprioriNaive r1 = new AprioriNaive();
		//r1.Apriori(10, 0.25f);
		
		System.out.println("DB-Apriori mit cutValue=0.015f");
		Apriori apri = new Apriori(0.015f);
		

		
		
	}
	
}
