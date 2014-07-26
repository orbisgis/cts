package org.cts.op;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.crs.*;
import org.cts.cs.GeographicExtent;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.projection.LambertConicConformal2SP;
import org.cts.op.projection.Projection;
import org.cts.op.projection.UniversalTransverseMercator;
import org.cts.op.transformation.GeocentricTranslation;
import org.cts.op.transformation.SevenParameterTransformation;
import org.cts.util.AngleFormat;
import org.junit.Test;

/**
 * Cf CirceNC
 * et
 * http://www.dittt.gouv.nc/portal/page/portal/dittt/geodesie_et_nivellement/referentiels_geodesiques
 * @TODO the small differences between CTS and CIRCE NC are difficult to analyse without a
 * better understanding of algorithm and parameters used in Circe NC
 */
public class CirceNouvelleCaledonieTest extends BaseCoordinateTransformTest {

    private static final double MM = 0.001;
    private static final double MM_IN_DEG = 0.00000001;

    static final GeographicExtent LIFOU  = new GeographicExtent("LIFOU (ILE DE LA LOYAUTE", -21.22, -20.65, 166.96, 167.51);
    static final GeographicExtent MARE   = new GeographicExtent("MARE (ILE DE LA LOYAUTE)", -21.69, -21.29, 167.69, 168.18);
    static final GeographicExtent OUVEA  = new GeographicExtent("OUVEA (ILE DE LA LOYAUTE)", -20.78, -20.34, 166.11, 166.71);
    static final GeographicExtent NOUVELLE_CALEDONIE = new GeographicExtent("Nouvelle Calédonie", -26.65, -14.60, 156.10, 174.50);
    static final GeographicExtent GRANDE_TERRE = new GeographicExtent("Grande Terre", -22.75, -19.50, 163.50, 167.20);
    static final GeographicExtent ILE_DES_PINS = new GeographicExtent("Ile des Pins", -22.80, -22.44, 167.29, 167.62);
    static final GeographicExtent BELEP  = new GeographicExtent("BELEP", -19.74, -19.68, 163.62, 163.68);
    static final GeographicExtent NOUMEA = new GeographicExtent("Nouméa", -22.36, -22.14, 166.36, 166.54);



