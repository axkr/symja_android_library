package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Abstract base class for functions that take a single matrix argument and return a matrix result
 * (e.g. {@code Inverse}, {@code PseudoInverse}).
 *
 * <p>
 * Extends {@link AbstractFunctionOptionEvaluator} so that subclasses can declare {@code ZeroTest}
 * and {@code Tolerance} options via {@link #setUp} and have them automatically parsed into the
 * {@code options[]} array before {@link #evaluate(IAST,int,IExpr[],EvalEngine,IAST)} is called.
 */
public abstract class AbstractMatrix1Matrix extends AbstractFunctionOptionEvaluator {

  public AbstractMatrix1Matrix() {}

  /**
   * Symbolic evaluation entry-point (options already parsed by the parent class).
   *
   * <p>
   * Subclasses that want to redirect to {@link #numericEval} (e.g. {@code PseudoInverse}) should
   * override this method.
   */
  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, final IAST originalAST) {
    boolean togetherMode = engine.isTogetherMode();
    try {
      engine.setTogetherMode(true);
      int[] dims = checkMatrixDimensions(ast.arg1());
      if (dims != null) {
        FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1());
        if (matrix != null) {
          Predicate<IExpr> zeroChecker = buildZeroChecker(ast, options, engine);
          matrix = matrixEval(matrix, zeroChecker);
          if (matrix != null) {
            return Convert.matrix2List(matrix);
          }
          return F.NIL;
        }
      }
    } catch (LimitException le) {
      throw le;
    } catch (MathRuntimeException mre) {
      Errors.printMessage(ast.topHead(), mre);
    } catch (final RuntimeException rex) {
      Errors.printMessage(ast.topHead(), rex);
    } finally {
      engine.setTogetherMode(togetherMode);
    }
    return F.NIL;
  }

  /**
   * Numeric evaluation entry-point (called by the engine when in numeric mode). Options are
   * re-parsed here because {@code numericEval} is invoked by the engine independently of the
   * {@code evaluate} path.
   */
  @Override
  public IExpr numericEval(final IAST ast, final EvalEngine engine) {
    // Re-parse options so they are available on the numeric path too
    IExpr[] options;
    if (optionSymbols == null) {
      options = new IExpr[0];
    } else {
      options = new IExpr[optionSymbols.length];
      AbstractFunctionEvaluator.determineOptions(options, ast, ast.argSize(), expectedArgSize(ast),
          optionSymbols, engine);
    }

    boolean togetherMode = engine.isTogetherMode();
    try {
      engine.setTogetherMode(true);
      int[] dims = checkMatrixDimensions(ast.arg1());
      if (dims != null) {
        if (engine.isArbitraryMode()) {
          // Arbitrary-precision: stay in the symbolic field
          FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(ast.arg1());
          if (fieldMatrix == null) {
            return F.NIL;
          }
          Predicate<IExpr> zeroChecker = buildZeroChecker(ast, options, engine);
          fieldMatrix = matrixEval(fieldMatrix, zeroChecker);
          if (fieldMatrix != null) {
            return Convert.matrix2List(fieldMatrix);
          }
          return F.NIL;
        }

        // Machine-precision real path
        RealMatrix matrix = ast.arg1().toRealMatrix();
        if (matrix != null) {
          matrix = realMatrixEval(matrix);
          if (matrix != null) {
            return Convert.realMatrix2List(matrix);
          }
        } else {
          // Complex-number path
          FieldMatrix<Complex> complexMatrix = Convert.list2ComplexMatrix(ast.arg1());
          if (complexMatrix == null) {
            return F.NIL;
          }
          complexMatrix = complexMatrixEval(complexMatrix);
          if (complexMatrix != null) {
            return Convert.complexMatrix2List(complexMatrix);
          }
          return F.NIL;
        }
      }
      return F.NIL;
    } catch (LimitException le) {
      throw le;
    } catch (final RuntimeException rex) {
      Errors.printMessage(ast.topHead(), rex);
      return F.NIL;
    } finally {
      engine.setTogetherMode(togetherMode);
    }
  }

  /** @param arg1 the first argument of the call */
  public int[] checkMatrixDimensions(IExpr arg1) {
    return arg1.isMatrix();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /**
   * Evaluate the symbolic/arbitrary-precision matrix.
   *
   * @param matrix the matrix (symbolic or Apfloat entries)
   * @param zeroChecker predicate that decides whether an entry is zero
   * @return the result matrix, or {@code null} on failure
   */
  public abstract FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix,
      Predicate<IExpr> zeroChecker);

  /**
   * Evaluate the machine-precision real matrix.
   *
   * @param matrix the real matrix
   * @return the result matrix, or {@code null} on failure
   */
  public abstract RealMatrix realMatrixEval(RealMatrix matrix);

  /**
   * Evaluate a complex-entry matrix.
   *
   * @param matrix the complex matrix
   * @return the result matrix, or {@code null} on failure
   */
  public abstract FieldMatrix<Complex> complexMatrixEval(FieldMatrix<Complex> matrix);
}
