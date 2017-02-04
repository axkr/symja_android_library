package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.util.HashSet;

import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.BigIntegerSym;

/**
 * Elliptic curve over finite fields.
 *
 *
 */
public class EllipticCurve {
	/**
	 * A, coefficient of x^2
	 */
	public int A;
	/**
	 * B, constant
	 */
	public int B;
	/**
	 * Order of finite field (i.e. N is a prime power)
	 */
	public int N;

	/**
	 * Set of points on the curve.
	 */
	private HashSet<Point> mPointSet = new HashSet<Point>();

	/**
	 * Gets the set of all points on the curve. Is empty unless enumerateGroup()
	 * is called first.
	 * 
	 * @return The set of points on the curve.
	 */
	public HashSet<Point> getAllPoints() {
		return mPointSet;
	}

	/**
	 * 
	 * @param A
	 * @param B
	 * @param N
	 */
	public EllipticCurve(int A, int B, int N) {
		this.A = A;
		this.B = B;
		this.N = N;
	}

	/**
	 * A point on the curve.
	 * 
	 */
	public class Point {
		public int x;
		public int y;
		public boolean IsInfinite;

		public Point(int u, int v, boolean isInfinite) {
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
			result = prime * result + x;
			result = prime * result + y;
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
			if (IsInfinite == other.IsInfinite && IsInfinite)
				return true;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private EllipticCurve getOuterType() {
			return EllipticCurve.this;
		}

	}

	private static int powerMod(int a, int r, int M) {
		int c = r;
		int total = 1;
		while (c-- > 0) {
			total = (total * a) % M;
		}
		return total;
	}

	/**
	 * returns i Modulo N
	 * 
	 * @param i
	 * @return
	 */
	private int m(int i) {
		return (i + N) % N;
	}

	/**
	 * Adds two points on the instance's elliptic curve (curve defined by A, B,
	 * N params of the constructor).
	 * 
	 * @param a
	 * @param b
	 * @return the sum a+b on the curve, will be a third point on the curve.
	 */
	public Point add(Point a, Point b) {
		if (a.IsInfinite)
			return new Point(b);
		else if (b.IsInfinite)
			return new Point(a);
		else if (a.x == b.x && a.y == -1 * b.y) {
			return new Point(0, 0, true);
		} else {
			int lambda;
			int phi = BigIntegerSym.eulerPhi(BigInteger.valueOf(N)).intValueExact();
			if (a.x == b.x && a.y == b.y) {
				int denom = m(2 * a.y);
				// inverse of the denominator modulo N
				int denomInv = powerMod(m(denom), phi - 1, N);
				lambda = m((3 * a.x * a.x + A) * denomInv);
			} else {
				lambda = m(b.y - a.y);
				int inv = powerMod(m(b.x - a.x), phi - 1, N);
				inv = m(inv);
				lambda *= inv;
			}
			lambda = m(lambda);
			int newx = m(lambda * lambda - a.x - b.x);
			if (newx < 0)
				newx = m(newx);
			int newy = m(-lambda * newx - (a.y - lambda * a.x));

			if (newy < 0)
				newy = m(newy);
			return new Point(newx, newy, false);
		}
	}

	/**
	 * 
	 * @param x
	 * @param c
	 * @return
	 */
	public static Point findPoint(int x, EllipticCurve c) {
		// using eqn Y^2 = X^3 + A*X+B
		long tmp = ((long) x * x * x + (long) c.A * x + (long) c.B + (long) c.N) % c.N;
		if (AbstractIntegerSym.jacobiSymbol(tmp, (long) c.N) == 1L) {
			for (int i = 0; i < c.N; i++) {
				if ((i * i + c.N) % c.N == tmp) {
					return c.new Point(x, i, false);
				}
			}
		}
		return null;
	}

	/**
	 * Enumerates the group of points on the Elliptic curve.
	 */
	public void enumerateGroup() {
		for (int i = 0; i < N; i++) {
			Point p;
			if ((p = EllipticCurve.findPoint(i, this)) != null) {
				mPointSet.add(p);

				Point q = add(p, p);
				mPointSet.add(q);

				Point c = new Point(p);
				c.y = (N - c.y) % N;
				mPointSet.add(c);
			}
		}
	}

	/**
	 * Gives an upper bound for the number of points on the Elliptic curve
	 * instance.
	 * 
	 * @return
	 */
	public int upperBoundNumberOfPoints() {
		return N + 1 + 2 * (int) Math.ceil(Math.sqrt(N));
	}
}
