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

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.util.AngleFormat;

/**
 * PrimeMeridian.<p> fr : Actuellement, le m�ridien d'origine de la plupart des
 * syt�mes g�od�siques est voisin du m�ridien de Greenwich, qui passe par
 * l'observatoire de Greenwich, en Angleterre. Jusqu'au d�but du XXe si�cle,
 * diff�rents pays utilis�rent d'autres m�ridiens d'origine comme le m�ridien de
 * Paris en France, le m�ridien de Berlin en Allemagne, le m�ridien de Tol�de en
 * Espagne ou le m�ridien d'Uppsala en Su�de. Certaines cartes nautiques
 * utilisaient le m�ridien de Ferro, correspondant � l'�le d'El Hierro dans
 * l'archipel des Canaries, afin d'obtenir une longitude positive pour toutes
 * les terres europ�ennes.<p> Taken from <a
 * href="http://fr.wikipedia.org/wiki/M%C3%A9ridien">wikipedia</a>
 *
 * @author Michael Michaud
 */
public class PrimeMeridian extends IdentifiableComponent {

    public static final PrimeMeridian GREENWICH =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8901", "Greenwich"), 0.0);
    public static final PrimeMeridian LISBON =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8902", "Lisbon"), -9.0754862);
    public static final PrimeMeridian PARIS =
            createPrimeMeridianFromDMSLongitude(new Identifier("EPSG", "8903", "Paris"), "2�20'14.025\" E");
    public static final PrimeMeridian BOGOTA =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8904", "Bogota"), -74.04513);
    public static final PrimeMeridian MADRID =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8905", "Madrid"), -3.411658);
    public static final PrimeMeridian ROME =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8906", "Rome"), 12.27084);
    public static final PrimeMeridian BERN =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8907", "Bern"), 7.26225);
    public static final PrimeMeridian JAKARTA =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8908", "Jakarta"), 106.482779);
    public static final PrimeMeridian FERRO =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8909", "Ferro"), -17.4);
    public static final PrimeMeridian BRUSSELS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8910", "Brussels"), 4.220471);
    public static final PrimeMeridian STOCKHOLM =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8911", "Stockholm"), 18.03298);
    public static final PrimeMeridian ATHENS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8912", "Athens"), 23.4258815);
    public static final PrimeMeridian OSLO =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8913", "Oslo"), 10.43225);
    public static final PrimeMeridian PARIS_RGS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8914", "Paris (RGS)"), 2.201395);
    private double ddLongitude;

    /**
     * Creates a new
     * <code>PrimeMeridian</code>.
     *
     * @param name.
     * @param ddLongitude the longitude of the Prime Meridian from Greenwich in
     * decimal degrees.
     */
    private PrimeMeridian(String name, double ddLongitude) {
        super(new Identifier(PrimeMeridian.class, name));
        this.ddLongitude = ddLongitude;
    }

    /**
     * Creates a new
     * <code>PrimeMeridian</code>.
     *
     * @param identifier.
     * @param ddLongitude the longitude of the Prime Meridian from Greenwich in
     * decimal degrees.
     */
    private PrimeMeridian(Identifier identifier, double ddLongitude) {
        super(identifier);
        this.ddLongitude = ddLongitude;
    }

    /**
     * Return the angle formed by this meridian with the international Greenwich
     * meridian in degrees.
     */
    public double getLongitudeFromGreenwichInDegrees() {
        return ddLongitude;
    }

    /**
     * Return the angle formed by this meridian with the international Greenwich
     * meridian in radians.
     */
    public double getLongitudeFromGreenwichInRadians() {
        return Math.toRadians(ddLongitude);
    }

    /**
     * Return the angle formed by this meridian with the international Greenwich
     * meridian in degree/minute/second.
     */
    public String getLongitudeFromGreenwichInDMS() {
        return AngleFormat.LONGITUDE_FORMATTER.format(ddLongitude);
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from a double longitude in decimal degrees.
     *
     * @param identifier identifier of the
     * <code>PrimeMeridian</code>
     * @param ddLongitude the longitude from Greenwich in decimal degrees
     */
    public static PrimeMeridian createPrimeMeridianFromDDLongitude(
            Identifier identifier, double ddLongitude) {
        return new PrimeMeridian(identifier, ddLongitude);
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from a double longitude in decimal degrees.
     *
     * @param identifier identifier of the PrimeMeridian
     * @param dmsLongitude the longitude from Greenwich in DMS
     */
    public static PrimeMeridian createPrimeMeridianFromDMSLongitude(
            Identifier identifier, double dmsLongitude) {
        return new PrimeMeridian(identifier, AngleFormat.dms2dd(dmsLongitude));
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from DMS longitude.
     *
     * @param identifier identifier of the
     * <code>PrimeMeridian</code>
     * @param dmsLongitude the longitude from Greenwich in degree/minute/second
     */
    public static PrimeMeridian createPrimeMeridianFromDMSLongitude(
            Identifier identifier, String dmsLongitude)
            throws IllegalArgumentException {
        double ddLongitude = AngleFormat.parseAngle(dmsLongitude);
        return new PrimeMeridian(identifier, ddLongitude);
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from a longitude in radians.
     *
     * @param identifier identifier of the PrimeMeridian
     * @param longitude the longitude from Greenwich in radians
     */
    public static PrimeMeridian createPrimeMeridianFromLongitudeInRadians(
            Identifier identifier, double longitude) {
        return new PrimeMeridian(identifier, Math.toDegrees(longitude));
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from a double longitude in grades.
     *
     * @param identifier identifier of the
     * <code>PrimeMeridian</code>
     * @param longitude the longitude from Greenwich in grades
     */
    public static PrimeMeridian createPrimeMeridianFromLongitudeInGrades(
            Identifier identifier, double longitude) {
        return new PrimeMeridian(identifier, longitude * 180.0 / 200.0);
    }

    /**
     * @return a String representation of this
     * <code>PrimeMeridian</code>.
     */
    @Override
    public String toString() {
        return "[" + getNamespace() + ":" + getId() + "] "
                + getName() + " (" + getLongitudeFromGreenwichInDMS() + ")";
    }

    /**
     * Return true if this
     * <code>PrimeMeridian</code> can be considered as equals to another one.
     */
    @Override
    public boolean equals(Object other) {
        // short circuit to compare final static Ellipsoids
        if (this == other) {
            return true;
        }
        if (other instanceof PrimeMeridian) {
            PrimeMeridian pm = (PrimeMeridian) other;
            // if prime meridian codes or names are equals, return true
            if (getCode().equals(pm.getCode())
                    || getName().equalsIgnoreCase(pm.getName())) {
                return true;
            }
            // else if prime meridians difference is less than 1E-11 rad
            // (i.e. < 0.1 mm) prime meridians are also considered as equals
            double l1 = getLongitudeFromGreenwichInRadians();
            double l2 = pm.getLongitudeFromGreenwichInRadians();
            return (Math.abs(l1 - l2) < 1e-11);
        }
        return false;
    }
}
