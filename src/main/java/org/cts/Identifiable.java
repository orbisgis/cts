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

import java.util.List;

/**
 * Identifiable is the interface implemented by geodetic objects issued from
 * registries (like the epsg database).<p> {@link org.cts.datum.Datum}s,
 * {@link org.cts.datum.Ellipsoid} and {@link org.cts.crs.CoordinateReferenceSystem}s are
 * Identifiable objects.<p>
 * Well known geodetic databases are :
 * <ul>
 * <li><a href="http://www.epsg.org/CurrentDB.html">EPSG</a></li>
 * <li><a href="http://www.ign.fr/telechargement/MPro/geodesie/RIG/RIG.xml">
 * IGN-F</a></li>
 * </ul>
 * Identifiable components have three main characteristics :
 * <ul>
 * <li>A namespace</li>
 * <li>An identifier which is relative to this namespace</li>
 * <li>A name</li>
 * </ul>
 * Examples of namespaces for spatial reference systems are :
 * <ul>
 * <li>EPSG</li>
 * <li>http://www.opengis.net/gml/srs/epsg.xml</li>
 * <li>urn:ogc:def:crs:EPSG:6.3</li>
 * </ul>
 * The syntax recommended by ogc for epsg object is described in
 * <a href="http://www.faqs.org/rfcs/rfc5165.html">rfc5165</a>.
 *
 * @author Michaël Michaud
 */
public interface Identifiable {

    /**
     * Namespace used to identify objects having no reference in an external
     * persistent database.
     */
    public String LOCAL = "LOCAL";

    /**
     * Value used as a dafault name for objects without name.
     */
    public String DEFAULT = "DEFAULT";

    /**
     * Value used for objects with an unknown name.
     */
    public String UNKNOWN = "UNKNOWN";

    /**
     * Returns the authority name of the CRS as a String.(ex : EPSG) <p>
     * The String must follow the syntax of a URI (ex. urn:ogc:def:crs:OGC:1.3).
     * You'll find more on the URI syntax
     * <a
     * href="http://java.sun.com/javase/6/docs/api/java/net/URI.html">here</a>.
     *
     */
    public String getAuthorityName();

    /**
     * Returns this authority's id (must be unique in this Identifiable's
     * namespace). (ex : 27572)
     */
    public String getAuthorityKey();

    /**
     * Returns the code formed with the namespace URI, ':' and the id value of
     * the identifier (ex. EPSG:27572).
     *
     * @return a String of the form namespace:id
     */
    public String getCode();

    /**
     * Returns the full readable name of this object.
     */
    public String getName();

    /**
     * Returns the short name of this Identifiable. A short name should have
     * less than 16 characters whenever possible, and should never exceed 48
     * characters.
     */
    public String getShortName();

    /**
     * Change the short name for this Identifiable. A short name should have
     * less than 16 characters whenever possible, and should never exceed 48
     * characters.
     *
     * @param shortName the short name to give to the identifiable
     */
    public void setShortName(String shortName);

    /**
     * Returns remarks.
     */
    public String getRemarks();

    /**
     * Change the remarks.
     *
     * @param remarks the new remarks of the identifiable
     */
    public void setRemarks(String remarks);

    /**
     * Add remarks.
     *
     * @param new_remark the remark to add
     */
    public void addRemark(String new_remark);

    /**
     * Get aliases
     */
    public List<Identifiable> getAliases();

    /**
     * Adds an alias
     *
     * @param alias an alias for this object
     */
    public boolean addAlias(Identifiable alias);
}
