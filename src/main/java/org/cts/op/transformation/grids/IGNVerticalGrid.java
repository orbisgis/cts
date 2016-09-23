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
package org.cts.op.transformation.grids;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.cts.cs.GeographicExtent;

/**
 * <p>Classe representing an Altimetric grid as defined by IGN (France).</p>
 * Here is an exemple of an Altimetric Grid from IGN with some explanations.
 * -5.5 8.5 42.0 51.5 0.0333333333333 0.025 2 0 1 1 0. France continentale -
 * NGF-IGN69 dans RGF93 - version 2009<br>
 * 53.5019 99<br>
 * 53.5002 99<br>
 * <br>
 * -5.5 = longitude min<br>
 * 8.5 = longitude max<br>
 * 42.0 = latitude min<br>
 * 51.5 = latitude max<br>
 * 0.0333333333333 = gap between two consecutive values of longitude<br>
 * 0.025 = gap between two consecutive values of latitude<br>
 * 2 = type of ordering (1 : constant minimal longitude and growing latitude,
 * then growing longitude; 2 : constant maximal latitude and growing longitude,
 * then decreasing latitude; 3 : constant minimal longitude and decreasing
 * latitude, then growing longitude; 4 : constant minimal latitude and growing
 * longitude, then growing latitude)<br>
 * 0 = presence of the coordinates of each node (1 : yes, 0 : no)<br>
 * 1 = number of value(s) in each node (except the precision code)<br>
 * 1 = presence of the precision code (1 : yes, 0 : no)<br>
 * 0. = translation that must be applied to all values of the grid (as many as
 * the number of value in each node)<br>
 * France con... = description of the grid<br>
 *
 * @author Jules Party
 */
public class IGNVerticalGrid extends GeographicGrid {

    int orderType;
    boolean isCoordinate;
    boolean isPrecision;
    double[] globalTranslation;

    /**
     * <p>Construct a GeographicGrid from an InputStream representing an IGN
     * GeographicGrid. Default value of zip is true.</p>
     *
     * @param is
     */
    public IGNVerticalGrid(InputStream is) throws Exception {
        this(is, true);
    }

