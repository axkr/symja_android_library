package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * FindInstance(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to find one instance which solves the <code>equations</code> for the variables <code>
 * vars</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; FindInstance({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;-2,y-&gt;-2*Sqrt(2)}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Solve.md">Solve</a>
 */
public class FindInstance extends Solve {

  public FindInstance() {
    // empty constructor
  }

  /**
   * Try to find at least one solution for a set of equations (i.e. <code>Equal[...]</code>
   * expressions).
   */
  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IAST vars = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
    if (vars.isNIL()) {
      return F.NIL;
    }
    try {
      boolean formula = false;
      int maxChoices = 1;
      if (argSize > 0 && argSize < ast.argSize()) {
        ast = ast.copyUntil(argSize + 1);
      }
      if (argSize >= 4) {
        maxChoices = ast.arg4().toIntDefault();
        if (maxChoices < 0) {
          maxChoices = 1;
        }
      } else if (argSize >= 3) {
        maxChoices = ast.arg3().toIntDefault();
        if (maxChoices < 0) {
          maxChoices = 1;
        }
      }

      try {
        if (ast.arg1().isBooleanFormula()) {
          formula = ast.arg1().isBooleanFormula();
          if (ast.isAST2()) {
            return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }

      ISymbol domain = S.Complexes;
      if (argSize >= 3) {
        if (!ast.arg3().isSymbol()) {
          // Warning: `1` is not a valid domain specification.
          Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
        } else {
          if (ast.arg3() == S.Booleans || formula) {
            return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
          } else if (ast.arg3() == S.Integers) {
            return Solve.solveIntegers(ast, vars, vars, maxChoices, engine);
          }
          if (domain != S.Reals && domain != S.Complexes) {
            // Warning: `1` is not a valid domain specification.
            Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
          }
        }
      }
      IASTMutable termsEqualZeroList = Validate.checkEquations(ast, 1);
      SolveData solveData = new Solve.SolveData(options);
      return solveData.solveEquations(termsEqualZeroList, F.List(), vars, maxChoices, false,
          engine);
    } catch (final ValidateException ve) {
      return Errors.printMessage(ast.topHead(), ve, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.FindInstance, rex, engine);
    }
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
    setOptions(newSymbol, S.GenerateConditions, S.False);
  }
}
