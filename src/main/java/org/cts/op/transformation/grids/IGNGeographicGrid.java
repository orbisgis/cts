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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>Classe representing a Geographic grid as defined by IGN (France).</p>
 * Here is an exemple of a Geographic Grid from IGN with some explanations.
 * GR1D REFALT 700 20370201<br>
 * GR1D1 -61.7000 -61.4750  15.8000  15.9250   .0250  .0250<br>
 * GR1D2 INTERPOLATION BILINEAIRE<br>
 * GR1D3 PREC CM 01:5 02:10 03:20 04:50 99>100<br>
 *   -61.700   15.925   40.0   03<br>
 *   -61.700   15.900   40.0   03<br>
 * <br>
 * GR     = GRID<br>
 * 1D     = one dimension<br>
 * REFALT = Referentiel alti<br>
 * 700    = referentiel number<br>
 * 2      = Coordinate type (Geographic coordinates)<br>
 * 037    = Ellipso�de (GRS80)<br>
 * 02     = Unites (Decimal degree)<br>
 * 01     = Prime Merdian (Greenwich)<br>
 * G1RD1  = line containing longitude min, longitude max,<br>
 *                          latitude min, latitude max,<br>
 *                          cell size (longitude)<br>
 *                          cell size (latitude)<br>
 * GR1D2  = Interpolation mode<br>
 * GR1D3  = line containing precision unit and codes used in the grid<br>
 * The grid is following with one line per node containing<br>
 * longitude, latitude, N, precision code<br>
 * For geoid grids :<br>
 * N = He - A = Ellipsoidal height - Altitude (above geoid)<br>
 * @author Michaël Michaud
 */

public class IGNGeographicGrid extends GeographicGrid {
    
    
   /**
    * <p>Construct a GeographicGrid from an InputStream representing an IGN
    * GeographicGrid. Default value of zip is true.</p>
    * @param is
    */
    public IGNGeographicGrid(InputStream is) throws Exception {
        this(is, true);
    }
    
