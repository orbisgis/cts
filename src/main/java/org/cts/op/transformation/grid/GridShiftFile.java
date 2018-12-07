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
import java.util.ArrayList;
import java.util.HashMap;

public class GridShiftFile
        implements Serializable {

    private static final int REC_SIZE = 16;
    private String overviewHeaderCountId;
    private int overviewHeaderCount;
    private int subGridHeaderCount;
    private int subGridCount;
    private String shiftType;
    private String version;
    private String fromEllipsoid = "";
    private String toEllipsoid = "";
    private double fromSemiMajorAxis;
    private double fromSemiMinorAxis;
    private double toSemiMajorAxis;
    private double toSemiMinorAxis;
    private SubGrid[] topLevelSubGrid;
    private SubGrid lastSubGrid;
    private transient RandomAccessFile raf;

    public void loadGridShiftFile(InputStream in, boolean loadAccuracy)
            throws IOException {
        byte[] b8 = new byte[8];
        boolean bigEndian = true;
        this.fromEllipsoid = "";
        this.toEllipsoid = "";
        this.topLevelSubGrid = null;
        in.read(b8);
        this.overviewHeaderCountId = new String(b8);
        if (!"NUM_OREC".equals(this.overviewHeaderCountId)) {
            throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
        }
        in.read(b8);
        this.overviewHeaderCount = Util.getIntBE(b8, 0);
        if (this.overviewHeaderCount == 11) {
            bigEndian = true;
        } else {
            this.overviewHeaderCount = Util.getIntLE(b8, 0);
            if (this.overviewHeaderCount == 11) {
                bigEndian = false;
            } else {
                throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
            }
        }
        in.read(b8);
        in.read(b8);
        this.subGridHeaderCount = Util.getInt(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.subGridCount = Util.getInt(b8, bigEndian);
        SubGrid[] subGrid = new SubGrid[this.subGridCount];
        in.read(b8);
        in.read(b8);
        this.shiftType = new String(b8);
        in.read(b8);
        in.read(b8);
        this.version = new String(b8);
        in.read(b8);
        in.read(b8);
        this.fromEllipsoid = new String(b8);
        in.read(b8);
        in.read(b8);
        this.toEllipsoid = new String(b8);
        in.read(b8);
        in.read(b8);
        this.fromSemiMajorAxis = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.fromSemiMinorAxis = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.toSemiMajorAxis = Util.getDouble(b8, bigEndian);
        in.read(b8);
        in.read(b8);
        this.toSemiMinorAxis = Util.getDouble(b8, bigEndian);

        for (int i = 0; i < this.subGridCount; i++) {
            subGrid[i] = new SubGrid(in, bigEndian, loadAccuracy);
        }
        this.topLevelSubGrid = createSubGridTree(subGrid);
        this.lastSubGrid = this.topLevelSubGrid[0];

        in.close();
    }

    public void loadGridShiftFile(RandomAccessFile raf)
            throws IOException {
        this.raf = raf;
        byte[] b8 = new byte[8];
        boolean bigEndian = true;
        this.fromEllipsoid = "";
        this.toEllipsoid = "";
        this.topLevelSubGrid = null;
        raf.seek(0L);
        raf.read(b8);
        this.overviewHeaderCountId = new String(b8);
        if (!"NUM_OREC".equals(this.overviewHeaderCountId)) {
            this.raf = null;
            throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
        }
        raf.read(b8);
        this.overviewHeaderCount = Util.getIntBE(b8, 0);
        if (this.overviewHeaderCount == 11) {
            bigEndian = true;
        } else {
            this.overviewHeaderCount = Util.getIntLE(b8, 0);
            if (this.overviewHeaderCount == 11) {
                bigEndian = false;
            } else {
                this.raf = null;
                throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
            }
        }
        raf.read(b8);
        raf.read(b8);
        this.subGridHeaderCount = Util.getInt(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.subGridCount = Util.getInt(b8, bigEndian);
        SubGrid[] subGrid = new SubGrid[this.subGridCount];
        raf.read(b8);
        raf.read(b8);
        this.shiftType = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.version = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.fromEllipsoid = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.toEllipsoid = new String(b8);
        raf.read(b8);
        raf.read(b8);
        this.fromSemiMajorAxis = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.fromSemiMinorAxis = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.toSemiMajorAxis = Util.getDouble(b8, bigEndian);
        raf.read(b8);
        raf.read(b8);
        this.toSemiMinorAxis = Util.getDouble(b8, bigEndian);

        long offset = this.overviewHeaderCount * 16;
        for (int i = 0; i < this.subGridCount; i++) {
            subGrid[i] = new SubGrid(raf, offset, bigEndian);
            offset = offset + this.subGridHeaderCount * 16 + subGrid[i].getNodeCount() * 16;
        }
        this.topLevelSubGrid = createSubGridTree(subGrid);
        this.lastSubGrid = this.topLevelSubGrid[0];
    }

    private SubGrid[] createSubGridTree(SubGrid[] subGrid) {
        int topLevelCount = 0;
        HashMap subGridMap = new HashMap();
        for (SubGrid aSubGrid : subGrid) {
            if (aSubGrid.getParentSubGridName().equalsIgnoreCase("NONE")) {
                topLevelCount++;
            }
            subGridMap.put(aSubGrid.getSubGridName(), new ArrayList());
        }
        SubGrid[] topLevelSubGridTemp = new SubGrid[topLevelCount];
        topLevelCount = 0;
        for (SubGrid subGrid1 : subGrid) {
            if (subGrid1.getParentSubGridName().equalsIgnoreCase("NONE")) {
                topLevelSubGridTemp[(topLevelCount++)] = subGrid1;
            } else {
                ArrayList parent = (ArrayList) subGridMap.get(subGrid1.getParentSubGridName());
                parent.add(subGrid1);
            }
        }
        SubGrid[] nullArray = new SubGrid[0];
        for (SubGrid subGrid1 : subGrid) {
            ArrayList subSubGrids = (ArrayList) subGridMap.get(subGrid1.getSubGridName());
            if (subSubGrids.size() > 0) {
                SubGrid[] subGridArray = (SubGrid[]) subSubGrids.toArray(nullArray);
                subGrid1.setSubGridArray(subGridArray);
            }
        }
        return topLevelSubGridTemp;
    }

    public boolean gridShiftForward(GridShift gs)
            throws IOException {
        SubGrid subGrid = this.lastSubGrid.getSubGridForCoord(gs.getLonPositiveWestSeconds(), gs.getLatSeconds());
        if (subGrid == null) {
            subGrid = getSubGrid(gs.getLonPositiveWestSeconds(), gs.getLatSeconds());
        }
        if (subGrid == null) {
            return false;
        }
        subGrid.interpolateGridShift(gs);
        gs.setSubGridName(subGrid.getSubGridName());
        this.lastSubGrid = subGrid;
        return true;
    }

    public boolean gridShiftReverse(GridShift gs)
            throws IOException {
        GridShift forwardGs = new GridShift();
        forwardGs.setLonPositiveWestSeconds(gs.getLonPositiveWestSeconds());
        forwardGs.setLatSeconds(gs.getLatSeconds());
        for (int i = 0; i < 4; i++) {
            if (!gridShiftForward(forwardGs)) {
                return false;
            }
            forwardGs.setLonPositiveWestSeconds(gs.getLonPositiveWestSeconds() - forwardGs.getLonShiftPositiveWestSeconds());

            forwardGs.setLatSeconds(gs.getLatSeconds() - forwardGs.getLatShiftSeconds());
        }
        gs.setLonShiftPositiveWestSeconds(-forwardGs.getLonShiftPositiveWestSeconds());
        gs.setLatShiftSeconds(-forwardGs.getLatShiftSeconds());
        gs.setLonAccuracyAvailable(forwardGs.isLonAccuracyAvailable());
        if (forwardGs.isLonAccuracyAvailable()) {
            gs.setLonAccuracySeconds(forwardGs.getLonAccuracySeconds());
        }
        gs.setLatAccuracyAvailable(forwardGs.isLatAccuracyAvailable());
        if (forwardGs.isLatAccuracyAvailable()) {
            gs.setLatAccuracySeconds(forwardGs.getLatAccuracySeconds());
        }
        return true;
    }

    private SubGrid getSubGrid(double lon, double lat) {
        SubGrid sub = null;
        for (SubGrid aTopLevelSubGrid : this.topLevelSubGrid) {
            sub = aTopLevelSubGrid.getSubGridForCoord(lon, lat);
            if (sub != null) {
                break;
            }
        }
        return sub;
    }

    public boolean isLoaded() {
        return this.topLevelSubGrid != null;
    }

    public void unload() throws IOException {
        this.topLevelSubGrid = null;
        if (this.raf != null) {
            this.raf.close();
            this.raf = null;
        }
    }

    @Override
    public String toString() {
        String buf = "Headers  : " + this.overviewHeaderCount +
                "\nSub Hdrs : " +
                this.subGridHeaderCount +
                "\nSub Grids: " +
                this.subGridCount +
                "\nType     : " +
                this.shiftType +
                "\nVersion  : " +
                this.version +
                "\nFr Ellpsd: " +
                this.fromEllipsoid +
                "\nTo Ellpsd: " +
                this.toEllipsoid +
                "\nFr Maj Ax: " +
                this.fromSemiMajorAxis +
                "\nFr Min Ax: " +
                this.fromSemiMinorAxis +
                "\nTo Maj Ax: " +
                this.toSemiMajorAxis +
                "\nTo Min Ax: " +
                this.toSemiMinorAxis;
        return buf;
    }

    public SubGrid[] getSubGridTree() {
        SubGrid[] clone = new SubGrid[this.topLevelSubGrid.length];
        for (int i = 0; i < this.topLevelSubGrid.length; i++) {
            clone[i] = ((SubGrid) this.topLevelSubGrid[i].clone());
        }
        return clone;
    }

    public String getFromEllipsoid() {
        return this.fromEllipsoid;
    }

    public String getToEllipsoid() {
        return this.toEllipsoid;
    }
}
