package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Folds arithmetic over <code>Piecewise</code> expressions of one variable into one
 * <code>Piecewise</code>: <code>u^2*Piecewise({{1, 0&lt;=u&lt;1}}, 0)+4*Piecewise({{1,
 * 1&lt;=u&lt;2}}, 0)</code> is <code>Piecewise({{4, 1&lt;=u&lt;2}, {u^2, 0&lt;=u&lt;1}}, 0)</code>.
 *
 * <p>
 * Every condition must compare one variable with numbers. The real line is cut at their bounds into
 * points and open intervals; on each the expression is evaluated with every <code>Piecewise</code>
 * replaced by the value it selects there, and neighbouring stretches with the same value merge into
 * one case.
 */
public final class PiecewiseFold {

  /** The largest number of bounds which are searched. */
  private static final int MAX_BOUNDS = 64;

  private PiecewiseFold() {}

  /** One stretch of the real line: the point {@link #lower}, or the interval between its ends. */
  private static final class Region {
    /** The lower end, <code>null</code> for <code>-Infinity</code>. */
    final IExpr lower;
    /** The upper end, <code>null</code> for <code>Infinity</code>. */
    final IExpr upper;
    final boolean point;

    Region(IExpr lower, IExpr upper, boolean point) {
      this.lower = lower;
      this.upper = upper;
      this.point = point;
    }

    IExpr sample() {
      if (point) {
        return lower;
      }
      if (lower == null) {
        return upper == null ? F.C0 : F.Subtract(upper, F.C1);
      }
      return upper == null ? F.Plus(lower, F.C1) : F.Times(F.C1D2, F.Plus(lower, upper));
    }
  }

  /** What the comparisons of the conditions have in common. */
  private static final class Conditions {
    IExpr variable = null;
    final List<IExpr> bounds = new ArrayList<IExpr>();

    boolean operand(IExpr operand) {
      if (operand.isReal() || (operand.isNumericFunction() && !operand.isVariable()
          && !Double.isNaN(operand.evalfNaN()))) {
        bounds.add(operand);
        return true;
      }
      if (!operand.isVariable() || (variable != null && !variable.equals(operand))) {
        return false;
      }
      variable = operand;
      return true;
    }

    /** Collect the variable and the bounds, or return <code>false</code> for another shape. */
    boolean collect(IExpr condition) {
      if (condition.isTrue() || condition.isFalse()) {
        return true;
      }
      if (condition.isAST(S.Inequality)) {
        IAST inequality = (IAST) condition;
        for (int i = 1; i < inequality.size(); i += 2) {
          if (!operand(inequality.get(i))) {
            return false;
          }
        }
        return true;
      }
      if (condition.isAST(S.And) || condition.isAST(S.Or) || condition.isAST(S.Not)) {
        return ((IAST) condition).forAll(this::collect);
      }
      if (condition.isAST(S.Less) || condition.isAST(S.LessEqual) || condition.isAST(S.Greater)
          || condition.isAST(S.GreaterEqual) || condition.isAST(S.Equal)
          || condition.isAST(S.Unequal)) {
        return ((IAST) condition).forAll(this::operand);
      }
      return false;
    }
  }

  /** Collect the <code>Piecewise</code> parts in the arithmetic of <code>expr</code>. */
  private static void collectPiecewise(IExpr expr, List<IAST> parts) {
    if (expr.isAST(S.Piecewise)) {
      if (!parts.contains(expr)) {
        parts.add((IAST) expr);
      }
    } else if (expr.isPlus() || expr.isTimes() || expr.isPower()) {
      for (IExpr arg : (IAST) expr) {
        collectPiecewise(arg, parts);
      }
    }
  }

  private static IExpr defaultValue(IAST piecewise) {
    return piecewise.argSize() >= 2 ? piecewise.arg2() : F.C0;
  }

  /** The value of <code>piecewise</code> at <code>at</code>, or {@link F#NIL} if it is unknown. */
  private static IExpr select(IAST piecewise, IExpr variable, IExpr at, EvalEngine engine) {
    for (IExpr piece : (IAST) piecewise.arg1()) {
      IExpr truth = engine.evalQuiet(F.subst(piece.second(), variable, at));
      if (truth.isTrue()) {
        return piece.first();
      }
      if (!truth.isFalse()) {
        return F.NIL;
      }
    }
    return defaultValue(piecewise);
  }

