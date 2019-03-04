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

import java.util.HashMap;
import java.util.Map;

import org.cts.CTSTestCase;
import org.cts.Identifier;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.parser.proj.ProjKeyParameters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Erwan Bocher
 */
class RegistryParserTest extends CTSTestCase {

    @Test
    void testEPSG() throws Exception {
        Map<String, String> parameters = getParameters("epsg", "4326");
        //Expected 
        //# WGS 84
        //<4326> +proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs  <>                
        assertEquals("longlat", parameters.get(ProjKeyParameters.proj));
        assertEquals("WGS84", parameters.get(ProjKeyParameters.ellps));
        assertEquals("WGS84", parameters.get(ProjKeyParameters.datum));
    }

    @Test
    void testReadEPSGFile1() throws Exception {
        CoordinateReferenceSystem crs = cRSFactory.getRegistryManager().getRegistry("EPSG")
                .getCoordinateReferenceSystem(new Identifier("EPSG", "2154", null));
        assertEquals("EPSG:2154", crs.getCode(), "epsg:2154 complete code");
        assertEquals("EPSG", crs.getAuthorityName(), "epsg:2154 authority name");
        assertEquals("2154", crs.getAuthorityKey(), "epsg:2154 authority code");
        assertEquals("RGF93 / Lambert-93", crs.getName(), "epsg:2154 name");
        assertEquals(700000, crs.getProjection().getFalseEasting(), 1E-9, "epsg:2154 x_0");
    }

    @Test
    void testReadEPSGFile2() throws Exception {
        Map<String, String> parameters = getParameters("epsg", "2736");
        //Expected 
        //# Tete / UTM zone 36S
        //<2736> +proj=utm +zone=36 +south +ellps=clrk66 
        //+towgs84=-115.064,-87.39,-101.716,-0.058,4.001,-2.062,9.366 
        //+units=m +no_defs  <>             
        assertEquals("utm", parameters.get(ProjKeyParameters.proj));
        assertEquals("36", parameters.get(ProjKeyParameters.zone));
        assertNull(parameters.get(ProjKeyParameters.south));
        assertEquals("clrk66", parameters.get(ProjKeyParameters.ellps));
        assertEquals("-80,-100,-228,0,0,0,0", parameters.get(ProjKeyParameters.towgs84));
        assertEquals("m", parameters.get(ProjKeyParameters.units));
    }

    @Test
    void testReadEPSGFileWrongCode() throws Exception {
        Map<String, String> parameters = getParameters("EPSG", "300000");
        assertNull(parameters);
    }

    /**
     * Return parameters from a registry and a code
     *
     * @param registry
     * @param code
     * @return
     * @throws java.lang.Exception
     */
    private Map<String, String> getParameters(String registry, String code) throws Exception {
        //Map<String, String> parameters = cRSFactory.getRegistryManager().getRegistry(registry).getParameters(code);
        Registry reg  = cRSFactory.getRegistryManager().getRegistry(registry);
        if (reg instanceof AbstractProjRegistry) {
            return ((AbstractProjRegistry) reg).getParameters(code);
    }
        return new HashMap<String, String>();
    }

    @Test
    void testReadIGNFFile() throws Exception {
        Map<String, String> parameters = getParameters("IGNF", "RGF93");
        //Expected 
        //<RGF93> +title=Reseau geodesique francais 1993 
        //+proj=geocent +towgs84=0.0000,0.0000,0.0000 +a=6378137.0000 
        //+rf=298.2572221010000 +units=m +no_defs <> 
        assertEquals("Reseau geodesique francais 1993", parameters.get(ProjKeyParameters.title));
        assertEquals("geocent", parameters.get(ProjKeyParameters.proj));
        assertEquals("0.0000,0.0000,0.0000", parameters.get(ProjKeyParameters.towgs84));
        assertEquals("6378137.0000", parameters.get(ProjKeyParameters.a));
        assertEquals("298.2572221010000", parameters.get(ProjKeyParameters.rf));
        assertEquals("m", parameters.get(ProjKeyParameters.units));
        assertNull(parameters.get(ProjKeyParameters.no_defs));
    }

    @Test
    void testReadIGNFNadGrids() throws Exception {
        //Expected
        //<NTF> +title=Nouvelle Triangulation Francaise +proj=geocent +nadgrids=ntf_r93.gsb,null 
        //+towgs84=-168.0000,-60.0000,320.0000 +a=6378249.2000 +rf=293.4660210000000 
        //+units=m +no_defs <>
        Map<String, String> parameters = getParameters("IGNF", "NTF");
        assertEquals("Nouvelle Triangulation Francaise", parameters.get(ProjKeyParameters.title));
        assertEquals("geocent", parameters.get(ProjKeyParameters.proj));
        assertEquals("ntf_r93.gsb,null", parameters.get(ProjKeyParameters.nadgrids));
        assertEquals("-168.0000,-60.0000,320.0000", parameters.get(ProjKeyParameters.towgs84));
        assertEquals("6378249.2000", parameters.get(ProjKeyParameters.a));
        assertEquals("293.4660210000000", parameters.get(ProjKeyParameters.rf));
        assertEquals("m", parameters.get(ProjKeyParameters.units));
    }

