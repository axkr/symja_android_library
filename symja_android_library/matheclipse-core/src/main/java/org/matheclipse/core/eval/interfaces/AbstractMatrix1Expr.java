package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(AbstractMatrix1Expr.class);

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
    return arg1.isMatrix(false);
  }

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    FieldMatrix<IExpr> matrix;
    try {
      int[] dim = checkMatrixDimensions(ast.arg1());
      if (dim != null) {
        if (ast.arg1().isNumericArgument(true)) {
          RealMatrix m = ast.arg1().toRealMatrix();
          if (m != null) {
            return realMatrixEval(m, engine, ast);
          }
        }
        matrix = Convert.list2Matrix(ast.arg1());
        if (matrix != null) {
          Predicate<IExpr> zeroChecker = optionZeroTest(ast, engine, S.Automatic, S.Automatic);
          return matrixEval(matrix, zeroChecker, ast);
        }
      }
    } catch (LimitException le) {
      throw le;
    } catch (final MathRuntimeException mre) {
      // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
      LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
    } catch (final RuntimeException e) {
      LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
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
   * @param ast TODO
   * @return <code>F.NIL</code> if the evaluation isn't possible
   */
  public abstract IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker,
      IAST ast);

  public IExpr matrixComplexEval(FieldMatrix<Complex> matrix, IAST ast) {
    return F.NIL;
  }

  @Override
  public IExpr numericEval(final IAST ast, final EvalEngine engine) {
    RealMatrix matrix;
    IExpr arg1 = ast.arg1();
    int[] dim = checkMatrixDimensions(arg1);
    if (dim != null) {
      // int maxValues = -1;
      // if (ast.isAST2()) {
      // maxValues = ast.arg2().toIntDefault(-1);
      // }
      try {
        if (engine.isArbitraryMode()) {
          FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(arg1);
          if (fieldMatrix != null) {
            Predicate<IExpr> zeroChecker = optionZeroTest(ast, engine, S.Automatic, S.Automatic);
            return matrixEval(fieldMatrix, zeroChecker, ast);
          }
          return F.NIL;
        }
        matrix = arg1.toRealMatrix();
        if (matrix != null) {
          return realMatrixEval(matrix, engine, ast);
        } else {
          FieldMatrix<Complex> complexFieldMatrix = Convert.list2ComplexMatrix(ast.arg1());
          if (complexFieldMatrix != null) {
            return matrixComplexEval(complexFieldMatrix, ast);
          }

          FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(arg1);
          if (fieldMatrix != null) {
            Predicate<IExpr> zeroChecker = optionZeroTest(ast, engine, S.Automatic, S.Automatic);
            return matrixEval(fieldMatrix, zeroChecker, ast);
          }
        }
      } catch (LimitException le) {
        throw le;
      } catch (final MathRuntimeException mre) {
        // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
        LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
      } catch (final RuntimeException e) {
        LOGGER.error("AbstractMatrix1Expr.numericEval() failed", e);
      }
    }
    return F.NIL;
  }

  /**
   * Build a zero-checker predicate that honours both a {@code ZeroTest} function and a
   * {@code Tolerance} value as extracted from the option arrays of
   * {@link AbstractFunctionOptionEvaluator} subclasses.
   *
   * @param ast the top-level call (used to detect numeric input)
   * @param engine the evaluation engine
   * @param zeroTestOption {@code options[0]} – the ZeroTest option value
   * @param toleranceOption {@code options[1]} – the Tolerance option value
   * @return a {@link Predicate} suitable for passing to {@code FieldReducedRowEchelonForm},
   *         {@code FieldLUDecomposition}, etc.
   */
  public static Predicate<IExpr> optionZeroTest(final IAST ast, EvalEngine engine,
      IExpr zeroTestOption, IExpr toleranceOption) {
    // Explicit ZeroTest -> always wins
    if (zeroTestOption.isPresent() && !zeroTestOption.equals(S.Automatic)) {
      return Predicates.isTrue(engine, zeroTestOption);
    }
    // Determine effective numeric tolerance
    double tolerance = Config.SPECIAL_FUNCTIONS_TOLERANCE;
    if (toleranceOption.isPresent() && !toleranceOption.equals(S.Automatic)) {
      double t = toleranceOption.toDoubleDefault(Double.NaN);
      if (!Double.isNaN(t) && t > 0.0) {
        tolerance = t;
      }
    }
    // For floating-point matrices use tolerance-based zero check
    if (ast.arg1().isNumericArgument(true)) {
      final double tol = tolerance;
      return x -> {
        try {
          return F.isZero(x.evalfc(), tol);
        } catch (ArgumentTypeException ignored) {
          // fall through to symbolic check
        }
        return x.isZero();
      };
    }
    return POSSIBLE_ZEROQ_TEST;
  }

  /**
   * Evaluate the numeric matrix for this algorithm.
   *
   * @param matrix the matrix which contains numeric values
   * @param engine TODO
   * @param ast TODO
   * @return <code>F.NIL</code> if the evaluation isn't possible
   */
  public abstract IExpr realMatrixEval(RealMatrix matrix, EvalEngine engine, IAST ast);

}
