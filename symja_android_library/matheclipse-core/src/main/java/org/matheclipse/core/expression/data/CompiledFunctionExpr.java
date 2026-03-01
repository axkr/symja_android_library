package org.matheclipse.core.expression.data;

import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A data holder for a compiled function representation.
 */
public class CompiledFunctionExpr implements IDataExpr<Class<?>> {
  private static final long serialVersionUID = 3098987741558862963L;

  public static CompiledFunctionExpr newInstance(IAST variables, IAST types, IExpr expr,
      Class<?> clazz, IExpr runtimeAttributes) {
    return new CompiledFunctionExpr(variables, types, expr, clazz, runtimeAttributes);
  }

  protected transient Class<?> compiledJavaClass = null;
  private IAST variables;
  private IAST types;
  private IExpr expr;
  private IAST runtimeAttributes;

  private int attributes = ISymbol.NOATTRIBUTE;

  protected CompiledFunctionExpr(IAST variables, IAST types, IExpr expr, Class<?> clazz,
      IExpr runtimeAttributes) {
    this.compiledJavaClass = clazz;
    this.variables = variables;
    this.types = types;
    this.expr = expr;
    this.runtimeAttributes =
        runtimeAttributes.isPresent() ? runtimeAttributes.makeList() : F.CEmptyList;
    attributes =
        AttributeFunctions.getSymbolsAsAttributes(this.runtimeAttributes, EvalEngine.get());

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
  public int compareTo(IExpr expr) {
    if (expr instanceof CompiledFunctionExpr) {
      CompiledFunctionExpr compiledFunctionExpr = ((CompiledFunctionExpr) expr);
      int exprCmp = this.expr.compareTo(compiledFunctionExpr.expr);
      if (exprCmp != 0)
        return exprCmp;

      int variablesCmp = variables.compareTo(compiledFunctionExpr.variables);
      if (variablesCmp != 0)
        return variablesCmp;

      int typesCmp = types.compareTo(compiledFunctionExpr.types);
      if (typesCmp != 0)
        return typesCmp;

      return runtimeAttributes.compareTo(compiledFunctionExpr.runtimeAttributes);
    }
    if (expr.isAST()) {
      return -1 * expr.compareTo(this);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public IExpr copy() {
    return new CompiledFunctionExpr(variables, types, expr, compiledJavaClass, runtimeAttributes);
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
          && types.equals(compiledFunctionExpr.types) //
          && runtimeAttributes.equals(compiledFunctionExpr.runtimeAttributes);
    }
    return false;
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

  public int getAttributes() {
    return attributes;
  }

  public IExpr getExpr() {
    return expr;
  }

  public IExpr getRuntimeAttributes() {
    return runtimeAttributes;
  }

  public IAST getVariables() {
    return variables;
  }

  @Override
  public int hashCode() {
    return 461 + expr.hashCode() + 17 * runtimeAttributes.hashCode();
  }

  @Override
  public IExpr head() {
    return S.CompiledFunction;
  }

  /**
   * Return the internal hierarchy id for compiled functions.
   *
   * @return the hierarchy id {@link IExpr#COMPILEFUNCTONID}
   */
  @Override
  public int hierarchy() {
    return COMPILEFUNCTONID;
  }

  @Override
  public Class<?> toData() {
    return compiledJavaClass;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("CompiledFunction(Arg count: ").append(variables.argSize());
    buf.append(" Types: {");
    types.joinToString(buf, ",");
    buf.append("} Variables: {");
    variables.joinToString(buf, ",");
    buf.append("} Attributes: ").append(runtimeAttributes);
    buf.append(")");
    return buf.toString();
  }
}
