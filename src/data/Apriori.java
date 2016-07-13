package data;

import java.util.ArrayList;
import java.util.List;

public class Apriori extends ReaderDB{

	
	List<ItemSet> ergList;
	
	public Apriori(float cutValue){
		super();
		//Erstelle erste Join Tabelle
				extracted(cutValue);
	}

	private void extracted(float cutValue) {
		System.out.println("Erstelle erste join Tabelle");
		this.createFirstJoinTable();
		int i = 1;
		itemSetList = new ArrayList<ItemSet>();
		while(((itemSetList.size()>1)||(i == 1))){
			System.out.println("Compute Combinations ...");
			itemSetList = this.generateLargerItemSet(i);
			//Lösche die Zeilen, die unter dem cutValue liegen
			System.out.println("There are "+itemSetList.size()+" combinations");
			int index = 0;
			System.out.println("Compute occurency and p...");
			int removed = 0;
			
			
			int c  = itemSetList.size() /  40000;
			
			int start = 0;
			int ende = 0;
			boolean finish[] = new boolean[c];
			for (int j = 0; j < finish.length; j++) {
				finish[j] = false;
			}
			
			for(int z = 0; z <= c ; z++){
				ende = (z+1) * 40000;
				if(ende > itemSetList.size()){
					ende = itemSetList.size();
				}
				
				System.out.println("( "+start + " - " + ende+")" );
				List<ItemSet> partialList = new ArrayList<ItemSet>();
				for (int j = start; j < ende; j++) {
					partialList.add(itemSetList.get(j));
				}
				
				ListThread t = new ListThread(partialList,data,cutValue,start,ende);
				t.Set_Erg_List(ergList);
				t.Set_C(z);
				t.Set_Finish(finish);
				t.start();
				start = ende+1;
			}
			
			//Auf die Beendigung der Threads warten
			
			for (int j = 0; j < finish.length; j++) {
				if(!finish[j]){
					c++;
				}
			}
			if(c != 0){
			
			}
			
			
			System.out.println("Es wurden "+removed+" Einträge entfernt");
			System.out.println("Finisch: "+itemSetList.size());
			System.out.println("Erstelle neue Join Tabelle");
			//neue Join Tabelle erstellen
			createNewTable(i,itemSetList);
			
			System.out.println("----------------------------------");
			
			i++;
		}
	}
}
