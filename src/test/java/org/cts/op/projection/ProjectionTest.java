package org.cts.op.projection;

import org.cts.Parameter;
import org.cts.datum.Ellipsoid;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by UMichael on 31/03/2016.
 */
public class ProjectionTest {

    @Test
    // Just check that tmerc-1(tmerc(x,y)) let the coordinate unchanged +/- 1 mm
    public void testInverseProjection() throws Exception {
        UniversalTransverseMercator utm = new UniversalTransverseMercator(Ellipsoid.GRS80,
                new HashMap<String, Measure>() {{
                    put(Parameter.CENTRAL_MERIDIAN, new Measure(0, Unit.DEGREE));
                    put(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER));
                }});
        assertTrue(utm.isDirect());
        assertTrue(!utm.inverse().isDirect());
        assertTrue(!utm.inverse().toString().equals(utm.toString()));
        assertTrue(utm.inverse().inverse() == utm);
    }
}
