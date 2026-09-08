package org.matheclipse.core.eval.steps;

import org.matheclipse.core.interfaces.IExpr;

/**
 * How fine-grained the recorded evaluation steps of
 * {@link org.matheclipse.core.expression.S#TraceForm} are.
 *
 * <p>
 * A step is recorded only if its own level is <b>less than or equal</b> to the level the listener
 * asks for, so a listener at {@link #RULE} never sees the arithmetic steps and never pays for
 * building their hint expressions. Every site which records a step declares the level it belongs
 * to; sites which don't declare one mean {@link #RULE}.
 *
 * <p>
 * The whole machinery is additionally switched off at compile time by
 * {@link org.matheclipse.core.basic.ToggleFeature#SHOW_STEPS}.
 */
public final class StepLevel {

  /** Record nothing. */
  public static final int NONE = 0;

  /**
   * The rule which was applied: <code>D::ChainRule</code>, <code>Integrate::RubiRule</code>,
   * <code>QuarticSolve::QuadraticFormulaStart</code>, a user defined rewrite rule. This is the
   * default and the level a reader of a derivation wants.
   */
  public static final int RULE = 1;

  /**
   * Algebraic reshaping below the rule level: cancelling common factors, factoring a numerator or
   * denominator.
   */
  public static final int ALGEBRA = 2;

  /**
   * Single arithmetic operations, for arithmetic education: cancelling the gcd of a fraction,
   * multiplying two numbers, <code>t^0 = 1</code>.
   */
  public static final int ARITHMETIC = 3;

  /** The highest level which can be requested. */
  public static final int MAX = ARITHMETIC;

  private StepLevel() {}

  /**
   * The level for a user supplied argument: an integer <code>0...3</code> or one of the strings
   * <code>"None"</code>, <code>"Rule"</code>, <code>"Algebra"</code>, <code>"Arithmetic"</code>
   * (case insensitive).
   *
   * @param expr the argument
   * @return <code>-1</code> if <code>expr</code> denotes no level
   */
  public static int parse(IExpr expr) {
    if (expr.isInteger()) {
      int level = expr.toIntDefault(-1);
      return (level >= NONE && level <= MAX) ? level : -1;
    }
    if (expr.isString() || expr.isSymbol()) {
      String name = expr.isString() ? expr.toString() : expr.toString();
      if (name.equalsIgnoreCase("None")) {
        return NONE;
      }
      if (name.equalsIgnoreCase("Rule")) {
        return RULE;
      }
      if (name.equalsIgnoreCase("Algebra")) {
        return ALGEBRA;
      }
      if (name.equalsIgnoreCase("Arithmetic") || name.equalsIgnoreCase("All")) {
        return ARITHMETIC;
      }
    }
    return -1;
  }

  /** The name of a level, as it is written in <code>TraceForm(expr, depth, level)</code>. */
  public static String toString(int level) {
    switch (level) {
      case NONE:
        return "None";
      case RULE:
        return "Rule";
      case ALGEBRA:
        return "Algebra";
      case ARITHMETIC:
        return "Arithmetic";
      default:
        return Integer.toString(level);
    }
  }
}
