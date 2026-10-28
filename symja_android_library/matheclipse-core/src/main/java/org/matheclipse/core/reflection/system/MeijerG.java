package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.WorkingPrecision;
import org.matheclipse.core.sympy.simplify.HyperExpand;
import org.matheclipse.external.apfloat.ApcomplexMeijerGMath;

/**
 * <pre>
 * MeijerG({{a1,...,an},{a(n+1),...,ap}}, {{b1,...,bm},{b(m+1),...,bq}}, z)
 * MeijerG({{a1,...,an},{a(n+1),...,ap}}, {{b1,...,bm},{b(m+1),...,bq}}, z, r)
 * </pre>
 *
 * <p>
 * The Meijer G-function and the generalized Meijer G-function, whose integral has
 * <code>z^(-s/r)</code> in place of <code>z^(-s)</code>.
 * </p>
 *
 * <p>
 * For a positive <code>r</code> the closed forms are those of <code>r == 1</code> in the variable
 * <code>w = z^(1/r)</code>, with every power <code>w^c</code> taken as <code>z^(c/r)</code>.
 * </p>
 */
public class MeijerG extends AbstractFunctionEvaluator implements IFunctionExpand {

  /**
   * The argument <code>w = z^(1/r)</code> of the closed forms, whose powers are taken as
   * <code>z^(c/r)</code>.
   */
  private static final class Argument {
    final IExpr z;
    final IExpr r;

    Argument(IExpr z, IExpr r) {
      this.z = z;
      this.r = r;
    }

    /** <code>w^c</code> */
    IExpr pow(IExpr c) {
      return r.isOne() ? F.Power(z, c) : F.Power(z, F.evalExpand(F.Divide(c, r)));
    }

    /** <code>w</code> */
    IExpr w() {
      return r.isOne() ? z : F.Power(z, F.Power(r, F.CN1));
    }

    /** <code>Sqrt(w)</code> */
    IExpr sqrt() {
      return r.isOne() ? F.Sqrt(z) : pow(F.C1D2);
    }

    /** <code>Log(w)</code> */
    IExpr log() {
      return r.isOne() ? F.Log(z) : F.Divide(F.Log(z), r);
    }

    boolean isZero() {
      return z.isZero();
    }

    /**
     * Whether a closed form holds for this <code>r</code>. A closed form in <code>w</code> takes the
     * principal values of the functions of <code>w = z^(1/r)</code>, which are those of the
     * G-function only while the argument of <code>w</code> stays in <code>(-Pi, Pi]</code>
     * (<code>r &gt;= 1</code>), or the argument of <code>Sqrt(w)</code> for the Bessel functions of
     * <code>2*Sqrt(w)</code> (<code>r &gt;= 1/2</code>). Closed forms of powers of <code>w</code> and
     * entire functions of them hold for every <code>r</code>.
     *
     * @param minimum the smallest <code>r</code> for which the closed form holds
     */
    boolean allows(IExpr minimum) {
      return r.isOne() || minimum.isZero() || r.greaterEqualThan(minimum).isTrue();
    }
  }

  /** The closed form holds for every positive <code>r</code>. */
  private static final IExpr ANY_R = F.C0;

  /** The closed form has Bessel functions of <code>2*Sqrt(w)</code>. */
  private static final IExpr HALF_R = F.C1D2;

  /** The closed form has a function with a branch cut in <code>w</code>. */
  private static final IExpr ONE_R = F.C1;

