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
package org.cts.op.transformation.grids;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.BitSet;

import org.cts.cs.GeographicExtent;

/**
 * <p>Class containing codec functions to compress or uncompress a
 * GeographicGrid in the BLEGG format (Bit Level Encoded Geographic Grid)</p>
 * <p>Format Specification :<br> 5 bytes : "BLEGG"<br> 4 bytes : integer (java),
 * block size<br> 4 bytes : integer (java), number of lines<br> 4 bytes :
 * integer (java), number of columns<br> 4 bytes : integer (java), scale
 * (terrain length = int value/scale)<br> 8 bytes : double (java), x0 (x
 * coordinate of the upper-left corner)<br> 8 bytes : double (java), y0 (y
 * coordinate of the upper-left corner)<br> 8 bytes : double (java), total width
 * of the grid (terrain coordinates)<br> 8 bytes : double (java), total height
 * of the grid (terrain coordinates)<br> 4 bytes : integer value in the first
 * line, first column<br> 4 bytes : integer value in the first line, second
 * column<br> </p> <p> n blocks follow : Ils décrivent la matrice suivant un
 * parcours en zigzag (gauche-droite pour les lignes paires et droite-gauche
 * pour les lignes impaires) commençant à la première ligne et 3ème colonne.
 * <p>Chaque valeur codée représente la différence <br> (n-(n-1)) -
 * ((n-1)-(n-2)) c'est à dire la variation de la pente </p> <p> Description d'un
 * bloc 1er octet : entier représentant le nombre de bits servant à stocker
 * chaque valeur<br> p octets : p = taille bloc x valeur stockée dans le premier
 * octet / 8<br> Chaque groupe de nbBits sert à coder une valeur de la
 * matrice.<br> Le premier groupe du premier bloc représente la 3eme colonne de
 * la premiere ligne.<br> C'est la différence des différences entre valeurs
 * successives qui est codée :<br> C3 = (valeur3-valeur2) -
 * (valeur2-valeur1)<br> Rappelons que le parcours des blocs décrit la matrice
 * en zig-zag<br> Le dernier blocs peut être légèrement plus grand que
 * nécessaire.</p>
 *
 * @author Michaël Michaud
 */
public class BleggGeographicGrid extends GeographicGrid {

