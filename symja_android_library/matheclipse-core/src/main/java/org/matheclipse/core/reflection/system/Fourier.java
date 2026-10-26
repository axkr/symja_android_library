package org.matheclipse.core.reflection.system;

/**
 * <p>
 * Implement the Fourier function.
 * </p>
 * * *
 * 
 * <pre>
 * Fourier(list)
 * Fourier(list, {p1, p2, ...})
 * </pre>
 * <p>
 * Options: FourierParameters -> {a, b} (default {0, 1})
 * </p>
 */
public class Fourier extends AbstractFourier {

  public Fourier() {}

  @Override
  protected int direction() {
    return 1;
  }
}
