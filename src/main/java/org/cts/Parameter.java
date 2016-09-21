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
package org.cts;

import org.cts.units.Measure;

public class Parameter {

    public static final String ELLIPSOID = "ellipsoid";
    /**
     * Key to access central meridian, one possible parameter of the projection.
     */
    public static final String CENTRAL_MERIDIAN = "central meridian";
    /**
     * Key to access latitude of origin, one possible parameter of the
     * projection.
     */
    public static final String LATITUDE_OF_ORIGIN = "latitude of origin";
    /**
     * Key to access the first standard parallel of secant conformal conic
     * projections.
     */
    public static final String STANDARD_PARALLEL_1 = "standard parallel 1";
    /**
     * Key to access the second standard parallel of secant conformal conic
     * projections.
     */
    public static final String STANDARD_PARALLEL_2 = "standard parallel 2";
    /**
     * Key to access latitude of true scale, one possible parameter of the
     * projection.
     */
    public static final String LATITUDE_OF_TRUE_SCALE = "latitude of true scale";
    /**
     * Key to access azimuth of the initial line of oblique projections.
     */
    public static final String AZIMUTH = "azimuth";
    /**
     * Key to access angle from the rectified grid to the skew (oblique) grid of
     * oblique projections.
     */
    public static final String RECTIFIED_GRID_ANGLE = "rectified grid angle";
    /**
     * Key to access scale factor, one possible parameter of the projection.
     */
    public static final String SCALE_FACTOR = "scale factor";
    /**
     * Key to access false_easting, one possible parameter of the projection.
     */
    public static final String FALSE_EASTING = "false easting";
    /**
     * Key to access false_northing, one possible parameter of the projection.
     */
    public static final String FALSE_NORTHING = "false northing";
    private String name;
    private Measure measure;

    /**
     * Creates a new Parameters defined by his name and his measure.
     *
     * @param name the name of the new parameter (ex : latitude, false northing)
     * @param measure the measure in which the parameter is expressed (ex :
     * degrees, meters)
     */
    public Parameter(String name, Measure measure) {
        this.name = name;
        this.measure = measure;
    }

    /**
     * Returns the name of this Parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the measure in which this Parameter is expressed.
     */
    public Measure getMeasure() {
        return measure;
    }
}
