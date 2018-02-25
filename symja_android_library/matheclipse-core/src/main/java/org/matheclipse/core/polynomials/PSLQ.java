package org.matheclipse.core.polynomials;

import java.math.BigInteger;
import java.util.Arrays;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

/**
 * Implement FindIntegerNullVector() with the PSLQ algorithm.
 *
 * See <a href="https://en.wikipedia.org/wiki/Integer_relation_algorithm">Wikipedia - Integer relation algorithm</a>
 * 
 * Copied from <a href="https://github.com/CompleteMetricSpace/SymbolicMath">Github - SymbolicMath</a> project.
 */
public class PSLQ {
	public static BigInteger[] pslq(Apfloat[] x, int prec, Apfloat epsilon) {
		Apfloat gamma = new Apfloat(2).divide(ApfloatMath.sqrt(new Apfloat(3).precision(prec)));
		int n = x.length;
		Apfloat[][] A = identity(n, prec);
		Apfloat[][] B = identity(n, prec);
		Apfloat sum = Apfloat.ZERO;
		Apfloat[] s = new Apfloat[n];
		for (int k = n; k >= 1; k--) {
			sum = sum.add(x[k - 1].multiply(x[k - 1]));
			s[k - 1] = ApfloatMath.sqrt(sum);
		}
		Apfloat t = s[0];
		Apfloat[] y = new Apfloat[n];
		for (int k = 1; k <= n; k++) {
			y[k - 1] = x[k - 1].divide(t);
			s[k - 1] = s[k - 1].divide(t);
		}
		Apfloat[][] H = new Apfloat[n][n - 1];
		for (int i = 1; i <= n; i++) {
			for (int j = i + 1; j <= n - 1; j++)
				H[i - 1][j - 1] = Apfloat.ZERO;
			if (i <= n - 1)
				H[i - 1][i - 1] = s[i + 1 - 1].divide(s[i - 1]);
			for (int j = 1; j <= i - 1; j++)
				H[i - 1][j - 1] = y[i - 1].multiply(y[j - 1]).negate().divide(s[j - 1].multiply(s[j + 1 - 1]));
		}
		// System.out.println("H: "+Arrays.deepToString(H));
		for (int i = 2; i <= n; i++) {
			for (int j = i - 1; j >= 1; j--) {
				t = nInt(H[i - 1][j - 1].divide(H[j - 1][j - 1]));
				y[j - 1] = y[j - 1].add(t.multiply(y[i - 1]));
				for (int k = 1; k <= j; k++)
					H[i - 1][k - 1] = H[i - 1][k - 1].subtract(t.multiply(H[j - 1][k - 1]));
				for (int k = 1; k <= n; k++) {
					A[i - 1][k - 1] = A[i - 1][k - 1].subtract(t.multiply(A[j - 1][k - 1]));
					B[k - 1][j - 1] = B[k - 1][j - 1].add(t.multiply(B[k - 1][i - 1]));
				}
			}
		}
		// System.out.println("H: "+Arrays.deepToString(H));
		boolean done = false;
		while (!done) {
			Apfloat max = Apfloat.ZERO;
			int m = 0;
			for (int i = 1; i <= n - 1; i++) {
				Apfloat tmp = ApfloatMath.pow(gamma, i).multiply(ApfloatMath.abs(H[i - 1][i - 1]));
				if (max.compareTo(tmp) < 0) {
					max = tmp;
					m = i;
				}
			}
			System.out.println("m: " + m);
			// Change mth and m+1th value of y
			Apfloat tmp = y[m + 1 - 1];
			y[m + 1 - 1] = y[m - 1];
			y[m - 1] = tmp;

			Apfloat[] tmpm = new Apfloat[A[m + 1 - 1].length];
			for (int i = 1; i <= tmpm.length; i++)
				tmpm[i - 1] = A[m + 1 - 1][i - 1];
			for (int i = 1; i <= A[m + 1 - 1].length; i++)
				A[m + 1 - 1][i - 1] = A[m - 1][i - 1];
			for (int i = 1; i <= A[m - 1].length; i++)
				A[m - 1][i - 1] = tmpm[i - 1];

			tmpm = new Apfloat[H[m + 1 - 1].length];
			for (int i = 1; i <= tmpm.length; i++)
				tmpm[i - 1] = H[m + 1 - 1][i - 1];
			for (int i = 1; i <= H[m + 1 - 1].length; i++)
				H[m + 1 - 1][i - 1] = H[m - 1][i - 1];
			for (int i = 1; i <= H[m - 1].length; i++)
				H[m - 1][i - 1] = tmpm[i - 1];

			tmpm = new Apfloat[B.length];
			for (int i = 1; i <= B.length; i++)
				tmpm[i - 1] = B[i - 1][m + 1 - 1];
			for (int i = 1; i <= B.length; i++)
				B[i - 1][m + 1 - 1] = B[i - 1][m - 1];
			for (int i = 1; i <= B.length; i++)
				B[i - 1][m - 1] = tmpm[i - 1];

			if (m <= n - 2) {
				Apfloat t0 = ApfloatMath
						.sqrt(ApfloatMath.pow(H[m - 1][m - 1], 2).add(ApfloatMath.pow(H[m - 1][m + 1 - 1], 2)));
				Apfloat t1 = H[m - 1][m - 1].divide(t0);
				Apfloat t2 = H[m - 1][m + 1 - 1].divide(t1);
				for (int i = m; i <= n; i++) {
					Apfloat t3 = H[i - 1][m - 1];
					Apfloat t4 = H[i - 1][m + 1 - 1];
					H[i - 1][m - 1] = t1.multiply(t3).add(t2.multiply(t4));
					H[i - 1][m + 1 - 1] = t2.multiply(t3).negate().add(t1.multiply(t4));
				}
			}
			for (int i = m + 1; i <= n; i++) {
				for (int j = Math.min(i - 1, m + 1); j >= 1; j--) {
					// System.out.println("Hjj: "+H[j-1][j-1]);
					t = nInt(H[i - 1][j - 1].divide(H[j - 1][j - 1]));
					y[j - 1] = y[j - 1].add(t.multiply(y[i - 1]));
					for (int k = 1; k <= j; k++) {
						H[i - 1][k - 1] = H[i - 1][k - 1].subtract(t.multiply(H[j - 1][k - 1]));
					}
					for (int k = 1; k <= n; k++) {
						A[i - 1][k - 1] = A[i - 1][k - 1].subtract(t.multiply(A[j - 1][k - 1]));
						B[k - 1][j - 1] = B[k - 1][j - 1].add(t.multiply(B[k - 1][i - 1]));
					}
				}
			}
			// Smallest entry of y
			Apfloat min = ApfloatMath.abs(y[1 - 1]);
			int w = 1;
			for (int i = 2; i <= y.length; i++) {
				if (ApfloatMath.abs(y[i - 1]).compareTo(min) < 0) {
					w = i;
					min = ApfloatMath.abs(y[i - 1]);
				}
			}
			if (min.compareTo(epsilon) < 0) {
				BigInteger[] v = new BigInteger[B.length];
				for (int i = 1; i <= B.length; i++)
					v[i - 1] = new BigInteger(B[i - 1][w - 1].toString(true));
				return v;
			}
			System.out.println("While Loop: A: " + Arrays.deepToString(A));
			System.out.println("While Loop: B: " + Arrays.deepToString(B));
			System.out.println("While Loop: H: " + Arrays.deepToString(H));
			System.out.println("While Loop: y: " + Arrays.deepToString(y));
		}
		return null;
	}

