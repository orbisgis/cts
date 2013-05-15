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
package org.cts;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.cts.Ellipsoid.*;

/**
 *
 * @author jparty
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
}
