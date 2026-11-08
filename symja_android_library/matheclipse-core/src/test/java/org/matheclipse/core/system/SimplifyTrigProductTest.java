package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Simplify keeps a product of a number and a power of a circular or hyperbolic function on a tie
 * with its reduced form, and contracts a difference of tangents whose arguments differ by a
 * simpler expression.
 */
public class SimplifyTrigProductTest extends ExprEvaluatorTestCase {

  @Test
  public void testDoubleAngleProduct() {
    check("Simplify(Cot(y)*Sin(2*y))", //
        "2*Cos(y)^2");
    check("Simplify(Coth(y)*Sinh(2*y))", //
        "2*Cosh(y)^2");
    check("Simplify(3*Cos(y)^2)", //
        "3*Cos(y)^2");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testTangentDifference() {
    check("Simplify(Tan(y+2)-Tan(y)+Cos(y))", //
        "Cos(y)+Sec(y)*Sec(2+y)*Sin(2)");
    check("Simplify(Tanh(y+z)-Tanh(z))", //
        "Sech(z)*Sech(y+z)*Sinh(y)");
    check("Simplify(Tan(u)-Tan(v))", //
        "Tan(u)-Tan(v)");
  }

  @Test
  public void testHalfAngleSquare() {
    check("Simplify(5+5*Cos(2*y))", //
        "10*Cos(y)^2");
    check("Simplify(4-4*Cos(6*y))", //
        "8*Sin(3*y)^2");
    check("Simplify(2-2*Cosh(4*y))", //
        "-4*Sinh(2*y)^2");
  }
}
