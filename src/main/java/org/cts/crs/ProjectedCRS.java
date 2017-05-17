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
import org.cts.op.ChangeCoordinateDimension;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.OppositeCoordinate;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import static org.cts.cs.Axis.EASTING;
import static org.cts.cs.Axis.NORTHING;
import static org.cts.cs.Axis.Direction.NORTH;
import static org.cts.cs.Axis.Direction.SOUTH;
import static org.cts.cs.Axis.Direction.WEST;
import static org.cts.units.Unit.METER;

/**
 * A CoordinateReferenceSystem based on a {@link org.cts.datum.GeodeticDatum}
 * and a {@link org.cts.op.projection.Projection}.
 *
 * @author Michaël Michaud
 */
public class ProjectedCRS extends GeodeticCRS {

    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains easting
     * and second {@link Axis} contains northing. The unit used by these axes is
     * meter.
     */
    public static final CoordinateSystem EN_CS = new CoordinateSystem(new Axis[]{
        EASTING, NORTHING}, new Unit[]{METER, METER});

    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains northing
     * and second {@link Axis} contains easting. The unit used by these axes is
     * meter.
     */
    public static final CoordinateSystem NE_CS = new CoordinateSystem(new Axis[]{
        NORTHING, EASTING}, new Unit[]{METER, METER});

    /**
     * The projection used by this ProjectedCRS.
     */
    private Projection projection;

    /**
     * Create a new ProjectedCRS.
     *
     * @param identifier the identifier of the ProjectedCRS
     * @param datum the datum associated with the ProjectedCRS
     * @param coordSys the coordinate system associated with the ProjectedCRS
     * @param projection the projection used in the ProjectedCRS
     */
    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys, Projection projection) {
        super(identifier, datum, coordSys);
        this.projection = projection;
    }

    /**
     * Create a new ProjectedCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains easting and the second {@link Axis}
     * contains northing.
     *
     * @param identifier the identifier of the ProjectedCRS
     * @param datum the datum associated with the ProjectedCRS
     * @param projection the projection used in the ProjectedCRS
     * @param unit the length unit to use for the coordinate system associated
     * with the ProjectedCRS
     */
    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            Projection projection, Unit unit) {
        super(identifier, datum, new CoordinateSystem(new Axis[]{EASTING,
            NORTHING}, new Unit[]{unit, unit}));
        this.projection = projection;
    }

    /**
     * Create a new ProjectedCRS. The first {@link Axis} of the associated
     * {@link CoordinateSystem} contains easting and the second {@link Axis}
     * contains northing.
     *
     * @param identifier the identifier of the ProjectedCRS
     * @param datum the datum associated with the ProjectedCRS
     * @param projection the projection used in the ProjectedCRS
     */
    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            Projection projection) {
        super(identifier, datum, EN_CS);
        this.projection = projection;
    }

    /**
     * @return 
     * @see GeodeticCRS#getProjection()
     */
    @Override
    public Projection getProjection() {
        return projection;
    }

    /**
     * @return 
     * @see GeodeticCRS#getType()
     */
    @Override
    public Type getType() {
        return Type.PROJECTED;
    }

    /**
     * @return 
     * @throws org.cts.op.NonInvertibleOperationException 
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException {

        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        for (int i = 0; i < 2; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == WEST) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        // Convert units
        if (getCoordinateSystem().getUnit(0) != METER) {
            ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), METER));
        }
        // switch easting/northing coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == NORTH
                || getCoordinateSystem().getAxis(0).getDirection() == SOUTH) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Apply the inverse projection
        ops.add(projection.inverse());
        // Add a third value to transform the geographic2D coord into a
        // geographic3D coord
        ops.add(ChangeCoordinateDimension.TO3D);
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
        // Remove the third value to transform the geographic3D coord into a
        // geographic2D coord
        ops.add(ChangeCoordinateDimension.TO2D);
        // Projection
        ops.add(projection);
        // switch easting/northing coordinate if necessary
        if (getCoordinateSystem().getAxis(0).getDirection() == NORTH
                || getCoordinateSystem().getAxis(0).getDirection() == SOUTH) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Unit conversion
        if (getCoordinateSystem().getUnit(0) != METER) {
            ops.add(UnitConversion.createUnitConverter(METER, getCoordinateSystem().getUnit(0)));
        }
        for (int i = 0; i < 2; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == WEST) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Returns a WKT representation of the projected CRS.
     *
     * @return 
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("PROJCS[\"");
        w.append(this.getName());
        w.append("\",GEOGCS[\"");
        w.append(this.getDatum().getShortName());
        w.append("\",");
        w.append(this.getDatum().toWKT());
        w.append(',');
        w.append(this.getDatum().getPrimeMeridian().toWKT());
        w.append("],");
        // Need CRS context to write projection parameters with the same units
        w.append(this.getProjection().toWKT(this.getCoordinateSystem().getUnit(0)));
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

    /**
     * Returns true if o is equals to this ProjectedCRS.
     *
     * @param o The object to compare this ProjectedCRS to.
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GeodeticCRS) {
            GeodeticCRS crs = (GeodeticCRS) o;
            if (!getType().equals(crs.getType())) {
                return false;
            }
            if (getIdentifier().equals(crs.getIdentifier())) {
                return true;
            }
            return getDatum().equals(crs.getDatum()) && getProjection().equals(crs.getProjection())
                    && getCoordinateSystem().equals(crs.getCoordinateSystem()) /*&& nadgrids*/;
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this ProjectedCRS.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.getDatum() != null ? this.getDatum().hashCode() : 0);
        hash = 59 * hash + (this.projection != null ? this.projection.hashCode() : 0);
        return hash;
    }
}
