package org.matheclipse.core.builtin.functions;

import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.analysis.solvers.BisectionSolver;
import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.math.DoubleMath;

import de.lab4inf.math.functions.Bessel;

/**
 * 
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/bessel.js">bessel.js</a>
 */
public class BesselJS {

	public static Complex besselJ(double n, double x) {
		if (DoubleMath.isMathematicalInteger(n) && n < 0.0) {
			return besselJ(-n, x).multiply(new Complex(-1.0).pow(n));
		}
		if (!DoubleMath.isMathematicalInteger(n) && x < 0.0) {
			return besselJ(new Complex(n), new Complex(x));
		}
		return new Complex(
				Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1, -0.25 * x * x) / Gamma.gamma(n + 1));
	}

	public static Complex besselJ(Complex n, Complex x) {
		if (DoubleMath.isMathematicalInteger(n.getReal()) && n.getReal() < 0.0 && n.getImaginary() == 0.0) {
			return new Complex(-1.0).pow(n).multiply(besselJ(n.negate(), x));
		}

		Complex product = x.divide(2.0).pow(n).divide(Arithmetic.lanczosApproxGamma(n.add(1.0)));
		return product.multiply(HypergeometricJS.hypergeometric0F1(n.add(1), x.pow(2).multiply(-0.25)));

	}

	/**
	 * 
	 * @param n
	 *            a positive value
	 * @param m
	 * @return
	 */
	public static double besselJZero(double n, int m) {
		// if ( n < 0 ) throw Error( 'Negative order for Bessel zero' );
		// if ( !Number.isInteger(m) ) throw Error( 'Nonintegral index for Bessel zero' );

		// approximations from dlmf.nist.gov/10.21#vi
		double delta = .9 * Math.PI / 2.0;

		double a = (m + n / 2 - 1 / 4) * Math.PI;
		double e = a - (4 * (n * n) - 1) / (8 * a);
		BisectionSolver solver = new BisectionSolver();
		ISymbol x = F.Dummy("x");
		UnivariateDifferentiableFunction f = new UnaryNumerical(F.BesselJ(F.num(n), x), x, EvalEngine.get(), true);
		return solver.solve(100, f, e - delta, e + delta);
	}

	// private static double diffBesselJ(int n, double x) {
	// return Bessel.jn(-1 + n, x) - Bessel.jn(1 + n, x) * 0.5;
	// }

	public static Complex besselY(double n, double x) {
		// for averaging over integer orders until write code for limit
		double delta = 1e-5;

		if (x < 0) {
			return besselY(new Complex(n), new Complex(x));
		}
		if (DoubleMath.isMathematicalInteger(n)) {
			// return new Complex((diffBesselJ((int) n, x) + diffBesselJ((int) -n, x) * Math.pow(-1, n)) / Math.PI);
			return (besselY(n + delta, x).add(besselY(n - delta, x))).divide(2.0);
		}
		return besselJ(n, x).multiply(Math.cos(n * Math.PI)).subtract(besselJ(-n, x)).divide(Math.sin(n * Math.PI));

	}

	public static Complex besselY(Complex n, Complex x) {
		// for averaging over integer orders until write code for limit
		double delta = 1e-5;
		if (DoubleMath.isMathematicalInteger(n.getReal()) && n.getImaginary() == 0.0) {
			return besselY(new Complex(n.getReal() + delta), x)
					.add(besselY(new Complex(n.getReal() - delta), x).divide(2.0));
		}

		Complex sum = n.multiply(Math.PI).cos().multiply(besselJ(n, x)).subtract(besselJ(n.negate(), x));
		return sum.divide(n.multiply(Math.PI).sin());

	}

	/**
	 * 
	 * @param n
	 *            a positive value
	 * @param m
	 * @return
	 */
	// public static double besselYZero(double n, int m) {
	// // if ( n < 0 ) throw Error( 'Negative order for Bessel zero' );
	// // if ( !Number.isInteger(m) ) throw Error( 'Nonintegral index for Bessel zero' );
	//
	// // approximations from dlmf.nist.gov/10.21#vi
	// double delta = .9 * Math.PI / 2.0;
	//
	// double a = (m + n / 2 - 3 / 4) * Math.PI;
	// double e = a - (4 * (n * n) - 1) / (8 * a);
	// BisectionSolver solver = new BisectionSolver();
	// ISymbol x = F.Dummy("x");
	// UnivariateDifferentiableFunction f = new UnaryNumerical(F.BesselY(F.num(n), x), x, EvalEngine.get(), true);
	// return solver.solve(100, f, e - delta, e + delta);
	// }

	public static Complex besselI(double n, double x) {
		if (DoubleMath.isMathematicalInteger(n) && n < 0)
			return besselI(-n, x);

		if (!DoubleMath.isMathematicalInteger(n) && x < 0)
			return besselI(new Complex(n), new Complex(x));

		return new Complex(Math.pow(x / 2.0, n) * HypergeometricJS.hypergeometric0F1(n + 1.0, 0.25 * x * x)
				/ Gamma.gamma(n + 1.0));
	}

	public static Complex besselI(Complex n, Complex x) {
		if (DoubleMath.isMathematicalInteger(n.getReal()) && n.getReal() < 0 && n.getImaginary() == 0.0) {
			return besselI(n.negate(), x);
		}
		Complex product = x.divide(2).pow(n).divide(Arithmetic.lanczosApproxGamma(n.add(1)));
		return product.multiply(HypergeometricJS.hypergeometric0F1(n.add(1), x.pow(2).multiply(.25)));

	}

	public static Complex besselK(double n, double x) {
		int useAsymptotic = 5;

		// for averaging over integer orders until write code for limit
		double delta = 1e-5;

		if (x > useAsymptotic) {
			return new Complex(Math.sqrt((Math.PI / 2.0) / x) * Math.exp(-x)
					* HypergeometricJS.hypergeometric2F0(n + 0.5, 0.5 - n, -0.5 / x));
		}
		if (x < 0) {
			return besselK(new Complex(n), new Complex(x));
		}
		if (DoubleMath.isMathematicalInteger(n)) {
			return besselK(n + delta, x).add(besselK(n - delta, x)).divide(2.0);
		}
		return (besselI(-n, x).subtract(besselI(n, x))).multiply(Math.PI / 2.0).divide(Math.sin(n * Math.PI));

	}

	public static Complex besselK(Complex n, Complex x) {
		int useAsymptotic = 5;

		// for averaging over integer orders until write code for limit
		double delta = 1e-5;

		// asymptotic form as per Johansson
		if (x.abs() > useAsymptotic) {

			Complex t1 = new Complex(Math.PI / 2.0).divide(x).sqrt().multiply(x.negate().exp());
			Complex t2 = HypergeometricJS.hypergeometric2F0(n.add(0.5), n.negate().add(0.5),
					x.multiply(2.5).reciprocal().negate());

			return t1.multiply(t2);

		}

		double nRe = n.getReal();
		if (DoubleMath.isMathematicalInteger(nRe) && F.isZero(n.getImaginary())) {
			return besselK(new Complex(nRe + delta), x).add(besselK(new Complex(nRe - delta), x)).divide(2.0);
		}
		Complex product = new Complex(Math.PI / 2.0).divide(n.multiply(Math.PI).sin());
		return product.multiply(besselI(n.negate(), x).subtract(besselI(n, x)));
	}

	public static Complex hankelH1(double n, double x) {
		return besselJ(n, x).add(Complex.I.multiply(besselY(n, x)));
	}

	public static Complex hankelH1(Complex n, Complex x) {
		return besselJ(n, x).add(Complex.I.multiply(besselY(n, x)));
	}

	public static Complex hankelH2(double n, double x) {
		return besselJ(n, x).add(Complex.I.multiply(besselY(n, x).negate()));
	}

	public static Complex hankelH2(Complex n, Complex x) {
		return besselJ(n, x).add(Complex.I.multiply(besselY(n, x).negate()));
	}

	public static Complex airyAi(double x) {

		if (F.isZero(x)) {
			return new Complex(1.0 / Math.pow(3.0, 2.0 / 3.0) / Gamma.gamma(2.0 / 3.0));
		}

		if (x < 0) {
			return besselJ(1.0 / 3.0, 2.0 / 3.0 * Math.pow(-x, 1.5))
					.subtract(besselY(1 / 3, 2 / 3 * Math.pow(-x, 1.5)).divide(Math.sqrt(3.0)))
					.multiply(Math.sqrt(-x) / 2);
		}

		return besselK(1.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5)).multiply(1 / Math.PI * Math.sqrt(x / 3.0));

	}

	public static Complex airyAi(Complex x) {
		return besselK(new Complex(1.0 / 3.0), x.pow(1.5).multiply(2.0 / 3.0)).multiply(x.divide(3.0).sqrt())
				.multiply(1 / Math.PI);
	}

	public static Complex airyAiPrime(double x) {
		return besselK(2.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5)).multiply(x).multiply(-1 / Math.PI / Math.sqrt(3));
	}

	public static Complex airyAiPrime(Complex x) {
		return besselK(new Complex(2.0 / 3.0), x.pow(1.5).multiply(2.0 / 3.0)).multiply(x)
				.multiply(-1 / Math.PI / Math.sqrt(3));
	}

	public static Complex airyBi(double x) {
		if (F.isZero(x)) {
			return new Complex(1.0 / Math.pow(3.0, 1.0 / 6.0) / Gamma.gamma(2.0 / 3.0));
		}

		if (x < 0) {
			return besselJ(new Complex(1.0 / 3.0), new Complex(2.0 / 3.0 * Math.pow(-x, 1.5) / Math.sqrt(3.0))
					.add(besselY(1.0 / 3.0, 2.0 / 3.0 * Math.pow(-x, 3 / 2)))).multiply(-Math.sqrt(-x) / 2.0);
		}
		return (besselI(1.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5)).add(besselI(-1.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5))))
				.multiply(Math.sqrt(x / 3.0));

	}

	public static Complex airyBi(Complex x) {

		Complex xPower3D2 = x.pow(1.5).multiply(2.0 / 3.0);
		return x.divide(3.0).sqrt()
				.multiply(besselI(new Complex(1.0 / 3.0), xPower3D2).add(besselI(new Complex(-1.0 / 3.0), xPower3D2)));
	}

	public static Complex airyBiPrime(double x) {
		return (besselI(2.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5)).add(besselI(-2.0 / 3.0, 2.0 / 3.0 * Math.pow(x, 1.5))))
				.multiply((1 / Math.sqrt(3.0)) * x);
	}

	public static Complex airyBiPrime(Complex x) {
		return (besselI(new Complex(2.0 / 3.0), x.pow(1.5).multiply(2.0 / 3.0))
				.add(besselI(new Complex(-2.0 / 3.0), x.pow(1.5).multiply(2.0 / 3.0))))
						.multiply(x.multiply((1 / Math.sqrt(3.0))));
	}

	public static Complex sphericalBesselJ(double n, double x) {
		return besselJ(n + 0.5, x).multiply(new Complex(Math.sqrt(Math.PI / 2.0)).divide(new Complex(x).sqrt()));
	}

	public static Complex sphericalBesselJ(Complex n, Complex x) {
		return besselJ(n.add(0.5), x).multiply(x.sqrt().reciprocal().multiply(Math.sqrt(Math.PI / 2.0)));
	}

	public static Complex sphericalBesselY(double n, double x) {
		Complex besselY = besselY(n + 0.5, x);
		return besselY.multiply(new Complex(Math.sqrt(Math.PI / 2.0)).divide(new Complex(x).sqrt()));
	}

	public static Complex sphericalBesselY(Complex n, Complex x) {
		Complex besselY = besselY(n.add(0.5), x);
		return besselY.multiply(x.sqrt().reciprocal().multiply(Math.sqrt(Math.PI / 2.0)));
	}

	public static Complex sphericalHankel1(double n, double x) {
		return sphericalBesselJ(n, x).add(Complex.I.multiply(sphericalBesselY(n, x)));
	}

	public static Complex sphericalHankel2(double n, double x) {

		return sphericalBesselJ(n, x).add(Complex.I.multiply(sphericalBesselY(n, x).negate()));

	}

	public static double struveH(double n, double x) {
		// can also evaluate from hypergeometric0F1
		// could use to test hypergeometricPFQ
		return Math.pow(x, n + 1.0) * 1 / (Math.pow(2.0, n) * Math.sqrt(Math.PI) * Gamma.gamma(n + 1.5)) * //
				HypergeometricJS.hypergeometric1F2(1, 1.5, n + 1.5, (-0.25) * x * x);
	}

	public static Complex struveH(Complex n, Complex x) {
		// can also evaluate from hypergeometric0F1
		// could use to test hypergeometricPFQ
		return x.pow(n.add(1.0))
				.multiply(Complex.valueOf(2.0).pow(n).multiply(Math.sqrt(Math.PI))
						.multiply(Arithmetic.lanczosApproxGamma(n.add(1.5))).reciprocal())
				.multiply(HypergeometricJS.hypergeometric1F2(Complex.ONE, new Complex(1.5), n.add(1.5),
						x.multiply(x).multiply(-0.25)));

	}

	public static double struveL(double n, double x) {
		// one sign different in 0.25 from struveH
		return Math.pow(x, n + 1.0) * 1 / (Math.pow(2.0, n) * Math.sqrt(Math.PI) * Gamma.gamma(n + 1.5)) * //
				HypergeometricJS.hypergeometric1F2(1, 1.5, n + 1.5, 0.25 * x * x);
	}

	public static Complex struveL(Complex n, Complex x) {
		// one sign different in 0.25 from struveH
		return x.pow(n.add(1.0))
				.multiply(Complex.valueOf(2.0).pow(n).multiply(Math.sqrt(Math.PI))
						.multiply(Arithmetic.lanczosApproxGamma(n.add(1.5))).reciprocal())
				.multiply(HypergeometricJS.hypergeometric1F2(Complex.ONE, new Complex(1.5), n.add(1.5),
						x.multiply(x).multiply(0.25)));

	}

	// public static Complex logGamma(Complex x) {
	//
	// double[] c = { 57.1562356658629235, -59.5979603554754912, 14.1360979747417471, -0.491913816097620199,
	// .339946499848118887e-4, .465236289270485756e-4, -.983744753048795646e-4, .158088703224912494e-3,
	// -.210264441724104883e-3, .217439618115212643e-3, -.164318106536763890e-3, .844182239838527433e-4,
	// -.261908384015814087e-4, .368991826595316234e-5 };
	//
	// // if ( isComplex(x) ) {
	//
	// if (DoubleMath.isMathematicalInteger(x.getReal()) && x.getReal() <= 0 && x.getImaginary() == 0) {
	// throw new ArithmeticException("Gamma function pole");
	// }
	//
	// // reflection formula with modified Hare correction to imaginary part
	// if (x.getReal() < 0.0) {
	// Complex t = new Complex(Math.PI).divide(x.multiply(Math.PI).sin()).log()
	// .subtract(logGamma(x.negate().add(1.0)));
	// double s = x.getImaginary() < 0.0 ? -1 : 1;
	// double d = x.getImaginary() == 0 ? 1 / 4 : 0;
	// double k = Math.ceil(x.getReal() / 2 - 3 / 4 + d);
	// return t.add(new Complex(0, 2 * s * k * Math.PI));
	// }
	//
	// Complex t = x.add(5.24218750000000000);
	// t = x.add(0.5).multiply(t.log()).subtract(t);
	// Complex s = new Complex(0.999999999999997092);
	// for (int j = 0; j < 14; j++) {
	// s = s.add(x.add(j + 1).reciprocal().multiply(c[j]));
	// }
	// Complex u = t.add(s.divide(x).multiply(2.5066282746310005).log());
	//
	// // adjustment to keep imaginary part on same sheet
	// if (s.getReal() < 0.0) {
	// if (x.getImaginary() < 0.0 && s.divide(x).getImaginary() < 0) {
	// u = u.add(new Complex(0, 2 * Math.PI));
	// }
	// if (x.getImaginary() > 0 && s.divide(x).getImaginary() > 0) {
	// u = u.add(new Complex(0, -2 * Math.PI));
	// }
	// }
	//
	// return u;
	//
	// // } else {
	// //
	// // if ( Number.isInteger(x) && x <= 0 ) throw 'Gamma function pole';
	// //
	// // var t = x + 5.24218750000000000;
	// // t = ( x + 0.5 ) * log(t) - t;
	// // var s = 0.999999999999997092;
	// // for ( var j = 0 ; j < 14 ; j++ ) s += c[j] / (x+j+1);
	// // return t + log( 2.5066282746310005 * s / x );
	// //
	// // }
	//
	// }

	// public static Complex gamma(Complex x) {
	// return logGamma(x).exp();
	// }
	//
	// public static double gamma(double x) {
	//
	// // logGamma complex on negative axis
	// if (x < 0.0) {
	// return logGamma(new Complex(x)).exp().getReal();
	// }
	//
	// return Math.exp(de.lab4inf.math.functions.Gamma.lngamma(x));
	//
	// }

}
