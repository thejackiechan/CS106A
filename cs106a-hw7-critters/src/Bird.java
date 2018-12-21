//This is the bird critter.
//It is blue, never eats, roars if the opponent is an ant (pounces otherwise), and moves in a clockwise square.
//It's shape is a "v" that rotates according to its last direction.

import java.awt.*;
import critters.model.*;
import critters.model.Critter.Direction;

//Here we make a field that tracks the number of moves that the bird takes.
public class Bird extends Critter{
	private int moves;

//Here we initialize the moves.
	public Bird(){
		moves = 0;
	}

//This method makes the bird blue.
	public Color getColor(){
		return Color.BLUE;
	}

//This method makes the bird roar if the opponent is a % and pounce otherwise by 
//checking the opponent's string.
	public Attack fight(String opponent){
		if(opponent.equals("&")) return Attack.ROAR;
		return Attack.POUNCE;
	}

////This method moves the bird. 
//There is a counter that increases with each move, and if the counter exceeds 
//the max # of moves (12 required for the square). 
//The counter is reset to 1. 
//Then if the counter is less than or equal to 3 (oneSide), the bird goes North. 
//If the counter is less than or equal to 6 (2*oneSide), the bird moves East. 
//If the counter is less than or equal to 9, (3*oneSide), the bird moves South. 
//Then for the remaining moves, the bird moves west.
	public Direction getMove(){
		int oneSide = 3;
		
		moves++;
		if(moves > 4*oneSide) moves = 1;
		if(moves <= oneSide)return Direction.NORTH;
		else if(moves <= 2*oneSide)return Direction.EAST;
		else if(moves <= 3*oneSide)return Direction.SOUTH;
		return Direction.WEST;
	}

//Since moves corresponds to the direction, the shape of the bird is changed
//according to the range of moves. 
//Each if statement checks a range of moves that the direction corresponds to and 
//then returns the appropriate facing v.

	public String toString(){
		int oneSide = 3;
		
		if(moves <= oneSide)return "^";
		else if(moves <= 2*oneSide)return ">";
		else if(moves <= 3*oneSide)return "V";
		return "<";
	}
}
