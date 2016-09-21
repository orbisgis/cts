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

import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * The Krovak (North Orientated) Projection (KROVAK). <p>
 *
 * @author Jules Party
 */
public class Krovak extends Projection {

    /**
     * The Identifier used for all Krovak projections.
     */
    public static final Identifier KROVAK =
            new Identifier("EPSG", "1041", "Krovak (North Orientated)", "KROVAK");
    protected final double lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN, // false northing
            alphac, // rotation in plane of meridian of origin of the conformal coordinates
            latp, // latitude of pseudo-standard parallel
            B, // a constant of the projection
            t0, // a constant of the projection
            n, // a constant of the projection
            r0; // a constant of the projection

    /**
     * Create a new Krovak (North Orientated) Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, FE, FN and other useful parameters.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public Krovak(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(KROVAK, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        double lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        alphac = getAzimuth();
        double kp = getScaleFactor();
        latp = 78.5 * PI / 180;
        double a = ellipsoid.getSemiMajorAxis();
        double e2 = ellipsoid.getSquareEccentricity();
        double e = ellipsoid.getEccentricity();
        double sin0 = sin(lat0);
        double A = a * sqrt(1 - e2) / (1 - e2 * sin0 * sin0);
        B = sqrt(1 + e2 / (1 - e2) * pow(cos(lat0), 4));
        double gamma0 = asin(sin0 / B);
        t0 = tan((PI / 2 + gamma0) / 2) * pow(pow((1 + e * sin0) / (1 - e * sin0), e / 2) / tan((PI / 2 + lat0) / 2), B);
        n = sin(latp);
        r0 = kp * A / tan(latp);
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
     * Transform coord using the Krovak (North Orientated) Projection. Input
     * coord is supposed to be a geographic latitude / longitude coordinate in
     * radians. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double lat = coord[0];
        double lon = coord[1];
        double e = ellipsoid.getEccentricity();
        double esin = e * sin(lat);
        double U = 2 * (atan(t0 * pow(tan((PI / 2 + lat) / 2) / pow((1 + esin) / (1 - esin), e / 2), B)) - PI / 4);
        double V = B * (lon0 - lon);
        double T = asin(cos(alphac) * sin(U) + sin(alphac) * cos(U) * cos(V));
        double sinD = cos(U) * sin(V) / cos(T);
        double cosD = (cos(alphac) * sin(T) - sin(U)) / sin(alphac) / cos(T);
        double D = atan2(sinD, cosD);
        double theta = n * D;
        double r = r0 * pow(tan((latp + PI / 2) / 2) / tan((T + PI / 2) / 2), n);
        coord[0] = FE - r * sin(theta);
        coord[1] = FN - r * cos(theta);
        return coord;
    }

    /**
     * Creates the inverse operation for Krovak (North Orientated) Projection.
     * Input coord is supposed to be a projected easting / northing coordinate
     * in meters. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new Krovak(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double Xp = -coord[1] + FN;
                double Yp = -coord[0] + FE;
                double r = sqrt(Xp * Xp + Yp * Yp);
                double theta = atan(Yp / Xp);
                double D = theta / sin(latp);
                double T = 2 * (atan(pow(r0 / r, 1 / n) * tan((latp + PI / 2) / 2)) - PI / 4);
                double U = asin(cos(alphac) * sin(T) - sin(alphac) * cos(T) * cos(D));
                double V = asin(cos(T) * sin(D) / cos(U));
                double oldLat = 1E30;
                double lat = U;
                final int MAXITER = 10;
                double e = ellipsoid.getEccentricity();
                int iter = 0;
                while (++iter < MAXITER && Math.abs(lat - oldLat) > 1E-15) {
                    oldLat = lat;
                    lat = 2 * (atan(pow(tan((U + PI / 2) / 2) / t0, 1 / B) * pow((1 + e * sin(lat)) / (1 - e * sin(lat)), e / 2)) - PI / 4);
                }
                if (iter == MAXITER) {
                    throw new ArithmeticException("The inverse method diverges");
                }
                double lon = lon0 - V / B;
                coord[0] = lat;
                coord[1] = lon;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return Krovak.this;
            }

            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return Krovak.this.toString() + " inverse";
            }
        };
    }
}