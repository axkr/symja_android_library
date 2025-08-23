package org.matheclipse.core.sympy.physics;


import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;

public class Wigner {

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

    IInteger multiplyValue = Wigner.factorial(v1.numerator())//
        .multiply(Wigner.factorial(v2.numerator())) //
        .multiply(Wigner.factorial(v3.numerator()));
    IRational v4 = intOrHalfInt(aa.add(bb).add(cc).add(F.C1));
    IInteger divideValue = Wigner.factorial(v4.numerator());
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

  public static IRational[] createMijSequence(IRational j, int jDouble) {
    IRational[] mijSequence = new IRational[jDouble + 1];
    for (int i = 0; i < mijSequence.length; i++) {
      mijSequence[i] = j.subtract(i);
    }
    return mijSequence;
  }

  public static IRational intOrHalfInt(IReal value) throws TriangularException {
    if (value.isRational()) {
      IInteger denominator = ((IRational) value).denominator();
      if (denominator.equals(F.C1)) {
        return ((IRational) value).numerator();
      } else if (denominator.equals(F.C2)) {
        return (IRational) value;
      }
    }
    int intValue = value.toIntDefault();
    if (F.isPresent(intValue)) {
      return F.ZZ(intValue);
    }
    value = value.multiply(F.C2);
    intValue = value.toIntDefault();
    if (F.isPresent(intValue)) {
      return F.QQ(intValue, 2);
    }
    throw new Wigner.TriangularException(F.C0);
  }

  public static int jDouble(IRational J) {
    return J.multiply(F.C2).toIntDefault();
  }

