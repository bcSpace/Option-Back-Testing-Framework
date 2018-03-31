package trade;

import java.util.ArrayList;

public class Trade {
	
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<Integer> numbers = new ArrayList<Integer>();
	private ArrayList<Double> prices = new ArrayList<Double>();
	
	public void addId(String s) {
		ids.add(s);
	}
	
	public void addNumber(int id) {
		numbers.add(id);
	}
	
	public void addPrice(double p) {
		prices.add(p);
	}
	
	public void changePrice(int id, double d) {
		prices.set(id, d);
	}
	
	public String getId(int id) {return ids.get(id);}
	public int getNumber(int id) {return numbers.get(id);}
	public double getPrice(int id) {return prices.get(id);}
	
	//Outputing
	private String tradeName;
	private String openDate, closeDate;
	private double openPrice, openSize, closePrice;
	private double profit;
	private String details = "";
	
	public void openTrade(String tradeName, String openDate, double openPrice, double openSize) {
		this.tradeName = tradeName;
		this.openDate = openDate;
		this.openPrice = openPrice;
		this.openSize = openSize;
	}
	
	public void closeTrade(String closeDate, double closePrice, double profit) {
		this.closeDate = closeDate;
		this.closePrice = closePrice;
		this.profit = profit;
	}
	
	public void addTradeDetail(String s) {
		if(details.length() != 0) details+="|"+s;
		else details = s;
	}
	
	public void addTradeDetail(double d) {
		this.addTradeDetail(d+"");
	}
	
	public String getTradeName() {return tradeName;}
	public String getOpenDate() {return openDate;}
	public String getCloseDate() {return closeDate;}
	public double getOpenPrice() {return openPrice;}
	public double getClosePrice() {return closePrice;}
	public double getSize() {return openSize;}
	public double getProfit() {return profit;}
	public String getDetails() {return details;}

	
	public void print() {
		System.out.println(tradeName+", "+openDate+", "+openPrice+", "+openSize+",, "+closeDate+", "+closePrice+", "+profit+", "+details);
	}

}
