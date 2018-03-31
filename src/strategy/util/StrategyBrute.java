package strategy.util;

import java.util.ArrayList;

public class StrategyBrute {
	
	//First rating is always annual return
	//Second rating is always sharpe ratio
	
	private String inputs;
	private ArrayList<Double> ratings = new ArrayList<Double>(); 
	
	public void create(double input[]) {
		for(int i = 0; i < input.length; i++) {
			if(i == 0) inputs = input[i]+"";
			else inputs += ","+input[i]; 
		}
	}
	
	public void addRating(double d) {this.ratings.add(d);}
	
	public void setRatings(ArrayList<Double> ratings) {this.ratings = ratings;}
	
	public String getInputs() {return inputs;}
	public double getRating(int i) {return ratings.get(i);}
	
	
}
