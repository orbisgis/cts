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
package org.cts.parser.proj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.cts.registry.Registry;

/**
 * Parser used to read a proj file as the one used in proj4 library.
 * It can return the list of codes available in the file
 * or the map of parameters associated to a particular code.
 *
 * @author Erwan Bocher
 */
public class ProjParser {

    /**
     * The registry parse by this parser.
     */
    private final Registry registry;

    /**
     * Create a new ProjParser for the given registry.
     *
     * @param registry the registry to parse
     */
    public ProjParser(Registry registry) {
        this.registry = registry;
    }

    /**
     * Read all parameters from the registry
     *
     * @param crsCode the code corresponding to the information that must be
     * extracted from the registry
     * @param regexPattern the pattern used to split the line that describes the
     * coordinate system
     * @return 
     * @throws IOException
     */
    public Map<String, String> readParameters(String crsCode, Pattern regexPattern)
            throws IOException {
        InputStream inStr = Registry.class.getResourceAsStream(registry.getRegistryName());
        if (inStr == null) {
            throw new IllegalStateException("Unable to access CRS file: " + registry.getRegistryName());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStr));
        Map<String, String> args;
        try {
            args = readRegistry(reader, crsCode, regexPattern);

        } finally {
            reader.close();
        }
        return args;
    }

    /**
     * Read all parameters from the registry
     *
     * @param br
     * @param nameOfCRS the code corresponding to the information that must be
     * extracted from the registry
     * @param regex the pattern used to split the line that describes the
     * coordinate system
     * @throws IOException
     */
    private Map<String, String> readRegistry(BufferedReader br, String nameOfCRS, Pattern regex) throws IOException {
        String line;
        String crsName = null;
        while (null != (line = br.readLine())) {
            if (line.startsWith("#")) {
                // in the "epsg" file, the crs name can only be read in the
                // comment line preceding the projection definition
                crsName = line.substring(1).trim();
            } else if (line.startsWith("<")) {
                while (!line.endsWith(">")) {
                    int i = line.indexOf('#');
                    if (i != -1) {
                        // in the "world" file, the crs name can only be read in
                        // a comment following the key tag
                        crsName = line.substring(i + 2);
                        line = line.substring(0, i - 1);
                    }
                    line = line + " " + br.readLine();
                }
                String[] tokens = regex.split(line);
                Map<String, String> v = new HashMap<String, String>();
                String crsID;
                boolean crsFound = true;
                for (String token : tokens) {
                    if (token.startsWith("<") && token.endsWith(">")
                            && token.length() > 2) {
                        crsID = token.substring(1, token.length() - 1);
                        if (!crsID.equalsIgnoreCase(nameOfCRS)) {
                            crsFound = false;
                            crsName = null;
                            break;
                        }
                    } else if (token.equals("<>")) {
                        break;
                    } else {
                        String[] keyValue = token.split("=");
                        if (keyValue.length == 2) {
                            String key = formatKey(keyValue[0]);
                            ProjKeyParameters.checkUnsupported(key);
                            v.put(key, keyValue[1]);
                        } else {
                            String key = formatKey(token);
                            ProjKeyParameters.checkUnsupported(key);
                            if (key.equals(ProjKeyParameters.wktext)) {
                                String[] lines = regex.split(line, 2);
                                v.put(key, lines[1]);
                            } else {
                                v.put(key, null);
                            }
                        }
                    }
                }
                // found requested CRS?
                if (crsFound) {
                    if (!v.containsKey(ProjKeyParameters.title) && crsName != null) {
                        v.put(ProjKeyParameters.title, crsName);
                    }
                    return v;
                }
            }
        }
        return null;
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

    /**
     * Return the list of all codes defined by this registry
     *
     * @param regex pattern
     * @return 
     * @throws java.io.IOException
     */
    public Set<String> getSupportedCodes(Pattern regex) throws IOException {
        InputStream inStr = Registry.class.getResourceAsStream(registry.getRegistryName());
        if (inStr == null) {
            throw new IllegalStateException("Unable to access CRS file: " + registry.getRegistryName());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
        try {
            Set<String> codes = new HashSet<String>();
            String line;
            while (null != (line = br.readLine())) {
                if (line.startsWith("<")) {
                    String token = regex.split(line, 2)[0];
                    codes.add(token.substring(1, token.length() - 1));
                }
            }
            return codes;
        } finally {
            br.close();
        }
    }
}
