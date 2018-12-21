// Partners: Jackie Chan and Meghana Rao

import acm.program.*;

public class Rocket extends ConsoleProgram {
	private static final int SIZE = 5;

	public void run() {
		rocketTop();
		row();
		middle();
		row();
		rocketTop();
	}
	public void rocketTop() {
		for(int i = 1; i <= SIZE; i++) {
			for(int j = 1; j <= SIZE - i+2; j++) {
				print(" ");
			}	
			for(int j = 0; j <= (2 * i -1)/2; j++) {
				print("/");
			}
			for(int j = 0; j <= (2 * i -1)/2; j++) {
				print("\\");
			}

			println();
		}
	}
	public void row() {
		print(" +");
		for(int i = 1; i <= SIZE*2; i++) {
			print("=");
		}
		print("+");
		println();
	}
	public void middle() {
		for(int i = 1; i <= SIZE; i++) {
			print(" |");
			for(int j = 1; j <= SIZE-i; j++) {
				print(".");
			}
			for(int j = 0; j <= (2 * i - 1)/2; j++) {
				print("/\\");
			}
			for(int j = 1; j <= SIZE-i; j++) {
				print(".");
			}
			print("|");
			println();
		}
		for(int i = 1; i <= SIZE; i++) {
			print(" |");
			for(int j = 0; j <= i-2; j++) {
				print(".");
			}
			for(int j = 1; j <= SIZE-(i-1); j++) {
				print("\\/");
			}
			for(int j = 0; j <= i-2; j++) {
				print(".");
			}
			print("|");
			println();
		}
	}
}