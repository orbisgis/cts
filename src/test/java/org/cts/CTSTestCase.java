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

package org.cts;

import java.util.Arrays;

import org.cts.registry.*;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A main class for all CTS tests
 *
 * @author Erwan Bocher, Michaël Michaud
 */
public class CTSTestCase {

    protected Logger LOGGER = LoggerFactory.getLogger(CTSTestCase.class);
    protected static CRSFactory cRSFactory;

    /**
     * This method is used to create the CRSFactory and load some default
     * registries.
     */
    @BeforeClass
    public static void setup() {
        cRSFactory = new CRSFactory();
        RegistryManager registryManager = cRSFactory.getRegistryManager();
        registryManager.addRegistry(new IGNFRegistry());
        registryManager.addRegistry(new EPSGRegistry());
        registryManager.addRegistry(new ESRIRegistry());
        registryManager.addRegistry(new Nad27Registry());
        registryManager.addRegistry(new Nad83Registry());
        registryManager.addRegistry(new WorldRegistry());

    }

    /**
     * Check if the result point is equal to the target point using an epsilon
     * clause
     *
     * @param test
     * @param o1 expected coordinates
     * @param o2 actual coordinates
     * @param tol tolerance
     */
    public boolean checkEquals(String test, double o1, double o2, double tol) {
        if (Math.abs(o1 - o2) <= tol) {
            LOGGER.debug("TRUE : " + test + " " + o1 + " = " + o2 + " <= " + tol);
            return true;
        } else {
            LOGGER.debug("FALSE : " + test + " " + Math.abs(o1 - o2) + " > " + tol);
            return false;
        }
    }

    /**
     * Check if the result point is equal to the target point using an epsilon
     * clause
     *
     * @param c1 expected coordinates
     * @param c2 actual coordinates
     * @param tol tolerance
     */
    protected boolean checkEquals(String test, double[] c1, double[] c2, double tol) {
        double dd = 0;
        for (int i = 0; i < Math.min(c1.length, c2.length); i++) {
            dd += (c2[i] - c1[i]) * (c2[i] - c1[i]);
        }
        if (dd < tol * tol) {
            LOGGER.debug("TRUE : From " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " < " + tol);
            return true;
        } else {
            LOGGER.debug("FALSE : From " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + Math.sqrt(dd));
            return false;
        }
    }

    /**
     * Check if the result point is equal in X and Y to the target point using
     * an epsilon clause
     *
     * @param test
     * @param c1 expected coordinates
     * @param c2 actual coordinates
     * @param tolerance
     */
    protected boolean checkEquals2D(String test, double[] c1, double[] c2, double tolerance) {
        double dx = Math.abs(c1[0] - c2[0]);
        double dy = Math.abs(c1[1] - c2[1]);
        double delta = Math.max(dx, dy);
        boolean isInTol = delta <= tolerance;
        if (isInTol) {
            LOGGER.debug("TRUE : " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " <= " + tolerance);
            return true;
        } else {
            LOGGER.debug("FALSE : " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + (tolerance - delta));
            return false;
        }
    }

    /**
     * Check if the result point is equal in X, Y and Z to the target point
     * using an epsilon clause
     *
     * @param test
     * @param c1 expected coordinates
     * @param c2 actual coordinates
     * @param tolerance
     */
    protected boolean checkEquals3D(String test, double[] c1, double[] c2, double tolerance, double toleranceZ) {
        double dx = Math.abs(c1[0] - c2[0]);
        double dy = Math.abs(c1[1] - c2[1]);
        double dz = Math.abs(c1[2] - c2[2]);
        if (dx <= tolerance && dy <= tolerance && dz <= toleranceZ) {
            LOGGER.debug("TRUE : " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " <= " + tolerance);
            return true;
        } else if (dx > tolerance || dy > tolerance) {
            LOGGER.debug("FALSE : " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + (tolerance - Math.max(dx, dy)));
            return false;
        } else {
            LOGGER.debug("FALSE : " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + (toleranceZ - dz));
            return false;
        }
    }

    /**
     * Check if the result point is equal in X, Y and Z to the target point
     * using an epsilon clause
     *
     * @param test
     * @param c1 expected coordinates
     * @param c2 actual coordinates
     * @param tolerance
     * @return 
     */
    protected boolean checkEquals3D(String test, double[] c1, double[] c2, double tolerance) {
        double dx = Math.abs(c1[0] - c2[0]);
        double dy = Math.abs(c1[1] - c2[1]);
        double dz = Math.abs(c1[2] - c2[2]);
        boolean isInTol = dx <= tolerance && dy <= tolerance && dz <= tolerance;
        if (isInTol) {
            LOGGER.debug("TRUE : " + test + " Result point : " + Arrays.toString(c1) + " = expected point : "
                    + Arrays.toString(c2) + " <= " + tolerance);
            return true;
        } else {
            LOGGER.debug("FALSE : " + test + " Result point : " + Arrays.toString(c1) + " compare to expected point : "
                    + Arrays.toString(c2) + " = " + (tolerance - Math.max(Math.max(dx, dy), dz)));
            return false;
        }
    }

    /**
     * Display point values.
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
