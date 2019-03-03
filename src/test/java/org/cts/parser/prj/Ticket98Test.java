package org.cts.parser.prj;

import org.cts.CTSTestCase;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.op.Identity;
import org.cts.op.transformation.SevenParameterTransformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ticket98Test extends CTSTestCase {

    @Test
    void albertEqualsAreaParsingFromESRITest() throws Exception {
        CoordinateReferenceSystem crs = cRSFactory.getCRS("ESRI:102039");
        assertTrue(crs.getDatum().getToWGS84() instanceof SevenParameterTransformation);
        assertFalse(crs.getDatum().getToWGS84().equals(Identity.IDENTITY));
    }
}
