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

import java.util.HashMap;
import java.util.Map;

import org.cts.datum.Ellipsoid;
import org.cts.Parameter;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.op.projection.CylindricalEqualArea;
import org.cts.op.projection.MillerCylindrical;
import org.cts.units.Measure;
import org.cts.units.Unit;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests that uses CRS not present in registries and thus
 * needing a special treatment.
 *
 * @author Jules Party
 */
public class SpecialCoordinateTransformTest extends BaseCoordinateTransformTest {

    @Test
    public void testPolyconic() throws Exception {
        double csNameSrc_X = -45;
        double csNameSrc_Y = 6;
        double csNameDest_X = 5996378.71;
        double csNameDest_Y = 10671650.06;
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = cRSFactory.getCRS("EPSG:4674");
        CoordinateReferenceSystem outputCRS = cRSFactory.createFromPrj(
                "PROJCS[\"SIRGAS 2000 / Brazil Polyconic\",\n"
                + "    GEOGCS[\"SIRGAS 2000\",\n"
                + "        DATUM[\"Sistema_de_Referencia_Geocentrico_para_America_del_Sur_2000\",\n"
                + "            SPHEROID[\"GRS 1980\",6378137,298.257222101,\n"
                + "                AUTHORITY[\"EPSG\",\"7019\"]],\n"
                + "            TOWGS84[0,0,0,0,0,0,0],\n"
                + "            AUTHORITY[\"EPSG\",\"6674\"]],\n"
                + "        PRIMEM[\"Greenwich\",0,\n"
                + "            AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "        UNIT[\"degree\",0.01745329251994328,\n"
                + "            AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"4674\"]],\n"
                + "    PROJECTION[\"Polyconic\"],\n"
                + "    PARAMETER[\"latitude_of_origin\",0],\n"
                + "    PARAMETER[\"central_meridian\",-54],\n"
                + "    PARAMETER[\"false_easting\",5000000],\n"
                + "    PARAMETER[\"false_northing\",10000000],\n"
                + "    UNIT[\"metre\",1,\n"
                + "        AUTHORITY[\"EPSG\",\"9001\"]]]");
        double[] pointSource = new double[]{csNameSrc_X, csNameSrc_Y, 0};
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        double[] pointDest = new double[]{csNameDest_X, csNameDest_Y, 0};
        double[] check = transform((GeodeticCRS) outputCRS, (GeodeticCRS) inputCRS, pointDest);
        //printCRStoWKT(inputCRS);
        //printCRStoWKT(outputCRS);
        assertTrue(checkEquals2D("POLY dir--> " + "EPSG:4674" + " to " + "SIRGAS 2000 / Brazil Polyconic", result, pointDest, tolerance));
        assertTrue(checkEquals2D("POLY inv--> " + "SIRGAS 2000 / Brazil Polyconic" + " to " + "EPSG:4674", check, pointSource, tolerance));
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

    //Read and write authority
    // @Test
    public void test27572PRJTo3857EPSG() throws Exception {
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
        CoordinateReferenceSystem crsIn = cRSFactory.createFromPrj(prj);
        assertNotNull(crsIn);
        CoordinateReferenceSystem crsOut = cRSFactory.getCRS("EPSG:3857");
        assertNotNull(crsOut);
    }
}
