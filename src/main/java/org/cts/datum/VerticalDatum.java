/*
 * Coordinate Transformations Suite (abridged CTS)  is a library developped to 
 * perform Coordinate Transformations using well known geodetic algorithms 
 * and parameter sets. 
 * Its main focus are simplicity, flexibility, interoperability, in this order.
 *
 * This library has been originally developed by Michaël Michaud under the JGeod
 * name. It has been renamed CTS in 2009 and shared to the community from 
 * the Atelier SIG code repository.
 * 
 * Since them, CTS is supported by the Atelier SIG team in collaboration with Michaël 
 * Michaud.
 * The new CTS has been funded  by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-08-VILL-0005-01 and the regional council 
 * "Région Pays de La Loire" under the projet SOGVILLE (Système d'Orbservation 
 * Géographique de la Ville).
 *
 * CTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * CTS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CTS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <https://github.com/irstv/cts/>
 */
package org.cts.datum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cts.op.CoordinateOperation;
import org.cts.Identifier;
import org.cts.cs.GeographicExtent;
import org.cts.op.Identity;
import org.cts.op.transformation.Altitude2EllipsoidalHeight;

/**
 * <p>Vertical datum are used to determine elevation. They are generally based
 * upon a gravity model.</p>
 *
 * @author Michaël Michaud, Jules Party
 */
public class VerticalDatum extends AbstractDatum {

