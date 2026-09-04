package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests for the {@link Casoratian} built-in function, the discrete Wronskian. */
public class CasoratianTest extends ExprEvaluatorTestCase {

  @Test
  public void testCasoratianOfSequences() {
    check("Casoratian({2^n, n*2^n}, n)", //
        "2^(1+2*n)");
    check("Casoratian({1, n}, n)", //
        "1");
    check("Casoratian({1, n, n^2}, n)", //
        "2");
    check("Casoratian({1, n, n^2, n^3}, n)", //
        "12");

    check("Casoratian({n, n^2}, n)", //
        "n*(1+n)");
    check("Casoratian({1/n, 1/(n + 1)}, n)", //
        "1/(n*(1+n)^2*(2+n))");
    check("Casoratian({3^n, n*3^n}, n)", //
        "3^(1+2*n)");

    // Dependent only when a vanishes.
    check("Casoratian({n^2, n^2 + a}, n)", //
        "a*(-1-2*n)");

    // A single sequence is its own determinant, and the empty product is one.
    check("Casoratian({n!}, n)", //
        "n!");
    check("Casoratian({}, n)", //
        "1");
  }

  @Test
  public void testCasoratianDetectsLinearDependence() {
    // The shifted sequence is a multiple of the first, though Det leaves that in a form which
    // only the reduction of the determinant makes visible.
    check("Casoratian({2^n, 2^(n+1)}, n)", //
        "0");
    check("Casoratian({1, n, 2*n + 3}, n)", //
        "0");
    check("Casoratian({f(n), c*f(n)}, n)", //
        "0");
    check("Casoratian({n^2, 3*n^2 + 5*n + 1, n^2 + 5*n + 1}, n)", //
        "0");

    // The third sequence is the difference of the first two in partial fractions.
    check("Casoratian({1/n, 1/(n + 1), 1/(n*(n + 1))}, n)", //
        "0");
  }

  @Test
  public void testCasoratianOfUndeterminedSequences() {
    check("Casoratian({f(n), g(n)}, n)", //
        "-f(1+n)*g(n)+f(n)*g(1+n)");

    // The definition is the determinant of the sequences under the shifts 0 .. m-1.
    check("Casoratian({f(n), g(n)}, n) == Det({{f(n), f(n+1)}, {g(n), g(n+1)}})", //
        "True");
  }

  @Test
  public void testCasoratianOfEquation() {
    // C(n+1) == (-1)^m*a(0)/a(m)*C(n), normalized to C(0) == 1. The solutions of this equation
    // are 1 and 2^n, whose Casoratian is Det({{1,1},{2^n,2^(n+1)}}) == 2^n.
    check("Casoratian(y(n+2) - 3*y(n+1) + 2*y(n) == 0, y, n)", //
        "2^n");

    // The Fibonacci recurrence: the ratio is -1, so the Casoratian alternates in sign.
    check("Casoratian(y(n+2) - y(n+1) - y(n) == 0, y, n)", //
        "(-1)^n");

    check("Casoratian(y(n+1) - 2*y(n) == 0, y, n)", //
        "2^n");
    check("Casoratian(y(n+2) + a*y(n+1) + b*y(n) == 0, y, n)", //
        "b^n");

    // A coefficient depending on n leaves a product, which is evaluated when it closes.
    check("Casoratian(y(n+2) - (n+1)*y(n) == 0, y, n)", //
        "(-1)^n*n!");

    // Third order: the ratio is (-1)^3*11 == -11.
    check("Casoratian(y(n+3) - 5*y(n+1+1) + 11*y(n) == 0, y, n)", //
        "(-11)^n");

    // Given as an equation with a right hand side rather than in the form which is zero.
    check("Casoratian(y(n+2) == y(n+1) + y(n), y, n)", //
        "(-1)^n");

    // A coefficient which is a polynomial in n leaves a factorial.
    check("Casoratian(y(n+2) - (n + 5)*y(n) == 0, y, n)", //
        "1/24*(-1)^n*(4+n)!");

    // The dependent variable may be given applied to the variable as well.
    check("Casoratian(y(n+2) - 3*y(n+1) + 2*y(n) == 0, y(n), n)", //
        "2^n");
  }

  @Test
  public void testCasoratianOfSystem() {
    check("Casoratian({y(n+1) == 2*y(n) + z(n), z(n+1) == y(n) + 2*z(n)}, {y, z}, n)", //
        "3^n");

    // The order of the dependent variables does not change the determinant of the system.
    check("Casoratian({y(n+1) == 2*y(n) + z(n), z(n+1) == y(n) + 2*z(n)}, {z, y}, n)", //
        "3^n");
  }

  @Test
  public void testCasoratianDeclines() {
    // Not linear in the dependent variable, so there is no basis of solutions.
    check("Casoratian(y(n)^2 == 0, y, n)", //
        "Casoratian(y(n)^2==0,y,n)");
    check("Casoratian({y(n+1) == y(n)*z(n), z(n+1) == y(n)}, {y, z}, n)", //
        "Casoratian({y(1+n)==y(n)*z(n),z(1+n)==y(n)},{y,z},n)");

    // No shift at all.
    check("Casoratian(y(n) == 0, y, n)", //
        "Casoratian(y(n)==0,y,n)");

    // Casoratian: 3 is not a valid variable.
    check("Casoratian({n, n^2}, 3)", //
        "Casoratian({n,n^2},3)");

    // Casoratian called with 1 argument; 2 or 3 arguments are expected.
    check("Casoratian({n, n^2})", //
        "Casoratian({n,n^2})");
  }

  @Test
  public void testCasoratianAgreesWithRSolve() {
    // The Casoratian taken from a basis of solutions agrees with the one taken from the equation.
    check("Casoratian({1, 2^n}, n) == Casoratian(y(n+2) - 3*y(n+1) + 2*y(n) == 0, y, n)", //
        "True");

    // The Fibonacci numbers and the Lucas numbers are a basis of the Fibonacci recurrence, so
    // their Casoratian is the one the equation gives, times its value at n == 0.
    check("Table(Casoratian({Fibonacci(k), LucasL(k)}, k) /. k -> n, {n, 0, 5})", //
        "{-2,2,-2,2,-2,2}");
    check("Table(-2 * (Casoratian(y(n+2) - y(n+1) - y(n) == 0, y, n) /. n -> m), {m, 0, 5})", //
        "{-2,2,-2,2,-2,2}");
  }
}
