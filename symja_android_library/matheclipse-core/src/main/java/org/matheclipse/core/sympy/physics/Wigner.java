package org.matheclipse.core.sympy.physics;


import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;

public class Wigner {

  public static class TriangularException extends Exception {
    IExpr value;

    public TriangularException(IExpr value) {
      super("");
      this.value = value;
    }

    public IExpr getValue() {
      return value;
    }

  }

  public static class NotPhysicalException extends Exception {
    IExpr value;

    public NotPhysicalException(IExpr value) {
      super("");
      this.value = value;
    }

    public IExpr getValue() {
      return value;
    }

  }

  private static IRational intOrHalfInt(IReal value) throws TriangularException {
    if (value.isRational()) {
      IInteger denominator = ((IRational) value).denominator();
      if (denominator.equals(F.C1)) {
        return ((IRational) value).numerator();
      } else if (denominator.equals(F.C2)) {
        return (IRational) value;
      }
    }
    int intValue = value.toIntDefault();
    if (intValue != Integer.MIN_VALUE) {
      return F.ZZ(intValue);
    }
    value = value.multiply(F.C2);
    intValue = value.toIntDefault();
    if (intValue != Integer.MIN_VALUE) {
      return F.QQ(intValue, 2);
    }
    throw new Wigner.TriangularException(F.C0);
  }

  public static IExpr wigner3j(IReal j1Num, IReal j2Num, IReal j3Num, IReal m1Num, IReal m2Num,
      IReal m3Num) throws TriangularException, NotPhysicalException {
    IRational j1 = intOrHalfInt(j1Num);
    IRational j2 = intOrHalfInt(j2Num);
    IRational j3 = intOrHalfInt(j3Num);
    IRational m1 = intOrHalfInt(m1Num);
    IRational m2 = intOrHalfInt(m2Num);
    IRational m3 = intOrHalfInt(m3Num);

    if (!m1.add(m2).add(m3).isZero()) {
      throw new Wigner.NotPhysicalException(F.C0);
    }

    IRational a1 = j1.add(j2).subtract(j3);
    if (a1.isNegative()) {
      throw new Wigner.NotPhysicalException(F.C0);
    }
    IRational a2 = j1.subtract(j2).add(j3);
    if (a2.isNegative()) {
      throw new Wigner.NotPhysicalException(F.C0);
    }
    IRational a3 = j2.add(j3).subtract(j1);
    // double a3 = -j1 + j2 + j3;
    if (a3.isNegative()) {
      return F.C0;
    }
    if (m1.abs().isGT(j1) || m2.abs().isGT(j2) || m3.abs().isGT(j3)) {
      return F.C0;
    }

    // int maxFact = (int) Math.max(j1 + j2 + j3 + 1,
    // Math.max(j1 + Math.abs(m1), Math.max(j2 + Math.abs(m2), j3 + Math.abs(m3))));
    // calcFactList(maxFact);

    IInteger multiplyValue = NumberTheory.factorial(j1.add(j2).subtract(j3).numerator()) //
        .multiply(NumberTheory.factorial(j1.subtract(j2).add(j3).numerator())) //
        .multiply(NumberTheory.factorial(j2.add(j3).subtract(j1).numerator())) //
        .multiply(NumberTheory.factorial(j1.subtract(m1).numerator())) //
        .multiply(NumberTheory.factorial(j1.add(m1).numerator())) //
        .multiply(NumberTheory.factorial(j2.subtract(m2).numerator())) //
        .multiply(NumberTheory.factorial(j2.add(m2).numerator())) //
        .multiply(NumberTheory.factorial(j3.subtract(m3).numerator())) //
        .multiply(NumberTheory.factorial(j3.add(m3).numerator()));//
    // .divide(NumberTheory.factorial((int) (j1 + j2 + j3 + 1)));

    // double multiplyValue = argsqrt.doubleValue();
    IRational divideValue = NumberTheory.factorial(j1.add(j2).add(j3).add(1).numerator());
    IExpr ressqrt = F.Sqrt(multiplyValue.divide(divideValue));
    // double ressqrt = Math.sqrt(multiplyValue / divideValue);
    // if (Double.isInfinite(ressqrt) || Double.isNaN(ressqrt)) {
    // ressqrt = Math.real(ressqrt);
    // }

    int imin = j1.add(m2).subtract(j3).max(j2.subtract(m1).subtract(j3).max(F.C0)).toIntDefault();
    int imax = j2.add(m2).min(j1.subtract(m1).min(j1.add(j2).subtract(j3))).toIntDefault();
    // int imin = (int) Math.max(-j3 + j1 + m2, Math.max(-j3 + j2 - m1, 0));
    // int imax = (int) Math.min(j2 + m2, Math.min(j1 - m1, j1 + j2 - j3));
    if (imin == Integer.MIN_VALUE || imax == Integer.MIN_VALUE) {
      throw new ArgumentTypeException("error",
          F.List("Expecting non-negative machine integer, got " + imin + " or " + imax));
    }
    IExpr sumres = F.C0;
    for (int ii = imin; ii <= imax; ii++) {
      IInteger den = NumberTheory.factorial(ii)//
          .multiply(NumberTheory.factorial(F.ZZ(ii).add(j3.subtract(j1).subtract(m2).numerator()))) //
          .multiply(NumberTheory.factorial(j2.add(m2).subtract(F.ZZ(ii)).numerator())) //
          .multiply(NumberTheory.factorial(j1.subtract(m1).subtract(F.ZZ(ii)).numerator())) //
          .multiply(NumberTheory.factorial(F.ZZ(ii).add(j3).subtract(j2).add(m1).numerator())) //
          .multiply(NumberTheory.factorial(j1.add(j2).subtract(j3).subtract(F.ZZ(ii)).numerator()));
      if (ii == 0) {
        sumres = sumres.add(F.Power(den, F.CN1));
      } else {
        sumres = sumres.add(F.Power(F.CN1, F.ZZ(ii)).divide(den));
      }
    }

    IRational exponent = j1.subtract(j2).subtract(m3);
    if (exponent.isZero()) {
      return F.Times(ressqrt, sumres);
    }
    IExpr prefid = F.Power(F.CN1, exponent);
    return F.Times(ressqrt, sumres, prefid);
  }

