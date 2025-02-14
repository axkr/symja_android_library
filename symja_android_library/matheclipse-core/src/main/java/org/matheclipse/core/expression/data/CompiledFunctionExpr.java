package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

public class CompiledFunctionExpr implements IDataExpr<Class<?>> {
  private static final long serialVersionUID = 3098987741558862963L;

  protected transient Class<?> compiledJavaClass = null;

  public static CompiledFunctionExpr newInstance(IAST variables, IAST types, IExpr expr,
      Class<?> clazz) {
    return new CompiledFunctionExpr(variables, types, expr, clazz);
  }

  private IAST variables;
  private IAST types;
  private IExpr expr;

  protected CompiledFunctionExpr(IAST variables, IAST types, IExpr expr, Class<?> clazz) {
    this.compiledJavaClass = clazz;
    this.variables = variables;
    this.types = types;
    this.expr = expr;
  }

  @Override
  public IExpr copy() {
    return new CompiledFunctionExpr(variables, types, expr, compiledJavaClass);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof CompiledFunctionExpr //
        && compiledJavaClass != null //
        && ((CompiledFunctionExpr) obj).compiledJavaClass != null) {
      CompiledFunctionExpr compiledFunctionExpr = ((CompiledFunctionExpr) obj);
      return expr.equals(compiledFunctionExpr.expr) //
          && variables.equals(compiledFunctionExpr.variables) //
          && types.equals(compiledFunctionExpr.types);
    }
    return false;
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof CompiledFunctionExpr) {
      CompiledFunctionExpr compiledFunctionExpr = ((CompiledFunctionExpr) expr);
      int exprCmp = expr.compareTo(compiledFunctionExpr.expr);
      if (exprCmp != 0) {
        return exprCmp;
      }
      int variablesCmp = variables.compareTo(compiledFunctionExpr.variables);
      if (variablesCmp != 0) {
        return variablesCmp;
      }
      return types.compareTo(compiledFunctionExpr.types);
    }
    if (expr.isAST()) {
      return -1 * expr.compareTo(this);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (compiledJavaClass == null) {
      // Non deserialized expression `1`.
      return Errors.printMessage(S.CompiledFunction, "zzdsex", ast, engine);
    }
    AbstractFunctionEvaluator fun;
    try {
      fun = (AbstractFunctionEvaluator) compiledJavaClass.getDeclaredConstructor().newInstance();
      return fun.evaluate(ast, engine);
    } catch (ReflectiveOperationException rex) {
      // `1`.
      Errors.printMessage(S.CompiledFunction, rex, engine);
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
    return 461 + expr.hashCode();
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
    // for (int i = 1; i < types.size(); i++) {
    // buf.append(types.get(i));
    // if (i < types.size() - 1) {
    // buf.append(",");
    // }
    // }
    buf.append("} Variables: {");
    variables.joinToString(buf, ",");
    // for (int i = 1; i < variables.size(); i++) {
    // buf.append(variables.get(i));
    // if (i < variables.size() - 1) {
    // buf.append(",");
    // }
    // }
    buf.append("}");
    buf.append(")");
    return buf.toString();
  }

  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  @Override
  public IExpr head() {
    return S.CompiledFunction;
  }

  @Override
  public Class<?> toData() {
    return compiledJavaClass;
  }
}
