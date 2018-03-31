package main;

import java.io.File;

import data.DataManager;
import data.UnderlyingData;
import ui.Gui;

public abstract class Controller {

	//THIS CONTROLLER is mostly for managing data
	
	
	DataManager dm;
	Gui gui;
	
	private final String dbPath = "B:\\option_database\\cleaned\\dailyData\\";
	private String[] underlyings;
	private String[] underlyingLoaded;
	private int[] underlyingSize;
	int[] underlyingStatus;
	
	
	Controller() {
		
		dm = new DataManager(this);
		dm.init();
		
		//get all the underlyings in the local database
		File files[] = new File(dbPath).listFiles();
		underlyings = new String[files.length];
		underlyingLoaded = new String[files.length];
		underlyingSize = new int[files.length];
		underlyingStatus = new int[files.length];
		
		for(int i = 0; i < files.length; i++) { 
			underlyings[i] = files[i].getName();
			underlyingLoaded[i] = "empty"; //default, memory, instance
			underlyingSize[i] = files[i].list().length; //number of days of data
			underlyingStatus[i] = 0;
		}	
		
		gui = new Gui(this);
		gui.create();
		
	}
	
	public abstract String sendCommand(String command); //should prolly move the code to this rather than having it be abstract 
	
	public void updateTable(int id, String update) {
		gui.updateDataTable(id, update);
	}
	
	public void updateStatus(int id, int status, String update) {
		underlyingStatus[id] = status;
		this.updateTable(id, update);
	}

	public int isLoaded(int id) {
		if(id >= underlyings.length || id < 0) return -1;
		return underlyingStatus[id];
	}
	
	int convertId(String s) {
		s = s.toUpperCase();
		for(int i = 0; i < underlyings.length; i++) {
			if(underlyings[i].equals(s)) return i;
		}
		return -2;
	}
	
	public void addUnderlying(int id, boolean mem) {
		dm.addUnderlying(getUnderlyingName(id), mem, -1, id);
		System.out.println("Added underlying " + id);
	}
	
	public int getUnderlyingSize() {return underlyings.length;}
	public String getUnderlyingName(int i) {return underlyings[i];}
	public String getLoaded(int i) {return underlyingLoaded[i];}
	public int getUndelyingDataAmount(int i) {return underlyingSize[i];}
	public int getUnderlyingStatus(int i) {return underlyingStatus[i];}
	
	public UnderlyingData getData(int id) {return dm.getUnderlying(id);}
	
	
	
}
