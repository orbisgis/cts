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
package org.cts;

import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author Erwan Bocher
 */
public class CTSTestCase {

    protected Logger LOGGER = Logger.getLogger(CTSTestCase.class);

    public boolean checkEquals(String test, double o1, double o2, double tol) {
        if (Math.abs(o1 - o2) <= tol) {
            System.out.println("TRUE : " + test + " " + o1 + " = " + o2 + " <= " + tol);
            return true;
        } else {
            System.out.println("FALSE : " + test + " " + Math.abs(o1 - o2) + " > " + tol);
            return false;
        }
    }

    /**
     * Check if the result point is equal to the target point using an epsilon
     * clause
     *
     * @param resultPoint
     * @param targetPoint
     * @param epsilon
     * @return
     */
    protected boolean checkEquals(String test, double[] c1, double[] c2,
            double tol) {
        double dd = 0;
        for (int i = 0; i < Math.min(c1.length, c2.length); i++) {
            dd += (c2[i] - c1[i]) * (c2[i] - c1[i]);
        }
        if (dd < tol * tol) {
            System.out.println("TRUE : From " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " < " + tol);
            return true;
        } else {
            System.out.println("FALSE : From " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + Math.sqrt(dd));
            return false;
        }
    }

    /**
     * Check if the result point is equal in X and Y to the target point using
     * an epsilon clause
     *
     * @param test
     * @param c1
     * @param c2
     * @param tolerance
     * @return
     */
    protected boolean checkEquals2D(String test, double[] c1, double[] c2,
            double tolerance) {
        double dx = Math.abs(c1[0] - c2[0]);
        double dy = Math.abs(c1[1] - c2[1]);
        double delta = Math.max(dx, dy);
        boolean isInTol = delta <= tolerance;
        if (isInTol) {
            System.out.println("TRUE : From " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " <= " + tolerance);
            return true;
        } else {
            System.out.println("FALSE : From " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + (tolerance - delta));
            return false;
        }
    }

    /**
     * Display point values
     *
     * @param coord
     * @return
     */
    protected String coord2string(double[] coord) {
        if (coord.length == 2) {
            return ("P = {" + coord[0] + ", " + coord[1] + "}");
        } else if (coord.length == 3) {
            return ("P = {" + coord[0] + ", " + coord[1] + ", " + coord[2] + "}");
        } else {
            return ("Error : " + coord + " size = " + coord.length);
        }
    }
}
