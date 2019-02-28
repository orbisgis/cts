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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;
import org.cts.units.Quantity;
import org.cts.units.Unit;

/**
 * This class is used to get values from parameter in the prj file.
 *
 * @author Antoine Gourlay, Erwan Bocher, Jules Party, Michaël Michaud
 */
public final class PrjMatcher {

    /**
     * Create a new PrjMatcher.
     */
    private PrjMatcher() {
    }
    /**
     * The map that contained all the parsed informations.
     */
    private Map<String, String> params = new HashMap<String, String>();
    private int indexAxis = 0;

    /**
     * Transform the PrjElement in parameter into a set of parameters.
     *
     * @param el the PrjElement to transform
     */
    static Map<String, String> match(PrjElement el) {
        PrjMatcher m = new PrjMatcher();
        return m.doMatch(el);
    }

    /**
     * Transform the PrjElement in parameter into a set of parameters.
     *
     * @param el the PrjElement to transform
     */
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
                    ll = matchNode(el, PrjKeyParameters.GEOCCS, false);
                    if (ll == null) {
                        ll = matchNode(el, PrjKeyParameters.VERTCS);
                        parseVertcs(ll, true);
                    } else {
                        parseGeoccs(ll);
                    }
                } else {
                    parseGeogcs(ll, true);
                }
            } else {
                parseProjcs(ll, true);
            }
        } else {
            parseCompdcs(ll);
        }
        cleanUnits();
        return params;
    }

    /**
     * Check the units defined in the WKT and correct the values of parameters
     * in consequences. The values of parameters in OGC WKT are expressed in the
     * unit defined in the WKT, but the CRSHelper only deal with meters and
     * degrees.
     */
    private void cleanUnits() {
        String units = params.get(ProjKeyParameters.units);
        String unitval = params.get(ProjKeyParameters.to_meter);
        String unitAuth = params.get(PrjKeyParameters.UNITREFNAME);
        Unit unit = Unit.getUnit(Quantity.LENGTH, units);
        if (unitAuth != null) {
            String[] unitRefname = unitAuth.split(":");
            if (unit == null) {
                unit = (Unit) IdentifiableComponent.getComponent(new Identifier(unitRefname[0], unitRefname[1], ""));
            }
        }
        if (unit != null && !unit.equals(Unit.METER) && unit.getQuantity().equals(Quantity.LENGTH)) {
            String x0 = params.remove(ProjKeyParameters.x_0);
            if (x0 != null) {
                x0 = Double.toString(unit.toBaseUnit(Double.valueOf(x0)));
                params.put(ProjKeyParameters.x_0, x0);
            }
            String y0 = params.remove(ProjKeyParameters.y_0);
            if (y0 != null) {
                y0 = Double.toString(unit.toBaseUnit(Double.valueOf(y0)));
                params.put(ProjKeyParameters.y_0, y0);
            }
        } else if (unitval != null && !Unit.METER.equals(unit)) {
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

        units = params.get(PrjKeyParameters.GEOGUNIT);
        unitval = params.get(PrjKeyParameters.GEOGUNITVAL);
        unitAuth = params.get(PrjKeyParameters.GEOGUNITREFNAME);
        unit = Unit.getUnit(Quantity.ANGLE, units);
        if (unitAuth != null) {
            String[] unitRefname = unitAuth.split(":");
            if (unit == null) {
                unit = (Unit) IdentifiableComponent.getComponent(new Identifier(unitRefname[0], unitRefname[1], ""));
            }
        }
        if (Unit.DEGREE.equals(unit)) {
            return;
        }
        if (unit != null) {
            String lon0 = params.remove(ProjKeyParameters.lon_0);
            if (lon0 != null) {
                lon0 = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lon0))));
                params.put(ProjKeyParameters.lon_0, lon0);
            }
            String lat0 = params.remove(ProjKeyParameters.lat_0);
            if (lat0 != null) {
                lat0 = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lat0))));
                params.put(ProjKeyParameters.lat_0, lat0);
            }
            String lat1 = params.remove(ProjKeyParameters.lat_1);
            if (lat1 != null) {
                lat1 = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lat1))));
                params.put(ProjKeyParameters.lat_1, lat1);
            }
            String lat2 = params.remove(ProjKeyParameters.lat_2);
            if (lat2 != null) {
                lat2 = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lat2))));
                params.put(ProjKeyParameters.lat_2, lat2);
            }
            String lat_ts = params.remove(ProjKeyParameters.lat_ts);
            if (lat_ts != null) {
                lat_ts = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lat_ts))));
                params.put(ProjKeyParameters.lat_ts, lat_ts);
            }
            String lonc = params.remove(ProjKeyParameters.lonc);
            if (lonc != null) {
                lonc = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(lonc))));
                params.put(ProjKeyParameters.lonc, lonc);
            }
            String alpha = params.remove(ProjKeyParameters.alpha);
            if (alpha != null) {
                alpha = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(alpha))));
                params.put(ProjKeyParameters.alpha, alpha);
            }
            String gamma = params.remove(ProjKeyParameters.gamma);
            if (gamma != null) {
                gamma = Double.toString(Unit.DEGREE.fromBaseUnit(unit.toBaseUnit(Double.valueOf(gamma))));
                params.put(ProjKeyParameters.gamma, gamma);
            }
        } else if (unitval != null) {
            String lon0 = params.remove(ProjKeyParameters.lon_0);
            if (lon0 != null) {
                lon0 = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lon0) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lon_0, lon0);
            }
            String lat0 = params.remove(ProjKeyParameters.lat_0);
            if (lat0 != null) {
                lat0 = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lat0) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lat_0, lat0);
            }
            String lat1 = params.remove(ProjKeyParameters.lat_1);
            if (lat1 != null) {
                lat1 = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lat1) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lat_1, lat1);
            }
            String lat2 = params.remove(ProjKeyParameters.lat_2);
            if (lat2 != null) {
                lat2 = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lat2) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lat_2, lat2);
            }
            String lat_ts = params.remove(ProjKeyParameters.lat_ts);
            if (lat_ts != null) {
                lat_ts = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lat_ts) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lat_ts, lat_ts);
            }
            String lonc = params.remove(ProjKeyParameters.lonc);
            if (lonc != null) {
                lonc = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(lonc) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.lonc, lonc);
            }
            String alpha = params.remove(ProjKeyParameters.alpha);
            if (alpha != null) {
                alpha = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(alpha) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.alpha, alpha);
            }
            String gamma = params.remove(ProjKeyParameters.gamma);
            if (gamma != null) {
                gamma = Double.toString(Unit.DEGREE.fromBaseUnit(Double.valueOf(gamma) * Double.valueOf(unitval)));
                params.put(ProjKeyParameters.gamma, gamma);
            }
        }
    }

    /**
     * Read the informations contains in the PROJCS node and put it into the set
     * of parameters.
     *
     * @param ll the children of the PROJCS node
     * @param rootElement true if PROJCS is the root element given in match
     */
    private void parseProjcs(List<PrjElement> ll, boolean rootElement) {
        if (!rootElement) {
            parseString(ll.get(0), PrjKeyParameters.PROJCS);
        } else {
            parseString(ll.get(0), PrjKeyParameters.NAME);
        }
        indexAxis = 0;

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[6];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.GEOGCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseGeogcs(list, false);
                params.remove(PrjKeyParameters.GEOGCS);
                params.remove(PrjKeyParameters.GEOGREFNAME);
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.UNIT;
            }

            @Override
            public void run(List<PrjElement> list) {
                List<String> info = getUnit(list);
                params.put(PrjKeyParameters.GEOGUNIT, params.remove(ProjKeyParameters.units));
                params.put(PrjKeyParameters.GEOGUNITVAL, params.remove(ProjKeyParameters.to_meter));
                params.put(ProjKeyParameters.units, info.get(0));
                params.put(ProjKeyParameters.to_meter, info.get(1));
                if (info.size() > 2) {
                    params.put(PrjKeyParameters.GEOGUNITREFNAME, params.remove(PrjKeyParameters.UNITREFNAME));
                    params.put(PrjKeyParameters.UNITREFNAME, info.get(2));
                }
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
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.PROJREFNAME, refname);
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
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.REFNAME, refname);
                }
            };
        }
        matchers[5] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AXIS;
            }

            @Override
            public void run(List<PrjElement> list) {
                List<String> info = getAxis(list);
                switch (indexAxis) {
                    case 0:
                        params.put(PrjKeyParameters.AXIS1, info.get(0));
                        params.put(PrjKeyParameters.AXIS1TYPE, info.get(1));
                        indexAxis++;
                        break;
                    case 1:
                        params.put(PrjKeyParameters.AXIS2, info.get(0));
                        params.put(PrjKeyParameters.AXIS2TYPE, info.get(1));
                        indexAxis++;
                        break;
                    default:
                        throw new PrjParserException("Failed to parse PRJ. Found '" + list + "', completely unexpected!");
                }
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }
    private boolean isHorizontalCRS = false;
    private boolean isVerticalCRS = false;

    /**
     * Read the informations contains in the COMPD_CS node and put it into the
     * set of parameters.
     *
     * @param ll the children of the COMPD_CS node
     */
    private void parseCompdcs(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.NAME);
        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[4];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.GEOGCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                if (!isHorizontalCRS) {
                    parseGeogcs(list, false);
                    isHorizontalCRS = true;
                } else {
                    throw new PrjParserException("Failed to parse PRJ, because of multiple horizontal CRS definition.");
                }
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.PROJCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                if (!isHorizontalCRS) {
                    parseProjcs(list, false);
                    isHorizontalCRS = true;
                } else {
                    throw new PrjParserException("Failed to parse PRJ, because of multiple horizontal CRS definition.");
                }
            }
        };
        matchers[2] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.VERTCS;
            }

            @Override
            public void run(List<PrjElement> list) {
                if (!isVerticalCRS) {
                    parseVertcs(list, false);
                    isVerticalCRS = true;
                } else {
                    throw new PrjParserException("Failed to parse PRJ, because of multiple vertical CRS definition.");
                }
            }
        };
        matchers[3] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AUTHORITY;
            }

            @Override
            public void run(List<PrjElement> list) {
                String refname = getAuthority(list);
                params.put(PrjKeyParameters.REFNAME, refname);
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
        if (!isHorizontalCRS || !isVerticalCRS) {
            throw new PrjParserException("Failed to parse PRJ. Missing definition for an horizontal CRS or for a VerticalCRS.");
        }
    }

    /**
     * Read the informations contains in the GEOCCS node and put it into the set
     * of parameters.
     *
     * @param ll the children of the GEOCCS node
     */
    private void parseGeoccs(List<PrjElement> ll) {
        parseString(ll.get(0), PrjKeyParameters.NAME);
        params.put(ProjKeyParameters.proj, ProjValueParameters.GEOCENT);
        indexAxis = 0;

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[5];
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
                List<String> info = getUnit(list);
                params.put(ProjKeyParameters.units, info.get(0));
                params.put(ProjKeyParameters.to_meter, info.get(1));
                if (info.size() > 2) {
                    params.put(PrjKeyParameters.UNITREFNAME, info.get(2));
                }
            }
        };
        matchers[3] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AUTHORITY;
            }

            @Override
            public void run(List<PrjElement> list) {
                String refname = getAuthority(list);
                params.put(PrjKeyParameters.REFNAME, refname);
            }
        };
        matchers[4] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AXIS;
            }

            @Override
            public void run(List<PrjElement> list) {
                List<String> info = getAxis(list);
                switch (indexAxis) {
                    case 0:
                        params.put(PrjKeyParameters.AXIS1, info.get(0));
                        params.put(PrjKeyParameters.AXIS1TYPE, info.get(1));
                        indexAxis++;
                        break;
                    case 1:
                        params.put(PrjKeyParameters.AXIS2, info.get(0));
                        params.put(PrjKeyParameters.AXIS2TYPE, info.get(1));
                        indexAxis++;
                        break;
                    case 2:
                        params.put(PrjKeyParameters.AXIS3, info.get(0));
                        params.put(PrjKeyParameters.AXIS3TYPE, info.get(1));
                        indexAxis++;
                        break;
                    default:
                        throw new PrjParserException("Failed to parse PRJ. Found '" + list + "', completely unexpected!");
                }
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }

    /**
     * Read the informations contains in the GEOGCS node and put it into the set
     * of parameters.
     *
     * @param ll the children of the GEOGCS node
     * @param rootElement true if GEOGCS is the root element given in match
     */
    private void parseGeogcs(List<PrjElement> ll, boolean rootElement) {
        if (!rootElement) {
            parseString(ll.get(0), PrjKeyParameters.GEOGCS);
        } else {
            parseString(ll.get(0), PrjKeyParameters.NAME);
        }
        if (!params.containsKey(ProjKeyParameters.proj)) {
            params.put(ProjKeyParameters.proj, ProjValueParameters.LONGLAT);
        }
        indexAxis = 0;

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[5];
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
                if (!params.containsKey(ProjKeyParameters.units)) {
                    List<String> info = getUnit(list);
                    params.put(ProjKeyParameters.units, info.get(0));
                    params.put(ProjKeyParameters.to_meter, info.get(1));
                    if (info.size() > 2) {
                        params.put(PrjKeyParameters.UNITREFNAME, info.get(2));
                    }
                }
            }
        };
        if (!rootElement) {
            parseString(ll.get(0), PrjKeyParameters.GEOGCS);
            matchers[3] = new PrjNodeMatcher() {
                @Override
                public String getName() {
                    return PrjKeyParameters.AUTHORITY;
                }

                @Override
                public void run(List<PrjElement> list) {
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.GEOGREFNAME, refname);
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
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.REFNAME, refname);
                }
            };
        }
        matchers[4] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AXIS;
            }

            @Override
            public void run(List<PrjElement> list) {
                if (!params.containsKey(PrjKeyParameters.AXIS1)) {
                    List<String> info = getAxis(list);
                    switch (indexAxis) {
                        case 0:
                            params.put(PrjKeyParameters.AXIS1, info.get(0));
                            params.put(PrjKeyParameters.AXIS1TYPE, info.get(1));
                            indexAxis++;
                            break;
                        case 1:
                            params.put(PrjKeyParameters.AXIS2, info.get(0));
                            params.put(PrjKeyParameters.AXIS2TYPE, info.get(1));
                            indexAxis++;
                            break;
                        case 2:
                            params.put(PrjKeyParameters.AXIS3, info.get(0));
                            params.put(PrjKeyParameters.AXIS3TYPE, info.get(1));
                            indexAxis++;
                            break;
                        default:
                            throw new PrjParserException("Failed to parse PRJ. Found '" + list + "', completely unexpected!");
                    }
                }
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }

        indexAxis = 0;
    }

    /**
     * Read the informations contains in the VERT_CS node and put it into the
     * set of parameters.
     *
     * @param ll the children of the VERT_CS node
     * @param rootElement true if VERT_CS is the root element given in match
     */
    private void parseVertcs(List<PrjElement> ll, boolean rootElement) {
        if (!rootElement) {
            parseString(ll.get(0), PrjKeyParameters.VERTCS);
        } else {
            parseString(ll.get(0), PrjKeyParameters.NAME);
        }
        indexAxis = 0;

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[4];
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
                List<String> info = getUnit(list);
                params.put(PrjKeyParameters.VERTUNIT, info.get(0));
                params.put(PrjKeyParameters.VERTUNITVAL, info.get(1));
                if (info.size() > 2) {
                    params.put(PrjKeyParameters.VERTUNITREFNAME, info.get(2));
                }
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
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.VERTREFNAME, refname);
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
                    String refname = getAuthority(list);
                    params.put(PrjKeyParameters.REFNAME, refname);
                }
            };
        }
        matchers[3] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AXIS;
            }

            @Override
            public void run(List<PrjElement> list) {
                List<String> info = getAxis(list);
                switch (indexAxis) {
                    case 0:
                        params.put(PrjKeyParameters.VERTAXIS, info.get(0));
                        params.put(PrjKeyParameters.VERTAXISTYPE, info.get(1));
                        indexAxis++;
                        break;
                    default:
                        throw new PrjParserException("Failed to parse PRJ. Found '" + list + "', completely unexpected!");
                }
            }
        };

        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }

    /**
     * Return the authority code contains in the AUTHORITY node in parameter.
     *
     * @param ll the children of the AUTHORITY node
     */
    private String getAuthority(List<PrjElement> ll) {
        String auth = getString(ll.get(0));
        PrjElement authorityCode = ll.get(1);
        String code ;
        if(authorityCode instanceof PrjNumberElement){
            code =  String.valueOf(Math.round(getNumber(authorityCode)));            
        }
        else{
            code = getString(authorityCode);
        }
        return auth + ':' + code;
    }

    /**
     * Read the informations contains in the DATUM node and put it into the set
     * of parameters.
     *
     * @param ll the children of the DATUM node
     */
    private void parseDatum(List<PrjElement> ll) {
        String datum = getString(ll.get(0));
        String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase()
                .replaceAll("^d_","")
                .replaceAll("[^a-zA-Z0-9]", "")
                .replaceAll("datum",""));
        datum = datm != null ? datm : datum;
        params.put(ProjKeyParameters.datum, datum);

        PrjNodeMatcher[] matchers;
        matchers = new PrjNodeMatcher[3];
        matchers[0] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.SPHEROID;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseSpheroid(list);
            }
        };
        matchers[1] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return ProjKeyParameters.towgs84;
            }

            @Override
            public void run(List<PrjElement> list) {
                parseToWGS84(list);
            }
        };
        matchers[2] = new PrjNodeMatcher() {
            @Override
            public String getName() {
                return PrjKeyParameters.AUTHORITY;
            }

            @Override
            public void run(List<PrjElement> list) {
                String refname = getAuthority(list);
                params.put(PrjKeyParameters.DATUMREFNAME, refname);
            }
        };
        for (int i = 1; i < ll.size(); i++) {
            matchAnyNode(ll.get(i), matchers);
        }
    }

    /**
     * Read the informations contains in the SPHEROID node and put it into the
     * set of parameters.
     *
     * @param ll the children of the SPHEROID node
     */
    private void parseSpheroid(List<PrjElement> ll) {
        String ellps = getString(ll.get(0));
        String elps = PrjValueParameters.ELLIPSOIDNAMES.get(ellps.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""));
        ellps = elps != null ? elps : ellps;
        params.put(ProjKeyParameters.ellps, ellps);
        parseNumber(ll.get(1), ProjKeyParameters.a);
        parseNumber(ll.get(2), ProjKeyParameters.rf);
        if (ll.size() > 3) {
            String auth = getAuthority(matchNode(ll.get(3), PrjKeyParameters.AUTHORITY));
            params.put(PrjKeyParameters.SPHEROIDREFNAME, auth);
        }
    }

    /**
     * Read the informations contains in the TOWGS84 node and put it into the
     * set of parameters.
     *
     * @param ll the children of the TOWGS84 node
     */
    private void parseToWGS84(List<PrjElement> ll) {
        StringBuilder b = new StringBuilder();
        b.append(getNumber(ll.get(0)));
        for (int i = 1; i < ll.size(); i++) {
            b.append(',').append(getNumber(ll.get(i)));
        }
        params.put(ProjKeyParameters.towgs84, b.toString());
    }

    /**
     * Read the informations contains in the VERT_DATUM node and put it into the
     * set of parameters.
     *
     * @param ll the children of the VERT_DATUM node
     */
    private void parseVertDatum(List<PrjElement> ll) {
        String datum = getString(ll.get(0));
        String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""));
        datum = datm != null ? datm : datum;
        params.put(PrjKeyParameters.VERTDATUM, datum);
        parseNumber(ll.get(1), PrjKeyParameters.VERTDATUMTYPE);
        if (ll.size() > 2) {
            String auth = getAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
            params.put(PrjKeyParameters.VERTDATUMREFNAME, auth);
        }
    }

    /**
     * Return the informations contains in an UNIT node in a list of String.
     *
     * @param ll the children of the UNIT node
     */
    private List<String> getUnit(List<PrjElement> ll) {
        List<String> result = new ArrayList<String>();
        String unit = getString(ll.get(0));
        String unt = PrjValueParameters.UNITNAMES.get(unit.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        if (unt != null) {
            result.add(unt);
        } else {
            result.add(unit);
        }
        result.add(String.valueOf(getNumber(ll.get(1))));
        if (ll.size() > 2) {
            result.add(getAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY)));
        }
        return result;
    }

    /**
     * Read the informations contains in the PROJECTION node and put it into the
     * set of parameters.
     *
     * @param ll the children of the PROJECTION node
     */
    private void parseProjection(List<PrjElement> ll) {
        String proj = getString(ll.get(0));
        String prj = PrjValueParameters.PROJNAMES.get(proj.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        proj = prj != null ? prj : proj;
        params.put(ProjKeyParameters.proj, proj);
    }

    /**
     * Read the informations contains in the PRIMEM node and put it into the set
     * of parameters.
     *
     * @param ll the children of the PRIMEM node
     */
    private void parsePrimeM(List<PrjElement> ll) {
        String pm = getString(ll.get(0));
        String prm = PrjValueParameters.PRIMEMERIDIANNAMES.get(pm.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        if (prm != null) {
            params.put(ProjKeyParameters.pm, prm);
        } else {
            params.put(ProjKeyParameters.pm, pm);
            parseNumber(ll.get(1), PrjKeyParameters.PMVALUE);
            if (ll.size() > 2) {
                String auth = getAuthority(matchNode(ll.get(2), PrjKeyParameters.AUTHORITY));
                params.put(PrjKeyParameters.PRIMEMREFNAME, auth);
            }
        }
    }

    /**
     * Read the informations contains in a PARAMETER node and put it into the
     * set of parameters.
     *
     * @param ll the children of the PARAMETER node
     */
    private void parseParameter(List<PrjElement> ll) {
        String param = getString(ll.get(0));
        String parm = PrjValueParameters.PARAMNAMES.get(param.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""));
        if (parm != null) {
            parseNumber(ll.get(1), parm);
        }
    }

    /**
     * Return the informations contains in an AXIS node in a list of String.
     *
     * @param ll the children of the UNIT node
     */
    private List<String> getAxis(List<PrjElement> ll) {
        List<String> result = new ArrayList<String>();
        String axisName = getString(ll.get(0));
        String axis = PrjValueParameters.AXISNAMES.get(axisName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        if (axis != null) {
            result.add(axis);
        } else {
            result.add(axisName);
        }
        result.add(getString(ll.get(1)));
        return result;
    }

    /**
     * Read the informations contains in the PrjElement in parameter using one
     * of the PrjNodeMatcher in parameter and put it into the set of parameters.
     *
     * @param e the PrjElement to parse
     * @param nn the PrjNodeMatcher to use to parse the given PrjElement
     */
    private void matchAnyNode(PrjElement e, PrjNodeMatcher[] nn) {
        matchAnyNode(e, nn, false);
    }

    /**
     * Read the informations contains in the PrjElement in parameter using one
     * of the PrjNodeMatcher in parameter and put it into the set of parameters.
     *
     * @param e the PrjElement to parse
     * @param nn the PrjNodeMatcher to use to parse the given PrjElement
     * @param strict if true, the method throw a PrjParserException if it does
     * not manage to parse the PrjElement in parameter
     */
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

    /**
     * Return the children of the PrjElement in parameter if it is an instance
     * of PrjNodeElement and if its name is the same as the name given in
     * parameter,else it throws a PrjParserException.
     *
     * @param e the PrjElement to match
     * @param name the name of the desired PrjElement
     */
    private List<PrjElement> matchNode(PrjElement e, String name) {
        return matchNode(e, name, true);
    }

    /**
     * Return the children of the PrjElement in parameter if it is an instance
     * of PrjNodeElement and if its name is the same as the name given in
     * parameter,else it throws a PrjParserException unless strict is false, it
     * then returns null.
     *
     * @param e the PrjElement to match
     * @param name the name of the desired PrjElement
     * @param strict true if the method must throw an exception instead of
     * return null
     */
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
     * Read the informations contains in the PrjElement in parameter if it is a
     * PrjStringParameter and put it into the set of parameters.
     *
     * @param e the PrjElement to parse
     * @param name the key to use to put the string in the Map of parameters
     */
    private void parseString(PrjElement e, String name) {
        if (e instanceof PrjStringElement) {
            PrjStringElement s = (PrjStringElement) e;
            params.put(name, s.getValue().trim());
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjStringElement with " + name + " in it.");
        }
    }

    /**
     * Return the informations contains in the PrjElement in parameter if it is
     * a PrjStringParameter.
     *
     * @param e the PrjElement to parse
     */
    private String getString(PrjElement e) {
        if (e instanceof PrjStringElement) {
            PrjStringElement s = (PrjStringElement) e;
            return s.getValue().trim();
        }
        throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected some PrjStringElement.");
    }

    /**
     * Read the informations contains in the PrjElement in parameter if it is a
     * PrjNumberParameter and put it into the set of parameters.
     *
     * @param e the PrjElement to parse
     * @param name the key to use to put the string in the Map of parameters
     */
    private void parseNumber(PrjElement e, String name) {
        if (e instanceof PrjNumberElement) {
            PrjNumberElement n = (PrjNumberElement) e;
            params.put(name, String.valueOf(n.getValue()));
        } else {
            throw new PrjParserException("Failed to parse PRJ. Found '" + e + "', expected PrjNumberElement with " + name + " in it.");
        }
    }

    /**
     * Return the informations contains in the PrjElement in parameter if it is
     * a PrjNumberParameter.
     *
     * @param e the PrjElement to parse
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