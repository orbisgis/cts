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
package org.cts.parser.prj;

import java.util.HashMap;

/**
 * A class to manage all values used to fill a PRJ file
 * @author Erwan Bocher, Jules Party
 */
public class PrjValueParameters {
    /**
     * A map linking the name of parameters used in PRJ file to the name used in CTS.
     */
    public static final HashMap<String, String> PARAMNAMES = new HashMap<String, String>();
    
    /**
     * A map linking the name of projections used in PRJ file to the short name used in CTS.
     */
    public static final HashMap<String, String> PROJNAMES = new HashMap<String, String>();
    
    /**
     * A map linking the name of datums used in PRJ file to the short name used in CTS.
     */
    public static final HashMap<String, String> DATUMNAMES = new HashMap<String, String>();
    
    static {
        PARAMNAMES.put("centralmeridian", "lon_0");
        PARAMNAMES.put("falseeasting", "x_0");
        PARAMNAMES.put("falsenorthing", "y_0");
        PARAMNAMES.put("latitudeoforigin", "lat_0");
        PARAMNAMES.put("scalefactor", "k_0");
        PARAMNAMES.put("standardparallel1", "lat_1");
        PARAMNAMES.put("standardparallel2", "lat_2");
        PROJNAMES.put("airy", "airy");
        PROJNAMES.put("aitoff", "aitoff");
        PROJNAMES.put("albers" + "equal" + "area", "aea");
        PROJNAMES.put("august" + "epicycloidal", "august");
        PROJNAMES.put("bipolar" + "conic" + "of" + "western" + "hemisphere", "aeqd");
        PROJNAMES.put("boggs" + "eumorphic", "bipc");
        PROJNAMES.put("bonne", "bonne");
        PROJNAMES.put("cassini", "cass");
        PROJNAMES.put("central" + "cylindrical", "cc");
        PROJNAMES.put("collignon", "collg");
        PROJNAMES.put("craster" + "parabolic", "crast");
        PROJNAMES.put("denoyer" + "semi" + "elliptical", "denoy");
        PROJNAMES.put("eckert" + "i", "eck1");
        PROJNAMES.put("eckert" + "ii", "eck2");
        PROJNAMES.put("eckert" + "iv", "eck4");
        PROJNAMES.put("eckert" + "v", "eck5");
        PROJNAMES.put("eckert" + "vi", "eck6");
        PROJNAMES.put("equidistant" + "conic", "eqdc");
        PROJNAMES.put("equidistant" + "cylindrical", "eqc");
        PROJNAMES.put("euler", "euler");
        PROJNAMES.put("fahey", "fahey");
        PROJNAMES.put("foucaut", "fouc");
        PROJNAMES.put("foucaut" + "sinusoidal", "fouc_s");
        PROJNAMES.put("gall", "gall");
        PROJNAMES.put("gnomonic", "gnom");
        PROJNAMES.put("goode" + "homolosine", "goode");
        PROJNAMES.put("hammer" + "eckert" + "greifendorff", "hammer");
        PROJNAMES.put("hatano" + "asymmetrical" + "equal" + "area", "hatano");
        PROJNAMES.put("kavraisky" + "v","kav5");
        PROJNAMES.put("lagrange", "lagrng");
        PROJNAMES.put("lambert" + "azimuthal" + "equal" + "area", "laea");
        PROJNAMES.put("lambert" + "conformal" + "conic", "lcc");
        PROJNAMES.put("lambert" + "conformal" + "conic" + "1sp", "lcc");
        PROJNAMES.put("lambert" + "conformal" + "conic" + "2sp", "lcc");
        PROJNAMES.put("lambert" + "equal" + "area" + "conic", "leac");
        PROJNAMES.put("landsat", "lsat");
        PROJNAMES.put("larrivee", "larr");
        PROJNAMES.put("laskowski", "lask");
        PROJNAMES.put("latlong", "latlong");
        PROJNAMES.put("longlat", "longlat");
        PROJNAMES.put("loximuthal", "loxim");
        PROJNAMES.put("mcbryde" + "thomas" + "flat" + "polar" + "parabolic", "mbtfpp");
        PROJNAMES.put("mcbryde" + "thomas" + "flat" + "polar" + "quartic", "mbtfpq");
        PROJNAMES.put("mcbryde" + "thomas" + "flat" + "pole" + "sine" + "2", "mbt_fps");
        PROJNAMES.put("mercator", "merc");
        PROJNAMES.put("mercator" + "1sp", "merc");
        PROJNAMES.put("miller" + "cylindrical", "mill");
        PROJNAMES.put("mollweide", "moll");
        PROJNAMES.put("murdoch" + "i", "murd1");
        PROJNAMES.put("murdoch" + "ii", "murd2");
        PROJNAMES.put("murdoch" + "iii", "murd3");
        PROJNAMES.put("near" + "sided" + "perspective", "nsper");
        PROJNAMES.put("nell", "nell");
        PROJNAMES.put("nicolosi" + "globular", "nicol");
        PROJNAMES.put("oblique" + "mercator", "omerc");
        PROJNAMES.put("oblique" + "stereographic" + "alternative", "sterea");
        PROJNAMES.put("orthographic", "ortho");
        PROJNAMES.put("perspective" + "conic", "pconic");
        PROJNAMES.put("polyconic", "poly");
        PROJNAMES.put("putnins" + "p2", "putp2");
        PROJNAMES.put("putnins" + "p4", "putp4p");
        PROJNAMES.put("putnins" + "p5", "putp5");
        PROJNAMES.put("putnins" + "p5p", "putp5p");
        PROJNAMES.put("quartic" + "authalic", "qua_aut");
        PROJNAMES.put("robinson", "robin");
        PROJNAMES.put("rectangular" + "polyconic", "rpoly");
        PROJNAMES.put("sinusoidal", "sinu");
        PROJNAMES.put("stereographic", "stere");
        PROJNAMES.put("swiss" + "oblique" + "mercator", "somerc");
        PROJNAMES.put("transverse" + "central" + "cylindrical", "tcc");
        PROJNAMES.put("transverse" + "cylindrical" + "equal" + "area", "tcea");
        PROJNAMES.put("transverse" + "mercator", "tmerc");
        PROJNAMES.put("universal" + "transverse" + "mercator", "utm");
        PROJNAMES.put("urmaev" + "flat" + "polar" + "sinusoidal", "urmfps");
        PROJNAMES.put("van" + "der" + "grinten", "vandg");
        PROJNAMES.put("vitkovsky" + "i", "vitk1");
        PROJNAMES.put("wagner" + "i", "wag1");
        PROJNAMES.put("wagner" + "ii", "wag2");
        PROJNAMES.put("wagner" + "iii", "wag3");
        PROJNAMES.put("wagner" + "iv", "wag4");
        PROJNAMES.put("wagner" + "v", "wag5");
        PROJNAMES.put("wagner" + "vii", "wag7");
        PROJNAMES.put("werenskiold" + "i", "werren");
        PROJNAMES.put("winkel" + "tripel", "wintri");
        DATUMNAMES.put("airy1830", "osgb36");
        DATUMNAMES.put("carthage1934tunisia", "carthage");
        DATUMNAMES.put("dntf", "ntf");
        DATUMNAMES.put("greekgeodeticreferencesystem1987", "ggrs87");
        DATUMNAMES.put("ireland1965", "ire65");
        DATUMNAMES.put("newzealandgeodeticdatum1949", "nzgd49");
        DATUMNAMES.put("northamericandatum1927", "nad27");
        DATUMNAMES.put("northamericandatum1983", "nad83");
        DATUMNAMES.put("nouvelletriangulationfrancaiseparis", "ntf");
        DATUMNAMES.put("potsdamrauenberg1950dhdn", "potsdam");
    }
}