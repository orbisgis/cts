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
package org.cts;

import java.util.ArrayList;
import java.util.List;

/**
 * Identifier used to identify objects such as Datums, Ellipsoids or
 * CoordinateReferenceSystems.<p>
 * Identifier encapsulates all identification info of {@link Identifiable}
 * objects in a special instance to make object creation clearer.<p>
 * Identifier also offers new unique ids for every object created in the LOCAL
 * namespace.
 *
 * @see Identifiable
 * @see IdentifiableComponent
 *
 * @author Michaël Michaud
 */
public class Identifier implements Identifiable {

    /**
     * Unique integer generated to identify a LOCAL object. LOCAL refers to a
     * namespace defined in the Identiable interface.
     */
    private static int localId = 0;

    /**
     * Return an identifier which is unique for this program session. This
     * identifier is usually associated with the LOCAL namespace
     * @return 
     */
    public static int getNewId() {
        return localId++;
    }
    /**
     * Mandatory attribute (default = LOCAL).
     */
    private String authorityName;
    /**
     * Mandatory attribute (getNewId can generate local unique ID).
     */
    private String authorityKey;
    /**
     * Complete name, sometimes called description.
     */
    private String name;
    /**
     * Short name used for user interface.
     */
    private String shortName;
    /**
     * Remarks.
     */
    private String remarks;
    /**
     * Aliases.
     */
    private List<Identifiable> aliases;

    /**
     * Creates a complete identifier.
     *
     * @param authorityName namespace of the identifier ie EPSG, IGNF
     * @param authorityKey unique key in the namespace
     * @param name name or description
     * @param shortName short name used for user interfaces
     * @param remarks remarks containing additionnal information on the object
     * @param aliases synonyms of this Identifiable
     */
    public Identifier(String authorityName, String authorityKey, String name,
            String shortName, String remarks, List<Identifiable> aliases) {
        this.authorityName = authorityName;
        this.authorityKey = authorityKey;
        this.name = name;
        this.shortName = shortName;
        this.remarks = remarks;
        this.aliases = aliases;
    }

    /**
     * Creates a local identifier.
     *
     * @param clazz the class of the identified object
     */
    public Identifier(Class clazz) {
        this(Identifiable.LOCAL + "_" + clazz.getSimpleName(),
                "" + getNewId(),
                Identifiable.UNKNOWN, null, null, null);
    }

    /**
     * Create a local identifier.
     *
     * @param clazz the class of the identified object
     * @param name the name of the identified object
     */
    public Identifier(Class clazz, String name) {
        this(Identifiable.LOCAL + "_" + clazz.getSimpleName(),
                "" + getNewId(),
                name, null, null, null);
    }

    /**
     * Create a local identifier.
     *
     * @param clazz the class of the identified object
     * @param name the name of the identified object
     * @param shortName the short name of the identified object
     */
    public Identifier(Class clazz, String name, String shortName) {
        this(Identifiable.LOCAL + "_" + clazz.getSimpleName(),
                "" + getNewId(),
                name, shortName, null, null);
    }

    /**
     * Create a local identifier.
     *
     * @param clazz the class of the identified object
     * @param name the name of the identified object
     * @param shortName the short name of the identified object
     * @param aliases synonyms of this Identifiable
     */
    public Identifier(Class clazz, String name, String shortName,
            List<Identifiable> aliases) {
        this(Identifiable.LOCAL + "_" + clazz.getSimpleName(),
                "" + getNewId(),
                name, shortName, null, aliases);
    }

    /**
     * Creates a complete identifier.
     *
     * @param authorityName ie EPSG, IGNF
     * @param authorityKey ie 4326, LAMB
     * @param name
     */
    public Identifier(String authorityName, String authorityKey, String name) {
        this(authorityName, authorityKey, name, null, null, null);
    }

    /**
     * Creates a complete identifier.
     *
     * @param authorityName ie EPSG, IGNF
     * @param authorityKey ie 4326, LAMB
     * @param name
     * @param shortName a short name to use in user interfaces
     */
    public Identifier(String authorityName, String authorityKey, String name, String shortName) {
        this(authorityName, authorityKey, name, shortName, null, null);
    }

    /**
     * Return the authority name of this identifier (ex. EPSG, IGN-F) The
     * namespace may represent a database name, a URL, a URN...
     * @return 
     */
    @Override
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     * Returns the key of this identifier (id must be unique inside the
     * authority name).
     * @return 
     */
    @Override
    public String getAuthorityKey() {
        return authorityKey;
    }

