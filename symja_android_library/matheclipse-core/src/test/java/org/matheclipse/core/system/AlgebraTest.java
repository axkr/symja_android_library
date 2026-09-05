package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * JUnit test cases extracted from algebra.rs testing algebraic operations.
 */
public class AlgebraTest extends ExprEvaluatorTestCase {

  /** The JUnit setup method */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testPolynomialQ() {
    check("PolynomialQ(x^2 + 1, x)", //
        "True");
    check("PolynomialQ(3*x^3 + 2*x + 1, x)", //
        "True");
    check("PolynomialQ(5, x)", //
        "True");
    check("PolynomialQ(x, x)", //
        "True");
    check("PolynomialQ(Sin(x), x)", //
        "False");
    check("PolynomialQ(1/x, x)", //
        "False");
    check("PolynomialQ(x^2 + y, x)", //
        "True");
    check("PolynomialQ(x + y^2, {x, y})", //
        "True");
    check("PolynomialQ(x^2 + 2*x*y + y^2, {x, y})", //
        "True");
    check("PolynomialQ(Sin(x) + y, {x, y})", //
        "False");
    check("PolynomialQ(f(a) + f(a)^2, f(a))", //
        "True");
    check("PolynomialQ(f(a) + g(b)^2, {f(a), g(b)})", //
        "True");
    check("PolynomialQ(1/f(a), f(a))", //
        "False");
  }

  @Test
  public void testExponent() {
    check("Exponent((x + 1)^2, x + 1)", //
        "2");
    check("Exponent((x + 1)^2 y, x + 1)", //
        "2");
    check("Exponent((x + 1)^2 + 1, x + 1)", //
        "2");

    check("Exponent(x^3 + x, x)", //
        "3");
    check("Exponent(x^2 + 3*x + 2, x)", //
        "2");
    check("Exponent(x^2, 5)", //
        "0");
    check("Exponent(x^2 + 3 x, 5)", //
        "0");

    check("Exponent(Sin(x)^3, Sin(x))", //
        "3");
    check("Exponent(x^2 y, 2 x)", //
        "0");
    check("Exponent(Series(Exp(x), {x, 0, 5}), x)", //
        "5");
    check("Exponent(5, x)", //
        "0");
    check("Exponent(3*x + 1, x)", //
        "1");
    check("Exponent(b*x^(3/2), x)", //
        "3/2");
    check("Exponent(-4x, x, List)", //
        "{1}");
    check("Exponent(x^3 + 2x^2 - 5x + 1, x, List)", //
        "{0,1,2,3}");
  }

  @Test
  public void testCoefficient() {
    check("Coefficient(x^2 + 3*x + 2, x, 2)", //
        "1");
    check("Coefficient(x^2 + 3*x + 2, x, 1)", //
        "3");
    check("Coefficient(x^2 + 3*x + 2, x, 0)", //
        "2");
    check("Coefficient(x^2, x^2)", //
        "1");
    check("Coefficient(3 x y, x y)", //
        "3");
    check("Coefficient(x^2, 2 y)", //
        "0");
    check("Coefficient(x^2, x + 1)", //
        "0");
    check("CoefficientList(1 + 2 x + 3 x^2, x)", //
        "{1,2,3}");
    check("Coefficient(a*x^2 + b*x + c, x, 2)", //
        "a");
    check("Coefficient(x^2 + 1, x, 1)", //
        "0");
    check("Coefficient((x + 1)^5, x^3)", //
        "10");
    check("Coefficient(3 x^2 + 5 x, x^2)", //
        "3");
    check("Coefficient((x + y)^4, (x^2) * (y^2))", //
        "6");
    check("Coefficient(x*Cos(x + 3) + 6*y, x)", //
        "Cos(3+x)");
    // Coefficient() only expands with respect to the given variable. If the expression is free of
    // the variable it's returned unchanged for exponent 0
    check("Coefficient((x + 2)^3 + (x + 3)^2, y, 0)", //
        "(2+x)^3+(3+x)^2");
    check("Coefficient((x + 2)^3 + (x + 3)^2, y, 2)", //
        "0");
    check("Coefficient((x + 2)^3 + (x + 3)^2, y)", //
        "0");
    // 1/(-3+y)+1/(-2+y) is the OutputForm of (-3+y)^(-1)+(-2+y)^(-1)
    check("Coefficient((x + 2)/(y - 3) + (x + 3)/(y - 2), x)", //
        "1/(-3+y)+1/(-2+y)");
    check("Coefficient(6*x, 2*x)", //
        "0");
    check("Coefficient(2*x, 2*x)", //
        "1");
  }

