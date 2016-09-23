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

import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for PRJ / WKT (OGC & ESRI) String.
 *
 * This very simple parser works in tree steps: 1. it parses the PRJ String and
 * produces an abstract tree, without any assumptions on it being a valid OGC
 * WKT String. 2. it walks the tree looking for the values needed for
 * transformation into a proj4 description string. 3. the proj4 description
 * string is passed to the {@link org.jproj.parser.Proj4Parser } that builds the
 * CRS.
 *
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public class PrjParser {

    /**
     * Creates a new parser.
     *
     */
    public PrjParser() {
    }

    /**
     * Parses a WKT PRJ String into a set of parameters.
     *
     * This is the main entry point of the parser.
     *
     * @param prjString a WKT string
     * @return a list of parameters
     * @throws PrjParserException if the PRJ cannot be parsed into a CRS for any
     * reason
     */
    public Map<String, String> getParameters(String prjString) {
        CharBuffer s = CharBuffer.wrap(prjString);
        PrjElement e;
        try {
            e = parseNode(s);
        } catch (BufferUnderflowException ex) {
            throw new PrjParserException("Failed to read PRJ.", ex);
        }
        Map<String, String> prjParameters = PrjMatcher.match(e);
        return prjParameters;
    }

    /**
     * Parses a Node into its PrjElement representation.
     *
     * @param s the Buffer to parse
     */
    public PrjElement parseNode(CharBuffer s) {
        // parse Name
        boolean complexNode = false;
        int start = s.position();
        int ll = 0;
        while (s.hasRemaining()) {
            char c = s.get();
            if (c == '[') {
                complexNode = true;
                break;
            } else if (c == ']' || c == ',') {
                break;
            } else {
                ll++;
            }
        }
        s.position(start);
        String name = s.subSequence(0, ll).toString();
        s.position(start + ll + 1);

        if (complexNode) {
            // parse children and return
            return new PrjNodeElement(name, parseNodeChildren(s));
        } else {
            s.position(s.position() - 1);
            return new PrjStringElement(name);
        }
    }

    /**
     * Return the next character that is not a white space.
     *
     * @param s the parsed Buffer
     */
    private char next(CharBuffer s) {
        char next;
        do {
            next = s.get();
        } while (Character.isWhitespace(next));
        return next;
    }

    /**
     * Parses a Node's children into a list of PrjElement.
     *
     * @param s the Buffer to parse
     */
    private List<PrjElement> parseNodeChildren(CharBuffer s) {
        List<PrjElement> elms = new ArrayList<PrjElement>();

        boolean finished = false;

        do {
            char next = next(s);
            if (next == '"') {
                elms.add(parseString(s));
            } else {
                s.position(s.position() - 1);
                if (Character.isDigit(next) || next == '-') {
                    elms.add(parseNumber(s));
                } else {
                    elms.add(parseNode(s));
                }
            }


            next = next(s);
            switch (next) {
                case ',':
                    break;
                case ']':
                    finished = true;
                    break;
                default:
                    throw new PrjParserException("weird character: " + next);
            }
        } while (!finished);

        return elms;
    }

    /**
     * Parses a String information into its PrjStringElement representation.
     *
     * @param s the Buffer to parse
     */
    private PrjStringElement parseString(CharBuffer s) {
        int start = s.position();
        int ll = 0;
        while (s.hasRemaining()) {
            char c = s.get();
            if (c == '"') {
                break;
            } else {
                ll++;
            }
        }
        s.position(start);
        String str = s.subSequence(0, ll).toString();
        s.position(start + ll + 1);

        return new PrjStringElement(str);
    }

    /**
     * Parses a Number information into its PrjNumberElement representation.
     *
     * @param s the Buffer to parse
     */
    private PrjNumberElement parseNumber(CharBuffer s) {
        int start = s.position();
        int ll = 0;
        while (s.hasRemaining()) {
            char c = s.get();
            if (c == ',' || c == ']' || Character.isWhitespace(c)) {
                break;
            } else {
                ll++;
            }
        }
        s.position(start);
        String str = s.subSequence(0, ll).toString();
        s.position(start + ll);

        return new PrjNumberElement(Double.parseDouble(str));
    }
}
