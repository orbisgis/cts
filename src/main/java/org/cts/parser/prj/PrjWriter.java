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
import org.cts.IllegalCoordinateException;

import org.cts.op.CoordinateOperation;
import org.cts.Parameter;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.Geographic2DCRS;
import org.cts.crs.Geographic3DCRS;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.Datum;
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
        boolean pr = true;

        if (crs instanceof Geographic2DCRS || crs instanceof Geographic3DCRS) {
            pr = false;
        }

        // projected or not projection
        if (pr) {
            w.append("PROJCS[");
            w.append('"').append(crs.getName()).append("\",");
        }

        w.append("GEOGCS[");
        w.append('"').append(datum.getShortName()).append("\",");


        w.append("DATUM[");
        w.append('"').append(datum.getName()).append("\",");


        w.append("SPHEROID[");
        w.append('"').append(datum.getEllipsoid().getName()).append("\",");

        w.append(datum.getEllipsoid().getSemiMajorAxis());
        if (datum.getEllipsoid().getInverseFlattening() != Double.POSITIVE_INFINITY) {
            w.append(',').append(datum.getEllipsoid().getInverseFlattening());
        } else {
            w.append(',').append(0);
        }
        w.append(",AUTHORITY[\"");
        w.append(datum.getEllipsoid().getAuthorityName()).append("\",\"");
        w.append(datum.getEllipsoid().getAuthorityKey()).append("\"]");

        // close spheroid
        w.append(']');
        CoordinateOperation towgs84 = datum.getToWGS84();

        if ((towgs84 != null) && (towgs84 instanceof GeoTransformation)) {

            GeoTransformation geoTransformation = (GeoTransformation) towgs84;
            w.append(geoTransformation.toWKT());
        }
        w.append(",AUTHORITY[\"");
        w.append(datum.getAuthorityName()).append("\",\"");
        w.append(datum.getAuthorityKey()).append("\"]");
        // close datum
        w.append(']');


        w.append(",PRIMEM[");
        String pmName = datum.getPrimeMeridian().getName();
        w.append('"');
        if (pmName != null) {
            w.append(pmName);
        }
        w.append("\",");
        w.append(datum.getPrimeMeridian().getLongitudeFromGreenwichInDegrees());

        w.append(",AUTHORITY[\"");
        w.append(datum.getPrimeMeridian().getAuthorityName()).append("\",\"");
        w.append(datum.getPrimeMeridian().getAuthorityKey()).append("\"]");

        // close pm
        w.append(']');

        if (!pr) {
            w.append(",UNIT[\"");
            w.append(crs.getCoordinateSystem().getUnit(0).getName()).append("\",");
            if (isInteger(crs.getCoordinateSystem().getUnit(0).getScale(), 1E-11)) {
                w.append(Math.round(crs.getCoordinateSystem().getUnit(0).getScale()));
            } else {
                w.append(crs.getCoordinateSystem().getUnit(0).getScale());
            }
            w.append(",AUTHORITY[\"");
            w.append(crs.getCoordinateSystem().getUnit(0).getAuthorityName()).append("\",\"");
            w.append(crs.getCoordinateSystem().getUnit(0).getAuthorityKey()).append("\"]");
            w.append("]");


            w.append(",AUTHORITY[\"");
            w.append(crs.getAuthorityName()).append("\",\"");
            w.append(crs.getAuthorityKey()).append("\"]");
        }


        // close geogcs
        w.append(']');

        if (pr) {
            CoordinateSystem cs = crs.getCoordinateSystem();
            w.append(",UNIT[");
            w.append('"').append(cs.getUnit(0).getName()).append("\",");
            if (isInteger(1. / cs.getUnit(0).getScale(), 1E-11)) {
                w.append(Math.round(1. / cs.getUnit(0).getScale()));
            } else {
                w.append(1. / cs.getUnit(0).getScale());
            }
            w.append(",AUTHORITY[\"");
            w.append(cs.getUnit(0).getAuthorityName()).append("\",\"");
            w.append(cs.getUnit(0).getAuthorityKey()).append("\"]");
            w.append("]");

            Projection proj = crs.getProjection();

            w.append(",PROJECTION[");
            w.append('"').append(proj.getName()).append("\"]");


            w.append(",PARAMETER[\"").append(Parameter.LATITUDE_OF_ORIGIN).append("\",");
            if (isInteger(fromRadianToDegree(proj.getLatitudeOfOrigin()), 1E-11)) {
                w.append(Math.round(fromRadianToDegree(proj.getLatitudeOfOrigin()))).append(']');
            } else {
                w.append(fromRadianToDegree(proj.getLatitudeOfOrigin())).append(']');
            }

            if (proj.getStandardParallel1() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_1).append("\",");
                if (isInteger(fromRadianToDegree(proj.getStandardParallel1()), 1E-11)) {
                    w.append(Math.round(fromRadianToDegree(proj.getStandardParallel1()))).append(']');
                } else {
                    w.append(fromRadianToDegree(proj.getStandardParallel1())).append(']');
                }
            }

            if (proj.getStandardParallel2() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_2).append("\",");
                if (isInteger(fromRadianToDegree(proj.getStandardParallel2()), 1E-11)) {
                    w.append(Math.round(fromRadianToDegree(proj.getStandardParallel2()))).append(']');
                } else {
                    w.append(fromRadianToDegree(proj.getStandardParallel2())).append(']');
                }
            }

            w.append(",PARAMETER[\"").append(Parameter.CENTRAL_MERIDIAN).append("\",");
            if (isInteger(proj.getCentralMeridian(), 1E-11)) {
                w.append(Math.round(proj.getCentralMeridian())).append(']');
            } else {
                w.append(proj.getCentralMeridian()).append(']');
            }

            w.append(",PARAMETER[\"").append(Parameter.SCALE_FACTOR).append("\",");
            if (isInteger(proj.getScaleFactor(), 1E-11)) {
                w.append(Math.round(proj.getScaleFactor())).append(']');
            } else {
                w.append(proj.getScaleFactor()).append(']');
            }

            w.append(",PARAMETER[\"").append(Parameter.FALSE_EASTING).append("\",");
            if (isInteger(proj.getFalseEasting(), 1E-11)) {
                w.append(Math.round(proj.getFalseEasting())).append(']');
            } else {
                w.append(proj.getFalseEasting()).append(']');
            }

            w.append(",PARAMETER[\"").append(Parameter.FALSE_NORTHING).append("\",");
            if (isInteger(proj.getFalseNorthing(), 1E-11)) {
                w.append(Math.round(proj.getFalseNorthing())).append(']');
            } else {
                w.append(proj.getFalseNorthing()).append(']');
            }

            w.append(",AUTHORITY[\"");
            w.append(crs.getAuthorityName()).append("\",\"");
            w.append(crs.getAuthorityKey()).append("\"]");
            w.append(",AXIS[\"");
            w.append(cs.getAxis(0).getName()).append("\",");
            w.append(cs.getAxis(0).getDirection()).append("]");
            w.append(",AXIS[\"");
            w.append(cs.getAxis(1).getName()).append("\",");
            w.append(cs.getAxis(1).getDirection()).append("]");

            // close projCS
            w.append(']');
        }



        return w.toString();
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
