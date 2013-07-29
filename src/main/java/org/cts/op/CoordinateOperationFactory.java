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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.cts.Identifier;
import org.cts.crs.*;
import org.cts.datum.GeodeticDatum;
import org.cts.op.transformation.NTv2GridShiftTransformation;

/**
 * CoordinateOperationFactory is a factory used to create
 * {@linkplain  org.cts.CoordinateOperation CoordinateOperations} from source
 * and target
 * {@linkplain org.cts.crs.CoordinateReferenceSystem CoordinateReferenceSystems}.
 *
 * @author Michaël Michaud, Jules Party
 */
public final class CoordinateOperationFactory {

    private static final Logger LOG = Logger.getLogger(CoordinateOperationFactory.class);
    public final static int GDATUM_OP = 1; // ex. NTF 2 RGF
    public final static int VDATUM_OP = 2; // ex. height instead of altitude
    public final static int ELLIPSOID_OP = 4; // ex. height instead of altitude
    public final static int PRIME_MERIDIAN_OP = 8; // ex. height instead of altitude
    public final static int GEOGRAPHIC_OP = 16; // ex. geographic from/to geocentric
    public final static int PROJECTION_OP = 32; // ex. project or unproject
    public final static int DIMENSION_OP = 64; // ex. 2D to 3D or 3D to 2D
    public final static int AXIS_ORDER_OP = 128; // ex. lon/lat instead of lat/lon
    public final static int UNIT_OP = 256; // ex. heights from meters to feet

