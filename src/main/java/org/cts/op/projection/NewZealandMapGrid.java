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
 * The New Zealand Map Grid Projection (NZMG). <p>
 *
 * @author Jules Party
 */
public class NewZealandMapGrid extends Projection {

    /**
     * The Identifier used for all New Zealand Map Grid projections.
     */
    public static final Identifier NZMG =
            new Identifier("EPSG", "9811", "New Zealand Map Grid", "NZMG");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN;   // false northing
    protected final Complex[] B;

    /**
     * Create a new New Zealand Map Grid Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN and other parameters
     * useful for the projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public NewZealandMapGrid(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(NZMG, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        B = new Complex[7];
        B[1] = new Complex(0.7557853228);
        B[2] = new Complex(0.249204646, 0.003371507);
        B[3] = new Complex(-0.001541739, 0.041058560);
        B[4] = new Complex(-0.10162907, 0.01727609);
        B[5] = new Complex(-0.26623489, -0.36249218);
        B[6] = new Complex(-0.6870983, -1.1651967);
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.MISCELLANEOUS;
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
     * Transform coord using the New Zealand Map Grid Projection. Input coord is
     * supposed to be a geographic latitude / longitude coordinate in radians.
     * Algorithm based on the USGS professional paper 1395, "Map Projection - A
     * Working Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double lambda = coord[1] - lon0;
        double isoPhi = ellipsoid.isometricLatitude(coord[0]) - ellipsoid.isometricLatitude(lat0);
        Complex zeta = new Complex(isoPhi, lambda);
        Complex origin = new Complex(FN, FE);
        Complex z = B[6].axpb(zeta, B[5]).axpb(zeta, B[4]).axpb(zeta, B[3]).axpb(zeta, B[2]).axpb(zeta, B[1]).times(zeta).times(ellipsoid.getSemiMajorAxis());
        Complex coordi = origin.plus(z);
        coord[0] = coordi.im();
        coord[1] = coordi.re();
        return coord;
    }

    /**
     * Creates the inverse operation for New Zealand Map Grid Projection. Input
     * coord is supposed to be a projected easting / northing coordinate in
     * meters. Algorithm based on the USGS professional paper 1395, "Map
     * Projection - A Working Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new NewZealandMapGrid(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                Complex z = (new Complex(coord[1] - FN, coord[0] - FE)).divideBy(new Complex(ellipsoid.getSemiMajorAxis()));
                Complex[] b = new Complex[7];
                b[1] = new Complex(1.3231270439);
                b[2] = new Complex(-0.577245789, -0.007809598);
                b[3] = new Complex(0.508307513, -0.112208952);
                b[4] = new Complex(-0.15094762, 0.18200602);
                b[5] = new Complex(1.01418179, 1.64497696);
                b[6] = new Complex(1.9660549, 2.5127645);
                Complex zeta = b[6].axpb(z, b[5]).axpb(z, b[4]).axpb(z, b[3]).axpb(z, b[2]).axpb(z, b[1]).times(z);
                zeta = (B[6].times(5).axpb(zeta, B[5].times(4)).axpb(zeta, B[4].times(3)).axpb(zeta, B[3].times(2)).axpb(zeta, B[2]).times(zeta).axpb(zeta, z))
                        .divideBy(B[6].times(6).axpb(zeta, B[5].times(5)).axpb(zeta, B[4].times(4)).axpb(zeta, B[3].times(3)).axpb(zeta, B[2].times(2)).axpb(zeta, B[1]));
                zeta = (B[6].times(5).axpb(zeta, B[5].times(4)).axpb(zeta, B[4].times(3)).axpb(zeta, B[3].times(2)).axpb(zeta, B[2]).times(zeta).axpb(zeta, z))
                        .divideBy(B[6].times(6).axpb(zeta, B[5].times(5)).axpb(zeta, B[4].times(4)).axpb(zeta, B[3].times(3)).axpb(zeta, B[2].times(2)).axpb(zeta, B[1]));
                double lon = zeta.im();
                double isoLat = zeta.re();
                coord[0] = ellipsoid.latitude(isoLat + ellipsoid.isometricLatitude(lat0));
                coord[1] = lon0 + lon;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return NewZealandMapGrid.this;
            }
            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return NewZealandMapGrid.this.toString() + " inverse";
            }
        };
    }
}