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

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.signum;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Formatter to print angles as a degrees/minutes/seconds (DMS) and to parse
 * DMS strings</p>
 * <p>The format method uses pattern such as #D� MM' SS\" H(N|S) to define the
 * string representation of an angle.</p>
 *
 * @author Michaël Michaud<br>
 * <p>
 */
public final class AngleFormat {

    /**
     * <p>This is the pattern used to define a DMS angle pattern.</p>
     */
    private static final Pattern DMSHFormatPattern = Pattern.compile(
            "([^#DMS]*)" + // prefix part (group 1)
            "(?:(#?D+(?:\\.D+)?)([^#\\.MSH]*))" + // degrees part (groups 2, 3)
            "(?:(#?M+(?:\\.M+)?)([^#\\.DSH]*))?" + // minutes part (groups 4, 5)
            "(?:(#?S+(?:\\.S+)?)([^#\\.DMH]*))?" + // seconds part (groups 6, 7)
            "(H\\((\\w+)\\|(\\w+)\\))?" // hemisphere part (groups 8,9,10)
            );
    /**
     * <p>This is the pattern used to parse a string representing a DMSH
     * angle.</p>
     */
    private static final Pattern DMSHFormat = Pattern.compile(
            // prefix, non capturing, any character sequence but signum, dot or digit
            "[^\\d\\+\\-\\.]*"
            + // signum, capturing (eventually followed by some spaces)
            "([\\+\\-])?[\\s]*"
            + // degrees, capturing, MANDATORY ('0', '0.0' or '.0'),
            "([\\d]+(?:[\\.,]\\d*)?|[\\.,]\\d+)"
            + // units, capturing optional initial (d[egree]d[egré]g[rado]...)
            "[\\s]*(?:\u00B0|([dDgG])[a-zA-Z]*\\.?)?[\\s]*"
            + // minutes, capturing, optional
            "([\\d]+(?:[\\.,]\\d*)?)?"
            + // units, capturing optional initial (m[in.])
            "[\\s]*(?:'|([mM])[a-zA-Z]*\\.?)?[\\s]*"
            + // minutes, capturing, optional
            "([\\d]+(?:[\\.,]\\d*)?)?"
            + // units, capturing optional initial (m[in.])
            "[\\s]*(?:\"|([sS])[a-zA-Z]*\\.?)?[\\s]*"
            + // hemisphere, capturing, optional (caution, the s for south may have
            // been captured by the second units group
            "([NSEWOnsewo])?"
            + // any other character, non capturing
            ".*");
    public static final AngleFormat LONGITUDE_FORMATTER = new AngleFormat("#D° MM' SS.SSSSS\" H(E|W)");
    public static final AngleFormat LATITUDE_FORMATTER = new AngleFormat("#D° MM' SS.SSSSS\" H(N|S)");
    private String prefix = "";
    private DecimalFormat degree_format = new DecimalFormat("#0");
    private String degree_suffix = "° ";
    private DecimalFormat minute_format = new DecimalFormat("00");
    private String minute_suffix = "' ";
    private DecimalFormat second_format = new DecimalFormat("00.000");
    private String second_suffix = "\" ";
    private boolean suffix = true;
    private String positive_suffix = "N";
    private String negative_suffix = "S";

    public static double rad2deg(double angle) {
        return angle * 180.0 / PI;
    }

    public static double rad2gra(double angle) {
        return angle * 200.0 / PI;
    }

    public static double rad2min(double angle) {
        return angle * 10800.0 / PI;
    }

    public static double rad2sec(double angle) {
        return angle * 648000.0 / PI;
    }

    public static double deg2rad(double angle) {
        return angle * PI / 180.0;
    }

    public static double deg2gra(double angle) {
        return angle * 200.0 / 180.0;
    }

    public static double deg2min(double angle) {
        return angle * 60.0;
    }

    public static double deg2sec(double angle) {
        return angle * 3600.0;
    }

    public static double gra2rad(double angle) {
        return angle * PI / 200.0;
    }

