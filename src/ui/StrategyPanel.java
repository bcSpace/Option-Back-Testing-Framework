package ui;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import data.UnderlyingData;
import loader.FileLoader;
import loader.FileWriter;
import main.Controller;
import strategy.Strategy;
import strategy.util.StrategyBrute;
import trade.Trade;

public class StrategyPanel extends JPanel implements PanelController {
	
	//every strategy panel will have three things
	
	//Input panel, all inputs for the strategy, including the data selection (scan based, underlying pool based, single underlying)
	//Strategy charting panel, charts, daily returns, strategy specific details
	//Strategy performance panel, Sharpe, Daily returns, srategy specific details
	
	//Fourth panel for brute testing stratgies to get a better idea of what works
	
	private Controller controller; 
	private Strategy strategy; 
	private String strategyName;
	
	//polish
	private final DecimalFormat formatter = new DecimalFormat("#.00"); 
	private String bruteLog = "B:\\backtester\\log\\strategyName\\brute.log";
	private String testingLog = "B:\\backtester\\log\\strategyName\\testing.log";
	
	private JTabbedPane tabedPanel;
	
	//TESTING PANEL
	private JTabbedPane testingPanel; 
	private JPanel inputPanel = new JPanel();
	private ArrayList<JTextField> inputFields = new ArrayList<JTextField>();
	private JTextField underlyingField = new JTextField(10);
	private JTextField scanField = new JTextField(15);
	private JTextField bruteDataField = new JTextField(15);
	private ChartPanel accountValuePanel = new ChartPanel(null);
	private JLabel status = new JLabel("Status: ");
	//TESTING charting
	private ArrayList<ChartPanel> chartPanels = new ArrayList<ChartPanel>();
	//TESTING stats
	private DefaultTableModel outModel;
	//TESTING trades
	private DefaultTableModel closedTradesModel;
	
	//BRUTE 
	private JTabbedPane brutePanel; 
	private ArrayList<ArrayList<JTextField>> bruteFeilds = new ArrayList<ArrayList<JTextField>>(); //inputs = starting input, interval, interval amount
	//Table outputs
	private ArrayList<DefaultTableModel> bruteTables = new ArrayList<>(); //each table will have: inputs, annual return, sharpe, rating (robust * annual return)
	private JLabel bruteStatus = new JLabel("Status: "); 
	private JTextField bruteDataSource = new JTextField(5); 
	
	public StrategyPanel(Controller c, Strategy strategy, String strategyName) {
		this.controller = c;
		this.strategy = strategy;
		this.strategyName = strategyName;
		
		bruteLog = "B:\\backtester\\log\\"+strategyName+"\\brute.log";
		testingLog = "B:\\backtester\\log\\"+strategyName+"\\testing.log";
		
		tabedPanel = new JTabbedPane();
		ArrayList<String> i = new ArrayList<String>();
		i.add("Take Profit");
		i.add("Stop Loss");
		i.add("Start Day");
		i.add("End Day");
		
		addInputs();
		createBrute();
		
		
		tabedPanel.add("Testing", testingPanel);
		tabedPanel.add("Brute", brutePanel);
		this.setLayout(new GridLayout(1,1));
		this.add(tabedPanel);
		
	}
	
	
	private void createBrute() {
		brutePanel = new JTabbedPane();
		
		JPanel masterPanel = new JPanel();
		
		JPanel inputsPanel = new JPanel();
		inputsPanel.setLayout(new GridLayout(10,1));
		
		ArrayList<JPanel> panels = new ArrayList<JPanel>();
		ArrayList<JLabel> labels = new ArrayList<JLabel>();
		ArrayList<String> inputs = strategy.getInputLabels();
		
		for(int i = 0; i < inputs.size(); i++) {
			bruteFeilds.add(new ArrayList<>());
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());
			JLabel label = new JLabel(inputs.get(i)); 
			
			JTextField starting = new JTextField(5), interval = new JTextField(5), amount = new JTextField(5);
			panel.add(label);
			panel.add(starting);
			panel.add(interval);
			panel.add(amount);
			
			
			starting.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					  runBrute();
				  } 
			});
			
			interval.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					  runBrute();
				  } 
			});
			
			amount.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					  runBrute();
				  } 
			});
			
			bruteFeilds.get(i).add(starting);
			bruteFeilds.get(i).add(interval);
			bruteFeilds.get(i).add(amount);
			inputsPanel.add(panel);
		}
		
		JButton calcSimulations = new JButton("Simulation: "), run = new JButton("Run"), logButton = new JButton("Use Last"); 
		calcSimulations.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  calcSimulations.setText("Simulation: " + bruteAmount()+"");
			  } 
		});
		
		run.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  runBrute();
			  } 
		});
		
		logButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  useBruteLog();
			  } 
		});
		
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(calcSimulations);
		buttons.add(run);
		buttons.add(logButton);
		buttons.add(bruteDataSource);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new FlowLayout());
		statusPanel.add(bruteStatus);
		
		inputsPanel.add(buttons);
		inputsPanel.add(statusPanel);
		
