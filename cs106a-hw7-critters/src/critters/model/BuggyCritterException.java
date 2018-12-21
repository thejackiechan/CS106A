/*
 * CS 106A Critters
 * An exception thrown when one of the student's critters does something that crashes the simulator.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.model;

public class BuggyCritterException extends RuntimeException {
    private static final long serialVersionUID = 0;
    
    private String buggyClassName;

    public BuggyCritterException(String message, Throwable cause, String buggyClassName) {
        super(message, cause);
        this.buggyClassName = buggyClassName;
    }

    public BuggyCritterException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getBuggyClassName() {
        return buggyClassName;
    }
}
