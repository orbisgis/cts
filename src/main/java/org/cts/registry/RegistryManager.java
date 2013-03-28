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
package org.cts.registry;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * This class manages all supported registry.
 *
 * @author Erwan Bocher
 */
public final class RegistryManager {

    static final Logger LOGGER = Logger.getLogger(RegistryManager.class);
    private final Map<String, Class<? extends AbstractProjRegistry>> registries = new HashMap<String, Class<? extends AbstractProjRegistry>>();
    private final List<RegistryManagerListener> listeners = new ArrayList<RegistryManagerListener>();

    public RegistryManager() {
        addRegistry(IGNFRegistry.class);
        addRegistry(EPSGRegistry.class);
        addRegistry(ESRIRegistry.class);
        addRegistry(Nad27Registry.class);
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

    public void addRegistry(Class<? extends AbstractProjRegistry> registryClass) {
        addRegistry(registryClass, false);
    }

    public void addRegistry(Class<? extends AbstractProjRegistry> registryClass, boolean replace) {
        LOGGER.trace("Adding a new registry " + registryClass.getName());
        Registry registry;
        try {
            registry = registryClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot instantiate this registry: "
                    + registryClass, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot instantiate this registry: "
                    + registryClass, e);
        }
        String registryName = registry.getRegistryName().toLowerCase();
        addRegistry(registryName, registryClass, replace);
    }

    public void addRegistry(String functionName, Class<? extends AbstractProjRegistry> functionClass, boolean replace) {
        if (!replace && registries.containsKey(functionName)) {
            throw new IllegalArgumentException("Registry " + functionName
                    + " already exists");
        }
        registries.put(functionName, functionClass);

        fireRegistryAdded(functionName);
    }

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
     * Return the corresponding registry
     *
     * @param string
     * @return
     */
    public Registry getRegistry(String registryName) {
        LOGGER.trace("Getting the registry " + registryName);
        Class<? extends Registry> registryClass = registries.get(registryName.toLowerCase());

        if (registryClass == null) {
            return null;
        } else {
            Registry registry;
            try {
                registry = registryClass.newInstance();
                return registry;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
