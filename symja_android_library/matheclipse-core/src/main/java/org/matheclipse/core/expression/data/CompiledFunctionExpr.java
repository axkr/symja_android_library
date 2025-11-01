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

/**
 * A data holder for a compiled function representation.
 * <p>
 * This class wraps metadata about a function that has been compiled to a Java class implementing
 * {@link org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator}. The
 * {@code compiledJavaClass} may be transient and unset after deserialization; in that case
 * {@link #evaluate(IAST, EvalEngine)} returns an appropriate error.
 * </p>
 *
 * @see AbstractFunctionEvaluator
 * @see IDataExpr
 */
public class CompiledFunctionExpr implements IDataExpr<Class<?>> {
  private static final long serialVersionUID = 3098987741558862963L;

  /**
   * The Java {@link Class} produced by compilation that implements the function evaluator. This
   * field is transient because the runtime class may not be available after deserialization.
   */
  protected transient Class<?> compiledJavaClass = null;

  /**
   * Create a new {@link CompiledFunctionExpr} instance.
   *
   * @param variables the AST listing function variables
   * @param types the AST listing parameter types
   * @param expr the original function expression (body)
   * @param clazz the compiled Java class implementing the function
   * @return a new {@link CompiledFunctionExpr}
   */
  public static CompiledFunctionExpr newInstance(IAST variables, IAST types, IExpr expr,
      Class<?> clazz) {
    return new CompiledFunctionExpr(variables, types, expr, clazz);
  }

  /**
   * The AST describing the formal variables of the compiled function.
   */
  private IAST variables;

  /**
   * The AST describing the parameter types of the compiled function.
   */
  private IAST types;

  /**
   * The original function body expression that was compiled.
   */
  private IExpr expr;

  /**
   * Protected constructor used by the factory method
   * {@link #newInstance(IAST, IAST, IExpr, Class)}.
   *
   * @param variables the function variables AST
   * @param types the parameter types AST
   * @param expr the function body expression
   * @param clazz the compiled Java class implementing the function
   */
  protected CompiledFunctionExpr(IAST variables, IAST types, IExpr expr, Class<?> clazz) {
    this.compiledJavaClass = clazz;
    this.variables = variables;
    this.types = types;
    this.expr = expr;
  }


  /**
   * Create a shallow copy of this compiled function descriptor.
   * <p>
   * The returned object shares references to the contained ASTs and expression.
   * </p>
   *
   * @return a new {@link CompiledFunctionExpr} with the same internal references
   */
  @Override
  public IExpr copy() {
    return new CompiledFunctionExpr(variables, types, expr, compiledJavaClass);
  }

  /**
   * Equality is defined by the original expression and the associated ASTs. Two instances compare
   * equal only if they contain equal expression, variable and type ASTs, and both have a non-null
   * compiled class reference (the latter is checked in the caller).
   *
   * @param obj the object to compare with
   * @return {@code true} if equal, {@code false} otherwise
   */
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

  /**
   * Compare this expression with another {@link IExpr}.
   * <p>
   * If the other expression is also a {@link CompiledFunctionExpr}, comparison is delegated to the
   * contained expression and ASTs. If the other expression is an AST, an ordering is imposed to
   * keep compiled functions distinct, otherwise the comparison uses the hierarchy ordering.
   * </p>
   *
   * @param expr the expression to compare against
   * @return negative, zero, or positive integer as this is less than, equal to, or greater than
   *         {@code expr}
   */
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

  /**
   * Attempt to evaluate this compiled function by instantiating the compiled Java class.
   * <p>
   * If {@link #compiledJavaClass} is {@code null} (e.g. after deserialization), an error message is
   * produced. Otherwise the class is instantiated and it's
   * {@link AbstractFunctionEvaluator#evaluate(IAST, EvalEngine)} method is invoked.
   * </p>
   *
   * @param ast the application AST (function call)
   * @param engine the evaluation engine
   * @return the evaluation result or {@link F#NIL} on failure
   */
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

  /**
   * Return the original function body expression.
   *
   * @return the contained {@link IExpr} representing the function body
   */
  public IExpr getExpr() {
    return expr;
  }

  /**
   * Return the AST describing the function variables.
   *
   * @return the variables {@link IAST}
   */
  public IAST getVariables() {
    return variables;
  }

  @Override
  public int hashCode() {
    return 461 + expr.hashCode();
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

  /**
   * Produce a concise textual representation useful for debugging. The string includes argument
   * count, types and variable names.
   *
   * @return a debug string describing this compiled function
   */
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

  /**
   * Return the head symbol for this expression type.
   *
   * @return {@link S#CompiledFunction}
   */
  @Override
  public IExpr head() {
    return S.CompiledFunction;
  }

  /**
   * Return the underlying compiled Java class as the data payload.
   *
   * @return the compiled {@link Class} or {@code null} if absent
   */
  @Override
  public Class<?> toData() {
    return compiledJavaClass;
  }
}
