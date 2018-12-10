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
package org.cts.units;

/**
 *
 * @author Michaël Michaud
 */
public class Measure implements java.io.Serializable {

    private double svalue;  // measure in base units stored for performance
    private Number value;   // precise measure
    private Unit unit;      // unit of the precise measure
    private double precision = Double.NaN;

    /**
     * Creates a new Measure.
     *
     * @param value the value of the measure
     * @param unit the unit used to express the measure
     */
    public Measure(double value, Unit unit) {
        this(value, unit, Double.NaN);
    }

    /**
     * Creates a new Measure.
     *
     * @param value the value of the measure
     * @param unit the unit used to express the measure
     */
    public Measure(Number value, Unit unit) {
        this(value, unit, Double.NaN);
    }

    /**
     * Creates a new Measure.
     *
     * @param value the value of the measure
     * @param unit the unit used to express the measure
     * @param precision of this measure. If different from NaN, this parameter
     * can be used to express a limit in this measure precision for future
     * calculations.
     */
    public Measure(Number value, Unit unit, double precision) {
        this.value = value;
        this.svalue = unit.toBaseUnit(value.doubleValue());
        this.unit = unit;
        this.precision = precision;
    }

    /**
     * Gets the value of this measure expressed as a double in the base unit.
     * The S stands for 'SI' or for 'Standard'. Prefer this method for
     * calculation and performance, and getValue for precision.
     */
    public double getSValue() {
        return svalue;
    }

    /**
     * Gets the value of this measure.
     */
    public Number getValue() {
        return value;
    }

    /**
     * Set Value of this measure.
     *
     * @param value the new Value to set to this Measure
     */
    public void setValue(Number value) {
        this.value = value;
        this.svalue = unit.toBaseUnit(value.doubleValue());
    }

    /**
     * Gets the unit of this measure.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Set the unit of this measure.
     *
     * @param unit the new Unit to set to this Measure
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Gets the Quantity measured.
     */
    public Quantity getQuantity() {
        return unit.getQuantity();
    }

    /**
     * Gets the precision of this measure.
     */
    public double getPrecision() {
        return precision;
    }

    /**
     * Gets the same Measure using the base unit. The measure value in the base
     * unit is a Double.
     */
    public Measure toBaseUnit() {
        if (Double.isNaN(precision)) {
            return new Measure(svalue, unit.getBaseUnit(), Double.NaN);
        }
        return new Measure(svalue, unit.getBaseUnit(), unit.toBaseUnit(precision));
    }

    /**
     * Converts this measure to another unit. The measure value in the new unit
     * system is a Double.
     *
     * @param unit the new unit for the returned measure
     */
    public Measure convert(Unit unit) throws IllegalArgumentException {
        if (Double.isNaN(precision)) {
            return new Measure(unit.fromBaseUnit(svalue), unit);
        } else {
            return new Measure(unit.fromBaseUnit(svalue), unit,
                    unit.fromBaseUnit(this.unit.toBaseUnit(precision)));
        }
    }

    /**
     * Returns the measure as a String.
     */
    @Override
    public String toString() {
        String symb = unit.getSymbol();
        return value.toString() + (symb == null || symb.equals("") ? " " : symb)
                + (Double.isNaN(precision) ? "" : " \u00b1" + precision);
    }

    /**
     * Get compatible Unit for this unit It uses after WKT parsing. Get the
     * compatible Unit parsed from WKT.
     *
     * @param units : list of possible compatible Unit
     */
    public Unit getCompatibleUnit(Unit... units) {
        for (Unit unt : units) {
            if (unt.getSymbol().equals(getUnit().getSymbol())) {
                return unt;
            }
        }
        return null;
    }
}
