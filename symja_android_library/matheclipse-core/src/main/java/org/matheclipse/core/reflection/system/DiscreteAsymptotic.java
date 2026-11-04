package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>DiscreteAsymptotic(expr, n -&gt; Infinity)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the leading term of the sequence <code>expr</code> as the integer <code>n</code> tends to
 * infinity.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>DiscreteAsymptotic(expr, {n, Infinity, m})
 * DiscreteAsymptotic(expr, n -&gt; Infinity, SeriesTermGoal -&gt; m)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives <code>m</code> terms of the expansion of a factorial or Gamma type sequence.
 * </p>
 * </blockquote>
 *
 * <p>
 * The leading term of a compound expression is assembled from the leading terms of its parts.
 * Every leading term carries a numeric growth key <code>n^(a*n)*E^(b*n)*n^p*Log(n)^q</code>, so
 * that the terms of a sum can be compared: the dominant one survives and terms of equal growth are
 * added. Hyperbolic functions are rewritten into exponentials and logarithms, the special sequences
 * (Gamma, Fibonacci, Binomial, ...) use their classical asymptotic formulas and
 * <code>SeriesCoefficient(f, {z, 0, n})</code> is found from the nearest simple pole of a
 * meromorphic <code>f</code>.
 * </p>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; DiscreteAsymptotic((3*n^3 + 5)/(4*n^2 + 11), n -&gt; Infinity)
 * 3/4*n
 * </code>
 * </pre>
 */
public class DiscreteAsymptotic extends AbstractFunctionOptionEvaluator {

  private static final double EPS = 1e-9;

  /** The largest number of terms of a Stirling expansion. */
  private static final int MAX_GOAL = 20;

  /** The deepest nesting of the leading term rules. */
  private static final int MAX_DEPTH = 64;

  /**
   * The growth exponents of <code>n^(nlogn*n) * E^(exp*n) * n^pow * Log(n)^log</code>, compared
   * lexicographically. <code>NaN</code> marks a component which isn't known numerically.
   */
  private static final class Growth {
    static final Growth ONE = new Growth(0.0, 0.0, 0.0, 0.0);
    static final Growth UNKNOWN = new Growth(Double.NaN, Double.NaN, Double.NaN, Double.NaN);

    final double[] c;

    Growth(double nlogn, double exp, double pow, double log) {
      c = new double[] {nlogn, exp, pow, log};
    }

    Growth times(Growth o) {
      return new Growth(c[0] + o.c[0], c[1] + o.c[1], c[2] + o.c[2], c[3] + o.c[3]);
    }

    Growth scale(double k) {
      return new Growth(c[0] * k, c[1] * k, c[2] * k, c[3] * k);
    }

    /**
     * Lexicographic comparison.
     *
     * @return <code>null</code> if a deciding component is unknown
     */
    Integer compare(Growth o) {
      for (int i = 0; i < 4; i++) {
        if (Double.isNaN(c[i]) || Double.isNaN(o.c[i])) {
          return null;
        }
        if (Math.abs(c[i] - o.c[i]) > EPS) {
          return c[i] < o.c[i] ? -1 : 1;
        }
      }
      return 0;
    }

    /**
     * Whether the sequence grows without bound.
     *
     * @return <code>null</code> if a deciding component is unknown
     */
    Boolean tendsToInfinity() {
      for (int i = 0; i < 4; i++) {
        if (Double.isNaN(c[i])) {
          return null;
        }
        if (Math.abs(c[i]) > EPS) {
          return c[i] > 0.0;
        }
      }
      return Boolean.FALSE;
    }

    boolean isConstant() {
      for (int i = 0; i < 4; i++) {
        if (!(Math.abs(c[i]) <= EPS)) {
          return false;
        }
      }
      return true;
    }
  }

  /** A leading term <code>expr</code> with its growth, or the zero sequence. */
  private static final class Term {
    final IExpr expr;
    final Growth growth;
    final boolean zero;

    Term(IExpr expr, Growth growth) {
      this(expr, growth, false);
    }

    Term(IExpr expr, Growth growth, boolean zero) {
      this.expr = expr;
      this.growth = growth;
      this.zero = zero;
    }

    static Term zero() {
      return new Term(F.C0, Growth.ONE, true);
    }
  }

  /** The leading term search for one index variable. */
  private static final class Search {
    final ISymbol n;
    final EvalEngine engine;
    int depth = 0;

    Search(ISymbol n, EvalEngine engine) {
      this.n = n;
      this.engine = engine;
    }