    private static Projection UTM58S_1924 = UniversalTransverseMercator.createUTM(Ellipsoid.INTERNATIONAL1924, 58, "SOUTH");
    private static Projection UTM58S_WGS84 = UniversalTransverseMercator.createUTM(Ellipsoid.WGS84, 58, "SOUTH");
    private static Projection UTM57S_GRS80 = UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 57, "SOUTH");
    private static Projection UTM58S_GRS80 = UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 58, "SOUTH");
    private static Projection UTM59S_GRS80 = UniversalTransverseMercator.createUTM(Ellipsoid.GRS80, 59, "SOUTH");

    private static Projection LambertNoumea2 = LambertConicConformal2SP.createLCC2SP(Ellipsoid.INTERNATIONAL1924,
            AngleFormat.parseAngle("22°16'11\"S"),
            AngleFormat.parseAngle("22°14'41\"S"),
            AngleFormat.parseAngle("22°17'41\"S"),
            AngleFormat.parseAngle("166°26'33\"E"), 8.313, -2.354);
    private static Projection Lambert_NC = LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80,
            AngleFormat.parseAngle("21°30'S"),
            AngleFormat.parseAngle("20°40'S"),
            AngleFormat.parseAngle("22°20'S"),
            AngleFormat.parseAngle("166°E"), 400000, 300000);

    // ------------------------------------------------------------------------
    // Systems based on IGN 72 GRANDE TERRE
    // ------------------------------------------------------------------------
    static GeodeticDatum IGN72_GRANDE_TERRE = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","IGN72_GRANDE_TERRE","IGN 72 Grande Terre"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    97.2970, -263.2430, 310.8790, -1.599879, 0.838730, 3.140947, 13.326,
                    SevenParameterTransformation.createBursaWolfTransformation(
                            -97.2910, 263.2440, -310.8730, 1.599892, -0.838705, -3.140953, -13.325849)), GRANDE_TERRE, null, null);

            //SevenParameterTransformation.createSevenParameterTransformation(
            //        97.2950, -263.2470, 310.8820, -1.599871, 0.838616, 3.140889, 13.325904,
            //        SevenParameterTransformation.POSITION_VECTOR, SevenParameterTransformation.NOT_LINEARIZED), GRANDE_TERRE, null, null);

            //new GeocentricTranslation(-11.6400, -348.6000, 291.6800), GRANDE_TERRE, null, null);

    static GeodeticCRS IGN72_GRANDE_TERRE_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","IGN72_GEO","IGN 72 Grande Terre Géographique 2D"), IGN72_GRANDE_TERRE);
    static GeodeticCRS IGN72_GRANDE_TERRE_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","IGN72_GEO3D","IGN 72 Grande Terre Géographique 3D"), IGN72_GRANDE_TERRE);
    static GeodeticCRS IGN72_GRANDE_TERRE_UTM = new ProjectedCRS(
            new Identifier("IGNF","IGN72_UTM58S","IGN 72 Grande Terre UTM 58S"), IGN72_GRANDE_TERRE, UTM58S_1924);

    @Test
    public void testIGN72_GRANDE_TERRE_GEO2D_To_UTM() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-20, 165}, IGN72_GRANDE_TERRE_GEO2D, IGN72_GRANDE_TERRE_UTM, new double[]{500000.000, 7788490.928}, MM_IN_DEG, MM);
    }

    // Matches ciece results with the seven parameters described in the help, not with those included in dataNC.txt
    @Test
    public void testIGN72_GRANDE_TERRE_GEO2D_To_RGNC_XYZ() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-20, 165}, IGN72_GRANDE_TERRE_GEO2D, RGNC1991_GEOC, new double[]{-5791783.050, 1551552.713, -2167430.438}, MM_IN_DEG, MM);
    }

    //@TODO : investigate why we have 2 to 3 mm difference with circe (also geocentric and projected transformations are OK)
    @Test
    public void testIGN72_GRANDE_TERRE_GEO2D_To_RGNC_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-20, 165}, IGN72_GRANDE_TERRE_GEO2D, RGNC1991_GEO2D, new double[]{-19.99722061, 165.00323863}, MM_IN_DEG*3, MM_IN_DEG*3);
    }

    //@TODO why does it fail ?
    //@Test
    //public void testIGN72_GRANDE_TERRE_GEO2D_To_RGNC_UTM() throws IllegalCoordinateException, CoordinateOperationException {
    //    test(new double[]{-20, 165}, IGN72_GRANDE_TERRE_GEO2D, RGNC1991_UTM58S, new double[]{500338.783, 7788826.257}, MM_IN_DEG, MM);
    //}

    //@TODO why does it fail ?
    //@Test
    //public void testIGN72_GRANDE_TERRE_GEO2D_To_RGNC_LAMBERT_NC() throws IllegalCoordinateException, CoordinateOperationException {
    //    test(new double[]{-20, 165}, IGN72_GRANDE_TERRE_GEO2D, RGNC1991_LAMBERT_NC, new double[]{295666.080, 466047.279}, MM_IN_DEG, MM);
    //}

    // ------------------------------------------------------------------------
    // Systems based on IGN 56 LIFOU
    // ------------------------------------------------------------------------
    static GeodeticDatum IGN56_LIFOU = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","IGN56_LIFOU","IGN 56 Lifou"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    137.092, 131.660, 91.475, -1.943600, -11.599324, -4.332090, -7.482352), LIFOU, null, null);
            //SevenParameterTransformation.createBursaWolfTransformation(
            //        -137.092, -131.660, -91.475, 1.943600, 11.599324, 4.332090, 7.482352).inverse0063(), LIFOU, null, null);

    static GeodeticCRS IGN56_LIFOU_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","IGN56_LIFOU_GEO2D","IGN56_LIFOU Géographique 2D"), IGN56_LIFOU);
    static GeodeticCRS IGN56_LIFOU_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","IGN56_LIFOU_GEO3D","IGN56_LIFOU Géographique 3D"), IGN56_LIFOU);
    static GeodeticCRS IGN56_LIFOU_UTM = new ProjectedCRS(
            new Identifier("IGNF","IGN56_LIFOU_UTM58S","IGN56_LIFOU UTM 58S"), IGN56_LIFOU, UTM58S_1924);

    @Test
    public void testIGN56_LIFOU_GEO2D_To_UTM() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-21, 167}, IGN56_LIFOU_GEO2D, IGN56_LIFOU_UTM, new double[]{707897.776, 7676522.112}, MM_IN_DEG, MM);
    }

    //@TODO why does it fail ?
    //Note : Matches Circe results with the seven parameters described in the help,
    //but not with those included in dataNC.txt
    //@Test
    //public void testIGN56_LIFOU_GEO2D_To_RGNC_XYZ() throws IllegalCoordinateException, CoordinateOperationException {
    //    test3D(new double[]{-21, 167, 0}, IGN56_LIFOU_GEO3D, RGNC1991_GEOC, new double[]{-5804288.561, 1340325.407, -2271654.423}, MM_IN_DEG, MM);
    //}

    //@TODO : investigate why we have 2 to 3 mm difference with circe (also geocentric and projected transformations are OK)
    @Test
    public void testIGN56_LIFOU_GEO2D_To_RGNC_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-21, 167}, IGN56_LIFOU_GEO2D, RGNC1991_GEO2D, new double[]{-21.00229264, 166.99719031}, MM_IN_DEG*3, MM_IN_DEG*3);
    }

    //@TODO why does it fail ?
    //@Test
    //public void testIGN56_LIFOU_GEO2D_To_RGNC_UTM() throws IllegalCoordinateException, CoordinateOperationException {
    //    test(new double[]{-21, 167}, IGN56_LIFOU_GEO2D, RGNC1991_UTM58S, new double[]{707593.903, 7676301.515}, MM_IN_DEG, MM);
    //}

    //@TODO why does it fail ?
    //@Test
    //public void testIGN56_LIFOU_GEO2D_To_RGNC_LAMBERT_NC() throws IllegalCoordinateException, CoordinateOperationException {
    //    test(new double[]{-21, 167}, IGN56_LIFOU_GEO2D, RGNC1991_LAMBERT_NC, new double[]{503668.985, 354770.563}, MM_IN_DEG, MM);
    //}

    // ------------------------------------------------------------------------
    // Systems based on IGN 53 Maré
    // ------------------------------------------------------------------------
    static GeodeticDatum IGN53_MARE = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","IGN53_MARE","IGN 53 Maré"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -408.809, 366.856, -412.987, 1.884178, -0.530793, 2.165473, -121.099322), MARE, null, null);

    static GeodeticCRS IGN53_MARE_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","IGN53_MARE_GEO2D","IGN53_MARE Géographique 2D"), IGN53_MARE);
    static GeodeticCRS IGN53_MARE_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","IGN53_MARE_GEO3D","IGN53_MARE Géographique 3D"), IGN53_MARE);
    static GeodeticCRS IGN53_MARE_UTM = new ProjectedCRS(
            new Identifier("IGNF","IGN53_MARE_UTM58S","IGN53_MARE UTM 58S"), IGN53_MARE, UTM58S_1924);

    // ------------------------------------------------------------------------
    // Systems based on ST87_OUVEA
    // ------------------------------------------------------------------------
    static GeodeticDatum ST87_OUVEA = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","ST87_OUVEA","ST87 Ouvéa"), PrimeMeridian.GREENWICH, Ellipsoid.WGS84,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -122.383, -188.696, 103.344, 3.510741, -4.966788, -5.704560, 4.479814), OUVEA, null, null);

    static GeodeticCRS ST87_OUVEA_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","ST87_OUVEA_GEO2D","ST87 Ouvéa Géographique 2D"), ST87_OUVEA);
    static GeodeticCRS ST87_OUVEA_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","ST87_OUVEA_GEO3D","ST87 Ouvéa Géographique 3D"), ST87_OUVEA);
    static GeodeticCRS ST87_OUVEA_UTM = new ProjectedCRS(
            new Identifier("IGNF","ST87_OUVEA_UTM58S","ST87 Ouvéa UTM 58S"), ST87_OUVEA, UTM58S_WGS84);

    // ------------------------------------------------------------------------
    // Systems based on ST84_ILE_DES_PINS
    // ------------------------------------------------------------------------
    static GeodeticDatum ST84_ILE_DES_PINS = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","ST84_ILE_DES_PINS","ST84 Ile des Pins"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    244.416, 85.339, 168.114, -8.935342, 7.752301, 12.595307, 14.267971), ILE_DES_PINS, null, null);

    static GeodeticCRS ST84_ILE_DES_PINS_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","ST84_ILE_DES_PINS_GEO2D","ST84 Ile des Pins Géographique 2D"), ST84_ILE_DES_PINS);
    static GeodeticCRS ST84_ILE_DES_PINS_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","ST84_ILE_DES_PINS_GEO3D","ST84 Ile des Pins Géographique 3D"), ST84_ILE_DES_PINS);
    static GeodeticCRS ST84_ILE_DES_PINS_UTM = new ProjectedCRS(
            new Identifier("IGNF","ST84_ILE_DES_PINS_UTM58S","ST84 Ile des Pins UTM 58S"), ST84_ILE_DES_PINS, UTM58S_1924);

    // ------------------------------------------------------------------------
    // Systems based on ST71_BELEP
    // ------------------------------------------------------------------------
    static GeodeticDatum ST71_BELEP = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","ST71_BELEP","ST71_BELEP"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -480.260, -438.320, -643.429, 16.311867, 20.172100, -4.034890, -111.700181), BELEP, null, null);

    static GeodeticCRS ST71_BELEP_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","ST71_BELEP_GEO2D","ST71_BELEP Géographique 2D"), ST71_BELEP);
    static GeodeticCRS ST71_BELEP_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","ST71_BELEP_GEO3D","ST71_BELEP Géographique 3D"), ST71_BELEP);
    static GeodeticCRS ST71_BELEP_UTM = new ProjectedCRS(
            new Identifier("IGNF","ST71_BELEP_UTM58S","ST71_BELEP UTM 58S"), ST71_BELEP, UTM58S_1924);

    // ------------------------------------------------------------------------
    // Systems based on NEA74 NOUMEA
    // ------------------------------------------------------------------------
    static GeodeticDatum NOUMEA74 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","NOUMEA74","Nouméa74"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            SevenParameterTransformation.createBursaWolfTransformation(
                    -166.207, -154.777, 254.831, -37.544387, 7.701146, -10.202481, -30.859839), NOUMEA, null, null);

    static GeodeticCRS NOUMEA74_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","NOUMEA74_GEO2D","Nouméa74 Géographique 2D"), NOUMEA74);
    static GeodeticCRS NOUMEA74_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","NOUMEA74_GEO3D","Nouméa74 Géographique 3D"), NOUMEA74);
    static GeodeticCRS NOUMEA74_LAMBERT = new ProjectedCRS(
            new Identifier("IGNF","NOUMEA74_LAMBERT","Nouméa74 Lambert Nouméa 2"), NOUMEA74, LambertNoumea2);

    // ------------------------------------------------------------------------
    // Systems based on RGNC1991
    // ------------------------------------------------------------------------
    static GeodeticDatum RGNC1991 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGNC1991","RGNC1991"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, NOUVELLE_CALEDONIE, null, null);

    static GeodeticCRS RGNC1991_GEOC = new GeocentricCRS(
            new Identifier("IGNF","RGNC1991","RGNC1991 Geocentric"), RGNC1991);
    static GeodeticCRS RGNC1991_GEO2D = new Geographic2DCRS(
            new Identifier("IGNF","RGNC1991_GEO2D","RGNC1991 Géographique 2D"), RGNC1991);
    static GeodeticCRS RGNC1991_GEO3D = new Geographic3DCRS(
            new Identifier("IGNF","RGNC1991_GEO3D","RGNC1991 Géographique 3D"), RGNC1991);
    static GeodeticCRS RGNC1991_LAMBERT_NC = new ProjectedCRS(
            new Identifier("IGNF","RGNC1991_LAMBERT_NC","RGNC1991 Lambert Nouvelle Calédonie"), RGNC1991, Lambert_NC);
    static GeodeticCRS RGNC1991_UTM57S = new ProjectedCRS(
            new Identifier("IGNF","RGNC1991_UTM57S","RGNC1991 UTM 57S"), RGNC1991, UTM57S_GRS80);
    static GeodeticCRS RGNC1991_UTM58S = new ProjectedCRS(
            new Identifier("IGNF","RGNC1991_UTM58S","RGNC1991 UTM 58S"), RGNC1991, UTM58S_GRS80);
    static GeodeticCRS RGNC1991_UTM59S = new ProjectedCRS(
            new Identifier("IGNF","RGNC1991_UTM59S","RGNC1991 UTM 59S"), RGNC1991, UTM59S_GRS80);
    //static {
    //    RGNC1991.addGeocentricTransformation(IGN72_GRANDE_TERRE,
    //            SevenParameterTransformation.createBursaWolfTransformation(
    //                    -97.2910, 263.2440, -310.8730, 1.599892, -0.838705, -3.140953, -13.325849));
    //}



}