//		DefaultTableModel bestProfit, bestSharpe, bestRating; //each table will have: inputs, annual return, sharpe, rating (robust * annual return)
		
		JPanel outputsPanel = new JPanel();
		outputsPanel.setLayout(new GridLayout(1,1));
		
		ArrayList<JScrollPane> tableScrolls = new ArrayList<>();
		ArrayList<String> names = strategy.getBruteLabels();

		
		for(int i = 0; i < names.size(); i++) {
			DefaultTableModel model = new DefaultTableModel(0,0);
//			this.bruteTables;
			model.setColumnIdentifiers(new String[] {"Inputs", "Annual Return", "Sharpe", names.get(i)});
			tableScrolls.add(new JScrollPane(new JTable(model)));
			bruteTables.add(model);
			
			
			
		}
		
		CardLayout chartingLayout = new CardLayout();
		JPanel chartingPanel = new JPanel(chartingLayout);
		
		JComboBox chartingList = new JComboBox();
		for(int i = 0; i < names.size(); i++) {
			chartingList.addItem(names.get(i));
		}
		chartingList.setSelectedIndex(0);
		
		chartingList.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				 chartingLayout.show(chartingPanel, chartingList.getSelectedIndex()+"");
			  } 
		});
		
		for(int i = 0; i < names.size(); i++) {
			chartingPanel.add(tableScrolls.get(i), ""+i);
		}
		chartingLayout.show(chartingPanel, 0+"");
		outputsPanel.add(chartingPanel);

		
		masterPanel.setLayout(new GridBagLayout());
	
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		listPanel.add(chartingList);
		inputsPanel.add(listPanel); //adding jcombo box
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = .1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		masterPanel.add(inputsPanel, c);
		c.weightx = .9;
		c.gridx = 1;
		c.gridy = 0;
		masterPanel.add(outputsPanel, c);
		brutePanel.add(masterPanel, "Inputs"); 
	}
	
	
	public void addInputs() {
		ArrayList<String> inputs = strategy.getInputLabels();
		testingPanel = new JTabbedPane();
		inputPanel.setLayout(new GridLayout(10,1));
		for(int i = 0; i < inputs.size(); i++) {
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());
			JLabel label = new JLabel(inputs.get(i)+":");
			panel.add(label);
			JTextField feild = new JTextField(5);
			feild.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					  runStrategy();
				  } 
			});
			inputFields.add(feild);
			panel.add(feild);
			inputPanel.add(panel);
		}
		
		bruteDataField.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  String split[] = bruteDataField.getText().split(",");
				  for(int i = 0; i < split.length; i++) {
					  inputFields.get(i).setText(split[i]);
				  }
				  bruteDataField.setText("");
			  } 
		});
		
		
		JButton enter = new JButton("Start");
		enter.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  runStrategy();
			  } 
		});
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  for(int i = 0; i < inputFields.size(); i++) {
					  inputFields.get(i).setText("");
				  }
			  } 
		});
		JButton logButton = new JButton("Use last"); 
		logButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  useTestingLog();
			  } 
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(enter);
		panel.add(clear);
		panel.add(logButton);
		inputPanel.add(panel);
		
		JPanel inputsPanel = new JPanel();

		inputsPanel.setLayout(new GridLayout(1,3));
		inputsPanel.add(inputPanel);
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new GridLayout(10,1));
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		panel1.add(new JLabel("Underlying(s):"));
		panel1.add(underlyingField);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		panel2.add(new JLabel("Scan(s):"));
		panel2.add(scanField);
		
		JPanel panel4 = new JPanel();
		panel4.setLayout(new FlowLayout());
		panel4.add(new JLabel("Brute Input:"));
		panel4.add(bruteDataField);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout());
		panel3.add(status);
		dataPanel.add(panel1);
		dataPanel.add(panel2);
		dataPanel.add(panel4);
		dataPanel.add(panel3);
		inputsPanel.add(dataPanel);
		
		JPanel statPanel = new JPanel();
		
		
		outModel = new DefaultTableModel(0,0);
		outModel.setColumnIdentifiers(new String[] {"Label", "Data"});
		ArrayList<String> labels = strategy.getStatLabels();
		for(int i = 0; i < labels.size(); i++) {
			outModel.addRow(new String[] {labels.get(i),"0.0"});
		}
		
		JTable outputStats = new JTable(outModel);
		JScrollPane scrollPane = new JScrollPane(outputStats);
		statPanel.setLayout(new GridLayout(2,1));
		statPanel.add(scrollPane);
		statPanel.add(accountValuePanel);
		
		inputsPanel.add(statPanel);
		
		testingPanel.add(inputsPanel, "Inputs");
		
		
		scanField.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  runStrategy();
			  } 
		});
		
		underlyingField.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  runStrategy();
			  } 
		});
		
		
		//OUTPUT charting GUI
		JPanel chartingMasterPanel = new JPanel();
		chartingMasterPanel.setLayout(new GridBagLayout());
		//20% drop down menu, 80% chart
		JPanel chartSelector = new JPanel();
		JComboBox chartingList = new JComboBox();
		ArrayList<String> chartingTitles = strategy.getChartLabels();
		for(int i = 0; i < chartingTitles.size(); i++) {
			chartingList.addItem(chartingTitles.get(i));
		}
		
		chartSelector.setLayout(new FlowLayout());
		chartSelector.add(chartingList);
		
		CardLayout chartingLayout = new CardLayout();
		JPanel chartingPanel = new JPanel(chartingLayout);
		
		
		for(int i = 0; i < chartingTitles.size(); i++) {
			chartPanels.add(new ChartPanel(ChartFactory.createLineChart("Spice", "Years", "number", null, PlotOrientation.VERTICAL, true, true, false)));
			chartingPanel.add(chartPanels.get(chartPanels.size()-1), ""+i);
		}
		chartingLayout.show(chartingPanel, 0+"");
		
		
		
		//action listeners 
		chartingList.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				 chartingLayout.show(chartingPanel, chartingList.getSelectedIndex()+"");
			  } 
		});
		
		
		//layout and final adding
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = .1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		chartingMasterPanel.add(chartSelector, c);
		c.weightx = .9;
		c.gridx = 1;
		c.gridy = 0;
		chartingMasterPanel.add(chartingPanel, c); 
		testingPanel.add(chartingMasterPanel, "Charting");
		
		//OUTPUT chart data, trade data/stats
		JPanel tradePanel = new JPanel();
		tradePanel.setLayout(new GridLayout(1,1));

		closedTradesModel = new DefaultTableModel();
		
		closedTradesModel.setColumnIdentifiers(new String[] {"Trade Name", "Open Date", "Open Price", "Open Size", "Close Date", "Close Price", "Profit", "Details"});
		JTable closeTradeTable = new JTable(closedTradesModel);
		JScrollPane tradeScrollPane = new JScrollPane(closeTradeTable);
		
		tradePanel.add(tradeScrollPane);
		
