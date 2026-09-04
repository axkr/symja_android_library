package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * Wronskian({y1, y2, ...}, x)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Wronskian determinant of the functions <code>y1, y2, ...</code> of <code>x</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Wronskian(equation, y, x)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Wronskian determinant of a basis of the solutions of the linear differential
 * <code>equation</code> with dependent variable <code>y</code> and independent variable
 * <code>x</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Wronskian(equations, {y1, y2, ...}, x)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Wronskian determinant of a system of linear differential <code>equations</code> of
 * first order.
 *
 * </blockquote>
 *
 * <p>
 * The Wronskian of <code>m</code> functions is the determinant of the matrix whose rows are the
 * functions and their derivatives up to order <code>m-1</code>. It vanishes everywhere when the
 * functions are linearly dependent, so it is the usual test for a set of solutions of a linear
 * differential equation being a basis of them.
 *
 * <p>
 * For a differential equation the solutions do not have to be known. Abel's identity gives the
 * Wronskian of a basis directly from the coefficient of the second highest derivative, which is
 * why <code>Wronskian</code> answers for equations no solver can solve. That Wronskian is
 * determined only up to a constant factor, and the one returned here is normalized so that no
 * arbitrary constant appears in it.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Wronskian({Exp(x), Exp(2*x)}, x)
 * E^(3*x)
 *
 * &gt;&gt; Wronskian(y''(x) - x*y(x) == 0, y, x)
 * 1
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Det.md">Det</a>, <a href="DSolve.md">DSolve</a>
 */
public class Wronskian extends AbstractFunctionEvaluator {

  /** The largest system or equation order this is built for. */
  private static final int MAX_ORDER = 16;

  /** How long the reduction of a determinant may take. */
  private static final int SIMPLIFY_SECONDS = 5;

