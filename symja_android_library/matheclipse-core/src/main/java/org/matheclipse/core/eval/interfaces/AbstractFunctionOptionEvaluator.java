package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractFunctionOptionEvaluator extends AbstractFunctionEvaluator {
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
    super.setOptions(symbol, F.list(F.Rule(lhsOptionSymbol, rhsValue)));
  }

  protected void setOptions(final ISymbol symbol, IBuiltInSymbol[] lhsOptionSymbols,
      IExpr[] rhsValues) {
    optionSymbols = lhsOptionSymbols;
    IASTAppendable list =
        F.mapRange(0, rhsValues.length, i -> F.Rule(lhsOptionSymbols[i], rhsValues[i]));
    super.setOptions(symbol, list);
  }

  public IBuiltInSymbol[] getOptionSymbols() {
    return optionSymbols;
  }

  public abstract IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine);
}
