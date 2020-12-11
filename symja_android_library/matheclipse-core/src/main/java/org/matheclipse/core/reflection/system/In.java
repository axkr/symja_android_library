package org.matheclipse.core.reflection.system;

import java.io.StringWriter;

import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalHistory;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the specified history <code>In</code> line from the <code>EvalEngine's</code> history list.
 * <br>
 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
 */
public class In extends AbstractCoreFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST0()) {
      final EvalHistory list = engine.getEvalHistory();
      if (list != null) {
        return list.getIn(-1);
      }
      return F.NIL;
    }

    final int position = ast.arg1().toIntDefault(0);
    if (position != 0) {
      final EvalHistory list = engine.getEvalHistory();
      if (list != null) {
        return list.getIn(position);
      }
    }
    return F.NIL;
  }

  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_0_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDFIRST);
  }
}
