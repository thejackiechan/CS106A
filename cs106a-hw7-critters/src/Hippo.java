//This is the hippo critter. 
//It is passed a "hunger" integer in the constructor which is the maximum number of 
//times the hippo will eat. 
//If the hippo can eat more, it is gray, otherwise it is white. 
//When the hippo is hungry, it scratches, and pounces otherwise.
//It moves five steps in a random direction and then picks a new direction to go five 
//steps, continuing the random direction pattern.

import java.awt.*;
import java.util.*;
import acm.util.RandomGenerator;
import critters.model.*;

//Here we make the fields that track the hunger value, amount of moves the hippo has 
//has taken, and the direction the hippo is moving in.
public class Hippo extends Critter{
	private int hunger;
	private int moves;
	private int direction;

//In the constructor we initialize the fields, setting the hunger passed in as a field.
	public Hippo(int hunger){
		this.hunger = hunger;
		moves = 0;
		direction = 0;
	}

//We return the color white if the hippo is no longer hungry (if hunger == 0)
//Otherwise, we return gray.
	public Color getColor(){
		int notHungry = 0;
		if(hunger == notHungry)return Color.WHITE;
		return Color.GRAY;
	}

	
//Here, we decrement the hunger integer and return true if it is greater than 0,
//indicating the hippo wants to eat.
	
	public boolean eat(){
		if(hunger > 0){
			hunger --;
			return true;
		}
		return false;
	}

//The choice of attack depends on hunger. 
//If the hippo is hungry (hunger > 0), it scratches; otherwise, it pounces.
	public Attack fight(String opponent){
		if(hunger > 0)return Attack.SCRATCH;
		return Attack.POUNCE;
	}

//The direction of the hippo is selected by a random generator. 
//Each random number corresponds to a direction. 
//Each time the hippo moves, an integer called moves is incremented. 
//If moves is greater than 5, it is set back to 1, because the hippo only 
//moves 5 times in each direction.
//We also create local variables to eliminate magic numbers.
	public Direction getMove(){
		int movesBeforeRepeat = 5;
		int movesBeforeMoving = 1;
		
		moves++;
		RandomGenerator rando = new RandomGenerator();
		if(moves > movesBeforeRepeat){
			moves = 1;
		}
		if(moves == movesBeforeMoving){
			direction = rando.nextInt(1,4);
		}
		if(direction == 1)return Direction.NORTH;
		else if(direction == 2)return Direction.EAST;
		else if(direction == 3)return Direction.SOUTH;
		return Direction.WEST;
	}

// Here, we return a string that displays the hunger number by concatenating it 
//with an empty string.
	public String toString(){
		String symbol = hunger + "";
		return symbol;
	}
}
