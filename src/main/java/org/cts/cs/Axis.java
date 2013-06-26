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
package org.cts.cs;

/**
 * One of the fixed reference lines of a {@link CoordinateSystem}.<p> Usually,
 * axis is a term reserved to cartesian coordinate systems made of several
 * perpendicular axis. In the context of this library, Axis objects are used for
 * any kind of coordinate system, including vertical and ellipsoidal ones.
 *
 * @author Michaël Michaud
 */
public enum Axis {

    /**
     * Easting axis. Used for planimetric coordinate system, generally in pair
     * with northing.
     */
    EASTING("Easting"),
    /**
     * Northing axis. Used for planimetric coordinate system, generally in pair
     * with easting.
     */
    NORTHING("Northing"),
    /**
     * Westing axis. Used for planimetric coordinate system, generally in pair
     * with southing.
     */
    WESTING("Westing"),
    /**
     * Southing axis. Used for planimetric coordinate system, generally in pair
     * with westing.
     */
    SOUTHING("Southing"),
    /**
     * x axis. Used for planimetric coordinate system, sometimes used in place
     * of easting.
     */
    x("x"),
    /**
     * y axis. Used for planimetric coordinate system, sometimes used in place
     * of northing.
     */
    y("y"),
    /**
     * Altitude axis. Used for vertical/compound system.
     */
    ALTITUDE("Altitude"),
    /**
     * Depth axis. Used for bathymetry.
     */
    DEPTH("Depth"),
    /**
     * Latitude axis. Used for geographic coordinate system, generally in pair
     * with longitude.
     */
    LATITUDE("Latitude"),
    /**
     * Longitude axis. Used for geographic coordinate system, generally in pair
     * with latitude.
     */
    LONGITUDE("Longitude"),
    /**
     * Height axis. Used for 3D ellipsoidal coordinate system, generally with
     * latitude and longitude axes.
     */
    HEIGHT("Height"),
    /**
     * X axis. Used for 3D cartesian system, generally with Y and Z axes.
     */
    X("X"),
    /**
     * Y axis. Used for 3D cartesian system, generally with X and Z axes.
     */
    Y("Y"),
    /**
     * Z axis. Used for 3D cartesian system, generally with X and Y axes.
     */
    Z("Z"),
    /**
     * Time axis. Not supported in CTS yet.
     */
    TIME("Time");
    /**
     * The name of this Axis (X, Y, Z, LONGITUDE, ALTITUDE,&hellip;).
     */
    private String name;

    /**
     * Create a new Axis.
     * @param name name of this new Axis
     */
    private Axis(String name) {
        this.name = name;
    }

    /**
     * Return the name of this Axis (X, Y, Z, LONGITUDE, ALTITUDE,&hellip;).
     */
    public String getName() {
        return name;
    }

    /**
     * Return a String representation of this Axis.
     */
    @Override
    public String toString() {
        return name;
    }
}
