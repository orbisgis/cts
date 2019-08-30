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

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.prj.PrjKeyParameters;
import org.cts.parser.prj.PrjParser;
import org.cts.parser.proj4.Proj4Parser;
import org.cts.registry.Registry;
import org.cts.registry.RegistryException;
import org.cts.registry.RegistryManager;

/**
 * This factory is in charge of creating new
 * {@link org.cts.crs.CoordinateReferenceSystem}s. It can do so :
 * <ul>
 * <li> 1. From an authority name and a code.</li>
 * <li> 2. From a OGC WKT String (PRJ) (that can be read from a file).</li>
 * </ul>
 * <p>Case 1 :</p>
 * <ul>Creation of the {@link org.cts.crs.CoordinateReferenceSystem} from a text
 * file will be delegated to one of {@link org.cts.registry.RegistryManager}'s
 * registries. If {@link org.cts.registry.RegistryManager} don't know the
 * authority of the CRS, an exception is returned.</ul>
 * <p>Case 2 :</p>
 * <ul>Creation of the {@link org.cts.crs.CoordinateReferenceSystem} from a OGC
 * WKT String (PRJ) will be delegated to the
 * {@link org.cts.parser.prj.PrjParser}.</ul>
 * <p>This class also manages a Cache which return
 * {@link org.cts.crs.CoordinateReferenceSystem}s which have already been
 * parsed.</p>
 *
 * @TODO authorityAndSrid is the same as Identifier.getCode()
 *
 * @author Erwan Bocher
 */
public class CRSFactory {

    private RegistryManager registryManager = new RegistryManager();
    protected final CRSCache<String, CoordinateReferenceSystem> CRSPOOL = new CRSCache<String, CoordinateReferenceSystem>(10);

    /**
     * Creates a new factory.
     */
    public CRSFactory() {
    }

    /**
     * Returns a {@link org.cts.crs.CoordinateReferenceSystem} corresponding to
     * an authority and a srid.
     *
     * @param authorityAndSrid the code of the desired CRS (for instance
     * EPSG:4326 or IGNF:LAMBE)
     * @return 
     * @throws CRSException
     */
    public CoordinateReferenceSystem getCRS(String authorityAndSrid) throws CRSException {
        CoordinateReferenceSystem crs = CRSPOOL.get(authorityAndSrid.toUpperCase());
        if (crs == null) {
            try {
                String[] registryNameWithCode = splitRegistryNameAndCode(authorityAndSrid);
                String authority = registryNameWithCode[0];
                String code = registryNameWithCode[1];
                if (isRegistrySupported(authority)) {
                    Registry registry = getRegistryManager().getRegistry(authority);
                    crs = registry.getCoordinateReferenceSystem(new Identifier(authority, code, ""));
                    if (crs != null) {
                        CRSPOOL.put(authorityAndSrid.toUpperCase(), crs);
                    }
                }
            } catch (RegistryException ex) {
                throw new CRSException("Cannot create the CRS", ex);
            }
        }
        return crs;
    }

    /**
     * Return the registry name and the code in a string array.
     *
     * @param authorityAndSrid a string following the pattern "name:code"
     * @return an array of two strings (ex. {"EPSG", "4326"})
     * @throws RegistryException
     */
    public String[] splitRegistryNameAndCode(String authorityAndSrid) throws RegistryException {
        String[] registryAndCode = authorityAndSrid.split(":");
        if (registryAndCode.length == 2) {
            return registryAndCode;
        } else {
            throw new RegistryException("The registry pattern '" + authorityAndSrid + "' is not supported");
        }

    }