    IExpr eval(IExpr e) {
      return engine.evalQuiet(e);
    }

    double numeric(IExpr e) {
      if (!e.isNumericFunction(true)) {
        return Double.NaN;
      }
      try {
        return engine.evalDoubleNaN(e);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Double.NaN;
      }
    }

    boolean isVar(IExpr e) {
      return e.equals(n);
    }

    /**
     * <code>e == c*n + d</code> with <code>c</code>, <code>d</code> free of <code>n</code>.
     *
     * @return <code>null</code> if <code>e</code> isn't linear in <code>n</code>
     */
    IExpr[] linearParts(IExpr e) {
      if (!e.isPolynomial(n)) {
        return null;
      }
      IExpr c = eval(F.D(e, n));
      if (!c.isFree(n)) {
        return null;
      }
      IExpr d = eval(F.subst(e, n, F.C0));
      return new IExpr[] {c, d};
    }

    /** <code>e == 2*n</code>, the argument of <code>BernoulliB(2*n)</code>. */
    boolean isTwiceVar(IExpr e) {
      IExpr[] parts = linearParts(e);
      return parts != null && parts[0].equals(F.C2) && parts[1].isZero();
    }

    /**
     * The leading term of <code>expr</code>. A sum whose leading terms cancel or the logarithm of a
     * sequence tending to <code>1</code> falls back to the expansion at infinity.
     */
    Term lead(IExpr expr) {
      engine.checkTimeLimit();
      if (depth > MAX_DEPTH) {
        return null;
      }
      depth++;
      try {
        Term term = leadRules(expr);
        if (term == null && (expr.isPlus() || expr.isAST(S.Log, 2))) {
          term = leadBySeries(expr);
        }
        return term;
      } finally {
        depth--;
      }
    }

    /** The first nonzero term of the series of <code>expr</code> at infinity. */
    Term leadBySeries(IExpr expr) {
      for (int order : new int[] {4, 12}) {
        IExpr series = eval(F.Series(expr, F.list(n, F.CInfinity, F.ZZ(order))));
        if (!(series instanceof ASTSeriesData)) {
          return null;
        }
        ASTSeriesData sd = (ASTSeriesData) series;
        int den = sd.puiseuxDenominator();
        for (int i = sd.minExponent(); i < sd.truncateOrder(); i++) {
          IExpr coefficient = sd.coefficient(i);
          if (!coefficient.isZero()) {
            IExpr term = eval(F.Times(coefficient, F.Power(n, F.QQ(-i, den))));
            Term t = leadRules(term);
            return t == null ? null : new Term(term, t.growth);
          }
        }
      }
      return null;
    }

