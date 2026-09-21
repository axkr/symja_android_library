package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Risch-Norman integration over a tower of Log, Exp and Tan with one square root in it. Each
 * integrand is the derivative of the expected answer, so the answer is known to be right.
 */
public class RischNormanRadicalTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testRadicalInTheTower() {
    // a generator above the radical: Log(x+Sqrt(x^2+1)) differentiates through the root
    check("Integrate(Together(D(Log(x)*Log(x+Sqrt(x^2+1)),x)),x,Method->\"RischNorman\")", //
        "Log(x)*Log(x+Sqrt(1+x^2))");
    check("Integrate(Together(D(Sqrt(x^3+1)/x*Log(x),x)),x,Method->\"RischNorman\")", //
        "(Sqrt(1+x^3)*Log(x))/x");
    check("Integrate(Together(D(E^x*Log(x)/Sqrt(x^2+x+1),x)),x,Method->\"RischNorman\")", //
        "(E^x*Log(x))/Sqrt(1+x+x^2)");
    check("Integrate(Together(D(Log(x)*Sqrt(x^2-3*x+5),x)),x,Method->\"RischNorman\")", //
        "Sqrt(5-3*x+x^2)*Log(x)");
    check("Integrate(Together(D(E^x*Log(x+Sqrt(x^2+4)),x)),x,Method->\"RischNorman\")", //
        "E^x*Log(x+Sqrt(4+x^2))");
    // D(Tan(x)) arrives as Sec(x)^2
    check("Integrate(Together(D(Tan(x)*Sqrt(x^2+1),x)),x,Method->\"RischNorman\")", //
        "Sqrt(1+x^2)*Tan(x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testNormLogands() {
    // the denominator factor 1+q*t^2 is a norm, a^2+b^2*q, which gives ArcTan(b*y/a)
    check("Integrate(Together(D(ArcTan(Sqrt(x^2+1)*Log(x)),x)),x,Method->\"RischNorman\")", //
        "ArcTan(Sqrt(1+x^2)*Log(x))");
    // E^x and E^(2*x) are one generator
    check("Integrate(Together(D(ArcTan(E^x*Sqrt(x^2+2)),x)),x,Method->\"RischNorman\")", //
        "ArcTan(E^x*Sqrt(2+x^2))");
    // x^2*q - t^4 is a norm without a constant term: a^2 - b^2*q with a = t^2, b = x
    check("Integrate(Together(D(Log(Log(x)^2+x*Sqrt(x^4+1)),x)),x,Method->\"RischNorman\")", //
        "Log(x*Sqrt(1+x^4)+Log(x)^2)");
  }

  @Test
  public void testFlattenedRoots() {
    // Sqrt(Log(x)) replaces the generator Log(x), and Sqrt(x^2+1) is the radical
    check("Integrate(Together(D(Sqrt(Log(x))*Sqrt(x^2+1),x)),x,Method->\"RischNorman\")", //
        "Sqrt(1+x^2)*Sqrt(Log(x))");
    check("Integrate(Together(D(Sqrt(x+Log(x))*Log(x),x)),x,Method->\"RischNorman\")", //
        "Log(x)*Sqrt(x+Log(x))");
  }

  @Test
  public void testNotElementary() {
    // declined, never answered wrongly
    check("Integrate(Log(x)/Sqrt(x^3+1),x,Method->\"RischNorman\")", //
        "Integrate(Log(x)/Sqrt(1+x^3),x,Method->RischNorman)");
    check("Integrate(Sqrt(x^4+1)*Log(x),x,Method->\"RischNorman\")", //
        "Integrate(Sqrt(1+x^4)*Log(x),x,Method->RischNorman)");
    check("Integrate(E^x/Sqrt(x^2+1),x,Method->\"RischNorman\")", //
        "Integrate(E^x/Sqrt(1+x^2),x,Method->RischNorman)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testAutomatic() {
    // the rules integrate these sums term by term, and the terms are not elementary on their own
    check("Integrate(Together(D(Log(x)*Log(x+Sqrt(x^2+1)),x)),x)", //
        "Log(x)*Log(x+Sqrt(1+x^2))");
    check("Integrate(Together(D(Sqrt(x^3+1)/x*Log(x),x)),x)", //
        "(Sqrt(1+x^3)*Log(x))/x");
    check("Integrate(Together(D(Log(x)*Log(2*x+Sqrt(4*x^2+1)),x)),x)", //
        "Log(x)*Log(2*x+Sqrt(1+4*x^2))");
  }
}
