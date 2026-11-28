package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Limits and comparisons at a limit point: <code>MaxLimit</code>, <code>MinLimit</code>,
 * <code>SumConvergence</code> and the asymptotic comparisons <code>AsymptoticLess</code>,
 * <code>AsymptoticLessEqual</code>, <code>AsymptoticGreater</code>,
 * <code>AsymptoticGreaterEqual</code>, <code>AsymptoticEqual</code> and
 * <code>AsymptoticEquivalent</code>; the curve functions <code>ArcCurvature</code> and
 * <code>FrenetSerretSystem</code>; and <code>FindMaxValue</code> and
 * <code>ParametricNDSolve</code>.
 */
public final class AsymptoticComparisonFunctions {

  private static class Initializer {

    private static void init() {
      S.MaxLimit.setEvaluator(new ExtremeLimit(true));
      S.MinLimit.setEvaluator(new ExtremeLimit(false));
      S.SumConvergence.setEvaluator(new SumConvergence());
      S.AsymptoticLess.setEvaluator(new AsymptoticComparison(S.AsymptoticLess));
      S.AsymptoticLessEqual.setEvaluator(new AsymptoticComparison(S.AsymptoticLessEqual));
      S.AsymptoticGreater.setEvaluator(new AsymptoticComparison(S.AsymptoticGreater));
      S.AsymptoticGreaterEqual.setEvaluator(new AsymptoticComparison(S.AsymptoticGreaterEqual));
      S.AsymptoticEqual.setEvaluator(new AsymptoticComparison(S.AsymptoticEqual));
      S.AsymptoticEquivalent.setEvaluator(new AsymptoticComparison(S.AsymptoticEquivalent));
      S.ArcCurvature.setEvaluator(new ArcCurvature());
      S.FrenetSerretSystem.setEvaluator(new FrenetSerretSystem());
      S.FindMaxValue.setEvaluator(new FindMaxValue());
      S.ParametricNDSolve.setEvaluator(new ParametricNDSolve());
    }
  }

  /** A real number or a signed infinity: a limit which is decided. */
  private static boolean isDetermined(IExpr limit) {
    return limit.isReal() || limit.isInfinity() || limit.isNegativeInfinity();
  }

  /** <code>Limit(f, rule)</code> with the direction, or the plain limit for a NIL direction. */
  private static IExpr limit(IExpr f, IExpr rule, IExpr direction, EvalEngine engine) {
    IAST call = direction.isPresent() ? F.Limit(f, rule, F.Rule(S.Direction, direction))
        : F.Limit(f, rule);
    return engine.evalQuiet(call);
  }

  // ---------------------------------------------------------------------------------------------
  // MaxLimit and MinLimit

  /**
   * The largest (smallest) limit point of <code>f</code> at the point of the rule: the limit if it
   * exists, the larger (smaller) of the one-sided limits at a finite point, or for a term
   * <code>a Sin(g)</code> or <code>a Cos(g)</code> whose argument grows without bound the limit of
   * the rest plus (minus) <code>Abs(a)</code>.
   *
   * @return {@link F#NIL} if it isn't decided
   */
  static @AllowNIL IExpr extremeLimit(IExpr f, IExpr rule, boolean max, EvalEngine engine) {
    IExpr plain = limit(f, rule, F.NIL, engine);
    if (isDetermined(plain)) {
      return plain;
    }
    IExpr point = rule.second();
    if (!point.isInfinity() && !point.isNegativeInfinity() && point.isRealResult()) {
      IExpr above = limit(f, rule, F.stringx("FromAbove"), engine);
      IExpr below = limit(f, rule, F.stringx("FromBelow"), engine);
      if (isDetermined(above) && isDetermined(below)) {
        return engine.evaluate(max ? F.Max(above, below) : F.Min(above, below));
      }
    }
    // one oscillating term, the others with a limit
    IAST terms = f.isPlus() ? (IAST) f : F.list(f);
    IASTAppendable sum = F.PlusAlloc(terms.argSize());
    boolean oscillating = false;
    for (IExpr term : terms) {
      IExpr termLimit = limit(term, rule, F.NIL, engine);
      if (isDetermined(termLimit)) {
        sum.append(termLimit);
        continue;
      }
      if (oscillating) {
        return F.NIL;
      }
      IExpr[] scaledAbs = scaledAbs(term);
      if (scaledAbs != null) {
        // c Abs(a Sin(g)) comes as close as it likes to 0 and to Abs(c a)
        IExpr amplitude = oscillationAmplitude(F.Times(scaledAbs[0], scaledAbs[1]), rule, engine);
        if (amplitude.isNIL()) {
          return F.NIL;
        }
        oscillating = true;
        sum.append(max ? amplitude : F.C0);
        continue;
      }
      IExpr amplitude = oscillationAmplitude(term, rule, engine);
      if (amplitude.isNIL()) {
        return F.NIL;
      }
      oscillating = true;
      sum.append(max ? amplitude : F.Negate(amplitude));
    }
    if (!oscillating) {
      return F.NIL;
    }
    IExpr result = engine.evaluate(sum);
    return isDetermined(result) ? result : F.NIL;
  }

