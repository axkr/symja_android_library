package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;


/**
 *
 *
 * <pre>
 * NSolveValues(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code> numerically and
 * returns the values of the variables instead of a list of rules.
 *
 * </blockquote>
 *
 * <pre>
 * NSolveValues(equations, vars, domain)
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
 * &gt;&gt; NSolveValues(x^2==2, x)
 * {-1.4142135623730951,1.4142135623730951}
 *
 * &gt;&gt; NSolveValues({x+y==3, x-y==1}, {x,y})
 * {{2.0,1.0}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="NSolve.md">NSolve</a>, <a href="SolveValues.md">SolveValues</a>
 */
public class NSolveValues extends AbstractFunctionOptionEvaluator {
  public NSolveValues() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    long precision = Solve.workingPrecision(ast, options[2], engine);
    if (precision == Solve.INVALID_PRECISION) {
      return F.NIL;
    }
    if (ast.size() == 5) {
      // the working precision was given as the fourth argument
      ast = ast.copyUntil(4);
    }
    // the shape of the result is determined by the variables argument the user wrote down, before
    // Solve turns a single variable into a list of variables
    IExpr variables = ast.arg2();
    return Solve.solutionValues(NSolve.solveNumeric(options, ast, precision, engine), variables);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IBuiltInSymbol[] optionKeys =
        new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots, S.WorkingPrecision};
    IExpr[] optionValues = new IExpr[] {S.False, F.C1000, S.Automatic};
    setOptions(newSymbol, optionKeys, optionValues);
  }
}
