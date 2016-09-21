/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the OrbisGIS code repository.
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/orbisgis/cts/>
 */
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
 * @author Michaël Michaud
 */
public class GeocentricTransformationSequence
        extends CoordinateOperationSequence
        implements GeocentricTransformation {

    /**
     * @param identifier * @see org.cts.op.CoordinateOperationSequence
     * @param sequence
     */
    public GeocentricTransformationSequence(Identifier identifier,
            CoordinateOperation... sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier * @see org.cts.op.CoordinateOperationSequence
     * @param sequence
     */
    public GeocentricTransformationSequence(Identifier identifier,
            List<CoordinateOperation> sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier * @see org.cts.op.CoordinateOperationSequence
     * @param sequence
     * @param precision
     */
    public GeocentricTransformationSequence(Identifier identifier,
            CoordinateOperation[] sequence, double precision) {
        super(identifier, sequence, precision);
    }

    @Override
    public GeocentricTransformation inverse() throws NonInvertibleOperationException {
        CoordinateOperation[] inverse_sequence
                = new CoordinateOperation[sequence.length];
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
     * Returns true if o is equals to <code>this</code>. GeocentricTranslations
     * are equals if they both are identity, or if all their parameters are
     * equal.
     *
     * @param o The object to compare this ProjectedCRS against
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GeocentricTransformationSequence) {
            if (sequence.length == ((GeocentricTransformationSequence) o).sequence.length) {
                for (int i = 0; i < sequence.length; i++) {
                    if (!sequence[i].equals(((GeocentricTransformationSequence) o).sequence[i])) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the hash code for this GeocentricTranslation.
     *
     * @return
     */
    @Override
    public int hashCode() {
        if (isIdentity()) {
            return 0;
        }
        int hash = 5;
        hash = 19 * hash + Arrays.deepHashCode(this.sequence);
        return hash;
    }

}
