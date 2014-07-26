/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
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

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

import java.util.Arrays;

/**
 * Add a fourth dimension to save one of the other coordinates. It is used in
 * CoumpoundCRS transformation to save the altitude value.
 *
 * @author Michaël Michaud
 */
public class MemorizeCoordinate extends AbstractCoordinateOperation {

    private final int[] indexesSaved;
    public static CoordinateOperation memoX = new MemorizeCoordinate(0);
    public static CoordinateOperation memoY = new MemorizeCoordinate(1);
    public static CoordinateOperation memoZ = new MemorizeCoordinate(2);
    public static CoordinateOperation memoXY = new MemorizeCoordinate(0,1);
    public static CoordinateOperation memoXYZ = new MemorizeCoordinate(0,1,2);

    /**
     * Creates a new CoordinateOperation increasing (resp decreasing) the coord
     * size by length.
     *
     * @param indexes indexes of ordinates to memorize
     */
    public MemorizeCoordinate(int... indexes) {
        super(new Identifier(CoordinateOperation.class, "Save coordinates at indexes " + Arrays.toString(indexes)));
        for (int i : indexes) assert i < 3;
        this.indexesSaved = indexes;
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
        double[] cc = new double[Math.max(coord.length + indexesSaved.length, 4)];
        System.arraycopy(coord, 0, cc, 0, Math.min(coord.length, cc.length));
        for (int i = 0 ; i < indexesSaved.length ; i++) {
            cc[Math.max(coord.length+i, 3+i)] = coord[indexesSaved[i]];
        }
        return cc;
    }
}
