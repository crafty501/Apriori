package data;

import java.util.ArrayList;
import java.util.List;

public class ListThread extends Thread{
	
	List<ItemSet> partialList;
	List<ItemSet> ergList;
	protected List<int[]> data;
	float cutValue;
	int start,ende;
	Counter counter;
	
	protected boolean contains(int[] A, int[] B){
		int c = 0;
		
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < B.length; j++) {
				if(A[i] == B[j]){
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
			if(this.contains(line, ids)){
				c++;
			}
		}
		return c;
	}
	
	public ListThread(List<ItemSet> _list,List<int[]> _data, float _cutValue,int _start,int _ende){
		partialList = _list;
		data = _data;
		cutValue = _cutValue;
		start = _start;
		ende = _ende;
		ergList = new ArrayList<ItemSet>();
	}
	
	public void Set_Erg_List(List<ItemSet> _ergList){
		ergList = _ergList;
	}
	
	public void Set_C(Counter _c){
		counter = _c;
	}
	
	@Override
	public void run(){
		
		int index = 0; 
		while(index < partialList.size()) {
			ItemSet s = partialList.get(index);
			int occurs = this.occurs(s.toArray());
			float p = (float)occurs / (float)data.size();
			if(p < cutValue){
				partialList.remove(index);
				//System.out.println("remove: "+p);
			}else{
				index++;
			}
		}
		
		//Ergebnisliste schreiben 
		
		for (int i = 0; i < partialList.size(); i++) {
			ergList.add(partialList.get(i));
		}
		
		counter.increase();
		//System.out.println("Ende...");
	}
}
