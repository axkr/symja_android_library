package org.matheclipse.core.integrate;

import java.util.function.UnaryOperator;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Integration of a root nested on a conic by rationalizing the conic first.
 *
 * <p>
 * If a root of the integrand contains <code>y = Sqrt(q)</code> with a quadratic <code>q</code>, an
 * Euler substitution writes <code>x</code> and <code>y</code> as rational functions of a new
 * variable <code>t</code>, and the integrand becomes a function of <code>t</code> with one level of
 * roots less:
 *
 * <pre>
 * q = a^2*x^2 + b*x + c:  t = y + a*x,                x = (t^2 - c) / (b + 2*a*t),  y = t - a*x
 * q = a*x^2 + b*x + c^2:  t = (a*x + b) / (y + c),    x = (b - 2*c*t) / (t^2 - a),  y = c + x*t
 * </pre>
 *
 * <p>
 * Example: with <code>t = Sqrt(4*x^2+3) + 2*x</code> the base of
 * <code>Sqrt(2*x^2 + x*Sqrt(4*x^2+3))</code> is <code>(t^2-3)/4</code>, a root the rules can
 * integrate. The square root of a coefficient may be either sign of a symbolic expression
 * (<code>a</code> for <code>a^2</code>), because only its square enters the substitution. The
 * definition of <code>t</code> holds for both values of the root <code>y</code>, so no branch has
 * to be chosen. The second <code>t</code> is <code>(y - c) / x</code> without the <code>0/0</code>
 * at <code>x = 0</code>. An integrand in <code>Sqrt(x)</code> is first written in
 * <code>s = Sqrt(x)</code>.
 *
 * <p>
 * The answer in <code>x</code> is verified by differentiation, on and off the real line and with
 * numbers of both signs for the parameters: the rules may answer for positive parameters only.
 */
public final class NestedRootEuler {

  /** The points of the numeric diff-back check, of both signs. */
  private static final double[] SAMPLE_POINTS = {0.37, 0.61, 1.37, 2.19, -0.43, -1.3};

  /** The values of the parameters in the diff-back check. */
  private static final int[] PARAMETER_VALUES = {2, 3, 5, 7, 11, 13, 17, 19};

  /** The points off the real line for positive parameters. */
  private static final int OFF_LINE_POINTS = 4;

  /** The points off the real line with one negative parameter: an answer has to be checked soon. */
  private static final int SIGNED_OFF_LINE_POINTS = 2;

  /** The time for the integral of one substitution: one which hangs must not stop the next. */
  private static final long ATTEMPT_TIMELIMIT_MILLIS = 4000L;

  /** Recursion depth guard: the rewritten integrand re-enters <code>Integrate</code>. */
  private static final ThreadLocal<Integer> RECURSION_DEPTH = ThreadLocal.withInitial(() -> 0);

  /** The integrand of the caller, and how an antiderivative of a rewritten one goes back to it. */
  private static final class Target {
    final IExpr integrand;
    final IExpr x;
    final UnaryOperator<IExpr> toTarget;
    final EvalEngine engine;

    Target(IExpr integrand, IExpr x, UnaryOperator<IExpr> toTarget, EvalEngine engine) {
      this.integrand = integrand;
      this.x = x;
      this.toTarget = toTarget;
      this.engine = engine;
    }
  }

  private NestedRootEuler() {}

