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
import org.matheclipse.core.interfaces.ISymbol;

public class MeijerG extends AbstractFunctionEvaluator implements IFunctionExpand {

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

  /**
   * <code>k1</code> is an empty list and contains no arguments.
   * 
   */
  private static IExpr k1ArgSize0(IExpr z, IAST k1, IAST k2, IAST l1, IAST l2, int p, int m, int q,
      IAST ast, EvalEngine engine) {
    switch (p) {
      case 0:
        // 0,0
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
                if (z.isZero() && b1.isZero()) {
                  return F.C1;
                }
                // z^b1/E^z
                return F.Times(F.Power(F.Exp(z), F.CN1), F.Power(z, b1));
              }
              case 1:
              // 0,0,1,1
              {
                IExpr b1 = l1.arg1();
                IExpr b2 = l2.arg1();
                return
                // [$ z^(b1 + (1/2)*(-b1 + b2))*BesselJ(b1 - b2, 2*Sqrt(z)) $]
                F.Times(F.Power(z, F.Plus(b1, F.Times(F.C1D2, F.Plus(F.Negate(b1), b2)))),
                    F.BesselJ(F.Subtract(b1, b2), F.Times(F.C2, F.Sqrt(z)))); // $$;
              }
            }
            break;
        }
        break;
      case 1:
        // 0,1
        IExpr a2 = k2.arg1();
        switch (m) {
          case 1:
            // 0,1,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 1:
                // 0,1,1,1
                IExpr b2 = l2.arg1();
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
              case 1:
                // 1,0,0,1
                IExpr b2 = l2.arg1();
                if (z.isPositive()) {
                  return
                  // [$ (z^b2/Gamma(a1 - b2))*(z - 1)^(a1 - b2 - 1)*UnitStep(z - 1) $]
                  F.Times(F.Power(z, b2), F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
                      F.Power(F.Plus(F.CN1, z), F.Plus(F.CN1, a1, F.Negate(b2))),
                      F.UnitStep(F.Plus(F.CN1, z))); // $$;
                }
            }
            break;
          case 1:
            // 1,0,1
            IExpr b1 = l1.arg1();
            switch (q) {
              case 0:
                if (z.isZero() && b1.isZero()) {
                  // Gamma(1-a1)
                  return F.Gamma(F.Subtract(F.C1, a1));
                }
                // 1,0,1,0
                // (z^b1*Gamma(1-a1+b1))/(1+z)^(1-a1+b1)
                return F.Times(F.Power(z, b1),
                    F.Power(F.Plus(F.C1, z), F.Plus(F.CN1, a1, F.Negate(b1))),
                    F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)));
              case 1:
                // 1,0,1,1
                IExpr b2 = l2.arg1();
                if (z.isZero() && b1.isZero()) {
                  // Gamma(1-a1)/Gamma(1-b2)
                  return F.Times(F.Gamma(F.Subtract(F.C1, a1)),
                      F.Power(F.Gamma(F.Subtract(F.C1, b2)), F.CN1));
                }
                return
                // [$ z^b1*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1 + b1,
                // 1 +
                // b1 - b2, -z) $]
                F.Times(F.Power(z, b1), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)),
                    F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
                        F.Plus(F.C1, b1, F.Negate(b2)), F.Negate(z))); // $$;
            }
            break;
        }
        break;
      case 1:
        // 1,1
        IExpr a2 = k2.arg1();
        switch (m) {
          case 0:
            // 1,1,0
            switch (q) {
              case 0:
                // 1,1,0,0
                return
                // [$ z^(-1 + a1 + (1/2)*(-a1 + a2))*BesselJ(-a1 + a2, 2/Sqrt(z)) $]
                F.Times(F.Power(z, F.Plus(F.CN1, a1, F.Times(F.C1D2, F.Plus(F.Negate(a1), a2)))),
                    F.BesselJ(F.Plus(F.Negate(a1), a2), F.Times(F.C2, F.Power(z, F.CN1D2)))); // $$;
              case 1:
                // 1,1,0,1
                IExpr b2 = l2.arg1();
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
            break;
          case 1:
            IExpr b1 = l1.arg1();
            switch (q) {
              case 0:
                // 1,1,1,0
                if (z.isZero() && a1.isOne()) {
                  // Gamma(b1)*Hypergeometric1F1Regularized(b1,a2,ComplexInfinity)
                  return F.Times(F.Gamma(b1),
                      F.Hypergeometric1F1Regularized(b1, a2, F.CComplexInfinity));
                }
                return
                // [$ z^(-1 + a1)*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1
                // + b1, 1
                // - a1 + a2, -(1/z)) $]
                F.Times(F.Power(z, F.Plus(F.CN1, a1)), F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)),
                    F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
                        F.Plus(F.C1, F.Negate(a1), a2), F.Negate(F.Power(z, F.CN1)))); // $$;
            }
            break;
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
            switch (q) {
              case 1:
                // 2,0,1,1
                if (k1.forAll(x -> x.isOne()) //
                    && l1.arg1().isOne() //
                    && l2.arg1().isZero()) {
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

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
    super.setUp(newSymbol);
  }
}
