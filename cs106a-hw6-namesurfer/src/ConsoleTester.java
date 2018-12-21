/*
 * CS 106A NameSurfer
 *
 * This instructor-provided file implements a console-based testing program
 * that allows you to test the behavior of your Person and NameDatabase classes.
 *
 * Author : Marty Stepp
 * Version: Tue 2015/05/18
 * - initial version for 15sp
 * Version: Tue 2014/05/23
 * - replaced getName with getSex in printInfo
 *
 * Your program should work properly with an UNMODIFIED version of this file.
 * If you want to modify this file for testing or for fun, that is your choice,
 * but when we grade your program we will do so with the original unmodified
 * version of this file, so your code must still work properly with that code.
 *
 * This file and its contents are copyright (C) Stanford University and Marty Stepp,
 * licensed under Creative Commons Attribution 2.5 License.  All rights reserved.
 */

import java.io.*;
import java.util.*;
import acm.program.*;

public class ConsoleTester extends ConsoleProgram implements NameSurferConstants {
	// a default string of data (taken from "Lisa (F)" 's data in input file)
	private static final String DATA = "0 0 0 0 0 0 1052 0 0 0 0 0 0 0 0 0 1680 0 0 1335 0 0 0 0 1296 2066 0 1725 0 0 1701 1718 2471 1492 2059 2200 2743 2486 2332 2451 2214 2228 2427 2413 1840 2687 1464 2381 2689 1728 1300 1382 1126 1686 1252 1313 1009 956 785 772 735 738 672 590 523 451 425 388 364 318 220 177 125 111 73 48 31 26 19 10 6 2 1 1 1 1 1 1 1 1 2 3 3 5 8 8 9 12 11 13 16 18 23 27 29 36 40 40 43 55 64 83 88 100 123 144 168 194 230 282 294 336 358 372 428 494 501 574 603 691 712 703 710 774";
	
	// default name of input file name to read
	private static final String DEFAULT_INPUT_FILE = "ranks-tiny.txt";
	
	public void run() {
		setFont("Monospaced-Bold-14");
		testPerson();
		println();
		testNameDatabase();
	}
	
	/*
	 * An interactive console-based test of the Person class.
	 * Prompts the user to type the person's name/sex, creates
	 * a person with those values and the given DATA set, then
	 * prints information about that person.
	 */
	private void testPerson() {
		println("Testing Person class:");
		
		// prompt user for person's name and sex
		String name = readLine("Name (Enter to skip this test)? ").trim();
		if (name.isEmpty()) {
			return;
		}
		String sex = readLine("Sex? ").trim();
		String line = name + " " + sex + " " + DATA;
		
		// create a Person object with this name/sex and print their info
		println("Going to construct a Person object ...");
		Person person = new Person(line);
		printInfo(person);
		println("  getRank(): ");
		int minYear = readInt("  Min year? ");
		int maxYear = readInt("  Max year? ");
		for (int year = minYear; year <= maxYear; year++) {
			println("    getRank(" + year + ") = " + person.getRank(year));
		}
	}
	
	/*
	 * Prints information about the given person.
	 */
	private void printInfo(Person person) {
		if (person == null) {
			println("  null");
		} else {
			println("  getName() : " + person.getName());
			println("  getSex()  : " + person.getSex());
			println("  toString(): " + person);
		}
	}
	
	/*
	 * An interactive console-based test of the NameDatabase class.
	 * Prompts the user to type a filename, reads a NameDatabase from that file,
	 * then gives the user a console menu for calling the various methods of
	 * the NameDatabase to test their functionality.
	 */
	private void testNameDatabase() {
		println("Testing NameDatabase class:");
		
		// prompt user for input filename
		String filename = readLine("Input file (Enter for " + DEFAULT_INPUT_FILE + ")? ").trim();
		if (filename.isEmpty()) {
			filename = DEFAULT_INPUT_FILE;
		}
		filename = "res/" + filename;
		
		// create name database using that file
		NameDatabase database = new NameDatabase();
		try {
			database.readRankData(new Scanner(new File(filename)));
		} catch (IOException ioe) {
			println("File I/O error:\n" + ioe);
			return;
		}
		
		// interactive console menu to test NameDatabase methods
		while (true) {
			String action = readLine("(G)etPerson, (S)elect, (C)ount, Selected(P)erson, c(L)ear, (I)sSel, (Q)uit? ").trim().toUpperCase();
			if (action.isEmpty() || action.equals("Q")) {
				break;
			} else if (action.equals("G")) {
				Person person = find(database);
				printInfo(person);
			} else if (action.equals("S")) {
				Person person = find(database);
				printInfo(person);
				println("Trying to select this person in the database ...");
				database.select(person);
			} else if (action.equals("C")) {
				println("Selected count: " + database.getSelectedCount());
			} else if (action.equals("P")) {
				int index = readInt("Index? ");
				Person person = database.getSelectedPerson(index);
				println("Selected person at index " + index  +":");
				println(person);
				println();
			} else if (action.equals("L")) {
				println("Clearing selected persons ...");
				database.clearSelected();
			} else if (action.equals("I")) {
				Person person = find(database);
				println("Is this person selected? " + database.isSelected(person));
			}
		}
	}

	/*
	 * Prompts the user for a name/sex, then searches for a person in the
	 * NameDatabase and returns whatever person is found, or null if none.
	 */
	private Person find(NameDatabase database) {
		String name = readLine("Name? ").trim();
		String sex = readLine("Sex? ").trim();
		println("Trying to find this person in the database ...");
		Person person = database.getPerson(name, sex);
		return person;
	}
}
