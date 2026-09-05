package org.matheclipse.core.series;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Computes the leading term of an expression at <code>t -&gt; 0+</code>.
 *
 * <p>
 * This is the Symja counterpart of SymPy's <code>_eval_as_leading_term</code> protocol
 * (<code>sympy/core/expr.py</code> and the per-class overrides in <code>add.py</code>,
 * <code>mul.py</code>, <code>power.py</code>, <code>functions/elementary/*</code>). SymPy hangs one
 * method on each expression class; Symja has no such place on {@code IAST}, so the same case
 * analysis lives here as one static dispatch on the head.
 *
 * <p>
 * The contract that makes this useful as a limit primitive: the recursion is <em>closed</em>. It
 * never calls back into the {@code Limit} evaluator, so it cannot participate in the
 * Limit-&gt;Series-&gt;Limit cycle that forces the Gruntz series step to run at a fixed low order.
 * The only escape hatch is {@link #nseriesLead}, which is bounded and re-entrancy guarded.
 *
 * <p>
 * Every method returns {@code null} rather than guessing. A {@code null} means "this routine cannot
 * establish a leading term", never "there is none".
 */
public class LeadTerm {

  /** Returned by {@link #compareExponents} when no ordering can be established. */
  public static final int UNDECIDABLE = Integer.MIN_VALUE;

  /** Structural recursion cap. Reached only by pathological nesting. */
  private static final int MAX_DEPTH = 64;

  /**
   * Nested {@link #nseriesLead} calls are refused beyond this depth. {@code ASTSeriesData}'s Taylor
   * path computes coefficients with engine-level {@code Limit} calls, which can re-enter this class;
   * one level of that is affordable, a tower of it is not.
   */
  private static final int MAX_NSERIES_DEPTH = 2;

  private static final ThreadLocal<Integer> NSERIES_DEPTH = ThreadLocal.withInitial(() -> 0);

  /**
   * Whether the series fallback may run. Callers that already sit in front of a full heuristic
   * cascade (the {@code Limit} fast path) switch it off: for them a structural answer is a cheap
   * win and anything deeper is better left to the machinery behind them, which would otherwise pay
   * for the expansion twice.
   */
  private static final ThreadLocal<Boolean> ALLOW_SERIES = ThreadLocal.withInitial(() -> true);

  private LeadTerm() {}

  // ---------------------------------------------------------------- public API

  /** Leading term of {@code f} at {@code t -> 0+}, with a freshly minted {@code logx} dummy. */
  public static Lead leadTerm(IExpr f, ISymbol t, EvalEngine engine) {
    return leadTerm(f, t, F.Dummy("logx"), engine);
  }

  /**
   * Leading term of {@code f} at {@code t -> 0+}.
   *
   * @param logx a symbol standing in for {@code Log(t)}, so that logarithmic factors can live in the
   *        coefficient without violating its t-freeness
   * @return the leading term, or {@code null} when it cannot be established
   */
  public static Lead leadTerm(IExpr f, ISymbol t, IExpr logx, EvalEngine engine) {
    if (f == null || !f.isPresent()) {
      return null;
    }
    return dispatch(f, t, logx, engine, 0);
  }

  /**
   * Leading term of {@code f} at {@code t -> 0+} using the structural rules only.
   *
   * <p>
   * The series fallback is disabled, so this is cheap and always terminates quickly. Use it where a
   * fuller (and slower) engine already stands behind the call.
   */
  public static Lead structuralLeadTerm(IExpr f, ISymbol t, EvalEngine engine) {
    boolean allowed = ALLOW_SERIES.get();
    ALLOW_SERIES.set(Boolean.FALSE);
    try {
      return leadTerm(f, t, engine);
    } finally {
      ALLOW_SERIES.set(allowed);
    }
  }

  /** The leading term rebuilt as an expression, or {@link F#NIL}. */
  public static IExpr asLeadingTerm(IExpr f, ISymbol t, EvalEngine engine) {
    Lead lead = leadTerm(f, t, engine);
    return lead == null ? F.NIL : lead.toExpr(t, engine);
  }

  /**
   * A limit problem rewritten so that the approach is always <code>t -&gt; 0+</code>.
   *
   * <p>
   * Normalizing first means the leading-term rules never need a direction flag, and it replaces the
   * several hand-rolled reciprocal substitutions that had each derived the direction mapping
   * independently (and, per their own comments, had each got it wrong at some point).
   */
  public static final class Normalized {
    private final IExpr expr;
    private final ISymbol t;
    private final IExpr tInTermsOfX;

    Normalized(IExpr expr, ISymbol t, IExpr tInTermsOfX) {
      this.expr = expr;
      this.t = t;
      this.tInTermsOfX = tInTermsOfX;
    }

    /** The original expression rewritten in terms of {@link #t()}. */
    public IExpr expr() {
      return expr;
    }

    /** The normalized variable, approaching zero from above. */
    public ISymbol t() {
      return t;
    }

    /**
     * Map a result expressed in {@link #t()} back to the original variable, by substituting the
     * inverse of the normalizing substitution.
     */
    public IExpr backSubstitute(IExpr inT, EvalEngine engine) {
      return engine.evaluate(F.subst(inT, t, tInTermsOfX));
    }
  }

  /**
   * Rewrite <code>f(x)</code> as <code>x -&gt; x0</code> into an equivalent problem at
   * <code>t -&gt; 0+</code>.
   *
   * <table>
   * <tr><th>limit point</th><th>direction</th><th>substitution</th></tr>
   * <tr><td>finite</td><td>from above</td><td><code>x -&gt; x0 + t</code></td></tr>
   * <tr><td>finite</td><td>from below</td><td><code>x -&gt; x0 - t</code></td></tr>
   * <tr><td>+Infinity</td><td>-</td><td><code>x -&gt; 1/t</code></td></tr>
   * <tr><td>-Infinity</td><td>-</td><td><code>x -&gt; -1/t</code></td></tr>
   * </table>
   *
   * <p>
   * Because the approach direction is folded into the substitution, no parity correction is needed
   * when reading the answer back: approaching from below puts the sign into the coefficient itself.
   *
   * @param fromBelow whether the approach is from smaller values; ignored at an infinite point
   * @return the normalized problem, or {@code null} for a limit point this cannot express
   *         (complex infinity, a directed infinity other than the real axis, or a point that
   *         itself depends on {@code x})
   */
  public static Normalized normalize(IExpr f, ISymbol x, IExpr x0, boolean fromBelow) {
    if (!x0.isFree(x)) {
      return null;
    }
    ISymbol t = F.Dummy("t");
    IExpr substituted;
    IExpr inverse;
    if (x0.isInfinity()) {
      substituted = F.subst(f, x, F.Power(t, F.CN1));
      inverse = F.Power(x, F.CN1);
    } else if (x0.isNegativeInfinity()) {
      substituted = F.subst(f, x, F.Negate(F.Power(t, F.CN1)));
      inverse = F.Negate(F.Power(x, F.CN1));
    } else if (x0.isDirectedInfinity() || x0.isComplexInfinity() || x0.isIndeterminate()
        || !x0.isNumericFunction(true)) {
      return null;
    } else if (fromBelow) {
      substituted = F.subst(f, x, F.Subtract(x0, t));
      inverse = F.Subtract(x0, x);
    } else {
      substituted = F.subst(f, x, F.Plus(x0, t));
      inverse = F.Subtract(x, x0);
    }
    return substituted.isPresent() ? new Normalized(substituted, t, inverse) : null;
  }

  /**
   * Read a limit off a leading term: <code>c*t^e</code> as <code>t -&gt; 0+</code> is
   * <code>0</code> for <code>e &gt; 0</code>, <code>c</code> for <code>e == 0</code>, and
   * <code>sign(c)*Infinity</code> for <code>e &lt; 0</code>.
   *
   * @return the limit, or {@link F#NIL} when it cannot be read off
   */
  public static IExpr limitFromLead(Lead lead, EvalEngine engine) {
    if (lead == null || lead.coefficientHasLogx()) {
      // a surviving Log(t) means the coefficient is not a constant at all
      return F.NIL;
    }
    int sign = lead.exponentSign(engine);
    if (sign == UNDECIDABLE) {
      return F.NIL;
    }
    if (sign > 0) {
      return F.C0;
    }
    IExpr coefficient = lead.coefficient();
    if (!coefficient.isPresent() || coefficient.isIndeterminate() || !coefficient.isSpecialsFree()) {
      return F.NIL;
    }
    if (sign == 0) {
      return coefficient;
    }
    // e < 0: the magnitude diverges, and the coefficient's sign says in which direction
    if (!coefficient.isRealResult()) {
      return F.NIL;
    }
    if (engine.evalTrue(F.Greater(coefficient, F.C0))) {
      return F.CInfinity;
    }
    if (engine.evalTrue(F.Less(coefficient, F.C0))) {
      return F.CNInfinity;
    }
    return F.NIL;
  }

  // ------------------------------------------------------------- the dispatch

  private static Lead dispatch(IExpr f, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    if (depth > MAX_DEPTH) {
      return null;
    }
    if (f.isFree(t)) {
      // a t-free factor is its own leading term; a t-free ZERO has none
      return f.isZero() ? null : new Lead(f, F.C0, logx);
    }
    if (f.equals(t)) {
      return new Lead(F.C1, F.C1, logx);
    }
    if (!f.isAST()) {
      return null;
    }
    IAST ast = (IAST) f;
    if (f.isPlus()) {
      return plusLead(ast, t, logx, engine, depth);
    }
    if (f.isTimes()) {
      return timesLead(ast, t, logx, engine, depth);
    }
    if (f.isPower()) {
      return powerLead(ast, t, logx, engine, depth);
    }
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      // an unknown head could be anything; refusing here matches the Gruntz mrv gate
      return null;
    }
    return functionLead(ast, (IBuiltInSymbol) head, t, logx, engine, depth);
  }

  // --------------------------------------------------------------------- Plus

  /**
   * SymPy <code>Add._eval_as_leading_term</code>: take the leading term of every summand, keep the
   * ones of minimal exponent and add them. If those cancel, the true leading term is deeper than
   * this analysis can see and only a series expansion will find it.
   */
  private static Lead plusLead(IAST plus, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    IExpr expanded = plus;
    if (plus.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
      IExpr e = engine.evalQuiet(F.Expand(plus));
      if (e.isPresent() && e.isPlus()) {
        expanded = e;
      } else if (e.isPresent() && !e.equals(plus)) {
        return dispatch(e, t, logx, engine, depth + 1);
      }
    }
    IAST terms = (IAST) expanded;

    IExpr minExponent = null;
    IASTAppendable tied = F.PlusAlloc(terms.argSize());
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      if (term.isZero()) {
        continue;
      }
      Lead lead = dispatch(term, t, logx, engine, depth + 1);
      if (lead == null) {
        return null;
      }
      if (minExponent == null) {
        minExponent = lead.exponent();
        tied.append(lead.coefficient());
        continue;
      }
      int cmp = compareExponents(lead.exponent(), minExponent, engine);
      if (cmp == UNDECIDABLE) {
        return null;
      }
      if (cmp < 0) {
        minExponent = lead.exponent();
        tied = F.PlusAlloc(terms.argSize());
        tied.append(lead.coefficient());
      } else if (cmp == 0) {
        tied.append(lead.coefficient());
      }
      // cmp > 0: a strictly higher power, irrelevant to the leading term
    }
    if (minExponent == null) {
      return null;
    }

    IExpr coefficient = engine.evalQuiet(tied.oneIdentity0());
    if (!coefficient.isPresent()) {
      return null;
    }
    if (isProvablyZero(coefficient, engine)) {
      // The minimal-order parts cancelled exactly. The surviving leading term sits at a higher
      // order that only a series expansion reveals (SymPy does the same, add.py 1072-1086).
      return nseriesLead(expanded, t, logx, exponentFloor(minExponent, engine) + 1, engine);
    }
    return new Lead(coefficient, minExponent, logx);
  }

  // -------------------------------------------------------------------- Times

  private static Lead timesLead(IAST times, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    IASTAppendable coefficient = F.TimesAlloc(times.argSize());
    IASTAppendable exponent = F.PlusAlloc(times.argSize());
    for (int i = 1; i < times.size(); i++) {
      Lead lead = dispatch(times.get(i), t, logx, engine, depth + 1);
      if (lead == null) {
        return null;
      }
      coefficient.append(lead.coefficient());
      exponent.append(lead.exponent());
    }
    IExpr c = engine.evalQuiet(coefficient.oneIdentity1());
    IExpr e = engine.evalQuiet(exponent.oneIdentity0());
    if (!c.isPresent() || !e.isPresent() || c.isZero()) {
      return null;
    }
    return new Lead(c, e, logx);
  }

  // -------------------------------------------------------------------- Power

  private static Lead powerLead(IAST power, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    IExpr base = power.base();
    IExpr exp = power.exponent();

    if (base.equals(S.E)) {
      return expLead(exp, t, logx, engine, depth);
    }
    if (!exp.isFree(t)) {
      // b^g with a t-dependent exponent is Exp(g*Log(b)) - SymPy power.py 1664-1666
      return expLead(F.Times(exp, F.Log(base)), t, logx, engine, depth);
    }
    Lead baseLead = dispatch(base, t, logx, engine, depth + 1);
    if (baseLead == null) {
      return null;
    }
    if (baseLead.coefficientHasLogx()) {
      // (c(logx) * t^v)^e would need a log-power lattice to represent
      return null;
    }
    IExpr c = engine.evalQuiet(F.Power(baseLead.coefficient(), exp));
    IExpr e = engine.evalQuiet(F.Times(baseLead.exponent(), exp));
    if (!c.isPresent() || !e.isPresent() || c.isZero() || !e.isFree(t)) {
      return null;
    }
    return new Lead(c, e, logx);
  }

  /**
   * Leading term of <code>Exp(g)</code>.
   *
   * <p>
   * The interesting case is <code>g -&gt; k*Log(t)</code>: then <code>Exp(g)</code> is the honest
   * power <code>t^k</code> and must come back as one, or the Gruntz caller would see an opaque
   * exponential where a w-power was expected. A genuinely divergent <code>g</code> is an essential
   * singularity and is refused - that is the mrv algorithm's job, not the leading term's.
   */
  private static Lead expLead(IExpr g, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    Lead inner = dispatch(g, t, logx, engine, depth + 1);
    if (inner == null) {
      return null;
    }
    int sign = inner.exponentSign(engine);
    if (sign == UNDECIDABLE) {
      return null;
    }
    if (sign > 0) {
      return new Lead(F.C1, F.C0, logx); // g -> 0, so Exp(g) -> 1
    }
    if (sign < 0) {
      return null; // g diverges: essential singularity
    }
    IExpr c = inner.coefficient();
    if (!inner.coefficientHasLogx()) {
      IExpr value = engine.evalQuiet(F.Exp(c));
      if (!value.isPresent() || value.isZero() || !value.isFree(t)) {
        return null;
      }
      return new Lead(value, F.C0, logx);
    }
    // c = a + k*logx with k free of logx  =>  Exp(c) = Exp(a) * t^k
    IExpr k = engine.evalQuiet(F.Coefficient(c, logx, F.C1));
    if (!k.isPresent() || !k.isFree(logx) || !k.isFree(t)) {
      return null;
    }
    IExpr a = engine.evalQuiet(F.Subtract(c, F.Times(k, logx)));
    if (!a.isPresent() || !a.isFree(logx)) {
      return null;
    }
    IExpr value = engine.evalQuiet(F.Exp(a));
    if (!value.isPresent() || value.isZero()) {
      return null;
    }
    return new Lead(value, k, logx);
  }

  // ---------------------------------------------------------------- functions

  private static Lead functionLead(IAST ast, IBuiltInSymbol head, ISymbol t, IExpr logx,
      EvalEngine engine, int depth) {
    if (ast.argSize() == 1 && head.ordinal() == ID.Exp) {
      return expLead(ast.arg1(), t, logx, engine, depth);
    }
    if (ast.argSize() == 1 && head.ordinal() == ID.Log) {
      return logLead(ast.arg1(), t, logx, engine, depth);
    }
    if (ast.argSize() != 1) {
      return multiArgFunctionLead(ast, head, t, logx, engine, depth);
    }

    IExpr arg = ast.arg1();
    Lead argLead = dispatch(arg, t, logx, engine, depth + 1);
    if (argLead == null) {
      return null;
    }
    int sign = argLead.exponentSign(engine);
    if (sign == UNDECIDABLE) {
      return null;
    }

    switch (head.ordinal()) {
      case ID.Sin:
      case ID.Tan:
      case ID.Sinh:
      case ID.Tanh:
      case ID.ArcSin:
      case ID.ArcTan:
      case ID.ArcSinh:
      case ID.ArcTanh:
        // f(u) ~ u for u -> 0
        if (sign > 0) {
          return argLead;
        }
        break;
      case ID.Cos:
      case ID.Cosh:
      case ID.Sec:
      case ID.Sech:
      case ID.Erfc:
        if (sign > 0) {
          return new Lead(F.C1, F.C0, logx);
        }
        break;
      case ID.Cot:
      case ID.Coth:
      case ID.Csc:
      case ID.Csch:
        // f(u) ~ 1/u for u -> 0
        if (sign > 0) {
          return reciprocal(argLead, engine);
        }
        break;
      case ID.Erf:
      case ID.Erfi:
        if (sign > 0) {
          return scale(argLead, F.Times(F.C2, F.Power(S.Pi, F.CN1D2)), engine);
        }
        break;
      case ID.InverseErf:
        if (sign > 0) {
          return scale(argLead, F.Times(F.C1D2, F.Sqrt(S.Pi)), engine);
        }
        break;
      case ID.Gamma:
        return gammaLead(argLead, arg, sign, t, logx, engine, depth);
      case ID.Abs:
        if (argLead.coefficient().isRealResult()) {
          return new Lead(engine.evaluate(F.Abs(argLead.coefficient())), argLead.exponent(), logx);
        }
        return null;
      case ID.Sign:
        if (argLead.coefficient().isRealResult()) {
          IExpr s = engine.evaluate(F.Sign(argLead.coefficient()));
          return s.isZero() ? null : new Lead(s, F.C0, logx);
        }
        return null;
      default:
        break;
    }
    // The continuous case: the argument tends to a finite value and f is regular there.
    return continuousLead(ast, argLead, sign, t, logx, engine, depth);
  }

  /**
   * <code>Gamma</code> has a simple pole at every non-positive integer, so the leading term is
   * <code>(-1)^k/k! * 1/(u+k)</code> there. Ported from
   * {@code ASTSeriesData.leadingTerm}, which carried this case already.
   */
  private static Lead gammaLead(Lead argLead, IExpr arg, int sign, ISymbol t, IExpr logx,
      EvalEngine engine, int depth) {
    if (sign > 0) {
      // arg -> 0: Gamma(u) ~ 1/u
      return reciprocal(argLead, engine);
    }
    if (sign != 0 || argLead.coefficientHasLogx()) {
      return null;
    }
    IExpr limitValue = argLead.coefficient();
    if (limitValue.isInteger() && !limitValue.isPositive()) {
      IExpr k = limitValue.negate();
      Lead shifted = dispatch(engine.evaluate(F.Plus(arg, k)), t, logx, engine, depth + 1);
      if (shifted == null) {
        return null;
      }
      IExpr factor = engine.evaluate(F.Divide(F.Power(F.CN1, k), F.Factorial(k)));
      Lead inverse = reciprocal(shifted, engine);
      return inverse == null ? null : scale(inverse, factor, engine);
    }
    return continuousLead(F.Gamma(arg), argLead, sign, t, logx, engine, depth);
  }

  private static Lead multiArgFunctionLead(IAST ast, IBuiltInSymbol head, ISymbol t, IExpr logx,
      EvalEngine engine, int depth) {
    if (head.ordinal() == ID.PolyGamma && ast.argSize() == 2) {
      IExpr n = ast.arg1();
      Lead zLead = dispatch(ast.arg2(), t, logx, engine, depth + 1);
      if (zLead == null || !n.isInteger() || n.isNegativeResult()) {
        return null;
      }
      if (zLead.exponentSign(engine) > 0) {
        // PolyGamma(n, z) ~ (-1)^(n+1) * n! / z^(n+1) for z -> 0
        IExpr factor = engine.evaluate(F.Times(F.Power(F.CN1, F.Plus(n, F.C1)), F.Factorial(n)));
        IExpr c = engine.evaluate(F.Times(factor, F.Power(zLead.coefficient(), F.Negate(F.Plus(n, F.C1)))));
        IExpr e = engine.evaluate(F.Times(zLead.exponent(), F.Negate(F.Plus(n, F.C1))));
        if (c.isPresent() && e.isPresent() && !c.isZero()) {
          return new Lead(c, e, logx);
        }
      }
      return null;
    }
    // Any other multi-argument function: only the fully continuous case is safe, and only when a
    // single argument depends on t.
    return null;
  }

  /**
   * <code>f(u)</code> where <code>u</code> tends to a finite limit and <code>f</code> is continuous
   * there: the leading term is the constant <code>f(u0)</code>. When <code>f(u0)</code> vanishes the
   * constant carries no information and a series expansion is needed instead.
   */

  /**
   * Whether the head has jump discontinuities, so that its value AT a point says nothing about the
   * values approached from either side.
   *
   * <p>
   * Every route that takes a limit by substituting the point - this class's continuity case, and
   * the direct-substitution and argument-limiting paths in the {@code Limit} evaluator - must
   * consult this, which is why the list lives in one place. Beyond the step functions the machinery
   * already knew about, the rest were found by probing each discontinuous builtin at a jump:
   * <code>Limit(PrimePi(x), x-&gt;2)</code> answered 1 rather than reporting that the one-sided
   * limits 0 and 1 disagree.
   */
  public static boolean isDiscontinuousHead(IExpr head) {
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Floor:
      case ID.Ceiling:
      case ID.Round:
      case ID.UnitStep:
      case ID.IntegerPart:
      case ID.FractionalPart:
      case ID.Mod:
      case ID.Quotient:
      case ID.Boole:
      case ID.SawtoothWave:
      case ID.SquareWave:
      case ID.UnitBox:
      case ID.HeavisidePi:
      case ID.HeavisideTheta:
      case ID.PrimePi:
      case ID.KroneckerDelta:
      case ID.DiscreteDelta:
      case ID.DiracDelta:
      case ID.Piecewise:
        return true;
      default:
        return false;
    }
  }

  private static Lead continuousLead(IAST ast, Lead argLead, int sign, ISymbol t, IExpr logx,
      EvalEngine engine, int depth) {
    if (sign < 0 || argLead.coefficientHasLogx()) {
      return null;
    }
    if (isDiscontinuousHead(ast.head())) {
      // These are piecewise constant: f(arg0) is the value AT the point, which is not the value
      // approached from either side. Reading Floor(x) at x -> 2 as Floor(2) would answer 2 for a
      // limit that does not exist, the one-sided values being 1 and 2.
      return null;
    }
    IExpr arg0 = sign > 0 ? F.C0 : argLead.coefficient();
    if (!arg0.isFree(t)) {
      return null;
    }
    IExpr value = engine.evalQuiet(ast.setAtCopy(1, arg0));
    if (!value.isPresent() || !value.isFree(t) || value.isIndeterminate()
        || value.isDirectedInfinity() || !value.isSpecialsFree()) {
      return null;
    }
    if (isProvablyZero(value, engine)) {
      return nseriesLead(ast, t, logx, 1, engine);
    }
    return new Lead(value, F.C0, logx);
  }

  // ------------------------------------------------------------------ helpers

  private static Lead reciprocal(Lead lead, EvalEngine engine) {
    IExpr c = engine.evalQuiet(F.Power(lead.coefficient(), F.CN1));
    IExpr e = engine.evalQuiet(F.Negate(lead.exponent()));
    if (!c.isPresent() || !e.isPresent() || c.isZero()) {
      return null;
    }
    return new Lead(c, e, lead.logx());
  }

  private static Lead scale(Lead lead, IExpr factor, EvalEngine engine) {
    IExpr c = engine.evalQuiet(F.Times(factor, lead.coefficient()));
    if (!c.isPresent() || c.isZero()) {
      return null;
    }
    return new Lead(c, lead.exponent(), lead.logx());
  }

  /**
   * Leading term of <code>Log(g)</code>, following SymPy's five steps
   * (<code>exponential.py</code> 1062-1104) specialised to <code>t -&gt; 0+</code>, where
   * <code>Log(cdir)</code> vanishes.
   */
  private static Lead logLead(IExpr g, ISymbol t, IExpr logx, EvalEngine engine, int depth) {
    IExpr arg = engine.evalQuiet(F.Together(g));
    if (!arg.isPresent()) {
      arg = g;
    }
    Lead inner = dispatch(arg, t, logx, engine, depth + 1);
    if (inner == null || inner.coefficientHasLogx()) {
      return null;
    }
    int sign = inner.exponentSign(engine);
    if (sign == UNDECIDABLE) {
      return null;
    }
    if (sign == 0 && inner.coefficient().isOne()) {
      // Log(1 + h) ~ h : the constant term cancels, so recurse on the vanishing part
      return dispatch(engine.evaluate(F.Subtract(arg, F.C1)), t, logx, engine, depth + 1);
    }
    IExpr c = engine.evalQuiet(F.Plus(F.Log(inner.coefficient()), F.Times(inner.exponent(), logx)));
    if (!c.isPresent() || c.isZero() || !c.isFree(t)) {
      return null;
    }
    return new Lead(c, F.C0, logx);
  }

  // ------------------------------------------------------------- nseries path

  /**
   * The single adaptive-order series fallback, used when the structural analysis cancels to zero.
   *
   * <p>
   * This consolidates the hand-rolled "expand until something non-zero appears" loops that had
   * grown independently in {@code SeriesFunctions.Series}, {@code Asymptotic.leadingTerm} and
   * {@code ASTSeriesData}. Order is raised by doubling; a {@code null} series means an unsupported
   * shape rather than an under-resolved one, so it stops immediately.
   *
   * @param n0 the order to start from
   * @return the leading term, or {@code null}
   */
  public static Lead nseriesLead(IExpr f, ISymbol t, IExpr logx, int n0, EvalEngine engine) {
    if (!ALLOW_SERIES.get()) {
      return null;
    }
    int depth = NSERIES_DEPTH.get();
    if (depth >= MAX_NSERIES_DEPTH) {
      return null;
    }
    NSERIES_DEPTH.set(depth + 1);
    try {
      int order = Math.max(1, n0);
      for (int i = 0; i < 5; i++, order *= 2) {
        ASTSeriesData series;
        try {
          series = ASTSeriesData.seriesDataRecursive(f, t, F.C0, order, -1, engine);
        } catch (RuntimeException rex) {
          return null;
        }
        if (series == null) {
          return null;
        }
        // Skip vanishing coefficients. The test has to be a real zero test, not just a syntactic
        // one: with a symbolic parameter the coefficient can cancel without looking like zero
        // (-1 + 1/(1+n) + n/(1+n) is zero for every n), and accepting it would report a leading
        // term whose coefficient is zero instead of expanding one order further.
        int index = series.minExponent();
        while (index < series.truncateOrder()
            && isProvablyZero(series.coefficient(index), engine)) {
          index++;
        }
        if (index < series.truncateOrder()) {
          IExpr coefficient = series.coefficient(index);
          if (!coefficient.isPresent()) {
            return null;
          }
          IExpr exponent = F.QQ(index, series.puiseuxDenominator()).normalize();
          return new Lead(coefficient, exponent, logx);
        }
        // the whole truncation cancelled - the leading term is deeper, expand further
      }
      return null;
    } finally {
      NSERIES_DEPTH.set(depth);
    }
  }

  // -------------------------------------------------------- exponent ordering

  /**
   * Order two t-free exponents.
   *
   * <p>
   * Exact reasoning first, numeric only as a tie-breaker, and an explicit {@link #UNDECIDABLE}
   * rather than a silent guess. The rankers this replaces compared exponents with a bare
   * <code>1e-9</code> epsilon and treated "too close to call" as equality.
   *
   * @return <code>-1</code>, <code>0</code>, <code>1</code>, or {@link #UNDECIDABLE}
   */
  public static int compareExponents(IExpr a, IExpr b, EvalEngine engine) {
    if (a.equals(b)) {
      return 0;
    }
    if (a instanceof IRational && b instanceof IRational) {
      return ((IRational) a).compareTo((IRational) b);
    }
    IExpr difference = engine.evalQuiet(F.Subtract(a, b));
    if (!difference.isPresent()) {
      return UNDECIDABLE;
    }
    if (difference.isZero()) {
      return 0;
    }
    if (difference instanceof IRational) {
      return ((IRational) difference).complexSign();
    }
    if (difference.isNumericFunction(true)) {
      try {
        double value = engine.evalDouble(difference);
        if (!Double.isNaN(value) && Math.abs(value) > 1.0e-9) {
          return value > 0 ? 1 : -1;
        }
      } catch (RuntimeException rex) {
        // fall through to the symbolic comparison
      }
      // too close to call numerically - only an exact zero test can decide it
      return engine.evalTrue(F.PossibleZeroQ(difference)) ? 0 : UNDECIDABLE;
    }
    // free parameters: whatever the assumptions can prove, nothing more
    if (engine.evalTrue(F.Greater(difference, F.C0))) {
      return 1;
    }
    if (engine.evalTrue(F.Less(difference, F.C0))) {
      return -1;
    }
    return UNDECIDABLE;
  }

  /** Largest integer not exceeding {@code exponent}, or 0 when it cannot be evaluated. */
  private static int exponentFloor(IExpr exponent, EvalEngine engine) {
    try {
      IExpr floor = engine.evalQuiet(F.Floor(exponent));
      if (floor.isInteger()) {
        return floor.toIntDefault(0);
      }
    } catch (RuntimeException rex) {
      // fall through
    }
    return 0;
  }

  /**
   * Whether the tied leading coefficients annihilate.
   *
   * <p>
   * This has to see through symbolic cancellation, not just syntactic zeros: the sum
   * <code>-1 + 1/(1+n) + n/(1+n)</code> is zero for every n, and missing it makes the caller report
   * a leading term whose coefficient is zero - which is not a leading term at all. Progressively
   * stronger tests, cheapest first.
   */
  private static boolean isProvablyZero(IExpr expr, EvalEngine engine) {
    if (expr.isZero()) {
      return true;
    }
    if (expr.isNumber()) {
      return false;
    }
    IExpr together = engine.evalQuiet(F.Together(expr));
    if (together.isPresent() && together.isZero()) {
      return true;
    }
    if (engine.evalTrue(F.PossibleZeroQ(expr))) {
      return true;
    }
    IExpr simplified = engine.evalQuiet(F.Simplify(expr));
    return simplified.isPresent() && simplified.isZero();
  }
}
