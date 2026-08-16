package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * {@code NCache(x, xn)} pairs an exact value with an approximate one and evaluates to the exact
 * value.
 *
 * <p>
 * The cached number is a note left by whoever wrote it: it is what {@code N} of the exact value
 * would give, worked out once so a renderer need not do the arithmetic again. Dropping it loses
 * nothing, because asking for the numerical value of the exact expression gives the same answer -
 * and keeping the exact value is what lets the expression still be computed with.
 */
public class NCacheTest extends ExprEvaluatorTestCase {

  @Test
  public void testNCacheEvaluatesToTheExactValue() {
    check("NCache(1/3, 0.3333333333333333)", //
        "1/3");
    check("NCache(Pi, 3.141592653589793)", //
        "Pi");
    check("NCache(Sqrt(2), 1.4142135623730951)", //
        "Sqrt(2)");
  }

  /** The exact value keeps its own head, so what follows still sees a rational or a symbol. */
  @Test
  public void testNCacheKeepsTheExactHead() {
    check("Head(NCache(1/3, 0.3333333333333333))", //
        "Rational");
    check("Head(NCache(Pi, 3.141592653589793))", //
        "Symbol");
  }

  /** Exactness survives, which is the point of keeping the first value rather than the second. */
  @Test
  public void testNCacheStaysExactInArithmetic() {
    check("NCache(1/3, 0.333) + 1/6", //
        "1/2");
    check("Total({NCache(1/2, 0.5), NCache(1/2, 0.5)})", //
        "1");
    check("Simplify(NCache(Pi, 3.14159)^2)", //
        "Pi^2");
  }

  /** Asking for the number gives the number the cache held, by computing it from the exact value. */
  @Test
  public void testNCacheUnderN() {
    check("N(NCache(1/3, 0.3333333333333333))", //
        "0.333333");
    check("N(NCache(Pi, 3.14))", //
        "3.14159");
  }

  /** Coordinates arrive wrapped one by one, which is how a formatted graphic carries them. */
  @Test
  public void testNCacheInsideAList() {
    check("{NCache(Pi/4, 0.7853981633974483), NCache(-Pi/4, -0.7853981633974483)}", //
        "{Pi/4,-Pi/4}");
  }

  /** Two arguments exactly; anything else is left alone with a message. */
  @Test
  public void testNCacheArgumentCount() {
    check("NCache(1/3)", //
        "NCache(1/3)");
    check("NCache(1/3, 0.333, 4)", //
        "NCache(1/3,0.333,4)");
  }

  @Test
  public void testNCacheAttributes() {
    check("Attributes(NCache)", //
        "{NHoldRest,Protected}");
  }

  /** The name is known to the parser, so it keeps its spelling rather than becoming a new symbol. */
  @Test
  public void testNCacheIsABuiltIn() {
    assertEquals("{NCache}", evalString("Names(\"NCache\")"));
  }
}
