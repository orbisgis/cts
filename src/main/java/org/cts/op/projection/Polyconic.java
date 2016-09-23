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
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * The Polyconic (American) Projection (POLY). <p>
 *
 * @author Jules Party
 */
public class Polyconic extends Projection {

    /**
     * The Identifier used for all Polyconic projections.
     */
    public static final Identifier POLY =
            new Identifier("EPSG", "9818", "Polyconic (American)", "POLY");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN;   // false northing

    /**
     * Create a new Polyconic Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public Polyconic(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(POLY, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.PSEUDOCONICAL;
    }

    /**
     * Return the
     * <code>Property</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Property getProperty() {
        return Projection.Property.APHYLACTIC;
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
     * Transform coord using the Polyconic Projection. Input coord is supposed
     * to be a geographic latitude / longitude coordinate in radians. Algorithm
     * based on the USGS professional paper 1395, "Map Projection - A Working
     * Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double a = ellipsoid.getSemiMajorAxis();
        double M0 = a * ellipsoid.curvilinearAbscissa(lat0);
        if (coord[0] == 0) {
            coord[0] = FE + ellipsoid.getSemiMajorAxis() * (coord[1] - lon0);
            coord[1] = FN - M0;
        } else {
            double M = a * ellipsoid.curvilinearAbscissa(coord[0]);
            double v = ellipsoid.transverseRadiusOfCurvature(coord[0]);
            double L = (coord[1] - lon0) * sin(coord[0]);
            coord[1] = FN + M - M0 + v / tan(coord[0]) * (1 - cos(L));
            coord[0] = FE + v / tan(coord[0]) * sin(L);
        }
        return coord;
    }

    private double curvilinearAbscissaPrime(double latitude) {
        double[] arc_coeff = ellipsoid.getArcCoeff();
        return arc_coeff[0]
                + 2 * arc_coeff[1] * cos(2 * latitude)
                + 4 * arc_coeff[2] * cos(4 * latitude)
                + 6 * arc_coeff[3] * cos(6 * latitude)
                + 8 * arc_coeff[4] * cos(8 * latitude);
    }

    /**
     * Creates the inverse operation for Polyconic Projection. Input coord is
     * supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the USGS professional paper 1395, "Map Projection - A
     * Working Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new Polyconic(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double a = ellipsoid.getSemiMajorAxis();
                double M0 = a * ellipsoid.curvilinearAbscissa(lat0);
                double e2 = ellipsoid.getSquareEccentricity();
                double x = coord[0] - FE;
                double y = coord[1] - FN;
                if (y + M0 == 0) {
                    coord[0] = 0;
                    coord[1] = lon0 + x / a;
                } else {
                    double A = (y + M0) / a;
                    double B = A * A + pow(x / a, 2);
                    double C = 0;
                    final int MAXITER = 10;
                    double lat = A, latold = 1.E30;
                    int iter = 0;
                    while (++iter < MAXITER && Math.abs(lat - latold) > 1.E-15) {
                        latold = lat;
                        C = sqrt(1 - e2 * sin(lat) * sin(lat)) * tan(lat);
                        double J = ellipsoid.curvilinearAbscissa(lat);
                        double I = curvilinearAbscissaPrime(lat);
                        lat = latold - (A * (C * J + 1) - J - C / 2 * (J * J + B)) / (e2 * sin(2 * latold) * (J * (J - 2 * A) + B) / 4 / C + (A - J) * (C * I - 2 / sin(2 * latold)) - I);
                    }
                    if (iter == MAXITER) {
                        throw new ArithmeticException("The inverse Polyconic Projection method diverges. Last value of tolerance = " + Math.abs(lat - latold));
                    }
                    coord[0] = lat;
                    coord[1] = lon0 + asin(x * C / a) / sin(lat);
                }
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return Polyconic.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return Polyconic.this.toString() + " inverse";
            }
        };
    }
}