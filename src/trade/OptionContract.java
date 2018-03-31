package trade;

import data.OptionData;

public class OptionContract {
	
	private String expiration;
	private double strikePrice;
	private double openPrice;
	private boolean call;
	
	private double openSize;
	
	public void create(String expire, double strike, double openPrice, boolean call, double size) {
		this.expiration = expire;
		this.strikePrice = strike;
		this.openPrice = openPrice;
		this.call = call;
		openSize = size;
	}
	
	public void create(String expire, OptionData od, boolean b, double openSize) {
		this.expiration = expire;
		this.strikePrice = od.getStrike();
		this.openPrice = od.getClose();
		this.call = b;
		this.openSize = openSize;
	}
	
	public void setSize(double openSize) {
		this.openSize = openSize;
	}
	

	public double getCapital(double underlying) {
		double in = 0;
		if(call) {
			in = underlying-strikePrice;
		} else {
			in = strikePrice-underlying;
		}
		if(in < 0) in = 0;
		
		double PL = in-openPrice;
		PL *= openSize;
		return PL*100;
	}
	
	
	public double getSize() {return openSize;}
	public boolean getCall() {return call;}
	public double getStrike() {return strikePrice;}
	
}
