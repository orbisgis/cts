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

import static java.lang.Math.PI;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.CompoundCRS;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.crs.Geographic2DCRS;
import org.cts.crs.ProjectedCRS;
import org.cts.crs.VerticalCRS;
import org.cts.cs.GeographicExtent;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.LambertConicConformal1SP;
import org.cts.op.projection.LambertConicConformal2SP;
import org.cts.op.transformation.FrenchGeocentricNTF2RGF;
import org.cts.op.transformation.NTv2GridShiftTransformation;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This class tests transformation using CoumpoundCRS and VerticalCRS.
 *
 * @author Jules Party
 */
public class VerticalTransformTest extends BaseCoordinateTransformTest {

    @Test
    public void testIGN69() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN69"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("EPSG", "5720", "NGF IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{50, 0, 50};
        double[] expectedPoint = new double[]{50 * PI / 180, 0, 94.194};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN69WithProjection() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN69"),
                new ProjectedCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93, LambertConicConformal2SP.LAMBERT93),
                new VerticalCRS(new Identifier("EPSG", "5720", "NGF IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{50.09631762 * PI / 180, 3.69807131 * PI / 180, 144.492};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN69BetweenCRS() throws Exception {
        CompoundCRS sourceCRS = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN69"),
                new ProjectedCRS(new Identifier("EPSG", "4807", "NTF (Paris)"), GeodeticDatum.RGF93, LambertConicConformal2SP.LAMBERT93),
                new VerticalCRS(new Identifier("EPSG", "5720", "NGF IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        CompoundCRS targetCRS = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN69"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("EPSG", "5000", "NGF IGN69"), new VerticalDatum(new Identifier(VerticalDatum.class), GeographicExtent.WORLD, null, null, VerticalDatum.Type.ELLIPSOIDAL, null, GeodeticDatum.RGF93),
                VerticalCRS.HEIGHT_CS));
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{50.09631762, 3.69807131, 144.492};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN78() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN78"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "NTF (Paris)"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("EPSG", "5720", "NGF IGN78"), VerticalDatum.IGN78, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{42.2, 9, 100};
        double[] expectedPoint = new double[]{42.2 * PI / 180, 9 * PI / 180, 150.224};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN69BetweenCRSPlus() throws Exception {
        CompoundCRS sourceCRS = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN69"),
                new ProjectedCRS(new Identifier("EPSG", "4807", "NTF (Paris)"), GeodeticDatum.RGF93, LambertConicConformal2SP.LAMBERT93),
                new VerticalCRS(new Identifier("EPSG", "5720", "NGF IGN69"), VerticalDatum.IGN69, VerticalCRS.ALTITUDE_CS));
        ProjectedCRS crs = new ProjectedCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.NTF_PARIS, LambertConicConformal1SP.LAMBERT2E);
        crs.addGridTransformation(GeodeticDatum.RGF93, new CoordinateOperationSequence(
                                        new Identifier(CoordinateOperation.class, "NTF" + " to " + "RGF93"),
                                        GeodeticDatum.NTF_PARIS.getCoordinateOperations(GeodeticDatum.NTF).get(0),
                                        new LongitudeRotation(GeodeticDatum.NTF.getPrimeMeridian().getLongitudeFromGreenwichInRadians()),
                                        new Geographic2Geocentric(GeodeticDatum.NTF.getEllipsoid()),
                                        new FrenchGeocentricNTF2RGF(),
                                        new Geocentric2Geographic(GeodeticDatum.RGF93.getEllipsoid()),
                                        new LongitudeRotation(-GeodeticDatum.RGF93.getPrimeMeridian().getLongitudeFromGreenwichInRadians())));
        NTv2GridShiftTransformation gt = NTv2GridShiftTransformation.createNTv2GridShiftTransformation("ntf_r93.gsb");
                            gt.setMode(NTv2GridShiftTransformation.SPEED);
                            crs.addGridTransformation(GeodeticDatum.datumFromName.get(gt.getToDatum()), gt);
        CompoundCRS targetCRS = new CompoundCRS(new Identifier("EPSG", "7401", "NTF (Paris) + NGF IGN69"), crs,
                new VerticalCRS(new Identifier("EPSG", "5000", "NGF IGN69"),
                VerticalDatum.IGN69,
                VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{697570.330, 2567559.095, 100.000};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88GTBT() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88GTBT"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88GTBT"), VerticalDatum.IGN88GTBT, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.5 * PI / 180, 59.747};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN92LD() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN92LD"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN92LD"), VerticalDatum.IGN92LD, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.3 * PI / 180, -61 * PI / 180, 55.584};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88LS() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88LS"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88LS"), VerticalDatum.IGN88LS, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.9 * PI / 180, -61.5 * PI / 180, 60.010};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN87MART() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN87MART"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN87MART"), VerticalDatum.IGN87MART, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.5 * PI / 180, -61 * PI / 180, 61.765};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88MG() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88MG"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88MG"), VerticalDatum.IGN88MG, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.2 * PI / 180, 58.652};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SB() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SB"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SB"), VerticalDatum.IGN88SB, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.9 * PI / 180, -62.8 * PI / 180, 57.365};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SM() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SM"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SM"), VerticalDatum.IGN88SM, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.1 * PI / 180, -63 * PI / 180, 56.727};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testNGG77GUY() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF NGG77GUY"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF NGG77GUY"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{4, -53, 100};
        double[] expectedPoint = new double[]{4 * PI / 180, -53 * PI / 180, 66.370};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testGuyanneTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF NGG77GUY"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF NGG77GUY"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        GeodeticCRS crsTemp = (GeodeticCRS) cRSFactory.getCRS("IGNF:CSG67UTM22");
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "100002", "? + NGF NGG77GUY"), crsTemp,
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF NGG77GUY"), VerticalDatum.NGG77GUY, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{4, -53, 100};
        double[] expectedPoint = new double[]{277944.208, 442285.668, 100};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testSHOM53() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF SHOM53"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-13, 45, 100};
        double[] expectedPoint = new double[]{-13 * PI / 180, 45 * PI / 180, 80.292};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testMayotteTransformation() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF SHOM53"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        GeodeticCRS crsTemp = (GeodeticCRS) cRSFactory.getCRS("IGNF:MAYO50UTM38S");
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "100001", "? + NGF SHOM53"), crsTemp,
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF SHOM53"), VerticalDatum.SHOM53, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs3 = new CompoundCRS(new Identifier("EPSG", "100003", "? + NGF SHOM53"), crsTemp,
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF SHOM53"),
                new VerticalDatum(crsTemp.getDatum().getIdentifier(), (GeographicExtent) crsTemp.getDatum().getExtent(), "", "",
                VerticalDatum.Type.ELLIPSOIDAL, null, crsTemp.getDatum()), VerticalCRS.HEIGHT_CS));
        double[] inputPoint = new double[]{-13, 45, 100};
        double[] expectedPoint = new double[]{499771.634, 8563128.517, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        double[] expectedPoint2 = new double[]{499771.634, 8563128.517, 78.815};
        double[] outputPoint2 = transform(crs, crs3, inputPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint2, expectedPoint2, 1E-3));
    }

    @Test
    public void testIGN62KER() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN62KER"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-49, 69, 100};
        double[] expectedPoint = new double[]{-49 * PI / 180, 69 * PI / 180, 140.638};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testKerguelenTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN62KER"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        GeodeticCRS crsTemp = (GeodeticCRS) cRSFactory.getCRS("IGNF:KERG62UTM42S");
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "? + NGF IGN62KER"), crsTemp,
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN62KER"), VerticalDatum.IGN62KER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-49, 69, 100};
        double[] expectedPoint = new double[]{500202.123, 4572372.239, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testDANGER50() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF DANGER50"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF DANGER50"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{46.7, -56.2, 100};
        double[] expectedPoint = new double[]{46.7 * PI / 180, -56.2 * PI / 180, 98.266};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testSaintPierreEtMiquelonTransfo() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF DANGER50"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF DANGER50"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        GeodeticCRS crsTemp = (GeodeticCRS) cRSFactory.getCRS("IGNF:STPM50UTM21");
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "? + NGF DANGER50"), crsTemp,
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF DANGER50"), VerticalDatum.DANGER50, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{46.8, -56.2, 100};
        double[] expectedPoint = new double[]{560806.129, 5182759.317, 100.000};
        double[] outputPoint = transform(crs, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testBORASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF BORASAU01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF BORASAU01"), VerticalDatum.BORASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.5, -151.7, 100};
        double[] expectedPoint = new double[]{-16.5 * PI / 180, -151.7 * PI / 180, 109.954};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testFAKARAVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF FAKARAVA"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF FAKARAVA"), VerticalDatum.FAKARAVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16, -145.5, 100};
        double[] expectedPoint = new double[]{-16 * PI / 180, -145.5 * PI / 180, 99.571};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testGAMBIER() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF GAMBIER"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF GAMBIER"), VerticalDatum.GAMBIER, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23, -135, 100};
        double[] expectedPoint = new double[]{-23 * PI / 180, -135 * PI / 180, 92.027};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testHAO() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF HAO"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF HAO"), VerticalDatum.HAO, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-18, -141, 100};
        double[] expectedPoint = new double[]{-18 * PI / 180, -141 * PI / 180, 94.022};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testHIVAOA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF HIVAOA"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF HIVAOA"), VerticalDatum.HIVAOA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-9.8, -139, 100};
        double[] expectedPoint = new double[]{-9.8 * PI / 180, -139 * PI / 180, 101.980};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testHUAHINESAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF HUAHINESAU01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF HUAHINESAU01"), VerticalDatum.HUAHINESAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.8, -151, 100};
        double[] expectedPoint = new double[]{-16.8 * PI / 180, -151 * PI / 180, 108.121};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGNTAHITI66() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGNTAHITI66"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGNTAHITI66"), VerticalDatum.IGNTAHITI66, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.5, -149.5, 100};
        double[] expectedPoint = new double[]{-17.5 * PI / 180, -149.5 * PI / 180, 108.196};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testMAIAO01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF MAIAO01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF MAIAO01"), VerticalDatum.MAIAO01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.7, -150.6, 100};
        double[] expectedPoint = new double[]{-17.7 * PI / 180, -150.6 * PI / 180, 105.168};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testMATAIVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF MATAIVA"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF MATAIVA"), VerticalDatum.MATAIVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-14.9, -148.6, 100};
        double[] expectedPoint = new double[]{-14.9 * PI / 180, -148.6 * PI / 180, 102.484};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testMAUPITISAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF MAUPITISAU01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF MAUPITISAU01"), VerticalDatum.MAUPITISAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.5, -152.3, 100};
        double[] expectedPoint = new double[]{-16.5 * PI / 180, -152.3 * PI / 180, 108.282};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testMOOREASAU81() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF MOOREASAU81"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF MOOREASAU81"), VerticalDatum.MOOREASAU81, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-17.5, -149.9, 100};
        double[] expectedPoint = new double[]{-17.5 * PI / 180, -149.9 * PI / 180, 106.686};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testNUKUHIVA() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF NUKUHIVA"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF NUKUHIVA"), VerticalDatum.NUKUHIVA, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-8.8, -140, 100};
        double[] expectedPoint = new double[]{-8.8 * PI / 180, -140 * PI / 180, 104.512};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testRAIATEASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF RAIATEASAU01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF RAIATEASAU01"), VerticalDatum.RAIATEASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.8, -151.5, 100};
        double[] expectedPoint = new double[]{-16.8 * PI / 180, -151.5 * PI / 180, 109.936};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testRAIVAVAE() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF RAIVAVAE"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF RAIVAVAE"), VerticalDatum.RAIVAVAE, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23.9, -147.6, 100};
        double[] expectedPoint = new double[]{-23.9 * PI / 180, -147.6 * PI / 180, 98.656};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testREAO() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF REAO"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF REAO"), VerticalDatum.REAO, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-18.5, -136.4, 100};
        double[] expectedPoint = new double[]{-18.5 * PI / 180, -136.4 * PI / 180, 91.930};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testRURUTU() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF RURUTU"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF RURUTU"), VerticalDatum.RURUTU, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-22.5, -151.4, 100};
        double[] expectedPoint = new double[]{-22.5 * PI / 180, -151.4 * PI / 180, 102.139};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testTAHAASAU01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF TAHAASAU01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF TAHAASAU01"), VerticalDatum.TAHAASAU01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.6, -151.4, 100};
        double[] expectedPoint = new double[]{-16.6 * PI / 180, -151.4 * PI / 180, 109.790};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testTIKEHAU() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF TIKEHAU"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF TIKEHAU"), VerticalDatum.TIKEHAU, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-15, -148, 100};
        double[] expectedPoint = new double[]{-15 * PI / 180, -148 * PI / 180, 103.785};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testTUBUAI() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF TUBUAI"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF TUBUAI"), VerticalDatum.TUBUAI, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-23.3, -149.5, 100};
        double[] expectedPoint = new double[]{-23.3 * PI / 180, -149.5 * PI / 180, 99.899};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testTUPAI01() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF TUPAI01"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF TUPAI01"), VerticalDatum.TUPAI01, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{-16.3, -151.9, 100};
        double[] expectedPoint = new double[]{-16.3 * PI / 180, -151.9 * PI / 180, 108.762};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testRAR07() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF RAR07"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF RAR07"), VerticalDatum.RAR07, VerticalCRS.ALTITUDE_CS));
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
    public void testIGN88GTBTold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88GTBTold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88GTBTold"), VerticalDatum.IGN88GTBTold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.5 * PI / 180, 59.613};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN92LDold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN92LDold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN92LDold"), VerticalDatum.IGN92LDold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.3 * PI / 180, -61 * PI / 180, 56.623};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88LSold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88LSold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88LSold"), VerticalDatum.IGN88LSold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.9 * PI / 180, -61.5 * PI / 180, 60.000};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN87MARTold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN87MARTold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84MART),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN87MARTold"), VerticalDatum.IGN87MARTold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.5 * PI / 180, -61 * PI / 180, 61.882};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88MGold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88MGold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88MGold"), VerticalDatum.IGN88MGold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16 * PI / 180, -61.2 * PI / 180, 59.199};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SBold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SBold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SBold"), VerticalDatum.IGN88SBold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.9 * PI / 180, -62.8 * PI / 180, 58.074};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SMold() throws Exception {
        CompoundCRS crs = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SMold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SMold"), VerticalDatum.IGN88SMold, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.1 * PI / 180, -63 * PI / 180, 57.299};
        double[] outputPoint = crs.toGeographicCoordinateConverter().transform(inputPoint.clone());
        double[] checkPoint = crs.fromGeographicCoordinateConverter().transform(expectedPoint.clone());
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88GTBToldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84GUAD + NGF IGN88GTBTold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84GUAD"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88GTBTold"), VerticalDatum.IGN88GTBTold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88GTBT"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88GTBT"), VerticalDatum.IGN88GTBT, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.5, 100};
        double[] expectedPoint = new double[]{16.00000066, -61.50000549, 99.427};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN92LDoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84GUAD + NGF IGN92LDold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84GUAD"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN92LDold"), VerticalDatum.IGN92LDold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN92LD"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN92LD"), VerticalDatum.IGN92LD, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16.3, -61, 100};
        double[] expectedPoint = new double[]{16.30000109, -61.00000556, 100.612};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88LSoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84GUAD + NGF IGN88LSold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84GUAD"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88LSold"), VerticalDatum.IGN88LSold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88LS"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88LS"), VerticalDatum.IGN88LS, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{15.9, -61.5, 100};
        double[] expectedPoint = new double[]{15.90000062, -61.50000542, 99.552};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN87MARToldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84MART + NGF IGN87MARTold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84MART"), GeodeticDatum.WGS84MART),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN87MARTold"), VerticalDatum.IGN87MARTold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN87MART"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN87MART"), VerticalDatum.IGN87MART, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{14.5, -61, 100};
        double[] expectedPoint = new double[]{14.49999981, -61.00000619, 100.017};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88MGoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84GUAD + NGF IGN88MGold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84GUAD"), GeodeticDatum.WGS84GUAD),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88MGold"), VerticalDatum.IGN88MGold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88MG"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88MG"), VerticalDatum.IGN88MG, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{16, -61.2, 100};
        double[] expectedPoint = new double[]{16.00000086, -61.20000540, 100.119};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SBoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84SBSM + NGF IGN88SBold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84SBSM"), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SBold"), VerticalDatum.IGN88SBold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SB"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SB"), VerticalDatum.IGN88SB, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{17.9, -62.8, 100};
        double[] expectedPoint = new double[]{17.90000142, -62.80000572, 100.114};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testIGN88SMoldToNew() throws Exception {
        CompoundCRS crs1 = new CompoundCRS(new Identifier("EPSG", "7400", "WGS84SBSM + NGF IGN88SMold"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "WGS84SBSM"), GeodeticDatum.WGS84SBSM),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SMold"), VerticalDatum.IGN88SMold, VerticalCRS.ALTITUDE_CS));
        CompoundCRS crs2 = new CompoundCRS(new Identifier("EPSG", "7400", "RGF93 + NGF IGN88SM"),
                new Geographic2DCRS(new Identifier("EPSG", "4807", "RGF93"), GeodeticDatum.RGF93),
                new VerticalCRS(new Identifier("UNKNOWN", "UNKNOWN", "NGF IGN88SM"), VerticalDatum.IGN88SM, VerticalCRS.ALTITUDE_CS));
        double[] inputPoint = new double[]{18.1, -63, 100};
        double[] expectedPoint = new double[]{18.10000162, -63.00000537, 99.923};
        double[] outputPoint = transform(crs1, crs2, inputPoint);
        double[] checkPoint = transform(crs2, crs1, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" ellipsoidal height to altitude.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testCompdCRS() throws CRSException, IllegalCoordinateException {
        String prjString = "COMPD_CS[\"NTF (Paris) / France II + NGF IGN69\",\n"
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
                + "    VERT_CS[\"NGF IGN69\",\n"
                + "        VERT_DATUM[\"Nivellement General de la France - IGN69\",2005,\n"
                + "            AUTHORITY[\"EPSG\",\"5119\"]],\n"
                + "        UNIT[\"m\",1.0],\n"
                + "        AXIS[\"Gravity-related height\",UP],\n"
                + "        AUTHORITY[\"EPSG\",\"5720\"]],\n"
                + "    AUTHORITY[\"EPSG\",\"7402\"]]";
        CoordinateReferenceSystem scrs = cRSFactory.createFromPrj(prjString);
        assertTrue(scrs instanceof CompoundCRS);
        CompoundCRS sourceCRS = (CompoundCRS) scrs;
        prjString = "COMPD_CS[\"NTF (Paris) / France II + NGF IGN69\",\n"
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
                + "    VERT_CS[\"NGF IGN69\",\n"
                + "        VERT_DATUM[\"Hauteur ellipsoidale - GRS80\",2002,\n"
                + "            AUTHORITY[\"EPSG\",\"5019\"]],\n"
                + "        UNIT[\"m\",1.0],\n"
                + "        AXIS[\"Ellipsoidal height\",UP],\n"
                + "        AUTHORITY[\"EPSG\",\"5720\"]],\n"
                + "    AUTHORITY[\"EPSG\",\"7402\"]]";
        CoordinateReferenceSystem tcrs = cRSFactory.createFromPrj(prjString);
        assertTrue(tcrs instanceof CompoundCRS);
        CompoundCRS targetCRS = (CompoundCRS) tcrs;
        double[] inputPoint = new double[]{750000, 7000000, 100};
        double[] expectedPoint = new double[]{3.69807131, 50.09631762, 144.492};
        double[] outputPoint = transform(sourceCRS, targetCRS, inputPoint);
        double[] checkPoint = transform(targetCRS, sourceCRS, expectedPoint);
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", outputPoint, expectedPoint, 1E-3));
        assertTrue(checkEquals3D(" altitude to ellipsoidal height.", checkPoint, inputPoint, 1E-3));
    }

    @Test
    public void testVerticalCRS() throws CRSException, IllegalCoordinateException {
        String prjString = "VERT_CS[\"NGF IGN69\",\n"
                + "    VERT_DATUM[\"Hauteur ellipsoidale - GRS80\",2002,\n"
                + "        AUTHORITY[\"EPSG\",\"5019\"]],\n"
                + "    UNIT[\"m\",1.0],\n"
                + "    AXIS[\"Ellipsoidal height\",UP],\n"
                + "    AUTHORITY[\"EPSG\",\"5720\"]]";
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(prjString);
        assertTrue(crs instanceof VerticalCRS);
        VerticalCRS vcrs = (VerticalCRS) crs;
        assertTrue(vcrs.getDatum().getEllipsoid().equals(Ellipsoid.GRS80));
    }
}
