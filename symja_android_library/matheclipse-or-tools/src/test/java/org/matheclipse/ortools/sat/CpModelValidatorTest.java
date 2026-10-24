package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.sat.engine.CpModelValidator;
import org.matheclipse.ortools.sat.engine.SolutionChecker;
import org.matheclipse.ortools.util.Domain;

/**
 * Tests of the model checker changes of OR-Tools 10.0 (cp_model_checker.cc): the expression form
 * of the inverse constraint, the exact int_div divisor check, the reordered checks and the
 * reworded messages.
 */
public class CpModelValidatorTest {

  private static void assertStartsWith(String prefix, String actual) {
    assertTrue(actual.startsWith(prefix), () -> "expected prefix <" + prefix + "> but was <"
        + actual + ">");
  }

  private static LinearExpressionProto expr(long offset, long... varCoeffPairs) {
    LinearExpressionProto.Builder b = LinearExpressionProto.newBuilder().setOffset(offset);
    for (int i = 0; i < varCoeffPairs.length; i += 2) {
      b.addVars((int) varCoeffPairs[i]).addCoeffs(varCoeffPairs[i + 1]);
    }
    return b.build();
  }

  private static CpModelProto.Builder vars(Domain... domains) {
    CpModelProto.Builder m = CpModelProto.newBuilder();
    for (Domain d : domains) {
      IntegerVariableProto.Builder v = m.addVariablesBuilder();
      for (long x : d.flattenedIntervals()) {
        v.addDomain(x);
      }
    }
    return m;
  }

  @Test
  public void testInverseFormats() {
    Domain d = new Domain(0, 1);
    // expressions: valid
    CpModelProto.Builder m = vars(d, d);
    InverseConstraintProto.Builder inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFExprDirect(expr(0, 0, 1)).addFExprDirect(expr(1, 0, -1));
    inv.addFExprInverse(expr(0, 1, 1)).addFExprInverse(expr(1, 1, -1));
    assertEquals("", CpModelValidator.validate(m.build()));
    // f(0) = x0, f(1) = 1 - x0, g(0) = x1, g(1) = 1 - x1: x1 == x0
    assertTrue(SolutionChecker.isFeasible(m.build(), new long[] {1, 1}));
    assertTrue(SolutionChecker.isFeasible(m.build(), new long[] {0, 0}));
    assertTrue(!SolutionChecker.isFeasible(m.build(), new long[] {0, 1}));

    // size mismatch of the expressions
    inv.addFExprDirect(expr(0));
    assertStartsWith("Non-matching fields size in inverse: ", CpModelValidator.validate(m.build()));

    // both formats
    m = vars(d, d);
    inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFDirect(0).addFInverse(1);
    inv.addFExprDirect(expr(0, 0, 1)).addFExprInverse(expr(0, 1, 1));
    assertStartsWith("Inconsistent inverse with both legacy and new format defined: ",
        CpModelValidator.validate(m.build()));

    // the expressions are validated like all linear expressions
    m = vars(d, d);
    inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFExprDirect(expr(0, -1, 1)).addFExprInverse(expr(0, 1, 1));
    assertStartsWith("Invalid negated variable in linear expression: ",
        CpModelValidator.validate(m.build()));
    m = vars(d, d);
    inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFExprDirect(expr(0, 5, 1)).addFExprInverse(expr(0, 1, 1));
    assertStartsWith("Out of bound integer variable 5 in constraint #0 : ",
        CpModelValidator.validate(m.build()));
    m = vars(d, d);
    inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFExprDirect(expr(0, 0, 1, 1, 1)).addFExprInverse(expr(Long.MAX_VALUE / 2 + 1, 1, 1));
    assertStartsWith("Possible overflow in linear expression: ",
        CpModelValidator.validate(m.build()));

    // legacy: variable indices
    m = vars(d, d);
    inv = m.addConstraintsBuilder().getInverseBuilder();
    inv.addFDirect(0).addFInverse(-1);
    assertEquals("Invalid variable index in inverse constraint: -1",
        CpModelValidator.validate(m.build()));
  }

