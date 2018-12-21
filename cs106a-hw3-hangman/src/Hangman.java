// TODO: comment this file

import acm.program.*;
import acm.util.*;
import java.io.*;    // for File
import java.util.*;  // for Scanner

public class Hangman extends HangmanProgram {

	public void run() {
		intro();
		String filename = promptUserForFile("Dictionary filename? ");

		int gameCount = 0;
		int gamesWon = 0;
		int bestGame = 0;
		do
		{
			canvas.clear();
			String randomWord = getRandomWord(filename);
			int guessLeft = playOneGame(randomWord);
			if(guessLeft > 0)
			{
				gamesWon++;
				println("You win, my word was " + randomWord);
			}
			else
			{
				println("You lost! My word was " + randomWord);
			}
			if(bestGame < guessLeft)
			{
				bestGame = guessLeft;
			}
			gameCount++;
		}
		while(readBoolean("Play again (Y/N)? ", "Y", "N"));
		stats(gameCount, gamesWon, bestGame);

	}

	public void intro() {
		println("Welcome to CS106A Hangman!");
		println("I will try to think of a random word.");
		println("You will try to guess its letters.");
		println("Every time you guess a letter ");
		println("that isn't in my word, a new ");
		println("part of the hanging man appears.");
		println("Guess wisely to avoid the gallows!");
	}

	public int playOneGame(String secretWord) {
		int guess = 8;
		int guessLeft = 8;
		String guessedLetters = "";    //declaring string
		String currentWord = "";
		for(int i = 0; i < secretWord.length(); i++)
		{
			currentWord += "-";
		}
		for(int i = guess; i >= 1; i--)
		{
			guessLeft--;
			println("Secret Word: " + currentWord);
			println("Your guesses: " + guessedLetters);    //insert guesses left below 
			println("Guesses left: " + i);
			displayHangman(i);
			char guessedChar = readGuess(guessedLetters);
			guessedLetters += guessedChar;
			if(secretWord.contains(guessedChar + ""))
			{
				i++;
				guessLeft++; //This adds back a guess if you guessed correctly because he integer guess decresaes 
			}
			currentWord = createHint(secretWord,guessedLetters);
			if(currentWord.equals(secretWord))
			{
				break;
			}
			canvas.clear();

		}
		displayHangman(guessLeft);
		return guessLeft;   
	}


	public String createHint(String secretWord, String guessedLetters) {
		String finalword = ""; 
		println(); 
		for(int j = 0; j <= secretWord.length() - 1; j++) {
			if(guessedLetters.contains(secretWord.charAt(j) + "")){
				finalword += secretWord.charAt(j); 
			} else {
				finalword += "-"; 
			}
		}
		if(!guessedLetters.isEmpty() && secretWord.contains(guessedLetters.charAt(guessedLetters.length() - 1) + "")) { 
			println("Correct!"); 
		} 
		else if(!guessedLetters.isEmpty() && !secretWord.contains(guessedLetters.charAt(guessedLetters.length() - 1) + ""))
		{
			println("Incorrect!");
		}
		return finalword;
	}

	public char readGuess(String guessedLetters) {
		String guess;
		while(true) {
			guess = readLine("Your guess? ");
			guess = guess.toUpperCase();

			if(guessedLetters.contains(guess) || guess.length() >1 || guess.equals(" ") || !Character.isLetter(guess.charAt(0)))	{

				if(guessedLetters.contains(guess)) {
					println("You already guessed that letter."); 
					continue;
				} 

				if(guess.length() > 1 || guess.equals(" ") || !Character.isLetter(guess.charAt(0))) {
					println("Type a single letter from A-Z.");
					continue;
				}
			}

			char g = Character.toUpperCase(guess.charAt(0)); 

			if(!guessedLetters.contains(guess) && guess.length() == 1) { 
				canvas.clear();
				return g;
			}
		}
	}

	public void displayHangman(int guessCount) {
		try{
			Scanner hangman = new Scanner(new File("res/display" + guessCount + ".txt"));
			while (hangman.hasNextLine()){
				String line = hangman.nextLine();
				canvas.println(line);
			}
		} catch (FileNotFoundException fnfe){
			println("Error reading the file: " + fnfe);
		}
	}

	public void stats(int gamesCount, int gameWon, int best) {
		println("\nOverall statistics: ");
		println("Games played: " + gamesCount);
		println("Games won: " + gameWon);
		println("Win percent: " + 100.0*gameWon/gamesCount + "%");
		println("Best game: " + best + " guess(es) remaining");
		println("\nThanks for playing");
	}

	public String getRandomWord(String filename) {
		try{
			Scanner dictionary = new Scanner(new File(filename));
			int countLines = 0;
			while(dictionary.hasNextLine()){
				dictionary.nextLine();
				countLines++;
			}
			RandomGenerator rg = new RandomGenerator();
			int randomIndex = rg.nextInt(0, countLines-1);
			dictionary = new Scanner(new File(filename));
			int position = 0;
			while(dictionary.hasNextLine())
			{
				String line = dictionary.nextLine();
				if(position == randomIndex)
				{
					return line;
				}
				position++;
			}
		} catch (FileNotFoundException fnfe){
			println("Error reading the file: " + fnfe);
		}
		return "";
	}
}