  /** <code>{c, x}</code> if the term is <code>c Abs(x)</code> with a positive number c, else null. */
  private static IExpr[] scaledAbs(IExpr term) {
    if (term.isAST(S.Abs, 2)) {
      return new IExpr[] {F.C1, term.first()};
    }
    if (term.isTimes() && term.argSize() == 2 && term.first().isReal() && term.first().isPositive()
        && term.second().isAST(S.Abs, 2)) {
      return new IExpr[] {term.first(), term.second().first()};
    }
    return null;
  }

  /**
   * <code>Abs(lim a)</code> for <code>a Sin(g)</code> or <code>a Cos(g)</code>, where
   * <code>g</code> grows without bound and <code>a</code> has a nonzero finite limit; NIL
   * otherwise.
   */
  private static @AllowNIL IExpr oscillationAmplitude(IExpr term, IExpr rule, EvalEngine engine) {
    IAST factors = term.isTimes() ? (IAST) term : F.list(term);
    for (int i = 1; i < factors.size(); i++) {
      IExpr trig = factors.get(i);
      if (!trig.isSin() && !trig.isCos()) {
        continue;
      }
      IExpr argument = limit(F.Abs(trig.first()), rule, F.NIL, engine);
      if (!argument.isInfinity()) {
        return F.NIL;
      }
      IExpr rest = factors.removeAtCopy(i);
      IExpr coefficient = term.isTimes() ? rest.oneIdentity1() : F.C1;
      IExpr amplitude = limit(coefficient, rule, F.NIL, engine);
      if (amplitude.isReal()) {
        return engine.evaluate(F.Abs(amplitude));
      }
      return F.NIL;
    }
    return F.NIL;
  }

  /**
   * <code>MaxLimit(f, x -&gt; x0)</code>, <code>MinLimit(f, x -&gt; x0)</code> - the largest and
   * the smallest limit point of <code>f</code> as <code>x</code> approaches <code>x0</code>.
   */
  private static final class ExtremeLimit extends AbstractFunctionEvaluator {

    private final boolean max;

