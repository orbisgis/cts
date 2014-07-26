package org.cts.op.transformation;

import org.cts.op.CoordinateOperation;
import org.cts.op.NonInvertibleOperationException;

/**
 * An interface to mark Geocentric transformations.
 * For transformation from or to a {@link org.cts.crs.CompoundCRS},
 * it is important to use a <code>GeocentricTransformation</code>
 * rather than a 2D transformation like NTv2GridShiftTransformation.
 */
public interface GeocentricTransformation extends CoordinateOperation {

    public GeocentricTransformation inverse() throws NonInvertibleOperationException;
}
