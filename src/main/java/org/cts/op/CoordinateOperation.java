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

import org.cts.Identifiable;
import org.cts.IllegalCoordinateException;

/**
 * A CoordinateOperation is an object able to modify values of a coordinate.<p>
 * This is the main interface used to implement geodetic algorithms and perform
 * coordinate conversions or coordinate transformations from one
 * {@link org.cts.crs.CoordinateReferenceSystem} to another.
 *
 * @author Michaël Michaud
 */
public interface CoordinateOperation extends Identifiable {

    /**
     * Transform values of a double array.<p> <b>WARNING</b> : In the general
     * case, double values of the coord parameter are changed without the
     * creation of a new array. In some cases (when the returned array must have
     * a different length, a new double array is created and returned.
     *
     * @param coord coordinate to be transformed
     * @return the same object with new values or a new double array
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     * @throws org.cts.op.CoordinateOperationException if this operation
     * failed during the transformation process.
     */
    public double[] transform(double[] coord) throws IllegalCoordinateException, CoordinateOperationException;

    /**
     * Return the inverse CoordinateOperation, or throw a
     * NonInvertibleOperationException. If op.inverse() is not null,
     * <pre>
     * op.inverse().transform(op.transform(point));
     * </pre> should let point unchanged.
     */
    public CoordinateOperation inverse() throws NonInvertibleOperationException;

    /**
     * Return the precision of the transformation.<p> Precision is a double
     * representing the mean error, in meters made on the position resulting
     * from this
     * <code>CoordinateOperation</code>.<p> ex. : 0.001 means that the precision
     * of the resulting position is about one millimeter<p> Default precision
     * (or maximum precision) is considered to be equals to 1E-9 which is the
     * value of an ulp (units in the last place) for a double value equals to
     * 6378137.0 (Earth semi-major axis).
     */
    public double getPrecision();

    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity();
}