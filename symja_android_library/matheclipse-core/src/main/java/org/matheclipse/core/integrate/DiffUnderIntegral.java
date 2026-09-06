package org.matheclipse.core.integrate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Definite integrals answered by differentiating under the integral sign, which is Leibniz's rule
 * and the trick Feynman is remembered for.
 *
 * <p>
 * For an integral <code>I(p) = Integrate(f(x,p), {x,a,b})</code> carrying a parameter, differentiate
 * the integrand by that parameter, do the inner integral, integrate the answer back over the
 * parameter, and fix the constant at a value of the parameter where the integral is known. What
 * this class does is the three cases where there is no parameter to start with: one is
 * <em>introduced</em> into the logarithm, <code>Log(1+W) -> Log(1+t*W)</code>, and the answer is
 * read at <code>t == 1</code>, the base <code>t == 0</code> being zero because
 * <code>Log(1) == 0</code>.
 *
 * <table>
 * <tr><th>integrand</th><th>over</th><th>value</th></tr>
 * <tr><td><code>Log(1 + c*x^p)/(x*Sqrt(1 - x^(2*p)))</code></td><td>0 to 1</td>
 *     <td><code>(Pi^2/8 - ArcCos(c)^2/2)/p</code></td></tr>
 * <tr><td><code>Sec(2*x)*Log(1 + c*Sqrt(1 - Tan(x)^2))</code></td><td>0 to Pi/4</td>
 *     <td><code>Pi^2/8 - ArcCos(c)^2/2</code></td></tr>
 * <tr><td><code>Csc(2*x)^2*Log(1 + Tan(x)^a)</code></td><td>0 to Pi/4</td>
 *     <td><code>(Pi*Csc(Pi/a) - a)/4</code></td></tr>
 * </table>
 *
 * <p>
 * The first two are the same integral seen twice: <code>u = x^p</code> turns one into
 * <code>Integrate(A*Log(1+c*u)/(u*Sqrt(1-u^2)), {u,0,1})</code> and <code>t = Tan(x)</code> turns
 * the other into it as well, and differentiating by the introduced parameter leaves
 * <code>Integrate(1/((1+c*u)*Sqrt(1-u^2)), {u,0,1}) == ArcCos(c)/Sqrt(1-c^2)</code>, whose integral
 * over <code>c</code> is the <code>ArcCos(c)^2/2</code> above. The third is the reflection formula
 * <code>Integrate((u^-b - u^b)/(1+u), {u,0,1}) == Pi*Csc(Pi*b) - 1/b</code> with
 * <code>b = 1/a</code>.
 *
 * <p>
 * The values are written down rather than computed: the inner integrals are ones the general
 * engine cannot do, and for the second family it returns a wrong number for it. What is checked at
 * run time is instead that the integrand really is the one being claimed -- the shape is read off
 * symbolically and then the two are compared numerically at several points, because a shape test
 * alone would accept a near miss.
 *
 * <p>
 * Reachable as <code>Integrate(f, {x,a,b})</code>, or explicitly with
 * <code>Method -&gt; "DiffUnderInt"</code>.
 */
public final class DiffUnderIntegral {

  private DiffUnderIntegral() {}

  /** Where the integrand and the shape claimed for it are compared. */
  private static final double[] SAMPLE_FRACTIONS = {0.17, 0.38, 0.61, 0.83};

  /** How far the two may differ there, relative to the size of the integrand. */
  private static final double TOLERANCE = 1.0e-6;

  /** How big an integrand is still worth looking at. */
  private static final int MAX_LEAF_COUNT = 120;

  /**
   * How long the one simplification this needs may take. The number is what it is on the machine
   * the stage was tuned on; a slower machine is given proportionally longer, see
   * {@link MachineProfile}.
   */
  private static final int STEP_SECONDS = 2;

  /** Whether the name of a <code>Method</code> option asks for this. */
  public static boolean isMethodName(String method) {
    return method.equalsIgnoreCase("DiffUnderInt") || method.equalsIgnoreCase("Feynman")
        || method.equalsIgnoreCase("Leibniz");
  }

