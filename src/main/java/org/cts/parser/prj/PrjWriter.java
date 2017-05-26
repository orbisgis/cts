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
package org.cts.parser.prj;

import java.util.Locale;

/**
 *
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public final class PrjWriter {

    private static final double[] currentRatios = new double[]{
            1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0,
            Math.PI/180.0, 180.0/Math.PI,     // ratio used during conversions between radians and degrees
            2*Math.PI/180.0, 2*180.0/Math.PI, // ratio used during conversions between radians and degrees
            3*Math.PI/180.0, 3*180.0/Math.PI, // ratio used during conversions between radians and degrees
            4*Math.PI/180.0, 4*180.0/Math.PI, // ratio used during conversions between radians and degrees
            0.9, 1.0/0.9,            // ratio used during conversions between degrees and grades
            0.3048, 1.0/0.3048,      // ratio used during conversions between meters and feet
            1200d/3937d, 3937d/1200d // ratio used during conversions between meters and feet
    };

    /**
     * Return a String representing the number round to the given tolerance. If
     * the input double is equal to an integer using the given tolerance, this
     * method return a number without the ".0".
     *
     * @param number the double to transform
     * @param tol the tolerance
     * @return
     * @deprecated this method is repaced by prettyRound, more precise
     */
    @Deprecated
    public static String roundToString(double number, double tol) {
        StringBuilder w = new StringBuilder();
        if (isInteger(number, tol)) {
            w.append(Math.round(number));
        } else {
            double res = 1 / tol;
            w.append(Math.rint(number * res) / res);
        }
        return w.toString();
    }

    /**
     * Returns a String representing the number. If the number represents an integer
     * or a simple fraction, the number will try to retrieve the full double precision
     * number even if last decimals are missing or erroneous.
     * For integers, it will omit the decimal part.
     *
     * @param number the double to transform
     * @return
     */
    public static String prettyRound(double number, double tol) {
        for (double f : currentRatios) {
            if (Math.abs(number*f-Math.round(number*f)) < tol) {
                if (f == 1.0 || f == 10.0) return Integer.toString((int)(Math.round(number*f)/f));
                else return String
                        .format(Locale.ENGLISH, "%.32f", Math.round(number*f)/f)
                        .replaceAll("(.)0*$","$1");
            }
        }
        return String
                .format(Locale.ENGLISH,"%.32f", number)
                .replaceAll("0*$","");
    }

    /**
     * Returns whether the double is equals to its nearest integer using the
     * tolerance given in parameter.
     *
     * @param a the double to test
     * @param tol the tolerance of the equality
     */
    private static boolean isInteger(double a, double tol) {
        return (Math.abs(a - Math.rint(a)) < tol);
    }

    /**
     * Returns the WKT in parameter into a Human-Readable OGC WKT form.
     *
     * @param wkt the OGC WKT String to transform.
     * @return 
     */
    public static String formatWKT(String wkt) {
        StringBuilder w = new StringBuilder();
        int n = 0;
        int index;
        int ind;
        String begin;
        String end;
        boolean dontAddAlinea = false;
        String[] wktexp = wkt.split("]],");
        for (int i = 0; i < wktexp.length; i++) {
            index = wktexp[i].indexOf("[");
            begin = wktexp[i].substring(0, index + 1);
            w.append(begin);
            end = wktexp[i].substring(index + 1);
            ind = end.indexOf(",");
            while (ind != -1) {
                begin = end.substring(0, ind + 1);
                index = end.indexOf("[");
                end = end.substring(ind + 1);
                if (dontAddAlinea) {
                    w.append("\n").append(indent(n)).append(begin);
                } else if (ind < index || index == -1) {
                    w.append(begin);
                } else {
                    n++;
                    w.append("\n").append(indent(n)).append(begin);
                }
                dontAddAlinea = begin.substring(begin.length() - 2).equals("],");
                ind = end.indexOf(",");
            }
            n = checkIndent(end, n);
            w.append(end);
            if (i != wktexp.length - 1) {
                n--;
                w.append("]],\n").append(indent(n));
            }
        }
        return w.toString();
    }

    /**
     * Return a String constituted by {@code n} indent. One indent = four space.
     *
     * @param n the number of indent wanted
     */
    private static String indent(int n) {
        StringBuilder w = new StringBuilder();
        for (int i = 0; i < n; i++) {
            w = w.append("    ");
        }
        return w.toString();
    }

    /**
     * Decrese the number of required indents, depending on the number of node
     * closed at the end of line.
     *
     * @param end the end of a node
     * @param n the current number of indent
     */
    private static int checkIndent(String end, int n) {
        int k = end.length() - 1;
        while (end.substring(k, k + 1).equals("]")) {
            n--;
            k--;
        }
        return n;
    }

    /**
     * Create a new PrjWriter.
     */
    private PrjWriter() {
    }
}
