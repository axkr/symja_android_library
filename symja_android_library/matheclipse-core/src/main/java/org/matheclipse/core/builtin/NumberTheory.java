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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.NumericComputationException;
import org.apfloat.internal.BackingStorageException;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
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
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.BigIntegerSym;
import org.matheclipse.core.expression.DMath;
import org.matheclipse.external.apfloat.ApcomplexZetaMath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.GaussianInteger;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.numbertheory.PartitionNumbers;
import org.matheclipse.core.numbertheory.PrimeCount;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.sympy.ntheory.ResidueNTheory;
import org.matheclipse.core.sympy.series.Sequences;
import com.google.common.math.LongMath;
import com.google.common.util.concurrent.UncheckedExecutionException;
import edu.jas.arith.BigRational;
import edu.jas.arith.MachinePrime;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import io.github.mangara.diophantine.QuadraticSolver;
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.quadratic.PellsSolver;

public final class NumberTheory {

  /**
   * How often a long running search checks for interruption. A test on every step would sit in the
   * innermost loop; every 16384 keeps it off the hot path and still reacts in a fraction of a
   * second.
   */
  private static final int INTERRUPT_CHECK_MASK = 0x3FFF;

  private static final long[] BELLB_25 = {1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570,
      4213597, 27644437, 190899322L, 1382958545L, 10480142147L, 82864869804L, 682076806159L,
      5832742205057L, 51724158235372L, 474869816156751L, 4506715738447323L, 44152005855084346L,
      445958869294805289L, 4638590332229999353L};

  private static final long[] FACTORIAL2_34 = {1, 1, 2, 3, 8, 15, 48, 105, 384, 945, 3840, 10395,
      46080, 135135, 645120, 2027025, 10321920, 34459425, 185794560, 654729075, 3715891200L,
      13749310575L, 81749606400L, 316234143225L, 1961990553600L, 7905853580625L, 51011754393600L,
      213458046676875L, 1428329123020800L, 6190283353629375L, 42849873690624000L,
      191898783962510625L, 1371195958099968000L, 6332659870762850625L};

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
      return F.sum(k -> stirlingS2(index, k, k.toIntDefault()), 0, index);
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

