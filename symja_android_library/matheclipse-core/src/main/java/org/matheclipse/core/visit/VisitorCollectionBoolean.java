package org.matheclipse.core.visit;

import java.util.Collection;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class VisitorCollectionBoolean<T extends IExpr> extends AbstractVisitorBoolean {
  /**
   * Number of visited nodes between two interruption checks, minus one, for masking. Checking every
   * node would put a {@code Thread.isInterrupted()} call on a walk that runs over every node of
   * every expression; checking every 16384 keeps that off the hot path while still reacting in a
   * fraction of a second.
   */
  private static final int INTERRUPT_CHECK_MASK = 0x3FFF;

  protected int fHeadOffset;

  protected Collection<T> fCollection;

  /** Nodes visited by this walk so far; only used to space out the interruption checks. */
  private int fVisitedNodes;

  public VisitorCollectionBoolean(Collection<T> collection) {
    super();
    fHeadOffset = 1;
    fCollection = collection;
  }

  public VisitorCollectionBoolean(int hOffset, Collection<T> collection) {
    super();
    fHeadOffset = hOffset;
    fCollection = collection;
  }

  @Override
  public boolean visit(IAST list) {
    // This walk is unbounded: it has no recursion or size limit, and on a large enough expression
    // it runs for minutes. Without this check it also ignored interruption entirely, so a
    // TimeConstrained/evaluateWithTimeout deadline could not stop it - the caller got $Aborted on
    // time but the worker thread kept running at full speed, and enough of those accumulating
    // starved every evaluation that followed. Throwing the same exception EvalEngine's own
    // interruption checks throw unwinds the walk and lets the thread finish.
    if ((++fVisitedNodes & INTERRUPT_CHECK_MASK) == 0
        && Thread.currentThread().isInterrupted()) {
      throw TimeoutException.TIMED_OUT;
    }
    list.forEach(fHeadOffset, list.size(), x -> x.accept(this));
    return false;
  }
}
