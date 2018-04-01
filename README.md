# Option-Back-Testing-Framework

### Package and Class outline at bottom

# Summary


# The Goal
The goal of the project was to streamline the process for developing systemic automated option trading strategies. The solution was to build a framework to manage all the moving parts including: 

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