  /** The condition of the stretch from <code>first</code> to <code>last</code>. */
  private static IExpr condition(IExpr x, Region first, Region last) {
    if (first == last && first.point) {
      return F.Equal(x, first.lower);
    }
    ISymbol lowerRelation = first.point ? S.LessEqual : S.Less;
    ISymbol upperRelation = last.point ? S.LessEqual : S.Less;
    IExpr upper = last.point ? last.lower : last.upper;
    if (first.lower == null) {
      return upper == null ? S.True : F.binaryAST2(upperRelation, x, upper);
    }
    if (upper == null) {
      return F.binaryAST2(first.point ? S.GreaterEqual : S.Greater, x, first.lower);
    }
    return F.Inequality(first.lower, lowerRelation, x, upperRelation, upper);
  }

  /**
   * The expression with its <code>Piecewise</code> parts folded into one.
   *
   * @param simplify if <code>true</code> the values are simplified
   * @return {@link F#NIL} if <code>expr</code> is a <code>Piecewise</code> itself, has none in its
   *         arithmetic or a condition isn't a comparison of one variable with numbers
   */
  public static IExpr fold(IExpr expr, boolean simplify, EvalEngine engine) {
    if (!(expr.isPlus() || expr.isTimes() || expr.isPower()) || expr.isFree(S.Piecewise, true)) {
      return F.NIL;
    }
    List<IAST> parts = new ArrayList<IAST>();
    collectPiecewise(expr, parts);
    Conditions conditions = new Conditions();
    for (IAST piecewise : parts) {
      if (piecewise.argSize() > 2 || !piecewise.arg1().isListOfLists()) {
        return F.NIL;
      }
      for (IExpr piece : (IAST) piecewise.arg1()) {
        if (piece.argSize() != 2 || !conditions.collect(piece.second())) {
          return F.NIL;
        }
      }
    }
    if (conditions.variable == null) {
      return F.NIL;
    }
    List<Region> regions = regions(conditions.bounds);
    if (regions == null) {
      return F.NIL;
    }
    IExpr x = conditions.variable;
    IExpr[] values = new IExpr[regions.size()];
    for (int i = 0; i < values.length; i++) {
      engine.checkTimeLimit();
      IExpr at = engine.evaluate(regions.get(i).sample());
      IExpr value = expr;
      for (IAST piecewise : parts) {
        IExpr selected = select(piecewise, x, at, engine);
        if (selected.isNIL()) {
          return F.NIL;
        }
        value = F.subst(value, piecewise, selected);
      }
      values[i] = evaluateValue(value, simplify, engine);
      if (!values[i].isFree(S.Piecewise, true)) {
        return F.NIL;
      }
    }
    IExpr defaults = expr;
    for (IAST piecewise : parts) {
      defaults = F.subst(defaults, piecewise, defaultValue(piecewise));
    }
    final IExpr defaultValue = evaluateValue(defaults, simplify, engine);

    List<IAST> cases = new ArrayList<IAST>();
    int start = 0;
    while (start < values.length) {
      int end = start;
      while (end + 1 < values.length && values[end + 1].equals(values[start])) {
        end++;
      }
      if (!values[start].equals(defaultValue)) {
        cases.add(F.list(values[start], condition(x, regions.get(start), regions.get(end))));
      }
      start = end + 1;
    }
    if (cases.isEmpty()) {
      return defaultValue;
    }
    cases.sort((a, b) -> a.first().compareTo(b.first()));
    IASTAppendable list = F.ListAlloc(cases.size());
    cases.forEach(list::append);
    return engine.evaluate(F.Piecewise(list, defaultValue));
  }

  private static IExpr evaluateValue(IExpr value, boolean simplify, EvalEngine engine) {
    return engine.evaluate(simplify ? F.Simplify(value) : value);
  }

  /** The stretches of the real line between the sorted distinct <code>bounds</code>. */
  private static List<Region> regions(List<IExpr> bounds) {
    List<IExpr> sorted = new ArrayList<IExpr>(bounds);
    sorted.sort((a, b) -> Double.compare(a.evalfNaN(), b.evalfNaN()));
    List<IExpr> distinct = new ArrayList<IExpr>();
    for (IExpr bound : sorted) {
      if (distinct.isEmpty()
          || distinct.get(distinct.size() - 1).evalfNaN() != bound.evalfNaN()) {
        distinct.add(bound);
      }
    }
    if (distinct.isEmpty() || distinct.size() > MAX_BOUNDS) {
      return null;
    }
    List<Region> result = new ArrayList<Region>(2 * distinct.size() + 1);
    result.add(new Region(null, distinct.get(0), false));
    for (int i = 0; i < distinct.size(); i++) {
      IExpr next = i + 1 < distinct.size() ? distinct.get(i + 1) : null;
      result.add(new Region(distinct.get(i), distinct.get(i), true));
      result.add(new Region(distinct.get(i), next, false));
    }
    return result;
  }
}
