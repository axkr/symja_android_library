package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** Evaluate a function with 2 arguments. */
public abstract class AbstractArg2 extends AbstractFunctionEvaluator
    implements IBinaryArgumentHooks {

  public IExpr binaryOperator(IAST ast, final IExpr o0, final IExpr o1, EvalEngine engine) {
    IExpr result = F.NIL;
    try {
      if (o0.isNumber() && o1.isNumber()) {
        result = e2NumericArg(ast, o0, o1);
        if (result.isPresent()) {
          return result;
        }
      }

      result = e2ObjArg(ast, o0, o1);
      if (result.isPresent()) {
        return result;
      }
      return IBinaryArgumentHooks.exactDispatch(this, o0, o1);
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
    }
    return F.NIL;
  }

  private IExpr e2NumericArg(IAST ast, final IExpr o0, final IExpr o1) {
    try {
      IExpr result = IBinaryArgumentHooks.inexactDispatch(this, o0, o1);
      return result.orElseGet(() -> e2ObjArg(ast, o0, o1));
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // EvalEngine.get().printMessage(ast.topHead().toString() + ": " + rex.getMessage());
    }

    return F.NIL;
  }

  public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
    return F.NIL;
  }

  public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
    return F.NIL;
  }

  public IExpr e2DblArg(final INum d0, final INum d1) {
    return F.NIL;
  }

  public IExpr e2ApcomplexArg(final ApcomplexNum ac0, final ApcomplexNum ac1) {
    return F.NIL;
  }

  public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
    return F.NIL;
  }

  public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
    return F.NIL;
  }

  public IExpr e2SymArg(final ISymbol s0, final ISymbol s1) {
    return F.NIL;
  }

  public IExpr e2FunArg(final IAST f0, final IAST f1) {
    return F.NIL;
  }

  public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
    return F.NIL;
  }

  /**
   * Evaluate the function for the 2 given expressions.
   *
   * @param ast the original ast which has 2 arguments
   * @param arg1
   * @param arg2
   * @return <code>F#NIL</code> if no evaluation is possible.
   */
  public IExpr e2ObjArg(IAST ast, final IExpr arg1, final IExpr arg2) {
    return F.NIL;
  }

  public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
    return F.NIL;
  }

  public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
    return F.NIL;
  }

  public IExpr eFunIntArg(final IAST f0, final IInteger i1) {
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    return binaryOperator(ast, ast.arg1(), ast.arg2(), engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }
}
