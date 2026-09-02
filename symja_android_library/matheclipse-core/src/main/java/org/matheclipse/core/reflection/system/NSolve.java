package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Solve.SolveData;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions) numerically. */
public class NSolve extends AbstractFunctionOptionEvaluator {
  public NSolve() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    SolveOptions solveOptions = SolveOptions.of(SolveOptions.NSOLVE_KEYS, options);
    long precision = Solve.workingPrecision(ast, solveOptions.workingPrecision(), engine);
    if (precision == Solve.INVALID_PRECISION) {
      return F.NIL;
    }
    if (ast.size() == 5) {
      // the working precision was given as the fourth argument
      ast = ast.copyUntil(4);
    }
    return solveNumeric(solveOptions, ast, precision, engine);
  }

  /**
   * Solve the equations numerically, if requested with a working precision beyond machine
   * precision.
   *
   * <p>
   * A machine number carries about 16 significant digits, so a higher working precision cannot be
   * served by the numerical polynomial solver: the equations are solved exactly and the exact
   * solutions - <code>Sqrt(2)</code>, or an inert <code>Root</code> object for a polynomial which
   * has no closed radical form - are then evaluated to the requested number of digits. Equations
   * which have no exact solution fall back to the machine precision solution.
   *
   * @param options the option values of the <code>NSolve(...)</code> /
   *        <code>NSolveValues(...)</code> call
   * @param ast the <code>NSolve(...)</code> / <code>NSolveValues(...)</code> ast without the
   *        working precision argument
   * @param precision the number of significant digits or {@link Solve#MACHINE_PRECISION_REQUESTED}
   * @param engine the evaluation engine
   * @return the list of solution lists
   */
  static IExpr solveNumeric(SolveOptions options, IAST ast, long precision, EvalEngine engine) {
    if (precision == Solve.MACHINE_PRECISION_REQUESTED) {
      return new SolveData(options).of(ast, true, true, engine);
    }
    IExpr result = new SolveData(options).of(ast, false, true, engine);
    if (result.isNIL()) {
      result = new SolveData(options).of(ast, true, true, engine);
    }
    if (result.isNIL()) {
      return result;
    }
    return Solve.sortNumericSolutions(engine.evaluate(F.N(result, precision)));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.NSOLVE_KEYS, SolveOptions.NSOLVE_DEFAULTS);
  }
}
