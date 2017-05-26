package org.cts.parser.prj;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by UMichael on 26/05/2017.
 */
public class PrjWriterTest {

    @Test
    public void testPrettyRoundingIntegerValues() {
        // Round values around 0
        assertEquals("0", PrjWriter.prettyRound(0.0, 1E-11));
        assertEquals("0", PrjWriter.prettyRound(0.000000000001, 1E-11));
        assertEquals("0", PrjWriter.prettyRound(-0.000000000001, 1E-11));
        // but not if difference > 1E-11
        assertEquals("0.000001", PrjWriter.prettyRound(0.000001, 1E-11));
        // Round values if difference < 1E-11 for small values
        assertEquals("99", PrjWriter.prettyRound(99.0, 1E-11));
        assertEquals("99", PrjWriter.prettyRound(99.000000000001, 1E-11));
        assertEquals("-99", PrjWriter.prettyRound(-99.000000000001, 1E-11));
        assertEquals("100", PrjWriter.prettyRound(99.999999999999, 1E-11));
        assertEquals("-100", PrjWriter.prettyRound(-99.999999999999, 1E-11));
        // Do not round for larger differences
        assertEquals("99.000001", PrjWriter.prettyRound(99.000001, 1E-11));
        assertEquals("-99.000001", PrjWriter.prettyRound(-99.000001, 1E-11));
        // Round values if difference < 1E-4 for values > 400
        assertEquals("100000", PrjWriter.prettyRound(100000.00005, 1E-4));
        assertEquals("100000", PrjWriter.prettyRound(99999.99995, 1E-4));
    }

    @Test
    public void testPrettyRoundingFractionalValues() {
        assertEquals("0.3333333333333333", PrjWriter.prettyRound(1.0/3.0, 1E-11));
        assertEquals("-0.3333333333333333", PrjWriter.prettyRound(-1.0/3.0, 1E-11));
        assertEquals("0.3333333333333333", PrjWriter.prettyRound(0.3333333333333333, 1E-11));
        assertEquals("0.6666666666666666", PrjWriter.prettyRound(0.6666666666666667, 1E-11));
        // improve precision if number has been truncated to 12 decimals or more
        assertEquals("0.3333333333333333", PrjWriter.prettyRound(0.333333333333, 1E-11));
        assertEquals("0.3333333333333333", PrjWriter.prettyRound(0.3333333333331987, 1E-11));
        assertEquals("0.6666666666666666", PrjWriter.prettyRound(0.6666666666671111, 1E-11));
        // ...but not if the number has been too much rounded
        assertEquals("0.33333333333", PrjWriter.prettyRound(0.33333333333, 1E-11));
        assertEquals("0.66666666667", PrjWriter.prettyRound(0.66666666667, 1E-11));
        // For large values, round if difference is less than 1E-4
        assertEquals("200000.5", PrjWriter.prettyRound(200000.5000065, 1E-4));
        assertEquals("-200000.5", PrjWriter.prettyRound(-200000.4999999, 1E-4));
    }

    @Test
    public void testPrettyRoundingSpecialValues() {
        assertEquals("3.141592653589793", PrjWriter.prettyRound(Math.PI, 1E-11));
        assertEquals("0.3183098861837907", PrjWriter.prettyRound(1.0/Math.PI, 1E-11));
        assertEquals("0.7853981633974483", PrjWriter.prettyRound(45.0*Math.PI/180.0, 1E-11));
        assertEquals("0.7853981633974483", PrjWriter.prettyRound(0.7853981633974000, 1E-11));
        assertEquals("0.41887902047863906", PrjWriter.prettyRound(24.0*Math.PI/180.0, 1E-11));
        assertEquals("0.41887902047863906", PrjWriter.prettyRound(0.4188790204786, 1E-11));
        assertEquals("-0.39269908169872414", PrjWriter.prettyRound(-22.5*Math.PI/180.0, 1E-11));
        assertEquals("-0.39269908169872414", PrjWriter.prettyRound(-0.39269908169872, 1E-11));
    }
}
