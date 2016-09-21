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

import java.util.HashMap;
import java.util.Map;

import org.cts.Identifiable;
import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.util.AngleFormat;

/**
 * PrimeMeridian.<p> fr : Actuellement, le méridien d'origine de la plupart des
 * sytèmes géodésiques est voisin du méridien de Greenwich, qui passe par
 * l'observatoire de Greenwich, en Angleterre. Jusqu'au début du XXe siècle,
 * différents pays utilisèrent d'autres méridiens d'origine comme le méridien de
 * Paris en France, le méridien de Berlin en Allemagne, le méridien de Tolède en
 * Espagne ou le méridien d'Uppsala en Suède. Certaines cartes nautiques
 * utilisaient le méridien de Ferro, correspondant à l'île d'El Hierro dans
 * l'archipel des Canaries, afin d'obtenir une longitude positive pour toutes
 * les terres européennes.<p> Taken from <a
 * href="http://fr.wikipedia.org/wiki/M%C3%A9ridien">wikipedia</a>
 *
 * @author Michaël Michaud, Erwan Bocher, Jules Party
 */
public class PrimeMeridian extends IdentifiableComponent {

    /**
     * Greenwich Meridian.
     */
    public static final PrimeMeridian GREENWICH =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8901", "Greenwich", "Greenwich"), 0.0);

