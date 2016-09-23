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
package org.cts.crs;

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationException;
import org.cts.op.projection.Projection;

/**
 * A {@link org.cts.crs.CoordinateReferenceSystem} based on a
 * {@link org.cts.datum.GeodeticDatum}, including a {@link org.cts.datum.PrimeMeridian}
 * and an {@link org.cts.datum.Ellipsoid} definition. It is an abstract class including
 * Geographic3D, Geographic2D and Projected CoordinateReferenceSystems.
 *
 * @author Michaël Michaud
 */
public abstract class GeodeticCRS extends IdentifiableComponent
        implements CoordinateReferenceSystem {

    /**
     * The {@link GeodeticDatum} to which this
     * <code>CoordinateReferenceSystem</code> is refering.
     */
    private GeodeticDatum geodeticDatum;

    /**
     * @see CoordinateReferenceSystem#getProjection()
     */
    public Projection getProjection() {
        return null;
    }

    /**
     * The {@link CoordinateSystem} used by this
     * <code>CoordinateReferenceSystem</code>.
     */
    protected CoordinateSystem coordinateSystem;

    /**
     * Creates a new GeodeticCRS.
     *
     * @param identifier the identifier of the GeodeticCRS
     * @param datum the datum associated with the GeodeticCRS
     * @param coordinateSystem the coordinate system associated with the GeodeticCRS
     */
    protected GeodeticCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordinateSystem) {
        super(identifier);
        this.geodeticDatum = datum;
        this.coordinateSystem = coordinateSystem;
    }

    /**
     * @see CoordinateReferenceSystem#getType()
     */
    abstract public Type getType();

    /**
     * @see CoordinateReferenceSystem#getCoordinateSystem()
     */
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the {@link org.cts.datum.Datum} to which this
     * <code>CoordinateReferenceSystem</code> is refering.
     */
    public GeodeticDatum getDatum() {
        return geodeticDatum;
    }

    /**
     * Return whether this coord is a valid coord in this
     * CoordinateReferenceSystem.
     *
     * @param coord standard coordinate for this CoordinateReferenceSystem
     * datums (ex. decimal degrees for geographic datums and meters for vertical
     * datums).
     */
    //@TODO Clarify : geodeticDatum.getExtent().isInside does not use coordinates
    // expressed with this CoordinateReferenceSystem
    // Do we have to convert coord to a decimal degree or radian Geographic CRS first ?
    //public boolean isValid(double[] coord) {
    //    return geodeticDatum.getExtent().isInside(coord);
    //}

    /**
     * Creates a CoordinateOperation object to convert coordinates from this
     * CoordinateReferenceSystem to a {@link org.cts.crs.Geographic3DCRS} based on
     * the same {@link org.cts.datum.GeodeticDatum}, and using normal SI units in the
     * following order : latitude (rad), longitude (rad) height (m).
     * @return 
     * @throws org.cts.op.CoordinateOperationException 
     */
    abstract public CoordinateOperation toGeographicCoordinateConverter()
            throws CoordinateOperationException;

    /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * {@link org.cts.crs.Geographic3DCRS} based on the same {@link org.cts.datum.GeodeticDatum},
     * and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height (m) to this CoordinateReferenceSystem.
     * @return 
     * @throws org.cts.op.CoordinateOperationException 
     */
    abstract public CoordinateOperation fromGeographicCoordinateConverter()
            throws CoordinateOperationException;

    /**
     * Returns a WKT representation of the geodetic CRS.
     */
    public abstract String toWKT();

    /**
     * Return a String representation of this Datum.
     * @return 
     */
    @Override
    public String toString() {
        return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName();
    }

    /**
     * Returns true if object is equals to
     * <code>this</code>. Tests equality between identifiers, then tests if the
     * components of this ProjectedCRS are equals : the grids transformations,
     * the {@link GeodeticDatum}, the {@link CoordinateSystem}.
     *
     * @param o The object to compare this GeodeticCRS against
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GeodeticCRS) {
            GeodeticCRS crs = (GeodeticCRS) o;
            if (!getType().equals(crs.getType())) {
                return false;
            }
            if (getIdentifier().equals(crs.getIdentifier())) {
                return true;
            }
            return getDatum().equals(crs.getDatum())
                    && getCoordinateSystem().equals(crs.getCoordinateSystem());
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this GeodeticCRS.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.geodeticDatum != null ? this.geodeticDatum.hashCode() : 0);
        hash = 29 * hash + (this.coordinateSystem != null ? this.coordinateSystem.hashCode() : 0);
        return hash;
    }
}