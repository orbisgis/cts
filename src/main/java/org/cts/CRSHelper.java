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
package org.cts;

import java.util.HashMap;
import java.util.Map;

import org.cts.crs.*;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.datum.VerticalDatum;
import org.cts.op.*;
import org.cts.op.projection.*;
import org.cts.op.transformation.*;
import org.cts.parser.prj.PrjKeyParameters;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;
import org.cts.units.*;
import org.cts.util.AngleFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to build a new
 * {@link org.cts.crs.CoordinateReferenceSystem} from a map of parameters,
 * generally obtained from the parser of a {@link org.cts.registry.Registry}
 * or from an OGC WKT String.
 *
 * @TODO Not sure this class is useful here. I would prefer a clear separation
 * between the model (CRS/Datum/Ellipsoid/Projection...) and the parsers which
 * create CRS from a file or from a stream. CRSHelper is in-between, no more a
 * file, but not yet a model.
 * @author Michaël Michaud, Erwan Bocher, Jules Party
 */
public class CRSHelper {

    static final Logger LOGGER = LoggerFactory.getLogger(CRSHelper.class);

    /**
     * Creates a new {@link org.cts.crs.CoordinateReferenceSystem} with the
     * given {@link org.cts.Identifier} and parameters.
     *
     * @param identifier the identifier we want to associate with the desired
     * CRS
     * @param parameters the map of parameters defining the properties of the
     * desired CRS
     */
    public static CoordinateReferenceSystem createCoordinateReferenceSystem(Identifier identifier, Map<String, String> parameters) throws CRSException {
        if ((parameters.get(PrjKeyParameters.PROJCS) != null || parameters.get(PrjKeyParameters.GEOGCS) != null)
                && parameters.get(PrjKeyParameters.VERTCS) != null) {
            Identifier id = getIdentifier(parameters);
            GeodeticCRS horizontalCRS = (GeodeticCRS) CRSHelper.createCoordinateReferenceSystem(id, parameters);
            id = getIdentifier(parameters);
            VerticalCRS verticalCRS = (VerticalCRS) CRSHelper.createCoordinateReferenceSystem(id, parameters);
            return new CompoundCRS(identifier, horizontalCRS, verticalCRS);
        }

        //Get the datum
        GeodeticDatum geodeticDatum = getDatum(parameters);

        if (geodeticDatum == null) {
            VerticalDatum verticalDatum = getVerticalDatum(parameters);
            if (verticalDatum == null) {
                throw new CRSException("No datum definition. Cannot create the "
                        + "CoordinateReferenceSystem");
            } else {
                CoordinateSystem cs = getCoordinateSystem(parameters, 1);
                return new VerticalCRS(identifier, verticalDatum, cs);
            }
        }

        GeodeticCRS crs;

        String sproj = parameters.remove(ProjKeyParameters.proj);
        if (null == sproj) {
            throw new CRSException("No projection defined for this Coordinate Reference System");
        }

        //It's not a projected CRS
        if (sproj.equals(ProjValueParameters.GEOCENT)) {
            CoordinateSystem cs = getCoordinateSystem(parameters, 2);
            crs = new GeocentricCRS(identifier, geodeticDatum, cs);
        } else if (sproj.equals(ProjValueParameters.LONGLAT)) {
            CoordinateSystem cs = getCoordinateSystem(parameters, 3);
            if (cs.getDimension() == 2) {
                crs = new Geographic2DCRS(identifier, geodeticDatum, cs);
            } else {
                crs = new Geographic3DCRS(identifier, geodeticDatum, cs);
            }
        } else {
            CoordinateSystem cs = getCoordinateSystem(parameters, 4);
            Projection proj = getProjection(sproj, geodeticDatum.getEllipsoid(),
                    parameters);
            if (null != proj) {
                crs = new ProjectedCRS(identifier, geodeticDatum, cs, proj);
                // SPECIAL CASE OF THE PSEUDO-MERCATOR PROJECTION (EPSG:3857)
                // In the case of EPSG:3857 (pseudo-mercator), the proj4 description
                // gives the a and b parameters of the ellipsoid to be used for the projection
                // but does not give the datum/ellipsoid to be used (WGS 84)
                if (identifier.getCode().equals("EPSG:3857")) crs = new ProjectedCRS(identifier, GeodeticDatum.WGS84, cs, proj);
            } else {
                throw new CRSException("Unknown projection : " + sproj);
            }
        }
        setNadgrids(crs, parameters);
        // parameters read in the registry or in the WKT by CTS, but not used yet
        parameters.remove(PrjKeyParameters.GEOGUNIT);
        parameters.remove(PrjKeyParameters.GEOGUNITVAL);
        parameters.remove(PrjKeyParameters.GEOGUNITREFNAME);
        parameters.remove(ProjKeyParameters.wktext);
        parameters.remove(ProjKeyParameters.no_defs);
        return crs;
    }

