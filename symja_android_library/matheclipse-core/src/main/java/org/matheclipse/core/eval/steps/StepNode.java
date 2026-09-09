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

  StepNode(IExpr input, IExpr display, IExpr identity, IAST hints, int level) {
    this.input = input;
    this.display = display;
    this.identity = identity;
    this.hints = hints;
    this.level = level;
  }

  /**
   * One level of the finished derivation, or the marker which says the rest is not shown.
   *
   * <p>
   * The depth is counted here rather than while the steps are collected, because it is only here
   * that a step is known to be worth showing: the rule sets reach a result through rewrites which
   * say nothing once they are written the ordinary way, and a cap applied before those are dropped
   * would spend the reader's budget on steps the reader never sees.
   *
   * @param steps the steps of one level
   * @param target where they are appended
   * @param depth how deep in the finished derivation this level sits, <code>1</code> at the top
   * @param maxDepth the deepest level which is shown
   * @param rename <code>Rule(boundVariable, dummy)</code> of a substitution made further out, or
   *        {@link F#NIL} while none is in force
   */
  static void appendAll(List<StepNode> steps, IASTAppendable target, int depth, int maxDepth,
      IExpr rename) {
    if (steps.isEmpty()) {
      return;
    }
    if (depth > maxDepth) {
      target.append(truncatedStep());
      return;
    }
    for (StepNode step : steps) {
      step.appendReadableTo(target, depth, maxDepth, rename);
    }
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
   * @param depth how many steps deep in the finished derivation this one would sit, counting only
   *        the steps a reader is shown
   * @param maxDepth the deepest a step may sit before the rest is replaced by a marker
   * @param rename <code>Rule(boundVariable, dummy)</code> of a substitution made further out, so
   *        that every step below the one which made it is written in the same variable, or
   *        {@link F#NIL} while none is in force
   */
  void appendReadableTo(IASTAppendable target, int depth, int maxDepth, IExpr rename) {
    // both sides together, or only one of them gets its arithmetic worked out and a step which
    // changed nothing stops looking like one
    boolean tidy = StepDisplay.carriesInternals(input) || StepDisplay.carriesInternals(display);
    IExpr heldInput = tidy ? StepDisplay.normalizeHeld(input) : input;
    IExpr heldResult = tidy ? StepDisplay.normalizeHeld(display) : display;
    // read off before `unhold` takes the wrapper away: the step says it in words instead
    IExpr substitution = tidy ? StepDisplay.substitutionIn(heldResult) : F.NIL;
    IExpr shownInput = renamed(tidy ? StepDisplay.unhold(heldInput) : heldInput, rename);
    IExpr shownResult = tidy ? StepDisplay.unhold(heldResult) : heldResult;

    IExpr note = F.NIL;
    if (substitution.isPresent()) {
      // The rule set went on calling the substituted variable by the name of the variable being
      // integrated over. Below this step it is a variable of its own and needs a name of its own,
      // or the integral left standing would say something false. What it stands for is written in
      // the outer variable, so that half takes the rename already in force.
      IExpr bound = substitution.first();
      IExpr replacement = renamed(substitution.second(), rename);
      IExpr dummy = StepDisplay.freeDummy(shownResult, replacement,
          rename.isPresent() ? rename.second() : F.NIL);
      if (dummy.isPresent()) {
        shownResult = F.subst(shownResult, bound, dummy);
        rename = F.Rule(bound, dummy);
      } else {
        dummy = bound;
      }
      note = substitutionStep(shownResult, dummy, replacement);
    } else {
      shownResult = renamed(shownResult, rename);
    }

    boolean saysNothing = tidy && substitution.isNIL() && shownInput.equals(shownResult);
    // a step which is dropped is not a level of its own, so its children keep this one's depth
    int childDepth = saysNothing ? depth : depth + 1;
    IASTAppendable subSteps = F.ListAlloc(children.size() + 1);
    if (note.isPresent()) {
      subSteps.append(note);
    }
    appendAll(children, subSteps, childDepth, maxDepth, rename);
    if (saysNothing) {
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

  /**
   * The note which says what a step substituted, as an annotation rather than a rewrite: the
   * expression is unchanged and only the sentence carries anything.
   *
   * <p>
   * The head is <code>Integrate</code> because a substitution is only ever made by an integration
   * rule, and because a derivation of an integral is expected to be made of steps which belong to
   * <code>Integrate</code> - see <code>TraceFormTest#testRubiInternalsAreNotSteps</code>.
   */
  private static IAST substitutionStep(IExpr shown, IExpr dummy, IExpr replacement) {
    return F.List(F.HoldForm(shown), F.HoldForm(shown), //
        F.List(S.Integrate, F.$str("Substitution"), dummy, replacement), F.CEmptyList);
  }

  /**
   * The expression as it reads under the substitution in force, which is what every step below the
   * one that made it is written in.
   */
  private static IExpr renamed(IExpr expr, IExpr rename) {
    return rename.isPresent() ? F.subst(expr, rename.first(), rename.second()) : expr;
  }

  /** The marker step which says that further sub-steps were dropped. */
  static IAST truncatedStep() {
    return F.List(F.HoldForm(S.Null), F.HoldForm(S.Null),
        F.List(S.TraceForm, F.$str(StepsTree.TRUNCATED_KEY)), F.CEmptyList);
  }
}
