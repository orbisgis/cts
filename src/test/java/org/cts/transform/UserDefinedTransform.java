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
package org.cts.transform;

import java.util.HashMap;
import java.util.Map;
import org.cts.CoordinateOperation;
import org.cts.Ellipsoid;
import org.cts.Parameter;
import org.cts.op.LongitudeRotation;
import org.cts.op.UnitConversion;
import org.cts.op.projection.LambertConicConformal1SP;
import org.cts.units.Measure;
import org.cts.units.Unit;
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
        double[] pointDest = new double[]{2.114551393, 50.345609791};
        CoordinateOperation LAMBERT2E = LambertConicConformal1SP.LAMBERT2E.inverse();
        double[] result = LAMBERT2E.transform(pointSource);
        result = LongitudeRotation.PARIS2GREENWICH.transform(result);
        CoordinateOperation conv = UnitConversion.RAD2DD;
        result = conv.transform(result);
        System.out.println(coord2string(result));
        checkEquals("Lambert 2 to WGS84", result, pointDest, 0.01);

    }

    @Test
    public void frenchProjections() throws Exception {
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
        LOGGER.info(LAMBERT1.toString());
        final CoordinateOperation LAMBERT1_I = LAMBERT1.inverse();
        LOGGER.info(LAMBERT1_I.toString());

        Map<String, Measure> params2 = new HashMap<String, Measure>();
        params2.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(52.0,
                Unit.GRAD));
        params2.put(Parameter.CENTRAL_MERIDIAN,
                new Measure(0.0, Unit.GRAD));
        params2.put(Parameter.SCALE_FACTOR, new Measure(0.99987742,
                Unit.UNIT));
        params2.put(Parameter.FALSE_EASTING, new Measure(600000.0,
                Unit.METER));
        params2.put(Parameter.FALSE_NORTHING, new Measure(2200000.0,
                Unit.METER));
        final LambertConicConformal1SP LAMBERT2E = new LambertConicConformal1SP(
                Ellipsoid.CLARKE1880IGN, params2);
        LOGGER.info(LAMBERT2E.toString());

        // Application num�rique prise sur
        // http://professionnels.ign.fr/DISPLAY/000/526/700/5267002/transformaton.pdf
        double[] cc = new double[]{750000.00, 300000.00};
        LOGGER.info("Lambert 1 : " + coord2string(cc));
        LOGGER.info("Geographiques : "
                + coord2string(LAMBERT1_I.transform(cc)));
        cc = LAMBERT2E.transform(cc);
        LOGGER.info("Lambert 2 étendu : " + coord2string(cc));
        checkEquals("test Lambert I vers Lambert 2 étendu", cc,
                new double[]{750283.12, 2600360.77}, 0.01);

        LOGGER.info("");
        // Application
        cc = new double[]{1029705.083, 272723.849};
        LOGGER.info("Lambert 1 : " + coord2string(cc));
        cc = LAMBERT1_I.transform(cc);
        cc = LongitudeRotation.PARIS2GREENWICH.transform(cc);
        LOGGER.info(LongitudeRotation.PARIS2GREENWICH.toString());
        LOGGER.info("Geographiques : " + coord2string(cc));
        checkEquals("Lambert I vers Géographique", cc, new double[]{
                    0.87266462600, 0.14551209931}, 1E-11);

        LOGGER.info("");
        // Application
        cc = new double[]{0.87266462600, 0.14551209931};
        LOGGER.info("Géographiques : " + coord2string(cc));
        cc = LongitudeRotation.GREENWICH2PARIS.transform(cc);
        cc = LAMBERT1.transform(cc);
        LOGGER.info("Lambert 1 : " + coord2string(cc));
        checkEquals("Lambert I vers Géographique", cc, new double[]{
                    1029705.083, 272723.849}, 1E-3);

    }
}
