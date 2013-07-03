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
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

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

    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains easting
     * and second {@link Axis} contains northing. The unit used by these axes
     * is meter.
     */
    public static CoordinateSystem EN_CS = new CoordinateSystem(new Axis[]{
        EASTING, NORTHING}, new Unit[]{METER, METER});
    /**
     * A 2D {@link CoordinateSystem} whose first {@link Axis} contains northing
     * and second {@link Axis} contains easting. The unit used by these axes
     * is meter.
     */
    public static CoordinateSystem NE_CS = new CoordinateSystem(new Axis[]{
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
        super(identifier, datum, coordSys, null);
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
            Projection projection, Unit unit, String wktext) {
        super(identifier, datum, new CoordinateSystem(new Axis[]{EASTING,
            NORTHING}, new Unit[]{unit, unit}), wktext);
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
        super(identifier, datum, EN_CS, null);
        this.projection = projection;
    }

    /**
     * @see GeodeticCRS#getProjection()
     */
    @Override
    public Projection getProjection() {
        return projection;
    }

    /**
     * @see GeodeticCRS#getType()
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
        if (getCoordinateSystem().getUnit(0) != Unit.METER) {
            ops.add(UnitConversion.createUnitConverter(Unit.METER,
                    getCoordinateSystem().getUnit(0)));
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Returns true if object is equals to
     * <code>this</code>. Tests equality between identifiers, then tests if the
     * components of this ProjectedCRS are equals : the grids transformations,
     * the {@link GeodeticDatum}, the {@link CoordinateSystem} and the
     * {@link Projection}.
     *
     * @param object The object to compare this ProjectedCRS against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ProjectedCRS) {
            ProjectedCRS crs = (ProjectedCRS) o;
            if (getIdentifier().equals(crs.getIdentifier())) {
                return true;
            }
            boolean nadgrids;
            if (getGridTransformations()== null) {
                if (crs.getGridTransformations()== null) {
                    nadgrids = true;
                } else {
                    nadgrids = false;
                }
            } else {
                nadgrids = getGridTransformations().equals(crs.getGridTransformations());
            }

            return getDatum().equals(crs.getDatum()) && getProjection().equals(crs.getProjection())
                    && getCoordinateSystem().equals(crs.getCoordinateSystem()) && nadgrids;
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this ProjectedCRS.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.projection != null ? this.projection.hashCode() : 0);
        return hash;
    }
}