  public static IExpr racah(IRational aa, IRational bb, IRational cc, IRational dd, IRational ee,
      IRational ff) throws TriangularException {
    IExpr prefac = F.Times(bigDeltaCoeff(aa, bb, ee), bigDeltaCoeff(cc, dd, ee),
        bigDeltaCoeff(aa, cc, ff), bigDeltaCoeff(bb, dd, ff));
    EvalEngine engine = EvalEngine.get();
    prefac = engine.evaluate(prefac);
    if (prefac.isPossibleZero(true, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return F.C0;
    }

    int imin = aa.add(bb).add(ee)
        .max(cc.add(dd).add(ee).max(aa.add(cc).add(ff).max(bb.add(dd).add(ff)))).toIntDefault();
    int imax = aa.add(bb).add(cc).add(dd)
        .min(aa.add(dd).add(ee).add(ff).min(bb.add(cc).add(ee).add(ff))).toIntDefault();

    if (F.isNotPresent(imin) || F.isNotPresent(imax)) {
      throw new ArgumentTypeException("error",
          F.List("Expecting non-negative machine integer, got " + imin + " or " + imax));
    }

    IExpr sumres = F.C0;
    for (int kk = imin; kk <= imax; kk++) {
      IInteger k1 = F.ZZ(kk).subtract(aa).subtract(bb).subtract(ee).numerator();
      IInteger k2 = F.ZZ(kk).subtract(cc).subtract(dd).subtract(ee).numerator();
      IInteger k3 = F.ZZ(kk).subtract(aa).subtract(cc).subtract(ff).numerator();
      IInteger k4 = F.ZZ(kk).subtract(bb).subtract(dd).subtract(ff).numerator();

      IInteger k5 = aa.add(bb).add(cc).add(dd).subtract(F.ZZ(kk)).numerator();
      IInteger k6 = aa.add(dd).add(ee).add(ff).subtract(F.ZZ(kk)).numerator();
      IInteger k7 = bb.add(cc).add(ee).add(ff).subtract(F.ZZ(kk)).numerator();
      IInteger den = Wigner.factorial(k1)//
          .multiply(Wigner.factorial(k2)) //
          .multiply(Wigner.factorial(k3))//
          .multiply(Wigner.factorial(k4)) //
          .multiply(Wigner.factorial(k5))//
          .multiply(Wigner.factorial(k6))//
          .multiply(Wigner.factorial(k7));

      IInteger kk1 = IInteger.factorial(kk + 1);
      if (kk == 0) {
        sumres = sumres.add(F.QQ(kk1, den));
      } else {
        sumres = sumres.add(F.CN1.power(F.ZZ(kk)).multiply(F.QQ(kk1, den)));
      }
    }
    IInteger exponent = aa.add(bb).add(cc).add(dd).numerator();
    return F.Times(prefac, sumres, F.CN1.power(exponent));
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
  // public static IExpr dotRotGradYnm(IExpr j, IExpr p, IExpr l, IExpr m, IExpr theta, IExpr phi) {
  // IExpr k = F.Dummy("k");
  //
  // IExpr alpha = (lExpr, mExpr, jExpr, pExpr, kExpr) -> F
  // .Sqrt(F.Divide(F.Times(F.Plus(F.Times(2, lExpr), 1), F.Plus(F.Times(2, jExpr), 1),
  // F.Plus(F.Times(2, kExpr), 1)), F.Times(4, S.Pi)))
  // .multiply(new Wigner3j().evaluate(F.List(jExpr, lExpr, kExpr, S.C0, S.C0, S.C0),
  // EvalEngine.get()))
  // .multiply(new Wigner3j().evaluate(
  // F.List(jExpr, lExpr, kExpr, pExpr, mExpr, F.Negate(F.Plus(mExpr, pExpr))),
  // EvalEngine.get()));
  //
  // IExpr sum = F.Sum(
  // F.Times(SphericalHarmonics.Ynm(k, F.Plus(m, p), theta, phi), alpha.apply(l, m, j, p, k),
  // F.Divide(F.C1, F.C2),
  // F.Plus(F.Times(k, k), F.Negate(F.Times(j, j)), F.Negate(F.Times(l, l)),
  // k, F.Negate(j), F.Negate(l))),
  // F.List(k, F.Abs(F.Negate(F.Plus(l, j))), F.Plus(l, j)));
  //
  // return F.Times(F.Power(F.Negate(F.Plus(m, p)), F.C1), sum);
  // }

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
    if (a3.isNegative()) {
      return F.C0;
    }
    if (m1.abs().isGT(j1) || m2.abs().isGT(j2) || m3.abs().isGT(j3)) {
      return F.C0;
    }

    IInteger multiplyValue = Wigner.factorial(j1.add(j2).subtract(j3).numerator()) //
        .multiply(Wigner.factorial(j1.subtract(j2).add(j3).numerator())) //
        .multiply(Wigner.factorial(j2.add(j3).subtract(j1).numerator())) //
        .multiply(Wigner.factorial(j1.subtract(m1).numerator())) //
        .multiply(Wigner.factorial(j1.add(m1).numerator())) //
        .multiply(Wigner.factorial(j2.subtract(m2).numerator())) //
        .multiply(Wigner.factorial(j2.add(m2).numerator())) //
        .multiply(Wigner.factorial(j3.subtract(m3).numerator())) //
        .multiply(Wigner.factorial(j3.add(m3).numerator()));//

    IRational divideValue = Wigner.factorial(j1.add(j2).add(j3).add(1).numerator());
    IExpr ressqrt = F.Sqrt(multiplyValue.divide(divideValue));

    int imin = j1.add(m2).subtract(j3).max(j2.subtract(m1).subtract(j3).max(F.C0)).toIntDefault();
    int imax = j2.add(m2).min(j1.subtract(m1).min(j1.add(j2).subtract(j3))).toIntDefault();
    if (F.isNotPresent(imin) || F.isNotPresent(imax)) {
      throw new ArgumentTypeException("error",
          F.List("Expecting non-negative machine integer, got " + imin + " or " + imax));
    }
    IExpr sumres = F.C0;
    for (int ii = imin; ii <= imax; ii++) {
      IInteger den = IInteger.factorial(ii)//
          .multiply(Wigner.factorial(F.ZZ(ii).add(j3.subtract(j1).subtract(m2).numerator()))) //
          .multiply(Wigner.factorial(j2.add(m2).subtract(F.ZZ(ii)).numerator())) //
          .multiply(Wigner.factorial(j1.subtract(m1).subtract(F.ZZ(ii)).numerator())) //
          .multiply(Wigner.factorial(F.ZZ(ii).add(j3).subtract(j2).add(m1).numerator())) //
          .multiply(Wigner.factorial(j1.add(j2).subtract(j3).subtract(F.ZZ(ii)).numerator()));
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

  /**
   * Return the Wigner D matrix for angular momentum J.
   * 
   * @param alpha real numbers representing the Euler angles of rotation about the so-called
   *        vertical, line of nodes, and figure axes
   * @param beta real numbers representing the Euler angles of rotation about the so-called
   *        vertical, line of nodes, and figure axes
   * @param gamma real numbers representing the Euler angles of rotation about the so-called
   *        vertical, line of nodes, and figure axes
   * @return the Wigner D matrix
   */
  public static FieldMatrix<IExpr> wignerD(IRational J, IExpr alpha, IExpr beta, IExpr gamma) {
    int JDouble = jDouble(J);
    IRational[] M = createMijSequence(J, JDouble);
    FieldMatrix<IExpr> d = wignerDSmall(M, J, beta, JDouble);

    IExpr[][] zeroM = new IExpr[JDouble + 1][JDouble + 1];
    for (int i = 0; i < zeroM.length; i++) {
      for (int j = 0; j < zeroM.length; j++) {
        zeroM[i][j] = F.C0;
      }
    }
    FieldMatrix<IExpr> D = new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, zeroM);

    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M.length; j++) {
        IExpr Mi = M[i];
        IExpr Mj = M[j];
        D.setEntry(i, j, F.Times(F.Exp(F.Times(S.I, Mi, alpha)), d.getEntry(i, j),
            F.Exp(F.Times(S.I, Mj, gamma))));
      }
    }

    return D;
  }

