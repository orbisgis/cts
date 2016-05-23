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

package org.cts.datum;

import org.cts.CTSTestCase;
import org.cts.Identifier;

import org.junit.Test;

import static org.cts.datum.Ellipsoid.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Jules Party
 */
public class EllipsoidTest extends CTSTestCase {

    @Test
    public void testEllipsoidConstruction() {
        LOGGER.info("Ellipsoid Construction");
        Ellipsoid e1 = createEllipsoidFromSemiMinorAxis(
                new Identifier("Test", "0001", "Construction from Semi-minor axis"),
                6380000.0, 6350000.0);
        Ellipsoid e2 = createEllipsoidFromInverseFlattening(
                new Identifier("Test", "0002", "Construction from Inverse flattening"),
                6380000.0, 300);
        Ellipsoid e3 = createEllipsoidFromEccentricity(
                new Identifier("Test", "0003", "Construction from Eccentricity"),
                6380000.0, 0.1);
        assertTrue(e1.toString().equals("[Test:0001] Construction from Semi-minor axis "
                + "(Semi-major axis = 6380000.0 | Semi-minor axis = 6350000.0)"));
        assertTrue(e2.toString().equals("[Test:0002] Construction from Inverse flattening "
                + "(Semi-major axis = 6380000.0 | Flattening = 1/300.0)"));
        assertTrue(e3.toString().equals("[Test:0003] Construction from Eccentricity "
                + "(Semi-major axis = 6380000.0 | Eccentricity = 0.1)"));
    }
    private double tol = 1E-15;

