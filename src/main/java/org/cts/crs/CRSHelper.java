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
package org.cts.crs;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.cts.*;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.Identity;
import org.cts.op.projection.*;
import org.cts.op.transformation.GeocentricTranslation;
import org.cts.op.transformation.SevenParameterTransformation;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;
import org.cts.units.Measure;
import org.cts.units.Quantity;
import org.cts.units.Unit;

import java.util.HashMap;
import java.util.Map;
import org.cts.op.transformation.NTv2GridShiftTransformation;

/**
 * @TODO Not sure this class is useful here. I'd prefer a clear separation
 * between the model (CRS/Datum/Ellipsoid/Projection...) and the parsers
 * which create CRS from a file or from a stream. CRSHelper is in-between,
 * no more a file, but not yet a model.
 * @author Michaël Michaud, Erwan Bocher, Jules Party
 */
public class CRSHelper {

        static final Logger LOGGER = Logger.getLogger(CRSHelper.class);

        /**
         * Creates a new {@link org.cts.crs.CoordinateReferenceSystem}
         * with the given Identifier and parameters.
         */
        public static CoordinateReferenceSystem createCoordinateReferenceSystem(Identifier identifier, Map<String, String> parameters) {

                //Get the datum
                GeodeticDatum geodeticDatum = getDatum(parameters);

                if (geodeticDatum == null) {
                        LOGGER.warn("No datum definition. Cannot create the"
                                + "CoordinateReferenceSystem");
                        return null;
                }
                
                GeodeticCRS crs;

                String sproj = parameters.get(ProjKeyParameters.proj);
                String sunit = parameters.get(ProjKeyParameters.units);
                String stometer = parameters.get(ProjKeyParameters.to_meter);
                if (null == sproj) {
                        LOGGER.warn("No projection defined for this Coordinate Reference System");
                        return null;
                }

                //It's not a projected CRS
                if (sproj.equals(ProjValueParameters.GEOCENT)) {
                        Unit unit = Unit.METER;
                        if (stometer != null) {
                                unit = new Unit(Quantity.LENGTH, "", Double.parseDouble(sunit),
                                        "");
                        }
                        CoordinateSystem cs = new CoordinateSystem(new Axis[]{Axis.X,
                                        Axis.Y, Axis.Z}, new Unit[]{unit, unit, unit});

                        crs = new GeocentricCRS(identifier, geodeticDatum,
                                cs);
                } else if (sproj.equals(ProjValueParameters.LONGLAT)) {
                        Unit unit = Unit.DEGREE;
                        if (stometer != null) {
                                unit = new Unit(Quantity.LENGTH, "", Double.parseDouble(sunit),
                                        "");
                        }
                        CoordinateSystem cs = new CoordinateSystem(new Axis[]{
                                        Axis.LONGITUDE, Axis.LATITUDE, Axis.HEIGHT}, new Unit[]{
                                        unit, unit, Unit.METER});
                        crs = new Geographic3DCRS(identifier, geodeticDatum,
                                cs);
                } else {
                        Projection proj = getProjection(sproj, geodeticDatum.getEllipsoid(),
                                parameters);
                        if (null != proj) {
                                crs = new ProjectedCRS(identifier,
                                        geodeticDatum, proj);
                        } else {
                                LOGGER.warn("Unknown projection : " + sproj);
                                return null;
                        }
                }
                
                setNadgrids(crs, parameters);
                return crs;
        }

        /**
         * Set default toWGS84 operation to a {@link org.cts.datum.GeodeticDatum}.
         *
         * @param gd the GeodeticDatum we want to associate default toWGS84 operation with
         * @param param the toWGS84 parameters to associate to gd
         */
        public static void setDefaultWGS84Parameters(GeodeticDatum gd, Map<String, String> param) {
                CoordinateOperation op;
                String towgs84Parameters = param.get(ProjKeyParameters.towgs84);
                if (null == towgs84Parameters) {
                        gd.setDefaultToWGS84Operation(Identity.IDENTITY);
                        return;
                }
                double[] bwp = new double[7];
                String[] sbwp = towgs84Parameters.split(",");
                boolean identity = true;
                boolean translation = true;
                for (int i = 0; i < sbwp.length; i++) {
                        bwp[i] = Double.parseDouble(sbwp[i]);
                        if (bwp[i] != 0) {
                                identity = false;
                        }
                        if (bwp[i] != 0 && i > 2) {
                                translation = false;
                        }
                }
                if (identity) {
                        op = Identity.IDENTITY;
                } else if (translation) {
                        op = new GeocentricTranslation(bwp[0], bwp[1], bwp[2]);
                } else {
                        op = SevenParameterTransformation.createBursaWolfTransformation(
                                bwp[0], bwp[1], bwp[2], bwp[3], bwp[4], bwp[5], bwp[6]);
                }
                if (op != null) {
                        gd.setDefaultToWGS84Operation(op);
                }
        }

