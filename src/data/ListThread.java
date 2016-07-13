package data;

import java.util.ArrayList;
import java.util.List;

public class ListThread extends Thread{
	
	List<ItemSet> partialList;
	List<ItemSet> ergList;
	protected List<String[]> data;
	float cutValue;
	int start,ende,c;
	
	boolean[] finish;
	
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
	
	public ListThread(List<ItemSet> _list,List<String[]> _data, float _cutValue,int _start,int _ende){
		partialList = _list;
		data = _data;
		cutValue = _cutValue;
		start = _start;
		ende = _ende;
	}
	
	public void Set_Erg_List(List<ItemSet> _ergList){
		ergList = _ergList;
	}
	
	public void Set_C(int _c){
		c = _c;
	}
	public void Set_Finish(boolean[] _finish){
		finish = _finish;
	}
	
	@Override
	public void run(){
		
		int index = 0; 
		while(index < partialList.size()) {
			ItemSet s = partialList.get(index);
			int occurs = this.occurs((String[])s.toArray());
			float p = (float)occurs / (float)data.size();
			if(p < cutValue){
				partialList.remove(index);
				//System.out.println("remove: "+p);
			}else{
				index++;
			}
		}
		
		//Ergebnisliste schreiben 
		
		for (int i = start; i < ende; i++) {
			int pindex = i - start; 
			ergList.set(i, partialList.get(pindex));
			
		}
		finish[c] = true;
	}
}
