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
import java.util.List;
import java.util.Map;
import org.cts.crs.CRSHelper;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;

/**
 * This class is used to get values from parameter in the prj file.
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public final class PrjMatcher {

    private static final double TOL = 1.0E-100;

    private PrjMatcher() {
    }
    private Map<String, String> params = new HashMap<String, String>();

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
        return PrjKeyParameters.GEOGCS;
    }

    @Override
    public void run(List<PrjElement> list) {
        parseGeogcs(list, false);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return PrjKeyParameters.UNIT;
    }

    @Override
    public void run(List<PrjElement> list) {
        parseUnit(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return PrjKeyParameters.PROJECTION;
    }

    @Override
    public void run(List<PrjElement> list) {
        parseProjection(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return PrjKeyParameters.PARAMETER;
    }

    @Override
    public void run(List<PrjElement> list) {
        parseParameter(list);
    }
}, new PrjNodeMatcher() {

    @Override
    public String getName() {
        return PrjKeyParameters.AUTHORITY;
    }

    @Override
    public void run(List<PrjElement> list) {
        parseAuthority(list);
    }
}};

    private Map<String, String> doMatch(PrjElement el) {
        // PROJCS[ ...
        List<PrjElement> ll = matchNode(el, PrjKeyParameters.PROJCS, false);
        if (ll == null) {
            // there is no PROJCS node, could still be valid...
            // let's look for GEOGCS directly
            ll = matchNode(el, PrjKeyParameters.GEOGCS);
            parseGeogcs(ll, true);
        }
        // projection name or GEOGCD name
        parseString(ll.get(0), PrjKeyParameters.NAME);

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), projCSmatchers);
        }

        // clean up params
        String unit = params.remove(PrjKeyParameters.UNITVAL);
        if (unit == null || Double.valueOf(unit) - 1.0 < TOL) {
            params.put(ProjKeyParameters.units, ProjValueParameters.M);
        } else {
            params.put(ProjKeyParameters.to_meter, unit);
        }
        // authority, if present
        String auth = params.remove(PrjKeyParameters.REFAUTHORITY);
        if (auth != null) {
            String code = params.remove(PrjKeyParameters.REFCODE);
            params.put(PrjKeyParameters.REFNAME, auth + ':' + code);
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
        parseString(ll.get(0), PrjKeyParameters.GEOGCS);

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
                return PrjKeyParameters.PRIMEM;
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
                    return PrjKeyParameters.AUTHORITY;
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
        parseString(ll.get(0), PrjKeyParameters.REFAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.REFCODE);
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
            String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase());
            if (datm!=null) {
                params.put(ProjKeyParameters.datum, datm);
            } else {
                List<PrjElement> nn = matchNode(ll.get(1), PrjKeyParameters.SPHEROID);
                //parseString(nn.get(0), "ellps");
                parseNumber(nn.get(1), ProjKeyParameters.a);
                parseNumber(nn.get(2), ProjKeyParameters.rf);

                if (ll.size() > 2) {
                    List<PrjElement> els = matchNode(ll.get(2), ProjKeyParameters.towgs84, false);
                    if (els != null) {
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
        parseNumber(ll.get(1), PrjKeyParameters.UNITVAL);
    }

    private void parseProjection(List<PrjElement> ll) {
        String proj = getString(ll.get(0));
        proj = proj.replaceAll("[^a-zA-Z0-9]", "");
        String prj = PrjValueParameters.PROJNAMES.get(proj.toLowerCase());
        if (prj!=null) {
            params.put(ProjKeyParameters.proj, prj);
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
        String parm = PrjValueParameters.PARAMNAMES.get(param.toLowerCase());
        if (parm!=null) {
            parseNumber(ll.get(1), parm);
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
        if (e instanceof PrjNodeElement) {
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
            params.put(name, s.getValue().trim());
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjStringElement with " + name + " in it.");
        }
    }

    private String getString(PrjElement e) {
        if (e instanceof PrjStringElement) {
            PrjStringElement s = (PrjStringElement) e;
            return s.getValue().trim();
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

    /*
     * Return the value of the element as double representation
     */
    private double getNumber(PrjElement e) {
        if (e instanceof PrjNumberElement) {
            PrjNumberElement n = (PrjNumberElement) e;
            return n.getValue();
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjNumberElement.");
        }
    }
}