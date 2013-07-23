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
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;

/**
 * This class is used to get values from parameter in the prj file.
 *
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
    private void parseProjcs(List<PrjElement> ll, boolean rootElement) {
        parseString(ll.get(0), PrjKeyParameters.PROJCS);

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[5];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.GEOGCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseGeogcs(list, false);
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.UNIT;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseProjUnit(list);
            }
        };
        matchers[2] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.PROJECTION;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseProjection(list);
            }
        };
        matchers[3] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.PARAMETER;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseParameter(list);
            }
        };
        if (!rootElement) {
            matchers[4] = new PrjNodeMatcher() {
                @Override
                public String getName() {
                    return PrjKeyParameters.AUTHORITY;
                }

                @Override
                public void run(List<PrjElement> list) {
                    parseProjAuthority(list);
                }
            };
        } else {
            matchers[4] = new PrjNodeMatcher() {
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
    
    private void parseCompdcs(List<PrjElement> ll, boolean rootElement) {
        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[4];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.GEOGCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseGeogcs(list, false);
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.PROJCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseProjcs(list, false);
            }
        };
        matchers[2] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.VERTCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseVertcs(list, false);
            }
        };
        matchers[3] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AUTHORITY;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseAuthority(list);
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }

    private Map<String, String> doMatch(PrjElement el) {
        // COMPD_CS[ ...
        List<PrjElement> ll = matchNode(el, PrjKeyParameters.COMPDCS, false);
        if (ll == null) {
            // PROJCS[ ...
            ll = matchNode(el, PrjKeyParameters.PROJCS, false);
            if (ll == null) {
                // there is no PROJCS node, could still be valid...
                // let's look for GEOGCS directly
                ll = matchNode(el, PrjKeyParameters.GEOGCS, false);
                if (ll == null) {
                    ll = matchNode(el, PrjKeyParameters.VERTCS);
                    parseVertcs(ll, true);
                } else {
                    parseGeogcs(ll, true);
                }
            } else {
                parseProjcs(ll, true);
            }
        } else {
            parseCompdcs(ll, true);
        }
        // projection name or GEOGCS name or COMPD_CS name
        parseString(ll.get(0), PrjKeyParameters.NAME);

        // clean up params
        String unit = params.remove(PrjKeyParameters.PROJUNIT);
        String unitval = params.remove(PrjKeyParameters.PROJUNITVAL);
        if (unit != null) {
            if (PrjValueParameters.UNITNAMES.containsKey(unit)) {
                params.put(ProjKeyParameters.units, PrjValueParameters.UNITNAMES.get(unit));
            } else if (Math.abs(Double.valueOf(unitval) - 1.0) < TOL) {
                params.put(ProjKeyParameters.units, ProjValueParameters.M);
            } else {
                params.put(ProjKeyParameters.to_meter, unitval);
                String x0 = params.remove(ProjKeyParameters.x_0);
                if (x0 != null) {
                    x0 = Double.toString(Double.valueOf(x0) * Double.valueOf(unitval));
                    params.put(ProjKeyParameters.x_0, x0);
                }
                String y0 = params.remove(ProjKeyParameters.y_0);
                if (y0 != null) {
                    y0 = Double.toString(Double.valueOf(y0) * Double.valueOf(unitval));
                    params.put(ProjKeyParameters.y_0, y0);
                }
            }
            params.remove(PrjKeyParameters.PROJUNITAUTHORITY);
            params.remove(PrjKeyParameters.PROJUNITCODE);
            params.remove(PrjKeyParameters.GEOGUNIT);
            params.remove(PrjKeyParameters.GEOGUNITVAL);
            params.remove(PrjKeyParameters.GEOGUNITAUTHORITY);
            params.remove(PrjKeyParameters.GEOGUNITCODE);
        } else {
            unit = params.remove(PrjKeyParameters.GEOGUNIT);
            unitval = params.remove(PrjKeyParameters.GEOGUNITVAL);
            if (unit != null) {
                if (PrjValueParameters.UNITNAMES.containsKey(unit)) {
                    params.put(ProjKeyParameters.units, PrjValueParameters.UNITNAMES.get(unit));
                } else if (Math.abs(Double.valueOf(unitval) - 1.0) < TOL) {
                    params.put(ProjKeyParameters.units, ProjValueParameters.RAD);
                }
            }
            params.remove(PrjKeyParameters.GEOGUNITAUTHORITY);
            params.remove(PrjKeyParameters.GEOGUNITCODE);
        }
        unit = params.remove(PrjKeyParameters.VERTUNIT);
        unitval = params.remove(PrjKeyParameters.VERTUNITVAL);
        if (unit != null) {
            if (PrjValueParameters.UNITNAMES.containsKey(unit)) {
                params.put(PrjKeyParameters.VERTUNIT, PrjValueParameters.UNITNAMES.get(unit));
            } else if (Math.abs(Double.valueOf(unitval) - 1.0) < TOL) {
                params.put(PrjKeyParameters.VERTUNIT, ProjValueParameters.M);
            }
            params.remove(PrjKeyParameters.VERTUNITAUTHORITY);
            params.remove(PrjKeyParameters.VERTUNITCODE);
        }
        // authority, if present
        String auth = params.remove(PrjKeyParameters.REFAUTHORITY);
        if (auth != null) {
            String code = params.remove(PrjKeyParameters.REFCODE);
            params.put(PrjKeyParameters.REFNAME, auth + ':' + code);
        }
        auth = params.remove(PrjKeyParameters.GEOGREFAUTHORITY);
        if (auth != null) {
            String code = params.remove(PrjKeyParameters.GEOGREFCODE);
            params.put(PrjKeyParameters.GEOGREFNAME, auth + ':' + code);
        }
        auth = params.remove(PrjKeyParameters.PROJREFAUTHORITY);
        if (auth != null) {
            String code = params.remove(PrjKeyParameters.PROJREFCODE);
            params.put(PrjKeyParameters.PROJREFNAME, auth + ':' + code);
        }
        auth = params.remove(PrjKeyParameters.VERTREFAUTHORITY);
        if (auth != null) {
            String code = params.remove(PrjKeyParameters.VERTREFCODE);
            params.put(PrjKeyParameters.VERTREFNAME, auth + ':' + code);
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
        matchers = new PrjNodeMatcher[4];
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
        matchers[2] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.UNIT;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseGeogUnit(list);
            }
        };
        if (!rootElement) {
            matchers[3] = new PrjNodeMatcher() {
                @Override
                public String getName() {
                    return PrjKeyParameters.AUTHORITY;
                }

                @Override
                public void run(List<PrjElement> list) {
                    parseGeogAuthority(list);
                }
            };
        } else {
            matchers[3] = new PrjNodeMatcher() {
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

    private void parseVertcs(List<PrjElement> ll, boolean rootElement) {
        parseString(ll.get(0), PrjKeyParameters.VERTCS);

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[3];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.VERTDATUM;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseVertDatum(list);
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.UNIT;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseVertUnit(list);
            }
        };
        if (!rootElement) {
            matchers[2] = new PrjNodeMatcher() {
                @Override
                public String getName() {
                    return PrjKeyParameters.AUTHORITY;
                }

                @Override
                public void run(List<PrjElement> list) {
                    parseVertAuthority(list);
                }
            };
        } else {
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

    private void parseGeogAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.GEOGREFAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.GEOGREFCODE);
    }

    private void parseProjAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.PROJREFAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.PROJREFCODE);
    }

    private void parseVertAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.VERTREFAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.VERTREFCODE);
    }

    private void parseSpheroidAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.SPHEROIDAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.SPHEROIDCODE);
    }

    private void parsePrimeMAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.PRIMEMAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.PRIMEMCODE);
    }

    private void parseDatumAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.DATUMAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.DATUMCODE);
    }

    private void parseVertDatumAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.VERTDATUMAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.VERTDATUMCODE);
    }

    private void parseProjUnitAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.PROJUNITAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.PROJUNITCODE);
    }

    private void parseGeogUnitAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.GEOGUNITAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.GEOGUNITCODE);
    }

    private void parseVertUnitAuthority(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.VERTUNITAUTHORITY);
        parseString(ll.get(1), PrjKeyParameters.VERTUNITCODE);
    }

    private void parseDatum(List<PrjElement> ll) {
        String datum = getString(ll.get(0));
        datum = datum.replaceAll("[^a-zA-Z0-9]", "");
        String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase());
        if (datm != null) {
            params.put(ProjKeyParameters.datum, datm);
        } else {
            List<PrjElement> nn = matchNode(ll.get(1), PrjKeyParameters.SPHEROID);
            String ellps = getString(nn.get(0));
            ellps = ellps.replaceAll("[^a-zA-Z0-9]", "");
            String elps = PrjValueParameters.ELLIPSOIDNAMES.get(ellps.toLowerCase());
            if (elps != null) {
                params.put(ProjKeyParameters.ellps, elps);
            } else {
                parseNumber(nn.get(1), ProjKeyParameters.a);
                parseNumber(nn.get(2), ProjKeyParameters.rf);
                parseSpheroidAuthority(matchNode(nn.get(3), PrjKeyParameters.AUTHORITY));
            }

            if (ll.size() > 3) {
                List<PrjElement> els = matchNode(ll.get(2), ProjKeyParameters.towgs84, false);
                if (els != null) {
                    StringBuilder b = new StringBuilder();
                    b.append(getNumber(els.get(0)));
                    for (int i = 1; i < els.size(); i++) {
                        b.append(',').append(getNumber(els.get(i)));
                    }
                    params.put(ProjKeyParameters.towgs84, b.toString());
                }
                parseDatumAuthority(matchNode(ll.get(3), PrjKeyParameters.AUTHORITY));
            } else {
                parseDatumAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
            }
        }
    }

    private void parseVertDatum(List<PrjElement> ll) {
        String datum = getString(ll.get(0));
        datum = datum.replaceAll("[^a-zA-Z0-9]", "");
        String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase());
        if (datm != null) {
            params.put(PrjKeyParameters.VERTDATUM, datm);
        } else {
            parseNumber(ll.get(1), PrjKeyParameters.VERTDATUMTYPE);
            parseVertDatumAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
        }
    }

    /**
     * Parse unit value to a {@code PrjNumberElement}
     *
     * @param {@code List<PrjElement>}
     */
    private void parseProjUnit(List<PrjElement> ll) {
        String unit = getString(ll.get(0));
        unit = unit.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        params.put(PrjKeyParameters.PROJUNIT, unit);
        parseNumber(ll.get(1), PrjKeyParameters.PROJUNITVAL);
        if (ll.size() > 2) {
            parseProjUnitAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
        }
    }

    /**
     * Parse unit value to a {@code PrjNumberElement}
     *
     * @param {@code List<PrjElement>}
     */
    private void parseGeogUnit(List<PrjElement> ll) {
        String unit = getString(ll.get(0));
        unit = unit.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        params.put(PrjKeyParameters.GEOGUNIT, unit);
        parseNumber(ll.get(1), PrjKeyParameters.GEOGUNITVAL);
        if (ll.size() > 2) {
            parseGeogUnitAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
        }
    }

    /**
     * Parse unit value to a {@code PrjNumberElement}
     *
     * @param {@code List<PrjElement>}
     */
    private void parseVertUnit(List<PrjElement> ll) {
        String unit = getString(ll.get(0));
        unit = unit.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        params.put(PrjKeyParameters.VERTUNIT, unit);
        parseNumber(ll.get(1), PrjKeyParameters.VERTUNITVAL);
        if (ll.size() > 2) {
            parseVertUnitAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
        }
    }

    /**
     * Parse the projection name of the CRS to a String representation
     *
     * @param {@code List<PrjElement>}
     */
    private void parseProjection(List<PrjElement> ll) {
        String proj = getString(ll.get(0));
        proj = proj.replaceAll("[^a-zA-Z0-9]", "");
        String prj = PrjValueParameters.PROJNAMES.get(proj.toLowerCase());
        if (prj != null) {
            params.put(ProjKeyParameters.proj, prj);
        }
    }

    /**
     * Parse the prime meridian of the CRS to a String representation
     *
     * @param {@code List<PrjElement>}
     */
    private void parsePrimeM(List<PrjElement> ll) {
        String pm = getString(ll.get(0));
        pm = pm.replaceAll("[^a-zA-Z0-9]", "");
        String prm = PrjValueParameters.PRIMEMERIDIANNAMES.get(pm.toLowerCase());
        if (prm != null) {
            params.put(ProjKeyParameters.pm, prm);
        } else {
            parseNumber(ll.get(1), ProjKeyParameters.pm);
            parsePrimeMAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
        }
    }

    private void parseParameter(List<PrjElement> ll) {
        String param = getString(ll.get(0));
        param = param.replaceAll("[^a-zA-Z0-9]", "");
        String parm = PrjValueParameters.PARAMNAMES.get(param.toLowerCase());
        if (parm != null) {
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