  private static String validateDivision(CpModelProto.Builder m, LinearExpressionProto divisor) {
    int t = m.getVariablesCount();
    m.addVariablesBuilder().addDomain(-100).addDomain(100);
    LinearArgumentProto.Builder div = m.addConstraintsBuilder().getIntDivBuilder();
    div.setTarget(expr(0, t, 1)).addExprs(expr(7)).addExprs(divisor);
    return CpModelValidator.validate(m.build());
  }

  @Test
  public void testIntDivDivisorCheck() {
    String zero = "The domain of the divisor cannot contain 0: ";
    Domain holeAtZero = Domain.fromIntervals(new long[][] {{-3, -1}, {1, 3}});
    Domain pos = new Domain(1, 3);
    Domain neg = new Domain(-3, -1);
    // one term: exact, a hole at zero is accepted
    assertEquals("", validateDivision(vars(holeAtZero), expr(0, 0, 1)));
    assertStartsWith(zero, validateDivision(vars(new Domain(-3, 3)), expr(0, 0, 1)));
    assertEquals("", validateDivision(vars(new Domain(0, 3)), expr(1, 0, 2)));
    assertStartsWith(zero, validateDivision(vars(new Domain(0, 3)), expr(-4, 0, 2)));
    // two terms: exact through the size two Diophantine equation
    assertEquals("", validateDivision(vars(pos, pos), expr(0, 0, 1, 1, 1)));
    assertStartsWith(zero, validateDivision(vars(pos, neg), expr(0, 0, 1, 1, 1)));
    // 2x + 4y + 1 is odd: never 0 although its bounds contain 0
    assertEquals("", validateDivision(vars(new Domain(-5, 5), new Domain(-5, 5)),
        expr(1, 0, 2, 1, 4)));
    // 2x - 2y with x in {0, 2}, y in {1, 3}: x == y is impossible
    assertEquals("", validateDivision(vars(Domain.fromValues(new long[] {0, 2}),
        Domain.fromValues(new long[] {1, 3})), expr(0, 0, 2, 1, -2)));
    assertStartsWith(zero, validateDivision(vars(Domain.fromValues(new long[] {0, 3}),
        Domain.fromValues(new long[] {1, 3})), expr(0, 0, 2, 1, -2)));
    // 3x + 5y = 1 has the solution x = 2, y = -1
    assertStartsWith(zero, validateDivision(vars(new Domain(0, 4), new Domain(-2, -1)),
        expr(-1, 0, 3, 1, 5)));
    assertEquals("", validateDivision(vars(new Domain(0, 1), new Domain(-2, -1)),
        expr(-1, 0, 3, 1, 5)));
    // more terms: gcd and bounds
    Domain wide = new Domain(-5, 5);
    assertEquals("", validateDivision(vars(wide, wide, wide), expr(1, 0, 2, 1, 2, 2, 2)));
    assertStartsWith(zero, validateDivision(vars(wide, wide, wide), expr(0, 0, 2, 1, 2, 2, 2)));
    assertEquals("", validateDivision(vars(pos, pos, pos), expr(0, 0, 1, 1, 1, 2, 1)));
    // canonicalization: repeated variables are merged, fixed variables folded into the offset
    assertEquals("", validateDivision(vars(wide), expr(2, 0, 1, 0, -1)));
    assertStartsWith("Division by 0: ", validateDivision(vars(wide), expr(0, 0, 1, 0, -1)));
    assertStartsWith("Division by 0: ",
        validateDivision(vars(new Domain(3)), expr(-6, 0, 2)));
    assertEquals("", validateDivision(vars(new Domain(3), pos), expr(-6, 0, 2, 1, 1)));
    assertEquals("", validateDivision(vars(new Domain(3), holeAtZero),
        expr(-6, 0, 2, 1, 1, 1, 0)));
  }

