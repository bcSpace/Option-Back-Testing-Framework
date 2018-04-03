# Option-Back-Testing-Framework

### Package/class summary and architecture outline at bottom

# Summary

Currently the framework allows for quick strategy creation and testing, only a few steps are needed to start testing a strategy with this framework

	1.	Create a class that extends Strategy.java
	
	2.	In setPortfolio() set if it is a portfolio testing or not, if so set the sub strategy in createPortStrategy()
	
	3.	In the makeInputs() define all the inputs for the algorithm and any custom stats/charts you want displayed
	
	4.	startPrep() anything that needs to be done before the test is ran
	
	5.	tradingDay(DailyData day) this is were the logic for the trading algorithm goes, the backend will pass each trading day within the dataset chronologically
	
	6.	finish() calculate any custom strategy stats and display any custom charts
	
	7.	Finally add to the GUI within the Gui.java class
	
Once all those steps are complete, the framework can then be used to optimize inputs, visualize performance and conduct testing in an efficient manner. 

An example of this working is the SimpleStranlge.java class within the strategy package. 

# The Goal of the framework
The goal of the project was to streamline the process for developing systematic automated option trading strategies. The solution was to build a framework to manage all the moving parts including: 

•	Data management

•	Simulating the market

•	Testing and optimizing trading algorithm inputs

•	Visualizing performance and calculating relevant stats

# Data Management
The source of the data is from: https://datashop.cboe.com/option-quotes and is ran through a closed source data cleaner

A single underlying that has 10+ years of data usually takes up about a 0.5gb of space after cleaned.

Data is managed in these ways:

•	Data can be either instanced or loaded into memory, instanced is better for one-time large market wide tests, whereas memory based is great for optimizing inputs across a smaller pool of underlying’s

•	Data is loaded in a separate thread, when a command to load the data is sent it is added to the data loading que, if the loading thread it not already loading then it will be notified that there is new data in the que

•	Data is loaded via this command “load id ‘the id’ ‘either ‘I’ for instanced or ‘m’ for memory’”

•	Data can be cleared via this command “clear id ‘id’”

![Alt text](https://snag.gy/dVSPrj.jpg)

# Simulating the Market

Simulating the market includes a few different things: 

•	Progressing through each trading day 

•	Managing all currently open trades and logging closed trades

•	Calculating buying power reductions in various option spreads by stressing underlying price (like the way most brokers calculate buying power)

•	Managing option contract expiration and data gaps

![Alt text](https://snag.gy/f3M4K9.jpg)

# Testing and Optimizing Algorithm Inputs

Optimizing inputs is very important when building trading algorithms, this framework includes: 

•	Testing individual input sets to get a deeper dive into its performance (Testing tab)

•	Brute force testing a lot of different input combinations to find the most optimal input combination based on a certain rating for example annual return or Sharpe

•	Brute force results display the top 50 for each rating, and the inputs can be copy pasted into the brute input field on the Testing tab

•	A portfolio of underlying’s can also be brute tested for stronger results

![Alt text](https://snag.gy/vwfbR7.jpg)

![Alt text](https://snag.gy/WcmV8R.jpg)

# Visualizing Performance and Calculating Relevant Stats

This is very important for getting a better understanding of how a strategy performs and includes: 

•	Recording of stats for the strategy (the table on the Testing tab), by default these stats are recorded; Daily Return, Daily Stdeva, Monthly Return, Annual Return, and Sharpe Ratio

•	Displaying various statistics on a chart (using JFreeChart), by default; Account value with no compounding and a histogram of daily returns.

•	Strategy specific stats and charts can easily be added

![Alt text](https://snag.gy/oYVcyL.jpg)

![Alt text](https://snag.gy/sIFjlu.jpg)

# Package and Class outline

### main

This package is the entry point

•	Main: Entry point, changes look and feel to dark

•	Controller: Middleman between Data Manager and strategies 

### data

This package is for managing everything related to the data

•	DataManager: Used for managing the status of each underlying, manages the load que and loading on a separate thread

•	UnderlyingData: Master data class for a single underlying handles the actual data loading

•	DailyData: Sub of UnderlyingData, contains all underlying/expiration data for a single day

•	GeneralData: Sub of DailyData, contains underlying specific data

•	ExpirationData: Sub of DailyData, a single expiration worth of option data

•	OptionData: Sub of ExpirationData, contains all pricing information and details on a single option contract

### ui

This package is everything related to what the user sees and controls

•	Gui: Master frame, init point for Strategy Panels and strategies

•	DataManagement: User control of data and displaying of data status

•	StrategyPanel: Controlling strategy and displaying relevant data for the strategy

### strategy

This package is all the custom trading strategies and the abstract strategy class

•	Strategy: Abstract class, contains all functions for brute testing, testing, calculating stats, portfolio calculations, chart displaying and gui updating

•	SimpleStrangle: A simple strategy that will always look to have a short strangle deployed

•	SimpleStranlgePortfolio: Portfolio implementation for testing purposes 

### strategy.util

This package is the utility for the strategy

•	BruteModel: Creating all the possible input combinations when a brute test is made, and sorting based on ratings

•	StrategyBrute: Stores the inputs for the brute and the ratings

•	StrategyData: Storing data such as daily returns, deriving stats from the data and creating charts

### trade

This package is for managing trades and buying power calculations

•	Trade: Storing trade related details such as; prices, ids, numbers, details. Also stores relevant trade actions

•	OptionSpread: Used for calculating buying power on multiple option contracts

•	OptionContract: A single option contract and its data, used for calculating buying power

### stratmath

• StratMath: Simple math for finding averages, Stdeva, etc.

### loader

• FileLoader: Loading files

• FileWriter: Writing files

# Architecture outline

There are 3 main processes 

•	Data Management

	Waits for input from the GUI; from the data management panel console line and when strategies are ran
	
	When new data requests are made, they are added into a loading que which is on a separate thread. If the data is instanced based, then the data is instanced skipping the loading que
	
	The loading thread waits until notified when the loading que is 0

•	Strategy process

	The strategy will wait for input from the GUI, which will be the data to be tested and the inputs
	
	Depending on the type of request it will either run a brute test for optimizing inputs or run a single test to get a better idea of strategy performance. The tests are ran on a separate thread
	
	When the tests are done the outputs are pushed back to the GUI


•	GUI process 

	The GUI is responsible for taking user inputs and displaying information
	
	It takes users controls for running strategy tests, managing data, and selecting what data to be displayed. Also has a console for controlling data
	
	It displays key stats, inputs, charting and brute outputs 