    Term leadRules(IExpr expr) {
      if (expr.isFree(n)) {
        if (expr.isZero()) {
          return Term.zero();
        }
        return new Term(expr, Growth.ONE);
      }
      if (isVar(expr)) {
        return new Term(expr, new Growth(0.0, 0.0, 1.0, 0.0));
      }
      if (!expr.isAST()) {
        return null;
      }
      IAST ast = (IAST) expr;
      if (ast.head().isAST(S.Inactive, 2)) {
        return leadInactive(ast);
      }
      if (ast.isPlus()) {
        return leadSum(ast);
      }
      if (ast.isAST(S.Piecewise) && ast.arg1().isListOfLists()) {
        return leadPiecewise(ast);
      }
      if (ast.isTimes()) {
        Growth growth = Growth.ONE;
        IASTAppendable product = F.TimesAlloc(ast.argSize());
        for (IExpr factor : ast) {
          Term l = lead(factor);
          if (l == null) {
            return null;
          }
          if (l.zero) {
            return l;
          }
          growth = growth.times(l.growth);
          product.append(l.expr);
        }
        return new Term(eval(product), growth);
      }
      if (ast.isPower()) {
        return leadPower(ast.base(), ast.exponent());
      }
      if (ast.argSize() == 1) {
        IExpr x = ast.arg1();
        switch (ast.headID()) {
          case org.matheclipse.core.expression.ID.Log:
            return leadLog(x);
          case org.matheclipse.core.expression.ID.Sinh:
            return lead(F.Times(F.C1D2, F.Subtract(F.Exp(x), F.Exp(F.Negate(x)))));
          case org.matheclipse.core.expression.ID.Cosh:
            return lead(F.Times(F.C1D2, F.Plus(F.Exp(x), F.Exp(F.Negate(x)))));
          case org.matheclipse.core.expression.ID.Tanh:
            return lead(F.Times(F.Subtract(F.Exp(x), F.Exp(F.Negate(x))),
                F.Power(F.Plus(F.Exp(x), F.Exp(F.Negate(x))), F.CN1)));
          case org.matheclipse.core.expression.ID.Coth:
            return lead(F.Times(F.Plus(F.Exp(x), F.Exp(F.Negate(x))),
                F.Power(F.Subtract(F.Exp(x), F.Exp(F.Negate(x))), F.CN1)));
          case org.matheclipse.core.expression.ID.Sech:
            return lead(F.Times(F.C2, F.Power(F.Plus(F.Exp(x), F.Exp(F.Negate(x))), F.CN1)));
          case org.matheclipse.core.expression.ID.Csch:
            return lead(
                F.Times(F.C2, F.Power(F.Subtract(F.Exp(x), F.Exp(F.Negate(x))), F.CN1)));
          case org.matheclipse.core.expression.ID.ArcSinh:
            return leadLog(F.Plus(x, F.Sqrt(F.Plus(F.Sqr(x), F.C1))));
          case org.matheclipse.core.expression.ID.ArcCosh:
            return leadLog(F.Plus(x, F.Sqrt(F.Plus(F.Sqr(x), F.CN1))));
          case org.matheclipse.core.expression.ID.Factorial:
            return leadGamma(eval(F.Plus(x, F.C1)), 1);
          case org.matheclipse.core.expression.ID.Gamma:
            return leadGamma(x, 1);
          case org.matheclipse.core.expression.ID.HarmonicNumber:
          case org.matheclipse.core.expression.ID.PolyGamma:
            return leadLog(x);
          case org.matheclipse.core.expression.ID.Zeta: {
            Term l = lead(x);
            if (l == null || l.zero || !Boolean.TRUE.equals(l.growth.tendsToInfinity())) {
              return null;
            }
            double v = numeric(l.expr);
            if (!Double.isNaN(v) && v <= 0.0) {
              return null;
            }
            return new Term(F.C1, Growth.ONE);
          }
          case org.matheclipse.core.expression.ID.Prime:
            return lead(F.Times(x, F.Log(x)));
          case org.matheclipse.core.expression.ID.Fibonacci:
            if (linearParts(x) == null) {
              return null;
            }
            return lead(F.Times(F.Power(S.GoldenRatio, x), F.Power(F.C5, F.CN1D2)));
          case org.matheclipse.core.expression.ID.LucasL:
            if (linearParts(x) == null) {
              return null;
            }
            return lead(F.Power(S.GoldenRatio, x));
          case org.matheclipse.core.expression.ID.BellB:
            if (isVar(x)) {
              // E^(n/W - n - 1)*(n/W)^(n + 1/2)/Sqrt(n) with W = ProductLog(n)
              IExpr w = F.Times(n, F.Power(F.ProductLog(n), F.CN1));
              IExpr e = F.Times(F.Exp(F.Plus(F.CN1, F.Negate(n), w)),
                  F.Power(w, F.Plus(F.C1D2, n)), F.Power(n, F.CN1D2));
              return new Term(eval(e), new Growth(1.0, Double.NaN, Double.NaN, Double.NaN));
            }
            return null;
          case org.matheclipse.core.expression.ID.BernoulliB:
            if (isTwiceVar(x)) {
              // B(2n) ~ (-1)^(n+1)*4*Sqrt(Pi*n)*(n/(Pi*E))^(2n)
              IExpr e = F.Times(F.C4, F.Power(F.CN1, F.Plus(F.CN1, n)),
                  F.Exp(F.Times(F.CN2, n)), F.Power(n, F.Plus(F.C1D2, F.Times(F.C2, n))),
                  F.Power(S.Pi, F.Plus(F.C1D2, F.Times(F.CN2, n))));
              return new Term(eval(e), new Growth(2.0, -2.0 - 2.0 * Math.log(Math.PI), 0.5, 0.0));
            }
            return null;
          case org.matheclipse.core.expression.ID.EulerE:
            if (isTwiceVar(x)) {
              // E(2n) ~ (-1)^n*8*Sqrt(n/Pi)*(4*n/(Pi*E))^(2n)
              IExpr e = F.Times(F.Power(F.CN1, n), F.Power(F.C2, F.Plus(F.C3, F.Times(F.C4, n))),
                  F.Exp(F.Times(F.CN2, n)), F.Power(n, F.Plus(F.C1D2, F.Times(F.C2, n))),
                  F.Power(S.Pi, F.Plus(F.CN1D2, F.Times(F.CN2, n))));
              return new Term(eval(e),
                  new Growth(2.0, 4.0 * Math.log(2.0) - 2.0 - 2.0 * Math.log(Math.PI), 0.5, 0.0));
            }
            return null;
          default:
            return null;
        }
      }
      if (ast.argSize() == 2) {
        IExpr a = ast.arg1();
        IExpr x = ast.arg2();
        switch (ast.headID()) {
          case org.matheclipse.core.expression.ID.Pochhammer:
            if (a.isFree(n)) {
              return lead(F.Times(F.Gamma(F.Plus(a, x)), F.Power(F.Gamma(a), F.CN1)));
            }
            return null;
          case org.matheclipse.core.expression.ID.FactorialPower:
            return lead(F.Times(F.Gamma(F.Plus(F.C1, a)),
                F.Power(F.Gamma(F.Plus(F.C1, a, F.Negate(x))), F.CN1)));
          case org.matheclipse.core.expression.ID.Binomial:
            return leadBinomial(a, x);
          case org.matheclipse.core.expression.ID.PolyGamma:
            if (a.isZero()) {
              return leadLog(x);
            }
            return null;
          case org.matheclipse.core.expression.ID.SeriesCoefficient:
            return leadSeriesCoefficient(a, x);
          default:
            return null;
        }
      }
      return null;
    }

