package org.matheclipse.core.reflection.system;

import java.util.Optional;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;

public final class Limit extends AbstractFunctionOptionEvaluator {

  /** Direction of limit computation */
  static enum Direction {
    /** Compute the limit approaching from larger real values. */
    FROM_ABOVE(-1),

    /** Compute the limit approaching from larger or smaller real values automatically. */
    TWO_SIDED(0),

    /** Compute the limit approaching from smaller real values. */
    FROM_BELOW(1);

    private int direction;

    private Direction(int direction) {
      this.direction = direction;
    }

    /**
     * Convert the direction <code>FROM_ABOVE, TWO_SIDED, FROM_BELOW</code> to the corresponding
     * value <code>-1, 0, 1</code>
     *
     * @return
     */
    int toInt() {
      return direction;
    }
  }


  /** Representing the data for the current limit. */
  static class LimitData {
    private final ISymbol variable;

    final private IExpr limitValue;

    /** The rule <code>variable->limitValue</code>. */
    final private IAST rule;

    final private Direction direction;

    public LimitData(ISymbol variable, IExpr limitValue, IAST rule, Direction direction) {
      this.variable = variable;
      this.limitValue = limitValue;
      this.rule = rule;
      this.direction = direction;
    }

    /**
     * Examples: * *
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * * * *
     * 
     * <pre>
     * Limit(x ^ 2 * Sin(1 / x) ^ 3, x -> 0)
     * </pre>
     * 
     * * * @param x * @return
     */
    private boolean determineCosSinCases(IExpr x) {
      if (x.isPower()) {
        int exponent = x.exponent().toIntDefault();
        if (exponent > 0) {
          return determineCosSinCases(x.base());
        }
      }
      return (x.isSin() || x.isCos()) && !x.isFree(variable);
    }

    /**
     * Get the optional direction value. Default is DIRECTION_TWO_SIDED = 0.
     *
     */
    public Direction direction() {
      return direction;
    }

    /**
     * Create a new <code>F.Limit( arg1, ... )</code> expression from this <code>LimitData</code>
     * object
     *
     * @param arg1 the first argument of the Limit expression
     * @return a new <code>F.Limit( arg1, ... )</code> expression
     */
    public IExpr limit(IExpr arg1) {
      return evalLimitQuiet(arg1, this);
    }

    /**
     * Get the limit value of the limit definition <code>variable->limitValue</code>
     */
    public IExpr limitValue() {
      return limitValue;
    }

    /**
     * Map a <code>F.Limit( arg1, ... )</code> expression at each argument of the given <code>ast
     * </code>.
     *
     * @param ast the expression to map the limit on
     */
    public IExpr mapLimit(final IAST ast) {
      IASTMutable result = ast.copy();
      boolean isIndeterminate = false;
      boolean isLimit = false;
      for (int i = 1; i < ast.size(); i++) {
        IExpr temp = evalLimitQuiet(ast.get(i), this);
        if (!temp.isPresent()) {
          // FIX: If any argument's limit fails, the mapped limit fails.
          // Do not insert F.NIL into the AST!
          return F.NIL;
        }
        if (!temp.isFree(S.Limit)) {
          isLimit = true;
        } else if (temp.isIndeterminate()) {
          isIndeterminate = true;
        }
        result.set(i, temp);
      }
      if (isLimit && isIndeterminate) {
        return F.NIL;
      }
      if (isIndeterminate && limitValue.isZero() && ast.isTimes()) {
        return squeezeTheorem(ast).orElse(result);
      }
      IExpr evaledResult = EvalEngine.get().evaluate(result);
      // Convert unresolved oscillating Intervals to Indeterminate
      return IntervalSym.toAccumBoundsIndeterminate(evaledResult);
    }

    public IAST rule() {
      return rule;
    }

