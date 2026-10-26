package org.matheclipse.core.reflection.system;

/**
 * <p>
 * Implement the InverseFourier function.
 * </p>
 * *
 * 
 * <pre>
 * InverseFourier(list)
 * InverseFourier(list, {p1, p2, ...})
 * </pre>
 * <p>
 * Options: FourierParameters -> {a, b} (default {0, 1})
 * </p>
 */
public class InverseFourier extends AbstractFourier {

  public InverseFourier() {}

  @Override
  protected int direction() {
    return -1;
  }
}
