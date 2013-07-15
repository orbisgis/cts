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
package org.cts.datum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cts.*;
import org.cts.cs.GeographicExtent;
import org.cts.op.*;
import org.cts.op.transformation.GeocentricTranslation;

/**
 * Geodetic Datum or horizontal Datum : a {@link org.cts.datum.Datum} used to
 * determine positions relative to the Earth (longitude / latitude) <p> It is
 * recommended that every
 * <code>GeodeticDatum</code> has a toWGS84
 * {@link org.cts.op.transformation.SevenParameterTransformation} attribute
 * (which may eventually be a Translation or the Identity transformation). This
 * operation must be the standard 3D transformation from/to the
 * GeocentricCoordinateSystem defined by this Datum to/from the
 * GeocentricCoordinateSystem defined by WGS84 Datum. <p> Moreover, a
 * GeodeticDatum also contains a map which may contain other
 * {@link org.cts.CoordinateOperation}s from the standard Geographic2DCRS or
 * Geographic3DCRS associated with this Datum to the one associated to another
 * Datum.
 *
 * @author Michaël Michaud, Jules Party
 */
public class GeodeticDatum extends AbstractDatum {

    /**
     * datums is a {@link HashMap} that registered all GeodeticDatums using
     * their identifiers as key.
     */
    private final static Map<Identifier, GeodeticDatum> datums = new HashMap<Identifier, GeodeticDatum>();
    /**
     * datumFromName associates each datum to a short string used to recognize
     * it in CTS.
     */
    public static final Map<String, GeodeticDatum> datumFromName = new HashMap<String, GeodeticDatum>();
    /**
     * The PrimeMeridian of this Datum.
     */
    private final PrimeMeridian primeMeridian;
    /**
     * The ellipsoid of this Datum.
     */
    private final Ellipsoid ellipsoid;
    /**
     * The default transformation to WGS84 of this Datum.
     */
    private CoordinateOperation toWGS84;
    /**
     * World Geodetic System 1984.
     */
    public final static GeodeticDatum WGS84 = new GeodeticDatum(new Identifier(
            "EPSG", "6326", "World Geodetic System 1984", "WGS84"),
            PrimeMeridian.GREENWICH, Ellipsoid.WGS84, GeographicExtent.WORLD,
            null, null);
    /**
     * Nouvelle Triangulation Française (Paris).
     */
    public final static GeodeticDatum NTF_PARIS = new GeodeticDatum(
            new Identifier("EPSG", "6807",
            "Nouvelle Triangulation Française (Paris)", "NTF (Paris)"),
            PrimeMeridian.PARIS,
            Ellipsoid.CLARKE1880IGN,
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.52 sec N; Longitude: 2 deg 20 min 48.67 sec E (of Greenwich).",
            "1895");
    /**
     * Nouvelle Triangulation Française.
     */
    public final static GeodeticDatum NTF = new GeodeticDatum(
            new Identifier("EPSG", "6275", "Nouvelle Triangulation Française",
            "NTF"),
            PrimeMeridian.GREENWICH,
            Ellipsoid.CLARKE1880IGN,
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.522 sec N; Longitude: 2 deg 20 min 48.667 sec E (of Greenwich).",
            "1898");
    /**
     * Réseau géodésique français 1993.
     */
    public final static GeodeticDatum RGF93 = new GeodeticDatum(new Identifier(
            "EPSG", "6171", "Réseau géodésique français 1993", "RGF93"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80, GeographicExtent.WORLD,
            "Coincident with ETRS89 at epoch 1993.0", "1993");
    /**
     * European Datum 1950.
     */
    public final static GeodeticDatum ED50 = new GeodeticDatum(
            new Identifier("EPSG", "6230", "European Datum 1950", "ED50"),
            PrimeMeridian.GREENWICH,
            Ellipsoid.INTERNATIONAL1924,
            GeographicExtent.WORLD,
            "Fundamental point: Potsdam (Helmert Tower). Latitude: 52 deg 22 min 51.4456 sec N; Longitude: 13 deg  3 min 58.9283 sec E (of Greenwich).",
            "1950");

    static {
        WGS84.setDefaultToWGS84Operation(Identity.IDENTITY);
        RGF93.setDefaultToWGS84Operation(Identity.IDENTITY);
        NTF.setDefaultToWGS84Operation(new GeocentricTranslation(-168.0, -60.0,
                320.0, 1.0));
        NTF_PARIS.setDefaultToWGS84Operation(new GeocentricTranslation(-168.0,
                -60.0, 320.0, 1.0));
        ED50.setDefaultToWGS84Operation(new GeocentricTranslation(-84.0, -97.0,
                -117.0, 1.0));

        datumFromName.put("wgs84", WGS84);
        datumFromName.put("ntfparis", NTF_PARIS);
        datumFromName.put("ntf", NTF);
        datumFromName.put("rgf93", RGF93);
        datumFromName.put("ed50", ED50);
    }

    /**
     * Creates a new Datum.
     *
     * @param primeMeridian the prime meridian to use with this datum
     * @param ellipsoid the ellipsoid to use with this datum
     */
    public GeodeticDatum(final PrimeMeridian primeMeridian,
            final Ellipsoid ellipsoid) {
        this(new Identifier(GeodeticDatum.class, Identifiable.UNKNOWN),
                primeMeridian, ellipsoid, GeographicExtent.WORLD, null, null);
    }

    /**
     * Creates a new Datum.
     *
     * @param primeMeridian the prime meridian to use with this datum
     * @param ellipsoid the ellipsoid to use with this datum
     */
    public GeodeticDatum(final PrimeMeridian primeMeridian,
            final Ellipsoid ellipsoid, final CoordinateOperation toWGS84) {
        this(new Identifier(GeodeticDatum.class, Identifiable.UNKNOWN),
                primeMeridian, ellipsoid, GeographicExtent.WORLD, null, null);
        this.setDefaultToWGS84Operation(toWGS84);
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
    public GeodeticDatum(final Identifier identifier,
            final PrimeMeridian primeMeridian, final Ellipsoid ellipsoid,
            final GeographicExtent extent, final String origin,
            final String epoch) {
        super(identifier, extent, origin, epoch);
        this.ellipsoid = ellipsoid;
        this.primeMeridian = primeMeridian;
        this.registerDatum();
    }

    /**
     * Register a datum in {@link HashMap} {@code datums} using its
     * {@link Identifier} as a key.
     */
    private void registerDatum() {
        datums.put(getIdentifier(), this);
    }

    /**
     * Returns a collection of all the registered geodetic datums.
     */
    public static Collection<GeodeticDatum> getAvailableDatums() {
        return datums.values();
    }

    /**
     * Returns the Datum from its idEPSG identifier.
     *
     * @param idEPSG the EPSG identifier of the datum
     */
    public static GeodeticDatum getDatum(Identifier idEPSG) {
        return datums.get(idEPSG);
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
     * Set the default transformation to WGS84 in two forms :
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
    public final void setDefaultToWGS84Operation(CoordinateOperation toWGS84) {
        this.toWGS84 = toWGS84;
        this.setToOtherDatumOperation(toWGS84, WGS84);
    }

    /**
     * Set a transformation to a target Datum.
     * <p>toOtherDatum is an operation to transform geocentric coordinates based
     * on this datum to geocentric coordinates based on target datum, generally
     * a translation or a SevenParameterTransformation (ex. Bursa-Wolf).</p>
     * <p>toOtherDatum does not use PrimeMerdian nor ellipsoid parameters.</p>
     * <p>The toOtherDatum transformation is stored in the datumTransformations
     * map, inherited from AbstractDatum. The operation is not stored as
     * Geocentric to Geocentric transformation but as a Geographic3D to
     * Geographic3D transformation.</p>
     * <p>The convention for this transformation is to start from Geographic3D
     * coordinates in radians, to include required longitude rotation, and
     * ellipsoid transformations, and to return GeographicCoordinates in radian.
     * Advantage is that it makes it possible to use algorithm which do not
     * involve Geographic to Geocentric transformation like the use of NTv2
     * grids.</p>
     *
     * @param toOtherDatum geocentric transformation from this to targetDatum
     * @param targetDatum the GeodeticDatum to which the transformation is
     * defined
     */
    public final void setToOtherDatumOperation(CoordinateOperation toOtherDatum, GeodeticDatum targetDatum) {
        // First case : toWGS (geocentric transformation) is not null
        if (toOtherDatum != null && toOtherDatum != Identity.IDENTITY) {
            // Add CoordinateOperation from the Geographic 3D CRS associated with
            // this datum to the one associated with WGS84
            this.addCoordinateOperation(
                    targetDatum,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to " + targetDatum.getName()),
                    new LongitudeRotation(primeMeridian.getLongitudeFromGreenwichInRadians()),
                    new Geographic2Geocentric(getEllipsoid()),
                    toOtherDatum,
                    new Geocentric2Geographic(targetDatum.getEllipsoid()),
                    new LongitudeRotation(-targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians())));
            try {
                targetDatum.addCoordinateOperation(
                        this,
                        new CoordinateOperationSequence(
                        new Identifier(CoordinateOperation.class, targetDatum.getName() + " to " + getName()),
                        new LongitudeRotation(targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians()),
                        new Geographic2Geocentric(targetDatum.getEllipsoid()),
                        toOtherDatum.inverse(),
                        new Geocentric2Geographic(getEllipsoid()),
                        new LongitudeRotation(-primeMeridian.getLongitudeFromGreenwichInRadians())));
            } catch (NonInvertibleOperationException e) {
                // eat it
                // toWGS84 should be Identity, GeocentricTranslation or
                // SevenParameterTransformation which are invertible
                // else, no transformation will be add from WGS84 to this
            }
        } // Second case : geocentric transformation is null but the ellipsoids
        // and the prime meridians are not the same
        else if (toOtherDatum == Identity.IDENTITY
                && !primeMeridian.equals(targetDatum.getPrimeMeridian())
                && !ellipsoid.equals(targetDatum.getEllipsoid())) {
            this.addCoordinateOperation(targetDatum,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to " + targetDatum.getName()),
                    new LongitudeRotation(primeMeridian.getLongitudeFromGreenwichInRadians()),
                    new Geographic2Geocentric(getEllipsoid()),
                    new Geocentric2Geographic(targetDatum.getEllipsoid()),
                    new LongitudeRotation(-targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians())));
            targetDatum.addCoordinateOperation(this,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to " + targetDatum.getName()),
                    new LongitudeRotation(targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians()),
                    new Geographic2Geocentric(targetDatum.getEllipsoid()),
                    new Geocentric2Geographic(getEllipsoid()),
                    new LongitudeRotation(-primeMeridian.getLongitudeFromGreenwichInRadians())));
        } // Third case : geocentric transformation is null and ellipsoid are
        // the same but prime meridians are not the same
        else if (toOtherDatum == Identity.IDENTITY
                && !primeMeridian.equals(targetDatum.getPrimeMeridian())) {
            this.addCoordinateOperation(targetDatum, new LongitudeRotation(primeMeridian.getLongitudeFromGreenwichInRadians() - targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians()));
            targetDatum.addCoordinateOperation(this, new LongitudeRotation(targetDatum.getPrimeMeridian().getLongitudeFromGreenwichInRadians() - primeMeridian.getLongitudeFromGreenwichInRadians()));
        } // Fourth case : geocentric transformation is null and prime meridians are 
        // the same but ellipsoids are not the same
        else if (toOtherDatum == Identity.IDENTITY
                && !ellipsoid.equals(targetDatum.getEllipsoid())) {
            this.addCoordinateOperation(targetDatum,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to " + targetDatum.getName()),
                    new Geographic2Geocentric(getEllipsoid()),
                    new Geocentric2Geographic(targetDatum.getEllipsoid())));
            targetDatum.addCoordinateOperation(this,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to " + targetDatum.getName()),
                    new Geographic2Geocentric(targetDatum.getEllipsoid()),
                    new Geocentric2Geographic(getEllipsoid())));
        }
        // Fifth case : this datum and WGS84 are equivalent
        else if (toOtherDatum == Identity.IDENTITY) {
            this.addCoordinateOperation(targetDatum, Identity.IDENTITY);
            targetDatum.addCoordinateOperation(this, Identity.IDENTITY);
        }
    }

    /**
     * Returns the default transformation to WGS84 of this Datum.
     */
    @Override
    public CoordinateOperation getToWGS84() {
        return toWGS84;
    }

    /**
     * If the GeodeticDatum is equal to one of the wellknown GeodeticDatum
     * (WGS84, RGF93, NTF, NTF_PARIS and ED50), the method return this wellknown
     * GeodeticDatum. If there is no such datum, the method return the
     * GeodeticDatum to which the method is applied.
     */
    public GeodeticDatum checkExistingGeodeticDatum() {
        if (this.equals(WGS84)) {
            return WGS84;
        } else if (this.equals(RGF93)) {
            return RGF93;
        } else if (this.equals(NTF)) {
            return NTF;
        } else if (this.equals(NTF_PARIS)) {
            return NTF_PARIS;
        } else if (this.equals(ED50)) {
            return ED50;
        } else {
            return this;
        }
    }

    /**
     * Returns true if object is equals to
     * <code>this</code>. Tests equality between identifiers, then tests if the
     * components of this ProjectedCRS are equals : the toWGS84 transformations,
     * the {@link Ellipsoid} and the {@link PrimeMeridian}.
     *
     * @param object The object to compare this GeodeticDatum against
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
            boolean toWGS84rs;
            if (getToWGS84() == null) {
                if (gd.getToWGS84() == null) {
                    toWGS84rs = true;
                } else {
                    toWGS84rs = false;
                }
            } else {
                toWGS84rs = getToWGS84().equals(gd.getToWGS84());
            }

            return ellipsoid.equals(gd.getEllipsoid())
                    && primeMeridian.equals(gd.getPrimeMeridian()) && toWGS84rs;
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
}
