package org.cts.op;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.cs.Extent;

import java.util.Arrays;


public class CheckInExtentCoordinateOperation extends AbstractCoordinateOperation {

    Extent extent;

    public CheckInExtentCoordinateOperation(CoordinateReferenceSystem crs) {
        super(new Identifier(CoordinateOperation.class, "extent of '" + crs.getName() + "'"));
        this.extent = crs;
    }

    /**
     * Check if coord lies in extent. If it is inside, coord is returned as is,
     * else, a IllegalCoordinateException is thrown.
     *
     * @param coord coordinate to check
     * @return the same coordinates array
     * @throws IllegalCoordinateException if <code>coord</code> does not lie in extent.
     * @throws org.cts.op.CoordinateOperationException if this operation
     * failed during the transformation process.
     */
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (extent == null || extent.isInside(coord)) return coord;
        throw new IllegalCoordinateException("Coord " + Arrays.toString(coord) + " is not within " + getName());
    }

    /**
     * Return the inverse CoordinateOperation, or throw a
     * NonInvertibleOperationException. If op.inverse() is not null,
     * <pre>
     * op.inverse().transform(op.transform(point));
     * </pre> should let point unchanged.
     */
    public CoordinateOperation inverse() {
        return this;
    }

    ///**
    // * Returns the maximum precision
    // */
    //public double getPrecision() {
    //    return 1E-9;
    //}

    ///**
    // * @return true if this operation does not change coordinates.
    // */
    //public boolean isIdentity() {
    //    return true;
    //}

    /**
     * Returns whether coord is consistent with source and target CRS.
     */
    //public boolean isInside(double[] coord) {
    //    if (extent == null || extent.isInside(coord)) return true;
    //    throw new IllegalCoordinateException("Coordinates " + coord + " does not lie in extent");
    //}
}
