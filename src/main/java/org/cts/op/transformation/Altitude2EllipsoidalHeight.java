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

import java.io.FileInputStream;
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
import org.cts.op.transformation.grids.GridUtils;
import org.cts.op.transformation.grids.IGNVerticalGrid;

/**
 * Altitude2EllipsoidalHeight is a coordinate operation used to transform 3D
 * coordinates containing the altitude in the third coordinate with a grid
 * transformation that return the equivalent coordinates with ellipsoidal height
 * instead of altitude.
 *
 * @author Jules Party
 */
public class Altitude2EllipsoidalHeight extends AbstractCoordinateOperation implements GridBasedTransformation {

    /**
     * The GeographicGrid that define this transformation.
     */
    private GeographicGrid GRID;

    /**
     * The name of the grid file used to define this transformation.
     */
    private String gridFileName;

    /**
     * The geodetic datum associated to this transformation. The latitude and
     * longitude of the coordinate must be expressed in this datum to obtain
     * good results.
     */
    private GeodeticDatum associatedDatum;

    /**
     * The Identifier used for all Altitude to Ellipsoidal Height translations.
     */
    private static final Identifier opId =
            new Identifier("EPSG", "9616", "Vertical Offset (by Interpolation of Gridded Data)", "Translation");

    // Inverse transformation
    private Altitude2EllipsoidalHeight inverse;

    /**
     * Altitude translation with parameter interpolated from a grid depending on
     * the geographic coordinates of the point to convert.
     *
     * @param nameGrid the name of the grid file to use
     * @param gd the geodetic datum in which the geographic coordinates used in
     * the interpolation must be expressed
     * @throws java.lang.Exception
     */
    public Altitude2EllipsoidalHeight(String nameGrid, GeodeticDatum gd) throws Exception {
        super(opId);
        this.associatedDatum = gd;
        this.precision = 0.01;
        this.gridFileName = nameGrid;
        try {
            InputStream is = GridUtils.class.getClassLoader().getResourceAsStream("org/cts/op/transformation/grids/" + nameGrid);
            if (is != null) {
                GRID = new IGNVerticalGrid(is, false);
            } else {
                GRID = new IGNVerticalGrid(new FileInputStream(GridUtils.findGrid(nameGrid)), false);
            }                
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\nThis problem occured when loading the " + nameGrid + " grid file.");
        }
    }

    /**
     * Return the geodetic datum associated to this transformation. The latitude
     * and longitude of the coordinate must be expressed in this datum to obtain
     * good results.
     * @return 
     */
    public GeodeticDatum getAssociatedDatum() {
        return associatedDatum;
    }

    /**
     * Return the name of the grid file used to define this transformation.
     */
    public String getGridFileName() {
        return gridFileName;
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
        double th;
        // Get the definitive translation parameters from the grids
        try {
            double[] t = GRID.bilinearInterpolation(coordi[0], coordi[1]);
            th = t[0];
        } catch (OutOfExtentException e) {
            throw new IllegalCoordinateException(e.getMessage());
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
        if (inverse != null) return inverse;
        try {
            inverse = new Altitude2EllipsoidalHeight(getGridFileName(), associatedDatum) {
                @Override
                public double[] transform(double[] coord)
                        throws IllegalCoordinateException {
                    // Creates a temp coord to find the final translation parameters
                    double[] coordi = coord.clone();
                    double th;
                    // Get the definitive translation parameters from the grids
                    try {
                        double[] t = GRID.bilinearInterpolation(coordi[0], coordi[1]);
                        th = t[0];
                    } catch (OutOfExtentException e) {
                        throw new IllegalCoordinateException(e.getMessage());
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
            return inverse;
        } catch (Exception e) {
            throw new NonInvertibleOperationException(e.getMessage());
        }
    }
}