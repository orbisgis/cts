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
package org.cts.cs;

/**
 * A geographic extent is an area of the Earth surface delimited by a southern
 * latitude, a northern latitude, a western longitude and an eastern longitude.
 * <p> The main difference between planimetric extent and geographic extent is
 * that geographic extent handles the case where the extent includes the 180
 * meridian. In that case, a point included in the extent may have a longitude
 * (ex. -179) which is lesser than western meridian (ex. 170) and lesser than
 * eastern meridian ex. -170).
 *
 * @author Michaël Michaud
 */
public class GeographicExtent implements Extent {

    /**
     * The World Extent that contains all the planet surface.
     */
    public static final GeographicExtent WORLD = new GeographicExtent("World", -90, 90, -180, 180);

    /**
     * The name of this GeographicExtent.
     */
    private String name;

    /**
     * The western bound of this GeographicExtent.
     */
    private double westernBound;

    /**
     * The eastern bound of this GeographicExtent.
     */
    private double easternBound;

    /**
     * The southern bound of this GeographicExtent.
     */
    private double southernBound;

    /**
     * The northern bound of this GeographicExtent.
     */
    private double northernBound;

    /**
     * The modulo value (360 for an extent in degree) for this GeographicExtent.
     */
    private double modulo;

    /**
     * Creates a new GeographicExtent. If easternBound is lower than
     * westernBound, CTS consider that the GeographicExtent crosses the 180
     * meridian.
     *
     * @param name the name of the GeographicExtent
     * @param southernBound the southern bound of the GeographicExtent, in
     * decimal degree
     * @param northernBound the northern bound of the GeographicExtent, in
     * decimal degree
     * @param westernBound the western bound of the GeographicExtent, in decimal
     * degree
     * @param easternBound the eastern bound of the GeographicExtent, in decimal
     * degree
     */
    public GeographicExtent(String name,
            double southernBound, double northernBound,
            double westernBound, double easternBound) {
        this(name, southernBound, northernBound, westernBound, easternBound, 360.0);
    }

    /**
     * Creates a new GeographicExtent. If easternBound is lower than
     * westernBound, CTS consider that the GeographicExtent crosses the 180
     * meridian.
     *
     * @param name the name of the GeographicExtent
     * @param southernBound the southern bound of the GeographicExtent, in
     * decimal degree
     * @param northernBound the northern bound of the GeographicExtent, in
     * decimal degree
     * @param westernBound the western bound of the GeographicExtent, in decimal
     * degree
     * @param easternBound the eastern bound of the GeographicExtent, in decimal
     * degree
     * @param modulo the modulo value for the GeographicExtent
     */
    public GeographicExtent(String name,
            double southernBound, double northernBound,
            double westernBound, double easternBound, double modulo) {
        this.name = name;
        this.southernBound = southernBound;
        this.northernBound = northernBound;
        this.westernBound = westernBound;
        this.easternBound = easternBound < westernBound
                ? easternBound + modulo : easternBound;
        this.modulo = modulo;
    }

    /**
     * Return the name of this geographic area.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Return the western bound of this geographic area.
     */
    public double getWesternBound() {
        return westernBound;
    }

    /**
     * Return the eastern bound of this geographic area.
     */
    public double getEasternBound() {
        return westernBound;
    }

    /**
     * Return the southern bound of this geographic area.
     */
    public double getSouthernBound() {
        return southernBound;
    }

    /**
     * Return the northern bound of this geographic area.
     */
    public double getNorthernBound() {
        return northernBound;
    }

    /**
     * Return the modulo value (360 for an extent in degree).
     */
    public double getModulo() {
        return modulo;
    }

    /**
     * Return whether the point formed by input latitude and longitude is inside
     * this GeographicExtent or not.
     *
     * @param lat the latitude of the point to test, expressed in decimal degree
     * @param lon the longitude of the point to test, expressed in decimal
     * degree
     */
    public boolean isInside(double lat, double lon) {
        double lon_ = lon < westernBound ? lon + modulo : lon;
        if (lat < southernBound || lat > northernBound) {
            return false;
        } else return !(lon_ < westernBound) && !(lon_ > easternBound);
    }

    /**
     * Return whether coord is inside this Extent or not. For
     * <code>GeographicExtent</code>, coord must be a latitude and a longitude
     * (in this order) and in decimal degrees.
     *
     * @param coord the coordinates to test
     */
    @Override
    public boolean isInside(double[] coord) {
        return isInside(coord[0], coord[1]);
    }

    /**
     * Return a String representation of this GeographicExtent.
     */
    @Override
    public String toString() {
        return name + " lat[" + southernBound + " to " + northernBound
                + "] lon[" + westernBound + " to " + easternBound + "]";
    }
}