  /**
   * The value of the definite integral, or {@link F#NIL} if it is not one of the three.
   *
   * @param f the integrand
   * @param x the integration variable
   * @param lower the lower limit
   * @param upper the upper limit
   */
  public static IExpr integrate(IExpr f, IExpr x, IExpr lower, IExpr upper, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHMS || !Config.INTEGRATE_ALGORITHM_DIFF_UNDER_INT
        || !x.isSymbol() || !lower.isZero()
        || f.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }
    try {
      if (upper.isOne()) {
        return powerLog(f, x, engine);
      }
      if (engine.evaluate(F.Subtract(upper, F.Divide(S.Pi, F.C4))).isZero()) {
        // The tangent power family owns a non-integer power of the tangent, and it is tried first:
        // the other family's simplification does not finish on one.
        IExpr value = tangentPower(f, x, engine);
        return value.isPresent() ? value : secantRadical(f, x, engine);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * <code>Integrate(A*Log(1 + c*x^p)/(x*Sqrt(1 - x^(2*p))), {x,0,1})</code>.
   */
  private static IExpr powerLog(IExpr f, IExpr x, EvalEngine engine) {
    IExpr logarithm = onePlusLog(f, x);
    if (logarithm.isNIL()) {
      return F.NIL;
    }
    IExpr inner = engine.evaluate(F.Subtract(logarithm.first(), F.C1));
    IExpr power = monomialExponent(inner, x, engine);
    if (power.isNIL() || !power.isReal() || !power.isPositive()) {
      return F.NIL;
    }
    IExpr factor = engine.evaluate(F.Cancel(F.Together(F.Divide(inner, F.Power(x, power)))));
    if (!factor.isFree(x, true) || factor.isZero()) {
      return F.NIL;
    }
    IExpr shape = F.Divide(logarithm,
        F.Times(x, F.Sqrt(F.Subtract(F.C1, F.Power(x, F.Times(F.C2, power))))));
    IExpr scale = constantRatio(f, shape, x, engine);
    if (scale.isNIL() || !isInRange(factor, engine)) {
      return F.NIL;
    }
    return engine.evaluate(F.Divide(F.Times(scale, arcCosValue(factor)), power));
  }

  /**
   * <code>Integrate(A*Sec(2*x)*Log(1 + c*Sqrt(1 - Tan(x)^2)), {x,0,Pi/4})</code>.
   */
  private static IExpr secantRadical(IExpr f, IExpr x, EvalEngine engine) {
    IExpr t = F.Dummy("diuiT");
    IExpr canonical = inTangent(f, x, t, engine);
    if (canonical.isNIL()) {
      return F.NIL;
    }
    IExpr logarithm = onePlusLog(canonical, t);
    if (logarithm.isNIL()) {
      return F.NIL;
    }
    IExpr radical = F.Sqrt(F.Subtract(F.C1, F.Sqr(t)));
    IExpr factor = engine.evaluate(
        F.Cancel(F.Together(F.Divide(F.Subtract(logarithm.first(), F.C1), radical))));
    if (!factor.isFree(t, true) || factor.isZero()) {
      return F.NIL;
    }
    IExpr shape = F.Divide(logarithm, F.Subtract(F.C1, F.Sqr(t)));
    IExpr scale = constantRatio(canonical, shape, t, engine);
    if (scale.isNIL() || !isInRange(factor, engine)) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(scale, arcCosValue(factor)));
  }

  /**
   * <code>Integrate(A*Csc(2*x)^2*Log(1 + Tan(x)^a), {x,0,Pi/4})</code>.
   */
  private static IExpr tangentPower(IExpr f, IExpr x, EvalEngine engine) {
    IExpr t = F.Dummy("diuiT");
    IExpr canonical = inTangent(f, x, t, engine);
    if (canonical.isNIL()) {
      return F.NIL;
    }
    IExpr logarithm = onePlusLog(canonical, t);
    if (logarithm.isNIL()) {
      return F.NIL;
    }
    IExpr inner = engine.evaluate(F.Subtract(logarithm.first(), F.C1));
    IExpr exponent = monomialExponent(inner, t, engine);
    if (exponent.isNIL()
        || !engine.evaluate(F.Cancel(F.Divide(inner, F.Power(t, exponent)))).isOne()) {
      return F.NIL;
    }
    // Below one the integrand does not converge at the origin: near it the logarithm is
    // t^exponent and the cosecant is 1/(4*t^2), so the integrand grows like t^(exponent-2).
    if (exponent.isReal() && !exponent.greaterThan(F.C1).isTrue()) {
      return F.NIL;
    }
    IExpr shape = F.Divide(F.Times(F.Plus(F.C1, F.Sqr(t)), logarithm), F.Sqr(t));
    IExpr scale = constantRatio(canonical, shape, t, engine);
    if (scale.isNIL()) {
      return F.NIL;
    }
    IExpr value = engine.evaluate(F.Times(scale,
        F.Subtract(F.Times(S.Pi, F.Csc(F.Divide(S.Pi, exponent))), exponent)));
    // A symbolic exponent carries its convergence condition with it rather than being answered as
    // though it held everywhere: at exponent 1 the value is finite and the integral is not.
    return exponent.isReal() ? value
        : F.ConditionalExpression(value, F.Greater(exponent, F.C1));
  }

  /** <code>Pi^2/8 - ArcCos(c)^2/2</code>, the value both radical families share. */
  private static IExpr arcCosValue(IExpr c) {
    return F.Subtract(F.Divide(F.Sqr(S.Pi), F.ZZ(8)), F.Divide(F.Sqr(F.ArcCos(c)), F.C2));
  }

  /**
   * The integrand rewritten in <code>t == Tan(x)</code>, including the Jacobian.
   *
   * <p>
   * By rules rather than by substituting <code>x -> ArcTan(t)</code>, which the engine does not
   * carry through <code>Sec(2*x)</code> reliably.
   */
  private static IExpr inTangent(IExpr f, IExpr x, IExpr t, EvalEngine engine) {
    IExpr twoX = F.Times(F.C2, x);
    IExpr oneMinus = F.Subtract(F.C1, F.Sqr(t));
    IExpr onePlus = F.Plus(F.C1, F.Sqr(t));
    IExpr rewritten = engine.evaluate(F.subst(f, F.List( //
        F.Rule(F.Sec(twoX), F.Divide(onePlus, oneMinus)), //
        F.Rule(F.Csc(twoX), F.Divide(onePlus, F.Times(F.C2, t))), //
        F.Rule(F.Cos(twoX), F.Divide(oneMinus, onePlus)), //
        F.Rule(F.Sin(twoX), F.Divide(F.Times(F.C2, t), onePlus)), //
        F.Rule(F.Tan(x), t), //
        F.Rule(F.Cot(x), F.Power(t, F.CN1)))));
    if (!rewritten.isFree(x, true)) {
      return F.NIL;
    }
    // dx == dt/(1+t^2), and the limits 0 and Pi/4 become 0 and 1.
    return engine.evaluate(F.Divide(rewritten, onePlus));
  }

  /** The <code>Log(1 + ...)</code> of the integrand, if it has exactly one. */
  private static IExpr onePlusLog(IExpr f, IExpr x) {
    IExpr[] found = new IExpr[] {F.NIL};
    int[] count = new int[1];
    collectLogs(f, x, found, count);
    return count[0] == 1 ? found[0] : F.NIL;
  }

  private static void collectLogs(IExpr expr, IExpr x, IExpr[] found, int[] count) {
    if (!expr.isAST()) {
      return;
    }
    if (expr.isAST(S.Log, 2) && expr.first().isPlus() && ((IAST) expr.first()).argSize() == 2
        && ((IAST) expr.first()).arg1().isOne() && !expr.first().isFree(x, true)) {
      found[0] = expr;
      count[0]++;
      return;
    }
    IAST ast = (IAST) expr;
    for (int i = 0; i < ast.size(); i++) {
      collectLogs(ast.get(i), x, found, count);
    }
  }

  /** The exponent of the variable in a single power of it, or {@link F#NIL}. */
  private static IExpr monomialExponent(IExpr expr, IExpr x, EvalEngine engine) {
    IExpr term = expr;
    if (term.isTimes()) {
      IAST times = (IAST) term;
      IExpr found = F.NIL;
      for (int i = 1; i <= times.argSize(); i++) {
        if (!times.get(i).isFree(x, true)) {
          if (found.isPresent()) {
            return F.NIL;
          }
          found = times.get(i);
        }
      }
      term = found;
    }
    if (term.equals(x)) {
      return F.C1;
    }
    if (term.isPower() && term.base().equals(x) && term.exponent().isFree(x, true)) {
      return term.exponent();
    }
    return F.NIL;
  }

  /**
   * The constant the integrand is the claimed shape times, or {@link F#NIL} if it is not.
   *
   * <p>
   * The ratio being free of the variable says the shapes agree, but only as far as the simplifier
   * got, so the two are also compared as numbers at several points inside the interval. A near
   * miss which happens to cancel symbolically would otherwise be answered with a formula that is
   * not its value.
   */
  private static IExpr constantRatio(IExpr f, IExpr shape, IExpr x, EvalEngine engine) {
    IExpr ratio = engine.evaluate(F.Cancel(F.Together(F.Divide(f, shape))));
    if (!ratio.isFree(x, true)) {
      ratio = engine.evalTimeConstrained(F.Simplify(F.PowerExpand(F.Divide(f, shape))),
          MachineProfile.seconds(STEP_SECONDS));
    }
    if (ratio.isNIL() || !ratio.isFree(x, true) || ratio.isZero() || ratio.isIndeterminate()
        || !ratio.isFree(S.Integrate, true)) {
      return F.NIL;
    }
    return sameFunction(f, F.Times(ratio, shape), x, engine) ? ratio : F.NIL;
  }

  /**
   * Whether the two agree as numbers inside the interval, at every point where both are one.
   *
   * <p>
   * A parameter of the integrand - the exponent of the tangent, or the constant in the logarithm -
   * is pinned at a generic value first, each at a different one. Without that an integrand
   * carrying a symbolic parameter never becomes a number and the comparison, finding nothing to
   * compare, would turn every one of them away.
   */
  private static boolean sameFunction(IExpr f, IExpr claimed, IExpr x, EvalEngine engine) {
    IASTAppendable parameters = F.ListAlloc();
    collectParameters(f, x, parameters);
    IASTAppendable pinned = F.ListAlloc(parameters.argSize());
    for (int i = 1; i <= parameters.argSize(); i++) {
      pinned.append(F.Rule(parameters.get(i),
          F.num(PARAMETER_VALUES[(i - 1) % PARAMETER_VALUES.length])));
    }
    if (pinned.argSize() > 0) {
      f = engine.evaluate(F.subst(f, pinned));
      claimed = engine.evaluate(F.subst(claimed, pinned));
    }
    int agreed = 0;
    for (double fraction : SAMPLE_FRACTIONS) {
      IExpr point = F.num(fraction);
      IExpr left = numeric(f, x, point, engine);
      IExpr right = numeric(claimed, x, point, engine);
      if (left.isNIL() || right.isNIL()) {
        continue;
      }
      double difference = Math.abs(left.evalf() - right.evalf());
      if (difference > TOLERANCE * (1.0 + Math.abs(left.evalf()))) {
        return false;
      }
      agreed++;
    }
    return agreed >= 2;
  }

  /** How big an integrand the general loop will look at. */
  private static final int MAX_GENERAL_LEAF_COUNT = 200;

  /**
   * How long the inner integral, and the one back over the parameter, may each take. Scaled for
   * this machine like {@link #STEP_SECONDS}.
   */
  private static final int INTEGRAL_SECONDS = 5;

  /** Values of the parameter tried as the one where the integral is already known. */
  private static final int[] BASE_VALUES = {0, 1, -1};

  /** At most this many parameters are tried, so a miss costs a bounded number of integrals. */
  private static final int MAX_PARAMETERS = 2;

  /** Generic values for the parameters, each above one so an exponent stays convergent. */
  private static final double[] PARAMETER_VALUES = {2.3, 1.7, 3.1, 1.3};

  /** The free parameters of the integrand, which are everything in it but the variable. */
  private static void collectParameters(IExpr expr, IExpr x, IASTAppendable out) {
    if (expr.isSymbol()) {
      if (!expr.equals(x) && !expr.isBuiltInSymbol() && !out.contains(expr)) {
        out.append(expr);
      }
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        collectParameters(ast.get(i), x, out);
      }
    }
  }

