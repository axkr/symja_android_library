package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;
import static org.matheclipse.core.expression.S.Pi;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.ZetaJS;

public class Zeta extends AbstractArg12 {

  @Override
  public IExpr e1ApfloatArg(Apfloat arg1) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    try {
      // Zeta(a): delegates directly to HurwitzZeta(s, 1)) (in apfloat zeta(a) is hurwitzZeta(a,1))
      return F.num(h.zeta(arg1));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);

    }
    return F.NIL;
  }

  @Override
  public IExpr e1ApcomplexArg(Apcomplex arg1) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
    try {
      // Zeta(a): delegates directly to HurwitzZeta(s, 1)) (in apfloat zeta(a) is hurwitzZeta(a,1))
      return F.complexNum(h.zeta(arg1));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1DblArg(INum num) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
    try {
      Apcomplex zeta = h.zeta(num.apfloatValue());
      return F.num(zeta.doubleValue());
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1DblComArg(IComplexNum cNum) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
    try {
      Apcomplex zeta = h.zeta(cNum.apcomplexValue());
      return F.complexNum(zeta.real().doubleValue(), zeta.imag().doubleValue());
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    try {
      // Zeta(s, a): delegates directly to HurwitzZeta(s, a)) (in apfloat zeta(a,s) is hurwitz
      // zeta(a,s)
      return F.num(h.zeta(a1.apfloatValue(), a2.apfloatValue()));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
    try {
      // Zeta(s, a): delegates directly to HurwitzZeta(s, a)) (in apfloat zeta(a,s) is hurwitz
      // zeta(a,s)
      return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1ObjArg(final IExpr arg1) {
    if (arg1.isZero()) {
      return CN1D2;
    }
    if (arg1.isOne()) {
      return CComplexInfinity;
    }
    if (arg1.isMinusOne()) {
      // -1/12
      return QQ(-1, 12);
    }
    if (arg1.isInteger()) {
      IInteger n = (IInteger) arg1;

      if (!n.isPositive()) {
        if (n.isEven()) {
          return F.C0;
        }
        // http://fungrim.org/entry/51fd98/
        // Zeta(-n) := ((-1)^n/(n + 1))*BernoulliB(n + 1)
        n = n.negate();
        IExpr n1 = n.add(C1);
        return Times(Power(CN1, n), Power(n1, -1), BernoulliB(n1));
      }
      if (n.isEven()) {
        // http://fungrim.org/entry/72ccda/
        // Zeta(2*n) := ((((-1)^(n-1)*2^(-1+2*n)*Pi^(2*n))/(2*n)!)*BernoulliB(2*n)
        n = n.shiftRight(1);
        return Times(Power(CN1, Plus(CN1, n)), Power(C2, Plus(CN1, Times(C2, n))),
            Power(Pi, Times(C2, n)), Power(Factorial(Times(C2, n)), -1), BernoulliB(Times(C2, n)));
      }

    } else if (arg1.isInfinity()) {
      return C1;
    } else if (arg1.isAST(S.ZetaZero, 2) && arg1.first().isInteger() && arg1.first().isPositive()) {
      return F.C0;
    }
    return NIL;
  }

  @Override
  public IExpr e2ObjArg(IExpr s, IExpr a) {
    // Zeta(s, 0) and Zeta(s, 1) evaluate to Zeta(s)
    if (a.isZero() || a.isOne()) {
      return Zeta(s);
    }
    if (a.isMinusOne()) {
      return Plus(C1, Zeta(s));
    }

    int aInt = a.toIntDefault();
    if (aInt == 1) {
      return F.Zeta(s);
    }
    // Exact Hurwitz Zeta reduction for positive integers > 1
    if (aInt > 1) {
      int sInt = s.toIntDefault();
      if (sInt > 0 && sInt < aInt) {
        // Zeta(s, m) := Zeta(s) - Sum(1/k^s, {k, 1, m-1})
        IExpr sum = F.sum(k -> Power(Power(k, s), -1), 1, aInt - 1);
        return Plus(Zeta(s), Times(CN1, sum));
      }
    }

    int sInt = s.toIntDefault();
    if (F.isPresent(sInt)) {
      if (sInt <= 0 || (sInt % 2) == 0) {
        if (aInt < 0 && aInt != Integer.MIN_VALUE) {
          int n = aInt * -1;
          // Zeta(s, -n) := Zeta(s) + Sum(1/k^s, {k, 1, n})
          return Plus(F.sum(k -> Power(Power(k, s), -1), 1, n), Zeta(s));
        }
      }
    }

    if (a.isNumEqualRational(C2)) {
      return Plus(CN1, Zeta(s));
    }
    if (a.isNumEqualRational(C1D2)) {
      return Times(Plus(CN1, F.Power(F.C2, s)), Zeta(s));
    }

    EvalEngine engine = EvalEngine.get();
    if (engine.isDoubleMode()) {
      if (a.re().isPositive()) {
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
            return F.complexNum(ZetaJS.hurwitzZeta(sc, ac));
          } else {
            if (aDouble >= 0 && sDouble != 1.0) {
              return F.num(ZetaJS.hurwitzZeta(sDouble, aDouble));
            }
          }
        } catch (ValidateException ve) {
          return Errors.printMessage(S.Zeta, ve, engine);
        } catch (ThrowException te) {
          Errors.printMessage(S.Zeta, te, engine);
          return te.getValue();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.Zeta, rex, engine);
        }
      } else if (a.re().isNegative()) {
        try {
          double sDouble = Double.NaN;
          double aDouble = Double.NaN;
          try {
            sDouble = s.evalf();
            aDouble = a.evalf();
          } catch (ValidateException ve) {
          }
          Complex sc = s.evalfc();
          Complex ac = a.evalfc();
          double aReal = ac.getReal();

          if (!Double.isNaN(aReal)) {
            // WMA's definition for Zeta[s, a] when Re(a) <= 0:
            // Zeta[s, a] = Zeta[s, a + m] + Sum[((a + k)^2)^(-s/2), {k, 0, m - 1}]
            // where any term with a + k == 0 is excluded.
            int m = (int) Math.floor(-aReal) + 1;
            if (m > 0 && m <= engine.getIterationLimit()) {
              Complex sum = Complex.ZERO;
              // Equivalent to -s/2
              Complex negSHalf = sc.divide(new Complex(-2.0));

              for (int k = 0; k < m; k++) {
                Complex ak = ac.add(k);
                // Exclude term where k + a == 0
                if (ak.getReal() == 0.0 && ak.getImaginary() == 0.0) {
                  continue;
                }
                // ((a + k)^2)^(-s/2)
                Complex term = ak.multiply(ak).pow(negSHalf);
                sum = sum.add(term);
              }

              Complex z = ZetaJS.hurwitzZeta(sc, ac.add(m));
              Complex result = z.add(sum);

              // Fallback to complex result if any inputs were complex
              if (Double.isNaN(sDouble) || Double.isNaN(aDouble)) {
                return F.complexNum(result);
              } else {
                if (F.isZero(result.getImaginary())) {
                  return F.num(result.getReal());
                }
                return F.complexNum(result);
              }
            }
          }
        } catch (ValidateException ve) {
          return Errors.printMessage(S.Zeta, ve, engine);
        } catch (ThrowException te) {
          Errors.printMessage(S.Zeta, te, engine);
          return te.getValue();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.Zeta, rex, engine);
        }

      }
      return F.NIL;
    }
    if (engine.isArbitraryMode()) {
      if (a.re().isPositive()) {
        if (s instanceof ApfloatNum && a instanceof ApfloatNum) {
          return e2ApfloatArg(((ApfloatNum) s), ((ApfloatNum) a));
        }
        if (s instanceof ApcomplexNum && a instanceof ApcomplexNum) {
          return e2ApcomplexArg((ApcomplexNum) s, (ApcomplexNum) a);
        }
      }
    }
    return F.NIL;
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
