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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Complex number, arithmetic, and complex functions.
 *
 * @author Michaël Michaud
 *
 * @see <a href="http://www.ngs.noaa.gov/gps-toolbox/Hehl/Complex.java"> taken
 * from Hehl</a> author hehl@tfh-berlin.de version 17-May-2005
 */
public final class Complex extends Number {

    private static final Logger LOGGER = LoggerFactory.getLogger(Complex.class);

    /**
     * Complex unit i. It holds i*i = -1.
     */
    public static final Complex i = Complex.cartesian(0.0, 1.0);
    /**
     * Complex unit 1.
     */
    public static final Complex ONE = Complex.cartesian(1.0, 0.0);
    /**
     * Real part of this {@link Complex}.
     */
    private final double re;
    /**
     * Imaginary part of this {@link Complex}.
     */
    private final double im;

    /**
     * Constructs a {@link Complex} number with its cartesian arguments : imaginary and real parts.
     *
     * @param re Real part of the number.
     * @param im Imaginary part of the number.
     * @return {@link Complex} number with imaginary and real parts.
     */
    public static Complex cartesian(double re, double im){
        return new Complex(re, im);
    }

    /**
     * Constructs a {@link Complex} number with its cartesian arguments : imaginary to 0 and real parts.
     *
     * @param re Real part of the number.
     * @return {@link Complex} number with imaginary to 0 and real parts.
     */
    public static Complex cartesian(double re){
        return new Complex(re, 0);
    }

    /**
     * Constructs a {@link Complex} number from its polar arguments : radius and phi.
     *
     * @param radius Radius in polar coordinates.
     * @param phi Phi angle in polar coordinates.
     * @return {@link Complex} number from its polar arguments : radius and phi.
     */
    public static Complex polar(double radius, double phi){
        return new Complex(radius * Math.cos(phi), radius * Math.sin(phi));
    }

    /**
     * Constructs a {@link Complex} number from its polar arguments : radius to 1 and phi.
     *
     * @param phi Phi angle in polar coordinates.
     * @return {@link Complex} number from its polar arguments : radius to 1 and phi.
     */
    public static Complex polar(double phi){
        return new Complex(Math.cos(phi), Math.sin(phi));
    }

    /**
     * Constructs a {@link Complex} number from real part and imaginary part.
     * Deprecated, use instead {@link Complex#cartesian(double, double)}
     *
     * @param re real part
     * @param im imaginary part
     */
    @Deprecated
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Constructs the {@link Complex} number 0+0i.
     */
    public Complex() {
        this(0.0, 0.0);
    }

    /**
     * Constructs a {@link Complex} number with imaginary part zero.
     * Deprecated, use instead {@link Complex#cartesian(double)}
     *
     * @param re real part
     */
    @Deprecated
    public Complex(double re) {
        this(re, 0.0);
    }

    /**
     * Copy the {@link Complex} number in parameter.
     *
     * @param z {@link Complex} number
     */
    public Complex(Complex z) {
        this(z.re(), z.im());
    }

    /**
     * Returns a {@link Complex} number with magnitude one and arbitrary argument.
     * Deprecated, use instead {@link Complex#polar(double)}
     *
     * @param phi argument
     */
    @Deprecated
    public static Complex createComplexFromA(double phi) {
        return Complex.cartesian(Math.cos(phi), Math.sin(phi));
    }

    /**
     * Returns a {@link Complex} number from radius/magnitude and argument.
     * Deprecated, use instead {@link Complex#polar(double, double)}
     *
     * @param radius radius
     * @param phi    argument
     */
    @Deprecated
    public static Complex createComplexFromRA(double radius, double phi) {
        return Complex.cartesian(radius * Math.cos(phi), radius * Math.sin(phi));
    }

    /**
     * Returns true if the object in parameter is equals to this.
     *
     * @param obj Object to test.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex) {
            return re == ((Complex) obj).re() && im == ((Complex) obj).im();
        }
        return false;
    }

    /**
     * Returns the {@link Complex} hash code.
     *
     * @return The {@link Complex} hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Double.valueOf(this.re).hashCode();
        hash = 83 * hash + Double.valueOf(this.im).hashCode();
        return hash;
    }

    /**
     * Returns the real part of the {@link Complex} number.
     *
     * @return The real part of the {@link Complex} number.
     */
    public double re() {
        return re;
    }

    /**
     * Returns the imaginary part of the {@link Complex} number.
     *
     * @return The imaginary part of the {@link Complex} number.
     */
    public double im() {
        return im;
    }

    /**
     * Returns the conjugate {@link Complex} number.
     *
     * @return The conjugate {@link Complex} number.
     */
    public Complex conj() {
        return Complex.cartesian(re(), -im());
    }

