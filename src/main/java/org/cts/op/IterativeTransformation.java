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
 * The IterativeTransformation is used to repeat a
 * {@link org.cts.op.CoordinateOperation} until one or more coordinate(s)
 * converge to predifined values (saved in additional dimensions using
 * {@link org.cts.op.MemorizeCoordinate}). This transformation should be used
 * when an iterative process is recommended (for instance when using a grid
 * using longitude and latitude calculated by a first approximate
 * transformation.
 *
 * @author Jules Party
 */
public class IterativeTransformation extends AbstractCoordinateOperation implements CoordinateOperation {

    CoordinateOperation op;
    int[] realValueIndex;
    int[] calculatedValueIndex;
    double[] tolerance;
    int maxIterations = 12;

    /**
     * Build a new IterativeTransformation.
     *
     * @param op the transformation to iterate
     * @param realValueIndex a list of indexes referring to the reference values
     * the iteration should reach
     * @param calculatedValueIndex the list of indexes referring to the calculated
     * values that must reach the reference values defined above
     * @param tol the maximal difference accepted between the real value and the
     * target value
     * @throws Exception when the arrays in parameter does not have the same
     * length
     */
    public IterativeTransformation(CoordinateOperation op, int[] realValueIndex, int[] calculatedValueIndex,
                                   double[] tol, int maxIterations) throws Exception {
        super(new Identifier(IterativeTransformation.class));
        this.op = op;
        if (calculatedValueIndex.length != realValueIndex.length) {
            throw new Exception("The two arrays in argument must have the same length.");
        }
        this.calculatedValueIndex = calculatedValueIndex;
        this.realValueIndex = realValueIndex;
        this.tolerance = tol;
        this.maxIterations = maxIterations;
    }

    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException, CoordinateOperationException {
        boolean iter = false;
        int count = 0;
        for (int i = 0; i < realValueIndex.length; i++) {
            iter = iter || Math.abs(coord[realValueIndex[i]] - coord[calculatedValueIndex[i]]) > tolerance[i];
        }
        while (iter) {
            coord = op.transform(coord);
            iter = false;
            for (int i = 0; i < realValueIndex.length; i++) {
                iter = iter || Math.abs(coord[realValueIndex[i]] - coord[calculatedValueIndex[i]]) > tolerance[i];
            }
            if (++count > maxIterations ) throw new TooManyIterationsException(this, count);
        }
        return coord;
    }

    @Override
    public double getPrecision() {
        // Precision of this iterative operation is difficult to guess
        // because we don't know it uses radians, degrees or meters
        // We just suppose it is less than the base transformation
        return op.getPrecision()/2.0;
    }

    public String toString() {
        return "Iterative transformation based on [\n" + op.toString().replaceAll("\n","\n\t") + "\n]";
    }
}