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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author ebocher
 */
public class CRSIdentifierTest extends CTSTestCase {

    @Test
    public void testEPSG4326AuthorityName() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:4326").getAuthorityName().equals("EPSG"));
    }

    @Test
    public void testEPSG4326AuthorityKey() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:4326").getAuthorityKey().equals("4326"));
    }

    //Disable this test because the javadoc is not coherent
    @Test
    public void testEPSG4326Code() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:4326").getCode().equals("EPSG:4326"));
    }

    @Test
    public void testEPSG4326Name() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:4326").getName().equals("WGS 84"));
    }

    @Test
    public void testEPSG27572AuthorityName() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:27572").getAuthorityName().equals("EPSG"));
    }

    @Test
    public void testEPSG27572AuthorityKey() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:27572").getAuthorityKey().equals("27572"));
    }

    @Test
    public void testEPSG27572Code() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:27572").getCode().equals("EPSG:27572"));
    }

    @Test
    public void testEPSG27572Name() throws Exception {
        assertTrue(cRSFactory.getCRS("EPSG:27572").getName().equals("NTF (Paris) / Lambert zone II"));
    }
}