    /**
     * Return true, if imaginary part of {@link Complex} number is (numerically) zero.
     * For geodetic calculations, a value strictly lesser than 1.E-12 can be considered as null.
     *
     * @return True, if imaginary part of {@link Complex} number is (numerically) zero.
     */
    public boolean isReal() {
        return 1.E-12 > Math.abs(im);
    }

    /**
     * Return the magnitude of {@link Complex} number.
     *
     * @return The magnitude of {@link Complex} number.
     */
    public double mag() {
        return Math.hypot(re, im);
    }

    @Override
    public double doubleValue() {
        if (!isReal()) {
            LOGGER.warn("The complex number is not a pure real.");
            return 0.0;
        }
        return re;
    }

    @Override
    public float floatValue() {
        if (!isReal()) {
            LOGGER.warn("The complex number is not a pure real.");
            return 0.0f;
        }
        return (float) re;
    }

    @Override
    public long longValue() {
        if (!isReal()) {
            LOGGER.warn("The complex number is not a pure real.");
            return 0;
        }
        return (long) re;
    }

    @Override
    public int intValue() {
        if (!isReal()) {
            LOGGER.warn("The complex number is not a pure real.");
            return 0;
        }
        return (int) re;
    }

    /**
     * Returns the argument of this {@link Complex} number.
     *
     * @return The argument of this {@link Complex} number.
     */
    public double arg() {
        return Math.atan2(im, re);
    }

    /**
     * Provides sum of this and right hand side.
     *
     * @param z Right hand side.
     * @return Sum of this and z.
     */
    public Complex plus(Complex z) {
        return Complex.cartesian(re + z.re(), im + z.im());
    }

    /**
     * Provides sum of this and a real number.
     *
     * @param x The real number to add.
     * @return Sum of this and x.
     */
    public Complex plus(double x) {
        return Complex.cartesian(re() + x, im());
    }

    /**
     * Provides difference of this and right hand side.
     *
     * @param z Right hand side.
     * @return Difference of this and z.
     */
    public Complex minus(Complex z) {
        return Complex.cartesian(re - z.re(), im - z.im());
    }

    /**
     * Provides difference of this and a real number.
     *
     * @param x The real number.
     * @return Difference of this and x.
     */
    public Complex minus(double x) {
        return Complex.cartesian(re() - x, im());
    }

    /**
     * Provides product of this and {@link Complex} right hand side.
     * Deprecated, use instead {@link Complex#multiplyBy(Complex)}.
     *
     * @param z Right hand side.
     * @return Product of this and z.
     */
    @Deprecated
    public Complex times(Complex z) {
        return Complex.cartesian(re * z.re() - im * z.im(), re * z.im() + im * z.re());
    }

    /**
     * Provides product of this and {@link Complex} right hand side.
     *
     * @param z Right hand side.
     * @return Product of this and z.
     */
    public Complex multiplyBy(Complex z) {
        return Complex.cartesian(re * z.re() - im * z.im(), re * z.im() + im * z.re());
    }

    /**
     * Provides product of this and double right hand side.
     * Deprecated, use instead {@link Complex#multiplyBy(double)}.
     *
     * @param x Right hand side.
     * @return Product of this and z.
     */
    @Deprecated
    public Complex times(double x) {
        return Complex.cartesian(x * re, x * im);
    }

    /**
     * Provides product of this and float right hand side.
     *
     * @param x Right hand side.
     * @return Product of this and z.
     */
    public Complex multiplyBy(double x) {
        return Complex.cartesian(x * re, x * im);
    }

    /**
     * Computes fraction between this and the {@link Complex} number z.
     *
     * @param z {@link Complex} right hand side
     * @return Fraction of this and z
     * @throws ArithmeticException if mag(z) == 0
     */
    public Complex divideBy(Complex z) throws ArithmeticException {
        double rz = z.mag();
        if (Math.abs(rz) < 1.E-12) {
            throw new ArithmeticException("Complex.divideBy cannot divide by a Complex with magnitude zero");
        }
        return Complex.cartesian(
                (re * z.re() + im * z.im()) / (rz * rz),
                (im * z.re() - re * z.im()) / (rz * rz));
    }

    /**
     * Computes fraction between this and the double number z.
     *
     * @param x Double right hand side.
     * @return Fraction of this and x.
     */
    public Complex divideBy(double x) {
        return Complex.cartesian(re / x, im / x);
    }

    /**
     * Computes linear combination 'a multiplyBy x plus b'.
     *
     * @param a Real factor.
     * @param b Real number.
     * @return a multiplyBy this plus b.
     */
    public Complex axpb(double a, double b) {
        return new Complex(multiplyBy(a).plus(b));
    }

