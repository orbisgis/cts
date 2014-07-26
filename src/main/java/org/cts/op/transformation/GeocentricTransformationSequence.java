package org.cts.op.transformation;

import org.cts.Identifier;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.NonInvertibleOperationException;

import java.util.Arrays;
import java.util.List;

/**
 * A GeocentricTransformationSequence is a CoordinateOperationSequence which
 * result is a {@link GeocentricTransformation}.
 */
public class GeocentricTransformationSequence
        extends CoordinateOperationSequence
        implements GeocentricTransformation {

    /** @see org.cts.op.CoordinateOperationSequence */
    public GeocentricTransformationSequence(Identifier identifier,
                                       CoordinateOperation... sequence) {
        super(identifier, sequence);
    }

    /** @see org.cts.op.CoordinateOperationSequence */
    public GeocentricTransformationSequence(Identifier identifier,
                                            List<CoordinateOperation> sequence) {
        super(identifier, sequence);
    }

    /** @see org.cts.op.CoordinateOperationSequence */
    public GeocentricTransformationSequence(Identifier identifier,
                                            CoordinateOperation[] sequence, double precision) {
        super(identifier, sequence, precision);
    }

    public GeocentricTransformation inverse() throws NonInvertibleOperationException {
        CoordinateOperation[] inverse_sequence =
                new CoordinateOperation[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            // If one of the CoordinateOperation is not invertible, it
            // will throw a NonInvertibleOperationException.
            inverse_sequence[sequence.length - i - 1] = sequence[i].inverse();
            //if (inverse_sequence[sequence.length-i-1] == null) return null;
        }
        return new GeocentricTransformationSequence(getIdentifier(),
                inverse_sequence, precision);
    }

    /**
     * Returns true if o is equals to <code>this</code>.
     * GeocentricTranslations are equals if they both are identity, or
     * if all their parameters are equal.
     *
     * @param o The object to compare this ProjectedCRS against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GeocentricTransformationSequence) {
            if (sequence.length == ((GeocentricTransformationSequence)o).sequence.length) {
                for (int i = 0 ; i < sequence.length ; i++) {
                    if (!sequence[i].equals(((GeocentricTransformationSequence)o).sequence[i])) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the hash code for this GeocentricTranslation.
     */
    @Override
    public int hashCode() {
        if (isIdentity()) return 0;
        int hash = 5;
        hash = 19 * hash + Arrays.deepHashCode(this.sequence);
        return hash;
    }

}
