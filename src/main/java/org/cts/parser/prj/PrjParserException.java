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
 * Return an exception when the prj cannot be parsed.
 *
 * @author Antoine Gourlay
 */
public class PrjParserException extends RuntimeException {

    /**
     * Build a PrjParserException from a cause.
     *
     * @param cause the cause of the error
     */
    public PrjParserException(Throwable cause) {
        super(cause);
    }

    /**
     * Build a PrjParserException from a message and a cause.
     *
     * @param message the message to throw
     * @param cause the cause of the error
     */
    public PrjParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Build a PrjParserException from a message.
     *
     * @param message the message to throw
     */
    public PrjParserException(String message) {
        super(message);
    }

    /**
     * Build a new PrjParserException.
     */
    public PrjParserException() {
    }
}
