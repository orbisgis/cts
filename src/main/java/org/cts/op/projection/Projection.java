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
package org.cts.op.projection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.cts.Ellipsoid;
import org.cts.Identifier;
import org.cts.Parameter;
import org.cts.op.AbstractCoordinateOperation;
import org.cts.units.Measure;
import org.cts.units.Unit;

/**
 * A map projection is any method used in cartography (mapmaking) to represent
 * the two-dimensional curved surface of the earth or other body on a plane. The
 * term "projection" here refers to any function defined on the earth's surface
 * and with values on the plane, and not necessarily a geometric projection.<p>
 *
 * @author Michaël Michaud, Erwan Bocher
 */
public abstract class Projection extends AbstractCoordinateOperation {

    public static final Parameter[] DEFAULT_PARAMETERS = new Parameter[]{
        new Parameter(Parameter.FALSE_EASTING, new Measure(0, Unit.METER)),
        new Parameter(Parameter.FALSE_NORTHING, new Measure(0, Unit.METER)),
        new Parameter(Parameter.CENTRAL_MERIDIAN, new Measure(0, Unit.DEGREE)),
        new Parameter(Parameter.STANDARD_PARALLEL_1, new Measure(0, Unit.DEGREE)),
        new Parameter(Parameter.STANDARD_PARALLEL_2, new Measure(0, Unit.DEGREE)),
        new Parameter(Parameter.SCALE_FACTOR, new Measure(0, Unit.UNIT)),
        new Parameter(Parameter.LATITUDE_OF_ORIGIN, new Measure(0, Unit.DEGREE))};

    public static Hashtable<String, Measure> getDefaultParameters() {
        Hashtable<String, Measure> parameters = new Hashtable<String, Measure>();
        for (Parameter param : DEFAULT_PARAMETERS) {
            parameters.put(param.getName(), param.getMeasure());
        }
        return parameters;
    }

    /**
     * Projection classification based on the surface type.
     */
    public static enum Surface {

        AZIMUTHAL, // or stereographic
        CONICAL,
        CYLINDRICAL,
        HYBRID,
        MISCELLANEOUS,
        POLYCONICAL,
        PSEUDOAZIMUTHAL,
        PSEUDOCONICAL,
        PSEUDOCYLINDRICAL,
        RETROAZIMUTHAL
    };

    /**
     * Projection property.
     */
    public static enum Property {

        APHYLACTIC, // A term sometimes used to describe a map projection
        //which is neither equal-area  nor conformal
        CONFORMAL, // Locally shape preserving (angle preserving)
        EQUAL_AREA, // Area preserving (also called Equiarea, Equivalent, Authalic)
        EQUIDISTANT, // Distance preserving
        GNOMONIC
    }       // Shortest route preserving;

    /**
     * Projection orientation.
     */
    public static enum Orientation {

        OBLIQUE,
        SECANT,
        TANGENT,
        TRANSVERSE
    };
    // Ellispoid used for this projection
    Ellipsoid ellipsoid;
    // Other parameters
    final Map<String, Measure> parameters; // = new HashMap<String,Measure>();

    /**
     * Creates a new Projection
     *
     * @param identifier identifier of the projection
     * @param ellipsoid ellipsoid used for this projection
     * @param parameters other projection parameters
     */
    protected Projection(final Identifier identifier, final Ellipsoid ellipsoid,
            final Map<String, Measure> parameters) {
        super(identifier);
        this.ellipsoid = ellipsoid;
        // store parameters in a new Map, because this Projection parameters
        // must never be modified after initialization
        // NOTE : I also wanted to make it unmodifiable, but could not
        // use Collections.<String,Measure>unmodifiableMap(clone)
        if (parameters == null) {
            this.parameters = Collections.<String, Measure>unmodifiableMap(new HashMap<String, Measure>());
        } else {
            this.parameters = Collections.<String, Measure>unmodifiableMap(new HashMap(parameters));
        }
    }

    public double getSemiMajorAxis() {
        return ellipsoid.getSemiMajorAxis();
    }

    public double getSemiMinorAxis() {
        return ellipsoid.getSemiMinorAxis();
    }

    public double getCentralMeridian() {
        return parameters.get(Parameter.CENTRAL_MERIDIAN).getSValue();
    }

    public double getLatitudeOfOrigin() {
        return parameters.get(Parameter.LATITUDE_OF_ORIGIN).getSValue();
    }

    public double getStandardParallel1() {
        return parameters.get(Parameter.STANDARD_PARALLEL_1).getSValue();
    }

    public double getStandardParallel2() {
        return parameters.get(Parameter.STANDARD_PARALLEL_2).getSValue();
    }
    
     public double getLatitudeOfTrueScale() {
        return parameters.get(Parameter.LATITUDE_OF_TRUE_SCALE).getSValue();
    }

    public double getScaleFactor() {
        Measure m = parameters.get(Parameter.SCALE_FACTOR);
        return m != null ? parameters.get(Parameter.SCALE_FACTOR).getSValue() : 1.;
    }

    public double getFalseEasting() {
        return parameters.get(Parameter.FALSE_EASTING).getSValue();
    }

    public double getFalseNorthing() {
        return parameters.get(Parameter.FALSE_NORTHING).getSValue();
    }

    /**
     * Return the
     * <code>Surface</code> type of this
     * <code>Projection</code>.
     */
    public abstract Surface getSurface();

    /**
     * Return the
     * <code>Property</code> of this
     * <code>Projection</code>.
     */
    public abstract Property getProperty();

    /**
     * Return the
     * <code>Orientation</code> of this
     * <code>Projection</code>.
     */
    public abstract Orientation getOrientation();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        Projection proj = (Projection) o;
        if (this.toString() != null) {
            if (toString().equals(proj.toString())) {
                return true;
            }
        }
        return false;
    }
}
