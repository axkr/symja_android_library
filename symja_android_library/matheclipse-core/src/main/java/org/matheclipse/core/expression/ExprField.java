package org.matheclipse.core.expression;

import org.apfloat.Apfloat;
import org.hipparchus.Field;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

/** Class for representing a field of <code>IExpr</code>. */
/* package private */ final class ExprField implements Field<IExpr> {

  @Override
  public IExpr getOne() {
    EvalEngine engine = EvalEngine.get(); // thread local evaluation engine instance
    if (engine.isArbitraryMode()) {
      return F.num(new Apfloat(1, engine.getNumericPrecision()));
    }
    return F.C1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getZero() {
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  public Class<IExpr> getRuntimeClass() {
    return IExpr.class;
  }
}