  public static IExpr racah(IRational aa, IRational bb, IRational cc, IRational dd, IRational ee,
      IRational ff) throws TriangularException {
    IExpr prefac = F.Times(bigDeltaCoeff(aa, bb, ee), bigDeltaCoeff(cc, dd, ee),
        bigDeltaCoeff(aa, cc, ff), bigDeltaCoeff(bb, dd, ff));
    EvalEngine engine = EvalEngine.get();
    prefac = engine.evaluate(prefac);
    if (prefac.isPossibleZero(true)) {
      return F.C0;
    }

    int imin = aa.add(bb).add(ee)
        .max(cc.add(dd).add(ee).max(aa.add(cc).add(ff).max(bb.add(dd).add(ff)))).toIntDefault();
    int imax = aa.add(bb).add(cc).add(dd)
        .min(aa.add(dd).add(ee).add(ff).min(bb.add(cc).add(ee).add(ff))).toIntDefault();
    // int imin =
    // (int) Math.max(Math.max(aa + bb + ee, cc + dd + ee), Math.max(aa + cc + ff, bb + dd + ff));
    // int imax = (int) Math.min(Math.min(aa + bb + cc + dd, aa + dd + ee + ff), bb + cc + ee + ff);
    if (imin == Integer.MIN_VALUE || imax == Integer.MIN_VALUE) {
      throw new ArgumentTypeException("error",
          F.List("Expecting non-negative machine integer, got " + imin + " or " + imax));
    }

    // int maxfact = (int) Math.max(Math.max(imax + 1, aa + bb + cc + dd),
    // Math.max(aa + dd + ee + ff, bb + cc + ee + ff));
    // calcFactList(maxfact);

    IExpr sumres = F.C0;
    for (int kk = imin; kk <= imax; kk++) {
      // BigInteger den = factList.get(kk - (int) aa - (int) bb - (int) ee)
      // .multiply(factList.get(kk - (int) cc - (int) dd - (int) ee))
      // .multiply(factList.get(kk - (int) aa - (int) cc - (int) ff))
      // .multiply(factList.get(kk - (int) bb - (int) dd - (int) ff))
      // .multiply(factList.get((int) aa + (int) bb + (int) cc + (int) dd - kk))
      // .multiply(factList.get((int) aa + (int) dd + (int) ee + (int) ff - kk))
      // .multiply(factList.get((int) bb + (int) cc + (int) ee + (int) ff - kk));
      // sumres += Math.pow(-1, kk) * factList.get(kk + 1).doubleValue() / den.doubleValue();
      IInteger k1 = F.ZZ(kk).subtract(aa).subtract(bb).subtract(ee).numerator();
      IInteger k2 = F.ZZ(kk).subtract(cc).subtract(dd).subtract(ee).numerator();
      IInteger k3 = F.ZZ(kk).subtract(aa).subtract(cc).subtract(ff).numerator();
      IInteger k4 = F.ZZ(kk).subtract(bb).subtract(dd).subtract(ff).numerator();

      IInteger k5 = aa.add(bb).add(cc).add(dd).subtract(F.ZZ(kk)).numerator();
      IInteger k6 = aa.add(dd).add(ee).add(ff).subtract(F.ZZ(kk)).numerator();
      IInteger k7 = bb.add(cc).add(ee).add(ff).subtract(F.ZZ(kk)).numerator();
      IInteger den = NumberTheory.factorial(k1)//
          .multiply(NumberTheory.factorial(k2)) //
          .multiply(NumberTheory.factorial(k3))//
          .multiply(NumberTheory.factorial(k4)) //
          .multiply(NumberTheory.factorial(k5))//
          .multiply(NumberTheory.factorial(k6))//
          .multiply(NumberTheory.factorial(k7));

      IInteger kk1 = NumberTheory.factorial(kk + 1);
      if (kk == 0) {
        sumres = sumres.add(F.QQ(kk1, den));
      } else {
        sumres = sumres.add(F.CN1.power(F.ZZ(kk)).multiply(F.QQ(kk1, den)));
      }
    }
    IInteger exponent = aa.add(bb).add(cc).add(dd).numerator();
    return F.Times(prefac, sumres, F.CN1.power(exponent));
  }

