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

  @Override
  default IAST normal(boolean nilIfUnevaluated) {
    // Function `1` not implemented for `2`.
    Errors.printMessage(S.General, "zznotimpl2", F.List(S.Normal, this));
    return nilIfUnevaluated ? F.NIL : F.Hold(F.unaryAST1(S.Normal, this));
  }
}
