package org.matheclipse.core.builtin.graphics;

/**
 * <code>ProbabilityScalePlot(data)</code>, <code>ProbabilityScalePlot({data1, data2, ...})</code>
 * or <code>ProbabilityScalePlot(data, "Normal")</code> - the sorted data on a normal probability
 * scale: the <code>i</code>-th of <code>n</code> values is drawn at the height of the standard
 * normal quantile of <code>(i - 0.3)/(n + 0.4)</code>, labelled in percent, with a dashed line
 * through the points of the two quartiles.
 */
public class ProbabilityScalePlot extends QuantilePlot {

  public ProbabilityScalePlot() {
    super(Kind.PROBABILITY_SCALE);
  }
}
