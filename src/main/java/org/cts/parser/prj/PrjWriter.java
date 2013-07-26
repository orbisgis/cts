/* 
 Copyright 2012 Antoine Gourlay

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.cts.parser.prj;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.cts.Identifiable;
import org.cts.IllegalCoordinateException;

import org.cts.op.CoordinateOperation;
import org.cts.Parameter;
import org.cts.crs.CompoundCRS;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeocentricCRS;
import org.cts.crs.ProjectedCRS;
import org.cts.crs.VerticalCRS;
import org.cts.cs.Axis;
import org.cts.datum.Datum;
import org.cts.datum.Ellipsoid;
import org.cts.datum.PrimeMeridian;
import org.cts.datum.VerticalDatum;
import org.cts.op.Identity;
import org.cts.op.UnitConversion;
import org.cts.op.projection.Projection;
import org.cts.op.transformation.GeoTransformation;
import org.cts.units.Unit;

/**
 *
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public final class PrjWriter {

    /**
     * Convert a CRS to a WKT representation
     *
     * @param crs
     * @return
     */
    public static String crsToWKT(CoordinateReferenceSystem crs) {
        Datum datum = crs.getDatum();

        StringBuilder w = new StringBuilder();
        boolean pr = crs instanceof ProjectedCRS;

        if (crs instanceof CompoundCRS) {
            w.append("COMPD_CS[\"");
            w.append(crs.getName());
            w.append("\",");
            w.append(crsToWKT(((CompoundCRS) crs).getHorizontalCRS()));
            w.append(',');
            w.append(crsToWKT(((CompoundCRS) crs).getVerticalCRS()));
            if (!crs.getAuthorityName().startsWith(Identifiable.LOCAL)) {
                w.append(',');
                addAuthority(crs, w);
            }
            w.append(']');
        } else {
            if (crs instanceof VerticalCRS) {
                w.append("VERT_CS[\"");
                w.append(crs.getName());
                w.append("\",");
                addVertDatum((VerticalDatum) crs.getDatum(), w);
                w.append(',');
            } else {
                // projected or not projection
                if (pr) {
                    w.append("PROJCS[\"");
                    w.append(crs.getName());
                    w.append("\",GEOGCS[\"");
                    w.append(datum.getShortName());
                } else if (crs instanceof GeocentricCRS) {
                    w.append("GEOCCS[\"");
                    w.append(crs.getName());
                } else {
                    w.append("GEOGCS[\"");
                    w.append(crs.getName());
                }
                w.append("\",");
                addDatum(crs.getDatum(), w);
                w.append(',');
                addPrimeMeridian(crs.getDatum().getPrimeMeridian(), w);
                w.append(',');
            }
            if (pr) {
                Projection proj = crs.getProjection();
                w.append("PROJECTION[\"");
                w.append(proj.getName());
                w.append("\"],PARAMETER[\"").append(Parameter.LATITUDE_OF_ORIGIN).append("\",");
                if (isInteger(fromRadianToDegree(proj.getLatitudeOfOrigin()), 1E-11)) {
                    w.append(Math.round(fromRadianToDegree(proj.getLatitudeOfOrigin())));
                } else {
                    w.append(fromRadianToDegree(proj.getLatitudeOfOrigin()));
                }
                if (proj.getStandardParallel1() != 0.0) {
                    w.append("],PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_1).append("\",");
                    if (isInteger(fromRadianToDegree(proj.getStandardParallel1()), 1E-11)) {
                        w.append(Math.round(fromRadianToDegree(proj.getStandardParallel1())));
                    } else {
                        w.append(fromRadianToDegree(proj.getStandardParallel1()));
                    }
                }
                if (proj.getStandardParallel2() != 0.0) {
                    w.append("],PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_2).append("\",");
                    if (isInteger(fromRadianToDegree(proj.getStandardParallel2()), 1E-11)) {
                        w.append(Math.round(fromRadianToDegree(proj.getStandardParallel2())));
                    } else {
                        w.append(fromRadianToDegree(proj.getStandardParallel2()));
                    }
                }
                w.append("],PARAMETER[\"").append(Parameter.CENTRAL_MERIDIAN).append("\",");
                if (isInteger(proj.getCentralMeridian(), 1E-11)) {
                    w.append(Math.round(proj.getCentralMeridian()));
                } else {
                    w.append(proj.getCentralMeridian());
                }
                w.append("],PARAMETER[\"").append(Parameter.SCALE_FACTOR).append("\",");
                if (isInteger(proj.getScaleFactor(), 1E-11)) {
                    w.append(Math.round(proj.getScaleFactor()));
                } else {
                    w.append(proj.getScaleFactor());
                }
                w.append("],PARAMETER[\"").append(Parameter.FALSE_EASTING).append("\",");
                if (isInteger(proj.getFalseEasting(), 1E-11)) {
                    w.append(Math.round(proj.getFalseEasting()));
                } else {
                    w.append(proj.getFalseEasting());
                }
                w.append("],PARAMETER[\"").append(Parameter.FALSE_NORTHING).append("\",");
                if (isInteger(proj.getFalseNorthing(), 1E-11)) {
                    w.append(Math.round(proj.getFalseNorthing()));
                } else {
                    w.append(proj.getFalseNorthing());
                }
                w.append("]],");
            }
            addUnit(crs.getCoordinateSystem().getUnit(0), w);
            w.append(',');
            for (int i = 0; i < crs.getCoordinateSystem().getDimension(); i++) {
                addAxis(crs.getCoordinateSystem().getAxis(i), w);
                w.append(',');
            }
            addAuthority(crs, w);
            w.append(']');
        }
        return w.toString();
    }

    private static void addAuthority(Identifiable obj, StringBuilder w) {
        w.append("AUTHORITY[\"");
        w.append(obj.getAuthorityName());
        w.append("\",\"");
        w.append(obj.getAuthorityKey());
        w.append("\"]");
    }

    private static void addAxis(Axis axis, StringBuilder w) {
        w.append("AXIS[\"");
        w.append(axis.getName());
        w.append("\",");
        w.append(axis.getDirection());
        w.append(']');
    }

    private static void addUnit(Unit unit, StringBuilder w) {
        w.append("UNIT[\"");
        w.append(unit.getName());
        w.append("\",");
        if (isInteger(1. / unit.getScale(), 1E-11)) {
            w.append(Math.round(1. / unit.getScale()));
        } else {
            w.append(1. / unit.getScale());
        }
        if (!unit.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            addAuthority(unit, w);
        }
        w.append(']');
    }

    private static void addPrimeMeridian(PrimeMeridian pm, StringBuilder w) {
        w.append("PRIMEM[\"");
        w.append(pm.getName());
        w.append("\",");
        w.append(pm.getLongitudeFromGreenwichInDegrees());
        if (!pm.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            addAuthority(pm, w);
        }
        w.append(']');
    }

    private static void addSpheroid(Ellipsoid ellps, StringBuilder w) {
        w.append("SPHEROID[\"");
        w.append(ellps.getName());
        w.append("\",");
        w.append(ellps.getSemiMajorAxis());
        w.append(',');
        if (ellps.getInverseFlattening() != Double.POSITIVE_INFINITY) {
            w.append(ellps.getInverseFlattening());
        } else {
            w.append(0);
        }
        if (!ellps.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            addAuthority(ellps, w);
        }
        w.append(']');
    }

    private static void addDatum(Datum datum, StringBuilder w) {
        w.append("DATUM[\"");
        w.append(datum.getName());
        w.append("\",");
        addSpheroid(datum.getEllipsoid(), w);
        CoordinateOperation towgs84 = datum.getToWGS84();
        if ((towgs84 != null) && (towgs84 instanceof GeoTransformation)) {
            GeoTransformation geoTransformation = (GeoTransformation) towgs84;
            w.append(geoTransformation.toWKT());
        } else if (towgs84 instanceof Identity) {
            w.append(",TOWGS84[0,0,0,0,0,0,0]");
        }
        if (!datum.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            addAuthority(datum, w);
        }
        w.append(']');
    }

    private static void addVertDatum(VerticalDatum vd, StringBuilder w) {
        w.append("VERT_DATUM[\"");
        w.append(vd.getName());
        w.append("\",");
        w.append(VerticalDatum.getTypeNumber(vd.getType()));
        if (!vd.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            addAuthority(vd, w);
        }
        w.append(']');
    }

    /**
     * Returns whether the double is equals to its nearest integer using the
     * tolerance given in parameter.
     *
     * @param a the double to test
     * @param tol the tolerance of the equality
     */
    private static boolean isInteger(double a, double tol) {
        return (Math.abs(a - ((double) Math.round(a))) < tol);
    }

    /**
     * Converts the input value from radian to degree.
     *
     * @param alpha the value (in radians) to convert
     */
    private static double fromRadianToDegree(double alpha) {
        UnitConversion op = UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE);
        double result = 0;
        try {
            result = op.transform(new double[]{alpha})[0];
        } catch (IllegalCoordinateException ex) {
            Logger.getLogger(PrjWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Returns the WKT in parameter into a Human-Readable OGC WKT form.
     *
     * @param wkt the OGC WKT String to transform.
     */
    public static String formatWKT(String wkt) {
        StringBuilder w = new StringBuilder();
        int n = 0;
        int index;
        int ind;
        String begin;
        String end;
        boolean dontAddAlinea = false;
        String[] wktexp = wkt.split("]],");
        for (int i = 0; i < wktexp.length; i++) {
            index = wktexp[i].indexOf("[");
            begin = wktexp[i].substring(0, index + 1);
            w.append(begin);
            end = wktexp[i].substring(index + 1);
            ind = end.indexOf(",");
            while (ind != -1) {
                begin = end.substring(0, ind + 1);
                index = end.indexOf("[");
                end = end.substring(ind + 1);
                if (dontAddAlinea) {
                    w.append("\n").append(indent(n)).append(begin);
                } else if (ind < index || index == -1) {
                    w.append(begin);
                } else {
                    n++;
                    w.append("\n").append(indent(n)).append(begin);
                }
                dontAddAlinea = begin.substring(begin.length() - 2).equals("],");
                ind = end.indexOf(",");
            }
            n = checkIndent(end, n);
            w.append(end);
            if (i != wktexp.length - 1) {
                n--;
                w.append("]],\n").append(indent(n));
            }
        }
        return w.toString();
    }

    /**
     * Return a String constituted by {@code n} indent. One indent = four space.
     *
     * @param n the number of indent wanted
     */
    private static String indent(int n) {
        StringBuilder w = new StringBuilder();
        for (int i = 0; i < n; i++) {
            w = w.append("    ");
        }
        return w.toString();
    }

    /**
     * Decrese the number of required indents, depending on the number of node
     * closed at the end of line.
     *
     * @param end the end of a node
     * @param n the current number of indent
     */
    private static int checkIndent(String end, int n) {
        int k = end.length() - 1;
        while (end.substring(k, k + 1).equals("]")) {
            n--;
            k--;
        }
        return n;
    }

    /**
     * Create a new PrjWriter.
     */
    private PrjWriter() {
    }
}
