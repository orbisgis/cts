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
package org.cts.op;

import org.cts.Identifier;
import org.cts.IllegalCoordinateException;
import org.cts.Parameter;
import org.cts.crs.*;
import org.cts.cs.GeographicExtent;
import org.cts.datum.Ellipsoid;
import org.cts.datum.GeodeticDatum;
import org.cts.datum.PrimeMeridian;
import org.cts.datum.VerticalDatum;
import org.cts.op.projection.GaussSchreiberTransverseMercator;
import org.cts.op.projection.UniversalTransverseMercator;
import org.cts.op.transformation.GeocentricTranslation;
import org.cts.op.transformation.SevenParameterTransformation;
import org.cts.units.Measure;
import org.cts.units.Unit;
import org.cts.util.AngleFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Michaël Michaud
 */
public class CirceReunionComplianceTest extends BaseCoordinateTransformTest {


    static GeographicExtent LA_REUNION = new GeographicExtent("La Réunion", -20.749, -21.501, 55.139, 55.941);
    // ------------------------------------------------------------------------
    // Datum
    // ------------------------------------------------------------------------
    static GeodeticDatum PITON_DES_NEIGES = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","PDN","Piton des Neiges"), PrimeMeridian.GREENWICH, Ellipsoid.INTERNATIONAL1924,
            new GeocentricTranslation(94.0, -948.0, -1262.0, 10), LA_REUNION, null, null);

    static GeodeticDatum RGR92 = GeodeticDatum.createGeodeticDatum(
            new Identifier("IGNF","RGR92","RGR92"), PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, LA_REUNION, null, null);

    static GeodeticDatum WGS84 = GeodeticDatum.createGeodeticDatum(
            new Identifier("EPSG","6326","WGS84"),  PrimeMeridian.GREENWICH, Ellipsoid.GRS80,
            Identity.IDENTITY, LA_REUNION, null, null);

    static Map<String,Measure> params;

    static GeodeticCRS PDN_GEO2D;
    static GeodeticCRS PDN_GAUSS_LABORDE;
    static GeodeticCRS RGR92_CARTESIAN;
    static GeodeticCRS RGR92_GEO2D;
    static GeodeticCRS RGR92_UTM40S;

    static VerticalCRS RAR07 = new VerticalCRS(new Identifier("IGNF","RAR07",""), VerticalDatum.RAR07);

    static CompoundCRS PDN_GEO2D_RAR07;
    static CompoundCRS PDN_GAUSS_LABORDE_RAR07;
    static CompoundCRS RGR92_GEO2D_RAR07;
    static CompoundCRS RGR92_UTM40S_RAR07;


    static {
        // Parameters taken from epsg database
        //PITON_DES_NEIGES.addGeocentricTransformation(RGR92, SevenParameterTransformation
        //        .createSevenParameterTransformation(789.524, -626.486, -89.904, 0.6006, 76.7946, -10.5788, -32.3241,
        //                SevenParameterTransformation.POSITION_VECTOR, SevenParameterTransformation.LINEARIZED, 0.1));


        //PITON_DES_NEIGES.addGeocentricTransformation(RGR92, SevenParameterTransformation
        //        .createBursaWolfTransformation(789.524, -626.486, -89.904, 0.6006, 76.7946, -10.5788, -32.3241));


        //PITON_DES_NEIGES.addGeocentricTransformation(RGR92, SevenParameterTransformation
        //        .createSevenParameterTransformation(789.524, -626.486, -89.904, 0.6, 76.8, -10.6, -32.324,
        //                SevenParameterTransformation.POSITION_VECTOR, SevenParameterTransformation.NOT_LINEARIZED, 0.1));

        RGR92.addGeocentricTransformation(PITON_DES_NEIGES, SevenParameterTransformation
                //.createBursaWolfTransformation(-789.990, 627.333, 89.685, -0.6072, -76.8019, 10.5680, 32.2083, 0.1));
                .createSevenParameterTransformation(-789.990, 627.333, 89.685, -0.6072, -76.8019, 10.5680, 32.2083,
                        SevenParameterTransformation.POSITION_VECTOR, SevenParameterTransformation.LINEARIZED, 0.1));

        PDN_GEO2D = new Geographic2DCRS(new Identifier("IGNF", "PDN_GEO2D", "Piton des Neige - Géographique 2D"),
                PITON_DES_NEIGES);

        params = new HashMap<String,Measure>();
        params.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(AngleFormat.parseAngle("21°07'S"), Unit.DEGREE));
        params.put(Parameter.CENTRAL_MERIDIAN, new Measure(AngleFormat.parseAngle("55°32'"), Unit.DEGREE));
        params.put(Parameter.FALSE_EASTING, new Measure(160000, Unit.METER));
        params.put(Parameter.FALSE_NORTHING, new Measure(50000, Unit.METER));
        params.put(Parameter.SCALE_FACTOR, new Measure(1.0, Unit.UNIT));
        PDN_GAUSS_LABORDE = new ProjectedCRS(new Identifier("IGNF", "PDN_GL", "Piton des Neige - Gauss Laborde"),
                PITON_DES_NEIGES, new GaussSchreiberTransverseMercator(Ellipsoid.INTERNATIONAL1924, params));

        RGR92_CARTESIAN = new GeocentricCRS(new Identifier("IGNF","RGR92_GC", "RGR92 Geocentric"), RGR92, GeocentricCRS.XYZ);

        RGR92_GEO2D = new Geographic2DCRS(new Identifier("IGNF","RGR92_GEO2D", "RGR92 Géographique 2D"), RGR92);

        params = new HashMap<String, Measure>();
        params.put(Parameter.CENTRAL_MERIDIAN, new Measure(57, Unit.DEGREE));
        params.put(Parameter.FALSE_EASTING, new Measure(500000, Unit.METER));
        params.put(Parameter.FALSE_NORTHING, new Measure(10000000, Unit.METER));
        params.put(Parameter.SCALE_FACTOR, new Measure(0.9996, Unit.UNIT));
        RGR92_UTM40S = new ProjectedCRS(new Identifier("IGNF","RGR92_UTM40S", "RGR92 UTM 40 S"), RGR92,
                new UniversalTransverseMercator(Ellipsoid.GRS80, params));

        try {
            PDN_GEO2D_RAR07 = new CompoundCRS(new Identifier("IGNF", "PDN_GEO2D", "Piton des Neige - Géographique 2D"),
                    PDN_GEO2D, RAR07);
            PDN_GAUSS_LABORDE_RAR07 = new CompoundCRS(new Identifier("IGNF", "PDN_GL_RAR07", "Piton des Neige - Gauss Laborde + RAR07"),
                    PDN_GAUSS_LABORDE, RAR07);
            RGR92_GEO2D_RAR07 = new CompoundCRS(new Identifier("IGNF", "RGR92_GEO2D_RAR07", "RGR92 - Géographique 2D + RAR07"),
                    RGR92_GEO2D, RAR07);
            RGR92_UTM40S_RAR07 = new CompoundCRS(new Identifier("IGNF", "RGR92_UTM40S_RAR07", "RGR92 - UTM 40 S + RAR07"),
                    RGR92_UTM40S, RAR07);
        } catch (CRSException e){
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------
    // Piton des Neiges -> RGR92
    // ------------------------------------------------------------------------

    //@Test
    public void testPDN_GEO2D_to_RGR92_GEO2D() throws IllegalCoordinateException, CoordinateOperationException {
        test(new double[]{-21.0, 55.5, 0.0}, PDN_GEO2D, RGR92_GEO2D, new double[]{-21.01244031, 55.49412901}, MM_IN_DEG*10, MM_IN_DEG*10);
        //test(new double[]{-21, 55.5, 0}, PDN_GEO2D_RAR07, RGR92_GEO2D_RAR07, new double[]{-21.01244031, 55.49412901, 0}, MM_IN_DEG, MM_IN_DEG);
    }

    /*
    private void test(double[] source, GeodeticCRS sourceCRS,
                      GeodeticCRS targetCRS, double[] ref,
                      double tolSource, double tolTarget) throws IllegalCoordinateException, CoordinateOperationException {
        double[] dir_tf = transform(sourceCRS, targetCRS, source);
        //double[] inv_tf = transform(targetCRS, sourceCRS, ref);
        System.out.println(Arrays.toString(source));
        System.out.println(Arrays.toString(dir_tf) + " (" + Arrays.toString(ref) + ")");
        assertTrue(checkEquals2D("dir--> " + sourceCRS + " to " + targetCRS, dir_tf, ref, tolTarget));
        //System.out.println(Arrays.toString(inv_tf) + " (" + Arrays.toString(source) + ")");
        //assertTrue(checkEquals2D("inv--> " + sourceCRS + " to " + targetCRS, inv_tf, source, tolSource));
    }
    */


}
