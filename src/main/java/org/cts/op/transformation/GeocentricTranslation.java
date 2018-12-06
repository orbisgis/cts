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
package org.cts.op.transformation;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.op.CoordinateOperation;
import org.cts.op.NonInvertibleOperationException;

/**
 * <p>GeocentricTranslation is a coordinate operation used to transform
 * geocentric coordinates with a 3D translation defined by three parameters
 * representing translation values along x axis, y axis and z axis.</p> <p>In
 * this operation, one assume that the axis of the ellipsoids are parallel, that
 * the prime meridian is Greenwich, and that there is no scale difference
 * between the source and target CoordinateReferenceSystem.</p> <p>Equations of
 * this transformation are : <ul> <li>X' = X + tx</li> <li>Y' = Y + ty</li>
 * <li>Z' = Z + tz</li> </ul> </p>
 *
 * @author Michaël Michaud, Erwan Bocher
 */
public class GeocentricTranslation extends AbstractCoordinateOperation implements GeoTransformation, ParamBasedTransformation {

    /**
     * The Identifier used for all Geocentric translations.
     */
    private static final Identifier opId =
            new Identifier("EPSG", "9603", "Geocentric translation", "Translation");
    /**
     * Translation value used in this Geocentric translation.
     */
    private double tx, ty, tz;

    // Inverse translation
    private GeocentricTranslation inverse;

    /**
     * <p>Geocentric translation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param precision mean precision of the geodetic transformation
     */
    public GeocentricTranslation(double tx, double ty, double tz, double precision) {
        super(opId);
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.precision = Math.min(1.0, precision);
    }

    /**
     * Geocentric translation.
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     */
    public GeocentricTranslation(double tx, double ty, double tz) {
        this(tx, ty, tz, 1.0);
    }

    /**
     * <p>Return a coordinates representing the same point as coord but in
     * another CoordinateReferenceSystem.</p> <p>Equations of this
     * transformation are : <ul> <li>X' = X + tx</li> <li>Y' = Y + ty</li>
     * <li>Z' = Z + tz</li> </ul> </p>
     *
     * @param coord coordinate to transform
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length < 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        coord[0] = tx + coord[0];
        coord[1] = ty + coord[1];
        coord[2] = tz + coord[2];
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public GeocentricTransformation inverse() {
        if (inverse != null) return inverse;
        else {
            return inverse = new GeocentricTranslation(-tx, -ty, -tz, precision);
        }
    }

    /**
     * Returns this Geocentric translation as a String.
     */
    @Override
    public String toString() {
        return "Geocentric translation (dX=" + (tx < 0 ? "" : "+") + tx + "m, "
                + "dY=" + (ty < 0 ? "" : "+") + ty + "m, "
                + "dZ=" + (tz < 0 ? "" : "+") + tz + "m) "
                + "precision = " + precision;
    }

    /**
     * Returns this Geocentric translation as an OGC WKT String.
     */
    @Override
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append(",TOWGS84[");
        if (Math.abs(tx - Math.rint(tx)) < 1e-9) {
            w.append((int) tx);
        } else {
            w.append(tx);
        }
        w.append(',');
        if (Math.abs(ty - Math.rint(ty)) < 1e-9) {
            w.append((int) ty);
        } else {
            w.append(ty);
        }
        w.append(',');
        if (Math.abs(tz - Math.rint(tz)) < 1e-9) {
            w.append((int) tz);
        } else {
            w.append(tz);
        }
        w.append(",0,0,0,0]");
        return w.toString();
    }

    /**
     * Returns true if o is equals to <code>this</code>.
     * GeocentricTranslations are equals if they both are identity, or
     * if all their parameters are equal.
     *
     * @param o The object to compare this ProjectedCRS against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CoordinateOperation) {
            if (this.isIdentity() && ((CoordinateOperation)o).isIdentity()) {
                return true;
            }
            if (o instanceof GeocentricTranslation) {
                GeocentricTranslation gt = (GeocentricTranslation) o;
                return ((this.tx == gt.tx) && (this.ty == gt.ty) && (this.tz == gt.tz));
            }
        }
        return false;
    }

    /**
     * Returns the hash code for this GeocentricTranslation.
     */
    @Override
    public int hashCode() {
        if (isIdentity()) return 0;
        int hash = 5;
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.tx) ^ (Double.doubleToLongBits(this.tx) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.ty) ^ (Double.doubleToLongBits(this.ty) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.tz) ^ (Double.doubleToLongBits(this.tz) >>> 32));
        return hash;
    }

    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity() {
        return tx == 0.0 && ty == 0.0 && tz == 0.0;
    }
}
