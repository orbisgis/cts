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
import org.junit.jupiter.api.Test;

import static org.cts.datum.Ellipsoid.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Jules Party
 */
class EllipsoidTest extends CTSTestCase {

    @Test
    void testEllipsoidConstruction() {
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
        assertEquals(e1.toString(), "[Test:0001] Construction from Semi-minor axis "
                + "(Semi-major axis = 6380000.0 | Semi-minor axis = 6350000.0)");
        assertEquals(e2.toString(), "[Test:0002] Construction from Inverse flattening "
                + "(Semi-major axis = 6380000.0 | Flattening = 1/300.0)");
        assertEquals(e3.toString(), "[Test:0003] Construction from Eccentricity "
                + "(Semi-major axis = 6380000.0 | Eccentricity = 0.1)");
    }
    private final double tol = 1E-15;

    @Test
    void testSPHERE() {
        assertEquals(SPHERE.getSemiMajorAxis(), 6371000.0, tol, SPHERE.getName() + " Semi-major axis");
        assertEquals(SPHERE.getInverseFlattening(), Double.POSITIVE_INFINITY, tol, SPHERE.getName() + " Inverse Flattening");
        assertEquals(SPHERE.getSemiMinorAxis(), 6371000.0, tol, SPHERE.getName() + " Semi-minor axis");
        assertEquals(SPHERE.getFlattening(), 0, tol, SPHERE.getName() + " Flattening");
        assertEquals(SPHERE.getEccentricity(), 0, tol, SPHERE.getName() + " Eccentricity");
        assertEquals(SPHERE.getSquareEccentricity(), 0, tol, SPHERE.getName() + " Square eccentricity");
        assertEquals(SPHERE.getSecondEccentricitySquared(), 0, tol, SPHERE.getName() + " Second eccentricity squared");
        assertEquals(SPHERE.getArcCoeff()[0], 1, tol, SPHERE.getName() + " ArcCoeff0");
        assertEquals(SPHERE.getArcCoeff()[1], 0, tol, SPHERE.getName() + " ArcCoeff1");
        assertEquals(SPHERE.getArcCoeff()[2], 0, tol, SPHERE.getName() + " ArcCoeff2");
        assertEquals(SPHERE.getArcCoeff()[3], 0, tol, SPHERE.getName() + " ArcCoeff3");
        assertEquals(SPHERE.getArcCoeff()[4], 0, tol, SPHERE.getName() + " ArcCoeff4");
        assertEquals(SPHERE.getKCoeff(8)[0], 0, tol, SPHERE.getName() + " KCoeff0");
        assertEquals(SPHERE.getKCoeff(8)[1], 0, tol, SPHERE.getName() + " KCoeff1");
        assertEquals(SPHERE.getKCoeff(8)[2], 0, tol, SPHERE.getName() + " KCoeff2");
        assertEquals(SPHERE.getKCoeff(8)[3], 0, tol, SPHERE.getName() + " KCoeff3");
        assertEquals(SPHERE.getKCoeff(8)[4], 0, tol, SPHERE.getName() + " KCoeff4");
        assertEquals(SPHERE.getKCoeff(8)[5], 0, tol, SPHERE.getName() + " KCoeff5");
        assertEquals(SPHERE.getKCoeff(8)[6], 0, tol, SPHERE.getName() + " KCoeff6");
        assertEquals(SPHERE.getKCoeff(8)[7], 0, tol, SPHERE.getName() + " KCoeff7");
    }

