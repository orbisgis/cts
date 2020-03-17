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
package org.cts.util;

import org.orbisgis.commons.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to get UTM information from latitude and longitude coordinates
 *
 * @author Erwan Bocher (CNRS)
 */
public class UTMUtils {

    /**
     * UTM span in degrees.
     */
    private static final int UTM_SPAN = 6;

    /**
     * UTM zone on 0° used as offset when converting lat/lon to UTM zone.
     */
    private static final int UTM_ZONE_OFFSET = 31;

    /**
     * {@link Map} with the UTM zone + hemisphere string representation ([0-9][0-9][NS]) as key and the corresponding
     * EPSG code as value.
     */
    private static Map<String, Integer> utmEpsg = new HashMap<>();

    // Initialisation of the utmEpsg map.
    static {
        utmEpsg.put("1S", 32701);
        utmEpsg.put("2S", 32702);
        utmEpsg.put("3S", 32703);
        utmEpsg.put("4S", 32704);
        utmEpsg.put("5S", 32705);
        utmEpsg.put("6S", 32706);
        utmEpsg.put("7S", 32707);
        utmEpsg.put("8S", 32708);
        utmEpsg.put("9S", 32709);
        utmEpsg.put("10S", 32710);
        utmEpsg.put("11S", 32711);
        utmEpsg.put("12S", 32712);
        utmEpsg.put("13S", 32713);
        utmEpsg.put("14S", 32714);
        utmEpsg.put("15S", 32715);
        utmEpsg.put("16S", 32716);
        utmEpsg.put("17S", 32717);
        utmEpsg.put("18S", 32718);
        utmEpsg.put("19S", 32719);
        utmEpsg.put("20S", 32720);
        utmEpsg.put("21S", 32721);
        utmEpsg.put("22S", 32722);
        utmEpsg.put("23S", 32723);
        utmEpsg.put("24S", 32724);
        utmEpsg.put("25S", 32725);
        utmEpsg.put("26S", 32726);
        utmEpsg.put("27S", 32727);
        utmEpsg.put("28S", 32728);
        utmEpsg.put("29S", 32729);
        utmEpsg.put("30S", 32730);
        utmEpsg.put("31S", 32731);
        utmEpsg.put("32S", 32732);
        utmEpsg.put("33S", 32733);
        utmEpsg.put("34S", 32734);
        utmEpsg.put("35S", 32735);
        utmEpsg.put("36S", 32736);
        utmEpsg.put("37S", 32737);
        utmEpsg.put("38S", 32738);
        utmEpsg.put("39S", 32739);
        utmEpsg.put("40S", 32740);
        utmEpsg.put("41S", 32741);
        utmEpsg.put("42S", 32742);
        utmEpsg.put("43S", 32743);
        utmEpsg.put("44S", 32744);
        utmEpsg.put("45S", 32745);
        utmEpsg.put("46S", 32746);
        utmEpsg.put("47S", 32747);
        utmEpsg.put("48S", 32748);
        utmEpsg.put("49S", 32749);
        utmEpsg.put("50S", 32750);
        utmEpsg.put("51S", 32751);
        utmEpsg.put("52S", 32752);
        utmEpsg.put("53S", 32753);
        utmEpsg.put("54S", 32754);
        utmEpsg.put("55S", 32755);
        utmEpsg.put("56S", 32756);
        utmEpsg.put("57S", 32757);
        utmEpsg.put("58S", 32758);
        utmEpsg.put("59S", 32759);
        utmEpsg.put("60S", 32760);
        utmEpsg.put("1N", 32601);
        utmEpsg.put("2N", 32602);
        utmEpsg.put("3N", 32603);
        utmEpsg.put("4N", 32604);
        utmEpsg.put("5N", 32605);
        utmEpsg.put("6N", 32606);
        utmEpsg.put("7N", 32607);
        utmEpsg.put("8N", 32608);
        utmEpsg.put("9N", 32609);
        utmEpsg.put("10N", 32610);
        utmEpsg.put("11N", 32611);
        utmEpsg.put("12N", 32612);
        utmEpsg.put("13N", 32613);
        utmEpsg.put("14N", 32614);
        utmEpsg.put("15N", 32615);
        utmEpsg.put("16N", 32616);
        utmEpsg.put("17N", 32617);
        utmEpsg.put("18N", 32618);
        utmEpsg.put("19N", 32619);
        utmEpsg.put("20N", 32620);
        utmEpsg.put("21N", 32621);
        utmEpsg.put("22N", 32622);
        utmEpsg.put("23N", 32623);
        utmEpsg.put("24N", 32624);
        utmEpsg.put("25N", 32625);
        utmEpsg.put("26N", 32626);
        utmEpsg.put("27N", 32627);
        utmEpsg.put("28N", 32628);
        utmEpsg.put("29N", 32629);
        utmEpsg.put("30N", 32630);
        utmEpsg.put("31N", 32631);
        utmEpsg.put("32N", 32632);
        utmEpsg.put("33N", 32633);
        utmEpsg.put("34N", 32634);
        utmEpsg.put("35N", 4037);
        utmEpsg.put("36N", 4038);
        utmEpsg.put("37N", 32637);
        utmEpsg.put("38N", 32638);
        utmEpsg.put("39N", 32639);
        utmEpsg.put("40N", 32640);
        utmEpsg.put("41N", 32641);
        utmEpsg.put("42N", 32642);
        utmEpsg.put("43N", 32643);
        utmEpsg.put("44N", 32644);
        utmEpsg.put("45N", 32645);
        utmEpsg.put("46N", 32646);
        utmEpsg.put("47N", 32647);
        utmEpsg.put("48N", 32648);
        utmEpsg.put("49N", 32649);
        utmEpsg.put("50N", 32650);
        utmEpsg.put("51N", 32651);
        utmEpsg.put("52N", 32652);
        utmEpsg.put("53N", 32653);
        utmEpsg.put("54N", 32654);
        utmEpsg.put("55N", 32655);
        utmEpsg.put("56N", 32656);
        utmEpsg.put("57N", 32657);
        utmEpsg.put("58N", 32658);
        utmEpsg.put("59N", 32659);
        utmEpsg.put("60N", 32660);
    }

