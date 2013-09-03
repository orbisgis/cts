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
import java.util.Arrays;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

import java.util.List;

/**
 * A coordinate operation sequence can transform a coordinate through several
 * ordered {@linkplain  org.cts.CoordinateOperation CoordinateOperations}.
 *
 * @author Michaël Michaud
 */
public class CoordinateOperationSequence extends AbstractCoordinateOperation {

    /**
     * The sequence of the {@link CoordinateOperation} used by this
     * CoordinateOperationSequence.
     */
    private CoordinateOperation[] sequence;

    /**
     * Create a CoordinateOperationSequence from an identifier and an array of
     * {@linkplain  org.cts.CoordinateOperation CoordinateOperations}. Precision
     * of this sequence is considered as the sum of all single
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
        this.sequence = cleanSequence(sequence);
        for (CoordinateOperation op : sequence) {
            precision += op.getPrecision();
        }
    }

    /**
     * Create a CoordinateOperationSequence from an identifier and a List of
     * {@linkplain  org.cts.CoordinateOperation CoordinateOperations}. Precision
     * of this sequence is considered as the sum of all single
     * {@link org.cts.CoordinateOperation}.
     *
     * @param identifier this operation sequence identifier
     * @param list a list containing ordered operations to apply to coordinates
     */
    public CoordinateOperationSequence(Identifier identifier,
            List<CoordinateOperation> list) {
        super(identifier);
        this.sequence = list.toArray(new CoordinateOperation[list.size()]);
        this.sequence = cleanSequence(sequence);
        for (CoordinateOperation op : sequence) {
            precision += op.getPrecision();
        }
    }

    /**
     * Create a CoordinateOperationSequence from an identifier an array of
     * {@linkplain  org.cts.CoordinateOperation CoordinateOperations} and a
     * precision.
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
        this.sequence = cleanSequence(sequence);
        this.precision = precision;
    }

    /**
     * Creates a CoordinateOperationSequence from an identifier, a List of
     * {@linkplain  org.cts.CoordinateOperation CoordinateOperations} and a
     * precision.
     *
     * @param identifier this operation sequence identifier
     * @param list a list containing ordered operations to apply to coordinates
     * @param precision precision of this CoordinateOperation as a whole.
     */
    public CoordinateOperationSequence(Identifier identifier,
            List<CoordinateOperation> list, double precision) {
        super(identifier);
        this.sequence = list.toArray(new CoordinateOperation[list.size()]);
        this.sequence = cleanSequence(sequence);
        this.precision = precision;
    }

    /**
     * Implementation of the transform method for a sequence of transformation.
     * It is important that input coordinate is a 3D coordinate because any of
     * the coordinate operation of the sequence may be a 3D coordinate of
     * {@link org.cts.CoordinateOperation}s.
     *
     * @param coord the 3D coord to transform
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
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
     * Return the sequence of the coordinateOperation.
     */
    CoordinateOperation[] getSequence() {
        return sequence;
    }

    /**
     * fusionSequences merge cleverly two list of CoordinateOperations by
     * removing the last element of the first list and the first of the second
     * list when one of these is the inverse of the other.
     *
     * @param list1 one of the list to merge (it will be the beginning of the
     * returned list)
     * @param list2 the other list to merge (it will be the end of the returned
     * list)
     */
    private static List<CoordinateOperation> fusionSequences(List<CoordinateOperation> list1, List<CoordinateOperation> list2) {
        List<CoordinateOperation> lst1 = new ArrayList<CoordinateOperation>(list1);
        List<CoordinateOperation> lst2 = new ArrayList<CoordinateOperation>(list2);
        if (lst1.isEmpty() && lst2.isEmpty()) {
            lst1.add(Identity.IDENTITY);
            return lst1;
        }
        if (lst1.isEmpty()) {
            return lst2;
        }
        if (lst2.isEmpty()) {
            return lst1;
        }
        CoordinateOperation op1 = lst1.get(lst1.size() - 1);
        CoordinateOperation op2 = lst2.get(0);
        if (op1.equals(Identity.IDENTITY)) {
            lst1.remove(lst1.size() - 1);
            return fusionSequences(lst1, lst2);
        }
        if (op2.equals(Identity.IDENTITY)) {
            lst2.remove(0);
            return fusionSequences(lst1, lst2);
        }
        try {
            if (op1.equals(ChangeCoordinateDimension.TO3D) && op2.equals(ChangeCoordinateDimension.TO2D)
                    || op1.equals(op2.inverse())) {
                lst1.remove(lst1.size() - 1);
                lst2.remove(0);
                return fusionSequences(lst1, lst2);
            }
        } catch (NonInvertibleOperationException ex) {
        }
        lst1.addAll(lst2);

        return lst1;
    }

    /**
     * Returned the sequence of CoordinateOperations cleaned by removing useless
     * operations as Identity and successive inverse transformations.
     *
     * @param sequence the sequence of CoordinateOperation to clean
     */
    private static CoordinateOperation[] cleanSequence(CoordinateOperation... sequence) {
        List<CoordinateOperation> result = new ArrayList<CoordinateOperation>();
        for (CoordinateOperation op : sequence) {
            if (op != null && !op.equals(Identity.IDENTITY) && !(op instanceof CoordinateOperationSequence)) {
                result.add(op);
            } else if (op instanceof CoordinateOperationSequence) {
                result = fusionSequences(result, Arrays.asList(((CoordinateOperationSequence) op).getSequence()));
            }
        }
        if (sequence.length > 0 && result.isEmpty()) {
            result.add(Identity.IDENTITY);
        }
        return result.toArray(new CoordinateOperation[result.size()]);
    }

    /**
     * cleverAdd add cleverly a CoordinateOperation in a list of
     * CoordinateOperations by removing the last element of list if it is the
     * inverse of the element to add. If not, it simply add the
     * CoordinateOperation at the end of the list. NB1: If
     * <code>op</code> is the Identity transformation, it is added only if
     * <code>ops</code> is empty. NB2: If
     * <code>ops</code> contains only the identity transformation, it is
     * replaced by
     * <code>op</code>.
     *
     * @param ops the list in which the CoordinateOperation should be added
     * @param op the CoordinateOperation to add list)
     */
    public static List<CoordinateOperation> cleverAdd(List<CoordinateOperation> ops, CoordinateOperation op) {
        List<CoordinateOperation> result = new ArrayList<CoordinateOperation>(ops);
        List<CoordinateOperation> addedOp = new ArrayList<CoordinateOperation>();
        addedOp.add(op);
        result = fusionSequences(result, addedOp);
        return result;
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

    /**
     * Returns true if object is equals to
     * <code>this</code>. Tests equality between the length of the sequences and
     * then the equality of each CoordinateOperation.
     *
     * @param object The object to compare this CoordinateOperationSequence
     * against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CoordinateOperationSequence) {
            CoordinateOperationSequence cooordseq = (CoordinateOperationSequence) o;
            if (getSequence().length == cooordseq.getSequence().length) {
                for (int i = 0; i < getSequence().length; i++) {
                    if (!getSequence()[i].equals(cooordseq.getSequence()[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the hash code for this CoordinateOperationSequence.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Arrays.deepHashCode(this.sequence);
        return hash;
    }
}