  public static IExpr wigner6j(IReal j1Num, IReal j2Num, IReal j3Num, IReal j4Num, IReal j5Num,
      IReal j6Num) throws TriangularException {
    IRational j1 = intOrHalfInt(j1Num);
    IRational j2 = intOrHalfInt(j2Num);
    IRational j3 = intOrHalfInt(j3Num);
    IRational j4 = intOrHalfInt(j4Num);
    IRational j5 = intOrHalfInt(j5Num);
    IRational j6 = intOrHalfInt(j6Num);
    IRational exponent = j1.add(j2).add(j4).add(j5);
    return F.Times(F.CN1.power(exponent), racah(j1, j2, j5, j4, j3, j6));
  }

  private static IExpr bigDeltaCoeff(IRational aa, IRational bb, IRational cc)
      throws TriangularException {
    IRational v1 = intOrHalfInt(aa.add(bb).subtractFrom(cc));
    IRational v2 = intOrHalfInt(aa.add(cc).subtractFrom(bb));
    IRational v3 = intOrHalfInt(bb.add(cc).subtractFrom(aa));
    // if (!intValued(aa + bb - cc) || !intValued(aa + cc - bb) || !intValued(bb + cc - aa)) {
    // throw new IllegalArgumentException(
    // "j values must be integer or half integer and fulfill the triangle relation");
    // }
    // if ((aa + bb - cc) < 0 || (aa + cc - bb) < 0 || (bb + cc - aa) < 0) {
    // return 0;
    // }
    if (v1.isNegative() || v2.isNegative() || v3.isNegative()) {
      throw new Wigner.TriangularException(F.C0);
    }

    // int maxfact = (int) Math.max(Math.max(aa + bb - cc, aa + cc - bb),
    // Math.max(bb + cc - aa, aa + bb + cc + 1));
    // calcFactList(maxfact);

    // BigInteger argsqrt = factList.get((int) (aa + bb - cc))
    // .multiply(factList.get((int) (aa + cc - bb))).multiply(factList.get((int) (bb + cc - aa)))
    // .divide(factList.get((int) (aa + bb + cc + 1)));

    IInteger multiplyValue = NumberTheory.factorial(v1.numerator())//
        .multiply(NumberTheory.factorial(v2.numerator())) //
        .multiply(NumberTheory.factorial(v3.numerator()));
    IRational v4 = intOrHalfInt(aa.add(bb).add(cc).add(F.C1));
    IInteger divideValue = NumberTheory.factorial(v4.numerator());
    // .divide(NumberTheory.factorial((int) (aa + bb + cc + 1)));
    IExpr ressqrt = F.Sqrt(multiplyValue.divide(divideValue));
    // Math.sqrt(multiplyValue.doubleValue() / divideValue.doubleValue());
    // if (prec != null) {
    // ressqrt = Math.round(ressqrt * Math.pow(10, prec)) / Math.pow(10, prec);
    // }
    return ressqrt;
  }

