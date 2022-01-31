package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractCoreFunctionOptionEvaluator extends AbstractCoreFunctionEvaluator {
  protected IBuiltInSymbol[] optionSymbols = null;

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr[] option;
    int argSize = ast.argSize();
    if (optionSymbols == null) {
      option = new IExpr[0];
    } else {
      option = new IExpr[optionSymbols.length];
      argSize = AbstractFunctionEvaluator.determineOptions(option, ast, ast.argSize(),
          expectedArgSize(ast), optionSymbols, engine);
    }

    return evaluate(ast, argSize, option, engine);
  }

  protected void setOptions(final ISymbol symbol, IBuiltInSymbol lhsOptionSymbol, IExpr rhsValue) {
    optionSymbols = new IBuiltInSymbol[] {lhsOptionSymbol};
    super.setOptions(symbol, F.List(F.Rule(lhsOptionSymbol, rhsValue)));
  }

  protected void setOptions(final ISymbol symbol, IBuiltInSymbol[] lhsOptionSymbol,
      IExpr[] rhsValue) {
    optionSymbols = lhsOptionSymbol;
    IASTAppendable list = F.ListAlloc(rhsValue.length);
    for (int i = 0; i < rhsValue.length; i++) {
      list.append(F.Rule(lhsOptionSymbol[i], rhsValue[i]));
    }
    super.setOptions(symbol, list);
  }

  protected abstract IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine);
}
