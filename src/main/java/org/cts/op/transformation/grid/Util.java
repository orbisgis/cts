/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