    /**
     * Returns the {@link org.cts.Identifier} identifier of one of the CRS part
     * of CoumpoundCRS.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static Identifier getIdentifier(Map<String, String> param) {
        Identifier id;
        String name = param.remove(PrjKeyParameters.PROJCS);
        String refname = param.remove(PrjKeyParameters.PROJREFNAME);
        if (name != null) {
            param.remove(PrjKeyParameters.GEOGCS);
            param.remove(PrjKeyParameters.GEOGREFNAME);
        } else {
            name = param.remove(PrjKeyParameters.GEOGCS);
            refname = param.remove(PrjKeyParameters.GEOGREFNAME);
        }
        if (name == null) {
            name = param.remove(PrjKeyParameters.VERTCS);
            refname = param.remove(PrjKeyParameters.VERTREFNAME);
        }
        if (refname != null) {
            String[] authorityNameWithKey = refname.split(":");
            id = new Identifier(authorityNameWithKey[0], authorityNameWithKey[1], name);
        } else {
            id = new Identifier(CoordinateReferenceSystem.class, name);
        }
        return id;
    }

    /**
     * Returns a {@link org.cts.cs.CoordinateSystem} from parameters.
     *
     * @param param the map of parameters defining the properties of a CRS
     * @param crsType 1 = VerticalCRS, 2 = GeocentricCRS, 3 = GeographicCRS, 4
     * = ProjectedCRS
     */
    private static CoordinateSystem getCoordinateSystem(Map<String, String> param, int crsType) throws CRSException {
        Unit[] units;
        Axis[] axes;
        Quantity quant = Quantity.LENGTH;
        boolean isVert = false;
        int dim = 0;
        switch (crsType) {
            case 1:
                isVert = true;
                dim = 1;
                break;
            case 2:
                dim = 3;
                break;
            case 3:
                dim = param.get(PrjKeyParameters.AXIS3) != null ? 3 : 2;
                quant = Quantity.ANGLE;
                break;
            case 4:
                dim = 2;
                break;
        }
        units = new Unit[dim];
        axes = new Axis[dim];
        Unit unit = getUnit(quant, param, isVert);
        for (int i = 0; i < dim; i++) {
            units[i] = unit;
            axes[i] = getAxis(param, crsType, i);
        }
        return new CoordinateSystem(axes, units);
    }