    public static double gra2deg(double angle) {
        return angle * 180.0 / 200.0;
    }

    public static double dms2dd(double dmsAngle) {
        double sign = signum(dmsAngle);
        double dd = floor(abs(dmsAngle));
        double mm = floor(100.0 * (abs(dmsAngle) - dd));
        double ss = 10000.0 * (abs(dmsAngle) - dd - mm / 100.0);
        return sign * (dd + mm / 60.0 + ss / 3600.0);
    }

    public static double dd2dms(double ddAngle) {
        double sign = signum(ddAngle);
        double dd = floor(abs(ddAngle));
        double mm = floor(60.0 * (abs(ddAngle) - dd));
        double ss = 3600.0 * (abs(ddAngle) - dd - mm / 60.0);
        return sign * (dd + mm / 100.0 + ss / 10000.0);
    }

    public AngleFormat() {
        degree_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        minute_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        second_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
    }

    /**
     * Create a new angle formatter able to write an angle as a DMS string
     * following a specific pattern.
     *
     * @param pattern the Pattern to format angles
     * <ul>
     * <li>#D means degrees</li>
     * <li>DD means degrees (two digits mandatory)</li>
     * <li>#D.DD means degrees (two fractional digits)</li>
     * <li>idem for minutes (M) and seconds (S)</li>
     * <li>H(N|S) means use suffix 'N' for positive angles and 'S' for negative
     * angles instead of +/- sign</li>
     * <li>no H(XXX|YYY) means that +/- sign will be used</li>
     * <li>a prefix may be used before the first # or D</li>
     * <li>any character different from DMSH may be used to separate D, M and
     * H</li>
     * </ul>
     * <p>Exemples :</p>
     * <ul>
     * <li>latitude = #D\u00B0 MM' SS.SSS\" H(N|S) --> latitude = 45\u00B0 09'
     * 56.897" S</li>
     * <li>#D deg #M min --> -4 deg 6 min</li>
     * </ul>
     */
    public AngleFormat(String pattern) throws IllegalArgumentException {
        Matcher matcher = DMSHFormatPattern.matcher(pattern);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(pattern + " is an illegal pattern for an AngleFormat");
        }

        prefix = matcher.group(1);

        if (null == matcher.group(2)) {
            degree_format = null;
        } else {
            degree_format = new DecimalFormat(matcher.group(2).replaceAll("D", "0"));
            degree_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        }
        if (null == matcher.group(3)) {
            degree_suffix = "";
        } else {
            degree_suffix = matcher.group(3);
        }

        if (null == matcher.group(4)) {
            minute_format = null;
        } else {
            minute_format = new DecimalFormat(matcher.group(4).replaceAll("M", "0"));
            minute_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        }
        if (null == matcher.group(5)) {
            minute_suffix = "";
        } else {
            minute_suffix = matcher.group(5);
        }

        if (null == matcher.group(6)) {
            second_format = null;
        } else {
            second_format = new DecimalFormat(matcher.group(6).replaceAll("S", "0"));
            second_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        }
        if (null == matcher.group(7)) {
            second_suffix = "";
        } else {
            second_suffix = matcher.group(7);
        }

