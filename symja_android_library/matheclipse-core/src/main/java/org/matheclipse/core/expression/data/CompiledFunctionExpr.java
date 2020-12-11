package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class CompiledFunctionExpr extends DataExpr<AbstractFunctionEvaluator> {

  private static final long serialVersionUID = 3098987741558862963L;

  public static CompiledFunctionExpr newInstance(
      IAST variables, IAST types, IExpr expr, final AbstractFunctionEvaluator value) {
    return new CompiledFunctionExpr(variables, types, expr, value);
  }

  private IAST variables;
  private IAST types;

  private IExpr expr;

  protected CompiledFunctionExpr(
      IAST variables, IAST types, IExpr expr, final AbstractFunctionEvaluator function) {
    super(S.CompiledFunction, function);
    this.variables = variables;
    this.types = types;
    this.expr = expr;
  }

  @Override
  public IExpr copy() {
    return new CompiledFunctionExpr(variables, types, expr, fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof CompiledFunctionExpr) {
      return fData.equals(((CompiledFunctionExpr) obj).fData);
    }
    return false;
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    return fData.evaluate(ast, engine);
  }

  public IExpr getExpr() {
    return expr;
  }

  public IAST getVariables() {
    return variables;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 461 : 461 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return COMPILEFUNCTONID;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("CompiledFunction(Arg count: ");
    buf.append(variables.argSize());
    buf.append(" Types: {");
    for (int i = 1; i < types.size(); i++) {
      buf.append(types.get(i));
      if (i < types.size() - 1) {
        buf.append(",");
      }
    }
    buf.append("} Variables: {");
    for (int i = 1; i < variables.size(); i++) {
      buf.append(variables.get(i));
      if (i < variables.size() - 1) {
        buf.append(",");
      }
    }
    buf.append("}");
    buf.append(")");
    return buf.toString();
  }
}
