package data;

import java.util.ArrayList;

import main.Controller;
import manager.DateManager;

public class DataManager {
	
	private Controller controller;
	
	private ArrayList<UnderlyingData> quedData = new ArrayList<UnderlyingData>();
	private ArrayList<UnderlyingData> data = new ArrayList<UnderlyingData>();
	
	private Thread loadingThread;
	private boolean loading = false;
	
	public DataManager(Controller c) {
		this.controller = c;
	}
	
	public void init() {
		DateManager.init();
		
		loadingThread = new Thread() {
			public synchronized void  run() {
				boolean running = true;
				while(running) {
					synchronized(this) {
						loading = true;
						loadLoop();
						loading = false;
						try {
							loadingThread.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			private void loadLoop() {
				while(quedData.size() > 0) {
					controller.updateStatus(quedData.get(0).getId(), 1, "Loading Qued");
					quedData.get(0).loadAllData(controller);
					controller.updateStatus(quedData.get(0).getId(), 2, "Memory");
					data.add(quedData.get(0));
					quedData.remove(0);
				}
			}
			
		};
		loadingThread.start();
		
	}
	
	public void addUnderlying(String underlying, boolean b, double part, int id) {
		if(b) {
			controller.updateStatus(id, 1, "Load Qued");
			quedData.add(new UnderlyingData(underlying, b, part, id));
			
			//if the thread is not alreay loading data then tell it that new data needs to be loaded
			if(!loading) { 
				synchronized(loadingThread) {
					loadingThread.notify();}
			}
		} else {
			//if its not memory based, then no need to load at all
			data.add(new UnderlyingData(underlying, b, part, id));
			controller.updateStatus(id, 2, "instanced");
		}
		
	}
	
	//clearing all data
	public void clear() {
		data.clear();
		DateManager.clear();
	}
	
	//removing data
	public void clearData(int id) {
		for(int i = 0; i < data.size(); i++) 
			if(data.get(i).getId() == id) {
				data.remove(i);
				return;
			}
	}
	
	public UnderlyingData getUnderlying(int id) {
		for(int i = 0; i < data.size(); i++) {
			if(id == data.get(i).getId()) return data.get(i);
		}
		
		//this should never happen, but if it does i'll know
		System.err.println("GET UNDERLYING ERROR IN DataManager.java " + id);
		System.exit(0);
		return null;
	}
	
	public int isAlive(int id) { //grab status of the data
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).getId() == id) {
				if(data.get(i).memBased()) return 1;
				else return 2;
			}
		}
		return 0;
	}
	
}