  /**
   * Integrate a root nested on a conic.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    int depth = RECURSION_DEPTH.get();
    if (depth >= 2) {
      return F.NIL;
    }
    RECURSION_DEPTH.set(depth + 1);
    try {
      IExpr conic = nestedConic(integrand, x, engine);
      if (conic.isPresent()) {
        Target target = new Target(integrand, x, UnaryOperator.identity(), engine);
        return integrateOnConic(integrand, x, conic, target);
      }
      return integrateInRootOfVariable(integrand, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      RECURSION_DEPTH.set(depth);
    }
  }

  /** Write the integrand in <code>s = Sqrt(x)</code>, if that shows a conic under a root. */
  private static IExpr integrateInRootOfVariable(IExpr integrand, IExpr x, EvalEngine engine) {
    final ISymbol s = F.Dummy("ne$s");
    IExpr inRoot = F.subst(integrand, e -> {
      if (e.isPower() && e.base().equals(x) && e.exponent().isRational()
          && !e.exponent().isInteger() && ((IRational) e.exponent()).denominator().equals(F.C2)) {
        return F.Power(s, ((IRational) e.exponent()).multiply(F.C2));
      }
      return F.NIL;
    });
    if (inRoot.equals(integrand)) {
      return F.NIL;
    }
    IExpr rewritten = engine.evaluate(F.Times(F.C2, s, F.subst(inRoot, x, F.Sqr(s))));
    IExpr conic = nestedConic(rewritten, s, engine);
    if (conic.isNIL()) {
      return F.NIL;
    }
    Target target =
        new Target(integrand, x, g -> engine.evaluate(F.subst(g, s, F.Sqrt(x))), engine);
    return integrateOnConic(rewritten, s, conic, target);
  }

  private static IExpr integrateOnConic(IExpr integrand, IExpr x, IExpr conic, Target target) {
    final EvalEngine engine = target.engine;
    IExpr expanded = engine.evaluate(F.Expand(conic));
    final IExpr c0 = engine.evaluate(F.Coefficient(expanded, x, F.C0));
    final IExpr c1 = engine.evaluate(F.Coefficient(expanded, x, F.C1));
    final IExpr c2 = engine.evaluate(F.Coefficient(expanded, x, F.C2));
    final ISymbol t = F.Dummy("ne$t");
    final IExpr y = F.Sqrt(conic);
    IExpr result = withBothSigns(squareRoot(c2, engine), a -> {
      IExpr xt = F.Divide(F.Subtract(F.Sqr(t), c0), F.Plus(c1, F.Times(F.C2, a, t)));
      IExpr yt = F.Subtract(t, F.Times(a, xt));
      return integrateIn(t, integrand, x, conic, xt, yt, F.Plus(y, F.Times(a, x)), target);
    }, engine);
    if (result.isPresent()) {
      return result;
    }
    return withBothSigns(squareRoot(c0, engine), c -> {
      IExpr xt = F.Divide(F.Subtract(c1, F.Times(F.C2, c, t)), F.Subtract(F.Sqr(t), c2));
      IExpr yt = F.Plus(c, F.Times(xt, t));
      IExpr back = F.Divide(F.Plus(c1, F.Times(c2, x)), F.Plus(y, c));
      return integrateIn(t, integrand, x, conic, xt, yt, back, target);
    }, engine);
  }

  /** The first answer of <code>attempt</code> for <code>root</code> and for its negative. */
  private static IExpr withBothSigns(IExpr root, UnaryOperator<IExpr> attempt, EvalEngine engine) {
    if (root.isNIL()) {
      return F.NIL;
    }
    IExpr result = attempt.apply(root);
    return result.isPresent() || root.isZero() ? result
        : attempt.apply(engine.evaluate(F.Negate(root)));
  }

  /**
   * Integrate in <code>t</code>, where <code>x = xt</code> and <code>Sqrt(conic) = yt</code>, and
   * write the answer with <code>t = back</code>.
   */
  private static IExpr integrateIn(ISymbol t, IExpr integrand, IExpr x, IExpr conic, IExpr xt,
      IExpr yt, IExpr back, Target target) {
    final EvalEngine engine = target.engine;
    IExpr inT = F.subst(integrand, e -> {
      if (e.isPower() && e.base().equals(conic) && e.exponent().isRational()) {
        return F.Power(yt, ((IRational) e.exponent()).multiply(F.C2));
      }
      return F.NIL;
    });
    inT = togetherRootBases(F.subst(inT, x, xt), t, engine);
    IExpr rewritten = F.Times(inT, engine.evaluate(F.Together(F.D(xt, t))));
    IExpr antiderivative = IntegrateTimeBudget.runWithin(
        () -> engine.evaluate(F.Integrate(rewritten, t)),
        MachineProfile.millis(ATTEMPT_TIMELIMIT_MILLIS));
    if (antiderivative.isNIL() || !antiderivative.isFreeAST(S.Integrate)) {
      return F.NIL;
    }
    IExpr result = target.toTarget.apply(engine.evaluate(F.subst(antiderivative, t, back)));
    return isAntiderivative(result, target) ? result : F.NIL;
  }

