package org.matheclipse.core.eval.interfaces;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/** Evaluate a function with 1 or 2 arguments. */
public abstract class AbstractArg12 extends AbstractFunctionEvaluator {

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
        if (arg1 instanceof ApcomplexNum) {
          if (arg2.isNumber()) {
            result = e2ApcomplexArg((ApcomplexNum) arg1, arg2.apcomplexNumValue());
          }
        } else if (arg2 instanceof ApcomplexNum) {
          if (arg1.isNumber()) {
            result = e2ApcomplexArg(arg1.apcomplexNumValue(), (ApcomplexNum) arg2);
          }
        } else if (arg1 instanceof ComplexNum) {
          if (arg2.isNumber()) {
            result = e2DblComArg((ComplexNum) arg1, arg2.complexNumValue());
          }
        } else if (arg2 instanceof ComplexNum) {
          if (arg1.isNumber()) {
            result = e2DblComArg(arg1.complexNumValue(), (ComplexNum) arg2);
          }
        }

        if (arg1 instanceof ApfloatNum) {
          if (arg2.isReal()) {
            result = e2ApfloatArg((ApfloatNum) arg1, ((ISignedNumber) arg2).apfloatNumValue());
          }
        } else if (arg2 instanceof ApfloatNum) {
          if (arg1.isReal()) {
            result = e2ApfloatArg(((ISignedNumber) arg1).apfloatNumValue(), (ApfloatNum) arg2);
          }
        } else if (arg1 instanceof Num) {
          if (arg2.isReal()) {
            result = e2DblArg((Num) arg1, ((ISignedNumber) arg2).numValue());
          }
        } else if (arg2 instanceof Num) {
          if (arg1.isReal()) {
            result = e2DblArg(((ISignedNumber) arg1).numValue(), (Num) arg2);
          }
        }
        if (result.isPresent()) {
          return result;
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        // EvalEngine.get().printMessage(ast.topHead().toString() + ": " + rex.getMessage());
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return F.NIL;
      }
    }

    if (o0 instanceof IInteger) {
      if (o1 instanceof IInteger) {
        return e2IntArg((IInteger) o0, (IInteger) o1);
      }
      if (o1 instanceof IFraction) {
        return e2FraArg(F.fraction((IInteger) o0, F.C1), (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return e2ComArg(F.complex((IInteger) o0, F.C0), (IComplex) o1);
      }

      return F.NIL;
    }

    if (o0 instanceof IFraction) {
      if (o1 instanceof IInteger) {
        return e2FraArg((IFraction) o0, F.fraction((IInteger) o1, F.C1));
      }
      if (o1 instanceof IFraction) {
        return e2FraArg((IFraction) o0, (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return e2ComArg(F.complex((IFraction) o0), (IComplex) o1);
      }

      return F.NIL;
    }

    if (o0 instanceof IComplex) {
      if (o1 instanceof IInteger) {
        return eComIntArg((IComplex) o0, (IInteger) o1);
      }
      if (o1 instanceof IFraction) {
        return eComFraArg((IComplex) o0, (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return e2ComArg((IComplex) o0, (IComplex) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof ISymbol) {
      if (o1 instanceof ISymbol) {
        return e2SymArg((ISymbol) o0, (ISymbol) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof IAST) {
      if (o1 instanceof IInteger) {
        return eFunIntArg((IAST) o0, (IInteger) o1);
      }
      if (o1 instanceof IAST) {
        return e2FunArg((IAST) o0, (IAST) o1);
      }
    }

    return F.NIL;
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
          return unaryOperator(ast.arg1());
        }
        return binaryOperator(ast, ast.arg1(), ast.arg2());
      }
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      return engine.printMessage(ast.topHead(), rex);
    }
    return F.NIL;
    // return engine.printMessage(ast.topHead() + ": " + ast.topHead()
    // + " function requires 1 or 2 arguments, but number of args equals: " + ast.argSize());
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
