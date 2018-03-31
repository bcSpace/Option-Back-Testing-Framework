package data;

import java.util.ArrayList;

public class GeneralData {
	
	//general underlying data

	private String date;
	private double underlyingPrice;
	private double iv;
	private int totalOptionVolume, totalInterest; 
	
	private ArrayList<String> expirations = new ArrayList<String>();
	
	public void create(double price, double iv, int volume, int interest, ArrayList<String> expire, String date) {
		this.underlyingPrice =price;
		this.iv = iv;
		this.totalOptionVolume = volume;
		this.totalInterest = interest; 
		this.expirations = expire;
		this.date = date;
	}
	
	public double getIv() {return iv;}
	public double getClosing() {return underlyingPrice;}
	public String getDate() {return date;}
	
}