        if (null != matcher.group(8)) {
            suffix = true;
            positive_suffix = matcher.group(9);
            negative_suffix = matcher.group(10);
        } else {
            suffix = false;
        }
    }

    /**
     * Format an angle following the special pattern defined by this object.
     *
     * @param angle angle to format (the angle must be in degrees).
     */
    public String format(double angle) {
        double absangle = Math.abs(angle);
        double signum = Math.signum(angle);
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        if (!suffix) {
            sb.append(angle < 0 ? "-" : "+");
        }

        // minute_format == null --> only degrees are represented
        if (minute_format == null) {
            sb.append(degree_format.format(absangle)).append(degree_suffix);
        } else {
            sb.append(degree_format.format(Math.floor(absangle))).append(degree_suffix);
        }
        if (degree_suffix.length() == 0) {
            sb.append(" ");
        }

        if (minute_format != null) {
            // second_format == null --> only degrees / minutes are reprsented
            double minutes = deg2min(absangle) % 60;
            if (second_format == null) {
                sb.append(minute_format.format(minutes)).append(minute_suffix);
            } else {
                sb.append(minute_format.format(Math.floor(minutes))).append(minute_suffix);
            }
            if (minute_suffix.length() == 0) {
                sb.append(" ");
            }

            if (second_format != null) {
                double seconds = deg2sec(absangle) % 60;
                sb.append(second_format.format(seconds)).append(second_suffix);
            }
        }

        if (suffix && angle >= 0) {
            sb.append(positive_suffix);
        } else if (suffix && angle < 0) {
            sb.append(negative_suffix);
        }

        return sb.toString();
    }

    /**
     * Parse a string representing an angle written in DMSH (degrees / minutes /
     * seconds / hemisphere). The parser is very flexible and can analyze
     * strings as
     * <ul>
     * <li>2</li>
     * <li>.2\u00B0</li>
     * <li>-.2\u00B0</li>
     * <li>0.2\u00B0 S</li>
     * <li>2\u00B0 2'</li>
     * <li>2\u00B0 2' 2" W</li>
     * <li>2\u00B0 02' 02" N</li>
     * <li>l=-2\u00B02'2.222"</li>
     * </ul>
     *
     * @param angle the string to parse
     * @return the angle in degrees
     */
    public static double parseAngle(String angle) throws IllegalArgumentException {
        Matcher m = DMSHFormat.matcher(angle);
        if (m.matches()) {
            try {
                double degrees = Double.parseDouble(m.group(2));
                String deg = m.group(3);
                double minutes = m.group(4) == null ? 0.0 : Double.parseDouble(m.group(4));
                String min = m.group(5);
                double seconds = m.group(6) == null ? 0.0 : Double.parseDouble(m.group(6));
                String sec = m.group(7);
                double a = degrees + minutes / 60.0 + seconds / 3600.0;

                String signum = m.group(1);
                // If there is a - signum, the hemisphere (north, south, east,
                // west) is ignored
                if (null != signum && signum.equals("-")) {
                    return -a;
                }
                // Hemisphere
                String h = m.group(8);
                if (null != h && h.matches("[SsOoWw].*")) {
                    return -a;
                } // if the sec unit group is not null but the degree unit group
                // is null and the min unit group is null,
                // then the s of the second unit group means south.
                else if (null != sec && deg == null && min == null) {
                    // last s means south and not second
                    return -a;
                } else {
                    return a;
                }
            } catch (NumberFormatException e) {
                throw e;
            }
        } else {
            throw new IllegalArgumentException(angle + " is not a recognized angle value");
        }
    }

    /**
     * <p>This method parse a string which represent an angle in radians, in
     * grades or in degrees.</p>
     * <p>The parser try to recognize a symbol to determine the units used in
     * the string and convert the angle into radians.</p>
     */
    public static double parseAndConvert2Radians(String angle) throws IllegalArgumentException {
        // If angle contains no unit, the angle is in radians
        if (angle.matches("[\\+\\-]?([0-9]+([\\.,][0-9]+)|[\\.][0-9]+)")) {
            angle = angle.replaceAll(",", ".");
            return Double.parseDouble(angle);
        } // If angle ends with g, gr, grad or grades, the angle is converted from
        // grades to radians
        else if (angle.matches("(?i).*(g|g\\.|gr|gr\\.|grad|grad\\.|grades)\\z")) {
            angle = angle.replaceAll("(?i)[\\s]*(g|g\\.|gr|gr\\.|grad|grad\\.|grades)\\z", "");
            angle = angle.replaceAll(",", ".");
            return Double.parseDouble(angle) * Math.PI / 200;
        } // Else if, the angle is considered as a degree/minutes/seconds angle,
        // and the parseAngle method is used
        else {
            double d = parseAngle(angle);
            return d * Math.PI / 180;
        }
    }
}
