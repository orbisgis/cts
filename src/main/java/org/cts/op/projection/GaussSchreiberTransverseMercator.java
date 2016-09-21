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
import static java.lang.Math.cosh;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sinh;
import static java.lang.Math.sqrt;

/**
 * The Gauss Schreiber Transverse Mercator (GSTMERC). <p>
 *
 * @author Jules Party
 */
public class GaussSchreiberTransverseMercator extends Projection {

    /**
     * The Identifier used for all Gauss Schreiber Transverse Mercator
     * projections.
     */
    public static final Identifier GSTMERC =
            new Identifier("IGNF", "REUN47GAUSSL", "Gauss Schreiber Transverse Mercator (aka Gauss Laborde Réunion", "GSTMERC");
    protected final double lon0, // the reference longitude (from the datum prime meridian)
            latc, // latitude of the origin on the medium sphere
            c, // constant of the projection
            n1, // exponent of the ellipsoid to sphere projection
            n2, // radius of the medium sphere
            xs, // x coordinate of the pole
            ys;   // y coordinate of the pole

    /**
     * Create a new Gauss Schreiber Transverse Mercator Projection corresponding
     * to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0 and other parameters useful for the
     * projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public GaussSchreiberTransverseMercator(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(GSTMERC, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        double lat0 = getLatitudeOfOrigin();
        double FE = getFalseEasting();
        double FN = getFalseNorthing();
        double k0 = getScaleFactor();
        double e2 = ellipsoid.getSquareEccentricity();
        n1 = sqrt(1 + e2 / (1 - e2) * pow(cos(lat0), 4));
        latc = asin(sin(lat0) / n1);
        c = Ellipsoid.SPHERE.isometricLatitude(latc) - n1 * ellipsoid.isometricLatitude(lat0);
        n2 = k0 * ellipsoid.getSemiMajorAxis() * sqrt(1 - e2) / (1 - e2 * sin(lat0) * sin(lat0));
        xs = FE;
        ys = FN - n2 * latc;
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.PSEUDOCYLINDRICAL;
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
        return Projection.Orientation.TRANSVERSE;
    }

    /**
     * Transform coord using the Gauss Schreiber Transverse Mercator Projection.
     * Input coord is supposed to be a geographic latitude / longitude
     * coordinate in radians. Algorithm based on the OGP's Guidance Note Number
     * 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double Lambda = n1 * (coord[1] - lon0);
        double isoLats = c + n1 * ellipsoid.isometricLatitude(coord[0]);
        coord[0] = xs + n2 * Ellipsoid.SPHERE.isometricLatitude(asin(sin(Lambda) / cosh(isoLats)));
        coord[1] = ys + n2 * atan(sinh(isoLats) / cos(Lambda));
        return coord;
    }

    /**
     * Creates the inverse operation for Gauss Schreiber Transverse Mercator
     * Projection. Input coord is supposed to be a projected easting / northing
     * coordinate in meters. Algorithm based on the OGP's Guidance Note Number 7
     * Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new GaussSchreiberTransverseMercator(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double Lambda = atan(sinh((coord[0] - xs) / n2) / cos((coord[1] - ys) / n2));
                double isoLats = Ellipsoid.SPHERE.isometricLatitude(asin(sin((coord[1] - ys) / n2) / cosh((coord[0] - xs) / n2)));
                coord[0] = ellipsoid.latitude((isoLats - c) / n1);
                coord[1] = lon0 + Lambda / n1;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return GaussSchreiberTransverseMercator.this;
            }

            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return GaussSchreiberTransverseMercator.this.toString() + " inverse";
            }
        };
    }
}