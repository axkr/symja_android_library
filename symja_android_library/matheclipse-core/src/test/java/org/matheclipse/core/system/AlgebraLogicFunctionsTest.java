package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for SubresultantPolynomials, SubresultantPolynomialRemainders, PowerSymmetricPolynomial, AugmentedSymmetricPolynomial, Conjunction, Disjunction, UnateQ, TensorExpand and the ExcludedForms option of Simplify. */
public class AlgebraLogicFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testSubresultantPolynomials() {
    check("SubresultantPolynomials(x^3 - 3*x + 2, x^2 - 5*x + 6, x)", //
        "{80,-28+16*x,6-5*x+x^2}");
    check("SubresultantPolynomials(x^4 - 2, x^2 + 3*x - 1, x)", //
        "{-233,-8+33*x,-1+3*x+x^2}");
    check("SubresultantPolynomials(3*x^2 + 1, x^2 - x + 2, x)", //
        "{28,5-3*x,2-x+x^2}");
    check("SubresultantPolynomials(x^3 + 4*x, -2*x^3 + x - 1, x)", //
        "{-325,-9+81*x,-1+9*x,1/2*(1-x+2*x^3)}");
    check("SubresultantPolynomials(x^3 + p*x + q, 3*x^2 + p, x)", //
        "{4*p^3+27*q^2,9*q+6*p*x,p+3*x^2}");
    check("SubresultantPolynomials(x^2 - y^2, x - y, x)", //
        "{0,x-y}");
    check("SubresultantPolynomials(x^3 + 1, 7, x)", //
        "{343}");
    check("SubresultantPolynomials(x^3 + 1, 0, x)", //
        "{}");
    check("SubresultantPolynomials(x^2 + 1, x^3 + 1, x)", //
        "SubresultantPolynomials(1+x^2,1+x^3,x)");
    check("SubresultantPolynomials(x^2 + 0.5, x + 1, x)", //
        "SubresultantPolynomials(0.5+x^2,1+x,x)");
    check("SubresultantPolynomials(x^4 - 2, x^2 + 3*x - 1, x, Modulus -> 7)", //
        "{5,6+5*x,6+3*x+x^2}");
  }

  @Test
  public void testSubresultantPolynomialRemainders() {
    check("SubresultantPolynomialRemainders(x^5 - 3*x^3 + x - 4, 2*x^3 + x^2 - 1, x)", //
        "{-4+x-3*x^3+x^5,-1+x^2+2*x^3,43-6*x-15*x^2,-12+159*x,4771}");
    check("SubresultantPolynomialRemainders(x^6 + 2*x^3 - x + 5, x^4 - 3*x^2 + 2, x)", //
        "{5-x+2*x^3+x^6,2-3*x^2+x^4,1+x-7*x^2-2*x^3,1-5*x+39*x^2,451+46*x,5285}");
    check("SubresultantPolynomialRemainders(x^3 - 3*x + 2, x^2 - 5*x + 6, x)", //
        "{2-3*x+x^3,6-5*x+x^2,-28+16*x,80}");
    check("SubresultantPolynomialRemainders(x^3 + p*x + q, 3*x^2 + p, x)", //
        "{q+p*x+x^3,p+3*x^2,9*q+6*p*x,4*p^3+27*q^2}");
    check("SubresultantPolynomialRemainders(x^3 - 1, x - 1, x)", //
        "{-1+x^3,-1+x}");
    check("SubresultantPolynomialRemainders(x^3 + 1, 0, x)", //
        "{1+x^3,0}");
    check("SubresultantPolynomialRemainders(x + 1, x^2 + 1, x)", //
        "SubresultantPolynomialRemainders(1+x,1+x^2,x)");
    check("SubresultantPolynomialRemainders(x^5 - 3*x^3 + x - 4, 2*x^3 + x^2 - 1, x, Modulus -> 3)", //
        "{2+x+x^5,2+x^2+2*x^3,1}");
  }

  @Test
  public void testPowerSymmetricPolynomial() {
    check("PowerSymmetricPolynomial(4, {u, v, w})", //
        "u^4+v^4+w^4");
    check("PowerSymmetricPolynomial(2, {1, 5, 7})", //
        "75");
    check("PowerSymmetricPolynomial(m, {a, b})", //
        "a^m+b^m");
    check("PowerSymmetricPolynomial(0, {a, b, c, d})", //
        "4");
    check("PowerSymmetricPolynomial(3, {})", //
        "0");
    check("PowerSymmetricPolynomial(-2, {a, b})", //
        "PowerSymmetricPolynomial(-2,{a,b})");
    check("PowerSymmetricPolynomial({3, 1}, {{a, b}, {c, d}, {e, f}})", //
        "a^3*b+c^3*d+e^3*f");
    check("PowerSymmetricPolynomial({1, 2}, {{2, 3}, {4, 5}})", //
        "118");
    check("PowerSymmetricPolynomial(5)", //
        "PowerSymmetricPolynomial(5)");
  }

  @Test
  public void testAugmentedSymmetricPolynomial() {
    check("AugmentedSymmetricPolynomial({3, 1}, {u, v, w})", //
        "u^3*v+u*v^3+u^3*w+v^3*w+u*w^3+v*w^3");
    check("AugmentedSymmetricPolynomial({1, 1}, {p, q, r, s})", //
        "2*p*q+2*p*r+2*q*r+2*p*s+2*q*s+2*r*s");
    check("AugmentedSymmetricPolynomial({2}, {u, v})", //
        "u^2+v^2");
    check("AugmentedSymmetricPolynomial({1, 2, 3}, {u, v})", //
        "0");
    check("AugmentedSymmetricPolynomial({}, {u, v})", //
        "1");
    check("AugmentedSymmetricPolynomial({2, 1}, {2, 3, 5})", //
        "220");
    check("AugmentedSymmetricPolynomial({{1, 0}, {0, 1}}, {{a, b}, {c, d}})", //
        "b*c+a*d");
  }

  @Test
  public void testConjunction() {
    check("Conjunction(p || q, q)", //
        "p");
    check("Conjunction((p || q) && (!p || r), {p})", //
        "q&&r");
    check("Conjunction(p && q, {p, q})", //
        "False");
  }

  @Test
  public void testDisjunction() {
    check("Disjunction(p && q, q)", //
        "p");
    check("Disjunction(Xor(p, q, r), {q, r})", //
        "True");
    check("Disjunction(Implies(p, q), {p, q})", //
        "True");
  }

  @Test
  public void testUnateQ() {
    check("UnateQ(p || (q && r))", //
        "True");
    check("UnateQ(Xor(p, q))", //
        "False");
    check("UnateQ(!p && q)", //
        "False");
    check("UnateQ(!p && q, {q})", //
        "True");
    check("UnateQ(Implies(p, q), {p})", //
        "False");
    check("UnateQ(True)", //
        "True");
  }

  @Test
  public void testTensorExpand() {
    check("TensorExpand(TensorProduct(a + b, c))", //
        "ac+bc");
    check("TensorExpand(TensorProduct(a + b, c + d))", //
        "ac+ad+bc+bd");
    check("TensorExpand(Dot(a, b + c))", //
        "a.b+a.c");
    check("TensorExpand(Dot(2*a, b + 3*c))", //
        "2*a.b+6*a.c");
    check("TensorExpand(TensorWedge(a + b, c))", //
        "TensorWedge(a,c)+TensorWedge(b,c)");
    check("TensorExpand(x*TensorProduct(a, b + c) + y)", //
        "y+x*ab+x*ac");
    check("TensorExpand(TensorTranspose(TensorProduct(a + b, c), {2, 1}))", //
        "TensorTranspose(ac,{2,1})+TensorTranspose(bc,{2,1})");
    check("TensorExpand((a + b)*c)", //
        "a*c+b*c");
  }

  @Test
  public void testSimplify() {
    check("Simplify(Sin(x)^2 + Cos(x)^2 + Log(a*b), ExcludedForms -> {Log(_)})", //
        "1+Log(a*b)");
    check("Simplify((x^2 - 1)/(x - 1) + Sqrt(y^2), ExcludedForms -> Sqrt(_))", //
        "1+x+Sqrt(y^2)");
    check("Simplify(Sqrt(x^2) + Sqrt(y^2), x > 0 && y > 0, ExcludedForms -> Sqrt(y^2))", //
        "x+Sqrt(y^2)");
    check("Simplify(Sqrt(x^2) + Sqrt(y^2), x > 0 && y > 0)", //
        "x+y");
  }

  @Test
  public void testFullSimplify() {
    check("FullSimplify(Log(Exp(x)) + Log(Exp(y)), Element(x|y, Reals), ExcludedForms -> Log(Exp(y)))", //
        "x+Log(E^y)");
  }

  @Test
  public void testOptions() {
    check("Options(Simplify)", //
        "{Assumptions->True,ComplexityFunction->Automatic,ExcludedForms->{}}");
  }
}
