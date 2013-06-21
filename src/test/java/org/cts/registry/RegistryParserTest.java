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

import java.util.Map;
import org.cts.CTSTestCase;
import org.cts.parser.proj.ProjKeyParameters;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Erwan Bocher
 */
public class RegistryParserTest extends CTSTestCase{
        

        @Test
        public void testEPSG() {
                Map<String, String> parameters = getParameters("epsg", "4326");
                //Expected 
                //# WGS 84
                //<4326> +proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs  <>                
                assertTrue(parameters.get(ProjKeyParameters.proj).equals("longlat"));
                assertTrue(parameters.get(ProjKeyParameters.ellps).equals("WGS84"));
                assertTrue(parameters.get(ProjKeyParameters.datum).equals("WGS84"));
        }

        @Test
        public void testReadEPSGFile2() {
                Map<String, String> parameters = getParameters("epsg", "2736");
                //Expected 
                //# Tete / UTM zone 36S
                //<2736> +proj=utm +zone=36 +south +ellps=clrk66 
                //+towgs84=-115.064,-87.39,-101.716,-0.058,4.001,-2.062,9.366 
                //+units=m +no_defs  <>             
                assertTrue(parameters.get(ProjKeyParameters.proj).equals("utm"));
                assertTrue(parameters.get(ProjKeyParameters.zone).equals("36"));
                assertTrue(parameters.get(ProjKeyParameters.south)==null);
                assertTrue(parameters.get(ProjKeyParameters.ellps).equals("clrk66"));
                assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("-115.064,-87.39,-101.716,-0.058,4.001,-2.062,9.366"));
                assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
        }

        @Test
        public void testReadEPSGFileWrongCode() {
                Map<String, String> parameters = getParameters("EPSG", "300000");
                assertTrue(parameters == null);
        }

        /**
         * Return parameters from a registry and a code
         *
         * @param registry
         * @param code
         * @return
         */
        public Map<String, String> getParameters(String registry, String code) {
                Map<String, String> parameters = cRSFactory.getRegistryManager().getRegistry(registry).getParameters(code);
                return parameters;
        }
        
        @Test
        public void testReadIGNFFile() {
                Map<String, String>  parameters = getParameters("IGNF","RGF93");
                //Expected 
                //<RGF93> +title=Reseau geodesique francais 1993 
                //+proj=geocent +towgs84=0.0000,0.0000,0.0000 +a=6378137.0000 
                //+rf=298.2572221010000 +units=m +no_defs <> 
                assertTrue(parameters.get(ProjKeyParameters.title).equals("Reseau geodesique francais 1993"));
                assertTrue(parameters.get(ProjKeyParameters.proj).equals("geocent"));
                assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("0.0000,0.0000,0.0000"));
                assertTrue(parameters.get(ProjKeyParameters.a).equals("6378137.0000"));
                assertTrue(parameters.get(ProjKeyParameters.rf).equals("298.2572221010000"));
                assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
                assertTrue(parameters.get(ProjKeyParameters.no_defs)==null);
        }
        
        @Test
        public void testReadIGNFNadGrids(){
             //Expected
             //<NTF> +title=Nouvelle Triangulation Francaise +proj=geocent +nadgrids=ntf_r93.gsb,null 
             //+towgs84=-168.0000,-60.0000,320.0000 +a=6378249.2000 +rf=293.4660210000000 
             //+units=m +no_defs <>
             Map<String, String>  parameters = getParameters("IGNF","NTF");
             assertTrue(parameters.get(ProjKeyParameters.title).equals("Nouvelle Triangulation Francaise"));
             assertTrue(parameters.get(ProjKeyParameters.proj).equals("geocent"));
             assertTrue(parameters.get(ProjKeyParameters.nadgrids).equals("ntf_r93.gsb,null"));
             assertTrue(parameters.get(ProjKeyParameters.towgs84).equals("-168.0000,-60.0000,320.0000"));                
             assertTrue(parameters.get(ProjKeyParameters.a).equals("6378249.2000"));
             assertTrue(parameters.get(ProjKeyParameters.rf).equals("293.4660210000000"));
             assertTrue(parameters.get(ProjKeyParameters.units).equals("m"));
        }
        
        @Test
        public void testReadESRIFile() {
                Map<String, String>  parameters = getParameters("ESRI","102632");
                //<102632> +proj=tmerc +lat_0=54 +lon_0=-142 +k=0.999900 +x_0=500000.0000000002 
                //+y_0=0 +ellps=GRS80 +datum=NAD83 +to_meter=0.3048006096012192  no_defs <>
                assertTrue(parameters.get(ProjKeyParameters.proj).equals("tmerc"));
                assertTrue(parameters.get(ProjKeyParameters.lat_0).equals("54"));
                assertTrue(parameters.get(ProjKeyParameters.lon_0).equals("-142"));
                assertTrue(parameters.get(ProjKeyParameters.k).equals("0.999900"));
                assertTrue(parameters.get(ProjKeyParameters.x_0).equals("500000.0000000002"));
                assertTrue(parameters.get(ProjKeyParameters.y_0).equals("0"));
                assertTrue(parameters.get(ProjKeyParameters.ellps).equals("GRS80"));
                assertTrue(parameters.get(ProjKeyParameters.datum).equals("NAD83"));
                assertTrue(parameters.get(ProjKeyParameters.to_meter).equals("0.3048006096012192"));
        }        
        
        @Test
        public void testRegisteryCaseInsensitive () {
                Map<String, String>  parameters = getParameters("IGnF","AmSt63");
                assertTrue(parameters!=null);
                parameters = getParameters("EPsg","4326");
                assertTrue(parameters!=null);
        }
}
