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

import org.cts.Identifiable;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.Projection;
import org.cts.units.Unit;

import static org.cts.cs.Axis.ALTITUDE;
import static org.cts.cs.Axis.HEIGHT;
import static org.cts.units.Unit.METER;

/**
 * A vertical {@link org.cts.crs.CoordinateReferenceSystem} is a
 * CoordinateReferenceSystem based on a VerticalDatum. It is used to indicate
 * what is the reference for the vertical ordinate of a 3D point (ex. ellipsoid
 * surface, world geoid, local geoid...).
 *
 * @author Michaël Michaud, Jules Party
 */
public class VerticalCRS extends IdentifiableComponent implements
        CoordinateReferenceSystem {

    /**
     * A 1D {@link CoordinateSystem} whose {@link Axis} contains the
     * (ellipsoidal) height. The units used by this axis is meter.
     */
    public static final CoordinateSystem HEIGHT_CS = new CoordinateSystem(
            new Axis[]{HEIGHT}, new Unit[]{METER});

    /**
     * A 1D {@link CoordinateSystem} whose {@link Axis} contains the altitude.
     * The units used by this axis is meter.
     */
    public static CoordinateSystem ALTITUDE_CS = new CoordinateSystem(
            new Axis[]{ALTITUDE}, new Unit[]{METER});

    /**
     * The {@link VerticalDatum} to which this
     * <code>CoordinateReferenceSystem</code> is refering.
     */
    private final VerticalDatum verticalDatum;

    /**
     * The {@link CoordinateSystem} used by this
     * <code>CoordinateReferenceSystem</code>.
     */
    private final CoordinateSystem coordinateSystem;

    /**
     * Create a new VerticalCRS.
     *
     * @param identifier the identifier of the VerticalCRS
     * @param datum the datum associated with the VerticalCRS
     * @param cs the coordinate system associated with the VerticalCRS
     */
    public VerticalCRS(Identifier identifier, VerticalDatum datum,
            CoordinateSystem cs) {
        super(identifier);
        this.verticalDatum = datum;
        this.coordinateSystem = cs;
    }

    /**
     * Create a new VerticalCRS.
     *
     * @param identifier the identifier of the VerticalCRS
     * @param datum the datum associated with the VerticalCRS
     */
    public VerticalCRS(Identifier identifier, VerticalDatum datum) {
        super(identifier);
        this.verticalDatum = datum;
        this.coordinateSystem = HEIGHT_CS;
    }

    /**
     * @see CoordinateReferenceSystem#getProjection()
     */
    @Override
    public Projection getProjection() {
        return null;
    }

    /**
     * @see CoordinateReferenceSystem#getType()
     */
    @Override
    public Type getType() {
        return CoordinateReferenceSystem.Type.VERTICAL;
    }

    /**
     * @see CoordinateReferenceSystem#getCoordinateSystem()
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Return the {@link org.cts.datum.VerticalDatum}.
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
     * @return 
     */
    public boolean isValid(double[] coord) {
        return verticalDatum.getExtent().isInside(coord);
    }


    /**
     * Returns a WKT representation of the vertical CRS.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("VERT_CS[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getDatum().toWKT());
        w.append(',');
        w.append(this.getCoordinateSystem().getUnit(0).toWKT());
        for (int i = 0; i < this.getCoordinateSystem().getDimension(); i++) {
            w.append(',');
            w.append(this.getCoordinateSystem().getAxis(i).toWKT());
        }
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Return a String representation of this Datum.
     * @return 
     */
    @Override
    public String toString() {
        return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName() + " ("
                + getShortName() + ")";
    }
}