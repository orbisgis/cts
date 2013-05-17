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
package org.cts;

/**
 * Exception occuring when a double array input has not the expected length.
 *
 * @author Michaël Michaud
 */
public class CoordinateDimensionException extends IllegalCoordinateException {

    /**
     * Create a new CoordinateDimensionException.
     *
     * @param s the message
     */
    public CoordinateDimensionException(String s) {
        super(s);
    }

    /**
     * Creates a new CoordinateDimensionException.
     *
     * @param coord the coord responsible for this exception
     * @param requiredDimension the dimension required by the calling method
     */
    public CoordinateDimensionException(double[] coord, int requiredDimension) {
        super("The dimension of " + java.util.Arrays.toString(coord)
                + " is not valid : a coord of at least "
                + requiredDimension + "D is required");
    }
}
