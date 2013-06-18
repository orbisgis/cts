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

import java.util.HashMap;
import java.util.Map;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.Parameter;
import org.cts.crs.CRSHelper;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.crs.ProjectedCRS;
import org.cts.datum.GeodeticDatum;
import org.cts.op.projection.CylindricalEqualArea;
import org.cts.op.projection.MillerCylindrical;
import org.cts.parser.prj.PrjParser;
import org.cts.registry.Registry;
import org.cts.registry.RegistryManager;
import org.cts.units.Measure;
import org.cts.units.Unit;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *This class contains tests that uses CRS not present in registries and thus needing
 * a special treatment.
 * 
 * @author Jules Party
 */
public class SpecialCoordinateTransformTest extends BaseCoordinateTransformTest {
    
    private PrjParser parser;

    @Before
    public void setUp() {
        parser = new PrjParser();
    }
    
    @Test
    public void testPolyconic() throws Exception {
        double csNameSrc_X = -45;
        double csNameSrc_Y = 6;
        double csNameDest_X = 5996378.71;
        double csNameDest_Y = 10671650.06;
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = createCRS("EPSG:4674");
        RegistryManager registryManager = new RegistryManager();
        Registry registry = registryManager.getRegistry("epsg");
        Map<String, String> crsParameters = registry.getParameters("29101");
        // The output CRS is the same as the EPSG:29101, but it uses the ellipsoid
        // GRS80 instead of the ellipsoid aust_SA. 
        CoordinateReferenceSystem outputCRS = new ProjectedCRS(new Identifier(ProjectedCRS.class, "SIRGAS 2000 / Brazil Polyconic"),
                (GeodeticDatum) inputCRS.getDatum(), CRSHelper.getProjection("poly", Ellipsoid.GRS80, crsParameters));
        double[] pointSource = new double[]{csNameSrc_X, csNameSrc_Y, 0};
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        double[] pointDest = new double[]{csNameDest_X, csNameDest_Y, 0};
        double[] check = transform((GeodeticCRS) outputCRS, (GeodeticCRS) inputCRS, pointDest);
        //printCRStoWKT(inputCRS);
        //printCRStoWKT(outputCRS);
        assertTrue(checkEquals2D("POLY dir--> " + "EPSG:4674" + " to " + "SIRGAS 2000 / Brazil Polyconic", result, pointDest, tolerance));
        assertTrue(checkEquals2D("POLY inv--> " + "SIRGAS 2000 / Brazil Polyconic" + " to " +"EPSG:4674", check, pointSource, tolerance));
    }

    @Test
    public void testCylindricalEqualArea() throws Exception {
        Map<String, Measure> map = new HashMap<String, Measure>();
        map.put(Parameter.CENTRAL_MERIDIAN, new Measure(-75, Unit.DEGREE));
        map.put(Parameter.LATITUDE_OF_TRUE_SCALE, new Measure(5, Unit.DEGREE));
        map.put(Parameter.FALSE_EASTING, new Measure(0, Unit.METER));
        map.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        CylindricalEqualArea proj = new CylindricalEqualArea(Ellipsoid.CLARKE1866, map);
        double PI = Math.PI;
        double[] pointSource = new double[]{5, -78, 0};
        double[] result = new double[]{5 * PI / 180, -78 * PI / 180, 0};
        result = proj.transform(result);
        double[] pointDest = new double[]{-332699.8, 554248.5, 0};
        double[] check = new double[]{-332699.8, 554248.5, 0};
        check = proj.inverse().transform(check);
        check[0] = check[0] * 180 / PI;
        check[1] = check[1] * 180 / PI;
        double tolerance = 1E-1;
        assertTrue(checkEquals2D("CEA dir--> ", result, pointDest, tolerance));
        assertTrue(checkEquals2D("CEA inv--> ", check, pointSource, tolerance));
    }
    
