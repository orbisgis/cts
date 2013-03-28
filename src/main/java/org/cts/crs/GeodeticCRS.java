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
package org.cts.crs;

import org.cts.CoordinateOperation;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.NonInvertibleOperationException;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.projection.Projection;

/**
 * A CoordinateReferenceSystem is a system which has to be associated to
 * coordinates in order to establish a non ambiguous relation between those
 * coordinates and an absolute position.<p> There are several kinds of
 * CoordinateReferenceSystem based on 1D, 2D or 3D
 * {@link fr.cts.cs.CoordinateSystem}s. CoordinateReferenceSystem are defined by
 * a {@link fr.cts.datum.Datum} which is an absolute reference.
 *
 * @author Michael Michaud
 */
public abstract class GeodeticCRS extends IdentifiableComponent
        implements CoordinateReferenceSystem {

    private GeodeticDatum geodeticDatum;

    @Override
    public Projection getProjection() {
        return null;
    }
    protected CoordinateSystem coordinateSystem;

    /**
     * Create a new GeodeticCRS.
     */
    protected GeodeticCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordinateSystem) {
        super(identifier);
        this.geodeticDatum = datum;
        this.coordinateSystem = coordinateSystem;
    }

    /**
     * Return this CoordinateReferenceSystem Type
     */
    @Override
    abstract public Type getType();

    /**
     * Returns the coordinate system of this CoordinateReferenceSystem.
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the number of dimensions of the coordinate system.
     */
    public int getDimension() {
        return coordinateSystem.getDimension();
    }

    /**
     * Return the {@link fr.cts.datum.GeodeticDatum}.
     */
    @Override
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
    public boolean isValid(double[] coord) {
        return geodeticDatum.getExtent().isInside(coord);
    }

    /**
     * Creates a CoordinateOperation object to convert coordinates from this
     * CoordinateReferenceSystem to a GeographicReferenceSystem based on the
     * same horizonal datum and vertical datum, and using normal SI units in the
     * following order : latitude (rad), longitude (rad) height/altitude (m).
     */
    abstract public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException;

    /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * GeographicReferenceSystem based on the same horizonal datum and vertical
     * datum, and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height/altitude (m) to this CoordinateReferenceSystem.
     */
    abstract public CoordinateOperation fromGeographicCoordinateConverter()
            throws NonInvertibleOperationException;

    /**
     * Return a String representation of this Datum.
     */
    @Override
    public String toString() {
        return "[" + getNamespace() + ":" + getId() + "] " + getName();
    }
}