package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.Integrate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.matheclipse.core.expression.KryoUtil;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.PowerTimesFunction;
import org.matheclipse.core.integrate.ChebyshevIntegration;
import org.matheclipse.core.integrate.DerivativeDivides;
import org.matheclipse.core.integrate.IntegralTable;
import org.matheclipse.core.integrate.ProductPowerIntegration;
import org.matheclipse.core.integrate.RadicalSubstitution;
import org.matheclipse.core.integrate.RationalIntegration;
import org.matheclipse.core.integrate.RischNorman;
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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
    /**
     * Attempts to deserialize the Rubi rules from a classpath resource. * @param resourcePath The
     * path to the resource (e.g., "/symja_rubi_rules.bin")
     * 
     * @return true if successful, false if the resource is missing, corrupted, or if the ID numbers
     *         have changed.
     */
    public static boolean deserializeRubiRulesFromResource(String resourcePath) {
      // Use the classloader to find the resource stream
      try (InputStream stream = Integrate.class.getResourceAsStream(resourcePath)) {
        if (stream == null) {
          System.out.println("Rubi rules resource not found at: " + resourcePath);
          return false;
        }

        try (Input input = new Input(stream)) {
          Kryo kryo = KryoUtil.initKryo();

          // 1. Verify the fingerprint
          String savedFingerprint = input.readString();
          if (!getSystemIdFingerprint().equals(savedFingerprint)) {
            System.out.println("Symja ID numbers changed. Packaged Rubi resource cache is stale.");
            return false;
          }

          // 2. Load the rules if the fingerprint matches
          RulesData rulesData = (RulesData) kryo.readClassAndObject(input);
          if (rulesData != null) {
            S.Integrate.setRulesData(rulesData);
            return true;
          }
        }
      } catch (Exception e) {
        System.err
            .println("Failed to load or deserialize Rubi rules from resource: " + e.getMessage());
      }
      return false;
    }

    /**
     * Orchestrator method to load rules. It tries the fast Kryo cache first. If the cache fails
     * (due to an ID shift or missing file), it builds from a fresh system and immediately
     * serializes the new state.
     */
    private static void loadOrRebuildRubiRules(File localCacheFile) {
      UtilityFunctionCtors.getUtilityFunctionsRuleASTRubi45();

      // Try to load from the JAR resource first (Fastest, read-only)
      // Note: The leading "/" indicates the root of the classpath
      // if (deserializeRubiRulesFromResource("/bin/symja_rubi_rules.bin")) {
      // System.out.println("Successfully loaded Rubi rules from classpath resource.");
      // return;
      // }
      //
      // // If resource is missing or stale, try the local temp file cache
      // if (deserializeRubiRules(localCacheFile)) {
      // System.out.println("Successfully loaded Rubi rules from local cache file.");
      // return;
      // }

      // If both fail, build from scratch and cache locally
      // System.out.println("Initializing Rubi rules from a fresh system...");
      try {
        // Load the actual integration rules (via pre-compiled Java classes or parsing .m files)
        // UtilityFunctionCtors.getUtilityFunctionsRuleASTRubi45();
        getRuleASTStatic();
      } catch (Exception e) {
        System.err.println("Error during raw Rubi initialization: " + e.getMessage());
      }

      // Save to the local file system cache for the next run
      // serializeRubiRules(localCacheFile);
    }

    @Override
    public void run() {
      if (!INTEGRATE_RULES_READ.get()) {
        INTEGRATE_RULES_READ.set(true);
        final EvalEngine engine = EvalEngine.get();
        ContextPath path = engine.getContextPath();
        try {
          engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);

          // Define the cache file location.
          // Using the system temp directory is usually safe, or you can specify a local cache
          // folder.
          File cacheFile = new File(System.getProperty("java.io.tmpdir"), "symja_rubi_rules.bin");

          // Call the Kryo orchestrator
          loadOrRebuildRubiRules(cacheFile);

          ISymbol[] rubiSymbols = {S.Derivative, S.D};
          for (int i = 0; i < rubiSymbols.length; i++) {
            INT_RUBI_FUNCTIONS.add(rubiSymbols[i]);
          }

          // /* Kryo DEL start */
          // UtilityFunctionCtors.getUtilityFunctionsRuleASTRubi45();
          // getRuleASTStatic();
          // /* Kryo DEL end */
          // /* Kryo INS start */
          // // Kryo kryo = KryoUtil.initKryo();
          // // try (
          // // InputStream resourceAsStream =
          // // Integrate.getClass().getResourceAsStream("/rubi_context.bin");
          // // Input input = new Input(resourceAsStream)) {
          // // // kryo sets Context.RUBI internally
          // // kryo.readClassAndObject(input);
          // // } catch (IOException e) {
          // // e.printStackTrace();
          // // }
          // // try (
          // // InputStream resourceAsStream =
          // // Integrate.getClass().getResourceAsStream("/integrate.bin");
          // // Input input = new Input(resourceAsStream)) {
          // // RulesData rulesData = (RulesData) kryo.readClassAndObject(input);
          // // S.Integrate.setRulesData(rulesData);
          // // } catch (IOException e) {
          // // e.printStackTrace();
          // // }
          // // } catch (ClassNotFoundException cnfex) {
          // // cnfex.printStackTrace();
          // /* Kryo INS end */

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

      // ISymbol[] rubiSymbols = {S.Derivative, S.D};
      // for (int i = 0; i < rubiSymbols.length; i++) {
      // INT_RUBI_FUNCTIONS.add(rubiSymbols[i]);
      // }
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
  public IExpr evaluate(IAST holdallAST, final int argSize, final IExpr[] option,
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
      if (argSize < 2) { // || holdallAST.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
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

          // Invalid integration variable or limit(s) in `1`.
          return Errors.printMessage(S.Integrate, "ilim", F.List(arg2), engine);
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
        // if (series.expansionVariable().equals(x)) {
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
      boolean showSteps = false;
      if (showSteps) {
        // if (DEBUG_EXPR.contains(arg1)) {
        // System.exit(-1);
        // }
        DEBUG_EXPR.add(arg1);
      }
      if (arg1.isAST()) {
        final IAST fx = (IAST) arg1;
        if (fx.topHead().equals(x)) {
          // issue #91
          return F.NIL;
        }
        if (forcedMethod != null) {
          // Integrate[f, x, Method -> "..."] forces a single native stage, bypassing the Automatic
          // cascade and the Rubi rules (used mainly by the per-method test suites). A stage that does
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
          // Fast, mostly correct-by-construction algorithm cascade, tried before the Rubi rules. Each
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
          result =
              RationalIntegration.integrate(fx, x, engine, RationalIntegration.RootSumMode.DEFER);
          if (result.isPresent()) {
            return result;
          }
          // Stage: substitution t = (a+b*x)^(1/n) for radicals of a linear function
          result = RadicalSubstitution.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
          // Stage: Chebyshev binomial differentials x^m (a+b*x^n)^p (correct-by-construction).
          result = ChebyshevIntegration.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
          // Stage: product of >= 2 polynomial powers with a compatible polynomial cofactor,
          // Integrate(S*prod(P_i^m_i)) = poly*prod(P_i^(m_i+1)) via a perfect-derivative ansatz.
          // Rubi leaves these unevaluated (its linearity split over the cofactor S produces
          // unresolved pieces), and its own 40s grind on them would otherwise exhaust the test
          // timeout, so this runs before the rules. Restricted to >= 2 power factors to avoid the
          // single-power integrals Rubi already renders canonically. Diff-back self-verified.
          result = ProductPowerIntegration.integrate(fx, x, engine, 2);
          if (result.isPresent()) {
            return result;
          }
        }
        result = integrateByRubiRules(fx, x, ast, engine);
        if (result.isPresent()) {
          return F.subst(result, f -> {
            if (f.isAST(UtilityFunctionCtors.Unintegrable, 3)) {
              IAST integrate = F.Integrate(f.first(), f.second());
              // integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
              return integrate;
            } else if (f.isAST(F.$rubi("CannotIntegrate"), 3)) {
              IAST integrate = F.Integrate(f.first(), f.second());
              // integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
              return integrate;
            }
            return F.NIL;
          });
        }

        result = callRestIntegrate(fx, x, engine);
        if (result.isPresent()) {
          return result;
        }

        // Post-Rubi heuristic fallbacks: broad stages that would otherwise intercept simple integrals
        // Rubi renders in a more canonical form, so they run only for integrands Rubi leaves
        // unevaluated. Each self-verifies (D(result) == integrand). The deterministic, form-safe
        // stages (rational, radical, Chebyshev) run before Rubi (above).
        if (Config.INTEGRATE_ALGORITHMS) {
          // RootSum fallback for rational functions whose denominator has an irreducible factor of
          // degree >= 5, deferred from the pre-Rubi rational stage (RootSumMode.DEFER above). Runs
          // only now that Rubi left the integral unevaluated, so Rubi's simpler closed form (when it
          // has one) always wins. Correct-by-construction (Trager), reuses the full general logic.
          result =
              RationalIntegration.integrate(fx, x, engine, RationalIntegration.RootSumMode.EMIT);
          if (result.isPresent()) {
            return result;
          }
          // Weierstrass t=Tan(x/2) substitution for rational trigonometric integrands.
          result = WeierstrassIntegration.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
          // Derivative-divides (Geddes) u-substitution heuristic.
          result = DerivativeDivides.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
          // Risch-Norman ("parallel Risch" / pmint) heuristic for transcendental integrands.
          result = RischNorman.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
          // Transcendental Risch (RDE / differential-tower based) recogniser.
          result = TranscendentalRisch.integrate(fx, x, engine);
          if (result.isPresent()) {
            return result;
          }
        }

      }
      return evaled ? ast : F.NIL;
    } finally {
      engine.setAssumptions(oldAssumptions);
      engine.setNumericMode(oldNumericMode);
    }
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
   * Force a single native integration stage selected by the {@code Method} option value. Used by the
   * {@code Integrate[f, x, Method -> "..."]} form and the per-method test suites. Returns the stage's
   * antiderivative, or {@link F#NIL} if the named stage does not apply or is unknown (the caller then
   * leaves the integral unevaluated rather than falling through to the Rubi rules).
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

    if (!n.isInteger() || n.toIntDefault() >= 0) {
      return F.NIL;
    }
    if (k.isZero()) {
      return F.NIL;
    }

    IExpr b = bTimes.argSize() == 0 ? F.C1
        : (bTimes.argSize() == 1 ? bTimes.arg1() : engine.evaluate(bTimes));
    int m = -n.toIntDefault();

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

  // /**
  // * Try to integrate {@code 1/p(x)} where {@code p} is a univariate polynomial in {@code x} of
  // * degree ≥ 5 that appears irreducible over Q (no rational roots detected). Returns a
  // * {@code RootSum} antiderivative:
  // *
  // * <pre>
  // * RootSum[(#^n + ...)&, (Log[x - #1] / p'(#1))&]
  // * </pre>
  // *
  // * @param function the integrand expression
  // * @param x the integration variable
  // * @param engine the evaluation engine
  // * @return the {@code RootSum} antiderivative, or {@link F#NIL} if not applicable
  // */
  // private static IExpr integrateOneOverPoly(final IExpr function, final IExpr x,
  // EvalEngine engine) {
  // // Match Power[poly, -1] i.e. poly^(-1)
  // if (!function.isPower()) {
  // return F.NIL;
  // }
  // IExpr base = function.base();
  // IExpr exponent = function.exponent();
  // if (!exponent.isMinusOne()) {
  // return F.NIL;
  // }
  // // base must be a polynomial in x
  // IExpr poly = base;
  //
  // // Determine degree
  // IExpr degExpr = engine.evaluate(F.Exponent(poly, x));
  // if (!degExpr.isInteger()) {
  // return F.NIL;
  // }
  // int deg = degExpr.toIntDefault();
  // if (deg < 5) {
  // // Degrees ≤ 4 have closed-form radical solutions;
  // // leave for Rubi / standard partial fraction rules
  // return F.NIL;
  // }
  //
  // // Check that poly is actually a polynomial in x (no x in denominators, etc.)
  // IExpr polyExpanded = engine.evaluate(F.ExpandAll(poly));
  // IExpr coeffList = engine.evaluate(F.CoefficientList(polyExpanded, x));
  // if (!coeffList.isList()) {
  // return F.NIL;
  // }
  //
  // // Quick rational-root screen: check if Factor splits off a linear factor
  // IExpr factored = engine.evaluate(F.Factor(polyExpanded));
  // // If factored differs from polyExpanded and contains a degree-1 factor, bail out
  // // to let partial fractions handle it
  // if (!factored.equals(polyExpanded)) {
  // // Contains factorable parts → partial fractions already handle this
  // return F.NIL;
  // }
  //
  // // Build p'(x)
  // IExpr dpoly = engine.evaluate(F.D(polyExpanded, x));
  //
  // // Build Function bodies by replacing x → Slot1 (#1)
  // IExpr polyInSlot = engine.evaluate(F.ReplaceAll(polyExpanded, F.Rule(x, F.Slot1)));
  // IExpr dpolyInSlot = engine.evaluate(F.ReplaceAll(dpoly, F.Rule(x, F.Slot1)));
  //
  // // polyFn = (polyInSlot)& i.e. Function[polyInSlot]
  // IExpr polyFn = F.Function(polyInSlot);
  //
  // // formBody = Log[x - #1] / p'(#1)
  // IExpr logArg = F.Subtract(x, F.Slot1);
  // IExpr formBody = F.Divide(F.Log(logArg), dpolyInSlot);
  // IExpr formFn = F.Function(formBody);
  //
  // return F.RootSum(polyFn, formFn);
  // }

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
  private static IExpr integrateAbs(IAST function, final IExpr x) {
    IExpr constant = F.C0;
    if (function.isAST1() && function.first().equals(x)) {
      IAST f1 = function;
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
        IAST abs = function;
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
        IAST power = function;
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
   * Recursively collects potential branch points by finding arguments of Log() and Power()
   * (fractional) that depend on the integration variable x.
   *
   * @param expr The expression to scan.
   * @param x The integration variable.
   */
  private static IAST collectBranchPoints(IExpr expr, IExpr x, IExpr lower, IExpr upper,
      EvalEngine engine) {
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
    IExpr value =
        engine.evaluate(F.Divide(F.ReplaceAll(cofactor, F.Rule(x, root)), F.Abs(slope)));
    if (!value.isFree(x, true)) {
      return F.NIL;
    }
    // reversed limits would need a sign flip; leave those to the generic route
    if (!engine.evaluate(F.Less(lower, upper)).isTrue()) {
      return F.NIL;
    }
    if (lower.isNegativeInfinity() && upper.isInfinity()) {
      return root.isRealResult() ? value
          : F.ConditionalExpression(value, F.Element(root, S.Reals));
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


    // Branch points (Log arguments, Root bases)
    IAST potentialSingularityEquations = collectBranchPoints(function, x, lower, upper, engine);

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
    IExpr upperLimit = engine.evaluate(F.Limit(function, F.Rule(x, upper), upperDirection));
    if (!upperLimit.isSpecialsFree() || upperLimit.isInterval() || upperLimit.isIntervalData()) {
      // Integral of `1` does not converge on `2`.
      return Errors.printMessage(S.Integrate, "idiv",
          F.List(originalAST.arg1(), originalAST.arg2()), engine);
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

        // IExpr temp = integrateTimesTrigFunctions(arg1AST, x);
        // if (temp.isPresent()) {
        // return temp;
        // }
      }

      if (arg1AST.size() >= 3 && arg1AST.isFree(S.Integrate) && arg1AST.isPlusTimesPower()) {
        if (!arg1AST.isEvalFlagOn(IAST.IS_DECOMPOSED_PARTIAL_FRACTION) && x.isSymbol()) {
          Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, true);
          if (parts.isPresent()) {
            IExpr temp = AlgebraUtil.partsApart(parts.get(), x, engine);
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
   * @param x the integ ration variable
   * @return
   */
  private static IExpr mapIntegrate(IAST ast, final IExpr x) {
    return ast.mapThread(F.Integrate(F.Slot1, x), 1);
  }

  /**
   * See <a href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia- Integration by
   * parts</a>
   *
   * @param arg1
   * @param symbol
   * @return
   */
  // private static IExpr integratePolynomialByParts(final IAST arg1, IExpr symbol,
  // EvalEngine engine) {
  // IASTAppendable fTimes = F.TimesAlloc(arg1.size());
  // IASTAppendable gTimes = F.TimesAlloc(arg1.size());
  // collectPolynomialTerms(arg1, symbol, gTimes, fTimes);
  // IExpr g = gTimes.oneIdentity1();
  // IExpr f = fTimes.oneIdentity1();
  // // conflicts with Rubi 4.5 integration rules
  // // only call integrateByParts for simple Times() expressions
  // if (f.isOne() || g.isOne()) {
  // return F.NIL;
  // }
  // return integrateByParts(f, g, symbol, engine);
  // }

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
      if (arg1.isNumericFunctionAST() || INT_RUBI_FUNCTIONS.contains(arg1.topHead())
          || arg1.topHead().getSymbolName().startsWith("§")) {

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
            // LOGGER.log(engine.getLogLevel(), "Integrate(Rubi recursion)", rle);
            return F.NIL;
          } catch (ApfloatInterruptedException | PreemptingException ex) {
            throw ex;
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            engine.setRecursionLimit(limit);
            return Errors.printMessage(S.Integrate, rex, engine);
            // LOGGER.log(engine.getLogLevel(),
            // "Integrate Rubi recursion limit {} RuntimeException: {}",
            // Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT, ast, rex);
            // return F.NIL;
          }

        } catch (AbortException ae) {
          // LOGGER.debug("Integrate.integrateByRubiRules() aborted", ae);
        } catch (final FailedException fe) {
          // LOGGER.debug("Integrate.integrateByRubiRules() failed", fe);
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

  /**
   * Generates a fingerprint based on the current number of built-in symbols. This ensures that if
   * the ID class changes (shifting ordinals), we can dynamically detect it and invalidate the stale
   * cache.
   */
  private static String getSystemIdFingerprint() {
    return "RUBI_V1_ID_COUNT:" + ID.class.getDeclaredFields().length;
  }

  /**
   * Serializes the current S.Integrate rules to a binary Kryo file.
   */
  public static void serializeRubiRules(File cacheFile) {
    try (Output output = new Output(new FileOutputStream(cacheFile))) {
      Kryo kryo = KryoUtil.initKryo();

      // 1. Write the protective fingerprint first
      output.writeString(getSystemIdFingerprint());

      // 2. Serialize the current RulesData for Integrate
      kryo.writeClassAndObject(output, S.Integrate.getRulesData());

      System.out.println("Successfully serialized Rubi rules to: " + cacheFile.getAbsolutePath());
    } catch (Exception e) {
      System.err.println("Failed to serialize Rubi rules: " + e.getMessage());
    }
  }

  /**
   * Attempts to deserialize the Rubi rules from a binary file. * @return true if successful, false
   * if the file is missing, corrupted, or if the ID numbers have changed.
   */
  public static boolean deserializeRubiRules(File cacheFile) {
    if (!cacheFile.exists()) {
      return false;
    }

    try (Input input = new Input(new FileInputStream(cacheFile))) {
      Kryo kryo = KryoUtil.initKryo();

      // 1. Verify the fingerprint
      String savedFingerprint = input.readString();
      if (!getSystemIdFingerprint().equals(savedFingerprint)) {
        System.out.println("Symja ID numbers changed. Invalidating stale Rubi Kryo cache.");
        return false;
      }

      // 2. Load the rules if the fingerprint matches
      RulesData rulesData = (RulesData) kryo.readClassAndObject(input);
      if (rulesData != null) {
        S.Integrate.setRulesData(rulesData);
        return true;
      }
    } catch (Exception e) {
      System.err.println("Corrupted Rubi Kryo cache. Forcing rebuild...");
    }
    return false;
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
      if (Config.THREAD_FACTORY != null) {
        INIT_THREAD = Config.THREAD_FACTORY.newThread(new IntegrateInitializer());
      } else {
        INIT_THREAD = new Thread(new IntegrateInitializer(), "IntegrateInitializer");
      }
      INIT_THREAD.start();
    } else {
      // see #evaluate() method
    }

  }
}
