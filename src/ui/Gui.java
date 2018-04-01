package ui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import main.Controller;
import strategy.SimpleStrangle;
import strategy.SimpleStranglePortfolio;

public class Gui {
	
	private Controller controller;
	
	private JFrame frame;
	private JTabbedPane mainPanel; 
	
	private DataManagement dataPanel;
	
	public Gui(Controller c) {
		controller = c;
	}
	
	public void create() {
		frame = new JFrame("Space Capital Backtesting");
		frame.setSize(1200,800);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		frame.setLayout(new GridLayout(1,1));
		makeMainPanel();
		frame.add(mainPanel);
	}
	
	public void makeMainPanel() {
		dataPanel = new DataManagement(controller);
		
		mainPanel = new JTabbedPane();
		mainPanel.add("Data Management", dataPanel);
		mainPanel.addTab("Simple Strangle", new StrategyPanel(controller, new SimpleStrangle(true), "SimpleStrangle"));
		mainPanel.addTab("Simple Strangle Portoflio", new StrategyPanel(controller, new SimpleStranglePortfolio(true), "SimpleStranglePortfolio"));
		
		frame.repaint();
		frame.pack();
		frame.setSize(1500, 800);
		frame.setVisible(true);
	}
	
	public void updateDataTable(int id, String loadStatus) {
		dataPanel.updateTable(id, loadStatus);
	}
	
}
