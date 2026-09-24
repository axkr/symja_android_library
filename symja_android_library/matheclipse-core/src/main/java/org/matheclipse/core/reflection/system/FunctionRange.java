package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.S.Power;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
   * matching {@code FunctionRange(head(x), x, y)}. Returns {@link F#NIL} when {@code function} is
   * not such a form or its head is not tabled here.
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
      case ID.ArcCot:
        // ArcCot jumps at 0, where it takes the value Pi/2 of the upper branch
        return F.Or(F.Less(F.CNPiHalf, y, F.C0),
            F.Inequality(F.C0, S.Less, y, S.LessEqual, F.CPiHalf));
      case ID.Ceiling:
      case ID.Floor:
      case ID.IntegerPart:
      case ID.Round:
        return F.Element(y, S.Integers);
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
          double root = rule.second().evalfNaN();
          if (!Double.isNaN(root)) {
            return root;
          }
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
        return numeric.evalfNaN();
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
   * The range of a function which is constant on each side of <code>x==0</code>, as
   * <code>x/Abs(x)</code> or <code>Sign(x)</code>: the values of both sides and the value at
   * <code>0</code>, if it is defined.
   *
   * @return <code>y==v1||y==v2...</code> or {@link F#NIL}
   */
  private static IExpr piecewiseConstantRange(IExpr function, ISymbol x, ISymbol y,
      EvalEngine engine) {
    if (function.isFree(h -> h == S.Abs || h == S.Sign, true)) {
      return F.NIL;
    }
    IExpr positive = engine.evalQuiet(F.Refine(function, F.Greater(x, F.C0)));
    IExpr negative = engine.evalQuiet(F.Refine(function, F.Less(x, F.C0)));
    if (!positive.isFree(x) || !negative.isFree(x)) {
      return signSplitRange(function, positive, negative, x, y, engine);
    }
    if (!isFiniteRealValue(positive) || !isFiniteRealValue(negative)) {
      return F.NIL;
    }
    IExpr atZero = engine.evalQuiet(F.subst(function, x, F.C0));
    IASTAppendable values = F.ListAlloc(3);
    values.append(negative);
    values.append(positive);
    if (isFiniteRealValue(atZero)) {
      values.append(atZero);
    }
    IExpr sorted = engine.evaluate(F.Union(values));
    if (!sorted.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) sorted;
    IASTAppendable or = F.ast(S.Or, list.argSize());
    for (IExpr value : list) {
      or.append(F.Equal(y, value));
    }
    return or.oneIdentity0();
  }

  /**
   * The range of a function with <code>Abs(x)</code> or <code>Sign(x)</code> as the union of the
   * ranges of its two branches on the half-lines and its value at <code>0</code>:
   * <code>Abs(x)+x</code> is <code>2*x</code> for <code>x&gt;0</code> and <code>0</code> for
   * <code>x&lt;0</code>, so its range is <code>y&gt;=0</code>.
   *
   * @return the range or {@link F#NIL} if a branch still contains <code>Abs</code> or
   *         <code>Sign</code>, or its range cannot be determined
   */
  private static IExpr signSplitRange(IExpr function, IExpr positive, IExpr negative, ISymbol x,
      ISymbol y, EvalEngine engine) {
    if (!positive.isFree(h -> h == S.Abs || h == S.Sign, true)
        || !negative.isFree(h -> h == S.Abs || h == S.Sign, true)) {
      return F.NIL;
    }
    List<Span> spans = new ArrayList<>();
    Span positiveSpan = branchSpan(positive, F.Greater(x, F.C0), x, engine);
    Span negativeSpan = branchSpan(negative, F.Less(x, F.C0), x, engine);
    if (positiveSpan == null || negativeSpan == null) {
      return F.NIL;
    }
    spans.add(positiveSpan);
    spans.add(negativeSpan);
    IExpr atZero = engine.evalQuiet(F.subst(function, x, F.C0));
    if (isFiniteRealValue(atZero)) {
      Span point = Span.point(atZero);
      if (point == null) {
        return F.NIL;
      }
      spans.add(point);
    }
    // merge overlapping or touching intervals, ordered by their lower ends
    // at equal lower ends a closed one first, so that a point closes the gap between two spans
    spans.sort((s1, s2) -> s1.loNum != s2.loNum ? Double.compare(s1.loNum, s2.loNum)
        : Boolean.compare(!s1.lo.closed, !s2.lo.closed));
    List<Span> merged = new ArrayList<>();
    for (Span span : spans) {
      Span last = merged.isEmpty() ? null : merged.get(merged.size() - 1);
      if (last != null && (span.loNum < last.hiNum
          || (span.loNum == last.hiNum && (last.hi.closed || span.lo.closed)))) {
        if (span.hiNum > last.hiNum || (span.hiNum == last.hiNum && span.hi.closed)) {
          last.hi = span.hi;
          last.hiNum = span.hiNum;
        }
        if (span.loNum == last.loNum && span.lo.closed) {
          last.lo = span.lo;
        }
      } else {
        merged.add(span.copy());
      }
    }
    IASTAppendable or = F.ast(S.Or, merged.size());
    for (Span span : merged) {
      if (span.loNum == span.hiNum && !span.lo.unbounded) {
        or.append(F.Equal(y, span.lo.value));
      } else {
        or.append(buildRangeRelational(span.lo, span.hi, y));
      }
    }
    return engine.evaluate(or.oneIdentity0());
  }

  /** An interval of values: its two {@link Bound}s and their numeric values for comparing. */
  private static final class Span {
    Bound lo;
    Bound hi;
    double loNum;
    double hiNum;

    Span(Bound lo, double loNum, Bound hi, double hiNum) {
      this.lo = lo;
      this.loNum = loNum;
      this.hi = hi;
      this.hiNum = hiNum;
    }

    static Span point(IExpr value) {
      double d = value.evalfNaN();
      if (Double.isNaN(d)) {
        return null;
      }
      Bound bound = Bound.finite(value, true);
      return new Span(bound, d, bound, d);
    }

    Span copy() {
      return new Span(lo, loNum, hi, hiNum);
    }
  }

  /**
   * The values of a branch of the function on the half-line <code>constraint</code>, from the
   * constrained {@code Minimize} and {@code Maximize}: an extremum is attained only if its witness
   * satisfies the constraint.
   *
   * @return <code>null</code> if an extremum cannot be determined
   */
  private static Span branchSpan(IExpr branch, IExpr constraint, ISymbol x, EvalEngine engine) {
    if (branch.isFree(x, true)) {
      return isFiniteRealValue(branch) ? Span.point(branch) : null;
    }
    IExpr min = engine.evalQuiet(F.Minimize(F.list(branch, constraint), x));
    IExpr max = engine.evalQuiet(F.Maximize(F.list(branch, constraint), x));
    Bound lo = branchBound(min, true, constraint, x, engine);
    Bound hi = branchBound(max, false, constraint, x, engine);
    if (lo == null || hi == null) {
      return null;
    }
    double loNum = lo.unbounded ? Double.NEGATIVE_INFINITY : lo.value.evalfNaN();
    double hiNum = hi.unbounded ? Double.POSITIVE_INFINITY : hi.value.evalfNaN();
    if (Double.isNaN(loNum) || Double.isNaN(hiNum)) {
      return null;
    }
    return new Span(lo, loNum, hi, hiNum);
  }

  private static Bound branchBound(IExpr extremum, boolean isMin, IExpr constraint, ISymbol x,
      EvalEngine engine) {
    if (!extremum.isList2() || !extremum.second().isList1()
        || !extremum.second().first().isRuleAST()) {
      return null;
    }
    IExpr value = extremum.first();
    if (isMin ? value.isNegativeInfinity() : value.isInfinity()) {
      return Bound.unbounded();
    }
    if (!isFiniteRealValue(value)) {
      return null;
    }
    IExpr witness = extremum.second().first().second();
    boolean attained = isFiniteRealValue(witness)
        && engine.evalTrue(F.subst(constraint, x, witness));
    return Bound.finite(value, attained);
  }

  /**
   * The range of <code>f(g(x))</code> for an outer function <code>f</code> which is periodic or
   * increasing: the image of the range of <code>g</code> under <code>f</code>, e.g.
   * <code>Sin(Exp(x))</code> covers a full period, so its range is <code>-1&lt;=y&lt;=1</code>,
   * and <code>Exp(Sin(x))</code> is <code>1/E&lt;=y&lt;=E</code>.
   */
  private static IExpr compositionRange(IExpr function, ISymbol x, ISymbol y,
      EvalEngine engine) {
    IExpr inner;
    int id;
    if (function.isPower() && function.base() == S.E) {
      // E^g(x)
      inner = function.exponent();
      id = ID.Exp;
    } else if (function.isAST1() && function.head().isBuiltInSymbol()) {
      inner = function.first();
      id = ((IBuiltInSymbol) function.head()).ordinal();
    } else {
      return F.NIL;
    }
    if (inner.equals(x) || inner.isFree(x, true)) {
      return F.NIL;
    }
    boolean periodic = id == ID.Sin || id == ID.Cos;
    boolean increasing = id == ID.Exp || id == ID.ArcTan || id == ID.Tanh || id == ID.Sinh
        || id == ID.ArcSinh || id == ID.Erf || id == ID.Log || id == ID.Sqrt;
    if (!periodic && !increasing) {
      return F.NIL;
    }
    ISymbol t = F.Dummy("t");
    IExpr innerRange = engine.evalQuiet(F.FunctionRange(inner, x, t));
    Bound[] bounds = rangeBounds(innerRange, t);
    if (bounds == null) {
      return F.NIL;
    }
    Bound lo = bounds[0];
    Bound hi = bounds[1];
    if (periodic) {
      if (lo.unbounded || hi.unbounded
          || engine.evalTrue(F.GreaterEqual(F.Subtract(hi.value, lo.value), F.C2Pi))) {
        return F.LessEqual(F.CN1, y, F.C1);
      }
      return F.NIL;
    }
    if (id == ID.Log || id == ID.Sqrt) {
      // the inner range has to lie in the real domain [0, Infinity)
      if (lo.unbounded || !engine.evalTrue(F.GreaterEqual(lo.value, F.C0))) {
        return F.NIL;
      }
      if (id == ID.Log && lo.value.isZero()) {
        if (lo.closed) {
          return F.NIL;
        }
        lo = Bound.unbounded();
      }
    }
    IExpr f = id == ID.Exp ? F.Function(F.Power(S.E, F.Slot1)) : function.head();
    Bound imageLo = image(f, lo, F.CNInfinity, engine);
    Bound imageHi = image(f, hi, F.CInfinity, engine);
    if (!imageLo.known || !imageHi.known) {
      return F.NIL;
    }
    return buildRangeRelational(imageLo, imageHi, y);
  }

  /**
   * The image of one end of an interval under an increasing function <code>f</code>; an unbounded
   * end is mapped to the limit <code>f(infinity)</code>, which isn't attained.
   */
  private static Bound image(IExpr f, Bound bound, IExpr infinity, EvalEngine engine) {
    if (!bound.known) {
      return Bound.UNKNOWN;
    }
    if (bound.unbounded) {
      if (f == S.Log) {
        // the open end 0 of the inner range
        return Bound.unbounded();
      }
      IExpr limit = engine.evalQuiet(F.unaryAST1(f, infinity));
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        return Bound.unbounded();
      }
      return isFiniteRealValue(limit) ? Bound.finite(limit, false) : Bound.UNKNOWN;
    }
    IExpr value = engine.evalQuiet(F.unaryAST1(f, bound.value));
    return isFiniteRealValue(value) ? Bound.finite(value, bound.closed) : Bound.UNKNOWN;
  }

  /**
   * The lower and upper {@link Bound} of a range relation of <code>t</code> as
   * {@code FunctionRange} returns it: <code>True</code>, a one sided or a two sided inequality.
   *
   * @return <code>null</code> for any other relation
   */
  private static Bound[] rangeBounds(IExpr range, IExpr t) {
    if (range.isTrue()) {
      return new Bound[] {Bound.unbounded(), Bound.unbounded()};
    }
    if (range.isAST(S.Inequality, 6) && range.getAt(3).equals(t)) {
      return new Bound[] {Bound.finite(range.first(), range.getAt(2) == S.LessEqual),
          Bound.finite(range.getAt(5), range.getAt(4) == S.LessEqual)};
    }
    if (!range.isAST() || !range.isFunctionID(ID.Less, ID.LessEqual, ID.Greater,
        ID.GreaterEqual)) {
      return null;
    }
    int id = range.headID();
    boolean closed = id == ID.LessEqual || id == ID.GreaterEqual;
    if (range.isAST3() && range.second().equals(t)) {
      if (id == ID.Less || id == ID.LessEqual) {
        return new Bound[] {Bound.finite(range.first(), closed),
            Bound.finite(range.getAt(3), closed)};
      }
      return null;
    }
    if (!range.isAST2()) {
      return null;
    }
    boolean ascending = id == ID.Less || id == ID.LessEqual;
    if (range.first().equals(t)) {
      // t<b or t>a
      return ascending ? new Bound[] {Bound.unbounded(), Bound.finite(range.second(), closed)}
          : new Bound[] {Bound.finite(range.second(), closed), Bound.unbounded()};
    }
    if (range.second().equals(t)) {
      // a<t or b>t
      return ascending ? new Bound[] {Bound.finite(range.first(), closed), Bound.unbounded()}
          : new Bound[] {Bound.unbounded(), Bound.finite(range.first(), closed)};
    }
    return null;
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
   * {@code Indeterminate} first, because {@code isRealResult()} returns {@code true} for the
   * directed infinities.
   */
  private static boolean isFiniteRealValue(IExpr v) {
    if (v.isInfinity() || v.isNegativeInfinity() || v.isComplexInfinity() || v.isDirectedInfinity()
        || v == S.Indeterminate) {
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
        IExpr piecewiseConstant = piecewiseConstantRange(function, x, y, engine);
        if (piecewiseConstant.isPresent()) {
          return piecewiseConstant;
        }
        IExpr composition = compositionRange(function, x, y, engine);
        if (composition.isPresent()) {
          return composition;
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
        } else if (domain == S.Reals) {
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
      // the positive half-line first, so that the root of an even function is the positive one
      for (double side : new double[] {1.0, -1.0}) {
      double prevX = side * xMin;
      double prevVal = sampleAt(eqExpr, x, prevX, engine);
      for (double xsAbs = xMin + step; xsAbs <= xMax; xsAbs += step) {
        double xs = side * xsAbs;
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
        IAST rootExpr = F.Root(F.list(pureFunction, F.num(c)));
        IExpr yCandidate = engine.evalQuiet(F.subst(function, x, rootExpr));
        double yNum = engine.evaluate(F.N(yCandidate)).evalfNaN();
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
      for (double side : new double[] {1.0, -1.0}) {
      double prevBx = side * xMin;
      double prevBv = sampleAt(function, x, prevBx, engine);
      for (double xsAbs = xMin + step; xsAbs <= xMax; xsAbs += step) {
        double xs = side * xsAbs;
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
      }
      for (Double c : boundaryRoots) {
        // Prefer the unevaluated Subst form, mirroring the Root[..] style used above.
        IExpr yCandidate = F.subst(function, x, F.num(c));
        double yNum = engine.evaluate(F.N(yCandidate)).evalfNaN();
        if (Double.isNaN(yNum) || Double.isInfinite(yNum)) {
          continue;
        }
        // The bisection only narrows to ~1e-9 of the true boundary. For Sqrt-style
        // edges (f(x) ~ sqrt(g(x)) with g vanishing linearly at x*), this gives a
        // residual value of order sqrt(1e-9) ~ 3e-5 — not exactly 0. Snap to exact 0
        // when both the real-side sample and the modulus of the complex-side sample
        // (just past the boundary) are negligibly small — the signature of a
        // limit-to-zero boundary such as Sqrt[Sin[2 x]] at Sin[2 x] == 0.
        // the complex side lies to the right on the positive and to the left on the negative scan
        double yAbsHi = Math.min(sampleAbsAt(function, x, c + 1.0e-9, engine),
            sampleAbsAt(function, x, c - 1.0e-9, engine));
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

      // Compare against the limits at +/- Infinity and at the real poles and removable
      // singularities of the function, so that extrema which are not attained are not missed
      // (e.g. Sin(x)/x tends to its supremum 1 at x == 0). A finite limit is an open bound, an
      // infinite one makes that side unbounded, and a limit which cannot be determined on a side
      // where the function is real leaves the range undecided.
      List<IExpr> limits = new ArrayList<>();
      if (!collectLimits(function, x, yMinNum, yMaxNum, engine, limits)) {
        return F.NIL;
      }
      boolean lowerOpen = false;
      boolean upperOpen = false;
      boolean lowerUnbounded = false;
      boolean upperUnbounded = false;
      for (IExpr limitExpr : limits) {
        if (limitExpr.isNegativeInfinity()) {
          lowerUnbounded = true;
        } else if (limitExpr.isInfinity()) {
          upperUnbounded = true;
        } else {
          double limit = limitExpr.evalfNaN();
          if (limit < yMinNum) {
            yMinNum = limit;
            yMinExpr = limitExpr;
            lowerOpen = true;
          }
          if (limit > yMaxNum) {
            yMaxNum = limit;
            yMaxExpr = limitExpr;
            upperOpen = true;
          }
        }
      }
      if (lowerUnbounded && upperUnbounded) {
        return S.True;
      }
      if (lowerUnbounded) {
        return upperOpen ? F.Less(y, yMaxExpr) : F.LessEqual(y, yMaxExpr);
      }
      if (upperUnbounded) {
        return lowerOpen ? F.Greater(y, yMinExpr) : F.GreaterEqual(y, yMinExpr);
      }

      if (yMinExpr.equals(yMaxExpr)) {
        return F.Equal(y, yMinExpr);
      }
      // Less for a bound which is only a limit, LessEqual otherwise
      return buildRangeRelational(Bound.finite(yMinExpr, !lowerOpen),
          Bound.finite(yMaxExpr, !upperOpen), y);
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
        double v = numeric.evalfNaN();
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
  /**
   * Collect the limits of the function at +/- Infinity and from both sides of the real zeros of
   * its denominator, on every side where the function is real.
   *
   * @return <code>false</code> if a limit on a real side cannot be determined
   */
  private static boolean collectLimits(IExpr function, ISymbol x, double yMin, double yMax,
      EvalEngine engine, List<IExpr> limits) {
    for (double side : new double[] {-1.0, 1.0}) {
      IExpr infinity = side < 0 ? F.CNInfinity : F.CInfinity;
      if (!addLimit(function, x, infinity, F.NIL, side * 50.0, engine, limits)
          && !isBoundedOscillation(function, x, side, yMin, yMax, engine)) {
        return false;
      }
    }
    IExpr denominator = engine.evalQuiet(F.Denominator(F.Together(function)));
    if (denominator.isFree(x, true) || !denominator.isPolynomial(x)) {
      return true;
    }
    IExpr poles = engine.evalQuiet(F.Solve(F.Equal(denominator, F.C0), x, S.Reals));
    if (!poles.isListOfLists()) {
      return true;
    }
    for (IExpr solution : (IAST) poles) {
      IExpr pole = solution.first().second();
      double p = pole.evalfNaN();
      if (Double.isNaN(p)) {
        continue;
      }
      if (!addLimit(function, x, pole, F.stringx("FromAbove"), p + 1.0e-4, engine, limits)
          || !addLimit(function, x, pole, F.stringx("FromBelow"), p - 1.0e-4, engine, limits)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test if the function stays within the values of its critical points far out on one side, as
   * <code>Sin(x)*Cos(x)</code> does while its limit at infinity doesn't exist. A function like
   * <code>E^x*Sin(x)</code> leaves them.
   */
  private static boolean isBoundedOscillation(IExpr function, ISymbol x, double side, double yMin,
      double yMax, EvalEngine engine) {
    double tolerance = 1.0e-6 * Math.max(1.0, Math.max(Math.abs(yMin), Math.abs(yMax)));
    for (int k = 0; k <= 200; k++) {
      double value = sampleAt(function, x, side * (50.0 + 4.95 * k), engine);
      if (Double.isNaN(value) || value < yMin - tolerance || value > yMax + tolerance) {
        return false;
      }
    }
    return true;
  }

  /**
   * Add the limit of the function towards <code>point</code> (from the given
   * <code>direction</code>, or two sided for {@link F#NIL}) if the function is real at
   * <code>probe</code>, a point on that side.
   */
  private static boolean addLimit(IExpr function, ISymbol x, IExpr point, IExpr direction,
      double probe, EvalEngine engine, List<IExpr> limits) {
    if (Double.isNaN(sampleAt(function, x, probe, engine))) {
      // the function isn't real on that side
      return true;
    }
    try {
      IExpr limit = direction.isPresent()
          ? engine.evalQuiet(F.Limit(function, F.Rule(x, point), F.Rule(S.Direction, direction)))
          : engine.evalQuiet(F.Limit(function, F.Rule(x, point)));
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        limits.add(limit);
        return true;
      }
      if (limit.isRealResult() && !limit.isDirectedInfinity()) {
        double v = limit.evalfNaN();
        if (!Double.isNaN(v) && !Double.isInfinite(v)) {
          limits.add(limit);
          return true;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return false;
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
        || v == S.Indeterminate || v.isAST(S.Limit) || v.isAST(S.DirectedInfinity)) {
      return false;
    }
    return true;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}