  /**
   * <code>k1</code> is an empty list and contains no arguments.
   *
   */
  private static IExpr k1ArgSize0(Argument z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m,
      int q, IAST ast, boolean automatic, EvalEngine engine) {
    if (p == m && q == 0) {
      // 0,N,N,0
      IExpr meijerg00n0 = meijerg0NN0(k2, l1, z);
      if (meijerg00n0.isPresent()) {
        return meijerg00n0;
      }
    }
    switch (p) {
      case 0:
        // 0,0
        if (q == 0) {
          // 0,0,N,0
          IExpr meijerg00n0 = meijerg00N0(l1, z, engine);
          if (meijerg00n0.isPresent()) {
            return meijerg00n0;
          }
        }
        switch (m) {
          case 0:
            switch (q) {
              case 0:
              case 1:
                // 0,0,0,{0,1}
                // `1` does not exist. Arguments are not consistent.
                return Errors.printMessage(S.MeijerG, "hdiv", F.List(ast), engine);
            }
            break;
          case 1:
            switch (q) {
              case 0:
              // 0,0,1,0
              {
                IExpr b1 = l1.arg1();
                return meijerg0010(b1, z);
              }
              case 1:
              // 0,0,1,1
              {
                IExpr b1 = l1.arg1();
                IExpr b2 = l2.arg1();
                return meijerg0011(b1, b2, z);
              }
            }
            break;
          case 2:
            switch (q) {
              case 0:
                // 0,0,2,0
                IExpr b1 = l1.arg1();
                IExpr b2 = l1.arg2();
                return meijerg0020(b1, b2, z);

            }
            break;
        }
        break;
      case 1:
        // 0,1
        IExpr a2 = k2.arg1();
        switch (m) {
          case 1: {
            // 0,1,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 1:
                // 0,1,1,1
                IExpr b2 = l2.arg1();
                return meijerg0111(a2, b1, b2, z);
            }
          }
            break;
          case 2: {
            // 0,1,2
            IExpr b1 = l1.arg1();
            IExpr b2 = l1.arg2();
            switch (q) {
              case 0:
                // 0,1,2,0
                return meijerg0120(a2, b1, b2, z, automatic, engine);
            }
          }
            break;
        }
        break;
    }
    return F.NIL;
  }

  /**
   * <code>k1</code> is a list with 1 argument
   *
   */
  private static IExpr k1ArgSize1(Argument z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m,
      int q, IAST ast, EvalEngine engine) {
    IExpr a1 = k1.arg1();
    switch (p) {
      case 0:
        // 1,0
        switch (m) {
          case 0:
            // 1,0,0
            switch (q) {
              case 0:
                // 1,0,0,0

                return meijerg1000(a1, z);
              case 1:
                // 1,0,0,1
                IExpr b2 = l2.arg1();
                return meijerg1001(a1, b2, z);
            }
            break;
          case 1: {
            // 1,0,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 0:
                // 1,0,1,0
                return meijerg1010(a1, b1, z);
              case 1:
                // 1,0,1,1
                IExpr b2 = l2.arg1();
                return meijerg1011(a1, b1, b2, z);
            }
          }
            break;
          case 2: {
            // 1,0,2
            IExpr b1 = l1.arg1();
            IExpr b2 = l1.arg2();
            switch (q) {
              case 0:
                // 1,0,2,0
                return meijerg1020(a1, b1, b2, z, engine);
            }
          }
            break;
          case 3: {
            // 1,0,3
            switch (q) {
              case 0:
                // 1,0,3,0
                return meijerg1030(a1, l1, z, engine);
            }
          }
            break;
        }
        break;
      case 1: {
        // 1,1
        IExpr a2 = k2.arg1();
        switch (m) {
          case 0:
            // 1,1,0
            switch (q) {
              case 0:
                // 1,1,0,0
                return meijerg1100(a1, a2, z);
              case 1:
                // 1,1,0,1
                IExpr b2 = l2.arg1();
                return meijerg1101(a1, a2, b2, z);
            }
            break;
          case 1: {
            // 1,1,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 0:
                // 1,1,1,0
                return meijerg1110(a1, a2, b1, z);
            }
          }
            break;
          case 2: {
            // 1,1,2
            IExpr b1 = l1.arg1();
            IExpr b2 = l1.arg2();
            switch (q) {
              case 0:
                // 1,1,2,0
                return meijerg1120(a1, a2, b1, b2, z, engine);
            }
          }
            break;
        }
      }
        break;
      case 2: {
        // 1,2
        IExpr a2 = k2.arg1();
        IExpr a3 = k2.arg2();
        switch (m) {
          case 2: {
            // 1,2,2
            IExpr b1 = l1.arg1();
            IExpr b2 = l1.arg2();
            switch (q) {
              case 0:
                // 1,2,2,0
                return meijerg1220(a1, a2, a3, b1, b2, z, engine);
            }
          }
            break;
        }
      }
        break;
    }
    return F.NIL;
  }

  /**
   * <code>k1</code> is a list with 2 arguments
   *
   */
  private static IExpr k1ArgSize2(Argument z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m,
      int q, IAST ast, EvalEngine engine) {
    switch (p) {
      case 0:
        // 2,0
        switch (m) {
          case 1:
            // 2,0,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 1:
                // 2,0,1,1
                IExpr b2 = l2.arg1();
                if (k1.forAll(x -> x.isOne()) //
                    && b1.isOne() //
                    && b2.isZero() //
                    && z.allows(ONE_R)) {
                  return F.Log(F.Plus(F.C1, z.w()));
                }
            }
            break;
        }
        break;
    }
    return F.NIL;
  }

  /**
   * https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/03/01/
   *
   * @param b1
   * @param z
   * @return
   */
  private static IExpr meijerg0010(IExpr b1, Argument z) {
    if (z.isZero() && b1.isZero()) {
      return F.C1;
    }
    // z^b1/E^z
    return F.Times(F.Power(F.Exp(z.w()), F.CN1), z.pow(b1));
  }

  private static IExpr meijerg0011(IExpr b1, IExpr b2, Argument z) {
    if (!z.allows(HALF_R)) {
      return F.NIL;
    }
    return
    // [$ z^(b1 + (1/2)*(-b1 + b2))*BesselJ(b1 - b2, 2*Sqrt(z)) $]
    F.Times(z.pow(F.Plus(b1, F.Times(F.C1D2, F.Plus(F.Negate(b1), b2)))),
        F.BesselJ(F.Subtract(b1, b2), F.Times(F.C2, z.sqrt()))); // $$;
  }

  private static IExpr meijerg0020(IExpr b1, IExpr b2, Argument z) {
    if (!z.allows(HALF_R)) {
      return F.NIL;
    }
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/01/0004/
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/01/0002/
    // 2*z^(1/2*(b1+b2))*BesselK(-b1+b2,2*Sqrt(z))
    IExpr exponent = F.evalExpand(F.Times(F.C1D2, F.Plus(b1, b2)));
    return F.Times(F.C2, z.pow(exponent),
        F.BesselK(F.Plus(F.Negate(b1), b2), F.Times(F.C2, z.sqrt())));
  }

  /**
   * See: <a href=
   * "https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/09/0001/">HypergeometricFunctions/MeijerG/03/01/01/09/0001/</a>
   *
   * <p>
   * The parameters <code>b, b+1/n, ..., b+(n-1)/n</code>, in any order.
   *
   * @param l1
   * @param z
   * @return
   */
  private static IExpr meijerg00N0(IAST l1, Argument z, EvalEngine engine) {
    int n = l1.argSize();
    if (n > 1) {
      // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/09/0001/
      for (int base = 1; base < l1.size(); base++) {
        IExpr b = l1.get(base);
        boolean[] found = new boolean[n];
        boolean matches = true;
        for (int i = 1; i < l1.size(); i++) {
          IExpr delta = engine.evaluate(F.Times(F.ZZ(n), F.Subtract(l1.get(i), b)));
          int k = delta.isInteger() ? delta.toIntDefault() : -1;
          if (k < 0 || k >= n || found[k]) {
            matches = false;
            break;
          }
          found[k] = true;
        }
        if (matches) {
          IExpr m = F.ZZ(n);
          IExpr halfM1 = F.QQ(n - 1, 2);
          // (2^((m-1)/2)*Pi^((m-1)/2)*z^b)/(E^(m*z^(1/m))*Sqrt(m))
          return F.Times(F.Power(F.Times(F.Exp(F.Times(m, z.pow(F.QQ(1, n)))), F.Sqrt(m)), F.CN1),
              F.Power(F.C2, halfM1), F.Power(S.Pi, halfM1), z.pow(b));
        }
      }
    }
    return F.NIL;
  }

  /**
   * <code>G(z | ; a,...,a; a-1,...,a-1; )</code>, the residue at the pole of order <code>m</code>.
   */
  private static IExpr meijerg0NN0(IAST k2, IAST l1, Argument z) {
    int n = l1.argSize();
    if (n == k2.argSize() && n > 1) {
      // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/08/0001/
      IExpr a = k2.arg1();
      IExpr b = l1.arg1();
      if (!b.subtract(a).subtract(F.CN1).isPossibleZero(true)) {
        return F.NIL;
      }
      for (int i = 2; i <= n; i++) {
        if (!k2.get(i).subtract(a).isPossibleZero(true)) {
          return F.NIL;
        }
        if (!l1.get(i).subtract(b).isPossibleZero(true)) {
          return F.NIL;
        }
      }

      IExpr m = F.ZZ(n);
      // 1/((-1)^(1-m)*z^(1-a)*(-1+m)!*Log(z)^(1-m))
      IExpr v1 = F.Plus(F.CN1, m);

      IAST fz = F.Times(F.Power(-1, v1), z.pow(F.Plus(F.CN1, a)), F.Power(F.Factorial(v1), F.CN1),
          F.Power(z.log(), v1));
      // Piecewise({{fz,Abs(z)<=1}},0)
      return F.Piecewise(F.list(F.list(fz, F.LessEqual(F.Abs(z.z), F.C1))), F.C0);
    }
    return F.NIL;
  }

  private static IExpr meijerg0111(IExpr a2, IExpr b1, IExpr b2, Argument z) {
    if (z.isZero() && b1.isZero()) {
      // 1/(Gamma(a2)*Gamma(1-b2))
      return F.Power(F.Times(F.Gamma(a2), F.Gamma(F.Subtract(F.C1, b2))), F.CN1);
    }
    return
    // [$ (z^b1*Hypergeometric1F1Regularized(1 - a2 + b1, 1 + b1 - b2,
    // z))/Gamma(a2
    // - b1) $]
    F.Times(z.pow(b1), F.Power(F.Gamma(F.Subtract(a2, b1)), F.CN1),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a2), b1),
            F.Plus(F.C1, b1, F.Negate(b2)), z.w())); // $$;
  }

  private static IExpr meijerg0120(IExpr a2, IExpr b1, IExpr b2, Argument z, boolean automatic,
      EvalEngine engine) {
    IExpr v4 = b2.negate();
    IExpr v3 = b1.negate();
    IExpr csc1 = engine.evaluate(F.Csc(F.Times(F.Pi, F.Plus(b2, v3))));
    if (csc1.isSpecialsFree()) {
      IExpr csc2 = engine.evaluate(F.Csc(F.Times(F.Pi, F.Plus(b1, v4))));
      if (csc2.isSpecialsFree()) {
        // Pi*((z^b1*Csc((-b1+b2)*Pi)*Hypergeometric1F1Regularized(1-a2+b1,1+b1-b2,-z))/Gamma(a2-b1)+(z^b2*Csc((b1-b2)*Pi)*Hypergeometric1F1Regularized(1-a2+b2,1-b1+b2,-z))/Gamma(a2-b2))

        IExpr v2 = z.w().negate();
        IExpr v1 = F.Subtract(F.C1, a2);
        IAST gamma1 = F.Gamma(F.Plus(a2, v3));
        if (gamma1.isSpecialsFree() && !gamma1.isZero()) {
          IAST gamma2 = F.Gamma(F.Plus(a2, v4));
          if (gamma2.isSpecialsFree() && !gamma2.isZero()) {
            IAST hg1f1Regularized1 =
                F.Hypergeometric1F1Regularized(F.Plus(b1, v1), F.Plus(F.C1, b1, v4), v2);
            IAST hg1f1Regularized2 =
                F.Hypergeometric1F1Regularized(F.Plus(b2, v1), F.Plus(F.C1, b2, v3), v2);
            IExpr result = F.Times(F.Pi,
                F.Plus(F.Times(z.pow(b1), csc1, F.Power(gamma1, F.CN1), hg1f1Regularized1),
                    F.Times(z.pow(b2), csc2, F.Power(gamma2, F.CN1), hg1f1Regularized2)));
            return engine.evaluate(result);
          }
        }
      }
      return F.NIL;
    }
    if (automatic || !z.allows(ONE_R)) {
      // b1-b2 is an integer: the poles of the integrand are of second order
      return F.NIL;
    }
    // b1-b2 is an integer: z^b1*HypergeometricU(a2-b2,1+b1-b2,z)/E^z
    return F.Times(F.Exp(F.Negate(z.w())), z.pow(b1),
        F.HypergeometricU(F.Subtract(a2, b2), F.Plus(F.C1, b1, v4), z.w()));
  }

  private static IExpr meijerg1000(IExpr a1, Argument z) {
    if (z.isZero()) {
      if (a1.isZero()) {
        // -Infinity
        return F.CNInfinity;
      }
      if (a1.isOne()) {
        // Infinity
        return F.CInfinity;
      }
    }
    return
    // [$ z^(-1 + a1)/E^z^(-1) $]
    F.Times(F.Power(F.Exp(z.pow(F.CN1)), F.CN1), z.pow(F.Plus(F.CN1, a1))); // $$;
  }

  private static IExpr meijerg1001(IExpr a1, IExpr b2, Argument z) {
    if (z.z.isPositive()) {
      return
      // [$ (z^b2/Gamma(a1 - b2))*(z - 1)^(a1 - b2 - 1)*UnitStep(z - 1) $]
      F.Times(z.pow(b2), F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
          F.Power(F.Plus(F.CN1, z.w()), F.Plus(F.CN1, a1, F.Negate(b2))),
          F.UnitStep(F.Plus(F.CN1, z.w()))); // $$;
    }
    return F.NIL;
  }

  private static IExpr meijerg1010(IExpr a1, IExpr b1, Argument z) {
    if (!z.allows(ONE_R)) {
      return F.NIL;
    }
    if (z.isZero() && b1.isZero()) {
      // Gamma(1-a1)
      return F.Gamma(F.Subtract(F.C1, a1));
    }
    // 1,0,1,0
    // (z^b1*Gamma(1-a1+b1))/(1+z)^(1-a1+b1)
    return F.Times(z.pow(b1), F.Power(F.Plus(F.C1, z.w()), F.Plus(F.CN1, a1, F.Negate(b1))),
        F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
  }

  private static IExpr meijerg1011(IExpr a1, IExpr b1, IExpr b2, Argument z) {
    if (z.isZero() && b1.isZero()) {
      // Gamma(1-a1)/Gamma(1-b2)
      return F.Times(F.Gamma(F.Subtract(F.C1, a1)), F.Power(F.Gamma(F.Subtract(F.C1, b2)), F.CN1));
    }
    return
    // [$ z^b1*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1 + b1,
    // 1 +
    // b1 - b2, -z) $]
    F.Times(z.pow(b1), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)), F.Hypergeometric1F1Regularized(
        F.Plus(F.C1, F.Negate(a1), b1), F.Plus(F.C1, b1, F.Negate(b2)), F.Negate(z.w()))); // $$;
  }

  /**
   * The evaluated factors <code>Gamma(1-a1+b1)</code> and <code>Gamma(1-a1+b2)</code>.
   *
   * @return <code>null</code> if one of them is not free of special values like
   *         <code>ComplexInfinity</code>
   */
  private static IExpr[] gammaFactors(IExpr a1, IExpr b1, IExpr b2, EvalEngine engine) {
    IExpr gamma1 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
    if (!gamma1.isSpecialsFree()) {
      return null;
    }
    IExpr gamma2 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b2)));
    if (!gamma2.isSpecialsFree()) {
      return null;
    }
    return new IExpr[] {gamma1, gamma2};
  }

  private static IExpr meijerg1020(IExpr a1, IExpr b1, IExpr b2, Argument z, EvalEngine engine) {
    if (!z.allows(ONE_R)) {
      return F.NIL;
    }
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/12/0002/
    // z^b1*Gamma(1-a1+b1)*Gamma(1-a1+b2)*HypergeometricU(1-a1+b1,1+b1-b2,z)
    IExpr[] gammas = gammaFactors(a1, b1, b2, engine);
    if (gammas == null) {
      return F.NIL;
    }
    return F.Times(z.pow(b1), gammas[0], gammas[1], F.HypergeometricU(
        F.Plus(F.C1, F.Negate(a1), b1), F.Plus(F.C1, b1, F.Negate(b2)), z.w()));
  }

  /**
   * <code>G(z | a; ; a-1/2, a, a; )</code>, with the three lower parameters in any order.
   */
  private static IExpr meijerg1030(IExpr a1, IAST l1, Argument z, EvalEngine engine) {
    if (!z.allows(HALF_R)) {
      return F.NIL;
    }
    IExpr a1Half2 = F.CN1D2.plus(a1);
    int half = -1;
    int equal = 0;
    for (int i = 1; i < l1.size(); i++) {
      IExpr b = l1.get(i);
      if (b.equals(a1)) {
        equal++;
      } else if (half < 0 && a1Half2.subtract(b).isPossibleZero(true)) {
        half = i;
      }
    }
    if (equal == 2 && half > 0) {
      // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/05/10/
      // -2*Sqrt(Pi)
      final IExpr factor1 = F.Times(F.CN2, F.CSqrtPi);
      final IExpr factor2 = a1Half2.isPossibleZero(true) //
          ? F.C1 //
          : z.pow(F.Plus(F.CN1D2, a1));
      final IExpr sqrt2 = F.Times(F.C2, z.sqrt());
      // CosIntegral(2*Sqrt(z))*Sin(2*Sqrt(z))+Cos(2*Sqrt(z))*(Pi/2-SinIntegral(2*Sqrt(z)))
      final IExpr factor3 = F.Plus(F.Times(F.CosIntegral(sqrt2), F.Sin(sqrt2)),
          F.Times(F.Cos(sqrt2), F.Subtract(F.CPiHalf, F.SinIntegral(sqrt2))));
      return F.Times(factor1, factor2, factor3);
    }
    return F.NIL;
  }

  private static IExpr meijerg1100(IExpr a1, IExpr a2, Argument z) {
    if (!z.allows(HALF_R)) {
      return F.NIL;
    }
    return
    // [$ z^(-1 + a1 + (1/2)*(-a1 + a2))*BesselJ(-a1 + a2, 2/Sqrt(z)) $]
    F.Times(z.pow(F.Plus(F.CN1, a1, F.Times(F.C1D2, F.Plus(F.Negate(a1), a2)))),
        F.BesselJ(F.Plus(F.Negate(a1), a2), F.Times(F.C2, z.pow(F.CN1D2)))); // $$;
  }

  private static IExpr meijerg1101(IExpr a1, IExpr a2, IExpr b2, Argument z) {
    if (z.isZero() && a1.isOne()) {
      // Hypergeometric1F1Regularized(b2,a2,ComplexInfinity)/Gamma(1-b2)
      return F.Times(F.Power(F.Gamma(F.Subtract(F.C1, b2)), F.CN1),
          F.Hypergeometric1F1Regularized(b2, a2, F.CComplexInfinity));
    }
    return
    // [$ (z^(-1 + a1)*Hypergeometric1F1Regularized(1 - a1 + b2, 1 - a1 +
    // a2,
    // 1/z))/Gamma(a1 - b2) $]
    F.Times(z.pow(F.Plus(F.CN1, a1)), F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b2),
            F.Plus(F.C1, F.Negate(a1), a2), z.pow(F.CN1))); // $$;
  }

  private static IExpr meijerg1110(IExpr a1, IExpr a2, IExpr b1, Argument z) {
    if (z.isZero() && a1.isOne()) {
      // Gamma(b1)*Hypergeometric1F1Regularized(b1,a2,ComplexInfinity)
      return F.Times(F.Gamma(b1), F.Hypergeometric1F1Regularized(b1, a2, F.CComplexInfinity));
    }
    return
    // [$ z^(-1 + a1)*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1
    // + b1, 1
    // - a1 + a2, -(1/z)) $]
    F.Times(z.pow(F.Plus(F.CN1, a1)), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
            F.Plus(F.C1, F.Negate(a1), a2), F.Negate(z.pow(F.CN1)))); // $$;
  }

  private static IExpr meijerg1120(IExpr a1, IExpr a2, IExpr b1, IExpr b2, Argument z,
      EvalEngine engine) {
    if (!z.allows(ONE_R)) {
      return F.NIL;
    }
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/14/0001/
    // (Gamma(1-a1+b1)*Gamma(1-a1+b2)*Hypergeometric2F1Regularized(1-a1+b1,1-a1+b2,1-a1+a2,-1/z))/z^(1-a1)
    IExpr[] gammas = gammaFactors(a1, b1, b2, engine);
    if (gammas == null) {
      return F.NIL;
    }
    return F.Times(z.pow(F.Plus(F.CN1, a1)), gammas[0], gammas[1],
        F.Hypergeometric2F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
            F.Plus(F.C1, F.Negate(a1), b2), F.Plus(F.C1, F.Negate(a1), a2),
            F.Negate(z.pow(F.CN1))));
  }

  private static IExpr meijerg1220(IExpr a1, IExpr a2, IExpr a3, IExpr b1, IExpr b2, Argument z,
      EvalEngine engine) {
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/17/0001/
    // (Gamma(1-a1+b1)*Gamma(1-a1+b2)*HypergeometricPFQRegularized({1-a1+b1,1-a1+b2},{1-a1+a2,1-a1+a3},-1/z))/z^(1-a1)
    IExpr v2 = F.Subtract(F.C1, a1);
    IExpr v1 = F.Plus(v2, b1);
    IExpr[] gammas = gammaFactors(a1, b1, b2, engine);
    if (gammas == null) {
      return F.NIL;
    }
    return F.Times(z.pow(F.Plus(F.CN1, a1)), gammas[0], gammas[1],
        F.HypergeometricPFQRegularized(F.list(v1, F.Plus(b2, v2)),
            F.list(F.Plus(a2, v2), F.Plus(a3, v2)), F.Negate(z.pow(F.CN1))));
  }

  public MeijerG() {}

  /**
   * Whether the first two arguments are the four parameter lists
   * <code>{{an},{ap}}, {{bm},{bq}}</code>.
   */
  private static boolean isParameterLists(IExpr arg1, IExpr arg2) {
    return arg1.isList2() && arg1.first().isList() && arg1.second().isList() && arg2.isList2()
        && arg2.first().isList() && arg2.second().isList();
  }

  /**
   * The scale <code>r</code>, <code>1</code> without a fourth argument; {@link F#NIL} if it isn't a
   * positive number.
   */
  private static IExpr scale(IAST ast) {
    if (ast.argSize() < 4) {
      return F.C1;
    }
    IExpr r = ast.arg4();
    if (r.isNumber() && r.isPositive()) {
      return r;
    }
    return F.NIL;
  }

  /**
   * The function with each of the four parameter lists in canonical order, or {@link F#NIL} if they
   * are already.
   */
  private static IAST sortParameters(IAST ast) {
    IAST aList = (IAST) ast.arg1();
    IAST bList = (IAST) ast.arg2();
    IAST[] lists = {(IAST) aList.arg1(), (IAST) aList.arg2(), (IAST) bList.arg1(),
        (IAST) bList.arg2()};
    boolean changed = false;
    for (int i = 0; i < lists.length; i++) {
      if (lists[i].argSize() > 1) {
        IASTMutable copy = lists[i].copy();
        EvalAttributes.sort(copy);
        if (!copy.equals(lists[i])) {
          lists[i] = copy;
          changed = true;
        }
      }
    }
    if (!changed) {
      return F.NIL;
    }
    IASTMutable result = ast.copy();
    result.set(1, F.list(lists[0], lists[1]));
    result.set(2, F.list(lists[2], lists[3]));
    return result;
  }

  /**
   * Cancel a parameter of <code>an</code> against an equal one of <code>bq</code>, and one of
   * <code>bm</code> against an equal one of <code>ap</code>: their gamma functions cancel in the
   * integral. {@link F#NIL} if there are none, or if no parameter of <code>an</code> and
   * <code>bm</code> would remain.
   */
  private static IAST cancelEqualParameters(IAST ast, IAST an, IAST ap, IAST bm, IAST bq,
      EvalEngine engine) {
    IASTAppendable newAn = F.ListAlloc(an.argSize());
    IASTAppendable newAp = F.ListAlloc(ap.argSize());
    IASTAppendable newBm = F.ListAlloc(bm.argSize());
    IASTAppendable newBq = F.ListAlloc(bq.argSize());
    newAp.appendArgs(ap);
    newBq.appendArgs(bq);
    boolean changed = false;
    for (int i = 1; i < an.size(); i++) {
      IExpr a = an.get(i);
      int j = indexOfEqual(newBq, a, engine);
      if (j > 0) {
        newBq.remove(j);
        changed = true;
      } else {
        newAn.append(a);
      }
    }
    for (int i = 1; i < bm.size(); i++) {
      IExpr b = bm.get(i);
      int j = indexOfEqual(newAp, b, engine);
      if (j > 0) {
        newAp.remove(j);
        changed = true;
      } else {
        newBm.append(b);
      }
    }
    if (!changed || newAn.argSize() + newBm.argSize() == 0) {
      return F.NIL;
    }
    IASTMutable result = ast.copy();
    result.set(1, F.list(newAn, newAp));
    result.set(2, F.list(newBm, newBq));
    return result;
  }

  /** The position of an element of <code>list</code> which is equal to <code>x</code>, or -1. */
  private static int indexOfEqual(IAST list, IExpr x, EvalEngine engine) {
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).equals(x) || HyperExpand.difference(list.get(i), x, engine).isZero()) {
        return i;
      }
    }
    return -1;
  }

  @Override
  // CPD-OFF shared entry prologue of evaluate and functionExpand
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 3 || ast.argSize() == 4) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr z = ast.arg3();
      if (z.isList()) {
        return z.mapThread(ast.setAtCopy(3, F.Slot1), 3);
      }
      if (!isParameterLists(arg1, arg2)) {
        return F.NIL;
      }
      // CPD-ON
      IAST sorted = sortParameters(ast);
      if (sorted.isPresent()) {
        return sorted;
      }
      IAST list1 = (IAST) arg1;
      IAST list2 = (IAST) arg2;
      IAST k1 = (IAST) list1.arg1();
      IAST k2 = (IAST) list1.arg2();
      IAST l1 = (IAST) list2.arg1();
      IAST l2 = (IAST) list2.arg2();
      IAST reduced = cancelEqualParameters(ast, k1, k2, l1, l2, engine);
      if (reduced.isPresent()) {
        return reduced;
      }
      if (HyperExpand.isUndefined(k1, l1, engine)) {
        // `1` does not exist. Arguments are not consistent.
        return Errors.printMessage(S.MeijerG, "hdiv", F.List(ast), engine);
      }
      IExpr numeric = numericMeijerG(k1, k2, l1, l2, z, ast.argSize() == 4 ? ast.arg4() : F.C1,
          engine);
      if (numeric.isPresent()) {
        return numeric;
      }
      IExpr r = scale(ast);
      if (r.isNIL()) {
        return F.NIL;
      }
      Argument argument = new Argument(z, r);
      int n = k1.argSize();
      int p = k2.argSize();
      int m = l1.argSize();
      int q = l2.argSize();
      IExpr closedForm = F.NIL;
      switch (n) {
        case 0:
          closedForm = k1ArgSize0(argument, k1, k2, l1, l2, p, m, q, ast, true, engine);
          break;
        case 1:
          closedForm = k1ArgSize1(argument, k1, k2, l1, l2, p, m, q, ast, engine);
          break;
        case 2:
          closedForm = k1ArgSize2(argument, k1, k2, l1, l2, p, m, q, ast, engine);
          break;
      }
      if (closedForm.isPresent()) {
        return closedForm;
      }
      IExpr slater = HyperExpand.meijerGExpand(k1, k2, l1, l2, z, r, true, engine);
      if (slater.isPresent()) {
        return slater;
      }
      if (r.isOne() && (list1.isNumericArgument(true) || list2.isNumericArgument(true)
          || z.isNumericArgument(false))) {
        IExpr temp = functionExpandSpecialCases(argument, list1, list2, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
    }
    return F.NIL;
  }

  /**
   * The numeric value for numbers as parameters and argument, of which one is inexact or which are
   * evaluated numerically.
   */
  private static IExpr numericMeijerG(IAST an, IAST ap, IAST bm, IAST bq, IExpr z, IExpr r,
      EvalEngine engine) {
    if (!z.isNumber() || !r.isNumber() || !an.forAll(x -> x.isNumber())
        || !ap.forAll(x -> x.isNumber()) || !bm.forAll(x -> x.isNumber())
        || !bq.forAll(x -> x.isNumber())) {
      return F.NIL;
    }
    final int n = an.argSize();
    final int p = n + ap.argSize();
    final int m = bm.argSize();
    final int q = m + bq.argSize();
    IExpr[] numbers = new IExpr[p + q + 2];
    boolean real = true;
    for (int i = 0; i < n; i++) {
      numbers[i] = an.get(i + 1);
    }
    for (int i = n; i < p; i++) {
      numbers[i] = ap.get(i - n + 1);
    }
    for (int i = 0; i < m; i++) {
      numbers[p + i] = bm.get(i + 1);
    }
    for (int i = m; i < q; i++) {
      numbers[p + i] = bq.get(i - m + 1);
    }
    numbers[p + q] = z;
    numbers[p + q + 1] = r;
    for (IExpr x : numbers) {
      real &= x.isReal();
    }
    final boolean realResult = real && z.isPositive();
    return WorkingPrecision.evaluate(numbers, engine, realResult, (arguments, precision) -> {
      Apcomplex[] a1 = new Apcomplex[n];
      Apcomplex[] a2 = new Apcomplex[p - n];
      Apcomplex[] b1 = new Apcomplex[m];
      Apcomplex[] b2 = new Apcomplex[q - m];
      System.arraycopy(arguments, 0, a1, 0, n);
      System.arraycopy(arguments, n, a2, 0, p - n);
      System.arraycopy(arguments, p, b1, 0, m);
      System.arraycopy(arguments, p + m, b2, 0, q - m);
      return ApcomplexMeijerGMath.meijerG(a1, a2, b1, b2, arguments[p + q], arguments[p + q + 1],
          precision);
    });
  }

  @Override
  // CPD-OFF shared entry prologue of evaluate and functionExpand
  public IExpr functionExpand(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 3 || ast.argSize() == 4) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr z = ast.arg3();
      if (z.isList()) {
        return z.mapThread(ast.setAtCopy(3, F.Slot1), 3);
      }
      if (!isParameterLists(arg1, arg2)) {
        return F.NIL;
      }
      // CPD-ON
      IExpr r = scale(ast);
      if (r.isNIL()) {
        return F.NIL;
      }
      IAST aList = (IAST) arg1;
      IAST bList = (IAST) arg2;
      if (HyperExpand.isUndefined((IAST) aList.arg1(), (IAST) bList.arg1(), engine)) {
        return F.NIL;
      }
      Argument argument = new Argument(z, r);
      IExpr temp = functionExpandSpecialCases(argument, aList, bList, engine);
      if (temp.isPresent()) {
        return temp;
      }
      IAST k1 = (IAST) aList.arg1();
      IAST k2 = (IAST) aList.arg2();
      IAST l1 = (IAST) bList.arg1();
      IAST l2 = (IAST) bList.arg2();
      if (k1.isEmpty()) {
        temp = k1ArgSize0(argument, k1, k2, l1, l2, k2.argSize(), l1.argSize(), l2.argSize(), ast,
            false, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      return HyperExpand.meijerGExpand((IAST) aList.arg1(), (IAST) aList.arg2(),
          (IAST) bList.arg1(), (IAST) bList.arg2(), z, r, false, engine);
    }
    return F.NIL;
  }

  private static IExpr functionExpandSpecialCases(Argument argument, IAST aList, IAST bList,
      EvalEngine engine) {
    IExpr z = argument.z;
    if (aList.arg1().isList1() && aList.arg2().isList1() && //
        bList.arg1().isList1() && bList.arg2().isList1() && argument.allows(ONE_R)) {
      // 1,1,1,1
      IExpr a1 = aList.arg1().first();
      IExpr a2 = aList.arg2().first();
      IExpr b1 = bList.arg1().first();
      IExpr b2 = bList.arg2().first();
      // Piecewise({{ComplexInfinity,z==1&&Re(-a1-a2+b1+b2)>-1},{(z^b1*Gamma(1-a1+b1)*Hypergeometric2F1(
      // 1-a1+b1,1-a2+b1,1+b1-b2,z))/(Gamma(a2-b1)*Gamma(1+b1-b2)),(z==1&&Re(-a1-a2+b1+b2)<-
      // 1)||Abs(z)<1},{(Gamma(1-a1+b1)*Hypergeometric2F1(1-a1+b1,1-a1+b2,1-a1+a2,1/z))/(z^(
      // 1-a1)*Gamma(1-a1+a2)*Gamma(a1-b2)),Abs(z)>1}},Indeterminate)
      IExpr v7 = F.Abs(z);
      IExpr v6 = F.Negate(b2);
      IExpr v5 = F.Negate(a2);
      IExpr v4 = F.Equal(z, F.C1);
      IExpr v3 = F.Subtract(F.C1, a1);
      IExpr v2 = F.Plus(F.C1, b1, v6);
      IExpr v1 = F.Re(F.Plus(F.Negate(a1), v5, b1, b2));
      return F.Piecewise(F.list(F.list(F.CComplexInfinity, F.And(v4, F.Greater(v1, F.CN1))),
          F.list(
              F.Times(argument.pow(b1), F.Power(F.Gamma(F.Subtract(a2, b1)), F.CN1),
                  F.Power(F.Gamma(v2), F.CN1), F.Gamma(F.Plus(b1, v3)),
                  F.Hypergeometric2F1(F.Plus(b1, v3), F.Plus(F.C1, b1, v5), v2, argument.w())),
              F.Or(F.And(v4, F.Less(v1, F.CN1)), F.Less(v7, F.C1))),
          F.list(F.Times(argument.pow(F.Plus(F.CN1, a1)), F.Power(F.Gamma(F.Plus(a2, v3)), F.CN1),
              F.Gamma(F.Plus(b1, v3)), F.Power(F.Gamma(F.Plus(a1, v6)), F.CN1),
              F.Hypergeometric2F1(F.Plus(b1, v3), F.Plus(b2, v3), F.Plus(a2, v3),
                  argument.pow(F.CN1))),
              F.Greater(v7, F.C1))),
          F.Indeterminate);
    }

    if (aList.arg1().isEmptyList() && aList.arg2().isList1() && //
        bList.arg1().isList1() && bList.arg2().isEmptyList() && argument.allows(ONE_R)) {
      // 0,1,1,0
      IExpr a2 = aList.arg2().first();
      IExpr b1 = bList.arg1().first();
      // Piecewise({{z^b1/((1-z)^(1-a2+b1)*Gamma(a2-b1)),(z==1&&Re(-a2+b1)<-1)||Abs(z)<1},{
      // 0,Abs(z)>1}},Indeterminate)
      IExpr v2 = F.Abs(z);
      IExpr v1 = F.Negate(b1);
      return F.Piecewise(F.list(F.list(
          F.Times(F.Power(F.Subtract(F.C1, argument.w()), F.Plus(F.CN1, a2, v1)),
              argument.pow(b1), F.Power(F.Gamma(F.Plus(a2, v1)), F.CN1)),
          F.Or(F.And(F.Equal(z, F.C1), F.Less(F.Plus(F.Negate(F.Re(a2)), F.Re(b1)), F.CN1)),
              F.Less(v2, F.C1))),
          F.list(F.C0, F.Greater(v2, F.C1))), F.Indeterminate);
    }

    if (aList.arg1().isList2() && aList.arg2().isEmptyList() && //
        bList.arg1().isList1() && bList.arg2().isEmptyList()) {
      // 2,0,1,0
      IExpr a11 = aList.arg1().first();
      IExpr a12 = aList.arg1().second();
      IExpr b11 = bList.arg1().first();
      if (a11.isOne() && a12.isNumEqualInteger(F.C2) && b11.isNumEqualInteger(F.C3)
          && z.isOne()) {
        // 2+3*E*ExpIntegralEi(-1)
        return F.Plus(F.C2, F.Times(F.C3, S.E, F.ExpIntegralEi(F.CN1)));
      }
      if (a11.isOne()) {
        // (Pi*Csc((1-a12)*Pi)*Gamma(b11)*Hypergeometric1F1(b11,a12,1/z))/Gamma(a12)+(Pi*Csc((-1+a12)*Pi)*Gamma(1-a12+b11)*Hypergeometric1F1(1-a12+b11,2-a12,1/z))/(z^(1-a12)*Gamma(2-a12))
        IExpr v5 = F.Plus(F.CN1, a12);
        IExpr v4 = argument.pow(F.CN1);
        IExpr v3 = F.Subtract(F.C1, a12);
        IExpr v2 = F.Subtract(F.C2, a12);
        IExpr v1 = F.Plus(v3, b11);
        return F.Plus(
            F.Times(F.Pi, F.Csc(F.Times(v3, F.Pi)), F.Power(F.Gamma(a12), F.CN1), F.Gamma(b11),
                F.Hypergeometric1F1(b11, a12, v4)),
            F.Times(F.Pi, argument.pow(v5), F.Csc(F.Times(v5, F.Pi)),
                F.Power(F.Gamma(v2), F.CN1), F.Gamma(v1), F.Hypergeometric1F1(v1, v2, v4)));

      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.NUMERICFUNCTION);
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
