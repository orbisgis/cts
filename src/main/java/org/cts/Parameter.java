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

import org.cts.units.Measure;

public class Parameter {
	
	
    public static final String ELLIPSOID = "ellipsoid";

    /** Key to access central meridian, one possible parameter of the projection.*/
    public static final String CENTRAL_MERIDIAN = "central meridian";

    /** Key to access latitude of origin, one possible parameter of the projection.*/
    public static final String LATITUDE_OF_ORIGIN = "latitude of origin";
    
    /** Key to access the first standard parallel of secant conformal conic projections.*/
    public static final String STANDARD_PARALLEL_1 = "standard parallel 1";
    
    /** Key to access the second standard parallel of secant conformal conic projections.*/
    public static final String STANDARD_PARALLEL_2 = "standard parallel 2";

    /** Key to access scale factor, one possible parameter of the projection.*/
    public static final String SCALE_FACTOR = "scale factor";

    /** Key to access false_easting, one possible parameter of the projection.*/
    public static final String FALSE_EASTING = "false easting";

    /** Key to access false_northing, one possible parameter of the projection.*/
    public static final String FALSE_NORTHING = "false northing";

	private String name;
	private Measure measure;

	public Parameter(String name, Measure measure)  {
		this.name=name;
		this.measure = measure;
	}
	
	public String getName() {
		return name;
	}
	
	public Measure getMeasure() {
		return measure;
	}
	
}
