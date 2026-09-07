package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Bound proving and complete enumeration of a finite integer solution set. */
public class FiniteIntegerSolverTest {

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

  private static IntegerSolveResult solve(IExpr condition, ISymbol... symbols) {
    Formula lowered = Lowering.lower(condition);
    assertNotNull(lowered, "expected to lower: " + condition);
    return FiniteIntegerSolver.solve(lowered, variables(symbols), IntegerDomain.INTEGERS);
  }

  private static String tuples(IntegerSolveResult result) {
    StringBuilder builder = new StringBuilder();
    for (BigInteger[] solution : result.solutions()) {
      if (builder.length() > 0) {
        builder.append(" ");
      }
      builder.append("(");
      for (int index = 0; index < solution.length; index++) {
        if (index > 0) {
          builder.append(",");
        }
        builder.append(solution[index]);
      }
      builder.append(")");
    }
    return builder.toString();
  }

  @Test
  public void testBoundsAreDerivedThroughAnEquality() {
    // x is bounded directly, and the equality carries that bound over to y. The constraint
    // solver's fixed search box excluded these solutions entirely and answered the empty set.
    IntegerSolveResult result = solve(
        F.And(F.Equal(F.Plus(S.x, S.y), F.ZZ(20000)), F.GreaterEqual(S.x, F.C0),
            F.GreaterEqual(S.y, F.C0), F.LessEqual(S.x, F.C3)),
        S.x, S.y);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(0,20000) (1,19999) (2,19998) (3,19997)", tuples(result));
  }

  @Test
  public void testCongruenceStride() {
    IntegerSolveResult result =
        solve(F.And(F.LessEqual(F.C0, S.x, F.ZZ(20)),
            F.Equal(F.Mod(F.Plus(F.Times(F.C6, S.x), F.C4), F.C10), F.C0)), S.x);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(1) (6) (11) (16)", tuples(result));
  }

  @Test
  public void testInconsistentCongruenceHasNoSolution() {
    IntegerSolveResult result =
        solve(F.And(F.LessEqual(F.C0, S.x, F.ZZ(20)),
            F.Equal(F.Mod(F.Plus(F.Times(F.C2, S.x), F.C1), F.C4), F.C0)), S.x);
    assertEquals(IntegerSolveResult.Kind.INFEASIBLE, result.kind());
  }

  @Test
  public void testTwoCongruencesMeetByTheRemainderTheorem() {
    IntegerSolveResult result = solve(F.And(F.LessEqual(F.C0, S.x), F.Less(S.x, F.ZZ(30)),
        F.Equal(F.Mod(S.x, F.C3), F.C1), F.Equal(F.Mod(S.x, F.C5), F.C2)), S.x);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(7) (22)", tuples(result));
  }

  @Test
  public void testDisjunctionAndDisequality() {
    IntegerSolveResult result = solve(
        F.And(F.LessEqual(F.C0, S.x, F.C3), F.Unequal(S.x, F.C1),
            F.Or(F.Equal(S.x, F.C0), F.GreaterEqual(S.x, F.C2))),
        S.x);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(0) (2) (3)", tuples(result));
  }

  @Test
  public void testSeveralVariablesWithACongruence() {
    IntegerSolveResult result = solve(
        F.And(F.LessEqual(F.C0, S.x, F.C5), F.LessEqual(F.C0, S.y, F.C5),
            F.Equal(F.Mod(F.Plus(S.x, F.Times(F.C2, S.y)), F.C4), F.C3)),
        S.x, S.y);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(1,1) (1,3) (1,5) (3,0) (3,2) (3,4) (5,1) (5,3) (5,5)", tuples(result));
  }

  @Test
  public void testCoefficientsBeyondMachineIntegers() {
    BigInteger big = BigInteger.valueOf(9007199254740993L);
    IntegerSolveResult result =
        solve(F.And(F.Equal(F.Times(F.ZZ(big), S.x), F.ZZ(big)), F.LessEqual(F.C0, S.x, F.C2)),
            S.x);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(1)", tuples(result));
  }

  @Test
  public void testUnboundedIsUnsupportedAndNotEmpty() {
    // the distinction matters: an empty solution set would be a wrong answer here
    assertEquals(IntegerSolveResult.Kind.UNSUPPORTED,
        solve(F.GreaterEqual(S.x, F.C0), S.x).kind());
    assertEquals(IntegerSolveResult.Kind.UNSUPPORTED,
        solve(F.Equal(F.Mod(S.x, F.C3), F.C1), S.x).kind());
    // and so does a range which is finite but too large to enumerate
    assertEquals(IntegerSolveResult.Kind.UNSUPPORTED,
        solve(F.LessEqual(F.C0, S.x, F.ZZ(1000001)), S.x).kind());
  }

  @Test
  public void testCyclicConstraintsAreInfeasibleRatherThanUnbounded() {
    // y >= x + 1 && x >= y + 1 tightens forever; the round cap stops it and the bounds cross
    IntegerSolveResult result = solve(
        F.And(F.GreaterEqual(S.x, F.C0), F.GreaterEqual(S.y, F.Plus(S.x, F.C1)),
            F.GreaterEqual(S.x, F.Plus(S.y, F.C1))),
        S.x, S.y);
    assertTrue(
        result.kind() == IntegerSolveResult.Kind.INFEASIBLE
            || result.kind() == IntegerSolveResult.Kind.UNSUPPORTED,
        "expected no solutions to be reported, was " + result.kind());
    assertTrue(result.solutions().isEmpty());
  }

  @Test
  public void testVariablesTheConditionDoesNotMention() {
    IntegerSolveResult result =
        solve(F.And(F.LessEqual(F.C0, S.x, F.C2)), S.x, S.y);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(0) (1) (2)", tuples(result));
    assertEquals(1, result.untouched().size());
    assertEquals(Variable.free(S.y), result.untouched().get(0));
  }

  @Test
  public void testParametersAreNotSolvedFor() {
    assertEquals(IntegerSolveResult.Kind.UNSUPPORTED,
        solve(F.And(F.LessEqual(F.C0, S.x, F.C3), F.Greater(S.x, S.a)), S.x).kind());
  }

  @Test
  public void testPrimesDomain() {
    Formula lowered = Lowering.lower(F.And(F.LessEqual(F.C0, S.x, F.ZZ(12))));
    assertNotNull(lowered);
    IntegerSolveResult result =
        FiniteIntegerSolver.solve(lowered, variables(S.x), IntegerDomain.PRIMES);
    assertEquals(IntegerSolveResult.Kind.FINITE, result.kind());
    assertEquals("(2) (3) (5) (7) (11)", tuples(result));
  }

  @Test
  public void testDisjunctiveNormalFormCap() {
    // a wide product of disjunctions is declined rather than expanded
    IExpr wide = F.C0;
    List<IExpr> parts = new ArrayList<IExpr>();
    for (int index = 0; index < 20; index++) {
      parts.add(F.Or(F.Equal(S.x, F.ZZ(index)), F.Equal(S.y, F.ZZ(index))));
    }
    IExpr condition = F.ast(parts.toArray(new IExpr[0]), S.And);
    assertNotNull(wide);
    Formula lowered = Lowering.lower(condition);
    assertNotNull(lowered);
    assertNull(FiniteIntegerSolver.disjunctiveNormalForm(lowered));
  }
}
