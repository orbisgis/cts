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

    EASTING("Easting"), // used for planimetric coordinate system
    NORTHING("Northing"), // used for planimetric coordinate system
    WESTING("Westing"), // used for planimetric coordinate system
    SOUTHING("Southing"), // used for planimetric coordinate system
    x("x"), // used for planimetric coordinate system
    y("y"), // used for planimetric coordinate system
    ALTITUDE("Altitude"), // used for vertical/compound system
    DEPTH("Depth"), // used for bathymetry
    LATITUDE("Latitude"), // used for geographic coordinate system
    LONGITUDE("Longitude"), // used for 
    HEIGHT("Height"), // used for 3D ellipsoidal coordinate system
    X("X"), // used for 3D cartesian system
    Y("Y"), // used for 3D cartesian system
    Z("Z"), // used for 3D cartesian system
    TIME("Time");
    private String name;

    /**
     * .
     * @param name name of this new Axis
     */
    private Axis(String name) {
        this.name = name;
    }

    /**
     * Return the name of this Axis (X, Y, Z, LONGITUDE, ALTITUDE,&hellip;).
     *
     * @return the name of this axis
     */
    public String getName() {
        return name;
    }

    /**
     * @return a String representation of this Axis
     */
    @Override
    public String toString() {
        return name;
    }
}
