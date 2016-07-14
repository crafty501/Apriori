package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
	protected List<int[]> data;
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
		data = new ArrayList<int[]>();
		String line;
		try {
			System.out.println("Load File: "+"data/transactionslarge.txt");
		    InputStream fis = new FileInputStream("data/transactionslarge.txt");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("ISO-8859-1"));
		    BufferedReader br = new BufferedReader(isr);
		  
		    while ((line = br.readLine()) != null){
		    	String[] zeile = line.split(" ");
		    	int[] intZeile = new int[zeile.length];
		    	for(int i = 0; i < zeile.length; i++){
		    		intZeile[i] = Integer.parseInt(zeile[i]);
		    	}
		    	data.add(intZeile);
		    }
		    
		}catch (IOException e ){
			System.out.println(e.getMessage());
		 }
	}

	protected void savetoFile(int c,List<ItemSet> liste){
		
		try {
	
			File datei = new File("data/"+c+".txt");
		    if (datei.exists()) {
		      datei.delete();
		    }
			
			
			System.out.println("Save to File: "+c+".txt");
		    OutputStream fis = new FileOutputStream("data/"+c+".txt");
		  
		    for (ItemSet itemSet : liste) {
		    	String line = itemSet.toString() + "\n";
				byte[] b = line.getBytes();
				fis.write(b);
			}
		    
		    fis.close();
		}catch (IOException e ){
			System.out.println(e.getMessage());
			System.exit(0);
		 }
	}
	
	/**
	 * Diese Methode gibt ein ItemSet der länge 1. 
	 * Es werden dazu die Daten durchgegangen und nur 
	 * Itemsets der länge 1 zurückgegeben. 
	 * @return
	 */
	protected List<int[]> giveItemsOnce(){
		List<int[]> list = new ArrayList<int[]>();
		List<Integer> x = new ArrayList<Integer>();
		for (int[] ids : data) {
			for (int i = 0; i < ids.length; i++) {
				int id = ids[i];
				if(!x.contains(id)){
					x.add(id);
					int[] o = new int[1];
					o[0] = id;
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
	protected List<int[]> giveItemsOnce(List<ItemSet> itemSetList){
		List<int[]> list = new ArrayList<int[]>();
		List<Integer> x = new ArrayList<Integer>();
		Iterator<ItemSet> it = itemSetList.iterator();
		
		while(it.hasNext()){
			ItemSet s = it.next();
			int[] values = s.toArray();
			for (int i = 0; i < values.length; i++) {
				int id = values[i];
				if(!x.contains(id)){
					x.add(id);
					int[] o = new int[1];
					o[0] = id;
					list.add(o);
				}
			}
		}
		return list;
	}
	
	
	protected void createNewTable(int c ,List<ItemSet> itemSetList){
		try {
			List<int[]> l = this.giveItemsOnce(itemSetList);
			String Anfrage = "CREATE TABLE VSISP72.ITEMSET"+c+" ( "
							+ "ITEM varchar(255) PRIMARY KEY NOT NULL)";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		for (int[] ids : l) {
			int value = ids[0];
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
			List<int[]> l = this.giveItemsOnce();
			String Anfrage = "CREATE TABLE VSISP72.ITEMSET0 ( "
							+ "ITEM varchar(255) PRIMARY KEY NOT NULL)";
			//System.out.println(Anfrage);
			mgr.sendQuery(Anfrage, false);
		for (int[] ids : l) {
			String value = String.valueOf(ids[0]);
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
		//System.out.println(Anfrage);
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
					itemSet.add(res.getInt(i));
			}
			if(itemSet.size() == c){
			//Doppelte Einträger werden vermieden, 
			//weil items ein HashSet ist
			items.add(itemSet);
		
			}
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
	protected boolean contains2(int[] A, int[] B){
		int c = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < B.length; j++) {
				if(A[i]==B[j]){
					c++;
				}
			}
		}
		if(c == A.length){
			return true;
		}else{
			return false;
		}
		
	}
	protected int occurs(int[] line){
		
		int c = 0;
		for (int[] ids : data) {
			if(this.contains2(line, ids)){
				c++;
			}
		}
		
		return c;
		
	}
	

	
	
	
}
