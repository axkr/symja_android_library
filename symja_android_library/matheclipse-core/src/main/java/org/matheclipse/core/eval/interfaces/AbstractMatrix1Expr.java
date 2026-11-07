package org.matheclipse.core.eval.interfaces;

import java.util.function.Predicate;
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
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.ParserConfig;

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {

  public final static PossibleZeroQTest POSSIBLE_ZEROQ_TEST = new PossibleZeroQTest();

  public static class PossibleZeroQTest implements Predicate<IExpr> {
    @Override
    public boolean test(IExpr x) {
      return x.isPossibleZero(false);
    }
  }

  /** The test of a numeric matrix: the absolute value up to a tolerance. */
  public static final class ToleranceTest implements Predicate<IExpr> {
    private final double tolerance;

    /** The tolerance is the one of the option <code>Tolerance</code>. */
    private final boolean given;

    public ToleranceTest(double tolerance, boolean given) {
      this.tolerance = tolerance;
      this.given = given;
    }

    /** Whether the tolerance is the one of the option <code>Tolerance</code>. */
    public boolean isGiven() {
      return given;
    }

    @Override
    public boolean test(IExpr x) {
      try {
        return F.isZero(x.evalfc(), tolerance);
      } catch (ArgumentTypeException ignored) {
        // fall through to symbolic check
      }
      return x.isZero();
    }
  }

  /**
   * Test if <code>expr</code> contains a number with more than machine precision and no machine
   * number: a matrix of such numbers is computed in their precision, not converted to doubles.
   */
  public static boolean hasArbitraryPrecisionNumber(IExpr expr) {
    return expr.has(e -> e.isInexactNumber() && !e.isMachineNumber(), false)
        && !expr.has(IExpr::isMachineNumber, false);
  }

  /**
   * The zero tolerance for a matrix with arbitrary-precision numbers: the largest absolute entry
   * times <code>10^(5-p)</code> for the least precision <code>p</code> of the numbers.
   *
   * @return <code>0.0</code> if the matrix has no arbitrary-precision number, or a machine number
   */
  private static double arbitraryPrecisionTolerance(IExpr matrix) {
    long[] precision = {Long.MAX_VALUE};
    double[] scale = {0.0};
    matrix.has(e -> {
      if (e.isNumber()) {
        INumber number = (INumber) e;
        if (e.isMachineNumber()) {
          // a machine number has machine precision: no tolerance of the arbitrary precision
          precision[0] = Math.min(precision[0], ParserConfig.MACHINE_PRECISION);
        } else if (e.isInexactNumber() && e instanceof INum) {
          precision[0] = Math.min(precision[0], ((INum) e).precision());
        } else if (e.isInexactNumber() && !e.isMachineNumber()
            && e instanceof org.matheclipse.core.interfaces.IComplexNum) {
          precision[0] =
              Math.min(precision[0], ((org.matheclipse.core.interfaces.IComplexNum) e).precision());
        }
        double abs = Math.hypot(number.reDoubleValue(), number.imDoubleValue());
        if (Double.isFinite(abs)) {
          scale[0] = Math.max(scale[0], abs);
        }
      }
      return false;
    }, false);
    if (precision[0] == Long.MAX_VALUE || precision[0] <= 16 || scale[0] == 0.0) {
      return 0.0;
    }
    return Math.max(scale[0] * Math.pow(10.0, 5 - precision[0]), Double.MIN_NORMAL);
  }

  public AbstractMatrix1Expr() {}

  /**
   * Check if <code>arg1</code> is a matrix.
   *
   * @param arg1
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
        if (ast.arg1().isNumericArgument(true) && !hasArbitraryPrecisionNumber(ast.arg1())) {
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
    } catch (final RuntimeException e) {
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
   * @param ast the original AST (used to extract parameters/options)
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
      } catch (final RuntimeException e) {
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
    if (zeroTestOption.isPresent() && zeroTestOption != S.Automatic) {
      return Predicates.isZeroTest(engine, zeroTestOption);
    }
    // Determine effective numeric tolerance
    double tolerance = Config.SPECIAL_FUNCTIONS_TOLERANCE;
    boolean given = false;
    if (toleranceOption.isPresent() && toleranceOption != S.Automatic) {
      double t = toleranceOption.toDoubleDefault(Double.NaN);
      if (!Double.isNaN(t) && t > 0.0) {
        tolerance = t;
        given = true;
      }
    }
    // For floating-point matrices use tolerance-based zero check
    if (ast.arg1().isNumericArgument(true)) {
      if (!given) {
        double precisionTolerance = arbitraryPrecisionTolerance(ast.arg1());
        if (precisionTolerance > 0.0) {
          return new ToleranceTest(precisionTolerance, false);
        }
      }
      return new ToleranceTest(tolerance, given);
    }
    return POSSIBLE_ZEROQ_TEST;
  }

  /**
   * Evaluate the numeric matrix for this algorithm.
   *
   * @param matrix the matrix which contains numeric values
   * @param engine the evaluation engine
   * @param ast the original AST (used to extract parameters/options)
   * @return <code>F.NIL</code> if the evaluation isn't possible
   */
  public abstract IExpr realMatrixEval(RealMatrix matrix, EvalEngine engine, IAST ast);

}
