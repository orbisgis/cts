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

import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.*;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.crs.*;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.VerticalDatum;
import org.junit.jupiter.api.Test;

/**
 * This class tests transformation using CoumpoundCRS and VerticalCRS.
 *
 * @author Jules Party
 */
class VerticalTransformTest extends BaseCoordinateTransformTest {

    private final Geographic2DCRS RGF93crs = new Geographic2DCRS(new Identifier("EPSG", "4171", "RGF93"), GeodeticDatum.RGF93);
    Geographic2DCRS WGS84crs = new Geographic2DCRS(new Identifier("EPSG", "4326", "WGS84"), GeodeticDatum.RGF93);


    @Test
    void testIGN69() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN69"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{50, 0, 50};
        double[] expectedPoint = new double[]{50 * PI / 180, 0, 94.194};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN69WithProjection() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 Lambert93 + IGN69"),
                (ProjectedCRS) cRSFactory.getCRS("IGNF:LAMB93"),
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{50.09631762 * PI / 180, 3.69807131 * PI / 180, 144.492};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3, 1E-3));
    }

    @Test
    void testIGN69BetweenCRS() throws Exception {
        CompoundCRS sourceCRS = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 Lambert93 + IGN69"),
                (ProjectedCRS) cRSFactory.getCRS("IGNF:LAMB93"),
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        Geographic3DCRS targetCRS = new Geographic3DCRS(new Identifier("EPSG", "4171", "RGF93"), GeodeticDatum.RGF93);
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{50.09631762, 3.69807131, 144.492};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3, 1E-3));
    }

    @Test
    void testIGN78() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN78"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5721", "IGN78"), VerticalDatum.IGN78, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{42.2, 9, 100};
        double[] expectedPoint = new double[]{42.2 * PI / 180, 9 * PI / 180, 150.224};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN69BetweenCRSPlus() throws Exception {
        CompoundCRS sourceCRS = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN69"),
                (ProjectedCRS) cRSFactory.getCRS("IGNF:LAMB93"),
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        CompoundCRS targetCRS = new CompoundCRS(new Identifier(CompoundCRS.class, "NTF LAMB2E + IGN69"),
                (ProjectedCRS) cRSFactory.getCRS("IGNF:LAMBE"),
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{697570.330, 2567559.095, 100.000};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3, 1E-3));
    }

    @Test
    void testIGN88GTBT() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Guadeloupe 1988"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5757", "Guadeloupe 1988"), VerticalDatum.IGN88GTBT, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.5 * PI / 180, 59.747};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN92LD() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1992 LD height"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5618", "IGN 1992 LD height"), VerticalDatum.IGN92LD, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.3 * PI / 180, -61 * PI / 180, 55.584};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88LS() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1988 LS height"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5616", "IGN 1988 LS height"), VerticalDatum.IGN88LS, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.9 * PI / 180, -61.5 * PI / 180, 60.010};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN87MART() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Martinique 1987"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5756", "Martinique 1987"), VerticalDatum.IGN87MART, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.5 * PI / 180, -61 * PI / 180, 61.765};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88MG() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1988 MG height"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5617", "IGN 1988 MG height"), VerticalDatum.IGN88MG, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.2 * PI / 180, 58.652};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SB() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1988 SB height"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5619", "IGN 1988 SB height"), VerticalDatum.IGN88SB, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.9 * PI / 180, -62.8 * PI / 180, 57.365};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SM() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1988 SM height"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5620", "IGN 1988 SM height"), VerticalDatum.IGN88SM, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.1 * PI / 180, -63 * PI / 180, 56.727};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testNGG77GUY() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + NGG1977"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5755", "NGG1977"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{4, -53, 100};
        double[] expectedPoint = new double[]{4 * PI / 180, -53 * PI / 180, 66.370};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testGuyanneTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + NGG1977"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5755", "NGG1977"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "CSG67UTM22 + NGG77GUY"),
                (GeodeticCRS) cRSFactory.getCRS("IGNF:CSG67UTM22"),
                new VerticalCRS(new Identifier("EPSG", "5755", "NGG1977"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{4, -53, 100};
        double[] expectedPoint = new double[]{277944.208, 442285.668, 100};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testSHOM53() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + SHOM53"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-13, 45, 100};
        double[] expectedPoint = new double[]{-13 * PI / 180, 45 * PI / 180, 80.292};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testMayotteTransformation() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + SHOM53"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        GeodeticCRS crsTemp = (GeodeticCRS) cRSFactory.getCRS("IGNF:MAYO50UTM38S");
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "MAYO50UTM38S + SHOM53"), crsTemp,
                new VerticalCRS(new Identifier(VerticalCRS.class, "SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs3 = new CompoundCRS(new Identifier(CompoundCRS.class), crsTemp,
                new VerticalCRS(new Identifier(VerticalCRS.class),
                new VerticalDatum(new Identifier(VerticalDatum.class), "", "", crsTemp.getDatum().getEllipsoid()), VerticalCRS.HEIGHT_CS));
        double[] inputPoint = new double[]{-13, 45, 100};
        double[] expectedPoint = new double[]{499771.634, 8563128.517, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        double[] expectedPoint2 = new double[]{499771.634, 8563128.517, 78.815};
        double[] outputPoint2 = transform(crs, crs3, inputPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint2, expectedPoint2, 1E-3, 1E-3));
    }

    @Test
    void testIGN62KER() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN62KER"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-49, 69, 100};
        double[] expectedPoint = new double[]{-49 * PI / 180, 69 * PI / 180, 140.638};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testKerguelenTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN62KER"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "NGF IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "KERG62UTM42S + IGN62KER"),
                (GeodeticCRS) cRSFactory.getCRS("IGNF:KERG62UTM42S"),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-49, 69, 100};
        double[] expectedPoint = new double[]{500202.123, 4572372.239, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testDANGER50() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Danger 1950"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5792", "Danger 1950"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{46.7, -56.2, 100};
        double[] expectedPoint = new double[]{46.7 * PI / 180, -56.2 * PI / 180, 98.266};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testSaintPierreEtMiquelonTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Danger 1950"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5792", "Danger 1950"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "STPM50UTM21 + Danger 1950"),
                (GeodeticCRS) cRSFactory.getCRS("IGNF:STPM50UTM21"),
                new VerticalCRS(new Identifier("EPSG", "5792", "Danger 1950"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{46.8, -56.2, 100};
        double[] expectedPoint = new double[]{560806.129, 5182759.317, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testBORASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Bora Bora SAU 2001"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5607", "Bora Bora SAU 2001"), VerticalDatum.BORASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.5, -151.7, 100};
        double[] expectedPoint = new double[]{-16.5 * PI / 180, -151.7 * PI / 180, 109.954};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testFAKARAVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + FAKARAVA"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "FAKARAVA"), VerticalDatum.FAKARAVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16, -145.5, 100};
        double[] expectedPoint = new double[]{-16 * PI / 180, -145.5 * PI / 180, 99.571};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testGAMBIER() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + GAMBIER"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "GAMBIER"), VerticalDatum.GAMBIER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23, -135, 100};
        double[] expectedPoint = new double[]{-23 * PI / 180, -135 * PI / 180, 92.027};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testHAO() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + HAO"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "HAO"), VerticalDatum.HAO, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-18, -141, 100};
        double[] expectedPoint = new double[]{-18 * PI / 180, -141 * PI / 180, 94.022};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testHIVAOA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + HIVAOA"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "HIVAOA"), VerticalDatum.HIVAOA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-9.8, -139, 100};
        double[] expectedPoint = new double[]{-9.8 * PI / 180, -139 * PI / 180, 101.980};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testHUAHINESAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Huahine SAU 2001"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5605", "Huahine SAU 2001"), VerticalDatum.HUAHINESAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.8, -151, 100};
        double[] expectedPoint = new double[]{-16.8 * PI / 180, -151 * PI / 180, 108.121};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGNTAHITI66() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN 1966"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5601", "IGN 1966"), VerticalDatum.IGNTAHITI66, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.5, -149.5, 100};
        double[] expectedPoint = new double[]{-17.5 * PI / 180, -149.5 * PI / 180, 108.196};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testMAIAO01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + MAIAO01"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "MAIAO01"), VerticalDatum.MAIAO01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.7, -150.6, 100};
        double[] expectedPoint = new double[]{-17.7 * PI / 180, -150.6 * PI / 180, 105.168};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testMATAIVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + MATAIVA"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "MATAIVA"), VerticalDatum.MATAIVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-14.9, -148.6, 100};
        double[] expectedPoint = new double[]{-14.9 * PI / 180, -148.6 * PI / 180, 102.484};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testMAUPITISAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Maupiti SAU 2001"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5604", "Maupiti SAU 2001"), VerticalDatum.MAUPITISAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.5, -152.3, 100};
        double[] expectedPoint = new double[]{-16.5 * PI / 180, -152.3 * PI / 180, 108.282};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testMOOREASAU81() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Moorea SAU 1981"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5602", "Moorea SAU 1981"), VerticalDatum.MOOREASAU81, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.5, -149.9, 100};
        double[] expectedPoint = new double[]{-17.5 * PI / 180, -149.9 * PI / 180, 106.686};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testNUKUHIVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + NUKUHIVA"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "NUKUHIVA"), VerticalDatum.NUKUHIVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-8.8, -140, 100};
        double[] expectedPoint = new double[]{-8.8 * PI / 180, -140 * PI / 180, 104.512};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testRAIATEASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Raiatea SAU 2001"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5603", "Raiatea SAU 2001"), VerticalDatum.RAIATEASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.8, -151.5, 100};
        double[] expectedPoint = new double[]{-16.8 * PI / 180, -151.5 * PI / 180, 109.936};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testRAIVAVAE() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + RAIVAVAE"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "RAIVAVAE"), VerticalDatum.RAIVAVAE, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23.9, -147.6, 100};
        double[] expectedPoint = new double[]{-23.9 * PI / 180, -147.6 * PI / 180, 98.656};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testREAO() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + REAO"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "REAO"), VerticalDatum.REAO, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-18.5, -136.4, 100};
        double[] expectedPoint = new double[]{-18.5 * PI / 180, -136.4 * PI / 180, 91.930};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testRURUTU() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + RURUTU"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "RURUTU"), VerticalDatum.RURUTU, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-22.5, -151.4, 100};
        double[] expectedPoint = new double[]{-22.5 * PI / 180, -151.4 * PI / 180, 102.139};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testTAHAASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + Tahaa SAU 2001"), RGF93crs,
                new VerticalCRS(new Identifier("EPSG", "5606", "Tahaa SAU 2001"), VerticalDatum.TAHAASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.6, -151.4, 100};
        double[] expectedPoint = new double[]{-16.6 * PI / 180, -151.4 * PI / 180, 109.790};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testTIKEHAU() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + TIKEHAU"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "TIKEHAU"), VerticalDatum.TIKEHAU, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-15, -148, 100};
        double[] expectedPoint = new double[]{-15 * PI / 180, -148 * PI / 180, 103.785};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testTUBUAI() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + TUBUAI"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "TUBUAI"), VerticalDatum.TUBUAI, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23.3, -149.5, 100};
        double[] expectedPoint = new double[]{-23.3 * PI / 180, -149.5 * PI / 180, 99.899};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testTUPAI01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + TUPAI01"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "TUPAI01"), VerticalDatum.TUPAI01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.3, -151.9, 100};
        double[] expectedPoint = new double[]{-16.3 * PI / 180, -151.9 * PI / 180, 108.762};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testRAR07() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + RAR07"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "RAR07"), VerticalDatum.RAR07, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-21.1, 55.5, 100};
        double[] expectedPoint = new double[]{-21.1 * PI / 180, 55.5 * PI / 180, 108.762};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        //System.out.println(outputPoint[0] * 180 / PI + ", " + outputPoint[1] * 180 / PI + ", " + outputPoint[2]);
        // TO DO find a test for this one
        //assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        //assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    void testIGN88GTBTold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88GTBTold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88GTBTold"), VerticalDatum.IGN88GTBTold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.5 * PI / 180, 59.613};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN92LDold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN92LDold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN92LDold"), VerticalDatum.IGN92LDold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.3 * PI / 180, -61 * PI / 180, 56.623};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88LSold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88LSold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88LSold"), VerticalDatum.IGN88LSold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.9 * PI / 180, -61.5 * PI / 180, 60.000};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN87MARTold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84MART + IGN87MARTold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84MART),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN87MARTold"), VerticalDatum.IGN87MARTold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.5 * PI / 180, -61 * PI / 180, 61.882};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88MGold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88MGold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88MGold"), VerticalDatum.IGN88MGold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.2 * PI / 180, 59.199};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SBold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84SBSM + IGN88SBold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SBold"), VerticalDatum.IGN88SBold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.9 * PI / 180, -62.8 * PI / 180, 58.074};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SMold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84SBSM + IGN88SMold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SMold"), VerticalDatum.IGN88SMold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.1 * PI / 180, -63 * PI / 180, 57.299};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88GTBToldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88GTBTold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88GTBTold"), VerticalDatum.IGN88GTBTold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + IGN88GTBT"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88GTBT"), VerticalDatum.IGN88GTBT, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16.00000066, -61.50000549, 99.427};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN92LDoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN92LDold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN92LDold"), VerticalDatum.IGN92LDold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN92LD"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN92LD"), VerticalDatum.IGN92LD, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.30000109, -61.00000556, 100.612};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88LSoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88LSold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88LSold"), VerticalDatum.IGN88LSold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN88LS"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88LS"), VerticalDatum.IGN88LS, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.90000062, -61.50000542, 99.552};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN87MARToldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84MART + IGN87MARTold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84MART),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN87MARTold"), VerticalDatum.IGN87MARTold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN87MART"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN87MART"), VerticalDatum.IGN87MART, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.49999981, -61.00000619, 100.017};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88MGoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84GUAD + IGN88MGold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88MGold"), VerticalDatum.IGN88MGold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN88MG"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88MG"), VerticalDatum.IGN88MG, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16.00000086, -61.20000540, 100.119};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SBoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84SBSM + IGN88SBold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SBold"), VerticalDatum.IGN88SBold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN88SB"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SB"), VerticalDatum.IGN88SB, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.90000142, -62.80000572, 100.114};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testIGN88SMoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier(CompoundCRS.class, "WGS84SBSM + IGN88SMold"),
                new Geographic2DCRS(new Identifier(Geographic2DCRS.class), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SMold"), VerticalDatum.IGN88SMold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier(CompoundCRS.class, "RGF93 + IGN88SM"), RGF93crs,
                new VerticalCRS(new Identifier(VerticalCRS.class, "IGN88SM"), VerticalDatum.IGN88SM, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.10000162, -63.00000537, 99.923};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-8, 1E-3));
    }

    @Test
    void testCompdCRS() throws CRSException, IllegalCoordinateException, CoordinateOperationException {
        String prjString = "COMPD_CS[\"RGF93 / Lambert-93 + IGN69\",\n"
                + "    PROJCS[\"RGF93 / Lambert-93\",\n"
                + "        GEOGCS[\"RGF93\",\n"
                + "            DATUM[\"Reseau_Geodesique_Francais_1993\",\n"
                + "                SPHEROID[\"GRS 1980\",6378137,298.257222101,\n"
                + "                    AUTHORITY[\"EPSG\",\"7019\"]],\n"
                + "                TOWGS84[0,0,0,0,0,0,0],\n"
                + "                AUTHORITY[\"EPSG\",\"6171\"]],\n"
                + "            PRIMEM[\"Greenwich\",0,\n"
                + "                AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "            UNIT[\"degree\",0.01745329251994328,\n"
                + "                AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "            AUTHORITY[\"EPSG\",\"4171\"]],\n"
                + "        UNIT[\"metre\",1,\n"
                + "            AUTHORITY[\"EPSG\",\"9001\"]],\n"
                + "        PROJECTION[\"Lambert_Conformal_Conic_2SP\"],\n"
                + "        PARAMETER[\"standard_parallel_1\",49],\n"
                + "        PARAMETER[\"standard_parallel_2\",44],\n"
                + "        PARAMETER[\"latitude_of_origin\",46.5],\n"
                + "        PARAMETER[\"central_meridian\",3],\n"
                + "        PARAMETER[\"false_easting\",700000],\n"
                + "        PARAMETER[\"false_northing\",6600000],\n"
                + "        AUTHORITY[\"EPSG\",\"2154\"],\n"
                + "        AXIS[\"X\",EAST],\n"
                + "        AXIS[\"Y\",NORTH]],\n"
                + "    VERT_CS[\"IGN69\",\n"
                + "        VERT_DATUM[\"Nivellement General de la France - IGN69\",2005,\n"
                + "            AUTHORITY[\"EPSG\",\"5119\"]],\n"
                + "        UNIT[\"m\",1.0],\n"
                + "        AXIS[\"Gravity-related height\",UP]]]";
        CoordinateReferenceSystem scrs = cRSFactory.createFromPrj(prjString);
        assertInstanceOf(CompoundCRS.class, scrs);
        CompoundCRS sourceCRS = (CompoundCRS) scrs;
        prjString = "COMPD_CS[\"RGF93 + IGN69\",\n"
                + "     GEOGCS[\"RGF93\",\n"
                + "        DATUM[\"Reseau_Geodesique_Francais_1993\",\n"
                + "            SPHEROID[\"GRS 1980\",6378137,298.257222101,\n"
                + "                AUTHORITY[\"EPSG\",\"7019\"]],\n"
                + "            TOWGS84[0,0,0,0,0,0,0],\n"
                + "            AUTHORITY[\"EPSG\",\"6171\"]],\n"
                + "        PRIMEM[\"Greenwich\",0,\n"
                + "            AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "        UNIT[\"degree\",0.01745329251994328,\n"
                + "            AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"4171\"]],\n"
                + "    VERT_CS[\"IGN69\",\n"
                + "        VERT_DATUM[\"Hauteur ellipsoidale - GRS80\",2002,\n"
                + "            AUTHORITY[\"EPSG\",\"5019\"]],\n"
                + "        UNIT[\"m\",1.0],\n"
                + "        AXIS[\"Ellipsoidal height\",UP]]]";
        CoordinateReferenceSystem tcrs = cRSFactory.createFromPrj(prjString);
        assertInstanceOf(CompoundCRS.class, tcrs);
        CompoundCRS targetCRS = (CompoundCRS) tcrs;
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{3.69807131, 50.09631762, 144.492};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-8, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3, 1E-3));
    }

    @Test
    void testVerticalCRS() throws CRSException {
        String prjString = "VERT_CS[\"GRS80VCS\",\n"
                + "    VERT_DATUM[\"Hauteur ellipsoidale - GRS80\",2002,\n"
                + "        AUTHORITY[\"EPSG\",\"5019\"]],\n"
                + "    UNIT[\"m\",1.0],\n"
                + "    AXIS[\"Ellipsoidal height\",UP]]";
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(prjString);
        assertInstanceOf(VerticalCRS.class, crs);
        VerticalCRS vcrs = (VerticalCRS) crs;
        assertEquals(vcrs.getDatum().getEllipsoid(), Ellipsoid.GRS80);
    }

    @Test
    void testGeocentricCRS() throws CRSException, IllegalCoordinateException, CoordinateOperationException {
        CompoundCRS sourceCRS = new CompoundCRS(new Identifier(CompoundCRS.class, "NTF LAMB2E + IGN69"),
                (ProjectedCRS) cRSFactory.getCRS("IGNF:LAMBE"),
                new VerticalCRS(new Identifier("EPSG", "5720", "IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        String prjString = "GEOCCS[\"RGF93 (geocentric)\",\n"
                + "    DATUM[\"Reseau Geodesique Francais 1993\",\n"
                + "        SPHEROID[\"GRS 1980\",6378137.0,298.257222101,\n"
                + "            AUTHORITY[\"EPSG\",\"7019\"]],\n"
                + "        TOWGS84[0.0,0.0,0.0,0.0,0.0,0.0,0.0],\n"
                + "        AUTHORITY[\"EPSG\",\"6171\"]],\n"
                + "    PRIMEM[\"Greenwich\",0.0,\n"
                + "        AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "    UNIT[\"m\",1.0],\n"
                + "    AXIS[\"Geocentric X\",OTHER],\n"
                + "    AXIS[\"Geocentric Y\",EAST],\n"
                + "    AXIS[\"Geocentric Z\",NORTH],\n"
                + "    AUTHORITY[\"EPSG\",\"4370\"]]";
        CoordinateReferenceSystem tcrs = cRSFactory.createFromPrj(prjString);
        assertInstanceOf(GeocentricCRS.class, tcrs);
        GeocentricCRS targetCRS = (GeocentricCRS) tcrs;
        double[] inputPoint = new double[]{650000, 2300000, 100};
        double[] expectedPoint = new double[]{4294839.989, 225283.135, 4694418.993};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3, 1E-3));
    }
}
