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
package org.cts.datum;

import org.cts.Identifiable;
import org.cts.cs.Extent;
import org.cts.op.CoordinateOperation;

/**
 * A datum (plural datums) is a reference from which measurements are made.<p>
 * In surveying and geodesy, a datum is a reference point on the earth's surface
 * against which position measurements are made, and an associated model of the
 * shape of the earth for computing positions. Horizontal datums are used for
 * describing a point on the earth's surface, in latitude and longitude or
 * another coordinate system. Vertical datums are used to measure elevations or
 * underwater depths. In engineering and drafting, a datum is a reference point,
 * surface, or axis on an object against which measurements are made.<p> (Taken
 * from <a ref="http://en.wikipedia.org/wiki/Datum">wikipedia</a> on
 * 2006-10-06)</p>
 *
 * @author Michaël Michaud
 *
 */
public interface Datum extends Identifiable {

    /**
     * Returns origin description of this Datum.
     */
    public String getOrigin();

    /**
     * Returns the valid extent of this Datum.
     */
    public Extent getExtent();

    /**
     * Returns the realization epoch as a String.
     */
    public String getEpoch();

    /**
     * Returns the ellipsoid of this datum.
     */
    public Ellipsoid getEllipsoid();

    /**
     * Returns the ellipsoid of this datum.
     */
    public CoordinateOperation getToWGS84();

    /**
     * Returns the primemeridian of this datum.
     */
    public PrimeMeridian getPrimeMeridian();
}