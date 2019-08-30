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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for UTMUtils
 * @author Erwan Bocher
 */
public class UTMUtilsTest {
    
    @Test
    void utmInfoTest() {
        assertArrayEquals(new String[]{"32","N"}, UTMUtils.getZoneHemisphere(59.04f, 3.68f));
        assertArrayEquals(new String[]{"17","S"}, UTMUtils.getZoneHemisphere(-10.8469f, -81.0351f));
        assertArrayEquals(new String[]{"34","N"}, UTMUtils.getZoneHemisphere(68.948f, 20.939f));
        assertArrayEquals(new String[]{"36","N"}, UTMUtils.getZoneHemisphere(66.682f, 32.2119f));
        assertArrayEquals(new String[]{"36","S"}, UTMUtils.getZoneHemisphere(-66.682f,32.2119f));
    }
    
    @Test
    void utmEpsgTest() {
        assertEquals(32632, UTMUtils.getEPSGCode(59.04f, 3.68f));
        assertEquals(32717, UTMUtils.getEPSGCode(-10.8469f, -81.0351f));
        assertEquals(32634, UTMUtils.getEPSGCode(68.948f, 20.939f));
        assertEquals(4038, UTMUtils.getEPSGCode(66.682f, 32.2119f));
        assertEquals(32736, UTMUtils.getEPSGCode(-66.682f,32.2119f));
    }
    
    @Test
    void utmProjTest() {
        assertEquals("+proj=utm +zone=32 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(59.04f, 3.68f));
        assertEquals("+proj=utm +zone=17 +south +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(-10.8469f, -81.0351f));
        assertEquals("+proj=utm +zone=34 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(68.948f, 20.939f));
        assertEquals("+proj=utm +zone=36 +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(66.682f, 32.2119f));
        assertEquals("+proj=utm +zone=36 +south +datum=WGS84 +units=m +no_defs", UTMUtils.getProj(-66.682f,32.2119f));
    }
}
