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
package org.cts.crs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cts.Identifiable;
import org.cts.Identifier;
import org.cts.cs.Axis;
import org.cts.cs.CoordinateSystem;
import org.cts.datum.VerticalDatum;
import org.cts.op.*;
import org.cts.op.transformation.Altitude2EllipsoidalHeight;
import org.cts.units.Unit;

import static org.cts.op.CoordinateOperationSequence.cleverAdd;

/**
 * A 3D {@link org.cts.crs.CoordinateReferenceSystem} composed of two distinct
 * CoordinateReferenceSystem : a {@link org.cts.crs.GeodeticCRS} for 2D
 * horizontal coordinates and a {@link org.cts.crs.VerticalCRS} for the z
 * coordinate.
 *
 * @author Michaël Michaud, Jules Party
 */
public class CompoundCRS extends GeodeticCRS {

    private GeodeticCRS horizontalCRS;
    private VerticalCRS verticalCRS;

    /**
     * Creates a new CompoundCRS from a 2D horizontal CRS and a 1D vertical CRS.
     *
     * @param identifier the identifier of the CompoundCRS
     * @param horizontalCRS the horizontal part of the CompoundCRS
     * @param verticalCRS the vertical part of the CompoundCRS
     * @throws org.cts.crs.CRSException
     */
    public CompoundCRS(Identifier identifier, GeodeticCRS horizontalCRS,
            VerticalCRS verticalCRS) throws CRSException {
        super(identifier, horizontalCRS.getDatum(), new CoordinateSystem(
                new Axis[]{horizontalCRS.getCoordinateSystem().getAxis(0),
            horizontalCRS.getCoordinateSystem().getAxis(1),
            verticalCRS.getCoordinateSystem().getAxis(0)},
                new Unit[]{horizontalCRS.getCoordinateSystem().getUnit(0),
            horizontalCRS.getCoordinateSystem().getUnit(1),
            verticalCRS.getCoordinateSystem().getUnit(0)}));
        if (!(horizontalCRS instanceof ProjectedCRS || horizontalCRS instanceof Geographic2DCRS)) {
            throw new CRSException("The horizontalCRS must be a ProjectedCRS or a Geographic2DCRS. The "
                    + horizontalCRS.getClass() + " cannot be used as horizontalCRS.");
        }
        this.horizontalCRS = horizontalCRS;
        this.verticalCRS = verticalCRS;
    }

    /**
     * Returns this CoordinateReferenceSystem Type.
     * @return 
     */
    @Override
    public Type getType() {
        return CoordinateReferenceSystem.Type.COMPOUND;
    }

    /**
     * Returns the horizonal part of this CoordinateReferenceSystem.
     * @return 
     */
    public GeodeticCRS getHorizontalCRS() {
        return horizontalCRS;
    }

    /**
     * Returns the vertical part of this CoordinateReferenceSystem.
     */
    public VerticalCRS getVerticalCRS() {
        return verticalCRS;
    }

