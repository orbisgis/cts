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
package org.cts.registry;

import org.cts.Identifier;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;

import java.util.Set;

/**
 *
 * @author Erwan Bocher
 */
public interface Registry {

    /**
     * Return the name of the registry.
     * @return 
     */
    public String getRegistryName();

    /**
     * Return all parameters need to build a CoordinateReferenceSystem.
     *
     * @param identifier
     * @return 
     * @throws RegistryException
     * @throws org.cts.crs.CRSException
     */
    //public Map<String, String> getParameters(String code) throws RegistryException;
    public CoordinateReferenceSystem getCoordinateReferenceSystem(Identifier identifier) throws RegistryException, CRSException;

    /**
     * Return all supported codes for this registry.
     *
     * @return 
     * @throws RegistryException
     */
    public Set<String> getSupportedCodes() throws RegistryException;
}
