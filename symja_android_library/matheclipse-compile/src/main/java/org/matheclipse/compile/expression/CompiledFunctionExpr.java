package org.matheclipse.compile.expression;

import org.matheclipse.compile.RuntimeOptions;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.compile.ICompiledFunction;
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
public class CompiledFunctionExpr implements IDataExpr<Class<?>>, ICompiledFunction {
  private static final long serialVersionUID = 3098987741558862963L;

  public static CompiledFunctionExpr newInstance(IAST variables, IAST types, IExpr expr,
      Class<?> clazz, IExpr runtimeAttributes, RuntimeOptions runtimeOptions) {
    return new CompiledFunctionExpr(variables, types, expr, clazz, runtimeAttributes,
        runtimeOptions);
  }

  /**
   * Wrap the <code>double</code> result of an integer-typed compiled function. If the computed
   * value is a finite whole number within the exactly representable integer range, return an
   * {@link org.matheclipse.core.interfaces.IInteger}; otherwise fall back to a real
   * {@link org.matheclipse.core.interfaces.INum} value.
   *
   * @param value the computed <code>double</code> value
   * @return an exact integer or a real number
   */
  public static IExpr symjifyInteger(double value) {
    if (!Double.isNaN(value) && !Double.isInfinite(value) && value == Math.rint(value)
        && Math.abs(value) < 9.007199254740992E15 /* 2^53 */) {
      return F.ZZ((long) value);
    }
    return F.num(value);
  }

  /**
   * Wrap the <code>double</code> result of an integer-typed compiled function without checking it,
   * which is what <code>RuntimeOptions -> "Speed"</code> asks for.
   *
   * <p>
   * This is {@link #symjifyInteger(double)} with the range test left out: a value which is too
   * large for a <code>long</code> silently comes back as
   * {@link Long#MAX_VALUE}/{@link Long#MIN_VALUE}, and a fractional value is truncated. That is
   * the point - the caller asked not to pay for the test - so the result is only an integer in the
   * mathematical sense while the computation stays inside the machine integer range.
   *
   * @param value the computed <code>double</code> value
   * @return the value as an exact integer, however it got there
   */
  public static IExpr symjifyIntegerUnchecked(double value) {
    return F.ZZ((long) value);
  }

  protected transient Class<?> compiledJavaClass = null;
  private IAST variables;
  private IAST types;
  private IExpr expr;
  private IAST runtimeAttributes;
  private RuntimeOptions runtimeOptions;

  private int attributes = ISymbol.NOATTRIBUTE;

  protected CompiledFunctionExpr(IAST variables, IAST types, IExpr expr, Class<?> clazz,
      IExpr runtimeAttributes, RuntimeOptions runtimeOptions) {
    this.compiledJavaClass = clazz;
    this.variables = variables;
    this.types = types;
    this.expr = expr;
    this.runtimeAttributes =
        runtimeAttributes.isPresent() ? runtimeAttributes.makeList() : F.CEmptyList;
    this.runtimeOptions = runtimeOptions == null ? RuntimeOptions.DEFAULT : runtimeOptions;
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

      int runtimeAttributesCmp =
          runtimeAttributes.compareTo(compiledFunctionExpr.runtimeAttributes);
      if (runtimeAttributesCmp != 0)
        return runtimeAttributesCmp;

      return runtimeOptions.toExpr().compareTo(compiledFunctionExpr.runtimeOptions.toExpr());
    }
    if (expr.isAST()) {
      return -1 * expr.compareTo(this);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public IExpr copy() {
    return new CompiledFunctionExpr(variables, types, expr, compiledJavaClass, runtimeAttributes,
        runtimeOptions);
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
          && runtimeAttributes.equals(compiledFunctionExpr.runtimeAttributes) //
          && runtimeOptions.equals(compiledFunctionExpr.runtimeOptions);
    }
    return false;
  }

  @Override
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
   * Evaluate this compiled function for a single <code>double</code> argument.
   *
   * <p>
   * The head of the argument list is this expression itself, which is what the generated
   * <code>evaluate</code> method expects; it only reads <code>ast.argSize()</code> and
   * <code>ast.get(i)</code>.
   */
  @Override
  public double evalDouble(double arg, EvalEngine engine) {
    IExpr result = evaluate(F.unaryAST1(this, F.num(arg)), engine);
    return result.isPresent() ? result.evalfNaN() : Double.NaN;
  }

  public int getAttributes() {
    return attributes;
  }

  @Override
  public IExpr getExpr() {
    return expr;
  }

  /**
   * The declared type of each argument, in order. Note that this does not record the rank of an
   * argument: a vector or matrix argument is listed by the type of its entries.
   */
  public IAST getTypes() {
    return types;
  }

  public IExpr getRuntimeAttributes() {
    return runtimeAttributes;
  }

  /** The normalized <code>RuntimeOptions</code> this function was compiled with. */
  public RuntimeOptions getRuntimeOptions() {
    return runtimeOptions;
  }

  @Override
  public IAST getVariables() {
    return variables;
  }

  @Override
  public int hashCode() {
    return 461 + expr.hashCode() + 17 * runtimeAttributes.hashCode()
        + 23 * runtimeOptions.hashCode();
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
    if (!runtimeOptions.equals(RuntimeOptions.DEFAULT)) {
      // the settings are printed only when they were asked for: spelling out all six of them on
      // every compiled function would bury the arguments and types they are printed beside
      buf.append(" Options: ").append(runtimeOptions);
    }
    buf.append(")");
    return buf.toString();
  }
}
