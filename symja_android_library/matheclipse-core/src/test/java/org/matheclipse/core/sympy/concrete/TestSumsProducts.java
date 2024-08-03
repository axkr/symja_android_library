package org.matheclipse.core.sympy.concrete;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestSumsProducts extends ExprEvaluatorTestCase {

  @Test
  public void test_is_convergent() {
    ISymbol n = F.n;
    assertEquals(Summations.isConvergent(
        F.Sum(F.Divide(n, F.Plus(F.C1, F.Times(F.C2, n))), F.List(n, F.C1, F.oo))), false);
    assertEquals(Summations.isConvergent(
        F.Sum(F.Divide(F.Factorial(n), F.Power(F.C5, n)), F.List(n, F.C1, F.oo))), false);
    // assertEquals(F.Sum(3**(-2*n - 1)*n**n,F.List(n, F.C1,F.oo )).is_convergent() , false);
    // assertEquals(F.Sum((-1)**n*n, F.List(n, F.C3, F.oo )).is_convergent(), false);
    // assertEquals(F.Sum((-1)**n, F.List(n, F.C1,F.oo )).is_convergent(), false);
    // assertEquals(F.Sum(log(1/n), F.List(n, F.C1, F.oo )).is_convergent(), false);


    // # p-series test --
    assertEquals(Summations.isConvergent(
        F.Sum(F.Divide(F.C1, F.Plus(F.C1, F.Power(n, F.C2))), F.List(n, F.C1, F.oo))), true);

    // # comparison test --
    // assert Sum(1/(n + log(n)), (n, 1, oo)).is_convergent() is S.false
    assertEquals(
        Summations.isConvergent(
            F.Sum(F.Divide(F.C1, F.Plus(n, F.Log(n))), F.List(n, F.C1, F.oo))),
        false);

    // assert Sum(1/(n**2*log(n)), (n, 2, oo)).is_convergent() is S.true
    assertEquals(
        Summations.isConvergent(
            F.Sum(F.Divide(F.C1, F.Times(F.Log(n), F.Power(n, F.C2))), F.List(n, F.C2, F.oo))),
        true);
  }
}
