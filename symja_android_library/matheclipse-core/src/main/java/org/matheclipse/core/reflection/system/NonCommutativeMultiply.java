package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NonCommutativeMultiply extends AbstractFunctionEvaluator {

  public NonCommutativeMultiply() {}

  @Override
  public IExpr evaluate(final IAST lst, EvalEngine engine) {
    // Function `1` not implemented.
    return Errors.printMessage(S.NonCommutativeMultiply, "zznotimpl",
        F.List("NonCommutativeMultiply (operator **)"));
  }

  @Override
  public int status() {
    return ImplementationStatus.NO_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // don't set OneIdentity attribute!
    newSymbol.setAttributes(ISymbol.FLAT | ISymbol.LISTABLE);
  }
}
