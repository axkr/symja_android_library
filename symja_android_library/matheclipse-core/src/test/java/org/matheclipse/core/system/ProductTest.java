package org.matheclipse.core.system;

import org.junit.Test;

public class ProductTest extends ExprEvaluatorTestCase {

  @Test
  public void testProduct001() {
    // Config.MAX_BIT_LENGTH = Integer.MAX_VALUE;
    // check("AbsoluteTiming(Product(i,{i,1,10^6});)", //
    // "");

    // message Product: Argument {} at position 2 does not have the correct form for an iterator.
    check("Product(-3/2,\"\",{-1/2,-2,3},-1+I,{0,0,0,0})", //
        "Product(-3/2,,{-1/2,-2,3},-1+I,{0,0,0,0})");

    check("Product(i^2,x)", //
        "(i^2)^(-1+x)");
    check("Product(i^2,Indeterminate)", //
        "Product(i^2,Indeterminate)");
    check("Product(f(x), {k,n, n-1})", //
        "1");
    check("Product(f(x), {k,3, 1/2})", //
        "1");

    // prints RecursionLimitExeceeded
    check("Product(f(x), {x, x, x+1})", //
        "Product(f(x),{x,x,x+1})");

    check("Product(f(x), {x, x, x})", //
        "f(x)");
    check("Product(f(x), {x, a, a+1})", //
        "f(a)*f(1+a)");
  }

  @Test
  public void testProduct002() {
    check("Product(k^p, {k,1,k-1})", //
        "((-1+k)!)^p");
    check("Product(k^p, k)", //
        "((-1+k)!)^p");
    check("Product(k^3, {k, 1, n})", //
        "(n!)^3");
  }

  @Test
  public void testProduct003() {
    check("Product(0, {k, a, Infinity})", //
        "0");
    check("Product(1, {k, a, Infinity})", //
        "1");
    check("Product(42, {k, a, Infinity})", //
        "Infinity");
    // {k,a,n} assumes a<=k<=n
    check("Product(2, {k, a, n})", //
        "2^(1-a+n)");
  }

  @Test
  public void testProduct004() {
    // {k,1,n} assumes 1<=k<=n
    check("Product(k^3, {k, 1, n})", //
        "(n!)^3");

    check("Product(i^2, {i,11,2})", //
        "1");
    check("Product(i^2, {i,11,2,-1})", //
        "1593350922240000");
    check("Product(i^2, {i,2,11})", //
        "1593350922240000");
    check("Product(i^2, {i,m,n})", //
        "Pochhammer(m,1-m+n)^2");
    check("Product(i^2, {i,k,k+j})", //
        "Pochhammer(k,1+j)^2");

    check("Product(a, {a, 1, 5})", //
        "120");
    check("Product(f(a), {a, 1, 5})", //
        "f(1)*f(2)*f(3)*f(4)*f(5)");
    check("Product(a^2, {a, 4})", //
        "576");
    check("Product(a + b, {a, 1, 2}, {b, 1, 3})", //
        "1440");
  }

  @Test
  public void testProduct005() {

    check("Product(k, {k, 1, 10})", //
        "3628800");
    check("10!", //
        "3628800");
    check("Product(x^k, {k, 2, 20, 2})", //
        "x^110");
    check("Product(2 ^ i, {i, 1, n})", //
        "2^(1/2*n*(1+n))");
    check("Product(k, {k, 3, n})", //
        "n!/2");
    check("Product(k, {k, 10, n})", //
        "n!/362880");

    check("primorial(0) = 1", //
        "1");
    check("primorial(n_Integer) := Product(Prime(k), {k, 1, n})", //
        "");
    check("primorial(12)", //
        "7420738134810");
  }

