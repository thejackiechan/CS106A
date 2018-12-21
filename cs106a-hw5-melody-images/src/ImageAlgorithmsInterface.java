/**
 * CS 106A Melody Player
 *
 * This file contains several methods that you must write in your
 * ImageAlgorithms code.  This file is here to make sure that your class
 * has all of the methods that we asked for in the spec; else it will not
 * compile.
 *
 * You should not modify the contents of this file in any way.
 * Your program should work properly with an UNMODIFIED version of this file.
 * 
 * author: Marty Stepp
 * version: 2015/05/09
 */

import acm.graphics.GImage;

public interface ImageAlgorithmsInterface {
	public void grayscale(GImage source);
	public void negative(GImage source);
	public void rotateLeft(GImage source);
	public void rotateRight(GImage source);
	public void translate(GImage source, int dx, int dy);
	public void blur(GImage source);
	public void mystery(GImage source);
}
