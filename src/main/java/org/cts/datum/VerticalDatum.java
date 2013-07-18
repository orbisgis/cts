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
package org.cts.datum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.cts.op.CoordinateOperation;
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;
import org.cts.op.Identity;
import org.cts.op.transformation.Altitude2EllipsoidalHeight;

/**
 * <p>Vertical datum are used to determine elevation. They are generally based
 * upon a gravity model.</p>
 *
 * @author Michaël Michaud, Jules Party
 */
public class VerticalDatum extends AbstractDatum {

    private final static Map<Identifier, VerticalDatum> datums =
            new HashMap<Identifier, VerticalDatum>();
    /**
     * WGS84VD stands for WGS84 Vertical Datum. This not a real datum, but a
     * reference used to transform 3D Ellipsoidal coordinates into coordinates
     * based on a compound Datum made of a Geodetic datum + a Vertical Datum.
     */
    public final static VerticalDatum WGS84VD = new VerticalDatum(
            new Identifier(VerticalDatum.class, "WGS84 Ellipsoid Surface"),
            GeographicExtent.WORLD,
            "Surface of the reference Ellipsoid for WGS 1984",
            "1984", Type.ELLIPSOIDAL, null, GeodeticDatum.WGS84);

    /**
     * Vertical Datum classification based on the surface type.
     */
    public static enum Type {

        GEOIDAL, //WKT code 2005
        ELLIPSOIDAL, //WKT code 2002
        DEPTH, //WKT code 2006
        BAROMETRIC, //WKT code 2003
        ORTHOMETRIC, //WKT code 2001
        OTHER_SURFACE //WKT code 2000
    };
    /**
     * The type of this vertical datum. Default is "geoidal".
     */
    private final Type type;
    /**
     * The operation converting altitude of the vertical datum into ellipsoidal
     * height.
     */
    private final CoordinateOperation alti2ellpsHeight;
    /**
     * The ellipsoid associated with the vertical datum. It can be the ellipsoid
     * defining th ellipsoidal height or the ellipsoid of the geodetic datum
     * used by the grid transformation binding altitude and height.
     */
    private final Ellipsoid ellps;

    /**
     * Creates a new Datum.
     *
     * @param name name of this vertical datum
     */
    public VerticalDatum(String name) {
        super(new Identifier(VerticalDatum.class, name),
                GeographicExtent.WORLD, null, null);
        this.type = Type.GEOIDAL;
        this.registerDatum();
        this.alti2ellpsHeight = null;
        this.ellps = null;
        //addCoordinateOperation(WGS84VD, altitude2EllipsoidalHeight);
    }

    /**
     * Creates a new VerticalDatum.
     *
     * @param identifier identifier.
     * @param extent this datum extension
     * @param origin origin decription this datum
     * @param epoch realization epoch of this datum
     * @param type the type of coordinate stored in this VerticalDatum
     * @param altitudeGrid the name of the grid file used to convert altitude in
     * ellipsoidal height
     * @param gd the GeodeticDatum associated to the grid
     */
    public VerticalDatum(Identifier identifier, GeographicExtent extent,
            String origin, String epoch, Type type, String altitudeGrid, GeodeticDatum gd) {
        super(identifier, extent, origin, epoch);
        this.type = type;
        if (gd != null && altitudeGrid != null) {
            this.alti2ellpsHeight = new Altitude2EllipsoidalHeight(new Identifier(Altitude2EllipsoidalHeight.class, altitudeGrid), altitudeGrid, gd);
            this.ellps = gd.getEllipsoid();
        } else if (gd != null && type == Type.ELLIPSOIDAL) {
            this.alti2ellpsHeight = Identity.IDENTITY;
            this.ellps = gd.getEllipsoid();
        } else {
            this.alti2ellpsHeight = null;
            this.ellps = null;
        }
        this.registerDatum();
    }

    /**
     * Return the type of this vertical datum. Default is "geoidal".
     */
    public Type getType() {
        return type;
    }

    /**
     * Return the operation converting altitude of the vertical datum into
     * ellipsoidal height.
     */
    public CoordinateOperation getAltiToEllpsHeight() {
        return alti2ellpsHeight;
    }

    /**
     * Register a datum in {@link HashMap} {@code datums} using its
     * {@link Identifier} as a key.
     */
    private void registerDatum() {
        datums.put(getIdentifier(), this);
    }

    /**
     * Return a collection of all the registered vertical datums.
     */
    public static Collection<VerticalDatum> getAvailableDatums() {
        return datums.values();
    }

    /**
     * Return the Datum with idEPSG identifier.
     */
    public static VerticalDatum getDatum(Identifier identifier) {
        return datums.get(identifier);
    }

    /**
     * @see Datum#getEllipsoid()
     */
    public Ellipsoid getEllipsoid() {
        return ellps;
    }

    /**
     * @see Datum#getToWGS84()
     */
    public CoordinateOperation getToWGS84() {
        if (alti2ellpsHeight instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight eH2A = (Altitude2EllipsoidalHeight) alti2ellpsHeight;
            return eH2A.getAssociatedDatum().getToWGS84();
        }
        return null;
    }

    /**
     * @see Datum#getPrimeMeridian()
     */
    public PrimeMeridian getPrimeMeridian() {
        if (alti2ellpsHeight instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight eH2A = (Altitude2EllipsoidalHeight) alti2ellpsHeight;
            return eH2A.getAssociatedDatum().getPrimeMeridian();
        }
        return null;
    }
}
