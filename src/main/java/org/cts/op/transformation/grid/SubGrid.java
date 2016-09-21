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

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class SubGrid implements Cloneable, Serializable {

    private static final int REC_SIZE = 16;
    private String subGridName;
    private String parentSubGridName;
    private String created;
    private String updated;
    private double minLat;
    private double maxLat;
    private double minLon;
    private double maxLon;
    private double latInterval;
    private double lonInterval;
    private int nodeCount;
    private int lonColumnCount;
    private int latRowCount;
    private float[] latShift;
    private float[] lonShift;
    private float[] latAccuracy;
    private float[] lonAccuracy;
    private final RandomAccessFile raf;
    private long subGridOffset;
    boolean bigEndian;
    private SubGrid[] subGrid;

    public SubGrid(InputStream in, boolean bigEndian, boolean loadAccuracy)
            throws IOException {
        this.raf = null;
        byte[] b8 = new byte[8];
        byte[] b4 = new byte[4];
        in.read(b8);
        in.read(b8);
        this.subGridName = new String(b8).trim();
        in.read(b8);
        in.read(b8);
        this.parentSubGridName = new String(b8).trim();
        in.read(b8);
        in.read(b8);
        this.created = new String(b8);
        in.read(b8);
        in.read(b8);
        this.updated = new String(b8);
        in.read(b8);
        in.read(b8);
        this.minLat = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.maxLat = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.minLon = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.maxLon = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.latInterval = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.lonInterval = Util.getDouble(b8, bigEndian);
        this.lonColumnCount = (1 + (int) ((this.maxLon - this.minLon) / this.lonInterval));
        this.latRowCount = (1 + (int) ((this.maxLat - this.minLat) / this.latInterval));
        in.read(b8);
        in.read(b8);
        this.nodeCount = Util.getInt(b8, bigEndian);
        if (this.nodeCount != this.lonColumnCount * this.latRowCount) {
            throw new IllegalStateException("SubGrid " + this.subGridName + " has inconsistent grid dimesions");
        }
        this.latShift = new float[this.nodeCount];
        this.lonShift = new float[this.nodeCount];
        if (loadAccuracy) {
            this.latAccuracy = new float[this.nodeCount];
            this.lonAccuracy = new float[this.nodeCount];
        }

        for (int i = 0; i < this.nodeCount; i++) {
            in.read(b4);
            this.latShift[i] = Util.getFloat(b4, bigEndian);
            in.read(b4);
            this.lonShift[i] = Util.getFloat(b4, bigEndian);
            in.read(b4);
            if (loadAccuracy) {
                this.latAccuracy[i] = Util.getFloat(b4, bigEndian);
            }
            in.read(b4);
            if (loadAccuracy) {
                this.lonAccuracy[i] = Util.getFloat(b4, bigEndian);
            }
        }
    }

    public SubGrid(RandomAccessFile raf, long subGridOffset, boolean bigEndian)
            throws IOException {
        this.raf = raf;
        this.subGridOffset = subGridOffset;
        this.bigEndian = bigEndian;
        raf.seek(subGridOffset);
        byte[] b8 = new byte[8];
        raf.read(b8);
        raf.read(b8);
        this.subGridName = new String(b8).trim();
        raf.read(b8);
        raf.read(b8);
        this.parentSubGridName = new String(b8).trim();
        raf.read(b8);
        raf.read(b8);
        this.created = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.updated = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.minLat = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.maxLat = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.minLon = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.maxLon = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.latInterval = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.lonInterval = Util.getDouble(b8, bigEndian);
        this.lonColumnCount = (1 + (int) ((this.maxLon - this.minLon) / this.lonInterval));
        this.latRowCount = (1 + (int) ((this.maxLat - this.minLat) / this.latInterval));
        raf.read(b8);
        raf.read(b8);
        this.nodeCount = Util.getInt(b8, bigEndian);
        if (this.nodeCount != this.lonColumnCount * this.latRowCount) {
            throw new IllegalStateException("SubGrid " + this.subGridName + " has inconsistent grid dimesions");
        }
    }

    public SubGrid getSubGridForCoord(double lon, double lat) {
        if (isCoordWithin(lon, lat)) {
            if (this.subGrid == null) {
                return this;
            }
            for (int i = 0; i < this.subGrid.length; i++) {
                if (this.subGrid[i].isCoordWithin(lon, lat)) {
                    return this.subGrid[i].getSubGridForCoord(lon, lat);
                }
            }
            return this;
        }

        return null;
    }

    private boolean isCoordWithin(double lon, double lat) {
        if ((lon >= this.minLon) && (lon < this.maxLon) && (lat >= this.minLat) && (lat < this.maxLat)) {
            return true;
        }
        return false;
    }

    private double interpolate(float a, float b, float c, float d, double X, double Y) {
        return a + (b - a) * X + (c - a) * Y + (a + d - b - c) * X * Y;
    }

    public GridShift interpolateGridShift(GridShift gs)
            throws IOException {
        int lonIndex = (int) ((gs.getLonPositiveWestSeconds() - this.minLon) / this.lonInterval);
        int latIndex = (int) ((gs.getLatSeconds() - this.minLat) / this.latInterval);

        double X = (gs.getLonPositiveWestSeconds() - (this.minLon + this.lonInterval * lonIndex)) / this.lonInterval;
        double Y = (gs.getLatSeconds() - (this.minLat + this.latInterval * latIndex)) / this.latInterval;

        int indexA = lonIndex + latIndex * this.lonColumnCount;
        int indexB = indexA + 1;
        int indexC = indexA + this.lonColumnCount;
        int indexD = indexC + 1;

        if (this.raf == null) {
            gs.setLonShiftPositiveWestSeconds(interpolate(this.lonShift[indexA], this.lonShift[indexB], this.lonShift[indexC], this.lonShift[indexD], X, Y));

            gs.setLatShiftSeconds(interpolate(this.latShift[indexA], this.latShift[indexB], this.latShift[indexC], this.latShift[indexD], X, Y));

            if (this.lonAccuracy == null) {
                gs.setLonAccuracyAvailable(false);
            } else {
                gs.setLonAccuracyAvailable(true);
                gs.setLonAccuracySeconds(interpolate(this.lonAccuracy[indexA], this.lonAccuracy[indexB], this.lonAccuracy[indexC], this.lonAccuracy[indexD], X, Y));
            }

            if (this.latAccuracy == null) {
                gs.setLatAccuracyAvailable(false);
            } else {
                gs.setLatAccuracyAvailable(true);
                gs.setLatAccuracySeconds(interpolate(this.latAccuracy[indexA], this.latAccuracy[indexB], this.latAccuracy[indexC], this.latAccuracy[indexD], X, Y));
            }
        } else {
            synchronized (this.raf) {
                byte[] b4 = new byte[4];
                long nodeOffset = this.subGridOffset + 176L + indexA * 16;
                this.raf.seek(nodeOffset);
                this.raf.read(b4);
                float latShiftA = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonShiftA = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float latAccuracyA = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonAccuracyA = Util.getFloat(b4, this.bigEndian);

                nodeOffset = this.subGridOffset + 176L + indexB * 16;
                this.raf.seek(nodeOffset);
                this.raf.read(b4);
                float latShiftB = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonShiftB = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float latAccuracyB = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonAccuracyB = Util.getFloat(b4, this.bigEndian);

                nodeOffset = this.subGridOffset + 176L + indexC * 16;
                this.raf.seek(nodeOffset);
                this.raf.read(b4);
                float latShiftC = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonShiftC = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float latAccuracyC = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonAccuracyC = Util.getFloat(b4, this.bigEndian);

                nodeOffset = this.subGridOffset + 176L + indexD * 16;
                this.raf.seek(nodeOffset);
                this.raf.read(b4);
                float latShiftD = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonShiftD = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float latAccuracyD = Util.getFloat(b4, this.bigEndian);
                this.raf.read(b4);
                float lonAccuracyD = Util.getFloat(b4, this.bigEndian);

                gs.setLonShiftPositiveWestSeconds(interpolate(lonShiftA, lonShiftB, lonShiftC, lonShiftD, X, Y));

                gs.setLatShiftSeconds(interpolate(latShiftA, latShiftB, latShiftC, latShiftD, X, Y));

                gs.setLonAccuracyAvailable(true);
                gs.setLonAccuracySeconds(interpolate(lonAccuracyA, lonAccuracyB, lonAccuracyC, lonAccuracyD, X, Y));

                gs.setLatAccuracyAvailable(true);
                gs.setLatAccuracySeconds(interpolate(latAccuracyA, latAccuracyB, latAccuracyC, latAccuracyD, X, Y));
            }
        }

        return gs;
    }

    public String getParentSubGridName() {
        return this.parentSubGridName;
    }

    public String getSubGridName() {
        return this.subGridName;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public int getSubGridCount() {
        return this.subGrid == null ? 0 : this.subGrid.length;
    }

    public SubGrid getSubGrid(int index) {
        return this.subGrid == null ? null : this.subGrid[index];
    }

    public void setSubGridArray(SubGrid[] subGrid) {
        this.subGrid = subGrid;
    }

    @Override
    public String toString() {
        return this.subGridName;
    }

    public String getDetails() {
        StringBuilder buf = new StringBuilder("Sub Grid : ");
        buf.append(this.subGridName);
        buf.append("\nParent   : ");
        buf.append(this.parentSubGridName);
        buf.append("\nCreated  : ");
        buf.append(this.created);
        buf.append("\nUpdated  : ");
        buf.append(this.updated);
        buf.append("\nMin Lat  : ");
        buf.append(this.minLat);
        buf.append("\nMax Lat  : ");
        buf.append(this.maxLat);
        buf.append("\nMin Lon  : ");
        buf.append(this.minLon);
        buf.append("\nMax Lon  : ");
        buf.append(this.maxLon);
        buf.append("\nLat Intvl: ");
        buf.append(this.latInterval);
        buf.append("\nLon Intvl: ");
        buf.append(this.lonInterval);
        buf.append("\nNode Cnt : ");
        buf.append(this.nodeCount);
        return buf.toString();
    }

    @Override
    public Object clone() {
        SubGrid clone = null;
        try {
            clone = (SubGrid) super.clone();
            if (this.subGrid != null) {
                clone.subGrid = new SubGrid[this.subGrid.length];
                for (int i = 0; i < this.subGrid.length; i++) {
                    clone.subGrid[i] = ((SubGrid) this.subGrid[i].clone());
                }
            }
        } catch (CloneNotSupportedException cnse) {
        }
        return clone;
    }

    public double getMaxLat() {
        return this.maxLat;
    }

    public double getMaxLon() {
        return this.maxLon;
    }

    public double getMinLat() {
        return this.minLat;
    }

    public double getMinLon() {
        return this.minLon;
    }
}