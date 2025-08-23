package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class MeijerG extends AbstractFunctionEvaluator implements IFunctionExpand {

  /**
   * <code>k1</code> is an empty list and contains no arguments.
   * 
   */
  private static IExpr k1ArgSize0(IExpr z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m, int q,
      IAST ast, EvalEngine engine) {
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
          IExpr meijerg00n0 = meijerg00N0(l1, z);
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
                return meijerg0120(a2, b1, b2, z, engine);
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
  private static IExpr k1ArgSize1(IExpr z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m, int q,
      IAST ast, EvalEngine engine) {
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
            IExpr b1 = l1.arg1();
            IExpr b2 = l1.arg2();
            IExpr b3 = l1.arg3();
            switch (q) {
              case 0:
                // 1,0,3,0
                return meijerg1030(a1, b1, b2, b3, z);
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
  private static IExpr k1ArgSize2(IExpr z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m, int q,
      IAST ast, EvalEngine engine) {
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
                    && b2.isZero()) {
                  return F.Log(F.Plus(F.C1, z));
                }
            }
            break;
        }
        break;
    }
    return F.NIL;
  }
  // @Override
  // public IAST getRuleAST() {
  // return RULES;
  // }

  /**
   * https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/03/01/
   * 
   * @param b1
   * @param z
   * @return
   */
  private static IExpr meijerg0010(IExpr b1, IExpr z) {
    if (z.isZero() && b1.isZero()) {
      return F.C1;
    }
    // z^b1/E^z
    return F.Times(F.Power(F.Exp(z), F.CN1), F.Power(z, b1));
  }

  private static IExpr meijerg0011(IExpr b1, IExpr b2, IExpr z) {
    return
    // [$ z^(b1 + (1/2)*(-b1 + b2))*BesselJ(b1 - b2, 2*Sqrt(z)) $]
    F.Times(F.Power(z, F.Plus(b1, F.Times(F.C1D2, F.Plus(F.Negate(b1), b2)))),
        F.BesselJ(F.Subtract(b1, b2), F.Times(F.C2, F.Sqrt(z)))); // $$;
  }

  private static IExpr meijerg0020(IExpr b1, IExpr b2, IExpr z) {
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/01/0004/
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/01/0002/
    // 2*z^(1/2*(b1+b2))*BesselK(-b1+b2,2*Sqrt(z))
    IExpr exponent = F.evalExpand(F.Times(F.C1D2, F.Plus(b1, b2)));
    return F.Times(F.C2, F.Power(z, exponent),
        F.BesselK(F.Plus(F.Negate(b1), b2), F.Times(F.C2, F.Sqrt(z))));
  }

  /**
   * See: <a href=
   * "https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/09/0001/">HypergeometricFunctions/MeijerG/03/01/01/09/0001/</a>
   * 
   * @param l1
   * @param z
   * @return
   */
  private static IExpr meijerg00N0(IAST l1, IExpr z) {
    int n = l1.argSize();
    if (n > 1) {
      // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/09/0001/
      IRational delta = F.C0;
      IExpr b = l1.arg1();
      for (int i = 2; i < l1.size(); i++) {
        delta = delta.add(F.QQ(1, n));
        if (!l1.get(i).subtract(delta).subtract(b).isPossibleZero(true)) {
          return F.NIL;
        }
      }
      IExpr m = F.ZZ(n);
      // ((2*Pi)^(1/2*(-1+m))*z^b)/(E^(m*z^(1/m))*Sqrt(m))
      return F.Times(
          F.Power(F.Times(F.Exp(F.Times(m, F.Power(z, F.Power(m, F.CN1)))), F.Sqrt(m)), F.CN1),
          F.Power(F.C2Pi, F.Times(F.C1D2, F.Plus(F.CN1, m))), F.Power(z, b));
    }
    return F.NIL;
  }

  private static IExpr meijerg0NN0(IAST k2, IAST l1, IExpr z) {
    int n = l1.argSize();
    if (n == k2.argSize() && n > 1) {
      // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/01/08/0001/
      IExpr a = k2.arg1();
      IExpr b = l1.arg1();
      if (!b.subtract(a).subtract(F.CN1).isPossibleZero(true)) {
        for (int i = 2; i <= n; i++) {
          if (!k2.get(i).subtract(a).isPossibleZero(true)) {
            return F.NIL;
          }
          if (!l1.get(i).subtract(b).isPossibleZero(true)) {
            return F.NIL;
          }
        }
      }

      IExpr m = F.ZZ(n);
      // 1/((-1)^(1-m)*z^(1-a)*(-1+m)!*Log(z)^(1-m))
      IExpr v1 = F.Plus(F.CN1, m);

      IAST fz = F.Times(F.Power(F.CN1, v1), F.Power(z, F.Plus(F.CN1, a)),
          F.Power(F.Factorial(v1), F.CN1), F.Power(F.Log(z), v1));
      // Piecewise({{fz,Abs(z)<=1}},0)
      return F.Piecewise(F.list(F.list(fz, F.LessEqual(F.Abs(z), F.C1))), F.C0);
    }
    return F.NIL;
  }

  private static IExpr meijerg0111(IExpr a2, IExpr b1, IExpr b2, IExpr z) {
    if (z.isZero() && b1.isZero()) {
      // 1/(Gamma(a2)*Gamma(1-b2))
      return F.Power(F.Times(F.Gamma(a2), F.Gamma(F.Subtract(F.C1, b2))), F.CN1);
    }
    return
    // [$ (z^b1*Hypergeometric1F1Regularized(1 - a2 + b1, 1 + b1 - b2,
    // z))/Gamma(a2
    // - b1) $]
    F.Times(F.Power(z, b1), F.Power(F.Gamma(F.Subtract(a2, b1)), F.CN1),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a2), b1),
            F.Plus(F.C1, b1, F.Negate(b2)), z)); // $$;
  }

  private static IExpr meijerg0120(IExpr a2, IExpr b1, IExpr b2, IExpr z, EvalEngine engine) {
    IExpr v4 = b2.negate();
    IExpr v3 = b1.negate();
    IExpr csc1 = engine.evaluate(F.Csc(F.Times(F.Pi, F.Plus(b2, v3))));
    if (csc1.isSpecialsFree()) {
      IExpr csc2 = engine.evaluate(F.Csc(F.Times(F.Pi, F.Plus(b1, v4))));
      if (csc2.isSpecialsFree()) {
        // Pi*((z^b1*Csc((-b1+b2)*Pi)*Hypergeometric1F1Regularized(1-a2+b1,1+b1-b2,-z))/Gamma(a2-b1)+(z^b2*Csc((b1-b2)*Pi)*Hypergeometric1F1Regularized(1-a2+b2,1-b1+b2,-z))/Gamma(a2-b2))

        IExpr v2 = z.negate();
        IExpr v1 = a2.dec();
        IAST gamma1 = F.Gamma(F.Plus(a2, v3));
        if (gamma1.isSpecialsFree() && !gamma1.isZero()) {
          IAST gamma2 = F.Gamma(F.Plus(a2, v4));
          if (gamma2.isSpecialsFree() && !gamma2.isZero()) {
            IAST hg1f1Regularized1 =
                F.Hypergeometric1F1Regularized(F.Plus(b1, v1), F.Plus(F.C1, b1, v4), v2);
            IAST hg1f1Regularized2 =
                F.Hypergeometric1F1Regularized(F.Plus(b2, v1), F.Plus(F.C1, b2, v3), v2);
            IExpr result = F.Times(F.Pi,
                F.Plus(F.Times(F.Power(z, b1), csc1, F.Power(gamma1, F.CN1), hg1f1Regularized1),
                    F.Times(F.Power(z, b2), csc2, F.Power(gamma2, F.CN1), hg1f1Regularized2)));
            return engine.evaluate(result);
          }
        }
      }
    }
    return F.NIL;
  }

  private static IExpr meijerg1000(IExpr a1, IExpr z) {
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
    F.Times(F.Power(F.Exp(F.Power(z, F.CN1)), F.CN1), F.Power(z, F.Plus(F.CN1, a1))); // $$;
  }

  private static IExpr meijerg1001(IExpr a1, IExpr b2, IExpr z) {
    if (z.isPositive()) {
      return
      // [$ (z^b2/Gamma(a1 - b2))*(z - 1)^(a1 - b2 - 1)*UnitStep(z - 1) $]
      F.Times(F.Power(z, b2), F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
          F.Power(F.Plus(F.CN1, z), F.Plus(F.CN1, a1, F.Negate(b2))), F.UnitStep(F.Plus(F.CN1, z))); // $$;
    }
    return F.NIL;
  }

  private static IExpr meijerg1010(IExpr a1, IExpr b1, IExpr z) {
    if (z.isZero() && b1.isZero()) {
      // Gamma(1-a1)
      return F.Gamma(F.Subtract(F.C1, a1));
    }
    // 1,0,1,0
    // (z^b1*Gamma(1-a1+b1))/(1+z)^(1-a1+b1)
    return F.Times(F.Power(z, b1), F.Power(F.Plus(F.C1, z), F.Plus(F.CN1, a1, F.Negate(b1))),
        F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
  }

  private static IExpr meijerg1011(IExpr a1, IExpr b1, IExpr b2, IExpr z) {
    if (z.isZero() && b1.isZero()) {
      // Gamma(1-a1)/Gamma(1-b2)
      return F.Times(F.Gamma(F.Subtract(F.C1, a1)), F.Power(F.Gamma(F.Subtract(F.C1, b2)), F.CN1));
    }
    return
    // [$ z^b1*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1 + b1,
    // 1 +
    // b1 - b2, -z) $]
    F.Times(F.Power(z, b1), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)), F.Hypergeometric1F1Regularized(
        F.Plus(F.C1, F.Negate(a1), b1), F.Plus(F.C1, b1, F.Negate(b2)), F.Negate(z))); // $$;
  }

  private static IExpr meijerg1020(IExpr a1, IExpr b1, IExpr b2, IExpr z, EvalEngine engine) {
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/12/0002/
    // z^b1*Gamma(1-a1+b1)*Gamma(1-a1+b2)*HypergeometricU(1-a1+b1,1+b1-b2,z)
    IExpr gamma1 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
    if (gamma1.isSpecialsFree()) {
      IExpr gamma2 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b2)));
      if (gamma2.isSpecialsFree()) {
        return F.Times(F.Power(z, b1), gamma1, gamma2,
            F.HypergeometricU(F.Plus(F.C1, F.Negate(a1), b1), F.Plus(F.C1, b1, F.Negate(b2)), z));
      }
    }
    return F.NIL;
  }

  private static IExpr meijerg1030(IExpr a1, IExpr b1, IExpr b2, IExpr b3, IExpr z) {
    if (a1.equals(b2) && a1.equals(b3)) {
      IExpr a1Half2 = F.CN1D2.plus(a1);
      if (a1Half2.subtract(b1).isPossibleZero(true)) {
        // https://functions.wolfram.com/07.34.03.0984.01
        // -2*Sqrt(Pi)
        final IExpr factor1 = F.Times(F.CN2, F.CSqrtPi);
        final IExpr factor2 = a1Half2.isPossibleZero(true) //
            ? F.C1 //
            : F.Power(z, F.Plus(F.CN1D2, a1));

        // CosIntegral(2*Sqrt(z))*Sin(2*Sqrt(z))+Cos(2*Sqrt(z))*(Pi/2-SinIntegral(2*Sqrt(z)))
        final IExpr factor3 = F.Plus(
            F.Times(F.CosIntegral(F.Times(F.C2, F.Sqrt(z))), F.Sin(F.Times(F.C2, F.Sqrt(z)))),
            F.Times(F.Cos(F.Times(F.C2, F.Sqrt(z))),
                F.Subtract(F.CPiHalf, F.SinIntegral(F.Times(F.C2, F.Sqrt(z))))));
        return F.Times(factor1, factor2, factor3);

      }
    }
    return F.NIL;
  }

  private static IExpr meijerg1100(IExpr a1, IExpr a2, IExpr z) {
    return
    // [$ z^(-1 + a1 + (1/2)*(-a1 + a2))*BesselJ(-a1 + a2, 2/Sqrt(z)) $]
    F.Times(F.Power(z, F.Plus(F.CN1, a1, F.Times(F.C1D2, F.Plus(F.Negate(a1), a2)))),
        F.BesselJ(F.Plus(F.Negate(a1), a2), F.Times(F.C2, F.Power(z, F.CN1D2)))); // $$;
  }

  private static IExpr meijerg1101(IExpr a1, IExpr a2, IExpr b2, IExpr z) {
    if (z.isZero() && a1.isOne()) {
      // Hypergeometric1F1Regularized(b2,a2,ComplexInfinity)/Gamma(1-b2)
      return F.Times(F.Power(F.Gamma(F.Subtract(F.C1, b2)), F.CN1),
          F.Hypergeometric1F1Regularized(b2, a2, F.CComplexInfinity));
    }
    return
    // [$ (z^(-1 + a1)*Hypergeometric1F1Regularized(1 - a1 + b2, 1 - a1 +
    // a2,
    // 1/z))/Gamma(a1 - b2) $]
    F.Times(F.Power(z, F.Plus(F.CN1, a1)), F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b2),
            F.Plus(F.C1, F.Negate(a1), a2), F.Power(z, F.CN1))); // $$;
  }

  private static IExpr meijerg1110(IExpr a1, IExpr a2, IExpr b1, IExpr z) {
    if (z.isZero() && a1.isOne()) {
      // Gamma(b1)*Hypergeometric1F1Regularized(b1,a2,ComplexInfinity)
      return F.Times(F.Gamma(b1), F.Hypergeometric1F1Regularized(b1, a2, F.CComplexInfinity));
    }
    return
    // [$ z^(-1 + a1)*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1
    // + b1, 1
    // - a1 + a2, -(1/z)) $]
    F.Times(F.Power(z, F.Plus(F.CN1, a1)), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)),
        F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
            F.Plus(F.C1, F.Negate(a1), a2), F.Negate(F.Power(z, F.CN1)))); // $$;
  }

  private static IExpr meijerg1120(IExpr a1, IExpr a2, IExpr b1, IExpr b2, IExpr z,
      EvalEngine engine) {
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/14/0001/
    // (Gamma(1-a1+b1)*Gamma(1-a1+b2)*Hypergeometric2F1Regularized(1-a1+b1,1-a1+b2,1-a1+a2,-1/z))/z^(1-a1)
    IExpr gamma1 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
    if (gamma1.isSpecialsFree()) {
      IExpr gamma2 = engine.evaluate(F.Gamma(F.Plus(F.C1, F.Negate(a1), b2)));
      if (gamma2.isSpecialsFree()) {
        return F.Times(F.Power(z, F.Plus(F.CN1, a1)), gamma1, gamma2,
            F.Hypergeometric2F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
                F.Plus(F.C1, F.Negate(a1), b2), F.Plus(F.C1, F.Negate(a1), a2),
                F.Negate(F.Power(z, F.CN1))));
      }
    }
    return F.NIL;
  }

  private static IExpr meijerg1220(IExpr a1, IExpr a2, IExpr a3, IExpr b1, IExpr b2, IExpr z,
      EvalEngine engine) {
    // https://functions.wolfram.com/HypergeometricFunctions/MeijerG/03/01/04/17/0001/
    // (Gamma(1-a1+b1)*Gamma(1-a1+b2)*HypergeometricPFQRegularized({1-a1+b1,1-a1+b2},{1-a1+a2,1-a1+a3},-1/z))/z^(1-a1)
    IExpr v2 = F.Subtract(F.C1, a1);
    IExpr v1 = F.Plus(v2, b1);
    IExpr gamma1 = engine.evaluate(F.Gamma(F.Plus(v2, b1)));
    if (gamma1.isSpecialsFree()) {
      IExpr gamma2 = engine.evaluate(F.Gamma(F.Plus(b2, v2)));
      if (gamma2.isSpecialsFree()) {
        return F.Times(F.Power(z, F.Plus(F.CN1, a1)), gamma1, gamma2,
            F.HypergeometricPFQRegularized(F.list(v1, F.Plus(b2, v2)),
                F.list(F.Plus(a2, v2), F.Plus(a3, v2)), F.Negate(F.Power(z, F.CN1))));
      }
    }
    return F.NIL;
  }

  public MeijerG() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST3()) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr z = ast.arg3();
      if (z.isList()) {
        return z.mapThread(ast.setAtCopy(3, F.Slot1), 3);
      }
      if (arg1.isList() && arg2.isList()) {
        IAST list1 = (IAST) arg1;
        IAST list2 = (IAST) arg2;
        if ((list1.isNumericArgument(true)//
            || list2.isNumericArgument(true))//
            || z.isNumericArgument(false)) {
          IExpr temp = functionExpand(ast, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        if (list1.isList2() && list1.arg1().isList() && list1.arg2().isList() && //
            list2.isList2() && list2.arg1().isList() && list2.arg2().isList()) {
          IAST k1 = (IAST) list1.arg1();
          IAST k2 = (IAST) list1.arg2();
          IAST l1 = (IAST) list2.arg1();
          IAST l2 = (IAST) list2.arg2();
          int n = k1.argSize();
          int p = k2.argSize();
          int m = l1.argSize();
          int q = l2.argSize();
          switch (n) {
            case 0:
              return k1ArgSize0(z, k1, k2, l1, l2, p, m, q, ast, engine);
            case 1:
              return k1ArgSize1(z, k1, k2, l1, l2, p, m, q, ast, engine);
            case 2:
              return k1ArgSize2(z, k1, k2, l1, l2, p, m, q, ast, engine);
          }
        }
      }
    }
    return F.NIL;
  }


  @Override
  public IExpr functionExpand(final IAST ast, EvalEngine engine) {
    if (ast.isAST3()) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr z = ast.arg3();
      if (z.isList()) {
        return z.mapThread(ast.setAtCopy(3, F.Slot1), 3);
      }
      if (arg1.isList2() && arg2.isList2()) {
        IAST aList = (IAST) arg1;
        IAST bList = (IAST) arg2;
        if (aList.arg1().isList1() && aList.arg2().isList1() && //
            bList.arg1().isList1() && bList.arg2().isList1()) {
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
          return F
              .Piecewise(F.list(F.list(F.CComplexInfinity, F.And(v4, F.Greater(v1, F.CN1))), F.list(
                  F.Times(
                      F.Power(z, b1), F.Power(F.Gamma(F.Subtract(a2, b1)), F.CN1), F
                          .Power(F.Gamma(v2), F.CN1),
                      F.Gamma(F.Plus(b1, v3)),
                      F.Hypergeometric2F1(F.Plus(b1, v3), F.Plus(F.C1, b1, v5), v2, z)),
                  F.Or(F.And(v4, F.Less(v1, F.CN1)), F.Less(v7, F.C1))),
                  F.list(
                      F.Times(F.Power(z, F.Plus(F.CN1, a1)),
                          F.Power(F.Gamma(F.Plus(a2, v3)), F.CN1), F.Gamma(F.Plus(b1, v3)),
                          F.Power(F.Gamma(F.Plus(a1, v6)), F.CN1), F.Hypergeometric2F1(
                              F.Plus(b1, v3), F.Plus(b2, v3), F.Plus(a2, v3), F.Power(z, F.CN1))),
                      F.Greater(v7, F.C1))),
                  F.Indeterminate);
        }

        if (aList.arg1().isEmptyList() && aList.arg2().isList1() && //
            bList.arg1().isList1() && bList.arg2().isEmptyList()) {
          // 0,1,1,0
          IExpr a2 = aList.arg2().first();
          IExpr b1 = bList.arg1().first();
          // Piecewise({{z^b1/((1-z)^(1-a2+b1)*Gamma(a2-b1)),(z==1&&Re(-a2+b1)<-1)||Abs(z)<1},{
          // 0,Abs(z)>1}},Indeterminate)
          IExpr v2 = F.Abs(z);
          IExpr v1 = F.Negate(b1);
          return F.Piecewise(F.list(F.list(
              F.Times(F.Power(F.Subtract(F.C1, z), F.Plus(F.CN1, a2, v1)), F.Power(z, b1),
                  F.Power(F.Gamma(F.Plus(a2, v1)), F.CN1)),
              F.Or(F.And(F.Equal(z, F.C1), F.Less(F.Plus(F.Negate(F.Re(a2)), F.Re(b1)), F.CN1)),
                  F.Less(v2, F.C1))),
              F.list(F.C0, F.Greater(v2, F.C1))), F.Indeterminate);
        }
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