    /**
     * Return the {@link org.cts.registry.RegistryManager} used in CTS.
     *
     */
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    /**
     * Check if the registry name (ie EPSG, IGNF...) is supported.
     *
     * @param registryName (ex : ESPG, IGNF, ESRI)
     * @throws org.cts.registry.RegistryException
     */
    public boolean isRegistrySupported(String registryName) throws RegistryException {
        if (getRegistryManager().contains(registryName.toLowerCase())) {
            return true;
        } else {
            throw new RegistryException("Registry '" + registryName + "' is not supported");
        }
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param prjString the OGC WKT String defining the CRS
     * @return 
     * @throws org.cts.crs.CRSException
     */
    public CoordinateReferenceSystem createFromPrj(String prjString) throws CRSException {
        PrjParser p = new PrjParser();
        Map<String, String> prjParameters = p.getParameters(prjString);
        String name = prjParameters.remove(PrjKeyParameters.NAME);
        String refname = prjParameters.remove(PrjKeyParameters.REFNAME);
        if (refname != null) {
            String[] authorityNameWithKey = refname.split(":");
            return CRSHelper.createCoordinateReferenceSystem(new Identifier(authorityNameWithKey[0], authorityNameWithKey[1], name), prjParameters);
        } else {
            return CRSHelper.createCoordinateReferenceSystem(new Identifier(CoordinateReferenceSystem.class, name), prjParameters);
        }
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param stream the input stream of bytes defining the OGC WKT String
     * @param encoding the charset used to read the input stream
     * @return a CoordinateReferenceSystem
     * @throws IOException
     * @throws org.cts.crs.CRSException
     */
    public CoordinateReferenceSystem createFromPrj(InputStream stream, Charset encoding) throws IOException, CRSException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream, encoding));
        StringBuilder b = new StringBuilder();
        while (r.ready()) {
            b.append(r.readLine());
        }
        return createFromPrj(b.toString());
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param stream the input stream of bytes defining the OGC WKT String
     * @return a CoordinateReferenceSystem
     * @throws IOException
     * @throws org.cts.crs.CRSException
     */
    public CoordinateReferenceSystem createFromPrj(InputStream stream) throws IOException, CRSException {
        return createFromPrj(stream, Charset.defaultCharset());
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     * @return a CoordinateReferenceSystem
     * @param file containing the OGC WKT String that defined the desired CRS
     * @return 
     * @throws IOException if there is a problem reading the file
     * @throws org.cts.crs.CRSException
     */
    public CoordinateReferenceSystem createFromPrj(File file) throws IOException, CRSException {
        InputStream i = null;
        CoordinateReferenceSystem crs;
        try {
            i = new FileInputStream(file);
            crs = createFromPrj(i);
        } finally {
            if (i != null) {
                i.close();
            }
        }
        return crs;
    }

    /**
     * Return a list of supported codes according an registryName.
     *
     * @param registryName (ex : EPSG, IGNF, ESRI)
     * @return List of supported codes
     * @throws org.cts.registry.RegistryException
     */
    public Set<String> getSupportedCodes(String registryName) throws RegistryException {
        return getRegistryManager().getRegistry(registryName).getSupportedCodes();
    }
    
    
    /**
     * Creates a {@link CoordinateReferenceSystem} defined by a proj4 string
     * representation
     *
     * @param prj4String the proj4 string defining the CRS
     * @return
     * @throws org.cts.crs.CRSException
     */
    public CoordinateReferenceSystem createFromPrj4(String prj4String) throws CRSException {
        Map<String, String> prjParameters = Proj4Parser.readParameters(prj4String);
        String zone = prjParameters.get("zone");
        String crsName;
        if (zone != null) {
            crsName = prjParameters.get("south") == null ? String.format("UTM %s %s", zone, "NORTH") : String.format("UTM %s %s", zone, "SOUTH");
        }
        else{
            crsName = String.format("Unknown CRS %s",System.currentTimeMillis());
        }
        return CRSHelper.createCoordinateReferenceSystem(new Identifier(CoordinateReferenceSystem.class, crsName), prjParameters);
    }

    /**
     * A simple cache to manage {@link CoordinateReferenceSystem}
     */
    public class CRSCache<K, V> extends LinkedHashMap<K, V> {

        private final int limit;

        public CRSCache(int limit) {
            super(16, 0.75f, true);
            this.limit = limit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > limit;
        }
    }
}
