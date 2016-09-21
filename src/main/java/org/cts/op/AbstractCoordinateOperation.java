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
package org.cts.op;

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

/**
 * AbstractCoordinateOperation is a partial implementation of the
 * {@link CoordinateOperation} interface.
 *
 * @author Michaël Michaud
 */
public abstract class AbstractCoordinateOperation
        extends IdentifiableComponent
        implements CoordinateOperation {

    protected double precision = 0.0;

    /**
     * Create a new {@link CoordinateOperation} instance.
     *
     * @param identifier this CoordinateOperation identifier
     */
    protected AbstractCoordinateOperation(Identifier identifier) {
        super(identifier);
    }

    /**
     * Return a double[] representing the same location as coord but in another
     * CoordinateReferenceSystem.
     *
     * @param coord the input coordinate
     * @return a double array containing the output coordinate
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     * @throws org.cts.op.CoordinateOperationException if this operation
     * failed during the transformation process.
     */
    public abstract double[] transform(double[] coord)
            throws IllegalCoordinateException, CoordinateOperationException;

    /**
     * Creates the inverse CoordinateOperation. This method can be used to chain
     * {@link org.cts.op.CoordinateOperation}s and/or inverse CoordinateOperation in
     * a unique CoordinateOperationSequence. This method is not declared
     * abstract, so that implementation classes have not to implement it if they
     * represent non invertible operation.
     */
    public CoordinateOperation inverse()
            throws NonInvertibleOperationException {
        throw new NonInvertibleOperationException(this.toString()
                + " is non invertible");
    }

    /**
     * Returns the precision of the transformation.<p> Precision is a double
     * representing the mean error, in meters made on the position resulting
     * from this {@link org.cts.op.CoordinateOperation}.<p> ex. : 0.001 means that
     * the precision of the resulting position is about one millimeter<p>
     * Default precision (or maximum precision) is considered to be equals to
     * 1E-9 which is the value of an ulp (units in the last place) for a double
     * value equals to 6378137.0 (Earth semi-major axis).
     */
    public double getPrecision() {
        return precision;
    }


    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity() {
        return false;
    }
}