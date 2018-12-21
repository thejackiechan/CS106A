// TODO: comment this program

import acm.graphics.*;
import acm.program.*;
import java.awt.*;


public class Targets extends GraphicsProgram {

	public void run() {
		circle(0, 0, 5, 100);
		circle(120,40,5,200);
		circle(350,20,8,128);
	}

	public void circle(int x, int y, int z, int m) {
		for(int i = 0; i <= z; i++) {
			GOval circle = new GOval(x+((m*i)/(2*z)), y+((m*i)/(2*z)), m-(m*i)/z, m-(m*i)/z);
			add(circle);
			circle.setFilled(true);

			if(i % 2 == 0) {
				circle.setFillColor(Color.RED);
			} else {
				circle.setFillColor(Color.WHITE);
			}
		}
	}
}