    @Test
    void testGRS80() {
        assertEquals(GRS80.getSemiMajorAxis(), 6378137.0, tol, GRS80.getName() + " Semi-major axis");
        assertEquals(GRS80.getInverseFlattening(), 298.257222101, tol, GRS80.getName() + " Inverse Flattening");
        assertEquals(GRS80.getSemiMinorAxis(), 6356752.314140356, tol, GRS80.getName() + " Semi-minor axis");
        assertEquals(GRS80.getFlattening(), 0.003352810681182, tol, GRS80.getName() + " Flattening");
        assertEquals(GRS80.getEccentricity(), 0.081819191042816, tol, GRS80.getName() + " Eccentricity");
        assertEquals(GRS80.getSquareEccentricity(), 0.006694380022901, tol, GRS80.getName() + " Square eccentricity");
        assertEquals(GRS80.getSecondEccentricitySquared(), 0.006739496775479, tol, GRS80.getName() + " Second eccentricity squared");
        assertEquals(GRS80.getArcCoeff()[0], 0.998324298423133, tol, GRS80.getName() + " ArcCoeff0");
        assertEquals(GRS80.getArcCoeff()[1], -0.002514607124329, tol, GRS80.getName() + " ArcCoeff1");
        assertEquals(GRS80.getArcCoeff()[2], 0.000002639110975, tol, GRS80.getName() + " ArcCoeff2");
        assertEquals(GRS80.getArcCoeff()[3], -0.000000003446648, tol, GRS80.getName() + " ArcCoeff3");
        assertEquals(GRS80.getArcCoeff()[4], 0.000000000004827, tol, GRS80.getName() + " ArcCoeff4");
        assertEquals(GRS80.getKCoeff(8)[0], -0.001675701576958, tol, GRS80.getName() + " KCoeff0");
        assertEquals(GRS80.getKCoeff(8)[1], -0.000002106571233, tol, GRS80.getName() + " KCoeff1");
        assertEquals(GRS80.getKCoeff(8)[2], -0.000000005881050, tol, GRS80.getName() + " KCoeff2");
        assertEquals(GRS80.getKCoeff(8)[3], -0.000000000021542, tol, GRS80.getName() + " KCoeff3");
        assertEquals(GRS80.getKCoeff(8)[4], -0.000000000000091, tol, GRS80.getName() + " KCoeff4");
        assertEquals(GRS80.getKCoeff(8)[5], 0, tol, GRS80.getName() + " KCoeff5");
        assertEquals(GRS80.getKCoeff(8)[6], 0, tol, GRS80.getName() + " KCoeff6");
        assertEquals(GRS80.getKCoeff(8)[7], 0, tol, GRS80.getName() + " KCoeff7");
    }

    @Test
    void testWGS84() {
        assertEquals(WGS84.getSemiMajorAxis(), 6378137.0, tol, WGS84.getName() + " Semi-major axis");
        assertEquals(WGS84.getInverseFlattening(), 298.257223563, tol, WGS84.getName() + " Inverse Flattening");
        assertEquals(WGS84.getSemiMinorAxis(), 6356752.314245179, tol, WGS84.getName() + " Semi-minor axis");
        assertEquals(WGS84.getFlattening(), 0.003352810664747, tol, WGS84.getName() + " Flattening");
        assertEquals(WGS84.getEccentricity(), 0.081819190842622, tol, WGS84.getName() + " Eccentricity");
        assertEquals(WGS84.getSquareEccentricity(), 0.006694379990141, tol, WGS84.getName() + " Square eccentricity");
        assertEquals(WGS84.getSecondEccentricitySquared(), 0.006739496742276, tol, WGS84.getName() + " Second eccentricity squared");
        assertEquals(WGS84.getArcCoeff()[0], 0.998324298431344, tol, WGS84.getName() + " ArcCoeff0");
        assertEquals(WGS84.getArcCoeff()[1], -0.002514607112003, tol, WGS84.getName() + " ArcCoeff1");
        assertEquals(WGS84.getArcCoeff()[2], 0.000002639110949, tol, WGS84.getName() + " ArcCoeff2");
        assertEquals(WGS84.getArcCoeff()[3], -0.000000003446648, tol, WGS84.getName() + " ArcCoeff3");
        assertEquals(WGS84.getArcCoeff()[4], 0.000000000004827, tol, WGS84.getName() + " ArcCoeff4");
        assertEquals(WGS84.getKCoeff(8)[0], -0.001675701568747, tol, WGS84.getName() + " KCoeff0");
        assertEquals(WGS84.getKCoeff(8)[1], -0.000002106571212, tol, WGS84.getName() + " KCoeff1");
        assertEquals(WGS84.getKCoeff(8)[2], -0.000000005881050, tol, WGS84.getName() + " KCoeff2");
        assertEquals(WGS84.getKCoeff(8)[3], -0.000000000021542, tol, WGS84.getName() + " KCoeff3");
        assertEquals(WGS84.getKCoeff(8)[4], -0.000000000000091, tol, WGS84.getName() + " KCoeff4");
        assertEquals(WGS84.getKCoeff(8)[5], 0, tol, WGS84.getName() + " KCoeff5");
        assertEquals(WGS84.getKCoeff(8)[6], 0, tol, WGS84.getName() + " KCoeff6");
        assertEquals(WGS84.getKCoeff(8)[7], 0, tol, WGS84.getName() + " KCoeff7");
    }

