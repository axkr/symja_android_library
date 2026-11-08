package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Closed forms of <code>Hypergeometric2F1</code> and <code>Hypergeometric1F1</code> for integer
 * parameters: terminating series, Euler's transformation, the logarithmic forms of
 * <code>2F1(1,b,c,z)</code>, the value at <code>z == 1</code> and the poles of a non-positive
 * lower parameter.
 */
public class HypergeometricTerminatingTest extends ExprEvaluatorTestCase {

  @Test
  public void testEulerTransformTerminates() {
    check("Hypergeometric2F1(4,4,2,z)", //
        "(3+6*z+z^2)/(3*(1-z)^6)");
    // the transformed series ends at the smaller of the two degrees
    check("Hypergeometric2F1(6,3,2,z)", //
        "(1+2*z)/(1-z)^7");
    check("Hypergeometric2F1(6,3,2,1/2)", //
        "256");
    check("HypergeometricPFQ({4,4},{2},z)", //
        "(3+6*z+z^2)/(3*(1-z)^6)");
  }

  @Test
  public void testNegativeLowerParameter() {
    // the series ends before the pole of its terms
    check("Hypergeometric2F1(-2,3,-2,z)", //
        "1+3*z+6*z^2");
    check("Hypergeometric2F1(3,-2,-4,z)", //
        "1/2*(2+3*z+2*z^2)");
    // it doesn't
    check("Hypergeometric2F1(-4,3,-2,z)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(2,2,-3,z)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(2,3,0,z)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(1,1,0,z)", //
        "ComplexInfinity");
  }

  @Test
  public void testLogarithmicForms() {
    check("Hypergeometric2F1(1,2,5,z)", //
        "(-2*(6*z-9*z^2+2*z^3+(6-12*z+6*z^2)*Log(1-z)))/z^4");
    check("Hypergeometric2F1(1,4,6,1/3)", //
        "-5*(-263+648*Log(3/2))");
    check("Hypergeometric2F1(4,1,6,-2)", //
        "5/8*(4-3*Log(3))");
    check("N(Hypergeometric2F1(1,3,7,-3/2)) - Hypergeometric2F1(1,3,7,-1.5) // Abs // Chop", //
        "0");
  }

  @Test
  public void testValueAtOne() {
    // c == a+b: the series diverges logarithmically
    check("Hypergeometric2F1(3,2,5,1)", //
        "Infinity");
    check("Hypergeometric2F1(1,1,2,1)", //
        "Infinity");
    check("Hypergeometric2F1(3,4,6,1)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(2,3,7,1)", //
        "5");
  }

  @Test
  public void testHypergeometric1F1LowerParameterPole() {
    check("Hypergeometric1F1(3,-2,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(E,-1,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(-3,-2,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(b,0,z)", //
        "Hypergeometric1F1(b,0,z)");
    check("Hypergeometric1F1(-2,-4,z)", //
        "1+z/2+z^2/12");
    check("Hypergeometric1F1(-2.0,-4.0,3.0)", //
        "3.25");
    check("Hypergeometric1F1(-2.0,-4.0,z)", //
        "1.0+0.5*z+0.0833333*z^2");
    check("Hypergeometric1F1(c,0,0)", //
        "Indeterminate");
    check("Hypergeometric1F1(c,-3,0)", //
        "1");
  }

  @Test
  public void testValueAtOneRationalParameters() {
    // c == a+b with positive rational parameters: the series diverges logarithmically
    check("Hypergeometric2F1(1/3,2/3,1,1)", //
        "Infinity");
    check("Hypergeometric2F1(3/4,1/4,1,1)", //
        "Infinity");
    check("Hypergeometric2F1(1/2,1/2,1,1)", //
        "Infinity");
  }

  @Test
  public void testLogarithmicClosedFormBeyondOne() {
    // 1-z < 0 keeps the principal logarithm: the closed form agrees with the numeric value
    check("Chop(N(Hypergeometric2F1(1,3,6,4))-Hypergeometric2F1(1,3,6,4.0))", //
        "0");
    check("Chop(N(Hypergeometric2F1(1,2,7,1/4))-Hypergeometric2F1(1,2,7,0.25))", //
        "0");
  }

  @Test
  public void testMachineTerminatingRoundsOnce() {
    // the polynomial is summed exactly at the binary values of the doubles and rounded once;
    // summing in machine arithmetic is a few units in the last place off
    check("Hypergeometric2F1(1.5,-3,2.5,0.6)-0.3108571428571428", //
        "1.11022*10^-16");
    check("Hypergeometric2F1(-3,1.5,2.5,0.6)-0.3108571428571428", //
        "1.11022*10^-16");
    check("RealDigits(Hypergeometric2F1(5,-2,3,0.9))", //
        "{{2,5,0,0,0,0,0,0,0,0,0,0,0,0,0,3},-1}");
    check("Hypergeometric2F1(5,-2,3,9/10)", //
        "1/40");
    // a pole of the terms before the series ends
    check("Hypergeometric2F1(-3,2,-2,0.3)", //
        "Hypergeometric2F1(-3.0,2.0,-2.0,0.3)");
  }

  @Test
  public void testIntegerParameters1F1ExactNumber() {
    // integers 0 < a < b and an exact numeric argument
    check("Hypergeometric1F1(1,2,E)", //
        "(-1+E^E)/E");
    check("Hypergeometric1F1(1,3,Sqrt(3))", //
        "2/3*(-1-Sqrt(3)+E^Sqrt(3))");
    check("Hypergeometric1F1(2,4,Pi/2)", //
        "(24*(4-4*E^(Pi/2)+Pi+E^(Pi/2)*Pi))/Pi^3");
    check("HypergeometricPFQ({1},{3},Pi)", //
        "(2*(-1+E^Pi-Pi))/Pi^2");
  }
}
