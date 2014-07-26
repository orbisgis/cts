package org.cts.op;

/**
 * Created by Michaël on 11/03/14.
 */
public class TooManyIterationsException extends CoordinateOperationException {

    /**
     * Create a new NonInvertibleOperationException.
     *
     * @param exception description of this exception
     */
    public TooManyIterationsException(String exception) {
        super(exception);
    }

    /**
     * Create a new InterpolationMethodException.
     *
     * @param op the interpolation method
     */
    public TooManyIterationsException(IterativeTransformation op, int count) {
        super("Iterative process " + op.getName() + " exceeded the maximum number of iterations (" + count + ")");
    }
}
