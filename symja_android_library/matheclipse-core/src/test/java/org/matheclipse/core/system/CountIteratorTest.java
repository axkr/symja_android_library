package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Iterators which are a count <code>{c}</code> in <code>Sum</code> and <code>Product</code>, and
 * iterators with a step and several iterators in <code>NSum</code> and <code>NProduct</code>; the
 * one-element specification <code>{x}</code> of <code>D</code>; integrals over an interval without
 * width.
 */
public class CountIteratorTest extends ExprEvaluatorTestCase {

  @Test
  public void testDOneElementList() {
    check("D(x^4*y^3, {x})", //
        "4*x^3*y^3");
    check("D(v(x, t), {t})", //
        "Derivative(0,1)[v][x,t]");
    check("D(x^4*y^3, {x}, {y})", //
        "12*x^3*y^2");
    check("D(x^2, {x, y, z})", //
        "D(x^2,{x,y,z})");
    // the gradient is another thing
    check("D(x^4*y^3, {{x, y}})", //
        "{4*x^3*y^3,3*x^4*y^2}");
  }

  @Test
  public void testIntegrateLimits() {
    check("Integrate(g(k), {k, y, y})", //
        "0");
    // the limits of an integral which stays are evaluated
    check("Integrate(g(k), {k, 0*a + y, 7})", //
        "Integrate(g(k),{k,y,7})");
    check("Integrate(h(k, y), {k, a, b}, {y, c, c})", //
        "0");
  }

  @Test
  public void testSumProductCount() {
    check("Sum(a, {4})", //
        "4*a");
    check("Sum(a, {7/2})", //
        "3*a");
    check("Sum(a, {-3})", //
        "0");
    // a symbol: the indefinite sum and product
    check("Sum(n^3, {n})", //
        "1/4*(n^2-2*n^3+n^4)");
    check("Product(b, {n})", //
        "b^(-1+n)");
    check("Product(n, {n})", //
        "(-1+n)!");
    check("Product(n, n)", //
        "(-1+n)!");
    check("Block({k = 0}, {Product(k++; 3, {i, 1, 4}), k})", //
        "{81,4}");
  }

  @Test
  public void testNSumCountIsNoIterator() {
    // a count is an iterator of Sum and Product only
    check("NSum(1/3, {5})", //
        "NSum(1/3,{5})");
    check("NProduct(3, {4})", //
        "NProduct(3,{4})");
    check("NSum(1/k^2, {k, 1, Infinity}, {3})", //
        "NSum(1/k^2,{k,1,Infinity},{3})");
  }

  @Test
  public void testNSumStep() {
    check("NSum(1/k^3, {k, 2, 20, 3})", //
        "0.136397");
    // no step downwards
    check("NSum(1/k, {k, 9, 2, -2})", //
        "NSum(1/k,{k,9,2,-2})");
    check("NSum(1/k, {k, 5, 1, 2})", //
        "0.0");
    check("Abs(NSum(1/k^2, {k, 1, Infinity, 2}) - Pi^2/8) < 10^-10", //
        "True");
  }

  @Test
  public void testNSumFiniteNested() {
    check("NSum(i*j, {i, 1, 3}, {j, 1, 2})", //
        "18.0");
    check("NSum(i + j, {i, 1, 3}, {j, 1, i})", //
        "24.0");
    check("NProduct(i + j, {i, 1, 3}, {j, 1, 2})", //
        "1440.0");
  }

  @Test
  public void testNSumFinitePrecision() {
    check("NSum(1/k, {k, 1, 3000}, WorkingPrecision -> 30)", //
        "8.58374988995918711434379209126");
    check("NSum(1/k, {k, 5, 1}, WorkingPrecision -> 30)", //
        "0");
  }

  @Test
  public void testHurwitzZetaLargeInteger() {
    check("HurwitzZeta(2, 7)", //
        "-5369/3600+Pi^2/6");
    check("Head(HurwitzZeta(2, 6001))", //
        "Plus");
    // no sum of 30000 exact fractions
    check("Head(HurwitzZeta(2, 30001))", //
        "HurwitzZeta");
    check("N(HurwitzZeta(2, 10^5 + 1), 25)", //
        "0.000009999950000166666666663333");
  }
}
