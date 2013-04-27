/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originaled developed by Michael Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michael 
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

import org.cts.*;
import org.cts.cs.GeographicExtent;
import org.cts.op.*;
import org.cts.op.transformation.GeocentricTranslation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
 * @author Michaël Michaud
 */
public class GeodeticDatum extends AbstractDatum {

    private final static Map<Identifier, GeodeticDatum> datums = new HashMap<Identifier, GeodeticDatum>();

    private final PrimeMeridian primeMeridian;

    private final Ellipsoid ellipsoid;

    private CoordinateOperation toWGS84;

    public final static GeodeticDatum WGS84 = new GeodeticDatum(new Identifier(
            "EPSG", "6326", "World Geodetic System 1984", "WGS84"),
            PrimeMeridian.GREENWICH, Ellipsoid.WGS84, GeographicExtent.WORLD,
            null, null);

    public final static GeodeticDatum NTF_PARIS = new GeodeticDatum(
            new Identifier("EPSG", "6807",
            "Nouvelle Triangulation Française (Paris)", "NTF (Paris)"),
            PrimeMeridian.PARIS,
            Ellipsoid.CLARKE1880IGN,
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.52 sec N; Longitude: 2 deg 20 min 48.67 sec E (of Greenwich).",
            "1895");

    public final static GeodeticDatum NTF = new GeodeticDatum(
            new Identifier("EPSG", "6275", "Nouvelle Triangulation Française",
            "NTF"),
            PrimeMeridian.GREENWICH,
            Ellipsoid.CLARKE1880IGN,
            GeographicExtent.WORLD,
            "Fundamental point: Pantheon. Latitude: 48 deg 50 min 46.522 sec N; Longitude: 2 deg 20 min 48.667 sec E (of Greenwich).",
            "1898");

    public final static GeodeticDatum RGF93 = new GeodeticDatum(new Identifier(
            "EPSG", "6171", "Réseau géodésique français 1993", "RGF93"),
            PrimeMeridian.GREENWICH, Ellipsoid.GRS80, GeographicExtent.WORLD,
            "Coincident with ETRS89 at epoch 1993.0", "1993");

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
    }

    /**
     * Returns a collection of all the registered datums.
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
     * Return the PrimeMeridian of this Datum
     */
    public PrimeMeridian getPrimeMeridian() {
        return primeMeridian;
    }

    /**
     * Return the ellipsoid of this Datum
     */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Uses a geocentric transformation between this datum and WGS84 to create a
     * {@link CoordinateOperation} from the {@link org.cts.crs.Geographic3DCRS}
     * based on this Datum to the WGS84 {@link org.cts.crs.Geographic3DCRS}. This
     * CoordinateOperation is usually the Identity transformation, a Translation
     * or a SevenParameterTransformation (ex. Bursa-Wolf). <p> If other
     * transformations are defined between this Datum and WGS84 datum or any
     * other datum, they can be added to the datumTransformations map with the
     * addCoordinateOperation(Datum datum, CoordinateOperation coordOp) method.
     * Operations added to the map are always CoordinateOperations from a
     * Geographic3DCRS to another.
     *
     * @param toWGS84 geocentric transformation from this to geocentric WGS 84
     */
    public void setDefaultToWGS84Operation(CoordinateOperation toWGS84) {
        this.toWGS84 = toWGS84;
        // First case : toWGS (geocentric transformation) is not null
        if (toWGS84 != null && toWGS84 != Identity.IDENTITY) {
            // Add CoordinateOperation from the Georaphic 3D CRS associated with
            // this datum to the one associated with WGS84
            this.addCoordinateOperation(
                GeodeticDatum.WGS84,
                new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName() + " to WGS84"),
                    LongitudeRotation.getLongitudeRotationFrom(primeMeridian),
                    new Geographic2Geocentric(getEllipsoid()),
                    toWGS84,
                    new Geocentric2Geographic(GeodeticDatum.WGS84.getEllipsoid())
                )
            );
            try {
                GeodeticDatum.WGS84.addCoordinateOperation(
                    this,
                    new CoordinateOperationSequence(
                        new Identifier(CoordinateOperation.class, "WGS84 to " + getName()),
                        new Geographic2Geocentric(GeodeticDatum.WGS84.getEllipsoid()),
                        toWGS84.inverse(),
                        new Geocentric2Geographic(getEllipsoid()),
                        LongitudeRotation.getLongitudeRotationTo(primeMeridian)
                    )
                );
            } catch (NonInvertibleOperationException e) {
                // eat it
                // toWGS84 should be Identity, GeocentricTranslation or
                // SevenParameterTransformation which are invertible
                // else, no transformation will be add from WGS84 to this
            }
        } // Second case : geocentric transformation is null but the ellipsoids
        // and the prime meridians are not the same
        else if (toWGS84 == Identity.IDENTITY
                && !primeMeridian.equals(PrimeMeridian.GREENWICH)
                && !ellipsoid.equals(Ellipsoid.WGS84)) {
            this.addCoordinateOperation(GeodeticDatum.WGS84,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName()
                    + " to WGS84"), LongitudeRotation.getLongitudeRotationFrom(primeMeridian),
                    new Geographic2Geocentric(getEllipsoid()),
                    new Geocentric2Geographic(GeodeticDatum.WGS84.getEllipsoid())));
            GeodeticDatum.WGS84.addCoordinateOperation(this,
                    new CoordinateOperationSequence(
                    new Identifier(CoordinateOperation.class, getName()
                    + " to WGS84"), new Geographic2Geocentric(
                    GeodeticDatum.WGS84.getEllipsoid()),
                    new Geocentric2Geographic(getEllipsoid()),
                    LongitudeRotation.getLongitudeRotationTo(primeMeridian)));
        } // Third case : geocentric transformation is null and ellipsoid are
        // the same but prime meridians are not the same
        else if (toWGS84 == Identity.IDENTITY
                && !primeMeridian.equals(PrimeMeridian.GREENWICH)) {
            this.addCoordinateOperation(GeodeticDatum.WGS84, LongitudeRotation.getLongitudeRotationFrom(primeMeridian));
            GeodeticDatum.WGS84.addCoordinateOperation(this, LongitudeRotation.getLongitudeRotationTo(primeMeridian));
        } // Fourth case : this datum and WGS84 are equivalent
        else if (toWGS84 == Identity.IDENTITY) {
            this.addCoordinateOperation(GeodeticDatum.WGS84, Identity.IDENTITY);
            GeodeticDatum.WGS84.addCoordinateOperation(this, Identity.IDENTITY);
        }
    }

    /**
     * Return the ellipsoid of this Datum
     */
    @Override
    public CoordinateOperation getToWGS84() {
        return toWGS84;
    }

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
            boolean toWGS84rs = false;
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
}
