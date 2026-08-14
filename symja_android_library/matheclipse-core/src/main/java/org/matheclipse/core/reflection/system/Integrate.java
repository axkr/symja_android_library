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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.apfloat.ApfloatInterruptedException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.PowerTimesFunction;
import org.matheclipse.core.integrate.ChebyshevIntegration;
import org.matheclipse.core.integrate.DerivativeDivides;
import org.matheclipse.core.integrate.IntegralTable;
import org.matheclipse.core.integrate.PrimitiveTowerIntegration;
import org.matheclipse.core.integrate.ProductPowerIntegration;
import org.matheclipse.core.integrate.RadicalCoefficients;
import org.matheclipse.core.integrate.RadicalSubstitution;
import org.matheclipse.core.integrate.RationalIntegration;
import org.matheclipse.core.integrate.RischNorman;
import org.matheclipse.core.integrate.SurdRationalization;
import org.matheclipse.core.integrate.TranscendentalRisch;
import org.matheclipse.core.integrate.WeierstrassIntegration;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.reflection.system.rules.IntegratePowerTimesFunctionRules;
import com.google.common.cache.CacheBuilder;
import edu.jas.kern.PreemptingException;

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
public class Integrate extends AbstractFunctionOptionEvaluator {


  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

  /**
   * Define rules for functions of the form <code>Integrate(x^n * unaryFunction(m*x), x)</code>.
   */
  private static Matcher POWER_TIMES_FUNCTION_MATCHER;

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
   * Causes the current thread to wait until the initializer thread (see {@link #setUp(ISymbol)})
   * has loaded the Integrate() rules.
   */
  @Override
  public final void await() throws InterruptedException {
    COUNT_DOWN_LATCH.await();
  }

  public static class IntegrateInitializer implements Runnable {
    @Override
    public void run() {
      // compareAndSet: exactly one thread performs the rule loading, even when several threads
      // call evaluate() concurrently (the Config.JAS_NO_THREADS path runs this inline)
      if (INTEGRATE_RULES_READ.compareAndSet(false, true)) {
        try {
          initializeRules();
        } finally {
          // always release the waiting evaluation threads - a latch stranded by an exception
          // during rule loading would block every further Integrate evaluation forever
          COUNT_DOWN_LATCH.countDown();
        }
      }
    }

    private static void initializeRules() {
      final EvalEngine engine = EvalEngine.get();
      ContextPath path = engine.getContextPath();
      try {
        engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);

        UtilityFunctionCtors.getUtilityFunctionsRuleASTRubi45();
        getRuleASTStatic();

        ISymbol[] rubiSymbols = {S.Derivative, S.D};
        for (int i = 0; i < rubiSymbols.length; i++) {
          INT_RUBI_FUNCTIONS.add(rubiSymbols[i]);
        }
      } finally {
        engine.setContextPath(path);
      }
      engine.setPackageMode(false);

      F.ISet(F.$s("§simplifyflag"), S.False);

      F.ISet(F.$s("§$timelimit"), F.ZZ(Config.INTEGRATE_RUBI_TIMELIMIT));
      F.ISet(F.$s("§$showsteps"), S.False);
      UtilityFunctionCtors.ReapList.setAttributes(ISymbol.HOLDFIRST);
      F.ISet(F.$s("§$trigfunctions"), F.List(S.Sin, S.Cos, S.Tan, S.Cot, S.Sec, S.Csc));
      F.ISet(F.$s("§$hyperbolicfunctions"), F.List(S.Sinh, S.Cosh, S.Tanh, S.Coth, S.Sech, S.Csch));
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
          F.Function(F.And(F.SameQ(F.Head(F.Slot1), S.Power), F.IntegerQ(F.Part(F.Slot1, F.C2)))));

      F.ISet(UtilityFunctionCtors.FractionalPowerQ, //
          F.Function(F.And(F.SameQ(F.Head(F.Slot1), S.Power),
              F.SameQ(F.Head(F.Part(F.Slot1, F.C2)), S.Rational))));

      POWER_TIMES_FUNCTION_MATCHER = initPowerTimesFunction();
    }

    private static void getRuleASTStatic() {
      INTEGRATE_RULES_DATA = S.Integrate.createRulesData(new int[] {0, 7000});
      UtilityFunctionCtors.getRuleASTRubi45();
    }
  }

  public static RulesData INTEGRATE_RULES_DATA;

  /** Constructor for the singleton */
  public static final Integrate CONST = new Integrate();

  public static final Set<ISymbol> INT_RUBI_FUNCTIONS = new HashSet<ISymbol>();

  public static final AtomicBoolean INTEGRATE_RULES_READ = new AtomicBoolean(false);

  public Integrate() {}

  /** Nesting depth of {@link #evaluate}: 1 is the integral the user asked for. */
  private static final ThreadLocal<Integer> EVAL_DEPTH = ThreadLocal.withInitial(() -> 0);

  /** Lazily created daemon scheduler for the Rubi time budget. */
  private static ScheduledExecutorService WATCHDOG_SCHEDULER = null;

  @Override
  public IExpr evaluate(IAST holdallAST, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    final int depth = EVAL_DEPTH.get();
    EVAL_DEPTH.set(depth + 1);
    try {
      IExpr result = evaluateIntegrate(holdallAST, argSize, option, engine, originalAST);
      if (depth > 0 || result.isNIL()) {
        return result;
      }
      // Only for the integral the user asked for, and only once it is complete: the rules split an
      // integral by linearity and hand the same transcendental back with coefficients written over
      // different radical extensions, which Plus cannot add. Collect them and denest the sums.
      IExpr x = holdallAST.arg2().isList() ? holdallAST.arg2().first() : holdallAST.arg2();
      return RadicalCoefficients.collect(result, x, engine).orElse(result);
    } finally {
      if (depth == 0) {
        // don't leave a boxed 0 behind in pooled threads
        EVAL_DEPTH.remove();
      } else {
        EVAL_DEPTH.set(depth);
      }
    }
  }