  @Test
  public void testExpand() {
    check("Expand((x + 1)*(x + 2))", //
        "2+3*x+x^2");
    check("Expand((x + 1)^2)", //
        "1+2*x+x^2");
    check("Expand((x + 1)^3)", //
        "1+3*x+3*x^2+x^3");
    check("Expand(x*(x + 1))", //
        "x+x^2");
    check("Expand(x^2 + 3*x + 2)", //
        "2+3*x+x^2");
    check("Expand(x/4)", //
        "x/4");
    check("Expand(5)", //
        "5");
    check("Expand((x + 2)*(x - 2))", //
        "-4+x^2");
    check("Expand((x + y)^2)", //
        "x^2+2*x*y+y^2");
    check("Expand((a + b)*(c + d))", //
        "a*c+b*c+a*d+b*d");
    check("Expand((x + 1)^2 > x)", //
        "1+2*x+x^2>x");
  }

  @Test
  public void testSimplify() {
    // TODO
    // check("Simplify((6 - 4*x)/(5*x))", //
    // "-4/5 + 6/(5*x)");
    // check("Simplify(4*x^2 - 2*x)", //
    // "2*x*(-1 + 2*x)");
    check("Simplify(x + x)", //
        "2*x");
    check("Simplify((1 - Cos(2*x))/2)", //
        "Sin(x)^2");
    check("Simplify((1 + Cos(2*x))/2)", //
        "Cos(x)^2");
    check("Simplify(Sqrt(2) + Sqrt(3))", //
        "Sqrt(2)+Sqrt(3)");
    check("Simplify(3 - 3*x)", //
        "3-3*x");
    check("Simplify(2*Log(2))", //
        "Log(4)");
    check("Simplify(Log(2) + Log(3))", //
        "Log(6)");
    check("Simplify(2*Sqrt(2))", //
        "2*Sqrt(2)");
    check("Simplify(x*x)", //
        "x^2");
    check("Simplify((x^2 - 1)/(x - 1))", //
        "1+x");
    check("Simplify(5)", //
        "5");
    check("Simplify(Sin(x)^2 + Cos(x)^2)", //
        "1");
    check("Simplify(Cosh(x)^2 - Sinh(x)^2)", //
        "1");
    check("Simplify(a/x + b/x)", //
        "(a+b)/x");
  }

  @Test
  @Disabled
  public void testFactor() {
    check("Factor(x^2 + 3*x + 2)", //
        "(1 + x)*(2 + x)");
    check("Factor((x^2 + 3)*(x^4 + x + 7))", //
        "(3 + x^2)*(7 + x + x^4)");
    check("Factor(x^3 + x^2 - x)", //
        "x*(-1 + x + x^2)");
    check("Factor(x^2 - 4)", //
        "(-2 + x)*(2 + x)");
    check("Factor(2*x^2 + 6*x + 4)", //
        "2*(1 + x)*(2 + x)");
    check("Factor(x^2 + 1)", //
        "1 + x^2");
    check("Factor(-x^2 - 5*x - 6)", //
        "-((2 + x)*(3 + x))");
    check("Factor(x^3 - 1)", //
        "(-1 + x)*(1 + x + x^2)");
    check("Factor(x^6 - 1)", //
        "(-1 + x)*(1 + x)*(1 - x + x^2)*(1 + x + x^2)");
  }

