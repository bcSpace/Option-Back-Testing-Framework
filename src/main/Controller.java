package main;

import java.io.File;

import data.DataManager;
import data.UnderlyingData;
import ui.Gui;

public class Controller {

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
	
	public String sendCommand(String command){ 
		//PARSING FOR DATA MANAGER LOADING
		if(command.contains("exit")) System.exit(0);
		if(command.startsWith("load")) {
			String split[] = command.split(" ");
			//load (id/stock) (id/stock) (m/i)
			if(split.length != 4) return "Invalid args";
			boolean mem = split[3].contains("m");
			int id = -1; 
			if(split[1].contains("id")) {
				try { id = Integer.parseInt(split[2]); } catch(Exception e) {return "Invalid id " + e.getMessage();}
				if(id == -1) return "Id out of range";
			} else if(split[1].contains("stock")) {
				id = this.convertId(split[2]);
				if(id == -2) return "Invalid stock";
			} else {
				return "Invlaid item type";
			}
		
			int status = isLoaded(id);
			if(status == 1) return id+" is loading";
			else if(status == 2) return id +" already filled";
			else if(status == 0) {
				dm.addUnderlying(getUnderlyingName(id), mem, -1, id);
				if(mem){ 
					return id+" loading started";
				}
				else { 
					gui.updateDataTable(id, "instanced");
					return id+" instanced";
				}
			}
		
		} else if(command.startsWith("clear")) {
			//clear data
			String split[] = command.split(" ");
			if(split.length != 3) return "Invalid args";
			int id = -1;
			if(split[1].contains("id")) {
				try { id = Integer.parseInt(split[2]); } catch(Exception e) {return "Invalid id " + e.getMessage();}
				if(id == -1) return "Id out of range";
			} else if(split[1].contains("stock")) {
				id = this.convertId(split[2]);
				if(id == -2) return "Invalid stock";
			} else {
				return "Invlaid item type";
			}
			
			int status = isLoaded(id);
			if(status == 1 || status == 2) {
				dm.clearData(id);
				underlyingStatus[id] = 0;
				gui.updateDataTable(id, "empty");
				System.gc();
				return "Cleard: " + id;
			} else if(status == 0) {
				return "already clear";
			}
		}
		return "Unknown command";
	}
	
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
