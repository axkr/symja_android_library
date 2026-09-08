package org.matheclipse.core.eval.steps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.AbstractEvalStepListener;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import jakarta.annotation.Nullable;

/**
 * Collects the evaluation steps of one evaluation as a hierarchy: a step which was caused by
 * another step becomes its sub-step.
 *
 * <p>
 * Only the steps a built-in function or the pattern matcher announced explicitly are recorded (the
 * ones with <code>iterationCounter &lt; 0</code>, pushed by the
 * <code>EvalEngine#addTraceStep...</code> family); the raw fixed point iterations of
 * <code>EvalEngine#evalLoop</code> are churn and are skipped.
 *
 * <h3>How the hierarchy is found</h3>
 *
 * <code>EvalEngine#addEvaluatedTraceStep</code> records a step and then evaluates the very object
 * it recorded as the step's result. The evaluation loop opens a frame for that object, so a frame
 * whose input is <b>the identical object</b> of the last recorded step's result is the evaluation
 * <i>of</i> that step: everything recorded inside it becomes that step's sub-steps. Every other
 * frame is transparent - when it closes, its steps are moved into the frame around it - so the
 * hierarchy follows the derivation and not the depth of the Java recursion.
 *
 * <p>
 * A frame which evaluated to nothing (a rule which did not match, a
 * <code>Condition</code> whose guard failed) is told to discard itself, and its steps go away
 * with it.
 */
public class StepsListener extends AbstractEvalStepListener {

  /**
   * Check that every <code>tearDown</code> closes the frame its <code>setUp</code> opened. Off
   * outside a debug build: an interrupted worker thread can leave the stack unbalanced through no
   * fault of the listener.
   */
  private static final boolean CHECK_MARKER = Config.DEBUG;

  /** How many steps are recorded before the rest is dropped. */
  public static final int DEFAULT_MAX_NODES = 5000;

  /** One nesting level of the collection: the steps recorded while it is open. */
  private static final class Frame {

    /**
     * The step whose evaluation this frame is, or <code>null</code> if the frame is
     * transparent.
     */
    final StepNode attachTo;

    /** The innermost step this frame is inside of, used to mark a step as truncated. */
    final StepNode owner;

    /** What <code>setUp</code> was given, to check the frames are balanced. */
    final Object marker;

    final List<StepNode> steps = new ArrayList<StepNode>();

    final int level;

    Frame(StepNode attachTo, StepNode owner, Object marker, int level) {
      this.attachTo = attachTo;
      this.owner = owner;
      this.marker = marker;
      this.level = level;
    }
  }

  private final Deque<Frame> fStack = new ArrayDeque<Frame>();

  private final Frame fRoot;

  /** The deepest nesting of steps which is kept. */
  private final int fMaxDepth;

  /** How many steps may be recorded at all. */
  private final int fMaxNodes;

  /** The finest level of steps which is recorded, see {@link StepLevel}. */
  private final int fStepLevel;

  private int fNodeCount = 0;

  /** Steps were dropped because {@link #fMaxNodes} was reached. */
  private boolean fGlobalTruncated = false;

  /**
   * @param maxDepth the deepest nesting of steps which is kept, <code>1</code> for top level steps
   *        only
   * @param stepLevel the finest level of steps which is recorded, see {@link StepLevel}
   */
  public StepsListener(int maxDepth, int stepLevel) {
    this(maxDepth, stepLevel, DEFAULT_MAX_NODES);
  }

  public StepsListener(int maxDepth, int stepLevel, int maxNodes) {
    super();
    fMaxDepth = maxDepth;
    fStepLevel = stepLevel;
    fMaxNodes = maxNodes;
    fRoot = new Frame(null, null, null, 1);
    fStack.push(fRoot);
  }

  @Override
  public int stepLevel() {
    return fStepLevel;
  }

  @Override
  public boolean traceRewriteRules() {
    return fStepLevel >= StepLevel.RULE;
  }

  @Override
  public void setUp(IExpr inputExpr, int recursionDepth, @Nullable Object stackMarker) {
    Frame parent = fStack.peek();
    List<StepNode> siblings = parent.steps;
    StepNode last = siblings.isEmpty() ? null : siblings.get(siblings.size() - 1);
    if (last != null && last.identity == inputExpr) {
      // the evaluation of the expression the last step rewrote its input to
      fStack.push(new Frame(last, last, stackMarker, last.level + 1));
    } else {
      // a frame of its own: transparent, its steps belong to the frame around it
      fStack.push(new Frame(null, parent.owner, stackMarker, parent.level));
    }
  }

