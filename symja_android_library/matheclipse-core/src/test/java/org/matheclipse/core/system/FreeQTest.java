package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** WMA's FreeQ sees the head of a complex or rational number, and takes a level specification. */
public class FreeQTest extends ExprEvaluatorTestCase {

  @Test
  public void testNumberAtomHeadsWMA() {
    // only FreeQ, only Complex and Rational, and not the parts of the number
    check("{FreeQ(1+2*I, Complex), FreeQ(1/2, Rational), FreeQ(1.5, Real), FreeQ(5, Integer), "
        + "FreeQ(\"ab\", String), FreeQ(1+2*I, 2), FreeQ(1/2, 2), FreeQ(1+2*I, _Integer), "
        + "FreeQ({1+2*I}, Complex), MemberQ({1+2*I}, Complex), Position(1+2*I, Complex), "
        + "Cases({1/2, x}, Rational, Infinity)}", //
        "{False,False,True,True,True,True,True,True,False,False,{},{}}");
    check("{FreeQ(1.5+2.1*I, Complex), FreeQ(x+y, Complex|Rational), FreeQ(x+1/2, Complex|Rational)}", //
        "{False,True,False}");
  }

  @Test
  public void testLevelSpecificationWMA() {
    check("{FreeQ(f(1/2), Rational, {1}), FreeQ(f(g(1/2)), Rational, {1}), "
        + "FreeQ(x+1/2, Rational, Heads->False)}", //
        "{False,True,True}");
    check("{FreeQ(f(x), x, {1}), FreeQ(f(g(x)), x, {1}), FreeQ(f(x), f, Heads->False), "
        + "FreeQ(f(g(x)), x, {2}, Heads->True)}", //
        "{False,True,True,False}");
    // an atom has only level 0, which is also its level -1
    check("{FreeQ(x, x, {1}), FreeQ(x, x, {0}), FreeQ(1/2, Rational, {1}), "
        + "FreeQ(1/2, Rational, {-1}), FreeQ(x, x, All), FreeQ(x, x, Infinity)}", //
        "{True,False,True,False,False,True}");
  }
}
