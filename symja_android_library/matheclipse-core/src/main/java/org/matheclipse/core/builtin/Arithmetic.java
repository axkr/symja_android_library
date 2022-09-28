package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Conjugate;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Factorial2;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Positive;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.S.Conjugate;
import static org.matheclipse.core.expression.S.E;
import static org.matheclipse.core.expression.S.Power;
import static org.matheclipse.core.expression.S.Times;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.OverflowException;
import org.apfloat.internal.BackingStorageException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.GammaJS;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PlusOp;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.IRewrite;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvaluator;
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
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherTimes;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesLog;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesTimes;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesTimesPower;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.reflection.system.rules.AbsRules;
import org.matheclipse.core.reflection.system.rules.ConjugateRules;
import org.matheclipse.core.reflection.system.rules.GammaRules;
import org.matheclipse.core.reflection.system.rules.PowerRules;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.math.MathException;

public final class Arithmetic {
  private static final Logger LOGGER = LogManager.getLogger();

  private static int g = 7;
  // private static double[] p = { 0.99999999999980993, 676.5203681218851, -1259.1392167224028,
  // 771.32342877765313,
  // -176.61502916214059, 12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6,
  // 1.5056327351493116e-7 };
  private static org.hipparchus.complex.Complex[] pComplex =
      new org.hipparchus.complex.Complex[] {new org.hipparchus.complex.Complex(0.99999999999980993), //
          new org.hipparchus.complex.Complex(676.5203681218851), //
          new org.hipparchus.complex.Complex(-1259.1392167224028), //
          new org.hipparchus.complex.Complex(771.32342877765313), //
          new org.hipparchus.complex.Complex(-176.61502916214059), //
          new org.hipparchus.complex.Complex(12.507343278686905), //
          new org.hipparchus.complex.Complex(-0.13857109526572012), //
          new org.hipparchus.complex.Complex(9.9843695780195716e-6), //
          new org.hipparchus.complex.Complex(1.5056327351493116e-7) //
      };

  public static final Plus CONST_PLUS = new Plus();
  public static final Times CONST_TIMES = new Times();
  public static final Power CONST_POWER = new Power();
  public static final IFunctionEvaluator CONST_COMPLEX = new Complex();
  public static final IFunctionEvaluator CONST_RATIONAL = new Rational();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Plus.setDefaultValue(F.C0);
      S.Plus.setEvaluator(CONST_PLUS);
      S.Times.setDefaultValue(F.C1);
      S.Times.setEvaluator(CONST_TIMES);
      S.Power.setDefaultValue(2, F.C1);
      S.Power.setEvaluator(CONST_POWER);
      S.Sqrt.setEvaluator(new Sqrt());
      S.Surd.setEvaluator(new Surd());
      S.Minus.setEvaluator(new Minus());

