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

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.datum.Datum;

/**
 * Exception thrown when a coordinate operation has not been found
 * or could not be calculated.
 * @author Michaël Michaud
 */
public class CoordinateOperationNotFoundException extends CoordinateOperationException {

    /**
     * Creates a new CoordinateOperationNotFoundException.
     *
     * @param exception description of this exception
     */
    public CoordinateOperationNotFoundException(String exception) {
        super(exception);
    }

    /**
     * Creates a new CoordinateOperationNotFoundException because no
     * CoordinateOperation has been found from source Datum to target Datum.
     */
    public CoordinateOperationNotFoundException(Datum source, Datum target) {
        super("No CoordinateOperation has been found to convert coordinates from\n" + source + " to\n" + target);
    }

    /**
     * Creates a new CoordinateOperationNotFoundException because no
     * CoordinateOperation has been found from source CoordinateReferenceSystem
     * to target CoordinateReferenceSystem.
     */
    public CoordinateOperationNotFoundException(CoordinateReferenceSystem source, CoordinateReferenceSystem target) {
        super("No CoordinateOperation has been found to convert coordinates from\n" + source + " to\n" + target);
    }
}
