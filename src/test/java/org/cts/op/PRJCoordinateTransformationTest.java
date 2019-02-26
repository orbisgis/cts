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

import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;

import org.cts.datum.GeodeticDatum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class contains tests that uses PRJ definition for the CRS
 *
 * @author Jules Party
 */
class PRJCoordinateTransformationTest extends BaseCoordinateTransformTest {

    @Test
    void testLAMBEtoLAMB93PRJ() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68) ID5863
        double[] srcPoint = new double[]{282331, 2273699.7};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{332602.961893497, 6709788.26447893};
        GeodeticDatum.NTF.removeAllTransformations();
        GeodeticDatum.NTF_PARIS.removeAllTransformations();
        GeodeticDatum.RGF93.removeAllTransformations();
        String srcprj = "PROJCS[\"NTF_Lambert_II_étendu\",	GEOGCS[\"GCS_NTF\", DATUM[\"D_NTF\","
                + "SPHEROID[\"Clarke_1866_IGN\",6378249.2,293.46602]], PRIMEM[\"Greenwich\",0.0],"
                + "UNIT[\"Degree\",0.0174532925199433]], PROJECTION[\"Lambert_Conformal_Conic\"],"
                + "PARAMETER[\"False_Easting\",600000.0], PARAMETER[\"False_Northing\",2200000.0],"
                + "PARAMETER[\"Central_Meridian\",2.3372291667], PARAMETER[\"Standard_Parallel_1\",45.8989188889],"
                + "PARAMETER[\"Standard_Parallel_2\",47.6960144444], PARAMETER[\"Scale_Factor\",1.0],"
                + "PARAMETER[\"Latitude_Of_Origin\",46.8], UNIT[\"Meter\",1.0]]";
        CoordinateReferenceSystem srcCRS = cRSFactory.createFromPrj(srcprj);
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
        CoordinateReferenceSystem outCRS = cRSFactory.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 1E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }

    @Test
    void testWGS84toLAMB93PRJ() throws Exception {
        //IGN data : POINT (931813.94 1786923.891 2525.68) ID5863
        double[] srcPoint = new double[]{2.114551393, 50.345609791};
        //IGN data : POINT (977362.95 6218045.569 0)	ID5863
        double[] expectedPoint = new double[]{636890.74032145, 7027895.26344997};
        String srcprj = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\","
                + "SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],"
                + "AUTHORITY[\"EPSG\",\"6326\"]],"
                + "PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                + "UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],"
                + "AUTHORITY[\"EPSG\",\"4326\"]]";
        CoordinateReferenceSystem srcCRS = cRSFactory.createFromPrj(srcprj);
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
        CoordinateReferenceSystem outCRS = cRSFactory.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }

    @Test
    void testMercatorPRJ() throws Exception {
        double[] srcPoint = new double[]{120, -3};
        double[] expectedPoint = new double[]{5009726.58, 569150.82};
        String srcprj = "GEOGCS[\"Makassar\",DATUM[\"Makassar\","
                + "SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],"
                + "TOWGS84[-587.8,519.75,145.76,0,0,0,0],AUTHORITY[\"EPSG\",\"6257\"]],"
                + "PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                + "UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],"
                + "AUTHORITY[\"EPSG\",\"4257\"]]";
        CoordinateReferenceSystem srcCRS = cRSFactory.createFromPrj(srcprj);
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
        CoordinateReferenceSystem outCRS = cRSFactory.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }

    @Test
    void testUnitsInPRJ() throws Exception {
        double[] srcPoint = new double[]{-62, 10};
        double[] expectedPoint = new double[]{66644.94, 82536.22};
        String srcprj = "GEOGCS[\"Trinidad 1903\",\n"
                + "    DATUM[\"Trinidad_1903\",\n"
                + "        SPHEROID[\"Clarke 1858\",6378293.645208759,294.2606763692654,\n"
                + "            AUTHORITY[\"EPSG\",\"7007\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"6302\"]],\n"
                + "    PRIMEM[\"Greenwich\",0,\n"
                + "        AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "    UNIT[\"degree\",0.01745329251994328,\n"
                + "        AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "    AUTHORITY[\"EPSG\",\"4302\"]]";
        CoordinateReferenceSystem srcCRS = cRSFactory.createFromPrj(srcprj);
        String outprj = "PROJCS[\"Trinidad 1903 / Trinidad Grid\",\n"
                + "    GEOGCS[\"Trinidad 1903\",\n"
                + "        DATUM[\"Trinidad_1903\",\n"
                + "            SPHEROID[\"Clarke 1858\",6378293.645208759,294.2606763692654,\n"
                + "                AUTHORITY[\"EPSG\",\"7007\"]],\n"
                + "            AUTHORITY[\"EPSG\",\"6302\"]],\n"
                + "        PRIMEM[\"Greenwich\",0,\n"
                + "            AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "        UNIT[\"degree\",0.01745329251994328,\n"
                + "            AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"4302\"]],\n"
                + "    UNIT[\"Clarke's link\",0.201166195164,\n"
                + "        AUTHORITY[\"EPSG\",\"9039\"]],\n"
                + "    PROJECTION[\"Cassini_Soldner\"],\n"
                + "    PARAMETER[\"latitude_of_origin\",10.44166666666667],\n"
                + "    PARAMETER[\"central_meridian\",-61.33333333333334],\n"
                + "    PARAMETER[\"false_easting\",430000],\n"
                + "    PARAMETER[\"false_northing\",325000],\n"
                + "    AUTHORITY[\"EPSG\",\"30200\"],\n"
                + "    AXIS[\"Easting\",EAST],\n"
                + "    AXIS[\"Northing\",NORTH]]";
        CoordinateReferenceSystem outCRS = cRSFactory.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }

    @Test
    void testCH1903toLV95PRJ() throws Exception {
        double[] srcPoint = new double[]{8.486419798, 47.0580435};
        double[] expectedPoint = new double[]{2679520.05, 1212273.44};
        String srcprj = "GEOGCS[\"CH1903+\",\n"
                + "    DATUM[\"CH1903\",\n"
                + "        SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,\n"
                + "            AUTHORITY[\"EPSG\",\"7004\"]],\n"
                + "        TOWGS84[674.374,15.056,405.346,0,0,0,0],\n"
                + "        AUTHORITY[\"EPSG\",\"6150\"]],\n"
                + "    PRIMEM[\"Greenwich\",0,\n"
                + "        AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "    UNIT[\"degree\",0.01745329251994328,\n"
                + "        AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "    AUTHORITY[\"EPSG\",\"4150\"]]";
        CoordinateReferenceSystem srcCRS = cRSFactory.createFromPrj(srcprj);
        String outprj = "PROJCS[\"CH1903+ / LV95\",\n"
                + "    GEOGCS[\"CH1903+\",\n"
                + "        DATUM[\"CH1903\",\n"
                + "            SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,\n"
                + "                AUTHORITY[\"EPSG\",\"7004\"]],\n"
                + "            TOWGS84[674.374,15.056,405.346,0,0,0,0],\n"
                + "            AUTHORITY[\"EPSG\",\"6150\"]],\n"
                + "        PRIMEM[\"Greenwich\",0,\n"
                + "            AUTHORITY[\"EPSG\",\"8901\"]],\n"
                + "        UNIT[\"degree\",0.01745329251994328,\n"
                + "            AUTHORITY[\"EPSG\",\"9122\"]],\n"
                + "        AUTHORITY[\"EPSG\",\"4150\"]],\n"
                + "    UNIT[\"metre\",1,\n"
                + "        AUTHORITY[\"EPSG\",\"9001\"]],\n"
                + "    PROJECTION[\"Hotine_Oblique_Mercator\"],\n"
                + "    PARAMETER[\"latitude_of_center\",46.95240555555556],\n"
                + "    PARAMETER[\"longitude_of_center\",7.439583333333333],\n"
                + "    PARAMETER[\"azimuth\",90],\n"
                + "    PARAMETER[\"rectified_grid_angle\",90],\n"
                + "    PARAMETER[\"scale_factor\",1],\n"
                + "    PARAMETER[\"false_easting\",2600000],\n"
                + "    PARAMETER[\"false_northing\",1200000],\n"
                + "    AUTHORITY[\"EPSG\",\"2056\"],\n"
                + "    AXIS[\"Y\",EAST],\n"
                + "    AXIS[\"X\",NORTH]]";
        CoordinateReferenceSystem outCRS = cRSFactory.createFromPrj(outprj);
        double[] result = transform((GeodeticCRS) srcCRS, (GeodeticCRS) outCRS, srcPoint);
        assertTrue(checkEquals2D(srcCRS + " to " + outCRS, result, expectedPoint, 10E-2));
        double[] check = transform((GeodeticCRS) outCRS, (GeodeticCRS) srcCRS, expectedPoint);
        assertTrue(checkEquals2D(outCRS + " to " + srcCRS, check, srcPoint, 1E-2));
    }
}
