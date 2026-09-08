package org.matheclipse.core.eval.steps;

import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * One step of an evaluation, shown to a {@link StepDialog} while the evaluation waits.
 *
 * <p>
 * A step is offered while the engine sits inside it, so an expression asked about here is answered
 * with everything the evaluation has done so far in force - which is the point of stepping through
 * one at all.
 */
public final class DialogStep {

  private final IExpr input;
  private final IExpr result;
  private final IAST hints;
  private final int level;
  private final int number;
  private final Function<IExpr, IExpr> evaluator;

  DialogStep(IExpr input, IExpr result, IAST hints, int level, int number,
      Function<IExpr, IExpr> evaluator) {
    this.input = input;
    this.result = result;
    this.hints = hints;
    this.level = level;
    this.number = number;
    this.evaluator = evaluator;
  }

  /** The expression this step started from. */
  public IExpr input() {
    return input;
  }

  /** The expression this step produced. */
  public IExpr result() {
    return result;
  }

  /** <code>{headSymbol, "RuleKey", hintArg...}</code>. */
  public IAST hints() {
    return hints;
  }

  /** 1 for a top level step, one more for every step this one is nested in. */
  public int level() {
    return level;
  }

  /** How many steps have been shown before this one, counting from 1. */
  public int number() {
    return number;
  }

  /**
   * The sentence which explains this step, with its formulas written plainly.
   *
   * @see StepDescription
   */
  public String description() {
    return StepDescription.render(StepDescription.template(descriptionKey()), input, result, hints,
        StepDescription.PLAIN_TEXT, "", "");
  }

  /** The key of this step's sentence, for example <code>D::ChainRule</code>. */
  public String descriptionKey() {
    IExpr head = hints.isEmpty() ? org.matheclipse.core.expression.F.NIL : hints.first();
    String ruleKey = hints.argSize() >= 2 ? hints.arg2().toString() : "";
    return head.toString() + "::" + ruleKey;
  }

  /**
   * Evaluate an expression without leaving the evaluation which is waiting here, and without that
   * evaluation becoming steps of its own.
   *
   * @param expr what to evaluate
   * @return what it evaluates to
   */
  public IExpr evaluate(IExpr expr) {
    return evaluator.apply(expr);
  }

  @Override
  public String toString() {
    return "step " + number + " at level " + level + ": " + input + " -> " + result;
  }
}
