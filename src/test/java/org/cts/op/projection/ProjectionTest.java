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
package org.cts.op.projection;

import org.cts.Parameter;
import org.cts.datum.Ellipsoid;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michaël Michaud
 */
class ProjectionTest {

    @Test
    // Just check that tmerc-1(tmerc(x,y)) let the coordinate unchanged +/- 1 mm
    void testInverseProjection() throws Exception {
        UniversalTransverseMercator utm = new UniversalTransverseMercator(Ellipsoid.GRS80,
                new HashMap<String, Measure>() {{
                    put(Parameter.CENTRAL_MERIDIAN, new Measure(0, Unit.DEGREE));
                    put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
                }});
        assertTrue(utm.isDirect());
        assertFalse(utm.inverse().isDirect());
        assertFalse(utm.inverse().toString().equals(utm.toString()));
        assertSame(utm.inverse().inverse(), utm);
    }
}