    /**
     * The piece of a <code>Piecewise</code> function which holds for all large <code>n</code>: the
     * first one whose condition is true at a large index, for conditions free of other variables.
     */
    Term leadPiecewise(IAST piecewise) {
      IExpr large = F.ZZ(1L << 40);
      for (IExpr row : (IAST) piecewise.arg1()) {
        if (!row.isList2()) {
          return null;
        }
        IExpr condition = eval(F.subst(row.second(), n, large));
        if (condition.isTrue()) {
          return lead(row.first());
        }
        if (!condition.isFalse()) {
          return null;
        }
      }
      return lead(piecewise.argSize() > 1 ? piecewise.arg2() : F.C0);
    }

    /** <code>Inactive(h)(args)</code>: activate it and use the result if <code>h</code> evaluated. */
    Term leadInactive(IAST ast) {
      IExpr h = ast.head().first();
      if (!h.isSymbol()) {
        return null;
      }
      if (h == S.SeriesCoefficient && ast.argSize() == 2) {
        return leadSeriesCoefficient(ast.arg1(), ast.arg2());
      }
      IExpr active = eval(ast.setAtCopy(0, h));
      if (active.isAST(h)) {
        return null;
      }
      return lead(active);
    }

    /** The dominant term of a sum; terms of equal growth are added. */
    Term leadSum(IAST plus) {
      Term best = null;
      List<IExpr> tied = new ArrayList<>();
      for (IExpr t : plus) {
        Term l = lead(t);
        if (l == null) {
          return null;
        }
        if (l.zero) {
          continue;
        }
        if (best == null) {
          tied.clear();
          tied.add(l.expr);
          best = l;
        } else {
          Integer cmp = l.growth.compare(best.growth);
          if (cmp == null) {
            return null;
          }
          if (cmp > 0) {
            tied.clear();
            tied.add(l.expr);
            best = l;
          } else if (cmp == 0) {
            tied.add(l.expr);
          }
        }
      }
      if (best == null) {
        return Term.zero();
      }
      if (tied.size() > 1) {
        IExpr sum = eval(F.Plus(tied.toArray(new IExpr[0])));
        if (sum.isZero()) {
          // the leading terms cancel: the next order would be needed
          return null;
        }
        return new Term(sum, best.growth);
      }
      return best;
    }

    Term leadPower(IExpr base, IExpr exponent) {
      if (exponent.isFree(n)) {
        Term l = lead(base);
        if (l == null || l.zero) {
          return null;
        }
        double k = numeric(exponent);
        IExpr raised = eval(F.Power(l.expr, exponent));
        IExpr expanded = eval(F.PowerExpand(raised));
        return new Term(expanded, l.growth.scale(k));
      }
      IExpr[] parts = linearParts(exponent);
      if (parts == null) {
        return null;
      }
      double c = numeric(parts[0]);
      if (base.isFree(n)) {
        // b^(c*n + d): exponential growth Log(|b|)*c
        double logAbs = numeric(F.Log(F.Abs(base)));
        IExpr e = eval(F.Power(base, exponent));
        return new Term(e, new Growth(0.0, c * logAbs, 0.0, 0.0));
      }
      if (isVar(base)) {
        // n^(c*n + d)
        double d = numeric(parts[1]);
        return new Term(eval(F.Power(base, exponent)), new Growth(c, 0.0, d, 0.0));
      }
      return null;
    }

