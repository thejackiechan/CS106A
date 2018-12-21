/*
 * CS 106A Critters
 * An exception thrown when a bad direction integer is passed.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.model;


public class InvalidDirectionException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public InvalidDirectionException(int direction) {
        super(String.valueOf(direction));
    }

    public InvalidDirectionException(String message) {
        super(message);
    }
}

