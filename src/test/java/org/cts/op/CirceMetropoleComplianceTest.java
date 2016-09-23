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
import org.cts.Parameter;
import org.cts.crs.*;
import org.cts.cs.GeographicExtent;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.*;
import org.cts.op.transformation.FrenchGeocentricNTF2RGF;
import org.cts.op.transformation.GeocentricTranslation;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.cts.util.AngleFormat;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


/**
 * In This test, we build CRS as they are defined in CIRCE to compare results
 * @author Michaël Michaud
 */
public class CirceMetropoleComplianceTest extends BaseCoordinateTransformTest {

    private static final double MM = 0.001;
    private static final double MM_IN_DEG = 0.00000001;

    // ------------------------------------------------------------------------
    // Datum
    // ------------------------------------------------------------------------
    static GeodeticDatum NTF_G = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","NTF_G","NTF"), PrimeMeridian.GREENWICH, Ellipsoid.CLARKE1880IGN,
            new GeocentricTranslation(-168, -60, 320, 1), GeographicExtent.WORLD, null, null);
    static GeodeticDatum NTF_P = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","NTF_P","NTF_P"), PrimeMeridian.PARIS, Ellipsoid.CLARKE1880IGN,
            new GeocentricTranslation(-168, -60, 320, 1), GeographicExtent.WORLD, null, null);
    static GeodeticDatum ED50 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","ED50","ED50"),     PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            new GeocentricTranslation(-84, -97, -117, 1), GeographicExtent.WORLD, null, null);
    //static GeodeticDatum WGS84 = GeodeticDatum.createGeodeticDatum(
    //        new Identifier("EPSG","6326","WGS84"),  PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
    //        Identity.IDENTITY, GeographicExtent.WORLD,null,null);
    static GeodeticDatum WGS84 = GeodeticDatum.WGS84;
    static GeodeticDatum RGF93 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGF93","RGF93"),  PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, GeographicExtent.WORLD, null, null);
    //static GeodeticDatum ETRS89 = RGF93;
    static GeodeticDatum ETRS89 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","ETRS89","ETRS 89"),  PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, GeographicExtent.WORLD, null, null);


    // ------------------------------------------------------------------------
    // Geocentric CRS
    // ------------------------------------------------------------------------
    static GeocentricCRS WGS84_CARTESIAN = new GeocentricCRS(new Identifier("IGNF","WGS84","WGS84 Cartésiennes géocentriques"),WGS84, GeocentricCRS.XYZ);
    static GeocentricCRS RGF93_CARTESIAN = new GeocentricCRS(new Identifier("IGNF","RGF93","RGF93 Cartésiennes géocentriques"),RGF93, GeocentricCRS.XYZ);
    static GeocentricCRS ETRS89_CARTESIAN = new GeocentricCRS(new Identifier("IGNF","ETRS89","ETRS89 cartesiennes geocentriques"),ETRS89, GeocentricCRS.XYZ);

    // ------------------------------------------------------------------------
    // Geographic CRS
    // ------------------------------------------------------------------------
    static Geographic2DCRS NTF_G_GEO2D = new Geographic2DCRS(new Identifier("IGNF","NTF_G_2DGEO_GR","NTF_G Géographique (gr)"), NTF_G, Geographic2DCRS.LONLAT_GG_CS);
    static Geographic2DCRS NTF_P_GEO2D = new Geographic2DCRS(new Identifier("IGNF","NTF_P_2DGEO_GR","NTF_P Géographique (gr)"), NTF_P, Geographic2DCRS.LONLAT_GG_CS);
    static Geographic2DCRS ED50_GEO2D = new Geographic2DCRS(new Identifier("IGNF","ED50_2DGEO_D","ED50 Géographique (deg)"), ED50, Geographic2DCRS.LONLAT_DD_CS);
    static Geographic3DCRS ED50_GEO3D = new Geographic3DCRS(new Identifier("IGNF","ED50_3DGEO_D","ED50 Géographique (deg)"), ED50, Geographic3DCRS.LONLATH_DDM_CS);
    static Geographic2DCRS WGS84_GEO2D = new Geographic2DCRS(new Identifier("IGNF","WGS84_2DGEO_D","WGS84 Géographique (deg)"), WGS84, Geographic2DCRS.LONLAT_DD_CS);
    static Geographic3DCRS WGS84_GEO3D = new Geographic3DCRS(new Identifier("IGNF","WGS84_3DGEO_D","WGS84 Géographique (deg)"), WGS84, Geographic3DCRS.LONLATH_DDM_CS);

    // ------------------------------------------------------------------------
    // Projected CRS
    // ------------------------------------------------------------------------
    static ProjectedCRS LAMB1 = new ProjectedCRS(new Identifier("IGNF","LAMB1","Lambert 1"), NTF_P,
            LambertConicConformal1SP.createLCC1SP(Ellipsoid.CLARKE1880IGN, 55d*180d/200d, 0.99987734, 0, 600000, 200000));
    static ProjectedCRS LAMB2 = new ProjectedCRS(new Identifier("IGNF","LAMB2","Lambert 2"), NTF_P,
            LambertConicConformal1SP.createLCC1SP(Ellipsoid.CLARKE1880IGN, 52d*180d/200d, 0.99987742, 0, 600000, 200000));
    static ProjectedCRS LAMB3 = new ProjectedCRS(new Identifier("IGNF","LAMB2","Lambert 2"), NTF_P,
            LambertConicConformal1SP.createLCC1SP(Ellipsoid.CLARKE1880IGN, 49d*180d/200d, 0.99987750, 0, 600000, 200000));
    static ProjectedCRS LAMB4 = new ProjectedCRS(new Identifier("IGNF","LAMB2","Lambert 2"), NTF_P,
            LambertConicConformal1SP.createLCC1SP(Ellipsoid.CLARKE1880IGN, 46.85*180d/200d, 0.99994471, 0, 600000, 200000));
    static ProjectedCRS LAMBE = new ProjectedCRS(new Identifier("IGNF","LAMBE","Lambert 2 étendu"), NTF_P,
            LambertConicConformal1SP.createLCC1SP(Ellipsoid.CLARKE1880IGN, 52d*180d/200d, 0.99987742, 0, 600000, 2200000));

    static ProjectedCRS LAMBGC = new ProjectedCRS(new Identifier("IGNF","LAMBGC","Lambert Grand Champ"), NTF_P,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.CLARKE1880IGN, 47d, 45d, 49d, 0, 600000, 600000));

    static ProjectedCRS LAMB_OACI_DECCA = new ProjectedCRS(new Identifier("IGNF","LAMB_OACI","Lambert OACI DECCA (GRS_80)"),
            WGS84,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80,
                    AngleFormat.parseAngle("44°47'30\""),
                    AngleFormat.parseAngle("36°15'"),
                    AngleFormat.parseAngle("52°56'"),
                    2.5, 700000, 200000));

    static ProjectedCRS WGS_UTM = new ProjectedCRS(new Identifier("IGNF","WGS84_UTM31N","UTM 31 N (WGS84)"),
            WGS84,
            new UniversalTransverseMercatorAuto(Ellipsoid.WGS84, new HashMap<String, Measure>()));

    static ProjectedCRS WGS_UTM31N;

    static ProjectedCRS LAMB93 = new ProjectedCRS(new Identifier("IGNF","LAMB93","Lambert 93"), RGF93,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80, 46.5d, 44d, 49d, 3d, 700000, 6600000));

    static ProjectedCRS LAMBERT_CC42 = new ProjectedCRS(new Identifier("IGNF","LAMBERT CC42","Lambert CC42"), RGF93,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80, 42d, 41.25d, 42.75d, 3d, 1700000, 1200000));

    static ProjectedCRS LAMBERT_CC43 = new ProjectedCRS(new Identifier("IGNF","LAMBERT CC43","Lambert CC43"), RGF93,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80, 43d, 42.25d, 43.75d, 3d, 1700000, 2200000));

    static ProjectedCRS ETRS_LCC = new ProjectedCRS(new Identifier("IGNF","ETRS_LCC","ETRS89 LCC"), ETRS89,
            LambertConicConformal2SP.createLCC2SP(Ellipsoid.GRS80, 52d, 35d, 65d, 10d, 4000000, 2800000));

    static Map<String,Measure> params = new HashMap<String,Measure>();
    static{
        params.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(52, Unit.DEGREE));
        params.put(Parameter.CENTRAL_MERIDIAN,   new Measure(10, Unit.DEGREE));
        params.put(Parameter.SCALE_FACTOR, new Measure(1, Unit.UNIT));
        params.put(Parameter.FALSE_EASTING, new Measure(4321000, Unit.METER));
        params.put(Parameter.FALSE_NORTHING, new Measure(3210000, Unit.METER));
    }
    static ProjectedCRS ETRS89_LAEA = new ProjectedCRS(new Identifier("IGNF","ETRS89LAEA","ETRS89 LAEA"), ETRS89,
            new LambertAzimuthalEqualArea(Ellipsoid.GRS80, params));

    static ProjectedCRS ETRS_UTM32N;

    // ------------------------------------------------------------------------
    // Vertical CRS
    // ------------------------------------------------------------------------
    static VerticalCRS IGN69 = new VerticalCRS(new Identifier("IGNF","IGN69","IGN69"), new VerticalDatum(
            new Identifier("EPSG", "5119", "Nivellement general de la France - IGN69", "IGN69"),
            new GeographicExtent("France", 42, 51.5, -5.5, 8.5),
            "Mean sea level at Marseille.",
            "1969", VerticalDatum.Type.GEOIDAL, "RAF09.txt", GeodeticDatum.RGF93));
    static VerticalCRS IGN78 = new VerticalCRS(new Identifier("IGNF","IGN78","IGN78"), new VerticalDatum(
                    new Identifier("EPSG", "5120", "Nivellement general de la France - IGN78", "IGN78"),
                    new GeographicExtent("Corse (France)", 41.2, 43.2, 8.41666666666666, 9.71666666666666),
                    "", "", VerticalDatum.Type.GEOIDAL, "RAC09.txt", GeodeticDatum.RGF93));
    static VerticalCRS EGM2008 = new VerticalCRS(new Identifier("IGNF","EGM2008","EGM2008"), VerticalDatum.EGM2008);

    static VerticalCRS RGF93_HEIGHT = new VerticalCRS(new Identifier("IGNF","RGF93_HEIGHT","RGF93 HEIGHT"), VerticalDatum.GRS80VD);

    // ------------------------------------------------------------------------
    // Projected CRS
    // ------------------------------------------------------------------------
    static CompoundCRS LAMBE_IGN69;
    static CompoundCRS LAMBE_IGN78;
    static CompoundCRS LAMB93_IGN69;
    static CompoundCRS LAMB93_IGN78;
    static CompoundCRS LAMB93_RGF93_HEIGHT;
    static CompoundCRS LAMBERT_CC42_IGN69;
    static CompoundCRS LAMBERT_CC43_IGN69;

    static CompoundCRS NTF_P_GEO2D_EGM2008;
    static CompoundCRS NTF_P_GEO2D_IGN69;
    static CompoundCRS ED50_GEO2D_EGM2008;
    static CompoundCRS WGS_UTM31N_EGM2008;

    static {
        //NTF_G.addGeocentricTransformation(WGS84, new GeocentricTranslation(-168, -60, 320, 1));
        NTF_G.addGeocentricTransformation(ED50, new GeocentricTranslation(-84, 37, 437, 1));
        //NTF_P.addGeocentricTransformation(WGS84, new GeocentricTranslation(-168, -60, 320, 1));
        NTF_P.addGeocentricTransformation(ED50, new GeocentricTranslation(-84, 37, 437, 1));
        //ED50.addGeocentricTransformation(WGS84, new GeocentricTranslation(-84, -97, -117, 1));

        params = new HashMap<String, Measure>();
        params.put(Parameter.CENTRAL_MERIDIAN, new Measure(3, Unit.DEGREE));
        params.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        WGS_UTM31N = new ProjectedCRS(new Identifier("IGNF","WGS84_UTM31N","UTM 31 N (WGS84)"),
                WGS84,
                new UniversalTransverseMercator(Ellipsoid.WGS84, params));

        params = new HashMap<String, Measure>();
        params.put(Parameter.CENTRAL_MERIDIAN, new Measure(9, Unit.DEGREE));
        params.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        ETRS_UTM32N = new ProjectedCRS(new Identifier("IGNF","ETRS_UTM32N","ETRS UTM32N"), ETRS89,
                new UniversalTransverseMercator(Ellipsoid.GRS80, params));
        try {
            NTF_G.addGeocentricTransformation(RGF93, FrenchGeocentricNTF2RGF.getInstance());
            NTF_P.addGeocentricTransformation(RGF93, FrenchGeocentricNTF2RGF.getInstance());
            NTF_G.addGeocentricTransformation(ETRS89, FrenchGeocentricNTF2RGF.getInstance());
            NTF_P.addGeocentricTransformation(ETRS89, FrenchGeocentricNTF2RGF.getInstance());
            LAMBE_IGN69 = new CompoundCRS(new Identifier("IGNF","LAMBE_IGN69","Lambert 2 étendu + IGN69"), LAMBE, IGN69);
            LAMBE_IGN78 = new CompoundCRS(new Identifier("IGNF","LAMBE_IGN78","Lambert 2 étendu + IGN78"), LAMBE, IGN69);
            LAMB93_IGN69 = new CompoundCRS(new Identifier("IGNF","LAMB93_IGN69","Lambert 93 + IGN69"), LAMB93, IGN69);
            LAMB93_IGN78 = new CompoundCRS(new Identifier("IGNF","LAMB93_IGN78","Lambert 93 + IGN78"), LAMB93, IGN69);
            LAMB93_RGF93_HEIGHT = new CompoundCRS(new Identifier("IGNF","LAMB93_HEIGHT","LAMB93_HEIGHT"), LAMB93, RGF93_HEIGHT);
            LAMBERT_CC42_IGN69 = new CompoundCRS(new Identifier("IGNF","LAMB_CC42_IGN69","Lambert CC42 + IGN69"), LAMBERT_CC42, IGN69);
            LAMBERT_CC43_IGN69 = new CompoundCRS(new Identifier("IGNF","LAMB_CC43_IGN69","Lambert CC43 + IGN69"), LAMBERT_CC43, IGN69);
            NTF_P_GEO2D_EGM2008 = new CompoundCRS(new Identifier("IGNF","NTF_GEO2D_EGM2008","NTF (Paris) Geographique 2D (gr) + EGM 2008"), NTF_P_GEO2D, EGM2008);
            ED50_GEO2D_EGM2008 = new CompoundCRS(new Identifier("IGNF","ED50_GEO2D_EGM2008","ED50 Geographique 2D (deg) + EGM 2008"), ED50_GEO2D, EGM2008);
            NTF_P_GEO2D_IGN69 = new CompoundCRS(new Identifier("IGNF","NTF_GEO2D_IGN69","NTF (Paris) Geographique 2D (gr) + IGN69"), NTF_P_GEO2D, IGN69);
            WGS_UTM31N_EGM2008 = new CompoundCRS(new Identifier("IGNF","WGS_UTM_31N_EGM2008","WGS UTM 31N + EGM 2008"), WGS_UTM31N, EGM2008);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------
    // NTF 2 WGS
    // ------------------------------------------------------------------------

    @Test
    public void testNTF_P_GEO2D_To_LAMBERT3() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-1, 50}, NTF_P_GEO2D, LAMB3, new double[]{529035.995, 300389.276}, MM_IN_DEG, MM);
    }

    // ------------------------------------------------------------------------
    // NTF 2 WGS
    // ------------------------------------------------------------------------

    @Test
    public void testNTF_P_GEO2D_To_WGS84_CARTESIAN() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0, 50}, NTF_P_GEO2D, WGS84_CARTESIAN, new double[]{4513867.678, 184180.302, 4487377.241}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_G_GEO2D_To_WGS84_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0,50}, NTF_G_GEO2D, WGS84_GEO2D, new double[]{-0.00076096, 44.99996000}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    public void testNTF_P_GEO2D_To_WGS84_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0,50}, NTF_P_GEO2D, WGS84_GEO2D, new double[]{2.33655573, 44.99997468}, MM_IN_DEG, MM_IN_DEG);
    }

    @Test
    public void testNTF_P_GEO2D_To_WGS84_LAMBERT_OACI() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0, 50}, NTF_P_GEO2D, LAMB_OACI_DECCA, new double[]{687249.051, 222917.435}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_To_WGS84_UTM_31N() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0, 50}, NTF_P_GEO2D, WGS_UTM31N, new double[]{447710.418, 4983161.661}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_EGM2008_To_WGS84_C() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0, 50, 0}, NTF_P_GEO2D_EGM2008, WGS84_CARTESIAN, new double[]{4513873.110, 184180.524, 4487382.677}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_EGM2008_To_WGS84_GEO3D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{0, 50, 0}, NTF_P_GEO2D_EGM2008, WGS84_GEO3D, new double[]{2.33655573, 44.99997468, 51.276}, MM_IN_DEG, MM_IN_DEG);
    }

    // ------------------------------------------------------------------------
    // NTF 2 ED50
    // ------------------------------------------------------------------------

    @Test
    public void testNTF_P_GEO2D_To_ED50_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-1, 50}, NTF_P_GEO2D, ED50_GEO2D, new double[]{1.43772499, 45.00098603}, MM_IN_DEG, MM_IN_DEG);
    }


    @Test
    public void testNTF_P_GEO2D_EGM2008_To_ED50_GEO3D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-1, 50, 100}, NTF_P_GEO2D_EGM2008, ED50_GEO3D, new double[]{1.43772498, 45.00098602, 82.631}, MM_IN_DEG, MM_IN_DEG);
    }

    // ------------------------------------------------------------------------
    // ED50 2 NTF
    // ------------------------------------------------------------------------

    @Test
    public void testED50_GEO2D_To_NTF_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{2, 48}, ED50_GEO2D, NTF_P_GEO2D, new double[]{-0.37529325, 53.33236388}, MM_IN_DEG, MM_IN_DEG);
    }

    // ------------------------------------------------------------------------
    // ED50 2 WGS
    // ------------------------------------------------------------------------

    @Test
    public void testED50_GEO2D_EGM2008_To_WGS_UTM31_EGM2008() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{2, 48, 0}, ED50_GEO2D_EGM2008, WGS_UTM31N_EGM2008, new double[]{425309.566, 5316680.987, 0}, MM_IN_DEG, MM);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // Transformation grille
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // NTF to NTF
    // ------------------------------------------------------------------------

    @Test
    public void testNTF_G_GEO2D_to_NTF_LAMBERT_GC() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48}, NTF_P_GEO2D, LAMBGC, new double[]{951494.057, 187341.695}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_to_NTF_LAMBERT_GC() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48}, NTF_G_GEO2D, LAMBGC, new double[]{761391.473, 179688.934}, MM_IN_DEG, MM);
    }

    // ------------------------------------------------------------------------
    // NTF to RGF93
    // ------------------------------------------------------------------------

    @Test
    public void testNTF_P_GEO2D_to_RGF93_CARTESIAN() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48}, NTF_P_GEO2D, RGF93_CARTESIAN, new double[]{4625404.758, 539819.798, 4343756.349}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_IGN69_to_RGF93_CARTESIAN() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48, 100}, NTF_P_GEO2D_IGN69, RGF93_CARTESIAN, new double[]{4625481.991, 539828.812, 4343829.368}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_G_to_LAMBERT93() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48}, NTF_P_GEO2D, LAMB93, new double[]{997304.067, 6240309.718}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_IGN69_to_LAMBERT93_RGF93_HEIGHT() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48, 100}, NTF_P_GEO2D_IGN69, LAMB93_RGF93_HEIGHT, new double[]{997304.067, 6240309.718, 147.722}, MM_IN_DEG, MM);
    }

    @Test
    public void testNTF_P_GEO2D_IGN69_to_LAMBERT93_IGN69() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{4.8, 48, 100}, NTF_P_GEO2D_IGN69, LAMB93_IGN69, new double[]{997304.067, 6240309.718, 100}, MM_IN_DEG, MM);
    }

    @Test
    public void testLAMBE_to_LAMBERT93() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000}, LAMBE, LAMB93, new double[]{945698.924, 6231380.043}, MM, MM);
    }

    @Test
    public void testLAMBE_IGN69_to_LAMBERT93_RGF93_HEIGHT() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000, 1000}, LAMBE_IGN69, LAMB93_RGF93_HEIGHT, new double[]{945698.930, 6231380.043, 1048.594}, MM, MM);
    }

    @Test
    public void testLAMBE_IGN69_to_LAMBERT_CC42_IGN69() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000, 100}, LAMBE_IGN69, LAMBERT_CC42_IGN69, new double[]{1945550.945, 1330895.995, 100}, MM, MM);
    }

    @Test
    public void testLAMBE_IGN69_to_LAMBERT_CC43_IGN69() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000, 100}, LAMBE_IGN69, LAMBERT_CC43_IGN69, new double[]{1945501.135, 2219896.534, 100}, MM, MM);
    }

    @Test
    public void testLAMBE_to_ETRS_LCC() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000}, LAMBE, ETRS_LCC, new double[]{3684671.410, 1854821.809}, MM, MM);
    }

    @Test
    public void testLAMBE_to_ETRS_LAEA() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000}, LAMBE, ETRS89_LAEA, new double[]{3996336.163, 2234361.507}, MM, MM);
    }

    @Test
    public void testLAMBE_to_ETRS_UTM32N() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{900000, 1800000}, LAMBE, ETRS_UTM32N, new double[]{257525.666, 4780611.308}, MM, MM);
    }

}
