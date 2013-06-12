/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
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

import static java.lang.Math.PI;
import org.cts.CoordinateDimensionException;
import org.cts.CoordinateOperation;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.NonInvertibleOperationException;

/**
 * Bursa-Wolf algorithm is the most widely used 7-parameter transformation to
 * transform coordinates from one geodetic datum to another.<p> It is a
 * simplified form of the Position Vector 7-parameter transformation(1) based on
 * linearised formulas. In Bursa-Wolf formulas, rotation angles are supposed to
 * be small enough to replace sin(rot) by rot, cos(rot) by 1 and sin(rot)^2 by
 * 0.<p> (1) Position Vector convention is the one recommended by International
 * Association of Geodesy. A special attention is recommended, as some
 * organisation may use the Bursa-Wolf method (linearization) to the Coordinate
 * Frame convention.
 *
 * @see Helmert7ParameterTransformation
 * @see PositionVector7ParameterTransformation
 * @see CoordinateFrame7ParameterTransformation
 *
 * @author Michaël Michaud
 */
public class BursaWolfTransformation extends Helmert7ParameterTransformation {

    private final static Identifier id =
            new Identifier("EPSG", "9606",
            "Bursa-Wolf (linearized Position Vector 7-param transformation)",
            "Bursa-Wolf");

    /**
     * Transform coord values
     *
     * @param coord the coordinate to transform
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length != 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        double x = coord[0];
        double y = coord[1];
        double z = coord[2];
        double srx = rx;
        double sry = ry;
        double srz = rz;
        coord[0] = tx + scale * (x + z * sry - y * srz);
        coord[1] = ty + scale * (y + x * srz - z * srx);
        coord[2] = tz + scale * (z + y * srx - x * sry);
        return coord;
    }
    private BursaWolfTransformation inverse;

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        if (Math.abs(rx) > 0.0001 || Math.abs(rx) > 0.0001 || Math.abs(rx) > 0.0001) {
            throw new NonInvertibleOperationException("Bursa-Wolf "
                    + "transformations should not be inverted for rotations "
                    + "exceeding 20\"");
        } else if (inverse == null) {
            inverse = new BursaWolfTransformation(tx, ty, tz,
                    rx * 3600.0 * 180.0 / PI, ry * 3600.0 * 180.0 / PI, rz * 3600.0 * 180.0 / PI,
                    (scale - 1.0) * 1000000.0, precision) {

                @Override
                public double[] transform(double[] coord)
                        throws IllegalCoordinateException {
                    if (coord.length != 3) {
                        throw new CoordinateDimensionException(coord, 3);
                    }
                    double x = coord[0] - tx;
                    double y = coord[1] - ty;
                    double z = coord[2] - tz;
                    double srx = -rx;
                    double sry = -ry;
                    double srz = -rz;
                    coord[0] = (1.0 / scale) * (x + z * sry - y * srz);
                    coord[1] = (1.0 / scale) * (y + x * srz - z * srx);
                    coord[2] = (1.0 / scale) * (z + y * srx - x * sry);
                    return coord;
                }

                @Override
                public CoordinateOperation inverse()
                        throws NonInvertibleOperationException {
                    return BursaWolfTransformation.this;
                }
            };
        }
        return inverse;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////                                 CONSTRUCTORS                               ////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <p>7-parameter transformation for geodesy calculation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds)
     * @param ry_sec rotation parameter around y axis (seconds)
     * @param rz_sec rotation parameter around z axis (seconds)
     * @param scale scale factor (parts per million)
     * @param precision mean precision of the geodetic transformation
     */
    public BursaWolfTransformation(double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double scale, double precision) {
        super(id, tx, ty, tz, rx_sec, ry_sec, rz_sec, scale, precision);
    }

    /**
     * <p>7-parameter transformation for geodesy calculation.</p>
     *
     * @param tx translation parameter along x axis (meters)
     * @param ty translation parameter along y axis (meters)
     * @param tz translation parameter along z axis (meters)
     * @param rx_sec rotation parameter around x axis (seconds)
     * @param ry_sec rotation parameter around y axis (seconds)
     * @param rz_sec rotation parameter around z axis (seconds)
     * @param scale scale factor (parts per million)
     */
    public BursaWolfTransformation(double tx, double ty, double tz,
            double rx_sec, double ry_sec, double rz_sec,
            double scale) {
        super(id, tx, ty, tz, rx_sec, ry_sec, rz_sec, scale, 1E-9);
    }
}
