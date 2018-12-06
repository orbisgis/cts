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
package org.cts.op.projection;

import java.util.HashMap;
import java.util.Map;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.Parameter;
import org.cts.datum.Ellipsoid;
import org.cts.op.NonInvertibleOperationException;
import org.cts.units.Measure;
import org.cts.units.Unit;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * A map projection is any method used in cartography (mapmaking) to represent
 * the two-dimensional curved surface of the earth or other body on a plane. The
 * term "projection" here refers to any function defined on the earth's surface
 * and with values on the plane, and not necessarily a geometric projection.<p>
 *
 * @author Michaël Michaud
 */
public class LambertConicConformal2SP extends Projection {

    /**
     * The Identifier used for all Cylindrical Equal Area projection.
     */
    public static final Identifier LCC2SP =
            new Identifier("EPSG", "9802", "Lambert Conic Conformal (2SP)", "Lambert secant");
    /**
     * Lambert 93, the new projection used in France with RGF93 datum.<p>
     */
    public static final LambertConicConformal2SP LAMBERT93 = createLCC2SP(
            Ellipsoid.GRS80, 46.5, 44.0, 49.0, 3.0, 700000.0, 6600000.0);
    // constants of the projections derived from definition parameters
    protected final double lon0, // the reference longitude (from the datum prime meridian)
            n, // projection exponent
            C, // projection constant
            xs, // x coordinate of the pole
            ys;   // y coordinate of the pole

    /**
     * Create a new Lambert Conic Conformal 2SP Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameter lon0 and other parameters useful for the
     * projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public LambertConicConformal2SP(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(LCC2SP, ellipsoid, parameters);
        double lat0 = getLatitudeOfOrigin();
        double lat1 = parameters.get(Parameter.STANDARD_PARALLEL_1).getSValue();
        double lat2 = parameters.get(Parameter.STANDARD_PARALLEL_2).getSValue();
        lon0 = getCentralMeridian();
        double x0 = getFalseEasting();
        double y0 = getFalseNorthing();
        double latIso0 = ellipsoid.isometricLatitude(lat0);
        double latIso1 = ellipsoid.isometricLatitude(lat1);
        double latIso2 = ellipsoid.isometricLatitude(lat2);
        double cos1 = Math.cos(lat1);
        double cos2 = Math.cos(lat2);
        double N1 = ellipsoid.grandeNormale(lat1);
        double N2 = ellipsoid.grandeNormale(lat2);
        n = Math.log((N2 * cos2) / (N1 * cos1)) / (latIso1 - latIso2);
        C = (N1 * cos1 / n) * Math.exp(n * latIso1);
        xs = x0;
        ys = Math.abs(lat0 - Math.PI / 2) < 1E-12 ? y0 : y0 + C * Math.exp(-n * latIso0);
    }

    /**
     * LambertConicConformal2SP factory to create a LambertConicConformal2SP
     * projection from a latitude of origin and a central meridian in degrees, a
     * scale factor and false coordinates in meters.
     *
     * @param ellipsoid reference ellipsoid for this projection instance
     * @param latitude_of_origin latitude of origin of the projection in degrees
     * @param standard_parallel_1 first standard parallel in degrees
     * @param standard_parallel_2 second standard parallel in degrees
     * @param central_meridian central meridian of the projection in degrees
     * @param false_easting false easting in meters
     * @param false_northing false northing in meters
     */
    public static LambertConicConformal2SP createLCC2SP(final Ellipsoid ellipsoid,
            double latitude_of_origin,
            double standard_parallel_1, double standard_parallel_2,
            double central_meridian, double false_easting, double false_northing) {
        return createLCC2SP(ellipsoid, latitude_of_origin, standard_parallel_1,
                standard_parallel_2, central_meridian, Unit.DEGREE,
                false_easting, false_northing, Unit.METER);
    }

    /**
     * LambertConicConformal2SP factory to create a LambertConicConformal2SP
     * projection from a latitude of origin, a central meridian and false
     * coordinates in any unit.
     *
     * @param latitude_of_origin latitude of origin of the projection in degrees
     * @param standard_parallel_1 first standard parallel
     * @param standard_parallel_2 second standard parallel
     * @param central_meridian central meridian of the projection en degrees
     * @param angleUnit unit used for central meridian and latitude of origin
     * @param false_easting false easting in meters
     * @param false_northing false northing in meters
     * @param planimetricUnit unit used for false easting and false northing
     */
    public static LambertConicConformal2SP createLCC2SP(
            final Ellipsoid ellipsoid, double latitude_of_origin,
            double standard_parallel_1, double standard_parallel_2,
            double central_meridian, final Unit angleUnit,
            double false_easting, double false_northing, final Unit planimetricUnit) {
        Map<String, Measure> params = new HashMap<String, Measure>();
        params.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(latitude_of_origin, angleUnit));
        params.put(Parameter.STANDARD_PARALLEL_1, new Measure(standard_parallel_1, angleUnit));
        params.put(Parameter.STANDARD_PARALLEL_2, new Measure(standard_parallel_2, angleUnit));
        params.put(Parameter.CENTRAL_MERIDIAN, new Measure(central_meridian, angleUnit));
        params.put(Parameter.FALSE_EASTING, new Measure(false_easting, planimetricUnit));
        params.put(Parameter.FALSE_NORTHING, new Measure(false_northing, planimetricUnit));
        return new LambertConicConformal2SP(ellipsoid, params);
    }

    /**
     * Transform coord using a Lambert Conformal Conic projection. Input coord
     * is supposed to be a geographic latitude / longitude coordinate in
     * radians.
     *
     * @param coord coordinate to transform
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>..
     */
    @Override
    public double[] transform(double[] coord) {
        double latIso = ellipsoid.isometricLatitude(coord[0]);
        double x = xs + C * exp(-n * latIso) * sin(n * (coord[1] - lon0));
        double y = ys - C * exp(-n * latIso) * cos(n * (coord[1] - lon0));
        coord[0] = x;
        coord[1] = y;
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public Projection inverse() {
        return new LambertConicConformal2SP(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) {
                double x = coord[0];
                double y = coord[1];
                double R = sqrt((x - xs) * (x - xs) + (y - ys) * (y - ys));
                double g = atan((x - xs) / (ys - y));
                double lon = lon0 + g / n;
                double latIso = (-1 / n) * log(abs(R / C));
                double lat = ellipsoid.latitude(latIso);
                coord[0] = lat;
                coord[1] = lon;
                return coord;
            }

            @Override
            public Projection inverse() {
                return LambertConicConformal2SP.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return LambertConicConformal2SP.this.toString() + " inverse";
            }
        };
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.CONICAL;
    }

    /**
     * Return the
     * <code>Property</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Property getProperty() {
        return Projection.Property.CONFORMAL;
    }

    /**
     * Return the
     * <code>Orientation</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Orientation getOrientation() {
        return Projection.Orientation.SECANT;
    }

    /**
     * Return a String representation of this projection
     */
    @Override
    public String toString() {
        return "Lambert Conic Conformal (2SP) ["
                + "lat0=" + parameters.get(Parameter.LATITUDE_OF_ORIGIN) + ";"
                + "sp1=" + parameters.get(Parameter.STANDARD_PARALLEL_1) + ";"
                + "sp2=" + parameters.get(Parameter.STANDARD_PARALLEL_2) + ";"
                + "lon0=" + parameters.get(Parameter.CENTRAL_MERIDIAN) + ";"
                + "x0=" + parameters.get(Parameter.FALSE_EASTING) + ";"
                + "y0=" + parameters.get(Parameter.FALSE_NORTHING) + "]";
    }
}
