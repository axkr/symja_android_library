/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.hyperbolic;

import java.math.BigDecimal;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.powers.Pow2;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.transcendental.Ln;

import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Inverse hyperbolic tangens function.
 * @author Tilman Neumann
 */
public class ArcTanH {

	/**
	 * atanh(x) implemented by ln() formula.
	 * @param x real argument with |x| < 1
	 * @param scale wanted accuracy in after-comma digits
	 * @return atanh(x)
	 */
	public static BigDecimal atanh(BigDecimal x, Scale scale) {
		if (x.abs().compareTo(F_1)>=0) throw new ArithmeticException("atanh(x) requires |x|<1 but x=" + x);
		BigDecimal fraction = BigDecimalMath.divide(F_1.add(x), F_1.subtract(x), scale);
		BigDecimal lnTerm = Ln.ln(fraction, scale);
		return Pow2.divPow2(lnTerm, 1);
	}
}
