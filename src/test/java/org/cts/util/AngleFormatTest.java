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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for AngleFormat class
 */
class AngleFormatTest {

    @Test
    void parseAngleInDMSTest() {
        assertEquals(23.0, AngleFormat.parseAngle("23°"), 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°"), -23.0, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°1'"), 23.0 + 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°1'"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'"), 23.0 + 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'01"), 23.0 + 1.0 / 60 + 1.0 / 3600, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01'01"), -23.0 - 1.0 / 60 - 1.0 / 3600, 0.0, "Parse angle");
    }

    @Test
    void parseAngleInDMSHTest() {
        assertEquals(23.0, AngleFormat.parseAngle("23°N"), 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°N"), -23.0, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°1'N"), 23.0 + 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°1'N"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'N"), 23.0 + 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01N"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'01N"), 23.0 + 1.0 / 60 + 1.0 / 3600, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01'01N"), -23.0 - 1.0 / 60 - 1.0 / 3600, 0.0, "Parse angle");
    }

    @Test
    void parseAngleInDMSHdoubleNegTest() {
        assertEquals(AngleFormat.parseAngle("23°S"), -23.0, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°S"), -23.0, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°1'S"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°1'S"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'S"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01S"), -23.0 - 1.0 / 60, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("23°01'01S"), -23.0 - 1.0 / 60 - 1.0 / 3600, 0.0, "Parse angle");
        assertEquals(AngleFormat.parseAngle("-23°01'01S"), -23.0 - 1.0 / 60 - 1.0 / 3600, 0.0, "Parse angle");
    }
}
