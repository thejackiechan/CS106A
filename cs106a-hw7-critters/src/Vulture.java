//This is the vulture critter. Since it is similar to bird, we chose to have it 
//extend bird instead of critter as its superclass. 
//We did not have to write a toString method for the vulture because it is the 
//same as bird.
import java.awt.*;
import java.util.*;
import critters.model.*;

//Here we create a field to track whether the vulture is hungry or not.
public class Vulture extends Bird{
	private boolean hungry;

//We initialize hungry to be true.
	public Vulture(){
		hungry = true;
	}

//The vulture is always black, so we return black.
	public Color getColor(){
		return Color.BLACK;
	}

//The vulture starts hungry (as initialized). After eating once, hungry is set as
//false, and the vulture is no longer hungry until hungry is set as true again in the fight.
	public boolean eat(){
		if(hungry == true){
			hungry = false;
			return true;
		}else{
			return false;
		}
	}

//Once this method has been entered, the vulture becomes hungry again.
//If the vulture sees an ant, it responds with roar, and pounces otherwise.
	public Attack fight(String opponent){
		hungry = true;
		Attack attack = super.fight(opponent);
		return attack;
	}
}
