/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.lab4inf.math.functions;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;

import org.matheclipse.core.interfaces.INum;

import de.lab4inf.math.util.ChebyshevExpansion;

/**
 * Cosine Integral approximation Ci(x) via Chebyshev expansion.
 * 
 * <pre>
 *                        x
 *                       / 
 *  Ci(x) = &gamma; + ln|x| + / [cos(t)-1]/t dt = f(x)*sin(x) - g(x)*cos(x)
 *                     /
 *                    0
 * </pre>
 * 
 * The approximation is from:
 * 
 * <pre>
 * R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967).
 * </pre>
 * 
 * with an accuracy of ~5.E-12.
 * 
 * @author nwulff
 * @since 18.09.2009
 * @version $Id: CosineIntegral.java,v 1.11 2015/01/29 15:13:03 nwulff Exp $
 *
 */
public class CosineIntegral extends AbstractSiCiIntegrals {
	/** Eulers constant. */
	// private static final double G = Constants.EULER; // 0.577215664901533;
	/** Coefficients from R. Bulirsch, Numerische Mathematik 9, pp 380-385 (1967). */
	private static final double[] A = { +29.985178735626818, -19.386124096607770, +12.741870869758071, -8.107903970562531,
			+4.862022348500627, -2.497505088539025, +1.008660787358110, -0.312080924825428, +0.074678255294576, -0.014110865253535,
			+0.002152046752074, -0.000270212331184, +0.000028416945498, -0.000002540125611, +0.000000195437144, -0.000000013084020,
			+0.000000000769379, -0.000000000040066, +0.000000000001861, -0.000000000000078, +0.000000000000003 };

	static {
		// first terms have to be halved
		A[0] /= 2;
		// double c1 = 0, c2 = 0;
		// for (int i = 0; i < A.length; i++) {
		// c1 += A[i];
		// c2 += abs(A[i]);
		// }
		// getLogger().info(format("Cosine integral check sum A[k]=%.15f; sum |A[k]|=%.15f", c1, c2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.lab4inf.math.Function#f(double[])
	 */
	@Override
	public double f(final double... x) {
		return ci(x[0]);
	}

	/**
	 * Approximation of the cosine integral.
	 * 
	 * @param x
	 *            the argument to be positive
	 * @return Ci(x)
	 */
	public static double ci(final double x) {
		double y, z = abs(x);
		if (z < AK) {
			double w = z / AK;
			double u = 2 * w * w - 1;
			y = ChebyshevExpansion.cheby(u, A) * w * w;
			y = INum.EulerGamma + log(z) - y;
		} else {
			y = auxf(z) * sin(z) - auxg(z) * cos(z);
		}
		if (x < 0) {
			y = -y;
		}
		return y;
	}
}
