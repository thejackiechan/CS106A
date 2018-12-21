// TODO: comment this file

import java.awt.*;
import java.io.*;
import java.util.*;

public class NameDatabase implements NameSurferConstants{
	private HashMap<String, Person> HashBoiz;
	private HashMap<String, Person> HashGurlz;
	private ArrayList<Person> selected;
	private Person entry;

	public NameDatabase() {
		HashBoiz = new HashMap<String, Person>();	
		HashGurlz = new HashMap<String, Person>();
		selected = new ArrayList<Person>();
	}

	public void readRankData(Scanner input) {
		while(input.hasNextLine()){
			String person = input.nextLine();
			entry = new Person(person);
			if(entry.getSex().equals("M")){
				HashBoiz.put(entry.getName(), entry);
			}else{
				HashGurlz.put(entry.getName(), entry);	
			}	
		}
	}

	public Person getPerson(String name, String sex) {
		char firstLetter = Character.toUpperCase(name.charAt(0));
		name = name.substring(1); 
		name = firstLetter + name; 

		if(HashBoiz.containsKey(name) && sex.equalsIgnoreCase("M")){
			return HashBoiz.get(name);	
		}else if(HashGurlz.containsKey(name) && sex.equalsIgnoreCase("F")){ 
			return HashGurlz.get(name);
		}else{
			return null;
		}	
	}

	public void select(Person person) {
		if(!selected.contains(person) && selected.size() <= MAX_NAMES_TO_DISPLAY){
			selected.add(person);
		}	
	}

	public boolean isSelected(Person person) {
		if(selected.contains(person)){
			return true;
		}else{
			return false;
		}
	}

	public void clearSelected() {
		selected.clear();
	}

	public int getSelectedCount() {
		return selected.size();
	}

	public Person getSelectedPerson(int i) {
		if(i >= 0 && i < MAX_NAMES_TO_DISPLAY){
			return selected.get(i);
		}else{
			return null;
		}
	}
}