    /**
     * Returns a {@link org.cts.cs.Axis} from its name or from its other
     * parameters. By default, it returns an {@link org.cts.cs.Axis} that
     * corresponds to the given CRS type and index.
     *
     * @param param the map of parameters defining the properties of a CRS
     * @param crsType 1 = VerticalCRS, 2 = GeocentricCRS, 3 = Geographic2DCRS, 4
     * = ProjectedCRS
     * @param index the index of the axis to defined (start with 0)
     */
    private static Axis getAxis(Map<String, String> param, int crsType, int index) throws CRSException {
        // crsType = 1 pour vertCRS ; 2 pour GEOCCRS ; 3 pour GEOGCRS ; 4 pour PROJ CRS.
        Axis axis;
        Axis defaultAxis = null;
        String saxis = null;
        String saxistype = null;
        switch (crsType) {
            case 1:
                saxis = param.remove(PrjKeyParameters.VERTAXIS);
                saxistype = param.remove(PrjKeyParameters.VERTAXISTYPE);
                defaultAxis = Axis.HEIGHT;
                break;
            case 2:
                switch (index) {
                    case 0:
                        saxis = param.remove(PrjKeyParameters.AXIS1);
                        saxistype = param.remove(PrjKeyParameters.AXIS1TYPE);
                        defaultAxis = Axis.X;
                        break;
                    case 1:
                        saxis = param.remove(PrjKeyParameters.AXIS2);
                        saxistype = param.remove(PrjKeyParameters.AXIS2TYPE);
                        defaultAxis = Axis.Y;
                        break;
                    case 2:
                        saxis = param.remove(PrjKeyParameters.AXIS3);
                        saxistype = param.remove(PrjKeyParameters.AXIS3TYPE);
                        defaultAxis = Axis.Z;
                        break;
                    default:
                        throw new CRSException("Wrong argument index: " + index + ". Parameter shall be between 1 and 3.");
                }
                break;
            case 3:
                switch (index) {
                    case 0:
                        saxis = param.remove(PrjKeyParameters.AXIS1);
                        saxistype = param.remove(PrjKeyParameters.AXIS1TYPE);
                        defaultAxis = Axis.LONGITUDE;
                        break;
                    case 1:
                        saxis = param.remove(PrjKeyParameters.AXIS2);
                        saxistype = param.remove(PrjKeyParameters.AXIS2TYPE);
                        defaultAxis = Axis.LATITUDE;
                        break;
                    case 2:
                        saxis = param.remove(PrjKeyParameters.AXIS3);
                        saxistype = param.remove(PrjKeyParameters.AXIS3TYPE);
                        defaultAxis = Axis.HEIGHT;
                    default:
                        throw new CRSException("Wrong argument index: " + index + ". Parameter shall be 1 or 2.");
                }
                break;
            case 4:
                switch (index) {
                    case 0:
                        saxis = param.remove(PrjKeyParameters.AXIS1);
                        saxistype = param.remove(PrjKeyParameters.AXIS1TYPE);
                        defaultAxis = Axis.EASTING;
                        break;
                    case 1:
                        saxis = param.remove(PrjKeyParameters.AXIS2);
                        saxistype = param.remove(PrjKeyParameters.AXIS2TYPE);
                        defaultAxis = Axis.NORTHING;
                        break;
                    default:
                        throw new CRSException("Wrong argument index: " + index + ". Parameter shall be 1 or 2.");
                }
                break;
            default:
                throw new CRSException("Wrong argument crsType: " + crsType + ". Parameter shall be between 1 and 4.");
        }
        Axis.Direction axistype = Axis.getDirection(saxistype);
        axis = Axis.getAxis(axistype, saxis);
        if (axis == null && saxis != null && axistype != null) {
            axis = new Axis(saxis, axistype);
        } else {
            axis = defaultAxis;
        }
        return axis;
    }

    /**
     * Returns a {@link org.cts.units.Unit} from its name or from its other
     * parameters. By default, it returns the base unit of the {@link Quantity}
     * in parameter or DEGREE, if the {@link Quantity} is ANGLE..
     *
     * @param quant the quantity of the desired unit (ANGLE for a geographic CRS,
     * else LENGTH)
     * @param param the map of parameters defining the properties of a CRS
     * @param isVertical true if the returned unit shall be used for a
     * VerticalCRS
     */
    private static Unit getUnit(Quantity quant, Map<String, String> param, boolean isVertical) {
        String sunit;
        String sunitval;
        String sunitAuth;
        Identifier id = null;
        if (isVertical) {
            sunit = param.remove(PrjKeyParameters.VERTUNIT);
            sunitval = param.remove(PrjKeyParameters.VERTUNITVAL);
            sunitAuth = param.remove(PrjKeyParameters.VERTUNITREFNAME);
        } else {
            sunit = param.remove(ProjKeyParameters.units);
            sunitval = param.remove(ProjKeyParameters.to_meter);
            sunitAuth = param.remove(PrjKeyParameters.UNITREFNAME);
        }
        Unit unit = Unit.getUnit(quant, sunit);
        sunit = sunit == null ? Identifiable.UNKNOWN : sunit;
        if (unit == null && sunitAuth != null) {
            String[] authNameWithKey = sunitAuth.split(":");
            id = new Identifier(authNameWithKey[0], authNameWithKey[1], sunit);
            unit = (Unit) IdentifiableComponent.getComponent(id);
        }
        if (unit == null && sunitval != null) {
            id = id == null ? new Identifier(Unit.class, sunit) : id;
            unit = new Unit(quant, Double.parseDouble(sunitval), id);
        }
        if (unit == null) {
            if (quant == Quantity.ANGLE) {
                unit = Unit.DEGREE;
            } else {
                unit = Unit.getBaseUnit(quant);
            }
        }
        return unit;
    }


