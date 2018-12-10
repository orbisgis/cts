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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.cts.Identifiable;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.parser.prj.PrjWriter;

import static org.cts.units.Quantity.*;

/**
 * According to wikipedia, a unit of measurement is a standardised quantity of a
 * physical property, used as a factor to express occurring quantities of that
 * property. This interface is not a complete definition of what units are and
 * what one can do with them, but rather a simple definition for conversion
 * purposes as used ni geodesy. See JSR-275 for a complete package.
 *
 * @author Michaël Michaud, Jules Party
 */
public class Unit extends IdentifiableComponent implements java.io.Serializable {

    // A table containing tables mapping symbols to unit for each quantity Class
    private static Map<Quantity, Map<String, Unit>> map =
            new HashMap<Quantity, Map<String, Unit>>();
    // A table containing base units for each quantity Class
    private static Map<Quantity, Unit> baseUnits = new HashMap<Quantity, Unit>();

    /**
     * Static method returning a Unit from its symbol and quantity Class.
     *
     * @param quantity the Quantity measured by the Unit to be found
     * @param symbol the symbol of the Unit to be found
     * @return the Unit measuring this quantity and having this symbol or null
     * if no Unit has been instanciated with this symbol for this quantity until
     * now.
     */
    public static Unit getUnit(Quantity quantity, String symbol) {
        Map<String, Unit> unts = map.get(quantity);
        if (unts == null) {
            return null;
        }
        return unts.get(symbol);
    }

    /**
     * Static method returning the base Unit for this quantity Class.
     *
     * @param quantity the quantity Class of the Unit to be found
     * @return the base Unit for this quantity
     */
    public static Unit getBaseUnit(Quantity quantity) {
        return baseUnits.get(quantity);
    }