    Term leadLog(IExpr x) {
      if (isVar(x)) {
        return new Term(F.Log(x), new Growth(0.0, 0.0, 0.0, 1.0));
      }
      Term l = lead(x);
      if (l == null || l.zero) {
        return null;
      }
      if (l.growth.isConstant()) {
        // the Log of a sequence tending to a constant c is Log(c), unless c == 1
        if (!l.expr.isFree(n) || l.expr.isOne()) {
          return null;
        }
        return new Term(eval(F.Log(l.expr)), Growth.ONE);
      }
      if (!Boolean.TRUE.equals(l.growth.tendsToInfinity())) {
        return null;
      }
      // the Log of the leading term, expanded into a sum of simpler pieces
      IExpr expanded = eval(F.PowerExpand(eval(F.Log(l.expr))));
      if (expanded.isAST(S.Log, 2) && !isVar(expanded.first())) {
        return null;
      }
      return lead(expanded);
    }

    /**
     * Stirling's formula <code>Gamma(c1*n + c0) ~ Sqrt(2*Pi)*(c1*n)^(c1*n + c0 - 1/2)*E^(-c1*n)</code>,
     * times the correction series to <code>goal</code> terms if <code>c1 == 1</code>. For
     * <code>c1 == -1</code> the reflection formula reduces it to the former.
     */
    Term leadGamma(IExpr x, int goal) {
      IExpr[] parts = linearParts(x);
      if (parts == null) {
        return null;
      }
      IExpr c1 = parts[0];
      IExpr c0 = parts[1];
      double c1f = numeric(c1);
      if (Double.isNaN(c1f)) {
        return null;
      }
      double c0f = numeric(c0);
      if (c1f > 0.0) {
        IExpr c1n = F.Times(c1, n);
        IExpr e = F.Times(F.Sqrt(F.Times(F.C2, S.Pi)),
            F.Power(c1n, F.Plus(c1n, c0, F.CN1D2)), F.Exp(F.Negate(c1n)));
        if (goal > 1) {
          if (!c1.isOne()) {
            return null;
          }
          IExpr correction = stirlingCorrection(c0, goal);
          if (correction == null) {
            return null;
          }
          e = F.Times(e, correction);
        }
        e = eval(F.PowerExpand(eval(e)));
        return new Term(e, new Growth(c1f, c1f * Math.log(c1f) - c1f, c0f - 0.5, 0.0));
      }
      if (c1f < 0.0 && goal == 1) {
        // Gamma(x) == Pi/(Sin(Pi*x)*Gamma(1 - x))
        Term reflected = leadGamma(eval(F.Subtract(F.C1, x)), 1);
        if (reflected == null) {
          return null;
        }
        IExpr rest = eval(F.Times(reflected.expr, F.Power(F.Times(F.C2, S.Pi), F.CN1D2)));
        IExpr e = F.Times(F.Sqrt(F.Times(F.C1D2, S.Pi)),
            F.Power(F.Times(F.Sin(F.Times(S.Pi, x)), rest), F.CN1));
        return new Term(eval(e), Growth.UNKNOWN);
      }
      return null;
    }

    /**
     * The Stirling correction factor of <code>Gamma(n + c)</code> to <code>goal</code> terms:
     * <code>Exp(Sum((-1)^(k+1)*BernoulliB(k+1, c)/(k*(k+1)*n^k), {k, 1, goal-1}))</code> in powers
     * of <code>1/n</code>.
     */
    IExpr stirlingCorrection(IExpr c, int goal) {
      ISymbol t = F.Dummy("t");
      int order = goal - 1;
      IASTAppendable sum = F.PlusAlloc(order);
      for (int k = 1; k <= order; k++) {
        sum.append(F.Times(F.Power(F.CN1, F.ZZ(k + 1)), F.BernoulliB(F.ZZ(k + 1), c),
            F.Power(t, F.ZZ(k)), F.QQ(1, k * (k + 1))));
      }
      IExpr series = eval(F.Series(F.Exp(sum), F.list(t, F.C0, F.ZZ(order))));
      if (!(series instanceof ASTSeriesData)) {
        return null;
      }
      IExpr normal = eval(((ASTSeriesData) series).normal(false));
      return eval(F.subst(normal, t, F.Power(n, F.CN1)));
    }