    /**
     * Returns a code formed with a namespace, ':' and the id value of
     * identifier (ex. EPSG:27572).
     *
     * @return a String of the form namespace:identifier
     */
    @Override
    public String getCode() {
        return authorityName + ":" + authorityKey;
    }

    /**
     * Returns a string used to identify clearly the object.
     * @return 
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a short string used to identify unambiguously the object. A short
     * name should have less than 16 characters whenever possible, and should
     * never exceed 48 characters.
     * @return 
     */
    @Override
    public String getShortName() {
        return shortName == null ? name : shortName;
    }

    /**
     * Change the short string used to identify unambiguously the object. A
     * short name should have less than 16 characters whenever possible, and
     * should never exceed 48 characters.
     *
     * @param shortName the new short name for the Identifier
     */
    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Returns remarks.
     * @return 
     */
    @Override
    public String getRemarks() {
        return remarks;
    }

    /**
     * Change the remarks. Be careful, this method will delete former remarks.
     *
     * @param remarks the new remarks on this identifier
     */
    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Add remarks.
     *
     * @param new_remark the remark to add to the Identifier's remarks
     */
    @Override
    public void addRemark(String new_remark) {
        this.remarks = this.remarks + "\n" + new_remark;
    }

    /**
     * Get aliases
     * @return 
     */
    @Override
    public List<Identifiable> getAliases() {
        return aliases == null ? new ArrayList<Identifiable>() : aliases;
    }

    /**
     * Add an alias
     *
     * @param alias an alias for this object
     * @return 
     */
    @Override
    public boolean addAlias(Identifiable alias) {
        if (aliases == null) {
            aliases = new ArrayList<Identifiable>();
        }
        return aliases.add(alias);
    }

    /**
     * Returns a WKT representation of the identifier.
     *
     * @return 
     */
    public String toWKT() {
        String w = "AUTHORITY[\"" +
                this.getAuthorityName() +
                "\",\"" +
                this.getAuthorityKey() +
                "\"]";
        return w;
    }

    /**
     * Returns true if object is equals to this. Test equality between codes
     * (namespace + id), then between aliases.
     *
     * @param object The object to compare this Identifier against
     * @return 
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Identifier) {
            Identifier other = (Identifier) object;
            // Test equality between this code and object's code
            if (getAuthorityName() != null && other.getAuthorityName() != null
                    && getAuthorityName().toUpperCase().equals(other.getAuthorityName().toUpperCase())
                    && getAuthorityKey() != null && other.getAuthorityKey() != null
                    && getAuthorityKey().equals(other.getAuthorityKey())) {
                return true;
            }
            // If not equal, test equality between this aliases and
            // the other object aliases
            boolean areEquals;
            for (Identifiable id2 : other.getAliases()) {
                areEquals
                        = this.getAuthorityName() != null
                        && id2.getAuthorityName() != null
                        && this.getAuthorityName().toUpperCase().equals(id2.getAuthorityName().toUpperCase())
                        && this.getAuthorityKey() != null
                        && id2.getAuthorityKey() != null
                        && this.getAuthorityKey().equals(id2.getAuthorityKey());
                if (areEquals) {
                    return true;
                }
            }
            for (Identifiable id1 : getAliases()) {
                areEquals
                        = id1.getAuthorityName() != null
                        && other.getAuthorityName() != null
                        && id1.getAuthorityName().toUpperCase().equals(other.getAuthorityName().toUpperCase())
                        && id1.getAuthorityKey() != null
                        && other.getAuthorityKey() != null
                        && id1.getAuthorityKey().equals(other.getAuthorityKey());
                if (areEquals) {
                    return true;
                }
                for (Identifiable id2 : other.getAliases()) {
                    areEquals
                            = id1.getAuthorityName() != null
                            && id2.getAuthorityName() != null
                            && id1.getAuthorityName().toUpperCase().equals(id2.getAuthorityName().toUpperCase())
                            && id1.getAuthorityKey() != null
                            && id2.getAuthorityKey() != null
                            && id1.getAuthorityKey().equals(id2.getAuthorityKey());
                    if (areEquals) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this Identifier.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.authorityName.toUpperCase() != null ? this.authorityName.toUpperCase().hashCode() : 0);
        hash = 11 * hash + (this.authorityKey != null ? this.authorityKey.hashCode() : 0);
        hash = 11 * hash + (this.aliases != null ? this.aliases.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a String representation of this identifier.
     * @return 
     */
    @Override
    public String toString() {
        return "[" + authorityName + ":" + authorityKey + "] " + name;
    }
}
