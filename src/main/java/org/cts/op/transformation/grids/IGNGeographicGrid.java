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
package org.cts.op.transformation.grids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.StringTokenizer;

import org.cts.cs.GeographicExtent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Classe representing a Geographic grid as defined by IGN (France).</p>
 * Here is an exemple of a Geographic Grid from IGN with some explanations. 
 * GR1D  REFALT 700 20370201<br>
 * GR1D1 -61.7000 -61.4750 15.8000 15.9250 .0250 .0250<br>
 * GR1D2 INTERPOLATION BILINEAIRE<br>
 * GR1D3 PREC CM 01:5 02:10 03:20 04:50 99>100<br>
 * -61.700 15.925 40.0 03<br>
 * -61.700 15.900 40.0 03<br>
 * <br>
 * GR = GRID<br>
 * 1D = one dimension<br>
 * REFALT = Referentiel alti<br>
 * 700 = referentiel number<br>
 * 2 = Coordinate type (Geographic coordinates)<br>
 * 037 = Ellipsoïde (GRS80)<br>
 * 02 = Unites (Decimal degree)<br>
 * 01 = Prime Merdian (Greenwich)<br>
 * G1RD1 = line containing longitude min, longitude max,<br>
 * latitude min, latitude max,<br>
 * cell size (longitude)<br>
 * cell size (latitude)<br>
 * GR1D2 = Interpolation mode<br>
 * GR1D3 = line containing precision unit and codes used in the grid<br>
 * The grid is following with one line per node containing<br>
 * longitude, latitude, N, precision code<br>
 * For geoid grids :<br>
 * N = He - A = Ellipsoidal height - Altitude (above geoid)<br>
 *
 * @author Michaël Michaud, Jules Party, Erwan Bocher
 */
public class IGNGeographicGrid extends GeographicGrid {

    static final Logger LOGGER = LoggerFactory.getLogger(IGNGeographicGrid.class);
    String gridType;
    int datumId;
    int coordinateType;
    int geographicDatumId;
    int unit;
    int primeMeridian;
    String interpolationMode;
    String precisionUnit;

    /**
     * <p>Construct a GeographicGrid from an InputStream representing an IGN
     * GeographicGrid</p>
     *
     * @param is input stream
     */
    public IGNGeographicGrid(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            //Read the header           
            String token;
            double xmin, xmax, ymin, ymax;
            ConcurrentHashMap precisionCodes = new ConcurrentHashMap();


            // Read the first line
            String gr = reader.readLine();
            StringTokenizer stt = new StringTokenizer(gr, " \t");
            if (stt.hasMoreTokens()) {
                dim = Integer.parseInt(stt.nextToken().substring(2, 3));
            } else {
                throw new IOException("Missing information in line : " + gr);
            }
            if (stt.hasMoreTokens()) {
                gridType = stt.nextToken();
            } else {
                throw new Exception("Missing information in line : " + gr);
            }
            if (stt.hasMoreTokens()) {
                datumId = Integer.parseInt(stt.nextToken());
            } else {
                throw new Exception("Missing information in line : " + gr);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                coordinateType = Integer.parseInt(token.substring(0, 1));
                geographicDatumId = Integer.parseInt(token.substring(1, 4));
                unit = Integer.parseInt(token.substring(4, 6));
                primeMeridian = Integer.parseInt(token.substring(6, 8));
            } else {
                throw new Exception("Missing information in line : " + gr);
            }

            // Second line decoder -> grid1

            String gr1 = reader.readLine();
            stt = new StringTokenizer(gr1, " \t");
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
            } else {
                throw new Exception("Missing information in line : " + gr1);
            }
            if (stt.hasMoreTokens() && token.endsWith("1")) {
                token = stt.nextToken();
                xmin = Double.parseDouble(token);
                x0 = xmin;
            } else {
                throw new Exception("Missing min longitude in line : " + gr1);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                xmax = Double.parseDouble(token);
                xL = xmax;
            } else {
                throw new Exception("Missing maximum longitude in line : " + gr1);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                ymin = Double.parseDouble(token);
                y0 = ymin;
            } else {
                throw new Exception("Missing minimum latitude in line : " + gr1);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                ymax = Double.parseDouble(token);
                yL = ymax;
            } else {
                throw new Exception("Missing maximum latitude in line : " + gr1);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                dx = Double.parseDouble(token);
                double gridWidth = Math.rint((xmax - xmin) * 1000000000000d) / 1000000000000d;
                colNumber = (int) Math.rint(gridWidth / dx) + 1;
            } else {
                throw new Exception("Missing cell size in line : " + gr1);
            }
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                dy = Double.parseDouble(token);
                double gridHeight = Math.rint((ymax - ymin) * 1000000000000d) / 1000000000000d;
                rowNumber = (int) Math.rint(gridHeight / dy) + 1;
            } else {
                throw new Exception("Missing cell size in line : " + gr1);
            }
            // Third line decoder -> grid2

            String gr2 = reader.readLine();
            stt = new StringTokenizer(gr2, " \t");
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
            } else {
                throw new Exception("Missing information in line : " + gr2);
            }
            if (stt.hasMoreTokens() && token.endsWith("2")) {
                token = stt.nextToken("");
                interpolationMode = token;
            } else {
                throw new Exception("Missing interpolation mode : " + gr2);
            }
            // Forth line decoder -> grid3
            String gr3 = reader.readLine();
            stt = new StringTokenizer(gr3, " \t");
            if (stt.hasMoreTokens()) {
                stt.nextToken();
            } else {
                throw new Exception("Missing information in line : " + gr3);
            }
            if (stt.hasMoreTokens()) {
                stt.nextToken();
            } else {
                throw new Exception("Missing information in line : " + gr3);
            }
            if (stt.hasMoreTokens()) {
                stt.nextToken();
                precisionUnit = stt.nextToken();
            } else {
                throw new Exception("Missing precision unit in line : " + gr3);
            }
            if (stt.hasMoreTokens()) {
                while (stt.hasMoreTokens()) {
                    token = stt.nextToken();
                    String[] precisionCode = token.split("[:>]");
                    if (precisionCode.length == 2) {
                        precisionCodes.put(precisionCode[0], precisionCode[1]);
                    }
                }
            }
            // Read the grid
            values = new double[rowNumber][colNumber][dim];

            int nbdec = 0;
            String line;
            while (null != (line = reader.readLine())) {
                String[] gg = line.split("[ \t]+");
                try {
                    double lon = Double.parseDouble(gg[1]);
                    double lat = Double.parseDouble(gg[2]);
                    double[] t = new double[dim];
                    for (int i = 0; i < dim; i++) {
                        t[i] = Double.parseDouble(gg[3 + i]);
                    }
                    //String prec = gg[3 + dim];
                    nbdec = Math.max(nbdec, gg[3].split("\\.")[1].length());
                    System.arraycopy(t, 0, values[(int) Math.rint((lat - y0) / dy)][(int) Math.rint((lon - x0) / dx)], 0, dim);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn("Cannot parse the number long : " + gg[0] + " lat : " + gg[1] + " dim :" + gg[2]);
                }
            }
            // decimal part size --> scale
            scale = (int) Math.rint(Math.pow(10.0, (double) nbdec));
            extent = new GeographicExtent("GG", y0, yL, x0, xL, modulo);
        } finally {
            reader.close();
        }

    }
}