  @Test
  public void testFactorList() {
    check("FactorList(x^3 - 1)", //
        "{{1,1},{-1+x,1},{1+x+x^2,1}}");
    check("FactorList(2*x^2 + 4*x + 2)", //
        "{{2,1},{1+x,2}}");
    check("FactorList(x^8 + x^4 + 1, Modulus -> 3)", //
        "{{1,1},{1+x,2},{2+x,2},{1+x^2,2}}");
    check("FactorList(x^5 + x + 1, Modulus -> 2)", //
        "{{1,1},{1+x+x^2,1},{1+x^2+x^3,1}}");
    check("FactorList(2*x^4 + 2, Modulus -> 5)", //
        "{{2,1},{2+x^2,1},{3+x^2,1}}");
    check("FactorList(5, Modulus -> 3)", //
        "{{2,1}}");
    check("FactorList(x^2 + 1)", //
        "{{1,1},{1+x^2,1}}");
    check("FactorList(6)", //
        "{{6,1}}");
    check("FactorList(x^4 - 1)", //
        "{{1,1},{-1+x,1},{1+x,1},{1+x^2,1}}");
    check("FactorList(x^3 + 3*x^2 + 3*x + 1)", //
        "{{1,1},{1+x,3}}");
    check("FactorList(x^2 + 3*x + 2)", //
        "{{1,1},{1+x,1},{2+x,1}}");
    check("FactorList(3/4)", //
        "{{3,1},{4,-1}}");
    check("FactorList(6/35)", //
        "{{6,1},{35,-1}}");
    check("FactorList(1/2)", //
        "{{2,-1}}");
    check("FactorList(-3/4)", //
        "{{-3,1},{4,-1}}");
    check("FactorList(-1/2)", //
        "{{-1,1},{2,-1}}");
    check("FactorList(-6)", //
        "{{-6,1}}");
    check("FactorList(-1)", //
        "{{-1,1}}");
    check("FactorList((x^2 - 1)/(x + 2))", //
        "{{1,1},{-1+x,1},{1+x,1},{2+x,-1}}");
    check("FactorList((x^2 - 1)/2)", //
        "{{2,-1},{-1+x,1},{1+x,1}}");
    check("FactorList(6*(x - 1)/(x + 2))", //
        "{{6,1},{-1+x,1},{2+x,-1}}");
    check("FactorList(2/(x^2 - 1))", //
        "{{2,1},{-1+x,-1},{1+x,-1}}");
  }

