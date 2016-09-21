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
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * The Oblique Mercator Projection (OMERC). <p>
 *
 * @author Jules Party
 */
public class ObliqueMercator extends Projection {

    /**
     * The Identifier used for all Oblique Mercator projections.
     */
    public static final Identifier OMERC =
            new Identifier("EPSG", "9815", "Oblique Mercator", "OMERC");
    protected final double latc, // latitude of the projection center
            lonc, // longitude of the projection center
            alphac, // azimuth of the initial line
            gammac, // angle from the rectified grid to the skew (oblique) grid
            kc, // scale factor on the initial line
            FE, // false easting
            FN, // false northing
            B, //constant of the projection
            A, //constant of the projection
            H, //constant of the projection
            gamma0, //constant of the projection
            lambda0, //constant of the projection
            uc; // center of the projection
    protected final double[] invcoeff;

    /**
     * Create a new Oblique Mercator Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters FE, FN and other parameters useful for the
     * projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public ObliqueMercator(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(OMERC, ellipsoid, parameters);
        lonc = getCentralMeridian();
        latc = getLatitudeOfOrigin();
        alphac = getAzimuth();
        gammac = getRectifiedGridAngle();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        kc = getScaleFactor();
        double e = ellipsoid.getEccentricity();
        double e2 = ellipsoid.getSquareEccentricity();
        double esin = e * sin(latc);
        B = sqrt(1 + (e2 * pow(cos(latc), 4) / (1 - e2)));
        A = ellipsoid.getSemiMajorAxis() * B * kc * sqrt(1 - e2) / (1 - esin * esin);
        double t0 = tan((PI / 2 - latc) / 2) / pow((1 - esin) / (1 + esin), e / 2);
        double D = B * sqrt((1 - e2) / (1 - esin * esin)) / cos(latc);
        double F = (D < 1) ? D : D + sqrt(D * D - 1) * signum(latc);
        H = F * pow(t0, B);
        double G = (F - 1 / F) / 2;
        gamma0 = asin(sin(alphac) / D);
        lambda0 = lonc - asin(G * tan(gamma0)) / B;
        uc = (D > 1) ? A / B * atan(sqrt(D * D - 1) / cos(alphac)) * signum(latc) : 0;
        invcoeff = Mercator1SP.getInverseMercatorCoeff(ellipsoid);
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
     * Transform coord using the Oblique Mercator Projection. Input coord is
     * supposed to be a geographic latitude / longitude coordinate in radians.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double e = ellipsoid.getEccentricity();
        double esin = e * sin(coord[0]);
        double t = tan((PI / 2 - coord[0]) / 2) / pow((1 - esin) / (1 + esin), e / 2);
        double Q = H / pow(t, B);
        double S = (Q - 1 / Q) / 2;
        double T = (Q + 1 / Q) / 2;
        double V = sin(B * (coord[1] - lambda0));
        double U = (S * sin(gamma0) - V * cos(gamma0)) / T;
        double v = A * log((1 - U) / (1 + U)) / 2 / B;
        double u = A * atan((S * cos(gamma0) + V * sin(gamma0)) / cos(B * (coord[1] - lambda0))) / B - abs(uc) * signum(latc);
        coord[0] = FE + v * cos(gammac) + u * sin(gammac);
        coord[1] = FN + u * cos(gammac) - v * sin(gammac);
        return coord;
    }

    /**
     * Creates the inverse operation for Oblique Mercator Projection. Input
     * coord is supposed to be a projected easting / northing coordinate in
     * meters. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new ObliqueMercator(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double v = (coord[0] - FE) * cos(gammac) - (coord[1] - FN) * sin(gammac);
                double u = (coord[1] - FN) * cos(gammac) + (coord[0] - FE) * sin(gammac) + abs(uc) * signum(latc);
                double Q = exp(-B * v / A);
                double S = (Q - 1 / Q) / 2;
                double T = (Q + 1 / Q) / 2;
                double V = sin(B * u / A);
                double U = (V * cos(gamma0) + S * sin(gamma0)) / T;
                double t = pow(H / sqrt((1 + U) / (1 - U)), 1 / B);
                double ki = 2 * (PI / 4 - atan(t));
                double lat = ki;
                for (int i = 1; i < 5; i++) {
                    lat += invcoeff[i] * sin(2 * i * ki);
                }
                coord[0] = lat;
                coord[1] = lambda0 - atan((S * cos(gamma0) - V * sin(gamma0)) / cos(B * u / A)) / B;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return ObliqueMercator.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return ObliqueMercator.this.toString() + " inverse";
            }
        };
    }
}