  /**
   * Return the small Wigner d matrix for angular momentum J.
   * 
   * @param J an integer, half-integer, or SymPy symbol for the total angular momentum of the
   *        angular momentum space being rotated
   * @param beta a real number representing the Euler angle of rotation about the so-called line of
   *        nodes
   * @return the small Wigner d matrix
   */
  public static FieldMatrix<IExpr> wignerDSmall(IRational J, IExpr beta) {
    int JDouble = jDouble(J);
    IRational[] mijSequence = createMijSequence(J, JDouble);
    return wignerDSmall(mijSequence, J, beta, JDouble);
  }

  private static FieldMatrix<IExpr> wignerDSmall(IRational[] mijSequence, IRational J, IExpr beta,
      int JDouble) {
    EvalEngine engine = EvalEngine.get();
    FieldMatrix<IExpr> d = new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, JDouble + 1, JDouble + 1);
    for (int i = 0; i < mijSequence.length; i++) {
      for (int j = 0; j < mijSequence.length; j++) {
        IExpr wignerDSmall =
            wignerDSmallEntry(J, mijSequence[i], mijSequence[j], beta, JDouble, engine);
        d.setEntry(i, j, wignerDSmall);
      }
    }
    return d;
  }

  public static IExpr wignerDEntry(IRational J, IRational mi, IRational mj, IExpr alpha, IExpr beta,
      IExpr gamma, EvalEngine engine) {
    IExpr entry = wignerDSmallEntry(J, mi, mj, beta, jDouble(J), engine);
    return engine
        .evaluate(F.Times(F.Exp(F.Times(S.I, mi, alpha)), entry, F.Exp(F.Times(S.I, mj, gamma))));
  }

  // public static IExpr wignerDEntry(IRational J, IRational mi, IRational mj, INumber alpha,
  // INumber beta, INumber gamma, EvalEngine engine) {
  // IExpr entry = wignerDSmallEntry(J, mi, mj, beta, jDouble(J), engine);
  // return engine
  // .evaluate(F.Times(F.Exp(F.Times(S.I, mi, alpha)), entry, F.Exp(F.Times(S.I, mj, gamma))));
  // }

  public static IExpr wignerDSmallEntry(IRational j, IRational mi, IRational mj, IExpr beta,
      int jDouble, EvalEngine engine) {
    IRational sigmamax = j.subtract(mi).min(j.subtract(mj));
    IRational sigmamin = F.C0.max(mi.add(mj).negate());
    final IExpr halfBeta = beta.isNumber() ? F.C1D2.times(beta) : F.Times(F.C1D2, beta);

    IExpr dij = S.Sqrt.of(engine, //
        F.Times( //
            j.add(mi).factorial(), //
            j.subtract(mi).factorial(), //
            F.Power(j.add(mj).factorial(), F.CN1), //
            F.Power(j.subtract(mj).factorial(), F.CN1)));

    IASTAppendable terms = F.PlusAlloc(16);
    IRational s = sigmamin;
    while (s.isLE(sigmamax)) {
      IRational r1 = j.subtract(mi).subtract(s);
      terms.append(F.Times(//
          F.CN1.power(r1), //
          F.Binomial(j.add(mj), r1), //
          F.Binomial(j.subtract(mj), s), //
          F.Power(F.Cos(halfBeta), //
              s.multiply(F.C2).add(mi).add(mj)), //
          F.Power(F.Sin(halfBeta), //
              F.Plus(F.ZZ(jDouble), s.multiply(F.CN2).subtract(mi).subtract(mj)))));
      s = s.inc();
    }
    return engine.evaluate(F.Times(dij, terms));
  }

  private static IInteger factorial(final IInteger x) {
    return x.factorial();
  }

  // public static IExpr wignerDSmallEntry(IRational j, IRational mi, IRational mj, INumber beta,
  // int jDouble, EvalEngine engine) {
  // IRational sigmamax = j.subtract(mi).min(j.subtract(mj));
  // IRational sigmamin = F.C0.max(mi.add(mj).negate());
  // final INumber halfBeta = F.C1D2.times(beta);
  //
  // IExpr dij = engine.evaluate( //
  // F.Sqrt( //
  // F.Times( //
  // F.Factorial(F.Plus(j, mi)), //
  // F.Factorial(F.Subtract(j, mi)), //
  // F.Power(F.Factorial(F.Plus(j, mj)), F.CN1), //
  // F.Power(F.Factorial(F.Subtract(j, mj)), F.CN1))));
  //
  // IASTAppendable terms = F.PlusAlloc(16);
  // IRational s = sigmamin;
  // while (s.isLE(sigmamax)) {
  // IRational r1 = j.subtract(mi).subtract(s);
  // terms.append(F.Times(//
  // F.CN1.power(r1), //
  // F.Binomial(j.add(mj), r1), //
  // F.Binomial(j.subtract(mj), s), //
  // F.Power(F.Cos(halfBeta), //
  // s.multiply(F.C2).add(mi).add(mj)), //
  // F.Power(F.Sin(halfBeta), //
  // F.Plus(F.ZZ(jDouble), s.multiply(F.CN2).subtract(mi).subtract(mj)))));
  // s = s.inc();
  // }
  // return engine.evaluate(F.Times(dij, terms));
  // }

}
