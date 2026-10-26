package org.matheclipse.core.eval.interfaces;

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
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The per-type hooks of a function with two arguments, as {@link AbstractArg2} and
 * {@link AbstractArg12} declare them, and the dispatch of an argument pair to the hook for its
 * types. Every hook returns {@link F#NIL} if it does not evaluate.
 */
public interface IBinaryArgumentHooks {

  IExpr e2ApcomplexArg(ApcomplexNum c0, ApcomplexNum c1);

  IExpr e2ApfloatArg(ApfloatNum d0, ApfloatNum d1);

  IExpr e2ComArg(IComplex c0, IComplex c1);

  IExpr e2DblArg(INum d0, INum d1);

  IExpr e2DblComArg(IComplexNum d0, IComplexNum d1);

  IExpr e2FraArg(IFraction f0, IFraction f1);

  IExpr e2FunArg(IAST f0, IAST f1);

  IExpr e2IntArg(IInteger i0, IInteger i1);

  IExpr e2SymArg(ISymbol s0, ISymbol s1);

  IExpr eComFraArg(IComplex c0, IFraction i1);

  IExpr eComIntArg(IComplex c0, IInteger i1);

  IExpr eFunIntArg(IAST f0, IInteger i1);

  /**
   * Dispatch two numbers of which at least one is inexact: a complex arbitrary or machine precision
   * number makes both complex, then an arbitrary or machine precision real makes both real if the
   * other one is real. The real hook wins when both apply.
   *
   * @return {@link F#NIL} if no hook evaluates
   */
  static IExpr inexactDispatch(IBinaryArgumentHooks f, final IExpr o0, final IExpr o1) {
    IExpr result = F.NIL;
    if (o0 instanceof ApcomplexNum) {
      if (o1.isNumber()) {
        result = f.e2ApcomplexArg((ApcomplexNum) o0, ((INumber) o1).apcomplexNumValue());
      }
    } else if (o1 instanceof ApcomplexNum) {
      if (o0.isNumber()) {
        result = f.e2ApcomplexArg(((INumber) o0).apcomplexNumValue(), (ApcomplexNum) o1);
      }
    } else if (o0 instanceof ComplexNum) {
      if (o1.isNumber()) {
        result = f.e2DblComArg((ComplexNum) o0, ((INumber) o1).complexNumValue());
      }
    } else if (o1 instanceof ComplexNum) {
      if (o0.isNumber()) {
        result = f.e2DblComArg(((INumber) o0).complexNumValue(), (ComplexNum) o1);
      }
    }

    if (o0 instanceof ApfloatNum) {
      if (o1.isReal()) {
        result = f.e2ApfloatArg((ApfloatNum) o0, ((IReal) o1).apfloatNumValue());
      }
    } else if (o1 instanceof ApfloatNum) {
      if (o0.isReal()) {
        result = f.e2ApfloatArg(((IReal) o0).apfloatNumValue(), (ApfloatNum) o1);
      }
    } else if (o0 instanceof Num) {
      if (o1.isReal()) {
        result = f.e2DblArg((Num) o0, ((IReal) o1).numValue());
      }
    } else if (o1 instanceof Num) {
      if (o0.isReal()) {
        result = f.e2DblArg(((IReal) o0).numValue(), (Num) o1);
      }
    }
    return result;
  }

  /**
   * Dispatch two exact numbers, symbols or functions. An integer or a fraction paired with a
   * fraction or a complex number is widened to that type.
   *
   * @return {@link F#NIL} if no hook evaluates or the types have no hook
   */
  static IExpr exactDispatch(IBinaryArgumentHooks f, final IExpr o0, final IExpr o1) {
    if (o0 instanceof IInteger) {
      if (o1 instanceof IInteger) {
        return f.e2IntArg((IInteger) o0, (IInteger) o1);
      }
      if (o1 instanceof IFraction) {
        return f.e2FraArg(F.fraction((IInteger) o0, F.C1), (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return f.e2ComArg(F.CC((IInteger) o0, F.C0), (IComplex) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof IFraction) {
      if (o1 instanceof IInteger) {
        return f.e2FraArg((IFraction) o0, F.fraction((IInteger) o1, F.C1));
      }
      if (o1 instanceof IFraction) {
        return f.e2FraArg((IFraction) o0, (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return f.e2ComArg(F.complex((IFraction) o0), (IComplex) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof IComplex) {
      if (o1 instanceof IInteger) {
        return f.eComIntArg((IComplex) o0, (IInteger) o1);
      }
      if (o1 instanceof IFraction) {
        return f.eComFraArg((IComplex) o0, (IFraction) o1);
      }
      if (o1 instanceof IComplex) {
        return f.e2ComArg((IComplex) o0, (IComplex) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof ISymbol) {
      if (o1 instanceof ISymbol) {
        return f.e2SymArg((ISymbol) o0, (ISymbol) o1);
      }
      return F.NIL;
    }

    if (o0 instanceof IAST) {
      if (o1 instanceof IInteger) {
        return f.eFunIntArg((IAST) o0, (IInteger) o1);
      }
      if (o1 instanceof IAST) {
        return f.e2FunArg((IAST) o0, (IAST) o1);
      }
    }
    return F.NIL;
  }
}
