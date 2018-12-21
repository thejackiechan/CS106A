// TODO: comment this file

import java.util.*;
import acm.graphics.*;

public class ImageAlgorithms implements ImageAlgorithmsInterface {
	public void grayscale(GImage source) {
		int[][] pixels = source.getPixelArray();
		int average = 0;

		for(int r = 0; r < pixels.length; r++){
			for(int c = 0; c < pixels[r].length; c++){
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				average = (red + green + blue)/3;
				pixels[r][c] = GImage.createRGBPixel(average, average, average);
			}
		}
		source.setPixelArray(pixels);
	}

	public void negative(GImage source) {
		int[][] pixels = source.getPixelArray();
		int white = 255;
		for(int r = 0; r < pixels.length; r++){
			for(int c = 0; c < pixels[r].length; c++){
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				pixels[r][c] = GImage.createRGBPixel(white - red, white - green, white - blue);
			}
		}
		source.setPixelArray(pixels);
	}

	public void rotateLeft(GImage source) {
		int[][] pixels = source.getPixelArray();
		int[][] rotatedLeft = new int[pixels[0].length][pixels.length];
		
		for(int r = 0; r < pixels.length; r++){
				for(int c = pixels[r].length - 1; c > pixels[r].length/2; c--){   
					//for(int i = 0; i < pixels[r].length/2; i++){
					int temp = pixels[r][c];
					pixels[r][c] = pixels[r][pixels[r].length - 1 - c];
					pixels[r][pixels[r].length - 1 - c] = temp;
					
					rotatedLeft[c][r] = pixels[r][c];
					//}
				}
			}
		System.out.println(Arrays.deepToString(rotatedLeft));
		source.setPixelArray(rotatedLeft);
	}

	public void rotateRight(GImage source) {
		int[][] pixels = source.getPixelArray();
		int[][] rotatedRight = new int[pixels[0].length][pixels.length];

		for(int r = pixels[0].length - 1; r > 0; r--){
			for(int c = pixels.length - 1; c > 0; c--){
				int red = GImage.getRed(pixels[r][c]);
				int green = GImage.getGreen(pixels[r][c]);
				int blue = GImage.getBlue(pixels[r][c]);
				rotatedRight[c][r] = GImage.createRGBPixel(red, green, blue);
			}
		}
		source.setPixelArray(rotatedRight);
	}

	public void translate(GImage source, int dx, int dy) {
		// TODO: implement this method

	}

	public void blur(GImage source) {
		// TODO: implement this method

	}

	public void mystery(GImage source) {
		// TODO: implement this method

	}
}
