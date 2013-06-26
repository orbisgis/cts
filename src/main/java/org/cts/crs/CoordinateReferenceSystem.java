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
package org.cts.crs;

import org.cts.Identifiable;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.Datum;
import org.cts.op.projection.Projection;

/**
 * A coordinate system which is related to the real world by a
 * {@link org.cts.datum.Datum} (ISO/DIS 19111). <p> A point in the real world
 * may have different coordinates, depending on the
 * <code>CoordinateReferenceSystem</code> used. The different types of
 * CoordinateReferenceSystem defined by <a href="http://www.epsg.org/"> EPSG</a>
 * are : </p> <ul> <li>Geocentric coordinate reference system</li>
 * <li>Geographic 3D coordinate reference system</li> <li>Geographic 2D
 * coordinate reference system</li> <li>Projected coordinate reference
 * system</li> <li>Vertical coordinate reference system</li> <li>Compound
 * coordinate reference system</li> <li>Engineering coordinate reference
 * system</li> </ul>
 *
 * @author Michaël Michaud
 */
public interface CoordinateReferenceSystem extends Identifiable {

    /**
     * Coordinate Reference System Type.
     */
    public static enum Type {
        GEOCENTRIC, GEOGRAPHIC3D, GEOGRAPHIC2D, PROJECTED, VERTICAL, COMPOUND, ENGINEERING
    };

    /**
     * Returns this CoordinateReferenceSystem Type.
     */
    public Type getType();

    /**
     * Returns the {@link CoordinateSystem} used by this
     * <code>CoordinateReferenceSystem</code>.
     */
    public CoordinateSystem getCoordinateSystem();

    /**
     * Returns the {@link Datum} to which this
     * <code>CoordinateReferenceSystem</code> is refering. For compound
     * <code>CoordinateReferenceSystem</code>, getDatum returns the the main
     * datum, ie the {@link org.cts.datum.GeodeticDatum} (or horizontal Datum).
     */
    public Datum getDatum();

    /**
     * Returns the {@link Projection} to which this
     * <code>CoordinateReferenceSystem</code> is refering. It returns null if no
     * projection is defined for this CRS.
     */
    public Projection getProjection();
}
