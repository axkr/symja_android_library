package org.matheclipse.core.builtin.graphics;

/**
 * <code>ProbabilityPlot(data)</code>, <code>ProbabilityPlot({data1, data2, ...})</code>,
 * <code>ProbabilityPlot(data, dist)</code> or <code>ProbabilityPlot(data, ref)</code> - the
 * cumulative probability <code>i/n</code> of the <code>i</code>-th of <code>n</code> sorted values
 * against the cumulative distribution function at that value of a normal distribution with the
 * mean and standard deviation of the data, of the distribution <code>dist</code> or of the
 * empirical distribution of the data <code>ref</code>, with the dashed diagonal of the unit square.
 */
public class ProbabilityPlot extends QuantilePlot {

  public ProbabilityPlot() {
    super(Kind.PROBABILITY);
  }
}
