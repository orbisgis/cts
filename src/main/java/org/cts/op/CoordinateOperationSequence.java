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
import org.cts.IllegalCoordinateException;
import org.cts.NonInvertibleOperationException;

import java.util.List;

/**
 * A coordinate operation sequence can transform a coordinate through several
 * ordered {@link CoordinateOperation}s.
 *
 * @author Michaël Michaud
 */
public class CoordinateOperationSequence extends AbstractCoordinateOperation {

    private CoordinateOperation[] sequence;
    protected static final Logger LOG = Logger.getLogger(CoordinateOperationSequence.class);

    /**
     * Create a CoordinateOperationSequence from an identifier and an array of {@link org.cts.CoordinateOperation}s.
     * Precision of this sequence is considered as the sum of all single
     * {@link org.cts.CoordinateOperation}.
     *
     * @param identifier this operation sequence identifier
     * @param sequence an array containing ordered operations to apply to
     * coordinates
     */
    public CoordinateOperationSequence(Identifier identifier,
            CoordinateOperation... sequence) {
        super(identifier);
        this.sequence = sequence;
        for (CoordinateOperation op : sequence) {
            precision += op.getPrecision();
        }
    }

    /**
     * Create a CoordinateOperationSequence from an identifier and a List of {@link org.cts.CoordinateOperation}s.
     * Precision of this sequence is considered as the sum of all single
     * {@link org.cts.CoordinateOperation}.
     *
     * @param identifier this operation sequence identifier
     * @param list a list containing ordered operations to apply to coordinates
     */
    public CoordinateOperationSequence(Identifier identifier,
            List<CoordinateOperation> list) {
        super(identifier);
        this.sequence = list.toArray(new CoordinateOperation[list.size()]);
        for (CoordinateOperation op : sequence) {
            precision += op.getPrecision();
        }
    }

    /**
     * Create a CoordinateOperationSequence from an identifier an array of
     * {@link org.cts.CoordinateOperation}s and a precision.
     *
     * @param identifier this operation sequence identifier
     * @param sequence a list containing ordered operations to apply to
     * coordinates
     * @param precision precision of this CoordinateOperation as a whole.
     */
    public CoordinateOperationSequence(Identifier identifier,
            CoordinateOperation[] sequence, double precision) {
        super(identifier);
        this.sequence = sequence;
        this.precision = precision;
    }

    /**
     * Creates a CoordinateOperationSequence from an identifier, a List of
     * {@link org.cts.CoordinateOperation}s and a precision.
     *
     * @param identifier this operation sequence identifier
     * @param list a list containing ordered operations to apply to coordinates
     * @param precision precision of this CoordinateOperation as a whole.
     */
    public CoordinateOperationSequence(Identifier identifier,
            List<CoordinateOperation> list, double precision) {
        super(identifier);
        this.sequence = list.toArray(new CoordinateOperation[list.size()]);
        this.precision = precision;
    }

    /**
     * Implementation of the transform method for a sequence of transformation.
     * It is important that input coordinate is a 3D coordinate because any of
     * the coordinate operation of the sequence may be a 3D coordinate of {@link org.cts.CoordinateOperation}s.
     *
     * @param coord the 3D coord to transform
     * @throws IllegalCoordinateException if
     * <code>coord</code> is not compatible with this
     * <code>CoordinateOperation</code>.
     */
    @Override
    public double[] transform(double[] coord)
            throws IllegalCoordinateException {
        for (CoordinateOperation op : sequence) {
            coord = op.transform(coord);
        }
        return coord;
    }

    /**
     * Apply the inverse transformation to coord
     *
     * @param coord the coord to transform
     */
    /*
     * public void inverseTransform(double[] coord) throws
     * CoordinateDimensionException, NonInvertibleOperationException { try { for
     * (int i=sequence.length-1 ; i >=0 ; i--)
     * sequence[i].inverseTransform(coord);
     * System.out.println(java.util.Arrays.toString(coord)); }
     * catch(CoordinateDimensionException e) {throw e;}
     * catch(NonInvertibleOperationException e) {throw e;} catch(Exception e)
     * {e.printStackTrace();} }
     */
    /**
     * Creates the inverse CoordinateOperation.
     */
    @Override
    public CoordinateOperation inverse() throws NonInvertibleOperationException {
        CoordinateOperation[] inverse_sequence =
                new CoordinateOperation[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            // If one of the CoordinateOperation is not invertible, it
            // will throw a NonInvertibleOperationException.
            inverse_sequence[sequence.length - i - 1] = sequence[i].inverse();
            //if (inverse_sequence[sequence.length-i-1] == null) return null;
        }
        return new CoordinateOperationSequence(getIdentifier(),
                inverse_sequence, precision);
    }

    /**
     * Return the sequence of the coordinateOperation
     * @return 
     */
    CoordinateOperation[] getSequence() {
        return sequence;
    }

    /**
     * Returns a String representation of this CoordinateOperationSequence. It
     * gives a correct representation of CoordinateOperationSequence nested in a
     * CoordinateOperationSequence, but will not display nicely a third level of
     * nests operation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(getIdentifier().getName()).append("{\n");
        for (CoordinateOperation op : sequence) {
            sb.append("   ").append(op.getCode());
            if (op instanceof CoordinateOperationSequence) {
                sb.append(" {\n");
                for (CoordinateOperation op2 : ((CoordinateOperationSequence) op).getSequence()) {
                    sb.append("      ").append(op2.toString()).append("\n");
                }
                sb.append("   }\n");
            } else {
                sb.append(" : ").append(op.toString()).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
