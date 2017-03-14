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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A utility class to manage grids
 * @author Erwan Bocher
 */
public class GridUtils {
    
    static String URL_PATH="https://github.com/orbisgis/cts/raw/master/grids/";

    /**
     * Find the grid used by the transformation
     * 
     * If the grid is not packaged into the resources folder then we look into a
     * ./cts folder. 
     * If the grid does not exist in this folder, we try to download it from
     * the CTS repository.
     * 
     * @param nameGrid name of the grid with its extension
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static File findGrid(String nameGrid) throws FileNotFoundException, IOException {
        //If the grid doesn't exist in resources folder then
        //We look into a .cts folder
        String ctsFolderPath = new File(System.getProperty("user.home")).getAbsolutePath() + File.separator + ".cts";
        File ctsFileFolder = new File(ctsFolderPath);
        File gridFile = new File(ctsFolderPath + File.separator + nameGrid);
        if (ctsFileFolder.exists()) {
           if (gridFile.exists()) {
                return gridFile;
            } else if(ctsFileFolder.canWrite()) {
                //Try to download the grid on the CTS repository
                downloadFile(gridFile, new URL(URL_PATH+nameGrid));                
                return gridFile;
            }
            else{                
                throw new IOException("Cannot download the grid : "+ nameGrid+ " into .cts folder");
            }
        } else {
            boolean ctsDir = ctsFileFolder.mkdir();
            if (ctsDir && ctsFileFolder.canWrite()) {
                //Try to download the grid on the CTS repository
                downloadFile(gridFile, new URL(URL_PATH+nameGrid));
                return gridFile;
            } else {
                throw new IOException("Unable to create the .cts folder");
            }
        }
    }
    
    /**
     * Download a file from an URL to a specified target
     * 
     * @param outputFile
     * @param urlGrid
     * @throws MalformedURLException
     * @throws IOException 
     */
    public static void downloadFile(final File outputFile, final URL urlGrid)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(urlGrid.openStream());
            fout = new FileOutputStream(outputFile);
            
            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ie) {
                //Do nothing
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException ie) {
                //Do nothing
            }
        }
    }

}
