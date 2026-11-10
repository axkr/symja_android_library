package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ParametricFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>ParametricNDSolveValue(eqns, expr, {t, t0, t1}, pars)</code> gives a function of the
 * parameters <code>pars</code> which solves the differential equations <code>eqns</code> numerically
 * for given parameter values and returns <code>expr</code> for the solution, as
 * {@link NDSolveValue} does.
 *
 * <p>
 * The parameters are given as <code>{p1, p2, ...}</code>; each one may come with a range
 * <code>{p, pmin, pmax}</code>. Applied to numeric values <code>pf(v1, v2, ...)</code> the result is
 * what <code>NDSolveValue</code> gives with <code>v1, v2, ...</code> in place of the parameters;
 * other values leave the application unevaluated. Trailing options are passed on to
 * <code>NDSolveValue</code>.
 */
public class ParametricNDSolveValue extends AbstractFunctionEvaluator {

  private static final int[] ARGS_4_INFINITY = new int[] {4, Integer.MAX_VALUE};

  public ParametricNDSolveValue() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr head = ast.head();
    if (head instanceof ParametricFunctionExpr) {
      try {
        return ((ParametricFunctionExpr) head).evaluate(ast, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.ParametricNDSolveValue, rex, engine);
      }
    }
    if (head != S.ParametricNDSolveValue || ast.argSize() < 4) {
      return F.NIL;
    }
    IExpr range = ast.arg3();
    if (!range.isList() || range.argSize() < 2 || !range.first().isSymbol()) {
      return F.NIL;
    }
    IAST parameters = parameterSymbols(ast.arg4(), engine);
    if (parameters.isNIL()) {
      return F.NIL;
    }
    for (int i = 5; i < ast.size(); i++) {
      if (!ast.get(i).isRuleAST()) {
        return F.NIL;
      }
    }
    return ParametricFunctionExpr.newInstance(ast, parameters);
  }

  /**
   * The parameter symbols of <code>{p1, {p2, pmin, pmax}, ...}</code>.
   *
   * @return {@link F#NIL} if the specification isn't a list of symbols, each of them
   *         possibly with a range; a parameter which is no unprotected symbol is reported
   */
  private static IAST parameterSymbols(IExpr spec, EvalEngine engine) {
    if (!spec.isList() || spec.argSize() == 0) {
      return F.NIL;
    }
    IAST list = (IAST) spec;
    IASTAppendable symbols = F.ListAlloc(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      IExpr parameter = list.get(i);
      if (parameter.isList() && parameter.argSize() == 3) {
        parameter = parameter.first();
      }
      if (!parameter.isSymbol() || ((ISymbol) parameter).hasProtectedAttribute()) {
        // `1` cannot be used as a parameter.
        return Errors.printMessage(S.ParametricNDSolveValue, "dspar", F.list(parameter), engine);
      }
      // a repeated parameter is allowed; it takes one value per position
      symbols.append(parameter);
    }
    return symbols;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // an application pf(v1, v2, ...) of a parametric function takes any number of values
    return ast.head() == S.ParametricNDSolveValue ? ARGS_4_INFINITY : null;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    ParametricFunctionExpr.registerHead();
  }
}