  /**
   * The value of a definite integral carrying a parameter of its own, or {@link F#NIL}.
   *
   * <p>
   * This is Leibniz's rule used the way it is normally taught, on an integral that already has a
   * parameter in it: differentiate by the parameter, do the inner integral, integrate the answer
   * back over the parameter, and fix the constant of integration at a value of the parameter where
   * the integral is known -- usually one where the integrand vanishes, so that the integral is
   * zero. Only that case is done here, where the inner integral is a plain quadrature; an
   * integrand whose derivative is a multiple of itself needs an integrating factor and is not
   * attempted.
   *
   * <p>
   * Tried only after the antiderivative has failed, unlike the three closers above: there is no
   * shape to recognize, so every attempt costs two integrals, and an integral that Newton-Leibniz
   * can do should never pay for them.
   */
  public static IExpr general(IExpr f, IExpr x, IExpr lower, IExpr upper, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHMS || !Config.INTEGRATE_ALGORITHM_DIFF_UNDER_INT
        || !x.isSymbol() || f.leafCount() > MAX_GENERAL_LEAF_COUNT
        || !f.isFreeAST(h -> h == S.Integrate || h == S.Sum || h == S.Product)) {
      return F.NIL;
    }
    IASTAppendable parameters = F.ListAlloc();
    collectParameters(f, x, parameters);
    try {
      for (int i = 1; i <= parameters.argSize() && i <= MAX_PARAMETERS; i++) {
        IExpr parameter = parameters.get(i);
        if (!lower.isFree(parameter, true) || !upper.isFree(parameter, true)) {
          // A parameter that is also a limit of integration is not one this rule can move.
          continue;
        }
        IExpr value = byParameter(f, x, lower, upper, parameter, parameters, engine);
        if (value.isPresent()) {
          return value;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /** One pass of the rule, differentiating by the given parameter. */
  private static IExpr byParameter(IExpr f, IExpr x, IExpr lower, IExpr upper, IExpr parameter,
      IAST parameters, EvalEngine engine) {
    IExpr derivative = engine.evaluate(F.D(f, parameter));
    if (derivative.isZero()) {
      return F.NIL;
    }
    IASTAppendable conditions = F.ListAlloc();
    IExpr inner =
        cleanIntegral(F.Integrate(derivative, F.List(x, lower, upper)), conditions, engine);
    if (inner.isNIL() || !inner.isFree(x, true)) {
      return F.NIL;
    }
    IExpr back = cleanIntegral(F.Integrate(inner, parameter), conditions, engine);
    if (back.isNIL() || !back.isFree(x, true)) {
      return F.NIL;
    }

    IExpr[] base = baseValue(f, x, lower, upper, parameter, parameters, conditions, engine);
    if (base == null) {
      return F.NIL;
    }
    IExpr shifted = engine.evaluate(F.subst(back, parameter, base[0]));
    if (!isUsable(shifted)) {
      return F.NIL;
    }
    IExpr value = engine.evaluate(F.Plus(back, F.Negate(shifted), base[1]));
    if (!isUsable(value) || !value.isFree(x, true)) {
      return F.NIL;
    }

    // What is checked is that the answer really does differentiate back to the inner integral.
    // It is nearly true by construction, which is the point: what it catches is the substitution
    // or the evaluation having gone wrong, not the rule being misapplied.
    IExpr residual = engine.evaluate(F.Together(F.Subtract(F.D(value, parameter), inner)));
    if (!residual.isZero()) {
      residual = engine.evalTimeConstrained(F.Simplify(residual),
          MachineProfile.seconds(STEP_SECONDS));
      if (residual.isNIL() || !residual.isZero()) {
        return F.NIL;
      }
    }
    return conditions.argSize() == 0 ? value
        : F.ConditionalExpression(value, conditions.argSize() == 1 ? conditions.arg1()
            : conditions.apply(S.And));
  }

  /**
   * The parameter value where the integral is known and its value there, or <code>null</code>.
   *
   * <p>
   * First a value that makes the integrand vanish identically, where the integral is zero without
   * anything having to be computed; then one of the other parameters, which is what the Frullani
   * integral needs; and only then a value where the integral itself can be done.
   */
  private static IExpr[] baseValue(IExpr f, IExpr x, IExpr lower, IExpr upper, IExpr parameter,
      IAST parameters, IASTAppendable conditions, EvalEngine engine) {
    IASTAppendable candidates = F.ListAlloc(BASE_VALUES.length + parameters.argSize());
    for (int value : BASE_VALUES) {
      candidates.append(F.ZZ(value));
    }
    for (int i = 1; i <= parameters.argSize(); i++) {
      if (!parameters.get(i).equals(parameter)) {
        candidates.append(parameters.get(i));
      }
    }
    IASTAppendable integrable = F.ListAlloc(candidates.argSize());
    for (int i = 1; i <= candidates.argSize(); i++) {
      IExpr candidate = candidates.get(i);
      IExpr at = engine.evaluate(F.subst(f, parameter, candidate));
      if (!isUsable(at)) {
        continue;
      }
      if (at.isZero() || engine.evaluate(F.Simplify(at)).isZero()) {
        return new IExpr[] {candidate, F.C0};
      }
      integrable.append(candidate);
    }
    for (int i = 1; i <= integrable.argSize(); i++) {
      IExpr candidate = integrable.get(i);
      IExpr at = engine.evaluate(F.subst(f, parameter, candidate));
      IExpr known = cleanIntegral(F.Integrate(at, F.List(x, lower, upper)), conditions, engine);
      if (known.isPresent() && known.isFree(x, true)) {
        return new IExpr[] {candidate, known};
      }
    }
    return null;
  }

  /**
   * The integral if it comes back as something this rule can go on using, else {@link F#NIL}.
   *
   * <p>
   * A condition the engine attaches is kept rather than thrown away or ignored: it is a condition
   * on the answer too, and the result carries every one collected here.
   */
  private static IExpr cleanIntegral(IExpr integral, IASTAppendable conditions,
      EvalEngine engine) {
    // Through the budget rather than TimeConstrained: this runs the Rubi rules, which do not come
    // back to the evaluation loop often enough for a TimeConstrained to end them, while the
    // budget's watchdog interrupts the thread they are running on.
    IExpr value = IntegrateTimeBudget.runWithin(() -> engine.evaluate(integral),
        MachineProfile.seconds((long) INTEGRAL_SECONDS) * 1000L);
    if (value.isNIL() || value.isAST(S.$Aborted)) {
      return F.NIL;
    }
    if (value.isAST(S.ConditionalExpression, 3)) {
      IExpr condition = value.second();
      if (!conditions.contains(condition)) {
        conditions.append(condition);
      }
      value = engine.evaluate(value.first());
    }
    if (!isUsable(value) || !value.isFreeAST(
        h -> h == S.Integrate || h == S.ConditionalExpression || h == S.Piecewise
            || h == S.Boole)) {
      return F.NIL;
    }
    return value;
  }

  /** Whether the engine came back with a value rather than with a way of saying it could not. */
  private static boolean isUsable(IExpr expr) {
    return expr.isPresent() && expr.isSpecialsFree() && !expr.isIndeterminate()
        && !expr.isDirectedInfinity() && !expr.isAST(S.$Aborted);
  }

  /** The expression as a real number at one point, or {@link F#NIL}. */
  private static IExpr numeric(IExpr expr, IExpr x, IExpr point, EvalEngine engine) {
    try {
      IExpr value = engine.evaluate(F.N(F.subst(expr, x, point)));
      return value.isReal() && Double.isFinite(value.evalf()) ? value : F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Whether the constant in the logarithm keeps it real over the whole interval.
   *
   * <p>
   * Only decided when it is a number; a symbolic one is answered with the formula, which carries
   * the <code>ArcCos</code> of it and says the same thing.
   */
  private static boolean isInRange(IExpr c, EvalEngine engine) {
    if (!c.isNumber()) {
      return true;
    }
    IExpr value = engine.evaluate(F.N(c));
    return value.isReal() && value.evalf() >= -1.0;
  }
}
