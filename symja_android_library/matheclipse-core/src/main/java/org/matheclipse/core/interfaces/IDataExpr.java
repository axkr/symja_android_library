package org.matheclipse.core.interfaces;

import org.matheclipse.core.eval.EvalEngine;

/** (I)nterface for a (Object) expressions */
public interface IDataExpr<T> extends IExpr {
  /** @return the data part of the IDataExpr */
  public T toData();

  @Override
  default IExpr eval(EvalEngine engine) {
    return evaluate(engine).orElse(this);
  }
}
