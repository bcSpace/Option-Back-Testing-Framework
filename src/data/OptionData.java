package data;

public class OptionData {
	
	
	//data related to an option
	
	private double strike;
	private double open, high, low, close;
	private int tradeVolume, interest; 
	private int bidSize, askSize;
	private double bid, ask;
	private double iv; 
	
	private boolean dataIsSolid = true;
	
	OptionData(String line) {
		String split[] = line.split(",");
		strike = Double.parseDouble(split[0]);
		
		if(split[0].contains(".")) dataIsSolid = false;
		
		open = Double.parseDouble(split[1]);
		high = Double.parseDouble(split[2]);
		low = Double.parseDouble(split[3]);
		close = Double.parseDouble(split[4]);
		
		tradeVolume = Integer.parseInt(split[5]);
		bidSize = Integer.parseInt(split[6]);
		bid = Double.parseDouble(split[7]);
		askSize = Integer.parseInt(split[8]);
		ask = Double.parseDouble(split[9]);
		
		interest = Integer.parseInt(split[10]);
		iv = Double.parseDouble(split[11]);
		
	}
	
	public double getStrike() {return strike;}
	public double getClose() {return close;}
	public double getOpen() {return open;}
	public double getHigh() {return high;}
	public double getLow() {return low;}
	
	public boolean getSolid() {return dataIsSolid;} //if the data is good or not, my data cleaner seems to miss some of the bad data
	
}
