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

import org.cts.op.CoordinateOperation;
import org.cts.Identifier;
import org.cts.op.NonInvertibleOperationException;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.ChangeCoordinateDimension;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import java.util.ArrayList;
import java.util.List;

import static org.cts.cs.Axis.EASTING;
import static org.cts.cs.Axis.NORTHING;
import static org.cts.units.Unit.METER;

/**
 * A Projected {@link org.cts.crs.CoordinateReferenceSystem} is a
 * CoordinateReferenceSystem based on a GeodeticDatum and a Projection
 * operation.
 *
 * @author Michaël Michaud
 */
public class ProjectedCRS extends GeodeticCRS {

    public static CoordinateSystem EN_CS = new CoordinateSystem(new Axis[]{
        EASTING, NORTHING}, new Unit[]{METER, METER});
    public static CoordinateSystem NE_CS = new CoordinateSystem(new Axis[]{
        NORTHING, EASTING}, new Unit[]{METER, METER});
    private Projection projection;
    private Unit angularUnit;

    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys, Projection projection) {
        super(identifier, datum, coordSys);
        this.projection = projection;
    }

    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            Projection projection, Unit unit, Unit angularUnit) {
        super(identifier, datum, new CoordinateSystem(new Axis[]{EASTING,
            NORTHING}, new Unit[]{unit, unit}));
        this.projection = projection;
        this.angularUnit = angularUnit;
    }

    public ProjectedCRS(Identifier identifier, GeodeticDatum datum,
            Projection projection) {
        super(identifier, datum, EN_CS);
        this.projection = projection;
    }

    @Override
    public Projection getProjection() {
        return projection;
    }

    /**
     * Return this CoordinateReferenceSystem Type
     */
    @Override
    public Type getType() {
        return Type.PROJECTED;
    }

    /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException {

        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // Convert units
        if (getCoordinateSystem().getUnit(0) != Unit.METER) {
            ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), METER));
        }
        // Add a third value to transform the geographic2D coord into a
        // geographic3D coord
        ops.add(ChangeCoordinateDimension.TO3D);
        // switch easting/northing coordinate if necessary
        if (getCoordinateSystem().getAxis(0) != EASTING) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Apply the inverse projection
        ops.add(projection.inverse());
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
        // Projection
        ops.add(projection);
        // switch easting/northing coordinate if necessary
        if (getCoordinateSystem().getAxis(0) != EASTING) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Unit conversion
        if (getCoordinateSystem().getAxis(0) != EASTING) {
            ops.add(UnitConversion.createUnitConverter(Unit.METER,
                    getCoordinateSystem().getUnit(0)));
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    public Unit getAngularUnit() {
        return angularUnit;
    }
}
