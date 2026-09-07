package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The parametrized integer solution set of a linear system.
 *
 * <p>
 * The expected offsets and bases are the canonical ones, which are also what wolframscript prints
 * for the corresponding <code>Reduce</code> calls.
 */
public class LatticeSolverTest {

  @BeforeAll
  public static void awaitSymbolTable() throws InterruptedException {
    F.await();
  }

  private static List<Variable> variables(ISymbol... symbols) {
    List<Variable> result = new ArrayList<Variable>(symbols.length);
    for (ISymbol symbol : symbols) {
      result.add(Variable.free(symbol));
    }
    return result;
  }

  /** Solve the equations, each written as an expression which equals zero. */
  private static LatticeSolver.Solution solve(List<Variable> variables, IExpr... equations) {
    List<AffineTerm> terms = new ArrayList<AffineTerm>(equations.length);
    for (IExpr equation : equations) {
      Formula lowered = Lowering.lower(F.Equal(equation, F.C0));
      assertNotNull(lowered, "expected to lower: " + equation);
      assertEquals(Formula.Kind.ATOM, lowered.kind());
      terms.add(lowered.atom().term());
    }
    return LatticeSolver.solve(terms, variables);
  }

  private static BigInteger[] row(long... values) {
    BigInteger[] result = new BigInteger[values.length];
    for (int i = 0; i < values.length; i++) {
      result[i] = BigInteger.valueOf(values[i]);
    }
    return result;
  }

  private static void assertSolution(LatticeSolver.Solution solution, BigInteger[] offset,
      BigInteger[]... basis) {
    assertNotNull(solution);
    assertArrayEquals(offset, solution.offset(), "offset");
    assertEquals(basis.length, solution.parameterCount(), "number of parameters");
    for (int row = 0; row < basis.length; row++) {
      assertArrayEquals(basis[row], solution.basis()[row], "basis row " + row);
    }
  }

  @Test
  public void testTwoVariableEquations() {
    List<Variable> xy = variables(S.x, S.y);
    // 3 x + 5 y == 7
    assertSolution(solve(xy, F.Plus(F.Times(F.C3, S.x), F.Times(F.C5, S.y), F.ZZ(-7))), row(4, -1),
        row(5, -3));
    // 2 x == 4 y : the solution is x = 2t, y = t, not y == x/2
    assertSolution(solve(xy, F.Subtract(F.Times(F.C2, S.x), F.Times(F.C4, S.y))), row(0, 0),
        row(2, 1));
    // 2 x + 3 y == 1
    assertSolution(solve(xy, F.Plus(F.Times(F.C2, S.x), F.Times(F.C3, S.y), F.CN1)), row(2, -1),
        row(3, -2));
    // 6 x + 4 y == 10
    assertSolution(solve(xy, F.Plus(F.Times(F.C6, S.x), F.Times(F.C4, S.y), F.ZZ(-10))), row(1, 1),
        row(2, -3));
  }

  @Test
  public void testNoIntegerSolution() {
    List<Variable> xy = variables(S.x, S.y);
    // gcd(2, 4) = 2 does not divide 5
    assertNull(solve(xy, F.Plus(F.Times(F.C2, S.x), F.Times(F.C4, S.y), F.ZZ(-5))));
    // an inconsistent system
    assertNull(solve(xy, F.Plus(S.x, F.Times(F.C2, S.y), F.CN1),
        F.Plus(F.Times(F.C2, S.x), F.Times(F.C4, S.y), F.ZZ(-3))));
  }

  @Test
  public void testThreeVariables() {
    List<Variable> xyz = variables(S.x, S.y, S.z);
    // 2 x + 3 y == 5 z
    assertSolution(
        solve(xyz, F.Plus(F.Times(F.C2, S.x), F.Times(F.C3, S.y), F.Times(F.CN5, S.z))),
        row(0, 0, 0), row(1, 1, 1), row(0, 5, 3));
    // x + y + z == 1
    assertSolution(solve(xyz, F.Plus(S.x, S.y, S.z, F.CN1)), row(0, 0, 1), row(1, 0, -1),
        row(0, 1, -1));
    // x == 2 y and y == 3 z leave one degree of freedom
    assertSolution(solve(xyz, F.Subtract(S.x, F.Times(F.C2, S.y)),
        F.Subtract(S.y, F.Times(F.C3, S.z))), row(0, 0, 0), row(6, 3, 1));
  }

