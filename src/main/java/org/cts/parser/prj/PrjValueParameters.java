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

/**
 * A class to manage all values used to fill a PRJ file
 * @author Erwan Bocher
 */
public class PrjValueParameters {

    public static final String[] LONGPARAMNAMES = {
        "centralmeridian",
        "falseeasting",
        "falsenorthing",
        "latitudeoforigin",
        "scalefactor",
        "standardparallel1",
        "standardparallel2"
    };
    public static final String[] SHORTPARAMNAMES = {
        "lon_0",
        "x_0",
        "y_0",
        "lat_0",
        "k_0",
        "lat_1",
        "lat_2"
    };
    public static final String[] LONGPROJNAMES = {
        "airy",
        "aitoff",
        "albers" + "equal" + "area",
        "august" + "epicycloidal",
        "azimuthal" + "equidistant",
        "bipolar" + "conic" + "of" + "western" + "hemisphere",
        "boggs" + "eumorphic",
        "bonne",
        "cassini",
        "central" + "cylindrical",
        "collignon",
        "craster" + "parabolic",
        "denoyer" + "semi" + "elliptical",
        "eckert" + "i",
        "eckert" + "ii",
        "eckert" + "iv",
        "eckert" + "v",
        "eckert" + "vi",
        "equidistant" + "conic",
        "equidistant" + "cylindrical",
        "euler",
        "fahey",
        "foucaut",
        "foucaut" + "sinusoidal",
        "gall",
        "gnomonic",
        "goode" + "homolosine",
        "hammer" + "eckert" + "greifendorff",
        "hatano" + "asymmetrical" + "equal" + "area",
        "kavraisky" + "v",
        "lagrange",
        "lambert" + "azimuthal" + "equal" + "area",
        "lambert" + "conformal" + "conic",
        "lambert" + "conformal" + "conic" + "1sp",
        "lambert" + "conformal" + "conic" + "2sp",
        "lambert" + "equal" + "area" + "conic",
        "landsat",
        "larrivee",
        "laskowski",
        "latlong",
        "longlat",
        "loximuthal",
        "mcbryde" + "thomas" + "flat" + "polar" + "parabolic",
        "mcbryde" + "thomas" + "flat" + "polar" + "quartic",
        "mcbryde" + "thomas" + "flat" + "pole" + "sine" + "2",
        "mercator",
        "miller" + "cylindrical",
        "mollweide",
        "murdoch" + "i",
        "murdoch" + "ii",
        "murdoch" + "iii",
        "near" + "sided" + "perspective",
        "nell",
        "nicolosi" + "globular",
        "oblique" + "mercator",
        "oblique" + "stereographic" + "alternative",
        "orthographic",
        "perspective" + "conic",
        "polyconic",
        "putnins" + "p2",
        "putnins" + "p4",
        "putnins" + "p5",
        "putnins" + "p5p",
        "quartic" + "authalic",
        "robinson",
        "rectangular" + "polyconic",
        "sinusoidal",
        "stereographic",
        "swiss" + "oblique" + "mercator",
        "transverse" + "central" + "cylindrical",
        "transverse" + "cylindrical" + "equal" + "area",
        "transverse" + "mercator",
        "universal" + "transverse" + "mercator",
        "urmaev" + "flat" + "polar" + "sinusoidal",
        "van" + "der" + "grinten",
        "vitkovsky" + "i",
        "wagner" + "i",
        "wagner" + "ii",
        "wagner" + "iii",
        "wagner" + "iv",
        "wagner" + "v",
        "wagner" + "vii",
        "werenskiold" + "i",
        "winkel" + "tripel"
    };
    public static final String[] SHORTPROJNAMES = {
        "airy",
        "aitoff",
        "aea",
        "august",
        "aeqd",
        "bipc",
        "boggs",
        "bonne",
        "cass",
        "cc",
        "collg",
        "crast",
        "denoy",
        "eck1",
        "eck2",
        "eck4",
        "eck5",
        "eck6",
        "eqdc",
        "eqc",
        "euler",
        "fahey",
        "fouc",
        "fouc_s",
        "gall",
        "gnom",
        "goode",
        "hammer",
        "hatano",
        "kav5",
        "lagrng",
        "laea",
        "lcc",
        "leac",
        "lsat",
        "larr",
        "lask",
        "latlong",
        "longlat",
        "loxim",
        "mbtfpp",
        "mbtfpq",
        "mbt_fps",
        "merc",
        "mill",
        "moll",
        "murd1",
        "murd2",
        "murd3",
        "nsper",
        "nell",
        "nicol",
        "omerc",
        "sterea",
        "ortho",
        "pconic",
        "poly",
        "putp2",
        "putp4p",
        "putp5",
        "putp5p",
        "qua_aut",
        "robin",
        "rpoly",
        "sinu",
        "stere",
        "somerc",
        "tcc",
        "tcea",
        "tmerc",
        "utm",
        "urmfps",
        "vandg",
        "vitk1",
        "wag1",
        "wag2",
        "wag3",
        "wag4",
        "wag5",
        "wag7",
        "weren",
        "wintri"
    };
    public static final String[] LONGDATUMNAMES = new String[]{
        "airy1830",
        "carthage1934tunisia",
        "dntf",
        "greekgeodeticreferencesystem1987",
        "ireland1965",
        "newzealandgeodeticdatum1949",
        "northamericandatum1927",
        "northamericandatum1983",
        "nouvelletriangulationfrancaiseparis",
        "potsdamrauenberg1950dhdn"
    };
    public static final String[] SHORTDATUMNAMES = new String[]{
        "osgb36",
        "carthage",
        "ntf",
        "ggrs87",
        "ire65",
        "nzgd49",
        "nad27",
        "nad83",
        "ntf",
        "potsdam"
    };
}
