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
package org.cts.op;

import org.cts.CoordinateDimensionException;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.units.Quantity;
import org.cts.units.Unit;

import java.util.*;

/**
 * Convert coordinates from a source unit to a target unit.
 *
 * @author Michaël Michaud
 */
public class UnitConversion extends AbstractCoordinateOperation {

    /**
     * Units used in source coordinates.
     */
    private Unit[] sourceUnits;
    /**
     * Units expected in the resulting coordinates.
     */
    private Unit[] targetUnits;

    private final static Map<String,UnitConversion> unitConverters = new HashMap<String,UnitConversion>();

    /**
     * Creates a new unit converter.
     *
     * @param sourceUnits units used in source coordinates
     * @param targetUnits units expected in the resulting coordinates
     */
    private UnitConversion(Unit[] sourceUnits, Unit[] targetUnits) {
        super(new Identifier(CoordinateOperation.class,
                sourceUnits[0].getName() + " to " + targetUnits[0].getName()));
        this.sourceUnits = sourceUnits;
        this.targetUnits = targetUnits;
    }

    /**
     * Creates a new unit converter.
     *
     * @param sourceUnits units used in source coordinates
     * @param targetUnits units expected in the resulting coordinates
     */
    private UnitConversion(Identifier identifier, Unit[] sourceUnits, Unit[] targetUnits) {
        super(identifier);
        this.sourceUnits = sourceUnits;
        this.targetUnits = targetUnits;
        unitConverters.put(Arrays.toString(sourceUnits)+Arrays.toString(targetUnits),this);
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
            throw new CoordinateDimensionException(Arrays.toString(coord) + " is an invalid coordinate");
        }
        int length = Math.min(coord.length, sourceUnits.length);
        for (int i = 0; i < length; i++) {
            if (Double.isNaN(coord[i])) {
                continue;
            }
            coord[i] = coord[i] * sourceUnits[i].getScale() / targetUnits[i].getScale();
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
    public CoordinateOperation inverse() {
        return new UnitConversion(targetUnits, sourceUnits);
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
        if (sourceUnit.getQuantity().equals(Quantity.LENGTH)) {
            identifier = new Identifier(CoordinateOperation.class,
                    sourceUnit.getName() + " to " + targetUnit.getName());
            String key = Arrays.toString(new Unit[]{sourceUnit, sourceUnit, sourceUnit})
                    +Arrays.toString(new Unit[]{targetUnit, targetUnit, targetUnit});
            if (unitConverters.containsKey(key)) return unitConverters.get(key);
            UnitConversion converter = new UnitConversion(identifier,
                    new Unit[]{sourceUnit, sourceUnit, sourceUnit},
                    new Unit[]{targetUnit, targetUnit, targetUnit});
            unitConverters.put(key, converter);
            return converter;
        } else if (sourceUnit.getQuantity().equals(Quantity.ANGLE)) {
            identifier = new Identifier(CoordinateOperation.class,
                    sourceUnit.getName() + " to " + targetUnit.getName());
            String key = Arrays.toString(new Unit[]{sourceUnit, sourceUnit, Unit.METER})
                    +Arrays.toString(new Unit[]{targetUnit, targetUnit, Unit.METER});
            if (unitConverters.containsKey(key)) return unitConverters.get(key);
            UnitConversion converter = new UnitConversion(identifier,
                    new Unit[]{sourceUnit, sourceUnit, Unit.METER},
                    new Unit[]{targetUnit, targetUnit, Unit.METER});
            unitConverters.put(key, converter);
            return converter;
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
        if (planiSourceUnit.getQuantity().equals(Quantity.LENGTH)) {
            identifier = new Identifier(CoordinateOperation.class,
                    planiSourceUnit.getName() + " to " + planiTargetUnit.getName());
        } else if (planiSourceUnit.getQuantity().equals(Quantity.ANGLE)) {
            identifier = new Identifier(CoordinateOperation.class,
                    planiSourceUnit.getName() + " to " + planiTargetUnit.getName());
        } else {
            throw new IllegalArgumentException(
                    "Source or target unit represents an unknown quantity : " + planiSourceUnit.getQuantity());
        }
        String key = Arrays.toString(new Unit[]{planiSourceUnit, planiSourceUnit, altiSourceUnit})
                +Arrays.toString(new Unit[]{planiTargetUnit, planiTargetUnit, altiTargetUnit});
        if (unitConverters.containsKey(key)) return unitConverters.get(key);
        UnitConversion converter = new UnitConversion(identifier,
                new Unit[]{planiSourceUnit, planiSourceUnit, altiSourceUnit},
                new Unit[]{planiTargetUnit, planiTargetUnit, altiTargetUnit});
        unitConverters.put(key, converter);
        return converter;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UnitConversion) {
            UnitConversion converter = (UnitConversion)o;
            if (this.sourceUnits.length != converter.sourceUnits.length) return false;
            if (this.targetUnits.length != converter.targetUnits.length) return false;
            for (int i = 0 ; i < sourceUnits.length ; i++) {
                if (!sourceUnits[i].equals(converter.sourceUnits[i])) return false;
                if (!targetUnits[i].equals(converter.targetUnits[i])) return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = 7;
        for (Unit sourceUnit : sourceUnits) {
            hash += 13 * hash + sourceUnit.hashCode();
        }
        for (Unit targetUnit : targetUnits) {
            hash += 17 * hash + targetUnit.hashCode();
        }
        return hash;
    }

    /**
     * @return true if this operation does not change coordinates.
     */
    public boolean isIdentity() {
        return Arrays.equals(sourceUnits, targetUnits);
    }
}
