package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The grouping of sums written with <code>+</code> and <code>-</code>, as the parser builds it and
 * as held subtractions and sums are printed.
 */
public class HeldSumGroupingTest extends ExprEvaluatorTestCase {

  @Test
  public void testParsedSumIsFlat() {
    // + and - written in a row are one sum
    check("FullForm(Hold(m-n+k))", //
        "Hold(Plus(m, Times(-1, n), k))");
    check("FullForm(Hold(m+n-k+j))", //
        "Hold(Plus(m, n, Times(-1, k), j))");
  }

  @Test
  public void testParenthesizedSumKeepsItsNode() {
    check("FullForm(Hold((m-n+k)-j))", //
        "Hold(Plus(Plus(m, Times(-1, n), k), Times(-1, j)))");
    check("FullForm(Hold((m+n+k)-j))", //
        "Hold(Plus(Plus(m, n, k), Times(-1, j)))");
    check("FullForm(Hold((m-n)-k))", //
        "Hold(Plus(Plus(m, Times(-1, n)), Times(-1, k)))");
    check("Hold((m+n)-k)", //
        "Hold((m+n)-k)");
    check("Hold((m-n+k)-j)", //
        "Hold((m-n+k)-j)");
    // evaluated, the sums are flattened as before
    check("(m-n+k)-j", //
        "-j+k+m-n");
  }

  @Test
  public void testHeldSubtractPrintsItsGrouping() {
    check("Hold(Subtract(m,Plus(n,k,j)))", //
        "Hold(m-(n+k+j))");
    check("Hold(Subtract(Plus(m,n),k))", //
        "Hold((m+n)-k)");
    check("Hold(Subtract(m,Subtract(n,k)))", //
        "Hold(m-(n-k))");
    // no second minus directly after the first one
    check("Hold(Subtract(m,Times(-1,n)))", //
        "Hold(m-(-n))");
    check("Hold(Subtract(m,-7))", //
        "Hold(m-(-7))");
    check("Hold(Subtract(m,3+2*I))", //
        "Hold(m-(3+2*I))");
    check("Hold(Subtract(m,Complex(3,-2)))", //
        "Hold(m-(3-I*2))");
    check("Hold(Subtract(m,5*n))", //
        "Hold(m-5*n)");
  }
}
