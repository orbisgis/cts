package org.cts.datum;

import org.cts.Identifier;
import org.cts.crs.Geographic2DCRS;
import org.cts.op.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.cts.datum.GeodeticDatum.WGS84;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoRecursionTest extends BaseCoordinateTransformTest {

    /**
     * Test that a Datum with no toWGS84 method defined does does not produce infinite recursion
     * (bug report #98)
     */
    @Test
    void testNoRecursion() {
        // datum without toWGS84 definition
        GeodeticDatum gd = GeodeticDatum.createGeodeticDatum(PrimeMeridian.GREENWICH, Ellipsoid.GRS80, null);
        final Geographic2DCRS crs = new Geographic2DCRS(new Identifier(Geographic2DCRS.class), gd);
        final Geographic2DCRS wgs = new Geographic2DCRS(new Identifier(Geographic2DCRS.class), WGS84);

        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        CoordinateOperationFactory.createCoordinateOperations(crs, wgs);
                    }
                },
                "Expected CoordinateOperationException but did not throw"
        );
    }
}
