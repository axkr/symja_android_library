package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Operands which the output form has to put in parentheses, or write as a call. */
public class OutputFormOperandsTest extends ExprEvaluatorTestCase {

  @Test
  public void testTwoWayRuleOperands() {
    // a rule binds looser than <->, and <-> groups to the right
    check("Hold(u <-> (v -> w))", //
        "Hold(u<->(v->w))");
    check("Hold((u -> v) <-> w)", //
        "Hold((u->v)<->w)");
    check("Hold((u <-> v) <-> w)", //
        "Hold((u<->v)<->w)");
    check("Hold(u <-> (v <-> w))", //
        "Hold(u<->v<->w)");
    check("Hold(u <-> (v /; w))", //
        "Hold(u<->v/;w)");
    check("FullForm(u <-> v <-> w)", //
        "TwoWayRule(u, TwoWayRule(v, w))");
  }

  @Test
  public void testRationalOfSymbols() {
    // only two integers make a number
    check("Rational(u, v*w)", //
        "Rational(u,v*w)");
    check("Rational(3, v)", //
        "Rational(3,v)");
    check("Hold(Rational(3, 7))", //
        "Hold(3/7)");
  }

  @Test
  public void testDerivativeOfASum() {
    check("Hold(Derivative(1)[u + v][w])", //
        "Hold((u+v)'[w])");
    check("Hold(Derivative(2)[u(v)][w])", //
        "Hold(u(v)''[w])");
  }
}
