package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for statistical moment functions */
public class EliminateTest extends ExprEvaluatorTestCase {

  @Test
  public void testEliminate() {
    check("Eliminate({f == x^5 + y^5, a == x + y, b == x*y}, {x, y})", //
        "-a^5+5*a^3*b-5*a*b^2+f==0");
    // check("Eliminate({a0*x^p+a1*x^q==0},x)", //
    // "(-a1)*x^q == a0*x^p");

    check("Eliminate({x^2 + y^2 + z^2 == 1, x - y + z == 2, x^3 - y^2 == z + 1}, {y, z})",
        "18*x-4*x^2+28*x^3-8*x^4-4*x^5-4*x^6==27");

    // print: Eliminate: y>2 is not a well-formed equation.
    check("Eliminate({x==y,y>2},{x})", //
        "Eliminate({x==y,y>2},{x})");

    check("Eliminate({(a*x + b)/(c*x + d)==y},x)", //
        "True");
    check("Eliminate({x == 2 + y, y == z}, y)", //
        "x-z==2");
    check("Eliminate({x == 2 + y, y == z}, {y,v})", //
        "x-z==2");
    check("Eliminate({2*x + 3*y + 4*z == 1, 9*x + 8*y + 7*z == 2}, z)", //
        "11/2*x+11/4*y==1/4");

    check("Eliminate({x == 2 + y^3, y^2 == z}, y)", //
        "x-z^(3/2)==2");

    // use evaluation step: Cos(ArcSin(y)) => Sqrt(1-y^2)
    check("Eliminate({Sin(x)==y, Cos(x) == z}, x)", //
        "Sqrt(1-y^2)-z==0");
    check("Eliminate({a^x==y, b^(2*x) == z}, x)", //
        "b^((2*Log(y))/Log(a))-z==0");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
