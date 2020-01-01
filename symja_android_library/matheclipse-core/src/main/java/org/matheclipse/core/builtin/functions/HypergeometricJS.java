package org.matheclipse.core.builtin.functions;

import java.util.ArrayList;
import java.util.function.IntFunction;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;

/**
 * 
 * Ported from JavaScript file
 * <a href="https://github.com/paulmasson/math/blob/master/src/functions/hypergeometric.js">hypergeometric.js</a>
 */
public class HypergeometricJS {

	public static Complex hypergeometricSeries(Complex[] A, Complex[] B, Complex x) {
		return hypergeometricSeries(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometricSeries(Complex[] A, Complex[] B, Complex x, double tolerance) {

		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		double i = 1;

		while (Math.abs(p.getReal()) > tolerance || //
				Math.abs(p.getImaginary()) > tolerance) {

			for (int j = 0; j < A.length; j++) {
				p = p.multiply(A[j]);
				A[j] = A[j].add(1.0);
			}

			for (int j = 0; j < B.length; j++) {
				p = p.divide(B[j]);
				B[j] = B[j].add(1.0);
			}

			p = p.multiply(x.divide(i));
			s = s.add(p);
			i++;

		}

		return s;
	}

	public static double hypergeometricSeries(double[] A, double[] B, double x) {
		return hypergeometricSeries(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static double hypergeometricSeries(double[] A, double[] B, double x, // boolean complexArguments,
			double tolerance) {

		double s = 1;
		double p = 1;
		double i = 1;

		while (Math.abs(p) > tolerance) {

			for (int j = 0; j < A.length; j++) {
				p *= A[j];
				A[j]++;
			}

			for (int j = 0; j < B.length; j++) {
				p /= B[j];
				B[j]++;
			}

			p *= x / i;
			s += p;
			i++;

		}

		return s;

	}

	static double hypergeometric0F1(double n, double x) {
		try {
			return de.lab4inf.math.functions.HypergeometricLimitFunction.limitSeries(n, x);
		} catch (RuntimeException rex) {
			throw new ArithmeticException("Hypergeometric0F1: " + rex.getMessage());
		}
	}
 
	public static Complex hypergeometric0F1(Complex a, Complex x) {
		return hypergeometric0F1(a, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometric0F1(Complex a, Complex x, double tolerance) {

		double useAsymptotic = 100;
		if (F.isNumIntValue(a.getReal()) && a.getReal() <= 0 && a.getImaginary() == 0) {
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

	public static Complex hypergeometric1F1(Complex a, Complex b, Complex x) {
		return hypergeometric1F1(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometric1F1(Complex a, Complex b, Complex x, double tolerance) {

		final double useAsymptotic = 30;
		if (F.isNumIntValue(b.getReal()) && b.getReal() <= 0 && F.isZero(b.getImaginary())) {
			throw new ArithmeticException("Hypergeometric function pole");
		}
		// Kummer transformation
		if (x.getReal() < 0) {
			return x.exp().multiply(hypergeometric1F1(b.subtract(a), b, x.negate()));
		}

		// asymptotic form as per Johansson arxiv.org/abs/1606.06977
		if (x.abs() > useAsymptotic) {
			Complex t1 = Arithmetic.lanczosApproxGamma(b).multiply(x.negate().pow(a.negate()))
					.multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)).reciprocal());
			t1 = t1.multiply(hypergeometric2F0(a, a.add(b.negate()).add(1), new Complex(-1.0).divide(x)));

			Complex t2 = Arithmetic.lanczosApproxGamma(b).multiply(x.pow(a.subtract(b))).multiply(x.exp())
					.multiply(Arithmetic.lanczosApproxGamma(a).reciprocal());
			t2 = t2.multiply(hypergeometric2F0(b.subtract(a), Complex.ONE.subtract(a), Complex.ONE.divide(x)));

			return t1.add(t2);
		}

		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		int i = 1;

		while (Math.abs(p.getReal()) > tolerance || //
				Math.abs(p.getImaginary()) > tolerance) {
			p = p.multiply(x).multiply(a).divide(b).divide(i);
			s = s.add(p);
			a = a.add(1);
			b = b.add(1);
			i++;
		}

		return s;

	}

	public static double hypergeometric1F1(double a, double b, double x) {
		return hypergeometric1F1(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static double hypergeometric1F1(double a, double b, double x, double tolerance) {
		double useAsymptotic = 30;
		if (F.isNumIntValue(b) && b <= 0) {
			throw new ArithmeticException("Hypergeometric function pole");
		}

		// Kummer transformation
		if (x < 0) {
			return Math.exp(x) * hypergeometric1F1(b - a, b, -x);
		}

		// asymptotic form is complex
		if (Math.abs(x) > useAsymptotic) {
			return hypergeometric1F1(new Complex(a), new Complex(b), new Complex(x)).getReal();
		}
		double s = 1;
		double p = 1;
		double i = 1;

		while (Math.abs(p) > tolerance) {
			p *= x * a / b / i;
			s += p;
			a++;
			b++;
			i++;
		}

		return s;

	}

	public static double hypergeometric2F0(double a, double b, double x) {
		return hypergeometric2F0(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometric2F0(Complex a, Complex b, Complex x) {
		return hypergeometric2F0(a, b, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

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

	public static Complex hypergeometric2F1(Complex a, Complex b, Complex c, Complex x) {
		return hypergeometric2F1(a, b, c, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometric2F1(Complex a, Complex b, Complex c, Complex x, double tolerance) {

		// choose smallest absolute value of transformed argument
		// transformations from Abramowitz & Stegun p.559
		// fewer operations compared to dlmf.nist.gov/15.8

		double[] absArray = new double[] { x.abs(), //
				x.divide(x.subtract(1)).abs(), //
				new Complex(1).subtract(x).abs(), //
				x.reciprocal().abs(), //
				new Complex(1).subtract(x).reciprocal().abs(), //
				new Complex(1).subtract(x.reciprocal()).abs() };

		double min = Double.POSITIVE_INFINITY;
		double newMin = Double.POSITIVE_INFINITY;
		int index = -1;
		for (int i = 0; i < absArray.length; i++) {
			newMin = Math.min(min, absArray[i]);
			if (newMin != min) {
				min = newMin;
				index = i;
			}
		}

		switch (index) {

		case 0:

			break;

		case 1:

			return new Complex(1.0).subtract(x).pow(a.negate())
					.multiply(hypergeometric2F1(a, c.subtract(b), c, x.divide(x.subtract(1))));

		case 2: {
			Complex t1 = Arithmetic.lanczosApproxGamma(c).multiply(Arithmetic.lanczosApproxGamma(c.subtract(a.add(b))))
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
					.multiply(hypergeometric2F1(a, b, a.add(b).add(c.negate()).add(1), new Complex(1).subtract(x)));

			Complex t2 = new Complex(1).subtract(x).pow(c.subtract(a.add(b))).multiply(Arithmetic.lanczosApproxGamma(c))
					.multiply(Arithmetic.lanczosApproxGamma(a.add(b).subtract(c)))
					.multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(b).reciprocal()).multiply(hypergeometric2F1(c.subtract(a),
							c.subtract(b), a.add(a.negate()).add(b.negate()).add(1), new Complex(1).subtract(x)));

			return t1.add(t2);
		}

		case 3: {
			Complex t1 = Arithmetic.lanczosApproxGamma(c).multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)))
					.multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal())
					.multiply(x.negate().pow(a.negate()))
					.multiply(hypergeometric2F1(a, a.add(1).add(c.negate()), a.add(1).add(b.negate()), x.reciprocal()));

			Complex t2 = Arithmetic.lanczosApproxGamma(c).multiply(Arithmetic.lanczosApproxGamma(a.subtract(b)))
					.multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal())
					.multiply(x.negate().pow(b.negate()))
					.multiply(hypergeometric2F1(b, b.add(1).add(c.negate()), b.add(1).add(a.negate()), x.reciprocal()));

			return t1.add(t2);
		}
		case 4: {
			Complex t1 = new Complex(1.0).subtract(x).pow(a.negate()).multiply(Arithmetic.lanczosApproxGamma(c))
					.multiply(Arithmetic.lanczosApproxGamma(b.subtract(a)))
					.multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal()).multiply(hypergeometric2F1(a,
							c.subtract(b), a.add(b.negate()).add(1), new Complex(1).subtract(x).reciprocal()));

			Complex t2 = new Complex(1).subtract(x).pow(b.negate()).multiply(Arithmetic.lanczosApproxGamma(c))
					.multiply(Arithmetic.lanczosApproxGamma(a.subtract(b)))
					.multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal()).multiply(hypergeometric2F1(b,
							c.subtract(a), b.add(a.negate()).add(1), new Complex(1).subtract(x).reciprocal()));

			return t1.add(t2);
		}
		case 5: {
			Complex t1 = Arithmetic.lanczosApproxGamma(c).multiply(Arithmetic.lanczosApproxGamma(c.subtract(a.add(b))))
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(b)).reciprocal()).multiply(x.pow(a.negate()))
					.multiply(hypergeometric2F1(a, a.add(c.negate()).add(1), a.add(b).add(c.negate()).add(1),
							new Complex(1).subtract(x.reciprocal())));

			Complex t2 = Arithmetic.lanczosApproxGamma(c).multiply(Arithmetic.lanczosApproxGamma(a.add(b).subtract(c)))
					.multiply(Arithmetic.lanczosApproxGamma(a).reciprocal())
					.multiply(Arithmetic.lanczosApproxGamma(b).reciprocal())
					.multiply(new Complex(1).subtract(x).pow(c.subtract(a.add(b)))).multiply(x.pow(a.subtract(c)))
					.multiply(hypergeometric2F1(c.subtract(a), new Complex(1).subtract(a),
							c.add(a.negate()).add(b.negate()).add(1), new Complex(1).subtract(x.reciprocal())));

			return t1.add(t2);
		}
		}

		if (F.isNumIntValue(c.getReal()) && c.getReal() <= 0 && F.isZero(c.getImaginary())) {
			throw new ArithmeticException("Hypergeometric function pole");
		}
		Complex s = Complex.ONE;
		Complex p = Complex.ONE;
		double i = 1;

		while (Math.abs(p.getReal()) > tolerance || Math.abs(p.getImaginary()) > tolerance) {
			p = p.multiply(x).multiply(a).multiply(b).multiply(c.reciprocal()).divide(i);
			s = s.add(p);
			a = a.add(1);
			b = b.add(1);
			c = c.add(1);
			i++;
		}

		return s;

	}

	public static double hypergeometric2F1(double a, double b, double c, double x) {

		return hypergeometric2F1(a, b, c, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static double hypergeometric2F1(double a, double b, double c, double x, double tolerance) {

		if (F.isNumIntValue(c) && c <= 0) {
			throw new ArithmeticException("Hypergeometric function pole");
		}

		// transformation from Abramowitz & Stegun p.559
		if (x < -1) {
			double t1 = Gamma.gamma(c) * Gamma.gamma(b - a) / Gamma.gamma(b) / Gamma.gamma(c - a) * Math.pow(-x, -a)
					* hypergeometric2F1(a, 1 - c + a, 1 - b + a, 1 / x);
			double t2 = Gamma.gamma(c) * Gamma.gamma(a - b) / Gamma.gamma(a) / Gamma.gamma(c - b) * Math.pow(-x, -b)
					* hypergeometric2F1(b, 1 - c + b, 1 - a + b, 1 / x);
			return t1 + t2;

		}

		if (x == -1) {
			new ArithmeticException("Unsupported real hypergeometric argument");
		}

		if (x == 1) {
			return Gamma.gamma(c) * Gamma.gamma(c - a - b) / Gamma.gamma(c - a) / Gamma.gamma(c - b);
		}

		if (x > 1) {
			new ArithmeticException("Unsupported real hypergeometric argument");
			// return hypergeometric2F1( new Complex(a), new Complex(b), new Complex(c), new Complex(x) );
		}

		double s = 1;
		double p = 1;
		double i = 1;

		while (Math.abs(p) > tolerance) {
			p *= x * a * b / c / i;
			s += p;
			a++;
			b++;
			c++;
			i++;
		}

		return s;
	}

	public static double hypergeometric1F2(double a, double b, double c, double x, double tolerance) {
		int useAsymptotic = 200;
		// asymptotic form is complex
		if (Math.abs(x) > useAsymptotic) {
			return hypergeometric1F2(new Complex(a), new Complex(b), new Complex(c), new Complex(x)).getReal();
		}
		return hypergeometricSeries(new double[] { a }, new double[] { b, c }, x);
	}

	public static Complex hypergeometricPFQ(Complex[] A, Complex[] B, Complex x) {
		return hypergeometricPFQ(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static Complex hypergeometricPFQ(Complex[] A, Complex[] B, Complex x, double tolerance) {
		// dlmf.nist.gov/16.11 for general transformations
		if (x.abs() > 1.0) {
			throw new ArithmeticException("HypergeometricPFQ: General hypergeometric argument currently restricted");
		}
		return hypergeometricSeries(A, B, x);
	}

	public static Complex hypergeometric1F2(Complex a, Complex b, Complex c, Complex x) {

		final int useAsymptotic = 200;

		if (x.abs() > useAsymptotic) {

			Complex p = a.add(b.negate()).add(c.negate()).add(0.5).divide(2.0);

			ArrayList<Complex> ck = new ArrayList<Complex>();
			ck.add(Complex.ONE); //
			ck.add(((a.multiply(3.0).add(b).add(c).add(-2.0)).multiply(a.subtract(b.add(c))).multiply(0.5))
					.add(b.multiply(c).multiply(2)).add(-3.0 / 8.0)); //
			ck.add((a.multiply(3.0).add(b).add(c).add(-2.0)).multiply(a.subtract(b.add(c))).multiply(0.25)
					.add(b.multiply(c).add(-3.0 / 16.0)).pow(2).multiply(2)); //
			ck.add(new Complex(-1.0).multiply(a.multiply(2.0).subtract(3.0)).multiply(b).multiply(c)); //
			ck.add(a.pow(2.0).multiply(-8.0).add(a.multiply(11.0)).add(b).add(c).add(-2.0)
					.multiply(a.subtract(b.add(c))).multiply(0.25)); //
			ck.add(new Complex(-3.0 / 16.0));

			IntFunction<Complex> w = k -> ck.get(k).multiply(x.negate().pow(-k / 2.0)).divide(Math.pow(2.0, k));

			Complex u1 = Complex.I.multiply(p.multiply(Math.PI).add(x.negate().sqrt().multiply(2.0))).exp();
			Complex u2 = new Complex(0.0, -1.0).multiply(p.multiply(Math.PI).add(x.negate().sqrt().multiply(2.0)))
					.exp();

			Complex wLast = w.apply(2);
			Complex w2Negate = wLast.negate();
			Complex s = u1.multiply(new Complex(0.0, -1.0).multiply(w.apply(1)).add(w2Negate).add(1.0)).add( //
					u2.multiply(Complex.I.multiply(w.apply(1)).add(w2Negate).add(1.0)));
			int k = 3;

			while (wLast.abs() > w.apply(k).abs()) {

				ck.add(//
						a.multiply(-6.0).add(b.multiply(2)).add(c.multiply(2.0)).add(-4.0).multiply(k)
								.add(a.pow(a).multiply(3.0)).add(b.subtract(c).pow(2.0).negate())
								.add(a.multiply(b.add(c).add(-2)).multiply(2.0).negate()).add(0.25).add(3.0 * k * k)
								.multiply(1.0 / (2.0 * k)).multiply(ck.get(k - 1)).subtract( //
										a.negate().add(b).add(c.negate()).add(-0.5).add(k)
												.multiply(a.negate().add(b.negate()).add(c).add(-0.5).add(k))
												.multiply(a.negate().add(b).add(c).add(-2.5).add(k))
												.multiply(ck.get(k - 2))) //
				);

				wLast = w.apply(k);
				s = s.add(u1.multiply(new Complex(0.0, -1.0).pow(k)).multiply(wLast).add( //
						u2.multiply(Complex.I.pow(k)).multiply(wLast)));
				k++;

			}

			Complex t1 = Arithmetic.lanczosApproxGamma(a).reciprocal().multiply(x.negate().pow(p)).multiply(s)
					.divide(2.0 * Math.sqrt(Math.PI));

			Complex t2 = Arithmetic.lanczosApproxGamma(b.subtract(a)).reciprocal()
					.multiply(Arithmetic.lanczosApproxGamma(c.subtract(a)).reciprocal())
					.multiply(x.negate().pow(a.negate())).multiply(
							hypergeometricPFQ(new Complex[] { a, a.add(b.negate()).add(1), a.add(c.negate().add(1.0)) },
									new Complex[] {}, x.reciprocal()));// , true ) );

			return Arithmetic.lanczosApproxGamma(b).multiply(Arithmetic.lanczosApproxGamma(c)).multiply(t1.add(t2));

		}

		return hypergeometricSeries(new Complex[] { a }, new Complex[] { b, c }, x);
	}

	public static double hypergeometric1F2(double a, double b, double c, double x) {
		final double useAsymptotic = 200;
		// asymptotic form is complex
		if (Math.abs(x) > useAsymptotic)
			return hypergeometric1F2(new Complex(a), new Complex(b), new Complex(c), new Complex(x)).getReal();

		return hypergeometricSeries(new double[] { a }, new double[] { b, c }, x);
	}

	public static double hypergeometricPFQ(double[] A, double[] B, double x) {
		return hypergeometricPFQ(A, B, x, Config.SPECIAL_FUNCTIONS_TOLERANCE);
	}

	public static double hypergeometricPFQ(double[] A, double[] B, double x, double tolerance) {
		// dlmf.nist.gov/16.11 for general transformations
		if (Math.abs(x) > 1.0) {
			throw new ArithmeticException("HypergeometricPFQ: General hypergeometric argument currently restricted");
		}
		return hypergeometricSeries(A, B, x);
	}

}
