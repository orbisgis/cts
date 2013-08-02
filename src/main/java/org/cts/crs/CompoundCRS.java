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

import org.cts.Identifiable;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.VerticalDatum;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.Identity;
import org.cts.op.LoadMemorizeCoordinate;
import org.cts.op.MemorizeCoordinate;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.OppositeCoordinate;
import org.cts.op.UnitConversion;
import org.cts.op.transformation.Altitude2EllipsoidalHeight;
import org.cts.units.Unit;

/**
 * A compound CoordinateReferenceSystem is a
 * {@link org.cts.crs.CoordinateReferenceSystem} composed by two distinct
 * CoordinateReferenceSystem : a {@link org.cts.crs.GeodeticCRS} for 2D
 * horizontal coordinates and a {@link org.cts.crs.VerticalCRS} for the z
 * coordinate.
 *
 * @author Michaël Michaud, Jules Party
 */
public class CompoundCRS extends GeodeticCRS {

    private GeodeticCRS horizontalCRS;
    private VerticalCRS verticalCRS;

    /**
     * Create a new GeodeticCRS.
     *
     * @param identifier the identifier of the CompoundCRS
     * @param horizontalCRS the horizontal part of the CompoundCRS
     * @param verticalCRS the vertical part of the CompoundCRS
     */
    public CompoundCRS(Identifier identifier, GeodeticCRS horizontalCRS,
            VerticalCRS verticalCRS) throws CRSException {
        super(identifier, horizontalCRS.getDatum(), new CoordinateSystem(
                new Axis[]{horizontalCRS.getCoordinateSystem().getAxis(0),
            horizontalCRS.getCoordinateSystem().getAxis(1),
            verticalCRS.getCoordinateSystem().getAxis(0)},
                new Unit[]{horizontalCRS.getCoordinateSystem().getUnit(0),
            horizontalCRS.getCoordinateSystem().getUnit(1),
            verticalCRS.getCoordinateSystem().getUnit(0)}));
        if (!(horizontalCRS instanceof ProjectedCRS || horizontalCRS instanceof Geographic2DCRS)) {
            throw new CRSException("The horizontalCRS must be a ProjectedCRS or a Geographic2DCRS. The "
                    + horizontalCRS.getClass() + " cannot be used as horizontalCRS.");
        }
        this.horizontalCRS = horizontalCRS;
        this.verticalCRS = verticalCRS;
    }

    /**
     * Return this CoordinateReferenceSystem Type.
     */
    @Override
    public Type getType() {
        return CoordinateReferenceSystem.Type.COMPOUND;
    }

    /**
     * Return the horizonal part of this CoordinateReferenceSystem.
     */
    public GeodeticCRS getHorizontalCRS() {
        return horizontalCRS;
    }

    /**
     * Return the vertical part of this CoordinateReferenceSystem.
     */
    public VerticalCRS getVerticalCRS() {
        return verticalCRS;
    }

    /**
     * Returns the number of dimensions of the coordinate system.
     */
    @Override
    public int getDimension() {
        return 3;
    }

    /**
     * Return the list of nadgrids transformation defined for the horizontal CRS
     * of this CompoundCRS that used the datum in parameter as target datum.
     *
     * @param datum the datum that must be a target for returned nadgrid
     * transformation
     */
    @Override
    public List<CoordinateOperation> getGridTransformations(GeodeticDatum datum) {
        return horizontalCRS.getGridTransformations(datum);
    }

