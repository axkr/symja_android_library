package org.matheclipse.core.sympy.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestGruntz extends ExprEvaluatorTestCase {
  private Set<IExpr> mmrv(IExpr a, ISymbol b) {
    Gruntz.SubsSet subsSet = (Gruntz.SubsSet) Gruntz.mrv(a, b)[0];
    return subsSet.keySet().stream().collect(Collectors.toSet());
  }

  @Test
  public void testMrv1() {
    ISymbol x = F.Dummy("x");

    try {
      assertEquals(Set.of(x), mmrv(x, x));
      assertEquals(Set.of(x), mmrv(F.Plus(x, F.Power(x, -1)), x));
      assertEquals(Set.of(x), mmrv(F.Power(x, 2), x));
      assertEquals(Set.of(x), mmrv(F.Log(x), x));
      // assertEquals(Set.of(F.Exp(x)), mmrv(F.Exp(x), x));
       // assertEquals(Set.of(F.Exp(F.Negate(x))), mmrv(F.Exp(F.Negate(x)), x));
       // assertEquals(Set.of(F.Exp(F.Power(x, 2))), mmrv(F.Exp(F.Power(x, 2)), x));
       // assertEquals(Set.of(x), mmrv(F.Negate(F.Exp(F.Power(x, -1))), x));
       // assertEquals(Set.of(F.Exp(F.Plus(x, F.Power(x, -1)))),
       // mmrv(F.Exp(F.Plus(x, F.Power(x, -1))), x));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception: " + e);
    }
  }


}