    Term leadBinomial(IExpr x, IExpr k) {
      if (k.isFree(n)) {
        // Binomial(x, k) ~ x^k/k! for a fixed k
        Term l = lead(x);
        if (l == null || l.zero || !Boolean.TRUE.equals(l.growth.tendsToInfinity())) {
          return null;
        }
        double kf = numeric(k);
        IExpr e = eval(F.Times(eval(F.PowerExpand(F.Power(l.expr, k))),
            F.Power(F.Factorial(k), F.CN1)));
        return new Term(e, l.growth.scale(kf));
      }
      // Binomial(a*n, b*n) ~ (a^a/(b^b*(a-b)^(a-b)))^n*Sqrt(a/(2*b*(a-b)))/Sqrt(Pi*n)
      IExpr[] xParts = linearParts(x);
      IExpr[] kParts = linearParts(k);
      if (xParts == null || kParts == null || !xParts[1].isZero() || !kParts[1].isZero()) {
        return null;
      }
      IExpr a = xParts[0];
      IExpr b = kParts[0];
      double af = numeric(a);
      double bf = numeric(b);
      if (!(af > bf && bf > 0.0)) {
        return null;
      }
      IExpr amb = F.Subtract(a, b);
      IExpr base = eval(F.Times(F.Power(a, a), F.Power(b, F.Negate(b)), F.Power(amb, F.Negate(amb))));
      IExpr coefficient = eval(F.Sqrt(F.Times(a, F.Power(F.Times(F.C2, b, amb), F.CN1))));
      IExpr e = eval(F.Times(F.Power(base, n), coefficient, F.Power(n, F.CN1D2),
          F.Power(S.Pi, F.CN1D2)));
      double baseValue = numeric(base);
      if (Double.isNaN(baseValue)) {
        return null;
      }
      return new Term(e, new Growth(0.0, Math.log(baseValue), -0.5, 0.0));
    }

    /**
     * Whether <code>e</code> is entire in <code>z</code>: built from constants and <code>z</code> by
     * sums, products, nonnegative integer powers and exponentials.
     */
    boolean isEntire(IExpr e, IExpr z) {
      if (e.isFree(z) || e.equals(z)) {
        return true;
      }
      if (e.isPlus() || e.isTimes()) {
        return ((IAST) e).forAll(x -> isEntire(x, z));
      }
      if (e.isPower()) {
        IExpr exponent = e.exponent();
        if (exponent.isInteger() && !exponent.isNegative()) {
          return isEntire(e.base(), z);
        }
        if (e.base().isFree(z)) {
          return isEntire(exponent, z);
        }
        return false;
      }
      if (e.isAST(S.Sin, 2) || e.isAST(S.Cos, 2) || e.isAST(S.Sinh, 2) || e.isAST(S.Cosh, 2)) {
        return isEntire(e.first(), z);
      }
      return false;
    }

