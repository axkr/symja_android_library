package org.matheclipse.core.builtin;

import java.util.Iterator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class GraphicsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BernsteinBasis.setEvaluator(new BernsteinBasis());
      S.Cuboid.setEvaluator(new Cuboid());
      S.Cylinder.setEvaluator(new Cylinder());
      S.Sphere.setEvaluator(new Sphere());
      S.Volume.setEvaluator(new Volume());
    }
  }

  private static class BernsteinBasis extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dArg1 = ast.arg1();
      IExpr nArg2 = ast.arg2();
      IExpr x = ast.arg3();
      if (dArg1.isReal() && nArg2.isReal() && x.isReal()) {
        int d = dArg1.toIntDefault();
        if (d < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        IInteger di = F.ZZ(d);
        int n = nArg2.toIntDefault();
        if (n < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        if (n > d) {
          // Index `1` should be a machine sized integer between `2` and `3`.
          return IOFunctions.printMessage(
              ast.topHead(), "invidx2", F.list(nArg2, F.C0, di), engine);
        }
        IExpr condition = F.Less(F.C0, x, F.C1);
        if (engine.evalTrue(condition)) {
          IInteger ni = F.ZZ(n);
          // Binomial(d, ni) * x^ni * (1 - x)^(di - ni)
          return F.Times(
              F.Binomial(di, ni), F.Power(x, ni), F.Power(F.Subtract(F.C1, x), F.Subtract(di, ni)));
        } else {
          return F.C0;
        }
        // return F.Piecewise(F.list1(F.list2(piece, condition)), F.C0);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDALL | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class Cuboid extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cuboid(F.List(0, 0, 0), F.List(1, 1, 1));
      } else if (ast.isAST1() && ast.arg1().isList3()) {
        IASTMutable list2 = ((IAST) ast.arg1()).copy();
        for (int i = 1; i < list2.size(); i++) {
          list2.set(i, F.Plus(F.C1, list2.get(i)));
        }
        return F.Cuboid(ast.arg1(), list2);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cylinder extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cylinder(F.List(F.List(0, 0, -1), F.List(0, 0, 1)));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Sphere extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Sphere(F.List(0, 0, 0), F.C1);
      }
      if (ast.isAST1()) {
        return F.Sphere(ast.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Volume extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST graphic = (IAST) ast.arg1();
        if (graphic.isAST(S.Ball, 2, 3) && graphic.first().isList3()) {
          IExpr radius = F.C1;
          if (graphic.size() == 3) {
            radius = graphic.second();
          }
          return F.Times(F.C3D4, S.Pi, F.Power(radius, F.C3));
        } else if (graphic.isAST(S.Cuboid, 3)
            && graphic.first().isList3()
            && graphic.second().isList3()) {
          IAST v1 = (IAST) graphic.first();
          IAST v2 = (IAST) graphic.second();
          // Abs((-a + x)*(-b + y)*(-c + z))
          return F.Abs(
              F.Times( //
                  F.Plus(v1.arg1().negate(), v2.arg1()),
                  F.Plus(v1.arg2().negate(), v2.arg2()),
                  F.Plus(v1.arg3().negate(), v2.arg3())));
        } else if (graphic.isAST(S.Ellipsoid, 3)
            && graphic.first().isList3()
            && graphic.second().isList3()) {
          // IAST v1 = (IAST) graphic.first();
          IAST v2 = (IAST) graphic.second();
          return F.Times(F.QQ(4, 3), S.Pi, v2.arg1(), v2.arg2(), v2.arg3());
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static void initialize() {
    Initializer.init();
  }

  private GraphicsFunctions() {}
}
