package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Exception for the <a href=
 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Return.md">Return</a>
 * function. This exception throws the result of a Symja {@link S#Return} function.
 */
public class ReturnException extends FlowControlException {

  private static final long serialVersionUID = 6165872840807864554L;

  public static final ReturnException RETURN_FALSE = new ReturnException(S.False);

  public static final ReturnException RETURN_TRUE = new ReturnException(S.True);

  private final IExpr value;

  /** The construct named by <code>Return(value, form)</code>, or {@link F#NIL}. */
  private final IExpr form;

  /** This exception throws the result {@link S.Null} of a Symja {@link S#Return} function. */
  public ReturnException() {
    this(S.Null);
  }

  /**
   * This exception throws the result of a Symja {@link S#Return} function.
   *
   * @param value the returned value
   */
  public ReturnException(final IExpr value) {
    this(value, F.NIL);
  }

  /**
   * The exception of <code>Return(value, form)</code>, which leaves the constructs up to the
   * innermost one with head <code>form</code>.
   *
   * @param value the returned value
   * @param form the head of the construct to return from
   */
  public ReturnException(final IExpr value, final IExpr form) {
    this.value = value;
    this.form = form;
  }

  public IExpr getValue() {
    return value;
  }

  /** The head named by <code>Return(value, form)</code>, or {@link F#NIL}. */
  public IExpr getForm() {
    return form;
  }

  /**
   * Whether the construct with head <code>head</code> takes the value of this return: every one
   * which consumes a plain <code>Return(value)</code>, or the one which is named.
   */
  public boolean isFor(IExpr head) {
    return form.isNIL() || form.equals(head) || !isIterationForm(form)
        || !org.matheclipse.core.eval.EvalEngine.get().isEvaluatingIteration(form);
  }

  /**
   * The constructs <code>Return(value, form)</code> can leave as a whole: their evaluation takes
   * the value. A return naming another construct, or one which is not being evaluated, acts as
   * <code>Return(value)</code>.
   */
  public static boolean isIterationForm(IExpr form) {
    return form == S.Table || form == S.Map || form == S.Do || form == S.Scan || form == S.Select
        || form == S.Array || form == S.MapIndexed || form == S.Sum || form == S.Product;
  }
}
