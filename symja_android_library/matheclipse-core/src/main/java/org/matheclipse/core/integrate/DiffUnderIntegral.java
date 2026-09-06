package org.matheclipse.core.integrate;

import org.matheclipse.core.basic.Config;
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

  /** How long the one simplification this needs may take. */
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
          STEP_SECONDS);
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
