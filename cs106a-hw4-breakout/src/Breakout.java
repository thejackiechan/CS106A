//Partners Jackie Chan and Meghana Rao
//This program allows the user to play games of Breakout
//When the program is opened, rows of various colored bricks appear along with a ball and paddle
//The user moves the paddle by moving the mouse, and every time the paddle contacts the ball, the ball bounces off towards the bricks
//The goal of the game is to clear all the bricks
//The user has 3 lives, and they lose a life every time the ball hits the bottom of the window

import acm.graphics.*;     // GOval, GRect, etc.
import acm.program.*;      // GraphicsProgram
import acm.util.*;         // RandomGenerator
import java.applet.*;      // AudioClip
import java.awt.*;         // Color
import java.awt.event.*;   // MouseEvent
import java.util.*;        // ArrayList

public class Breakout extends BreakoutProgram {
	private GRect paddle;
	private double vx;
	private double vy;
	private int score;
	private int bricksLeft;
	private ArrayList<GRect> storeBricks;


	//The run method has all the steps necessary to play the game
	//It implements mouse listeners to track the user's mouse and also creates the paddle and bricks
	//It also creates the ball and display label that says if you win or lose
	//Within the while loop, the number of turns left is tracked and reduced accordingly
	// An if statement controls if the user can play again 
	public void run() {
		int turns = NTURNS;
		bricksLeft = NBRICKS_PER_ROW*NBRICK_ROWS;
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		storeBricks = new ArrayList<GRect>();
		addMouseListeners();
		createBricks();
		createPaddle();
		GOval ball = ball();
		GLabel display = addDisplayStats(turns);

		while(turns != 0){
			bouncingBall(ball, display, turns);
			if(bricksLeft != 0){
				turns--;
			}
			stats(turns, display);

			if(turns != 0 && bricksLeft != 0){
				waitForClick();
			}
			if(turns == 0 && bricksLeft != 0){
				lose(turns, display, ball);
			}
		}
	}

	//This method displays whether the user has won or lost 
	//It is simply to reduce redundancy in the win and lose respective methods 
	public void winOrLoseDisplay(int turns, GLabel display, GOval ball){
		stats(turns, display);
		remove(ball);
		remove(paddle);
	}

	//This method does the actual display if the user has won the game
	//It creates the new label and sets the appropriate font and location 
	public void win(int turns, GLabel display, GOval ball){
		winOrLoseDisplay(turns, display, ball);
		GLabel win = new GLabel("YOU WIN!", 0,0);
		add(win);
		win.setFont(SCREEN_FONT);
		win.setLocation(getWidth()/2 - win.getWidth()/2, getHeight()/2 + win.getHeight()/2);
	}

	//This method checks if the user has lost and then a for loop resets the bricks before the label is displayed
	public void lose(int turns, GLabel display, GOval ball){
		winOrLoseDisplay(turns, display, ball);

		for(int i = 0; i <= storeBricks.size() - 1 ; i++){
			storeBricks.get(i).setFilled(false);
		}
		displayLose();
	}

	//This method displays the loser label 
	public void displayLose(){
		GLabel lose = new GLabel("GAME OVER.", 0,0);
		add(lose);
		lose.setFont(SCREEN_FONT);
		lose.setLocation(getWidth()/2 - lose.getWidth()/2, getHeight()/2 + lose.getHeight()/2);
	}

	//This method prepares the stats from the game, taking in turns from earlier 	
	public void stats(int turns, GLabel display){
		display.setLabel("Score: " + score + ", Turns: " + turns);
	}

	//This method does the displaying of the stats from earlier 
	public GLabel addDisplayStats(int turns){
		GLabel display = new GLabel("Score: " + score + ",Turns: " + turns, 0, 0);
		add(display);
		display.setFont(SCREEN_FONT);
		display.setLocation(0, display.getAscent());
		return display;
	}

	// This method tracks the collisions of the ball
	//First we get the coordinates at various positions of the ball that were given to us 
	// Then we check what the object collided with that the point is 
	// If the object is a paddle, the ball reverses direction by changing the sign on the y velocity
	// If the object is a brick, we remove the object and add to the score and subtract from the bricks left 

