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
package org.cts.crs;

import java.util.ArrayList;
import java.util.List;

import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.ChangeCoordinateDimension;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import static org.cts.cs.Axis.LATITUDE;
import static org.cts.cs.Axis.LONGITUDE;
import org.cts.op.OppositeCoordinate;
import static org.cts.units.Unit.*;

/**
 * <p> A Geographic CoordinateReferenceSystem is a reference system based on a
 * GeodeticDatum and a 2D or 3D Ellipsoidal Coordinate System. </p> <p>
 *
 * @author Michaël Michaud, Erwan Bocher
 */
public class Geographic2DCRS extends GeodeticCRS {

    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains latitude
     * and second {@link Axis} contains longitude. The unit used by these axes
     * is radian.
     */
    public static CoordinateSystem LATLON_RR_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE}, new Unit[]{RADIAN, RADIAN});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains longitude
     * and second {@link Axis} contains latitude. The unit used by these axes is
     * radian.
     */
    public static CoordinateSystem LONLAT_RR_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE}, new Unit[]{RADIAN, RADIAN});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains latitude
     * and second {@link Axis} contains longitude. The unit used by these axes
     * is decimal degree.
     */
    public static CoordinateSystem LATLON_DD_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE}, new Unit[]{DEGREE, DEGREE});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains longitude
     * and second {@link Axis} contains latitude. The unit used by these axes is
     * decimal degree.
     */
    public static CoordinateSystem LONLAT_DD_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE}, new Unit[]{DEGREE, DEGREE});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains latitude
     * and second {@link Axis} contains longitude. The unit used by these axes
     * is grad.
     */
    public static CoordinateSystem LATLON_GG_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE}, new Unit[]{GRAD, GRAD});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains longitude
     * and second {@link Axis} contains latitude. The unit used by these axes is
     * grad.
     */
    public static CoordinateSystem LONLAT_GG_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE}, new Unit[]{GRAD, GRAD});

    /**
     * Create a new Geographic2DCRS.
     *
     * @param identifier the identifier of the Geographic2DCRS
     * @param datum the datum associated with the Geographic2DCRS
     * @param coordSys the coordinate system associated with the Geographic2DCRS
     */
    public Geographic2DCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys) {
        super(identifier, datum, coordSys);
    }

    /**
     * Create a new Geographic2DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude and the second {@link Axis}
     * contains longitude.
     *
     * @param identifier the identifier of the Geographic2DCRS
     * @param datum the datum associated with the Geographic2DCRS
     * @param unit the unit to use for the coordinate system associated with the
     * Geographic2DCRS
     */
    public Geographic2DCRS(Identifier identifier, GeodeticDatum datum, Unit unit) {
        super(identifier, datum, LATLON_DD_CS);
        if (unit == RADIAN) {
            this.coordinateSystem = LATLON_RR_CS;
        } else if (unit == DEGREE) {
            this.coordinateSystem = LATLON_DD_CS;
        } else if (unit == GRAD) {
            this.coordinateSystem = LATLON_GG_CS;
        } else;
    }

    /**
     * Create a new Geographic2DCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains latitude and the second {@link Axis}
     * contains longitude.
     *
     * @param identifier the identifier of the Geographic2DCRS
     * @param datum the datum associated with the Geographic2DCRS
     */
    public Geographic2DCRS(Identifier identifier, GeodeticDatum datum) {
        super(identifier, datum, LATLON_DD_CS);
    }

    /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        for (int i = 0; i < 2; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        // Convert from source unit to radians
        ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.RADIAN));
        // Add a third value to transform the geographic2D coord into a
        // geographic3D coord
        ops.add(ChangeCoordinateDimension.TO3D);
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation fromGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // Remove the third value to transform the geographic3D coord into a
        // geographic2D coord
        ops.add(ChangeCoordinateDimension.TO2D);
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Convert from radian to this coordinate system's units
        ops.add(UnitConversion.createUnitConverter(Unit.RADIAN,
                getCoordinateSystem().getUnit(0)));
        for (int i = 0; i < 2; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * @see GeodeticCRS#getProjection()
     */
    @Override
    public Projection getProjection() {
        return null;
    }

    /**
     * @see GeodeticCRS#getType()
     */
    public Type getType() {
        return Type.GEOGRAPHIC2D;
    }
}
