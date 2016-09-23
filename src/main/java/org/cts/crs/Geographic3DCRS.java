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
package org.cts.crs;

import java.util.ArrayList;
import java.util.List;

import org.cts.Identifiable;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.OppositeCoordinate;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import static org.cts.cs.Axis.HEIGHT;
import static org.cts.cs.Axis.LATITUDE;
import static org.cts.cs.Axis.LONGITUDE;
import static org.cts.cs.Axis.Direction.DOWN;
import static org.cts.cs.Axis.Direction.EAST;
import static org.cts.cs.Axis.Direction.SOUTH;
import static org.cts.cs.Axis.Direction.WEST;
import static org.cts.units.Unit.DEGREE;
import static org.cts.units.Unit.GRAD;
import static org.cts.units.Unit.METER;
import static org.cts.units.Unit.RADIAN;

/**
 * <p>A {@link org.cts.crs.CoordinateReferenceSystem} based on a
 * {@link org.cts.datum.GeodeticDatum} and a 3D Ellipsoidal
 * {@link org.cts.cs.CoordinateSystem}.</p>
 *
 * @author Michaël Michaud
 */
public class Geographic3DCRS extends GeodeticCRS {

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are radian and meter.
     */
    public static final CoordinateSystem LATLONH_RRM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{RADIAN,
        RADIAN, METER});

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains
     * longitude, second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are radian and meter.
     */
    public static final CoordinateSystem LONLATH_RRM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{RADIAN,
        RADIAN, METER});

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are decimal degree and
     * meter.
     */
    public static final CoordinateSystem LATLONH_DDM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{DEGREE,
        DEGREE, METER});

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains
     * longitude, second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are decimal degree and
     * meter.
     */
    public static final CoordinateSystem LONLATH_DDM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{DEGREE,
        DEGREE, METER});

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains latitude,
     * second {@link Axis} contains longitude and third axis contains
     * ellipsoidal height. The units used by these axes are grad and meter.
     */
    public static final CoordinateSystem LATLONH_GGM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{GRAD,
        GRAD, METER});

    /**
     * A 3D {@link CoordinateSystem} whose first {@link Axis} contains
     * longitude, second {@link Axis} contains latitude and third axis contains
     * ellipsoidal height. The units used by these axes are grad and meter.
     */
    public static final CoordinateSystem LONLATH_GGM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{GRAD,
        GRAD, METER});

    /**
     * Creates a new Geographic3DCRS.
     *
     * @param identifier the identifier of the Geographic3DCRS
     * @param datum the datum associated with the Geographic3DCRS
     * @param coordSys the coordinate system associated with the Geographic3DCRS
     */
    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys) {
        super(identifier, datum, coordSys);
    }

    /**
     * Creates a new Geographic2DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude, the second {@link Axis}
     * contains longitude and the third contains the ellipsoidal height in
     * meters.
     *
     * @param identifier the identifier of the Geographic3DCRS
     * @param datum the datum associated with the Geographic3DCRS
     * @param unit the angular unit to use for the two first axis of the
     * coordinate system associated with the Geographic3DCRS
     */
    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum, Unit unit) {
        super(identifier, datum, LATLONH_DDM_CS);
        if (unit == RADIAN) {
            this.coordinateSystem = LATLONH_RRM_CS;
        } else if (unit == DEGREE) {
            this.coordinateSystem = LATLONH_DDM_CS;
        } else if (unit == GRAD) {
            this.coordinateSystem = LATLONH_GGM_CS;
        } else {
            // default unit
            this.coordinateSystem = LATLONH_RRM_CS;
        }
    }

    /**
     * Creates a new Geographic3DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude, the second {@link Axis}
     * contains longitude and the third contains the ellipsoidal height.
     * Latitude and longitude are in decimal degrees while height is in meters.
     *
     * @param identifier the identifier of the Geographic2DCRS
     * @param datum the datum associated with the Geographic2DCRS
     */
    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum) {
        super(identifier, datum, LATLONH_DDM_CS);
    }

    /**
     * @return 
     * @see GeodeticCRS#getType()
     */
    @Override
    public Type getType() {
        return Type.GEOGRAPHIC3D;
    }

    /**
     * @return 
     * @see GeodeticCRS#getProjection()
     */
    @Override
    public Projection getProjection() { return null; }

    /**
     * @return 
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == DOWN) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        // Convert from source unit to radians
        ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), RADIAN,
                getCoordinateSystem().getUnit(2), METER));
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == EAST
                || getCoordinateSystem().getAxis(0).getDirection() == WEST) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * @return 
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation fromGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == EAST
                || getCoordinateSystem().getAxis(0).getDirection() == WEST) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Convert from radian to this coordinate system's units
        ops.add(UnitConversion.createUnitConverter(RADIAN, getCoordinateSystem().getUnit(0),
                METER, getCoordinateSystem().getUnit(2)));
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == DOWN) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Returns a WKT representation of the geographic 3D CRS.
     * @return 
     */
    @Override
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("GEOGCS[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getDatum().toWKT());
        w.append(',');
        w.append(this.getDatum().getPrimeMeridian().toWKT());
        w.append(',');
        w.append(this.getCoordinateSystem().getUnit(0).toWKT());
        for (int i = 0; i < this.getCoordinateSystem().getDimension(); i++) {
            w.append(',');
            w.append(this.getCoordinateSystem().getAxis(i).toWKT());
        }
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }
}
