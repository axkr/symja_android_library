package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.INumber;

/**
 * 
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/gamma.js">gamma.js</a>
 */
public class GammaJS {
	public static Complex beta(Complex x, Complex y) {
		return Arithmetic.lanczosApproxGamma(x).multiply(Arithmetic.lanczosApproxGamma(y))
				.divide(Arithmetic.lanczosApproxGamma(x.add(y)));
	}

	public static Complex beta(Complex x, Complex y, Complex z) {
		return x.pow(y).multiply(HypergeometricJS.hypergeometric2F1(y, new Complex(1.0).subtract(z), y.add(1.0), x))
				.divide(y);
	}

	public static double beta(double x, double y) {
		return Gamma.gamma(x) * Gamma.gamma(y) / Gamma.gamma(x + y);
	}

	public static double beta(double x, double y, double z) {
		return Math.pow(x, y) * HypergeometricJS.hypergeometric2F1(y, 1.0 - z, y + 1.0, x) / y;
	}

	public static INumber incompleteBeta(double x, double y, double z) {
		if (x == -1 || x > 1) {
			return F.complexNum(beta(new Complex(x), new Complex(y), new Complex(z)));
		}
		return F.num(beta(x, y, z));
	}

	public static Complex fresnelS(Complex x) {

		Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
				new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
		Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
				new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));

		Complex result = x.multiply(m1.subtract(m2)).multiply(new Complex(0, -0.5));
		return result;

	}

	public static Complex fresnelC(Complex x) {

		Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
				new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
		Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
				new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));

		Complex result = x.multiply(m1.add(m2)).multiply(0.5);
		return result;

	}

}
