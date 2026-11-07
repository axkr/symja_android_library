package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** A number as the head of an expression is printed in parentheses if it has a sign or parts. */
public class NumberHeadFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testSignedNumberHead() {
    check("ReplacePart({u,v}, 0 -> -7)", //
        "(-7)[u,v]");
    check("ReplacePart({u,v}, 0 -> -1.25)", //
        "(-1.25)[u,v]");
    check("ReplacePart({u}, 0 -> -2^70)", //
        "(-1180591620717411303424)[u]");
    // a positive integer needs no parentheses
    check("ReplacePart({u,v}, 0 -> 7)", //
        "7[u,v]");
  }

  @Test
  public void testFractionAndComplexHead() {
    check("ReplacePart({u,v}, 0 -> 3/7)", //
        "(3/7)[u,v]");
    check("ReplacePart({u,v}, 0 -> -3/7)", //
        "(-3/7)[u,v]");
    check("ReplacePart({u,v}, 0 -> 2-5*I)", //
        "(2-I*5)[u,v]");
  }

  @Test
  public void testNumberHeadInputForm() {
    // the precision of a big float head is kept
    check("ToString(ReplacePart({u}, 0 -> -3`25), InputForm)", //
        "(-3`25)[u]");
    check("ToString(ReplacePart({u}, 0 -> 4+I), InputForm)", //
        "(4 + I)[u]");
  }
}
