/*
 * CS 106A Critters
 * A Stone object is displayed as S and always stays put.
 * It always picks ROAR in a fight.
 */

import java.awt.Color;

import critters.model.Critter;

public class Stone extends Critter {
	public Attack fight(String opponent) {
		return Attack.ROAR;    // good ol' ROAR... nothing beats that!
	}

	public Color getColor() {
		return Color.GRAY;     // stones are gray in color
	}

	public String toString() {
		return "S";            // the game displays a stone as an S
	}
}