    /**
     * Try the squeeze theorem: <a href="https://en.wikipedia.org/wiki/Squeeze_theorem">Wikipedia -
     * Squeeze theorem</a>. It is assumed that {@link #limitValue} equals <code>0</code>.
     * <p>
     * Example: * *
     * 
     * <pre>
     * Limit(x * Sin(1 / x), x -> 0)
     * </pre>
     * 
     * * * @param ast * @param defaultResult
     * 
     * @return <code>F.Times(F.C0)</code> or {@link F#NIL} if squeeze theorem was not applicable.
     */
    private IAST squeezeTheorem(final IAST ast) {
      IASTAppendable[] cosSinFilter = ast.filter(x -> determineCosSinCases(x));
      if (cosSinFilter != null //
          && cosSinFilter[0].argSize() > 0 //
          && cosSinFilter[1].argSize() > 0 //
          && !cosSinFilter[0].isOne() //
          && !cosSinFilter[1].isOne()) {
        IExpr temp = evalLimitQuiet(F.Abs(cosSinFilter[1]), this);
        if (temp.isZero()) {
          temp = evalLimitQuiet(F.Abs(cosSinFilter[0]), this);
          if (temp.isIndeterminate()) {
            return F.Times(F.C0);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Get the <code>variable</code> of the limit definition <code>variable->limitValue</code>
     */
    public ISymbol variable() {
      return variable;
    }
  }

  final static boolean DEBUG = false;

  /**
   * Minimum leaf count of an expression before {@link #evalLimit} attempts an algebraic
   * {@code Simplify} to escape a combinatorial blow-up of the limit heuristics / Gruntz algorithm
   * (see issue #1420). Kept high enough that ordinary small limit expressions are unaffected.
   */
  private final static int LIMIT_SIMPLIFY_LEAFCOUNT = 30;

  /**
   * Guards the hyperbolic-to-exponential retry in {@link #evalLimit} against re-entry: the
   * rewritten form is fed back through {@code evalLimit}, and a later {@code Simplify} step could
   * in principle turn it back into a hyperbolic function and loop. We only ever rewrite once per
   * evaluation stack.
   */
  private static final ThreadLocal<Boolean> HYPERBOLIC_EXP_RETRY =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Depth guard for the Gamma pole-shift retry in {@link #evaluateLimit}: the shift
   * <code>Gamma(z) -> Gamma(z+m+1)/(z...(z+m))</code> and FunctionExpand's opposite rewrite
   * <code>Gamma(1+z) -> z*Gamma(z)</code> would otherwise ping-pong through recursive limit
   * evaluations forever. A small cap still allows legitimately nested shifts.
   */
  private static final ThreadLocal<Integer> GAMMA_POLE_SHIFT_DEPTH =
      ThreadLocal.withInitial(() -> 0);

  /**
   * Depth guard for the Erfc asymptotic substitution in {@link #evaluateLimit}: the rewritten form
   * is resolved by a recursive {@link #evaluateLimit}, whose own sub-limits may carry further Erfc
   * applications.
   */
  private static final ThreadLocal<Integer> ERFC_ASYMPTOTIC_DEPTH =
      ThreadLocal.withInitial(() -> 0);

  /**
   * Recursion guard for the dominant-term rule {@link #dominantTermLimit}, which recurses through
   * {@link #evaluateLimit} on the winning summand and {@link #evalLimitQuiet} on the pairwise
   * ratios.
   */
  private static final ThreadLocal<Integer> DOMINANT_TERM_DEPTH = ThreadLocal.withInitial(() -> 0);

  /**
   * True while an {@link #lHospitalesRule} application holds the relative recursion budget - nested
   * applications must not extend the ceiling again (see the comment inside the method).
   */
  private static final ThreadLocal<Boolean> LHOSPITAL_BUDGET_ACTIVE =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /** One-shot guard for the Together retry of an Indeterminate finite-point difference limit. */
  private static final ThreadLocal<Boolean> TOGETHER_LIMIT_RETRY =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * One-shot guard for the one-sided rewrite of jump-discontinuous functions. The rewrite computes
   * the limit of the step function's ARGUMENT, which re-enters {@link #evalLimit}; the flag keeps
   * that sub-limit from starting another rewrite sweep.
   */
  private static final ThreadLocal<Boolean> STEP_FUNCTION_REWRITE =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * One-shot guard for the dominant-argument rewrite of {@link S#Max} / {@link S#Min}. Selecting
   * the dominant argument compares argument DIFFERENCES through the sign machinery, which re-enters
   * {@link #evalLimit}; the flag keeps that sub-limit from starting another rewrite sweep.
   */
  private static final ThreadLocal<Boolean> MAX_MIN_REWRITE =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Nesting depth of the Limit builtin itself. Internal machinery mostly bypasses the builtin, but
   * embedded <code>Limit(...)</code> shells inside partial results and engine-level Limit calls
   * from the series helpers re-enter it mid-computation - only the OUTERMOST entry (depth 0) may
   * reset per-user-call state like the {@link LimitGruntz} caches.
   */
  private static final ThreadLocal<Integer> LIMIT_BUILTIN_DEPTH = ThreadLocal.withInitial(() -> 0);

  /**
   * Evaluate the limit for the given limit data.
   *
   * @param expr
   * @param data the limits data definition
   * @param engine
   * @return {@link S#NIL} if no limit could be found
   */
  private static IExpr evalLimit(IExpr evaledExpr, LimitData data, EvalEngine engine) {
    final ISymbol symbol = data.variable();
    final IExpr limitValue = data.limitValue();

    // Android-changed: do not use shared EvalEngine
    if (engine == null) {
      engine = EvalEngine.get();
    }

    // Limit[RootSum[f, g], x -> x0] with finite x0 and a root polynomial f free of x is the direct
    // substitution RootSum[f, g /. x -> x0]; this is what Integrate uses to evaluate a RootSum
    // antiderivative at the bounds of a definite integral.
    IExpr rootSumLimit = limitRootSum(evaledExpr, data, engine);
    if (rootSumLimit.isPresent()) {
      return rootSumLimit;
    }

    if (data.direction() == Direction.TWO_SIDED && !limitValue.isDirectedInfinity()) {
      return evalLimitTwoSided(evaledExpr, data, engine);
    }

    // One traversal computes every feature flag the gates below test - re-running a full
    // .has() scan per gate made each evalLimit recursion pay for a dozen tree walks.
    LimitFeatures features = LimitFeatures.refresh(null, evaledExpr, symbol);

    // Jump-discontinuous functions (Floor, Ceiling, Round, Sign, UnitStep, Mod, ...) are constant
    // or continuous on each side of a jump, so a DIRECTIONAL limit is the value on the approached
    // side - not the value AT the point, which is what the direct substitution further down
    // returns. Replace every such sub-expression by its one-sided value up front; the TWO_SIDED
    // case above then reports Indeterminate whenever the two sides disagree.
    if (features.stepFunction) {
      IExpr stepRewritten = stepFunctionRewrite(evaledExpr, data, engine);
      if (stepRewritten.isPresent()) {
        evaledExpr = stepRewritten;
        features = LimitFeatures.refresh(features, evaledExpr, symbol);
      }
    }

    // Max/Min is eventually EQUAL to whichever argument dominates near the limit point, so replace
    // it by that argument. Without this, x*Max(1/x,2/x,3/x) substitutes to Infinity*0 ->
    // Indeterminate and the series machinery differentiates Max as an unknown smooth function
    // (Derivative(0,0,1)[Max][0,0,0] terms). Only fires when EVERY argument comparison is
    // decidable, so crossing arguments (Max(Sin(x),Cos(x))) keep their current behaviour.
    if (features.maxMin) {
      IExpr maxMinRewritten = maxMinRewrite(evaledExpr, data, engine);
      if (maxMinRewritten.isPresent()) {
        evaledExpr = maxMinRewritten;
        features = LimitFeatures.refresh(features, evaledExpr, symbol);
      }
    }

    // --- OSCILLATING SPECIAL FUNCTIONS AT NEGATIVE INFINITY ---
    // Gamma, Factorial, and PolyGamma have poles at every negative integer.
    // As they approach -Infinity, they oscillate wildly and their limits are Indeterminate.
    // Sin and Cos oscillate without bound as their argument approaches ±Infinity.
    // We must intercept them here to prevent the series evaluator from endlessly
    // differentiating them into a fractal recursion explosion.
    boolean hasOscillatingSpecial =
        features.oscillator && isOscillatingSpecial(evaledExpr, symbol, limitValue, data);
    if (hasOscillatingSpecial) {
      return S.Indeterminate;
    }

    if (features.absOrSign && limitValue.isInfinity()) {
      evaledExpr = F.subst(evaledExpr, x -> {
        if (x.isAbs() && x.first().equals(data.variable())) {
          return data.variable();
        }
        if (x.isAST(S.Sign, 2) && x.first().equals(data.variable())) {
          return F.C1;
        }
        return F.NIL;
      });
      features = LimitFeatures.refresh(features, evaledExpr, symbol);
    } else if (features.absOrSign && limitValue.isNegativeInfinity()) {
      evaledExpr = F.subst(evaledExpr, x -> {
        if (x.isAbs() && x.first().equals(data.variable())) {
          return data.variable().negate();
        }
        if (x.isAST(S.Sign, 2) && x.first().equals(data.variable())) {
          return F.CN1;
        }
        return F.NIL;
      });
      features = LimitFeatures.refresh(features, evaledExpr, symbol);
    }

    // Hyperbolic functions (Cosh/Sinh/Tanh/...) confuse the downstream +/-Infinity heuristics
    // (e.g. 2*Cosh(x)*E^x -> Indeterminate, which also poisons signInf and the Gruntz fallback).
    // At an infinite limit, rewrite them to exponentials up front and retry via a recursive
    // evalLimit on the expanded form (2*Cosh(x)*E^x -> E^(2*x)+1 -> Infinity). Only adopt the
    // result if it fully resolves, so cases the normal path already handles fall through
    // unchanged; the one-shot HYPERBOLIC_EXP_RETRY guard keeps the single rewrite loop-proof.
    // Only the oscillation cases (argument depends on the limit variable) need the exp
    // rewrite; a hyperbolic CONSTANT like Sinh(1) (from e.g. 3*Sin(I)) must stay intact
    // (issue #42: y*3*Sin(I)*x) - see LimitFeatures.hyperbolicVar.
    if (!HYPERBOLIC_EXP_RETRY.get() && (limitValue.isInfinity() || limitValue.isNegativeInfinity())
        && features.hyperbolicVar) {
      // targeted hyperbolic->exponential rewrite: TrigToExp would ALSO turn circular trig in
      // the same expression into complex I-exponentials, which the adoption guards then have
      // to reject at full cost
      IExpr expForm = engine.evalQuiet(
          F.ExpandAll(LimitGruntz.expandHyperbolics(evaledExpr, engine, data.variable())));
      if (expForm.isPresent() && !expForm.equals(evaledExpr)) {
        HYPERBOLIC_EXP_RETRY.set(Boolean.TRUE);
        try {
          IExpr hypTemp = evalLimit(expForm, data, engine);
          if (hypTemp.isPresent() && hypTemp.isFree(S.Limit) && hypTemp.isIndeterminateFree()) {
            return hypTemp;
          }
        } finally {
          HYPERBOLIC_EXP_RETRY.set(Boolean.FALSE);
        }
      }
    }

    IExpr result = engine.evalQuiet(evaledExpr);
    if (result.isNumericFunction(true)) {
      return result;
    }
    if (!result.isIndeterminate()) {
      evaledExpr = result;
    }
    if (result.isFree(data.variable(), true)) {
      // Limit[a_,sym->lim] -> a
      return evaledExpr;
    }
    if (result.equals(data.variable())) {
      // Limit[x_,x_->lim] -> lim
      return limitValue;
    }
    if (DEBUG) {
      System.out.println("Evaluating limit of " + evaledExpr + " as " + data.variable()
          + " approaches " + limitValue);
    }
    // Certain shapes starve the L'Hopital heuristics while the Gruntz algorithm resolves
    // them in about a second when it can and fails fast when it cannot - consult Gruntz FIRST
    // for exactly those (see isGruntzFirstShape), but only at the TOP level: the algorithm's
    // own sub-limits re-entering this gate multiply the cost combinatorially (observed
    // 1s -> 49s). This must run BEFORE the substitution fast paths below: limitInfinityZero's
    // argument-limit already starts the L'Hopital burn (observed on thesis 8.20's
    // E^(log-nest ratio)).
    features = LimitFeatures.refresh(features, evaledExpr, symbol);
    if ((limitValue.isInfinity() || limitValue.isNegativeInfinity()) && !LimitGruntz.isActive()
        && !LimitGruntz.isInGruntzSeries() && features.gruntzFirstShape()
        && evaledExpr.isNumericFunction(new VariablesSet(evaledExpr))) {
      IExpr gruntzFirst =
          LimitGruntz.evaluateLimit(evaledExpr, symbol, limitValue, data.direction(), engine);
      if (gruntzFirst.isPresent() && gruntzFirst.isFree(S.Limit)
          && gruntzFirst.isIndeterminateFree() && !hasNestedDirectedInfinity(gruntzFirst)) {
        return gruntzFirst;
      }
    }

    if (limitValue.isNumericFunction(true) && !features.piecewise) {
      IExpr temp = evalReplaceAll(evaledExpr, data, engine);
      if (temp.isPresent()) {
        return temp;
      }
    } else if ((limitValue.isInfinity() || limitValue.isNegativeInfinity()) && evaledExpr.isAST()
        && evaledExpr.size() > 1) {
      if (limitValue.isInfinity() || limitValue.isNegativeInfinity()) {
        IExpr temp = evalReplaceAll(evaledExpr, data, engine);
        if (temp.isNumericFunction(true)) {
          return temp;
        }
        if (evaledExpr.isNumericFunction(data.variable()) && evaledExpr.size() > 1
            && !evaledExpr.isPlusTimesPower()) {
          temp = limitNumericFunctionArgs((IAST) evaledExpr, data, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      IExpr temp = limitInfinityZero((IAST) evaledExpr, data, (IAST) limitValue);
      if (temp.isPresent()) {
        return temp;
      }
    }

    // Large "0 * Infinity" style forms (e.g. issue #1420) can drive the naive limit heuristics
    // and the Gruntz algorithm into a combinatorial explosion, while an algebraic Simplify
    // reduces them to a form whose limit resolves directly. Once the cheap fast paths above
    // have declined, retry on a simplified form when it is strictly smaller; the strict
    // leaf-count decrease guarantees this recursion terminates. Never Simplify when a
    // circular trig function of the limit variable is present: Simplify then runs
    // TrigSimplifyFu/TrigExpand whose output explodes combinatorially even for small inputs
    // (TimeConstrained cannot cut it - it degrades to a plain evaluation in
    // TIMECONSTRAINED_NO_THREAD mode), e.g. the Sin(n*ansatz) Taylor remainders produced by
    // AsymptoticRSolveValue ground for minutes here; the oscillation logic handles trig.
    if (evaledExpr.isAST() && evaledExpr.leafCount() > LIMIT_SIMPLIFY_LEAFCOUNT
        && !LimitFeatures.refresh(features, evaledExpr, symbol).trigVar) {
      IExpr simplified = engine.evalQuiet(F.Simplify(evaledExpr));
      if (simplified.isPresent() && !simplified.equals(evaledExpr)
          && simplified.leafCount() < evaledExpr.leafCount()) {
        return evalLimit(simplified, data, engine);
      }
    }

    IExpr temp = evalLimitAST(evaledExpr, limitValue, data, engine);
    // Return early if the limit heuristically found a definitive mathematical form
    // including correctly determined Indeterminate bounds (e.g. from branch cuts).
    // A result with a DirectedInfinity nested inside a function argument (e.g. the naive
    // termwise sum Infinity-Log(Infinity+a)) is NOT definitive - let the fallbacks run.
    if (temp.isPresent() && temp.isFree(S.Limit) && temp.isIndeterminateFree()
        && !hasNestedDirectedInfinity(temp)) {
      return temp;
    }

    // 2. If heuristics failed (NIL) OR returned an unresolved Limit or infinity-junk,
    // intercept it and fall back to advanced methods.
    if (temp.isNIL() || !temp.isFree(S.Limit) || !temp.isIndeterminateFree()
        || hasNestedDirectedInfinity(temp)) {

      IExpr expandedExpr = engine.evalQuiet(F.ExpandAll(evaledExpr));
      if (expandedExpr.isPresent() && !expandedExpr.equals(evaledExpr)) {
        IExpr expTemp = evalLimitAST(expandedExpr, limitValue, data, engine);
        if (expTemp.isPresent() && expTemp.isFree(S.Limit) && !hasNestedDirectedInfinity(expTemp)) {
          return expTemp;
        }
      }

      // Free symbolic parameters (e.g. Limit(E^x/(a+E^x), x->Infinity)) are legal for the
      // Gruntz algorithm - treat every symbol as a numeric constant when gating.
      if (evaledExpr.isNumericFunction(new VariablesSet(evaledExpr))) {
        IExpr gruntzResult = LimitGruntz.evaluateLimit(evaledExpr, data.variable(),
            data.limitValue(), data.direction(), engine);

        if (gruntzResult.isPresent() && gruntzResult.isFree(S.Limit)) {
          return gruntzResult;
        }
        if (gruntzResult.isPresent() && temp.isNIL()) {
          return gruntzResult;
        }
      }
    }
    if (temp.isPresent()) {
      return temp;
    }
    return F.NIL;
  }

  /**
   * A limit result is junk when a {@link S#DirectedInfinity} ended up nested inside the argument of
   * a non-arithmetic function - e.g. the naive termwise sum <code>Infinity-Log(Infinity+a)</code> -
   * rather than standing alone or inside a top-level sum/product. Such results must not be accepted
   * as definitive; the Gruntz fallback usually resolves them properly. Structural containers (List,
   * Interval, ConditionalExpression) that legitimately carry infinities are exempt.
   */
  private static boolean hasNestedDirectedInfinity(IExpr result) {
    if (result.isFree(S.DirectedInfinity)) {
      return false;
    }
    return result.has(sub -> {
      if (!sub.isAST() || sub.isPlus() || sub.isTimes() || sub.isDirectedInfinity() || sub.isList()
          || sub.isInterval() || sub.isIntervalData() || sub.isAST(S.ConditionalExpression)) {
        return false;
      }
      return ((IAST) sub).exists(arg -> !arg.isFree(S.DirectedInfinity));
    }, true);
  }

  /**
   * <code>E^(diverging Gamma-family)</code> like <code>E^Gamma(x)/Gamma(x)</code>: the heuristic
   * Stirling preprocessing substitutes <code>E^Gamma(x) -&gt; E^(x*Log(x)-x+...)</code> into a form
   * whose Gamma-vs-poly-log ranking the machinery cannot do, while Gruntz ranks the raw
   * <code>E^Gamma(x)</code> via its mrv set <code>{E^Gamma(x)}</code> in ~70ms (thesis #46). Routed
   * to Gruntz FIRST at the builtin boundary - once per user Limit, not on every recursive
   * evaluateLimit (the nested has-scan there made the Gamma-difference cases O(n^2)).
   */
  private static boolean isExpGammaTowerShape(IExpr expr, ISymbol limitVar) {
    return expr
        .has(
            p -> p.isExp() && !p.exponent().isFree(limitVar, true)
                && p.exponent().has(
                    t -> t.isFunctionID(ID.Gamma, ID.LogGamma, ID.Factorial, ID.Pochhammer), true),
            true);
  }

  /**
   * Feature flags of one expression, computed in a SINGLE traversal (heads included, mirroring
   * <code>has(..., true)</code> semantics). {@link #evalLimit} consults up to a dozen structural
   * gates per recursion - as separate <code>.has()</code> scans they each re-walked the whole tree;
   * this class walks it once and the gates test booleans. {@link #refresh} rescans only when the
   * expression object actually changed.
   */
  private static final class LimitFeatures {
    /** The expression the flags were computed for (identity comparison in {@link #refresh}). */
    final IExpr source;

    /** Floor/Ceiling/Round/Sign/UnitStep/IntegerPart/FractionalPart/Mod/Quotient present. */
    boolean stepFunction;
    /** Max/Min depending on the limit variable present. */
    boolean maxMin;
    /**
     * A head {@link #isOscillatingSpecial} cares about is present (trig, Gamma-family poles,
     * Airy/Bessel/Struve, Zeta, Factorial2).
     */
    boolean oscillator;
    /** A hyperbolic function whose argument depends on the limit variable is present. */
    boolean hyperbolicVar;
    /** Abs or Sign present (gates the Abs(x)/Sign(x) substitutions at +-Infinity). */
    boolean absOrSign;
    /** A power f(x)^g(x) with variable, non-E, trig-free base and variable exponent. */
    boolean varPower;
    /** A Log of a variable-dependent sum with a nested Log (thesis 8.19/8.20 log-nests). */
    boolean logNest;
    /** An E^f with variable, Log-bearing, trig-free exponent (thesis 8.9 towers). */
    boolean expLog;
    /** Gamma/LogGamma/Factorial/Pochhammer/PolyGamma present. */
    boolean gammaFamily;
    /** A circular trig function depending on the limit variable is present. */
    boolean trigVar;
    /** The Piecewise symbol occurs (any position, including as a head). */
    boolean piecewise;

    private LimitFeatures(IExpr expr, ISymbol variable) {
      this.source = expr;
      scan(expr, variable);
    }

    /** Rescan only when <code>expr</code> is not the object <code>current</code> was built on. */
    static LimitFeatures refresh(LimitFeatures current, IExpr expr, ISymbol variable) {
      return (current != null && current.source == expr) ? current
          : new LimitFeatures(expr, variable);
    }

    /**
     * The expression shapes consulted with the Gruntz algorithm BEFORE the general machinery at an
     * infinite limit point (top level only - see the caller in {@link #evalLimit}):
     *
     * <ul>
     * <li>a power <code>f(x)^g(x)</code> with a variable base (not <code>E</code>) and variable
     * exponent - drives the L'Hopital heuristics into an unbounded derivative explosion
     * (<code>(1+1/x)^(x^2)</code> burned whole time budgets). Oscillatory bases like
     * <code>(Sin(1/x)/2)^(1/x^2)</code> belong to the envelope machinery; the Gruntz mrv/rewrite
     * chain grinds on them.</li>
     * <li>log-nests like <code>Log(Log(x)+Log(Log(x)))</code> (thesis 8.19/8.20) - equally starve
     * the L'Hopital heuristics while Gruntz handles them via repeated moveup. Gamma-family
     * expressions are excluded: their Stirling preprocessing produces log-sums that route better
     * through the established Gamma pipeline.</li>
     * <li>exponential towers <code>E^f</code> with a Log-bearing exponent (thesis 8.9's
     * <code>Log(x)^2*E^(Sqrt(Log(x))*...)/Sqrt(x)</code>) - sit between power growth classes; the
     * heuristic Times path collapses them to Indeterminate via <code>oo*oo*0</code>. Same
     * Gamma-family exclusion.</li>
     * </ul>
     */
    boolean gruntzFirstShape() {
      return varPower || ((logNest || expLog) && !gammaFamily);
    }

    private void scan(IExpr expr, ISymbol variable) {
      if (expr == S.Piecewise) {
        piecewise = true;
      }
      if (!expr.isAST()) {
        return;
      }
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Floor:
          case ID.Ceiling:
          case ID.Round:
          case ID.UnitStep:
          case ID.IntegerPart:
          case ID.FractionalPart:
          case ID.Mod:
          case ID.Quotient:
            stepFunction = true;
            break;
          case ID.Sign:
            stepFunction = true;
            absOrSign = true;
            break;
          case ID.Abs:
            absOrSign = true;
            break;
          case ID.Max:
          case ID.Min:
            if (!maxMin && !ast.isFree(variable, true)) {
              maxMin = true;
            }
            break;
          case ID.Gamma:
          case ID.LogGamma:
          case ID.Factorial:
          case ID.PolyGamma:
            gammaFamily = true;
            oscillator = true;
            break;
          case ID.Pochhammer:
            gammaFamily = true;
            break;
          case ID.Factorial2:
          case ID.Zeta:
          case ID.AiryAi:
          case ID.AiryBi:
          case ID.BesselJ:
          case ID.BesselY:
          case ID.StruveH:
          case ID.StruveL:
            oscillator = true;
            break;
          case ID.Sin:
          case ID.Cos:
          case ID.Tan:
          case ID.Cot:
          case ID.Sec:
          case ID.Csc:
            oscillator = true;
            if (!trigVar && !ast.isFree(variable, true)) {
              trigVar = true;
            }
            break;
          case ID.Sinh:
          case ID.Cosh:
          case ID.Tanh:
          case ID.Coth:
          case ID.Sech:
          case ID.Csch:
            if (!hyperbolicVar && ast.argSize() >= 1 && !ast.arg1().isFree(variable, true)) {
              hyperbolicVar = true;
            }
            break;
          case ID.Power:
            if (ast.argSize() == 2) {
              IExpr base = ast.base();
              IExpr exponent = ast.exponent();
              if (base == S.E) {
                if (!expLog && !exponent.isFree(variable, true)
                    && exponent.has(t -> t.isLog(), true)
                    && exponent.isFree(
                        t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc),
                        true)) {
                  expLog = true;
                }
              } else if (!varPower && !base.isFree(variable, true)
                  && !exponent.isFree(variable, true) && base.isFree(
                      t -> t.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc), true)) {
                varPower = true;
              }
            }
            break;
          case ID.Log:
            if (!logNest && ast.argSize() == 1) {
              IExpr arg = ast.arg1();
              if (arg.isPlus() && !arg.isFree(variable, true)
                  && ((IAST) arg).exists(a -> a.has(s -> s.isLog(), true))) {
                logNest = true;
              }
            }
            break;
          default:
            break;
        }
      }
      scan(head, variable);
      for (int i = 1; i <= ast.argSize(); i++) {
        scan(ast.get(i), variable);
      }
    }
  }

  /**
   * Special-function presence flags for {@link #evaluateLimit(IExpr, IAST, Direction, EvalEngine)}
   * - one traversal instead of one <code>.has()</code> scan per gate (Ei shifts, Ei near zero, Erfc
   * asymptotics, Gamma-family/HarmonicNumber pipeline).
   */
  private static final class SpecialFunctionFeatures {
    /** ExpIntegralEi with a Plus argument (Gamma-shift pipeline at +-Infinity). */
    boolean eiShiftArg;
    /** ExpIntegralEi whose argument depends on the limit variable (near-0 series). */
    boolean eiVarArg;
    /** Erfc whose argument depends on the limit variable (asymptotic expansion). */
    boolean erfcVarArg;
    /** Gamma-family function or one-argument HarmonicNumber present. */
    boolean gammaOrHarmonic;

    SpecialFunctionFeatures(IExpr expr, ISymbol variable) {
      scan(expr, variable);
    }

    private void scan(IExpr expr, ISymbol variable) {
      if (!expr.isAST()) {
        return;
      }
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.ExpIntegralEi:
            if (ast.argSize() == 1) {
              if (ast.arg1().isPlus()) {
                eiShiftArg = true;
              }
              if (!eiVarArg && !ast.arg1().isFree(variable, true)) {
                eiVarArg = true;
              }
            }
            break;
          case ID.Erfc:
            if (!erfcVarArg && ast.argSize() == 1 && !ast.arg1().isFree(variable, true)) {
              erfcVarArg = true;
            }
            break;
          case ID.Factorial:
          case ID.Gamma:
          case ID.LogGamma:
          case ID.Pochhammer:
          case ID.PolyGamma:
            gammaOrHarmonic = true;
            break;
          case ID.HarmonicNumber:
            if (ast.argSize() == 1) {
              gammaOrHarmonic = true;
            }
            break;
          default:
            break;
        }
      }
      scan(head, variable);
      for (int i = 1; i <= ast.argSize(); i++) {
        scan(ast.get(i), variable);
      }
    }
  }

  private static IExpr evalLimitAST(final IExpr expression, final IExpr limitValue, LimitData data,
      EvalEngine engine) {

    // Safely catches shapes like (Sin(1/x)/2)^(1/x^2) as x -> 0 natively
    IExpr envelope = envelopeBounded(expression, data, engine);
    if (envelope.isPresent()) {
      return envelope;
    }

    if (expression.isAST()) {
      if (!limitValue.isNumericFunction(true) && limitValue.isFree(S.DirectedInfinity)
          && limitValue.isFree(data.variable())) {
        // example Limit(E^(3*x), x->a) ==> E^(3*a)
        IExpr temp = expression.replaceAll(data.rule()).orElse(expression);
        IExpr evalTemp = engine.evalQuiet(temp);
        if (evalTemp.isFree(S.DirectedInfinity) && evalTemp.isIndeterminateFree()) {
          return evalTemp;
        }
      }
      final IAST ast = (IAST) expression;
      if (ast.isPlus()) {
        return plusLimit(ast, data, engine);
      } else if (ast.isTimes()) {
        return timesLimit(ast, data, engine);
      } else if (ast.isPower()) {
        return powerLimit(ast, data, engine);
      } else if (ast.isAST(S.Piecewise, 3)) {
        return piecewiseLimit(ast, data, engine);
      } else if (ast.argSize() > 0 && ast.isNumericFunctionAST()) {
        IASTMutable copy = ast.copy();
        IExpr temp = F.NIL;
        boolean indeterminate = false;
        boolean hasNIL = false;
        for (int i = 1; i < ast.size(); i++) {
          temp = data.limit(ast.get(i));
          if (temp.isPresent()) {
            if (temp.isIndeterminate()) {
              if (data.direction != Direction.TWO_SIDED) {
                return S.Indeterminate;
              }
              indeterminate = true;
              copy.set(i, S.Indeterminate);
            } else {
              copy.set(i, temp);
            }
          } else {
            // Limit computation failed for this argument — don't confuse with Indeterminate
            hasNIL = true;
          }
        }
        if (!indeterminate && !hasNIL) {
          temp = engine.evalQuiet(copy);

          // Intercept unevaluated special functions with DirectedInfinity arguments
          if (temp.isAST()) {
            IExpr specialLimit = directedInfinityLimit((IAST) temp);
            if (specialLimit.isPresent()) {
              return specialLimit;
            }
          }

          // a result like PolyGamma(0,...,Infinity) - DirectedInfinity stuck inside a
          // function argument - is junk, not a limit value; decline and let fallbacks run.
          // ComplexInfinity is declined for the same reason as in limitNumericFunctionArgs:
          // substituting the argument's limit is only valid where the function is CONTINUOUS,
          // and ComplexInfinity is exactly the report that it has a pole there (Gamma(0),
          // Zeta(1)). The one-sided limits at such a pole are +Infinity and -Infinity, which
          // the fallbacks below resolve.
          if (temp.isPresent() && !temp.isIndeterminate() && !temp.isComplexInfinity()
              && !hasNestedDirectedInfinity(temp)) {
            return temp;
          }
        }
        if (data.direction == Direction.TWO_SIDED && (indeterminate || hasNIL)) {
          IExpr twoSided = evalLimitTwoSided(expression, data, engine);
          if (twoSided.isPresent()) {
            return twoSided;
          }
        }
        // If any argument's limit couldn't be computed at all, signal failure (F.NIL),
        // not mathematical indeterminacy (S.Indeterminate).
        if (hasNIL) {
          return F.NIL;
        }
        return indeterminate ? S.Indeterminate : F.NIL;
      }
    }
    return F.NIL;
  }



  static IExpr evalLimitQuiet(final IExpr expr, LimitData data) {
    if (expr.isNumber()) {
      return expr;
    }
    EvalEngine engine = EvalEngine.get();
    boolean quiet = engine.isQuietMode();
    try {
      // this is a speculative internal sub-limit: its messages (0*Infinity encountered, ...)
      // must not leak to the user - the finally below restores the caller's mode
      engine.setQuietMode(true);
      IExpr evaledExpr = engine.evaluate(expr);

      if (data.direction() == Direction.TWO_SIDED) {
        IExpr temp = S.Limit.evalDownRule(engine, F.Limit(evaledExpr, data.rule()));
        if (temp.isPresent()) {
          return temp;
        }
      } else {
        IExpr direction =
            data.direction() == Direction.TWO_SIDED ? S.Reals : F.ZZ(data.direction().toInt());
        IExpr temp = S.Limit.evalDownRule(engine,
            F.Limit(evaledExpr, data.rule(), F.Rule(S.Direction, direction)));
        if (temp.isPresent()) {
          return temp;
        }
      }
      IExpr result = evaluateLimit(evaledExpr, data.rule(), data.direction(), engine);
      if (result.isPresent()) {
        return result;
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      // A quiet internal sub-limit must never blow up its caller: recursion-budget
      // exhaustion (e.g. RecursionLimitExceeded under the constrained L'Hopital budget)
      // simply means "could not determine" - every caller handles NIL.
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /**
   * <p>
   * Evaluate the limit of a function by evaluating the separate directions
   * <code>({@link Direction#FROM_BELOW}, {@link Direction#FROM_ABOVE}</code> for the arguments and
   * comparing the function evaluation result for equality. * @param astLimitEvaluated the limit
   * evaluation which can contain {@link S#Indeterminate} as arguments * @param astOriginal the
   * original ast with all non {@link S#Indeterminate} arguments
   * 
   * @param engine
   * @return {@link S#Indeterminate} if no limit can be found
   */
  private static IExpr evalLimitTwoSided(IExpr evaledExpr, LimitData data,
      final EvalEngine engine) {
    ISymbol symbol = data.variable();
    final IExpr limitValue = data.limitValue();
    IExpr limitAbove = evalLimit(evaledExpr,
        new LimitData(symbol, limitValue, data.rule(), Direction.FROM_ABOVE), engine);
    if (!limitAbove.isPresent() || !limitAbove.isFree(S.Limit)) {
      return F.NIL;
    }

    IExpr limitBelow = evalLimit(evaledExpr,
        new LimitData(symbol, limitValue, data.rule(), Direction.FROM_BELOW), engine);
    if (!limitBelow.isPresent() || !limitBelow.isFree(S.Limit)) {
      return F.NIL;
    }

    if (limitAbove.equals(limitBelow)) {
      if (!limitAbove.isFree(x -> x.isInterval() || x.isIntervalData(), true)) {
        return S.Indeterminate;
      }
      if (limitAbove == S.ComplexInfinity) {
        return F.NIL;
      }
      // COMPLEX PRINCIPAL BRANCH PHASE CHECK
      // If the limit diverges, Symja's real-valued auto-evaluator may have dropped
      // the complex phase of a fractional power (e.g., (-x)^(-2/3) -> x^(-2/3)).
      // We mathematically verify if the base approaches 0 from below.
      if (limitAbove.isInfinity() || limitAbove.isNegativeInfinity()
          || limitAbove.isDirectedInfinity()) {
        boolean hasComplexPhase = evaledExpr.has(expr -> {
          if (expr.isPower()) {
            IExpr exponent = expr.exponent();
            if (exponent.isFraction() || (exponent.isNumber() && !exponent.isInteger())) {
              IExpr base = expr.base();
              IExpr baseLimit = evalLimitQuiet(base,
                  new LimitData(symbol, limitValue, data.rule(), Direction.FROM_BELOW));
              if (baseLimit.isZero()) {
                return signViaApproach(base, symbol, limitValue, Direction.FROM_BELOW,
                    engine) == -1;
              }
            }
          }
          return false;
        }, true);


        if (hasComplexPhase) {
          return S.Indeterminate; // Mismatched phase rays force Indeterminate
        }
      }
      return limitAbove;
    }
    return S.Indeterminate;
  }

  /**
   * Replace every jump-discontinuous sub-expression of <code>expr</code> (see
   * {@link #stepFunctionLimit(IAST, LimitData, EvalEngine)}) by its one-sided value for the
   * direction of <code>data</code>.
   *
   * <p>
   * Only a finite real limit point is handled: at <code>&plusmn;Infinity</code> the existing
   * Abs/Sign substitutions and the Gruntz machinery keep precedence.
   *
   * @return {@link F#NIL} if nothing was rewritten
   */
  private static IExpr stepFunctionRewrite(IExpr expr, LimitData data, EvalEngine engine) {
    if (STEP_FUNCTION_REWRITE.get() || !data.limitValue().isReal()) {
      return F.NIL;
    }
    if (!expr.has(y -> y.isFunctionID(ID.Floor, ID.Ceiling, ID.Round, ID.Sign, ID.UnitStep,
        ID.IntegerPart, ID.FractionalPart, ID.Mod, ID.Quotient), true)) {
      return F.NIL;
    }
    STEP_FUNCTION_REWRITE.set(Boolean.TRUE);
    try {
      IExpr result = F.subst(expr, y -> {
        if (y.isAST() && y.head().isBuiltInSymbol() && !y.isFree(data.variable(), true)) {
          return stepFunctionLimit((IAST) y, data, engine);
        }
        return F.NIL;
      });
      if (result.isPresent() && !result.equals(expr)) {
        return result;
      }
    } finally {
      STEP_FUNCTION_REWRITE.set(Boolean.FALSE);
    }
    return F.NIL;
  }

  /**
   * Replace every {@link S#Max} / {@link S#Min} sub-expression that depends on the limit variable
   * by the argument which dominates in a neighbourhood of the limit point.
   *
   * <p>
   * <code>Max(f1, ..., fn)</code> is not merely <i>close to</i> but <b>equal</b> to its dominant
   * argument on that neighbourhood, so the substitution preserves the limit exactly. Unlike
   * {@link #stepFunctionRewrite} this is not restricted to a finite limit point - the interesting
   * cases (<code>x*Max(1/x,2/x,3/x)</code>, <code>Log(Max(E^x,E^(2*x)))/x</code>) live at Infinity,
   * where the direct substitution otherwise degenerates into <code>Infinity*0</code> and the series
   * machinery treats <code>Max</code> as an unknown differentiable function.
   *
   * @return {@link F#NIL} if nothing was rewritten
   */
  private static IExpr maxMinRewrite(IExpr expr, LimitData data, EvalEngine engine) {
    if (MAX_MIN_REWRITE.get()) {
      return F.NIL;
    }
    final ISymbol variable = data.variable();
    if (!expr.has(y -> y.isFunctionID(ID.Max, ID.Min) && !y.isFree(variable, true), true)) {
      return F.NIL;
    }
    MAX_MIN_REWRITE.set(Boolean.TRUE);
    try {
      IExpr result = F.subst(expr, y -> {
        if ((y.isAST(S.Max) || y.isAST(S.Min)) && ((IAST) y).argSize() >= 2
            && !y.isFree(variable, true)) {
          return dominantExtremum((IAST) y, data, engine);
        }
        return F.NIL;
      });
      if (result.isPresent() && !result.equals(expr)) {
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    } finally {
      MAX_MIN_REWRITE.set(Boolean.FALSE);
    }
    return F.NIL;
  }

  /**
   * The argument of a {@link S#Max} / {@link S#Min} which dominates near the limit point, found by
   * comparing the sign of the pairwise differences.
   *
   * @return {@link F#NIL} unless <b>every</b> comparison is decidable - an undecided pair means the
   *         arguments cross infinitely often (or are asymptotically indistinguishable to the sign
   *         machinery), and picking either one would be a guess
   */
  private static IExpr dominantExtremum(IAST extremum, LimitData data, EvalEngine engine) {
    final boolean isMax = extremum.isAST(S.Max);
    IExpr dominant = extremum.arg1();
    for (int i = 2; i < extremum.size(); i++) {
      IExpr candidate = extremum.get(i);
      IExpr difference = engine.evaluate(F.Subtract(candidate, dominant));
      if (difference.isZero()) {
        continue;
      }
      int sign = signAtLimitPoint(difference, data, engine);
      if (sign == 0) {
        return F.NIL;
      }
      if (isMax == (sign > 0)) {
        dominant = candidate;
      }
    }
    return dominant;
  }

  /**
   * The sign of <code>expr</code> in a neighbourhood of the limit point, on the approached side.
   * Dispatches between the two sign engines: {@link LimitGruntz#signInf} at
   * <code>&plusmn;Infinity</code> and {@link #signViaApproach} at a finite point (which returns
   * <code>0</code> for an infinite limit value by construction and cannot be used there).
   *
   * @return <code>1</code>, <code>-1</code>, or <code>0</code> when the sign is not decidable
   */
  private static int signAtLimitPoint(IExpr expr, LimitData data, EvalEngine engine) {
    IExpr limitValue = data.limitValue();
    ISymbol variable = data.variable();
    if (limitValue.isInfinity()) {
      return LimitGruntz.signInf(expr, variable, engine);
    }
    if (limitValue.isNegativeInfinity()) {
      // x -> -Infinity becomes x -> +Infinity under x -> -x, which is what signInf expects
      IExpr reflected = engine.evaluate(F.subst(expr, variable, variable.negate()));
      return LimitGruntz.signInf(reflected, variable, engine);
    }
    return signViaApproach(expr, variable, limitValue, data.direction(), engine);
  }

  /**
   * The one-sided limit of a single jump-discontinuous function application <code>f(g(x))</code>.
   *
   * <p>
   * The piecewise <b>constant</b> heads ({@link S#Floor}, {@link S#Ceiling}, {@link S#Round},
   * {@link S#Sign}, {@link S#UnitStep}) are resolved to a constant by
   * {@link #stepFunctionValue(IBuiltInSymbol, IExpr, LimitData, EvalEngine)}. The piecewise
   * <b>linear</b> heads ({@link S#IntegerPart}, {@link S#FractionalPart}, {@link S#Mod},
   * {@link S#Quotient}) are rewritten onto that constant - e.g. <code>Mod(g,m)</code> becomes
   * <code>g - m*Floor(g/m)</code> with the <code>Floor</code> already evaluated - so the remaining
   * expression is continuous and the normal machinery finishes it.
   *
   * @return {@link F#NIL} if the head isn't a supported step function or the one-sided value could
   *         not be determined
   */
  private static IExpr stepFunctionLimit(IAST step, LimitData data, EvalEngine engine) {
    IExpr head = step.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    IBuiltInSymbol symbol = (IBuiltInSymbol) head;
    switch (symbol.ordinal()) {
      case ID.Floor:
      case ID.Ceiling:
      case ID.Round:
      case ID.Sign:
      case ID.UnitStep:
        if (step.isAST1()) {
          return stepFunctionValue(symbol, step.arg1(), data, engine);
        }
        return F.NIL;
      case ID.IntegerPart:
      case ID.FractionalPart: {
        if (!step.isAST1()) {
          return F.NIL;
        }
        IExpr arg = step.arg1();
        IExpr argLimit = stepArgumentLimit(arg, data, engine);
        if (argLimit.isNIL() || !argLimit.isReal() || argLimit.isZero()) {
          // at 0 the integer part flips between Floor and Ceiling - leave it alone
          return F.NIL;
        }
        // IntegerPart truncates towards zero
        IExpr integerPart =
            stepFunctionValue(argLimit.isPositive() ? S.Floor : S.Ceiling, arg, data, engine);
        if (integerPart.isNIL()) {
          return F.NIL;
        }
        return symbol == S.IntegerPart ? integerPart
            : engine.evaluate(F.Subtract(arg, integerPart));
      }
      case ID.Mod:
      case ID.Quotient: {
        if (step.size() != 3) {
          return F.NIL;
        }
        IExpr arg = step.arg1();
        IExpr modulus = step.arg2();
        if (!modulus.isReal() || !modulus.isPositive()) {
          return F.NIL;
        }
        IExpr quotient = stepFunctionValue(S.Floor, F.Divide(arg, modulus), data, engine);
        if (quotient.isNIL()) {
          return F.NIL;
        }
        return symbol == S.Quotient ? quotient
            : engine.evaluate(F.Subtract(arg, F.Times(modulus, quotient)));
      }
      default:
        return F.NIL;
    }
  }

  /**
   * The one-sided limit of a piecewise constant <code>head(arg)</code>, where <code>head</code> is
   * one of {@link S#Floor}, {@link S#Ceiling}, {@link S#Round}, {@link S#Sign}, {@link S#UnitStep}.
   *
   * <p>
   * Away from a jump the function is continuous, so the value at the limit of <code>arg</code> is
   * the answer. At a jump the side from which <code>arg</code> approaches that value decides - note
   * that this is <b>not</b> the direction of <code>x</code>: <code>Floor(x^2+1)</code> at
   * <code>x-&gt;0</code> approaches <code>1</code> from above for both directions of <code>x</code>
   * and is therefore continuous there.
   *
   * @return {@link F#NIL} if the limit of <code>arg</code> or the approach side is unknown
   */
  private static IExpr stepFunctionValue(IBuiltInSymbol head, IExpr arg, LimitData data,
      EvalEngine engine) {
    IExpr argLimit = stepArgumentLimit(arg, data, engine);
    if (argLimit.isNIL() || !argLimit.isReal()) {
      return F.NIL;
    }
    boolean atJump;
    switch (head.ordinal()) {
      case ID.Floor:
      case ID.Ceiling:
        atJump = argLimit.isInteger();
        break;
      case ID.Round:
        // Round jumps at the half-integers
        atJump = !argLimit.isInteger() && engine.evaluate(F.Times(F.C2, argLimit)).isInteger();
        break;
      default: // Sign, UnitStep
        atJump = argLimit.isZero();
        break;
    }
    if (!atJump) {
      return engine.evaluate(F.unaryAST1(head, argLimit));
    }
    int side = approachSide(arg, argLimit, data, engine);
    if (side == 0) {
      return F.NIL;
    }
    switch (head.ordinal()) {
      case ID.Floor:
        return side > 0 ? argLimit : engine.evaluate(F.Subtract(argLimit, F.C1));
      case ID.Ceiling:
        return side > 0 ? engine.evaluate(F.Plus(argLimit, F.C1)) : argLimit;
      case ID.Round:
        return engine.evaluate(side > 0 ? F.Plus(argLimit, F.C1D2) : F.Subtract(argLimit, F.C1D2));
      case ID.Sign:
        return side > 0 ? F.C1 : F.CN1;
      default: // UnitStep
        return side > 0 ? F.C1 : F.C0;
    }
  }

  /**
   * The limit of the argument of a step function, for the direction of <code>data</code>.
   *
   * @return {@link F#NIL} if the limit isn't a value free of the limit variable
   */
  private static IExpr stepArgumentLimit(IExpr arg, LimitData data, EvalEngine engine) {
    ISymbol x = data.variable();
    if (arg.equals(x)) {
      return data.limitValue();
    }
    if (arg.isFree(x, true)) {
      return engine.evalQuiet(arg);
    }
    IExpr argLimit = evalLimitQuiet(arg, data);
    if (argLimit.isPresent() && argLimit.isFree(S.Limit, true) && argLimit.isFree(x, true)
        && argLimit.isIndeterminateFree()) {
      return engine.evalQuiet(argLimit);
    }
    return F.NIL;
  }

  /**
   * Determine whether <code>arg</code> approaches <code>argLimit</code> from above (<code>1</code>)
   * or from below (<code>-1</code>) while <code>x</code> approaches the limit point in the
   * direction of <code>data</code>.
   *
   * @return <code>0</code> if the side could not be determined
   */
  private static int approachSide(IExpr arg, IExpr argLimit, LimitData data, EvalEngine engine) {
    ISymbol x = data.variable();
    if (arg.equals(x)) {
      return data.direction() == Direction.FROM_ABOVE ? 1 : -1;
    }
    int sign =
        signViaApproach(F.Subtract(arg, argLimit), x, data.limitValue(), data.direction(), engine);
    if (sign != 0) {
      return sign;
    }
    // fall back to the sign of the first non-vanishing derivative at the limit point: for
    // arg - argLimit ~ c*(x-x0)^k the side is sign(c) from above and sign(c)*(-1)^k from below
    IExpr derivative = arg;
    for (int k = 1; k <= 3; k++) {
      derivative = engine.evalQuiet(F.D(derivative, x));
      if (derivative.isNIL() || derivative.isIndeterminate()) {
        break;
      }
      IExpr value = engine.evalQuiet(F.subst(derivative, x, data.limitValue()));
      if (!value.isReal()) {
        break;
      }
      if (!value.isZero()) {
        int side = value.isNegative() ? -1 : 1;
        if (data.direction() == Direction.FROM_BELOW && (k % 2 == 1)) {
          side = -side;
        }
        return side;
      }
    }
    return 0;
  }

  /**
   * <code>Limit[RootSum[f, g], x -> x0]</code> for a finite <code>x0</code> whose root polynomial
   * <code>f</code> is free of the limit variable <code>x</code>: the roots do not move with
   * <code>x</code>, so the summand <code>g</code> (typically <code>Log(x - #1)/...</code> from a
   * differentiated-log antiderivative) is continuous in <code>x</code> unless <code>x0</code> is
   * itself a root. The limit is therefore the direct substitution
   * <code>RootSum[f, g /. x -> x0]</code>, independent of the approach direction.
   *
   * <p>
   * If <code>x0</code> is a root, the substitution introduces a <code>Log(0)</code> term and the
   * RootSum evaluates to <code>ComplexInfinity</code>; that case is declined ({@link F#NIL}) so the
   * generic limit machinery can deal with the singular endpoint.
   *
   * @return the substituted RootSum, or {@link F#NIL} if this shortcut does not apply
   */
  private static IExpr limitRootSum(IExpr function, LimitData data, EvalEngine engine) {
    if (!function.isAST(S.RootSum, 3)) {
      return F.NIL;
    }
    if (data.limitValue().isDirectedInfinity()) {
      return F.NIL; // only finite endpoints substitute directly
    }
    final ISymbol x = data.variable();
    if (!function.first().isFree(x, true)) {
      return F.NIL; // the roots depend on x -> not a plain substitution
    }
    IExpr substituted = engine.evalQuiet(function.replaceAll(data.rule()).orElse(function));
    if (substituted.isPresent() && substituted.isFree(x, true) && substituted.isFree(S.Limit)
        && substituted.isIndeterminateFree() && substituted.isFree(S.ComplexInfinity)
        && substituted.isFree(S.DirectedInfinity)) {
      return substituted;
    }
    return F.NIL;
  }

  private static IExpr evalReplaceAll(IExpr expression, LimitData data, EvalEngine engine) {
    // Direct substitution assumes continuity at the limit point. For an UNKNOWN function
    // f(...) whose arguments contain the limit variable, continuity is not justified -
    // e.g. Limit(x*f(x)^2/(x^2+f(x)^4), x->0) depends on f (f=Sqrt gives 1/2, not 0) and
    // must stay unevaluated, exactly as the substitution x->0 would wrongly suggest 0.
    final ISymbol variable = data.variable();
    if (expression.has(e -> e.isAST() && e.head().isSymbol() && !e.head().isBuiltInSymbol()
        && !e.isFree(variable, true), true)) {
      return F.NIL;
    }
    IExpr result = expression.replaceAll(data.rule());
    if (result.isPresent()) {
      result = engine.evalQuiet(result);
      if (result.isComplexInfinity()) {
        // ComplexInfinity is what the substitution reports at a POLE - precisely the case in
        // which the continuity assumption above does NOT hold. On the real line the one-sided
        // limits at a simple pole are +Infinity and -Infinity, so the two-sided limit does not
        // exist (Gamma(z) at 0, Zeta(z) at 1, like 1/z at 0). Leave it to the machinery below,
        // which resolves the pole and compares the two sides.
        return F.NIL;
      }
      if (result.isNumericFunction(true) || result.isInfinity() || result.isNegativeInfinity()) {
        return IntervalSym.toAccumBoundsIndeterminate(result);
      }
    }
    return F.NIL;
  }

  private static IExpr evaluateLimit(IExpr function, IAST rule, Direction direction,
      final EvalEngine engine) {
    ISymbol symbol = (ISymbol) rule.arg1();
    IExpr limit = rule.arg2();
    try {
      // one traversal for the four special-function gates below (Ei/Erfc/Gamma-family)
      SpecialFunctionFeatures special = new SpecialFunctionFeatures(function, symbol);
      // ExpIntegralEi(base + shift), shift -> 0 (Ei(x - E^(-E^x))): the difference
      // Ei(base+shift) - Ei(base) is an unresolved oo - oo, resolved by the same order-2 Taylor
      // as the Gamma differences (Ei'(x) = E^x/x), thesis #64. Ei is not in the Gamma
      // special-function block below, so handle it here.
      if ((limit.isInfinity() || limit.isNegativeInfinity()) && GAMMA_POLE_SHIFT_DEPTH.get() < 3
          && special.eiShiftArg) {
        function = expandGammaShifts(function, rule, direction, engine);
      }

      // ExpIntegralEi(g) with g -> 0 at a finite limit point: Ei has a logarithmic branch point at
      // 0 (Ei(g) ~ EulerGamma + Log(g) + g + g^2/4), which Symja's Series does not provide, so
      // e.g. E^(2*Ei(-x))/x^2 at x->0 never resolves. Substitute the near-0 series (an exact
      // asymptotic equality that preserves the limit) and resolve the rewritten form via a direct
      // depth-guarded evaluateLimit, adopting only a clean result (thesis #59).
      if (!limit.isInfinity() && !limit.isNegativeInfinity() && GAMMA_POLE_SHIFT_DEPTH.get() < 3
          && special.eiVarArg) {
        IExpr expanded = expandEiNearZero(function, rule, direction, engine);
        if (expanded.isPresent() && !expanded.equals(function)) {
          int eiDepth = GAMMA_POLE_SHIFT_DEPTH.get();
          GAMMA_POLE_SHIFT_DEPTH.set(eiDepth + 1);
          try {
            IExpr eiResult = evaluateLimit(engine.evaluate(expanded), rule, direction, engine);
            if (eiResult.isPresent() && eiResult.isFree(S.Limit)
                && eiResult.isIndeterminateFree()) {
              return eiResult;
            }
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            // ignore - continue with the unexpanded function
          } finally {
            GAMMA_POLE_SHIFT_DEPTH.set(eiDepth);
          }
        }
      }

      // Erfc(z) with z -> +Infinity is exponentially small, and neither Series nor Asymptotic
      // provides its expansion at Infinity - so x*Erfc(x)*E^(x^2) never resolves (no denominator
      // for L'Hopital either) and Erfc(x+c/x)/Erfc(x) has to be reconstructed from derivatives.
      // Substitute the standard asymptotic expansion (an exact asymptotic equality that preserves
      // the limit - see expandErfcAtInfinity for the truncation order) and resolve the rewritten
      // form, adopting only a clean result.
      //
      // The rewritten form is a pure exp/log expression - Gruntz territory - and it is handed to
      // GruntzLimit FIRST, deliberately bypassing the general machinery: the expansion produces
      // shapes like rational(x)*E^(vanishing) (x/(x+1/x)*E^(-2-1/x^2) for Erfc(x+1/x)/Erfc(x)),
      // on which timesLimit and lHospitalesRule recurse into each other with an exponential
      // fan-out and never terminate. The general path stays as the fallback.
      if (ERFC_ASYMPTOTIC_DEPTH.get() < 3 && special.erfcVarArg) {
        IExpr expanded = expandErfcAtInfinity(function, rule, direction, engine);
        if (expanded.isPresent() && !expanded.equals(function)) {
          int erfcDepth = ERFC_ASYMPTOTIC_DEPTH.get();
          ERFC_ASYMPTOTIC_DEPTH.set(erfcDepth + 1);
          try {
            expanded = engine.evaluate(expanded);
            IExpr erfcResult =
                LimitGruntz.evaluateLimit(expanded, symbol, limit, direction, engine);
            if (erfcResult.isNIL() || !erfcResult.isFree(S.Limit)
                || !erfcResult.isIndeterminateFree()) {
              erfcResult = evaluateLimit(expanded, rule, direction, engine);
            }
            if (erfcResult.isPresent() && erfcResult.isFree(S.Limit)
                && erfcResult.isIndeterminateFree()) {
              return erfcResult;
            }
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            // ignore - continue with the unexpanded function
          } finally {
            ERFC_ASYMPTOTIC_DEPTH.set(erfcDepth);
          }
        }
      }

      // Dominant-term rule for a sum of exponential towers at +-Infinity: the difference E^A - E^B
      // of two towers can grind indefinitely in the mrv machinery even when E^A alone and the ratio
      // E^B/E^A both resolve in under a second (thesis #80/8.3). When one summand strictly
      // out-grows
      // all others the sum's limit is that summand's limit; adopt only a clean result, so a
      // same-order sum (genuine cancellation) falls through to the normal machinery unchanged.
      if ((limit.isInfinity() || limit.isNegativeInfinity()) && DOMINANT_TERM_DEPTH.get() < 2
          && function.isPlus() && function.argSize() >= 2
          && ((IAST) function).count(t -> hasDivergentExpFactor(t, symbol)) >= 2) {
        int domDepth = DOMINANT_TERM_DEPTH.get();
        DOMINANT_TERM_DEPTH.set(domDepth + 1);
        try {
          IExpr dominant = dominantTermLimit((IAST) function, rule, direction, engine);
          if (dominant.isPresent() && dominant.isFree(S.Limit) && dominant.isIndeterminateFree()) {
            return dominant;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // ignore - continue with the normal machinery
        } finally {
          DOMINANT_TERM_DEPTH.set(domDepth);
        }
      }

      // E^(same-order tower difference): the engine stores E^(E^p)/E^(E^q) as E^(E^p - E^q), a
      // single E^ of a Plus of two towers whose exponents differ by a vanishing amount - the mrv
      // machinery grinds on it. Linearize E^p - E^q ~ E^q*(p-q) at the exponent level and adopt
      // only
      // a clean result (thesis 8.14).
      if ((limit.isInfinity() || limit.isNegativeInfinity()) && DOMINANT_TERM_DEPTH.get() < 2
          && function.isPower() && function.base() == S.E && function.exponent().isPlus()
          && function.exponent().argSize() == 2 && ((IAST) function.exponent()).count(
              t -> t.has(p -> p.isPower() && !p.exponent().isFree(symbol, true), true)) >= 2) {
        int towerDepth = DOMINANT_TERM_DEPTH.get();
        DOMINANT_TERM_DEPTH.set(towerDepth + 1);
        try {
          IExpr reduced = reduceExpOfTowerDiff(function, rule, direction, engine);
          if (reduced.isPresent() && reduced.isFree(S.Limit) && reduced.isIndeterminateFree()) {
            return reduced;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // ignore - continue with the normal machinery
        } finally {
          DOMINANT_TERM_DEPTH.set(towerDepth);
        }
      }

      // The one-argument HarmonicNumber joins this list because FunctionExpand rewrites it to
      // EulerGamma + PolyGamma(0, n+1) - the form the whole Gamma pipeline below understands.
      // Two conditions on that:
      // - the rewrite must stay LOCAL to Limit; as an auto-evaluation it would also expand
      // HarmonicNumber(1/2), HarmonicNumber(Sqrt(2)), ... which have to stay unevaluated
      // - the generalized HarmonicNumber(n, r) is excluded: it has no digamma form, and routing
      // it through the Gamma pipeline displaces the substitution that resolves
      // Limit(HarmonicNumber(m,5), m->Infinity) to Zeta(5)
      if (special.gammaOrHarmonic) {
        function = engine.evaluate(F.FunctionExpand(function));

        // Gamma(base + shift) with a diverging base and a shift -> 0 (Gamma(x + 1/Gamma(x)),
        // Gamma(x + E^(-x))): a difference Gamma(base+shift) - Gamma(base) is an unresolved
        // oo - oo. Substitute the order-2 Taylor expansion in the shift so the leading
        // Gamma'(base)*shift = Gamma(base)*PolyGamma(0,base)*shift term surfaces for the mrv
        // machinery (thesis #70/#75/#76/#77). Shares the anti-ping-pong pole-shift budget.
        if ((limit.isInfinity() || limit.isNegativeInfinity()) && GAMMA_POLE_SHIFT_DEPTH.get() < 3
            && function.has(g -> g.isAST(S.Gamma, 2) && g.first().isPlus(), true)) {
          function = expandGammaShifts(function, rule, direction, engine);
        }

        // Shift Gamma arguments away from their poles using the exact recurrence
        // Gamma(z) = Gamma(z+m+1)/(z*(z+1)*...*(z+m)) whenever the argument's limit is a
        // non-positive integer. This turns e.g. x*Gamma(x) at x->0 into Gamma(x+1) (limit 1)
        // and x - Gamma(1/x) at x->Infinity into x - x*Gamma(1+1/x) (limit EulerGamma).
        // FunctionExpand applies the opposite rewrite Gamma(1+z) -> z*Gamma(z), so the shifted
        // form must be resolved by a direct depth-guarded evalLimit and only a clean result
        // adopted - substituting it back into `function` ping-pongs forever.
        int poleShiftDepth = GAMMA_POLE_SHIFT_DEPTH.get();
        if (poleShiftDepth < 3) {
          final LimitData poleData = new LimitData(symbol, limit, rule, direction);
          IExpr shifted = F.subst(function, sub -> {
            if (sub.isAST(S.Gamma, 2)) {
              IExpr z = sub.first();
              try {
                IExpr zLim = evalLimitQuiet(z, poleData);
                if (zLim.isInteger()) {
                  int m = -zLim.toIntDefault();
                  if (m >= 0 && m <= 12) { // pole at 0, -1, ..., -12
                    IASTAppendable den = F.TimesAlloc(m + 1);
                    for (int k = 0; k <= m; k++) {
                      den.append(F.Plus(z, F.ZZ(k)));
                    }
                    return F.Divide(F.Gamma(F.Plus(z, F.ZZ(m + 1))), den.oneIdentity1());
                  }
                }
              } catch (RuntimeException rex) {
                Errors.rethrowsInterruptException(rex);
                // fall through - leave this Gamma unshifted
              }
            }
            return F.NIL;
          });
          if (shifted.isPresent() && !shifted.equals(function)) {
            GAMMA_POLE_SHIFT_DEPTH.set(poleShiftDepth + 1);
            try {
              IExpr shiftedResult = evalLimit(engine.evaluate(shifted), poleData, engine);
              if (shiftedResult.isPresent() && shiftedResult.isFree(S.Limit)
                  && shiftedResult.isIndeterminateFree()
                  && !hasNestedDirectedInfinity(shiftedResult)) {
                return shiftedResult;
              }
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              // ignore - continue with the unshifted function
            } finally {
              GAMMA_POLE_SHIFT_DEPTH.set(poleShiftDepth);
            }
          }
        }

        // Apply Stirling's approximation exclusively for limits approaching Infinity
        if (limit.isInfinity()) {

          // A nested divergent Gamma logarithm Log(Gamma(Gamma(x))) / LogGamma(Gamma(x)) ranks with
          // its leading Stirling order Gamma(x)*Log(Gamma(x)), which already outranks E^x - but the
          // full additive expansion leaves a Gamma difference that strands the mrv machinery, and
          // the Exp(Limit(Log(...))) trick below only wraps another Log around it. Substitute the
          // pure leading product and adopt the result exclusively when it is 0 or +-Infinity, where
          // the dropped lower-order terms provably cannot change the ranking (thesis #50).
          if (GAMMA_POLE_SHIFT_DEPTH.get() < 3 && function.has(sub -> {
            IExpr g = sub.isLog() && sub.first().isAST(S.Gamma, 2) ? sub.first().first()
                : sub.isAST(S.LogGamma, 2) ? sub.first() : F.NIL;
            return g.isPresent() && isNestedGammaArg(g, symbol);
          }, true)) {
            IExpr leading = leadingLogGamma(function, symbol);
            if (leading.isPresent() && !leading.equals(function)) {
              int leadDepth = GAMMA_POLE_SHIFT_DEPTH.get();
              GAMMA_POLE_SHIFT_DEPTH.set(leadDepth + 1);
              try {
                IExpr leadingResult =
                    evaluateLimit(engine.evaluate(leading), rule, direction, engine);
                if (leadingResult.isPresent() && leadingResult.isFree(S.Limit)
                    && leadingResult.isIndeterminateFree() && (leadingResult.isInfinity()
                        || leadingResult.isNegativeInfinity() || leadingResult.isZero())) {
                  return leadingResult;
                }
              } catch (RuntimeException rex) {
                Errors.rethrowsInterruptException(rex);
                // ignore - continue with the unexpanded function
              } finally {
                GAMMA_POLE_SHIFT_DEPTH.set(leadDepth);
              }
            }
          }

          // The Exp(Limit(Log(...))) Stirling trick only helps when at least one Gamma-family
          // argument actually diverges - otherwise replaceLogStirling substitutes nothing and
          // the inner evalLimit recurses back to this very expression (infinite descent, e.g.
          // Limit(Gamma(1/x), x->Infinity)).
          final ISymbol stirlingSymbol = symbol;
          boolean stirlingApplies = function.has(sub -> {
            if (sub.isAST(S.Gamma, 2) || sub.isAST(S.LogGamma, 2) || sub.isAST(S.Factorial, 2)) {
              return divergesAtInfinity(sub.first(), stirlingSymbol);
            }
            if (sub.isAST(S.Pochhammer, 3)) {
              return divergesAtInfinity(sub.first(), stirlingSymbol)
                  || divergesAtInfinity(sub.second(), stirlingSymbol);
            }
            return false;
          }, true);

          // If the expression is primarily multiplicative, evaluate Exp(Limit(Log(expr)))
          // to completely bypass massive Gruntz exponential towers.
          if (stirlingApplies
              && (function.isTimes() || function.isPower() || function.isAST(S.Gamma, 2)
                  || function.isAST(S.Factorial, 2) || function.isAST(S.Pochhammer, 3))) {


            // Force logarithms to expand (e.g. Log(a/b) -> Log(a) - Log(b))
            IExpr logExpr = engine.evaluate(F.PowerExpand(F.Log(function)));
            if (DEBUG) {
              System.out.println("Before replaceLogStirling: " + logExpr);
            }
            // Inject additive Stirling series
            logExpr = LimitGruntz.replaceLogStirling(logExpr, symbol, engine);

            // Expand to distribute terms like (x + 1/2)*Log(x + 1/2)
            logExpr = engine.evaluate(F.ExpandAll(logExpr));

            // Re-combine logs to stabilize the rational fractions before evaluation
            logExpr = LimitGruntz.logCombine(logExpr, true, symbol);
            if (DEBUG) {
              System.out.println("After logCombine: " + logExpr);
            }
            LimitData data = new LimitData(symbol, limit, rule, direction);
            IExpr logLimit = evalLimit(logExpr, data, engine);

            // If the logarithmic limit resolved cleanly, immediately return Exp(Result)
            if (logLimit.isPresent() && logLimit.isFree(S.Limit)) {
              return engine.evaluate(F.Exp(logLimit));
            }
          }

          // --- FALLBACK: STANDARD EXPONENTIAL STIRLING ---
          // Only runs if the heuristic above was additive or failed to resolve.
          IExpr stirlingFunction = replaceStirling(function, symbol, engine);
          if (!stirlingFunction.equals(function)) {
            // Cancel trivial Sqrt(1/x)*Sqrt(x) terms generated by Stirling
            stirlingFunction = engine.evaluate(F.Simplify(stirlingFunction));
            // Re-combine logarithms into stable rational fractions
            stirlingFunction = LimitGruntz.logCombine(stirlingFunction, true, symbol);
            LimitData stirlingData = new LimitData(symbol, limit, rule, direction);
            IExpr stirlingResult = evalLimit(stirlingFunction, stirlingData, engine);
            // Adopt only clean resolutions: a truncated asymptotic series can strand the
            // evaluation somewhere WORSE than the original, so an unresolved or Indeterminate
            // result must fall through to the ORIGINAL function rather than be adopted.
            // (This guard once also caught PolyGamma's reflection formula firing on the
            // re-evaluated digamma tail and producing Cot(divergent) -> a false Indeterminate
            // on psi(psi(psi(x))). That rewrite is now gated on a wholly negated argument in
            // SpecialFunctions.PolyGamma.functionExpand, so it can no longer reach here.)
            if (stirlingResult.isPresent() && stirlingResult.isFree(S.Limit)
                && stirlingResult.isIndeterminateFree()) {
              // A constant (limit-variable-free) Stirling result can be an uncollapsed additive
              // log-sum that is actually a simpler closed form - e.g.
              // LogGamma(x+1)-LogGamma(x)-Log(x) resolves to Log(1/(2*Pi))/2+Log(2*Pi)/2, which
              // is 0. Collapse a constant result to its closed form (cheap: it is x-free).
              if (stirlingResult.isFree(symbol)) {
                IExpr simplified = engine.evaluate(F.Simplify(stirlingResult));
                if (simplified.isPresent() && simplified.isIndeterminateFree()) {
                  return simplified;
                }
              }
              return stirlingResult;
            }
            // otherwise continue below with the ORIGINAL function
          }
        }
      }

      if (direction == Direction.TWO_SIDED) {
        IExpr temp = S.Limit.evalDownRule(engine, F.Limit(function, rule));
        if (temp.isPresent()) {
          return temp;
        }
      }
      LimitData data = new LimitData(symbol, limit, rule, direction);
      IExpr result = evalLimit(function, data, engine);

      // An oo - oo cancellation at a FINITE limit point - each term has a pole, so a
      // term-by-term limit is Indeterminate - becomes a resolvable 0/0 once combined over a
      // common denominator. Concrete-exponent forms auto-combine, but a symbolic exponent does
      // not (thesis (n+1)*x^(n+1)/(x^(n+1)-1) - x/(x-1) at x->1 -> n/2). Retry once with
      // Together and reduce the correct-but-unsimplified result.
      if (result.isPresent() && result.isIndeterminate() && function.isPlus()
          && limit.isNumericFunction() && !limit.isInfinity() && !limit.isNegativeInfinity()
          && !limit.isDirectedInfinity() && !TOGETHER_LIMIT_RETRY.get()) {
        IExpr combined = engine.evalQuiet(F.Together(function));
        if (combined.isPresent() && !combined.equals(function)) {
          TOGETHER_LIMIT_RETRY.set(Boolean.TRUE);
          try {
            IExpr retry = evalLimit(combined, data, engine);
            if (retry.isPresent() && retry.isFree(S.Limit) && retry.isIndeterminateFree()) {
              return engine.evaluate(F.Simplify(retry));
            }
          } finally {
            TOGETHER_LIMIT_RETRY.set(Boolean.FALSE);
          }
        }
      }
      return result;

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(symbol, rex);
    }
    return F.NIL;
  }

  /**
   * The accumulation range of a sum of bounded oscillations
   * <code>c1*Sin(g1) + c2*Cos(g2) + ...</code> at <code>x -> +/-Infinity</code>, where every
   * <code>gi</code> diverges.
   *
   * <p>
   * Two cases, decided by whether the arguments oscillate at asymptotically <b>different</b> rates
   * (<code>Limit(gi/gj)</code> is <code>0</code> or infinite) or at the <b>same</b> rate
   * (<code>Limit(gi/gj)</code> is a finite non-zero constant):
   *
   * <ul>
   * <li>different rates - the phases are asymptotically independent, so the sum comes arbitrarily
   * close to every value of the interval-arithmetic sum of the summand ranges: the result is
   * <code>Interval({-A, A})</code> with <code>A = Sum(|ci|)</code>. Note this is an
   * <b>enclosure</b>, exactly as interval arithmetic gives it - <code>Sin(x)+Cos(x)+Sin(x^2)</code>
   * yields <code>Interval({-3,3})</code> though its true closure is
   * <code>+/-(Sqrt(2)+1)</code>.</li>
   * <li>same rate - the summands stay phase-locked and the range depends on their relative phases,
   * which this rule does not compute; the limit simply does not exist:
   * {@link S#Indeterminate}.</li>
   * </ul>
   *
   * @return {@link F#NIL} if the expression is not such a sum, or a growth comparison of the
   *         arguments could not be decided
   */
  private static IExpr oscillatingEnvelope(IExpr expr, IAST rule, Direction direction,
      EvalEngine engine) {
    if (!expr.isPlus()) {
      return F.NIL;
    }
    IExpr limitPoint = rule.arg2();
    if (!limitPoint.isInfinity() && !limitPoint.isNegativeInfinity()) {
      return F.NIL;
    }
    ISymbol x = (ISymbol) rule.arg1();
    IAST plusAST = (IAST) expr;
    java.util.List<IExpr> arguments = new java.util.ArrayList<IExpr>(plusAST.argSize());
    IExpr amplitude = F.C0;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      IExpr coefficient = F.C1;
      IExpr oscillation = F.NIL;
      if (term.isSin() || term.isCos()) {
        oscillation = term;
      } else if (term.isTimes()) {
        IAST timesAST = (IAST) term;
        IASTAppendable rest = F.TimesAlloc(timesAST.argSize());
        for (int j = 1; j < timesAST.size(); j++) {
          IExpr factor = timesAST.get(j);
          if ((factor.isSin() || factor.isCos()) && !factor.first().isFree(x, true)) {
            if (oscillation.isPresent()) {
              // a product of two oscillations has an amplitude this rule cannot read off
              return F.NIL;
            }
            oscillation = factor;
          } else {
            rest.append(factor);
          }
        }
        coefficient = engine.evaluate(rest.oneIdentity1());
      }
      if (oscillation.isNIL() || !coefficient.isFree(x, true) || !coefficient.isRealResult()) {
        return F.NIL;
      }
      // a non-diverging argument means the summand converges - not an oscillation
      IExpr argument = oscillation.first();
      IExpr argumentLimit = evaluateLimit(argument, rule, direction, engine);
      if (argumentLimit.isNIL()
          || (!argumentLimit.isInfinity() && !argumentLimit.isNegativeInfinity())) {
        return F.NIL;
      }
      arguments.add(argument);
      amplitude = engine.evaluate(F.Plus(amplitude, F.Abs(coefficient)));
    }
    if (arguments.size() < 2) {
      return F.NIL;
    }

    boolean independentRates = false;
    for (int i = 1; i < arguments.size(); i++) {
      IExpr ratio = evaluateLimit(engine.evaluate(F.Divide(arguments.get(i), arguments.get(0))),
          rule, direction, engine);
      if (ratio.isNIL() || ratio.isIndeterminate()) {
        return F.NIL; // growth comparison undecided - leave the limit to the normal machinery
      }
      if (ratio.isZero() || ratio.isInfinity() || ratio.isNegativeInfinity()
          || ratio.isDirectedInfinity()) {
        independentRates = true;
      } else if (!ratio.isRealResult()) {
        return F.NIL;
      }
    }
    if (!independentRates) {
      // phase-locked summands: the limit does not exist and this rule cannot narrow the range
      return S.Indeterminate;
    }
    if (!amplitude.isRealResult()) {
      return F.NIL;
    }
    return F.Interval(F.List(engine.evaluate(F.Negate(amplitude)), amplitude));
  }

  /**
   *
   * @param function The original function being analyzed for oscillating special functions.
   * @param symbol The variable with respect to which the limit is being taken.
   * @param limitValue The value that the variable is approaching in the limit.
   * @param data Additional data related to the limit calculation.
   * @return True if the function contains oscillating special functions, false otherwise.
   */
  private static boolean isOscillatingSpecial(final IExpr function, final ISymbol symbol,
      final IExpr limitValue, LimitData data) {
    return function.has(expr -> {
      if (expr.isAST() && expr.head().isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) expr.head()).ordinal();

        // Functions with poles at every negative integer → oscillate wildly as arg → -Infinity
        if (id == ID.Gamma || id == ID.LogGamma || id == ID.Factorial || id == ID.Factorial2
            || id == ID.PolyGamma || id == ID.Zeta) {
          IAST ast = (IAST) expr;
          IExpr arg = (id == ID.PolyGamma && ast.argSize() == 3) ? ast.arg2() : ast.arg1();
          IExpr argLim =
              evalLimitQuiet(arg, new LimitData(symbol, limitValue, data.rule(), data.direction()));
          if (argLim.isNegativeInfinity()) {
            return true;
          }
        }

        // Real-valued trig functions oscillate as their argument → ±Infinity
        if (id == ID.Sin || id == ID.Cos || id == ID.Tan || id == ID.Cot || id == ID.Sec
            || id == ID.Csc) {
          if (function.isTimes()) {
            // For Times: only oscillating if no co-factor decays to zero (undamped)
            boolean hasDampingFactor = ((IAST) function).exists(factor -> {
              if (factor.isSin() || factor.isCos()
                  || factor.isFunctionID(ID.Tan, ID.Cot, ID.Sec, ID.Csc)) {
                return false; // skip the trig term itself
              }
              IExpr factorLim = evalLimitQuiet(factor,
                  new LimitData(symbol, limitValue, data.rule(), data.direction()));
              return factorLim.isZero();
            });
            if (!hasDampingFactor) {
              IAST ast = (IAST) expr;
              IExpr arg = ast.arg1();
              IExpr argLim = evalLimitQuiet(arg,
                  new LimitData(symbol, limitValue, data.rule(), data.direction()));
              if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
                return true;
              }
            }
          } else if (function.isPlus()) {
            for (int i = 1; i < function.size(); i++) {
              IExpr term = function.get(i);
              IExpr termLimit = evalLimitQuiet(term, data);
              if (termLimit.isIndeterminate()) {
                return true;
              }
            }
          }
        }

        // NOTE: hyperbolic Sinh/Cosh/Tanh/Coth/Sech/Csch are deliberately NOT treated as
        // oscillating here. They converge or diverge monotonically, so their limits - including
        // undamped products such as 2*Cosh(x)*E^x - are determinate and are resolved by the
        // hyperbolic->exponential rewrite in evalLimit. Flagging Sinh/Cosh as Indeterminate here
        // produced wrong results and poisoned signInf/Gruntz.

        // Airy functions AiryAi and AiryBi: oscillate for real negative arguments → -Infinity
        if (id == ID.AiryAi || id == ID.AiryBi) {
          IAST ast = (IAST) expr;
          IExpr arg = ast.arg1();
          IExpr argLim =
              evalLimitQuiet(arg, new LimitData(symbol, limitValue, data.rule(), data.direction()));
          if (argLim.isNegativeInfinity()) {
            return true;
          }
        }

        // Bessel functions BesselJ and BesselY oscillate for real arguments → +Infinity
        // (they behave like damped sinusoids, but the damping goes to 0, not to a finite value)
        if (id == ID.BesselJ || id == ID.BesselY) {
          if (expr.argSize() == 2) {
            IAST ast = (IAST) expr;
            IExpr arg = ast.arg2(); // second argument is the variable argument
            IExpr argLim = evalLimitQuiet(arg,
                new LimitData(symbol, limitValue, data.rule(), data.direction()));
            if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
              return true;
            }
          }
        }

        // Struve functions StruveH and StruveL: similar oscillatory behaviour to Bessel
        if (id == ID.StruveH || id == ID.StruveL) {
          if (expr.argSize() == 2) {
            IAST ast = (IAST) expr;
            IExpr arg = ast.arg2();
            IExpr argLim = evalLimitQuiet(arg,
                new LimitData(symbol, limitValue, data.rule(), data.direction()));
            if (argLim.isInfinity() || argLim.isNegativeInfinity()) {
              return true;
            }
          }
        }
      }
      return false;
    }, true);
  }

  /**
   * Test if <code>y</code> matches pattern <code>Sqrt(_)</code> or
   * <code>Times(f1,...,Sqrt(_),...,fn)</code> * @param y * @return
   */
  private static boolean isSqrtExpression(IExpr y) {
    if (y.isTimes()) {
      return ((IAST) y).exists(x -> x.isSqrt());
    }
    return y.isSqrt();
  }

  /**
   * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">
   * Wikipedia L'Hôpital's rule</a>
   *
   * @param numerator
   * @param denominator
   * @param data the limits data definition
   * @param engine
   * @return
   */
  private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, LimitData data,
      EvalEngine engine) {
    final ISymbol x = data.variable();
    int recursionLimit = engine.getRecursionLimit();
    int currentDepth = engine.getRecursionCounter();
    // Only the OUTERMOST L'Hopital application may establish the relative recursion budget.
    // Each nested application re-reading the current depth would EXTEND the ceiling by
    // another budget-increment forever - on derivative-growing ratios (E^Gamma(x)/Gamma(x):
    // every round multiplies in Gamma*PolyGamma factors) the renewing budget let the D-tree
    // recursion run to a raw JVM StackOverflowError instead of the graceful
    // RecursionLimitExceeded below.
    boolean outermostBudget = !LHOSPITAL_BUDGET_ACTIVE.get();
    try {
      if (outermostBudget && (recursionLimit <= 0
          || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT)) {
        // Give L'Hopital a safe relative budget to prevent instant starvation inside Gruntz
        engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
        LHOSPITAL_BUDGET_ACTIVE.set(Boolean.TRUE);
      }
      engine.incRecursionCounter();
      if (data.limitValue.isInfinity() || data.limitValue.isNegativeInfinity()) {
        if (!numerator.isPower() && denominator.isPower()
            && denominator.exponent().equals(F.C1D2)) {
          IFraction frac = (IFraction) denominator.exponent();
          if (frac.numerator().isOne()) {
            IInteger exp = frac.denominator(); // == 2
            IExpr expr = engine.evalQuiet(F.Times(F.D(F.Power(numerator, exp), x),
                F.Power(F.D(denominator.base(), x), F.CN1)));
            if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
              expr = engine.evalQuiet(F.Simplify(expr));
            }
            expr = evalLimit(expr, data, engine);
            if (expr.isNumber()) {
              return F.Power(expr, frac);
            }
          }
        }
      }
      if (numerator.isPowerFraction()) {
        return lHospitalesRuleWithNumeratorRoot((IAST) numerator, denominator, data, engine);
      }
      IExpr expr =
          engine.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
      // Derivative-growing ratios (E^Gamma(x)/Gamma(x): each round multiplies in
      // Gamma*PolyGamma factors) blow up combinatorially - evaluating the grown expression
      // later triggers E^(Plus)-auto-Expand recursion deep enough for a raw
      // StackOverflowError. L'Hopital cannot converge on such shapes; refuse by size.
      if (expr.leafCount() > 2000) {
        return F.NIL;
      }
      if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
        expr = engine.evalQuiet(F.Simplify(expr));
      }
      if (expr.isFree(v -> v == S.D || v == S.Derivative, true)) {
        return evalLimit(expr, data, engine);
      }
    } catch (RecursionLimitExceeded rle) {
      //
    } finally {
      if (outermostBudget && LHOSPITAL_BUDGET_ACTIVE.get()) {
        LHOSPITAL_BUDGET_ACTIVE.set(Boolean.FALSE);
      }
      engine.setRecursionLimit(recursionLimit);
      engine.decRecursionCounter();
    }
    return F.NIL;
  }

  /**
   * The <code>numerator</code> is of the form <code>base ^ (n/root)</code>. L'hospital rule is
   * tried for <code>{base ^ n, denominator ^ root}</code> and the result returned as <code>
   * result ^ (1/root)</code>.
   *
   * @param numerator is of the form <code>Power(base,n/root)</code>
   * @param denominator
   * @param data
   * @param engine
   * @return
   */
  private static IExpr lHospitalesRuleWithNumeratorRoot(IAST numerator, IExpr denominator,
      LimitData data, EvalEngine engine) {
    // see github #230
    final ISymbol x = data.variable();
    final IFraction exponentFraction = (IFraction) numerator.exponent();
    final IInteger n = exponentFraction.numerator();
    final IInteger root = exponentFraction.denominator();
    final IExpr newNumerator = engine.evalQuiet(F.Power(numerator.base(), n));
    final IExpr newDenominator = engine.evalQuiet(F.Power(denominator, root));
    final IExpr expr =
        engine.evalQuiet(F.Times(F.D(newNumerator, x), F.Power(F.D(newDenominator, x), F.CN1)));
    // see lHospitalesRule: refuse derivative blow-ups by size
    if (expr.leafCount() > 2000) {
      return F.NIL;
    }
    final IExpr temp = evalLimit(expr, data, engine);
    if (temp.isPresent()) {
      return F.Power(temp, F.QQ(F.C1, root));
    }
    return F.NIL;
  }

  /**
   * Solve for example:<br>
   * <code>Limit(Gamma(1/t),t->Infinity) ==> Infinity</code> <br>
   * <code>Limit(Gamma(1/t),t->-Infinity)  ==> -Infinity</code>
   *
   * @param ast
   * @param data
   * @param limitValue <code>Infinity</code> or <code>-Infinity</code>
   * @return
   */
  private static IExpr limitInfinityZero(IAST ast, LimitData data, final IAST limitValue) {
    Direction direction = limitValue.isNegativeInfinity() ? //
        Direction.FROM_BELOW //
        : Direction.FROM_ABOVE;
    Direction dataDirection = data.direction();
    if (dataDirection == Direction.TWO_SIDED || dataDirection == direction) {
      int variableArgPosition = -1;
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isFree(data.variable())) {
          if (variableArgPosition == -1) {
            variableArgPosition = i;
          } else {
            // more than 1 argument contains the variable
            return F.NIL;
          }
        }
      }
      if (variableArgPosition > 0) {
        IExpr arg1 = evalLimitQuiet(ast.get(variableArgPosition), data);
        if (arg1.isZero()) {

          LimitData tempData =
              new LimitData(data.variable(), F.C0, F.Rule(data.variable(), F.C0), direction);
          return evalLimitQuiet(ast.setAtCopy(variableArgPosition, data.variable()), tempData);
        }
      }
    }
    return F.NIL;
  }

