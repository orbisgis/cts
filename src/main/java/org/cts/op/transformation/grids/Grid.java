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
package org.cts.op.transformation.grids;

import org.cts.cs.OutOfExtentException;

/**
 * A grid is a data structure containing numeric data organized in rows and
 * columns. Cell 0,0 represents a real location x0, y0, and every cell has a
 * width (dx) and a height (dy) so that a cell at row i and column j represents
 * a location of coordinates
 * <ul>
 * <li>x = j*dx+x0</li>
 * <li>y = i*dy+y0</li>
 * </ul>
 * The main grid parameters are :
 * <ul>
 * <li>The number of columns (c) and rows (r) (with a total number of values in
 * the grid equals to c * r)</li>
 * <li>x0, y0, the real coordinates of the grid cell at column 0, row 0</li>
 * <li>gridWidth and gridHeight, which represent the real grid width and the
 * real grid height</li>
 * <ul>
 * Other derivative parameters are
 * <ul>
 * <li>dx = gridWidth/(column number -1)</li>
 * <li>dx = gridHeight/(row number -1)</li>
 * </ul>
 * For interpolation methods, see
 * <a href="http://www.geovista.psu.edu/sites/geocomp99/Gc99/082/gc_082.htm">
 * What's the point? Interpolation and extrapolation with a regular grid DEM</a>
 * </p>
 *
 * @author Michaël Michaud
 */
public interface Grid {

    public static enum InterpolationMethod {

        NEAREST, BILINEAR, BICUBIC
    }
    /**
     * Nearest interpolation method.
     */
    public final static int NEAREST = 1;
    /**
     * Bilinear interpolation method.
     */
    public final static int BILINEAR = 2;
    /**
     * Bicubic interpolation method.
     */
    public final static int BICUBIC = 3;

    /**
     * Get the number of columns of this grid.
     * @return 
     */
    public int getColumnNumber();

    /**
     * Get the number of rows of this grid.
     * @return 
     */
    public int getRowNumber();

    /**
     * Get the real world abscisse (x) of the first column.
     * @return 
     */
    public double getX0();

    /**
     * Get the real world ordinate (y) of the first row.
     * @return 
     */
    public double getY0();

    /**
     * Get the first ordinate of the last grid column.
     */
    public double getXL();

    /**
     * Get the second ordinate of the last grid row.
     */
    public double getYL();

    /**
     * Real world interval between two consecutive columns. The method returns a
     * negative value if x values decreases when column indices increases.
     * @return 
     */
    public double getDX();

    /**
     * Real world interval between two consecutive row. The method returns a
     * negative value if y values decreases when column indices increases.
     * @return 
     */
    public double getDY();

    /**
     * Get the value corrsponding to the x,y position. WARNING : x, y represent
     * the real world coordinates and not the matrix coordinate.
     * @param x
     * @param y
     * @param method
     * @return 
     * @throws org.cts.cs.OutOfExtentException 
     * @throws org.cts.op.transformation.grids.InterpolationMethodException 
     */
    public double[] getValue(double x, double y, InterpolationMethod method)
            throws OutOfExtentException, InterpolationMethodException;
}