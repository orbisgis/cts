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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;

/**
 * Formatter to print angles as a degrees/minutes/seconds (DMS) and to parse DMS strings.
 * The format method uses pattern such as #D° MM' SS\" H(N|S) to define the string representation of an angle.
 *
 * @author Michaël Michaud
 *
 */
public final class AngleFormat {

    private static double DEG_TO_MIN = 60.0;
    private static double DEG_TO_SEC = 3600.0;

    /**
     * Regex capture groups for DMSH format pattern parts.
     */
    private static final int
            PREFIX = 1,
            DEGREE_FORMAT = 2, DEGREE_SUFFIX = 3,
            MINUTE_FORMAT = 4, MINUTE_SUFFIX = 5,
            SECOND_FORMAT = 6, SECOND_SUFFIX = 7,
            HEMISPHERE = 8, POSITIVE_SUFFIX = 9, NEGATIVE_SUFFIX = 10;

    /**
     * This is the pattern used to define a DMS angle pattern with the following capture groups :
     * - 1  : the prefix, {@link AngleFormat#PREFIX}
     * - 2  : the degree format, {@link AngleFormat#DEGREE_FORMAT}
     * - 3  : the degree suffix, {@link AngleFormat#DEGREE_SUFFIX}
     * - 4  : the minute format, {@link AngleFormat#MINUTE_FORMAT}
     * - 5  : the minute suffix, {@link AngleFormat#MINUTE_SUFFIX}
     * - 6  : the second format, {@link AngleFormat#SECOND_FORMAT}
     * - 7  : the second suffix, {@link AngleFormat#SECOND_SUFFIX}
     * - 8  : the hemisphere, {@link AngleFormat#HEMISPHERE}
     * - 9  : the positive suffix, {@link AngleFormat#POSITIVE_SUFFIX}
     * - 10 : the negative suffix, {@link AngleFormat#NEGATIVE_SUFFIX}
     */
    private static final Pattern DMSHFormatPattern = Pattern.compile(
            "(?:([^#DMS]*))" + // prefix part (group 1)
            "(?:(#?D+(?:\\.D+)?)([^#.MSH]*))" + // degrees part (groups 2, 3)
            "(?:(#?M+(?:\\.M+)?)([^#.DSH]*))?" + // minutes part (groups 4, 5)
            "(?:(#?S+(?:\\.S+)?)([^#.DMH]*))?" + // seconds part (groups 6, 7)
            "(H\\((\\w+)\\|(\\w+)\\))?" // hemisphere part (groups 8,9,10)
    );

    /**
     * Regex capture groups for DMSH format parts.
     */
    private static final int
            SIGNUM = 1,
            DEGREE_VALUE = 2, DEGREE_UNIT = 3,
            MINUTE_VALUE = 4, MINUTE_UNIT = 5,
            SECOND_VALUE = 6, SECOND_UNIT = 7;

    /**
     * This is the pattern used to parse a string representing a DMSH angle with the following capture groups :
     * - 1  : the signum, {@link AngleFormat#SIGNUM}
     * - 2  : the degree value, {@link AngleFormat#DEGREE_VALUE}
     * - 3  : the degree unit, {@link AngleFormat#DEGREE_UNIT}
     * - 4  : the minute value, {@link AngleFormat#MINUTE_VALUE}
     * - 5  : the minute unit, {@link AngleFormat#MINUTE_UNIT}
     * - 6  : the second value, {@link AngleFormat#SECOND_VALUE}
     * - 7  : the second unit, {@link AngleFormat#SECOND_UNIT}
     * - 8  : the hemisphere, {@link AngleFormat#HEMISPHERE}
     */
    private static final Pattern DMSHFormat = Pattern.compile(
            // prefix, non capturing, any character sequence but signum, dot or digit
            "[^\\d+\\-.]*" +
            // signum, capturing (eventually followed by some spaces)
            "([+\\-])?[\\s]*" +
            // degrees, capturing, MANDATORY ('0', '0.0' or '.0'),
            "([\\d]+(?:[.,]\\d*)?|[.,]\\d+)" +
            // units, capturing optional initial (d[egree]d[egré]g[rado]...)
            "[\\s]*(?:°|([dDgG])[a-zA-Z]*\\.?)?[\\s]*" +
            // minutes, capturing, optional
            "([\\d]+(?:[.,]\\d*)?)?" +
            // units, capturing optional initial (m[in.])
            "[\\s]*(?:'|([mM])[a-zA-Z]*\\.?)?[\\s]*" +
            // seconds, capturing, optional
            "([\\d]+(?:[.,]\\d*)?)?" +
            // units, capturing optional initial (m[in.])
            "[\\s]*(?:\"|([sS])[a-zA-Z]*\\.?)?[\\s]*" +
            // hemisphere, capturing, optional (caution, the s for south may have been captured by the second units
            // group
            "([NSEWOnsewo])?" +
            // any other character, non capturing
            ".*");

