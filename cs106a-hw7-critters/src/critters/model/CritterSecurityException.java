/*
 * CS 106A Critters
 * An exception thrown when a critter's code locks up and times out.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.model;

public class CritterSecurityException extends RuntimeException {
    private static final long serialVersionUID = 0;

    public CritterSecurityException(String message) {
        super(message);
    }
}
