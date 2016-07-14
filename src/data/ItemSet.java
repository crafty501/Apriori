package data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ItemSet {

	public Collection items;
	
	public ItemSet(){
		super();
		items = new HashSet<Integer>();
	}
	
	public boolean add(int item){
		return items.add(item);
	}
	
	public int size(){
		return items.size();
	}

	public int[] toArray(){
		
		int size = items.size();
		
		int[] erg = new int[size];
		int i = 0;
		Iterator<Integer> I = items.iterator();
		while(I.hasNext()){
			int id = I.next();
			//System.out.println(S);
			erg[i] = id;
			i++;
		}
		return erg;
	}
	
	
	@Override
	public boolean equals(Object o){
		
		ItemSet is = (ItemSet)o;
		return is.items.equals(this.items);
	}
	
	
	@Override
	public int hashCode(){
		
		int sum = 0;
		Iterator<Integer> I = items.iterator();
		while (I.hasNext()) {
			int id = I.next();
			sum = sum + id;
		}
		return sum;
	}
	
	public String toString(){
		
		Iterator<Integer> I = items.iterator();
		String erg = "{";
		while (I.hasNext()) {
			if(erg.equals("{")){
				erg = erg + I.next();
			}else{
				erg = erg + " , " + I.next();	
			}
		}
		//System.out.println(erg+"}");
		return erg +"}";
	}
}
