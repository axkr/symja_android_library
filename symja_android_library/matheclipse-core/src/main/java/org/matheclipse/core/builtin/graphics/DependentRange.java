package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A range of a plot whose bounds depend on the variable of an earlier range, as the second one of
 * <code>ContourPlot(f, {x, 0, 20}, {y, -10, 0.99*x})</code>.
 *
 * <p>
 * Such a range is plotted over the numbers its bounds reach, and the bounds become a region: a
 * point belongs to the picture where <code>lo &lt;= v &lt;= hi</code> holds. The region is handed
 * to the plot as its <code>RegionFunction</code> - or, for a plot of a predicate, as a part of the
 * predicate - so that the plot draws the edge the way it draws any other region.
 */
public final class DependentRange {

  /** A plot which takes no range that depends on another one. */
  public static final int UNSUPPORTED = -2;

  /** A plot of a predicate: the bounds are joined to its first argument. */
  public static final int PREDICATE = -1;

  /** The number of values of an earlier variable at which a bound is evaluated. */
  private static final int SAMPLES = 25;

  private DependentRange() {}

  /**
   * @param call the plot with evaluated arguments
   * @param regionOffset the number of arguments of the plot's <code>RegionFunction</code> which
   *        come before the variables of the ranges, or {@link #PREDICATE}
   * @return <code>call</code> itself if it has no dependent range, or one which cannot be read
   */
  public static IAST rewrite(IAST call, int regionOffset, EvalEngine engine) {
    List<Integer> positions = new ArrayList<>();
    for (int i = 2; i < call.size(); i++) {
      IExpr arg = call.get(i);
      if (arg.isList3() && arg.first().isSymbol()) {
        positions.add(i);
      } else if (!positions.isEmpty()) {
        break;
      }
    }
    if (positions.size() < 2) {
      return call;
    }
    int n = positions.size();
    ISymbol[] variables = new ISymbol[n];
    double[][] bounds = new double[n][];
    IExpr condition = F.NIL;
    IAST[] iterators = new IAST[n];
    for (int k = 0; k < n; k++) {
      IAST iterator = (IAST) call.get(positions.get(k));
      iterators[k] = iterator;
      variables[k] = (ISymbol) iterator.arg1();
      IExpr lo = iterator.arg2();
      IExpr hi = iterator.arg3();
      boolean dependent = false;
      for (int j = 0; j < k; j++) {
        dependent |= !lo.isFree(variables[j]) || !hi.isFree(variables[j]);
      }
      if (!dependent) {
        try {
          bounds[k] = new double[] {engine.evalDouble(lo), engine.evalDouble(hi)};
        } catch (RuntimeException rex) {
          return call;
        }
        continue;
      }
      bounds[k] = reach(lo, hi, variables, bounds, k, engine);
      if (bounds[k] == null) {
        return call;
      }
      IExpr between = F.And(F.LessEqual(lo, variables[k]), F.LessEqual(variables[k], hi));
      condition = condition.isPresent() ? F.And(condition, between) : between;
      iterators[k] = F.list(variables[k], F.num(bounds[k][0]), F.num(bounds[k][1]));
    }
    if (condition.isNIL()) {
      return call;
    }

    IASTAppendable result = F.ast(call.head(), call.argSize() + 1);
    IExpr region = F.NIL;
    for (int i = 1; i < call.size(); i++) {
      IExpr arg = call.get(i);
      int k = positions.indexOf(i);
      if (k >= 0) {
        result.append(iterators[k]);
      } else if (regionOffset != PREDICATE && arg.isRuleAST() && arg.first() == S.RegionFunction) {
        region = arg.second();
      } else {
        result.append(arg);
      }
    }
    if (regionOffset == PREDICATE) {
      result.set(1, F.And(result.arg1(), condition));
      return result;
    }
    // the condition in the slots the plot hands the variables over in
    IExpr slots = condition;
    for (int k = 0; k < n; k++) {
      slots = F.subst(slots, F.Rule(variables[k], F.Slot(regionOffset + k + 1)));
    }
    if (region.isPresent() && !region.isAutomatic() && !region.isTrue()) {
      slots = F.And(slots, F.unaryAST1(region, F.SlotSequence(1)));
    }
    result.append(F.Rule(S.RegionFunction, F.Function(slots)));
    return result;
  }

  /**
   * The smallest lower and the largest upper bound of the range <code>k</code> over the values of
   * the earlier variables.
   *
   * @return <code>null</code> if a bound is no number somewhere, or the range is empty everywhere
   */
  private static double[] reach(IExpr lo, IExpr hi, ISymbol[] variables, double[][] bounds, int k,
      EvalEngine engine) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    int[] index = new int[k];
    while (true) {
      IASTAppendable rules = F.ListAlloc(k);
      for (int j = 0; j < k; j++) {
        double t = (double) index[j] / (SAMPLES - 1);
        rules.append(F.Rule(variables[j], F.num(bounds[j][0] + t * (bounds[j][1] - bounds[j][0]))));
      }
      try {
        double a = engine.evalDouble(F.subst(lo, rules));
        double b = engine.evalDouble(F.subst(hi, rules));
        if (!Double.isFinite(a) || !Double.isFinite(b)) {
          return null;
        }
        if (a <= b) {
          min = Math.min(min, a);
          max = Math.max(max, b);
        }
      } catch (RuntimeException rex) {
        return null;
      }
      int j = 0;
      while (j < k && ++index[j] == SAMPLES) {
        index[j++] = 0;
      }
      if (j == k) {
        break;
      }
    }
    return min < max ? new double[] {min, max} : null;
  }
}
