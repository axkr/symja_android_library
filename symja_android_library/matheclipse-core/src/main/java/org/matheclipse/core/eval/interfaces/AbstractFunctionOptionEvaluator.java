package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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
    return evaluate(ast, argSize, option, engine, ast);
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

  /**
   * Convenience method for matrix functions that declare {@code ZeroTest} as {@code options[0]} and
   * {@code Tolerance} as {@code options[1]}.
   *
   * @param ast the top-level call AST (arg1 must be the matrix)
   * @param options the option array populated by {@link #evaluate(IAST, EvalEngine)}
   * @param engine the evaluation engine
   * @return a zero predicate that respects both options
   */
  public static Predicate<IExpr> buildZeroChecker(final IAST ast, final IExpr[] options,
      EvalEngine engine) {
    IExpr zeroTest = options.length > 0 ? options[0] : S.Automatic;
    IExpr tolerance = options.length > 1 ? options[1] : S.Automatic;
    return AbstractMatrix1Expr.optionZeroTest(ast, engine, zeroTest, tolerance);
  }

  @Override
  public IBuiltInSymbol[] getOptionSymbols() {
    return optionSymbols;
  }

  public abstract IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST);
}