  @Test
  public void testRedundantEquationIsNotAContradiction() {
    List<Variable> xy = variables(S.x, S.y);
    // the second equation is twice the first
    assertSolution(solve(xy, F.Plus(S.x, F.Times(F.C2, S.y), F.CN1),
        F.Plus(F.Times(F.C2, S.x), F.Times(F.C4, S.y), F.ZZ(-2))), row(1, 0), row(2, -1));
  }

  @Test
  public void testSystemOfTwoEquationsInThreeUnknowns() {
    List<Variable> xyz = variables(S.x, S.y, S.z);
    LatticeSolver.Solution solution = solve(xyz,
        F.Plus(F.Times(F.C2, S.x), F.Times(F.C3, S.y), F.Times(F.CN5, S.z), F.CN1),
        F.Plus(F.Times(F.C3, S.x), F.Times(F.CN4, S.y), F.Times(F.C7, S.z), F.ZZ(-3)));
    assertNotNull(solution);
    assertEquals(1, solution.parameterCount());
    // verify by substitution rather than by pinning one of several equivalent bases
    for (long parameter = -3; parameter <= 3; parameter++) {
      BigInteger[] point = new BigInteger[3];
      for (int index = 0; index < 3; index++) {
        point[index] = solution.offset()[index]
            .add(solution.basis()[0][index].multiply(BigInteger.valueOf(parameter)));
      }
      assertEquals(BigInteger.ONE,
          point[0].multiply(BigInteger.TWO).add(point[1].multiply(BigInteger.valueOf(3)))
              .subtract(point[2].multiply(BigInteger.valueOf(5))));
      assertEquals(BigInteger.valueOf(3),
          point[0].multiply(BigInteger.valueOf(3)).subtract(point[1].multiply(BigInteger.valueOf(4)))
              .add(point[2].multiply(BigInteger.valueOf(7))));
    }
  }

  @Test
  public void testCoefficientsBeyondMachineIntegers() {
    List<Variable> x = variables(S.x);
    BigInteger big = BigInteger.valueOf(9007199254740993L);
    assertSolution(solve(x, F.Subtract(F.Times(F.ZZ(big), S.x), F.ZZ(big))), row(1));
    // and a system whose right hand side is not divisible has no solution
    assertNull(solve(x, F.Subtract(F.Times(F.ZZ(big), S.x), F.ZZ(big.add(BigInteger.ONE)))));
  }

  @Test
  public void testCanonicalFormIsIdempotent() {
    List<Variable> xyz = variables(S.x, S.y, S.z);
    LatticeSolver.Solution first =
        solve(xyz, F.Plus(F.Times(F.C2, S.x), F.Times(F.C3, S.y), F.Times(F.CN5, S.z)));
    assertNotNull(first);
    List<AffineTerm> reconstructed = new ArrayList<AffineTerm>();
    reconstructed.add(Lowering
        .lower(F.Equal(F.Plus(F.Times(F.C2, S.x), F.Times(F.C3, S.y), F.Times(F.CN5, S.z)), F.C0))
        .atom().term());
    LatticeSolver.Solution second = LatticeSolver.solve(reconstructed, xyz);
    assertArrayEquals(first.offset(), second.offset());
    assertEquals(first.parameterCount(), second.parameterCount());
    for (int row = 0; row < first.parameterCount(); row++) {
      assertArrayEquals(first.basis()[row], second.basis()[row]);
    }
  }

  @Test
  public void testAParameterOutsideTheUnknownsIsRejected() {
    List<Variable> x = variables(S.x);
    // x + a == 0 is not a system in x alone
    assertNull(solve(x, F.Plus(S.x, S.a)));
  }

  @Test
  public void testSubstitutionRebuildsTheSolution() {
    List<Variable> xy = variables(S.x, S.y);
    LatticeSolver.Solution solution =
        solve(xy, F.Plus(F.Times(F.C3, S.x), F.Times(F.C5, S.y), F.ZZ(-7)));
    assertNotNull(solution);
    List<Variable> parameters = Arrays.asList(Variable.bound(S.C, 0));
    // x == 4 + 5 C(1)
    AffineTerm first = solution.substitution(0, parameters);
    assertEquals(BigInteger.valueOf(4), first.constant().numerator().toBigNumerator());
    assertEquals(BigInteger.valueOf(5),
        first.coefficient(parameters.get(0)).numerator().toBigNumerator());
    // y == -1 - 3 C(1)
    AffineTerm second = solution.substitution(1, parameters);
    assertEquals(BigInteger.valueOf(-1), second.constant().numerator().toBigNumerator());
    assertEquals(BigInteger.valueOf(-3),
        second.coefficient(parameters.get(0)).numerator().toBigNumerator());
  }
}
