package org.matheclipse.core.numbertheory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.matheclipse.core.expression.AbstractIntegerSym;

/**
 * Class for dealing with Elliptic Curves using BigIntegers rather than 32bit
 * ints.
 */
public class BigEllipticCurve {
	private static BigDecimal EPSILON = new BigDecimal("0.000001");
	private static BigDecimal TWO = new BigDecimal("2");
	private static int ROUNDING = BigDecimal.ROUND_HALF_UP;
	
	private final BigInteger A;
	private final BigInteger N;

	public BigEllipticCurve(BigInteger A, BigInteger N) {
		this.A = A;
		this.N = N;
	}

	/**
	 * Elliptic curve point. Points can be eithe rof two catergories: <br>
	 * 1. (x,y) coordinates <br>
	 * 2. infinity - indicating the "ppint-at-infinity", which can be considered
	 * as the identity elemenet in elliptic curve arithmetic.<br>
	 *
	 *
	 */
	public class Point {
		public BigInteger x;
		public BigInteger y;
		public boolean IsInfinite;

		public Point(BigInteger u, BigInteger v, boolean isInfinite) {
			x = u;
			y = v;
			IsInfinite = isInfinite;
		}

		public Point(Point p) {
			x = p.x;
			y = p.y;
			IsInfinite = p.IsInfinite;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (IsInfinite ? 1231 : 1237);
			result = prime * result + ((x == null) ? 0 : x.hashCode());
			result = prime * result + ((y == null) ? 0 : y.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (IsInfinite != other.IsInfinite)
				return false;
			if (x == null) {
				if (other.x != null)
					return false;
			} else if (!x.equals(other.x))
				return false;
			if (y == null) {
				if (other.y != null)
					return false;
			} else if (!y.equals(other.y))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "(" + x.toString() + ", " + y.toString() + ")";
		}

		/**
		 * Negates the point (negates the y-coordinate.
		 * 
		 * @return Negation of this point.
		 */
		public Point negate() {
			return new Point(x, y.multiply(AbstractIntegerSym.BI_MINUS_ONE), IsInfinite);
		}

		/**
		 * Copies this point.
		 * 
		 * @return New point with coordinates equal to this point.
		 */
		public Point copy() {
			return new Point(x, y, IsInfinite);
		}

		private BigEllipticCurve getOuterType() {
			return BigEllipticCurve.this;
		}

	}

	/**
	 * return i Modulo N
	 *
	 * @param i
	 * @return
	 */
	private BigInteger m(BigInteger i) {
		return (i.add(N)).mod(N);
	}

	/**
	 * Adds two points on the instance's elliptic curve (curve defined by A, B,
	 * N params of the constructor).
	 *
	 * @param a
	 *            elliptic curve point
	 * @param b
	 *            elliptic curve point
	 * @return the sum a+b on the curve, will be a third point on the curve.
	 * @throws ArithmeticException
	 */
	public Point add(Point a, Point b) {
		if (a.IsInfinite)
			return new Point(b);
		else if (b.IsInfinite)
			return new Point(a);
		else if (a.x.compareTo(b.x) == 0 && a.y.compareTo(AbstractIntegerSym.BI_MINUS_ONE.multiply(b.y)) == 0) {
			return new Point(BigInteger.ZERO, BigInteger.ZERO, false);
		} else {
			BigInteger lambda;

			if (a.x.equals(b.x) && a.y.equals(b.y)) {
				BigInteger denom = m(a.y.shiftLeft(1));
				// inverse of the denominator modulo N

				BigInteger denomInv;
				try {
					denomInv = denom.modInverse(N);
				} catch (ArithmeticException e) {
					throw new EllipticCurveException(e.getMessage(), m(denom));
				}
				lambda = m((AbstractIntegerSym.BI_THREE.multiply(a.x).multiply(a.x).add(A)).multiply(denomInv));
			} else {
				lambda = m(b.y.subtract(a.y));
				// check if the two points add to infinity (zero)
				if (m(b.x.subtract(a.x)).compareTo(BigInteger.ZERO) == 0) {
					return new Point(BigInteger.ZERO, BigInteger.ZERO, true);
				}

				BigInteger inv;
				try {
					inv = b.x.subtract(a.x).modInverse(N);
				} catch (ArithmeticException e) {
					throw new EllipticCurveException(e.getMessage(), m(b.x.subtract(a.x)));
				}
				inv = m(inv);
				lambda = lambda.multiply(inv);
			}
			lambda = m(lambda);
			BigInteger newx = m(lambda.multiply(lambda).subtract(a.x).subtract(b.x));
			if (newx.compareTo(BigInteger.ZERO) < 0) {
				newx = m(newx);
			}
			BigInteger newy = m(AbstractIntegerSym.BI_MINUS_ONE.multiply(lambda).multiply(newx)
					.subtract(a.y.subtract(lambda.multiply(a.x))));

			if (newy.compareTo(BigInteger.ZERO) < 0)
				newy = m(newy);
			return new Point(newx, newy, false);
		}
	}

