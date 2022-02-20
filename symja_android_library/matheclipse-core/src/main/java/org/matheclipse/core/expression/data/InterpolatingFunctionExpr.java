package org.matheclipse.core.expression.data;

import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.hipparchus.stat.regression.SimpleRegression;
import org.hipparchus.stat.regression.UpdatingMultipleLinearRegression;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class InterpolatingFunctionExpr<T> extends DataExpr<T> {
  double min;
  double max;

  private static class ASTFunctionExpr extends InterpolatingFunctionExpr<IAST> {
    private static final long serialVersionUID = 7355009868203033788L;

    @Override
    public IAST normal(boolean nilIfUnevaluated) {
      return toData();
    }

    public ASTFunctionExpr(final IAST interpolator, double min, double max) {
      super(interpolator, min, max);
    }
  }

  private static class HermiteFunctionExpr
      extends InterpolatingFunctionExpr<FieldHermiteInterpolator> {
    private static final long serialVersionUID = 5139557783149167160L;

    public HermiteFunctionExpr(final FieldHermiteInterpolator interpolator, double min,
        double max) {
      super(interpolator, min, max);
    }
  }

  /** */
  private static final long serialVersionUID = -3183236658957651705L;

  public static InterpolatingFunctionExpr newInstance(
      final FieldHermiteInterpolator<IExpr> interpolator, double min, double max) {
    return new HermiteFunctionExpr(interpolator, min, max);
  }

  public static InterpolatingFunctionExpr newInstance(final IAST interpolator, double min,
      double max) {
    return new ASTFunctionExpr(interpolator, min, max);
  }

  /**
   * @param value
   * @return
   */
  public static InterpolatingFunctionExpr newInstance(final UpdatingMultipleLinearRegression value,
      double min, double max) {
    return new InterpolatingFunctionExpr(value, min, max);
  }

  protected InterpolatingFunctionExpr(final T function, double min, double max) {
    super(S.InterpolatingFunction, function);
    this.min = min;
    this.max = max;
  }

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

  @Override
  public int hierarchy() {
    return INTERPOLATEDFUNCTONID;
  }

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof InterpolatingFunctionExpr && ast.isAST1()) {
      IExpr arg1 = ast.arg1();
      if (arg1.isComplex() || arg1.isComplexNumeric()) {
        return F.NIL;
      }
      boolean evaled = false;
      try {
        double value = arg1.evalDouble();
        evaled = true;
        if (value < min || value > max) {
          // Input value `1` lies outside the range of data in the interpolating function.
          // Extrapolation will be used.
          IOFunctions.printMessage(ast.topHead(), "dmval", F.list(F.list(arg1)), engine);
        }
      } catch (ArgumentTypeException atex) {
        // fall through for symbolic arguments
      }
      if (ast.head() instanceof HermiteFunctionExpr) {
        if (evaled) {
          InterpolatingFunctionExpr<IAST> function = (InterpolatingFunctionExpr<IAST>) ast.head();
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
        return F.NIL;
      }

      if (ast.head() instanceof ASTFunctionExpr) {
        ASTFunctionExpr function = (ASTFunctionExpr) ast.head();
        Object model = function.toData();
        if (model instanceof IAST) {
          IAST interpolator = (IAST) model;
          return F.unaryAST1(interpolator, arg1);
        }

        return F.NIL;
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
