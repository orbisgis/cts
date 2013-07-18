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

import java.io.IOException;
import java.io.InputStream;
import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.cs.OutOfExtentException;
import org.cts.datum.GeodeticDatum;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.op.CoordinateOperation;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.transformation.grids.GeographicGrid;
import org.cts.op.transformation.grids.IGNReunionGrid;
import org.cts.op.transformation.grids.IGNVerticalGrid;

/**
 * 
 *
 * @author Jules Party
 */
public class Altitude2EllipsoidalHeight extends AbstractCoordinateOperation {

    private GeographicGrid GRID;
    private GeodeticDatum associatedDatum;

    /**
     * Altitude translation with parameter interpolated from a grid depending on
     * the geographic coordinates of the point to convert.
     *
     * @param id the identifier of the Altitude2EllipsoidalHeight
     * @param nameGrid the name of the grid file to use
     * @param gd the geodetic datum in which the geographic coordinates used in
     * the interpolation must be expressed
     */
    public Altitude2EllipsoidalHeight(Identifier id, String nameGrid, GeodeticDatum gd) {
        super(id);
        this.associatedDatum = gd;
        this.precision = 0.01;
        try {
            InputStream is = IGNVerticalGrid.class.getClassLoader().getResourceAsStream("org/cts/op/transformation/grids/" + nameGrid);
            if (nameGrid.equals("RAR07_bl.txt")) {
                GRID = new IGNReunionGrid(is, false);
            } else {
                GRID = new IGNVerticalGrid(is, false);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Return the geodetic datum associated to this transformation.
     */
    public GeodeticDatum getAssociatedDatum() {
        return associatedDatum;
    }

    /**
     * @see AbstractCoordinateOperation#transform(double[])
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length < 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        // Creates a temporary coord to find the final translation parameters
        double[] coordi = coord.clone();
        double th = 0;
        // Get the definitive translation parameters from the grids
        try {
            double[] t = GRID.bilinearInterpolation(coordi[0], coordi[1]);
            th = t[0];
        } catch (OutOfExtentException e) {
            e.printStackTrace();
        }
        // Apply definitive translation
        coord[2] = th + coord[2];
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return new Altitude2EllipsoidalHeight(getIdentifier(), getName(), associatedDatum) {
            @Override
            public double[] transform(double[] coord)
                    throws IllegalCoordinateException {
                // Creates a temp coord to find the final translation parameters
                double[] coordi = coord.clone();
                double th = 0;
                // Get the definitive translation parameters from the grids
                try {
                    double[] t = GRID.bilinearInterpolation(coordi[0], coordi[1]);
                    th = t[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Apply definitive translation
                coord[2] = -th + coord[2];
                return coord;
            }

            @Override
            public CoordinateOperation inverse()
                    throws NonInvertibleOperationException {
                return Altitude2EllipsoidalHeight.this;
            }
        };
    }
}