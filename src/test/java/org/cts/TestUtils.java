package org.cts;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class providing methods for test purpose.
 *
 * @author Sylvain PALOMINOS (UBS LabSTICC 2020)
 */
public class TestUtils {

    /**
     * Default precision for the test according the the java double precision which is around 1.0e-16.
     */
    public static final double PRECISION = 1.0e-15;

    /**
     * Do a {@link org.junit.jupiter.api.Assertions#assertEquals(double, double, double)} with the given expected and
     * actual values with the given precision.
     * The precision is converted into a delta as request in junit.
     *
     * @param expected Expected double value.
     * @param actual Actual value.
     * @param precision Precision of the assertion.
     */
    public static void assertEqualsWithPrecision(double expected, double actual, double precision){
        double exp = Math.floor(expected == 0 ? 1 : Math.log10(Math.abs(expected)));
        //Use PRECISION as minimum delta
        double delta = Math.max(Math.pow(10, exp) * precision, PRECISION);
        assertEquals(expected, actual, delta);
    }

    /**
     * Do a {@link org.junit.jupiter.api.Assertions#assertEquals(double, double, double)} with the given expected and
     * actual values.
     * The precision used is the default one : {@link TestUtils#PRECISION}.
     *
     * @param expected Expected double value.
     * @param actual Actual value.
     */
    public static void assertEqualsWithPrecision(double expected, double actual){
        assertEqualsWithPrecision(expected, actual, PRECISION);
    }
}
