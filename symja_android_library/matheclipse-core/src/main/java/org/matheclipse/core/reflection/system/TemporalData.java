package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>TemporalData(...)["property"]</code> - a property of the paths {@link RandomFunction}
 * simulated or of a {@link TimeSeries}: <code>"PathCount"</code>, <code>"Times"</code>,
 * <code>"Values"</code> (or <code>"States"</code>), <code>"Paths"</code>, <code>"Path"</code>,
 * <code>"PathLength"</code>, <code>"FirstTime"</code>, <code>"LastTime"</code>,
 * <code>"FirstValue"</code> and <code>"LastValue"</code>. The values of one path are a list, of
 * several paths a list of lists.
 *
 * <p>
 * The object's layout, <code>TemporalData(type, {{path1, ...}, {tspec1, ...}, pathCount,
 * {"Continuous", 1}, {"Continuous", 1}, 1, {options}}, False, version)</code>, with a path
 * <code>{x1, x2, ...}</code> and the times <code>tspec</code> of the paths, the equally spaced
 * times <code>{tmin, tmax, dt}</code> or the list of times <code>{{t1, t2, ...}}</code>. A single
 * time specification holds for all paths.
 */
public class TemporalData extends AbstractEvaluator {

  private static final String[] PROPERTIES = {"FirstTime", "FirstValue", "LastTime", "LastValue",
      "Path", "PathCount", "PathLength", "Paths", "States", "Times", "Values"};

  /** The <code>TemporalData</code> object of equally spaced paths. */
  static IAST of(IAST paths, IAST timeSpec, int pathCount, boolean discrete) {
    IAST kind = F.List(F.stringx(discrete ? "Discrete" : "Continuous"), F.C1);
    IAST options =
        F.List(F.Rule(F.symbol("ValueDimensions"), F.C1), F.Rule(F.symbol("ResamplingMethod"),
            F.List(F.stringx("Interpolation"), F.Rule(S.InterpolationOrder, F.C1))));
    IAST data = F.List(paths, F.List(timeSpec), F.ZZ(pathCount), kind, kind, F.C1, options);
    return F.function(S.TemporalData, S.Automatic, data, S.False, F.num(15.0));
  }

  /**
   * The paths of a <code>TemporalData</code> object at index 0 and their times at index 1, or
   * <code>null</code> if <code>temporalData</code> doesn't have the layout of one.
   */
  private static IAST[][] pathsAndTimes(IExpr temporalData, EvalEngine engine) {
    if (!temporalData.isAST(S.TemporalData, 5)) {
      return null;
    }
    IExpr data = temporalData.second();
    if (!data.isList() || data.argSize() < 3 || !data.first().isList() || !data.second().isList()
        || data.second().argSize() == 0) {
      return null;
    }
    IAST paths = (IAST) data.first();
    IAST timeSpecs = (IAST) data.second();
    final int pathCount = paths.argSize();
    if (timeSpecs.argSize() != 1 && timeSpecs.argSize() != pathCount) {
      return null;
    }
    IAST[][] result = new IAST[2][pathCount];
    IAST shared = timeSpecs.argSize() == 1 ? times(timeSpecs.arg1(), engine) : F.NIL;
    for (int i = 0; i < pathCount; i++) {
      if (!paths.get(i + 1).isList()) {
        return null;
      }
      result[0][i] = (IAST) paths.get(i + 1);
      result[1][i] = timeSpecs.argSize() == 1 ? shared : times(timeSpecs.get(i + 1), engine);
      if (result[1][i].isNIL() || result[1][i].argSize() != result[0][i].argSize()) {
        // a path needs one time for each of its values
        return null;
      }
    }
    return result;
  }

