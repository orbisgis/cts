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

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Erwan Bocher
 */
public class EPSGTransformTest extends BaseCoordinateTransformTest {

    @Test
    public void testFrenchEPSGCodeFrom4326To27582() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{2.114551393, 50.345609791};
        String csNameDest = "EPSG:27582";  //Target EPSG lambert 2 etendu france
        double[] pointDest = new double[]{584173.736, 2594514.828};
        double tolerance = 10E-3;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        System.out.println(inputCRS.toWKT());
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = true;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:27582 ", result, pointDest, tolerance));
    }

    @Test
    public void testFrenchEPSGCodeFrom27582To4326() throws Exception {
        String csNameSrc = "EPSG:27582"; //Input EPSG
        double[] pointSource = new double[]{584173.736, 2594514.828};
        String csNameDest = "EPSG:4326";  //Target EPSG 
        double[] pointDest = new double[]{2.114551393, 50.345609791};
        double tolerance = 0.0001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = true;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:27582 to EPSG:4326", result, pointDest, tolerance));
    }
}
