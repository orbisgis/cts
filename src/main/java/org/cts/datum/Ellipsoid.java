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
package org.cts.datum;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.util.HashMap;
import java.util.Map;

import org.cts.Identifiable;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;

/**
 * An ellipsoid is a mathematical surface used to describe the Earth surface.<p>
 * It is only an approximation of the Earth surface, but it is used as a
 * reference surface for the expression of coordinates as a latitude and a
 * longitude.<p> The definition of an ellipsoid is based on two parameters :
 * <ul> <li>1st parameter = semi-major axis</li> <li>2nd parameter = inverse
 * flattening, semi-minor axis or eccentricity. The parameter type is choosen
 * among the values of SecondParameter enum.</li> </ul> <h3>Note on the
 * precision of some algorithms :</h3> <ul> <li>The calculation of semi-minor
 * axis, inverse flattening and eccentricity are precise. For example, the
 * construction of 10 successive ellipsoids using alternatively the 3 parameters
 * keeps a precision of less than 0.01 micron for the semi-minor axis</li>
 * <li>Default curvilinearAbscissa method or arcFromLat method using parameters
 * computed from 4, 5 or 6 iteration (see initKCoeff) give consistant results at
 * a precision of 1 micron (1E-6).</li> </ul>
 *
 * @author Michaël Michaud, Jules Party
 */
public class Ellipsoid extends IdentifiableComponent {

    /**
     * The second parameter use to create the ellipsoid, this parameter can be
     * the inverse flattening, the semi-minor axis or the eccentricity.
     */
    public enum SecondParameter {

        InverseFlattening, SemiMinorAxis, Eccentricity
    }

    /**
     * The double value of PI/2.
     */
    private static final double PI_2 = Math.PI / 2.;

    /**
     * Perfect SPHERE.
     */
    public static final Ellipsoid SPHERE = createEllipsoidFromSemiMinorAxis(
            new Identifier("EPSG", "7035", "SPHERE"), 6371000.0, 6371000.0);

    /**
     * GRS 1980 ellipsoid, used in most recent spatial geodetic system (1990 and
     * after).
     */
    public static final Ellipsoid GRS80 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7019", "GRS 1980", "GRS80"), 6378137.0, 298.257222101);

    /**
     * WGS84 ellipsoid, used with the WGS84 spatial geodetic datum. This
     * ellipsoid (and datum) is coherent with GRS80 and the ITRS.
     */
    public static final Ellipsoid WGS84 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7030", "WGS 84", "WGS84"), 6378137.0, 298.257223563);

    /**
     * International 1924.
     */
    public static final Ellipsoid INTERNATIONAL1924 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7022", "Intenational 1924", "Int_1924"), 6378388.0, 297.0);

    /**
     * Bessel 1841.
     */
    public static final Ellipsoid BESSEL1841 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7004", "Bessel 1841", "Bessel_1841"), 6377397.155, 299.1528128);

    /**
     * Clarke 1866.
     */
    public static final Ellipsoid CLARKE1866 = createEllipsoidFromSemiMinorAxis(
            new Identifier("EPSG", "7008", "Clarke 1866", "Clarke_1866"), 6378206.4, 6356583.8);

    /**
     * Clarke 1880 (IGN).
     */
    public static final Ellipsoid CLARKE1880IGN = createEllipsoidFromSemiMinorAxis(
            new Identifier("EPSG", "7011", "Clarke 1880 (IGN)", "Clarke_1880_IGN"), 6378249.2, 6356515.0);

    /**
     * Clarke 1880 (RGS) or Clarke 1880 modified.
     */
    public static final Ellipsoid CLARKE1880RGS = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7012", "Clarke 1880 (RGS)", "Clarke_1880_mod"), 6378249.2, 293.465);

    /**
     * Clarke 1880 (Arc). Note that the ellipsoid called clrk80 in the proj
     * library is defined as the Clarke 1880 mod. (modified) which is refered as
     * the Clarke 1880 (RGS) in the epsg database, with an inverse flattening
     * parameter of 293.465 instead of 293.466
     */
    public static final Ellipsoid CLARKE1880ARC = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7013", "Clarke 1880 (Arc)", "Clarke_1880_Arc"), 6378249.145, 293.4663077);

