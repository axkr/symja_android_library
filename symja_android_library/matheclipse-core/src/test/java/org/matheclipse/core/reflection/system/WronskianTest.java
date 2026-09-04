package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests for the {@link Wronskian} built-in function. */
public class WronskianTest extends ExprEvaluatorTestCase {

  @Test
  public void testWronskianOfFunctions() {
    check("Wronskian({Exp(x), Exp(2*x)}, x)", //
        "E^(3*x)");
    check("Wronskian({Cos(x), Sin(x)}, x)", //
        "1");
    check("Wronskian({x, x^2}, x)", //
        "x^2");
    check("Wronskian({Cos(x), x*Cos(x)}, x)", //
        "Cos(x)^2");
    check("Wronskian({ChebyshevT(1, x), ChebyshevT(2, x)}, x)", //
        "1+2*x^2");
    check("Wronskian({3^x, x*3^x}, x)", //
        "3^(2*x)");

    // A determinant of rational functions is put over a common denominator, which is what shows
    // that the numerator cancels.
    check("Wronskian({1/x, 1/(x + 1)}, x)", //
        "1/(x^2*(1+x)^2)");

    // A single function is its own determinant, and the empty product is one.
    check("Wronskian({x^2}, x)", //
        "x^2");
    check("Wronskian({}, x)", //
        "1");
  }

  @Test
  public void testWronskianDetectsLinearDependence() {
    check("Wronskian({Exp(x), Exp(x + 3)}, x)", //
        "0");
    check("Wronskian({x^2, 3*x^2 + 5*x + 1, x^2 + 5*x + 1}, x)", //
        "0");
    check("Wronskian({1, x, x^2, (x - 3)*(x - 4)}, x)", //
        "0");
    check("Wronskian({1/x, 1/(x + 1), 1/(x*(x + 1))}, x)", //
        "0");
    check("Wronskian({Cos(x), Sin(x), Sin(x + Pi/3)}, x)", //
        "0");

    // Linear dependence is detected for undetermined functions too.
    check("Wronskian({f(x), c*f(x)}, x)", //
        "0");
  }

  @Test
  public void testWronskianOfUndeterminedFunctions() {
    check("Wronskian({f(x), g(x)}, x)", //
        "-g(x)*f'(x)+f(x)*g'(x)");

    // The definition is a determinant of the functions and their derivatives.
    check("Wronskian({f(x), g(x)}, x) == Det(Table(D({f(x), g(x)}, {x, m}), {m, 0, 1}))", //
        "True");
  }

  @Test
  public void testWronskianOfEquation() {
    // Abel's identity: the coefficient of the second highest derivative determines the Wronskian
    // of a basis, so the equation does not have to be solvable. Airy's equation has no first
    // derivative, so the Wronskian is constant.
    check("Wronskian(y''(x) - x*y(x) == 0, y, x)", //
        "1");

    check("Wronskian(y'''(x) - 5*y''(x) + 11*y(x) == 0, y, x)", //
        "E^(5*x)");
    check("Wronskian(y''(x) - x*y'(x) + y(x) == 0, y, x)", //
        "E^(x^2/2)");
    check("Wronskian(y''(x) + a*y'(x) + b*y(x) == 0, y, x)", //
        "E^(-a*x)");

    // Coefficients no solver understands are no obstacle, because they are never looked at.
    check("Wronskian(y''(x) + y'(x) + BesselJ(1, x)*y(x) == 0, y, x)", //
        "E^(-x)");
    check("Wronskian(y''(x) + Sinc(x)*y(x) == 0, y, x)", //
        "1");

    // Fourth order, the equation of the Kelvin functions.
    check("Wronskian(x^4*y''''(x) + 2*x^3*y'''(x) - (1 + 2*v^2)*(x^2*y''(x) - x*y'(x))"
        + " + (v^4 - 4*v^2 + x^4)*y(x) == 0, y, x)", //
        "1/x^2");

    // The dependent variable may be given applied to the variable as well.
    check("Wronskian(y''(x) - x*y(x) == 0, y(x), x)", //
        "1");
  }

  @Test
  public void testWronskianOfSystem() {
    // Liouville's formula: the trace of the coefficient matrix determines the Wronskian.
    check("Wronskian({y'(x) == y(x) - z(x), z'(x) == y(x) + z(x)}, {y, z}, x)", //
        "E^(2*x)");

    // The order of the dependent variables is the caller's choice and does not change the trace.
    check("Wronskian({y'(x) == v(x), v'(x) == z(x), z'(x) == y(x) - z(x)}, {v, y, z}, x)", //
        "E^(-x)");
    check("Wronskian({y'(x) == v(x), v'(x) == z(x), z'(x) == y(x) - z(x)}, {y, v, z}, x)", //
        "E^(-x)");
  }

  @Test
  public void testWronskianDeclines() {
    // Not linear in the dependent variable, so there is no basis of solutions.
    check("Wronskian(y'(x)^2 == 0, y, x)", //
        "Wronskian(y'(x)^2==0,y,x)");
    check("Wronskian({y'(x) == y(x)*z(x), z'(x) == y(x)}, {y, z}, x)", //
        "Wronskian({y'(x)==y(x)*z(x),z'(x)==y(x)},{y,z},x)");

    // No derivative at all.
    check("Wronskian(y(x) == 0, y, x)", //
        "Wronskian(y(x)==0,y,x)");

    // Wronskian: 3 is not a valid variable.
    check("Wronskian({x, x^2}, 3)", //
        "Wronskian({x,x^2},3)");

    // Wronskian called with 1 argument; 2 or 3 arguments are expected.
    check("Wronskian({x, x^2})", //
        "Wronskian({x,x^2})");
  }

  @Test
  public void testWronskianAgreesWithDSolve() {
    // The Wronskian of a solution basis agrees with the one taken from the equation, up to the
    // constant factor which Abel's identity leaves open.
    check("Wronskian(Coefficient(y(x) /. DSolve(y''(x) - 3*y'(x) + 2*y(x) == 0, y(x), x)[[1]],"
        + " {C(1), C(2)}), x) / Wronskian(y''(x) - 3*y'(x) + 2*y(x) == 0, y, x) // Simplify", //
        "1");
  }
}
