package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Solve.SolveData;

/**
 *
 *
 * <pre>
 * SolveValues(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code> and returns the
 * values of the variables instead of a list of rules.
 *
 * </blockquote>
 *
 * <pre>
 * SolveValues(equations, vars, domain)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code> in the given
 * <code>domain</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; SolveValues(x^2==1, x)
 * {-1,1}
 *
 * &gt;&gt; SolveValues({x+y==2, x-y==0}, {x,y})
 * {{1,1}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="NSolveValues.md">NSolveValues</a>, <a href="Solve.md">Solve</a>
 */
public class SolveValues extends AbstractFunctionOptionEvaluator {
  public SolveValues() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    boolean isNumericArgument = !ast.arg1().isFree(x -> x.isInexactNumber(), false);
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    // the shape of the result is determined by the variables argument the user wrote down, before
    // Solve turns a single variable into a list of variables
    IExpr variables = ast.arg2();
    SolveData sd = new SolveData(SolveOptions.of(SolveOptions.SOLVE_KEYS, options));
    return Solve.solutionValues(sd.of(ast, isNumericArgument, engine), variables);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.SOLVE_KEYS, SolveOptions.SOLVE_DEFAULTS);
  }
}
