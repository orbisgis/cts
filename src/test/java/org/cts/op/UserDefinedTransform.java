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
package org.cts.op;

import java.util.*;

import org.cts.op.transformation.GeocentricTranslation;

import org.cts.Identifier;
import org.cts.Parameter;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.projection.LambertConicConformal1SP;
import org.cts.op.projection.LambertConicConformal2SP;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.junit.jupiter.api.Test;

import static org.cts.op.UnitConversion.createUnitConverter;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Erwan Bocher
 */
class UserDefinedTransform extends BaseCoordinateTransformTest {

    private final LongitudeRotation PARIS2GREENWICH =
            LongitudeRotation.getLongitudeRotationFrom(PrimeMeridian.PARIS);
    private final LongitudeRotation GREENWICH2PARIS =
            LongitudeRotation.getLongitudeRotationTo(PrimeMeridian.PARIS);
    private static final UnitConversion RAD2DD = createUnitConverter(Unit.RADIAN, Unit.DEGREE);
    private final static UnitConversion DD2RAD = createUnitConverter(Unit.DEGREE, Unit.RADIAN);

    /**
     * Transform a point from lambert2 etendu to WGS84
     */
    @Test
    void fromLambert2EtenduToWGS84() throws Exception {
        double[] pointSource = new double[]{584173.736, 2594514.828};
        double[] pointDest = new double[]{50.345609791, 2.114551393};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E.inverse();
        double[] result = LAMBERT2E.transform(pointSource);
        result = PARIS2GREENWICH.transform(result);
        Geographic2Geocentric geographic2Geocentric = new Geographic2Geocentric(Ellipsoid.CLARKE1880IGN);
        result = geographic2Geocentric.transform(result);
        CoordinateOperation toWG84 = GeodeticDatum.NTF_PARIS.getToWGS84();
        result = toWG84.transform(result);
        Geocentric2Geographic geocentric2Geographic = new Geocentric2Geographic(Ellipsoid.WGS84);
        result = geocentric2Geographic.transform(result);
        CoordinateOperation conv = RAD2DD;
        result = conv.transform(result);
        assertTrue(checkEquals("Lambert 2 etendu to WGS84 degrees", result, pointDest, 10E-7));
    }

    /**
     * Short circuit to transform a point from lambert2 etendu to WGS84
     */
    @Test
    void fromSCLambert2EtenduToWGS84() throws Exception {
        double[] pointSource = new double[]{584173.736, 2594514.828};
        double[] pointDest = new double[]{50.345609791, 2.114551393};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E.inverse();
        double[] result = LAMBERT2E.transform(pointSource);
        Set<CoordinateOperation> ops = GeodeticDatum.NTF_PARIS.getGeographicTransformations(GeodeticDatum.WGS84);
        if (!ops.isEmpty()) {
            result = ChangeCoordinateDimension.TO3D.transform(result);
            result = ops.iterator().next().transform(result);
        }
        result = RAD2DD.transform(result);
        assertTrue(checkEquals("Lambert 2 etendu to WGS84 degrees", result, pointDest, 10E-7));
    }

    @Test
    void fromWGS84TOLambert2Etendu() throws Exception {
        double[] pointSource = new double[]{50.345609791, 2.114551393};
        double[] pointDest = new double[]{584173.736, 2594514.828};
        CoordinateOperationSequence WGS84_LAMB2E = new CoordinateOperationSequence(new Identifier("CTS",
                "WGS84 (lat/lon)-> Lambert 2 etendu",
                "WGS84 (lat/lon) to  Lambert 2 etendu"),
                DD2RAD,
                // we must use a 3D geocentric transformation from WGS84 to NTF (cf. circe)
                ChangeCoordinateDimension.TO3D,
                new Geographic2Geocentric(Ellipsoid.WGS84),
                new GeocentricTranslation(168, 60, -320),
                new Geocentric2Geographic(Ellipsoid.CLARKE1880IGN),
                // NTF is based on Paris prime meridian
                GREENWICH2PARIS,
                LambertConicConformal1SP.LAMBERT2E,
                CoordinateRounding.MILLIMETER);
        double[] result = WGS84_LAMB2E.transform(pointSource);
        assertTrue(checkEquals("WGS84 degrees to Lambert 2 etendu", result, pointDest, 10E-3));
    }

    @Test
    void fromLambert1ToGeographic() throws Exception {
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
        result = PARIS2GREENWICH.transform(result);
        assertTrue(checkEquals("Lambert I to Geographic in radians", result, new double[]{
            0.87266462600, 0.14551209931}, 1E-7));
    }

    @Test
    void fromLambert1ToLambert2Etendu() throws Exception {
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
    void fromGeographicToLambert1() throws Exception {
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
        double[] result = GREENWICH2PARIS.transform(pointSource);
        result = LAMBERT1.transform(result);
        checkEquals("From Geographic radians to Lambert 1", result, new double[]{
            1029705.083, 272723.849}, 1E-3);
    }

    @Test
    void fromWGS84TOLambert93() throws Exception {
        double[] pointSource = new double[]{50.345609791, 2.114551393};
        //double[] pointDest = new double[]{584173.736, 2594514.828};
        double[] pointDest = new double[]{636890.740, 7027895.263}; // circe (last tab)
        CoordinateOperationSequence WGS84_LAMB2E = new CoordinateOperationSequence(new Identifier("CTS",
                "WGS84 (lat/lon)-> RGF93/Lambert 93",
                "WGS84 (lat/lon) to  RGF93/Lambert 93"),
                //CoordinateSwitch.SWITCH_LAT_LON,
                DD2RAD,
                LambertConicConformal2SP.LAMBERT93,
                CoordinateRounding.MILLIMETER);
        double[] result = WGS84_LAMB2E.transform(pointSource);
        assertTrue(checkEquals("WGS84 degrees to RGF93/Lambert 93", result, pointDest, 0.01), Arrays.toString(result));
    }
}
