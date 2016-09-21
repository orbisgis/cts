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
package org.cts.op.transformation.grid;

public class Util {

    public static int getIntLE(byte[] b, int i) {
        return b[(i++)] & 0xFF | b[(i++)] << 8 & 0xFF00 | b[(i++)] << 16 & 0xFF0000 | b[i] << 24;
    }

    public static int getIntBE(byte[] b, int i) {
        return b[(i++)] << 24 | b[(i++)] << 16 & 0xFF0000 | b[(i++)] << 8 & 0xFF00 | b[i] & 0xFF;
    }

    public static int getInt(byte[] b, boolean bigEndian) {
        if (bigEndian) {
            return getIntBE(b, 0);
        }
        return getIntLE(b, 0);
    }

    public static float getFloat(byte[] b, boolean bigEndian) {
        int i;
        if (bigEndian) {
            i = getIntBE(b, 0);
        } else {
            i = getIntLE(b, 0);
        }
        return Float.intBitsToFloat(i);
    }

    public static double getDouble(byte[] b, boolean bigEndian) {
        int i;
        int j;
        if (bigEndian) {
            i = getIntBE(b, 0);
            j = getIntBE(b, 4);
        } else {
            i = getIntLE(b, 4);
            j = getIntLE(b, 0);
        }
        long l = ((long) i) << 32 | j & 0xFFFFFFFF;

        return Double.longBitsToDouble(l);
    }

    public static boolean isNioAvailable() {
        boolean nioAvailable = false;
        try {
            Class.forName("java.nio.channels.FileChannel");
            nioAvailable = true;
        } catch (ClassNotFoundException cnfe) {
        }
        return nioAvailable;
    }
}