  @Test
  public void testCancel() {
    // Cancel reduces a single fraction by cancelling common factors of numerator and denominator;
    // it must NOT split a single fraction into a partial-fraction-like sum (that was a bug: e.g.
    // (3-5*x)/(2-2*x) used to become 3/(2-2*x)-(5*x)/(2-2*x)). The expected strings below are
    // Symja's OutputForm. They are the mathematically-equal reductions of the corresponding
    // InputForm results (verified value-equal); Symja prints a reciprocal
    // as 1/(...) rather than (...)^(-1), does not push a leading sign into the denominator, and
    // only cancels factors that are actually common to numerator and denominator (so a
    // denominator-only numeric/monomial factor such as the 2 in 2-2*x is not pulled out).
    // A fraction whose every leaf is constant is still cancellable: PolynomialHomogenization
    // replaces each non-polynomial kernel (here E^Sqrt(3), since E is CONSTANT and Sqrt(3) is a
    // numeric Power, so there is no variable to be found) with a dummy variable. Every result
    // below was checked against its input numerically at 30 digits.
    check("Cancel((2+2*E^(2*Sqrt(3)))/(4*E^Sqrt(3)))", //
        "(1+E^(2*Sqrt(3)))/(2*E^Sqrt(3))");
    check("Cancel((6+6*E^(2*Sqrt(3)))/(4*E^Sqrt(3)))", //
        "(3+3*E^(2*Sqrt(3)))/(2*E^Sqrt(3))");
    check("Cancel((2+2*Pi)/(4*Pi))", //
        "(1+Pi)/(2*Pi)");
    check("Cancel((2+2*Cos(1))/(4*Cos(1)^2))", //
        "1/2*(Sec(1)+Sec(1)^2)");
    check("Cancel((2+2*I*E^(2*Sqrt(3)))/(4*E^Sqrt(3)))", //
        "(1+I*E^(2*Sqrt(3)))/(2*E^Sqrt(3))");
    // already reduced: the gcd is 1 and the fraction is returned unchanged
    check("Cancel((1+E^(2*Sqrt(3)))/(2*E^Sqrt(3)))", //
        "(1+E^(2*Sqrt(3)))/(2*E^Sqrt(3))");
    // neither a variable nor a substituted kernel, so there is no polynomial ring to build
    check("Cancel((1+Sqrt(2))/(3+Sqrt(2)))", //
        "(1+Sqrt(2))/(3+Sqrt(2))");
    // an irrational coefficient must not hide the integer content: the ring-level GCD of the
    // coefficients reports the rational content, so gcd(2, 2*Sqrt(3)) is 2 and not 1
    check("Cancel((2*Sqrt(3)+2*E^(2*q))/(4*E^q))", //
        "(Sqrt(3)+E^(2*q))/(2*E^q)");
    check("Cancel((2*Sqrt(3)*x+2*x^2)/(4*x))", //
        "1/2*(Sqrt(3)+x)");
    check("Cancel((Sqrt(3)+Sqrt(3)*x^2)/(2*Sqrt(3)*x))", //
        "(1+x^2)/(2*x)");
    // the polynomial GCD keeps the coefficients expanded, so this stays a compact quotient instead
    // of collapsing into a page of nested fractions built from unexpanded products of Sqrt(2)
    check("Cancel(Expand((1+Sqrt(2)+x)^4*(2+x))/Expand((1+Sqrt(2)+x)^2*(2+x)^2))", //
        "(17+12*Sqrt(2)+(14+10*Sqrt(2))*x+(3+2*Sqrt(2))*x^2)/(6+4*Sqrt(2)+(3+2*Sqrt(2))*x)");
    check("Cancel(Expand((1+Sqrt(2)+E^Sqrt(3))^4*(2+E^Sqrt(3)))/Expand((1+Sqrt(2)+E^Sqrt(3))^2*(2+E^Sqrt(3))^2))", //
        "(17+12*Sqrt(2)+(14+10*Sqrt(2))*E^Sqrt(3)+(3+2*Sqrt(2))*E^(2*Sqrt(3)))/(6+4*Sqrt(\n"
            + "2)+(3+2*Sqrt(2))*E^Sqrt(3))");
    check("Cancel((4*x^2 - 2*x)/(2 + 3*x))", //
        "(-2*x+4*x^2)/(2+3*x)");
    check("Cancel((-5 - 2*x - 4*x^2)/(3 + 2*x^2 + 5*x^3))", //
        "(-5-2*x-4*x^2)/(3+2*x^2+5*x^3)");
    check("Cancel(x/(2 - 2*x))", //
        "x/(2-2*x)");
    check("Cancel((x*y)/(2 - 2*x))", //
        "(x*y)/(2-2*x)");
    check("Cancel((3 - 5*x)/(2 - 2*x))", //
        "(3-5*x)/(2-2*x)");
    check("Cancel((3 - 5*x)/(4 - 6*x))", //
        "(3-5*x)/(4-6*x)");
    check("Cancel((x^2 - 1)/(x - 1))", //
        "1+x");
    check("Cancel((x^3 - x)/(x^2 - 1))", //
        "x");
    check("Cancel((a*b)/(a*c))", //
        "b/c");
    check("Cancel((a^2*b)/(a*b^2))", //
        "a/b");
    check("Cancel((2*x)/(4*x))", //
        "1/2");
    check("Cancel((a*b*x)/(a*c*x^2))", //
        "b/(c*x)");
    check("Cancel((x^2 + 2*x + 1)/(x + 1))", //
        "1+x");
    check("Cancel((x/2)/(1 - x))", //
        "x/(2*(1-x))");
    check("Cancel((5*x)/(2 - 2*x))", //
        "(5*x)/(2-2*x)");
    check("Cancel((5*x)/(1 - 5*x))", //
        "(5*x)/(1-5*x)");
    check("Cancel(x/(2 - 2*x)^2)", //
        "x/(2-2*x)^2");
    check("Cancel(x/(2 - 2*x)^3)", //
        "x/(2-2*x)^3");
    check("Cancel((2 - 4*x)/(2 + 2*x))", //
        "(1-2*x)/(1+x)");
    check("Cancel((2 - 4*x)/(-2 + 2*x))", //
        "(1-2*x)/(-1+x)");
    check("Cancel((2 - 4*x)/2)", //
        "1-2*x");
    check("Cancel((-4 - 3*x^2)/(4 + x - 4*x^2))", //
        "(-4-3*x^2)/(4+x-4*x^2)");
    check("Cancel((2 - 4*x)/(2 - 2*x))", //
        "(1-2*x)/(1-x)");
    check("Cancel((1 + x)/(1 - x))", //
        "(1+x)/(1-x)");
    check("Cancel(x/(1 - x))", //
        "x/(1-x)");
    check("Cancel(2/(2 - 2*x))", //
        "1/(1-x)");
    check("Cancel(1/(1 - x))", //
        "1/(1-x)");
    check("Cancel(-1/(2 - 4*x))", //
        "-1/(2-4*x)");
    check("Cancel(-1/(3 - 6*x))", //
        "-1/(3-6*x)");
    check("Cancel(-1/(1 + x))", //
        "-1/(1+x)");
    check("Cancel(-1/(2 + x))", //
        "-1/(2+x)");
    check("Cancel(-1/(x + y))", //
        "-1/(x+y)");
    check("Cancel(-1/(-1 + x))", //
        "-1/(-1+x)");
    check("Cancel(-1/(2 + 2*x))", //
        "-1/(2+2*x)");
    check("Cancel(-2/(1 + x))", //
        "-2/(1+x)");
    check("Cancel(1/(2 + 2*x))", //
        "1/(2+2*x)");
    check("Cancel(1/(2 - 2*x))", //
        "1/(2-2*x)");
    check("Cancel(1/(x^2 - x))", //
        "1/(-x+x^2)");
    check("Cancel(1/((x - 1)*(x + 1)))", //
        "1/((-1+x)*(1+x))");
    check("Cancel(1/(x*(x + 1)))", //
        "1/(x*(1+x))");
    check("Cancel(1/(x + 1/x))", //
        "1/(1/x+x)");
    check("Cancel((x + 1)/(4*x + 5*x^2))", //
        "(1+x)/(4*x+5*x^2)");
    check("Cancel(2/(4*x + 5*x^2))", //
        "2/(4*x+5*x^2)");
    check("Cancel((x + 2)/(x^2 - x))", //
        "(2+x)/(-x+x^2)");
    check("Cancel((x + 1)/(x^2 + x^4))", //
        "(1+x)/(x^2+x^4)");
    check("Cancel((x + 1)/((x - 1)*(x + 2)))", //
        "(1+x)/((-1+x)*(2+x))");
    check("Cancel(x/(4*x^2 + 4*x^3))", //
        "1/(4*x*(1+x))");
    check("Cancel(2/(1 - 2*x)^2)", //
        "2/(1-2*x)^2");
  }

