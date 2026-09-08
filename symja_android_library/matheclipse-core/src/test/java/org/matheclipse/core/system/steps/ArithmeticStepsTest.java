package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * The finer levels of detail, for arithmetic education: below the rule which was applied lie the
 * algebraic reshaping it needed and the single arithmetic operations that reshaping came down to.
 *
 * <p>
 * A level is asked for, never given: a derivation of the rules must not carry the arithmetic, or
 * the rules would be buried in it.
 *
 * @see org.matheclipse.core.eval.steps.StepLevel
 */
public class ArithmeticStepsTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
    // show the steps on the expressions the reader typed, not on their normal form
    Config.USER_STEPS_PARSER = true;
  }

  @AfterAll
  public static void afterAll() {
    // a global parser setting: put it back for the tests which run after this class
    Config.USER_STEPS_PARSER = false;
  }

  // ---------------------------------------------------------------- Algebra

  @Test
  public void testCancellingIsAnAlgebraStep() {
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[2,1,3,2]]", //
        "Factor");
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[2,2,3,2]]", //
        "CancelCommonFactors");
  }

  @Test
  public void testTheSentenceNamesTheFactor() {
    String steps = evalString("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")");
    assertTrue(steps.contains("Cancel the common factor"), steps);
    assertTrue(steps.contains("Factor -1+x^2 as"), steps);
  }

  // ---------------------------------------------------------------- Arithmetic

  @Test
  public void testTheQuadraticFormulaIsWorkedThrough() {
    // every operation of the formula, which is the point of the arithmetic level
    String steps = evalString("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")");
    for (String sentence : new String[] {"Square -4.", "Multiply -4 times -3.", "Add 16 to 12.",
        "Take the square root of 28.", "Multiply 2 times 1."}) {
      assertTrue(steps.contains(sentence), sentence + " is missing from:\n" + steps);
    }
  }

  @Test
  public void testTheTwoSolutionsAreReachedSeparately() {
    String steps = evalString("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")");
    assertTrue(steps.contains("when ± is plus"), steps);
    assertTrue(steps.contains("when ± is minus"), steps);
  }

  @Test
  public void testALinearEquationIsSolvedStepByStep() {
    String steps = evalString("TraceForm(QuarticSolve(0,3,-6), Infinity, \"Arithmetic\")");
    assertTrue(steps.contains("Divide both sides by"), steps);
  }

  // ---------------------------------------------------------------- the levels filter

  @Test
  public void testARuleTraceCarriesNoAlgebra() {
    check("TraceForm(Cancel((x^2-1)/(x-1)))[[2]]", //
        "{}");
  }

  @Test
  public void testARuleTraceCarriesNoArithmetic() {
    String rules = evalString("TraceForm(QuarticSolve(1,-4,-3))");
    assertFalse(rules.contains("Square -4."), rules);
    assertFalse(rules.contains("Take the square root of 28."), rules);
    // the rules themselves are still there
    assertTrue(rules.contains("All equations of the form"), rules);
  }

  @Test
  public void testAlgebraDoesNotReachTheArithmetic() {
    String algebra = evalString("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Algebra\")");
    assertFalse(algebra.contains("Square -4."), algebra);
  }

  @Test
  public void testEachLevelAddsToTheOneBeforeIt() {
    int rule = countSteps("TraceForm(QuarticSolve(1,-4,-3), Infinity)");
    int algebra = countSteps("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Algebra\")");
    int arithmetic = countSteps("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")");
    assertTrue(rule <= algebra, "Algebra dropped a step the rules had: " + rule + " -> " + algebra);
    assertTrue(algebra < arithmetic,
        "Arithmetic added nothing: " + algebra + " -> " + arithmetic);
  }

  @Test
  public void testTheLevelNamesAreCaseInsensitive() {
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"algebra\")[[2,1,3,2]]", //
        "Factor");
  }

  @Test
  public void testTheResultsAreTheSameAtEveryLevel() {
    // the level chooses what is shown, never what is computed
    check("TraceForm(QuarticSolve(1,-4,-3), Infinity)[[1]]", //
        "{1/2*(4+2*Sqrt(7)),1/2*(4-2*Sqrt(7))}");
    check("TraceForm(QuarticSolve(1,-4,-3), Infinity, \"Arithmetic\")[[1]]", //
        "{1/2*(4+2*Sqrt(7)),1/2*(4-2*Sqrt(7))}");
    check("TraceForm(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[1]]", //
        "1+x");
  }

  private int countSteps(String input) {
    return Integer.parseInt(evalString("Length(" + input + "[[2]])").trim());
  }
}
