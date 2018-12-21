/*
 * CS 106A NameSurfer
 *
 * This instructor-provided file declares several constants that you should use
 * in your code files throughout this program.
 *
 * Author : Marty Stepp
 * Version: Tue 2014/05/22
 *
 * Your program should work properly with an UNMODIFIED version of this file.
 * If you want to modify this file for testing or for fun, that is your choice,
 * but when we grade your program we will do so with the original unmodified
 * version of this file, so your code must still work properly with that code.
 *
 * This file and its contents are copyright (C) Stanford University and Marty Stepp,
 * licensed under Creative Commons Attribution 2.5 License.  All rights reserved.
 */

import java.awt.*;

public interface NameSurferConstants {
	/* filename from which to read name ranking data */
	public static final String RANKS_FILENAME = "res/ranks.txt";
	
	/* starting year for which there is ranking data */
	public static final int MIN_YEAR = 1880;
	
	/* ending year for which there is ranking data */
	public static final int MAX_YEAR = 2013;
	
	/* length of the range between max/min years inclusive */
	public static final int YEARS_OF_DATA = MAX_YEAR - MIN_YEAR + 1;
	
	/* the largest rank value that should be displayed in the graph */
	public static final int MAX_RANK_TO_DISPLAY = 2000;
	
	/* the maximum number of names to show in the graph at a time */
	public static final int MAX_NAMES_TO_DISPLAY = 10;
	
	/* the color in which to display each name on the graph */
	public static final Color[] NAME_COLORS = new Color[] {
		Color.BLUE,
		Color.RED.darker(),
		Color.GREEN.darker(),
		Color.MAGENTA,
		Color.ORANGE,
		Color.YELLOW.darker(),
		Color.DARK_GRAY,
		new Color(255, 128, 128),   // pink
		Color.CYAN.darker(),
		new Color(100, 100, 0)      // brown
	};
}