    /**
     * <p>Construct a GeographicGrid from an InputStream representing an IGN
     * GeographicGrid</p>
     *
     * @param is input stream
     * @param zip flag indicating if input data is zipped or not
     */
    public IGNVerticalGrid(InputStream is, boolean zip) throws Exception {
        String token;
        String ignFile;
        boolean lonlat;
        boolean firstRecord = true;

        if (zip) {
            try {
                // Decompression du fichier Zip
                ZipInputStream zis =
                        new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze = zis.getNextEntry();
                byte[] bytes = new byte[1024 * 32];
                StringBuilder sb = new StringBuilder();
                int nb;
                while ((nb = zis.read(bytes)) != -1) {
                    sb.append(new String(bytes, 0, nb));
                }
                ignFile = sb.toString();
            } catch (IOException e) {
                throw e;
            }
        } else {
            byte[] bb = new byte[is.available()];
            is.read(bb);
            ignFile = new String(bb);
        }

        //
        StringTokenizer st = new StringTokenizer(ignFile, "\r\n");
        String gr = st.nextToken();
        // Title (first line) decoder
        StringTokenizer stt = new StringTokenizer(gr, " \t");
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            x0 = Double.parseDouble(token);
        } else {
            throw new Exception("Missing min longitude in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            xL = Double.parseDouble(token);
        } else {
            throw new Exception("Missing maximum longitude in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            y0 = Double.parseDouble(token);
        } else {
            throw new Exception("Missing minimum latitude in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            yL = Double.parseDouble(token);
        } else {
            throw new Exception("Missing maximum latitude in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            dx = Double.parseDouble(token);
            double gridWidth = Math.rint((xL - x0) * 1000000000000d) / 1000000000000d;
            colNumber = (int) Math.rint(gridWidth / dx) + 1;
        } else {
            throw new Exception("Missing cell size in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            dy = Double.parseDouble(token);
            double gridHeight = Math.rint((yL - y0) * 1000000000000d) / 1000000000000d;
            rowNumber = (int) Math.rint(gridHeight / dy) + 1;
        } else {
            throw new Exception("Missing cell size in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            orderType = Integer.parseInt(token);
        } else {
            throw new Exception("Missing information in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            isCoordinate = token.equals("1");
        } else {
            throw new Exception("Missing information mode : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            dim = Integer.parseInt(token);
        } else {
            throw new Exception("Missing information in line : " + gr);
        }
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            isPrecision = token.equals("1");
        } else {
            throw new Exception("Missing information in line : " + gr);
        }
        globalTranslation = new double[dim];
        for (int i = 0; i < dim; i++) {
            if (stt.hasMoreTokens()) {
                token = stt.nextToken();
                globalTranslation[i] = Double.parseDouble(token);
            } else {
                throw new Exception("Missing precision unit in line : " + gr);
            }
        }
        // Lecture de la grille
        values = new double[rowNumber][colNumber][dim];
        int nbdec = 0;
        double lon;
        double lat;
        int index;
        int i = (orderType == 2 || orderType == 3) ? rowNumber - 1 : 0;
        int j = 0;
        int[] incr = new int[2];
        double[] t = new double[dim];
        lonlat = (orderType == 1 || orderType == 3);
        while (st.hasMoreTokens()) {
            index = 0;
            String[] gg = st.nextToken().trim().split("[ \t]+");
            int max = gg.length;
            while (index != max) {
                try {
                    if (isCoordinate) {
                        if (firstRecord) {
                            lonlat = (Double.parseDouble(gg[index]) == x0);
                        }
                        if (lonlat) {
                            lon = Double.parseDouble(gg[index]);
                            index++;
                            lat = Double.parseDouble(gg[index]);
                            index++;
                        } else {
                            lat = Double.parseDouble(gg[index]);
                            index++;
                            lon = Double.parseDouble(gg[index]);
                            index++;
                        }
                        i = (int) Math.rint((lat - y0) / dy);
                        j = (int) Math.rint((lon - x0) / dx);
                    } else {
                        incr = increment(i, j);
                    }
                    String[] dec = gg[index].split("\\.");
                    if (dec.length > 1) {
                        nbdec = Math.max(nbdec, dec[1].length());
                    }
                    for (int k = 0; k < dim; k++) {
                        t[k] = Double.parseDouble(gg[index]);
                        index++;
                    }
                    if (isPrecision) {
                        String prec = gg[index];
                        index++;
                    }
                    System.arraycopy(t, 0, values[i][j], 0, dim);
                    if (!isCoordinate) {
                        i = incr[0];
                        j = incr[1];
                    }
                    firstRecord = false;
                } catch (NumberFormatException nfe) {
                }
            }
        }
        // decimal part size --> scale
        scale = (int) Math.rint(Math.pow(10.0, (double) nbdec));
        extent = new GeographicExtent("GG", y0, yL, x0, xL, modulo);
    }

    /**
     * Return a table storing the new values of the parameter i and j used to
     * browse the table. The returned values depend on the order used to write
     * values in the file, the IGN used 4 different methods. For instance method
     * one means that the values were stored beginning by the minimal value of
     * longitude and latitude and order values by growing latitude, then growing
     * longitude.
     *
     * @param i the first indice used to browse the grid table (corresponding to
     * latitude)
     * @param j the second indice used to browse the grid table (corresponding
     * to longitude)
     * @return
     */
    private int[] increment(int i, int j) {
        int[] incr = new int[2];
        switch (orderType) {
            case 1:
                if (i != rowNumber - 1) {
                    i++;
                } else {
                    i = 0;
                    j++;
                }
                incr[0] = i;
                incr[1] = j;
                break;
            case 2:
                if (j != colNumber - 1) {
                    j++;
                } else {
                    j = 0;
                    i--;
                }
                incr[0] = i;
                incr[1] = j;
                break;
            case 3:
                if (i != 0) {
                    i--;
                } else {
                    i = rowNumber - 1;
                    j++;
                }
                incr[0] = i;
                incr[1] = j;
                break;
            case 4:
                if (j != colNumber - 1) {
                    j++;
                } else {
                    j = 0;
                    i++;
                }
                incr[0] = i;
                incr[1] = j;
                break;
        }
        return incr;
    }
}