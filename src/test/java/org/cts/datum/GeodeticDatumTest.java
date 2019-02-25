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
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;
import org.cts.op.Identity;
import org.cts.op.transformation.GeocentricTranslation;

import org.junit.jupiter.api.Test;

import static org.cts.datum.GeodeticDatum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Jules Party
 */
class GeodeticDatumTest extends CTSTestCase {

    @Test
    void testWGS84() {
        assertEquals(WGS84.getToWGS84(), Identity.IDENTITY, "WGS84_geTtoWGS84");
        assertEquals(WGS84.getPrimeMeridian(), PrimeMeridian.GREENWICH, "WGS84_getPrimeMeridian");
        assertEquals(WGS84.getEllipsoid(), Ellipsoid.WGS84, "WGS84_getEllipsoid");
        assertEquals(WGS84.getExtent(), GeographicExtent.WORLD, "WGS84_getExtend");
    }

    @Test
    void testNTF_PARIS() {
        assertEquals(NTF_PARIS.getToWGS84(), new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0), "WGS84_getExtend");
        assertEquals(NTF_PARIS.getPrimeMeridian(), PrimeMeridian.PARIS, "NTF_PARIS_getPrimeMeridian");
        assertEquals(NTF_PARIS.getEllipsoid(), Ellipsoid.CLARKE1880IGN, "NTF_PARIS_getEllipsoid");
        assertEquals(NTF_PARIS.getExtent(), GeographicExtent.WORLD, "NTF_PARIS_getExtend");
    }

    @Test
    void testNTF() {
        assertEquals(NTF.getToWGS84(), new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0), "NTF_geTtoWGS84");
        assertEquals(NTF.getPrimeMeridian(), PrimeMeridian.GREENWICH, "NTF_getPrimeMeridian");
        assertEquals(NTF.getEllipsoid(), Ellipsoid.CLARKE1880IGN, "NTF_getEllipsoid");
        assertEquals(NTF.getExtent(), GeographicExtent.WORLD, "NTF_getExtend");
    }

    @Test
    void testRGF93() {
        assertEquals(RGF93.getToWGS84(), Identity.IDENTITY, "RGF93_geTtoWGS84");
        assertEquals(RGF93.getPrimeMeridian(), PrimeMeridian.GREENWICH, "RGF93_getPrimeMeridian");
        assertEquals(RGF93.getEllipsoid(), Ellipsoid.GRS80, "RGF93_getEllipsoid");
        assertEquals(RGF93.getExtent(), GeographicExtent.WORLD, "RGF93_getExtend");
    }

    @Test
    void testED50() {
        assertEquals(ED50.getToWGS84(), new GeocentricTranslation(-84.0, -97.0, -117.0, 1.0), "ED50_geTtoWGS84");
        assertEquals(ED50.getPrimeMeridian(), PrimeMeridian.GREENWICH, "ED50_getPrimeMeridian");
        assertEquals(ED50.getEllipsoid(), Ellipsoid.INTERNATIONAL1924, "ED50_getEllipsoid");
        assertEquals(ED50.getExtent(), GeographicExtent.WORLD, "ED50_getExtend");
    }

    
    
    @Test
    void testCreateGeodeticDatum() {
        GeodeticDatum datum = GeodeticDatum.createGeodeticDatum(new Identifier(GeodeticDatum.class,"MyDatum"),
                PrimeMeridian.PARIS, Ellipsoid.GRS80,
                new GeocentricTranslation(10,10,10), new GeographicExtent("",0, 0, 0, 0), "","");
        //datum.addGeocentricTransformation(WGS84, new GeocentricTranslation(10,10,10));
        assertEquals(datum.getPrimeMeridian(), PrimeMeridian.PARIS);
        assertEquals(datum.getEllipsoid(), Ellipsoid.GRS80);
        assertEquals(datum.getToWGS84(), new GeocentricTranslation(10, 10, 10));
        assertTrue(datum.getGeocentricTransformations(WGS84).size()>0);
        assertEquals(datum.getGeocentricTransformations(WGS84).iterator().next(), new GeocentricTranslation(10, 10, 10));
        assertTrue(WGS84.getGeocentricTransformations(datum).size()>0);
        assertEquals(WGS84.getGeocentricTransformations(datum).iterator().next(), new GeocentricTranslation(-10, -10, -10));
    }
}
