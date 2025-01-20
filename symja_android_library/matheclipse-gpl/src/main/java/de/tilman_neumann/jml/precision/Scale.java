/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.precision;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Immutable class for precision statements in after-floating point decimal digits.
 * @author Tilman Neumann
 */
public class Scale implements Comparable<Scale> {
	private static final boolean DEBUG = false;
	
	private static final int NUMBER_OF_STORED_SCALES = 5000;
	private static Scale[] STORED_POSITIVE_SCALES;
	private static Scale[] STORED_NEGATIVE_SCALES;
	static {
		STORED_POSITIVE_SCALES = new Scale[NUMBER_OF_STORED_SCALES];
		STORED_NEGATIVE_SCALES = new Scale[NUMBER_OF_STORED_SCALES];
		for (int i=0; i<NUMBER_OF_STORED_SCALES; i++) {
			STORED_POSITIVE_SCALES[i] = new Scale(i);
			STORED_NEGATIVE_SCALES[i] = new Scale(-i);
		}
	}
	
	private static final Logger LOG = LogManager.getLogger(Scale.class);
	
	private int digits;

	private Scale(int digits) {
		this.digits = digits;
	}
	
	public static Scale valueOf(int digits) {
		if (Math.abs(digits) < NUMBER_OF_STORED_SCALES) {
			return digits>=0 ? STORED_POSITIVE_SCALES[digits] : STORED_NEGATIVE_SCALES[-digits];
		}
		return new Scale(digits);
	}
	
	public static Scale of(float x) {
		// floats and doubles usually work with the full mantissa so they have
		// constant precision most of the time, but their precision drops when
		// the exponent has reached its maximum. the smallest representable
		// non-zero value has precision 1 and scale -ZERO_X_MAGNITUDE-1,
		// and zero has precision 0 and scale -ZERO_X_MAGNITUDE
		// -> the scale must be capped at -ZERO_X_MAGNITUDE (X = FLOAT or DOUBLE)
		return Scale.valueOf( Math.min(Precision.FLOAT_PRECISION - Magnitude.of(x), -Magnitude.ZERO_FLOAT_MAGNITUDE) );
	}

	public static Scale of(double x) {
		// see comment above
		return Scale.valueOf( Math.min(Precision.DOUBLE_PRECISION - Magnitude.of(x), -Magnitude.ZERO_DOUBLE_MAGNITUDE) );
	}

	public static Scale of(BigDecimal x) {
		return Scale.valueOf(x.scale());
	}

	public Scale add(int n) {
		return Scale.valueOf(digits + n);
	}
	
	public Scale multiply(int multiplier) {
		return valueOf(digits * multiplier);
	}

	public BigDecimal applyTo(BigDecimal x) {
		if (DEBUG) LOG.debug("x.scale()=" + x.scale() + ", digits=" + digits + ", x=" + x);
		if (x.scale() > digits) {
			return x.setScale(digits, RoundingMode.HALF_EVEN);
		}
		// keep x-scale if smaller or equal
		return x;
	}

	/**
	 * The error a computation with this scale should not exceed.
	 * This is just half of an ulp for values with this scale, thus
	 * computation results should be correct to this scale after rounding.
	 * @return maximum allowed error
	 */
	public BigDecimal getErrorBound() {
		return new BigDecimal(I_5, digits+1);
	}
	
	public int digits() {
		return digits;
	}

	public String toString() {
		return "scale " + digits;
	}

	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof Scale)) return false;
		return this.digits == ((Scale) o).digits;
	}

	@Override
	public int hashCode() {
		return digits;
	}

	@Override
	public int compareTo(Scale other) {
		return this.digits - other.digits;
	}
}