    ExtremeLimit(boolean max) {
      this.max = max;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isRuleAST() || !ast.arg2().first().isSymbol()) {
        return F.NIL;
      }
      return extremeLimit(ast.arg1(), ast.arg2(), max, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // SumConvergence

  /**
   * <code>SumConvergence(f, n)</code> - <code>True</code> if <code>Sum(f, {n, n0, Infinity})</code>
   * converges, <code>False</code> if it diverges. A rational function converges if the degree of
   * its denominator exceeds that of its numerator by at least 2, an alternating one
   * <code>(-1)^n r(n)</code> if <code>r</code> tends to <code>0</code>; otherwise the terms must
   * tend to <code>0</code> and the ratio test decides. <code>x^n</code> converges for
   * <code>Abs(x) &lt; 1</code> and <code>1/n^p</code> for <code>Re(p) &gt; 1</code>.
   */
  private static final class SumConvergence extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr f = ast.arg1();
      final IExpr n = ast.arg2();
      if (!n.isSymbol()) {
        return F.NIL;
      }
      if (f.isPower()) {
        if (f.exponent().equals(n) && f.base().isSymbol() && f.base().isFree(n)) {
          return F.Less(F.Abs(f.base()), F.C1);
        }
        if (f.base().equals(n) && f.exponent().isFree(n) && !f.exponent().isNumber()) {
          return F.Greater(F.Re(engine.evaluate(F.Negate(f.exponent()))), F.C1);
        }
      }
      IExpr rule = F.Rule(n, F.CInfinity);
      IExpr termLimit = limit(f, rule, F.NIL, engine);
      if (termLimit.isPresent() && isDetermined(termLimit) && !termLimit.isZero()) {
        return S.False;
      }
      // (-1)^n r(n)
      IExpr alternating = alternatingPart(f, n);
      if (alternating.isPresent()) {
        int difference = degreeDifference(alternating, n, engine);
        if (difference != Integer.MIN_VALUE) {
          return F.booleSymbol(difference >= 1);
        }
      } else {
        int difference = degreeDifference(f, n, engine);
        if (difference != Integer.MIN_VALUE) {
          return F.booleSymbol(difference >= 2);
        }
      }
      IExpr ratio = engine.evalQuiet(F.Limit(
          F.Abs(F.Simplify(F.FunctionExpand(F.Times(F.subst(f, n, F.Plus(n, F.C1)), F.Power(f, F.CN1))))),
          rule));
      if (ratio.isReal()) {
        double r = ratio.evalf();
        if (r < 1.0) {
          return S.True;
        }
        if (r > 1.0) {
          return S.False;
        }
      } else if (ratio.isInfinity()) {
        return S.False;
      }
      return F.NIL;
    }

    /** <code>r</code> if <code>f</code> is <code>(-1)^n r</code>, otherwise NIL. */
    private static @AllowNIL IExpr alternatingPart(IExpr f, IExpr n) {
      IAST factors = f.isTimes() ? (IAST) f : F.list(f);
      for (int i = 1; i < factors.size(); i++) {
        IExpr factor = factors.get(i);
        if (factor.isPower() && factor.base().isMinusOne() && factor.exponent().equals(n)) {
          return f.isTimes() ? factors.removeAtCopy(i).oneIdentity1() : F.C1;
        }
      }
      return F.NIL;
    }

