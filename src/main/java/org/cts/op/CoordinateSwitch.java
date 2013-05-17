/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
 * Michaud.
 * The new CTS has been funded  by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-08-VILL-0005-01 and the regional council 
 * "Région Pays de La Loire" under the projet SOGVILLE (Système d'Orbservation 
 * Géographique de la Ville).
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/irstv/cts/>
 */
package org.cts.op;

import org.cts.CoordinateDimensionException;
import org.cts.CoordinateOperation;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.NonInvertibleOperationException;

/**
 * A class to switch to values in a double array.
 *
 * @author Michaël Michaud
 */
public class CoordinateSwitch extends AbstractCoordinateOperation {

    public final static CoordinateSwitch SWITCH_LAT_LON = new CoordinateSwitch(0, 1);
    int pos1, pos2;

    /**
     * Change ordinate at pos1 with ordinate at pos2.
     *
     * @param coord is an array containing one, two or three ordinates.
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
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
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return this;
    }

    /**
     * Creates a new operation to switch coord pos1 and coord pos2.
     *
     * @param pos1 position of the ordinate to switch to pos2
     * @param pos2 position of the ordinate to switch to pos1
     */
    private CoordinateSwitch(int pos1, int pos2) {
        super(new Identifier(CoordinateSwitch.class));
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
}