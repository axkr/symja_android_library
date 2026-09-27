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
 * simulated: <code>"PathCount"</code>, <code>"Times"</code>, <code>"Values"</code> (or
 * <code>"States"</code>), <code>"Paths"</code>, <code>"Path"</code>, <code>"PathLength"</code>,
 * <code>"FirstTime"</code>, <code>"LastTime"</code>, <code>"FirstValue"</code> and
 * <code>"LastValue"</code>. The values of one path are a list, of several paths a list of lists.
 *
 * <p>
 * The object's layout, <code>TemporalData(Automatic, {{path1, ...}, {{tmin, tmax,
 * dt}}, pathCount, {"Continuous", 1}, {"Continuous", 1}, 1, {options}}, False, 15.0)</code>, with a
 * path <code>{x1, x2, ...}</code>.
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

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // td(property), where td = TemporalData(Automatic, {...}, False, version)
    IExpr head = ast.head();
    if (!head.isAST(S.TemporalData, 5) || !ast.isAST1() || !ast.arg1().isString()) {
      return F.NIL;
    }
    IExpr data = head.second();
    if (!data.isList() || data.argSize() < 3 || !data.first().isList() || !data.second().isList()
        || !data.second().first().isList3()) {
      return F.NIL;
    }
    IAST paths = (IAST) data.first();
    if (!paths.forAll(x -> x.isList())) {
      return F.NIL;
    }
    IAST timeSpec = (IAST) data.second().first();
    int pathCount = paths.argSize();
    IAST times = times(timeSpec, engine);
    if (times.isNIL()) {
      return F.NIL;
    }
    String property = ast.arg1().toString();
    switch (property) {
      case "Properties":
        return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
      case "PathCount":
        return F.ZZ(pathCount);
      case "PathLength":
        return F.ZZ(times.argSize());
      case "Times":
        return perPath(pathCount, i -> times);
      case "Values":
      case "States":
        return perPath(pathCount, i -> values(paths, i));
      case "Paths":
        return F.mapRange(1, pathCount + 1, i -> path(times, values(paths, i)));
      case "Path":
        return perPath(pathCount, i -> path(times, values(paths, i)));
      case "FirstTime":
        return times.arg1();
      case "LastTime":
        return times.last();
      case "FirstValue":
        return perPath(pathCount, i -> values(paths, i).arg1());
      case "LastValue":
        return perPath(pathCount, i -> values(paths, i).last());
      default:
        return F.Missing(S.NotAvailable, ast.arg1());
    }
  }

  /** The property of the one path, or the list of the property of every path. */
  private static IExpr perPath(int pathCount, java.util.function.IntFunction<IExpr> property) {
    if (pathCount == 1) {
      return property.apply(1);
    }
    return F.mapRange(1, pathCount + 1, property);
  }

  /** The values of the <code>i</code>-th path. */
  private static IAST values(IAST paths, int i) {
    return (IAST) paths.get(i);
  }

  private static IAST path(IAST times, IAST values) {
    return F.mapRange(1, Math.min(times.size(), values.size()),
        i -> F.List(times.get(i), values.get(i)));
  }

  /** The times <code>tmin, tmin + dt, ..., tmax</code>. */
  private static IAST times(IAST timeSpec, EvalEngine engine) {
    IExpr steps = engine
        .evaluate(F.Round(F.Divide(F.Subtract(timeSpec.arg2(), timeSpec.arg1()), timeSpec.arg3())));
    int n = steps.toIntDefault();
    if (n < 0) {
      return F.NIL;
    }
    return F.mapRange(0, n + 1,
        i -> engine.evaluate(F.Plus(timeSpec.arg1(), F.Times(F.ZZ(i), timeSpec.arg3()))));
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
