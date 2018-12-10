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
package org.cts.cs;

import java.util.Arrays;
import org.cts.CoordinateDimensionException;
import org.cts.units.Unit;

/**
 * A CoordinateSystem is a set of ordered {@link Axis} defining how coordinates
 * assigned to a point have to be interpreted.</p>
 *
 * @author Michaël Michaud
 */
public class CoordinateSystem {

    /**
     * The {@linkplain Axis axes} of this CoordinateSystem.
     */
    private Axis[] axes;

    /**
     * The {@linkplain Unit units} for the {@linkplain Axis axes} of this
     * CoordinateSystem.
     */
    private Unit[] units;

    /**
     * Create a new Coordinate System from an array of {@link Axis}
     *
     * @param axes the array of axes defining this CoordinateSystem
     * @param units the units used by coordinates, defined in the same order as
     * axes.
     */
    public CoordinateSystem(Axis[] axes, Unit[] units) {
        this.axes = axes;
        this.units = units;
    }

    /**
     * Return the {@link Axis} with index i of this CoordinateSystem.
     *
     * @param i index of the {@link Axis} to return
     * @return 
     */
    public Axis getAxis(int i) throws ArrayIndexOutOfBoundsException {
        try {
            return axes[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Try to access Axis "
                    + (i + 1) + " in a " + (axes.length) + "-d CoordinateSystem");
        }
    }

    /**
     * Return the {@link Unit} for Axis with index i of this CoordinateSystem.
     *
     * @param i index of the {@link Axis} for which Unit is returned
     * @return 
     */
    public Unit getUnit(int i) throws ArrayIndexOutOfBoundsException {
        try {
            return units[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Try to access unit of Axis " + (i + 1) + " in a "
                    + (axes.length) + "-d CoordinateSystem");
        }
    }

    /**
     * Return the axes number of this CoordinateSystem.
     * @return 
     */
    public int getDimension() {
        return axes.length;
    }

    /**
     * Return the index of {@link Axis} axis or -1 if axis is not part of this
     * CoordinateSystem. If there is many axes of the CoordinateSystem equals to
     * the axis in argument, the minimum index of these axes is returned.
     *
     * @param axis the axis whose index is looked for
     * @return 
     */
    public int getIndex(Axis axis) {
        for (int i = 0; i < axes.length; i++) {
            if (axes[i] == axis) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return a String representation of this coordinate in this
     * <code>CoordinateSystem</code>.
     *
     * @param coord the coordinate to format
     * @return 
     * @throws org.cts.CoordinateDimensionException
     */
    public String format(double[] coord) throws CoordinateDimensionException {
        if (coord.length < axes.length) {
            throw new CoordinateDimensionException(coord, axes.length);
        }
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < axes.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(axes[i]).append("=").append(coord[i]);
        }
        return sb.toString();
    }

    /**
     * Return a String representation of this CoordinateSystem.
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < axes.length; i++) {
            sb.append(i > 0 ? "|" : "").append(axes[i].toString()).append(" (").append(units[i].getSymbol()).append(")");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CoordinateSystem that = (CoordinateSystem) o;

        if (!Arrays.equals(axes, that.axes)) {
            return false;
        }
        return Arrays.equals(units, that.units);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(axes);
        result = 31 * result + Arrays.hashCode(units);
        return result;
    }
}
