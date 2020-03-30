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
package org.cts.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UTMUtils
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class UTMUtilsTest {
    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = +90;
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = +180;

    @Test
    void isValidLatitudeTest() {
        assertTrue(UTMUtils.isValidLatitude(MIN_LATITUDE));
        assertTrue(UTMUtils.isValidLatitude(MAX_LATITUDE));
        assertTrue(UTMUtils.isValidLatitude(5.69f));

        assertFalse(UTMUtils.isValidLatitude(-91));
        assertFalse(UTMUtils.isValidLatitude(91));
    }

    @Test
    void isValidLongitudeTest() {
        assertTrue(UTMUtils.isValidLongitude(MIN_LONGITUDE));
        assertTrue(UTMUtils.isValidLongitude(MAX_LONGITUDE));
        assertTrue(UTMUtils.isValidLongitude(5.69f));

        assertFalse(UTMUtils.isValidLongitude(-181));
        assertFalse(UTMUtils.isValidLongitude(181));
    }

    @Test
    void getEPSGCodeTest() {
        assertEquals(32632, UTMUtils.getEPSGCode(59.04f, 3.68f));
        assertEquals(32717, UTMUtils.getEPSGCode(-10.8469f, -81.0351f));
        assertEquals(32634, UTMUtils.getEPSGCode(68.948f, 20.939f));
        assertEquals(4038, UTMUtils.getEPSGCode(66.682f, 32.2119f));
        assertEquals(32736, UTMUtils.getEPSGCode(-66.682f, 32.2119f));
        assertEquals(32633, UTMUtils.getEPSGCode(74.23f, 14.10f));
        assertEquals(32631, UTMUtils.getEPSGCode(74.23f, 8.10f));
        assertEquals(4037, UTMUtils.getEPSGCode(74.23f, 23.55f));
        assertEquals(32633, UTMUtils.getEPSGCode(74.23f, 19.55f));
        assertEquals(32637, UTMUtils.getEPSGCode(74.23f, 33.68f));
        assertEquals(4037, UTMUtils.getEPSGCode(74.23f, 32.68f));
    }

    @Test
    void isBetweenTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method isBetween = UTMUtils.class.getDeclaredMethod("isBetween", float.class, int.class, int.class);
        isBetween.setAccessible(true);

        assertTrue((boolean) isBetween.invoke(new UTMUtils(), 2.3f, 1, 3));
        assertTrue((boolean) isBetween.invoke(new UTMUtils(), 1f, 1, 3));
        assertFalse((boolean) isBetween.invoke(new UTMUtils(), 3f, 1, 3));
    }

    @Test
    void getZoneHemisphereTest() {
        assertArrayEquals(new String[]{"17", "S"}, UTMUtils.getZoneHemisphere(-10.8469f, -81.0351f));
        assertArrayEquals(new String[]{"34", "N"}, UTMUtils.getZoneHemisphere(68.948f, 20.939f));
        assertArrayEquals(new String[]{"36", "N"}, UTMUtils.getZoneHemisphere(66.682f, 32.2119f));
        assertArrayEquals(new String[]{"36", "S"}, UTMUtils.getZoneHemisphere(-66.682f, 32.2119f));
        assertArrayEquals(new String[]{"31", "N"}, UTMUtils.getZoneHemisphere(49.04f, 3.68f));
        assertArrayEquals(new String[]{"31", "N"}, UTMUtils.getZoneHemisphere(49.04f, 2.68f));
        assertArrayEquals(new String[]{"31", "N"}, UTMUtils.getZoneHemisphere(59.04f, 2.68f));
        assertArrayEquals(new String[]{"32", "N"}, UTMUtils.getZoneHemisphere(70.23f, 10.10f));

        //Norway workaround
        assertArrayEquals(new String[]{"32", "N"}, UTMUtils.getZoneHemisphere(59.04f, 3.68f));

        //Svalbard workaround
        assertArrayEquals(new String[]{"33", "N"}, UTMUtils.getZoneHemisphere(74.23f, 10.10f));
        assertArrayEquals(new String[]{"31", "N"}, UTMUtils.getZoneHemisphere(74.23f, 8.10f));
        assertArrayEquals(new String[]{"35", "N"}, UTMUtils.getZoneHemisphere(74.23f, 23.55f));
        assertArrayEquals(new String[]{"33", "N"}, UTMUtils.getZoneHemisphere(74.23f, 19.55f));
        assertArrayEquals(new String[]{"37", "N"}, UTMUtils.getZoneHemisphere(74.23f, 33.68f));
        assertArrayEquals(new String[]{"35", "N"}, UTMUtils.getZoneHemisphere(74.23f, 32.68f));

        assertThrows(IllegalArgumentException.class, () -> UTMUtils.getZoneHemisphere(-91.0f, 0.0f));
        assertThrows(IllegalArgumentException.class, () -> UTMUtils.getZoneHemisphere(91.0f, 0.0f));
        assertThrows(IllegalArgumentException.class, () -> UTMUtils.getZoneHemisphere(0.0f, -181.0f));
        assertThrows(IllegalArgumentException.class, () -> UTMUtils.getZoneHemisphere(0.0f, 181.0f));
    }

    @Test
    void getProjTest() {
        assertEquals("+proj=utm +zone=32 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(59.04f, 3.68f));
        assertEquals("+proj=utm +zone=17 +south +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(-10.8469f, -81.0351f));
        assertEquals("+proj=utm +zone=34 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(68.948f, 20.939f));
        assertEquals("+proj=utm +zone=36 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(66.682f, 32.2119f));
        assertEquals("+proj=utm +zone=36 +south +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(-66.682f, 32.2119f));
        assertEquals("+proj=utm +zone=31 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(74.23f, 8.10f));
        assertEquals("+proj=utm +zone=35 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(74.23f, 23.55f));
    }
}
