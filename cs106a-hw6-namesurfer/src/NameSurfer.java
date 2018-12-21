// TODO: comment this file

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import acm.graphics.*;
import acm.gui.*;
import acm.program.*;
import stanford.cs106.gui.*;

public class NameSurfer extends Program implements NameSurferConstants {
	private JTextField name;              
	private String nameTemp;
	private Scanner input;
	private NameDatabase list;
	private JRadioButton male;
	private JRadioButton female;
	private Person newPerson;
	private JStringList nameList;
	private NameGraph graph;
	private int listCount;

	public void init() {
		GuiUtils.setSystemLookAndFeel();
		list = new NameDatabase();
		add(new JLabel("Name:"), NORTH);
		addTextField();
		addRadioButtons();
		addGraphButton();
		addClearButton();
		add(new JLabel("Names shown:"), WEST);
		nameList = new JStringList();
		add(nameList, WEST);
		addActionListeners();
	}

	public void addTextField(){
		int TEXT_FIELD_WIDTH = 16;
		name = new JTextField(TEXT_FIELD_WIDTH);
		name.setEditable(false);
		name.setText("Loading data ...");
		name.addActionListener(this);
		add(name, NORTH);
	}

	public void addRadioButtons(){
		female = new JRadioButton("Female");
		female.setMnemonic('e');
		female.setSelected(true);
		add(female, NORTH);
		male = new JRadioButton("Male");
		male.setMnemonic('M');
		add(male, NORTH);
		ButtonGroup sex = new ButtonGroup();
		sex.add(male);
		sex.add(female);
	}

	public void addGraphButton(){
		JButton graphButton = new JButton("Graph");
		graphButton.setIcon(new ImageIcon("res/icons/graph.gif"));
		graphButton.setMnemonic('G');
		add(graphButton, NORTH);	
	}

	public void addClearButton(){
		JButton clear = new JButton("Clear");
		clear.setIcon(new ImageIcon("res/icons/clear.gif"));
		clear.setMnemonic('C');
		add(clear, NORTH);
	}

	public void actionPerformed(ActionEvent event){
		String gender = "";

		if(male.isSelected()){
			gender += "M";
		}else if(female.isSelected()){
			gender += "F";
		}
		if(name.getText().length() == 0){
			JOptionPane.showMessageDialog(this, " was not found.");
		}
		if(name.getText().length() != 0){
			if(event.getActionCommand().equals("Graph") || event.getSource() == name){             
				nameTemp = name.getText().toLowerCase();
			}
			if(event.getActionCommand().equals("Clear")){
				clearData();
			}
			newPerson = list.getPerson(nameTemp,gender);
			preCheck();
			if(newPerson == null && (event.getActionCommand().equals("Graph") || event.getSource() == name)){
				JOptionPane.showMessageDialog(this, nameTemp + " was not found.");
			}else if (newPerson != null && (event.getActionCommand().equals("Graph") || event.getSource() == name) && list.getSelectedCount() < MAX_NAMES_TO_DISPLAY){
				if(!list.isSelected(newPerson)){
					nameList.addItem(nameTemp + " (" + gender + ")", NAME_COLORS[listCount]);
					listCount++;
				}
				list.select(newPerson);
				graph.update();
			}
		}
	}

	public void clearData(){
		nameList.clear();
		list.clearSelected();
		listCount = 0;
		graph.update();
	}

	public void preCheck(){
		char firstLetter = Character.toUpperCase(nameTemp.charAt(0));
		nameTemp = nameTemp.substring(1); 
		nameTemp = firstLetter + nameTemp;
	}

	public void run() {
		try{
			input = new Scanner(new File(RANKS_FILENAME));
		}catch (FileNotFoundException fnfe){
			println("Error reading the file: " + fnfe);
		}
		setup();
	}

	public void setup(){
		graph = new NameGraph(list);
		add(graph, CENTER);
		graph.update();
		list.readRankData(input);
		name.setEditable(true);
		name.setText("");
	}
}