    @Test
    void testReadESRIFile() throws Exception {
        Map<String, String> parameters = getParameters("ESRI", "102632");
        //<102632> +proj=tmerc +lat_0=54 +lon_0=-142 +k=0.999900 +x_0=500000.0000000002 
        //+y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192  no_defs <>
        assertEquals("tmerc", parameters.get(ProjKeyParameters.proj));
        assertEquals("54", parameters.get(ProjKeyParameters.lat_0));
        assertEquals("-142", parameters.get(ProjKeyParameters.lon_0));
        assertEquals("0.999900", parameters.get(ProjKeyParameters.k));
        assertEquals("500000.0000000002", parameters.get(ProjKeyParameters.x_0));
        assertEquals("0", parameters.get(ProjKeyParameters.y_0));
        assertEquals("GRS80", parameters.get(ProjKeyParameters.ellps));
        assertEquals("NAD83", parameters.get(ProjKeyParameters.datum));
        assertEquals("0.3048006096012192", parameters.get(ProjKeyParameters.to_meter));
    }

    @Test
    void testReadNAD27File() throws Exception {
        Map<String, String> parameters = getParameters("NAD27", "2001");
        //# 2001: massachusetts mainland: nad27
        //<2001> proj=lcc  datum=NAD27
        //lon_0=-71d30 lat_1=42d41 lat_2=41d43 lat_0=41
        //x_0=182880.3657607315 y_0=0
        //no_defs <>
        assertEquals("lcc", parameters.get(ProjKeyParameters.proj));
        assertEquals("41", parameters.get(ProjKeyParameters.lat_0));
        assertEquals("-71d30", parameters.get(ProjKeyParameters.lon_0));
        assertEquals("42d41", parameters.get(ProjKeyParameters.lat_1));
        assertEquals("41d43", parameters.get(ProjKeyParameters.lat_2));
        assertEquals("182880.3657607315", parameters.get(ProjKeyParameters.x_0));
        assertEquals("0", parameters.get(ProjKeyParameters.y_0));
        assertEquals("NAD27", parameters.get(ProjKeyParameters.datum));
    }

    @Test
    void testReadNAD83File() throws Exception {
        Map<String, String> parameters = getParameters("NAD83", "2112");
        //# 2112: michigan central/l: nad83
        //<2112> proj=lcc  datum=NAD83
        //lon_0=-84d22 lat_1=45d42 lat_2=44d11 lat_0=43d19
        //x_0=6000000 y_0=0
        //no_defs <>
        assertEquals("lcc", parameters.get(ProjKeyParameters.proj));
        assertEquals("43d19", parameters.get(ProjKeyParameters.lat_0));
        assertEquals("-84d22", parameters.get(ProjKeyParameters.lon_0));
        assertEquals("45d42", parameters.get(ProjKeyParameters.lat_1));
        assertEquals("44d11", parameters.get(ProjKeyParameters.lat_2));
        assertEquals("6000000", parameters.get(ProjKeyParameters.x_0));
        assertEquals("0", parameters.get(ProjKeyParameters.y_0));
        assertEquals("NAD83", parameters.get(ProjKeyParameters.datum));
    }

    @Test
    void testReadworldFile() throws Exception {
        Map<String, String> parameters = getParameters("world", "levant");
        //<levant> # Levant
        //proj=lcc ellps=clrk66 lat_1=34d39'N lon_0=37d21'E
        //x_0=500000 y_0=300000 k_0=0.9996256
        //no_defs <>
        assertEquals("lcc", parameters.get(ProjKeyParameters.proj));
        assertEquals("0.9996256", parameters.get(ProjKeyParameters.k_0));
        assertEquals("37d21'E", parameters.get(ProjKeyParameters.lon_0));
        assertEquals("34d39'N", parameters.get(ProjKeyParameters.lat_1));
        assertEquals("500000", parameters.get(ProjKeyParameters.x_0));
        assertEquals("300000", parameters.get(ProjKeyParameters.y_0));
        assertEquals("clrk66", parameters.get(ProjKeyParameters.ellps));
    }

    @Test
    void testRegisteryCaseInsensitive() throws Exception {
        Map<String, String> parameters = getParameters("IGnF", "AmSt63");
        assertNotNull(parameters);
        parameters = getParameters("EPsg", "4326");
        assertNotNull(parameters);
    }
}
