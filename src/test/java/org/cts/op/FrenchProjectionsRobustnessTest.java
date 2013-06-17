/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This class is used to test the robustness of french projections The input
 * point and expected data have been produced with the Circe tool from the IGNF
 * institute
 *
 * @author ebocher
 */
public class FrenchProjectionsRobustnessTest extends BaseCoordinateTransformTest {

    @Test
    public void Lamb2toLamb93_2DProjection() throws Exception {
        //IGN data : POINT (311748.822 140616.022 0)	ID0430
        double[] srcPoint = new double[]{311748.822, 140616.022, 0};
        //IGN data : POINT (360908.932 6576578.199 0)	ID0430
        double[] expectedPoint = new double[]{360908.932, 6576578.199, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMB2");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void Lamb1toLamb93_2DProjection1() throws Exception {
        //IGN data : POINT (201418.662 137651.011 0)	ID0141
        double[] srcPoint = new double[]{201418.662, 137651.011, 0};
        //IGN data : POINT (252831.433 6873925.434 0)	ID0141
        double[] expectedPoint = new double[]{252831.433, 6873925.434, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMB1");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void Lamb1toLamb93_2DProjection2() throws Exception {
        //IGN data : POINT (87842.341 101687.92 449.99)	ID0003 
        double[] srcPoint = new double[]{87842.341, 101687.92, 0};
        //IGN data : POINT (139121.353 6838500.192 449.99)	ID0003
        double[] expectedPoint = new double[]{139121.353, 6838500.192, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMB1");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void CC44toLamb93_2DProjection() throws Exception {
        //IGN data : POINT (1977142.451 3095503.314 0)	ID5863
        double[] srcPoint = new double[]{1977142.451, 3095503.314, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{977362.95, 6218045.569, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:RGF93CC44");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void LonLat_RGF93_circetoLamb93_2DProjection() throws Exception {
        //IGN data : POINT (6.4 43.008 2525.68)	ID5863
        double[] srcPoint = new double[]{6.4, 43.008, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{977362.95, 6218045.569, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:RGF93G");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void Lamb2e_circetoLamb93_2DProjection1() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68)
        double[] srcPoint = new double[]{931813.94, 1786923.891, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{977362.95, 6218045.569, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMBE");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
    }

    @Test
    public void Lamb2e_circetoLamb93_2DProjection2() throws Exception {
        //IGN data : POINT (87674.404 2400935.485 449.99)	ID0003
        double[] srcPoint = new double[]{87674.404, 2400935.485, 0};
        //IGN data : POINT (139121.353 6838500.192 449.99)	ID0003
        double[] expectedPoint = new double[]{139121.353, 6838500.192, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMBE");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
    }

    @Test
    public void Lamb4toLonLat_RGF93_circe_2DProjection() throws Exception {
        //IGN data : POINT (576164.366 303143.285 540.8)	ID6429 
        double[] srcPoint = new double[]{576164.366, 303143.285, 0};
        //IGN data : POINT (9.408 43.006 540.8)	ID6429
        double[] expectedPoint = new double[]{9.408, 43.006, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:LAMB4");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:RGF93G");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void LonLat_RGF93_circetoLamb93_3DProjection() throws Exception {
        //IGN data : POINT (6.4 43.008 2525.68)	ID5863
        double[] srcPoint = new double[]{6.4, 43.008, 2525.68};
        //IGN data : POINT (977362.95 6218045.569 2525.68)	ID5863
        double[] expectedPoint = new double[]{977362.95, 6218045.569, 2525.68};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:RGF93G");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    @Test
    public void CC42toLamb93_3DProjection() throws Exception {
        //IGN data : POINT (1708465.025 1256428.742 2423.84)	ID3673
        double[] srcPoint = new double[]{1708465.025, 1256428.742, 0};
        //IGN data : POINT (708477.597 6156470.549 2423.84)	ID3673
        double[] expectedPoint = new double[]{708477.597, 6156470.549, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:RGF93CC42");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:LAMB93");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    /**
     * Guadeloupe projection test - Fort Marigot
     *
     * @throws Exception
     */
    @Test
    public void FortMarigottoRRAF_UTM20_2DProjection() throws Exception {
        //IGN data : POINT (484950.069 1997266.647)	ID1384 -> Fort Marigot
        double[] srcPoint = new double[]{484950.069, 1997266.647, 0};
        //IGN data : POINT (485184.241 1996829.341)	ID1384 -> RRAF UTM20
        double[] expectedPoint = new double[]{485184.241, 1996829.341, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:GUADFM49U20");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:UTM20W84GUAD");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    /**
     * Guadeloupe projection test - Saint Anne
     *
     * @throws Exception
     */
    @Test
    public void SaintAnnetoRRAF_UTM20_2DProjection() throws Exception {
        //IGN data : POINT (704500.635 1803378.302)	ID0365 -> Saint Anne
        double[] srcPoint = new double[]{704500.635, 1803378.302, 0};
        //IGN data : POINT (704078.258 1803075.127)	ID0365 -> RRAF UTM20
        double[] expectedPoint = new double[]{704078.258, 1803075.127, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:GUAD48UTM20");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:UTM20W84GUAD");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }

    /**
     * Guyanne projection test
     *
     * @throws Exception
     */
    //@Test this projection is badly supported or maybe or pb with CRS identifiants.
    public void UTM22RGFG95toCSG67UTM22_2DProjection() throws Exception {
        //IGN data : POINT (170451.067 633659.662 4331.42)	ID0001
        double[] srcPoint = new double[]{170451.067, 633659.662, 0};
        //IGN data : POINT (170453.13 633546.624 4331.42)	ID0001
        double[] expectedPoint = new double[]{170453.13, 633546.624, 0};
        CoordinateReferenceSystem srcCRS = createCRS("IGNF:UTM22RGFG95");
        CoordinateReferenceSystem outCRS = createCRS("IGNF:CSG67UTM22");
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals(srcCRS + " to " + outCRS, result, expectedPoint, 10E-3));
    }
}