  // private static IExpr mapLimit(final IAST ast, LimitData data) {
  // return ast.mapThread(data.limit(null), 1);
  // }

  /**
   * Evaluate the limits of the arguments of the <code>function</code> and evaluate the <code>
   * function</code> with these new arguments if available.
   *
   * @param function
   * @param data the data for the limit
   * @param engine
   * @return {@link F#NIL} if evaluation wasn't successful
   */
  private static IExpr limitNumericFunctionArgs(IAST function, LimitData data, EvalEngine engine) {
    IASTMutable functionLimitArgs = F.NIL;
    for (int i = 1; i < function.size(); i++) {
      IExpr arg = function.get(i);
      if (!arg.isFree(data.variable())) {
        IExpr temp = evalLimitQuiet(arg, data);
        if (temp.isPresent() && temp.isFree(data.variable()) && temp.isNumericFunction(true)) {
          if (functionLimitArgs.isNIL()) {
            functionLimitArgs = function.copy();
          }
          functionLimitArgs.set(i, temp);
        }
      }
    }
    if (functionLimitArgs.isPresent()) {
      IExpr temp = engine.evaluate(functionLimitArgs);
      if (!temp.isIndeterminate() && !temp.isComplexInfinity()) {
        return temp;
      }
    }
    return F.NIL;
  }

