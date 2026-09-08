package org.matheclipse.core.eval.steps;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * One recorded evaluation step while {@link StepsListener} is collecting; converted to the nested
 * list form described in {@link StepsTree} when the evaluation is done.
 *
 * <p>
 * This is a mutable working object, not part of an expression: the sub-steps of a step are only
 * known after the step's rewritten expression has itself been evaluated.
 */
public final class StepNode {

  /** The expression this step started from. */
  final IExpr input;

  /** The expression this step produced, as it should be displayed. */
  final IExpr display;

  /**
   * The expression this step produced, as the identical object the engine goes on to evaluate. A
   * {@link StepsListener#setUp} whose input <code>==</code> this object opens the frame whose steps
   * become this step's {@link #children}.
   */
  final IExpr identity;

  /** <code>{headSymbol, "RuleKey", hintArg...}</code>. */
  final IAST hints;

  /** The steps caused by this one. */
  final List<StepNode> children = new ArrayList<StepNode>();

  /** 1 for a top level step, one more for every step this one is nested in. */
  final int level;

  /** <code>true</code> if sub-steps of this step were dropped by the depth or the node cap. */
  boolean truncated;

  StepNode(IExpr input, IExpr display, IExpr identity, IAST hints, int level) {
    this.input = input;
    this.display = display;
    this.identity = identity;
    this.hints = hints;
    this.level = level;
  }

  /**
   * <code>{HoldForm(input), HoldForm(result), {headSymbol, "RuleKey", hintArg...}, {subStep...}}
   * </code>, with the rule set's own working rewritten into the mathematics it stands for.
   *
   * <p>
   * This runs once the evaluation is over, never while it is going on: tidying up asks the engine
   * to work out the arithmetic of a step, and doing that in the middle of a rule match disturbs
   * the match.
   *
   * @param target the steps of the level this one belongs to. A step which turns out to say
   *        nothing appends its own sub-steps here instead of itself.
   */
  void appendReadableTo(IASTAppendable target) {
    // both sides together, or only one of them gets its arithmetic worked out and a step which
    // changed nothing stops looking like one
    boolean tidy = StepDisplay.carriesInternals(input) || StepDisplay.carriesInternals(display);
    IExpr shownInput = tidy ? StepDisplay.normalize(input) : input;
    IExpr shownResult = tidy ? StepDisplay.normalize(display) : display;
    IASTAppendable subSteps = F.ListAlloc(children.size() + (truncated ? 1 : 0));
    for (StepNode child : children) {
      child.appendReadableTo(subSteps);
    }
    if (truncated) {
      subSteps.append(truncatedStep());
    }
    if (tidy && shownInput.equals(shownResult)) {
      // machinery rather than mathematics: the rule set moved to a spelling of its own and, once
      // that is written the ordinary way, the expression came back unchanged. Whatever it went on
      // to do belongs to the step above.
      //
      // Only a step which needed tidying up can be dropped this way. A step whose input and result
      // are equal to begin with is an annotation - `addTraceInfoStep` records the expression
      // against itself on purpose, which is how the quadratic formula narrates its arithmetic.
      for (int i = 1; i < subSteps.size(); i++) {
        target.append(subSteps.get(i));
      }
      return;
    }
    target.append(F.List(F.HoldForm(shownInput), F.HoldForm(shownResult), hints, subSteps));
  }

  /** The marker step which says that further sub-steps were dropped. */
  static IAST truncatedStep() {
    return F.List(F.HoldForm(S.Null), F.HoldForm(S.Null),
        F.List(S.TraceForm, F.$str(StepsTree.TRUNCATED_KEY)), F.CEmptyList);
  }
}
