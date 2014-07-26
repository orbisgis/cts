package org.cts.op.transformation;

import org.cts.CTSTestCase;
import org.cts.IllegalCoordinateException;
import org.cts.op.CoordinateOperationException;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static java.lang.Math.PI;
import static org.cts.op.transformation.SevenParameterTransformation.*;


/**
 * Created by Michaël on 08/03/14.
 */
public class SevenParameterTransformationTest extends CTSTestCase {

    // Test seven parameters transformation called Bursa-Wolf
    // Ref = EPSG:9606 or IGN ALG0013
    // Test using IGN test with a precision of 1e-4
    @Test
    public void testBursaWolf() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op = createBursaWolfTransformation(-69.400, 18.000, 452.2, 0.0, 0.0, 1.030, -3.21);
        double[] source = new double[]{4154088.1420, -80626.3310, 4822852.8130};
        double[] result = op.transform(source.clone());
        //System.out.println(Arrays.toString(result));
        double[] ref = new double[]{4154005.8099, -80587.3284, 4823289.5316};
        assertTrue(checkEquals3D("BursaWolf", result, ref, 0.0001, 0.0001));
        result = op.inverse().transform(ref);
        //System.out.println(Arrays.toString(result));
        assertTrue(checkEquals3D("BursaWolf-inv", result, source, 0.0001, 0.0001));
    }

    // Test seven parameters transformation using exact sinus
    // Ref = EPSG:9606 or IGN ALG0063
    // Test using IGN test with a precision of 1e-4
    @Test
    public void testALG0063() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op = createBursaWolfTransformation(
                789.524, -626.486, -89.904,
                0.6, 76.8, -10.6, -32.324, 0.1);
        double[] source = new double[]{3356123.540, 1303218.309, 5247430.605};
        double[] result = op.inverse0063().transform(source.clone());
        //System.out.println(Arrays.toString(result));
        double[] ref = new double[]{3353421.0230, 1304074.5496, 5248934.9846};
        //System.out.println(Arrays.toString(ref));
        assertTrue(checkEquals3D("7-parameter", result, ref, 0.0001, 0.0001));
    }

    // Test seven parameters transformation using exact sinus
    // Ref = EPSG:9606 or IGN ALG0063
    // Test using IGN test with a precision of 1e-4
    @Test
    public void testALG0063b() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op = createBursaWolfTransformation(-80.283, -107.802, -136.031, 0.035, 0.0, 0.547, 0.185, 0.1);
        double[] source = new double[]{3353657.175, 1303862.662, 5249102.055};
        double[] result = op.inverse0063().transform(source.clone());
        //System.out.println(Arrays.toString(result));
        double[] ref = new double[]{3353740.2956, 1303962.2196, 5249236.8936};
        //System.out.println(Arrays.toString(ref));
        assertTrue(checkEquals3D("7-parameter", result, ref, 0.0001, 0.0001));
    }


    // Test reversibility of seven parameters transformations
    // with a 100" rotation
    @Test
    public void testReversibilityALG0013() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op1 = createBursaWolfTransformation(-80.283, -107.802, -136.031, 0.035, 50.0, 0.547, 32.185, 0.1);
        SevenParameterTransformation op2 = createBursaWolfTransformation(80.283, 107.802, 136.031, -0.035, -50.0, -0.547, -32.185, 0.1);

        double[] source = new double[]{3353657.175, 1303862.662, 5249102.055};
        double[] target = op1.transform(source.clone());
        double[] inverse = op2.transform(target.clone());
        assertFalse(checkEquals3D("Reversibility Bursa-Wolf", inverse, source, 0.0001, 0.0001));
        assertFalse(checkEquals3D("Reversibility Bursa-Wolf", inverse, source, 0.001, 0.001));
        assertFalse(checkEquals3D("Reversibility Bursa-Wolf", inverse, source, 0.01, 0.01));
        assertFalse(checkEquals3D("Reversibility Bursa-Wolf", inverse, source, 0.1, 0.1));
        assertTrue(checkEquals3D("Reversibility Bursa-Wolf", inverse, source, 1.0, 1.0));
    }

    //@TODO ALG0063 seems not to be stable - cf with IGN what is it for as it seems to be used in Circe
    // Test reversibility of seven parameters transformations
    // with a 100" rotation
    //@Test
    //public void testReversibilityALG0063() throws IllegalCoordinateException, CoordinateOperationException {
    //    SevenParameterTransformation op1 = SevenParameterTransformation
    //            .createSevenParameterTransformation(-80.283, -107.802, -136.031, 0.035, 50.0, 0.547, 32.185,
    //                    SevenParameterTransformation.POSITION_VECTOR, SevenParameterTransformation.LINEARIZED);
    //    SevenParameterTransformation op2 = op1.inverse0063();
    //    double[] source = new double[]{3353657.175, 1303862.662, 5249102.055};
    //    double[] target = op1.transform(source.clone());
    //    double[] inverse = op2.transform(target.clone());
    //    assertTrue(checkEquals3D("Reversibility Bursa-Wolf with ALG0063", inverse, source, 0.0001, 0.0001));
    //}

    // Test reversibility of seven parameters transformations
    // with a 100" rotation
    @Test
    public void testReversibilityBursaWolfCTS() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op1 = createSevenParameterTransformation(
                -80.283, -107.802, -136.031, 0.035, 50.0, 0.547, 32.185, POSITION_VECTOR, LINEARIZED);

        SevenParameterTransformation op2 = op1.inverse();

        double[] source = new double[]{3353657.175, 1303862.662, 5249102.055};
        double[] target = op1.transform(source.clone());
        double[] inverse = op2.transform(target.clone());
        assertTrue(checkEquals3D("Reversibility Bursa-Wolf with ALG0063", inverse, source, 0.0001, 0.0001));
    }


    @Test
    public void testStabilityInverseCTS() throws IllegalCoordinateException, CoordinateOperationException {
        SevenParameterTransformation op1 = createBursaWolfTransformation(
                789.524, -626.486, -89.904,
                0.00000290888 * 180 * 3600 / PI,
               0.00037233691 * 180 * 3600 / PI,
                -0.00005139025 * 180 * 3600 / PI, -32.324, 0.1);

        double[] source = new double[]{3356123.5400, 1303218.3090, 5247430.6050};
        double[] result1 = op1.inverse().transform(op1.transform(op1.inverse().transform(op1.transform(source.clone()))));
        assertTrue(checkEquals3D("Stability Bursa-Wolf CTS", result1, source, 0.0001, 0.0001));
    }

    //@TODO ALG0063 seems not to be stable - cf with IGN what is it for as it seems to be used in Circe
    //@Test
    //public void testStabilityALG0063() throws IllegalCoordinateException, CoordinateOperationException {
    //    SevenParameterTransformation op1 = SevenParameterTransformation
    //            .createBursaWolfTransformation(789.524, -626.486, -89.904,
    //                    0.00000290888*180*3600/PI, 0.00037233691*180*3600/PI, -0.00005139025*180*3600/PI, -32.324, 0.1);
    //    SevenParameterTransformation op2 = SevenParameterTransformation
    //            .createBursaWolfTransformation(-789.524, 626.486, 89.904,
    //                    -0.00000290888*180*3600/PI, -0.00037233691*180*3600/PI, 0.00005139025*180*3600/PI, 32.324, 0.1);
    //    double[] source = new double[]{3356123.5400, 1303218.3090, 5247430.6050};
    //    double[] result1 = op1.inverse0063().transform(op1.transform(op1.inverse0063().transform(op1.transform(source.clone()))));
    //    assertTrue(checkEquals3D("Stability Bursa-Wolf CTS", result1, source, 0.0001, 0.0001));
    //}

}