      S.Abs.setEvaluator(new Abs());
      S.AbsArg.setEvaluator(new AbsArg());
      S.AddTo.setEvaluator(new AddTo());
      S.Arg.setEvaluator(new Arg());
      S.Chop.setEvaluator(new Chop());
      S.Clip.setEvaluator(new Clip());
      S.Complex.setEvaluator(CONST_COMPLEX);
      S.ConditionalExpression.setEvaluator(new ConditionalExpression());
      S.Conjugate.setEvaluator(new Conjugate());
      S.CubeRoot.setEvaluator(new CubeRoot());
      S.Decrement.setEvaluator(new Decrement());
      S.Differences.setEvaluator(new Differences());
      S.DirectedInfinity.setEvaluator(new DirectedInfinity());
      S.Divide.setEvaluator(new Divide());
      S.DivideBy.setEvaluator(new DivideBy());
      S.Gamma.setEvaluator(new Gamma());
      S.GCD.setEvaluator(new GCD());
      S.HarmonicNumber.setEvaluator(new HarmonicNumber());
      S.Im.setEvaluator(new Im());
      S.Increment.setEvaluator(new Increment());
      S.LCM.setEvaluator(new LCM());
      S.MantissaExponent.setEvaluator(new MantissaExponent());
      S.N.setEvaluator(new N());
      S.Piecewise.setEvaluator(new Piecewise());
      S.PiecewiseExpand.setEvaluator(new PiecewiseExpand());
      S.Pochhammer.setEvaluator(new Pochhammer());
      S.Precision.setEvaluator(new Precision());
      S.PreDecrement.setEvaluator(new PreDecrement());
      S.PreIncrement.setEvaluator(new PreIncrement());
      S.Rational.setEvaluator(CONST_RATIONAL);
      S.Re.setEvaluator(new Re());
      S.RealAbs.setEvaluator(new RealAbs());
      S.RealSign.setEvaluator(new RealSign());
      S.Sign.setEvaluator(new Sign());
      S.SignCmp.setEvaluator(new SignCmp());
      S.Subtract.setEvaluator(new Subtract());
      S.SubtractFrom.setEvaluator(new SubtractFrom());
      S.TimesBy.setEvaluator(new TimesBy());
    }
  }

  /**
   *
   *
   * <pre>
   * Abs(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the absolute value of the real or complex number <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Absolute_value">Wikipedia - Absolute value</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Abs(-3)
   * 3
   * </pre>
   */
  private static final class Abs extends AbstractTrigArg1
      implements INumeric, AbsRules, DoubleUnaryOperator {

    private static final class AbsNumericFunction implements DoubleFunction<IExpr> {
      final ISymbol symbol;

      public AbsNumericFunction(ISymbol symbol) {
        this.symbol = symbol;
      }

      @Override
      public IExpr apply(double value) {
        if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
          double result = Math.abs(value);
          if (result > 0.0) {
            return symbol;
          }
        }
        return F.NIL;
      }
    }

    private static final class AbsTimesFunction implements Function<IExpr, IExpr> {
      @Override
      public IExpr apply(IExpr expr) {
        if (expr.isNumber()) {
          return ((INumber) expr).abs();
        }
        IExpr temp = F.eval(F.Abs(expr));
        if (!temp.isAbs()) {
          return temp;
        }
        return F.NIL;
      }
    }

    @Override
    public double applyAsDouble(double operand) {
      return Math.abs(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.num(ApcomplexMath.abs(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.abs(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final org.hipparchus.complex.Complex arg1) {
      return F.num(ComplexNum.dabs(arg1));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.abs(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.abs(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isDirectedInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isNumber()) {
        return ((INumber) arg1).abs();
      }
      if (arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isReal()) {
          return arg1.copySign((ISignedNumber) temp);
        }
      }
      if (arg1.isNegativeResult()) {
        return F.Negate(arg1);
      }
      if (arg1.isNonNegativeResult()) {
        return arg1;
      }
      if (arg1.isSymbol()) {
        ISymbol sym = (ISymbol) arg1;
        return sym.mapConstantDouble(new AbsNumericFunction(sym));
      }

      if (arg1.isTimes()) {
        IASTAppendable[] result = ((IAST) arg1).filterNIL(new AbsTimesFunction());
        if (result[0].size() > 1) {
          if (result[1].size() > 1) {
            result[0].append(F.Abs(result[1]));
          }
          return result[0];
        }
      }
      if (arg1.isPower() && arg1.exponent().isRealResult()) {
        return F.Power(F.Abs(arg1.base()), arg1.exponent());
      }
      if (arg1.isNumericFunction(true)) {
        IExpr re = arg1.re();
        if (re.isFree(S.Re) && re.isFree(S.Im)) {
          IExpr im = arg1.im();
          if (im.isFree(S.Re) && im.isFree(S.Im)) {
            return F.Sqrt(F.Plus(F.Sqr(re), F.Sqr(im)));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.abs((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Return a list with the 2 values <code>Abs(x), Arg(x)</code> for a complex number <code>x</code>
   * .
   */
  private static final class AbsArg extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (size == 2) {
        IExpr z = ast.arg1();
        return F.list(F.Abs(z), F.Arg(z));
      }
      return F.NIL;
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
   * AddTo(x, dx)
   *
   * x += dx
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is equivalent to <code>x = x + dx</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 10
   * &gt;&gt; a += 2
   * 12
   *
   * &gt;&gt; a
   * 12
   * </pre>
   */
  private static class AddTo extends AbstractFunctionEvaluator {

    protected IASTMutable getAST(final IExpr value) {
      return (IASTMutable) F.Plus(null, value);
    }

    /**
     * Get the head symbol of this function.
     *
     * @return
     */
    protected ISymbol getFunctionSymbol() {
      return S.AddTo;
    }

    /**
     * Get the corresponding arithmetic head symbol for the function symbol in <code>
     * getFunctionSymbol()</code>
     *
     * @return
     */
    protected ISymbol getArithmeticSymbol() {
      return S.Plus;
    }

    private IExpr assignPart(IExpr part, IExpr value, EvalEngine engine) {
      IExpr oldValue = engine.evaluate(part);
      IASTMutable operator = getAST(value);
      operator.set(1, oldValue);
      IExpr newResult = engine.evaluate(operator);
      engine.evaluate(F.Set(part, newResult));
      return newResult;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr leftHandSide = ast.arg1();
      final IExpr head = leftHandSide.head();
      try {
        if (head.isBuiltInSymbol() && leftHandSide.isAST()) {
          IEvaluator eval = ((IBuiltInSymbol) head).getEvaluator();
          if (eval instanceof ISetEvaluator) {
            IExpr temp = engine.evaluateNIL(leftHandSide);
            if (!temp.isPresent()) {
              return F.NIL;
            }
            IExpr rhs = engine.evaluate(F.binaryAST2(getArithmeticSymbol(), temp, ast.arg2()));
            return ((ISetEvaluator) eval).evaluateSet(leftHandSide, rhs, S.Set, engine);
          }
        }
        if (leftHandSide.isASTSizeGE(S.Part, 3) && leftHandSide.first().isSymbol()) {
          ISymbol sym = (ISymbol) leftHandSide.first();
          if (sym.hasAssignedSymbolValue()) {
            return assignPart(leftHandSide, ast.arg2(), engine);
          }
          // `1` is not a variable with a value, so its value cannot be changed.
          return IOFunctions.printMessage(ast.topHead(), "rvalue", F.list(sym), engine);
        }
        if (leftHandSide.isSymbol()) {
          ISymbol sym = (ISymbol) leftHandSide;
          if (!sym.hasAssignedSymbolValue()) {
            // `1` is not a variable with a value, so its value cannot be changed.
            return IOFunctions.printMessage(getFunctionSymbol(), "rvalue", F.list(sym), engine);
          }
          IExpr arg2 = engine.evaluate(ast.arg2());
          IExpr[] results = sym.reassignSymbolValue(getAST(arg2), getFunctionSymbol(), engine);
          if (results != null) {
            return results[1];
          }
          return F.NIL;
        }
      } catch (ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
        return F.NIL;
      }
      // `1` is not a variable with a value, so its value cannot be changed.
      return IOFunctions.printMessage(getFunctionSymbol(), "rvalue", F.list(leftHandSide), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Arg(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the argument of the complex number <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Argument_%28complex_analysis%29">Wikipedia - Argument
   * (complex_analysis)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Arg(1+I)
   * Pi/4
   * </pre>
   */
  private static class Arg extends AbstractCoreFunctionEvaluator
      implements INumeric, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      if (operand < 0) {
        return Math.PI;
      }
      return 0.0;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      if (stack[top] < 0) {
        return Math.PI;
      } else if (stack[top] >= 0) {
        return 0;
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = F.NIL;
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        result = F.Arg(arg1);
      } else {
        arg1 = ast.arg1();
      }
      if (arg1.isList()) {
        return ((IAST) arg1).mapThread(F.Arg(F.Slot1), 1);
      }
      if (arg1.isNumber()) {
        return ((INumber) arg1).complexArg();
      } else if (arg1.isIndeterminate()) {
        return S.Indeterminate;
      } else if (arg1.isDirectedInfinity()) {
        IAST directedInfininty = (IAST) arg1;
        if (directedInfininty.isAST1()) {
          if (directedInfininty.isInfinity()) {
            return F.C0;
          }
          return F.Arg(directedInfininty.arg1());
        } else if (arg1.isComplexInfinity()) {
          // Indeterminate expression `1` encountered.
          IOFunctions.printMessage(ast.topHead(), "indet", F.list(ast), engine);
          return F.Interval(F.list(F.CNPi, S.Pi));
        }

      } else if (arg1.isTimes() && arg1.first().isRealResult()) {
        IExpr first = arg1.first();
        if (first.isPositive()) {
          return F.Arg(arg1.rest());
        }
        if (first.isNegative() && !first.isMinusOne()) {
          return F.Arg(F.Times(F.CN1, arg1.rest()));
        }
      } else if (arg1.isPower()) {
        if (arg1.exponent().isFraction()) {
          IFraction exponent = (IFraction) arg1.exponent();
          if (exponent.numerator().isOne()) {
            IInteger n = exponent.denominator();
            if (!n.isMinusOne() && !n.isZero()) {
              // Arg(z^(1/n)) => Arg(z)/n
              return F.Divide(F.Arg(arg1.base()), n);
            }
          }
        }
        if (arg1.base().isE()) {
          IExpr exponent = arg1.exponent();
          IExpr imPart = AbstractFunctionEvaluator.imaginaryPart(exponent, false);
          if (imPart.isPresent()) {
            IExpr rePart = AbstractFunctionEvaluator.realPart(exponent, false);
            if (rePart.isZero() && !imPart.isZero()) {
              // Arg(E^(I*z)) => Re(z) + 2*Pi*Floor((Pi - Re(z))/(2*Pi))
              return F.Plus(
                  F.Times(F.C2, S.Pi, F.Floor(
                      F.Times(F.C1D2, F.Power(S.Pi, -1), F.Plus(S.Pi, F.Negate(F.Re(imPart)))))),
                  F.Re(imPart));
            }
            // Arg(E^z) => Im(z) + 2*Pi*Floor((Pi - Im(z))/(2*Pi))
            return F
                .Plus(
                    F.Times(F.C2, S.Pi,
                        F.Floor(
                            F.Times(F.C1D2, F.Power(S.Pi, -1), F.Plus(S.Pi, F.Negate(imPart))))),
                    imPart);
          }
        }
      } else if (arg1.isInterval()) {
        IAST interval = (IAST) arg1;
        if (interval.isAST1()) {
          IAST list = (IAST) interval.arg1();
          IExpr lhs = engine.evaluate(F.Arg(list.arg1()));
          IExpr rhs = engine.evaluate(F.Arg(list.arg2()));
          if (lhs.equals(rhs)) {
            return F.Interval(F.List(lhs, rhs));
          }
        }
      }
      if (arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isRealResult()) {
          if (temp.isNegative()) {
            return S.Pi;
          }
          return F.C0;
        } else if (temp.isNumber() && engine.isNumericMode()) {
          return ((INumber) temp).complexArg();
        }
      }

      if (AbstractAssumptions.assumeNegative(arg1)) {
        return S.Pi;
      }
      if (AbstractAssumptions.assumePositive(arg1)) {
        return F.C0;
      }
      IExpr imPart = AbstractFunctionEvaluator.imaginaryPart(arg1, true);
      if (imPart.isPresent()) {
        return F.ArcTan(F.Re(arg1), imPart);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * Chop(numerical - expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces numerical values in the <code>numerical-expr</code> which are close to zero with
   * symbolic value <code>0</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Chop(0.00000000001)
   * 0
   * </pre>
   */
  private static class Chop extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      double delta = Config.DEFAULT_CHOP_DELTA;
      if (ast.isAST2() && ast.arg2() instanceof INum) {
        delta = ((INum) ast.arg2()).getRealPart();
      }
      try {
        // arg1 = engine.evaluate(arg1);
        if (arg1.isAST()) {
          IAST list = (IAST) arg1;
          // Chop[{a,b,c}] -> {Chop[a],Chop[b],Chop[c]}
          return list.mapThread(F.Chop(F.Slot1), 1);
        }
        if (arg1.isNumber()) {
          return F.chopNumber((INumber) arg1, delta);
        }
      } catch (Exception e) {
        LOGGER.debug("Chop.evaluate() failed", e);
      }

      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Clip(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>-1</code> to <code>1</code>. Returns <code>-1
   * </code> if <code>expr</code> is less than <code>-1</code>. Returns <code>1</code> if <code>expr
   * </code> is greater than <code>1</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Clip(expr, {min, max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>
   * min</code> if <code>expr</code> is less than <code>min</code>. Returns <code>max</code> if
   * <code>expr</code> is greater than <code>max</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Clip(expr, {min, max}, {vMin, vMax})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>
   * vMin</code> if <code>expr</code> is less than <code>min</code>. Returns <code>vMax</code> if
   * <code>expr</code> is greater than <code>max</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Clip(Sin(Pi/7))
   * Sin(Pi/7)
   *
   * &gt;&gt; Clip(Tan(E))
   * Tan(E)
   *
   * &gt;&gt; Clip(Tan(2*E)
   * -1
   *
   * &gt;&gt; Clip(Tan(-2*E))
   * 1
   *
   * &gt;&gt; Clip(x)
   * Clip(x)
   *
   * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2})
   * -1/2
   *
   * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2})
   * 1/2
   *
   * &gt;&gt; Clip(Tan(E), {-1/2,1/2}, {a,b})
   * Tan(E)
   *
   * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2}, {a,b})
   * a
   *
   * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2}, {a,b})
   * b
   * </pre>
   */
  private static final class Clip extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.first();
      if (x.isSparseArray()) {
        x = x.normal(false);
      }
      if (x.isList()) {
        IAST list = (IAST) x;
        IAST result = list.map(a -> {
          IASTMutable copy = ast.copy();
          copy.set(1, a);
          IExpr temp = evaluate(copy, engine);
          if (temp.isPresent()) {
            return temp;
          }
          ArgumentTypeStopException.throwNIL();
          return F.NIL;
        });
        return result;
      }

      if (ast.size() == 2) {
        try {
          return clipX(x);
        } catch (ArgumentTypeStopException atsex) {
          return F.NIL;
        }
      }

      IExpr vMin = null;
      IExpr vMax = null;
      if (ast.size() == 4) {
        IExpr arg3 = ast.arg3();
        if (arg3.isAST(S.List, 3)) {
          // { vMin, vMax } as 3rd argument expected
          vMin = arg3.first();
          vMax = arg3.second();
        } else {
          return F.NIL;
        }
      }
      if (ast.size() >= 3) {
        IExpr arg2 = ast.arg2();
        if (arg2.isAST(S.List, 3)) {
          // { min, max } as 2nd argument expected
          IExpr min = arg2.first();
          IExpr max = arg2.second();
          if (ast.size() == 3) {
            vMin = min;
            vMax = max;
          }
          try {
            if (min.isReal() && max.isReal()) {
              return clip(x, (ISignedNumber) min, (ISignedNumber) max, vMin, vMax);
            }
            ISignedNumber minEvaled = min.evalReal();
            if (minEvaled != null) {
              ISignedNumber maxEvaled = max.evalReal();
              if (maxEvaled != null) {
                return clip(x, minEvaled, maxEvaled, vMin, vMax);
              }
            }
          } catch (ArgumentTypeStopException atsex) {
            return F.NIL;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    private static IExpr clipX(IExpr x) {
      if (x.isReal()) {
        ISignedNumber real = (ISignedNumber) x;
        if (real.isGT(F.C1)) {
          return F.C1;
        }
        if (real.isLT(F.CN1)) {
          return F.CN1;
        }
        return x;
      }
      ISignedNumber real = x.evalReal();
      if (real != null) {
        if (real.isGT(F.C1)) {
          return F.C1;
        }
        if (real.isLT(F.CN1)) {
          return F.CN1;
        }
        return x;
      }
      if (x.isInfinity()) {
        return F.C1;
      }
      if (x.isNegativeInfinity()) {
        return F.CN1;
      }
      return F.NIL;
    }



    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Complex
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the head of complex numbers.
   *
   * </blockquote>
   *
   * <pre>
   * Complex(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * constructs the complex number <code>a + I * b</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Head(2 + 3*I)
   * Complex
   *
   * &gt;&gt; Complex(1, 2/3)
   * 1+I*2/3
   *
   * &gt;&gt; Abs(Complex(3, 4))
   * 5
   *
   * &gt;&gt; -2 / 3 - I
   * -2/3-I
   *
   * &gt;&gt; Complex(10, 0)
   * 10
   *
   * &gt;&gt; 0. + I
   * I*1.0
   *
   * &gt;&gt; 1 + 0*I
   * 1
   *
   * &gt;&gt; Head(1 + 0*I)
   * Integer
   *
   * &gt;&gt; Complex(0.0, 0.0)
   * 0.0
   *
   * &gt;&gt; 0.*I
   * 0.0
   *
   * &gt;&gt; 0. + 0.*I
   * 0.0
   *
   * &gt;&gt; 1. + 0.*I
   * 1.0
   *
   * &gt;&gt; 0. + 1.*I
   * I*1.0
   * </pre>
   *
   * <p>
   * Check nesting Complex
   *
   * <pre>
   * &gt;&gt; Complex(1, Complex(0, 1))
   * 0
   *
   * &gt;&gt; Complex(1, Complex(1, 0))
   * 1+I
   *
   * &gt;&gt; Complex(1, Complex(1, 1))
   * I
   * </pre>
   */
  private static final class Complex extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.head().equals(S.Complex)) {
          if (!ast.isAST2()) {
            return IOFunctions.printArgMessage(ast, ARGS_2_2, engine);
          }
          IExpr realExpr = ast.arg1();
          IExpr imaginaryExpr = ast.arg2();
          if (realExpr.isRational() && imaginaryExpr.isRational()) {
            // already evaluated
          } else if (realExpr instanceof INum && imaginaryExpr instanceof INum) {
            // already evaluated
          } else {
            realExpr = engine.evaluate(realExpr);
            imaginaryExpr = engine.evaluate(imaginaryExpr);
            if (!realExpr.isNumber() || !imaginaryExpr.isNumber()) {
              return F.NIL;
            }
          }

          if (realExpr.isRational() && imaginaryExpr.isRational()) {
            IRational re;
            if (realExpr.isInteger()) {
              re = (IInteger) realExpr; // F.fraction((IInteger) arg1, F.C1);
            } else {
              re = (IFraction) realExpr;
            }
            IRational im;
            if (imaginaryExpr.isInteger()) {
              im = (IInteger) imaginaryExpr; // F.fraction((IInteger) arg2, F.C1);
            } else {
              im = (IFraction) imaginaryExpr;
            }
            return F.complex(re, im);
          }
          if (realExpr instanceof INum && imaginaryExpr instanceof INum) {
            return F.complexNum(((INum) realExpr).doubleValue(),
                ((INum) imaginaryExpr).doubleValue());
          }
          if (realExpr.isNumber() && imaginaryExpr.isNumber()) {
            return F.Plus(realExpr, F.Times(F.CI, imaginaryExpr));
          }

          // don't optimize this way:
          // if (imaginaryExpr.isZero()) {
          // return realExpr;
          // }
          // if (realExpr.isZero()) {
          // return F.Times(F.CI, imaginaryExpr);
          // }
        }
      } catch (Exception e) {
        LOGGER.debug("Complex.evaluate() failed", e);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class ConditionalExpression extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (arg2.isTrue()) {
        return ast.arg1();
      }
      if (arg2.isFalse()) {
        return S.Undefined;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set NUMERICFUNCTION
      // newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Conjugate(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the complex conjugate of the complex number <code>z</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Conjugate(3 + 4*I)
   * 3 - 4 I
   *
   * &gt;&gt; Conjugate(3)
   * 3
   *
   * &gt;&gt; Conjugate(a + b * I)
   * -I*Conjugate(b)+Conjugate(a)
   *
   * &gt;&gt; Conjugate({{1, 2 + I*4, a + I*b}, {I}})
   * {{1,2-I*4,-I*Conjugate(b)+Conjugate(a)},{-I}}
   *
   * &gt;&gt; {Conjugate(Pi), Conjugate(E)}
   * {Pi,E}
   *
   * &gt;&gt; Conjugate(1.5 + 2.5*I)
   * 1.5+I*(-2.5)
   * </pre>
   */
  private static final class Conjugate extends AbstractTrigArg1
      implements INumeric, ConjugateRules {

    /**
     * Conjugate numbers and special objects like <code>Infinity</code> and <code>Indeterminate
     * </code>.
     *
     * @param arg1
     * @return {@link F#NIL} if the evaluation wasn't possible
     */
    private IExpr conjugate(IExpr arg1) {
      if (arg1.isNumber()) {
        return ((INumber) arg1).conjugate();
      }
      if (arg1.isRealResult() || arg1.isRealVector() || arg1.isRealMatrix()) {
        return arg1;
      }
      if (arg1.isQuantity()) {
        return arg1.conjugate();
      }
      if (arg1.isDirectedInfinity()) {
        IAST directedInfininty = (IAST) arg1;
        if (directedInfininty.isComplexInfinity()) {
          return F.CComplexInfinity;
        }
        if (directedInfininty.isAST1()) {
          if (directedInfininty.isInfinity()) {
            return F.CInfinity;
          }
          IExpr conjug = F.eval(F.Conjugate(directedInfininty.arg1()));
          return F.Times(conjug, F.CInfinity);
        }
      }
      return F.NIL;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return stack[top];
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      return evaluateArg1(arg1, engine);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr temp = conjugate(arg1);
      if (temp.isPresent()) {
        return temp;
      }
      if (arg1.isPower()) {
        IExpr base = arg1.base();
        if (base.isPositiveResult()) {
          return F.Power(base, F.Conjugate(arg1.exponent()));
        }
      }
      if (arg1.isPlus()) {
        return ((IAST) arg1).mapThread(F.Conjugate(F.Slot1), 1);
      }
      if (arg1.isTimes()) {
        IASTAppendable result = F.NIL;
        IASTAppendable clone = ((IAST) arg1).copyAppendable();
        int i = 1;
        while (i < clone.size()) {
          temp = conjugate(clone.get(i));
          if (temp.isPresent()) {
            clone.remove(i);
            if (!result.isPresent()) {
              result = ((IAST) arg1).copyHead();
            }
            result.append(temp);
            continue;
          }
          i++;
        }
        if (result.isPresent()) {
          if (clone.isAST0()) {
            return result;
          }
          if (clone.isAST0()) {
            result.append(F.Conjugate(clone.arg1()));
            return result;
          }
          result.append(F.Conjugate(clone));
          return result;
        }
      } else if (arg1.isConjugate()) {
        return arg1.first();
      } else if (arg1.isAST(S.Zeta, 2)) {
        return F.Zeta(F.Conjugate(arg1.first()));
      } else if (arg1.isAST(S.Zeta, 3) && arg1.first().isReal() && arg1.second().isReal()) {
        return F.Zeta(F.Conjugate(arg1.first()), F.Conjugate(arg1.second()));
      }
      if (arg1.isNumericFunction(true)) {
        IExpr im = arg1.im();
        if (im.isFree(S.Re) && im.isFree(S.Im)) {
          // arg1 - 2 * im * I
          return F.Subtract(arg1, F.Times(F.C2, F.CI, im));
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.mapSymbol(S.Conjugate, (IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class CubeRoot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr base = ast.arg1();
      if (base.isNumericFunction(true)) {
        if (base.isComplex() || base.isComplexNumeric()) {
          return IOFunctions.printMessage(ast.topHead(), "preal", F.list(base), engine);
        }
        if (base.isPositiveResult()) {
          return F.Power(base, F.C1D3);
        }
        return F.Times(F.CN1, F.Power(F.Negate(base), F.C1D3));
      }
      return F.Surd(base, F.C3);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
   * Decrement(x)
   *
   * x--
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * decrements <code>x</code> by <code>1</code>, returning the original value of <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 5
   * &gt;&gt; a--
   * 5
   *
   * &gt;&gt; a
   * 4
   * </pre>
   */
  private static class Decrement extends AbstractFunctionEvaluator {

    protected IASTMutable getAST() {
      return (IASTMutable) F.Plus(null, F.CN1);
    }

    public Decrement() {
      super();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isASTSizeGE(S.Part, 3) && arg1.first().isSymbol()) {
          ISymbol sym = (ISymbol) arg1.first();
          if (sym.hasAssignedSymbolValue()) {
            return assignPart(arg1, F.CN1, engine);
          }
          // `1` is not a variable with a value, so its value cannot be changed.
          return IOFunctions.printMessage(ast.topHead(), "rvalue", F.list(sym), engine);
        }
        if (arg1.isSymbol()) {
          ISymbol sym = (ISymbol) arg1;
          if (sym.hasAssignedSymbolValue()) {
            IExpr[] results =
                ((ISymbol) arg1).reassignSymbolValue(getAST(), getFunctionSymbol(), engine);
            if (results != null) {
              return getResult(results[0], results[1]);
            }
          } else {
            // `1` is not a variable with a value, so its value cannot be changed.
            return IOFunctions.printMessage(ast.topHead(), "rvalue", F.list(sym), engine);
          }
        } else {
          // `1` is not a variable with a value, so its value cannot be changed.
          return IOFunctions.printMessage(ast.topHead(), "rvalue", F.list(arg1), engine);
        }
      } catch (ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
      }
      return F.NIL;
    }

    private IExpr assignPart(IExpr part, IExpr value, EvalEngine engine) {
      IExpr oldResult = engine.evaluate(part);
      IASTMutable operator = getAST();
      operator.set(1, oldResult);
      IExpr newResult = engine.evaluate(operator);
      engine.evaluate(F.Set(part, newResult));
      // return oldResult;
      return getResult(oldResult, newResult);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    protected ISymbol getFunctionSymbol() {
      return S.Decrement;
    }

    protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
      return symbolValue;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }


  private static class Differences extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int c = 1;
      if (ast.isAST2()) {
        int n = ast.arg2().toIntDefault();
        if (n == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (n < 0) {
          // Single or list of non-negative machine-sized integers expected at position `1` of `2`.
          return IOFunctions.printMessage(ast.topHead(), "ilsmn", F.list(F.C2, ast), engine);
        }
        c = n;
      }
      if (ast.isAST1()) {
        if (arg1.isSparseArray()) {
          arg1 = arg1.normal(false);
        }
        if (arg1.isList()) {
          if (arg1.size() <= 2) {
            return F.CEmptyList;
          }
          return F.ListConvolve(F.list(F.ZZ(c), F.ZZ(-c)), arg1);
        } else if (arg1.isNumber()) {
          // List or SparseArray or structured array expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "listrp", F.list(F.C1, ast), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * Divide(a, b)
   *
   * a / b
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents the division of <code>a</code> by <code>b</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 30 / 5
   * 6
   *
   * &gt;&gt; 1 / 8
   * 1/8
   *
   * &gt;&gt; Pi / 4
   * Pi / 4
   * </pre>
   *
   * <p>
   * Use <code>N</code> or a decimal point to force numeric evaluation:
   *
   * <pre>
   * &gt;&gt; Pi / 4.0
   * 0.7853981633974483
   *
   * &gt;&gt; N(1 / 8)
   * 0.125
   * </pre>
   *
   * <p>
   * Nested divisions:
   *
   * <pre>
   * &gt;&gt; a / b / c
   * a/(b*c)
   *
   * &gt;&gt; a / (b / c)
   * (a*c)/b
   *
   * &gt;&gt; a / b / (c / (d / e))
   * (a*d)/(b*c*e)
   *
   * &gt;&gt; a / (b ^ 2 * c ^ 3 / e)
   * (a*e)/(b^2*c^3)
   *
   * &gt;&gt; 1 / 4.0
   * 0.25
   *
   * &gt;&gt; 10 / 3 // FullForm
   * "Rational(10,3)"
   *
   * &gt;&gt; a / b // FullForm
   * "Times(a, Power(b, -1))"
   * </pre>
   */
  private static class Divide extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // arg1 * arg2^(-1)
      return F.Divide(ast.arg1(), ast.arg2());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * DivideBy(x, dx)
   *
   * x /= dx
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is equivalent to <code>x = x / dx</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 10
   * &gt;&gt; a /= 2
   * 5
   *
   * &gt;&gt; a
   * 5
   * </pre>
   */
  private static class DivideBy extends AddTo {

    @Override
    protected IASTMutable getAST(final IExpr value) {
      return F.Times(null, F.Power(value, F.CN1));
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.DivideBy;
    }

    @Override
    protected ISymbol getArithmeticSymbol() {
      return S.Divide;
    }
  }


  private static final class DirectedInfinity extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        boolean numericMode = engine.isNumericMode();
        boolean evaled = false;
        try {
          engine.setNumericMode(false);
          IExpr arg1 = ast.arg1();
          IExpr temp = engine.evaluateNIL(arg1);
          if (temp.isPresent()) {
            arg1 = temp;
            evaled = true;
          }
          if (arg1.isIndeterminate() || arg1.isZero()) {
            return F.CComplexInfinity;
          }
          if (arg1.isReal()) {
            if (arg1.isOne() || arg1.isMinusOne()) {
              if (evaled) {
                return F.DirectedInfinity(arg1);
              }
              return F.NIL;
            }
            if (arg1.isNegative()) {
              return F.DirectedInfinity(F.CN1);
            } else {
              return F.DirectedInfinity(F.C1);
            }
          }
          if (arg1.isNumber()) {
            IExpr arg1Abs = engine.evaluate(F.Divide(arg1, F.Abs(arg1)));
            if (arg1.equals(arg1Abs)) {
              if (evaled) {
                return F.DirectedInfinity(arg1);
              }
              return F.NIL;
            }
            return F.DirectedInfinity(arg1Abs);
          }
          if (arg1.isNumericFunction(true)) {
            IExpr a1 = engine.evalN(arg1);
            if (a1.isReal()) {
              if (a1.isZero()) {
                return F.CComplexInfinity;
              }
              if (a1.isNegative()) {
                return F.DirectedInfinity(F.CN1);
              } else {
                return F.DirectedInfinity(F.C1);
              }
            }
          }
          if (evaled) {
            return F.DirectedInfinity(arg1);
          }
        } finally {
          engine.setNumericMode(numericMode);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    public static IExpr timesInf(IAST inf, IExpr a2) {
      if (inf.isAST1()) {
        IExpr result;
        IExpr a1 = inf.arg1();
        if (a1.isNumber()) {
          if (a2.isNumber()) {
            result = a1.times(a2);
            if (result.isReal()) {
              if (result.isNegative()) {
                return F.CNInfinity;
              } else {
                return F.CInfinity;
              }
            } else if (result.isImaginaryUnit()) {
              return F.DirectedInfinity(F.CI);
            } else if (result.isNegativeImaginaryUnit()) {
              return F.DirectedInfinity(F.CNI);
            }
          } else if (a2.isSymbol()) {
            if (a1.isOne()) {
              return F.DirectedInfinity(a2);
            } else if (a1.isMinusOne() || a1.equals(F.CI) || a1.equals(F.CNI)) {
              return F.DirectedInfinity(F.Times(a1, a2));
            }
          }
        } else if (a1.isSymbol()) {
          if (a2.isReal()) {
            if (a2.isNegative()) {
              return F.DirectedInfinity(F.Times(F.CN1, F.Sign(a1)));
            } else {
              return F.DirectedInfinity(a1);
            }
          } else if (a2.equals(F.CI)) {
            return F.DirectedInfinity(F.Times(F.CI, a1));
          } else if (a2.equals(F.CNI)) {
            return F.DirectedInfinity(F.Times(F.CNI, F.Sign(a1)));
          }
        }
        result = F.Divide(F.Times(a1, a2), F.Abs(F.Times(a1, a2)));
        return F.DirectedInfinity(result);
      }
      // ComplexInfinity
      return inf;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set ISymbol.NUMERICFUNCTION);
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
    }
  }


  /**
   *
   *
   * <pre>
   * Gamma(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the gamma function on the complex number <code>z</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Gamma(8)
   * 5040
   *
   * &gt;&gt; Gamma(1/2)
   * Sqrt(Pi)
   *
   * &gt;&gt; Gamma(2.2)
   * 1.1018024908797128
   * </pre>
   */
  private static final class Gamma extends AbstractArg12 implements GammaRules {

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      if (arg1.isInteger() && arg1.real().compareTo(Apfloat.ZERO) < 0) {
        return F.CComplexInfinity;
      }
      return F.complexNum(ApcomplexMath.gamma(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        if (arg1.isInteger() && arg1.compareTo(Apfloat.ZERO) < 0) {
          return F.CComplexInfinity;
        }
        return F.num(h.gamma(arg1));
      } catch (ArithmeticException aex1) {
        try {
          return F.complexNum(h.gamma(new Apcomplex(arg1, Apcomplex.ZERO)));
        } catch (ArithmeticException aex2) {
          return F.NIL;
        }
      }
    }

    @Override
    public IExpr e1DblComArg(final IComplexNum c) {
      // Apcomplex gamma =
      // ApcomplexMath.gamma(c.apcomplexNumValue(Config.MACHINE_PRECISION).apcomplexValue());
      // return F.complexNum(gamma.real().doubleValue(), gamma.imag().doubleValue());
      if (c.isNumIntValue() && c.getRealPart() < 0) {
        return F.CComplexInfinity;
      }

      // TODO improve lanczos approx:
      return F.complexNum(lanczosApproxGamma(c.evalComplex()));
    }

    @Override
    public IExpr e1DblArg(final INum arg1) {
      double d1 = arg1.doubleValue();
      if (arg1.isNumIntValue() && d1 < 0) {
        return F.CComplexInfinity;
      }
      double gamma = org.hipparchus.special.Gamma.gamma(d1);
      if (Double.isNaN(gamma)) {
        if (d1 > 0.0) {
          // Overflow occurred in computation.
          IOFunctions.printMessage(S.Gamma, "ovfl", F.CEmptyList, EvalEngine.get());
          return F.Overflow();
        }
        return e1DblComArg(F.complexNum(d1));
      }
      return num(gamma);
    }

    @Override
    public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
      if (d0.isZero() && d1.isZero()) {
        return F.CInfinity;
      }
      return F.complexNum(GammaJS.gamma(d0.evalComplex(), d1.evalComplex()));
    }

    @Override
    public IExpr e2ApcomplexArg(final ApcomplexNum arg1, final ApcomplexNum arg2) {
      try {
        return F.complexNum(ApcomplexMath.gamma(arg1.apcomplexValue(), arg2.apcomplexValue()));
      } catch (ArithmeticException ae) {
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApfloatArg(final ApfloatNum arg1, final ApfloatNum arg2) {
      try {
        return F.num(ApfloatMath.gamma(arg1.apfloatValue(), arg2.apfloatValue()));
      } catch (ArithmeticException aex1) {
        try {
          return e2ApcomplexArg(ApcomplexNum.valueOf(arg1.apfloatValue()),
              ApcomplexNum.valueOf(arg2.apfloatValue()));
        } catch (ArithmeticException aex2) {
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr e2DblArg(final INum d0, final INum d1) {
      if (d0.isZero() && d1.isZero()) {
        return F.CInfinity;
      }
      if (d0.isZero() || d0.isNegative() || d1.isNegative()) {
        return F.complexNum(GammaJS.gamma(d0.evalComplex(), d1.evalComplex()));
      }
      double c = GammaJS.gamma(d0.doubleValue(), d1.doubleValue());
      // if (F.isZero(c.getImaginary())) {
      // return F.num(c.getReal());
      // }
      return F.num(c);
      // return F.num(de.lab4inf.math.functions.IncompleteGamma.incGammaP(d0.doubleValue(),
      // d1.doubleValue()));
    }

    @Override
    public IExpr e2ObjArg(final IExpr o0, final IExpr z) {
      if (z.isZero()) {
        if (o0.isZero()) {
          return F.CInfinity;
        }
        if (o0.isMathematicalIntegerNegative()) {
          return F.CComplexInfinity;
        }
        if (o0.isPositiveResult()) {
          return F.Gamma(o0);
        }
        IExpr re = o0.re();
        if (re.isNegative()) {
          return F.CComplexInfinity;
        }
        if (re.isPositive()) {
          return F.Gamma(o0);
        }
      }
      int n = o0.toIntDefault();
      if (n > 0 && z.isNumericFunction(true)) {
        //
        // Gamma(n,z) = ((n - 1)! * Sum(z^k/k!, {k, 0, n - 1}))/E^z
        //
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= n + 1) {
          IterationLimitExceeded.throwIt(n, F.Gamma(o0, z));
        }
        IASTAppendable sum = F.PlusAlloc(n);
        for (int k = 0; k < n; k++) {
          sum.append(F.Divide(F.Power(z, k), F.Factorial(F.ZZ(k))));
        }
        return F.Times(F.Factorial(F.ZZ(n - 1)), sum, F.Power(E, z.negate()));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr a = ast.arg1();
        if (ast.isAST3()) {
          // see GammaRules.m - Gamma(a_, x_, y_) := Gamma(a, x) - Gamma(a, y)
          return F.NIL;
        }
        if (ast.size() != 3) {
          return unaryOperator(a);
        }
        return binaryOperator(ast, a, ast.arg2());
      } catch (ApfloatRuntimeException | ValidateException e) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public IExpr e1ObjArg(final IExpr arg1) {
      if (arg1.isZero()) {
        return F.CComplexInfinity;
      }
      if (arg1.isMathematicalIntegerNegative()) {
        return F.CComplexInfinity;
      }
      if (arg1.isIntegerResult()) {
        // if (arg1.isNegative()) {
        // return F.CComplexInfinity;
        // }
        if (arg1.isNonNegativeResult()) {
          if (arg1.isInteger()) {
            return NumberTheory.factorial(((IInteger) arg1).subtract(F.C1));
          }
          return F.Factorial(arg1.subtract(F.C1));
        }
        return F.NIL;
      }
      if (arg1.isFraction()) {
        IFraction frac = (IFraction) arg1;
        if (frac.denominator().equals(C2)) {
          IInteger n = frac.numerator();
          if (arg1.isNegative()) {
            n = n.negate();
            return Times(Power(CN1, Times(C1D2, Plus(C1, n))), Power(C2, n), F.CSqrtPi,
                Power(Factorial(n), -1), Factorial(Times(C1D2, Plus(CN1, n))));
          } else {
            // Sqrt(Pi) * (n-2)!! / 2^((n-1)/2)
            return Times(F.CSqrtPi, Factorial2(n.subtract(C2)),
                Power(C2, Times(C1D2, Subtract(C1, n))));
          }
        }
      } else if (arg1.isAST()) {
        IAST z = (IAST) arg1;
        if (z.isAST(Conjugate, 2)) {
          // mirror symmetry for Conjugate()
          return Conjugate(F.Gamma(z.arg1()));
        }
        if (z.isAST(S.Overflow, 1) || z.isAST(S.Underflow, 1)) {
          return F.Overflow();
        }
      }
      return NIL;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * GCD(n1, n2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the greatest common divisor of the given integers.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; GCD(20, 30)
   * 10
   * &gt;&gt; GCD(10, y)
   * GCD(10, y)
   * </pre>
   *
   * <p>
   * 'GCD' is 'Listable':
   *
   * <pre>
   * &gt;&gt; GCD(4, {10, 11, 12, 13, 14})
   * {2, 1, 4, 1, 2}
   * </pre>
   */
  private static final class GCD extends AbstractArgMultiple {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.C0;
      } else if (ast.isAST1()) {
        if (ast.arg1().isExactNumber()) {
          return ast.arg1().abs();
        }
      }
      return super.evaluate(ast, engine);
    }

    @Override
    public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
      return e2ComArg(c0, F.complex(i1, F.C0));
    }

    @Override
    public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
      // TODO implement GCD for gaussian integers
      IInteger[] gi0 = c0.gaussianIntegers();
      IInteger[] gi1 = c1.gaussianIntegers();

      if (gi0 != null && gi1 != null) {
        // ComplexSym devidend = ComplexSym.valueOf(c0.getRealPart(), c0.getImaginaryPart());
        IInteger[] result = GaussianInteger.gcd(gi0, gi1);
        if (result != null) {
          return F.complex(result[0], result[1]);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr e2FraArg(IFraction f0, IFraction f1) {
      return f0.gcd(f1);
    }

    /** Compute gcd of 2 integer numbers */
    @Override
    public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
      return i0.gcd(i1);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol
          .setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
    }
  }


  /**
   *
   *
   * <pre>
   * <code>HarmonicNumber(n)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>n</code>th harmonic number.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Harmonic_number">Wikipedia - Harmonic number</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Table(HarmonicNumber(n), {n, 8})
   * {1,3/2,11/6,25/12,137/60,49/20,363/140,761/280}
   * </code>
   * </pre>
   */
  private static final class HarmonicNumber extends AbstractEvaluator {

    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.digamma(arg1.add(Apfloat.ONE)).add(ApfloatMath.euler(arg1.precision())));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F
            .complexNum(h.digamma(arg1.add(Apfloat.ONE)).add(ApfloatMath.euler(arg1.precision())));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      try {
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          return harmonic(arg1, arg2, ast, engine);
        }
        return harmonic(arg1, ast, engine);
      } catch (final ValidateException ve) {
        // int number validation
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
        return F.NIL;
      }
    }

    private static IExpr harmonic(IExpr arg1, final IAST ast, EvalEngine engine) {
      if (arg1.isNumber()) {
        if (arg1.isMathematicalIntegerNegative()) {
          return F.CComplexInfinity;
        }
        if (engine.isNumericMode()) {
          return F.Plus(S.EulerGamma, F.PolyGamma(F.C0, F.Plus(F.C1, arg1)));
        }
      }
      if (arg1.isInteger()) {
        if (arg1.isNegative()) {
          return F.CComplexInfinity;
        }
        int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
        if (n < 0) {
          return F.NIL;
        }
        if (n == 0) {
          return C0;
        }
        if (n == 1) {
          return C1;
        }

        return QQ(harmonicNumber(n));
      }
      if (arg1.isInfinity()) {
        return arg1;
      }
      if (arg1.isNegativeInfinity()) {
        return F.CComplexInfinity;
      }
      return F.NIL;
    }

    private static IExpr harmonic(IExpr arg1, IExpr arg2, final IAST ast, EvalEngine engine) {
      if (arg2.isOne()) {
        return F.HarmonicNumber(arg1);
      } else {
        // generalized harmonic number
        if (arg2.isInteger()) {
          if (arg1.isInfinity()) {
            if (arg2.isPositive() && ((IInteger) arg2).isEven()) {
              // Module({v=s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!))
              IExpr v = Times(C1D2, arg2);
              return Times(Power(F.C2Pi, Times(C2, v)), Power(CN1, Plus(v, C1)),
                  BernoulliB(Times(C2, v)), Power(Times(C2, Factorial(Times(C2, v))), CN1));
            }
            return F.NIL;
          }
        }
        if (arg2.isInteger() && !arg2.isPositive()) {
          IExpr z = arg1;
          IInteger n = ((IInteger) arg2).negate();
          IInteger m = n.inc();
          return
          // [$ Expand( (1/m)*(BernoulliB(m, z + 1) + ((-1)^n)* BernoulliB(m)) ) $]
          F.Expand(F.Times(F.Power(m, F.CN1), F.Plus(F.BernoulliB(m, F.Plus(z, F.C1)),
              F.Times(F.Power(F.CN1, n), F.BernoulliB(m))))); // $$;
        }

        if (arg1.isInteger()) {
          if (arg1.isNegative() && arg2.isNumber() && arg2.isPositive()) {
            return F.CComplexInfinity;
          }

          int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
          if (n < 0) {
            return F.NIL;
          }
          if (n == 0) {
            return C0;
          }

          int iterationLimit = EvalEngine.get().getIterationLimit();
          if (iterationLimit >= 0 && iterationLimit <= n) {
            IterationLimitExceeded.throwIt(n, ast);
          }
          int intArg2 = arg2.toIntDefault();
          if (intArg2 != Integer.MIN_VALUE) {
            int exponent = intArg2;
            if (intArg2 < 0) {
              exponent *= -1;
            }
            IRational result = F.C0;
            for (int i = 1; i <= n; i++) {
              final IInteger pow = F.ZZ(i).powerRational(exponent);
              result = result.add(intArg2 < 0 ? pow : pow.inverse());
              result.checkBitLength();
            }
            return result;
          }

          final IExpr arg2Negate = arg2.negate();
          return F.sum(i -> Power(i, arg2Negate), 1, n);
          // IASTAppendable result = F.PlusAlloc(n);
          // return result.appendArgs(n + 1, i -> Power(F.ZZ(i), arg2Negate));
        }
        if (engine.isNumericMode() && arg1.isNumber() && arg2.isNumber()) {
          return F.Plus(F.Negate(F.HurwitzZeta(arg2, F.Plus(F.C1, arg1))), F.Zeta(arg2));
        }
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /**
     * The Harmonic number at the index specified
     *
     * @param n the index, non-negative.
     * @return the H_1=1 for n=1, H_2=3/2 for n=2 etc. For values of n less than 1, zero is
     *         returned.
     */
    public static BigFraction harmonicNumber(int n) {
      if (n < 1)
        return BigFraction.ZERO;
      else {
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= n) {
          IterationLimitExceeded.throwIt(n, F.HarmonicNumber(F.ZZ(n)));
        }
        /*
         * start with 1 as the result
         */
        BigFraction a = new BigFraction(1, 1);

        /*
         * add 1/i for i=2..n
         */
        for (int i = 2; i <= n; i++) {
          a = a.add(new BigFraction(1, i));
        }
        return a;
      }
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
   * Im(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the imaginary component of the complex number <code>z</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Im(3+4I)
   * 4
   *
   * &gt;&gt; Im(0.5 + 2.3*I)
   * 2.3
   * </pre>
   */
  private static final class Im extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isDirectedInfinity()) {
        IAST directedInfininty = (IAST) arg1;
        if (directedInfininty.isComplexInfinity()) {
          return S.Indeterminate;
        }
        if (directedInfininty.isAST1()) {
          if (directedInfininty.isInfinity()) {
            return F.C0;
          }
          IExpr im = S.Im.of(engine, directedInfininty.arg1());
          if (im.isNumber()) {
            if (im.isZero()) {
              return F.C0;
            }
            return F.Times(F.Sign(im), F.CInfinity);
          }
        }
      }
      if (arg1.isNumber() || arg1.isQuantity()) {
        return arg1.im();
      }
      if (arg1.isRealResult()) {
        return F.C0;
      }
      if (arg1.isRealVector()) {
        // 0.0 - vector
        return new ASTRealVector(new ArrayRealVector(arg1.size() - 1, 0.0), false);
      }
      if (arg1.isRealMatrix()) {
        ASTRealMatrix matrix = (ASTRealMatrix) arg1;
        return new ASTRealMatrix(
            new Array2DRowRealMatrix(matrix.getRowDimension(), matrix.getColumnDimension()), false);
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Im(negExpr));
      }
      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        int position = timesAST.indexOf(x -> x.isRealResult());
        if (position > 0) {
          return F.Times(timesAST.get(position), F.Im(timesAST.splice(position)));
        }
        IExpr first = timesAST.arg1();
        if (first.isNumber()) {
          IExpr rest = timesAST.rest().oneIdentity1();
          if (first.isReal()) {
            return F.Times(first, F.Im(rest));
          }
          return F.Plus(F.Times(first.re(), F.Im(rest)), F.Times(first.im(), F.Re(rest)));
        }
      }
      if (arg1.isPlus()) {
        return ((IAST) arg1).mapThread((IAST) F.Im(F.Slot1), 1);
      }
      if (arg1.isPower()) {
        IExpr base = arg1.base();
        if (base.isRealResult()) {
          // test for x^(a+I*b)
          IExpr exponent = arg1.exponent();
          if (exponent.isNumber()) {
            // (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
            IExpr a = exponent.re();
            IExpr b = exponent.im();
            return imPowerComplex(base, a, b);
          }
          if (exponent.isNumericFunction(true)) {
            // (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
            IExpr a = engine.evaluate(F.Re(exponent));
            IExpr b = engine.evaluate(F.Im(exponent));
            return imPowerComplex(base, a, b);
          }
        }
      }
      if (arg1.isInterval()) {
        if (arg1.size() == 2) {
          IAST list = (IAST) arg1.first();
          if (list.first().isRealResult() && list.second().isRealResult()) {
            return F.C0;
          }
        }
        return IntervalSym.mapSymbol(S.Im, (IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    /**
     * Evaluate <code>Im(x^(a+I*b))</code>
     *
     * @param x
     * @param a the real part of the exponent
     * @param b the imaginary part of the exponent
     * @return
     */
    private IExpr imPowerComplex(IExpr x, IExpr a, IExpr b) {
      if (x.isE()) {
        // Im(E^(a+I*b)) -> E^a*Sin[b]
        return Times(Power(S.E, a), Sin(b));
      }
      return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Negate(b), Arg(x)))),
          Sin(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
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
   * Increment(x)
   *
   * x++
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * increments <code>x</code> by <code>1</code>, returning the original value of <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 2;
   * &gt;&gt; a++
   * 2
   *
   * &gt;&gt; a
   * 3
   * </pre>
   *
   * <p>
   * Grouping of 'Increment', 'PreIncrement' and 'Plus':<br>
   *
   * <pre>
   * &gt;&gt; ++++a+++++2//Hold//FullForm
   * Hold(Plus(PreIncrement(PreIncrement(Increment(Increment(a)))), 2))
   * </pre>
   */
  private static class Increment extends Decrement {

    @Override
    protected IASTMutable getAST() {
      return (IASTMutable) F.Plus(null, F.C1);
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.Increment;
    }
  }


  /**
   *
   *
   * <pre>
   * LCM(n1, n2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the least common multiple of the given integers.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LCM(15, 20)
   * 60
   * &gt;&gt; LCM(20, 30, 40, 50)
   * 600
   * </pre>
   */
  private static final class LCM extends AbstractArgMultiple {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        if (ast.arg1().isExactNumber()) {
          return ast.arg1().abs();
        }
      }
      return super.evaluate(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    /** Compute lcm of 2 integer numbers */
    @Override
    public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
      return i0.lcm(i1);
    }

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr arg1, final IExpr arg2) {
      if (arg1.isZero()) {
        return arg1;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
    }
  }


  private static final class MantissaExponent extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isReal()) {
          Apfloat x;
          if (arg1 instanceof ApfloatNum) {
            x = ((ApfloatNum) arg1).apfloatValue();
          } else {
            x = ((ISignedNumber) arg1).apfloatValue();
          }
          Apfloat mantissa = ApfloatMath.scale(x, -x.scale());
          long exponent = x.scale();
          return F.list(F.num(mantissa), F.ZZ(exponent));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  /**
   *
   *
   * <pre>
   * Minus(expr)
   *
   *     - expr
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the negation of <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; -a //FullForm
   * "Times(-1, a)"
   * </pre>
   *
   * <p>
   * <code>Minus</code> automatically distributes:
   *
   * <pre>
   * &gt;&gt; -(x - 2/3)
   * 2/3-x
   * </pre>
   *
   * <p>
   * <code>Minus</code> threads over lists:
   *
   * <pre>
   * &gt;&gt; -Range(10)
   * {-1,-2,-3,-4,-5,-6,-7,-8,-9,-10}
   * </pre>
   */
  private static final class Minus extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.Times(F.CN1, ast.arg1());
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
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
   * N(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the numerical value of <code>expr</code>.<br>
   *
   * </blockquote>
   *
   * <pre>
   * N(expr, precision)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> numerically with a precision of <code>prec</code> digits.<br>
   *
   * </blockquote>
   *
   * <p>
   * <strong>Note</strong>: the upper case identifier <code>N</code> is different from the lower
   * case identifier <code>n</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(Pi)
   * 3.141592653589793
   *
   * &gt;&gt; N(Pi, 50)
   * 3.1415926535897932384626433832795028841971693993751
   *
   * &gt;&gt; N(1/7)
   * 0.14285714285714285
   *
   * &gt;&gt; N(1/7, 5)
   * 1.4285714285714285714e-1
   * </pre>
   */
  private static class N extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return numericEval(ast, engine);
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        return numericEvalAST1(arg1, engine);
      }
      IExpr arg2 = ast.arg2();
      final boolean oldNumericMode = engine.isNumericMode();
      final long oldDigitPrecision = engine.getNumericPrecision();
      try {
        long nDigitPrecision = oldDigitPrecision;
        arg2 = engine.evaluateNonNumeric(arg2);
        nDigitPrecision = arg2.toIntDefault();
        if (nDigitPrecision <= 0) {
          // Requested precision `1` is smaller than `2`.
          return IOFunctions.printMessage(S.N, "precsm", F.list(arg2, F.C1), engine);
        }
        if (nDigitPrecision > Config.MAX_PRECISION_APFLOAT) {
          // Requested precision `1` is greater than `2`.
          return IOFunctions.printMessage(S.N, "precgt",
              F.list(arg2, F.ZZ(Config.MAX_PRECISION_APFLOAT)), engine);
        }
        return numericEvalAST2(arg1, nDigitPrecision, engine);
      } finally {
        engine.setNumericMode(oldNumericMode);
        engine.setNumericPrecision(oldDigitPrecision);
      }
    }


    private static IExpr numericEvalAST1(IExpr expr, EvalEngine engine) {
      final boolean oldNumericMode = engine.isNumericMode();
      final long oldDigitPrecision = engine.getNumericPrecision();
      final int oldSignificantFigures = engine.getSignificantFigures();
      try {
        long numericPrecision = oldDigitPrecision; // Config.MACHINE_PRECISION;
        // avoid infinite recursions in symbolic mode
        if (expr.isNumericFunction(true)) {
          engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
          return engine.evalWithoutNumericReset(expr);
        }
        expr = engine.evaluate(expr);
        if (expr.isInexactNumber()) {
          return expr;
        }
        engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
        return engine.evalWithoutNumericReset(expr);
      } finally {
        engine.setNumericMode(oldNumericMode);
        engine.setNumericPrecision(oldDigitPrecision);
      }
    }

    private static IExpr numericEvalAST2(IExpr expr, long nDigitPrecision, EvalEngine engine) {
      // first try symbolic evaluation
      expr = engine.evaluate(expr);
      if (expr.isInexactNumber()) {
        return expr;
      }
      final int maxSize =
          (Config.MAX_OUTPUT_SIZE > Short.MAX_VALUE) ? Short.MAX_VALUE : Config.MAX_OUTPUT_SIZE;
      int significantFigures = (nDigitPrecision > maxSize) ? maxSize : (int) nDigitPrecision;
      if (nDigitPrecision < ParserConfig.MACHINE_PRECISION) {
        nDigitPrecision = ParserConfig.MACHINE_PRECISION;
      }

      // after symbolic evaluation do numeric evaluation with n-digit precision
      engine.setNumericMode(true, nDigitPrecision, significantFigures);
      return engine.evalWithoutNumericReset(expr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * Piecewise({{expr1, cond1}, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents a piecewise function.
   *
   * </blockquote>
   *
   * <pre>
   * Piecewise({{expr1, cond1}, ...}, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents a piecewise function with default <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Piecewise">Wikipedia - Piecewise</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Piecewise({{-x, x&lt;0}, {x, x&gt;=0}})/.{{x-&gt;-3}, {x-&gt;-1/3}, {x-&gt;0}, {x-&gt;1/2}, {x-&gt;5}}
   * {3,1/3,0,1/2,5}
   * </pre>
   *
   * <p>
   * Heaviside function
   *
   * <pre>
   * &gt;&gt; Piecewise({{0, x &lt;= 0}}, 1)
   * Piecewise({{0, x &lt;= 0}}, 1)
   * </pre>
   *
   * <p>
   * Piecewise defaults to <code>0</code>, if no other case is matching.<br>
   *
   * <pre>
   * &gt;&gt; Piecewise({{1, False}})
   * 0
   *
   * &gt;&gt; Piecewise({{0 ^ 0, False}}, -1)
   * -1
   * </pre>
   */
  private static final class Piecewise extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[] dim = arg1.isMatrix(false);
      if (dim == null || dim[0] <= 0 || dim[1] != 2 || !arg1.isAST()) {
        if (arg1.isEmptyList()) {
          if (ast.isAST2()) {
            return ast.arg2();
          }
          return F.C0;
        }
        // The first argument `1` of `2` is not a list of pairs.
        return IOFunctions.printMessage(ast.topHead(), "pairs", F.list(arg1, ast.topHead()),
            engine);
      }
      IAST matrix = (IAST) arg1;
      IExpr defaultValue = F.C0;
      if (ast.isAST1()) {
        return ast.appendClone(F.C0);
      }
      if (ast.isAST2()) {
        defaultValue = ast.arg2();
      }
      IExpr condition;
      int matrixSize = matrix.size();
      IASTAppendable result = F.NIL;
      IASTAppendable piecewiseAST = F.NIL;
      boolean evaluated = false;
      boolean noBoolean = false;
      for (int i = 1; i < matrixSize; i++) {
        final IAST row = matrix.getAST(i);
        condition = row.arg2();
        if (condition.isTrue()) {
          if (!evaluated && i == matrixSize - 1) {
            if (!row.arg1().isSymbol()) {
              return row.arg1();
            }
            return F.NIL;
          }
          if (noBoolean) {
            // result = appendPiecewise(result, row.arg1(), S.True, matrixSize);
            piecewiseAST = createPiecewise(piecewiseAST, result);
            piecewiseAST.append(row.arg1());
            return piecewiseAST;
          }
          return row.arg1();
        } else if (condition.isFalse()) {
          evaluated = true;
          continue;
        }
        condition = engine.evaluateNIL(condition);
        if (condition.isPresent()) {
          evaluated = true;
          if (condition.isTrue()) {
            if (noBoolean) {
              result = appendPiecewise(result, row.arg1(), S.True, matrixSize);
              return createPiecewise(piecewiseAST, result);
            }
            return row.arg1();
          } else if (condition.isFalse()) {
            continue;
          }
        }
        IExpr rowArg1 = engine.evaluateNIL(row.arg1());
        if (rowArg1.isPresent()) {
          evaluated = true;
        } else {
          rowArg1 = row.arg1();
        }
        if (i == matrixSize - 1 && rowArg1.equals(defaultValue)) {
          evaluated = true;
          continue;
        }

        result = appendPiecewise(result, rowArg1, condition.orElse(row.arg2()), matrixSize);
        piecewiseAST = createPiecewise(piecewiseAST, result);
        noBoolean = true;
        continue;
      }
      if (!noBoolean) {
        return defaultValue;
      } else {
        if (evaluated) {
          piecewiseAST = createPiecewise(piecewiseAST, F.List());
          piecewiseAST.append(engine.evaluate(defaultValue));
          return piecewiseAST;
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IASTAppendable createPiecewise(IASTAppendable piecewiseAST, IAST resultList) {
      if (!piecewiseAST.isPresent()) {
        piecewiseAST = F.ast(S.Piecewise);
        piecewiseAST.append(resultList);
      }
      return piecewiseAST;
    }

    private static IASTAppendable appendPiecewise(IASTAppendable list, IExpr function,
        IExpr predicate, int matrixSize) {
      if (!list.isPresent()) {
        list = F.ListAlloc(matrixSize);
      }
      list.append(F.list(function, predicate));
      return list;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set NUMERICFUNCTION
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }


  private static final class PiecewiseExpand extends AbstractFunctionEvaluator {
    static class PiecewiseExpandVisitor extends VisitorExpr {
      // private final EvalEngine engine;
      private final IBuiltInSymbol domain;

      public PiecewiseExpandVisitor(IBuiltInSymbol domain) {
        super();
        // this.engine = engine;
        this.domain = domain;
      }

      @Override
      public IExpr visit(IASTMutable ast) {
        IExpr expr = visitAST(ast).orElse(ast);
        if (expr.isAST()) {
          return piecewiseExpand((IAST) expr, domain).orElseGet(() -> visitAST((IAST) expr));
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        IBuiltInSymbol domain = S.Complexes;
        IAssumptions assumptions = null;
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          if (arg2.equals(S.Reals) || arg2.equals(S.Complexes)) {
            domain = ((IBuiltInSymbol) arg2);
          } else {
            assumptions = Assumptions.getInstance(arg2);
          }
        } else if (ast.isAST3()) {
          IExpr arg2 = ast.arg2();
          IExpr arg3 = ast.arg3();
          if (arg3.equals(S.Reals) || arg3.equals(S.Complexes)) {
            domain = ((IBuiltInSymbol) arg3);
          }
          assumptions = Assumptions.getInstance(arg2);
        }

        PiecewiseExpandVisitor visitor = new PiecewiseExpandVisitor(domain);
        IAssumptions oldAssumptions = engine.getAssumptions();
        try {
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
          }
          return arg1.accept(visitor).orElse(arg1);
        } finally {
          engine.setAssumptions(oldAssumptions);
        }
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }


  /**
   *
   *
   * <pre>
   * Plus(a, b, ...)
   *
   * a + b + ...
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents the sum of the terms <code>a, b, ...</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 1 + 2
   * 3
   * </pre>
   *
   * <p>
   * <code>Plus</code> performs basic simplification of terms:
   *
   * <pre>
   * &gt;&gt; a + b + a
   * 2*a+b
   *
   * &gt;&gt; a + a + 3 * a
   * 5*a
   *
   * &gt;&gt; a + b + 4.5 + a + b + a + 2 + 1.5 * b
   * 6.5+3.0*a+3.5*b
   * </pre>
   *
   * <p>
   * Apply <code>Plus</code> on a list to sum up its elements:
   *
   * <pre>
   * &gt;&gt; Plus @@ {2, 4, 6}
   * 12
   * </pre>
   *
   * <p>
   * The sum of the first <code>1000</code> integers:
   *
   * <pre>
   * &gt;&gt; Plus @@ Range(1000)
   * 500500
   * </pre>
   *
   * <p>
   * <code>Plus</code> has default value <code>0</code>:
   *
   * <pre>
   * &gt;&gt; a /. n_. + x_ :&gt; {n, x}
   * {0,a}
   *
   * &gt;&gt; -2*a - 2*b
   * -2*a-2*b
   *
   * &gt;&gt; -4+2*x+2*Sqrt(3)
   * -4+2*x+2*Sqrt(3)
   *
   * &gt;&gt; 1 - I * Sqrt(3)
   * 1-I*Sqrt(3)
   *
   * &gt;&gt; Head(3 + 2 I)
   * Complex
   *
   * &gt;&gt; N(Pi, 30) + N(E, 30)
   * 5.85987448204883847382293085463
   *
   * &gt;&gt; N(Pi, 30) + N(E, 30) // Precision
   * 30
   * </pre>
   */
  public static class Plus extends AbstractArgMultiple implements IRewrite, INumeric {

    private static HashedOrderlessMatcherPlus PLUS_ORDERLESS_MATCHER;

    public Plus() {}

    @Override
    public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
      return c0.add(c1);
    }

    @Override
    public IExpr e2DblArg(final INum d0, final INum d1) {
      return d0.add(d1);
    }

    @Override
    public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
      return d0.add(d1);
    }

    @Override
    public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
      return f0.add(f1);
    }

    @Override
    public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
      return i0.add(i1);
    }

    @Override
    public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
      return c0.add(F.complex(i1, F.C0));
    }

    private IExpr evalNumericMode(final IAST ast) {
      INum number = F.CD0;
      int start = -1;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg instanceof INum) {
          if (arg instanceof ApfloatNum) {
            number = number.add((INum) arg);
          } else {
            if (number instanceof ApfloatNum) {
              number = number.add(((INum) arg).apfloatNumValue());
            } else {
              number = number.add((INum) arg);
            }
          }
        } else if (arg instanceof IComplexNum) {
          start = i;
          break;
        } else {
          return F.NIL;
        }
      }
      if (start < 0) {
        return number;
      }
      IComplexNum complexNumber;
      if (number instanceof Num) {
        complexNumber = F.complexNum(number.doubleValue());
      } else {
        complexNumber = F.complexNum(number.apfloatValue());
      }
      for (int i = start; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg instanceof INum) {
          number = (INum) arg;
          if (number instanceof Num) {
            complexNumber = complexNumber.add(F.complexNum(((Num) number).doubleValue()));
          } else {
            complexNumber = complexNumber.add(F.complexNum(number.apfloatValue()));
          }
        } else if (arg instanceof IComplexNum) {
          if (complexNumber instanceof ApcomplexNum) {
            complexNumber = complexNumber.add(((IComplexNum) arg).apcomplexNumValue());
          } else {
            complexNumber = complexNumber.add((IComplexNum) arg);
          }
        } else {
          return F.NIL;
        }
      }
      return complexNumber;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      double result = 0;
      for (int i = top - size + 1; i < top + 1; i++) {
        result += stack[i];
      }
      return result;
    }

    /**
     * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">Experiments in
     * Hash-coded Algebraic Simplification</a>
     *
     * @param ast the abstract syntax tree (AST) of the form <code>Plus(...)</code> which should be
     *        evaluated
     * @return the evaluated object or <code>null</code>, if evaluation isn't possible
     */
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      final int size = ast.size();
      if (size > 2) {
        if (ast.head() != S.Plus) {
          return F.NIL;
        }
        if (ast.isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
          IAST temp = engine.evalArgsOrderlessN(ast);
          if (temp.isPresent()) {
            ast = temp;
          }
        }

        PlusOp plusOp = new PlusOp(size);
        IExpr temp = ast.findFirst(x -> plusOp.plus(x));
        if (temp.isPresent()) {
          return temp;
        }
        if (plusOp.isEvaled()) {
          return plusOp.getSum();
        }

        temp = evaluateHashsRepeated(ast, engine);
        if (temp.isAST(S.Plus, 2)) {
          return temp.first();
        }
        return temp;
      } else {
        if (size == 1) {
          if (ast.head() == S.Plus) {
            return F.C0;
          }
          return F.NIL;
        }
        if (size == 2) {
          if (ast.head() == S.Plus) {
            return ast.arg1();
          }
          return F.NIL;
        }
      }
      if (engine.isSymbolicMode(S.Plus.getAttributes())) {
        ast.addEvalFlags(IAST.BUILT_IN_EVALED);
      }
      return F.NIL;
    }

    @Override
    public HashedOrderlessMatcher getHashRuleMap() {
      return PLUS_ORDERLESS_MATCHER;
    }

    /** {@inheritDoc} */
    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      IExpr temp = evalNumericMode(ast);
      if (temp.isPresent()) {
        return temp;
      }
      return evaluate(ast, engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT
          | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      PLUS_ORDERLESS_MATCHER = Arithmetic.Plus.initPlusHashMatcher();
      super.setUp(newSymbol);
    }

    private static HashedOrderlessMatcherPlus initPlusHashMatcher() {
      HashedOrderlessMatcherPlus plusMatcher = new HashedOrderlessMatcherPlus();

      plusMatcher.definePatternHashRule(Power(Sin(x_), C2), Power(Cos(x_), C2), //
          C1);
      plusMatcher.definePatternHashRule(Power(F.Sech(x_), C2), Power(F.Tanh(x_), C2), //
          C1);
      plusMatcher.definePatternHashRule(Power(F.Cosh(x_), C2), Power(F.Sinh(x_), C2), //
          C1, true);
      plusMatcher.definePatternHashRule(Power(F.Csc(x_), C2), Power(F.Cot(x_), C2), //
          C1, true);

      plusMatcher.defineHashRule(ArcSin(x_), ArcCos(x_), //
          F.CPiHalf);
      plusMatcher.defineHashRule(ArcTan(x_), ArcCot(x_), //
          F.CPiHalf);
      // "Positive[x]&&(y==1/x)");
      plusMatcher.defineHashRule(ArcTan(x_), ArcTan(y_), //
          F.CPiHalf, //
          And(Positive(x), Equal(y, Power(x, CN1))));

      // ArcTan(1/2) + ArcTan(1/3) = Pi/4
      plusMatcher.defineHashRule(F.ArcTan(F.C1D3), F.ArcTan(F.C1D2), //
          F.CPiQuarter);
      // ArcTan(1/3) + ArcTan(1/7) = ArcTan(1/2)
      plusMatcher.defineHashRule(F.ArcTan(F.C1D3), F.ArcTan(F.QQ(1L, 7L)), //
          F.ArcTan(F.C1D2));
      // plusMatcher.setUpHashRule("-ArcTan[x_]", "-ArcTan[y_]",
      // "-Pi/2", "Positive[x]&&(y==1/x)");
      // plusMatcher.definePatternHashRule(Times(CN1, ArcTan(x_)), Times(CN1,
      // ArcTan(y_)), Times(CN1D2, Pi),
      // And(Positive(x), Equal(y, Power(x, CN1))));

      return plusMatcher;
    }

    // @Override
    // public IExpr evalAsLeadingTerm(IAST ast, ISymbol x, IExpr logx, int cdir) {
    // //
    // https://github.com/sympy/sympy/blob/ab414d124bb9899eb9d677e95498c7a021c9bc29/sympy/core/add.py#L996
    // IExpr old=ast;
    //// o = self.getO()
    //// if o is None:
    //// o = Order(0)
    //// old = self.removeO()
    ////
    //// if old.has(Piecewise):
    //// old = piecewise_fold(old)
    //
    //// # This expansion is the last part of expand_log. expand_log also calls
    //// # expand_mul with factor=True, which would be more expensive
    //// if (any(isinstance(a, log) for a in self.args)) {
    //// logflags = dict(deep=True, log=True, mul=False, power_exp=False,
    //// power_base=False, multinomial=False, basic=False, force=False,
    //// factor=False)
    //// old = old.expand(**logflags)
    //// }
    //// expr = expand_mul(old);
    // IExpr expr=old;
    // if (! expr.isPlus()) {
    // return expr.asLeadingTerm(x, logx , cdir );
    // }
    // IExpr infinite = [t for t in expr.args if t.is_infinite]
    //
    // _logx = logx.orElseGet( ()-> F.Dummy("logx"));
    // leading_terms = [t.asLeadingTerm(x, logx=_logx, cdir=cdir) for t in expr.args]
    //
    //// min, new_expr = Order(0), 0
    // IExpr new_expr =F.C0;
    // try{
    // for (term in leading_terms) {
    // order = Order(term, x);
    // if (not min or order not in min) {
    // min = order;
    // new_expr = term;
    // } else if (min in order) {
    // new_expr += term;
    // }
    // }
    // }catch(ArgumentTypeError ate) {
    // return expr;
    // }
    // if (!log.isPresent()) {
    // new_expr = new_expr.subs(_logx, log(x));
    // }
    // boolean is_zero = new_expr.isZero();
    // if (!is_zero ) {
    // new_expr = new_expr.trigsimp().cancel();
    // is_zero = new_expr.isZero();
    // }
    // if (is_zero ) {
    //// # simple leading term analysis gave us cancelled terms but we have to send
    //// # back a term, so compute the leading term (via series)
    // try {
    // n0 = min.getn();
    // } catch (NotImplementedError nie) {
    // n0 = 1;
    // }
    // IExpr res = Order(1);
    // int incr = 1;
    // while (res.is_Order) {
    // res = old._eval_nseries(x, n=n0+incr, logx , cdir ).cancel().trigsimp();
    // //powsimp().trigsimp();
    // incr *= 2;
    // }
    // return res.as_leading_term(x, logx=logx, cdir=cdir);
    //
    // }else if(new_expr.isNaN()){
    // return old.func._from_args(infinite) + o;
    // }
    // return new_expr;
    // }

  }


  /**
   *
   *
   * <pre>
   * Pochhammer(a, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the pochhammer symbol for a rational number <code>a</code> and an integer number
   * <code>n</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Pochhammer_symbol">Wikipedia - Pochhammer symbol</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Pochhammer(4, 8)
   * 6652800
   * </pre>
   */
  private static final class Pochhammer extends AbstractArg2 {

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr a, final IExpr n) {
      if (n.isZero()) {
        return F.C1;
      }
      if (n.isOne()) {
        return a;
      }
      int ni = n.toIntDefault();
      if (a.isRational() && ni > Integer.MIN_VALUE) {
        BigFraction bf = ((IRational) a).toBigFraction();
        return pochhammer(bf, ni, ast);
      }
      if (a.isInteger() && a.isPositive()) {
        IExpr temp = EvalEngine.get().evaluate(F.Plus(((IInteger) a).subtract(F.C1), n));
        if (temp.isSymbol()) {
          return F.Divide(F.Factorial(temp), F.Gamma(a));
        }
      }

      if (n.isInteger()) {

        if (ni > Integer.MIN_VALUE) {
          if (ni > Config.MAX_POLYNOMIAL_DEGREE) {
            PolynomialDegreeLimitExceeded.throwIt(ni);
          }
          EvalEngine engine = EvalEngine.get();
          if (ni > 0) {
            // Product(a + k, {k, 0, n - 1})
            IAST product = F.product(k -> F.Plus(a, k), 0, ni - 1);
            return engine.evaluate(product);
          }
          if (ni < 0) {
            // Product(1/(a - k), {k, 1, -n})
            IExpr temp = Power(F.product(k -> F.Plus(a, k.negate()), 1, -ni), -1);
            IExpr result = engine.evaluate(temp);
            return result;
          }
        }
      }
      if (a.isNumber() && n.isNumber()) {
        return F.Divide(F.Gamma(F.Plus(a, n)), F.Gamma(a));
      }
      return F.NIL;
    }

    /**
     * Compute Pochhammer's symbol (that)_n.
     *
     * @param that
     * @param n The number of product terms in the evaluation.
     * @return Gamma(that+n)/Gamma(that) = that*(that+1)*...*(that+n-1).
     */
    private static IExpr pochhammer(BigFraction that, final int n, IAST ast) {
      if (n < 0) {
        int positiveN = -n;
        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= positiveN) {
          IterationLimitExceeded.throwIt(positiveN, ast);
        }
        BigFraction res = BigFraction.ONE;
        for (int i = (-1); i >= n; i--) {
          res = res.multiply(that.add(i));
        }
        if (res.equals(BigFraction.ZERO)) {
          return F.CComplexInfinity;
        }
        return F.fraction(res.reciprocal());
      } else if (n == 0) {
        return F.C1;
      } else {
        if (that.equals(BigFraction.ZERO)) {
          return F.C0;
        }

        int iterationLimit = EvalEngine.get().getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= n) {
          IterationLimitExceeded.throwIt(n, ast);
        }
        BigFraction res = that;
        for (int i = 1; i < n; i++) {
          res = res.multiply(that.add(i));
        }
        return F.fraction(res);
      }
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
   * Power(a, b)
   *
   * a ^ b
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents <code>a</code> raised to the power of <code>b</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 4 ^ (1/2)
   * 2
   *
   * &gt;&gt; 4 ^ (1/3)
   * 4^(1/3)
   *
   * &gt;&gt; 3^123
   * 48519278097689642681155855396759336072749841943521979872827
   *
   * &gt;&gt; (y ^ 2) ^ (1/2)
   * Sqrt(y^2)
   *
   * &gt;&gt; (y ^ 2) ^ 3
   * y^6
   * </pre>
   *
   * <p>
   * Use a decimal point to force numeric evaluation:
   *
   * <pre>
   * &gt;&gt; 4.0 ^ (1/3)
   * 1.5874010519681994
   * </pre>
   *
   * <p>
   * <code>Power</code> has default value <code>1</code> for its second argument:
   *
   * <pre>
   * &gt;&gt; a /. x_ ^ n_. :&gt; {x, n}
   * {a,1}
   * </pre>
   *
   * <p>
   * <code>Power</code> can be used with complex numbers:
   *
   * <pre>
   * &gt;&gt; (1.5 + 1.0*I) ^ 3.5
   * -3.682940057821917+I*6.951392664028508
   *
   * &gt;&gt; (1.5 + 1.0*I) ^ (3.5 + 1.5*I)
   * -3.1918162904562815+I*0.6456585094161581
   * </pre>
   *
   * <p>
   * Infinite expression 0^(negative number)
   *
   * <pre>
   * &gt;&gt; 1/0
   * ComplexInfinity
   *
   * &gt;&gt; 0 ^ -2
   * ComplexInfinity
   *
   * &gt;&gt; 0 ^ (-1/2)
   * ComplexInfinity
   *
   * &gt;&gt; 0 ^ -Pi
   * ComplexInfinity
   * </pre>
   *
   * <p>
   * Indeterminate expression 0 ^ (complex number) encountered.
   *
   * <pre>
   * &gt;&gt; 0 ^ (2*I*E)
   * Indeterminate
   *
   * &gt;&gt; 0 ^ - (Pi + 2*E*I)
   * ComplexInfinity
   * </pre>
   *
   * <p>
   * Indeterminate expression 0 ^ 0 encountered.
   *
   * <pre>
   * &gt;&gt; 0 ^ 0
   *
   * &gt;&gt; Sqrt(-3+2.*I)
   * 0.5502505227003375+I*1.8173540210239707
   *
   * &gt;&gt; Sqrt(-3+2*I)
   * Sqrt(-3+I*2)
   *
   * &gt;&gt; (3/2+1/2I)^2
   * 2+I*3/2
   *
   * &gt;&gt; I ^ I
   * I^I
   *
   * &gt;&gt; 2 ^ 2.0
   * 4.0
   *
   * &gt;&gt; Pi ^ 4.
   * 97.40909103400242
   *
   * &gt;&gt; a ^ b
   * a^b
   * </pre>
   */
  public static class Power extends AbstractFunctionEvaluator
      implements INumeric, IFunctionExpand, PowerRules {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isSqrt() && ast.base().isAST(S.Plus, 3)) {
        IAST plus = (IAST) ast.base();
        final IExpr arg1 = plus.arg1();
        final IExpr arg2 = plus.arg2();
        if (arg1.isRational()) {
          return sqrtDenest((IRational) arg1, arg2);
        }
      }
      return F.NIL;
    }

    /**
     * Denests <code>Sqrt()</code> in an expression that contain other square roots if possible,
     * otherwise returns <code>F.NIL</code>.
     *
     * <pre>
     * Example: sqrt(5 + 2*Sqrt(6))
     *
     *   >> sqrtDenest(5, 2*Sqrt(6))
     *   sqrt(2) + sqrt(3)
     * </pre>
     *
     * <p>
     * See:
     * <a href="// https://en.wikipedia.org/wiki/Nested_radical#Two_nested_square_roots">Wikipedia -
     * Nested radical - Two nested square roots</a> Github #166. References for possible
     * improvements of this method:
     *
     * <pre>
     *
     * .. [1] http://researcher.watson.ibm.com/researcher/files/us-fagin/symb85.pdf
     * .. [2] D. J. Jeffrey and A. D. Rich, 'Symplifying Square Roots of Square Roots
     *        by Denesting' (available at http://www.cybertester.com/data/denest.pdf)
     * </pre>
     *
     * @param arg1
     * @param arg2
     * @return <code>F.NIL</code> if no change could be applied
     */
    public static IExpr sqrtDenest(IRational arg1, IExpr arg2) {
      if (arg1.isNegative()) {
        return sqrtDenest(arg1.negate(), //
            arg2.negate()).mapExpr(x -> F.Times(F.CI, x));
      } else {
        final EvalEngine engine = EvalEngine.get();
        boolean arg2IsNegative = false;
        IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
        if (negExpr.isPresent()) {
          arg2 = negExpr;
          arg2IsNegative = true;
        }
        // (arg2/2) ^ 2
        IExpr squared = engine.evaluate(F.Sqr(F.Divide(arg2, F.C2)));
        if (squared.isRealResult()) {
          IAST list = QuarticSolver.quadraticSolve(F.C1, arg1.negate(), squared);
          if (list.isAST2()) {
            IExpr a = engine.evaluate(list.arg1());
            if (a.isRational()) {
              IExpr b = engine.evaluate(list.arg2());
              if (b.isRational()) {
                if (arg2IsNegative) {
                  return F.Plus(F.Sqrt(a), F.Negate(F.Sqrt(b)));
                }
                return F.Plus(F.Sqrt(a), F.Sqrt(b));
              }
            }
          }
        }
      }
      return F.NIL;
    }

    public static IExpr binaryOperator(IAST ast, final IExpr base, final IExpr exponent,
        EvalEngine engine) {
      try {
        if (base.isInexactNumber() && exponent.isInexactNumber()) {
          IExpr result = e2NumericArg(ast, base, exponent);
          if (result.isPresent()) {
            return result;
          }
        }

        if (exponent.isDirectedInfinity()) {
          IExpr temp = evalDirectedInfinityArg2(base, (IAST) exponent, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        if (base.isDirectedInfinity()) {
          IExpr temp = evalDirectedInfinityArg1((IAST) base, exponent);
          if (temp.isPresent()) {
            return temp;
          }
        }

        if (base.isZero()) {
          if (exponent.isInterval()) {
            return org.matheclipse.core.expression.IntervalSym.power(base, (IAST) exponent);
          }
          return powerZeroArg1(exponent);
        }
        if (base.isQuantity()) {
          try {
            IQuantity q = (IQuantity) base;
            return q.power(exponent);
          } catch (MathException mex) {
            return F.NIL;
          }
        } else if (base.isAST()) {
          if (base.isInterval()) {
            if (exponent.isInteger()) {
              return IntervalSym.power((IAST) base, (IInteger) exponent);
              // return powerInterval(base, ii);
            }
            if (exponent.isReal()) {
              return IntervalSym.power((IAST) base, (ISignedNumber) exponent);
              // return powerInterval(base, ii);
            }
            // } else if (base.isQuantity()) {
            // try {
            // IQuantity q = (IQuantity) base;
            // return q.power(exponent);
            // } catch (MathException mex) {
            // return F.NIL;
            // }
          } else if (base instanceof ASTSeriesData) {
            int exp = exponent.toIntDefault();
            if (exp != Integer.MIN_VALUE) {
              return ((ASTSeriesData) base).powerSeries(exp);
            }
            return F.NIL;
          }
        }

        if (exponent.isInterval()) {
          if (base.isRealResult()) {
            return org.matheclipse.core.expression.IntervalSym.power(base, (IAST) exponent);
          }
        }

        if (exponent.isReal()) {
          if (exponent.isZero()) {
            return (base.isInfinity() || base.isNegativeInfinity()) ? S.Indeterminate : F.C1;
          }
          if (exponent.isOne()) {
            return base;
          }
          if (exponent.isInteger()) {
            if (base.isInteger()) {
              return integerInteger((IInteger) base, (IInteger) exponent);
            }
            if (base instanceof IFraction) {
              return fractionInteger((IFraction) base, (IInteger) exponent);
            }
            if (base instanceof IComplex) {
              return complexInteger((IComplex) base, (IInteger) exponent);
            }
            if (base.isAtom()) {
              return F.NIL;
            }

            if (exponent.isNegative() && base.isAST1()) {
              if (base.isFunctionID(new int[] {ID.Cos, ID.Cosh, ID.Cot, ID.Coth, ID.Csc, ID.Csch,
                  ID.Sec, ID.Sech, ID.Sin, ID.Sinh, ID.Tan, ID.Tanh})) {
                IExpr x = ((IAST) base).arg1();
                IExpr mNeg = exponent.negate();
                boolean disabledTrigRules = engine.isDisabledTrigRules();
                int id = ((IAST) base).headID();
                switch (id) {
                  case ID.Tan:
                    // Tan(x_)^m_?(IntegerQ(#)&&#<0 &):=Cot(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Cot(x), mNeg);
                  case ID.Cot:
                    // Cot(x_)^m_?(IntegerQ(#)&&#<0 &):=Tan(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Tan(x), mNeg);
                  case ID.Sec:
                    // Sec(x_)^m_?(IntegerQ(#)&&#<0 &):=Cos(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Cos(x), mNeg);
                  case ID.Cos:
                    // Cos(x_)^m_?(IntegerQ(#)&&#<0 &):=Sec(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Sec(x), mNeg);
                  case ID.Csc:
                    // Csc(x_)^m_?(IntegerQ(#)&&#<0 &):=Sin(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Sin(x), mNeg);
                  case ID.Sin:
                    // Sin(x_)^m_?(IntegerQ(#)&&#<0 &):=Csc(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Csc(x), mNeg);
                  case ID.Tanh:
                    // Tanh(x_)^m_?(IntegerQ(#)&&#<0 &):=Coth(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Coth(x), mNeg);
                  case ID.Coth:
                    // Coth(x_)^m_?(IntegerQ(#)&&#<0 &):=Tanh(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Tanh(x), mNeg);
                  case ID.Sech:
                    // Sech(x_)^m_?(IntegerQ(#)&&#<0 &):=Cosh(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Cosh(x), mNeg);
                  case ID.Cosh:
                    // Cosh(x_)^m_?(IntegerQ(#)&&#<0 &):=Sech(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Sech(x), mNeg);
                  case ID.Csch:
                    // Csch(x_)^m_?(IntegerQ(#)&&#<0 &):=Sinh(x)^(-m),
                    return disabledTrigRules ? F.NIL : F.Power(F.Sinh(x), mNeg);
                  case ID.Sinh:
                    // Sinh(x_)^m_?(IntegerQ(#)&&#<0 &):=Csch(x)^(-m)
                    return disabledTrigRules ? F.NIL : F.Power(F.Csch(x), mNeg);
                }
              }
            }
          }
        }

        if (base.isOne()) {
          return F.C1;
        }
        if (base.isMinusOne()) {
          // if (exponent.isInteger()) {
          // return (((IInteger) exponent).isEven()) ? F.C1 : F.CN1;
          // }
          if (exponent.isEvenResult()) {
            return F.C1;
          }
          if (exponent.isIntegerResult()) {
            if (exponent.isPlus() && exponent.first().isInteger()) {
              IInteger arg1Plus = (IInteger) exponent.first();
              if (!arg1Plus.isOne()) {
                IInteger factor = (((IInteger) exponent.first()).isEven()) ? F.C1 : F.CN1;
                if (factor.isMinusOne()) {
                  return F.Power(F.CN1, F.Plus(1, exponent.rest().oneIdentity1()));
                }
                return F.Times(factor, F.Power(F.CN1, exponent.rest().oneIdentity1()));
              }
            } else if (exponent.isTimes() && exponent.first().isInteger()) {
              IInteger arg1Times = (IInteger) exponent.first();
              return F.Power(F.Power(F.CN1, arg1Times), exponent.rest().oneIdentity1());
            }
          }
        }

        IExpr result = e2ObjArg(ast, base, exponent);
        if (result.isPresent()) {
          return result;
        }

        if (base instanceof IInteger) {
          if (exponent instanceof IFraction) {
            return fractionFraction(F.fraction((IInteger) base, F.C1), (IFraction) exponent);
          }
          if (exponent instanceof IComplex) {
            return complexComplex(F.complex((IInteger) base, F.C0), (IComplex) exponent);
          }
          return F.NIL;
        }

        if (base instanceof IFraction) {
          if (exponent instanceof IFraction) {
            return fractionFraction((IFraction) base, (IFraction) exponent);
          }
          if (exponent instanceof IComplex) {
            return complexComplex(F.complex((IFraction) base), (IComplex) exponent);
          }
          return F.NIL;
        }

        if (base instanceof IComplex) {
          if (exponent instanceof IFraction) {
            return complexFraction((IComplex) base, (IFraction) exponent);
          }
          if (exponent instanceof IComplex) {
            return complexComplex((IComplex) base, (IComplex) exponent);
          }
        }
      } catch (BackingStorageException | LossOfPrecisionException lpe) {
        // Complete loss of accurate digits (apfloat).
        return IOFunctions.printMessage(S.General, "zzapfloatcld", F.List(), EvalEngine.get());
      } catch (OverflowException | InfiniteExpansionException | ArithmeticException aex) {
        // Overflow occurred in computation.
        return IOFunctions.printMessage(S.General, "ovfl", F.List(), EvalEngine.get());
      }

      if (engine.isSymbolicMode(S.Power.getAttributes())) {
        ast.addEvalFlags(IAST.BUILT_IN_EVALED);
      }
      return F.NIL;
    }

    public static IExpr powerSurd(final IExpr surdAST, final IExpr exponent) {
      IExpr surdArg1 = surdAST.first();
      IExpr surdArg2 = surdAST.second();
      if (surdArg2.isInteger() && exponent.isInteger()) {
        IInteger surdExponent = (IInteger) surdArg2;
        IInteger powExponent = (IInteger) exponent;
        boolean negativeExponent = false;
        if (surdExponent.isPositive() && surdExponent.isOdd()) {
          if (powExponent.isNegative()) {
            powExponent = powExponent.negate();
            negativeExponent = true;
          }
          if (powExponent.isGT(surdExponent)) {
            IInteger iquo = powExponent.iquo(surdExponent);
            IInteger irem = powExponent.irem(surdExponent);
            if (negativeExponent) {
              return F.Times(F.Power(surdArg1, iquo.negate()), F.Power(surdAST, irem.negate()));
            }
            return F.Times(F.Power(surdArg1, iquo), F.Power(surdAST, irem));
          }
        }
      }
      return F.NIL;
    }

    private static IExpr e2NumericArg(IAST ast, final IExpr o0, final IExpr o1) {
      // try {
      IExpr result = F.NIL;
      if (o0 instanceof ApcomplexNum) {
        if (o1.isNumber()) {
          result = e2ApcomplexArg((ApcomplexNum) o0, ((INumber) o1).apcomplexNumValue());
        }
      } else if (o1 instanceof ApcomplexNum) {
        if (o0.isNumber()) {
          result = e2ApcomplexArg(((INumber) o0).apcomplexNumValue(), (ApcomplexNum) o1);
        }
      } else if (o0 instanceof ComplexNum) {
        if (o1.isNumber()) {
          result = e2DblComArg((ComplexNum) o0, ((INumber) o1).complexNumValue());
        }
      } else if (o1 instanceof ComplexNum) {
        if (o0.isNumber()) {
          result = e2DblComArg(((INumber) o0).complexNumValue(), (ComplexNum) o1);
        }
      }

      if (o0 instanceof ApfloatNum) {
        if (o1.isReal()) {
          result = e2ApfloatArg((ApfloatNum) o0, ((ISignedNumber) o1).apfloatNumValue());
        }
      } else if (o1 instanceof ApfloatNum) {
        if (o0.isReal()) {
          result = e2ApfloatArg(((ISignedNumber) o0).apfloatNumValue(), (ApfloatNum) o1);
        }
      } else if (o0 instanceof Num) {
        if (o1.isReal()) {
          result = e2DblArg((Num) o0, ((ISignedNumber) o1).numValue());
        }
      } else if (o1 instanceof Num) {
        if (o0.isReal()) {
          result = e2DblArg(((ISignedNumber) o0).numValue(), (Num) o1);
        }
      }
      if (result.isPresent()) {
        return result;
      }
      return e2ObjArg(ast, o0, o1);
      // } catch (RuntimeException rex) {
      // // EvalEngine.get().printMessage(ast.topHead().toString() + ": " + rex.getMessage());
      // }
      //
      // return F.NIL;
    }

    /**
     * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest
     * factor&quot;
     *
     * @return <code>{nth-root, rest factor}</code> or <code>null</code> if the root is not
     *         available
     */
    // private static IInteger[] calculateRoot(IInteger a, IInteger root) {
    // if (a.isOne() || a.isMinusOne()) {
    // return null;
    // }
    // int n = root.toIntDefault();
    // if (n > 0) {
    // IInteger[] result = a.nthRootSplit(n);
    // if (result[1].equals(a)) {
    // // no roots found
    // return null;
    // }
    // return result;
    // }
    // return null;
    // }

    private static IExpr e2ApcomplexArg(final ApcomplexNum base, final ApcomplexNum exponent) {
      return base.pow(exponent);
    }

    private static IExpr e2ApfloatArg(final ApfloatNum base, final ApfloatNum exponent) {
      if (base.isZero()) {
        if (exponent.isNegative()) {
          // Infinite expression `1` encountered.
          IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, exponent)),
              EvalEngine.get());
          return F.CComplexInfinity;
        }
        if (exponent.isZero()) {
          // 0^0
          // Indeterminate expression `1` encountered.
          IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
          return S.Indeterminate;
        }
      }
      if (exponent.isMinusOne()) {
        return base.inverse();
      }
      if (exponent.isNumIntValue()) {
        return base.pow(exponent);
      }
      if (base.isNegative()) {
        ApcomplexNum b = base.apcomplexNumValue();
        ApcomplexNum e = exponent.apcomplexNumValue();
        return b.pow(e);
      }

      return base.pow(exponent);
    }

    private static IExpr complexComplex(final IComplex base, final IComplex exponent) {
      if (base.getImaginaryPart().isZero()) {
        IRational a = base.getRealPart();
        IRational b = exponent.getRealPart();
        IRational c = exponent.getImaginaryPart();
        IExpr temp = // [$ b*Arg(a)+1/2*c*Log(a^2) $]
            F.Plus(F.Times(b, F.Arg(a)), F.Times(F.C1D2, c, F.Log(F.Sqr(a)))); // $$;
        return // [$ (a^2)^(b/2)*E^(-c*Arg(a)) * (Cos(temp)+I* Sin(temp)) $]
        F.Times(F.Power(F.Sqr(a), F.Times(F.C1D2, b)), F.Exp(F.Times(F.CN1, c, F.Arg(a))),
            F.Plus(F.Cos(temp), F.Times(F.CI, F.Sin(temp)))); // $$;
      }
      return F.NIL;
    }

    private static IExpr e2DblArg(final INum base, final INum exponent) {
      if (base.isZero()) {
        if (exponent.isNegative()) {
          IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, exponent)),
              EvalEngine.get());
          // EvalEngine.get().printMessage("Infinite expression 0^(negative number)");
          return F.CComplexInfinity;
        }
        if (exponent.isZero()) {
          // 0^0
          IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
          // EvalEngine.get().printMessage("Infinite expression 0^0");
          return S.Indeterminate;
        }
      }
      if (exponent.isMinusOne()) {
        return base.inverse();
      }
      if (exponent.isNumIntValue()) {
        return base.pow(exponent);
      }
      if (base.isNegative()) {
        return F.complexNum(base.doubleValue()).pow(F.complexNum(exponent.doubleValue()));
      }
      return base.pow(exponent);
    }

    private static IExpr e2DblComArg(final IComplexNum base, final IComplexNum exponent) {
      return base.pow(exponent);
    }

    private static IExpr fractionInteger(IFraction base, IInteger exponent) {
      if (base.numerator().isZero()) {
        return F.C0;
      }

      if (exponent.isZero()) {
        return F.C1;
      }
      // exponent is integer
      IInteger exp = exponent.numerator();
      final int expInt = exp.toIntDefault();
      if (expInt != Integer.MIN_VALUE) {
        return base.powerRational(expInt);
      }
      if (exp.isNegative()) {
        IInteger negExponent = exp.negate();
        return F.Rational(base.denominator().power(negExponent),
            base.numerator().power(negExponent));
      }
      return F.Rational(base.numerator().power(exp), base.denominator().power(exp));
    }

    private static IExpr fractionFraction(IFraction base, IFraction exponent) {
      IInteger baseNumerator = base.numerator();
      if (baseNumerator.isZero()) {
        return F.C0;
      }

      if (exponent.numerator().isZero()) {
        return F.C1;
      }

      IInteger baseDenominator = base.denominator();
      if (baseNumerator.isOne() && !baseDenominator.isOne()) {
        return F.Power(baseDenominator, exponent.negate());
      }

      // if (exponent.equals(F.C1D2)) {
      // if (base.isNegative()) {
      // done in e2ObjArg
      // return F.Times(F.CI, F.Power(base.negate(), exponent));
      // }
      // }

      if (exponent.equals(F.CN1D2)) {
        if (base.isNegative()) {
          return F.Times(F.CNI, F.Power(base.negate().inverse(), exponent.negate()));
        }
      }

      IInteger exponentDenominator = exponent.denominator();
      if (exponent.isNegative() && !baseDenominator.isOne()) {
        return F.Power(base.inverse(), exponent.negate());
      }
      if (exponentDenominator.isOne()) {
        return fractionInteger(base, exponent.numerator());
      }

      IExpr temp = rationalPower(baseNumerator, baseDenominator, exponent);
      if (temp.isPresent()) {
        return temp;
      }

      return F.NIL;
    }

    private static IExpr integerInteger(final IInteger base, final IInteger exponent)
        throws ArithmeticException {
      if (base.isMinusOne()) {
        return (exponent.isEven()) ? F.C1 : F.CN1;
      }
      if (base.isZero()) {
        // all other cases see e2ObjArg
        return F.NIL;
      }
      // may throw ArithmeticException
      long n = exponent.toLong();
      return base.power(n);
    }

    /**
     * @param arg1 a number
     * @param arg2 must be a <code>DirectedInfinity[...]</code> expression
     * @return
     */
    private static IExpr e2NumberDirectedInfinity(final INumber arg1, final IAST arg2) {
      int comp = arg1.compareAbsValueToOne();
      switch (comp) {
        case 1:
          // Abs(arg1) > 1
          if (arg2.isInfinity()) {
            // arg1 ^ Inf
            if (arg1.isReal() && arg1.isPositive()) {
              return F.CInfinity;
            }
            // complex or negative numbers
            return F.CComplexInfinity;
          }
          if (arg2.isNegativeInfinity()) {
            // arg1 ^ (-Inf)
            return F.C0;
          }
          break;
        case -1:
          // Abs(arg1) < 1
          if (arg2.isInfinity()) {
            // arg1 ^ Inf
            return F.C0;
          }
          if (arg2.isNegativeInfinity()) {
            // arg1 ^ (-Inf)
            if (arg1.isReal() && arg1.isPositive()) {
              return F.CInfinity;
            }
            // complex or negative numbers
            return F.CComplexInfinity;
          }
          break;
      }
      return F.NIL;
    }

    private static IExpr e2ObjArg(IAST ast, final IExpr base, final IExpr exponent) {
      if (base.isAST(S.Surd, 3)) {
        return powerSurd(base, exponent);
      }
      if (base.isReal() || exponent.isReal()) {
        if (exponent.isReal()) {
          ISignedNumber realExponent = (ISignedNumber) exponent;
          if (base.isPower()) {
            if (powerPowerRealExponent((IAST) base, realExponent)) {
              return Power(base.base(), base.exponent().times(realExponent));
              // return temp;
            }
          }

          if (base.isInfinity()) {
            if (realExponent.isNegative()) {
              return F.C0;
            } else {
              return F.CInfinity;
            }
          } else if (base.isNegativeInfinity()) {
            if (realExponent.isNegative()) {
              return F.C0;
            }
            if (realExponent.isInteger()) {
              IInteger ii = (IInteger) realExponent;
              if (ii.isOdd()) {
                return F.CNInfinity;
              } else {
                return F.CInfinity;
              }
            } else {
              int exp = realExponent.toIntDefault();
              if (exp != Integer.MIN_VALUE) {
                if ((exp & 0x1) == 0x1) {
                  return F.CNInfinity;
                } else {
                  return F.CInfinity;
                }
              }
            }
          }
          if (exponent.isMinusOne() || exponent.isInteger()) {
            if (base.isNumber()) {
              if (exponent.isMinusOne()) {
                return ((INumber) base).inverse();
              }
              try {
                long n = ((IInteger) exponent).toLong();
                return ((INumber) base).power(n);
              } catch (ArithmeticException ae) {

              }
            } else {
              IExpr o1negExpr = F.NIL;
              if (exponent.isInteger() && ((IInteger) exponent).isEven()) {
                o1negExpr = AbstractFunctionEvaluator.getPowerNegativeExpression(base, true);
              } else {
                o1negExpr = AbstractFunctionEvaluator.getPowerNegativeExpression(base, false);
              }
              if (o1negExpr.isPresent()) {
                if (exponent.isMinusOne()) {
                  return Times(CN1, Power(o1negExpr, CN1));
                } else {
                  IInteger ii = (IInteger) exponent;
                  if (ii.isEven()) {
                    return Power(o1negExpr, exponent);
                  }
                }
              }
              if (exponent.isMinusOne() && base.isTimes()) {
                IExpr temp = powerTimesInverse((IAST) base, (ISignedNumber) exponent);
                if (temp.isPresent()) {
                  return temp;
                }
              }
            }
          }
        } else {
          if (base.isFraction() && base.isPositive() && ((IFraction) base).isLT(F.C1)) {
            IExpr o1negExpr = AbstractFunctionEvaluator.getPowerNegativeExpression(exponent, true);
            if (o1negExpr.isPresent()) {
              return F.Power(base.inverse(), o1negExpr);
            }
          }
        }

        if (base.isReal() && base.isNegative() && exponent.isNumEqualRational(F.C1D2)) {
          // extract I for sqrt
          return F.Times(F.CI, F.Power(F.Negate(base), exponent));
        }
      }
      if (base.isDirectedInfinity() && !exponent.isReal()) {
        // check negative/positive assumptions about exponent
        if (base.isInfinity()) {
          if (exponent.isNegativeResult()) {
            return F.C0;
          }
          if (exponent.isPositiveResult()) {
            return F.CInfinity;
          }
        }
        if (base.isNegativeInfinity() && exponent.isNegativeResult()) {
          return F.C0;
        }
      }

      if (base.isE()) {
        // E^exponent_
        if (exponent.isLog()) {
          // E^Log(x_) := x
          return exponent.first();
        }
        if (exponent.isPlusTimesPower()) {
          IExpr expandedFunction = F.evalExpand(exponent);
          if (expandedFunction.isPlus()) {
            return powerEPlus((IAST) expandedFunction);
          }
          if (expandedFunction.isTimes()) {
            IAST times = (IAST) expandedFunction;

            int index = times.indexOf(x -> x.isLog());
            if (index > 0) {
              // E^(rest_*Log(x_)):=x^rest /; FreeQ(rest,x)
              IAST log = (IAST) times.get(index);
              IExpr logArg1 = log.first();
              IAST rest = times.removeAtCopy(index);
              if (rest.isFree(logArg1)) {
                return F.Power(logArg1, rest);
              }
            }
            IExpr i = Times.of(times, F.CNI, F.Power(S.Pi, F.CN1));
            if (i.isRational()) {
              IRational rat = (IRational) i;
              if (rat.isGT(F.C1) || rat.isLE(F.CN1)) {
                IInteger t = rat.trunc();
                t = t.add(t.irem(F.C2));
                // exp(I*(i - t)*Pi)
                return S.Exp.of(F.Times(F.CI, S.Pi, F.Subtract(i, t)));
              } else {
                IRational t1 = rat.multiply(F.C6).normalize();
                IRational t2 = rat.multiply(F.C4).normalize();
                if (t1.isInteger() || t2.isInteger()) {
                  // Cos(- I*times) + I*Sin(- I*times)
                  return S.Plus.of(F.Cos(F.Times(F.CNI, times)),
                      F.Times(F.CI, F.Sin(F.Times(F.CNI, times))));
                }
              }
            }
          }
        }
      } else {
        if (exponent.isPower()) {
          IAST logBase = F.Log(base);
          IAST pow = (IAST) ast.exponent();
          if (pow.base().equals(logBase) && pow.exponent().isInteger()) {
            // x ^ (Log(x) ^ intExponent) ==> E ^ (Log(x) ^ (intExponent+1))
            IInteger intExponent = (IInteger) pow.exponent();
            if (!intExponent.isZero()) {
              return F.Power(S.E, F.Power(logBase, intExponent.inc()));
            }
          }
        } else if (exponent.isTimes()) {
          IAST logBase = F.Log(base);
          IAST times = (IAST) ast.exponent();
          int index = times
              .indexOf(x -> x.isPower() && x.first().equals(logBase) && x.exponent().isInteger());
          if (index > 0) {
            // x ^ (rest_ * Log(x) ^ intExponent) ==> E ^ (rest * Log(x) ^ (intExponent+1))
            IAST pow = (IAST) times.get(index);
            IInteger intExponent = (IInteger) pow.exponent();
            if (!intExponent.isZero()) {
              IASTMutable copy = times.setAtCopy(index, F.Power(logBase, intExponent.inc()));
              return F.Power(S.E, copy);
            }
          }
        }
      }

      if (base.isAST()) {
        IAST powBase = (IAST) base;
        if (powBase.isTimes()) {
          if (exponent.isInteger() || exponent.isMinusOne()) {
            // (a * b * c)^n => a^n * b^n * c^n
            return powBase.mapThread(Power(F.Slot1, exponent), 1);
          }
          if (base.first().isMinusOne() && exponent.isReal() && base.isRealResult()) {
            // ((-1) * rest) ^ (exponent) ;rest is real result
            return F.Times(F.Power(base.first(), exponent), F.Power(base.rest(), exponent));
          }
          if ((base.size() > 2)) {
            IASTAppendable filterAST = powBase.copyHead();
            IASTAppendable restAST = powBase.copyHead();
            IASTAppendable simplifiedTimesArgs = powBase.copyHead();
            powBase.forEach(x -> {
              if (x.isRealResult()) {
                if (x.isMinusOne()) {
                  restAST.append(x);
                } else {
                  if (x.isNegativeResult()) {
                    filterAST.append(x.negate());
                    restAST.append(F.CN1);
                  } else {
                    if (exponent.isReal() && x.isPower() && x.base().isNumber()) {
                      if (powerPowerRealExponent((IAST) x, (ISignedNumber) exponent)) {
                        simplifiedTimesArgs.append(F.Power(x.base(), x.exponent().times(exponent)));
                        return;
                      }
                    }
                    filterAST.append(x);
                  }
                }
              } else {
                restAST.append(x);
              }
            });
            IExpr temp = restAST.oneIdentity1(); // powBase is Times()
            if (simplifiedTimesArgs.size() > 1 || (filterAST.size() > 1 && !temp.isNumber())) {
              if (filterAST.size() > 1) {
                simplifiedTimesArgs.append(Power(filterAST, exponent));
              }
              if (restAST.size() > 1) {
                simplifiedTimesArgs.append(Power(temp, exponent));
              }
              return simplifiedTimesArgs;
            }
          }
        } else if (base.isPower()) {
          if (base.exponent().isReal() && exponent.isReal()) {
            IExpr temp = base.exponent().times(exponent);
            if (temp.isOne()) {
              // (a ^ b )^exponent => a ^ (b * exponent) && b*exponent==1
              if (base.base().isNonNegativeResult()) {
                return base.base();
              }
              if (base.base().isRealResult() && //
                  base.exponent().isEvenResult()) {
                return F.Abs(base.base());
              }
            }
          }
          if (exponent.isInteger()) {
            // (a ^ b )^n => a ^ (b * n)
            if (base.exponent().isNumber()) {
              return F.Power(base.base(), exponent.times(base.exponent()));
            }
            return F.Power(base.base(), F.Times(exponent, base.exponent()));
          }
        }
      }
      if (exponent.isFraction() && base.isRational()) {

        if (((IFraction) exponent).isGT(F.C1)) {
          // exponent > 1
          IInteger expNumerator = ((IFraction) exponent).numerator();
          IInteger expDenominator = ((IFraction) exponent).denominator();
          IInteger expDiv = expNumerator.div(expDenominator);
          IInteger expMod = expNumerator.mod(expDenominator);
          return F.Times(base.power(expDiv), base.power(F.QQ(expMod, expDenominator)));
        } else if (((IFraction) exponent).isLT(F.CN1)) {
          // exponent < -1
          IInteger expNumerator = ((IFraction) exponent).numerator().negate();
          IInteger expDenominator = ((IFraction) exponent).denominator();
          IInteger expDiv = expNumerator.div(expDenominator);
          IInteger expMod = expNumerator.mod(expDenominator);
          return F.Times(F.Power(base.power(expDiv), F.CN1),
              F.Power(base.power(F.QQ(expMod, expDenominator)), F.CN1));
        } else if (base.isNegative() && ((IFraction) exponent).isNegative()) {
          return F.Times(F.CN1, F.Power(F.CN1, F.C1.add(exponent)),
              F.Power(base.negate(), exponent));
        }
        if (base.isRational() && !ast.isAllExpanded()) {
          // try factorizing base
          IRational num = ((IRational) base);
          IInteger expNumerator = ((IFraction) exponent).numerator();
          IInteger expDenominator = ((IFraction) exponent).denominator();
          int denominator = expDenominator.toIntDefault();
          if (denominator > 1) {
            int numerator = 1;
            if (!expNumerator.isOne()) {
              numerator = expNumerator.toIntDefault();
            }
            if (numerator > 0) {
              IExpr temp = num.factorSmallPrimes(numerator, denominator);
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
          if (ast.isPresent()) {
            ast.addEvalFlags(IAST.IS_ALL_EXPANDED);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Test if <code>
     * (powerAST.base() ^ powerAST.exponent()) ^ exponent ==> powerAST.base() ^ (powerAST.exponent() * exponent)
     * </code> should be performed.
     *
     * @param powerAST
     * @param exponent
     * @return <code>true</code> if the operation can be performed; <code>false</code> otherwise.
     */
    private static boolean powerPowerRealExponent(final IAST powerAST, ISignedNumber exponent) {
      final IExpr baseArg1 = powerAST.base();
      final IExpr exponentArg1 = powerAST.exponent();
      if (exponentArg1.isReal() && baseArg1.isNonNegativeResult()) {
        return true;
      }
      if (exponent.isRational() && exponentArg1.isFraction() && exponentArg1.isRational()) {
        return true;
      }
      if (exponent.isNumIntValue() && exponent.isPositive()) {
        if (exponentArg1.isNumIntValue() && exponentArg1.isPositive()) {
          return true;
        }
      }
      return false;
    }

    /**
     * <code>DirectedInfinity(...) ^ exponent</code>
     *
     * @param directedInfinity
     * @param exponent
     * @return {@link F#NIL} if evaluation is not possible
     */
    private static IExpr evalDirectedInfinityArg1(final IAST directedInfinity,
        final IExpr exponent) {
      if (exponent.isZero()) {
        return S.Indeterminate;
      }
      if (directedInfinity.isComplexInfinity()) {
        if (exponent.isReal()) {
          if (exponent.isNegative()) {
            return F.C0;
          }
          return F.CComplexInfinity;
        }
        return S.Indeterminate;
      }
      if (exponent.isOne()) {
        return directedInfinity;
      }
      if (exponent.isMinusOne()) {
        return F.C0;
      }
      return F.NIL;
    }

    /**
     * <code>base ^ DirectedInfinity(...)</code>
     *
     * @param base
     * @param directedInfinity
     * @return {@link F#NIL} if evaluation is not possible
     */
    private static IExpr evalDirectedInfinityArg2(final IExpr base, final IAST directedInfinity,
        EvalEngine engine) {
      if (directedInfinity.isComplexInfinity()) {
        return S.Indeterminate;
      }

      if (base.isOne() || base.isMinusOne() || base.isImaginaryUnit()
          || base.isNegativeImaginaryUnit()) {
        return S.Indeterminate;
      }
      if (base.isZero()) {
        if (directedInfinity.isInfinity()) {
          // 0 ^ Inf
          return F.C0;
        }
        if (directedInfinity.isNegativeInfinity()) {
          // 0 ^ (-Inf)
          return F.CComplexInfinity;
        }
        return S.Indeterminate;
      }
      if (base.isInfinity()) {
        if (directedInfinity.isInfinity()) {
          // Inf ^ Inf
          return F.CComplexInfinity;
        }
        if (directedInfinity.isNegativeInfinity()) {
          // Inf ^ (-Inf)
          return F.C0;
        }
        return S.Indeterminate;
      }
      if (base.isNegativeInfinity()) {
        if (directedInfinity.isInfinity()) {
          // (-Inf) ^ Inf
          return F.CComplexInfinity;
        }
        if (directedInfinity.isNegativeInfinity()) {
          // (-Inf) ^ (-Inf)
          return F.C0;
        }
        return S.Indeterminate;
      }
      if (base.isComplexInfinity()) {
        if (directedInfinity.isInfinity()) {
          // ComplexInfinity ^ Inf
          return F.CComplexInfinity;
        }
        if (directedInfinity.isNegativeInfinity()) {
          // ComplexInfinity ^ (-Inf)
          return F.C0;
        }
        return S.Indeterminate;
      }
      if (base.isDirectedInfinity()) {
        if (directedInfinity.isInfinity()) {
          return F.CComplexInfinity;
        }
        if (directedInfinity.isNegativeInfinity()) {
          return F.C0;
        }
        return S.Indeterminate;
      }

      if (base.isNumber()) {
        IExpr temp = e2NumberDirectedInfinity((INumber) base, directedInfinity);
        if (temp.isPresent()) {
          return temp;
        }
      } else {
        IExpr a1 = engine.evalN(base);
        if (a1.isNumber()) {
          IExpr temp = e2NumberDirectedInfinity((INumber) a1, directedInfinity);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      return F.NIL;
    }

    /**
     * Transform <code>Power(Times(a,b,c,Power(d,-1.0)....), -1.0)</code> to <code>
     * Times(a^(-1.0),b^(-1.0),c^(-1.0),d,....)</code>
     *
     * @param timesAST a <code>Times(...)</code> expression
     * @param arg2 equals <code>-1</code> or <code>-1.0</code>
     * @return {@link F#NIL} if the transformation isn't possible.
     */
    private static IExpr powerTimesInverse(final IAST timesAST, final ISignedNumber arg2) {
      IASTAppendable resultAST = F.NIL;
      for (int i = 1; i < timesAST.size(); i++) {
        final IExpr arg = timesAST.get(i);
        if (arg.isPower() && arg.exponent().isReal()) {
          if (!resultAST.isPresent()) {
            resultAST = timesAST.copyAppendable();
            resultAST.map(resultAST, x -> F.Power(x, arg2));
          }
          if (arg.exponent().isMinusOne()) {
            resultAST.set(i, arg.base());
          } else {
            resultAST.set(i, F.Power(arg.base(), arg.exponent().times(arg2)));
          }
        }
      }
      return resultAST;
    }

    /**
     * Determine <code>0 ^ exponent</code>.
     *
     * @param exponent the exponent of the 0-Power expression
     * @return
     */
    private static IExpr powerZeroArg1(final IExpr exponent) {
      EvalEngine engine = EvalEngine.get();
      if (exponent.isZeroResult()) {
        // 0^0
        // engine.printMessage("Infinite expression 0^0");
        IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
        return S.Indeterminate;
      }
      if (exponent.isPositiveResult()) {
        // 0^x /; x>0
        return F.C0;
      }
      if (exponent.isNegativeResult()) {
        // 0^x /; x<0
        IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, exponent)),
            EvalEngine.get());
        return F.CComplexInfinity;
      }

      IExpr a = exponent.re();
      if (a.isReal()) {
        if (a.isNegative()) {
          // engine.printMessage("Infinite expression 0^(negative number)");
          IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, exponent)),
              EvalEngine.get());
          return F.CComplexInfinity;
        }
        if (a.isZero()) {
          // engine.printMessage("Infinite expression 0^0.");
          IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
          return S.Indeterminate;
        }
        return F.C0;
      }
      if (a.isNumericFunction(true)) {
        IExpr temp = engine.evalN(a);
        if (temp.isReal()) {
          if (temp.isNegative()) {
            IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, temp)),
                EvalEngine.get());
            // engine.printMessage("Infinite expression 0^(negative number)");
            return F.CComplexInfinity;
          }
          if (temp.isZero()) {
            IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)),
                EvalEngine.get());
            // engine.printMessage("Infinite expression 0^0.");
            return S.Indeterminate;
          }
          return F.C0;
        }
        if (temp.isComplex() || temp.isComplexNumeric()) {
          IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, temp)), EvalEngine.get());
          // engine.printMessage("Indeterminate expression 0 ^ (complex number) encountered.");
          return S.Indeterminate;
        }
      }

      return F.NIL;
    }

    /**
     * Simplify <code>E^(y+Log(x))</code> to <code>x*E^(y)</code>
     *
     * @param plus
     * @return
     */
    private static IAST powerEPlus(IAST plus) {
      IASTAppendable multiplicationFactors = F.NIL;
      IASTAppendable plusClone = F.NIL;
      for (int i = plus.argSize(); i > 0; i--) {
        final IExpr arg = plus.get(i);
        if (arg.isLog()) {
          if (!multiplicationFactors.isPresent()) {
            multiplicationFactors = F.TimesAlloc(8);
            plusClone = plus.copyAppendable();
          }
          multiplicationFactors.append(arg.first());
          plusClone.remove(i);
        } else if (arg.isTimes() && arg.size() == 3 && arg.second().isLog()
            && arg.first().isReal()) {
          IAST times = (IAST) arg;
          IExpr logArgument = times.arg2().first();
          if (!multiplicationFactors.isPresent()) {
            multiplicationFactors = F.TimesAlloc(8);
            plusClone = plus.copyAppendable();
          }
          // logArgument ^ times.arg1()
          multiplicationFactors.append(F.Power(logArgument, times.arg1()));
          plusClone.remove(i);
        }
      }
      if (multiplicationFactors.isPresent()) {
        multiplicationFactors.append(F.Exp(plusClone));
        return multiplicationFactors;
      }
      return F.NIL;
    }

    /**
     * <code> complex-number ^ fraction-number</code>
     *
     * @param base
     * @param exponent
     * @return
     */
    private static IExpr complexFraction(final IComplex base, final IFraction exponent) {
      if (base.getRealPart().isZero()) {
        if (exponent.isNumEqualRational(F.C1D2)) {
          // square root of pure imaginary number
          IRational im = base.getImaginaryPart();
          boolean negative = false;
          im = im.divideBy(F.C2);
          if (im.isNegative()) {
            im = im.negate();
            negative = true;
          }
          if (NumberUtil.isPerfectSquare(im)) {
            IExpr temp = F.Sqrt(im);
            if (negative) {
              // Sqrt(im.negate()) - I * Sqrt(im);
              return F.Plus(temp, F.Times(F.CNI, temp));
            }
            // Sqrt(im.negate()) + I * Sqrt(im);
            return F.Plus(temp, F.Times(F.CI, temp));
          }
        } else if (exponent.isNumEqualRational(F.CN1D2)) {
          // -(-1)^(3/4)
          return F.Times(F.CN1, F.Power(F.CN1, F.C3D4));
        }
      }
      if (exponent.isPositive()) {
        if (base.isImaginaryUnit()) {
          return F.Power(F.CN1, F.C1D2.times(exponent));
        } else if (base.isNegativeImaginaryUnit()) {
          IInteger numerator = exponent.numerator();
          IInteger denominator = exponent.denominator();
          IInteger div = numerator.div(denominator);
          if (div.isOdd()) {
            div = div.subtract(F.C1);
          }
          IRational rat = exponent.subtract(div);
          numerator = rat.numerator();
          denominator = rat.denominator().multiply(F.C2);
          return F.Times(F.CN1, F.Power(F.CNI, div),
              F.Power(F.CN1, F.fraction(denominator.subtract(numerator), denominator)));
        }
        if (exponent.equals(F.C1D2)) {
          IComplex sqrt = base.sqrtCC();
          if (sqrt != null) {
            return sqrt;
          }
        }
      }
      return F.NIL;
    }

    private static IExpr complexInteger(final IComplex base, final IInteger exponent) {
      if (base.isZero()) {
        return F.C0;
      }

      if (exponent.isZero()) {
        return F.C1;
      }

      return base.pow(exponent.toBigNumerator().intValue());
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 2) {
        throw new UnsupportedOperationException();
      }
      return Math.pow(stack[top - 1], stack[top]);
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int size = ast.size();
      if (ast.head() == S.Power) {
        switch (size) {
          case 0:
            break;
          case 1:
            return F.C1;
          case 2:
            return ast.arg1();
          case 3:
            return binaryOperator(ast, ast.arg1(), ast.arg2(), engine);
          default:
            return powerFoldRight(ast, engine);
        }
      }
      return F.NIL;
    }

    /**
     * <code>Power(a,b,c,d)</code> ==> <code>Power(a, b, Power(c, d)))</code>
     *
     * @param ast
     * @param engine
     * @return
     */
    private static IExpr powerFoldRight(final IAST ast, EvalEngine engine) {
      IExpr last = ast.last();
      for (int i = ast.size() - 2; i >= 0; i--) {
        final IExpr arg = ast.get(i);
        IExpr temp = Power.ofNIL(engine, arg, last);
        if (temp.isPresent()) {
          last = temp;
        } else {
          if (i <= 1) {
            return F.Power(arg, last);
          }
          IASTAppendable result = ast.copyUntil(i);
          result.append(F.Power(arg, last));
          return result;
        }
      }
      return last;
    }

    /** {@inheritDoc} */
    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.ONEIDENTITY | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class Precision extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1 instanceof INum) {
        return F.ZZ(((INum) arg1).precision());
      }
      if (arg1 instanceof IComplexNum) {
        return F.ZZ(((IComplexNum) arg1).precision());
      }
      LOGGER.log(engine.getLogLevel(), "Precision: Numeric expression expected");
      return F.NIL;
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
   * PreDecrement(x)
   *
   * --x
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * decrements <code>x</code> by <code>1</code>, returning the new value of <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>--a</code> is equivalent to <code>a = a - 1</code>:
   *
   * <pre>
   * &gt;&gt; a = 2
   * &gt;&gt; --a
   * 1
   *
   * &gt;&gt; a
   * 1
   * </pre>
   */
  private static class PreDecrement extends Decrement {

    @Override
    protected IASTMutable getAST() {
      return (IASTMutable) F.Plus(null, F.CN1);
    }

    @Override
    protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
      return calculatedResult;
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.PreDecrement;
    }
  }


  /**
   *
   *
   * <pre>
   * PreIncrement(x)
   *
   * ++x
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * increments <code>x</code> by <code>1</code>, returning the new value of <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>++a</code> is equivalent to <code>a = a + 1</code>:
   *
   * <pre>
   * &gt;&gt; a = 2
   * &gt;&gt; ++a
   * 3
   *
   * &gt;&gt; a
   * 3
   * </pre>
   */
  private static class PreIncrement extends PreDecrement {

    @Override
    protected IASTMutable getAST() {
      return (IASTMutable) F.Plus(null, F.C1);
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.PreIncrement;
    }
  }


  /**
   *
   *
   * <pre>
   * Rational
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the head of rational numbers.
   *
   * </blockquote>
   *
   * <pre>
   * Rational(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * constructs the rational number <code>a / b</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Head(1/2)
   * Rational
   *
   * &gt;&gt; Rational(1, 2)
   * 1/2
   *
   * &gt;&gt; -2/3
   * -2/3
   * </pre>
   */
  private static final class Rational extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().equals(S.Rational)) {
        if (!ast.isAST2()) {
          return IOFunctions.printArgMessage(ast, ARGS_2_2, engine);
        }
        try {
          // try to convert into a fractional number
          IExpr numeratorExpr = ast.arg1();
          IExpr denominatorExpr = ast.arg2();
          if (numeratorExpr.isInteger() && denominatorExpr.isInteger()) {
            // already evaluated
          } else {
            numeratorExpr = engine.evaluate(numeratorExpr);
            denominatorExpr = engine.evaluate(denominatorExpr);
            if (!numeratorExpr.isInteger() || !denominatorExpr.isInteger()) {
              return F.NIL;
            }
          }

          // symbolic mode
          IInteger numerator = (IInteger) numeratorExpr;
          IInteger denominator = (IInteger) denominatorExpr;
          if (denominator.isZero()) {
            LOGGER.log(engine.getLogLevel(), "Division by zero expression: {}/{}", numerator,
                denominator);
            if (numerator.isZero()) {
              // 0^0
              return S.Indeterminate;
            }
            return F.CComplexInfinity;
          }
          if (numerator.isZero()) {
            return F.C0;
          }
          return F.fraction(numerator, denominator);

          // don't evaluate in numeric mode
          // } else if (numeratorExpr instanceof INum && denominatorExpr instanceof INum) {
          // INum numerator = (INum) numeratorExpr;
          // INum denominator = (INum) denominatorExpr;
          // if (denominator.isZero()) {
          // engine.printMessage(
          // "Division by zero expression: " + numerator.toString() + "/" + denominator.toString());
          // if (numerator.isZero()) {
          // // 0^0
          // return F.Indeterminate;
          // }
          // return F.CComplexInfinity;
          // }
          // if (numerator.isZero()) {
          // return F.C0;
          // }
          // return F.num(numerator.doubleValue() / denominator.doubleValue());

        } catch (Exception e) {
          LOGGER.debug("Rational.evaluate() failed", e);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Re(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the real component of the complex number <code>z</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Re(3+4I)
   * 3
   *
   * &gt;&gt; Im(0.5 + 2.3*I)
   * 2.3
   * </pre>
   */
  private static final class Re extends AbstractEvaluator {

    public static IExpr evalRe(IExpr expr, EvalEngine engine) {
      if (expr.isDirectedInfinity()) {
        IAST directedInfininty = (IAST) expr;
        if (directedInfininty.isComplexInfinity()) {
          return S.Indeterminate;
        }
        if (directedInfininty.isAST1()) {
          if (directedInfininty.isInfinity()) {
            return F.CInfinity;
          }
          IExpr re = directedInfininty.arg1().re();
          if (re.isNumber()) {
            if (re.isZero()) {
              return F.C0;
            }
            return F.Times(F.Sign(re), F.CInfinity);
          }
        }
      }
      if (expr.isNumber() || expr.isQuantity()) {
        return expr.re();
      }
      if (expr.isRealResult() || expr.isRealVector() || expr.isRealMatrix()) {
        return expr;
      }

      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
      if (negExpr.isPresent()) {
        return Negate(Re(negExpr));
      }
      if (expr.isTimes()) {
        IAST timesAST = (IAST) expr;
        int position = timesAST.indexOf(x -> x.isRealResult());
        if (position > 0) {
          return F.Times(timesAST.get(position), F.Re(timesAST.splice(position)));
        }
        IExpr first = timesAST.arg1();
        if (first.isNumber()) {
          IExpr rest = timesAST.rest().oneIdentity1();
          if (first.isReal()) {
            return F.Times(first, F.Re(expr.rest()));
          }
          return F.Subtract(F.Times(first.re(), F.Re(rest)), F.Times(first.im(), F.Im(rest)));
        }
      }
      if (expr.isPlus()) {
        return ((IAST) expr).mapThread((IAST) F.Re(F.Slot1), 1);
      }
      if (expr.isPower()) {
        IExpr base = expr.base();
        if (base.isRealResult()) {
          // test for x^(a+I*b)
          IExpr exponent = expr.exponent();
          // if (exponent.isNumber()) {
          // // (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
          // return rePowerComplex(x, ((INumber) exponent).re(), ((INumber) exponent).im());
          // }
          // (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
          return rePowerComplex(base, exponent.re(), exponent.im());
        }
      }
      if (expr.isInterval()) {
        return IntervalSym.mapSymbol(S.Re, (IAST) expr);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      return evalRe(arg1, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    /**
     * Evaluate <code>Re(x^(a+I*b))</code>
     *
     * @param x
     * @param a the real part of the exponent
     * @param b the imaginary part of the exponent
     * @return
     */
    private static IExpr rePowerComplex(IExpr x, IExpr a, IExpr b) {
      if ((a.isNumber() || a.isRealResult()) && //
          (b.isNumber() || b.isRealResult())) {
        if (x.isE()) {
          // Re(E^(a+I*b)) -> E^a*Cos[b]
          return Times(Power(S.E, a), Cos(b));
        }
        return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Negate(b), Arg(x)))),
            Cos(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }


  private static final class RealAbs extends AbstractEvaluator {
    private static final class AbsNumericFunction implements DoubleFunction<IExpr> {
      final ISymbol symbol;

      public AbsNumericFunction(ISymbol symbol) {
        this.symbol = symbol;
      }

      @Override
      public IExpr apply(double value) {
        if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
          double result = Math.abs(value);
          if (result > 0.0) {
            return symbol;
          }
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isNumber()) {
        if (arg1.isReal()) {
          return arg1.abs();
        }
        return F.NIL;
      }
      if (arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isReal()) {
          return temp.abs();
        }
        if (temp.isNumber()) {
          return F.NIL;
        }
      }
      if (arg1.isNegativeResult()) {
        return F.Negate(arg1);
      }
      if (arg1.isNonNegativeResult()) {
        return arg1;
      }
      if (arg1.isSymbol()) {
        ISymbol sym = (ISymbol) arg1;
        return sym.mapConstantDouble(new AbsNumericFunction(sym));
      }

      if (arg1.isInterval()) {
        return IntervalSym.abs((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class RealSign extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isReal()) {
        return arg1.sign();
      }
      if (arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isReal()) {
          return temp.sign();
        }
        if (temp.isNumber()) {
          return F.NIL;
        }
      }
      if (arg1.isInfinity()) {
        return F.C1;
      }
      if (arg1.isNegativeInfinity()) {
        return F.CN1;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
   * Sign(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives <code>-1</code>, <code>0</code> or <code>1</code> depending on whether <code>x</code> is
   * negative, zero or positive.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Sign(-2.5)
   * -1
   * </pre>
   */
  private static final class Sign extends AbstractCoreFunctionEvaluator {

    private static final class SignTimesFunction implements Function<IExpr, IExpr> {
      @Override
      public IExpr apply(IExpr expr) {
        if (expr.isNumber()) {
          return numberSign((INumber) expr);
        }
        IExpr temp = F.eval(F.Sign(expr));
        if (!temp.topHead().equals(S.Sign)) {
          return temp;
        }
        return F.NIL;
      }
    }

    /**
     * Gets the sign value of a number. See
     * <a href="http://en.wikipedia.org/wiki/Sign_function">Wikipedia - Sign function</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = F.NIL;
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        result = F.Sign(arg1);
      } else {
        arg1 = ast.arg1();
      }
      if (arg1.isList()) {
        return ((IAST) arg1).mapThread(F.Sign(F.Slot1), 1);
      }

      if (arg1.isNumber()) {
        if (arg1.isComplexNumeric()) {
          IComplexNum c = (IComplexNum) arg1;
          return c.divide(F.num(c.dabs()));
        }
        return numberSign((INumber) arg1);
      }
      if (arg1.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (arg1.isDirectedInfinity()) {
        IAST directedInfininty = (IAST) arg1;
        if (directedInfininty.isComplexInfinity()) {
          return S.Indeterminate;
        }
        if (directedInfininty.isAST1()) {
          return F.Sign(directedInfininty.arg1());
        }
      } else if (arg1.isTimes()) {
        IASTAppendable[] res = ((IAST) arg1).filterNIL(new SignTimesFunction());
        if (res[0].size() > 1) {
          if (res[1].size() > 1) {
            res[0].append(F.Sign(res[1]));
          }
          return res[0];
        }
      } else if (arg1.isPower()) {
        if (arg1.exponent().isReal()) {
          return F.Power(F.Sign(arg1.base()), arg1.exponent());
        }
        if (arg1.base().isE()) {
          // E^z == > E^(I*Im(z))
          return F.Power(S.E, F.Times(F.CI, F.Im(arg1.exponent())));
        }
      } else if (arg1.isAST(S.Sign, 2)) {
        return arg1;
      }
      if (AbstractAssumptions.assumeNegative(arg1)) {
        return F.CN1;
      }
      if (AbstractAssumptions.assumePositive(arg1)) {
        return F.C1;
      }

      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Times(F.CN1, F.Sign(negExpr));
      }
      INumber number = arg1.evalNumber();
      if (number != null) {
        IExpr temp = numberSign(number);
        if (temp.isPresent()) {
          return temp;
        }
      }
      if (arg1.isRealResult() && !arg1.isZero()) {
        return F.Divide(arg1, F.Abs(arg1));
      }
      IExpr y = AbstractFunctionEvaluator.imaginaryPart(arg1, true);
      if (y.isPresent() && y.isRealResult()) {
        IExpr x = AbstractFunctionEvaluator.realPart(arg1, false);
        if (x.isPresent() && x.isRealResult()) {
          // (x + I*y)/Sqrt(x^2 + y^2)
          return F.Times(F.Plus(x, F.Times(F.CI, y)), F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.CN1D2));
        }
      }
      if (arg1.isInterval()) {
        if (arg1.size() == 2) {
          IAST list = (IAST) arg1.first();
          if (list.first().isNegativeResult() && list.second().isNegativeResult()) {
            return F.CN1;
          } else if (list.first().isPositiveResult() && list.second().isPositiveResult()) {
            return F.C1;
          } else if (list.first().isZero() && list.second().isZero()) {
            return F.C0;
          }
        }
        return IntervalSym.mapSymbol(S.Sign, (IAST) arg1);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    public static IExpr numberSign(INumber arg1) {
      if (arg1.isReal()) {
        final int signum = ((ISignedNumber) arg1).complexSign();
        return F.ZZ(signum);
      } else if (arg1.isComplex()) {
        IComplex c = (IComplex) arg1;
        return F.Times(c, F.Power(c.abs(), F.CN1));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }


  /**
   * Gets the signum value of a complex number <code>cc</code>.
   * 
   * <p>
   * Returns:
   * </p>
   * 
   * <pre>
   *  0 for cc == 0; 
   * +1 for Re(cc) &gt; 0 || ( Re(cc) == 0 &amp;&amp; Im(cc) &gt; 0 );
   * -1 for Re(cc) &lt; 0 || ( Re(cc) == 0 &amp;&amp; Im(cc) &lt; 0 );
   * </pre>
   */
  private static final class SignCmp extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      INumber number = arg1.evalNumber();
      if (number != null) {
        final int signum = number.complexSign();
        return F.ZZ(signum);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
   * Sqrt(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the square root of <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Sqrt(4)
   * 2
   *
   * &gt;&gt; Sqrt(5)
   * Sqrt(5)
   *
   * &gt;&gt; Sqrt(5) // N
   * 2.23606797749979
   *
   * &gt;&gt; Sqrt(a)^2
   * a
   * </pre>
   *
   * <p>
   * Complex numbers:
   *
   * <pre>
   * &gt;&gt; Sqrt(-4)
   * I*2
   *
   * &gt;&gt; I == Sqrt(-1)
   * True
   *
   * &gt;&gt; N(Sqrt(2), 50)
   * 1.41421356237309504880168872420969807856967187537694
   * </pre>
   */
  private static class Sqrt extends AbstractArg1 implements INumeric {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      return Power(o, F.C1D2);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.sqrt(stack[top]);
    }

    /** {@inheritDoc} */
    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }


  /**
   *
   *
   * <pre>
   * Surd(expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>n</code>-th root of <code>expr</code>. If the result is defined, it's a real
   * value.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Surd(16.0,3)
   * 2.51984
   * </pre>
   */
  private static class Surd extends AbstractArg2 implements INumeric {
    @Override
    public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
      if (af1.isZero()) {
        EvalEngine ee = EvalEngine.get();
        LOGGER.log(ee.getLogLevel(), "Surd(a,b) division by zero");
        return S.Indeterminate;
      }
      if (af0.isNegative()) {
        return af0.abs().pow(af1.inverse()).negate();
      }
      return af0.pow(af1.inverse());
    }

    @Override
    public IExpr e2DblArg(INum d0, INum d1) {
      double val = d0.doubleValue();
      double r = d1.doubleValue();
      double result = doubleSurd(val, r);
      if (Double.isNaN(result)) {
        return S.Indeterminate;
      }
      return F.num(result);
    }

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr base, final IExpr root) {
      if (base.isNumber() && root.isInteger()) {
        EvalEngine engine = EvalEngine.get();
        if (base.isComplex() || base.isComplexNumeric()) {
          LOGGER.log(engine.getLogLevel(), "Surd(a,b) - \"a\" should be a real value.");
          return F.NIL;
        }

        if (root.isZero()) {
          LOGGER.log(engine.getLogLevel(), "Surd(a,b) division by zero");
          return S.Indeterminate;
        }
        if (base.isNegative()) {
          if (((IInteger) root).isEven()) {
            // Surd is not defined for even roots of negative values.
            IOFunctions.printMessage(ast.topHead(), "nonegs", F.CEmptyList, engine);
            return S.Indeterminate;
          }
          return F.Times(F.CN1, Power(base.negate(), ((IInteger) root).inverse()));
        }

        if (base.isMinusOne()) {
          return F.CN1;
        }
        return Power(base, ((IInteger) root).inverse());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDREST | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public double evalReal(double[] stack, int top, int size) {
      if (size != 2) {
        throw new UnsupportedOperationException();
      }
      return doubleSurd(stack[top - 1], stack[top]);
    }

    private static double doubleSurd(double val, double r) {
      if (r == 0.0d) {
        EvalEngine ee = EvalEngine.get();
        LOGGER.log(ee.getLogLevel(), "Surd(a,b) division by zero");
        return Double.NaN;
      }
      if (val < 0.0d) {
        double root = Math.floor(r);
        if (Double.isFinite(r) && Double.compare(r, root) == 0) {
          // integer type
          int iRoot = (int) root;
          if ((iRoot & 0x0001) == 0x0000) {
            Level level = EvalEngine.get().getLogLevel();
            LOGGER.log(level, "Surd(a,b) - undefined for negative \"a\" and even \"b\" values");
            return Double.NaN;
          }
          return -Math.pow(Math.abs(val), 1.0d / r);
        }
        return Double.NaN;
      }
      return Math.pow(val, 1.0d / r);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr base = ast.arg1();
      if (base.isComplex() || base.isComplexNumeric()) {
        return IOFunctions.printMessage(ast.topHead(), "preal", F.list(base), engine);
      }
      IExpr arg2 = engine.evaluateNonNumeric(ast.arg2());
      if (arg2.isZero()) {
        // Indeterminate expression `1` encountered.
        IOFunctions.printMessage(ast.topHead(), "indet", F.list(ast), engine);
        return S.Indeterminate;
      }
      if (arg2.isNumber()) {
        if (arg2.isInteger()) {
          IInteger root = (IInteger) arg2;
          if (base.isNegative()) {
            if (root.isEven()) {
              // Surd is not defined for even roots of negative values.
              IOFunctions.printMessage(ast.topHead(), "nonegs", F.CEmptyList, engine);
              return S.Indeterminate;
            }
          }
          if (base.isInfinity()) {
            if (root.isNegative()) {
              return F.C0;
            } else {
              return F.CInfinity;
            }
          } else if (base.isNegativeInfinity()) {
            if (root.isNegative()) {
              return F.C0;
            } else {
              return F.CNInfinity;
            }
          }
          if (root.isNegative()) {
            if (root.isMinusOne()) {
              return F.Power(base, F.CN1);
            }
            return F.Power(F.Surd(base, root.negative()), F.CN1);
          }
        } else {
          // Integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "int", F.list(ast, F.C2),
              EvalEngine.get());
        }
      }

      return binaryOperator(ast, ast.arg1(), ast.arg2(), engine);
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
   * SubtractFrom(x, dx)
   *
   * x -= dx
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is equivalent to <code>x = x - dx</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 10
   * &gt;&gt; a -= 2
   * 8
   *
   * &gt;&gt; a
   * 8
   * </pre>
   */
  private static class SubtractFrom extends AddTo {

    @Override
    protected IASTMutable getAST(final IExpr value) {
      return (IASTMutable) F.Plus(null, F.Negate(value));
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.SubtractFrom;
    }

    @Override
    protected ISymbol getArithmeticSymbol() {
      return S.Subtract;
    }
  }


  /**
   *
   *
   * <pre>
   * Subtract(a, b)
   *
   * a - b
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents the subtraction of <code>b</code> from <code>a</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 5 - 3
   * 2
   *
   * &gt;&gt; a - b // FullForm
   * "Plus(a, Times(-1, b))"
   *
   * &gt;&gt; a - b - c
   * a-b-c
   *
   * &gt;&gt; a - (b - c)
   * a-b+c
   * </pre>
   */
  private static class Subtract extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // arg1 + (-1)*arg2
      return F.Subtract(ast.arg1(), ast.arg2());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }


  /**
   *
   *
   * <pre>
   * Times(a, b, ...)
   *
   * a * b * ...
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents the product of the terms <code>a, b, ...</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 10*2
   * 20
   *
   * &gt;&gt; a * a
   * a^2
   *
   * &gt;&gt; x ^ 10 * x ^ -2
   * x^8
   *
   * &gt;&gt; {1, 2, 3} * 4
   * {4,8,12}
   *
   * &gt;&gt; Times @@ {1, 2, 3, 4}
   * 24
   *
   * &gt;&gt; IntegerLength(Times@@Range(100))
   * 158
   * </pre>
   *
   * <p>
   * <code>Times</code> has default value <code>1</code>:<br>
   *
   * <pre>
   * &gt;&gt; a /. n_. * x_ :&gt; {n, x}
   * {1,a}
   *
   * &gt;&gt; -a*b // FullForm
   * "Times(-1, a, b)"
   *
   * &gt;&gt; -(x - 2/3)
   * 2/3-x
   *
   * &gt;&gt; -x*2
   * -2 x
   *
   * &gt;&gt; -(h/2) // FullForm
   * "Times(Rational(-1,2), h)"
   *
   * &gt;&gt; x / x
   * 1
   *
   * &gt;&gt; 2*x^2 / x^2
   * 2
   *
   * &gt;&gt; 3.*Pi
   * 9.42477796076938
   *
   * &gt;&gt; Head(3 * I)
   * Complex
   *
   * &gt;&gt; Head(Times(I, 1/2))
   * Complex
   *
   * &gt;&gt; Head(Pi * I)
   * Times
   *
   * &gt;&gt; -2.123456789 * x
   * -2.123456789*x
   *
   * &gt;&gt; -2.123456789 * I
   * I*(-2.123456789)
   *
   * &gt;&gt; N(Pi, 30) * I
   * I*3.14159265358979323846264338327
   *
   * &gt;&gt; N(I*Pi, 30)
   * I*3.14159265358979323846264338327
   *
   * &gt;&gt; N(Pi * E, 30)
   * 8.53973422267356706546355086954
   *
   * &gt;&gt; N(Pi, 30) * N(E, 30)
   * 8.53973422267356706546355086954
   *
   * &gt;&gt; N(Pi, 30) * E
   * 8.53973422267356649108017774746
   *
   * &gt;&gt; N(Pi, 30) * E // Precision
   * 30
   * </pre>
   */
  public static class Times extends AbstractArgMultiple implements INumeric {
    /** Constructor for the singleton */
    public static final Times CONST = new Times();

    private static HashedOrderlessMatcherTimes TIMES_ORDERLESS_MATCHER;

    @Override
    public HashedOrderlessMatcher getHashRuleMap() {
      return TIMES_ORDERLESS_MATCHER;
    }

    private static IExpr eInfinity(IAST inf, IExpr o1) {
      if (inf.isComplexInfinity()) {
        if (o1.isZero()) {
          return S.Indeterminate;
        }
        return F.CComplexInfinity;
      }
      if (inf.isInfinity()) {
        if (o1.isInfinity()) {
          return F.CInfinity;
        }
        if (o1.isNegativeInfinity()) {
          return F.CNInfinity;
        }
        if (o1.isComplexInfinity()) {
          return F.CComplexInfinity;
        }
        if (!o1.isZero()) {
          if (o1.isNegativeResult()) {
            return F.CNInfinity;
          }
          if (o1.isPositiveResult()) {
            return F.CInfinity;
          }
        }
      }
      if (inf.isNegativeInfinity()) {
        if (o1.isInfinity()) {
          return F.CNInfinity;
        }
        if (o1.isNegativeInfinity()) {
          return F.CInfinity;
        }
        if (o1.isComplexInfinity()) {
          return F.CComplexInfinity;
        }
        if (!o1.isZero()) {
          if (o1.isNegativeResult()) {
            return F.CInfinity;
          }
          if (o1.isPositiveResult()) {
            return F.CNInfinity;
          }
        }
      }
      if (inf.isAST1()) {
        if (o1.isNumber()) {
          if (inf.isAST1()) {
            return DirectedInfinity.timesInf(inf, o1);
          }
        }
        if (o1.isDirectedInfinity() && o1.isAST1()) {
          return F.eval(F.DirectedInfinity(F.Times(inf.first(), o1.first())));
        }
      }
      return F.NIL;
    }

    public Times() {}

    /**
     * Distribute a leading integer factor.
     *
     * @param noEvalExpression return this expression if no evaluation step was done
     * @param originalExpr the original expression which is used, if <code>
     *     !noEvalExpression.isPresent()</code>
     * @return the evaluated object or <code>noEvalExpression</code>, if the distribution of an
     *         integer factor isn't possible
     */
    private static IExpr distributeLeadingFactor(IExpr noEvalExpression, IAST originalExpr) {
      IExpr expr = noEvalExpression;
      if (!expr.isPresent()) {
        expr = originalExpr;
      }
      if (expr.isTimes() && expr.first().isInteger()) {
        IAST times = (IAST) expr;
        IInteger leadingFactor = (IInteger) times.arg1();

        if (leadingFactor.isMinusOne()) {
          return distributeLeadingFactorCN1(noEvalExpression, times);
        } else {
          return distributeLeadingFactorModulus(noEvalExpression, times, leadingFactor);
        }
      }
      return noEvalExpression;
    }

    /**
     * Distribute a leading integer factor over the integer powers if available. Example: <code>
     * 12*2^x*3^y</code> distribute leading factor <code>12 == 2*2*3</code> to the Power expressions
     * <code>2^(2+x)*3^(1+y)</code>
     *
     * @param noEvalExpression return this expression if no evaluation step was done
     * @param times the <code>Times(...)</code> AST
     * @param leadingFactor the first factor in <code>Times(...)</code>
     * @return the evaluated object or <code>noEvalExpression</code>, if the distribution of an
     *         integer factor isn't possible
     */
    private static IExpr distributeLeadingFactorModulus(IExpr noEvalExpression, IAST times,
        IInteger leadingFactor) {
      boolean negative = false;
      if (leadingFactor.isNegative()) {
        leadingFactor = leadingFactor.negate();
        negative = true;
      }
      IASTAppendable result = F.NIL;
      for (int i = 2; i < times.size(); i++) {
        final IExpr arg = times.get(i);
        if (arg.isPower() && arg.base().isInteger() && !arg.exponent().isNumber()) {
          IInteger powArg1 = (IInteger) arg.base();
          if (powArg1.isPositive()) {
            IInteger mod = F.C0;
            int count = 0;
            while (!leadingFactor.isZero()) {
              mod = leadingFactor.mod(powArg1);
              if (mod.isZero()) {
                count++;
                leadingFactor = leadingFactor.div(powArg1);
              } else {
                break;
              }
            }
            if (count > 0) {
              if (!result.isPresent()) {
                result = times.copyAppendable();
              }
              result.set(i, F.Power(arg.base(), F.Plus(F.ZZ(count), arg.exponent())));
            }
          }
        }
      }
      if (result.isPresent()) {
        if (negative) {
          leadingFactor = leadingFactor.negate();
        }
        result.set(1, leadingFactor);
        if (leadingFactor.isMinusOne()) {
          return distributeLeadingFactorCN1(result, result);
        }
        return result;
      }
      return noEvalExpression;
    }

    /**
     * Distribute a leading factor <code>-1</code> to <code>Plus(...)</code> terms of the <code>
     * times</code> expression if possible. <b>Example:</b> <code>-a*(2-x)</code> distribute leading
     * factor <code>-1</code> to the <code>Plus(...)</code> expression <code>(2-x)</code> returns
     * <code>a*(-2+x)</code>
     *
     * @param noEvalExpr return this expression if no evaluation step was done
     * @param times the <code>Times(...)</code> AST
     * @return the evaluated object or <code>noEvalExpression</code>, if the distribution of an
     *         integer factor isn't possible
     */
    private static IExpr distributeLeadingFactorCN1(IExpr noEvalExpr, IAST times) {
      IASTAppendable result = F.NIL;
      for (int i = 2; i < times.size(); i++) {
        final IExpr arg = times.get(i);
        if (arg.isPlus()) {
          IAST plus = (IAST) arg;
          if (AbstractFunctionEvaluator.isNegativeWeighted(plus, true)) {
            IExpr temp =
                EvalEngine.get().evaluate(plus.mapThread(F.binaryAST2(Times, CN1, F.Slot1), 2));
            result = times.copyAppendable();
            result.set(i, temp);
            result.remove(1);
            return result;
          }
        }
      }
      return noEvalExpr;
    }

    @Override
    public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
      return c0.multiply(c1);
    }

    @Override
    public IExpr e2DblArg(final INum d0, final INum d1) {
      return d0.multiply(d1);
    }

    @Override
    public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
      return d0.multiply(d1);
    }

    @Override
    public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
      return f0.mul(f1);
    }

    @Override
    public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
      return i0.multiply(i1);
    }

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr arg1, final IExpr arg2) {

      // the case where both args are numbers is already handled in binaryOperator()
      if (arg1.isReal() || arg2.isReal()) {
        if (arg1.isZero()) {
          if (arg2.isQuantity()) {
            return ((IQuantity) arg2).ofUnit(F.C0);
          }
          if (arg2.isDirectedInfinity()) {
            // Indeterminate expression `1` encountered.
            IOFunctions.printMessage(S.Infinity, "indet", F.list(F.Times(arg1, arg2)),
                EvalEngine.get());
            return S.Indeterminate;
          }
          return F.C0;
        } else if (arg2.isZero()) {
          if (arg1.isQuantity()) {
            return ((IQuantity) arg1).ofUnit(F.C0);
          }
          if (arg1.isDirectedInfinity()) {
            // Indeterminate expression `1` encountered.
            IOFunctions.printMessage(S.Infinity, "indet", F.list(F.Times(arg1, arg2)),
                EvalEngine.get());
            return S.Indeterminate;
          }
          return F.C0;
        } else if (arg1.isOne()) {
          return arg2;
        } else if (arg2.isOne()) {
          return arg1;
        }
      }
      if (arg1.isNumber() && arg2.isNumber()) {
        return F.NIL;
      }
      if (arg1.isSymbol() || arg2.isSymbol()) {
        if (arg1 == arg2) {
          return F.Power(arg1, C2);
        } else if (arg1.isSymbol()) {
          if (arg2.isAtom()) {
            return F.NIL;
          }
        } else if (arg2.isSymbol()) {
          if (arg1.isAtom()) {
            return F.NIL;
          }
        }
      }

      // note: not a general rule
      // if (arg1.isMinusOne() && arg2.isPlus()) {
      // return ((IAST) arg2).map(x -> x.negate(), 1);
      // }

      if (arg1.isInterval()) {
        if (arg2.isInterval()) {
          return IntervalSym.times((IAST) arg1, (IAST) arg2);
        }
        if (arg2.isRealResult()) {
          // return timesInterval(arg1, arg2);
          return IntervalSym.times(arg2, (IAST) arg1);
        }
        // donn't create Power(...,...)
        return F.NIL;
      } else if (arg2.isInterval()) {
        if (arg1.isRealResult()) {
          // return timesInterval(arg1, arg2);
          return IntervalSym.times(arg1, (IAST) arg2);
        }
        // donn't create Power(...,...)
        return F.NIL;
      } else if (arg1.equals(arg2)) {
        return F.Power(arg1, C2);
      }

      if (arg1.isQuantity()) {
        IQuantity q = (IQuantity) arg1;
        return q.times(arg2, true);
      } else if (arg2.isQuantity()) {
        IQuantity q = (IQuantity) arg2;
        return q.times(arg1, true);
      } else if (arg1.isAST() || arg2.isAST()) {
        final int arg1Ordinal = arg1.headID();
        final int arg2Ordinal = arg2.headID();
        if (arg1Ordinal < 0 && arg2Ordinal < 0) {
          return F.NIL;
        }
        if (arg1Ordinal == ID.DirectedInfinity && arg1.isDirectedInfinity()) {
          IExpr temp = eInfinity((IAST) arg1, arg2);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (arg2Ordinal == ID.DirectedInfinity && arg2.isDirectedInfinity()) {
          IExpr temp = eInfinity((IAST) arg2, arg1);
          if (temp.isPresent()) {
            return temp;
          }
        }

        switch (arg1Ordinal) {
          case ID.Power:
            if (arg1.size() == 3) {
              // (x^a) * b
              IExpr power0Base = arg1.base();
              IExpr power0Exponent = arg1.exponent();
              if (arg1.equalsAt(1, arg2)) {
                // (x^a) * x
                if ((power0Exponent.isNumber() && !arg2.isRational())
                    || !power0Exponent.isNumber()) {
                  // avoid re-evaluation of a root of a rational number (example: 2*Sqrt(2) )
                  return F.Power(arg2, power0Exponent.inc());
                }
              }
              if (arg2.isPower()) {
                IExpr power1Base = arg2.base();
                IExpr power1Exponent = arg2.exponent();
                IExpr temp =
                    timesPowerPower(power0Base, power0Exponent, power1Base, power1Exponent);
                if (temp.isPresent()) {
                  return temp;
                }
              }
            }
            break;
          // case ID.Quantity:
          // if (arg1.isQuantity()) {
          // IQuantity q = (IQuantity) arg1;
          // return q.times(arg2);
          // }
          // break;
          default:
        }

        switch (arg2Ordinal) {
          case ID.Plus:
            if (arg1.isFraction() && arg1.isNegative() && arg2.isPlus()) {
              return F.Times(arg1.negate(), arg2.negate());
            }
            break;
          case ID.Power:
            if (arg2.size() == 3) {
              IExpr power1Base = arg2.base();
              IExpr power1Exponent = arg2.exponent();
              IExpr temp = timesArgPower(arg1, power1Base, power1Exponent);
              if (temp.isPresent()) {
                return temp;
              }
            }
            break;
          case ID.Log:
            if (arg1.isNegative() && arg2.isLog() && arg2.first().isFraction() && arg1.isReal()) {
              IFraction f = (IFraction) arg2.first();
              if (f.isPositive() && f.isLT(F.C1)) {
                // -<number> * Log(<fraction>) -> <number> * Log(<fraction>.inverse())
                return arg1.negate().times(F.Log(f.inverse()));
              }
            }
            break;

          case ID.Interval:
            if (arg2.isInterval()) {
              if (arg1.isInterval()) {
                return IntervalSym.times((IAST) arg1, (IAST) arg2);
              }
              return IntervalSym.times(arg1, (IAST) arg2);
            }
            break;
          // case ID.Quantity:
          // if (arg2.isQuantity()) {
          // IQuantity q = (IQuantity) arg2;
          // return q.times(arg1);
          // }
          // break;
          case ID.SeriesData:
            if (arg2 instanceof ASTSeriesData) {
              return ((ASTSeriesData) arg2).times(arg1);
            }
            break;

          default:
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
      return c0.multiply(F.complex(i1, F.C0));
    }

    private IExpr evalNumericMode(final IAST ast) {
      INum number = F.CD1;
      int start = -1;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg instanceof INum) {
          if (arg instanceof ApfloatNum) {
            number = number.multiply((INum) arg);
          } else {
            if (number instanceof ApfloatNum) {
              number = number.multiply(((INum) arg).apfloatNumValue());
            } else {
              number = number.multiply((INum) arg);
            }
          }
        } else if (arg instanceof IComplexNum) {
          start = i;
          break;
        } else {
          return F.NIL;
        }
      }
      if (start < 0) {
        return number;
      }
      IComplexNum complexNumber;
      if (number instanceof Num) {
        complexNumber = F.complexNum(((Num) number).doubleValue());
      } else {
        complexNumber = F.complexNum(number.apfloatValue());
      }
      for (int i = start; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg instanceof INum) {
          number = (INum) arg;
          if (number instanceof Num) {
            complexNumber = complexNumber.multiply(F.complexNum(((Num) number).doubleValue()));
          } else {
            complexNumber = complexNumber.multiply(F.complexNum(number.apfloatValue()));
          }
        } else if (arg instanceof IComplexNum) {
          if (complexNumber instanceof ApcomplexNum) {
            complexNumber = complexNumber.multiply(((IComplexNum) arg).apcomplexNumValue());
          } else {
            complexNumber = complexNumber.multiply((IComplexNum) arg);
          }
        } else {
          return F.NIL;
        }
      }
      return complexNumber;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      double result = 1;
      for (int i = top - size + 1; i < top + 1; i++) {
        result *= stack[i];
      }
      return result;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (size == 1) {
        if (ast.head() == S.Times) {
          return F.C1;
        }
        return F.NIL;
      }
      final IExpr arg1 = ast.arg1();
      if (size == 2) {
        if (ast.head() == S.Times) {
          // OneIdentity
          return arg1;
        }
        return F.NIL;
      }
      if (size > 2) {
        IAST temp = evaluateHashsRepeated(ast, engine);
        if (temp.isPresent()) {
          return temp.oneIdentity1();
        }
      }
      if (ast.isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
        IAST temp = engine.evalArgsOrderlessN(ast);
        if (temp.isPresent()) {
          ast = temp;
        }
      }
      if (size == 3) {
        // if ((ast1.arg1().isNumeric() || ast1.arg1().isOne() || ast1.arg1().isMinusOne())
        if ((arg1.isOne() || arg1.isMinusOne()) && ast.arg2().isPlus()) {
          if (arg1.isOne()) {
            return ast.arg2();
          }
          final IAST arg2 = (IAST) ast.arg2();
          // distribute the number over the sum:
          return arg2.mapThread(F.Times(arg1, F.Slot1), 2);
        }
        final IExpr arg2 = ast.arg2();
        IExpr temp = distributeLeadingFactor(binaryOperator(ast, arg1, arg2, engine), ast);
        if (temp.isPresent()) {
          return temp;
        }
        return binaryOperator(ast, arg1, arg2, engine);
      }

      if (size > 3) {
        final ISymbol sym = ast.topHead();
        IASTAppendable result = F.NIL;
        IExpr tempArg1 = arg1;
        boolean evaled = false;
        int i = 2;
        boolean isIASTAppendable = false;
        IAST astTimes = ast;
        while (i < astTimes.size()) {

          IExpr binaryResult = binaryOperator(astTimes, tempArg1, astTimes.get(i), engine);

          if (!binaryResult.isPresent()) {

            for (int j = i + 1; j < astTimes.size(); j++) {
              binaryResult = binaryOperator(astTimes, tempArg1, astTimes.get(j), engine);

              if (binaryResult.isPresent()) {
                evaled = true;
                tempArg1 = binaryResult;
                if (isIASTAppendable) {
                  ((IASTAppendable) astTimes).remove(j);
                } else {
                  // creates an IASTAppendable
                  astTimes = astTimes.splice(j);
                  isIASTAppendable = true;
                }
                break;
              }
            }

            if (!binaryResult.isPresent()) {
              if (!result.isPresent()) {
                result = F.ast(sym, astTimes.size() - i + 1);
              }
              result.append(tempArg1);
              if (i == astTimes.argSize()) {
                result.append(astTimes.get(i));
              } else {
                tempArg1 = astTimes.get(i);
              }
              i++;
            }

          } else {
            evaled = true;
            tempArg1 = binaryResult;

            if (i == astTimes.argSize()) {
              if (!result.isPresent()) {
                result = F.ast(sym, astTimes.size() - i + 1);
              }
              result.append(tempArg1);
            }

            i++;
          }
        }

        if (evaled && result.isPresent()) {
          if (sym.hasOneIdentityAttribute() && result.size() > 1) {
            return result.oneIdentity0();
          }

          return distributeLeadingFactor(result, F.NIL);
        }
        return distributeLeadingFactor(F.NIL, astTimes);
      }

      if (engine.isSymbolicMode(S.Times.getAttributes())) {
        ast.addEvalFlags(IAST.BUILT_IN_EVALED);
      }
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      IExpr temp = evalNumericMode(ast);
      if (temp.isPresent()) {
        return temp;
      }
      return evaluate(ast, engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT
          | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);

      TIMES_ORDERLESS_MATCHER = Arithmetic.Times.initTimesHashMatcher();
      super.setUp(newSymbol);
    }

    private static HashedOrderlessMatcherTimes initTimesHashMatcher() {
      HashedOrderlessMatcherTimes timesMatcher = new HashedOrderlessMatcherTimes();
      timesMatcher.defineHashRule(new HashedPatternRulesLog( //
          Log(x_), //
          Log(y_)));

      // Sin(x)*Cot(x) -> Cos(x)
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Sin(x_), //
          F.Cot(x_), //
          F.Cos(x)));
      // Sin(x)*Csc(x) -> 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Sin(x_), //
          F.Csc(x_), //
          F.C1));

      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Tan(x_), //
          F.Cot(x_), //
          F.C1));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Cos(x_), //
          F.Sec(x_), //
          F.C1));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Cos(x_), //
          F.Tan(x_), //
          F.Sin(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Csc(x_), //
          F.Tan(x_), //
          F.Sec(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Csc(x_), F.m_), //
          F.Power(F.Cot(x_), F.n_DEFAULT), //
          F.Condition(F.Times(F.Power(F.Csc(S.x), F.Plus(S.m, S.n)), F.Power(F.Cos(S.x), S.n)),
              F.And(F.Not(F.NumberQ(S.m)), F.IntegerQ(S.n), F.Greater(S.n, F.C0)))));
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Sec(x_), F.m_), //
          F.Power(F.Tan(x_), F.n_DEFAULT), //
          F.Condition(F.Times(F.Power(F.Sec(S.x), F.Plus(S.m, S.n)), F.Power(F.Sin(S.x), S.n)),
              F.And(F.Not(F.NumberQ(S.m)), F.IntegerQ(S.n), F.Greater(S.n, F.C0)))));
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Csch(x_), F.m_), //
          F.Power(F.Coth(x_), F.n_DEFAULT), //
          F.Condition(F.Times(F.Power(F.Csch(S.x), F.Plus(S.m, S.n)), F.Power(F.Cosh(S.x), S.n)),
              F.And(F.Not(F.NumberQ(S.m)), F.IntegerQ(S.n), F.Greater(S.n, F.C0)))));
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Sech(x_), F.m_), //
          F.Power(F.Tanh(x_), F.n_DEFAULT), //
          F.Condition(F.Times(F.Power(F.Sech(S.x), F.Plus(S.m, S.n)), F.Power(F.Sinh(S.x), S.n)),
              F.And(F.Not(F.NumberQ(S.m)), F.IntegerQ(S.n), F.Greater(S.n, F.C0)))));
      // ProductLog(x_)*E^ProductLog(x_) = x
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.ProductLog(x_), //
          F.Power(S.E, F.ProductLog(x_)), //
          x));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Gamma(x_), //
          F.Gamma(F.Plus(F.C1, F.Times(F.CN1, x_))), //
          // Pi*Csc(x*Pi)
          F.Times(S.Pi, F.Csc(F.Times(x, S.Pi)))));

      // Sin(x_)^2/(1-Cos(x_)^2) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Sin(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Cos(x_), F.C2))), F.CN1), //
          F.C1));
      // (1-Cos(x_)^2) / Sin(x_)^2 = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Cos(x_), F.C2))), //
          F.Power(F.Sin(x_), F.CN2), //
          F.C1));

      // Cos(x_)^2/(1-Sin(x_)^2) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Cos(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Sin(x_), F.C2))), F.CN1), //
          F.C1));
      // (1-Sin(x_)^2) / Cos(x_)^2 = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Sin(x_), F.C2))), //
          F.Power(F.Cos(x_), F.CN2), //
          F.C1));

      // Sech(x_)^2/(1-Tanh(x_)^2 ) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Sech(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Tanh(x_), F.C2))), F.CN1), //
          F.C1));
      // (1-Tanh(x_)^2 ) / Sech(x_)^2 = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Tanh(x_), F.C2))), //
          F.Power(F.Sech(x_), F.CN2), //
          F.C1));

      // Tanh(x_)^2/(1-Sech(x_)^2 ) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Tanh(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Sech(x_), F.C2))), F.CN1), //
          F.C1));
      // (1-Sech(x_)^2 ) / Tanh(x_)^2= 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Sech(x_), F.C2))), //
          F.Power(F.Tanh(x_), F.CN2), //
          F.C1));

      // Cos(2*x_)/(1-2*Sin(x)^2) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Cos(F.Times(F.C2, x_)), //
          F.Power(F.Plus(F.C1, F.Times(F.CN2, F.Power(F.Sin(x_), F.C2))), F.CN1), //
          F.C1));
      // (1-2*Sin(x)^2) / Cos(2*x_) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Times(F.CN2, F.Power(F.Sin(x_), F.C2))), //
          F.Power(F.Cos(F.Times(F.C2, x_)), F.CN1), //
          F.C1));

      // Cos(2*x_)/(-1+2*Cos(x)^2) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Cos(F.Times(F.C2, x_)), //
          F.Power(F.Plus(F.CN1, F.Times(F.C2, F.Power(F.Cos(x_), F.C2))), F.CN1), //
          F.C1));
      // (-1+2*Cos(x)^2) / Cos(2*x_) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.CN1, F.Times(F.C2, F.Power(F.Cos(x_), F.C2))), //
          F.Power(F.Cos(F.Times(F.C2, x_)), F.CN1), //
          F.C1));

      // Sec(x_)^2/(1+Tan(x_)^2 ) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Sec(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Power(F.Tan(x_), F.C2)), F.CN1), //
          F.C1));
      // (1+Tan(x_)^2) / Sec(x_)^2 = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Power(F.Tan(x_), F.C2)), //
          F.Power(F.Sec(x_), F.CN2), //
          F.C1));

      // Csc(x_)^2/(1+Cot(x_)^2 ) = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Power(F.Csc(x_), F.C2), //
          F.Power(F.Plus(F.C1, F.Power(F.Cot(x_), F.C2)), F.CN1), //
          F.C1));
      // (1+Cot(x_)^2) / Csc(x_)^2 = 1
      timesMatcher.defineHashRule(new HashedPatternRulesTimesPower( //
          F.Plus(F.C1, F.Power(F.Cot(x_), F.C2)), //
          F.Power(F.Csc(x_), F.CN2), //
          F.C1));

      // TODO: HACK useOnlyEqualFactors = true in the following rules,
      // to avoid stack overflow in integration rules.
      // If true use only rules where both factors are equal,
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Sin(x_), //
          F.Sec(x_), //
          F.Tan(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Cos(x_), //
          F.Csc(x_), //
          F.Cot(x)));

      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Cosh(x_), //
          F.Tanh(x_), //
          F.Sinh(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Coth(x_), //
          F.Sinh(x_), //
          F.Cosh(x)));

      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Csch(x_), //
          F.Tanh(x_), //
          F.Sech(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Coth(x_), //
          F.Sech(x_), //
          F.Csch(x)));

      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Sech(x_), //
          F.Sinh(x_), //
          F.Tanh(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Sech(x_), //
          F.Cosh(x_), //
          F.C1));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Csch(x_), //
          F.Sinh(x_), //
          F.C1));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Cosh(x_), //
          F.Csch(x_), //
          F.Coth(x)));
      timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
          F.Coth(x_), //
          F.Tanh(x_), //
          F.C1));
      return timesMatcher;
    }

    /**
     * Try simplifying <code>arg1 * ( base2 ^ exponent2 )</code>
     *
     * @param arg1
     * @param base2
     * @param exponent2
     * @return
     */
    private IExpr timesArgPower(final IExpr arg1, IExpr base2, IExpr exponent2) {
      if (arg1.isNumber() && base2.isRational() && exponent2.isFraction()) {
        if (arg1.isExactNumber() && exponent2.isNegative()) {
          // arg1_ * base2_ ^exponent2_ /; expoennt2 negative fraction; base rational; arg1
          // IRational or
          // IComplex
          IRational rat = ((INumber) arg1).rationalFactor();
          if (rat != null) {
            if (base2.equals(rat.numerator())) {
              if (rat.isNegative()) {
                rat = rat.negate();
              }
              IExpr factor = ((INumber) arg1).divide(rat.numerator());
              return F.Times(factor, F.Power(rat.numerator(), F.C1.add((IRational) exponent2)));
            }
          }
        }

        if (base2.isMinusOne()) {
          if (arg1.isImaginaryUnit()) {
            // I * power1Arg1 ^ power1Arg2 -> (-1) ^ (power1Arg2 + (1/2))
            return F.Power(F.CN1, exponent2.plus(F.C1D2));
          }
          if (arg1.isNegativeImaginaryUnit()) {
            // (-I) * power1Arg1 ^ power1Arg2 -> (-1) * (-1) ^ (power1Arg2 + (1/2))
            return F.Times(F.CN1, F.Power(F.CN1, exponent2.plus(F.C1D2)));
          }
        }
        if (arg1.isRational()) {
          IExpr temp = timesRationalPower((IRational) arg1, base2, exponent2);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (arg1.isComplex() && ((IComplex) arg1).getRealPart().isZero()) {
          IComplex complex1 = (IComplex) arg1;
          IRational complex1Im = complex1.getImaginaryPart();
          if (!complex1Im.isOne() && !complex1Im.isMinusOne()) {
            IExpr temp = timesRationalPower(complex1Im, base2, exponent2);
            if (temp.isPresent()) {
              return F.Times(F.CI, temp);
            }
          }
        }
      }

      if (arg1.equals(base2)) {
        if (exponent2.isNumber() && !arg1.isRational() || //
            !exponent2.isNumber()) {
          // avoid reevaluation of a root of a rational number (example: 2*Sqrt(2) )
          return F.Power(arg1, exponent2.inc());
        }
      } else if (arg1.negate().equals(base2) && base2.isPositive()) {
        if ((exponent2.isNumber() && !arg1.isRational()) || //
            !exponent2.isNumber()) {
          // avoid reevaluation of a root of a rational number (example: -2*Sqrt(2) )
          return F.Negate(F.Power(base2, exponent2.inc()));
        }
      } else if (arg1.isFraction() && base2.isFraction() && base2.isPositive()) {
        IExpr inverse = base2.inverse();
        IExpr o1negExpr = AbstractFunctionEvaluator.getPowerNegativeExpression(exponent2, true);
        if (o1negExpr.isPresent()) {
          if (arg1.equals(inverse)) {
            return F.Power(base2, F.Plus(F.CN1, exponent2));
          } else if (arg1.negate().equals(inverse)) {
            return F.Negate(F.Power(base2, F.Plus(F.CN1, exponent2)));
          }
        } else {
          if (arg1.equals(inverse)) {
            return F.Power(inverse, F.Subtract(F.C1, exponent2));
          } else if (arg1.negate().equals(inverse)) {
            return F.Negate(F.Power(inverse, F.Subtract(F.C1, exponent2)));
          }
        }
      }

      if (arg1.isRational() && !exponent2.isNumber()) {
        return timesPowerPower(arg1, F.C1, base2, exponent2);
      }
      return F.NIL;
    }

    /**
     * Evaluate <code>&lt;rational-arg1&gt; * base2 ^ exponent2</code>
     *
     * @param rationalArg1
     * @param base2
     * @param exponent2
     * @return
     */
    private IExpr timesRationalPower(final IRational rationalArg1, IExpr base2, IExpr exponent2) {
      EvalEngine engine = EvalEngine.get();
      if (engine.isTogetherMode()) {
        return F.NIL;
      }
      if (exponent2.isNegative()) {
        IExpr temp = timesPowerPower(rationalArg1.numerator(), rationalArg1.denominator(), F.C1, //
            ((IRational) base2).denominator(), ((IRational) base2).numerator(), exponent2.negate(),
            false);
        if (temp.isPresent()) {
          return temp;
        }
      } else {
        IExpr temp = timesPowerPower(rationalArg1.numerator(), rationalArg1.denominator(), F.C1, //
            ((IRational) base2).numerator(), ((IRational) base2).denominator(), exponent2, false);
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }

  }


  /**
   *
   *
   * <pre>
   * TimesBy(x, dx)
   *
   * x *= dx
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is equivalent to <code>x = x * dx</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; a = 10
   * &gt;&gt; a *= 2
   * 20
   *
   * &gt;&gt; a
   * 20
   * </pre>
   */
  private static class TimesBy extends AddTo {

    @Override
    protected IASTMutable getAST(final IExpr value) {
      return F.Times(null, value);
    }

    @Override
    protected ISymbol getFunctionSymbol() {
      return S.TimesBy;
    }

    @Override
    protected ISymbol getArithmeticSymbol() {
      return S.Times;
    }

  }

  /**
   * Try simpplifying <code>(power0Arg1 ^ power0Arg2) * (power1Arg1 ^ power1Arg2)</code>
   *
   * @param power0Arg1
   * @param power0Arg2
   * @param power1Arg1
   * @param power1Arg2
   * @return
   */
  private static IExpr timesPowerPower(IExpr power0Arg1, IExpr power0Arg2, IExpr power1Arg1,
      IExpr power1Arg2) {
    if (power0Arg2.isNumber()) {
      if (power1Arg2.isNumber()) {
        if (power0Arg1.equals(power1Arg1)) {
          // x^(a)*x^(b) => x ^(a+b)
          return F.Power(power0Arg1, power0Arg2.plus(power1Arg2));
        }
        if (power0Arg2.equals(power1Arg2)) {
          if ((power0Arg1.isRealResult() && power1Arg1.isRealResult())) {
            IExpr timesBase = EvalEngine.get().evaluate(F.Times(power0Arg1, power1Arg1));
            if (!timesBase.isTimes() || !power0Arg2.isInteger()) {
              // a^(c)*b^(c) => (a*b) ^c
              return F.Power(timesBase, power0Arg2);
            }
          }
        }
        if (power0Arg2.negate().equals(power1Arg2) && power0Arg1.isPositive()
            && power1Arg1.isPositive() && power0Arg1.isReal() && power1Arg1.isReal()) {
          // a^(c)*b^(-c) => (a/b)^c
          if (power0Arg2.isNegative()) {
            return F.Power(power1Arg1.divide(power0Arg1), power1Arg2);
          } else {
            return F.Power(power0Arg1.divide(power1Arg1), power0Arg2);
          }
        }
      }
    }
    if (power0Arg1.isRational() && power1Arg1.isRational()) {
      IExpr temp = timesPowerPower(((IRational) power0Arg1).numerator(),
          ((IRational) power0Arg1).denominator(), power0Arg2, //
          ((IRational) power1Arg1).numerator(), ((IRational) power1Arg1).denominator(), power1Arg2,
          false);
      if (temp.isPresent()) {
        return temp;
      }
    }
    // if (power0Arg1.isPlus() && power1Arg1.isPlus() &&
    // power0Arg1.equals(power1Arg1.negate())) {// Issue#128
    // return
    // power0Arg1.power(power0Arg2.plus(power1Arg2)).times(CN1.power(power1Arg2));
    // }

    if (power0Arg1.equals(power1Arg1)) {
      // x^(a)*x^(b) => x ^(a+b)
      return F.Power(power0Arg1, power0Arg2.plus(power1Arg2));
    }
    return F.NIL;
  }

  /**
   * (p1Numer/p1Denom)^(p1Exp) * (p2Numer1/p2Denom1)^(p2Exp)
   *
   * @return {@link F#NIL} if evaluation wasn't possible
   */
  private static IExpr timesPowerPower(IInteger p1Numer, IInteger p1Denom, IExpr p1Exp,
      IInteger p2Numer, IInteger p2Denom, IExpr p2Exp, boolean setEvaled) {
    boolean[] evaled = new boolean[] {false};

    OpenIntToIExprHashMap<IExpr> fn1Map = new OpenIntToIExprHashMap<IExpr>();
    IInteger fn1Rest = Primality.countPrimes1021(p1Numer, p1Exp, fn1Map, setEvaled, evaled);
    IInteger fd2Rest =
        Primality.countPrimes1021(p2Denom, p2Exp.negate(), fn1Map, setEvaled, evaled);

    OpenIntToIExprHashMap<IExpr> fn2Map = new OpenIntToIExprHashMap<IExpr>();
    IInteger fn2Rest = Primality.countPrimes1021(p2Numer, p2Exp, fn2Map, setEvaled, evaled);
    IInteger fd1Rest =
        Primality.countPrimes1021(p1Denom, p1Exp.negate(), fn2Map, setEvaled, evaled);
    if (!evaled[0] && fn2Map.size() > 0) {
      OpenIntToIExprHashMap<IExpr>.Iterator iter = fn2Map.iterator();
      while (iter.hasNext()) {
        iter.advance();
        int base = iter.key();
        IExpr exp1 = fn1Map.get(base);
        if (exp1 != null) {
          if (exp1.isAST()) {
            evaled[0] = true;
            break;
          }
          IExpr exp2 = fn2Map.get(base);
          if (exp2.isAST()) {
            evaled[0] = true;
            break;
          }
          if ((exp1.isInteger() && exp2.isInteger())) {
            evaled[0] = true;
            break;
          }
        }
      }
    }
    if (evaled[0]) {
      if (fn2Map.size() > 0) {
        OpenIntToIExprHashMap<IExpr>.Iterator iter = fn2Map.iterator();
        while (iter.hasNext()) {
          iter.advance();
          int base = iter.key();
          IExpr exponent = iter.value();
          IExpr exp = fn1Map.get(base);
          if (exp == null) {
            fn1Map.put(base, exponent);
          } else {
            fn1Map.put(base, exp.add(exponent));
          }
        }
      }
      IASTAppendable times1 = F.TimesAlloc(fn1Map.size() + 4);
      if (!fn1Rest.isOne()) {
        times1.append(F.Power(fn1Rest, p1Exp));
      }
      if (!fd2Rest.isOne()) {
        times1.append(F.Power(fd2Rest, p2Exp.negate()));
      }
      if (!fn2Rest.isOne()) {
        times1.append(F.Power(fn2Rest, p2Exp));
      }
      if (!fd1Rest.isOne()) {
        times1.append(F.Power(fd1Rest, p1Exp.negate()));
      }
      if (fn1Map.size() > 0) {
        OpenIntToIExprHashMap<IExpr>.Iterator iter = fn1Map.iterator();
        while (iter.hasNext()) {
          iter.advance();
          int base = iter.key();
          IExpr exponent = iter.value();
          if (base != 1) {
            times1.append(F.Power(F.ZZ(base), F.evalExpand(exponent)));
          }
        }
      }
      return times1;
    }

    return F.NIL;
  }

  /**
   * Gives <code>min</code> for <code>x&lt;min</code> and <code>max</code> for
   * <code>x&gt;max</code>.
   * 
   * @param x
   * @param min
   * @param max
   * @return
   */
  public static IExpr clip(IExpr x, ISignedNumber min, ISignedNumber max) {
    return clip(x, min, max, min, max);
  }

  /**
   * Gives <code>vMin</code> for <code>x&lt;min</code> and <code>vMax</code> for
   * <code>x&gt;max</code>.
   *
   * @param x the expression value
   * @param min minimum value
   * @param max maximum value
   * @param vMin value for x less than minimum
   * @param vMax value for x greater than minimum
   * @return x if x is in the range min to max. Return vMin if x is less than min.Return vMax if x
   *         is greater than max.
   */
  public static IExpr clip(IExpr x, ISignedNumber min, ISignedNumber max, IExpr vMin, IExpr vMax) {
    if (x.isSparseArray()) {
      x = x.normal(false);
    }
    if (x.isList()) {
      IAST list = (IAST) x;
      IAST result = list.map(a -> {
        IExpr temp = clip(a, min, max, vMin, vMax);
        if (temp.isPresent()) {
          return temp;
        }
        ArgumentTypeStopException.throwNIL();
        return F.NIL;
      });
      return result;
    }
    if (x.isReal()) {
      ISignedNumber real = (ISignedNumber) x;
      if (real.isGT(max)) {
        return vMax;
      }
      if (real.isLT(min)) {
        return vMin;
      }
      return x;
    }
    ISignedNumber real = x.evalReal();
    if (real != null) {
      if (real.isGT(max)) {
        return vMax;
      }
      if (real.isLT(min)) {
        return vMin;
      }
      return x;
    }
    if (x.isInfinity() && S.Greater.ofQ(x, max)) {
      return vMax;
    }
    if (x.isNegativeInfinity() && S.Less.ofQ(x, min)) {
      return vMin;
    }
    return F.NIL;
  }

  /**
   * Rewrite the function as <code>Piecewise()</code> function if possible. Rewrite functions:
   * <code>Abs, Clip, If, Ramp, UnitStep</code>
   *
   * @param function
   * @param domain if set to <code>F.Reals</code> a function like <code>Abs(x)</code> can be
   *        rewritten.
   * @return {@link F#NIL} if evaluation wasn't possible
   */
  public static IAST piecewiseExpand(final IAST function, IBuiltInSymbol domain) {
    EvalEngine engine = EvalEngine.get();
    if (function.isPlus()) {
      int piecewisePosition = function.indexOf(x -> x.isPiecewise() != null);
      if (piecewisePosition > 0) {
        IAST piecewise = (IAST) function.get(piecewisePosition);
        int[] piecewiseDimension = piecewise.isPiecewise();
        if (piecewiseDimension[0] > 0 && piecewiseDimension[1] == 2) {
          IAST piecewiseList = (IAST) piecewise.arg1();
          IExpr rest = function.removeAtCopy(piecewisePosition).oneIdentity0();
          IASTAppendable result = F.mapList(piecewiseList, subList -> {
            return F.list(F.Plus(rest, subList.first()), subList.second());
          });
          if (piecewise.argSize() == 2) {
            return F.Piecewise(result, S.Plus.of(engine, rest, piecewise.arg2()));
          }
          return F.Piecewise(result);
        }
      }
      return F.NIL;
    }
    if (function.isTimes()) {
      int piecewisePosition = function.indexOf(x -> x.isPiecewise() != null);
      if (piecewisePosition > 0) {
        IAST piecewise = (IAST) function.get(piecewisePosition);
        int[] piecewiseDimension = piecewise.isPiecewise();
        if (piecewiseDimension[0] > 0 && piecewiseDimension[1] == 2) {
          IAST piecewiseList = (IAST) piecewise.arg1();
          IExpr rest = function.removeAtCopy(piecewisePosition).oneIdentity1();
          IASTAppendable result = F.mapList(piecewiseList, subList -> {
            return F.list(F.Times(rest, subList.first()), subList.second());
          });
          if (piecewise.argSize() == 2) {
            return F.Piecewise(result, S.Times.of(engine, rest, piecewise.arg2()));
          }
          return F.Piecewise(result);
        }
      }
      return F.NIL;
    }
    if (function.argSize() == 1) {
      IExpr x = function.arg1();
      if ((domain.equals(S.Reals) || function.arg1().isRealResult())) {
        if (function.isAST(S.Abs) || function.isAST(S.RealAbs)) {
          return F.Piecewise(F.list(F.list(F.Negate(x), F.Less(x, F.C0))), x);
        }
        if (function.isAST(S.Arg)) {
          return F.Piecewise(F.list(F.list(S.Pi, F.Less(x, F.C0))), F.C0);
        }
        if (function.isAST(S.Sign) || function.isAST(S.RealSign)) {
          return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.C0)), //
              F.list(F.C1, F.Greater(x, F.C0))), F.C0);
        }
      }
      if (function.isAST(S.Boole)) {
        return F.Piecewise(F.list(F.list(F.C1, x)), F.C0);
      }
      if (function.isAST(S.Unitize)) {
        return F.Piecewise(F.list(F.list(F.C1, F.Unequal(x, F.C0))), F.C0);
      }
    }
    if (function.isAST(S.BernsteinBasis, 4)) {
      IExpr d = function.arg1();
      IExpr n = function.arg2();
      IExpr x = function.arg3();
      return F
          .Piecewise(F.list(F.list(F.C1,
                  F.Or(F.And(F.Equal(d, F.C0), F.Equal(n, F.C0)),
                      F.And(F.GreaterEqual(d, F.C0), F.Equal(n, F.C0), F.Equal(x, F.C0)),
                      F.And(F.Greater(d, F.C0), F.Equal(x, F.C1),
                          F.Equal(F.Subtract(d, n), F.C0)))),
              F.list(
                  F.Times(F.Power(F.Subtract(F.C1, x), F.Subtract(d, n)), F.Power(x, n),
                      F.Binomial(d, n)),
                  F.And(F.Greater(d, F.C0), F.GreaterEqual(n, F.C0),
                      F.GreaterEqual(F.Subtract(d, n), F.C0), F.Less(F.C0, x, F.C1)))),
              F.C0);
    }
    if (function.isAST(S.RealAbs, 2)) {
      return F.Piecewise(F.list(F.list(F.Negate(x), F.Less(x, F.C0))), x);
    } else if (function.isAST(S.RealSign, 2)) {
      return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.C0)), //
          F.list(F.C1, F.Greater(x, F.C0))), F.C0);
    }
    if (function.isAST(S.Clip, 2)) {
      IExpr x = function.arg1();
      return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.CN1)), F.list(F.C1, F.Greater(x, F.C1))),
          x);
    }
    if (function.isAST(S.Clip, 3) && function.second().isList2()) {
      IExpr x = function.arg1();
      IExpr low = function.second().first();
      IExpr high = function.second().second();
      return F.Piecewise(F.list(F.list(low, F.Less(x, low)), F.list(high, F.Greater(x, high))), x);
    }
    if (function.isAST(S.If, 3)) {
      IExpr a1 = function.arg1();
      IExpr a2 = function.arg2();
      return F.Piecewise(F.list(F.list(a2, a1), F.C0));
    }
    if (function.isAST(S.If, 4)) {
      IExpr a1 = function.arg1();
      IExpr a2 = function.arg2();
      IExpr a3 = function.arg3();
      return F.Piecewise(F.list(F.list(a2, a1)), a3);
    }
    if (function.isAST(S.Ramp, 2)) {
      IExpr x = function.arg1();
      return F.Piecewise(F.list(F.list(x, F.GreaterEqual(x, F.C0))), F.C0);
    }
    if (function.isAST(S.Unitize, 3)) {
      IExpr x = function.arg1();
      IExpr dx = function.arg2();
      return F.Piecewise(F.list(F.list(F.C1, F.LessEqual(F.Subtract(dx, F.Abs(x)), F.C0))), F.C0);
    }
    if (function.isAST(S.UnitStep) && function.size() > 1) {
      // Piecewise({{1, x >= 0 && y >= 0 && z >= 0}}, 0)
      final int size = function.size();
      IASTAppendable andAST = F.mapFunction(S.And, function, t -> F.GreaterEqual(t, F.C0));
      return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
    }

    if (function.size() > 1) {
      if (function.isAST(S.DiscreteDelta)) {
        if (function.size() == 2) {
          return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
        }
        IASTAppendable andAST = F.mapFunction(S.And, function, t -> F.Equal(t, F.C0));
        return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
      }
      if (function.isAST(S.KroneckerDelta)) {
        if (function.size() == 2) {
          return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
        }
        IExpr[] last = new IExpr[] {function.arg1()};
        IASTAppendable andAST = F.mapFunction(S.And, function, 2, function.size(), t -> {
          final IExpr subtrahend = last[0];
          last[0] = t;
          return F.Equal(F.Subtract(subtrahend, t), F.C0);
        });
        return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
      }
    }
    return F.NIL;
  }

  /**
   * (p1Numer/p1Denom)^(p1Exp)
   *
   * @return {@link F#NIL} if evaluation wasn't possible
   */
  public static IExpr rationalPower(IInteger p1Numer, IInteger p1Denom, IRational p1Exp) {
    boolean[] evaled = new boolean[] {false};

    OpenIntToIExprHashMap<IExpr> fn1Map = new OpenIntToIExprHashMap<IExpr>();
    IInteger fn1Rest = Primality.countPrimes1021(p1Numer, p1Exp, fn1Map, true, evaled);
    IInteger fd1Rest = Primality.countPrimes1021(p1Denom, p1Exp.negate(), fn1Map, true, evaled);

    if (evaled[0]) {
      IASTAppendable times1 = F.TimesAlloc(fn1Map.size() + 4);
      if (!fn1Rest.isOne()) {
        times1.append(F.Power(fn1Rest, p1Exp));
      }
      if (!fd1Rest.isOne()) {
        times1.append(F.Power(fd1Rest, p1Exp.negate()));
      }
      OpenIntToIExprHashMap<IExpr>.Iterator iter = fn1Map.iterator();
      while (iter.hasNext()) {
        iter.advance();
        int base = iter.key();
        IExpr exponent = iter.value();
        if (base != 1) {
          times1.append(F.Power(F.ZZ(base), exponent));
        }
      }
      return times1;
    }

    return F.NIL;
  }

  /**
   * The Lanczos approximation is a method for computing the gamma function numerically.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Lanczos_approximation">Lanczos approximation</a>
   *
   * @param z
   * @return the gamma function value
   */
  public static org.hipparchus.complex.Complex lanczosApproxGamma(
      org.hipparchus.complex.Complex z) {
    if (z.getReal() < 0.5) {
      // Pi / ( Sin(Pi * z) * Gamma(1 - z) )
      return lanczosApproxGamma(z.negate().add(1.0)).multiply(z.multiply(Math.PI).sin())
          .reciprocal().multiply(Math.PI);
    } else {
      z = z.subtract(1.0);
      org.hipparchus.complex.Complex x = pComplex[0];
      for (int i = 1; i < g + 2; i++) {
        // x += p[i] / (z+i)
        x = x.add(pComplex[i].divide(z.add(i)));
      }
      org.hipparchus.complex.Complex t = z.add(g).add(0.5);
      // Sqrt(2 * Pi) * Pow(t, z + 0.5) * Exp(-t) * x
      return t.pow(z.add(0.5)).multiply(t.negate().exp()).multiply(x)
          .multiply(Math.sqrt(2 * Math.PI));
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private Arithmetic() {}

}
