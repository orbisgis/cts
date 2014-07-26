package org.cts.op;

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.datum.Datum;

/**
 * Exception thrown when a coordinate operation has not been found
 * or could not be calculated.
 */
public class CoordinateOperationNotFoundException extends CoordinateOperationException {

    /**
     * Creates a new CoordinateOperationNotFoundException.
     *
     * @param exception description of this exception
     */
    public CoordinateOperationNotFoundException(String exception) {
        super(exception);
    }

    /**
     * Creates a new CoordinateOperationNotFoundException because no
     * CoordinateOperation has been found from source Datum to target Datum.
     */
    public CoordinateOperationNotFoundException(Datum source, Datum target) {
        super("No CoordinateOperation has been found to convert coordinates from\n" + source + " to\n" + target);
    }

    /**
     * Creates a new CoordinateOperationNotFoundException because no
     * CoordinateOperation has been found from source CoordinateReferenceSystem
     * to target CoordinateReferenceSystem.
     */
    public CoordinateOperationNotFoundException(CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        super("No CoordinateOperation has been found to convert coordinates from\n" + source + " to\n" + target);
    }
}
