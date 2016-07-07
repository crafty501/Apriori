package data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ItemSet {

	public Collection items;
	
	public ItemSet(){
		super();
		items = new HashSet<String>();
	}
	
	public boolean add(String item){
		return items.add(item);
	}
	

	public Object[] toArray(){
		
		int size = items.size();
		
		String[] erg = new String[size];
		int i = 0;
		Iterator<String> I = items.iterator();
		while(I.hasNext()){
			String S = I.next();
			//System.out.println(S);
			erg[i] = S;
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
		Iterator<String> I = items.iterator();
		while (I.hasNext()) {
			String S = I.next();
			sum = sum + S.hashCode();
		}
		return sum;
	}
	
	public String toString(){
		
		Iterator<String> I = items.iterator();
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
