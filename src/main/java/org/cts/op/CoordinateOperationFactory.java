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
package org.cts.op;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.cts.Identifier;
import org.cts.crs.GeodeticCRS;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.op.transformation.GeocentricTransformation;
import org.cts.op.transformation.GeocentricTransformationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CoordinateOperationFactory is a factory used to create
 * {@linkplain  org.cts.op.CoordinateOperation CoordinateOperations} from source
 * and target
 * {@linkplain org.cts.crs.CoordinateReferenceSystem CoordinateReferenceSystems}.
 *
 * @author Michaël Michaud, Jules Party
 */
public final class CoordinateOperationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CoordinateOperationFactory.class);

    /**
     * Creates a list of {@link CoordinateOperation}s from a source
     * {@link org.cts.crs.GeodeticCRS} to a target
     * {@link org.cts.crs.GeodeticCRS}.
     * {@link org.cts.crs.GeodeticCRS}s include {@link org.cts.crs.GeocentricCRS}s,
     * {@link org.cts.crs.Geographic2DCRS}, {@link org.cts.crs.Geographic3DCRS} and
     * {@link org.cts.crs.ProjectedCRS}.
     *
     * @param source the (non null) source geodetic coordinate reference system
     * @param target the (non null) target geodetic coordinate reference system
     */
    public static Set<CoordinateOperation> createCoordinateOperations(
            GeodeticCRS source, GeodeticCRS target) throws CoordinateOperationException {
        if (source == null) {
            throw new IllegalArgumentException("The source CRS must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("The target CRS must not be null");
        }
        Set<CoordinateOperation> opList = new HashSet<CoordinateOperation>();
        GeodeticDatum sourceDatum = source.getDatum();
        if (sourceDatum == null) {
            LOG.warn(source.getName() + " has no Geodetic Datum");
            throw new IllegalArgumentException("The source datum must not be null");
        }
        //extendGeodeticDatumTransformationSet(sourceDatum);

        GeodeticDatum targetDatum = target.getDatum();
        if (targetDatum == null) {
            LOG.warn(target.getName() + " has no Geodetic Datum");
            throw new IllegalArgumentException("The target datum must not be null");
        }

        if (sourceDatum.equals(targetDatum)) {
            addCoordinateOperations(source, target, opList);
        } else {
            //extendGeodeticDatumTransformationSet(targetDatum);
            addCoordinateOperations(sourceDatum, source, targetDatum, target, opList);
        }
        if (opList.isEmpty()) {
            LOG.warn("No transformation found from " + source.getCode() + " to " + target.getCode());
            throw new IllegalArgumentException("No transformation found from " + source.getCode() + " to " + target.getCode());
        }
        return opList;
    }


    /**
     * Adds a CoordinateOperation to the list of CoordinateOperations usable to transform
     * coordinates from source CRS to target CRS.
     * parameter. This CoordinateOperation links a source
     * {@link org.cts.crs.GeodeticCRS} to a target {@link org.cts.crs.GeodeticCRS} using the
     * same {@link org.cts.datum.GeodeticDatum}.
     * Remember that {@link org.cts.crs.GeodeticCRS}s include {@link org.cts.crs.GeocentricCRS}s,
     * {@link org.cts.crs.Geographic2DCRS}s, {@link org.cts.crs.Geographic3DCRS}s and
     * {@link org.cts.crs.ProjectedCRS}s.
     *
     * @param source the source geodetic coordinate reference system
     * @param target the target geodetic coordinate reference system
     * @param opList the list in which the CoordinateOperation must be added
     */
    private static void addCoordinateOperations(
            GeodeticCRS source, GeodeticCRS target,
            Set<CoordinateOperation> opList) throws CoordinateOperationException {
        try {
            opList.add(new CoordinateOperationSequence(
                    new Identifier(CoordinateOperationSequence.class, source.getCode() + " to " + target.getCode()),
                    source.toGeographicCoordinateConverter(),
                    target.fromGeographicCoordinateConverter()));
        } catch (NonInvertibleOperationException e) {
            LOG.warn("Operation from " + source.getCode() + " to " + target.getCode() + " could not be created");
            LOG.error("CoordinateOperationFactory", e);
        }
    }

    /**
     * Adds a CoordinateOperation to the set of CoordinateOperations usable to transform
     * coordinates from source CRS to target CRS.
     * This CoordinateOperation links a source {@link org.cts.crs.GeodeticCRS}
     * to a target {@link org.cts.crs.GeodeticCRS} which may use different {@link org.cts.datum.Datum}s.
     * Remember that {@link org.cts.crs.GeodeticCRS}s include {@link org.cts.crs.GeocentricCRS}s,
     * {@link org.cts.crs.Geographic2DCRS}s, {@link org.cts.crs.Geographic3DCRS}s and
     * {@link org.cts.crs.ProjectedCRS}s.
     *
     * @param sourceDatum the (non null) datum used by source CRS
     * @param source the source geodetic coordinate reference system
     * @param targetDatum the (non null) datum used by target CRS
     * @param target the target geodetic coordinate reference system
     * @param opList the list in which the CoordinateOperation must be added
     */
    private static void addCoordinateOperations(
            GeodeticDatum sourceDatum, GeodeticCRS source,
            GeodeticDatum targetDatum, GeodeticCRS target,
            Set<CoordinateOperation> opList) throws CoordinateOperationException {

        // We get registered transformation from source GeodeticDatum to target GeodeticDatum
        // There maybe more than one transformations available.
        Set<CoordinateOperation> datumTransformations = new HashSet<CoordinateOperation>(2);

        // If source CRS or target CRS is 3D, we need to use a 3D Geocentric transformation
        // from source Datum to target Datum
        if (source.getCoordinateSystem().getDimension() == 3 || target.getCoordinateSystem().getDimension() == 3) {

            // First, we get all transformations already available from sourceDatum to targetDatum
            CoordinateOperation mostPrecise3DTransform =
                    getMostPrecise3DTransformation(sourceDatum.getGeocentricTransformations(targetDatum));
            if (mostPrecise3DTransform != null) {
                datumTransformations.add(mostPrecise3DTransform);
            }
            // The following process adds new datum transformations from sourceDatum to targetDatum
            // if one of these datum is considered equivalent to WGS84.
            // Here, we consider that source or target is equivalent to WGS84 if its toWGS84 is identity
            // its PrimeMeridian is Greenwhich and its Ellipsoid is one of WGS84, GRS80
            // In this case, all transformation from/to WGS84 is considered as available to
            // sourceDatum (resp. targetDatum)
            if (sourceDatum.getToWGS84().isIdentity() && sourceDatum.getPrimeMeridian().equals(PrimeMeridian.GREENWICH)
                    && (sourceDatum.getEllipsoid().equals(Ellipsoid.GRS80) || sourceDatum.getEllipsoid().equals(Ellipsoid.WGS84))) {
                datumTransformations.addAll(GeodeticDatum.WGS84.getGeocentricTransformations(targetDatum));
            }
            if (targetDatum.getToWGS84().isIdentity() && targetDatum.getPrimeMeridian().equals(PrimeMeridian.GREENWICH)
                    && (targetDatum.getEllipsoid().equals(Ellipsoid.GRS80) || targetDatum.getEllipsoid().equals(Ellipsoid.WGS84))) {
                datumTransformations.addAll(sourceDatum.getGeocentricTransformations(GeodeticDatum.WGS84));
            }

            // OK, we found at least one geocentric transformation between source and target datum,
            // now let's build complete transformation sequences from sourceCRS to target CRS
            if (!datumTransformations.isEmpty()) {
                for (CoordinateOperation datumTransformation : datumTransformations) {
                    try {
                        GeocentricTransformationSequence newSequence = new GeocentricTransformationSequence(
                                new Identifier(CoordinateOperation.class,
                                        source.getCode() + " to " + target.getCode() + " through " + datumTransformation.getName()),
                                source.toGeographicCoordinateConverter(),
                                new LongitudeRotation(source.getDatum().getPrimeMeridian().getLongitudeFromGreenwichInRadians()),
                                new Geographic2Geocentric(source.getDatum().getEllipsoid()),
                                datumTransformation,
                                new Geocentric2Geographic(target.getDatum().getEllipsoid()),
                                new LongitudeRotation(target.getDatum().getPrimeMeridian().getLongitudeFromGreenwichInRadians()).inverse(),
                                target.fromGeographicCoordinateConverter());
                        //for (CoordinateOperation op : newSequence.sequence) System.out.println("     " + op.getPrecision() + " : " + op);
                        opList.add(newSequence);
                    } catch (NonInvertibleOperationException e) {
                        LOG.warn("Operation from " + source.getCode() + " to " + target.getCode()
                                + " through " + datumTransformation.getName() + " could not be created");
                        LOG.error("CoordinateOperationFactory", e);
                    }
                }
                if (opList.isEmpty()) throw new CoordinateOperationNotFoundException(sourceDatum, targetDatum);
            } else {
                LOG.warn("Cannot create a CoordinateOperation from :\n" + source + "\nto :\n" + target);
            }
        }
        // Now we consider the simpler case where source and target are 2D CRS
        else {
            // do getGeocentricTransformations first as it may add calculated geographicTransformations
            sourceDatum.getGeocentricTransformations(targetDatum);
            datumTransformations.addAll(sourceDatum.getGeographicTransformations(targetDatum));
            // See the remark above
            if (sourceDatum.getToWGS84().isIdentity() && sourceDatum.getPrimeMeridian().equals(PrimeMeridian.GREENWICH)
                    && (sourceDatum.getEllipsoid().equals(Ellipsoid.GRS80) || sourceDatum.getEllipsoid().equals(Ellipsoid.WGS84))) {
                datumTransformations.addAll(GeodeticDatum.WGS84.getGeographicTransformations(targetDatum));
            }
            if (targetDatum.getToWGS84().isIdentity() && targetDatum.getPrimeMeridian().equals(PrimeMeridian.GREENWICH)
                    && (targetDatum.getEllipsoid().equals(Ellipsoid.GRS80) || targetDatum.getEllipsoid().equals(Ellipsoid.WGS84))) {
                datumTransformations.addAll(sourceDatum.getGeographicTransformations(GeodeticDatum.WGS84));
            }

            if (!datumTransformations.isEmpty()) {
                for (CoordinateOperation datumTf : datumTransformations) {
                    try {
                        opList.add(new CoordinateOperationSequence(
                                new Identifier(CoordinateOperationSequence.class,
                                source.getCode() + " to " + target.getCode() + " through " + datumTf.getName()),
                                source.toGeographicCoordinateConverter(),
                                datumTf,
                                target.fromGeographicCoordinateConverter()));
                    } catch (NonInvertibleOperationException e) {
                        LOG.warn("Operation from " + source.getCode() + " to " + target.getCode()
                                + " through " + datumTf.getName() + " could not be created");
                        LOG.error("CoordinateOperationFactory", e);
                    }
                }
            } else {
                LOG.warn("Cannot create a CoordinateOperation from :\n" + source + "\nto :\n" + target);
            }
        }
    }

    /**
     * Returns {@link org.cts.op.CoordinateOperation}s including operations of a particular type.
     */
    public static Set<CoordinateOperation> includeFilter(Collection<? extends CoordinateOperation> ops, Class clazz) {
        Set<CoordinateOperation> list = new HashSet<CoordinateOperation>();
        for (CoordinateOperation op : ops) {
            if (clazz.isAssignableFrom(op.getClass())) list.add(op);
            else if (op instanceof CoordinateOperationSequence) {
                boolean includeOp = false;
                for (CoordinateOperation subOp : ((CoordinateOperationSequence)op).getSequence()) {
                    if (clazz.isAssignableFrom(subOp.getClass())) {
                        includeOp = true;
                        break;
                    }
                }
                if (includeOp) list.add(op);
            }
        }
        return list;
    }

    /**
     * Returns {@link org.cts.op.CoordinateOperation}s excluding sequence containing a particular operation type.
     */
    public static Set<CoordinateOperation> excludeFilter(Collection<? extends CoordinateOperation> ops, Class clazz) {
        Set<CoordinateOperation> list = new HashSet<CoordinateOperation>();
        for (CoordinateOperation op : ops) {
            if (clazz.isAssignableFrom(op.getClass())) continue;
            if (op instanceof CoordinateOperationSequence) {
                boolean excludedOp = false;
                for (CoordinateOperation subOp : ((CoordinateOperationSequence)op).getSequence()) {
                    if (clazz.isAssignableFrom(subOp.getClass())) {
                        excludedOp = true;
                        break;
                    }
                }
                if (!excludedOp) list.add(op);
            }
        }
        return list;
    }

    /**
     * Returns the most precise among the list of {@link org.cts.op.CoordinateOperation}s.
     */
    public static CoordinateOperation getMostPrecise(Collection<? extends CoordinateOperation> ops) {
        CoordinateOperation preciseOp = null;
        double currentPrecision = Double.MAX_VALUE;
        for (CoordinateOperation op : ops) {
            if (op.getPrecision() < currentPrecision) {
                preciseOp = op;
                currentPrecision = op.getPrecision();
            }
        }
        return preciseOp;
    }

    /**
     * Returns the most precise among the list of {@link org.cts.op.CoordinateOperation}s.
     */
    public static CoordinateOperation getMostPrecise3DTransformation(Collection<? extends CoordinateOperation> ops) {
        CoordinateOperation preciseOp = null;
        double currentPrecision = Double.MAX_VALUE;
        for (CoordinateOperation op : ops) {
            if (op.getPrecision() < currentPrecision && op instanceof GeocentricTransformation) {
                preciseOp = op;
                currentPrecision = op.getPrecision();
            }
        }
        return preciseOp;
    }

}
