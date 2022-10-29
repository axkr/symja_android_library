package org.matheclipse.core.sympy;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;

public class TestHyperbolic {
  @Test
  public void test_asinh_leading_term() {
    // https://github.com/sympy/sympy/blob/3cd03df7724a6a05405693cc0aaabaf5a780b844/sympy/functions/elementary/tests/test_hyperbolic.py#L572
    ISymbol x = F.x;
    // assertEquals(F.ArcSinh(x).asLeadingTerm(x, 1).toString(), //
    // "x");
    // assertEquals(F.ArcSinh(F.Plus(F.CI, x)).asLeadingTerm(x, 1).toString(), //
    // "1/2*I*Pi");


    // assert asinh(x).as_leading_term(x, cdir=1) == x
    // # Tests concerning boundary points lying on branch cuts
    // assert asinh(x + I).as_leading_term(x, cdir=1) == I*pi/2
    // assert asinh(x - I).as_leading_term(x, cdir=1) == -I*pi/2
    // assert asinh(1/x).as_leading_term(x, cdir=1) == -log(x) + log(2)
    // assert asinh(1/x).as_leading_term(x, cdir=-1) == log(x) - log(2) - I*pi
  }

}
