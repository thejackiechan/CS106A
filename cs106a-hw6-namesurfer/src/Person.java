// TODO: comment this file

import java.util.*;

public class Person implements NameSurferConstants {
	private String name;
	private String sex;
	private int rank;
	private int[] ranks; 

	public Person(String dataLine) {
		Scanner scan = new Scanner(dataLine);

		name = scan.next();
		sex = scan.next();
		ranks = new int[YEARS_OF_DATA];
		for(int i = 0; i < YEARS_OF_DATA; i++){
			rank = scan.nextInt();
			ranks[i] = rank;
		}
	}

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

	public int getRank(int year) {
		if(year >= MIN_YEAR && year <= MAX_YEAR){
			int i = year - MIN_YEAR; 
			return ranks[i];
		}else{
			return -1;
		}
	}

	public String toString() {
		String data = name + ", " + sex + ", " + Arrays.toString(ranks);
		return data;
	}
}