    /**
     * Krassowski 1940.
     */
    public static final Ellipsoid KRASSOWSKI = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7024", "Krassowski 1940", "Krassowski_1940"), 6378245.0, 298.3);

    /**
     * Everest 1830 (1967 definition).
     */
    public static final Ellipsoid EVERESTSS = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7016", "Everest 1830 (1967 Definition)", "evrstSS"), 6377298.556, 300.8017);

    /**
     * GRS 1967 ellipsoid, used in Australian Geodetic Datum and in South
     * American Datum 1969.
     */
    public static final Ellipsoid GRS67 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7036", "GRS 1967", "GRS67"), 6378160, 298.247167427);

    /**
     * GRS 1967 (SAD 1969) ellipsoid, used in Australian Geodetic Datum and in
     * South American Datum 1969.
     */
    public static final Ellipsoid AustSA = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7050", "GRS 1967 (SAD 1969)", "aust_SA"), 6378160, 298.25);

    /**
     * Airy 1830.
     */
    public static final Ellipsoid AIRY = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7001", "AIRY 1830", "airy"), 6377563.396, 299.3249646);

    /**
     * Bessel Namibia (GLM).
     */
    public static final Ellipsoid BESSNAM = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7046", "Bessel Namibia (GLM)", "bess_nam"), 6377483.865280419, 299.1528128);

    /**
     * Helmert 1906.
     */
    public static final Ellipsoid HELMERT = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7020", "Helmert 1906", "helmert"), 6378200, 298.3);

    /**
     * Airy Modified 1849.
     */
    public static final Ellipsoid AIRYMOD = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7002", "Airy Modified 1849", "mod_airy"), 6377340.189, 299.3249646);

    /**
     * WGS 66.
     */
    public static final Ellipsoid WGS66 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7025", "WGS 66", "WGS66"), 6378145, 298.25);

    /**
     * WGS 72.
     */
    public static final Ellipsoid WGS72 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7043", "WGS 72", "WGS72"), 6378135, 298.26);

    /**
     * The SecondParameters used to create this Ellipsoid.
     */
    final SecondParameter secondParameter;

    /**
     * The semi-major axis of this Ellipsoid.
     */
    final private double a;

    /**
     * The semi-minor axis of this Ellipsoid.
     */
    final private double b;

    /**
     * The flattening of this Ellipsoid.
     */
    final private double f;

    /**
     * The inverse flattening of this Ellipsoid.
     */
    final private double invf;

    /**
     * The eccentricity of this Ellipsoid.
     */
    final private double e;

    /**
     * The square eccentricity of this Ellipsoid.
     */
    final private double e2;

    /**
     * The coefficients used to compute the meridian arc length.
     */
    transient private double[] arc_coeff;

    /**
     * The coefficients for the direct UTM projection.
     */
    transient private double[] dir_utm_coeff;

    /**
     * The coefficients for the inverse UTM projection.
     */
    transient private double[] inv_utm_coeff;

    /**
     * The coefficients used to compute meridian arc length from/to latitude
     * this second method is taken from <a
     * href="http://www.ngs.noaa.gov/gps-toolbox/Hehl"> here </a>. It makes it
     * possible to choose the precision of the result.
     */
    transient private double[] kk;

    /**
     * The coefficients for the inverse Mercator projection.
     */
    transient private double[] inv_merc_coeff;

    /**
     * ellipsoidFromName associates each ellipsoid to a short string used to
     * recognize it in CTS.
     */
    public static final Map<String, Ellipsoid> ellipsoidFromName = new HashMap<String, Ellipsoid>();

