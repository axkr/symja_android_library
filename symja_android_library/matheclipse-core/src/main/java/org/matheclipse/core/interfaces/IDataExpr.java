package org.matheclipse.core.interfaces;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/** (I)nterface for a (Object) expressions */
public interface IDataExpr<T> extends IExpr {
  /** @return the data part of the IDataExpr */
  public T toData();

  @Override
  default IExpr eval(EvalEngine engine) {
    return evaluate(engine).orElse(this);
  }

  /**
   * Evaluate a sum, product or power in which this object occurs, for an object that knows how to
   * combine with the other operands - an image adds, multiplies and raises its samples one by one.
   *
   * @param ast a <code>Plus(...)</code> or <code>Times(...)</code> with this object among its
   *        arguments, or a <code>Power(this, exponent)</code>
   * @param engine the evaluation engine
   * @return {@link F#NIL} if this object does not combine with the operands
   */
  default IExpr arithmetic(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  /**
   * The options of this object.
   *
   * @param all <code>false</code> for the options <code>Options(obj)</code> reports, <code>true</code>
   *        for every option the object has a value for, from which <code>Options(obj, name)</code>
   *        picks
   * @return {@link F#NIL} if the object has no options
   */
  default IAST options(boolean all) {
    return F.NIL;
  }

  @Override
  default IAST normal(boolean nilIfUnevaluated) {
    // Function `1` not implemented for `2`.
    Errors.printMessage(S.General, "zznotimpl2", F.List(S.Normal, this));
    return nilIfUnevaluated ? F.NIL : F.Hold(F.Normal(this));
  }
}
