package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.Traversal;

/**
 * <pre>
 * MeijerGReduce(expr, x)
 * </pre>
 *
 * <blockquote>
 * <p>
 * attempts to reduce elementary and special functions in <code>expr</code> to equivalent
 * representations using the Meijer G-function <code>MeijerG</code>.
 * </p>
 * </blockquote>
 */
public class MeijerGReduce extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST2()) {
      IExpr expr = ast.arg1();
      IExpr x = ast.arg2();

      if (!x.isSymbol()) {
        return F.NIL;
      }

      IExpr result = Traversal.bottomUp(expr, e -> transformStep(e, x, engine)).orElse(expr);

      return engine.evaluate(F.Together(result));
    }
    return F.NIL;
  }

  private static IExpr transformStep(IExpr expr, IExpr x, EvalEngine engine) {
    if (expr.isExp()) {
      IExpr z = expr.exponent();
      IExpr[] linear = z.linear(x);
      if (linear != null && linear[0].isZero()) {
        IExpr aTimesX = z;
        IAST emptyList = F.List();
        IAST aLists = F.List(emptyList, emptyList);
        // Exp(z) = MeijerG({{}, {}}, {{0, 1/2}, {}}, -z/2, 1/2) / Sqrt(Pi)
        IAST bListsExp = F.List(F.List(F.C0, F.C1D2), emptyList);
        IExpr zExp = F.Times(F.CN1D2, aTimesX);
        return F.Times(F.Power(S.Pi, F.CN1D2),
            F.quaternary(F.Inactive(S.MeijerG), aLists, bListsExp, zExp, F.C1D2));
      }
    }

    if (expr.isAST1()) {
      IExpr head = expr.head();
      if (head.isBuiltInSymbol()) {
        IExpr arg = expr.first();
        IExpr[] linear = arg.linear(x);

        if (linear != null && linear[0].isZero()) {
          IExpr aTimesX = arg;
          IAST emptyList = F.List();
          IAST aLists = F.List(emptyList, emptyList);

          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Sin:
              // Sin(z) = Sqrt(Pi) * MeijerG({{}, {}}, {{1/2}, {0}}, z/2, 1/2)
              IAST bListsSin = F.List(F.List(F.C1D2), F.List(F.C0));
              IExpr zSin = F.Times(F.C1D2, aTimesX);
              return F.Times(F.Power(S.Pi, F.C1D2),
                  F.quaternary(F.Inactive(S.MeijerG), aLists, bListsSin, zSin, F.C1D2));

            case ID.Cos:
              // Cos(z) = Sqrt(Pi) * MeijerG({{}, {}}, {{0}, {1/2}}, z/2, 1/2)
              IAST bListsCos = F.List(F.List(F.C0), F.List(F.C1D2));
              IExpr zCos = F.Times(F.C1D2, aTimesX);
              return F.Times(F.Power(S.Pi, F.C1D2),
                  F.quaternary(F.Inactive(S.MeijerG), aLists, bListsCos, zCos, F.C1D2));

            case ID.Sinh:
              // Sinh(z) = Sqrt(2)*Pi^(3/2) * MeijerG({{}, {3/4}}, {{1/2}, {3/4, 0}}, z/2, 1/2)
              IAST aListsSinh = F.List(emptyList, F.List(F.QQ(3, 4)));
              IAST bListsSinh = F.List(F.List(F.C1D2), F.List(F.QQ(3, 4), F.C0));
              IExpr zSinh = F.Times(F.C1D2, aTimesX);
              IExpr prefactorSinh = F.Times(F.Sqrt(F.C2), F.Power(S.Pi, F.QQ(3, 2)));
              return F.Times(prefactorSinh,
                  F.quaternary(F.Inactive(S.MeijerG), aListsSinh, bListsSinh, zSinh, F.C1D2));

            case ID.Cosh:
              // Cosh(z) = Sqrt(2)*Pi^(3/2) * MeijerG({{}, {3/4}}, {{0}, {3/4, 1/2}}, z/2, 1/2)
              IAST aListsCosh = F.List(emptyList, F.List(F.QQ(3, 4)));
              IAST bListsCosh = F.List(F.List(F.C0), F.List(F.QQ(3, 4), F.C1D2));
              IExpr zCosh = F.Times(F.C1D2, aTimesX);
              IExpr prefactorCosh = F.Times(F.Sqrt(F.C2), F.Power(S.Pi, F.QQ(3, 2)));
              return F.Times(prefactorCosh,
                  F.quaternary(F.Inactive(S.MeijerG), aListsCosh, bListsCosh, zCosh, F.C1D2));
            case ID.AiryAi:
              // AiryAi(z) = MeijerG({{}, {}}, {{0, 1/3}, {}}, z / 3^(2/3), 1/3) / (2 * 3^(1/6) *
              // Pi)
              IAST bListsAiryAi = F.List(F.List(F.C0, F.QQ(1, 3)), emptyList);
              IExpr zAiryAi = F.Times(aTimesX, F.Power(F.C3, F.QQ(-2, 3)));
              IExpr prefactorAi = F.Power(F.Times(F.C2, F.Power(F.C3, F.QQ(1, 6)), S.Pi), F.CN1);
              return F.Times(prefactorAi,
                  F.quaternary(F.Inactive(S.MeijerG), aLists, bListsAiryAi, zAiryAi, F.QQ(1, 3)));
          }
        }
      }
    }
    else if (expr.isAST2()) {
      IExpr head = expr.head();
      if (head.isBuiltInSymbol()) {
        IExpr v = expr.first();
        IExpr arg = expr.second();
        IExpr[] linear = arg.linear(x);

        if (linear != null && linear[0].isZero()) {
          IExpr aTimesX = arg;
          IAST emptyList = F.List();
          IAST aLists = F.List(emptyList, emptyList);

          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.BesselJ:
              // BesselJ(v, z) = MeijerG({{}, {}}, {{v/2}, {-v/2}}, z/2, 1/2)
              IExpr vHalfJ = F.Times(F.C1D2, v);
              IAST bListsJ = F.List(F.List(vHalfJ), F.List(F.Negate(vHalfJ)));
              IExpr zJ = F.Times(F.C1D2, aTimesX);
              return F.quaternary(F.Inactive(S.MeijerG), aLists, bListsJ, zJ, F.C1D2);

            case ID.BesselK:
              // BesselK(v, z) = 1/2 * MeijerG({{}, {}}, {{v/2, -v/2}, {}}, z/2, 1/2)
              IExpr vHalfK = F.Times(F.C1D2, v);
              IAST bListsK = F.List(F.List(vHalfK, F.Negate(vHalfK)), emptyList);
              IExpr zK = F.Times(F.C1D2, aTimesX);
              return F.Times(F.C1D2,
                  F.quaternary(F.Inactive(S.MeijerG), aLists, bListsK, zK, F.C1D2));

            case ID.BesselY:
              // BesselY(v, z) = MeijerG({{-(v+1)/2}, {}}, {{v/2, -v/2}, {-(v+1)/2}}, z/2, 1/2)
              IExpr vHalfY = F.Times(F.C1D2, v);
              IExpr topY = F.Times(F.CN1D2, F.Plus(v, F.C1));
              IAST aListsY = F.List(F.List(topY), emptyList);
              IAST bListsY = F.List(F.List(vHalfY, F.Negate(vHalfY)), F.List(topY));
              IExpr zY = F.Times(F.C1D2, aTimesX);
              return F.quaternary(F.Inactive(S.MeijerG), aListsY, bListsY, zY, F.C1D2);
          }
        }
      }
    }

    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
  
  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
