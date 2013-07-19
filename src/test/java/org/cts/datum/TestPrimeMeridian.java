/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
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
package org.cts.datum;

import org.cts.CTSTestCase;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class TestPrimeMeridian extends CTSTestCase {

    @Test
    public void testGREENWICH_PM() {
        assertTrue(checkEquals("GREENWICH ", PrimeMeridian.GREENWICH.getLongitudeFromGreenwichInDegrees(), 0, 10E-9));
        assertTrue(PrimeMeridian.GREENWICH.getAuthorityKey().equals("8901"));
    }

    @Test
    public void testLISBON_PM() {
        assertTrue(checkEquals("LISBON ", PrimeMeridian.LISBON.getLongitudeFromGreenwichInDegrees(), -9.131906111111112, 10E-16));
        assertTrue(PrimeMeridian.LISBON.getAuthorityKey().equals("8902"));
    }

    @Test
    public void testPARIS_PM() {
        assertTrue(checkEquals("PARIS ", PrimeMeridian.PARIS.getLongitudeFromGreenwichInDegrees(), 2.337229167, 10E-10));
        assertTrue(PrimeMeridian.PARIS.getAuthorityKey().equals("8903"));
    }

    @Test
    public void testBOGOTA_PM() {
        assertTrue(checkEquals("BOGOTA ", PrimeMeridian.BOGOTA.getLongitudeFromGreenwichInDegrees(), -74.08091666666667, 10E-15));
        assertTrue(PrimeMeridian.BOGOTA.getAuthorityKey().equals("8904"));
    }

    @Test
    public void testMADRID_PM() {
        assertTrue(checkEquals("MADRID ", PrimeMeridian.MADRID.getLongitudeFromGreenwichInDegrees(), -3.687938888888889, 10E-16));
        assertTrue(PrimeMeridian.MADRID.getAuthorityKey().equals("8905"));
    }

    @Test
    public void testROME_PM() {
        assertTrue(checkEquals("ROME ", PrimeMeridian.ROME.getLongitudeFromGreenwichInDegrees(), 12.45233333333333, 10E-15));
        assertTrue(PrimeMeridian.ROME.getAuthorityKey().equals("8906"));
    }

    @Test
    public void testBERN_PM() {
        assertTrue(checkEquals("BERN ", PrimeMeridian.BERN.getLongitudeFromGreenwichInDegrees(), 7.439583333333333, 10E-16));
        assertTrue(PrimeMeridian.BERN.getAuthorityKey().equals("8907"));
    }

    @Test
    public void testJAKARTA_PM() {
        assertTrue(checkEquals("JAKARTA ", PrimeMeridian.JAKARTA.getLongitudeFromGreenwichInDegrees(), 106.8077194444444, 10E-14));
        assertTrue(PrimeMeridian.JAKARTA.getAuthorityKey().equals("8908"));
    }

    @Test
    public void testFERRO_PM() {
        assertTrue(checkEquals("FERRO ", PrimeMeridian.FERRO.getLongitudeFromGreenwichInDegrees(), -17.66666666666667, 10E-15));
        assertTrue(PrimeMeridian.FERRO.getAuthorityKey().equals("8909"));
    }

    @Test
    public void testBRUSSELS_PM() {
        assertTrue(checkEquals("BRUSSELS ", PrimeMeridian.BRUSSELS.getLongitudeFromGreenwichInDegrees(), 4.367975, 10E-9));
        assertTrue(PrimeMeridian.BRUSSELS.getAuthorityKey().equals("8910"));
    }

    @Test
    public void testSTOCKHOLM_PM() {
        assertTrue(checkEquals("STOCKHOLM ", PrimeMeridian.STOCKHOLM.getLongitudeFromGreenwichInDegrees(), 18.05827777777778, 10E-15));
        assertTrue(PrimeMeridian.STOCKHOLM.getAuthorityKey().equals("8911"));
    }

    @Test
    public void testATHENS_PM() {
        assertTrue(checkEquals("ATHENS ", PrimeMeridian.ATHENS.getLongitudeFromGreenwichInDegrees(), 23.7163375, 10E-9));
        assertTrue(PrimeMeridian.ATHENS.getAuthorityKey().equals("8912"));
    }

    @Test
    public void testOSLO_PM() {
        assertTrue(checkEquals("OSLO ", PrimeMeridian.OSLO.getLongitudeFromGreenwichInDegrees(), 10.72291666666667, 10E-15));
        assertTrue(PrimeMeridian.OSLO.getAuthorityKey().equals("8913"));
    }

    @Test
    public void testPARIS_RGS_PM() {
        assertTrue(checkEquals("PARIS_RGS ", PrimeMeridian.PARIS_RGS.getLongitudeFromGreenwichInDegrees(), 2.201395, 10E-9));
        assertTrue(PrimeMeridian.PARIS_RGS.getAuthorityKey().equals("8914"));
    }
}
