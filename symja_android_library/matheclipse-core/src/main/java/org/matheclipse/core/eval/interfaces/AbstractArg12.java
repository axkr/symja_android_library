package org.matheclipse.core.eval.interfaces;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.AroundFunctions;
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
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/** Evaluate a function with 1 or 2 arguments. */
public abstract class AbstractArg12 extends AbstractFunctionEvaluator
    implements IBinaryArgumentHooks {

  public IExpr unaryOperator(final IExpr arg0) {
    IExpr result = e1ObjArg(arg0);

    if (result.isPresent()) {
      return result;
    }
    // argument dispatching
    if (arg0 instanceof IAST) {
      result = e1FunArg((IAST) arg0);
      if (result.isPresent()) {
        return result;
      }
    }
    final int hier = arg0.hierarchy();
    if (hier <= IExpr.INTEGERID) {
      if (hier <= IExpr.DOUBLECOMPLEXID) {
        if (hier == IExpr.DOUBLEID) {
          INumber x = ((INumber) arg0).evaluatePrecision(EvalEngine.get());
          if (x instanceof ApfloatNum) {
            return e1ApfloatArg(((ApfloatNum) x).apfloatValue());
          }
          return e1DblArg((INum) x);
        }
        if (arg0 instanceof ApcomplexNum) {
          return e1ApcomplexArg(((ApcomplexNum) arg0).apcomplexValue());
        }
        return e1DblComArg((IComplexNum) arg0);
      } else {
        return e1IntArg((IInteger) arg0);
      }
    } else {
      if (hier <= IExpr.COMPLEXID) {
        if (hier == IExpr.FRACTIONID) {
          return e1FraArg((IFraction) arg0);
        }
        return e1ComArg((IComplex) arg0);
      } else {
        if (hier == IExpr.SYMBOLID) {
          return e1SymArg((ISymbol) arg0);
        }
      }
    }

    return F.NIL;
  }

  public IExpr e1ObjArg(final IExpr o) {
    return F.NIL;
  }

  public IExpr e1DblArg(final INum d) {
    return F.NIL;
  }

  public IExpr e1DblComArg(final IComplexNum c) {
    return F.NIL;
  }

  public IExpr e1ApfloatArg(Apfloat arg1) {
    return F.NIL;
  }

  // CPD-OFF default hooks of two parallel evaluator base classes
  public IExpr e1ApcomplexArg(Apcomplex arg1) {
    return F.NIL;
  }

  public IExpr e1IntArg(final IInteger i) {
    return F.NIL;
  }

  public IExpr e1FraArg(final IFraction f) {
    return F.NIL;
  }

  public IExpr e1ComArg(final IComplex c) {
    return F.NIL;
  }

  public IExpr e1SymArg(final ISymbol s) {
    return F.NIL;
  }

  public IExpr e1FunArg(final IAST f) {
    return F.NIL;
  }
  // CPD-ON

  public IExpr binaryOperator(IAST ast, final IExpr o0, final IExpr o1) {
    IExpr result = e2ObjArg(o0, o1);
    if (result.isPresent()) {
      return result;
    }

    if (o0.isInexactNumber() && o1.isInexactNumber()) {
      try {
        EvalEngine engine = EvalEngine.get();
        INumber arg1 = ((INumber) o0).evaluatePrecision(engine);
        INumber arg2 = ((INumber) o1).evaluatePrecision(engine);
        result = IBinaryArgumentHooks.inexactDispatch(this, arg1, arg2);
        if (result.isPresent()) {
          return result;
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
    }
    return IBinaryArgumentHooks.exactDispatch(this, o0, o1);
  }

  public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
    return F.NIL;
  }

  public IExpr e2DblArg(final INum d0, final INum d1) {
    return F.NIL;
  }

  public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
    return F.NIL;
  }

  public IExpr e2ApfloatArg(final ApfloatNum d0, final ApfloatNum d1) {
    return F.NIL;
  }

  // CPD-OFF default hooks of two parallel evaluator base classes
  public IExpr e2ApcomplexArg(final ApcomplexNum c0, final ApcomplexNum c1) {
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

  public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
    return F.NIL;
  }

  public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
    return F.NIL;
  }

  public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
  // CPD-ON
    return F.NIL;
  }

  public IExpr eFunIntArg(final IAST f0, final IInteger i1) {
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    try {
      if (ast.size() == 2 || ast.size() == 3) {
        if (ast.size() != 3) {
          // f(Around(x, delta)) is Around(f(x), Abs(f'(x))*delta)
          IExpr aroundResult = AroundFunctions.mapUnaryAround(ast, engine);
          if (aroundResult.isPresent()) {
            return aroundResult;
          }
          return unaryOperator(ast.arg1());
        }
        return binaryOperator(ast, ast.arg1(), ast.arg2());
      }
    } catch (LimitException le) {
      if (Config.SHOW_STACKTRACE) {
        le.printStackTrace();
      }
      throw le;
    } catch (RuntimeException rex) {
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
