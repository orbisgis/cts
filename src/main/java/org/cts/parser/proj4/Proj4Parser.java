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
package org.cts.parser.proj4;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.cts.parser.proj.ProjKeyParameters;

/**
 * A simple Proj4 parser to return a list of parameters used to build a
 * CoordinateSystem
 * 
 * @author Erwan Bocher, CNRS
 */
public class Proj4Parser {
    
     /**
     * The regex that must be used to parse.
     */
    static final Pattern regex = Pattern.compile("[ ]\\+|\\s<>");

    public static Map<String, String> readParameters(String projText) {
        if (projText == null || projText.isEmpty()) {
            throw new IllegalArgumentException("Please set a correct proj4 representation");
        }
        Map<String, String> params = new HashMap<String, String>();

        String[] tokens = regex.split(projText);

        if (tokens[0].startsWith("+proj")) {
            for (String token : tokens) {
                String[] keyValue = token.split("=");
                if (keyValue.length == 2) {
                    String key = formatKey(keyValue[0]);
                    ProjKeyParameters.checkUnsupported(key);
                    params.put(key, keyValue[1]);
                } else {
                    String key = formatKey(token);
                    ProjKeyParameters.checkUnsupported(key);
                    params.put(key, null);
                }
            }
            return params;
        } else {
            throw new IllegalArgumentException("The proj4 representation must startwith +proj");
        }
    }
    
    /**
     * Remove + char if exists
     *
     * @param key
     */
    private static String formatKey(String key) {
        String formatKey = key;
        if (key.startsWith("+")) {
            formatKey = key.substring(1);
        }
        return formatKey;
    }

   
    
}
