package strategy;

import java.util.ArrayList;

import org.jfree.chart.JFreeChart;

import data.DailyData;
import data.UnderlyingData;
import strategy.util.BruteModel;
import strategy.util.StrategyBrute;
import strategy.util.StrategyData;
import trade.Trade;

public abstract class Strategy {
	
	int inputCount = 0;
	
	//Data
	private UnderlyingData data; 
	private int totalDays = 0;
	
	//UTIL
	boolean strategyRunning = false, bruteRunning = false;
	boolean isMaster = true; //used to determine if to render charts or other gui related items
	
	//GUI related
	ArrayList<String> inputs = new ArrayList<String>(); //user inputs

	ArrayList<String> chartLabels = new ArrayList<String>();
	ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
	
	ArrayList<String> statLabels = new ArrayList<String>();
	ArrayList<Double> stats = new ArrayList<Double>();
	
	//trading related
	double deployCapital = 100000;
	double accountValue = deployCapital;
	boolean isPortfolio = false; //this is for testing many underlyings
	
	ArrayList<Trade> closedTrades = new ArrayList<Trade>();
	ArrayList<Trade> openTrades = new ArrayList<Trade>();
	
	//strategy stats related
	ArrayList<StrategyData> stratData = new ArrayList<StrategyData>(); //0 = account value, 1 = daily returns
	private double sharpeRatio, averageDailyReturn, stdevaDailyReturn;
	
	//Brute
	ArrayList<String> bruteLabels = new ArrayList<String>();
	ArrayList<StrategyBrute[]> bruteRatings = new ArrayList<>();
	ArrayList<Double> bruteRating = new ArrayList<Double>(); //used for outputs
	
	//Portoflio
	ArrayList<UnderlyingData> dataPool = new ArrayList<UnderlyingData>();
	ArrayList<Strategy> strategyPool = new ArrayList<Strategy>();
	
	public Strategy(boolean isMaster) {
		setPortfolio();
		this.isMaster = isMaster;
		
		if(isMaster) {
			if(!isPortfolio) {
				this.addChart("Account Value, no compound");
				this.addChart("Daily Return histogram");
			}
		}

		if(isPortfolio) {
			//match the data of the son strategy
			Strategy strategy = this.createPortStrategy(); 
			this.inputs = strategy.getInputLabels();
			this.statLabels = strategy.getStatLabels();
			for(int i = 0; i < statLabels.size(); i++) this.stats.add(0.0);
			bruteLabels = strategy.getBruteLabels();
			bruteRatings = strategy.getBruteOutputs(); 
			stratData = strategy.getStratData();
		} else {
			this.addStat("Daily Return");
			this.addStat("Daily Stdeva");
			this.addStat("Monthly Return");
			this.addStat("Annual Return");
			this.addStat("Sharpe");
			
			this.addStrategyData("Account Value", 1, 1);
			this.addStrategyData("Daily Return", 1, 2);
			
			this.addBruteOutput("Annual Return");
			this.addBruteOutput("Sharpe");
		}
		makeInputs(); //inputs are strategy specific 
	}
	
	abstract void setPortfolio();
	abstract void inputs(double inputs[]);
	abstract void makeInputs(); 
	abstract void startPrep();
	abstract void tradingDay(DailyData day);
	abstract void finish();
	
	abstract Strategy createPortStrategy();
	
	public void changeInputs(double d[]) {
		this.inputs(d);
		//need to change all the sub strategy inputs if portoflio
		if(isPortfolio) 
			for(int i = 0; i < strategyPool.size(); i++) 
				strategyPool.get(i).changeInputs(d);
	}
	
	//single underlying data if not a portfolio
	public void sendData(UnderlyingData d) {
		this.data = d;
		this.totalDays = d.getMaxData();
	}
	
	//data pool if the strategy is portfolio
	public void sendDataPool(ArrayList<UnderlyingData> dataPool) {
		this.dataPool = dataPool;
		strategyPool.clear();
		createPool();
	}
	
	public void runTest(boolean brute) {
		strategyRunning = true;
		if(!isPortfolio) runSingleUnderlyingTest(brute);
		else runMultiunderlying(brute);
		strategyRunning = false;
	}
	
