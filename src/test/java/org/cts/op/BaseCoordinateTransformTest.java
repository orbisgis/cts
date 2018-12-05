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

import org.cts.CTSTestCase;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CompoundCRS;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.datum.GeodeticDatum;
import org.cts.op.transformation.FrenchGeocentricNTF2RGF;
import org.cts.op.transformation.GridBasedTransformation;
import org.cts.op.transformation.NTv2GridShiftTransformation;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Erwan Bocher
 */
public class BaseCoordinateTransformTest extends CTSTestCase {

    // Standard precision in meters and in degrees
    protected static final double MM = 0.001;
    protected static final double MM_IN_DEG = 0.00000001;

    protected boolean verbose = true;

    /**
     * Transform a point from a CRS to another CRS
     *
     * @param sourceCRS
     * @param targetCRS
     * @param inputPoint
     * @return
     * @throws IllegalCoordinateException
     * @throws org.cts.op.CoordinateOperationException
     */
    public double[] transform(GeodeticCRS sourceCRS, GeodeticCRS targetCRS, double[] inputPoint)
            throws IllegalCoordinateException, CoordinateOperationException {
        Set<CoordinateOperation> ops;
        int tot, subtot;
        ops = CoordinateOperationFactory.createCoordinateOperations(sourceCRS, targetCRS);
        tot = ops.size();
        if (sourceCRS.getDatum() == GeodeticDatum.WGS84 || targetCRS.getDatum() == GeodeticDatum.WGS84) {
            ops = CoordinateOperationFactory.excludeFilter(ops, FrenchGeocentricNTF2RGF.class);
            ops = CoordinateOperationFactory.excludeFilter(ops, NTv2GridShiftTransformation.class);
        }
        // If source CRS comes from the EPSG registry and is not a CompoundCRS,
        // we use BursaWolf or translation rather than GridBasedTransformation,
        // even if a GridBasef Transformation is available (precise transformation
        // may be available because we also read IGNF registry and precise
        // transformations have been stored in GeodeticDatum objects.
        else if (sourceCRS.getIdentifier().getAuthorityName().equals("EPSG") &&
                !(sourceCRS instanceof CompoundCRS) && !(targetCRS instanceof CompoundCRS)) {
            ops = CoordinateOperationFactory.excludeFilter(ops, GridBasedTransformation.class);
        }
        subtot = ops.size();
        if (!ops.isEmpty()) {
            CoordinateOperation op = CoordinateOperationFactory.getMostPrecise(ops);
            if (verbose) {
                System.out.println("Source " + sourceCRS);
                System.out.println("Target " + targetCRS);
                System.out.println(tot + " transformations found, " + subtot + " retained");
                System.out.println("Used transformation (" + op.getPrecision() + ") : " + op);

                if (ops.size() > 1) {
                    for (CoordinateOperation oop : ops) {
                        //System.out.println("   a transformation with precision (" + oop.getPrecision() + ") : " + oop);
                        System.out.println("   other transformation : precision = " + oop.getPrecision());
                    }
                }
            }
            double[] input = new double[inputPoint.length];
            System.arraycopy(inputPoint, 0, input, 0, inputPoint.length);
            return op.transform(input);
        } else {
            System.out.println("No transformation found from " + sourceCRS + " to " + targetCRS);
            return new double[]{0.0d, 0.0d, 0.0d};
        }
    }

    /**
     * Display the CRS in a WKT representation
     *
     * @param crs
     */
    public void printCRStoWKT(CoordinateReferenceSystem crs) {
        System.out.println(crs.toWKT());
    }

    protected void test(double[] source, GeodeticCRS sourceCRS,
                      GeodeticCRS targetCRS, double[] ref,
                      double tolSource, double tolTarget) throws IllegalCoordinateException, CoordinateOperationException {
        double[] dir_tf = transform(sourceCRS, targetCRS, source);
        if (verbose) System.out.println("found (direct): " + Arrays.toString(dir_tf) + " (ref=" + Arrays.toString(ref) + ")");
        assertTrue(checkEquals2D("dir--> " + sourceCRS + " to " + targetCRS, dir_tf, ref, tolTarget));

        double[] inv_tf = transform(targetCRS, sourceCRS, dir_tf.clone());
        if (verbose) System.out.println("found (inverse): " + Arrays.toString(inv_tf) + " (ref=" + Arrays.toString(source) + ")");
        assertTrue(checkEquals2D("inv--> " + sourceCRS + " to " + targetCRS, inv_tf, source, tolSource));
    }

    protected void test3D(double[] source, GeodeticCRS sourceCRS,
                        GeodeticCRS targetCRS, double[] ref,
                        double tolSource, double tolTarget) throws IllegalCoordinateException, CoordinateOperationException {
        double[] dir_tf = transform(sourceCRS, targetCRS, source);
        if (verbose) System.out.println("found (direct): " + Arrays.toString(dir_tf) + " (ref=" + Arrays.toString(ref) + ")");
        assertTrue(checkEquals3D("dir--> " + sourceCRS + " to " + targetCRS, dir_tf, ref, tolTarget, MM));

        double[] inv_tf = transform(targetCRS, sourceCRS, dir_tf.clone());
        if (verbose) System.out.println("found (inverse): " + Arrays.toString(inv_tf) + " (ref=" + Arrays.toString(source) + ")");
        assertTrue(checkEquals3D("inv--> " + sourceCRS + " to " + targetCRS, inv_tf, source, tolSource, MM));
    }
}
