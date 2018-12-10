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

/**
 * Complex number, arithmetics, and complex functions.
 *
 * @see <a href="http://www.ngs.noaa.gov/gps-toolbox/Hehl/Complex.java"> taken
 * from Hehl</a> author hehl@tfh-berlin.de version 17-May-2005
 */
public final class Complex extends Number {

    /**
     * Complex unit i. It holds i*i = -1.
     */
    public static final Complex i = new Complex(0.0, 1.0);
    public static final Complex ONE = new Complex(1.0, 0.0); // added by mm on
    // 2009-01-18
    /**
     * Real part of this complex.
     */
    private double re;
    /**
     * Imaginery part of this complex.
     */
    private double im;

    /**
     * Constructs a complex number from real part and imaginary part.
     *
     * @param re real part
     * @param im imaginary part
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Constructs complex number zero.
     */
    public Complex() {
        this(0.0, 0.0);
    }

    /**
     * Constructs a complex number with imaginary part zero.
     *
     * @param re real part
     */
    public Complex(double re) {
        this(re, 0.0);
    }

    /**
     * Copy the complex number in parameter.
     *
     * @param z complex number
     */
    public Complex(Complex z) {
        this(z.re(), z.im());
    }

    /**
     * Returns a complex number with magnitude one and arbitrary argument.
     *
     * @param phi argument
     */
    public static Complex createComplexFromA(double phi) {
        return new Complex(Math.cos(phi), Math.sin(phi));
    }

    /**
     * Returns a complex number from radius/magnitude and argument.
     *
     * @param radius radius
     * @param phi argument
     */
    public static Complex createComplexFromRA(double radius, double phi) {
        return new Complex(radius * Math.cos(phi), radius * Math.sin(phi));
    }

    // added by mmichaud on 2009-01-12
    /**
     * Returns true if the object in parameter is equals to
     * <code>this</code>.
     *
     * @param obj
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex) {
            return re == ((Complex) obj).re() && im == ((Complex) obj).im();
        }
        return false;
    }

    /**
     * Returns the hash code for this Complex.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.re) ^ (Double.doubleToLongBits(this.re) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.im) ^ (Double.doubleToLongBits(this.im) >>> 32));
        return hash;
    }

    // added by mmichaud on 2009-01-12
    public int hashcode() {
        long lre = Double.doubleToLongBits(re);
        if (im() == 0) {
            return (int) (lre ^ (lre >>> 32));
        }
        long lim = Double.doubleToLongBits(im);
        return (int) (lre & lim);
    }

    /**
     * Returns the real part of the complex number.
     */
    public double re() {
        return re;
    }

    /**
     * Returns the imaginary part of the complex number.
     */
    public double im() {
        return im;
    }

    /**
     * Returns the conjugate complex number.
     */
    public Complex conj() {
        return new Complex(re(), -im());
    }

    /**
     * Return true, if imaginary part of complex number is (numerically) zero.
     * [MM] : for geodetic calculations, a value strictly lesser than 1.E-12 can
     * be considered as null
     */
    public boolean isReal() {
        return 1.E-12 > Math.abs(im);
    }

    /**
     * Return magnitude of complex number.
     */
    public double mag() {
        // return Math.sqrt(re*re + im*im);
        // Change by mmichaud on 2009-01-12 (java 5 hypot function is more
        // robust than sqrt(re*re+im*im), see java documentation
        return Math.hypot(re, im);
    }

    // following methods assure implements the Number interface by returning
    // the real part of this Number
    @Override
    public double doubleValue() {
        if (!isReal()) {
            return 0.0;
        }
        // better: throw new Msg("Complex.doubleValue","must be  pure real");
        return re;
    }

    @Override
    public float floatValue() {
        if (!isReal()) {
            return 0.0f;
        }
        // throw new Msg("Complex.doubleValue","must be pure real");
        return (float) re;
    }

