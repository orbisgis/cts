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
 * A class to switch to values in a double array.
 *
 * @author Michaël Michaud
 */
public class CoordinateSwitch extends AbstractCoordinateOperation {

    /**
     * Switch the two first value of a coordinate. For instance longitude and
     * latitude in the case of geographic coordinates.
     */
    public final static CoordinateSwitch SWITCH_LAT_LON = new CoordinateSwitch(0, 1);
    /**
     * Position of the ordinate to switch.
     */
    int pos1, pos2;

    /**
     * Change ordinate at pos1 with ordinate at pos2.
     *
     * @param coord is an array containing one, two or three ordinates
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord)
            throws IllegalCoordinateException {
        if (coord.length < Math.min(pos1, pos2)) {
            throw new CoordinateDimensionException(coord, Math.max(pos1, pos2));
        }
        double d1 = coord[pos1];
        coord[pos1] = coord[pos2];
        coord[pos2] = d1;
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() {
        return this;
    }

    /**
     * Creates a new operation to switch coord pos1 and coord pos2.
     *
     * @param pos1 position of the ordinate to switch to pos2
     * @param pos2 position of the ordinate to switch to pos1
     */
    public CoordinateSwitch(int pos1, int pos2) {
        super(new Identifier(CoordinateOperation.class, "Coordinates switch"));
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Returns true if o is equals to <code>this</code> operation.
     *
     * @param o The object to compare to
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof CoordinateSwitch &&
                pos1 == ((CoordinateSwitch) o).pos1 &&
                pos2 == ((CoordinateSwitch) o).pos2;
    }
}