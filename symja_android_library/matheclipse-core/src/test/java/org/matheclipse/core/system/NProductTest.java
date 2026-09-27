package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for NProduct function */
public class NProductTest extends ExprEvaluatorTestCase {

  @Test
  public void testNProductFinite() {
    check("NProduct(k, {k, 1, 5})", //
        "120.0");
    check("NProduct(1+1/k, {k, 1, 10})", //
        "11.0");
    check("NProduct(k, {k, 3, 2})", //
        "1.0");
    check("NProduct(f(k), {k, 1, 3})", //
        "NProduct(f(k),{k,1,3})");
    check("NProduct(k, {2, 1, 3})", //
        "NProduct(k,{2,1,3})");
  }

  @Test
  public void testNProductInfinite() {
    // Sinh(Pi)/Pi
    check("NProduct(1+1/k^2, {k, 1, Infinity})", //
        "3.67607791037502");
    check("NProduct(1-1/k^2, {k, 2, Infinity})", //
        "0.5");
    // QPochhammer(-1/2,1/2)
    check("NProduct(1+1/2^k, {k, 1, Infinity})", //
        "2.38423");
    check("NProduct(Exp(1/k!), {k, 0, Infinity})", //
        "15.15426");
  }
}
