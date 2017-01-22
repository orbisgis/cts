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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages all supported registry. It permits to declare a custom
 * registry or remove one.
 * Registries are stored in a case-insensitive map (keys are uppercase)
 *
 * @author Erwan Bocher
 */
public final class RegistryManager {

    static final Logger LOGGER = LoggerFactory.getLogger(RegistryManager.class);
    private final Map<String, Registry> registries = new HashMap<String, Registry>();
    private final List<RegistryManagerListener> listeners = new ArrayList<RegistryManagerListener>();

    /**
     * Creates a default registry manager without any registered {@link Registry}.
     * To load registries, use {@code addRegistry} method
     * (eg : addRegistry(new IGNFRegistry()));
     */
    public RegistryManager() {
    }

    /**
     * Adds a listener able to process add/remove registry events.
     */
    public void addRegistryManagerListener(RegistryManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the listener if it is present in the listener list.
     *
     * @return true if the listener was successfully removed. False if the
     * specified parameter was not a listener
     */
    public boolean removeRegistryManagerListener(RegistryManagerListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Register a {@link Registry} in this {@code RegistryManager}.
     */
    public void addRegistry(Registry registryClass) {
        addRegistry(registryClass, false);
    }

    /**
     * Register a {@link Registry} in this {@code RegistryManager}.
     * An existing registry can be replaced by a new one.
     *
     * @param registry the Registry to add
     * @param replace whether an existing Registry with the same name should be
     *                replaced  or not.
     */
    public void addRegistry(Registry registry, boolean replace) {
        LOGGER.trace("Adding a new registry " + registry.getRegistryName());
        String registryName = registry.getRegistryName().toUpperCase();
        if (!replace && registries.containsKey(registryName)) {
            throw new IllegalArgumentException("Registry " + registryName
                    + " already exists");
        }
        registries.put(registryName, registry);
        fireRegistryAdded(registry.getRegistryName());
    }

    /**
     * Informs listeners that a registry has been added.
     *
     * @param registryName name of the registry
     */
    private void fireRegistryAdded(String registryName) {
        for (RegistryManagerListener listener : listeners) {
            listener.registryAdded(registryName);
        }
    }

    /**
     * Returns whether a registry with the given name is already
     * registered or not.
     *
     * @param name a registry name ie epsg, ignf, esri...
     * @return true if name is already registered
     */
    public boolean contains(String name) {
        return registries.containsKey(name.toUpperCase());
    }

    /**
     * Gets all registered registry names
     * The returned array contains a case-sensitive version of registry names.
     * @return an array of names
     */
    public String[] getRegistryNames() {
        LOGGER.trace("Getting all registry names");
        List<String> names = new ArrayList<String>();
        for (Registry r : registries.values()) {
            names.add(r.getRegistryName());
        }
        return names.toArray(new String[names.size()]);
    }

    /**
     * Gets the {@link Registry} registered with this name or
     * null if no Registry has been registered with this name.
     */
    public Registry getRegistry(String registryName) {
        LOGGER.trace("Getting the registry " + registryName);
        return registries.get(registryName.toUpperCase());
    }
}
