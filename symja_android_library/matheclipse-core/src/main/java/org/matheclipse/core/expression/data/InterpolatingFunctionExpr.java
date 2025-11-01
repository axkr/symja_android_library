package org.matheclipse.core.expression.data;

import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.hipparchus.stat.regression.SimpleRegression;
import org.hipparchus.stat.regression.UpdatingMultipleLinearRegression;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents an interpolating function stored as data within the expression system.
 * <p>
 * This class wraps various interpolation/regression models (for example
 * {@link FieldHermiteInterpolator}, {@link SimpleRegression}, or an {@link IAST} representing a
 * symbolic interpolator). It provides evaluation and normalization behavior appropriate for the
 * stored model and records the valid input domain via {@code min} and {@code max}.
 * </p>
 *
 * @param <T> the concrete model type stored in this expression
 */
public class InterpolatingFunctionExpr<T> extends DataExpr<T> {
  /**
   * Minimum domain value for which the interpolator is defined (inclusive).
   */
  double min;

  /**
   * Maximum domain value for which the interpolator is defined (inclusive).
   */
  double max;

  /**
   * Wrapper for an interpolating function represented as an {@link IAST}. This variant returns the
   * original stored AST via {@link #normal(boolean)}.
   */
  private static class ASTFunctionExpr extends InterpolatingFunctionExpr<IAST> {
    private static final long serialVersionUID = 7355009868203033788L;

    /**
     * Return the stored {@link IAST} representation for normalization.
     *
     * @param nilIfUnevaluated ignored for this implementation
     * @return the AST data representing the interpolator
     */
    @Override
    public IAST normal(boolean nilIfUnevaluated) {
      return toData();
    }

    /**
     * Construct an AST-backed interpolating function.
     *
     * @param interpolator the AST representing the interpolator
     * @param min the minimum domain value
     * @param max the maximum domain value
     */
    public ASTFunctionExpr(final IAST interpolator, double min, double max) {
      super(interpolator, min, max);
    }
  }

  /**
   * Wrapper for a Hermite interpolator model ({@link FieldHermiteInterpolator})
   */
  private static class HermiteFunctionExpr
      extends InterpolatingFunctionExpr<FieldHermiteInterpolator<IExpr>> {
    private static final long serialVersionUID = 5139557783149167160L;

    public HermiteFunctionExpr(final FieldHermiteInterpolator<IExpr> interpolator, double min,
        double max) {
      super(interpolator, min, max);
    }
  }

  /** */
  private static final long serialVersionUID = -3183236658957651705L;

  /**
   * Create a new instance wrapping a {@link FieldHermiteInterpolator}.
   *
   * @param interpolator the hermite interpolator model
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(
      final FieldHermiteInterpolator<IExpr> interpolator, double min, double max) {
    return new HermiteFunctionExpr(interpolator, min, max);
  }

  /**
   * Create a new instance wrapping an {@link IAST} interpolator.
   *
   * @param interpolator the AST representing the interpolator
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final IAST interpolator, double min,
      double max) {
    return new ASTFunctionExpr(interpolator, min, max);
  }

  /**
   * Create a new instance wrapping an {@link UpdatingMultipleLinearRegression} model.
   *
   * @param value the regression model instance
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final UpdatingMultipleLinearRegression value,
      double min, double max) {
    return new InterpolatingFunctionExpr(value, min, max);
  }

  /**
   * Protected constructor used by concrete subclasses and factory methods.
   *
   * @param function the underlying model to store as data
   * @param min the minimum domain value (inclusive)
   * @param max the maximum domain value (inclusive)
   */
  protected InterpolatingFunctionExpr(final T function, double min, double max) {
    super(S.InterpolatingFunction, function);
    this.min = min;
    this.max = max;
  }

  /**
   * Equality checks consider the wrapped model data for equivalence.
   *
   * @param obj the object to compare with
   * @return {@code true} if the underlying data is equal, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof InterpolatingFunctionExpr) {
      return fData.equals(((InterpolatingFunctionExpr) obj).fData);
    } else if (obj instanceof ASTFunctionExpr) {
      return fData.equals(((ASTFunctionExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 461 : 461 + fData.hashCode();
  }

  /**
   * Returns the expression hierarchy id for interpolated functions.
   *
   * @return the hierarchy id
   */
  @Override
  public int hierarchy() {
    return INTERPOLATEDFUNCTONID;
  }

  /**
   * Evaluate an AST that has this interpolating function as head and a single argument.
   * <p>
   * Behavior:
   * <ul>
   * <li>If the head is an {@link InterpolatingFunctionExpr} and the AST has one argument, attempt
   * to evaluate the argument to a numeric value.</li>
   * <li>If numeric and outside {@code [min, max]} a warning message is emitted via
   * {@link Errors#printMessage} indicating extrapolation will be used.</li>
   * <li>If the head is a {@link HermiteFunctionExpr} and the stored model is a
   * {@link FieldHermiteInterpolator}, the interpolator is invoked and a numeric result (or list) is
   * returned.</li>
   * <li>If the head is an {@link ASTFunctionExpr} and the stored model is an {@link IAST}, the
   * interpolator AST is applied to the argument via {@link F#unaryAST1}.</li>
   * <li>For non-numeric/symbolic arguments or unsupported models {@link F#NIL} is returned.</li>
   * </ul>
   *
   * @param ast the AST to evaluate (expected with this interpolator as head)
   * @param engine the evaluation engine (used for error/warning messages)
   * @return the evaluated expression or {@link F#NIL} if not applicable
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof InterpolatingFunctionExpr && ast.isAST1()) {
      IExpr arg1 = ast.arg1();
      if (arg1.isComplex() || arg1.isComplexNumeric()) {
        return F.NIL;
      }
      boolean evaled = false;
      try {
        double value = arg1.evalf();
        evaled = true;
        if (value < min || value > max) {
          // Input value `1` lies outside the range of data in the interpolating function.
          // Extrapolation will be used.
          Errors.printMessage(ast.topHead(), "dmval", F.list(F.list(arg1)), engine);
        }
      } catch (ArgumentTypeException atex) {
        // fall through for symbolic arguments
      }
      final IExpr head = ast.head();
      if (head instanceof HermiteFunctionExpr) {
        if (evaled) {
          InterpolatingFunctionExpr<IAST> function = (InterpolatingFunctionExpr<IAST>) head;
          Object model = function.toData();
          if (model instanceof FieldHermiteInterpolator) {
            FieldHermiteInterpolator<IExpr> interpolator = (FieldHermiteInterpolator<IExpr>) model;
            IExpr[] arr = interpolator.value(arg1);
            if (arr.length == 1) {
              return arr[0];
            }
            return F.List(arr);
          }
        }
      } else if (head instanceof ASTFunctionExpr) {
        ASTFunctionExpr function = (ASTFunctionExpr) head;
        Object model = function.toData();
        if (model instanceof IAST) {
          IAST interpolator = (IAST) model;
          return F.unaryAST1(interpolator, arg1);
        }
      }
    }

    return F.NIL;
  }

  @Override
  public IExpr copy() {
    return new InterpolatingFunctionExpr(fData, min, max);
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    Object model = toData();
    if (model instanceof SimpleRegression) {
      SimpleRegression simpleModel = (SimpleRegression) model;
      return F.Plus(F.num(simpleModel.getIntercept()), F.Times(F.num(simpleModel.getSlope()), S.C));
    }
    return F.NIL;
  }
}
