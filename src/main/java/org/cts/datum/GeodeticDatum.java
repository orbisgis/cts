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
package org.cts.datum;

import java.util.*;

import org.cts.Identifiable;
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationSequence;
import org.cts.op.Geocentric2Geographic;
import org.cts.op.Geographic2Geocentric;
import org.cts.op.Identity;
import org.cts.op.LongitudeRotation;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.transformation.*;

/**
 * Geodetic {@link org.cts.datum.Datum} used to determine positions relative
 * to the Earth (longitude / latitude) <p> It is recommended that every
 * <code>GeodeticDatum</code> has a toWGS84
 * {@link org.cts.op.transformation.SevenParameterTransformation} attribute
 * (which may eventually be a
 * {@link org.cts.op.transformation.GeocentricTranslation} or the
 * {@link org.cts.op.Identity} transformation). This
 * operation must be the standard 3D transformation from/to the
 * GeocentricCoordinateSystem defined by this Datum to/from the
 * GeocentricCoordinateSystem defined by WGS84 Datum. <p> Moreover, a
 * GeodeticDatum also contains a map which may contain other
 * {@link org.cts.op.CoordinateOperation}s from the standard Geographic2DCRS or
 * Geographic3DCRS associated with this Datum to the one associated to another
 * Datum.
 *
 * @author Michaël Michaud, Jules Party
 */
public class GeodeticDatum extends AbstractDatum {

    /**
     * knownDatumMap maps datum Identifier or names to {@linkplain Datum datums}.
     */
    private static final Map<Object, GeodeticDatum> knownDatumMap = new HashMap<Object, GeodeticDatum>();
    private final static Set<GeodeticDatum> knownDatum = new HashSet<GeodeticDatum>();

    /**
     * <p>A map of known geocentric to geocentric transformations from this Datum
     * to other {@linkplain Datum datums}.</p>
     * <p>These transformations transform coordinates from the geocentric coordinate system
     * based on this datum (and the Greenwich meridian) to the geocentric coordinate
     * system based on a target datum (and the Greenwich meridian). They typically use
     * Bursa-Wolf equations.</p>
     */
    private final Map<GeodeticDatum, Set<GeocentricTransformation>> geocentricTransformations =
            new HashMap<GeodeticDatum, Set<GeocentricTransformation>>();

    /**
     * <p>A map of known geographic to geographic transformations from this Geodetic Datum
     * to other {@linkplain GeodeticDatum geodetic datums}.</p>
     * <p>These transformations transform coordinates from the geographic coordinate system
     * based on this datum, this prime meridian and this ellipsoid to the geographic
     * system based on a target datum, its prime meridian and its ellipsoid.</p>
     * <p>Geographic transformations include transformations based on parameters like the
     * Molodenski transformations and transformations based on an interpolated grid like
     * the NTv2 method.</p>
     */
    private final Map<GeodeticDatum, Set<CoordinateOperation>> geographicTransformations =
            new HashMap<GeodeticDatum, Set<CoordinateOperation>>();

    /**
     * A map of known vertical transformations from this ellipsoid to other
     * {@linkplain VerticalDatum vertical datum}.
     */
    private final Map<Datum, Set<CoordinateOperation>> heightTransformations =
            new HashMap<Datum, Set<CoordinateOperation>>();

    /**
     * The PrimeMeridian used with this Datum.
     */
    private final PrimeMeridian primeMeridian;

    /**
     * The ellipsoid used with this Datum.
     */
    private final Ellipsoid ellipsoid;

    /**
     * The default geocentric transformation from this datum to WGS84.
     */
    private GeocentricTransformation toWGS84;

    /**
     * World Geodetic System 1984.
     */
    public final static GeodeticDatum WGS84 = new GeodeticDatum(new Identifier(
            "EPSG", "6326", "World Geodetic System 1984", "WGS 84"),
            PrimeMeridian.GREENWICH, Ellipsoid.WGS84, Identity.IDENTITY,
            GeographicExtent.WORLD, null, null);

