/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cts.crs.CRSHelper;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;

/**
 *
 * @author Antoine Gourlay, Erwan Bocher
 */
public final class PrjMatcher {

    private static final double TOL = 1.0E-100;

    private PrjMatcher() {
    }
    private Map<String, String> params = new HashMap<String, String>();
    private static final String[] LONGPARAMNAMES = {
        "centralmeridian",
        "falseeasting",
        "falsenorthing",
        "latitudeoforigin",
        "scalefactor",
        "standardparallel1",
        "standardparallel2"
    };
    private static final String[] SHORTPARAMNAMES = {
        "lon_0",
        "x_0",
        "y_0",
        "lat_0",
        "k_0",
        "lat_1",
        "lat_2"
    };
    private static final String[] LONGPROJNAMES = {
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
    private static final String[] SHORTPROJNAMES = {
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
        "lcc",
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
    private static final String[] LONGDATUMNAMES = new String[]{
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
    private static final String[] SHORTDATUMNAMES = new String[]{
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

    static Map<String, String> match(PrjElement el) {
        PrjMatcher m = new PrjMatcher();
        return m.doMatch(el);
    }
    /**
     * This class is used to find the key and value in the WKT
     */
    private PrjNodeMatcher[] projCSmatchers = new PrjNodeMatcher[]{new PrjNodeMatcher() {

    @Override
    public String getName() {
        return "geogcs";
    }

    @Override
    public void run(List<PrjElement> list) {
        parseGeogcs(list, false);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return "unit";
    }

    @Override
    public void run(List<PrjElement> list) {
        parseUnit(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return "projection";
    }

    @Override
    public void run(List<PrjElement> list) {
        parseProjection(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return "parameter";
    }

    @Override
    public void run(List<PrjElement> list) {
        parseParameter(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return "authority";
    }

    @Override
    public void run(List<PrjElement> list) {
        parseAuthority(list);
    }
}};

    private Map<String, String> doMatch(PrjElement el) {
        // PROJCS[ ...
        List<PrjElement> ll = matchNode(el, "projcs", false);
        if (ll == null) {
            // there is no PROJCS node, could still be valid...
            // let's look for GEOGCS directly
            ll = matchNode(el, "geogcs");
            parseGeogcs(ll, true);
        }
        // projection name or GEOGCD name
        parseString(ll.get(0), "name");

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), projCSmatchers);
        }

        // clean up params
        String unit = params.remove("unitval");
        if (unit == null || Double.valueOf(unit) - 1.0 < TOL) {
            params.put(ProjKeyParameters.units, ProjValueParameters.M);
        } else {
            params.put(ProjKeyParameters.to_meter, unit);
        }
        // authority, if present
        String auth = params.remove("refauthority");
        if (auth != null) {
            String code = params.remove("refcode");
            params.put("refname", auth + ':' + code);
        }

        String pm = params.get(ProjKeyParameters.pm);
        boolean pmSupported = CRSHelper.isPrimeMeridianSupported(pm.toLowerCase());
        if (pmSupported) {
            params.put(ProjKeyParameters.pm, pm.toLowerCase());
        }

        // no projection specified usually means longlat
        if (!params.containsKey(ProjKeyParameters.proj)) {
            params.put(ProjKeyParameters.proj, ProjValueParameters.LONGLAT);
        }

        return params;
    }

    private void parseGeogcs(List<PrjElement> ll, boolean rootElement) {
        parseString(ll.get(0), "geogcs");

        PrjNodeMatcher[] matchers;
        if (rootElement) {
            matchers = new PrjNodeMatcher[3];
        } else {
            matchers = new PrjNodeMatcher[2];
        }
        matchers[0] = new PrjNodeMatcher() {

            @Override
            public String getName() {
                return ProjKeyParameters.datum;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseDatum(list);
            }
        };
        matchers[1] = new PrjNodeMatcher() {

            @Override
            public String getName() {
                return "primem";
            }

            @Override
            public void run(List<PrjElement> list) {
                parsePrimeM(list);
            }
        };

        if (rootElement) {
            matchers[2] = new PrjNodeMatcher() {

                @Override
                public String getName() {
                    return "authority";
                }

                @Override
                public void run(List<PrjElement> list) {
                    parseAuthority(list);
                }
            };
        }

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }

    private void parseAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), "refauthority");
        parseString(ll.get(1), "refcode");
    }

    private void parseDatum(List<PrjElement> ll) {
        String datum = getString(ll.get(0));
        datum = datum.replaceAll("[^a-zA-Z0-9]", "");
        boolean found = false;
        if (CRSHelper.isDatumSupported(datum)) {
            params.put(ProjKeyParameters.datum, datum);
            found = true;
        }
        if (!found) {
            int k = Arrays.binarySearch(LONGDATUMNAMES, datum.toLowerCase());
            if (k >= 0) {
                params.put(ProjKeyParameters.datum, SHORTDATUMNAMES[k]);
            } else {
                List<PrjElement> nn = matchNode(ll.get(1), "spheroid");
                //parseString(nn.get(0), "ellps");
                parseNumber(nn.get(1), "a");
                parseNumber(nn.get(2), "rf");

                if (ll.size() > 2) {
                    List<PrjElement> els = matchNode(ll.get(2), ProjKeyParameters.towgs84, false);
                    if (ll == null) {
                        StringBuilder b = new StringBuilder();
                        b.append(getNumber(els.get(0)));
                        for (int i = 1; i < els.size(); i++) {
                            b.append(',').append(getNumber(els.get(i)));
                        }
                        params.put(ProjKeyParameters.towgs84, b.toString());
                    }
                }
            }
        }
    }

    private void parseUnit(List<PrjElement> ll) {
        //parseString(ll.get(0), "unit");
        parseNumber(ll.get(1), "unitval");
    }

    private void parseProjection(List<PrjElement> ll) {
        String proj = getString(ll.get(0));
        proj = proj.replaceAll("[^a-zA-Z0-9]", "");
        int i = Arrays.binarySearch(LONGPROJNAMES, proj.toLowerCase());
        if (i >= 0) {
            params.put(ProjKeyParameters.proj, SHORTPROJNAMES[i]);
        }
    }

    private void parsePrimeM(List<PrjElement> ll) {
        String pm = getString(ll.get(0));
        boolean pmval = CRSHelper.isPrimeMeridianSupported(pm.toLowerCase());
        if (pmval) {
            params.put(ProjKeyParameters.pm, pm.toLowerCase());
        } else {
            parseNumber(ll.get(1), ProjKeyParameters.pm);
        }
    }

    private void parseParameter(List<PrjElement> ll) {
        String param = getString(ll.get(0));
        param = param.replaceAll("[^a-zA-Z0-9]", "");
        int i = Arrays.binarySearch(LONGPARAMNAMES, param.toLowerCase());
        if (i >= 0) {
            parseNumber(ll.get(1), SHORTPARAMNAMES[i]);
        }
    }

    private void matchAnyNode(PrjElement e, PrjNodeMatcher[] nn) {
        matchAnyNode(e, nn, false);
    }

    private void matchAnyNode(PrjElement e, PrjNodeMatcher[] nn, boolean strict) {
        if (e instanceof PrjNodeElement) {
            PrjNodeElement ne = (PrjNodeElement) e;
            for (PrjNodeMatcher m : nn) {
                if (ne.getName().equalsIgnoreCase(m.getName())) {
                    m.run(ne.getChildren());
                    return;
                }
            }
        }

        if (strict) {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', completely unexpected!");
        }
    }

    private List<PrjElement> matchNode(PrjElement e, String name) {
        return matchNode(e, name, true);
    }

    private List<PrjElement> matchNode(PrjElement e, String name, boolean strict) {
        if (e instanceof PrjElement) {
            PrjNodeElement n = (PrjNodeElement) e;
            if (n.getName().equalsIgnoreCase(name)) {
                return n.getChildren();
            }
        }

        if (strict) {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjNodeElement[" + name + "].");
        } else {
            return null;
        }
    }

    /**
     * Return the name of the projection
     *
     * @param e
     * @param name
     */
    private void parseString(PrjElement e, String name) {
        if (e instanceof PrjStringElement) {
            PrjStringElement s = (PrjStringElement) e;
            params.put(name, s.getValue());
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjStringElement with " + name + " in it.");
        }
    }

    private String getString(PrjElement e) {
        if (e instanceof PrjStringElement) {
            PrjStringElement s = (PrjStringElement) e;
            return s.getValue();
        }

        throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected some PrjStringElement.");
    }

    private void parseNumber(PrjElement e, String name) {
        if (e instanceof PrjNumberElement) {
            PrjNumberElement n = (PrjNumberElement) e;
            params.put(name, String.valueOf(n.getValue()));
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjNumberElement with " + name + " in it.");
        }
    }

    private double getNumber(PrjElement e) {
        if (e instanceof PrjNumberElement) {
            PrjNumberElement n = (PrjNumberElement) e;
            return n.getValue();
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjNumberElement.");
        }
    }
}