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
package org.cts.op.transformation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.cts.*;
import org.cts.grid.GridShift;
import org.cts.grid.GridShiftFile;
import org.cts.op.AbstractCoordinateOperation;

/**
 * Geographic Offset by Interpolation of Gridded Data.<p>
 * The relationship between some geographical 2D coordinate reference systems
 * is available through gridded data sets of latitude and longitude offsets.
 *
 * @author Michaël Michaud
 */
public class NTv2GridShiftTransformation extends AbstractCoordinateOperation {

	////////////////////////////////////////////////////////////////////////////
	////////////////              CLASS ATTRIBUTES              ////////////////
	////////////////////////////////////////////////////////////////////////////
	private static final Identifier opId =
		new Identifier("EPSG", "9615", "NTv2 Geographic Offset", "NTv2");
	public static final int SPEED = 0;
	public static final int LOW_MEMORY = 1;
	/**
	 * if set to true, this class will use a RandomAccessFile to access the
	 * gridded data instead of loading it into memory
	 */
	private int mode = 1;	
	private String grid_file;
	private GridShiftFile gsf;
    
    
    /**
	 * NTv2GridShiftTransformation constructor.
	 * @param ntv2_gridFile file containing the description of the NTv2 grid
	 * @param precision mean precision of the geodetic transformation
	 */
	public NTv2GridShiftTransformation(String ntv2_gridFile, double precision) {
		super(opId);
		this.grid_file = ntv2_gridFile;
		this.gsf = new GridShiftFile();
		this.precision = Math.max(0.000000001, precision);
	}

	/**
	 * NTv2GridShiftTransformation constructor.
	 * @param ntv2_gridFile file containing the description of the NTv2 grid
	 */
	public NTv2GridShiftTransformation(String ntv2_gridFile) {
		super(opId);
		this.grid_file = ntv2_gridFile;
		this.gsf = new GridShiftFile();
	}

	/**
	 * Shift geographic coordinates (in decimal degrees) by an offset
	 * interpolated in a grid.
	 * @param coord coordinate to shift
	 * @throws IllegalCoordinateException if <code>coord</code> is not
	 * compatible with this <code>CoordinateOperation</code>.
	 */
	@Override
	public double[] transform(double[] coord) throws IllegalCoordinateException {
		if (coord.length < 2) {
			throw new CoordinateDimensionException(coord, 2);
		}
		GridShift gs = new GridShift();
		gs.setLatDegrees(coord[0] * 180d / Math.PI);
		gs.setLonPositiveEastDegrees(coord[1] * 180d / Math.PI);
		try {
			if (gsf == null) {
				loadGridShiftFile();
			}
			boolean withinGrid = gsf.gridShiftForward(gs);
			if (withinGrid) {
				coord[0] = gs.getShiftedLatDegrees() * Math.PI / 180d;
				coord[1] = gs.getShiftedLonPositiveEastDegrees() * Math.PI / 180d;
			}
		} catch (IOException ioe) {
			throw new CoordinateDimensionException(ioe.getMessage());
		}
		return coord;
	}

	/**
	 * Creates the inverse CoordinateOperation.
	 */
	@Override
	public CoordinateOperation inverse() throws NonInvertibleOperationException {
		return new NTv2GridShiftTransformation(grid_file, precision) {

			@Override
			public double[] transform(double[] coord) throws IllegalCoordinateException {
				if (coord.length < 2) {
					throw new CoordinateDimensionException(coord, 2);
				}
				GridShift gs = new GridShift();
				gs.setLatDegrees(coord[0] * 180d / Math.PI);
				gs.setLonPositiveEastDegrees(coord[1] * 180d / Math.PI);
				try {
					if (gsf == null) {
						loadGridShiftFile();
					}
					boolean withinGrid = gsf.gridShiftReverse(gs);
					if (withinGrid) {
						coord[0] = gs.getShiftedLatDegrees() * Math.PI / 180d;
						coord[1] = gs.getShiftedLonPositiveEastDegrees() * Math.PI / 180d;
					}
				} catch (IOException ioe) {
					throw new CoordinateDimensionException(ioe.getMessage());
				}
				return coord;
			}

			@Override
			public CoordinateOperation inverse()
				throws NonInvertibleOperationException {
				return NTv2GridShiftTransformation.this;
			}
		};
	}

	/**
	 * Load the gridshift file
	 */
	public void loadGridShiftFile() throws IOException {
		if (mode == 0) {
			InputStream is;
			is = NTv2GridShiftTransformation.class.getClassLoader().getResourceAsStream(grid_file);
			if (is == null) {
			             gsf.loadGridShiftFile(new FileInputStream(grid_file), false);
            } else {
                gsf.loadGridShiftFile(is, false);
            }
        } else if (mode == 1) {
            gsf.loadGridShiftFile(new RandomAccessFile(grid_file, "r"));

        } else;
    }

	public boolean isLoaded() {
		return gsf.isLoaded();
	}

	public void unload() throws IOException {
		gsf.unload();
	}

	public boolean setMode(int mode) throws IOException {
		if ((mode == 0 || mode == 1) && this.mode != mode) {
			this.mode = mode;
			unload();
			loadGridShiftFile();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns this Geocentric translation as a String.
	 */
	@Override
	public String toString() {
		return "NTv2 Geographic Offset (" + grid_file + ")";
	}
        
    public String getFromDatum() {
        return gsf.getFromEllipsoid().trim();
    }
    
    public String getToDatum() {
        return gsf.getToEllipsoid().trim();
    }
}

