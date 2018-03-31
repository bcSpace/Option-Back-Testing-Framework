package strategy;

import data.DailyData;

public class SimpleStranglePortfolio extends Strategy {

	//this is very simple, all need to do is set isPortfolio to true and it is not able to test multiple underlyings and optimize inputs against multiple underlyings
	
	public SimpleStranglePortfolio(boolean isMaster) {
		super(isMaster);
	}

	void setPortfolio() {
		isPortfolio = true;
	}

	void inputs(double[] inputs) {
		
	
	}

	void makeInputs() {
	}

	void startPrep() {
	}

	void tradingDay(DailyData day) {
	}
	
	Strategy createPortStrategy() {
		return new SimpleStrangle(false);
	}
 
	void finish() {
	}

}
