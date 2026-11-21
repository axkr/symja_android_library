package org.matheclipse.core.expression.data;

import org.hipparchus.ode.DenseOutputModel;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.sampling.ODEStateInterpolator;

/**
 * A {@link DenseOutputModel} which also remembers the times its steps ended at, the points the
 * <code>"Grid"</code> property of an interpolating function lists. The model it extends keeps the
 * steps but does not hand out their times.
 */
public class StepTimesOutputModel extends DenseOutputModel {
  private static final long serialVersionUID = 4610941209935873461L;

  /** The start time and the end time of every step, in the order they were taken. */
  private double[] times = new double[16];
  private int size = 0;

  private void add(double t) {
    if (size == times.length) {
      times = java.util.Arrays.copyOf(times, 2 * size);
    }
    times[size++] = t;
  }

  @Override
  public void init(ODEStateAndDerivative initialState, double finalTime) {
    super.init(initialState, finalTime);
    size = 0;
    add(initialState.getTime());
  }

  @Override
  public void handleStep(ODEStateInterpolator interpolator) {
    super.handleStep(interpolator);
    if (size == 0) {
      add(interpolator.getPreviousState().getTime());
    }
    add(interpolator.getCurrentState().getTime());
  }

  @Override
  public void append(DenseOutputModel model) {
    super.append(model);
    if (model instanceof StepTimesOutputModel) {
      double[] other = ((StepTimesOutputModel) model).getTimes();
      for (int i = 0; i < other.length; i++) {
        // the start of the appended model is the end of this one
        if (i == 0 && size > 0 && other[0] == times[size - 1]) {
          continue;
        }
        add(other[i]);
      }
    }
  }

  /** The times the steps started and ended at, in the order of the integration. */
  public double[] getTimes() {
    return java.util.Arrays.copyOf(times, size);
  }
}
