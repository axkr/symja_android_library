package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>RecurrenceTable(eqns, a(n), {n, nmin, nmax})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * generates a list of values of <code>a(n)</code> for successive <code>n</code> based on solving
 * the recurrence equations <code>eqns</code>.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; RecurrenceTable({a(n+1)==3*a(n), a(1)==7}, a(n), {n, 1, 5})
 * {7,21,63,189,567}
 * </code>
 * </pre>
 */
public class RecurrenceTable extends AbstractFunctionEvaluator {

  /** The maximum number of computed values */
  private static final int MAX_VALUES = 100000;

  public RecurrenceTable() {}

  /**
   * Collect the minimum and maximum shift <code>k</code> of the terms <code>a(n+k)</code>.
   *
   * @return <code>false</code> if the expression contains an unsupported term <code>a(...)</code>
   */
  private static boolean shifts(IExpr expr, IExpr head, IExpr n, int[] minMax, EvalEngine engine) {
    if (!expr.isAST()) {
      return true;
    }
    IAST ast = (IAST) expr;
    if (ast.head().equals(head)) {
      if (!ast.isAST1()) {
        return false;
      }
      int k = engine.evaluate(F.Subtract(ast.arg1(), n)).toIntDefault();
      if (k == Integer.MIN_VALUE) {
        return false;
      }
      minMax[0] = Math.min(minMax[0], k);
      minMax[1] = Math.max(minMax[1], k);
      minMax[2]++;
      return true;
    }
    for (int i = 0; i < ast.size(); i++) {
      if (!shifts(ast.get(i), head, n, minMax, engine)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = engine.evaluate(ast.arg1());
    IAST equations;
    if (arg1.isList() || arg1.isAnd()) {
      equations = (IAST) arg1;
    } else {
      equations = F.List(arg1);
    }
    IExpr function = ast.arg2();
    if (!function.isAST1() || !function.head().isSymbol() || !ast.arg3().isList()) {
      return F.NIL;
    }
    final IExpr head = function.head();
    final IExpr n = function.first();
    IAST iterator = (IAST) ast.arg3();
    if (iterator.argSize() < 2 || iterator.argSize() > 3 || !iterator.arg1().equals(n)
        || !n.isSymbol()) {
      return F.NIL;
    }
    final int nMin = iterator.isAST2() ? 1 : engine.evaluate(iterator.arg2()).toIntDefault();
    final int nMax = engine.evaluate(iterator.last()).toIntDefault();
    if (nMin == Integer.MIN_VALUE || nMax == Integer.MIN_VALUE) {
      return F.NIL;
    }
    if (nMax < nMin) {
      return F.CEmptyList;
    }

    Map<Integer, IExpr> values = new HashMap<Integer, IExpr>();
    IExpr recurrence = F.NIL;
    int[] minMax = new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
    for (int i = 1; i < equations.size(); i++) {
      IExpr equation = equations.get(i);
      if (!equation.isEqual() || !equation.isAST2()) {
        return F.NIL;
      }
      if (equation.isFree(n)) {
        // initial condition a(i)==value
        IExpr lhs = equation.first();
        IExpr rhs = equation.second();
        if (!lhs.isAST(head, 2)) {
          IExpr t = lhs;
          lhs = rhs;
          rhs = t;
        }
        int index = lhs.isAST(head, 2) ? lhs.first().toIntDefault() : Integer.MIN_VALUE;
        if (index == Integer.MIN_VALUE || !rhs.isFree(head)) {
          return F.NIL;
        }
        values.put(index, rhs);
      } else {
        if (recurrence.isPresent()) {
          // only one recurrence equation is supported
          return F.NIL;
        }
        if (!shifts(equation, head, n, minMax, engine) || minMax[2] == 0) {
          return F.NIL;
        }
        recurrence = equation;
      }
    }
    if (recurrence.isNIL()) {
      return F.NIL;
    }
    final int order = minMax[1] - minMax[0];
    int start = Integer.MAX_VALUE;
    for (Integer index : values.keySet()) {
      start = Math.min(start, index);
    }
    if (order > 0) {
      for (int i = 0; i < order; i++) {
        if (start == Integer.MAX_VALUE || !values.containsKey(start + i)) {
          // not enough initial conditions
          return F.NIL;
        }
      }
    } else {
      start = nMin;
    }
    if (nMin < start || (long) nMax - start > MAX_VALUES) {
      return F.NIL;
    }

    try {
      final ISymbol unknown = F.Dummy();
      for (int index = start + order; index <= nMax; index++) {
        if (values.containsKey(index)) {
          continue;
        }
        final int nValue = index - minMax[1];
        final int current = index;
        IExpr equation = F.subst(recurrence, x -> {
          if (x.isAST(head, 2)) {
            int j = engine.evaluate(F.subst(x.first(), n, F.ZZ(nValue))).toIntDefault();
            if (j == current) {
              return unknown;
            }
            IExpr value = values.get(j);
            return value != null ? value : F.NIL;
          }
          return F.NIL;
        });
        equation = engine.evaluate(F.subst(equation, n, F.ZZ(nValue)));
        if (!equation.isFree(head)) {
          return F.NIL;
        }
        IExpr solution = engine.evaluate(F.Solve(equation, unknown));
        if (!solution.isListOfLists() || solution.argSize() < 1
            || !solution.first().first().isRuleAST()) {
          return F.NIL;
        }
        values.put(index, solution.first().first().second());
      }
      IASTAppendable result = F.ListAlloc(nMax - nMin + 1);
      for (int index = nMin; index <= nMax; index++) {
        IExpr value = values.get(index);
        if (value == null) {
          return F.NIL;
        }
        result.append(value);
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