    /**
     * Get the default toWGS84 operation from parameters given by the
     * {@code towgs84} keyword.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static GeocentricTransformation getToWGS84(Map<String, String> param) {
        GeocentricTransformation op;
        String towgs84Parameters = param.remove(ProjKeyParameters.towgs84);
        if (null == towgs84Parameters) {
            return Identity.IDENTITY;
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
        return op == null ? Identity.IDENTITY : op;
    }

    /**
     * Returns a {@link org.cts.datum.PrimeMeridian} from its name or from its
     * parameters, using {@code pm} keyword. By default, it returns Greenwich
     * prime meridian if no meridian is defined in {@code param}.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static PrimeMeridian getPrimeMeridian(Map<String, String> param) {
        String pmName = param.remove(ProjKeyParameters.pm);
        String pmValueWKT = param.remove(PrjKeyParameters.PMVALUE);
        String authCode = param.remove(PrjKeyParameters.PRIMEMREFNAME);
        Identifier id;
        if (authCode != null) {
            String[] authNameWithKey = authCode.split(":");
            id = pmName != null ? new Identifier(authNameWithKey[0], authNameWithKey[1], pmName)
                    : new Identifier(authNameWithKey[0], authNameWithKey[1], Identifiable.UNKNOWN);
        } else {
            id = pmName != null ? new Identifier(PrimeMeridian.class, pmName)
                    : new Identifier(PrimeMeridian.class);
        }
        PrimeMeridian pm = null;
        if (null != pmName) {
            pm = PrimeMeridian.primeMeridianFromName.get(pmName.toLowerCase());
            if (pm == null) {
                try {
                    double pmdd = Double.parseDouble(pmName);
                    pm = PrimeMeridian.createPrimeMeridianFromDDLongitude(id, pmdd);
                } catch (NumberFormatException ex) {
                    try {
                        double pmdd = Double.parseDouble(pmValueWKT);
                        pm = PrimeMeridian.createPrimeMeridianFromDDLongitude(id, pmdd);
                    } catch (NumberFormatException e) {
                        LOGGER.error(pmName + " prime meridian is not parsable");
                        return null;
                    }
                }
            }
        }
        if (pm == null && authCode != null) {
            pm = (PrimeMeridian) IdentifiableComponent.getComponent(id);
        }
        if (pm == null) {
            pm = PrimeMeridian.GREENWICH;
        }
        return pm;
    }

    /**
     * Returns a {@link GeodeticDatum} from a map of parameters. Try first to
     * obtain the {@link GeodeticDatum} from its name using {@code datum}
     * keyword. Then if {@code param} does not contain {@code datum} keyword or
     * if the name is not recognized, it uses
     * {@code getEllipsoid}, {@code getPrimeMeridian} and
     * {@code setDefaultWGS84Parameters} methods to define the
     * {@link GeodeticDatum}.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static GeodeticDatum getDatum(Map<String, String> param) {
        String datumName = param.remove(ProjKeyParameters.datum);
        String authCode = param.remove(PrjKeyParameters.DATUMREFNAME);
        GeodeticDatum gd = null;
        if (null != datumName) {
            gd = GeodeticDatum.getGeodeticDatum(datumName.toLowerCase());
        }
        if (gd == null && authCode != null) {
            String[] authNameWithKey = authCode.split(":");
            Identifier id = datumName != null ? new Identifier(authNameWithKey[0], authNameWithKey[1], datumName)
                    : new Identifier(authNameWithKey[0], authNameWithKey[1], Identifiable.UNKNOWN);
            gd = (GeodeticDatum) IdentifiableComponent.getComponent(id);
        }
        // Short circuit identifying NTF datum when the ntf_r93.gsb nadgrids is used
        // Getting a pre-built datum is always better than creating a new one
        if (param.get(ProjKeyParameters.nadgrids) != null &&
                param.get(ProjKeyParameters.nadgrids).contains("ntf_r93.gsb")) {
            if (PrimeMeridian.PARIS.equals(getPrimeMeridian(param))) {
                gd = GeodeticDatum.NTF_PARIS;
            } else if (PrimeMeridian.GREENWICH.equals(getPrimeMeridian(param))) {
                gd = GeodeticDatum.NTF;
            }
        }
        // Create a new GeodeticDatum from its primeMeridian, ellipsoid and toWGS84
        if (gd == null) {
            Ellipsoid ell = getEllipsoid(param);
            PrimeMeridian pm = getPrimeMeridian(param);
            if (null != pm && null != ell) {
                GeocentricTransformation toWGS84 = getToWGS84(param);
                gd = GeodeticDatum.createGeodeticDatum(pm, ell, toWGS84);
            }
        }
        param.remove(ProjKeyParameters.ellps);
        param.remove(ProjKeyParameters.a);
        param.remove(ProjKeyParameters.b);
        param.remove(ProjKeyParameters.rf);
        param.remove(PrjKeyParameters.SPHEROIDREFNAME);
        param.remove(ProjKeyParameters.pm);
        param.remove(ProjKeyParameters.towgs84);
        return gd;
    }

    /**
     * Returns a {@link VerticalDatum} from a map of parameters. Try first to
     * obtain the {@link VerticalDatum} from its name using {@code vertdatum}
     * keyword. Then if {@code param} does not contain {@code vertdatum} keyword
     * or if the name is not recognized, it tries to get the datum from its
     * identifier. Last, it defines a {@link VerticalDatum} from its type.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static VerticalDatum getVerticalDatum(Map<String, String> param) {
        String datumName = param.remove(PrjKeyParameters.VERTDATUM);
        String authCode = param.remove(PrjKeyParameters.VERTDATUMREFNAME);
        String vertType = param.remove(PrjKeyParameters.VERTDATUMTYPE);
        VerticalDatum vd = null;
        Identifier id = new Identifier(VerticalDatum.class);
        if (null != datumName) {
            vd = VerticalDatum.datumFromName.get(datumName.toLowerCase());
            id = new Identifier(VerticalDatum.class, datumName);
        }
        if (vd == null && authCode != null) {
            String[] authNameWithKey = authCode.split(":");
            id = datumName != null ? new Identifier(authNameWithKey[0], authNameWithKey[1], datumName)
                    : new Identifier(authNameWithKey[0], authNameWithKey[1], Identifiable.UNKNOWN);
            vd = (VerticalDatum) IdentifiableComponent.getComponent(id);

        }
        if (vd == null && vertType != null) {
            int type = (int) Double.parseDouble(vertType);
            vd = new VerticalDatum(id, null, "", "", VerticalDatum.getType(type), "", null);
        }
        return vd;
    }

    /**
     * Set nadgrids operation used by the
     * {@link org.cts.crs.CoordinateReferenceSystem}.
     *
     * @param crs the CRS defined by {@code param} we want to associate nadgrids
     * operation with
     * @param param the map of parameters defining the properties of a CRS
     */
    private static void setNadgrids(GeodeticCRS crs, Map<String, String> param) {
        String nadgrids = param.remove(ProjKeyParameters.nadgrids);
        if (nadgrids != null) {
            String[] grids = nadgrids.split(",");
            for (String grid : grids) {
                if (!grid.equals("null")) {
                    LOGGER.warn("A grid has been found.");
                    if (grid.equals("@null")) {
                        crs.getDatum().addGeocentricTransformation(GeodeticDatum.WGS84, Identity.IDENTITY);
                    } else {
                        try {
                            if (grid.equals("ntf_r93.gsb")) {
                                // If this CRS uses the ntf_r93.gsb, we know it is based on NTF, and we can
                                // use FrenchGeocentricNTF2RGF to transform coordinates to WGS or RGF93
                                FrenchGeocentricNTF2RGF ntf2rgf = FrenchGeocentricNTF2RGF.getInstance();
                                crs.getDatum().addGeocentricTransformation(GeodeticDatum.RGF93, ntf2rgf);
                                crs.getDatum().addGeocentricTransformation(GeodeticDatum.WGS84, ntf2rgf);
                                //System.out.println("Add French Geocentric Grid transformation from " + crs.getDatum() + " to RGF93 and WGS84");
                                LOGGER.info("Add French Geocentric Grid transformation from " + crs.getDatum() + " to RGF93 and WGS84");

                                NTv2GridShiftTransformation ntf_r93 = NTv2GridShiftTransformation.createNTv2GridShiftTransformation(grid);
                                ntf_r93.loadGridShiftFile();
                                crs.getDatum().addGeographicTransformation(GeodeticDatum.WGS84,
                                        new CoordinateOperationSequence(ntf_r93.getIdentifier(),
                                                LongitudeRotation.getLongitudeRotationFrom(crs.getDatum().getPrimeMeridian()), ntf_r93));
                                crs.getDatum().addGeographicTransformation(GeodeticDatum.RGF93,
                                        new CoordinateOperationSequence(ntf_r93.getIdentifier(),
                                                LongitudeRotation.getLongitudeRotationFrom(crs.getDatum().getPrimeMeridian()), ntf_r93));
                                //System.out.println("Add NTv2 transformation from " + crs.getDatum() + " to RGF93 and WGS84");
                                LOGGER.info("Add NTv2 transformation from " + crs.getDatum() + " to RGF93 and WGS84");
                            } else {
                                // This is the general case where we want to add a NTv2 transformation
                                // using the file header to determine source and target datums
                                NTv2GridShiftTransformation gt = NTv2GridShiftTransformation.createNTv2GridShiftTransformation(grid);
                                gt.loadGridShiftFile();
                                GeodeticDatum datum = GeodeticDatum.getGeodeticDatum(gt.getToDatum());
                                crs.getDatum().addGeographicTransformation(datum,
                                        new CoordinateOperationSequence(
                                                gt.getIdentifier(),
                                                new LongitudeRotation(crs.getDatum().getPrimeMeridian().getLongitudeFromGreenwichInRadians()),
                                                gt));
                                //System.out.println("Add NTv2 transformation from " + crs.getDatum() + " to " + datum);
                                LOGGER.info("Add NTv2 transformation from " + crs.getDatum() + " to " + datum);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            LOGGER.error("Cannot find the nadgrid " + grid + ".", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a {@link Ellipsoid} from a map of parameters. Try first to obtain
     * the {@link Ellipsoid} from its name using {@code ellps} keyword. Then if
     * {@code param} does not contain {@code ellps} keyword or if the name is
     * not recognized, it tries to obtain it from parameters {@code a} and
     * {@code b} or {@code rf}, last it tries to get the {@link Ellipsoid}
     * associated with a known {@link org.cts.datum.Datum}.
     *
     * @param param the map of parameters defining the properties of a CRS
     */
    private static Ellipsoid getEllipsoid(Map<String, String> param) {
        String ellipsoidName = param.remove(ProjKeyParameters.ellps);
        String a = param.remove(ProjKeyParameters.a);
        String b = param.remove(ProjKeyParameters.b);
        String rf = param.remove(ProjKeyParameters.rf);
        String authorityCode = param.remove(PrjKeyParameters.SPHEROIDREFNAME);
        Ellipsoid ellps = null;

        if (null != ellipsoidName) {
            ellps = Ellipsoid.ellipsoidFromName.get(ellipsoidName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        }
        if (ellps == null && authorityCode != null) {
            String[] authNameWithKey = authorityCode.split(":");
            Identifier id = ellipsoidName != null ? new Identifier(authNameWithKey[0], authNameWithKey[1], ellipsoidName)
                    : new Identifier(authNameWithKey[0], authNameWithKey[1], Identifiable.UNKNOWN);
            ellps = (Ellipsoid) IdentifiableComponent.getComponent(id);
        }
        if (ellps == null && null != a && (null != b || null != rf)) {
            double a_ = Double.parseDouble(a);
            if (null != b) {
                double b_ = Double.parseDouble(b);
                ellps = Ellipsoid.createEllipsoidFromSemiMinorAxis(a_, b_);
            } else {
                double rf_ = Double.parseDouble(rf);
                ellps = Ellipsoid.createEllipsoidFromInverseFlattening(a_, rf_);
            }
        }
        if (ellps == null) {
            LOGGER.warn("Ellipsoid cannot be defined");
        }
        return ellps;
    }

    /**
     * Creates a {@link org.cts.op.projection.Projection} from a projection type
     * (ie lcc, tmerc), an ellipsoid and a map of parameters.
     *
     * @param projectionName name of the projection type
     * @param ell ellipsoid used in the projection
     * @param param the map of parameters defining the properties of a CRS
     */
    private static Projection getProjection(String projectionName, Ellipsoid ell,
            Map<String, String> param) throws CRSException {
        String slat_0 = param.remove("lat_0");
        String slat_1 = param.remove("lat_1");
        String slat_2 = param.remove("lat_2");
        String slat_ts = param.remove("lat_ts");
        String slon_0 = param.remove("lon_0");
        String slonc = param.remove("lonc");
        String salpha = param.remove("alpha");
        String sgamma = param.remove("gamma");
        String sk = param.remove("k");
        String sk_0 = param.remove("k_0");
        String sx_0 = param.remove("x_0");
        String sy_0 = param.remove("y_0");
        double lat_0 = slat_0 != null ? AngleFormat.parseAngle(slat_0) : 0.;
        double lat_1 = slat_1 != null ? AngleFormat.parseAngle(slat_1) : 0.;
        double lat_2 = slat_2 != null ? AngleFormat.parseAngle(slat_2) : 0.;
        double lat_ts = slat_ts != null ? AngleFormat.parseAngle(slat_ts) : 0.;
        double lon_0 = slon_0 != null ? AngleFormat.parseAngle(slon_0) : slonc != null ? AngleFormat.parseAngle(slonc) : 0.;
        double alpha = salpha != null ? AngleFormat.parseAngle(salpha) : 0.;
        double gamma = sgamma != null ? AngleFormat.parseAngle(sgamma) : 0.;
        if (sk != null && sk_0 != null) {
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
        map.put(Parameter.AZIMUTH, new Measure(alpha, Unit.DEGREE));
        map.put(Parameter.RECTIFIED_GRID_ANGLE, new Measure(gamma, Unit.DEGREE));
        map.put(Parameter.SCALE_FACTOR, new Measure(k_0, Unit.UNIT));
        map.put(Parameter.FALSE_EASTING, new Measure(x_0, Unit.METER));
        map.put(Parameter.FALSE_NORTHING, new Measure(y_0, Unit.METER));

        if (projectionName.equalsIgnoreCase(ProjValueParameters.LCC)) {
            if (slat_2 != null) {
                return new LambertConicConformal2SP(ell, map);
            } else {
                return new LambertConicConformal1SP(ell, map);
            }
        } else if (projectionName.equalsIgnoreCase(ProjValueParameters.TMERC)) {
            return new TransverseMercator(ell, map);
        } else if (projectionName.equalsIgnoreCase(ProjValueParameters.UTM)) {
            int zone = param.get("zone") != null ? Integer.parseInt(param.remove("zone")) : 0;
            lon_0 = (6.0 * (zone - 1) + 183.0) % 360.0;
            lon_0 = (((lon_0 + 180) % 360) - 180); // set lon_0 to -180;180 interval
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
            if (alpha == 90 && gamma == 90) {
                return new SwissObliqueMercator(ell, map);
            }
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
            throw new CRSException("Cannot create the projection " + projectionName);
        }
    }
}
