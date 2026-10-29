package org.matheclipse.core.reflection.system;

import org.matheclipse.external.apfloat.ApcomplexZetaMath;
import org.matheclipse.core.interfaces.INumber;
import org.apfloat.ApfloatRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.external.apfloat.ZetaWorkaround;
import org.matheclipse.core.interfaces.Attribute;
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
import org.matheclipse.core.eval.util.CompensatedSum;
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
import org.matheclipse.core.numerics.functions.HurwitzZetaKernel;
import org.matheclipse.core.numerics.functions.ZetaJS;

public class Zeta extends AbstractArg12 {

  /**
   * From this real part on a machine argument is summed with the Dirichlet series, which has its
   * terms above the precision of a double then.
   */
  private static final double LARGE_MACHINE_ARGUMENT = 16.0;

  /**
   * Below this even integer the value is written with <code>Pi</code> and a Bernoulli number,
   * whose numerator has thousands of digits from here on.
   */
  private static final int MAX_EVEN_CLOSED_FORM = 1000;

  /**
   * <code>Zeta(s)</code> for an <code>s</code> next to the pole at <code>1</code>. The value is
   * <code>1/(s-1)+EulerGamma+...</code>, so it has only as many digits as <code>s-1</code> has,
   * which are fewer than those of <code>s</code> by the number of zeros after the <code>1</code>.
   * A number of fixed precision would show the missing digits filled up with noise:
   * <code>N(Zeta(1+10^-10),30)</code> was wrong from the 21st digit on. The argument is taken as
   * the decimal number it shows, and the digits which are lost are added to it first.
   *
   * @param s the argument
   * @param precision the precision of the result
   * @return <code>null</code> if <code>s</code> is not near to the pole
   */
  private static Apcomplex zetaNearPole(Apcomplex s, long precision) {
    Apcomplex distance = s.subtract(Apcomplex.ONE);
    if (distance.real().signum() == 0 && distance.imag().signum() == 0) {
      return null;
    }
    long lostDigits = 1 - distance.scale();
    if (lostDigits <= 1 || lostDigits > Config.MAX_PRECISION_APFLOAT) {
      return null;
    }
    long workingPrecision = precision + lostDigits + 5;
    Apcomplex extended =
        new Apcomplex(s.real().precision(workingPrecision), s.imag().precision(workingPrecision));
    return ZetaWorkaround.zeta(extended);
  }

