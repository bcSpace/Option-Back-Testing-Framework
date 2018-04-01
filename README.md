# Option-Back-Testing-Framework

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

![Alt text](https://snag.gy/oYVcyL.jpg)

![Alt text](https://snag.gy/sIFjlu.jpg)

![Alt text](https://snag.gy/f3M4K9.jpg)

![Alt text](https://snag.gy/vwfbR7.jpg)

![Alt text](https://snag.gy/WcmV8R.jpg)