    @Test
    public void testSPHERE() {
        assertEquals(SPHERE.getName() + " Semi-major axis", SPHERE.getSemiMajorAxis(), 6371000.0, tol);
        assertEquals(SPHERE.getName() + " Inverse Flattening", SPHERE.getInverseFlattening(), Double.POSITIVE_INFINITY, tol);
        assertEquals(SPHERE.getName() + " Semi-minor axis", SPHERE.getSemiMinorAxis(), 6371000.0, tol);
        assertEquals(SPHERE.getName() + " Flattening", SPHERE.getFlattening(), 0, tol);
        assertEquals(SPHERE.getName() + " Eccentricity", SPHERE.getEccentricity(), 0, tol);
        assertEquals(SPHERE.getName() + " Square eccentricity", SPHERE.getSquareEccentricity(), 0, tol);
        assertEquals(SPHERE.getName() + " Second eccentricity squared", SPHERE.getSecondEccentricitySquared(), 0, tol);
        assertEquals(SPHERE.getName() + " ArcCoeff0", SPHERE.getArcCoeff()[0], 1, tol);
        assertEquals(SPHERE.getName() + " ArcCoeff1", SPHERE.getArcCoeff()[1], 0, tol);
        assertEquals(SPHERE.getName() + " ArcCoeff2", SPHERE.getArcCoeff()[2], 0, tol);
        assertEquals(SPHERE.getName() + " ArcCoeff3", SPHERE.getArcCoeff()[3], 0, tol);
        assertEquals(SPHERE.getName() + " ArcCoeff4", SPHERE.getArcCoeff()[4], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff0", SPHERE.getKCoeff(8)[0], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff1", SPHERE.getKCoeff(8)[1], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff2", SPHERE.getKCoeff(8)[2], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff3", SPHERE.getKCoeff(8)[3], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff4", SPHERE.getKCoeff(8)[4], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff5", SPHERE.getKCoeff(8)[5], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff6", SPHERE.getKCoeff(8)[6], 0, tol);
        assertEquals(SPHERE.getName() + " KCoeff7", SPHERE.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testGRS80() {
        assertEquals(GRS80.getName() + " Semi-major axis", GRS80.getSemiMajorAxis(), 6378137.0, tol);
        assertEquals(GRS80.getName() + " Inverse Flattening", GRS80.getInverseFlattening(), 298.257222101, tol);
        assertEquals(GRS80.getName() + " Semi-minor axis", GRS80.getSemiMinorAxis(), 6356752.314140356, tol);
        assertEquals(GRS80.getName() + " Flattening", GRS80.getFlattening(), 0.003352810681182, tol);
        assertEquals(GRS80.getName() + " Eccentricity", GRS80.getEccentricity(), 0.081819191042816, tol);
        assertEquals(GRS80.getName() + " Square eccentricity", GRS80.getSquareEccentricity(), 0.006694380022901, tol);
        assertEquals(GRS80.getName() + " Second eccentricity squared", GRS80.getSecondEccentricitySquared(), 0.006739496775479, tol);
        assertEquals(GRS80.getName() + " ArcCoeff0", GRS80.getArcCoeff()[0], 0.998324298423133, tol);
        assertEquals(GRS80.getName() + " ArcCoeff1", GRS80.getArcCoeff()[1], -0.002514607124329, tol);
        assertEquals(GRS80.getName() + " ArcCoeff2", GRS80.getArcCoeff()[2], 0.000002639110975, tol);
        assertEquals(GRS80.getName() + " ArcCoeff3", GRS80.getArcCoeff()[3], -0.000000003446648, tol);
        assertEquals(GRS80.getName() + " ArcCoeff4", GRS80.getArcCoeff()[4], 0.000000000004827, tol);
        assertEquals(GRS80.getName() + " KCoeff0", GRS80.getKCoeff(8)[0], -0.001675701576958, tol);
        assertEquals(GRS80.getName() + " KCoeff1", GRS80.getKCoeff(8)[1], -0.000002106571233, tol);
        assertEquals(GRS80.getName() + " KCoeff2", GRS80.getKCoeff(8)[2], -0.000000005881050, tol);
        assertEquals(GRS80.getName() + " KCoeff3", GRS80.getKCoeff(8)[3], -0.000000000021542, tol);
        assertEquals(GRS80.getName() + " KCoeff4", GRS80.getKCoeff(8)[4], -0.000000000000091, tol);
        assertEquals(GRS80.getName() + " KCoeff5", GRS80.getKCoeff(8)[5], 0, tol);
        assertEquals(GRS80.getName() + " KCoeff6", GRS80.getKCoeff(8)[6], 0, tol);
        assertEquals(GRS80.getName() + " KCoeff7", GRS80.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testWGS84() {
        assertEquals(WGS84.getName() + " Semi-major axis", WGS84.getSemiMajorAxis(), 6378137.0, tol);
        assertEquals(WGS84.getName() + " Inverse Flattening", WGS84.getInverseFlattening(), 298.257223563, tol);
        assertEquals(WGS84.getName() + " Semi-minor axis", WGS84.getSemiMinorAxis(), 6356752.314245179, tol);
        assertEquals(WGS84.getName() + " Flattening", WGS84.getFlattening(), 0.003352810664747, tol);
        assertEquals(WGS84.getName() + " Eccentricity", WGS84.getEccentricity(), 0.081819190842622, tol);
        assertEquals(WGS84.getName() + " Square eccentricity", WGS84.getSquareEccentricity(), 0.006694379990141, tol);
        assertEquals(WGS84.getName() + " Second eccentricity squared", WGS84.getSecondEccentricitySquared(), 0.006739496742276, tol);
        assertEquals(WGS84.getName() + " ArcCoeff0", WGS84.getArcCoeff()[0], 0.998324298431344, tol);
        assertEquals(WGS84.getName() + " ArcCoeff1", WGS84.getArcCoeff()[1], -0.002514607112003, tol);
        assertEquals(WGS84.getName() + " ArcCoeff2", WGS84.getArcCoeff()[2], 0.000002639110949, tol);
        assertEquals(WGS84.getName() + " ArcCoeff3", WGS84.getArcCoeff()[3], -0.000000003446648, tol);
        assertEquals(WGS84.getName() + " ArcCoeff4", WGS84.getArcCoeff()[4], 0.000000000004827, tol);
        assertEquals(WGS84.getName() + " KCoeff0", WGS84.getKCoeff(8)[0], -0.001675701568747, tol);
        assertEquals(WGS84.getName() + " KCoeff1", WGS84.getKCoeff(8)[1], -0.000002106571212, tol);
        assertEquals(WGS84.getName() + " KCoeff2", WGS84.getKCoeff(8)[2], -0.000000005881050, tol);
        assertEquals(WGS84.getName() + " KCoeff3", WGS84.getKCoeff(8)[3], -0.000000000021542, tol);
        assertEquals(WGS84.getName() + " KCoeff4", WGS84.getKCoeff(8)[4], -0.000000000000091, tol);
        assertEquals(WGS84.getName() + " KCoeff5", WGS84.getKCoeff(8)[5], 0, tol);
        assertEquals(WGS84.getName() + " KCoeff6", WGS84.getKCoeff(8)[6], 0, tol);
        assertEquals(WGS84.getName() + " KCoeff7", WGS84.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testINTERNATIONAL1924() {
        assertEquals(INTERNATIONAL1924.getName() + " Semi-major axis", INTERNATIONAL1924.getSemiMajorAxis(), 6378388, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Inverse Flattening", INTERNATIONAL1924.getInverseFlattening(), 297, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Semi-minor axis", INTERNATIONAL1924.getSemiMinorAxis(), 6356911.9461279465, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Flattening", INTERNATIONAL1924.getFlattening(), 0.003367003367003, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Eccentricity", INTERNATIONAL1924.getEccentricity(), 0.081991889979030, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Square eccentricity", INTERNATIONAL1924.getSquareEccentricity(), 0.006722670022333, tol);
        assertEquals(INTERNATIONAL1924.getName() + " Second eccentricity squared", INTERNATIONAL1924.getSecondEccentricitySquared(), 0.006768170197224, tol);
        assertEquals(INTERNATIONAL1924.getName() + " ArcCoeff0", INTERNATIONAL1924.getArcCoeff()[0], 0.998317208056044, tol);
        assertEquals(INTERNATIONAL1924.getName() + " ArcCoeff1", INTERNATIONAL1924.getArcCoeff()[1], -0.002525251627373, tol);
        assertEquals(INTERNATIONAL1924.getName() + " ArcCoeff2", INTERNATIONAL1924.getArcCoeff()[2], 0.000002661520252, tol);
        assertEquals(INTERNATIONAL1924.getName() + " ArcCoeff3", INTERNATIONAL1924.getArcCoeff()[3], -0.000000003490651, tol);
        assertEquals(INTERNATIONAL1924.getName() + " ArcCoeff4", INTERNATIONAL1924.getArcCoeff()[4], 0.000000000004909, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff0", INTERNATIONAL1924.getKCoeff(8)[0], -0.001682791944049, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff1", INTERNATIONAL1924.getKCoeff(8)[1], -0.000002124438465, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff2", INTERNATIONAL1924.getKCoeff(8)[2], -0.000000005956017, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff3", INTERNATIONAL1924.getKCoeff(8)[3], -0.000000000021909, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff4", INTERNATIONAL1924.getKCoeff(8)[4], -0.000000000000093, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff5", INTERNATIONAL1924.getKCoeff(8)[5], 0, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff6", INTERNATIONAL1924.getKCoeff(8)[6], 0, tol);
        assertEquals(INTERNATIONAL1924.getName() + " KCoeff7", INTERNATIONAL1924.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testBESSEL1841() {
        assertEquals(BESSEL1841.getName() + " Semi-major axis", BESSEL1841.getSemiMajorAxis(), 6377397.155, tol);
        assertEquals(BESSEL1841.getName() + " Inverse Flattening", BESSEL1841.getInverseFlattening(), 299.1528128, tol);
        assertEquals(BESSEL1841.getName() + " Semi-minor axis", BESSEL1841.getSemiMinorAxis(), 6356078.962818189, tol);
        assertEquals(BESSEL1841.getName() + " Flattening", BESSEL1841.getFlattening(), 0.003342773182175, tol);
        assertEquals(BESSEL1841.getName() + " Eccentricity", BESSEL1841.getEccentricity(), 0.081696831222528, tol);
        assertEquals(BESSEL1841.getName() + " Square eccentricity", BESSEL1841.getSquareEccentricity(), 0.006674372231802, tol);
        assertEquals(BESSEL1841.getName() + " Second eccentricity squared", BESSEL1841.getSecondEccentricitySquared(), 0.006719218799175, tol);
        assertEquals(BESSEL1841.getName() + " ArcCoeff0", BESSEL1841.getArcCoeff()[0], 0.998329312961632, tol);
        assertEquals(BESSEL1841.getName() + " ArcCoeff1", BESSEL1841.getArcCoeff()[1], -0.002507079008022, tol);
        assertEquals(BESSEL1841.getName() + " ArcCoeff2", BESSEL1841.getArcCoeff()[2], 0.000002623319743, tol);
        assertEquals(BESSEL1841.getName() + " ArcCoeff3", BESSEL1841.getArcCoeff()[3], -0.000000003415752, tol);
        assertEquals(BESSEL1841.getName() + " ArcCoeff4", BESSEL1841.getArcCoeff()[4], 0.000000000004769, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff0", BESSEL1841.getKCoeff(8)[0], -0.001670687038458, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff1", BESSEL1841.getKCoeff(8)[1], -0.000002093980507, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff2", BESSEL1841.getKCoeff(8)[2], -0.000000005828413, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff3", BESSEL1841.getKCoeff(8)[3], -0.000000000021286, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff4", BESSEL1841.getKCoeff(8)[4], -0.000000000000090, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff5", BESSEL1841.getKCoeff(8)[5], 0, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff6", BESSEL1841.getKCoeff(8)[6], 0, tol);
        assertEquals(BESSEL1841.getName() + " KCoeff7", BESSEL1841.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testCLARKE1866() {
        assertEquals(CLARKE1866.getName() + " Semi-major axis", CLARKE1866.getSemiMajorAxis(), 6378206.4, tol);
        assertEquals(CLARKE1866.getName() + " Inverse Flattening", CLARKE1866.getInverseFlattening(), 294.9786982138982, tol);
        assertEquals(CLARKE1866.getName() + " Semi-minor axis", CLARKE1866.getSemiMinorAxis(), 6356583.8, tol);
        assertEquals(CLARKE1866.getName() + " Flattening", CLARKE1866.getFlattening(), 0.003390075303929, tol);
        assertEquals(CLARKE1866.getName() + " Eccentricity", CLARKE1866.getEccentricity(), 0.082271854223004, tol);
        assertEquals(CLARKE1866.getName() + " Square eccentricity", CLARKE1866.getSquareEccentricity(), 0.006768657997291, tol);
        assertEquals(CLARKE1866.getName() + " Second eccentricity squared", CLARKE1866.getSecondEccentricitySquared(), 0.006814784945915, tol);
        assertEquals(CLARKE1866.getName() + " ArcCoeff0", CLARKE1866.getArcCoeff()[0], 0.998305681856014, tol);
        assertEquals(CLARKE1866.getName() + " ArcCoeff1", CLARKE1866.getArcCoeff()[1], -0.002542555561458, tol);
        assertEquals(CLARKE1866.getName() + " ArcCoeff2", CLARKE1866.getArcCoeff()[2], 0.000002698151786, tol);
        assertEquals(CLARKE1866.getName() + " ArcCoeff3", CLARKE1866.getArcCoeff()[3], -0.000000003562982, tol);
        assertEquals(CLARKE1866.getName() + " ArcCoeff4", CLARKE1866.getArcCoeff()[4], 0.000000000005044, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff0", CLARKE1866.getKCoeff(8)[0], -0.001694318144082, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff1", CLARKE1866.getKCoeff(8)[1], -0.000002153644759, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff2", CLARKE1866.getKCoeff(8)[2], -0.000000006079239, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff3", CLARKE1866.getKCoeff(8)[3], -0.000000000022516, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff4", CLARKE1866.getKCoeff(8)[4], -0.000000000000096, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff5", CLARKE1866.getKCoeff(8)[5], 0, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff6", CLARKE1866.getKCoeff(8)[6], 0, tol);
        assertEquals(CLARKE1866.getName() + " KCoeff7", CLARKE1866.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testCLARKE1880IGN() {
        assertEquals(CLARKE1880IGN.getName() + " Semi-major axis", CLARKE1880IGN.getSemiMajorAxis(), 6378249.2, tol);
        assertEquals(CLARKE1880IGN.getName() + " Inverse Flattening", CLARKE1880IGN.getInverseFlattening(), 293.4660212936269, tol);
        assertEquals(CLARKE1880IGN.getName() + " Semi-minor axis", CLARKE1880IGN.getSemiMinorAxis(), 6356515.0, tol);
        assertEquals(CLARKE1880IGN.getName() + " Flattening", CLARKE1880IGN.getFlattening(), 0.003407549520016, tol);
        assertEquals(CLARKE1880IGN.getName() + " Eccentricity", CLARKE1880IGN.getEccentricity(), 0.082483256763418, tol);
        assertEquals(CLARKE1880IGN.getName() + " Square eccentricity", CLARKE1880IGN.getSquareEccentricity(), 0.0068034876463, tol);
        assertEquals(CLARKE1880IGN.getName() + " Second eccentricity squared", CLARKE1880IGN.getSecondEccentricitySquared(), 0.006850092163712, tol);
        assertEquals(CLARKE1880IGN.getName() + " ArcCoeff0", CLARKE1880IGN.getArcCoeff()[0], 0.998296952190891, tol);
        assertEquals(CLARKE1880IGN.getName() + " ArcCoeff1", CLARKE1880IGN.getArcCoeff()[1], -0.002555661209260, tol);
        assertEquals(CLARKE1880IGN.getName() + " ArcCoeff2", CLARKE1880IGN.getArcCoeff()[2], 0.000002726062669, tol);
        assertEquals(CLARKE1880IGN.getName() + " ArcCoeff3", CLARKE1880IGN.getArcCoeff()[3], -0.000000003618424, tol);
        assertEquals(CLARKE1880IGN.getName() + " ArcCoeff4", CLARKE1880IGN.getArcCoeff()[4], 0.000000000005149, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff0", CLARKE1880IGN.getKCoeff(8)[0], -0.001703047809207, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff1", CLARKE1880IGN.getKCoeff(8)[1], -0.000002175897632, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff2", CLARKE1880IGN.getKCoeff(8)[2], -0.000000006173687, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff3", CLARKE1880IGN.getKCoeff(8)[3], -0.000000000022983, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff4", CLARKE1880IGN.getKCoeff(8)[4], -0.000000000000099, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff5", CLARKE1880IGN.getKCoeff(8)[5], 0, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff6", CLARKE1880IGN.getKCoeff(8)[6], 0, tol);
        assertEquals(CLARKE1880IGN.getName() + " KCoeff7", CLARKE1880IGN.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testCLARKE1880RGS() {
        assertEquals(CLARKE1880RGS.getName() + " Semi-major axis", CLARKE1880RGS.getSemiMajorAxis(), 6378249.2, tol);
        assertEquals(CLARKE1880RGS.getName() + " Inverse Flattening", CLARKE1880RGS.getInverseFlattening(), 293.465, tol);
        assertEquals(CLARKE1880RGS.getName() + " Semi-minor axis", CLARKE1880RGS.getSemiMinorAxis(), 6356514.9243623605, tol);
        assertEquals(CLARKE1880RGS.getName() + " Flattening", CLARKE1880RGS.getFlattening(), 0.003407561378699, tol);
        assertEquals(CLARKE1880RGS.getName() + " Eccentricity", CLARKE1880RGS.getEccentricity(), 0.082483400044185, tol);
        assertEquals(CLARKE1880RGS.getName() + " Square eccentricity", CLARKE1880RGS.getSquareEccentricity(), 0.006803511282849, tol);
        assertEquals(CLARKE1880RGS.getName() + " Second eccentricity squared", CLARKE1880RGS.getSecondEccentricitySquared(), 0.006850116125196, tol);
        assertEquals(CLARKE1880RGS.getName() + " ArcCoeff0", CLARKE1880RGS.getArcCoeff()[0], 0.998296946266614, tol);
        assertEquals(CLARKE1880RGS.getName() + " ArcCoeff1", CLARKE1880RGS.getArcCoeff()[1], -0.002555670103263, tol);
        assertEquals(CLARKE1880RGS.getName() + " ArcCoeff2", CLARKE1880RGS.getArcCoeff()[2], 0.000002726081660, tol);
        assertEquals(CLARKE1880RGS.getName() + " ArcCoeff3", CLARKE1880RGS.getArcCoeff()[3], -0.000000003618461, tol);
        assertEquals(CLARKE1880RGS.getName() + " ArcCoeff4", CLARKE1880RGS.getArcCoeff()[4], 0.000000000005149, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff0", CLARKE1880RGS.getKCoeff(8)[0], -0.001703053733485, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff1", CLARKE1880RGS.getKCoeff(8)[1], -0.000002175912773, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff2", CLARKE1880RGS.getKCoeff(8)[2], -0.000000006173752, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff3", CLARKE1880RGS.getKCoeff(8)[3], -0.000000000022984, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff4", CLARKE1880RGS.getKCoeff(8)[4], -0.000000000000099, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff5", CLARKE1880RGS.getKCoeff(8)[5], 0, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff6", CLARKE1880RGS.getKCoeff(8)[6], 0, tol);
        assertEquals(CLARKE1880RGS.getName() + " KCoeff7", CLARKE1880RGS.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testCLARKE1880ARC() {
        assertEquals(CLARKE1880ARC.getName() + " Semi-major axis", CLARKE1880ARC.getSemiMajorAxis(), 6378249.145, tol);
        assertEquals(CLARKE1880ARC.getName() + " Inverse Flattening", CLARKE1880ARC.getInverseFlattening(), 293.4663077, tol);
        assertEquals(CLARKE1880ARC.getName() + " Semi-minor axis", CLARKE1880ARC.getSemiMinorAxis(), 6356514.966398753, tol);
        assertEquals(CLARKE1880ARC.getName() + " Flattening", CLARKE1880ARC.getFlattening(), 0.003407546194442, tol);
        assertEquals(CLARKE1880ARC.getName() + " Eccentricity", CLARKE1880ARC.getEccentricity(), 0.082483216582625, tol);
        assertEquals(CLARKE1880ARC.getName() + " Square eccentricity", CLARKE1880ARC.getSquareEccentricity(), 0.006803481017816, tol);
        assertEquals(CLARKE1880ARC.getName() + " Second eccentricity squared", CLARKE1880ARC.getSecondEccentricitySquared(), 0.006850085444106, tol);
        assertEquals(CLARKE1880ARC.getName() + " ArcCoeff0", CLARKE1880ARC.getArcCoeff()[0], 0.998296953852258, tol);
        assertEquals(CLARKE1880ARC.getName() + " ArcCoeff1", CLARKE1880ARC.getArcCoeff()[1], -0.002555658715082, tol);
        assertEquals(CLARKE1880ARC.getName() + " ArcCoeff2", CLARKE1880ARC.getArcCoeff()[2], 0.000002726057344, tol);
        assertEquals(CLARKE1880ARC.getName() + " ArcCoeff3", CLARKE1880ARC.getArcCoeff()[3], -0.000000003618413, tol);
        assertEquals(CLARKE1880ARC.getName() + " ArcCoeff4", CLARKE1880ARC.getArcCoeff()[4], 0.000000000005149, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff0", CLARKE1880ARC.getKCoeff(8)[0], -0.001703046147840, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff1", CLARKE1880ARC.getKCoeff(8)[1], -0.000002175893386, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff2", CLARKE1880ARC.getKCoeff(8)[2], -0.000000006173669, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff3", CLARKE1880ARC.getKCoeff(8)[3], -0.000000000022983, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff4", CLARKE1880ARC.getKCoeff(8)[4], -0.000000000000099, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff5", CLARKE1880ARC.getKCoeff(8)[5], 0, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff6", CLARKE1880ARC.getKCoeff(8)[6], 0, tol);
        assertEquals(CLARKE1880ARC.getName() + " KCoeff7", CLARKE1880ARC.getKCoeff(8)[7], 0, tol);
    }

    @Test
    public void testKRASSOWSKI() {
        assertEquals(KRASSOWSKI.getName() + " Semi-major axis", KRASSOWSKI.getSemiMajorAxis(), 6378245.0, tol);
        assertEquals(KRASSOWSKI.getName() + " Inverse Flattening", KRASSOWSKI.getInverseFlattening(), 298.3, tol);
        assertEquals(KRASSOWSKI.getName() + " Semi-minor axis", KRASSOWSKI.getSemiMinorAxis(), 6356863.018773047, tol);
        assertEquals(KRASSOWSKI.getName() + " Flattening", KRASSOWSKI.getFlattening(), 0.003352329869259, tol);
        assertEquals(KRASSOWSKI.getName() + " Eccentricity", KRASSOWSKI.getEccentricity(), 0.081813334016931, tol);
        assertEquals(KRASSOWSKI.getName() + " Square eccentricity", KRASSOWSKI.getSquareEccentricity(), 0.006693421622966, tol);
        assertEquals(KRASSOWSKI.getName() + " Second eccentricity squared", KRASSOWSKI.getSecondEccentricitySquared(), 0.006738525414684, tol);
        assertEquals(KRASSOWSKI.getName() + " ArcCoeff0", KRASSOWSKI.getArcCoeff()[0], 0.998324538627092, tol);
        assertEquals(KRASSOWSKI.getName() + " ArcCoeff1", KRASSOWSKI.getArcCoeff()[1], -0.002514246515768, tol);
        assertEquals(KRASSOWSKI.getName() + " ArcCoeff2", KRASSOWSKI.getArcCoeff()[2], 0.000002638353468, tol);
        assertEquals(KRASSOWSKI.getName() + " ArcCoeff3", KRASSOWSKI.getArcCoeff()[3], -0.000000003445164, tol);
        assertEquals(KRASSOWSKI.getName() + " ArcCoeff4", KRASSOWSKI.getArcCoeff()[4], 0.000000000004824, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff0", KRASSOWSKI.getKCoeff(8)[0], -0.001675461372998, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff1", KRASSOWSKI.getKCoeff(8)[1], -0.000002105967257, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff2", KRASSOWSKI.getKCoeff(8)[2], -0.000000005878522, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff3", KRASSOWSKI.getKCoeff(8)[3], -0.000000000021530, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff4", KRASSOWSKI.getKCoeff(8)[4], -0.000000000000091, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff5", KRASSOWSKI.getKCoeff(8)[5], 0, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff6", KRASSOWSKI.getKCoeff(8)[6], 0, tol);
        assertEquals(KRASSOWSKI.getName() + " KCoeff7", KRASSOWSKI.getKCoeff(8)[7], 0, tol);
    }
    Ellipsoid eTest = createEllipsoidFromEccentricity(6380000, 0.08199188998);

    /*
     * Test isometricLatitudeTest() based on the algorithm "ALG0001" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    public void isometricLatitudeTest() {
        assertEquals("isometricLatitude test 1", eTest.isometricLatitude(0.872664626), 1.00552653649, 1e-11);
        assertEquals("isometricLatitude test 2", eTest.isometricLatitude(-0.3), -0.30261690063, 1e-11);
        assertEquals("isometricLatitude test 3", eTest.isometricLatitude(0.19998903370), 0.200000000009, 1e-11);
        assertEquals("isometricLatitude equator", eTest.isometricLatitude(0), 0, 1e-11);
    }

    /*
     * Test curvilinearAbscissaTest() based on the algorithm "ALG0002" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    public void latitudeTest() {
        assertEquals("latitude test 1", eTest.latitude(1.00552653648), 0.872664626, 1e-11);
        assertEquals("latitude test 2", eTest.latitude(-0.30261690060), -0.29999999997, 1e-11);
        assertEquals("latitude test 3", eTest.latitude(0.2), 0.19998903369, 1e-11);
        assertEquals("latitude equator", eTest.latitude(0), 0, 1e-11);
        assertEquals("latitude pole", eTest.latitude(Double.POSITIVE_INFINITY), Math.PI / 2, 1e-11);
    }
    Ellipsoid eTest2 = createEllipsoidFromEccentricity(6378388, 0.08199189);

    /*
     * Test transverseRadiusOfCurvatureTest() based on the algorithm "ALG0021" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    public void transverseRadiusOfCurvatureTest() {
        assertEquals("transverseRadiusOfCurvature test 1", eTest2.transverseRadiusOfCurvature(0.977384381), 6393174.9755, 1e-4);
        assertEquals("transverseRadiusOfCurvature equator", eTest2.transverseRadiusOfCurvature(0), 6378388, 1e-4);
        assertEquals("transverseRadiusOfCurvature pole", eTest2.transverseRadiusOfCurvature(Math.PI / 2), 6399936.6081, 1e-4);
    }

    /*
     * Test curvilinearAbscissaTest() based on the algorithm "ALG0026" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_76.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    public void curvilinearAbscissaTest() {
        Ellipsoid eTestLocal = createEllipsoidFromEccentricity(6378388, 0.081819191043);
        assertEquals("curvilinearAbscissa test 1", eTest.curvilinearAbscissa(0.78539816340), 0.781551253561, 1e-12);
        assertEquals("curvilinearAbscissa pole", eTestLocal.curvilinearAbscissa(1.57079632679), 1.568164140908, 1e-12);
        assertEquals("curvilinearAbscissa equator", eTestLocal.curvilinearAbscissa(0), 0, 1e-12);
    }

    @Test
    public void meridionalRadiusOfCurvatureTest() {
        assertEquals("meridionalRadiusOfCurvature test 1", eTest2.meridionalRadiusOfCurvature(0.977384381), 6379673.1341, 1e-4);
        assertEquals("meridionalRadiusOfCurvature equator", eTest2.meridionalRadiusOfCurvature(0), 6335508.2022, 1e-4);
        assertEquals("meridionalRadiusOfCurvature pole", eTest2.meridionalRadiusOfCurvature(Math.PI / 2), 6399936.6081, 1e-4);
    }

    @Test
    public void arcFromLatTest() {
        assertEquals("arcFromLatTest test 1", eTest2.arcFromLat(Math.PI / 3), 6654228.3963, 1e-4);
        assertEquals("arcFromLatTest test 2", eTest2.arcFromLat(Math.PI / 7), 2845220.2110, 1e-4);
        assertEquals("arcFromLatTest equator", eTest2.arcFromLat(0), 0, 1e-11);
    }

    @Test
    public void latFromArcTest() {
        assertEquals("latFromArcTest test 1", eTest2.latFromArc(6654228.3963), Math.PI / 3, 1e-11);
        assertEquals("latFromArcTest test 2", eTest2.latFromArc(2845220.2110), Math.PI / 7, 1e-11);
        assertEquals("latFromArcTest equator", eTest2.latFromArc(0), 0, 1e-11);
    }
}
