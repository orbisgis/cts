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
     * Creates a new CoordinateOperation increasing (resp decreasing) the coord
     * size by length.
     *
     * @param dim final dimension of the new coordinate
     */
    public LoadMemorizeCoordinate(int index) {
        super(new Identifier(LoadMemorizeCoordinate.class, "Load the last saved coordinate in the position " + index));
        this.indexSaved = index;
    }

    public int getIndexSaved() {
        return indexSaved;
    }

    /**
     * Load the last memorized coordinates
     *
     * @param coord is an array containing one, two or three ordinates
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
