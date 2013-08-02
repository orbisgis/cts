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
package org.cts.registry;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * This class manages all supported registry. It permits to declare a custom
 * registry or remove one.
 *
 * @author Erwan Bocher
 */
public final class RegistryManager {

    static final Logger LOGGER = Logger.getLogger(RegistryManager.class);
    private final Map<String, Registry> registries = new HashMap<String, Registry>();
    private final List<RegistryManagerListener> listeners = new ArrayList<RegistryManagerListener>();

    /**
     * Create a default registry manager without any declared registry. To load
     * a registry you must call the {@code addRegistry} ig : addRegistry(new
     * IGNFRegistry());
     */
    public RegistryManager() {
    }

    /**
     * Adds a listener.
     *
     * @param listener a listener
     */
    public void addRegistryManagerListener(RegistryManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove the listener if it is present in the listener list
     *
     * @param listener
     * @return true if the listener was successfully removed. False if the
     * specified parameter was not a listener
     */
    public boolean removeRegistryManagerListener(RegistryManagerListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Declare a registry to the {@code RegistryManager}
     *
     * @param registryClass
     */
    public void addRegistry(Registry registryClass) {
        addRegistry(registryClass, false);
    }

    /**
     * Declare a registry to the {@code RegistryManager} An existing registry
     * can be replaced by a new one.
     *
     * @param registryClass
     * @param replace
     */
    public void addRegistry(Registry registry, boolean replace) {
        LOGGER.trace("Adding a new registry " + registry.getRegistryName());
        String registryName = registry.getRegistryName().toLowerCase();
        if (!replace && registries.containsKey(registryName)) {
            throw new IllegalArgumentException("Registry " + registryName
                    + " already exists");
        }
        registries.put(registryName, registry);
        fireRegistryAdded(registryName);
    }

    /**
     * Listener to inform that a registry has been added.
     *
     * @param functionName
     */
    private void fireRegistryAdded(String functionName) {
        for (RegistryManagerListener listener : listeners) {
            listener.registryAdded(functionName);
        }
    }

    /**
     * Gets if the registry with the given name has been registered.
     *
     * @param name a registry name ie epsg, ignf, esri...
     * @return true if registered
     */
    public boolean contains(String name) {
        return registries.containsKey(name);
    }

    /**
     * Gets all registered registry names
     *
     * @return an array of names
     */
    public String[] getRegistryNames() {
        LOGGER.trace("Getting all function names");
        Set<String> k = registries.keySet();
        return k.toArray(new String[k.size()]);
    }

    /**
     * Return the corresponding registry based on its name
     *
     * @param string
     * @return
     */
    public Registry getRegistry(String registryName) {
        LOGGER.trace("Getting the registry " + registryName);
        Registry registry = registries.get(registryName.toLowerCase());
        if (registry == null) {
            return null;
        } else {
            return registry;
        }
    }
}
