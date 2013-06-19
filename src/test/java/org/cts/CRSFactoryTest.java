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

import org.cts.crs.CoordinateReferenceSystem;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class CRSFactoryTest {
    
    public CRSFactory cRSFactory = new CRSFactory();
    
    //@Test waiting the leac projection
    public void testCRSFroomLAMBEDeprecated(){
        String prj = "PROJCS[\"NTF (Paris) / France II (deprecated)\",GEOGCS[\"NTF (Paris)\","
                 + "DATUM[\"Nouvelle_Triangulation_Francaise_Paris\",SPHEROID[\"Clarke 1880 (IGN)\",6378249.2,293.4660212936269,AUTHORITY[\"EPSG\",\"7011\"]],TOWGS84[-168,-60,320,0,0,0,0],AUTHORITY[\"EPSG\",\"6807\"]],"
                 + "PRIMEM[\"Paris\",2.33722917,AUTHORITY[\"EPSG\",\"8903\"]],UNIT[\"grad\",0.01570796326794897,AUTHORITY[\"EPSG\",\"9105\"]],AUTHORITY[\"EPSG\",\"4807\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],"
                 + "PROJECTION[\"Lambert_Conformal_Conic_1SP\"],PARAMETER[\"latitude_of_origin\",52],PARAMETER[\"central_meridian\",0],PARAMETER[\"scale_factor\",0.99987742],PARAMETER[\"false_easting\",600000],PARAMETER[\"false_northing\",2200000],AUTHORITY[\"EPSG\",\"27582\"],"
                 + "AXIS[\"X\",EAST],AXIS[\"Y\",NORTH]]";
        CoordinateReferenceSystem crs = cRSFactory.createFromPrj(prj);
        assertNotNull(crs);
        assertTrue(crs.getName().equals("NTF (Paris) / France II (deprecated)"));
    }
    
}
