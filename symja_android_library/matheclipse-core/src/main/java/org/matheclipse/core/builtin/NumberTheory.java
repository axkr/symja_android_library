package org.matheclipse.core.builtin;

import static java.lang.Math.addExact;
import static java.lang.Math.floorMod;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.subtractExact;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.util.CombinatoricsUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.GaussianInteger;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

public final class NumberTheory {

	private static final int[] FIBONACCI_45 = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597,
			2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269,
			2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 165580141, 267914296,
			433494437, 701408733, 1134903170 };

	private static final int[] BELLB_14 = { 1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570, 4213597,
			27644437, 190899322 };

	/**
	 * <pre>
	 * BellB(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the Bell number function counts the number of different ways to partition a set that has exactly <code>n</code>
	 * elements
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Bell_number">Wikipedia - Bell number</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; BellB(15)
	 * 1382958545
	 * </pre>
	 */
	private static class BellB extends AbstractFunctionEvaluator {

		/**
		 * Generates the Bell number of the given index, where B(1) is 1. This is recursive.
		 * 
		 * @param index
		 * @return
		 */
		private static BigInteger bellNumber(int index) {
			if (index < BELLB_14.length) {
				return BigInteger.valueOf(BELLB_14[index]);
			}
			if (index > 1) {
				BigInteger sum = BigInteger.ZERO;
				for (int i = 0; i < index; i++) {
					BigInteger prevBellNum = bellNumber(i);
					BigInteger binomialCoeff = BigIntegerMath.binomial(index - 1, i);
					sum = sum.add(binomialCoeff.multiply(prevBellNum));
				}
				return sum;
			}
			return BigInteger.ONE;
		}

		/**
		 * Generates the Bell polynomial of the given index, where B(1) is 1. This is recursive.
		 * 
		 * @param index
		 * @return
		 */
		private static IExpr bellBPolynomial(int n, IExpr z) {
			if (n == 0) {
				return F.C1;
			}

			if (z.isZero()) {
				return F.C0;
			}
			if (n == 1) {
				return z;
			}

			IASTAppendable sum = F.PlusAlloc(n + 1);
			for (int k = 0; k <= n; k++) {
				sum.append(F.Times(F.StirlingS2(F.ZZ(n), F.ZZ(k)), F.Power(z, k)));
			}
			return sum;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			int n = arg1.toIntDefault(Integer.MIN_VALUE);
			if (n >= 0) {
				if (ast.isAST2() && !ast.arg2().isOne()) {
					// bell polynomials: Sum(StirlingS2(n, k)* z^k, {k, 0, n})
					IExpr z = ast.arg2();
					return bellBPolynomial(n, z);
				}

				// bell numbers start here
				if (arg1.isZero()) {
					return F.C1;
				}
				BigInteger bellB = bellNumber(n);
				return F.integer(bellB);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * BernoulliB(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the Bernoulli number of the first kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a></li>
	 * </ul>
	 */
	private static class BernoulliB extends AbstractFunctionEvaluator {

		/**
		 * Compute the Bernoulli number of the first kind.
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>. <br/>
		 * For better performing implementations see
		 * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
		 * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
		 * 
		 * @param biggi
		 * @return
		 */
		public static IFraction bernoulliNumber(final IInteger biggi) {
			int n = biggi.toIntDefault(-1);
			if (n >= 0) {
				return bernoulliNumber(n);
			}
			return null;
		}

		/**
		 * Compute the Bernoulli number of the first kind.
		 * 
		 * @param n
		 * @return
		 */
		public static IFraction bernoulliNumber(int n) {
			if (n == 0) {
				return AbstractFractionSym.ONE;
			} else if (n == 1) {
				return AbstractFractionSym.valueOf(-1, 2);
			} else if (n % 2 != 0) {
				return AbstractFractionSym.ZERO;
			}
			IFraction[] bernoulli = new IFraction[n + 1];
			bernoulli[0] = AbstractFractionSym.ONE;
			bernoulli[1] = AbstractFractionSym.valueOf(-1, 2);// new
																// BigFraction(-1,
																// 2);
			for (int k = 2; k <= n; k++) {
				bernoulli[k] = AbstractFractionSym.ZERO;
				for (int i = 0; i < k; i++) {
					if (!bernoulli[i].isZero()) {
						IFraction bin = AbstractFractionSym.valueOf(BigIntegerMath.binomial(k + 1, k + 1 - i));
						bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
					}
				}
				bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));
			}
			return bernoulli[n];
		}

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1()) {
				// bernoulli number
				if (ast.arg1().isInteger()) {
					IFraction bernoulli = bernoulliNumber((IInteger) ast.arg1());
					if (bernoulli != null) {
						return bernoulli;
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * <pre>
	 * Binomial(n, k)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the binomial coefficient of the 2 integers <code>n</code> and <code>k</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Wikipedia - Binomial coefficient</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; Binomial(4,2)
	 * 6
	 * 
	 * &gt;&gt; Binomial(5, 3)   
	 * 10
	 * </pre>
	 */
	private static class Binomial extends AbstractArg2 {

		public static IInteger binomial(final IInteger n, final IInteger k) {
			// k>n : by definition --> 0
			if (k.isNegative() || k.compareTo(n) > 0) {
				return F.C0;
			}
			if (k.isZero() || k.equals(n)) {
				return F.C1;
			}

			int ni = n.toIntDefault(-1);
			if (ni >= 0) {
				int ki = k.toIntDefault(-1);
				if (ki >= 0) {
					if (ki > ni) {
						return F.C0;
					}
					return AbstractIntegerSym.valueOf(BigIntegerMath.binomial(ni, ki));
				}
			}

			IInteger bin = F.C1;
			IInteger i = F.C1;

			while (!(i.compareTo(k) > 0)) {
				bin = bin.multiply(n.subtract(i).add(F.C1)).div(i);
				i = i.add(F.C1);
			}
			return bin;
		}

		@Override
		public IExpr e2IntArg(final IInteger n, final IInteger k) {
			return binomial(n, k);
		}

		@Override
		public IExpr e2ObjArg(final IExpr n, final IExpr k) {
			if (k.isOne()) {
				return n;
			}
			if (k.isInteger()) {
				if (k.isNegative()) {
					return F.C0;
				}
				if (n.isInteger()) {
					// use e2IntArg() method
					return F.NIL;
				}
				IInteger ki = (IInteger) k;
				if (ki.isOne()) {
					return n;
				}
				if (ki.isZero()) {
					return F.C1;
				}
				if (ki.compareInt(6) < 0 && ki.compareInt(1) > 0) {
					int kInt = ki.intValue();
					IASTAppendable ast = F.TimesAlloc(kInt);
					IAST temp;
					IExpr nTemp = n;
					for (int i = 1; i <= kInt; i++) {
						temp = F.Divide(nTemp, F.integer(i));
						ast.append(temp);
						nTemp = F.eval(F.Subtract(nTemp, F.C1));
					}
					return ast;
				}
			}
			if (n.equals(k)) {
				return F.C1;
			}
			if (n instanceof INum && k instanceof INum) {
				// Gamma(n+1)/(Gamma(k+1)*Gamma(n-k+1))
				return F.Times(F.Power(F.Gamma(F.Plus(F.C1, k)), -1), F.Gamma(F.Plus(F.C1, n)),
						F.Power(F.Gamma(F.Plus(F.C1, F.Negate(k), n)), -1));
			}
			IExpr difference = F.eval(F.Subtract(n, F.C1));
			if (difference.equals(k)) {
				return n;
			}
			difference = F.eval(F.Subtract(k, n));
			if (difference.isIntegerResult() && difference.isPositiveResult()) {
				// k-n is a positive integer number
				return F.C0;
			}

			IExpr boole = F.eval(F.Greater(F.Times(F.C2, k), n));
			if (boole.isTrue()) {
				// case k*2 > n : Binomial[n, k] -> Binomial[n, n-k]
				return F.Binomial(n, F.Subtract(n, k));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * CarmichaelLambda(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the Carmichael function of <code>n</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Carmichael_function">Wikipedia - Carmichael function</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; CarmichaelLambda(35)
	 * 12
	 * </pre>
	 */
	private static class CarmichaelLambda extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).charmichaelLambda();
				} catch (ArithmeticException ae) {

				}
			} else {
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return F.CarmichaelLambda(negExpr);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * CatalanNumber(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the catalan number for the integer argument <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Catalan_number">Wikipedia - Catalan number</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; CatalanNumber(4)
	 * 14
	 * </pre>
	 */
	private static class CatalanNumber extends AbstractTrigArg1 {
		public static IInteger catalanNumber(IInteger n) {
			if (n.equals(F.CN1)) {
				return F.CN1;
			}
			n = n.add(F.C1);
			if (!(n.compareInt(0) > 0)) {
				return F.C0;
			}
			IInteger i = F.C1;
			IInteger c = F.C1;
			final IInteger temp1 = n.shiftLeft(1).subtract(F.C1);
			while (i.compareTo(n) < 0) {
				c = c.multiply(temp1.subtract(i)).div(i);
				i = i.add(F.C1);
			}
			return c.div(n);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				return catalanNumber((IInteger) arg1);
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the chinese remainder function.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href= "https://en.wikipedia.org/wiki/Chinese_remainder_theorem">Wikipedia -
	 * Chinese_remainder_theorem</a></li>
	 * <li><a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta Code - Chinese remainder
	 * theorem</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; ChineseRemainder({0,3,4},{3,4,5})
	 * 39
	 * </pre>
	 */
	private static class ChineseRemainder extends AbstractFunctionEvaluator {
		private static long bezout0(long a, long b) {
			long s = 0, old_s = 1;
			long r = b, old_r = a;

			long q;
			long tmp;
			while (r != 0) {
				q = old_r / r;

				tmp = old_r;
				old_r = r;
				r = subtractExact(tmp, multiplyExact(q, r));

				tmp = old_s;
				old_s = s;
				s = subtractExact(tmp, multiplyExact(q, s));
			}
			if (old_r != 1) {
				throw new ArithmeticException();
			}
			// assert old_r == 1 : "a = " + a + " b = " + b;
			return old_s;
		}

		private static BigInteger bezout0(BigInteger a, BigInteger b) {
			BigInteger s = BigInteger.ZERO, old_s = BigInteger.ONE;
			BigInteger r = b, old_r = a;

			BigInteger q;
			BigInteger tmp;
			while (!r.equals(BigInteger.ZERO)) {
				q = old_r.divide(r);

				tmp = old_r;
				old_r = r;
				r = tmp.subtract(q.multiply(r));

				tmp = old_s;
				old_s = s;
				s = tmp.subtract(q.multiply(s));
			}
			if (!old_r.equals(BigInteger.ONE)) {
				throw new ArithmeticException();
			}
			// assert old_r.isOne();
			return old_s;
		}

		/**
		 * Runs Chinese Remainders algorithm
		 *
		 * @param primes
		 *            list of coprime numbers
		 * @param remainders
		 *            remainder
		 * @return the result
		 */
		public static long chineseRemainders(final long[] primes, final long[] remainders) {
			if (primes.length != remainders.length)
				throw new IllegalArgumentException();

			long modulus = primes[0];
			for (int i = 1; i < primes.length; ++i) {
				if (primes[i] <= 0)
					throw new RuntimeException("Negative CRT input: " + primes[i]);
				modulus = multiplyExact(primes[i], modulus);
			}

			long result = 0;
			for (int i = 0; i < primes.length; ++i) {
				long iModulus = modulus / primes[i];
				long bezout = bezout0(iModulus, primes[i]);
				result = floorMod(addExact(result, floorMod(
						multiplyExact(iModulus, floorMod(multiplyExact(bezout, remainders[i]), primes[i])), modulus)),
						modulus);
			}
			return result;
		}

		/**
		 * Runs Chinese Remainders algorithm
		 *
		 * @param primes
		 *            list of coprime numbers
		 * @param remainders
		 *            remainder
		 * @return the result
		 */
		private static BigInteger chineseRemainders(final BigInteger[] primes, final BigInteger[] remainders) {
			if (primes.length != remainders.length)
				throw new IllegalArgumentException();
			BigInteger m = primes[0];
			for (int i = 1; i < primes.length; i++) {
				if (primes[i].signum() <= 0)
					throw new RuntimeException("Negative CRT input: " + primes[i]);
				m = primes[i].multiply(m);
			}

			BigInteger result = BigInteger.ZERO;
			for (int i = 0; i < primes.length; i++) {
				BigInteger mi = m.divide(primes[i]);
				BigInteger eea = bezout0(mi, primes[i]);
				result = result.add(mi.multiply(eea.multiply(remainders[i]).mod(primes[i]))).mod(m);
			}
			return result;
		}

		/**
		 * <p>
		 * Calculate the chinese remainder of 2 integer lists.
		 * </p>
		 * <p>
		 * See <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem"> Rosetta Code: Chinese remainder
		 * theorem</a><br/>
		 * <a href=
		 * "https://github.com/PoslavskySV/rings/blob/master/rings/src/main/java/cc/redberry/rings/bigint/ChineseRemainders.java">cc/redberry/rings/bigint/ChineseRemainders.java</a>
		 * </p>
		 *
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isList() && ast.arg2().isList()) {
				try {
					long[] a = Validate.checkListOfLongs(ast.arg1(), Long.MIN_VALUE);
					long[] n = Validate.checkListOfLongs(ast.arg2(), Long.MIN_VALUE);
					if (a.length != n.length) {
						return F.NIL;
					}
					try {
						return F.integer(chineseRemainders(n, a));
					} catch (ArithmeticException ae) {
						if (Config.SHOW_STACKTRACE) {
							ae.printStackTrace();
						}
					}
					return F.NIL;
				} catch (WrongArgumentType wat) {
					// try with BigIntegers
					BigInteger[] aBig = Validate.checkListOfBigIntegers(ast.arg1());
					BigInteger[] nBig = Validate.checkListOfBigIntegers(ast.arg2());
					if (aBig.length != nBig.length) {
						return F.NIL;
					}
					try {
						return F.integer(chineseRemainders(nBig, aBig));
					} catch (ArithmeticException ae) {
						if (Config.SHOW_STACKTRACE) {
							ae.printStackTrace();
						}
					}
					return F.NIL;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * CoprimeQ(x, y)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * tests whether <code>x</code> and <code>y</code> are coprime by computing their greatest common divisor.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia - Coprime</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; CoprimeQ(7, 9)
	 * True
	 * &gt;&gt; CoprimeQ(-4, 9)
	 * True
	 * &gt;&gt; CoprimeQ(12, 15)
	 * False 
	 * &gt;&gt; CoprimeQ(2, 3, 5)
	 * True
	 * &gt;&gt; CoprimeQ(2, 4, 5)
	 * False
	 * </pre>
	 */
	private static class CoprimeQ extends AbstractFunctionEvaluator {

		/**
		 * The integers a and b are said to be <i>coprime</i> or <i>relatively prime</i> if they have no common factor
		 * other than 1.
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia:Coprime</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			int size = ast.size();
			IExpr expr;
			for (int i = 1; i < size - 1; i++) {
				expr = ast.get(i);
				for (int j = i + 1; j < size; j++) {
					if (!F.GCD.of(engine, expr, ast.get(j)).isOne()) {
						return F.False;
					}
				}
			}
			return F.True;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	private static class CubeRoot extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr n = ast.arg1();
			if (n.isNumericFunction()) {
				if (n.isPositiveResult()) {
					return F.Power(n, F.C1D3);
				}
				return F.Times(F.CN1, F.Power(F.Negate(n), F.C1D3));
			}
			return F.Power(n, F.C1D3);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * DiracDelta(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>DiracDelta</code> function returns <code>0</code> for all real numbers <code>x</code> where
	 * <code>x != 0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; DiracDelta(-42)
	 * 0
	 * </pre>
	 * <p>
	 * <code>DiracDelta</code> doesn't evaluate for <code>0</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; DiracDelta(0)
	 * DiracDelta(0)
	 * </pre>
	 */
	private static class DiracDelta extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			IASTAppendable result = F.NIL;
			if (size > 1) {
				for (int i = 1; i < size; i++) {
					IExpr expr = ast.get(i);
					ISignedNumber temp = expr.evalSignedNumber();
					if (temp != null) {
						if (temp.isZero()) {
							return F.NIL;
						}
						return F.C0;
					}
					if (expr.isNonZeroRealResult()) {
						return F.C0;
					}
					IExpr negated = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
					if (negated.isPresent()) {
						if (!result.isPresent()) {
							result = F.ast(F.DiracDelta);
						}
						result.append(negated);
					} else {
						if (result.isPresent()) {
							result.append(expr);
						}
					}
				}
			}
			return result;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * DiscreteDelta(n1, n2, n3, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>DiscreteDelta</code> function returns <code>1</code> if all the <code>ni</code> are <code>0</code>. Returns
	 * <code>0</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; DiscreteDelta(0, 0, 0.0)
	 * 1
	 * </pre>
	 */
	private static class DiscreteDelta extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size > 1) {
				IExpr arg1 = ast.arg1();

				if (size == 2) {
					INumber temp = arg1.evalNumber();
					if (temp != null) {
						if (temp.isZero()) {
							return F.C1;
						}
						if (temp.isNumber()) {
							return F.C0;
						}
					}
					if (arg1.isNonZeroComplexResult()) {
						return F.C0;
					}
					return F.NIL;
				}

				IExpr result = removeEval(ast);
				if (result.isPresent()) {
					if (result.isAST()) {
						if (result.isAST() && ((IAST) result).size() > 1) {
							return result;
						}
						return F.C1;
					}
					return result;
				}

			}
			return F.NIL;
		}

		private static IExpr removeEval(final IAST ast) {
			IASTAppendable result = F.NIL;
			int size = ast.size();
			int j = 1;
			for (int i = 1; i < size; i++) {
				IExpr expr = ast.get(i);
				INumber temp = expr.evalNumber();
				if (temp != null) {
					if (temp.isZero()) {
						if (!result.isPresent()) {
							result = ast.removeAtClone(i);
						} else {
							result.remove(j);
						}
						continue;
					}
					if (temp.isNumber()) {
						return F.C0;
					}
				}
				if (expr.isNonZeroComplexResult()) {
					return F.C0;
				}
				j++;
			}
			return result;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Divisible(n, m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>n</code> could be divide by <code>m</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Divisible(42,7)
	 * True
	 * </pre>
	 */
	private static class Divisible extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				// thread over first list
				IAST list = (IAST) ast.arg1();
				return list.mapThread(F.ListAlloc(list.size()), ast, 1);
			}

			IExpr result = engine.evaluate(F.Divide(ast.arg1(), ast.arg2()));
			if (result.isNumber()) {
				if (result.isComplex()) {
					IComplex comp = (IComplex) result;
					if (isSignedNumberDivisible(comp.re()).isTrue() && isSignedNumberDivisible(comp.im()).isTrue()) {
						return F.True;
					}
					return F.False;
				}
				if (result.isSignedNumber()) {
					return isSignedNumberDivisible((ISignedNumber) result);
				}
				return F.False;
			}
			return F.NIL;
		}

		/**
		 * Return F.True or F.False if result is divisible. Return <code>F.NIL</code>, if the result could not be
		 * determined.
		 * 
		 * @param result
		 * @return
		 */
		private IExpr isSignedNumberDivisible(ISignedNumber result) {
			if (result.isInteger()) {
				return F.True;
			}
			if (result.isNumIntValue()) {
				// return F.True;
				try {
					result.toLong();
					return F.True;
				} catch (ArithmeticException ae) {
					return F.NIL;
				}
			}
			return F.False;
		}

	}

	/**
	 * <pre>
	 * Divisors(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns all integers that divide the integer <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Divisors(990)
	 * {1,2,3,5,6,9,10,11,15,18,22,30,33,45,55,66,90,99,110,165,198,330,495,990}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; Divisors(341550071728321)
	 * {1,10670053,32010157,341550071728321}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; Divisors(2010)
	 * {1,2,3,5,6,10,15,30,67,134,201,335,402,670,1005,2010}
	 * </pre>
	 */
	private static class Divisors extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				IInteger i = (IInteger) arg1;
				if (i.isNegative()) {
					i = i.negate();
				}
				return i.divisors();
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * DivisorSigma(k, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the sum of the <code>k</code>-th powers of the divisors of <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Divisor_function">Wikipedia - Divisor function</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; DivisorSigma(0,12)
	 * 6
	 * 
	 * &gt;&gt; DivisorSigma(1,12)
	 * 28
	 * </pre>
	 */
	private static class DivisorSigma extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg1.isOne() && arg2.isOne()) {
				return F.C1;
			}
			if (arg2.isInteger() && arg2.isPositive()) {
				IInteger n = (IInteger) arg2;
				return divisorSigma(arg1, n);
			}
			return F.NIL;
		}

		private static IExpr divisorSigma(IExpr arg1, IInteger n) {
			IAST list = n.divisors();
			if (list.isList()) {
				int size = list.size();
				if (arg1.isOne()) {
					IInteger sum = F.C0;
					for (int i = 1; i < size; i++) {
						sum = sum.add(((IInteger) list.get(i)));
					}
					return sum;
				}
				if (arg1.isInteger()) {
					// special formula if k is integer
					IInteger k = (IInteger) arg1;
					try {
						long kl = k.toLong();

						IInteger sum = F.C0;
						for (int i = 1; i < size; i++) {
							sum = sum.add(((IInteger) list.get(i)).pow(kl));
						}
						return sum;
					} catch (ArithmeticException ae) {
						//
					}
				}
				// general formula
				IASTAppendable sum = F.PlusAlloc(size);
				return sum.appendArgs(size, i -> F.Power(list.get(i), arg1));
				// for (int i = 1; i < size; i++) {
				// sum.append(F.Power(list.get(i), arg1));
				// }
				// return sum;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * EulerE(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the euler number <code>En</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Euler_number">Wikipedia - Euler number</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EulerE(6)
	 * -61
	 * </pre>
	 */
	private static class EulerE extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				int n = ((IInteger) arg1).toIntDefault(-1);
				if (n >= 0) {
					if ((n & 0x00000001) == 0x00000001) {
						return F.C0;
					}
					n /= 2;

					// The list of all Euler numbers as a vector, n=0,2,4,....
					ArrayList<IInteger> a = new ArrayList<IInteger>();
					if (a.size() == 0) {
						a.add(F.C1);
						a.add(F.C1);
						a.add(F.C5);
						a.add(AbstractIntegerSym.valueOf(61));
					}

					IInteger eulerE = eulerE(a, n);
					if (n > 0) {
						n -= 1;
						n %= 2;
						if (n == 0) {
							eulerE = eulerE.negate();
						}
					}
					return eulerE;
				}
			}
			return F.NIL;
		}

		/**
		 * Compute a coefficient in the internal table.
		 * 
		 * @param a
		 *            list of integers
		 * @param n
		 *            the zero-based index of the coefficient. n=0 for the E_0 term.
		 */
		protected void set(ArrayList<IInteger> a, final int n) {
			while (n >= a.size()) {
				IInteger val = F.C0;
				boolean sigPos = true;
				int thisn = a.size();
				for (int i = thisn - 1; i > 0; i--) {
					IInteger f = a.get(i);
					f = f.multiply(AbstractIntegerSym.valueOf(BigIntegerMath.binomial(2 * thisn, 2 * i)));
					if (sigPos)
						val = val.add(f);
					else
						val = val.subtract(f);
					sigPos = !sigPos;
				}
				if (thisn % 2 == 0)
					val = val.subtract(F.C1);
				else
					val = val.add(F.C1);
				a.add(val);
			}
		}

		/**
		 * The Euler number at the index provided.
		 * 
		 * @param a
		 *            list of integers
		 * @param n
		 *            the index, non-negative.
		 * @return the E_0=E_1=1 , E_2=5, E_3=61 etc
		 */
		public IInteger eulerE(ArrayList<IInteger> a, int n) {
			set(a, n);
			return (a.get(n));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * EulerPhi(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * compute Euler's totient function.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href= "http://en.wikipedia.org/wiki/Euler%27s_totient_function">Wikipedia - Euler's totient
	 * function</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EulerPhi(10)
	 * 4
	 * </pre>
	 */
	private static class EulerPhi extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).eulerPhi();
				} catch (ArithmeticException e) {
					// integer to large?
				}
			} else {
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return F.EulerPhi(negExpr);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * ExtendedGCD(n1, n2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the extended greatest common divisor of the given integers.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href= "https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm">Wikipedia: Extended Euclidean
	 * algorithm</a></li>
	 * <li><a href= "https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Wikipedia: Bézout's identity</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ExtendedGCD(10, 15)
	 * {5,{-1,1}}
	 * </pre>
	 * <p>
	 * <code>ExtendedGCD</code> works with any number of arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ExtendedGCD(10, 15, 7)
	 * {1,{-3,3,-2}}
	 * </pre>
	 * <p>
	 * Compute the greatest common divisor and check the result:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; numbers = {10, 20, 14};
	 * 
	 * &gt;&gt; {gcd, factors} = ExtendedGCD(Sequence @@ numbers)
	 * {2,{3,0,-2}}
	 * 
	 * &gt;&gt; Plus @@ (numbers * factors)
	 * 2
	 * </pre>
	 */
	private static class ExtendedGCD extends AbstractFunctionEvaluator {

		/**
		 * Returns the gcd of two positive numbers plus the bezout relations
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Extended_Euclidean_algorithm"> Extended Euclidean algorithm</a> and
		 * See <a href="http://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Bézout's identity</a>
		 * 
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			IExpr arg;
			for (int i = 1; i < ast.size(); i++) {
				arg = ast.get(i);
				if (!arg.isInteger()) {
					return F.NIL;
				}
				if (!((IInteger) arg).isPositive()) {
					return F.NIL;
				}
			}
			// all arguments are positive integers now

			try {

				// BigInteger factor = BigInteger.ONE;
				BigInteger[] subBezouts = new BigInteger[ast.argSize()];
				BigInteger gcd = extendedGCD(ast, subBezouts);
				// convert the Bezout numbers to sublists
				IASTAppendable subList = F.ListAlloc(subBezouts.length);
				subList.appendArgs(0, subBezouts.length, i -> F.integer(subBezouts[i]));
				// for (int i = 0; i < subBezouts.length; i++) {
				// subList.append(F.integer(subBezouts[i]));
				// }
				// create the output list
				return F.List(F.integer(gcd), subList);
			} catch (ArithmeticException ae) {
				if (Config.SHOW_STACKTRACE) {
					ae.printStackTrace();
				}
			}
			return F.NIL;
		}

		public static BigInteger extendedGCD(final IAST ast, BigInteger[] subBezouts) {
			BigInteger factor;
			BigInteger gcd = ((IInteger) ast.arg1()).toBigNumerator();
			Object[] stepResult = extendedGCD(((IInteger) ast.arg2()).toBigNumerator(), gcd);

			gcd = (BigInteger) stepResult[0];
			subBezouts[0] = ((BigInteger[]) stepResult[1])[0];
			subBezouts[1] = ((BigInteger[]) stepResult[1])[1];

			for (int i = 3; i < ast.size(); i++) {
				stepResult = extendedGCD(((IInteger) ast.get(i)).toBigNumerator(), gcd);
				gcd = (BigInteger) stepResult[0];
				factor = ((BigInteger[]) stepResult[1])[0];
				for (int j = 0; j < i - 1; j++) {
					subBezouts[j] = subBezouts[j].multiply(factor);
				}
				subBezouts[i - 1] = ((BigInteger[]) stepResult[1])[1];
			}
			return gcd;
		}

		/**
		 * Returns the gcd of two positive numbers plus the bezout relation
		 */
		public static Object[] extendedGCD(BigInteger numberOne, BigInteger numberTwo) throws ArithmeticException {
			Object[] results = new Object[2];
			BigInteger dividend;
			BigInteger divisor;
			BigInteger quotient;
			BigInteger remainder;
			BigInteger xValue;
			BigInteger yValue;
			BigInteger tempValue;
			BigInteger lastxValue;
			BigInteger lastyValue;
			BigInteger gcd = BigInteger.ONE;
			BigInteger mValue = BigInteger.ONE;
			BigInteger nValue = BigInteger.ONE;
			boolean exchange;

			remainder = BigInteger.ONE;
			xValue = BigInteger.ZERO;
			lastxValue = BigInteger.ONE;
			yValue = BigInteger.ONE;
			lastyValue = BigInteger.ZERO;
			if ((!((numberOne.compareTo(BigInteger.ZERO) == 0) || (numberTwo.compareTo(BigInteger.ZERO) == 0)))
					&& (((numberOne.compareTo(BigInteger.ZERO) == 1) && (numberTwo.compareTo(BigInteger.ZERO) == 1)))) {
				if (numberOne.compareTo(numberTwo) == 1) {
					exchange = false;
					dividend = numberOne;
					divisor = numberTwo;
				} else {
					exchange = true;
					dividend = numberTwo;
					divisor = numberOne;
				}

				BigInteger[] divisionResult = null;
				while (remainder.compareTo(BigInteger.ZERO) != 0) {
					divisionResult = dividend.divideAndRemainder(divisor);
					quotient = divisionResult[0];
					remainder = divisionResult[1];

					dividend = divisor;
					divisor = remainder;

					tempValue = xValue;
					xValue = lastxValue.subtract(quotient.multiply(xValue));
					lastxValue = tempValue;

					tempValue = yValue;
					yValue = lastyValue.subtract(quotient.multiply(yValue));
					lastyValue = tempValue;
				}

				gcd = dividend;
				if (exchange) {
					mValue = lastyValue;
					nValue = lastxValue;
				} else {
					mValue = lastxValue;
					nValue = lastyValue;
				}
			} else {
				throw new ArithmeticException("ExtendedGCD contains wrong arguments");
			}
			results[0] = gcd;
			BigInteger[] values = new BigInteger[2];
			values[0] = nValue;
			values[1] = mValue;
			results[1] = values;
			return results;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Factorial(n)
	 * 
	 * n!
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the factorial number of the integer <code>n</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Factorial(3)
	 * 6
	 * 
	 * &gt;&gt; 4!
	 * 24 
	 * 
	 * &gt;&gt; 10.5!
	 * 1.1899423083962249E7
	 * 
	 * &gt;&gt; !a! //FullForm
	 * "Not(Factorial(a))"
	 * </pre>
	 */
	private static class Factorial extends AbstractTrigArg1 {

		@Override
		public IExpr e1DblArg(final double arg1) {
			double d = org.hipparchus.special.Gamma.gamma(arg1 + 1.0);
			return F.num(d);
		}

		/**
		 * Returns the factorial of an integer n
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Factorial">Factorial</a>
		 */
		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				return factorial((IInteger) arg1);
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNegativeInfinity()) {
				return F.Indeterminate;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Factorial2(n)
	 * 
	 * n!!
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the double factorial number of the integer <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href= "http://en.wikipedia.org/wiki/Factorial#Double_factorial">Wikipedia - Double Factorial</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Factorial2(3)
	 * 3
	 * </pre>
	 */
	private static class Factorial2 extends AbstractTrigArg1 {

		public static IInteger factorial2(final IInteger iArg) {
			IInteger result = F.C1;
			final IInteger biggi = iArg;
			IInteger start;
			if (biggi.compareTo(F.C0) == -1) {
				result = F.CN1;
				if (biggi.isOdd()) {
					start = F.CN3;
				} else {
					start = F.CN2;
				}
				for (IInteger i = start; i.compareTo(biggi) >= 0; i = i.add(F.CN2)) {
					result = result.multiply(i);
				}
			} else {
				if (biggi.isOdd()) {
					start = F.C3;
				} else {
					start = F.C2;
				}
				for (IInteger i = start; i.compareTo(biggi) <= 0; i = i.add(F.C2)) {
					result = result.multiply(i);
				}
			}

			return result;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				if (!arg1.isNegative()) {
					return factorial2((IInteger) arg1);
				}
				int n = ((IInteger) arg1).toIntDefault(0);
				if (n < 0) {
					switch (n) {
					case -1:
						return F.C1;
					case -2:
						return F.CComplexInfinity;
					case -3:
						return F.CN1;
					case -4:
						return F.CComplexInfinity;
					case -5:
						return F.C1D3;
					case -6:
						return F.CComplexInfinity;
					case -7:
						return F.fraction(-1L, 15L);
					}
				}
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNegativeInfinity()) {
				return F.Indeterminate;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * FactorInteger(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the factorization of <code>n</code> as a list of factors and exponents.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; FactorInteger(990)
	 * {{2,1},{3,2},{5,1},{11,1}}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt;&gt; FactorInteger(341550071728321)
	 * {{10670053,1},{32010157,1}}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; factors = FactorInteger(2010)
	 * {{2, 1}, {3, 1}, {5, 1}, {67, 1}}
	 * </pre>
	 * <p>
	 * To get back the original number:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Times @@ Power @@@ factors
	 * 2010
	 * </pre>
	 * <p>
	 * <code>FactorInteger</code> factors rationals using negative exponents:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FactorInteger(2010 / 2011)
	 * {{2, 1}, {3, 1}, {5, 1}, {67, 1}, {2011, -1}}
	 * </pre>
	 */
	private static class FactorInteger extends AbstractEvaluator {

		@Override
		// public IExpr evaluateArg1(final IExpr arg1) {
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 2 && ast.size() <= 3) {
				IExpr arg1 = ast.arg1();
				if (ast.size() == 2) {
					if (arg1.isRational()) {
						return ((IRational) arg1).factorInteger();
					}
					return F.NIL;
				}
				if (ast.size() == 3) {
					final Options options = new Options(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption("GaussianIntegers");
					if (option.isTrue()) {
						BigInteger re = BigInteger.ONE;
						if (arg1.isInteger()) {
							re = ((IInteger) arg1).toBigNumerator();
							return GaussianInteger.factorize(re, BigInteger.ZERO, arg1);
						} else if (arg1.isComplex()) {
							IComplex c = (IComplex) arg1;
							IRational rer = c.getRealPart();
							IRational imr = c.getImaginaryPart();
							if (rer.isInteger() && imr.isInteger()) {
								re = ((IInteger) rer).toBigNumerator();
								BigInteger im = ((IInteger) imr).toBigNumerator();
								return GaussianInteger.factorize(re, im, arg1);
							}
						}
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Fibonacci(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Fibonacci number of the integer <code>n</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Fibonacci(0)
	 * 0
	 * 
	 * &gt;&gt; Fibonacci(1)
	 * 1
	 * 
	 * &gt;&gt; Fibonacci(10)
	 * 55
	 * 
	 * &gt;&gt; Fibonacci(200)
	 * 280571172992510140037611932413038677189525
	 * </pre>
	 */
	private static class Fibonacci extends AbstractTrigArg1 {

		/**
		 * <p>
		 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F
		 * </p>
		 * See: <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code: Fibonacci
		 * sequence.</a>
		 */
		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				int n = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (n > Integer.MIN_VALUE) {
					return fibonacci(n);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * JacobiSymbol(m, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * calculates the Jacobi symbol.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; JacobiSymbol(1001, 9907)
	 * -1
	 * </pre>
	 */
	private static class JacobiSymbol extends AbstractArg2 {

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			try {
				if (i0.isNegative() || i1.isNegative()) {
					// not defined for negative arguments
					return F.NIL;
				}
				return i0.jacobiSymbol(i1);
			} catch (ArithmeticException e) {
				// integer to large?
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * KroneckerDelta(arg1, arg2, ... argN)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * if all arguments <code>arg1</code> to <code>argN</code> are equal return <code>1</code>, otherwise return
	 * <code>0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; KroneckerDelta(42)
	 * 0
	 * 
	 * &gt;&gt; KroneckerDelta(42, 42.0, 42)
	 * 1
	 * </pre>
	 */
	private static class KroneckerDelta extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size == 1) {
				return F.C1;
			}
			if (size > 1) {
				IExpr arg1 = ast.arg1();
				IExpr temp = arg1.evalNumber();
				if (temp == null) {
					temp = arg1;
				}
				if (size == 2) {
					if (temp.isZero()) {
						return F.C1;
					}
					if (temp.isNonZeroComplexResult()) {
						return F.C0;
					}
					return F.NIL;
				}
				arg1 = temp;
				for (int i = 2; i < size; i++) {
					IExpr expr = ast.get(i);
					if (expr.equals(arg1)) {
						continue;
					}
					temp = expr.evalNumber();
					if (temp == null) {
						return F.NIL;
					} else {
						if (temp.equals(arg1)) {
							continue;
						}
					}
					return F.C0;
				}
				return F.C1;

			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	private static class LiouvilleLambda extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isOne()) {
				return F.C1;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				IExpr expr = F.FactorInteger.of(engine, arg1);
				if (expr.isList()) {
					IAST list = (IAST) expr;
					int result = 1;
					IInteger temp;
					for (int i = 1; i < list.size(); i++) {
						temp = (IInteger) list.get(i).second();
						if (temp.isOdd()) {
							if (result == -1) {
								result = 1;
							} else {
								result = -1;
							}
						}
					}
					if (result == -1) {
						return F.CN1;
					}
					return F.C1;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <p>
	 * Lucas number.
	 * </p>
	 * See: <a href= "https://en.wikipedia.org/wiki/Lucas_number">Wikipedia: Lucas number</a>
	 */
	private static class LucasL extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				int i = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (i > Integer.MIN_VALUE) {
					if (i < 0) {
						i *= (-1);
					}
					// LucasL(n) = Fibonacci(n-1) + Fibonacci(n+1)
					return fibonacci(i - 1).add(fibonacci(i + 1));
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	private static class MangoldtLambda extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				if (arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
					return F.C0;
				}
				IExpr expr = F.FactorInteger.of(engine, arg1);
				if (expr.isList()) {
					IAST list = (IAST) expr;
					if (list.size() == 2) {
						int result = 1;
						IInteger temp = (IInteger) list.get(1).first();
						return F.Log(temp);
					}
					return F.C0;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * MersennePrimeExponent(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>n</code>th mersenne prime exponent. <code>2^n - 1</code> must be a prime number. Currently
	 * <code>0 &lt;= n &lt;= 45</code> can be computed, otherwise the function returns unevaluated.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect numbers</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Table(MersennePrimeExponent(i), {i,20})
	 * {2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423}
	 * </pre>
	 */
	private static class MersennePrimeExponent extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger() && arg1.isPositive()) {
				int n = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (n > 0) {
					if (n > NumberTheory.MPE_45.length) {
						return F.NIL;
					}
					return F.ZZ(NumberTheory.MPE_45[n - 1]);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * MersennePrimeExponentQ(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>2^n - 1</code> is a prime number. Currently <code>0 &lt;= n &lt;= 45</code>
	 * can be computed in reasonable time.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect numbers</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Select(Range(10000), MersennePrimeExponentQ)
	 * {2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423,9689,9941}
	 * </pre>
	 */
	private static class MersennePrimeExponentQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (!arg1.isInteger() || arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
				return F.False;
			}

			try {
				long n = ((IInteger) arg1).toLong();
				if (n <= MPE_45[MPE_45.length - 1]) {
					for (int i = 0; i < MPE_45.length; i++) {
						if (MPE_45[i] == n) {
							return F.True;
						}
					}
					return F.False;
				}
				if (n < Integer.MAX_VALUE) {
					// 2^n - 1
					BigInteger b2nm1 = BigInteger.ONE.shiftLeft((int) n).subtract(BigInteger.ONE);
					return F.bool(b2nm1.isProbablePrime(32));
				}
			} catch (ArithmeticException ae) {
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * MoebiusMu(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * calculate the Möbius function.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/M%C3%B6bius_function">Wikipedia - Möbius function</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MoebiusMu(30)
	 * -1
	 * </pre>
	 */
	private static class MoebiusMu extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).moebiusMu();
				} catch (ArithmeticException e) {
					// integer to large?
				}
			} else {
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return F.MoebiusMu(negExpr);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Multinomial(n1, n2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the multinomial coefficient <code>(n1+n2+...)!/(n1! n2! ...)</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Multinomial_coefficient">Wikipedia: Multinomial coefficient</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Multinomial(2, 3, 4, 5)
	 * 2522520
	 * 
	 * &gt;&gt; Multinomial()
	 * 1
	 * </pre>
	 * <p>
	 * <code>Multinomial(n-k, k)</code> is equivalent to <code>Binomial(n, k)</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Multinomial(2, 3)
	 * 10
	 * </pre>
	 */
	private static class Multinomial extends AbstractFunctionEvaluator {

		public static IInteger multinomial(final IAST ast) {
			IInteger[] k = new IInteger[ast.argSize()];
			IInteger n = F.C0;
			for (int i = 1; i < ast.size(); i++) {
				k[i - 1] = (IInteger) ast.get(i);
				n = n.add(k[i - 1]);
			}

			IInteger result = factorial(n);
			for (int i = 0; i < k.length; i++) {
				result = result.div(factorial(k[i]));
			}
			return result;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1);

			if (ast.isAST1()) {
				return F.C1;
			}
			if (ast.isAST2()) {
				return F.Binomial(F.Plus(ast.arg1(), ast.arg2()), ast.arg1());
			}
			if (ast.exists(x -> (!x.isInteger()) || ((IInteger) x).isNegative())) {
				return F.NIL;
			}

			return multinomial(ast);

		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia: Multiplicative order</a> and
	 * <a href="https://rosettacode.org/wiki/Multiplicative_order">Rosettacode. org: Multiplicative order</a>.
	 *
	 */
	private static class MultiplicativeOrder extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isInteger() && ast.arg2().isInteger()) {
				try {
					IInteger k = ast.getInt(1);
					IInteger n = ast.getInt(2);
					if (n.isNegative()) {
						return F.NIL;
					}

					if (!k.gcd(n).isOne()) {
						return F.NIL;
					}

					return F.ZZ(Primality.multiplicativeOrder(k.toBigNumerator(), n.toBigNumerator()));
				} catch (ArithmeticException ae) {

				}

			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * NextPrime(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the next prime after <code>n</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NextPrime(n, k)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the <code>k</code>th prime after <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NextPrime(10000)
	 * 10007
	 * &gt;&gt; NextPrime(100, -5)
	 * 73
	 * &gt;&gt; NextPrime(10, -5)
	 * -2
	 * &gt;&gt; NextPrime(100, 5)
	 * 113
	 * &gt;&gt; NextPrime(5.5, 100)
	 * 563
	 * &gt;&gt; NextPrime(5, 10.5)
	 * NextPrime(5, 10.5)
	 * </pre>
	 */
	private static class NextPrime extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1() && ast.arg1().isInteger()) {
				BigInteger primeBase = ((IInteger) ast.arg1()).toBigNumerator();
				return F.integer(primeBase.nextProbablePrime());
			} else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {

				BigInteger primeBase = ((IInteger) ast.arg1()).toBigNumerator();
				final int n = Validate.checkIntType(ast, 2, 1);
				BigInteger temp = primeBase;
				for (int i = 0; i < n; i++) {
					temp = temp.nextProbablePrime();
				}
				return F.integer(temp);

			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * PartitionsP(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the number of unrestricted partitions of the integer <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PartitionsP(50)
	 * 204226
	 * 
	 * &gt;&gt; PartitionsP(6)
	 * 11
	 * 
	 * &gt;&gt; IntegerPartitions(6)
	 * {{6},{5,1},{4,2},{4,1,1},{3,3},{3,2,1},{3,1,1,1},{2,2,2},{2,2,1,1},{2,1,1,1,1},{1,1,1,1,1,1}}
	 * </pre>
	 */
	private static class PartitionsP extends AbstractFunctionEvaluator {

		private static class BigIntegerPartitionsP {
			/**
			 * The list of all partitions as a java.util.List.
			 */
			protected ArrayList<BigInteger> fList = new ArrayList<BigInteger>();

			/**
			 * Default constructor initializing a list of partitions up to 7.
			 */
			public BigIntegerPartitionsP() {
				fList.add(BigInteger.valueOf(1));
				fList.add(BigInteger.valueOf(1));
				fList.add(BigInteger.valueOf(2));
				fList.add(BigInteger.valueOf(3));
				fList.add(BigInteger.valueOf(5));
				fList.add(BigInteger.valueOf(7));
			}

			/**
			 * Return the number of partitions of i
			 * 
			 * @param n
			 *            the zero-based index into the list of partitions
			 * @param capacity
			 *            capacity of the list which should be ensured
			 * @return the ith partition number. This is 1 if i=0 or 1, 2 if i=2 and so forth.
			 */
			private BigInteger sumPartitionsP(int n, int capacity) {
				fList.ensureCapacity(capacity);
				while (fList.size() <= capacity) {
					BigInteger per = BigInteger.ZERO;
					BigInteger cursiz = BigInteger.valueOf(fList.size());
					for (int k = 0; k < fList.size(); k++) {
						BigInteger tmp = fList.get(k).multiply(NumberTheory.divisorSigma(1, fList.size() - k));
						per = per.add(tmp);
					}
					fList.add(per.divide(cursiz));
				}
				return fList.get(n);
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.C1;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				if (arg1.isOne()) {
					return F.C1;
				}
				if (arg1.equals(F.C2)) {
					return F.C2;
				}
				if (arg1.equals(F.C3)) {
					return F.C3;
				}
				try {
					IExpr result = F.REMEMBER_INTEGER_CACHE.get(ast, new Callable<IExpr>() {
						@Override
						public IExpr call() throws Exception {
							return sumPartitionsP(engine, (IInteger) arg1);
						}

					});
					if (result != null) {
						return result;
					}
				} catch (ArithmeticException e) {
					// e.printStackTrace();
				} catch (MathException e) {
					// e.printStackTrace();
				} catch (ExecutionException e) {
					// e.printStackTrace();
				}
				return F.NIL;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			return F.NIL;
		}

		/**
		 * 
		 * @param engine
		 * @param n
		 *            positive integer number
		 * @return
		 */
		private static IExpr sumPartitionsP(EvalEngine engine, IInteger n) {
			int i = n.toIntDefault(Integer.MIN_VALUE);
			if (i >= 0) {
				if (i < Integer.MAX_VALUE - 3) {
					BigIntegerPartitionsP bipp = new BigIntegerPartitionsP();
					return F.ZZ(bipp.sumPartitionsP(i, i + 3));
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * PartitionsQ(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the number of partitions of the integer <code>n</code> into distinct parts
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PartitionsQ(50)
	 * 3658
	 * 
	 * &gt;&gt; PartitionsQ(6)
	 * 4
	 * </pre>
	 */
	private static class PartitionsQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.C1;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				if (arg1.isOne()) {
					return F.C1;
				}
				if (arg1.equals(F.C2)) {
					return F.C1;
				}
				if (arg1.equals(F.C3)) {
					return F.C2;
				}

				try {
					IInteger n = (IInteger) arg1;
					if (n.isLessThan(F.ZZ(201))) {
						IExpr result = F.REMEMBER_INTEGER_CACHE.get(ast, new Callable<IExpr>() {
							@Override
							public IExpr call() throws Exception {
								return partitionsQ(engine, (IInteger) arg1);
							}

						});
						if (result != null) {
							return result;
						}
					}
				} catch (ArithmeticException e) {
					// e.printStackTrace();
				} catch (MathException e) {
					// e.printStackTrace();
				} catch (ExecutionException e) {
					// e.printStackTrace();
				}
				return F.NIL;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			return F.NIL;
		}

		/**
		 * TODO because of recursion you can get stack-overflows
		 * 
		 * @param engine
		 * @param n
		 * @return
		 */
		private static IExpr partitionsQ(EvalEngine engine, IInteger n) {
			// (1/n)*Sum(DivisorSigma(1, k)*PartitionsQ(n - k), {k, 1, n}) -
			// (2/n)*Sum(DivisorSigma(1, k)*PartitionsQ(n
			// - 2*k), {k, 1, Floor(n/2)})
			IFraction nInverse = F.QQ(F.C1, n);
			IExpr sum1 = sumPartitionsQ1(engine, n);
			if (!sum1.isPresent()) {
				return F.NIL;
			}
			IExpr sum2 = sumPartitionsQ2(engine, n);
			if (!sum2.isPresent()) {
				return F.NIL;
			}
			return engine.evaluate(Plus(Times(nInverse, sum1), Times(F.CN2, nInverse, sum2)));
		}

		private static IExpr sumPartitionsQ1(EvalEngine engine, IInteger n) {
			IInteger sum = F.C0;
			int nInt = n.toIntDefault(Integer.MIN_VALUE);
			if (nInt >= 0) {
				for (int k = 1; k <= nInt; k++) {
					IExpr temp = termPartitionsQ1(engine, n, k);
					if (!temp.isInteger()) {
						return F.NIL;
					}
					sum = sum.add((IInteger) temp);
				}
			}
			return sum;
		}

		private static IExpr termPartitionsQ1(EvalEngine engine, IInteger n, int k) {
			// DivisorSigma(1, k)*PartitionsQ(n - k)
			IInteger k2 = F.ZZ(k);
			return engine.evaluate(Times(F.DivisorSigma(C1, k2), F.PartitionsQ(Plus(Negate(k2), n))));
		}

		private static IExpr sumPartitionsQ2(EvalEngine engine, IInteger n) {
			int floorND2 = n.div(F.C2).toInt();
			IInteger sum = F.C0;
			for (int k = 1; k <= floorND2; k++) {
				IExpr temp = termPartitionsQ2(engine, n, k);
				if (!temp.isInteger()) {
					return F.NIL;
				}
				sum = sum.add((IInteger) temp);
			}
			return sum;
		}

		private static IExpr termPartitionsQ2(EvalEngine engine, IInteger n, int k) {
			// DivisorSigma(1, k)*PartitionsQ(n - 2*k)
			IInteger k2 = F.ZZ(k);
			return engine.evaluate(Times(F.DivisorSigma(C1, k2), F.PartitionsQ(Plus(Times(F.CN2, k2), n))));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	private static class PerfectNumber extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger() && arg1.isPositive()) {
				int n = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (n >= 0) {
					if (n > NumberTheory.MPE_45.length) {
						return F.NIL;
					}
					if (n <= NumberTheory.PN_8.length) {
						return F.ZZ(NumberTheory.PN_8[n - 1]);
					}
					// 2^p
					BigInteger b2p = BigInteger.ONE.shiftLeft(NumberTheory.MPE_45[n - 1]);
					// 2^(p-1)
					BigInteger b2pm1 = b2p.shiftRight(1);
					return F.ZZ(b2p.subtract(BigInteger.ONE).multiply(b2pm1));
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private static class PerfectNumberQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (!arg1.isInteger() || arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
				return F.False;
			}

			IInteger n = (IInteger) arg1;

			try {
				long value = n.toLong();
				if (value > 0 && value <= PN_8[PN_8.length - 1]) {
					for (int i = 0; i < PN_8.length; i++) {
						if (PN_8[i] == value) {
							return F.True;
						}
					}
					return F.False;
				}
			} catch (ArithmeticException ae) {
				return F.NIL;
			}

			IAST list = n.divisors();
			if (list.isList()) {
				IInteger sum = F.C0;
				int size = list.argSize();
				for (int i = 1; i < size; i++) {
					sum = sum.add((IInteger) list.get(i));
				}
				return F.bool(sum.equals(n));
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Prime(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>n</code>th prime number.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Prime(1)
	 * 2
	 * &gt;&gt; Prime(167)
	 * 991
	 * </pre>
	 */
	private static class Prime extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				int nthPrime = ((IInteger) ast.arg1()).toIntDefault(Integer.MIN_VALUE);
				if (nthPrime < 0 || nthPrime > 103000000) {
					return F.NIL;
				}
				try {
					return F.integer(Primality.prime(nthPrime));
				} catch (RuntimeException ae) {
					if (Config.SHOW_STACKTRACE) {
						ae.printStackTrace();
					}
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

	private static class PrimePi extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isNegative() || arg1.isOne() || arg1.isZero()) {
				return F.C0;
			}

			IExpr x = F.NIL;
			if (arg1.isInteger()) {
				x = arg1;
			} else if (arg1.isSignedNumber() && arg1.isPositive()) {
				x = engine.evaluate(((ISignedNumber) arg1).floorFraction());
			} else {
				ISignedNumber sn = arg1.evalSignedNumber();
				if (sn != null) {
					x = engine.evaluate(sn.floorFraction());
				}
			}

			if (x.isInteger() && x.isPositive()) {
				// TODO improve performance by caching some values?

				int maxK = ((IInteger) x).toIntDefault(Integer.MIN_VALUE);
				if (maxK >= 0) {
					int result = 0;
					BigInteger temp = BigInteger.ONE;
					for (int i = 2; i <= maxK; i++) {
						temp = temp.nextProbablePrime();
						if (temp.intValue() > maxK) {
							break;
						}
						result++;
					}
					return F.integer(result);
				}
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * PrimeOmega(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the sum of the exponents of the prime factorization of <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; PrimeOmega(990)
	 * {{2,1},{3,2},{5,1},{11,1}}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt;&gt; PrimeOmega(341550071728321)
	 * {{10670053,1},{32010157,1}}
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; PrimeOmega(2010)
	 * {{2, 1}, {3, 1}, {5, 1}, {67, 1}}
	 * </pre>
	 */
	private static class PrimeOmega extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.NIL;
			}
			if (arg1.isOne()) {
				return F.C0;
			}
			if (arg1.isInteger()) {
				if (arg1.isNegative()) {
					arg1 = arg1.negate();
				}
				SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
				Primality.factorInteger(((IInteger) arg1).toBigNumerator(), map);
				BigInteger sum = BigInteger.ZERO;
				for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
					sum = sum.add(BigInteger.valueOf(entry.getValue()));
				}
				return F.ZZ(sum);
			} else {
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return F.PrimeOmega(negExpr);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * PrimePowerQ(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>n</code> is a power of a prime number.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PrimePowerQ(9)
	 * True
	 * 
	 * &gt;&gt; PrimePowerQ(52142)
	 * False
	 * 
	 * &gt;&gt; PrimePowerQ(-8)
	 * True
	 * 
	 * &gt;&gt; PrimePowerQ(371293)
	 * True
	 * 
	 * &gt;&gt; PrimePowerQ(1)
	 * False
	 * </pre>
	 */
	private static class PrimePowerQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				return F.bool(Primality.isPrimePower(((IInteger) arg1).toBigNumerator()));
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * PrimitiveRootList(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the list of the primitive roots of <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PrimitiveRootList(37)
	 * {2,5,13,15,17,18,19,20,22,24,32,35}
	 * 
	 * &gt;&gt; PrimitiveRootList(127)
	 * {3,6,7,12,14,23,29,39,43,45,46,48,53,55,56,57,58,65,67,78,83,85,86,91,92,93,96,97,101,106,109,110,112,114,116,118}
	 * </pre>
	 */
	private static class PrimitiveRootList extends AbstractTrigArg1 {

		/**
		 * See: <a href= "http://exploringnumbertheory.wordpress.com/2013/09/09/finding-primitive-roots/"> Exploring
		 * Number Theory - Finding Primitive Roots</a>
		 */
		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					IInteger[] roots = ((IInteger) arg1).primitiveRootList();
					if (roots != null) {
						int size = roots.length;
						IASTAppendable list = F.ListAlloc(size);
						return list.appendArgs(0, size, i -> roots[i]);
					}
				} catch (ArithmeticException e) {
					// integer to large?
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * SquareFreeQ(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>n</code> a square free integer number or a square free univariate polynomial.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SquareFreeQ(9)
	 * False
	 * 
	 * &gt;&gt; SquareFreeQ(105)
	 * True
	 * </pre>
	 */
	private final static class SquareFreeQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			VariablesSet eVar = new VariablesSet(ast.arg1());
			if (eVar.isSize(0)) {
				IExpr arg1 = ast.arg1();
				if (arg1.isZero()) {
					return F.False;
				}
				if (arg1.isInteger()) {
					return F.bool(Primality.isSquareFree(((IInteger) arg1).toBigNumerator()));
				}
				if (arg1.isAtom()) {
					return F.False;
				}
			}
			if (!eVar.isSize(1)) {
				throw new WrongArgumentType(ast, ast.arg1(), 1,
						"SquareFreeQ only implemented for univariate polynomials");
			}
			try {
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				List<IExpr> varList = eVar.getVarList().copyTo();

				if (ast.isAST2()) {
					return F.bool(isSquarefreeWithOption(ast, expr, varList, engine));
				}
				return F.bool(isSquarefree(expr, varList));
			} catch (JASConversionException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		public static boolean isSquarefree(IExpr expr, List<IExpr> varList) throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);

			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
			return factorAbstract.isSquarefree(poly);
		}

		public static boolean isSquarefreeWithOption(final IAST lst, IExpr expr, List<IExpr> varList,
				final EvalEngine engine) throws JASConversionException {
			final Options options = new Options(lst.topHead(), lst, 2, engine);
			IExpr option = options.getOption("Modulus");
			if (option.isSignedNumber()) {

				// found "Modulus" option => use ModIntegerRing
				ModIntegerRing modIntegerRing = JASConvert.option2ModIntegerRing((ISignedNumber) option);
				JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
				GenPolynomial<ModInteger> poly = jas.expr2JAS(expr, false);

				FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
				return factorAbstract.isSquarefree(poly);
			}
			// option = options.getOption("GaussianIntegers");
			// if (option.equals(F.True)) {
			// try {
			// ComplexRing<edu.jas.arith.BigInteger> fac = new
			// ComplexRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ONE);
			//
			// JASConvert<edu.jas.structure.Complex<edu.jas.arith.BigInteger>> jas =
			// new JASConvert<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>(
			// varList, fac);
			// GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
			// poly = jas.expr2Poly(expr);
			// FactorAbstract<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
			// factorAbstract = FactorFactory
			// .getImplementation(fac);
			// SortedMap<GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>,
			// Long> map = factorAbstract.factors(poly);
			// IAST result = F.Times();
			// for
			// (SortedMap.Entry<GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>,
			// Long> entry : map.entrySet()) {
			// GenPolynomial<edu.jas.structure.Complex<edu.jas.arith.BigInteger>>
			// singleFactor = entry.getKey();
			// // GenPolynomial<edu.jas.arith.BigComplex> integerCoefficientPoly
			// // = (GenPolynomial<edu.jas.arith.BigComplex>) jas
			// // .factorTerms(singleFactor)[2];
			// // Long val = entry.getValue();
			// // result.add(F.Power(jas.integerPoly2Expr(integerCoefficientPoly),
			// // F.integer(val)));
			// System.out.println(singleFactor);
			// }
			// return result;
			// } catch (ArithmeticException ae) {
			// // toInt() conversion failed
			// if (Config.DEBUG) {
			// ae.printStackTrace();
			// }
			// return null; // no evaluation
			// }
			// }
			return false; // no evaluation
		}

	}

	/**
	 * <pre>
	 * StirlingS1(n, k)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Stirling numbers of the first kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href= "https://en.wikipedia.org/wiki/Stirling_numbers_of_the_first_kind">Wikipedia - Stirling numbers of
	 * the first kind</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; StirlingS1(9, 6)
	 * -4536
	 * </pre>
	 */
	private static class StirlingS1 extends AbstractFunctionEvaluator {

		private static IExpr stirlingS1(IInteger n, IInteger m) {
			if (n.isZero() && m.isZero()) {
				return F.C1;
			}
			if (n.isZero() && m.isPositive()) {
				return C0;
			}
			IInteger nSubtract1 = n.subtract(F.C1);
			if (n.isPositive() && m.isOne()) {
				return Times(Power(F.CN1, nSubtract1), F.Factorial(nSubtract1));
			}
			IInteger factorPlusMinus1;
			if (n.isPositive() && m.equals(F.C2)) {
				if (n.isOdd()) {
					factorPlusMinus1 = F.CN1;
				} else {
					factorPlusMinus1 = F.C1;
				}
				return Times(factorPlusMinus1, F.Factorial(nSubtract1), F.HarmonicNumber(nSubtract1));
			}

			IInteger nSubtractm = n.subtract(m);

			IInteger nTimes2Subtractm = n.add(n.subtract(m));

			int counter = nSubtractm.toIntDefault(Integer.MIN_VALUE);
			if (counter > Integer.MIN_VALUE) {
				counter++;
				IInteger k;
				IASTAppendable temp = F.PlusAlloc(counter >= 0 ? counter : 0);
				for (int i = 0; i < counter; i++) {
					k = F.integer(i);
					if ((i & 1) == 1) { // isOdd(i) ?
						factorPlusMinus1 = F.CN1;
					} else {
						factorPlusMinus1 = F.C1;
					}
					temp.append(Times(factorPlusMinus1, Binomial(Plus(k, nSubtract1), Plus(k, nSubtractm)),
							Binomial(nTimes2Subtractm, F.Subtract(nSubtractm, k)),
							F.StirlingS2(Plus(k, nSubtractm), k)));

				}
				return temp;
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr nArg1 = ast.arg1();
			IExpr mArg2 = ast.arg2();
			if (nArg1.isNegative() || mArg2.isNegative()) {
				return F.NIL;
			}
			if (nArg1.isInteger() && mArg2.isInteger()) {
				return stirlingS1((IInteger) nArg1, (IInteger) mArg2);
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * StirlingS2(n, k)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Stirling numbers of the second kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href= "http://en.wikipedia.org/wiki/Stirling_numbers_of_the_second_kind">Wikipedia - Stirling numbers of
	 * the second kind</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; StirlingS2(10, 6)
	 * 22827
	 * </pre>
	 */
	private static class StirlingS2 extends AbstractFunctionEvaluator {

		/**
		 * Returns the Stirling number of the second kind, "{@code S(n,k)}", the number of ways of partitioning an
		 * {@code n}-element set into {@code k} non-empty subsets.
		 * 
		 * @param n
		 *            the size of the set
		 * @param k
		 *            the number of non-empty subsets
		 * @param ki
		 *            the number of non-empty subsets as int value
		 * @return {@code S(nArg1,kArg2)}
		 */
		private static IExpr stirlingS2(IInteger n, IInteger k, int ki) {
			try {
				int ni = n.toIntDefault(0);
				if (ni != 0 && ni <= 25) {// S(26,9) = 11201516780955125625 is larger than Long.MAX_VALUE
					return F.ZZ(CombinatoricsUtils.stirlingS2(ni, ki));
				}
			} catch (MathRuntimeException mre) {
				if (Config.DEBUG) {
					mre.printStackTrace();
				}
			}
			IASTAppendable temp = F.PlusAlloc(ki >= 0 ? ki : 0);
			for (int i = 0; i < ki; i++) {
				if ((i & 1) == 1) { // isOdd(i) ?
					temp.append(Times(Negate(Binomial(k, integer(i))), Power(Plus(k, integer(-i)), n)));
				} else {
					temp.append(Times(Times(Binomial(k, integer(i))), Power(Plus(k, integer(-i)), n)));
				}
			}
			return Times(Power(Factorial(k), CN1), temp);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr nArg1 = ast.arg1();
			IExpr kArg2 = ast.arg2();
			if (nArg1.isNegative() || kArg2.isNegative()) {
				return F.NIL;
			}
			if (nArg1.isZero() && kArg2.isZero()) {
				return F.C1;
			}
			if (nArg1.isInteger() && kArg2.isInteger()) {
				IInteger ki = (IInteger) kArg2;
				if (ki.greaterThan(nArg1).isTrue()) {
					return C0;
				}
				if (ki.isZero()) {
					return C0;
				}
				if (ki.isOne()) {
					// {n,1}==1
					return C1;
				}
				if (ki.equals(C2)) {
					// {n,2}==2^(n-1)-1
					return Subtract(Power(C2, Subtract(nArg1, C1)), C1);
				}

				int k = ki.toIntDefault(0);
				if (k != 0) {
					return stirlingS2((IInteger) nArg1, ki, k);
				}
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Subfactorial(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the subfactorial number of the integer <code>n</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Subfactorial(12)
	 * 176214841
	 * </pre>
	 */
	private static class Subfactorial extends AbstractTrigArg1 {

		/**
		 * <p>
		 * Iterative subfactorial algorithm based on the recurrence:
		 * <code>Subfactorial(n) = n * Subfactorial(n-1) + (-1)^n</code>
		 * </p>
		 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
		 * 
		 * <pre>
		 * result = 1;
		 * for (long i = 3; i &lt;= n; i++) {
		 *   result = (result * i);
		 *   if (i is ODD) {
		 *     result = (result - 1);
		 *   } else {
		 *     result = (result + 1);
		 *   }
		 * }
		 * </pre>
		 * 
		 * @param n
		 * @return
		 */
		private static IInteger subFactorial(final long n) {
			if (0L <= n && n <= 2L) {
				return n != 1L ? F.C1 : F.C0;
			}
			IInteger result = F.C1;
			boolean isOdd = true;
			for (long i = 3; i <= n; i++) {
				result = AbstractIntegerSym.valueOf(i).multiply(result);
				if (isOdd) {
					// result = (result - 1)
					result = result.subtract(F.C1);
					isOdd = false;
				} else {
					// result = (result + 1)
					result = result.add(F.C1);
					isOdd = true;
				}
			}
			return result;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger() && arg1.isPositive()) {
				try {
					long n = ((IInteger) arg1).toLong();
					return subFactorial(n);
				} catch (ArithmeticException ae) {
					EvalEngine.get().printMessage("Subfactorial: argument n is to big.");
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Unitize(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * maps a non-zero <code>expr</code> to <code>1</code>, and a zero <code>expr</code> to <code>0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Unitize((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)
	 * 0
	 * </pre>
	 */
	private static class Unitize extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size == 2) {
				IExpr arg1 = ast.arg1();
				if (arg1.isNumber()) {
					return arg1.isZero() ? F.C0 : F.C1;
				}
				if (F.PossibleZeroQ.ofQ(engine, arg1)) {
					return F.C0;
				}
				IExpr temp = arg1.evalNumber();
				if (temp == null) {
					temp = arg1;
				}
				if (temp.isNumber()) {
					return temp.isZero() ? F.C0 : F.C1;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	static {

		F.BellB.setEvaluator(new BellB());
		F.BernoulliB.setEvaluator(new BernoulliB());
		F.Binomial.setEvaluator(new Binomial());
		F.CarmichaelLambda.setEvaluator(new CarmichaelLambda());
		F.CatalanNumber.setEvaluator(new CatalanNumber());
		F.ChineseRemainder.setEvaluator(new ChineseRemainder());
		F.CoprimeQ.setEvaluator(new CoprimeQ());
		F.CubeRoot.setEvaluator(new CubeRoot());
		F.DiracDelta.setEvaluator(new DiracDelta());
		F.DiscreteDelta.setEvaluator(new DiscreteDelta());
		F.Divisible.setEvaluator(new Divisible());
		F.Divisors.setEvaluator(new Divisors());
		F.DivisorSigma.setEvaluator(new DivisorSigma());
		F.EulerE.setEvaluator(new EulerE());
		F.EulerPhi.setEvaluator(new EulerPhi());
		F.ExtendedGCD.setEvaluator(new ExtendedGCD());
		F.Factorial.setEvaluator(new Factorial());
		F.Factorial2.setEvaluator(new Factorial2());
		F.FactorInteger.setEvaluator(new FactorInteger());
		F.Fibonacci.setEvaluator(new Fibonacci());
		F.JacobiSymbol.setEvaluator(new JacobiSymbol());
		F.KroneckerDelta.setEvaluator(new KroneckerDelta());
		F.LiouvilleLambda.setEvaluator(new LiouvilleLambda());
		F.LucasL.setEvaluator(new LucasL());
		F.MangoldtLambda.setEvaluator(new MangoldtLambda());
		F.MersennePrimeExponent.setEvaluator(new MersennePrimeExponent());
		F.MersennePrimeExponentQ.setEvaluator(new MersennePrimeExponentQ());
		F.MoebiusMu.setEvaluator(new MoebiusMu());
		F.Multinomial.setEvaluator(new Multinomial());
		F.MultiplicativeOrder.setEvaluator(new MultiplicativeOrder());
		F.NextPrime.setEvaluator(new NextPrime());
		F.PartitionsP.setEvaluator(new PartitionsP());
		F.PartitionsQ.setEvaluator(new PartitionsQ());
		F.PerfectNumber.setEvaluator(new PerfectNumber());
		F.PerfectNumberQ.setEvaluator(new PerfectNumberQ());
		F.Prime.setEvaluator(new Prime());
		F.PrimePi.setEvaluator(new PrimePi());
		F.PrimeOmega.setEvaluator(new PrimeOmega());
		F.PrimePowerQ.setEvaluator(new PrimePowerQ());
		F.PrimitiveRootList.setEvaluator(new PrimitiveRootList());
		F.SquareFreeQ.setEvaluator(new SquareFreeQ());
		F.StirlingS1.setEvaluator(new StirlingS1());
		F.StirlingS2.setEvaluator(new StirlingS2());
		F.Subfactorial.setEvaluator(new Subfactorial());
		F.Unitize.setEvaluator(new Unitize());
	}

	private final static NumberTheory CONST = new NumberTheory();

	public static IInteger factorial(final IInteger x) {

		int ni = x.toIntDefault(Integer.MIN_VALUE);
		if (ni > Integer.MIN_VALUE) {
			return factorial(ni);
		}

		IInteger result = F.C1;
		if (x.compareTo(F.C0) == -1) {
			result = F.CN1;

			for (IInteger i = F.CN2; i.compareTo(x) >= 0; i = i.add(F.CN1)) {
				result = result.multiply(i);
			}
		} else {
			for (IInteger i = F.C2; i.compareTo(x) <= 0; i = i.add(F.C1)) {
				result = result.multiply(i);
			}
		}
		return result;
	}

	public static IInteger factorial(int ni) {
		BigInteger result;
		if (ni < 0) {
			result = BigIntegerMath.factorial(-1 * ni);
			if ((ni & 0x0001) == 0x0001) {
				// odd integer number
				result = result.multiply(BigInteger.valueOf(-1L));
			}
		} else {
			if (ni <= 20) {
				return AbstractIntegerSym.valueOf(LongMath.factorial(ni));
			}
			result = BigIntegerMath.factorial(ni);
		}
		return AbstractIntegerSym.valueOf(result);
	}

	/**
	 * <p>
	 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.
	 * </p>
	 * See: <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code: Fibonacci
	 * sequence.</a>
	 * 
	 * @param iArg
	 * @return
	 */
	public static IInteger fibonacci(int iArg) {
		int temp = iArg;
		if (temp < 0) {
			temp *= (-1);
		}
		if (temp < FIBONACCI_45.length) {
			int result = FIBONACCI_45[temp];
			if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) {
				return F.ZZ(-result);
			}
			return F.ZZ(result);
		}

		BigInteger a = BigInteger.ONE;
		BigInteger b = BigInteger.ZERO;
		BigInteger c = BigInteger.ONE;
		BigInteger d = BigInteger.ZERO;
		BigInteger result = BigInteger.ZERO;
		while (temp != 0) {
			if ((temp & 0x00000001) == 0x00000001) { // odd?
				d = result.multiply(c);
				result = a.multiply(c).add(result.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).shiftLeft(1).add(d);
			b = b.multiply(b).add(d);
			temp >>= 1;
		}

		if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) { // even
			return F.ZZ(result.negate());
		}
		return F.ZZ(result);
	}

	public static NumberTheory initialize() {
		return CONST;
	}

	public static BigInteger divisorSigma(int exponent, int n) {
		IAST list = F.ZZ(n).divisors();
		if (list.isList()) {
			if (exponent == 1) {
				IInteger sum = F.C0;
				for (int i = 1; i < list.size(); i++) {
					sum = sum.add(((IInteger) list.get(i)));
				}
				return sum.toBigNumerator();
			}

			long kl = exponent;

			IInteger sum = F.C0;
			for (int i = 1; i < list.size(); i++) {
				sum = sum.add(((IInteger) list.get(i)).pow(kl));
			}
			return sum.toBigNumerator();
		}
		return null;
	}

	/**
	 * 
	 * @param indices
	 *            the non-negative coefficients
	 * @param n
	 *            the sum of the non-negative coefficients
	 * @return
	 */
	public static IInteger multinomial(final int[] indices, final int n) {
		IInteger bn = AbstractIntegerSym.valueOf(n);
		IInteger result = factorial(bn);
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] != 0) {
				result = result.div(factorial(AbstractIntegerSym.valueOf(indices[i])));
			}
		}
		return result;
	}

	/**
	 * The first 49 perfect numbers.
	 * 
	 * See <a href= "https://en.wikipedia.org/wiki/List_of_perfect_numbers">List_of_perfect_numbers</a>
	 */
	private final static long[] PN_8 = { 6, 28, 496, 8128, 33550336L, 8589869056L, 137438691328L,
			2305843008139952128L };

	/**
	 * The first 45 mersenne prime exponents.
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Mersenne_prime">Mersenne prime</a>
	 */
	private final static int[] MPE_45 = { 2, 3, 5, 7, 13, 17, 19, 31, 61, 89, 107, 127, 521, 607, 1279, 2203, 2281,
			3217, 4253, 4423, 9689, 9941, 11213, 19937, 21701, 23209, 44497, 86243, 110503, 132049, 216091, 756839,
			859433, 1257787, 1398269, 2976221, 3021377, 6972593, 13466917, 20996011, 24036583, 25964951, 30402457,
			32582657, 37156667

	};

	private NumberTheory() {

	}
}
