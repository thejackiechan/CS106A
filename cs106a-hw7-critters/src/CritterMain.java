/*
 * CS 106A Critters
 * Provides the main method for the simulation.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp, Stuart Reges
 * @version 2015/05/23
 * - initial version for 15sp
 */

import critters.gui.CritterGui;

public class CritterMain {
    public static void main(String[] args) {
        CritterGui.createGui();
    }
}



// Marty's note to self: If you change the names of the animal classes assigned,
// make sure to update ClassUtils.java's getClasses method (lines 203-214)
// and CritterClassVerifier's CLASSES_TO_CHECK_METHODS array.
// Also update retro.txt before creating the sample solution file.
