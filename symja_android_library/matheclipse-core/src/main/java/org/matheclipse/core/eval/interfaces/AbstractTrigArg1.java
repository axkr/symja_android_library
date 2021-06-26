package org.matheclipse.core.eval.interfaces;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;

/**
 * Base class for functions with 1 argument (i.e. Sin, Cos...) with Attributes <i>Listable</i> and
 * <i>NumericFunction</i>
 */
public abstract class AbstractTrigArg1 extends AbstractArg1 {

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    return evaluateArg1(ast.arg1(), engine);
  }

  @Override
  public IExpr numericEval(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    try {
      if (arg1 instanceof INum) {
        INumber x = ((INumber) arg1).evaluatePrecision(engine);
        if (x instanceof ApfloatNum) {
          return e1ApfloatArg(((INum) x).apfloatValue( ));
        }
        return e1DblArg(((Num) arg1).doubleValue());
      } else if (arg1 instanceof IComplexNum) {
        if (arg1 instanceof ApcomplexNum) {
          return e1ApcomplexArg(((ApcomplexNum) arg1).apcomplexValue());
        }
        return e1ComplexArg(((ComplexNum) arg1).complexValue());
      }
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      EvalEngine.get().printMessage(ast.topHead().toString() + ": " + rex.getMessage());
      return F.NIL;
    }
    return evaluateArg1(arg1, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public IExpr e1DblArg(final double d) {
    return F.NIL;
  }

  @Override
  public IExpr e1ComplexArg(final Complex c) {
    return F.NIL;
  }

  public IExpr evaluateArg1(final IExpr arg1, final EvalEngine engine) {
    return F.NIL;
  }
}
