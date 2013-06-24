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
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.CoordinateSwitch;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import java.util.ArrayList;
import java.util.List;

import static org.cts.cs.Axis.*;
import static org.cts.units.Unit.*;

/**
 * <p> A Geographic CoordinateReferenceSystem is a reference system based on a
 * GeodeticDatum and a 2D or 3D Ellipsoidal Coordinate System. </p> <p>
 *
 * @author Michaël Michaud
 */
public class Geographic3DCRS extends GeodeticCRS {

    public static CoordinateSystem LATLONH_RRM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{RADIAN,
        RADIAN, METER});
    public static CoordinateSystem LONLATH_RRM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{RADIAN,
        RADIAN, METER});
    public static CoordinateSystem LATLONH_DDM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{DEGREE,
        DEGREE, METER});
    public static CoordinateSystem LONLATH_DDM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{DEGREE,
        DEGREE, METER});
    public static CoordinateSystem LATLONH_GGM_CS = new CoordinateSystem(
            new Axis[]{LATITUDE, LONGITUDE, HEIGHT}, new Unit[]{GRAD,
        GRAD, METER});
    public static CoordinateSystem LONLATH_GGM_CS = new CoordinateSystem(
            new Axis[]{LONGITUDE, LATITUDE, HEIGHT}, new Unit[]{GRAD,
        GRAD, METER});

    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys) {
        super(identifier, datum, coordSys);
    }

    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum, Unit unit) {
        super(identifier, datum, LATLONH_DDM_CS);
        if (unit == RADIAN) {
            this.coordinateSystem = LATLONH_RRM_CS;
        } else if (unit == DEGREE) {
            this.coordinateSystem = LATLONH_DDM_CS;
        } else if (unit == GRAD) {
            this.coordinateSystem = LATLONH_GGM_CS;
        } else;
    }

    public Geographic3DCRS(Identifier identifier, GeodeticDatum datum) {
        super(identifier, datum, LATLONH_DDM_CS);
    }

    /**
     * Return this CoordinateReferenceSystem Type
     */
    @Override
    public Type getType() {
        return Type.GEOGRAPHIC3D;
    }

    @Override
    public Projection getProjection() {
        return null;
    }

    /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter() {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        // Convert from source unit to radians
        ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.RADIAN, getCoordinateSystem().getUnit(2),
                Unit.METER));
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0) == Axis.LONGITUDE) {
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
        // switch from LON/LAT to LAT/LON coordinate if necessary
        if (getCoordinateSystem().getAxis(0) == Axis.LONGITUDE) {
            ops.add(CoordinateSwitch.SWITCH_LAT_LON);
        }
        // Convert from radian to this coordinate system's units
        ops.add(UnitConversion.createUnitConverter(Unit.RADIAN,
                getCoordinateSystem().getUnit(0)));
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }
}
