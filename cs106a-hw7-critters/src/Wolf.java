// Our wolf is disguised as an ant. It is a strategy of camouflage. 
//Our wolves form an L shape, covering two sides of the perimeter of the world 
//and cycle in that formation.  
//Our wolf attacks differently based on what opponent it sees, to optimize points.
import java.awt.*;
import java.util.*;
import critters.model.*;

public class Wolf extends Critter{ 
	private static int meetX; 
	private static int meetY; 
	private boolean move; 

	public Wolf(){ 
		meetX = getWidth(); 
		meetY = getHeight(); 
		move = false; 
	} 

	public Color getColor(){ 
		return Color.RED; 
	} 

	public boolean eat(){ 
		return true;
	} 

	public Attack fight(String opponent){ 
		if(opponent.equals("%")||opponent.equals("w")||opponent.equals("1")||opponent.equals("2")||opponent.equals("3")||opponent.equals("4")||opponent.equals("5")||opponent.equals("6")||opponent.equals("7")||opponent.equals("8")||opponent.equals("9"))
			return Attack.ROAR; 
		if(opponent.equals("^")||opponent.equals(">")||opponent.equals("V")||opponent.equals("<")||opponent.equals("0")){ 
			return Attack.SCRATCH;
		} 
		return Attack.POUNCE; 
	} 

	public Direction getMove(){ 
		if(move == false){ 
			if(getX() != meetX){ 
				return Direction.WEST;
			}else if(getY() != meetY){ 
				return Direction.SOUTH; 
			} 
		}
		if(getX() == meetX){ 
			move = !move; 
		} 
		if(move == true)return Direction.EAST; 
		if(getY() == 0)return Direction.SOUTH;
		return Direction.CENTER;
	} 

	public String toString(){ 
		return "%";
	}
}

