package org.cts.cs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GeographicextentTest {

    @Test
    void testGeographicExtentConstructor() {
        GeographicExtent extent = new GeographicExtent("test", 10.0, 20.0, 30.0, 40.0);
        assertEquals(10.0, extent.getSouthernBound());
        assertEquals(20.0, extent.getNorthernBound());
        assertEquals(30.0, extent.getWesternBound());
        assertEquals(40.0, extent.getEasternBound());
        assertEquals(360.0, extent.getModulo());
        assertTrue(extent.isInside(15.0, 35.0));
        assertTrue(extent.isInside(10.0, 35.0));
        assertTrue(extent.isInside(20.0, 35.0));
        assertTrue(extent.isInside(15.0, 30.0));
        assertTrue(extent.isInside(15.0, 40.0));
        assertFalse(extent.isInside(5.0, 35.0));
        assertFalse(extent.isInside(25.0, 35.0));
        assertFalse(extent.isInside(15.0, 25.0));
        assertFalse(extent.isInside(15.0, 45.0));
    }

    @Test
    void testInversedLatitudeBounds() {
        // InversedLatitude : southern latitude must be < northern latitude
        GeographicExtent extent = new GeographicExtent("test",
                20.0,
                10.0,
                30.0,
                40.0
        );
        assertFalse(extent.isInside(0.0, 35));
        assertFalse(extent.isInside(10.0, 35));
        assertFalse(extent.isInside(15.0, 35));
        assertFalse(extent.isInside(20.0, 35));
        assertFalse(extent.isInside(25.0, 35));
    }

    @Test
    void testInversedLongitudeBounds() {
        // InversedLongitude : means that the extent includes the 180 meridian
        GeographicExtent extent = new GeographicExtent("test",
                10.0,
                20.0,
                170.0,
                -170.0
        );
        assertTrue(extent.isInside(15.0, 170));
        assertTrue(extent.isInside(15.0, 175));
        assertTrue(extent.isInside(15.0, 180));
        assertTrue(extent.isInside(15.0, -180));
        assertTrue(extent.isInside(15.0, -175));
        assertTrue(extent.isInside(15.0, -170));

        assertFalse(extent.isInside(15.0, 160));
        assertFalse(extent.isInside(15.0, -160));
    }

    @Test
    void testLongitudeSup180() {
        // Eastern longitude > 180 to express an extent including the 180 meridian
        GeographicExtent extent = new GeographicExtent("test",
                10.0,
                20.0,
                170.0,
                190.0
        );
        assertTrue(extent.isInside(15.0, 170));
        assertTrue(extent.isInside(15.0, 175));
        assertTrue(extent.isInside(15.0, 180));
        assertTrue(extent.isInside(15.0, -180));
        assertTrue(extent.isInside(15.0, -175));
        assertTrue(extent.isInside(15.0, -170));

        assertFalse(extent.isInside(15.0, 160));
        assertFalse(extent.isInside(15.0, -160));
    }

    @Test
    void testInversedLongitudeInGrades() {
        // InversedLongitude : means that the extent includes the 180 meridian
        // note the modulo parameter
        GeographicExtent extent = new GeographicExtent("test",
                10.0,
                20.0,
                190.0,
                -190.0,
                400.0
        );
        assertTrue(extent.isInside(15.0, 190));
        assertTrue(extent.isInside(15.0, 195));
        assertTrue(extent.isInside(15.0, 200));
        assertTrue(extent.isInside(15.0, -200));
        assertTrue(extent.isInside(15.0, -195));
        assertTrue(extent.isInside(15.0, -190));

        assertFalse(extent.isInside(15.0, 180));
        assertFalse(extent.isInside(15.0, -180));
    }

}