  @Override
  public IExpr e1ApfloatArg(Apfloat arg1) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    try {
      // Zeta(a): delegates directly to HurwitzZeta(s, 1)) (in apfloat zeta(a) is hurwitzZeta(a,1))
      Apcomplex nearPole = zetaNearPole(arg1, h.precision());
      if (nearPole != null) {
        return F.num(h.valueOf(nearPole.real()));
      }
      // the Dirichlet series for a large argument
      Apcomplex large = ApcomplexZetaMath.zetaOfLargeRealPart(arg1.precision(h.precision()));
      if (large != null) {
        return F.num(large.real());
      }
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
      Apcomplex nearPole = zetaNearPole(arg1, h.precision());
      if (nearPole != null) {
        return F.complexNum(h.valueOf(nearPole));
      }
      final long precision = h.precision();
      Apcomplex large =
          ApcomplexZetaMath.zetaOfLargeRealPart(ApcomplexNum.precisionOfParts(arg1, precision));
      if (large != null) {
        return F.complexNum(large);
      }
      return F.complexNum(h.zeta(arg1));
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1DblArg(INum num) {
    double machine = HurwitzZetaKernel.hurwitzZeta(num.doubleValue(), 1.0);
    if (!Double.isNaN(machine)) {
      return F.num(machine);
    }
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
    try {
      if (num.doubleValue() >= LARGE_MACHINE_ARGUMENT) {
        // 1 + 2^-s + ...: the routine of the library doesn't return for 10^10, and is 0 from 10^20
        Apcomplex large = ApcomplexZetaMath
            .zetaOfLargeRealPart(ApfloatNum.apfloatOf(num.doubleValue(), h.precision()));
        if (large != null) {
          return F.num(large.real().doubleValue());
        }
      }
      Apcomplex zeta = h.zeta(num.apfloatValue());
      return F.num(zeta.doubleValue());
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e1DblComArg(IComplexNum cNum) {
    Complex machine = HurwitzZetaKernel.hurwitzZeta(
        new Complex(cNum.reDoubleValue(), cNum.imDoubleValue()), Complex.ONE);
    if (machine != null) {
      return F.complexNum(machine);
    }
    FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
    try {
      if (cNum.reDoubleValue() >= LARGE_MACHINE_ARGUMENT) {
        Apcomplex large = ApcomplexZetaMath.zetaOfLargeRealPart(
            new Apcomplex(ApfloatNum.apfloatOf(cNum.reDoubleValue(), h.precision()),
                ApfloatNum.apfloatOf(cNum.imDoubleValue(), h.precision())));
        if (large != null) {
          return F.complexNum(large.real().doubleValue(), large.imag().doubleValue());
        }
      }
      Apcomplex zeta = h.zeta(cNum.apcomplexValue());
      return F.complexNum(zeta.real().doubleValue(), zeta.imag().doubleValue());
    } catch (Exception ce) {
      Errors.rethrowsInterruptException(ce);
    }
    return F.NIL;
  }

  @Override
  public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
    // Zeta(s, a) is HurwitzZeta(s, a)
    return HurwitzZeta.e2ApfloatArg(a1, a2);
  }

  @Override
  public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
    // Zeta(s, a) is HurwitzZeta(s, a)
    return HurwitzZeta.e2ApcomplexArg(a1, a2);
  }

  @Override
  public IExpr e1ObjArg(final IExpr arg1) {
    // a regular point with the value -1/2
    if (arg1.isZero()) {
      return arg1.isInexactNumber() ? F.num(-0.5) : CN1D2;
    }
    if (arg1.isInexactNumber() && ((INumber) arg1).isFuzzyZero()) {
      // a number next to 0, which the series do not reach: -1/2-Log(2*Pi)/2*s
      return F.Plus(CN1D2, F.Times(CN1D2, F.Log(F.Times(F.C2, S.Pi)), arg1));
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
      if (n.isEven() && n.isLT(F.ZZ(MAX_EVEN_CLOSED_FORM))) {
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

    if (s.isInteger() && !s.isPositive() && a.isFraction()
        && (a.isPositive() || ((IInteger) s).isEven())) {
      // -BernoulliB(n+1, a)/(n+1) for s == -n. Below zero this function sums the absolute values
      // of k+a, which is the same for an even n only.
      IExpr n1 = F.C1.subtract(s);
      return F.Times(F.CN1, F.Power(n1, F.CN1), F.BernoulliB(n1, a));
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
      try {
        if (a.re().isPositive()) {
          double sDouble = s.evalfNaN();
          double aDouble = a.evalfNaN();
          if (aDouble < 0.0 || Double.isNaN(sDouble) || Double.isNaN(aDouble)) {
            Complex sc = s.evalfc();
            Complex ac = a.evalfc();
            return F.complexNum(ZetaJS.hurwitzZeta(sc, ac));
          } else {
            if (aDouble >= 0 && sDouble != 1.0) {
              return F.num(ZetaJS.hurwitzZeta(sDouble, aDouble));
            }
          }
        } else if (a.re().isNegative()) {
          double sDouble = s.evalfNaN();
          double aDouble = a.evalfNaN();
          Complex sc = s.evalfc();
          Complex ac = a.evalfc();
          double aReal = ac.getReal();

          if (!Double.isNaN(aReal)) {
            // the definition of Zeta[s, a] when Re(a) <= 0:
            // Zeta[s, a] = Zeta[s, a + m] + Sum[((a + k)^2)^(-s/2), {k, 0, m - 1}]
            // where any term with a + k == 0 is excluded.
            int m = (int) Math.floor(-aReal) + 1;
            if (m > 0 && m <= engine.getIterationLimit()) {
              final CompensatedSum re = new CompensatedSum();
              final CompensatedSum im = new CompensatedSum();
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
                re.add(term.getReal());
                im.add(term.getImaginary());
              }
              Complex sum = new Complex(re.value(), im.value());

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
      if (s instanceof INumber && a instanceof INumber) {
        // a real and a complex argument, or a second argument without a positive real part, where
        // the terms with a + k == 0 are left out and the other ones are ((a + k)^2)^(-s/2)
        try {
          // CPD-OFF parallel apfloat Zeta kernels
          Apcomplex value = ApcomplexZetaMath.generalizedZeta(((INumber) s).apcomplexValue(),
              ((INumber) a).apcomplexValue());
          return value.imag().signum() == 0 ? F.num(value.real()) : F.complexNum(value);
        } catch (ArithmeticException | ApfloatRuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
        }
      }
      // CPD-ON
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    super.setUp(newSymbol);
  }

}
