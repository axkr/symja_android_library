package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;

public class ZetaJS {
	private final static int MAX_VALUE_HALF = Integer.MAX_VALUE / 2;

	private ZetaJS() {
	}

	public static Complex summation(java.util.function.Function<Complex, Complex> f, double a, double b,
			int iterationLimit) {

		Complex s = Complex.ZERO;
		int counter = 0;
		for (double i = a; i <= b; i++) {
			if (counter++ > iterationLimit && iterationLimit > 0) {
				IterationLimitExceeded.throwIt(counter, F.Sum);
			}
			s = s.add(f.apply(new Complex(i)));
		}

		return s;

	}

	public static Complex complexSummation(java.util.function.DoubleFunction<Complex> f, double a, double b,
			int iterationLimit) {
		Complex s = Complex.ZERO;
		int counter = 0;
		for (double i = a; i <= b; i++) {
			if (counter++ > iterationLimit && iterationLimit > 0) {
				IterationLimitExceeded.throwIt(counter, F.Sum);
			}
			s = s.add(f.apply(i));
		}
		return s;
	}

	public static double sumDouble(java.util.function.DoubleUnaryOperator f, double a, double b, int iterationLimit) {
		double s = 0.0;
		int counter = 0;
		for (double i = a; i <= b; i++) {
			if (counter++ > iterationLimit && iterationLimit > 0) {
				IterationLimitExceeded.throwIt(counter, F.Sum);
			}
			s += f.applyAsDouble(i);
		}
		return s;
	}

	public static double sumInt(java.util.function.IntToDoubleFunction f, int a, int b, int iterationLimit) {
		double s = 0;
		if ((b - a) > iterationLimit && iterationLimit > 0) {
			IterationLimitExceeded.throwIt((b - a), F.Sum);
		}
		for (int i = a; i <= b; i++) {
			s += f.applyAsDouble(i);
		}
		return s;
	}

	// public static Complex zeta(Complex x ) {
	//
	// // Borwein algorithm
	//
	// int n = 14; // from error bound for tolerance
	//
	// if ( x.getImaginary() != 0.0 ) {//isComplex(x) &&
	// n = Math.max( n, Math.ceil( log( 2.0 / GammaJS.gamma(x),abs( ) / tolerance ) / log( 3.0 + Math.Sqrt(8.0) ) ) );
	// }
	// int[] d = new int[] { 1 };
	// for ( int i = 1 ; i <= n ; i++ ) {
	// // order of multiplication reduces overflow, but factorial overflows at 171
	// d.push( d[i-1] + n * factorial( n+i-1 ) / factorial( n-i ) / factorial( 2*i ) * 4**i );
	// }
	// if ( x.getImaginary() != 0.0 ) {//isComplex(x)
	//
	// // functional equation dlmf.nist.gov/25.4.2
	// if ( x.re < 0 ) {
	// return mul( pow(2,x), pow(pi,sub(x,1)), sin( mul(pi/2,x) ), GammaJS.gamma( sub(1,x) ), zeta( sub(1,x) ) );
	// }
	// Complex s = summation( k => div( (-1)**k * ( d[k] - d[n] ), pow( k+1, x ) ), [0,n-1] );
	//
	// return div( div( s, -d[n] ), sub( 1, pow( 2, sub(1,x) ) ) );
	//
	// } else {
	//
	// // functional equation dlmf.nist.gov/25.4.2
	// if ( x < 0 ) {
	// return 2**x * pi**(x-1) * sin(pi*x/2) * GammaJS.gamma(1-x) * zeta(1-x);
	// }
	//
	// Complex s = summation( k => (-1)**k * ( d[k] - d[n] ) / (k+1)**x, [0,n-1] );
	//
	// return -s / d[n] / ( 1 - 2**(1-x) );
	//
	// }
	//
	// }

	// public static Complex dirichletEta(Complex x ) {
	// return mul( zeta(x), sub( 1, pow( 2, sub(1,x) ) ) );
	// }

	public static double bernoulliInt(int n) {
		return NumberTheory.bernoulliDouble(n);
	}

	// public static Complex harmonic(int n ) {
	//
	// if ( !Number.isInteger(n) ) throw Error( 'Noninteger argument for harmonic number' );
	//
	// return summation( i => 1/i, [1,n] );
	//
	// }

