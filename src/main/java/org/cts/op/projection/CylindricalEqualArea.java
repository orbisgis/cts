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
 * The Cylindrical Equal Area (normal case) Projection (OMERC). <p>
 *
 * @author Jules Party
 */
public class CylindricalEqualArea extends Projection {

    public static final Identifier CEA =
            new Identifier("EPSG", "9835", "Cylindrical Equal Area (normal case)", "CEA");
    protected final double lat_ts, // the latitude of true scale
            lon0, // the reference longitude (from the datum prime meridian)
            FE, // false easting
            FN,   // false northing
            k0; // scale factor of the projection
            

    public CylindricalEqualArea(final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(CEA, ellipsoid, parameters);
        lon0 = getCentralMeridian();
        lat_ts = getLatitudeOfTrueScale();
        FE = getFalseEasting();
        FN = getFalseNorthing();
        double e2 = ellipsoid.getSquareEccentricity();
        k0 = cos(lat_ts)/sqrt(1-e2*sin(lat_ts)*sin(lat_ts));
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
        return Projection.Property.EQUAL_AREA;
    }

    /**
     * Return the
     * <code>Orientation</code> of this
     * <code>Projection</code>.
     */
    @Override
    public Orientation getOrientation() {
        return Projection.Orientation.SECANT;
    }
    /**
     * Calculation of q from the equation (3-12) of Snyder in the USGS
     * professional paper 1395, "Map Projection - A Working Manual"
     * by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     */
    private double q(double lat) {
        double e = ellipsoid.getEccentricity();
        double esin = e*sin(lat);
        return (1-e*e)*(sin(lat)/(1-esin*esin)-log((1-esin)/(1+esin))/2/e);
    }

    /**
     * Transform coord using the Cylindrical Equal Area Projection. Input
     * coord is supposed to be a geographic latitude / longitude coordinate in
     * radians. Algorithm based on the USGS professional paper 1395,
     * "Map Projection - A Working Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     *
     * @param coord coordinate to transform
     * @throws CoordinateDimensionException if <code>coord</code> length is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws CoordinateDimensionException {
        double lat = coord[0];
        double a = ellipsoid.getSemiMajorAxis();
        coord[0] = FE + a*k0*(coord[1]-lon0);
        coord[1] = FN + a*q(lat)/2/k0;
        return coord;
    }
    
    /**
     * Creates the inverse operation for Cylindrical Equal Area Projection.
     * Input coord is supposed to be a projected easting / northing coordinate in meters.
     * Algorithm based on the USGS professional paper 1395,
     * "Map Projection - A Working Manual" by John P. Snyder :
     * <http://pubs.er.usgs.gov/publication/pp1395>
     * 
     * @param coord coordinate to transform
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return new CylindricalEqualArea(ellipsoid, parameters) {

            @Override
            public double[] transform(double[] coord) throws CoordinateDimensionException {
                double a = ellipsoid.getSemiMajorAxis();
                double e = ellipsoid.getEccentricity();
                double X = coord[0];
                double Y = coord[1];
                double qp = q(PI / 2);
                double beta = asin(2 * (Y - FN) * k0 / a / qp);
                double q = qp * sin(beta);
                if (abs(beta) == PI / 2) {
                    coord[0] = beta;
                } else {
                    final int MAXITER = 10;
                    double lat = asin(q / 2), latold = 1.E30;
                    int iter = 0;
                    while (++iter < MAXITER && Math.abs(lat - latold) > 1.E-15) {
                        latold = lat;
                        double esin = e * sin(lat);
                        lat = latold + pow(1 - esin * esin, 2) / 2 / cos(latold) / (1 - e * e) * (q - q(latold));
                    }
                    if (iter == MAXITER) {
                        throw new ArithmeticException("The inverse Polyconic Projection method diverges. Last value of tolerance = " + Math.abs(lat - latold));
                    }
                    coord[0] = lat;
                }
                coord[1] = lon0 + (X-FE)/a/k0;
                return coord;
            }
        };
    }
}