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
package org.cts.op;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;

import org.cts.datum.Datum;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.transformation.FrenchGeocentricNTF2RGF;
import org.cts.op.transformation.NTv2GridShiftTransformation;
import org.junit.Test;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * This class is used to test french grids transformations.
 *
 * @author Jules Party
 */
public class FrenchGridsTest extends BaseCoordinateTransformTest {

    /**
     * Test if a transformation works from a CRS to another CRS. A file is used
     * to specified the input and output CRS codes, starting point and excepted
     * result according a tolerance. The file use test transformation using a
     * french geographic grids. The value for the tests has been found <a
     * href=http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NT111_V1_HARMEL_TransfoNTF-RGF93_FormatGrilleNTV2.pdf>here</a>.
     *
     * @throws Exception
     */
    @Test
    public void testCoordinateTransformFromFile() throws Exception {
        String filePath = BatchCoordinateTransformTest.class.getResource("frenchgridstest.csv").toURI().getPath();
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
            System.out.println(line);
            String[] values = line.split(";");
            String id = values[0];
            String csNameSrc = values[1];
            double csNameSrc_X = parseNumber(values[2]);
            double csNameSrc_Y = parseNumber(values[3]);
            String csNameDest = values[4];
            double csNameDest_X_Blegg = parseNumber(values[5]);
            double csNameDest_Y_Blegg = parseNumber(values[6]);
            double tolerance = parseNumber(values[7]);
            int index = ((int) parseNumber(id)) % 2;
            //@TODO Fix NTv2 transformation for RGF93 (index = 1)
            CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
            CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
            double[] pointSource = new double[]{csNameSrc_X, csNameSrc_Y};
            System.out.println("src: " + Arrays.toString(pointSource));
            double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource, index);
            double[] pointDest = new double[]{csNameDest_X_Blegg, csNameDest_Y_Blegg};
            System.out.println("tgt: " + Arrays.toString(pointDest));
            double[] check = transform((GeodeticCRS) outputCRS, (GeodeticCRS) inputCRS, pointDest, index);
            System.out.println("cal: " + Arrays.toString(result));
            System.out.println("chk: " + Arrays.toString(check));
            //System.out.println("" + index + " " + CoordinateOperationFactory.createCoordinateOperations((GeodeticCRS)inputCRS, (GeodeticCRS)outputCRS));
            assertTrue(checkEquals2D(id + " dir--> " + csNameSrc + " to " + csNameDest, result, pointDest, tolerance));
            assertTrue(checkEquals2D(id + " inv--> " + csNameDest + " to " + csNameSrc, check, pointSource, 1E-3));
        }
        lineReader.close();
    }

    /**
     * for index = 0, choose the second (NTv2-based) transform returned by createCoordinateOperations
     * for index = 1, choose the first (blegg-based) transform (=circe) returned by createCoordinateOperations
     */
    public double[] transform(GeodeticCRS sourceCRS, GeodeticCRS targetCRS, double[] inputPoint, int index) throws Exception {

        Set<CoordinateOperation> ops = CoordinateOperationFactory.createCoordinateOperations(sourceCRS, targetCRS);
        //System.out.println(ops);
        CoordinateOperation gr3d = CoordinateOperationFactory.getMostPrecise(
                CoordinateOperationFactory.includeFilter(ops, FrenchGeocentricNTF2RGF.class));
        CoordinateOperation ntv2 = CoordinateOperationFactory.getMostPrecise(
                CoordinateOperationFactory.includeFilter(ops, NTv2GridShiftTransformation.class));

        CoordinateOperation op = index == 1 ? ntv2 : gr3d;

        //if (sourceCRS.getDatum().getEllipsoid().equals(Ellipsoid.GRS80)) {
        //    op = op.inverse();
        //}

        System.out.println(op);
        return op.transform(new double[]{inputPoint[0], inputPoint[1]});
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
}
