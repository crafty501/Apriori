package data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.ReadOnlyFileSystemException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ReaderDB {

	
	protected DB2ConnectionManager mgr;
	protected List<String[]> data;
	Collection<ItemSet> items = new HashSet<ItemSet>();
	
	
	protected void readFile(){
		String line;
		try {
		    InputStream fis = new FileInputStream("data/transactions.txt");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("ISO-8859-1"));
		    BufferedReader br = new BufferedReader(isr);
		  
		    while ((line = br.readLine()) != null){
		    	String[] zeile = line.split(" ");
		    	data.add(zeile);
		    }
		    
		}catch (IOException e ){
			System.out.println(e.getMessage());
		 }
	}
	
	
	/**
	 * Diese Methode gibt ein ItemSet der länge 1. 
	 * Es werden dazu die Daten durchgegangen und nur 
	 * Itemsets der länge 1 zurückgegeben. 
	 * @return
	 */
	protected List<String[]> giveItemsOnce(){
		List<String[]> list = new ArrayList<String[]>();
		List<String> x = new ArrayList<String>();
		for (String[] strings : data) {
			for (int i = 0; i < strings.length; i++) {
				String str = strings[i];
				if(!x.contains(str)){
					x.add(str);
					String[] o = new String[1];
					o[0] = str;
					list.add(o);
				}
			}
		}
		return list;
	}
	/**
	 * CREATE TABLE VSISP72.ItemSet(
	 *	ITEM varchar(255) PRIMARY KEY NOT NULL
	 *	);
	 * 
	 */
	
	public void InsertItemList(){
		
		this.readFile();
		
		//Erstellt ein ItemSet mit einem Element, jedes Element kommt nur einmal vor 
		List<String[]> itemsOnce = this.giveItemsOnce();
		
		for (String[] strings : itemsOnce) {
			String Anfrage = "INSERT INTO ITEMSET (ITEM) VALUES ("+strings[0]+")";
			
			try {
				mgr.sendQuery(Anfrage, false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(Anfrage);
		
		}
		
	}
	
	public ReaderDB(DB2ConnectionManager _mgr){
		super();
		mgr = _mgr;
	}
	
	
	public Collection<ItemSet> generateLargerItemSet(){
		
		String Anfrage = "SELECT ITEMSSET.ITEM AS A,ITEMSET.ITEM AS B FROM "
						+ "ITEMSSET,ITEMSET "
						+ "WHERE NOT ITEMSSET.ITEM=ITEMSET.ITEM ";
		
		
		ResultSet res;
		try {
			res = mgr.sendQuery(Anfrage, true);
		
		
		int k = 2;
		while(res.next()){
			ItemSet itemSet = new ItemSet();
			for (int i = 1; i <= k; i++) {
				itemSet.add(res.getString(i));
			}
			//Doppelte Einträger werden vermieden, 
			//weil items ein HashSet ist
			items.add(itemSet);
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(items.size());
		return items;
		
	}
	
	
	
	
	
}
