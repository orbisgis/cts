/* 
 Copyright 2012 Antoine Gourlay

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.cts.parser.prj;

/**
 *
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public final class PrjWriter {

    /**
     * Return a String representing the number round to the given tolerance. If
     * the input double is equal to an integer using the given tolerance, this
     * method return a number without the ".0".
     *
     * @param number the double to transform
     * @param tol the tolerance
     */
    public static String roundToString(double number, double tol) {
        StringBuilder w = new StringBuilder();
        if (isInteger(number, tol)) {
            w.append(Math.round(number));
        } else {
            double res = 1/tol;
            w.append(Math.rint(number * res) / res);
        }
        return w.toString();
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