        /**
         * Returns a {@link org.cts.datum.PrimeMeridian} from its name or from its parameters.
         *
         * @param param parameters including a {@link org.cts.datum.PrimeMeridian} definition
         * @return a {@link org.cts.datum.PrimeMeridian}
         */
        public static PrimeMeridian getPrimeMeridian(Map<String, String> param) {
                String pmName = param.get(ProjKeyParameters.pm);
                PrimeMeridian pm;
                if (null != pmName) {
                    pm = PrimeMeridian.primeMeridianFromName.get(pmName.toLowerCase());
                    if (pm==null) {
                                try {
                                        double pmdd = Double.parseDouble(pmName);
                                        pm = PrimeMeridian.createPrimeMeridianFromDDLongitude(
                                                new Identifier(PrimeMeridian.class,
                                                Identifiable.UNKNOWN), pmdd);
                                } catch (NumberFormatException ex) {
                                        LOGGER.error(pmName + " prime meridian is not parsable");
                                        return null;
                                }
                        }
                } else {
                        pm = PrimeMeridian.GREENWICH;
                }
                return pm;
        }

        /**
         * Returns true if the {@link org.cts.datum.GeodeticDatum} with this name is supported.
         * TODO : add other datum
         *
         * @param datumName name of the GeodeticDatum to check
         * @return true if datumName is supported
         */
        public static boolean isDatumSupported(String datumName) {
                if (null != datumName) {
                        if (datumName.equals(GeodeticDatum.WGS84.getName())) {
                                return true;
                        } else if (datumName.equals(GeodeticDatum.ED50.getName())) {
                                return true;
                        } else if (datumName.equals(GeodeticDatum.NTF.getName())) {
                                return true;
                        } else if (datumName.equals(GeodeticDatum.NTF_PARIS.getName())) {
                                return true;
                        } else if (datumName.equals(GeodeticDatum.RGF93.getName())) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Returns a {@link GeodeticDatum} from a map of parameters.
         */
        public static GeodeticDatum getDatum(Map<String, String> param) {
                String datumName = param.get(ProjKeyParameters.datum);
                if (null != datumName) {
                        if (datumName.equalsIgnoreCase(GeodeticDatum.WGS84.getShortName())) {
                                return GeodeticDatum.WGS84;
                        } else if (datumName.equalsIgnoreCase(GeodeticDatum.ED50.getShortName())) {
                                return GeodeticDatum.ED50;
                        } else if (datumName.equalsIgnoreCase(GeodeticDatum.NTF.getShortName())) {
                                return GeodeticDatum.NTF;
                        } else if (datumName.equalsIgnoreCase(GeodeticDatum.NTF_PARIS.getShortName())) {
                                return GeodeticDatum.NTF_PARIS;
                        } else if (datumName.equalsIgnoreCase(GeodeticDatum.RGF93.getShortName())) {
                                return GeodeticDatum.RGF93;
                        }
               } else {
                Ellipsoid ell = getEllipsoid(param);
                PrimeMeridian pm = getPrimeMeridian(param);
                if (null != pm && null != ell) {
                    GeodeticDatum gd = new GeodeticDatum(pm, ell);
                    setDefaultWGS84Parameters(gd, param);
                    gd = gd.checkExistingGeodeticDatum();
                    return gd;
                }
            }
            return null;
        }
        
        private static void setNadgrids(GeodeticCRS crs, Map<String, String> param) {
            String nadgrids = param.get(ProjKeyParameters.nadgrids);
                    if (nadgrids != null) {
                        String[] grids = nadgrids.split(",");
                        for (String grid : grids) {
                            if (!grid.equals("null")) {
                                LOGGER.warn("A grid has been founded.");
                                if (grid.equals("@null")) {
                                    crs.getDatum().addCoordinateOperation(GeodeticDatum.WGS84, Identity.IDENTITY);
                                    GeodeticDatum.WGS84.addCoordinateOperation(crs.getDatum(), Identity.IDENTITY);
                                } else {
                                    try {
                                        //NTv2GridShiftTransformation gt = new NTv2GridShiftTransformation(
                                        //        GridShift.class.getResource(grid).toURI().toURL());
                                        NTv2GridShiftTransformation gt = NTv2GridShiftTransformation.createNTv2GridShiftTransformation(grid);
                                        gt.setMode(NTv2GridShiftTransformation.SPEED);
                                        crs.addGridTransformation(GeodeticDatum.getGeodeticDatumFromShortName(gt.getToDatum()), gt);
                                    } catch (IOException ex) {
                                        LOGGER.error("Cannot found the nadgrid", ex);
                                    } catch (URISyntaxException ex) {
                                        LOGGER.error("Cannot found the nadgrid", ex);
                                    }
                                }
                            }
                        }
                    }
        }

        /**
         * Returns an Ellipsoid from its name or from its parameter.
         *
         * Try first to find a known ellipsoid then to create an ellipsoid from
         * its parameter then to get the ellipsoid associated with a known datum
         */
        public static Ellipsoid getEllipsoid(Map<String, String> param) {
                String ellipsoidName = param.get(ProjKeyParameters.ellps);
                String a = param.get(ProjKeyParameters.a);
                String b = param.get(ProjKeyParameters.b);
                String rf = param.get(ProjKeyParameters.rf);
                String datum = param.get(ProjKeyParameters.datum);

                if (null != ellipsoidName) {
                    ellipsoidName = ellipsoidName.replaceAll("[^a-zA-Z0-9]", "");
                    return Ellipsoid.ellipsoidFromName.get(ellipsoidName.toLowerCase());
                } else if (null != a && (null != b || null != rf)) {
                        double a_ = Double.parseDouble(a);
                        if (null != b) {
                                double b_ = Double.parseDouble(b);
                                return Ellipsoid.createEllipsoidFromSemiMinorAxis(a_, b_);
                        } else {
                                double rf_ = Double.parseDouble(rf);
                                return Ellipsoid.createEllipsoidFromInverseFlattening(a_, rf_);
                        }

                } else if (null != datum) {
                        GeodeticDatum gd = getDatum(param);
                        if (gd != null) {
                                return gd.getEllipsoid();
                        } else {
                                LOGGER.warn("The unknown datum do not define an ellipsoid");
                                return null;
                        }
                } else {
                        LOGGER.warn("Ellipsoid cannot be defined");
                        return null;
                }
        }

        /**
         * Creates a {@link org.cts.op.projection.Projection} from a projection type,
         * an ellipsoid and a map of parameters.
         *
         * @param projectionName name of the projection type
         * @param ell ellipsoid to use
         * @param param parameters of this projection
         * @return a Projection
         */
        public static Projection getProjection(String projectionName, Ellipsoid ell,
                Map<String, String> param) {
                String slat_0 = param.get("lat_0");
                String slat_1 = param.get("lat_1");
                String slat_2 = param.get("lat_2");
                String slat_ts = param.get("lat_ts");
                String slon_0 = param.get("lon_0");
                String slonc = param.get("lonc");
                String salpha = param.get("alpha");
                String sgamma = param.get("gamma");
                String sk = param.get("k");
                String sk_0 = param.get("k_0");
                String sx_0 = param.get("x_0");
                String sy_0 = param.get("y_0");
                double lat_0 = slat_0 != null ? Double.parseDouble(slat_0) : 0.;
                double lat_1 = slat_1 != null ? Double.parseDouble(slat_1) : 0.;
                double lat_2 = slat_2 != null ? Double.parseDouble(slat_2) : 0.;
                double lat_ts = slat_ts != null ? Double.parseDouble(slat_ts) : 0.;
                double lon_0 = slon_0 != null ? Double.parseDouble(slon_0) : slonc != null ? Double.parseDouble(slonc) : 0.;
                double alpha = salpha != null ? Double.parseDouble(salpha) : 0.;
                double gamma = sgamma != null ? Double.parseDouble(sgamma) : 0.;
                if (sk!=null && sk_0!=null) {
                    if (!sk.equals(sk_0)) {
                        LOGGER.warn("Two different scales factor at origin are defined, the one chosen for the projection is k_0");
                    }
                }
                double k_0 = sk_0 != null ? Double.parseDouble(sk_0) : sk != null ? Double.parseDouble(sk) : 1.;
                double x_0 = sx_0 != null ? Double.parseDouble(sx_0) : 0.;
                double y_0 = sy_0 != null ? Double.parseDouble(sy_0) : 0.;
                Map<String, Measure> map = new HashMap<String, Measure>();
                map.put(Parameter.CENTRAL_MERIDIAN, new Measure(lon_0, Unit.DEGREE));
                map.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(lat_0, Unit.DEGREE));
                map.put(Parameter.STANDARD_PARALLEL_1, new Measure(lat_1, Unit.DEGREE));
                map.put(Parameter.STANDARD_PARALLEL_2, new Measure(lat_2, Unit.DEGREE));
                map.put(Parameter.LATITUDE_OF_TRUE_SCALE, new Measure(lat_ts, Unit.DEGREE));
                map.put(Parameter.AZIMUTH_OF_INITIAL_LINE, new Measure(alpha, Unit.DEGREE));
                map.put(Parameter.ANGLE_RECTIFIED_TO_OBLIQUE, new Measure(gamma, Unit.DEGREE));
                map.put(Parameter.SCALE_FACTOR, new Measure(k_0, Unit.UNIT));
                map.put(Parameter.FALSE_EASTING, new Measure(x_0, Unit.METER));
                map.put(Parameter.FALSE_NORTHING, new Measure(y_0, Unit.METER));

                if (projectionName.equalsIgnoreCase(ProjValueParameters.LCC)) {
                        if (param.get("lat_2") != null) {
                                return new LambertConicConformal2SP(ell, map);
                        } else {
                                return new LambertConicConformal1SP(ell, map);
                        }
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.TMERC)) {
                        return new TransverseMercator(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.UTM)) {
                        int zone = param.get("zone") != null ? Integer.parseInt(param.get("zone")) : 0;
                        lon_0 = (6.0 * (zone - 1) + 183.0) % 360.0;
                        lon_0 = (((lon_0+180)%360)-180); // set lon_0 to -180;180 interval
                        y_0 = param.containsKey("south") ? 10000000.0 : 0.0;
                        map.put(Parameter.CENTRAL_MERIDIAN, new Measure(lon_0,
                                Unit.DEGREE));
                        map.put(Parameter.FALSE_NORTHING, new Measure(y_0, Unit.METER));
                        return new UniversalTransverseMercator(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.MERC)) {
                    return new Mercator1SP(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.EQC)) {
                    return new EquidistantCylindrical(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.STERE)) {
                    return new Stereographic(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.STEREA)) {
                    return new ObliqueStereographicAlternative(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.CASS)) {
                    return new CassiniSoldner(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.OMERC)) {
                    return new ObliqueMercator(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.SOMERC)) {
                    return new SwissObliqueMercator(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.AEA)) {
                    return new AlbersEqualArea(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.LAEA)) {
                    return new LambertAzimuthalEqualArea(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.POLY)) {
                    return new Polyconic(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.CEA)) {
                    return new CylindricalEqualArea(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.MILL)) {
                    return new MillerCylindrical(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.KROVAK)) {
                    return new Krovak(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.NZMG)) {
                    return new NewZealandMapGrid(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.GSTMERC)) {
                    return new GaussSchreiberTransverseMercator(ell, map);
                } else if (projectionName.equalsIgnoreCase(ProjValueParameters.LEAC)) {
                    // LEAC stand for LambertEqualAreaConic
                    // It is similar to AlbersEqualArea except that one of the two standard parallels is at the pole (north of south)
                    // See <http://www.georeference.org/doc/albers_conical_equal_area.htm>
                    if (map.containsKey(ProjKeyParameters.south)) {
                        map.put(ProjKeyParameters.lat_2, new Measure(-90, Unit.DEGREE));
                    } else {
                        map.put(ProjKeyParameters.lat_2, new Measure(90, Unit.DEGREE));
                    }
                    // The other standard parallel is the equator by default
                    if (!map.containsKey(ProjKeyParameters.lat_1)) {
                        map.put(ProjKeyParameters.lat_1, new Measure(0, Unit.DEGREE));
                    }
                    return new AlbersEqualArea(ell, map);
                } else {
                        throw new RuntimeException("Cannot create the projection " + projectionName);
                }
        }
}
