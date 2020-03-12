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

package org.cts.util;

import org.junit.jupiter.api.Test;

import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for AngleFormat class
 */
class AngleFormatTest {

    private static final double PRECISION = 1.0e-15;
    private static final double DEG55_IN_RAD = 9.599310885968813e-1;
    private static final double DEG55_IN_GRA = 6.111111111111111e1;

    private static void assertEqualsWithPrecision(double expected, double actual, double precision){
        double exp = Math.floor(expected == 0 ? 1 : Math.log10(Math.abs(expected)));
        assertEquals(expected, actual, Math.pow(10, exp) * precision);
    }

    @Test
    void longitudeFormatterTest(){
        assertEquals("0° 00' 00.00000\" E", AngleFormat.LONGITUDE_FORMATTER.format(0));
        assertEquals("35° 33' 36.00000\" E", AngleFormat.LONGITUDE_FORMATTER.format(35.56));
        assertEquals("35° 33' 36.00000\" W", AngleFormat.LONGITUDE_FORMATTER.format(-35.56));
    }

    @Test
    void latitudeFormatterTest(){
        assertEquals("0° 00' 00.00000\" N", AngleFormat.LATITUDE_FORMATTER.format(0));
        assertEquals("35° 33' 36.00000\" N", AngleFormat.LATITUDE_FORMATTER.format(35.56));
        assertEquals("35° 33' 36.00000\" S", AngleFormat.LATITUDE_FORMATTER.format(-35.56));
    }

