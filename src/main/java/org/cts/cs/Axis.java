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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * One of the fixed reference lines of a {@link CoordinateSystem}.<p> Usually,
 * axis is a term reserved to cartesian coordinate systems made of several
 * perpendicular axis. In the context of this library, Axis objects are used for
 * any kind of coordinate system, including vertical and ellipsoidal ones.
 *
 * @author Michaël Michaud, Jules Party
 */
public class Axis {

    /**
     * The map that allows CTS to get Axes from their name.
     */
    private static Map<Direction, Map<String, Axis>> axisFromDirAndName = new EnumMap<Direction, Map<String, Axis>>(Direction.class);

    /**
     * Easting axis. Used for planimetric coordinate system, generally in pair
     * with northing.
     */
    public static final Axis EASTING = new Axis("Easting", Direction.EAST);

    /**
     * Northing axis. Used for planimetric coordinate system, generally in pair
     * with easting.
     */
    public static final Axis NORTHING = new Axis("Northing", Direction.NORTH);

    /**
     * Westing axis. Used for planimetric coordinate system, generally in pair
     * with southing.
     */
    public static final Axis WESTING = new Axis("Westing", Direction.WEST);

    /**
     * Southing axis. Used for planimetric coordinate system, generally in pair
     * with westing.
     */
    public static final Axis SOUTHING = new Axis("Southing", Direction.SOUTH);

    /**
     * x axis. Used for planimetric coordinate system, sometimes used in place
     * of easting.
     */
    public static final Axis x = new Axis("X", Direction.EAST);

    /**
     * y axis. Used for planimetric coordinate system, sometimes used in place
     * of northing.
     */
    public static final Axis y = new Axis("Y", Direction.NORTH);

    /**
     * Altitude axis. Used for vertical/compound system.
     */
    public static final Axis ALTITUDE = new Axis("Altitude", Direction.UP);

    /**
     * Depth axis. Used for bathymetry.
     */
    public static final Axis DEPTH = new Axis("Depth", Direction.DOWN);

    /**
     * Latitude axis. Used for geographic coordinate system, generally in pair
     * with longitude.
     */
    public static final Axis LATITUDE = new Axis("Latitude", Direction.NORTH);

    /**
     * Longitude axis. Used for geographic coordinate system, generally in pair
     * with latitude.
     */
    public static final Axis LONGITUDE = new Axis("Longitude", Direction.EAST);

    /**
     * Height axis. Used for 3D ellipsoidal coordinate system, generally with
     * latitude and longitude axes.
     */
    public static final Axis HEIGHT = new Axis("Height", Direction.UP);

    /**
     * X axis. Used for 3D cartesian system, generally with Y and Z axes.
     */
    public static final Axis X = new Axis("X", Direction.OTHER);

    /**
     * Y axis. Used for 3D cartesian system, generally with X and Z axes.
     */
    public static final Axis Y = new Axis("Y", Direction.EAST);

    /**
     * Z axis. Used for 3D cartesian system, generally with X and Y axes.
     */
    public static final Axis Z = new Axis("Z", Direction.NORTH);

    /**
     * Time axis. Not supported in CTS yet.
     */
    public static final Axis TIME = new Axis("Time", Direction.OTHER);

    /**
     * Axis different directions.
     */
    public enum Direction {

        EAST,
        WEST,
        NORTH,
        SOUTH,
        UP,
        DOWN,
        OTHER
    }

    /**
     * The name of this Axis (X, Y, Z, LONGITUDE, ALTITUDE,&hellip;).
     */
    private String name;

    /**
     * The direction of the axis as it is defined in OGC WKT. It should only
     * take one of these values : NORTH, SOUTH, EAST, WEST or OTHER. See
     * <a href =http://trac.osgeo.org/gdal/wiki/rfc20_srs_axes>here</a> for
     * further details.
     */
    private Direction direction;

    /**
     * Register the axis into the map that allows CTS to get Axes from their
     * name.
     */
    private void registerAxis() {
        Map<String, Axis> map = axisFromDirAndName.get(getDirection());
        if (map == null) {
            axisFromDirAndName.put(getDirection(), new HashMap<String, Axis>());
        }
        axisFromDirAndName.get(getDirection()).put(getName().toLowerCase(), this);
    }

    public static Axis getAxis(Direction dir, String name) {
        Map<String, Axis> map = axisFromDirAndName.get(dir);
        if (map == null) {
            return null;
        }
        return map.get(name);
    }

    /**
     * Create a new Axis.
     *
     * @param name name of this new Axis
     * @param dir the direction of the axis (EAST, NORTH, UP, DOWN,&hellip;)
     */
    public Axis(String name, Direction dir) {
        this.name = name;
        this.direction = dir;
        this.registerAxis();
    }

    /**
     * Return the name of this Axis (X, Y, Z, LONGITUDE, ALTITUDE,&hellip;).
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * Return the direction of this Axis (NORTH, SOUTH, EAST, WEST or OTHER).
     * @return 
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Return the direction correpsonding to the string in parameter.
     *
     * @param dir the name of the direction
     * @return 
     */
    public static Direction getDirection(String dir) {
        Direction direction = null;
        if (dir != null) {
            if (dir.equals("EAST")) {
                direction = Direction.EAST;
            } else if (dir.equals("NORTH")) {
                direction = Direction.NORTH;
            } else if (dir.equals("WEST")) {
                direction = Direction.WEST;
            } else if (dir.equals("SOUTH")) {
                direction = Direction.SOUTH;
            } else if (dir.equals("UP")) {
                direction = Direction.UP;
            } else if (dir.equals("DOWN")) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.OTHER;
            }
        }
        return direction;
    }

    /**
     * Returns a WKT representation of the axis.
     *
     * @return 
     */
    public String toWKT() {
        String w = "AXIS[\"" +
                this.getName() +
                "\"," +
                this.getDirection() +
                ']';
        return w;
    }

    /**
     * Return a String representation of this Axis.
     * @return 
     */
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Axis axis = (Axis) o;

        return direction == axis.direction;

    }

    @Override
    public int hashCode() {
        return direction.hashCode();
    }
}