    /**
     * The degree of the denominator minus the degree of the numerator of a rational function of
     * <code>n</code>, or <code>Integer.MIN_VALUE</code>.
     */
    private static int degreeDifference(IExpr f, IExpr n, EvalEngine engine) {
      IExpr together = engine.evaluate(F.Together(f));
      IExpr numerator = engine.evaluate(F.Numerator(together));
      IExpr denominator = engine.evaluate(F.Denominator(together));
      if (!numerator.isPolynomial(F.list(n)) || !denominator.isPolynomial(F.list(n))) {
        return Integer.MIN_VALUE;
      }
      int numeratorDegree = engine.evaluate(F.Exponent(numerator, n)).toIntDefault();
      int denominatorDegree = engine.evaluate(F.Exponent(denominator, n)).toIntDefault();
      if (numeratorDegree == Integer.MIN_VALUE || denominatorDegree == Integer.MIN_VALUE) {
        return Integer.MIN_VALUE;
      }
      return denominatorDegree - numeratorDegree;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // asymptotic comparisons

  /**
   * <code>AsymptoticLess(f, g, x -&gt; x0)</code> - <code>f</code> is <code>o(g)</code>:
   * <code>Limit(f/g) = 0</code>. <code>AsymptoticLessEqual</code> - <code>f</code> is
   * <code>O(g)</code>: <code>MaxLimit(Abs(f/g))</code> is finite. <code>AsymptoticGreater</code>
   * and <code>AsymptoticGreaterEqual</code> - the same with <code>f</code> and <code>g</code>
   * exchanged. <code>AsymptoticEqual</code> - both <code>O</code> relations.
   * <code>AsymptoticEquivalent</code> - <code>Limit(f/g) = 1</code>.
   */
  private static final class AsymptoticComparison extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol relation;

    AsymptoticComparison(IBuiltInSymbol relation) {
      this.relation = relation;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg3().isRuleAST() || !ast.arg3().first().isSymbol()) {
        return F.NIL;
      }
      final IExpr f = ast.arg1();
      final IExpr g = ast.arg2();
      final IExpr rule = ast.arg3();
      switch (relation.ordinal()) {
        case org.matheclipse.core.expression.ID.AsymptoticLess:
          return less(f, g, rule, engine);
        case org.matheclipse.core.expression.ID.AsymptoticGreater:
          return less(g, f, rule, engine);
        case org.matheclipse.core.expression.ID.AsymptoticLessEqual:
          return lessEqual(f, g, rule, engine);
        case org.matheclipse.core.expression.ID.AsymptoticGreaterEqual:
          return lessEqual(g, f, rule, engine);
        case org.matheclipse.core.expression.ID.AsymptoticEqual: {
          IExpr first = lessEqual(f, g, rule, engine);
          IExpr second = lessEqual(g, f, rule, engine);
          if (first.isFalse() || second.isFalse()) {
            return S.False;
          }
          return first.isTrue() && second.isTrue() ? S.True : F.NIL;
        }
        default: {
          IExpr ratio = limit(F.Times(f, F.Power(g, F.CN1)), rule, F.NIL, engine);
          return isDetermined(ratio) ? F.booleSymbol(ratio.isOne()) : F.NIL;
        }
      }
    }

    private static @AllowNIL IExpr less(IExpr f, IExpr g, IExpr rule, EvalEngine engine) {
      IExpr ratio = limit(F.Times(f, F.Power(g, F.CN1)), rule, F.NIL, engine);
      return isDetermined(ratio) ? F.booleSymbol(ratio.isZero()) : F.NIL;
    }

