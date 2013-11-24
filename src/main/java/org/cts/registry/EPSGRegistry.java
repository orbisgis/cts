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

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class parses the epsg file available in the resources package.
 * For a given code, it returns a map of parameters required by
 * {@link org.cts.CRSHelper} to build a {@link org.cts.crs.CoordinateReferenceSystem}.
 *
 * @author Erwan Bocher
 */
public class EPSGRegistry extends AbstractProjRegistry implements Registry {

    /**
     * The regex that must be used to parse EPSG registry.
     */
    static final Pattern EPSG_REGEX = Pattern.compile("\\s+|<>");

    @Override
    public String getRegistryName() {
        return "epsg";
    }

    @Override
    public Map<String, String> getParameters(String code) throws RegistryException {
        try {
            Map<String, String> crsParameters = projParser.readParameters(code, EPSG_REGEX);
            return crsParameters;
        } catch (IOException ex) {
            throw new RegistryException("Cannot load the EPSG registry", ex);
        }
    }

    @Override
    public Set<String> getSupportedCodes() throws RegistryException {
        try {
            return projParser.getSupportedCodes(EPSG_REGEX);
        } catch (IOException ex) {
            throw new RegistryException("Cannot load the EPSG registry", ex);
        }
    }
}
