package data;

import java.util.ArrayList;

public class Apriori extends ReaderDB{

	
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
			while(index < itemSetList.size()) {
				ItemSet s = itemSetList.get(index);
				int occurs = this.occurs((String[])s.toArray());
				float p = (float)occurs / (float)data.size();
				if(p < cutValue){
					itemSetList.remove(index);
					removed++;
					//System.out.println("remove: "+p);
				}else{
					index++;
				}
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
