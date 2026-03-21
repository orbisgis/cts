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
import org.cts.op.transformation.SevenParameterTransformation;
import org.junit.jupiter.api.Test;


/**
 * Tests for transformations available through Circe Antilles Guyane v 4.2
 * WARNING : to test transformation reversibility based on a geocentric 7-parameter transformation,
 * we have to use 3D target CRS.
 * If we use a 2D target CRS, we obtain a correct result for the direct transformation,
 * but the 3D -> 2D final operation will project the z coordinates onto the ellipsoid
 * surface, and this operation is not reversible.
 * @author Michaël Michaud
 */
class CirceAntilleGuyaneComplianceTest extends BaseCoordinateTransformTest {

    private static final GeographicExtent GUYANE = new GeographicExtent("Guyane Française", 2.05, 5.95, -54.95, -51.05);

    // ////////////////////////////////////////////////////////////////////////
    // DATUM
    // ////////////////////////////////////////////////////////////////////////
    private static final GeodeticDatum FORT_DESAIX = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","FORT_DESAIX","FORT_DESAIX"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    127.744,  547.069,  118.359,
                     -3.1116,   4.9509,  -0.8837,
                     14.1012), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum RRAF_MTQ = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RRAF_MTQ","RRAF_MTQ"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                     0.7696,  -0.8692, -12.0631,
                    -0.32511, -0.21041, -0.02390,
                     0.2829), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum STE_ANNE = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","STE_ANNE","STE_ANNE"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -471.060,   -3.212,  -305.843,
                       0.4752,  -0.9978,    0.2068,
                       2.1353), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum RRAF_GUA = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RRAF_GUA","RRAF_GUA"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                     1.2239,   2.4156,  -1.7598,
                     0.03800, -0.16101, -0.04925,
                     0.2387 ), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum FORT_MARIGOT = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","FORT_MARIGOT","FORT_MARIGOT"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    151.613,  253.832, -429.084 ,
                    -0.0506,   0.0958,  -0.5974,
                    -0.3971), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum RRAF_SBSM = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RRAF_SBSM","RRAF_SBSM"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                    14.6642,   5.2493,   0.1981,
                    -0.06838,  0.09141, -0.58131,
                    -0.4067  ), GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum CSG1967 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","REG4070001","CSG 1967"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -193.066,  236.993, 105.447,
                       0.4814,  -0.8074,  0.1276,
                       1.5649), GUYANE, null, null);

    private static final GeodeticDatum RGAF09 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGAF09","RGAF09"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, GeographicExtent.WORLD,null,null);

    private static final GeodeticDatum RGFG95 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGFG95","RGFG95"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, GeographicExtent.WORLD,null,null);


    // ////////////////////////////////////////////////////////////////////////
    // VERTICAL DATUM
    // ////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////
    // CRS
    // ////////////////////////////////////////////////////////////////////////

    // Martinique
    private static final GeodeticCRS FORT_DESAIX_GEO2D = new Geographic2DCRS(new Identifier("IGNF","Fort Desaix Geo2D", "Fort Desaix Geo2D"), FORT_DESAIX);

    private static final GeodeticCRS FORT_DESAIX_UTM20 = new ProjectedCRS(new Identifier("IGNF","Fort Desaix UTM 20N", "Fort Desaix UTM 20N"),
            FORT_DESAIX, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 20, "NORTH"));

    private static final GeodeticCRS RRAF_MTQ_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RRAF MTQ Geo2D", "RRAF MTQ Geo2D"), RRAF_MTQ);

    static final GeodeticCRS RRAF_MTQ_GEO3D = new Geographic3DCRS(new Identifier("IGNF","RRAF MTQ Geo3D", "RRAF MTQ Geo3D"), RRAF_MTQ);

    private static final GeodeticCRS RRAF_MTQ_UTM20 = new ProjectedCRS(new Identifier("IGNF","RRAF (MTQ) UTM 20N", "RRAF (MTQ) UTM 20N"),
            RRAF_MTQ, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 20, "NORTH"));

    private static CompoundCRS RRAF_MTQ_UTM20_HE = null;
    static {
        try {RRAF_MTQ_UTM20_HE = new CompoundCRS(new Identifier("IGNF","RRAF (MTQ) UTM 20N + He", "RRAF (MTQ) UTM 20N + He"),
                RRAF_MTQ_UTM20, new VerticalCRS(new Identifier("","",""), VerticalDatum.GRS80VD));
        } catch(CRSException e) {e.printStackTrace();}
    }
    private static CompoundCRS FORT_DESAIX_IGN1987 = null;
    static {
        try {FORT_DESAIX_IGN1987 = new CompoundCRS(new Identifier("IGNF","FORT DESAIX + IGN1987 MTQ", "FORT DESAIX + IGN1987 MTQ"),
                FORT_DESAIX_GEO2D, new VerticalCRS(new Identifier("IGNF","IGN1987_MART","IGN1987_MART"), VerticalDatum.IGN87MART));
        } catch(CRSException e) {e.printStackTrace();}
    }


    // Guadeloupe
    private static final GeodeticCRS STE_ANNE_GEO2D = new Geographic2DCRS(new Identifier("IGNF","Sainte-Anne Geo2D", "Sainte-Anne Geo2D"), STE_ANNE);

    private static final GeodeticCRS STE_ANNE_UTM20 = new ProjectedCRS(new Identifier("IGNF","STE_ANNE UTM 20N", "STE_ANNE UTM 20N"),
            STE_ANNE, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 20, "NORTH"));

    private static final GeodeticCRS RRAF_GUA_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RRAF GUA Geo2D", "RRAF GUA Geo2D"), RRAF_GUA);

    static final GeodeticCRS RRAF_GUA_GEO3D = new Geographic2DCRS(new Identifier("IGNF","RRAF GUA Geo3D", "RRAF GUA Geo3D"), RRAF_GUA);

    static final GeodeticCRS RRAF_GUA_UTM20 = new ProjectedCRS(new Identifier("IGNF","RRAF (GUA) UTM 20N", "RRAF (GUA) UTM 20N"),
            RRAF_GUA, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 20, "NORTH"));

    private static CompoundCRS RRAF_GUA_UTM20_HE = null;
    static {
        try {RRAF_GUA_UTM20_HE = new CompoundCRS(new Identifier("IGNF","RRAF (GUA) UTM 20N + He", "RRAF (GUA) UTM 20N + He"),
                RRAF_MTQ_UTM20, new VerticalCRS(new Identifier("","",""), VerticalDatum.GRS80VD));
        } catch(CRSException e) {e.printStackTrace();}
    }

    private static CompoundCRS STE_ANNE_IGN1988_GTBT = null;
    static {
        try {STE_ANNE_IGN1988_GTBT = new CompoundCRS(new Identifier("IGNF","STE_ANNE + IGN1988 GTBT", "STE_ANNE + IGN1988 GTBT"),
                STE_ANNE_GEO2D, new VerticalCRS(new Identifier("IGNF","IGN1988_GBT","IGN1988_GBT"), VerticalDatum.IGN88GTBT));
        } catch(CRSException e) {e.printStackTrace();}
    }


    // ST-BARTHELEMY / ST-MARTIN
    private static final GeodeticCRS FORT_MARIGOT_GEO2D = new Geographic2DCRS(new Identifier("IGNF","Fort Marigot Geo2D", "Fort Marigot Geo2D"), FORT_MARIGOT);

    private static final GeodeticCRS FORT_MARIGOT_UTM20 = new ProjectedCRS(new Identifier("IGNF","FORT_MARIGOT UTM 20N", "FORT_MARIGOT UTM 20N"),
            FORT_MARIGOT, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 20, "NORTH"));

    private static final GeodeticCRS RRAF_SBSM_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RRAF SBSM Geo2D", "RRAF SBSM Geo2D"), RRAF_SBSM);

    static final GeodeticCRS RRAF_SBSM_UTM20 = new ProjectedCRS(new Identifier("IGNF","RRAF_SBSM UTM 20N", "RRAF_SBSM UTM 20N"),
            RRAF_SBSM, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 20, "NORTH"));

    private static CompoundCRS FORT_MARIGOT_IGN1988_SB = null;
    static {
        try {FORT_MARIGOT_IGN1988_SB = new CompoundCRS(new Identifier("IGNF","FORT_MARIGOT + IGN1988 GTBT", "FORT_MARIGOT + IGN1988 SB"),
                FORT_MARIGOT_GEO2D, new VerticalCRS(new Identifier("IGNF","IGN1988_SB","IGN1988_SB"), VerticalDatum.IGN88SB));
        } catch(CRSException e) {e.printStackTrace();}
    }


    // GUYANE
    private static final GeodeticCRS CSG1967_GEO2D = new Geographic2DCRS(new Identifier("IGNF","CSG67G", "CSG 1967 Geo2D"), CSG1967);

    private static final GeodeticCRS CSG1967_UTM22 = new ProjectedCRS(new Identifier("IGNF","CSG1967 UTM 22N", "CSG1967 UTM 22N"),
            CSG1967, UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 22, "NORTH"));

    static final GeodeticCRS RGFG95_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RGFG95 Geo2D", "RGFG95 Geo2D"), RGFG95);

    private static final GeodeticCRS RGFG95_GEO3D = new Geographic3DCRS(new Identifier("IGNF","RGFG95 Geo3D", "RGFG95 Geo3D"), RGFG95);

    private static final GeodeticCRS RGFG95_UTM22 = new ProjectedCRS(new Identifier("IGNF","RGFG95 UTM 22N", "RGFG95 UTM 22N"),
            RGFG95, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 22, "NORTH"));

    private static GeodeticCRS RGFG95_UTM22_HE = null;
    static {
        try {RGFG95_UTM22_HE = new CompoundCRS(new Identifier("IGNF", "RGFG95_UTM22 + He", "RGFG95_UTM22 + He"),
                RGFG95_UTM22, new VerticalCRS(new Identifier("","",""), VerticalDatum.GRS80VD));
        } catch(CRSException e) {e.printStackTrace();}
    }

    private static GeodeticCRS CSG1967_GUYA77 = null;
    static {
        try {CSG1967_GUYA77 = new CompoundCRS(new Identifier("IGNF", "CSG1967 + NGG1977", "CSG1967 + NGG1977"),
                CSG1967_GEO2D, new VerticalCRS(new Identifier("IGNF","GUYA77","NGG 1977"), VerticalDatum.NGG77GUY));
        } catch(CRSException e) {e.printStackTrace();}
    }


    // ANTILLE-GUYANE
    static final GeodeticCRS RGAF09_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RGAF09 Geo2D", "RGAF09 Geo2D"), RGAF09);

    private static final GeodeticCRS RGAF09_GEO3D = new Geographic3DCRS(new Identifier("IGNF","RGAF09 Geo3D", "RGAF09 Geo3D"), RGAF09);

    private static final GeodeticCRS RGAF09_UTM20 = new ProjectedCRS(new Identifier("IGNF","RGAF09 UTM 20N", "RGAF09 UTM 20N"),
            RGAF09, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 20, "NORTH"));

    private static CompoundCRS RGAF09_UTM20_HE = null;
    static {
        try {RGAF09_UTM20_HE = new CompoundCRS(new Identifier("IGNF","RGAF09 UTM 20N + He", "RGAF09 UTM 20N + He"),
                RGAF09_UTM20, new VerticalCRS(new Identifier("","",""), VerticalDatum.GRS80VD));
        } catch(CRSException e) {e.printStackTrace();}
    }

    static final GeodeticCRS RGAF09_UTM22 = new ProjectedCRS(new Identifier("IGNF","RGAF09 UTM 22N", "RGAF09 UTM 22N"),
            RGAF09, UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 22, "NORTH"));


    // ////////////////////////////////////////////////////////////////////////
    // TESTS
    // ////////////////////////////////////////////////////////////////////////

    // //////////////////////////////
    // Tests from FORT DESAIX
    // //////////////////////////////

    @Test
    void testFORT_DESAIX_to_FORT_DESAIX()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{14.5, -61.0}, FORT_DESAIX_GEO2D, FORT_DESAIX_UTM20, new double[]{715553.632, 1603986.075}, MM_IN_DEG, MM);
    }

    @Test
    void testFORT_DESAIX_to_RGAF09_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{14.5, -61.0}, FORT_DESAIX_GEO2D, RGAF09_GEO3D, new double[]{14.50166751, -60.99633191, 0}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testFORT_DESAIX_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{14.5, -61.0}, FORT_DESAIX_GEO2D, RGAF09_UTM20_HE, new double[]{715938.802, 1604155.162, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testWGS84_MTQ_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{14.5, -61.0}, RRAF_MTQ_GEO2D, RGAF09_UTM20_HE, new double[]{715544.290, 1603967.149, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testFORT_DESAIX_IGN1987_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test3D(new double[]{14.5, -61.0, 100}, FORT_DESAIX_IGN1987, RGAF09_UTM20_HE, new double[]{715938.796, 1604155.159, 61.775}, MM_IN_DEG, MM);
    }

    // //////////////////////////////
    // Tests from FORT STE_ANNE
    // //////////////////////////////

    @Test
    void testSTE_ANNE_to_STE_ANNE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{16.0, -61.5}, STE_ANNE_GEO2D, STE_ANNE_UTM20, new double[]{660509.501, 1769535.756}, MM_IN_DEG, MM);
    }

    @Test
    void testSTE_ANNE_to_RGAF09_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{16.0, -61.5}, STE_ANNE_GEO2D, RGAF09_GEO3D, new double[]{15.99747584, -61.50391185, 0}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testSTE_ANNE_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{16.0, -61.5}, STE_ANNE_GEO2D, RGAF09_UTM20_HE, new double[]{660086.365, 1769232.273, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testWGS84_GUA_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{16.0, -61.5}, RRAF_GUA_GEO2D, RGAF09_UTM20_HE, new double[]{660502.424, 1769514.648, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testSTE_ANNE_IGN1988_GTBT_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test3D(new double[]{16, -61.5, 100}, STE_ANNE_IGN1988_GTBT, RGAF09_UTM20_HE, new double[]{660086.371, 1769232.277, 59.769}, MM_IN_DEG, MM);
    }

    // //////////////////////////////
    // Tests from FORT MARIGOT (SBSM)
    // //////////////////////////////

    @Test
    void testFORT_MARIGOT_to_FORT_MARIGOT()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{18.0, -62.8}, FORT_MARIGOT_GEO2D, FORT_MARIGOT_UTM20, new double[]{521173.421, 1990221.324}, MM_IN_DEG, MM);
    }

    @Test
    void testFORT_MARIGOT_to_RGAF09_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{18.0, -62.8}, FORT_MARIGOT_GEO2D, RGAF09_GEO3D, new double[]{17.99626623, -62.79778742, 0}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testFORT_MARIGOT_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{18.0, -62.8}, FORT_MARIGOT_GEO2D, RGAF09_UTM20_HE, new double[]{521407.240, 1989784.123, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testWGS84_SBSM_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{18.0, -62.8}, RRAF_SBSM_GEO2D, RGAF09_UTM20_HE, new double[]{521171.969, 1990197.114, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testFORT_MARIGOT_IGN88SB_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test3D(new double[]{18.0, -62.8, 100}, FORT_MARIGOT_IGN1988_SB, RGAF09_UTM20_HE, new double[]{521407.236, 1989784.130, 56.794}, MM_IN_DEG, MM);
    }

    // //////////////////////////////
    // Tests from GUYANE
    // //////////////////////////////

    @Test
    void testCSG1967_to_CSG1967()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.9, -52.3}, CSG1967_GEO2D, CSG1967_UTM22, new double[]{355849.751, 541756.113}, MM_IN_DEG, MM);
    }

    @Test
    void testCSG1967_to_RGFG95_GEO3D()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.9, -52.3}, CSG1967_GEO2D, RGFG95_GEO3D, new double[]{4.90107745, -52.30005723, 0}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    void testCSG1967_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.9, -52.3}, CSG1967_GEO2D, RGFG95_UTM22_HE, new double[]{355849.323, 541869.306, 0}, MM_IN_DEG, MM);
    }

    @Test
    void testCSG1967_GUYA77_to_RGAF09_UTM20_HE()  throws IllegalCoordinateException, CoordinateOperationException {
        test3D(new double[]{4.9, -52.3, 100}, CSG1967_GUYA77, RGFG95_UTM22_HE, new double[]{355849.323, 541869.304, 65.240}, MM_IN_DEG, MM);
    }

}
