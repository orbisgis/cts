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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.datum.Ellipsoid;

/**
 * <p>Transform geographic coordinates (latitude, longitude, ellipsoidal height
 * into geocentric coordinates.</p>
 * <p>Geographic coordinates and geocentric coordinates are supposed to use the
 * same reference datum and to be standardized :</p>
 * <ul>
 * <li>Geographic coordinates are given in the following order : latitude
 * (radians), longitude (radians from Greenwich) and optionnaly ellipsoidal
 * height (default = 0.0).</li>
 * <li>The center of the geocentric system (center of the mass) is equal to the
 * geographic coordinates reference ellipsoid.</p>
 * <li>Z axis is oriented from origin to North Pole</li>
 * <li>Y axis is oriented from origin to intersection of equator and Greenwich
 * Meridian</li>
 * <li>OXYZ is direct</li>
 * <li>Units = radian, meter (to facilitate transformation operations).</li>
 * </ul>
 *
 * @author Michaël Michaud
 */
public class Geographic2Geocentric extends AbstractCoordinateOperation {

    /**
     * The Identifier used for all Geographic to geocentric conversions.
     */
    private static final Identifier opId =
            new Identifier("EPSG", "9602",
            "Geographic to geocentric conversion", "Geographic to geocentric");
    /**
     * The ellipsoid used to define geographic coordinates.
     */
    private final Ellipsoid ellipsoid;
    /**
     * Stop condition for the Geocentric to Geographic transformation algorithm
     * (the inverse transformation of this Geographic2Geocentric). epsilon is a
     * value in radian, 1E-11 is the default epsilon and it means that error is
     * less than 1E-4 m.
     */
    private final double epsilon;

    /**
     * <p>Create a new Geographic2Geocentric transformation for a specific
     * ellipsoid. The reference datum for both geographic and geocentric
     * coordinates is the same.</p>
     *
     * @param ellipsoid the ellipsoid used to define geographic coordinates
     */
    public Geographic2Geocentric(Ellipsoid ellipsoid) {
        super(opId);
        this.ellipsoid = ellipsoid;
        this.precision = 1E-4;   // 0.0001 mm
        this.epsilon = 1E-11;    // condition d'arret = 1E-11 radian < 1E-4 m
    }

    /**
     * <p>Create a new Geographic2Geocentric transformation for a specific
     * ellipsoid. The reference datum for both geographic and geocentric
     * coordinates is the same.</p>
     *
     * @param ellipsoid the ellipsoid used to define geographic coordinates
     * @param epsilon stop condition for the Geocentric to Geographic
     * transformation algorithm (epsilon is a value in radian, 1E-11 is the
     * default epsilon and it means that error is less than 1E-4 m)
     */
    public Geographic2Geocentric(Ellipsoid ellipsoid, double epsilon) {
        super(opId);
        this.ellipsoid = ellipsoid;
        this.precision = 1E-4;   // 0.0001 mm
        this.epsilon = epsilon;  // condition d'arret = 1E-11 radian < 1E-4 m
    }

    /**
     * Return coordinates representing the same point in a standard geocentric
     * coordinate system.<p>
     *
     * @param coord is an array containing 2 or 3 double representing geographic
     * coordinates in the following order : latitude (radians), longitude
     * (radians from Greenwich) and optionnaly ellipsoidal height (if coord
     * contains only 2 double's, height is set to 0).
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord)
            throws IllegalCoordinateException {
        if (coord.length < 2) {
            throw new CoordinateDimensionException(coord, 3);
        } else if (coord.length == 2) {
            coord = new double[]{coord[0], coord[1], 0.0};
        }
        double lat = coord[0];
        double lon = coord[1];
        double height = 0.0;
        if (coord.length > 2 && !Double.isNaN(coord[2])) {
            height = coord[2];
        }
        double N = ellipsoid.transverseRadiusOfCurvature(lat);
        coord[0] = (N + height) * cos(lat) * cos(lon);
        coord[1] = (N + height) * cos(lat) * sin(lon);
        coord[2] = (N * (1 - ellipsoid.getSquareEccentricity()) + height) * sin(lat);
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() {
        return new Geocentric2Geographic(ellipsoid, epsilon);
    }

    /**
     * Return a String representation of this Geographic/Geocentric converter.
     */
    @Override
    public String toString() {
        return getName() + " (" + ellipsoid.getName() + ")";
    }

    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Returns true if object is equals to
     * <code>this</code>. Tests equality between the ellipsoid used by the
     * transformation.
     *
     * @param o The object to compare this Geographic2Geocentric against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Geographic2Geocentric) {
            Geographic2Geocentric gg2gc = (Geographic2Geocentric) o;
            return getEllipsoid().equals(gg2gc.getEllipsoid());
        }
        return false;
    }

    /**
     * Returns the hash code for this Geographic2Geocentric.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.ellipsoid != null ? this.ellipsoid.hashCode() : 0);
        return hash;
    }
}
