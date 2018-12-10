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



import org.cts.CRSHelper;
import org.cts.Identifier;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjParser;

import java.util.Map;

/**
 *
 * @author Erwan Bocher
 */
public abstract class AbstractProjRegistry implements Registry {

    /**
     * The parser associated to the PROJ registry.
     */
    protected final ProjParser projParser;

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem(Identifier identifier) throws RegistryException, CRSException {
        Map<String,String> params = getParameters(identifier.getAuthorityKey());
        if (!identifier.getAuthorityName().equalsIgnoreCase(getRegistryName())) {
            throw new RegistryException("CRS code '" + identifier.getCode() +
                    "' does not match this registry name : " + getRegistryName());
        }
        if (params == null) {
            throw new CRSException("Registry '" + getRegistryName() + "' contains no parameter for " + identifier);
        }
        // try to set a name from params to the identifier if identifier name is empty
        if (identifier.getName() == null || identifier.getName().isEmpty()) {
            String title = params.get(ProjKeyParameters.title);
            if (title != null && !title.isEmpty()) {
                identifier = new Identifier(identifier.getAuthorityName(), identifier.getAuthorityKey(), title);
            }
        }
        return CRSHelper.createCoordinateReferenceSystem(identifier, params);
    }

    /**
     * Return all parameters need to build a CoordinateReferenceSystem.
     *
     * @param code
     * @return 
     * @throws RegistryException
     */
    abstract public Map<String, String> getParameters(String code) throws RegistryException;

    /**
     * Create a new AbstractProjRegistry.
     */
    public AbstractProjRegistry() {
        projParser = new ProjParser(this);
    }
}
