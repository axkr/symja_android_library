package org.matheclipse.core.builtin.functions;

import java.util.stream.DoubleStream;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;

import com.google.common.math.DoubleMath;

/**
 * 
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/hypergeometric.js">hypergeometric.js</a>
 */
public class HypergeometricJS {

	static double hypergeometric0F1(double n, double x) {
		try {
			return de.lab4inf.math.functions.HypergeometricLimitFunction.limitSeries(n, x);
		} catch (RuntimeException rex) {
			throw new ArithmeticException("Hypergeometric0F1: " + rex.getMessage());
		}
	}

	// public double hypergeometric0F1(double a,double x ) {
	// return hypergeometric0F1( a, x, 1e-10 ) {
	// }
	// public double hypergeometric0F1(double a,double x,double tolerance ) {
	//
	// double useAsymptotic = 100;
	//
	// if ( isComplex(a) || isComplex(x) ) {
	//
	// if ( !isComplex(a) ) a = complex(a);
	// if ( !isComplex(x) ) x = complex(x);
	//
	// if ( Number.isInteger(a.re) && a.re <= 0 && a.im === 0 )
	// throw 'Hypergeometric function pole';
	//
	// // asymptotic form as per Johansson
	// if ( abs(x) > useAsymptotic ) {
	//
	// var b = sub( mul(2,a), 1 ); // do first
	// var a = sub( a, 1/2 );
	// var x = mul( 4, sqrt(x) );
	//
	// // copied from hypergeometric1F1
	// var t1 = div( mul( gamma(b), pow( mul(-1,x), mul(-1,a) ) ), gamma( sub(b,a) ) );
	// t1 = mul( t1, hypergeometric2F0( a, add( sub(a,b), 1 ), div(-1,x) ) );
	//
	// var t2 = div( mul( gamma(b), mul( pow( x, sub(a,b) ), exp( x ) ) ), gamma(a) );
	// t2 = mul( t2, hypergeometric2F0( sub(b,a), sub(1,a), div(1,x) ) );
	//
	// return mul( exp( div(x,-2) ), add( t1, t2 ) );
	//
	// }
	//
	// var s = complex(1);
	// var p = complex(1);
	// var i = 1;
	//
	// while ( Math.abs(p.re) > tolerance || Math.abs(p.im) > tolerance ) {
	// p = mul( p, div( div( x, a ), i ) );
	// s = add( s, p );
	// a = add( a, 1 );
	// i++;
	// }
	//
	// return s;
	//
	// } else {
	//
	// if ( Number.isInteger(a) && a <= 0 ) throw 'Hypergeometric function pole';
	//
	// // asymptotic form is complex
	// if ( Math.abs(x) > useAsymptotic ) return hypergeometric0F1( a, complex(x) ).re;
	//
	// var s = 1;
	// var p = 1;
	// var i = 1;
	//
	// while ( Math.abs(p) > tolerance ) {
	// p *= x / a / i;
	// s += p;
	// a++;
	// i++;
	// }
	//
	// return s;
	//
	// }
	//
	// }
	//
	//
	public static Complex hypergeometric0F1(Complex a, Complex x) {
		return hypergeometric0F1(a, x, 1e-10);
	}

	public static Complex hypergeometric0F1(Complex a, Complex x, double tolerance) {

		double useAsymptotic = 100;
		if (DoubleMath.isMathematicalInteger(a.getReal()) && a.getReal() <= 0 && a.getImaginary() == 0) {
			throw new IllegalArgumentException("Hypergeometric0F1: hypergeometric function pole");
		}

		// asymptotic form as per Johansson
		if (x.abs() > useAsymptotic) {

			Complex b = a.multiply(2).subtract(1); // do first
			a = a.subtract(1 / 2);
			x = x.sqrt().multiply(4);

			// copied from hypergeometric1F1
			Complex t1 = Arithmetic.lanczosApproxGamma(b).multiply(x.negate().pow(a.negate()))
					.divide(Arithmetic.lanczosApproxGamma(b.subtract(a)));
			t1 = t1.multiply(hypergeometric2F0(a, a.subtract(b).add(1), x.reciprocal().negate()));

			Complex t2 = Arithmetic.lanczosApproxGamma(b).multiply(x.pow(a.subtract(b)).multiply(x.exp()))
					.divide(Arithmetic.lanczosApproxGamma(a));
			t2 = t2.multiply(hypergeometric2F0(b.subtract(a), Complex.ONE.subtract(a), x.reciprocal()));

			return x.divide(-2).exp().multiply(t1.add(t2));

		}

		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		int i = 1;

		while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
			p = p.multiply(x.divide(a).divide(i));
			s = s.add(p);
			a = a.add(1);
			i++;
		}