    /**
     * Computes linear combination 'a multiplyBy x plus b'.
     *
     * @param a {@link Complex} factor
     * @param b {@link Complex} number
     * @return a multiplyBy this plus b
     */
    public Complex axpb(Complex a, Complex b) {
        return new Complex(multiplyBy(a).plus(b));
    }

    /**
     * Computes {@link Complex} sine.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} sine.
     */
    public static Complex sin(Complex z) {
        double _re = Math.sin(z.re()) * Math.cosh(z.im());
        double _im = Math.cos(z.re()) * Math.sinh(z.im());
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes {@link Complex} cosine.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} cosine.
     */
    public static Complex cos(Complex z) {
        double _re = Math.cos(z.re()) * Math.cosh(z.im());
        double _im = -Math.sin(z.re()) * Math.sinh(z.im());
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes {@link Complex} tangent.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} cosine.
     */
    public static Complex tan(Complex z) {
        double nenner = Math.cos(2. * z.re()) + Math.cosh(2 * z.im());
        double _re = Math.sin(2. * z.re()) / nenner;
        double _im = Math.sinh(2. * z.im()) / nenner;
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes {@link Complex} power.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} power.
     */
    public static Complex pow(Complex z) {
        double ex = Math.pow(Math.E, z.re());
        return Complex.cartesian(ex * Math.cos(z.im()), ex * Math.sin(z.im()));
    }

    /**
     * Computes hyperbolic sine.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} hyperbolic sine.
     */
    public static Complex sinh(Complex z) {
        return new Complex((pow(z).minus(pow(z.multiplyBy(-1.)))).multiplyBy(0.5));
    }

    /**
     * Computes hyperbolic cosine.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} hyperbolic cosine.
     */
    public static Complex cosh(Complex z) {
        double _re = Math.cosh(z.re()) * Math.cos(z.im());
        double _im = Math.sinh(z.re()) * Math.sin(z.im());
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes hyperbolic tangent.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex}hyperbolic tangent.
     */
    public static Complex tanh(Complex z) throws ArithmeticException {
        return new Complex(sinh(z).divideBy(cosh(z)));
    }

    /**
     * Computes hyperbolic tangent of a double using complex arithmetic.
     *
     * @param x double argument.
     * @return Double hyperbolic tangent.
     */
    public static double tanh(double x) throws ArithmeticException {
        return Complex.cartesian(x, 0.0).re();
    }

    /**
     * Computes {@link Complex} arctangent.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} arctangent.
     */
    public static Complex atan(Complex z) throws ArithmeticException {
        Complex frac = z.multiplyBy(i).plus(1.).divideBy(z.multiplyBy(i).multiplyBy(-1.).plus(1.));
        return new Complex(i.multiplyBy(-0.5).multiplyBy(Complex.ln(frac)));
    }

    /**
     * Computes {@link Complex} exponential.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} exponential.
     */
    public static Complex exp(Complex z) {
        double _re = Math.exp(z.re()) * Math.cos(z.im());
        double _im = Math.exp(z.re()) * Math.sin(z.im());
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes {@link Complex} natural logarithm.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} logarithm.
     */
    public static Complex ln(Complex z) {
        double _re = Math.log(z.mag());
        double _im = z.arg();
        return Complex.cartesian(_re, _im);
    }

    /**
     * Computes {@link Complex} square root.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} square root.
     */
    public static Complex sqrt(Complex z) {
        double r = Math.sqrt(z.mag());
        double phi = z.arg() / 2.;
        return Complex.cartesian(r * Math.cos(phi), r * Math.sin(phi));
    }

    /**
     * Computes {@link Complex} arcsine.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} arcsine.
     */
    public static Complex asin(Complex z) {
        Complex zz = ONE.minus(z.multiplyBy(z));
        zz = sqrt(zz);
        zz = zz.plus(i.multiplyBy(z));
        zz = i.multiplyBy(ln(zz)).multiplyBy(-1.);
        return zz;
    }

    /**
     * Computes {@link Complex} arctangent.
     *
     * @param z {@link Complex} argument.
     * @return {@link Complex} arctangent.
     */
    public static Complex atanh(Complex z) throws ArithmeticException {
        Complex zz = z;
        zz = zz.plus(1.).divideBy(zz.minus(1.)).multiplyBy(-1.);
        zz = ln(zz).multiplyBy(0.5);
        return zz;
    }

    /**
     * Computes hyperbolic arctangent of a double using complex arithmetic.
     *
     * @param x {@link Complex} argument.
     * @return {@link Complex} arctangent.
     */
    public static double atanh(double x) throws ArithmeticException {
        return atanh(Complex.cartesian(x, 0.0)).re();
    }

    /**
     * Returns string representation.
     * @return String representation.
     */
    @Override
    public String toString() {
        return "[" + re() + (im() < 0.0 ? " - " : " + ") + Math.abs(im()) + "i]";
    }
}