    @Override
    public long longValue() {
        if (!isReal()) {
            return 0;
        }
        // throw new Msg("Complex.doubleValue","must be pure real");
        return (long) re;
    }

    @Override
    public int intValue() {
        if (!isReal()) {
            return 0;
        }
        // throw new Msg("Complex.doubleValue","must be pure real");
        return (int) re;
    }

    /**
     * Returns the argument of this complex number.
     */
    public double arg() {
        return Math.atan2(im, re);
    }

    /**
     * Provides sum of this and right hand side.
     *
     * @param z right hand side
     * @return sum of this and z
     */
    public Complex plus(Complex z) {
        return new Complex(re + z.re(), im + z.im());
    }

    /**
     * Provides sum of this and a real number.
     *
     * @param x the real number to add
     * @return sum of this and x
     */
    public Complex plus(double x) {
        return new Complex(re() + x, im());
    }

    /**
     * Provides difference of this and right hand side.
     *
     * @param z right hand side
     * @return difference of this and z
     */
    public Complex minus(Complex z) {
        return new Complex(re - z.re(), im - z.im());
    }

    /**
     * Provides difference of this and a real number.
     *
     * @param x the real number
     * @return difference of this and x
     */
    public Complex minus(double x) {
        return new Complex(re() - x, im());
    }

    /**
     * Provides product of this and complex right hand side.
     *
     * @param z right hand side
     * @return product of this and z
     */
    public Complex times(Complex z) {
        return new Complex(re * z.re() - im * z.im(), re * z.im() + im * z.re());
    }

    /**
     * Provides product of this and float right hand side.
     *
     * @param x right hand side
     * @return product of this and z
     */
    public Complex times(double x) {
        return new Complex(x * re, x * im);
    }

    /**
     * Computes fraction between this and the complex number z.
     *
     * @param z complex right hand side
     * @return fraction of this and z
     * @throws ArithmeticException if mag(z) == 0
     */
    public Complex divideBy(Complex z) throws ArithmeticException {
        double rz = z.mag();
        if (Math.abs(rz) < 1.E-12) {
            throw new ArithmeticException("Complex.divideBy cannot divide by a Complex with magnitude zero");
        }
        return new Complex((re * z.re() + im * z.im()) / (rz * rz), (im
                * z.re() - re * z.im())
                / (rz * rz));
    }

    /**
     * Computes linear combination 'a times x plus b'.
     *
     * @param a real factor
     * @param b real number
     * @return a times this plus b
     */
    public Complex axpb(double a, double b) {
        return new Complex(times(a).plus(b));
    }

    /**
     * Computes linear combination 'a times x plus b'.
     *
     * @param a complex factor
     * @param b complex number
     * @return a times this plus b
     */
    public Complex axpb(Complex a, Complex b) {
        return new Complex(times(a).plus(b));
    }

    /**
     * Computes complex sine.
     *
     * @param z complex argument
     * @return complexer sine
     */
    public static Complex sin(Complex z) {
        double _re = Math.sin(z.re()) * Math.cosh(z.im());
        double _im = Math.cos(z.re()) * Math.sinh(z.im());
        return new Complex(_re, _im);
    }

    /**
     * Computes complex cosine.
     *
     * @param z complex argument
     * @return complexer cosine
     */
    public static Complex cos(Complex z) {
        double _re = Math.cos(z.re()) * Math.cosh(z.im());
        double _im = -Math.sin(z.re()) * Math.sinh(z.im());
        return new Complex(_re, _im);
    }

    /**
     * Computes complex tangent.
     *
     * @param z complex argument
     * @return complex cosine
     */
    public static Complex tan(Complex z) {
        double nenner = Math.cos(2. * z.re()) + Math.cosh(2 * z.im());
        double _re = Math.sin(2. * z.re()) / nenner;
        double _im = Math.sinh(2. * z.im()) / nenner;
        return new Complex(_re, _im);
    }

