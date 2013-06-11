/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
 * Michaud.
 * The new CTS has been funded  by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-08-VILL-0005-01 and the regional council 
 * "Région Pays de La Loire" under the projet SOGVILLE (Système d'Orbservation 
 * Géographique de la Ville).
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/irstv/cts/>
 */
package org.cts;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

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
 * @author Michaël Michaud
 */
public class Ellipsoid extends IdentifiableComponent {

    public static enum SecondParameter {

        InverseFlattening, SemiMinorAxis, Eccentricity
    };
    private static final double PI_2 = Math.PI / 2.;
    private static final double PI_4 = Math.PI / 4.;
    /**
     * Perfect SPHERE
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
            new Identifier("EPSG", "7022", "Intenational 1924", "Int_1924"), 6378388, 297);
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
    //public static final Ellipsoid CLARKE1880IGN = createEllipsoidFromInverseFlattening(
    //    new Identifier("EPSG", "7011", "Clarke 1880 (IGN)", "Clarke_1880_IGN"), 6378249.2, 293.466021);
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
            new Identifier("EPSG", "4016", "Everest 1830 (1967 Definition)", "evrstSS"), 6377298.556, 300.8017);
    /**
     * GRS 1967 ellipsoid, used in Australian Geodetic Datum and in South American Datum 1969.
     */
    public static final Ellipsoid GRS67 = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7036", "GRS 1967", "GRS67"), 6378160, 298.247167427);
    /**
     * GRS 1967 (SAD 1969) ellipsoid, used in Australian Geodetic Datum and in South American Datum 1969.
     */
    public static final Ellipsoid AustSA = createEllipsoidFromInverseFlattening(
            new Identifier("EPSG", "7050", "GRS 1967 (SAD 1969)", "aust_SA"), 6378160, 298.25);
    