    /**
     * Lisbon Meridian.
     */
    public static final PrimeMeridian LISBON =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8902", "Lisbon", "Lisbon"),
                    AngleFormat.parseAngle("9° 07' 54.862\" W"));

    /**
     * Paris Meridian.
     */
    public static final PrimeMeridian PARIS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8903", "Paris", "Paris",
                    "Value adopted by IGN (Paris) in 1936. Equivalent to 2°20'14.025\". Preferred by EPSG to earlier value of 2° 12' 5.022\" used by RGS London", null),
                    AngleFormat.parseAngle("2°20'14.025\""));

    /**
     * Bogota Meridian.
     */
    public static final PrimeMeridian BOGOTA =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8904", "Bogota", "Bogota"),
                    AngleFormat.parseAngle("74° 04' 51.3\" W"));

    /**
     * Madrid Meridian.
     */
    public static final PrimeMeridian MADRID =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8905", "Madrid", "Madrid"),
                    AngleFormat.parseAngle("3° 41' 16.58\" W"));

    /**
     * Rome Meridian.
     */
    public static final PrimeMeridian ROME =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8906", "Rome", "Rome"),
                    AngleFormat.parseAngle("12° 27' 08.4\" E"));

    /**
     * Bern Meridian.
     */
    public static final PrimeMeridian BERN =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8907", "Bern", "Bern", "1895 value. Newer value of 7°26'22.335\" determined in 1938.", null),
                    AngleFormat.parseAngle("7° 26' 22.5\" E"));

    /**
     * Jakarta Meridian.
     */
    public static final PrimeMeridian JAKARTA =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8908", "Jakarta", "Jakarta"),
                    AngleFormat.parseAngle("106° 48' 27.79\" E"));

    /**
     * Ferro Meridian.
     */
    public static final PrimeMeridian FERRO =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8909", "Ferro", "Ferro", "Used in Austria and former Czechoslovakia.", null),
                    AngleFormat.parseAngle("17° 40' W"));

    /**
     * Brussels Meridian.
     */
    public static final PrimeMeridian BRUSSELS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8910", "Brussels", "Brussels"),
                    AngleFormat.parseAngle("4° 22' 4.71\" E"));

    /**
     * Stockholm Meridian.
     */
    public static final PrimeMeridian STOCKHOLM =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8911", "Stockholm"),
                    AngleFormat.parseAngle("18° 03' 29.8\" E"));

    /**
     * Athens Meridian.
     */
    public static final PrimeMeridian ATHENS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8912", "Athens", "Athens", "Used in Greece for older mapping based on Hatt projection.", null),
                    AngleFormat.parseAngle("23° 42' 58.815\" E"));

    /**
     * Oslo Meridian.
     */
    public static final PrimeMeridian OSLO =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8913", "Oslo", "Oslo", " Formerly known as Kristiania or Christiania.", null),
                    AngleFormat.parseAngle("10° 43' 22.5\" E"));

    /**
     * Paris (Royal Geographic Society) Meridian.
     */
    public static final PrimeMeridian PARIS_RGS =
            createPrimeMeridianFromDDLongitude(new Identifier("EPSG", "8914", "Paris (RGS)", "Paris (RGS)", "Value replaced by IGN (France) in 1936 - see code 8903. Equivalent to 2.596898 grads.", null),
                    AngleFormat.parseAngle("2° 12' 5.022\" E"));

    /**
     * The angle formed by this meridian with the international Greenwich
     * meridian in decimal degrees.
     */
    private double ddLongitude;

    /**
     * primeMeridianFromName associates each prime meridian to a short string
     * used to recognize it in CTS.
     */
    public static final Map<String, PrimeMeridian> primeMeridianFromName = new HashMap<String, PrimeMeridian>();

    static {
        primeMeridianFromName.put("greenwich", PrimeMeridian.GREENWICH);
        primeMeridianFromName.put("paris", PrimeMeridian.PARIS);
        primeMeridianFromName.put("lisbon", PrimeMeridian.LISBON);
        primeMeridianFromName.put("bogota", PrimeMeridian.BOGOTA);
        primeMeridianFromName.put("madrid", PrimeMeridian.MADRID);
        primeMeridianFromName.put("rome", PrimeMeridian.ROME);
        primeMeridianFromName.put("bern", PrimeMeridian.BERN);
        primeMeridianFromName.put("jakarta", PrimeMeridian.JAKARTA);
        primeMeridianFromName.put("ferro", PrimeMeridian.FERRO);
        primeMeridianFromName.put("brussels", PrimeMeridian.BRUSSELS);
        primeMeridianFromName.put("stockholm", PrimeMeridian.STOCKHOLM);
        primeMeridianFromName.put("athens", PrimeMeridian.ATHENS);
        primeMeridianFromName.put("oslo", PrimeMeridian.OSLO);
    }

    /**
     * Creates a new
     * <code>PrimeMeridian</code>.
     *
     * @param name name ofthe PrimeMeridian
     * @param ddLongitude the longitude of the Prime Meridian from Greenwich in
     * decimal degrees.
     */
    private PrimeMeridian(String name, double ddLongitude) {
        this(new Identifier(PrimeMeridian.class, name), ddLongitude);
    }

    /**
     * Creates a new
     * <code>PrimeMeridian</code>.
     *
     * @param identifier identifier if the PrimeMeridian
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
     * @param identifier identifier of the <code>PrimeMeridian</code>
     * @param ddLongitude the longitude from Greenwich in decimal degrees
     */
    public static PrimeMeridian createPrimeMeridianFromDDLongitude(
            Identifier identifier, double ddLongitude) {
        PrimeMeridian pm = new PrimeMeridian(identifier, ddLongitude);
        return pm.checkExistingPrimeMeridian();
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
        PrimeMeridian pm = new PrimeMeridian(identifier, AngleFormat.dms2dd(dmsLongitude));
        return pm.checkExistingPrimeMeridian();
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from DMS longitude.
     *
     * @param identifier identifier of the <code>PrimeMeridian</code>
     * @param dmsLongitude the longitude from Greenwich in degree/minute/second
     */
    public static PrimeMeridian createPrimeMeridianFromDMSLongitude(
            Identifier identifier, String dmsLongitude)
            throws IllegalArgumentException {
        double ddLongitude = AngleFormat.parseAngle(dmsLongitude);
        PrimeMeridian pm = new PrimeMeridian(identifier, ddLongitude);
        return pm.checkExistingPrimeMeridian();
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
        PrimeMeridian pm = new PrimeMeridian(identifier, Math.toDegrees(longitude));
        return pm.checkExistingPrimeMeridian();
    }

    /**
     * Create a new
     * <code>PrimeMeridian</code> from a double longitude in grades.
     *
     * @param identifier identifier of the <code>PrimeMeridian</code>
     * @param longitude the longitude from Greenwich in grades
     */
    public static PrimeMeridian createPrimeMeridianFromLongitudeInGrades(
            Identifier identifier, double longitude) {
        PrimeMeridian pm = new PrimeMeridian(identifier, longitude * 180.0 / 200.0);
        return pm.checkExistingPrimeMeridian();
    }

    /**
     * Check if
     * <code>this</code> is equals to one of the predefined PrimeMeridian
     * (Greenwich, Paris,&hellip;). Return the predifined PrimeMeridian that
     * matches if exists, otherwise return
     * <code>this</code>.
     */
    private PrimeMeridian checkExistingPrimeMeridian() {
        if (this.equals(PrimeMeridian.GREENWICH)) {
            return PrimeMeridian.GREENWICH;
        } else if (this.equals(PrimeMeridian.ATHENS)) {
            return PrimeMeridian.ATHENS;
        } else if (this.equals(PrimeMeridian.BERN)) {
            return PrimeMeridian.BERN;
        } else if (this.equals(PrimeMeridian.BOGOTA)) {
            return PrimeMeridian.BOGOTA;
        } else if (this.equals(PrimeMeridian.BRUSSELS)) {
            return PrimeMeridian.BRUSSELS;
        } else if (this.equals(PrimeMeridian.FERRO)) {
            return PrimeMeridian.FERRO;
        } else if (this.equals(PrimeMeridian.JAKARTA)) {
            return PrimeMeridian.JAKARTA;
        } else if (this.equals(PrimeMeridian.LISBON)) {
            return PrimeMeridian.LISBON;
        } else if (this.equals(PrimeMeridian.MADRID)) {
            return PrimeMeridian.MADRID;
        } else if (this.equals(PrimeMeridian.OSLO)) {
            return PrimeMeridian.OSLO;
        } else if (this.equals(PrimeMeridian.PARIS)) {
            return PrimeMeridian.PARIS;
        } else if (this.equals(PrimeMeridian.PARIS_RGS)) {
            return PrimeMeridian.PARIS_RGS;
        } else if (this.equals(PrimeMeridian.ROME)) {
            return PrimeMeridian.ROME;
        } else if (this.equals(PrimeMeridian.STOCKHOLM)) {
            return PrimeMeridian.STOCKHOLM;
        } else {
            return this;
        }
    }

    /**
     * Returns a WKT representation of the prime meridian.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("PRIMEM[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getLongitudeFromGreenwichInDegrees());
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Return a String representation of this
     * <code>PrimeMeridian</code>.
     */
    @Override
    public String toString() {
        return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] "
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
            if (getAuthorityKey().equals(pm.getAuthorityKey())
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

    /**
     * Returns the hash code for this PrimeMeridian.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.ddLongitude) ^ (Double.doubleToLongBits(this.ddLongitude) >>> 32));
        return hash;
    }
}
