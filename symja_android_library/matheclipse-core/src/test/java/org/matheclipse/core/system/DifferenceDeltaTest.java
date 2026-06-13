package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class DifferenceDeltaTest extends ExprEvaluatorTestCase {

  @Test
  public void testDifferenceDelta() {
    check("DifferenceDelta({f(i), g(i)}, i)", //
        "{-f(i)+f(1+i),-g(i)+g(1+i)}");
    check("DifferenceDelta(Cosh(a*i+b),{i,2,h})", //
        "4*Cosh(b+a*h+a*i)*Sinh(1/2*a*h)^2");
    check("DifferenceDelta(Sin(a*i+b),{i,5})", //
        "32*Sin(a/2)^5*Sin(b+a*i+5/2*(a+Pi))");
    check("DifferenceDelta(Sinh(a*i+b),{i,4})", //
        "16*Sinh(a/2)^4*Sinh(2*a+b+a*i)");
    check("DifferenceDelta(Cosh(a*i+b),{i,4})", //
        "16*Cosh(2*a+b+a*i)*Sinh(a/2)^4");
    check("DifferenceDelta(Exp(a*i+b),{i,5})", //
        "E^(b+a*i)*(-1+E^a)^5");
    check("DifferenceDelta(b(a),{a,2,c})", //
        "b(a)-2*b(a+c)+b(a+2*c)");
    check("DifferenceDelta(b(a),{a,3,c})", //
        "-b(a)+3*b(a+c)-3*b(a+2*c)+b(a+3*c)");
    check("DifferenceDelta(b(a),{a,5,c})", //
        "-b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)");
    check("DifferenceDelta(b(a),{a,1,c})", //
        "-b(a)+b(a+c)");
    check("DifferenceDelta(b(a),a)", //
        "-b(a)+b(1+a)");
    check("DifferenceDelta(f(i, j), i, j)", //
        "f(i,j)-f(i,1+j)-f(1+i,j)+f(1+i,1+j)");
    check("DifferenceDelta(f(i, j), {i,2}, j)", //
        "-f(i,j)+f(i,1+j)+2*f(1+i,j)-2*f(1+i,1+j)-f(2+i,j)+f(2+i,1+j)");


  }

  @Test
  public void testForwardDifference() {
    // DifferenceDelta(f, i) generates the forward difference (f /. i->i+1) - f
    check("DifferenceDelta(b(a),a)", //
        "-b(a)+b(1+a)");
    // first difference of a factorial: Factorial(x)*(Pochhammer(x+1,1)-1) = x*x!
    check("DifferenceDelta(x!,x)", //
        "x*x!");
  }

  @Test
  public void testHigherOrderDifference() {
    check("DifferenceDelta(b(a),{a,5,c})", //
        "-b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)");
  }

  @Test
  public void testClosedFormDifference() {
    // base^(c + a*x) * (base^(a*h) - 1)^n
    check("DifferenceDelta(2^a, a)", //
        "2^a");
  }

  @Test
  public void testLeftInverseOfIndefiniteSum() {
    // DifferenceDelta is the left inverse of the indefinite Sum
    check("DifferenceDelta(Sum(a^i, i), i)", //
        "a^i");
  }

  @Test
  public void testDifferenceDeltaIndefiniteSum() {
    check("DifferenceDelta(Sum(k*k!,k),k)", //
        "k*k!");
  }
}
