package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>EvaluationMonitor :&gt; expr</code> of the functions which sample a function. */
public class EvaluationMonitorTest extends ExprEvaluatorTestCase {

  @Test
  public void testPlotMonitor() {
    check("Length(Reap(Plot(Sin(x), {x, 0, 1}, PlotPoints -> 9, EvaluationMonitor :> Sow(x)))[[2, 1]])", //
        "9");
    check("Min(Reap(Plot(x, {x, 0, 4}, EvaluationMonitor :> Sow(x)))[[2, 1]])*49*10^6", //
        "4.0");
  }

  @Test
  public void testNIntegrateMonitor() {
    // the value is the same with and without the monitor
    check("Reap(NIntegrate(x^3, {x, 0, 2}, EvaluationMonitor :> Sow(x)))[[1]]", //
        "4.0");
    // one 21 point Gauss-Kronrod rule integrates the cubic
    check("Length(Reap(NIntegrate(x^3, {x, 0, 2}, EvaluationMonitor :> Sow(x)))[[2, 1]])", //
        "21");
    check("Reap(NIntegrate(x^3, {x, 0, 2}, EvaluationMonitor :> Sow(x)))[[2, 1, 1]]", //
        "1.0");
    check("Reap(NIntegrate(Exp(-x), {x, 1, 4}, EvaluationMonitor :> Sow(x)))[[2, 1]] // "
        + "MinMax // Map(1 < # < 4&, #)&", //
        "{True,True}");
    // every piece of a path is monitored
    check("Union(Sign(Reap(NIntegrate(1/Sqrt(Abs(x)), {x, -1, 0, 1}, "
        + "EvaluationMonitor :> Sow(x)))[[2, 1]]))", //
        "{-1,1}");
  }

  @Test
  public void testNIntegrateMonitorTwoVariables() {
    // the innermost integral runs the monitor, when both variables have values
    check("Union(Map(NumberQ, Flatten(Reap(NIntegrate(u + v, {u, 0, 2}, {v, 0, 1}, "
        + "EvaluationMonitor :> Sow({u, v})))[[2, 1]])))", //
        "{True}");
  }
}
