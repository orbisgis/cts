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
import org.junit.jupiter.api.Disabled;
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
    
    @Test
    void testFrenchEPSGCodeFrom4326To27700() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{-7.899170,52.831312};
        String csNameDest = "EPSG:27700";  //Target EPSG 
        double[] pointDest = new double[]{2838.77,342299.51};
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = true;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:27700", result, pointDest, tolerance));
    }

    @Test
    void testEPSGCodeFrom31256To4326() throws Exception {
        String csNameSrc = "EPSG:31256"; //Input EPSG
        double[] pointSource = new double[]{-38048.66 , 389405.66};
        String csNameDest = "EPSG:4326";  //Target EPSG
        double[] pointDest = new double[]{15.815805676616735 ,48.64153355841612};
        double tolerance = 0.0001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:31256 to EPSG:4326 ", result, pointDest, tolerance));
    }


    @Test
    void testEPSGCodeFrom4326To31467() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{9, 51};
        String csNameDest = "EPSG:31467";  //Target EPSG
        double[] pointDest = new double[]{3500073.57463, 5651645.88247};
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:31467 ", result, pointDest, tolerance));
    }


    @Test
    void testEPSGCodeFrom25832To31467() throws Exception {
        //See https://epsg.io/transform#s_srs=25832&t_srs=31467&x=604740.8835930&y=7992968.2448370
        String csNameSrc = "EPSG:25832"; //Input EPSG
        double[] pointSource = new double[]{604740.883593, 7992968.244837};
        String csNameDest = "EPSG:31467";  //Target EPSG
        double[] pointDest = new double[]{3604831.5221369416,7995714.14172839};
        double tolerance = 0.001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:25832 to EPSG:31467 ", result, pointDest, tolerance));
    }

    @Test
    void testEPSGCodeFrom2100To4326() throws Exception {
        String csNameSrc = "EPSG:2100"; //Input EPSG
        double[] pointSource = new double[]{475283.4855100708, 4207015.674958611};
        String csNameDest = "EPSG:4326";  //Target EPSG
        double[] pointDest = new double[]{23.7201375,38.0130833};
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:2100 to EPSG:4326 ", result, pointDest, tolerance));
    }


    //TODO : Fix me add support to NADGRID
    @Disabled
    @Test
    void testEPSGCodeFrom2249To4326() throws Exception {
        String csNameSrc = "EPSG:2249"; //Input EPSG
        double[] pointSource = new double[]{743238 , 2967416};
        String csNameDest = "EPSG:4326";  //Target EPSG
        double[] pointDest = new double[]{-71.1776848522251, 42.39028965129018};
        double tolerance = 0.0001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = false;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:2249 to EPSG:4326 ", result, pointDest, tolerance));
    }


    //TODO : Fix me add support to NADGRID
    @Disabled
    @Test
    void testFrenchEPSGCodeFrom4326To2232() throws Exception {
        String csNameSrc = "EPSG:4326"; //Input EPSG
        double[] pointSource = new double[]{-105,40};
        String csNameDest = "EPSG:2232";  //Target EPSG 
        double[] pointDest = new double[]{3140089.069 , 1789525.0783};
        double tolerance = 0.001;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS(csNameSrc);
        CoordinateReferenceSystem outputCRS = cRSFactory.getCRS(csNameDest);
        verbose = true;
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        assertTrue(checkEquals2D("EPSG:4326 to EPSG:2232", result, pointDest, tolerance));
    }
}
