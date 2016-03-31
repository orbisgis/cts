package org.cts.op.projection;

import org.cts.CTSTestCase;
import org.cts.Parameter;
import org.cts.datum.Ellipsoid;
import org.cts.op.BaseCoordinateTransformTest;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by UMichael on 31/03/2016.
 */
public class TransverseMercatorTest extends CTSTestCase {

    @Test
    // Just check that tmerc-1(tmerc(x,y)) let the coordinate unchanged +/- 1 mm
    public void testTransverseMercator() throws Exception {
        TransverseMercator tmerc1 = new TransverseMercator(Ellipsoid.GRS80, new HashMap<String, Measure>(){{
            put(Parameter.SCALE_FACTOR, new Measure(1.0, Unit.UNIT));
            put(Parameter.CENTRAL_MERIDIAN, new Measure(20, Unit.DEGREE));
            put(Parameter.FALSE_EASTING, new Measure(500000.0, Unit.METER));
            put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
            put(Parameter.LATITUDE_OF_ORIGIN, new Measure(20, Unit.DEGREE));
        }});
        assertTrue(checkEquals2D("0,0 -> tmerc -> tmerc-1 -> 0,0",
                new double[]{0.0,0.0},
                tmerc1.inverse().transform(tmerc1.transform(new double[]{0.0,0.0})), 0.001));

        TransverseMercator tmerc2 = new TransverseMercator(Ellipsoid.GRS80, new HashMap<String, Measure>(){{
            put(Parameter.SCALE_FACTOR, new Measure(1.0, Unit.UNIT));
            put(Parameter.CENTRAL_MERIDIAN, new Measure(20, Unit.DEGREE));
            put(Parameter.FALSE_EASTING, new Measure(20500000.0, Unit.METER));
            put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
            put(Parameter.LATITUDE_OF_ORIGIN, new Measure(20, Unit.DEGREE));
        }});

    }

    @Test
    // Check that changing false easting parameter changes the result
    public void testTransverseMercatorFalseEasting() throws Exception {
        TransverseMercator tmerc1 = new TransverseMercator(Ellipsoid.GRS80, new HashMap<String, Measure>(){{
            put(Parameter.SCALE_FACTOR, new Measure(1.0, Unit.UNIT));
            put(Parameter.CENTRAL_MERIDIAN, new Measure(20, Unit.DEGREE));
            put(Parameter.FALSE_EASTING, new Measure(500000.0, Unit.METER));
            put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
            put(Parameter.LATITUDE_OF_ORIGIN, new Measure(20, Unit.DEGREE));
        }});
        TransverseMercator tmerc2 = new TransverseMercator(Ellipsoid.GRS80, new HashMap<String, Measure>(){{
            put(Parameter.SCALE_FACTOR, new Measure(1.0, Unit.UNIT));
            put(Parameter.CENTRAL_MERIDIAN, new Measure(20, Unit.DEGREE));
            put(Parameter.FALSE_EASTING, new Measure(20500000.0, Unit.METER));
            put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
            put(Parameter.LATITUDE_OF_ORIGIN, new Measure(20, Unit.DEGREE));
        }});
        System.out.println(tmerc2.toString());
        System.out.println(tmerc2.inverse().toString());
        double[] dd = new double[]{0.0,0.0};
        assertTrue(!checkEquals2D("", tmerc1.transform(new double[]{0.0,0.0}), tmerc2.transform(new double[]{0.0,0.0}), 1000));
    }

}