    //@TODO see GeodeticDatum to homogeneize how to get objects from id or names
    static {
        ellipsoidFromName.put("airy", AIRY);
        ellipsoidFromName.put("airymod", AIRYMOD);
        ellipsoidFromName.put("austsa", AustSA);
        ellipsoidFromName.put("bessel", BESSEL1841);
        ellipsoidFromName.put("bessnam", BESSNAM);
        ellipsoidFromName.put("clrk66", CLARKE1866);
        ellipsoidFromName.put("clrk80", CLARKE1880RGS);
        ellipsoidFromName.put("clrk80ign", CLARKE1880IGN);
        ellipsoidFromName.put("clrk80arc", CLARKE1880ARC);
        ellipsoidFromName.put("evrstss", EVERESTSS);
        ellipsoidFromName.put("grs67", GRS67);
        ellipsoidFromName.put("grs80", GRS80);
        ellipsoidFromName.put("helmert", HELMERT);
        ellipsoidFromName.put("intl", INTERNATIONAL1924);
        ellipsoidFromName.put("krass", KRASSOWSKI);
        ellipsoidFromName.put("mod_airy", AIRYMOD);
        ellipsoidFromName.put("wgs66", WGS66);
        ellipsoidFromName.put("wgs72", WGS72);
        ellipsoidFromName.put("wgs84", WGS84);
        ellipsoidFromName.put("modairy", AIRYMOD);
    }

    /**
     * Create a new Ellipsoid and initialize common parameters : a, b, e, e2, f,
     * 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param identifier Ellipsoid identifier in the EPSG database
     * @param semiMajorAxis length of the semi major axis in meters
     * @param secondParameter second parameter type (inverse flattening,
     * semi-minor axis or eccentricity).
     * @param secondParameterValue value of the second parameter.
     */
    private Ellipsoid(Identifier identifier,
            double semiMajorAxis,
            SecondParameter secondParameter,
            double secondParameterValue) throws IllegalArgumentException {
        super(identifier);
        this.a = semiMajorAxis;
        this.secondParameter = secondParameter;
        switch (secondParameter) {
            case InverseFlattening:
                this.invf = secondParameterValue;
                this.f = 1.0 / this.invf;
                this.b = this.a - this.a / this.invf;
                this.e2 = (2.0 - 1.0 / this.invf) / this.invf;
                this.e = sqrt(this.e2);
                break;
            case SemiMinorAxis:
                this.b = secondParameterValue;
                this.f = 1.0 - this.b / this.a;
                invf = a / (a - b);
                this.e2 = 1.0 - ((this.b * this.b) / (this.a * this.a));
                this.e = sqrt((this.a * this.a - this.b * this.b) / (this.a * this.a));
                break;
            case Eccentricity:
                this.e = secondParameterValue;
                this.e2 = this.e * this.e;
                this.b = this.a * sqrt(1.0 - this.e2);
                this.f = 1.0 - sqrt(1.0 - this.e2);
                invf = 1.0 / (1.0 - sqrt(1.0 - e2));
                break;
            default:
                this.b = this.a;
                this.f = 0.0;
                this.invf = Double.POSITIVE_INFINITY;
                this.e = 0.0;
                this.e2 = 0.0;
        }
        /**
         * The second square eccentricity of this Ellipsoid.
         */
        double eprime2 = e2 / (1.0 - e2);
    }

    /**
     * Return the semi-major axis of this ellipsoid (fr : demi grand axe).
     */
    public double getSemiMajorAxis() {
        return a;
    }

    /**
     * Return the inverse flattening of this ellipsoid (fr : aplatissement
     * inverse).
     */
    public double getInverseFlattening() {
        return invf;
    }

    /**
     * Return the semi-minor axis of this ellipsoid (fr : demi petit axe).
     */
    public double getSemiMinorAxis() {
        return b;
    }

    /**
     * Return the flattening of this ellipsoid (fr : aplatissement).
     */
    public double getFlattening() {
        return f;
    }

    /**
     * Return the eccentricity of this ellipsoid (fr : excentricit�).
     */
    public double getEccentricity() {
        return e;
    }

    /**
     * Return the square eccentricity of this ellipsoid (fr : carré de
     * l'excentricité).
     */
    public double getSquareEccentricity() {
        return e2;
    }

    /**
     * Return the second eccentricity ((a-b)/b) of this ellipsoid (fr : seconde
     * excentricité).
     */
    public double getSecondEccentricitySquared() {
        return e2 / (1.0 - e2);
    }

    /**
     * Get coefficients for the meridian arc length.
     */
    public double[] getArcCoeff() {
        if (arc_coeff == null) {
            initMeridianArcCoefficients();
        }
        return arc_coeff;
    }

