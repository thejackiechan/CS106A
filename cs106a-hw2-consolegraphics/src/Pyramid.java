// TODO: comment this program

import acm.graphics.*;
import acm.program.*;
import java.awt.*;

public class Pyramid extends GraphicsProgram {
	private static final int BRICK_WIDTH = 70;
	private static final int BRICK_HEIGHT = 50;
	private static final int BRICKS_IN_BASE = 7;
	public void run() {
		double bottomLeftBrick = (getWidth()-BRICKS_IN_BASE*BRICK_WIDTH)/2;
		int heightChange = getHeight() - BRICK_HEIGHT;
		int brickChange = BRICKS_IN_BASE;

		for (int i = 0; i < BRICKS_IN_BASE; i++) {
			for (int j = 0; j < brickChange; j++) {
				add (new GRect(bottomLeftBrick + BRICK_WIDTH * j, heightChange, BRICK_WIDTH, BRICK_HEIGHT));
			}
			bottomLeftBrick = bottomLeftBrick + 0.5*BRICK_WIDTH;
			heightChange = heightChange - BRICK_HEIGHT;
			brickChange--;
		}
	}
}