    @Test
    void rad2degTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.rad2deg(0.0), PRECISION);
        assertEqualsWithPrecision(180.0, AngleFormat.rad2deg(PI), PRECISION);
        assertEqualsWithPrecision(-180.0, AngleFormat.rad2deg(-PI), PRECISION);
        assertEqualsWithPrecision(55.0, AngleFormat.rad2deg(DEG55_IN_RAD), PRECISION);
    }

    @Test
    void rad2graTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.rad2gra(0.0), PRECISION);
        assertEqualsWithPrecision(200.0, AngleFormat.rad2gra(PI), PRECISION);
        assertEqualsWithPrecision(-200.0, AngleFormat.rad2gra(-PI), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_GRA, AngleFormat.rad2gra(DEG55_IN_RAD), PRECISION);
    }

    @Test
    void rad2minTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.rad2min(0.0), PRECISION);
        assertEqualsWithPrecision(180*60, AngleFormat.rad2min(PI), PRECISION);
        assertEqualsWithPrecision(-180*60, AngleFormat.rad2min(-PI), PRECISION);
        assertEqualsWithPrecision(55*60, AngleFormat.rad2min(DEG55_IN_RAD), PRECISION);
    }

    @Test
    void rad2secTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.rad2sec(0.0), PRECISION);
        assertEqualsWithPrecision(180*60*60, AngleFormat.rad2sec(PI), PRECISION);
        assertEqualsWithPrecision(-180*60*60, AngleFormat.rad2sec(-PI), PRECISION);
        assertEqualsWithPrecision(55*60*60, AngleFormat.rad2sec(DEG55_IN_RAD), PRECISION);
    }

    @Test
    void deg2radTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.deg2rad(0.0), PRECISION);
        assertEqualsWithPrecision(PI, AngleFormat.deg2rad(180), PRECISION);
        assertEqualsWithPrecision(-PI, AngleFormat.deg2rad(-180), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.deg2rad(55), PRECISION);
    }

    @Test
    void deg2graTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.deg2gra(0.0), PRECISION);
        assertEqualsWithPrecision(200, AngleFormat.deg2gra(180), PRECISION);
        assertEqualsWithPrecision(-200, AngleFormat.deg2gra(-180), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_GRA, AngleFormat.deg2gra(55), PRECISION);
    }

    @Test
    void deg2minTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.deg2min(0.0), PRECISION);
        assertEqualsWithPrecision(180*60, AngleFormat.deg2min(180), PRECISION);
        assertEqualsWithPrecision(-180*60, AngleFormat.deg2min(-180), PRECISION);
        assertEqualsWithPrecision(55*60, AngleFormat.deg2min(55), PRECISION);
    }

    @Test
    void deg2secTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.deg2sec(0.0), PRECISION);
        assertEqualsWithPrecision(180*60*60, AngleFormat.deg2sec(180), PRECISION);
        assertEqualsWithPrecision(-180*60*60, AngleFormat.deg2sec(-180), PRECISION);
        assertEqualsWithPrecision(55*60*60, AngleFormat.deg2sec(55), PRECISION);
    }

    @Test
    void gra2radTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.gra2rad(0.0), PRECISION);
        assertEqualsWithPrecision(PI, AngleFormat.gra2rad(200), PRECISION);
        assertEqualsWithPrecision(-PI, AngleFormat.gra2rad(-200), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.gra2rad(DEG55_IN_GRA), PRECISION);
    }

    @Test
    void gra2degTest(){
        assertEqualsWithPrecision(0.0, AngleFormat.gra2deg(0.0), PRECISION);
        assertEqualsWithPrecision(180, AngleFormat.gra2deg(200), PRECISION);
        assertEqualsWithPrecision(-180, AngleFormat.gra2deg(-200), PRECISION);
        assertEqualsWithPrecision(55, AngleFormat.gra2deg(DEG55_IN_GRA), PRECISION);
    }

    @Test
    void dms2ddTest(){
        assertEqualsWithPrecision(-55.365, AngleFormat.dms2dd(-55.2154), PRECISION);
        assertEqualsWithPrecision(32.6625, AngleFormat.dms2dd(32.3945), PRECISION);
    }

    @Test
    void dd2dmsTest(){
        assertEqualsWithPrecision(-55.2154, AngleFormat.dd2dms(-55.365), PRECISION);
        assertEqualsWithPrecision(32.3945, AngleFormat.dd2dms(32.6625), PRECISION);
    }

    @Test
    void angleFormatTest(){
        AngleFormat defaultFormat = new AngleFormat();
        assertEquals("42° 12' 55.600\" S", defaultFormat.format(-42.2154444));
        assertEquals("2° 06' 03.600\" N", defaultFormat.format(2.1010));

        AngleFormat formatWithLotOfDigit = new AngleFormat("DDDD.DD° MMMM.MM' SSSS.SSSSSS\" H(M|P)");
        assertEquals("0042.00° 0012.00' 0055.599840\" P", formatWithLotOfDigit.format(-42.2154444));
        assertEquals("0002.00° 0006.00' 0003.600000\" M", formatWithLotOfDigit.format(2.1010));

        AngleFormat formatWithoutSuffix = new AngleFormat("DDMMSS.SSSH(N|S)");
        assertEquals("42 12 55.600S", formatWithoutSuffix.format(-42.2154444));
        assertEquals("02 06 03.600N", formatWithoutSuffix.format(2.1010));

        AngleFormat formatWithSuffix = new AngleFormat("DD% MM@ SS.SSS£ H(N|S)");
        assertEquals("42% 12@ 55.600£ S", formatWithSuffix.format(-42.2154444));
        assertEquals("02% 06@ 03.600£ N", formatWithSuffix.format(2.1010));

        AngleFormat formatWithPrefix = new AngleFormat("prefixDD° MM' SS.SSS\" H(N|S)");
        assertEquals("prefix42° 12' 55.600\" S", formatWithPrefix.format(-42.2154444));
        assertEquals("prefix02° 06' 03.600\" N", formatWithPrefix.format(2.1010));

        AngleFormat formatWithFewDigit = new AngleFormat("#D° M' S\" H(N|S)");
        assertEquals("42° 12' 56\" S", formatWithFewDigit.format(-42.2154444));
        assertEquals("2° 6' 4\" N", formatWithFewDigit.format(2.1010));

        AngleFormat formatWithoutH = new AngleFormat("#DD° MM' SS.SSS\"");
        assertEquals("-42° 12' 55.600\"", formatWithoutH.format(-42.2154444));
        assertEquals("+02° 06' 03.600\"", formatWithoutH.format(2.1010));

        AngleFormat formatWithoutS = new AngleFormat("#DD° MM' H(N|S)");
        assertEquals("42° 13' S", formatWithoutS.format(-42.2154444));
        assertEquals("02° 06' N", formatWithoutS.format(2.1010));

        AngleFormat formatWithoutM = new AngleFormat("#DD° SS.SSS\" H(N|S)");
        assertEquals("42° S", formatWithoutM.format(-42.2154444));
        assertEquals("02° N", formatWithoutM.format(2.1010));

        AngleFormat formatWithoutSH = new AngleFormat("#DD° MM'");
        assertEquals("-42° 13'", formatWithoutSH.format(-42.2154444));
        assertEquals("+02° 06'", formatWithoutSH.format(2.1010));

        AngleFormat formatWithoutMH = new AngleFormat("#DD° SS.SSS\"");
        assertEquals("-42° ", formatWithoutMH.format(-42.2154444));
        assertEquals("+02° ", formatWithoutMH.format(2.1010));

        AngleFormat formatWithoutMS = new AngleFormat("#DD° H(N|S)");
        assertEquals("-42° ", formatWithoutMH.format(-42.2154444));
        assertEquals("+02° ", formatWithoutMH.format(2.1010));

        AngleFormat formatWithoutMSH = new AngleFormat("#DD°");
        assertEquals("-42°", formatWithoutMSH.format(-42.2154444));
        assertEquals("+02°", formatWithoutMSH.format(2.1010));
    }

    @Test
    void angleFormatBadTest(){
        assertThrows(IllegalArgumentException.class, () -> new AngleFormat(""));
        assertThrows(IllegalArgumentException.class, () -> new AngleFormat("Not a valid format"));
        assertThrows(IllegalArgumentException.class, () -> new AngleFormat("prefix#MM' SS.SSS\" H(N|S)"));
    }

    @Test
    void parseAngleTest(){
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("23°01'01s"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23°01m01s"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23°01m01"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23d01'01"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23d01'01s"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23d01m01"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23d01m01s"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23d01m"));
        assertEquals(23.0, AngleFormat.parseAngle("23d"));
        //DMS format
        assertEquals(23.0, AngleFormat.parseAngle("23°"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°1'"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°01'"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23°01'01"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01"));
        //DMSH format
        assertEquals(23.0, AngleFormat.parseAngle("23°n"));
        assertEquals(23.0, AngleFormat.parseAngle("23°N"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°n"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°N"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°1'N"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'N"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°01'N"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01N"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23°01'01N"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01N"));
        //DMSH double neg
        assertEquals(-23.0, AngleFormat.parseAngle("23°s"));
        assertEquals(-23.0, AngleFormat.parseAngle("23°S"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°s"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°S"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°1'S"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'S"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°01'S"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01S"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("23°01'01S"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01S"));
        //DMSH format
        assertEquals(23.0, AngleFormat.parseAngle("23°e"));
        assertEquals(23.0, AngleFormat.parseAngle("23°E"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°e"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°E"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°1'E"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'E"));
        assertEquals(23.0 + 1.0 / 60, AngleFormat.parseAngle("23°01'E"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01E"));
        assertEquals(23.0 + 1.0 / 60 + 1.0 / 3600, AngleFormat.parseAngle("23°01'01E"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01E"));
        //DMSH double neg
        assertEquals(-23.0, AngleFormat.parseAngle("23°w"));
        assertEquals(-23.0, AngleFormat.parseAngle("23°W"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°w"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°W"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°1'W"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'W"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°01'W"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01W"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("23°01'01W"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01W"));
        //DMSH double neg
        assertEquals(-23.0, AngleFormat.parseAngle("23°o"));
        assertEquals(-23.0, AngleFormat.parseAngle("23°O"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°o"));
        assertEquals(-23.0, AngleFormat.parseAngle("-23°O"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°1'O"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°1'O"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("23°01'O"));
        assertEquals(-23.0 - 1.0 / 60, AngleFormat.parseAngle("-23°01O"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("23°01'01O"));
        assertEquals(-23.0 - 1.0 / 60 - 1.0 / 3600, AngleFormat.parseAngle("-23°01'01O"));
    }

    @Test
    void parseAngleBadTest(){
        assertThrows(IllegalArgumentException.class, () -> AngleFormat.parseAngle("Not an angle"));
    }

    @Test
    void parseAndConvert2RadiansTest(){
        assertEquals(-0.235, AngleFormat.parseAndConvert2Radians("-.235"));
        assertEquals(23.77, AngleFormat.parseAndConvert2Radians("+23.77"));
        assertEquals(4.6, AngleFormat.parseAndConvert2Radians("4,6"));

        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"g"), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"g."), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"gr"), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"gr."), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"grad"), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"grad."), PRECISION);
        assertEqualsWithPrecision(DEG55_IN_RAD, AngleFormat.parseAndConvert2Radians(DEG55_IN_GRA+"grades"), PRECISION);

        assertEquals(-0.40, AngleFormat.parseAndConvert2Radians("-23°01'01O"), 0.01);
        assertEquals(0.40, AngleFormat.parseAndConvert2Radians("23d01m01s"), 0.01);
    }

    @Test
    void parseAndConvert2RadiansBadTest(){
        assertThrows(IllegalArgumentException.class, () -> AngleFormat.parseAndConvert2Radians("Not an angle"));
    }
}
