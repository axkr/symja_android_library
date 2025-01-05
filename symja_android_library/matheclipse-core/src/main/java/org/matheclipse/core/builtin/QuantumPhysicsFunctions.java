package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.sympy.physics.Wigner;

public class QuantumPhysicsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ClebschGordan.setEvaluator(new ClebschGordan());
      S.ThreeJSymbol.setEvaluator(new ThreeJSymbol());
      S.SixJSymbol.setEvaluator(new SixJSymbol());
    }
  }

  private static class ClebschGordan extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (arg1.isList2() && arg2.isList2() && arg3.isList2()) {
        IExpr j1 = arg1.first();
        IExpr j2 = arg2.first();
        IExpr j3 = arg3.first();
        IExpr m1 = arg1.second();
        IExpr m2 = arg2.second();
        IExpr m3 = arg3.second();

        IExpr clebschGordan = Wigner.clebschGordan(j1, j2, j3, m1, m2, m3);
        if (clebschGordan.isPresent()) {
          return clebschGordan;
        }
        // (-1)^(j1-j2+m3)*Sqrt(1+2*j3)*ThreeJSymbol({j1,m1},{j2,m2},{j3,-m3})
        return F.Times(F.Power(F.CN1, F.Plus(j1, F.Negate(j2), m3)),
            F.Sqrt(F.Plus(F.C1, F.Times(F.C2, j3))),
            F.ThreeJSymbol(F.list(j1, m1), F.list(j2, m2), F.list(j3, F.Negate(m3))));
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  private static class ThreeJSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (arg1.isList2() && arg2.isList2() && arg3.isList2()) {
        IExpr j1 = arg1.first();
        IExpr j2 = arg2.first();
        IExpr j3 = arg3.first();
        IExpr m1 = arg1.second();
        IExpr m2 = arg2.second();
        IExpr m3 = arg3.second();
        return Wigner.threeJSymbol(j1, j2, j3, m1, m2, m3, //
            S.ThreeJSymbol, ast);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }


  }

  private static class SixJSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList3() && arg2.isList3()) {
        IAST list1 = (IAST) arg1;
        IAST list2 = (IAST) arg2;
        IExpr j1 = list1.arg1();
        IExpr j2 = list1.arg2();
        IExpr j3 = list1.arg3();
        IExpr m1 = list2.arg1();
        IExpr m2 = list2.arg2();
        IExpr m3 = list2.arg3();
        if (j1.isRational() && j2.isRational() && j3.isRational() && m1.isRational()
            && m2.isRational() && m3.isRational()) {
          try {
            // double wigner6j = Wigner.wigner6j(j1.evalf(), j2.evalf(), j3.evalf(), m1.evalf(),
            // m2.evalf(), m3.evalf());
            return Wigner.wigner6j((IRational) j1, (IRational) j2, (IRational) j3, (IRational) m1,
                (IRational) m2, (IRational) m3);
          } catch (Wigner.TriangularException wtex) {
            Errors.printMessage(S.SixJSymbol, "tri", F.List(ast), engine);
            return wtex.getValue();
          }
        }
        if (j1.isReal() && j2.isReal() && j3.isReal() && m1.isReal() && m2.isReal()
            && m3.isReal()) {
          try {
            // double wigner6j = Wigner.wigner6j(j1.evalf(), j2.evalf(), j3.evalf(), m1.evalf(),
            // m2.evalf(), m3.evalf());
            IExpr wigner6j = Wigner.wigner6j((IReal) j1, (IReal) j2, (IReal) j3, (IReal) m1,
                (IReal) m2, (IReal) m3);
            return F.num(wigner6j.evalf());
          } catch (Wigner.TriangularException wtex) {
            Errors.printMessage(S.SixJSymbol, "tri", F.List(ast), engine);
            return wtex.getValue();
          }
        }
        if (j1.isNumericFunction() && j2.isNumericFunction() && j3.isNumericFunction()
            && m1.isNumericFunction() && m2.isNumericFunction() && m3.isNumericFunction()) {
          Errors.printMessage(S.SixJSymbol, "tri", F.List(ast), engine);
          return F.C0;
        }
        // return sixJSymbol(j1, j2, j3, m1, m2, m3, //
        // S.SixJSymbol, ast, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


  }



  public static void initialize() {
    Initializer.init();
  }

  private QuantumPhysicsFunctions() {}
}
