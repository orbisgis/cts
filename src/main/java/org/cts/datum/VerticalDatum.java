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
package org.cts.datum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.cts.CoordinateOperation;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;

/**
 * <p>Vertical datum are used to determine elevation. They are generally based
 * upon a gravity model.</p>
 *
 * @author Michael Michaud
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
            "1984");

    /**
     * Creates a new Datum.
     *
     * @param name name of this vertical datum
     */
    public VerticalDatum(String name) {
        super(new Identifier(VerticalDatum.class, name),
                GeographicExtent.WORLD, null, null);
        datums.put(getIdentifier(), this);
        //addCoordinateOperation(WGS84VD, altitude2EllipsoidalHeight);
    }

    /**
     * Creates a new Datum.
     *
     * @param identifier identifier.
     * @param extent this datum extension
     * @param origin origin decription this datum
     * @param epoch realization epoch of this datum
     */
    public VerticalDatum(Identifier identifier, GeographicExtent extent,
            String origin, String epoch) {
        super(identifier, extent, origin, epoch);
        datums.put(identifier, this);
        //addCoordinateOperation(WGS84VD, altitude2EllipsoidalHeight);
    }

    /**
     * <p>Return a collection of all the registered datums.</p>
     */
    public static Collection<VerticalDatum> getAvailableDatums() {
        return datums.values();
    }

    /**
     * <p>Return the Datum with idEPSG identifier.</p>
     */
    public static VerticalDatum getDatum(Identifier identifier) {
        return datums.get(identifier);
    }

    /**
     * <p>Return the Datum with this name.</p>
     */
    public static VerticalDatum getEpsgDatum(int idEpsg) {
        return datums.get(new Identifier("EPSG", String.valueOf(idEpsg), DEFAULT));
    }

    public Ellipsoid getEllipsoid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CoordinateOperation getToWGS84() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrimeMeridian getPrimeMeridian() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
