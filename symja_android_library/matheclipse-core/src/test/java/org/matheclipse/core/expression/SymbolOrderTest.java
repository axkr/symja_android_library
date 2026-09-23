package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Two different symbols of one name - a dummy a solver made beside the user's symbol - are two
 * variables, and have to be ordered as two: sorted as equal, <code>a*b</code> and
 * <code>b*a</code> came out in either order and their difference did not cancel.
 */
class SymbolOrderTest {

  @BeforeAll
  static void setUp() {
    F.initSymbols();
  }

  @Test
  void sameNameSymbolsAreOrderedConsistently() {
    ISymbol user = F.symbol("Y");
    ISymbol dummy = F.Dummy("Y");
    ISymbol dummy2 = F.Dummy("Y");
    assertNotEquals(0, user.compareTo(dummy));
    assertEquals(-Integer.signum(dummy.compareTo(user)), Integer.signum(user.compareTo(dummy)));
    assertNotEquals(0, dummy.compareTo(dummy2));
    assertEquals(-Integer.signum(dummy2.compareTo(dummy)), Integer.signum(dummy.compareTo(dummy2)));
    assertEquals(0, dummy.compareTo(dummy));
  }

  @Test
  void productsOfSameNameSymbolsCancel() {
    EvalEngine engine = new EvalEngine(false);
    ISymbol user = F.symbol("Y");
    ISymbol dummy = F.Dummy("Y");
    IExpr difference =
        engine.evaluate(F.Subtract(F.Times(dummy, user), F.Times(user, dummy)));
    assertTrue(difference.isZero(), difference.toString());
    IExpr sum = engine.evaluate(F.Plus(F.Times(F.CN1, dummy, user), F.Times(user, dummy)));
    assertTrue(sum.isZero(), sum.toString());
  }
}
