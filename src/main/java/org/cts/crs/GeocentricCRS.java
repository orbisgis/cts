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

import org.cts.op.CoordinateOperation;
import org.cts.Identifier;
import org.cts.op.NonInvertibleOperationException;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.Geocentric2Geographic;
import org.cts.op.Geographic2Geocentric;
import org.cts.op.LongitudeRotation;
import org.cts.units.Unit;

import java.util.ArrayList;
import java.util.List;

import static org.cts.cs.Axis.*;
import static org.cts.units.Unit.METER;

/**
 * <p> A geocentric CoordinateReferenceSystem is a 3D cartesian coordinate
 * system centered on the Earth center of mass. </p>
 * <p> Note that the Earth center of mass is not something easily identifiable,
 * especially before the advent of space geodesy, which partially explains
 * there are so many datums, and therefore, so many CoordinateReferenceSystems</p>
 *
 * @author Michaël Michaud
 */
public class GeocentricCRS extends GeodeticCRS {

    //private CRSAuthority crsAuthority;

    public boolean hasProjection() {
        return false;
    }
    // //////////////////////////////////////////////////////////////////////////
    // ////////////// CLASS ATTRIBUTES ////////////////
    // //////////////////////////////////////////////////////////////////////////
    public static final CoordinateSystem GEOCENTRIC_CS = new CoordinateSystem(
            new Axis[]{X, Y, Z}, new Unit[]{METER, METER, METER});

    // //////////////////////////////////////////////////////////////////////////
    // ////////////// ATTRIBUTES & GET/SET METHODS ////////////////
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Returns this CoordinateReferenceSystem Type
     */
    @Override
    public Type getType() {
        return Type.GEOCENTRIC;
    }

    // //////////////////////////////////////////////////////////////////////////
    // ////////////// CONSTRUCTOR ////////////////
    // //////////////////////////////////////////////////////////////////////////
    // This constructor is made private.
    // This means that GeocentricCoordinateSystem always use the same metric
    // Coordinate System
    public GeocentricCRS(Identifier identifier, GeodeticDatum datum,
            CoordinateSystem coordSys) {
        super(identifier, datum, coordSys);
    }

    public GeocentricCRS(Identifier identifier, GeodeticDatum datum) {
        super(identifier, datum, GEOCENTRIC_CS);
    }

    // //////////////////////////////////////////////////////////////////////////
    // ////////////// OBJECT METHODS ////////////////
    // //////////////////////////////////////////////////////////////////////////
    /**
     * @see GeodeticCRS#toGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        ops.add(new Geocentric2Geographic(getDatum().getEllipsoid()));
        if (!getDatum().getPrimeMeridian().equals(PrimeMeridian.GREENWICH)) {
            ops.add(LongitudeRotation.getLongitudeRotationTo(getDatum().getPrimeMeridian()));
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * @see GeodeticCRS#fromGeographicCoordinateConverter()
     */
    @Override
    public CoordinateOperation fromGeographicCoordinateConverter()
            throws NonInvertibleOperationException {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();
        if (!getDatum().getPrimeMeridian().equals(PrimeMeridian.GREENWICH)) {
            ops.add(LongitudeRotation.getLongitudeRotationFrom(getDatum().getPrimeMeridian()));
        }
        ops.add(new Geographic2Geocentric(getDatum().getEllipsoid()));
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

}
