package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class BellYTest extends ExprEvaluatorTestCase {

  @Test
  public void testBellY() {

    // display error messages: Polynomial degree 2147483647 exceeded
    check("BellY(2147483647,3,{x,-3,-1/2})", //
        "BellY(2147483647,3,{x,-3,-1/2})");
    // message Polynomial degree 1009 exceeded
    check("BellY(1009,19,{-1/2,-2,3})", //
        "BellY(1009,19,{-1/2,-2,3})");

    check("BellY(2,1,{1/2,0})", //
        "0");
    check("BellY(5, 2, {})", //
        "0");
    check("BellY(5, 2, {1})", //
        "0");
    check("BellY(5, 2, {1,2})", //
        "0");
    check("BellY(5, 2, {1,2,3})", //
        "60");
    check("BellY(5, 8, {1,2,3})", //
        "0");

    // https://en.wikipedia.org/wiki/Bell_polynomials
    check("BellY(6, 2, {x1, x2, x3, x4, x5})", //
        "10*x3^2+15*x2*x4+6*x1*x5");
    check("BellY(6, 3, {x1, x2, x3, x4})", //
        "15*x2^3+60*x1*x2*x3+15*x1^2*x4");

    check("BellY(4, 2, {x1, x2, x3})", //
        "3*x2^2+4*x1*x3");
    check("With({n = 7, k = 2}, BellY(n, k, Array(x, n)))", //
        "35*x(3)*x(4)+21*x(2)*x(5)+7*x(1)*x(6)");
  }

  @Test
  public void testBellYGeneralizedMatrix() {
    // BellY(m) - generalized Bell polynomial of a matrix m
    check("BellY(Partition(Range(25), 5))", //
        "68327950346100");
    check("BellY({{1,0,0},{2,3,0},{4,5,6}})", //
        "0");

    check("BellY({{x1, x2, x3}, {y1, y2, y3}, {z1, z2, z3}})", //
        "3*x2*x3*y1*(x3^2*y2+x2*y3)+x2^3*x3^3*z1+x1*(3*x3*y2*y3+x3^3*z2+x2*z3)");
  }

  @Test
  public void testBellYGeneralized3Args() {
    check("BellY(4, 2, {x1, x2, y1})", //
        "3*x2^2+4*x1*y1");

    check("BellY(4, 2, {{x1, x2, x3}, {y1, y2, y3}})", //
        "3*(x2^2*x3^2*y1+x1*(x3^2*y2+x2*y3))^2+4*x1*x2*x3*(3*x1*x3*y2*y3+3*x2*x3*y1*(x3^2*y2+x2*y3))");

    check("BellY(4, 3, {{x11, x12},{x21,x22},{x31,x32}, {x41, x42}})", //
        "6*x11^2*x12^2*(x12^2*x21+x11*x22)");
  }
}
