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

import de.tilman_neumann.jml.powers.Pow;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.roots.SqrtReal;
import de.tilman_neumann.jml.transcendental.Ln;

import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Inverse hyperbolic sinus function.
 * @author Tilman Neumann
 */
public class ArcSinH {

	/**
	 * y = asinh(x) implemented by ln() formula, for all real x.
	 * 
	 * @param x real argument
	 * @param scale wanted accuracy in after-comma digits
	 * @return y = asinh(x), y elemOf (-inf, +inf).
	 */
	public static BigDecimal asinh(BigDecimal x, Scale scale) {
		if (x.equals(F_0)) return F_0;
		BigDecimal xSquare_plus1 = Pow.pow(x, 2, scale).add(F_1);
		BigDecimal sqrtTerm = SqrtReal.sqrt(xSquare_plus1, scale);
		BigDecimal angle = Ln.ln(x.add(sqrtTerm), scale);
		return angle;
	}
}