		return s;

	}

	public static double hypergeometric2F0(double a, double b, double x) {
		return hypergeometric2F0(a, b, x, 1e-10);
	}

	public static Complex hypergeometric2F0(Complex a, Complex b, Complex x) {
		return hypergeometric2F0(a, b, x, 1e-10);
	}

	// function hypergeometric2F0( a, b, x, tolerance=1e-10 ) {
	public static Complex hypergeometric2F0(Complex a, Complex b, Complex x, double tolerance) {
		int terms = 50;

		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		Complex pLast = p;
		boolean converging = false;
		int i = 1;

		while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {

			p = p.multiply(x.multiply(a).multiply(b).divide(i));

			if (p.abs() > pLast.abs() && converging) {
				break; // prevent runaway sum
			}
			if (p.abs() < pLast.abs()) {
				converging = true;
			}
			if (i > terms) {
				throw new ArithmeticException("Hypergeometric2F0: not converging after " + terms + " terms");
			}

			s = s.add(s);
			a = a.add(a);
			b = b.add(b);
			i++;
			pLast = p;

		}

		return s;

	}

	public static double hypergeometric2F0(double a, double b, double x, double tolerance) {
		int terms = 50;
		double s = 1;
		double p = 1, pLast = p;
		boolean converging = false;
		double i = 1;

		while (Math.abs(p) > tolerance) {

			p *= (x * a * b) / i;

			if (Math.abs(p) > Math.abs(pLast) && converging) {
				break; // prevent runaway sum
			}
			if (Math.abs(p) < Math.abs(pLast)) {
				converging = true;
			}
			if (i > terms) {
				throw new ArithmeticException("Hypergeometric2F0: not converging after " + terms + " terms");
			}
			s += p;
			a++;
			b++;
			i++;
			pLast = p;

		}

		return s;
	}

	// function hypergeometric2F1( a, b, c, x, tolerance=1e-10 ) {
	//
	// if ( isComplex(a) || isComplex(b) || isComplex(c) || isComplex(x) ) {
	//
	// // choose smallest absolute value of transformed argument
	// // transformations from Abramowitz & Stegun p.559
	// // fewer operations compared to dlmf.nist.gov/15.8
	//
	// var absArray = [ abs(x), abs(div(x,sub(x,1))), abs(sub(1,x)),
	// abs(inv(x)), abs(inv(sub(1,x))), abs(sub(1,inv(x))) ];
	//
	// var index = absArray.indexOf( Math.min.apply( null, absArray ) );
	//
	// switch( index ) {
	//
	// case 0:
	//
	// break;
	//
	// case 1:
	//
	// return mul( pow( sub(1,x), neg(a) ), hypergeometric2F1( a, sub(c,b), c, div(x,sub(x,1)) ) );
	//
	// case 2:
	//
	// var t1 = mul( gamma(c), gamma( sub(c,add(a,b)) ),
	// inv( gamma(sub(c,a)) ), inv( gamma(sub(c,b)) ),
	// hypergeometric2F1( a, b, add(a,b,neg(c),1), sub(1,x) ) );
	//
	// var t2 = mul( pow( sub(1,x), sub(c,add(a,b)) ),
	// gamma(c), gamma( sub(add(a,b),c) ), inv( gamma(a) ), inv( gamma(b) ),
	// hypergeometric2F1( sub(c,a), sub(c,b), add(c,neg(a),neg(b),1), sub(1,x) ) );
	//
	// return add( t1, t2 );
	//
	// case 3:
	//
	// var t1 = mul( gamma(c), gamma(sub(b,a)), inv( gamma(b) ),
	// inv( gamma(sub(c,a)) ), pow( neg(x), neg(a) ),
	// hypergeometric2F1( a, add(1,neg(c),a), add(1,neg(b),a), inv(x) ) );
	//
	// var t2 = mul( gamma(c), gamma(sub(a,b)), inv( gamma(a) ),
	// inv( gamma(sub(c,b)) ), pow( neg(x), neg(b) ),
	// hypergeometric2F1( b, add(1,neg(c),b), add(1,neg(a),b), inv(x) ) );
	//
	// return add( t1, t2 );
	//
	// case 4:
	//
	// var t1 = mul( pow( sub(1,x), neg(a) ), gamma(c), gamma(sub(b,a)),
	// inv( gamma(b) ), inv( gamma(sub(c,a)) ),
	// hypergeometric2F1( a, sub(c,b), add(a,neg(b),1), inv(sub(1,x)) ) );
	//
	// var t2 = mul( pow( sub(1,x), neg(b) ), gamma(c), gamma(sub(a,b)),
	// inv( gamma(a) ), inv( gamma(sub(c,b)) ),
	// hypergeometric2F1( b, sub(c,a), add(b,neg(a),1), inv(sub(1,x)) ) );
	//
	// return add( t1, t2 );
	//
	// case 5:
	//
	// var t1 = mul( gamma(c), gamma( sub(c,add(a,b)) ), inv( gamma(sub(c,a)) ),
	// inv( gamma(sub(c,b)) ), pow( x, neg(a) ),
	// hypergeometric2F1( a, add(a,neg(c),1), add(a,b,neg(c),1), sub(1,inv(x)) ) );
	//
	// var t2 = mul( gamma(c), gamma( sub(add(a,b),c) ), inv( gamma(a) ), inv( gamma(b) ),
	// pow( sub(1,x), sub(c,add(a,b)) ), pow( x, sub(a,c) ),
	// hypergeometric2F1( sub(c,a), sub(1,a), add(c,neg(a),neg(b),1), sub(1,inv(x)) ) );
	//
	// return add( t1, t2 );
	//
	// }
	//
	// if ( !isComplex(c) ) c = complex(c);
	//
	// if ( Number.isInteger(c.re) && c.re <= 0 && c.im === 0 )
	// throw Error( 'Hypergeometric function pole' );
	//
	// var s = complex(1);
	// var p = complex(1);
	// var i = 1;
	//
	// while ( Math.abs(p.re) > tolerance || Math.abs(p.im) > tolerance ) {
	// p = mul( p, x, a, b, inv(c), 1/i );
	// s = add( s, p );
	// a = add( a, 1 );
	// b = add( b, 1 );
	// c = add( c, 1 );
	// i++;
	// }
	//
	// return s;
	//
	// } else {
	//
	// if ( Number.isInteger(c) && c <= 0 ) throw Error( 'Hypergeometric function pole' );
	//
	// // transformation from Abramowitz & Stegun p.559
	// if ( x < -1 ) {
	//
	// var t1 = gamma(c) * gamma(b-a) / gamma(b) / gamma(c-a)
	// * (-x)**(-a) * hypergeometric2F1( a, 1-c+a, 1-b+a, 1/x );
	// var t2 = gamma(c) * gamma(a-b) / gamma(a) / gamma(c-b)
	// * (-x)**(-b) * hypergeometric2F1( b, 1-c+b, 1-a+b, 1/x );
	//
	// return t1 + t2;
	//
	// }
	//
	// if ( x === -1 ) throw Error( 'Unsupported real hypergeometric argument' );
	//
	// if ( x === 1 ) return gamma(c) * gamma(c-a-b) / gamma(c-a) / gamma(c-b);
	//
	// if ( x > 1 ) return hypergeometric2F1( a, b, c, complex(x) );
	//
	// var s = 1;
	// var p = 1;
	// var i = 1;
	//
	// while ( Math.abs(p) > tolerance ) {
	// p *= x * a * b / c / i;
	// s += p;
	// a++;
	// b++;
	// c++;
	// i++;
	// }
	//
	// return s;
	//
	// }
	//
	// }

	public static Complex hypergeometricPFQ(Complex[] A, Complex[] B, Complex x, double tolerance) {
		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		int i = 1;

		while (Math.abs(p.getReal()) > tolerance || //
				Math.abs(p.getImaginary()) > tolerance) {
			Complex aReduced = A[0];
			for (int j = 1; j < A.length; j++) {
				aReduced = aReduced.multiply(A[j]);
			}
			Complex bReduced = B[0];
			for (int j = 1; j < B.length; j++) {
				bReduced = bReduced.multiply(B[j]);
			}
			p = p.multiply(x).multiply(aReduced).multiply(bReduced.reciprocal()).divide(i);
			s = s.add(p);
			for (int j = 0; j < A.length; j++) {
				A[j] = A[j].add(1);
			}
			for (int j = 0; j < B.length; j++) {
				B[j] = B[j].add(1);
			}
			i++;
		}

		return s;
	}

	public static double hypergeometricPFQ(double[] A, double[] B, double x, double tolerance) {
		double s = 1;
		double p = 1;
		double i = 1;
		while (Math.abs(p) > tolerance) {
			double aReduced = A[0];
			for (int j = 1; j < A.length; j++) {
				aReduced = aReduced * A[j];
			}
			double bReduced = B[0];
			for (int j = 1; j < B.length; j++) {
				bReduced = bReduced * B[j];
			}
			p *= x * aReduced / bReduced / i;
			s += p;
			for (int j = 0; j < A.length; j++) {
				A[j]++;
			}
			for (int j = 0; j < B.length; j++) {
				B[j]++;
			}
			i++;
		}

		return s;

	}

}
