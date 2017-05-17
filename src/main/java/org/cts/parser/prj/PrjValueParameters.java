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
package org.cts.parser.prj;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to manage all values used to fill a PRJ file
 *
 * @author Erwan Bocher, Jules Party, Michaël Michaud
 */
public class PrjValueParameters {

    /**
     * A map linking the name of parameters used in PRJ file to the name used in
     * CTS.
     */
    public static final Map<String, String> PARAMNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of projections used in PRJ file to the short name
     * used in CTS.
     */
    public static final Map<String, String> PROJNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of datums used in PRJ file to the short name used
     * in CTS.
     */
    public static final Map<String, String> DATUMNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of ellipsoids used in PRJ file to the short name
     * used in CTS.
     */
    public static final Map<String, String> ELLIPSOIDNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of prime meridians used in PRJ file to the short
     * name used in CTS.
     */
    public static final Map<String, String> PRIMEMERIDIANNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of units used in PRJ file to the short name used
     * in CTS.
     */
    public static final Map<String, String> UNITNAMES = new HashMap<String, String>();
    /**
     * A map linking the name of axes used in PRJ file to the short name used in
     * CTS.
     */
    public static final Map<String, String> AXISNAMES = new HashMap<String, String>();

    static {
        PARAMNAMES.put("centralmeridian", "lon_0");
        PARAMNAMES.put("longitudeofcenter", "lon_0");
        PARAMNAMES.put("falseeasting", "x_0");
        PARAMNAMES.put("falsenorthing", "y_0");
        PARAMNAMES.put("latitudeoforigin", "lat_0");
        PARAMNAMES.put("latitudeofcenter", "lat_0");
        PARAMNAMES.put("scalefactor", "k_0");
        PARAMNAMES.put("standardparallel1", "lat_1");
        PARAMNAMES.put("standardparallel2", "lat_2");
        PARAMNAMES.put("azimuth", "alpha");
        PARAMNAMES.put("rectifiedgridangle", "gamma");
        PROJNAMES.put("airy", "airy");
        PROJNAMES.put("aitoff", "aitoff");
        PROJNAMES.put("albers" + "equal" + "area", "aea");
        PROJNAMES.put("august" + "epicycloidal", "august");
        PROJNAMES.put("bipolar" + "conic" + "of" + "western" + "hemisphere", "aeqd");
        PROJNAMES.put("boggs" + "eumorphic", "bipc");
        PROJNAMES.put("bonne", "bonne");
        PROJNAMES.put("cassini", "cass");
        PROJNAMES.put("cassini" + "soldner", "cass");
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
        PROJNAMES.put("hotine" + "oblique" + "mercator", "omerc");
        PROJNAMES.put("kavraisky" + "v", "kav5");
        PROJNAMES.put("lagrange", "lagrng");
        PROJNAMES.put("lambert" + "azimuthal" + "equal" + "area", "laea");
        PROJNAMES.put("lambert" + "conformal" + "conic", "lcc");
        PROJNAMES.put("lambert" + "conic" + "conformal", "lcc");
        PROJNAMES.put("lambert" + "conformal" + "conic" + "1sp", "lcc");
        PROJNAMES.put("lambert" + "conic" + "conformal" + "1sp", "lcc");
        PROJNAMES.put("lambert" + "conformal" + "conic" + "2sp", "lcc");
        PROJNAMES.put("lambert" + "conic" + "conformal" + "2sp", "lcc");
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
        PROJNAMES.put("popularvisualisationpseudomercator", "merc");
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
        DATUMNAMES.put("greekgeodeticreferencesystem1987", "ggrs87");
        DATUMNAMES.put("ireland1965", "ire65");
        DATUMNAMES.put("newzealandgeodeticdatum1949", "nzgd49");
        DATUMNAMES.put("northamerican1927", "nad27");
        DATUMNAMES.put("northamerican1983", "nad83");
        DATUMNAMES.put("northamericandatum1927", "nad27");
        DATUMNAMES.put("northamericandatum1983", "nad83");
        DATUMNAMES.put("nouvelletriangulationfrancaiseparis", "ntfparis");
        DATUMNAMES.put("potsdamrauenberg1950dhdn", "potsdam");
        DATUMNAMES.put("wgs84", "wgs84");
        DATUMNAMES.put("wgs1984", "wgs84");
        DATUMNAMES.put("worldgeodeticsystem1984", "wgs84");
        DATUMNAMES.put("rgf93", "rgf93");
        DATUMNAMES.put("rgf1993", "rgf93");
        DATUMNAMES.put("reseaugeodesiquefrancais1993", "rgf93");
        DATUMNAMES.put("ed50", "ed50");
        DATUMNAMES.put("europeandatum1950", "ed50");
        DATUMNAMES.put("ntf", "ntf");
        DATUMNAMES.put("ntfparis", "ntfparis");
        DATUMNAMES.put("nivellementgeneraldelafranceign69", "ign69");
        DATUMNAMES.put("ign78corsica", "ign78");
        DATUMNAMES.put("guadeloupe1988", "ign88gtbt");
        DATUMNAMES.put("ign1992ld", "ign92ld");
        DATUMNAMES.put("ign1988ls", "ign88ls");
        DATUMNAMES.put("martinique1987", "ign87mart");
        DATUMNAMES.put("ign1988mg", "ign88mg");
        DATUMNAMES.put("ign1988sb", "ign88sb");
        DATUMNAMES.put("ign1988sm", "ign88sm");
        DATUMNAMES.put("nivellementgeneralguyanais1977", "ngg77guy");
        DATUMNAMES.put("mayotte1950", "shom53");
        DATUMNAMES.put("danger1950", "danger50");
        DATUMNAMES.put("Bora Bora SAU 2001", "bora");
        DATUMNAMES.put("huahinesau2001", "huahine");
        DATUMNAMES.put("ign1966", "ign66tahiti");
        DATUMNAMES.put("maupitisau2001", "maupiti");
        DATUMNAMES.put("mooreasau1981", "moorea");
        DATUMNAMES.put("raiateasau2001", "raiatea");
        DATUMNAMES.put("tahaasau2001", "tahaa");
        DATUMNAMES.put("Reunion 1989", "rar07");
        ELLIPSOIDNAMES.put("airy", "airy");
        ELLIPSOIDNAMES.put("airy1830", "airy");
        ELLIPSOIDNAMES.put("austsa", "austsa");
        ELLIPSOIDNAMES.put("grs1967modified", "austsa");
        ELLIPSOIDNAMES.put("grs1967sad69", "austsa");
        ELLIPSOIDNAMES.put("bessel", "bessel");
        ELLIPSOIDNAMES.put("bessel1841", "bessel");
        ELLIPSOIDNAMES.put("bessnam", "bessnam");
        ELLIPSOIDNAMES.put("besselnamibiaglm", "bessnam");
        ELLIPSOIDNAMES.put("clrk66", "clrk66");
        ELLIPSOIDNAMES.put("clarke1866", "clrk66");
        ELLIPSOIDNAMES.put("clrk80", "clrk80");
        ELLIPSOIDNAMES.put("clarke1880rgs", "clrk80");
        ELLIPSOIDNAMES.put("Clarke1880IGN", "clrk80ign");
        ELLIPSOIDNAMES.put("Clarke1880Arc", "clrk80arc");
        ELLIPSOIDNAMES.put("evrstss", "evrstss");
        ELLIPSOIDNAMES.put("everest18301967definition", "evrstss");
        ELLIPSOIDNAMES.put("grs67", "grs67");
        ELLIPSOIDNAMES.put("grs1967", "grs67");
        ELLIPSOIDNAMES.put("grs80", "grs80");
        ELLIPSOIDNAMES.put("grs1980", "grs80");
        ELLIPSOIDNAMES.put("helmert", "helmert");
        ELLIPSOIDNAMES.put("helmert1906", "helmert");
        ELLIPSOIDNAMES.put("intl", "intl");
        ELLIPSOIDNAMES.put("international1924", "intl");
        ELLIPSOIDNAMES.put("modairy", "airymod");
        ELLIPSOIDNAMES.put("airymodified1849", "airymod");
        ELLIPSOIDNAMES.put("krass", "krass");
        ELLIPSOIDNAMES.put("krassowsky1940", "krass");
        ELLIPSOIDNAMES.put("wgs66", "wgs66");
        ELLIPSOIDNAMES.put("nwl9d", "wgs66");
        ELLIPSOIDNAMES.put("wgs72", "wgs72");
        ELLIPSOIDNAMES.put("wgs84", "wgs84");
        PRIMEMERIDIANNAMES.put("greenwich", "greenwich");
        PRIMEMERIDIANNAMES.put("paris", "paris");
        PRIMEMERIDIANNAMES.put("lisbon", "lisbon");
        PRIMEMERIDIANNAMES.put("bogota", "bogota");
        PRIMEMERIDIANNAMES.put("madrid", "madrid");
        PRIMEMERIDIANNAMES.put("rome", "rome");
        PRIMEMERIDIANNAMES.put("bern", "bern");
        PRIMEMERIDIANNAMES.put("jakarta", "jakarta");
        PRIMEMERIDIANNAMES.put("ferro", "ferro");
        PRIMEMERIDIANNAMES.put("brussels", "brussels");
        PRIMEMERIDIANNAMES.put("stockholm", "stockholm");
        PRIMEMERIDIANNAMES.put("athens", "athens");
        PRIMEMERIDIANNAMES.put("oslo", "oslo");
        UNITNAMES.put("meter", "m");
        UNITNAMES.put("metre", "m");
        UNITNAMES.put("radian", "rad");
        UNITNAMES.put("degree", "\u00B0");
        UNITNAMES.put("minute", "'");
        UNITNAMES.put("second", "\"");
        UNITNAMES.put("grad", "g");
        UNITNAMES.put("kilometer", "km");
        UNITNAMES.put("kilometre", "km");
        UNITNAMES.put("foot", "ft");
        UNITNAMES.put("foot_us", "us-ft");
        UNITNAMES.put("yard", "yd");
        AXISNAMES.put("geocentricx", "x");
        AXISNAMES.put("geocentricy", "y");
        AXISNAMES.put("geocentricz", "z");
        AXISNAMES.put("x", "x");
        AXISNAMES.put("y", "y");
        AXISNAMES.put("z", "z");
        AXISNAMES.put("easting", "easting");
        AXISNAMES.put("northing", "northing");
        AXISNAMES.put("latitude", "latitude");
        AXISNAMES.put("longitude", "longitude");
        AXISNAMES.put("geodeticlatitude", "latitude");
        AXISNAMES.put("geodeticlongitude", "longitude");
        AXISNAMES.put("gravityrelatedheight", "altitude");
    }
}