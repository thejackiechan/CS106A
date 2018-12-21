//Partners: Jackie Chan and Meghana Rao
//This program simulates a race between two cars. I had the idea of creating a race scene but it turned out to be more 
//complicated than I thought so I asked a dormmate for help in creating classes for each car and for the race function. 
//I realized that having two methods for racing each car wouldn't simulate a real race because one method would run before the other. 

import acm.graphics.*;
import acm.program.*;
import java.awt.*;

public class Artistry extends GraphicsProgram {
	public void run(){
		this.setBackground(Color.YELLOW);
		this.setSize(1300,500);
		Car1 c1 = new Car1();
		Car2 c2 = new Car2();
		name();
		flower1();
		flower2();
		flower3();
		createStreet();
		Race r = new Race(c1,c2);
		r.animate();
	}

	public void createStreet() {    //This creates the white break line in the street. 
		double startX = 0;
		double startY = getHeight()/2.5;
		double heightOfStreetLine = 20;
		double widthOfStreetLine = getWidth()/30;
		for(int i = 0; i < 15; i++){
			GRect divider = new GRect(startX, startY, widthOfStreetLine, heightOfStreetLine);
			add(divider);
			divider.setFilled(true);
			divider.setFillColor(Color.WHITE);
			startX += 2*widthOfStreetLine;
		}
	}

	public void name() {       //This creates the GLabel on the bottom right corner.
		GLabel names = new GLabel("Artistry by Meghana Rao and Jackie Chan", 1050, 430);
		names.setFont("SansSerif-12");
		add(names);
	}


	public void flower1() {       //Creates a flower in the background
		GOval center = new GOval(250, 250,30, 30); 
		add(center);
		center.setFilled(true); 
		center.setFillColor(Color.YELLOW);
		GOval petal1 = new GOval(260, 215, 10, 40); 
		add(petal1);
		petal1.setFilled(true); 
		petal1.setFillColor(Color.PINK); 
		GOval petal2 = new GOval(260, 275, 10, 40); 
		add(petal2);
		petal2.setFilled(true); 
		petal2.setFillColor(Color.PINK); 
		GOval petal3 = new GOval(270, 260, 40, 10); 
		add(petal3);
		petal3.setFilled(true); 
		petal3.setFillColor(Color.PINK); 
		GRect stem = new GRect(260, 260, 10, 200); 
		add(stem);
		stem.setFilled(true);
		stem.setFillColor(Color.GREEN); 
		GOval petal4 = new GOval(220, 260, 40, 10); 
		add(petal4);
		petal4.setFilled(true); 
		petal4.setFillColor(Color.PINK); 
	}

	public void flower2() {       //Creates a flower in the background
		GOval center = new GOval(550, 250,30, 30); 
		add(center);
		center.setFilled(true); 
		center.setFillColor(Color.YELLOW);
		GOval petal1 = new GOval(560, 215, 10, 40); 
		add(petal1);
		petal1.setFilled(true); 
		petal1.setFillColor(Color.PINK); 
		GOval petal2 = new GOval(560, 275, 10, 40); 
		add(petal2);
		petal2.setFilled(true); 
		petal2.setFillColor(Color.PINK); 
		GOval petal3 = new GOval(570, 260, 40, 10); 
		add(petal3);
		petal3.setFilled(true); 
		petal3.setFillColor(Color.PINK); 
		GRect stem = new GRect(560, 260, 10, 200); 
		add(stem);
		stem.setFilled(true);
		stem.setFillColor(Color.GREEN); 
		GOval petal4 = new GOval(520, 260, 40, 10); 
		add(petal4);
		petal4.setFilled(true); 
		petal4.setFillColor(Color.PINK); 
	}

	public void flower3() {       //Creates a flower in the background
		GOval center = new GOval(850, 250,30, 30); 
		add(center);
		center.setFilled(true); 
		center.setFillColor(Color.YELLOW);
		GOval petal1 = new GOval(860, 215, 10, 40); 
		add(petal1);
		petal1.setFilled(true); 
		petal1.setFillColor(Color.PINK); 
		GOval petal2 = new GOval(860, 275, 10, 40); 
		add(petal2);
		petal2.setFilled(true); 
		petal2.setFillColor(Color.PINK); 
		GOval petal3 = new GOval(870, 260, 40, 10); 
		add(petal3);
		petal3.setFilled(true); 
		petal3.setFillColor(Color.PINK); 
		GRect stem = new GRect(860, 260, 10, 200); 
		add(stem);
		stem.setFilled(true);
		stem.setFillColor(Color.GREEN); 
		GOval petal4 = new GOval(820, 260, 40, 10); 
		add(petal4);
		petal4.setFilled(true); 
		petal4.setFillColor(Color.PINK); 
	}

