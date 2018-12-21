/**
 * CS 106A Melody Player
 *
 * This file contains several methods that you must write in your
 * Melody code.  This file is here to make sure that your class has all of
 * the methods that we asked for in the spec; else it will not compile.
 *
 * You should not modify the contents of this file in any way.
 * Your program should work properly with an UNMODIFIED version of this file.
 * 
 * author: Marty Stepp
 * version: 2015/05/09
 */

public interface MelodyInterface {
	public void changeDuration(double ratio);
	public String getArtist();
	public String getTitle();
	public double getTotalDuration();
	public boolean octaveDown();
	public boolean octaveUp();
	public void play();
	public void reverse();
}