  @Test
  public void testProduct006() {
    check("Product(i^2 - i + 10 ,{i,1,10})", //
        "1426481971200000");
    check("Product(a^i, {i, n})", //
        "a^(1/2*n*(1+n))");
    check("Product(c, {j, 2}, {i, 1, j})", //
        "c^3");
    check("Product(c, {i, 1, j}, {j, 2})", //
        "c^(2*j)");
    check("Product(c, {i, 1, j}, {j, 1, 2})", //
        "c^(2*j)");
    check("Product(c, {i, 1, n})", //
        "c^n");
    check("Product(c+n, {i, 1, n})", //
        "(c+n)^n");
    check("Product(c+n, {i, 0, n})", //
        "(c+n)^(1+n)");
    check("n!", //
        "n!");
    check("$prod(x_,{x_,1,m_}) := m!; $prod(i0, {i0, 1, n0})", //
        "n0!");
    check("Product(i0, {i0, 1, n0})", //
        "n0!");
    check("Product(i^2, {i, 1, n})", //
        "(n!)^2");
    check("Product(i0^2, {i0, 0, n0})", //
        "0");
    check("Product(4*i0^2, {i0, 0, n0})", //
        "0");
    check("Product(i0^3, {i0, 1, n0})", //
        "(n0!)^3");
    check("Product(i0^3+p^2, {i0, 1, n0})", //
        "Product(i0^3+p^2,{i0,1,n0})");
    check("Product(p, {i0, 1, n0})", //
        "p^n0");
    check("Product(p+q, {i0, 1, n0})", //
        "(p+q)^n0");
    check("Product(p, {i0, 0, n0})", //
        "p^(1+n0)");
    check("Product(4, {i0, 0, n0})", //
        "4^(1+n0)");
  }

  @Test
  public void testProduct007() {
    check("Product(c, {i, 1, n}, {j, 1, n})", //
        "(c^n)^n");
    check("Product(c, {j, 1, n}, {i, 1, j})", //
        "c^(1/2*n*(1+n))");
    check("Product(f(i, j), {i, 1, 3}, {j, 1, 3})", //
        "f(1,1)*f(1,2)*f(1,3)*f(2,1)*f(2,2)*f(2,3)*f(3,1)*f(3,2)*f(3,3)");
    check("Product(f(i, j), {i, 1, 3, 2}, {j, 1, 3, 1/2})", //
        "f(1,1)*f(1,3/2)*f(1,2)*f(1,5/2)*f(1,3)*f(3,1)*f(3,3/2)*f(3,2)*f(3,5/2)*f(3,3)");
    // check("Product(2^(j + i0), {i0, 1, p}, {j, 1, i0})", "");
  }

  @Test
  public void testProduct008() {
    check("Product(x,{x,10})", //
        "3628800");
    check("Product(x,{x,0,1})", //
        "0");
    check("Product(x,{x,0,10,2})", //
        "0");
    check("Product(x,{x,1,1})", //
        "1");
    check("Product(x,{x,1,5})", //
        "120");
    check("Product(x,{x,3,2,-1})", //
        "6");
    check("Product(x,{x,10,3,-4})", //
        "60");
    // use default value "1" for iterator with invalid range
    check("Product(x,{x,1,0})", //
        "1");
    check("Product(x,{x,2,3,-1})", //
        "1");
  }

  @Test
  public void testProduct009() {
    check("Product(x,{x,0,-1,2})", //
        "1");
  }

  @Test
  public void testProduct010() {
    check("Product(x,{a,10,z})", //
        "x^(-9+z)");
    check("Product(x,{a,b,c})", //
        "x^(1-b+c)");
  }

  @Test
  public void testProduct011() {
    check("Product(i^(x),{i,1,n})", //
        "(n!)^x");
  }

  @Test
  public void testProduct012() {
    // https://docs.sympy.org/latest/modules/simplify/fu.html#sympy.simplify.fu.TRmorrie
    check("Product(Cos(x*2^i), {i, 0, 3})", //
        "1/4*Csc(x)*Sin(4*x)");
    check("Product(Cos(x*2^i), {i, 0, k})", //
        "2^(1-k)*Csc(x)*Sin(x/2^(1-k))");
  }
}
