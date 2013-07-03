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

import org.cts.op.CoordinateOperation;
import org.cts.Parameter;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.Geographic2DCRS;
import org.cts.crs.Geographic3DCRS;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.Datum;
import org.cts.op.projection.Projection;
import org.cts.op.transformation.GeoTransformation;

/**
 *
 * @author Antoine Gourlay
 * @author Erwan Bocher
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
        w.append('"').append(crs.getName()).append("\",");


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

        // close spheroid
        w.append(']');
        CoordinateOperation towgs84 = datum.getToWGS84();

        if ((towgs84 != null) && (towgs84 instanceof GeoTransformation)) {

            GeoTransformation geoTransformation = (GeoTransformation) towgs84;
            w.append(geoTransformation.toWKT());
        }
        // close datum
        w.append(']');


        w.append(",PRIMEM[");
        String pmName = datum.getPrimeMeridian().getName();
        w.append('"');
        if (pmName != null) {
            w.append(pmName);
        }
        w.append(" \",");
        w.append(datum.getPrimeMeridian().getLongitudeFromGreenwichInDegrees());

        // close pm
        w.append(']');

        // close geogcs
        w.append(']');

        if (pr) {
            CoordinateSystem cs = crs.getCoordinateSystem();
            w.append(",UNIT[");
            w.append('"').append(cs.getUnit(0).getName()).append("\",");
            w.append(1. / cs.getUnit(0).getScale());
            w.append("]");

            Projection proj = crs.getProjection();

            w.append(",PROJECTION[");
            w.append('"').append(proj.getName()).append("\"]");


            if (proj.getLatitudeOfOrigin() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.LATITUDE_OF_ORIGIN).append("\",");
                w.append(proj.getLatitudeOfOrigin()).append(']');
            }

            if (proj.getStandardParallel1() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_1).append("\",");
                w.append(proj.getStandardParallel1()).append(']');
            }

            if (proj.getStandardParallel2() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.STANDARD_PARALLEL_2).append("\",");
                w.append(proj.getStandardParallel2()).append(']');
            }

            if (proj.getCentralMeridian() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.CENTRAL_MERIDIAN).append("\",");
                w.append(proj.getCentralMeridian()).append(']');
            }

            if (proj.getScaleFactor() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.SCALE_FACTOR).append("\",");
                w.append(proj.getScaleFactor()).append(']');
            }

            if (proj.getFalseEasting() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.FALSE_EASTING).append("\",");
                w.append(proj.getFalseEasting()).append(']');
            }

            if (proj.getFalseNorthing() != 0.0) {
                w.append(",PARAMETER[\"").append(Parameter.FALSE_NORTHING).append("\",");
                w.append(proj.getFalseNorthing()).append(']');
            }

            // close projCS
            w.append(']');
        }



        return w.toString();
    }

    private PrjWriter() {
    }
}
