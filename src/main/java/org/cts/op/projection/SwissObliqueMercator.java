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
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * The Swiss Oblique Mercator Projection (SOMERC). <p>
 * This projection is a special case of the Oblique Mercator Projection where
 * the azimuth of the line through the projection center (alpha) is 90 degrees.
 *
 * @author Jules Party
 */
public class SwissObliqueMercator extends Projection {

    /**
     * The Identifier used for all Swiss Oblique Mercator projections.
     */
    public static final Identifier SOMERC =
            new Identifier("EPSG", "9815", "Swiss Oblique Mercator", "SOMERC");
    protected final double latc, // latitude of the projection center
            lonc, // longitude of the projection center
            kc, // scale factor on the initial line
            FE, // false easting
            FN, // false northing
            alpha, // relation between longitude on sphere and on ellipsoid
            b0, // latitude of the foundamental point on the sphere
            K, // constant of the latitude formula
            R; // Radius of the projection sphere

    /**
     * Create a new Swiss Oblique Stereographic Alternative Projection
     * corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters FE, FN and other parameters useful for the
     * projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public SwissObliqueMercator(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(SOMERC, ellipsoid, parameters);
        lonc = getCentralMeridian();
        latc = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        kc = getScaleFactor();
        double e = ellipsoid.getEccentricity();
        double e2 = ellipsoid.getSquareEccentricity();
        double esin = e * sin(latc);
        alpha = sqrt(1 + (e2 * pow(cos(latc), 4) / (1 - e2)));
        R = ellipsoid.getSemiMajorAxis() * kc * sqrt(1 - e2) / (1 - esin * esin);
        b0 = asin(sin(latc) / alpha);
        K = log(tan((PI / 2 + b0) / 2)) - alpha * log(tan((PI / 2 + latc) / 2)) + alpha * e / 2 * log((1 + e * sin(latc)) / (1 - e * sin(latc)));
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
     * Transform coord using the Swiss Oblique Mercator Projection. Input coord
     * is supposed to be a geographic latitude / longitude coordinate in
     * radians. Algorithm based on a Swiss Federal Office of Topography document
     * :
     * <http://www.swisstopo.admin.ch/internet/swisstopo/en/home/topics/survey/sys/refsys/switzerland.parsysrelated1.37696.downloadList.97912.DownloadFile.tmp/swissprojectionen.pdf>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double e = ellipsoid.getEccentricity();
        double S = alpha * log(tan((PI / 2 + coord[0]) / 2)) - alpha * e / 2 * log((1 + e * sin(coord[0])) / (1 - e * sin(coord[0]))) + K;
        double b = 2 * (atan(exp(S)) - PI / 4);
        double I = alpha * (coord[1] - lonc);
        double Ibar = atan(sin(I) / (sin(b0) * tan(b) + cos(b0) * cos(I)));
        double bbar = asin(cos(b0) * sin(b) - sin(b0) * cos(b) * cos(I));
        double Y = R * Ibar;
        double X = R / 2 * log((1 + sin(bbar)) / (1 - sin(bbar)));
        coord[0] = FE + Y;
        coord[1] = FN + X;
        return coord;
    }

    private double findLatSwissObliqueMercator(double b) {
        final int MAXITER = 10;
        double e = ellipsoid.getEccentricity();
        double oldLat = 1E30;
        double S;
        double lat = b;
        int iter = 0;
        while (++iter < MAXITER && Math.abs(lat - oldLat) > 1E-15) {
            oldLat = lat;
            S = (log(tan((PI / 2 + b) / 2)) - K) / alpha + e * log(tan((PI / 2 + asin(e * sin(oldLat))) / 2));
            lat = 2 * (atan(exp(S)) - PI / 4);
        }
        if (iter == MAXITER) {
            throw new ArithmeticException("The findLatSwissObliqueMercator method diverges");
        }
        return lat;
    }

    /**
     * Creates the inverse operation for Swiss Oblique Mercator Projection.
     * Input coord is supposed to be a projected easting / northing coordinate
     * in meters. Algorithm based on a Swiss Federal Office of Topography
     * document:
     * <http://www.swisstopo.admin.ch/internet/swisstopo/en/home/topics/survey/sys/refsys/switzerland.parsysrelated1.37696.downloadList.97912.DownloadFile.tmp/swissprojectionen.pdf>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new SwissObliqueMercator(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double X = (coord[1] - FN);
                double Y = (coord[0] - FE);
                double Ibar = Y / R;
                double bbar = 2 * (atan(exp(X / R)) - PI / 4);
                double b = asin(cos(b0) * sin(bbar) + sin(b0) * cos(bbar) * cos(Ibar));
                double I = atan(sin(Ibar) / (cos(b0) * cos(Ibar) - sin(b0) * tan(bbar)));
                coord[1] = lonc + I / alpha;
                coord[0] = findLatSwissObliqueMercator(b);
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return SwissObliqueMercator.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return SwissObliqueMercator.this.toString() + " inverse";
            }
        };
    }
}