  private IExpr evaluateIntegrate(IAST holdallAST, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
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

    final IAssumptions oldAssumptions = engine.getAssumptions();
    final boolean oldNumericMode = engine.isNumericMode();
    try {
      IExpr assumptionOption = option[0];
      IExpr assumptionExpr = OptionArgs.determineAssumptions(assumptionOption);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        IAssumptions assumptions =
            org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        if (assumptions != null) {
          engine.setAssumptions(assumptions);
        }
      }
      final String forcedMethod = parseIntegrateMethod(option);

      boolean evaled = false;
      IExpr result;
      if (argSize < 2) {
        return F.NIL;
      }
      if (engine.isNumericMode()) {
        IExpr arg2 = engine.evaluate(holdallAST.arg2());
        if (arg2.isList3()) {
          IAST xList = (IAST) arg2;
          IASTAppendable copy = holdallAST.apply(S.NIntegrate);
          copy.set(2, xList);
          IExpr temp = engine.evaluate(copy);
          if (temp.isFreeAST(S.NIntegrate)) {
            return temp;
          }
          // NIntegrate left the integral unevaluated (and already reported why); the limits
          // themselves are a valid {x,a,b} list here, so an "ilim" message would be misleading.
          return F.NIL;
        }
        return F.NIL;
      }
      engine.setNumericMode(false);

      final IExpr arg1Holdall = holdallAST.arg1();
      final IExpr a1 = AbstractFractionSym.rationalize(arg1Holdall, false).orElse(arg1Holdall);
      IExpr arg1 = engine.evaluateNIL(a1);
      if (arg1.isPresent()) {
        evaled = true;
      } else {
        arg1 = a1;
      }
      if (arg1.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (argSize > 2) {
        // reduce arguments by folding Integrate[fxy, x, y] to
        // Integrate[Integrate[fxy, y], x] ...
        return holdallAST.foldRight((x, y) -> engine.evaluateNIL(F.Integrate(x, y)), arg1, 2);
      }

      holdallAST = holdallAST.copyUntil(argSize + 1);
      IExpr arg2 = engine.evaluateNIL(holdallAST.arg2());
      if (arg2.isPresent()) {
        evaled = true;
      } else {
        arg2 = holdallAST.arg2();
      }
      if (arg2.isList()) {
        IAST xList = (IAST) arg2;
        if (xList.isList3()) {
          // Integrate(Derivative(n)[f][x], {x,a,b}) for an unknown function f stays unevaluated,
          // the fundamental theorem of calculus value f(b)-f(a) needs f' to be integrable on [a,b],
          // which cannot be assumed for an arbitrary f. (The indefinite
          // Integrate(Derivative(n)[f][x], x) still evaluates to the antiderivative.) Sin'(x) and
          // other known derivatives resolve to Cos(x) etc. before reaching here, so they are
          // unaffected.
          IAST[] derivative = arg1.isDerivative();
          if (derivative != null && derivative[2] != null && derivative[2].isAST1()
              && derivative[2].first().equals(xList.arg1()) //
              && derivative[1].isAST1() && derivative[1].first().isSymbol()) {
            return F.NIL;
          }
          // Integrate(c, {x,a,b}) for an infinite constant c is c*(b-a). The generic route cannot
          // do this because the antiderivative c*x is not "specials free". Only a finite width
          // gives a determinate value: a degenerate range is Infinity*0 == Indeterminate, while an
          // unbounded one is left unevaluated rather than asserting Infinity*Infinity.
          if (arg1.isDirectedInfinity() && arg1.isFree(xList.arg1(), true)) {
            IExpr width = engine.evaluate(F.Subtract(xList.arg3(), xList.arg2()));
            if (width.isReal()) {
              return engine.evaluate(F.Times(arg1, width));
            }
          }
          // Integrate(f(x)*DiracDelta(c1*x+c0), {x,a,b}) - the sifting property. Handled before the
          // generic antiderivative route, which would evaluate HeavisideTheta at both limits and
          // report a root sitting on the lower limit as 1-HeavisideTheta(0) instead of
          // HeavisideTheta(0).
          IExpr sifted =
              integrateDiracDelta(arg1, xList.arg1(), xList.arg2(), xList.arg3(), engine);
          if (sifted.isPresent()) {
            return sifted;
          }
          // Integrate(f(x), {x,a,b})
          IAST copy = holdallAST.setAtCopy(2, xList.arg1());
          IExpr temp = engine.evaluate(copy);
          if (temp.isFreeAST(h -> h == S.Integrate || h == S.Boole) //
              && temp.isSpecialsFree()) {
            return definiteIntegral(temp, xList, holdallAST, engine);
          }
          return integrateBooleTimesFxRegion(arg1, xList, false, engine);
        }
        // Invalid integration variable or limit(s) in `1`.
        return Errors.printMessage(S.Integrate, "ilim", F.List(arg2), engine);
      }
      if (arg1.isList() && arg2.isVariable()) {
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
        final ASTSeriesData temp = series.integrate(x);
        if (temp != null) {
          return temp;
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
      if (arg1.isAST()) {
        final IAST fx = (IAST) arg1;
        if (fx.topHead().equals(x)) {
          // issue #91
          return F.NIL;
        }
        if (forcedMethod != null) {
          // Integrate[f, x, Method -> "..."] forces a single native stage, bypassing the Automatic
          // cascade and the Rubi rules (used mainly by the per-method test suites). A stage that
          // does
          // not apply returns F.NIL, leaving the integral unevaluated.
          return integrateBySingleMethod(forcedMethod, fx, x, engine);
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

        if (fx.argSize() > 0 || fx.isBuiltInFunction()) {
          IExpr temp = POWER_TIMES_FUNCTION.xPowNTimesFmx(fx, x, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        IExpr temp = integrateTimesPower(fx, x);
        if (temp.isPresent()) {
          return temp;
        }
        // ====================================================================
        // Exponential Integral Engine: Integrates b * e^(kx) / x^m forms natively
        // ====================================================================
        IExpr tempExp = integrateExpIntegral(fx, x, engine);
        if (tempExp.isPresent()) {
          return tempExp;
        }
        if (Config.INTEGRATE_ALGORITHMS) {
          // Fast, mostly correct-by-construction algorithm cascade, tried before the Rubi rules.
          // Each
          // stage self-gates on a Config.INTEGRATE_ALGORITHM_* kill-switch (default on). A ported
          // stage takes part in this Automatic cascade only when it is *also* wired in here; stages
          // that are unit-tested but not yet trusted to change production output forms are left
          // un-wired (still reachable via the Method -> option and their direct tests).

          // Stage: native rational function integration
          // (Hermite/Horowitz-Ostrogradsky reduction + Lazard-Rioboo-Trager logarithmic part).
          // RootSumMode.DEFER: when the antiderivative is essentially a bare RootSum, defer the
          // integrand to the Rubi rules (which often have a far simpler closed form) and re-emit
          // the RootSum only as a post-Rubi fallback (see below). Closed-form results, including a
          // mixed Log(..)+RootSum(..), are still produced here.
          result = quietStage(engine, () -> RationalIntegration.integrate(fx, x, engine,
              RationalIntegration.RootSumMode.DEFER));
          if (result.isPresent()) {
            return result;
          }
          // Stage: substitution t = (a+b*x)^(1/n) for radicals of a linear function
          result = quietStage(engine, () -> RadicalSubstitution.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
          // Stage: Chebyshev binomial differentials x^m (a+b*x^n)^p (correct-by-construction).
          result = quietStage(engine, () -> ChebyshevIntegration.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
          // Stage: product of >= 2 polynomial powers with a compatible polynomial cofactor,
          // Integrate(S*prod(P_i^m_i)) = poly*prod(P_i^(m_i+1)) via a perfect-derivative ansatz.
          // Rubi leaves these unevaluated (its linearity split over the cofactor S produces
          // unresolved pieces), and its own 40s grind on them would otherwise exhaust the test
          // timeout, so this runs before the rules. Restricted to >= 2 power factors to avoid the
          // single-power integrals Rubi already renders canonically. Diff-back self-verified.
          result = quietStage(engine, () -> ProductPowerIntegration.integrate(fx, x, engine, 2));
          if (result.isPresent()) {
            return result;
          }
        }
        if (Config.INTEGRATE_ALGORITHMS) {
          // Primitive (Log) monomial with x mixed into a denominator of degree >= 2 in it: the
          // rules have nothing for this shape and grind until the deadline, while partial
          // fractions in the monomial plus the logarithmic-derivative test settle it. The stage
          // gates on that shape itself and diff-back verifies.
          result = quietStage(engine, () -> PrimitiveTowerIntegration.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
        }
        if (Config.INTEGRATE_ALGORITHMS && DerivativeDivides.hasExponentialTower(fx, x)) {
          // An exponential tower like E^(1-x*E^(x^2)) has no Rubi rule, and the rules grind on it
          // until the evaluation deadline - so the derivative-divides heuristic, which does solve
          // these by substituting the inner function, runs before them instead of after. Only for
          // this shape, so no integral the rules can render more canonically is intercepted; the
          // stage diff-back verifies its result as always.
          result = quietStage(engine, () -> DerivativeDivides.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
        }
        result = integrateByRubiRulesWithBudget(fx, x, ast, engine);
        if (result.isPresent()) {
          IExpr rubiResult = F.subst(result, f -> {
            if (f.isAST(UtilityFunctionCtors.Unintegrable, 3)) {
              return F.Integrate(f.first(), f.second());
            } else if (f.isAST(F.$rubi("CannotIntegrate"), 3)) {
              return F.Integrate(f.first(), f.second());
            }
            return F.NIL;
          });
          // Rubi reports "no rule for this integrand" as Unintegrable[] or CannotIntegrate[], which
          // the substitution above maps back to the unevaluated Integrate(). That isn't an
          // antiderivative, so it must not end the cascade here: the native fallback stages below
          // can still solve the integral, for example with a RootSum() antiderivative.
          if (!rubiResult.equals(ast)) {
            return rubiResult;
          }
        }

        result = callRestIntegrate(fx, x, engine);
        if (result.isPresent()) {
          return result;
        }

        // Post-Rubi heuristic fallbacks: broad stages that would otherwise intercept simple
        // integrals
        // Rubi renders in a more canonical form, so they run only for integrands Rubi leaves
        // unevaluated. Each self-verifies (D(result) == integrand). The deterministic, form-safe
        // stages (rational, radical, Chebyshev) run before Rubi (above).
        if (Config.INTEGRATE_ALGORITHMS) {
          // RootSum fallback for rational functions whose denominator has an irreducible factor of
          // degree >= 5, deferred from the pre-Rubi rational stage (RootSumMode.DEFER above). Runs
          // only now that Rubi left the integral unevaluated, so Rubi's simpler closed form (when
          // it
          // has one) always wins. Correct-by-construction (Trager), reuses the full general logic.
          result = quietStage(engine, () -> RationalIntegration.integrate(fx, x, engine,
              RationalIntegration.RootSumMode.EMIT));
          if (result.isPresent()) {
            return result;
          }
          // Conjugate rationalization of a denominator containing a single square root, e.g.
          // x^2/(x^2+Sqrt(1-x^2)) -> x^2*(x^2-Sqrt(1-x^2))/(x^4+x^2-1). Post-Rubi because it only
          // rewrites the integrand and re-enters Integrate: whenever Rubi has an answer for the
          // original form, that (more canonical) form wins.
          result = quietStage(engine, () -> SurdRationalization.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
          // Weierstrass t=Tan(x/2) substitution for rational trigonometric integrands.
          // result = WeierstrassIntegration.integrate(fx, x, engine);
          // if (result.isPresent()) {
          // return result;
          // }
          // Derivative-divides (Geddes) u-substitution heuristic.
          result = quietStage(engine, () -> DerivativeDivides.integrate(fx, x, engine));
          if (result.isPresent()) {
            return result;
          }
          // Risch-Norman ("parallel Risch" / pmint) heuristic for transcendental integrands.
          // result = RischNorman.integrate(fx, x, engine);
          // if (result.isPresent()) {
          // return result;
          // }
          // Transcendental Risch (RDE / differential-tower based) recogniser.
          // result = TranscendentalRisch.integrate(fx, x, engine);
          // if (result.isPresent()) {
          // return result;
          // }
        }

      }
      return evaled ? ast : F.NIL;
    } finally {
      engine.setAssumptions(oldAssumptions);
      engine.setNumericMode(oldNumericMode);
    }
  }

  /**
   * Runs one stage of the native integration cascade with messages suppressed.
   *
   * <p>
   * The cascade is a speculative search: a stage rewrites the integrand, evaluates the rewritten
   * form - often by re-entering {@link S#Integrate} - and returns {@link F#NIL} when that leads
   * nowhere, in which case the next stage or the Rubi rules take over. Messages emitted while such
   * an attempt is being explored describe an intermediate expression the caller never sees, so
   * printing them only produces noise.
   *
   * <p>
   * For example {@code Probability(x>1, Distributed(x, ExponentialDistribution(2/3)))} integrates
   * {@code Boole(x>1)*Piecewise({{2/(3*E^(2*x/3)),x>=0}},0)}. The derivative-divides stage
   * substitutes a dummy for the exponential and re-enters {@code Integrate}, whose Rubi utility
   * function {@code TrigSimplifyRecur} maps itself over the arguments of the {@code Piecewise} -
   * briefly making its first argument a function call instead of a list of pairs. That printed
   * about a dozen "not a list of pairs" messages per call, while the returned probability was
   * correct all along.
   *
   * <p>
   * <b>Note:</b> this deliberately hides genuine messages from these stages too. That is the right
   * trade for an attempt whose result is either discarded or verified by differentiating it back,
   * but it means a stage must never rely on a message to report a real problem. An explicitly
   * requested stage ({@code Integrate[f, x, Method -> "..."]}) is not run through here and stays
   * verbose.
   *
   * @param engine the evaluation engine, whose quiet mode is restored before returning
   * @param stage the stage to run
   * @see EvalEngine#withQuietMode(Supplier)
   */
  private static IExpr quietStage(final EvalEngine engine, final Supplier<IExpr> stage) {
    return engine.withQuietMode(stage);
  }

  /**
   * Parse the {@code Method} option (option[1]) of {@code Integrate[f, x, Method -> "..."]} into a
   * canonical native-stage name, or {@code null} for {@code Automatic} (i.e. the normal cascade).
   */
  private static String parseIntegrateMethod(final IExpr[] option) {
    if (option.length < 2) {
      return null;
    }
    final IExpr methodOption = option[1];
    if (methodOption == null || methodOption == S.Automatic || methodOption.isList()) {
      return null;
    }
    String name = methodOption.toString();
    if (name.length() > 1 && name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"') {
      name = name.substring(1, name.length() - 1);
    }
    return name;
  }

  /**
   * Force a single native integration stage selected by the {@code Method} option value. Used by
   * the {@code Integrate[f, x, Method -> "..."]} form and the per-method test suites. Returns the
   * stage's antiderivative, or {@link F#NIL} if the named stage does not apply or is unknown (the
   * caller then leaves the integral unevaluated rather than falling through to the Rubi rules).
   *
   * @param method canonical method name (never {@code "Automatic"})
   */
  private static IExpr integrateBySingleMethod(String method, IAST fx, IExpr x, EvalEngine engine) {
    switch (method) {
      case "Rational":
      case "BronsteinRational":
        return RationalIntegration.integrate(fx, x, engine);
      case "Table":
      case "CRCTable":
        return IntegralTable.integrate(fx, x, engine);
      case "RadicalSubstitution":
      case "LinearRadicals":
        return RadicalSubstitution.integrate(fx, x, engine);
      case "Chebyshev":
      case "Chebychev":
        return ChebyshevIntegration.integrate(fx, x, engine);
      case "DerivativeDivides":
        return DerivativeDivides.integrate(fx, x, engine);
      case "RischNorman":
        return RischNorman.integrate(fx, x, engine);
      case "Weierstrass":
      case "Jeffrey":
        return WeierstrassIntegration.integrate(fx, x, engine);
      case "RischTranscendental":
        return TranscendentalRisch.integrate(fx, x, engine);
      case "PrimitiveTower":
      case "RischPrimitive":
        return PrimitiveTowerIntegration.integrate(fx, x, engine);
      case "SurdRationalization":
      case "ConjugateRationalization":
        return SurdRationalization.integrate(fx, x, engine);
      case "ProductPower":
      case "ProductOfPowers":
        return ProductPowerIntegration.integrate(fx, x, engine);
      default:
        // Unknown or not-yet-ported method name: leave the integral unevaluated.
        return F.NIL;
    }
  }

  // ====================================================================================
  // Natively integrates b * e^(kx) / x^m terms into ExpIntegralEi functions via recurrence
  // ====================================================================================
  private static IExpr integrateExpIntegral(IAST function, final IExpr xVar, EvalEngine engine) {
    IASTAppendable bTimes = F.TimesAlloc();
    IExpr n = F.C0;
    IExpr k = F.C0;

    IAST factors = function.isTimes() ? function : F.Times(function);

    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr arg = factors.get(i);
      if (arg.isFree(xVar)) {
        bTimes.append(arg);
      } else if (arg.equals(xVar)) {
        n = engine.evaluate(F.Plus(n, F.C1));
      } else if (arg.isPower() && arg.first().equals(xVar) && arg.second().isFree(xVar)) {
        n = engine.evaluate(F.Plus(n, arg.second()));
      } else if (arg.isExp()) {
        IExpr expArg = arg.second();
        IExpr kCoeff = engine.evaluate(F.Coefficient(expArg, xVar));
        IExpr rem = engine.evaluate(F.ExpandAll(F.Subtract(expArg, F.Times(kCoeff, xVar))));
        if (rem.isZero()) {
          k = engine.evaluate(F.Plus(k, kCoeff));
        } else if (rem.isFree(xVar)) {
          k = engine.evaluate(F.Plus(k, kCoeff));
          bTimes.append(F.Power(S.E, rem));
        } else {
          return F.NIL;
        }
      } else {
        return F.NIL;
      }
    }

    int nInt = n.toMachineInt();
    if (F.isNotPresent(nInt) || nInt >= 0) {
      return F.NIL;
    }
    if (k.isZero()) {
      return F.NIL;
    }

    IExpr b = bTimes.argSize() == 0 ? F.C1
        : (bTimes.argSize() == 1 ? bTimes.arg1() : engine.evaluate(bTimes));
    int m = -nInt;

    if (m == 1) {
      return engine.evaluate(F.Times(b, F.ExpIntegralEi(F.Times(k, xVar))));
    } else {
      // Loop to apply integration by parts:
      // Integral(E^(kx)/x^m) = -E^(kx)/((m-1) x^(m-1)) + k/(m-1) Integral(E^(kx)/x^(m-1))
      IASTAppendable plus = F.PlusAlloc();
      IExpr currentCoeff = b;

      for (int i = m; i > 1; i--) {
        IExpr termCoeff = engine.evaluate(F.Divide(currentCoeff, F.ZZ(1 - i)));
        IExpr term = F.Times(termCoeff, F.Exp(F.Times(k, xVar)), F.Power(xVar, F.ZZ(1 - i)));
        plus.append(term);

        currentCoeff = engine.evaluate(F.Divide(F.Times(currentCoeff, k), F.ZZ(i - 1)));
      }

      plus.append(F.Times(currentCoeff, F.ExpIntegralEi(F.Times(k, xVar))));
      return engine.evaluate(plus);
    }
  }

  /**
   * Integrates the given <code>function</code>, by analyzing, if its a multiplication with a
   * {@link S#Boole}. Example: <code>Integrate(Boole(condition)*f(x), {x,-Infinity,Infinity})</code>
   * 
   * @param function the function to integrate which will be analyzed for {@link S#Boole} function
   * @param xList the integration variable and the limits of integration, e.g. <code>{x,
   *        -Infinity,Infinity}</code>
   * @param useNIntegrate use {@link S#NIntegrate} instead of {@link S#Integrate}
   * @param engine the evaluation engine
   * @return the integrated function or {@link F#NIL} if no {@link S#Boole} function was found
   */
  public static IExpr integrateBooleTimesFxRegion(IExpr function, IAST xList, boolean useNIntegrate,
      final EvalEngine engine) {
    if (function.isAST(S.Boole, 2)) {
      // 1 * Boole(condition)
      function = F.Times(F.C1, function);
    }
    if (function.isTimes() //
        && xList.arg2().isNegativeInfinity() && xList.arg3().isInfinity()) {
      int index = function.indexOf(b -> b.isAST(S.Boole, 2));
      if (index > 0) {
        // Integrate(Boole(condition)*f(x), {x,-Infinity,Infinity})
        IExpr condition = ((IAST) function).get(index).first();
        IExpr x = xList.arg1();
        IExpr interval = IntervalDataSym.toIntervalData(condition, x, engine, false);

        if (interval.isIntervalData()) {
          if (interval.argSize() == 0) {
            return F.C0;
          }
          IExpr fx = ((IAST) function).removeAtCopy(index).oneIdentity1();
          IAST intervalData = (IAST) interval;
          IASTAppendable result = F.PlusAlloc(intervalData.argSize());
          for (int i = 1; i < intervalData.size(); i++) {
            IExpr arg = intervalData.get(i);
            if (!arg.isList4()) {
              return F.NIL;
            }
            IAST intervalList = (IAST) arg;
            final IExpr integratedInterval;
            if (useNIntegrate) {
              integratedInterval = engine
                  .evaluate(F.NIntegrate(fx, F.List(x, intervalList.arg1(), intervalList.arg4()),
                      F.Rule(S.Method, "LegendreGauss")));
            } else {
              integratedInterval = engine
                  .evaluate(F.Integrate(fx, F.List(x, intervalList.arg1(), intervalList.arg4())));
            }
            if (integratedInterval.isSpecialsFree()) {
              result.append(integratedInterval);
            } else {
              return F.NIL;
            }
          }
          return result;
        }
      }
    }
    return F.NIL;
  }

  private static IExpr integrateTimesPower(final IAST function, final IExpr x) {
    if (function.isTimes()) {
      IAST[] temp = function.filter(arg -> arg.isFree(x));
      IExpr free = temp[0].oneIdentity1();
      if (!free.isOne()) {
        IExpr rest = temp[1].oneIdentity1();
        // Integrate(free_ * rest_,x_) -> free*Integrate(rest, x) /; FreeQ(free,x)
        return Times(free, Integrate(rest, x));
      }
    }
    if (function.isPower()) {
      // base ^ exponent
      IExpr base = function.base();
      IExpr exponent = function.exponent();
      if (base.equals(x) && exponent.isFree(x)) {
        if (exponent.isMinusOne()) {
          // Integrate[ 1 / x_ , x_] -> Log[x]
          return Log(x);
        }
        // Integrate[ x_ ^n_ , x_ ] -> x^(n+1)/(n+1) /; FreeQ[n, x]
        IExpr temp = Plus(F.C1, exponent);
        return Divide(Power(x, temp), temp);
      }
      if (exponent.equals(x) && base.isFree(x)) {
        if (base.isE()) {
          // E^x
          return function;
        }
        // a^x / Log(a)
        return F.Divide(function, F.Log(base));
      }
    }
    return F.NIL;
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
        return POWER_TIMES_FUNCTION_MATCHER.apply(appendableList);
      }
      return POWER_TIMES_FUNCTION_MATCHER.apply(list);
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
   * Integrate forms of <code>Abs()</code> or <code>Abs()^n</code> with <code>n</code> integer.
   *
   * <p>
   * Every returned antiderivative is real and continuous through the roots of the
   * <code>Abs()</code> argument (the integrand is locally integrable there), so the results stay
   * valid for definite integration by the fundamental theorem of calculus.
   *
   * @param function the integrand
   * @param x the integration variable, assumed to be an element of the reals
   * @return the antiderivative, {@link S#Undefined} as a marker that ends the integration cascade
   *         (the integral is then left unevaluated), or {@link F#NIL} if this method does not apply
   */
  private static IExpr integrateAbs(IAST function, final IExpr x) {
    if (function.isAST1() && function.first().equals(x)) {
      IExpr head = function.head();
      if (head.equals(S.RealAbs)) {
        return F.Times(F.C1D2, x, F.RealAbs(x));
      } else if (head.equals(S.RealSign)) {
        return F.RealAbs(x);
      }
    }

    if (x.isRealResult()) {
      if (function.isAbs()) {
        // Abs(l0 + l1 * x^exp)
        IExpr[] lin = function.arg1().linearPower(x);
        if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()
            && lin[2].isInteger()) {
          IExpr l0 = lin[0];
          IExpr l1 = lin[1];
          IInteger exp = (IInteger) lin[2];
          if (exp.isOne()) {
            // u*Abs(u)/(2*l1) with u = l0+l1*x is the antiderivative of Abs(u) which is
            // continuous through the root of u
            IExpr u = F.Plus(l0, F.Times(l1, x));
            return F.Divide(F.Times(u, F.Abs(u)), F.Times(F.C2, l1));
          }
          if (exp.isEven()) {
            // x^exp >= 0 everywhere (x != 0 for negative exp), so l0 + l1*x^exp is sign-definite
            // exactly when l0 and l1 have the same sign - only then may Abs() be dropped
            IInteger expP1 = exp.inc();
            IExpr dropped =
                F.Plus(F.Times(l0, x), F.Times(F.Power(expP1, F.CN1), l1, F.Power(x, expP1)));
            if (l0.isNonNegativeResult() && l1.isNonNegativeResult()) {
              return dropped;
            }
            if (l0.isNonPositiveResult() && l1.isNonPositiveResult()) {
              return F.Negate(dropped);
            }
          } else if (exp.isMinusOne()) {
            IExpr temp = integrateAbsLinearOverX(l0, l1, x);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      } else if (function.isPower() && function.base().isAbs() && function.exponent().isInteger()) {
        // Abs(l0 + l1 * x) ^ exp
        IAST abs = (IAST) function.base();
        IExpr[] lin = abs.arg1().linear(x);
        if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()) {
          IExpr l0 = lin[0];
          IExpr l1 = lin[1];
          IInteger exp = (IInteger) function.exponent();
          IInteger expP1 = exp.inc();
          IExpr u = F.Plus(l0, F.Times(l1, x));
          if (exp.isMinusOne()) {
            // Sign(u)*Log(Abs(u))/l1 - the root of u is a non-integrable singularity, the
            // antiderivative is valid on each side of it
            return F.Divide(F.Times(F.Sign(u), F.Log(F.Abs(u))), l1);
          }
          if (exp.isEven()) {
            // Abs(u)^exp == u^exp for even integer exponents
            return F.Divide(F.Power(u, expP1), F.Times(expP1, l1));
          }
          // odd exp != -1: u^exp*Abs(u)/((exp+1)*l1) == Abs(u)^exp*u/((exp+1)*l1), continuous
          // through the root of u for positive exp
          return F.Divide(F.Times(F.Power(u, exp), F.Abs(u)), F.Times(expP1, l1));
        }
      }
    }
    if (function.isAbs() || (function.isPower() && function.base().isAbs())) {
      return S.Undefined;
    }
    return F.NIL;
  }

  /**
   * Antiderivative of <code>Abs(l0 + l1/x)</code> for real <code>x</code>, currently only when
   * <code>l0</code> and <code>l1</code> are both positive or both negative (otherwise
   * {@link F#NIL}).
   *
   * <p>
   * With <code>p0 = Abs(l0), p1 = Abs(l1)</code> the integrand equals <code>Abs(p0 + p1/x)</code>,
   * which changes sign at its root <code>r = -p1/p0 &lt; 0</code> and has a non-integrable pole at
   * <code>x = 0</code>. With the primitive <code>P(x) = p0*x + p1*Log(Abs(x))</code> of
   * <code>p0 + p1/x</code>, the antiderivative is <code>P</code> outside of <code>(r, 0)</code> and
   * <code>2*P(r) - P</code> inside - real, and continuous at <code>r</code>.
   */
  private static IExpr integrateAbsLinearOverX(IExpr l0, IExpr l1, IExpr x) {
    final IExpr p0, p1;
    if (l0.isPositiveResult() && l1.isPositiveResult()) {
      p0 = l0;
      p1 = l1;
    } else if (l0.isNegativeResult() && l1.isNegativeResult()) {
      p0 = l0.negate();
      p1 = l1.negate();
    } else {
      return F.NIL;
    }
    IExpr root = F.Divide(F.Negate(p1), p0);
    IExpr primitive = F.Plus(F.Times(p0, x), F.Times(p1, F.Log(F.Abs(x))));
    // 2*P(r) with P(r) = -p1 + p1*Log(p1/p0)
    IExpr constant = F.Times(F.C2, p1, F.Plus(F.CN1, F.Log(F.Divide(p1, p0))));
    return F.Piecewise(F.list(F.list( //
        F.Subtract(constant, primitive), //
        F.And(F.Less(root, x), F.LessEqual(x, F.C0)))), //
        primitive);
  }

  /**
   * Recursively collects potential branch points by finding arguments of Log() and Power()
   * (fractional) that depend on the integration variable x.
   *
   * @param expr The expression to scan.
   * @param x The integration variable.
   */
  private static IAST collectBranchPoints(IExpr expr, IExpr x, EvalEngine engine) {
    if (expr.isFree(x, true)) {
      return F.NIL;
    }
    if (expr.isAST()) {
      IExpr result = engine.evaluate(F.binaryAST2(S.FunctionSingularities, expr, x)).makeList();
      if (result.isList()) {
        return (IAST) result;
      }
    }
    return F.NIL;
  }

  /**
   * Sifting property of {@link S#DiracDelta} for a definite integral
   * <code>Integrate(f(x)*DiracDelta(c1*x+c0), {x, lower, upper})</code>.
   *
   * <p>
   * With <code>r = -c0/c1</code> the single root of the delta argument, the integral is
   * <code>f(r)/Abs(c1)</code> if <code>r</code> lies strictly inside the integration range,
   * <code>0</code> if it lies outside, and <code>f(r)/Abs(c1)*HeavisideTheta(0)</code> if it
   * coincides with one of the limits - only "half" of the spike is then covered and
   * <code>HeavisideTheta(0)</code> is deliberately left unevaluated. Over the whole real line the
   * spike is always covered, but a symbolic root must additionally be real, which is reported as a
   * {@link S#ConditionalExpression}.
   *
   * <p>
   * See: <a href="https://en.wikipedia.org/wiki/Dirac_delta_function">Wikipedia - Dirac delta
   * function</a>
   *
   * @param integrand the (already evaluated) integrand
   * @param x the integration variable
   * @param lower the lower limit of integration
   * @param upper the upper limit of integration
   * @param engine the evaluation engine
   * @return the value of the definite integral or {@link F#NIL} if this shape does not apply
   */
  private static IExpr integrateDiracDelta(IExpr integrand, IExpr x, IExpr lower, IExpr upper,
      EvalEngine engine) {
    if (integrand.isFreeAST(S.DiracDelta) || !x.isVariable()) {
      return F.NIL;
    }
    IAST factors = integrand.isTimes() ? (IAST) integrand : F.Times(integrand);
    IExpr deltaArgument = F.NIL;
    IASTAppendable cofactor = F.TimesAlloc(factors.size());
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (deltaArgument.isNIL() && factor.isAST(S.DiracDelta, 2)) {
        deltaArgument = factor.first();
      } else {
        cofactor.append(factor);
      }
    }
    // a second delta in the same variable would be a product of distributions - not defined here
    if (deltaArgument.isNIL() || !cofactor.isFreeAST(S.DiracDelta)) {
      return F.NIL;
    }
    // the delta argument must be linear in x; DiracDelta itself already normalises Abs(c1) to 1
    if (!engine.evaluate(F.PolynomialQ(deltaArgument, x)).isTrue()
        || engine.evaluate(F.Exponent(deltaArgument, x)).toIntDefault(-1) != 1) {
      return F.NIL;
    }
    IExpr slope = engine.evaluate(F.Coefficient(deltaArgument, x, F.C1));
    if (slope.isZero()) {
      return F.NIL;
    }
    IExpr constant = engine.evaluate(F.Coefficient(deltaArgument, x, F.C0));
    IExpr root = engine.evaluate(F.Divide(constant.negate(), slope));
    IExpr value = engine.evaluate(F.Divide(F.ReplaceAll(cofactor, F.Rule(x, root)), F.Abs(slope)));
    if (!value.isFree(x, true)) {
      return F.NIL;
    }
    // reversed limits would need a sign flip; leave those to the generic route
    if (!engine.evaluate(F.Less(lower, upper)).isTrue()) {
      return F.NIL;
    }
    if (lower.isNegativeInfinity() && upper.isInfinity()) {
      return root.isRealResult() ? value : F.ConditionalExpression(value, F.Element(root, S.Reals));
    }
    if (engine.evaluate(F.Equal(root, lower)).isTrue()
        || engine.evaluate(F.Equal(root, upper)).isTrue()) {
      return engine.evaluate(F.Times(value, F.HeavisideTheta(F.C0)));
    }
    IExpr aboveLower = engine.evaluate(F.Less(lower, root));
    IExpr belowUpper = engine.evaluate(F.Less(root, upper));
    if (aboveLower.isTrue() && belowUpper.isTrue()) {
      return value;
    }
    if (aboveLower.isFalse() || belowUpper.isFalse()) {
      return F.C0;
    }
    return F.NIL;
  }

  /**
   * Given a continuous <code>function</code> of a real variable <code>x</code> and an interval
   * <code>[lower, upper]</code> of the real line, calculate the definite integral
   * <code>F(upper)-F(lower)</code>.
   *
   * <p>
   * See: <a href="https://en.wikipedia.org/wiki/Integral">Wikipedia - Integral</a>
   * <p>
   * <b>Note:</b>: the method does not strictly check whether the domain of integration is
   * continuous.
   * 
   * @param function a function of <code>x</code>
   * @param xValueList a list of the form <code>{x, lower, upper}</code> with <code>3</code>
   *        arguments
   * @param engine the evaluation engine
   * @return
   */
  private static IExpr definiteIntegral(IExpr function, IAST xValueList, IAST originalAST,
      EvalEngine engine) {
    IExpr x = xValueList.arg1();
    IExpr lower = xValueList.arg2();
    IExpr upper = xValueList.arg3();


    // Branch points / poles of the antiderivative (Log arguments, fractional or negative powers,
    // ...). A polynomial antiderivative has none - skip the FunctionSingularities/Solve round
    // trip, which would otherwise run for every simple definite integral.
    IAST potentialSingularityEquations =
        function.isPolynomial(F.list(x)) ? F.NIL : collectBranchPoints(function, x, engine);

    // Solve and Split
    if (potentialSingularityEquations.isPresent()) {
      IASTAppendable singularities = F.ListAlloc();
      // Solve eq for x
      for (IExpr eq : potentialSingularityEquations) {
        // Solve({eq, x >= lower, x <= upper}, x)
        IExpr solved = engine
            .evaluate(F.Solve(F.List(eq, F.GreaterEqual(x, lower), F.LessEqual(x, upper)), x));
        if (solved.isList()) {
          singularities.appendArgs((IAST) solved);
        }
      }

      if (!singularities.isEmpty()) {
        for (IExpr solution : singularities) {
          if (solution.isList()) {
            // Extract value from Rule: {{x->val}, ...}
            IExpr singularPoint = F.NIL;
            for (IExpr rule : (IAST) solution) {
              if (rule.isRule() && rule.first().equals(x)) {
                singularPoint = rule.second();
                break;
              }
            }

            if (singularPoint.isPresent()) {
              // Check if lower < singularPoint < upper
              if (engine
                  .evalTrue(F.And(F.Less(lower, singularPoint), F.Less(singularPoint, upper)))) {
                // Singularity/Branch point found strictly inside. Split.
                IExpr left = definiteIntegral(function, F.List(x, lower, singularPoint),
                    originalAST, engine);
                if (left.isNIL()) {
                  return F.NIL;
                }
                IExpr right = definiteIntegral(function, F.List(x, singularPoint, upper),
                    originalAST, engine);
                if (right.isNIL()) {
                  return F.NIL;
                }
                return F.Plus(left, right);
              }
            }
          }
        }
      }
    }


    // Standard Newton-Leibniz
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
    if (!lowerLimit.isSpecialsFree() || lowerLimit.isInterval() || lowerLimit.isIntervalData()) {
      // Integral of `1` does not converge on `2`.
      return Errors.printMessage(S.Integrate, "idiv",
          F.List(originalAST.arg1(), originalAST.arg2()), engine);
    }
    if (!lowerLimit.isFreeAST(S.Limit)) {
      // the limit stayed unevaluated - neither convergence nor divergence can be decided, so
      // don't assemble a result containing raw Limit() calls; leave the integral unevaluated
      return F.NIL;
    }
    IExpr upperLimit = engine.evaluate(F.Limit(function, F.Rule(x, upper), upperDirection));
    if (!upperLimit.isSpecialsFree() || upperLimit.isInterval() || upperLimit.isIntervalData()) {
      // Integral of `1` does not converge on `2`.
      return Errors.printMessage(S.Integrate, "idiv",
          F.List(originalAST.arg1(), originalAST.arg2()), engine);
    }
    if (!upperLimit.isFreeAST(S.Limit)) {
      return F.NIL;
    }


    if (upperLimit.isAST() && lowerLimit.isAST()) {
      IExpr bDenominator = engine.evaluate(F.Denominator(upperLimit));
      IExpr aDenominator = engine.evaluate(F.Denominator(lowerLimit));
      if (bDenominator.equals(aDenominator)) {
        return F.Divide(F.Subtract(F.Numerator(upperLimit), F.Numerator(lowerLimit)), bDenominator);
      }
    }
    IExpr difference = engine.evaluate(F.Subtract(upperLimit, lowerLimit));
    // distribute constant factors over sums
    // 2*(2-2*Log(2)+Log(2)^2) -> 4-4*Log(2)+2*Log(2)^2
    for (int i = 0; i < 3; i++) {
      IExpr expanded = F.expand(difference, false, true, true);
      if (expanded.equals(difference)) {
        break;
      }
      difference = engine.evaluate(expanded);
    }
    return difference;
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
      }

      if (arg1AST.size() >= 3 && arg1AST.isFree(S.Integrate) && arg1AST.isPlusTimesPower()) {
        if (!arg1AST.isEvalFlagOn(IAST.IS_DECOMPOSED_PARTIAL_FRACTION) && x.isSymbol()) {
          Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, true);
          if (parts.isPresent()) {
            IExpr temp = AlgebraUtil.partsApart(parts.get(), x, engine);
            if (temp.isPresent() && !temp.equals(arg1) && temp.isPlus()) {
              return mapIntegrate((IAST) temp, x);
            }
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
   * @param x the integration variable
   * @return
   */
  private static IExpr mapIntegrate(IAST ast, final IExpr x) {
    return ast.mapThread(F.Integrate(F.Slot1, x), 1);
  }


  /**
   * Run the Rubi rules under a time budget, see {@link Config#INTEGRATE_RUBI_TIMELIMIT_MILLIS}.
   *
   * <p>
   * Without it a set of rules that cannot finish an integral keeps grinding until the caller's
   * deadline, and the native stages behind them - which may well have an answer - never run at all
   * ({@code Integrate(1/(1+x^5),x)} and the exponential towers are the examples). The budget turns
   * "aborted after 60s" into "the rules gave up after their share, the other stages tried too".
   *
   * <p>
   * The engine enforces time limits through the interrupt flag of the evaluating thread (its
   * evaluation loop throws {@link TimeoutException} when it sees one), so a watchdog that
   * interrupts <em>this</em> thread reuses exactly that mechanism and nothing has to move to
   * another thread - which matters because {@link EvalEngine} is thread-local. Only an interrupt
   * this method raised itself is swallowed; one from the outside stays an abort.
   */
  private static IExpr integrateByRubiRulesWithBudget(IAST arg1, IExpr x, IAST ast,
      EvalEngine engine) {
    long budgetMillis = rubiBudgetMillis(engine);
    // one watchdog per user-level integral: nested Integrate calls are covered by the outer one
    if (budgetMillis <= 0 || Config.JAS_NO_THREADS || EVAL_DEPTH.get() != 1) {
      return integrateByRubiRules(arg1, x, ast, engine);
    }
    final Thread evaluationThread = Thread.currentThread();
    final boolean[] finished = new boolean[] {false};
    final boolean[] budgetExceeded = new boolean[] {false};
    final Object lock = new Object();
    ScheduledFuture<?> watchdog = watchdogScheduler().schedule(() -> {
      synchronized (lock) {
        if (!finished[0]) {
          budgetExceeded[0] = true;
          evaluationThread.interrupt();
        }
      }
    }, budgetMillis, TimeUnit.MILLISECONDS);
    try {
      return integrateByRubiRules(arg1, x, ast, engine);
    } catch (RuntimeException rex) {
      synchronized (lock) {
        if (!budgetExceeded[0]) {
          throw rex; // not our interrupt - the caller's deadline or a real failure
        }
      }
      return F.NIL;
    } finally {
      synchronized (lock) {
        finished[0] = true;
      }
      watchdog.cancel(false);
      if (budgetExceeded[0]) {
        // clear the flag we raised, or the stages after this one abort immediately
        Thread.interrupted();
      }
    }
  }

  /** The Rubi budget for this evaluation, or {@code <= 0} to run the rules unbounded. */
  private static long rubiBudgetMillis(EvalEngine engine) {
    long budgetMillis = Config.INTEGRATE_RUBI_TIMELIMIT_MILLIS;
    if (budgetMillis <= 0) {
      return 0;
    }
    double remainingSeconds = engine.getRemainingSeconds();
    if (remainingSeconds >= 0.0) {
      // leave the native stages a slice of whatever time the caller granted
      long share = (long) (remainingSeconds * 1000.0 * Config.INTEGRATE_RUBI_TIMELIMIT_SHARE);
      budgetMillis = Math.min(budgetMillis, share);
    }
    return budgetMillis;
  }

  private static synchronized ScheduledExecutorService watchdogScheduler() {
    if (WATCHDOG_SCHEDULER == null) {
      ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, runnable -> {
        Thread thread = Config.THREAD_FACTORY.newThread(runnable);
        thread.setDaemon(true);
        thread.setName("symja-integrate-watchdog");
        return thread;
      });
      scheduler.setRemoveOnCancelPolicy(true);
      WATCHDOG_SCHEDULER = scheduler;
    }
    return WATCHDOG_SCHEDULER;
  }

  private static IExpr integrateByRubiRules(IAST arg1, IExpr x, IAST ast, EvalEngine engine) {
    if (arg1.isFreeAST(s -> s.isSymbol() && ((ISymbol) s).isContext(Context.RUBI))) {
      int limit = engine.getRecursionLimit();
      boolean quietMode = engine.isQuietMode();
      if (arg1.isNumericFunctionAST() || INT_RUBI_FUNCTIONS.contains(arg1.topHead())
          || arg1.topHead().getSymbolName().startsWith("§")) {

        // Persistent per-engine LRU memo for Rubi results. Entry semantics:
        // - F.NIL: in-progress sentinel - the same integral is already being matched further up
        // the call stack (breaks rule-recursion cycles),
        // - the input `ast` itself: the rules matched nothing for this integral,
        // - anything else: the rule result.
        // Rubi answers depend on the active assumptions, so while assumptions are set the memo
        // is neither trusted nor written - only the cycle sentinel is used (and removed again in
        // the finally block below, because it is left as F.NIL on that path).
        final boolean assumptionsActive = engine.getAssumptions() != null;
        if (engine.rubiASTCache == null) {
          engine.rubiASTCache =
              CacheBuilder.newBuilder().maximumSize(Config.INTEGRATE_RUBI_CACHE_SIZE).build();
        }
        IExpr cached = engine.rubiASTCache.getIfPresent(ast);
        if (cached != null) {
          if (cached.isNIL()) {
            return callRestIntegrate(arg1, x, engine);
          }
          if (!assumptionsActive) {
            if (cached.equals(ast)) {
              return F.NIL;
            }
            return cached;
          }
        }
        try {
          try {
            engine.setQuietMode(true);
            if (limit <= 0 || limit > Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT) {
              engine.setRecursionLimit(Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT);
            }

            engine.rubiASTCache.put(ast, F.NIL);
            IExpr temp = S.Integrate.evalDownRule(engine, ast);
            if (temp.isPresent()) {
              if (temp.equals(ast)) {
                if (!assumptionsActive) {
                  engine.rubiASTCache.put(ast, ast);
                }
                return F.NIL;
              }
              if (!assumptionsActive && temp.isAST()) {
                engine.rubiASTCache.put(ast, temp);
              }
              return temp;
            }
            if (!assumptionsActive) {
              // remember that the rules have no match for this integral
              engine.rubiASTCache.put(ast, ast);
            }
          } catch (RecursionLimitExceeded rle) {
            engine.setRecursionLimit(limit);
            return F.NIL;
          } catch (ApfloatInterruptedException | PreemptingException | AbortException ex) {
            // a user Abort[] (or an engine abort) must terminate the whole evaluation instead
            // of being treated as "the rules failed, try the next integration stage"
            throw ex;
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            engine.setRecursionLimit(limit);
            return Errors.printMessage(S.Integrate, rex, engine);
          }

        } catch (final FailedException fe) {
          // Rubi utility functions use FailedException as internal control flow, treat as "no
          // result from the rules"
        } finally {
          engine.setRecursionLimit(limit);
          IExpr sentinel = engine.rubiASTCache.getIfPresent(ast);
          if (sentinel != null && sentinel.isNIL()) {
            // an exception/assumption path left the in-progress sentinel behind: this attempt
            // is retryable (budget, recursion limit, assumptions), don't memoize it as failure
            engine.rubiASTCache.invalidate(ast);
          }
          engine.setQuietMode(quietMode);
        }
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
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Assumptions, S.Method},
        new IExpr[] {S.$Assumptions, S.Automatic});
    super.setUp(newSymbol);

    if (!Config.JAS_NO_THREADS) {
      final Thread initThread;
      if (Config.THREAD_FACTORY != null) {
        initThread = Config.THREAD_FACTORY.newThread(new IntegrateInitializer());
      } else {
        initThread = new Thread(new IntegrateInitializer(), "IntegrateInitializer");
      }
      initThread.start();
    } else {
      // see #evaluate() method
    }
  }
}
