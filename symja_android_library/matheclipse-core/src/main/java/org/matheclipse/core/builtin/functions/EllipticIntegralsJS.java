package org.matheclipse.core.builtin.functions;

import org.gavaghan.geodesy.GeodeticMeasurement;
import org.hipparchus.complex.Complex;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;

/**
 * 
 * Ported from JavaScript file <a href=
 * "https://github.com/paulmasson/math/blob/master/src/functions/elliptic-integrals.js">elliptic-integrals.js</a>
 */
public class EllipticIntegralsJS {

	// Carlson symmetric integrals

	public static Complex carlsonRC(Complex x, Complex y) {

		// if ( x < 0 || y < 0 || isComplex(x) || isComplex(y) ) {

		// if ( !isComplex(x) ) x = complex(x);
		// if ( !isComplex(y) ) y = complex(y);

		if (x.getReal() == y.getReal() && x.getImaginary() == y.getImaginary()) {
			return x.sqrt().reciprocal();
		}

		return x.divide(y).sqrt().acos().divide(y.subtract(x).sqrt());

		// }

	}

	public static Complex carlsonRC(double x, double y) {
		if (x < 0 || y < 0) {
			return carlsonRC(new Complex(x), new Complex(y));
		}
		if (x == y) {
			return new Complex(1 / x).sqrt();
		}

		if (x < y) {
			return new Complex(Math.acos(Math.sqrt(x / y)) / Math.sqrt(y - x));
		} else {
			return new Complex(FastMath.acosh(Math.sqrt(x / y)) / Math.sqrt(x - y));
		}
	}

	public static Complex carlsonRD(Complex x, Complex y, Complex z) {
		return carlsonRJ(x, y, z, z);
	}

	public static Complex carlsonRD(double x, double y, double z) {
		return carlsonRJ(x, y, z, z);
	}

