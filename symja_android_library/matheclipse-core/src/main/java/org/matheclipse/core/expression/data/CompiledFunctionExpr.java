package org.matheclipse.core.expression.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class CompiledFunctionExpr extends DataExpr<Class<?>> {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final long serialVersionUID = 3098987741558862963L;

  public static CompiledFunctionExpr newInstance(
      IAST variables, IAST types, IExpr expr, Class<?> clazz) {
    return new CompiledFunctionExpr(variables, types, expr, clazz);
  }

  private IAST variables;
  private IAST types;

  private IExpr expr;

  protected CompiledFunctionExpr(IAST variables, IAST types, IExpr expr, Class<?> clazz) {
    super(S.CompiledFunction, clazz);
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

    AbstractFunctionEvaluator fun;
    try {
      fun = (AbstractFunctionEvaluator) fData.getDeclaredConstructor().newInstance();
      return fun.evaluate(ast, engine);
    } catch (ReflectiveOperationException rex) {
      LOGGER.error("CompiledFunctionExpr.evaluate() failed", rex);
    }
    return F.NIL;
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
    types.joinToString(buf, ",");
    //    for (int i = 1; i < types.size(); i++) {
    //      buf.append(types.get(i));
    //      if (i < types.size() - 1) {
    //        buf.append(",");
    //      }
    //    }
    buf.append("} Variables: {");
    variables.joinToString(buf, ",");
    //    for (int i = 1; i < variables.size(); i++) {
    //      buf.append(variables.get(i));
    //      if (i < variables.size() - 1) {
    //        buf.append(",");
    //      }
    //    }
    buf.append("}");
    buf.append(")");
    return buf.toString();
  }
}
