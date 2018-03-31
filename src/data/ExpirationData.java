package data;

import java.util.ArrayList;

import loader.FileLoader;

public class ExpirationData {
	
	private boolean isPrim;
	private boolean lastDay;
	private String expirationDate;
	private int dte; 
	
	private double callIv, putIv, iv;
	private int callVolume, putVolume, volume;
	private int callInterest, putInterest, interest; 
	
	private ArrayList<OptionData> calls = new ArrayList<OptionData>();
	private ArrayList<OptionData> puts = new ArrayList<OptionData>();
	
	public void setDetails(String expire, int dte, boolean prime, String lastDay) {
		this.expirationDate = expire;
		this.dte = dte-1;
		this.isPrim = prime;
		if(lastDay.equals("0")) this.lastDay = false;
		else this.lastDay = true;
	}
	
	public void setIv(double callIv, double putIv, double iv) {
		this.callIv = callIv;
		this.putIv = putIv;
		this.iv = iv;
	}
	
	public void setVolume(int callVolume, int putVolume, int volume) {
		this.callVolume = callVolume;
		this.putVolume = putVolume;
		this.volume = volume;
	}

	public void setInterest(int call, int put, int total) {
		this.callInterest = call;
		this.putInterest = put;
		this.interest = total;
	}
	
	private FileLoader fl = new FileLoader();
	public void loadOptions(String path) {
		String callPath = path+"\\calls.csv";
		String putPath = path+"\\puts.csv";
		//load the data my boi
		String lines[] = fl.loadData(callPath);
		for(int i = 0; i < lines.length; i++) {
			calls.add(new OptionData(lines[i]));
		}
		lines = fl.loadData(putPath);
		for(int i = 0; i < lines.length; i++) {
			puts.add(new OptionData(lines[i]));
		}
	}
	
	public ArrayList<OptionData> getCalls() {return calls;}
	public ArrayList<OptionData> getPuts() {return puts;}
	
	public OptionData getCall(double strike) {
		for(int i = 0; i < calls.size(); i++) {
			if(strike == calls.get(i).getStrike()) return calls.get(i);
		}
		return null;
	}
	
	public OptionData getPut(double strike) {
		for(int i = 0; i < puts.size(); i++) {
			if(strike == puts.get(i).getStrike()) return puts.get(i);
		}
		return null;
	}
	
	public OptionData findClosestCall(double strike) {
		double closestAmount = 5000;
		int closeId = 0;
		for(int i = 0; i < calls.size(); i++) {
			if(Math.abs(calls.get(i).getStrike()-strike) < closestAmount)  {
				closestAmount = Math.abs(calls.get(i).getStrike()-strike);
				closeId = i;
				if(closestAmount < .49) return calls.get(i);
			}
		}
		return calls.get(closeId);
	}
	
	public OptionData findClosestPut(double strike) {
		double closestAmount = 5000;
		int closeId = 0;
		for(int i = 0; i < puts.size(); i++) {
			if(Math.abs(puts.get(i).getStrike()-strike) < closestAmount) {
				closestAmount = Math.abs(puts.get(i).getStrike()-strike);
				closeId = i;
				if(closestAmount < .49) return puts.get(i);
			}
		}
		return puts.get(closeId);
	}
	
	public String getExpiration() {return expirationDate;}
	
	public int getDte() {return dte;}
	public boolean getLastDay() {return lastDay;}
	public boolean isPrim() {if(volume == 0) return false; return isPrim;}
	
}
