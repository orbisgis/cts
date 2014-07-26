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
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;
import org.cts.op.Identity;
import org.cts.op.transformation.GeocentricTranslation;

import org.junit.Test;

import static org.cts.datum.GeodeticDatum.*;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Jules Party
 */
public class GeodeticDatumTest extends CTSTestCase {

    @Test
    public void testWGS84() {
        assertTrue("WGS84_geTtoWGS84",
                WGS84.getToWGS84().equals(Identity.IDENTITY));
        assertTrue("WGS84_getPrimeMeridian",
                WGS84.getPrimeMeridian().equals(PrimeMeridian.GREENWICH));
        assertTrue("WGS84_getEllipsoid",
                WGS84.getEllipsoid().equals(Ellipsoid.WGS84));
        assertTrue("WGS84_getExtend",
                WGS84.getExtent().equals(GeographicExtent.WORLD));
    }

    @Test
    public void testNTF_PARIS() {
        assertTrue("NTF_PARIS_geTtoWGS84", NTF_PARIS.getToWGS84().equals(
                new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0)));
        assertTrue("NTF_PARIS_getPrimeMeridian",
                NTF_PARIS.getPrimeMeridian().equals(PrimeMeridian.PARIS));
        assertTrue("NTF_PARIS_getEllipsoid",
                NTF_PARIS.getEllipsoid().equals(Ellipsoid.CLARKE1880IGN));
        assertTrue("NTF_PARIS_getExtend",
                NTF_PARIS.getExtent().equals(GeographicExtent.WORLD));
    }

    @Test
    public void testNTF() {
        assertTrue("NTF_geTtoWGS84", NTF.getToWGS84().equals(
                new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0)));
        assertTrue("NTF_getPrimeMeridian",
                NTF.getPrimeMeridian().equals(PrimeMeridian.GREENWICH));
        assertTrue("NTF_getEllipsoid",
                NTF.getEllipsoid().equals(Ellipsoid.CLARKE1880IGN));
        assertTrue("NTF_getExtend",
                NTF.getExtent().equals(GeographicExtent.WORLD));
    }

    @Test
    public void testRGF93() {
        assertTrue("RGF93_geTtoWGS84",
                RGF93.getToWGS84().equals(Identity.IDENTITY));
        assertTrue("RGF93_getPrimeMeridian",
                RGF93.getPrimeMeridian().equals(PrimeMeridian.GREENWICH));
        assertTrue("RGF93_getEllipsoid",
                RGF93.getEllipsoid().equals(Ellipsoid.GRS80));
        assertTrue("RGF93_getExtend",
                RGF93.getExtent().equals(GeographicExtent.WORLD));
    }

    @Test
    public void testED50() {
        assertTrue("ED50_geTtoWGS84", ED50.getToWGS84().equals(
                new GeocentricTranslation(-84.0, -97.0, -117.0, 1.0)));
        assertTrue("ED50_getPrimeMeridian",
                ED50.getPrimeMeridian().equals(PrimeMeridian.GREENWICH));
        assertTrue("ED50_getEllipsoid",
                ED50.getEllipsoid().equals(Ellipsoid.INTERNATIONAL1924));
        assertTrue("ED50_getExtend",
                ED50.getExtent().equals(GeographicExtent.WORLD));
    }

    @Test
    public void testGetDatum() {
        Identifier id = new Identifier("EPSG", "6171", "Réseau géodésique français 1993", "RGF93");
        assertTrue("test de getDatum(id)", IdentifiableComponent.getComponent(id).equals(RGF93));
    }

    @Test
    public void testCreateGeodeticDatum() {
        GeodeticDatum datum = GeodeticDatum.createGeodeticDatum(new Identifier(GeodeticDatum.class,"MyDatum"),
                PrimeMeridian.PARIS, Ellipsoid.GRS80,
                new GeocentricTranslation(10,10,10), new GeographicExtent("",0, 0, 0, 0), "","");
        //datum.addGeocentricTransformation(WGS84, new GeocentricTranslation(10,10,10));
        assertTrue(datum.getPrimeMeridian().equals(PrimeMeridian.PARIS));
        assertTrue(datum.getEllipsoid().equals(Ellipsoid.GRS80));
        assertTrue(datum.getToWGS84().equals(new GeocentricTranslation(10,10,10)));
        assertTrue(datum.getGeocentricTransformations(WGS84).size()>0);
        assertTrue(datum.getGeocentricTransformations(WGS84).iterator().next().equals(new GeocentricTranslation(10,10,10)));
        assertTrue(WGS84.getGeocentricTransformations(datum).size()>0);
        assertTrue(WGS84.getGeocentricTransformations(datum).iterator().next().equals(new GeocentricTranslation(-10,-10,-10)));
        //System.out.println(datum.getGeographicTransformations(WGS84));
        //System.out.println(WGS84.getGeographicTransformations(datum));
    }
}
