package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>TimeSeries({x1, x2, ...}, tspec)</code> - the time series of the values <code>xi</code>
 * at the times of <code>tspec</code>: <code>{tmin, tmax, dt}</code> for <code>tmin, tmin + dt,
 * ..., tmax</code>, <code>{tmin, tmax}</code> for equally spaced times from <code>tmin</code> to
 * <code>tmax</code>, <code>{tmin}</code> for the times <code>tmin, tmin + 1, ...</code> or
 * <code>{{t1, t2, ...}}</code> for the given times.
 *
 * <p>
 * <code>TimeSeries({{t1, x1}, {t2, x2}, ...})</code> - the time series of the values
 * <code>xi</code> at the times <code>ti</code>.
 *
 * <p>
 * The values of a time series are ordered by their times. A time series is a
 * {@link TemporalData} object of one path, whose properties like <code>"Path"</code>,
 * <code>"Times"</code> and <code>"Values"</code> it answers; <code>Normal</code> gives its path.
 */
public class TimeSeries extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1().normal(false);
    if (!arg1.isList()) {
      return F.NIL;
    }
    IAST values = (IAST) arg1;
    if (ast.isAST1()) {
      // {{t1, x1}, {t2, x2}, ...}
      if (values.isEmpty() || !values.forAll(x -> x.isList2())) {
        return F.NIL;
      }
      IAST times = values.map(x -> x.first(), 1);
      return explicitTimes(times, values.map(x -> x.second(), 1), engine);
    }
    final int n = values.argSize();
    IExpr timeSpec = ast.arg2();
    if (timeSpec.isList1() && timeSpec.first().isList()) {
      IAST times = (IAST) timeSpec.first();
      return times.argSize() == n ? explicitTimes(times, values, engine) : F.NIL;
    }
    if (n == 0 || !timeSpec.isList() || timeSpec.argSize() < 1 || timeSpec.argSize() > 3) {
      return F.NIL;
    }
    IExpr tmin = timeSpec.first();
    IExpr tmax;
    IExpr dt;
    if (timeSpec.argSize() == 1) {
      dt = F.C1;
      tmax = engine.evaluate(F.Plus(tmin, F.ZZ(n - 1)));
    } else {
      tmax = timeSpec.second();
      if (timeSpec.argSize() == 3) {
        dt = timeSpec.getAt(3);
      } else if (n > 1) {
        dt = engine.evaluate(F.Divide(F.Subtract(tmax, tmin), F.ZZ(n - 1)));
      } else {
        dt = F.C1;
      }
    }
    IAST regular = F.List(tmin, tmax, dt);
    IAST times = TemporalData.times(regular, engine);
    if (times.isNIL() || times.argSize() != n) {
      return F.NIL;
    }
    return temporalData(values, regular);
  }

  /**
   * The time series of the values at the given times, ordered by the times if they are real
   * numbers.
   */
  private static IExpr explicitTimes(IAST times, IAST values, EvalEngine engine) {
    final int n = times.argSize();
    double[] keys = new double[n];
    for (int i = 0; i < n; i++) {
      keys[i] = times.get(i + 1).isReal() ? times.get(i + 1).evalf() : Double.NaN;
      if (Double.isNaN(keys[i])) {
        // times which aren't real numbers keep their order
        return temporalData(values, F.List(times));
      }
    }
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      order[i] = i;
    }
    // a stable sort: equal times keep their order
    java.util.Arrays.sort(order, (a, b) -> Double.compare(keys[a], keys[b]));
    IASTAppendable sortedTimes = F.ListAlloc(n);
    IASTAppendable sortedValues = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      sortedTimes.append(times.get(order[i] + 1));
      sortedValues.append(values.get(order[i] + 1));
    }
    return temporalData(sortedValues, F.List(sortedTimes));
  }

  /** The <code>TemporalData</code> object of a time series of one path. */
  private static IAST temporalData(IAST values, IAST timeSpec) {
    IAST options = F.List(F.Rule(F.symbol("ResamplingMethod"),
        F.List(F.stringx("Interpolation"), F.Rule(S.InterpolationOrder, F.C1))));
    IAST data = F.List(F.List(values), F.List(timeSpec), F.C1,
        F.List(F.stringx("Continuous"), F.C1), F.List(F.stringx("Discrete"), F.C1), F.C1,
        options);
    return F.function(S.TemporalData, S.TimeSeries, data, S.False, F.num(15.0));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
