package org.matheclipse.core.builtin.functions;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.expression.F;

/**
 * 
 * Ported from JavaScript file <a href=
 * "https://github.com/paulmasson/math/blob/master/src/functions/elliptic-functions.js">elliptic-functions.js</a>
 */
public class EllipticFunctionsJS {
	public static Complex jacobiTheta(int n, double x, double q) {
		return jacobiTheta(n, x, q, 1e-10);
	}

	public static Complex jacobiTheta(int n, double x, double q, double tolerance) {
		if (Math.abs(q) >= 1) {
			throw new ArithmeticException("Unsupported elliptic nome");
		}

		if (n < 1 || n > 4) {
			throw new ArithmeticException("Undefined Jacobi theta index");
		}
		// dlmf.nist.gov/20.2 to reduce overflow
		if (Math.abs(x) > Math.PI) {

			double p = Math.round(x / Math.PI);
			x = x - p * Math.PI;

			switch (n) {

			case 1:
			case 2:

				return new Complex(Math.pow(-1, p)).multiply(jacobiTheta(n, x, q));

			case 3:
			case 4:

				return jacobiTheta(n, x, q);

			}

		}

		switch (n) {

		case 1:
			if (q < 0) {
				return jacobiTheta(n, new Complex(x), new Complex(q));
			}

			double s = 0;
			double p = 1;
			int i = 0;

			while (Math.abs(p) > tolerance) {
				p = Math.pow(-1, i) * Math.pow(q, (i * i + i)) * Math.sin((2 * i + 1) * x);
				s += p;
				i++;
			}
			return new Complex(2 * Math.pow(q, 0.25) * s);
		case 2:
			if (q < 0) {
				return jacobiTheta(n, new Complex(x), new Complex(q));
			}

			s = 0;
			p = 1;
			i = 0;

			while (Math.abs(p) > tolerance) {
				p = Math.pow(q, (i * i + i)) * Math.cos((2 * i + 1) * x);
				s += p;
				i++;
			}
			return new Complex(2 * Math.pow(q, 0.25) * s);
		case 3:
			s = 0;
			p = 1;
			i = 1;
			while (Math.abs(p) > tolerance) {
				p = Math.pow(q, (i * i)) * Math.cos(2 * i * x);
				s += p;
				i++;
			}
			return new Complex(1 + 2 * s);
		case 4:

			s = 0;
			p = 1;
			i = 1;

			while (Math.abs(p) > tolerance) {
				p = Math.pow(-q, (i * i)) * Math.cos(2 * i * x);
				s += p;
				i++;
			}

			return new Complex(1 + 2 * s);

		}
		throw new ArithmeticException("Undefined Jacobi theta index");
	}

	public static Complex jacobiTheta(int n, Complex x, Complex q) {
		return jacobiTheta(n, x, q, 1e-10);
	}