  /**
   * <code>Normal(td)</code>: the path <code>{{t1, x1}, {t2, x2}, ...}</code> of a
   * <code>TemporalData</code> object of one path, the list of the paths of one with several.
   *
   * @return {@link F#NIL} if <code>temporalData</code> doesn't have the layout of one
   */
  public static IExpr normal(IExpr temporalData, EvalEngine engine) {
    IAST[][] pathsAndTimes = pathsAndTimes(temporalData, engine);
    if (pathsAndTimes == null) {
      return F.NIL;
    }
    return perPath(pathsAndTimes[0].length,
        i -> path(pathsAndTimes[1][i - 1], pathsAndTimes[0][i - 1]));
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // td(property), where td = TemporalData(type, {...}, False, version)
    if (!ast.isAST1() || !ast.arg1().isString()) {
      return F.NIL;
    }
    IAST[][] pathsAndTimes = pathsAndTimes(ast.head(), engine);
    if (pathsAndTimes == null) {
      return F.NIL;
    }
    final IAST[] paths = pathsAndTimes[0];
    final IAST[] times = pathsAndTimes[1];
    final int pathCount = paths.length;
    String property = ast.arg1().toString();
    switch (property) {
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      case "PathCount":
        return F.ZZ(pathCount);
      case "PathLength": {
        // the length of all paths, or the list of the lengths of paths of different lengths
        IAST lengths = F.mapRange(1, pathCount + 1, i -> F.ZZ(times[i - 1].argSize()));
        return pathCount > 0 && lengths.forAll(x -> x.equals(lengths.arg1())) ? lengths.arg1()
            : lengths;
      }
      case "Times":
        return perPath(pathCount, i -> times[i - 1]);
      case "Values":
      case "States":
        return perPath(pathCount, i -> paths[i - 1]);
      case "Paths":
        return F.mapRange(1, pathCount + 1, i -> path(times[i - 1], paths[i - 1]));
      case "Path":
        return perPath(pathCount, i -> path(times[i - 1], paths[i - 1]));
      case "FirstTime":
        return extremeTime(times, true, engine);
      case "LastTime":
        return extremeTime(times, false, engine);
      case "FirstValue":
        return perPath(pathCount,
            i -> paths[i - 1].isEmpty() ? F.Missing(S.NotAvailable) : paths[i - 1].arg1());
      case "LastValue":
        return perPath(pathCount,
            i -> paths[i - 1].isEmpty() ? F.Missing(S.NotAvailable) : paths[i - 1].last());
      default:
        return F.Missing(S.NotAvailable, ast.arg1());
    }
  }

  /** The first time of all paths if <code>first</code>, else the last time of all paths. */
  private static IExpr extremeTime(IAST[] times, boolean first, EvalEngine engine) {
    IExpr result = F.NIL;
    for (IAST t : times) {
      if (t.isEmpty()) {
        continue;
      }
      IExpr candidate = first ? t.arg1() : t.last();
      if (result.isNIL() || (!result.equals(candidate)
          && engine.evalTrue(first ? F.Less(candidate, result) : F.Greater(candidate, result)))) {
        result = candidate;
      }
    }
    return result.isPresent() ? result : F.Missing(S.NotAvailable);
  }

  /** The property of the one path, or the list of the property of every path. */
  private static IExpr perPath(int pathCount, java.util.function.IntFunction<IExpr> property) {
    if (pathCount == 1) {
      return property.apply(1);
    }
    return F.mapRange(1, pathCount + 1, property);
  }

  private static IAST path(IAST times, IAST values) {
    return F.mapRange(1, Math.min(times.size(), values.size()),
        i -> F.List(times.get(i), values.get(i)));
  }

  /**
   * The times of a time specification: <code>tmin, tmin + dt, ..., tmax</code> of
   * <code>{tmin, tmax, dt}</code>, or the list of <code>{{t1, t2, ...}}</code>.
   *
   * @return {@link F#NIL} if <code>timeSpec</code> has neither form
   */
  static IAST times(IExpr timeSpec, EvalEngine engine) {
    if (timeSpec.isList1() && timeSpec.first().isList()) {
      return (IAST) timeSpec.first();
    }
    if (!timeSpec.isList3()) {
      return F.NIL;
    }
    IExpr steps = engine.evaluate(
        F.Round(F.Divide(F.Subtract(timeSpec.second(), timeSpec.first()), timeSpec.getAt(3))));
    int n = steps.toIntDefault();
    if (n < 0) {
      return F.NIL;
    }
    return F.mapRange(0, n + 1,
        i -> engine.evaluate(F.Plus(timeSpec.first(), F.Times(F.ZZ(i), timeSpec.getAt(3)))));
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