    /**
     * Computes complex power.
     *
     * @param z complex argument
     * @return complex power
     */
    public static Complex pow(Complex z) {
        double ex = Math.pow(Math.E, z.re());
        return new Complex(ex * Math.cos(z.im()), ex * Math.sin(z.im()));
    }

    /**
     * Computes hyperbolic sine.
     *
     * @param z complex argument
     * @return hyperbolic sine
     */
    public static Complex sinh(Complex z) {
        return new Complex((pow(z).minus(pow(z.times(-1.)))).times(0.5));
    }

    /**
     * Computes hyperbolic cosine.
     *
     * @param z complex argument
     * @return hyperbolic cosine
     */
    public static Complex cosh(Complex z) {
        double _re = Math.cosh(z.re()) * Math.cos(z.im());
        double _im = Math.sinh(z.re()) * Math.sin(z.im());
        return new Complex(_re, _im);
    }

    /**
     * Computes hyperbolic tangent.
     *
     * @param z complex argument
     * @return hyperbolic tangent
     */
    public static Complex tanh(Complex z) throws ArithmeticException {
        return new Complex(sinh(z).divideBy(cosh(z)));
    }

    /**
     * Computes hyperbolic tangent of a double using complex arithmetic.
     *
     * @param x double argument
     * @return hyperbolic tangent
     */
    public static double tanh(double x) throws ArithmeticException {
        // throw new FatalMsg("Complex.tanh"," tanh() einbauen");
        return new Complex(x, 0.0).re();
    }

    /**
     * Computes complex arctangent.
     *
     * @param z complex argument
     * @return complex arctangent
     */
    public static Complex atan(Complex z) throws ArithmeticException {
        Complex frac = z.times(i).plus(1.).divideBy(
                z.times(i).times(-1.).plus(1.));
        return new Complex(i.times(-0.5).times(Complex.ln(frac)));
    }

    /**
     * Computes complex exponential.
     *
     * @param z complex argument
     * @return complex exponential
     */
    public static Complex exp(Complex z) {
        double _re = Math.exp(z.re()) * Math.cos(z.im());
        double _im = Math.exp(z.re()) * Math.sin(z.im());
        return new Complex(_re, _im);
    }

    /**
     * Computes complex natural logarithm.
     *
     * @param z complex argument
     * @return complex logarithm
     */
    public static Complex ln(Complex z) {
        double _re = Math.log(z.mag());
        double _im = z.arg();
        return new Complex(_re, _im);
    }

    /**
     * Computes complex square root.
     *
     * @param z complex argument
     * @return complex square root
     */
    public static Complex sqrt(Complex z) {
        double r = Math.sqrt(z.mag());
        double phi = z.arg() / 2.;
        return new Complex(r * Math.cos(phi), r * Math.sin(phi));
    }

    /**
     * Computes complex arcsine.
     *
     * @param z complex argument
     * @return complex arcsine
     */
    public static Complex asin(Complex z) {
        Complex zz = ONE.minus(z.times(z));
        zz = sqrt(zz);
        zz = zz.plus(i.times(z));
        zz = i.times(ln(zz)).times(-1.);
        return zz;
    }

    /**
     * Computes complex arctangent.
     *
     * @param z complex argument
     * @return complex arctangent
     */
    public static Complex atanh(Complex z) throws ArithmeticException {
        Complex zz = z;
        zz = zz.plus(1.).divideBy(zz.minus(1.)).times(-1.);
        zz = ln(zz).times(0.5);
        return zz;
    }

    /**
     * Computes hyperbolic arctangent of a double using complex arithmetic.
     *
     * @param x complex argument
     * @return complex arctangent
     */
    public static double atanh(double x) throws ArithmeticException {
        return atanh(new Complex(x, 0.0)).re();
    }

    /**
     * Returns string representation of this.
     */
    @Override
    public String toString() {
        return "[" + re() + (im() < 0.0 ? " - " : " + ") + Math.abs(im())
                + "i]";
    }
}
