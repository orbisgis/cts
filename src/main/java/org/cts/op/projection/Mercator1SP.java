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

import java.util.Map;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.datum.Ellipsoid;
import org.cts.op.NonInvertibleOperationException;
import org.cts.units.Measure;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * The Mercator Projection (MERC). <p>
 *
 * @author Jules Party
 */
public class Mercator1SP extends Projection {

    /**
     * The Identifier used for all Mercator 1SP projections.
     */
    public static final Identifier MERC =
            new Identifier("EPSG", "9804", "Mercator (1SP)", "MERC");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // the false easting
            FN, // the false northing
            n; // projection exponent
    protected final double[] invcoeff;

    /**
     * Create a new Mercator 1SP Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN and other parameters
     * useful for the projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public Mercator1SP(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(MERC, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        double lat_ts = getLatitudeOfTrueScale();
        double e2 = ellipsoid.getSquareEccentricity();
        double k0;
        if (lat_ts != 0) {
            k0 = cos(lat_ts) / sqrt(1 - e2 * pow(sin(lat_ts), 2));
        } else {
            k0 = getScaleFactor();
        }
        double a = getSemiMajorAxis();
        n = k0 * a;
        invcoeff = getInverseMercatorCoeff(ellipsoid);
    }

    /**
     * Return the coefficients for the inverse Mercator projection associated
     * with the ellipsoid in parameter.
     *
     * @param ellps the projected ellipsoid
     */
    public static double[] getInverseMercatorCoeff(Ellipsoid ellps) {
        double e2 = ellps.getSquareEccentricity();
        double e4 = e2 * e2;
        double e6 = e4 * e2;
        double e8 = e4 * e4;
        double[] inv_merc_coeff = new double[5];
        inv_merc_coeff[0] = 1.0;
        inv_merc_coeff[1] = e2 * 1 / 2 + e4 * 5 / 24 + e6 * 1 / 12 + e8 * 13 / 360;
        inv_merc_coeff[2] = e4 * 7 / 48 + e6 * 29 / 240 + e8 * 811 / 11520;
        inv_merc_coeff[3] = e6 * 7 / 120 + e8 * 81 / 1120;
        inv_merc_coeff[4] = e8 * 4279 / 161280;
        return inv_merc_coeff;
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.CYLINDRICAL;
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
        return Projection.Orientation.TANGENT;
    }

    /**
     * Transform coord using the Mercator Projection. Input coord is supposed to
     * be a geographic latitude / longitude coordinate in radians. Algorithm
     * based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double lon = coord[1];
        double lat = abs(coord[0]) > PI * 85 / 180 ? PI * 85 / 180 : coord[0];
        double E = n * (lon - lon0);
        double N = n * ellipsoid.isometricLatitude(lat);
        coord[0] = FE + E;
        coord[1] = FN + N;
        return coord;
    }

    /**
     * Creates the inverse operation for Mercator Projection. Input coord is
     * supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new Mercator1SP(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double t = exp((FN - coord[1]) / n);
                double ki = PI / 2 - 2 * atan(t);
                double lat = ki;
                for (int i = 1; i < 5; i++) {
                    lat += invcoeff[i] * sin(2 * i * ki);
                }
                coord[1] = (coord[0] - FE) / n + lon0;
                coord[0] = lat;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return Mercator1SP.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return Mercator1SP.this.toString() + " inverse";
            }
        };
    }
}