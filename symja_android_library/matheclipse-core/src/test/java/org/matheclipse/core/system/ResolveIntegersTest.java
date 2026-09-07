package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Resolve(..., Integers)</code>: quantifier elimination over the integers.
 *
 * <p>
 * These formulas are decided by Cooper's method, not by the real valued strategies which answer
 * every other domain. The difference is visible in both directions: <code>2*x == 1</code> has a
 * real solution but no integer one, and <code>Mod(x, 4) == 1 &amp;&amp; Mod(x, 6) == 2</code> is
 * inconsistent for reasons no continuum argument can see.
 */
public class ResolveIntegersTest extends ExprEvaluatorTestCase {

  @Test
  public void testExistsWithNoIntegerWitness() {
    check("Resolve(Exists(x, 2*x == 1), Integers)", //
        "False");
    check("Resolve(Exists(x, 2*x == 4), Integers)", //
        "True");
    // no integer lies strictly between two consecutive ones
    check("Resolve(Exists(x, x > 3 && x < 4), Integers)", //
        "False");
    check("Resolve(Exists(x, x >= 3 && x <= 3), Integers)", //
        "True");
  }

  @Test
  public void testInconsistentCongruences() {
    // one class is odd, the other even
    check("Resolve(Exists(x, Mod(x, 4) == 1 && Mod(x, 6) == 2), Integers)", //
        "False");
    check("Resolve(Exists(x, Mod(x, 4) == 1 && Mod(x, 6) == 3), Integers)", //
        "True");
  }

  @Test
  public void testUnboundedFormulasAreDecidedWithoutASearch() {
    check("Resolve(Exists(x, x > a && Mod(x, 2) == 0), Integers)", //
        "True");
    check("Resolve(ForAll(x, x <= 3 || x > 3), Integers)", //
        "True");
  }

  @Test
  public void testAlternatingQuantifiers() {
    check("Resolve(ForAll(x, Exists(y, y > x)), Integers)", //
        "True");
    check("Resolve(Exists(x, ForAll(y, y <= x)), Integers)", //
        "False");
  }

  @Test
  public void testEliminationLeavesAConditionOnTheParameters() {
    check("Resolve(Exists(y, x == 2*y + 1), Integers)", //
        "Mod(x,2)==1");
  }

  /** A nonlinear formula is outside the decidable fragment and stays unevaluated. */
  @Test
  public void testNonLinearStaysUnevaluated() {
    check("Resolve(Exists(x, x^2 == 2), Integers)", //
        "Resolve(Exists(x,x^2==2),Integers)");
    check("Resolve(Exists(x, x*y == 1), Integers)", //
        "Resolve(Exists(x,x*y==1),Integers)");
  }
}
