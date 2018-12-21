// Partners: Jackie Chan and Meghana Rao

import acm.program.*;

public class Hailstone extends ConsoleProgram {
	public void run() {
		println("This program displays a series of numbers");
		println("known as a \"Hailstone sequence.\"");
		int n = readInt("Enter an integer number: ");
		int count = 0;

		while(n != 1) {
			print(n + "," + " ");
			if(n%2 == 0) {
				n=n/2;
				count++;
			} else {
				n = 3*n + 1;
				count++;
			}
		} 
		if (n == 1) {
			print(n);
			count++;
			println();
		}

		println("This Hailstone sequence has length" + " " + count + ".");

	}
}

