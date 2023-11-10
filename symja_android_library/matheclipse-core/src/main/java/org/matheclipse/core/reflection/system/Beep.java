package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Beep extends AbstractEvaluator {

  public Beep() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
      // this should work on Windows using awt:
      java.awt.Toolkit.getDefaultToolkit().beep();
    } else {
      System.out.print("\007"); // \007 is the ASCII bell
      System.out.flush();
    }
    return S.Null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_0_0;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