  /** Bring the base of every root to one fraction: <code>a*x+y</code> collapses to <code>t</code>. */
  private static IExpr togetherRootBases(IExpr expr, IExpr t, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(t, true)) {
      return expr;
    }
    IASTMutable copy = ((IAST) expr).copy();
    for (int i = 1; i < copy.size(); i++) {
      copy.set(i, togetherRootBases(copy.get(i), t, engine));
    }
    if (isRoot(copy) && !copy.base().isFree(t, true)) {
      copy.set(1, engine.evaluate(F.Together(copy.base())));
    }
    return engine.evaluate(copy);
  }

  /**
   * The diff-back check of the answer in the variable of the caller. Parameters get numbers, so that
   * the sample points give numbers: positive ones, then each parameter once with the other sign.
   */
  private static boolean isAntiderivative(IExpr result, Target target) {
    final EvalEngine engine = target.engine;
    IAST variables = new VariablesSet(target.integrand).getVarList();
    int parameters = Math.min(variables.argSize() - 1, PARAMETER_VALUES.length);
    for (int negative = -1; negative < parameters; negative++) {
      IASTAppendable rules = F.ListAlloc(variables.argSize());
      int next = 0;
      for (int i = 1; i < variables.size() && next < parameters; i++) {
        if (!variables.get(i).equals(target.x)) {
          int value = PARAMETER_VALUES[next];
          rules.append(F.Rule(variables.get(i), F.ZZ(next == negative ? -value : value)));
          next++;
        }
      }
      IExpr antiderivative = F.subst(result, rules);
      IExpr f = F.subst(target.integrand, rules);
      if (negative < 0 && !AntiderivativeCheck.verifyWithNumericFallback(antiderivative, f,
          target.x, SAMPLE_POINTS, engine)) {
        return false;
      }
      if (!AntiderivativeCheck.agreesOffTheRealLine(antiderivative, f, target.x,
          negative < 0 ? OFF_LINE_POINTS : SIGNED_OFF_LINE_POINTS, engine)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The polynomial <code>r</code> with <code>r^2 == c</code>, one of two: a rational number, or
   * free of roots for a symbolic <code>c</code>. {@link F#NIL} if there is none.
   */
  private static IExpr squareRoot(IExpr c, EvalEngine engine) {
    IExpr root = engine.evaluate(F.PowerExpand(F.Sqrt(c)));
    boolean rootFree = root.isFree(e -> isRoot(e) || (e.isNumber() && !e.isRational()), true);
    return rootFree ? root : F.NIL;
  }

  /** The first quadratic under a square root which is itself under a root, or {@link F#NIL}. */
  private static IExpr nestedConic(IExpr expr, IExpr x, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(x, true)) {
      return F.NIL;
    }
    if (isRoot(expr)) {
      IExpr conic = conic(expr.base(), x, engine);
      if (conic.isPresent()) {
        return conic;
      }
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      IExpr conic = nestedConic(ast.get(i), x, engine);
      if (conic.isPresent()) {
        return conic;
      }
    }
    return F.NIL;
  }

  private static IExpr conic(IExpr expr, IExpr x, EvalEngine engine) {
    if (!expr.isAST() || expr.isFree(x, true)) {
      return F.NIL;
    }
    if (isRoot(expr) && ((IRational) expr.exponent()).denominator().equals(F.C2)
        && expr.base().isPolynomialOfMaxDegree(x, 2)
        && engine.evaluate(F.Exponent(expr.base(), x)).equals(F.C2)) {
      return expr.base();
    }
    IAST ast = (IAST) expr;
    for (int i = 1; i < ast.size(); i++) {
      IExpr conic = conic(ast.get(i), x, engine);
      if (conic.isPresent()) {
        return conic;
      }
    }
    return F.NIL;
  }

  private static boolean isRoot(IExpr expr) {
    return expr.isPower() && expr.exponent().isRational() && !expr.exponent().isInteger();
  }
}
