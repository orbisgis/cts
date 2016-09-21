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

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

/**
 * This transformation turn the choosing coordinate into its opposite.
 *
 * @author Jules Party
 */
public class OppositeCoordinate extends AbstractCoordinateOperation {

    /**
     * The index of the coordinate to transform.
     */
    private int index;

    /**
     * Construct the transformation turning the value at the given index into
     * its opposite.
     *
     * @param index the index of the value to turn into its opposite
     */
    public OppositeCoordinate(int index) {
        super(new Identifier(CoordinateOperation.class, "Transform the coordinate " + index + " into its opposite."));
        this.index = index;
        this.precision = 0.0;
    }

    /**
     * Apply the OppositeCoordinate transformation to input coordinates.
     *
     * @param coord is an array containing one, two or three ordinates
     * @throws IllegalCoordinateException
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (index >= coord.length) {
            throw new CoordinateDimensionException(coord, index);
        }
        coord[index] = -coord[index];
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return this;
    }
}
