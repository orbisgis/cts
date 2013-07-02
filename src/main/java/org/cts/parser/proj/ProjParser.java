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
 *
 * @author Erwan Bocher
 */
public class ProjParser {

    private final Registry registry;

    public ProjParser(Registry registry) {
        this.registry = registry;
    }

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
     * @param nameOfCRS
     * @param regex the pattern used to split the line that describes the
     * coordinate system
     * @return
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
            } else if (line.startsWith("<") && line.endsWith(">")) {
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
                            v.put(key, null);
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
     * @return
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
                if (line.startsWith("#")) {
                } else if (line.startsWith("<") && line.endsWith(">")) {
                    String[] tokens = regex.split(line);
                    for (String token : tokens) {
                        if (token.startsWith("<") && token.endsWith(">")
                                && token.length() > 2) {
                            codes.add(token.substring(1, token.length() - 1));
                        } else if (token.equals("<>")) {
                            break;
                        } else {
                        }
                    }
                }
            }
            return codes;
        } finally {
            br.close();
        }
    }
}
