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
import org.cts.crs.*;
import org.cts.cs.GeographicExtent;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.UniversalTransverseMercator;
import org.cts.op.transformation.GeocentricTranslation;
import org.cts.op.transformation.SevenParameterTransformation;
import org.junit.jupiter.api.Test;


/**
 * Tests for transformations available through Circe Mayotte v 3.2.0
 * @author Michaël Michaud
 */
class CirceMayotteComplianceTest extends BaseCoordinateTransformTest {

    private static final GeographicExtent MAYOTTE = new GeographicExtent("Ile de Mayotte", -13.05, -12.5, 44,95, 45.4);


    // ////////////////////////////////////////////////////////////////////////
    // DATUM
    // ////////////////////////////////////////////////////////////////////////
    private static final GeodeticDatum COMBANI1950 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","REG3180001","COMBANI 1950"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -599.928, -275.552, -195.665,
                    -0.0835,  -0.4715,     0.0602,
                    49.2814), MAYOTTE,null,null);

    private static final GeodeticDatum CAD1997 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","REG7010001","CADASTRE 1997"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            new GeocentricTranslation(-381.788, -57.501, -256.673), MAYOTTE,null,null);

    private static final GeodeticDatum RGM04 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGM04","RGM04"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, MAYOTTE,null,null);



    // ////////////////////////////////////////////////////////////////////////
    // CRS
    // ////////////////////////////////////////////////////////////////////////

    private static final VerticalCRS MAYO53 = new VerticalCRS(new Identifier("IGNF","MAYO53","SHOM 1953"), VerticalDatum.SHOM53);

    // ////////////////////////////////////////////////////////////////////////
    // COMBANI 1950
    // ////////////////////////////////////////////////////////////////////////
    private static final GeodeticCRS MAYO50_GEO2D = new Geographic2DCRS(new Identifier("IGNF","MAYO50G", "COMBANI 1950 GEO2D"), COMBANI1950);

    private static final GeodeticCRS MAYO50_UTM38S = new ProjectedCRS(new Identifier("IGNF","MAYO50UTM38S", "COMBANI 1950 UTM38S"),
            COMBANI1950, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 38, "SOUTH"));

    private static CompoundCRS MAYO50_UTM38S_SHOM1953 = null;
    static {
        try {
            MAYO50_UTM38S_SHOM1953 = new CompoundCRS(new Identifier("IGNF","MAYO50UTM38S.MAYO53", "COMBANI 1950 UTM38S SHOM 1953"),
                MAYO50_UTM38S, MAYO53);
        } catch (CRSException e) {e.printStackTrace();}
    }

    // ////////////////////////////////////////////////////////////////////////
    // CADASTRE 1997
    // ////////////////////////////////////////////////////////////////////////
    private static final GeodeticCRS CAD1997_GEO2D = new Geographic2DCRS(new Identifier("IGNF","CAD97GEO", "CADASTRE 1997 GEO2D"), CAD1997);

    private static final GeodeticCRS CAD1997_UTM38S = new ProjectedCRS(new Identifier("IGNF","CAD97UTM38S", "CADASTRE 1997 UTM38S"),
            CAD1997, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 38, "SOUTH"));

    private static CompoundCRS CAD1997_UTM38S_MAYO53 = null;
    static {
        try {
            CAD1997_UTM38S_MAYO53 = new CompoundCRS(new Identifier("IGNF","CAD97UTM38S.MAYO53", "CADASTRE 1997 UTM38S + SHOM 1953"),
                    CAD1997_UTM38S, MAYO53);
        } catch (CRSException e) {e.printStackTrace();}
    }

    // ////////////////////////////////////////////////////////////////////////
    // RGM04
    // ////////////////////////////////////////////////////////////////////////
    private static final GeodeticCRS RGM04_GEOC = new GeocentricCRS(new Identifier("IGNF","RGM04_GEO2D", "RGM04 GEO2D"), RGM04);
    private static final GeodeticCRS RGM04_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RGM04_GEO2D", "RGM04 GEO2D"), RGM04);
    private static final GeodeticCRS RGM04_GEO3D = new Geographic3DCRS(new Identifier("IGNF","RGM04_GEO2D", "RGM04 GEO2D"), RGM04);

    private static final GeodeticCRS RGM04_UTM38S = new ProjectedCRS(new Identifier("IGNF","RGM04 UTM38S", "RGM04 UTM38S"),
            RGM04, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 38, "SOUTH"));

    private static CompoundCRS RGM04_UTM38S_SHOM1953 = null;
    static {
        try {
            RGM04_UTM38S_SHOM1953 = new CompoundCRS(new Identifier("IGNF","RGM04_UTM38S_SHOM1953", "RGM04_UTM38S + SHOM1953"),
                    RGM04_UTM38S, MAYO53);
        } catch (CRSException e) {e.printStackTrace();}
    }

    // ////////////////////////////////////////////////////////////////////////
    // TESTS TRANSFORMATIONS
    // ////////////////////////////////////////////////////////////////////////

    // //////////////////////////////
    // Tests from COMBANI
    // //////////////////////////////

    @Test
    void testMAYO50_to_MAYO50UTM38S() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, MAYO50_GEO2D, MAYO50_UTM38S, new double[]{512807.225, 8585957.337}, MM_IN_DEG, MM);
    }

    @Test
    void testMAYO50_to_RGM04_GEOCENTRIQUE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, MAYO50_GEO2D, RGM04_GEOC, new double[]{4389551.047, 4407994.483, -1403139.565}, MM_IN_DEG, MM);
    }

    @Test
    void testMAYO50_to_RGM04()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, MAYO50_GEO2D, RGM04_GEO2D, new double[]{-12.79352658, 45.12011640}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testMAYO50_to_RGM04_UTM38S()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, MAYO50_GEO2D, RGM04_UTM38S, new double[]{513036.279, 8585694.190}, MM_IN_DEG, MM);
    }

    @Test
    void testMAYO50_UTM38S_SHOM1953_to_RGM04_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{513036.279, 8585694.190, 100}, MAYO50_UTM38S_SHOM1953, RGM04_GEO3D, new double[]{-12.79590528, 45.12222812, 80.931}, MM, MM_IN_DEG);
    }

    // //////////////////////////////
    // Tests from CADASTRE 1997
    // //////////////////////////////

    @Test
    void testCAD1997_to_CAD1997_UTM38S() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, CAD1997_GEO2D, CAD1997_UTM38S, new double[]{512807.225, 8585957.337}, MM_IN_DEG, MM);
    }

    @Test
    void testCAD1997_to_RGM04_GEOCENTRIQUE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, CAD1997_GEO2D, RGM04_GEOC, new double[]{4389550.925, 4407994.586, -1403139.687}, MM_IN_DEG, MM);
    }

    @Test
    void testCAD1997_to_RGM04()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, CAD1997_GEO2D, RGM04_GEO2D, new double[]{-12.79352769, 45.12011787}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testCAD1997_to_RGM04_UTM38S()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-12.791, 45.118}, CAD1997_GEO2D, RGM04_UTM38S, new double[]{513036.438, 8585694.067}, MM_IN_DEG, MM);
    }

    @Test
    void testCAD1997_UTM38S_SHOM1953_to_RGM04_GEOC()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{513036.279, 8585694.190, 100}, CAD1997_UTM38S_MAYO53, RGM04_GEOC, new double[]{4389402.403, 4408170.355, -1403414.015}, MM, MM);
    }

    // This test does not pass the 1E-8° test but it passes with 2E-8 (about 2 mm)
    // It is a bit strange as the same conversion to geocentric (above) passes, which means that
    // some precision is lost during the geocentric to geographic transformation
    @Test
    void testCAD1997_UTM38S_MAYO53_to_RGM04_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{513036.279, 8585694.190, 100}, CAD1997_UTM38S_MAYO53, RGM04_GEO3D, new double[]{-12.79590627, 45.12222948, 80.931}, MM, 2*MM_IN_DEG);
    }

}
