package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class RSolveTest {

  private ExprEvaluator util;

  @BeforeEach
  public void setUp() {
    // Initialize the standard Symja expression evaluator
    util = new ExprEvaluator(false, (short) 100);
  }

  /**
   * Helper method to evaluate an input string and assert it matches the expected canonical output.
   */
  private void check(String input, String expected) {
    // Reset constant counter before each evaluation so C(1), C(2) are deterministic
    util.getEvalEngine().setConstantCounter(1);

    IExpr result = util.eval(input);
    assertEquals(expected, result.toString());
  }

  @Test
  public void testFirstOrderConstantCoefficients() {
    check("RSolve(a(n+1) - 2*a(n) == 1, a(n), n)", //
        "{{a(n)->-1+2^n+C(1)/2^(1-n)}}");

    // a(n+1) - 2*a(n) = 0 => a(n) = C(1) * 2^n
    check("RSolve(a(n+1) - 2*a(n) == 0, a(n), n)", //
        "{{a(n)->2^n*C(1)}}");

    // a(n+1) == 3*a(n) => a(n) = C(1) * 3^n
    check("RSolve(a(n+1) == 3*a(n), a(n), n)", //
        "{{a(n)->3^n*C(1)}}");
  }

  @Test
  public void testSecondOrderConstantCoefficients() {
    // a(n+2) - 3*a(n+1) + 2*a(n) == 0
    // Roots of x^2 - 3x + 2 = 0 are 1 and 2.
    // Solution: C(1)*1^n + C(2)*2^n => C(1) + 2^n*C(2)
    check("RSolve(a(n+2) - 3*a(n+1) + 2*a(n) == 0, a(n), n)", //
        "{{a(n)->C(1)+2^n*C(2)}}");
  }

  @Test
  public void testInitialConditions() {
    // a(n+1) == 2*a(n) with a(0) == 3 => a(n) = 3 * 2^n
    check("RSolve({a(n+1) == 2*a(n), a(0) == 3}, a(n), n)", //
        "{{a(n)->3*2^n}}");

    // Second order with boundary conditions
    // a(n+2) - 3*a(n+1) + 2*a(n) == 0, a(0)==0, a(1)==1
    // General: C(1) + C(2)*2^n.
    // a(0)=0 => C(1) + C(2) = 0
    // a(1)=1 => C(1) + 2*C(2) = 1 => C(2)=1, C(1)=-1
    // Solution: -1 + 2^n
    check("RSolve({a(n+2) - 3*a(n+1) + 2*a(n) == 0, a(0) == 0, a(1) == 1}, a(n), n)", //
        "{{a(n)->-1+2^n}}");
  }

  @Test
  public void testPureFunctionReturn() {
    // Requesting `a` instead of `a(n)` should return a pure function
    check("RSolve(a(n+1) == 2*a(n), a, n)", //
        "{{a->Function({n},2^n*C(1))}}");
    check("RSolve({a(n+1) == 2*a(n), a(0) == 3}, a, n)", //
        "{{a->Function({n},3*2^n)}}");
  }

  @Test
  public void testFirstOrderVariableCoefficients() {
    // a(n+1) == (n+1)*a(n)
    check("RSolve(a(n+1) == (n+1)*a(n), a(n), n)", //
        "{{a(n)->C(1)*n!}}");
  }

  @Test
  public void testNonHomogeneousFirstOrder() {
    // a(n+1) - a(n) = 1 => K=1, q(K)=1, p(K)=1
    // Particular part should evaluate to a Sum.
    // a(n) = C(1) + Sum(1, {K, 1, n-1}) = C(1) + n - 1
    check("RSolve({a(n+1) - a(n) == 1, a(0) == 0}, a(n), n)", //
        "{{a(n)->n}}");
  }

  @Test
  public void testInvalidInputs() {
    // Missing arguments
    check("RSolve(a(n+1) == a(n), a(n))", //
        "RSolve(a(1+n)==a(n),a(n))");

    // Non-linear recurrence (currently unsupported by this specific implementation, should return
    // NIL/unevaluated)
    check("RSolve(a(n+1) == a(n)^2, a(n), n)", //
        "RSolve(a(1+n)==a(n)^2,a(n),n)");

    // No shift detected
    check("RSolve(a(n) == a(n), a(n), n)", //
        "RSolve(True,a(n),n)");
  }


  @Test
  public void testSqrt() {
    check("RSolve(a(n)==Sqrt(n)*a(Sqrt(n))+n, a, n)", //
        "{{a->Function({n},n*(C(1)/E^2+Log(Log(n))/Log(2)))}}");
  }

  // Trigonometric Canonicalization
  @Test
  public void testTrigonometricCanonicalization() {
    // a(n+1) - a(n) = Sin(n)
    // The engine should automatically convert Sin(n) to complex exponentials
    // (I/2*E^(-I*n) - I/2*E^(I*n)) and solve it as a non-homogeneous linear equation.
    // This expects the canonicalized rational/exponential particular solution.
    check("RSolve(a(n+1) - a(n) == Sin(n), a(n), n)", //
        "{{a(n)->(-I*E^(I+I*n))/(-2*E^I+2*E^(I*2))+I/(2/E^(I-I*n)-2*E^(I*n))+C(1)}}");

  }

  // Exponential Generating Functions (EGF)
  @Test
  public void testMethodEGFPolynomialCoefficients() {
    // a(n+1) == (n+1) * a(n)
    // Linear solvers fail here because the shift coefficient is a variable (n+1).
    // The EGF method maps this to y'(x) = y(x), yields y(x) = C e^x,
    // and extracts the coefficient n! * C(1).
    check("RSolve(a(n+1) == (n+1)*a(n), a(n), n)", //
        "{{a(n)->C(1)*n!}}");
  }

  // Hypergeometric / Pochhammer Mapping
  @Test
  public void testHypergeometricGammaMapping() {
    // a(n+1) == (n + 1/2) * a(n)
    // The symbolic product evaluator should intercept (K + 1/2) and map it
    // strictly to the ratio of Gamma functions: Gamma(n + 1/2) / Gamma(1/2).
    check("RSolve(a(n+1) == (n + 1/2)*a(n), a(n), n)", //
        // "{{a(n)->C(1)*Pochhammer(3/2,-1+n)}}");
        "{{a(n)->C(1)*Gamma(1/2+n)}}");

    // a(n+1) == 3*(n + 2) * a(n)
    // Tests A != 1 scaling factor: A^n * Gamma(...)
    check("RSolve(a(n+1) == 3*(n + 2)*a(n), a(n), n)", //
        // "{{a(n)->3^(1+n)*C(1)*Pochhammer(1,1+n)}}");
        "{{a(n)->(C(1)*(1+n)!)/(2*3^(1-n))}}");
  }

  // Systems of Recurrence Equations
  @Test
  public void testSystemsOfRecurrences() {
    // Coupled system:
    // a(n+1) == b(n)
    // b(n+1) == a(n)
    // With boundary conditions a(0)=1, b(0)=0.
    // Solution involves alternating sequences 1/2(1 + (-1)^n) and 1/2(1 - (-1)^n).
    check("RSolve({a(n+1) == b(n), b(n+1) == a(n), a(0)==1, b(0)==0}, {a(n), b(n)}, n)", //
        "{{a(n)->1/2*(1+(-1)^n),b(n)->1/2*(1-(-1)^n)}}");
  }

  // ==========================================================
  // Generating Functions (GF) with Boundary Injection
  // ==========================================================
  @Test
  public void testMethodGFWithBoundaryInjection() {
    // Fibonacci recurrence: a(n+2) - a(n+1) - a(n) == 0, a(0)==0, a(1)==1
    // The GF transform immediately substitutes a(0) and a(1) into z^2*G(z) - z^2*a(0) - z*a(1),
    // cleanly resolving to G(z) = z / (z^2 - z - 1) before InverseZTransform applies.

    // Exact canonical format depends on InverseZTransform implementation,
    // but it should successfully resolve to the Binet formula involving Sqrt(5).
    check("RSolve({a(n+2) - a(n+1) - a(n) == 0, a(0)==0, a(1)==1}, a(n), n)", //
        "{{a(n)->Fibonacci(n)}}");
  }

  // ==========================================================
  // EDGE CASES & FALLBACKS
  // ==========================================================
  @Test
  public void testPureFunctionReturnSystem() {
    // Requesting `a` instead of `a(n)` should correctly wrap the final output in Function({n}, ...)
    check("RSolve(a(n+1) == 2*a(n), a, n)", //
        "{{a->Function({n},2^n*C(1))}}");
  }

  // ====================================================================================
  // 1. Logarithmic Transformation (Divide and Conquer Recurrences)
  // ====================================================================================
  @Test
  public void testRSolveLogarithmicTransform() {
    // Tests the interception of fractional index shifts (Sqrt(n))
    // Verifies the n = E^(b^m) double-exponential substitution and global Product exponent
    // summation
    check("RSolve(a(n) == Sqrt(n)*a(Sqrt(n)) + n, a, n)", //
        "{{a->Function({n},n*(C(1)/E^2+Log(Log(n))/Log(2)))}}");

    // Tests a different fractional power: a(n) = n^(1/3) * a(n^(1/3)) + n
    check("RSolve(a(n) == n^(1/3)*a(n^(1/3)) + n, a, n)", //
        "RSolve(a(n)==n+n^(1/3)*a(n^(1/3)),a,n)");
  }

  // ====================================================================================
  // 2. Binet Formula Recognition (Fibonacci Sequences)
  // ====================================================================================
  @Test
  public void testRSolveBinetFibonacci() {
    // Standard Fibonacci Sequence with boundary conditions
    // Verifies that fractional Golden Ratio powers automatically collapse into Fibonacci(n)
    check("RSolve({a(n+2) - a(n+1) - a(n) == 0, a(0)==0, a(1)==1}, a(n), n)", //
        "{{a(n)->Fibonacci(n)}}");

    // Shifted Fibonacci Sequence (Lucas-style initial conditions mapping to shifted Fibonaccis)
    check("RSolve({a(n+2) - a(n+1) - a(n) == 0, a(0)==1, a(1)==1}, a(n), n)", //
        "{{a(n)->1/2*(Fibonacci(n)+LucasL(n))}}");

    // Unbound Fibonacci (General Solution)
    // Verifies the identity mapping: phi^n -> F_{n-1} + phi*F_n
    check("RSolve(a(n+2) - a(n+1) - a(n) == 0, a(n), n)", //
        "{{a(n)->C(1)*Fibonacci(n)+C(2)*LucasL(n)}}");
  }

  // ====================================================================================
  // 3. Holonomic Sequences (Discrete Reduction of Order)
  // ====================================================================================
  @Test
  public void testRSolveReductionOfOrder() {
    // 2nd-Order Holonomic Equation: a(n+2) - (n+2)*a(n+1) + (n+1)*a(n) == 0
    // P = 1, Q = -(n+2), R = n+1. Sum = 0, so y1(n) = 1 is a fundamental solution.
    // The solver reduces it to a 1st order equation and evaluates the Product/Sum natively.
    check("RSolve(a(n+2) - (n+2)*a(n+1) + (n+1)*a(n) == 0, a(n), n)", //
        "{{a(n)->C(2)+(-1)^n*C(1)*Gamma(1+n)*Subfactorial(-1-n)}}");

    // Alternating Sign Heuristic: a(n+2) + (n-1)*a(n+1) - n*a(n) == 0
    // P = 1, Q = n-1, R = -n. P - Q + R = 0, so y1(n) = (-1)^n is a fundamental solution.
    check("RSolve(a(n+2) + (n-1)*a(n+1) - n*a(n) == 0, a(n), n)", //
        "{{a(n)->C(2)+(-1)^n*E*C(1)*ExpIntegralE(n,1)*Gamma(n)}}");
  }

  // ====================================================================================
  // 4. First-Order Linear Particular Solutions (Constant Absorption & Bounds)
  // ====================================================================================
  @Test
  public void testRSolveFirstOrderCanonicalBounds() {
    // Verifies the lower bound offset (K=0) in the particular solution sum
    // mathematically maps exactly to the expected 2^n - 1 (without fractional integration offsets)
    check("RSolve(a(n+1) - 2*a(n) == 1, a(n), n)", //
        "{{a(n)->-1+2^n+C(1)/2^(1-n)}}");

    // Verifies fractional Pochhammer index mapping and fraction absorption
    check("RSolve(a(n+1) == 3*(n + 2)*a(n), a(n), n)", //
        // "{{a(n)->3^(1+n)*C(1)*Pochhammer(1,1+n)}}");
        "{{a(n)->(C(1)*(1+n)!)/(2*3^(1-n))}}");

    check("RSolve(a(n+1) == (n + 1/2)*a(n), a(n), n)", //
        // "{{a(n)->C(1)*Pochhammer(3/2,-1+n)}}");
        "{{a(n)->C(1)*Gamma(1/2+n)}}");
  }

  @Test
  public void testRSolveValue001() {
    // Basic First-Order Recurrence
    check("RSolveValue(a(n+1) - 2*a(n) == 0, a(n), n)", //
        "2^n*C(1)");

    // Recurrence with Initial Conditions
    check("RSolveValue({a(n+1) == 3*a(n), a(0) == 5}, a(n), n)", //
        "5*3^n");
  }

  @Test
  public void testRSolveValue002() {
    // Binet Formula Recognition (Fibonacci)
    check("RSolveValue({a(n+2) - a(n+1) - a(n) == 0, a(0)==0, a(1)==1}, a(n), n) ", //
        "Fibonacci(n)");

    // Evaluating an arbitrary expression
    // Solves to 5*3^n, then divides by 5
    check("RSolveValue({a(n+1) == 3*a(n), a(0) == 5}, a(n) / 5, n)", //
        "3^n");
  }

  @Test
  public void testRSolveValue003() {
    // System of Recurrences
    check("RSolve({a(n+1) == 2*a(n) + b(n), b(n+1) == a(n) + 2*b(n)}, {a,b}, n)", //
        "{{a->Function({n},(1/2+3^n/2)*C(1)+(-1/2+3^n/2)*C(2)),b->Function({n},(-1/2+3^n/\n" //
            + "2)*C(1)+(1/2+3^n/2)*C(2))}}");

    check("RSolveValue({a(n+1) == 2*a(n) + b(n), b(n+1) == a(n) + 2*b(n)}, {a(n), b(n)}, n)", //
        "{(1/2+3^n/2)*C(1)+(-1/2+3^n/2)*C(2),(-1/2+3^n/2)*C(1)+(1/2+3^n/2)*C(2)}");

    // System of Recurrences mapped into an expression
    check("RSolveValue({a(n+1) == 2*a(n) + b(n), b(n+1) == a(n) + 2*b(n)}, a(n) + b(n), n)", //
        "(-1/2+3^n/2)*C(1)+(1/2+3^n/2)*C(1)+(-1/2+3^n/2)*C(2)+(1/2+3^n/2)*C(2)");
  }

  @Test
  public void testRSolveValue004() {
    check("RSolve({n*a(n) == 2*a(n - 2), a(0) == 1, a(1) == 0}, a(n), n)", //
        "{{a(n)->(1+(-1)^n)/(2*Gamma(1/2*(2+n)))}}");
  }

  // ====================================================================================
  // 5. System of Recurrences
  // ====================================================================================
  @Test
  public void testRSolveSystems() {
    // Verifies ZTransform algebraic system solver handles multiple equations
    // and maps unbound initial conditions (a(0), b(0)) strictly to C(1), C(2)
    check("RSolve({a(n+1) == 2*a(n) + b(n), b(n+1) == a(n) + 2*b(n)}, {a(n), b(n)}, n)", //
        "{{a(n)->(1/2+3^n/2)*C(1)+(-1/2+3^n/2)*C(2),b(n)->(-1/2+3^n/2)*C(1)+(1/2+3^n/2)*C(\n" //
            + "2)}}");
  }

  @Test
  public void testRSolveValueSubstitution() {
    // 1. Ask for a shifted sequence value: a(n+1)
    // The engine must extract 'a', solve for a(n), generate the Function rule,
    // and correctly apply F.subst() so Function({n}, 3*2^n)[n+1] yields 3*2^(n+1).
    check("RSolveValue({a(n+1) == 2*a(n), a(0) == 3}, a(n+1), n)", //
        "3*2^(1+n)");

    // 2. Ask for a difference delta/linear combination: a(n+1) - a(n)
    // Ensures multiple applications of the pure function substitute and algebraically simplify.
    check("RSolveValue({a(n+1) == 2*a(n), a(0) == 3}, a(n+1) - a(n), n)", //
        "-3*2^n+3*2^(1+n)");

    // 3. Ask for the difference evaluated at a shifted polynomial index: a(n^2)
    check("RSolveValue({a(n+1) == 3*a(n), a(0) == 5}, a(n^2), n)", //
        "5*3^n^2");
  }

  @Test
  public void testLinearRecurrence() {
    // Exact solution is n + C(1).
    // Series expansion at Infinity should yield the exact same polynomial.
    check("AsymptoticRSolveValue(a(n) == a(n - 1) + 1, a(n), {n, Infinity, 3})", //
        "n+C(1)");
  }

  @Test
  public void testGeometricRecurrence() {
    // Exact solution is 2^n * C(1).
    check("AsymptoticRSolveValue(a(n) == 2*a(n - 1), a(n), {n, Infinity, 3})", //
        "2^n*C(1)");
  }

  @Test
  public void testRationalRecurrence() {
    // RSolve[a(n) == a(n-1) + 1/(n*(n-1)), a(n), n] yields C(1) - 1/n.
    // The asymptotic expansion at Infinity naturally produces the descending rational terms.
    check("AsymptoticRSolveValue(a(n) == a(n-1) + 1/(n*(n-1)), a(n), {n, Infinity, 3})",
        "-1/n+C(1)");
  }

  @Test
  public void testWithInitialConditions() {
    // Initial conditions should substitute C(1) before the series expansion.
    check("AsymptoticRSolveValue({a(n) == 3*a(n - 1), a(0) == 5}, a(n), {n, Infinity, 3})", //
        "5*3^n");
  }

  @Test
  public void testFibonacciRecurrence() {
    // Validates that GoldenRatio terms from RSolve are safely passed through Series
    check("AsymptoticRSolveValue(a(n) == a(n - 1) + a(n - 2), a(n), {n, Infinity, 3})", //
        "C(1)*Fibonacci(n)+C(2)*LucasL(n)");
  }

  @Test
  public void testImplicitInfinity() {
    // Tests the shorthand call signature: AsymptoticRSolveValue[eqn, a(n), n]
    // where x0 defaults to Infinity and the order defaults to SeriesTermGoal.
    check("AsymptoticRSolveValue(a(n) == a(n - 1) + 2, a(n), n)", //
        "2*n+C(1)");
  }

  @Test
  public void testUnevaluatedFallback() {
    // A highly non-linear equation that RSolve cannot solve exactly.
    // AsymptoticRSolveValue should return F.NIL, which Symja renders as the unevaluated input.
    check("AsymptoticRSolveValue(a(n) == a(n-1) + Sin(n * a(n-2)), a(n), {n, Infinity, 3})", //
        "AsymptoticRSolveValue(a(n)==a(-1+n)+Sin(n*a(-2+n)),a(n),{n,Infinity,3})");
  }

  @Test
  public void testInvalidArguments() {
    // Missing arguments or invalid formats should safely return unevaluated
    check("AsymptoticRSolveValue(a(n) == a(n - 1) + 1, a(n))",
        "AsymptoticRSolveValue(a(n)==1+a(-1+n),a(n))");

    check("AsymptoticRSolveValue(a(n) == a(n - 1) + 1, a, {n, Infinity, 3})",
        "AsymptoticRSolveValue(a(n)==1+a(-1+n),a,{n,Infinity,3})");
  }

  @Test
  public void testRSolveBinetIntegration() {
    // 1. Fibonacci recurrence: y(n) = y(n-1) + y(n-2)
    // Closed form: (phi^n - psi^n) / sqrt(5)
    // This tests if the binet utility correctly identifies the Fibonacci sequence
    // when expressed as a recurrence.
    check("RSolve({y(n) == y(n-1) + y(n-2), y(0) == 0, y(1) == 1}, y(n), n)", //
        "{{y(n)->Fibonacci(n)}}");

    // 2. Generic Order-2 Recurrence (The "Binet" test case)
    // y(n) = 3*y(n-1) - 1*y(n-2)
    // This triggers the generalizedBinet path in solveLinearConstantCoefficients
    // instead of solving the quadratic roots.
    check("RSolve({y(n) == 3*y(n-1) - y(n-2), y(0) == 0, y(1) == 1}, y(n), n)", //
        "{{y(n)->-(3/2-Sqrt(5)/2)^n/Sqrt(5)+(3/2+Sqrt(5)/2)^n/Sqrt(5)}}");

    // 3. Lucas sequence recurrence (Order-2)
    // y(n) = y(n-1) + y(n-2), y(0)=2, y(1)=1
    // This tests if the solver correctly handles non-standard initial conditions
    // and merges them into the unified formula.
    check("RSolve({y(n) == y(n-1) + y(n-2), y(0) == 2, y(1) == 1}, y(n), n)", //
        "{{y(n)->LucasL(n)}}");
  }

  @Test
  public void testRSolveHigherOrderFallbacks() {
    // TODO output a better symbolic solution

    // 4. Test that order-3 recurrences still fall back to the generic root solver
    // and do not crash the Binet logic (Ensures safety of the Order-2 intercept).
    check("RSolve({y(n) == y(n-1) + y(n-2) + y(n-3), y(0)==1, y(1)==1, y(2)==2}, y(n), n) // N", //
        "{{y(n)->(0.19079+I*0.0187006)*(-0.419643+I*(-0.606291))^n+(0.19079+I*(-0.0187006))*(-0.419643+I*0.606291)^n+(0.61842)*1.83929^n}}");
  }

}
