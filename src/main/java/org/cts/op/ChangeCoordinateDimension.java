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
package org.cts.op;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

/**
 * Change de coordinate dimension. For example, this operation can add or remove
 * a vertical dimension to a 2D coordinate.
 *
 * @author Michaël Michaud
 */
public class ChangeCoordinateDimension extends AbstractCoordinateOperation {

    private static final Identifier G3DG2D =
            new Identifier("EPSG", "9659", "Geographic 3D to 2D conversion", "Geo3D->2D");
    private static final Identifier G2DG3D =
            new Identifier("EPSG", "9659i", "Geographic 2D to 3D conversion", "Geo2D->3D");

    public final static ChangeCoordinateDimension TO3D = new ChangeCoordinateDimension(G2DG3D, 2, 3) {
        @Override public CoordinateOperation inverse() {return TO2D;}
    };

    public final static ChangeCoordinateDimension TO2D = new ChangeCoordinateDimension(G3DG2D, 3, 2) {
        @Override public CoordinateOperation inverse() {return TO3D;}
    };

    private int inputDim, outputDim;

    /**
     * Creates a new CoordinateOperation increasing (resp decreasing) the coord
     * size by length.
     *
     * @param inputDim dimension of the input coordinate
     * @param outputDim dimension of the output coordinate
     */
    private ChangeCoordinateDimension(Identifier identifier, int inputDim, int outputDim) {
        super(identifier);
        this.inputDim = inputDim;
        this.outputDim = outputDim;
    }

    /**
     * Add a vertical coordinate.
     *
     * @param coord is an array containing one, two or three ordinates
     * @return 
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) {
        if (inputDim == outputDim) {
            return coord;
        }
        double[] cc = new double[outputDim];
        System.arraycopy(coord, 0, cc, 0, Math.min(inputDim, outputDim));
        return cc;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() {
        return new ChangeCoordinateDimension(new Identifier(CoordinateOperation.class), outputDim, inputDim);
    }
}
