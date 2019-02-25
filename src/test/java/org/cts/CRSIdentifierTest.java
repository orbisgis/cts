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

package org.cts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author ebocher
 */
class CRSIdentifierTest extends CTSTestCase {

    @Test
    void testEPSG4326AuthorityName() throws Exception {
        assertEquals("EPSG", cRSFactory.getCRS("EPSG:4326").getAuthorityName());
    }

    @Test
    void testEPSG4326AuthorityKey() throws Exception {
        assertEquals("4326", cRSFactory.getCRS("EPSG:4326").getAuthorityKey());
    }

    @Test
    void testEPSG4326Code() throws Exception {
        assertEquals("EPSG:4326", cRSFactory.getCRS("EPSG:4326").getCode());
    }
    
    @Test
    void testEPSG4326Name() throws Exception {
        assertEquals("WGS 84", cRSFactory.getCRS("EPSG:4326").getName());
    }

    @Test
    void testEPSG27572AuthorityName() throws Exception {
        assertEquals("EPSG", cRSFactory.getCRS("EPSG:27572").getAuthorityName());
    }

    @Test
    void testEPSG27572AuthorityKey() throws Exception {
        assertEquals("27572", cRSFactory.getCRS("EPSG:27572").getAuthorityKey());
    }

    @Test
    void testEPSG27572Code() throws Exception {
        assertEquals("EPSG:27572", cRSFactory.getCRS("EPSG:27572").getCode());
    }

    @Test
    void testEPSG27572Name() throws Exception {
        assertEquals("NTF (Paris) / Lambert zone II", cRSFactory.getCRS("EPSG:27572").getName());
    }
}
