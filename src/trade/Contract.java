package trade;

public class Contract {
	
	String underlying;
	
	boolean option;
	boolean call;
	double strike; 
	String expiration;
	
	public void createStock(String underlying) {
		this.underlying = underlying;
		this.option = false;
	}
	
	
	

}