	/**
	 *
	 * @param d
	 * @param p
	 * @return
	 */
	public Point mul(int d, Point p) {
		Point q = new Point(BigInteger.ZERO, BigInteger.ZERO, true);
		Point r = p.copy();
		BigInteger D = BigInteger.valueOf(d);
		for (int i = 0; i < D.bitLength(); i++) {
			if (D.shiftRight(i).and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) {

				// may throw an exception -- possible factor found.
				Point s;
				s = this.add(q, r);
				q = s;
			}
			r = this.add(r, r);
		}
		return q;
	}

	/**
	 * Returns a single prime factor of the positive integer, <i>N</i>, by using
	 * <i>Lenstra's</i> factorization algorithm. If none can be found the
	 * algorithm will fallback to <i>Rho factorization</i>.
	 * 
	 * @param N
	 * @return
	 */
	public static BigInteger factorLenstra(BigInteger N) {
		if (N.isProbablePrime(100))
			return N;

		if (N.mod(AbstractIntegerSym.BI_TWO).equals(BigInteger.ZERO))
			return AbstractIntegerSym.BI_TWO;

		if (N.mod(AbstractIntegerSym.BI_THREE).equals(BigInteger.ZERO))
			return AbstractIntegerSym.BI_THREE;
		// choose random values
		final Random random = new Random();
		int k = 100;
		// int numBits=N.bitLength();
		// System.out.println(numBits);
		while (k-- > 0) {
			BigInteger x = new BigInteger(18, random).add(BigInteger.ONE).mod(N);
			BigInteger y = new BigInteger(18, random).add(BigInteger.ONE).mod(N);
			BigInteger a = new BigInteger(18, random).add(BigInteger.ONE).mod(N);

			// assume elliptic curve y^2 = x^3 +ax+b
			// set bound
			long bound = bSmoothL(N).toBigInteger().longValue();
			// prevent bound being too small.
			if (bound < 100)
				bound = 100;

			BigEllipticCurve bec = new BigEllipticCurve(a, N);
			Point p = bec.new Point(x, y, false);
			Point q = null;
			long counter = 2;
			while (counter++ < bound) {

				if (p.IsInfinite)
					break;
				try {
					q = bec.mul((int) counter, p);

				} catch (EllipticCurveException e) {
					if (e.exceptionPoint.compareTo(BigInteger.ZERO) != 0) {
						BigInteger l = e.exceptionPoint.gcd(N);
						if (l.compareTo(BigInteger.ONE) > 0 && l.compareTo(N) < 0) {

							if (l.isProbablePrime(99)) {
								System.out.println("ECF found prime factor " + l.toString());
								return l;
							} else {
								return factorLenstra(l);
							}
						}
					}

				}
				p = q;
			}
		}
		System.out.println("ECF exhausted bounds " + N.toString());
		if (N.isProbablePrime(99))
			return N;
		else
			return Primality.rho(N);

	}

	/**
	 * Base 2 log of e (~ 2.71828...)
	 */
	public static final BigDecimal LOG_2_E = new BigDecimal(
			"1.4426950408889634073599246810017822653278448610936510082308932567674020219783415086567401885986328125");