  public static IExpr clebschGordan(IExpr j1, IExpr j2, IExpr j3, IExpr m1, IExpr m2, IExpr m3) {
    IExpr m3Negate = m3.negate();
    IExpr threeJSymbol = threeJSymbol(j1, j2, j3, m1, m2, m3Negate, //
        S.ClebschGordan, F.ThreeJSymbol(F.List(j1, m1), F.List(j2, m2), F.List(j3, m3Negate)));
    if (threeJSymbol.isPresent()) {
      // res = (-1) ** sympify(j_1 - j_2 + m_3) * sqrt(2 * j_3 + 1) * \
      return F.Times(F.Power(F.CN1, F.Plus(j1, F.Negate(j2), m3)),
          F.Sqrt(F.Plus(F.Times(F.C2, j3), F.C1)), threeJSymbol);
    }
    return F.NIL;
  }

  public static IExpr threeJSymbol(IExpr j1, IExpr j2, IExpr j3, IExpr m1, IExpr m2, IExpr m3,
      final IBuiltInSymbol head, final IAST ast) {
    // https://functions.wolfram.com/HypergeometricFunctions/ThreeJSymbol/02/
    if (j1.isRational() && j2.isRational() && j3.isRational() && m1.isRational() && m2.isRational()
        && m3.isRational()) {
      try {
        IExpr wigner3j = Wigner.wigner3j((IRational) j1, (IRational) j2, (IRational) j3,
            (IRational) m1, (IRational) m2, (IRational) m3);
        return wigner3j;
      } catch (Wigner.NotPhysicalException wnph) {
        Errors.printMessage(S.ThreeJSymbol, "phy", F.List(ast));
        return wnph.getValue();
      } catch (Wigner.TriangularException wtex) {
        Errors.printMessage(S.ThreeJSymbol, "tri", F.List(ast));
        return wtex.getValue();
      }
    }
    if (j1.isReal() && j2.isReal() && j3.isReal() && m1.isReal() && m2.isReal() && m3.isReal()) {
      try {
        IExpr wigner3j =
            Wigner.wigner3j((IReal) j1, (IReal) j2, (IReal) j3, (IReal) m1, (IReal) m2, (IReal) m3);
        // return wigner3j;
        // double wigner3j =
        // Wigner.wigner3j(j1.evalf(), j2.evalf(), j3.evalf(), m1.evalf(), m2.evalf(), m3.evalf());
        return F.num(wigner3j.evalf());
      } catch (Wigner.NotPhysicalException wnph) {
        Errors.printMessage(S.ThreeJSymbol, "phy", F.List(ast));
        return wnph.getValue();
      } catch (Wigner.TriangularException wtex) {
        Errors.printMessage(S.ThreeJSymbol, "tri", F.List(ast));
        return wtex.getValue();
      }
    }
    return F.NIL;
  }

}
