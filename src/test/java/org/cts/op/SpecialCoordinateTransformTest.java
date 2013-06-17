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
package org.cts.op;

import java.util.HashMap;
import java.util.Map;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.Parameter;
import org.cts.crs.CRSHelper;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.crs.ProjectedCRS;
import org.cts.datum.GeodeticDatum;
import org.cts.op.projection.CylindricalEqualArea;
import org.cts.op.projection.MillerCylindrical;
import org.cts.registry.Registry;
import org.cts.registry.RegistryManager;
import org.cts.units.Measure;
import org.cts.units.Unit;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *This class contains tests that uses CRS not present in registries and thus needing
 * a special treatment.
 * 
 * @author Jules Party
 */
public class SpecialCoordinateTransformTest extends BaseCoordinateTransformTest {
    
    @Test
    public void testPolyconic() throws Exception {
        double csNameSrc_X = -45;
        double csNameSrc_Y = 6;
        double csNameDest_X = 5996378.71;
        double csNameDest_Y = 10671650.06;
        double tolerance = 0.01;
        CoordinateReferenceSystem inputCRS = createCRS("EPSG:4674");
        RegistryManager registryManager = new RegistryManager();
        Registry registry = registryManager.getRegistry("epsg");
        Map<String, String> crsParameters = registry.getParameters("29101");
        // The output CRS is the same as the EPSG:29101, but it uses the ellipsoid
        // GRS80 instead of the ellipsoid aust_SA. 
        CoordinateReferenceSystem outputCRS = new ProjectedCRS(new Identifier(ProjectedCRS.class, "SIRGAS 2000 / Brazil Polyconic"),
                (GeodeticDatum) inputCRS.getDatum(), CRSHelper.getProjection("poly", Ellipsoid.GRS80, crsParameters));
        double[] pointSource = new double[]{csNameSrc_X, csNameSrc_Y, 0};
        double[] result = transform((GeodeticCRS) inputCRS, (GeodeticCRS) outputCRS, pointSource);
        double[] pointDest = new double[]{csNameDest_X, csNameDest_Y, 0};
        double[] check = transform((GeodeticCRS) outputCRS, (GeodeticCRS) inputCRS, pointDest);
        //printCRStoWKT(inputCRS);
        //printCRStoWKT(outputCRS);
        assertTrue(checkEquals2D("POLY dir--> " + "EPSG:4674" + " to " + "SIRGAS 2000 / Brazil Polyconic", result, pointDest, tolerance));
        assertTrue(checkEquals2D("POLY inv--> " + "SIRGAS 2000 / Brazil Polyconic" + " to " +"EPSG:4674", check, pointSource, tolerance));
    }

    @Test
    public void testCylindricalEqualArea() throws Exception {
        Map<String, Measure> map = new HashMap<String, Measure>();
        map.put(Parameter.CENTRAL_MERIDIAN, new Measure(-75, Unit.DEGREE));
        map.put(Parameter.LATITUDE_OF_TRUE_SCALE, new Measure(5, Unit.DEGREE));
        map.put(Parameter.FALSE_EASTING, new Measure(0, Unit.METER));
        map.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        CylindricalEqualArea proj = new CylindricalEqualArea(Ellipsoid.CLARKE1866, map);
        double PI = Math.PI;
        double[] pointSource = new double[]{5, -78, 0};
        double[] result = new double[]{5 * PI / 180, -78 * PI / 180, 0};
        result = proj.transform(result);
        double[] pointDest = new double[]{-332699.8, 554248.5, 0};
        double[] check = new double[]{-332699.8, 554248.5, 0};
        check = proj.inverse().transform(check);
        check[0] = check[0] * 180 / PI;
        check[1] = check[1] * 180 / PI;
        double tolerance = 1E-1;
        assertTrue(checkEquals2D("CEA dir--> ", result, pointDest, tolerance));
        assertTrue(checkEquals2D("CEA inv--> ", check, pointSource, tolerance));
    }
    
    @Test
    public void testMillerCylindrical() throws Exception {
        Map<String, Measure> map = new HashMap<String, Measure>();
        map.put(Parameter.CENTRAL_MERIDIAN, new Measure(0, Unit.DEGREE));
        map.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(0, Unit.DEGREE));
        map.put(Parameter.FALSE_EASTING, new Measure(0, Unit.METER));
        map.put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
        map.put(Parameter.SCALE_FACTOR, new Measure(1, Unit.UNIT));
        MillerCylindrical proj = new MillerCylindrical(Ellipsoid.createEllipsoidFromSemiMinorAxis(1, 1), map);
        double PI = Math.PI;
        double[] pointSource = new double[]{50, -75, 0};
        double[] result = new double[]{50 * PI / 180, -75 * PI / 180, 0};
        result = proj.transform(result);
        double[] pointDest = new double[]{-1.3089969, 0.9536371, 0};
        double[] check = new double[]{-1.3089969, 0.9536371, 0};
        check = proj.inverse().transform(check);
        check[0] = check[0] * 180 / PI;
        check[1] = check[1] * 180 / PI;
        double tolerance = 1E-1;
        assertTrue(checkEquals2D("MILL dir--> ", result, pointDest, tolerance));
        assertTrue(checkEquals2D("MILL inv--> ", check, pointSource, tolerance));
    }
}
