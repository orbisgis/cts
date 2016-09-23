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

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.tan;

/**
 * The Cassini-Soldner Projection (CASS). <p>
 *
 * @author Jules Party
 */
public class CassiniSoldner extends Projection {

    /**
     * The Identifier used for all Cassini-Soldner projections.
     */
    public static final Identifier CASS =
            new Identifier("EPSG", "9806", "Cassini-Soldner", "CASS");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            M0, // the arc length between the equator and the reference latitude.
            FE, // x coordinate of the pole
            FN, // y coordinate of the pole
            k0, // scale coefficent for easting
            e, // eccentricity of the ellipsoid
            e2; // square eccentricity of the ellipsoid

    /**
     * Create a new Cassini-Soldner Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN and other parameters
     * useful for the projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public CassiniSoldner(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(CASS, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        e = ellipsoid.getEccentricity();
        e2 = ellipsoid.getSquareEccentricity();
        k0 = getScaleFactor();
        M0 = ellipsoid.arcFromLat(lat0);
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
     * Transform coord using the Cassini-Soldner Projection. Input coord is
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
        double lon = coord[1];
        double lat = coord[0];
        double A = (lon - lon0) * cos(lat);
        double A2 = A * A;
        double A4 = A2 * A2;
        double T = pow(tan(lat), 2);
        double C = e2 * pow(cos(lat), 2) / (1 - e2);
        double v = ellipsoid.transverseRadiusOfCurvature(lat);
        double M = ellipsoid.arcFromLat(lat);
        double dE = v * A * (1 - T * A2 / 6 - (8 * (1 + C) - T) * T * A4 / 120);
        double dN = M - M0 + v * tan(lat) * (A2 / 2 + (5 - T + 6 * C) * A4 / 24);
        coord[0] = FE + dE;
        coord[1] = FN + dN;
        return coord;
    }

    /**
     * Creates the inverse operation for Cassini-Soldner Projection. Input coord
     * is supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new CassiniSoldner(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double M1 = M0 + coord[1] - FN;
                double lat1 = ellipsoid.latFromArc(M1);
                double T1 = pow(tan(lat1), 2);
                double v1 = ellipsoid.transverseRadiusOfCurvature(lat1);
                double rho1 = ellipsoid.meridionalRadiusOfCurvature(lat1);
                double D = (coord[0] - FE) / v1;
                double D2 = D * D;
                coord[1] = lon0 + D * (1 - T1 * D2 / 3 + (1 + 3 * T1) * T1 * D2 * D2 / 15) / cos(lat1);
                coord[0] = lat1 - v1 * tan(lat1) / rho1 * D2 / 2 * (1 - (1 + 3 * T1) * D2 / 12);
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return CassiniSoldner.this;
            }

            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return CassiniSoldner.this.toString() + " inverse";
            }
        };
    }
}