    /**
     * String prefix of the format.
     */
    private String prefix = null;
    /**
     * DecimalFormat of the degree part.
     */
    private DecimalFormat degree_format = new DecimalFormat("#0");
    /**
     * String suffix of the degree part.
     */
    private String degree_suffix = "° ";
    /**
     * DecimalFormat of the minute part.
     */
    private DecimalFormat minute_format = new DecimalFormat("00");
    /**
     * String suffix of the minute part.
     */
    private String minute_suffix = "' ";
    /**
     * DecimalFormat of the second part.
     */
    private DecimalFormat second_format = new DecimalFormat("00.000");
    /**
     * String suffix of the second part.
     */
    private String second_suffix = "\" ";
    /**
     * Indicates if the format has a hemisphere suffix.
     */
    private boolean suffix = true;
    /**
     * Positive hemisphere suffix.
     */
    private String positive_suffix = "N";
    /**
     * Negative hemisphere suffix.
     */
    private String negative_suffix = "S";

    /**
     * Longitude angle format with the syntax : #D° MM' SS.SSSSS" H(E|W)
     */
    public static final AngleFormat LONGITUDE_FORMATTER = new AngleFormat("#D° MM' SS.SSSSS\" H(E|W)");

    /**
     * Latitude angle format with the syntax : #D° MM' SS.SSSSS" H(N|S)
     */
    public static final AngleFormat LATITUDE_FORMATTER = new AngleFormat("#D° MM' SS.SSSSS\" H(N|S)");

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
        return angle * DEG_TO_MIN;
    }

    public static double deg2sec(double angle) {
        return angle * DEG_TO_SEC;
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
        return sign * (dd + mm / DEG_TO_MIN + ss / DEG_TO_SEC);
    }

    public static double dd2dms(double ddAngle) {
        double sign = signum(ddAngle);
        double dd = floor(abs(ddAngle));
        double mm = floor(DEG_TO_MIN * (abs(ddAngle) - dd));
        double ss = DEG_TO_SEC * (abs(ddAngle) - dd - mm / DEG_TO_MIN);
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
     *                <ul>
     *                <li>#D means degrees</li>
     *                <li>DD means degrees (two digits mandatory)</li>
     *                <li>#D.DD means degrees (two fractional digits)</li>
     *                <li>idem for minutes (M) and seconds (S)</li>
     *                <li>H(N|S) means use suffix 'N' for positive angles and 'S' for negative
     *                angles instead of +/- sign</li>
     *                <li>no H(XXX|YYY) means that +/- sign will be used</li>
     *                <li>a prefix may be used before the first # or D</li>
     *                <li>any character different from DMSH may be used to separate D, M and
     *                H</li>
     *                </ul>
     *                <p>Exemples :</p>
     *                <ul>
     *                <li>latitude = #D° MM' SS.SSS\" H(N|S) --> latitude = 45° 09'
     *                56.897" S</li>
     *                <li>#D deg #M min --> -4 deg 6 min</li>
     *                </ul>
     */
    public AngleFormat(String pattern) throws IllegalArgumentException {
        Matcher matcher = DMSHFormatPattern.matcher(pattern);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(pattern + " is an illegal pattern for an AngleFormat");
        }

        prefix = matcher.group(PREFIX);

        //The degree part is mandatory in the regex DMSHFormatPattern so there is always a degree_format and degree_suffix
        degree_format = new DecimalFormat(matcher.group(DEGREE_FORMAT).replaceAll("D", "0"));
        degree_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        degree_suffix = matcher.group(DEGREE_SUFFIX);

        if (null == matcher.group(MINUTE_FORMAT)) {
            minute_format = null;
        } else {
            minute_format = new DecimalFormat(matcher.group(MINUTE_FORMAT).replaceAll("M", "0"));
            minute_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        }
        if (null == matcher.group(MINUTE_SUFFIX)) {
            minute_suffix = "";
        } else {
            minute_suffix = matcher.group(MINUTE_SUFFIX);
        }

        if (null == matcher.group(SECOND_FORMAT)) {
            second_format = null;
        } else {
            second_format = new DecimalFormat(matcher.group(SECOND_FORMAT).replaceAll("S", "0"));
            second_format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        }
        if (null == matcher.group(SECOND_SUFFIX)) {
            second_suffix = "";
        } else {
            second_suffix = matcher.group(SECOND_SUFFIX);
        }

        if (null != matcher.group(HEMISPHERE)) {
            positive_suffix = matcher.group(POSITIVE_SUFFIX);
            negative_suffix = matcher.group(NEGATIVE_SUFFIX);
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
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        if (!suffix) {
            sb.append(angle < 0 ? "-" : "+");
        }

        // minute_format == null --> only degrees are represented
        if (minute_format == null) {
            sb.append(degree_format.format(absangle));
        } else {
            sb.append(degree_format.format(Math.floor(absangle)));
        }
        sb.append(degree_suffix);
        if (degree_suffix.length() == 0) {
            sb.append(" ");
        }

        if (minute_format != null) {
            // second_format == null --> only degrees / minutes are represented
            double minutes = deg2min(absangle) % DEG_TO_MIN;
            if (second_format == null) {
                sb.append(minute_format.format(minutes));
            } else {
                sb.append(minute_format.format(Math.floor(minutes)));
            }
            sb.append(minute_suffix);
            if (minute_suffix.length() == 0) {
                sb.append(" ");
            }

            if (second_format != null) {
                double seconds = deg2sec(absangle) % DEG_TO_MIN;
                sb.append(second_format.format(seconds)).append(second_suffix);
            }
        }

        if (suffix) {
            if (angle >= 0) {
                sb.append(positive_suffix);
            } else {
                sb.append(negative_suffix);
            }
        }

        return sb.toString();
    }

    /**
     * Parse a string representing an angle written in DMSH (degrees / minutes / seconds / hemisphere). The parser is
     * very flexible and can analyze strings as :
     * - 2
     * - .2°
     * - -.2°
     * - 0.2° S
     * - 2° 2'
     * - 2° 2' 2" W
     * - 2° 02' 02" N
     * - l=-2°2'2.222"
     *
     * @param angle the string to parse
     * @return the angle in degrees
     */
    public static double parseAngle(String angle) throws IllegalArgumentException {
        Matcher m = DMSHFormat.matcher(angle);
        if (m.matches()) {
            double degrees = Double.parseDouble(m.group(DEGREE_VALUE));
            String deg = m.group(DEGREE_UNIT);
            double minutes = m.group(MINUTE_VALUE) == null ? 0.0 : Double.parseDouble(m.group(MINUTE_VALUE));
            String min = m.group(MINUTE_UNIT);
            double seconds = m.group(SECOND_VALUE) == null ? 0.0 : Double.parseDouble(m.group(SECOND_VALUE));
            String sec = m.group(SECOND_UNIT);
            double a = degrees + minutes / DEG_TO_MIN + seconds / DEG_TO_SEC;

            String signum = m.group(SIGNUM);
            // If there is a - signum, the hemisphere (north, south, east, west) is ignored
            if ("-".equals(signum)) {
                return -a;
            }
            // Hemisphere
            String h = m.group(HEMISPHERE);
            if (h != null && h.matches("[SsOoWw].*")) {
                return -a;
            } // if the sec unit group is not null but the degree unit group is null and the min unit group is null,
            // then the s of the second unit group means south.
            else if (sec != null && deg == null && min == null) {
                // last s means south and not second
                return -a;
            } else {
                return a;
            }
        } else {
            throw new IllegalArgumentException(angle + " is not a recognized angle value");
        }
    }

    /**
     * This method parse a string which represent an angle in radians, in grades or in degrees.
     * The parser try to recognize a symbol to determine the units used in the string and convert the angle into
     * radians.
     */
    public static double parseAndConvert2Radians(String angle) throws IllegalArgumentException {
        // If angle contains no unit, the angle is in radians
        if (angle.matches("[+\\-]?([0-9]+([.,][0-9]+)|[.][0-9]+)")) {
            angle = angle.replaceAll(",", ".");
            return Double.parseDouble(angle);
        } // If angle ends with g, gr, grad or grades, the angle is converted from grades to radians
        else if (angle.matches("(?i).*(g|g\\.|gr|gr\\.|grad|grad\\.|grades)\\z")) {
            angle = angle.replaceAll("(?i)[\\s]*(g|g\\.|gr|gr\\.|grad|grad\\.|grades)\\z", "");
            angle = angle.replaceAll(",", ".");
            return gra2rad(Double.parseDouble(angle));
        } // Else if, the angle is considered as a degree/minutes/seconds angle, and the parseAngle method is used
        else {
            double d = parseAngle(angle);
            return deg2rad(d);
        }
    }
}
