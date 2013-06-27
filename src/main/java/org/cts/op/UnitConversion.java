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
package org.cts.op;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.units.Quantity;
import org.cts.units.Unit;

/**
 * Convert coordinates from a source unit to a target unit.
 *
 * @author Michaël Michaud
 */
public class UnitConversion extends AbstractCoordinateOperation {

    /**
     * Units used in source coordinates.
     */
    private Unit[] sourceUnit;
    /**
     * Units expected in the resulting coordinates.
     */
    private Unit[] targetUnit;

    /**
     * Creates a new unit converter.
     *
     * @param sourceUnits units used in source coordinates
     * @param targetUnits units expected in the resulting coordinates
     */
    private UnitConversion(Unit[] sourceUnit, Unit[] targetUnit) {
        super(new Identifier(UnitConversion.class,
                sourceUnit[0].getName() + " to " + targetUnit[0].getName()));
        assert sourceUnit.length == targetUnit.length : "sourceUnit[] and targetUnit[] must have the same size";
        this.sourceUnit = sourceUnit;
        this.targetUnit = targetUnit;
    }

    /**
     * Creates a new unit converter.
     *
     * @param sourceUnits units used in source coordinates
     * @param targetUnits units expected in the resulting coordinates
     */
    private UnitConversion(Identifier identifier, Unit[] sourceUnit, Unit[] targetUnit) {
        super(identifier);
        assert sourceUnit.length == targetUnit.length : "sourceUnit[] and targetUnit[] must have the same size";
        this.sourceUnit = sourceUnit;
        this.targetUnit = targetUnit;
    }

    /**
     * Returns a coordinate representing the same point as coord but with
     * different units.
     *
     * @param coord is an array containing one, two or three ordinates.
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord) throws IllegalCoordinateException {
        if (coord == null || coord.length == 0) {
            throw new CoordinateDimensionException("" + coord + " is an invalid coordinate");
        }
        int length = Math.min(coord.length, sourceUnit.length);
        for (int i = 0; i < length; i++) {
            if (Double.isNaN(coord[i])) {
                continue;
            }
            coord[i] = coord[i] * sourceUnit[i].getScale() / targetUnit[i].getScale();
        }
        return coord;
    }

    /**
     * Returns a coordinate representing the same point as coord but with
     * different units.
     *
     * @param coord is an array containing one, two or three ordinates.
     */
    /*
     * public void inverseTransform(double[] coord) throws
     * CoordinateDimensionException { if (coord == null || coord.length == 0)
     * throw new CoordinateDimensionException("" + coord + " is an invalid
     * coordinate"); int length = Math.min(coord.length, sourceUnit.length); for
     * (int i = 0 ; i < length ; i++) { if (Double.isNaN(coord[i])) continue;
     * coord[i] = coord[i] * targetUnit[i].getScale() /
     * sourceUnit[i].getScale(); } }
     */
    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        return new UnitConversion(targetUnit, sourceUnit);
    }

    /**
     * Creates a unit converter for homogeneous units (ex. XYZ or LAT-LON).
     *
     * @param sourceUnit unique source units used for 1-D, 2-D or 3-D source
     * coordinates
     * @param targetUnit unique target units expected in the resulting 1-D, 2-D
     * or 3-D coordinates
     */
    public static UnitConversion createUnitConverter(Unit sourceUnit, Unit targetUnit) {
        Identifier identifier;
        assert sourceUnit.isComparable(targetUnit) : "source and target units must be comparable";
        if (sourceUnit.getQuantity().equals(Quantity.LENGTH)) {
            identifier = new Identifier(UnitConversion.class,
                    sourceUnit.getName() + " to " + targetUnit.getName());
            return new UnitConversion(identifier,
                    new Unit[]{sourceUnit, sourceUnit, sourceUnit},
                    new Unit[]{targetUnit, targetUnit, targetUnit});
        } else if (sourceUnit.getQuantity().equals(Quantity.ANGLE)) {
            identifier = new Identifier(UnitConversion.class,
                    sourceUnit.getName() + " to " + targetUnit.getName());
            return new UnitConversion(identifier,
                    new Unit[]{sourceUnit, sourceUnit, Unit.METER},
                    new Unit[]{targetUnit, targetUnit, Unit.METER});
        } else {
            throw new IllegalArgumentException(
                    "Source or target unit represents an unknown quantity : "
                    + sourceUnit.getQuantity());
        }
    }

    /**
     * Create a unit converter for coordinates using different units for
     * planimetry and altimetry.
     *
     * @param planiSourceUnit source unit for latitude/longitude or
     * northing/easting coordinates.
     * @param planiTargetUnit target unit for latitude/longitude or
     * northing/easting coordinates.
     * @param altiSourceUnit source unit for height or altitude coordinate.
     * @param altiTargetUnit target unit for height or altitude coordinate.
     */
    public static UnitConversion createUnitConverter(Unit planiSourceUnit,
            Unit planiTargetUnit, Unit altiSourceUnit, Unit altiTargetUnit) {
        Identifier identifier;
        assert planiSourceUnit.isComparable(planiTargetUnit) : "source and target horizontal units must be comparable";
        assert altiSourceUnit.isComparable(altiTargetUnit) : "source and target vertical units must be comparable";
        if (planiSourceUnit.getQuantity().equals(Quantity.LENGTH)) {
            identifier = new Identifier(UnitConversion.class,
                    planiSourceUnit.getName() + " to " + planiTargetUnit.getName());
        } else if (planiSourceUnit.getQuantity().equals(Quantity.ANGLE)) {
            identifier = new Identifier(UnitConversion.class,
                    planiSourceUnit.getName() + " to " + planiTargetUnit.getName());
        } else {
            throw new IllegalArgumentException(
                    "Source or target unit represents an unknown quantity : " + planiSourceUnit.getQuantity());
        }
        return new UnitConversion(identifier,
                new Unit[]{planiSourceUnit, planiSourceUnit, altiSourceUnit},
                new Unit[]{planiTargetUnit, planiTargetUnit, altiTargetUnit});
    }
}
