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

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Erwan Bocher
 */
class EPSGTransformTest extends BaseCoordinateTransformTest {

    @Test
    void testFrenchEPSGCodeFrom4326To27582() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{2.114551393, 50.345609791};
        String csNameDest = "EPSG:27582";  //Target EPSG lambert 2 etendu france
        double[] pointDest = new double[]{584173.736, 2594514.828};
        double tolerance = 10E-3;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:27582 ", result, pointDest, tolerance));
    }

    @Test
    void testFrenchEPSGCodeFrom27582To4326() throws Exception {
        String csNameSrc = "EPSG:27582"; //Input EPSG
        double[] pointSource = new double[]{584173.736, 2594514.828};
        String csNameDest = "EPSG:4326";  //Target EPSG 
        double[] pointDest = new double[]{2.114551393, 50.345609791};
        double tolerance = 0.0001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:27582 to EPSG:4326", result, pointDest, tolerance));
    }
    
    
     @Test
     void testFrenchEPSGCodeFrom27572To3857() throws Exception {
        String csNameSrc = "EPSG:27572"; //Input EPSG
        double[] pointSource = new double[]{282331, 2273699.7};
        String csNameDest = "EPSG:3857";  //Target EPSG 
        double[] pointDest = new double[]{-208496.53743537163, 6005369.877027287};
        double tolerance = 0.0001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:27572 to EPSG:3857", result, pointDest, tolerance));
    }
     
    @Test
    void testFrenchEPSGCodeFrom4326To4299() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{-7.899170,52.831312};
        String csNameDest = "EPSG:4299";  //Target EPSG 
        double[] pointDest = new double[]{-7.89842402505289,52.8310168494995};
        double tolerance = 0.0000001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = true;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:4299", result, pointDest, tolerance));
    }
}
