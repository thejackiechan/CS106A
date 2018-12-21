/*
 * CS 106A Critters
 * An exception thrown when the model is unable to instantiate a class.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.model;

public class InvalidCritterClassException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public InvalidCritterClassException(Throwable cause) {
        super(cause);
    }

    public InvalidCritterClassException(String message) {
        super(message);
    }
}

