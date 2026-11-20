package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Simplify writes a rational function of square roots of polynomials with a denominator free of
 * the roots and cancels the common factor.
 */
public class SimplifyRadicalNormalFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testSymbolicRadicands() {
    check("Simplify(2/Sqrt(b)+2*u/(b*Sqrt(b+u)-Sqrt(b)*(b+u)))", //
        "-2/Sqrt(b+u)");
    check("Simplify(3/Sqrt(k)-3*Sqrt(k+2*m)/(k+2*m)+6*m/(k*Sqrt(k+2*m)-Sqrt(k)*(k+2*m)))", //
        "-6/Sqrt(k+2*m)");
  }

  @Test
  public void testNumericRadicand() {
    check("Simplify(-1/Sqrt(5)-w/(5*Sqrt(5+w)-Sqrt(5)*(5+w)))", //
        "1/Sqrt(5+w)");
  }

  @Test
  public void testRelatedRootsKept() {
    // Sqrt(-f/g) is not independent of Sqrt(-g) and Sqrt(f): the denominator vanishes for f>0,
    // g>0 only, and a conjugate can't make it free of the roots
    check("Simplify((f*z)/(Sqrt(-g)*f-g*Sqrt(f)*Sqrt(-f/g)))", //
        "(f*z)/(f*Sqrt(-g)-Sqrt(f)*Sqrt(-f/g)*g)");
  }

  @Test
  public void testTranscendentalAtoms() {
    check("Simplify(-1/Sqrt(b)-E^(2*t)/(b*Sqrt(b+E^(2*t))-Sqrt(b)*(b+E^(2*t))))", //
        "1/Sqrt(b+E^(2*t))");
    check("Simplify(-1/Sqrt(b)-Log(t)/(b*Sqrt(b+Log(t))-Sqrt(b)*(b+Log(t))))", //
        "1/Sqrt(b+Log(t))");
  }

  @Test
  public void testNoNewPoles() {
    // rationalized, the denominator would be s*(w-1) and q*s, which vanish where the input is
    // defined: at w==1, s==-1 and at s==0, p==-1
    check("Simplify((-w*Sqrt(s^2*w))/(s-Sqrt(s^2*w)))", //
        "(-w*Sqrt(s^2*w))/(s-Sqrt(s^2*w))");
    check("Simplify(5*q/(p-Sqrt(p^2+q*s)))", //
        "(5*q)/(p-Sqrt(p^2+q*s))");
  }
}
