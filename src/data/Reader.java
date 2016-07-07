package data;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Reader {

	protected List<String[]> data;
	
	protected List<String[]> itemSet;
	
	Collection elementsA = new HashSet<String>();
	Collection elementsB = new HashSet<String>();
	List<String> erg_x = new ArrayList<String>();
	
	protected void readFile(){
		String line;
		try {
		    InputStream fis = new FileInputStream("data/test");
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
	
	private boolean contains(String[] A, String[] B){
		
		elementsA.clear();
		elementsB.clear();
		for (int i = 0; i < A.length; i++) {
			elementsA.add(A[i]);
			elementsB.add(B[i]);
		}	
		return elementsA.equals(elementsB);
		

	}
	/**
	 * Diese Methode gibt true zurück, wenn das Array A ein 
	 * Teilarry von B ist d.h wenn B alle Elemente von A 
	 * enthält und/oder noch mehr.
	 * @param A
	 * @param B
	 * @return
	 */
	
	private boolean contains2(String[] A, String[] B){
		int c = 0;
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < B.length; j++) {
				if(A[i].equals(B[j])){
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
	
	
	
	private int occurs(String[] line){
		
		int c = 0;
		for (String[] strings : data) {
			if(this.contains2(line, strings)){
				c++;
			}
		}
		
		return c;
		
	}
	
	/**
	 * Diese Methode gibt ein ItemSet der länge 1. 
	 * Es werden dazu die Daten durchgegangen und nur 
	 * Itemsets der länge 1 zurückgegeben. 
	 * @return
	 */
	protected List<String[]> giveItemSet(){
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
	 * Diese Methode liefert alle Elemente die im ItemSet 
	 * sind und schreibt diese einmal in die reult-liste 
	 * @return
	 */
	private List<String> getElementlist(List<String[]> liste){
		
		erg_x.clear();
		for (String[] strings : liste) {
			for (int i = 0; i < strings.length; i++) {
				String S = strings[i];
				if(!erg_x.contains(S)){
					erg_x.add(S);
				}
			}
		}
		return erg_x;
		
	}
	/**
	 * Diese Methode kombiniert alle möglichen (ausser mit sich selbst) 
	 * Kombinationen des als Parameter übergebenen Item Sets: 
	 * 
	 * Beispiel:
	 *  
	 * {beer}	-->  {beer,cola}   --> {beer,cola,chips}
	 * {cola}	-->  {beer,chips}  -->
	 * {chips}	-->  {cola,chips}  -->
	 * 
	 */
	private List<String[]> generateLargerItemSet(List<String[]> liste){
			List<String> one = this.getElementlist(liste);
			List<String[]> erg = new ArrayList<String[]>();
			int c = 0;
			for (int i = 0; i < liste.size(); i++) {
				String[] obj 	= liste.get(i);
								
				for (String lastElement : one) {
					//Erstelle ein neues Array und die letzte stelle 
					//wird jedes Element angehangen
					String[] neu = new String[obj.length + 1];
					for (int j = 0; j < neu.length -1; j++) {
						neu[j] = obj[j];
					}
				
					//Nur wenn Element aus one nicht schon im Array vorliegt
					boolean anhaengen = true;
					for (int j = 0; j < neu.length -1; j++) {
						if(neu[j].equals(lastElement)){
							anhaengen = false;
						}
					}
					//Doppelte Einträge wie zum Beispiel (A,B) und (B,A) nicht anhängen
					if(anhaengen){
						neu[neu.length-1] = lastElement;
						for (String[] element : erg) {
							if(this.contains(neu, element)){
								anhaengen = false;
							}
						}
						}
					
					
					if(anhaengen){	
						erg.add(neu);
					}
					c++;
					anhaengen = true;
				}
				
			}
		
		return erg;
		
	}
	
	
	
	public Reader(){	
		data = new ArrayList<String[]>();
		//data = new LinkedList<String[]>();
	
		itemSet = new ArrayList<String[]>();
		this.readFile();
		
		itemSet = this.giveItemSet();		
	}
	
	
	
	public void printItemSet(){
		
		for (String[] strings : itemSet) {
			String Zeile = "{"; 
			for (int i = 0; i < strings.length; i++) {
				if(Zeile.equals("{")){
					Zeile = Zeile + strings[i];
				}else{
					Zeile = Zeile + " , " +strings[i];
				}
			}
			
			int occ = this.occurs(strings);
			float p   = (float)occ / (float)data.size();
			System.out.println(Zeile+"}"+ " #"+occ + " -> " +p);
		}
	}
	
	
	
	public void Apriori(int k, float cutValue){
		
		
		int i = 0;
		while((i < k) && (itemSet.size()>= 1)){
			
			this.printItemSet();
			System.out.println("-------------------------");
			
			
			itemSet = this.generateLargerItemSet(itemSet);
			
			
			//Lösche die Zeilen, die unter dem cutValue liegen
			int index = 0;
			while(index < itemSet.size()) {
				String[] zeile = itemSet.get(index);
				int occurs = this.occurs(zeile);
				float p = (float)occurs / (float)data.size();
				if(p < cutValue){
					itemSet.remove(index);
					//System.out.println("remove"+index+ " "+p);
				}else{
				index++;
				}
				
			}
			
			
			i++;
		}
		
		
			
		
	}
	
}
