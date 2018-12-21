/*
 *Jackie Chan
 *This program moves Karel from the bottom left corner to the midpoint
 *of the first row. 
 */

import stanford.karel.*;

public class MidpointFinder extends SuperKarel {

	/**
	 *Runs program file.
	 */
	public void run() {
		diagonal();
		while(frontIsClear()) {
			moveLShape();
		}
		putBeeper();
	}

	/**
	 *Moves Karel in a diagonal of slope 1. 
	 *The precondition is that Karel is on the bottom left corner facing east.  
	 *The postcondition is that Karel will be at the top right corner (assuming a square)
	 *facing south.
	 */
	
	public void diagonal() {
		while(frontIsClear()) {
			move();
			turnLeft();
			if(frontIsClear()) {
				move();
			}
			turnRight();
		}
		turnRight();
	}
	
	/**
	 * This descends Karel in an L-shape to the midpoint. 
	 * The precondition is that Karel is at the top right corner (of a square) 
	 * facing south. The postcondition is that Karel will be facing south at 
	 * the midpoint of the first row (assuming odd number of columns). If there is 
	 * an even number of rows, Karel may be on either side of the center squares.
	 */
	public void moveLShape() {     //descends Karel in an L-shape to the midpoint
		move();
		if(frontIsClear()) {     //checks if Karel has reached the bottom 
			move();				 //(used to tackle worlds with even # of rows)
		}
		turnRight();
		move();
		turnLeft();
	}
}
