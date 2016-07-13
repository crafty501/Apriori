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
import java.util.Iterator;
import java.util.List;

/**
 * Die Idee ist, dass die kombinationen der einzelnen Items nicht in 
 * der Java-Anwendung berechnet werden, sondern also join 
 * auf der Datenbank. 
 * 
 * @author callya 
 */
public class ReaderDB {

	
	protected final DB2ConnectionManager mgr;
	protected List<String[]> data;
	protected List<ItemSet> itemSetList;
	int itemSetCount;
	
	
	public ReaderDB(){
		mgr = new DB2ConnectionManager();
		itemSetCount = 3;
		readFile();
		//Neue Tabelle zum Joinen anlegen
				//createNewJoinTable();
				//itemSetCount++;
				//createNewJoinTable();
				//itemSetCount++;
				//createNewJoinTable();
	}
	
	protected void readFile(){
		data = new ArrayList<String[]>();
		String line;
		try {
			System.out.println("Load File: "+"data/transactions.txt");
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
	 * Diese Methode gibt ein ItemSet der länge 1. 
	 * Es werden dazu die Daten durchgegangen und nur 
	 * Itemsets der länge 1 zurückgegeben. 
	 * @return
	 */
	protected List<String[]> giveItemsOnce(List<ItemSet> itemSetList){
		List<String[]> list = new ArrayList<String[]>();
		List<String> x = new ArrayList<String>();
		Iterator<ItemSet> it = itemSetList.iterator();
		
		while(it.hasNext()){
			ItemSet s = it.next();
			String[] values = (String[])s.toArray();
			for (int i = 0; i < values.length; i++) {
				String str = values[i];
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
	
	
	protected void createNewTable(int c ,List<ItemSet> itemSetList){
		try {
			List<String[]> l = this.giveItemsOnce(itemSetList);
			String Anfrage = "CREATE TABLE VSISP72.ITEMSET"+c+" ( "
							+ "ITEM varchar(255) PRIMARY KEY NOT NULL)";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		for (String[] strings : l) {
			String value = strings[0];
			Anfrage = "INSERT INTO ItemSet"+c+" "
						+ "(ITEM)"
						+ "VALUES"
						+ "('"+value+"')";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected void createFirstJoinTable(){
		try {
			//TODO: Hier die itemSetList übergeben
			List<String[]> l = this.giveItemsOnce();
			String Anfrage = "CREATE TABLE VSISP72.ITEMSET0 ( "
							+ "ITEM varchar(255) PRIMARY KEY NOT NULL)";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		for (String[] strings : l) {
			String value = strings[0];
			Anfrage = "INSERT INTO ItemSet0 "
						+ "(ITEM)"
						+ "VALUES"
						+ "('"+value+"')";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	protected String makeJoinQuery(int c){
		String table_names = "ItemSet0" ;
		String field_names = "ItemSet0.ITEM AS \"0\"";
		String where_cond  = "NOT ItemSet0.ITEM=ItemSet1.ITEM";
		for (int j = 1; j < c; j++) {
			table_names = table_names + ",ItemSet"+j;
			field_names = field_names +",ItemSet"+j+".ITEM AS \""+j+"\"";
			if((j+1) < c){
			where_cond 	= where_cond + " AND NOT ItemSet"+j+".ITEM =ItemSet"+(j+1)+".ITEM";
			}
			}
		String Anfrage = "";
		if(c != 1){
		 Anfrage = "SELECT "+field_names+" FROM "
						+ table_names
						+ " WHERE "
						+ where_cond;
		}else{
		 Anfrage = "SELECT "+field_names+" FROM "+ table_names;
		}
		return Anfrage;
	}
	
	
	
	
	public List<ItemSet> generateLargerItemSet(int c){
		
		Collection<ItemSet> items = new HashSet<ItemSet>();
		
		
		//Join bilden
		String Anfrage = makeJoinQuery(c);
		//System.out.println(Anfrage);
		ResultSet res;
		try {
			res = mgr.sendQuery(Anfrage, true);
		while(res.next()){
			ItemSet itemSet = new ItemSet();
			for (int i = 1; i <= c; i++) {
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
		//System.out.println("itemset_size:"+items.size());
		
		Iterator<ItemSet> it = items.iterator();
		List<ItemSet> erg = new ArrayList<ItemSet>();
		while(it.hasNext()){
		erg.add(it.next());
		}
		
		return erg;
		
	}
	
	/**
	 * Diese Methode schaut ob Array A in Array B 
	 * enthalten ist. 
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	protected boolean contains2(String[] A, String[] B){
		int c = 0;
		for (int i = 0; i < A.length; i++) {
			int valueA = Integer.valueOf(A[i]);
			for (int j = 0; j < B.length; j++) {
				if(A[i].equals(B[j])){
					c++;
				}
				int valueB = Integer.valueOf(B[j]);
				if(valueA < valueB){ 
					j = B.length; // Beende Schleife
				}
			}
		}
		if(c == A.length){
			return true;
		}else{
			return false;
		}
		
	}
	protected int occurs(String[] line){
		
		int c = 0;
		for (String[] strings : data) {
			if(this.contains2(line, strings)){
				c++;
			}
		}
		
		return c;
		
	}
	

	
	
	
}
