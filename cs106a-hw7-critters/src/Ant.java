//This is the ant critter. None of its methods are from the default class, so they are all overridden. 
//The ant is a red % sign that always eats and scratches when attacked. 
//If the Ant receives a walkSouth value of true, it then goes S, E, S, E. 
//IF the ant has a walkSouth value of false, it goes N, E, N, E.

import java.awt.*;

import critters.model.*;

//We make the fields for walkSouth and tracking the number of moves.
public class Ant extends Critter{
	private boolean walkSouth;
	private int moves;

//Here, we pass in walkSouth to the ant constructor and initialize the number of moves.
	public Ant(boolean walkSouth) {
		this.walkSouth = walkSouth;
		moves = 0;
	}

//This method makes the ant red.
	public Color getColor(){
		return Color.RED;
	}

//This method makes the ant always eat by always returning true.
	public boolean eat(){
		return true;
	}

//This method always returns the scratch attack when it is passed in an opponent.
	public Attack fight(String opponent){
		return Attack.SCRATCH;
	}

//This method moves the ant. It checks if walkSouth is true or false, and then proceeds to pick the first move 
//as South or North accordingly, followed by East. 
//The counter makes the move method loop and alternate by resetting the value to 1.
	public Direction getMove(){
		int movesBeforeReset = 2; 
		int moveReset = 1;

		moves++;
		if(moves > movesBeforeReset) moves = moveReset;
		if(walkSouth == true && moves <= moveReset){
			return Direction.SOUTH;
		}else if(walkSouth == false && moves <= moveReset){ 
			return Direction.NORTH;	
		}
		return Direction.EAST;
	}

	//This method makes the ant be displayed as a "%"
	public String toString(){
		return "%";
	}
}
