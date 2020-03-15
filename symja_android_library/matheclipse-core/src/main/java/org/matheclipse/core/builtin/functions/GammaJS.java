package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.INumber;

import de.lab4inf.math.Function;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.ContinuedFraction;

import static de.lab4inf.math.functions.Gamma.gamma;
import static de.lab4inf.math.functions.Gamma.lngamma;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.matheclipse.core.builtin.functions.HypergeometricJS.*;

/**
 * 
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/gamma.js">gamma.js</a>
 */
public class GammaJS {
	private static final double DEFAULT_EPSILON = 1.E-14;
	private static final int MAX_ITERATIONS = 1500;

	/**
	 * Internal helper class for the continued fraction.
	 */
	static class RegularizedGammaFraction extends ContinuedFraction {
		private final double a;

		public RegularizedGammaFraction(final double a) {
			this.a = a;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.lab4inf.math.util.ContinuedFraction#getA0(double)
		 */
		@Override
		protected double getA0(final double x) {
			return getAn(0, x);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.lab4inf.math.util.ContinuedFraction#getAn(int, double)
		 */
		@Override
		protected double getAn(final int n, final double x) {
			return (2.0 * n + 1.0) - a + x;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.lab4inf.math.util.ContinuedFraction#getBn(int, double)
		 */
		@Override
		protected double getBn(final int n, final double x) {
			return n * (a - n);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
		 */
		@Override
		public void accept(final Visitor<Function> visitor) {
			visitor.visit(this);
		}
	}

	/**
	 * Calculate the regularized gamma function P(a,x), with epsilon precision using maximal max iterations. The
	 * algorithm uses series expansion 6.5.29 and formula 6.5.4 from A&amp;ST.
	 *
	 * @param a
	 *            the a parameter.
	 * @param x
	 *            the value.
	 * @param eps
	 *            the desired accuracy
	 * @param max
	 *            maximum number of iterations to complete
	 * @return the regularized gamma function P(a,x)
	 */
	private static double regGammaP(final double a, final double x, final double eps, final int max) {
		double ret = 0;
		if ((a <= 0.0) || (x < 0.0)) {
			throw new IllegalArgumentException(String.format("P(%f,%f)", a, x));
		}
		if (a >= 1 && x > a) {
			ret = 1.0 - regGammaQ(a, x, eps, max);
		} else if (x > 0) {
			// calculate series expansion A&S 6.5.29
			int n = 1;
			final double ea = exp(-x + (a * log(x)) - lngamma(a));
			final double err = eps;
			double an = 1.0 / a, so, sn = an;
			do {
				so = sn;
				an *= x / (a + n);
				sn += an;
			} while (!hasConverged(sn, so, err, ++n, max));
			// do the transformation 6.5.4
			ret = ea * sn;
		}
		return ret;
	}

	/**
	 * Calculate the regularized gamma function Q(a,x) = 1 - P(a,x), with epsilon precision using maximal maxIterations.
	 * The algorithm uses a continued fraction until convergence is reached.
	 *
	 * @param a
	 *            the a parameter.
	 * @param x
	 *            the value.
	 * @param epsilon
	 *            the desired accuracy
	 * @param maxIterations
	 *            maximum number of iterations to complete
	 * @return the regularized gamma function Q(a,x)
	 */
	private static double regGammaQ(final double a, final double x, final double epsilon, final int maxIterations) {
		double ret = 0;

		if ((a <= 0.0) || (x < 0.0)) {
			throw new IllegalArgumentException(String.format("Q(%f,%f)", a, x));
		}
		if (x < a || a < 1.0) {
			ret = 1.0 - regGammaP(a, x, epsilon, maxIterations);
		} else if (x > 0) {
			// create continued fraction analog to A&S 6.5.31 / 26.4.10 ?
			// this implementation is due to Wolfram research
			// http://functions.wolfram.com/GammaBetaErf/GammaRegularized/10/0003/
			final double ea = exp(-x + (a * log(x)) - lngamma(a));
			final double err = epsilon;
			final ContinuedFraction cf = new RegularizedGammaFraction(a);
			ret = 1.0 / cf.evaluate(x, err, maxIterations);
			ret *= ea;
		}
		return ret;
	}

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

	public static Complex gamma(Complex x) {
		return Arithmetic.lanczosApproxGamma(x);
	}

	/**
	 * Incomplete gamma function.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double gamma(double x, double y) {
		return org.hipparchus.special.Gamma.gamma(x) * regGammaQ(x, y, DEFAULT_EPSILON, MAX_ITERATIONS);
	}

	/**
	 * Incomplete gamma function.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Complex gamma(Complex x, Complex y) {
		// patch lower end or evaluate exponential integral independently
		if (Complex.equals(x, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
			// taylorSeries => (-EulerGamma - Log(y)) + x - 1/4 * x^2 + 1/18 * x^3 - 1/96 * x^4 + 1/600 * x^5
			Complex result = y.log().add(ConstantDefinitions.EULER_GAMMA).negate();
			double[] coeff = new double[] { 1.0, -0.25, 1.0 / 18.0, -1.0 / 96.0, 1.0 / 600.0 };
			Complex yPow = y;
			for (int i = 0; i < coeff.length; i++) {
				result = result.add(yPow.multiply(coeff[i]));
				yPow = yPow.multiply(y);
			}
			return result;
		}

		return gamma(x).subtract(gamma(x, Complex.ZERO, y));
	}

	public static Complex gamma(Complex x, Complex y, Complex z) {
		if (!Complex.equals(y, Complex.ZERO, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
			return gamma(x, Complex.ZERO, z).subtract(gamma(x, Complex.ZERO, y));
		}

		return z.pow(x).multiply(x.reciprocal()).multiply(hypergeometric1F1(x, x.add(Complex.ONE), z.negate()));
	}

	public static Complex expIntegral(Complex x) {
		Complex result = gamma(Complex.ZERO, x.negate()).negate()
				.add(x.log().subtract(x.reciprocal().log()).multiply(0.5)).add(x.negate().log().negate());

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex logIntegral(Complex x) {
		Complex result = gamma(Complex.ZERO, x.log().negate()).negate()
				.add(x.log().log().subtract(x.log().reciprocal().log()).multiply(0.5))
				.add(x.log().negate().log().negate());

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex sinIntegral(Complex x) {
		Complex ix = Complex.I.multiply(x);

		Complex result = new Complex(0, 0.5).multiply(gamma(Complex.ZERO, ix.negate())
				.add(gamma(Complex.ZERO, ix).negate()).add(ix.negate().log()).add(ix.log().negate()));

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex cosIntegral(Complex x) {
		Complex ix = Complex.I.multiply(x);

		Complex result = x.log().subtract(gamma(Complex.ZERO, ix.negate()).add(gamma(Complex.ZERO, ix))
				.add(ix.negate().log()).add(ix.log()).multiply(0.5));

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex sinhIntegral(Complex x) {
		Complex result = gamma(Complex.ZERO, x).add(gamma(Complex.ZERO, x.negate()).negate()).add(x.log())
				.add(x.negate().log().negate()).multiply(0.5);

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex coshIntegral(Complex x) {
		Complex result = gamma(Complex.ZERO, x)
				.add(gamma(Complex.ZERO, x.negate()).add(x.log().negate()).add(x.negate().log()).multiply(-0.5));

		// if ( isComplex(x) ) return result;
		// return result.re;
		return result;
	}

	public static Complex expIntegralEn(Complex n, Complex x) {
		return x.pow(n.subtract(1.0)).multiply(gamma(Complex.ONE.subtract(n), x));
	}
}
