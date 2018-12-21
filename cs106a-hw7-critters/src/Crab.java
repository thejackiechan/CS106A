// This is the crab critter. It has a color passe din as the constructor that determines
//its color and randomly decides when to eat with a 50% probability. 
//The crab always scratches and moves west and then east in a pyramid pattern of (1W, 2E, 3W, 
//4E, etc) as outlined in the spec.

import java.awt.*;
import acm.util.RandomGenerator;   //We created a random generator to decide when the crab eats.
import critters.model.*;

//We created fields for the color being passed in, one to track the west movement, another for 
//east movement, and one to switch on and off the up and down pyramid motion.
public class Crab extends Critter{
	private Color color;
	private int west;
	private int east;
	public boolean goUp;

//In the constructor, we initialize the fields and pass in color.
	public Crab(Color color){
		this.color = color;
		west = 0;
		east = 1;
		goUp = true;
	}

//In the color method, we return the color that was passed into the constructor.
	public Color getColor(){
		return color;
	}

//In this method, a random generator generates booleans at random. If it generates true,
//the method returns true.
	public boolean eat(){
		RandomGenerator rando = new RandomGenerator();
		if(rando.nextBoolean() == true)return true;
		return false;
	}

//In this method, we return the scratch attack, since it is consistent for the crab.
	public Attack fight(String opponent){
		return Attack.SCRATCH;
	}

//In this method, we create local variables to eliminate magic numbers. 
//We have various switches to signify when to turn the goUp switch on and off. 
//We also have resets to reset the west and east values so that going up and down 
//is possible. 
	public Direction getMove(){
		int westSwitch = 1;
		int eastSwitchUp = 1;
		int eastSwitchDown = 9;
		int westReset = 1;
		int eastDownReset = 7;
		int eastUpReset = 1;
		
		if(goUp == true){
			west++;
			if(west > east){
				east++;
				west = westReset;
			}
			if(west == westSwitch && east == eastSwitchDown){
				goUp = false;
				west = westReset;    //reset west for motion going down(and up) the pyramid
				east = eastDownReset;  //reset east for motion going down the pyramid
			}
		}else{
			west++;
			if(west > east){
				east --;
				west = westReset;
			}
			if(west == westSwitch && east == eastSwitchUp){
				goUp = true;
				west = westReset;     
				east = eastUpReset;  //reset east for motion going up the pyramid
			}
		}
		if(east % 2 == 0)return Direction.EAST;
		return Direction.WEST;
	}

//In this method, we return a "w" because it is consistently the shape of the crab.
	public String toString(){
		return "w";
	}
}