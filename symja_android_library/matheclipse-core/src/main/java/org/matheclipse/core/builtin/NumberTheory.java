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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.CombinatoricsUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
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
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.GaussianInteger;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.sympy.series.Sequences;
import org.matheclipse.core.visit.VisitorExpr;
import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;
import com.google.common.util.concurrent.UncheckedExecutionException;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;

public final class NumberTheory {

  private static final int[] FIBONACCI_45 = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377,
      610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811,
      514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169,
      63245986, 102334155, 165580141, 267914296, 433494437, 701408733, 1134903170};

  private static final long[] BELLB_25 = {1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570,
      4213597, 27644437, 190899322L, 1382958545L, 10480142147L, 82864869804L, 682076806159L,
      5832742205057L, 51724158235372L, 474869816156751L, 4506715738447323L, 44152005855084346L,
      445958869294805289L, 4638590332229999353L};

  /**
   *
   *
   * <pre>
   * BellB(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the Bell number function counts the number of different ways to partition a set that has
   * exactly <code>n</code> elements
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bell_number">Wikipedia - Bell number</a>
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
     * @param index an int number >= 0
     * @return
     */
    private static IExpr bellNumber(int index) {
      if (index < BELLB_25.length) {
        return AbstractIntegerSym.valueOf(BELLB_25[index]);
      }

      // Sum[StirlingS2[n, k], {k, 0, n}]
      return F.sum(k -> stirlingS2(index, k, k.toIntDefault()), 0, index, 1);
    }

    /**
     * Generates the Bell polynomial of the given index <code>n</code>, where B(1) is 1. This is
     * recursive.
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

      return F.sum(k -> F.Times(F.StirlingS2(F.ZZ(n), k), F.Power(z, k)), 0, n + 1, 1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (arg1.isNegative()) {
          // Non-negative machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(S.BellB, "intnm", F.list(ast, F.C1), engine);
        }
        int n = arg1.toIntDefault();
        if (n < 0) {
          if (arg1.isNumber()) {
            // Non-negative machine-sized integer expected at position `2` in `1`
            return Errors.printMessage(S.BellB, "intnm", F.list(ast, F.C1), engine);
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
            if (n > Config.MAX_POLYNOMIAL_DEGREE) {
              PolynomialDegreeLimitExceeded.throwIt(n);
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
            return bellNumber(n);
          }
        }
      } catch (RuntimeException rex) {
        Errors.printMessage(S.BellB, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * BernoulliB(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the Bernoulli number of the first kind.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>
   * </ul>
   */
  private static class BernoulliB extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isNegative()) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.BernoulliB, "intnm", F.List(ast, F.C1), engine);
      }
      if (ast.isAST1()) {
        int bn = ast.arg1().toIntDefault();
        if (bn >= 0) {
          return bernoulliNumber(bn);
        }
        IExpr temp = engine.evaluate(F.Subtract(ast.arg1(), F.C3));
        if (temp.isIntegerResult() && temp.isPositiveResult() && temp.isEvenResult()) {
          // http://fungrim.org/entry/a98234/
          return F.C0;
        }
        return F.NIL;
      }
      if (ast.isAST2()) {
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
        }
        if (n.isInteger() && n.isNonNegativeResult()) {
          if (x.isNumEqualRational(F.C1D2)) {
            // http://fungrim.org/entry/03ee0b/
            return F.Times(F.Subtract(F.Power(F.C2, F.Subtract(F.C1, n)), F.C1), F.BernoulliB(n));
          }
          int bn = n.toIntDefault();
          if (bn >= 0) {
            // http://fungrim.org/entry/555e10/
            return F.sum(
                k -> F.Times(F.Binomial(n, k), F.BernoulliB(F.Subtract(n, k)), F.Power(x, k)), 0,
                bn);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Binomial(n, k)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the binomial coefficient of the 2 integers <code>n</code> and <code>k</code>
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Wikipedia - Binomial
   * coefficient</a>
   * </ul>
   *
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
    public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.binomial(a1.apfloatValue(), a2.apfloatValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.binomial(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr e2IntArg(final IInteger n, final IInteger k) {
      return binomial(n, k);
    }

    @Override
    public IExpr e2ObjArg(IAST ast, IExpr n, IExpr k) {
      if (n.isInteger() && k.isInteger()) {
        // use e2IntArg() method
        return F.NIL;
      }
      int ni = n.toIntDefault();
      if (ni != Integer.MIN_VALUE) {
        int ki = k.toIntDefault();
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
          int ki = k.toIntDefault();
          if (ki >= 1 && ki <= 5) {
            return S.Infinity;
          }
        } else if (n.isNegativeInfinity()) {
          if (k.isNegative()) {
            return F.C0;
          }
          int ki = k.toIntDefault();
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
          IExpr temp;
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
        return binomialNumeric((INumber) n, (INumber) k);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * CarmichaelLambda(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the Carmichael function of <code>n</code>
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carmichael_function">Wikipedia - Carmichael
   * function</a>
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * CatalanNumber(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the catalan number for the integer argument <code>n</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Catalan_number">Wikipedia - Catalan number</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; CatalanNumber(4)
   * 14
   * </pre>
   */
  private static class CatalanNumber extends AbstractTrigArg1 {

    @Override
    public IExpr evaluateArg1(final IExpr n, EvalEngine engine) {
      if (n.isInteger()) {
        return catalanNumber((IInteger) n);
      } else if (n.isFraction()) {
        if (((IFraction) n).denominator().equals(F.C2)) {
          return functionExpand(n);
        }
      }

      return F.NIL;
    }

    @Override
    public IExpr numericEval(final IAST ast, final EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isInexactNumber()) {
          return functionExpand(arg1);
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.CatalanNumber, rex, engine);
      }
      return evaluateArg1(arg1, engine);
    }

    private static IExpr functionExpand(final IExpr n) {
      // (2^(2*n)*Gamma(1/2+n))/(Sqrt(Pi)*Gamma(2+n))
      return F.Times(F.Power(F.C2, F.Times(F.C2, n)), F.Gamma(F.Plus(n, F.C1D2)),
          F.Power(F.Times(F.Sqrt(S.Pi), F.Gamma(F.Plus(n, F.C2))), F.CN1));
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the chinese remainder function.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Chinese_remainder_theorem">Wikipedia -
   * Chinese_remainder_theorem</a>
   * <li><a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta Code - Chinese
   * remainder theorem</a>
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
     * @param primes list of coprime numbers
     * @param remainders remainder
     * @return the result
     */
    private static long chineseRemaindersInt(final int[] primes, final int[] remainders) {
      if (primes.length != remainders.length) {
        // The arguments to `1` must be two lists of integers of identical length, with the second
        // list only
        // containing positive integers.
        String message = Errors.getMessage("pilist", F.list(S.ChineseRemainder), EvalEngine.get());
        throw new ArgumentTypeException(message);
      }
      long modulus = primes[0];
      for (int i = 1; i < primes.length; ++i) {
        if (primes[i] <= 0) {
          // The arguments to `1` must be two lists of integers of identical length, with the second
          // list only containing positive integers.
          String message =
              Errors.getMessage("pilist", F.list(S.ChineseRemainder), EvalEngine.get());
          throw new ArgumentTypeException(message);
        }
        modulus = Math.multiplyExact(primes[i], modulus);
      }

      long result = 0;
      for (int i = 0; i < primes.length; ++i) {
        long iModulus = modulus / primes[i];
        long bezout = bezout0(iModulus, primes[i]);
        result =
            floorMod(
                addExact(result,
                    floorMod(multiplyExact(iModulus,
                        floorMod(multiplyExact(bezout, remainders[i]), primes[i])), modulus)),
                modulus);
      }
      return result;
    }

    /**
     * Runs Chinese Remainders algorithm
     *
     * @param primes list of coprime numbers
     * @param remainders remainder
     * @return the result
     */
    private static BigInteger chineseRemainders(final BigInteger[] primes,
        final BigInteger[] remainders) {
      if (primes.length != remainders.length) {
        // The arguments to `1` must be two lists of integers of identical length, with the second
        // list only
        // containing positive integers.
        String message = Errors.getMessage("pilist", F.list(S.ChineseRemainder), EvalEngine.get());
        throw new ArgumentTypeException(message);
      }
      BigInteger m = primes[0];
      for (int i = 1; i < primes.length; i++) {
        if (primes[i].signum() <= 0) {
          // The arguments to `1` must be two lists of integers of identical length, with the second
          // list only
          // containing positive integers.
          String message =
              Errors.getMessage("pilist", F.list(S.ChineseRemainder), EvalEngine.get());
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
     * Calculate the chinese remainder of 2 integer lists.
     *
     * <p>
     * See <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta Code: Chinese
     * remainder theorem</a><br>
     * <a href=
     * "https://github.com/PoslavskySV/rings/blob/master/rings/src/main/java/cc/redberry/rings/bigint/ChineseRemainders.java">cc/redberry/rings/bigint/ChineseRemainders.java</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg2().isList()) {
        try {
          int[] a = Validate.checkListOfInts(ast, ast.arg1(), false, true, engine);
          if (a == null) {
            return chineseRemainderBigInteger(ast, engine);
          }
          int[] n = Validate.checkListOfInts(ast, ast.arg2(), false, true, engine);
          if (n == null) {
            return chineseRemainderBigInteger(ast, engine);
          }
          if (a.length != n.length) {
            return F.NIL;
          }
          if (a.length == 0) {
            return F.NIL;
          }
          try {
            return F.ZZ(chineseRemaindersInt(n, a));
          } catch (ArithmeticException aex) {
            // from Math.multiplyExact()
            return chineseRemainderBigInteger(ast, engine);
          }
        } catch (ArithmeticException ae) {
          Errors.printMessage(S.ChineseRemainder, ae, engine);
        }
      }
      return F.NIL;
    }

    private static IExpr chineseRemainderBigInteger(final IAST ast, EvalEngine engine) {
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
        Errors.printMessage(S.ChineseRemainder, ae, engine);
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Convergents({n1, n2, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the list of convergents which represents the continued fraction list <code>
   * {n1, n2, ...}</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Continued_fraction">Wikipedia - Continued
   * fraction</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Convergents({2,3,4,5})
   * {2,7/3,30/13,157/68}
   * </pre>
   */
  private static final class Convergents extends AbstractEvaluator {

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
          IASTMutable plus = F.binary(S.Plus, F.C0, list.arg1());
          IASTMutable result = plus;
          for (int i = 2; i <= size; i++) {
            IExpr temp;
            if (result.isAST()) {
              temp = engine.evaluate(F.Together(((IAST) result).copy()));
            } else {
              temp = engine.evaluate(result);
            }
            resultList.append(temp);
            IASTMutable plusAST = F.binary(S.Plus, F.C0, list.get(i));
            plus.set(1, F.Power(plusAST, F.CN1));
            plus = plusAST;
          }
          resultList.append(engine.evaluate(F.Together(result)));
          return resultList;
        } else if (list.size() == 1) {
          return F.CListC0;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * ContinuedFraction(number)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the continued fraction representation of <code>number</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Continued_fraction">Wikipedia - Continued
   * fraction</a>
   * </ul>
   *
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
   *
   * <p>
   * For square roots of non-negative integer arguments <code>ContinuedFraction</code> determines
   * the periodic part:
   *
   * <pre>
   * &gt;&gt; ContinuedFraction(Sqrt(13))
   * {3,{1,1,1,1,6}}
   *
   * &gt;&gt; ContinuedFraction(Sqrt(919))
   * {30,3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}}
   * </pre>
   */
  private static final class ContinuedFraction extends AbstractEvaluator {

    /**
     * Find the periodic continued fraction expansion of a quadratic irrational of the form <code>
     * (p + s*Sqrt(d)) / q</code>
     *
     * <p>
     * Compute the continued fraction expansion of a rational or a quadratic irrational number, i.e.
     * <code>(p + s*Sqrt(d)) / q</code>, where `p`, `q != 0` and `d != 0` are integers. Returns the
     * continued fraction representation (canonical form) as a list of integers, optionally ending
     * (for quadratic irrationals) with list of integers representing the repeating (periodic)
     * digits.
     *
     * @param p
     * @param q
     * @param d
     * @param s
     * @param negate
     * @param maxIterations
     * @param engine
     * @return {@link F#NIL} if the evaluation into integers wasn't possible
     */
    private IAST continuedFractionPeriodic(IInteger p, IInteger q, IInteger d, IInteger s,
        boolean negate, int maxIterations, EvalEngine engine) {
      // https://github.com/sympy/sympy/blob/07a6388bc237a2c43e65dc3cf932373e4d06d91b/sympy/ntheory/continued_fraction.py#L71
      IExpr sd = F.Sqrt(d);
      if (q.isNegative()) {
        p = p.negate();
        q = q.negate();
        s = s.negate();
      }

      IExpr n = S.Times.of(engine, Plus(p, F.Times(s, sd)));
      if (n.isNegativeResult()) {
        IAST resultList =
            continuedFractionPeriodic(p.negate(), q, d, s.negate(), true, maxIterations, engine);
        if (resultList.isList()) {
          return resultList;
        }
        return F.NIL;
      }

      d = d.multiply(s.multiply(s));
      sd = F.Times(s, sd);
      if (!d.subtract(p.multiply(p)).mod(q).isZero()) {
        d = d.multiply(q.multiply(q));
        sd = S.Times.of(engine, sd, q);
        p = p.multiply(q);
        q = q.multiply(q);
      }

      IASTAppendable integerTerms = F.ListAlloc();
      Map<IAST, Integer> pqPeriodic2Index = new HashMap<IAST, Integer>();
      IAST key = F.list(p, q);
      do {
        pqPeriodic2Index.put(key, integerTerms.size() - 1);
        IExpr quotient = S.Quotient.of(engine, F.Plus(p, sd), q);
        if (!quotient.isInteger()) {
          return F.NIL;
        }

        IInteger x = (IInteger) quotient;
        integerTerms.append(x);
        p = x.multiply(q).subtract(p);
        q = d.subtract(p.multiply(p)).quotient(q);
        key = F.list(p, q);
      } while (!pqPeriodic2Index.containsKey(key));

      int i = pqPeriodic2Index.get(key);

      IAST tempList = integerTerms;
      if (negate) {
        tempList = tempList.map(x -> x.negate());
      }

      if (maxIterations < Integer.MAX_VALUE && maxIterations > 0) {
        IASTAppendable resultList = F.ListAlloc(maxIterations);
        for (int j = 1; j < i + 1; j++) {
          resultList.append(tempList.get(j));
          if (--maxIterations == 0) {
            return resultList;
          }
        }
        IAST periodic = tempList.copyFrom(i + 1);
        while (true) {
          for (int j = 1; j < periodic.size(); j++) {
            resultList.append(periodic.get(j));
            if (--maxIterations == 0) {
              return resultList;
            }
          }
        }
      }
      IASTAppendable resultList = F.ListAlloc(i + 1);
      resultList.appendAll(tempList, 1, i + 1);
      resultList.append(tempList.copyFrom(i + 1));
      return resultList;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isComplex() || arg1.isComplexNumeric()) {
        // The value `1` is not a real number.
        return Errors.printMessage(S.ContinuedFraction, "realx", F.list(arg1), engine);
      }

      int maxIterations = Integer.MAX_VALUE;
      if (ast.isAST2()) {
        if (ast.arg2().isNumber()) {
          maxIterations = ast.arg2().toIntDefault();
          if (maxIterations <= 0) {
            // Positive integer (less equal 2147483647) expected at position `2` in `1`.
            return Errors.printMessage(S.ContinuedFraction, "intpm", F.list(ast, F.C2), engine);
          }
        } else {
          return F.NIL;
        }
      }

      IAST list4 = quadraticIrrational(ast.arg1());
      if (list4.isPresent()) {
        return continuedFractionPeriodic((IInteger) list4.arg1(), (IInteger) list4.arg2(),
            (IInteger) list4.arg3(), (IInteger) list4.arg4(), false, maxIterations, engine);
      }

      if (arg1 instanceof INum) {
        // arg1 = F.fraction(((INum) arg1).getRealPart());
        return realToContinuedFraction(((INum) arg1), maxIterations, engine);
      } else if (arg1.isAST() || arg1.isSymbol() && arg1.isNumericFunction(true)) {
        IExpr num = engine.evalN(arg1);
        if (num instanceof INum) {
          return realToContinuedFraction(((INum) num), maxIterations, engine);
        }
      }

      if (arg1.isRational()) {
        IRational rat = (IRational) arg1;
        BigInteger numerator = rat.toBigNumerator();
        BigInteger denominator = rat.toBigDenominator();
        boolean isNegative = rat.isNegative();
        if (isNegative) {
          numerator = numerator.negate();
        }
        return rationalToContinuedFraction(numerator, denominator, isNegative, maxIterations,
            false);
      }

      return F.NIL;
    }

    private static IAST realToContinuedFraction(INum value, int iterationLimit, EvalEngine engine) {
      double doubleValue = value.getRealPart();
      if (value.isNumIntValue()) {
        return F.list(F.ZZ((int) Math.rint(doubleValue)));
      }
      boolean isNegative = doubleValue < 0;
      if (isNegative) {
        doubleValue = Math.abs(doubleValue);
      }
      BigFraction bigFraction = new BigFraction(doubleValue);
      return rationalToContinuedFraction(bigFraction.getNumerator(), bigFraction.getDenominator(),
          isNegative, iterationLimit, true);
    }

    private static IAST rationalToContinuedFraction(BigInteger numerator, BigInteger denominator,
        boolean isNegative, int iterationLimit, boolean checkNumericZero) {
      IASTAppendable continuedFractionList;
      if (denominator.equals(BigInteger.ONE)) {
        continuedFractionList = F.ListAlloc(1);
        continuedFractionList.append(isNegative ? F.ZZ(numerator.negate()) : F.ZZ(numerator));
      } else if (numerator.equals(BigInteger.ONE)) {
        continuedFractionList = F.ListAlloc(2);
        continuedFractionList.append(F.C0);
        continuedFractionList.append(isNegative ? F.ZZ(denominator.negate()) : F.ZZ(denominator));
      } else {
        continuedFractionList = F.ListAlloc(10);
        while (denominator.compareTo(BigInteger.ONE) > 0 && (0 < iterationLimit--)) {
          BigInteger[] divideAndRemainder = numerator.divideAndRemainder(denominator);
          continuedFractionList.append(
              isNegative ? F.ZZ(divideAndRemainder[0].negate()) : F.ZZ(divideAndRemainder[0]));
          numerator = denominator;
          denominator = divideAndRemainder[1];
          if (denominator.equals(BigInteger.ONE)) {
            continuedFractionList.append(isNegative ? F.ZZ(numerator.negate()) : F.ZZ(numerator));
          }
          if (checkNumericZero && F.isZero(denominator.doubleValue() / numerator.doubleValue(),
              Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            break;
          }
        }
      }
      return continuedFractionList;
    }

    // private static IAST realToContinuedFraction(INum value, int iterationLimit,
    // EvalEngine engine) {
    // final double doubleValue = value.getRealPart();
    // if (value.isNumIntValue()) {
    // return F.list(F.ZZ((int) Math.rint(doubleValue)));
    // }
    // BigFraction bigFraction = new BigFraction(doubleValue);
    // System.out.println(bigFraction.toString());
    // // int ip = (int) doubleValue;
    // IASTAppendable continuedFractionList =
    // F.ListAlloc(iterationLimit > 0 && iterationLimit < 1000 ? iterationLimit + 10 : 100);
    // int aNow = (int) doubleValue;
    // double tNow = doubleValue - aNow;
    // double tNext;
    // int aNext;
    // continuedFractionList.append(aNow);
    // for (int i = 0; i < iterationLimit - 1; i++) {
    // if (i >= 99) {
    // LOGGER.log(engine.getLogLevel(),
    // "ContinuedFraction: calculations of double number values require a iteration limit less equal
    // 100.");
    // return F.NIL;
    // }
    // double rec = 1.0 / tNow;
    // aNext = (int) rec;
    // if (aNext == Integer.MAX_VALUE) {
    // break;
    // }
    // tNext = rec - aNext;
    //
    // continuedFractionList.append(aNext);
    // tNow = tNext;
    // }
    // return continuedFractionList;
    // }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * CoprimeQ(x, y)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * tests whether <code>x</code> and <code>y</code> are coprime by computing their greatest common
   * divisor.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Coprime">Wikipedia - Coprime</a>
   * </ul>
   *
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
     * The integers a and b are said to be <i>coprime</i> or <i>relatively prime</i> if they have no
     * common factor other than 1.
     *
     * <p>
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
            if (!S.GCD.of(engine, expr, ast.get(j)).isOne()) {
              return S.False;
            }
          }
        }
        return S.True;
      }
      return S.False;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class DedekindNumber extends AbstractTrigArg1 {

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger() && arg1.isNonNegativeResult()) {
        int n = ((IInteger) arg1).toIntDefault();
        if (n >= 0) {
          if (n >= NumberTheory.DEDEKIND_7.length) {
            int restIndex = n - 7;
            if (restIndex >= NumberTheory.DEDEKIND_REST.length) {
              return F.NIL;
            }
            return NumberTheory.DEDEKIND_REST[restIndex];
          }
          return F.ZZ(NumberTheory.DEDEKIND_7[n]);
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * DiracDelta(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * <code>DiracDelta</code> function returns <code>0</code> for all real numbers <code>x</code>
   * where <code>x != 0</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; DiracDelta(-42)
   * 0
   * </pre>
   *
   * <p>
   * <code>DiracDelta</code> doesn't evaluate for <code>0</code>:
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
          IReal temp = expr.evalReal();
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
            if (result.isNIL()) {
              result = F.ast(S.DiracDelta);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Divisible(n, m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>n</code> could be divide by <code>m</code>.
   *
   * </blockquote>
   *
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
        return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
      }

      IExpr result = engine.evaluate(F.Divide(ast.arg1(), ast.arg2()));
      if (result.isNumber()) {
        if (result.isComplex()) {
          IComplex comp = (IComplex) result;
          if (isRealDivisible(comp.re()).isTrue() && isRealDivisible(comp.im()).isTrue()) {
            return S.True;
          }
          return S.False;
        }
        if (result.isReal()) {
          return isRealDivisible((IReal) result);
        }
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    /**
     * Return S.True or S.False if result is divisible. Return <code>F.NIL</code>, if the result
     * could not be determined.
     *
     * @param result
     * @return
     */
    private IExpr isRealDivisible(IReal result) {
      if (result.isInteger()) {
        return S.True;
      }
      if (result.isNumIntValue()) {
        // return S.True;
        try {
          result.toLong();
          return S.True;
        } catch (ArithmeticException ae) {
          return F.NIL;
        }
      }
      return S.False;
    }
  }

  /**
   *
   *
   * <pre>
   * Divisors(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns all integers that divide the integer <code>n</code>.
   *
   * </blockquote>
   *
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
      if (arg1.isInteger() && !arg1.isZero()) {
        IInteger i = (IInteger) arg1;
        if (i.isNegative()) {
          i = i.negate();
        }
        return i.divisors();
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
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
              if (condition.isPresent() && !engine.evalTrue(condition, divisor)) {
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * DivisorSigma(k, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the sum of the <code>k</code>-th powers of the divisors of <code>n</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Divisor_function">Wikipedia - Divisor function</a>
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
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
              sum = sum.add(((IInteger) list.get(i)).powerRational(kl));
            }
            return sum;
          } catch (ArithmeticException ae) {
            //
          }
        }
        // general formula
        IASTAppendable sum = F.PlusAlloc(size);
        return sum.appendArgs(size, i -> F.Power(list.get(i), arg1));
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * EulerE(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the euler number <code>En</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Euler_number">Wikipedia - Euler number</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EulerE(6)
   * -61
   * </pre>
   */
  private static class EulerE extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        IExpr z = ast.arg2();
        if (z.isNumEqualRational(F.C1D2)) {
          // EulerE(n)/2^(n)
          return F.Times(F.EulerE(arg1), F.Power(F.C2, F.Negate(arg1)));
        }
        if (arg1.isMathematicalIntegerNonNegative()) {
          try {
            int nMax = arg1.toIntDefault();
            IExpr n = arg1;
            ArrayList<IInteger> eulerE = eulerEList(nMax);
            // https://functions.wolfram.com/Polynomials/EulerE2/27/01/0001/
            // Sum((Binomial(n,k)*EulerE(k))/(2^k*(-1/2+z)^(k-n)),{k,0,n})
            return F.sum(k -> //
            F.Times(F.Power(F.Power(F.C2, k), F.CN1),
                F.Power(F.Plus(F.CN1D2, z), F.Plus(F.Negate(k), n)), F.Binomial(n, k),
                getEulerEEvenOdd(eulerE, k.toIntDefault())), //
                0, nMax, 1, //
                true);
          } catch (IllegalArgumentException iae) {
            return Errors.printMessage(S.EulerE, iae, engine);
          }
        }
        return F.NIL;
      }
      if (arg1.isInteger()) {
        int n = ((IInteger) arg1).toIntDefault(-1);
        if (n >= 0) {
          if ((n & 0x00000001) == 0x00000001) {
            return F.C0;
          }
          n /= 2;

          // The list of all Euler numbers as a vector, n=0,2,4,....
          ArrayList<IInteger> a = eulerEList(n);

          IInteger eulerE = eulerEGet(a, n);
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
     * Get the Euler number.
     * 
     * @param eulerEList the list of all Euler numbers as a vector for the even integer indices
     *        <code>n=0,2,4,....</code>
     * @param n
     * @return the Euler number <code>E(n)</code>
     */
    private static IInteger getEulerEEvenOdd(ArrayList<IInteger> eulerEList, int n) {
      if ((n & 0x00000001) == 0x00000001) {
        return F.C0;
      }
      n /= 2;
      IInteger eulerE = eulerEList.get(n);
      if (n > 0) {
        n -= 1;
        n %= 2;
        if (n == 0) {
          eulerE = eulerE.negate();
        }
      }
      return eulerE;
    }

    private static ArrayList<IInteger> eulerEList(int n) {
      ArrayList<IInteger> a = new ArrayList<IInteger>();
      a.add(F.C1);
      a.add(F.C1);
      a.add(F.C5);
      a.add(AbstractIntegerSym.valueOf(61));
      set(a, n);
      return a;
    }

    /**
     * Compute a coefficient in the internal table.
     *
     * @param a list of integers
     * @param n the zero-based index of the coefficient. n=0 for the E_0 term.
     */
    protected static void set(List<IInteger> a, final int n) {
      while (n >= a.size()) {
        IInteger val = F.C0;
        boolean sigPos = true;
        int thisn = a.size();
        for (int i = thisn - 1; i > 0; i--) {
          IInteger f = a.get(i);
          f = f.multiply(AbstractIntegerSym.valueOf(BigIntegerMath.binomial(2 * thisn, 2 * i)));
          val = sigPos ? val.add(f) : val.subtract(f);
          sigPos = !sigPos;
        }
        if (thisn % 2 == 0) {
          val = val.subtract(F.C1);
        } else {
          val = val.add(F.C1);
        }
        a.add(val);
      }
    }

    /**
     * The positive Euler number at the index provided.
     *
     * @param a list of integers
     * @param n the index, non-negative.
     * @return the E_0=1, E_1=1 , E_2=5, E_3=61 etc
     */
    public static IInteger eulerEGet(List<IInteger> a, int n) {
      return a.get(n);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * EulerPhi(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute Euler's totient function.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href= "http://en.wikipedia.org/wiki/Euler%27s_totient_function">Wikipedia - Euler's
   * totient function</a>
   * </ul>
   *
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
        if (S.True.equals(AbstractAssumptions.assumePrime(arg1))) {
          // fungrim "cb410e"
          return F.Plus(F.CN1, arg1);
        }
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * ExtendedGCD(n1, n2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the extended greatest common divisor of the given integers.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm">Wikipedia: Extended
   * Euclidean algorithm</a>
   * <li><a href= "https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Wikipedia: Bézout's
   * identity</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ExtendedGCD(10, 15)
   * {5,{-1,1}}
   * </pre>
   *
   * <p>
   * <code>ExtendedGCD</code> works with any number of arguments:
   *
   * <pre>
   * &gt;&gt; ExtendedGCD(10, 15, 7)
   * {1,{-3,3,-2}}
   * </pre>
   *
   * <p>
   * Compute the greatest common divisor and check the result:
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
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Extended_Euclidean_algorithm">Extended Euclidean
     * algorithm</a> and See
     * <a href="http://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Bézout's identity</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
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
        IASTAppendable subList =
            F.mapRange(0, bezoutCoefficients.length, i -> F.ZZ(bezoutCoefficients[i]));
        return F.list(F.ZZ(gcd), subList);
      } catch (ArithmeticException ae) {
        Errors.printMessage(S.ExtendedGCD, ae, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    /**
     * Calculate the extended GCD
     *
     * @param gcdArgs an array of positive BigInteger numbers
     * @param bezoutsCoefficients returns the Bezout Coefficients
     * @return
     */
    public static BigInteger extendedGCD(final BigInteger[] gcdArgs,
        BigInteger[] bezoutsCoefficients) {
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

    /** Returns the gcd of two positive numbers plus the bezout relation */
    public static Object[] extendedGCD(BigInteger numberOne, BigInteger numberTwo)
        throws ArithmeticException {
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
      if ((!((numberOne.signum() == 0) || (numberTwo.signum() == 0)))//
          && (((numberOne.signum() > 0) && (numberTwo.signum() > 0)))) {
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
        while (remainder.signum() != 0) {
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Factorial(n)
   *
   * n!
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the factorial number of the integer <code>n</code>
   *
   * </blockquote>
   *
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
    public IExpr e1ComplexArg(Complex c) {
      return F.complexNum(Arithmetic.lanczosApproxGamma(c.add(1.0)));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex c) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.complexNum(h.gamma(h.add(c, Apfloat.ONE)));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat d) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.num(h.gamma(h.add(d, Apfloat.ONE)));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      double d = org.hipparchus.special.Gamma.gamma(arg1 + 1.0);
      return F.num(d);
    }

    /**
     * Returns the factorial of an integer n
     *
     * <p>
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
        IFraction frac = (IFraction) arg1;
        if (arg1.equals(F.C1D2)) {
          return F.Times(F.C1D2, F.Sqrt(S.Pi));
        }
        if (arg1.equals(F.CN1D2)) {
          return F.Sqrt(S.Pi);
        }

        if (frac.denominator().equals(F.C2)) {
          return F.Gamma(frac.inc());
        }
      }
      if (arg1.isInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isNegativeInfinity()) {
        return S.Indeterminate;
      }
      if (arg1.isDirectedInfinity()) {
        if (arg1.isComplexInfinity()) {
          return S.Indeterminate;
        }
        if (arg1.isAST1()) {
          if (arg1.first().equals(F.CI) || arg1.first().equals(F.CNI)) {
            return F.C0;
          }
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class FactorialPower extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr n = ast.arg2();
      if (ast.isAST2()) {
        if (x.isInteger() && n.isInteger() && x.isNonNegativeResult() && n.isNonNegativeResult()) {
          if (((IInteger) x).isLT((IInteger) n)) {
            return F.C0;
          }
          if (((IInteger) x).equals(n)) {
            if (x.isZero()) {
              return F.C1;
            }
            return x;
          }
        }
      }
      int ni = n.toIntDefault();
      if (ni >= 0) {
        if (Config.MAX_AST_SIZE < ni) {
          ASTElementLimitExceeded.throwIt(ni);
        }
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit <= ni) {
          IterationLimitExceeded.throwIt(ni, ast);
        }
      } else {
        if (n.isInteger()) {
          return F.NIL;
        }
      }

      if (ast.isAST2()) {
        IExpr result = F.C1;

        // x*(x-1)*(x-(n-1))
        if (engine.evalLess(n, F.C0)) {
          return F.NIL;
        } else if (n.isZero()) {
          return F.C1;
        } else if (n.isOne()) {
          return x;
        } else {
          if (engine.isDoubleMode()) {
            if (!x.isMathematicalIntegerNegative()) {
              Complex cx = x.evalfc();
              Complex cn = n.evalfc();
              cx = cx.add(1.0);
              cn = cx.subtract(cn);
              if (!(cn.isMathematicalInteger() && cn.getReal() < 0.0)) {
                // Gamma(1+x)/Gamma(1-n+x)
                return F.Divide(F.Gamma(F.complexNum(cx)), F.Gamma(F.complexNum(cn)));
              }
            }
            double real = Double.NaN;
            try {
              real = x.evalf();
            } catch (ArgumentTypeException ate) {
              Complex temp = x.evalfc();
              if (temp == null) {
                return F.NIL;
              }
              real = temp.getReal();
            }
            if (Double.isNaN(real)) {
              return F.NIL;
            }
            long iterationLimit = EvalEngine.get().getIterationLimit();
            long k = 0L;
            double dN = n.evalf();
            double i = real - dN + 1;
            while (real >= i) {
              result = result.multiply(x);
              x = x.dec();
              real--;
              if (k++ > iterationLimit && iterationLimit > 0) {
                IterationLimitExceeded.throwIt(k, S.FactorialPower);
              }
            }
            return result;
          } else if (x.isExactNumber() && n.isRational()) {
            IRational real = (IRational) ((INumber) x).re();
            IRational dN = (IRational) n;
            IRational i = real.subtract(dN).inc();
            while (real.isGE(i)) {
              result = result.multiply(x);
              x = x.dec();
              real = real.dec();
            }
            return result;
          }
        }
        return F.NIL;
      }

      if (ast.isAST3()) {
        IExpr result = F.C1;
        IExpr h = ast.arg3();
        if (h.isZero()) {
          return F.Power(x, n);
        }
        // x*(x-h)* (x-(n-1)*h)
        if (engine.evalTrue(F.Less(n, F.C0))) {
          return F.NIL;
        } else if (n.isZero()) {
          return F.C1;
        } else if (n.isOne()) {
          return x;
        } else {
          if (engine.isDoubleMode()) {
            if (!x.isMathematicalIntegerNegative()) {
              Complex cx = x.evalfc();
              Complex cn = n.evalfc();
              Complex ch = h.evalfc();

              Complex cxDch = cx.divide(ch);
              Complex cxDchPlus1 = cxDch.add(1.0);
              Complex cxDchPlus1SubtractN = cxDchPlus1.subtract(cn);
              Complex factor = cx.pow(cn).divide(cxDch.pow(cn));
              if (!(cxDchPlus1.isMathematicalInteger() && cxDchPlus1.getReal() < 0.0)) {
                // (x^n*Gamma(1+x/h))/((x/h)^n*Gamma(1-n+x/h))
                return F.Divide(F.Times(F.complexNum(factor), F.Gamma(F.complexNum(cxDchPlus1))),
                    F.Gamma(F.complexNum(cxDchPlus1SubtractN)));
              }
            }
            double real = Double.NaN;
            try {
              real = x.evalf();
            } catch (ArgumentTypeException ate) {
              Complex temp = x.evalfc();
              if (temp == null) {
                return F.NIL;
              }
              real = temp.getReal();
            }
            if (Double.isNaN(real)) {
              return F.NIL;
            }
            double dN = n.evalf();
            double doubleH = h.evalf();
            if (h.isZero()) {
              while (engine.evalTrue(F.Greater(n, F.C0))) {
                result = result.multiply(x);
                n = n.dec();
              }
              return result;
            } else if (engine.evalTrue(F.Greater(h, F.C0))) {
              long iterationLimit = EvalEngine.get().getIterationLimit();
              long k = 0L;
              double i = real - (dN - 1) * doubleH;
              while (real >= i) {
                result = result.multiply(x);
                x = x.minus(h);
                real -= doubleH;
                if (k++ > iterationLimit && iterationLimit > 0) {
                  IterationLimitExceeded.throwIt(k, S.FactorialPower);
                }
              }
              return result;
            } else {
              long iterationLimit = EvalEngine.get().getIterationLimit();
              long k = 0L;
              double i = real - (dN - 1) * doubleH;
              while (real <= i) {
                result = result.multiply(x);
                x = x.minus(h);
                real -= doubleH;
                if (k++ > iterationLimit && iterationLimit > 0) {
                  IterationLimitExceeded.throwIt(k, S.FactorialPower);
                }
              }
              return result;
            }

          } else if (x.isExactNumber() && n.isRational() && h.isRational()) {
            IRational real = (IRational) ((INumber) x).re();
            IRational dN = (IRational) n;
            IRational H = (IRational) h;
            if (H.isZero()) {
              while (dN.isGT(F.C0)) {
                result = result.multiply(x);
                dN = dN.dec();
              }
              return result;
            }
            if (H.isGT(F.C0)) {
              IRational i = real.subtract(dN.dec().multiply(H));
              while (real.isGE(i)) {
                result = result.multiply(x);
                x = x.minus(H);
                real = real.subtract(H);
              }
              return result;
            }
            IRational i = real.subtract(dN.dec().multiply(H));
            while (real.isLE(i)) {
              result = result.multiply(x);
              x = x.minus(H);
              real = real.subtract(H);
            }
            return result;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Factorial2(n)
   *
   * n!!
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the double factorial number of the integer <code>n</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "http://en.wikipedia.org/wiki/Factorial#Double_factorial">Wikipedia - Double
   * Factorial</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Factorial2(3)
   * 3
   * </pre>
   */
  private static class Factorial2 extends AbstractEvaluator {

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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
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
        return S.Indeterminate;
      }
      if (engine.isNumericMode() && arg1.isNumber()) {
        IExpr temp = S.FunctionExpand.ofNIL(engine, F.Unevaluated(F.Factorial2(arg1)));
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * FactorInteger(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the factorization of <code>n</code> as a list of factors and exponents.
   *
   * </blockquote>
   *
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
   *
   * <p>
   * To get back the original number:
   *
   * <pre>
   * &gt;&gt; Times @@ Power @@@ factors
   * 2010
   * </pre>
   *
   * <p>
   * <code>FactorInteger</code> factors rationals using negative exponents:
   *
   * <pre>
   * &gt;&gt; FactorInteger(2010 / 2011)
   * {{2, 1}, {3, 1}, {5, 1}, {67, 1}, {2011, -1}}
   * </pre>
   */
  public static class FactorInteger extends AbstractEvaluator {

    @Override
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
          IExpr option = options.getOption(S.GaussianIntegers);
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
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Fibonacci(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Fibonacci number of the integer <code>n</code>
   *
   * </blockquote>
   *
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
     * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F See:
     * <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code:
     * Fibonacci sequence.</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = ((IInteger) arg1).toIntDefault();
        if (n > Integer.MIN_VALUE) {
          if (ast.isAST2()) {
            if (ast.arg2().isSymbol() || ast.arg2().isNumber() || ast.arg2().isAST()) {
              return fibonacciPolynomialIterative(n, ast.arg2(), ast, engine);
            }
            return F.NIL;
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
              F.Plus(
                  F.Times(F.Power(F.Power(F.C2, n), F.CN1),
                      F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n)),
                  F.Times(F.CN1, F.Power(F.C2, n),
                      F.Power(F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n), F.CN1),
                      F.Cos(F.Times(n, S.Pi))))); // $$;
        }
        return
        // [$ ( GoldenRatio^n - Cos(Pi*n) * GoldenRatio^(-n) ) / Sqrt(5) $]
        F.Times(F.C1DSqrt5, F.Plus(F.Power(S.GoldenRatio, n),
            F.Times(F.CN1, F.Power(S.GoldenRatio, F.Negate(n)), F.Cos(F.Times(S.Pi, n))))); // $$;
      }
      return F.NIL;
    }

    /**
     * Create Fibonacci polynomial with iteration.
     *
     * @param n an integer <code>n >= 0</code>
     * @param x the variable expression of the polynomial
     * @return
     */
    public IExpr fibonacciPolynomialIterative(int n, IExpr x, IAST ast, final EvalEngine engine) {
      int iArg = n;
      if (n < 0) {
        n *= (-1);
      }
      if (n > Config.MAX_POLYNOMIAL_DEGREE) {
        PolynomialDegreeLimitExceeded.throwIt(n);
      }
      IExpr previousFibonacci = F.C0;
      IExpr fibonacci = F.C1;
      if (n == 0) {
        return previousFibonacci;
      }
      if (n == 1) {
        return fibonacci;
      }

      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= n) {
        IterationLimitExceeded.throwIt(n, ast);
      }
      for (int i = 1; i < n; i++) {
        IExpr temp = fibonacci;
        if (fibonacci.isPlus()) {
          fibonacci = fibonacci.mapThread(F.Times(x, F.Slot1), 2);
        } else {
          fibonacci = F.Times(x, fibonacci);
        }
        fibonacci = S.Expand.of(engine, F.Plus(fibonacci, previousFibonacci));
        previousFibonacci = temp;
      }
      if (iArg < 0 && ((iArg & 0x00000001) == 0x00000000)) {
        return F.Negate(fibonacci);
      }

      return fibonacci;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class FindLinearRecurrence extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isList()) {
          ISeqBase seq = Sequences.sequence(arg1);
          return seq.find_linear_recurrence(arg1.argSize());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }
  /**
   *
   *
   * <pre>
   * FrobeniusNumber({a1, ... ,aN})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Frobenius number of the nonnegative integers <code>{a1, ... ,aN}</code>
   *
   * </blockquote>
   *
   * <p>
   * The Frobenius problem, also known as the postage-stamp problem or the money-changing problem,
   * is an integer programming problem that seeks nonnegative integer solutions to <code>
   * x1*a1 + ... + xN*aN = M</code> where <code>ai</code> and <code>M</code> are positive integers.
   * In particular, the Frobenius number <code>FrobeniusNumber({a1, ... ,aN})</code>, is the largest
   * <code>M</code> so that this equation fails to have a solution.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Coin_problem">Wikipedia - Coin problem</a>
   * </ul>
   *
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * FromContinuedFraction({n1, n2, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the number which represents the continued fraction list <code>{n1, n2, ...}</code>.
   *
   * </blockquote>
   *
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
  private static final class FromContinuedFraction extends AbstractEvaluator {

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
          try {
            IExpr period = list.last();
            if (period.isNonEmptyList()) {
              if (!((IAST) period).forAll(x -> x.isInteger())) {
                // Unable to determine the appropriate root for the periodic continued fraction.
                return Errors.printMessage(S.FromContinuedFraction, "root", F.CEmptyList, engine);
              }
              boolean nonNegative = ((IAST) period).forAll(x -> x.isNonNegativeResult());

              ISymbol y = F.Dummy();
              IASTAppendable periodicPart = ((IAST) period).copyAppendable(1);
              periodicPart.append(y);

              IExpr periodReduced = continuedFractionReduce(periodicPart, engine);
              if (periodReduced.isPresent()) {
                IExpr[] solutions = F.solve(F.Equal(F.Subtract(periodReduced, y), F.C0), y);
                if (solutions.length > 0) {
                  IExpr solution = nonNegative ? solutions[solutions.length - 1] : solutions[0];
                  ISymbol x = F.Dummy();
                  IASTMutable nonPeriodicPart = list.setAtCopy(list.argSize(), x);
                  IExpr nonPeriodReduced = continuedFractionReduce(nonPeriodicPart, engine);
                  if (nonPeriodReduced.isPresent()) {
                    return radSimplify(
                        F.subst(nonPeriodReduced, arg -> arg.equals(x) ? solution : F.NIL), engine);
                  }
                }
              }
              // Unable to determine the appropriate root for the periodic continued fraction.
              return Errors.printMessage(S.FromContinuedFraction, "root", F.CEmptyList, engine);
            }

            return continuedFractionReduce(list, engine);
          } catch (ArithmeticException aex) {
            // java.lang.ArithmeticException: Inverse root of zero
            // at org.apfloat.ApfloatMath.inverseRoot(ApfloatMath.java:280)
            Errors.printMessage(S.FromContinuedFraction, aex, engine);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Rationalize the denominator of the <code>expr</code> by removing square roots. This method
     * handles only very simple cases.
     *
     * <p>
     * <b>Note:</b> the expression returned from <code>radSimplify</code> must be used with caution
     * since if the denominator contains symbols, it will be possible to make substitutions that
     * violate the assumptions of the simplification process: that for a denominator matching
     * <code>a + b*sqrt(c), a != +/-b*sqrt(c)</code>.
     *
     * @param expr the expression which denominator should be rationalized
     * @param engine
     * @return
     */
    private static IExpr radSimplify(IExpr expr, EvalEngine engine) {
      expr = S.Together.of(engine, expr);
      IExpr numerator = S.Numerator.of(engine, expr);
      IExpr denominator = S.Expand.of(engine, F.Denominator(expr));
      if (!denominator.isFree(x -> x.isSqrt(), false)) {
        if (denominator.isPlus2()) {
          IASTMutable plus = ((IAST) denominator).setAtCopy(2, denominator.second().negate());
          IExpr squared = S.Expand.of(plus.times(denominator));
          IExpr newNumerator = S.Expand.of(plus.times(numerator));
          expr = F.Times(newNumerator, F.Power(squared, F.CN1));
        } else if (denominator.isTimes() || denominator.isSqrt()) {
          IAST timesAST = ((IAST) denominator);
          IExpr squared = timesAST.times(timesAST);
          expr = F.Times(numerator, timesAST, F.Power(squared, F.CN1));
        }
      }
      return S.Simplify.of(engine, expr);
    }

    /**
     * Reduce a continued fraction to a rational or quadratic irrational.
     *
     * <p>
     * Compute the rational or quadratic irrational number from its terminating or periodic
     * continued fraction expansion.
     *
     * @param continuedFractionList the list of integers
     * @param engine
     * @return
     */
    private static IExpr continuedFractionReduce(IAST continuedFractionList, EvalEngine engine) {
      try {
        int size = continuedFractionList.argSize();
        if (continuedFractionList.forAll(x -> x.isReal())) {
          IExpr result = continuedFractionList.get(size--);
          for (int i = size; i >= 1; i--) {
            result = continuedFractionList.get(i).plus(result.power(-1));
          }
          return result;
        }
        IExpr result = continuedFractionList.get(size--);
        for (int i = size; i >= 1; i--) {
          result = F.Plus(continuedFractionList.get(i), F.Power(result, F.CN1));
        }
        return result;
      } catch (ValidateException ve) {
        Errors.printMessage(S.FromContinuedFraction, ve, engine);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Hyperfactorial extends AbstractEvaluator {

    private static IExpr hyperfactorial(int n) {
      if (n >= 0) {
        BigInteger result = BigInteger.ONE;
        for (int k = 2; k <= n; k++) {
          result = result.multiply(BigInteger.valueOf(k).pow(k));
          if (result.bitLength() > Config.MAX_AST_SIZE * 8) {
            IterationLimitExceeded.throwIt(result.bitLength(), F.Hyperfactorial(F.ZZ(n)));
          }
        }
        return F.ZZ(result);
      }
      return F.NIL;
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = ((IInteger) arg1).toIntDefault(0);
        if (-8 <= n && n <= 7) {
          // long integer results
          switch (n) {
            case 0:
              return F.C1;
            case 1:
              return F.C1;
            case 2:
              return F.C4;
            case 3:
              return F.ZZ(108);
            case 4:
              return F.ZZ(27648);
            case 5:
              return F.ZZ(86400000);
            case 6:
              return F.ZZ(4031078400000L);
            case 7:
              return F.ZZ(3319766398771200000L);
            case -1:
              return F.C1;
            case -2:
              return F.CN1;
            case -3:
              return F.CN4;
            case -4:
              return F.ZZ(108);
            case -5:
              return F.ZZ(27648);
            case -6:
              return F.ZZ(-86400000);
            case -7:
              return F.ZZ(-4031078400000L);
            case -8:
              return F.ZZ(3319766398771200000L);
          }
        }

        if (n > 0) {
          return hyperfactorial(n);
        }
      }
      if (arg1.isRationalValue(F.CN1D2)) {
        return F.Divide(F.Power(S.Glaisher, F.C3D2), //
            F.Times(F.Power(F.C2, F.QQ(1, 24)), F.Exp(F.QQ(1, 8))));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * JacobiSymbol(m, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculates the Jacobi symbol.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a>
   * </ul>
   *
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
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
        if (arg3.isInteger()) {
          int n = arg3.toIntDefault(-1);
          if (n <= 0) {
            // Positive integer expected at position `2` in `1`.
            return Errors.printMessage(S.LinearRecurrence, "intp", F.List(ast, F.C3), engine);
          }
          return linearRecurrence(list1, list2, n, ast, engine);
        }
        if (arg3.isList() && arg3.size() == 2 && arg3.first().isReal()) {
          int n = arg3.first().toIntDefault();
          if (n != Integer.MIN_VALUE) {
            if (n < 0) {
              // Positive integer expected at position `2` in `1`.
              return Errors.printMessage(S.LinearRecurrence, "intp", F.List(arg3, F.C1), engine);
            }
            IAST result = linearRecurrence(list1, list2, n, ast, engine);
            if (result.isPresent()) {
              return result.get(n);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    private IAST linearRecurrence(IAST list1, IAST list2, int n, IAST ast, EvalEngine engine) {
      if (n < 0) {
        return F.NIL;
      }
      int size1 = list1.size();
      int size2 = list2.size();
      if (size2 >= size1) {
        int counter = 0;
        if (Config.MAX_AST_SIZE < n) {
          ASTElementLimitExceeded.throwIt(n);
        }
        int loopCounter = n;
        if (size1 > 0) {
          loopCounter = n * size1;
        }

        int iterationLimit = engine.getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= loopCounter) {
          IterationLimitExceeded.throwIt(loopCounter, ast);
        }
        IASTAppendable result = F.ListAlloc(n);
        int start = size2 - size1 + 1;
        boolean isNumber = true;
        for (int i = start; i < list2.size(); i++) {
          IExpr x = list2.get(i);
          if (!x.isNumber()) {
            isNumber = false;
          }
          result.append(x);
          if (++counter == n) {
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
          long numberOfLeaves = n;
          while (counter < n) {
            int size = result.size();
            IASTAppendable plusAST = F.PlusAlloc(size1);
            int k = size - 1;
            for (int i = 1; i < size1; i++) {
              plusAST.append(F.Times(list1.get(i), result.get(k--)));
            }
            IExpr temp = engine.evaluate(F.Expand(plusAST));
            numberOfLeaves += temp.leafCount();
            if (numberOfLeaves >= Config.MAX_AST_SIZE) {
              ASTElementLimitExceeded.throwIt(numberOfLeaves);
            }
            result.append(temp);
            counter++;
          }
        }
        return result;
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class LiouvilleLambda extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isOne()) {
        return F.C1;
      }
      if (arg1.isInteger() && arg1.isPositive()) {
        IExpr expr = S.FactorInteger.of(engine, arg1);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * Lucas number. See: <a href= "https://en.wikipedia.org/wiki/Lucas_number">Wikipedia: Lucas
   * number</a>
   */
  private static class LucasL extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = ((IInteger) arg1).toIntDefault();
        if (n > Integer.MIN_VALUE) {
          if (ast.isAST2()) {
            return lucasLPolynomialIterative(n, ast.arg2(), ast, engine);
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
          F.Plus(F.Power(F.Plus(F.Times(F.C1D2, x),
              F.Sqrt(F.Plus(F.C1, F.Times(F.C1D4, F.Sqr(x))))), n), F
                  .Times(
                      F.Power(F.Power(F.Plus(F.Times(F.C1D2, x),
                          F.Sqrt(F.Plus(F.C1, F.Times(F.C1D4, F.Sqr(x))))), n), F.CN1),
                      F.Cos(F.Times(n, S.Pi)))); // $$;
        }
        return
        // [$ GoldenRatio^n + Cos(Pi*n) * GoldenRatio^(-n) $]
        F.Plus(F.Power(S.GoldenRatio, n),
            F.Times(F.Cos(F.Times(S.Pi, n)), F.Power(S.GoldenRatio, F.Negate(n)))); // $$;
      }
      return F.NIL;
    }

    /**
     * Create LucasL polynomial with iteration. Performs much better than recursion.
     *
     * @param n an integer <code>n >= 0</code>
     * @param x the variable expression of the polynomial
     * @return
     */
    private static IExpr lucasLPolynomialIterative(int n, IExpr x, final IAST ast,
        final EvalEngine engine) {
      int iArg = n;
      if (n < 0) {
        n *= (-1);
      }
      if (n > Config.MAX_POLYNOMIAL_DEGREE) {
        PolynomialDegreeLimitExceeded.throwIt(n);
      }
      IExpr previousLucasL = F.C2;
      IExpr lucasL = x;
      if (n == 0) {
        return previousLucasL;
      }
      if (n == 1) {
        if (iArg < 0) {
          return F.Negate(lucasL);
        }
        return lucasL;
      }

      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= n) {
        IterationLimitExceeded.throwIt(n, ast);
      }
      for (int i = 1; i < n; i++) {
        IExpr temp = lucasL;
        if (lucasL.isPlus()) {
          lucasL = lucasL.mapThread(F.Times(x, F.Slot1), 2);
        } else {
          lucasL = F.Times(x, lucasL);
        }
        lucasL = S.Expand.of(engine, F.Plus(lucasL, previousLucasL));
        previousLucasL = temp;
      }
      if (iArg < 0 && ((iArg & 0x00000001) == 0x00000001)) {
        return F.Negate(lucasL);
      }
      return lucasL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * MangoldtLambda(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the von Mangoldt function of <code>n</code>
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Von_Mangoldt_function">Wikipedia - Von Mangoldt
   * function</a>
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
      if (ast.arg1().isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isInteger()) {
        if (arg1.isZero() || arg1.isOne() || arg1.isNegative()) {
          return F.C0;
        }
        IExpr expr = S.FactorInteger.of(engine, arg1);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * MersennePrimeExponent(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>n</code>th mersenne prime exponent. <code>2^n - 1</code> must be a prime
   * number. Currently <code>0 &lt;= n &lt;= 47</code> can be computed, otherwise the function
   * returns unevaluated.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a>
   * </ul>
   *
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
          if (n > NumberTheory.MPE_51.length) {
            return F.NIL;
          }
          return F.ZZ(NumberTheory.MPE_51[n - 1]);
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * MersennePrimeExponentQ(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>2^n - 1</code> is a prime number. Currently <code>
   * 0 &lt;= n &lt;= 47</code> can be computed in reasonable time.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a>
   * </ul>
   *
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
        return S.False;
      }

      try {
        long n = ((IInteger) arg1).toLong();
        if (n <= MPE_51[MPE_51.length - 1]) {
          for (int i = 0; i < MPE_51.length; i++) {
            if (MPE_51[i] == n) {
              return S.True;
            }
          }
          return S.False;
        }
        if (n < Integer.MAX_VALUE) {
          // 2^n - 1
          BigInteger b2nm1 = BigInteger.ONE.shiftLeft((int) n).subtract(BigInteger.ONE);
          return F.booleSymbol(b2nm1.isProbablePrime(32));
        }
      } catch (ArithmeticException ae) {
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ModularInverse extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isInteger() && arg2.isInteger()) {
        if (arg2.isZero()) {
          // Nonzero integer expected at position `1` in `2`.
          return Errors.printMessage(S.ModularInverse, "intnz", F.List(F.C2, ast), engine);
        }
        java.math.BigInteger k = ((IInteger) arg1).toBigNumerator();
        java.math.BigInteger n = ((IInteger) arg2).toBigNumerator();
        try {
          if (arg2.isNegative()) {
            return F.ZZ(k.negate().modInverse(n.negate()).negate());
          }
          return F.ZZ(k.modInverse(n));
        } catch (ArithmeticException aex) {
          // `1` is not invertible modulo `2`.
          return Errors.printMessage(S.ModularInverse, "ninv", F.List(arg1, arg2), engine);
        }
      }
      // TODO add gaussian integers
      // The `1` arguments to `2` must be ordinary integers.
      return Errors.printMessage(S.ModularInverse, "minv", F.List(F.C2, S.ModularInverse), engine);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {

    }
  }


  /**
   *
   *
   * <pre>
   * MoebiusMu(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the Möbius function.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/M%C3%B6bius_function">Wikipedia - Möbius function</a>
   * </ul>
   *
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Multinomial(n1, n2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the multinomial coefficient <code>(n1+n2+...)!/(n1! n2! ...)</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Multinomial_coefficient">Wikipedia: Multinomial
   * coefficient</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Multinomial(2, 3, 4, 5)
   * 2522520
   *
   * &gt;&gt; Multinomial()
   * 1
   * </pre>
   *
   * <p>
   * <code>Multinomial(n-k, k)</code> is equivalent to <code>Binomial(n, k)</code>.
   *
   * <pre>
   * &gt;&gt; Multinomial(2, 3)
   * 10
   * </pre>
   */
  private static class Multinomial extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST(S.Multinomial, 1, Integer.MAX_VALUE - 1)) {
        int size = ast.size();
        int n = size - 1;
        IASTAppendable numerator = F.PlusAlloc(n + 1);
        numerator.append(F.C1);
        numerator.appendArgs(ast);
        IASTAppendable denominator = F.mapFunction(S.Times, ast, x -> F.Gamma(F.Plus(1, x)));
        return F.Divide(F.Gamma(numerator), denominator);
      }
      return F.NIL;
    }

    /**
     * @param ast
     * @return <code>F.NIL</code> if the ast arguments couldn't be converted to an IInteger
     */
    private static IExpr multinomial(final IAST ast) {
      IInteger[] k = new IInteger[ast.argSize()];
      for (int i = 1; i < ast.size(); i++) {
        IExpr temp = ast.get(i);
        int value = temp.toIntDefault();
        if (value != Integer.MIN_VALUE) {
          k[i - 1] = F.ZZ(value);
        } else {
          if (temp.isInteger()) {
            k[i - 1] = (IInteger) temp;
          }
          return F.NIL;
        }
      }
      IInteger multinomial = NumberTheory.multinomial(k);
      if (multinomial == null) {
        return F.NIL;
      }
      return multinomial;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() <= 2) {
        // (n) ==> 1
        return F.C1;
      }
      if (ast.isAST2()) {
        return F.Binomial(F.Plus(ast.arg1(), ast.arg2()), ast.arg2());
      }
      int position = ast.indexOf(x -> (!x.isInteger()));
      if (position < 0) {
        return multinomial(ast);
      }
      final int argSize = ast.argSize();
      if (position == argSize && !ast.last().isNumber()) {
        // recurrence: Multinomial(n1, n2, n3,..., ni, k) =>
        // Multinomial(n1+n2+n3+...+ni, k) * Multinomial(n1, n2, n3,..., ni)
        IAST reducedMultinomial = ast.removeFromEnd(argSize);
        IAST reducedPlus = reducedMultinomial.apply(S.Plus);
        IExpr multinomial = multinomial(reducedMultinomial);
        if (multinomial.isNIL()) {
          return F.Times(F.Multinomial(reducedPlus, ast.last()));
        }
        return F.Times(F.Multinomial(reducedPlus, ast.last()), multinomial);
      }
      if (engine.isNumericMode()) {
        position = ast.indexOf(x -> !x.isNumber());
        if (position < 0) {
          return functionExpand(ast, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * MultiplicativeOrder(a, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the multiplicative order <code>a</code> modulo <code>n</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia: Multiplicative
   * order</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The <a href="https://oeis.org/A023394">A023394 Prime factors of Fermat numbers</a> integer
   * sequence
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * NextPrime(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the next prime after <code>n</code>.
   *
   * </blockquote>
   *
   * <pre>
   * NextPrime(n, k)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the <code>k</code>th prime after <code>n</code>.
   *
   * </blockquote>
   *
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
        if (primeBase.signum() < 0) {
          // Non-negative integer expected.
          return Errors.printMessage(S.NextPrime, "intnn", F.CEmptyList, engine);
        }
        return F.ZZ(primeBase.nextProbablePrime());
      } else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {
        BigInteger primeBase = ((IInteger) ast.arg1()).toBigNumerator();
        if (primeBase.signum() < 0) {
          // Non-negative integer expected.
          return Errors.printMessage(S.NextPrime, "intnn", F.CEmptyList, engine);
        }
        final int n = ast.arg2().toIntDefault();
        if (n < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return Errors.printMessage(S.NextPrime, "intpm", F.list(ast, F.C2), engine);
        }

        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= n) {
          IterationLimitExceeded.throwIt(n, ast);
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * PartitionsP(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the number of unrestricted partitions of the integer <code>n</code>.
   *
   * </blockquote>
   *
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
      /** The list of all partitions as a java.util.List. */
      protected ArrayList<BigInteger> fList = new ArrayList<BigInteger>();

      /** Default constructor initializing a list of partitions up to 7. */
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
       * @param n the zero-based index into the list of partitions
       * @param capacity capacity of the list which should be ensured
       * @return the ith partition number. This is 1 if i=0 or 1, 2 if i=2 and so forth.
       */
      private BigInteger sumPartitionsP(int n, int capacity) {
        int iterationLimit = EvalEngine.get().getIterationLimit();
        long maxIterations = capacity;
        if (iterationLimit >= 0 && iterationLimit <= maxIterations) {
          IterationLimitExceeded.throwIt(capacity, F.PartitionsP(F.ZZ(n)));
        }

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
              public IExpr call() {
                return sumPartitionsP(engine, (IInteger) arg1);
              }
            });
            if (result != null) {
              return result;
            }
          } catch (UncheckedExecutionException e) {
            Throwable th = e.getCause();
            if (th instanceof LimitException) {
              throw (LimitException) th;
            }
          } catch (ExecutionException e) {
            // LOGGER.error("PartitionsP.evaluate() failed", e);
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    /**
     * @param engine
     * @param n positive integer number
     * @return
     */
    private static IExpr sumPartitionsP(EvalEngine engine, IInteger n) {
      int i = n.toIntDefault();
      if (i >= 0) {
        if (i < Integer.MAX_VALUE - 3) {
          BigIntegerPartitionsP bipp = new BigIntegerPartitionsP();
          return F.ZZ(bipp.sumPartitionsP(i, i + 3));
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * PartitionsQ(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the number of partitions of the integer <code>n</code> into distinct parts
   *
   * </blockquote>
   *
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
                public IExpr call() {
                  return partitionsQ(engine, (IInteger) arg1);
                }
              });
              if (result != null) {
                return result;
              }
            }
          } catch (UncheckedExecutionException e) {
            Throwable th = e.getCause();
            if (th instanceof LimitException) {
              throw (LimitException) th;
            }
          } catch (ExecutionException e) {
            // LOGGER.error("PartitionsQ.evaluate() failed", e);
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
      if (sum1.isNIL()) {
        return F.NIL;
      }
      IExpr sum2 = sumPartitionsQ2(engine, n);
      if (sum2.isNIL()) {
        return F.NIL;
      }
      return Plus(Times(nInverse, sum1), Times(F.CN2, nInverse, sum2)).eval(engine);
    }

    private static IExpr sumPartitionsQ1(EvalEngine engine, IInteger n) {

      int nInt = n.toIntDefault();
      if (nInt >= 0) {
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= nInt) {
          IterationLimitExceeded.throwIt(nInt, F.PartitionsQ(F.ZZ(nInt)));
        }
        IInteger sum = F.C0;
        for (int k = 1; k <= nInt; k++) {
          IExpr temp = termPartitionsQ1(engine, n, k);
          if (!temp.isInteger()) {
            return F.NIL;
          }
          sum = sum.add((IInteger) temp);
        }
        return sum;
      }
      return F.NIL;
    }

    private static IExpr termPartitionsQ1(EvalEngine engine, IInteger n, int k) {
      // DivisorSigma(1, k)*PartitionsQ(n - k)
      final IInteger k2 = F.ZZ(k);
      return Times(F.DivisorSigma(C1, k2), F.PartitionsQ(Plus(Negate(k2), n))).eval(engine);
    }

    private static IExpr sumPartitionsQ2(EvalEngine engine, IInteger n) {
      int floorND2 = n.div(F.C2).toIntDefault();
      if (floorND2 >= 0) {
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= floorND2) {
          IterationLimitExceeded.throwIt(floorND2, F.PartitionsQ(n));
        }
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
      return F.NIL;
    }

    private static IExpr termPartitionsQ2(EvalEngine engine, IInteger n, int k) {
      // DivisorSigma(1, k)*PartitionsQ(n - 2*k)
      IInteger k2 = F.ZZ(k);
      return engine
          .evaluate(Times(F.DivisorSigma(C1, k2), F.PartitionsQ(Plus(Times(F.CN2, k2), n))));
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
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
          if (n > NumberTheory.MPE_51.length) {
            return F.NIL;
          }
          if (n <= NumberTheory.PN_8.length) {
            return F.ZZ(NumberTheory.PN_8[n - 1]);
          }
          int p = NumberTheory.MPE_51[n - 1];
          if (p > Config.MAX_BIT_LENGTH) {
            BigIntegerLimitExceeded.throwIt(p);
          }
          // 2^p
          BigInteger b2p = BigInteger.ONE.shiftLeft(p);
          // 2^(p-1)
          BigInteger b2pm1 = b2p.shiftRight(1);
          return F.ZZ(b2p.subtract(BigInteger.ONE).multiply(b2pm1));
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        return S.False;
      }

      IInteger n = (IInteger) arg1;

      try {
        long value = n.toLong();
        if (value > 0 && value <= PN_8[PN_8.length - 1]) {
          for (int i = 0; i < PN_8.length; i++) {
            if (PN_8[i] == value) {
              return S.True;
            }
          }
          return S.False;
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
        return F.booleSymbol(sum.equals(n));
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * Prime(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>n</code>th prime number.
   *
   * </blockquote>
   *
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
        int nthPrime = ((IInteger) ast.arg1()).toIntDefault();
        if (nthPrime <= 0) {
          // Positive integer argument expected in `1`.
          return Errors.printMessage(ast.topHead(), "intpp", F.list(ast), engine);
        }
        if (nthPrime > 103000000) {
          // Maximum Prime limit `1` exceeded.
          return Errors.printMessage(ast.topHead(), "zzprime", F.list(ast.arg1()), engine);
        }
        try {
          return F.ZZ(Primality.prime(nthPrime));
        } catch (RuntimeException rex) {
          Errors.printMessage(S.Prime, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        x = engine.evaluate(((IReal) arg1).floorFraction());
      } else {
        IReal sn = arg1.evalReal();
        if (sn != null) {
          x = engine.evaluate(sn.floorFraction());
        }
      }

      if (x.isInteger() && x.isPositive()) {
        // TODO improve performance by caching some values?

        int maxK = ((IInteger) x).toIntDefault();
        if (maxK >= 0) {
          int result = 0;
          BigInteger max = BigInteger.valueOf(maxK);
          BigInteger temp = BigInteger.ONE;
          int iterationLimit = engine.getIterationLimit();
          if (iterationLimit >= 0 && iterationLimit < (maxK / 100)) {
            IterationLimitExceeded.throwIt(maxK, ast);
          }
          for (int i = 2; i <= maxK; i++) {
            temp = temp.nextProbablePrime();
            if (temp.compareTo(max) > 0) {
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * PrimeOmega(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the sum of the exponents of the prime factorization of <code>n</code>.
   *
   * </blockquote>
   *
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
      if (ast.arg1().isList()) {
        return arg1.mapThread(ast, 1);
      }
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
        // SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
        SortedMap<BigInteger, Integer> map =
            Config.PRIME_FACTORS.factorInteger(((IInteger) arg1).toBigNumerator());
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * PrimePowerQ(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>n</code> is a power of a prime number.
   *
   * </blockquote>
   *
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
        return F.booleSymbol(Primality.isPrimePower(((IInteger) arg1).toBigNumerator()));
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
          Errors.printMessage(S.PrimitiveRoot, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * PrimitiveRootList(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the list of the primitive roots of <code>n</code>.
   *
   * </blockquote>
   *
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
     * See:
     * <a href= "http://exploringnumbertheory.wordpress.com/2013/09/09/finding-primitive-roots/">
     * Exploring Number Theory - Finding Primitive Roots</a>
     */
    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger()) {
        try {
          IInteger[] roots = ((IInteger) arg1).primitiveRootList();
          if (roots != null) {
            return F.mapRange(0, roots.length, i -> roots[i]);
          }
        } catch (LimitException le) {
          throw le;
        } catch (RuntimeException rex) {
          Errors.printMessage(S.PrimitiveRootList, rex, engine);
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class QuadraticIrrationalQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST result = quadraticIrrational(ast.arg1());
      return F.booleSymbol(result.isPresent());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class RamseyNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int r = ast.arg1().toIntDefault();
      int s = ast.arg2().toIntDefault();
      if (r <= 0 || s <= 0) {
        return F.NIL;
      }
      if (r > s) {
        // symmetry across the diagonal: R(r, s) = R(s, r)
        return F.binary(S.RamseyNumber, ast.arg2(), ast.arg1());
      }
      // https://en.wikipedia.org/wiki/Ramsey%27s_theorem#Known_values)
      switch (r) {
        case 1:
          return F.C1;
        case 2:
          return F.ZZ(s);
        case 3:
          switch (s) {
            case 3:
              return F.C6;
            case 4:
              return F.C9;
            case 5:
              return F.ZZ(14);
            case 6:
              return F.ZZ(18);
            case 7:
              return F.ZZ(23);
            case 8:
              return F.ZZ(28);
            case 9:
              return F.ZZ(36);
          }
          break;
        case 4:
          switch (s) {
            case 4:
              return F.ZZ(18);
            case 5:
              return F.ZZ(25);
          }
          break;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * Rationalize(expression)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert numerical real or imaginary parts in (sub-)expressions into rational numbers.
   *
   * </blockquote>
   *
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
  private static final class Rationalize extends AbstractCoreFunctionEvaluator {

    static class RationalizeNumericsVisitor extends VisitorExpr {
      double epsilon;
      boolean useConvergenceMethod;

      public RationalizeNumericsVisitor(double epsilon, boolean useConvergenceMethod) {
        super();
        this.epsilon = epsilon;
        this.useConvergenceMethod = useConvergenceMethod;
      }

      @Override
      public IExpr visit(IASTMutable ast) {
        return super.visitAST(ast);
      }

      @Override
      public IExpr visit(IComplexNum element) {
        if (useConvergenceMethod) {
          IComplex complexConvergent =
              F.complexConvergent(element.getRealPart(), element.getImaginaryPart());
          if ((complexConvergent.getRealPart().isZero() && !element.re().isZero()) //
              || (complexConvergent.getImaginaryPart().isZero() && !element.im().isZero())) {
            return element;
          }
          return complexConvergent;
        }
        return F.complex(element.getRealPart(), element.getImaginaryPart(), epsilon);
      }

      @Override
      public IExpr visit(INum element) {
        if (useConvergenceMethod) {
          IFraction fractionConvergent = F.fractionConvergent(element.getRealPart());
          if (fractionConvergent.isZero() && !element.isZero()) {
            return element;
          }
          return fractionConvergent;
        }
        return F.fraction(element.getRealPart(), epsilon);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      double epsilon = Config.DOUBLE_EPSILON;
      boolean useConvergenceMethod = true;
      try {
        if (ast.isAST2()) {
          IReal epsilonExpr = ast.arg2().evalReal();
          if (epsilonExpr == null) {
            return F.NIL;
          }
          useConvergenceMethod = false;
          epsilon = epsilonExpr.doubleValue();
          if (arg1.isNumericFunction(true)) {
            // works more similar to MMA if we do this step:
            arg1 = engine.evalN(arg1);
          }
        }
        // try to convert into a fractional number
        return rationalize(arg1, epsilon, useConvergenceMethod).orElse(arg1);
      } catch (RuntimeException rex) {
        // ex.printStackTrace();
        Errors.printMessage(S.Rationalize, rex, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class RootReduce extends AbstractFunctionEvaluator {

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isPowerReciprocal()) {
        IExpr base = arg1.base();
        if (base.isPlus() && base.size() == 3) {
          IExpr p1 = base.first();
          IExpr p2 = base.second();
          if (base.size() == 3 //
              && (p1.isRational() || p1.isFactorSqrtExpr()) && p2.isFactorSqrtExpr()) {
            IRational denominator = (IRational) F.Subtract.of(F.Sqr(p1), F.Sqr(p2));
            IAST numerator = F.Subtract(p1, p2);
            return F.Divide(numerator, denominator);
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }
  /**
   *
   *
   * <pre>
   * SquareFreeQ(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>n</code> a square free integer number or a square free
   * univariate polynomial.
   *
   * </blockquote>
   *
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
  private static final class SquareFreeQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      VariablesSet eVar = new VariablesSet(ast.arg1());
      if (eVar.isSize(0)) {
        IExpr arg1 = ast.arg1();
        if (arg1.isZero()) {
          return S.False;
        }
        if (arg1.isInteger()) {
          return F.booleSymbol(Primality.isSquareFree(((IInteger) arg1).toBigNumerator()));
        }
        if (arg1.isAtom()) {
          return S.False;
        }
      }
      if (!eVar.isSize(1)) {
        // `1` currently not supported in `2`.
        Errors.printMessage(S.SquareFreeQ, "unsupported", F.List(F.CEmptyList, S.SquareFreeQ),
            engine);
        return F.NIL;
      }
      try {
        IExpr expr = F.evalExpandAll(ast.arg1(), engine);
        List<IExpr> varList = eVar.getVarList().copyTo();

        if (ast.isAST2()) {
          return F.booleSymbol(isSquarefreeWithOption(ast, expr, varList, engine));
        }
        return F.booleSymbol(isSquarefree(expr, varList));
      } catch (RuntimeException rex) {
        // JAS may throw RuntimeExceptions
        Errors.printMessage(S.SquareFreeQ, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    public static boolean isSquarefree(IExpr expr, List<IExpr> varList)
        throws JASConversionException {
      JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
      GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);

      FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
      return factorAbstract.isSquarefree(poly);
    }

    public static boolean isSquarefreeWithOption(final IAST lst, IExpr expr, List<IExpr> varList,
        final EvalEngine engine) throws JASConversionException {
      final OptionArgs options = new OptionArgs(lst.topHead(), lst, 2, engine);
      IExpr option = options.getOption(S.Modulus);
      if (option.isReal()) {

        // found "Modulus" option => use ModIntegerRing
        ModIntegerRing modIntegerRing = JASConvert.option2ModIntegerRing((IReal) option);
        JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
        GenPolynomial<ModInteger> poly = jas.expr2JAS(expr, false);

        FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
        return factorAbstract.isSquarefree(poly);
      }
      // option = options.getOption("GaussianIntegers");
      // if (option.equals(S.True)) {
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
      // }
      // return result;
      // } catch (ArithmeticException ae) {
      // // toInt() conversion failed
      // LOGGER.debug("SquareFreeQ.isSquarefreeWithOption() failed", ae);
      // return null; // no evaluation
      // }
      // }
      return false; // no evaluation
    }
  }

  /**
   *
   *
   * <pre>
   * StirlingS1(n, k)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Stirling numbers of the first kind.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Stirling_numbers_of_the_first_kind">Wikipedia -
   * Stirling numbers of the first kind</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; StirlingS1(9, 6)
   * -4536
   * </pre>
   */
  private static class StirlingS1 extends AbstractFunctionEvaluator {

    private static IExpr stirlingS1(IInteger n, IInteger m) {
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

      int counter = nSubtractm.toIntDefault();
      if (counter > Integer.MIN_VALUE) {
        counter++;
        IInteger value;
        IASTAppendable temp = F.PlusAlloc(counter >= 0 ? counter : 0);
        long leafCount = 0;
        for (int i = 0; i < counter; i++) {
          value = F.ZZ(i);
          if ((i & 1) == 1) { // isOdd(i) ?
            factorPlusMinus1 = F.CN1;
          } else {
            factorPlusMinus1 = F.C1;
          }
          temp.append(
              Times(factorPlusMinus1, F.Binomial(Plus(value, nSubtract1), Plus(value, nSubtractm)),
                  F.Binomial(nTimes2Subtractm, F.Subtract(nSubtractm, value)),
                  F.StirlingS2(Plus(value, nSubtractm), value)));
          leafCount += temp.leafCount();
          if (leafCount > Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(leafCount);
          }
        }
        return temp;
      }
      // Machine-sized integer expected at position `2` in `1`.
      throw new ArgumentTypeException("intm", F.list(F.ZZ(1), F.StirlingS1(n, m)));
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr m = ast.arg2();
      if (n.isNegativeResult() || m.isNegativeResult()) {
        return F.NIL;
      }
      if (n.isZero() && m.isZero()) {
        return F.C1;
      }
      if (n.isZero() && m.isPositiveResult()) {
        return C0;
      }

      if (n.equals(m)) {
        return F.C1;
      }
      if (n.isInteger() && m.isInteger()) {
        return stirlingS1((IInteger) n, (IInteger) m);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * StirlingS2(n, k)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Stirling numbers of the second kind.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "http://en.wikipedia.org/wiki/Stirling_numbers_of_the_second_kind">Wikipedia -
   * Stirling numbers of the second kind</a>
   * </ul>
   *
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
        if (nArg1.isNegativeResult() || kArg2.isNegativeResult()) {
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

          int n = Validate.checkNonNegativeIntType(ast, 1);
          int k = ki.toIntDefault(0);
          if (k != 0) {
            return stirlingS2(n, ki, k);
          }
        }
      } catch (MathRuntimeException mre) {
        Errors.printMessage(S.StirlingS2, mre, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Subfactorial(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the subfactorial number of the integer <code>n</code>
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Subfactorial(12)
   * 176214841
   * </pre>
   */
  private static class Subfactorial extends AbstractFunctionEvaluator {

    /**
     * Iterative subfactorial algorithm based on the recurrence: <code>
     * Subfactorial(n) = n * Subfactorial(n-1) + (-1)^n</code> See
     * <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger() && arg1.isPositive()) {
        try {
          long n = ((IInteger) arg1).toLong();
          return subFactorial(n);
        } catch (ArithmeticException ae) {
          Errors.printMessage(S.Subfactorial, ae, engine);
        }
        return F.NIL;
      }
      if (arg1.isZero()) {
        return F.C1;
      }
      if (arg1.isInexactNumber()) {
        // Gamma(1+arg1, -1) / E
        return F.Times(F.Gamma(F.Plus(F.C1, arg1), F.CN1), F.Power(S.E, F.CN1));
      }
      if (engine.isNumericMode() && arg1.isNumericFunction()) {
        // Gamma(1+arg1, -1) / E
        return F.Times(F.Gamma(F.Plus(F.C1, arg1), F.CN1), F.Power(S.E, F.CN1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BellB.setEvaluator(new BellB());
      S.BernoulliB.setEvaluator(new BernoulliB());
      S.Binomial.setEvaluator(new Binomial());
      S.CarmichaelLambda.setEvaluator(new CarmichaelLambda());
      S.CatalanNumber.setEvaluator(new CatalanNumber());
      S.ChineseRemainder.setEvaluator(new ChineseRemainder());
      S.Convergents.setEvaluator(new Convergents());
      S.ContinuedFraction.setEvaluator(new ContinuedFraction());
      S.CoprimeQ.setEvaluator(new CoprimeQ());
      S.DedekindNumber.setEvaluator(new DedekindNumber());
      S.DiracDelta.setEvaluator(new DiracDelta());
      S.Divisible.setEvaluator(new Divisible());
      S.Divisors.setEvaluator(new Divisors());
      S.DivisorSum.setEvaluator(new DivisorSum());
      S.DivisorSigma.setEvaluator(new DivisorSigma());
      S.EulerE.setEvaluator(new EulerE());
      S.EulerPhi.setEvaluator(new EulerPhi());
      S.ExtendedGCD.setEvaluator(new ExtendedGCD());
      S.Factorial.setEvaluator(new Factorial());
      S.FactorialPower.setEvaluator(new FactorialPower());
      S.Factorial2.setEvaluator(new Factorial2());
      S.FactorInteger.setEvaluator(new FactorInteger());
      S.Fibonacci.setEvaluator(new Fibonacci());
      S.FindLinearRecurrence.setEvaluator(new FindLinearRecurrence());
      S.FrobeniusNumber.setEvaluator(new FrobeniusNumber());
      S.FromContinuedFraction.setEvaluator(new FromContinuedFraction());
      S.Hyperfactorial.setEvaluator(new Hyperfactorial());
      S.JacobiSymbol.setEvaluator(new JacobiSymbol());
      S.LinearRecurrence.setEvaluator(new LinearRecurrence());
      S.LiouvilleLambda.setEvaluator(new LiouvilleLambda());
      S.LucasL.setEvaluator(new LucasL());
      S.MangoldtLambda.setEvaluator(new MangoldtLambda());
      S.MersennePrimeExponent.setEvaluator(new MersennePrimeExponent());
      S.MersennePrimeExponentQ.setEvaluator(new MersennePrimeExponentQ());
      S.ModularInverse.setEvaluator(new ModularInverse());
      S.MoebiusMu.setEvaluator(new MoebiusMu());
      S.Multinomial.setEvaluator(new Multinomial());
      S.MultiplicativeOrder.setEvaluator(new MultiplicativeOrder());
      S.NextPrime.setEvaluator(new NextPrime());
      S.PartitionsP.setEvaluator(new PartitionsP());
      S.PartitionsQ.setEvaluator(new PartitionsQ());
      S.PerfectNumber.setEvaluator(new PerfectNumber());
      S.PerfectNumberQ.setEvaluator(new PerfectNumberQ());
      S.Prime.setEvaluator(new Prime());
      S.PrimePi.setEvaluator(new PrimePi());
      S.PrimeOmega.setEvaluator(new PrimeOmega());
      S.PrimePowerQ.setEvaluator(new PrimePowerQ());
      S.PrimitiveRoot.setEvaluator(new PrimitiveRoot());
      S.PrimitiveRootList.setEvaluator(new PrimitiveRootList());
      S.QuadraticIrrationalQ.setEvaluator(new QuadraticIrrationalQ());
      S.RamseyNumber.setEvaluator(new RamseyNumber());
      S.Rationalize.setEvaluator(new Rationalize());
      S.RootReduce.setEvaluator(new RootReduce());
      S.SquareFreeQ.setEvaluator(new SquareFreeQ());
      S.StirlingS1.setEvaluator(new StirlingS1());
      S.StirlingS2.setEvaluator(new StirlingS2());
      S.Subfactorial.setEvaluator(new Subfactorial());
    }
  }

  public static IInteger factorial(final IInteger x) {
    return x.factorial();
  }

  public static boolean check(IExpr n, IExpr k, IExpr delta, EvalEngine engine) {
    return engine.evalEqual(n, k.plus(delta));
  }

  public static IInteger factorial(int ni) {
    BigInteger result;
    if (ni < 0) {
      int positiveN = -1 * ni;
      int iterationLimit = EvalEngine.get().getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit < positiveN) {
        IterationLimitExceeded.throwIt(positiveN, F.Factorial(F.ZZ(ni)));
      }
      result = BigIntegerMath.factorial(positiveN);
      if ((ni & 0x0001) == 0x0001) {
        // odd integer number
        result = result.multiply(BigInteger.valueOf(-1L));
      }
    } else {
      int iterationLimit = EvalEngine.get().getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit < ni) {
        IterationLimitExceeded.throwIt(ni, F.Factorial(F.ZZ(ni)));
      }

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
   * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time. See:
   * <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code:
   * Fibonacci sequence.</a>
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
        if (result.bitLength() > Config.MAX_AST_SIZE * 8) {
          IterationLimitExceeded.throwIt(result.bitLength(), F.Fibonacci(F.ZZ(iArg)));
        }
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
   * Calculate integer binomial number. See definitions by
   * <a href="https://arxiv.org/abs/1105.3689">Kronenburg 2011</a>
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


  private static IExpr binomialNumeric(final INumber n, final INumber k) {
    INumber nPlus1 = n.plus(F.C1);
    INumber nMinuskPlus1 = nPlus1.subtract(k);
    INumber kPlus1 = k.plus(F.C1);
    // (n,k) ==> Gamma(n+1)/(Gamma(k+1)*Gamma(n-k+1))
    return F.Times(F.Gamma(nPlus1), F.Power(F.Gamma(kPlus1), -1),
        F.Power(F.Gamma(nMinuskPlus1), -1));
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
    if (n > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(n);
    }
    IFraction[] bernoulli = new IFraction[n + 1];
    bernoulli[0] = FractionSym.ONE;
    bernoulli[1] = AbstractFractionSym.valueOf(-1L, 2L);

    int iterationLimit = EvalEngine.get().getIterationLimit();
    if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
      iterationLimit *= 2;
    }
    int iterationCounter = 0;
    for (int k = 2; k <= n; k++) {
      bernoulli[k] = FractionSym.ZERO;
      for (int i = 0; i < k; i++) {
        if (!bernoulli[i].isZero()) {
          if (iterationLimit > 0 && iterationLimit <= iterationCounter++) {
            IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
          }
          IFraction bin = AbstractFractionSym.valueOf(BigIntegerMath.binomial(k + 1, k + 1 - i));
          bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
        }
      }
      bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));
    }
    return bernoulli[n].normalize();
  }

  public static double bernoulliDouble(int n) {
    return bernoulliNumber(n).doubleValue();
  }

  /**
   * Compute the Bernoulli number of the first kind.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>.
   * <br>
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
        sum = sum.add(((IInteger) list.get(i)).powerRational(kl));
      }
      return sum.toBigNumerator();
    }
    return null;
  }

  /**
   * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
   *
   * @param k the non-negative coefficients
   * @param n the sum of the non-negative coefficients
   * @return
   */
  public static IInteger multinomial(final int[] k, final int n) {
    IInteger bn = AbstractIntegerSym.valueOf(n);
    IInteger pPlus = F.C1;
    IRational pNeg = F.C1;
    int nNeg = 0;
    for (int i = 0; i < k.length; i++) {
      if (k[i] != 0) {
        if (k[i] < 0) {
          nNeg++;
          int temp = -1 - k[i];
          pNeg = pNeg.divideBy(factorial(temp));
          if ((temp & 1) == 1) {
            pNeg = pNeg.negate();
          }
        } else if (k[i] > 0) {
          pPlus = pPlus.multiply(factorial(k[i]));
        } else {
          return F.C0;
        }
      }
    }
    if (n < 0) {
      nNeg--;
      if (nNeg > 0) {
        return F.C0;
      }
      int kFactor = -1 - n;
      IRational p = pPlus.multiply(pNeg).multiply(factorial(kFactor));
      if ((kFactor & 1) == 1) {
        p = p.negate();
      }
      return p.isNegative() ? p.denominator().negate() : p.denominator();
    }
    if (nNeg > 0) {
      return F.C0;
    }
    IInteger result = factorial(bn).div(pPlus);
    return result;
  }

  /**
   * Gives the multinomial coefficient <code>(k0+k1+...)!/(k0! * k1! ...)</code>.
   *
   * @param k the non-negative coefficients
   * @return
   */
  public static IInteger multinomial(IInteger[] k) {
    IInteger n = F.C0;
    for (int i = 0; i < k.length; i++) {
      n = n.add(k[i]);
    }
    int ni = n.toIntDefault();
    if (ni != Integer.MIN_VALUE) {
      int[] ki = new int[k.length];
      boolean evaled = true;
      for (int i = 0; i < k.length; i++) {
        ki[i] = k[i].toIntDefault();
        if (ki[i] == Integer.MIN_VALUE) {
          evaled = false;
          break;
        }
      }
      if (evaled) {
        return multinomial(ki, ni);
      }
    }
    return null;
    // IInteger result = factorial(n);
    // for (int i = 0; i < k.length; i++) {
    // result = result.div(factorial(k[i]));
    // }
    // return result;
  }

  /**
   * Returns the Stirling number of the second kind, "{@code S(n,k)}", the number of ways of
   * partitioning an {@code n}-element set into {@code k} non-empty subsets.
   *
   * @param n the size of the set. Must be a value &gt; 0
   * @param k the number of non-empty subsets
   * @param ki the number of non-empty subsets as int value
   * @return {@code S2(nArg1,kArg2)} or throw <code>ArithmeticException</code> if <code>n</code>
   *         cannot be converted into a positive int number
   */
  public static IInteger stirlingS2(int n, IInteger k, int ki) throws MathRuntimeException {
    if (n != 0 && n <= 25) { // S(26,9) = 11201516780955125625 is larger than Long.MAX_VALUE
      return F.ZZ(CombinatoricsUtils.stirlingS2(n, ki));
    }
    IInteger sum = F.C0;
    for (int i = 0; i < ki; i++) {
      IInteger bin = binomial(k, F.ZZ(i));
      IInteger pow = k.add(F.ZZ(-i)).powerRational(n);
      if ((i & 1) == 1) { // isOdd(i) ?
        sum = sum.add(bin.negate().multiply(pow));
      } else {
        sum = sum.add(bin.multiply(pow));
      }
    }
    return sum.div(factorial(k));
  }

  /**
   * The first 8 perfect numbers fitting into a Java long
   *
   * <p>
   * See
   * <a href= "https://en.wikipedia.org/wiki/List_of_perfect_numbers">List_of_perfect_numbers</a>
   */
  private static final long[] PN_8 =
      {6, 28, 496, 8128, 33550336L, 8589869056L, 137438691328L, 2305843008139952128L};

  /**
   * The first 51 mersenne prime exponents.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Mersenne_prime">Mersenne prime</a>
   */
  private static final int[] MPE_51 = {2, 3, 5, 7, 13, 17, 19, 31, 61, 89, 107, 127, 521, 607, 1279,
      2203, 2281, 3217, 4253, 4423, 9689, 9941, 11213, 19937, 21701, 23209, 44497, 86243, 110503,
      132049, 216091, 756839, 859433, 1257787, 1398269, 2976221, 3021377, 6972593, 13466917,
      20996011, 24036583, 25964951, 30402457, 32582657, 37156667, 42643801, 43112609, 57885161,
      74207281, 77232917, 82589933};

  /**
   * The first 7 Dedekind numbers.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Dedekind_number">Dedekind number</a>
   */
  private static final int[] DEDEKIND_7 = {2, 3, 6, 20, 168, 7581, 7828354};

  /**
   * The 7th, 8th and 9th Dedekind number.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Dedekind_number">Dedekind number</a>
   */
  private static final IInteger[] DEDEKIND_REST = {//
      F.ZZ("2414682040998", 10), //
      F.ZZ("56130437228687557907788", 10), //
      F.ZZ("286386577668298411128469151667598498812366", 10)};

  private NumberTheory() {}

  public static IExpr rationalize(IExpr arg1) {
    return NumberTheory.rationalize(arg1, Config.DOUBLE_EPSILON, true);
  }

  /**
   * Rationalize only pure numeric numbers in expression <code>arg</code>.
   *
   * @param arg1
   * @return <code>F.NIL</code> if no expression was transformed
   */
  public static IExpr rationalize(IExpr arg1, boolean useConvergenceMethod) {
    return NumberTheory.rationalize(arg1, Config.DOUBLE_EPSILON, useConvergenceMethod);
  }

  /**
   * Rationalize only pure numeric numbers in expression <code>arg</code>.
   *
   * @param arg1
   * @param epsilon
   * @param useConvergenceMethod
   * @return <code>F.NIL</code> if no expression was transformed
   */
  public static IExpr rationalize(IExpr arg1, double epsilon, boolean useConvergenceMethod) {
    Rationalize.RationalizeNumericsVisitor rationalizeVisitor =
        new Rationalize.RationalizeNumericsVisitor(epsilon, useConvergenceMethod);
    return arg1.accept(rationalizeVisitor);
  }

  /**
   * Return a list <code>{p,q,d,s}</code>, with <code>p,q,d,s</code> integers, if the expression is
   * of the form <code>(p + s * Sqrt(d)) / q</code>.
   *
   * @param expr
   * @return {@link F#NIL} if <code>expr</code> is not quadratic irrational.
   */
  public static IAST quadraticIrrational(final IExpr expr) {
    if (expr.isAST()) {
      IASTMutable resultList = F.List(0, 1, 0, 1);
      if (expr.isSqrt() && expr.first().isInteger() && expr.first().isNonNegativeResult()) {
        resultList.set(3, expr.first());
        return resultList;
      }
      if (expr.isPlus2()) {
        return quadraticIrrationalPlus((IAST) expr, resultList);
      } else if (expr.isTimes2() && expr.first().isInteger() && expr.second().isSqrt()) {
        resultList.set(4, expr.first());
        IAST sqrt = (IAST) expr.second();
        if (sqrt.arg1().isInteger() && sqrt.arg1().isPositive()) {
          resultList.set(3, sqrt.first());
          return resultList;
        }
      } else if (expr.isTimes2() && expr.first().isFraction()) {
        IFraction frac = (IFraction) expr.first();
        if (frac.numerator().isOne() || frac.numerator().isMinusOne()) {
          if (frac.numerator().isOne()) {
            resultList.set(2, frac.denominator());
          } else {
            resultList.set(2, frac.denominator().negate());
          }

          IExpr arg2 = expr.second();
          if (arg2.isSqrt() && arg2.first().isInteger() && arg2.first().isPositive()) {
            resultList.set(3, arg2.first());
            return resultList;
          }
          if (arg2.isPlus2()) {
            return quadraticIrrationalPlus((IAST) arg2, resultList);
          }
        }
      }
    }
    return F.NIL;
  }

  private static IAST quadraticIrrationalPlus(IAST plusAST, IASTMutable resultList) {
    if (plusAST.arg1().isInteger()) {
      resultList.set(1, plusAST.arg1());
      IExpr arg2 = plusAST.arg2();
      if (arg2.isSqrt() && arg2.first().isInteger()) {
        resultList.set(3, arg2.first());
        return resultList;
      } else if (arg2.isTimes2() && arg2.first().isInteger() && arg2.second().isSqrt()) {
        resultList.set(4, arg2.first());
        IAST sqrt = (IAST) arg2.second();
        if (sqrt.arg1().isInteger() && sqrt.arg1().isPositive()) {
          resultList.set(3, sqrt.first());
          return resultList;
        }
      }
    }
    return F.NIL;
  }
}
