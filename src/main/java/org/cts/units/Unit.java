/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
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
package org.cts.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.cts.units.Quantity.*;

/**
 * According to wikipedia, a unit of measurement is a standardised quantity of a physical
 * property, used as a factor to express occurring quantities of that property.
 * This interface is not a complete definition of what units are and what one can do with
 * them, but rather a simple definition for conversion purposes as used ni geodesy.
 * See JSR-275 for a complete package.
 * @author Micha�l Michaud
 * @version 0.1 (2007-09-10)
 */
public class Unit implements java.io.Serializable {

	// A table containing tables mapping symbols to unit for each quantity Class
	private static Map<Quantity, Map<String, Unit>> map =
		new HashMap<Quantity, Map<String, Unit>>();
	// A table containing base units for each quantity Class
	private static Map<Quantity, Unit> baseUnits = new HashMap<Quantity, Unit>();

	/**
	 * Static method returning a Unit from its symbol and quantity Class.
	 * @param quantity the Quantity measured by the Unit to be found
	 * @param symbol the symbol of the Unit to be found
	 * @return the Unit measuring this quantity and having this symbol
	 * or null if no Unit has been instanciated with this symbol for this
	 * quantity until now.
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
	 * @param quantity the quantity Class of the Unit to be found
	 * @return the base Unit for this quantity
	 */
	public static Unit getBaseUnit(Quantity quantity) {
		return baseUnits.get(quantity);
	}
	// Units used in Geodetic domain
	private ArrayList<String> names = new ArrayList<String>();

	public ArrayList<String> getNames() {
		return names;
	}
	public static final Unit RADIAN = new Unit(ANGLE, "radian", "rad");
	public static final Unit DEGREE = new Unit(ANGLE, "degree", Math.PI / 180d, "\u00B0");
	public static final Unit ARC_MINUTE = new Unit(ANGLE, "minute", Math.PI / 10800d, "'");
	public static final Unit ARC_SECOND = new Unit(ANGLE, "second", Math.PI / 648000d, "\"");
	public static final Unit GRAD = new Unit(ANGLE, "grad", Math.PI / 200d, "g");
	public static final Unit METER;
	public static final Unit MILLIMETER = new Unit(LENGTH, "millimeter", 0.001, "mm");
	public static final Unit CENTIMETER = new Unit(LENGTH, "centimeter", 0.01, "cm");
	public static final Unit DECIMETER = new Unit(LENGTH, "decimeter", 0.1, "dm");
	public static final Unit KILOMETER = new Unit(LENGTH, "kilometer", 1000d, "km");
	public static final Unit FOOT;
	public static final Unit YARD = new Unit(LENGTH, "yard", 0.9144, "yd");
	public static final Unit UNIT = new Unit(NODIM, "", "");
	public static final Unit SECOND = new Unit(TIME, "second", "s");
	public static ArrayList<Unit> units = new ArrayList<Unit>();

	static {
		units.add(RADIAN);
		units.add(DEGREE);
		units.add(ARC_MINUTE);
		units.add(ARC_SECOND);
		units.add(GRAD);

		ArrayList<String> unitNames = new ArrayList<String>();
		unitNames.add("meter");
		unitNames.add("metre");
		METER = new Unit(LENGTH, unitNames, "m");



		units.add(METER);
		units.add(MILLIMETER);
		units.add(CENTIMETER);
		units.add(DECIMETER);
		units.add(KILOMETER);

		unitNames = new ArrayList<String>();
		unitNames.add("foot");
		unitNames.add("foot_us");
		FOOT = new Unit(LENGTH, unitNames, 0.3048, "ft");

		units.add(FOOT);
		units.add(YARD);
		units.add(UNIT);
		units.add(SECOND);
	}

    private Quantity quantity;
	private String name;
	private double scale;
	private double offset = 0d;  // used for temperature units
	private String symbol;