      return F.sum(k -> F.Times(stirlingS2(n, k, k.toIntDefault()), F.Power(z, k)), 0, n + 1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr exactArguments = PolynomialFunctions.exactArguments(ast, engine);
      if (exactArguments.isPresent()) {
        return exactArguments;
      }
      try {
        IExpr arg1 = ast.arg1();
        if (arg1.isNegative()) {
          // Non-negative machine-sized integer expected at position `2` in `1`
          return Errors.printMessage(S.BellB, "intnm", F.list(ast, F.C1), engine);
        }
        int n = arg1.toMachineInt();
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
        rex.printStackTrace();
        Errors.rethrowsInterruptException(rex);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
      IExpr n = ast.arg1();
      if (n.isNegative()) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.BernoulliB, "intnm", F.List(ast, F.C1), engine);
      }
      if (ast.isAST1()) {
        if (n.isZero()) {
          return F.C1;
        }
        if ((engine.isArbitraryMode() || engine.isDoubleMode()) && n.isPositive()) {
          long ln = n.toLongDefault();
          if (ln >= 0) {
            FixedPrecisionApfloatHelper h;
            // from Zeta(n): the rational number of the library takes seconds for n = 1000
            if (engine.isArbitraryMode()) {
              h = EvalEngine.getApfloat(engine);
              try {
                return F.num(ApcomplexZetaMath.bernoulli(ln, h.precision()));
              } catch (Exception ce) {
                Errors.rethrowsInterruptException(ce);
              }
            } else {
              h = EvalEngine.getApfloatDouble();
              try {
                return ApfloatNum.machineOrPromoted(ApcomplexZetaMath.bernoulli(ln, h.precision()));
              } catch (Exception ce) {
                Errors.rethrowsInterruptException(ce);
              }
            }

          }
          return F.NIL;
        }
        final int bn = n.toIntDefault();
        if (bn >= 0) {
          return AbstractFractionSym.bernoulliNumber(bn);
        }
        IExpr temp = engine.evaluate(F.Subtract(n, F.C3));
        if (temp.isIntegerResult() && temp.isPositiveResult() && temp.isEvenResult()) {
          // http://fungrim.org/entry/a98234/
          return F.C0;
        }
        return F.NIL;
      } else if (ast.isAST2()) {
        IExpr x = ast.arg2();
        if (n.isZero()) {
          return F.C1;
        }
        if ((engine.isArbitraryMode() || engine.isDoubleMode()) && n.isPositive() && x.isNumber()) {
          INumber z = (INumber) x;
          long ln = n.toLongDefault();
          if (ln >= 0) {
            FixedPrecisionApfloatHelper h;
            if (engine.isArbitraryMode()) {
              h = EvalEngine.getApfloat(engine);
            } else {
              h = EvalEngine.getApfloatDouble();
            }
            if (z.isReal()) {
              IReal r = (IReal) z;
              try {
                if (engine.isArbitraryMode()) {
                  return F.num(h.bernoulliB(ln, r.apfloatValue()));
                } else {
                  return F.num(h.bernoulliB(ln, r.apfloatValue()).doubleValue());
                }
              } catch (ApfloatArithmeticException | BackingStorageException
                  | IllegalArgumentException | NumericComputationException ex) {
                return Errors.printMessage(S.BernoulliB, ex, engine);
              }
            }
            try {
              Apcomplex bernoulliB = h.bernoulliB(ln, z.apcomplexValue());
              if (engine.isArbitraryMode()) {
                return F.complexNum(bernoulliB);
              } else {
                return F.complexNum(bernoulliB.real().doubleValue(),
                    bernoulliB.imag().doubleValue());
              }
            } catch (ApfloatArithmeticException | BackingStorageException | IllegalArgumentException
                | NumericComputationException ex) {
              return Errors.printMessage(S.BernoulliB, ex, engine);
            }
          }
        }

        int xInt = x.toIntDefault();
        if (F.isPresent(xInt)) {
          if (xInt == 0) {
            // http://fungrim.org/entry/a1d2d7/
            return F.BernoulliB(n);
          }
          if (xInt == 1 && n.isIntegerResult()) {
            // http://fungrim.org/entry/829185/
            return F.Times(F.Power(-1, n), F.BernoulliB(n));
          }
        }
        if (n.isInteger() && n.isNonNegativeResult()) {
          if (x.isNumEqualRational(F.C1D2)) {
            // http://fungrim.org/entry/03ee0b/
            return F.Times(F.Subtract(F.Power(F.C2, F.Subtract(F.C1, n)), F.C1), F.BernoulliB(n));
          }
          final int bn = n.toIntDefault();
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
  private static class Binomial extends AbstractArg2 implements IntBinaryOperator {
    @Override
    public int applyAsInt(int n, int k) {
      if (n < 0 || k < 0 || k > n) {
        throw new ArithmeticException("Binomial out of domain");
      }
      try {
        return Math.toIntExact(org.hipparchus.util.CombinatoricsUtils.binomialCoefficient(n, k));
      } catch (MathIllegalArgumentException | ArithmeticException e) {
        throw new ArithmeticException("Binomial out of 32-bit int bounds");
      }
    }

    @Override
    public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.binomial(a1.apfloatValue(), a2.apfloatValue()));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.binomial(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e2IntArg(final IInteger n, final IInteger k) {
      return AbstractIntegerSym.binomial(n, k);
    }

    @Override
    public IExpr e2ObjArg(IAST ast, IExpr n, IExpr k) {
      if (n.isInteger() && k.isInteger()) {
        // use e2IntArg() method
        return F.NIL;
      }
      return binomialExpr(n, k).eval(EvalEngine.get());
    }

    private static IExpr binomialExpr(IExpr n, IExpr k) {
      int ni = n.toIntDefault();
      if (F.isPresent(ni)) {
        int ki = k.toIntDefault();
        if (F.isPresent(ki)) {
          return AbstractIntegerSym.binomial(F.ZZ(ni), F.ZZ(ki));
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
            return F.CInfinity;
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
        final int kInt = ki.toIntDefault();
        if (n instanceof Num && ki.isPositive() && (!F.isPresent(kInt) || kInt > 64)) {
          // no product of that many factors: Binomial(0.5, 10^20) was 0
          double machine = DMath.binomial(((Num) n).doubleValue(), ki.doubleValue());
          if (!Double.isNaN(machine)) {
            return F.num(machine);
          }
        }
        if (F.isPresent(kInt) && kInt > 1 //
            && (n.isNumber() || kInt < 6)) {
          return binomialPolynomial(n, kInt);
        }
      }
      if (n.equals(k)) {
        return F.C1;
      }

      if (n instanceof Num && k instanceof Num) {
        // large arguments, for which a gamma function of the quotient leaves the range of a double
        double machine = DMath.binomial(((Num) n).doubleValue(), ((Num) k).doubleValue());
        if (!Double.isNaN(machine)) {
          return F.num(machine);
        }
      }
      if (n.isNumber() && k.isNumber()) {
        return INumber.binomialNumeric((INumber) n, (INumber) k);
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
          result.append(F.Power(AbstractIntegerSym.factorial(diff), -1));
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
  private static class CarmichaelLambda extends AbstractTrigArg1 implements IntUnaryOperator {
    @Override
    public int applyAsInt(int arg) {
      if (arg == 0) {
        throw new ArithmeticException("CarmichaelLambda(0) is undefined");
      }
      return F.ZZ(arg).charmichaelLambda().toIntDefault();
    }

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class CatalanNumber extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr n = ast.arg1();
        // (2^(2*n)*Gamma(1/2+n))/(Sqrt(Pi)*Gamma(2+n))
        return F.Times(F.Power(F.C2, F.Times(F.C2, n)), F.Gamma(F.Plus(n, F.C1D2)),
            F.Power(F.Times(F.Sqrt(S.Pi), F.Gamma(F.Plus(n, F.C2))), F.CN1));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      if (n.isInteger()) {
        return AbstractIntegerSym.catalanNumber((IInteger) n);
      } else if (n.isFraction()) {
        if (((IFraction) n).denominator().equals(F.C2)) {
          return functionExpand(ast, engine);
        }
      }

      return F.NIL;
    }

    @Override
    public IExpr numericEval(final IAST ast, final EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isInexactNumber()) {
          return functionExpand(ast, engine);
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.CatalanNumber, rex, engine);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
      while (r.signum() != 0) {
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
        for (int position = 1; position <= 2; position++) {
          IAST list = (IAST) ast.get(position);
          if (list.exists(x -> x.isNumber() && !x.isInteger())) {
            // The arguments to `1` must be two lists of integers of identical length, with the
            // second list only containing positive integers.
            return Errors.printMessage(S.ChineseRemainder, "pilist", F.list(S.ChineseRemainder),
                engine);
          }
        }
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
        if (list.size() > 1 && list.forAll(x -> x.isInteger())) {
          // p(k) = a(k)*p(k-1) + p(k-2), q(k) = a(k)*q(k-1) + q(k-2)
          IASTAppendable resultList = F.ListAlloc(list.size());
          IInteger pPrevious = F.C1;
          IInteger qPrevious = F.C0;
          IInteger p = (IInteger) list.arg1();
          IInteger q = F.C1;
          resultList.append(p);
          for (int i = 2; i < list.size(); i++) {
            IInteger a = (IInteger) list.get(i);
            IInteger pNext = a.multiply(p).add(pPrevious);
            IInteger qNext = a.multiply(q).add(qPrevious);
            pPrevious = p;
            qPrevious = q;
            p = pNext;
            q = qNext;
            if (q.isZero()) {
              return F.NIL;
            }
            resultList.append(F.QQ(p, q).normalize());
          }
          return resultList;
        }
        if (list.size() > 1) {
          int size = list.argSize();
          IASTAppendable resultList = F.ListAlloc(list.size());
          IASTMutable plus = F.binary(S.Plus, F.C0, list.arg1());
          IASTMutable result = plus;
          for (int i = 2; i <= size; i++) {
            IExpr temp;
            if (result.isAST()) {
              temp = engine.evaluate(F.Together(result.copy()));
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
      newSymbol.setAttributes(Attribute.NHOLDREST);
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
     * The continued fraction of the quadratic irrational <code>x = a + b*Sqrt(d)</code> with
     * rational <code>a</code>, <code>b != 0</code> and an integer <code>d &gt; 1</code> which is not
     * a square. The terms of a negative <code>x</code> are the negated terms of <code>-x</code>.
     *
     * <p>
     * <code>x</code> is written as <code>(p + Sqrt(dd)) / q</code> with integers, where
     * <code>q</code> divides <code>dd - p^2</code>. Every complete quotient has this form again and
     * its term is computed exactly from the integer square root of <code>dd</code>. The period
     * starts at the first pair <code>{p, q}</code> which repeats.
     *
     * @param maxTerms the number of terms, or {@link Integer#MAX_VALUE} for the periodic form
     *        <code>{a0, a1, ..., {period}}</code>
     */
    private static IAST continuedFractionQuadratic(IRational a, IRational b, BigInteger d,
        int maxTerms, EvalEngine engine) {
      BigInteger q = a.toBigDenominator().multiply(b.toBigDenominator())
          .divide(a.toBigDenominator().gcd(b.toBigDenominator()));
      BigInteger p = a.toBigNumerator().multiply(q.divide(a.toBigDenominator()));
      BigInteger s = b.toBigNumerator().multiply(q.divide(b.toBigDenominator()));
      // x == (p + s*Sqrt(d)) / q with q > 0
      final boolean negate = !isPositiveSurd(p, s, d);
      if (negate) {
        p = p.negate();
        s = s.negate();
      }
      BigInteger dd = s.multiply(s).multiply(d);
      if (s.signum() < 0) {
        // (p - |s|*Sqrt(d)) / q == (-p + Sqrt(dd)) / (-q)
        p = p.negate();
        q = q.negate();
      }
      if (dd.subtract(p.multiply(p)).remainder(q).signum() != 0) {
        BigInteger absQ = q.abs();
        p = p.multiply(absQ);
        dd = dd.multiply(q.multiply(q));
        q = q.multiply(absQ);
      }
      final BigInteger root = dd.sqrt();
      final boolean periodic = maxTerms == Integer.MAX_VALUE;
      IASTAppendable terms = F.ListAlloc(periodic ? 16 : maxTerms);
      Map<List<BigInteger>, Integer> seen = new HashMap<List<BigInteger>, Integer>();
      int periodStart = -1;
      while (true) {
        if (periodic) {
          Integer index = seen.putIfAbsent(java.util.Arrays.asList(p, q), terms.argSize());
          if (index != null) {
            periodStart = index;
            break;
          }
          if (terms.argSize() >= Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(terms.argSize());
          }
        } else if (terms.argSize() >= maxTerms) {
          break;
        }
        engine.checkTimeLimit();
        // Floor((p + Sqrt(dd)) / q) for an irrational Sqrt(dd)
        BigInteger term = q.signum() > 0 ? floorDivide(p.add(root), q)
            : floorDivide(p.negate().subtract(root).subtract(BigInteger.ONE), q.negate());
        terms.append(F.ZZ(negate ? term.negate() : term));
        p = term.multiply(q).subtract(p);
        q = dd.subtract(p.multiply(p)).divide(q);
      }
      if (periodStart < 0) {
        return terms;
      }
      if (periodStart == 0) {
        // a purely periodic expansion is written with its first term in front of the period
        terms.append(terms.arg1());
        periodStart = 1;
      }
      IASTAppendable result = F.ListAlloc(periodStart + 1);
      result.appendAll(terms, 1, periodStart + 1);
      result.append(terms.copyFrom(periodStart + 1));
      return result;
    }

    /** Test if <code>p + s*Sqrt(d)</code> is positive, for a <code>d</code> which is no square. */
    private static boolean isPositiveSurd(BigInteger p, BigInteger s, BigInteger d) {
      if (p.signum() >= 0 && s.signum() > 0) {
        return true;
      }
      if (p.signum() <= 0 && s.signum() < 0) {
        return false;
      }
      int compare = p.multiply(p).compareTo(s.multiply(s).multiply(d));
      return p.signum() > 0 ? compare > 0 : compare < 0;
    }

    /** <code>Floor(n / m)</code> for a positive <code>m</code>. */
    private static BigInteger floorDivide(BigInteger n, BigInteger m) {
      BigInteger[] quotientRemainder = n.divideAndRemainder(m);
      return quotientRemainder[1].signum() < 0 ? quotientRemainder[0].subtract(BigInteger.ONE)
          : quotientRemainder[0];
    }

    /**
     * The rationals <code>a, b</code> and the integer <code>d</code> of an <code>expr</code> which
     * is written <code>a + b*Sqrt(d)</code> with <code>b != 0</code> and <code>d &gt; 1</code> no
     * square, or <code>null</code>.
     */
    private static IExpr[] quadraticSurd(IExpr expr) {
      IExpr[] parts = new IExpr[] {F.C0, F.C0, null};
      if (!addQuadraticSurd(expr, F.C1, parts) || parts[1].isZero() || parts[2] == null) {
        return null;
      }
      return parts;
    }

    /**
     * Add <code>factor*expr</code> to <code>parts[0] + parts[1]*Sqrt(parts[2])</code>.
     *
     * @return <code>false</code> if <code>expr</code> is not of the form <code>a + b*Sqrt(d)</code>
     *         with the <code>d</code> of <code>parts[2]</code>
     */
    private static boolean addQuadraticSurd(IExpr expr, IRational factor, IExpr[] parts) {
      if (expr.isRational()) {
        parts[0] = ((IRational) parts[0]).add(factor.multiply((IRational) expr));
        return true;
      }
      if (expr.isPlus()) {
        for (IExpr summand : (IAST) expr) {
          if (!addQuadraticSurd(summand, factor, parts)) {
            return false;
          }
        }
        return true;
      }
      if (expr.isTimes2() && expr.first().isRational()) {
        return addQuadraticSurd(expr.second(), factor.multiply((IRational) expr.first()), parts);
      }
      if (expr.isPower() && expr.base().isRational() && expr.base().isPositive()
          && (expr.exponent().equals(F.C1D2) || expr.exponent().equals(F.CN1D2))) {
        // Sqrt(n/m) == Sqrt(n*m)/m and 1/Sqrt(n/m) == Sqrt(n*m)/n
        IRational radicand = (IRational) expr.base();
        IInteger n = radicand.numerator();
        IInteger m = radicand.denominator();
        IInteger d = n.multiply(m);
        IRational coefficient = factor.divideBy(expr.exponent().equals(F.C1D2) ? m : n);
        BigInteger dRoot = d.toBigNumerator().sqrt();
        if (dRoot.multiply(dRoot).equals(d.toBigNumerator())) {
          // Sqrt(4/9) and 1 are rational
          return false;
        }
        if (parts[2] == null) {
          parts[2] = d;
        } else if (!parts[2].equals(d)) {
          return false;
        }
        parts[1] = ((IRational) parts[1]).add(coefficient);
        return true;
      }
      if (expr.isPower() && expr.exponent().isMinusOne()) {
        // 1/(a + b*Sqrt(d)) == (a - b*Sqrt(d)) / (a^2 - b^2*d)
        IExpr[] inverse = quadraticSurd(expr.base());
        if (inverse == null || (parts[2] != null && !parts[2].equals(inverse[2]))) {
          return false;
        }
        IRational a = (IRational) inverse[0];
        IRational b = (IRational) inverse[1];
        IRational norm = a.multiply(a).subtract(b.multiply(b).multiply((IInteger) inverse[2]));
        parts[0] = ((IRational) parts[0]).add(factor.multiply(a).divideBy(norm));
        parts[1] = ((IRational) parts[1]).subtract(factor.multiply(b).divideBy(norm));
        parts[2] = inverse[2];
        return true;
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1() && arg1.isAST(S.N, 3) && arg1.second().isInteger()) {
        // an exact number given to a precision is evaluated with guard digits: the precision,
        // not the rounding of the last digit, limits the number of terms
        IExpr exact = engine.evaluate(arg1.first());
        long digits = arg1.second().toLongDefault();
        if (exact.isNumericFunction(true) && !exact.isInexactNumber() && !exact.isRational()
            && digits > org.matheclipse.parser.client.ParserConfig.MACHINE_PRECISION
            && digits < Config.MAX_PRECISION_APFLOAT - 20) {
          IExpr value = engine.evalN(exact, digits + 20);
          if (value instanceof ApfloatNum) {
            return apfloatToContinuedFraction(((ApfloatNum) value).apfloatValue(), digits);
          }
        }
      }
      IExpr evaluated = engine.evaluate(arg1);
      if (evaluated.isList()) {
        // the first argument is held, so it is threaded here when it evaluated to a list
        final IAST finalAST = ast;
        return F.mapList((IAST) evaluated, x -> finalAST.setAtCopy(1, x));
      }
      if (evaluated.equals(arg1)) {
        return evaluateArgument(ast, engine);
      }
      // an unevaluated result keeps the evaluated argument
      IAST evaluatedAST = ast.setAtCopy(1, evaluated);
      return evaluateArgument(evaluatedAST, engine).orElse(evaluatedAST);
    }

    /**
     * <code>ContinuedFraction(x)</code> or <code>ContinuedFraction(x, n)</code> for an evaluated
     * <code>x</code>.
     */
    private IExpr evaluateArgument(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isComplex() || arg1.isComplexNumeric()) {
        // The value `1` is not a real number.
        return Errors.printMessage(S.ContinuedFraction, "realx", F.list(arg1), engine);
      }

      int maxNTerms = Integer.MAX_VALUE;
      if (ast.isAST2()) {
        if (ast.arg2().isNumber()) {
          maxNTerms = ast.arg2().toMachineInt();
          if (maxNTerms <= 0) {
            // Positive integer (less equal 2147483647) expected at position `2` in `1`.
            return Errors.printMessage(S.ContinuedFraction, "intpm", F.list(ast, F.C2), engine);
          }
        } else {
          return F.NIL;
        }
      }

      IExpr[] surd = quadraticSurd(arg1);
      if (surd != null) {
        return continuedFractionQuadratic((IRational) surd[0], (IRational) surd[1],
            ((IInteger) surd[2]).toBigNumerator(), maxNTerms, engine);
      }

      if (arg1 instanceof ApfloatNum && maxNTerms == Integer.MAX_VALUE) {
        Apfloat value = ((ApfloatNum) arg1).apfloatValue();
        // a number of infinite precision has no interval of uncertainty, it takes the general way
        if (value.precision() < Integer.MAX_VALUE) {
          return apfloatToContinuedFraction(value, value.precision());
        }
      }
      if (arg1 instanceof INum && maxNTerms == Integer.MAX_VALUE) {
        // arg1 = F.fraction(((INum) arg1).getRealPart());
        return realToContinuedFraction(((INum) arg1), maxNTerms, engine);
      } else if (!arg1.isRational() && arg1.isNumericFunction(true)) {

        final IExpr num;
        if (maxNTerms < Integer.MAX_VALUE) {
          // a term takes about one digit, more for numbers like E whose terms grow; the digits are
          // bounded by the precision a calculation may ask for, and only the terms which a less
          // precise value gives too are reliable
          long digits = Math.min(Config.MAX_PRECISION_APFLOAT, 30L + 2L * maxNTerms);
          IExpr value = engine.evalN(arg1, digits);
          IExpr check = engine.evalN(arg1,
              Math.max(org.matheclipse.parser.client.ParserConfig.MACHINE_PRECISION + 1, digits - 10));
          if (value instanceof ApfloatNum && check instanceof ApfloatNum) {
            return apfloatToContinuedFraction(((ApfloatNum) value).apfloatValue(),
                ((ApfloatNum) check).apfloatValue(), maxNTerms, engine);
          }
          num = value;
        } else {
          num = engine.evalN(arg1);
        }

        if (num instanceof INum) {
          return realToContinuedFraction(((INum) num), maxNTerms, engine);
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
        return rationalToContinuedFraction(numerator, denominator, isNegative, maxNTerms, false);
      }

      return F.NIL;
    }

    /**
     * The terms of the continued fraction of <code>apfloat</code> which the less precise
     * <code>check</code> of the same number gives too.
     */
    private static IExpr apfloatToContinuedFraction(final Apfloat apfloat, final Apfloat check,
        int maxNTerms, EvalEngine engine) {
      Apint[] terms = ApfloatMath.continuedFraction(apfloat, maxNTerms);
      Apint[] checkTerms = ApfloatMath.continuedFraction(check, maxNTerms);
      int agree = 0;
      while (agree < terms.length && agree < checkTerms.length
          && terms[agree].equals(checkTerms[agree])) {
        agree++;
      }
      // the last common term may still be cut short in both
      int length = agree < terms.length ? Math.max(1, agree - 1) : agree;
      Apint[] continuedFraction = java.util.Arrays.copyOf(terms, length);
      if (length < maxNTerms) {
        // Warning: `1` terminated before `2` terms.
        Errors.printMessage(S.ContinuedFraction, "incompCF",
            F.list(S.ContinuedFraction, F.ZZ(maxNTerms)), engine);
      }
      IASTAppendable continuedFractionList = F.ListAlloc(maxNTerms + 1);
      for (int i = 0; i < length; i++) {
        continuedFractionList.append(F.ZZ(continuedFraction[i].toBigInteger()));
      }
      return continuedFractionList;
    }

    /**
     * The terms of the continued fraction which all numbers in the interval of uncertainty of an
     * arbitrary precision number share: the value <code>x</code> stands for the numbers within
     * <code>|x|*10^(-precision)</code>, and at least within one unit of its last digit.
     *
     * @param value the number, possibly with guard digits beyond <code>precision</code>
     * @param precision the number of significant decimal digits
     */
    private static IAST apfloatToContinuedFraction(Apfloat value, long precision) {
      boolean isNegative = value.signum() < 0;
      java.math.BigDecimal x = new java.math.BigDecimal(ApfloatMath.abs(value).toString(true));
      if (x.signum() == 0) {
        return F.list(F.C0);
      }
      java.math.BigDecimal radius = x.movePointLeft((int) precision);
      java.math.BigDecimal ulp = x.ulp();
      if (ulp.compareTo(radius) > 0) {
        radius = ulp;
      }
      BigInteger[] lo = toFraction(x.subtract(radius));
      BigInteger[] hi = toFraction(x.add(radius));
      IASTAppendable continuedFractionList = F.ListAlloc(10);
      while (lo[1].signum() != 0 && hi[1].signum() != 0) {
        BigInteger[] qrLo = lo[0].divideAndRemainder(lo[1]);
        BigInteger[] qrHi = hi[0].divideAndRemainder(hi[1]);
        if (!qrLo[0].equals(qrHi[0])) {
          break;
        }
        continuedFractionList.append(F.ZZ(isNegative ? qrLo[0].negate() : qrLo[0]));
        if (qrLo[1].signum() == 0 || qrHi[1].signum() == 0) {
          break;
        }
        // the reciprocals of the remainders swap the ends of the interval
        BigInteger[] nextLo = new BigInteger[] {hi[1], qrHi[1]};
        hi = new BigInteger[] {lo[1], qrLo[1]};
        lo = nextLo;
      }
      if (continuedFractionList.isEmpty()) {
        // an interval around an integer
        BigInteger integer = x.setScale(0, java.math.RoundingMode.HALF_EVEN).toBigInteger();
        continuedFractionList.append(F.ZZ(isNegative ? integer.negate() : integer));
      }
      return continuedFractionList;
    }

    /** The numerator and denominator of a non-negative decimal number. */
    private static BigInteger[] toFraction(java.math.BigDecimal decimal) {
      if (decimal.scale() <= 0) {
        return new BigInteger[] {decimal.toBigIntegerExact(), BigInteger.ONE};
      }
      return new BigInteger[] {decimal.unscaledValue(), BigInteger.TEN.pow(decimal.scale())};
    }

    private static IAST realToContinuedFraction(INum value, int precision, EvalEngine engine) {
      double doubleValue = value.getRealPart();
      if (value.isNumIntValue()) {
        return F.list(F.ZZ((int) Math.rint(doubleValue)));
      }
      if (Double.isInfinite(doubleValue)) {
        // an infinity has no continued fraction expansion to give.
        // BigFraction refuses it with "cannot convert infinite value", which used to escape.
        return F.CEmptyList;
      }
      if (Double.isNaN(doubleValue)) {
        return F.NIL;
      }
      boolean isNegative = doubleValue < 0;
      if (isNegative) {
        doubleValue = Math.abs(doubleValue);
      }
      final BigFraction numeratorDenominator = new BigFraction(doubleValue);
      return rationalToContinuedFraction(numeratorDenominator.getNumerator(),
          numeratorDenominator.getDenominator(), isNegative, precision, true);
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
      newSymbol.setAttributes(Attribute.HOLDFIRST, Attribute.LISTABLE);
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
      final int size = ast.size();
      if (size < 3) {
        return S.False;
      }
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


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  private static class DedekindNumber extends AbstractTrigArg1 {

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger() && arg1.isNonNegativeResult()) {
        int n = arg1.toIntDefault();
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
  private static class DiracDelta extends AbstractFunctionEvaluator {

    public DiracDelta() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 1) {
        IASTAppendable newArgs = F.ast(S.DiracDelta);
        IExpr globalMultiplier = F.C1;
        boolean changed = false;

        // Loop through all arguments for multivariate DiracDelta
        for (int i = 1; i <= ast.argSize(); i++) {
          IExpr arg = ast.get(i);

          // 1. DiracDelta(..., c, ...) -> 0 for non-zero REAL numbers
          if (arg.isNumber()) {
            IExpr im = engine.evaluate(F.Im(arg));
            if (im.isZero()) {
              if (!arg.isZero()) {
                return F.C0; // If any argument evaluates to 0, the entire product is 0
              }
            }
            newArgs.append(arg);
            continue;
          }
          if (arg.isNonZeroRealResult()) {
            return F.C0;
          }
          IExpr currentMultiplier = F.C1;
          IExpr newArg = arg;

          // 2. DiracDelta(a * x) -> 1/Abs(a) * DiracDelta(x)
          if (arg.isTimes()) {
            IAST times = (IAST) arg;
            IExpr coeff = times.arg1();
            if (coeff.isNumericFunction()) {
              IExpr im = engine.evaluate(F.Im(coeff));
              if (im.isZero()) { // Only apply to reals
                IExpr absA = engine.evaluate(F.Abs(coeff));
                IExpr rest = times.removeAtCopy(1);
                if (rest.isAST(S.Times, 2)) {
                  rest = ((IAST) rest).arg1();
                }
                currentMultiplier = F.Divide(F.C1, absA);
                newArg = rest;
              }
            }
          }
          // 3. DiracDelta(a * x + b) -> 1/Abs(a) * DiracDelta(x + b/a)
          else if (arg.isPlus()) {
            IAST plus = (IAST) arg;
            IExpr lastTerm = plus.last();

            if (lastTerm.isTimes()) {
              IExpr coeff = ((IAST) lastTerm).arg1();
              if (coeff.isNumericFunction() && !coeff.isOne()) {
                IExpr im = engine.evaluate(F.Im(coeff));
                if (im.isZero()) { // Only apply to reals
                  IExpr absA = engine.evaluate(F.Abs(coeff));
                  currentMultiplier = F.Divide(F.C1, absA);
                  newArg = engine.evaluate(F.Expand(F.Divide(plus, coeff)));
                }
              }
            }
          }

          // Accumulate scaling factors if this specific argument was modified
          if (!currentMultiplier.isOne() || newArg != arg) {
            changed = true;
            globalMultiplier = engine.evaluate(F.Times(globalMultiplier, currentMultiplier));
          }
          newArgs.append(newArg);
        }

        // If any argument was transformed, return the scaled AST
        if (changed) {
          if (globalMultiplier.isOne()) {
            return newArgs;
          }
          return engine.evaluate(F.Times(globalMultiplier, newArgs));
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE);
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
      for (int i = 2; i < ast.size(); i++) {
        if (ast.get(i).isZero()) {
          // The argument `1` in `2` should be nonzero.
          return Errors.printMessage(S.Divisible, "divz", F.list(ast.get(i), ast), engine);
        }
      }
      if (ast.argSize() > 2) {
        // Divisible(n1, n2, n3, ...): each argument is divisible by the next one
        for (int i = 1; i < ast.argSize(); i++) {
          IExpr divisible = engine.evaluate(F.Divisible(ast.get(i), ast.get(i + 1)));
          if (divisible.isFalse()) {
            return S.False;
          }
          if (!divisible.isTrue()) {
            return F.NIL;
          }
        }
        return S.True;
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
      if (result.isNumericFunction()) {
        try {
          Complex evalfc = result.evalfc();
          Num re = F.num(evalfc.getReal());
          Num im = F.num(evalfc.getImaginary());
          return (isRealDivisible(re).isTrue() && isRealDivisible(im).isTrue()) //
              ? S.True
              : S.False;
        } catch (ArgumentTypeException e) {
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
            IASTAppendable sum = F.PlusAlloc(list.size() + 1);
            INumber sumNumber = F.C0;
            sum.append(sumNumber);
            for (int i = 1; i < list.size(); i++) {
              IExpr divisor = list.get(i);
              // apply condition on divisor
              if (condition.isPresent() && !engine.evalTrue(condition, divisor)) {
                continue;
              }
              IExpr term = engine.evaluate(F.unaryAST1(head, divisor));
              if (term.isNumber()) {
                sumNumber = sumNumber.plus((INumber) term);
              } else {
                sum.append(term);
              }
            }
            if (sum.argSize() == 0) {
              return sumNumber;
            }
            sum.set(1, sumNumber);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class DivisorSigma extends AbstractFunctionOptionEvaluator
      implements IntBinaryOperator {
    @Override
    public int applyAsInt(int k, int n) {
      if (n <= 0)
        throw new IllegalArgumentException("DivisorSigma n <= 0");
      IAST list = F.ZZ(n).divisors();
      if (list.isList()) {
        int size = list.size();
        if (k == 1) {
          IInteger sum = F.C0;
          for (int i = 1; i < size; i++) {
            sum = sum.add(((IInteger) list.get(i)));
          }
          return sum.toInt();
        }

        long kl = k;

        IInteger sum = F.C0;
        for (int i = 1; i < size; i++) {
          sum = sum.add(((IInteger) list.get(i)).powerRational(kl));
        }
        return sum.toInt();
      }
      throw new IllegalArgumentException("DivisorSigma undefined");
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      boolean gaussian = options[0].isTrue();
      if (arg2.isComplex() && originalAST
          .exists(x -> x.isRuleAST() && x.first() == S.GaussianIntegers && x.second().isFalse())) {
        // a Gaussian integer with GaussianIntegers->False
        // Inappropriate parameter: `1`.
        return Errors.printMessage(S.DivisorSigma, "par",
            F.list(F.Rule(S.GaussianIntegers, S.False)), engine);
      }
      if (gaussian || arg2.isComplex()) {
        // over the Gaussian integers: the divisors are the products of powers of the prime
        // factors, each prime in the first quadrant, so the sum is the product of the sums
        // 1+p^k+p^(2*k)+...+p^(e*k)
        IAST factors;
        if (arg2.isInteger() && !arg2.isZero()) {
          IInteger n = (IInteger) arg2;
          factors = GaussianInteger.factorize(n, n.toBigNumerator(), BigInteger.ZERO);
        } else if (GaussianInteger.isGaussianInteger(arg2)) {
          IComplex c = (IComplex) arg2;
          factors = GaussianInteger.factorize(c, ((IInteger) c.re()).toBigNumerator(),
              ((IInteger) c.im()).toBigNumerator());
        } else {
          return F.NIL;
        }
        IASTAppendable product = F.TimesAlloc(factors.argSize());
        for (IExpr factor : factors) {
          IExpr p = factor.first();
          if (p.isOne() || p.isMinusOne() || p.equals(F.CI) || p.equals(F.CNI)) {
            continue;
          }
          int e = factor.second().toIntDefault();
          if (e <= 0) {
            return F.NIL;
          }
          IASTAppendable sum = F.PlusAlloc(e + 1);
          for (int a = 0; a <= e; a++) {
            sum.append(F.Power(p, F.Times(F.ZZ(a), arg1)));
          }
          product.append(sum);
        }
        return engine.evaluate(F.Expand(product.oneIdentity1()));
      }
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
      newSymbol.setAttributes(Attribute.LISTABLE);
      setOptions(newSymbol, S.GaussianIntegers, S.False);
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
          int nMax = arg1.toMachineInt();
          if (F.isPresent(nMax)) {
            try {
              IExpr n = arg1;
              // getEulerEEvenOdd() reads index k/2 for k in 0..nMax, so half the table answers
              // every term of the sum below - and each entry it does not need is an integer with
              // thousands of digits
              ArrayList<IInteger> eulerE = eulerEList(nMax / 2);
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
        }
        return F.NIL;
      }
      if (arg1.isInteger()) {
        int n = arg1.toIntDefault(-1);
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
      if (n > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(n);
      }
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
        if (Thread.currentThread().isInterrupted()) {
          // one row is O(n) products of integers with thousands of digits: without this the table
          // outlives the deadline that asked for it and the thread runs on after the caller has
          // its $Aborted
          throw TimeoutException.TIMED_OUT;
        }
        IInteger val = F.C0;
        boolean sigPos = true;
        int thisn = a.size();
        for (int i = thisn - 1; i > 0; i--) {
          IInteger f = a.get(i);
          f = f.multiply(AbstractIntegerSym.binomial(2 * thisn, 2 * i));
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class EulerPhi extends AbstractTrigArg1 implements IntUnaryOperator {
    @Override
    public int applyAsInt(int arg) {
      if (arg == 0) {
        return 0; // Standard definition handling or throw an exception
      }
      return F.ZZ(arg).eulerPhi().toIntDefault();
    }

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger()) {
        try {
          return ((IInteger) arg1).eulerPhi();
        } catch (ArithmeticException e) {
          // integer to large?
        }
      } else {
        if (arg1.isProbablePrimeResult()) {
          // fungrim "cb410e"
          return F.Plus(F.CN1, arg1);
        }
        if (arg1.isPower() && arg1.exponent().isIntegerResult()
            && arg1.base().isProbablePrimeResult()) {
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
      if (ast.exists(GaussianInteger::isGaussianInteger)
          && ast.forAll(x -> x.isInteger() || GaussianInteger.isGaussianInteger(x))) {
        return gaussianExtendedGCD(ast);
      }
      if (ast.forAll(x -> x.isInteger()) && !ast.forAll(x -> x.isPositive())) {
        return signedExtendedGCD(ast, engine);
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

    private static IInteger[] gaussianParts(IExpr x) {
      if (x.isInteger()) {
        return new IInteger[] {(IInteger) x, F.C0};
      }
      IComplex c = (IComplex) x;
      return new IInteger[] {(IInteger) c.re(), (IInteger) c.im()};
    }

    private static IExpr gaussianExpr(IInteger[] g) {
      return g[1].isZero() ? g[0] : F.CC(g[0], g[1]);
    }

    /**
     * The gcd of Gaussian integers and its Bezout coefficients, from the extended Euclidean
     * algorithm with the rounded quotient of <code>Mod</code>, folded over the arguments.
     */
    private static IExpr gaussianExtendedGCD(IAST ast) {
      IInteger[] g = gaussianParts(ast.arg1());
      java.util.List<IInteger[]> coefficients = new java.util.ArrayList<IInteger[]>();
      coefficients.add(new IInteger[] {F.C1, F.C0});
      for (int i = 2; i < ast.size(); i++) {
        IInteger[][] gst = GaussianInteger.extendedGcd(g, gaussianParts(ast.get(i)));
        for (int j = 0; j < coefficients.size(); j++) {
          IInteger[] c = coefficients.get(j);
          IInteger[] s = gst[1];
          coefficients.set(j, new IInteger[] {c[0].multiply(s[0]).subtract(c[1].multiply(s[1])),
              c[0].multiply(s[1]).add(c[1].multiply(s[0]))});
        }
        coefficients.add(gst[2]);
        g = gst[0];
      }
      IASTAppendable list = F.ListAlloc(coefficients.size());
      for (IInteger[] c : coefficients) {
        list.append(gaussianExpr(c));
      }
      return F.list(gaussianExpr(g), list);
    }

    /**
     * The extended gcd of integers which may be negative or zero: the one of their absolute values,
     * with the sign of an argument on its coefficient and <code>0</code> for a zero argument.
     */
    private static IExpr signedExtendedGCD(IAST ast, EvalEngine engine) {
      final int n = ast.argSize();
      java.util.List<Integer> nonZero = new java.util.ArrayList<Integer>();
      for (int i = 1; i <= n; i++) {
        if (!ast.get(i).isZero()) {
          nonZero.add(i);
        }
      }
      IInteger[] coefficients = new IInteger[n];
      java.util.Arrays.fill(coefficients, F.C0);
      if (nonZero.isEmpty()) {
        return F.list(F.C0, F.List(coefficients));
      }
      BigInteger gcd;
      if (nonZero.size() == 1) {
        int i = nonZero.get(0);
        IInteger a = (IInteger) ast.get(i);
        coefficients[i - 1] = a.isNegative() ? F.CN1 : F.C1;
        return F.list(a.abs(), F.List(coefficients));
      }
      BigInteger[] args = new BigInteger[nonZero.size()];
      for (int k = 0; k < args.length; k++) {
        args[k] = ((IInteger) ast.get(nonZero.get(k))).toBigNumerator().abs();
      }
      BigInteger[] bezout = new BigInteger[args.length];
      try {
        gcd = extendedGCD(args, bezout);
      } catch (ArithmeticException ae) {
        return F.NIL;
      }
      for (int k = 0; k < args.length; k++) {
        int i = nonZero.get(k);
        IInteger c = F.ZZ(bezout[k]);
        coefficients[i - 1] = ((IInteger) ast.get(i)).isNegative() ? c.negate() : c;
      }
      return F.list(F.ZZ(gcd), F.List(coefficients));
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class Factorial extends AbstractEvaluator
      implements IFunctionExpand, INumeric, IntUnaryOperator {
    @Override
    public int applyAsInt(int n) {
      if (n < 0 || n > 12) {
        throw new ArithmeticException("Factorial out of 32-bit int bounds");
      }
      int fact = 1;
      for (int i = 2; i <= n; i++)
        fact *= i;
      return fact;
    }

    @Override
    public IExpr numericFunction(IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.factorial();
      }
      return F.NIL;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr z = ast.arg1();
        // Gamma(1+z)
        return F.Gamma(F.Plus(F.C1, z));
      }
      return F.NIL;
    }

    @Override
    public double evalReal(double[] stack, int top, int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Num.factorial(stack[top]);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isInteger()) {
        if (z.isNegative()) {
          return F.CComplexInfinity;
        }
        return ((IInteger) z).factorial();
      }
      if (z.isFraction()) {
        IFraction frac = (IFraction) z;
        if (z.equals(F.C1D2)) {
          return F.Times(F.C1D2, F.Sqrt(S.Pi));
        }
        if (z.equals(F.CN1D2)) {
          return F.Sqrt(S.Pi);
        }

        if (frac.denominator().equals(F.C2)) {
          return F.Gamma(frac.inc());
        }
      }
      if (z.isDirectedInfinity()) {
        if (z.isInfinity()) {
          return F.CInfinity;
        }
        if (z.isNegativeInfinity()) {
          return S.Indeterminate;
        }
        if (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI)) {
          return F.C0;
        }
        if (z.isComplexInfinity()) {
          return S.Indeterminate;
        }
      }
      return F.NIL;
    }


    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }

  }


  /**
   * <pre>
   * <code>FactorialPower(v, n)
   * </code>
   * </pre>
   * 
   * <p>
   * The <code>FactorialPower</code> implements the falling factorial. The falling factorial
   * (sometimes called the descending factorial, falling sequential product, or lower factorial) is
   * defined as the polynomial <code>v*(v-1)*(v-2)*...*(v-n+1)</code>.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Falling_and_rising_factorials">Wikipedia - Falling
   * and rising factorials</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; FactorialPower(v,4) // FunctionExpand 
   * (-3+v)*(-2+v)*(-1+v)*v
   * </code>
   * </pre>
   */
  private static class FactorialPower extends AbstractEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && ast.arg2().isInteger() && ast.arg2().isNonNegativeResult()) {
        return factorialPower2(ast.arg1(), ast.arg2(), engine);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr v = ast.arg1();
      IExpr n = ast.arg2();
      if (ast.isAST2()) {
        if (v.isInteger() && n.isInteger() && v.isNonNegativeResult() && n.isNonNegativeResult()) {
          if (((IInteger) v).isLT((IInteger) n)) {
            return F.C0;
          }
          if (v.equals(n)) {
            if (v.isZero()) {
              return F.C1;
            }
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
        // also the integer which a numeric evaluation has turned into an inexact number
        if (n.isInteger() || (n.isReal() && ((IReal) n).isNumIntValue())) {
          if (ast.isAST2() && v.isNumber() && F.isPresent(ni)) {
            // FactorialPower(x, -k) == 1/((x+1)*(x+2)*...*(x+k))
            int k = -ni;
            int iterationLimit = engine.getIterationLimit();
            if (iterationLimit >= 0 && iterationLimit <= k) {
              IterationLimitExceeded.throwIt(k, ast);
            }
            IASTAppendable product = F.TimesAlloc(k);
            for (int i = 1; i <= k; i++) {
              product.append(F.Plus(v, F.ZZ(i)));
            }
            return F.Power(product, F.CN1);
          }
          return F.NIL;
        }
      }

      if (ast.isAST2()) {
        return factorialPower2(v, n, engine);
        //
        // IExpr result = F.C1;
        //
        // // x*(x-1)*(x-(n-1))
        // if (engine.evalLess(n, F.C0)) {
        // return F.NIL;
        // } else if (n.isZero()) {
        // return F.C1;
        // } else if (n.isOne()) {
        // return v;
        // } else {
        // if (engine.isDoubleMode()) {
        // if (!v.isMathematicalIntegerNegative()) {
        // Complex cx = v.evalfc();
        // Complex cn = n.evalfc();
        // cx = cx.add(1.0);
        // cn = cx.subtract(cn);
        // if (!(cn.isMathematicalInteger() && cn.getReal() < 0.0)) {
        // // Gamma(1+x)/Gamma(1-n+x)
        // return F.Divide(F.Gamma(F.complexNum(cx)), F.Gamma(F.complexNum(cn)));
        // }
        // }
        // double real = Double.NaN;
        // try {
        // real = v.evalf();
        // } catch (ArgumentTypeException ate) {
        // Complex temp = v.evalfc();
        // if (temp == null) {
        // return F.NIL;
        // }
        // real = temp.getReal();
        // }
        // if (Double.isNaN(real)) {
        // return F.NIL;
        // }
        // long iterationLimit = EvalEngine.get().getIterationLimit();
        // long k = 0L;
        // double dN = n.evalf();
        // double i = real - dN + 1;
        // while (real >= i) {
        // result = result.multiply(v);
        // v = v.dec();
        // real--;
        // if (k++ > iterationLimit && iterationLimit > 0) {
        // IterationLimitExceeded.throwIt(k, S.FactorialPower);
        // }
        // }
        // return result;
        // } else if (v.isExactNumber() && n.isRational()) {
        // IRational real = (IRational) ((INumber) v).re();
        // IRational dN = (IRational) n;
        // IRational i = real.subtract(dN).inc();
        // while (real.isGE(i)) {
        // result = result.multiply(v);
        // v = v.dec();
        // real = real.dec();
        // }
        // return result;
        // }
        // }
        // return F.NIL;
      }

      if (ast.isAST3()) {
        IExpr result = F.C1;
        IExpr h = ast.arg3();
        if (h.isZero()) {
          return F.Power(v, n);
        }
        // x*(x-h)* (x-(n-1)*h)
        if (engine.evalTrue(F.Less(n, F.C0))) {
          return F.NIL;
        } else if (n.isZero()) {
          return F.C1;
        } else if (n.isOne()) {
          return v;
        } else {
          if (!engine.isDoubleMode() && v.isNumber() && n.isNumber() && h.isNumber()
              && (v.isInexactNumber() || n.isInexactNumber() || h.isInexactNumber())) {
            // arbitrary precision numbers: h^n*Gamma(x/h+1)/Gamma(x/h+1-n)
            IExpr ratio = F.Divide(v, h);
            return F.Times(F.Power(h, n), F.Gamma(F.Plus(F.C1, ratio)),
                F.Power(F.Gamma(F.Plus(F.C1, ratio, F.Negate(n))), F.CN1));
          }
          if (engine.isDoubleMode()) {
            if (!v.isMathematicalIntegerNegative()) {
              Complex cx = v.evalfc();
              Complex cn = n.evalfc();
              Complex ch = h.evalfc();

              Complex cxDch = cx.divide(ch);
              Complex cxDchPlus1 = cxDch.add(1.0);
              Complex cxDchPlus1SubtractN = cxDchPlus1.subtract(cn);
              Complex factor = cx.pow(cn).divide(cxDch.pow(cn));
              if (!(cxDchPlus1.isMathematicalInteger() && cxDchPlus1.getReal() < 0.0)) {
                // (x^n*Gamma(1+x/h))/((x/h)^n*Gamma(1-n+x/h))
                if (factor.getImaginary() == 0.0 && cxDchPlus1.getImaginary() == 0.0
                    && cxDchPlus1SubtractN.getImaginary() == 0.0) {
                  // real numbers have a real value
                  return F.Divide(
                      F.Times(F.num(factor.getReal()), F.Gamma(F.num(cxDchPlus1.getReal()))),
                      F.Gamma(F.num(cxDchPlus1SubtractN.getReal())));
                }
                return F.Divide(F.Times(F.complexNum(factor), F.Gamma(F.complexNum(cxDchPlus1))),
                    F.Gamma(F.complexNum(cxDchPlus1SubtractN)));
              }
            }
            double real = v.evalfNaN();
            if (Double.isNaN(real)) {
              Complex temp = v.evalfc();
              if (temp == null) {
                return F.NIL;
              }
              real = temp.getReal();
            }
            if (Double.isNaN(real)) {
              return F.NIL;
            }
            double dN = n.evalfNaN();
            double doubleH = h.evalfNaN();
            if (Double.isNaN(dN) || Double.isNaN(doubleH)) {
              return F.NIL;
            }
            if (h.isZero()) {
              while (n.isPositive()) {// engine.evalTrue(F.Greater(n, F.C0))) {
                result = result.multiply(v);
                n = n.dec();
              }
              return result;
            } else if (h.isPositive()) {// engine.evalTrue(F.Greater(h, F.C0))) {
              long iterationLimit = EvalEngine.get().getIterationLimit();
              long k = 0L;
              double i = real - (dN - 1) * doubleH;
              while (real >= i) {
                result = result.multiply(v);
                v = v.minus(h);
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
                result = result.multiply(v);
                v = v.minus(h);
                real -= doubleH;
                if (k++ > iterationLimit && iterationLimit > 0) {
                  IterationLimitExceeded.throwIt(k, S.FactorialPower);
                }
              }
              return result;
            }

          } else if (v.isExactNumber() && n.isRational() && h.isRational()) {
            IRational real = (IRational) ((INumber) v).re();
            IRational dN = (IRational) n;
            IRational H = (IRational) h;
            if (H.isZero()) {
              while (dN.isGT(F.C0)) {
                result = result.multiply(v);
                dN = dN.dec();
              }
              return result;
            }
            if (H.isGT(F.C0)) {
              IRational i = real.subtract(dN.dec().multiply(H));
              while (real.isGE(i)) {
                result = result.multiply(v);
                v = v.minus(H);
                real = real.subtract(H);
              }
              return result;
            }
            IRational i = real.subtract(dN.dec().multiply(H));
            while (real.isLE(i)) {
              result = result.multiply(v);
              v = v.minus(H);
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
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
  private static class Factorial2 extends AbstractEvaluator implements IFunctionExpand {
    @Override
    public IExpr numericFunction(IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return functionExpand(ast, engine);
      }
      return F.NIL;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr n = ast.arg1();
        // 2^(n/2+1/4*(1-Cos(n*Pi)))*Pi^(1/4*(-1+Cos(n*Pi)))*Gamma(1+n/2)
        IExpr cosNPi = F.Cos(F.Times(n, F.Pi));
        IExpr halfN = F.Times(F.C1D2, n);
        return F.Times(F.Power(F.C2, F.Plus(halfN, F.Times(F.C1D4, F.Subtract(F.C1, cosNPi)))),
            F.Power(F.Pi, F.Times(F.C1D4, F.Plus(F.CN1, cosNPi))), F.Gamma(F.Plus(F.C1, halfN)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      long n = arg1.toLongDefault();
      if (F.isPresent(n)) {
        long nAbs = n < 0 ? -n : n;
        final int iterationLimit = engine.getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit < nAbs) {
          IterationLimitExceeded.throwIt(nAbs, F.Factorial2(F.ZZ(n)));
        }
        return factorial2(n);
      }

      if (arg1.isInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isNegativeInfinity()) {
        return S.Indeterminate;
      }
      if (arg1.isDirectedInfinity(F.CI) || arg1.isDirectedInfinity(F.CNI)) {
        return F.CComplexInfinity;
      }
      if (arg1.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    private static IExpr factorial2(long n) {
      if (n < 0) {
        if ((n & 1) == 0) {
          // n is even and negative => ComplexInfinity
          return F.CComplexInfinity;
        }
        // n is odd and negative
        if (n >= -35) {
          if (n == -1) {
            return F.C1;
          }
          long sign = (((-n + 1) % 4) == 0) ? -1 : 1;
          return F.QQ(sign, FACTORIAL2_34[(int) (-n - 2L)]);
        }
        BigInteger result = BigInteger.ONE;
        for (long i = -1; i > n; i -= 2) {
          result = result.multiply(BigInteger.valueOf(i));
        }
        return F.QQ(BigInteger.ONE, result);
      }
      if (n < 34) {
        return AbstractIntegerSym.valueOf(FACTORIAL2_34[(int) n]);
      }
      BigInteger result;
      final int start;
      if ((n & 1) == 0) { // is even
        start = 34;
        result = BigInteger.valueOf(FACTORIAL2_34[32]);
      } else {
        start = 35;
        result = BigInteger.valueOf(FACTORIAL2_34[33]);
      }
      // final int start = ((n & 1) == 0) ? 2 : 3;
      for (long i = start; i <= n; i += 2) {
        result = result.multiply(BigInteger.valueOf(i));
      }
      return F.ZZ(result);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
      if (ast.size() >= 2 && ast.size() <= 4) {
        IExpr arg1 = ast.arg1();
        if (ast.size() == 2) {
          if (arg1.isRational()) {
            return ((IRational) arg1).factorInteger();
          }
          return F.NIL;
        }
        if (!ast.arg2().isRuleAST()) {
          // FactorInteger(n, k)
          IExpr kSpec = ast.arg2();
          if (ast.size() > 3) {
            // FactorInteger(n, 1, options)
            if (kSpec.isOne() && ast.arg3().isRuleAST() && arg1.isNumber()) {
              return F.list(F.list(arg1, F.C1));
            }
            return F.NIL;
          }
          if (!arg1.isRational()) {
            return F.NIL;
          }
          if (kSpec == S.All || kSpec.isInfinity()) {
            return ((IRational) arg1).factorInteger();
          }
          int k = kSpec.toIntDefault();
          if (F.isNotPresent(k) && kSpec.isInteger() && kSpec.isPositive()) {
            // more factors than any number has
            k = Integer.MAX_VALUE;
          }
          if (k <= 0) {
            return F.NIL;
          }
          if (k == 1) {
            return F.list(F.list(arg1, F.C1));
          }
          return partialFactorization((IRational) arg1, k);
        }
        if (ast.size() == 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
          IExpr option = options.getOption(S.GaussianIntegers);
          if (option.isTrue()) {
            BigInteger re = BigInteger.ONE;
            if (arg1.isInteger()) {
              re = ((IInteger) arg1).toBigNumerator();
              return GaussianInteger.factorize((IInteger) arg1, re, BigInteger.ZERO);
            } else if (arg1.isComplex()) {
              IComplex c = (IComplex) arg1;
              IRational real = c.getRealPart();
              IRational imaginary = c.getImaginaryPart();
              if (real.isInteger() && imaginary.isInteger()) {
                re = ((IInteger) real).toBigNumerator();
                BigInteger im = ((IInteger) imaginary).toBigNumerator();
                return GaussianInteger.factorize((IComplex) arg1, re, im);
              }
            }
          }
        }
      }
      return F.NIL;
    }


    /** Primes below this bound are found by trial division; the rest form the cofactor. */
    private static final BigInteger TRIAL_BOUND = BigInteger.valueOf(65536);

    private static final int[] TRIAL_PRIMES = PrimeCount.primesUpTo(65535);

    /**
     * The factorization of <code>q</code> into at most <code>k</code> factors for the numerator and
     * for the denominator. The largest prime powers are kept; the smallest are multiplied into
     * one factor with exponent 1. A cofactor without prime factors below <code>2^16</code> stays
     * one factor unless there is room to split it, smallest primes first.
     */
    private static IExpr partialFactorization(IRational q, int k) {
      if (q.isZero() || q.isOne() || q.isMinusOne()) {
        return q.factorInteger();
      }
      // an entry is {numerator of the base, exponent, denominator of the base}
      List<BigInteger[]> entries = new ArrayList<BigInteger[]>();
      BigInteger[] merged = new BigInteger[2];
      int numeratorCount = partialFactors(q.toBigNumerator().abs(), k, false, entries, merged);
      partialFactors(q.toBigDenominator(), k, true, entries, merged);
      if (q.isNegative()) {
        if (merged[0] != null) {
          merged[0] = merged[0].negate();
        } else if (merged[1] != null) {
          merged[1] = merged[1].negate();
        } else if (numeratorCount == 0) {
          entries.add(new BigInteger[] {BigInteger.ONE.negate(), BigInteger.ONE, BigInteger.ONE});
        } else {
          // without a merged factor the sign goes into the largest numerator factor
          BigInteger[] largest = null;
          for (BigInteger[] e : entries) {
            if (e[1].signum() > 0 && (largest == null || e[0].compareTo(largest[0]) > 0)) {
              largest = e;
            }
          }
          largest[0] = largest[0].pow(largest[1].intValueExact()).negate();
          largest[1] = BigInteger.ONE;
        }
      }
      if (merged[0] != null) {
        entries.add(new BigInteger[] {merged[0], BigInteger.ONE, BigInteger.ONE});
      }
      if (merged[1] != null) {
        if (merged[1].signum() > 0) {
          entries.add(new BigInteger[] {merged[1], BigInteger.ONE.negate(), BigInteger.ONE});
        } else {
          // with the sign the merged denominator is the base -1/m with exponent 1
          entries.add(
              new BigInteger[] {BigInteger.ONE.negate(), BigInteger.ONE, merged[1].negate()});
        }
      }
      // sort by the value of the base
      entries.sort((x, y) -> x[0].multiply(y[2]).compareTo(y[0].multiply(x[2])));
      IASTAppendable result = F.ListAlloc(entries.size());
      for (BigInteger[] e : entries) {
        result.append(F.list(F.QQ(F.ZZ(e[0]), F.ZZ(e[2])), F.ZZ(e[1])));
      }
      return result;
    }

    /**
     * Appends the factors of <code>n &gt; 1</code> to <code>entries</code>; a product of merged
     * small factors goes to <code>merged[0]</code> (<code>merged[1]</code> for the denominator).
     * Only primes below <code>2^16</code> are divided out; the cofactor is factored only if there
     * is room to split it.
     *
     * @return the number of factors including the merged one
     */
    private static int partialFactors(BigInteger n, int k, boolean denominator,
        List<BigInteger[]> entries, BigInteger[] merged) {
      if (n.equals(BigInteger.ONE)) {
        return 0;
      }
      BigInteger sign = denominator ? BigInteger.ONE.negate() : BigInteger.ONE;
      List<BigInteger[]> small = new ArrayList<BigInteger[]>();
      BigInteger cofactor = trialDivision(n, sign, small);
      boolean hasCofactor = !cofactor.equals(BigInteger.ONE);
      int count = small.size() + (hasCofactor ? 1 : 0);
      if (count <= k) {
        entries.addAll(small);
        if (!hasCofactor) {
          return small.size();
        }
        int room = k - small.size();
        if (room == 1) {
          entries.add(new BigInteger[] {cofactor, sign, BigInteger.ONE});
          return count;
        }
        List<BigInteger[]> large = new ArrayList<BigInteger[]>();
        for (Map.Entry<BigInteger, Integer> entry : Config.PRIME_FACTORS.factorInteger(cofactor)
            .entrySet()) {
          large.add(new BigInteger[] {entry.getKey(),
              BigInteger.valueOf(entry.getValue()).multiply(sign), BigInteger.ONE});
        }
        if (large.size() <= room) {
          entries.addAll(large);
          return small.size() + large.size();
        }
        // split the cofactor, smallest primes first
        BigInteger rest = cofactor;
        for (int i = 0; i < room - 1; i++) {
          BigInteger[] e = large.get(i);
          entries.add(e);
          rest = rest.divide(e[0].pow(e[1].abs().intValueExact()));
        }
        entries.add(new BigInteger[] {rest, sign, BigInteger.ONE});
        return k;
      }
      // k >= 2: keep the cofactor and the largest small prime powers; merge the smallest
      int keep = k - 1 - (hasCofactor ? 1 : 0);
      if (hasCofactor) {
        entries.add(new BigInteger[] {cofactor, sign, BigInteger.ONE});
      }
      BigInteger product = BigInteger.ONE;
      for (int i = 0; i < small.size(); i++) {
        BigInteger[] e = small.get(i);
        if (i >= small.size() - keep) {
          entries.add(e);
        } else {
          product = product.multiply(e[0].pow(e[1].abs().intValueExact()));
        }
      }
      merged[denominator ? 1 : 0] = product;
      return k;
    }

    /**
     * Divides the primes below <code>2^16</code> out of <code>n</code> and appends them as
     * <code>{p, sign*e, 1}</code> to <code>small</code>.
     *
     * @return the cofactor without prime factors below <code>2^16</code>
     */
    private static BigInteger trialDivision(BigInteger n, BigInteger sign,
        List<BigInteger[]> small) {
      BigInteger rest = n;
      for (int p : TRIAL_PRIMES) {
        if (rest.bitLength() < 64) {
          long r = rest.longValue();
          if ((long) p * p > r) {
            break;
          }
          int e = 0;
          while (r % p == 0) {
            r /= p;
            e++;
          }
          if (e > 0) {
            small.add(new BigInteger[] {BigInteger.valueOf(p),
                BigInteger.valueOf(e).multiply(sign), BigInteger.ONE});
            rest = BigInteger.valueOf(r);
          }
          continue;
        }
        BigInteger bp = BigInteger.valueOf(p);
        int e = 0;
        BigInteger[] qr = rest.divideAndRemainder(bp);
        while (qr[1].signum() == 0) {
          rest = qr[0];
          e++;
          qr = rest.divideAndRemainder(bp);
        }
        if (e > 0) {
          small.add(new BigInteger[] {bp, BigInteger.valueOf(e).multiply(sign), BigInteger.ONE});
        }
      }
      if (rest.compareTo(TRIAL_BOUND) < 0 && !rest.equals(BigInteger.ONE)) {
        // a prime below 2^16 is left
        small.add(new BigInteger[] {rest, sign, BigInteger.ONE});
        return BigInteger.ONE;
      }
      return rest;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class Fibonacci extends AbstractFunctionEvaluator
      implements IFunctionExpand, IntUnaryOperator {
    @Override
    public int applyAsInt(int n) {
      // 46th Fibonacci is 1,836,311,903. 47th exceeds Integer.MAX_VALUE
      if (n < 0 || n > 46) {
        throw new ArithmeticException("Fibonacci out of 32-bit int bounds");
      }
      return AbstractIntegerSym.fibonacci(n).toIntDefault();
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr n = ast.arg1();
        if (n.isPlus() && n.first().isInteger()) {
          IASTAppendable[] filter = ((IAST) n).filter(x -> x.isInteger());
          if (filter[0].argSize() > 0) {
            IExpr m = filter[0].oneIdentity0();
            n = filter[1].oneIdentity0();
            if (n.isIntegerResult()) {
              // Fibonacci(m_Integer+n_) := ((1/2)*Fibonacci(m)*LucasL(n) +
              // (1/2)*Fibonacci(n)*LucasL(m)) /; Element(n, Integers)
              return F.Plus(F.Times(F.C1D2, F.Fibonacci(m), F.LucasL(n)),
                  F.Times(F.C1D2, F.Fibonacci(n), F.LucasL(m)));
            }
          }
        }
        // ((1/2*(1+Sqrt(5)))^n-(2/(1+Sqrt(5)))^n*Cos(n*Pi))/Sqrt(5)
        IExpr v1 = F.Plus(F.C1, F.CSqrt5);
        return F.Times(F.C1DSqrt5,
            F.Plus(F.Times(F.Power(F.C1D2, n), F.Power(v1, n)), F.Times(F.CN1, F.Power(F.C2, n),
                F.Power(F.Power(v1, F.CN1), n), F.Cos(F.Times(n, F.Pi)))));
      }
      return F.NIL;
    }

    /**
     * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F See:
     * <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28"> Roseatta code:
     * Fibonacci sequence.</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = arg1.toIntDefault();
        if (n > Integer.MIN_VALUE) {
          if (ast.isAST2()) {
            if (ast.arg2().isSymbol() || ast.arg2().isNumber() || ast.arg2().isAST()) {
              return fibonacciPolynomialIterative(n, ast.arg2(), ast, engine);
            }
            return F.NIL;
          }
          return AbstractIntegerSym.fibonacci(n);
        }
      } else if (arg1.isInexactNumber()) {
        final IInexactNumber n = (IInexactNumber) arg1;
        if (ast.isAST2() && ast.arg2().isInexactNumber()) {
          final IInexactNumber x = (IInexactNumber) ast.arg2();
          return n.fibonacci(x);
          // INumber x = ((INumber) ast.arg2()).evaluatePrecision(engine);
          // return
          // // [$ ((x + Sqrt(4 + x^2))^n/2^n - (2^n*Cos(n*Pi))/(x + Sqrt(4 + x^2))^n)/Sqrt(4 + x^2)
          // $]
          // F.Times(F.Power(F.Plus(F.C4, F.Sqr(x)), F.CN1D2),
          // F.Plus(
          // F.Times(F.Power(F.Power(F.C2, n), F.CN1),
          // F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n)),
          // F.Times(F.CN1, F.Power(F.C2, n),
          // F.Power(F.Power(F.Plus(x, F.Sqrt(F.Plus(F.C4, F.Sqr(x)))), n), F.CN1),
          // F.Cos(F.Times(n, S.Pi))))); // $$;
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
        if (array.length == 1) {
          return F.CInfinity;
        }
        BigInteger gcd = BigIntegerSym.gcd(array);
        if (!gcd.equals(BigInteger.ONE)) {
          return F.CInfinity;
        }
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
      // newSymbol.setAttributes(Attribute.LISTABLE);
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
                    return radSimplify(F.subst(nonPeriodReduced, arg -> arg.equals(x), solution),
                        engine);
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
          result = engine
              .evaluate(F.Together(F.Plus(continuedFractionList.get(i), F.Power(result, F.CN1))));
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


  private static class GoldbachList extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int maxPairs = Integer.MAX_VALUE;
      if (ast.isAST2()) {
        maxPairs = ast.arg2().toMachineInt();
        if (maxPairs <= 0) {
          return F.NIL;
        }
      }
      if (ast.arg1().isInteger()) {
        IInteger n = (IInteger) ast.arg1();
        if (n.isOdd()) {
          return F.CEmptyList;
        }
        IInteger nHalf = n.div(F.C2);
        if (maxPairs == Integer.MAX_VALUE && F.isNotPresent(nHalf.toIntDefault())) {
          // Asked for every pair, the search runs the whole way to n/2 with a primality test per
          // odd number on the way, and the list it would build is longer than anything that could
          // hold it. Asked for a few, it stops at the first ones found, which is why the bound is
          // only checked here - GoldbachList(3325581707333960528, 1) answers at once.
          // Java int value greater equal `1` expected instead of `2`.
          return Errors.printMessage(S.GoldbachList, "intjava", F.list(F.C0, nHalf), engine);
        }
        IASTAppendable list = F.ListAlloc(7);
        long steps = 0;
        for (IInteger i = F.C3; nHalf.isGE(i); i = i.add(2)) {
          if ((++steps & INTERRUPT_CHECK_MASK) == 0 && Thread.currentThread().isInterrupted()) {
            // a search over a billion candidates outlives the deadline that started it, and the
            // thread would go on running after the caller already has its $Aborted
            throw TimeoutException.TIMED_OUT;
          }
          if (i.isProbablePrime()) {
            IInteger j = n.subtract(i);
            if (j.isProbablePrime()) {
              list.append(F.List(i, j));
              if (list.argSize() >= maxPairs) {
                break;
              }
            }
          }
        }
        return list;
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  private static class Hyperfactorial extends AbstractEvaluator {

    /**
     * The largest magnitude of an integer-valued machine number whose hyperfactorial is a machine
     * number.
     */
    private static final int MACHINE_LIMIT = 23;

    /**
     * The largest magnitude of a negative integer-valued arbitrary precision number whose
     * hyperfactorial is computed as an exact product.
     */
    private static final int APFLOAT_LIMIT = 200;

    /**
     * The product <code>1^1*2^2*...*n^n</code> for <code>n &gt;= 0</code>. For negative
     * <code>n</code> it is continued by <code>(-1)^Floor(-n/2)*Hyperfactorial(-n-1)</code>.
     */
    private static IInteger hyperfactorial(int n, EvalEngine engine) {
      if (n < 0) {
        IInteger h = hyperfactorial(-n - 1, engine);
        return ((-n / 2) & 1) == 1 ? h.negate() : h;
      }
      // the product has about Sum(k*Log2(k), {k, 2, n}) bits
      double bits = 0.0;
      for (int k = 2; k <= n; k++) {
        bits += k * Math.log(k) / Math.log(2.0);
        if (bits > Config.MAX_BIT_LENGTH) {
          BigIntegerLimitExceeded.throwIt((long) bits);
        }
      }
      BigInteger result = BigInteger.ONE;
      for (int k = 2; k <= n; k++) {
        engine.checkTimeLimit();
        result = result.multiply(BigInteger.valueOf(k).pow(k));
      }
      return F.ZZ(result);
    }

    /** The digits added to the precision of an arbitrary precision argument while calculating. */
    private static final long GUARD_DIGITS = 10L;

    /**
     * <code>E^(z*LogGamma(z+1))/BarnesG(z+1)</code>. For an arbitrary precision <code>z</code> it
     * is calculated with more digits than <code>z</code> has, because the quotient loses digits,
     * and rounded to the precision of <code>z</code>.
     */
    private static IExpr numericHyperfactorial(INumber z, EvalEngine engine) {
      if (z.isMachineNumber()) {
        IExpr z1 = F.Plus(F.C1, z);
        return F.Divide(F.Exp(F.Times(z, F.LogGamma(z1))), F.BarnesG(z1));
      }
      final Apcomplex value = z.apcomplexValue();
      final long precision = value.precision();
      final long digits =
          Math.max(precision, Math.min(precision + GUARD_DIGITS, Config.MAX_PRECISION_APFLOAT));
      // the exact value of z: a number of the working precision would be rounded back to the
      // precision of the evaluation it occurs in
      IExpr re = exactValue(new java.math.BigDecimal(value.real().toString(true)));
      IExpr exact = z.isReal() ? re
          : F.Complex(re, exactValue(new java.math.BigDecimal(value.imag().toString(true))));
      IExpr z1 = F.Plus(F.C1, exact);
      IExpr result;
      final boolean numericMode = engine.isNumericMode();
      try {
        // in the numeric mode of the argument N keeps its precision
        engine.setNumericMode(false);
        result =
            engine.evalN(F.Divide(F.Exp(F.Times(exact, F.LogGamma(z1))), F.BarnesG(z1)), digits);
      } finally {
        engine.setNumericMode(numericMode);
      }
      if (!result.isNumber()) {
        return result;
      }
      Apcomplex rounded = ((INumber) result).apcomplexValue().precision(precision);
      return result.isReal() ? ApfloatNum.valueOf(rounded.real()) : ApcomplexNum.valueOf(rounded);
    }

    /** The rational number with the value of <code>decimal</code>. */
    private static IExpr exactValue(java.math.BigDecimal decimal) {
      if (decimal.scale() <= 0) {
        return F.ZZ(decimal.toBigIntegerExact());
      }
      return F.QQ(decimal.unscaledValue(), BigInteger.TEN.pow(decimal.scale()));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = arg1.toIntDefault();
        return F.isPresent(n) ? hyperfactorial(n, engine) : F.NIL;
      }
      if (arg1.isMachineNumber() && arg1.isReal() && arg1.isNumIntValue()) {
        // the exact product, without the rounding of the Gamma function formula
        int n = arg1.toIntDefault();
        if (F.isPresent(n) && Math.abs(n) <= MACHINE_LIMIT) {
          double value = hyperfactorial(n, engine).doubleValue();
          if (Double.isFinite(value)) {
            return F.num(value);
          }
        }
      }
      if (arg1 instanceof ApfloatNum && arg1.isNumIntValue() && arg1.isNegative()) {
        // the Gamma function formula divides zero by zero at the negative integers
        int n = arg1.toIntDefault();
        if (F.isPresent(n) && n >= -APFLOAT_LIMIT) {
          long precision = ((ApfloatNum) arg1).apfloatValue().precision();
          return F.num(new Apfloat(hyperfactorial(n, engine).toBigNumerator(), precision));
        }
      }
      if (arg1.isInexactNumber()) {
        return numericHyperfactorial((INumber) arg1, engine);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
  private static class JacobiSymbol extends AbstractArg2
      implements java.util.function.IntBinaryOperator {
    @Override
    public int applyAsInt(int m, int n) {
      return F.ZZ(m).jacobiSymbol(F.ZZ(n)).toIntDefault();
    }

    @Override
    public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
      return jacobiSymbol(i0, i1);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  private static class KroneckerSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isZero()) {
        if (arg1.isNumber()) {
          if (arg1.abs().isOne()) {
            return F.C1;
          }
          return F.C0;
        }
        return F.NIL;
      }
      if (arg2.isOne()) {
        return F.C1;
      }
      if (arg2.isMinusOne()) {
        if (arg1.isNegativeResult()) {
          return F.CN1;
        } else if (arg1.isPositiveResult()) {
          return F.C1;
        }
        return F.NIL;
      }
      if (arg2.isNumEqualInteger(F.C2)) {
        if (arg1.isInteger()) {
          IInteger a = (IInteger) arg1;
          if (a.isEven()) {
            return F.C0;
          }
          IInteger mod = a.mod(F.C8);
          if (mod.isOne() || mod.isNumEqualInteger(F.C7)) {
            return F.C1;
          }
          return F.CN1;
        }
        return F.NIL;
      }
      if (arg1.isInteger() && arg2.isInteger()) {
        IInteger a = (IInteger) arg1;
        IInteger n = (IInteger) arg2;
        if (n.isPositive() && n.isOdd()) {
          return jacobiSymbol(a, n);
        }

        // Times@@MapApply(KroneckerSymbol(a,#1)^#2&,FactorInteger(n))
        return F.Apply(F.Times, F.MapApply(
            F.Function(F.Power(F.KroneckerSymbol(a, F.Slot1), F.Slot2)), F.FactorInteger(n)));

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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
        if (arg3.isList1() && arg3.first().isReal()) {
          int n = arg3.first().toMachineInt();
          if (F.isPresent(n)) {
            if (n < 0) {
              // Positive integer expected at position `2` in `1`.
              return Errors.printMessage(S.LinearRecurrence, "intp", F.List(arg3, F.C1), engine);
            }
            IAST result = linearRecurrence(list1, list2, n, ast, engine);
            if (result.isPresent()) {
              // Wrap the single resulting element in a list
              return F.List(result.get(n));
            }
          }
          return F.NIL;
        }
        if (arg3.isList2() && arg3.first().isReal() && arg3.second().isReal()) {
          int nmin = arg3.first().toMachineInt();
          if (F.isPresent(nmin)) {
            if (nmin < 0) {
              // Positive integer expected at position `2` in `1`.
              return Errors.printMessage(S.LinearRecurrence, "intp", F.List(arg3, F.C1), engine);
            }
            int nmax = arg3.second().toMachineInt();
            if (F.isPresent(nmax)) {
              if (nmax < 0) {
                // Positive integer expected at position `2` in `1`.
                return Errors.printMessage(S.LinearRecurrence, "intp", F.List(arg3, F.C2), engine);
              }
              IAST result = linearRecurrence(list1, list2, nmax, ast, engine);
              if (result.isPresent()) {
                return result.copyFrom(nmin, nmax + 1);
              }
            }
          }
          return F.NIL;
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
              num = (INumber) num.plus(list1.get(i).times(result.get(k--)));
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


  private static class LiouvilleLambda extends AbstractFunctionEvaluator
      implements IntUnaryOperator {
    @Override
    public int applyAsInt(int arg) {
      if (arg <= 0) {
        throw new IllegalArgumentException("LiouvilleLambda undefined for n <= 0");
      }
      if (arg == 1)
        return 1;

      int omega = Config.PRIME_FACTORS.factorInteger(BigInteger.valueOf(arg)).values().stream()
          .mapToInt(Integer::intValue).sum();
      return (omega % 2 == 0) ? 1 : -1;
    }

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
  private static class LucasL extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr n = ast.arg1();
        if (!n.isIntegerResult()) {
          // (1/2*(1+Sqrt(5)))^n+(2/(1+Sqrt(5)))^n*Cos(n*Pi)
          IExpr v1 = F.Plus(F.C1, F.CSqrt5);
          return F.Plus(F.Times(F.Power(F.C1D2, n), F.Power(v1, n)),
              F.Times(F.Power(F.C2, n), F.Power(F.Power(v1, F.CN1), n), F.Cos(F.Times(n, F.Pi))));
        }
      } else if (ast.isAST2()) {
        IExpr n = ast.arg1();
        if (!n.isIntegerResult()) {
          IExpr z = ast.arg2();
          // (z/2+Sqrt(1+z^2/4))^n+Cos(n*Pi)/(z/2+Sqrt(1+z^2/4))^n
          IExpr v1 = F.Power(
              F.Plus(F.Times(F.C1D2, z), F.Sqrt(F.Plus(F.C1, F.Times(F.C1D4, F.Sqr(z))))), n);
          return F.Plus(v1, F.Times(F.Power(v1, F.CN1), F.Cos(F.Times(n, F.Pi))));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int n = arg1.toIntDefault();
        if (n > Integer.MIN_VALUE) {
          if (ast.isAST2()) {
            return lucasLPolynomialIterative(n, ast.arg2(), ast, engine);
          }
          int iArg = n;
          if (n < 0) {
            n *= (-1);
          }
          // LucasL(n) = Fibonacci(n-1) + Fibonacci(n+1)
          IExpr lucalsL =
              AbstractIntegerSym.fibonacci(n - 1).add(AbstractIntegerSym.fibonacci(n + 1));
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
   * <pre>
   * <code>MersennePrimeExponent(n)
   * </code>
   * </pre>
   * 
   * <p>
   * returns the <code>n</code>th mersenne prime exponent. <code>2^n - 1</code> must be a prime
   * number. Currently <code>0 &lt; n &lt;= 52</code> can be computed, otherwise the function
   * returns unevaluated.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Table(MersennePrimeExponent(i), {i,20})
   * {2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423}
   * </code>
   * </pre>
   * 
   * <h3>Implementation status</h3>
   * <ul>
   * <li>☑ - partially implemented</li>
   * </ul>
   */
  private static class MersennePrimeExponent extends AbstractTrigArg1 implements IntUnaryOperator {
    @Override
    public int applyAsInt(int n) {
      if (n <= 0 || n > NumberTheory.MPE_52.length) {
        throw new IllegalArgumentException("Out of domain");
      }
      return NumberTheory.MPE_52[n - 1];
    }

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger() && arg1.isPositive()) {
        int n = arg1.toIntDefault();
        if (n > 0) {
          if (n > NumberTheory.MPE_52.length) {
            return F.NIL;
          }
          return F.ZZ(NumberTheory.MPE_52[n - 1]);
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
   * <pre>
   * <code>MersennePrimeExponentQ(n)
   * </code>
   * </pre>
   * 
   * <p>
   * returns <code>True</code> if <code>2^n - 1</code> is a prime number. Currently
   * <code>0 &lt;= n &lt;= 52</code> can be computed in reasonable time.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mersenne_prime">Wikipedia - Mersenne prime</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Select(Range(10000), MersennePrimeExponentQ)
   * {2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423,9689,9941}
   * </code>
   * </pre>
   * 
   * <h3>Implementation status</h3>
   * <ul>
   * <li>☑ - partially implemented</li>
   * </ul>
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
        if (n <= MPE_52[MPE_52.length - 1]) {
          for (int i = 0; i < MPE_52.length; i++) {
            if (MPE_52[i] == n) {
              return S.True;
            }
          }
          return S.False;
        }
        if (n < Integer.MAX_VALUE) {
          // 2^n - 1
          BigInteger b2nm1 = BigInteger.ONE.shiftLeft((int) n).subtract(BigInteger.ONE);
          return F.booleSymbol(MachinePrime.isProbablePrime(b2nm1, 32));
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


  /**
   * <pre>
   * <code>ModularInverse(k, n)
   * </code>
   * </pre>
   * 
   * <p>
   * returns the modular inverse <code>k^(-1) mod n</code>.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Modular_multiplicative_inverse">Wikipedia - Modular
   * multiplicative inverse</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; ModularInverse(3, 11)
   * 4
   * </code>
   * </pre>
   */
  private static class ModularInverse extends AbstractFunctionEvaluator
      implements java.util.function.IntBinaryOperator {
    @Override
    public int applyAsInt(int k, int n) {
      if (n == 0) {
        throw new ArithmeticException("ModularInverse modulo 0 is undefined");
      }
      try {
        BigInteger bn = BigInteger.valueOf(n);
        if (n < 0) {
          return BigInteger.valueOf(k).negate().modInverse(bn.negate()).negate().intValue();
        }
        return BigInteger.valueOf(k).modInverse(bn).intValue();
      } catch (ArithmeticException e) {
        throw new ArithmeticException("Not invertible");
      }
    }

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
   * * &gt;&gt; MoebiusMu(3 + 4*I)
   * -1
   * </pre>
   */
  private static class MoebiusMu extends AbstractTrigArg1 implements IntUnaryOperator {
    @Override
    public int applyAsInt(int arg) {
      if (arg == 0) {
        throw new ArithmeticException("MoebiusMu(0) is undefined");
      }
      return F.ZZ(arg).moebiusMu().toIntDefault();
    }

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger()) {
        try {
          return ((IInteger) arg1).moebiusMu();
        } catch (ArithmeticException e) {
          // integer to large?
        }
      } else if (arg1.isComplex()) {
        IComplex c = (IComplex) arg1;
        IRational real = c.getRealPart();
        IRational imaginary = c.getImaginaryPart();
        if (real.isInteger() && imaginary.isInteger()) {
          BigInteger re = ((IInteger) real).toBigNumerator();
          BigInteger im = ((IInteger) imaginary).toBigNumerator();

          IAST factors = GaussianInteger.factorize(c, re, im);
          int primeCount = 0;
          int argSize = factors.argSize();

          for (int i = 1; i <= argSize; i++) {
            IAST factorList = (IAST) factors.get(i);
            IExpr primeOrUnit = factorList.arg1();
            IExpr exponent = factorList.arg2();

            // Skip Gaussian units: 1, -1, I, -I
            if (primeOrUnit.equals(F.C1) || primeOrUnit.equals(F.CN1) || primeOrUnit.equals(F.CI)
                || primeOrUnit.equals(F.CNI)) {
              continue;
            }

            // MoebiusMu is 0 if it is divisible by a prime square
            if (!exponent.isOne()) {
              return F.C0;
            }
            primeCount++;
          }

          // (-1)^k where k is the number of distinct prime factors
          return (primeCount % 2 == 0) ? F.C1 : F.CN1;
        }
      } else {
        if (arg1.isProbablePrimeResult()) {
          return F.CN1;
        }
        // IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
        // if (negExpr.isPresent()) {
        // return F.MoebiusMu(negExpr);
        // }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
        if (F.isPresent(value)) {
          k[i - 1] = F.ZZ(value);
        } else {
          if (temp.isInteger()) {
            k[i - 1] = (IInteger) temp;
          }
          return F.NIL;
        }
      }
      IInteger multinomial = AbstractIntegerSym.multinomial(k);
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
      newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
  private static class MultiplicativeOrder extends AbstractFunctionEvaluator
      implements IntBinaryOperator {
    @Override
    public int applyAsInt(int a, int n) {
      if (n <= 0)
        throw new IllegalArgumentException("n must be positive");
      BigInteger bigA = BigInteger.valueOf(a);
      BigInteger bigN = BigInteger.valueOf(n);
      if (!bigA.gcd(bigN).equals(BigInteger.ONE)) {
        throw new ArithmeticException("Not coprime");
      }
      return Primality.multiplicativeOrder(bigA, bigN).intValueExact();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger() && ast.arg2().isInteger()) {
        try {
          IInteger k = ast.getInt(1);
          IInteger n = ast.getInt(2);
          if (!n.isPositive()) {
            return F.NIL;
          }

          if (ast.isAST2()) {
            if (!k.gcd(n).isOne()) {
              // not coprime
              return F.NIL;
            }
            return F.ZZ(Primality.multiplicativeOrder(k.toBigNumerator(), n.toBigNumerator()));
          } else if (ast.isAST3()) {
            IExpr arg3 = ast.arg3();
            if (arg3.isList() && arg3.argSize() > 0 && k.gcd(n).isOne()) {
              return smallestPowerIn(k.toBigNumerator(), n.toBigNumerator(), (IAST) arg3,
                  engine);
            }
          }
        } catch (ArithmeticException ae) {
          // fall through to return F.NIL
        }
      }
      return F.NIL;
    }

    /**
     * The smallest <code>m &gt;= 1</code> with <code>k^m</code> congruent to one of the
     * <code>residues</code> modulo <code>n</code>. Each residue is a discrete logarithm to the base
     * <code>k</code>, which is unique modulo the order of <code>k</code>.
     *
     * @return {@link F#NIL} if a residue is not an integer or no residue is a power of
     *         <code>k</code>
     */
    private static IExpr smallestPowerIn(BigInteger k, BigInteger n, IAST residues,
        EvalEngine engine) {
      final BigInteger base = k.mod(n);
      final List<BigInteger> units = new ArrayList<>(residues.argSize());
      for (int i = 1; i < residues.size(); i++) {
        if (!residues.get(i).isInteger()) {
          return F.NIL;
        }
        final BigInteger target = ((IInteger) residues.get(i)).toBigNumerator().mod(n);
        if (target.gcd(n).equals(BigInteger.ONE)) {
          // only a unit is a power of k
          units.add(target);
        }
      }
      if (units.isEmpty()) {
        return F.NIL;
      }
      final BigInteger[] targets = units.toArray(new BigInteger[0]);
      final ResidueNTheory.DiscreteLogs logs = ResidueNTheory.discreteLogs(n, base, targets);
      BigInteger smallest = null;
      for (int i = 0; i < targets.length; i++) {
        final BigInteger log = logs.logs[i];
        if (log == null) {
          continue;
        }
        final BigInteger m = log.signum() == 0 ? logs.order : log;
        if (base.modPow(m, n).equals(targets[i])
            && (smallest == null || m.compareTo(smallest) < 0)) {
          smallest = m;
        }
      }
      engine.checkTimeLimit();
      return smallest == null ? F.NIL : F.ZZ(smallest);
    }

    @Override
    public int status() {
      // Upgraded to full support since 3-argument form is now implemented
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
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
      IExpr arg1 = ast.arg1();
      // lists thread like an outer product: first over the start values, then over k
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (ast.isAST2() && ast.arg2().isList()) {
        return ast.arg2().mapThread(ast, 2);
      }
      int k = 1;
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (!arg2.isInteger()) {
          return F.NIL;
        }
        k = arg2.toIntDefault();
        if (F.isNotPresent(k)) {
          return F.NIL;
        }
      }
      if (!arg1.isReal()) {
        // a real numeric value like Pi or Sqrt(2) starts from its exact integer bound
        if (k == 0 || !arg1.isNumericFunction(true) || !arg1.isRealResult()) {
          return F.NIL;
        }
        arg1 = engine.evaluate(k > 0 ? F.Floor(arg1) : F.Ceiling(arg1));
        if (!arg1.isInteger()) {
          return F.NIL;
        }
      }
      IReal x = (IReal) arg1;
      if (k == 0) {
        // x itself if x is a prime or the negative of a prime
        if (x.isInteger()) {
          BigInteger n = ((IInteger) x).toBigNumerator().abs();
          if (n.compareTo(BigInteger.ONE) > 0 && MachinePrime.isProbablePrime(n, 100)) {
            return x;
          }
        }
        return F.NIL;
      }
      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= Math.abs((long) k)) {
        IterationLimitExceeded.throwIt(Math.abs((long) k), ast);
      }
      // the primes and their negatives in ascending order: ..., -5, -3, -2, 2, 3, 5, ...
      BigInteger temp;
      if (k > 0) {
        temp = x.floorFraction().toBigNumerator();
        for (int i = 0; i < k; i++) {
          temp = signedPrimeAbove(temp);
          engine.checkTimeLimit();
        }
      } else {
        temp = x.ceilFraction().toBigNumerator();
        for (int i = 0; i > k; i--) {
          temp = signedPrimeBelow(temp);
          engine.checkTimeLimit();
        }
      }
      return F.ZZ(temp);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    /** The smallest prime or negative prime greater than <code>n</code>. */
    private static BigInteger signedPrimeAbove(BigInteger n) {
      if (n.compareTo(BigInteger.valueOf(-2)) >= 0) {
        // -2, -1, 0, 1, ... continue with the positive primes
        return n.signum() <= 0 ? BigInteger.TWO : n.nextProbablePrime();
      }
      // n <= -3: the negative of the largest prime below |n|
      return previousProbablePrime(n.negate()).negate();
    }

    /** The largest prime or negative prime less than <code>n</code>. */
    private static BigInteger signedPrimeBelow(BigInteger n) {
      if (n.compareTo(BigInteger.TWO) > 0) {
        return previousProbablePrime(n);
      }
      if (n.compareTo(BigInteger.valueOf(-2)) > 0) {
        // -1, 0, 1, 2
        return BigInteger.valueOf(-2);
      }
      return n.negate().nextProbablePrime().negate();
    }

    /**
     * The largest prime less than <code>n</code>.
     *
     * @param n a number <code>&gt; 2</code>
     */
    private static BigInteger previousProbablePrime(BigInteger n) {
      BigInteger temp = n.subtract(BigInteger.ONE);
      while (!MachinePrime.isProbablePrime(temp, 100)) {
        temp = temp.subtract(BigInteger.ONE);
      }
      return temp;
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
  private static class PartitionsP extends AbstractFunctionEvaluator implements IntUnaryOperator {
    @Override
    public int applyAsInt(int n) {
      if (n < 0) {
        return 0;
      }
      // p(121) is the largest value in int range
      if (n > 121) {
        throw new ArithmeticException("PartitionsP out of 32-bit int bounds");
      }
      return PartitionNumbers.partitionsP(n).intValueExact();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return partitionNumber(ast.arg1(), false);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /** The <code>exact</code> message of <code>PrimeNu</code> and <code>PrimeOmega</code>. */
  public static final String EXACT_MESSAGE = "Argument `1` in `2` is not an exact number.";

  /** The largest argument of <code>PartitionsP</code> and <code>PartitionsQ</code>. */
  private static final int MAX_PARTITIONS_ARGUMENT = 200_000;

  /**
   * <code>PartitionsP(n)</code> or (for <code>distinct</code>) <code>PartitionsQ(n)</code>.
   */
  private static IExpr partitionNumber(IExpr arg1, boolean distinct) {
    if (arg1.isInteger()) {
      if (arg1.isNegative()) {
        // http://fungrim.org/entry/cd3013/
        return F.C0;
      }
      int n = arg1.toIntDefault();
      if (n >= 0 && n <= MAX_PARTITIONS_ARGUMENT) {
        return F.ZZ(
            distinct ? PartitionNumbers.partitionsQ(n) : PartitionNumbers.partitionsP(n));
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
      return partitionNumber(ast.arg1(), true);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  /**
   * <pre>
   * <code>PerfectNumber(n)
   * </code>
   * </pre>
   * 
   * <p>
   * returns the <code>n</code>th perfect number. In number theory, a perfect number is a positive
   * integer that is equal to the sum of its proper positive divisors, that is, the sum of its
   * positive divisors excluding the number itself.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Perfect_number">Wikipedia - Perfect number</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Table(PerfectNumber(i), {i,5})
   * {6,28,496,8128,33550336}
   * </code>
   * </pre>
   */
  private static class PerfectNumber extends AbstractTrigArg1 {

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger() && arg1.isPositive()) {
        int n = arg1.toIntDefault();
        if (n >= 0) {
          if (n > NumberTheory.MPE_52.length) {
            return F.NIL;
          }
          if (n <= NumberTheory.PN_8.length) {
            return F.ZZ(NumberTheory.PN_8[n - 1]);
          }
          int p = NumberTheory.MPE_52[n - 1];
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  /**
   * <pre>
   * <code>PerfectNumberQ(n)
   * </code>
   * </pre>
   * 
   * <p>
   * returns <code>True</code> if <code>n</code> is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper positive divisors, that is,
   * the sum of its positive divisors excluding the number itself.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Perfect_number">Wikipedia - Perfect number</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/List_of_perfect_numbers">Wikipedia - List of perfect
   * numbers</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Select(Range(1000), PerfectNumberQ)
   * {6,28,496}
   * </code>
   * </pre>
   */
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
  private static class Prime extends AbstractFunctionEvaluator implements IntUnaryOperator {
    @Override
    public int applyAsInt(int n) {
      if (n <= 0 || n > 103000000) {
        throw new IllegalArgumentException("Prime(" + n + ") is out of domain");
      }
      return (int) Primality.prime(n);
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        IInteger n = (IInteger) arg1;
        if (!n.isPositive()) {
          // Positive integer argument expected in `1`.
          return Errors.printMessage(ast.topHead(), "intpp", F.list(ast), engine);
        }
        long nthPrime = n.toLongDefault();
        if (nthPrime == Config.INVALID_LONG || nthPrime > PrimeCount.MAX_INDEX) {
          // Maximum Prime limit `1` exceeded.
          return Errors.printMessage(ast.topHead(), "zzprime",
              F.list(F.ZZ(PrimeCount.MAX_INDEX)), engine);
        }
        try {
          long p = PrimeCount.nthPrime(nthPrime);
          if (p < 0) {
            // Maximum Prime limit `1` exceeded.
            return Errors.printMessage(ast.topHead(), "zzprime",
                F.list(F.ZZ(PrimeCount.MAX_INDEX)), engine);
          }
          return F.ZZ(p);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.Prime, rex, engine);
        }
      } else if (arg1.isNumber()) {
        // Positive integer argument expected in `1`.
        return Errors.printMessage(ast.topHead(), "intpp", F.list(ast), engine);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  private static class PrimeOmega extends AbstractFunctionOptionEvaluator implements IntUnaryOperator {

    @Override
    public int applyAsInt(int arg) {
      if (arg == 0) {
        throw new ArithmeticException("PrimeOmega(0) is undefined");
      }
      if (arg == 1 || arg == -1) {
        return 0;
      }

      long absArg = Math.abs((long) arg);
      SortedMap<BigInteger, Integer> map =
          Config.PRIME_FACTORS.factorInteger(BigInteger.valueOf(absArg));

      int sum = 0;
      for (Integer exponent : map.values()) {
        sum += exponent;
      }
      return sum;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isInexactNumber()) {
        // Argument `1` in `2` is not an exact number.
        return Errors.printMessage(S.PrimeOmega, "exact", F.list(arg1, ast), engine);
      }
      if (!options[0].isTrue() && !options[0].isFalse()) {
        return F.NIL;
      }
      if (arg1.isZero()) {
        return F.NIL;
      }
      if (arg1.isOne() || arg1.isMinusOne()) {
        return F.C0;
      }
      if (options[0].isTrue() || arg1.isComplex()) {
        IAST factors = F.NIL;
        if (arg1.isInteger()) {
          factors = GaussianInteger.factorize((IInteger) arg1,
              ((IInteger) arg1).toBigNumerator(), BigInteger.ZERO);
        } else if (arg1.isComplex()) {
          IComplex c = (IComplex) arg1;
          if (c.getRealPart().isInteger() && c.getImaginaryPart().isInteger()) {
            factors = GaussianInteger.factorize(c,
                ((IInteger) c.getRealPart()).toBigNumerator(),
                ((IInteger) c.getImaginaryPart()).toBigNumerator());
          }
        }
        if (!factors.isPresent()) {
          return F.NIL;
        }
        long sum = 0;
        for (int i = 1; i < factors.size(); i++) {
          IAST factor = (IAST) factors.get(i);
          IExpr base = factor.arg1();
          // skip the units 1, -1, I, -I
          if (base.isOne() || base.isMinusOne() || base.isImaginaryUnit()
              || base.isNegativeImaginaryUnit()) {
            continue;
          }
          sum += factor.arg2().toIntDefault();
        }
        return F.ZZ(sum);
      }
      if (arg1.isInteger()) {
        try {
          int argInt = arg1.toIntDefault();
          if (F.isPresent(argInt)) {
            return F.ZZ(applyAsInt(argInt));
          }
        } catch (ArithmeticException e) {
          // fallback if it exceeds int bounds
        }

        if (arg1.isNegative()) {
          arg1 = arg1.negate();
        }
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.putMessage(IPatternMatcher.SET, "exact", F.stringx(EXACT_MESSAGE));
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.GaussianIntegers}, //
          new IExpr[] {S.False});
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
  private static class PrimePowerQ extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      IExpr gaussian = options[0];
      if (arg1.isFraction() || (arg1.isComplex() && !GaussianInteger.isGaussianInteger(arg1))) {
        // a rational number is tested by its reciprocal, which has to be an integer
        IExpr reciprocal = engine.evaluate(F.Divide(F.C1, arg1));
        if (!reciprocal.isInteger() && !GaussianInteger.isGaussianInteger(reciprocal)) {
          return S.False;
        }
        arg1 = reciprocal;
      }
      if (arg1.isInteger()) {
        if (gaussian.isTrue()) {
          return F.booleSymbol(isGaussianPrimePower(arg1));
        }
        return F.booleSymbol(Primality.isPrimePower(((IInteger) arg1).toBigNumerator()));
      }
      if (GaussianInteger.isGaussianInteger(arg1)) {
        if (gaussian.isFalse()) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(S.PrimePowerQ, "par",
              F.list(F.Rule(S.GaussianIntegers, S.False)), engine);
        }
        return F.booleSymbol(isGaussianPrimePower(arg1));
      }
      return S.False;
    }

    /** A unit times a power of one Gaussian prime. */
    private static boolean isGaussianPrimePower(IExpr z) {
      IAST factors;
      if (z.isInteger()) {
        IInteger n = (IInteger) z;
        if (n.isZero()) {
          return false;
        }
        factors = GaussianInteger.factorize(n, n.toBigNumerator(), BigInteger.ZERO);
      } else {
        IComplex c = (IComplex) z;
        factors = GaussianInteger.factorize(c, ((IInteger) c.getRealPart()).toBigNumerator(),
            ((IInteger) c.getImaginaryPart()).toBigNumerator());
      }
      if (factors == null || factors.isNIL()) {
        return false;
      }
      int primes = 0;
      for (IExpr factor : factors) {
        IExpr p = factor.isList() ? factor.first() : factor;
        // the units 1, -1, I, -I aren't primes
        if (!(p.isOne() || p.isMinusOne() || p.equals(F.CI) || p.equals(F.CNI))) {
          primes++;
        }
      }
      return primes == 1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
      setOptions(newSymbol, S.GaussianIntegers, S.Automatic);
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  private static class PrimitiveRoot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        try {
          IInteger n = (IInteger) arg1;
          // n must be > 1
          if (!n.isPositive() || n.isOne()) {
            // Integer greater than `1` expected at position `2` in `3`
            return Errors.printMessage(S.PrimitiveRoot, "intg", F.list(F.C1, F.C1, ast), engine);
          }
          // Determine start of search (1-arg or 2-arg form)
          IInteger startValue = F.C1;
          if (ast.isAST2()) {
            IExpr arg2 = ast.arg2();
            if (!arg2.isInteger() || ((IInteger) arg2).isNegative()) {
              return F.NIL;
            }
            startValue = ((IInteger) arg2).add(F.C1);
          }
          BigInteger nBig = n.toBigNumerator();
          BigInteger phiBig = Primality.eulerPhi(nBig);
          BigInteger lambdaBig = Primality.charmichaelLambda(nBig);
          // n has primitive roots iff CarmichaelLambda(n) == EulerPhi(n)
          if (!lambdaBig.equals(phiBig)) {
            return F.NIL;
          }
          // Compute distinct prime factors of phi(n) once
          SortedMap<BigInteger, Integer> phiFactors = Config.PRIME_FACTORS.factorInteger(phiBig);
          BigInteger[] primeFactorsOfPhi = phiFactors.keySet().toArray(new BigInteger[0]);
          // Iterate candidates from startValue up to n-1
          IInteger m = startValue;
          int iterationLimit = engine.getIterationLimit();
          int loopCounter = 0;
          while (m.compareTo(n) < 0) {
            if (iterationLimit >= 0 && ++loopCounter > iterationLimit) {
              IterationLimitExceeded.throwIt(loopCounter, ast);
            }
            if (m.gcd(n).isOne()) {
              BigInteger mBig = m.toBigNumerator();
              boolean isPrimitiveRoot = true;
              for (BigInteger p : primeFactorsOfPhi) {
                BigInteger exp = phiBig.divide(p);
                if (mBig.modPow(exp, nBig).equals(BigInteger.ONE)) {
                  isPrimitiveRoot = false;
                  break;
                }
              }
              if (isPrimitiveRoot) {
                return m;
              }
            }
            m = m.add(F.C1);
          }
          return F.NIL;
        } catch (LimitException le) {
          throw le;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
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
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    /**
     * See:
     * <a href= "http://exploringnumbertheory.wordpress.com/2013/09/09/finding-primitive-roots/">
     * Exploring Number Theory - Finding Primitive Roots</a>
     */
    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInteger()) {
        try {
          // the primitive roots of -n are those of n
          IInteger[] roots = ((IInteger) arg1).abs().primitiveRootList();
          if (roots != null) {
            return F.mapRange(0, roots.length, i -> roots[i]);
          }
        } catch (LimitException le) {
          throw le;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  /**
   * <pre>
   * <code>QuadraticIrrationalQ(expr)
   * </code>
   * </pre>
   * 
   * <p>
   * returns <code>True</code>, if the <code>expr</code> is of the form
   * <code>(p + s * Sqrt(d)) / q</code> for integers <code>p,q,d,s</code>.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Quadratic_irrational_number">Wikipedia - Quadratic
   * irrational number</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/Periodic_continued_fraction">Wikipedia - Periodic
   * continued fraction</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; QuadraticIrrationalQ(5*Sqrt(11))
   * True
   * 
   * &gt;&gt; QuadraticIrrationalQ((7*Sqrt(2) + 1)/11)
   * True
   * 
   * &gt;&gt; QuadraticIrrationalQ(42)
   * False
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="ContinuedFraction.md">ContinuedFraction</a>
   * </p>
   */
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  /**
   * <pre>
   * <code>RamseyNumber(r, s)
   * </code>
   * </pre>
   * 
   * <p>
   * returns the Ramsey number <code>R(r,s)</code>. Currently not all values are known for
   * <code>1 &lt;= r &lt;= 4</code>. The function returns unevaluated if the value is unknown.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Ramsey's_theorem">Wikipedia - Ramsey's
   * theorem</a></li>
   * <li><a href="https://oeis.org/A212954">OEIS - A212954</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; Table(RamseyNumber(1,j), {j,1,20})
   * {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
   * 
   * &gt;&gt; Table(RamseyNumber(2,j), {j,1,20})
   * {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}
   *         
   * &gt;&gt; Table(RamseyNumber(i,j), {i,1,5},{j,i,10})
   * {{1,1,1,1,1,1,1,1,1,1},{2,3,4,5,6,7,8,9,10},{6,9,14,18,23,28,36,RamseyNumber(3,10)},{18,25,RamseyNumber(4,6),RamseyNumber(4,7),RamseyNumber(4,8),RamseyNumber(4,9),RamseyNumber(4,10)}}
   * </code>
   * </pre>
   */
  private static class RamseyNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int r = ast.arg1().toMachineInt();
      int s = ast.arg2().toMachineInt();
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
  public static final class Rationalize extends AbstractCoreFunctionEvaluator {

    /**
     * The exact rational value of every inexact number in <code>expr</code>: unlike
     * <code>Rationalize(expr, 0)</code>, which takes the simplest number within the precision, the
     * number itself.
     */
    public static IExpr exact(IExpr expr) {
      return AbstractFractionSym.rationalize(expr, 0.0, false, true).orElse(expr);
    }

    /**
     * The relative distance within which a convergent of the continued fraction stands for a
     * machine number: five quarters of a unit of the 53rd binary digit.
     */
    private static final double MACHINE_TOLERANCE = 1.25 * Math.pow(2.0, -53);

    /**
     * The first convergent within the precision of an inexact real or complex number, or
     * {@link F#NIL} for any other expression.
     */
    private static IExpr convergent(IExpr x) {
      if (x instanceof INum && x.isMachineNumber()) {
        double value = ((INum) x).doubleValue();
        return Double.isFinite(value)
            ? convergentWithin(new org.hipparchus.fraction.BigFraction(value),
                new org.hipparchus.fraction.BigFraction(MACHINE_TOLERANCE))
            : F.NIL;
      }
      if (x instanceof ApfloatNum) {
        Apfloat value = ((ApfloatNum) x).apfloatValue();
        java.math.BigDecimal decimal = new java.math.BigDecimal(value.toString(true));
        org.hipparchus.fraction.BigFraction exact = decimal.scale() <= 0
            ? new org.hipparchus.fraction.BigFraction(decimal.toBigIntegerExact())
            : new org.hipparchus.fraction.BigFraction(decimal.unscaledValue(),
                java.math.BigInteger.TEN.pow(decimal.scale()));
        // five quarters of a unit of the last digit
        org.hipparchus.fraction.BigFraction tolerance = new org.hipparchus.fraction.BigFraction(
            java.math.BigInteger.valueOf(5),
            java.math.BigInteger.TEN.pow((int) Math.min(value.precision(), 100_000)).shiftLeft(2));
        return convergentWithin(exact, tolerance);
      }
      if (x.isInexactNumber() && x instanceof INumber && !x.isReal()) {
        IExpr re = convergent(((INumber) x).re());
        IExpr im = convergent(((INumber) x).im());
        return re.isPresent() && im.isPresent() ? F.Complex(re, im) : F.NIL;
      }
      return F.NIL;
    }

    /**
     * The first convergent of the continued fraction of <code>x</code> within the relative
     * distance <code>relativeTolerance</code>: <code>0.1</code> is <code>1/10</code>.
     */
    private static IExpr convergentWithin(org.hipparchus.fraction.BigFraction x,
        org.hipparchus.fraction.BigFraction relativeTolerance) {
      if (x.getNumerator().signum() == 0) {
        return F.C0;
      }
      final org.hipparchus.fraction.BigFraction value = x.abs();
      final org.hipparchus.fraction.BigFraction tolerance = value.multiply(relativeTolerance);
      java.math.BigInteger h0 = java.math.BigInteger.ZERO;
      java.math.BigInteger h1 = java.math.BigInteger.ONE;
      java.math.BigInteger k0 = java.math.BigInteger.ONE;
      java.math.BigInteger k1 = java.math.BigInteger.ZERO;
      org.hipparchus.fraction.BigFraction rest = value;
      while (true) {
        java.math.BigInteger a = rest.getNumerator().divide(rest.getDenominator());
        java.math.BigInteger h2 = a.multiply(h1).add(h0);
        java.math.BigInteger k2 = a.multiply(k1).add(k0);
        h0 = h1;
        h1 = h2;
        k0 = k1;
        k1 = k2;
        org.hipparchus.fraction.BigFraction convergent = new org.hipparchus.fraction.BigFraction(h1, k1);
        org.hipparchus.fraction.BigFraction fractional =
            rest.subtract(new org.hipparchus.fraction.BigFraction(a));
        if (fractional.getNumerator().signum() == 0
            || convergent.subtract(value).abs().compareTo(tolerance) <= 0) {
          IExpr result = F.QQ(h1, k1);
          return x.getNumerator().signum() < 0 ? result.negate() : result;
        }
        rest = fractional.reciprocal();
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1 instanceof INum) {
        arg1 = ((INum) arg1).numericValue();
      }
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
          if (arg1.isNumericFunction(true) && !arg1.isList()) {
            // an exact numeric expression is approximated as a whole; inside a list or any other
            // expression only the inexact numbers are rationalized
            arg1 = engine.evalN(arg1);
          }
          if (epsilon == 0.0) {
            // every inexact number becomes the simplest rational number it stands for
            IExpr converted = arg1.isAST() ? arg1.replaceAll(Rationalize::convergent)
                : convergent(arg1);
            if (converted.isPresent()) {
              return engine.evaluate(converted);
            }
          }
        }
        // try to convert into a fractional number
        return AbstractFractionSym.rationalize(arg1, epsilon, useConvergenceMethod, true)
            .orElse(arg1);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
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
      // newSymbol.setAttributes(Attribute.HOLDALL);
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
  private static final class SquareFreeQ extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr modulus = options[0];
      IExpr gaussianIntegers = options[1];
      final VariablesSet eVar;
      boolean hasOption = ast.isAST2() ? true : false;
      if (argSize == 2) {
        eVar = new VariablesSet(ast.arg2());
        final VariablesSet exprVariables = new VariablesSet(ast.arg1());
        if (eVar.size() != exprVariables.size()) {
          // `1`
          Errors.printMessage(S.SquareFreeQ, "error",
              F.List(F.stringx("Currently not supported: number of variables in expression (" //
                  + exprVariables.size() //
                  + ") unequals number of user variables (" //
                  + eVar.size() //
                  + ").")),
              engine);
          return F.NIL;
        }
        hasOption = false;
      } else {
        eVar = new VariablesSet(ast.arg1());
      }
      if (eVar.isSize(0)) {
        // no variable path
        IExpr arg1 = ast.arg1();
        if (arg1.isZero()) {
          return S.False;
        }
        if (arg1.isExactNumber()) {
          if (!modulus.isZero()) {
            // Inappropriate parameter: `1`.
            return Errors.printMessage(S.SquareFreeQ, "par", F.List(F.Rule(S.Modulus, modulus)),
                engine);
          }
          if (arg1.isInteger()) {
            return F.booleSymbol(Primality.isSquareFree(((IInteger) arg1).toBigNumerator()));
          }
          if (arg1.isFraction()) {
            return F.booleSymbol(Primality.isSquareFree(((IFraction) arg1).toBigNumerator()) //
                && Primality.isSquareFree(((IFraction) arg1).toBigDenominator()));
          }
          if (arg1.isComplex()) {
            if (gaussianIntegers == S.False) {
              // Inappropriate parameter: `1`.
              return Errors.printMessage(S.SquareFreeQ, "par",
                  F.List(F.Rule(S.GaussianIntegers, S.False)), engine);
            }
            IRational re = ((IComplex) arg1).re();
            if (re.isInteger()) {
              IRational im = ((IComplex) arg1).im();
              if (im.isInteger()) {
                return F.booleSymbol(
                    GaussianInteger.isSquareFree((IComplex) arg1, (IInteger) re, (IInteger) im));
              }

            }
          }
          return S.False;
        }
        // if (arg1.isAtom()) {
        // return S.False;
        // }
        return F.False;
      }
      if (!eVar.isSize(1)) {
        // `1` currently not supported in `2`.
        Errors.printMessage(S.SquareFreeQ, "unsupported", F.List(eVar.getVarList(), S.SquareFreeQ),
            engine);
        return F.NIL;
      }
      try {
        IExpr expr = F.evalExpandAll(ast.arg1(), engine);
        IAST varList = eVar.getVarList();

        if (hasOption) {
          return isSquarefreeWithOption(ast, expr, varList, options, engine);
        }
        return F.booleSymbol(isSquarefree(expr, varList));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // JAS may throw RuntimeExceptions
        Errors.printMessage(S.SquareFreeQ, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.Modulus, S.GaussianIntegers}, //
          new IExpr[] {F.C0, S.Automatic});
    }

    private static boolean isSquarefree(IExpr expr, IAST varList) throws JASConversionException {
      JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
      GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
      if (poly == null) {
        return false;
      }
      FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ONE);
      return factorAbstract.isSquarefree(poly);
    }

    private static IExpr isSquarefreeWithOption(final IAST lst, IExpr expr, IAST varList,
        IExpr[] options, final EvalEngine engine) throws JASConversionException {

      IExpr modulus = options[0];
      IExpr gaussianIntegers = options[1];
      if (modulus.isInteger() && !modulus.isZero()) {
        // found "Modulus" option => use ModIntegerRing
        ModIntegerRing modIntegerRing = JASConvert.option2ModIntegerRing((IInteger) modulus);
        JASConvert<ModInteger> jas = new JASConvert<ModInteger>(varList, modIntegerRing);
        GenPolynomial<ModInteger> poly = jas.expr2JAS(expr, false);
        if (poly == null) {
          return S.False;
        }
        FactorAbstract<ModInteger> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
        return F.booleSymbol(factorAbstract.isSquarefree(poly));
      }
      if (gaussianIntegers == S.True) {
        // found "GaussianIntegers->True" option => factor over the Gaussian integers (complex
        // numbers with rational real and imaginary parts)
        ComplexRing<BigRational> complexRing = new ComplexRing<BigRational>(BigRational.ONE);
        JASConvert<edu.jas.poly.Complex<BigRational>> jas =
            new JASConvert<edu.jas.poly.Complex<BigRational>>(varList, complexRing);
        GenPolynomial<edu.jas.poly.Complex<BigRational>> poly = jas.expr2JAS(expr, false);
        if (poly == null) {
          return S.False;
        }
        FactorAbstract<edu.jas.poly.Complex<BigRational>> factorAbstract =
            FactorFactory.getImplementation(complexRing);
        return F.booleSymbol(factorAbstract.isSquarefree(poly));
      }
      // GaussianIntegers->Automatic or GaussianIntegers->False:
      // use the squarefree test over the rational numbers
      try {
        return F.booleSymbol(isSquarefree(expr, varList));
      } catch (JASConversionException jce) {
        if (gaussianIntegers == S.False) {
          // Inappropriate parameter: `1`.
          return Errors.printMessage(S.SquareFreeQ, "par",
              F.List(F.Rule(S.GaussianIntegers, S.False)), engine);
        }
      }
      return S.False;
    }

  }


  /**
   * <pre>
   * <code>SquaresR(k, intNumber)
   * </code>
   * </pre>
   * 
   * <p>
   * counts the numbers of the representation of <code>intNumber</code> as sum of <code>x^2</code>
   * terms which occur <code>k</code> times.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Sum_of_squares_function">Wikipedia - Sum of squares
   * function</a></li>
   * <li><a href="https://en.wikipedia.org/wiki/Sum_of_two_squares_theorem">Wikipedia - Sum of two
   * quares function</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; SquaresR(8, 30)
   * 395136
   * </code>
   * </pre>
   */

  private static class SquaresR extends AbstractFunctionEvaluator
      implements java.util.function.IntBinaryOperator {
    @Override
    public int applyAsInt(int d, int n) {
      if (d <= 0 || n < 0) {
        throw new IllegalArgumentException("SquaresR out of domain");
      }
      return squaresR(d, n);
    }

    /**
     * TODO use Wikipedia formulas for small d
     * <a href="https://en.wikipedia.org/wiki/Sum_of_squares_function">Wikipedia - Sum of squares
     * function</a>
     * 
     * @param d
     * @param n
     * @return
     */
    private static int squaresR(int d, long n) {
      if (d == 0) {
        // Only one way to represent 0 as a sum of 0 squares
        return n == 0 ? 1 : 0;
      }
      if (n < 0) {
        // No way to represent a negative number as a sum of squares
        return 0;
      }

      int count = 0;
      for (long i = 0; i <= n; i++) {
        long square = LongMath.checkedPow(i, 2);
        if (square > n) {
          return count;
        }
        long n2 = n - square;
        long squaresR = squaresR(d - 1, n2);
        // Add the number of ways to represent n - i^2 as a sum of d - 1 squares
        if (i == 0) {
          count += squaresR;
        } else {
          count += 2 * squaresR;
        }
      }
      return count;
    }

    private IExpr squaresR2(IInteger n, EvalEngine engine) {
      if (n.isZero()) {
        return F.C1;
      }
      if (n.isOne()) {
        return F.C4;
      }
      IASTAppendable factorsList = n.factorInteger();
      if (factorsList.isList()) {
        return squaresR2(factorsList, engine);
      }
      return F.NIL;
    }

    private static IExpr squaresR2(IAST factorsList, EvalEngine engine) {
      IInteger result = F.C4;
      for (int i = 1; i < factorsList.size(); i++) {
        IAST primeExponentList = (IAST) factorsList.get(i);
        IInteger mod = ((IInteger) primeExponentList.arg1()).mod(4);
        IExpr exponent = primeExponentList.arg2();
        if (exponent.isOdd()) {
          if (mod.equals(F.C3)) {
            return F.C0;
          }
        }
        if (mod.isOne()) {
          // result = result * (exponent + 1)
          result = result.multiply(((IInteger) primeExponentList.second()).inc());
        }
      }
      return result;
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int d = ast.arg1().toMachineInt();
      if (d <= 0) {
        // The value `1` in position `2` must be a non-negative machine sized integer
        return Errors.printMessage(S.SquaresR, "pint", F.List(ast.arg1(), F.C1), engine);
      }
      IExpr arg2 = ast.arg2();
      if (arg2.isInteger()) {
        IInteger n = (IInteger) arg2;
        switch (d) {
          case 2:
            return squaresR2(n, engine);
          case 4:
            // 8*Total(Select(Divisors(n),Mod(#1,4)!=0&))
            return F.Times(F.C8, F
                .Total(F.Select(F.Divisors(n), F.Function(F.Unequal(F.Mod(F.Slot1, F.C4), F.C0)))));
          case 6:
            // 4*Total((#1^2*(4*KroneckerSymbol(-4,n/#1)-KroneckerSymbol(-4,#1))&)/@Divisors(n))
            return F
                .Times(
                    F.C4, F
                        .Total(
                            F.Map(F.Function(F.Times(F.Sqr(F.Slot1),
                                F.Subtract(
                                    F.Times(F.C4,
                                        F.KroneckerSymbol(F.CN4,
                                            F.Times(n, F.Power(F.Slot1, F.CN1)))),
                                    F.KroneckerSymbol(F.CN4, F.Slot1)))),
                                F.Divisors(n))));
          case 8:
            // 16*Total(((-1)^(n+#1)*#1^3&)/@Divisors(n))
            return F.Times(F.ZZ(16L),
                F.Total(F.Map(
                    F.Function(F.Times(F.Power(-1, F.Plus(n, F.Slot1)), F.Power(F.Slot1, F.C3))),
                    F.Divisors(n))));
          default:
            break;
        }

      }
      int n = ast.arg2().toMachineInt();
      if (F.isNotPresent(n)) {
        // Machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.SquaresR, "pint", F.List(ast.arg2(), F.C2), engine);
      }


      final int recursionLimit = engine.getRecursionLimit();
      try {
        if (recursionLimit > 0) {
          int counter = engine.incRecursionCounter();
          if (d > recursionLimit || counter + d > recursionLimit) {
            RecursionLimitExceeded.throwIt(counter, ast);
          }
        }
        int squaresR = squaresR(d, n);
        if (squaresR >= 0) {
          return F.ZZ(squaresR);
        }
      } catch (ArithmeticException aex) {
        // from LongMath#checkedPow()
        return Errors.printMessage(S.SquaresR, aex, engine);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }


  /**
   * <pre>
   * <code>PowersRepresentations(intNumber, k, exponent)
   * </code>
   * </pre>
   * 
   * <p>
   * computes the representations of the <code>intNumber</code> as sum of <code>x^exponent</code>
   * terms which occur <code>k</code> times.
   * </p>
   * 
   * <p>
   * See
   * </p>
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Sum_of_squares_function">Wikipedia - Sum of squares
   * function</a></li>
   * </ul>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; PowersRepresentations(8174, 6, 3)
   * {{0,0,4,10,13,17},{0,3,6,7,9,19},{0,7,10,12,12,15},{1,1,1,11,14,16},{1,3,5,12,13,16},{1,4,5,5,10,19},{1,5,6,10,10,18},{2,3,4,6,10,19},{3,3,3,4,13,18},{3,5,9,10,13,16},{4,5,6,13,13,15},{4,9,12,12,12,13},{5,9,9,13,13,13},{7,7,10,10,14,14}}
   * 
   * &gt;&gt; 2^3+3^3+4^3+6^3+10^3+19^3
   * 8174
   * </code>
   * </pre>
   */
  private static class PowersRepresentations extends AbstractFunctionEvaluator {

    /**
     * Compute <code>PowersRepresentations(n,k,p)</code> recursively and collect the resulting
     * integer lists in <code>resultList</code>.
     * 
     * @param resultList
     * @param intList
     * @param start
     * @param n the number which should be represented by the sum of powers
     *        <code>n1^k + n2^k + ...</code>
     * @param k the resulting number of integers in each intList
     * @param p the exponent
     */
    private static void powersRepresentationsRecursive(IASTAppendable resultList,
        IASTAppendable intList, long start, long n, int k, int p) {
      if (k == 0) {
        if (n == 0) {
          resultList.append(intList);
        }
        return;
      }
      if (n < 0) {
        return;
      }

      for (long i = start; i <= n; i++) {
        long checkedPow = LongMath.checkedPow(i, p);
        if (checkedPow > n) {
          return;
        }
        long n2 = n - checkedPow;
        IASTAppendable subList = intList.copyAppendable();
        subList.append(i);
        powersRepresentationsRecursive(resultList, subList, i, n2, k - 1, p);
      }

    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      long n = ast.arg1().toLongDefault();
      if (F.isNotPresent(n)) {
        // Machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.PowersRepresentations, "pint", F.List(ast.arg1(), F.C1),
            engine);
      }
      int k = ast.arg2().toMachineInt();
      if (k < 0) {
        // The value `1` in position `2` must be a non-negative machine sized integer
        return Errors.printMessage(S.PowersRepresentations, "pint", F.List(ast.arg2(), F.C2),
            engine);
      }
      int p = ast.arg3().toMachineInt();
      if (p <= 0) {
        // The value `1` of argument `2` must be a positive integer
        return Errors.printMessage(S.PowersRepresentations, "ppnt", F.List(ast.arg3(), F.C3),
            engine);
      }
      if (k == 0) {
        return F.CEmptyList;
      }
      if (n < 0) {
        return F.CEmptyList;
      }


      try {
        IASTAppendable result = F.ListAlloc();
        powersRepresentationsRecursive(result, F.ListAlloc(k), 0, n, k, p);
        return result;
      } catch (ArithmeticException aex) {
        // from LongMath#checkedPow()
        return Errors.printMessage(S.PowersRepresentations, aex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }


    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    // @Override
    // public void setUp(final ISymbol newSymbol) {
    // }
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
        return Times(F.Power(-1, nSubtract1), F.Factorial(nSubtract1));
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
          IInteger k = value.add(nSubtractm);
          temp.append(
              F.Times(factorPlusMinus1, AbstractIntegerSym.binomial(value.add(nSubtract1), k),
                  AbstractIntegerSym.binomial(nTimes2Subtractm, nSubtractm.subtract(value)),
                  F.StirlingS2(k, value)));
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class StirlingS2 extends AbstractFunctionEvaluator implements IntBinaryOperator {
    @Override
    public int applyAsInt(int n, int k) {
      if (n < 0 || k < 0 || k > n)
        throw new ArithmeticException("Out of domain");
      return stirlingS2(n, F.ZZ(k), k).toInt(); // Throws ArithmeticException if > MAX_VALUE
    }

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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
  private static class Subfactorial extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      return functionExpand(n);
    }

    private static IExpr functionExpand(IExpr n) {
      // Gamma(1+n,-1)/E
      return F.Times(F.Exp(F.CN1), F.Gamma(F.Plus(F.C1, n), F.CN1));
    }

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
      return subfactorial(arg1, engine.isNumericMode()).eval(engine);
    }

    protected IExpr subfactorial(IExpr arg1, boolean numericMode) {
      if (arg1.isInteger() && arg1.isPositive()) {
        try {
          long n = ((IInteger) arg1).toLong();
          return subFactorial(n);
        } catch (ArithmeticException ae) {
          Errors.printMessage(S.Subfactorial, ae, EvalEngine.get());
        }
        return F.NIL;
      }
      if (arg1.isZero()) {
        return F.C1;
      }
      if (numericMode && arg1.isNumericFunction()) {
        return functionExpand(arg1);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
      S.DivisorSum.setEvaluator(new DivisorSum());
      S.DivisorSigma.setEvaluator(new DivisorSigma());
      S.EulerE.setEvaluator(new EulerE());
      S.EulerPhi.setEvaluator(new EulerPhi());
      S.ExtendedGCD.setEvaluator(new ExtendedGCD());
      S.Factorial.setEvaluator(new Factorial());
      S.FactorialPower.setEvaluator(new FactorialPower());
      S.Factorial2.setEvaluator(new Factorial2());
      S.FactorInteger.setEvaluator(new FactorInteger());
      S.GoldbachList.setEvaluator(new GoldbachList());
      S.Fibonacci.setEvaluator(new Fibonacci());
      S.FindLinearRecurrence.setEvaluator(new FindLinearRecurrence());
      S.FrobeniusNumber.setEvaluator(new FrobeniusNumber());
      S.FromContinuedFraction.setEvaluator(new FromContinuedFraction());
      S.Hyperfactorial.setEvaluator(new Hyperfactorial());
      S.JacobiSymbol.setEvaluator(new JacobiSymbol());
      S.KroneckerSymbol.setEvaluator(new KroneckerSymbol());
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
      S.PowersRepresentations.setEvaluator(new PowersRepresentations());
      S.Prime.setEvaluator(new Prime());
      S.PrimeOmega.setEvaluator(new PrimeOmega());
      S.PrimePowerQ.setEvaluator(new PrimePowerQ());
      S.PrimitiveRoot.setEvaluator(new PrimitiveRoot());
      S.PrimitiveRootList.setEvaluator(new PrimitiveRootList());
      S.QuadraticIrrationalQ.setEvaluator(new QuadraticIrrationalQ());
      S.RamseyNumber.setEvaluator(new RamseyNumber());
      S.Rationalize.setEvaluator(new Rationalize());
      S.SquareFreeQ.setEvaluator(new SquareFreeQ());
      S.SquaresR.setEvaluator(new SquaresR());
      S.StirlingS1.setEvaluator(new StirlingS1());
      S.StirlingS2.setEvaluator(new StirlingS2());
      S.Subfactorial.setEvaluator(new Subfactorial());
    }

  }

  public static boolean check(IExpr n, IExpr k, IExpr delta, EvalEngine engine) {
    return engine.evalEqual(n, k.plus(delta));
  }

  /**
   * Returns the rising factorial <code>n*(n+1)*...*(n+k-1)</code>
   *
   * @param n
   * @param k
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

  public static void initialize() {
    Initializer.init();
  }

  /**
   * Compute the binomial coefficient <code>n choose k</code> as a polynomial in n. Assumption:
   * <code>n &gt; k</code>.
   * 
   * @param n the expression <code>n</code> in the binomial coefficient <code>n choose k</code>
   * @param k the integer <code>k</code> in the binomial coefficient <code>n choose k</code>
   * @return the binomial polynomial <code>n*(n-1)*...*(n-k+1)/(k!)</code>
   */
  public static IAST binomialPolynomial(IExpr n, int k) {
    IASTAppendable result = F.TimesAlloc(k);
    IExpr temp;
    IExpr nTemp = n;
    for (int i = 1; i <= k; i++) {
      temp = F.Divide(nTemp, F.ZZ(i));
      result.append(temp);
      nTemp = nTemp.dec();
    }
    return result;
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
   * Gives the <a href="https://en.wikipedia.org/wiki/Jacobi_symbol">Jacobi symbol</a>
   * <code>(n/m)</code> of two integers.
   * 
   * @param n
   * @param m
   * @return <code>(n/m)</code>
   */
  public static IExpr jacobiSymbol(final IInteger n, final IInteger m) {
    try {
      return n.jacobiSymbol(m);
    } catch (ArithmeticException e) {
      // integer to large?
    }
    return F.NIL;
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
  public static IInteger stirlingS2(int n, IInteger k, final int ki) throws MathRuntimeException {
    if (ki > n || ki == 0) {
      return C0;
    }
    if (ki == 1) {
      // {n,1}==1
      return C1;
    }
    if (ki == 2) {
      // {n,2}==2^(n-1)-1
      return C2.powerRational(n - 1).subtract(C1);
    }
    if (n != 0 && n <= 25) { // S(26,9) = 11201516780955125625 is larger than Long.MAX_VALUE
      return F.ZZ(CombinatoricsUtils.stirlingS2(n, ki));
    }
    IInteger sum = F.C0;
    for (int i = 0; i < ki; i++) {
      IInteger bin = AbstractIntegerSym.binomial(ki, i);
      IInteger pow = F.ZZ(ki - i).powerRational(n);
      if ((i & 1) == 1) { // isOdd(i) ?
        sum = sum.add(bin.negate().multiply(pow));
      } else {
        sum = sum.add(bin.multiply(pow));
      }
    }
    return sum.div(AbstractIntegerSym.factorial(ki));
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
  private static final int[] MPE_52 = {2, 3, 5, 7, 13, 17, 19, 31, 61, 89, 107, 127, 521, 607, 1279,
      2203, 2281, 3217, 4253, 4423, 9689, 9941, 11213, 19937, 21701, 23209, 44497, 86243, 110503,
      132049, 216091, 756839, 859433, 1257787, 1398269, 2976221, 3021377, 6972593, 13466917,
      20996011, 24036583, 25964951, 30402457, 32582657, 37156667, 42643801, 43112609, 57885161,
      74207281, 77232917, 82589933, 136279841};

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

  public static IAST diophantinePolynomial(final IExpr expr, IAST varList,
      int maximumNumberOfResults) {
    VariablesSet varSet = new VariablesSet(varList);
    IASTMutable result = F.NIL;
    try {
      // try to generate a common expression polynomial
      JASConvert<edu.jas.arith.BigInteger> jas = new JASConvert<edu.jas.arith.BigInteger>(
          varSet.getVarList(), edu.jas.arith.BigInteger.ZERO);
      GenPolynomial<edu.jas.arith.BigInteger> ePoly = jas.expr2JAS(expr, false);
      if (ePoly == null) {
        return F.NIL;
      }
      result = diophantinePolynomial(ePoly, varList, maximumNumberOfResults);
      result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (JASConversionException e2) {
      if (Config.SHOW_STACKTRACE) {
        e2.printStackTrace();
      } ;
    }
    return result;
  }

  private static IASTAppendable diophantinePolynomial(
      GenPolynomial<edu.jas.arith.BigInteger> polynomial, IAST varList,
      int maximumNumberOfResults) {
    long varDegree = polynomial.degree(0);

    if (polynomial.isConstant()) {
      return F.ListAlloc(1);
    }
    // a*x^2 + b*x*y + c*y^2 + d*x + e*y + f = 0
    BigInteger a = BigInteger.ZERO;
    BigInteger b = BigInteger.ZERO;
    BigInteger c = BigInteger.ZERO;
    BigInteger d = BigInteger.ZERO;
    BigInteger e = BigInteger.ZERO;
    BigInteger f = BigInteger.ZERO;
    try {
      if (varDegree <= 2) {
        if (varList.argSize() == 1) {
          // x is only variable => b=0;c=0;e=0;
          for (Monomial<edu.jas.arith.BigInteger> monomial : polynomial) {
            edu.jas.arith.BigInteger coeff = monomial.coefficient();
            BigInteger zz = coeff.val;
            long xExp = monomial.exponent().getVal(0);
            if (xExp == 2) {
              a = zz;
            } else if (xExp == 1) {
              d = zz;
            } else if (xExp == 0) {
              f = zz;
            } else {
              throw new ArithmeticException(
                  "diophantinePolynomial::Unexpected exponent value: " + xExp);
            }
          }
        } else if (varList.argSize() == 2) {
          // x and y are both variables
          // a*x^2 + b*x*y + c*y^2 + d*x + e*y + f = 0
          for (Monomial<edu.jas.arith.BigInteger> monomial : polynomial) {
            edu.jas.arith.BigInteger coeff = monomial.coefficient();
            BigInteger zz = coeff.val;
            int xi = monomial.exponent().varIndex(0);
            int yi = monomial.exponent().varIndex(1);
            long xExp = monomial.exponent().getVal(xi);
            long yExp = monomial.exponent().getVal(yi);
            if (xExp == 2 && yExp == 0) {
              a = zz;
            } else if (xExp == 1 && yExp == 1) {
              b = zz;
            } else if (xExp == 0 && yExp == 2) {
              c = zz;
            } else if (xExp == 1 && yExp == 0) {
              d = zz;
            } else if (xExp == 0 && yExp == 1) {
              e = zz;
            } else if (xExp == 0 && yExp == 0) {
              f = zz;
            } else {
              throw new ArithmeticException(
                  "diophantinePolynomial::Unexpected exponent value: " + xExp);
            }
          }
        }
        Iterator<XYPair> diophantineSolver = null;
        IASTAppendable result = F.ListAlloc();
        if (maximumNumberOfResults == 1 && c.signum() < 0 && f.equals(BigInteger.valueOf(-4))
            && b.signum() == 0 && d.signum() == 0 && e.signum() == 0) {
          BigInteger cNegate = c.negate();
          if (!Utils.isSquare(cNegate)) {
            // use Pell's equation if -c is not a perfect square
            XYPair xyPair = PellsSolver.leastPositivePellsFourSolution(cNegate);
            result.append(F.List(F.Rule(varList.arg1(), F.ZZ(xyPair.x)),
                F.Rule(varList.arg2(), F.ZZ(xyPair.y))));
            return result;
          }
        }
        if (diophantineSolver == null) {
          diophantineSolver = QuadraticSolver.solve(a, b, c, d, e, f);
        }

        int n = 0;
        while (diophantineSolver.hasNext() && n++ < maximumNumberOfResults) {
          XYPair xyPair = diophantineSolver.next();
          result.append(F.List(F.Rule(varList.arg1(), F.ZZ(xyPair.x)),
              F.Rule(varList.arg2(), F.ZZ(xyPair.y))));
        }
        return result;
      }
    } catch (ArithmeticException aex) {
      //
      aex.printStackTrace();
    }

    return F.NIL;
  }


  public static IExpr factorialPower2(IExpr v, IExpr n, EvalEngine engine) {
    IExpr result = F.C1;
    if (n.isNumber() && !n.isInteger() && !(n.isReal() && ((IReal) n).isNumIntValue())) {
      // not a product of n factors: Gamma(1+x)/Gamma(1-n+x)
      return F.Divide(F.Gamma(F.Plus(F.C1, v)), F.Gamma(F.Plus(F.C1, F.Negate(n), v)));
    }
    // x*(x-1)*(x-(n-1))
    if (engine.evalLess(n, F.C0)) {
      return F.NIL;
    } else if (n.isZero()) {
      return F.C1;
    } else if (n.isOne()) {
      return v;
    } else {
      if (engine.isDoubleMode()) {
        if (!v.isMathematicalIntegerNegative()) {
          Complex cx = v.evalfc();
          Complex cn = n.evalfc();
          cx = cx.add(1.0);
          cn = cx.subtract(cn);
          if (!(cn.isMathematicalInteger() && cn.getReal() < 0.0)) {
            // Gamma(1+x)/Gamma(1-n+x)
            if (cx.getImaginary() == 0.0 && cn.getImaginary() == 0.0) {
              // real numbers have a real value
              return F.Divide(F.Gamma(F.num(cx.getReal())), F.Gamma(F.num(cn.getReal())));
            }
            return F.Divide(F.Gamma(F.complexNum(cx)), F.Gamma(F.complexNum(cn)));
          }
        }
        double real = v.evalfNaN();
        if (Double.isNaN(real)) {
          Complex temp = v.evalfc();
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
        double dN = n.evalfNaN();
        if (Double.isNaN(dN)) {
          return F.NIL;
        }
        double i = real - dN + 1;
        while (real >= i) {
          result = result.multiply(v);
          v = v.dec();
          real--;
          if (k++ > iterationLimit && iterationLimit > 0) {
            IterationLimitExceeded.throwIt(k, S.FactorialPower);
          }
        }
        return result;
      } else if (v.isInexactNumber()) {
        // an arbitrary precision number: the product of the n factors
        final int factors = n.toIntDefault();
        final long iterationLimit = engine.getIterationLimit();
        if (factors > 0 && (iterationLimit <= 0 || factors <= iterationLimit)) {
          for (int i = 0; i < factors; i++) {
            result = result.multiply(v);
            v = v.dec();
          }
          return result;
        }
      } else if (v.isExactNumber() && n.isRational()) {
        IRational real = (IRational) ((INumber) v).re();
        IRational dN = (IRational) n;
        IRational i = real.subtract(dN).inc();
        while (real.isGE(i)) {
          result = result.multiply(v);
          v = v.dec();
          real = real.dec();
        }
        return result;
      }
    }
    return F.NIL;
  }
}
