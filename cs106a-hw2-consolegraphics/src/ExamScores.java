// TODO: comment this program

import acm.program.*;

public class ExamScores extends ConsoleProgram {
	public void run() {
		println("This program computes stats about exam scores.");
		int score = readInt("Next exam score (or -1 to quit)? ");
		int count = 0;
		int fail = 0;
		int max = 0;
		int min = 0;
		int sum = 0;
		
		if(score == -1) {
			print("No scores were entered.");	
		} else {
		while(score != -1) {
			if(max < score) {
				max = score;
			}
			if(min > score) {
				min = score;
			}
			sum = sum + score;
			count++;
			if(score <= 59) {
				fail++;
			}
			score = readInt("Next exam score (or -1 to quit)? ");
		}
		println("Highest score = " + max);
		println("Lowest score = " + min);
		println("Average = " + (double)sum/count);
		println(fail + " " + "student(s) failed the exam.");  
		}
	}
}