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
package org.cts.op.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cts.CoordinateOperation;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.Parameter;
import org.cts.datum.GeodeticDatum;
import org.cts.op.*;
import org.cts.op.projection.LambertConicConformal1SP;
import org.cts.op.projection.LambertConicConformal2SP;
import org.cts.units.Measure;
import org.cts.units.Unit;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ebocher
 */
public class UserDefinedTransform extends BaseCoordinateTransformTest {

    /**
     * Transform a point from lambert2 etendu to WGS84
     */
    @Test
    public void fromLambert2EtenduToWGS84() throws Exception {
        double[] pointSource = new double[]{584173.736, 2594514.828};
        double[] pointDest = new double[]{50.345609791, 2.114551393};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E.inverse();
        double[] result = LAMBERT2E.transform(pointSource);
        result = LongitudeRotation.PARIS2GREENWICH.transform(result);
        Geographic2Geocentric geographic2Geocentric = new Geographic2Geocentric(Ellipsoid.CLARKE1880IGN);
        result = geographic2Geocentric.transform(result);
        CoordinateOperation toWG84 = GeodeticDatum.NTF_PARIS.getToWGS84();
        result = toWG84.transform(result);
        Geocentric2Geographic geocentric2Geographic = new Geocentric2Geographic(Ellipsoid.WGS84);
        result = geocentric2Geographic.transform(result);
        CoordinateOperation conv = UnitConversion.RAD2DD;
        result = conv.transform(result);
        assertTrue(checkEquals("Lambert 2 etendu to WGS84 degrees", result, pointDest, 10E-7));
    }

    /**
     * Short circuit to transform a point from lambert2 etendu to WGS84
     */
    @Test
    public void fromSCLambert2EtenduToWGS84() throws Exception {
        double[] pointSource = new double[]{584173.736, 2594514.828};
        double[] pointDest = new double[]{50.345609791, 2.114551393};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E.inverse();
        double[] result = LAMBERT2E.transform(pointSource);
        List<CoordinateOperation> ops = GeodeticDatum.NTF_PARIS.getCoordinateOperations(GeodeticDatum.WGS84);
        if (!ops.isEmpty()) {
            result = ChangeCoordinateDimension.TO3D.transform(result);
            result = ops.get(0).transform(result);
        }
        result = UnitConversion.RAD2DD.transform(result);
        assertTrue(checkEquals("Lambert 2 etendu to WGS84 degrees", result, pointDest, 10E-7));
    }

    @Test
    public void fromWGS84TOLambert2Etendu() throws Exception {
        double[] pointSource = new double[]{50.345609791, 2.114551393};
        double[] pointDest = new double[]{584173.736, 2594514.828};
        CoordinateOperationSequence WGS84_LAMB2E = new CoordinateOperationSequence(new Identifier("CTS",
                "WGS84 (lon/lat)-> Lambert 2 etendu",
                "WGS84 (lon/lat) to  Lambert 2 etendu"),
                UnitConversion.DD2RAD,
                LongitudeRotation.GREENWICH2PARIS,
                LambertConicConformal1SP.LAMBERT2E,
                CoordinateRounding.MILLIMETER);
        System.out.println(WGS84_LAMB2E.toString());
        double[] result = WGS84_LAMB2E.transform(pointSource);
        assertTrue(checkEquals("WGS84 degrees to Lambert 2 etendu", result, pointDest, 10E-3));
    }

    @Test
    public void fromLambert1ToGeographic() throws Exception {
        double[] pointSource = new double[]{1029705.083, 272723.849};
        Map<String, Measure> params1 = new HashMap<String, Measure>();
        params1.put(Parameter.LATITUDE_OF_ORIGIN,
                new Measure(55, Unit.GRAD));
        params1.put(Parameter.CENTRAL_MERIDIAN,
                new Measure(0.0, Unit.GRAD));
        params1.put(Parameter.SCALE_FACTOR, new Measure(0.999877340,
                Unit.UNIT));
        params1.put(Parameter.FALSE_EASTING, new Measure(600000.0,
                Unit.METER));
        params1.put(Parameter.FALSE_NORTHING, new Measure(200000.0,
                Unit.METER));
        LambertConicConformal1SP LAMBERT1 = new LambertConicConformal1SP(
                Ellipsoid.CLARKE1880IGN, params1);
        CoordinateOperation LAMBERT1_I = LAMBERT1.inverse();
        double[] result = LAMBERT1_I.transform(pointSource);
        result = LongitudeRotation.PARIS2GREENWICH.transform(result);
        assertTrue(checkEquals("Lambert I to Geographic in radians", result, new double[]{
                    0.87266462600, 0.14551209931}, 1E-7));
    }

    @Test
    public void fromLambert1ToLambert2Etendu() throws Exception {
        double[] pointSource = new double[]{750000.00, 300000.00};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E;
        CoordinateOperation LAMBERT1 = LambertConicConformal1SP.LAMBERT1.inverse();
        final CoordinateOperation op = new CoordinateOperationSequence(new Identifier("CTS", "L1 -> L2E",
                "From Lambert 1 to Lambert 2 etendu"), LAMBERT1, LAMBERT2E);
        double[] result = op.transform(pointSource);
        assertTrue(checkEquals("Lambert I to Lambert 2 étendu", result,
                new double[]{750283.12, 2600360.77}, 0.01));
    }

    @Test
    public void fromGeographicToLambert1() throws Exception {
        Map<String, Measure> params1 = new HashMap<String, Measure>();
        params1.put(Parameter.LATITUDE_OF_ORIGIN,
                new Measure(55, Unit.GRAD));
        params1.put(Parameter.CENTRAL_MERIDIAN,
                new Measure(0.0, Unit.GRAD));
        params1.put(Parameter.SCALE_FACTOR, new Measure(0.999877340,
                Unit.UNIT));
        params1.put(Parameter.FALSE_EASTING, new Measure(600000.0,
                Unit.METER));
        params1.put(Parameter.FALSE_NORTHING, new Measure(200000.0,
                Unit.METER));
        final LambertConicConformal1SP LAMBERT1 = new LambertConicConformal1SP(
                Ellipsoid.CLARKE1880IGN, params1);
        double[] pointSource = new double[]{0.87266462600, 0.14551209931};
        double[] result = LongitudeRotation.GREENWICH2PARIS.transform(pointSource);
        result = LAMBERT1.transform(result);
        checkEquals("From Geographic radians to Lambert 1", result, new double[]{
                    1029705.083, 272723.849}, 1E-3);
    }

    @Test
    public void fromWGS84TOLambert93() throws Exception {
        double[] pointSource = new double[]{50.345609791, 2.114551393};
        double[] pointDest = new double[]{584173.736, 2594514.828};
        CoordinateOperationSequence WGS84_LAMB2E = new CoordinateOperationSequence(new Identifier("CTS",
                "WGS84 (lon/lat)-> RGF93/Lambert 93",
                "WGS84 (lon/lat) to  RGF93/Lambert 93"),
                CoordinateSwitch.SWITCH_LAT_LON, UnitConversion.DD2RAD,
                LambertConicConformal2SP.LAMBERT93,
                CoordinateRounding.MILLIMETER);
        double[] result = WGS84_LAMB2E.transform(pointSource);
        assertTrue(checkEquals("WGS84 degrees to RGF93/Lambert 93", result, pointDest, 0.01));
    }
}
