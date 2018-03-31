package trade;

import java.util.ArrayList;

import data.OptionData;

public class OptionSpread {
	
	private boolean hasUnderlying;
	private double underlyingPrice;
	private double underlyingSize;
	private int underlyingPos;
	
	private ArrayList<OptionContract> options = new ArrayList<OptionContract>();
	
	public void addUnderlying(double underlyingPrice, double underlyingSize, int pos) {
		this.underlyingPrice = underlyingPrice;
		this.underlyingSize = underlyingSize;
		this.underlyingPos = pos;
		if(underlyingPos != 0) hasUnderlying = true;
		else hasUnderlying = false;
	}
	
	public void addOptionContract(OptionContract option) {
		options.add(option);
	}
	
	public ArrayList<OptionContract> getOptions() {
		return options;		
	}
	
	public double getCapitalRequirement(double stress) {
		//using a basic .15 stress test up and down
		
		double lowCapital = 0;
		double highCapital = 0;
		
		for(int i = 0; i < options.size(); i++) {
			lowCapital += options.get(i).getCapital(underlyingPrice*(1-stress));
			highCapital += options.get(i).getCapital(underlyingPrice*(1+stress));
		}
		
		
		if(lowCapital < highCapital) return Math.abs(lowCapital);
		else return Math.abs(highCapital);
	}
	
	public void print() {
		System.out.println("-------------------------");
		for(int i = 0; i < options.size(); i++) {
			System.out.println(options.get(i).getSize() + ", " + options.get(i).getCall() + ", " + options.get(i).getStrike() + ", " +options.get(i).getCapital(underlyingPrice*.8) + ", " + options.get(i).getCapital(underlyingPrice*1.2));
		}
		System.out.println(""+this.getCapitalRequirement(.2));
		System.out.println("-----------------------------");
	}
	
	public String getDetails() {
		return "";
	}

}
