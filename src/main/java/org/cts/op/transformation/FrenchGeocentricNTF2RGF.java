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

package org.cts.op.transformation;

import java.io.FileInputStream;
import java.io.InputStream;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.cs.OutOfExtentException;
import org.cts.datum.Ellipsoid;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.op.CoordinateOperation;
import org.cts.op.Geocentric2Geographic;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.UnitConversion;
import org.cts.op.transformation.grids.IGNGeographicGrid;
import org.cts.units.Unit;

/**
 * French Geocentric interpolation is a transformation used at IGN-France to
 * transform coordinates from the old local NTF system to the new ETRS-89
 * compatible RGF93.<p> It is a geocentric translation which parameters are
 * interpolated on a geographic grid.
 *
 * @author Michaël Michaud, Jules Party, Erwan Bocher
 */
public class FrenchGeocentricNTF2RGF extends AbstractCoordinateOperation {

    /**
     * The Identifier used for all French Geocentric NTF to RGF transformation.
     */
    private static final Identifier opId =
            new Identifier("EPSG", "9655", "French geographic interpolation", "NTF2RGF93");
    private static final GeocentricTranslation NTF2WGS84 =
            new GeocentricTranslation(-168.0, -60.0, 320.0);
    private static final Geocentric2Geographic GEOC2GEOG =
            new Geocentric2Geographic(Ellipsoid.GRS80);
    public final static UnitConversion RAD2DD = UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE);
    /**
     * The GeographicGrid that define this transformation.
     */
    private IGNGeographicGrid GRID3D;

    /**
     * Geocentric translation with parameters interpolated in a grid.<p> The
     * gride can be found <a href =
     * http://geodesie.ign.fr/contenu/fichiers/documentation/rgf93/gr3df97a.txt>here</a>.
     *
     * @param gridPath url of the geographic grid containing the translation
     * parameters
     */
    public FrenchGeocentricNTF2RGF(String gridPath) throws Exception {
        super(opId);
        this.precision = 0.01;
        try {
            InputStream is = FrenchGeocentricNTF2RGF.class.getResourceAsStream("grids/gr3df97a.txt");
            if (is == null) {
                GRID3D = new IGNGeographicGrid(new FileInputStream(gridPath + "gr3df97a.txt"));
            } else {
                GRID3D = new IGNGeographicGrid(is);
            }
        } catch (Exception e) {
            throw new Exception( "\nThis problem occured when trying to load the gr3df97a.txt grid file", e);
        }
    }

    /**
     * Geocentric translation with parameters interpolated in a grid.<p> The
     * gride can be found <a href =
     * http://geodesie.ign.fr/contenu/fichiers/documentation/rgf93/gr3df97a.txt>here</a>.
     */
    public FrenchGeocentricNTF2RGF() throws Exception {
        super(opId);
        this.precision = 0.01;
        try {
            InputStream is = FrenchGeocentricNTF2RGF.class.getResourceAsStream("grids/gr3df97a.txt");
            GRID3D = new IGNGeographicGrid(is);
        } catch (Exception e) {
            throw new Exception("\nThis problem occured when trying to load the gr3df97a.txt grid file", e);
        }
    }

    /**
     * Transforms NTF Geocentric coordinate into RGF93 geocentric coordinate.
     *
     * @param coord coordinate to transform
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord.length < 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        // Creates a temporary coord to find the final translation parameters
        double[] coordi = coord.clone();

        // Translation using mean parameters (precision = +/- 5 m)
        coordi = NTF2WGS84.transform(coordi);

        // Find a rough position on GRS 80
        coordi = GEOC2GEOG.transform(coordi);

        double oldLon = 10;
        double oldLat = 10;

        // Definitive translation parameters are initialized with mean
        // translation parameters
        double tx = -168.0;
        double ty = -60.0;
        double tz = 320.0;

        while (Math.max(Math.abs(oldLon - coordi[0]), Math.abs(oldLat - coordi[1])) > 1e-11) {

            oldLon = coordi[0];
            oldLat = coordi[1];

            // Get decimal degree coordinates for grid interpolation
            coordi = RAD2DD.transform(coordi);

            // Get the definitive translation parameters from the grids
            try {
                double[] t = GRID3D.bilinearInterpolation(coordi[0], coordi[1]);
                tx = t[0];
                ty = t[1];
                tz = t[2];
            } catch (OutOfExtentException e) {
                throw new IllegalCoordinateException(e.getMessage());
            }

            coordi[0] = tx + coord[0];
            coordi[1] = ty + coord[1];
            coordi[2] = tz + coord[2];

            coordi = GEOC2GEOG.transform(coordi);
        }

        // Apply definitive translation
        coord[0] = tx + coord[0];
        coord[1] = ty + coord[1];
        coord[2] = tz + coord[2];
        return coord;
    }

    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        try {
            return new FrenchGeocentricNTF2RGF() {
                @Override
                public double[] transform(double[] coord)
                        throws IllegalCoordinateException {
                    // Creates a temp coord to find the final translation parameters
                    double[] coordi = coord.clone();
                    // Find a rough position on GRS 80
                    coordi = GEOC2GEOG.transform(coordi);
                    // Get decimal degree coordinates for grid interpolation
                    coordi = RAD2DD.transform(coordi);
                    // Definitive translation parameters are initialized with mean
                    // translation parameters
                    double tx = -168.0;
                    double ty = -60.0;
                    double tz = 320.0;
                    // Get the definitive translation parameters from the grids
                    try {
                        double[] t = GRID3D.bilinearInterpolation(coordi[0], coordi[1]);
                        tx = t[0];
                        ty = t[1];
                        tz = t[2];
                    } catch (OutOfExtentException e) {
                        throw new IllegalCoordinateException(e.getMessage());
                    }
                    // Apply definitive translation
                    coord[0] = -tx + coord[0];
                    coord[1] = -ty + coord[1];
                    coord[2] = -tz + coord[2];
                    return coord;
                }

                @Override
                public CoordinateOperation inverse()
                        throws NonInvertibleOperationException {
                    return FrenchGeocentricNTF2RGF.this;
                }
            };
        } catch (Exception e) {
            throw new NonInvertibleOperationException(e.getMessage());
        }
    }

    /**
     * Return a string representation of this transformation.
     */
    @Override
    public String toString() {
        return "French Geocentric transformation from NTF to RGF93";
    }
}