    /**
     * Creates a CoordinateOperation object to convert coordinates from this CRS
     * to the Geographic3DCRS based on the same {@link org.cts.datum.GeodeticDatum},
     * and using normal SI units in the following order : latitude (rad), longitude
     * (rad) height (m).
     * @return 
     * @throws org.cts.op.NonInvertibleOperationException 
     * @throws org.cts.op.CoordinateOperationNotFoundException 
     */
    @Override
    public CoordinateOperation toGeographicCoordinateConverter()
            throws NonInvertibleOperationException, CoordinateOperationNotFoundException {

        // Ordered list of operations to perform
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();

        // Takes care of axis orientation
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.DOWN) {
                ops.add(new OppositeCoordinate(i));
            }
        }

        // Converts to geographic coordinates in radians
        if (horizontalCRS instanceof Geographic2DCRS) {
            // Convert from source unit to radians and meters.
            if (getCoordinateSystem().getUnit(0) != Unit.RADIAN || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.RADIAN, getCoordinateSystem().getUnit(2),
                        Unit.METER));
            }
            // switch from LON/LAT to LAT/LON or northing/easting coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
        } else {
            // Convert units
            if (getCoordinateSystem().getUnit(0) != Unit.METER || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops.add(UnitConversion.createUnitConverter(getCoordinateSystem().getUnit(0), Unit.METER, getCoordinateSystem().getUnit(2),
                        Unit.METER));
            }
            // switch easting/northing coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.NORTH
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.SOUTH) {
                ops.add(CoordinateSwitch.SWITCH_LAT_LON);
            }
            // apply the inverse projection
            ops.add(horizontalCRS.getProjection().inverse());
        }

        // Now we have geographic coordinates (radian), let's take care of the vertical component

        // Case 1a - Ellipsoidal heights refering to the same ellipsoid as horizontal datum
        // @TODO : if we remove verticalCRS.getDatum().getEllipsoid() we must find another way to identify this case
        // We should test GeodeticDatum used by VerticalDaum, but is is not always defined
        if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL) &&
                horizontalCRS.getDatum().getEllipsoid().equals(verticalCRS.getDatum().getEllipsoid())) {
            ops = cleverAdd(ops, Identity.IDENTITY);
        }

        // Case 1b : Ellipsoidal heights refering to a different ellipsoid than horizontal crs
        else if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)) {
            throw new CoordinateOperationNotFoundException("Incompatible horizontal and vertical datum for this CRS : " + this);
            //@TODO should be possible to convert ellipsoidal heights if we know the GeodeticDatum associated with the vertical CRS of this compoundCRS
            // Note : above verticalCRS.getDatum().getEllipsoid() should not exists
            // Try to do the following :
            // find in verticalCRS.getVerticalDatum() a CoordinateOperation to convert to horizontalCRS.getVerticalDatum()
            // to achieve that, we must :
            // - add a method to get a ellipsoidal VerticalDatum from a GeodeticDatum
            // - use a pivot : for any vertical datum, have a toWGS / fromWGS ellipsoidal height
        }

        // Cas 2 : Altitude defined by a grid
        else if (verticalCRS.getDatum().getAltiToEllpsHeight() instanceof Altitude2EllipsoidalHeight) {

            //@TODO find a way to get a height transformation from verticalCRS.getDatum() to the verticalDatum
            // associated to this horizontalDatum with a new verticalCRS.getDatum().getHeightOperations(vDatum associated with hDatum)
            Altitude2EllipsoidalHeight z_transfo = (Altitude2EllipsoidalHeight) verticalCRS.getDatum().getAltiToEllpsHeight();

            // Case 2a : Altitude defined by a grid using the same horizontal datum (ellipsoid) as the
            // horizontal CRS
            if (horizontalCRS.getDatum().equals(z_transfo.getAssociatedDatum())) {
                ops = cleverAdd(ops, UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                ops.add(z_transfo);
                ops.add(UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER));
            }

            // Case 2b : Altitude defined by a grid using a datum different from the one used by
            // horizontal CRS
            // We have first to transform horizontal coordinates to the one used by z transformation
            //@TODO : this case should be partly transferred to the VerticalDatum.getHeightOperations() code
            else {
                // We need no transform horizontal coordinates just to be able to transform vertical ordinate
                // Before that, we save current horizontal coordinate to be able to set them back at the end
                // of the process
                ops.add(MemorizeCoordinate.memoXYZ);  // X, Y, Z, X, Y, Z

                // We find an operation to transform horizontal coordinates to the datum used by the z transformation
                Collection<CoordinateOperation> h_datum_tf =
                        horizontalCRS.getDatum().getGeographicTransformations(z_transfo.getAssociatedDatum());
                if (h_datum_tf.isEmpty()) {
                    throw new CoordinateOperationNotFoundException(horizontalCRS.getDatum(), z_transfo.getAssociatedDatum());
                }
                CoordinateOperation h_op = CoordinateOperationFactory.getMostPrecise(h_datum_tf);

                ops.add(h_op);
                ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                // We set back the original Z value we had before h_op ((datum change may have changed z ordinate)
                ops.add(LoadMemorizeCoordinate.loadZ);
                // and keep this value in memory in the eventuality of an iterative process
                ops.add(MemorizeCoordinate.memoZ);

                // Now that we have horizontal coordinates and z value consistent with z_transfo
                // we can use the z_transfo to get an ellipsoid height ABOVE z_transfo.getAssociatedDatum()
                ops.add(z_transfo);

                // h_op.inverse should transform height above z_transfo.getAssociatedDatum
                // into z above horizontalCRS.getDatum()
                ops.add(UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER));
                ops.add(h_op.inverse());

                // Complete sequence to
                // - get original X, Y coordinates
                // - apply h_op from horizonal crs used by source coordinates
                //   to horizonal crs needed for vertical datum transformation
                // - get original z ordinate
                // - apply z transfo
                // - apply h_op.inverse() to get back coordinates in the original crs
                CoordinateOperationSequence seq = new CoordinateOperationSequence(new Identifier(CoordinateOperationSequence.class),
                        new CoordinateSwitch(4, 5),   // X,  Y,  Z,  X', Z', Y'
                        new CoordinateSwitch(3, 4),   // X,  Y,  Z,  Z', X', Y'
                        LoadMemorizeCoordinate.loadY, // X,  Y', Z,  Z', X'
                        LoadMemorizeCoordinate.loadX, // X', Y', Z,  Z'
                        MemorizeCoordinate.memoXY,    // X', Y', Z,  Z', X,  Y
                        new CoordinateSwitch(3, 4),   // X', Y', Z,  X,  Z', Y
                        new CoordinateSwitch(4, 5),   // X', Y', Z,  X,  Y,  Z'
                        h_op,
                        UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER),
                        LoadMemorizeCoordinate.loadZ,
                        MemorizeCoordinate.memoZ,
                        z_transfo,
                        UnitConversion.createUnitConverter(Unit.DEGREE, Unit.RADIAN, Unit.METER, Unit.METER),
                        //h_op_inv
                        h_op.inverse()
                        );
                try {
                    // apply the sequence until calculated coordinates at index 0,1
                    // reach reference values at index 3, 4 with a precision of 1e-11
                    // and a maximum of 6 iterations
                    ops.add(new IterativeTransformation(seq, new int[]{3, 4}, new int[]{0, 1},
                            new double[]{1e-11, 1e-11}, 6));
                } catch (Exception ex) {
                    // new IterativeTransformation throws exception when array arguments
                    // have heterogeneous lengths, which should never happen here.
                }

                ops.add(LoadMemorizeCoordinate.loadY); // In fact, it deletes the memorized value of altitude
                ops.add(LoadMemorizeCoordinate.loadY); // We use the original value for a greater precision
                ops.add(LoadMemorizeCoordinate.loadX); // We use the original value for a greater precision

            }
        }
        else {
            throw new CoordinateOperationNotFoundException("Unknown vertical datum type for this CRS : " + this);
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Creates a CoordinateOperation object to convert coordinates from a
     * Geographic3DCRS based on the same {@link org.cts.datum.GeodeticDatum},
     * and using normal SI units in the following order : latitude (rad),
     * longitude (rad) height/altitude (m) to this CoordinateReferenceSystem.
     * @return 
     * @throws org.cts.op.NonInvertibleOperationException 
     * @throws org.cts.op.CoordinateOperationNotFoundException 
     */
    @Override
    public CoordinateOperation fromGeographicCoordinateConverter()
            throws NonInvertibleOperationException, CoordinateOperationNotFoundException {
        List<CoordinateOperation> ops = new ArrayList<CoordinateOperation>();

        // Case 1a : ellipsoidal height based on the same datum (ellipsoid) as horizontal crs
        if ((verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL))
                && horizontalCRS.getDatum().getEllipsoid().equals(verticalCRS.getDatum().getEllipsoid())) {
            ops.add(Identity.IDENTITY);
        }
        // Case 1b : ellipsoid height based on a different datum (ellipsoid) than horizontal crs
        // (TODO) ellipsoidal height : horizontal and vertical CRS don't use the same horizontal datum
        else if (verticalCRS.getDatum().getType().equals(VerticalDatum.Type.ELLIPSOIDAL)) {
            throw new CoordinateOperationNotFoundException("Incompatible horizontal and vertical datum for this CRS : " + this);
            // TODO
            //ops.add(verticalCRS.getDatum().getAltiToEllpsHeight());
        }

        // Case 2 : Altitude based on a grid
        else if (verticalCRS.getDatum().getAltiToEllpsHeight() instanceof Altitude2EllipsoidalHeight) {

            Altitude2EllipsoidalHeight z_transfo = (Altitude2EllipsoidalHeight) verticalCRS.getDatum().getAltiToEllpsHeight();
            ops.add(MemorizeCoordinate.memoXY);

            // Case 2a : altitude definition uses the same horizontal datum as horizontal crs
            if (horizontalCRS.getDatum().equals(z_transfo.getAssociatedDatum())) {
                // Convert to degree to be able to use the grid
                ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));
                // transform from height to altitude
                ops.add(z_transfo.inverse());
                // get original latitude / longitude in radians
                ops.add(LoadMemorizeCoordinate.loadY);
                ops.add(LoadMemorizeCoordinate.loadX);
            }

            // Case 2a : altitude definition uses a different horizontal datum as horizontal crs
            else {
                // We find an operation to transform horizontal coordinates to the datum used by the z transformation
                Collection<CoordinateOperation> h_datum_tf =
                        horizontalCRS.getDatum().getGeographicTransformations(z_transfo.getAssociatedDatum());
                if (h_datum_tf.isEmpty()) {
                    throw new CoordinateOperationNotFoundException(horizontalCRS.getDatum(), z_transfo.getAssociatedDatum());
                }
                CoordinateOperation h_op = CoordinateOperationFactory.getMostPrecise(h_datum_tf);

                // We apply horizontal transformation (temporary op) to use the grid
                ops.add(h_op);
                ops.add(UnitConversion.createUnitConverter(Unit.RADIAN, Unit.DEGREE, Unit.METER, Unit.METER));

                // Now that we have horizontal coordinates and z value consistent with z_transfo
                // we can use the z_transfo to get an ellipsoid height ABOVE z_transfo.getAssociatedDatum()
                ops.add(z_transfo.inverse());

                //ops.add(LoadMemorizeCoordinate.loadY); // In fact, it deletes the memorized value of altitude
                ops.add(LoadMemorizeCoordinate.loadY);
                ops.add(LoadMemorizeCoordinate.loadX);

            }
        }
        // 4th case (TODO) : Z-coordinate transformation uses the same horizontal datum as this crs
        else {
            throw new CoordinateOperationNotFoundException("Unknown vertical datum type for this CRS : " + this.getVerticalCRS());
        }

        if (horizontalCRS instanceof Geographic2DCRS) {
            // Convert from source unit to radians and meters.
            if (getCoordinateSystem().getUnit(0) != Unit.RADIAN || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops = cleverAdd(ops, UnitConversion.createUnitConverter(Unit.RADIAN, getCoordinateSystem().getUnit(0),
                        Unit.METER, getCoordinateSystem().getUnit(2)));
            }
            // switch from LON/LAT to LAT/LON coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.EAST
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.WEST) {
                ops = cleverAdd(ops, CoordinateSwitch.SWITCH_LAT_LON);
            }
        } else {
            // Apply the inverse projection
            ops.add(horizontalCRS.getProjection());
            // Convert units
            if (getCoordinateSystem().getUnit(0) != Unit.METER || getCoordinateSystem().getUnit(2) != Unit.METER) {
                ops = cleverAdd(ops, UnitConversion.createUnitConverter(Unit.METER, getCoordinateSystem().getUnit(0),
                        Unit.METER, getCoordinateSystem().getUnit(2)));
            }
            // switch easting/northing coordinate if necessary
            if (getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.NORTH
                    || getCoordinateSystem().getAxis(0).getDirection() == Axis.Direction.SOUTH) {
                ops = cleverAdd(ops, CoordinateSwitch.SWITCH_LAT_LON);
            }
        }
        for (int i = 0; i < 3; i++) {
            if (getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.SOUTH
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.WEST
                    || getCoordinateSystem().getAxis(i).getDirection() == Axis.Direction.DOWN) {
                ops = cleverAdd(ops, new OppositeCoordinate(i));
            }
        }
        return new CoordinateOperationSequence(new Identifier(
                CoordinateOperationSequence.class), ops);
    }

    /**
     * Returns a WKT representation of the compound CRS.
     *
     * @return 
     */
    public String toWKT() {
        StringBuilder w = new StringBuilder();
        w.append("COMPD_CS[\"");
        w.append(this.getName());
        w.append("\",");
        w.append(this.getHorizontalCRS().toWKT());
        w.append(',');
        w.append(this.getVerticalCRS().toWKT());
        if (!this.getAuthorityName().startsWith(Identifiable.LOCAL)) {
            w.append(',');
            w.append(this.getIdentifier());
        }
        w.append(']');
        return w.toString();
    }

    /**
     * Return a String representation of this Datum.
     * @return 
     */
    @Override
    public String toString() {
        return "[" + getAuthorityName() + ":" + getAuthorityKey() + "] " + getName() + " ("
                + getShortName() + ")";
    }
}