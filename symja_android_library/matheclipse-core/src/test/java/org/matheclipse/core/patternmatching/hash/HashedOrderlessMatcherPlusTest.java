package org.matheclipse.core.patternmatching.hash;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * {@link HashedOrderlessMatcherPlus#updateHashValues} consumed both summands of a pair before it
 * knew whether the integer factors allow the rewrite; if another pair of the sum was rewritten,
 * the two summands were silently dropped from the result.
 */
public class HashedOrderlessMatcherPlusTest extends ExprEvaluatorTestCase {

  @Test
  public void testNegateRuleWithSameSignFactors() {
    // Sec^2 - Tan^2 -> 1 does not apply to 3*Sec^2 + 2*Tan^2, but Sin^2 + Cos^2 -> 1 fires
    check("3*Sec(x)^2+2*Tan(x)^2+Sin(y)^2+Cos(y)^2", "1+3*Sec(x)^2+2*Tan(x)^2");
    check("3*Sec(x)^2+2*Tan(x)^2", "3*Sec(x)^2+2*Tan(x)^2");
    check("-3*Sec(x)^2-2*Tan(x)^2+Sin(y)^2+Cos(y)^2", "1-3*Sec(x)^2-2*Tan(x)^2");
  }

  @Test
  public void testNegateRuleWithOppositeSignFactors() {
    check("3*Sec(x)^2-2*Tan(x)^2+Sin(y)^2+Cos(y)^2", "3+Sec(x)^2");
    check("2*Sec(x)^2-3*Tan(x)^2+Sin(y)^2+Cos(y)^2", "3-Tan(x)^2");
    check("-2*Sec(x)^2+3*Tan(x)^2+Sin(y)^2+Cos(y)^2", "-1+Tan(x)^2");
  }

  @Test
  public void testRuleWithMixedSignFactors() {
    // Sin^2 + Cos^2 -> 1 does not apply to 3*Sin^2 - 2*Cos^2
    check("3*Sin(y)^2-2*Cos(y)^2+Sin(z)^2+Cos(z)^2", "1-2*Cos(y)^2+3*Sin(y)^2");
    check("3*Sin(y)^2-2*Cos(y)^2", "-2*Cos(y)^2+3*Sin(y)^2");
  }

  @Test
  public void testRuleWithSameSignFactors() {
    check("3*Sin(y)^2+2*Cos(y)^2+Sin(z)^2+Cos(z)^2", "3+Sin(y)^2");
    check("2*Sin(y)^2+3*Cos(y)^2+Sin(z)^2+Cos(z)^2", "3+Cos(y)^2");
    check("-2*Sin(y)^2-3*Cos(y)^2+Sin(z)^2+Cos(z)^2", "-1-Cos(y)^2");
  }
}