  @Override
  public void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter,
      IAST listOfHints) {
    if (iterationCounter >= 0L) {
      // a raw fixed point iteration of the evaluation loop, not an announced step
      return;
    }
    if (listOfHints == null || !listOfHints.isList() || listOfHints.argSize() < 2) {
      return;
    }
    if (isInternal(listOfHints)) {
      return;
    }
    Frame frame = fStack.peek();
    if (frame.level > fMaxDepth) {
      markTruncated(frame.owner);
      return;
    }
    if (fNodeCount >= fMaxNodes) {
      fGlobalTruncated = true;
      return;
    }
    // A rule with a `/;` guard records `Condition(body, test)`. Only the body is worth showing,
    // and the body is also what the evaluation goes on with: `Condition` never reaches the
    // evaluation loop's frame - it is answered by a fast path - and it is `conditionEval` which
    // then evaluates the body, handing that very object to the next `setUp`. So the body, not the
    // wrapper, is what the sub-steps will be found under.
    IExpr identity = resultExpr.isCondition() ? resultExpr.first() : resultExpr;
    fNodeCount++;
    // The hint list is deliberately *not* copied: `addEvaluatedTraceStep` completes it after this
    // call returns, either by appending the evaluated result or by replacing the `Slot1`
    // placeholder with it. Rendering happens when the evaluation is over, so the reference sees
    // the finished list.
    StepNode node = new StepNode(inputExpr, identity, identity, listOfHints, frame.level);
    frame.steps.add(node);
    recorded(node);
  }

  /**
   * A step has been recorded. Overridden by {@link DialogStepsListener} to show it to a reader
   * before the evaluation goes on; the steps which were filtered out never reach it.
   */
  protected void recorded(StepNode node) {}

  @Override
  public void tearDown(@Nullable IExpr result, int recursionDepth, boolean commitTraceFrame,
      @Nullable Object stackMarker) {
    if (fStack.size() <= 1) {
      // unbalanced: an interrupted or aborted evaluation. Keep what was collected.
      return;
    }
    Frame frame = fStack.pop();
    if (CHECK_MARKER && stackMarker != null && frame.marker != stackMarker) {
      throw new IllegalStateException(
          "TraceForm: the frame closed here is not the frame which was opened");
    }
    if (!commitTraceFrame) {
      return;
    }
    if (frame.attachTo != null) {
      frame.attachTo.children.addAll(frame.steps);
    } else {
      fStack.peek().steps.addAll(frame.steps);
    }
  }

  @Override
  public void tearDown(IExpr result, IAST hints, int recursionDepth, boolean commitTraceFrame,
      @Nullable Object stackMarker) {
    tearDown(result, recursionDepth, commitTraceFrame, stackMarker);
  }

  /**
   * Is this a step of a helper function which only exists inside a rule set?
   *
   * <p>
   * The Rubi integration rules are written on top of about a hundred predicates and utilities of
   * their own - <code>Rubi`functionoftrig</code>, <code>Rubi`trigq</code> and the like - and each
   * of them is an ordinary rewrite rule, so every integral would otherwise bury its two or three
   * real steps under pages of the rule set deciding what kind of integrand it is looking at. The
   * integration rules themselves are steps and are announced as <code>Integrate::RubiRule</code>,
   * which is unaffected by this.
   */
  private static boolean isInternal(IAST listOfHints) {
    IExpr head = listOfHints.first();
    if (!head.isSymbol()) {
      return false;
    }
    ISymbol symbol = (ISymbol) head;
    return symbol.isContext(Context.RUBI) //
        // the inert markers the Rubi rules use for a deactivated trigonometric function
        || symbol.getSymbolName().startsWith("\u00a7");
  }

  private void markTruncated(@Nullable StepNode step) {
    if (step != null) {
      step.truncated = true;
    } else {
      fGlobalTruncated = true;
    }
  }

  /** How many steps were recorded. */
  public int size() {
    return fRoot.steps.size();
  }

  /** Were steps dropped because the overall limit was reached? */
  public boolean isTruncated() {
    return fGlobalTruncated;
  }

  /**
   * The collected steps as the list which {@link StepsTree} reads.
   *
   * @return <code>{step, ...}</code>
   */
  public IAST toExpr() {
    List<StepNode> steps = fRoot.steps;
    IASTAppendable result = F.ListAlloc(steps.size() + (fGlobalTruncated ? 1 : 0));
    for (StepNode step : steps) {
      step.appendReadableTo(result);
    }
    if (fGlobalTruncated) {
      result.append(StepNode.truncatedStep());
    }
    return result;
  }
}
