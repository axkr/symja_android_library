package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.S.Pi;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.ZetaJS;

public final class HurwitzZeta extends AbstractFunctionEvaluator {

  public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    try {
      return F.num(h.zeta(a1.apfloatValue(), a2.apfloatValue()));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
    try {
      return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);

    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr s = ast.arg1();
    IExpr a = ast.arg2();

    // 1. Fundamental Pole: s == 1
    if (s.isOne()) {
      // http://fungrim.org/entry/532f31/
      return F.CComplexInfinity;
    }

    // 2. Base Zero Poles: a <= 0 integers
    if (a.isInteger() && !a.isPositive()) {
      // If Re(s) > 0, the sum encounters a 1/0^s term -> ComplexInfinity
      if (s.re().isPositive()) {
        return F.CComplexInfinity;
      }
      // If s == 0, the sum encounters a 0^0 term -> Indeterminate
      if (s.isZero()) {
        return S.Indeterminate;
      }
    }

    if (s.isNumber()) {
      if (s.isZero()) {
        // http://fungrim.org/entry/d99808/
        return F.Subtract(F.C1D2, a);
      }
    }

    if (a.isNumber()) {
      if (a.isZero() && s.isInteger() && s.isNegative()) {
        // http://fungrim.org/entry/7dab87/
        return F.Times(F.CN1, F.Divide(F.BernoulliB(F.Plus(1, s.negate())), F.Plus(1, s.negate())));
      }
      if (a.isOne()) {
        // http://fungrim.org/entry/af23f7/
        return F.Zeta(s);
      }
      if (a.isNumEqualInteger(F.C2)) {
        // http://fungrim.org/entry/b721b4/
        return F.Plus(F.CN1, F.Zeta(s));
      }
      if (a.isNumEqualRational(F.C1D2)) {
        // http://fungrim.org/entry/af7d3d/
        return F.Times(F.Plus(F.CN1, F.Power(F.C2, s)), F.Zeta(s));
      }
      if (a.isNumEqualRational(F.C3D4)) {
        // http://fungrim.org/entry/951f86/
        return F.Plus(F.Times(F.CN8, S.Catalan), F.Sqr(Pi));
      }
      if (s.isInteger() && a.isInteger() && a.isPositive()) {
        IInteger sInt = (IInteger) s;
        if (sInt.isNegative() || sInt.isEven()) {
          // http://fungrim.org/entry/6e69fc/
          int n = a.toIntDefault();
          int sNegate = sInt.negate().toIntDefault();
          if (n > Integer.MIN_VALUE && sNegate > Integer.MIN_VALUE) {
            return F.Subtract(F.Zeta(s), F.sum(k -> {
              return k.power(sNegate);
            }, 1, n - 1));
          }
        }
      }
    }

    if (engine.isDoubleMode()) {
      try {
        double sDouble = Double.NaN;
        double aDouble = Double.NaN;
        try {
          sDouble = s.evalf();
          aDouble = a.evalf();
        } catch (ValidateException ve) {
        }
        if (aDouble < 0.0 || Double.isNaN(sDouble) || Double.isNaN(aDouble)) {
          Complex sc = s.evalfc();
          Complex ac = a.evalfc();
          Complex result = ZetaJS.hurwitzZeta(sc, ac);

          // Determine optimal numeric return format
          if (Double.isNaN(sDouble) || Double.isNaN(aDouble)) {
            return F.complexNum(result);
          } else if (F.isZero(result.getImaginary())) {
            return F.num(result.getReal());
          }
          return F.complexNum(result);
        } else {
          if (aDouble >= 0 && sDouble != 1.0) {
            return F.num(ZetaJS.hurwitzZeta(sDouble, aDouble));
          }
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (ThrowException te) {
        Errors.printMessage(S.HurwitzZeta, te, engine);
        return te.getValue();
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.HurwitzZeta, rex, engine);
      }
    }
    if (engine.isArbitraryMode()) {
      if (s instanceof ApfloatNum && a instanceof ApfloatNum) {
        return e2ApfloatArg(((ApfloatNum) s), ((ApfloatNum) a));
      }
      if (s instanceof ApcomplexNum && a instanceof ApcomplexNum) {
        return e2ApcomplexArg((ApcomplexNum) s, (ApcomplexNum) a);
      }
    }
    return NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    super.setUp(newSymbol);
  }
}
