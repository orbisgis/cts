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
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import static org.cts.cs.Axis.ALTITUDE;
import static org.cts.cs.Axis.HEIGHT;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.OppositeCoordinate;
import org.cts.op.UnitConversion;
import static org.cts.units.Unit.METER;

/**
 * A vertical {@link org.cts.crs.CoordinateReferenceSystem} is a
 * CoordinateReferenceSystem based on a VerticalDatum. It is used to indicate
 * what is the reference for the vertical ordinate of a 3D point (ex. ellipsoid
 * surface, world geoid, local geoid...).
 *
 * @author Michaël Michaud, Jules Party
 */
public class VerticalCRS extends IdentifiableComponent implements
        CoordinateReferenceSystem {

    /**
     * A 1D {@link CoordinateSystem} whose {@link Axis} contains the
     * (ellipsoidal) height. The units used by this axis is meter.
     */
    public static CoordinateSystem HEIGHT_CS = new CoordinateSystem(
            new Axis[]{HEIGHT}, new Unit[]{METER});
    /**
     * A 1D {@link CoordinateSystem} whose {@link Axis} contains the altitude.
     * The units used by this axis is meter.
     */
    public static CoordinateSystem ALTITUDE_CS = new CoordinateSystem(
            new Axis[]{ALTITUDE}, new Unit[]{METER});
    /**
     * The {@link VerticalDatum} to which this
     * <code>CoordinateReferenceSystem</code> is refering.
     */
    private final VerticalDatum verticalDatum;
    /**
     * The {@link CoordinateSystem} used by this
     * <code>CoordinateReferenceSystem</code>.
     */
    private final CoordinateSystem coordinateSystem;

    /**
     * Create a new VerticalCRS.
     *
     * @param identifier the identifier of the VerticalCRS
     * @param datum the datum associated with the VerticalCRS
     * @param cs the coordinate system associated with the VerticalCRS
     */
    public VerticalCRS(Identifier identifier, VerticalDatum datum,
            CoordinateSystem cs) {
        super(identifier);
        this.verticalDatum = datum;
        this.coordinateSystem = cs;
    }

    /**
     * Create a new VerticalCRS.
     *
     * @param identifier the identifier of the VerticalCRS
     * @param datum the datum associated with the VerticalCRS
     * @param cs the coordinate system associated with the VerticalCRS
     */
    public VerticalCRS(Identifier identifier, VerticalDatum datum) {
        super(identifier);
        this.verticalDatum = datum;
        this.coordinateSystem = HEIGHT_CS;
    }

    /**
     * @see CoordinateReferenceSystem#getProjection()
     */
    @Override
    public Projection getProjection() {
        return null;
    }

    /**
     * @see CoordinateReferenceSystem#getType()
     */
    @Override
    public Type getType() {
        return CoordinateReferenceSystem.Type.VERTICAL;
    }

    /**
     * @see CoordinateReferenceSystem#getCoordinateSystem()
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the number of dimensions of the coordinate system.
     */
    public int getDimension() {
        return 1;
    }

    /**
     * Return the {@link org.cts.datum.VerticalDatum}.
     */
    @Override
    public VerticalDatum getDatum() {
        return verticalDatum;
    }

    /**
     * Return whether this coord is a valid coord in this
     * CoordinateReferenceSystem.
     *
     * @param coord standard coordinate for this CoordinateReferenceSystem
     * datums (ex. decimal degrees for geographic datums and meters for vertical
     * datums).
     */
    public boolean isValid(double[] coord) {
        return verticalDatum.getExtent().isInside(coord);
    }

    /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    public CoordinateOperation toGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // change the sign if the axis is oriented down
        if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.DOWN) {
            ops.add(new OppositeCoordinate(0));
        }
        // Convert from source unit to meters
        ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.METER));
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
    public CoordinateOperation fromGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // Convert from meters to source unit
        ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.METER));
        // change the sign if the axis is oriented down
        if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.DOWN) {
            ops.add(new OppositeCoordinate(0));
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
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