	private void runSingleUnderlyingTest(boolean brute) {
		for(int i = 0; i < stratData.size(); i++) stratData.get(i).reset();
		
		//clear data
		accountValue = deployCapital;
		openTrades.clear();
		closedTrades.clear();
		
		//strategy prep
		startPrep();
		
		for(int i = 0; i < totalDays; i++) {  
			runTradingDay(i);
		}
		
		calculateStats();
		finish();
		outputReport();
		
	}
	
	private void runMultiunderlying(boolean brute) {
		//open trades is not used for portoflio
		closedTrades.clear();
		
		startPrep();
		
		for(int i = 0; i < strategyPool.size(); i++) {
			strategyPool.get(i).runTest(brute);
		}
		
		calculateStats();
		finish();
		outputReport();
		
	}
	
	
	private void createPool() {
		strategyPool.clear();
		for(int i = 0; i < dataPool.size(); i++) {
			Strategy strategy = createPortStrategy();
			strategy.sendData(dataPool.get(i));
			strategyPool.add(strategy);
		}
	}
	
	
	
	private void runTradingDay(int i) {
		DailyData d = data.getDailyData(i);
		if(!d.getDate().contains("2012-04-30"))tradingDay(d);
	}
	
	private void calculateStats() {
		if(!isPortfolio) {
			averageDailyReturn = stratData.get(1).getAverage(0);
			stdevaDailyReturn = stratData.get(1).getStdeva(0);
			sharpeRatio = (averageDailyReturn*252.0)/(Math.sqrt(252.0) * stdevaDailyReturn);
		} else {

			//take average of all stats
			
			averageDailyReturn = 0;
			stdevaDailyReturn = 0;
			for(int i = 0; i < strategyPool.size(); i++) {
				averageDailyReturn+=strategyPool.get(i).getStat(0)/100.0;
				stdevaDailyReturn+=strategyPool.get(i).getStat(1)/100.0;
			}
			averageDailyReturn/=(double)strategyPool.size();
			stdevaDailyReturn/=(double)strategyPool.size();
			sharpeRatio = (averageDailyReturn*252.0)/(Math.sqrt(252.0) * stdevaDailyReturn);
		
			closedTrades.clear();
			for(int i = 0; i < strategyPool.size(); i++) {
				ArrayList<Trade> trades = strategyPool.get(i).getClosedTrades();
				for(int ii = 0; ii < trades.size(); ii++)
					closedTrades.add(trades.get(ii));
			}
		}
		
		if(isPortfolio) {
			
			//take the average of all inputs
			
			ArrayList<Double> bruteRatings = new ArrayList<>();
			for(int i = 0; i < strategyPool.size(); i++) {
				ArrayList<Double> ratings = strategyPool.get(i).getBruteRating(); 
				for(int ii = 0; ii < ratings.size(); ii++) {
					if(i == 0) 
						bruteRatings.add(ratings.get(ii)); 
					else 
						bruteRatings.set(ii, bruteRatings.get(ii)+ratings.get(ii)); 
				}
			}
			
			for(int i = 0; i < bruteRatings.size(); i++) bruteRatings.set(i, bruteRatings.get(i)/(double)strategyPool.size());
			bruteRating = bruteRatings; 
			
			ArrayList<Double> strategyStats = new ArrayList<>();
			for(int i = 0; i < strategyPool.size(); i++) {
				ArrayList<Double> ratings = strategyPool.get(i).getStats(); 
				for(int ii = 0; ii < ratings.size(); ii++) {
					if(i == 0) 
						strategyStats.add(ratings.get(ii)); 
					else 
						strategyStats.set(ii, strategyStats.get(ii)+ratings.get(ii)); 
				}
			}
			
			for(int i = 0; i < strategyStats.size(); i++) strategyStats.set(i, strategyStats.get(i)/(double)strategyPool.size());
			stats = strategyStats; 
		} else {
			bruteRating.set(0, averageDailyReturn*252.0);
			bruteRating.set(1, sharpeRatio);
			
			stats.set(0, averageDailyReturn*100.0);
			stats.set(1, stdevaDailyReturn*100.0);
			stats.set(2, (averageDailyReturn*252.0)/12.0*100.0);
			stats.set(3, averageDailyReturn*252.0*100.0);
			stats.set(4, sharpeRatio);
		}
		
		
		
	}
	
