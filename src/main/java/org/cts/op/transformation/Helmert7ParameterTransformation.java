/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
 * Michaud.
 * The new CTS has been funded  by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-08-VILL-0005-01 and the regional council 
 * "Région Pays de La Loire" under the projet SOGVILLE (Système d'Orbservation 
 * Géographique de la Ville).
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/irstv/cts/>
 */
package org.cts.op.transformation;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.op.AbstractCoordinateOperation;

/**
 * Helmert 7-Parameter transformations are mathematical similarities in a three
 * dimensional space.<p> There are several subclasses of the
 * Helmert7ParameterTransformation because there are several rotation
 * conventions and several implementations : <ul> <li>Simple 3D translation
 * (Helmert transformation with null rotations and scale factor)</li>
 * <li>Transformations based on two opposite rotation sign convention :</li>
 * <ul> <li>Position Vector where rotations are relative to datum 1. Mostly used
 * in Europe. It is used by the International Association of Geodesy and
 * recommended by ISO 19111. In this convention, rx, ry and rz are the rotations
 * to be applied to the point's vector.</li> <li>Coordinate Frame where
 * rotations are relative to datum 2. This convention is mostly used in USA and
 * recommended by NATO. Rotation parameters represent rotations to be applied to
 * the frame.</li> </ul> <li>Simplified implementations where sin of rotations
 * are replaced by the angle value in radians</li> <li>The Bursa-Wolfe
 * transformation is a helmert transformation with sin(rot)=rot and
 * cos(rot)=1.0. It is most often associated with Position Vector
 * convention.</li> </ul> </ul>
 *
 * @author Michael Michaud, Erwan Bocher
 */
abstract public class Helmert7ParameterTransformation extends AbstractCoordinateOperation implements GeoTransformation {

    // Transformation parameters in meters and radians
    protected final double tx, ty, tz, rx, ry, rz, scale, precision;

    /**
     * Transform coord values
     *
     * @param coord the coordinate to transform
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
     */
    @Override
    abstract public double[] transform(double[] coord)
            throws IllegalCoordinateException;

    /**
     * <p>Helmert 7-parameter transformation for geodesy calculation.</p>
     *
     * @param id transformation Identifier
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds)
     * @param ry_sec rotation parameter around y axis (seconds)
     * @param rz_sec rotation parameter around z axis (seconds)
     * @param ds_ppm scale factor
     * @param precision mean precision of the geodetic transformation
     */
    protected Helmert7ParameterTransformation(Identifier id,
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double ds_ppm, double precision) {
        super(id);
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.rx = rx_sec * Math.PI / 180.0 / 3600.0;
        this.ry = ry_sec * Math.PI / 180.0 / 3600.0;
        this.rz = rz_sec * Math.PI / 180.0 / 3600.0;
        this.scale = 1.0 + ds_ppm / 1000000.0;
        this.precision = precision;
    }

    /**
     * Returns this Helmert 7-Parameter Transformation as a String.
     */
    @Override
    public String toString() {
        return getName()
                + " (dX=" + (tx < 0 ? "" : "+") + tx + "m, "
                + "dY=" + (ty < 0 ? "" : "+") + ty + "m, "
                + "dZ=" + (tz < 0 ? "" : "+") + tz + "m, "
                + "rX=" + (rx < 0 ? "" : "+") + rx * 180.0 * 3600.0 / Math.PI + "\", "
                + "rY=" + (ry < 0 ? "" : "+") + ry * 180.0 * 3600.0 / Math.PI + "\", "
                + "rZ=" + (rz < 0 ? "" : "+") + rz * 180.0 * 3600.0 / Math.PI + "\", "
                + "ds=" + (scale < 0 ? "" : "+") + (scale - 1.0) * 1000000.0 + "ppm) "
                + "precision = " + precision;
    }

    /**
     * Two Helmert7ParameterTransformation are considered equal to each other if
     * all parameters are similar for a precision of less than a millimeter
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Helmert7ParameterTransformation) {
            Helmert7ParameterTransformation t = (Helmert7ParameterTransformation) o;
            if (getIdentifier().equals(t.getIdentifier())) {
                return true;
            }
            return Math.abs(tx - t.tx) < 0.001
                    && Math.abs(ty - t.ty) < 0.001
                    && Math.abs(tz - t.tz) < 0.001
                    && Math.abs(rx - t.rx) < 1.0E-10
                    && Math.abs(ry - t.ry) < 1.0E-10
                    && Math.abs(rz - t.rz) < 1.0E-10
                    && Math.abs(scale - t.scale) < 0.0000001;
        } else {
            return false;
        }
    }

    @Override
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append(",TOWGS84[");
        w.append((int) tx);
        w.append(',');
        w.append((int) ty);
        w.append(',');
        w.append((int) tz);
        w.append(',');
        w.append((int) rx);
        w.append(',');
        w.append((int) ry);
        w.append(',');
        w.append((int) rz);
        w.append(',');
        w.append((scale == 1 ? "0" : scale));
        w.append("]");
        return w.toString();
    }
}
