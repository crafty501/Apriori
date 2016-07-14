package data;

public  class Counter {

	int x;
	public Counter(){
		x = 0; 
	}
	
	synchronized public void increase(){
		x++;
		//System.out.println(x);
	}
	
	public int get(){
		return x;
	}
	
	public void setNull(){
		x= 0;
	}
}