  @Test
  public void testIntDivSolvesWithExactDivisorCheck() {
    // a model rejected by the 9.15 bound check: 7 / (x - x + 2) and 9 / (2y + 4z + 1)
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(-5, 5, "x");
    IntVar y = model.newIntVar(-2, 2, "y");
    IntVar z = model.newIntVar(-2, 2, "z");
    IntVar q1 = model.newIntVar(-100, 100, "q1");
    IntVar q2 = model.newIntVar(-100, 100, "q2");
    model.addDivisionEquality(q1, LinearExpr.constant(7),
        LinearExpr.newBuilder().add(x).addTerm(x, -1).add(2));
    model.addDivisionEquality(q2, LinearExpr.constant(9),
        LinearExpr.newBuilder().addTerm(y, 2).addTerm(z, 4).add(1));
    assertEquals("", model.validate());
    CpSolver solver = new CpSolver();
    solver.getParameters().setEnumerateAllSolutions(true);
    long[] count = new long[1];
    CpSolverStatus status = solver.solve(model, new CpSolverSolutionCallback() {
      @Override
      public void onSolutionCallback() {
        long d = 2 * value(y) + 4 * value(z) + 1;
        assertEquals(3, value(q1));
        assertEquals(9 / d, value(q2));
        count[0]++;
      }
    });
    assertEquals(CpSolverStatus.OPTIMAL, status);
    assertEquals(11 * 5 * 5, count[0]);
  }

  @Test
  public void testReorderedChecksAndMessages() {
    // element: index and target are checked before the vars
    CpModelProto.Builder m = vars(new Domain(0, 1), new Domain(0, 1));
    ElementConstraintProto.Builder el = m.addConstraintsBuilder().getElementBuilder();
    el.setIndex(-1).setTarget(1).addVars(0).addVars(5);
    assertStartsWith("Out of bound integer variable 5", CpModelValidator.validate(m.build()));
    el.setVars(1, 1);
    assertStartsWith("Element constraint index and target must be valid variables: ",
        CpModelValidator.validate(m.build()));

    // a negated variable is reported before a possible overflow
    m = vars(new Domain(0, Long.MAX_VALUE / 2));
    m.addConstraintsBuilder().getAllDiffBuilder().addExprs(expr(0, -1, 4));
    assertStartsWith("Invalid negated variable in linear expression: ",
        CpModelValidator.validate(m.build()));

    // the offset of an expression must be in [kint64min / 2, kint64max / 2]
    m = vars(new Domain(0, 1));
    m.addConstraintsBuilder().getAllDiffBuilder().addExprs(expr(Long.MAX_VALUE / 2 + 1, 0, 1));
    assertStartsWith("Possible overflow in linear expression: ",
        CpModelValidator.validate(m.build()));
    m = vars(new Domain(0, 1));
    m.addConstraintsBuilder().getAllDiffBuilder().addExprs(expr(Long.MAX_VALUE / 2 - 1, 0, 1));
    assertEquals("", CpModelValidator.validate(m.build()));

    // reworded messages
    m = CpModelProto.newBuilder();
    m.addVariablesBuilder().addDomain(0).addDomain(5).addDomain(6).addDomain(7);
    assertStartsWith("var #0 has an invalid domain() format: ",
        CpModelValidator.validate(m.build()));
    m = CpModelProto.newBuilder();
    m.addVariablesBuilder().addDomain(0).addDomain(Long.MAX_VALUE / 2 + 1);
    assertStartsWith("var #0 domain does not fall in [-kint64max / 2, kint64max / 2]. ",
        CpModelValidator.validate(m.build()));
    m = vars(new Domain(0, 1));
    IntervalConstraintProto.Builder iv = m.addConstraintsBuilder().getIntervalBuilder();
    iv.setStart(expr(0, 0, 1)).setSize(expr(1));
    assertStartsWith("Interval must have an end expression: ",
        CpModelValidator.validate(m.build()));
    m = vars(new Domain(0, 1));
    m.getSolutionHintBuilder().addVars(0).addValues(Long.MAX_VALUE);
    assertEquals("The solution hint cannot contain the INT_MIN or INT_MAX values.",
        CpModelValidator.validate(m.build()));
    m = vars(new Domain(0, 1));
    m.addConstraintsBuilder().getTableBuilder().addValues(1);
    assertStartsWith("Inconsistent table with empty expressions and non-empty tuples: ",
        CpModelValidator.validate(m.build()));
    m = vars(new Domain(0, 1));
    m.getObjectiveBuilder().addVars(0);
    assertStartsWith("vars and coeffs sizes do not match in objective: ",
        CpModelValidator.validate(m.build()));
  }
}
