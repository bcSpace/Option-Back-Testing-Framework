package stratmath;

import java.util.ArrayList;

public class StratMath {
	
	public double getAverage(ArrayList<Double> data)  {
		double count = data.size();
		double sum = 0;
		for(int i = 0; i < data.size(); i++) {
			sum+=data.get(i);
		}
		sum = sum/count;
		return sum;
	}
	
	public double getStdeva(double average, ArrayList<Double> data) {
		double size = data.size();
		double sum = 0;
		for(int i = 0; i < data.size(); i++) {
			sum += (average-data.get(i))*(average-data.get(i));
		}
		sum/=size;
		sum = Math.sqrt(sum);
		return sum;
	}
	
	
}