    /**
     * <code>SeriesCoefficient(g/h, {z, 0, n})</code> for entire <code>g</code>, <code>h</code>:
     * the nearest zero <code>z0</code> of <code>h</code>, if it is unique and a simple pole,
     * contributes <code>-g(z0)/(h'(z0)*z0)*(1/z0)^n</code>.
     */
    Term leadSeriesCoefficient(IExpr f, IExpr spec) {
      if (!spec.isList3() || !spec.first().isSymbol() || !spec.second().isZero()
          || !isVar(((IAST) spec).arg3()) || !f.isFree(n)) {
        return null;
      }
      IExpr z = spec.first();
      IExpr together = eval(F.Together(f));
      IExpr g = eval(F.Numerator(together));
      IExpr h = eval(F.Denominator(together));
      if (h.isFree(z) || !isEntire(g, z) || !isEntire(h, z)) {
        return null;
      }
      IExpr solutions = eval(F.Solve(F.Equal(h, F.C0), z));
      if (!solutions.isListOfLists()) {
        return null;
      }
      // the candidate roots with their moduli; a ConditionalExpression family is sampled at its
      // principal member and its neighbours
      List<IExpr> roots = new ArrayList<>();
      List<Double> moduli = new ArrayList<>();
      for (IExpr solution : (IAST) solutions) {
        if (solution.argSize() != 1 || !solution.first().isRule()) {
          return null;
        }
        IExpr r = solution.first().second();
        List<IExpr> members = new ArrayList<>();
        if (r.isAST(S.ConditionalExpression, 3)) {
          for (int k = -1; k <= 1; k++) {
            members.add(F.subst(r.first(), F.C(1), F.ZZ(k)));
          }
        } else {
          members.add(r);
        }
        for (IExpr m : members) {
          IExpr member = eval(m);
          double modulus = numeric(F.Abs(member));
          if (Double.isNaN(modulus)) {
            return null;
          }
          int i = 0;
          while (i < moduli.size() && moduli.get(i) <= modulus) {
            i++;
          }
          roots.add(i, member);
          moduli.add(i, modulus);
        }
      }
      if (roots.isEmpty()) {
        return null;
      }
      IExpr root = roots.get(0);
      double modulus = moduli.get(0);
      if (modulus < 1e-12 || (moduli.size() > 1 && moduli.get(1) - modulus < EPS)) {
        return null;
      }
      IExpr dh = eval(F.D(h, z));
      IExpr g0 = eval(F.subst(g, z, root));
      IExpr dh0 = eval(F.subst(dh, z, root));
      double g0Abs = numeric(F.Abs(g0));
      double dh0Abs = numeric(F.Abs(dh0));
      if (!(g0Abs >= 1e-12) || !(dh0Abs >= 1e-12)) {
        return null;
      }
      IExpr ratio = eval(F.Cancel(eval(F.Times(F.CN1, g, F.Power(dh, F.CN1)))));
      IExpr coefficient = eval(F.Expand(eval(F.Simplify(eval(F.subst(ratio, z, root))))));
      IExpr power = eval(F.Power(root, F.Subtract(F.CN1, n)));
      IExpr e = eval(F.Expand(eval(F.Times(coefficient, power))));
      return new Term(e, new Growth(0.0, -Math.log(modulus), 0.0, 0.0));
    }

    /**
     * Combine several exponential factors <code>r^(c*n + d)</code> with positive rational bases
     * <code>r</code> into one <code>R^n</code> times a constant: <code>4^n*2^(-n)</code> is
     * <code>2^n</code>.
     */
    IExpr mergeRationalExponentials(IExpr e) {
      if (!e.isTimes()) {
        return e;
      }
      IASTAppendable bases = F.TimesAlloc(4);
      IASTAppendable result = F.TimesAlloc(((IAST) e).argSize() + 2);
      List<IExpr> constants = new ArrayList<>();
      for (IExpr f : (IAST) e) {
        if (f.isPower() && f.base().isRational() && f.base().isPositive()
            && !f.exponent().isFree(n)) {
          IExpr[] parts = linearParts(f.exponent());
          if (parts != null) {
            bases.append(F.Power(f.base(), parts[0]));
            constants.add(F.Power(f.base(), parts[1]));
            continue;
          }
        }
        result.append(f);
      }
      if (bases.argSize() < 2) {
        return e;
      }
      result.append(F.Power(eval(bases), n));
      for (IExpr c : constants) {
        result.append(c);
      }
      return eval(result);
    }

    /**
     * <code>Log(c*f)</code> or <code>E^(c*f)</code> with <code>f</code> a factorial or Gamma
     * function of the index: the function of the expanded Stirling form. <code>Gamma(n)</code> is
     * expanded with <code>goal</code> terms, <code>n!</code> and <code>Gamma(n + 1)</code> as
     * <code>n*Gamma(n)</code> with one term more.
     *
     * @return <code>null</code> for other shapes
     */
    IExpr logOfGammaType(IExpr expr, int goal) {
      // a power of such a logarithm is that power of its form
      if (expr.isPower() && expr.exponent().isFree(n) && !expr.base().isE()) {
        IExpr inner = logOfGammaType(expr.base(), goal);
        if (inner != null) {
          return eval(F.Power(inner, expr.exponent()));
        }
      }
      IExpr head;
      IExpr arg;
      if (expr.isPower() && expr.base().isE()) {
        head = S.Exp;
        arg = expr.exponent();
      } else if (expr.isAST(S.Log, 2)) {
        head = S.Log;
        arg = expr.first();
      } else {
        return null;
      }
      IASTAppendable constant = F.TimesAlloc(4);
      IExpr growing = null;
      for (IExpr f : arg.isTimes() ? (IAST) arg : F.list(arg)) {
        // an exponential b^n with a constant base rides along
        if (f.isFree(n) || (f.isPower() && f.base().isFree(n))) {
          constant.append(f);
        } else if (growing == null) {
          growing = f;
        } else {
          return null;
        }
      }
      if (growing == null) {
        return null;
      }
      int stirlingGoal;
      boolean timesN;
      if (growing.isAST(S.Gamma, 2) && isVar(growing.first())) {
        stirlingGoal = goal;
        timesN = false;
      } else if ((growing.isAST(S.Gamma, 2) && isVar(eval(F.Plus(growing.first(), F.CN1))))
          || (growing.isAST(S.Factorial, 2) && isVar(growing.first()))) {
        stirlingGoal = goal + 1;
        timesN = true;
      } else {
        return null;
      }
      Term l = leadGamma(n, stirlingGoal);
      if (l == null) {
        return null;
      }
      constant.append(l.expr);
      if (timesN) {
        constant.append(n);
      }
      IExpr inner = eval(F.Expand(eval(constant)));
      return head == S.Exp ? eval(F.Exp(inner)) : eval(F.Log(inner));
    }

