package org.matheclipse.core.reflection.system;

import java.util.Random;
import org.matheclipse.core.builtin.StochasticProcesses;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>RandomFunction(proc, {tmin, tmax, dt})</code> - a simulated path of the random process
 * <code>proc</code> at the times <code>tmin, tmin + dt, ..., tmax</code>, as a
 * <code>TemporalData</code> object; <code>RandomFunction(proc, {tmin, tmax, dt}, n)</code> - n
 * paths.
 *
 * <p>
 * <code>{tmin, tmax}</code> steps by <code>1</code> for a discrete time process and by a hundredth
 * of the range for a continuous one; <code>{tmax}</code> starts at <code>0</code>. The processes
 * are <code>WienerProcess</code>, <code>OrnsteinUhlenbeckProcess</code> and
 * <code>ARMAProcess</code>, simulated by their exact transitions. <code>SeedRandom</code> makes the
 * paths repeatable.
 */
public class RandomFunction extends AbstractFunctionEvaluator {

  /** The number of points a path has at most. */
  private static final int MAX_POINTS = 10_000_000;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr process = ast.arg1();
    IExpr.COMPARE_TERNARY timeKind = StochasticProcesses.isDiscrete(process);
    if (timeKind == IExpr.COMPARE_TERNARY.UNDECIDABLE || !ast.arg2().isList()) {
      return F.NIL;
    }
    final boolean discrete = timeKind == IExpr.COMPARE_TERNARY.TRUE;
    IAST range = (IAST) ast.arg2();
    IExpr tmin;
    IExpr tmax;
    IExpr dt;
    if (range.isList1()) {
      tmin = F.C0;
      tmax = range.arg1();
      dt = discrete ? F.C1 : F.NIL;
    } else if (range.isList2() || range.isList3()) {
      tmin = range.arg1();
      tmax = range.arg2();
      dt = range.isList3() ? range.arg3() : discrete ? F.C1 : F.NIL;
    } else {
      return F.NIL;
    }
    if (dt.isNIL()) {
      dt = engine.evaluate(F.Divide(F.Subtract(tmax, tmin), F.ZZ(100)));
    }
    double min = tmin.evalfNaN();
    double max = tmax.evalfNaN();
    double step = dt.evalfNaN();
    if (!Double.isFinite(min) || !Double.isFinite(max) || !(step > 0.0) || max < min) {
      return F.NIL;
    }
    int pathCount = 1;
    if (ast.isAST3()) {
      pathCount = ast.arg3().toIntDefault();
      if (pathCount < 1) {
        return F.NIL;
      }
    }
    double count = Math.floor((max - min) / step + 1.0e-9) + 1.0;
    if (count * pathCount > MAX_POINTS) {
      return F.NIL;
    }
    int points = (int) count;
    double[] times = new double[points];
    for (int i = 0; i < points; i++) {
      times[i] = min + i * step;
    }
    Random random = engine.getRandom();
    IASTAppendable paths = F.ListAlloc(pathCount);
    for (int k = 0; k < pathCount; k++) {
      double[] values = StochasticProcesses.simulate((IAST) process, times, random);
      if (StochasticProcesses.isIntegerValued(process)) {
        paths.append(F.mapRange(0, values.length, i -> F.ZZ((long) values[i])));
      } else {
        paths.append(F.List(values));
      }
    }
    // the last time is tmax itself when the steps reach it
    IExpr last = Math.abs(times[points - 1] - max) <= 1.0e-9 * Math.max(1.0, Math.abs(max)) ? tmax
        : engine.evaluate(F.Plus(tmin, F.Times(F.ZZ(points - 1), dt)));
    IAST timeSpec = F.List(tmin, last, dt);
    return TemporalData.of(paths, timeSpec, pathCount, discrete);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
