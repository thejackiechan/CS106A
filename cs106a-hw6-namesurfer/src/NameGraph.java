// TODO: comment this file

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import acm.graphics.*;

public class NameGraph extends GCanvas implements NameSurferConstants {
	private NameDatabase data;

	public NameGraph(NameDatabase database) {
		data = database;
	}

	public void update() {
		int border = 2;
		int margin = 20;
		int decade = 10;
		int offset = 3;
		double newHeight = getHeight() - 2 * margin; 

		setBackground(border);
		if(data.getSelectedCount() == 0){
			removeAll();
			drawGraph(decade, margin, offset);
			drawMargins(margin, border);
		}
		drawPlotLines(newHeight, margin, border, decade);
	}

	public void setBackground(int border){
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK, border));


	}

	public void drawGraph(int decade, int margin, int offset){
		for(int i = 0; i < YEARS_OF_DATA; i++){
			GLine years = new GLine((double)getWidth()*i/YEARS_OF_DATA, margin, (double)getWidth()*i/YEARS_OF_DATA, getHeight() - margin);
			if(i % decade != 0){
				years.setColor(Color.LIGHT_GRAY);
			}
			add(years);
			if(i % decade == 0){
				GLabel decadeLabel = new GLabel(i + MIN_YEAR + "", (double)getWidth()*i/YEARS_OF_DATA + offset, getHeight() - offset);
				add(decadeLabel);
			}
		}
	}

	public void drawMargins(int margin, int border){
		GLine topLine = new GLine(0, margin, getWidth(), margin);
		topLine.setLineWidth(border);
		add(topLine);

		GLine bottomLine = new GLine(0, getHeight() - margin, getWidth(), getHeight() - margin);
		bottomLine.setLineWidth(border);
		add(bottomLine);
	}

	public void drawPlotLines(double newHeight, int margin, int border, int decade){
		for(int i = 0; i < data.getSelectedCount(); i++){ 
			Person person = data.getSelectedPerson(i);
			for(int j = MIN_YEAR; j < MAX_YEAR; j++){
				double JFactor = (double)person.getRank(j)/MAX_RANK_TO_DISPLAY;
				double JPlusOneFactor = (double)person.getRank(j+1)/MAX_RANK_TO_DISPLAY;

				if(person.getRank(j) == 0 || person.getRank(j) > MAX_RANK_TO_DISPLAY){
					JFactor = 1;
				}

				if(person.getRank(j+1) == 0 || person.getRank(j+1) > MAX_RANK_TO_DISPLAY){
					JPlusOneFactor = 1;
				}
				GLine yearLine = new GLine((double)getWidth()*(j-MIN_YEAR)/YEARS_OF_DATA, margin + newHeight*JFactor, (double)getWidth()*(j-MIN_YEAR+1)/YEARS_OF_DATA, margin + JPlusOneFactor*newHeight);
				yearLine.setColor(NAME_COLORS[i]);
				yearLine.setLineWidth(border);
				add(yearLine);

				if(j % decade == 0){
					GLabel rankingLabel = new GLabel(person.getRank(j) +"", (double)getWidth()*(j-MIN_YEAR)/YEARS_OF_DATA, margin + newHeight*JFactor);
					rankingLabel.setColor(NAME_COLORS[i]);
					add(rankingLabel);
				}
			}
		}
	}
}