    /**
     * Create a {@link org.cts.CoordinateOperation} from a source
     * {@link org.cts.crs.CompoundCRS} to a target
     * {@link org.cts.crs.CompoundCRS}.
     */
    public static List<CoordinateOperation> createCoordinateOperations(
            CompoundCRS source, CompoundCRS target) {
        if (source == null) {
            throw new IllegalArgumentException("The source CRS must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("The target CRS must not be null");
        }
        List<CoordinateOperation> opList = source.getCRSTransformations(target);
        if (opList != null) {
            return opList;
        } else {
            opList = new ArrayList<CoordinateOperation>();
            GeodeticDatum sourceDatum = source.getHorizontalCRS().getDatum();
            if (sourceDatum == null) {
                LOG.warn(source.getName() + " has no Geodetic Datum");
                throw new IllegalArgumentException("The source datum must not be null");
            }
            GeodeticDatum targetDatum = target.getHorizontalCRS().getDatum();
            if (targetDatum == null) {
                LOG.warn(target.getName() + " has no Geodetic Datum");
                throw new IllegalArgumentException("The target datum must not be null");
            }
            if (source.getHorizontalCRS().getGridTransformations(targetDatum) != null) {
                addNadgridsOperationDir(sourceDatum, source, targetDatum, target, source.getHorizontalCRS().getGridTransformations(targetDatum), opList);
            } else if (target.getHorizontalCRS().getGridTransformations(sourceDatum) != null) {
                addNadgridsOperationInv(sourceDatum, source, targetDatum, target, target.getHorizontalCRS().getGridTransformations(sourceDatum), opList);
            }
            if (sourceDatum.equals(targetDatum)) {
                addCoordinateOperations(sourceDatum, source, target, opList);
            } else {
                addCoordinateOperations(sourceDatum, source, targetDatum, target, opList);
            }
            source.addCRSTransformation(target, opList);
        }
        return opList;
    }

    /**
     * Create a CoordinateOperation from a source
     * {@link org.cts.crs.GeodeticCRS} to a target
     * {@link org.cts.crs.GeodeticCRS}. Remember that
     * {@link org.cts.crs.GeodeticCRS} includes {@link org.cts.crs.GeocentricCRS},
     * {@link Geographic2DCRS}, {@link org.cts.crs.Geographic3DCRS} and
     * {@link org.cts.crs.ProjectedCRS}.
     *
     * @param source the (non null) source geodetic coordinate reference system
     * @param target the (non null) target geodetic coordinate reference system
     */
    public static List<CoordinateOperation> createCoordinateOperations(
            GeodeticCRS source, GeodeticCRS target) {
        if (source == null) {
            throw new IllegalArgumentException("The source CRS must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("The target CRS must not be null");
        }
        if (source instanceof CompoundCRS) {
            if (target instanceof CompoundCRS) {
                return createCoordinateOperations((CompoundCRS) source, (CompoundCRS) target);
            } else {
                try {
                    CompoundCRS targt = CompoundCRS.toCompoundCRS(target);
                    return createCoordinateOperations((CompoundCRS) source, targt);
                } catch (CRSException ex) {
                    throw new IllegalArgumentException("There cannot be operations between CompoundCRS and " + target.getClass() + ".");
                }
            }
        } else if (target instanceof CompoundCRS) {
            try {
                CompoundCRS sourc = CompoundCRS.toCompoundCRS(source);
                return createCoordinateOperations(sourc, (CompoundCRS) target);
            } catch (CRSException ex) {
                throw new IllegalArgumentException("There cannot be operations between CompoundCRS and " + target.getClass() + ".");
            }
        }
        List<CoordinateOperation> opList = source.getCRSTransformations(target);
        if (opList != null) {
            return opList;
        } else {
            opList = new ArrayList<CoordinateOperation>();
            GeodeticDatum sourceDatum = source.getDatum();
            if (sourceDatum == null) {
                LOG.warn(source.getName() + " has no Geodetic Datum");
                throw new IllegalArgumentException("The source datum must not be null");
            }
            GeodeticDatum targetDatum = target.getDatum();
            if (targetDatum == null) {
                LOG.warn(target.getName() + " has no Geodetic Datum");
                throw new IllegalArgumentException("The target datum must not be null");
            }

            if (source.getGridTransformations(targetDatum) != null) {
                addNadgridsOperationDir(sourceDatum, source, targetDatum, target, source.getGridTransformations(targetDatum), opList);
            } else if (target.getGridTransformations(sourceDatum) != null) {
                addNadgridsOperationInv(sourceDatum, source, targetDatum, target, target.getGridTransformations(sourceDatum), opList);
            }
            if (sourceDatum.equals(targetDatum)) {
                addCoordinateOperations(sourceDatum, source, target, opList);
            } else {
                addCoordinateOperations(sourceDatum, source, targetDatum, target, opList);
            }
            source.addCRSTransformation(target, opList);
        }
        return opList;
    }

    /**
     * Add a CoordinateOperation to the list of CoordinateOperation in
     * parameter. This CoordinateOperation linked a source {@link GeodeticCRS}
     * to a target {@link GeodeticCRS} based on different
     * {@link org.cts.datum.Datum} using a CoordinateOperation to convert
     * coordinates directly from one Geographic CRS to another without the use
     * of GeocentricCRS. Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and
     * {@link ProjectedCRS}, but here the use of {@link GeocentricCRS} is
     * senseless. NB : This class was made for nadgrids that are defined only in
     * the parameter of the source CRS for the nadgrids transformation, it is
     * why there is two createNadgridsOperation, createNadgridsOperationDir must
     * be use when the nadgrids is defined in the sourceCRS.
     *
     * @param sourceDatum the (non null) datum used by source CRS
     * @param source the source geodetic coordinate reference system
     * @param targetDatum the (non null) datum used by target CRS
     * @param target the target geodetic coordinate reference system
     * @param coordOp the transformation between two Geographic CRS
     * @param opList the list in which the CoordinateOperation must be added
     */
    private static void addNadgridsOperationDir(
            GeodeticDatum sourceDatum, GeodeticCRS source,
            GeodeticDatum targetDatum, GeodeticCRS target, List<CoordinateOperation> nadgridsTransformations,
            List<CoordinateOperation> opList) {
        for (CoordinateOperation coordOp : nadgridsTransformations) {
            try {
                if (!(coordOp instanceof NTv2GridShiftTransformation) || (sourceDatum.getShortName().equals(((NTv2GridShiftTransformation) coordOp).getFromDatum()))) {
                    opList.add(new CoordinateOperationSequence(
                            new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()),
                            source.toGeographicCoordinateConverter(),
                            coordOp,
                            target.fromGeographicCoordinateConverter()));
                } else {
                    NTv2GridShiftTransformation gt = (NTv2GridShiftTransformation) coordOp;
                    GeodeticDatum gtSource = GeodeticDatum.datumFromName.get(gt.getFromDatum());
                    opList.add(new CoordinateOperationSequence(
                            new Identifier(CoordinateOperationSequence.class, sourceDatum.getName() + " to " + targetDatum.getName() + " through " + gt.getName() + " transformation"),
                            source.toGeographicCoordinateConverter(),
                            sourceDatum.getCoordinateOperations(gtSource).get(0),
                            gt,
                            target.fromGeographicCoordinateConverter()));
                }
            } catch (NonInvertibleOperationException e) {
                LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
                LOG.error("CoordinateOperationFactory", e);
            }
        }
    }

    /**
     * Add a CoordinateOperation to the list of CoordinateOperation in
     * parameter. This CoordinateOperation linked a source {@link GeodeticCRS}
     * to a target {@link GeodeticCRS} based on different
     * {@link org.cts.datum.Datum} using a CoordinateOperation to convert
     * coordinates directly from one Geographic CRS to another without the use
     * of GeocentricCRS. Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and
     * {@link ProjectedCRS}, but here the use of {@link GeocentricCRS} is
     * senseless. NB : This class was made for nadgrids that are defined only in
     * the parameter of the source CRS for the nadgrids transformation, it is
     * why there is two createNadgridsOperation, createNadgridsOperationInv must
     * be use when the nadgrids is defined in the targetCRS.
     *
     * @param sourceDatum the (non null) datum used by source CRS
     * @param source the source geodetic coordinate reference system
     * @param targetDatum the (non null) datum used by target CRS
     * @param target the target geodetic coordinate reference system
     * @param coordOp the transformation between two Geographic CRS
     * @param opList the list in which the CoordinateOperation must be added
     */
    private static void addNadgridsOperationInv(
            GeodeticDatum sourceDatum, GeodeticCRS source,
            GeodeticDatum targetDatum, GeodeticCRS target, List<CoordinateOperation> nadgridsTransformations,
            List<CoordinateOperation> opList) {
        for (CoordinateOperation coordOp : nadgridsTransformations) {
            try {
                if (!(coordOp instanceof NTv2GridShiftTransformation) || sourceDatum.getShortName().equals(((NTv2GridShiftTransformation) coordOp).getFromDatum())) {
                    opList.add(new CoordinateOperationSequence(
                            new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()),
                            source.toGeographicCoordinateConverter(),
                            coordOp.inverse(),
                            target.fromGeographicCoordinateConverter()));
                } else {
                    NTv2GridShiftTransformation gt = (NTv2GridShiftTransformation) coordOp;
                    GeodeticDatum gtSource = GeodeticDatum.datumFromName.get(gt.getFromDatum());
                    opList.add(new CoordinateOperationSequence(
                            new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()),
                            source.toGeographicCoordinateConverter(),
                            gt.inverse(),
                            gtSource.getCoordinateOperations(targetDatum).get(0),
                            target.fromGeographicCoordinateConverter()));
                }
            } catch (NonInvertibleOperationException e) {
                LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
                LOG.error("CoordinateOperationFactory", e);
            }
        }
    }

    /**
     * Add a CoordinateOperation to the list of CoordinateOperation in
     * parameter. This CoordinateOperation linked a source
     * {@link org.cts.crs.GeodeticCRS} to a target {@link GeodeticCRS} using the
     * same {@link org.cts.datum.GeodeticDatum}. Remember that
     * {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and
     * {@link ProjectedCRS}.
     *
     * @param datum the (non null) common datum of source and target CRS
     * @param source the source geodetic coordinate reference system
     * @param target the target geodetic coordinate reference system
     * @param opList the list in which the CoordinateOperation must be added
     */
    private static void addCoordinateOperations(
            GeodeticDatum datum, GeodeticCRS source, GeodeticCRS target,
            List<CoordinateOperation> opList) {
        try {
            opList.add(new CoordinateOperationSequence(
                    new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()),
                    source.toGeographicCoordinateConverter(),
                    target.fromGeographicCoordinateConverter()));
        } catch (NonInvertibleOperationException e) {
            LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
            LOG.error("CoordinateOperationFactory", e);
        }
    }

    /**
     * Add a CoordinateOperation to the list of CoordinateOperation in
     * parameter. This CoordinateOperation linked a source {@link GeodeticCRS}
     * to a target {@link GeodeticCRS} based on different
     * {@link org.cts.datum.Datum}. Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
     * {@link Geographic2DCRS}, {@link Geographic3DCRS} and
     * {@link ProjectedCRS}.
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
            List<CoordinateOperation> opList) {
        // We get registered transformation from source GeodeticDatum to target GeodeticDatum
        // There maybe one or more transformations available.
        List<CoordinateOperation> datumTransformations = sourceDatum.getCoordinateOperations(targetDatum);
        for (CoordinateOperation datumTf : datumTransformations) {
            try {
                opList.add(new CoordinateOperationSequence(
                        new Identifier(CoordinateOperationSequence.class,
                        source.getName() + " to " + target.getName() + " through " + datumTf.getName()),
                        source.toGeographicCoordinateConverter(),
                        datumTf,
                        target.fromGeographicCoordinateConverter()));
            } catch (NonInvertibleOperationException e) {
                LOG.warn("Operation from " + source.getName() + " to " + target.getName()
                        + " through " + datumTf.getName() + " could not be created");
                LOG.error("CoordinateOperationFactory", e);
            }
        }
    }
}
