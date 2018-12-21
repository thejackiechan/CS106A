/*
 * Jackie Chan
 * This program makes Karel move from his house, collect a 
 * newspaper, and return him back to his initial position.
 */

import stanford.karel.*;

public class CollectNewspaper extends SuperKarel {

	/**
	 * Runs program file.	
	 */
	public void run() {
		grabNewspaper();
		returnHome();
	}
	
	/**
	 * Karel moves from his home and picks up the newspaper.
	 * The precondition is that Karel is at the top left corner of his
	 * room facing east with a beeper right outside his house. 
	 */
	public void grabNewspaper() {   
		moveDown();
		turnLeft();
		for(int i = 0; i < 3; i++) {   
			move();
		}
		pickBeeper();
	}
	
	/**
	 * Karel moves down one unit.
	 * The precondition is that Karel must not have a wall directly underneath him.
	 */
	public void moveDown() {
		turnRight();
		move();
	}
	
	/**
	 * Karel goes back home and returns to his initial position.
	 * The postcondition is that Karel is at the top left corner of his 
	 * room facing east without a beeper right outside his house.
	 */
	public void returnHome() {    
		turnAround();
		while(frontIsClear()) {  
			move();
		}
		turnRight();
		move();
		turnRight();
	}
}