    private double semiMajorAxis;
    transient SecondParameter secondParameter;
    // Following fields are initialized at construction time
    // They are transient because they don't need to be serialized
    // NOTE : if those fields were directly initialized in the constructor,
    // they could be declared final.
    // semi-major axis
    transient private double a;
    // semi-minor axis
    transient private double b;
    // flattening
    transient private double f;
    // inverse flattening
    transient private double invf;
    // eccentricity
    transient private double e;
    // square eccentricity
    transient private double e2;
    // second square eccentricity
    transient private double eprime2;
    // coefficients used to compute the meridian arc length
    transient private double[] arc_coeff;
    // coefficients used to compute the meridian arc length from the latitude
    transient private double[] dir_utm_coeff;
    // coefficients used to compute the latitude from the meridian arc length
    transient private double[] inv_utm_coeff;
    // coefficients used to compute meridian arc length from/to latitude
    // this second method is taken from http://www.ngs.noaa.gov/gps-toolbox/Hehl
    // It makes it possible to choose the precision of the result
    transient private double[] kk;
    //coefficients used by the inverse Mercator projection
    transient private double[] inv_merc_coeff;

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
        this.semiMajorAxis = semiMajorAxis;
        this.secondParameter = secondParameter;
        switch (secondParameter) {
            case InverseFlattening:
                this.invf = secondParameterValue;
                break;
            case SemiMinorAxis:
                this.b = secondParameterValue;
                break;
            case Eccentricity:
                this.e = secondParameterValue;
                break;
            default:
        }
        initDoubleParameters();
    }

    /**
     * Return the semi-major axis of this ellipsoid (fr : demi grand axe).
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
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
     * Return the square eccentricity of this ellipsoid (fr : carr� de
     * l'excentricit�).
     */
    public double getSquareEccentricity() {
        return e2;
    }

    /**
     * Return the second eccentricity ((a-b)/b) of this ellipsoid (fr : seconde
     * excentricit�).
     */
    public double getSecondEccentricitySquared() {
        return e2 / (1.0 - e2);
    }

    /**
     * Get coefficients for the meridian arc length
     */
    public double[] getArcCoeff() {
        return arc_coeff;
    }

    /**
     * Get coefficients for the direct UTM projection
     */
    public double[] getDirectUTMCoeff() {
        return dir_utm_coeff;
    }

    /**
     * Get coefficients for the inverse UTM projection
     */
    public double[] getInverseUTMCoeff() {
        return inv_utm_coeff;
    }

    /**
     * Get k coefficients computed with an iterative method
     */
    public double[] getKCoeff(int max) {
        initKCoeff(max);
        return kk;
    }
    
    /**
     * Get coefficients for the inverse Mercator projection
     */
    public double[] getInverseMercatorCoeff() {
        return inv_merc_coeff;
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
        Identifier id = new Identifier(Ellipsoid.class, Identifiable.UNKNOWN);
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
        Identifier id = new Identifier(Ellipsoid.class, Identifiable.UNKNOWN);
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
        Identifier id = new Identifier(Ellipsoid.class, Identifiable.UNKNOWN);
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
        } else {
            return this;
        }
     }

    /**
     * Since version 0&#046;3 : initialization of all the double parameters of
     * the ellipsoid. NOTE : It could be good to initialize directly these
     * parameters in the constructor so that they can be declared final.
     */
    private void initDoubleParameters() {
        a = semiMajorAxis;
        switch (secondParameter) {
            case InverseFlattening:
                f = 1.0 / invf;
                b = a - a / invf;
                e2 = (2.0 - 1.0 / invf) / invf;
                e = sqrt(e2);
                break;
            case SemiMinorAxis:
                f = 1.0 - b / a;
                invf = a / (a - b);
                e2 = 1.0 - ((b * b) / (a * a));
                e = sqrt((a * a - b * b) / (a * a));
                break;
            case Eccentricity:
                e2 = e * e;
                b = a * sqrt(1.0 - e2);
                f = 1.0 - sqrt(1.0 - e2);
                invf = 1.0 / (1.0 - sqrt(1.0 - e2));
                break;
            default:
                f = 0.0;
                invf = Double.POSITIVE_INFINITY;
                e = 0.0;
                e2 = 0.0;
        }
        eprime2 = e2 / (1.0 - e2);
        initMeridianArcCoefficients();
    }

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
        dir_utm_coeff = new double[5];
        dir_utm_coeff[0] = 1.0 - e2 * 1 / 4 - e4 * 3 / 64 - e6 * 5 / 256 - e8 * 175 / 16384;
        dir_utm_coeff[1] = e2 * 1 / 8 - e4 * 1 / 96 - e6 * 9 / 1024 - e8 * 901 / 184320;
        dir_utm_coeff[2] = e4 * 13 / 768 + e6 * 17 / 5120 - e8 * 311 / 737280;
        dir_utm_coeff[3] = e6 * 61 / 15360 + e8 * 899 / 430080;
        dir_utm_coeff[4] = e8 * 49561 / 41287680;
        inv_utm_coeff = new double[5];
        inv_utm_coeff[0] = 1.0 - e2 * 1 / 4 - e4 * 3 / 64 - e6 * 5 / 256 - e8 * 175 / 16384;
        inv_utm_coeff[1] = e2 * 1 / 8 + e4 * 1 / 48 + e6 * 7 / 2048 + e8 * 1 / 61440;
        inv_utm_coeff[2] = e4 * 1 / 768 + e6 * 3 / 1280 + e8 * 559 / 368640;
        inv_utm_coeff[3] = e6 * 17 / 30720 + e8 * 283 / 430080;
        inv_utm_coeff[4] = e8 * 4397 / 41287680;
        inv_merc_coeff = new double[5];
        inv_merc_coeff[0] = 1.0;
        inv_merc_coeff[1] = e2 * 1 / 2 + e4 * 5 / 24 + e6 * 1 / 12 + e8 * 13 / 360;
        inv_merc_coeff[2] = e4 * 7 / 48 + e6 * 29 / 240 + e8 * 811 / 11520;
        inv_merc_coeff[3] = e6 * 7 / 120 + e8 * 81 / 1120;
        inv_merc_coeff[4] = e8 * 4279 / 161280;
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
     * @return the first coefficient of series expansion
     */
    private double k1() {
        if (kk == null) {
            initKCoeff(5);
        }
        return 1.0 + kk[0];
    }

    /**
     * @return the second coefficient of series expansion
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
     * @return the complex second coefficient of series expansion
     */
    /*
     * public Complex koeff2(Complex beta) { if (kk==null) initKCoeff(5);
     * Complex cos2 = Complex.cos(beta).times(Complex.cos(beta)); Complex result
     * = new Complex(kk[0],0.0); Complex k = new Complex(1.0,0.0); for(int n = 1
     * ; n < kk.length ; n++) { k = k.times((2.*n)/(2.*n+1.0)).times(cos2);
     * result = result.plus(k.times(kk[n])); } return(result);
	}
     */
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
     * computes the ellipsoidal latitude from meridian arc length.
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
        return log(tan(PI_4 + latitude / 2) * pow((1 - esinlat) / (1 + esinlat), e / 2));
    }

    /**
     * Computes the geographic latitude from the isometric latitude (fr : calcul
     * de la latitude geographique � partir de la latitude isometrique).<p>
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
        double lat0 = 2 * atan(exp_isolatitude) - PI_2;
        double lati = lat0;
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
     * latitude g�ographique d'un point P � partir de sa latitude
     * isom�trique.<p> La latitude g�ographique est d�finie comme �tant la
     * limite d'une suite convergente. Le calcul de cette suite est interrompu
     * lorsque la diff�rence entre deux termes cons�cutifs est plus petit que
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
        return arc_coeff[0] * latitude
                + arc_coeff[1] * sin(2 * latitude)
                + arc_coeff[2] * sin(4 * latitude)
                + arc_coeff[3] * sin(6 * latitude)
                + arc_coeff[4] * sin(8 * latitude);
    }

    /**
     * @return a string representtaion of this ellipsoid.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getIdentifier().toString());
        sb.append(" (Semi-major axis = ").append(semiMajorAxis);
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
     * Returns true if this Ellipoid can be considered as equals to another one.
     * Ellipsoid equals method is based on a comparison of the object dimensions
     * with a sensibility of 0.1 mm.
     */
    @Override
    public boolean equals(Object other) {
        // short circuit to compare final static Ellipsoids
        if (this == other) {
            return true;
        }
        if (other instanceof Ellipsoid) {
            Ellipsoid ell = (Ellipsoid) other;
            // if ellipsoid codes are equals, ellipsoids are equals
            if (getCode().equals(ell.getCode())) {
                return true;
            }
            // else if ellipsoid semi-major axis and ellipsoid semi-minor axis
            // are equals (+/- 0.1 mm) ellipoids are also considered as equals
            double a2 = ell.getSemiMajorAxis();
            double b2 = ell.getSemiMinorAxis();
            return (Math.abs(a2 - a) < 1e-4 && Math.abs(b2 - b) < 1e-4);
        }
        return false;
    }
}
