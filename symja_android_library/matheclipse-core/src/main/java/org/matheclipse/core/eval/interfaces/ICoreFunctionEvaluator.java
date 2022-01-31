package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Interface for &quot;core functions&quot; which don't have associated rules and are only defined
 * by a derived classes from this interface.
 */
public interface ICoreFunctionEvaluator extends IFunctionEvaluator {
  public static final ICoreFunctionEvaluator ARGS_EVALUATOR = new ICoreFunctionEvaluator() {

    @Override
    public final IExpr evaluate(IAST ast, final EvalEngine engine) {
      return F.NIL; // engine.evalAttributes(ast.topHead(), ast);
    }

    @Override
    public final IExpr numericEval(IAST ast, final EvalEngine engine) {
      return F.NIL; // engine.evalAttributes(ast.topHead(), ast);
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
  };

  @Override
  default IExpr numericEval(IAST ast, final EvalEngine engine) {
    return evaluate(ast, engine);
  }
}
