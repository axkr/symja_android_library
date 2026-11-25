package org.matheclipse.core.eval.exception;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/** Exception which will be thrown, if the {@link Config#MAX_AST_SIZE} limit was exceeded. */
public class ASTElementLimitExceeded extends LimitException {

  private static final long serialVersionUID = 8925451277545397036L;

  private final long fRequestedCapacity;

  public ASTElementLimitExceeded(final long requestedCapacity) {
    fRequestedCapacity = requestedCapacity;
  }

  /**
   * Set the exceeded limit to <code>(long)rowDimension*(long)columnDimension</code>.
   *
   * @param rowDimension
   * @param columnDimension
   */
  public ASTElementLimitExceeded(int rowDimension, int columnDimension) {
    fRequestedCapacity = rowDimension * (long) columnDimension;
  }

  @Override
  public String getMessage() {
    return "Maximum AST dimension " + fRequestedCapacity + " exceeded";
  }

  /**
   * Throws a new <code>ASTElementLimitExceeded</code> exception, if the {@link Config#MAX_AST_SIZE}
   * limit was exceeded.
   *
   * <p>
   * Usage:
   *
   * <pre>
   * if (Config.MAX_AST_SIZE < requestedCapacity) {
   *   ASTElementLimitExceeded.throwIt(requestedCapacity);
   * }
   * </pre>
   *
   * @param requestedCapacity
   */
  public static void throwIt(final long requestedCapacity) {
    throw new ASTElementLimitExceeded(requestedCapacity);
  }

  /**
   * The bytes of the smallest element of a new expression: one compressed reference. A count of
   * elements which needs more than the heap at this size can never be allocated.
   */
  private static final long BYTES_PER_ELEMENT = 4L;

  /**
   * Check that a nested list with <code>dimensions[0] * dimensions[1] * ...</code> elements can be
   * built before it is allocated. A dimension larger than {@link Config#MAX_AST_SIZE}, the limit of
   * one expression, throws this exception. More elements in all than the Java heap could hold even
   * as bare references print <code>General::nomem</code> and abort the evaluation, which is what
   * running out of memory does anyway, only much later and without a message.
   *
   * @param dimensions the number of elements on each level
   */
  public static void checkAllocation(final long... dimensions) {
    long count = 1L;
    for (long d : dimensions) {
      if (d <= 0L) {
        // nothing to allocate below an empty level
        return;
      }
      if (d > Config.MAX_AST_SIZE) {
        throwIt(d);
      }
      count = count > Long.MAX_VALUE / d ? Long.MAX_VALUE : count * d;
    }
    checkMemory(count);
  }

  /**
   * Abort with <code>General::nomem</code> if <code>count</code> elements cannot fit into the
   * memory of the virtual machine. Unlike {@link #checkAllocation(long...)} the size limit of one
   * expression is not applied: the elements may be collected step by step.
   *
   * @param count the number of elements
   */
  public static void checkMemory(final long count) {
    final long maxMemory = Runtime.getRuntime().maxMemory();
    if (maxMemory != Long.MAX_VALUE && count > maxMemory / BYTES_PER_ELEMENT) {
      // The current computation was aborted because there was insufficient memory available to
      // complete the computation.
      Errors.printMessage(S.General, "nomem", F.CEmptyList, EvalEngine.get());
      throw AbortException.ABORTED;
    }
  }
}
