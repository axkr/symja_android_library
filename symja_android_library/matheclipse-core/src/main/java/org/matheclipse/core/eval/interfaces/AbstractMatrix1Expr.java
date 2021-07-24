package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;

import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {
  public final static PossibleZeroQTest POSSIBLE_ZEROQ_TEST = new PossibleZeroQTest();
  
  public static class PossibleZeroQTest implements Predicate<IExpr> {
    @Override
    public boolean test(IExpr x) {
      return x.isPossibleZero(false);
    }
  }

  public AbstractMatrix1Expr() {}

  /**
   * Check if <code>arg1</code> is a matrix.
   *
   * @param arg1
   * @return
   */
  public int[] checkMatrixDimensions(IExpr arg1) {
    return arg1.isMatrix();
  }

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    FieldMatrix<IExpr> matrix;
    try {
      int[] dim = checkMatrixDimensions(ast.arg1());
      if (dim != null) {
        matrix = Convert.list2Matrix(ast.arg1());
        if (matrix != null) {
          Predicate<IExpr> zeroChecker = optionZeroTest(ast, 2, engine);
          return matrixEval(matrix, zeroChecker);
        }
      }
    } catch (LimitException le) {
      throw le;
    } catch (final MathRuntimeException mre) {
      // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
      return engine.printMessage(ast.topHead(), mre);
    } catch (final RuntimeException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return engine.printMessage(ast.topHead() + ": " + e.getMessage());
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /**
   * Evaluate the symbolic matrix for this algorithm.
   *
   * @param matrix the matrix which contains symbolic values
   * @param zeroChecker test if a calculation is <code>0</code>.
   * @return <code>F.NIL</code> if the evaluation isn't possible
   */
  public abstract IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker);

  @Override
  public IExpr numericEval(final IAST ast, final EvalEngine engine) {
    RealMatrix matrix;
    IExpr arg1 = ast.arg1();
    int[] dim = checkMatrixDimensions(arg1);
    if (dim != null) {
      try {
        if (engine.isArbitraryMode()) {
          FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(arg1);
          if (fieldMatrix != null) {
            Predicate<IExpr> zeroChecker = optionZeroTest(ast, 2, engine);
            return matrixEval(fieldMatrix, zeroChecker);
          }
          return F.NIL;
        }
        matrix = arg1.toRealMatrix();
        if (matrix != null) {
          return realMatrixEval(matrix);
        } else {
          FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(arg1);
          if (fieldMatrix != null) {
            Predicate<IExpr> zeroChecker = optionZeroTest(ast, 2, engine);
            return matrixEval(fieldMatrix, zeroChecker);
          }
        }
      } catch (LimitException le) {
        throw le;
      } catch (final MathRuntimeException mre) {
        // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
        return engine.printMessage(ast.topHead(), mre);
      } catch (final RuntimeException e) {
        if (Config.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  public static Predicate<IExpr> optionZeroTest(final IAST ast, int start, EvalEngine engine) {
    Predicate<IExpr> zeroChecker = POSSIBLE_ZEROQ_TEST;
    if (ast.size() > 1) {
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, start, ast.size(), engine);
      IExpr zeroTest = options.getOption(S.ZeroTest);
      if (zeroTest.isPresent()) {
        if (!zeroTest.equals(S.Automatic)) {
          zeroChecker = Predicates.isTrue(engine, zeroTest);
        }
      }
    }
    return zeroChecker;
  }

  /**
   * Evaluate the numeric matrix for this algorithm.
   *
   * @param matrix the matrix which contains numeric values
   * @return <code>F.NIL</code> if the evaluation isn't possible
   */
  public abstract IExpr realMatrixEval(RealMatrix matrix);
}
