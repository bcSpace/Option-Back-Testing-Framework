package ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import main.Controller;

public class DataManagement extends JPanel {
	
	private Controller controller;
	
	//id, underlying, size, loaded
	private JTable dataTable;
	private JScrollPane tableScroll;
	private DefaultTableModel tableModel;
	
	private JLabel controlLabel;
	private JTextField input;
	private JLabel output;
	private JPanel controlPanel;
	
	
	DataManagement(Controller controller) {
		this.controller = controller;
		
		tableModel = new DefaultTableModel(0,0);
		tableModel.setColumnIdentifiers(new String[] {"id","Underlying","Size","Status"});
		dataTable = new JTable(tableModel);
		tableScroll = new JScrollPane(dataTable);
		createDataTable();
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = .5;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		
		this.add(tableScroll, c);
	
		controlPanel = new JPanel();
		controlLabel = new JLabel("CommandLine");
		input = new JTextField(10);
		output = new JLabel(); //feed back to user
		
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(controlLabel);
		controlPanel.add(input);
		controlPanel.add(output);
		
		input.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  sendCommand();
			  } 
		});
		
		
		c.weighty = 0.1;
		c.gridy = 1;
		this.add(controlPanel, c);
		
	}
	
	private void createDataTable() {
		String data[] = new String[4];
		for(int i = 0; i < controller.getUnderlyingSize(); i++) {
			data[0] = ""+i;
			data[1] = controller.getUnderlyingName(i);
			data[2] = ""+controller.getUndelyingDataAmount(i);
			data[3] = ""+controller.getLoaded(i);
			tableModel.addRow(data);
		}
	}
	
	
	private void sendCommand() {
		String command = input.getText();
		input.setText("");
		if(command.length() > 0){ 
			String response = controller.sendCommand(command);
			output.setText(response);
		}
	}
	
	public void updateTable(int id, String status) {
		tableModel.setValueAt(status, id, 3);
	}
	

}
