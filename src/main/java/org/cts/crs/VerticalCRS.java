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

import static org.cts.units.Unit.METER;
import static org.cts.cs.Axis.ALTITUDE;
import static org.cts.cs.Axis.HEIGHT;
import org.cts.Authority;
import org.cts.CRSAuthority;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.units.Unit;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.Projection;

/**
 * A CoordinateReferenceSystem is a system which has to be associated to
 * coordinates in order to establish a non ambiguous relation between those
 * coordinates and an absolute position. <p> There are several kinds of
 * CoordinateReferenceSystem based on 1D, 2D or 3D
 * {@link fr.cts.cs.CoordinateSystem}s. CoordinateReferenceSystem are defined by
 * a {@link fr.cts.datum.Datum} which is an absolute reference.
 *
 * @author Michael Michaud
 */
public class VerticalCRS extends IdentifiableComponent implements
        CoordinateReferenceSystem {

    public static CoordinateSystem HEIGHT_CS = new CoordinateSystem(
            new Axis[]{HEIGHT}, new Unit[]{METER});
    public static CoordinateSystem ALTITUDE_CS = new CoordinateSystem(
            new Axis[]{ALTITUDE}, new Unit[]{METER});
    private VerticalDatum verticalDatum;
    private CoordinateSystem coordinateSystem;
    private CRSAuthority crsAuthority;

    /**
     * Create a new VerticalCRS.
     */
    public VerticalCRS(Identifier identifier, VerticalDatum datum,
            CoordinateSystem cs) {
        super(identifier);
        this.verticalDatum = datum;
        this.coordinateSystem = cs;
    }

    @Override
    public Projection getProjection() {
        return null;
    }

    /**
     * Returns this CoordinateReferenceSystem Type
     */
    @Override
    public Type getType() {
        return CoordinateReferenceSystem.Type.VERTICAL;
    }

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
        return 1;
    }

    /**
     * Return the {@link fr.cts.datum.VerticalDatum}.
     */
    @Override
    public VerticalDatum getDatum() {
        return verticalDatum;
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
        return verticalDatum.getExtent().isInside(coord);
    }

    /**
     * Return a String representation of this Datum.
     */
    @Override
    public String toString() {
        return "[" + getNamespace() + ":" + getId() + "] " + getName() + " ("
                + getShortName() + ")";
    }

    @Override
    public Authority getAuthority() {
        return crsAuthority;
    }

    @Override
    public void setAuthority(CRSAuthority crsAuthority) {
        this.crsAuthority = crsAuthority;
    }
}