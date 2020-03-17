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

package org.cts.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.PI;
import static org.cts.TestUtils.PRECISION;
import static org.cts.TestUtils.assertEqualsWithPrecision;
import static org.cts.util.Complex.ONE;
import static org.cts.util.Complex.i;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test dedicated to the {@link org.cts.util.Complex} class
 *
 * @author Michaël Michaud
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
class ComplexTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexTest.class);

    @Test
    void complexTest(){
        Complex c0 = new Complex();
        assertEquals(0.0, c0.re());
        assertEquals(0.0, c0.im());

        assertEquals(1.0, ONE.re());
        assertEquals(0.0, ONE.im());

        assertEquals(0.0, i.re());
        assertEquals(1.0, i.im());

        Complex c1 = Complex.cartesian(-5.36);
        assertEquals(-5.36, c1.re());
        assertEquals(0, c1.im());

        Complex c2 = Complex.cartesian(5.36, -7.56);
        assertEquals(5.36, c2.re());
        assertEquals(-7.56, c2.im());

        Complex c3 = Complex.cartesian(-5.36, 7.56);
        assertEquals(-5.36, c3.re());
        assertEquals(7.56, c3.im());

        Complex c4 = Complex.polar(-PI/3);
        assertEqualsWithPrecision(0.5, c4.re(), PRECISION);
        assertEqualsWithPrecision(-0.866025403784438, c4.im(), PRECISION);

        Complex c5 = Complex.polar(5, -PI/3);
        assertEqualsWithPrecision(2.5, c5.re(), PRECISION);
        assertEqualsWithPrecision(-4.330127018922193, c5.im(), PRECISION);

        Complex c6 = Complex.polar(5, PI/3);
        assertEqualsWithPrecision(2.5, c6.re(), PRECISION);
        assertEqualsWithPrecision(4.330127018922193, c6.im(), PRECISION);

        Complex c7 = Complex.polar(-5, PI/3);
        assertEqualsWithPrecision(-2.5, c7.re(), PRECISION);
        assertEqualsWithPrecision(-4.330127018922193, c7.im(), PRECISION);

        Complex c8 = new Complex(c7);
        assertEqualsWithPrecision(-2.5, c8.re(), PRECISION);
        assertEqualsWithPrecision(-4.330127018922193, c8.im(), PRECISION);
    }

    @Test
    void deprecatedComplexTest(){
        Complex c0 = new Complex(5.36);
        assertEquals(5.36, c0.re());
        assertEquals(0, c0.im());

        Complex c1 = new Complex(5.36, 7.56);
        assertEquals(5.36, c1.re());
        assertEquals(7.56, c1.im());

        Complex c3 = Complex.createComplexFromA(PI/3);
        assertEqualsWithPrecision(0.5, c3.re(), PRECISION);
        assertEqualsWithPrecision(0.866025403784438, c3.im(), PRECISION);

        Complex c4 = Complex.createComplexFromRA(-5, PI/3);
        assertEqualsWithPrecision(-2.5, c4.re(), PRECISION);
        assertEqualsWithPrecision(-4.330127018922193, c4.im(), PRECISION);
    }

    @Test
    void equalsTest(){
        Complex c0 = Complex.cartesian(-2.5000000000000004, -4.330127018922193);
        Complex c1 = Complex.cartesian(-2.5000000000000007, -4.330127018922193);
        Complex c2 = Complex.cartesian(-2.5000000000000004, -4.330127018922194);
        Complex c3 = Complex.polar(-5, PI/3);
        Complex c4 = Complex.polar(-6, PI/3);
        Complex c5 = Complex.polar(-5, PI/4);

        assertEquals(c0, c3);
        assertEquals(c3, c0);

        assertNotEquals(c0, c2);
        assertNotEquals(c0, c1);
        assertNotEquals(c3, c4);
        assertNotEquals(c3, c5);
        assertNotEquals(c3, "String");
    }

    @Test
    void hashCodeTest(){
        Complex c0 = Complex.cartesian(-2.5, -4.330127018922193);
        Complex c1 = Complex.polar(-5, PI/3);

        assertNotEquals(0.0, c0.hashCode());
        assertNotEquals(0.0, c1.hashCode());
        assertNotEquals(c0.hashCode(), c1.hashCode());

        assertEquals(c0.hashCode(), c0.hashCode());
        assertEquals(c1.hashCode(), c1.hashCode());
    }

    @Test
    void conjTest(){
        assertEquals(0.0, new Complex().conj().re());
        assertEquals(-0.0, new Complex().conj().im());

        assertEquals(1.0, ONE.conj().re());
        assertEquals(-0.0, ONE.conj().im());

        assertEquals(0.0, i.conj().re());
        assertEquals(-1.0, i.conj().im());

        Complex c0 = Complex.cartesian(5.36, -7.56);
        assertEquals(5.36, c0.conj().re());
        assertEquals(7.56, c0.conj().im());

        Complex c1 = Complex.polar(-5, PI/3);
        assertEqualsWithPrecision(-2.5, c1.conj().re(), PRECISION);
        assertEqualsWithPrecision(4.330127018922193, c1.conj().im(), PRECISION);
    }

    @Test
    void isRealTest(){
        assertTrue(new Complex().isReal());
        assertTrue(ONE.isReal());
        assertTrue(Complex.cartesian(0, 1e-13).isReal());

        assertFalse(i.isReal());
        assertFalse(Complex.cartesian(5.36, 7.56).isReal());
        assertFalse(Complex.polar(-5, PI/3).isReal());
        assertFalse(Complex.cartesian(0, 1e-12).isReal());
    }

    @Test
    void magTest(){
        assertEquals(0.0, new Complex().mag());
        assertEquals(1.0, ONE.mag());
        assertEquals(1.0, i.mag());

        assertEquals(5.0, Complex.cartesian(-3, 4).mag());
        assertEquals(5.0, Complex.cartesian(3, -4).mag());
    }

    @Test
    void doubleValueTest(){
        assertEquals(0.0, new Complex().doubleValue());
        assertEquals(1.0, ONE.doubleValue());
        assertEquals(0.0, i.doubleValue());

        Complex c1 = Complex.cartesian(-5.36);
        assertEquals(-5.36, c1.doubleValue());
        Complex c2 = Complex.cartesian(5.36, -7.56);
        assertEquals(0.0, c2.doubleValue());
        Complex c3 = Complex.polar(-5, PI/3);
        assertEquals(0.0, c3.doubleValue());
    }

    @Test
    void floatValueTest(){
        assertEquals(0.0, new Complex().floatValue());
        assertEquals(1.0, ONE.floatValue());
        assertEquals(0.0, i.floatValue());

        Complex c1 = Complex.cartesian(-5.36);
        assertEqualsWithPrecision(-5.36, c1.floatValue(), 1e-6);
        Complex c2 = Complex.cartesian(5.36, -7.56);
        assertEquals(0.0, c2.floatValue());
        Complex c3 = Complex.polar(-5, PI/3);
        assertEquals(0.0, c3.floatValue());
    }

    @Test
    void longValueTest(){
        assertEquals(0, new Complex().longValue());
        assertEquals(1, ONE.longValue());
        assertEquals(0, i.longValue());

        Complex c1 = Complex.cartesian(-5.36);
        assertEquals(-5, c1.longValue());
        Complex c2 = Complex.cartesian(5.36, -7.56);
        assertEquals(0, c2.longValue());
        Complex c3 = Complex.polar(-5, PI/3);
        assertEquals(0, c3.longValue());
    }

    @Test
    void intValueTest(){
        assertEquals(0, new Complex().intValue());
        assertEquals(1, ONE.intValue());
        assertEquals(0, i.intValue());

        Complex c1 = Complex.cartesian(-5.36);
        assertEquals(-5, c1.intValue());
        Complex c2 = Complex.cartesian(5.36, -7.56);
        assertEquals(0, c2.intValue());
        Complex c3 = Complex.polar(-5, PI/3);
        assertEquals(0, c3.intValue());
    }

    @Test
    void plusTest(){
        Complex c0 = new Complex();
        assertEquals(0.0, c0.re());
        assertEquals(0.0, c0.im());

        Complex c1 = c0.plus(Complex.cartesian(4));
        assertEquals(4.0, c1.re());
        assertEquals(0.0, c1.im());

        Complex c2 = c1.plus(Complex.cartesian(0, -5));
        assertEquals(4.0, c2.re());
        assertEquals(-5.0, c2.im());

        Complex c3 = c2.plus(Complex.cartesian(-2.22, 3.2));
        assertEqualsWithPrecision(1.78, c3.re());
        assertEqualsWithPrecision(-1.8, c3.im());

        Complex c4 = c3.plus(1.7);
        assertEqualsWithPrecision(3.48, c4.re());
        assertEqualsWithPrecision(-1.8, c4.im());
    }

    @Test
    void minusTest(){
        Complex c0 = new Complex();
        assertEquals(0.0, c0.re());
        assertEquals(0.0, c0.im());

        Complex c1 = c0.minus(Complex.cartesian(4));
        assertEquals(-4.0, c1.re());
        assertEquals(0.0, c1.im());

        Complex c2 = c1.minus(Complex.cartesian(0, -5));
        assertEquals(-4.0, c2.re());
        assertEquals(5.0, c2.im());

        Complex c3 = c2.minus(Complex.cartesian(-2.22, 3.2));
        assertEqualsWithPrecision(-1.78, c3.re());
        assertEqualsWithPrecision(1.8, c3.im());

        Complex c4 = c3.minus(1.7);
        assertEqualsWithPrecision(-3.48, c4.re());
        assertEqualsWithPrecision(1.8, c4.im());
    }

    @Test
    void multiplyByTest(){
        Complex c0 = i.multiplyBy(ONE);
        assertEquals(0.0, c0.re());
        assertEquals(1.0, c0.im());

        Complex c1 = Complex.cartesian(2.0, 5.0);
        Complex c2 = Complex.cartesian(3.0, 4.0);
        assertEquals(2.0*3.0-5.0*4.0, c1.multiplyBy(c2).re());
        assertEquals(2.0*4.0+5.0*3.0, c1.multiplyBy(c2).im());

        assertEquals(4.2, c1.multiplyBy(2.1).re());
        assertEquals(10.5, c1.multiplyBy(2.1).im());
    }

    @Test
    void timesTest(){
        Complex c0 = i.times(ONE);
        assertEquals(0.0, c0.re());
        assertEquals(1.0, c0.im());

        Complex c1 = Complex.cartesian(2.0, 5.0);
        Complex c2 = Complex.cartesian(3.0, 4.0);
        assertEquals(2.0*3.0-5.0*4.0, c1.times(c2).re());
        assertEquals(2.0*4.0+5.0*3.0, c1.times(c2).im());

        assertEquals(4.2, c1.times(2.1).re());
        assertEquals(10.5, c1.times(2.1).im());
    }

    @Test
    void divideByTest(){
        Complex c1 = Complex.cartesian(2.0, 5.0);
        Complex c2 = Complex.cartesian(3.0, 4.0);
        assertEqualsWithPrecision((1/(2.0*2.0+5.0*5.0))*(2.0*3.0+5.0*4.0), c2.divideBy(c1).re());
        assertEqualsWithPrecision((1/(2.0*2.0+5.0*5.0))*(4.0*2.0-3.0*5.0), c2.divideBy(c1).im());

        assertEquals(10.0, c1.divideBy(0.2).re());
        assertEquals(25.0, c1.divideBy(0.2).im());

        assertThrows(ArithmeticException.class, () -> new Complex().divideBy(Complex.cartesian(1e-14, 1e-14)));
    }

    @Test
    void axpbTest(){
        Complex c1 = Complex.cartesian(2.0, 5.0);
        Complex c2 = Complex.cartesian(3.0, 4.0);
        Complex c3 = Complex.cartesian(6.0, 1.0);

        assertEquals(10.3, c1.axpb(2.0, 6.3).re());
        assertEquals(10.0, c1.axpb(2.0, 6.3).im());

        assertEquals(2.0*3.0-5.0*4.0 + 6.0, c1.axpb(c2, c3).re());
        assertEquals(2.0*4.0+5.0*3.0 + 1.0, c1.axpb(c2, c3).im());
    }

    @Test
    void sinTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(67.47891524, Complex.sin(c0).re(), 1e-9);
        assertEqualsWithPrecision(-30.87943134, Complex.sin(c0).im(), 1e-9);
    }

    @Test
    void cosTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(-30.88223532, Complex.cos(c0).re(), 1e-9);
        assertEqualsWithPrecision(-67.47278844, Complex.cos(c0).im(), 1e-9);
    }

    @Test
    void tanTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(-6.87216388e-5, Complex.tan(c0).re(), 1e-8);
        assertEqualsWithPrecision(1.00005935, Complex.tan(c0).im(), 1e-8);
    }

    @Test
    void powTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(2.0959958, Complex.pow(c0).re(), 1e-8);
        assertEqualsWithPrecision(-7.08554526, Complex.pow(c0).im(), 1e-8);
    }

    @Test
    void sinhTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(1.02880315, Complex.sinh(c0).re(), 1e-8);
        assertEqualsWithPrecision(-3.60766077, Complex.sinh(c0).im(), 1e-8);
    }

    @Test
    void coshTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(1.06719265, Complex.cosh(c0).re(), 1e-8);
        assertEqualsWithPrecision(-3.47788449, Complex.cosh(c0).im(), 1e-8);
    }

    @Test
    void tanhTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(1.03100801, Complex.tanh(c0).re(), 1e-8);
        assertEqualsWithPrecision(-0.02055301, Complex.tanh(c0).im(), 1e-6);

        assertEqualsWithPrecision(2.0, Complex.tanh(2.0));
    }

    @Test
    void atanTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(1.4998478, Complex.atan(c0).re(), 1e-8);
        assertEqualsWithPrecision(0.1732868, Complex.atan(c0).im(), 1e-7);
    }

    @Test
    void expTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(2.0959958, Complex.exp(c0).re(), 1e-8);
        assertEqualsWithPrecision(-7.08554526, Complex.exp(c0).im(), 1e-8);
    }

    @Test
    void lnTest(){
        Complex c0 = Complex.polar(5, PI/3);
        assertEqualsWithPrecision(Math.log(5), Complex.ln(c0).re());
        assertEqualsWithPrecision(PI/3, Complex.ln(c0).im());
    }

    @Test
    void sqrtTest(){
        Complex c0 = Complex.polar(5, PI/3);
        Complex c1 = Complex.polar(Math.sqrt(5), PI/6);
        assertEqualsWithPrecision(c1.re(), Complex.sqrt(c0).re());
        assertEqualsWithPrecision(c1.im(), Complex.sqrt(c0).im());
    }

    @Test
    void asinTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(0.37467080482552484, Complex.asin(c0).re());
        assertEqualsWithPrecision(2.3830308809003307, Complex.asin(c0).im(), 1e-8);
    }

    @Test
    void atanhTest(){
        Complex c0 = Complex.cartesian(2.0, 5.0);
        assertEqualsWithPrecision(0.06706599664866988, Complex.atanh(c0).re());
        assertEqualsWithPrecision(1.399284356584545, Complex.atanh(c0).im());

        assertEqualsWithPrecision(0.34657359027997264, Complex.atanh(3.0));
    }

    @Test
    void toStringTest() {
        LOGGER.info("Complex construction");
        Assertions.assertEquals("[0.0 + 0.0i]", new Complex().toString());
        Assertions.assertEquals("[1.0 + 0.0i]", ONE.toString());
        Assertions.assertEquals("[0.0 + 1.0i]", i.toString());
        Assertions.assertEquals("[1.0 - 2.0i]", Complex.cartesian(1.0, -2.0).toString());
        Assertions.assertEquals("[3.0 + 0.0i]", Complex.cartesian(3.0).toString());
        Assertions.assertEquals("[0.7071067811865476 + 0.7071067811865475i]", Complex.polar(PI/4).toString());
        Assertions.assertEquals("[3.0000000000000004 + 3.0i]", Complex.polar(3.0*Math.sqrt(2.0), PI/4).toString());
    }
}