	/**
	 * 
	 * @param x
	 * @param a
	 * @return
	 * @deprecated INVALID at the moment
	 */
	public static Complex hurwitzZeta(final Complex x, final Complex a) {
		// TODO INVALID at the moment
		if (x.getReal() == 1.0 && x.getImaginary() == 0.0) {
			throw new ArgumentTypeException("Hurwitz zeta pole");
		}

		// dlmf.nist.gov/25.11.4
		int iterationLimit = EvalEngine.get().getIterationLimit();
		if (a.getReal() > 1.0) {
			double m = Math.floor(a.getReal());
			Complex aValue = a.subtract(m);
			return hurwitzZeta(x, aValue)
					.subtract(summation(i -> aValue.add(i).pow(x.negate()), 0, m - 1.0, iterationLimit));
			// return sub( hurwitzZeta(x,a), summation( i => pow( add(a,i), neg(x) ), [0,m-1] ) );
		}

		if (a.getReal() < 0.0) {
			double m = -Math.floor(a.getReal());
			return hurwitzZeta(x, a.add(m)).add(summation(i -> a.add(i).pow(x.negate()), 0, m - 1.0, iterationLimit));
		}

		// Euler-Maclaurin has differences of large values in left-hand plane
		// but different summation (dlmf.nist.gov/25.11.9) does not converge for complex a
		// to be improved...

		double switchForms = -5.0;

		if (x.getReal() < switchForms) {
			throw new ArgumentTypeException("Currently unsuppported complex Hurwitz zeta");
		}

		// Johansson arxiv.org/abs/1309.2877

		int n = 15; // recommendation of Vepstas, Efficient Algorithm, p.12

		Complex S = summation(i -> a.add(i).pow(x.negate()), 0, n - 1, iterationLimit);

		Complex I = a.add(n).pow(x.add(-1.0)).divide(x.subtract(1.0));

		Complex p = x.multiply(0.5).multiply(a.add(n).reciprocal());
		Complex t = p.multiply(bernoulliInt(2));
		int i = 1;

		// converges rather quickly
		while (Math.abs(p.getReal()) > Config.SPECIAL_FUNCTIONS_TOLERANCE || //
				Math.abs(p.getImaginary()) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
			i++;
			if (i > MAX_VALUE_HALF) {
				throw new ArgumentTypeException("Hurwitz zeta: i > MAX_VALUE_HALF");
			}
			int iPlusi = i + i;
			p = p.multiply(x.add(iPlusi - 2.0).multiply(x.add(iPlusi - 3.0))
					.multiply(a.add(n).pow(2.0).multiply(iPlusi * (iPlusi - 1)).reciprocal()));
			t = t.add(p.multiply(bernoulliInt(iPlusi)));
		}

		Complex T = t.add(0.5).divide(a.add(n).pow(x));

		return S.add(I).add(T);
	}

	public static double hurwitzZeta(final double x, final double a) {

		// Johansson arxiv.org/abs/1309.2877

		if (x == 1.0) {
			throw new ArgumentTypeException("Hurwitz zeta pole");
		}

		// dlmf.nist.gov/25.11.4

		int iterationLimit = EvalEngine.get().getIterationLimit();
		if (a > 1.0) {
			double m = Math.floor(a);
			final double aValue = a - m;
			return hurwitzZeta(x, aValue) - sumDouble(i -> 1.0 / Math.pow(aValue + i, x), 0, m - 1, iterationLimit);
		}

		if (a < 0.0) {
			throw new ArgumentTypeException("Hurwitz zeta a < 0.0 ");
			// return hurwitzZeta( x, complex(a) );
		}

		// Euler-Maclaurin has differences of large values in left-hand plane
		// swith to difference summation: dlmf.nist.gov/25.11.9

		double switchForms = -5.0;

		if (x < switchForms) {

			final double xValue = 1 - x;
			double t = Math.cos(Math.PI * xValue / 2.0 - 2.0 * Math.PI * a);
			double s = t;
			int i = 1;

			while (Math.abs(t) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
				if (i++ > iterationLimit && iterationLimit > 0) {
					IterationLimitExceeded.throwIt(i, F.HurwitzZeta);
				}
				t = Math.cos(Math.PI * xValue / 2.0 - 2.0 * i * Math.PI * a) / Math.pow(i, xValue);
				s += t;
			}

			return 2.0 * GammaJS.gamma(xValue) / Math.pow(2.0 * Math.PI, xValue) * s;

		}

		// Johansson arxiv.org/abs/1309.2877
		final int n = 15; // recommendation of Vepstas, Efficient Algorithm, p.12

		double S = sumDouble(i -> 1.0 / Math.pow(a + i, x), 0, n - 1, iterationLimit);

		double I = Math.pow(a + n, 1.0 - x) / (x - 1.0);

		double p = x / 2.0 / (a + n);
		double t = bernoulliInt(2) * p;
		int i = 1;
		// converges rather quickly
		while (Math.abs(p) > Config.SPECIAL_FUNCTIONS_TOLERANCE) {
			if (i++ > iterationLimit && iterationLimit > 0) {
				IterationLimitExceeded.throwIt(i, F.HurwitzZeta);
			}
			if (Double.isNaN(t)) {
				throw new ArgumentTypeException("Hurwitz zeta: t == NaN");
			}
			if (Double.isInfinite(p)) {
				throw new ArgumentTypeException("Hurwitz zeta: p == Infinity");
			}
			if (i > MAX_VALUE_HALF) {
				throw new ArgumentTypeException("Hurwitz zeta: i > MAX_VALUE_HALF");
			}
			int iPlusi = i + i;
			p *= (x + iPlusi - 2.0) * (x + iPlusi - 3.0) / (iPlusi * (iPlusi - 1.0) * Math.pow(a + n, 2));
			t += bernoulliInt(iPlusi) * p;
		}

		double T = (0.5 + t) / Math.pow(a + n, x);

		return S + I + T;
	}
}
