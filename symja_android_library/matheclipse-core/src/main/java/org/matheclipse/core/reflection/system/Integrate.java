package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.Integrate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.PowerTimesFunction;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.reflection.system.rules.IntegratePowerTimesFunctionRules;
import com.google.common.cache.CacheBuilder;

/**
 *
 *
 * <pre>
 * Integrate(f, x)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * integrates <code>f</code> with respect to <code>x</code>. The result does not contain the
 * additive integration constant.
 *
 * </blockquote>
 *
 * <pre>
 * Integrate(f, {x,a,b})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * computes the definite integral of <code>f</code> with respect to <code>x</code> from <code>a
 * </code> to <code>b</code>.
 *
 * </blockquote>
 *
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/Integral">Wikipedia: Integral</a>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Integrate(x^2, x)
 * x^3/3
 *
 * &gt;&gt; Integrate(Tan(x) ^ 5, x)
 * -Log(Cos(x))-Tan(x)^2/2+Tan(x)^4/4
 * </pre>
 */
public class Integrate extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();
  private static Thread INIT_THREAD = null;

  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

  /**
   * Define rules for functions of the form <code>Integrate(x^n * unaryFunction(m*x), x)</code>.
   */
  private static Matcher POWER_TIMES_FUNCION_MATCHER;

  private static Matcher initPowerTimesFunction() {
    Matcher MATCHER = new Matcher();
    IAST list = IntegratePowerTimesFunctionRules.RULES;

    for (int i = 1; i < list.size(); i++) {
      IExpr arg = list.get(i);
      if (arg.isAST(S.SetDelayed, 3)) {
        MATCHER.caseOf(arg.first(), arg.second());
      } else if (arg.isAST(S.Set, 3)) {
        MATCHER.caseOf(arg.first(), arg.second());
      }
    }
    return MATCHER;
  }

  /**
   * Try to integrate functions of the form <code>x^n * f(m*x)</code>.
   */
  private static final PowerTimesFunction POWER_TIMES_FUNCTION = new PowerTimesFunction(
      org.matheclipse.core.reflection.system.Integrate::integrateXPowNTimesFMTimesX);

  /**
   * Causes the current thread to wait until the INIT_THREAD has initialized the Integrate() rules.
   */
  @Override
  public final void await() throws InterruptedException {
    COUNT_DOWN_LATCH.await();
  }

  public static class IntegrateInitializer implements Runnable {

    @Override
    public void run() {
      if (!INTEGRATE_RULES_READ.get()) {
        INTEGRATE_RULES_READ.set(true);
        final EvalEngine engine = EvalEngine.get();
        ContextPath path = engine.getContextPath();
        try {
          engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);

          /* Kryo DEL start */
          UtilityFunctionCtors.getUtilityFunctionsRuleASTRubi45();
          getRuleASTStatic();
          /* Kryo DEL end */
          /* Kryo INS start */
          // Kryo kryo = KryoUtil.initKryo();
          // try (
          // InputStream resourceAsStream =
          // Integrate.getClass().getResourceAsStream("/rubi_context.bin");
          // Input input = new Input(resourceAsStream)) {
          // // kryo sets Context.RUBI internally
          // kryo.readClassAndObject(input);
          // } catch (IOException e) {
          // e.printStackTrace();
          // }
          // try (
          // InputStream resourceAsStream =
          // Integrate.getClass().getResourceAsStream("/integrate.bin");
          // Input input = new Input(resourceAsStream)) {
          // RulesData rulesData = (RulesData) kryo.readClassAndObject(input);
          // S.Integrate.setRulesData(rulesData);
          // } catch (IOException e) {
          // e.printStackTrace();
          // }
          // } catch (ClassNotFoundException cnfex) {
          // cnfex.printStackTrace();
          /* Kryo INS end */

        } finally {
          engine.setContextPath(path);
        }
        // F.Integrate.setEvaluator(CONST);
        engine.setPackageMode(false);

        F.ISet(F.$s("§simplifyflag"), S.False);

        F.ISet(F.$s("§$timelimit"), F.ZZ(Config.INTEGRATE_RUBI_TIMELIMIT));
        F.ISet(F.$s("§$showsteps"), S.False);
        UtilityFunctionCtors.ReapList.setAttributes(ISymbol.HOLDFIRST);
        F.ISet(F.$s("§$trigfunctions"), F.List(S.Sin, S.Cos, S.Tan, S.Cot, S.Sec, S.Csc));
        F.ISet(F.$s("§$hyperbolicfunctions"),
            F.List(S.Sinh, S.Cosh, S.Tanh, S.Coth, S.Sech, S.Csch));
        F.ISet(F.$s("§$inversetrigfunctions"),
            F.List(S.ArcSin, S.ArcCos, S.ArcTan, S.ArcCot, S.ArcSec, S.ArcCsc));
        F.ISet(F.$s("§$inversehyperbolicfunctions"),
            F.List(S.ArcSinh, S.ArcCosh, S.ArcTanh, S.ArcCoth, S.ArcSech, S.ArcCsch));
        F.ISet(F.$s("§$calculusfunctions"), F.List(S.D, S.Sum, S.Product, S.Integrate,
            F.$rubi("Unintegrable"), F.$rubi("CannotIntegrate"), F.$rubi("Dif"), F.$rubi("Subst")));
        F.ISet(F.$s("§$stopfunctions"), F.List(S.Hold, S.HoldForm, S.Defer, S.Pattern, S.If,
            S.Integrate, UtilityFunctionCtors.Unintegrable, F.$rubi("CannotIntegrate")));
        F.ISet(F.$s("§$heldfunctions"), F.List(S.Hold, S.HoldForm, S.Defer, S.Pattern));

        F.ISet(UtilityFunctionCtors.IntegerPowerQ, //
            F.Function(
                F.And(F.SameQ(F.Head(F.Slot1), S.Power), F.IntegerQ(F.Part(F.Slot1, F.C2)))));

        F.ISet(UtilityFunctionCtors.FractionalPowerQ, //
            F.Function(F.And(F.SameQ(F.Head(F.Slot1), S.Power),
                F.SameQ(F.Head(F.Part(F.Slot1, F.C2)), S.Rational))));

        POWER_TIMES_FUNCION_MATCHER = initPowerTimesFunction();

        COUNT_DOWN_LATCH.countDown();
      }
    }

    private static void getRuleASTStatic() {
      INTEGRATE_RULES_DATA = S.Integrate.createRulesData(new int[] {0, 7000});
      UtilityFunctionCtors.getRuleASTRubi45();

      ISymbol[] rubiSymbols = {S.Derivative, S.D};
      for (int i = 0; i < rubiSymbols.length; i++) {
        INT_RUBI_FUNCTIONS.add(rubiSymbols[i]);
      }
    }
  }

  public static RulesData INTEGRATE_RULES_DATA;
  /** Constructor for the singleton */
  public static final Integrate CONST = new Integrate();

  public static final Set<ISymbol> INT_RUBI_FUNCTIONS = new HashSet<ISymbol>();

  public static final Set<IExpr> DEBUG_EXPR = new HashSet<IExpr>(64);

  public static final AtomicBoolean INTEGRATE_RULES_READ = new AtomicBoolean(false);

  public Integrate() {}

  @Override
  public IExpr evaluate(IAST holdallAST, EvalEngine engine) {
    if (Config.JAS_NO_THREADS) {
      // Android changed: call static initializer in evaluate() method.
      new IntegrateInitializer().run();
    } else {
      // see #setUp() method
    }
    try {
      // wait for initializer run is completed, no matter how many threads call evaluate() method
      await();
    } catch (InterruptedException ignored) {
    }

    IAssumptions oldAssumptions = engine.getAssumptions();
    boolean numericMode = engine.isNumericMode();
    try {
      OptionArgs options = null;
      if (holdallAST.size() > 3) {
        options = new OptionArgs(S.Integrate, holdallAST, holdallAST.size() - 1, engine);
        if (!options.isInvalidPosition()) {
          holdallAST = holdallAST.most();
        }
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(holdallAST, -1, options);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        IAssumptions assumptions =
            org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        if (assumptions != null) {
          engine.setAssumptions(assumptions);
        }
      }

      boolean evaled = false;
      IExpr result;
      engine.setNumericMode(false);
      if (holdallAST.size() < 3 || holdallAST.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
        return F.NIL;
      }
      final IExpr arg1Holdall = holdallAST.arg1();
      final IExpr a1 = NumberTheory.rationalize(arg1Holdall, false).orElse(arg1Holdall);
      IExpr arg1 = engine.evaluateNIL(a1);
      if (arg1.isPresent()) {
        evaled = true;
      } else {
        arg1 = a1;
      }
      if (arg1.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (holdallAST.size() > 3) {
        // reduce arguments by folding Integrate[fxy, x, y] to
        // Integrate[Integrate[fxy, y], x] ...
        return holdallAST.foldRight((x, y) -> engine.evaluateNIL(F.Integrate(x, y)), arg1, 2);
      }

      IExpr arg2 = engine.evaluateNIL(holdallAST.arg2());
      if (arg2.isPresent()) {
        evaled = true;
      } else {
        arg2 = holdallAST.arg2();
      }
      if (arg2.isList()) {
        IAST xList = (IAST) arg2;
        if (xList.isVector() == 3) {
          // Integrate[f[x], {x,a,b}]
          IAST copy = holdallAST.setAtCopy(2, xList.arg1());
          IExpr temp = engine.evaluate(copy);
          if (temp.isFreeAST(S.Integrate)) {
            return definiteIntegral(temp, xList, engine);
          }
        }
        return F.NIL;
      }
      if (arg1.isList() && arg2.isSymbol()) {
        return mapIntegrate((IAST) arg1, arg2);
      }

      final IASTAppendable ast = holdallAST.setAtClone(1, arg1);
      ast.set(2, arg2);
      final IExpr x = ast.arg2();
      if (!x.isVariable()) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }
      if (arg1.isNumber()) {
        // Integrate[x_?NumberQ,y_Symbol] -> x*y
        return Times(arg1, x);
      }
      if (arg1 instanceof ASTSeriesData) {
        ASTSeriesData series = ((ASTSeriesData) arg1);
        if (series.getX().equals(x)) {
          final IExpr temp = ((ASTSeriesData) arg1).integrate(x);
          if (temp != null) {
            return temp;
          }
        }
        return F.NIL;
      }
      if (arg1.isFree(x, true)) {
        // Integrate[x_,y_Symbol] -> x*y /; FreeQ[x,y]
        return Times(arg1, x);
      }
      if (arg1.equals(x)) {
        // Integrate[x_,x_Symbol] -> x^2 / 2
        return Times(F.C1D2, Power(arg1, F.C2));
      }
      boolean showSteps = false;
      if (showSteps) {
        LOGGER.info(arg1);
        if (DEBUG_EXPR.contains(arg1)) {
          // System.exit(-1);
        }
        DEBUG_EXPR.add(arg1);
      }
      if (arg1.isAST()) {
        final IAST fx = (IAST) arg1;
        if (fx.topHead().equals(x)) {
          // issue #91
          return F.NIL;
        }
        int[] dim = fx.isPiecewise();
        if (dim != null) {
          return integratePiecewise(dim, fx, ast);
        }
        result = integrateAbs(fx, x);
        if (result.isPresent()) {
          if (result == S.Undefined) {
            return F.NIL;
          }
          return result;
        }

        if (fx.argSize() > 0 || fx.head().isBuiltInSymbol()) {
          IExpr temp = POWER_TIMES_FUNCTION.xPowNTimesFmx(fx, x, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        result = integrateByRubiRules(fx, x, ast, engine);
        if (result.isPresent()) {
          IExpr temp = result.replaceAll(f -> {
            if (f.isAST(UtilityFunctionCtors.Unintegrable, 3)) {
              IAST integrate = F.Integrate(f.first(), f.second());
              integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
              return integrate;
            } else if (f.isAST(F.$rubi("CannotIntegrate"), 3)) {
              IAST integrate = F.Integrate(f.first(), f.second());
              integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
              return integrate;
            }
            return F.NIL;
          });
          return temp.orElse(result);
        }

        if (fx.isTimes()) {
          IAST[] temp = fx.filter(arg -> arg.isFree(x));
          IExpr free = temp[0].oneIdentity1();
          if (!free.isOne()) {
            IExpr rest = temp[1].oneIdentity1();
            // Integrate[free_ * rest_,x_Symbol] -> free*Integrate[rest, x] /; FreeQ[free,x]
            return Times(free, Integrate(rest, x));
          }
        }
        if (fx.isPower()) {
          // base ^ exponent
          IExpr base = fx.base();
          IExpr exponent = fx.exponent();
          if (base.equals(x) && exponent.isFree(x)) {
            if (exponent.isMinusOne()) {
              // Integrate[ 1 / x_ , x_ ] -> Log[x]
              return Log(x);
            }
            // Integrate[ x_ ^n_ , x_ ] -> x^(n+1)/(n+1) /; FreeQ[n, x]
            IExpr temp = Plus(F.C1, exponent);
            return Divide(Power(x, temp), temp);
          }
          if (exponent.equals(x) && base.isFree(x)) {
            if (base.isE()) {
              // E^x
              return fx;
            }
            // a^x / Log(a)
            return F.Divide(fx, F.Log(base));
          }
        }

        result = callRestIntegrate(fx, x, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      return evaled ? ast : F.NIL;
    } finally {
      engine.setAssumptions(oldAssumptions);
      engine.setNumericMode(numericMode);
    }
  }

  /**
   * Try to integrate functions of the form <code>x^n * naryFunction(m*x)</code>.
   * 
   * @param naryFunction
   * @param x
   * @param n
   * @param m
   * @param p
   * 
   * @return {@link F#NIL} if no rule was found
   */
  private static IExpr integrateXPowNTimesFMTimesX(IAST naryFunction, final IExpr x, IExpr n,
      IExpr m, IExpr p) {
    int headID = naryFunction.headID();
    if (headID > ID.UNKNOWN) {
      final IAST list;
      if (p.isOne()) {
        if (n.isZero()) {
          list = F.f1(naryFunction.head(), x, m);
        } else {
          list = F.f2(naryFunction.head(), x, n, m);
        }
      } else {
        list = F.f3(naryFunction.head(), x, n, m, p);
      }
      if (naryFunction.argSize() > 1) {
        IASTAppendable appendableList = list.copyAppendable();
        appendableList.set(0, S.f4);
        appendableList.appendArgs(naryFunction.rest());
        return POWER_TIMES_FUNCION_MATCHER.apply(appendableList);
      }
      return POWER_TIMES_FUNCION_MATCHER.apply(list);
    }
    return F.NIL;
  }

  private static IExpr integratePiecewise(int[] dim, final IAST piecewiseFunction,
      final IAST integrateFunction) {
    IAST list = (IAST) piecewiseFunction.arg1();
    if (list.size() > 1) {
      IASTAppendable listOfPiecewiseIntegrateFunctions = F.mapList(list, t -> {
        IASTMutable integrate = integrateFunction.setAtCopy(1, t.first());
        return F.list(integrate, t.second());
      });
      IASTMutable piecewise = piecewiseFunction.setAtCopy(1, listOfPiecewiseIntegrateFunctions);
      if (piecewiseFunction.size() > 2) {
        IASTMutable integrate = integrateFunction.setAtCopy(1, piecewiseFunction.arg2());
        piecewise.set(2, integrate);
      }
      return piecewise;
    }
    return F.NIL;
  }

  /**
   * Integrate forms of <code>Abs()</code> or <code>Abs()^n</code> with <code>n^n</code> integer.
   *
   * @param function
   * @param x assumes to be an element of the Reals
   * @return
   */
  private static IExpr integrateAbs(IExpr function, final IExpr x) {
    IExpr constant = F.C0;
    if (function.isAST1() && function.first().equals(x)) {
      IAST f1 = (IAST) function;
      IExpr head = f1.head();
      if (head.equals(S.RealAbs)) {
        return F.Times(F.C1D2, x, F.RealAbs(x));
      } else if (head.equals(S.RealSign)) {
        return F.RealAbs(x);
      }
    }

    if (x.isRealResult()) {
      if (function.isAbs()) {
        // Abs(x)
        IAST abs = (IAST) function;
        IExpr[] lin = abs.arg1().linearPower(x);
        if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()
            && lin[2].isInteger()) {
          // Abs(l0 + l1 * x^exp)

          IExpr l0 = lin[0];
          IExpr l1 = lin[1];
          IInteger exp = (IInteger) lin[2];
          constant = F.Divide(F.Negate(l0), l1);
          if (exp.isOne()) {
            // Piecewise({{(-l0)*x - (l1*x^2)/2, x <= constant}}, l0^2/Pi + l0*x + (l1*x^2)/2)
            return F.Piecewise( //
                F.list(F.list( //
                    F.Plus(F.Times(F.CN1, l0, S.x), F.Times(F.CN1D2, l1, F.Sqr(S.x))),
                    F.LessEqual(S.x, constant))),
                F.Plus(F.Times(F.Sqr(l0), F.Power(S.Pi, F.CN1)), F.Times(l0, S.x),
                    F.Times(F.C1D2, l1, F.Sqr(S.x))));
          } else if (exp.isMinusOne()) {
            // Abs(l0 + l1 * x^(-1))

            if (!l0.isZero()) {
              // Piecewise({{l0*x + l1*Log(x), x <= -(l1/l0)},
              // {(-l0)*x - l1*(2 - I*l1 - Log(l1)) + l1*(-2 + I*l1 + Log(l1)) - l1*Log(x),
              // Inequality(-(l1/l0), Less, x, LessEqual, 0)}}, l0*x + l1*Log(x))
              return F.Piecewise(F.list(F.list( //
                  F.Plus(F.Times(l0, S.x), F.Times(l1, F.Log(S.x))),
                  F.LessEqual(S.x, F.Times(F.CN1, F.Power(l0, F.CN1), l1))),
                  F.list(
                      F.Plus(F.Times(F.CN1, l0, S.x),
                          F.Times(F.C2, l1, F.Plus(F.CN2, F.Times(F.CI, l1), F.Log(l1))),
                          F.Times(F.CN1, l1, F.Log(S.x))),
                      F.And(F.Less(F.Times(F.CN1, F.Power(l0, F.CN1), l1), S.x),
                          F.LessEqual(S.x, F.C0)))),
                  F.Plus(F.Times(l0, S.x), F.Times(l1, F.Log(S.x))));
            }
          } else if (exp.isPositive()) {
            IInteger expP1 = exp.inc();
            if (exp.isEven()) {
              // l0*x + (l1*x^(expP1))/(expP1)
              return F.Plus(F.Times(l0, S.x), F.Times(expP1.inverse(), l1, F.Power(S.x, expP1)));
            }
          } else if (exp.isNegative()) {
            IInteger expP1 = exp.inc();
            if (exp.isEven()) {
              // -(l1/(expP1*x^expP1)) + l0*x
              return F.Plus(F.Times(l0, S.x),
                  F.Times(F.CN1, F.Power(expP1, F.CN1), l1, F.Power(S.x, F.Negate(expP1))));
            }
          }
        }
      } else if (function.isPower() && function.base().isAbs() && function.exponent().isInteger()) {
        IAST power = (IAST) function;
        IAST abs = (IAST) power.base();

        IExpr[] lin = abs.arg1().linear(x);
        if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()) {
          // Abs(l0 + l1 * x) ^ exp
          IExpr l0 = lin[0];
          IExpr l1 = lin[1];
          constant = F.Divide(F.Negate(l0), l1);
          IInteger exp = (IInteger) power.exponent();
          IInteger expP1 = exp.inc();
          if (exp.isNegative()) {
            if (exp.isMinusOne()) {
              // Abs(l0 + l1 * x) ^ (-1)

              return F.Piecewise( //
                  F.list(F.list(F.Negate(F.Log(x)), F.LessEqual(x, constant))), //
                  F.Log(x));
            }
            if (exp.isEven()) {
              return F.Times(expP1.inverse().negate(), F.Power(x, expP1));
            }
            return F.Piecewise( //
                F.list(F.list(F.Times(expP1.inverse().negate(), F.Power(x, expP1)),
                    F.LessEqual(x, constant))), //
                F.Times(expP1.inverse(), F.Power(x, expP1)));
          }
          if (exp.isEven()) {
            return F.Divide(F.Power(x, expP1), expP1);
          }
          return F.Piecewise( //
              F.list(F.list(F.Divide(F.Power(x, expP1), expP1.negate()), F.LessEqual(x, constant))), //
              F.Divide(F.Power(x, expP1), expP1));
        }
      }
    }
    if (function.isAbs() || (function.isPower() && function.base().isAbs())) {
      return S.Undefined;
    }
    return F.NIL;
  }

  /**
   * Given a <code>function</code> of a real variable <code>x</code> and an interval <code>
   * [lower, upper]
   * </code> of the real line, calculate the definite integral <code>F(upper)-F(lower)</code>.
   *
   * <p>
   * See: <a href="https://en.wikipedia.org/wiki/Integral">Wikipedia - Integral</a>
   *
   * @param function a function of <code>x</code>
   * @param xValueList a list of the form <code>{x, lower, upper}</code> with <code>3</code>
   *        arguments
   * @param engine the evaluation engine
   * @return
   */
  private static IExpr definiteIntegral(IExpr function, IAST xValueList, EvalEngine engine) {
    // see Rubi rule for definite integrals
    IExpr x = xValueList.arg1();
    IExpr lower = xValueList.arg2();
    IExpr upper = xValueList.arg3();
    IExpr diff = engine.evaluate(F.Subtract(upper, lower));
    if (S.PossibleZeroQ.ofQ(engine, diff)) {
      return F.C0;
    }

    IExpr lowerDirection, upperDirection;
    if (diff.isNegativeResult()) {
      lowerDirection = F.Rule(F.Direction, F.C1);
      upperDirection = F.Rule(F.Direction, F.CN1);
    } else {
      lowerDirection = F.Rule(F.Direction, F.CN1);
      upperDirection = F.Rule(F.Direction, F.C1);
    }
    IExpr lowerLimit = engine.evaluate(F.Limit(function, F.Rule(x, lower), lowerDirection));
    if (!lowerLimit.isFree(S.DirectedInfinity, true) || !lowerLimit.isFree(S.Indeterminate, true)) {
      LOGGER.log(engine.getLogLevel(), "Not integrable: {} for limit {} -> {}", function, x, lower);
      return F.NIL;
    }
    IExpr upperLimit = engine.evaluate(F.Limit(function, F.Rule(x, upper), upperDirection));
    if (!upperLimit.isFree(S.DirectedInfinity, true) || !upperLimit.isFree(S.Indeterminate, true)) {
      LOGGER.log(engine.getLogLevel(), "Not integrable: {} for limit {} -> {}", function, x, upper);
      return F.NIL;
    }

    if (upperLimit.isAST() && lowerLimit.isAST()) {
      IExpr bDenominator = engine.evaluate(F.Denominator(upperLimit));
      IExpr aDenominator = engine.evaluate(F.Denominator(lowerLimit));
      if (bDenominator.equals(aDenominator)) {
        return F.Divide(F.Subtract(F.Numerator(upperLimit), F.Numerator(lowerLimit)), bDenominator);
      }
    }
    return F.Subtract(upperLimit, lowerLimit);
  }

  private static IExpr callRestIntegrate(IAST arg1, final IExpr x, final EvalEngine engine) {
    IExpr fxExpanded = F.expand(arg1, false, false, false);
    if (fxExpanded.isAST()) {
      if (fxExpanded.isPlus()) {
        return mapIntegrate((IAST) fxExpanded, x);
      }

      final IAST arg1AST = (IAST) fxExpanded;
      if (arg1AST.isTimes()) {
        // Integrate[a_*y_,x_Symbol] -> a*Integrate[y,x] /; FreeQ[a,x]
        IASTAppendable filterCollector = F.TimesAlloc(arg1AST.size());
        IASTAppendable restCollector = F.TimesAlloc(arg1AST.size());
        arg1AST.filter(filterCollector, restCollector, input -> input.isFree(x, true));
        if (filterCollector.size() > 1) {
          if (restCollector.size() > 1) {
            filterCollector.append(F.Integrate(restCollector.oneIdentity0(), x));
          }
          return filterCollector;
        }

        // IExpr temp = integrateTimesTrigFunctions(arg1AST, x);
        // if (temp.isPresent()) {
        // return temp;
        // }
      }

      if (arg1AST.size() >= 3 && arg1AST.isFree(S.Integrate) && arg1AST.isPlusTimesPower()) {
        if (!arg1AST.isEvalFlagOn(IAST.IS_DECOMPOSED_PARTIAL_FRACTION) && x.isSymbol()) {
          Optional<IExpr[]> parts = Algebra.fractionalParts(arg1, true);
          if (parts.isPresent()) {
            IExpr temp = Algebra.partsApart(parts.get(), x, engine);
            if (temp.isPresent() && !temp.equals(arg1)) {
              if (temp.isPlus()) {
                return mapIntegrate((IAST) temp, x);
              }
              // return F.Integrate(temp, x);
              // return mapIntegrate((IAST) temp, x);
            }
            // if (temp.isPlus()) {
            // return mapIntegrate((IAST) temp, x);
            // }
            // return Algebra.partialFractionDecompositionRational(new
            // PartialFractionIntegrateGenerator(x),parts, x);
          }
        }
      }
    }
    if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
      // https://github.com/RuleBasedIntegration/Rubi/issues/12
      return F.Integrate(F.TrigToExp(arg1).eval(engine), x)//
          .eval(engine);
    }
    return F.NIL;
  }

  /**
   * Map <code>Integrate</code> on <code>ast</code>. Examples:
   *
   * <ul>
   * <li><code>Integrate[{a_, b_,...},x_] -> {Integrate[a,x], Integrate[b,x], ...}</code> or
   * <li><code>Integrate[a_+b_+...,x_] -> Integrate[a,x]+Integrate[b,x]+...</code>
   * </ul>
   *
   * @param ast a <code>List(...)</code> or <code>Plus(...)</code> ast
   * @param x the integ ration veariable
   * @return
   */
  private static IExpr mapIntegrate(IAST ast, final IExpr x) {
    return ast.mapThread(F.Integrate(F.Slot1, x), 1);
  }

  /**
   * See <a href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia- Integration by
   * parts</a>
   *
   * @param ast TODO - not used
   * @param arg1
   * @param symbol
   * @return
   */
  private static IExpr integratePolynomialByParts(IAST ast, final IAST arg1, IExpr symbol,
      EvalEngine engine) {
    IASTAppendable fTimes = F.TimesAlloc(arg1.size());
    IASTAppendable gTimes = F.TimesAlloc(arg1.size());
    collectPolynomialTerms(arg1, symbol, gTimes, fTimes);
    IExpr g = gTimes.oneIdentity1();
    IExpr f = fTimes.oneIdentity1();
    // conflicts with Rubi 4.5 integration rules
    // only call integrateByParts for simple Times() expressions
    if (f.isOne() || g.isOne()) {
      return F.NIL;
    }
    return integrateByParts(f, g, symbol, engine);
  }

  /**
   * Use the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Symbolic Integration Rules</a> to
   * integrate the expression.
   *
   * @param ast
   * @return
   */
  private static IExpr integrateByRubiRules(IAST arg1, IExpr x, IAST ast, EvalEngine engine) {
    // EvalEngine engine = EvalEngine.get();
    if (arg1.isFreeAST(s -> s.isSymbol() && ((ISymbol) s).isContext(Context.RUBI))) {
      int limit = engine.getRecursionLimit();
      boolean quietMode = engine.isQuietMode();
      ISymbol head = arg1.topHead();

      if (head.isNumericFunctionAttribute() || INT_RUBI_FUNCTIONS.contains(head)
          || head.getSymbolName().startsWith("§")) {

        boolean newCache = false;
        try {

          if (engine.rubiASTCache != null) {
            IExpr result = engine.rubiASTCache.getIfPresent(ast);
            if (result != null) { // &&engine.getRecursionCounter()>0) {
              if (result.isPresent()) {
                return result;
              }
              return callRestIntegrate(arg1, x, engine);
            }
          } else {
            newCache = true;
            engine.rubiASTCache = CacheBuilder.newBuilder().maximumSize(50).build();
          }
          try {
            engine.setQuietMode(true);
            if (limit <= 0 || limit > Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT) {
              engine.setRecursionLimit(Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT);
            }

            engine.rubiASTCache.put(ast, F.NIL);
            IExpr temp = S.Integrate.evalDownRule(EvalEngine.get(), ast);
            if (temp.isPresent()) {
              if (temp.equals(ast)) {
                if (LOGGER.isDebugEnabled()) {
                  engine.setQuietMode(false);
                  Errors.printMessage(S.Integrate, "rubiendless", F.list(temp), engine);
                }
                return F.NIL;
              }
              if (temp.isAST()) {
                engine.rubiASTCache.put(ast, temp);
              }
              return temp;
            }
          } catch (RecursionLimitExceeded rle) {
            // engine.printMessage("Integrate(Rubi recursion): " +
            // Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT
            // + " exceeded: " + ast.toString());
            engine.setRecursionLimit(limit);
            LOGGER.log(engine.getLogLevel(), "Integrate(Rubi recursion)", rle);
            return F.NIL;
          } catch (RuntimeException rex) {
            engine.setRecursionLimit(limit);
            LOGGER.log(engine.getLogLevel(),
                "Integrate Rubi recursion limit {} RuntimeException: {}",
                Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT, ast, rex);
            return F.NIL;
          }

        } catch (AbortException ae) {
          LOGGER.debug("Integrate.integrateByRubiRules() aborted", ae);
        } catch (final FailedException fe) {
          LOGGER.debug("Integrate.integrateByRubiRules() failed", fe);
        } finally {
          engine.setRecursionLimit(limit);
          if (newCache) {
            engine.rubiASTCache = null;
          }
          engine.setQuietMode(quietMode);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Integrate by parts rule: <code>
   * Integrate(f'(x) * g(x), x) = f(x) * g(x) - Integrate(f(x) * g'(x),x )</code> . See
   * <a href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia- Integration by parts</a>
   *
   * @param f <code>f(x)</code>
   * @param g <code>g(x)</code>
   * @param x
   * @return <code>f(x) * g(x) - Integrate(f(x) * g'(x),x )</code>
   */
  private static IExpr integrateByParts(IExpr f, IExpr g, IExpr x, EvalEngine engine) {
    int limit = engine.getRecursionLimit();
    try {
      if (limit <= 0 || limit > Config.INTEGRATE_BY_PARTS_RECURSION_LIMIT) {
        engine.setRecursionLimit(Config.INTEGRATE_BY_PARTS_RECURSION_LIMIT);
      }
      IExpr firstIntegrate = engine.evaluate(F.Integrate(f, x));
      if (!firstIntegrate.isFreeAST(Integrate)) {
        return F.NIL;
      }
      IExpr gDerived = F.eval(F.D(g, x));
      IExpr second2Integrate = F.eval(F.Integrate(F.Times(gDerived, firstIntegrate), x));
      if (!second2Integrate.isFreeAST(Integrate)) {
        return F.NIL;
      }
      return F.eval(F.Subtract(F.Times(g, firstIntegrate), second2Integrate));
    } catch (RecursionLimitExceeded rle) {
      engine.setRecursionLimit(limit);
    } finally {
      engine.setRecursionLimit(limit);
    }
    return F.NIL;
  }

  /**
   * Collect all found polynomial terms into <code>polyTimes</code> and the rest into <code>
   * restTimes</code>.
   *
   * @param timesAST an AST representing a <code>Times[...]</code> expression.
   * @param symbol
   * @param polyTimes the polynomial terms part
   * @param restTimes the non-polynomil terms part
   */
  private static void collectPolynomialTerms(final IAST timesAST, IExpr symbol,
      IASTAppendable polyTimes, IASTAppendable restTimes) {
    IExpr temp;
    for (int i = 1; i < timesAST.size(); i++) {
      temp = timesAST.get(i);
      if (temp.isFree(symbol, true)) {
        polyTimes.append(temp);
        continue;
      } else if (temp.equals(symbol)) {
        polyTimes.append(temp);
        continue;
      } else if (temp.isPolynomial(F.list(symbol))) {
        polyTimes.append(temp);
        continue;
      }
      restTimes.append(temp);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    super.setUp(newSymbol);

    if (Config.THREAD_FACTORY != null) {
      INIT_THREAD = Config.THREAD_FACTORY.newThread(new IntegrateInitializer());
    } else {
      INIT_THREAD = new Thread(new IntegrateInitializer(), "IntegrateInitializer");
    }

    if (!Config.JAS_NO_THREADS) {
      INIT_THREAD.start();
    } else {
      // see #evaluate() method
    }
    setOptions(newSymbol, F.list(F.Rule(S.Assumptions, S.$Assumptions)));
  }
}
