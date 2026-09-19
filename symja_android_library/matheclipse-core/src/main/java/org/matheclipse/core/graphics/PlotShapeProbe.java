package org.matheclipse.core.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Splits a plot's functions into curves by the shape of a sampled value rather than by how they
 * are written.
 *
 * <p>
 * The written form cannot always say how many curves there are. {@code Plot(f(x), ...)} with
 * {@code f(u_?NumericQ) := {Sin(u), Cos(u)}} is two curves, and {@code {g(t), h(t), k(t)}} in
 * {@code ParametricPlot3D} is three curves when each item gives a point, but one curve's three
 * components when each gives a number. Only a value tells them apart, so each function is evaluated
 * at a probe point and read by the depth of what comes back.
 *
 * <p>
 * The reading follows Mathematica, measured 2026-09-14: once one entry of a sampled list is a
 * curve, the list is a list of curves, and an entry that gives no value at all is still a curve of
 * its own, one that draws nothing ({@code {p(t), undefined(t)}} is one line, {@code {p(t),
 * undefined(t), p(2 t)}} two). Where a curve is a point of several coordinates, an entry that is a
 * number reads as a coordinate instead, so that level is no list of curves.
 */
public final class PlotShapeProbe {

  /** A leaf of one number, the value of a {@code Plot}, {@code Plot3D} or contour function. */
  /**
   * Whether a curve specification asks for nothing to be drawn.
   *
   * <p>
   * An empty list is no curve - <code>ParametricPlot3D[{If[cond, curves, {}], ...}]</code> is how a
   * picture is made to drop a curve when a control says so - and a list of nothing but those is no
   * curve either. It is worth telling apart from a curve that could not be sampled: one is an empty
   * picture, the other a call that could not be read.
   */
  public static boolean isNoCurve(IExpr spec) {
    if (!spec.isList()) {
      return false;
    }
    IAST list = (IAST) spec;
    if (list.argSize() == 0) {
      return true;
    }
    return list.forAll(x -> isNoCurve(x));
  }

  public static final int SCALAR = 1;

  /**
   * Where a continuous range is probed: away from the ends, where a function is most often
   * undefined, and at more than one place in case the first is a singular point.
   */
  private static final double[] PROBE_FRACTIONS = {0.5, 0.382, 0.618, 0.25, 0.75};

  private PlotShapeProbe() {}

  /**
   * Probe points spread over continuous ranges, one rule list per probe.
   *
   * @param vars the iteration variables
   * @param mins the lower end of each variable's range
   * @param maxs the upper end of each variable's range
   * @return the probes, empty when some end is not a finite number
   */
  public static List<IAST> rangeProbes(IExpr[] vars, double[] mins, double[] maxs) {
    List<IAST> probes = new ArrayList<>(PROBE_FRACTIONS.length);
    for (int i = 0; i < vars.length; i++) {
      if (!Double.isFinite(mins[i]) || !Double.isFinite(maxs[i])) {
        return probes;
      }
    }
    for (double fraction : PROBE_FRACTIONS) {
      IASTAppendable rules = F.ListAlloc(vars.length);
      for (int i = 0; i < vars.length; i++) {
        rules.append(F.Rule(vars[i], F.num(mins[i] + fraction * (maxs[i] - mins[i]))));
      }
      probes.add(rules);
    }
    return probes;
  }

  /**
   * Probe points taken from discrete iterators such as {@code {n, 1, 10}} or {@code {n, {1, 3,
   * 7}}}, so a function defined only on the iterator's own values (an integer argument, say) is
   * probed where it is defined.
   *
   * @return the probes, empty when an iterator's values cannot be read
   */
  public static List<IAST> iteratorProbes(IAST[] iterators, EvalEngine engine) {
    List<List<IExpr>> valuesPerIterator = new ArrayList<>(iterators.length);
    int count = Integer.MAX_VALUE;
    for (IAST iterator : iterators) {
      List<IExpr> values = iteratorValues(iterator, engine);
      if (values.isEmpty()) {
        return new ArrayList<>();
      }
      valuesPerIterator.add(values);
      count = Math.min(count, values.size());
    }
    List<IAST> probes = new ArrayList<>(count);
    for (int k = 0; k < count; k++) {
      IASTAppendable rules = F.ListAlloc(iterators.length);
      for (int i = 0; i < iterators.length; i++) {
        rules.append(F.Rule(iterators[i].arg1(), valuesPerIterator.get(i).get(k)));
      }
      probes.add(rules);
    }
    return probes;
  }

  /** The first, second and last values an iterator steps through. */
  private static List<IExpr> iteratorValues(IAST iterator, EvalEngine engine) {
    List<IExpr> values = new ArrayList<>(3);
    if (!iterator.isList() || iterator.argSize() < 2) {
      return values;
    }
    if (iterator.argSize() == 2 && iterator.arg2().isList()) {
      IAST explicit = (IAST) iterator.arg2();
      for (int i = 1; i <= Math.min(3, explicit.argSize()); i++) {
        values.add(explicit.get(i));
      }
      return values;
    }
    IExpr min = iterator.argSize() == 2 ? F.C1 : iterator.arg2();
    IExpr max = iterator.argSize() == 2 ? iterator.arg2() : iterator.arg3();
    IExpr step = iterator.argSize() >= 4 ? iterator.arg4() : F.C1;
    for (IExpr value : new IExpr[] {min, F.Plus(min, step), max}) {
      IExpr evaluated = engine.evaluate(value);
      if (evaluated.isNumber()) {
        values.add(evaluated);
      }
    }
    return values;
  }

