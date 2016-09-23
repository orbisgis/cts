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
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * The Equidistant Cylindrical projection (EQC). <p>
 *
 * @author Jules Party
 */
public class EquidistantCylindrical extends Projection {

    /**
     * The Identifier used for all Equidistant Cylindrical projections.
     */
    public static final Identifier EQC =
            new Identifier("EPSG", "1028", "Equidistant Cylindrical", "EQC");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN, // false northing
            C; // constant of the projection

    /**
     * Create a new Equidistant Cylindrical Projection corresponding to the
     * <code>Ellipsoid</code> and the list of parameters given in argument and
     * initialize common parameters lon0, lat0, FE, FN and C a constant useful
     * for the projection.
     *
     * @param ellipsoid ellipsoid used to define the projection.
     * @param parameters a map of useful parameters to define the projection.
     */
    public EquidistantCylindrical(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(EQC, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        double lat_ts = getLatitudeOfTrueScale();
        double e2 = ellipsoid.getSquareEccentricity();
        double k0 = cos(lat_ts) / sqrt(1 - e2 * pow(sin(lat_ts), 2));
        C = getSemiMajorAxis() * k0;
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
     * Transform coord using the Equidistant Cylindrical Projection. Input coord
     * is supposed to be a geographic latitude / longitude coordinate in
     * radians. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
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
        double E = C * (lon - lon0);
        double N = ellipsoid.arcFromLat(lat);
        coord[0] = FE + E;
        coord[1] = FN + N;
        return coord;
    }

    /**
     * Creates the inverse operation for Equidistant Cylindrical Projection.
     * Input coord is supposed to be a projected easting / northing coordinate
     * in meters. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     */
    @Override
    public Projection inverse() throws NonInvertibleOperationException {
        return new EquidistantCylindrical(ellipsoid, parameters) {
            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double lat = ellipsoid.latFromArc(coord[1] - FN);
                coord[1] = (coord[0] - FE) / C + lon0;
                coord[0] = lat;
                return coord;
            }

            @Override
            public Projection inverse()
                    throws NonInvertibleOperationException {
                return EquidistantCylindrical.this;
            }

            @Override
            public boolean isDirect() {
                return false;
            }

            @Override
            public String toString() {
                return EquidistantCylindrical.this.toString() + " inverse";
            }
        };
    }
}