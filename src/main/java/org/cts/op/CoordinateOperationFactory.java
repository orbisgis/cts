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
package org.cts.op;

import org.apache.log4j.Logger;
import org.cts.CoordinateOperation;
import org.cts.Identifier;
import org.cts.NonInvertibleOperationException;
import org.cts.crs.*;
import org.cts.datum.Datum;
import org.cts.datum.GeodeticDatum;
import org.cts.op.transformation.GeocentricTranslation;

import java.util.ArrayList;
import java.util.List;

/**
 * CoordinateOperationFactory is a factory used to create new
 * {@link org.cts.CoordinateOperation}s from {@link CoordinateReferenceSystem}s.
 * @author Michaël Michaud
 */
public final class CoordinateOperationFactory {

	private static final Logger LOG =  Logger.getLogger(CoordinateOperationFactory.class);
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
	 * Create a {@link org.cts.CoordinateOperation} from a source {@link org.cts.crs.CompoundCRS}
	 * to a target {@link org.cts.crs.CompoundCRS}.
	 */
	public static List<CoordinateOperation> createCoordinateOperations(
		CompoundCRS source, CompoundCRS target) {
		System.out.println("createCoordinateOperations() for compound CRS is not yet implemented");
		return new ArrayList<CoordinateOperation>();
	}

	/**
	 * Create a CoordinateOperation from a source {@link org.cts.crs.GeodeticCRS}
	 * to a target {@link org.cts.crs.GeodeticCRS}.
	 * Remember that {@link org.cts.crs.GeodeticCRS} includes {@link org.cts.crs.GeocentricCRS},
	 * {@link Geographic2DCRS}, {@link org.cts.crs.Geographic3DCRS} and {@link org.cts.crs.ProjectedCRS}.
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

		if (sourceDatum.equals(targetDatum)) {
			return createCoordinateOperations(sourceDatum, source, target);
		} else {
			return createCoordinateOperations(sourceDatum, source, targetDatum, target);
		}
	}

	/**
	 * Create a CoordinateOperation from a source {@link GeodeticCRS}
	 * to a target {@link GeodeticCRS} based on the same {@link Datum}.
	 * Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
	 * {@link Geographic2DCRS}, {@link Geographic3DCRS} and {@link ProjectedCRS}.
	 * @param datum the (non null) common datum of source and target CRS
	 * @param source the source geodetic coordinate reference system
	 * @param target the target geodetic coordinate reference system
	 */
	private static List<CoordinateOperation> createCoordinateOperations(
		GeodeticDatum datum, GeodeticCRS source, GeodeticCRS target) {
		//if (datum ==  null) throw new IllegalArgumentException("The datum must not be null");
		List<CoordinateOperation> opList = new ArrayList<CoordinateOperation>();
		try {
			opList.add(new CoordinateOperationSequence(
				new Identifier(CoordinateOperationSequence.class, source.getName() + " to " + target.getName()),
				source.toGeographicCoordinateConverter(),
				target.fromGeographicCoordinateConverter()));
		} catch (NonInvertibleOperationException e) {
			LOG.warn("Operation from " + source.getName() + " to " + target.getName() + " could not be created");
			LOG.error("CoordinateOperationFactory", e);
		}
		return opList;
	}

