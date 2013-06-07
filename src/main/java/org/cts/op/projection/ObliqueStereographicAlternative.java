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
 * The Oblique Stereographic Alternative Projection (STEREA). <p>
 *
 * @author Jules Party
 */
public class ObliqueStereographicAlternative extends Projection {

    public static final Identifier STERE =
            new Identifier("EPSG", "9809", "Oblique Stereographic Alternative", "STEREA");
    protected final double lat0, // the reference latitude
            lon0, // the reference longitude (from the datum prime meridian)
            conLat0, // the conformal latitude of the origin
            xs, // x coordinate of the pole
            ys,   // y coordinate of the pole
            k0, // scale coefficent for easting
            R, // geometrical mean of the radius of curvature at origin
            e, // eccentricity of the ellipsoid
            e2, // square eccentricity of the ellipsoid
            c, // constant of the projection
            n; // exponent of the projection
    private double PI_2 = PI/2;

    public ObliqueStereographicAlternative(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(STERE, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat0 = getLatitudeOfOrigin();
        xs = getFalseEasting();
        ys = getFalseNorthing();
        e = ellipsoid.getEccentricity();
        e2 = ellipsoid.getSquareEccentricity();
        k0 = getScaleFactor();
        R = pow(ellipsoid.meridionalRadiusOfCurvature(lat0)*ellipsoid.transverseRadiusOfCurvature(lat0), 0.5);
        n = pow(1+e2*pow(cos(lat0), 4)/(1-e2), 0.5);
        double w1 = w(lat0);
        double sinki0 = (w1-1)/(w1+1);
        c = (n + sin(lat0))*(1-sinki0)/(n-sin(lat0))/(1+sinki0);
        conLat0 = asin((c*w1-1)/(c*w1+1));
    }
    
    private double w(double lat) {
        return pow((1+sin(lat))/(1-sin(lat))*pow((1-e*sin(lat))/(1+e*sin(lat)), e), n);
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    @Override
    public Surface getSurface() {
        return Projection.Surface.AZIMUTHAL;
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
     * Transform coord using the Oblique Stereographic Alternative Projection.
     * Input coord is supposed to be a geographic latitude / longitude coordinate
     * in radians. Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double lon = coord[1];
        double lat = coord[0];
        double conLon = n*(lon-lon0)+lon0;
        double conLat = asin((c*w(lat)-1)/(c*w(lat)+1));
        double B = 1 + sin(conLat) * sin(conLat0) + cos(conLat) * cos(conLat0) * cos(conLon - lon0);
        double dE = 2 * R * k0 * cos(conLat) * sin(conLon - lon0) / B;
        double dN = 2 * R * k0 * (sin(conLat) * cos(conLat0) - cos(conLat) * sin(conLat0) * cos(conLon - lon0)) / B;
        coord[0] = xs + dE;
        coord[1] = ys + dN;
        return coord;
    }
    
    /**
     * Creates the inverse operation for Oblique Stereographic Alternative Projection.
     * Input coord is supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the OGP's Guidance Note Number 7 Part 2 :
     * <http://www.epsg.org/guides/G7-2.html>
     * 
     * @param coord coordinate to transform
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return new ObliqueStereographicAlternative(ellipsoid, parameters) {

            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double dE = coord[0]-xs;
                double dN = coord[1]-ys;
                double g = 2*R*k0*tan((PI_2-conLat0)/2);
                double h = 4*R*k0*tan(conLat0) + g;
                double i = atan(dE/(h+dN));
                double j = atan(dE/(g-dN)) - i;
                double conLat = conLat0 + 2*atan((dN-dE*tan(j/2))/2/R/k0);
                double conLon = j + 2*i + lon0;
                coord[1] = (conLon-lon0)/n + lon0;
                double isoLat = log((1+sin(conLat))/(1-sin(conLat))/c)/2/n;
                coord[0] = ellipsoid.latitude(isoLat);
                return coord;
            }
        };
    }
}