    /**
     * Get k coefficients computed with an iterative method.
     */
    public double[] getKCoeff(int max) {
        initKCoeff(max);
        return kk;
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * inverse flattening and initializes common parameters such as a, b, e, e2,
     * f, 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param invFlattening inverse flattening of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromInverseFlattening(
            double semiMajorAxis,
            double invFlattening)
            throws IllegalArgumentException {
        Identifier id = new Identifier(Ellipsoid.class);
        Ellipsoid ellps = new Ellipsoid(id, semiMajorAxis,
                SecondParameter.InverseFlattening, invFlattening);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * inverse flattening and initializes common parameters such as a, b, e, e2,
     * f, 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param identifier ellipsoid identifier
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param invFlattening inverse flattening of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromInverseFlattening(
            Identifier identifier,
            double semiMajorAxis,
            double invFlattening)
            throws IllegalArgumentException {
        Ellipsoid ellps = new Ellipsoid(identifier, semiMajorAxis,
                SecondParameter.InverseFlattening, invFlattening);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * semi-minor axis and initializes common parameters such as a, b, e, e2, f,
     * 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param semiMinorAxis semi-minor-axis of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromSemiMinorAxis(
            double semiMajorAxis,
            double semiMinorAxis)
            throws IllegalArgumentException {
        Identifier id = new Identifier(Ellipsoid.class);
        Ellipsoid ellps = new Ellipsoid(id, semiMajorAxis,
                SecondParameter.SemiMinorAxis, semiMinorAxis);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * semi-minor axis and initializes common parameters such as a, b, e, e2, f,
     * 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param identifier ellipsoid identifier
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param semiMinorAxis semi-minor-axis of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromSemiMinorAxis(
            Identifier identifier,
            double semiMajorAxis,
            double semiMinorAxis)
            throws IllegalArgumentException {
        Ellipsoid ellps = new Ellipsoid(identifier, semiMajorAxis,
                SecondParameter.SemiMinorAxis, semiMinorAxis);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * eccentricity and initializes common parameters such as a, b, e, e2, f,
     * 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param eccentricity semi-minor-axis of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromEccentricity(
            double semiMajorAxis,
            double eccentricity)
            throws IllegalArgumentException {
        Identifier id = new Identifier(Ellipsoid.class);
        Ellipsoid ellps = new Ellipsoid(id, semiMajorAxis,
                SecondParameter.Eccentricity, eccentricity);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Creates a new Ellipsoid whose definition is based on semi-major axis and
     * eccentricity and initializes common parameters such as a, b, e, e2, f,
     * 1/f, e'2 and coefficients for the meridian arc.
     *
     * @param identifier ellipsoid identifier
     * @param semiMajorAxis length of the semi-major axis in meters
     * @param eccentricity semi-minor-axis of the ellipsoid
     */
    public static Ellipsoid createEllipsoidFromEccentricity(
            Identifier identifier, double semiMajorAxis, double eccentricity)
            throws IllegalArgumentException {
        Ellipsoid ellps = new Ellipsoid(identifier, semiMajorAxis,
                SecondParameter.Eccentricity, eccentricity);
        return ellps.checkExistingEllipsoid();
    }

    /**
     * Check if
     * <code>this</code> is equals to one of the predefined Ellipsoid (GRS80,
     * WGS84,&hellip;). Return the predifined Ellipsoid that matches if exists,
     * otherwise return
     * <code>this</code>.
     */
    private Ellipsoid checkExistingEllipsoid() {
        if (this.equals(Ellipsoid.GRS80)) {
            return Ellipsoid.GRS80;
        } else if (this.equals(Ellipsoid.WGS84)) {
            return Ellipsoid.WGS84;
        } else if (this.equals(Ellipsoid.INTERNATIONAL1924)) {
            return Ellipsoid.INTERNATIONAL1924;
        } else if (this.equals(Ellipsoid.CLARKE1866)) {
            return Ellipsoid.CLARKE1866;
        } else if (this.equals(Ellipsoid.CLARKE1880ARC)) {
            return Ellipsoid.CLARKE1880ARC;
        } else if (this.equals(Ellipsoid.CLARKE1880IGN)) {
            return Ellipsoid.CLARKE1880IGN;
        } else if (this.equals(Ellipsoid.CLARKE1880RGS)) {
            return Ellipsoid.CLARKE1880RGS;
        } else if (this.equals(Ellipsoid.SPHERE)) {
            return Ellipsoid.SPHERE;
        } else if (this.equals(Ellipsoid.BESSEL1841)) {
            return Ellipsoid.BESSEL1841;
        } else if (this.equals(Ellipsoid.KRASSOWSKI)) {
            return Ellipsoid.KRASSOWSKI;
        } else if (this.equals(Ellipsoid.GRS67)) {
            return Ellipsoid.GRS67;
        } else if (this.equals(Ellipsoid.AustSA)) {
            return Ellipsoid.AustSA;
        } else if (this.equals(Ellipsoid.AIRY)) {
            return Ellipsoid.AIRY;
        } else if (this.equals(Ellipsoid.AIRYMOD)) {
            return Ellipsoid.AIRYMOD;
        } else if (this.equals(Ellipsoid.BESSNAM)) {
            return Ellipsoid.BESSNAM;
        } else if (this.equals(Ellipsoid.HELMERT)) {
            return Ellipsoid.HELMERT;
        } else if (this.equals(Ellipsoid.WGS66)) {
            return Ellipsoid.WGS66;
        } else if (this.equals(Ellipsoid.WGS72)) {
            return Ellipsoid.WGS72;
        } else {
            return this;
        }
    }

    /**
     * Initialize the coefficients for the meridian arc length.
     */
    private void initMeridianArcCoefficients() {
        double e4 = e2 * e2;
        double e6 = e4 * e2;
        double e8 = e4 * e4;
        arc_coeff = new double[5];
        arc_coeff[0] = 1.0 - e2 * 1 / 4 - e4 * 3 / 64 - e6 * 5 / 256 - e8 * 175 / 16384;
        arc_coeff[1] = -e2 * 3 / 8 - e4 * 3 / 32 - e6 * 45 / 1024 - e8 * 105 / 4096;
        arc_coeff[2] = e4 * 15 / 256 + e6 * 45 / 1024 + e8 * 525 / 16384;
        arc_coeff[3] = -e6 * 35 / 3072 - e8 * 175 / 12288;
        arc_coeff[4] = e8 * 315 / 131072;
    }

    /**
     * This second method to compute the meridian arc length is taken from <a
     * href="http://www.ngs.noaa.gov/gps-toolbox/Hehl">. It is based upon an
     * iterative method and the precision of the result will depend on the
     * number of iterations. It is to be used with arcFromLat or from
     * latFromArc.
     */
    public void initKCoeff(int max) {
        if (max < 1) {
            max = 1;
        }
        if (max > 8) {
            max = 8;
        }
        kk = new double[max];
        //for(int n = 0; n < max; n++) k[n] = 0.0;
        double c = 1.0;
        for (int n = 1; n <= max; n++) {
            double n2 = 2.0 * n;
            c *= (n2 - 1.0) * (n2 - 3.0) / n2 / n2 * e2;
            for (int m = 0; m < n; m++) {
                kk[m] += c;
            }
        }
    }

    /**
     * Return the first coefficient of series expansion.
     */
    private double k1() {
        if (kk == null) {
            initKCoeff(5);
        }
        return 1.0 + kk[0];
    }

    /**
     * Return the second coefficient of series expansion
     */
    private double k2(double beta_rad) {
        if (kk == null) {
            initKCoeff(5);
        }
        double cos2 = Math.cos(beta_rad) * Math.cos(beta_rad);
        double result = kk[0];
        double k = 1.0;
        for (int n = 1; n < kk.length; n++) {
            k *= (2.0 * n) / (2.0 * n + 1.0) * cos2;
            result += kk[n] * k;
        }
        return (result);
    }

    /**
     * Computes the meridian arc from equator to point with ellipsoidal latitude
     * phi.
     *
     * @param phi the ellipsoidal latitude
     * @return the meridian arc in meters
     */
    public double arcFromLat(double phi) {
        double beta = Math.atan((1.0 - f) * Math.tan(phi));
        return a * beta * k1() + a / 2.0 * Math.sin(2.0 * beta) * k2(beta);
    }

    /**
     * Computes the ellipsoidal latitude from meridian arc length.
     *
     * @param s the meridian arc length in meters
     * @return the ellipsoidal latitude
     */
    public double latFromArc(double s) throws ArithmeticException {
        final int MAXITER = 10;
        double beta0 = s / a / k1();
        double beta = beta0, betaold = 1.E30;
        int iter = 0;
        while (++iter < MAXITER && Math.abs(beta - betaold) > 1.E-15) {
            betaold = beta;
            beta = beta0 - k2(beta) / 2. / k1() * Math.sin(2. * beta);
        }
        if (iter == MAXITER) {
            throw new ArithmeticException("The latitudeFromArc method diverges");
        }
        return (Math.atan(Math.tan(beta) / (1. - f)));
    }

    /**
     * The Meridional Radius of Curvature is the radius of curvature, at a
     * specific latitude, of the ellipse used to generate the ellipsoid.
     *
     * @param latitude the geographic latitude
     * @return the radius of curvature in meters
     */
    public final double meridionalRadiusOfCurvature(double latitude) {
        double sinlat = sin(latitude);
        return a * (1 - e2) / pow((1 - e2 * sinlat * sinlat), 1.5);
    }

    /**
     * The Transverse Radius of Curvature or radius of the first vertical
     * section or prime vertical radius of curvature (fr : Grande Normale).<p>
     * This is the radius of curvature in the plane which is normal to (i.e.
     * perpendicular to) both the surface of the ellipsoid at, and the meridian
     * passing through, the specific point of interest.
     *
     * @param latitude geographic latitude in radians
     * @return a double value representing a length in meters
     */
    public final double transverseRadiusOfCurvature(double latitude) {
        // trigonometric function are slow, use it one time instead of two
        double sinlat = sin(latitude);
        return a / sqrt(1 - (e2 * sinlat * sinlat));
    }
    // The same method with a french name : DEPRECATED

    public final double grandeNormale(double latitude) {
        // trigonometric function are slow, use it one time instead of two
        double sinlat = sin(latitude);
        return a / sqrt(1 - (e2 * sinlat * sinlat));
    }

    /**
     * Computes isometric latitude from geographic (or geodetic) latitude.<p>
     * Geographic latitude of a point P located on the surface of the ellipsoid
     * is the angle between the perpendicular to the ellipsoid surface at P and
     * the equatorial plan.<p> Isometric latitude is a function of geographic
     * latitude L(lat) such as (lambda, L) is a symmetric parametric form of the
     * ellipsoid surface.<p> Ref. <a
     * href="http://www.ign.fr/rubrique.asp?rbr_id=1700&lng_id=FR#68096">
     * IGN</a> ALG0001
     *
     * @param latitude geographic latitude
     * @return isometric latitude in radians
     */
    public final double isometricLatitude(double latitude) {
        double esinlat = e * sin(latitude);
        return log(tan((PI_2 + latitude) / 2) * pow((1 - esinlat) / (1 + esinlat), e / 2));
    }

    /**
     * Computes the geographic latitude from the isometric latitude (fr : calcul
     * de la latitude géographique à partir de la latitude isometrique).<p>
     * Geographic latitude of a point P located on the surface of the ellipsoid
     * is the angle between the perpendicular to the ellipsoid surface at P and
     * the equatorial plan.<p> Isometric latitude is a function of geographic
     * latitude L(lat) such as (lambda, L) is a symmetric parametric form of the
     * ellipsoid surface.<p> Geographic latitude is computed as the limit of a
     * convergent suite. The loop is stopped when two consecutive terms of the
     * suite is less than epsilon.<p> Ref. <a
     * href="http://www.ign.fr/rubrique.asp?rbr_id=1700&lng_id=FR#68096">
     * IGN</a> ALG0002
     *
     * @param isoLatitude latitude isometrique
     * @param epsilon value controlling the stop condition of this convergent
     * sequence. Use 1E-10 for a precision of about 0.6 mm, 1E-11 for a
     * precision of about 0.06 mm and 1E-12 for a preciison of about 0.006 mm
     * @return the geographic latitude as a double
     */
    public final double latitude(double isoLatitude, double epsilon) {
        double exp_isolatitude = exp(isoLatitude);
        double lati = 2 * atan(exp_isolatitude) - PI_2;
        double latj = 1000;
        while (abs(latj - lati) >= epsilon) {
            lati = latj;
            double esinlat = e * sin(lati);
            latj = 2 * atan(pow((1 + esinlat) / (1 - esinlat), e / 2) * exp_isolatitude)
                    - PI_2;
        }
        return latj;
    }

    /**
     * Computes geographic latitude from isometric latitude. The process stops
     * as soon as the result reaches a pecision of 1.0E-11.<p> fr : Calcul la
     * latitude géographique d'un point P à partir de sa latitude
     * isométrique.<p> La latitude géographique est définie comme étant la
     * limite d'une suite convergente. Le calcul de cette suite est interrompu
     * lorsque la différence entre deux termes consécutifs est plus petit que
     * 1E-11 (soit environ 0.006 mm).
     *
     * @param isoLatitude isometric latitude
     * @return the geographic latitude in radians
     */
    public final double latitude(double isoLatitude) {
        return latitude(isoLatitude, 1E-11);
    }

    /**
     * Returns the curvilinear abscissa of the meridian arc for this latitude on
     * an ellipsoid with eccentricity = this.e and semi-major axis = 1.0.<p>
     * Usually noted beta, the meridian arc length is obtained by multiplying
     * this result by the semi-major axis.
     *
     * @param latitude latitude.
     * @return the curvilinear abscissa of this latitude on the meridian arc
     */
    public double curvilinearAbscissa(double latitude) {
        if (arc_coeff == null) {
            initMeridianArcCoefficients();
        }
        return arc_coeff[0] * latitude
                + arc_coeff[1] * sin(2 * latitude)
                + arc_coeff[2] * sin(4 * latitude)
                + arc_coeff[3] * sin(6 * latitude)
                + arc_coeff[4] * sin(8 * latitude);
    }

    /**
     * Returns a WKT representation of the ellipsoid.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("SPHEROID[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getSemiMajorAxis());
        w.append(',');
        if (this.getInverseFlattening() != Double.POSITIVE_INFINITY) {
            w.append(this.getInverseFlattening());
        } else {
            w.append(0);
        }
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Return a string representtaion of this ellipsoid.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getIdentifier().toString());
        sb.append(" (Semi-major axis = ").append(a);
        switch (secondParameter) {
            case SemiMinorAxis:
                sb.append(" | ").append("Semi-minor axis = ").append(b).append(")");
                break;
            case InverseFlattening:
                sb.append(" | ").append("Flattening = 1/").append(invf).append(")");
                break;
            case Eccentricity:
                sb.append(" | ").append("Eccentricity = ").append(e).append(")");
                break;
            default:
                sb.append(")");
        }
        return sb.toString();
    }

    /**
     * Returns true if this Ellipsoid can be considered as equals to another
     * one. Ellipsoid equals method is based on a comparison of the object
     * dimensions with a sensibility of 0.1 mm.
     *
     * @param o the object to compare this Ellipsoid against
     */
    @Override
    public boolean equals(Object o) {
        // short circuit to compare final static Ellipsoids
        if (this == o) {
            return true;
        }
        if (o instanceof Ellipsoid) {
            Ellipsoid ell = (Ellipsoid)o;
            // if ellipsoid codes are equals, ellipsoids are equals
            if (getCode().equals(ell.getCode())) {
                return true;
            }
            // if ellipsoid semi-major axis and ellipsoid semi-minor axis
            // are equals (+/- 0.1 mm) ellipsoids are considered as equals
            // Rational : there maybe small differences between an
            // ellipsoid defined by its b value, its e2 or its 1/f.
            // Note : 0.1 mm is less than the difference between WGS84 and
            // GRS80
            double a2 = Math.rint(ell.getSemiMajorAxis() * 10000);
            double b2 = Math.rint(ell.getSemiMinorAxis() * 10000);
            return (Math.rint(a*10000) == a2 && Math.rint(b*10000) == b2);
        }
        return false;
    }

    /**
     * Returns the hash code for this Ellipsoid.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        // Round to 0.0001 to be consistent with equals definition
        long la = Double.doubleToLongBits((long)Math.rint(this.a*10000));
        long lb = Double.doubleToLongBits((long)Math.rint(this.b*10000));
        hash = 97 * hash + (int) (la ^ (la >>> 32));
        hash = 97 * hash + (int) (lb ^ (lb >>> 32));
        return hash;
    }
}