	public void collisions(GOval ball, GLabel display){
		GObject collider = getElementAt(ball.getX(), ball.getY(), ball.getX(), ball.getY() + 2*BALL_RADIUS, ball.getX() + 2*BALL_RADIUS , ball.getY(),ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);	

		if(collider == paddle){
			vy = -Math.abs(vy);
		}else if(collider != null && collider != display && bricksLeft != 0){
			vy = -vy;
			storeBricks.remove(collider);
			remove(collider);
			score++;
			bricksLeft--;
		}
	}

	//This method makes the ball 
	public GOval ball(){
		GOval ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2*BALL_RADIUS, 2*BALL_RADIUS);
		add(ball);
		ball.setFilled(true);
		return ball;
	}

	//This method allows the ball to move
	//A random generator is used to create the x velocity which is restricted by the given range
	//The y velocity is always 3 because it was given as such 
	// After the velocity is set, a while loop moves the ball 
	//Within the while loop, if statements check if the ball has hit the sides of the display and how to change the velocity accordingly 

	public void bouncingBall(GOval ball, GLabel display, int turns){	
		RandomGenerator velocityPicker = new RandomGenerator();
		vx = velocityPicker.nextDouble(-VELOCITY_MAX, VELOCITY_MAX);
		while(vx <= VELOCITY_MIN && vx >= -VELOCITY_MIN){
			vx = velocityPicker.nextDouble(-VELOCITY_MAX, VELOCITY_MAX);
		}
		vy = 3.0;

		while(true){
			ball.move(vx, vy);
			if(ball.getX() + 2*BALL_RADIUS > getWidth() || ball.getX() < 0 && bricksLeft >= 85){
				vx = -vx;
			}  
			if(ball.getY() + 2*BALL_RADIUS > getHeight() || ball.getY() < 0 && bricksLeft >= 85){
				vy = -vy;	
			}
			else if(ball.getX() + 2*BALL_RADIUS == getWidth() || ball.getX() < 0 && bricksLeft < 85){
				vx = 1.02*vx;		 //speed multiplier
				vx = -vx;
			}
			else if(ball.getY() + 2*BALL_RADIUS == getHeight() || ball.getY() < 0 && bricksLeft < 85){
				vy = 1.05*vy;                //speed multiplier 
				vy = -vy;
			}
			collisions(ball, display); // Calling the collider method 
			pause(DELAY);
			stats(turns, display);
			if(bricksLeft == 0){ // Calling the win method 
				win(turns, display, ball);
				break;
			}
			if(ball.getY() + 2*BALL_RADIUS >= getHeight()){ //This replaces the ball in the middle if you die 
				ball.setLocation(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS);
				break;
			}
		} 
	}
	// This method moves the paddle (works for Macs but not Windows for some reason)
	public void mouseMoved(MouseEvent movePaddle){
		int x = movePaddle.getX(); 

		if(x + PADDLE_WIDTH/2 < BOARD_WIDTH && x - PADDLE_WIDTH/2 > 0) { // The if statement keeps it from going outside the board 
			paddle.setLocation(x - PADDLE_WIDTH/2,getHeight() - PADDLE_Y_OFFSET); 
		}
		paddle.setFilled(true); 
	}

	//This method creates the paddle 
	public void createPaddle(){
		paddle = new GRect(PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}


	//This method creates the bricks 
	//The bricks were laid using for loops very similar to the ones we used in pyramid 
	//The various if statements allow for the right colors of the rows to exist
	//The mod 10 allows the color pattern to remain even when the number of rows fluctuates 

	public void createBricks(){
		double topLeftBrick = (getWidth() - NBRICKS_PER_ROW *(BRICK_WIDTH + BRICK_SEP))/2;
		int heightChange = BRICK_Y_OFFSET;

		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {
				GRect brick = new GRect(topLeftBrick + ((BRICK_SEP + BRICK_WIDTH) * j), heightChange, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				add(brick);
				storeBricks.add(brick);

				if(i % 10 == 0 || i % 10 == 1){
					brick.setColor(Color.RED);
				} else if(i % 10 == 2 || i % 10 == 3){
					brick.setColor(Color.ORANGE);
				} else if(i % 10 == 4 || i % 10 == 5){
					brick.setColor(Color.YELLOW);
				} else if(i % 10 == 6 || i % 10 == 7){
					brick.setColor(Color.GREEN);
				} else if(i % 10 == 8 || i % 10 == 9){
					brick.setColor(Color.CYAN);
				}
			}
			heightChange = heightChange + (BRICK_HEIGHT + BRICK_SEP);
		}
	}
}