  /**
   * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of
   * Rational Functions</a>
   *
   * @param numeratorPoly
   * @param denominatorPoly
   * @param symbol the variable for which to approach to the limit
   * @param limit the limit value
   * @param data the limit expression which the variable should approach to
   * @return
   */
  private static IExpr limitsInfinityOfRationalFunctions(ExprPolynomial numeratorPoly,
      ExprPolynomial denominatorPoly, ISymbol symbol, IExpr limit, LimitData data) {
    long numDegree = numeratorPoly.degree();
    long denomDegree = denominatorPoly.degree();
    if (numDegree > denomDegree) {
      // If the numerator has the highest term, then the fraction is
      // called "top-heavy". If, when you divide the numerator
      // by the denominator the resulting exponent on the variable is
      // even, then the limit (at both \infty and -\infty) is
      // \infty. If it is odd, then the limit at \infty is \infty, and the
      // limit at -\infty is -\infty.
      long oddDegree = (numDegree + denomDegree) % 2;
      if (oddDegree == 1) {
        return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
            denominatorPoly.leadingBaseCoefficient()), limit));
      } else {
        return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
            denominatorPoly.leadingBaseCoefficient()), F.CInfinity));
      }
    } else if (numDegree < denomDegree) {
      // If the denominator has the highest term, then the fraction is
      // called "bottom-heavy" and the limit (at both \infty
      // and -\infty) is zero.
      return F.C0;
    }
    // If the exponent of the highest term in the numerator matches the
    // exponent of the highest term in the denominator,
    // the limit (at both \infty and -\infty) is the ratio of the
    // coefficients of the highest terms.
    return F.Divide(numeratorPoly.leadingBaseCoefficient(),
        denominatorPoly.leadingBaseCoefficient());
  }

  /**
   * Try l'Hospitales rule for numerator and denominator expression.
   *
   * @param numerator
   * @param denominator
   * @param data the limit data definition
   * @param engine
   * @return <code>F.NIL</code> if no limit was found
   */
  private static IExpr numeratorDenominatorLimit(IExpr numerator, IExpr denominator, LimitData data,
      EvalEngine engine) {
    IExpr numValue;
    IExpr denValue;
    if (denominator.isOne() && numerator.isTimes()) {
      return data.mapLimit((IAST) numerator);
    }
    if (!denominator.isNumber() || denominator.isZero()) {
      int recursionLimit = engine.getRecursionLimit();
      int currentDepth = engine.getRecursionCounter();
      // Shares the ONE L'Hopital budget with lHospitalesRule: only the outermost holder may
      // establish it - re-extending per nesting level lets derivative-growing ratios run to
      // a raw StackOverflowError (see lHospitalesRule).
      boolean outermostBudget = !LHOSPITAL_BUDGET_ACTIVE.get();
      try {
        if (outermostBudget && (recursionLimit <= 0
            || recursionLimit > currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT)) {
          // Give L'Hopital a safe relative budget
          engine.setRecursionLimit(currentDepth + Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
          LHOSPITAL_BUDGET_ACTIVE.set(Boolean.TRUE);
        }
        IExpr result = F.NIL;
        denValue = evalLimitQuiet(denominator, data);
        if (denValue.isIndeterminate()) {
          return F.NIL;
        } else if (denValue.isZero()) {
          numValue = evalLimitQuiet(numerator, data);
          if (numValue.isZero()) {
            return lHospitalesRule(numerator, denominator, data, engine);
          }
          return F.NIL;
        } else if (denValue.isInfinity()) {
          numValue = evalLimitQuiet(numerator, data);
          if (numValue.isInfinity()) {
            return lHospitalesRule(numerator, denominator, data, engine);
          } else if (numValue.isNegativeInfinity()) {
            numerator = engine.evaluate(numerator.negate());
            numValue = evalLimitQuiet(numerator, data);
            if (numValue.isInfinity()) {
              result = lHospitalesRule(numerator, denominator, data, engine);
              if (result.isPresent()) {
                return result.negate();
              }
            }
          }
          return F.NIL;
        } else if (denValue.isNegativeInfinity()) {
          denominator = engine.evaluate(denominator.negate());
          denValue = evalLimitQuiet(denominator, data);
          if (denValue.isInfinity()) {
            numValue = evalLimitQuiet(numerator, data);
            if (numValue.isInfinity()) {
              result = lHospitalesRule(numerator, denominator, data, engine);
              if (result.isPresent()) {
                return result.negate();
              }
            } else if (numValue.isNegativeInfinity()) {
              numerator = engine.evaluate(numerator.negate());
              numValue = evalLimitQuiet(numerator, data);
              if (numValue.isInfinity()) {
                return lHospitalesRule(numerator, denominator, data, engine);
              }
            }
          }
          return F.NIL;
        }
      } catch (RecursionLimitExceeded rle) {
        engine.setRecursionLimit(recursionLimit);
      } finally {
        if (outermostBudget && LHOSPITAL_BUDGET_ACTIVE.get()) {
          LHOSPITAL_BUDGET_ACTIVE.set(Boolean.FALSE);
        }
        engine.setRecursionLimit(recursionLimit);
      }
    }
    return F.NIL;
  }

  /**
   * Rewrite a {@link S#Piecewise} condition so that evaluating it <b>at</b> the limit point answers
   * the question "does this piece govern the neighbourhood on the given side?".
   *
   * <p>
   * That is the substitution of the limit variable by <code>limit -/+ epsilon</code>, which for a
   * comparison against the limit point is a fixed table - a boundary is either relaxed <b>into</b>
   * the approached side or tightened <b>out</b> of it:
   *
   * <pre>
   * FROM_BELOW:  x &lt; c  -&gt; x &lt;= c    x &gt;= c -&gt; x &gt; c
   * FROM_ABOVE:  x &gt; c  -&gt; x &gt;= c    x &lt;= c -&gt; x &lt; c
   * both:        x == c -&gt; False     x != c -&gt; True
   * </pre>
   *
   * The remaining comparisons already evaluate correctly at the limit point and stay untouched.
   * Without the tightening half a piece like <code>x &gt;= c</code> is counted for the from-BELOW
   * limit too - it is True at <code>c</code> - although it is False everywhere strictly below
   * <code>c</code>, and a Piecewise whose two branches meet at <code>c</code> comes back
   * {@link S#Indeterminate} on both sides.
   *
   * <p>
   * Comparisons nested in {@link S#And}/{@link S#Or}/{@link S#Not} are reached by the substitution;
   * an {@link S#Inequality} is first unfolded into the conjunction it stands for.
   */
  private static IExpr boundaryCondition(IExpr condition, IExpr variable, IExpr limit,
      Direction direction) {
    return F.subst(condition, y -> {
      if (y.isAST(S.Inequality) && y.size() >= 4) {
        // Inequality(a, op1, b, op2, c) is the conjunction op1(a,b) && op2(b,c) - unfold it so
        // every comparison passes through the table below
        IAST inequality = (IAST) y;
        IASTAppendable and = F.ast(S.And, inequality.size() / 2);
        for (int i = 1; i + 2 < inequality.size(); i += 2) {
          and.append(F.binaryAST2(inequality.get(i + 1), inequality.get(i), inequality.get(i + 2)));
        }
        return boundaryCondition(and, variable, limit, direction);
      }
      if (y.isAST2()) {
        IAST comparison = (IAST) y;
        IExpr head = comparison.head();
        if (comparison.arg1().equals(limit) && comparison.arg2().equals(variable)) {
          // c < x is x > c - flip the head to look the table entry up, but note that the
          // ORIGINAL comparison is returned unchanged whenever the table has no entry
          head = flipComparison(head);
        } else if (!(comparison.arg1().equals(variable) && comparison.arg2().equals(limit))) {
          return F.NIL;
        }
        return oneSidedComparison(head, variable, limit, direction);
      }
      return F.NIL;
    });
  }

  /**
   * The one-sided form of <code>head(variable, limit)</code> for the approach direction, or
   * {@link F#NIL} if the comparison already evaluates correctly at the limit point (or isn't a
   * comparison at all). See {@link #boundaryCondition(IExpr, IExpr, IExpr, Direction)}.
   */
  private static IExpr oneSidedComparison(IExpr head, IExpr variable, IExpr limit,
      Direction direction) {
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Equal:
        // the limit point itself is never part of the punctured neighbourhood
        return S.False;
      case ID.Unequal:
        return S.True;
      case ID.Less:
        return direction == Direction.FROM_BELOW ? F.LessEqual(variable, limit) : F.NIL;
      case ID.GreaterEqual:
        return direction == Direction.FROM_BELOW ? F.Greater(variable, limit) : F.NIL;
      case ID.Greater:
        return direction == Direction.FROM_ABOVE ? F.GreaterEqual(variable, limit) : F.NIL;
      case ID.LessEqual:
        return direction == Direction.FROM_ABOVE ? F.Less(variable, limit) : F.NIL;
      default:
        return F.NIL;
    }
  }

  /**
   * The comparison head with its arguments swapped: <code>c &lt; x</code> is <code>x &gt; c</code>.
   */
  private static IExpr flipComparison(IExpr head) {
    if (head == S.Less) {
      return S.Greater;
    }
    if (head == S.LessEqual) {
      return S.GreaterEqual;
    }
    if (head == S.Greater) {
      return S.Less;
    }
    if (head == S.GreaterEqual) {
      return S.LessEqual;
    }
    return head; // Equal and Unequal are symmetric
  }

  private static IExpr piecewiseLimit(final IAST piecwiseAST, LimitData data, EvalEngine engine) {
    IExpr limit = data.limitValue();
    IExpr variable = data.variable();
    if (limit.isReal()) {
      int[] piecewiseDimension = piecwiseAST.isPiecewise();
      if (piecewiseDimension != null && piecewiseDimension[0] > 0) {
        IAST matrixOfValueConditionPairs = (IAST) piecwiseAST.first();
        IExpr defaultPiecewiseValue = piecwiseAST.second();
        IExpr limitFromBelow = F.NIL;
        IExpr limitFromAbove = F.NIL;
        for (int i = 0; i < piecewiseDimension[0]; i++) {
          IAST row = matrixOfValueConditionPairs.getAST(i + 1);
          IExpr arg1Result = row.arg1();
          IExpr arg2Comparison = row.arg2();

          IExpr tempComparison;
          if (data.direction == Direction.FROM_BELOW //
              || data.direction == Direction.TWO_SIDED) {
            tempComparison =
                boundaryCondition(arg2Comparison, variable, limit, Direction.FROM_BELOW);
            IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
            if (temp.isTrue()) {
              temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
              if (limitFromBelow.isPresent() && !limitFromBelow.equals(temp)) {
                return S.Indeterminate;
              }
              limitFromBelow = temp;
            } else if (!temp.isFalse()) {
              return F.NIL;
            }
          }

          if (data.direction == Direction.FROM_ABOVE //
              || data.direction == Direction.TWO_SIDED) {
            tempComparison =
                boundaryCondition(arg2Comparison, variable, limit, Direction.FROM_ABOVE);
            IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
            if (temp.isTrue()) {
              temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
              if (limitFromAbove.isPresent() && !limitFromAbove.equals(temp)) {
                return S.Indeterminate;
              }
              limitFromAbove = temp;
            } else if (!temp.isFalse()) {
              return F.NIL;
            }

          }
        }

        if (data.direction == Direction.FROM_BELOW) {
          if (limitFromBelow.isPresent()) {
            return limitFromBelow;
          }
          return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
        }
        if (data.direction == Direction.FROM_ABOVE) {
          if (limitFromAbove.isPresent()) {
            return limitFromAbove;
          }
          return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
        }
        if (data.direction == Direction.TWO_SIDED) {
          if (limitFromBelow.isPresent() && limitFromBelow.equals(limitFromAbove)) {
            return limitFromBelow;
          }
          if (limitFromBelow.isNIL() && limitFromAbove.isNIL()) {
            return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
          }
          return S.Indeterminate;
        }
      }
    }
    return F.NIL;
  }

  private static IExpr plusLimit(final IAST plusAST, LimitData data, EvalEngine engine) {
    // Limit[a_+b_+c_,sym->lim] ->
    // Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
    // IAST rule = data.getRule();
    IExpr limit = data.limitValue();
    if (limit.isInfinity() || limit.isNegativeInfinity()) {
      ISymbol symbol = data.variable();
      if (limit.isInfinity()) {
        // Mixed polynomial-logarithmic sums like x*Log(x) - x + Log(2*Pi)/2 - Log(x)/2
        // (typical Stirling remainders): ranking terms c*x^a*Log(x)^b lexicographically by
        // (a, b) decides the limit exactly. This MUST run before the polynomial-ring
        // heuristic below, which wrongly accepts Log(x)-coefficient sums (treating them as
        // opaque coefficients) and then reports a sign from the wrong "leading" term - e.g.
        // x*Log(x) - x + Log(2*Pi/x)/2 came back -Infinity instead of +Infinity.
        IExpr growth = plusLeadingGrowthLimit(plusAST, symbol, engine);
        if (growth.isPresent()) {
          return growth;
        }
      }
      try {
        ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
        ExprPolynomial poly = ring.create(plusAST);
        IExpr coeff = poly.leadingBaseCoefficient();
        long oddDegree = poly.degree() % 2;
        if (oddDegree == 1) {
          return evalLimitQuiet(F.Times(coeff, limit), data);
        }
        return evalLimitQuiet(F.Times(coeff, F.CInfinity), data);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    IExpr mapLimit = data.mapLimit(plusAST);
    if (mapLimit.isPresent() && !mapLimit.isIndeterminate()) {
      if (mapLimit.isFree(x -> x == S.Limit, true)) {
        IExpr temp = F.eval(mapLimit);
        if (temp.isIndeterminate() && plusAST.isPlus()) {
          int indexOf = plusAST.indexOf(x -> isSqrtExpression(x));
          if (indexOf > 0) {
            temp = timesConjugateLHospital(plusAST, indexOf, data);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      return mapLimit;
    }
    return F.NIL;
  }

  /**
   * For <code>x -> +Infinity</code>: rank the terms of a sum, each of the shape
   * <code>c * x^a * Log(x)^b</code> with an <code>x</code>-free coefficient <code>c</code>,
   * lexicographically by <code>(a, b)</code>. A divergent unique leader whose coefficient has a
   * determinate sign decides the limit. Returns {@link F#NIL} for any term outside this shape
   * (conservative - the ordinary machinery continues).
   */
  private static IExpr plusLeadingGrowthLimit(IAST plusAST, ISymbol x, EvalEngine engine) {
    try {
      return plusLeadingGrowthLimitImpl(plusAST, x, engine);
    } catch (RuntimeException rex) {
      // engine sub-evaluations (PowerExpand, sign checks) can exhaust a constrained
      // recursion budget - treat as "shape not analyzable"
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Formal additive expansion of a logarithm's argument: <code>Log(u*v^k) -> Log(u) +
   * k*Log(v)</code>, recursively. (PowerExpand does not reliably split quotient arguments like
   * <code>Log(2*Pi/x)</code>, which the growth ranker needs in <code>c*x^a*Log(x)^b</code> form.)
   */
  static IExpr logExpand(IExpr arg) {
    if (arg.isTimes()) {
      IASTAppendable plus = F.PlusAlloc(arg.argSize());
      for (int i = 1; i <= arg.argSize(); i++) {
        plus.append(logExpand(((IAST) arg).get(i)));
      }
      return plus;
    }
    if (arg.isPower()) {
      return F.Times(arg.exponent(), logExpand(arg.base()));
    }
    return F.Log(arg);
  }

  /** Splits every composite-argument Log inside <code>expr</code> additively. */
  private static IExpr splitCompositeLogs(IExpr expr, EvalEngine engine) {
    IExpr split = F.subst(expr, e -> e.isLog() && (e.first().isTimes() || e.first().isPower()) //
        ? logExpand(e.first())
        : F.NIL);
    if (split.isPresent() && !split.equals(expr)) {
      // Expand distributes coefficients over the freshly created log-sums, e.g.
      // 1/2*(Log(2)+Log(Pi)-Log(x)) -> Log(2)/2+Log(Pi)/2-Log(x)/2 (plain evaluation
      // does NOT distribute, leaving a Times(c, Plus(...)) the ranker cannot classify).
      return engine.evalQuiet(F.Expand(split));
    }
    return expr;
  }

  private static IExpr plusLeadingGrowthLimitImpl(IAST plusAST, ISymbol x, EvalEngine engine) {
    IExpr expanded = splitCompositeLogs(plusAST, engine);
    if (DEBUG) {
      System.err.println("GROWTH-RANKER in=" + plusAST + "  split=" + expanded);
    }
    IAST plus = expanded.isPlus() ? (IAST) expanded : plusAST;
    final double eps = 1.0e-9;
    double bestA = Double.NEGATIVE_INFINITY;
    double bestB = Double.NEGATIVE_INFINITY;
    double[][] exps = new double[plus.argSize()][];
    IExpr[] coeffs = new IExpr[plus.argSize()];
    for (int i = 1; i <= plus.argSize(); i++) {
      Object[] part = xLogGrowthTerm(plus.get(i), x, engine);
      if (part == null) {
        return F.NIL;
      }
      double a = ((Double) part[0]).doubleValue();
      double b = ((Double) part[1]).doubleValue();
      exps[i - 1] = new double[] {a, b};
      coeffs[i - 1] = (IExpr) part[2];
      if (a > bestA + eps || (Math.abs(a - bestA) <= eps && b > bestB + eps)) {
        bestA = a;
        bestB = b;
      }
    }
    if (bestA < eps && bestB < eps) {
      return F.NIL; // leading term does not diverge
    }
    IASTAppendable leaderSum = F.PlusAlloc(plus.argSize());
    for (int i = 0; i < exps.length; i++) {
      if (Math.abs(exps[i][0] - bestA) <= eps && Math.abs(exps[i][1] - bestB) <= eps) {
        if (coeffs[i].isNIL()) {
          // an unknown-coefficient term (Log of a divergent sum) ties the leader - give up
          return F.NIL;
        }
        leaderSum.append(coeffs[i]);
      }
    }
    IExpr coefficient = engine.evalQuiet(leaderSum.oneIdentity0());
    if (coefficient.isZero() || !coefficient.isFree(x)) {
      return F.NIL;
    }
    if (coefficient.isPositiveResult() || engine.evalQuiet(F.Greater(coefficient, F.C0)).isTrue()) {
      return F.CInfinity;
    }
    if (coefficient.isNegativeResult() || engine.evalQuiet(F.Less(coefficient, F.C0)).isTrue()) {
      return F.CNInfinity;
    }
    return F.NIL;
  }

  /**
   * Decomposes a term into <code>{a, b, c}</code> with <code>term == c * x^a * Log(x)^b</code> and
   * <code>c</code> free of <code>x</code>; <code>null</code> if the term is not of this shape.
   * Exponents must be numerically evaluable.
   */
  private static Object[] xLogGrowthTerm(IExpr term, ISymbol x, EvalEngine engine) {
    if (term.isFree(x)) {
      return new Object[] {Double.valueOf(0.0), Double.valueOf(0.0), term};
    }
    if (term.equals(x)) {
      return new Object[] {Double.valueOf(1.0), Double.valueOf(0.0), F.C1};
    }
    if (term.isLog() && term.first().equals(x)) {
      return new Object[] {Double.valueOf(0.0), Double.valueOf(1.0), F.C1};
    }
    if (term.isLog()) {
      // Log(f) for divergent f grows like a*Log(x) (or slower): rank it in the (0,1) class
      // with an UNKNOWN coefficient (F.NIL) - usable only when strictly dominated by the
      // leading term, e.g. the -Log(x*Log(x)-x+...) remainder of a Stirling expansion.
      double[] lead = plusLeaderGrowth(term.first(), x, engine);
      if (lead != null && (lead[0] > 1.0e-9 || lead[1] > 1.0e-9)) {
        return new Object[] {Double.valueOf(0.0), Double.valueOf(1.0), F.NIL};
      }
      return null;
    }
    if (term.isPower() && term.exponent().isFree(x)) {
      double e = term.exponent().evalfNaN();
      if (Double.isNaN(e)) {
        return null;
      }
      if (term.base().equals(x)) {
        return new Object[] {Double.valueOf(e), Double.valueOf(0.0), F.C1};
      }
      if (term.base().isLog() && term.base().first().equals(x)) {
        return new Object[] {Double.valueOf(0.0), Double.valueOf(e), F.C1};
      }
      return null;
    }
    if (term.isTimes()) {
      double a = 0.0;
      double b = 0.0;
      boolean unknownCoeff = false;
      IASTAppendable coeff = F.TimesAlloc(term.argSize());
      for (int i = 1; i <= term.argSize(); i++) {
        Object[] part = xLogGrowthTerm(((IAST) term).get(i), x, engine);
        if (part == null) {
          return null;
        }
        a += ((Double) part[0]).doubleValue();
        b += ((Double) part[1]).doubleValue();
        IExpr c = (IExpr) part[2];
        if (c.isNIL()) {
          unknownCoeff = true;
        } else {
          coeff.append(c);
        }
      }
      return new Object[] {Double.valueOf(a), Double.valueOf(b),
          unknownCoeff ? F.NIL : coeff.oneIdentity1()};
    }
    return null;
  }

  /**
   * Leading <code>(a, b)</code> growth of <code>f</code> in the <code>c*x^a*Log(x)^b</code> scale
   * (lexicographic max over the terms of a sum), or <code>null</code> if any term falls outside
   * that shape.
   */
  private static double[] plusLeaderGrowth(IExpr f, ISymbol x, EvalEngine engine) {
    final double eps = 1.0e-9;
    double bestA = Double.NEGATIVE_INFINITY;
    double bestB = Double.NEGATIVE_INFINITY;
    // split composite logs like Log(2*Pi/x) -> Log(2*Pi) - Log(x) so terms conform
    f = splitCompositeLogs(f, engine);
    IAST terms = f.isPlus() ? (IAST) f : F.Plus(f);
    for (int i = 1; i <= terms.argSize(); i++) {
      Object[] part = xLogGrowthTerm(terms.get(i), x, engine);
      if (part == null) {
        return null;
      }
      double a = ((Double) part[0]).doubleValue();
      double b = ((Double) part[1]).doubleValue();
      if (a > bestA + eps || (Math.abs(a - bestA) <= eps && b > bestB + eps)) {
        bestA = a;
        bestB = b;
      }
    }
    return new double[] {bestA, bestB};
  }

  /**
   * Checks if the expression structurally contains a bounded head (Sin, Cos, Tanh, ArcTan, ArcCot).
   */
  private static boolean containsBoundedHead(IExpr e) {
    if (!e.isAST()) {
      return false;
    }
    IAST ast = (IAST) e;
    IExpr head = ast.head();

    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Sin:
        case ID.Cos:
        case ID.Tanh:
        case ID.ArcTan:
        case ID.ArcCot:
          return true;
      }
    }

    for (int i = 1; i <= ast.argSize(); i++) {
      if (containsBoundedHead(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an expression that pointwise dominates the magnitude of e(x) over the reals.
   */
  private static IExpr magnitudeUpperBound(IExpr e, ISymbol x) {
    if (e.isFree(x)) {
      return F.Abs(e);
    }
    if (!e.isAST()) {
      return F.Abs(e);
    }

    IAST ast = (IAST) e;
    IExpr head = ast.head();

    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Sin:
        case ID.Cos:
        case ID.Tanh:
          return F.C1;
        case ID.ArcTan:
        case ID.ArcCot:
          return F.eval(F.Times(F.C1D2, S.Pi));
        case ID.Plus:
          IASTAppendable sum = F.PlusAlloc(ast.argSize());
          for (int i = 1; i <= ast.argSize(); i++) {
            sum.append(magnitudeUpperBound(ast.get(i), x));
          }
          return F.eval(sum);
        case ID.Times:
          IASTAppendable prod = F.TimesAlloc(ast.argSize());
          for (int i = 1; i <= ast.argSize(); i++) {
            prod.append(magnitudeUpperBound(ast.get(i), x));
          }
          return F.eval(prod);
        case ID.Power:
          if (ast.argSize() == 2) {
            IExpr base = ast.arg1();
            IExpr exp = ast.arg2();
            if (base == S.E && containsBoundedHead(exp) && exp.isAST(S.Sin, 2)) {
              return S.E;
            }
            // Generalize the bound through powers (e.g. Abs(base)^exp)
            return F.eval(F.Power(magnitudeUpperBound(base, x), exp));
          }
          break;
      }
    }
    return F.Abs(e);
  }

  /**
   * Resolves limits of vanishing magnitude-bounded expressions (e.g. envelope squeeze theorem).
   */
  private static IExpr envelopeBounded(final IExpr expr, LimitData data, EvalEngine engine) {
    if (!containsBoundedHead(expr)) {
      return F.NIL;
    }

    IExpr b = magnitudeUpperBound(expr, data.variable());

    // Prevent infinite recursion: if the bound still contains oscillating heads
    // (e.g., they were trapped inside Log or Power), the squeeze fails symbolically.
    if (b.equals(F.Abs(expr)) || containsBoundedHead(b)) {
      return F.NIL;
    }

    if (!b.isFree(data.variable())) {
      IExpr limBound = evalLimitQuiet(b, data);
      if (limBound.isZero()) {
        return F.C0;
      }
    }
    return F.NIL;
  }

  private static IExpr powerLimit(final IAST powerAST, LimitData data, EvalEngine engine) {
    IExpr base = powerAST.arg1();
    IExpr exponent = powerAST.arg2();

    // --- UNIVERSAL POWER FORM HEURISTIC ---
    // Transformation: Limit(f(x)^g(x), x->a) == Exp(Limit(g(x) * Log(f(x)), x->a))
    // MUST be at the very top to prevent 1^Infinity, 0^0, and Infinity^0 forms
    // from being prematurely evaluated to Indeterminate by naive base/exponent checks.
    if (!base.isFree(data.variable()) && !exponent.isFree(data.variable())) {
      IExpr expLogBase = engine.evaluate(F.Times(exponent, F.Log(base)));

      IExpr limitExpLog = evalLimitQuiet(expLogBase, data);

      // Fallback to Gruntz if L'Hopital returns Indeterminate or fails
      if (!limitExpLog.isIndeterminateFree() || limitExpLog.isNIL()) {
        limitExpLog = LimitGruntz.evaluateLimit(expLogBase, data.variable(), data.limitValue(),
            data.direction(), engine);
      }

      if (limitExpLog.isPresent() && !limitExpLog.isAST(S.Limit)
          && limitExpLog.isIndeterminateFree()) {
        // Use PowerExpand to cleanly format terms like E^(Log(a)/2) into Sqrt(a)
        IExpr powerResult = engine.evaluate(F.PowerExpand(F.Exp(limitExpLog)));
        // PowerExpand leaves E^(3*Log(3)) alone - collapse a constant result to its closed form
        // so (2^x+3^x)^(3/x) reports 27 rather than E^(3*Log(3))
        return powerResult.isFree(data.variable())
            ? LimitGruntz.collapseConstant(powerResult, engine)
            : powerResult;
      }
    }

    // Safely processes a^f(x) by evaluating Limit(f(x)) first.
    if (base.isFree(data.variable())) {
      if (base.isZero()) {
        IExpr limitExp = evalLimitQuiet(exponent, data);
        if (limitExp.isPositiveResult() || limitExp.isInfinity()) {
          return F.C0;
        }
        if (limitExp.isNegativeResult() || limitExp.isNegativeInfinity()) {
          return F.CComplexInfinity;
        }
        if (limitExp.isZero()) {
          return S.Indeterminate;
        }
      } else {
        IExpr limitExp = evalLimitQuiet(exponent, data);
        if (limitExp.isPresent() && !limitExp.isNIL() && limitExp.isFree(S.Indeterminate)
            && limitExp.isFree(S.Limit)) {

          // Allow Symja's native power logic to resolve forms like E^Infinity -> Infinity
          IExpr result = engine.evaluate(F.Power(base, limitExp));
          if (!result.isPower() || !result.base().equals(base)) {
            return result;
          }

          // Fallback for unresolved symbolic bases (e.g., a^Infinity -> ConditionalExpression)
          if (limitExp.isInfinity() || limitExp.isNegativeInfinity()) {
            IExpr evalLogBase = engine.evaluate(F.Log(base));
            if (evalLogBase.isNumericFunction(true)) {
              boolean isInf = limitExp.isInfinity();
              if (engine.evaluate(F.Greater(evalLogBase, F.C0)).isTrue()) {
                return isInf ? F.CInfinity : F.C0;
              }
              if (engine.evaluate(F.Less(evalLogBase, F.C0)).isTrue()) {
                return isInf ? F.C0 : F.CInfinity;
              }
              if (engine.evaluate(F.Equal(evalLogBase, F.C0)).isTrue()) {
                return F.C1;
              }
            } else {
              if (limitExp.isNegativeInfinity()) {
                return F.ConditionalExpression(F.CInfinity, F.Less(evalLogBase, F.C0));
              } else {
                return F.ConditionalExpression(F.CInfinity, F.Greater(evalLogBase, F.C0));
              }
            }
          }
          return result;
        }
      }
    }

    if (exponent.isFree(data.variable())) {
      final IExpr temp = evalLimitQuiet(base, data);
      if (temp.isPresent()) {
        if (temp.isZero()) {
          if (!exponent.isNumericFunction(true)) {
            // ConditionalExpression(0, exponent > 0)
            return F.ConditionalExpression(F.C0, F.Greater(exponent, F.C0));
          }
        } else {
          if (temp.isFree(data.variable())) {
            // ConditionalExpression(0, exponent > 0)
            return F.Power(temp, exponent);
          }
        }
      }
      if (base.isTimes()) {
        IAST isFreeResult =
            base.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
        // Only factor out a constant part when there genuinely is one (arg1 != 1) and a
        // variable-dependent part remains (arg2 != 1). If the base is entirely
        // variable-dependent, arg2 equals the whole base and recursing on
        // Power(arg2, exponent) would loop forever (issue #1420).
        if (!isFreeResult.arg1().isOne() && !isFreeResult.arg2().isOne()) {
          IExpr innerLimit = data.limit(F.Power(isFreeResult.arg2(), exponent));
          if (innerLimit.isNIL()) {
            return F.NIL; // never embed the NIL sentinel into a result AST
          }
          return F.Times(F.Power(isFreeResult.arg1(), exponent), innerLimit);
        }
      }
    }
    if (exponent.isNumericFunction(true)) {
      // Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
      // IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
      IExpr temp = evalLimitQuiet(base, data);
      if (temp.isNumericFunction(true)) {
        if (temp.isZero()) {
          if (exponent.isPositive()) {
            // 0 ^ (positve exponent)
            return F.C0;
          }
          if (exponent.isNegative()) {
            // 0 ^ (negative exponent)
            if (exponent.isInteger()) {
              IInteger n = (IInteger) exponent;
              if (n.isEven()) {
                return F.CInfinity;
              }
              if (data.direction() == Direction.TWO_SIDED) {
                return S.Indeterminate;
              } else if (data.direction() == Direction.FROM_ABOVE
                  || data.direction() == Direction.FROM_BELOW) {
                return directedInfinityForZeroBase(base, exponent, data, engine);
              }

            } else if (exponent.isFraction() || (exponent.isNumber() && !exponent.isInteger())) {
              if (data.direction() == Direction.TWO_SIDED) {
                return S.Indeterminate;
              } else if (data.direction() == Direction.FROM_ABOVE
                  || data.direction() == Direction.FROM_BELOW) {
                return directedInfinityForZeroBase(base, exponent, data, engine);
              }
            }
          }
          return F.NIL;
        }
        return F.Power(temp, exponent);
      }
      if (temp.isNIL()) {
        temp = base;
      }
      if (exponent.isInteger()) {
        IInteger n = (IInteger) exponent;
        if (temp.isInfinity()) {
          if (n.isPositive()) {
            return temp;
          } else if (n.isNegative()) {
            return F.C0;
          }
          return F.NIL;
        } else if (temp.isNegativeInfinity()) {
          if (n.isPositive()) {
            if (n.isEven()) {
              return F.CInfinity;
            } else {
              return F.CNInfinity;
            }
          } else if (n.isNegative()) {
            return F.C0;
          }
          return F.NIL;
        } else if (temp.isIndeterminate() || temp.isAST(S.Limit)) {
          return F.NIL;
        }
        if (n.isPositive()) {
          return F.Power(temp, n);
        } else if (n.isNegative() && n.isEven()) {
          return F.Power(temp, n);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Replace a nested divergent Gamma logarithm <code>Log(Gamma(g))</code> /
   * <code>LogGamma(g)</code> by its leading Stirling order <code>g*Log(g)</code> (a single product
   * term), for <code>g</code> a composite divergent argument - not the bare limit variable, whose
   * <code>Log(Gamma(x))</code> the normal machinery already ranks. The dropped lower-order terms
   * (<code>-g</code>, <code>(1/2)*Log(2*Pi/g)</code>, ...) are why the caller must only adopt a
   * <code>0</code> / <code>+-Infinity</code> result: the full additive expansion
   * <code>Gamma(x)*Log(Gamma(x)) - Gamma(x)</code> divided by <code>E^x</code> strands the mrv
   * machinery on an <code>oo - oo</code>, while the pure leading product resolves (thesis #50,
   * <code>Log(Gamma(Gamma(x)))/E^x -> Infinity</code>).
   */
  private static IExpr leadingLogGamma(IExpr function, ISymbol x) {
    return F.subst(function, sub -> {
      IExpr g = F.NIL;
      if (sub.isLog() && sub.first().isAST(S.Gamma, 2)) {
        g = sub.first().first();
      } else if (sub.isAST(S.LogGamma, 2)) {
        g = sub.first();
      }
      if (g.isPresent() && isNestedGammaArg(g, x)) {
        return F.Times(g, F.Log(g));
      }
      return F.NIL;
    });
  }

  /**
   * True if <code>g</code> is a legal argument for the leading-order {@link #leadingLogGamma}
   * rewrite: divergent and itself built from a Gamma-family function (e.g. <code>Gamma(x)</code>),
   * so it grows super-polynomially. A merely affine divergent argument like <code>x + 1</code> is
   * rejected: <code>LogGamma(x+1) - LogGamma(x) - Log(x) -> 0</code> depends on the very
   * lower-order terms the leading-order rewrite discards, and expanding only one side of the
   * difference would fabricate an Infinity. A Gamma-family argument is instead too large to cancel
   * against anything but an identical copy (which the rewrite reproduces identically).
   */
  private static boolean isNestedGammaArg(IExpr g, ISymbol x) {
    return !g.equals(x) && !g.isFree(x, true)
        && g.has(t -> t.isFunctionID(ID.Gamma, ID.LogGamma, ID.Factorial, ID.Pochhammer), true)
        && divergesAtInfinity(g, x);
  }

  /**
   * The non-vanishing leading part of a digamma argument <code>z</code>, for the correction tail
   * <code>1/(2z)</code> of <code>PolyGamma(0,z) ~ Log(z) - 1/(2z)</code>. Summands of
   * <code>z</code> that vanish as <code>x -> Infinity</code> are dropped, so the tail uses
   * <code>z</code>'s leading asymptotic. When <code>z</code> is itself a digamma expansion
   * (<code>psi(psi(x))</code> recurses innermost-out to <code>z = Log(x) - 1/(2x)</code>), keeping
   * the vanishing <code>-1/(2x)</code> in the <code>1/(2z)</code> denominator yields
   * <code>1/(2*(Log(x) - 1/(2x)))</code>, whose second-order cancellation through two E-levels
   * defeats the fixed-order Puiseux machinery and times out (thesis #71); the clean
   * <code>1/(2*Log(x))</code> tail resolves. Sound because <code>1/(2z) -> 0</code> is already a
   * correction and z's dropped part is negligible relative to its divergent leading term - it can
   * never change a correctly-computed limit. The principal <code>Log(z)</code> term keeps the full
   * <code>z</code> except on a psi-in-psi tower - see {@link #digammaPrincipalArg}.
   */
  static IExpr digammaTailArg(IExpr z, ISymbol x, EvalEngine engine) {
    if (!z.isPlus()) {
      return z;
    }
    IAST plus = (IAST) z;
    IASTAppendable kept = F.PlusAlloc(plus.argSize());
    for (int i = 1; i < plus.size(); i++) {
      IExpr term = plus.get(i);
      if (divergesAtInfinity(term, x) || divergesAtInfinity(term.negate(), x)
          || (term.isFree(x, true) && !term.isZero())) {
        kept.append(term);
      }
    }
    return kept.argSize() == 0 ? z : engine.evaluate(kept);
  }

  /**
   * The argument for the principal <code>Log(z)</code> term of
   * <code>PolyGamma(0,z) ~ Log(z) - 1/(2z) - 1/(12z^2)</code>. Normally the full <code>z</code>:
   * unlike in the <code>1/(2z)</code> tail, z's vanishing summands still contribute there -
   * <code>Log(x + 1/x) = Log(x) + 1/x^2 - ...</code> carries the <code>1/x^2</code> that makes
   * <code>x^2*(psi(x+1/x) - Log(x) + 1/(2x)) -> 11/12</code>.
   * <p>
   * The exception is a psi-in-psi tower, where <code>rawArg</code> was itself a
   * <code>PolyGamma</code> and the recursion already replaced it by a truncated digamma expansion
   * (<code>psi(psi(x))</code> arrives here with <code>z = Log(x) - 1/(2x) - 1/(12x^2)</code>).
   * Carrying that noise into <code>Log(z)</code> leaves a factor
   * <code>Log(x) - 1/(2x) - 1/(12x^2)</code> where a clean <code>Log(x)</code> belongs, and the mrv
   * rewrite of the surrounding <code>E^E^</code> tower never converges - the heuristic fallback
   * then burns in L'Hopital recursion until it overflows the stack
   * (<code>E^(E^psi(psi(x)))/x -> 1/Sqrt(E)</code>). Expanding around z's leading part instead
   * drops a correction of order <code>1/(x*Log(x))</code>, strictly below the
   * <code>1/(2*Log(x))</code> tail the expansion still carries, so the truncation stays consistent
   * to its own order - and the limit resolves. Only a leading part that still diverges is
   * substituted; anything else keeps the full <code>z</code>.
   */
  static IExpr digammaPrincipalArg(IExpr z, IExpr rawArg, ISymbol x, EvalEngine engine) {
    if (rawArg.isFree(t -> t.isAST(S.PolyGamma), true)) {
      return z;
    }
    IExpr lead = digammaTailArg(z, x, engine);
    return !lead.equals(z) && divergesAtInfinity(lead, x) ? lead : z;
  }

  /**
   * Substitute the near-0 series <code>ExpIntegralEi(g) ~ EulerGamma + Log(g) + g + g^2/4</code>
   * for every <code>ExpIntegralEi(g)</code> whose argument <code>g -> 0</code> at the limit point.
   * Ei has a logarithmic branch point at 0, so this is not a plain Taylor series and Symja's
   * <code>Series</code>/<code>FunctionExpand</code> leave it unexpanded - yet the closed form is an
   * exact asymptotic equality there, and the truncation error <code>O(g^3) -> 0</code> preserves
   * the limit (thesis #59: <code>E^(2*Ei(-x))/x^2 -> E^(2*EulerGamma)</code> at <code>x->0</code>).
   * Only arguments with limit exactly 0 are touched; any other Ei is left intact.
   */
  private static IExpr expandEiNearZero(IExpr function, IAST rule, Direction direction,
      EvalEngine engine) {
    final LimitData data = new LimitData((ISymbol) rule.arg1(), rule.arg2(), rule, direction);
    return F.subst(function, sub -> {
      if (sub.isAST(S.ExpIntegralEi, 2) && !sub.first().isFree(data.variable(), true)) {
        IExpr g = sub.first();
        try {
          if (evalLimitQuiet(g, data).isZero()) {
            return F.Plus(S.EulerGamma, F.Log(g), g, F.Times(F.C1D4, F.Sqr(g)));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // leave this Ei unexpanded
        }
      }
      return F.NIL;
    });
  }

  /**
   * Substitute the asymptotic expansion
   * <code>Erfc(z) ~ E^(-z^2)/(z*Sqrt(Pi)) * (1 - 1/(2*z^2))</code> for every <code>Erfc(z)</code>
   * whose argument diverges to <code>+Infinity</code> at the limit point. Only a
   * <code>+Infinity</code> argument qualifies - on the other branch <code>Erfc</code> tends to
   * <code>2</code> and the expansion does not hold.
   *
   * <p>
   * The relation is an exact asymptotic equality there, so the limit is preserved. One correction
   * term is carried rather than the leading one alone, so that a limit probing the
   * <code>1/z^2</code> order (<code>x^2*(Sqrt(Pi)*x*E^(x^2)*Erfc(x) - 1) -> -1/2</code>) still
   * resolves correctly. Like the truncations in {@link #expandEiNearZero} and
   * {@link #replaceStirling}, the series stops there: a limit probing the <code>1/z^4</code> order
   * collapses to <code>0</code>. The next term costs about 20x - the degree-4 rational factor it
   * introduces turns the Gruntz series expansion into repeated Expand of large polynomials
   * (measured: 24s vs 1s for <code>Erfc(x+1/x)/Erfc(x)</code>) - so raising the order is only worth
   * it if a case actually needs it.
   *
   * @return {@link F#NIL} if no <code>Erfc</code> was expanded
   */
  private static IExpr expandErfcAtInfinity(IExpr function, IAST rule, Direction direction,
      EvalEngine engine) {
    final LimitData data = new LimitData((ISymbol) rule.arg1(), rule.arg2(), rule, direction);
    return F.subst(function, sub -> {
      if (sub.isAST(S.Erfc, 2) && !sub.first().isFree(data.variable(), true)) {
        IExpr z = sub.first();
        try {
          if (evalLimitQuiet(z, data).isInfinity()) {
            // The square MUST be expanded: an unexpanded E^(-(x+1/x)^2) does not cancel against
            // the E^(x^2) of a neighbouring factor, and the surviving exponential tower grinds
            // in the mrv machinery for minutes. Expanded, -(x^2+2+1/x^2) cancels term by term.
            IExpr zSqr = engine.evaluate(F.ExpandAll(F.Sqr(z)));
            return F.Times(F.Power(F.Times(z, F.Sqrt(S.Pi)), F.CN1), F.Exp(F.Negate(zSqr)), //
                F.Plus(F.C1, F.Negate(F.Divide(F.C1, F.Times(F.C2, zSqr)))));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // leave this Erfc unexpanded
        }
      }
      return F.NIL;
    });
  }

  /**
   * True if <code>term</code> contains an exponential factor <code>E^(exponent)</code> whose
   * exponent is not free of the limit variable - the tower signature whose difference grinds in the
   * mrv machinery.
   */
  private static boolean hasDivergentExpFactor(IExpr term, ISymbol x) {
    return term.has(p -> p.isPower() && p.base() == S.E && !p.exponent().isFree(x, true),
        true);
  }

  /**
   * Dominant-term rule for a sum at <code>+-Infinity</code>: if one summand <code>t_k</code>
   * strictly out-grows every other (<code>Limit(t_j / t_k) == 0</code> for all
   * <code>j != k</code>), then <code>Limit(Sum t_i) == Limit(t_k)</code>, since
   * <code>Sum t_i = t_k*(1 + Sum_{j!=k} t_j/t_k) -> t_k*(1 + 0)</code>. Ranks via RATIO limits,
   * which resolve for exponential towers, instead of the divergent difference the mrv machinery
   * grinds on: <code>E^(E^(x-E^-x)/(1-1/x)) - E^(E^x)</code> times out as a Plus, yet the dominant
   * tower alone and the ratio <code>E^(E^x)/E^(E^(...))</code> each resolve in under a second
   * (thesis #80/8.3). Returns {@link F#NIL} when no summand strictly dominates - a same-order sum
   * (genuine cancellation) is left to the normal machinery.
   */
  private static IExpr dominantTermLimit(IAST plus, IAST rule, Direction direction,
      EvalEngine engine) {
    final ISymbol symbol = (ISymbol) rule.arg1();
    final int n = plus.argSize();
    // Classify every summand up front: a clean exponential yields its exponent; a summand with NO
    // x-dependent exponential is sub-exponential (NIL exponent, poly-bounded). A "messy"
    // exponential - an E^(...) buried in a denominator or Plus, e.g. -2*E^(2u)/(1+E^u) whose true
    // order is E^u not E^(2u) - cannot be ranked by a bare exponent, so abort the whole rule rather
    // than misjudge it (thesis 8.11 regression: that term cancels 2*E^u, leaving u -> +Infinity).
    IExpr[] exps = new IExpr[n + 1];
    for (int i = 1; i <= n; i++) {
      exps[i] = divergentExpExponent(plus.get(i), symbol);
      if (exps[i].isNIL() && hasDivergentExpFactor(plus.get(i), symbol)) {
        return F.NIL;
      }
    }
    for (int k = 1; k <= n; k++) {
      IExpr pk = exps[k];
      if (pk.isNIL()) {
        continue; // only a clean exponential can strictly out-grow every other summand
      }
      boolean dominates = true;
      for (int j = 1; j <= n && dominates; j++) {
        if (j == k) {
          continue;
        }
        try {
          if (exps[j].isPresent()) {
            // both exponential: E^pk >> E^pj iff pk - pj -> +Infinity. Rank via the EXPONENT
            // difference, which resolves fast, not the ratio E^(pk-pj) whose exp-of-a-tower-
            // difference re-enters the grinding mrv rewrite (thesis #80).
            IExpr d =
                evaluateLimit(engine.evaluate(F.Subtract(pk, exps[j])), rule, direction, engine);
            dominates = d.isPresent() && d.isInfinity();
          } else {
            // exps[j] is sub-exponential (poly/log): E^pk beats it iff pk -> +Infinity
            IExpr pkLimit = evaluateLimit(pk, rule, direction, engine);
            dominates = pkLimit.isPresent() && pkLimit.isInfinity();
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          dominates = false;
        }
      }
      if (dominates) {
        return evaluateLimit(plus.get(k), rule, direction, engine);
      }
    }
    return F.NIL;
  }

  /**
   * The exponent <code>P</code> if <code>term</code> is a <em>clean</em> exponential
   * <code>coeff * E^P</code> with an <code>x</code>-dependent <code>P</code> and a coefficient free
   * of any other <code>x</code>-dependent exponential, else {@link F#NIL}. The cleanliness check is
   * essential: <code>-2*E^(2u)/(1+E^u)</code> has a bare exponent <code>2u</code> but an
   * <code>E^u</code> in its denominator drops its true order to <code>E^u</code>, so ranking it by
   * <code>2u</code> is wrong (thesis 8.11). A polynomially-bounded coefficient (<code>x^5</code>)
   * is fine - it does not change the exponential order.
   */
  private static IExpr divergentExpExponent(IExpr term, ISymbol x) {
    if (term.isPower() && term.base() == S.E && !term.exponent().isFree(x, true)) {
      return term.exponent();
    }
    if (term.isTimes()) {
      IAST times = (IAST) term;
      IExpr exponent = F.NIL;
      for (int i = 1; i < times.size(); i++) {
        IExpr f = times.get(i);
        if (f.isPower() && f.base() == S.E && !f.exponent().isFree(x, true)) {
          if (exponent.isPresent()) {
            return F.NIL; // two E^(x-dependent) factors - not a single clean exponential
          }
          exponent = f.exponent();
        } else if (hasDivergentExpFactor(f, x)) {
          return F.NIL; // an x-dependent exponential hidden in another factor (denominator, ...)
        }
      }
      return exponent;
    }
    return F.NIL;
  }

  /**
   * Asymptotically expand <code>Log(polynomial)</code> at <code>x -> Infinity</code> by factoring
   * the leading monomial:
   * <code>Log(c*x^n + rest) = n*Log(x) + Log(c) + Log(1 + rest/(c*x^n))</code>, and replace the
   * vanishing <code>Log(1 + u)</code> by its leading term <code>u = rest/(c*x^n) -> 0</code> so it
   * reads as a clean vanishing power. Symja leaves <code>Log</code> of a sum unexpanded
   * (<code>PowerExpand</code> and <code>Series</code>-at-Infinity are both no-ops) and returns
   * Indeterminate for the resulting <code>Log(x^5+x) - 5*Log(x) = Log(1+x^-4)</code> times a
   * divergent factor (a <code>0*Infinity</code>); emitting <code>x^-4</code> instead lets the
   * same-order cancellation resolve (thesis 8.14). Only used inside {@link #reduceExpOfTowerDiff},
   * where a leading-order tail is sufficient (the dropped <code>u^2/2</code> is lower order and
   * cannot change the Infinity/0 verdict). Only <code>Log</code> of a polynomial <code>Plus</code>
   * of degree &gt;= 1 is rewritten.
   */
  private static IExpr expandLogOfPolynomial(IExpr expr, ISymbol x, EvalEngine engine) {
    return F.subst(expr, sub -> {
      if (sub.isLog() && sub.first().isPlus() && sub.first().isPolynomial(x)) {
        IExpr poly = sub.first();
        try {
          IExpr degree = engine.evaluate(F.Exponent(poly, x));
          if (degree.isInteger() && degree.isPositive()) {
            IExpr lead = engine.evaluate(F.Coefficient(poly, x, degree));
            IExpr leadMonomial = F.Times(lead, F.Power(x, degree));
            // u = rest/(c*x^n) = poly/(c*x^n) - 1 -> 0; Log(1+u) ~ u
            IExpr u = engine.evaluate(F.Subtract(F.Divide(poly, leadMonomial), F.C1));
            return engine.evaluate(F.Plus(F.Times(degree, F.Log(x)), F.Log(lead), u));
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }
      return F.NIL;
    });
  }

  /** -1 if <code>t</code> carries an explicit negative numeric sign, else +1. */
  private static int termSign(IExpr t) {
    if (t.isTimes() && ((IAST) t).arg1().isNegative()) {
      return -1;
    }
    return t.isNegative() ? -1 : 1;
  }

  /**
   * Reduce <code>E^(g)</code> where the exponent <code>g</code> is a same-order difference of two
   * super-polynomially divergent tower terms, at <code>+-Infinity</code>. The engine stores
   * <code>E^(E^p)/E^(E^q)</code> as <code>E^(E^p - E^q)</code> and <code>E^(a*Log(b))</code> as
   * <code>b^a</code>, so thesis 8.14's body is
   * <code>E^((x+x^5)^(2*Log(Log x)) - E^(10*Log(x)*Log(Log x)))</code> - a single <code>E^</code>
   * of a same-order tower difference the mrv machinery grinds on. Rank the two summands by their
   * log-magnitudes <code>l1=Log|t1|</code>, <code>l2=Log|t2|</code> (uniform across
   * <code>b^a</code> and <code>E^p</code> forms, with <code>Log</code> of a polynomial expanded);
   * when they are the same order (<code>l1-l2 -> 0</code>) linearize
   * <code>E^l1 - E^l2 ~ E^l2*(l1-l2)</code>, turning <code>E^(g)</code> into the resolvable
   * <code>E^(s1*E^l2*(l1-l2))</code>. Returns {@link F#NIL} unless the result is clean (the caller
   * adopts only then).
   */
  private static IExpr reduceExpOfTowerDiff(IExpr function, IAST rule, Direction direction,
      EvalEngine engine) {
    final ISymbol x = (ISymbol) rule.arg1();
    IAST g = (IAST) function.exponent();
    IExpr t1 = g.arg1();
    IExpr t2 = g.arg2();
    int s1 = termSign(t1);
    if (s1 == termSign(t2)) {
      return F.NIL; // a sum, not a difference: no leading cancellation to linearize
    }
    IExpr m1 = s1 < 0 ? t1.negate() : t1;
    IExpr m2 = s1 < 0 ? t2 : t2.negate();
    IExpr l1 = expandLogOfPolynomial(engine.evaluate(F.PowerExpand(F.Log(m1))), x, engine);
    IExpr l2 = expandLogOfPolynomial(engine.evaluate(F.PowerExpand(F.Log(m2))), x, engine);
    if (!divergesAtInfinity(l1, x) || !divergesAtInfinity(l2, x)) {
      return F.NIL; // not two super-polynomial towers
    }
    // Same order iff l1 - l2 -> 0. With the leading-order Log-tail (a vanishing power) this is a
    // clean power-vs-log limit, not the 0*Infinity the heuristic returns Indeterminate for.
    IExpr d = engine
        .evaluate(evaluateLimit(engine.evaluate(F.Subtract(l1, l2)), rule, direction, engine));
    if (!(d.isPresent() && d.isZero())) {
      return F.NIL; // strict dominance / non-zero gap: no vanishing leading cancellation
    }
    // g = s1*(m1 - m2) = s1*(E^l1 - E^l2) ~ s1*E^l2*(l1 - l2)
    IExpr reducedExponent = engine.evaluate(F.Times(F.ZZ(s1), F.Exp(l2), F.Subtract(l1, l2)));
    return evaluateLimit(engine.evaluate(F.Exp(reducedExponent)), rule, direction, engine);
  }

  /**
   * True if <code>expr -> +Infinity</code> as <code>x -> Infinity</code>. Stirling's approximation
   * of the Gamma family is only valid for a divergent (large positive) argument - substituting it
   * for e.g. <code>Gamma(1/7 + 1/x)</code> (argument limit 1/7) produces a wrong closed form.
   * Conservative: any evaluation failure counts as "not divergent" (less simplification, never a
   * wrong substitution).
   */
  static boolean divergesAtInfinity(IExpr expr, ISymbol x) {
    if (expr.isFree(x)) {
      return false;
    }
    if (expr.equals(x)) {
      return true; // the overwhelmingly common Gamma(x) case, without engine recursion
    }
    try {
      LimitData data = new LimitData(x, F.CInfinity, F.Rule(x, F.CInfinity), Direction.FROM_BELOW);
      IExpr lim = evalLimitQuiet(expr, data);
      return lim.isInfinity();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * Replaces each {@code f(base + shift)} subexpression - for {@code f} in
   * {@code {Gamma, ExpIntegralEi}} - whose base diverges and whose shift tends to 0 with its
   * order-2 Taylor series in the shift. A difference {@code f(base + shift) - f(base)} is otherwise
   * an unresolved {@code oo - oo}; the expansion surfaces the leading {@code f'(base)*shift} term
   * so the mrv machinery can rank it (thesis Gamma set #70/#75/#76/#77; ExpIntegralEi #64 with
   * {@code Ei'(x) = E^x/x}). Order 2 also captures the second-order tail #75/#76 need. Only fires
   * when the shift genuinely vanishes, so it never touches an ordinary {@code f(x)} or a
   * diverging-argument call.
   */
  private static IExpr expandGammaShifts(IExpr function, IAST rule, Direction direction,
      EvalEngine engine) {
    ISymbol symbol = (ISymbol) rule.arg1();
    IExpr limit = rule.arg2();
    final LimitData data = new LimitData(symbol, limit, rule, direction);
    IExpr expanded = F.subst(function, sub -> {
      if (!(sub.isAST(S.Gamma, 2) || sub.isAST(S.ExpIntegralEi, 2)) || !sub.first().isPlus()) {
        return F.NIL;
      }
      IAST arg = (IAST) sub.first();
      IASTAppendable baseParts = F.PlusAlloc(arg.size());
      IASTAppendable shiftParts = F.PlusAlloc(arg.size());
      for (int i = 1; i < arg.size(); i++) {
        IExpr term = arg.get(i);
        IExpr tLim = evalLimitQuiet(term, data);
        if (tLim.isPresent() && tLim.isZero()) {
          shiftParts.append(term);
        } else {
          baseParts.append(term);
        }
      }
      if (shiftParts.isEmpty() || baseParts.isEmpty()) {
        return F.NIL; // no vanishing shift, or nothing left as base
      }
      IExpr base = baseParts.oneIdentity0();
      IExpr shift = shiftParts.oneIdentity0();
      // the base must diverge - the Taylor around an analytic point is only valid there
      IExpr baseLim = evalLimitQuiet(base, data);
      if (!(baseLim.isInfinity() || baseLim.isNegativeInfinity())) {
        return F.NIL;
      }
      // order-2 Taylor of f(base + d) in d, then substitute d -> shift
      ISymbol d = F.Dummy("gd");
      IExpr series = engine.evalQuiet(
          F.Normal(F.Series(F.unaryAST1(sub.head(), F.Plus(base, d)), F.List(d, F.C0, F.C2))));
      if (series.isPresent() && series.isFree(S.Series) && series.isFree(S.O)
          && series.isIndeterminateFree() && !series.equals(sub)) {
        return engine.evaluate(F.subst(series, d, shift));
      }
      return F.NIL;
    });
    return expanded.isPresent() ? expanded : function;
  }

  private static IExpr replaceStirling(IExpr expr, ISymbol x, EvalEngine engine) {
    if (expr.isFree(x)) {
      return expr;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      switch (ast.validHeadID()) {
        case ID.Factorial: {
          // x! -> Gamma(x+1)
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          return replaceStirling(engine.evaluate(F.Gamma(F.Plus(arg, F.C1))), x, engine);
        }
        case ID.Pochhammer: {
          // Pochhammer(a, b) -> Gamma(a+b)/Gamma(a)
          IExpr a = replaceStirling(ast.arg1(), x, engine);
          IExpr b = replaceStirling(ast.arg2(), x, engine);
          return replaceStirling(engine.evaluate(F.Divide(F.Gamma(F.Plus(a, b)), F.Gamma(a))), x,
              engine);
        }
        case ID.Gamma: {
          // Stirling's Approximation maps Gamma growth strictly to Exp and Log
          // Gamma(z) ~ Sqrt(2*Pi/z) * Exp(z*Log(z) - z + 1/(12*z))
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          if (!divergesAtInfinity(arg, x)) {
            return F.Gamma(arg); // Stirling invalid for a non-divergent argument
          }
          return LimitGruntz.stirlingGamma(arg, engine);
        }
        case ID.LogGamma: {
          // LogGamma(z) ~ z*Log(z) - z + (1/2)*Log(2*Pi/z) + 1/(12*z)
          IExpr arg = replaceStirling(ast.arg1(), x, engine);
          if (!divergesAtInfinity(arg, x)) {
            return F.LogGamma(arg); // Stirling invalid for a non-divergent argument
          }
          return LimitGruntz.stirlingLogGamma(arg, engine);
        }
        case ID.PolyGamma: {
          // Digamma: PolyGamma(0, z) ~ Log(z) - 1/(2z) - 1/(12z^2) for a divergent argument free
          // of nested PolyGamma (the arg was already recursed, so psi(psi(x)) arrives here
          // with the inner level expanded; Log-bearing args are fine - see mrv PolyGamma).
          // The second-order term is what a limit probing the 1/z^2 order needs: without it
          // n^2*(psi(n+1) - Log(n) - 1/(2n)) collapses to a false 0 instead of -1/12.
          if (ast.argSize() == 2 && ast.arg1().isZero()) {
            IExpr arg = replaceStirling(ast.arg2(), x, engine);
            if (!divergesAtInfinity(arg, x) || !arg.isFree(t -> t.isAST(S.PolyGamma), true)) {
              return F.PolyGamma(F.C0, arg);
            }
            IExpr tail = digammaTailArg(arg, x, engine);
            return LimitGruntz.digammaAsymptotic(digammaPrincipalArg(arg, ast.arg2(), x, engine),
                tail, true, engine);
          }
          break;
        }
        default:
          break;
      }
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        result.append(replaceStirling(ast.get(i), x, engine));
      }
      return engine.evaluate(result);
    }
    return expr;
  }

  /**
   * Replace Abs(variable) with the variable or its negation based on the approach direction.
   * 
   * @param expr the expression
   * @param variable the limit variable
   * @param direction -1 for from-right (positive side), 1 for from-left (negative side)
   * @param limitValue the value being approached
   * @return the rewritten expression
   */
  private static IExpr rewriteAbsForDirection(IExpr expr, IExpr variable, Direction direction,
      IExpr limitValue, EvalEngine engine) {
    if (!variable.isSymbol()) {
      return F.NIL;
    }
    if (direction == Direction.TWO_SIDED) {
      // A two sided limit has to keep the kink: it is exactly the disagreement of the two sides
      // which makes Abs(x)/x Indeterminate rather than 1.
      //
      // RealAbs cannot keep it on its own. It carries the derivative rule x/RealAbs(x), so the
      // Taylor expansion of RealAbs(x) starts with x and RealAbs(x)/x comes out as 1, while
      // Abs'(0) stays unevaluated and Abs answers honestly. The two functions agree on the reals,
      // which is where a limit with a Direction lives, so normalize to the honest one.
      IExpr asAbs = F.subst(expr, y -> {
        if (y.isAST(S.RealAbs, 2) && !y.first().isFree(variable, true)) {
          return F.Abs(y.first());
        }
        return F.NIL;
      });
      return asAbs.equals(expr) ? F.NIL : asAbs;
    }
    if (limitValue.isZero()) {
      // the plain Abs(x) at zero, without asking signViaApproach
      IExpr shortcut = F.subst(expr,
          x -> (x.isAST(S.RealAbs, 2) || x.isAST(S.Abs, 2)) && x.first().equals(variable),
          direction == Direction.FROM_ABOVE ? variable : variable.negate());
      if (!shortcut.equals(expr)) {
        return shortcut;
      }
    }
    // The general rewrite decides the sign of the argument with signViaApproach, which probes
    // limitValue -/+ 1/w and ranks growth. That is only meaningful at a concrete real point: at a
    // symbolic one the constant term cannot be ranked and the sign of the vanishing 1/w term is
    // reported instead, which turned Integrate(RealSign(x),{x,a,b}) into a-b.
    if (!limitValue.isRealResult() || !limitValue.isNumericFunction()) {
      return F.NIL;
    }
    ISymbol x = (ISymbol) variable;
    IExpr rewritten = F.subst(expr, y -> {
      if (!y.isAST(S.Abs, 2) && !y.isAST(S.RealAbs, 2)) {
        return F.NIL;
      }
      IExpr argument = y.first();
      if (argument.isFree(x, true)) {
        // a constant Abs() is not in the way of the limit
        return F.NIL;
      }
      int sign = signViaApproach(argument, x, limitValue, direction, engine);
      if (sign > 0) {
        return argument;
      }
      if (sign < 0) {
        return argument.negate();
      }
      return F.NIL;
    });
    return rewritten.equals(expr) ? F.NIL : rewritten;
  }

  /**
   * Rewrite <code>Sqrt(f^2)</code> as <code>Abs(f)</code>, which is the same function for a real
   * <code>f</code> but the form the limit machinery reasons about correctly.
   *
   * <p>
   * The series expansion flattens <code>(f^2)^(1/2)</code> to <code>f^(2*1/2) == f</code>, which
   * silently picks the positive branch: <code>Limit(Sqrt(x^2)/x, x->0)</code> came out as
   * <code>1</code> where <code>Limit(Abs(x)/x, x->0)</code> correctly stays
   * <code>Indeterminate</code>, and a definite integral built on such an antiderivative returned a
   * wrong value. Normalizing into <code>Abs</code> hands the expression to
   * {@link #rewriteAbsForDirection}, which resolves the branch per direction instead of guessing
   * one.
   *
   * @return {@link F#NIL} if <code>expr</code> contains no such radical
   */
  private static IExpr normalizeSqrtOfSquare(IExpr expr, IExpr variable) {
    if (!expr.has(y -> y.isPower() && y.exponent().isNumEqualRational(F.C1D2), true)) {
      return F.NIL;
    }
    IExpr rewritten = F.subst(expr, y -> {
      if (!y.isPower() || !y.exponent().isNumEqualRational(F.C1D2)) {
        return F.NIL;
      }
      IExpr radicand = y.base();
      if (!radicand.isPower() || !radicand.exponent().isNumEqualRational(F.C2)) {
        return F.NIL;
      }
      IExpr inner = radicand.base();
      if (inner.isFree(variable, true)) {
        // a constant radical does not need the case distinction
        return F.NIL;
      }
      if (!inner.isFree(y2 -> y2.isComplex() || y2.isComplexNumeric(), true)) {
        // Sqrt(z^2) is not Abs(z) off the real axis
        return F.NIL;
      }
      return F.Abs(inner);
    });
    return rewritten.equals(expr) ? F.NIL : rewritten;
  }

  /**
   * Determine the directed infinity for {@code 0^(negative exponent)} when approaching from a
   * specific direction. Uses {@link #signViaApproach} to detect whether the base is positive or
   * negative near the limit point.
   *
   * @param base the base expression approaching zero
   * @param exponent the negative exponent
   * @param data the limit data (must not be {@link Direction#TWO_SIDED})
   * @param engine the evaluation engine
   * @return a directed infinity, or {@link F#NIL} if the sign could not be determined
   */
  private static IExpr directedInfinityForZeroBase(IExpr base, IExpr exponent, LimitData data,
      EvalEngine engine) {
    IExpr limitVal = data.limitValue();
    int sign = signViaApproach(base, data.variable(), limitVal, data.direction(), engine);
    if (sign == 1) {
      return F.CInfinity;
    } else if (sign == -1) {
      if (exponent.isInteger() && ((IInteger) exponent).isOdd()) {
        return F.CNInfinity;
      }
      return F.DirectedInfinity(engine.evaluate(F.Power(F.CN1, exponent)));
    }
    // Fallback based on direction
    if (data.direction() == Direction.FROM_BELOW) {
      if (exponent.isInteger() && ((IInteger) exponent).isOdd()) {
        return F.CNInfinity;
      }
      return F.DirectedInfinity(engine.evaluate(F.Power(F.CN1, exponent)));
    }
    return F.CInfinity;
  }

  /**
   * Evaluates limits for special functions when their arguments evaluate to DirectedInfinity.
   * * @param ast the unevaluated AST containing the DirectedInfinity
   * 
   * @return the resolved limit, or F.NIL if not applicable
   */
  private static IExpr directedInfinityLimit(IAST ast) {
    if (ast.isAST1() && ast.arg1().isDirectedInfinity()) {
      IExpr head = ast.head();
      IExpr z = ((IAST) ast.arg1()).arg1();

      if (z.isValidBuiltInFunction() && z.isNumericFunction()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Erf: {
            double re = z.re().evalfNaN();
            double im = z.im().evalfNaN();
            if (!Double.isNaN(re) && !Double.isNaN(im) && Math.abs(re) >= Math.abs(im)) {
              if (re > 0) {
                return F.C1;
              } else if (re < 0) {
                return F.CN1;
              }
            }
            break;
          }
          case ID.Erfc: {
            double re = z.re().evalfNaN();
            double im = z.im().evalfNaN();
            if (!Double.isNaN(re) && !Double.isNaN(im) && Math.abs(re) >= Math.abs(im)) {
              if (re > 0) {
                return F.C0;
              } else if (re < 0) {
                return F.C2;
              }
            }
            break;
          }
          case ID.Erfi: {
            double re = z.re().evalfNaN();
            double im = z.im().evalfNaN();
            if (!Double.isNaN(re) && !Double.isNaN(im) && Math.abs(im) >= Math.abs(re)) {
              if (im > 0) {
                return F.CI;
              } else if (im < 0) {
                return F.CNI;
              }
            }
            break;
          }
        }
      }
    }
    return F.NIL;
  }


  /**
   * Compute the sign of {@code baseExpr} as {@code variable} approaches {@code limitValue} from the
   * given {@code direction} by substituting a dummy variable approaching Infinity and delegating to
   * {@link LimitGruntz#signInf}.
   *
   * @param baseExpr the expression whose sign is to be determined
   * @param variable the limit variable
   * @param limitValue the value being approached
   * @param direction the approach direction ({@link Direction#FROM_BELOW} or
   *        {@link Direction#FROM_ABOVE})
   * @param engine the evaluation engine
   * @return {@code 1}, {@code -1}, or {@code 0} as determined by {@link LimitGruntz#signInf}, or
   *         {@code 0} if the approach could not be constructed
   */
  private static int signViaApproach(IExpr baseExpr, ISymbol variable, IExpr limitValue,
      Direction direction, EvalEngine engine) {
    if (limitValue.isInfinity() || limitValue.isNegativeInfinity()
        || limitValue.isDirectedInfinity()) {
      return 0;
    }
    ISymbol w = F.Dummy("w");
    IExpr approach;
    if (direction == Direction.FROM_BELOW) {
      approach =
          limitValue.isZero() ? F.Divide(F.CN1, w) : F.Subtract(limitValue, F.Divide(F.C1, w));
    } else {
      approach = limitValue.isZero() ? F.Divide(F.C1, w) : F.Plus(limitValue, F.Divide(F.C1, w));
    }
    IExpr substituted = engine.evaluate(F.subst(baseExpr, variable, approach));
    return LimitGruntz.signInf(substituted, w, engine);
  }

  /**
   * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity
   * </code> or <code>-Infinity</code>, <code>y</code> approaches <code>0</code>.
   *
   * @param arg1
   * @param data (the datas limit must be Infinity or -Infinity)
   * @param engine
   * @return <code>F.NIL</code> if the substitution didn't succeed.
   */
  private static IExpr substituteInfinity(final IAST arg1, LimitData data, EvalEngine engine) {
    ISymbol x = data.variable();
    IExpr y = F.Power(x, F.CN1); // substituting by 1/x
    IExpr temp = engine.evalQuiet(F.subst(arg1, x, y));
    if (temp.isTimes()) {
      Optional<IExpr[]> parts =
          AlgebraUtil.fractionalPartsTimesPower((IAST) temp, true, false, true, true, true, true);
      if (parts.isPresent()) {
        if (!parts.get()[1].isOne()) { // denominator != 1
          // The substitution maps the approach side: x -> +Infinity becomes 1/x -> 0 from
          // ABOVE, x -> -Infinity becomes 1/x -> 0 from BELOW. The original direction of
          // the +-Infinity limit must NOT be passed through - a TWO_SIDED request would
          // needlessly demand agreement of both sides of 0, and an explicit directional
          // request would probe the wrong side.
          Direction zeroDirection = data.limitValue().isNegativeInfinity() //
              ? Direction.FROM_BELOW //
              : Direction.FROM_ABOVE;
          LimitData ndData = new LimitData(x, F.C0, F.Rule(x, F.C0), zeroDirection);
          temp = numeratorDenominatorLimit(parts.get()[0], parts.get()[1], ndData, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * The limit at <code>0</code> of a Times expression, computed through the substitution
   * <code>x -&gt; 1/x</code>: <code>x -&gt; 0</code> from ABOVE corresponds to
   * <code>1/x -&gt; +Infinity</code> and from BELOW to <code>-Infinity</code>. A TWO_SIDED request
   * therefore needs BOTH infinite probes to agree - collapsing it to the <code>+Infinity</code>
   * side alone (as this rule once did) would adopt a one-sided value for an asymmetric function
   * like <code>x*E^(1/x)</code>.
   *
   * @param newTimes the expression with <code>x</code> already replaced by <code>1/x</code>
   * @return the limit, {@link S#Indeterminate} when the two sides provably differ, or {@link F#NIL}
   */
  private static IExpr reciprocalZeroLimit(IExpr newTimes, LimitData data, EvalEngine engine) {
    if (data.direction() == Direction.TWO_SIDED) {
      IExpr above = reciprocalInfinityLimit(newTimes, data, F.CInfinity, engine);
      if (above.isNIL() || above.isIndeterminate() || !above.isFree(S.Limit)) {
        return F.NIL;
      }
      IExpr below = reciprocalInfinityLimit(newTimes, data, F.CNInfinity, engine);
      if (below.isNIL() || below.isIndeterminate() || !below.isFree(S.Limit)) {
        return F.NIL;
      }
      if (above.equals(below)) {
        return above;
      }
      // both sides resolved cleanly but to different values - the two-sided limit does not exist
      return S.Indeterminate;
    }
    IAST infinityExpr = (data.direction() == Direction.FROM_BELOW) ? F.CNInfinity : F.CInfinity;
    IExpr temp = reciprocalInfinityLimit(newTimes, data, infinityExpr, engine);
    if (temp.isPresent() && !temp.isIndeterminate()) {
      return temp;
    }
    return F.NIL;
  }

  /** One directional probe for {@link #reciprocalZeroLimit}; NIL-safe. */
  private static IExpr reciprocalInfinityLimit(IExpr expr, LimitData data, IAST infinityExpr,
      EvalEngine engine) {
    LimitData copy = new LimitData(data.variable(), infinityExpr,
        F.Rule(data.variable(), infinityExpr), data.direction());
    IExpr temp = evalLimitQuiet(expr, copy);
    if (temp.isPresent()) {
      return engine.evaluate(temp);
    }
    return F.NIL;
  }

  private static IExpr timesConjugateLHospital(final IAST plusAST, int indexOf, LimitData data) {
    IExpr factor1 = plusAST.removeAtCopy(indexOf).oneIdentity0();
    IExpr factor2 = plusAST.get(indexOf);
    IExpr numerator = F.evalExpand(F.Subtract(F.Sqr(factor1), F.Sqr(factor2)));
    IExpr denominator = F.eval(F.Subtract(factor1, factor2));
    // IASTMutable timesConjugate = F.Times(numerator, F.Power(denominator, F.CN1));
    return numeratorDenominatorLimit(numerator, denominator, data, EvalEngine.get());
    // temp = evalLimitQuiet(timesConjugate, data);

  }

  private static IExpr timesLimit(final IAST timesAST, LimitData data, EvalEngine engine) {
    IAST isFreeResult =
        timesAST.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
    if (!isFreeResult.arg1().isOne()) {
      IExpr freeOfVariable = isFreeResult.arg1();
      IExpr limit = data.limit(isFreeResult.arg2());
      if (limit.isNIL()) {
        // never embed the NIL sentinel into a result AST - evaluating
        // Times(freeOfVariable, NIL) later aborts with "unexpected NIL expression"
        return F.NIL;
      }
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        if (engine.evaluate(F.Greater(freeOfVariable, F.C0)).isTrue()) {
          return limit.isInfinity() ? F.CInfinity : F.CNInfinity;
        }
        if (engine.evaluate(F.Less(freeOfVariable, F.C0)).isTrue()) {
          return limit.isInfinity() ? F.CNInfinity : F.CInfinity;
        }
      }

      return F.Times(freeOfVariable, limit);
    }
    Optional<IExpr[]> parts =
        AlgebraUtil.fractionalPartsTimesPower(timesAST, true, false, true, true, true, true);
    if (parts.isEmpty()) {
      IAST[] timesPolyFiltered = timesAST.filter(x -> x.isPolynomial(data.variable));
      if (timesPolyFiltered[0].size() > 1 && timesPolyFiltered[1].size() > 1) {
        IExpr first = data.limit(timesPolyFiltered[0].oneIdentity1());
        if (first.isNIL()) {
          return F.NIL; // evaluating the NIL sentinel would abort the evaluation
        }
        first = engine.evaluate(first);
        if (first.isIndeterminate()) {
          return S.Indeterminate;
        }
        IExpr second = data.limit(timesPolyFiltered[1].oneIdentity1());
        if (second.isNIL()) {
          return F.NIL;
        }
        second = engine.evaluate(second);
        if (second.isIndeterminate()) {
          return S.Indeterminate;
        }
        if (first.isRealResult() || second.isRealResult()) {
          IExpr temp = engine.evaluate(F.Times(first, second));
          if (!temp.isIndeterminate()) {
            return temp;
          }
          if (data.limitValue().isZero()) {
            // Try reciprocal of symbol and approach to +/- Infinity
            IExpr newTimes =
                timesAST.replaceAll(F.Rule(data.variable, F.Power(data.variable, F.CN1)));
            if (newTimes.isPresent()) {
              temp = reciprocalZeroLimit(newTimes, data, engine);
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
        }
      }
    } else {

      IExpr numerator = parts.get()[0];
      IExpr denominator = parts.get()[1];
      IExpr limit = data.limitValue();
      ISymbol symbol = data.variable();
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        try {
          // Add Expand to safely parse newly extracted negative polynomials
          IExpr expNumerator = engine.evaluate(F.Expand(numerator));
          IExpr expDenominator = engine.evaluate(F.Expand(denominator));
          ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
          ExprPolynomial denominatorPoly = ring.create(expDenominator);
          ExprPolynomial numeratorPoly = ring.create(expNumerator);
          return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit,
              data);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }

      IExpr plusResult = AlgebraUtil.partsApart(parts.get(), symbol, engine);
      // Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(),
      // parts,symbol);
      if (plusResult.isPlus()) {
        return data.mapLimit((IAST) plusResult);
      }

      if (denominator.isOne()) {
        if (limit.isInfinity() || limit.isNegativeInfinity()) {
          IExpr temp = substituteInfinity(timesAST, data, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      IExpr temp = numeratorDenominatorLimit(numerator, denominator, data, engine);
      if (temp.isPresent()) {
        return temp;
      }
    }
    return data.mapLimit(timesAST);
  }

  /**
   * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of
   * Limits</a>
   */
  /**
   * Retry a finite-point limit that came back Indeterminate/unevaluated for a body containing a
   * radical (a fractional power such as Sqrt). Symja keeps e.g. {@code Sqrt((2-t)^4*(4+t))} as a
   * single radical, so a perfect-square factor never cancels against the denominator, and it
   * mishandles 0*Infinity products such as {@code (x-4)*Sqrt((4+x)/(4-x))} on the side where the
   * radicand turns negative. {@code PowerExpand} (optionally followed by {@code Together}) exposes
   * the reduction, but PowerExpand is unsound in general (it drops {@code Abs} / picks a branch),
   * so a candidate is adopted only when a small numeric probe of the ORIGINAL body confirms it. For
   * a one-sided candidate the opposite side must be non-real (the two-sided limit legitimately
   * leaves the reals, e.g. {@code Sqrt((4+x)/(4-x))} for {@code x>4}), so an ordinary real jump
   * discontinuity stays Indeterminate.
   */
  private static IExpr finiteRadicalLimitRetry(IExpr body, IAST rule, ISymbol x, IExpr x0,
      Direction requested, EvalEngine engine) {
    if (body.leafCount() > 200) {
      return F.NIL;
    }
    double x0d;
    try {
      x0d = x0.evalDouble();
    } catch (RuntimeException rex) {
      return F.NIL; // a real finite point is required for the numeric confirmation
    }
    if (!Double.isFinite(x0d)) {
      return F.NIL;
    }
    IExpr pe;
    try {
      pe = engine.evaluate(F.PowerExpand(body));
    } catch (RuntimeException rex) {
      return F.NIL;
    }
    IExpr result = F.NIL;
    if (!pe.equals(body)) {
      result = tryRadicalForm(pe, body, rule, x, x0d, requested, engine);
    }
    if (result.isNIL()) {
      try {
        IExpr tpe = engine.evaluate(F.Together(pe));
        if (!tpe.equals(pe) && !tpe.equals(body)) {
          result = tryRadicalForm(tpe, body, rule, x, x0d, requested, engine);
        }
      } catch (RuntimeException rex) {
        // no Together reduction available - fall through
      }
    }
    return result;
  }

  private static IExpr tryRadicalForm(IExpr form, IExpr origBody, IAST rule, ISymbol x, double x0d,
      Direction requested, EvalEngine engine) {
    Direction[] dirs = (requested == Direction.TWO_SIDED)
        ? new Direction[] {Direction.TWO_SIDED, Direction.FROM_BELOW, Direction.FROM_ABOVE}
        : new Direction[] {requested};
    for (Direction dir : dirs) {
      IExpr cand = evaluateLimit(form, rule, dir, engine);
      if (cand.isNIL() || cand.isAST(S.Limit) || !cand.isFree(S.Limit)
          || !cand.isIndeterminateFree() || cand.isComplexInfinity() || cand.isDirectedInfinity()) {
        continue;
      }
      double candD;
      try {
        candD = cand.evalDouble();
      } catch (RuntimeException rex) {
        continue; // a real finite candidate is required for the numeric confirmation
      }
      if (!Double.isFinite(candD)) {
        continue;
      }
      if (!numericLimitConfirms(origBody, x, x0d, dir, candD, engine)) {
        continue;
      }
      // A one-sided value is adopted for a two-sided request only when the other side does not
      // resolve to a different real value. testLimitIssue536 (from-above unresolved) -> 4*Pi;
      // testLimitIssue1420 (from-above -Pi/8 vs from-below Pi/8) -> stays Indeterminate.
      if (requested == Direction.TWO_SIDED && dir != Direction.TWO_SIDED
          && oppositeSideDisagrees(origBody, rule, dir, candD, engine)) {
        continue;
      }
      return cand;
    }
    return F.NIL;
  }

  private static boolean numericLimitConfirms(IExpr body, ISymbol x, double x0, Direction dir,
      double candidate, EvalEngine engine) {
    switch (dir) {
      case FROM_BELOW:
        return radicalSideConverges(body, x, x0, -1.0, candidate, engine);
      case FROM_ABOVE:
        return radicalSideConverges(body, x, x0, +1.0, candidate, engine);
      default: // TWO_SIDED - both real sides must approach the candidate
        return radicalSideConverges(body, x, x0, -1.0, candidate, engine)
            && radicalSideConverges(body, x, x0, +1.0, candidate, engine);
    }
  }

  /**
   * True if, for a two-sided request, adopting the one-sided {@code candidate} (direction
   * {@code dir}) would be wrong because the OPPOSITE side resolves to a different finite real value
   * - a genuine two-sided disagreement so the limit does not exist. When the opposite side stays
   * unresolved (Indeterminate / a Limit shell) or is non-real there is no contradiction and the
   * real-domain one-sided value may be returned.
   */
  private static boolean oppositeSideDisagrees(IExpr origBody, IAST rule, Direction dir,
      double candidate, EvalEngine engine) {
    Direction opposite =
        (dir == Direction.FROM_BELOW) ? Direction.FROM_ABOVE : Direction.FROM_BELOW;
    IExpr other = evaluateLimit(origBody, rule, opposite, engine);
    if (other.isNIL() || other.isAST(S.Limit) || !other.isFree(S.Limit)
        || !other.isIndeterminateFree()) {
      return false; // unresolved opposite side - no contradiction
    }
    try {
      double otherD = other.evalDouble();
      return Double.isFinite(otherD)
          && Math.abs(otherD - candidate) > 1.0e-6 * (1.0 + Math.abs(candidate));
    } catch (RuntimeException rex) {
      return false; // opposite side is not a real number - no contradiction
    }
  }

  /** True if body(x0 + sign*delta) approaches {@code candidate} as delta shrinks (real samples). */
  private static boolean radicalSideConverges(IExpr body, ISymbol x, double x0, double sign,
      double candidate, EvalEngine engine) {
    IExpr coarse = numericLimitSample(body, x, x0 + sign * 1.0e-3, engine);
    IExpr fine = numericLimitSample(body, x, x0 + sign * 1.0e-7, engine);
    if (!coarse.isReal() || !fine.isReal()) {
      return false;
    }
    double coarseErr = Math.abs(coarse.evalDouble() - candidate);
    double fineErr = Math.abs(fine.evalDouble() - candidate);
    double tol = 0.05 * (1.0 + Math.abs(candidate));
    return fineErr <= tol && fineErr <= coarseErr + 1.0e-9;
  }

  private static IExpr numericLimitSample(IExpr body, ISymbol x, double pt, EvalEngine engine) {
    try {
      IExpr sub = body.replaceAll(F.Rule(x, F.num(pt))).orElse(body);
      IExpr n = engine.evaluate(F.N(sub));
      return n.isNumber() ? n : F.NIL;
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (!arg2.isRuleAST()) {
      // Limit specification `1` is not of the form x->x0.
      return Errors.printMessage(S.Limit, "lim", F.List(arg2), engine);
    }
    IAST rule = (IAST) arg2;
    if (!(rule.arg1().isSymbol())) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.Limit, "ivar", F.List(arg2), engine);
    }
    if (arg1.isList()) {
      return arg1.mapThread(ast, 1);
    }
    boolean numericMode = engine.isNumericMode();
    IAssumptions oldAssumptions = engine.getAssumptions();
    final int builtinDepth = LIMIT_BUILTIN_DEPTH.get().intValue();
    try {
      LIMIT_BUILTIN_DEPTH.set(Integer.valueOf(builtinDepth + 1));
      if (builtinDepth == 0) {
        // fresh user-level Limit call: assumptions may differ from the previous call and the
        // sign cache is also fed outside Gruntz runs - start clean (see clearSessionCaches)
        LimitGruntz.clearSessionCaches();
      }
      engine.setNumericMode(false);
      Direction direction = Direction.TWO_SIDED; // no direction as default

      // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      IExpr directionOption = option[0];
      if (directionOption.isPresent()) {
        if (directionOption.isOne() || directionOption.isString("FromBelow")) {
          direction = Direction.FROM_BELOW;
        } else if (directionOption.isMinusOne() || directionOption.isString("FromAbove")) {
          direction = Direction.FROM_ABOVE;
        } else if (directionOption == S.Automatic || directionOption == S.Reals
            || directionOption.isString("TwoSided")) {
          direction = Direction.TWO_SIDED;
        } else {
          // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided
          // or a list of these.
          return Errors.printMessage(S.Limit, "ldir", F.List(ast.arg3()), engine);
        }
      } else {
        // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided or
        // a list of these.
        return Errors.printMessage(S.Limit, "ldir", F.List(S.Null), engine);
      }

      IExpr assumptionOption = option[1];
      // IExpr generateConditionOption = option[2];

      IExpr assumptionExpr = OptionArgs.determineAssumptions(assumptionOption);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        if (oldAssumptions != null) {
          engine.setAssumptions(oldAssumptions.copy().addAssumption(assumptionExpr));
        } else {
          IAssumptions assumptions =
              org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
          }
        }
      }

      // Sqrt(f^2) is Abs(f) - normalize it first so that the Abs rewrite below resolves the
      // branch per direction instead of the series silently picking the positive one
      IExpr normalized = normalizeSqrtOfSquare(arg1, rule.arg1());
      if (normalized.isPresent()) {
        arg1 = normalized;
      }
      // RealAbs is the real valued twin of Abs which substAbs() and the derivative of Abs
      // produce, so it has to reach the same rewrite - rewriteAbsForDirection handles both heads
      if (arg1.has(S.Abs, true) || arg1.has(S.RealAbs, true)) {
        IExpr rewritten = rewriteAbsForDirection(arg1, rule.arg1(), direction, rule.arg2(), engine);
        if (rewritten.isPresent()) {
          // Compute the limit on the rewritten expression (without Abs)
          arg1 = rewritten;
        }
      }

      // Top-level algebraic simplification: cancel a rational expression like
      // (x^2-a^2)/(x-a) -> x+a so that 0/0 forms at a finite (possibly symbolic)
      // limit point are reduced to a polynomial whose limit can be taken by
      // direct substitution. This runs exactly once per top-level Limit call.
      if (arg1.isTimes() && arg1.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT
          && !arg1.isFree(rule.arg1())) {
        boolean hasDenominator = ((IAST) arg1).exists(x -> x.isPower() && x.exponent().isInteger()
            && x.exponent().isNegative() && !x.base().isFree(rule.arg1()));
        if (hasDenominator) {
          IExpr cancelled = engine.evalQuiet(F.Cancel(arg1));
          if (cancelled.isPresent() && !cancelled.isIndeterminate()
              && cancelled.leafCount() < arg1.leafCount()) {
            arg1 = cancelled;
          }
        }
      }

      // see isExpGammaTowerShape - routed to Gruntz FIRST at the builtin boundary; adopt
      // only a clean result
      ISymbol limitVar = (ISymbol) rule.arg1();
      IExpr limitPoint = rule.arg2();
      if ((limitPoint.isInfinity() || limitPoint.isNegativeInfinity()) && !LimitGruntz.isActive()
          && !LimitGruntz.isInGruntzSeries() && isExpGammaTowerShape(arg1, limitVar)
          && arg1.isNumericFunction(new VariablesSet(arg1))) {
        IExpr gruntzFirst =
            LimitGruntz.evaluateLimit(arg1, limitVar, limitPoint, direction, engine);
        if (gruntzFirst.isPresent() && gruntzFirst.isFree(S.Limit)
            && gruntzFirst.isIndeterminateFree() && !hasNestedDirectedInfinity(gruntzFirst)) {
          return gruntzFirst;
        }
      }

      // A sum of bounded oscillations has an accumulation RANGE, not a limit. It is resolved at
      // the builtin boundary and returned directly: the answer is an Interval, and the
      // toAccumBoundsIndeterminate() call below (rightly) collapses every Interval that reaches
      // it to Indeterminate.
      IExpr envelope = oscillatingEnvelope(arg1, rule, direction, engine);
      if (envelope.isPresent()) {
        return envelope;
      }

      IExpr temp = evaluateLimit(arg1, rule, direction, engine);
      if ((temp.isNIL() || temp.isIndeterminate()) && limitPoint.isFree(S.DirectedInfinity)
          && !limitPoint.isInfinity() && !limitPoint.isNegativeInfinity()
          && arg1.has(p -> p.isPower() && p.exponent().isFraction(), true)) {
        // A finite-point limit of a radical expression that the machinery could not reduce
        // (Sqrt of a perfect-square product, or a 0*Infinity Sqrt product): retry via
        // PowerExpand(+Together), numerically confirmed against the original body.
        IExpr radical =
            finiteRadicalLimitRetry(arg1, rule, limitVar, limitPoint, direction, engine);
        if (radical.isPresent()) {
          return radical;
        }
      }
      if (temp.isPresent()) {
        // A RESOLVED limit must not surface machinery-internal symbols: series/Taylor
        // helpers substitute Dummy("y")-style variables and a partially aborted evaluation
        // can leak them into an otherwise definite result (observed: a Gamma-difference
        // tower "resolving" to Infinity*y). A free symbol of the result that does not
        // occur in the input marks such junk - stay unevaluated instead. Results still
        // containing Limit are exempt: a returned (possibly variable-renamed) Limit shell
        // is the engine's re-entry mechanism, not an adopted answer.
        boolean leakedSymbol = false;
        if (temp.isFree(S.Limit, true)) {
          java.util.Set<IExpr> allowedVars = new VariablesSet(ast).toSet();
          for (IExpr resultVar : new VariablesSet(temp).toSet()) {
            if (!allowedVars.contains(resultVar)) {
              if (DEBUG) {
                System.out.println("LEAK-GUARD input=" + ast + " raw=" + temp + " leakedVar="
                    + resultVar + " allowed=" + allowedVars);
              }
              leakedSymbol = true;
              break;
            }
          }
        }
        if (!leakedSymbol) {
          return IntervalSym.toAccumBoundsIndeterminate(temp);
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Limit, rex);
    } catch (StackOverflowError soe) {
      // Gamma-tower shapes (Limit(E^Gamma(x)/Gamma(x))) drive mutually recursive heuristic
      // corridors - L'Hopital derivatives, the mrv rewrite, E^(Plus) auto-Expand - through
      // raw Java recursions that no engine recursion limit observes. Recover at the builtin
      // boundary (the per-frame finally blocks have unwound the engine state) and leave the
      // Limit unevaluated; the background worker path (EvalControlledCallable) already
      // recovers from StackOverflowError the same way.
      Errors.printMessage(S.Limit, "error",
          F.List("StackOverflowError in Limit evaluation - expression stays unevaluated"), engine);
    } finally {
      LIMIT_BUILTIN_DEPTH.set(Integer.valueOf(builtinDepth));
      engine.setNumericMode(numericMode);
      engine.setAssumptions(oldAssumptions);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Direction, S.Assumptions, S.GenerateConditions}, //
        new IExpr[] {S.Reals, S.$Assumptions, S.Automatic});
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