   /**
    * <p>Construct a GeographicGrid from an InputStream representing an IGN
    * GeographicGrid</p>
    * @param is input stream
    * @param zip flag indicating if input data is zipped or not
    */
    public IGNGeographicGrid(InputStream is, boolean zip) throws Exception {
        String token;
        int dim = 0;
        String gridType;
        int datumId;
        int coordinateType;
        int geographicDatumId;
        int unit;
        int primeMeridian;
        double xmin, xmax, ymin, ymax;
        String interpolationMode;
        String precisionUnit;
        ConcurrentHashMap precisionCodes = new ConcurrentHashMap();
        String ignFile;
        
        if (zip) {
            try {
                // Decompression du fichier Zip
                ZipInputStream zis =
                    new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze = zis.getNextEntry();
                byte[] bytes = new byte[1024*32];
                StringBuilder sb = new StringBuilder();
                int nb;
                while ((nb = zis.read(bytes)) != -1) {
                    sb.append(new String(bytes,0,nb));
                }
                ignFile = sb.toString();
            }
            catch (Exception e) {throw e;}
        }
        else {
            byte[] bb = new byte[is.available()];
            is.read(bb);
            ignFile = new String(bb);
        }

        //
        StringTokenizer st = new StringTokenizer(ignFile, "\r\n");
        String gr = st.nextToken();
        String gr1 = st.nextToken();
        String gr2 = st.nextToken();
        String gr3 = st.nextToken();
        //String grid = st.nextToken();
        // First line decoder
        StringTokenizer stt = new StringTokenizer(gr, " \t");
        if (stt.hasMoreTokens()) {
            dim = Integer.parseInt(stt.nextToken().substring(2,3));
        }
        else {throw new Exception("Missing information in line : " + gr);}
        if (stt.hasMoreTokens()) {
            gridType = stt.nextToken();
        }
        else {throw new Exception("Missing information in line : " + gr);}
        if (stt.hasMoreTokens()) {
            datumId = Integer.parseInt(stt.nextToken());
        }
        else {throw new Exception("Missing information in line : " + gr);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            coordinateType = Integer.parseInt(token.substring(0,1));
            geographicDatumId = Integer.parseInt(token.substring(1,4));
            unit = Integer.parseInt(token.substring(4,6));
            primeMeridian = Integer.parseInt(token.substring(6,8));
        }
        else {throw new Exception("Missing information in line : " + gr);}
        // Second line decoder
        stt = new StringTokenizer(gr1, " \t");
        if (stt.hasMoreTokens()) {token = stt.nextToken();}
        else {throw new Exception("Missing information in line : " + gr1);}
        if (stt.hasMoreTokens() && token.endsWith("1")) {
            token = stt.nextToken();
            xmin = Double.parseDouble(token);
            x0 = xmin;
        }
        else {throw new Exception("Missing min longitude in line : " + gr1);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            xmax = Double.parseDouble(token);
        }
        else {throw new Exception("Missing maximum longitude in line : " + gr1);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            ymin = Double.parseDouble(token);
        }
        else {throw new Exception("Missing minimum latitude in line : " + gr1);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            ymax = Double.parseDouble(token);
            y0 = ymax;
        }
        else {throw new Exception("Missing maximum latitude in line : " + gr1);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            dx = Double.parseDouble(token);
            double gridWidth = Math.rint((xmax-xmin)*1000000000000d)/1000000000000d;
            colNumber = (int)Math.rint(gridWidth/dx)+1;
        }
        else {throw new Exception("Missing cell size in line : " + gr1);}
        if (stt.hasMoreTokens()) {
            token = stt.nextToken();
            dy = Double.parseDouble(token);
            double gridHeight = Math.rint((ymax-ymin)*1000000000000d)/1000000000000d;
            rowNumber = (int)Math.rint(gridHeight/dy)+1;
        }
        else {throw new Exception("Missing cell size in line : " + gr1);}
        // Third line decoder
        stt = new StringTokenizer(gr2, " \t");
        if (stt.hasMoreTokens()) {token = stt.nextToken();}
        else {throw new Exception("Missing information in line : " + gr2);}
        if (stt.hasMoreTokens() && token.endsWith("2")) {
            token = stt.nextToken("");
            interpolationMode = token;
        }
        else {throw new Exception("Missing interpolation mode : " + gr2);}
        // Forth line decoder
        stt = new StringTokenizer(gr3, " \t");
        if (stt.hasMoreTokens()) {stt.nextToken();}
        else {throw new Exception("Missing information in line : " + gr3);}
        if (stt.hasMoreTokens()) {stt.nextToken();}
        else {throw new Exception("Missing information in line : " + gr3);}
        if (stt.hasMoreTokens()) {
            stt.nextToken();
            precisionUnit = stt.nextToken();
        }
        else {throw new Exception("Missing precision unit in line : " + gr3);}
        if (stt.hasMoreTokens()) {
            while (stt.hasMoreTokens()) {
                token = stt.nextToken();
                String[] precisionCode = token.split("[:>]");
                if (precisionCode.length == 2) {
                    precisionCodes.put(precisionCode[0], precisionCode[1]);
                }
            }
        }
        // Lecture de la grille
        values = new float[rowNumber][colNumber];
        int nbdec = 0;
        while (st.hasMoreTokens()) {
            String[] gg = st.nextToken().trim().split("[ \t]+");
            try {
                if (gg.length > 3) {
                    double lon = Double.parseDouble(gg[0]);
                    double lat = Double.parseDouble(gg[1]);
                    double h = Double.parseDouble(gg[2]);
                    nbdec = Math.max(nbdec, gg[2].split("\\.")[1].length());
                    values[(int)Math.rint((ymax-lat)/dy)][(int)Math.rint((lon-xmin)/dx)] =
                      (float)h;
                }
            }
            catch(NumberFormatException nfe) {
              System.out.println(gg[0]);
              System.out.println(gg[1]);
              System.out.println(gg[2]);
            }
        }
        // decimal part size --> scale
        scale = (int)Math.rint(Math.pow(10.0, (double)nbdec));
    }

}
