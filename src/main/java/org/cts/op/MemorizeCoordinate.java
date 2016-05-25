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

/**
 * Add a fourth dimension to save one of the other coordinates. It is used in
 * CoumpoundCRS transformation to save the altitude value.
 *
 * @author Michaël Michaud
 */
public class MemorizeCoordinate extends AbstractCoordinateOperation {

    private final int indexSaved;
    public static CoordinateOperation memoX = new MemorizeCoordinate(0);
    public static CoordinateOperation memoY = new MemorizeCoordinate(1);
    public static CoordinateOperation memoZ = new MemorizeCoordinate(2);

    /**
     * Creates a new CoordinateOperation increasing (resp decreasing) the coord
     * size by length.
     *
     * @param dim final dimension of the new coordinate
     */
    public MemorizeCoordinate(int index) {
        super(new Identifier(MemorizeCoordinate.class, "Save the " + (index + 1) + "e coordinate"));
        this.indexSaved = index;
    }

    /**
     * Add a fourth coordinate, to save a value.
     *
     * @param coord is an array containing one, two or three ordinates
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord)
            throws IllegalCoordinateException {
        double[] cc = new double[Math.max(coord.length + 1, 4)];
        System.arraycopy(coord, 0, cc, 0, Math.min(coord.length, cc.length));
        cc[Math.max(coord.length, 3)] = coord[indexSaved];
        return cc;
    }
}
