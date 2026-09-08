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

  /**
   * <code>true</code> while this step was recorded from a <code>Condition(body, test)</code>
   * right-hand-side whose guard has not been seen to hold yet.
   */
  boolean pendingCondition;

  /** <code>true</code> if sub-steps of this step were dropped by the depth or the node cap. */
  boolean truncated;

  StepNode(IExpr input, IExpr display, IExpr identity, IAST hints, int level,
      boolean pendingCondition) {
    this.input = input;
    this.display = display;
    this.identity = identity;
    this.hints = hints;
    this.level = level;
    this.pendingCondition = pendingCondition;
  }

  /**
   * <code>{HoldForm(input), HoldForm(result), {headSymbol, "RuleKey", hintArg...}, {subStep...}}
   * </code>.
   */
  IAST toExpr() {
    IASTAppendable subSteps = F.ListAlloc(children.size() + (truncated ? 1 : 0));
    for (StepNode child : children) {
      subSteps.append(child.toExpr());
    }
    if (truncated) {
      subSteps.append(truncatedStep());
    }
    return F.List(F.HoldForm(input), F.HoldForm(display), hints, subSteps);
  }

  /** The marker step which says that further sub-steps were dropped. */
  static IAST truncatedStep() {
    return F.List(F.HoldForm(S.Null), F.HoldForm(S.Null),
        F.List(S.TraceForm, F.$str(StepsTree.TRUNCATED_KEY)), F.CEmptyList);
  }
}