    /** The leading term of <code>expr</code>, with <code>goal</code> terms for Gamma type sequences. */
    IExpr discreteAsymptotic(IExpr expr, int goal) {
      IExpr logForm = logOfGammaType(expr, goal);
      if (logForm != null) {
        return logForm;
      }
      IExpr result;
      if (goal > 1) {
        // c*Gamma(n + c0) type products: the correction series multiplies the single growing factor
        IASTAppendable constant = F.TimesAlloc(4);
        IExpr growing = null;
        for (IExpr f : expr.isTimes() ? (IAST) expr : F.list(expr)) {
          if (f.isFree(n)) {
            constant.append(f);
          } else if (growing == null) {
            growing = f;
          } else {
            return null;
          }
        }
        if (growing == null) {
          return null;
        }
        IExpr gammaArg;
        if (growing.isAST(S.Gamma, 2)) {
          gammaArg = growing.first();
        } else if (growing.isAST(S.Factorial, 2)) {
          gammaArg = F.Plus(growing.first(), F.C1);
        } else if (growing.isAST(S.Pochhammer, 3) && growing.first().isFree(n)) {
          constant.append(F.Power(F.Gamma(growing.first()), F.CN1));
          gammaArg = F.Plus(growing.first(), growing.second());
        } else {
          return null;
        }
        Term l = leadGamma(eval(gammaArg), goal);
        if (l == null) {
          return null;
        }
        constant.append(l.expr);
        result = eval(constant);
      } else {
        // an undefined function of the index is its own asymptotic form
        if (expr.isAST() && expr.head().isSymbol() && !expr.head().isBuiltInSymbol()
            && !((IAST) expr).forAll(a -> a.isFree(n))
            && !((ISymbol) expr.head()).hasAssignedSymbolValue()
            && ((ISymbol) expr.head()).getRulesData() == null) {
          return expr;
        }
        Term l = lead(expr);
        if (l == null) {
          return null;
        }
        if (l.zero) {
          result = F.C0;
        } else {
          // a sum dominated by one of its terms is that term as written
          if (expr.isPlus()) {
            IExpr dominant = null;
            int count = 0;
            for (IExpr t : (IAST) expr) {
              Term lt = lead(t);
              if (lt != null && !lt.zero && lt.expr.equals(l.expr)) {
                dominant = t;
                count++;
              }
            }
            if (count == 1) {
              return dominant;
            }
          }
          result = l.expr;
        }
      }
      return mergeRationalExponentials(result);
    }
  }

  public DiscreteAsymptotic() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr spec = ast.arg2();
    IExpr n;
    int goal = 1;
    if (spec.isRule() && spec.second().isInfinity()) {
      n = spec.first();
    } else if (spec.isList3() && spec.second().isInfinity()) {
      n = spec.first();
      goal = ((IAST) spec).arg3().toIntDefault();
    } else {
      return F.NIL;
    }
    if (!n.isSymbol() || ((ISymbol) n).hasProtectedAttribute()) {
      return F.NIL;
    }
    if (options[0].isPresent() && !options[0].equals(S.Automatic)) {
      goal = options[0].toIntDefault();
    }
    if (goal < 1 || goal > MAX_GOAL) {
      return F.NIL;
    }
    IExpr result = new Search((ISymbol) n, engine).discreteAsymptotic(ast.arg1(), goal);
    return result == null ? F.NIL : result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol, new IBuiltInSymbol[] {S.SeriesTermGoal}, new IExpr[] {S.Automatic});
  }
}