	class Race { //this class allows these two cars to move at the same time rather than one after the other
		private Car1 c1;
		private Car2 c2;
		public Race(Car1 c1, Car2 c2)
		{
			this.c1 = c1;
			this.c2 = c2;
		}
		public void animate() {    //this method moves the two cars
			c1.velocity = 3;
			c2.velocity = 7;
			while(c1.body1.getX() < getWidth()-c1.body1.getWidth()){
				c1.body.move(c1.velocity, 0);
				c1.body1.move(c1.velocity, 0);
				c1.body2.move(c1.velocity, 0);
				c1.wheel1.move(c1.velocity, 0);
				c1.wheel2.move(c1.velocity, 0);
				c1.spoiler1.move(c1.velocity, 0);
				c1.spoiler2.move(c1.velocity, 0);
				c1.blower1.move(c1.velocity, 0);
				c1.blower2.move(c1.velocity, 0);
				c1.bumper1.move(c1.velocity, 0);
				c1.bumper2.move(c1.velocity, 0);
				c2.body.move(c2.velocity , 0);
				c2.body1.move(c2.velocity , 0);
				c2.body2.move(c2.velocity , 0);
				c2.wheel1.move(c2.velocity , 0);
				c2.wheel2.move(c2.velocity , 0);
				c2.spoiler1.move(c2.velocity , 0);
				c2.spoiler2.move(c2.velocity , 0);
				c1.velocity += 0.15;                 //this simulates acceleration
				c2.velocity  += 0.06;
				pause(10);
			}
		}
	} 
	class Car1{            //this class is for the black car above
		GRect body; 
		GRect body1;
		GRect body2;
		GOval wheel1;
		GOval wheel2;
		GLine spoiler1;
		GLine spoiler2;
		GRect blower1;
		GRect blower2;
		GRect bumper1;
		GRect bumper2;
		double velocity;

		public Car1(){
			car();
		}
		public void car(){           //this method builds the black car
			body = new GRect(50,50,100,50);
			add(body);
			body.setFilled(true);
			body1 = new GRect(150, 80, 35, 20);
			add(body1);
			body1.setFilled(true);
			body2 = new GRect(15, 75, 35 , 25);
			add(body2);
			body2.setFilled(true);
			wheel1 = new GOval(40, 90, 25, 25);
			add(wheel1);
			wheel1.setFilled(true);
			wheel1.setFillColor(Color.LIGHT_GRAY);
			wheel2 = new GOval(140, 90, 25, 25);
			add(wheel2);
			wheel2.setFilled(true);
			wheel2.setFillColor(Color.LIGHT_GRAY);
			blower();
			bumpers();
			spoiler1 = new GLine(15, 75, 15, 65);              //Glines create spoiler on car
			add(spoiler1);
			spoiler1.setColor(Color.CYAN);
			spoiler2 = new GLine(15, 65, 23, 75);
			add(spoiler2);
			spoiler2.setColor(Color.CYAN);
		}
		public void blower(){       //creates the green blower on the hood of the black car
			blower1 = new GRect(160, 70, 10, 10);
			add(blower1);
			blower1.setFilled(true);
			blower1.setFillColor(Color.GREEN);
			blower2 = new GRect(170, 73, 3, 2);
			add(blower2);
			blower2.setFilled(true);
			blower2.setFillColor(Color.GREEN);
		}
		public void bumpers(){        //creates bumpers on the rear
			bumper1 = new GRect(10, 80, 5, 5);
			add(bumper1);
			bumper1.setFilled(true);
			bumper2 = new GRect(10, 90, 5, 5);
			add(bumper2);
			bumper2.setFilled(true);
		}
	}
	class Car2{           //this class is for the blue car below
		private GRoundRect body;
		private GRect body1;
		private GRect body2;
		private GOval wheel1;
		private GOval wheel2;
		private GLine spoiler1;
		private GLine spoiler2;
		private double velocity;


		public Car2(){
			car();
		}
		public void animate() {     //this moves the blue car
			velocity = 4;
			while(body1.getX() < getWidth()-body1.getWidth()) {
				body.move(velocity, 0);
				body1.move(velocity, 0);
				body2.move(velocity, 0);
				wheel1.move(velocity, 0);
				wheel2.move(velocity, 0);
				spoiler1.move(velocity, 0);
				spoiler2.move(velocity, 0);
				pause(10);
				velocity += 0.05;
			}
		}
		public void car(){          //this creates the blue car on the bottom
			body = new GRoundRect(50,275,100,50);
			add(body);
			body.setFilled(true);
			body.setFillColor(Color.BLUE);
			body1 = new GRect(145, 305, 35, 20);
			add(body1);
			body1.setFilled(true);
			body1.setFillColor(Color.BLUE);
			body2 = new GRect(20, 300, 35 , 25);
			add(body2);
			body2.setFilled(true);
			body2.setFillColor(Color.BLUE);
			wheel1 = new GOval(40, 310, 25, 25);
			add(wheel1);
			wheel1.setFilled(true);
			wheel1.setFillColor(Color.LIGHT_GRAY);
			wheel2 = new GOval(135, 310, 25, 25);
			add(wheel2);
			wheel2.setFilled(true);
			wheel2.setFillColor(Color.LIGHT_GRAY);
			spoiler1 = new GLine(20, 300, 20, 280);
			add(spoiler1);
			spoiler1.setColor(Color.CYAN);
			spoiler2 = new GLine(20, 280, 35, 300);
			add(spoiler2);
			spoiler2.setColor(Color.CYAN);
		}
	}
}