	public static Complex jacobiTheta(int n, Complex x, Complex q, double tolerance) {

		if (q.abs() >= 1) {
			throw new ArithmeticException("Unsupported elliptic nome");
		}

		if (n < 1 || n > 4) {
			throw new ArithmeticException("Undefined Jacobi theta index");
		}

		Complex piTau = q.log().divide(Complex.I);

		// dlmf.nist.gov/20.2 to reduce overflow
		if (Math.abs(x.getImaginary()) > Math.abs(piTau.getImaginary()) || Math.abs(x.getReal()) > Math.PI) {

			long pt = Math.round(x.getImaginary() / piTau.getImaginary());
			x = x.subtract(piTau.multiply(pt));

			long p = Math.round(x.getReal() / Math.PI);
			x = x.subtract(p * Math.PI);

			Complex qFactor = q.pow(-pt * pt);
			Complex eFactor = x.multiply(Complex.I).multiply(-2 * pt).exp();

			// factors can become huge, so chop spurious parts first
			switch (n) {
			case 1:
				return qFactor.multiply(eFactor).multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
						.multiply(Math.pow((-1), (p + pt)));

			case 2:
				return qFactor.multiply(eFactor).multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
						.multiply(Math.pow((-1), p));

			case 3:
				return qFactor.multiply(eFactor).multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance));
			case 4:
				return qFactor.multiply(eFactor).multiply(F.chopComplex(jacobiTheta(n, x, q), tolerance))
						.multiply(Math.pow((-1), pt));
			}

		}
		Complex s = Complex.ZERO;
		Complex p = Complex.ONE;
		int i = 0;
		switch (n) {

		case 1:
			while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
				p = q.pow(i * i + i).multiply(x.multiply(2 * i + 1).sin()).multiply(Math.pow(-1, i));
				s = s.add(p);
				i++;
			}

			return q.pow(0.25).multiply(s).multiply(2);
		case 2:
			while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
				p = q.pow(i * i + i).multiply(x.multiply(2 * i + 1).cos());
				s = s.add(p);
				i++;
			}
			return q.pow(0.25).multiply(s).multiply(2);
		case 3:
			i = 1;
			while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
				p = q.pow(i * i).multiply(x.multiply(2 * i).cos());
				s = s.add(p);
				i++;
			}
			return s.multiply(2.0).add(1.0);
		case 4:
			i = 1;
			while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
				p = q.negate().pow(i * i).multiply(x.multiply(2 * i).cos());
				s = s.add(p);
				i++;
			}
			return s.multiply(2.0).add(1.0);
		}

		throw new ArithmeticException("Undefined Jacobi theta index");
	}

	public static Complex ellipticNome(Complex m) {
		return EllipticIntegralsJS.ellipticK(m.negate().add(1.0)).multiply(-Math.PI)
				.divide(EllipticIntegralsJS.ellipticK(m)).exp();
	}

	public static Complex ellipticNome(double m) {
		if (m > 1) {
			return ellipticNome(new Complex(m));
		}
		if (m < 0) {
			return EllipticIntegralsJS.ellipticK(1.0 / (1.0 - m)).divide(EllipticIntegralsJS.ellipticK(m / (m - 1.0)))
					.multiply(-Math.PI).exp().negate();
		}
		return EllipticIntegralsJS.ellipticK(1 - m).divide(EllipticIntegralsJS.ellipticK(m)).multiply(-Math.PI).exp();

	}

	public static Complex sn(Complex x, Complex m) {

		Complex q = ellipticNome(m);

		// if ( m > 1 || isComplex(x) || isComplex(m) ) {

		Complex t = x.divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		return jacobiTheta(3, Complex.ZERO, q).divide(jacobiTheta(2, Complex.ZERO, q))
				.multiply(jacobiTheta(1, t, q).divide(jacobiTheta(4, t, q)));

		// }
	}

	public static Complex sn(double x, double m) {
		if (m > 1) {
			return sn(new Complex(x), new Complex(m));
		}
		Complex q = ellipticNome(new Complex(m));
		Complex t = new Complex(x).divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		if (m < 0) {
			return jacobiTheta(3, Complex.ZERO, q).divide(jacobiTheta(4, t, q))
					.multiply(jacobiTheta(1, t, q).divide(jacobiTheta(2, Complex.ZERO, q)).getReal());
		}
		return jacobiTheta(3, Complex.ZERO, q).divide(jacobiTheta(2, Complex.ZERO, q))
				.multiply(jacobiTheta(1, t, q).divide(jacobiTheta(4, t, q)));

	}

	public static Complex cn(Complex x, Complex m) {
		Complex q = ellipticNome(m);
		// if ( m > 1 || isComplex(x) || isComplex(m) ) {
		Complex t = x.divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		return jacobiTheta(4, Complex.ZERO, q).divide(jacobiTheta(2, Complex.ZERO, q))
				.multiply(jacobiTheta(2, t, q).divide(jacobiTheta(4, t, q)));
		// }
	}

	public static Complex cn(double x, double m) {
		if (m > 1) {
			return cn(new Complex(x), new Complex(m));
		}
		Complex q = ellipticNome(new Complex(m));

		Complex t = new Complex(x).divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		if (m < 0) {
			return jacobiTheta(4, Complex.ZERO, q).divide(jacobiTheta(4, t, q))
					.multiply(jacobiTheta(2, t, q).divide(jacobiTheta(2, Complex.ZERO, q)).getReal());
		}
		return jacobiTheta(4, Complex.ZERO, q).divide(jacobiTheta(2, Complex.ZERO, q))
				.multiply(jacobiTheta(2, t, q).divide(jacobiTheta(4, t, q)));
	}

	public static Complex dn(Complex x, Complex m) {

		Complex q = ellipticNome(m);

		// if ( m > 1 || isComplex(x) || isComplex(m) ) {

		Complex t = x.divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		return jacobiTheta(4, Complex.ZERO, q).divide(jacobiTheta(3, Complex.ZERO, q))
				.multiply(jacobiTheta(3, t, q).divide(jacobiTheta(4, t, q)));

		// }
	}

	public static Complex dn(double x, double m) {
		if (m > 1) {
			return dn(new Complex(x), new Complex(m));
		}
		Complex q = ellipticNome(new Complex(m));

		Complex t = new Complex(x).divide(jacobiTheta(3, Complex.ZERO, q).pow(2));

		return jacobiTheta(4, Complex.ZERO, q).divide(jacobiTheta(3, Complex.ZERO, q))
				.multiply(jacobiTheta(3, t, q).divide(jacobiTheta(4, t, q)));

	}

	public static Complex am(Complex x, Complex m) {

		// if ( m > 1 || isComplex(x) || isComplex(m) ) {

		if (m.getImaginary() == 0.0 && m.getReal() <= 1) {

			Complex K = EllipticIntegralsJS.ellipticK(m.getReal());
			long n = Math.round(x.getReal() / 2.0 / K.getReal()); // ??? getReal() inserted
			x = x.subtract(2.0 * n * K.getReal());

			if (m.getReal() < 0.0) {

				Complex Kp = EllipticIntegralsJS.ellipticK(1 - m.getReal());
				long p = Math.round(x.getImaginary() / 2.0 / Kp.getReal());

				// bitwise test for odd integer
				if ((p & 1) == 1) {
					return sn(x, m).asin().negate().add(n * Math.PI);
				}

			}

			return sn(x, m).asin().add(n * Math.PI);

		}

		return sn(x, m).asin();

		// }
	}

	public static Complex am(double x, double m) {
		if (m > 1) {
			return am(new Complex(x), new Complex(m));
		}

		Complex K = EllipticIntegralsJS.ellipticK(m);
		long n = Math.round(x / 2.0 / K.getReal()); // ??? .getReal() inserted
		x = x - 2 * n * K.getReal();// ??? .getReal() inserted

		return sn(x, m).asin().add(n * Math.PI);

	}

	private static Complex cubicTrigSolution(Complex p, Complex q, int n) {
		// p, q both negative in defining cubic
		return p.sqrt().multiply(q.multiply(p.pow(-1.5)).multiply(3.0 * Math.sqrt(3.0) / 2.0).acos().divide(3.0)
				.subtract(2.0 * Math.PI * n / 3.0).cos()).multiply(2.0 / Math.sqrt(3));
	}

	public static Complex[] weierstrassRoots(Complex g2, Complex g3) {
		g2 = g2.divide(4);
		g3 = g3.divide(4);

		Complex e1 = cubicTrigSolution(g2, g3, 0);
		Complex e2 = cubicTrigSolution(g2, g3, 1);
		Complex e3 = cubicTrigSolution(g2, g3, 2);

		return new Complex[] { e1, e2, e3 };
	}

	public static Complex[] weierstrassHalfPeriods(Complex g2, Complex g3) {

		Complex[] sol = weierstrassRoots(g2, g3);
		Complex e1 = sol[0];
		// Complex e2 = sol[1];
		Complex e3 = sol[2];
		Complex w1 = inverseWeierstrassP(e1, g2, g3);
		Complex w3 = inverseWeierstrassP(e3, g2, g3);

		return new Complex[] { w1, w3 };

	}

	public static Complex[] weierstrassInvariants(Complex w1, Complex w3) {

		// if ( !isComplex(w1) ) w1 = complex(w1);
		// if ( !isComplex(w3) ) w3 = complex(w3);

		// order half periods by complex slope
		if (w3.getImaginary() / w3.getReal() < w1.getImaginary() / w1.getReal()) {
			Complex temp = w1;
			w1 = w3;
			w3 = temp;
		}

		Complex ratio = w3.divide(w1);
		boolean conjugate = false;

		if (ratio.getImaginary() < 0) {
			ratio = ratio.conjugate();
			conjugate = true;
		}

		Complex q = Complex.I.multiply(Math.PI).multiply(ratio).exp();

		// en.wikipedia.org/wiki/Weierstrass's_elliptic_functions
		// modified for input of half periods

		Complex a = jacobiTheta(2, Complex.ZERO, q);
		Complex b = jacobiTheta(3, Complex.ZERO, q);

		Complex g2 = w1.multiply(2).pow(-4).multiply(a.pow(8).add(a.pow(4).multiply(b.pow(4)).negate()).add(b.pow(8)))
				.multiply(4.0 / 3.0 * Math.pow(Math.PI, 4));

		Complex g3 = w1.multiply(2).pow(-6).multiply(8.0 / 27.0 * Math.pow(Math.PI, 6))
				.multiply(a.pow(12).add(a.pow(8).multiply(b.pow(4)).multiply(-1.5))
						.add(a.pow(4).multiply(b.pow(8)).multiply(-1.5)).add(b.pow(12)));

		if (conjugate) {
			g2 = g2.conjugate();
			g3 = g3.conjugate();
		}

		return new Complex[] { g2, g3 };

	}

	public static Complex weierstrassP(Complex x, Complex g2, Complex g3) {

		// if ( !isComplex(x) ) x = complex(x);

		Complex[] sol = weierstrassRoots(g2, g3);
		Complex e1 = sol[0];
		Complex e2 = sol[1];
		Complex e3 = sol[2];
		// Whittaker & Watson, Section 22.351

		Complex m = e2.subtract(e3).divide(e1.subtract(e3));

		return e3.add(e1.subtract(e3).multiply(sn(x.multiply(e1.subtract(e3).sqrt()), m).pow(-2)));

	}

	public static Complex weierstrassPPrime(Complex x, Complex g2, Complex g3) {

		// if ( !isComplex(x) ) x = complex(x);

		Complex[] sol = weierstrassRoots(g2, g3);
		Complex e1 = sol[0];
		Complex e2 = sol[1];
		Complex e3 = sol[2];

		// Whittaker & Watson, Section 22.351

		Complex m = e2.subtract(e3).divide(e1.subtract(e3));

		Complex argument = x.multiply(e1.subtract(e3).sqrt());

		return e1.subtract(e3).pow(1.5).multiply(cn(argument, m)).multiply(dn(argument, m))
				.multiply(sn(argument, m).pow(-3)).multiply(-2);

	}

	public static Complex inverseWeierstrassP(Complex x, Complex g2, Complex g3) {

		Complex[] sol = weierstrassRoots(g2, g3);
		Complex e1 = sol[0];
		Complex e2 = sol[1];
		Complex e3 = sol[2];

		// Johansson arxiv.org/pdf/1806.06725.pdf p.17
		// sign of imaginary part on real axis differs from MMA

		return EllipticIntegralsJS.carlsonRF(x.subtract(e1), x.subtract(e2), x.subtract(e3));

	}

}
