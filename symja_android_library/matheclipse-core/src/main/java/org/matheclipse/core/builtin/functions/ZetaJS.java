package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;

public class ZetaJS {
	private ZetaJS() {
	}

	public static Complex summation(java.util.function.Function<Complex, Complex> f, double a, double b) {

		Complex s = Complex.ZERO;

		for (double i = a; i <= b; i++) {
			s = s.add(f.apply(new Complex(i)));
		}

		return s;

	}
	public static Complex complexSummation(java.util.function.DoubleFunction<Complex> f, double a, double b) {
		Complex s = Complex.ZERO;

		for (double i = a; i <= b; i++) {
			s=s.add(f.apply(i));
		}
		return s;
	}
	
	public static double summation(java.util.function.DoubleUnaryOperator f, double a, double b) {
		double s = 0.0;

		for (double i = a; i <= b; i++) {
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

	// public static Complex bernoulli( n ) {
	//
	// if ( !Number.isInteger(n) ) throw Error( 'Noninteger argument for Bernoulli number' );
	//
	// if ( n < 0 ) throw Error( 'Unsupported argument for Bernoulli number' );
	//
	// if ( n === 0 ) return 1;
	//
	// if ( n === 1 ) return -.5;
	//
	// if ( n & 1 ) return 0;
	//
	// return (-1)**(n+1) * n * zeta(-n+1);
	//
	// }

	// public static Complex harmonic(int n ) {
	//
	// if ( !Number.isInteger(n) ) throw Error( 'Noninteger argument for harmonic number' );
	//
	// return summation( i => 1/i, [1,n] );
	//
	// }

	// public static Complex hurwitzZeta(Complex x, Complex a ) {
	//
	// // Johansson arxiv.org/abs/1309.2877
	//
	// if ( isComplex(x) || isComplex(a) ) {
	//
	//
	// } else {
	//
	// if ( x === 1 ) throw Error( 'Hurwitz zeta pole' );
	//
	// // dlmf.nist.gov/25.11.4
	//
	// if ( a > 1 ) {
	// var m = Math.floor(a);
	// a -= m;
	// return hurwitzZeta(x,a) - summation( i => 1 / (a+i)**x, [0,m-1] );
	// }
	//
	// if ( a < 0 ) return hurwitzZeta( x, complex(a) );
	//
	// var n = Math.round( -log( tolerance, 2) ); // from bit precision
	// var m = Math.round( -log( tolerance, 2) );
	//
	// var s = summation( i => 1 / (a+i)**x, [0,n-1] );
	//
	// var t = summation( i => bernoulli(2*i) / factorial(2*i) * gamma(x+2*i-1) / (a+n)**(2*i-1), [1,m] );
	//
	// return s + (a+n)**(1-x) / (x-1) + ( .5 + t / gamma(x) ) / (a+n)**x;
	//
	// }
	//
	// }
}