  public Wronskian() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      if (ast.isAST2()) {
        return wronskianOfFunctions(ast.arg1(), ast.arg2(), engine);
      }
      IExpr variable = ast.arg3();
      if (!variable.isSymbol()) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.Wronskian, "ivar", F.list(variable), engine);
      }
      if (ast.arg2().isList()) {
        return wronskianOfSystem(ast.arg1(), (IAST) ast.arg2(), variable, engine);
      }
      return wronskianOfEquation(ast.arg1(), ast.arg2(), variable, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Wronskian, rex);
    }
  }

  /**
   * The determinant of the matrix whose row <code>i</code> holds the function <code>i</code> and
   * its derivatives up to order <code>m-1</code>.
   */
  private static IExpr wronskianOfFunctions(IExpr functions, IExpr variable, EvalEngine engine) {
    if (!functions.isList()) {
      return F.NIL;
    }
    if (!variable.isSymbol()) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.Wronskian, "ivar", F.list(variable), engine);
    }
    IAST list = (IAST) functions;
    int m = list.argSize();
    if (m == 0) {
      return F.C1;
    }
    if (m > MAX_ORDER) {
      return F.NIL;
    }
    IASTAppendable matrix = F.ListAlloc(m);
    for (int i = 1; i <= m; i++) {
      IASTAppendable row = F.ListAlloc(m);
      IExpr derivative = list.get(i);
      row.append(derivative);
      for (int order = 1; order < m; order++) {
        derivative = engine.evaluate(F.D(derivative, variable));
        row.append(derivative);
      }
      matrix.append(row);
    }
    return normalizeDeterminant(S.Det.of(engine, matrix), engine);
  }

  /**
   * Reduces the determinant, and keeps the reduction only when it is not more complicated.
   *
   * <p>
   * This is what makes the answer for linearly dependent functions the plain <code>0</code> the
   * caller is looking for: the determinant of <code>{2^n, 2^(n+1)}</code> comes out of
   * {@link org.matheclipse.core.expression.S#Det} as a difference of two powers which are written
   * differently but are the same. Putting the terms over a common denominator does the same for a
   * determinant of rational functions, whose numerator cancels.
   *
   * <p>
   * Shared with {@link Casoratian}, which faces the same question for shifts instead of
   * derivatives.
   */
  static IExpr normalizeDeterminant(IExpr determinant, EvalEngine engine) {
    if (determinant.isNIL() || determinant.isNumber()) {
      return determinant;
    }
    IExpr best = determinant;
    IExpr together = S.Together.of(engine, determinant);
    if (together.isPresent() && together.leafCount() <= best.leafCount()) {
      best = together;
    }
    IExpr simplified;
    try {
      simplified = engine.evaluate(
          F.TimeConstrained(F.Simplify(best), F.ZZ(SIMPLIFY_SECONDS), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return best;
    }
    if (simplified.isPresent() && !simplified.equals(S.$Aborted)
        && simplified.leafCount() <= best.leafCount()) {
      return simplified;
    }
    return best;
  }

  /**
   * The Wronskian of a basis of the solutions of a linear differential equation, by Abel's
   * identity: for <code>a[n]*y^(n) + a[n-1]*y^(n-1) + ... == 0</code> it is
   * <code>E^(-Integrate(a[n-1]/a[n], x))</code>.
   *
   * <p>
   * Only the two highest coefficients are looked at, so the equation neither has to be solvable nor
   * to have coefficients any solver understands.
   */
  private static IExpr wronskianOfEquation(IExpr equation, IExpr dependent, IExpr variable,
      EvalEngine engine) {
    IExpr head = dependent.isAST1() ? dependent.head() : dependent;
    if (!head.isSymbol()) {
      return F.NIL;
    }
    IExpr residual = equation;
    if (equation.isEqual()) {
      residual = S.Subtract.of(engine, equation.first(), equation.second());
    }
    residual = engine.evaluate(F.ExpandAll(residual));

    IExpr applied = F.unaryAST1(head, variable);
    int n = LinearODEForm.highestDerivativeOrder(residual, head, variable);
    if (n < 1 || n > MAX_ORDER) {
      return F.NIL;
    }
    LinearODEForm form = LinearODEForm.extract(residual, applied, variable, engine);
    if (form == null || form.order != n || form.a[n].isZero()) {
      // Not linear in the dependent variable, so it has no basis of solutions.
      return F.NIL;
    }
    IExpr ratio = engine.evaluate(F.Divide(form.a[n - 1], form.a[n]));
    if (ratio.isZero()) {
      return F.C1;
    }
    IExpr integral = engine.evaluate(F.Integrate(ratio, variable));
    if (!integral.isFree(S.Integrate)) {
      return F.NIL;
    }
    return engine.evaluate(F.Exp(F.Negate(integral)));
  }

  /**
   * The Wronskian of a system of linear differential equations of first order, by Liouville's
   * formula <code>E^Integrate(Tr(A), x)</code> for the system <code>Y' == A.Y</code>.
   */
  private static IExpr wronskianOfSystem(IExpr equations, IAST dependents, IExpr variable,
      EvalEngine engine) {
    if (!equations.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) equations;
    int n = dependents.argSize();
    if (n == 0 || n > MAX_ORDER || list.argSize() != n) {
      return F.NIL;
    }
    IASTAppendable applied = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr dependent = dependents.get(i);
      IExpr head = dependent.isAST1() ? dependent.head() : dependent;
      if (!head.isSymbol()) {
        return F.NIL;
      }
      applied.append(F.unaryAST1(head, variable));
    }
    IASTAppendable residuals = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr equation = list.get(i);
      residuals.append(equation.isEqual() //
          ? S.Subtract.of(engine, equation.first(), equation.second())
          : equation);
    }
    IExpr[] system = LinearODEForm.extractSystem(residuals, applied, variable, engine);
    if (system == null) {
      return F.NIL;
    }
    // The system is read as M.Y' + N.Y == b, so Y' == A.Y with A == -Inverse(M).N, and only the
    // homogeneous part matters for the Wronskian.
    IExpr inverse = engine.evaluate(F.Inverse(system[0]));
    if (!inverse.isList()) {
      return F.NIL;
    }
    IExpr matrixA = engine.evaluate(F.Dot(F.Times(F.CN1, inverse), system[1]));
    IExpr trace = engine.evaluate(F.Tr(matrixA));
    if (trace.isZero()) {
      return F.C1;
    }
    IExpr integral = engine.evaluate(F.Integrate(trace, variable));
    if (!integral.isFree(S.Integrate)) {
      return F.NIL;
    }
    return engine.evaluate(F.Exp(integral));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }
}
