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

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.op.transformation.GeocentricTransformation;
import org.cts.op.transformation.ParamBasedTransformation;

/**
 * The identity transformation.<p> This transformation doesn't do anything.
 *
 * @author Michaël Michaud
 */
public class Identity extends AbstractCoordinateOperation implements ParamBasedTransformation, GeocentricTransformation {

    /**
     * The identity transformation. When used to transform coordinates, it
     * returns the input coordinates.
     */
    public static final Identity IDENTITY = new Identity();

    /**
     * Identity is defined as a Singleton in order to avoid unuseful object
     * creation.
     */
    private Identity() {
        super(new Identifier(CoordinateOperation.class, "Identity"));
        this.precision = 0.0;
    }

    /**
     * Apply the identity transformation to input coordinates.
     *
     * @param coord is an array containing one, two or three ordinates
     * @throws IllegalCoordinateException
     */
    @Override
    public double[] transform(double[] coord) {
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public GeocentricTransformation inverse() {
        return this;
    }

    /**
     * Returns true if o is also an Identity CoordinateOperation.
     *
     * @param o The object to compare this ProjectedCRS against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CoordinateOperation) {
            return ((CoordinateOperation)o).isIdentity();
        }
        return false;
    }

    /**
     * Returns 0 for all identity operations.
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity() {
        return true;
    }
}