    private final static Map<Identifier, VerticalDatum> datums =
            new HashMap<Identifier, VerticalDatum>();
    /**
     * WGS84VD stands for WGS84 Vertical Datum. This not a real datum, but a
     * reference used to transform 3D Ellipsoidal coordinates into coordinates
     * based on a compound Datum made of a Geodetic datum + a Vertical Datum.
     */
    public final static VerticalDatum WGS84VD = new VerticalDatum(
            new Identifier(VerticalDatum.class, "WGS84 Ellipsoid Surface"),
            GeographicExtent.WORLD,
            "Surface of the reference Ellipsoid for WGS 1984",
            "1984", Type.ELLIPSOIDAL, null, GeodeticDatum.WGS84);
    /**
     * Nivellement général de la France - IGN69. It is the main vertical datum
     * used in France.
     */
    public final static VerticalDatum IGN69 = new VerticalDatum(
            new Identifier("EPSG", "5119", "Nivellement general de la France - IGN69", "IGN69"),
            new GeographicExtent("France", 42, 51.5, -5.5, 8.5),
            "Mean sea level at Marseille.",
            "1969", Type.GEOIDAL, "RAF09.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN78 = new VerticalDatum(
            new Identifier("EPSG", "5120", "Nivellement general de la France - IGN78", "IGN78"),
            new GeographicExtent("Corse (France)", 41.2, 43.2, 8.41666666666666, 9.71666666666666),
            "", "", Type.GEOIDAL, "RAC09.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN88GTBT = new VerticalDatum(
            new Identifier("EPSG", "5155", "Geoide géométrique pour Grande-Terre & Basse-Terre (EGM2008 + points GPS nivelés)", "IGN88GTBT"),
            new GeographicExtent("Guadeloupe", 15.875, 16.625, -61.9, -61.075),
            "", "", Type.GEOIDAL, "gg10_gtbt.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN92LD = new VerticalDatum(
            new Identifier("EPSG", "5212", "Geoide géométrique pour La Désirade (EGM2008 + points GPS nivelés)", "IGN92LD"),
            new GeographicExtent("La Désirade", 16.25, 16.4, -61.2, -60.75),
            "", "", Type.GEOIDAL, "gg10_ld.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN88LS = new VerticalDatum(
            new Identifier("EPSG", "5210", "Geoide géométrique pour Les Saintes (EGM2008 + points GPS nivelés)", "IGN88LS"),
            new GeographicExtent("Les Saintes", 15.8, 15.925, -61.7, -61.475),
            "", "", Type.GEOIDAL, "gg10_ls.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN87MART = new VerticalDatum(
            new Identifier("EPSG", "5154", "Martinique 1987", "IGN87MART"),
            new GeographicExtent("Martinique", 14.3, 15, -61.3, -60.725),
            "", "", Type.GEOIDAL, "gg10_mart.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN88MG = new VerticalDatum(
            new Identifier("EPSG", "5211", "Geoide géométrique pour Marie-Galante (EGM2008 + points GPS nivelés)", "IGN88MG"),
            new GeographicExtent("Marie-Galante", 15.8, 16.125, -61.4, -61.075),
            "", "", Type.GEOIDAL, "gg10_mg.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN88SB = new VerticalDatum(
            new Identifier("EPSG", "5213", "Geoide géométrique pour Saint-Barthélémy (EGM2008 + points GPS nivelés)", "IGN88SB"),
            new GeographicExtent("Saint-Barthélémy", 17.8, 18.025, -63, -62.725),
            "", "", Type.GEOIDAL, "gg10_sb.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN88SM = new VerticalDatum(
            new Identifier("EPSG", "5214", "Geoide géométrique pour Saint-Martin (EGM2008 + points GPS nivelés)", "IGN88SM"),
            new GeographicExtent("Saint-Martin", 18, 18.2, -63.2, -62.9),
            "", "", Type.GEOIDAL, "gg10_sm.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum NGG77GUY = new VerticalDatum(
            new Identifier("EPSG", "5153", "Nivellement General Guyanais 1977", "NGG77GUY"),
            new GeographicExtent("Guyane", 2, 6, -55, -51),
            "", "", Type.GEOIDAL, "ggguy00.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum SHOM53 = new VerticalDatum(
            new Identifier("EPSG", "5191", "Mayotte SHOM 1953 dans RGM04", "SHOM53"),
            new GeographicExtent("Mayotte", -13.095, -12.42, 44.91, 45.405),
            "", "", Type.GEOIDAL, "ggm04v1.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGN62KER = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Géoïde géométrique Kerguelen (EGM2008 + Points GPS nivelés)", "IGN62KER"),
            new GeographicExtent("Kerguelen", 67, 71, -50.5, -48),
            "", "", Type.GEOIDAL, "ggker08v2.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum DANGER50 = new VerticalDatum(
            new Identifier("EPSG", "5190", "Géoïde géométrique Saint Pierre et Miquelon (EGM96 + Points GPS nivelés)", "DANGER50"),
            new GeographicExtent("Saint Pierre et Miquelon", -56.52, -55.9350, 46.485, 47.295),
            "", "", Type.GEOIDAL, "ggspm06v1.txt", GeodeticDatum.RGF93);
    public final static VerticalDatum BORASAU01 = new VerticalDatum(
            new Identifier("EPSG", "5202", "Bora Bora SAU 2001", "BORASAU01"),
            new GeographicExtent("Bora", -152, -151.5, -16.75, -16.25),
            "", "", Type.GEOIDAL, "ggpf02-Bora.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum FAKARAVA = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Fakarava dans RGPF", "FAKARAVA"),
            new GeographicExtent("Fakarava", -145.9, -145.3, -16.65, -15.95),
            "", "", Type.GEOIDAL, "ggpf08-Fakarava.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum GAMBIER = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Gambier vers RGPF", "GAMBIER"),
            new GeographicExtent("Gambier", -135.25, -134.75, -23.4, -22.9),
            "", "", Type.GEOIDAL, "ggpf08-Gambier.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum HAO = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynesie : Hao vers RGPF", "HAO"),
            new GeographicExtent("Hao", -141.2, -140.55, -18.55, -17.95),
            "", "", Type.GEOIDAL, "ggpf08-Hao.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum HIVAOA = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : HIVA OA dans RGPF", "HAO"),
            new GeographicExtent("Hao", -139.25, -138.675, -9.9, -9.6),
            "", "", Type.GEOIDAL, "ggpf05-HivaOa.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum HUAHINESAU01 = new VerticalDatum(
            new Identifier("EPSG", "5200", "Huahine SAU 2001", "HUAHINESAU01"),
            new GeographicExtent("Huahine", -151.5, -150.75, -17, -16.5),
            "", "", Type.GEOIDAL, "ggpf02-Huahine.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum IGNTAHITI66 = new VerticalDatum(
            new Identifier("EPSG", "5196", "Polynésie : IGN TAHITI 1966 dans RGPF", "IGNTAHITI66"),
            new GeographicExtent("Tahiti", -149.69, -149, -18, -17),
            "", "", Type.GEOIDAL, "ggpf10-Tahiti.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum MAIAO01 = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : MAIAO 2001 dans RGPF", "MAIAO01"),
            new GeographicExtent("Maiao", -150.75, -150.5, -17.75, -17.5),
            "", "", Type.GEOIDAL, "ggpf02-Maiao.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum MATAIVA = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Mataiva vers RGPF", "MATAIVA"),
            new GeographicExtent("Mataiva", -148.8, -148.55, -14.95, -14.8),
            "", "", Type.GEOIDAL, "ggpf08-Mataiva.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum MAUPITISAU01 = new VerticalDatum(
            new Identifier("EPSG", "5199", "Maupiti SAU 2001", "MAUPITISAU01"),
            new GeographicExtent("Maupiti", -152.5, -152, -16.75, -16.25),
            "", "", Type.GEOIDAL, "ggpf02-Maupiti.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum MOOREASAU81 = new VerticalDatum(
            new Identifier("EPSG", "5197", "Moorea SAU 1981", "MOOREASAU81"),
            new GeographicExtent("Moorea", -150.05, -149.65, -17.7, -17.35),
            "", "", Type.GEOIDAL, "ggpf10-Moorea.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum NUKUHIVA = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : NUKU HIVA ALTI dans RGPF", "NUKUHIVA"),
            new GeographicExtent("Nuku Hiva", -140.3, -139.9, -9, -8.675),
            "", "", Type.GEOIDAL, "ggpf05-Nuku.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum RAIATEASAU01 = new VerticalDatum(
            new Identifier("EPSG", "5198", "Raiatea SAU 2001", "RAIATEASAU01"),
            new GeographicExtent("Raiatea", -151.75, -151.25, -17, -16.5),
            "", "", Type.GEOIDAL, "ggpf02-Raiatea.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum RAIVAVAE = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Raivavae vers RGPF", "RAIVAVAE"),
            new GeographicExtent("Raicvavae", -147.8, -147.5, -24, -23.75),
            "", "", Type.GEOIDAL, "ggpf08-Raivavae.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum REAO = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Reao vers RGPF", "REAO"),
            new GeographicExtent("Reao", -136.55, -136.2, -18.65, -18.4),
            "", "", Type.GEOIDAL, "ggpf08-Reao.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum RURUTU = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Rurutu vers RGPF", "RURUTU"),
            new GeographicExtent("Rurutu", -151.45, -151.25, -22.6, -22.35),
            "", "", Type.GEOIDAL, "ggpf08-Rurutu.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum TAHAASAU01 = new VerticalDatum(
            new Identifier("EPSG", "5201", "Tahaa SAU 2001", "TAHAASAU01"),
            new GeographicExtent("Tahaa", -151.75, -151.25, -16.75, -16.5),
            "", "", Type.GEOIDAL, "ggpf02-Tahaa.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum TIKEHAU = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Tikehau vers RGPF", "TIKEHAU"),
            new GeographicExtent("Tikehau", -148.35, -147.95, -15.2, -14.85),
            "", "", Type.GEOIDAL, "ggpf08-Tikehau.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum TUBUAI = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Tubuai vers RGPF", "TUBUAI"),
            new GeographicExtent("Tubuai", -149.65, -149.3, -23.5, -23.25),
            "", "", Type.GEOIDAL, "ggpf08-Tubuai.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum TUPAI01 = new VerticalDatum(
            new Identifier(Identifier.UNKNOWN, Identifier.UNKNOWN, "Polynésie : Tubuai vers RGPF", "TUPAI01"),
            new GeographicExtent("Tupai", -152, -151.75, -16.5, -16),
            "", "", Type.GEOIDAL, "ggpf02-Tupai.mnt", GeodeticDatum.RGF93);
    public final static VerticalDatum RAR07 = new VerticalDatum(
            new Identifier("EPSG", "5156", "Reunion 1989", "RAR07"),
            new GeographicExtent("Reunion", 55.14, 55.94, -21.5, -20.75),
            "", "", Type.GEOIDAL, "RAR07.mnt", GeodeticDatum.RGF93);

    /**
     * Vertical Datum classification based on the surface type.
     */
    public static enum Type {

        GEOIDAL, //WKT code 2005
        ELLIPSOIDAL, //WKT code 2002
        DEPTH, //WKT code 2006
        BAROMETRIC, //WKT code 2003
        ORTHOMETRIC, //WKT code 2001
        OTHER_SURFACE //WKT code 2000
    };
    /**
     * The type of this vertical datum. Default is "geoidal".
     */
    private final Type type;
    /**
     * The operation converting altitude of the vertical datum into ellipsoidal
     * height.
     */
    private CoordinateOperation alti2ellpsHeight;
    /**
     * The ellipsoid associated with the vertical datum. It can be the ellipsoid
     * defining th ellipsoidal height or the ellipsoid of the geodetic datum
     * used by the grid transformation binding altitude and height.
     */
    private final Ellipsoid ellps;

    /**
     * Creates a new Datum.
     *
     * @param name name of this vertical datum
     */
    public VerticalDatum(String name) {
        super(new Identifier(VerticalDatum.class, name),
                GeographicExtent.WORLD, null, null);
        this.type = Type.GEOIDAL;
        this.registerDatum();
        this.alti2ellpsHeight = null;
        this.ellps = null;
        //addCoordinateOperation(WGS84VD, altitude2EllipsoidalHeight);
    }

    /**
     * Creates a new VerticalDatum.
     *
     * @param identifier identifier.
     * @param extent this datum extension
     * @param origin origin decription this datum
     * @param epoch realization epoch of this datum
     * @param type the type of coordinate stored in this VerticalDatum
     * @param altitudeGrid the name of the grid file used to convert altitude in
     * ellipsoidal height
     * @param gd the GeodeticDatum associated to the grid
     */
    public VerticalDatum(Identifier identifier, GeographicExtent extent,
            String origin, String epoch, Type type, String altitudeGrid, GeodeticDatum gd) {
        super(identifier, extent, origin, epoch);
        this.type = type;
        if (gd != null && altitudeGrid != null) {
            try {
                this.alti2ellpsHeight = new Altitude2EllipsoidalHeight(altitudeGrid, gd);
            } catch (Exception ex) {
                Logger.getLogger(VerticalDatum.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.ellps = gd.getEllipsoid();
        } else if (gd != null && type == Type.ELLIPSOIDAL) {
            this.alti2ellpsHeight = Identity.IDENTITY;
            this.ellps = gd.getEllipsoid();
        } else {
            this.alti2ellpsHeight = null;
            this.ellps = null;
        }
        this.registerDatum();
    }

    /**
     * Return the type of this vertical datum. Default is "geoidal".
     */
    public Type getType() {
        return type;
    }

    /**
     * Return the operation converting altitude of the vertical datum into
     * ellipsoidal height.
     */
    public CoordinateOperation getAltiToEllpsHeight() {
        return alti2ellpsHeight;
    }

    /**
     * Register a datum in {@link HashMap} {@code datums} using its
     * {@link Identifier} as a key.
     */
    private void registerDatum() {
        datums.put(getIdentifier(), this);
    }

    /**
     * Return a collection of all the registered vertical datums.
     */
    public static Collection<VerticalDatum> getAvailableDatums() {
        return datums.values();
    }

    /**
     * Return the Datum with idEPSG identifier.
     */
    public static VerticalDatum getDatum(Identifier identifier) {
        return datums.get(identifier);
    }

    /**
     * @see Datum#getEllipsoid()
     */
    public Ellipsoid getEllipsoid() {
        return ellps;
    }

    /**
     * @see Datum#getToWGS84()
     */
    public CoordinateOperation getToWGS84() {
        if (alti2ellpsHeight instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight eH2A = (Altitude2EllipsoidalHeight) alti2ellpsHeight;
            return eH2A.getAssociatedDatum().getToWGS84();
        }
        return null;
    }

    /**
     * @see Datum#getPrimeMeridian()
     */
    public PrimeMeridian getPrimeMeridian() {
        if (alti2ellpsHeight instanceof Altitude2EllipsoidalHeight) {
            Altitude2EllipsoidalHeight eH2A = (Altitude2EllipsoidalHeight) alti2ellpsHeight;
            return eH2A.getAssociatedDatum().getPrimeMeridian();
        }
        return null;
    }
}