//		outputPanel.setLayout(new FlowLayout());
//		outputPanel.add(statPanel);
		
		testingPanel.add(tradePanel, "Trades");
		
	}
	
	private void strategyFinished() {
		ArrayList<JFreeChart>  charts = strategy.getCharts();
		for(int i = 0; i < charts.size(); i++) {
			chartPanels.get(i).setChart(charts.get(i));
		}
		
		if(charts.size() > 0) accountValuePanel.setChart(charts.get(0)); //first chart is always account value
		
		ArrayList<Double> stats = strategy.getStats(); 
		for(int i = 0; i < stats.size(); i++) {
			outModel.setValueAt(formatter.format(stats.get(i)), i, 1);
		}
		outModel.fireTableDataChanged();
		
		ArrayList<Trade> closedTrades = strategy.getClosedTrades();
		closedTradesModel.setNumRows(0);
		for(int i = 0; i < closedTrades.size(); i++) {
			Trade trade = closedTrades.get(i);
			closedTradesModel.addRow(new String[] {trade.getTradeName(),trade.getOpenDate(),formatter.format(trade.getOpenPrice()),formatter.format(trade.getSize()),trade.getCloseDate(),formatter.format(trade.getClosePrice()),formatter.format(trade.getProfit()),trade.getDetails()});
		}
		closedTradesModel.fireTableDataChanged();
		
		status.setText("Status: Strategy Done");
		
		
	}
	
	
	//new double[] {.25, .25, 2.6, 45, 15, 5}
	
	private void runStrategy() {
		
		if(strategy.isRunning()) { //prevent multiple testing
			status.setText("Status: Strategy is already running");
			return;
		}
		
		status.setText("Status: Working");
		double inputs[] = new double[inputFields.size()];
		try {
			for(int i = 0; i < inputs.length; i++) {
				inputs[i] = Double.parseDouble(inputFields.get(i).getText());
			}
		} catch (Exception e) {
			status.setText("Status: Failed to parse inputs");
			return;
		}
		
		String underlyings = underlyingField.getText();
		String split[] = underlyings.split(",");
		if(underlyings.length() == 0) {
			status.setText("Status: No underlyings");
			return;
		} // TODO: CHECK FOR A SCAN HERE
		
		int underlyingIds[] = new int[split.length];
		try {
			for(int i = 0; i < underlyingIds.length; i++) {
				underlyingIds[i] = Integer.parseInt(split[i]);
			}
		} catch(Exception e) {status.setText("Status: Failed to parse underlyings"); return;}
	
		//TODO figure out how to load if needed
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		for(int i = 0; i < underlyingIds.length; i++) {
			//remove dups
			boolean canAdd = true;
			for(int ii = 0; ii < ids.size(); ii++) {
				if(ids.get(ii) == underlyingIds[i]) canAdd = false;
			}
			if(canAdd) ids.add(underlyingIds[i]); 
		}
		
		for(int i = 0; i < ids.size(); i++) {
			int status = controller.isLoaded(ids.get(i));
			if(status == -1){
				this.status.setText("Status: Data id error: " + ids.get(i));
				return;
			}
			if(status == 0) {
				System.out.println("Added data");
				controller.addUnderlying(ids.get(i), false); //instance the data if not loaded/instanced
			} else if(status == 1) {
				this.status.setText("Status: Data is still loading: " + ids.get(i));
				return;
			} //do nothing else if data is already loaded
		}
		
		logTest();
		
		
		//TODO intergrate multiple underlyings
		
		ArrayList<UnderlyingData> dataPool = new ArrayList<UnderlyingData>();
		for(int i = 0; i < ids.size(); i++) dataPool.add(controller.getData(ids.get(i)));
		System.out.println("DATA POOL SIZE: " + dataPool.size());
		
		
		if(strategy.isPortfolio()) strategy.sendDataPool(dataPool);
		else strategy.sendData(dataPool.get(0));
		
		strategy.changeInputs(inputs);
		
		new Thread(() -> {
			System.out.println("Running strategy");
			strategy.runTest(false);
			strategyFinished();
        }).start();
		
	}
	
	private void runBrute() {
		//parse all the feilds
		
		if(strategy.isRunning()) {
			bruteStatus.setText("Strategy already running");
			return;
		}
		
		ArrayList<Double> startingValues = new ArrayList<Double>();
		ArrayList<Double> valueInterval = new ArrayList<Double>();
		ArrayList<Integer> amount = new ArrayList<Integer>(); 

		try {
			
			String split[] = bruteDataSource.getText().split(",");
			ArrayList<Integer> underlyingIds = new ArrayList<>();
			for(int i = 0; i < split.length; i++) underlyingIds.add(Integer.parseInt(split[i]));
			ArrayList<UnderlyingData> dataPool = new ArrayList<>();
			
			for(int i = 0; i < underlyingIds.size(); i++) {
				if(controller.getUnderlyingStatus(underlyingIds.get(i)) != 2) {
					bruteStatus.setText("Status: Need to load data into memory: " + underlyingIds.get(i));
					return;
				}
				dataPool.add(controller.getData(underlyingIds.get(i))); 
			}
			
			if(dataPool.size() == 0) {
				bruteStatus.setText("Status: no data");
				return;
			}
			
			if(strategy.isPortfolio()) strategy.sendDataPool(dataPool);
			else strategy.sendData(dataPool.get(0));
		} catch(Exception e) {bruteStatus.setText("Status: Error parsing data"); return;}
		
		
		
		try {
			for(int i = 0; i < bruteFeilds.size(); i++) {
				startingValues.add(Double.parseDouble(bruteFeilds.get(i).get(0).getText())); 
				valueInterval.add(Double.parseDouble(bruteFeilds.get(i).get(1).getText()));
				amount.add(Integer.parseInt(bruteFeilds.get(i).get(2).getText())); 
			}
			
		} catch(Exception e) {
			bruteStatus.setText("Status: Failed to parse inputs"); 
			return;
		}
		bruteStatus.setText("Status: Running Brute");
		logBrute();
		new Thread(() -> {
			System.out.println("Running strategy");
			strategy.runBrute(startingValues, valueInterval, amount);
			bruteFinished();
        }).start();
		
		
	}
	
	private void bruteFinished() {
		ArrayList<StrategyBrute[]> topRatings = strategy.getBruteOutputs();
		for(int i = 0; i < topRatings.size(); i++) {
			bruteTables.get(i).setRowCount(0);
			for(int ii = 0; ii < topRatings.get(i).length; ii++) {
				bruteTables.get(i).addRow(new String[] {topRatings.get(i)[ii].getInputs(), topRatings.get(i)[ii].getRating(0)+"", topRatings.get(i)[ii].getRating(1)+"", topRatings.get(i)[ii].getRating(i)+""});
			}
			bruteTables.get(i).fireTableDataChanged();
		}
		bruteStatus.setText("Status: Brute Finished");
	}
	
	private void logTest() {
		FileWriter writer = new FileWriter();
		String testingContents = "";
		
		for(int i = 0; i < inputFields.size(); i++) {
			testingContents += inputFields.get(i).getText(); 
			if(i != inputFields.size()-1) testingContents+="\n";
		}
		writer.writeFile(testingLog, testingContents);
	}
	
	private void logBrute() {
		FileWriter writer = new FileWriter();
		String bruteContents = "";
		
		for(int i = 0; i < bruteFeilds.size(); i++) {
			ArrayList<JTextField> list = bruteFeilds.get(i);
			bruteContents+=list.get(0).getText()+","+list.get(1).getText()+","+list.get(2).getText();
			if(i != bruteFeilds.size()-1) bruteContents+="\n";
		}
		writer.writeFile(bruteLog, bruteContents);
	}
	
	private void useBruteLog() {
		FileLoader loader = new FileLoader();
		String[] s = loader.loadData(bruteLog);
		if(s.length == 9999) {
			bruteStatus.setText("Status: Error loading brute.log");
			return;
		}
		try {
			for(int i = 0; i < s.length; i++) {
				bruteFeilds.get(i).get(0).setText(s[i].split(",")[0]);
				bruteFeilds.get(i).get(1).setText(s[i].split(",")[1]);
				bruteFeilds.get(i).get(2).setText(s[i].split(",")[2]);
			}
		} catch(Exception e) {
			bruteStatus.setText("Status: Error parsing brute.log");
		}
		
	}
	
	private void useTestingLog() {
		FileLoader loader = new FileLoader();
		String[] s = loader.loadData(testingLog);
		if(s.length == 9999) {
			status.setText("Status: Error loading testing.log");
			return;
		}
		try {
			for(int i = 0; i < s.length; i++) {
				inputFields.get(i).setText(s[i]);
			}
		} catch(Exception e) {
			status.setText("Status: Error parsing testing.log");
		}
		
	}
	
	
	private int bruteAmount() {
		try {
			int count = 1; 
			for(int i = 0; i < bruteFeilds.size(); i++) {
				count = (count) * Integer.parseInt(bruteFeilds.get(i).get(2).getText());
			}
			return count; 
		} catch (Exception e) {return -1;}
	}
	
	
	

}