    /**
     * Return a list of the names of the unit (ex : metre, meter).
     */
    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Identifiable id : getAliases()) {
            if (!names.contains(id.getName())) {
                names.add(id.getName());
            }
        }
        return names;
    }
    public static final Unit RADIAN = new Unit(ANGLE, new Identifier("EPSG", "9101", "radian", "rad"));
    public static final Unit DEGREE = new Unit(ANGLE, Math.PI / 180d, new Identifier("EPSG", "9122", "degree", "\u00B0"));
    public static final Unit ARC_MINUTE = new Unit(ANGLE, Math.PI / 10800d, new Identifier("EPSG", "9103", "minute", "'"));
    public static final Unit ARC_SECOND = new Unit(ANGLE, Math.PI / 648000d, new Identifier("EPSG", "9104", "second", "\""));
    public static final Unit GRAD = new Unit(ANGLE, Math.PI / 200d, new Identifier("EPSG", "9105", "grad", "g"));
    public static final Unit METER;
    public static final Unit MILLIMETER;
    public static final Unit CENTIMETER;
    public static final Unit DECIMETER;
    public static final Unit KILOMETER;
    public static final Unit FOOT = new Unit(LENGTH, 0.3048, new Identifier("EPSG", "9002", "foot", "ft"));
    public static final Unit USFOOT = new Unit(LENGTH, 1200d / 3937d, new Identifier("EPSG", "9003", "foot_us", "us-ft"));
    public static final Unit YARD = new Unit(LENGTH, 0.9144, new Identifier("EPSG", "9096", "yard", "yd"));
    public static final Unit UNIT = new Unit(NODIM, new Identifier(Unit.class, "no dimension", ""));
    public static final Unit SECOND = new Unit(TIME, new Identifier(Unit.class, "second", "s"));

    static {
        Identifier id = new Identifier("EPSG", "9001", "metre", "m");
        ArrayList<Identifiable> aliases = new ArrayList<Identifiable>();
        aliases.add(id);
        METER = new Unit(LENGTH, new Identifier("EPSG", "9001", "meter", "m", "", aliases));
        id = new Identifier(Unit.class, "millimetre", "mm");
        aliases = new ArrayList<Identifiable>();
        aliases.add(id);
        MILLIMETER = new Unit(LENGTH, 0.001, new Identifier(Unit.class, "millimeter", "mm", aliases));
        id = new Identifier(Unit.class, "centimetre", "cm");
        aliases = new ArrayList<Identifiable>();
        aliases.add(id);
        CENTIMETER = new Unit(LENGTH, 0.01, new Identifier(Unit.class, "centimeter", "cm", aliases));
        id = new Identifier(Unit.class, "decimetre", "dm");
        aliases = new ArrayList<Identifiable>();
        aliases.add(id);
        DECIMETER = new Unit(LENGTH, 0.1, new Identifier(Unit.class, "decimeter", "dm", aliases));
        id = new Identifier("EPSG", "9036", "kilometre", "km");
        aliases = new ArrayList<Identifiable>();
        aliases.add(id);
        KILOMETER = new Unit(LENGTH, 1000d, new Identifier("EPSG", "9036", "kilometer", "km", "", aliases));
    }
    private Quantity quantity;
    private double scale;
    private double offset = 0d;  // used for temperature units

    /**
     * Creates a base unit for this quantity. The name of the identifier should
     * be the name of the unit and the short name of the identifier, the symbol
     * of this unit.
     *
     * @param quantity the quantity measured by this unit
     * @param id the identifier of this unit
     */
    public Unit(Quantity quantity, Identifier id) {
        this(quantity, 1d, 0d, id);
    }

    /**
     * Creates a new unit for this quantity. The name of the identifier should
     * be the name of the unit and the short name of the identifier, the symbol
     * of this unit.
     *
     * @param quantity the quantity measured by this unit
     * @param scale the scale factor of this unit compared to the base unit
     * @param id the identifier of this unit
     */
    public Unit(Quantity quantity, double scale, Identifier id) {
        this(quantity, scale, 0d, id);
    }

    /**
     * Creates a new Unit for the Quantity Q. The name of the identifier should
     * be the name of the unit and the short name of the identifier, the symbol
     * of this unit.
     *
     * @param quantity the quantity measured by this unit
     * @param scale the scale factor of this unit compared to the base unit
     * @param offset the shift factor of this unit compared to the base unit
     * @param id the identifier of this unit
     */
    public Unit(Quantity quantity, double scale, double offset,
            Identifier id) {
        super(id);
        this.quantity = quantity;
        this.scale = scale;
        this.offset = offset;
        this.registerUnit();
    }

    /**
     * Register the unit in different maps, one uses the unit's symbol (ie its
     * short name) as a key, and another its identifier. Moreover, if the unit
     * is a base unit, the method also register it in a specific map for base
     * units, this time the key is the quantity of the unit.
     */
    private void registerUnit() {
        Map<String, Unit> unts = map.get(quantity);
        if (unts == null) {
            map.put(quantity, new HashMap<String, Unit>());
        }
        map.get(quantity).put(getShortName(), this);
        if (scale == 1d && offset == 0d) {
            baseUnits.put(quantity, this);
        }
    }

    /**
     * Returns the Quantity measured by this Unit.
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Returns the scale of this unit compared to the base unit.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Returns the offset from base unit to this (ex. temperature).
     */
    public double getOffset() {
        return offset;
    }

    /**
     * Convert a measure from this unit into base unit(s).
     *
     * @param measure the measure to convert into base unit
     */
    public double toBaseUnit(double measure) {
        return measure * scale + offset;
    }

    /**
     * Convert a measure from base unit(s) into this unit.
     *
     * @param measure the measure to convert into this unit
     */
    public double fromBaseUnit(double measure) {
        return (measure - offset) / scale;
    }

    /**
     * Return the preferred symbol to use with this unit.
     */
    public String getSymbol() {
        return getShortName();
    }

    /**
     * Set factor or scale.
     *
     * @param scale the scale to set to the unit.
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Return the base unit for measures of the same quantity. If the base unit
     * has not been defined, one default base unit with an unknown name and an
     * empty symbol is created.
     */
    public Unit getBaseUnit() {
        Unit baseUnit = baseUnits.get(quantity);
        return baseUnits == null ? new Unit(quantity,
                new Identifier(Unit.class, Identifiable.UNKNOWN, "")) : baseUnit;
    }

    /**
     * Returns true if quantity measured by this unit and quantity measured by
     * anotherUnit are equals.
     *
     * @param anotherUnit another unit
     */
    public boolean isComparable(Unit anotherUnit) {
        return quantity.equals(anotherUnit.getQuantity());
    }

    /**
     * Returns a WKT representation of the unit.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("UNIT[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(PrjWriter.prettyRound(this.getScale(), 1e-11));
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * String representation of this Unit.
     */
    @Override
    public String toString() {
        return getName() + " (" + quantity
                + (scale != 1.0 ? " : " + scale + getBaseUnit().getSymbol() : "") + ")";
    }

    /**
     * Returns true if this Unit can be considered as equals to another one.
     *
     * @param o the object to compare this Unit against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Unit) {
            Unit unit = (Unit) o;
            return quantity.equals(unit.getQuantity()) && (scale == unit.getScale()) && (offset == unit.getOffset());
        }
        return false;
    }

    /**
     * Returns the hash code for this Unit.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.scale) ^ (Double.doubleToLongBits(this.scale) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.offset) ^ (Double.doubleToLongBits(this.offset) >>> 32));
        return hash;
    }
}
