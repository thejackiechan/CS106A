/*
 * CS 106A Hangman
 * This file exists to force the student's program to have certain methods
 * that are required by the spec.
 * It is fine if your Hangman class has more methods
 * beyond the ones listed here.
 *
 * Author : Marty Stepp
 * Version: 2015/04/19
 *
 * Your program should work properly with an UNMODIFIED version of this file.
 * If you want to modify this file for testing or for fun, that is your choice,
 * but when we grade your program we will do so with the original unmodified
 * version of this file, so your code must still work properly with that code.
 *
 * This file and its contents are copyright (C) Stanford University and Marty Stepp,
 * licensed under Creative Commons Attribution 2.5 License.  All rights reserved.
 */

import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;

import acm.program.*;

public abstract class HangmanProgram extends ConsoleProgram implements HangmanInterface {
	private static final String FONT = "Monospaced-Bold-14";
	private static final String RESOURCES_DIRECTORY = "res";
	
	/** drawing canvas used as second console */
	protected HangmanCanvas canvas;
	private boolean clearEnabled;   // whether clearConsole(); is effectual
	
	/**
	 * Sets up the initial state of the program.
	 */
	public HangmanProgram() {
		clearEnabled = true;
	}
	
	/**
	 * Erases any text from the main console.
	 */
	public void clearConsole() {
		if (clearEnabled) {
			getConsole().clear();
		}
	}
	
	/**
	 * Initializes the state of the Hangman program.
	 * Sets up the graphical canvas console and sets main console's font.
	 */
	public void init() {
		setFont(FONT);
		canvas = new HangmanCanvas();
		add(canvas);
	}
	
	public void mergeConsoles() {
		canvas.merge(this);
	}
	
	/**
	 * Asks the user to type a file name, re-prompting until the user types a
	 * file that exists in the 'res' resources directory.
	 * The file's full path is returned as a string.
	 * @param prompt the text to display to the user
	 * @param directory the working directory in which to look for files (e.g. "res/")
	 * @return the file name typed by the user
	 */
	public String promptUserForFile(String prompt) {
		return promptUserForFile(prompt, RESOURCES_DIRECTORY);
	}
	
	/**
	 * Asks the user to type a file name, re-prompting until the user types a
	 * file that exists in the current directory.
	 * The file's full path is returned as a string.
	 * @param prompt the text to display to the user
	 * @param directory the working directory in which to look for files (e.g. "res/")
	 * @return the file name typed by the user
	 */
	public String promptUserForFile(String prompt, String directory) {
		String filename = readLine(prompt);
		while (!(new File(directory, filename).exists())) {
			println("Unable to open that file. Try again.");
			filename = readLine(prompt);
		}
		if (!directory.equals("")) {
			filename = new File(directory, filename).getAbsolutePath();
			directory = directory.replace("\\", "/");
			if (!directory.endsWith("/")) {
				directory += "/";
			}
		}
		return filename;
	}
	
	/**
	 * Turns on/off the ability to clear the console using clearConsole();
	 * @param enabled Whether to enable clearConsole();
	 */
	public void setClearConsoleEnabled(boolean enabled) {
		clearEnabled = enabled;
	}
	
}
