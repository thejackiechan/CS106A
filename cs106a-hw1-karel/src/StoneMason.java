/*Jackie Chan  
 *This program repairs an arch by replacing missing stones with beepers.
 */

import stanford.karel.*;

public class StoneMason extends SuperKarel {

	/**
	 * Runs program file.
	 */
	public void run() {	
		while(frontIsClear()) {
			sweepAndMove();
		}
		if(frontIsBlocked()) {
			sweepColumn();
		}
	}

	/**
	 * Method repairs the 1st and every other 4th column other than the last column.
	 */
	public void sweepAndMove() {  
		sweepColumn();
		if(frontIsClear()){
			move4();
		}
	}
	/**
	 * Method checks for and replaces missing stones with beepers.
	 */
	public void safePutDown() {     
		if(noBeepersPresent()) {
			putBeeper();
		}
	}

	/**
	 * Method moves Karel back to the first row after each sweep.
	 * The precondition is that Karel must be facing east on some row. 
	 */
	public void returnHome() {      
		turnAround();
		while(frontIsClear()) {
			move();
		}
	}

	/**
	 * Method advances Karel to the next column which is on ever 4 spaces.
	 */
	public void move4() {            
		for(int i = 0; i < 4; i++) {
			move();
		}
	}
	
	/**
	 * Method repairs a single column by filling in beepers.
	 * The precondition is that Karel must be facing east.
	 * The postcondition is that Karel must be on the first row facing east.
	 */
	public void sweepColumn() {      
		turnLeft();
		safePutDown();              //post
		while(frontIsClear()) {   
			move();                 //wire
			safePutDown();          //post
		}
		returnHome();
		turnLeft();	
	}
}