/*
 * Jackie Chan
 * This program creates a checkered pattern. Karel will checker 
 * the odd rows first before proceeding to the even rows.
 */

import stanford.karel.*;

public class Checkerboard extends SuperKarel {

	/**
	 * Runs program file.
	 */
	public void run() {
		columnCase();
		while(frontIsClear()) {
			sweepOddRows();       
		}
		moveToBottomRightCorner();
		if(noBeepersPresent()) {
			sweepEven();     
		} else {
			sweepOdd();
		}
	}
	
	/**
	 * Karel places beeper on every odd row for special case of 1x8 world.
	 * The precondition is that Karel is at the bottom facing east.
	 */
	public void columnCase() {   
		if(frontIsBlocked()) {
			turnLeft();
			sweepOddRows();
		}
	}
	
	/**
	 * Moves Karel from top left to bottom right corner
	 * The postcondition is that Karel is at the bottom right corner facing east.
	 */
	public void moveToBottomRightCorner() {   
		turnAround();
		while(frontIsClear()) {             //moves Karel from top left to bottom left corner
			move();
		}
		turnLeft();
		while(frontIsClear()) {       //moves Karel from bottom left to bottom right corner
			move();
		}
	}
	
	/**
	 * Returns Karel to initial spot on row before he checkers it.
	 */
	public void returnHome() {      //returns Karel to initial spot on row before he checkers it
		turnAround();
		while(frontIsClear()) {
			move();
		}	
	}
	
	/**
	 * Moves Karel up by one unit. Method used when sweeping from bottom left.
	 * The precondition is that Karel must have a spot right above him.
	 */
	public void moveUp() {    //from bottom left corner 
		move();
		turnRight();
	}
	
	/**
	 * Method moves Karel up by one unit and is used when sweeping from the bottom right.
	 * The precondition is that Karel must have a spot right above him.
	 */
	public void moveUp2() {     
		turnLeft();
		if(frontIsClear()) {
			move();
			turnLeft();
		}
	}
	
	/**
	 * Checkers odd row.
	 * The precondition is that Karel is on the bottom left corner facing east.
	 */
	public void sweep() {              
		putBeeper();                 
		while(frontIsClear()) {
			move();
			if(frontIsBlocked()) {
				returnHome();
			} else {            //Karel checkers everything in between the first and last column
				move();
				putBeeper();
				if(frontIsBlocked()) {    //takes care of odd # of columns
					returnHome();
				}
			}
		}
	}
	
	/**
	 * Applies sweep to all odd rows(Karel will sweep and move up).
	 * The postcondition is that Karel will be at the top left corner facing north.
	 */
	public void sweepOddRows() {    
		sweep();
		turnRight();
		if(frontIsClear()) {
			move();
			if (frontIsClear()) {    //Karel checks for wall before moving up once more 
				moveUp();			//(to prevent crashing if there is an even # of rows)
			}
		}
	}
	
	/**
	 * Method is basically sweepOddRows method but for even rows.
	 */
	public void sweepSideways() {       
		sweep();
		turnLeft();
		if(frontIsClear()) {         //needed so Karel doesn't sweep the odd rows again
			move();
			if (frontIsClear()) {     //Karel checks before continuing to checker rows above 
				move();
				turnLeft();
			}
		}
	}
	
	/**
	 * Method checkers when there is an odd # of columns (ex. 5x6).
	 * The precondition is that Karel must be at the bottom right corner facing east.
	 */
	public void sweepOdd() {   
		moveUp2();
		while(frontIsClear()) {
			move();
			sweepSideways();
		}
	}
	
	/**
	 * Method checkers when there is an even # of rows (ex. 6x5).
	 * The precondition is that Karel must be at the bottom right corner facing east.
	 */
	public void sweepEven() {   
		moveUp2();
		while(frontIsClear()) {
			sweepSideways();
		}
	}
}
