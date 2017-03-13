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

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Martin Davis
 */
public class ProjKeyParameters {

    public static final String comments = "comments";
    public static final String a = "a";
    public static final String b = "b";
    public static final String f = "f";
    public static final String alpha = "alpha";
    public static final String gamma = "gamma";
    public static final String datum = "datum";
    public static final String ellps = "ellps";
    public static final String es = "es";
    public static final String azi = "azi";
    public static final String axis = "axis";
    public static final String k = "k";
    public static final String k_0 = "k_0";
    public static final String lat_ts = "lat_ts";
    public static final String lat_0 = "lat_0";
    public static final String lat_1 = "lat_1";
    public static final String lat_2 = "lat_2";
    public static final String lon_0 = "lon_0";
    public static final String lonc = "lonc";
    public static final String pm = "pm";
    public static final String proj = "proj";
    public static final String R = "R";
    public static final String R_A = "R_A";
    public static final String R_a = "R_a";
    public static final String R_V = "R_V";
    public static final String R_g = "R_g";
    public static final String R_h = "R_h";
    public static final String R_lat_a = "R_lat_a";
    public static final String R_lat_g = "R_lat_g";
    public static final String rf = "rf";
    public static final String south = "south";
    public static final String to_meter = "to_meter";
    public static final String towgs84 = "towgs84";
    public static final String units = "units";
    public static final String x_0 = "x_0";
    public static final String y_0 = "y_0";
    public static final String zone = "zone";
    public static final String title = "title";
    public static final String nadgrids = "nadgrids";
    public static final String no_defs = "no_defs";
    public static final String wktext = "wktext";
    public static final String bsouth = "bsouth";
    private static Set<String> supportedParams = null;

    /**
     * Create an array with the list of supported parameters.
     */
    public static Set supportedParameters() {
        if (supportedParams == null) {
            supportedParams = new TreeSet<String>();
            supportedParams.add(a);
            supportedParams.add(rf);
            supportedParams.add(f);
            supportedParams.add(alpha);
            supportedParams.add(gamma);
            supportedParams.add(es);
            supportedParams.add(b);
            supportedParams.add(datum);
            supportedParams.add(ellps);
            supportedParams.add(R_A);
            supportedParams.add(k);
            supportedParams.add(k_0);
            supportedParams.add(lat_ts);
            supportedParams.add(lat_0);
            supportedParams.add(lat_1);
            supportedParams.add(lat_2);
            supportedParams.add(lon_0);
            supportedParams.add(lonc);
            supportedParams.add(x_0);
            supportedParams.add(y_0);
            supportedParams.add(proj);
            supportedParams.add(south);
            supportedParams.add(towgs84);
            supportedParams.add(to_meter);
            supportedParams.add(units);
            supportedParams.add(zone);
            supportedParams.add(axis);
            supportedParams.add(title);
            supportedParams.add(no_defs);
            supportedParams.add(wktext);
            supportedParams.add(nadgrids);
            supportedParams.add(pm);

        }
        return supportedParams;
    }

    /**
     * Return true if the parameter is supported.
     *
     * @param paramKey
     * @return 
     */
    public static boolean isSupported(String paramKey) {
        return supportedParameters().contains(paramKey);
    }

    /**
     * If the parameter is not supported throw an exception
     *
     * @param paramKey
     */
    public static void checkUnsupported(String paramKey) {
        if (!isSupported(paramKey)) {
            throw new ParameterException(paramKey + " parameter is not supported");
        }
    }
}
