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

import java.io.FileReader;
import java.io.LineNumberReader;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class is used to test several transformations.
 *
 * @author Erwan Bocher
 */
public class BatchCoordinateTransformTest extends BaseCoordinateTransformTest {

    /**
     * Test if a transformation works from a CRS to another CRS. A file is used
     * to specified the input and output CRS codes, starting point and excepted
     * result according a tolerance.
     *
     * @throws Exception
     */
    @Test
    public void testCoordinateTransformFromFile() throws Exception {
        String filePath = BatchCoordinateTransformTest.class.getResource("crstransform.csv").toURI().getPath();
        FileReader reader = new FileReader(filePath);
        LineNumberReader lineReader = new LineNumberReader(reader);

        //Do not read the first line because of header
        lineReader.readLine();
        while (true) {
            String line = lineReader.readLine();
            if (line == null) {
                break;
            } else if (line.startsWith("#")) {
                continue;
            }
            String[] values = line.split(";");
            String id = values[0];
            String csNameSrc = values[1];
            double csNameSrc_X = parseNumber(values[2]);
            double csNameSrc_Y = parseNumber(values[3]);
            String csNameDest = values[4];
            double csNameDest_X = parseNumber(values[5]);
            double csNameDest_Y = parseNumber(values[6]);
            double tolerance = parseNumber(values[7]);
            CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
            CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
            double[] pointSource = new double[]{csNameSrc_X, csNameSrc_Y};
            double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
            double[] pointDest = new double[]{csNameDest_X, csNameDest_Y};
            double[] check = transform((GeodeticCRS) outputCRS, (GeodeticCRS) inputCRS, pointDest);
            assertTrue(checkEquals2D(id + " dir--> " + csNameSrc + " to " + csNameDest, result, pointDest, tolerance), "Error for direct transformation line " + line + "\n");
            assertTrue(checkEquals2D(id + " inv--> " + csNameDest + " to " + csNameSrc, check, pointSource, 0.01), "Error for inverse transformation line " + line + "\n");
        }
        lineReader.close();
    }

    /**
     * Parses a number from a String. If the string is empty returns
     * {@link java.lang.Double#NaN}.
     *
     * @param numStr
     * @return the number as a double
     * @return Double.NaN if the string is null or empty
     * @throws NumberFormatException if the number is ill-formed
     */
    private static double parseNumber(String numStr) {
        if (numStr == null || numStr.length() == 0) {
            return Double.NaN;
        }
        return Double.parseDouble(numStr);
    }

    /**
     * Transform a point from a CRS to another CRS
     *
     * @param sourceCRS
     * @param targetCRS
     * @param inputPoint
     * @return
     * @throws org.cts.IllegalCoordinateException
     */
    //public double[] transform(GeodeticCRS sourceCRS, GeodeticCRS targetCRS, double[] inputPoint)
    //        throws IllegalCoordinateException, CoordinateOperationException {
    //    List<CoordinateOperation> ops;
    //    ops = CoordinateOperationFactory.createCoordinateOperations(sourceCRS, targetCRS);
    //    if (sourceCRS.getIdentifier().getAuthorityName().equals("EPSG")) {
    //        ops = CoordinateOperationFactory.excludeFilter(ops, GridBasedTransformation.class);
    //    }
    //    //if () ops = CoordinateOperationFactory.excludeFilter(ops, GridBasedTransformation.class);
    //    if (!ops.isEmpty()) {
    //        if (verbose) {
    //            System.out.println("Source " + sourceCRS);
    //            System.out.println("Target " + targetCRS);
    //            System.out.println("Used transformation " + CoordinateOperationFactory.getMostPrecise(ops));
    //            if (ops.size() > 1) {
    //                for (CoordinateOperation ope : ops) {
    //                    System.out.println("All transformations : " + ope);
    //                }
    //            }
    //        }
    //        //return ops.get(0).transform(new double[]{inputPoint[0], inputPoint[1], inputPoint[2]});
    //        double[] input = new double[inputPoint.length];
    //        for (int i = 0 ; i < inputPoint.length ; i++) input[i] = inputPoint[i];
    //        return CoordinateOperationFactory.getMostPrecise(ops).transform(input);
    //        //System.out.println("Choix de la transfo : " + CoordinateOperationFactory.getTypedCoordinateOperation(ops, GeoTransformation.class));
    //        //return CoordinateOperationFactory.getTypedCoordinateOperation(ops, GeoTransformation.class).transform(new double[]{inputPoint[0], inputPoint[1], inputPoint[2]});
    //    } else {
    //        return new double[]{0.0d, 0.0d, 0.0d};
    //    }
    //}
}
