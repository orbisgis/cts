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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Erwan Bocher
 */
class TestPrimeMeridian extends CTSTestCase {

    @Test
    void testGREENWICH_PM() {
        assertTrue(checkEquals("GREENWICH ", PrimeMeridian.GREENWICH.getLongitudeFromGreenwichInDegrees(), 0, 10E-9));
        assertEquals("8901", PrimeMeridian.GREENWICH.getAuthorityKey());
    }

    @Test
    void testLISBON_PM() {
        assertTrue(checkEquals("LISBON ", PrimeMeridian.LISBON.getLongitudeFromGreenwichInDegrees(), -9.131906111111112, 10E-16));
        assertEquals("8902", PrimeMeridian.LISBON.getAuthorityKey());
    }

    @Test
    void testPARIS_PM() {
        assertTrue(checkEquals("PARIS ", PrimeMeridian.PARIS.getLongitudeFromGreenwichInDegrees(), 2.337229167, 10E-10));
        assertEquals("8903", PrimeMeridian.PARIS.getAuthorityKey());
    }

    @Test
    void testBOGOTA_PM() {
        assertTrue(checkEquals("BOGOTA ", PrimeMeridian.BOGOTA.getLongitudeFromGreenwichInDegrees(), -74.08091666666667, 10E-15));
        assertEquals("8904", PrimeMeridian.BOGOTA.getAuthorityKey());
    }

    @Test
    void testMADRID_PM() {
        assertTrue(checkEquals("MADRID ", PrimeMeridian.MADRID.getLongitudeFromGreenwichInDegrees(), -3.687938888888889, 10E-16));
        assertEquals("8905", PrimeMeridian.MADRID.getAuthorityKey());
    }

    @Test
    void testROME_PM() {
        assertTrue(checkEquals("ROME ", PrimeMeridian.ROME.getLongitudeFromGreenwichInDegrees(), 12.45233333333333, 10E-15));
        assertEquals("8906", PrimeMeridian.ROME.getAuthorityKey());
    }

    @Test
    void testBERN_PM() {
        assertTrue(checkEquals("BERN ", PrimeMeridian.BERN.getLongitudeFromGreenwichInDegrees(), 7.439583333333333, 10E-16));
        assertEquals("8907", PrimeMeridian.BERN.getAuthorityKey());
    }

    @Test
    void testJAKARTA_PM() {
        assertTrue(checkEquals("JAKARTA ", PrimeMeridian.JAKARTA.getLongitudeFromGreenwichInDegrees(), 106.8077194444444, 10E-14));
        assertEquals("8908", PrimeMeridian.JAKARTA.getAuthorityKey());
    }

    @Test
    void testFERRO_PM() {
        assertTrue(checkEquals("FERRO ", PrimeMeridian.FERRO.getLongitudeFromGreenwichInDegrees(), -17.66666666666667, 10E-15));
        assertEquals("8909", PrimeMeridian.FERRO.getAuthorityKey());
    }

    @Test
    void testBRUSSELS_PM() {
        assertTrue(checkEquals("BRUSSELS ", PrimeMeridian.BRUSSELS.getLongitudeFromGreenwichInDegrees(), 4.367975, 10E-9));
        assertEquals("8910", PrimeMeridian.BRUSSELS.getAuthorityKey());
    }

    @Test
    void testSTOCKHOLM_PM() {
        assertTrue(checkEquals("STOCKHOLM ", PrimeMeridian.STOCKHOLM.getLongitudeFromGreenwichInDegrees(), 18.05827777777778, 10E-15));
        assertEquals("8911", PrimeMeridian.STOCKHOLM.getAuthorityKey());
    }

    @Test
    void testATHENS_PM() {
        assertTrue(checkEquals("ATHENS ", PrimeMeridian.ATHENS.getLongitudeFromGreenwichInDegrees(), 23.7163375, 10E-9));
        assertEquals("8912", PrimeMeridian.ATHENS.getAuthorityKey());
    }

    @Test
    void testOSLO_PM() {
        assertTrue(checkEquals("OSLO ", PrimeMeridian.OSLO.getLongitudeFromGreenwichInDegrees(), 10.72291666666667, 10E-15));
        assertEquals("8913", PrimeMeridian.OSLO.getAuthorityKey());
    }

    @Test
    void testPARIS_RGS_PM() {
        assertTrue(checkEquals("PARIS_RGS ", PrimeMeridian.PARIS_RGS.getLongitudeFromGreenwichInDegrees(), 2.201395, 10E-9));
        assertEquals("8914", PrimeMeridian.PARIS_RGS.getAuthorityKey());
    }
}
