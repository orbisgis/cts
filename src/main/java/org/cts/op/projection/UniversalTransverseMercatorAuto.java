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
import org.cts.util.Complex;

/**
 * A map projection is any method used in cartography (mapmaking) to represent
 * the two-dimensional curved surface of the earth or other body on a plane. The
 * term "projection" here refers to any function defined on the earth's surface
 * and with values on the plane, and not necessarily a geometric projection.<p>
 *
 * @author Michaël Michaud
 */
public class UniversalTransverseMercatorAuto extends Projection {

    /**
     * The Identifier used for all Universal Transverse Mercator projections.
     */
    public static final Identifier UTM =
            new Identifier("EPSG", "9824", "Transverse Mercator Zoned Grid System", "UTM");
    protected final double lat0, n, xs;
    protected final double[] dircoeff, invcoeff;

    /**
     * Creates a new Universal Transverse Mercator Projection based on the given
     * <code>Ellipsoid</code> and parameters.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public UniversalTransverseMercatorAuto(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(UTM, ellipsoid, parameters);
        lat0 = 0.0;
        n = 0.9996 * ellipsoid.getSemiMajorAxis();
        xs = 500000;
        dircoeff = UniversalTransverseMercator.getDirectUTMCoeff(ellipsoid);
        invcoeff = UniversalTransverseMercator.getInverseUTMCoeff(ellipsoid);
    }

    public int getZone(double[] coord) {
        return (int) ((coord[1] * 180.0 / Math.PI + 180.0) / 6.0) + 1;
    }

    /**
     * Transform coord using a Universal Transverse Mercator projection. Input
     * coord is supposed to be a geographic latitude / longitude coordinate in
     * radians.
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) {
        int zone = getZone(coord);
        double lon0 = (6 * zone - 183) * Math.PI / 180;
        double y0 = coord[0] >= 0 ? 0 : 10000000;
        double ys = y0 - n * ellipsoid.curvilinearAbscissa(lat0); // = y0
        double latIsoPhi = ellipsoid.isometricLatitude(coord[0]);
        double PHI = Math.asin(Math.sin(coord[1] - lon0) / Math.cosh(latIsoPhi));
        double latIsoPHI = Ellipsoid.SPHERE.isometricLatitude(PHI);
        double lambda = Math.atan(Math.sinh(latIsoPhi) / Math.cos(coord[1] - lon0));
        Complex z = new Complex(lambda, latIsoPHI);
        Complex Z = z.times(n * dircoeff[0]);
        for (int i = 1; i < 5; i++) {
            Z = Z.plus(Complex.sin(z.times(2.0 * i)).times(n * dircoeff[i]));
        }
        coord[0] = xs + Z.im();
        coord[1] = ys + Z.re();
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public Projection inverse() {
        return new UniversalTransverseMercatorAuto(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) {
                double lon0 = 0;
                double ys = coord[0] >= 0 ? 0 : 10000000;
                Complex z = new Complex((coord[1] - ys) / (n * invcoeff[0]),
                        (coord[0] - xs) / (n * invcoeff[0]));
                Complex Z = z;
                for (int i = 1; i < 5; i++) {
                    Z = Z.plus(Complex.sin(z.times((2.0 * i))).times(-invcoeff[i]));
                }
                double lon = lon0 + Math.atan(Math.sinh(Z.im()) / Math.cos(Z.re()));
                double PHI = Math.asin(Math.sin(Z.re()) / Math.cosh(Z.im()));
                double latIso = Ellipsoid.SPHERE.isometricLatitude(PHI);
                double lat = ellipsoid.latitude(latIso);
                coord[0] = lat;
                coord[1] = lon;
                return coord;
            }

            @Override
            public Projection inverse() {
                return UniversalTransverseMercatorAuto.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return UniversalTransverseMercatorAuto.this.toString() + " inverse";
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
        return Projection.Orientation.TRANSVERSE;
    }
}