    /**
     * Creates a CoordinateOperation object to convert coordinates from this
     * CoordinateReferenceSystem to a GeographicReferenceSystem based on the
     * same horizonal datum and vertical datum, and using normal SI units in the
     * following order : latitude (rad), longitude (rad) height/altitude (m).
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.DOWN) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        if (horizontalCRS instanceof Geographic2DCRS) {
            // Convert from source unit to radians and meters.
            if (getCoordinateSystem().getUnit(0) != Unit.RADIAN || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.RADIAN, getCoordinateSystem().getUnit(2),
                        Unit.METER));
            }
            // switch from LON/LAT to LAT/LON or northing/easting coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
        } else {
            // Convert units
            if (getCoordinateSystem().getUnit(0) != Unit.METER || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.METER, getCoordinateSystem().getUnit(2),
                        Unit.METER));
            }
            // switch easting/northing coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.NORTH
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.SOUTH) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
            // Apply the inverse projection
            ops.add(horizontalCRS.getProjection().inverse());
        }
        if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)
                && !horizontalCRS.getDatum().getEllipsoid().equals(verticalCRS.getDatum().getEllipsoid())) {
            System.out.println("Unsupported operation for this CRS : " + this);
            //TO DO
        } else if (verticalCRS.getDatum().getAltiToEllpsHeight() instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight transfo = (Altitude2EllipsoidalHeight) verticalCRS.getDatum().getAltiToEllpsHeight();
            if (!horizontalCRS.getDatum().equals(transfo.getAssociatedDatum())) {
                ops.add(MemorizeCoordinate.memoZ);
                if (horizontalCRS.getGridTransformations(transfo.getAssociatedDatum()) != null) {
                    ops.add(horizontalCRS.getGridTransformations(transfo.getAssociatedDatum()).get(0));
                    ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                    ops.add(LoadMemorizeCoordinate.loadZ);
                    ops.add(transfo);
                    ops.add(UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER));
                    ops.add(horizontalCRS.getGridTransformations(transfo.getAssociatedDatum()).get(0).inverse());
                } else {
                    ops.add(horizontalCRS.getDatum().getCoordinateOperations(transfo.getAssociatedDatum()).get(0));
                    ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                    ops.add(LoadMemorizeCoordinate.loadZ);
                    ops.add(transfo);
                    ops.add(UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER));
                    ops.add(transfo.getAssociatedDatum().getCoordinateOperations(horizontalCRS.getDatum()).get(0));
                }
            } else {
                ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                ops.add(transfo);
                ops.add(UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER));
            }
        } else if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)) {
            ops.add(Identity.IDENTITY);
        } else {
            System.out.println("Unsupported operation for this CRS : " + this);
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * GeographicReferenceSystem based on the same horizonal datum and vertical
     * datum, and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height/altitude (m) to this CoordinateReferenceSystem.
     */
    @Override
    public CoordinateOperation fromGeographicCoordinateConverter()
            throws NonInvertibleOperationException {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)
                && !horizontalCRS.getDatum().getEllipsoid().equals(verticalCRS.getDatum().getEllipsoid())) {
            System.out.println("Unsupported operation for this CRS : " + this);
            // TO DO
        } else if (verticalCRS.getDatum().getAltiToEllpsHeight() instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight transfo = (Altitude2EllipsoidalHeight) verticalCRS.getDatum().getAltiToEllpsHeight();
            ops.add(MemorizeCoordinate.memoX);
            ops.add(MemorizeCoordinate.memoY);
            if (!horizontalCRS.getDatum().equals(transfo.getAssociatedDatum())) {
                if (horizontalCRS.getGridTransformations(transfo.getAssociatedDatum()) != null) {
                    ops.add(horizontalCRS.getGridTransformations(transfo.getAssociatedDatum()).get(0));
                } else {
                    ops.add(horizontalCRS.getDatum().getCoordinateOperations(transfo.getAssociatedDatum()).get(0));
                }
            }
            ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
            ops.add(transfo.inverse());
            ops.add(LoadMemorizeCoordinate.loadY);
            ops.add(LoadMemorizeCoordinate.loadX);
        } else if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)) {
            ops.add(Identity.IDENTITY);
        } else {
            System.out.println("Unsupported operation for this CRS : " + this);
        }
        if (horizontalCRS instanceof Geographic2DCRS) {
            // Convert from source unit to radians and meters.
            if (getCoordinateSystem().getUnit(0) != Unit.RADIAN || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, getCoordinateSystem().getUnit(0),
                        Unit.METER, getCoordinateSystem().getUnit(2)));
            }
            // switch from LON/LAT to LAT/LON coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
        } else {
            // Apply the inverse projection
            ops.add(horizontalCRS.getProjection());
            // Convert units
            if (getCoordinateSystem().getUnit(0) != Unit.METER || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(Unit.METER, getCoordinateSystem().getUnit(0),
                        Unit.METER, getCoordinateSystem().getUnit(2)));
            }
            // switch easting/northing coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.NORTH
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.SOUTH) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
        }
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.DOWN) {
                ops.add(new OppositeCoordinate(i));
            }
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Returns a WKT representation of the compound CRS.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("COMPD_CS[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getHorizontalCRS().toWKT());
        w.append(',');
        w.append(this.getVerticalCRS().toWKT());
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Return a String representation of this Datum.
     */
    @Override
    public String toString() {
        return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName() + " ("
                + getShortName() + ")";
    }
}