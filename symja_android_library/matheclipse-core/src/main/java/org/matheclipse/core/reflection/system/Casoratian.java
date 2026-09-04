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
 * Casoratian({y1, y2, ...}, n)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Casoratian determinant of the sequences <code>y1, y2, ...</code> of <code>n</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Casoratian(equation, y, n)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Casoratian determinant of a basis of the solutions of the linear difference
 * <code>equation</code> with dependent variable <code>y</code> and independent variable
 * <code>n</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Casoratian(equations, {y1, y2, ...}, n)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Casoratian determinant of a system of linear difference <code>equations</code> of
 * first order.
 *
 * </blockquote>
 *
 * <p>
 * The Casoratian is to a difference equation what the {@link Wronskian} is to a differential one.
 * For <code>m</code> sequences it is the determinant of the matrix whose rows are the sequences
 * shifted by <code>0</code> to <code>m-1</code>, and it vanishes identically when they are linearly
 * dependent.
 *
 * <p>
 * For a difference equation the solutions do not have to be known. The Casoratian of a basis
 * satisfies the first order recurrence <code>C(n+1) == (-1)^m*a[0](n)/a[m](n)*C(n)</code>, so it
 * follows from the lowest and highest coefficient alone. The result is normalized to
 * <code>C(0) == 1</code>, so that no arbitrary constant appears in it.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Casoratian({2^n, n*2^n}, n)
 * 2^(1+2*n)
 *
 * &gt;&gt; Casoratian(y(n+2) - 3*y(n+1) + 2*y(n) == 0, y, n)
 * 2^n
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Det.md">Det</a>, <a href="RSolve.md">RSolve</a>, <a href="Wronskian.md">Wronskian</a>
 */
public class Casoratian extends AbstractFunctionEvaluator {

  /** The largest system or equation order this is built for. */
  private static final int MAX_ORDER = 16;

