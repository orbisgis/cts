package org.cts.util;

import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by Michaël on 08/03/14.
 */
public class AngleFormatTest {

    @Test
    public void parseAngleInDMSTest() {
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°")).equals(23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°")).equals(-23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°1'")).equals(23.0 + 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°1'")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'")).equals(23.0 + 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'01")).equals(23.0 + 1.0/60 + 1.0/3600));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01'01")).equals(-23.0 - 1.0/60 - 1.0/3600));
    }

    @Test
     public void parseAngleInDMSHTest() {
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°N")).equals(23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°N")).equals(-23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°1'N")).equals(23.0 + 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°1'N")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'N")).equals(23.0 + 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01N")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'01N")).equals(23.0 + 1.0/60 + 1.0/3600));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01'01N")).equals(-23.0 - 1.0/60 - 1.0/3600));
    }

    @Test
    public void parseAngleInDMSHdoubleNegTest() {
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°S")).equals(-23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°S")).equals(-23.0));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°1'S")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°1'S")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'S")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01S")).equals(-23.0 - 1.0/60));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("23°01'01S")).equals(-23.0 - 1.0/60 - 1.0/3600));
        assertTrue("Parse angle", new Double(AngleFormat.parseAngle("-23°01'01S")).equals(-23.0 - 1.0/60 - 1.0/3600));
    }
}
