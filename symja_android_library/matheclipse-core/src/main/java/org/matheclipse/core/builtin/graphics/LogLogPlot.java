package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IExpr;

public class LogLogPlot extends LogLinearPlot {

  public LogLogPlot() {}

  /** Use the natural logarithm &quot;Log&quot; for the y-axis too. */
  @Override
  protected IExpr yScaling() {
    return F.stringx("Log");
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
