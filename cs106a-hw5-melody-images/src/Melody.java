// TODO: comment this file

import java.util.*;
import java.io.*;
import stanford.cs106.audio.*;

public class Melody implements MelodyInterface {
	private Note[] song;
	private String title;
	private String artist;
	private int noteLength;
	private int OCTAVE_MIN = 1;
	private int OCTAVE_MAX = 10;

	public Melody(Scanner input) {
		title = input.nextLine();
		artist = input.nextLine();
		noteLength = input.nextInt();
		song = new Note[noteLength];
		double duration = 0; 
		int octave = 0;
		boolean repeat;

		for(int i = 0; i < noteLength; i++){
			duration = input.nextDouble();
			String p = input.next();
			Pitch pitch = Pitch.valueOf(p);

			if(!p.equals("R")){
				octave = input.nextInt();
				String a = input.next();
				Accidental accidental = Accidental.valueOf(a);
				repeat = input.nextBoolean();
				Note note = new Note(duration, pitch, octave, accidental, repeat);
				song[i] = note;
			}else{
				repeat = input.nextBoolean();
				Note rest = new Note(duration, repeat);
				song[i] = rest;
			}
		}
	}

	public void changeDuration(double ratio) {
		for(int i = 0; i < noteLength; i++){
			song[i].setDuration(ratio * song[i].getDuration());
		}
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public double getTotalDuration() {
		double songLength = 0;
		double repeatDuration = 0;

		for(int i = 0; i < noteLength; i++){
			songLength += song[i].getDuration();
		}
		for(int j = 0; j < noteLength; j++){
			if(song[j].isRepeat() == true){
				repeatDuration += song[j].getDuration();
				j++;
				while(song[j].isRepeat() == false){
					repeatDuration += song[j].getDuration();
					j++;
				}
				repeatDuration += song[j].getDuration();
			}
		}
		songLength += repeatDuration;
		return songLength;
	}



	public boolean octaveDown() {
		for(int i = 0; i < noteLength; i++){
			if(song[i].getOctave() == OCTAVE_MIN){
				return false;
			}
			song[i].setOctave(song[i].getOctave() - 1);	
		}
		return true;

	}

	public boolean octaveUp() {
		for(int i = 0; i < noteLength; i++){
			if(song[i].getOctave() == OCTAVE_MAX){
				return false;
			}
			song[i].setOctave(song[i].getOctave() + 1);
		}
		return true;
	}

	public void play() {   
		int j = 0; 
		int k = 0; 

		for(int i = 0; i < noteLength; i++){ 
			while(i < noteLength){
				if(song[i].isRepeat() != true){ 
					song[i].play(); 
					i++;
				} else if(song[i].isRepeat() == true){ 
					j = i; 
					song[i].play(); 
					i++;

					while(song[i].isRepeat() == false){          //goes through all non-repeats 
						song[i].play();
						i++;
					}    
					if(song[i].isRepeat() == true) { 
						song[i].play();  
						k = i; 
					} 
					for(int l = j; l <= k; l++) {  // supposed to play the repeat section a second time 
						song[l].play(); 
					} 
					i = k+1; 
				} 
			}
		}
	}

	public void reverse() {
		for(int i = 0; i < noteLength/2; i++){
			Note temp = song[i];
			song[i] = song[noteLength - 1 - i];
			song[noteLength - 1 - i] = temp;
		}
	}

	public String toString() {
		return Arrays.toString(song);
	}
}
