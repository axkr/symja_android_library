package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.S.Power;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

public class FunctionRange extends AbstractFunctionEvaluator {
  private static final class FunctionRangeRealsVisitor extends VisitorExpr {

    public FunctionRangeRealsVisitor() {
      super();
    }

    // @Override
    // public IExpr visit2(IExpr head, IExpr arg1) {
    // boolean evaled = false;
    // IExpr x = arg1;
    // IExpr result = arg1.accept(this);
    // if (result.isPresent()) {
    // evaled = true;
    // x = result;
    // }
    // if (evaled) {
    // return F.unaryAST1(head, x);
    // }
    // return F.NIL;
    // }

    /** {@inheritDoc} */
    @Override
    public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
      boolean evaled = false;
      IExpr x1 = arg1;
      IExpr result = arg1.accept(this);
      if (result.isPresent()) {
        evaled = true;
        x1 = result;
      }
      IExpr x2 = arg2;
      result = arg2.accept(this);
      if (result.isPresent()) {
        evaled = true;
        x2 = result;
      }
      if (head.equals(Power)) {
        if (x1.isInterval1()) {
          IAST interval = (IAST) x1;
          IExpr l = interval.lower();
          IExpr u = interval.upper();
          if (x2.isMinusOne()) {
            if (l.greaterEqual(F.C1).isTrue()) {
              // if (S.GreaterEqual.ofQ(engine, l, F.C1)) {
              // [>= 1, u]
              return F.Interval(F.Power(u, x2), F.Power(l, x2));
            }
          }
          if (l.isNegativeResult() && u.isPositiveResult()) {
            if (x2.isPositiveResult()) {
              return F.Interval(F.C0, F.Power(u, x2));
            }
            if (x2.isEvenResult() || (x2.isFraction() && ((IFraction) x2).denominator().isEven())) {
              return F.Interval(F.C0, F.Power(u, x2));
            }
          }
        }
      }
      if (evaled) {
        return F.binaryAST2(head, x1, x2);
      }
      return F.NIL;
    }
  }

  /**
   * Closed-form range for a known built-in function applied directly to the range variable, i.e.
   * matching {@code FunctionRange(head(x), x, y)}. Returns {@link F#NIL} when {@code function} is not
   * such a form or its head is not tabled here.
   */
  private static IExpr builtinRange(IExpr function, ISymbol x, ISymbol y) {
    if (!function.isAST1() || !function.first().equals(x)) {
      return F.NIL;
    }
    IExpr head = function.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.ExpIntegralEi:
      case ID.Log:
      case ID.LogIntegral:
      case ID.Re:
        // range is all real numbers
        return S.True;
      case ID.Im:
        // Im(x) == 0 for real x
        return F.Equal(y, F.C0);
      case ID.Cosh:
        // minimum Cosh(0) == 1 is attained -> 1 <= y
        return F.LessEqual(F.C1, y);
      case ID.Gamma:
        // Gamma is real-valued on its real domain and never 0 -> y<0 || y>0
        return F.Or(F.Less(y, F.C0), F.Greater(y, F.C0));
      default:
        return F.NIL;
    }
  }

  /**
   * Refine an approximate root of {@code expr == 0} using {@code FindRoot[expr == 0, {x, guess}]}.
   *
   * @return the refined root value, or {@code null} if FindRoot fails.
   */
  private static Double findRootRefine(IExpr expr, ISymbol x, double guess, EvalEngine engine) {
    try {
      IExpr eq = F.Equal(expr, F.C0);
      IExpr result = engine.evaluate(F.FindRoot(eq, F.list(x, F.num(guess))));
      if (result.isList() && result.size() == 2) {
        IExpr rule = result.first();
        if (rule.isRuleAST() && rule.second().isNumber()) {
          return rule.second().evalf();
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return null;
  }

  /** Evaluate {@code expr} with {@code x -> value} as a machine-precision double. */
  private static double sampleAt(IExpr expr, ISymbol x, double value, EvalEngine engine) {
    try {
      IExpr substituted = F.subst(expr, x, F.num(value));
      IExpr numeric = engine.evaluate(F.N(substituted));
      if (numeric.isNumber()) {
        return numeric.evalf();
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return Double.NaN;
  }

  /**
   * Return a shorter but equivalent (on the real-zero set) form of {@code eqExpr} so that the
   * {@code Root[{body &, c}]} body printed by {@code FunctionRange} matches canonical output. Tries
   * {@code Simplify}, then (when both {@code Sin[x]} and {@code Cos[x]} appear) division by
   * {@code Cos[x]} followed by {@code Simplify}, and keeps the candidate with the smallest
   * {@code LeafCount}.
   *
   * <p>
   * Only {@code Cos[x]} is tried as a divisor — canonical form prefers {@code Tan[x]} over
   * {@code Cot[x]}.
   * 
   */
  private static IExpr canonicalizeRootBody(IExpr eqExpr, ISymbol x, EvalEngine engine) {
    IExpr best = eqExpr;
    long bestLeaves = best.leafCount();
    try {
      IExpr simplified = engine.evaluate(F.Simplify(eqExpr));
      if (simplified.isPresent() && !simplified.isFree(x, true)) {
        long lc = simplified.leafCount();
        if (lc < bestLeaves) {
          best = simplified;
          bestLeaves = lc;
        }
      }
      boolean hasSin = !eqExpr.isFree(arg -> arg.isAST(S.Sin, 2) && arg.first().equals(x), true);
      boolean hasCos = !eqExpr.isFree(arg -> arg.isAST(S.Cos, 2) && arg.first().equals(x), true);
      if (hasSin && hasCos) {
        // Only try Cos[x] as a divisor. Canonical Root[..] body prefers
        // Tan[x] over Cot[x], so dividing by Sin[x] (which would surface Cot) is skipped.
        // Accept ties (<=) so the Tan form wins over an equally-sized untransformed
        // Simplify result.
        IExpr cand = engine.evaluate(F.Simplify(F.Together(F.Divide(eqExpr, F.Cos(x)))));
        if (cand.isPresent() && !cand.isFree(x, true)) {
          long lc = cand.leafCount();
          if (lc <= bestLeaves) {
            best = cand;
            bestLeaves = lc;
          }
        }
      }

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return best;
  }

  private IExpr convertInterval(IExpr result, ISymbol y) {
    IAST list = (IAST) result.first();
    return convertMinMaxList(list, y);
  }

  /**
   * Convert a two-element {@code {lower, upper}} bounds list (as produced by the
   * {@code Interval}-visitor path, whose endpoints are closed by convention) into the normalized
   * range relational. {@code -Infinity} / {@code Infinity} endpoints are treated as unbounded, so a
   * full real line collapses to {@link S#True} instead of {@code y>=-Infinity}.
   */
  private IExpr convertMinMaxList(IAST list, ISymbol y) {
    Bound lo = closedEndpointBound(list.arg1(), true);
    Bound hi = closedEndpointBound(list.arg2(), false);
    if (!lo.known || !hi.known) {
      return F.NIL;
    }
    return buildRangeRelational(lo, hi, y);
  }

  /**
   * One side (lower or upper) of a computed range: either a finite bound (attained -> closed, or an
   * unattained limit -> open) or an unbounded side. {@link #known} is {@code false} when the side
   * could not be determined.
   */
  private static final class Bound {
    static final Bound UNKNOWN = new Bound(false, false, F.NIL, false);

    final boolean known;
    final boolean unbounded;
    final IExpr value;
    final boolean closed;

    private Bound(boolean known, boolean unbounded, IExpr value, boolean closed) {
      this.known = known;
      this.unbounded = unbounded;
      this.value = value;
      this.closed = closed;
    }

    static Bound unbounded() {
      return new Bound(true, true, F.NIL, false);
    }

    static Bound finite(IExpr value, boolean closed) {
      return new Bound(true, false, value, closed);
    }
  }

  /**
   * Combine a lower and an upper {@link Bound} into the range relational for {@code y}:
   * {@link S#True} for the whole real line, a single (possibly strict) inequality when one side is
   * unbounded, and a chained inequality (or {@code Inequality[...]} for a mixed open/closed pair)
   * when both sides are finite.
   */
  private static IExpr buildRangeRelational(Bound lo, Bound hi, ISymbol y) {
    if (lo.unbounded && hi.unbounded) {
      return S.True;
    }
    if (lo.unbounded) {
      return hi.closed ? F.LessEqual(y, hi.value) : F.Less(y, hi.value);
    }
    if (hi.unbounded) {
      return lo.closed ? F.GreaterEqual(y, lo.value) : F.Greater(y, lo.value);
    }
    if (lo.closed && hi.closed) {
      return F.LessEqual(lo.value, y, hi.value);
    }
    if (!lo.closed && !hi.closed) {
      return F.Less(lo.value, y, hi.value);
    }
    return F.Inequality(lo.value, lo.closed ? S.LessEqual : S.Less, y,
        hi.closed ? S.LessEqual : S.Less, hi.value);
  }

  /**
   * Interpret a {@code Minimize} ({@code isMin == true}) or {@code Maximize} result as a
   * {@link Bound}. A finite real extremum value is a bound whose open/closed flag comes from the
   * witness (a finite {@code {x -> value}} means attained -> closed; an asymptotic witness means an
   * unattained limit -> open). {@code Minimize -> -Infinity} / {@code Maximize -> +Infinity} is
   * unbounded. Empty {@code {}} lists and unevaluated results are {@link Bound#UNKNOWN}.
   */
  private static Bound interpretExtremum(IExpr minMax, boolean isMin) {
    if (!minMax.isList2()) {
      return Bound.UNKNOWN;
    }
    IExpr value = minMax.first();
    if (isMin) {
      if (value.isNegativeInfinity()) {
        return Bound.unbounded();
      }
    } else if (value.isInfinity()) {
      return Bound.unbounded();
    }
    if (isFiniteRealValue(value)) {
      return Bound.finite(value, isFiniteExtremum(minMax));
    }
    return Bound.UNKNOWN;
  }

  /**
   * Interpret one endpoint of a closed {@code {lower, upper}} bounds list as a {@link Bound}:
   * {@code -Infinity} (lower) / {@code Infinity} (upper) is unbounded, a finite real value is a
   * closed bound, everything else is {@link Bound#UNKNOWN}.
   */
  private static Bound closedEndpointBound(IExpr endpoint, boolean isLower) {
    if (isLower) {
      if (endpoint.isNegativeInfinity()) {
        return Bound.unbounded();
      }
    } else if (endpoint.isInfinity()) {
      return Bound.unbounded();
    }
    if (isFiniteRealValue(endpoint)) {
      return Bound.finite(endpoint, true);
    }
    return Bound.UNKNOWN;
  }

  /**
   * Return {@code true} iff {@code v} is a finite real value. Rejects every flavour of infinity and
   * {@code Indeterminate} first, because {@code isRealResult()} returns {@code true} for the directed
   * infinities.
   */
  private static boolean isFiniteRealValue(IExpr v) {
    if (v.isInfinity() || v.isNegativeInfinity() || v.isComplexInfinity() || v.isDirectedInfinity()
        || v.equals(S.Indeterminate)) {
      return false;
    }
    return v.isRealResult();
  }

  // public IExpr evaluate(final IAST ast, EvalEngine engine) {
  // IExpr function = ast.arg1();
  // IExpr xExpr = ast.arg2();
  // IExpr yExpr = ast.arg3();
  // IBuiltInSymbol domain = S.Reals;
  // try {
  // if (xExpr.isSymbol() && yExpr.isSymbol()) {
  // IAST constrained_interval = IntervalDataSym.reals();
  // if (function.isAST()) {
  // IAST f = (IAST) function;
  // for (int i = 1; i < f.size(); i++) {
  // IExpr arg = f.get(i);
  // if (arg.isPower()) {
  //
  // } else if (arg.isLog()) {
  //
  // }
  // }
  // }
  // }
  // } catch (RuntimeException rex) {
  // rex.printStackTrace();
  // }
  // return F.NIL;
  // }
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    IExpr xExpr = ast.arg2();
    IExpr yExpr = ast.arg3();
    IBuiltInSymbol domain = S.Reals;
    try {
      if (xExpr.isSymbol() && yExpr.isSymbol()) {
        ISymbol x = (ISymbol) xExpr;
        ISymbol y = (ISymbol) yExpr;

        // Fast path: closed-form range for a known built-in function applied directly to the
        // range variable, e.g. FunctionRange(Cosh(x), x, y) -> 1<=y.
        IExpr builtin = builtinRange(function, x, y);
        if (builtin.isPresent()) {
          return builtin;
        }

        // Transcendental short-circuit: for expressions involving Sin/Cos/Tan/Exp/Log/... the
        // Minimize/Maximize, IntervalData and Interval paths below tend to either fail or
        // return a degenerate result (e.g. 0<=y<=0 from the asymptotic limit at infinity, or
        // from naive interval arithmetic Sin[Reals]/Sqrt[Reals] -> {0}). Try the numerical
        // critical-point fallback first so the WMA-style Root[{f, c}] inequality is
        // returned when possible. Polynomial / rational inputs skip this branch and use the
        // existing exact paths unchanged.
        if (containsTranscendental(function)) {
          IExpr critRange = numericalCriticalPointRange(function, x, y, engine);
          if (critRange.isPresent()) {
            return critRange;
          }
        }

        // Closed-form extrema via Minimize / Maximize. Classify each side from the extremum
        // VALUE: a finite real number is a bound (attained -> closed, or an unattained limit
        // -> open, decided by the witness), Minimize -> -Infinity / Maximize -> +Infinity means
        // unbounded on that side, and anything else (an empty {} list, or a left-unevaluated
        // Minimize[...]) is unknown. Symja often returns the asymptotic limit (e.g.
        // {0, {x -> Infinity}}) for bounded oscillating functions such as Sin[x]/Sqrt[x], hence
        // the witness-based open/closed check. Return only when BOTH sides are determined;
        // otherwise fall through to the interval heuristics below so e.g. 1/(1+x^2)
        // (Minimize -> {}) can still be handled there.
        IExpr min = engine.evalQuiet(F.Minimize(function, x));
        IExpr max = engine.evalQuiet(F.Maximize(function, x));
        Bound lo = interpretExtremum(min, true);
        Bound hi = interpretExtremum(max, false);
        if (lo.known && hi.known) {
          return buildRangeRelational(lo, hi, y);
        }
        // Minimize/Maximize could not determine a closed-form extremum for at least one
        // side. Try substituting with IntervalData over the reals so that open/closed
        // endpoints are preserved (e.g. E^x -> (0, Infinity) yields y>0 instead of
        // y>=0). Naive interval arithmetic on expressions where the variable occurs
        // multiple times (e.g. x/(1+x^2)) loses correlation and typically degrades to
        // the full real line; in that case ignore the result and fall through to the
        // Interval-based visitor heuristics below.
        try {
          IExpr fIntervalData = F.subst(function, x, IntervalDataSym.reals());
          IExpr resultIntervalData = engine.evaluate(fIntervalData);
          if (resultIntervalData.isIntervalData() && resultIntervalData.size() > 1) {
            IExpr rel = IntervalDataSym.asRelational((IAST) resultIntervalData, y);
            if (rel.isPresent() && !rel.isTrue()) {
              return rel;
            }
          }
        } catch (RuntimeException rex) {
          // fall through
        }
        IExpr f = F.subst(function, x, F.Interval(F.CNInfinity, F.CInfinity));
        IExpr result = engine.evaluate(f);
        if (result.isInterval1()) {
          return convertInterval(result, y);
        } else if (domain.equals(S.Reals)) {
          IExpr temp = result;
          while (temp.isPresent()) {
            temp = temp.accept(new FunctionRangeRealsVisitor());
            if (temp.isPresent()) {
              result = engine.evaluate(temp);
              temp = result;
            }
          }
          if (result.isInterval1()) {
            return convertInterval(result, y);
          }
          // Final fallback: numerically locate critical points of f'(x)==0 over a bounded
          // real range and wrap them as Root[{eq &, c}] placeholders so the resulting
          // Inequality keeps a symbolic form.
          IExpr critRange = numericalCriticalPointRange(function, x, y, engine);
          if (critRange.isPresent()) {
            return critRange;
          }
        }
      }

    } catch (RuntimeException rex) {
      // falls through
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }

  /**
   * Search the real line within a bounded window for critical points of {@code function} (where
   * {@code D[function, x] == 0}) and return a symbolic {@code Inequality} bounding {@code y} by the
   * smallest / largest function values at those critical points. Each critical point is wrapped as
   * {@code Root[{eq &, c}]}, an inert symbolic placeholder for the transcendental root.
   *
   * <p>
   * Example: for {@code FunctionRange[Sin[x]/Sqrt[x], x, y]} this produces
   * {@code Sin[Root[{2 x Cos[x] - Sin[x] &, c1}]]/Sqrt[Root[{..., c1}]] <= y <= ...}.
   *
   * @return the bounding inequality, or {@link F#NIL} if no usable critical points are found.
   */
  private IExpr numericalCriticalPointRange(IExpr function, ISymbol x, ISymbol y,
      EvalEngine engine) {
    try {
      // Only attempt for expressions that actually depend on x
      if (function.isFree(x, true)) {
        return F.NIL;
      }
      // Compute the derivative; bail if D leaves it unevaluated
      IExpr df = engine.evaluate(F.D(function, x));
      if (df.isAST(S.D) || df.isFree(x, true)) {
        return F.NIL;
      }
      // Extract the "interesting" zero-set: numerator of Together[df]
      IExpr together = engine.evaluate(F.Together(df));
      IExpr eqExpr = engine.evaluate(F.Numerator(together));
      if (eqExpr.isFree(x, true)) {
        return F.NIL;
      }
      // Build a canonical, short form for the Root[{body &, c}] body so the printed
      // representation matches WMA (e.g. 2 x Cos[x] - Sin[x] -> 2 x - Tan[x]).
      // The original eqExpr is still used for sampling / FindRoot below so the root
      // locations stay exact.
      IExpr eqExprForRoot = canonicalizeRootBody(eqExpr, x, engine);
      // Build pure Function: replace x by Slot1 in eqExprForRoot → (body &)
      IExpr body = F.subst(eqExprForRoot, x, F.Slot1);
      IAST pureFunction = F.Function(body);

      // Sample over a bounded window looking for sign changes of eqExpr(x)
      // (heuristic — covers the typical range where global extrema occur).
      final double xMin = 0.01;
      final double xMax = 50.0;
      final double step = 0.1;
      List<Double> roots = new ArrayList<>();
      double prevX = xMin;
      double prevVal = sampleAt(eqExpr, x, prevX, engine);
      for (double xs = xMin + step; xs <= xMax; xs += step) {
        double curVal = sampleAt(eqExpr, x, xs, engine);
        if (!Double.isNaN(prevVal) && !Double.isNaN(curVal) && prevVal * curVal < 0.0) {
          // Sign change in [prevX, xs] → refine with FindRoot
          double mid = 0.5 * (prevX + xs);
          Double refined = findRootRefine(eqExpr, x, mid, engine);
          if (refined != null) {
            // Avoid duplicates
            boolean dup = false;
            for (Double r : roots) {
              if (Math.abs(r - refined) < 1.0e-6) {
                dup = true;
                break;
              }
            }
            if (!dup) {
              roots.add(refined);
            }
          }
        }
        prevX = xs;
        prevVal = curVal;
      }
      if (roots.isEmpty()) {
        return F.NIL;
      }

      // For each critical point build Root[{pureFunction, c}] and substitute into function
      IExpr yMinExpr = F.NIL;
      IExpr yMaxExpr = F.NIL;
      double yMinNum = Double.POSITIVE_INFINITY;
      double yMaxNum = Double.NEGATIVE_INFINITY;
      for (Double c : roots) {
        IAST rootExpr = F.unaryAST1(S.Root, F.list(pureFunction, F.num(c)));
        IExpr yCandidate = engine.evaluate(F.subst(function, x, rootExpr));
        double yNum;
        try {
          yNum = engine.evaluate(F.N(yCandidate)).evalf();
        } catch (ArgumentTypeException atex) {
          continue;
        }
        if (Double.isNaN(yNum) || Double.isInfinite(yNum)) {
          continue;
        }
        if (yNum < yMinNum) {
          yMinNum = yNum;
          yMinExpr = yCandidate;
        }
        if (yNum > yMaxNum) {
          yMaxNum = yNum;
          yMaxExpr = yCandidate;
        }
      }

      // Also probe the boundary of the real-valued domain of f itself. The derivative-zero
      // scan above misses extrema that occur where f transitions between real and complex
      // (e.g. Sqrt[Sin[2 x]] attains its minimum 0 exactly where Sin[2 x] == 0, the edge
      // of its real domain). Detect such transitions by watching sampleAt(f) flip between
      // a finite real value and NaN (complex-valued evaluations also return NaN here),
      // then refine the boundary x* via bisection.
      List<Double> boundaryRoots = new ArrayList<>();
      double prevBx = xMin;
      double prevBv = sampleAt(function, x, prevBx, engine);
      for (double xs = xMin + step; xs <= xMax; xs += step) {
        double curBv = sampleAt(function, x, xs, engine);
        boolean prevReal = !Double.isNaN(prevBv) && !Double.isInfinite(prevBv);
        boolean curReal = !Double.isNaN(curBv) && !Double.isInfinite(curBv);
        if (prevReal ^ curReal) {
          // Bisection: keep lo on the real side and hi on the NaN side.
          double lo = prevReal ? prevBx : xs;
          double hi = prevReal ? xs : prevBx;
          for (int it = 0; it < 60; it++) {
            double mid = 0.5 * (lo + hi);
            double mv = sampleAt(function, x, mid, engine);
            if (!Double.isNaN(mv) && !Double.isInfinite(mv)) {
              lo = mid;
            } else {
              hi = mid;
            }
            if (Math.abs(hi - lo) < 1.0e-9) {
              break;
            }
          }
          double refined = lo; // last known real-valued sample point on the boundary
          boolean dup = false;
          for (Double r : boundaryRoots) {
            if (Math.abs(r - refined) < 1.0e-6) {
              dup = true;
              break;
            }
          }
          if (!dup) {
            for (Double r : roots) {
              if (Math.abs(r - refined) < 1.0e-6) {
                dup = true;
                break;
              }
            }
          }
          if (!dup) {
            boundaryRoots.add(refined);
          }
        }
        prevBx = xs;
        prevBv = curBv;
      }
      for (Double c : boundaryRoots) {
        // Prefer the unevaluated Subst form, mirroring the Root[..] style used above.
        IExpr yCandidate = F.subst(function, x, F.num(c));
        double yNum;
        try {
          yNum = engine.evaluate(F.N(yCandidate)).evalf();
        } catch (ArgumentTypeException atex) {
          continue;
        }
        if (Double.isNaN(yNum) || Double.isInfinite(yNum)) {
          continue;
        }
        // The bisection only narrows to ~1e-9 of the true boundary. For Sqrt-style
        // edges (f(x) ~ sqrt(g(x)) with g vanishing linearly at x*), this gives a
        // residual value of order sqrt(1e-9) ~ 3e-5 — not exactly 0. Snap to exact 0
        // when both the real-side sample and the modulus of the complex-side sample
        // (just past the boundary) are negligibly small — the signature of a
        // limit-to-zero boundary such as Sqrt[Sin[2 x]] at Sin[2 x] == 0.
        double yAbsHi = sampleAbsAt(function, x, c + 1.0e-9, engine);
        if (Math.abs(yNum) < 1.0e-3 && (Double.isNaN(yAbsHi) || yAbsHi < 1.0e-3)) {
          yNum = 0.0;
          yCandidate = F.C0;
        }
        if (yNum < yMinNum) {
          yMinNum = yNum;
          yMinExpr = yCandidate;
        }
        if (yNum > yMaxNum) {
          yMaxNum = yNum;
          yMaxExpr = yCandidate;
        }
      }



      if (yMinExpr.isNIL() || yMaxExpr.isNIL()) {
        return F.NIL;
      }

      // Compare against limits at +/- Infinity so global extrema at the boundary are not
      // missed (e.g. monotonic transcendental functions). A finite boundary limit beats
      // a critical-point value when smaller (for the min) or larger (for the max).
      double[] limitBounds = boundaryLimits(function, x, engine);
      double limNegNum = limitBounds[0];
      double limPosNum = limitBounds[1];
      boolean lowerOpen = false;
      boolean upperOpen = false;
      if (!Double.isNaN(limNegNum)) {
        if (limNegNum < yMinNum) {
          yMinNum = limNegNum;
          yMinExpr = F.num(limNegNum);
          lowerOpen = true;
        }
        if (limNegNum > yMaxNum) {
          yMaxNum = limNegNum;
          yMaxExpr = F.num(limNegNum);
          upperOpen = true;
        }
      }
      if (!Double.isNaN(limPosNum)) {
        if (limPosNum < yMinNum) {
          yMinNum = limPosNum;
          yMinExpr = F.num(limPosNum);
          lowerOpen = true;
        }
        if (limPosNum > yMaxNum) {
          yMaxNum = limPosNum;
          yMaxExpr = F.num(limPosNum);
          upperOpen = true;
        }
      }

      if (yMinExpr.equals(yMaxExpr)) {
        return F.Equal(y, yMinExpr);
      }
      // Use Less for open boundary bounds and LessEqual otherwise.
      IExpr lower = lowerOpen ? F.Less(yMinExpr, y) : F.LessEqual(yMinExpr, y);
      IExpr upper = upperOpen ? F.Less(y, yMaxExpr) : F.LessEqual(y, yMaxExpr);
      if (lowerOpen || upperOpen) {
        return F.And(lower, upper);
      }
      return F.LessEqual(yMinExpr, y, yMaxExpr);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Evaluate {@code Abs[expr]} with {@code x -> value} as a machine-precision double. Returns
   * {@link Double#NaN} when the result is not a finite real number. Used by the boundary-of-
   * real-domain probe to test whether the complex-valued side of a domain edge is also tending to
   * zero (so the limit-from-the-real-side can safely be snapped to exact 0).
   */
  private static double sampleAbsAt(IExpr expr, ISymbol x, double value, EvalEngine engine) {
    try {
      IExpr substituted = F.subst(expr, x, F.num(value));
      IExpr numeric = engine.evaluate(F.N(F.Abs(substituted)));
      if (numeric.isNumber()) {
        double v = numeric.evalf();
        if (!Double.isNaN(v) && !Double.isInfinite(v)) {
          return v;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return Double.NaN;
  }

  /**
   * Compute {@code Limit[function, x -> -Infinity]} and {@code Limit[function, x -> +Infinity]} as
   * machine-precision doubles. Returns {@link Double#NaN} for either side if the limit is not a
   * finite real number.
   */
  private static double[] boundaryLimits(IExpr function, ISymbol x, EvalEngine engine) {
    return new double[] {evalLimit(function, x, F.CNInfinity, engine),
        evalLimit(function, x, F.CInfinity, engine)};
  }

  private static double evalLimit(IExpr function, ISymbol x, IExpr direction, EvalEngine engine) {
    try {
      IExpr lim = engine.evalQuiet(F.Limit(function, F.Rule(x, direction)));
      if (lim.isNumber()) {
        double v = lim.evalf();
        if (!Double.isNaN(v) && !Double.isInfinite(v)) {
          return v;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return Double.NaN;
  }

  /**
   * Return {@code true} iff {@code function} contains a transcendental head (Sin, Cos, Tan, Cot,
   * Sec, Csc, ArcSin..., Sinh, Cosh, Tanh, Exp, Log, ...). Used to decide whether to attempt the
   * numerical critical-point fallback before the closed-form Minimize/Maximize path, since Symja's
   * closed-form path often degrades to the asymptotic limit (e.g. {@code 0<=y<=0}) for such
   * functions.
   */
  private static boolean containsTranscendental(IExpr function) {
    return !function.isFree(arg -> arg.isAST() && isTranscendentalHead(arg.head()), true);
  }

  private static boolean isTranscendentalHead(IExpr head) {
    return head == S.Sin || head == S.Cos || head == S.Tan || head == S.Cot //
        || head == S.Sec || head == S.Csc //
        || head == S.ArcSin || head == S.ArcCos || head == S.ArcTan || head == S.ArcCot //
        || head == S.ArcSec || head == S.ArcCsc //
        || head == S.Sinh || head == S.Cosh || head == S.Tanh || head == S.Coth //
        || head == S.Sech || head == S.Csch //
        || head == S.ArcSinh || head == S.ArcCosh || head == S.ArcTanh || head == S.ArcCoth //
        || head == S.Exp || head == S.Log;
  }

  /**
   * Return {@code true} iff the witness rule {@code {x -> value}} of a {@code Minimize} /
   * {@code Maximize} result has a finite, real-valued {@code value}. Rejects asymptotic results
   * such as {@code {x -> Infinity}} which would otherwise short-circuit FunctionRange with a
   * trivial inequality.
   */
  private static boolean isFiniteExtremum(IExpr minMax) {
    IExpr witness = minMax.second();
    if (witness.isList() && witness.size() >= 2) {
      witness = witness.first();
    }
    if (!witness.isRuleAST()) {
      return false;
    }
    IExpr v = witness.second();
    if (v.isInfinity() || v.isNegativeInfinity() || v.isComplexInfinity()
        || v.equals(S.Indeterminate) || v.isAST(S.Limit) || v.isAST(S.DirectedInfinity)) {
      return false;
    }
    return true;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}
