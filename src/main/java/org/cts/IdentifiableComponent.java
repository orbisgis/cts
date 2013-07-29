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
package org.cts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * IdentifiableComponent is a helper class used as a parent class for components
 * having to implement the Identifiable interface. Instead of implementing
 * Identifiable methods in each component class, the programmer just has to
 * extends IdentifiableComponent.
 *
 * @author Michaël Michaud
 */
public class IdentifiableComponent implements Identifiable {

    private Identifier identifier;
    static final Logger LOGGER = Logger.getLogger(CRSHelper.class);
    private static Map<Identifier, IdentifiableComponent> registry = new HashMap<Identifier, IdentifiableComponent>();

    /**
     * Return this component's Identifier
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Change this component's Identifier
     * 
     * @param identifier the new identifier of the component
     */
    protected void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Return the namespace of this identifier (ex. EPSG, IGNF) The namespace
     * may represent a database name, a URL, a URN...
     */
    @Override
    public String getAuthorityName() {
        return identifier.getAuthorityName();
    }

    /**
     * Returns the id of this identifier (id must be unique inside a namespace).
     */
    @Override
    public String getAuthorityKey() {
        return identifier.getAuthorityKey();
    }

    /**
     * Returns a code formed with a namespace, ':' and the id value of
     * identifier (ex. EPSG:27572).
     *
     * @return a String of the form namespace:identifier
     */
    @Override
    public String getCode() {
        return identifier.getCode();
    }

    /**
     * Returns a string used to identify clearly the object.
     */
    @Override
    public String getName() {
        return identifier.getName();
    }

    /**
     * Returns a short string used to identify unambiguously the object. The
     * string must have a maximum of 16 characters to fit menus with ease.
     */
    @Override
    public String getShortName() {
        return identifier.getShortName();
    }

    /**
     * Change the short string used to identify unambiguously the object. The
     * string must have a maximum of 16 characters to fit menus with ease.
     * 
     * @param uiName the new short name of the component
     */
    @Override
    public void setShortName(String uiName) {
        identifier.setShortName(uiName);
    }

    /**
     * Returns the name of this extent.
     */
    @Override
    public String getRemarks() {
        return identifier.getRemarks();
    }

    /**
     * Change the remarks. Be careful, this method will delete former remarks.
     * 
     * @param remarks the new remarks of the component
     */
    @Override
    public void setRemarks(String remarks) {
        identifier.setRemarks(remarks);
    }

    /**
     * Add remarks.
     * 
     * @param new_remark the remark to add to the component
     */
    @Override
    public void addRemark(String new_remark) {
        identifier.addRemark(new_remark);
    }

    /**
     * Get aliases
     */
    @Override
    public List<Identifiable> getAliases() {
        return identifier.getAliases();
    }

    /**
     * Add an alias
     *
     * @param alias an alias for this object
     */
    @Override
    public boolean addAlias(Identifiable alias) {
        return identifier.addAlias(alias);
    }

    /**
     * Creates an identifiable component from an identifier.
     *
     * @param identifier the identifier of the component
     */
    public IdentifiableComponent(Identifier identifier) {
        this.identifier = identifier;
        this.registerComponent();
    }

    private void registerComponent() {
        if (!registry.containsKey(getIdentifier())) {
            registry.put(getIdentifier(), this);
        } else {
            LOGGER.warn("A component has already been register for key " + getAuthorityName() + ":" + getAuthorityKey() + ".");
        }
    }

    public static IdentifiableComponent getComponent(Identifier id) {
        return registry.get(id);
    }

    /**
     * Returns true if object is an Identifier equals to this one.
     *
     * @param object The object to compare this IdentifiableComponent against
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Identifiable) {
            Identifiable other = (Identifiable) object;
            return ((getAuthorityName().equals(other.getAuthorityName())
                    && getAuthorityKey().equals(other.getAuthorityKey()))
                    || getName().equals(other.getName()));
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code for this IdentifiableComponent.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a String representation of this identifier.
     */
    @Override
    public String toString() {
        if (identifier != null) {
            return "[" + identifier.getAuthorityName() + ":" + identifier.getAuthorityKey() + "] " + identifier.getName();
        }
        return null;
    }
}
