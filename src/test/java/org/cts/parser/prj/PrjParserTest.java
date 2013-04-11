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
package org.cts.parser.prj;

import java.nio.CharBuffer;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class PrjParserTest {

    private PrjParser parser;

    @Before
    public void setUp() {
        parser = new PrjParser();
    }

    @Test
    public void testParseNodeWithText() {
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
    public void testParseNodeMultipleChildren() {
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
    public void testParseNodeWithDouble() {
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
    public void testParseNestedNodes() {
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
    public void testLambert93() {
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
        assertEquals(Double.parseDouble(p.get("a")), 6378137.0, 0);
        assertEquals(Double.parseDouble(p.get("lon_0")), 3.0, 0);
        assertEquals(Double.parseDouble(p.get("x_0")), 700000.0, 0);
        assertEquals(Double.parseDouble(p.get("y_0")), 6600000.0, 0);
        assertEquals(Double.parseDouble(p.get("rf")), 298.257222101, 0);
        assertEquals(Double.parseDouble(p.get("lat_1")), 44.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_0")), 46.5, 0);
        assertEquals(Double.parseDouble(p.get("lat_2")), 49.0, 0);
    }

    @Test
    public void testLambert2Etendu() {
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
    public void testGoogleProjection() {
        String prj =
                "PROJCS[\"WGS 84 / Pseudo - Mercator\", GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], "
                + "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], "
                + "AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]], "
                + "PROJECTION[\"Popular Visualisation Pseudo Mercator\", AUTHORITY[\"EPSG\",\"1024\"]], PARAMETER[\"semi_minor\", 6378137.0], PARAMETER[\"latitude_of_origin\", 0.0],"
                + " PARAMETER[\"central_meridian\", 0.0], PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 0.0], PARAMETER[\"false_northing\", 0.0], "
                + "UNIT[\"m\", 1.0], AXIS[\"Easting\", EAST], AXIS[\"Northing\", NORTH], AUTHORITY[\"EPSG\",\"3857\"]]";

        Map<String, String> p = parser.getParameters(prj);
        assertEquals(p.get("proj"), "longlat");
        assertEquals(p.get("units"), "m");
        assertEquals(Double.parseDouble(p.get("lon_0")), 0., 0);
        assertEquals(Double.parseDouble(p.get("x_0")), 0.0, 0);
        assertEquals(Double.parseDouble(p.get("y_0")), 0.0, 0);
        assertEquals(Double.parseDouble(p.get("lat_0")), 0.0, 0);
    }

    @Test
    public void testOGCWKT() {
        String prj = "PROJCS[\"NTF (Paris) / Lambert zone II\""
                + ",GEOGCS[\"NTF (Paris)\",DATUM[\"Nouvelle_Triangulation_Francaise_Paris\","
                + "SPHEROID[\"Clarke 1880 (IGN)\",6378249.2,293.4660212936269,"
                + "AUTHORITY[\"EPSG\",\"7011\"]],TOWGS84[-168,-60,320,0,0,0,0],"
                + "AUTHORITY[\"EPSG\",\"6807\"]],PRIMEM[\"Paris\",2.33722917,AUTHORITY[\"EPSG\",\"8903\"]],"
                + "UNIT[\"grad\",0.01570796326794897,AUTHORITY[\"EPSG\",\"9105\"]],AUTHORITY[\"EPSG\",\"4807\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Lambert_Conformal_Conic_1SP\"],PARAMETER[\"latitude_of_origin\",52],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",0.99987742],PARAMETER[\"false_easting\",600000],PARAMETER[\"false_northing\",2200000],AUTHORITY[\"EPSG\",\"27572\"],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";
        Map<String, String> p = parser.getParameters(prj);
        assertTrue(p.get(PrjKeyParameters.REFNAME).equals("EPSG:27572"));
    }
}
