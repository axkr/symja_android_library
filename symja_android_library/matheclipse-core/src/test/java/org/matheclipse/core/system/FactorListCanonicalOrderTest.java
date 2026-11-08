package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The factors of <code>FactorList</code> after the numeric content are in canonical order. */
public class FactorListCanonicalOrderTest extends ExprEvaluatorTestCase {

  @Test
  public void testCanonicalOrder() {
    check("FactorList((7*x-3*x^2)^3)", //
        "{{-1,1},{x,3},{-7+3*x,3}}");
    check("FactorList(Expand((7*x-3*x^2)^3))", //
        "{{-1,1},{x,3},{-7+3*x,3}}");
    check("FactorList(x^2*(x-4))", //
        "{{1,1},{-4+x,1},{x,2}}");
    check("FactorList((3*x-2)*(x^2+5)^2)", //
        "{{1,1},{-2+3*x,1},{5+x^2,2}}");
    check("FactorList((x^3+2)*(x^2+x+3))", //
        "{{1,1},{3+x+x^2,1},{2+x^3,1}}");
  }
}
