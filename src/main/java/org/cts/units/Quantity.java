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
 * According to wikipedia, quantity is a kind of property which exists as
 * magnitude or multitude. Mass, time, distance, heat, and angular separation
 * are among the familiar examples of quantitative properties. This interface
 * has no particular method, and let programmers free to implement Quantities as
 * they want. A internal Factory class has been added to help creating
 * Quantities from a simple String. To use it, just write :
 * <pre>Quantity SPEED = Quantity.Factory.create("Speed");</pre> Otherwise, you
 * can create your own implementation of Quantity :
 * <pre>
 * public class Speed implements Quantity {
 *     // your implementation
 * }
 * </pre> Note that two quantities are considered as comparable if their
 * toString method return equalsIgnoreCase values.
 *
 * For a complete package about units, quantities and measurements, see JSR-275.
 *
 * @author Michaël Michaud
 */
public interface Quantity {

    public Quantity LENGTH = Factory.create("Length");
    public Quantity ANGLE = Factory.create("Angle");
    public Quantity NODIM = Factory.create("Dimensionless");
    public Quantity TIME = Factory.create("Time");

    /**
     * A factory to easily create
     * <code>Quantities</code> from simple Strings.
     */
    public class Factory {

        /**
         * Creates a new Quantity from a String.
         *
         * @param name the name of the Quantity to be created
         * @return a new Quantity object
         */
        public static Quantity create(final String name) {
            return new Quantity() {
                @Override
                public String toString() {
                    return name;
                }

                @Override
                public boolean equals(Object o) {
                    if (!(o instanceof Quantity)) {
                        return false;
                    }
                    return toString().equalsIgnoreCase(o.toString());
                }

                @Override
                public int hashCode() {
                    return 5;
                }
            };
        }
    }
}
