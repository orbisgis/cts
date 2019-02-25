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

import org.cts.CTSTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test {@link org.cts.util.Complex} class</p>
 *
 * @author Michaël Michaud
 */
class TestComplex extends CTSTestCase {

    private static Complex c0 = new Complex();
    private static Complex c1 = new Complex(1.0, 0.0);
    private static Complex ci = Complex.i;
    private static Complex a = new Complex(1.0, 2.0);
    private static Complex b = new Complex(3.0, 4.0);
    private static Complex e = new Complex(Math.PI / 4);
    private static Complex r = new Complex(3.0 * Math.sqrt(2.), Math.PI / 4);
    private static Complex z1 = Complex.createComplexFromA(30.0 * Math.PI / 180.0);
    private static Complex z2 = Complex.createComplexFromRA(2.0, 30.0 * Math.PI / 180.0);
    private static Complex z = new Complex(0.9, 0.05);

    @Test
    void testComplexConstruction() {
        LOGGER.info("Complex construction");
        Assertions.assertEquals("[0.0 + 0.0i]", c0.toString());
        Assertions.assertEquals("[1.0 + 0.0i]", c1.toString());
        Assertions.assertEquals("[0.0 + 1.0i]", ci.toString(), "ci = Complex.i");
        Assertions.assertEquals("[1.0 + 2.0i]", a.toString(), " a = Complex(1.0, 2.0)");
        Assertions.assertEquals("[3.0 + 4.0i]", b.toString(), " b = Complex(3.0, 4.0)");
        Assertions.assertEquals("[0.7853981633974483 + 0.0i]", e.toString(), " e = Complex(PI/4)");
        Assertions.assertEquals("[4.242640687119286 + 0.7853981633974483i]", r.toString(), " r = Complex(3.0*sqrt(2), PI/4)");
        assertEquals(z1, new Complex(0.8660254037844387, 0.5), "z1 = createComplexFromA(30\u00B0)");
        assertEquals(z2, new Complex(1.7320508075688774, 1.0), "z2 = createComplexFromRA(2, 30\u00B0)");
    }

    @Test
    void testFunctions() {
        LOGGER.info("");
        LOGGER.info("Arithmetic Fonctions (with z = 0.9+0.05i)");
        assertEquals(a.plus(b), new Complex(4.0, 6.0), "a.plus(b)");
        assertEquals(a.times(b), new Complex(-5.0, 10.0), "a.times(b)");
        assertEquals(b.axpb(a, r), new Complex(-0.7573593128807143, 10.78539816339745), "a*b+r = b.axpb(a,r)");

        LOGGER.info("");
        LOGGER.info("Trigonometric Fonctions (with z = 0.9+0.05i)");
        assertEquals(Complex.sin(z), new Complex(0.78430627227290049899, 0.0310934502400778464295), "sin(z)");
        assertEquals(Complex.cos(z), new Complex(0.62238714262208909277, -0.0391826668320266634735), "cos(z)");
        assertEquals(Complex.tan(z), new Complex(1.2520507005952087298, 0.128781798673777431448), "tan(z)");
        LOGGER.info("");
        assertEquals(Complex.sinh(z), new Complex(1.0252338471008281991759, 0.07162446703784886380476), "sinh(z)");
        assertEquals(Complex.cosh(z), new Complex(1.431295400635444387199, 0.0513044531933513685934), "cosh(z)");
        assertEquals(Complex.tanh(z), new Complex(0.7171701494277074825709, 0.02433490994200271762601), "tanh(z)");
        LOGGER.info("");
        assertEquals(Complex.exp(z), new Complex(2.4565292477362725863753, 0.122928920231200232398), "exp(z)");
        assertEquals(Complex.ln(z), new Complex(-0.10381968238912225080772, 0.055498505245716835557198), "ln(z)");
        assertEquals(Complex.asin(z), new Complex(1.107148717794090503017, 0.111571775657104877883), "asin(z)");
        assertEquals(Complex.atanh(z), new Complex(1.416606672028108040124, 0.244978663126864154172), "atanh(z)");
    }

    private void assertEquals(Complex o1, Complex o2, String test) {
        if (Math.abs(o1.re() - o2.re()) <= Math.max(Math.ulp(o1.re()), Math.ulp(o2.re()))
                && Math.abs(o1.im() - o2.im()) <= Math.max(Math.ulp(o1.im()), Math.ulp(o2.im()))) {
            LOGGER.info("TRUE : " + test + " " + o1 + " = " + o2 + " \u2213  1 ulp");

        } else {
            LOGGER.info("FALSE : " + test + " distance " + o1 + " - " + o2 + " > 1 ulp");
        }
    }
}
