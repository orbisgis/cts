/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
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
package org.cts.op.transformation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.datum.Ellipsoid;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.op.CoordinateOperation;
import org.cts.op.Geocentric2Geographic;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.UnitConversion;
import org.cts.op.transformation.grids.BleggGeographicGrid;

/**
 * French Geocentric interpolation is a transformation used at IGN-France to
 * transform coordinates from the old local NTF system to the new ETRS-89
 * compatible RGF93.<p> It is a geocentric translation which parameters are
 * interpolated on a geographic grid.
 *
 * @author Michaël Michaud
 */
public class FrenchGeocentricNTF2RGF extends AbstractCoordinateOperation {

    private static final Identifier opId =
            new Identifier("EPSG", "9655", "French geographic interpolation", "NTF2RGF93");
    private static final GeocentricTranslation NTF2WGS84 =
            new GeocentricTranslation(-168.0, -60.0, 320.0);
    private static final Geocentric2Geographic GEOC2GEOG =
            new Geocentric2Geographic(Ellipsoid.GRS80);
    //private static GeographicExtent EXTENT =
    //    new GeographicExtent("gr3df97a", 41.0, 52.0, -5.5, 10.0, 360.0);
    private BleggGeographicGrid GRIDX, GRIDY, GRIDZ;
    private String gridPath;

    /**
     * Geocentric translation with parameters interpolated in a grid.<p>
     *
     * @param gridPath url of the geographic grid containing the translation
     *
     * "http://www.ign.fr/telechargement/MPro/geodesie/CIRCE/gr3df97a.txt"
     *
     * parameters
     */
    public FrenchGeocentricNTF2RGF(String gridPath) {
        super(opId);
        this.gridPath = gridPath;
        this.precision = 0.01;
        try {
            InputStream is;

            is = FrenchGeocentricNTF2RGF.class.getClassLoader().getResourceAsStream("fr/cts/datum/gr3df97a-tx.blegg");
            if (is == null) {
                GRIDX = new BleggGeographicGrid(new FileInputStream(gridPath + "gr3df97a-tx.blegg"));
            } else {
                GRIDX = new BleggGeographicGrid(is);
            }
            is = FrenchGeocentricNTF2RGF.class.getClassLoader().getResourceAsStream("fr/cts/datum/gr3df97a-ty.blegg");
            if (is == null) {
                GRIDY = new BleggGeographicGrid(new FileInputStream(gridPath + "gr3df97a-ty.blegg"));
            } else {
                GRIDY = new BleggGeographicGrid(is);
            }
            is = FrenchGeocentricNTF2RGF.class.getClassLoader().getResourceAsStream("fr/cts/datum/gr3df97a-tz.blegg");
            if (is == null) {
                GRIDZ = new BleggGeographicGrid(new FileInputStream(gridPath + "gr3df97a-tz.blegg"));
            } else {
                GRIDZ = new BleggGeographicGrid(is);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (coord.length != 3) {
            throw new CoordinateDimensionException(coord, 3);
        }
        // Creates a temporary coord to find the final translation parameters
        double[] coordi = coord.clone();

        // Translation using mean parameters (precision = +/- 5 m)
        coordi = NTF2WGS84.transform(coordi);

        // Find a rough position on GRS 80
        coordi = GEOC2GEOG.transform(coordi);

        // Get decimal degree coordinates for grid interpolation
        coordi = UnitConversion.RAD2DD.transform(coordi);

        // Definitive translation parameters are initialized with mean
        // translation parameters
        double tx = -168.0;
        double ty = -60.0;
        double tz = 320.0;
        // Get the definitive translation parameters from the grids
        try {
            tx = GRIDX.bilinearInterpolation(coordi[0], coordi[1]);
            ty = GRIDY.bilinearInterpolation(coordi[0], coordi[1]);
            tz = GRIDZ.bilinearInterpolation(coordi[0], coordi[1]);
        } catch (Exception e) {
            e.printStackTrace();
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
        return new FrenchGeocentricNTF2RGF(gridPath) {
            @Override
            public double[] transform(double[] coord)
                    throws IllegalCoordinateException {
                // Creates a temp coord to find the final translation parameters
                double[] coordi = coord.clone();
                // Find a rough position on GRS 80
                coordi = GEOC2GEOG.transform(coordi);
                // Get decimal degree coordinates for grid interpolation
                coordi = UnitConversion.RAD2DD.transform(coordi);
                // Definitive translation parameters are initialized with mean
                // translation parameters
                double tx = -168.0;
                double ty = -60.0;
                double tz = 320.0;
                // Get the definitive translation parameters from the grids
                try {
                    tx = GRIDX.bilinearInterpolation(coordi[0], coordi[1]);
                    ty = GRIDY.bilinearInterpolation(coordi[0], coordi[1]);
                    tz = GRIDZ.bilinearInterpolation(coordi[0], coordi[1]);
                } catch (Exception e) {
                    e.printStackTrace();
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
    }

    /**
     * Return a string representation of this transformation.
     */
    @Override
    public String toString() {
        return "French Geocentric transformation from NTF to RGF93";
    }
}