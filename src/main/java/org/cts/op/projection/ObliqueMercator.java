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
package org.cts.op.projection;

import java.util.Map;
import org.cts.CoordinateDimensionException;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.units.Measure;
import static java.lang.Math.*;
import org.cts.CoordinateOperation;
import org.cts.NonInvertibleOperationException;

/**
 * The Oblique Mercator Projection (OMERC). <p>
 *
 * @author Jules Party
 */
public class ObliqueMercator extends Projection {

    public static final Identifier OMERC =
            new Identifier("EPSG", "9815", "Oblique Mercator", "OMERC");
    protected final double latc, // latitude of the projection center
            lonc, // longitude of the projection center
            alphac, // azimuth of the initial line
            gammac, // angle from the rectified grid to the skew (oblique) grid
            kc, // scale factor on the initial line
            FE, // false easting
            FN,   // false northing
            B, //constant of the projection
            A, //constant of the projection
            H, //constant of the projection
            gamma0, //constant of the projection
            lambda0, //constant of the projection
            uc; // center of the projection

    public ObliqueMercator(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(OMERC, ellipsoid, parameters);
        lonc = getCentralMeridian();
        latc = getLatitudeOfOrigin();
        alphac = getAzimuthOfInitialLine();
        gammac = getAngleRectifiedToOblique();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        kc = getScaleFactor();
        double e = ellipsoid.getEccentricity();
        double e2 = ellipsoid.getSquareEccentricity();
        double esin = e*sin(latc);
        B = pow(1+(e2*pow(cos(latc), 4)/(1-e2)), 0.5);
        A = ellipsoid.getSemiMajorAxis()*B*kc*pow(1-e2, 0.5)/(1-esin*esin);
        double t0 = tan((PI/2-latc)/2)/pow((1-esin)/(1+esin), e/2);
        double D = B*pow((1-e2)/(1-esin*esin), 0.5)/cos(latc);
        double F = (D<1) ? D : D + pow(D*D-1, 0.5)*signum(latc);
        H = F * pow(t0, B);
        double G = (F - 1/F)/2;
        gamma0 = asin(sin(alphac)/D);
        lambda0 = lonc - asin(G*tan(gamma0))/B;
        uc = (D>1) ? A/B * atan(pow(D*D-1, 0.5)/cos(alphac))*signum(latc) : 0;
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.CYLINDRICAL;
    }

    /**
     * Return the
     * <code>Property</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Property getProperty() {
        return Projection.Property.CONFORMAL;
    }

    /**
     * Return the
     * <code>Orientation</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Orientation getOrientation() {
        return Projection.Orientation.TANGENT;
    }

    /**
     * Transform coord using the Oblique Mercator Projection. Input coord is supposed to
     * be a geographic latitude / longitude coordinate in radians.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double e = ellipsoid.getEccentricity();
        double esin = e*sin(coord[0]);
        double t = tan((PI/2-coord[0])/2)/pow((1-esin)/(1+esin), e/2);
        double Q = H / pow(t, B);
        double S = (Q - 1/Q)/2;
        double T = (Q + 1/Q)/2;
        double V = sin(B*(coord[1]-lambda0));
        double U = (S*sin(gamma0) - V*cos(gamma0))/T;
        double v = A*log((1-U)/(1+U))/2/B;
        double u = A*atan((S*cos(gamma0)+V*sin(gamma0))/cos(B*(coord[1]-lambda0)))/B - abs(uc)*signum(latc);
        coord[0] = FE + v*cos(gammac) + u*sin(gammac);
        coord[1] = FN + u*cos(gammac) - v*sin(gammac);
        return coord;
    }
    
    /**
     * Creates the inverse operation for Oblique Mercator Projection.
     * Input coord is supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     * 
     * @param coord coordinate to transform
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return new Mercator1SP(ellipsoid, parameters) {

            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double v = (coord[0]-FE)*cos(gammac) - (coord[1]-FN)*sin(gammac);
                double u = (coord[1]-FN)*cos(gammac) + (coord[0]-FE)*sin(gammac)+ abs(uc)*signum(latc);
                double Q = exp(-B*v/A);
                double S = (Q - 1/Q)/2;
                double T = (Q + 1/Q)/2;
                double V = sin(B*u/A);
                double U = (V*cos(gamma0)+S*sin(gamma0))/T;
                double t = pow(H/pow((1+U)/(1-U), 0.5), 1/B);
                double ki = 2*(PI/4 - atan(t));
                double lat = ki;
                double[] coeff = ellipsoid.getInverseMercatorCoeff();
                for (int i =1;i<5;i++) {
                    lat+= coeff[i]*sin(2*i*ki);
                }
                coord[0] = lat;
                coord[1] = lambda0 - atan((S*cos(gamma0)-V*sin(gamma0))/cos(B*u/A))/B;
                return coord;
            }
        };
    }
}