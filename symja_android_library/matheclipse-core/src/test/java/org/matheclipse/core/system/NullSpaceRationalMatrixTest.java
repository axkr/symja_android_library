package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The null space vectors of a matrix with fractions keep the free variable 1, those of an integer
 * matrix are cleared of denominators.
 */
public class NullSpaceRationalMatrixTest extends ExprEvaluatorTestCase {

  @Test
  public void testFractionMatrix() {
    check("NullSpace({{1/3,1/4}})", //
        "{{-3/4,1}}");
    check("NullSpace({{3,5,0},{0,0,1/3}})", //
        "{{-5/3,1,0}}");
    check("NullSpace({{1/2,1/3,1/5}})", //
        "{{-2/5,0,1},\n" //
            + " {-2/3,1,0}}");
  }

  @Test
  public void testIntegerMatrix() {
    check("NullSpace({{3,5}})", //
        "{{-5,3}}");
  }
}
