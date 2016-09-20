package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Prime(i)</code> gives the i-th prime number for <code>i</code> less
 * equal 103000000.
 * 
 * See:
 * <a href="https://bitbucket.org/dafis/javaprimes">https://bitbucket.org/dafis/
 * javaprimes</a><br />
 * <a href=
 * "http://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number/9704912#9704912">
 * stackoverflow. com - Calculating and printing the nth prime number</a>
 */
public class Prime extends AbstractFunctionEvaluator {

	// Count number of set bits in an int
	public static int popCount(int n) {
		n -= (n >>> 1) & 0x55555555;
		n = ((n >>> 2) & 0x33333333) + (n & 0x33333333);
		n = ((n >> 4) & 0x0F0F0F0F) + (n & 0x0F0F0F0F);
		return (n * 0x01010101) >> 24;
	}

	// Speed up counting by counting the primes per
	// array slot and not individually. This yields
	// another factor of about 1.24 or so.
	public static long nthPrime(long n) {
		if (n < 2L) {
			return 2L;
		}
		if (n == 2L) {
			return 3L;
		}
		if (n == 3L) {
			return 5L;
		}
		long limit, root, count = 2;
		limit = (long) (n * (Math.log(n) + Math.log(Math.log(n)))) + 3;
		root = (long) Math.sqrt(limit);
		switch ((int) (limit % 6)) {
		case 0:
			limit = 2 * (limit / 6) - 1;
			break;
		case 5:
			limit = 2 * (limit / 6) + 1;
			break;
		default:
			limit = 2 * (limit / 6);
		}
		switch ((int) (root % 6)) {
		case 0:
			root = 2 * (root / 6) - 1;
			break;
		case 5:
			root = 2 * (root / 6) + 1;
			break;
		default:
			root = 2 * (root / 6);
		}
		int dim = (int) ((limit + 31) >> 5);
		int[] sieve = new int[dim];
		int start, s1, s2, tempi;
		for (int i = 0; i < root; ++i) {
			if ((sieve[i >> 5] & (1 << (i & 31))) == 0) {
				if ((i & 1) == 0) {
					tempi = i + i;
					start = i * (tempi + i + 10) + 7;
					s1 = tempi + 3;
					s2 = tempi + tempi + 7;
				} else {
					tempi = i + i;
					start = i * (tempi + i + 8) + 4;
					s1 = tempi + tempi + 5;
					s2 = tempi + 3;
				}
				for (long j = start; j < limit; j += s2) {
					sieve[(int) (j >> 5)] |= 1 << (j & 31);
					j += s1;
					if (j >= limit)
						break;
					sieve[(int) (j >> 5)] |= 1 << (j & 31);
				}
			}
		}
		int i;
		for (i = 0; count < n; ++i) {
			count += popCount(~sieve[i]);
		}
		--i;
		int mask = ~sieve[i];
		int p;
		for (p = 31; count >= n; --p) {
			count -= (mask >> p) & 1;
		}
		return 3 * (p + (i << 5)) + 7 + (p & 1);
	}

	public Prime() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isInteger()) {
			try {
				long nthPrime = ((IInteger) ast.arg1()).toLong();
				if (nthPrime > 103000000L) {
					return F.NIL;
				}
				return F.integer(nthPrime(nthPrime));
			} catch (RuntimeException ae) {
				ae.printStackTrace();
				return F.NIL;
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(newSymbol);
	}

}