  /**
   * The curves, with every function that evaluates to several curves taken as that many.
   *
   * <p>
   * A wrapped function keeps its wrapper whole, since its label belongs to all of it, and a
   * function no probe can read stays as it was written.
   *
   * @param functions the functions as the plot split them from its argument
   * @param probes rule lists to evaluate each function at, tried in order
   * @param dimension {@link #SCALAR} for a function of one value, otherwise the number of
   *        coordinates of one point
   * @param partLeaves whether a curve that is not written as a list of coordinates is rewritten as
   *        one, {@code {Part(f, 1), Part(f, 2)}}, for a plot that reads its coordinates one by one
   */
  public static List<IExpr> split(List<IExpr> functions, List<IAST> probes, int dimension,
      boolean partLeaves, EvalEngine engine) {
    List<IExpr> result = new ArrayList<>(functions.size());
    for (IExpr function : functions) {
      result.addAll(split(function, probes, dimension, partLeaves, engine));
    }
    return result;
  }

  /** The curves one function stands for; the function itself when no probe tells them apart. */
  public static List<IExpr> split(IExpr function, List<IAST> probes, int dimension,
      boolean partLeaves, EvalEngine engine) {
    List<IExpr> curves = new ArrayList<>();
    if (!PlotWrapper.isWrapper(function)) {
      for (IAST rules : probes) {
        IExpr value = probe(function, rules, engine);
        if (value.isPresent() && curvesOfValue(function, value, dimension, partLeaves, curves)) {
          return curves;
        }
        curves.clear();
      }
    }
    curves.add(function);
    return curves;
  }

  /** The function's value at one probe, or {@link F#NIL}; its messages are not the user's. */
  private static IExpr probe(IExpr function, IAST rules, EvalEngine engine) {
    boolean quiet = engine.isQuietMode();
    engine.setQuietMode(true);
    try {
      return engine.evaluate(F.subst(function, rules));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /**
   * Read the curves {@code expr} stands for off its sampled {@code value}.
   *
   * <p>
   * Each level of the value is matched by an item of {@code expr} where {@code expr} is itself a
   * list of the same length, so {@code {g(t), m(t)}} keeps {@code g(t)} as written and evaluates
   * only it for its curve; where it is not, the level is reached with {@code Part}.
   *
   * @return {@code false} when the value is neither a leaf nor a list holding a curve
   */
  private static boolean curvesOfValue(IExpr expr, IExpr value, int dimension,
      boolean partLeaves, List<IExpr> out) {
    if (isLeaf(value, dimension)) {
      out.add(partLeaves ? coordinates(expr, dimension) : expr);
      return true;
    }
    if (!value.isList() || ((IAST) value).argSize() == 0) {
      return false;
    }
    IAST values = (IAST) value;
    boolean itemwise = expr.isList() && ((IAST) expr).argSize() == values.argSize();
    List<IExpr> level = new ArrayList<>(values.argSize());
    boolean anyCurve = false;
    for (int j = 1; j <= values.argSize(); j++) {
      IExpr part = itemwise ? ((IAST) expr).get(j) : F.Part(expr, F.ZZ(j));
      List<IExpr> curves = new ArrayList<>();
      if (curvesOfValue(part, values.get(j), dimension, partLeaves, curves)) {
        level.addAll(curves);
        anyCurve = true;
      } else if (dimension > SCALAR && Double.isFinite(values.get(j).evalfNaN())) {
        // a number among points is a coordinate, and this level one malformed point
        return false;
      } else {
        // a curve with no value here, which draws nothing, or nothing where it is undefined
        level.add(part);
      }
    }
    if (!anyCurve) {
      return false;
    }
    out.addAll(level);
    return true;
  }

  private static boolean isLeaf(IExpr value, int dimension) {
    if (dimension == SCALAR) {
      return !value.isList() && Double.isFinite(value.evalfNaN());
    }
    if (!value.isList() || ((IAST) value).argSize() != dimension) {
      return false;
    }
    return ((IAST) value).forAll(x -> Double.isFinite(x.evalfNaN()));
  }

  /** {@code expr} as a list of its coordinates, reaching each with {@code Part} if need be. */
  private static IExpr coordinates(IExpr expr, int dimension) {
    if (dimension == SCALAR || (expr.isList() && ((IAST) expr).argSize() == dimension)) {
      return expr;
    }
    IASTAppendable list = F.ListAlloc(dimension);
    for (int i = 1; i <= dimension; i++) {
      list.append(F.Part(expr, F.ZZ(i)));
    }
    return list;
  }
}
