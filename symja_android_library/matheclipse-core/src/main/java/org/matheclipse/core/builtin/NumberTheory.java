package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
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
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

public final class NumberTheory {
	private static class BellB extends AbstractFunctionEvaluator {

		/**
		 * Generates the Bell Number of the given index, where B(1) is 1. This is recursive.
		 * 
		 * @param index
		 * @return
		 */
		public static BigInteger generateBellNumber(int index) {
			if (index > 1) {
				BigInteger sum = BigInteger.ZERO;
				for (int i = 0; i < index; i++) {
					BigInteger prevBellNum = generateBellNumber(i);
					BigInteger binomialCoeff = BigIntegerMath.binomial(index - 1, i);
					sum = sum.add(binomialCoeff.multiply(prevBellNum));
				}
				return sum;
			}
			return BigInteger.ONE;
		}

		public BellB() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isOne()) {
				return F.C1;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				try {
					int index = ((IInteger) arg1).toInt();
					BigInteger bellB = generateBellNumber(index);
					return F.integer(bellB);
				} catch (ArithmeticException ae) {
					//
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
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>. <br/>
	 * For better performing implementations see
	 * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
	 * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
	 * 
	 */
	private static class BernoulliB extends AbstractFunctionEvaluator {

		/**
		 * Compute the Bernoulli number of the first kind.
		 * 
		 * @param biggi
		 * @return
		 */
		public static IFraction bernoulliNumber(final IInteger biggi) {
			int N = 0;
			try {
				N = biggi.toInt(); // NumberUtil.toInt(biggi);
				return bernoulliNumber(N);
			} catch (ArithmeticException ae) {
				//
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

		// /**
		// * Compute the Bernoulli number of the first kind.
		// *
		// * @param biggi
		// * @return
		// */
		// public static IFraction bernoulliNumber(final IInteger biggi) {
		// return bernoulliNumber(biggi.getBigNumerator());
		// }

		public BernoulliB() {
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
	 * Returns the binomial coefficient of 2 integers.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial coefficient</a>
	 */
	private static class Binomial extends AbstractArg2 {

		public static IInteger binomial(final IInteger n, final IInteger k) {
			// k>n : by definition --> 0
			if (k.compareTo(n) > 0) {
				return F.C0;
			}
			if (k.isZero() || k.equals(n)) {
				return F.C1;
			}

			try {
				int ni = n.toInt();
				int ki = k.toInt();
				if (ki > ni) {
					return F.C0;
				}
				return AbstractIntegerSym.valueOf(BigIntegerMath.binomial(ni, ki));
			} catch (ArithmeticException ae) {
				//
			}

			IInteger bin = F.C1;
			IInteger i = F.C1;

			while (!(i.compareTo(k) > 0)) {
				bin = bin.multiply(n.subtract(i).add(F.C1)).div(i);
				i = i.add(F.C1);
			}
			return bin;
		}

		public Binomial() {
		}

		@Override
		public IExpr e2IntArg(final IInteger n, final IInteger k) {
			return binomial(n, k);
		}

		@Override
		public IExpr e2ObjArg(final IExpr n, final IExpr k) {
			if (k.isInteger()) {
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
					IAST ast = F.TimesAlloc(kInt);
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
			IExpr nMinus1 = F.eval(F.Subtract(n, F.C1));
			if (nMinus1.equals(k)) {
				return n;
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
	 * <p>
	 * The Carmichael function.
	 * </p>
	 * <p>
	 * See: <a href="https://en.wikipedia.org/wiki/Carmichael_function">Wikipedia - Carmichael function</a>
	 * </p>
	 *
	 */
	private static class CarmichaelLambda extends AbstractTrigArg1 {

		public CarmichaelLambda() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger() && !arg1.isNegative()) {
				try {
					return ((IInteger) arg1).charmichaelLambda();
				} catch (ArithmeticException ae) {

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
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Catalan_number">Wikipedia:Catalan number</a>
	 * 
	 */
	private static class CatalanNumber extends AbstractTrigArg1 {
		public static IInteger catalanNumber(IInteger n) {
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

		public CatalanNumber() {
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
	 * See <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta Code: Chinese remainder theorem</a>
	 *
	 */
	private static class ChineseRemainder extends AbstractFunctionEvaluator {
		private static long chineseRemainder(long[] n, long[] a) {
			long prod = 1;
			for (int k = 0; k < n.length; k++) {
				prod = prod * n[k];
			}

			long p, sm = 0;
			for (int i = 0; i < n.length; i++) {
				p = prod / n[i];
				sm += a[i] * mulInv(p, n[i]) * p;
			}
			return sm % prod;
		}

		private static long mulInv(long a, long b) {
			long b0 = b;
			long x0 = 0;
			long x1 = 1;

			if (b == 1)
				return 1;

			while (a > 1) {
				long q = a / b;
				long amb = a % b;
				a = b;
				b = amb;
				long xqx = x1 - q * x0;
				x1 = x0;
				x0 = xqx;
			}

			if (x1 < 0)
				x1 += b0;

			return x1;
		}

		public ChineseRemainder() {
		}

		/**
		 * <p>
		 * Calculate the chinese remainder of 2 integer lists.
		 * </p>
		 * <p>
		 * See <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem"> Rosetta Code: Chinese remainder
		 * theorem</a>
		 * </p>
		 *
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			long[] a = Validate.checkListOfLongs(ast.arg1(), 0L);
			long[] n = Validate.checkListOfLongs(ast.arg2(), 0L);
			if (a.length != n.length) {
				return F.NIL;
			}
			try {
				return F.integer(chineseRemainder(n, a));
			} catch (ArithmeticException ae) {
				// mulInv may throw a division by zero ArithmeticException
				return F.NIL;
			}
		}

	}

	/**
	 * The integers a and b are said to be <i>coprime</i> or <i>relatively prime</i> if they have no common factor other
	 * than 1.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia:Coprime</a>
	 */
	private static class CoprimeQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			int size = ast.size();
			IExpr expr;
			for (int i = 1; i < size - 1; i++) {
				expr = ast.get(i);
				for (int j = i + 1; j < size; j++) {
					if (!engine.evaluate(F.GCD(expr, ast.get(j))).isOne()) {
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
	 * DiracDelta function returns <code>0</code> for all x != 0
	 */
	private static class DiracDelta extends AbstractEvaluator {

		public DiracDelta() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size > 1) {
				for (int i = 1; i < size; i++) {
					IExpr expr = ast.get(i);
					ISignedNumber temp = expr.evalSignedNumber();
					if (temp != null) {
						if (temp.isZero()) {
							return F.NIL;
						}
						continue;
					}
					return F.NIL;
				}
			}
			return F.C0;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	private static class Divisible extends AbstractFunctionEvaluator {

		public Divisible() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				// thread over first list
				return ((IAST) ast.arg1()).mapThread(F.List(), ast, 1);
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
	 * Return the divisors of an integer number.
	 * 
	 * <pre>
	 * divisors(24) ==> {1,2,3,4,6,8,12,24}
	 * </pre>
	 */
	private static class Divisors extends AbstractTrigArg1 {

		public Divisors() {
		}

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
	 * <code>DivisorSigma(k,n)</code> - the sum of the <code>k</code>-th powers of the divisors of <code>n</code>.
	 *
	 */
	private static class DivisorSigma extends AbstractFunctionEvaluator {

		public DivisorSigma() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg2.isInteger() && arg2.isPositive()) {

				IInteger n = (IInteger) arg2;
				IAST list = n.divisors();
				if (list.isList()) {
					if (arg1.isInteger()) {
						// special formula if k is integer
						IInteger k = (IInteger) arg1;
						try {
							long kl = k.toLong();

							IInteger sum = F.C0;
							for (int i = 1; i < list.size(); i++) {
								sum = sum.add(((IInteger) list.get(i)).pow(kl));
							}
							return sum;
						} catch (ArithmeticException ae) {
							//
						}
					}
					// general formula
					IAST sum = F.PlusAlloc(list.size());
					for (int i = 1; i < list.size(); i++) {
						sum.append(F.Power(list.get(i), arg1));
					}
					return sum;
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
	 * Euler number
	 * </p>
	 * 
	 * <p>
	 * See <a href="http://oeis.org/A000364">A000364</a> in the OEIS.
	 * </p>
	 */
	private static class EulerE extends AbstractTrigArg1 {

		public EulerE() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					int n = ((IInteger) arg1).toInt();
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
				} catch (ArithmeticException e) {
					// integer to large?
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
	 * Euler phi function
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient function</a>
	 */
	private static class EulerPhi extends AbstractTrigArg1 {

		public EulerPhi() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).eulerPhi();
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
	 * Returns the factorial of an integer n
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Factorial">Factorial</a>
	 * 
	 */
	private static class Factorial extends AbstractTrigArg1 {

		public Factorial() {
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double d = org.hipparchus.special.Gamma.gamma(arg1 + 1.0);
			return F.num(d);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				return factorial((IInteger) arg1);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

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

		public Factorial2() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				if (!arg1.isNegative()) {
					return factorial2((IInteger) arg1);
				}
				try {
					int n = ((IInteger) arg1).toInt();
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
				} catch (ArithmeticException ae) {

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
	 * Return the factors of an integer number
	 * 
	 * FactorInteger[-32536] ==> {{-1,1},{2,3},{7,2},{83,1}}
	 */
	private static class FactorInteger extends AbstractTrigArg1 {

		public FactorInteger() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isRational()) {
				return ((IRational) arg1).factorInteger();
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
	 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F
	 * </p>
	 * See: <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code: Fibonacci
	 * sequence.</a>
	 */
	private static class Fibonacci extends AbstractTrigArg1 {

		public Fibonacci() {
		}

		// public static BigInteger fibonacci(long k) {
		// return fibonacci(BigInteger.valueOf(k));
		// }

		// public static BigInteger fibonacci(BigInteger temp) {
		// BigInteger a = BigInteger.ONE;
		// BigInteger b = BigInteger.ZERO;
		// BigInteger c = BigInteger.ONE;
		// BigInteger d = BigInteger.ZERO;
		// BigInteger result = BigInteger.ZERO;
		// while (!temp.equals(BigInteger.ZERO)) {
		// if (NumberUtil.isOdd(temp)) {
		// d = result.multiply(c);
		// result = a.multiply(c).add(result.multiply(b).add(d));
		// a = a.multiply(b).add(d);
		// }
		//
		// d = c.multiply(c);
		// c = b.multiply(c).shiftLeft(1).add(d);
		// b = b.multiply(b).add(d);
		// temp = temp.shiftRight(1);
		// }
		// return result;
		// }

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				return fibonacci((IInteger) arg1);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a>
	 */
	private static class JacobiSymbol extends AbstractArg2 {

		public JacobiSymbol() {
		}

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

	private static class KroneckerDelta extends AbstractEvaluator {

		public KroneckerDelta() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size > 1) {
				IExpr arg1 = ast.arg1();
				INumber temp = arg1.evalNumber();
				if (temp != null) {
					if (size == 2) {
						if (temp.isZero()) {
							return F.C1;
						}
						if (temp.isNumber()) {
							return F.C0;
						}
						return F.NIL;
					}
					arg1 = temp;
					for (int i = 2; i < size; i++) {
						IExpr expr = ast.get(i);
						temp = expr.evalNumber();
						if (temp != null && temp.equals(arg1)) {
							continue;
						}
						return F.C0;
					}
					return F.C1;
				}

			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 */
	private static class LiouvilleLambda extends AbstractFunctionEvaluator {

		public LiouvilleLambda() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isOne()) {
				return F.C1;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				IExpr expr = engine.evaluate(F.FactorInteger(arg1));
				if (expr.isList()) {
					IAST list = (IAST) expr;
					int result = 1;
					IInteger temp;
					for (int i = 1; i < list.size(); i++) {
						temp = (IInteger) list.get(i).getAt(2);
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

		public LucasL() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			try {
				if (arg1.isInteger()) {
					IInteger n = (IInteger) arg1;
					// LucasL(n) = Fibonacci(n-1) + Fibonacci(n+1)
					return fibonacci(n.subtract(F.CN1)).add(fibonacci(n.add(F.CN1)));
				}
			} catch (ArithmeticException ae) {
				// because of toInt() method
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * The Möbius function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/M%C3%B6bius_function">Wikipedia: Möbius function</a>
	 */
	private static class MoebiusMu extends AbstractTrigArg1 {

		public MoebiusMu() {
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				try {
					return ((IInteger) arg1).moebiusMu();
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
	 * Returns the multinomial coefficient.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Multinomial_coefficient"> Multinomial coefficient</a>
	 */
	private static class Multinomial extends AbstractFunctionEvaluator {
		public static IInteger multinomial(final IAST ast) {
			IInteger[] k = new IInteger[ast.size() - 1];
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
			for (int i = 1; i < ast.size(); i++) {
				if (!(ast.get(i).isInteger())) {
					return F.NIL;
				}
				if (((IInteger) ast.get(i)).isNegative()) {
					return F.NIL;
				}
			}

			return multinomial(ast);

		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia: Multiplicative order</a> and
	 * <a href="https://rosettacode.org/wiki/Multiplicative_order">Rosettacode. org: Multiplicative order</a>.
	 *
	 */
	private static class MultiplicativeOrder extends AbstractFunctionEvaluator {

		private static IInteger multiplicativeOrder(IInteger a, IInteger prime, long exponent) {
			IInteger m = prime.pow(exponent);
			IInteger t = m.div(prime).multiply(prime.subtract(F.C1));
			IAST divisors = t.divisors();
			int len = divisors.size();
			for (int i = 1; i < len; i++) {
				IInteger factor = divisors.getInt(i);
				if (a.modPow(factor, m).isOne()) {
					return factor;
				}
			}
			return F.C0;
		}

		public MultiplicativeOrder() {
			// default ctor
		}

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

					IAST primeExponentList = n.factorInteger();
					IInteger res = F.C1;
					for (int i = 1; i < primeExponentList.size(); i++) {
						res = res.lcm(multiplicativeOrder(k, primeExponentList.getAST(i).getInt(1),
								primeExponentList.getAST(i).getInt(2).toLong()));
					}
					return res;
				} catch (ArithmeticException ae) {

				}

			}
			return F.NIL;
		}
	}

	/**
	 * Get the next prime number. See: <a href="http://en.wikipedia.org/wiki/Prime_number">Wikipedia:Prime number</a>
	 * 
	 * @see org.matheclipse.core.builtin.function.PrimeQ
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
	 * <code>Prime(i)</code> gives the i-th prime number for <code>i</code> less equal 103000000.
	 * 
	 * See: <a href="https://bitbucket.org/dafis/javaprimes">https://bitbucket.org/ dafis/ javaprimes</a><br />
	 * <a href=
	 * "http://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number/9704912#9704912">
	 * stackoverflow. com - Calculating and printing the nth prime number</a>
	 */
	private static class Prime extends AbstractFunctionEvaluator {

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

		// Count number of set bits in an int
		public static int popCount(int n) {
			n -= (n >>> 1) & 0x55555555;
			n = ((n >>> 2) & 0x33333333) + (n & 0x33333333);
			n = ((n >> 4) & 0x0F0F0F0F) + (n & 0x0F0F0F0F);
			return (n * 0x01010101) >> 24;
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

	/**
	 */
	private static class PrimeOmega extends AbstractFunctionEvaluator {

		public PrimeOmega() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isOne()) {
				return F.C0;
			}
			if (arg1.isInteger() && arg1.isPositive()) {
				IExpr expr = engine.evaluate(F.FactorInteger(arg1));
				if (expr.isList()) {
					IAST list = (IAST) expr;
					IInteger temp;
					IInteger sum = F.C0;
					for (int i = 1; i < list.size(); i++) {
						temp = (IInteger) list.get(i).getAt(2);
						sum = sum.add(temp);
					}
					return sum;
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
	 */
	private static class PrimePowerQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				IInteger i = (IInteger) arg1;
				if (i.isNegative()) {
					i = i.negate();
				}
				IAST list = i.factorInteger();
				if (list.isAST1()) {
					IInteger temp = (IInteger) list.get(1).getAt(2);
					if (!temp.isOne()) {
						return F.True;
					}
				}
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * Check if a univariate polynomial or an integer number is square free
	 * 
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
					IInteger i = (IInteger) arg1;
					if (i.isNegative()) {
						i = i.negate();
					}
					IAST list = i.factorInteger();
					for (int j = 1; j < list.size(); j++) {
						IInteger temp = (IInteger) list.get(j).getAt(2);
						if (temp.isGreaterThan(F.C1)) {
							return F.False;
						}
					}

					return F.True;
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
				IExpr expr = F.evalExpandAll(ast.arg1());
				ASTRange r = new ASTRange(eVar.getVarList(), 1);
				List<IExpr> varList = r;

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
	 * Stirling numbers of the first kind.
	 * 
	 * See <a href= "https://en.wikipedia.org/wiki/Stirling_numbers_of_the_first_kind" > Wikipedia - Stirling numbers of
	 * the first kind</a>
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

			try {
				int counter = nSubtractm.toInt() + 1;
				IInteger k;
				IAST temp = F.PlusAlloc(counter >= 0 ? counter : 0);
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
			} catch (ArithmeticException ae) {
				// because of toInt() method
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
	 * Stirling numbers of the second kind.
	 * 
	 * See <a href= "http://en.wikipedia.org/wiki/Stirling_numbers_of_the_second_kind" > Wikipedia - Stirling numbers of
	 * the second kind</a>
	 */
	private static class StirlingS2 extends AbstractFunctionEvaluator {

		private static IExpr stirlingS2(IInteger nArg1, IInteger kArg2, int k) {
			IAST temp = F.PlusAlloc(k >= 0 ? k : 0);
			for (int i = 0; i < k; i++) {
				if ((i & 1) == 1) { // isOdd(i) ?
					temp.append(Times(Negate(Binomial(kArg2, integer(i))), Power(Plus(kArg2, integer(-i)), nArg1)));
				} else {
					temp.append(Times(Times(Binomial(kArg2, integer(i))), Power(Plus(kArg2, integer(-i)), nArg1)));
				}
			}
			return Times(Power(Factorial(kArg2), CN1), temp);
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
				if (kArg2.greaterThan(nArg1).isTrue()) {
					return C0;
				}
				if (kArg2.isZero()) {
					return C0;
				}
				if (kArg2.isOne()) {
					// {n,1}==1
					return C1;
				}
				if (kArg2.equals(C2)) {
					// {n,2}==2^(n-1)-1
					return Subtract(Power(C2, Subtract(nArg1, C1)), C1);
				}

				try {
					int k = ((ISignedNumber) kArg2).toInt();
					return stirlingS2((IInteger) nArg1, (IInteger) kArg2, k);
				} catch (ArithmeticException ae) {
					// because of toInt() method
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
	 * Returns the subfactorial of a positive integer n
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
	 * 
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

		public Subfactorial() {
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
		F.Divisible.setEvaluator(new Divisible());
		F.Divisors.setEvaluator(new Divisors());
		F.DivisorSigma.setEvaluator(new DivisorSigma());
		F.EulerE.setEvaluator(new EulerE());
		F.EulerPhi.setEvaluator(new EulerPhi());
		F.Factorial.setEvaluator(new Factorial());
		F.Factorial2.setEvaluator(new Factorial2());
		F.FactorInteger.setEvaluator(new FactorInteger());
		F.Fibonacci.setEvaluator(new Fibonacci());
		F.JacobiSymbol.setEvaluator(new JacobiSymbol());
		F.KroneckerDelta.setEvaluator(new KroneckerDelta());
		F.LiouvilleLambda.setEvaluator(new LiouvilleLambda());
		F.LucasL.setEvaluator(new LucasL());
		F.MoebiusMu.setEvaluator(new MoebiusMu());
		F.Multinomial.setEvaluator(new Multinomial());
		F.MultiplicativeOrder.setEvaluator(new MultiplicativeOrder());
		F.NextPrime.setEvaluator(new NextPrime());
		F.Prime.setEvaluator(new Prime());
		F.PrimeOmega.setEvaluator(new PrimeOmega());
		F.PrimePowerQ.setEvaluator(new PrimePowerQ());
		F.SquareFreeQ.setEvaluator(new SquareFreeQ());
		F.StirlingS1.setEvaluator(new StirlingS1());
		F.StirlingS2.setEvaluator(new StirlingS2());
		F.Subfactorial.setEvaluator(new Subfactorial());
	}

	final static NumberTheory CONST = new NumberTheory();

	public static IInteger factorial(final IInteger x) {
		try {
			int ni = x.toInt();

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

		} catch (ArithmeticException ae) {
			//
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
	public static IInteger fibonacci(final IInteger iArg) {
		IInteger a = F.C1;
		IInteger b = F.C0;
		IInteger c = F.C1;
		IInteger d = F.C0;
		IInteger result = F.C0;
		IInteger temp = iArg;
		if (iArg.isNegative()) {
			temp = temp.negate();
		}

		while (!temp.isZero()) {
			if (temp.isOdd()) {
				d = result.multiply(c);
				result = a.multiply(c).add(result.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).shiftLeft(1).add(d);
			b = b.multiply(b).add(d);
			temp = temp.shiftRight(1);
		}
		if (iArg.isNegative() && iArg.isEven()) {
			return result.negate();
		}
		return result;
	}

	public static NumberTheory initialize() {
		return CONST;
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

	private NumberTheory() {

	}
}