	public static Complex carlsonRF(Complex x, Complex y, Complex z) {
		return carlsonRF(x, y, z, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex carlsonRF(Complex x, Complex y, Complex z, double tolerance) {

		// if ( isComplex(x) || isComplex(y) || isComplex(z) ) {

		Complex xm = x;
		Complex ym = y;
		Complex zm = z;
		Complex A0 = x.add(y).add(z).divide(3.0);
		Complex Am = A0;
		double Q = Math.pow(3.0 * tolerance, -1.0 / 6.0)
				* Math.max(A0.subtract(x).abs(), Math.max(A0.subtract(y).abs(), A0.subtract(z).abs()));
		double g = 0.25;
		double pow4 = 1.0;
		double m = 0.0;

		while (true) {
			Complex xs = xm.sqrt();
			Complex ys = ym.sqrt();
			Complex zs = zm.sqrt();
			Complex lm = xs.multiply(ys).add(xs.multiply(zs)).add(ys.multiply(zs));
			Complex Am1 = Am.add(lm).multiply(g);
			xm = xm.add(lm).multiply(g);
			ym = ym.add(lm).multiply(g);
			zm = zm.add(lm).multiply(g);
			if (pow4 * Q < Am.abs()) {
				break;
			}
			Am = Am1;
			m += 1;
			pow4 *= g;
		}

		Complex t = Am.reciprocal().multiply(pow4);
		Complex X = A0.subtract(x).multiply(t);
		Complex Y = A0.subtract(y).multiply(t);
		Complex Z = X.add(Y).negate();
		Complex E2 = X.multiply(Y).subtract(Z.multiply(Z));
		Complex E3 = X.multiply(Y).multiply(Z);

		return Am.pow(-0.5).multiply(E2.multiply(-924).add(E2.multiply(E2).multiply(385)).add(E3.multiply(660))
				.add(E2.multiply(E3).multiply(-630)).add(9240)).multiply(1.0 / 9240.0);

		// }
	}

	public static Complex carlsonRF(double x, double y, double z) {
		return carlsonRF(x, y, z, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex carlsonRF(double x, double y, double z, double tolerance) {
		if (y == z)
			return carlsonRC(x, y);
		if (x == z)
			return carlsonRC(y, x);
		if (x == y)
			return carlsonRC(z, x);

		// adapted from mpmath / elliptic.py

		double xm = x;
		double ym = y;
		double zm = z;
		double A0 = (x + y + z) / 3.0;
		double Am = A0;

		double Q = Math.pow(3 * tolerance, -1.0 / 6.0)
				* Math.max(Math.max(Math.abs(A0 - x), Math.abs(A0 - y)), Math.abs(A0 - z));
		double g = .25;
		double pow4 = 1.0;
		double m = 0.0;

		while (true) {
			double xs = Math.sqrt(xm);
			double ys = Math.sqrt(ym);
			double zs = Math.sqrt(zm);
			double lm = xs * ys + xs * zs + ys * zs;
			double Am1 = (Am + lm) * g;
			xm = (xm + lm) * g;
			ym = (ym + lm) * g;
			zm = (zm + lm) * g;
			if (pow4 * Q < Math.abs(Am)) {
				break;
			}
			Am = Am1;
			m += 1;
			pow4 *= g;
		}

		double t = pow4 / Am;
		double X = (A0 - x) * t;
		double Y = (A0 - y) * t;
		double Z = -X - Y;
		double E2 = X * Y - Math.pow(Z, 2);
		double E3 = X * Y * Z;

		return new Complex(
				Math.pow(Am, -0.5) * (9240 - 924 * E2 + 385 * Math.pow(E2, 2) + 660.0 * E3 - 630 * E2 * E3) / 9240.0);

	}

	public static double carlsonRG(Complex x, Complex y, Complex z) {

		return 1;

	}

	public static Complex carlsonRJ(Complex x, Complex y, Complex z, Complex p) {
		return carlsonRJ(x, y, z, p, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex carlsonRJ(Complex x, Complex y, Complex z, Complex p, double tolerance) {

		// if ( isComplex(x) || isComplex(y) || isComplex(z) || isComplex(p) ) {

		Complex xm = x;
		Complex ym = y;
		Complex zm = z;
		Complex pm = p;

		Complex Am = x.add(y).add(z).add(p.multiply(2)).divide(5.0);
		Complex A0 = Am;
		Complex delta = p.subtract(x).multiply(p.subtract(y)).multiply(p.subtract(z));
		double Q = Math.pow(0.25 * tolerance, -1.0 / 6.0) * Math.max(A0.subtract(x).abs(),
				Math.max(A0.subtract(y).abs(), Math.max(A0.subtract(z).abs(), A0.subtract(p).abs())));
		double m = 0.0;
		double g = 0.25;
		double pow4 = 1.0;
		Complex S = Complex.ZERO;

		while (true) {
			Complex sx = xm.sqrt();
			Complex sy = ym.sqrt();
			Complex sz = zm.sqrt();
			Complex sp = pm.sqrt();
			Complex lm = sx.multiply(sy).add(sx.multiply(sz)).add(sy.multiply(sz));
			Complex Am1 = Am.add(lm).multiply(g);
			xm = xm.add(lm).multiply(g);
			ym = ym.add(lm).multiply(g);
			zm = zm.add(lm).multiply(g);
			pm = pm.add(lm).multiply(g);
			Complex dm = sp.add(sx).multiply(sp.add(sy)).multiply(sp.add(sz));
			Complex em = dm.reciprocal().multiply(dm.reciprocal()).multiply(delta).multiply(Math.pow(4.0, -3.0 * m));
			if (pow4 * Q < Am.abs()) {
				break;
			}
			Complex T = carlsonRC(Complex.ONE, em.add(1)).multiply(pow4).multiply(dm.reciprocal());
			S = S.add(T);
			pow4 *= g;
			m += 1;
			Am = Am1;
		}

		Complex t = Am.reciprocal().multiply(Math.pow(2, -2 * m));
		Complex X = A0.subtract(x).multiply(t);
		Complex Y = A0.subtract(y).multiply(t);
		Complex Z = A0.subtract(z).multiply(t);
		Complex P = X.add(Y.add(Z)).divide(-2);
		Complex E2 = X.multiply(Y).add(X.multiply(Z)).add(Y.multiply(Z)).add(P.multiply(P).multiply(-3));
		Complex E3 = X.multiply(Y).multiply(Z).add(E2.multiply(P).multiply(2))
				.add(P.multiply(P).multiply(P).multiply(4));
		Complex E4 = X.multiply(Y).multiply(Z).multiply(2).add(E2.multiply(P))
				.add(P.multiply(P).multiply(P).multiply(3)).multiply(P);
		Complex E5 = X.multiply(Y).multiply(Z).multiply(P).multiply(P);
		P = E2.multiply(-5148).add(E2.multiply(E2).multiply(2457)).add(E3.multiply(4004))
				.add(E2.multiply(E3).multiply(-4158)).add(E4.multiply(-3276)).add(E5.multiply(2772)).add(24024);
		Complex v1 = Am.pow(-1.5).multiply(Math.pow(g, m)).multiply(P).multiply(1.0 / 24024.0);
		// Complex v2 = mul(6,S);

		// return add( v1, v2 );
		return S.multiply(6.0).add(v1);
		// }
	}

	public static Complex carlsonRJ(double x, double y, double z, double p) {
		return carlsonRJ(x, y, z, p, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex carlsonRJ(double x, double y, double z, double p, double tolerance) {
		// adapted from mpmath / elliptic.py

		double xm = x;
		double ym = y;
		double zm = z;
		double pm = p;

		double Am = (x + y + z + 2 * p) / 5.0;
		double A0 = Am;
		double delta = (p - x) * (p - y) * (p - z);
		double Q = Math.pow(.25 * tolerance, -1.0 / 6.0)
				* Math.max(Math.abs(A0 - x), Math.max(Math.abs(A0 - y), Math.max(Math.abs(A0 - z), Math.abs(A0 - p))));
		double m = 0;
		double g = 0.25;
		double pow4 = 1;
		Complex S = Complex.ZERO;

		while (true) {
			double sx = Math.sqrt(xm);
			double sy = Math.sqrt(ym);
			double sz = Math.sqrt(zm);
			double sp = Math.sqrt(pm);
			double lm = sx * sy + sx * sz + sy * sz;
			double Am1 = (Am + lm) * g;
			xm = (xm + lm) * g;
			ym = (ym + lm) * g;
			zm = (zm + lm) * g;
			pm = (pm + lm) * g;
			double dm = (sp + sx) * (sp + sy) * (sp + sz);
			double em = delta * Math.pow(4.0, -3.0 * m) / Math.pow(dm, 2);
			if (pow4 * Q < Math.abs(Am)) {
				break;
			}
			Complex T = carlsonRC(1, 1 + em).multiply(pow4 / dm);
			S = S.add(T);
			pow4 *= g;
			m += 1;
			Am = Am1;
		}

		double t = Math.pow(2, -2 * m) / Am;
		double X = (A0 - x) * t;
		double Y = (A0 - y) * t;
		double Z = (A0 - z) * t;
		double P = (-X - Y - Z) / 2.0;
		double E2 = X * Y + X * Z + Y * Z - 3 * Math.pow(P, 2);
		double E3 = X * Y * Z + 2 * E2 * P + 4 * Math.pow(P, 3);
		double E4 = (2 * X * Y * Z + E2 * P + 3 * Math.pow(P, 3)) * P;
		double E5 = X * Y * Z * Math.pow(P, 2);
		P = 24024 - 5148 * E2 + 2457 * Math.pow(E2, 2) + 4004 * E3 - 4158 * E2 * E3 - 3276 * E4 + 2772 * E5;
		double v1 = Math.pow(g, m) * Math.pow(Am, -1.5) * P / 24024.0;
		// double v2 = S.multiply(6.0);

		return S.multiply(6.0).add(v1);

	}

	// elliptic integrals

	public static Complex ellipticF(Complex x, Complex m) {

		// if ( arguments.length === 1 ) {
		// m = x;
		// x = pi / 2;
		// }

		// if ( isComplex(x) || isComplex(m) ) {

		// if ( !isComplex(x) ) x = complex(x);

		Complex period = Complex.ZERO;
		if (Math.abs(x.getReal()) > (Math.PI / 2)) {
			long p = Math.round(x.getReal() / Math.PI);
			x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
			period = ellipticK(m).multiply(p + p);
		}

		return x.sin().multiply(carlsonRF(x.cos().pow(2), m.multiply(x.sin().pow(2)).negate().add(1), Complex.ONE))
				.add(period);

		// }
	}

	public static Complex ellipticF(double x, double m) {
		if (m > 1 && x > Math.asin(1 / Math.sqrt(m))) {
			return ellipticF(new Complex(x), new Complex(m));
		}

		Complex period = Complex.ZERO;
		if (Math.abs(x) > Math.PI / 2.0) {
			long p = Math.round(x / Math.PI);
			x = x - p * Math.PI;
			period = ellipticK(m).multiply(p + p);
		}

		return carlsonRF(Math.pow(Math.cos(x), 2), 1 - m * Math.pow(Math.sin(x), 2), 1).multiply(Math.sin(x))
				.add(period);

	}

	public static Complex ellipticK(double m) {
		return ellipticF(Math.PI / 2.0, m);
	}

	public static Complex ellipticK(Complex m) {
		return ellipticF(new Complex(Math.PI / 2.0), m);
	}

	public static Complex ellipticE(Complex x, Complex m) {

		// if ( arguments.length === 1 ) {
		// m = x;
		// x = pi / 2;
		// }

		// if ( isComplex(x) || isComplex(m) ) {

		// if (!isComplex(x))
		// x = complex(x);

		Complex period = Complex.ZERO;
		if (Math.abs(x.getReal()) > Math.PI / 2.0) {
			long p = Math.round(x.getReal() / Math.PI);
			x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
			period = ellipticE(new Complex(Math.PI / 2.0), m).multiply(p + p);
		}

		Complex diff = m.multiply(x.sin().pow(2.0)).negate().add(1.0);
		return period.add(x.sin().multiply(carlsonRF(x.cos().pow(2.0), diff, Complex.ONE)))
				.add(m.multiply(x.sin().pow(3)).multiply(carlsonRD(x.cos().pow(2.0), diff, Complex.ONE))
						.multiply(-1.0 / 3.0));

		// }
	}

	public static Complex ellipticE(double x, double m) {
		if (m > 1 && x > Math.asin(1 / Math.sqrt(m))) {
			return ellipticE(new Complex(x), new Complex(m));
		}

		Complex period = Complex.ZERO;
		if (Math.abs(x) > Math.PI / 2.0) {
			long p = Math.round(x / Math.PI);
			x = x - p * Math.PI;
			period = ellipticE(Math.PI / 2.0, m).multiply(p + p);
		}

		return period.add(
				carlsonRF(Math.pow(Math.cos(x), 2), 1.0 - m * Math.pow(Math.sin(x), 2.0), 1.0).multiply(Math.sin(x))
						.subtract(carlsonRD(Math.pow(Math.cos(x), 2.0), 1 - m * Math.pow(Math.sin(x), 2.0), 1.0)
								.multiply(m / 3.0 * Math.pow(Math.sin(x), 3.0))));

	}

	public static Complex ellipticPi(Complex n, Complex x, Complex m) {

		// if ( arguments.length === 2 ) {
		// m = x;
		// x = pi / 2;
		// }

		// if ( isComplex(n) || isComplex(x) || isComplex(m) ) {

		// if ( !isComplex(x) ) x = complex(x);

		Complex period = Complex.ZERO;
		if (Math.abs(x.getReal()) > Math.PI / 2.0) {
			long p = Math.round(x.getReal() / Math.PI);
			x = new Complex(x.getReal() - p * Math.PI, x.getImaginary());
			period = ellipticPi(n, new Complex(Math.PI), m).multiply(p + p);
		}
		Complex a2;
		Complex aAdd1;
		if (x.equals(Complex.ZERO)) {
			a2 = Complex.ONE;
			aAdd1 = Complex.ZERO;
		} else {
			a2 = x.sin().pow(2.0).multiply(m).negate().add(1.0);
			aAdd1 = x.sin().pow(3);
		}
		return carlsonRF(x.cos().pow(2), a2, Complex.ONE).multiply(x.sin())
				.add(n.multiply(aAdd1.multiply(carlsonRJ(x.cos().multiply(x.cos()),
						Complex.ONE.subtract(m.multiply(x.sin()).multiply(x.sin())), Complex.ONE,
						Complex.ONE.subtract(n.multiply(x.sin()).multiply(x.sin()))).multiply(1.0 / 3.0))))
				.add(period);

		// }
	}

	public static Complex ellipticPi(double n, double x, double m) {
		if (n > 1 && x > Math.asin(1 / Math.sqrt(n))) {
			return ellipticPi(new Complex(n), new Complex(x), new Complex(m));
		}

		if (m > 1 && x > Math.asin(1 / Math.sqrt(m))) {
			return ellipticPi(new Complex(n), new Complex(x), new Complex(m));
		}

		Complex period = Complex.ZERO;
		if (Math.abs(x) > Math.PI / 2.0) {
			long p = Math.round(x / Math.PI);
			x = x - p * Math.PI;
			period = ellipticPi(n, Math.PI / 2.0, m).multiply(2 * p);
		}

		return carlsonRF(Math.pow(Math.cos(x), 2), 1 - m * Math.pow(Math.sin(x), 2), 1).multiply(Math.sin(x))
				.add(carlsonRJ(Math.pow(Math.cos(x), 2), 1 - m * Math.pow(Math.sin(x), 2), 1,
						1 - n * Math.pow(Math.sin(x), 2)).multiply(n / 3.0 * Math.pow(Math.sin(x), 3)))
				.add(period);

	}

	public static Complex jacobiZeta(Complex x, Complex m) {
		// using definition matching elliptic integrals
		// alternate definition replaces x with am(x,m)
		return ellipticE(x, m).subtract(
				ellipticF(x, m).multiply(ellipticE(new Complex(Math.PI / 2.0), m)).multiply(ellipticK(m).reciprocal()));
	}

}
