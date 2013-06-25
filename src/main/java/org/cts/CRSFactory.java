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

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.prj.PrjParser;
import org.cts.registry.Registry;
import org.cts.registry.RegistryManager;

/**
 * This factory is in charge of creating new
 * {@link org.cts.crs.CoordinateReferenceSystem}s. It can do so by two way :
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
     * Return a {@link org.cts.crs.CoordinateReferenceSystem} cooresponding to
     * an authority and a srid.
     *
     * @param authorityAndSrid the code of the desired CRS (for instance
     * EPSG:4326 or IGNF:LAMBE)
     * @throws CRSException
     */
    public CoordinateReferenceSystem getCRS(String authorityAndSrid) throws CRSException {
        CoordinateReferenceSystem crs = CRSPOOL.get(authorityAndSrid);
        if (crs == null) {
            if (isRegistrySupported(authorityAndSrid)) {
                String[] registryNameWithCode = authorityAndSrid.split(":");
                Registry registry = getRegistryManager().getRegistry(registryNameWithCode[0]);
                Map<String, String> crsParameters = registry.getParameters(registryNameWithCode[1]);
                if (crsParameters != null) {
                    crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(registryNameWithCode[0], registryNameWithCode[1], crsParameters.get("title")), crsParameters);
                }
                if (crs != null) {
                    CRSPOOL.put(authorityAndSrid, crs);
                }
            }
        }
        return crs;
    }

    /**
     * Return the {@link org.cts.registry.RegistryManager} used in CTS.
     *
     */
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    /**
     * Check if the registry name (ie EPSG, IGNF...) of the crsCode is
     * supported.
     *
     * @param crsCode ie <code>authorityName:authorityKey</code> (ex
     * : <code>EPSG:4326</code> or <code>IGNF:LAMBE</code>)
     */
    public boolean isRegistrySupported(String crsCode) {
        int p = crsCode.indexOf(':');
        if (p >= 0) {
            String auth = crsCode.substring(0, p);
            if (getRegistryManager().contains(auth.toLowerCase())) {
                return true;
            }
        }
        throw new RuntimeException("This registry is not supported");
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param prjString the OGC WKT String defining the CRS
     */
    public CoordinateReferenceSystem createFromPrj(String prjString) {
        PrjParser p = new PrjParser();
        Map<String, String> prjParameters = p.getParameters(prjString);
        String name = prjParameters.remove("name");
        String refname = prjParameters.remove("refname");
        CoordinateReferenceSystem crs;
        if (refname != null) {
            crs = CRSPOOL.get(refname);
            if (crs == null) {
                String[] authorityNameWithKey = refname.split(":");
                crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(authorityNameWithKey[0], authorityNameWithKey[1], name), prjParameters);
            }
            if (crs != null) {
                CRSPOOL.put(refname, crs);
            }
        } else {
            crs = CRSHelper.createCoordinateReferenceSystem(new Identifier(name, name, name), prjParameters);
        }
        return crs;
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param stream the input stream of bytes defining the OGC WKT String
     * @param encoding the charset used to read the input stream
     * @throws IOException
     */
    public CoordinateReferenceSystem createFromPrj(InputStream stream, Charset encoding) throws IOException {
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
     * @throws IOException
     */
    public CoordinateReferenceSystem createFromPrj(InputStream stream) throws IOException {
        return createFromPrj(stream, Charset.defaultCharset());
    }

    /**
     * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT String
     * (PRJ).
     *
     * @param file containing the OGC WKT String that defined the desired CRS
     * @throws IOException if there is a problem reading the file
     */
    public CoordinateReferenceSystem createFromPrj(File file) throws IOException {
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
     */
    public Set<String> getSupportedCodes(String registryName) {
        return getRegistryManager().getRegistry(registryName).getSupportedCodes();
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