	/**
	 * Creates a base unit for this quantity.
	 * @param quantity the quantity measured by this unit
	 * @param name the name of the unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, String name, String symbol) {
		this(quantity, name, 1d, 0d, symbol);
	}

	/**
	 * Creates a base unit for this quantity.
	 * @param quantity the quantity measured by this unit
	 * @param name the name of the unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, ArrayList<String> names, String symbol) {
		this(quantity, names, 1d, 0d, symbol);
	}

	/**
	 * Creates a new unit for this quantity.
	 * @param quantity the quantity measured by this unitDATUM["D_NTF",SPHEROID["Clarke_1880_IGN",6378249.2,293.46602]]
	 * @param name the name of the unit
	 * @param scale the scale factor of this unit compared to the base unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, String name, double scale, String symbol) {
		this(quantity, name, scale, 0d, symbol);
	}

	/**
	 * Creates a new unit for this quantity.
	 * @param quantity the quantity measured by this unitDATUM["D_NTF",SPHEROID["Clarke_1880_IGN",6378249.2,293.46602]]
	 * @param name the name of the unit
	 * @param scale the scale factor of this unit compared to the base unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, ArrayList<String> names, double scale, String symbol) {
		this(quantity, names, scale, 0d, symbol);
	}

	/**
	 * Creates a new Unit for the Quantity Q.
	 * @param quantity the quantity measured by this unit
	 * @param name the name of the unit
	 * @param scale the scale factor of this unit compared to the base unit
	 * @param offset the shift factor of this unit compared to the base unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, String name, double scale, double offset, String symbol) {
		this.quantity = quantity;
		this.name = name;
		this.scale = scale;
		this.offset = offset;
		this.symbol = symbol;
		Map<String, Unit> unts = map.get(quantity);
		if (unts == null) {
			map.put(quantity, new HashMap<String, Unit>());
		}
		map.get(quantity).put(symbol, this);
		if (scale == 1d && offset == 0d) {
			baseUnits.put(quantity, this);
		}
	}

	/**
	 * Creates a new Unit for the Quantity Q.
	 * @param quantity the quantity measured by this unit
	 * @param name the name of the unit
	 * @param scale the scale factor of this unit compared to the base unit
	 * @param offset the shift factor of this unit compared to the base unit
	 * @param symbol the symbol representing this unit
	 */
	public Unit(Quantity quantity, ArrayList<String> names, double scale, double offset, String symbol) {
		this.quantity = quantity;
		this.names = names;
		if (names != null & names.size() > 0) {
			this.name = names.get(0);
		}
		this.scale = scale;
		this.offset = offset;
		this.symbol = symbol;
		Map<String, Unit> unts = map.get(quantity);
		if (unts == null) {
			map.put(quantity, new HashMap<String, Unit>());
		}
		map.get(quantity).put(symbol, this);
		if (scale == 1d && offset == 0d) {
			baseUnits.put(quantity, this);
		}
	}

	public static Unit getUnit(String name) {
		for (Unit unit : units) {
			if (unit.getNames().size() == 0) {
				if (unit.getName().toLowerCase().equals(name)) {
					return unit;
				}
			} else {
				for (String oneName : unit.getNames()) {
					if (oneName.toLowerCase().equals(name)) {
						return unit;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Quantity measured by this Unit.
	 */
	public Quantity getQuantity() {
		return quantity;
	}

	/**
	 * Returns the name of this unit of measure.
	 */
	public String getName() {
		return name;
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
	 */
	public double toBaseUnit(double measure) {
		return measure * scale + offset;
	}

	/**
	 * Convert a measure from base unit(s) into this unit.
	 */
	public double fromBaseUnit(double measure) {
		return (measure - offset) / scale;
	}

	/**
	 * Return the preferred symbol to use with this unit.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Set factor or scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Return the base unit for measures of the same quantity.
	 * If the base unit has not been defined, one default base unit with an
	 * unknown name and an empty symbol is created.
	 */
	public Unit getBaseUnit() {
		Unit baseUnit = baseUnits.get(quantity);
		return baseUnits == null ? new Unit(quantity, "unknown", "") : baseUnit;
	}

	/**
	 * Is comparable returns true if quantity measured by this unit and
	 * quantity measured by anotherUnit are equals.
	 * @param anotherUnit another unit
	 * @return true if this unit is comparable with anotherUnit
	 */
	public boolean isComparable(Unit anotherUnit) {
		return quantity.equals(anotherUnit.getQuantity());
	}

	/**
	 * String representation of this Unit
	 */
	@Override
	public String toString() {
		return name + " (" + quantity
			+ (scale != 1.0 ? " : " + scale + getBaseUnit().getSymbol() : "") + ")";
	}
}
