package org.cts.parser.prj;

import org.cts.CTSTestCase;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.op.Identity;
import org.cts.op.transformation.SevenParameterTransformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ticket98Test extends CTSTestCase {

    @Test
    void albertEqualsAreaParsingFromESRITest() throws Exception {
        CoordinateReferenceSystem crs = cRSFactory.getCRS("ESRI:102039");
        assertInstanceOf(SevenParameterTransformation.class, crs.getDatum().getToWGS84());
        assertNotEquals(crs.getDatum().getToWGS84(), Identity.IDENTITY);
    }
}