	public static Apfloat[][] identity(int n, int prec) {
		Apfloat[][] A = new Apfloat[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (j == i)
					A[i][j] = Apfloat.ONE.precision(prec);
				else
					A[i][j] = Apfloat.ZERO;
			}
		}
		return A;
	}

	public static Apfloat nInt(Apfloat x) {
		if (x.frac().compareTo(new Apfloat("0.5")) < 0)
			return x.floor();
		return x.ceil();
	}

	public static void main(String[] args) {
		// System.out.println("pslq: " + Arrays.toString( //
		// pslq(new Apfloat[] { new Apfloat("1", 70), //
		// new Apfloat("1.61833", 70), //
		// new Apfloat("1.61833", 70).multiply(new Apfloat("1.61833", 70)) //
		// }, //
		// 70, //
		// new Apfloat("0.001", 70) //
		// )));
		// System.out.println("pslq: " + Arrays.toString( //
		// pslq(new Apfloat[] { ApfloatMath.log(new Apfloat("2", 70)), //
		// ApfloatMath.log(new Apfloat("4", 70)), //
		// }, //
		// 70, //
		// new Apfloat("0.001", 70) //
		// )));
		
		// Pi / 8
		System.out.println(ApfloatMath.pi(100).multiply(new Apfloat("0.125", 100)));
		String piD8Str = "3.926990816987241548078304229099378605246461749218882276218680740384770507857761248285043531677646333e-1";
		Apfloat piD8 = new Apfloat(piD8Str, 100);
		// sin( pi / 8 )
		Apfloat sinPiD8 = ApfloatMath.sin(piD8);
		Apfloat[] vec = new Apfloat[] { //
				// 1
				new Apfloat("1", 100), //
				// sin( pi / 8 )
				sinPiD8, //
				// sin( pi / 8 ) ^ 2
				ApfloatMath.pow(sinPiD8, 2L), //
				// sin( pi / 8 ) ^ 3
				ApfloatMath.pow(sinPiD8, 3L), //
				// sin( pi / 8 ) ^ 4
				ApfloatMath.pow(sinPiD8, 4L), //
		};
		for (int i = 0; i < vec.length; i++) {
			System.out.print(vec[i].toString() + ", ");
		}

		System.out.println("pslq: " + Arrays.toString( //
				pslq(vec, //
						100, //
						new Apfloat("0.00001", 100) //
				)));
	}
}
