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
package org.cts.parser.prj;

/**
 * List all parameters used to define a PRJ file
 *
 * @author Erwan Bocher, Jules Party
 */
public class PrjKeyParameters {

    public static final String GEOGCS = "geogcs";
    public static final String GEOCCS = "geoccs";
    public static final String UNIT = "unit";
    public static final String VERTUNIT = "vertunit";
    public static final String PROJECTION = "projection";
    public static final String PARAMETER = "parameter";
    public static final String AUTHORITY = "authority";
    public static final String PROJCS = "projcs";
    public static final String PRIMEM = "primem";
    public static final String SPHEROID = "spheroid";
    public static final String AXIS = "axis";
    public static final String COMPDCS = "compd_cs";
    public static final String VERTCS = "vert_cs";
    //Internal key parameters used by the parser
    public static final String VERTUNITVAL = "vertunitval";
    public static final String GEOGUNITREFNAME = "geogunitrefname";
    public static final String GEOGUNIT = "geogunit";
    public static final String GEOGUNITVAL = "geogunitval";
    public static final String NAME = "name";
    public static final String REFNAME = "refname";
    public static final String GEOGREFNAME = "geogrefname";
    public static final String PROJREFNAME = "projrefname";
    public static final String VERTREFNAME = "vertrefname";
    public static final String SPHEROIDREFNAME = "spheroidrefname";
    public static final String DATUMREFNAME = "datumrefname";
    public static final String VERTDATUM = "vert_datum";
    public static final String VERTDATUMREFNAME = "vertdatumrefname";
    public static final String VERTDATUMTYPE = "vertdatumtype";
    public static final String PRIMEMREFNAME = "primemrefname";
    public static final String UNITREFNAME = "unitrefname";
    public static final String VERTUNITREFNAME = "vertunitrefname";
    public static final String AXIS1 = "axis1";
    public static final String AXIS1TYPE = "axis1type";
    public static final String AXIS2 = "axis2";
    public static final String AXIS2TYPE = "axis2type";
    public static final String AXIS3 = "axis3";
    public static final String AXIS3TYPE = "axis3type";
    public static final String VERTAXIS = "vertaxis";
    public static final String VERTAXISTYPE = "vertaxistype";
    public static final String PMVALUE = "pmvalue";
}