    @Test
    public void testLAMBEtoLAMB93PRJ() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68) ID5863
        double[] srcPoint = new double[]{282331, 2273699.7, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{332602.961893497, 6709788.26447893, 0};
        String srcprj = "PROJCS[\"NTF_Lambert_II_étendu\",	GEOGCS[\"GCS_NTF\", DATUM[\"D_NTF\","
                + "SPHEROID[\"Clarke_1866_IGN\",6378249.2,293.46602]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",600000.0], PARAMETER[\"False_Northing\",2200000.0],"
                + "PARAMETER[\"Central_Meridian\",2.3372291667], PARAMETER[\"Standard_Parallel_1\",45.8989188889],"
                + "PARAMETER[\"Standard_Parallel_2\",47.6960144444], PARAMETER[\"Scale_Factor\",1.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.8], UNIT[\"Meter\",1.0]]";
        CoordinateReferenceSystem srcCRS = crsf.createFromPrj(srcprj);
        String outprj = "PROJCS[\"RGF93_Lambert_93\", GEOGCS[\"GCS_RGF_1993\", DATUM[\"D_RGF_1993\", "
                + "SPHEROID[\"GRS_1980\",6378137.0,298.257222101]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",700000.0],"
                + "PARAMETER[\"False_Northing\",6600000.0],"
                + "PARAMETER[\"Central_Meridian\",3.0],"
                + "PARAMETER[\"Standard_Parallel_1\",44.0],"
                + "PARAMETER[\"Standard_Parallel_2\",49.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.5],"
                + "UNIT[\"Meter\",1.0]]";
        CoordinateReferenceSystem outCRS = crsf.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);       
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 1E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }
    
    @Test
    public void testWGS84toLAMB93PRJ() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68) ID5863
        double[] srcPoint = new double[]{2.114551393, 50.345609791, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{636890.74032145, 7027895.26344997, 0};
        String srcprj = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\","
                + "SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],"
                + "AUTHORITY[\"EPSG\",\"6326\"]],"
                + "PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                + "UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],"
                + "AUTHORITY[\"EPSG\",\"4326\"]]";
        CoordinateReferenceSystem srcCRS = crsf.createFromPrj(srcprj);
        String outprj = "PROJCS[\"RGF93_Lambert_93\", GEOGCS[\"GCS_RGF_1993\", DATUM[\"D_RGF_1993\", "
                + "SPHEROID[\"GRS_1980\",6378137.0,298.257222101]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",700000.0],"
                + "PARAMETER[\"False_Northing\",6600000.0],"
                + "PARAMETER[\"Central_Meridian\",3.0],"
                + "PARAMETER[\"Standard_Parallel_1\",44.0],"
                + "PARAMETER[\"Standard_Parallel_2\",49.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.5],"
                + "UNIT[\"Meter\",1.0]]";
        CoordinateReferenceSystem outCRS = crsf.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);       
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }
    
    @Test
    public void testMercatorPRJ() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68) ID5863
        double[] srcPoint = new double[]{120, -3, 0};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{5009726.58, 569150.82, 0};
        String srcprj = "GEOGCS[\"Makassar\",DATUM[\"Makassar\","
                + "SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],"
                + "TOWGS84[-587.8,519.75,145.76,0,0,0,0],AUTHORITY[\"EPSG\",\"6257\"]],"
                + "PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                + "UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],"
                + "AUTHORITY[\"EPSG\",\"4257\"]]";
        CoordinateReferenceSystem srcCRS = crsf.createFromPrj(srcprj);
        String outprj = "PROJCS[\"Makassar / NEIEZ\",GEOGCS[\"Makassar\","
                + "DATUM[\"Makassar\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,"
                + "AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[-587.8,519.75,145.76,0,0,0,0],"
                + "AUTHORITY[\"EPSG\",\"6257\"]],"
                + "PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                + "UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],"
                + "AUTHORITY[\"EPSG\",\"4257\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],"
                + "PROJECTION[\"Mercator_1SP\"],PARAMETER[\"central_meridian\",110],"
                + "PARAMETER[\"scale_factor\",0.997],PARAMETER[\"false_easting\",3900000],"
                + "PARAMETER[\"false_northing\",900000],AUTHORITY[\"EPSG\",\"3002\"],"
                + "AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";
        CoordinateReferenceSystem outCRS = crsf.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);       
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }
    
    @Test
    public void testMillerCylindrical() throws Exception {
        Map<String, Measure> map = new HashMap<String, Measure>();
        map.put(Parameter.CENTRAL_MERIDIAN, new Measure(0, Unit.DEGREE));
        map.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(0, Unit.DEGREE));
        map.put(Parameter.FALSE_EASTING, new Measure(0, Unit.METER));
        map.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        map.put(Parameter.SCALE_FACTOR, new Measure(1, Unit.UNIT));
        MillerCylindrical proj = new MillerCylindrical(Ellipsoid.createEllipsoidFromSemiMinorAxis(1, 1), map);
        double PI = Math.PI;
        double[] pointSource = new double[]{50, -75, 0};
        double[] result = new double[]{50 * PI / 180, -75 * PI / 180, 0};
        result = proj.transform(result);
        double[] pointDest = new double[]{-1.3089969, 0.9536371, 0};
        double[] check = new double[]{-1.3089969, 0.9536371, 0};
        check = proj.inverse().transform(check);
        check[0] = check[0] * 180 / PI;
        check[1] = check[1] * 180 / PI;
        double tolerance = 1E-1;
        assertTrue(checkEquals2D("MILL dir--> ", result, pointDest, tolerance));
        assertTrue(checkEquals2D("MILL inv--> ", check, pointSource, tolerance));
    }
}
