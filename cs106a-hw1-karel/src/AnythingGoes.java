/*
 * Jackie Chan
 * Karel will fill the entire world with beepers before making a :P face 
 * (rotated 90 degrees to the right). Then he will move to the bottom right
 * corner where he will spin and drop beepers with bliss.
 */

import stanford.karel.*;

public class AnythingGoes extends SuperKarel {

/**
 * Runs program file.	
 */
	public void run() {
		while(frontIsClear() && noBeepersPresent()) {
			putBeeper();     //post
			coverWorld();
		}
		makeLeftEye();
		makeRightEye();
		makeMouth();
		makeTongue();
		returnHome();
		woopWoopBeeperVersion();
	}
	
	/**
	 * Method makes Karel covers world with beepers.
	 * The postcondition is that Karel is at the top left corner
	 * facing north.
	 */
	public void coverWorld() {        
		while(frontIsClear()) {
			move();
			putBeeper();
		}
		returnToSide();
		turnRight();
		if(frontIsClear()) {
			moveUp();
		}		
	}
	
	/**
	 * Karel moves up one unit.
	 * The precondition is that Karel must have a space in front of him.
	 */
	public void moveUp() {            
		move();
		turnRight();
	}
	
	/**
	 * Returns Karel back to column one.
	 */
	public void returnToSide() {        
		if(frontIsBlocked()) {
			turnAround();
			while(frontIsClear()) {
				move();
			}
		}
	}
	
	/**
	 * Karel removes beepers in a square and paints it cyan to make left eye.
	 * The precondition is that Karel is at the top left corner facing north.
	 */
	public void makeLeftEye() {     
		turnAround();
		for(int i = 0; i < 3; i++) {      //moves  down to row 7
			move();
		}
		turnLeft();                        
		for(int i = 0; i < 2; i++) {       //moves  right to column 3
			move();
		}
		cycle();                           //removes beepers and paints square eye
	}
	
	/**
	 * Karel removes beepers in a square and paints it cyan to make right eye.
	 * The precondition is that Karel is at point (3,7) facing east.
	 */
	public void makeRightEye() {        
		for (int i = 0; i < 4; i++) {     //moves right to column 7 
			move();
		}
		cycle();						 
	}

	/**
	 * Removes beepers in a square and then paints empty space.
	 */
	public void cycle() {		 
		for (int i = 0; i < 4; i++) {
			beeperPicking();
			paintCorner(CYAN);
		}
	}
	
	/**
	 * Method removes beepers, creating an empty square.
	 * The precondition is that there must be a beeper present.
	 */
	public void beeperPicking() {       
		move();
		turnLeft();
		pickBeeper();
	}
	
	/**
	 * Karel paints the mouth yellow.
	 * The precondition is that Karel must be at point (7,7) facing east.
	 */
	public void makeMouth() {           
		for (int i = 0; i < 2; i++) {   //moves Karel to (9,7)
			move();
		}
		turnRight();
		for (int i = 0; i < 3; i++) {    //moves Karel to (9,4)
			move();
		}
		turnRight();
		pickBeeper();                    //post
		paintCorner(YELLOW);             //post
		for (int i = 0; i < 7; i++) {    
			move();                      //wire
			pickBeeper();                //post
			paintCorner(YELLOW);		 //post
		}
		turnLeft();
		move();
		turnRight();
		turnAround();
	}
	
	/**
	 * Karel paints the tongue red.
	 * The precondition is that Karel must be at point (2,3) facing east. 
	 */
	public void makeTongue() {          
		for (int i = 0; i < 4; i++) {   //Karel moves to (6,3)
			move();
		}
		turnRight();
		for (int i = 0; i < 4; i++) {    
			beeperPicking();
			paintCorner(RED);
		}
	}
	
	/**
	 * Karel moves to bottom right corner.
	 */
	public void returnHome() {          
		while(frontIsClear()) {
			move();
			if(frontIsBlocked()) {      
				turnLeft();
				while(frontIsClear()) {
					move();
				}
			}
		}
	}
	
	/**
	 * Karel spins and drops beepers forever.
	 */
	public void woopWoopBeeperVersion() {      
		while(beepersPresent()) {
			turnLeft();
			putBeeper();
		}
	}
}


