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
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * The Albers Equal Area Projection (AEA). <p>
 *
 * @author Jules Party
 */
public class AlbersEqualArea extends Projection {

    /**
     * The Identifier used for all Albers Equal Area projections.
     */
    public static final Identifier AEA =
            new Identifier("EPSG", "9822", "Albers Equal Area", "AEA");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN, // false northing
            rho0, // constant of the projection for north axis
            C, // constant of the projection
            n; // exponent of the projection

    /**
     * Create a new Albers Equal Area Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN and other parameters
     * useful for the projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public AlbersEqualArea(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(AEA, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        double e2 = ellipsoid.getSquareEccentricity();
        double lat1 = getStandardParallel1();
        double alpha1 = q(lat1);
        double m1 = cos(lat1) / sqrt(1 - e2 * sin(lat1) * sin(lat1));
        double lat2 = getStandardParallel2();
        double alpha2 = q(lat2);
        double m2 = cos(lat2) / sqrt(1 - e2 * sin(lat2) * sin(lat2));
        n = (m1 * m1 - m2 * m2) / (alpha2 - alpha1);
        C = m1 * m1 + n * alpha1;
        rho0 = ellipsoid.getSemiMajorAxis() / n * sqrt(C - n * q(lat0));
    }

    /**
     * Calculation of q from the equation (3-12) of Snyder in the USGS
     * professional paper 1395, "Map Projection - A Working Manual" by John P.
     * Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     * NB : q is named alpha in OGP's Guidance Note, it is why the result is
     * stored in a parameter whose name is alpha
     */
    private double q(double lat) {
        double e = ellipsoid.getEccentricity();
        double esin = e * sin(lat);
        return (1 - e * e) * (sin(lat) / (1 - esin * esin) - log((1 - esin) / (1 + esin)) / 2 / e);
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
     * Transform coord using the Albers Equal Area Projection. Input coord is
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
        double theta = n * (coord[1] - lon0);
        double rho = ellipsoid.getSemiMajorAxis() / n * sqrt(C - n * q(coord[0]));
        coord[0] = FE + rho * sin(theta);
        coord[1] = FN + rho0 - rho * cos(theta);
        return coord;
    }

    /**
     * Creates the inverse operation for Albers Equal Area Projection. Input
     * coord is supposed to be a projected easting / northing coordinate in
     * meters. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new AlbersEqualArea(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double e = ellipsoid.getEccentricity();
                double e2 = ellipsoid.getSquareEccentricity();
                double e4 = e2 * e2;
                double e6 = e4 * e2;
                double x = coord[0] - FE;
                double y = rho0 - (coord[1] - FN);
                double theta = atan(x / y);
                double rho = sqrt(x * x + y * y);
                double alphap = (C - pow(rho * n / ellipsoid.getSemiMajorAxis(), 2)) / n;
                double betap = asin(alphap / (1 - (1 - e2) / 2 / e * log((1 - e) / (1 + e))));
                coord[0] = betap + (e2 / 3 + 31 / 180 * e4 + 517 / 5040 * e6) * sin(2 * betap)
                        + (23 / 360 * e4 + 251 / 3780 * e6) * sin(4 * betap) + 761 / 45360 * e6 * sin(6 * betap);
                coord[1] = lon0 + theta / n;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return AlbersEqualArea.this;
            }

            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return AlbersEqualArea.this.toString() + " inverse";
            }
        };
    }
}