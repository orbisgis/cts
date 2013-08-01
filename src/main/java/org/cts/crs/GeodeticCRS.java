/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.projection.Projection;

/**
 * A geodetic {@link org.cts.crs.CoordinateReferenceSystem} is a coordinate
 * system based on a {@link org.cts.datum.GeodeticDatum}, a
 * {@link org.cts.datum.PrimeMeridian} and an {@link org.cts.Ellipsoid}. It is
 * an abstract class including Geographic3D, Geographic2D and Projected
 * CoordinateReferenceSystems.
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
     * A map of known grid transformations from this CRS, the key of the map is
     * the target datum of the nadgrid.
     */
    private Map<GeodeticDatum, List<CoordinateOperation>> nadgridsTransformations = new HashMap<GeodeticDatum, List<CoordinateOperation>>();
    /**
     * A map of known transformations from this CRS to other CRS.
     */
    private Map<CoordinateReferenceSystem, List<CoordinateOperation>> crsTransformations = new HashMap<CoordinateReferenceSystem, List<CoordinateOperation>>();

    /**
     * @see CoordinateReferenceSystem#getProjection()
     */
    @Override
    public Projection getProjection() {
        return null;
    }
    /**
     * The {@link CoordinateSystem} used by this
     * <code>CoordinateReferenceSystem</code>.
     */
    protected CoordinateSystem coordinateSystem;

    /**
     * Create a new GeodeticCRS.
     *
     * @param identifier the identifier of the GeodeticCRS
     * @param datum the datum associated with the GeodeticCRS
     * @param coordSys the coordinate system associated with the GeodeticCRS
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
    @Override
    abstract public Type getType();

    /**
     * @see CoordinateReferenceSystem#getCoordinateSystem()
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the number of dimensions of the {@link CoordinateSystem} used by
     * this
     * <code>CoordinateReferenceSystem</code>.
     */
    public int getDimension() {
        return coordinateSystem.getDimension();
    }

    /**
     * Returns the {@link Datum} to which this
     * <code>CoordinateReferenceSystem</code> is refering.
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
     * Add a Nadgrids Transformation for this CRS to the CRS using the key
     * {@link GeodeticDatum}.
     *
     * @param gd the target GeodeticDatum of the nadgrid transformation to add
     * @param coordOp the transformation linking this CRS and the *      * target <code>gd</code>
     */
    public void addGridTransformation(GeodeticDatum gd, CoordinateOperation coordOp) {
        if (nadgridsTransformations.get(gd) == null) {
            nadgridsTransformations.put(gd, new ArrayList<CoordinateOperation>());
        }
        nadgridsTransformations.get(gd).add(coordOp);
    }

    /**
     * Return the list of nadgrids transformation defined for this CRS.
     */
    public Map<GeodeticDatum, List<CoordinateOperation>> getGridTransformations() {
        return nadgridsTransformations;
    }

    /**
     * Return the list of nadgrids transformation defined for this CRS that used
     * the datum in parameter as target datum.
     *
     * @param datum the datum that must be a target for returned nadgrid
     * transformation
     */
    public List<CoordinateOperation> getGridTransformations(GeodeticDatum datum) {
        if (nadgridsTransformations.get(datum) == null && nadgridsTransformations.get(GeodeticDatum.WGS84) != null) {
            List<CoordinateOperation> opList = new ArrayList<CoordinateOperation>();
            opList.add(new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class),
                    nadgridsTransformations.get(GeodeticDatum.WGS84).get(0),
                    GeodeticDatum.WGS84.getCoordinateOperations(datum).get(0)));
            return opList;
        }
        return nadgridsTransformations.get(datum);
    }

    /**
     * Add a transformation for this CRS to the CRS in parameter.
     *
     * @param crs the target crs of the transformation to add
     * @param opList the list of operations linking <code>this</code> *      * and <code>crs</code>
     */
    public void addCRSTransformation(CoordinateReferenceSystem crs, List<CoordinateOperation> opList) {
        crsTransformations.put(crs, opList);
    }

    /**
     * Return the list of nadgrids transformations defined for this CRS.
     */
    public Map<CoordinateReferenceSystem, List<CoordinateOperation>> getCRSTransformations() {
        return crsTransformations;
    }

    /**
     * Return the list of transformations defined for this CRS to the CRS in
     * parameter.
     *
     * @param crs the crs that must be a target for returned list of operations
     */
    public List<CoordinateOperation> getCRSTransformations(CoordinateReferenceSystem crs) {
        return crsTransformations.get(crs);
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
     * Returns a WKT representation of the geodetic CRS.
     *
     */
    public abstract String toWKT();

    /**
     * Return a String representation of this Datum.
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
     * @param object The object to compare this GeodeticCRS against
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
            boolean nadgrids;
            if (getGridTransformations() == null) {
                if (crs.getGridTransformations() == null) {
                    nadgrids = true;
                } else {
                    nadgrids = false;
                }
            } else {
                nadgrids = getGridTransformations().equals(crs.getGridTransformations());
            }
            return getDatum().equals(crs.getDatum()) && nadgrids
                    && getCoordinateSystem().equals(crs.getCoordinateSystem());
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this GeodeticCRS.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.geodeticDatum != null ? this.geodeticDatum.hashCode() : 0);
        hash = 29 * hash + (this.coordinateSystem != null ? this.coordinateSystem.hashCode() : 0);
        return hash;
    }
}