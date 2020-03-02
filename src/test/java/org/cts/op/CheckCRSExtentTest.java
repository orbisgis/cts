package org.cts.op;

import org.cts.CRSFactory;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.GeodeticCRS;
import org.cts.registry.EPSGRegistry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CheckCRSExtentTest {

    // Lambert 93 (2154)
    // Projected bounds:
    // -378305.81 6093283.21
    // 1212610.74 7186901.68
    // WGS84 bounds:
    // -9.86 41.15
    // 10.38 51.56
    @Test
    void testExtentInLocalCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs1, crs2).iterator().next();
        crs1.setExtent(new double[]{-378305.81, 6093283.21}, new double[]{1212610.74,7186901.68});
        System.out.println(Arrays.toString(op.transform(new double[]{0.0, 7000000.0})));
        assertTrue(true);
    }

    @Test
    void testExtentInGeographicCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs1, crs2).iterator().next();
        //crs1.setExtent(new double[]{-378305.81, 6093283.21}, new double[]{1212610.74,7186901.68});
        crs1.setExtent(-9.86, 41.15, 10.38, 51.56);
        System.out.println(Arrays.toString(op.transform(new double[]{0.0, 7000000.0})));
        assertTrue(true);
    }

    @Test
    void testExtentNotInLocalCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException, IOException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs1, crs2).iterator().next();
        crs1.setExtent(new double[]{-378305.81, 6093283.21}, new double[]{1212610.74,7186901.68});
        Exception exception = assertThrows(IllegalCoordinateException.class, () -> {
            System.out.println(Arrays.toString(op.transform(new double[]{-400000.0, 7000000.0})));
        });
    }

    @Test
    void testExtentNotInGeographicCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs1, crs2).iterator().next();
        //crs1.setExtent(new double[]{-378305.81, 6093283.21}, new double[]{1212610.74,7186901.68});
        crs1.setExtent(-9.86, 41.15, 10.38, 51.56);
        Exception exception = assertThrows(IllegalCoordinateException.class, () -> {
            System.out.println(Arrays.toString(op.transform(new double[]{-400000.0, 7000000.0})));
        });
    }

    @Test
    void testExtentInWGSCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException, IOException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs2, crs1).iterator().next();
        crs2.setExtent(-9.86, 41.15, 10.38, 51.56);
        System.out.println(Arrays.toString(op.transform(new double[]{1.0, 45.0})));
        assertTrue(true);
    }

    @Test
    void testExtentNotInWGSCRS() throws CRSException, CoordinateOperationException, IllegalCoordinateException, IOException {
        CRSFactory crsFactory = new CRSFactory();
        crsFactory.getRegistryManager().addRegistry(new EPSGRegistry());
        GeodeticCRS crs1 = (GeodeticCRS) crsFactory.getCRS("EPSG:2154");
        GeodeticCRS crs2 = (GeodeticCRS) crsFactory.getCRS("EPSG:4326");
        CoordinateOperation op = CoordinateOperationFactory.createCoordinateOperations(crs2, crs1).iterator().next();
        crs2.setExtent(-9.86, 41.15, 10.38, 51.56);
        Exception exception = assertThrows(IllegalCoordinateException.class, () -> {
            System.out.println(Arrays.toString(op.transform(new double[]{-10.0, 45.0})));
        });
    }
}