    /**
     * Minimum value for latitude
     */
    private static final int MIN_LATITUDE = -90;

    /**
     * Maximum value for latitude
     */
    private static final int MAX_LATITUDE = +90;

    /**
     * Minimum value for longitude
     */
    private static final int MIN_LONGITUDE = -180;

    /**
     * Maximum usual value for longitude
     */
    private static final int MAX_LONGITUDE = +180;

    /**
     * UTM north border
     */
    private static final int UTM_NORTH_MAX = 84;

    /**
     * UTM min latitude for Norway grid exception
     */
    private static final int NORWAY_MIN_LATITUDE = 56;

    /**
     * UTM max latitude for Norway grid exception
     */
    private static final int NORWAY_MAX_LATITUDE = 64;

    /**
     * UTM min latitude for Svalbard grid exception
     */
    private static final int SVALBARD_MIN_LATITUDE = 72;


    /**
     * Check if the latitude is valid (within the MIN and MAX latitude).
     *
     * @param latitude The latitude to check is valid.
     * @return True if the latitude is within the MIN and MAX latitude, false otherwise.
     */
    public static boolean isValidLatitude(float latitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
    }

    /**
     * Check if the longitude is valid (within the MIN and MAX longitude).
     *
     * @param longitude The longitude to check.
     * @return True if the longitude is between the MIN and MAX longitude.
     */
    public static boolean isValidLongitude(float longitude) {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    /**
     * Return the EPSG UTM code from the tuple latitude and longitude
     *
     * @param latitude  Latitude in the desired UTM
     * @param longitude Longitude in the desired UTM
     * @return EPSG code corresponding to the given latitude and longitude.
     */
    public static int getEPSGCode(float latitude, float longitude) {
        String[] utmInfo = getZoneHemisphere(latitude, longitude);
        return utmEpsg.get(utmInfo[0] + utmInfo[1]);
    }

    /**
     * Check if the  float value is between [min;max[.
     *
     * @param value Value to test.
     * @param minValue Included minimum valid value.
     * @param maxValue Excluded maximum valid value.
     * @return True if the value is in the range, false otherwise.
     */
    private static boolean isBetween(float value, int minValue, int maxValue) {
        return value >= minValue && value < maxValue;
    }

    /**
     * Return the zone number of grid plus its hemisphere (N for North, S for South) for a given latitude/longitude
     * position.
     *
     * @param latitude Latitude of the position.
     * @param longitude Longitude position.
     * @return String array with two values : the zone number as first one and the hemisphere as the second one.
     */
    @NotNull
    public static String[] getZoneHemisphere(float latitude, float longitude) {
        if (isValidLatitude(latitude) && isValidLongitude(longitude)) {
            int zone = (int) Math.floor(longitude / UTM_SPAN + UTM_ZONE_OFFSET);
            String hemisphere = latitude < 0 ? "S" : "N";
            // Workaround  for southwest coast of Norway and region around Svalbard
            switch (zone) {
                //Norway case
                case 31:
                    if (isBetween(latitude, NORWAY_MIN_LATITUDE, NORWAY_MAX_LATITUDE) && longitude >= 3) {
                        zone++;
                    }
                    break;
                //Svalbard case    
                case 32:
                    if (isBetween(latitude, SVALBARD_MIN_LATITUDE, UTM_NORTH_MAX)) {
                        if (longitude >= 9) {
                            zone++;
                        } else {
                            zone--;
                        }
                    }
                    break;
                //Svalbard case
                case 34:
                    if (isBetween(latitude, SVALBARD_MIN_LATITUDE, UTM_NORTH_MAX)) {
                        if (longitude >= 21) {
                            zone++;
                        } else {
                            zone--;
                        }
                    }
                    break;
                //Svalbard case    
                case 36:
                    if (isBetween(latitude, SVALBARD_MIN_LATITUDE, UTM_NORTH_MAX)) {
                        if (longitude >= 33) {
                            zone++;
                        } else {
                            zone--;
                        }
                    }
                    break;
                default:
                    break;
            }
            return new String[]{String.valueOf(zone), hemisphere};
        } else {
            throw new IllegalArgumentException("Please set valid latitude and longitude values");
        }
    }

    /**
     * Return the UTM proj String representation from the tuple latitude and longitude.
     * e.g. :
     * +proj=utm +zone=31 +datum=WGS84 +units=m +no_defs
     *
     * @param latitude  Latitude in the desired UTM.
     * @param longitude Longitude in the desired UTM.
     * @return The UTM proj String representation.
     */
    @NotNull
    public static String getProj(float latitude, float longitude) {
        String[] utmInfo = getZoneHemisphere(latitude, longitude);
        if (utmInfo[1].equals("S")) {
            return String.format("+proj=utm +zone=%s +south +datum=WGS84 +units=m +no_defs", utmInfo[0]);
        } else {
            return String.format("+proj=utm +zone=%s +datum=WGS84 +units=m +no_defs", utmInfo[0]);
        }
    }
}
