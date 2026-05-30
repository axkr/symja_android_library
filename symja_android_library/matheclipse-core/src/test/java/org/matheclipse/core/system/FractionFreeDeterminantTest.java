package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for the fraction-free (Bareiss) symbolic determinant and the cofactor based
 * {@code Adjugate}. These verify that symbolic results stay polynomial (no spurious denominators)
 * and satisfy the defining algebraic identities.
 */
public class FractionFreeDeterminantTest extends ExprEvaluatorTestCase {

  @Test
  public void testSymbolicDeterminantHasNoDenominator() {
    // the fraction-free determinant returns a polynomial (denominator 1) for symbolic n>3 input
    check("Denominator(Det({{a, b, 0, 0}, {0, c, d, 0}, {0, 0, e, f}, {g, 0, 0, h}}))", //
        "1");
  }

  @Test
  public void testSymbolicDeterminantValue() {
    // a*c*e*h - b*d*f*g, checked by substitution to be independent of term ordering
    check("Det({{a, b, 0, 0}, {0, c, d, 0}, {0, 0, e, f}, {g, 0, 0, h}}) "
        + "/. {a -> 1, b -> 2, c -> 3, d -> 4, e -> 5, f -> 6, g -> 7, h -> 8}", //
        "-216");
  }

  @Test
  public void testAdjugateIdentity3x3() {
    // Adjugate(m).m == Det(m)*IdentityMatrix; the difference must expand to the zero matrix
    check("m = {{a, b, c}, {d, e, f}, {g, h, k}}; "
        + "Flatten(Expand(Adjugate(m).m - Det(m)*IdentityMatrix(3)))", //
        "{0,0,0,0,0,0,0,0,0}");
  }

  @Test
  public void testAdjugateIdentity4x4() {
    // also cross-checks the Bareiss determinant (n=4) against the cofactor adjugate
    check("m = {{a, b, 0, 0}, {0, c, d, 0}, {0, 0, e, f}, {g, 0, 0, h}}; "
        + "Flatten(Expand(Adjugate(m).m - Det(m)*IdentityMatrix(4)))", //
        "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}");
  }

  @Test
  public void testAdjugateEntriesArePolynomial() {
    // every adjugate entry of a symbolic matrix is a polynomial (no denominators)
    check("m = {{a, b, c}, {d, e, f}, {g, h, k}}; "
        + "Union(Flatten(Map(Denominator, Adjugate(m), {2})))", //
        "{1}");
  }
}
