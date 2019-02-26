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

package org.cts.parser.prj;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Map;

import org.cts.CRSFactory;
import org.cts.CTSTestCase;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeocentricCRS;
import org.cts.registry.EPSGRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Erwan Bocher, Michaël Michaud
 */
class PrjParserTest extends CTSTestCase {

    private PrjParser parser;

    @BeforeEach
    void setUp() {
        parser = new PrjParser();
    }

    @Test
    void testParseNodeWithText() {
        String prj = "TOTO[\"some text\"]";
        PrjElement elem = parser.parseNode(CharBuffer.wrap(prj));

        assertTrue(elem instanceof PrjNodeElement);

        PrjNodeElement ne = (PrjNodeElement) elem;
        assertEquals("TOTO", ne.getName());
        assertEquals(1, elem.getChildren().size());

        assertTrue(ne.getChildren().get(0) instanceof PrjStringElement);

        PrjStringElement se = (PrjStringElement) ne.getChildren().get(0);
        assertEquals("some text", se.getValue());
    }

    @Test
    void testParseNodeMultipleChildren() {
        String prj = "TOTO[\"some text\", \"some other text\"]";
        PrjElement elem = parser.parseNode(CharBuffer.wrap(prj));

        PrjNodeElement ne = (PrjNodeElement) elem;
        assertEquals(2, elem.getChildren().size());

        PrjStringElement se = (PrjStringElement) ne.getChildren().get(0);
        assertEquals("some text", se.getValue());

        se = (PrjStringElement) ne.getChildren().get(1);
        assertEquals("some other text", se.getValue());
    }

    @Test
    void testParseNodeWithDouble() {
        String prj = "TOTO[\"some text\", 48.000178]";
        PrjElement elem = parser.parseNode(CharBuffer.wrap(prj));

        PrjNodeElement ne = (PrjNodeElement) elem;

        PrjStringElement se = (PrjStringElement) ne.getChildren().get(0);
        assertEquals("some text", se.getValue());

        assertTrue(ne.getChildren().get(1) instanceof PrjNumberElement);
        PrjNumberElement nne = (PrjNumberElement) ne.getChildren().get(1);
        assertEquals(48.000178, nne.getValue(), 0);
    }

    @Test
    void testParseNestedNodes() {
        String prj = "TOTO[\"some text\", TATA[\"some other text\"]]";
        PrjElement elem = parser.parseNode(CharBuffer.wrap(prj));

        PrjNodeElement ne = (PrjNodeElement) elem;
        assertEquals(2, elem.getChildren().size());

        PrjStringElement se = (PrjStringElement) ne.getChildren().get(0);
        assertEquals("some text", se.getValue());

        assertTrue(ne.getChildren().get(1) instanceof PrjNodeElement);
        ne = (PrjNodeElement) ne.getChildren().get(1);
        assertEquals("TATA", ne.getName());
        se = (PrjStringElement) ne.getChildren().get(0);
        assertEquals("some other text", se.getValue());
    }

    @Test
    void testLambert93() {
        String prj = "PROJCS[\"RGF93_Lambert_93\", GEOGCS[\"GCS_RGF_1993\", DATUM[\"D_RGF_1993\", "
                + "SPHEROID[\"GRS_1980\",6378137.0,298.257222101]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",700000.0],"
                + "PARAMETER[\"False_Northing\",6600000.0],"
                + "PARAMETER[\"Central_Meridian\",3.0],"
                + "PARAMETER[\"Standard_Parallel_1\",44.0],"
                + "PARAMETER[\"Standard_Parallel_2\",49.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.5],"
                + "UNIT[\"Meter\",1.0]]";