  public Casoratian() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      if (ast.isAST2()) {
        return casoratianOfSequences(ast.arg1(), ast.arg2(), engine);
      }
      IExpr variable = ast.arg3();
      if (!variable.isSymbol()) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.Casoratian, "ivar", F.list(variable), engine);
      }
      if (ast.arg2().isList()) {
        return casoratianOfSystem(ast.arg1(), (IAST) ast.arg2(), variable, engine);
      }
      return casoratianOfEquation(ast.arg1(), ast.arg2(), variable, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Casoratian, rex);
    }
  }

  /**
   * The determinant of the matrix whose row <code>i</code> holds the sequence <code>i</code>
   * shifted by <code>0</code> to <code>m-1</code>.
   */
  private static IExpr casoratianOfSequences(IExpr sequences, IExpr variable, EvalEngine engine) {
    if (!sequences.isList()) {
      return F.NIL;
    }
    if (!variable.isSymbol()) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.Casoratian, "ivar", F.list(variable), engine);
    }
    IAST list = (IAST) sequences;
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
      for (int shift = 0; shift < m; shift++) {
        row.append(shift(list.get(i), variable, shift, engine));
      }
      matrix.append(row);
    }
    return Wronskian.normalizeDeterminant(S.Det.of(engine, matrix), engine);
  }

  /** The sequence with its argument moved on by <code>shift</code>. */
  private static IExpr shift(IExpr sequence, IExpr variable, int shift, EvalEngine engine) {
    if (shift == 0) {
      return engine.evaluate(sequence);
    }
    return engine.evaluate(F.subst(sequence, variable, F.Plus(variable, F.ZZ(shift))));
  }

  /**
   * The Casoratian of a basis of the solutions of a linear difference equation. It satisfies
   * <code>C(n+1) == (-1)^m*a[0](n)/a[m](n)*C(n)</code>, so only the lowest and the highest
   * coefficient are needed and the solutions never have to be found.
   */
  private static IExpr casoratianOfEquation(IExpr equation, IExpr dependent, IExpr variable,
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

    int m = highestShift(residual, head, variable, engine);
    if (m < 1 || m > MAX_ORDER) {
      return F.NIL;
    }
    IExpr[] coefficients = new IExpr[m + 1];
    IExpr rest = residual;
    for (int j = m; j >= 0; j--) {
      IExpr term = F.unaryAST1(head, engine.evaluate(F.Plus(variable, F.ZZ(j))));
      IExpr coefficient = engine.evaluate(F.Coefficient(rest, term));
      coefficients[j] = coefficient;
      rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(coefficient, term))));
    }
    // Everything mentioning the unknown has to have been read into a coefficient, or the equation
    // is not linear and has no basis of solutions.
    if (!rest.isZero() || !rest.isFree(head, true)) {
      return F.NIL;
    }
    for (int j = 0; j <= m; j++) {
      if (!coefficients[j].isFree(head, true)) {
        return F.NIL;
      }
    }
    if (coefficients[m].isZero() || coefficients[0].isZero()) {
      return F.NIL;
    }
    IExpr ratio = engine.evaluate(
        F.Times(F.Power(F.CN1, F.ZZ(m)), F.Divide(coefficients[0], coefficients[m])));
    return accumulate(ratio, variable, engine);
  }

  /**
   * The Casoratian of a system of linear difference equations of first order. For
   * <code>Y(n+1) == A(n).Y(n)</code> it accumulates the determinant of the coefficient matrix.
   */
  private static IExpr casoratianOfSystem(IExpr equations, IAST dependents, IExpr variable,
      EvalEngine engine) {
    if (!equations.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) equations;
    int n = dependents.argSize();
    if (n == 0 || n > MAX_ORDER || list.argSize() != n) {
      return F.NIL;
    }
    IExpr[] heads = new IExpr[n];
    for (int i = 0; i < n; i++) {
      IExpr dependent = dependents.get(i + 1);
      IExpr head = dependent.isAST1() ? dependent.head() : dependent;
      if (!head.isSymbol()) {
        return F.NIL;
      }
      heads[i] = head;
    }
    IExpr next = engine.evaluate(F.Plus(variable, F.C1));
    IASTAppendable matrix = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      // The equation which advances the i-th unknown.
      IExpr shifted = F.unaryAST1(heads[i], next);
      IExpr equation = F.NIL;
      for (int e = 1; e <= list.argSize(); e++) {
        IExpr candidate = list.get(e);
        if (!candidate.isFree(shifted, true)) {
          if (equation.isPresent()) {
            return F.NIL;
          }
          equation = candidate;
        }
      }
      if (equation.isNIL()) {
        return F.NIL;
      }
      IExpr residual = equation.isEqual() //
          ? S.Subtract.of(engine, equation.first(), equation.second())
          : equation;
      residual = engine.evaluate(F.ExpandAll(residual));
      IExpr leading = engine.evaluate(F.Coefficient(residual, shifted));
      if (leading.isZero() || !leading.isFree(shifted, true)) {
        return F.NIL;
      }
      residual = engine.evaluate(F.ExpandAll(F.Subtract(residual, F.Times(leading, shifted))));
      IASTAppendable row = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        IExpr term = F.unaryAST1(heads[j], variable);
        IExpr coefficient = engine.evaluate(F.Coefficient(residual, term));
        row.append(engine.evaluate(F.Divide(F.Negate(coefficient), leading)));
        residual = engine.evaluate(F.ExpandAll(F.Subtract(residual, F.Times(coefficient, term))));
      }
      for (int j = 0; j < n; j++) {
        if (!residual.isFree(heads[j], true)) {
          // Not linear, or it refers to a shift this reading does not account for.
          return F.NIL;
        }
      }
      matrix.append(row);
    }
    return accumulate(S.Det.of(engine, matrix), variable, engine);
  }

  /**
   * The solution of <code>C(n+1) == ratio(n)*C(n)</code> with <code>C(0) == 1</code>, which is the
   * product of the ratio over the steps taken so far.
   */
  private static IExpr accumulate(IExpr ratio, IExpr variable, EvalEngine engine) {
    if (ratio.isZero()) {
      return F.NIL;
    }
    if (ratio.isFree(variable)) {
      return engine.evaluate(F.Power(ratio, variable));
    }
    IExpr index = F.Dummy("k");
    IExpr product = engine.evaluate(F.Product(F.subst(ratio, variable, index),
        F.List(index, F.C0, F.Subtract(variable, F.C1))));
    return product.isFree(S.Product) && product.isFree(index, true) ? product : F.NIL;
  }

  /** The highest shift in which the unknown occurs, or <code>-1</code> if it does not occur. */
  private static int highestShift(IExpr expr, IExpr head, IExpr variable, EvalEngine engine) {
    if (!expr.isAST()) {
      return -1;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST1() && ast.head().equals(head)) {
      int shift = engine.evaluate(F.Subtract(ast.arg1(), variable)).toIntDefault();
      return F.isPresent(shift) && shift >= 0 ? shift : -1;
    }
    int max = -1;
    for (int i = 0; i < ast.size(); i++) {
      int found = highestShift(ast.get(i), head, variable, engine);
      if (found > max) {
        max = found;
      }
    }
    return max;
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