    private static @AllowNIL IExpr lessEqual(IExpr f, IExpr g, IExpr rule, EvalEngine engine) {
      IExpr bound = extremeLimit(F.Abs(F.Times(f, F.Power(g, F.CN1))), rule, true, engine);
      if (bound.isReal()) {
        return S.True;
      }
      return bound.isInfinity() ? S.False : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // curves

  /** The vector of the derivatives of the components, or of <code>{t, f}</code> for a function. */
  private static IAST curve(IExpr r, IExpr t) {
    return r.isList() ? (IAST) r : F.list(t, r);
  }

  /** The expression simplified for a real parameter. */
  private static IExpr simplifyReal(IExpr expr, IExpr t, EvalEngine engine) {
    return engine.evaluate(F.Simplify(expr, F.Element(t, S.Reals)));
  }

  private static IExpr dot(IExpr u, IExpr v) {
    return F.Dot(u, v);
  }

  /**
   * <code>ArcCurvature({x1(t), x2(t), ...}, t)</code> - the curvature
   * <code>Sqrt(Abs(r')^2 Abs(r'')^2 - (r'.r'')^2)/Abs(r')^3</code> of a curve with a real
   * parameter; <code>ArcCurvature(f, t)</code> - the curvature of the graph of <code>f</code>.
   */
  private static final class ArcCurvature extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr t = ast.arg2();
      if (!t.isSymbol()) {
        return F.NIL;
      }
      IAST r = curve(ast.arg1(), t);
      IExpr d1 = engine.evaluate(F.D(r, t));
      IExpr d2 = engine.evaluate(F.D(d1, t));
      IExpr speed2 = dot(d1, d1);
      IExpr numerator = F.Sqrt(F.Subtract(F.Times(speed2, dot(d2, d2)), F.Sqr(dot(d1, d2))));
      return simplifyReal(F.Times(numerator, F.Power(speed2, F.QQ(-3, 2))), t, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>FrenetSerretSystem({x(t), y(t)}, t)</code> - <code>{{k}, {T, N}}</code> with the signed
   * curvature <code>k</code>, the unit tangent <code>T</code> and the unit normal <code>N</code>,
   * which is <code>T</code> turned by 90 degrees. <code>FrenetSerretSystem({x(t), y(t), z(t)}, t)</code>
   * - <code>{{k, tau}, {T, N, B}}</code> with the curvature, the torsion and the binormal
   * <code>B = T x N</code>. A function <code>f</code> is the curve <code>{t, f}</code>.
   */
  private static final class FrenetSerretSystem extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr t = ast.arg2();
      if (!t.isSymbol()) {
        return F.NIL;
      }
      IAST r = curve(ast.arg1(), t);
      if (r.argSize() != 2 && r.argSize() != 3) {
        return F.NIL;
      }
      IExpr d1 = engine.evaluate(F.D(r, t));
      IExpr d2 = engine.evaluate(F.D(d1, t));
      IExpr speed = F.Sqrt(dot(d1, d1));
      IExpr tangent = F.Times(d1, F.Power(speed, F.CN1));
      if (r.argSize() == 2) {
        IExpr cross = F.Subtract(F.Times(d1.first(), d2.second()), F.Times(d1.second(), d2.first()));
        IExpr k = F.Times(cross, F.Power(speed, F.CN3));
        IExpr t1 = simplifyReal(tangent, t, engine);
        if (!t1.isList2()) {
          return F.NIL;
        }
        IExpr normal = F.list(F.Negate(t1.second()), t1.first());
        return F.list(F.list(simplifyReal(k, t, engine)),
            F.list(t1, simplifyReal(normal, t, engine)));
      }
      IExpr d3 = engine.evaluate(F.D(d2, t));
      IExpr cross = engine.evaluate(F.Cross(d1, d2));
      IExpr crossNorm2 = dot(cross, cross);
      IExpr k = F.Times(F.Sqrt(crossNorm2), F.Power(speed, F.CN3));
      IExpr tau = F.Times(dot(cross, d3), F.Power(crossNorm2, F.CN1));
      IExpr binormal = F.Times(cross, F.Power(F.Sqrt(crossNorm2), F.CN1));
      IExpr t1 = simplifyReal(tangent, t, engine);
      IExpr b = simplifyReal(binormal, t, engine);
      IExpr normal = simplifyReal(F.Cross(b, t1), t, engine);
      return F.list(F.list(simplifyReal(k, t, engine), simplifyReal(tau, t, engine)),
          F.list(t1, normal, b));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // FindMaxValue, ParametricNDSolve

  /**
   * <code>FindMaxValue(f, {x, x0})</code> - the value of the local maximum
   * <code>FindMaximum</code> finds.
   */
  private static final class FindMaxValue extends AbstractCoreFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr maximum = engine.evaluate(ast.setAtCopy(0, S.FindMaximum));
      if (maximum.isList2()) {
        return maximum.first();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * <code>ParametricNDSolve(eqns, u, {x, xmin, xmax}, pars)</code> - the rules
   * <code>{u -&gt; ParametricFunction(...)}</code> with the solutions
   * <code>ParametricNDSolveValue</code> gives; <code>u</code> may be a list of functions.
   */
  private static final class ParametricNDSolve extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr unknowns = ast.arg2();
      IAST list = unknowns.isList() ? (IAST) unknowns : F.list(unknowns);
      IASTAppendable rules = F.ListAlloc(list.argSize());
      for (IExpr u : list) {
        IExpr solution = engine.evaluate(ast.setAtCopy(0, S.ParametricNDSolveValue).setAtCopy(2, u));
        if (solution.isAST(S.ParametricNDSolveValue)) {
          return F.NIL;
        }
        rules.append(F.Rule(u, solution));
      }
      return rules;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return new int[] {4, Integer.MAX_VALUE};
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AsymptoticComparisonFunctions() {}
}