        Map<String, String> p = parser.getParameters(prj);
        assertEquals(p.get("proj"), "lcc");
        assertEquals(p.get("units"), "m");
        assertEquals(p.get("pm"), "greenwich");
        assertEquals(p.get("datum"), "rgf93");
        assertEquals(Double.parseDouble(p.get("lon_0")), 3.0, 0);
        assertEquals(Double.parseDouble(p.get("x_0")), 700000.0, 0);
        assertEquals(Double.parseDouble(p.get("y_0")), 6600000.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_1")), 44.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_0")), 46.5, 0);
        assertEquals(Double.parseDouble(p.get("lat_2")), 49.0, 0);
    }

    @Test
    void testLambert2Etendu() {
        String prj = "PROJCS[\"NTF_Lambert_II_étendu\",	GEOGCS[\"GCS_NTF\", DATUM[\"D_NTF\","
                + "SPHEROID[\"Clarke_1880_IGN\",6378249.2,293.46602]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",600000.0], PARAMETER[\"False_Northing\",2200000.0],"
                + "PARAMETER[\"Central_Meridian\",2.3372291667], PARAMETER[\"Standard_Parallel_1\",45.8989188889],"
                + "PARAMETER[\"Standard_Parallel_2\",47.6960144444], PARAMETER[\"Scale_Factor\",1.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.8], UNIT[\"Meter\",1.0]]";

        // +proj=lcc+a=6378137.0+lon_0=3.0+pm=greenwich+x_0=700000.0+y_0=6600000.0
        // +rf=298.257222101+lat_1=44.0+lat_0=46.5+units=m+lat_2=49.0"
        Map<String, String> p = parser.getParameters(prj);
        assertEquals(p.get("proj"), "lcc");
        assertEquals(p.get("units"), "m");
        assertEquals(p.get("pm"), "greenwich");
        assertEquals(p.get("datum"), "ntf");
        assertEquals(Double.parseDouble(p.get("lon_0")), 2.3372291667, 0);
        assertEquals(Double.parseDouble(p.get("x_0")), 600000.0, 0);
        assertEquals(Double.parseDouble(p.get("y_0")), 2200000.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_1")), 45.8989188889, 0);
        assertEquals(Double.parseDouble(p.get("lat_0")), 46.8, 0);
        assertEquals(Double.parseDouble(p.get("lat_2")), 47.6960144444, 0);
    }

    @Test
    void testGoogleProjection() {
        String prj =
                "PROJCS[\"WGS 84 / Pseudo - Mercator\", GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], "
                + "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], "
                + "AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]], "
                + "PROJECTION[\"Popular Visualisation Pseudo Mercator\", AUTHORITY[\"EPSG\",\"1024\"]], PARAMETER[\"semi_minor\", 6378137.0], PARAMETER[\"latitude_of_origin\", 0.0],"
                + " PARAMETER[\"central_meridian\", 0.0], PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 0.0], PARAMETER[\"false_northing\", 0.0], "
                + "UNIT[\"m\", 1.0], AXIS[\"Easting\", EAST], AXIS[\"Northing\", NORTH], AUTHORITY[\"EPSG\",\"3857\"]]";

        Map<String, String> p = parser.getParameters(prj);
        assertEquals(p.get("proj"), "merc");
        assertEquals(p.get("units"), "m");
        assertEquals(Double.parseDouble(p.get("lon_0")), 0., 0);
        assertEquals(Double.parseDouble(p.get("x_0")), 0.0, 0);
        assertEquals(Double.parseDouble(p.get("y_0")), 0.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_0")), 0.0, 0);
    }

    @Test
    void testOGCWKT() {
        String prj = "PROJCS[\"NTF (Paris) / Lambert zone II\""
                + ",GEOGCS[\"NTF (Paris)\",DATUM[\"Nouvelle_Triangulation_Francaise_Paris\","
                + "SPHEROID[\"Clarke 1880 (IGN)\",6378249.2,293.4660212936269,"
                + "AUTHORITY[\"EPSG\",\"7011\"]],TOWGS84[-168,-60,320,0,0,0,0],"
                + "AUTHORITY[\"EPSG\",\"6807\"]],PRIMEM[\"Paris\",2.33722917,AUTHORITY[\"EPSG\",\"8903\"]],"
                + "UNIT[\"grad\",0.01570796326794897,AUTHORITY[\"EPSG\",\"9105\"]],AUTHORITY[\"EPSG\",\"4807\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Lambert_Conformal_Conic_1SP\"],PARAMETER[\"latitude_of_origin\",52],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",0.99987742],PARAMETER[\"false_easting\",600000],PARAMETER[\"false_northing\",2200000],AUTHORITY[\"EPSG\",\"27572\"],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";
        Map<String, String> p = parser.getParameters(prj);
        assertEquals("EPSG:27572", p.get(PrjKeyParameters.REFNAME));
    }

    @Test
    void testReadWriteOGC_PRJ() throws Exception {
        CRSFactory cRSFactory = new CRSFactory();
        String prj = "PROJCS[\"NTF (Paris) / Lambert zone II\",GEOGCS[\"NTF (Paris)\","
                + "DATUM[\"Nouvelle_Triangulation_Francaise_Paris\","
                + "SPHEROID[\"Clarke 1880 (IGN)\",6378249.2,293.4660212936269,"
                + "AUTHORITY[\"EPSG\",\"7011\"]],TOWGS84[-168,-60,320,0,0,0,0],"
                + "AUTHORITY[\"EPSG\",\"6807\"]],PRIMEM[\"Paris\",2.33722917,"
                + "AUTHORITY[\"EPSG\",\"8903\"]],UNIT[\"grad\",0.01570796326794897,"
                + "AUTHORITY[\"EPSG\",\"9105\"]],AUTHORITY[\"EPSG\",\"4807\"]],UNIT[\"metre\",1,"
                + "AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Lambert_Conformal_Conic_1SP\"],"
                + "PARAMETER[\"latitude_of_origin\",52],PARAMETER[\"central_meridian\",0],"
                + "PARAMETER[\"scale_factor\",0.99987742],PARAMETER[\"false_easting\",600000],"
                + "PARAMETER[\"false_northing\",2200000],"
                + "AUTHORITY[\"EPSG\",\"27572\"],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(prj);
        assertNotNull(crs);
        assertEquals("EPSG", crs.getAuthorityName());
        assertEquals("27572", crs.getAuthorityKey());
        //String crsWKT = crs.toWKT();
        // This test cannot work because the ProjectedCRS of CTS does not retain
        // the Geographic2DCRS equivalent to this CRS without the projection so
        // the unit used by the geog CRS and its authority are missing.
        //assertTrue(prj.equals(crsWKT));
    }

    @Test
    void testWriteOGC_3857_PRJ() throws Exception {
        CRSFactory cRSFactory = new CRSFactory();
        cRSFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        CoordinateReferenceSystem crs = cRSFactory.getCRS("EPSG:3857");
        assertNotNull(crs);
        assertEquals("EPSG", crs.getAuthorityName());
        assertEquals("3857", crs.getAuthorityKey());
    }

    @Test
    void testGeocentricCRS_PRJ() throws Exception {
        CRSFactory cRSFactory = new CRSFactory();
        String prj = "GEOCCS[\"WGS 84 (geocentric)\",\n"
                + "    DATUM[\"World Geodetic System 1984\",\n"
                + "        SPHEROID[\"WGS 84\",6378137.0,298.257223563,\n"
                + "            AUTHORITY[\"EPSG\",\"7030\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"6326\"]],\n"
                + "    PRIMEM[\"Greenwich\",0.0,\n"
                + "        AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "    UNIT[\"m\",1.0],\n"
                + "    AXIS[\"Geocentric X\",OTHER],\n"
                + "    AXIS[\"Geocentric Y\",EAST],\n"
                + "    AXIS[\"Geocentric Z\",NORTH],\n"
                + "    AUTHORITY[\"EPSG\",\"4328\"]]";
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(prj);
        assertNotNull(crs);
        assertTrue(crs instanceof GeocentricCRS);
    }

    @Test
    void testCH1903_LV03_PRJ() {
        String prj = "PROJCS[\"CH1903_LV03\",GEOGCS[\"GCS_CH1903\",DATUM[\"D_CH1903\","
                + "SPHEROID[\"Bessel_1841\",6377397.155,299.1528128]],PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Hotine_Oblique_Mercator_Azimuth_Center\"],PARAMETER[\"False_Easting\",600000.0],PARAMETER[\"False_Northing\",200000.0],PARAMETER[\"Scale_Factor\",1.0],PARAMETER[\"Azimuth\",90.0],"
                + "PARAMETER[\"Longitude_Of_Center\",7.439583333333333],PARAMETER[\"Latitude_Of_Center\",46.95240555555556],UNIT[\"Meter\",1.0],AUTHORITY[\"EPSG\",21781]]";
        Map<String, String> p = parser.getParameters(prj);
        assertEquals("EPSG:21781", p.get(PrjKeyParameters.REFNAME));
    }

    @Test
    void testNAD_1983_StatePlane_Iowa_South_FIPS_1402_Feet_PRJ() throws Exception {
        URI uri = IOPrjTest.class.getResource("WaukeeStreets.prj").toURI();
        RandomAccessFile f = new RandomAccessFile(uri.getPath(), "r");
        byte[] bb = new byte[(int)f.length()];
        f.readFully(bb);
        Map<String,String> params = new PrjParser().getParameters(new String(bb));
        assertEquals(6378137.0, Double.parseDouble(params.get("a")), 0.001);
        assertEquals(500000, Double.parseDouble(params.get("x_0")), 0.001);

        // Check that wkt has the same parameters after it has been parsed and written back
        String filePath = uri.getPath();
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(new File(filePath));
        params = new PrjParser().getParameters(crs.toWKT());
        assertEquals(6378137.0, Double.parseDouble(params.get("a")), 0.001);
        assertEquals(500000, Double.parseDouble(params.get("x_0")), 0.001);
    }
}