	/**
	 * Calculates the base 2 logarithm of a Bigdecimal N.
	 * 
	 * @param X
	 *            BigDecimal to find log
	 * @param scale
	 * @return Base 2 logarithm of N.
	 */
	public static BigDecimal log2(BigDecimal X, int scale) {
		if (X.compareTo(BigDecimal.ZERO) <= 0)
			throw new IllegalArgumentException("X must be positive");
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal mantissa = new BigDecimal("0.5");
		final BigDecimal TWOD = new BigDecimal(AbstractIntegerSym.BI_TWO);
		while (X.compareTo(BigDecimal.ONE) < 0 || X.compareTo(TWOD) >= 0) {
			while (X.compareTo(BigDecimal.ONE) < 0) {
				X = X.multiply(TWOD);
				result = result.subtract(BigDecimal.ONE);
			}
			while (X.compareTo(TWOD) >= 0) {
				X = X.divide(TWOD, scale, BigDecimal.ROUND_HALF_DOWN);
				result = result.add(BigDecimal.ONE);
			}
		}

		int count = scale;
		while (count-- > 0) {
			X = X.multiply(X);
			if (X.compareTo(TWOD) >= 0) {
				X = X.divide(TWOD, scale, BigDecimal.ROUND_HALF_DOWN);
				result = result.add(mantissa);
			}
			mantissa = mantissa.divide(TWOD, scale, BigDecimal.ROUND_HALF_DOWN);
		}
		return result;
	}

	/**
	 * Calculates the natural logarithm of the BigDecimal X.
	 * 
	 * @param X
	 * @param scale
	 * @return the natural logarithm of X
	 */
	private static BigDecimal ln(BigDecimal X, int scale) {
		if (X.compareTo(BigDecimal.ZERO) <= 0)
			throw new IllegalArgumentException("X must be positive " + X.toPlainString());
		return log2(X, scale).divide(LOG_2_E, scale, BigDecimal.ROUND_HALF_DOWN);
	}

	/**
	 * Calculates the base^exponent.
	 * 
	 * @param base
	 * @param exponent
	 * @return base^exponent
	 */
	public static BigDecimal exp(BigDecimal base, BigInteger exponent) {
		if (exponent.compareTo(BigInteger.ZERO) < 0) {
			return BigDecimal.ONE.divide(exp(base, new BigInteger("-1").multiply(exponent)), 100,
					BigDecimal.ROUND_HALF_DOWN);
		}
		if (exponent.compareTo(BigInteger.ZERO) == 0) {
			return new BigDecimal("1");
		} else if (exponent.compareTo(BigInteger.ONE) == 0) {
			return base;
		} else {
			if (exponent.mod(AbstractIntegerSym.BI_TWO).compareTo(BigInteger.ZERO) == 0) {
				return exp(base.pow(2), exponent.divide(AbstractIntegerSym.BI_TWO));
			} else {
				return base.multiply(
						exp(base.pow(2), exponent.subtract(BigInteger.ONE).divide(AbstractIntegerSym.BI_TWO)));
			}
		}
	}

	/**
	 * Calculates an approximation for the square root of BigDecimal value n.
	 * 
	 * @param n
	 *            the value to root.
	 * @param scale
	 * @return the root of n.
	 * @throws IllegalArgumentException
	 *             if n is negative or zero.
	 */
	public static BigDecimal squareRoot(BigDecimal n, int scale) throws IllegalArgumentException {

		if (n.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Only positive integers accepted.");
		}
		BigDecimal temp = BigDecimal.ONE;
		BigDecimal root = (n.add(n.divide(temp, scale, ROUNDING))).divide(TWO, scale, BigDecimal.ROUND_HALF_UP);
		while (root.subtract(temp).abs().compareTo(EPSILON) > 0) {
			temp = new BigDecimal(root.toString());
			root = (temp.add(n.divide(temp, scale, ROUNDING))).divide(TWO, scale, ROUNDING);
		}

		return root;
	}

	/**
	 * Calculates the function L(X) of a BigInteger X. L(X) defined as
	 * exp(sqrt(ln(x) * ln(ln(x))). <br>
	 * An Introduction to Mathematical Cryptography page 147.
	 * 
	 * @param X
	 * @return L(X)
	 */
	private static BigDecimal bSmoothL(BigInteger X) {
		final int scale = 10;
		BigDecimal lnX = ln(new BigDecimal(X), scale);
		BigDecimal lnlnX = ln(lnX, scale);
		BigDecimal expDecimal = squareRoot(lnX.multiply(lnlnX), scale);
		BigInteger expInt = expDecimal.toBigInteger();
		BigDecimal l = exp(new BigDecimal("2.7182818"), expInt);
		return l;
	}

}
