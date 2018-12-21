/*
 * CS 106A Critters
 * A small testing program with a main method to test your animals.
 * Most of your testing should be done in CritterMain.java, but this smaller
 * file can help you to test simpler behavior or see a bit about how
 * the CritterMain uses your animals.
 * 
 * To use it, un-comment the body of the class.
 * (It is commented out because we didn't want it to fail to compile
 * before you had written your critter classes and their methods.)
 * 
 * This program does NOT test all aspects of your animals' behavior; you should
 * perform your own testing to make sure your animals work properly.
 *
 * If you have not finished all of the animals yet, you can comment out the
 * code for the animals you have not written.
 * In Eclipse, select a group of lines and press Ctrl-/ to comment them out.
 *
 * YOU DON'T NEED TO EDIT THIS FILE FOR YOUR ASSIGNMENT (but you may if you like).
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

import acm.program.*;

import java.awt.Color;

import critters.model.Critter;

public class MiniMain extends ConsoleProgram {
	
	public void run() {
		setFont("Monospaced-Bold-18");
		test1();
		test2();
	}
	
	// Small, very simple test (Ant only).
	public void test1() {
		println("Test 1 (Ant):");
		
		// create an Ant and move it 10 times
		Ant animal = new Ant(true);
		print(animal.toString() + " ");
		
		for (int i = 1; i <= 10; i++) {
			Critter.Direction move = animal.getMove();
			print(move + " " + animal.toString() + " ");
		}
		println();
		println();
	}
	

	// A bigger test (move several animals).  You can un-comment this
	// test (delete the /* and * /) once you implement the rest of the animals.
	// You'll also need to un-comment the call to test2 up in main.
	
	public void test2() {
		println("Test 2 (all animals):");

		// Ant
		Ant ant1 = new Ant(true);
		Ant ant2 = new Ant(false);
		moves(ant1, 5);
		moves(ant2, 10);
		println();
		
		// Bird
		Bird bird1 = new Bird();
		Bird bird2 = new Bird();
		moves(bird1, 17);
		moves(bird2, 14);
		println();
		
		// Crab
		Crab crab1 = new Crab(Color.GREEN);
		Crab crab2 = new Crab(Color.YELLOW);
		moves(crab1, 1 + 2 + 3 + 4);
		println("color: " + crab1.getColor());
		moves(crab2, (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8) * 2);
		println("color: " + crab2.getColor());
		println();
		
		// Hippo (movement is random so may not match perfectly)
		Hippo hippo1 = new Hippo(4);
		Hippo hippo2 = new Hippo(0);
		moves(hippo1, 8);
		moves(hippo2, 12);
		eating(hippo1, 6);
		println();

		// Vulture
		Vulture vulture1 = new Vulture();
		Vulture vulture2 = new Vulture();
		moves(vulture1, 13);
		moves(vulture2, 8);
		println();
	}
	
	// Moves the given animal the given number of times and prints which
	// way the animal wanted to move each time.
	public void moves(Critter critter, int times) {
		print(critter.getClass().getName() + " moving " + times + " times: ");
		print(critter.toString() + " ");
		for (int i = 1; i <= times; i++) {
			Critter.Direction move = critter.getMove();
			print(move + " ");
		}
		println(critter.toString());
	}
	
	// Asks the given animal if he wants to eat the given number of times
	// and prints whether the animal wanted to eat each time.
	public void eating(Critter critter, int times) {
		print(critter.getClass().getName() + " eating " + times + " times: ");
		print(critter.toString() + " ");
		for (int i = 1; i <= times; i++) {
			boolean ate = critter.eat();
			print(ate + " " + critter.toString() + " ");
		}
		println();
	}
	
}
