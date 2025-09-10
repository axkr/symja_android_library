package org.matheclipse.core.interfaces.statistics;

import java.util.Random;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Capability to produce random variate. */
public interface IRandomVariate {
  /**
   * @param distribution the distribution
   * @param size the size of the sample
   * @return sample generated using the given random generator
   */
  public IExpr randomVariate(Random random, IAST distribution, int size);
}

