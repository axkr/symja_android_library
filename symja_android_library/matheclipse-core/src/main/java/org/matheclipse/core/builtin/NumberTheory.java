package org.matheclipse.core.builtin;

import static java.lang.Math.addExact;
import static java.lang.Math.floorMod;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.subtractExact;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;

import java.math.BigInteger;
import java.math.RoundingMode;
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
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
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
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.FEConfig;

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

	private static final long[] BELLB_25 = { 1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570, 4213597,
			27644437, 190899322L, 1382958545L, 10480142147L, 82864869804L, 682076806159L, 5832742205057L,
			51724158235372L, 474869816156751L, 4506715738447323L, 44152005855084346L, 445958869294805289L,
			4638590332229999353L };

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
		 *            an int number >= 0
		 * @return
		 */
		private static IInteger bellNumber(int index) {
			if (index < BELLB_25.length) {
				return AbstractIntegerSym.valueOf(BELLB_25[index]);
			}

			// Sum[StirlingS2[n, k], {k, 0, n}]
			IInteger sum = F.C1;
			for (int ki = 0; ki < index; ki++) {
				sum = sum.add(stirlingS2(F.ZZ(index), F.ZZ(ki), ki));
			}
			return sum;
		}

		/**
		 * Generates the Bell polynomial of the given index <code>n</code>, where B(1) is 1. This is recursive.
		 * 
		 * @param n
		 * @param z
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
			try {
				IExpr arg1 = ast.arg1();
				int n = arg1.toIntDefault(Integer.MIN_VALUE);
				if (n < 0) {
					if (arg1.isNumber()) {
						return IOFunctions.printMessage(F.BellB, "intnm", F.List(ast, F.C1), engine);
					}
				}
				if (ast.isAST2()) {
					IExpr z = ast.arg2();
					if (n == 0) {
						return F.C1;
					}
					if (n == 1) {
						return z;
					}
					if (z.isOne()) {
						return F.BellB(arg1);
					}
					if (n > 1) {
						if (z.isZero()) {
							return F.C0;
						}
						if (!z.isOne()) {
							// bell polynomials: Sum(StirlingS2(n, k)* z^k, {k, 0, n})
							return bellBPolynomial(n, z);
						}
					}
				} else {
					// bell numbers start here
					if (n == 0) {
						return F.C1;
					}
					if (n > 0) {
						IInteger bellB = bellNumber(n);
						return bellB;
					}
				}
			} catch (MathRuntimeException mre) {
				return engine.printMessage(ast.topHead(), mre);
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
				return engine.printMessage(ast.topHead(), rex);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				try {
					int bn = ast.arg1().toIntDefault();
					if (bn >= 0) {
						return bernoulliNumber(bn);
					}
					IExpr temp = engine.evaluate(F.Subtract(ast.arg1(), F.C3));
					if (temp.isIntegerResult() && temp.isPositiveResult() && temp.isEvenResult()) {
						// http://fungrim.org/entry/a98234/
						return F.C0;
					}

				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
				return F.NIL;
			}
			if (ast.isAST2()) {
				try {
					IExpr n = ast.arg1();
					IExpr x = ast.arg2();
					int xInt = x.toIntDefault();
					if (xInt != Integer.MIN_VALUE) {
						if (xInt == 0) {
							// http://fungrim.org/entry/a1d2d7/
							return F.BernoulliB(ast.arg1());
						}
						if (xInt == 1 && n.isIntegerResult()) {
							// http://fungrim.org/entry/829185/
							return F.Times(F.Power(F.CN1, n), F.BernoulliB(n));
						}

						return F.NIL;
					}
					if (n.isInteger() && n.isNonNegativeResult()) {
						if (x.isNumEqualRational(F.C1D2)) {
							// http://fungrim.org/entry/03ee0b/
							return F.Times(F.Subtract(F.Power(F.C2, F.Subtract(F.C1, n)), F.C1), F.BernoulliB(n));
						}
						int bn = n.toIntDefault();
						if (bn >= 0) {
							// http://fungrim.org/entry/555e10/
							return F.sum(k -> F.Times(F.Binomial(n, k), F.BernoulliB(F.Subtract(n, k)), F.Power(x, k)),
									0, bn);
						}
					}
				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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

		@Override
		public IExpr e2IntArg(final IInteger n, final IInteger k) {
			return binomial(n, k);
		}

		@Override
		public IExpr e2ObjArg(IAST ast, final IExpr n, final IExpr k) {
			if (n.isInteger() && k.isInteger()) {
				// use e2IntArg() method
				return F.NIL;
			}
			int ni = n.toIntDefault(Integer.MIN_VALUE);
			if (ni != Integer.MIN_VALUE) {
				int ki = k.toIntDefault(Integer.MIN_VALUE);
				if (ki != Integer.MIN_VALUE) {
					return binomial(F.ZZ(ni), F.ZZ(ki));
				}
			}
			if (n.isZero() && k.isZero()) {
				return F.C1;
			}
			if (k.isOne()) {
				return n;
			}
			if (k.isMinusOne()) {
				return F.C0;
			}
			if (k.isInteger()) {
				if (n.isInfinity()) {
					if (k.isNegative()) {
						return F.C0;
					}
					int ki = k.toIntDefault(Integer.MIN_VALUE);
					if (ki >= 1 && ki <= 5) {
						return F.Infinity;
					}
				} else if (n.isNegativeInfinity()) {
					if (k.isNegative()) {
						return F.C0;
					}
					int ki = k.toIntDefault(Integer.MIN_VALUE);
					if (ki >= 1 && ki <= 5) {
						if (ki % 2 == 0) {
							return F.CInfinity;
						}
						return F.CNInfinity;
					}
				}
				if (k.isOne()) {
					return n;
				}
				if (k.isZero()) {
					return F.C1;
				}
				if (n.isDirectedInfinity()) {
					return F.NIL;
				}
				IInteger ki = (IInteger) k;
				if (ki.compareInt(6) < 0 && ki.compareInt(1) > 0 && !n.isNumber()) {
					int kInt = ki.intValue();
					IASTAppendable result = F.TimesAlloc(kInt);
					IAST temp;
					IExpr nTemp = n;
					for (int i = 1; i <= kInt; i++) {
						temp = F.Divide(nTemp, F.ZZ(i));
						result.append(temp);
						nTemp = F.eval(F.Subtract(nTemp, F.C1));
					}
					return result;
				}
			}
			if (n.equals(k)) {
				return F.C1;
			}
			if (n.isNumber() && k.isNumber()) {
				IExpr n1 = ((INumber) n).add(F.C1);
				// (n,k) ==> Gamma(n+1)/(Gamma(k+1)*Gamma(n-k+1))
				return F.Times(F.Gamma(n1), F.Power(F.Gamma(F.Plus(F.C1, k)), -1),
						F.Power(F.Gamma(F.Plus(n1, F.Negate(k))), -1));
			}
			IExpr difference = F.eval(F.Subtract(n, F.C1));
			if (difference.equals(k)) {
				// n-1 == k
				return n;
			}
			difference = F.eval(F.Subtract(k, n));
			if (difference.isIntegerResult() && difference.isPositiveResult()) {
				// k-n is a positive integer number
				return F.C0;
			}

			if (!n.isNumber() && !k.isNumber()) {
				int diff = F.eval(F.Subtract(n, k)).toIntDefault(-1);
				if (diff > 0 && diff <= 5) {
					IASTAppendable result = F.TimesAlloc(diff + 1);
					result.append(F.Power(NumberTheory.factorial(diff), -1));
					for (int i = 1; i <= diff; i++) {
						IAST temp = F.Plus(F.ZZ(i), k);
						result.append(temp);
					}
					return result;
				}
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
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

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
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
			if (primes.length != remainders.length) {
				// The arguments to `1` must be two lists of integers of identical length, with the second list only
				// containing positive integers.
				String message = IOFunctions.getMessage("pilist", F.List(F.ChineseRemainder), EvalEngine.get());
				throw new ArgumentTypeException(message);
			}
			long modulus = primes[0];
			for (int i = 1; i < primes.length; ++i) {
				if (primes[i] <= 0) {
					// The arguments to `1` must be two lists of integers of identical length, with the second list only
					// containing positive integers.
					String message = IOFunctions.getMessage("pilist", F.List(F.ChineseRemainder), EvalEngine.get());
					throw new ArgumentTypeException(message);
				}
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
			if (primes.length != remainders.length) {
				// The arguments to `1` must be two lists of integers of identical length, with the second list only
				// containing positive integers.
				String message = IOFunctions.getMessage("pilist", F.List(F.ChineseRemainder), EvalEngine.get());
				throw new ArgumentTypeException(message);
			}
			BigInteger m = primes[0];
			for (int i = 1; i < primes.length; i++) {
				if (primes[i].signum() <= 0) {
					// The arguments to `1` must be two lists of integers of identical length, with the second list only
					// containing positive integers.
					String message = IOFunctions.getMessage("pilist", F.List(F.ChineseRemainder), EvalEngine.get());
					throw new ArgumentTypeException(message);
				}
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
			if (ast.arg1().isList() && ast.arg2().isList()) {
				try {
					long[] a = Validate.checkListOfLongs(ast, ast.arg1(), Long.MIN_VALUE, true, engine);
					long[] n = Validate.checkListOfLongs(ast, ast.arg2(), Long.MIN_VALUE, true, engine);
					if (a == null || n == null) {
						// try with BigIntegers
						BigInteger[] aBig = Validate.checkListOfBigIntegers(ast, ast.arg1(), false, engine);
						if (aBig == null) {
							return F.NIL;
						}
						BigInteger[] nBig = Validate.checkListOfBigIntegers(ast, ast.arg2(), false, engine);
						if (nBig == null) {
							return F.NIL;
						}
						if (aBig.length != nBig.length) {
							return F.NIL;
						}
						try {
							return F.ZZ(chineseRemainders(nBig, aBig));
						} catch (ArithmeticException ae) {
							if (FEConfig.SHOW_STACKTRACE) {
								ae.printStackTrace();
							}
						}
						return F.NIL;
					}
					if (a.length != n.length) {
						return F.NIL;
					}
					if (a.length == 0) {
						return F.NIL;
					}

					return F.ZZ(chineseRemainders(n, a));
				} catch (ValidateException ve) {
					return engine.printMessage(ast.topHead(), ve);
				} catch (ArithmeticException ae) {
					if (FEConfig.SHOW_STACKTRACE) {
						ae.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}
	}

	/**
	 * <pre>
	 * Convergents({n1, n2, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the list of convergents which represents the continued fraction list <code>{n1, n2, ...}</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Continued_fraction">Wikipedia - Continued fraction</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Convergents({2,3,4,5})
	 * {2,7/3,30/13,157/68}
	 * </pre>
	 */
	private final static class Convergents extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				if (list.exists(x -> x.isList())) {
					return F.NIL;
				}
				if (list.size() > 1) {
					int size = list.argSize();
					IASTAppendable resultList = F.ListAlloc(list.size());
					IASTMutable plus = F.binary(F.Plus, F.C0, list.arg1());
					IASTMutable result = plus;
					for (int i = 2; i <= size; i++) {
						IExpr temp;
						if (result.isAST()) {
							temp = engine.evaluate(F.Together(((IAST) result).copy()));
						} else {
							temp = engine.evaluate(result);
						}
						resultList.append(temp);
						IASTMutable plusAST = F.binary(F.Plus, F.C0, list.get(i));
						plus.set(1, F.Power(plusAST, F.CN1));
						plus = plusAST;
					}
					resultList.append(engine.evaluate(F.Together(result)));
					return resultList;
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDREST);
		}

	}

	/**
	 * <pre>
	 * ContinuedFraction(number)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the continued fraction representation of <code>number</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Continued_fraction">Wikipedia - Continued fraction</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FromContinuedFraction({2,3,4,5})
	 * 157/68
	 * 
	 * &gt;&gt; ContinuedFraction(157/68)
	 * {2,3,4,5} 
	 * 
	 * &gt;&gt; ContinuedFraction(45/16)
	 * {2,1,4,3}
	 * </pre>
	 * <p>
	 * For square roots of non-negative integer arguments <code>ContinuedFraction</code> determines the periodic part:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ContinuedFraction(Sqrt(13))
	 * {3,{1,1,1,1,6}}
	 * 
	 * &gt;&gt; ContinuedFraction(Sqrt(919))
	 * {30,3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}}
	 * </pre>
	 */
	private final static class ContinuedFraction extends AbstractEvaluator {

		/**
		 * Return the continued fraction of <code>Sqrt( d )</code>.
		 * 
		 * @param d
		 *            a positive integer number
		 * @return
		 */
		private IExpr sqrtContinuedFraction(IInteger d) {
			IInteger p = F.C0;
			IInteger q = F.C1;
			IInteger a = F.ZZ(BigIntegerMath.sqrt(d.toBigNumerator(), RoundingMode.FLOOR));
			IInteger last = a;
			IASTAppendable result = F.ListAlloc(10);

			do {
				p = last.multiply(q).subtract(p);
				q = d.subtract(p.pow(2L)).quotient(q);
				last = p.add(a).quotient(q);
				result.append(last);
			} while (!q.isOne());

			return F.List(a, result);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 2 && ast.size() <= 3) {
				IExpr arg1 = ast.arg1();

				int maxIterations = Integer.MAX_VALUE;
				if (ast.isAST2()) {
					if (ast.arg2().isInteger()) {
						maxIterations = ast.arg2().toIntDefault(Integer.MIN_VALUE);
						if (maxIterations < 0) {
							// Positive integer (less equal 2147483647) expected at position `2` in `1`.
							return IOFunctions.printMessage(F.ContinuedFraction, "intpm", F.List(ast, F.C2), engine);
						}
					} else {
						return F.NIL;
					}
				}

				if (ast.isAST1() && arg1.isSqrt() && arg1.base().isInteger() && arg1.base().isPositive()) {
					// Sqrt( d ) with d positive integer number
					return sqrtContinuedFraction((IInteger) arg1.base());
				}
				if (arg1 instanceof INum) {
					// arg1 = F.fraction(((INum) arg1).getRealPart());
					return realToContinuedFraction(((INum) arg1), maxIterations, engine);
				} else if (arg1.isAST() || arg1.isSymbol() && arg1.isNumericFunction()) {
					IExpr num = engine.evalN(arg1);
					if (num instanceof INum) {
						return realToContinuedFraction(((INum) num), maxIterations, engine);
					}
				}

				if (arg1.isRational()) {
					IRational rat = (IRational) arg1;

					IASTAppendable continuedFractionList;
					if (rat.denominator().isOne()) {
						continuedFractionList = F.ListAlloc(1);
						continuedFractionList.append(rat.numerator());
					} else if (rat.numerator().isOne()) {
						continuedFractionList = F.ListAlloc(2);
						continuedFractionList.append(F.C0);
						continuedFractionList.append(rat.denominator());
					} else {
						IFraction temp = F.fraction(rat.numerator(), rat.denominator());
						IInteger quotient;
						IInteger remainder;
						continuedFractionList = F.ListAlloc(10);
						while (temp.denominator().compareInt(1) > 0 && (0 < maxIterations--)) {
							quotient = temp.numerator().div(temp.denominator());
							remainder = temp.numerator().mod(temp.denominator());
							continuedFractionList.append(quotient);
							temp = F.fraction(temp.denominator(), remainder);
							if (temp.denominator().isOne()) {
								continuedFractionList.append(temp.numerator());
							}
						}
					}
					return continuedFractionList;

				}
			}
			return F.NIL;
		}

		private static IAST realToContinuedFraction(INum value, int iterationLimit, EvalEngine engine) {
			final double doubleValue = value.getRealPart();
			if (value.isNumIntValue()) {
				return F.List(F.ZZ((int) Math.rint(doubleValue)));
			}
			// int ip = (int) doubleValue;
			IASTAppendable continuedFractionList = F
					.ListAlloc(iterationLimit > 0 && iterationLimit < 1000 ? iterationLimit + 10 : 10);
			int aNow = (int) doubleValue;
			double tNow = doubleValue - aNow;
			double tNext;
			int aNext;
			continuedFractionList.append(F.ZZ(aNow));
			for (int i = 0; i < iterationLimit - 1; i++) {
				if (i >= 99) {
					return engine.printMessage(
							"ContinuedFraction: calculations of double number values require a iteration limit less equal 100.");
				}
				double rec = 1.0 / tNow;
				aNext = (int) rec;
				if (aNext == Integer.MAX_VALUE) {
					break;
				}
				tNext = rec - aNext;

				continuedFractionList.append(F.ZZ(aNext));
				tNow = tNext;
			}
			return continuedFractionList;

		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
			int size = ast.size();
			if (size >= 3) {
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
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	private static class CubeRoot extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr n = ast.arg1();
			if (n.isNumericFunction()) {
				if (!n.isComplex() && !n.isComplexNumeric()) {
					if (n.isPositiveResult()) {
						return F.Power(n, F.C1D3);
					}
					return F.Times(F.CN1, F.Power(F.Negate(n), F.C1D3));
				}
			}
			return F.Power(n, F.C1D3);
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
					ISignedNumber temp = expr.evalReal();
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
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE);
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
	private static class DiscreteDelta extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size > 1) {
				IExpr arg1 = engine.evaluate(ast.arg1());

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

				IExpr result = removeEval(ast, engine);
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

		private static IExpr removeEval(final IAST ast, EvalEngine engine) {
			IASTAppendable result = F.NIL;
			int size = ast.size();
			int j = 1;
			for (int i = 1; i < size; i++) {
				IExpr expr = engine.evaluate(ast.get(i));
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
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.NUMERICFUNCTION);
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
				if (result.isReal()) {
					return isSignedNumberDivisible((ISignedNumber) result);
				}
				return F.False;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
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

	private static class DivisorSum extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IExpr head = ast.arg2();
			IExpr condition = F.NIL;
			if (arg1.isInteger()) {
				IInteger n = (IInteger) arg1;
				if (n.isPositive()) {
					IAST list = n.divisors();
					if (list.isList()) {
						if (ast.isAST3()) {
							condition = ast.arg3();
						}
						// Sum( head(divisor), list-of-divisors )
						IASTAppendable sum = F.PlusAlloc(list.size());
						for (int i = 1; i < list.size(); i++) {
							IExpr divisor = list.get(i);
							// apply condition on divisor
							if (condition.isPresent() && !engine.evalTrue(F.unaryAST1(condition, divisor))) {
								continue;
							}
							sum.append(F.unaryAST1(head, divisor));
						}
						return sum;
					}
				}

			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
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

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).eulerPhi();
				} catch (ArithmeticException e) {
					// integer to large?
				}
			} else {
				if (arg1.isPower() && arg1.exponent().isIntegerResult()
						&& AbstractAssumptions.assumePrime(arg1.base()).isTrue()) {
					IExpr p = arg1.base();
					IExpr n = arg1.exponent();
					// Power(p, n) => p^n - p^(n - 1)
					return F.Subtract(arg1, F.Power(p, F.Subtract(n, F.C1)));
				}
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

	// public static void main(String[] args) {
	// BigInteger[] gcdArgs = new BigInteger[] { BigInteger.valueOf(550), BigInteger.valueOf(420),
	// BigInteger.valueOf(3515) };
	// BigInteger[] bezoutCoefficients = new BigInteger[3];
	// BigInteger gcd = ExtendedGCD.extendedGCD(gcdArgs, bezoutCoefficients);
	// System.out.println("GCD: " + gcd.toString());
	// System.out.println("Bezout Coefficients: ");
	// for (int i = 0; i < bezoutCoefficients.length; i++) {
	// System.out.print(" " + bezoutCoefficients[i].toString());
	// }
	// }
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
			IExpr arg;
			BigInteger[] gcdArgs = new BigInteger[ast.argSize()];
			for (int i = 1; i < ast.size(); i++) {
				arg = ast.get(i);
				if (!arg.isInteger()) {
					return F.NIL;
				}
				if (!((IInteger) arg).isPositive()) {
					return F.NIL;
				}
				gcdArgs[i - 1] = ((IInteger) ast.get(i)).toBigNumerator();
			}
			// all arguments are positive integers now

			try {
				BigInteger[] bezoutCoefficients = new BigInteger[ast.argSize()];
				BigInteger gcd = extendedGCD(gcdArgs, bezoutCoefficients);
				// convert the Bezout numbers to sublists
				IASTAppendable subList = F.ListAlloc(bezoutCoefficients.length);
				subList.appendArgs(0, bezoutCoefficients.length, i -> F.ZZ(bezoutCoefficients[i]));
				// for (int i = 0; i < subBezouts.length; i++) {
				// subList.append(F.integer(subBezouts[i]));
				// }
				// create the output list
				return F.List(F.ZZ(gcd), subList);
			} catch (ArithmeticException ae) {
				if (FEConfig.SHOW_STACKTRACE) {
					ae.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_INFINITY;
		}

		/**
		 * Calculate the extended GCD
		 * 
		 * @param gcdArgs
		 *            an array of positive BigInteger numbers
		 * @param bezoutsCoefficients
		 *            returns the Bezout Coefficients
		 * @return
		 */
		public static BigInteger extendedGCD(final BigInteger[] gcdArgs, BigInteger[] bezoutsCoefficients) {
			BigInteger factor;
			BigInteger gcd = gcdArgs[0];
			Object[] stepResult = extendedGCD(gcdArgs[1], gcd);

			gcd = (BigInteger) stepResult[0];
			bezoutsCoefficients[0] = ((BigInteger[]) stepResult[1])[0];
			bezoutsCoefficients[1] = ((BigInteger[]) stepResult[1])[1];

			for (int i = 2; i < gcdArgs.length; i++) {
				stepResult = extendedGCD(gcdArgs[i], gcd);
				gcd = (BigInteger) stepResult[0];
				factor = ((BigInteger[]) stepResult[1])[0];
				for (int j = 0; j < i; j++) {
					bezoutsCoefficients[j] = bezoutsCoefficients[j].multiply(factor);
				}
				bezoutsCoefficients[i] = ((BigInteger[]) stepResult[1])[1];
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger()) {
				if (arg1.isNegative()) {
					return F.CComplexInfinity;
				}
				return factorial((IInteger) arg1);
			}
			if (arg1.isFraction()) {
				if (arg1.equals(F.C1D2)) {
					return F.Times(F.C1D2, F.Sqrt(F.Pi));
				}
				if (arg1.equals(F.CN1D2)) {
					return F.Sqrt(F.Pi);
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

	private static class FactorialPower extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				IExpr x = ast.arg1();
				IExpr n = ast.arg2();
				// x*(x-1)* (x-(n-1))
				
				return F.NIL;
			}
			if (ast.isAST2()) {
				IExpr x = ast.arg1();
				IExpr n = ast.arg2();
				IExpr h = ast.arg3();
				// x*(x-h)* (x-(n-1)*h)
				
				return F.NIL;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
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
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption(F.GaussianIntegers);
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
	private static class Fibonacci extends AbstractFunctionEvaluator {

		/**
		 * <p>
		 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F
		 * </p>
		 * See: <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code: Fibonacci
		 * sequence.</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				int n = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (n > Integer.MIN_VALUE) {
					if (ast.isAST2()) {
						return fibonacciPolynomialIterative(n, ast.arg2(), engine);
					}
					return fibonacci(n);
				}
			} else if (arg1.isInexactNumber()) {
				INumber n = ((INumber) arg1).evaluatePrecision(engine);
				if (ast.isAST2() && ast.arg2().isInexactNumber()) {
					INumber x = ((INumber) ast.arg2()).evaluatePrecision(engine);
					return
					// [$ ((x + Sqrt(4 + x^2))^n/2^n - (2^n*Cos(n*Pi))/(x + Sqrt(4 + x^2))^n)/Sqrt(4 + x^2) $]
					F.Times(F.Power(F.Plus(F.C4, F.Sqr(x)), F.CN1D2),
							F.Plus(F.Times(F.Power(F.Power(F.C2, n), F.CN1),
									F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n)),
									F.Times(F.CN1, F.Power(F.C2, n),
											F.Power(F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n), F.CN1),
											F.Cos(F.Times(n, F.Pi))))); // $$;
				}
				return
				// [$ ( GoldenRatio^n - Cos(Pi*n) * GoldenRatio^(-n) ) / Sqrt(5) $]
				F.Times(F.C1DSqrt5, F.Plus(F.Power(F.GoldenRatio, n),
						F.Times(F.CN1, F.Power(F.GoldenRatio, F.Negate(n)), F.Cos(F.Times(F.Pi, n))))); // $$;
			}
			return F.NIL;
		}

		/**
		 * Create Fibonacci polynomial with iteration.
		 * 
		 * @param n
		 *            an integer <code>n >= 0</code>
		 * @param x
		 *            the variable expression of the polynomial
		 * @return
		 */
		public IExpr fibonacciPolynomialIterative(int n, IExpr x, final EvalEngine engine) {
			int iArg = n;
			if (n < 0) {
				n *= (-1);
			}

			IExpr previousFibonacci = F.C0;
			IExpr fibonacci = F.C1;
			if (n == 0) {
				return previousFibonacci;
			}
			if (n == 1) {
				return fibonacci;
			}

			for (int i = 1; i < n; i++) {
				IExpr temp = fibonacci;
				if (fibonacci.isPlus()) {
					fibonacci = ((IAST) fibonacci).mapThread(F.Times(x, null), 2);
				} else {
					fibonacci = F.Times(x, fibonacci);
				}
				fibonacci = F.Expand.of(engine, F.Plus(fibonacci, previousFibonacci));
				previousFibonacci = temp;
			}
			if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) {
				return F.Negate(fibonacci);
			}

			return fibonacci;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * FrobeniusNumber({a1, ... ,aN})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Frobenius number of the nonnegative integers <code>{a1, ... ,aN}</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * The Frobenius problem, also known as the postage-stamp problem or the money-changing problem, is an integer
	 * programming problem that seeks nonnegative integer solutions to <code>x1*a1 + ... + xN*aN = M</code> where
	 * <code>ai</code> and <code>M</code> are positive integers. In particular, the Frobenius number
	 * <code>FrobeniusNumber({a1, ... ,aN})</code>, is the largest <code>M</code> so that this equation fails to have a
	 * solution.
	 * </p>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Coin_problem">Wikipedia - Coin problem</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FrobeniusNumber({1000, 1476, 3764, 4864, 4871, 7773})
	 * 47350
	 * </pre>
	 */
	private static class FrobeniusNumber extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			BigInteger[] array = Validate.checkListOfBigIntegers(ast, ast.arg1(), true, engine);
			if (array != null && array.length > 0) {
				BigInteger result = org.matheclipse.core.frobenius.FrobeniusNumber.frobeniusNumber(array);
				return F.ZZ(result);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * FromContinuedFraction({n1, n2, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the number which represents the continued fraction list <code>{n1, n2, ...}</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FromContinuedFraction({2,3,4,5})
	 * 157/68
	 * 
	 * &gt;&gt; ContinuedFraction(157/68)
	 * {2,3,4,5}
	 * </pre>
	 */
	private final static class FromContinuedFraction extends AbstractEvaluator {

		/**
		 * Convert a list of numbers to a fraction. See
		 * <a href="http://en.wikipedia.org/wiki/Continued_fraction">Continued fraction</a>
		 * 
		 * @see org.matheclipse.core.reflection.system.ContinuedFraction
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				if (list.size() > 1) {
					int size = list.argSize();
					if (list.forAll(x -> x.isReal())) {
						IExpr result = list.get(size--);
						for (int i = size; i >= 1; i--) {
							result = list.get(i).plus(result.power(-1));
						}
						return result;
					}
					IExpr result = list.get(size--);
					for (int i = size; i >= 1; i--) {
						result = F.Plus(list.get(i), F.Power(result, F.CN1));
					}
					return result;
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
				IExpr arg1 = engine.evaluate(ast.arg1());
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
					IExpr expr = engine.evaluate(ast.get(i));
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

	private static class LinearRecurrence extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			IExpr arg3 = ast.arg3();
			if (arg1.isList() && arg2.isList()) {
				IAST list1 = (IAST) arg1;
				IAST list2 = (IAST) arg2;
				if (arg3.isReal() && arg3.isPositive()) {
					int n = arg3.toIntDefault(-1);
					return linearRecurrence(list1, list2, n, engine);
				}
				if (arg3.isList() && arg3.size() == 2 && arg3.first().isReal()) {
					int n = arg3.first().toIntDefault(-1);
					IAST result = linearRecurrence(list1, list2, n, engine);
					if (result.isPresent()) {
						return result.get(n);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_3;
		}

		private IAST linearRecurrence(IAST list1, IAST list2, int n, EvalEngine engine) {
			if (n < 0) {
				return F.NIL;
			}
			int size1 = list1.size();
			int size2 = list2.size();
			if (size2 >= size1) {
				int counter = 0;
				IASTAppendable result = F.ListAlloc(n);
				int start = size2 - size1 + 1;
				boolean isNumber = true;
				for (int i = start; i < list2.size(); i++) {
					IExpr x = list2.get(i);
					if (!x.isNumber()) {
						isNumber = false;
					}
					result.append(x);
					if (counter++ == n) {
						return result;
					}
				}
				if (isNumber) {
					for (int i = 1; i < list1.size(); i++) {
						if (!list1.get(i).isNumber()) {
							isNumber = false;
						}
					}
				}
				if (isNumber) {
					while (counter < n) {
						int size = result.size();
						INumber num = F.C0;
						int k = size - 1;
						for (int i = 1; i < size1; i++) {
							num = (INumber) num.plus(((INumber) list1.get(i)).times(result.get(k--)));
						}
						result.append(num);
						counter++;
					}
				} else {
					while (counter < n) {
						int size = result.size();
						IASTAppendable plusAST = F.PlusAlloc(size);
						int k = size - 1;
						for (int i = 1; i < size1; i++) {
							plusAST.append(F.Times(list1.get(i), result.get(k--)));
						}
						result.append(engine.evaluate(plusAST));
						counter++;
					}
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	private static class LiouvilleLambda extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
	private static class LucasL extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				int n = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (n > Integer.MIN_VALUE) {
					if (ast.isAST2()) {
						return lucasLPolynomialIterative(n, ast.arg2(), engine);
					}
					int iArg = n;
					if (n < 0) {
						n *= (-1);
					}
					// LucasL(n) = Fibonacci(n-1) + Fibonacci(n+1)
					IExpr lucalsL = fibonacci(n - 1).add(fibonacci(n + 1));
					if (iArg < 0 && ((iArg & 0x00000001) == 0x00000001)) {
						return F.Negate(lucalsL);
					}

					return lucalsL;
				}
			} else if (arg1.isInexactNumber()) {
				INumber n = ((INumber) arg1).evaluatePrecision(engine);
				if (ast.isAST2() && ast.arg2().isInexactNumber()) {
					INumber x = ((INumber) ast.arg2()).evaluatePrecision(engine);
					return
					// [$ (x/2 + Sqrt(1 + x^2/4))^n + Cos(n*Pi)/(x/2 + Sqrt(1 + x^2/4))^n $]
					F.Plus(F.Power(F.Plus(F.Times(F.C1D2, x), F.Sqrt(F.Plus(F.C1, F.Times(F.C1D4, F.Sqr(x))))), n),
							F.Times(F.Power(
									F.Power(F.Plus(F.Times(F.C1D2, x), F.Sqrt(F.Plus(F.C1, F.Times(F.C1D4, F.Sqr(x))))),
											n),
									F.CN1), F.Cos(F.Times(n, F.Pi)))); // $$;
				}
				return
				// [$ GoldenRatio^n + Cos(Pi*n) * GoldenRatio^(-n) $]
				F.Plus(F.Power(F.GoldenRatio, n),
						F.Times(F.Cos(F.Times(F.Pi, n)), F.Power(F.GoldenRatio, F.Negate(n)))); // $$;

			}
			return F.NIL;
		}

		/**
		 * Create LucasL polynomial with iteration. Performs much better than recursion.
		 * 
		 * @param n
		 *            an integer <code>n >= 0</code>
		 * @param x
		 *            the variable expression of the polynomial
		 * @return
		 */
		private static IExpr lucasLPolynomialIterative(int n, IExpr x, final EvalEngine engine) {
			int iArg = n;
			if (n < 0) {
				n *= (-1);
			}
			IExpr previousLucasL = F.C2;
			IExpr lucalsL = x;
			if (n == 0) {
				return previousLucasL;
			}
			if (n == 1) {
				if (iArg < 0) {
					return F.Negate(lucalsL);
				}
				return lucalsL;
			}

			for (int i = 1; i < n; i++) {
				IExpr temp = lucalsL;
				if (lucalsL.isPlus()) {
					lucalsL = ((IAST) lucalsL).mapThread(F.Times(x, null), 2);
				} else {
					lucalsL = F.Times(x, lucalsL);
				}
				lucalsL = F.Expand.of(engine, F.Plus(lucalsL, previousLucasL));
				previousLucasL = temp;
			}
			if (iArg < 0 && ((iArg & 0x00000001) == 0x00000001)) {
				return F.Negate(lucalsL);
			}
			return lucalsL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * MangoldtLambda(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the von Mangoldt function of <code>n</code>
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Von_Mangoldt_function">Wikipedia - Von Mangoldt function</a></li>
	 * </ul>
	 * 
	 * <pre>
	 * &gt;&gt; MangoldtLambda({1,2,3,4,5,6,7,8,9})
	 * {0,Log(2),Log(3),Log(2),Log(5),0,Log(7),Log(2),Log(3)}
	 * </pre>
	 */
	private static class MangoldtLambda extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				if (arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
					return F.C0;
				}
				IExpr expr = F.FactorInteger.of(engine, arg1);
				if (expr.isList()) {
					IAST list = (IAST) expr;
					if (list.size() == 2) {
						IInteger temp = (IInteger) list.arg1().first();
						return F.Log(temp);
					}
					return F.C0;
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
	 * <code>0 &lt;= n &lt;= 47</code> can be computed, otherwise the function returns unevaluated.
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger() && arg1.isPositive()) {
				int n = ((IInteger) arg1).toIntDefault();
				if (n > 0) {
					if (n > NumberTheory.MPE_47.length) {
						return F.NIL;
					}
					return F.ZZ(NumberTheory.MPE_47[n - 1]);
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
	 * returns <code>True</code> if <code>2^n - 1</code> is a prime number. Currently <code>0 &lt;= n &lt;= 47</code>
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
			IExpr arg1 = ast.arg1();
			if (!arg1.isInteger() || arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
				return F.False;
			}

			try {
				long n = ((IInteger) arg1).toLong();
				if (n <= MPE_47[MPE_47.length - 1]) {
					for (int i = 0; i < MPE_47.length; i++) {
						if (MPE_47[i] == n) {
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).moebiusMu();
				} catch (ArithmeticException e) {
					// integer to large?
				}
			} else {
				if (AbstractAssumptions.assumePrime(arg1).isTrue()) {
					return F.CN1;
				}
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

		/**
		 * 
		 * @param ast
		 * @return <code>F.NIL</code> if the ast arguments couldn't be converted to an IInteger
		 */
		private static IExpr multinomial(final IAST ast) {
			IInteger[] k = new IInteger[ast.argSize()];
			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				int value = temp.toIntDefault(Integer.MIN_VALUE);
				if (value != Integer.MIN_VALUE) {
					k[i - 1] = F.ZZ(value);
				} else {
					if (!temp.isInteger()) {
						return F.NIL;
					}
					k[i - 1] = (IInteger) temp;
				}
			}
			return NumberTheory.multinomial(k);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				// (n) ==> 1
				return F.C1;
			}
			if (ast.isAST2()) {
				return F.Binomial(F.Plus(ast.arg1(), ast.arg2()), ast.arg2());
			}
			int position = ast.indexOf(x -> (!x.isInteger()) || x.isNegative());
			if (position < 0) {
				return multinomial(ast);
			}
			int argSize = ast.size() - 1;
			if (position == argSize && !ast.get(argSize).isNumber()) {
				// recurrence: Multinomial(n1, n2, n3,..., ni, k) =>
				// Multinomial(n1+n2+n3+...+ni, k) * Multinomial(n1, n2, n3,..., ni)
				IAST reducedMultinomial = ast.removeFromEnd(argSize);
				IAST reducedPlus = reducedMultinomial.apply(F.Plus);
				return F.Times(F.Multinomial(reducedPlus, ast.get(argSize)), multinomial(reducedMultinomial));
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * MultiplicativeOrder(a, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the multiplicative order <code>a</code> modulo <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia: Multiplicative order</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * <p>
	 * The <a href="https://oeis.org/A023394">A023394 Prime factors of Fermat numbers</a> integer sequence
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Select(Prime(Range(500)), IntegerQ(Log(2, MultiplicativeOrder(2, # )))&amp;) 
	 * {3,5,17,257,641}
	 * </pre>
	 */
	private static class MultiplicativeOrder extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isInteger() && ast.arg2().isInteger()) {
				try {
					IInteger k = ast.getInt(1);
					IInteger n = ast.getInt(2);
					if (!n.isPositive()) {
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

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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
			if (ast.isAST1() && ast.arg1().isInteger()) {
				BigInteger primeBase = ((IInteger) ast.arg1()).toBigNumerator();
				if (primeBase.compareTo(BigInteger.ZERO) < 0) {
					// Non-negative integer expected.
					return IOFunctions.printMessage(F.NextPrime, "intnn", F.List(), engine);
				}
				return F.ZZ(primeBase.nextProbablePrime());
			} else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {
				BigInteger primeBase = ((IInteger) ast.arg1()).toBigNumerator();
				if (primeBase.compareTo(BigInteger.ZERO) < 0) {
					// Non-negative integer expected.
					return IOFunctions.printMessage(F.NextPrime, "intnn", F.List(), engine);
				}
				final int n = ast.arg2().toIntDefault(Integer.MIN_VALUE);
				if (n < 0) {
					// Positive integer (less equal 2147483647) expected at position `2` in `1`.
					return IOFunctions.printMessage(F.NextPrime, "intpm", F.List(ast, F.C2), engine);
				}

				BigInteger temp = primeBase;
				for (int i = 0; i < n; i++) {
					temp = temp.nextProbablePrime();
				}
				return F.ZZ(temp);

			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.C1;
			}
			if (arg1.isInteger()) {
				if (arg1.isPositive()) {
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
					} catch (RuntimeException rex) {
						// e.printStackTrace();
					} catch (ExecutionException e) {
						// e.printStackTrace();
					}
					return F.NIL;
				}
				// http://fungrim.org/entry/cd3013/
				return F.C0;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
			IExpr arg1 = ast.arg1();
			if (arg1.isZero()) {
				return F.C1;
			}
			if (arg1.isInteger()) {
				if (arg1.isPositive()) {
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
						if (n.isLT(F.ZZ(201))) {
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
					} catch (RuntimeException rex) {
						// e.printStackTrace();
					} catch (ExecutionException e) {
						// e.printStackTrace();
					}
					return F.NIL;
				}
				return F.C0;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger() && arg1.isPositive()) {
				int n = ((IInteger) arg1).toIntDefault();
				if (n >= 0) {
					if (n > NumberTheory.MPE_47.length) {
						return F.NIL;
					}
					if (n <= NumberTheory.PN_8.length) {
						return F.ZZ(NumberTheory.PN_8[n - 1]);
					}
					// 2^p
					BigInteger b2p = BigInteger.ONE.shiftLeft(NumberTheory.MPE_47[n - 1]);
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
			if (ast.arg1().isInteger()) {
				int nthPrime = ((IInteger) ast.arg1()).toIntDefault(Integer.MIN_VALUE);
				if (nthPrime < 0 || nthPrime > 103000000) {
					return F.NIL;
				}
				try {
					return F.ZZ(Primality.prime(nthPrime));
				} catch (RuntimeException ae) {
					if (FEConfig.SHOW_STACKTRACE) {
						ae.printStackTrace();
					}
					return F.NIL;
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
			IExpr arg1 = ast.arg1();
			if (arg1.isNegative() || arg1.isOne() || arg1.isZero()) {
				return F.C0;
			}

			IExpr x = F.NIL;
			if (arg1.isInteger()) {
				x = arg1;
			} else if (arg1.isReal() && arg1.isPositive()) {
				x = engine.evaluate(((ISignedNumber) arg1).floorFraction());
			} else {
				ISignedNumber sn = arg1.evalReal();
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
					return F.ZZ(result);
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				return F.bool(Primality.isPrimePower(((IInteger) arg1).toBigNumerator()));
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	private static class PrimitiveRoot extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// TODO
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				try {
					IInteger ii = (IInteger) arg1;
					if (ii.isEven() && !ii.equals(F.C2) && !ii.equals(F.C4)) {
						if (ii.quotient(F.C2).isEven()) {
							return F.NIL;
						}
					}
				} catch (LimitException le) {
					throw le;
				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger()) {
				try {
					IInteger[] roots = ((IInteger) arg1).primitiveRootList();
					if (roots != null) {
						int size = roots.length;
						IASTAppendable list = F.ListAlloc(size);
						return list.appendArgs(0, size, i -> roots[i]);
					}
				} catch (LimitException le) {
					throw le;
				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
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
	 * Rationalize(expression)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert numerical real or imaginary parts in (sub-)expressions into rational numbers.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Rationalize(6.75)
	 * 27/4
	 * 
	 * &gt;&gt; Rationalize(0.25+I*0.33333)
	 * 1/4+I*33333/100000
	 * </pre>
	 */
	private final static class Rationalize extends AbstractFunctionEvaluator {

		private static class RationalizeVisitor extends VisitorExpr {
			double epsilon;

			public RationalizeVisitor(double epsilon) {
				super();
				this.epsilon = epsilon;
			}

			@Override
			public IExpr visit(IASTMutable ast) {
				if (ast.isNumericFunction()) {
					ISignedNumber signedNumber = ast.evalReal();
					if (signedNumber != null) {
						return getRational(signedNumber);
					}
				}
				return super.visitAST(ast);
			}

			@Override
			public IExpr visit(IComplex element) {
				return element;
			}

			@Override
			public IExpr visit(IComplexNum element) {
				return F.complex(element.getRealPart(), element.getImaginaryPart(), epsilon);
			}

			@Override
			public IExpr visit(INum element) {
				return F.fraction(element.getRealPart(), epsilon);
			}

			/**
			 * 
			 * @return <code>F.NIL</code>, if no evaluation is possible
			 */
			@Override
			public IExpr visit(ISymbol element) {
				if (element.isNumericFunction()) {
					ISignedNumber signedNumber = element.evalReal();
					if (signedNumber != null) {
						return getRational(signedNumber);
					}
				}
				return F.NIL;
			}

			private IRational getRational(ISignedNumber signedNumber) {
				if (signedNumber.isRational()) {
					return (IRational) signedNumber;
				}
				return F.fraction(signedNumber.doubleValue(), epsilon);
			}
		}

		static class RationalizeNumericsVisitor extends VisitorExpr {
			double epsilon;

			public RationalizeNumericsVisitor(double epsilon) {
				super();
				this.epsilon = epsilon;
			}

			@Override
			public IExpr visit(IASTMutable ast) {
				return super.visitAST(ast);
			}

			// @Override
			// public IExpr visit(IComplex element) {
			// return element;
			// }

			@Override
			public IExpr visit(IComplexNum element) {
				return F.complex(element.getRealPart(), element.getImaginaryPart(), epsilon);
			}

			@Override
			public IExpr visit(INum element) {
				return F.fraction(element.getRealPart(), epsilon);
			}

			// private IRational getRational(ISignedNumber signedNumber) {
			// if (signedNumber.isRational()) {
			// return (IRational) signedNumber;
			// }
			// return F.fraction(signedNumber.doubleValue(), epsilon);
			// }
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			double epsilon = Config.DOUBLE_EPSILON;
			try {
				if (ast.isAST2()) {
					ISignedNumber epsilonExpr = ast.arg2().evalReal();
					if (epsilonExpr == null) {
						return F.NIL;
					}
					epsilon = epsilonExpr.doubleValue();
					if (arg1.isNumericFunction()) {
						// works more similar to MMA if we do this step:
						arg1 = engine.evalN(arg1);
					}
				}
				// try to convert into a fractional number
				return rationalize(arg1, epsilon).orElse(arg1);
			} catch (Exception e) {
				if (FEConfig.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
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
				return engine
						.printMessage(ast.topHead() + ": only implemented for univariate polynomials at position 1");
			}
			try {
				IExpr expr = F.evalExpandAll(ast.arg1(), engine);
				List<IExpr> varList = eVar.getVarList().copyTo();

				if (ast.isAST2()) {
					return F.bool(isSquarefreeWithOption(ast, expr, varList, engine));
				}
				return F.bool(isSquarefree(expr, varList));
			} catch (JASConversionException e) {
				if (FEConfig.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (RuntimeException e) {
				// JAS may throw RuntimeExceptions
				if (FEConfig.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		public static boolean isSquarefree(IExpr expr, List<IExpr> varList) throws JASConversionException {
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);

			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
			return factorAbstract.isSquarefree(poly);
		}

		public static boolean isSquarefreeWithOption(final IAST lst, IExpr expr, List<IExpr> varList,
				final EvalEngine engine) throws JASConversionException {
			final OptionArgs options = new OptionArgs(lst.topHead(), lst, 2, engine);
			IExpr option = options.getOption(F.Modulus);
			if (option.isReal()) {

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
					k = F.ZZ(i);
					if ((i & 1) == 1) { // isOdd(i) ?
						factorPlusMinus1 = F.CN1;
					} else {
						factorPlusMinus1 = F.C1;
					}
					temp.append(Times(factorPlusMinus1, F.Binomial(Plus(k, nSubtract1), Plus(k, nSubtractm)),
							F.Binomial(nTimes2Subtractm, F.Subtract(nSubtractm, k)),
							F.StirlingS2(Plus(k, nSubtractm), k)));

				}
				return temp;
			}
			throw new ArithmeticException("StirlingS1(n, m): arguments out of range.");
		}

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr nArg1 = ast.arg1();
			IExpr mArg2 = ast.arg2();
			if (nArg1.isNegative() || mArg2.isNegative()) {
				return F.NIL;
			}
			if (nArg1.isInteger() && mArg2.isInteger()) {
				try {
					return stirlingS1((IInteger) nArg1, (IInteger) mArg2);
				} catch (RuntimeException rex) {
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
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
					if (ki.equals(nArg1)) {
						return C1;
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
			} catch (MathRuntimeException mre) {
				return engine.printMessage(ast.topHead(), mre);
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
				return engine.printMessage(ast.topHead(), rex);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
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
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isInteger() && arg1.isPositive()) {
				try {
					long n = ((IInteger) arg1).toLong();
					return subFactorial(n);
				} catch (ArithmeticException ae) {
					return engine.printMessage("Subfactorial: argument n is to big.");
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

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.BellB.setEvaluator(new BellB());
			F.BernoulliB.setEvaluator(new BernoulliB());
			F.Binomial.setEvaluator(new Binomial());
			F.CarmichaelLambda.setEvaluator(new CarmichaelLambda());
			F.CatalanNumber.setEvaluator(new CatalanNumber());
			F.ChineseRemainder.setEvaluator(new ChineseRemainder());
			F.Convergents.setEvaluator(new Convergents());
			F.ContinuedFraction.setEvaluator(new ContinuedFraction());
			F.CoprimeQ.setEvaluator(new CoprimeQ());
			F.CubeRoot.setEvaluator(new CubeRoot());
			F.DiracDelta.setEvaluator(new DiracDelta());
			F.DiscreteDelta.setEvaluator(new DiscreteDelta());
			F.Divisible.setEvaluator(new Divisible());
			F.Divisors.setEvaluator(new Divisors());
			F.DivisorSum.setEvaluator(new DivisorSum());
			F.DivisorSigma.setEvaluator(new DivisorSigma());
			F.EulerE.setEvaluator(new EulerE());
			F.EulerPhi.setEvaluator(new EulerPhi());
			F.ExtendedGCD.setEvaluator(new ExtendedGCD());
			F.Factorial.setEvaluator(new Factorial());
			F.FactorialPower.setEvaluator(new FactorialPower());
			F.Factorial2.setEvaluator(new Factorial2());
			F.FactorInteger.setEvaluator(new FactorInteger());
			F.Fibonacci.setEvaluator(new Fibonacci());
			F.FrobeniusNumber.setEvaluator(new FrobeniusNumber());
			F.FromContinuedFraction.setEvaluator(new FromContinuedFraction());
			F.JacobiSymbol.setEvaluator(new JacobiSymbol());
			F.KroneckerDelta.setEvaluator(new KroneckerDelta());
			F.LinearRecurrence.setEvaluator(new LinearRecurrence());
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
			F.PrimitiveRoot.setEvaluator(new PrimitiveRoot());
			F.PrimitiveRootList.setEvaluator(new PrimitiveRootList());
			F.Rationalize.setEvaluator(new Rationalize());
			F.SquareFreeQ.setEvaluator(new SquareFreeQ());
			F.StirlingS1.setEvaluator(new StirlingS1());
			F.StirlingS2.setEvaluator(new StirlingS2());
			F.Subfactorial.setEvaluator(new Subfactorial());
			F.Unitize.setEvaluator(new Unitize());
		}
	}

	public static IInteger factorial(final IInteger x) {
		return x.factorial();
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
	 * Returns the rising factorial <code>n*(n+1)*...*(n+k-1)</code>
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	public static IInteger risingFactorial(int n, int k) {
		if (k == 0) {
			return F.C1;
		}
		IInteger result = AbstractIntegerSym.valueOf(n);
		for (int i = n + 1; i < n + k; i++) {
			result = result.multiply(i);
		}
		return result;
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

	public static void initialize() {
		Initializer.init();
	}

	/**
	 * <p>
	 * Calculate integer binomial number.
	 * </p>
	 * See definitions by <a href="https://arxiv.org/abs/1105.3689">Kronenburg 2011</a>
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	public static IInteger binomial(final IInteger n, final IInteger k) {
		if (n.isZero() && k.isZero()) {
			return F.C1;
		}
		if (!n.isNegative() && !k.isNegative()) {
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
		} else if (n.isNegative()) {
			// see definitions at https://arxiv.org/abs/1105.3689
			if (!k.isNegative()) {
				// (-1)^k * Binomial(-n+k-1, k)
				IInteger factor = k.isOdd() ? F.CN1 : F.C1;
				return binomial(n.negate().add(k).add(F.CN1), k).multiply(factor);
			}
			if (n.compareTo(k) >= 0) {
				// (-1)^(n-k) * Binomial(-k-1, n-k)
				IInteger factor = n.subtract(k).isOdd() ? F.CN1 : F.C1;
				return binomial(k.add(F.C1).negate(), n.subtract(k)).multiply(factor);
			}
		}
		return F.C0;
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param n
	 * @return throws ArithmeticException if n is a negative int number
	 */
	public static IRational bernoulliNumber(int n) {
		if (n == 0) {
			return F.C1;
		} else if (n == 1) {
			return F.CN1D2;
		} else if (n < 0) {
			throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
		} else if (n % 2 != 0) {
			// http://fungrim.org/entry/a98234/
			return F.C0;
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
		return bernoulli[n].normalize();
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>. <br/>
	 * For better performing implementations see
	 * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
	 * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
	 * 
	 * @param n
	 * @return throws ArithmeticException if n is not an non-negative Java int number
	 */
	public static IRational bernoulliNumber(final IInteger n) {
		int bn = n.toIntDefault(-1);
		if (bn >= 0) {
			return bernoulliNumber(bn);
		}
		throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
	}

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
	 * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
	 * 
	 * @param k
	 *            the non-negative coefficients
	 * @param n
	 *            the sum of the non-negative coefficients
	 * @return
	 */
	public static IInteger multinomial(final int[] k, final int n) {
		IInteger bn = AbstractIntegerSym.valueOf(n);
		IInteger result = factorial(bn);
		for (int i = 0; i < k.length; i++) {
			if (k[i] != 0) {
				result = result.div(factorial(k[i]));
			}
		}
		return result;
	}

	/**
	 * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
	 * 
	 * @param k
	 *            the non-negative coefficients
	 * @return
	 */
	public static IInteger multinomial(IInteger[] k) {
		IInteger n = F.C0;
		for (int i = 0; i < k.length; i++) {
			n = n.add(k[i]);
		}
		int ni = n.toIntDefault(Integer.MIN_VALUE);
		if (ni > 0) {
			int[] ki = new int[k.length];
			boolean evaled = true;
			for (int i = 0; i < k.length; i++) {
				ki[i] = k[i].toIntDefault(Integer.MIN_VALUE);
				if (ki[i] < 0) {
					evaled = false;
					break;
				}
			}
			if (evaled) {
				return multinomial(ki, ni);
			}
		}
		IInteger result = factorial(n);
		for (int i = 0; i < k.length; i++) {
			result = result.div(factorial(k[i]));
		}
		return result;
	}

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
	 * @return {@code S2(nArg1,kArg2)} or throw <code>ArithmeticException</code> if <code>n</code> cannot be converted
	 *         into a positive int number
	 */
	public static IInteger stirlingS2(IInteger n, IInteger k, int ki) throws MathRuntimeException {
		// try {
		int ni = n.toIntDefault(0);
		if (ni != 0 && ni <= 25) {// S(26,9) = 11201516780955125625 is larger than Long.MAX_VALUE
			return F.ZZ(CombinatoricsUtils.stirlingS2(ni, ki));
		}
		// } catch (MathRuntimeException mre) {
		// if (Config.DEBUG) {
		// mre.printStackTrace();
		// }
		// }
		IInteger sum = F.C0;
		int nInt = n.toIntDefault(-1);
		if (nInt < 0) {
			throw new ArithmeticException("StirlingS2(n,k) n is not a positive int number");
		}
		for (int i = 0; i < ki; i++) {
			IInteger bin = binomial(k, F.ZZ(i));
			IInteger pow = k.add(F.ZZ(-i)).pow(nInt);
			if ((i & 1) == 1) { // isOdd(i) ?
				sum = sum.add(bin.negate().multiply(pow));
			} else {
				sum = sum.add(bin.multiply(pow));
			}
		}
		return sum.div(factorial(k));
	}

	/**
	 * The first 49 perfect numbers.
	 * 
	 * See <a href= "https://en.wikipedia.org/wiki/List_of_perfect_numbers">List_of_perfect_numbers</a>
	 */
	private final static long[] PN_8 = { 6, 28, 496, 8128, 33550336L, 8589869056L, 137438691328L,
			2305843008139952128L };

	/**
	 * The first 47 mersenne prime exponents.
	 * 
	 * See <a href="https://en.wikipedia.org/wiki/Mersenne_prime">Mersenne prime</a>
	 */
	private final static int[] MPE_47 = { 2, 3, 5, 7, 13, 17, 19, 31, 61, 89, 107, 127, 521, 607, 1279, 2203, 2281,
			3217, 4253, 4423, 9689, 9941, 11213, 19937, 21701, 23209, 44497, 86243, 110503, 132049, 216091, 756839,
			859433, 1257787, 1398269, 2976221, 3021377, 6972593, 13466917, 20996011, 24036583, 25964951, 30402457,
			32582657, 37156667, 42643801, 43112609

	};

	private NumberTheory() {

	}

	/**
	 * Rationalize only pure numeric numbers in expression <code>arg</code>.
	 * 
	 * @param arg1
	 * @return <code>F.NIL</code> if no expression was transformed
	 */
	public static IExpr rationalize(IExpr arg1) {
		return NumberTheory.rationalize(arg1, Config.DOUBLE_EPSILON);
	}

	/**
	 * Rationalize only pure numeric numbers in expression <code>arg</code>.
	 * 
	 * @param arg1
	 * @param epsilon
	 * @return <code>F.NIL</code> if no expression was transformed
	 */
	public static IExpr rationalize(IExpr arg1, double epsilon) {
		Rationalize.RationalizeNumericsVisitor rationalizeVisitor = new Rationalize.RationalizeNumericsVisitor(epsilon);
		return arg1.accept(rationalizeVisitor);
	}
}
