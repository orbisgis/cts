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
package org.cts.parser.proj4;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Test for proj4 parser
 * @author Erwan Bocher CNRS
 */
public class Proj4ParserTest {
    
    @Test
    void testParseUTMNorth() {        
        Map<String, String> params = Proj4Parser.readParameters("+proj=utm +zone=32 +datum=WGS84 +units=m +no_defs");
        assertEquals(params.get("proj"), "utm");
        assertEquals(params.get("zone"), "32");
        assertEquals(params.get("datum"), "WGS84");
        assertEquals(params.get("units"), "m");
        assertNull(params.get("no_defs"));        
    }
    
    @Test
    void testParseUTMSouth() {        
        Map<String, String> params = Proj4Parser.readParameters("+proj=utm +zone=32 +south +datum=WGS84 +units=m +no_defs");
        assertEquals(params.get("proj"), "utm");
        assertEquals(params.get("zone"), "32");
        assertEquals(params.get("datum"), "WGS84");
        assertEquals(params.get("units"), "m");
        assertNull(params.get("south"));  
        assertNull(params.get("no_defs"));        
    }
    
     @Test
    void testParse() {        
        Map<String, String> params = Proj4Parser.readParameters("+proj=tmerc +lat_0=0 +lon_0=106 +k=1 +x_0=500000 +y_0=0 +ellps=krass +towgs84=-17.51,-108.32,-62.39,0,0,0,0 +units=m +no_defs");
        assertEquals(params.get("proj"), "tmerc");
        assertEquals(params.get("lat_0"), "0");
        assertEquals(params.get("lon_0"), "106");
        assertEquals(params.get("k"), "1");
        assertEquals(params.get("x_0"), "500000");
        assertEquals(params.get("y_0"), "0");
        assertEquals(params.get("towgs84"), "-17.51,-108.32,-62.39,0,0,0,0");
        assertEquals(params.get("ellps"), "krass");
        assertEquals(params.get("units"), "m");
        assertNull(params.get("no_defs"));        
    }
    
}
