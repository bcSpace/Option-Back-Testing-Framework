package strategy.util;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import stratmath.StratMath;

public class StrategyData {
	
	private String dataName = "";
	private ArrayList<ArrayList<String>> labels = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
	
	private JFreeChart chart;

	private final StratMath math = new StratMath();
	
	public StrategyData(String dataName) {
		this.dataName = dataName;
	}

	public void createLabel() {
		labels.add(new ArrayList<String>());
	}

	public void addLabel(int id, String s) {
		labels.get(id).add(s);
	}
	
	public void createData() {
		data.add(new ArrayList<Double>());
	}
	
	public void addData(int id, double d) {
		data.get(id).add(d);
	}
	
	public void reset() {
		for(int i = 0; i < labels.size(); i++) labels.get(i).clear();
		for(int i = 0; i < data.size(); i++) data.get(i).clear();
	
	}
	
	public void createTimeSeriesChart(int labelId, int dataId, String xName, String yName, String dataName) {
		double minPrice = 0;
		double highPrice = 0;
		
		
		TimeSeries series = new TimeSeries(dataName);
		for(int i = 0; i < labels.get(labelId).size(); i++) {
			String time = labels.get(labelId).get(i);
			int day = Integer.parseInt(time.split("-")[2]);
			int year = Integer.parseInt(time.split("-")[0]);
			int month = Integer.parseInt(time.split("-")[1]); 
			series.add(new Day(day, month, year), data.get(dataId).get(i));
		}
		
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		tsc.addSeries(series);
		chart = ChartFactory.createTimeSeriesChart(dataName, "Time", yName, tsc, true, true, false);
		
	}
	
	public void createHistogram(int dataId, String xLabel, String yLabel, int bins) {
		
		double tabs[] = new double[data.get(dataId).size()];
		
		for(int i = 0; i < tabs.length; i++){ 
			tabs[i] = data.get(dataId).get(i);
		}
		
		
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
		dataset.addSeries("Hist", tabs, bins);
		chart = ChartFactory.createHistogram(dataName, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
	}
	
	public double getAverage(int id) {
		return math.getAverage(data.get(id));
	}
	
	public double getStdeva(int id) {
		return math.getStdeva(this.getAverage(id), data.get(id));
	}
	
	public JFreeChart getChart() { return chart;}
	public String getDataName() {return dataName;}
	
}
