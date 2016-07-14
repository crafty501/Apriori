package data;

import java.util.ArrayList;
import java.util.List;

public class Apriori extends ReaderDB{

	
	List<ItemSet> ergList;
	final Counter counter;
	
	public Apriori(float cutValue){
		super();
		counter = new Counter();
		//Erstelle erste Join Tabelle
				extracted(cutValue);
	}
	
	private void warteaufThreads(int c){
		//TODO
		// Beim warten auf die Threads könnte man schon ein 
		// neues Thread Starten welches
		// Die neue Join Tabelle anlegt. 
		System.out.println("warte auf Berechnung von "+(c+1)+" Threads...");
		boolean weiter = true;
		while(weiter){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(counter.get() == (c+1)){
					weiter = false;
			}
		}
	}

	private void extracted(float cutValue) {
		
		long starttime = System.currentTimeMillis();
		
		System.out.println("Erstelle erste join Tabelle");
		this.createFirstJoinTable();
		int i = 1;
		itemSetList = new ArrayList<ItemSet>();
		ergList = new ArrayList<ItemSet>();
		
		
		while(((ergList.size()>1)||(i == 1))){
			ergList.clear();
			System.out.println("Compute Combinations ...");
			itemSetList = this.generateLargerItemSet(i);
			//Lösche die Zeilen, die unter dem cutValue liegen
			System.out.println("There are "+itemSetList.size()+" combinations");
			System.out.println("Compute occurency and p...");
			int c  = itemSetList.size() /  100000;
			
			int start = 0;
			int ende = 0;
			
			counter.setNull();
			
			for(int z = 0; z <= c ; z++){
				ende = (z+1) * 100000;
				if(ende > itemSetList.size()){
					ende = itemSetList.size();
				}
				
				//System.out.println("( "+start + " - " + ende+")" );
				List<ItemSet> partialList = new ArrayList<ItemSet>();
				for (int j = start; j < ende; j++) {
					partialList.add(itemSetList.get(j));
				}
				
				ListThread t = new ListThread(partialList,data,cutValue,start,ende);
				t.Set_Erg_List(ergList);
				t.Set_C(counter);
				t.start();
				start = ende+1;
			}
			
			//Auf die Beendigung der Threads warten
			warteaufThreads(c);
			
			System.out.println("Finisch: "+ergList.size());
			System.out.println("Speichere Ergebnisse...");
			//TODO Ergebnisse von ErgebnisListe in eine seperate datei schreiben
			savetoFile(i, ergList);
			
			System.out.println("Erstelle neue Join Tabelle");
			//neue Join Tabelle erstellen
			createNewTable(i,itemSetList);
			System.out.println("----------------------------------");
			i++;
		}
		
		long time = (System.currentTimeMillis() - starttime) / 1000 ;
		System.out.println("Fertig in: "+time+"s" );
		System.out.println("Lösche temporäre Tabellen...");
		
		
	}
}
