package strategy.util;

import java.util.ArrayList;

public class BruteModel {
	
	private ArrayList<Double> startingValues;
	private ArrayList<Double> intervals;
	private ArrayList<Integer> amounts; 
	
	private int totalSimulations; 
	private double[][] inputs; 
	
	public void create(ArrayList<Double> startingValues, ArrayList<Double> intervals, ArrayList<Integer> amount) {
		this.startingValues = startingValues;
		this.intervals = intervals;
		this.amounts = amount; 
		createModel(); 
	}
	
	//get all input combinations
	private void createModel() {
		int totalSimulations = 1;
		int inputAmount = startingValues.size();
		
		ArrayList<ArrayList<Double>> intervalData = new ArrayList<ArrayList<Double>>();

		for(int i = 0; i < inputAmount; i++) { 
			totalSimulations *= amounts.get(i); 
			intervalData.add(new ArrayList<Double>()); 
			for(int ii = 0; ii < amounts.get(i); ii++) {
				intervalData.get(i).add(startingValues.get(i) + (intervals.get(i) * (double)ii)); 
			}
		}
		
		this.totalSimulations = totalSimulations;
		inputs = new double[totalSimulations][inputAmount]; 
		
		for(int i = 0; i < inputAmount; i++) {
			int amountToAdd = 1;
			for(int ii = i; ii < inputAmount; ii++) amountToAdd*=amounts.get(ii); 
			int amountPer = amountToAdd/amounts.get(i); 
			
			for(int ii = 0; ii < totalSimulations; ii++) {
				int adder1 = ii/amountToAdd; 
				int netty = ii - (adder1*amountToAdd); 
				double adder = intervalData.get(i).get(netty/amountPer); 
				inputs[ii][i] = adder; 
			}
		}
	}
	
	public StrategyBrute[] getTop20Rating(ArrayList<StrategyBrute> data, int ratingId) {
		
		System.out.println("SORTING FOR ID: "+ ratingId);
		
		ArrayList<StrategyBrute> data1 = new ArrayList<StrategyBrute>();
		for(int i = 0; i < data.size(); i++) data1.add(data.get(i));
		
		
		StrategyBrute top[] = new StrategyBrute[50]; 
		
		for(int i = 0; i < 50; i++) {
			double highest = -9999;
			int id = 0; 
			for(int ii = 0; ii < data1.size(); ii++) {
				if(data1.get(ii).getRating(ratingId) > highest) {
					id = ii;
					highest = data1.get(ii).getRating(ratingId);
				}
			}
			data1.remove(id); 
		}
		return top;
	}
	
	public double[] getInputs(int i) {return inputs[i];}
	public int getTotalSimulations() {return totalSimulations;}

}
