package strategy;

import data.DailyData;
import data.ExpirationData;
import data.OptionData;
import trade.OptionContract;
import trade.OptionSpread;
import trade.Trade;

public class SimpleStrangle extends Strategy {

	private int startDay;
	private int endDay;
	private int minStartDay;
	private double takeProfit;
	private double stopLoss;
	private double stdeva;
	
	private final double featherSize = .5;
	
	private double slippage = .025; //2.5% per transcation
	
	
	public SimpleStrangle(boolean isMaster) {
		super(isMaster);
	}

	void setPortfolio() {
		isPortfolio = false;
	}

	void inputs(double[] inputs) {
		startDay = (int)inputs[0];
		minStartDay = (int)(inputs[0]*featherSize);
		endDay = (int)inputs[1];
		takeProfit = inputs[2];
		stopLoss = inputs[3]; 
		stdeva = inputs[4];
	}

	void makeInputs() {
		inputs.add("Start Day");
		inputs.add("End Day");
		inputs.add("Take profit");
		inputs.add("Stop loss");
		inputs.add("Stdeva");
		this.addStat("Average trade profit");
	}

	void startPrep() {
		
	}

	private final String tradeName = "STRANGLE";
	private double dayPL = 0;;
	
	void tradingDay(DailyData day) {
		dayPL = 0;
		
		boolean isStrangleOpen = (this.isTradeOpen(tradeName) > 0); 
		
		if(isStrangleOpen) {
			Trade trade = openTrades.get(0);
			ExpirationData data = day.getExpireData(trade.getId(1));
			
			double currentPremium;
			
			try {
				OptionData call = data.getCall(trade.getPrice(0)), put = data.getPut(trade.getPrice(1));
				currentPremium = call.getClose()+put.getClose();
			} catch(Exception e) {currentPremium = trade.getPrice(5);}

			boolean closed = false;
			
			try {
				if(currentPremium > trade.getPrice(4)) {
					closed = true;
					currentPremium = trade.getPrice(4);
				} else if(currentPremium < trade.getPrice(3)) {
					closed = true;
					currentPremium = trade.getPrice(3);
				} else if(data.getDte() < endDay || data.getLastDay()) {
					closed = true;
				}
			} catch (Exception e) {
				System.out.println("Bad data cleaning");
				System.out.println("Current Date: " + day.getDate());
				System.out.println("Looking for expire: " + trade.getId(1));
				System.exit(0);
				
			}

			if(closed) {
				double slippageEffect = currentPremium * slippage;
				currentPremium += slippageEffect;
				
				dayPL += (currentPremium-trade.getPrice(5)) * ((double)trade.getNumber(0)*100.0); 
				
				double profit = currentPremium-trade.getPrice(2);
				profit *= ((double) trade.getNumber(0)) * 100.0;
				
				trade.closeTrade(day.getDate(), currentPremium, profit);

				this.closeTrade(trade, 0);
				tryOpen(day);
			} else {
				dayPL+= (currentPremium-trade.getPrice(5)) * ((double)trade.getNumber(0)*100.0); 
				trade.changePrice(5, currentPremium);
			}
		} else {
			tryOpen(day);
		}
		
		this.logDailyReturn(day.getDate(), dayPL);
		
	}
	
	void tryOpen(DailyData day) {
		ExpirationData data = day.getExpireData(startDay, minStartDay, true, true);
		
		if(data == null) return;
		double underlyingPrice = day.getClosePrice();
		double underlyingIv = day.getIv();
		double diff = this.getDiff(underlyingPrice, underlyingIv, data.getDte(), stdeva);
		double wantedCall = diff+underlyingPrice, wantedPut = underlyingPrice-diff;
		
		OptionData call = data.findClosestCall(wantedCall), put = data.findClosestPut(wantedPut);
		
		double openPremium = call.getClose()+put.getClose();
		double takeProfit = openPremium * this.takeProfit;
		double stopLoss = openPremium * this.stopLoss;
		
		double slippageEffect = openPremium * slippage; 
		openPremium-=slippageEffect;
		
		OptionSpread spread = new OptionSpread();
		OptionContract callContract = new OptionContract(), putContract = new OptionContract();
		callContract.create("", call, true, -1);
		putContract.create("", put, false, -1);
		spread.addOptionContract(callContract);
		spread.addOptionContract(putContract);
		
		double buyingPower = spread.getCapitalRequirement(.2);
		int size = (int) (deployCapital/buyingPower);
		
		Trade trade = new Trade();
		trade.openTrade(tradeName, day.getDate(), openPremium, size);
		
		trade.addPrice(call.getStrike());
		trade.addPrice(put.getStrike());
		trade.addPrice(openPremium);
		trade.addPrice(takeProfit);
		trade.addPrice(stopLoss); 
		trade.addPrice(openPremium); //5
		
		trade.addId(tradeName);
		trade.addId(data.getExpiration());
		
		trade.addNumber(-size);
		
		this.openTrade(trade);
	}

	void finish() {
		
		double sum = 0;
		for(int i = 0; i < closedTrades.size(); i++) {
			sum+=closedTrades.get(i).getProfit();
		}
		sum/=(double)closedTrades.size();
		this.stats.set(5, sum);
	}

	void createPool() {
		
	}
	
	Strategy createPortStrategy() {
		return null;
	}
	
	private double getDiff(double price, double iv, int dte, double stdeva) {return price * iv * Math.sqrt((double)dte/365.0)*stdeva;}
	
}