  @Test
  public void testCollect() {
    check("Collect(x*y + x*z, x)", //
        "x*(y+z)");
    check("Collect(x^2, 5)", //
        "x^2");
    check("Collect(x^3 + y + x, x)", //
        "x+x^3+y");
    check("Collect(q(x) + q(x) q(y), q(x))", //
        "q(x)*(1+q(y))");
    check("Collect(a*x^2 + b*x + c*x^2 + d*x, x)", //
        "(b+d)*x+(a+c)*x^2");
    check("Collect(a x^2 + b x^2 y + c x y, x)", //
        "c*x*y+x^2*(a+b*y)");
  }

  @Test
  public void testTogether() {
    // regression: JAS' GenPolynomial.divide asserts its dividend is in descending leading-exponent
    // order and threw an AssertionError (an Error, so it escaped the RuntimeException guard and
    // aborted the whole evaluation) while cancelling the gcd of this multivariate combination
    check("Together(1/(1+x) + 1/(1+x+x^5))", //
        "(2+2*x+x^5)/((1+x)*(1+x+x^2)*(1-x^2+x^3))");
    // regression: a Gaussian integer coefficient makes the JAS BigRational conversion fail, so the
    // denominator was factored through PolynomialHomogenization. That path used to rebalance every
    // factor by x^(-degree/2), which kept the value but shifted all exponents by a half-integer
    check("Together(1/(1-I*x)-1)", //
        "(I*x)/(1-I*x)");
    check("Together(-1+2/(1-I*x))", //
        "(1+I*x)/(1-I*x)");
    check("Together(1/(1-I*x^2)-1)", //
        "(I*x^2)/(1-I*x^2)");
    check("Together(1/(1-I*x-x^2)-1)", //
        "(I*x+x^2)/(1-I*x-x^2)");
    check("Together(1/(2-I*x)-1)", //
        "(-1+I*x)/(2-I*x)");
    check("Together((1+I)/(1-I*x)-1)", //
        "(I+I*x)/(1-I*x)");
    check("Together(1/(1-I*x-x)-1)", //
        "-x/(-1/2+I*1/2+x)");
    check("Together(1+x/y)", //
        "(x+y)/y");
    check("Together(x*(1/x + 1/y))", //
        "(x+y)/y");
    check("Together(1/x + 1/y)", //
        "(x+y)/(x*y)");
    check("Together((3 - 5*x)/(2 - 2*x))", //
        "(3-5*x)/(2-2*x)");
    check("Together(1/3 + I/3)", //
        "1/3+I*1/3");
    check("Together((x^2 + x)/(x^2 - 1))", //
        "x/(-1+x)");
    check("Together(a/b + c/d)", //
        "(b*c+a*d)/(b*d)");

    // regression: a bare symbol denominator never reached the polynomial-gcd cancellation, and
    // cancelCommonFactors() only factors the numerator for up to 3 variables - so from four
    // variables on the common x survived. Rubi's PolyQ is
    // PolynomialQ(u,x)||PolynomialQ(Together(u),x), so ExpandToSum() then printed
    // "Warning: Unrecognized expression for expansion"
    check("Together((b*x+c*x^2)/x)", //
        "b+c*x");
    check("Together((b*x+c*x^2+d*x^3)/x)", //
        "b+c*x+d*x^2");
    check("Together((b*x+c*x^2+d*x^3+e*x^4)/x)", //
        "b+c*x+d*x^2+e*x^3");
    check("Together((3*b*x+6*c*x^2+9*d*x^3)/x)", //
        "3*(b+2*c*x+3*d*x^2)");
    check("Together((b*(1+p)*x+c*(2+2*p)*x^2+d*(3+3*p)*x^3)/x)", //
        "b+b*p+2*c*x+2*c*p*x+3*d*x^2+3*d*p*x^2");
    check("PolynomialQ(Together((b*(1+p)*x+c*(2+2*p)*x^2+d*(3+3*p)*x^3)/x), x)", //
        "True");
    // nothing to cancel - the constant term keeps 1/x in the result
    check("Together((a+b*x+c*x^2+d*x^3)/x)", //
        "(a+b*x+c*x^2+d*x^3)/x");
    check("Together((b*x+c*x^2+d*x^3)/x^2)", //
        "(b+c*x+d*x^2)/x");
  }

