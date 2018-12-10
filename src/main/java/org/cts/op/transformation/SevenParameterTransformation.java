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

import static java.lang.Math.sin;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.op.CoordinateOperation;
import org.cts.op.NonInvertibleOperationException;

/**
 * Seven-Parameter transformations are mathematical similarities or Helmert
 * transformations in a three dimensional space.<p> In the geodesy domain,
 * formulas are often linearized, considering that rotation angles are small
 * enough to replace sin(x) by x, cos(x) by 1 and sin(x)*sin(x) by 0. This is
 * the case of Bursa-Wolf formulas and Coordinate- frame rotation. If you want
 * to use non linearized formulas, please, use the
 * createSevenParameterTransformation constructor<p> Geodesian also use two
 * different conventions for rotation angle : <ul><li>The Position Vector
 * convention, used in Bursa-Wolf formulas are widely used in Europe. It is used
 * by the International Association of Geodesy and recommended by ISO 19111). In
 * this convention, rx, ry and rz are the rotations to be applied to the point's
 * vector.</li> <li>The CoordinateFrame convention uses opposite values for
 * rotations. In this convention, most used in USA and recommended by NATO,
 * rotation parameters represent rotations to be applied to the frame.</li>
 *
 * @author Michaël Michaud
 */
public class SevenParameterTransformation extends AbstractCoordinateOperation
        implements GeoTransformation, ParamBasedTransformation {

    /**
     * POSITION_VECTOR sign convention is such that a positive rotation about an
     * axis is defined as a clockwise rotation of the position vector when
     * viewed from the origin of the cartesian coordinate reference system in
     * the positive direction of that axis; e.g. a positive rotation about the
     * Z-axis only from source system to target system will result in a larger
     * longitude value for the point in the target system. [EPSG : Coordinate
     * Conversions and Transformations including Formulas - Revised April 2006]
     */
    public final static int POSITION_VECTOR = 0;

    /**
     * COORDINATE_FRAME sign convention is such that a positive rotation of the
     * frame about an axis is defined as a clockwise rotation of the coordinate
     * reference frame when viewed from the origin of the cartesian coordinate
     * reference system in the positive direction of that axis, that is a
     * positive rotation about the Z-axis only from source coordinate reference
     * system to target coordinate reference system will result in a smaller
     * longitude value for the point in the target coordinate reference system.
     * [EPSG : Coordinate Conversions and Transformations including Formulas -
     * Revised April 2006]
     */
    public final static int COORDINATE_FRAME = 1;

    /**
     * LINEARIZED when formulas use x (rad) instead of sin(x).
     */
    public final static boolean LINEARIZED = true;

    /**
     * NOT_LINEARIZED when formulas use exact sin(x) function.
     */
    public final static boolean NOT_LINEARIZED = false;

    private final static Identifier idBW = new Identifier("EPSG", "1033", "Position vector 7-parameter transformation (linearized)", "Bursa-Wolf (lin.)");
    private final static Identifier idSinBW = new Identifier(SevenParameterTransformation.class, "Position vector 7-parameter transformation");
    private final static Identifier idCFR = new Identifier("EPSG", "1032", "Frame Rotation (lin.)", "Coordinate Frame rotation (linearized)");
    private final static Identifier idSinCFR = new Identifier(SevenParameterTransformation.class, "Coordinate Frame rotation");
    private final static Identifier idT = new Identifier("EPSG", "1031", "Geocentric translation", "Translation");
    private final double tx, ty, tz, rx, ry, rz, scale;
    private final int rotationConvention;
    private final boolean linearized;

    // Inverse transformation
    private SevenParameterTransformation inverse;

    /**
     * <p>7-parameter transformation for geodesy calculation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx rotation parameter around x axis (radians)
     * @param ry rotation parameter around y axis (radians)
     * @param rz rotation parameter around z axis (radians)
     * @param scale scale factor
     * @param rotationConvention rotation convention (POSITION_VECTOR or
     * COORDINATE_FRAME)
     * @param linearized true means sin(x) is replaced by x
     * @param precision mean precision of the geodetic transformation
     */
    private SevenParameterTransformation(double tx, double ty, double tz,
            double rx, double ry, double rz,
            double scale, int rotationConvention,
            boolean linearized, double precision) {
        super(idT);
        if (rx == 0.0 && ry == 0.0 && rz == 0.0 && scale == 1.0) {
            setIdentifier(idT);
        } else if (linearized) {
            if (rotationConvention == POSITION_VECTOR) {
                setIdentifier(idBW);
            } else if (rotationConvention == COORDINATE_FRAME) {
                setIdentifier(idCFR);
            } else {
                setIdentifier(new Identifier(SevenParameterTransformation.class));
            }
        } else {
            if (rotationConvention == POSITION_VECTOR) {
                setIdentifier(idSinBW);
            } else if (rotationConvention == COORDINATE_FRAME) {
                setIdentifier(idSinCFR);
            } else {
                setIdentifier(new Identifier(SevenParameterTransformation.class));
            }
        }
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.scale = scale;
        this.rotationConvention = rotationConvention;
        this.linearized = linearized;
        this.precision = precision;
    }

    /**
     * <p>Create a 7-Parameter transformation with a default precision.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds)
     * @param ry_sec rotation parameter around y axis (seconds)
     * @param rz_sec rotation parameter around z axis (seconds)
     * @param ds_ppm scale factor in ppm (parts per million)
     * @param rotationConvention convention used for the rotation (Position-
     * Vector or Coordinate-Frame)
     * @param linearized true if the formula is linearized (sin(x) = x)
     * @return 
     */
    public static SevenParameterTransformation createSevenParameterTransformation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double ds_ppm, int rotationConvention, boolean linearized) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, rotationConvention, linearized, 0.5);
    }

    /**
     * <p>Create a 7-parameter transformation with a specific precision.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds)
     * @param ry_sec rotation parameter around y axis (seconds)
     * @param rz_sec rotation parameter around z axis (seconds)
     * @param ds_ppm scale factor in ppm (parts per million)
     * @param rotationConvention convention used for the rotation (Position-
     * Vector or Coordinate-Frame)
     * @param linearized true if the formula is linearized (sin(x) = x)
     * @param precision precision of the transformation in meters
     */
    public static SevenParameterTransformation createSevenParameterTransformation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec, double ds_ppm,
            int rotationConvention, boolean linearized, double precision) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, rotationConvention, linearized, precision);
    }

    /**
     * <p>Create a Bursa-Wolf transformation with the default precision.</p>
     * <p>Bursa-Wolf transformation (or simplified seven parameters
     * transformation or linearized Helmert transformation) is a 3D similarity
     * with very small rotations (< 2") making it possible to approximate sin(r)
     * with r, cos(r) with 1 and sin(r)*sin(r) with 0, which is very helpful to
     * compute the transformation parameters from two data sets using a mean
     * square method.</p> <p>This is the the most widely used method to
     * transform coordinates between two different datums, specially in Europe,
     * and it is recommended by the International Association of Geodesy and ISO
     * 19111).</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds, point vector
     * convention)
     * @param ry_sec rotation parameter around y axis (seconds, point vector
     * convention)
     * @param rz_sec rotation parameter around z axis (seconds, point vector
     * convention)
     * @param ds_ppm scale factor in ppm (parts per million)
     */
    public static SevenParameterTransformation createBursaWolfTransformation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec, double ds_ppm) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, POSITION_VECTOR, LINEARIZED, 0.5);
    }

    public static SevenParameterTransformation createBursaWolfTransformation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec, double ds_ppm,
            final SevenParameterTransformation inverse) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, POSITION_VECTOR, LINEARIZED, 0.5) {
            @Override
            public SevenParameterTransformation inverse() {
                return inverse;
            }
        };
    }

    /**
     * <p>Create a Bursa-Wolf transformation with a specific precision.</p>
     * <p>Bursa-Wolf transformation (or simplified seven parameters
     * transformation or linearized Helmert transformation) is a 3D similarity
     * with very small rotations (< 2") making it possible to approximate sin(r)
     * with r, cos(r) with 1 and sin(r)*sin(r) with 0, which is very helpful to
     * compute the transformation parameters from two data sets using a mean
     * square method.</p> <p>This is the the most widely used method to
     * transform coordinates between two different datums, specially in Europe,
     * and it is recommended by the International Association of Geodesy and ISO
     * 19111).</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds, point vector
     * convention)
     * @param ry_sec rotation parameter around y axis (seconds, point vector
     * convention)
     * @param rz_sec rotation parameter around z axis (seconds, point vector
     * convention)
     * @param ds_ppm scale factor in ppm (parts per million)
     * @param precision mean precision of the geodetic transformation
     */
    public static SevenParameterTransformation createBursaWolfTransformation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double ds_ppm, double precision) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, POSITION_VECTOR, LINEARIZED, precision);
    }

    /**
     * <p>Create a CoordinateFrame rotation with the default precision.</p>
     * <p>Coordinate frame rotation (sometimes called Helmert transformation or
     * similarity) is the same transformation as Bursa-Wolf except it uses the
     * opposite rotation angle convention. It is used in USA and recommended by
     * NATO.</p> <p>Formulas are linearized as in BursaWolf transformation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds, point vector
     * convention)
     * @param ry_sec rotation parameter around y axis (seconds, point vector
     * convention)
     * @param rz_sec rotation parameter around z axis (seconds, point vector
     * convention)
     * @param ds_ppm scale factor in ppm (parts per million)
     */
    public static SevenParameterTransformation createCoordinateFrameRotation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec, double ds_ppm) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, COORDINATE_FRAME, LINEARIZED, 0.5);
    }

    /**
     * <p>Create a Coordinate Frame rotation with a specific precision.</p>
     * <p>Coordinate frame rotation (sometimes called Helmert transformation or
     * similarity) is the same transformation as Bursa-Wolf except it uses the
     * opposite rotation angle convention. It is used in USA and recommended by
     * NATO.</p> <p>Formulas are linearized as in BursaWolf transformation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds, point vector
     * convention)
     * @param ry_sec rotation parameter around y axis (seconds, point vector
     * convention)
     * @param rz_sec rotation parameter around z axis (seconds, point vector
     * convention)
     * @param ds_ppm scale factor in ppm (parts per million)
     * @param precision mean precision of the geodetic transformation
     */
    public static SevenParameterTransformation createCoordinateFrameRotation(
            double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double ds_ppm, double precision) {
        return new SevenParameterTransformation(
                tx, ty, tz,
                rx_sec * Math.PI / 180.0 / 3600.0,
                ry_sec * Math.PI / 180.0 / 3600.0,
                rz_sec * Math.PI / 180.0 / 3600.0,
                1.0 + ds_ppm / 1000000.0, COORDINATE_FRAME, LINEARIZED, precision);
    }

    /**
     * Transform coord values
     *
     * @param coord the coordinate to transform
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length < 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        double rotationSign = (rotationConvention == POSITION_VECTOR) ? 1.0 : -1.0;
        double x = coord[0];
        double y = coord[1];
        double z = coord[2];
        double srx = rx * rotationSign;
        double sry = ry * rotationSign;
        double srz = rz * rotationSign;
        srx = linearized ? srx : sin(srx);
        sry = linearized ? sry : sin(sry);
        srz = linearized ? srz : sin(srz);
        coord[0] = tx + scale * (x + z * sry - y * srz);
        coord[1] = ty + scale * (y + x * srz - z * srx);
        coord[2] = tz + scale * (z + y * srx - x * sry);
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public SevenParameterTransformation inverse() {
        // If inverse has already been calculated, return cached object.
        if (inverse != null) return inverse;
        return inverse = inverseStandard();
    }

    // We can inverse a seven-parameter transformation by negating the sign of all parameters
    // but we don't get an exact inverse (about 2 cm with a 10" rotation and 1 m with a 100" rotation)
    // With the above inverse formula, the transformation is stable, even after several back and forth
    public SevenParameterTransformation inverseStandard() {
        return new SevenParameterTransformation(tx, ty, tz, rx, ry, rz, scale,
                rotationConvention, linearized, precision) {
            @Override
            public double[] transform(double[] coord) throws IllegalCoordinateException {
                if (coord.length < 3) {
                    throw new CoordinateDimensionException(coord, 3);
                }
                double rotationSign = (rotationConvention == POSITION_VECTOR) ? 1.0 : -1.0;
                double x = coord[0] - tx;
                double y = coord[1] - ty;
                double z = coord[2] - tz;
                double srx = rx * rotationSign;
                double sry = ry * rotationSign;
                double srz = rz * rotationSign;
                srx = linearized ? -srx : -sin(srx);
                sry = linearized ? -sry : -sin(sry);
                srz = linearized ? -srz : -sin(srz);
                coord[0] = (1.0 / scale) * (x * (1 + srx * srx) + z * (sry + srx * srz) - y * (srz - srx * sry)) / (1 + srx * srx + sry * sry + srz * srz);
                coord[1] = (1.0 / scale) * (y * (1 + sry * sry) + x * (srz + srx * sry) - z * (srx - sry * srz)) / (1 + srx * srx + sry * sry + srz * srz);
                coord[2] = (1.0 / scale) * (z * (1 + srz * srz) + y * (srx + sry * srz) - x * (sry - srx * srz)) / (1 + srx * srx + sry * sry + srz * srz);
                return coord;
            }

            @Override
            public SevenParameterTransformation inverse() {
                return SevenParameterTransformation.this;
            }

            @Override
            public double getPrecision() {
                if (linearized == NOT_LINEARIZED) return precision;
                // If the transformation is linearized, inverseTransformation return a lesser precision
                // so that direct transformation may always have priority over inverse transformation.
                else if (Math.abs(rx) + Math.abs(ry) + Math.abs(rz) < 0.0001) return precision*0.9;
                else if (Math.abs(rx) + Math.abs(ry) + Math.abs(rz) < 0.001) return precision*0.5;
                else return precision*0.1;
            }
        };
    }

    // IGN ALG0063
    // @TODO from my tests, results are not as stable as with inverseStandard
    public SevenParameterTransformation inverse0063() {

        return new SevenParameterTransformation(tx, ty, tz, rx, ry, rz, scale,
                rotationConvention, linearized, precision) {
            @Override
            public double[] transform(double[] coord) throws IllegalCoordinateException {
                if (coord.length < 3) {
                    throw new CoordinateDimensionException(coord, 3);
                }
                double rotationSign = (rotationConvention == POSITION_VECTOR) ? 1.0 : -1.0;
                double x = coord[0] - tx;
                double y = coord[1] - ty;
                double z = coord[2] - tz;
                double srx = rx * rotationSign;
                double sry = ry * rotationSign;
                double srz = rz * rotationSign;
                srx = linearized ? srx : sin(srx);
                sry = linearized ? sry : sin(sry);
                srz = linearized ? srz : sin(srz);
                double e = scale;
                double e2 = e*e;
                double det = e * (e2 + srx*srx + sry*sry + srz*srz);
                coord[0] = (x * (e2 + srx*srx) + z * (srx*srz - e*sry) + y * (e*srz + srx*sry)) / det;
                coord[1] = (y * (e2 + sry*sry) + x * (sry*srx - e*srz) + z * (e*srx + sry*srz)) / det;
                coord[2] = (z * (e2 + srz*srz) + y * (srz*sry - e*srx) + x * (e*sry + srz*srx)) / det;
                return coord;
            }

            @Override
            public SevenParameterTransformation inverse() {
                return SevenParameterTransformation.this;
            }

            @Override
            public double getPrecision() {
                if (linearized == NOT_LINEARIZED) return precision;
                    // If the transformation is linearized, inverseTransformation return a lesser precision
                    // so that direct transformation may always have priority over inverse transformation.
                else if (Math.abs(rx) + Math.abs(ry) + Math.abs(rz) < 0.0001) return precision*0.9;
                else if (Math.abs(rx) + Math.abs(ry) + Math.abs(rz) < 0.001) return precision*0.5;
                else return precision*0.1;
            }

        };
    }

    /**
     * Return this SevenParameterTransformation as a String.
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
     * Returns a WKT representation of the seven parameter transformation.
     *
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
        w.append(',');
        double rxn = rx * 3600 * 180 / Math.PI;
        if (Math.abs(rxn - Math.rint(rxn)) < 1e-9) {
            w.append((int) rxn);
        } else {
            w.append(rxn);
        }
        w.append(',');
        double ryn = ry * 3600 * 180 / Math.PI;
        if (Math.abs(ryn - Math.rint(ryn)) < 1e-9) {
            w.append((int) ryn);
        } else {
            w.append(ryn);
        }
        w.append(',');
        double rzn = rz * 3600 * 180 / Math.PI;
        if (Math.abs(rzn - Math.rint(rzn)) < 1e-9) {
            w.append((int) rzn);
        } else {
            w.append(rzn);
        }
        w.append(',');
        w.append(Math.rint((scale - 1) * 1e15) / 1e9);
        w.append("]");
        return w.toString();
    }

    /**
     * Returns true if o is equals to <code>this</code>.
     * SevenParametersTransformations are equals if they both are identity, or
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
            if (o instanceof SevenParameterTransformation) {
                SevenParameterTransformation transfo = (SevenParameterTransformation) o;
                return ((this.tx == transfo.tx) && (this.ty == transfo.ty) && (this.tz == transfo.tz)
                    && (this.rx == transfo.rx) && (this.ry == transfo.ry) && (this.rz == transfo.rz)
                    && (this.scale == transfo.scale) && (this.rotationConvention == transfo.rotationConvention)
                    && (this.linearized == transfo.linearized));
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
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.tx) ^ (Double.doubleToLongBits(this.tx) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.ty) ^ (Double.doubleToLongBits(this.ty) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.tz) ^ (Double.doubleToLongBits(this.tz) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.rx) ^ (Double.doubleToLongBits(this.rx) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.ry) ^ (Double.doubleToLongBits(this.ry) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.rz) ^ (Double.doubleToLongBits(this.rz) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.scale) ^ (Double.doubleToLongBits(this.scale) >>> 32));
        hash = 67 * hash + this.rotationConvention;
        hash = 67 * hash + (this.linearized ? 1 : 0);
        return hash;
    }

    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity() {
        return tx == 0.0 && ty == 0.0 && tz == 0.0 && rx == 0 && ry == 0 && rz == 0 && scale == 1;
    }
}
