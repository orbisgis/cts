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
package org.cts.datum;

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.Extent;

/**
 * A simple implementation of {@link Datum} interface with a default
 * constructor.
 *
 * @author Michaël Michaud
 */
public abstract class AbstractDatum extends IdentifiableComponent
        implements Datum {

    /**
     * The valid {@link Extent} of this Datum.
     */
    private Extent extent;

    /**
     * The description of this Datum origin.
     */
    private String origin;

    /**
     * The realization epoch of this Datum as a String.
     */
    private String epoch;

    /**
     * Creates a new Datum.
     *
     * @param identifier the identifier of this Datum
     * @param extent valid domain extent (extent definition depends on the kind
     * of Datum)
     * @param origin description of the origin or anchor point of this Datum.
     * @param epoch epoch of this Datum realization
     */
    protected AbstractDatum(Identifier identifier, Extent extent, String origin, String epoch) {
        super(identifier);
        this.extent = extent;
        this.origin = origin;
        this.epoch = epoch;
    }

    /**
     * Returns the valid extent of this Datum.
     */
    @Override
    public Extent getExtent() {
        return extent;
    }

    /**
     * Returns the description of this Datum origin.
     */
    @Override
    public String getOrigin() {
        return origin;
    }

    /**
     * Returns the realization epoch of this Datum as a String.
     */
    @Override
    public String getEpoch() {
        return epoch;
    }

    /**
     * Returns a String representation of this Datum.
     */
    @Override
    public String toString() {
        return getIdentifier().toString();
    }
}
