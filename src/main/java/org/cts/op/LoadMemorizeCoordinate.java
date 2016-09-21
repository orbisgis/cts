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
public class LoadMemorizeCoordinate extends AbstractCoordinateOperation {

    private final int indexSaved;
    public static CoordinateOperation loadX = new LoadMemorizeCoordinate(0);
    public static CoordinateOperation loadY = new LoadMemorizeCoordinate(1);
    public static CoordinateOperation loadZ = new LoadMemorizeCoordinate(2);

    /**
     * Creates a new CoordinateOperation removing the last double value from
     * a coordinate array and loading it at the specified index in the array.
     *
     * @param index final dimension of the new coordinate
     */
    public LoadMemorizeCoordinate(int index) {
        super(new Identifier(CoordinateOperation.class, "Load last saved coordinate in position " + index));
        this.indexSaved = index;
    }

    public int getIndexSaved() {
        return indexSaved;
    }

    /**
     * Load the last memorized coordinates : remove the last ordinate
     * of the array, and load it at index indexSaved.
     *
     * @param coord is an array containing four ordinates or more
     * @return 
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length < 4) {
            throw new IllegalCoordinateException("There is no saved value in these coordinates.");
        }
        double[] cc = new double[coord.length - 1];
        System.arraycopy(coord, 0, cc, 0, coord.length - 1);
        cc[indexSaved] = coord[coord.length - 1];
        return cc;
    }
}