    private static DecimalFormat formatter = new DecimalFormat();
    private int groupSize;

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * <p>Construct a GeographicGrid of floats from a compressed stream</p>
     *
     * @param is input stream
     */
    public BleggGeographicGrid(InputStream is) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        this.dim = 1;
        int pos = 0;
        try {
            // Reading file format signature
            byte[] bytes = new byte[5];
            dis.read(bytes);
            pos += 5;
            String signature = new String(bytes);
            if (!signature.equals("BLEGG")) {
                throw new Exception("The file is not "
                        + "a Bit Level Encoded Geographic Grid");
            }

            // Reading groupSize used for compression
            groupSize = dis.readInt();
            pos += 4;

            // Reading grid parameters
            rowNumber = dis.readInt();
            pos += 4;
            colNumber = dis.readInt();
            pos += 4;
            scale = dis.readInt();
            pos += 4;
            x0 = dis.readDouble();
            pos += 8;
            y0 = dis.readDouble();
            pos += 8;
            double gridWidth = dis.readDouble();
            pos += 8;
            double gridHeight = dis.readDouble();
            pos += 8;
            xL = x0 + gridWidth;
            yL = y0 - gridHeight;
            dx = gridWidth / (colNumber - 1);
            dy = -gridHeight / (rowNumber - 1);
            this.extent = new GeographicExtent("GG", yL, y0, x0, xL, 360.0);
            int[] intVal = new int[(rowNumber * colNumber + groupSize - 3) / groupSize * groupSize + 2];

            // Reading values of cell 0,0 and cell 0,1
            intVal[0] = dis.readInt();
            pos += 4;
            intVal[1] = dis.readInt();
            pos += 4;

            // Reading cell values
            int n; // Length of values to read (bits)
            int groupNumber = 0;
            while (dis.available() > 0) {
                n = dis.readByte();
                pos++;
                // for debug
                // System.out.print(Integer.toHexString(pos-1) + ": " + n);
                byte[] bb = new byte[n * groupSize / 8];
                // Use readFully(bb) (read(bb) do not guarantee the array is
                // fullfilled even if more data is available !!)
                dis.readFully(bb);
                pos += bb.length;
                NBitArray bitArray = new NBitArray(n, groupSize);
                bitArray.setBytes(bb);
                for (int i = 0; i < groupSize; i++) {
                    int serialNumber = 2 + groupNumber * groupSize + i;
                    intVal[serialNumber] = intVal[serialNumber - 1]
                            + intVal[serialNumber - 1]
                            - intVal[serialNumber - 2]
                            + bitArray.getValue(i);
                }
                groupNumber++;
            }
            values = new double[rowNumber][colNumber][];
            values[0][0][0] = intVal[0] / (float) scale;
            values[0][1][0] = intVal[1] / (float) scale;
            for (int i = 2; i < rowNumber * colNumber; i++) {
                int[] lc = getPos(i, rowNumber, colNumber);
                values[lc[0]][lc[1]][0] = intVal[i] / (float) scale;
            }
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * <p>Compress and write a grid of double at the bit level</p>
     *
     * @param values grid of double values. The array of array is supposed to be
     * rectangle.
     * @param scale scale factor used to transform doubles values to int : each
     * value will be considered equal to Math.rint(value*scale)
     * @param groupSize is the number of consecutive values coded with the same
     * bit number (default is 16).
     * @param os outputStream
     */
    public static void compress(double[][][] values,
            int scale,
            int groupSize,
            OutputStream os) throws Exception {

        compress(values, scale, groupSize, 0.0, 0.0,
                (double) values.length, (double) values[0].length, os);
    }

    /**
     * <p>Compress and write a grid of double at the bit level</p>
     *
     * @param values grid of double values. The array of array is supposed to be
     * rectangle.
     * @param scale scale factor used to transform doubles values to int : each
     * value will be considered equal to Math.rint(value*scale)
     * @param groupSize is the number of consecutive values coded with the same
     * bit number (default is 16).
     * @param x0 longitude origine (may be negative)
     * @param y0 latitude maximum
     * @param gridWidth in degrees
     * @param gridHeight in degrees
     * @param os output stream
     */
    public static void compress(double[][][] values,
            int scale,
            int groupSize,
            double x0,
            double y0,
            double gridWidth,
            double gridHeight,
            OutputStream os) throws Exception {
        int nbl = values.length;
        int nbc = values[0].length;
        DataOutputStream dos = new DataOutputStream(os);
        // Format signature
        // BLEGG is the acronym for Bit Level Encoded Geographic Grid
        dos.writeBytes("BLEGG");
        dos.writeInt(groupSize);
        dos.writeInt(nbl);
        dos.writeInt(nbc);
        dos.writeInt(scale);
        // Parametres de conversion en coordonnees terrain
        dos.writeDouble(x0);
        dos.writeDouble(y0);
        dos.writeDouble(gridWidth == 0.0 ? (double) nbc : gridWidth);
        dos.writeDouble(gridHeight == 0.0 ? -(double) nbc : gridHeight);
        // La longueur du tableau est égale à 2 (les deux premieres valeurs) +
        // Le plus petit multiple de groupSize contenant entierement nbl*nbc-2
        int length = (nbl * nbc + groupSize - 3) / groupSize * groupSize + 2;
        int[] val = new int[length];
        // Le tableau est rempli en parcourant le tableau de gauche à droite
        // pour les lignes paires et de droite à gauche pour les impaires
        for (int i = 0; i < nbl; i++) {
            for (int j = 0; j < nbc; j++) {
                if (i % 2 == 0) {
                    val[i * nbc + j] = (int) Math.rint(values[i][j][0] * scale);
                } else {
                    val[(i + 1) * nbc - j - 1] = (int) Math.rint(values[i][j][0] * scale);
                }
            }
        }
        formatter.applyPattern("+00;-00");

        int[] diff = new int[length];
        for (int i = 1; i < nbl * nbc; i++) {
            diff[i] = val[i] - val[i - 1];
        }

        dos.writeInt(val[0]);
        dos.writeInt(val[1]);
        int[] slope = new int[length];
        for (int i = 2; i < nbl * nbc; i++) {
            slope[i] = diff[i] - diff[i - 1];
        }

        byte[] bytes = new byte[0];
        try {
            for (int i = 0; i < (length - 2) / groupSize; i++) {
                int[] block = new int[groupSize];
                // Copy des valeurs suivantes dans un tableau
                System.arraycopy(slope, (2 + i * groupSize), block, 0, groupSize);
                // Clone le tableau et tri le clone pour déterminer les extrema
                int[] blockClone = new int[groupSize];
                System.arraycopy(block, 0, blockClone, 0, groupSize);
                Arrays.sort(blockClone);
                int maxAbs = Math.max(Math.abs(blockClone[0]),
                        Math.abs(blockClone[blockClone.length - 1]));
                // Nombre de bits necessaires = log2(maxAbs)
                // + 1 pour compenser l'arrondi
                // + 1 pour le signe
                int n = (int) (Math.max(0, Math.log(maxAbs) / Math.log(2) + 2));
                NBitArray group = new NBitArray(n, groupSize);
                for (int j = 0; j < groupSize; j++) {
                    group.setValue(block[j], j);
                }
                dos.writeByte(n);
                dos.write(group.getBytes());
            }
            dos.close();
        } catch (Exception e) {
            dos.close();
            throw e;
        }
    }

    /**
     * <p>Write a GeographicGrid in a Blegg-compressed format.</p>
     *
     * @param gg grid to write.
     * @param os OutputStream
     */
    public static void write(GeographicGrid gg,
            OutputStream os) throws Exception {

        compress(gg.getValues(), gg.getScale(), 16, gg.getX0(), gg.getY0(),
                gg.getGridWidth(), gg.getGridHeight(), os);
    }

    /**
     * Convert a serial position in the zig-zag compressed file into a
     * line/column position.
     */
    private int[] getPos(int serialNumber, int nbl, int nbc) {
        if (serialNumber >= nbl * nbc) {
            return null;
        }
        int line = serialNumber / nbc;
        int column = line % 2 == 0 ? serialNumber - line * nbc : nbc - 1 - (serialNumber - line * nbc);
        return new int[]{line, column};
    }

    /**
     * <p>Champ de bits destiné à stocker des entiers sign�s sur une largeur
     * ajustée au plus juste (0 < nombre de bits < 256).<br> Une série de n
     * valeurs comprises entre -16 et 15 pourront par exemple être stockées sur
     * n x 5 bits.<br> Les bits sont ordonnés des bits de poids faible vers les
     * bits de poids fort. Le dernier bit de chaque groupe est le bit de
     * signe.</p>
     */
    static class NBitArray {

        BitSet bitSet;
        int nbBits;
        int groupSize;

        /**
         * Constructeur
         */
        public NBitArray(int nbBits, int groupSize) throws Exception {
            if (!((groupSize * nbBits) % 8 == 0)) {
                throw new Exception("La taille d'un bloc doit �tre un multiple de 8");
            }
            this.nbBits = nbBits;
            this.groupSize = groupSize;
            bitSet = new BitSet(groupSize * nbBits);
        }

        /**
         * <p>Retourne la valeur stockée sur nbBits à la position pos.<br> La
         * fonction lit nbBits bits.</p>
         */
        public int getValue(int n) {
            return getValue(n, nbBits);
        }

        /**
         * <p>Retourne la n-ième valeur de size bits de large.<br> <ul> <li>Le
         * premier bit vaut 1 si vrai</li> <li>Le second vaut 2 si vrai</li>
         * <li>Le troisieme vaut 4 si vrai</li> <li>...</li> <li>Le dernier est
         * le bit de signe (negatif si vrai)</li> </u>
         */
        public int getValue(int n, int size) {
            if (size == 0) {
                return 0;
            }
            int ret = 0;
            int powerOf2 = 1;
            for (int i = 0; i < (size - 1); i++) {
                if (bitSet.get(n * size + i)) {
                    ret += powerOf2;
                }
                powerOf2 *= 2;
            }
            if (bitSet.get((n + 1) * size - 1)) {
                ret = -ret;
            }
            return ret;
        }

        /**
         * <p>Change le n-ième groupe de nbBits bits en lui donnant la valeur
         * value. Seul les size bits affectés au codage de cette valeur sont
         * changés.</p>
         */
        public void setValue(int value, int pos) {
            setValue(value, pos, nbBits);
        }

        /**
         * <p>Change le n-ième group de size bits en lui donnant la valeur
         * value. Seul les size bits affectés au codage de cette valeur sont
         * changés.</p>
         */
        public void setValue(int value, int n, int size) {
            int powerOf2 = 1;
            int abs = Math.abs(value);
            for (int i = 0; i < (size - 1); i++) {
                if ((abs & powerOf2) == powerOf2) {
                    bitSet.set(n * size + i);
                }
                powerOf2 *= 2;
            }
            if (value < 0) {
                bitSet.set((n + 1) * size - 1);
            }
        }

        /**
         * <p>Transforme le BitSet en tableau d'octets pour en faciliter la
         * persistance.</p>
         */
        public byte[] getBytes() {
            byte[] bytes = new byte[groupSize * nbBits / 8];
            for (int i = 0; i < bitSet.length(); i++) {
                if (bitSet.get(i)) {
                    if (i % 8 == 0) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 1);
                    } else if (i % 8 == 1) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 2);
                    } else if (i % 8 == 2) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 4);
                    } else if (i % 8 == 3) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 8);
                    } else if (i % 8 == 4) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 16);
                    } else if (i % 8 == 5) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 32);
                    } else if (i % 8 == 6) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | 64);
                    } else if (i % 8 == 7) {
                        bytes[i / 8] = (byte) (bytes[i / 8] | -128);
                    }
                }
            }
            return bytes;
        }

        /**
         * Convert the byte array into a BitSet in order to de-serialize an
         * encoded file
         */
        public void setBytes(byte[] bytes) {
            for (int i = 0; i < bytes.length; i++) {
                if ((bytes[i] & 1) == 1) {
                    bitSet.set(i * 8 + 0);
                }
                if ((bytes[i] & 2) == 2) {
                    bitSet.set(i * 8 + 1);
                }
                if ((bytes[i] & 4) == 4) {
                    bitSet.set(i * 8 + 2);
                }
                if ((bytes[i] & 8) == 8) {
                    bitSet.set(i * 8 + 3);
                }
                if ((bytes[i] & 16) == 16) {
                    bitSet.set(i * 8 + 4);
                }
                if ((bytes[i] & 32) == 32) {
                    bitSet.set(i * 8 + 5);
                }
                if ((bytes[i] & 64) == 64) {
                    bitSet.set(i * 8 + 6);
                }
                if ((bytes[i] & -128) == -128) {
                    bitSet.set(i * 8 + 7);
                }
            }
        }

        @Override
        public String toString() {
            return "Champ de " + groupSize + " x " + nbBits + " bits :\n"
                    + bitSet.toString();
        }
    }
}