    /**
     * Nouvelle Triangulation Française (Paris).
     */
    public final static GeodeticDatum NTF_PARIS = new GeodeticDatum(
            new Identifier("EPSG", "6807",
            "Nouvelle Triangulation Française (Paris)", "NTF (Paris)"),
            PrimeMeridian.PARIS, Ellipsoid.CLARKE1880IGN,
            new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0),
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.52 sec N; Longitude: 2 deg 20 min 48.67 sec E (of Greenwich).",
            "1895");

    /**
     * Nouvelle Triangulation Française.
     */
    public final static GeodeticDatum NTF = new GeodeticDatum(
            new Identifier("EPSG", "6275", "Nouvelle Triangulation Française",
            "NTF"),
            PrimeMeridian.GREENWICH, Ellipsoid.CLARKE1880IGN,
            new GeocentricTranslation(-168.0, -60.0, 320.0, 1.0),
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.522 sec N; Longitude: 2 deg 20 min 48.667 sec E (of Greenwich).",
            "1898");

    /**
     * Réseau géodésique français 1993.
     */
    public final static GeodeticDatum RGF93 = new GeodeticDatum(new Identifier(
            "EPSG", "6171", "Réseau géodésique français 1993", "RGF93"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,  Identity.IDENTITY,
            GeographicExtent.WORLD, "Coincident with ETRS89 at epoch 1993.0", "1993");

    /**
     * European Datum 1950.
     */
    public final static GeodeticDatum ED50 = new GeodeticDatum(
            new Identifier("EPSG", "6230", "European Datum 1950", "ED50"),
            PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            new GeocentricTranslation(-84.0, -97.0, -117.0, 1.0),
            GeographicExtent.WORLD,
            "Fundamental point: Potsdam (Helmert Tower). Latitude: 52 deg 22 min 51.4456 sec N; Longitude: 13 deg  3 min 58.9283 sec E (of Greenwich).",
            "1950");

    public final static GeodeticDatum WGS84GUAD = new GeodeticDatum(
            new Identifier(GeodeticDatum.class, "Guadeloupe : WGS84", "WGS84GUAD"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                    1.2239, 2.4156, -1.7598, 0.03800, -0.16101, -0.04925, 0.2387),
            new GeographicExtent("Guadeloupe", 15.875, 16.625, -61.85, -61.075),
            "", "");

    public final static GeodeticDatum WGS84MART = new GeodeticDatum(
            new Identifier(GeodeticDatum.class, "Martinique : WGS84", "WGS84GUAD"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                    0.7696, -0.8692, -12.0631, -0.32511, -0.21041, -0.02390, 0.2829),
            new GeographicExtent("Martinique", 14.25, 15.025, -61.25, -60.725),
            "", "");

    public final static GeodeticDatum WGS84SBSM = new GeodeticDatum(
            new Identifier(GeodeticDatum.class, "St-Martin St-Barth : WGS84", "WGS84SBSM"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(
                    14.6642, 5.2493, 0.1981, -0.06838, 0.09141, -0.58131, -0.4067),
            new GeographicExtent("St-Martin St-Barth", 17.8, 18.2, -63.2, -62.5),
            "", "");


    // Old North American Datum
    // Conversions to NAD83 or WGS84 require NADCON grid-based transformations
    // NADCON grids are not yet implemented in CTS library.
    // (https://www.ngs.noaa.gov/TOOLS/Nadcon/Nadcon.shtml)
    public final static GeodeticDatum NAD27 = new GeodeticDatum(
            new Identifier("EPSG", "6267", "North American Datum 1927", "NAD27"),
            PrimeMeridian.GREENWICH, Ellipsoid.CLARKE1866, null,
            GeographicExtent.WORLD,
            "", "1927");


    // Original NAD83 datum. For topographic purposes.
    // To get pecise transformation from NAD83(1986) to NAD83(HARN) and be
    // consistent with WGS, one must use extended NADCON grids (5-6 cm accuracy)
    // NADCON grids are not yet implemented in CTS library.
    public final static GeodeticDatum NAD83 = new GeodeticDatum(
            new Identifier("EPSG", "6269", "North American Datum 1983", "NAD83"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80, null,
            GeographicExtent.WORLD,
            "", "1983");

    // NAD83(HARN) is a precise geodetic network for USA.
    // Grid-based transformations from NAD83 to NAD83(HARN) use different grids for
    // different states (5-6 cm accuracy)
    // The hereafter toWGS84 transformation from NAD83(HARN) to WGS84 ignores time-dependant
    // parameters.
    public final static GeodeticDatum NAD83_HARN = new GeodeticDatum(
            new Identifier("EPSG", "6152", "North American Datum 1983 (HARN)", "NAD83(HARN)"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(-0.991, 1.9072, 0.5129, 0.125033, 0.046785, 0.056529, -0.00062, 1.0),
            GeographicExtent.WORLD,
            "", null);


    // NAD83(CORS96) is based on the Continuously Operating Reference Station (CORS) network.
    // Discrepancies with NAD83(HARN) are about 6cm
    // The hereafter toWGS84 transformation is for epoch 1997.0 and for WGS84 (G1150)
    // To get timedependant transformations, see :
    // https://support.geocue.com/wp-content/uploads/2015/01/CueTip-Transforming-WGS84-to-NAD83.pdf
    public final static GeodeticDatum NAD83_CORS96 = new GeodeticDatum(
            new Identifier("EPSG", "1133", "North American Datum 1983 (CORS96)", "NAD83(CORS96)"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(-0.9956, 1.9013, 0.5215, 0.025915, 0.009426, 0.0011599, -0.00062, 1.0),
            GeographicExtent.WORLD,
            "", "1997");


    // NAD83(2011) can be assimilated to WGS84 (G1674) for topographic topics
    // For precise geodetic purposes, relation between NAD83(2011) and ITRF 2008 (G1674) is defined
    // by a 14 parameters time dependant transformation and has 0.1 m accuracy.
    // The hereafter toWGS84 transformation is for epoch 1997.0. To get more precise transformation
    // one should use the time-dependant 14 parameters transformation
    public final static GeodeticDatum NAD83_2011 = new GeodeticDatum(
            new Identifier("EPSG", "1116", "North American Datum 1983 (NSRS2011)", "NAD83 (2011)"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            SevenParameterTransformation.createBursaWolfTransformation(-0.99343, 1.90331, 0.52655, 0.02591467, 0.00942645, 0.001159935, -0.00171504, 0.1),
            GeographicExtent.WORLD,
            "", "2012");

    
    public final static GeodeticDatum GGRS87 = new GeodeticDatum(
            new Identifier("EPSG", "4121", "Greek Geodetic Reference System 1987", "GGRS87"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80,null, 
            new GeographicExtent("Greece - onshore", 34.88, 41.75, 19.57,28.3), "","1987" );
    
    public final static GeodeticDatum HERMANNSKOGEL = new GeodeticDatum(
            new Identifier("EPSG", "4312", "Militar-Geographische Institut", "MGI"),
            PrimeMeridian.GREENWICH, Ellipsoid.BESSEL1841, null,
            new GeographicExtent("Austria", 46.4, 49.02, 9.53, 17.17), "","" );
    
    public final static GeodeticDatum POSTDAM = new GeodeticDatum(
            new Identifier("EPSG", "4314", "Deutsches Hauptdreiecksnetz", "DHDN"),
            PrimeMeridian.GREENWICH, Ellipsoid.BESSEL1841,null, 
            new GeographicExtent("Germany - states of former West Germany onshore", 47.27, 55.09, 5.87 , 13.84 ), "","" );
    
    public final static GeodeticDatum CARTHAGE = new GeodeticDatum(
            new Identifier("EPSG", "4223", "Carthage", "Carthage"),
            PrimeMeridian.GREENWICH, Ellipsoid.CLARKE1880IGN, null,
            new GeographicExtent("Tunisia - onshore and offshore", 30.23, 38.41, 7.49, 13.67), "", "");   
    

    public final static GeodeticDatum NZGD49 = new GeodeticDatum(
            new Identifier("EPSG", "4272", "New Zealand Geodetic Datum 1949", "NZGD49"),
            PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924, null,
            new GeographicExtent("New Zealand - North Island, South Island, Stewart Island - onshore and nearshore", 
                    165.87, 179.27, -47.65, -33.89), "", "");  

   
    public final static GeodeticDatum OSGB36 = new GeodeticDatum(
            new Identifier("EPSG", "4277", "OSGB 1936", "OSGB36"),
            PrimeMeridian.GREENWICH, Ellipsoid.AIRY, null,
            new GeographicExtent("United Kingdom (UK) - Great Britain - England and Wales onshore, Scotland onshore and Western Isles nearshore; Isle of Man onshore", 
                    49.79, 60.94, -8.82, 1.92), "", ""); 
    
    public final static GeodeticDatum IRE65 = new GeodeticDatum(
            new Identifier("EPSG", "4299", "TM65", "TM65"),
            PrimeMeridian.GREENWICH, Ellipsoid.AIRYMOD,
            SevenParameterTransformation.createBursaWolfTransformation(482.5, -130.6, 564.6, -1.042, -0.214, -0.631, 8.15, 1),
            new GeographicExtent("Ireland - onshore. United Kingdom (UK) - Northern Ireland (Ulster) - onshore",
                    51.39, 55.43, -10.56, -5.34), "", "");
 
    static {
        //@TODO this should be moved to the parser using these particular names
        knownDatumMap.put("wgs84", WGS84);
        knownDatumMap.put("ntfparis", NTF_PARIS);
        knownDatumMap.put("ntf", NTF);
        knownDatumMap.put("rgf93", RGF93);
        knownDatumMap.put("ed50", ED50);
        knownDatumMap.put("nad27", NAD27);
        knownDatumMap.put("nad83", NAD83);
        knownDatumMap.put("ggrs87", GGRS87);
        knownDatumMap.put("hermannskogel", HERMANNSKOGEL);
        knownDatumMap.put("potsdam", POSTDAM);
        knownDatumMap.put("carthage", CARTHAGE);
        knownDatumMap.put("nzgd49", NZGD49);
        knownDatumMap.put("osgb36",OSGB36);
        knownDatumMap.put("ire65", IRE65);
    }

    /**
     * Creates a new Datum.
     *
     * @param primeMeridian the prime meridian to use with this datum
     * @param ellipsoid the ellipsoid to use with this datum
     * @param toWGS84 the toWGS84 <code>CoordinateOperation</code>
     */
    public static GeodeticDatum createGeodeticDatum(final PrimeMeridian primeMeridian,
                final Ellipsoid ellipsoid, final GeocentricTransformation toWGS84) {
        GeodeticDatum gd = createGeodeticDatum(new Identifier(GeodeticDatum.class),
                primeMeridian, ellipsoid, toWGS84, GeographicExtent.WORLD, "","");
        //gd.setDefaultToWGS84Operation(toWGS84);
        if (knownDatumMap.containsKey(gd)) {
            return knownDatumMap.get(gd);
        }
        else {
            knownDatumMap.put(gd.getIdentifier(), gd);
            knownDatum.add(gd);
            return gd;
        }
    }

    /**
     * Creates a new Datum.
     *
     * @param identifier identifier.
     * @param primeMeridian the prime meridian to use with this datum
     * @param ellipsoid the ellipsoid to use with this datum
     * @param extent this datum extension
     * @param origin origin decription this datum
     * @param epoch realization epoch of this datum
     */
    private GeodeticDatum(
            final Identifier identifier,
            final PrimeMeridian primeMeridian,
            final Ellipsoid ellipsoid,
            final GeocentricTransformation toWGS84,
            final GeographicExtent extent, final String origin, final String epoch) {
        super(identifier, extent, origin, epoch);
        this.ellipsoid = ellipsoid;
        this.primeMeridian = primeMeridian;
        this.toWGS84 = toWGS84;
        setDefaultToWGS84Operation(toWGS84);
        knownDatumMap.put(this.getIdentifier(), this);
        knownDatum.add(this);
    }

    /**
     * Creates a new Datum or return a known datum if it already exists.
     *
     * @param identifier identifier.
     * @param primeMeridian the prime meridian to use with this datum
     * @param ellipsoid the ellipsoid to use with this datum
     * @param extent this datum extension
     * @param origin origin decription this datum
     * @param epoch realization epoch of this datum
     */
    public static GeodeticDatum createGeodeticDatum(
            final Identifier identifier,
            final PrimeMeridian primeMeridian, final Ellipsoid ellipsoid,
            final GeocentricTransformation toWGS84,
            final GeographicExtent extent, final String origin, final String epoch) {
        if (knownDatumMap.containsKey(identifier)) return knownDatumMap.get(identifier);
        else if (knownDatumMap.containsKey(identifier.getCode())) return knownDatumMap.get(identifier.getCode());
        else if (knownDatumMap.containsKey(identifier.getName())) return knownDatumMap.get(identifier.getName());
        else {
            return new GeodeticDatum(identifier, primeMeridian, ellipsoid, toWGS84, extent, origin, epoch);
        }
    }

    /**
     * Return the PrimeMeridian of this Datum.
     */
    public PrimeMeridian getPrimeMeridian() {
        return primeMeridian;
    }

    /**
     * Return the ellipsoid of this Datum.
     */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Sets the default transformation to WGS84 in two forms :
     * <p><b>toWGS84 Geocentric transformation</b></p>
     * <p>toWGS84 is an operation to transform geocentric coordinates based on
     * this datum to geocentric coordinates based on WGS84 datum, generally a
     * translation or a SevenParameterTransformation (ex. Bursa-Wolf).</p>
     * <p>toWGS84 does not use PrimeMerdian nor ellipsoid parameters.</p>
     * <p><b>datumTransformations map (direct Geographic3D
     * transformations)</b></p>
     * <p>The toWGS84 transformation is also stored in the datumTransformations
     * map, inherited from AbstractDatum, but this time, the operation is not
     * stored as Geocentric to Geocentric transformation but as a Geographic3D
     * to Geographic3D transformation.</p>
     * <p>The convention for this transformation is to start from Geographic3D
     * coordinates in radians, to include required longitude rotation, and
     * ellipsoid transformations, and to return GeographicCoordinates in radian.
     * Advantage is that it makes it possible to use algorithm which do not
     * involve Geographic to Geocentric transformation like the use of NTv2
     * grids.</p>
     *
     * @param toWGS84 geocentric transformation from this to geocentric WGS 84
     */
    public final void setDefaultToWGS84Operation(GeocentricTransformation toWGS84) {
        this.toWGS84 = toWGS84;
        if (toWGS84 != null && WGS84 != null) this.addGeocentricTransformation(WGS84, toWGS84, true);
    }

    public Set<GeodeticDatum> getTargetDatum() {
        return geographicTransformations.keySet();
    }


    /**
     * Adds a Geocentric Transformation from this datum to targetDatum.
     * When a geocentric transformation is added to a geodetic datum, the
     * corresponding geographic transformation is automatically built and added
     * to geographicTransformation map.
     *
     * @param targetDatum the target datum of the transformation to add
     * @param coordOp the transformation linking this Datum and the target
     * <code>datum</code>
     */
    public void addGeocentricTransformation(GeodeticDatum targetDatum, GeocentricTransformation coordOp) {
        addGeocentricTransformation(targetDatum, coordOp, true);
    }

    private void addGeocentricTransformation(GeodeticDatum targetDatum,
                GeocentricTransformation coordOp, boolean addInverseOp) {
        // Add a transformation operation from this to datum
        boolean added = false;
        if (geocentricTransformations.get(targetDatum) == null) {
            geocentricTransformations.put(targetDatum, new HashSet<GeocentricTransformation>());
        }
        added = geocentricTransformations.get(targetDatum).add(coordOp);

        // 2015-02-07 : if we already have added this coordOp for targetDatum,
        // we want to prevent adding derived geographicTransformation a second time,
        // but we don't want to prevent adding the inverse transformation as
        // targetDatum may be another datum equals but not == (ex. RGF93 vs ETRS89)

        // Add the inverse transformation operation from datum to this
        try {
            if (addInverseOp) {
                targetDatum.addGeocentricTransformation(this, coordOp.inverse(), false);
            }
        } catch (NonInvertibleOperationException e) {
            e.printStackTrace();
        }

        if (!added) return;

        // Add the coordinate operation sequence from this geographic coordinate system to
        // the target datum geographic crs based on the geocentric coordOp.
        if (geographicTransformations.get(targetDatum) == null) {
            geographicTransformations.put(targetDatum, new HashSet<CoordinateOperation>());
        }
        CoordinateOperationSequence cos = null;
        // datum change with only prime meridian change
        if (coordOp.isIdentity() && this.getEllipsoid().equals(targetDatum.getEllipsoid())) {
            cos = new CoordinateOperationSequence(
                    new Identifier(CoordinateOperationSequence.class,
                            "Geographic Transformation from " + this.getShortName() + " to " + targetDatum.getShortName()),
                    new LongitudeRotation(this.primeMeridian.getLongitudeFromGreenwichInRadians()),
                    new LongitudeRotation(targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians()).inverse());
        }
        else {
            cos = new CoordinateOperationSequence(
                    new Identifier(CoordinateOperationSequence.class,
                            "Geographic Transformation from " + this.getShortName() + " to " + targetDatum.getShortName()),
                    new LongitudeRotation(this.primeMeridian.getLongitudeFromGreenwichInRadians()),
                    new Geographic2Geocentric(ellipsoid),
                    coordOp,
                    new Geocentric2Geographic(targetDatum.getEllipsoid()),
                    new LongitudeRotation(targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians()).inverse());
        }
        geographicTransformations.get(targetDatum).add(cos);
        // Inverse geographic operation is added through previous instruction
    }

    /**
     * Add a Geographic Transformation to another GeodeticDatum.
     * This transformation transforms geographic coordinates of the source geodetic datum
     * (in radians, and based on the source prime meridian) to the target geographic datum
     * (in radian and based on the target prime meridian)
     *
     * @param targetDatum the target geodetic datum of the transformation to add
     * @param coordOp the geographic to geographic coordinate transformation linking this Datum
     *                and the target <code>datum</code>
     */
    public void addGeographicTransformation(GeodeticDatum targetDatum, CoordinateOperation coordOp) {
        addGeographicTransformation(targetDatum, coordOp, true);
    }

    private void addGeographicTransformation(GeodeticDatum targetDatum, CoordinateOperation coordOp, boolean addInverseOp) {

        if (geographicTransformations.get(targetDatum) == null) {
            geographicTransformations.put(targetDatum, new HashSet<CoordinateOperation>());
        } else if (geographicTransformations.get(targetDatum).contains(coordOp)) return;
        geographicTransformations.get(targetDatum).add(coordOp);
        if (addInverseOp) {
            try {
                targetDatum.addGeographicTransformation(this, coordOp.inverse(), false);
            } catch (NonInvertibleOperationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get available geocentric transformations to another datum.
     *
     * @param targetDatum the datum that must be a target for returned transformation
     * @return 
     */
    public Set<GeocentricTransformation> getGeocentricTransformations(GeodeticDatum targetDatum) {

        // Create a new empty set of operations towards targetDatum if it does not already exist
        if (geocentricTransformations.get(targetDatum) == null) {
            geocentricTransformations.put(targetDatum, new HashSet<GeocentricTransformation>());
        }

        // If targetDatum is equivalent to WGS84, this.toWGS84 is a possible CoordinateOperation
        if (targetDatum.equals(GeodeticDatum.WGS84) && getToWGS84() != null) {
            addGeocentricTransformation(targetDatum, getToWGS84(), true);
        }
        // If this is equivalent to WGS84, we can use targetDatum.toWGS84.inverse CoordinateOperation
        else if (this.equals(GeodeticDatum.WGS84) && targetDatum.getToWGS84() != null) {
            try {
                addGeocentricTransformation(targetDatum, targetDatum.getToWGS84().inverse(), true);
            } catch(NonInvertibleOperationException e) {
                // if datum.getToWGS84() is not invertible, just ignore it
            }
        }
        // Neither this nor targetDatum are equal to WGS84
        else if (!targetDatum.equals(WGS84) && !this.equals(WGS84)) {
            // We have transformations from each datum to WGS84. Use WGS84 as a pivot
            if (!getGeocentricTransformations(GeodeticDatum.WGS84).isEmpty() &&
                    !targetDatum.getGeocentricTransformations(GeodeticDatum.WGS84).isEmpty()) {
                try {
                    for (GeocentricTransformation op1 : getGeocentricTransformations(WGS84)) {
                        for (GeocentricTransformation op2 : targetDatum.getGeocentricTransformations(WGS84)) {
                            if (op1.equals(op2) || (op1.isIdentity() && op2.isIdentity())) {
                                addGeocentricTransformation(targetDatum, Identity.IDENTITY, true);
                            } else {
                                if (op1.isIdentity()) {
                                    addGeocentricTransformation(targetDatum, op2.inverse(), true);
                                }
                                else if (op2.isIdentity()) {
                                    addGeocentricTransformation(targetDatum, op1, true);
                                }
                                else {
                                    addGeocentricTransformation(targetDatum, new GeocentricTransformationSequence(
                                            new Identifier(CoordinateOperation.class), op1, op2.inverse()), true);
                                }
                            }
                        }
                    }
                } catch (NonInvertibleOperationException e) {
                    // The geocentric transformation should always be inversible.
                    // Moreover, add the transformation to the target datum is useful
                    // for further calulation but not essential, so if the inversion
                    // fails it has no importance
                }
            }
        }
        //}
        return geocentricTransformations.get(targetDatum);
    }

    /**
     * <p>Get available  geographic transformations to another datum.</p>
     * <p>The method first call getGeocentricTransformations which will try to
     * build geocentric transformations from this datum to target datum using WGS84
     * pivot if no direct transformation is available. Building a new geocentric
     * transformation to target datum will automatically build the corresponding
     * geographic transformation and put it in the geographicTransformations
     * map</p>
     *
     * @param targetDatum the datum that must be a target for returned transformation
     * @return available geographic transformations to targetDatum or an empty List if no
     * transformation is available.
     */
    public Set<CoordinateOperation> getGeographicTransformations(GeodeticDatum targetDatum) {
        // Calling getGeocentricTransformations will build new transformations
        // using WGS84 pivot if needed and it will add associated geographic transformations
        getGeocentricTransformations(targetDatum);
        Set<CoordinateOperation> ops = geographicTransformations.get(targetDatum);
        if (ops == null) {
            geographicTransformations.put(targetDatum, new HashSet<CoordinateOperation>());
        }

        // We have transformations from each datum to WGS84. Use WGS84 as a pivot
        if (!this.equals(WGS84) && ! targetDatum.equals(WGS84) &&
                !getGeographicTransformations(GeodeticDatum.WGS84).isEmpty() &&
                !targetDatum.getGeographicTransformations(GeodeticDatum.WGS84).isEmpty()) {
            try {
                for (CoordinateOperation op1 : getGeographicTransformations(WGS84)) {
                    for (CoordinateOperation op2 : targetDatum.getGeographicTransformations(WGS84)) {
                        if (op1.equals(op2) || (op1.isIdentity() && op2.isIdentity())) {
                            addGeographicTransformation(targetDatum, Identity.IDENTITY, true);
                        } else {
                            addGeographicTransformation(targetDatum, new GeocentricTransformationSequence(
                                    new Identifier(CoordinateOperation.class), op1, op2.inverse()), true);
                        }
                    }
                }
            } catch (NonInvertibleOperationException e) {
                // The geocentric transformation should always be inversible.
                // Moreover, add the transformation to the target datum is useful
                // for further calulation but not essential, so if the inversion
                // fails it has no importance
            }
        }
        return geographicTransformations.get(targetDatum);
    }

    /**
     * Get available  geographic transformations to another datum.
     * Transformations are ordered from the least precise to the most precise one.
     *
     * @param datum the datum that must be a target for returned transformation
     * @return 
     */
    public Set<CoordinateOperation> getHeightTransformations(Datum datum) {
        return heightTransformations.get(datum);
    }

    /**
     * Returns the default transformation to WGS84 of this Datum.
     * @return 
     */
    @Override
    public GeocentricTransformation getToWGS84() {
        return toWGS84;
    }

    public static GeodeticDatum getGeodeticDatum(Object id) {
        return knownDatumMap.get(id);
    }


    /**
     * Returns a WKT representation of the geodetic datum.
     *
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("DATUM[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getEllipsoid().toWKT());
        CoordinateOperation towgs84 = this.getToWGS84();
        if ((towgs84 != null) && (towgs84 instanceof GeoTransformation)) {
            GeoTransformation geoTransformation = (GeoTransformation) towgs84;
            w.append(geoTransformation.toWKT());
        } else if (towgs84 instanceof Identity) {
            w.append(",TOWGS84[0,0,0,0,0,0,0]");
        }
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier().toWKT());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Returns a String representation of this GeodeticDatum.
     */
    @Override
    public String toString() {
        return getIdentifier().toString();
    }

    /**
     * Returns true if o equals <code>this</code>.
     * o equals this if they refer to the same memory object or if they have
     * the same identifier.
     *
     * @param o The object to compare this GeodeticDatum against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GeodeticDatum) {
            GeodeticDatum gd = (GeodeticDatum) o;
            if (getIdentifier().equals(gd.getIdentifier())) {
                return true;
            }
            boolean toWGS84Equality, extentEquality;
            if (getToWGS84() == null || gd.toWGS84 == null) {
                // If toWGS84 has not been defined, don't
                // consider a datum equals to any other
                toWGS84Equality = false;
            } else {
                toWGS84Equality = getToWGS84().equals(gd.getToWGS84()) ||
                        (getToWGS84().isIdentity() && gd.getToWGS84().isIdentity());
            }
            extentEquality = getExtent() == null ? gd.getExtent() == null : getExtent().equals(gd.getExtent());
            return ellipsoid.equals(gd.getEllipsoid())
                    && primeMeridian.equals(gd.getPrimeMeridian())
                    && toWGS84Equality
                    && extentEquality;
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code for this GeodeticDatum.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.primeMeridian != null ? this.primeMeridian.hashCode() : 0);
        hash = 83 * hash + (this.ellipsoid != null ? this.ellipsoid.hashCode() : 0);
        hash = 83 * hash + (this.toWGS84 != null ? this.toWGS84.hashCode() : 0);
        return hash;
    }

    /**
     * Removes all transformation from this datum to others and the other way.
     */
    public void removeAllTransformations() {
        for (GeodeticDatum gd : geocentricTransformations.keySet()) {
            gd.geocentricTransformations.remove(this);
        }
        for (GeodeticDatum gd : geographicTransformations.keySet()) {
            gd.geographicTransformations.remove(this);
        }
        geocentricTransformations.clear();
        geographicTransformations.clear();
    }
}