    @Test
    void testINTERNATIONAL1924() {
        assertEquals(INTERNATIONAL1924.getSemiMajorAxis(), 6378388, tol, INTERNATIONAL1924.getName() + " Semi-major axis");
        assertEquals(INTERNATIONAL1924.getInverseFlattening(), 297, tol, INTERNATIONAL1924.getName() + " Inverse Flattening");
        assertEquals(INTERNATIONAL1924.getSemiMinorAxis(), 6356911.9461279465, tol, INTERNATIONAL1924.getName() + " Semi-minor axis");
        assertEquals(INTERNATIONAL1924.getFlattening(), 0.003367003367003, tol, INTERNATIONAL1924.getName() + " Flattening");
        assertEquals(INTERNATIONAL1924.getEccentricity(), 0.081991889979030, tol, INTERNATIONAL1924.getName() + " Eccentricity");
        assertEquals(INTERNATIONAL1924.getSquareEccentricity(), 0.006722670022333, tol, INTERNATIONAL1924.getName() + " Square eccentricity");
        assertEquals(INTERNATIONAL1924.getSecondEccentricitySquared(), 0.006768170197224, tol, INTERNATIONAL1924.getName() + " Second eccentricity squared");
        assertEquals(INTERNATIONAL1924.getArcCoeff()[0], 0.998317208056044, tol, INTERNATIONAL1924.getName() + " ArcCoeff0");
        assertEquals(INTERNATIONAL1924.getArcCoeff()[1], -0.002525251627373, tol, INTERNATIONAL1924.getName() + " ArcCoeff1");
        assertEquals(INTERNATIONAL1924.getArcCoeff()[2], 0.000002661520252, tol, INTERNATIONAL1924.getName() + " ArcCoeff2");
        assertEquals(INTERNATIONAL1924.getArcCoeff()[3], -0.000000003490651, tol, INTERNATIONAL1924.getName() + " ArcCoeff3");
        assertEquals(INTERNATIONAL1924.getArcCoeff()[4], 0.000000000004909, tol, INTERNATIONAL1924.getName() + " ArcCoeff4");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[0], -0.001682791944049, tol, INTERNATIONAL1924.getName() + " KCoeff0");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[1], -0.000002124438465, tol, INTERNATIONAL1924.getName() + " KCoeff1");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[2], -0.000000005956017, tol, INTERNATIONAL1924.getName() + " KCoeff2");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[3], -0.000000000021909, tol, INTERNATIONAL1924.getName() + " KCoeff3");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[4], -0.000000000000093, tol, INTERNATIONAL1924.getName() + " KCoeff4");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[5], 0, tol, INTERNATIONAL1924.getName() + " KCoeff5");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[6], 0, tol, INTERNATIONAL1924.getName() + " KCoeff6");
        assertEquals(INTERNATIONAL1924.getKCoeff(8)[7], 0, tol, INTERNATIONAL1924.getName() + " KCoeff7");
    }

    @Test
    void testBESSEL1841() {
        assertEquals(BESSEL1841.getSemiMajorAxis(), 6377397.155, tol, BESSEL1841.getName() + " Semi-major axis");
        assertEquals(BESSEL1841.getInverseFlattening(), 299.1528128, tol, BESSEL1841.getName() + " Inverse Flattening");
        assertEquals(BESSEL1841.getSemiMinorAxis(), 6356078.962818189, tol, BESSEL1841.getName() + " Semi-minor axis");
        assertEquals(BESSEL1841.getFlattening(), 0.003342773182175, tol, BESSEL1841.getName() + " Flattening");
        assertEquals(BESSEL1841.getEccentricity(), 0.081696831222528, tol, BESSEL1841.getName() + " Eccentricity");
        assertEquals(BESSEL1841.getSquareEccentricity(), 0.006674372231802, tol, BESSEL1841.getName() + " Square eccentricity");
        assertEquals(BESSEL1841.getSecondEccentricitySquared(), 0.006719218799175, tol, BESSEL1841.getName() + " Second eccentricity squared");
        assertEquals(BESSEL1841.getArcCoeff()[0], 0.998329312961632, tol, BESSEL1841.getName() + " ArcCoeff0");
        assertEquals(BESSEL1841.getArcCoeff()[1], -0.002507079008022, tol, BESSEL1841.getName() + " ArcCoeff1");
        assertEquals(BESSEL1841.getArcCoeff()[2], 0.000002623319743, tol, BESSEL1841.getName() + " ArcCoeff2");
        assertEquals(BESSEL1841.getArcCoeff()[3], -0.000000003415752, tol, BESSEL1841.getName() + " ArcCoeff3");
        assertEquals(BESSEL1841.getArcCoeff()[4], 0.000000000004769, tol, BESSEL1841.getName() + " ArcCoeff4");
        assertEquals(BESSEL1841.getKCoeff(8)[0], -0.001670687038458, tol, BESSEL1841.getName() + " KCoeff0");
        assertEquals(BESSEL1841.getKCoeff(8)[1], -0.000002093980507, tol, BESSEL1841.getName() + " KCoeff1");
        assertEquals(BESSEL1841.getKCoeff(8)[2], -0.000000005828413, tol, BESSEL1841.getName() + " KCoeff2");
        assertEquals(BESSEL1841.getKCoeff(8)[3], -0.000000000021286, tol, BESSEL1841.getName() + " KCoeff3");
        assertEquals(BESSEL1841.getKCoeff(8)[4], -0.000000000000090, tol, BESSEL1841.getName() + " KCoeff4");
        assertEquals(BESSEL1841.getKCoeff(8)[5], 0, tol, BESSEL1841.getName() + " KCoeff5");
        assertEquals(BESSEL1841.getKCoeff(8)[6], 0, tol, BESSEL1841.getName() + " KCoeff6");
        assertEquals(BESSEL1841.getKCoeff(8)[7], 0, tol, BESSEL1841.getName() + " KCoeff7");
    }

    @Test
    void testCLARKE1866() {
        assertEquals(CLARKE1866.getSemiMajorAxis(), 6378206.4, tol, CLARKE1866.getName() + " Semi-major axis");
        assertEquals(CLARKE1866.getInverseFlattening(), 294.9786982138982, tol, CLARKE1866.getName() + " Inverse Flattening");
        assertEquals(CLARKE1866.getSemiMinorAxis(), 6356583.8, tol, CLARKE1866.getName() + " Semi-minor axis");
        assertEquals(CLARKE1866.getFlattening(), 0.003390075303929, tol, CLARKE1866.getName() + " Flattening");
        assertEquals(CLARKE1866.getEccentricity(), 0.082271854223004, tol, CLARKE1866.getName() + " Eccentricity");
        assertEquals(CLARKE1866.getSquareEccentricity(), 0.006768657997291, tol, CLARKE1866.getName() + " Square eccentricity");
        assertEquals(CLARKE1866.getSecondEccentricitySquared(), 0.006814784945915, tol, CLARKE1866.getName() + " Second eccentricity squared");
        assertEquals(CLARKE1866.getArcCoeff()[0], 0.998305681856014, tol, CLARKE1866.getName() + " ArcCoeff0");
        assertEquals(CLARKE1866.getArcCoeff()[1], -0.002542555561458, tol, CLARKE1866.getName() + " ArcCoeff1");
        assertEquals(CLARKE1866.getArcCoeff()[2], 0.000002698151786, tol, CLARKE1866.getName() + " ArcCoeff2");
        assertEquals(CLARKE1866.getArcCoeff()[3], -0.000000003562982, tol, CLARKE1866.getName() + " ArcCoeff3");
        assertEquals(CLARKE1866.getArcCoeff()[4], 0.000000000005044, tol, CLARKE1866.getName() + " ArcCoeff4");
        assertEquals(CLARKE1866.getKCoeff(8)[0], -0.001694318144082, tol, CLARKE1866.getName() + " KCoeff0");
        assertEquals(CLARKE1866.getKCoeff(8)[1], -0.000002153644759, tol, CLARKE1866.getName() + " KCoeff1");
        assertEquals(CLARKE1866.getKCoeff(8)[2], -0.000000006079239, tol, CLARKE1866.getName() + " KCoeff2");
        assertEquals(CLARKE1866.getKCoeff(8)[3], -0.000000000022516, tol, CLARKE1866.getName() + " KCoeff3");
        assertEquals(CLARKE1866.getKCoeff(8)[4], -0.000000000000096, tol, CLARKE1866.getName() + " KCoeff4");
        assertEquals(CLARKE1866.getKCoeff(8)[5], 0, tol, CLARKE1866.getName() + " KCoeff5");
        assertEquals(CLARKE1866.getKCoeff(8)[6], 0, tol, CLARKE1866.getName() + " KCoeff6");
        assertEquals(CLARKE1866.getKCoeff(8)[7], 0, tol, CLARKE1866.getName() + " KCoeff7");
    }

    @Test
    void testCLARKE1880IGN() {
        assertEquals(CLARKE1880IGN.getSemiMajorAxis(), 6378249.2, tol, CLARKE1880IGN.getName() + " Semi-major axis");
        assertEquals(CLARKE1880IGN.getInverseFlattening(), 293.4660212936269, tol, CLARKE1880IGN.getName() + " Inverse Flattening");
        assertEquals(CLARKE1880IGN.getSemiMinorAxis(), 6356515.0, tol, CLARKE1880IGN.getName() + " Semi-minor axis");
        assertEquals(CLARKE1880IGN.getFlattening(), 0.003407549520016, tol, CLARKE1880IGN.getName() + " Flattening");
        assertEquals(CLARKE1880IGN.getEccentricity(), 0.082483256763418, tol, CLARKE1880IGN.getName() + " Eccentricity");
        assertEquals(CLARKE1880IGN.getSquareEccentricity(), 0.0068034876463, tol, CLARKE1880IGN.getName() + " Square eccentricity");
        assertEquals(CLARKE1880IGN.getSecondEccentricitySquared(), 0.006850092163712, tol, CLARKE1880IGN.getName() + " Second eccentricity squared");
        assertEquals(CLARKE1880IGN.getArcCoeff()[0], 0.998296952190891, tol, CLARKE1880IGN.getName() + " ArcCoeff0");
        assertEquals(CLARKE1880IGN.getArcCoeff()[1], -0.002555661209260, tol, CLARKE1880IGN.getName() + " ArcCoeff1");
        assertEquals(CLARKE1880IGN.getArcCoeff()[2], 0.000002726062669, tol, CLARKE1880IGN.getName() + " ArcCoeff2");
        assertEquals(CLARKE1880IGN.getArcCoeff()[3], -0.000000003618424, tol, CLARKE1880IGN.getName() + " ArcCoeff3");
        assertEquals(CLARKE1880IGN.getArcCoeff()[4], 0.000000000005149, tol, CLARKE1880IGN.getName() + " ArcCoeff4");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[0], -0.001703047809207, tol, CLARKE1880IGN.getName() + " KCoeff0");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[1], -0.000002175897632, tol, CLARKE1880IGN.getName() + " KCoeff1");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[2], -0.000000006173687, tol, CLARKE1880IGN.getName() + " KCoeff2");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[3], -0.000000000022983, tol, CLARKE1880IGN.getName() + " KCoeff3");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[4], -0.000000000000099, tol, CLARKE1880IGN.getName() + " KCoeff4");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[5], 0, tol, CLARKE1880IGN.getName() + " KCoeff5");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[6], 0, tol, CLARKE1880IGN.getName() + " KCoeff6");
        assertEquals(CLARKE1880IGN.getKCoeff(8)[7], 0, tol, CLARKE1880IGN.getName() + " KCoeff7");
    }

    @Test
    void testCLARKE1880RGS() {
        assertEquals(CLARKE1880RGS.getSemiMajorAxis(), 6378249.2, tol, CLARKE1880RGS.getName() + " Semi-major axis");
        assertEquals(CLARKE1880RGS.getInverseFlattening(), 293.465, tol, CLARKE1880RGS.getName() + " Inverse Flattening");
        assertEquals(CLARKE1880RGS.getSemiMinorAxis(), 6356514.9243623605, tol, CLARKE1880RGS.getName() + " Semi-minor axis");
        assertEquals(CLARKE1880RGS.getFlattening(), 0.003407561378699, tol, CLARKE1880RGS.getName() + " Flattening");
        assertEquals(CLARKE1880RGS.getEccentricity(), 0.082483400044185, tol, CLARKE1880RGS.getName() + " Eccentricity");
        assertEquals(CLARKE1880RGS.getSquareEccentricity(), 0.006803511282849, tol, CLARKE1880RGS.getName() + " Square eccentricity");
        assertEquals(CLARKE1880RGS.getSecondEccentricitySquared(), 0.006850116125196, tol, CLARKE1880RGS.getName() + " Second eccentricity squared");
        assertEquals(CLARKE1880RGS.getArcCoeff()[0], 0.998296946266614, tol, CLARKE1880RGS.getName() + " ArcCoeff0");
        assertEquals(CLARKE1880RGS.getArcCoeff()[1], -0.002555670103263, tol, CLARKE1880RGS.getName() + " ArcCoeff1");
        assertEquals(CLARKE1880RGS.getArcCoeff()[2], 0.000002726081660, tol, CLARKE1880RGS.getName() + " ArcCoeff2");
        assertEquals(CLARKE1880RGS.getArcCoeff()[3], -0.000000003618461, tol, CLARKE1880RGS.getName() + " ArcCoeff3");
        assertEquals(CLARKE1880RGS.getArcCoeff()[4], 0.000000000005149, tol, CLARKE1880RGS.getName() + " ArcCoeff4");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[0], -0.001703053733485, tol, CLARKE1880RGS.getName() + " KCoeff0");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[1], -0.000002175912773, tol, CLARKE1880RGS.getName() + " KCoeff1");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[2], -0.000000006173752, tol, CLARKE1880RGS.getName() + " KCoeff2");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[3], -0.000000000022984, tol, CLARKE1880RGS.getName() + " KCoeff3");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[4], -0.000000000000099, tol, CLARKE1880RGS.getName() + " KCoeff4");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[5], 0, tol, CLARKE1880RGS.getName() + " KCoeff5");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[6], 0, tol, CLARKE1880RGS.getName() + " KCoeff6");
        assertEquals(CLARKE1880RGS.getKCoeff(8)[7], 0, tol, CLARKE1880RGS.getName() + " KCoeff7");
    }

    @Test
    void testCLARKE1880ARC() {
        assertEquals(CLARKE1880ARC.getSemiMajorAxis(), 6378249.145, tol, CLARKE1880ARC.getName() + " Semi-major axis");
        assertEquals(CLARKE1880ARC.getInverseFlattening(), 293.4663077, tol, CLARKE1880ARC.getName() + " Inverse Flattening");
        assertEquals(CLARKE1880ARC.getSemiMinorAxis(), 6356514.966398753, tol, CLARKE1880ARC.getName() + " Semi-minor axis");
        assertEquals(CLARKE1880ARC.getFlattening(), 0.003407546194442, tol, CLARKE1880ARC.getName() + " Flattening");
        assertEquals(CLARKE1880ARC.getEccentricity(), 0.082483216582625, tol, CLARKE1880ARC.getName() + " Eccentricity");
        assertEquals(CLARKE1880ARC.getSquareEccentricity(), 0.006803481017816, tol, CLARKE1880ARC.getName() + " Square eccentricity");
        assertEquals(CLARKE1880ARC.getSecondEccentricitySquared(), 0.006850085444106, tol, CLARKE1880ARC.getName() + " Second eccentricity squared");
        assertEquals(CLARKE1880ARC.getArcCoeff()[0], 0.998296953852258, tol, CLARKE1880ARC.getName() + " ArcCoeff0");
        assertEquals(CLARKE1880ARC.getArcCoeff()[1], -0.002555658715082, tol, CLARKE1880ARC.getName() + " ArcCoeff1");
        assertEquals(CLARKE1880ARC.getArcCoeff()[2], 0.000002726057344, tol, CLARKE1880ARC.getName() + " ArcCoeff2");
        assertEquals(CLARKE1880ARC.getArcCoeff()[3], -0.000000003618413, tol, CLARKE1880ARC.getName() + " ArcCoeff3");
        assertEquals(CLARKE1880ARC.getArcCoeff()[4], 0.000000000005149, tol, CLARKE1880ARC.getName() + " ArcCoeff4");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[0], -0.001703046147840, tol, CLARKE1880ARC.getName() + " KCoeff0");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[1], -0.000002175893386, tol, CLARKE1880ARC.getName() + " KCoeff1");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[2], -0.000000006173669, tol, CLARKE1880ARC.getName() + " KCoeff2");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[3], -0.000000000022983, tol, CLARKE1880ARC.getName() + " KCoeff3");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[4], -0.000000000000099, tol, CLARKE1880ARC.getName() + " KCoeff4");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[5], 0, tol, CLARKE1880ARC.getName() + " KCoeff5");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[6], 0, tol, CLARKE1880ARC.getName() + " KCoeff6");
        assertEquals(CLARKE1880ARC.getKCoeff(8)[7], 0, tol, CLARKE1880ARC.getName() + " KCoeff7");
    }

    @Test
    void testKRASSOWSKI() {
        assertEquals(KRASSOWSKI.getSemiMajorAxis(), 6378245.0, tol, KRASSOWSKI.getName() + " Semi-major axis");
        assertEquals(KRASSOWSKI.getInverseFlattening(), 298.3, tol, KRASSOWSKI.getName() + " Inverse Flattening");
        assertEquals(KRASSOWSKI.getSemiMinorAxis(), 6356863.018773047, tol, KRASSOWSKI.getName() + " Semi-minor axis");
        assertEquals(KRASSOWSKI.getFlattening(), 0.003352329869259, tol, KRASSOWSKI.getName() + " Flattening");
        assertEquals(KRASSOWSKI.getEccentricity(), 0.081813334016931, tol, KRASSOWSKI.getName() + " Eccentricity");
        assertEquals(KRASSOWSKI.getSquareEccentricity(), 0.006693421622966, tol, KRASSOWSKI.getName() + " Square eccentricity");
        assertEquals(KRASSOWSKI.getSecondEccentricitySquared(), 0.006738525414684, tol, KRASSOWSKI.getName() + " Second eccentricity squared");
        assertEquals(KRASSOWSKI.getArcCoeff()[0], 0.998324538627092, tol, KRASSOWSKI.getName() + " ArcCoeff0");
        assertEquals(KRASSOWSKI.getArcCoeff()[1], -0.002514246515768, tol, KRASSOWSKI.getName() + " ArcCoeff1");
        assertEquals(KRASSOWSKI.getArcCoeff()[2], 0.000002638353468, tol, KRASSOWSKI.getName() + " ArcCoeff2");
        assertEquals(KRASSOWSKI.getArcCoeff()[3], -0.000000003445164, tol, KRASSOWSKI.getName() + " ArcCoeff3");
        assertEquals(KRASSOWSKI.getArcCoeff()[4], 0.000000000004824, tol, KRASSOWSKI.getName() + " ArcCoeff4");
        assertEquals(KRASSOWSKI.getKCoeff(8)[0], -0.001675461372998, tol, KRASSOWSKI.getName() + " KCoeff0");
        assertEquals(KRASSOWSKI.getKCoeff(8)[1], -0.000002105967257, tol, KRASSOWSKI.getName() + " KCoeff1");
        assertEquals(KRASSOWSKI.getKCoeff(8)[2], -0.000000005878522, tol, KRASSOWSKI.getName() + " KCoeff2");
        assertEquals(KRASSOWSKI.getKCoeff(8)[3], -0.000000000021530, tol, KRASSOWSKI.getName() + " KCoeff3");
        assertEquals(KRASSOWSKI.getKCoeff(8)[4], -0.000000000000091, tol, KRASSOWSKI.getName() + " KCoeff4");
        assertEquals(KRASSOWSKI.getKCoeff(8)[5], 0, tol, KRASSOWSKI.getName() + " KCoeff5");
        assertEquals(KRASSOWSKI.getKCoeff(8)[6], 0, tol, KRASSOWSKI.getName() + " KCoeff6");
        assertEquals(KRASSOWSKI.getKCoeff(8)[7], 0, tol, KRASSOWSKI.getName() + " KCoeff7");
    }
    private final Ellipsoid eTest = createEllipsoidFromEccentricity(6380000, 0.08199188998);

    /*
     * Test isometricLatitudeTest() based on the algorithm "ALG0001" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    void isometricLatitudeTest() {
        assertEquals(eTest.isometricLatitude(0.872664626), 1.00552653649, 1e-11, "isometricLatitude test 1");
        assertEquals(eTest.isometricLatitude(-0.3), -0.30261690063, 1e-11, "isometricLatitude test 2");
        assertEquals(eTest.isometricLatitude(0.19998903370), 0.200000000009, 1e-11, "isometricLatitude test 3");
        assertEquals(eTest.isometricLatitude(0), 0, 1e-11, "isometricLatitude equator");
    }

    /*
     * Test curvilinearAbscissaTest() based on the algorithm "ALG0002" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    void latitudeTest() {
        assertEquals(eTest.latitude(1.00552653648), 0.872664626, 1e-11, "latitude test 1");
        assertEquals(eTest.latitude(-0.30261690060), -0.29999999997, 1e-11, "latitude test 2");
        assertEquals(eTest.latitude(0.2), 0.19998903369, 1e-11, "latitude test 3");
        assertEquals(eTest.latitude(0), 0, 1e-11, "latitude equator");
        assertEquals(eTest.latitude(Double.POSITIVE_INFINITY), Math.PI / 2, 1e-11, "latitude pole");
    }
    private final Ellipsoid eTest2 = createEllipsoidFromEccentricity(6378388, 0.08199189);

    /*
     * Test transverseRadiusOfCurvatureTest() based on the algorithm "ALG0021" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    void transverseRadiusOfCurvatureTest() {
        assertEquals(eTest2.transverseRadiusOfCurvature(0.977384381), 6393174.9755, 1e-4, "transverseRadiusOfCurvature test 1");
        assertEquals(eTest2.transverseRadiusOfCurvature(0), 6378388, 1e-4, "transverseRadiusOfCurvature equator");
        assertEquals(eTest2.transverseRadiusOfCurvature(Math.PI / 2), 6399936.6081, 1e-4, "transverseRadiusOfCurvature pole");
    }

    /*
     * Test curvilinearAbscissaTest() based on the algorithm "ALG0026" from the IGN.
     * See <http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_76.pdf>.
     * Date of consultation : May 15th 2013.
     */
    @Test
    void curvilinearAbscissaTest() {
        Ellipsoid eTestLocal = createEllipsoidFromEccentricity(6378388, 0.081819191043);
        assertEquals(eTest.curvilinearAbscissa(0.78539816340), 0.781551253561, 1e-12, "curvilinearAbscissa test 1");
        assertEquals(eTestLocal.curvilinearAbscissa(1.57079632679), 1.568164140908, 1e-12, "curvilinearAbscissa pole");
        assertEquals(eTestLocal.curvilinearAbscissa(0), 0, 1e-12, "curvilinearAbscissa equator");
    }

    @Test
    void meridionalRadiusOfCurvatureTest() {
        assertEquals(eTest2.meridionalRadiusOfCurvature(0.977384381), 6379673.1341, 1e-4, "meridionalRadiusOfCurvature test 1");
        assertEquals(eTest2.meridionalRadiusOfCurvature(0), 6335508.2022, 1e-4, "meridionalRadiusOfCurvature equator");
        assertEquals(eTest2.meridionalRadiusOfCurvature(Math.PI / 2), 6399936.6081, 1e-4, "meridionalRadiusOfCurvature pole");
    }

    @Test
    void arcFromLatTest() {
        assertEquals(eTest2.arcFromLat(Math.PI / 3), 6654228.3963, 1e-4, "arcFromLatTest test 1");
        assertEquals(eTest2.arcFromLat(Math.PI / 7), 2845220.2110, 1e-4, "arcFromLatTest test 2");
        assertEquals(eTest2.arcFromLat(0), 0, 1e-11, "arcFromLatTest equator");
    }

    @Test
    void latFromArcTest() {
        assertEquals(eTest2.latFromArc(6654228.3963), Math.PI / 3, 1e-11, "latFromArcTest test 1");
        assertEquals(eTest2.latFromArc(2845220.2110), Math.PI / 7, 1e-11, "latFromArcTest test 2");
        assertEquals(eTest2.latFromArc(0), 0, 1e-11, "latFromArcTest equator");
    }
}
