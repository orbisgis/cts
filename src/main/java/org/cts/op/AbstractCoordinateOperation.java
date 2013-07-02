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

import org.cts.IdentifiableComponent;
import org.cts.Identifier;
import org.cts.IllegalCoordinateException;

/**
 * AbstractCoordinateOperation is a partial implementation of the
 * {@link CoordinateOperation} interface.
 *
 * @author Michaël Michaud
 */
public abstract class AbstractCoordinateOperation
        extends IdentifiableComponent
        implements CoordinateOperation {

    protected double precision;

    /**
     * Create a new {@link CoordinateOperation} instance.
     *
     * @param identifier this CoordinateOperation identifier
     */
    public AbstractCoordinateOperation(Identifier identifier) {
        super(identifier);
    }

    /**
     * Return a double[] representing the same location as coord but in another
     * CoordinateReferenceSystem.
     *
     * @param coord the input coordinate
     * @return a double array containing the output coordinate
     * @throws IllegalCoordinateException if <code>coord</code> is not
     * compatible with this <code>CoordinateOperation</code>.
     */
    @Override
    public abstract double[] transform(double[] coord)
            throws IllegalCoordinateException;

    /**
     * Creates the inverse CoordinateOperation. This method can be used to chain
     * {@link fr.cts.CoordinateOperation}s and/or inverse CoordinateOperation in
     * a unique CoordinateOperationSequence. This method is not declared
     * abstract, so that implementation classes have not to implement it if they
     * represent non invertible operation.
     */
    @Override
    public CoordinateOperation inverse()
            throws NonInvertibleOperationException {
        throw new NonInvertibleOperationException(this.toString()
                + " is non invertible");
    }

    /**
     * Returns the precision of the transformation.<p> Precision is a double
     * representing the mean error, in meters made on the position resulting
     * from this {@link fr.cts.CoordinateOperation}.<p> ex. : 0.001 means that
     * the precision of the resulting position is about one millimeter<p>
     * Default precision (or maximum precision) is considered to be equals to
     * 1E-9 which is the value of an ulp (units in the last place) for a double
     * value equals to 6378137.0 (Earth semi-major axis).
     */
    @Override
    public double getPrecision() {
        return precision;
    }
}