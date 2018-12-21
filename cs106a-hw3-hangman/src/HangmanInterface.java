/*
 * CS 106A Hangman
 * This instructor-provided file exists to force the student's program to have
 * certain methods that are required by the spec.
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

public interface HangmanInterface {
	public String createHint(String secretWord, String guessedLetters);
	public void displayHangman(int guessCount);
	public String getRandomWord(String filename);
	public void intro();
	public int playOneGame(String secretWord);
	public char readGuess(String guessedLetters);
	public void stats(int gamesCount, int gamesWon, int best);
}
