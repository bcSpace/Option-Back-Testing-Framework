package data;

import java.util.ArrayList;

public class DailyData {
	
	//daily data
	//This data will contain the expiration data, and general underlying information
	//The expiration data contains all the options for such expiration

	private GeneralData data; 
	private ArrayList<ExpirationData> expirations = new ArrayList<ExpirationData>();
	
	public DailyData(GeneralData data, ArrayList<ExpirationData> expire) {
		this.data = data;
		this.expirations = expire;
	}
	
	public void create(GeneralData data, ArrayList<ExpirationData> expire) {
		this.data = data;
		this.expirations = expire;
	}

	
	public ArrayList<ExpirationData> getExpires() {return expirations;} 
	public ExpirationData getCurrentExpire() {return expirations.get(0);}
	public ExpirationData getExpireData(String s) {
		for(int i = 0; i < expirations.size(); i++) if(expirations.get(i).getExpiration().equals(s)) return expirations.get(i);
		return null;
	}

	//general data
	public double getIv() {return data.getIv();}
	public double getClosePrice() {return data.getClosing();}
	public String getDate() {return data.getDate();}
	
	//fast way for getting a expiration with a DTE between startDay-endDay
	//prim only = only primary expiration cycles, monthly/quarterally/etc
	//top = grabbing the furthest dte, biggest dte, if there is more than 1 expiration that fits the dte requirements
	public ExpirationData getExpireData(int startDay, int endDay, boolean primOnly, boolean top) {
		ArrayList<ExpirationData> expires = new ArrayList<ExpirationData>();
		
		for(int i = 0; i < expirations.size(); i++) {
			if(expirations.get(i).getDte() < startDay && expirations.get(i).getDte() > endDay && expirations.get(i).isPrim()) { 
				expires.add(expirations.get(i));
			}
		}
		
		for(int i = 0; i < expires.size(); i++) {
			if(expires.get(i).getExpiration().contains("2011-05-21")) {
				expires.remove(i);
				i = expires.size()+2;
			}
		}
		
		if(expires.size() == 0) return null;
		else 
			if(top) return expires.get(expires.size()-1);
			else return expires.get(0);
	}
	
}