  @Test
  public void testApart() {
    check("Apart(1/(x^2 - 1))", //
        "1/(2*(-1+x))-1/(2*(1+x))");
    check("Apart((x^2 + 1)/(x^3 - x))", //
        "1/(-1+x)-1/x+1/(1+x)");
    check("Apart(1/(-3 x))", //
        "-1/(3*x)");
    check("Apart(1/((x - 1)*(x - 2)))", //
        "1/(-2+x)-1/(-1+x)");
    check("Apart(x/((x - 1) (x - 2)))", //
        "2/(-2+x)-1/(-1+x)");
  }

  @Test
  public void testApartGaussianDenominator() {
    // JAS decomposes over BigRational only, so a Gaussian-integer denominator used to fall back to
    // the Bezout iteration in AlgebraUtil.partialFractionDecomposition(). All three denominators
    // below are -I*(x-I)*(x+I)^2 up to a unit, and a repeated factor is something that iteration
    // cannot express at all - it returned a wrong value for each of them. They now go through
    // AlgebraUtil.partialFractionDecompositionComplexRational(), which redoes the same JAS
    // algorithm over ComplexRing<BigRational>. Values verified against Mathematica.
    check("Apart(1/((1-I*x)*(1+x^2)))", //
        "(-I*1/4)/(-I+x)-1/(2*(I+x)^2)+(I*1/4)/(I+x)");
    check("Apart(x/((1-I*x)*(1+x^2)))", //
        "1/(4*(-I+x))+(I*1/2)/(I+x)^2-1/(4*(I+x))");
    check("Apart(1/((I+x)*(1+x^2)))", //
        "-1/(4*(-I+x))+(I*1/2)/(I+x)^2+1/(4*(I+x))");
    // this one collapsed all the way to 0, which is what made Simplify() pick 0 as its
    // "simplest" candidate and lose a PolyLog term from Integrate(Log(x^2/(1+x^2))/(1+x^2),x).
    // The fraction cancels to the constant -I.
    check("Apart(((I-x)*(I+x)^2)/((1-I*x)*(1+x^2)))", //
        "-I");

    // a real irreducible quadratic is NOT split into complex linear factors - the complex ring is
    // only reached after the rational decomposition declined
    check("Apart(1/(1+x^2))", //
        "1/(1+x^2)");

    // coprime factors decompose too, with the denominators normalized to monic linear factors
    check("Apart(1/((1-I*x)*(2+x)))", //
        "(-1/5+I*2/5)/(I+x)+(1/5-I*2/5)/(2+x)");
    // ... and an improper fraction keeps its polynomial part
    check("Apart(x^3/((1-I*x)*(2+x)))", //
        "1-I*2+(-2/5-I*1/5)/(I+x)+I*x+(-8/5+I*16/5)/(2+x)");

    // an irrational coefficient reaches neither JAS ring and still uses the Bezout fallback, which
    // used to drop the quotients of its two divisions - i.e. the polynomial part
    check("Together(Apart(x^3/((1-Sqrt(2)*x)*(2+x))) - x^3/((1-Sqrt(2)*x)*(2+x)))", //
        "0");
  }

}
