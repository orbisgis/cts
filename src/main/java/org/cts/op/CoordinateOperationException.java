package org.cts.op;

/**
 * An exception thrown because a CoordinateOperation could not be
 * found or created.
 */
public class CoordinateOperationException extends Exception {

    /**
     * Creates a new CoordinateOperationException.
     *
     * @param exception description of this exception
     */
    public CoordinateOperationException(String exception) {
        super(exception);
    }
}