	private void outputReport() {
		if(isMaster && !isPortfolio) {
			stratData.get(0).createTimeSeriesChart(0, 0, "Date", "Account Value", "Account Value");
			stratData.get(1).createHistogram(0, "Buckets", "Amount", 50, "Daily Return Histogram");
			charts.set(0, stratData.get(0).getChart());
			charts.set(1, stratData.get(1).getChart());
		}
	}
	
	void logDailyReturn(String date, double profit) {
		accountValue+=profit;
		
		stratData.get(0).addLabel(0, date);
		stratData.get(1).addLabel(0, date);
		
		stratData.get(0).addData(0, accountValue);
		stratData.get(1).addData(0, profit/deployCapital);
		stratData.get(1).addData(1, profit);
	}
	
	void openTrade(Trade trade) {
		openTrades.add(trade);
	}
	
	void closeTrade(Trade trade, int tradeId) {
		closedTrades.add(trade);
		openTrades.remove(tradeId);
	}
	
	
	public void runBrute(ArrayList<Double> startingValues, ArrayList<Double> intervals, ArrayList<Integer> amount) {
		bruteRunning = true;
		
		BruteModel model = new BruteModel();
		model.create(startingValues, intervals, amount);
		runBrute(model); 
		
		bruteRunning = false;
		System.out.println("All done boss");
	}
	
	private void runBrute(BruteModel model) {
		
		int totalSimulations = model.getTotalSimulations(); 
		ArrayList<StrategyBrute> bruteData = new ArrayList<StrategyBrute>();
		
		for(int i = 0; i < totalSimulations; i++) {
			System.out.println((double)i/(double)totalSimulations); //simple to keep track of progress, TODO move to gui
			
			double inputs[] = model.getInputs(i); 
			this.changeInputs(inputs);
			this.runTest(true);
			
			StrategyBrute data = new StrategyBrute();
			data.create(inputs);

			for(int ii = 0; ii < bruteRating.size(); ii++) data.addRating(bruteRating.get(ii));
			
			bruteData.add(data);
		}
		System.out.println("Finshed running brute test");
		
		for(int i = 0; i < bruteRatings.size(); i++) {
			bruteRatings.set(i, model.getTop20Rating(bruteData, i));
		}
	}
	
	int isTradeOpen(String name) {
		int count = 0; 
		for(int i = 0; i < openTrades.size(); i++) 
			if(name.equals(openTrades.get(i).getTradeName())) count++;
		return count;
	}

	int getFirstTradeId(String name) {
		for(int i = 0; i < openTrades.size(); i++) 
			if(name.equals(openTrades.get(i).getTradeName())) return i;
		return -1;
	}
	
	public void changeDeployCapital(double value) {this.deployCapital = value;}
	
	public ArrayList<String> getInputLabels() {return inputs;}
	public ArrayList<String> getChartLabels() {return chartLabels;}
	public ArrayList<JFreeChart> getCharts() {return charts;}
	public ArrayList<String> getStatLabels() {return statLabels;}
	public ArrayList<Double> getStats() {return stats;}
	public ArrayList<StrategyData> getStratData() {return stratData;}
	public double getStat(int i) {return stats.get(i);} 

	public ArrayList<Trade> getClosedTrades() {return closedTrades;}
	
	public ArrayList<String> getBruteLabels() {return bruteLabels;}
	public ArrayList<StrategyBrute[]> getBruteOutputs() {return bruteRatings;}
	public ArrayList<Double> getBruteRating() {return bruteRating;}
	
	public boolean isRunning() {if(strategyRunning || bruteRunning) return true; return false;}
	public boolean isPortfolio() {return this.isPortfolio;}
	
	
	//util--------------------------------------------------------------------------------------------------------
	void addChart(String s) {this.chartLabels.add(s); this.charts.add(null);}
	
	void addBruteOutput(String s) {bruteLabels.add(s); bruteRatings.add(null); bruteRating.add(0.0);}
	void addStat(String s) {statLabels.add(s); stats.add(0.0);}
	void addStrategyData(String s, int labelCount, int dataCount) {
		stratData.add(new StrategyData("Account Value"));
		for(int i = 0; i < dataCount; i++) 
			stratData.get(stratData.size()-1).createData();
		for(int i = 0; i < labelCount; i++) 
			stratData.get(stratData.size()-1).createLabel();
	}
	
}