	/**
	 * Create a CoordinateOperation from a source {@link GeodeticCRS}
	 * to a target {@link GeodeticCRS} based on different {@link Datum}.
	 * Remember that {@link GeodeticCRS} includes {@link GeocentricCRS},
	 * {@link Geographic2DCRS}, {@link Geographic3DCRS} and {@link ProjectedCRS}.
	 * @param sourceDatum the (non null) datum used by source CRS
	 * @param source the source geodetic coordinate reference system
	 * @param targetDatum the (non null) datum used by target CRS
	 * @param target the target geodetic coordinate reference system
	 */
	private static List<CoordinateOperation> createCoordinateOperations(
		GeodeticDatum sourceDatum, GeodeticCRS source,
		GeodeticDatum targetDatum, GeodeticCRS target) {
		if (sourceDatum == null) {
			throw new IllegalArgumentException("The source datum must not be null");
		}
		if (targetDatum == null) {
			throw new IllegalArgumentException("The target datum must not be null");
		}
		List<CoordinateOperation> datumTransformations = sourceDatum.getCoordinateOperations(targetDatum);
		List<CoordinateOperation> opList = new ArrayList<CoordinateOperation>();
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
		if (opList.isEmpty()) {
			try {

				/*NTv2GridShiftTransformation ntv2 = new NTv2GridShiftTransformation(Datum.class.getResource(
				"ntf_r93.gsb").getPath(), 0.001);
				ntv2.setMode(NTv2GridShiftTransformation.SPEED);
				ntv2.loadGridShiftFile();*/

				opList.add(new CoordinateOperationSequence(
                        new Identifier(CoordinateOperationSequence.class),
						source.toGeographicCoordinateConverter(),
						LongitudeRotation.getLongitudeRotationFrom(sourceDatum.getPrimeMeridian()),
						new Geographic2Geocentric(sourceDatum.getEllipsoid()),
						sourceDatum.getToWGS84(),
						//ntv2,
						targetDatum.getToWGS84() == null ? new GeocentricTranslation(0, 0, 0) : targetDatum.getToWGS84().inverse(),
						new Geocentric2Geographic(targetDatum.getEllipsoid()),
						LongitudeRotation.getLongitudeRotationTo(targetDatum.getPrimeMeridian()),
						target.fromGeographicCoordinateConverter()
					)
                );

				/*	CoordinateOperation[] sequence = new CoordinateOperation[]
				{	source.toGeographicCoordinateConverter(),
				LongitudeRotation.getLongitudeRotationFrom(sourceDatum.getPrimeMeridian()),
				new Geographic2Geocentric(sourceDatum.getEllipsoid())};
				if(sourceDatum.getToWGS84()!=null) {
				sequence[sequence.length] = new AbstractCoordinateOperation("");
				sourceDatum.getToWGS84();
				if(targetDatum.getToWGS84()!=null)
				sequence[sequence.length] = targetDatum.getToWGS84().inverse();
				sequence[sequence.length] = new Geocentric2Geographic(targetDatum.getEllipsoid());
				sequence[sequence.length] = LongitudeRotation.getLongitudeRotationTo(targetDatum.getPrimeMeridian());
				if(target.fromGeographicCoordinateConverter()!=null)
				sequence[sequence.length] = target.fromGeographicCoordinateConverter();

				opList.add(new CoordinateOperationSequence(
				new Identifier(CoordinateOperationSequence.class),
				sequence));*/

			} catch (NonInvertibleOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /*catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}*/

		}
		return opList;
	}

	/**
	 * Create a CoordinateOperation from a source {@link CoordinateReferenceSystem}
	 * and a target {@link CoordinateReferenceSystem}.
	 */
	/*public static List<CoordinateOperation> createCoordinateOperations(
	GeodeticCRS source, GeodeticCRS target) {
	List<CoordinateOperation> oplist = new ArrayList<CoordinateOperation>();
	if (source == null || target == null) {
	throw new IllegalArgumentException(
	"Source and target arguments must be non null");
	}
	GeodeticDatum sourceGDatum = source.getDatum();
	if (sourceGDatum == null) {
	LOG.warning(source.getName() + " has no Geodetic Datum");
	}
	GeodeticDatum targetGDatum = target.getDatum();
	if (targetGDatum == null) {
	LOG.warning(source.getName() + " has no Geodetic Datum");
	}
	CoordinateReferenceSystem.Type sourceCRSType = source.getType();
	CoordinateReferenceSystem.Type targetCRSType = target.getType();
	// 1 - Geodetic Datums are identical
	if (sourceGDatum.equals(targetGDatum)) {
	// 1.1 - CRS are both geodetic CRS (either geocentric, geographic or projected)
	if ((sourceCRSType == GEOCENTRIC || sourceCRSType == GEOGRAPHIC2D ||
	sourceCRSType == GEOGRAPHIC3D || sourceCRSType == PROJECTED) &&
	(targetCRSType == GEOCENTRIC || targetCRSType == GEOGRAPHIC2D ||
	targetCRSType == GEOGRAPHIC3D || targetCRSType == PROJECTED)) {
	try {
	oplist.add(new CoordinateOperationSequence(
	new Identifier(CoordinateOperation.class,
	source.getName() + " to " + target.getName()),
	new CoordinateOperation[]{
	source.toGeographicCoordinateConverter(),
	target.fromGeographicCoordinateConverter()}));
	} catch(NonInvertibleOperationException e) {
	LOG.warning("Operation from " + source.getName() + " to " +
	target.getName() + " could not be created");
	LOG.throwing("CoordinateOperationFactory", "createCoordinateOperations", e);
	}

	// 1.1.1 - CRS Types are also identical
	//if (sourceCRSType == targetCRSType) {
	//    return createCoordinateOperation(source, target, sourceGDatum, sourceCRSType);
	//}
	// 1.1.2 - CRS Types are different
	//else {
	//    return createCoordinateOperation(source, target, sourceGDatum);
	//}
	}
	else if (sourceCRSType == COMPOUND || targetCRSType == COMPOUND) {
	// not yet implemented
	LOG.warning("Coumpound CRS transformation is not yet implemented");
	}
	else if (sourceCRSType == VERTICAL || targetCRSType == VERTICAL) {
	// not yet implemented
	LOG.warning("Vertical CRS transformation is not yet implemented");
	}
	else if (sourceCRSType == ENGINEERING || targetCRSType == ENGINEERING) {
	// not yet implemented
	LOG.warning("Engineering CRS transformation is not yet implemented");
	}
	else {
	// should never reach here
	throw new IllegalArgumentException("Source CRS " +
	source.getName() + " cannot be transformed into " +
	target.getName());
	}
	return oplist;

	}
	// 2 - Geodetic Datums are different
	else {
	List<CoordinateOperation> datumTfs =
	sourceGDatum.getCoordinateOperations(targetGDatum);
	for (CoordinateOperation co : datumTfs) {
	try {
	oplist.add(new CoordinateOperationSequence(
	new Identifier(CoordinateOperation.class,
	source.getName() + " to " + target.getName()),
	new CoordinateOperation[]{
	source.toGeographicCoordinateConverter(),
	co,
	target.fromGeographicCoordinateConverter()}));
	} catch(NonInvertibleOperationException e) {
	LOG.warning("Operation from " + source.getName() + " to " +
	target.getName() + " could not be created");
	LOG.throwing("CoordinateOperationFactory", "createCoordinateOperations", e);
	}
	}
	return oplist;
	}

	}
	 */
	private static CoordinateOperation createCoordinateOperation(
		GeodeticCRS source,
		GeodeticCRS target,
		GeodeticDatum sourceGDatum,
		CoordinateReferenceSystem.Type sourceCRSType) {
		return null;
	}

	private static CoordinateOperation createCoordinateOperation(GeodeticCRS source,
		GeodeticCRS target,
		GeodeticDatum sourceGDatum) {
		return null;
	}
}
