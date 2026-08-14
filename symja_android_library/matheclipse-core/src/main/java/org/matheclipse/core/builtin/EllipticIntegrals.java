package org.matheclipse.core.builtin;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.elliptic.carlson.CarlsonEllipticIntegral;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.EllipticFunctionsJS;
import org.matheclipse.core.numerics.functions.EllipticIntegralsJS;

public class EllipticIntegrals {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.CarlsonRC.setEvaluator(new CarlsonRC());
      S.CarlsonRD.setEvaluator(new CarlsonRD());
      S.CarlsonRF.setEvaluator(new CarlsonRF());
      S.CarlsonRG.setEvaluator(new CarlsonRG());
      S.CarlsonRJ.setEvaluator(new CarlsonRJ());

      S.EllipticE.setEvaluator(new EllipticE());
      S.EllipticExp.setEvaluator(new EllipticExp());
      S.EllipticF.setEvaluator(new EllipticF());
      S.EllipticK.setEvaluator(new EllipticK());
      S.EllipticLog.setEvaluator(new EllipticLog());
      S.EllipticPi.setEvaluator(new EllipticPi());
      S.EllipticTheta.setEvaluator(new EllipticTheta());

      // S.InverseWeierstrassP.setEvaluator(new InverseWeierstrassP());
      S.InverseJacobiCD.setEvaluator(new InverseJacobiCD());
      S.InverseJacobiCN.setEvaluator(new InverseJacobiCN());
      S.InverseJacobiDN.setEvaluator(new InverseJacobiDN());
      S.InverseJacobiSC.setEvaluator(new InverseJacobiSC());
      S.InverseJacobiSD.setEvaluator(new InverseJacobiSD());
      S.InverseJacobiSN.setEvaluator(new InverseJacobiSN());
      S.JacobiAmplitude.setEvaluator(new JacobiAmplitude());
      S.JacobiCD.setEvaluator(new JacobiCD());
      S.JacobiCN.setEvaluator(new JacobiCN());
      S.JacobiDN.setEvaluator(new JacobiDN());
      S.JacobiSC.setEvaluator(new JacobiSC());
      S.JacobiSD.setEvaluator(new JacobiSD());
      S.JacobiSN.setEvaluator(new JacobiSN());
      S.JacobiZeta.setEvaluator(new JacobiZeta());

      S.KleinInvariantJ.setEvaluator(new KleinInvariantJ());

      S.WeierstrassHalfPeriods.setEvaluator(new WeierstrassHalfPeriods());
      S.WeierstrassInvariants.setEvaluator(new WeierstrassInvariants());
      S.WeierstrassP.setEvaluator(new WeierstrassP());
      S.WeierstrassPPrime.setEvaluator(new WeierstrassPPrime());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CarlsonRC(x, y)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Carlson RC function..
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   * symmetric form</a>
   * </ul>
   */
  private static class CarlsonRC extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr a = ast.arg1();
        IExpr b = ast.arg2();
        if (a.isReal() && b.isReal()) {
          // https://en.wikipedia.org/wiki/Carlson_symmetric_form#Special_cases
          IReal x = (IReal) a;
          IReal y = (IReal) b;
          if (x.isLT(y)) {
            if (x.divide(y).isPositive()) {
              // ArcCos( Sqrt(x/y) ) / Sqrt(y-x)
              return F.Times(F.ArcCos(F.Sqrt(F.Divide(x, y))), F.Power(F.Subtract(y, x), F.CN1D2));
            }
          } else if (x.isGT(y)) {
            if (x.divide(y).isPositive()) {
              // ArcCosh( Sqrt(x/y) ) / Sqrt(x-y)
              return F.Times(F.ArcCosh(F.Sqrt(F.Divide(x, y))), F.Power(F.Subtract(x, y), F.CN1D2));
            }
          }
        }
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      return carlsonRC(engine, x, y).eval(engine);
    }

    private static IExpr carlsonRC(EvalEngine engine, IExpr x, IExpr y) {
      if (x.equals(y)) {
        final IExpr reCondition = S.LessEqual.of(engine, F.Re(x), F.C0);
        final IExpr imCondition = S.Equal.of(engine, F.Im(x), F.C0);
        if (reCondition.isTrue() && imCondition.isTrue()) {
          return F.CComplexInfinity;
        }
        if (reCondition.isFalse() || imCondition.isFalse()) {
          // 1 / Sqrt(x)
          return F.Power.of(engine, x, F.CN1D2);
        }
        return F.Piecewise(F.list(
            F.list(F.CComplexInfinity, F.And(F.LessEqual(F.Re(x), F.C0), F.Equal(F.Im(x), F.C0)))),
            F.Power(x, F.CN1D2));
      }
      if (y.isZero()) {
        return F.CComplexInfinity;
      }

      if (engine.isNumericMode()) {
        if (engine.isArbitraryMode()) {
          x = engine.evalN(x);
          y = engine.evalN(y);
          if (x.isNumber() && y.isNumber()) {
            return CarlsonEllipticIntegral.rC(x, y);
          }
          return F.NIL;
        }
        double xd = x.evalfNaN();
        double yd = y.evalfNaN();
        if (Double.isNaN(xd) || Double.isNaN(yd)) {
          Complex xc = x.evalfc();
          Complex yc = y.evalfc();
          return F.complexNum(EllipticIntegralsJS.carlsonRC(xc, yc));
        } else {
          return F.num(EllipticIntegralsJS.carlsonRC(xd, yd));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CarlsonRD(x, y, z)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Carlson RD function.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   * symmetric form</a>
   * </ul>
   */
  private static class CarlsonRD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      return carlsonRD(x, y, z, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr carlsonRD(IExpr x, IExpr y, IExpr z, boolean doubleMode) {
      if (doubleMode) {
        double xd = x.evalfNaN();
        double yd = y.evalfNaN();
        double zd = z.evalfNaN();
        if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
          Complex xc = x.evalfc();
          Complex yc = y.evalfc();
          Complex zc = z.evalfc();
          return F.complexNum(EllipticIntegralsJS.carlsonRD(xc, yc, zc));
        } else {
          return F.num(EllipticIntegralsJS.carlsonRD(xd, yd, zd));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CarlsonRF(x, y, z)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Carlson RF function.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   * symmetric form</a>
   * </ul>
   */
  private static class CarlsonRF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      return carlsonRF(x, y, z, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr carlsonRF(IExpr x, IExpr y, IExpr z, boolean doubleMode) {
      if (x.equals(y) && x.equals(z)) {
        // 1 / Sqrt(x)
        return F.Power(x, F.CN1D2);
      }
      if (doubleMode) {
        double xd = x.evalfNaN();
        double yd = y.evalfNaN();
        double zd = z.evalfNaN();
        if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
          Complex xc = x.evalfc();
          Complex yc = y.evalfc();
          Complex zc = z.evalfc();
          return F.complexNum(EllipticIntegralsJS.carlsonRF(xc, yc, zc));
        } else {
          return F.num(EllipticIntegralsJS.carlsonRF(xd, yd, zd));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CarlsonRG(x, y, z)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Carlson RG function.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   * symmetric form</a>
   * </ul>
   */
  private static class CarlsonRG extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      return carlsonRG(x, y, z, engine.isDoubleMode()).eval();
    }

    private static IExpr carlsonRG(IExpr x, IExpr y, IExpr z, boolean doubleMode) {
      if (x.equals(y)) {
        if (x.equals(z)) {
          // 1 / Sqrt(x)
          return F.Power(x, F.CN1D2);
        }
        // (1/2) * (Sqrt(z) + x*CarlsonRF(x,x,z))
        return F.Times(F.C1D2, F.Plus(F.Sqrt(z), F.Times(x, F.CarlsonRF(x, x, z))));
      }
      if (doubleMode) {
        try {
          double xd = x.evalfNaN();
          double yd = y.evalfNaN();
          double zd = z.evalfNaN();
          if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
            Complex xc = x.evalfc();
            Complex yc = y.evalfc();
            Complex zc = z.evalfc();
            return F.complexNum(EllipticIntegralsJS.carlsonRG(xc, yc, zc));
          } else {
            return F.num(EllipticIntegralsJS.carlsonRG(xd, yd, zd));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.CarlsonRG, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CarlsonRJ(x, y, z, p)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Carlson RJ function.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   * symmetric form</a>
   * </ul>
   */
  private static class CarlsonRJ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      IExpr p = ast.arg4();
      return carlsonRG(x, y, z, p, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr carlsonRG(IExpr x, IExpr y, IExpr z, IExpr p, boolean doubleMode) {
      if (doubleMode) {
        try {
          double xd = x.evalfNaN();
          double yd = y.evalfNaN();
          double zd = z.evalfNaN();
          double pd = p.evalfNaN();
          if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd) || Double.isNaN(pd)) {
            Complex xc = x.evalfc();
            Complex yc = y.evalfc();
            Complex zc = z.evalfc();
            Complex pc = p.evalfc();
            return F.complexNum(EllipticIntegralsJS.carlsonRJ(xc, yc, zc, pc));
          } else {
            return F.num(EllipticIntegralsJS.carlsonRJ(xd, yd, zd, pd));
          }
        } catch (ValidateException ve) {
          throw ve; // NOPMD
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.CarlsonRJ, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class EllipticE extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (ast.isAST2()) {
        return ellipticE2(z, ast, engine).eval(engine);
      }

      return ellipticE3(z).eval(engine);
    }

    private static IExpr ellipticE3(IExpr z) {
      if (z.isZero()) {
        // Pi/2
        return F.CPiHalf;
      }
      if (z.isOne()) {
        return z;
      }
      if (z.isNumEqualRational(F.C1D2)) {
        // (Pi^2 + 2 Gamma(3/4)^4)/(4*Sqrt(Pi)*Gamma(3/4)^2)
        return F.Times(F.C1D4, F.Power(S.Pi, F.CN1D2), F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
            F.Plus(F.Sqr(S.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
      }
      if (z.isMinusOne()) {
        // (Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2)*Sqrt(Pi)*Gamma(3/4)^2)
        return F.Times(F.C1D2, F.C1DSqrt2, F.Power(S.Pi, F.CN1D2),
            F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
            F.Plus(F.Sqr(S.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
      }

      if (z.isInfinity() || z.isNegativeInfinity() || z.isComplexInfinity()) {
        return F.CComplexInfinity;
      }
      return F.NIL;
    }

    private static IExpr ellipticE2(IExpr z, IAST ast, EvalEngine engine) {
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return z;
      }
      if (z.isZero()) {
        return z;
      }
      if (m.isOne()) {
        // Abs(Re(z)) <= Pi/2
        if (engine.evalLessEqual(F.Abs(F.Re(z)), F.CPiHalf)) {
          return F.Sin(z);
        }
      }
      if (m.isInfinity() || m.isNegativeInfinity()) {
        return F.CComplexInfinity;
      }
      if (z.equals(F.CPiHalf)) {
        // EllipticE(Pi/2, m) = EllipticE(m)
        return F.EllipticE(m);
      }

      if (engine.isDoubleMode() && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            Complex zc = z.evalfc();
            Complex mc = m.evalfc();
            return F.complexNum(EllipticIntegralsJS.ellipticE(zc, mc));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticE(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.EllipticE, rex);
          return F.NIL;
        }
      }

      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        // EllipticE(-z,m) = -EllipticE(z,m)
        return F.Negate(F.EllipticE(negExpr, m));
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.ellipticE();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class EllipticExp extends AbstractFunctionEvaluator implements IFunctionExpand {

    /**
     * The Weierstrass invariants <code>{g2,g3}</code> of the curve
     * <code>y^2 == x^3+a*x^2+b*x</code>, which is brought into the form
     * <code>Y^2 == 4*X^3-g2*X-g3</code> by <code>X == x+a/3</code> and <code>Y == 2*y</code>.
     *
     * @param a
     * @param b
     */
    private static IAST invariants(IExpr a, IExpr b) {
      return F.list(//
          // g2 == 4/3*(a^2-3*b)
          F.Times(F.QQ(4, 3), F.Subtract(F.Sqr(a), F.Times(F.C3, b))), //
          // g3 == 4/27*(9*a*b-2*a^3)
          F.Times(F.QQ(4, 27), F.Subtract(F.Times(F.C9, a, b), F.Times(F.C2, F.Power(a, F.C3)))));
    }

    /**
     * The point <code>{x,y}</code> of the curve <code>y^2 == x^3+a*x^2+b*x</code> expressed by the
     * Weierstrass functions of its invariants:
     * <code>{-a/3+WeierstrassP(u,{g2,g3}), WeierstrassPPrime(u,{g2,g3})/2}</code>.
     *
     * @param u
     * @param a
     * @param invariants the <code>{g2,g3}</code> of {@link #invariants(IExpr, IExpr)}
     */
    private static IAST weierstrassForm(IExpr u, IExpr a, IAST invariants) {
      return F.list(//
          F.Subtract(F.binaryAST2(S.WeierstrassP, u, invariants), F.Divide(a, F.C3)), //
          F.Divide(F.binaryAST2(S.WeierstrassPPrime, u, invariants), F.C2));
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList2()) {
        IExpr u = ast.arg1();
        IAST list = (IAST) ast.arg2();
        IExpr a = list.arg1();
        IExpr invariants = engine.evaluate(invariants(a, list.arg2()));
        if (invariants.isList2()) {
          IAST inv = (IAST) invariants;
          IAST parameters = weierstrassJacobiParameters(inv.arg1(), inv.arg2());
          if (parameters.isPresent()) {
            // FunctionExpand() only descends one level into the returned expression, so expand the
            // Weierstrass functions here instead of leaving them to a second FunctionExpand() call
            return F.list(//
                F.Subtract(weierstrassPJacobi(u, parameters), F.Divide(a, F.C3)), //
                F.Divide(weierstrassPPrimeJacobi(u, parameters), F.C2));
          }
          return weierstrassForm(u, a, inv);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr u = ast.arg1();
      if (!ast.arg2().isList2()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg2();
      IExpr a = list.arg1();
      IExpr b = list.arg2();
      if (u.isZero()) {
        // the point at infinity of the curve y^2 == x^3+a*x^2+b*x
        return F.list(F.CComplexInfinity, F.CComplexInfinity);
      }
      IExpr temp = engine.evaluate(weierstrassForm(u, a, invariants(a, b)));
      if (temp.isList2() && isConcrete(temp)) {
        IAST point = (IAST) temp;
        return F.list(//
            F.chopExpr(point.arg1(), Config.DEFAULT_CHOP_DELTA), //
            F.chopExpr(point.arg2(), Config.DEFAULT_CHOP_DELTA));
      }
      return F.NIL;
    }

    /**
     * <code>true</code> if the Weierstrass functions of
     * {@link #weierstrassForm(IExpr, IExpr, IAST)} were reduced to a value. Returning the unreduced
     * form would replace <code>EllipticExp</code> by two special functions which are no simpler,
     * and returning <code>Indeterminate</code> would hide that no value is known.
     *
     * @param expr the evaluated result of {@link #weierstrassForm(IExpr, IExpr, IAST)}
     */
    private static boolean isConcrete(IExpr expr) {
      return expr.isFree(S.WeierstrassP, true) //
          && expr.isFree(S.WeierstrassPPrime, true) //
          && expr.isFree(S.Indeterminate, true);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  private static class EllipticF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();

      return ellipticF(z, m, engine).eval(engine);
    }

    private static IExpr ellipticF(IExpr z, IExpr m, EvalEngine engine) {
      if (z.isZero() || m.isInfinity() || m.isNegativeInfinity()) {
        return F.C0;
      }
      if (m.isZero()) {
        return z;
      }
      if (z.equals(F.CPiHalf)) {
        // EllipticF(Pi/2, m) = EllipticK(m)
        return F.EllipticK(m);
      }
      if (z.isTimes() && z.second().equals(S.Pi) && z.first().isRational()) {
        IRational k = ((IRational) z.first()).multiply(F.C2).normalize();
        if (k.isInteger()) {
          // EllipticF(k*Pi/2, m) = k*EllipticK(m) /; IntegerQ(k)
          return F.Times(k, F.EllipticK(m));
        }
      }
      if (m.isOne()) {
        // Abs(Re(z)) <= Pi/2
        IExpr temp = engine.evaluate(F.Abs(F.Re(z)));
        if (temp.lessEqual(F.CPiHalf).isTrue()) {
          // Log(Sec(z) + Tan(z))
          return F.Log(F.Plus(F.Sec(z), F.Tan(z)));
        }
        if (temp.greater(F.CPiHalf).isTrue()) {
          // if (S.Greater.ofQ(engine, temp, F.CPiHalf)) {
          return F.CComplexInfinity;
        }
      }

      if (engine.isDoubleMode() && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            Complex zc = z.evalfc();
            Complex mc = m.evalfc();
            Complex ellipticF = EllipticIntegralsJS.ellipticF(zc, mc);
            if (F.isZero(ellipticF.getImaginary())) {
              return F.num(ellipticF.getReal());
            }
            return F.complexNum(ellipticF);
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticF(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.EllipticF, rex);
        }
      }

      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        // EllipticF(-z,m) = -EllipticF(z,m)
        return F.Negate(F.EllipticF(negExpr, m));
      }

      // test EllipticF(zz+k*Pi,m)
      IAST parts = AbstractFunctionEvaluator.getPeriodicParts(z, S.Pi);
      if (parts.isPresent()) {
        IExpr k = parts.arg2();
        if (k.isInteger()) {
          // EllipticF(zz,m)+2*k*EllipticK(m)
          IExpr zz = parts.arg1();
          return F.Plus(F.EllipticF(zz, m), F.Times(F.C2, k, F.EllipticK(m)));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class EllipticK extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      return ellipticK(m).eval(engine);
    }

    private static IExpr ellipticK(IExpr m) {
      if (m.isInfinity() || m.isNegativeInfinity() || m.isDirectedInfinity(F.CI)
          || m.isDirectedInfinity(F.CNI)) {
        return F.C0;
      }
      if (m.isZero()) {
        return F.CPiHalf;
      }
      if (m.isOne()) {
        return F.CComplexInfinity;
      }
      if (m.isMinusOne()) {
        // Gamma(1/4)^2/(4*Sqrt(2*Pi))
        return F.Times(F.C1D4, F.C1DSqrt2, F.Power(S.Pi, F.CN1D2), F.Sqr(F.Gamma(F.C1D4)));
      }
      if (m.isNumEqualRational(F.C1D2)) {
        // (8 Pi^(3/2))/Gamma(-(1/4))^2
        return F.Times(F.C8, F.Power(S.Pi, F.QQ(3L, 2L)), F.Power(F.Gamma(F.CN1D4), -2));
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST1()) {
        IInexactNumber m = (IInexactNumber) ast.arg1();
        IExpr temp = ellipticK(m);
        if (temp.isPresent()) {
          return temp.eval(engine);
        }
        return m.ellipticK();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class EllipticLog extends AbstractFunctionEvaluator implements IFunctionExpand {

    /**
     * The elliptic logarithm of the point <code>{x,y}</code> of the curve
     * <code>y^2 == x^3+a*x^2+b*x</code> as the Carlson symmetric form
     * <code>-CarlsonRF(x, x-r1, x-r2)</code>, where <code>r1,r2</code> are the roots of
     * <code>t^2+a*t+b</code>. The sign is the one of the principal square root
     * <code>y == Sqrt(x^3+a*x^2+b*x)</code>.
     *
     * @param x the <code>x</code> coordinate of the point
     * @param a
     * @param b
     */
    private static IExpr carlsonForm(IExpr x, IExpr a, IExpr b) {
      // d == Sqrt(a^2-4*b)
      IExpr d = F.Sqrt(F.Subtract(F.Sqr(a), F.Times(F.C4, b)));
      return F.Negate(F.CarlsonRF(x, //
          // x-r1 == x+(a+d)/2
          F.Plus(x, F.Times(F.C1D2, F.Plus(a, d))), //
          // x-r2 == x+(a-d)/2
          F.Plus(x, F.Times(F.C1D2, F.Subtract(a, d)))));
    }

    /**
     * The sign of the square root <code>Sqrt(x^3+a*x^2+b*x)</code> which is selected by the
     * <code>y</code> coordinate of the point <code>{x,y}</code>.
     *
     * @return <code>1</code> for the principal square root, <code>-1</code> for its negation and
     *         <code>0</code> if <code>{x,y}</code> isn't recognized as a point of the curve
     *         <code>y^2 == x^3+a*x^2+b*x</code>
     */
    private static int principalSqrtSign(IExpr x, IExpr y, IExpr a, IExpr b, EvalEngine engine) {
      // Sqrt(x^3+a*x^2+b*x)
      IExpr sqrt =
          engine.evaluate(F.Sqrt(F.Plus(F.Power(x, F.C3), F.Times(a, F.Sqr(x)), F.Times(b, x))));
      if (sqrt.isZero()) {
        // {x,y} is a point of order 2, both signs give the same half period
        return y.isZero() ? 1 : 0;
      }
      int sign = compare(y, sqrt, engine);
      if (sign == 0) {
        // FunctionExpand() rewrites the y coordinate before it calls
        // functionExpand(IAST, EvalEngine), so compare the same normal form of both square roots
        sign = compare(F.FunctionExpand(y), F.FunctionExpand(sqrt), engine);
      }
      return sign;
    }

    /**
     * Compare the <code>y</code> coordinate of a point with the principal square root
     * <code>Sqrt(x^3+a*x^2+b*x)</code>.
     *
     * @return <code>1</code> if both are equal, <code>-1</code> if they are negatives of each other
     *         and <code>0</code> if that can't be decided
     */
    private static int compare(IExpr y, IExpr sqrt, EvalEngine engine) {
      // compare the quotient, because the coordinates of a point can be large
      IExpr quotient = engine.evaluate(F.Divide(y, sqrt));
      if (isZero(F.Subtract(quotient, F.C1), engine)) {
        return 1;
      }
      if (isZero(F.Plus(quotient, F.C1), engine)) {
        return -1;
      }
      return 0;
    }

    private static boolean isZero(IExpr expr, EvalEngine engine) {
      return F.chopExpr(engine.evaluate(expr), Config.DEFAULT_CHOP_DELTA).isZero();
    }

    /**
     * Evaluate {@link #carlsonForm(IExpr, IExpr, IExpr)} numerically. The <code>CarlsonRF</code>
     * evaluator itself is restricted to {@link EvalEngine#isDoubleMode()}, but like for
     * <code>EllipticExp</code> the numeric mode isn't necessarily set here.
     *
     * @return {@link F#NIL} if the arguments of the Carlson symmetric form aren't inexact numbers
     */
    private static IExpr numericCarlsonForm(IExpr x, IExpr a, IExpr b, EvalEngine engine) {
      // d == Sqrt(a^2-4*b)
      IExpr d = engine.evaluate(F.Sqrt(F.Subtract(F.Sqr(a), F.Times(F.C4, b))));
      IExpr y = engine.evaluate(F.Plus(x, F.Times(F.C1D2, F.Plus(a, d))));
      IExpr z = engine.evaluate(F.Plus(x, F.Times(F.C1D2, F.Subtract(a, d))));
      if (x.isNumber() && y.isNumber() && z.isNumber() //
          && (engine.isDoubleMode() || x.isInexactNumber() || y.isInexactNumber()
              || z.isInexactNumber())) {
        try {
          return F.complexNum(
              EllipticIntegralsJS.carlsonRF(x.evalfc(), y.evalfc(), z.evalfc()).negate());
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.EllipticLog, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList2() && ast.arg2().isList2()) {
        IAST point = (IAST) ast.arg1();
        IAST list = (IAST) ast.arg2();
        int sign = principalSqrtSign(point.arg1(), point.arg2(), list.arg1(), list.arg2(), engine);
        if (sign != 0) {
          IExpr carlsonForm = carlsonForm(point.arg1(), list.arg1(), list.arg2());
          return sign < 0 ? carlsonForm.negate() : carlsonForm;
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList2() || !ast.arg2().isList2()) {
        return F.NIL;
      }
      IAST point = (IAST) ast.arg1();
      IExpr x = point.arg1();
      IExpr y = point.arg2();
      IAST list = (IAST) ast.arg2();
      IExpr a = list.arg1();
      IExpr b = list.arg2();
      if (x.isComplexInfinity() && y.isComplexInfinity()) {
        // the point at infinity is the neutral element of the group law of the curve
        // y^2 == x^3+a*x^2+b*x
        return F.C0;
      }
      if (a.isZero() && b.isZero()) {
        if (x.isZero() && y.isZero()) {
          // the singular point of the curve y^2 == x^3
          return F.NIL;
        }
        IExpr onCurve = engine.evaluate(F.Subtract(F.Sqr(y), F.Power(x, F.C3)));
        if (onCurve.isNumber() && !isZero(onCurve, engine)) {
          // {x,y} isn't a point of the curve y^2 == x^3
          return F.NIL;
        }
        // invert the rational parameterization EllipticExp(u,{0,0}) == {1/u^2,-1/u^3}
        return F.Divide(F.Negate(x), y);
      }
      int sign = principalSqrtSign(x, y, a, b, engine);
      if (sign == 0) {
        return F.NIL;
      }
      IExpr logarithm = numericCarlsonForm(x, a, b, engine);
      if (logarithm.isPresent()) {
        return F.chopExpr(sign < 0 ? logarithm.negate() : logarithm, Config.DEFAULT_CHOP_DELTA);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  /**
   *
   *
   * <pre>
   * EllipticPi(n, m)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * EllipticPi(n, m, z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the complete elliptic integral of the third kind.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href=
   * "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_third_kind">Wikipedia
   * - Elliptic integral - Complete elliptic integral of the third kind</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EllipticPi(n,Pi/2,x)
   * EllipticPi(n,x)
   * </pre>
   */
  private static class EllipticPi extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();

      if (ast.isAST3()) {
        IExpr z = ast.arg2();
        IExpr m = ast.arg3();
        return ellipticPi3(n, z, m, engine.isDoubleMode()).eval(engine);
      }
      IExpr m = ast.arg2();
      return ellipticPi2(n, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr ellipticPi2(IExpr n, IExpr m, boolean doubleMode) {
      if (n.isZero()) {
        return F.EllipticK(m);
      }
      if (n.isOne()) {
        return F.CComplexInfinity;
      }
      if (m.isZero()) {
        // Pi/(2*Sqrt(1-n))
        return F.Times(F.C1D2, F.Power(F.Plus(F.C1, F.Negate(n)), F.CN1D2), S.Pi);
      }
      if (m.isOne()) {
        // -(Infinity/Sign(n-1))
        return F.Times(F.oo, F.Power(F.Sign(F.Plus(F.C1, F.Negate(n))), -1));
      }
      if (n.equals(m)) {
        // EllipticE(n)/(1 - n)
        return F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), -1), F.EllipticE(n));
      }

      if (doubleMode) {
        try {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(nDouble) || Double.isNaN(mDouble)) {
            Complex nc = n.evalfc();
            Complex mc = m.evalfc();
            return F.complexNum(EllipticIntegralsJS.ellipticPi(nc, new Complex(Math.PI / 2.0), mc));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticPi(nDouble, Math.PI / 2.0, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.EllipticPi, rex);
          return F.NIL;
        }
      }

      // if (n.isReal() && m.isReal()) {
      // try {
      // return F.complexNum(EllipticIntegralsJS.ellipticPi(n.evalDouble(), Math.PI / 2,
      // m.evalDouble()));
      // } catch (RuntimeException rte) {
      // return engine.printMessage("EllipticPi: " + rte.getMessage());
      // }
      // } else if (n.isNumeric() && m.isNumeric()) {
      // try {
      // return F.complexNum(EllipticIntegralsJS.ellipticPi(n.evalComplex(), new Complex(Math.PI /
      // 2.0),
      // m.evalComplex()));
      // } catch (RuntimeException rte) {
      // return engine.printMessage("EllipticPi: " + rte.getMessage());
      // }
      // }
      return F.NIL;
    }

    private static IExpr ellipticPi3(IExpr n, IExpr z, final IExpr m, boolean doubleMode) {
      if (doubleMode && z.isNumber() && n.isNumber() && m.isNumber()) {
        try {
          double nDouble = n.evalfNaN();
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            Complex nc = n.evalfc();
            Complex zc = z.evalfc();
            Complex mc = m.evalfc();
            Complex ellipticPi = EllipticIntegralsJS.ellipticPi(nc, zc, mc);
            if (F.isZero(ellipticPi.getImaginary())) {
              return F.num(ellipticPi.getReal());
            }
            return F.complexNum(ellipticPi);
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticPi(nDouble, zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.EllipticPi, rex);
          return F.NIL;
        }
      }
      if (z.equals(F.CPiHalf)) {
        if (n.isZero()) {
          // EllipticPi(0,Pi/2,z) = EllipticK(z)
          return F.EllipticK(m);
        }
        if (n.equals(m)) {
          // EllipticPi(n,Pi/2,n) = EllipticE(n)/(1-n)
          return F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), -1), F.EllipticE(n));
        }
        return F.EllipticPi(n, m);
      }
      if (n.isZero()) {
        return F.EllipticF(z, m);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class EllipticTheta extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      int a = n.toIntDefault();
      if (ast.isAST3()) {
        IExpr x = ast.arg2();
        IExpr m = ast.arg3();
        return ellipticTheta3(a, x, m, engine).eval(engine);
      }

      IExpr m = ast.arg2();
      return ellipticTheta2(a, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr ellipticTheta2(int a, IExpr m, boolean doubleMode) {
      if (a >= 1 && a <= 4) {
        if (m.isZero()) {
          switch (a) {
            case 1:
            case 2:
              return F.C0;
            case 3:
            case 4:
              return F.C1;
          }
        }
        if (doubleMode && m.isNumber()) {
          if (m.isReal()) {
            try {
              return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, 0.0, m.evalfNaN()));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.EllipticTheta, rex);
            }
          } else if (m.isInexactNumber()) {
            try {
              return F.complexNum(EllipticFunctionsJS.jacobiTheta(a,
                  org.hipparchus.complex.Complex.ZERO, m.evalfc()));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.EllipticTheta, rex);
            }
          }
        }
      }
      return F.NIL;
    }

    private static IExpr ellipticTheta3(int a, IExpr x, IExpr m, EvalEngine engine) {
      if (a >= 1 && a <= 4) {
        if (m.isZero()) {
          switch (a) {
            case 1:
            case 2:
              return F.C0;
            case 3:
            case 4:
              return F.C1;
          }
        } else if (a == 1) {
          if (x.isZero() || (x.isPi() && m.isNumEqualRational(F.C1D2))) {
            return F.C0;
          }
        }
        if (engine.isDoubleMode() && x.isNumber() && m.isNumber()) {
          if (x.isReal() && m.isReal()) {
            try {
              return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, x.evalfNaN(), m.evalfNaN()));
            } catch (ValidateException ve) {
              throw ve;
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.EllipticTheta, rex);
            }
          } else if (x.isInexactNumber() && m.isInexactNumber()) {
            try {
              return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, x.evalfc(), m.evalfc()));
            } catch (ValidateException ve) {
              throw ve;
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              Errors.printMessage(S.EllipticTheta, rex);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDFIRST | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  // private static class InverseWeierstrassP extends AbstractFunctionEvaluator {
  //
  // @Override
  // public IExpr evaluate(IAST ast, EvalEngine engine) {
  // IExpr u = ast.arg1();
  // if (ast.arg2().isVector() == 2) {
  // IAST list = (IAST) ast.arg2();
  // IExpr g2 = list.arg1();
  // IExpr g3 = list.arg2();
  // if (u.isNumeric() && g2.isNumeric() && g3.isNumeric()) {
  // try {
  // return F.complexNum(
  // EllipticFunctionsJS.inverseWeierstrassP(u.evalComplex(), g2.evalComplex(), g3.evalComplex()));
  // } catch (RuntimeException rte) {
  // return engine.printMessage("InverseWeierstrassP: " + rte.getMessage());
  // }
  // }
  // }
  //
  // return F.NIL;
  // }
  //
  // @Override
  // public int[] expectedArgSize() {
  // return IOFunctions.ARGS_2_2;
  // }
  //
  // @Override
  // public void setUp(final ISymbol newSymbol) {
  // newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
  // super.setUp(newSymbol);
  // }
  // }


  private static class InverseJacobiCD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiCD(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiCD(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.ArcCos(z);
      }
      if (z.isZero()) {
        return F.EllipticK(m);
      }
      if (z.isOne()) {
        return F.C0;
      }
      if (m.isInfinity() || m.isNegativeInfinity()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiCD(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiCD(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiCD, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseJacobiCN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiCN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiCN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.ArcCos(z);
      }
      if (m.isOne()) {
        return F.ArcSech(z);
      }
      if (z.isMinusOne()) {
        return F.Times(F.C2, F.EllipticK(m));
      }
      if (z.isZero()) {
        return F.EllipticK(m);
      }
      if (z.isOne()) {
        return F.C0;
      }
      if (m.isInfinity() || m.isNegativeInfinity()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiCN(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiCN(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiCN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseJacobiDN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiDN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiDN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isOne()) {
        return F.ArcSech(z);
      }
      if (z.isOne()) {
        return F.C0;
      }
      if (m.isInfinity() || m.isNegativeInfinity()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiDN(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiDN(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiDN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseJacobiSC extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiSC(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiSC(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.ArcTan(z);
      }
      if (m.isOne()) {
        return F.ArcSinh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isImaginaryUnit()) {
        return F.Times(F.CI, F.EllipticK(F.Subtract(F.C1, m)));
      }
      if (z.isNegativeImaginaryUnit()) {
        return F.Times(F.CNI, F.EllipticK(F.Subtract(F.C1, m)));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return F.Negate(F.InverseJacobiSC(negExpr, m));
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiSC(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiSC(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiSC, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseJacobiSD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiSD(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiSD(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.ArcSin(z);
      }
      if (m.isOne()) {
        return F.ArcSinh(z);
      }
      if (z.isZero() || z.isInfinity() || z.isNegativeInfinity()) {
        return F.C0;
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return F.Negate(F.InverseJacobiSD(negExpr, m));
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiSD(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiSD(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiSD, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseJacobiSN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return inverseJacobiSN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr inverseJacobiSN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.ArcSin(z);
      }
      if (m.isOne()) {
        return F.ArcTanh(z);
      }
      if (z.isMinusOne()) {
        return F.Negate(F.EllipticK(m));
      }
      if (z.isZero() || z.isInfinity() || z.isNegativeInfinity()) {
        return F.C0;
      }
      if (z.isOne()) {
        return F.EllipticK(m);
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return F.Negate(F.InverseJacobiSN(negExpr, m));
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          // double zDouble = Double.NaN;
          // double mDouble = Double.NaN;
          // try {
          // zDouble = z.evalDouble();
          // mDouble = m.evalDouble();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.inverseJacobiSN(z.evalfc(), m.evalfc()));
          // } else {
          // return F.num(EllipticFunctionsJS.inverseJacobiSN(zDouble, mDouble));
          // }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.InverseJacobiSN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }



  /**
   *
   *
   * <pre>
   * <code>JacobiAmplitude(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the amplitude <code>am(x, m)</code> for Jacobian elliptic function.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.16">NIST - Jacobi’s Amplitude (am) Function</a>
   * </ul>
   */
  private static class JacobiAmplitude extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiAmplitude(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiAmplitude(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return z;
      }
      if (m.isOne()) {
        // return F.Plus(F.CNPiHalf, F.Times(2, F.ArcTan(F.Power(S.E, z))));
        return F.Gudermannian(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (F.EllipticK(m).equals(z)) {
        return F.CPiHalf;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          if (z.isReal() && m.isReal()) {
            return F.complexNum(EllipticFunctionsJS.jacobiAmplitude(z.evalfNaN(), m.evalfNaN()));
          }
          return F.complexNum(EllipticFunctionsJS.jacobiAmplitude(z.evalfc(), m.evalfc()));
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.JacobiAmplitude, rex);
        }
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return F.Negate(F.JacobiAmplitude(negExpr, m));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiCD(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>cd(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiCD(10.0,1/3)
   * -0.945268
   * </code>
   * </pre>
   */
  private static class JacobiCD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiCD(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiCD(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.Cos(z);
      }
      if (m.isOne() || z.isZero()) {
        return F.C1;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiCD(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiCD(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiCD, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiCN(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>cn(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiCN(10.0,1/3)
   * -0.92107
   * </code>
   * </pre>
   */
  private static class JacobiCN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiCN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiCN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.Cos(z);
      }
      if (m.isOne()) {
        return F.Sech(z);
      }
      if (z.isZero()) {
        return F.C1;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiCN(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiCN(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiCN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiDN(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>dn(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiDN(10.0,1/3)
   * 0.974401
   * </code>
   * </pre>
   */
  private static class JacobiDN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiDN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiDN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.C1;
      }
      if (m.isOne()) {
        return F.Sech(z);
      }
      if (z.isZero()) {
        return F.C1;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiDN(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiDN(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiDN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiSC(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>sc(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiSC(10.0,1/3)
   * -0.422766
   * </code>
   * </pre>
   */
  private static class JacobiSC extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiSC(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiSC(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.Tan(z);
      }
      if (m.isOne()) {
        return F.Sinh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSC(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSC(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiSC, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiSD(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>sd(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiSD(10.0,1/3)
   * 0.399627
   * </code>
   * </pre>
   */
  private static class JacobiSD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiSD(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiSD(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.Sin(z);
      }
      if (m.isOne()) {
        return F.Sinh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSD(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSD(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiSD, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JacobiSN(x, m)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Jacobian elliptic function <code>sn(x, m)</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   * elliptic functions</a>
   * <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; JacobiSN(10.0,1/3)
   * 0.389397
   * </code>
   * </pre>
   */
  private static class JacobiSN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiSN(z, m, engine.isDoubleMode()).eval(engine);
    }

    private static IExpr jacobiSN(IExpr z, IExpr m, boolean doubleMode) {
      if (m.isZero()) {
        return F.Sin(z);
      }
      if (m.isOne()) {
        return F.Tanh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (doubleMode && z.isNumber() && m.isNumber()) {
        try {
          double zDouble = z.evalfNaN();
          double mDouble = m.evalfNaN();
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSN(z.evalfc(), m.evalfc()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSN(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          throw ve;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.JacobiSN, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class JacobiZeta extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      return jacobiZeta(z, m, engine).eval(engine);
    }

    private static IExpr jacobiZeta(IExpr z, IExpr m, EvalEngine engine) {
      if (m.isZero()) {
        return F.C0;
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (z.equals(F.CPiHalf)) {
        return F.C0;
      }
      if (m.isOne()) {
        // Abs(Re(z)) <= Pi/2
        if (engine.evalLessEqual(F.Abs(F.Re(z)), F.CPiHalf)) {
          return F.Sin(z);
        }
      }
      if (m.isInfinity() || m.isNegativeInfinity()) {
        return F.CComplexInfinity;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class KleinInvariantJ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr t = ast.arg1();
      return kleinInvariantJ(t, engine.isNumericMode()).eval(engine);
    }

    private static IExpr kleinInvariantJ(IExpr t, boolean numericMode) {
      IExpr im = t.im();

      if (im.isZero()) {
        return F.NIL;
      }
      if (im.isOne()) {
        IExpr re = t.re();
        if (re.isInteger()) {
          // KleinInvariantJ(re+I) = 1 and re is Integer
          return F.C1;
        }
        int r = re.toIntDefault();
        if (F.isPresent(r)) {
          // KleinInvariantJ(re+I) = 1 and re is Integer
          return F.C1;
        }
      }
      if (numericMode && t.isNumber()) {
        try {
          double tDouble = t.evalfNaN();
          if (Double.isNaN(tDouble)) {
            Complex tComplex = t.evalfc();
            return F.complexNum(EllipticIntegralsJS.kleinJ(tComplex));
          } else {
            return F.complexNum(EllipticIntegralsJS.kleinJ(tDouble));
          }
        } catch (ArithmeticException ae) {
          // unevaluated
          return F.NIL;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class WeierstrassHalfPeriods extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isList2()) {
        IAST list = (IAST) ast.arg1();
        IExpr g2 = list.arg1();
        IExpr g3 = list.arg2();
        return weierstrassHalfPeriods(g2, g3).eval(engine);
      }
      return F.NIL;
    }

    private static IExpr weierstrassHalfPeriods(IExpr g2, IExpr g3) {
      // numeric mode isn't set here
      if (g2.isInexactNumber() || g3.isInexactNumber()) {
        if (g2.isNumber() && g3.isNumber()) {
          try {
            org.hipparchus.complex.Complex[] invariants =
                EllipticFunctionsJS.weierstrassHalfPeriods(g2.evalfc(), g3.evalfc());
            return Object2Expr.convertComplex(false, invariants);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            Errors.printMessage(S.WeierstrassHalfPeriods, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class WeierstrassInvariants extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isList2()) {
        IAST list = (IAST) ast.arg1();
        IExpr g2 = list.arg1();
        IExpr g3 = list.arg2();
        return weierstrassInvariants(g2, g3).eval(engine);
      }
      return F.NIL;
    }

    private static IExpr weierstrassInvariants(IExpr g2, IExpr g3) {
      // numeric mode isn't set here
      if (g2.isInexactNumber() || g3.isInexactNumber()) {
        if (g2.isNumber() && g3.isNumber()) {
          try {
            org.hipparchus.complex.Complex[] invariants =
                EllipticFunctionsJS.weierstrassInvariants(g2.evalfc(), g3.evalfc());
            return Object2Expr.convertComplex(false, invariants);
          } catch (ValidateException ve) {
            throw ve;
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            Errors.printMessage(S.WeierstrassInvariants, rex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * The parameters <code>{e3, e1-e3, (e2-e3)/(e1-e3)}</code> which express the Weierstrass
   * functions of the invariants <code>{g2,g3}</code> by Jacobi elliptic functions, where
   * <code>e1,e2,e3</code> are the roots of <code>4*t^3-g2*t-g3</code>. They are returned in this
   * reduced form, because the differences of the roots simplify much further than the roots
   * themselves. Available for the two families of invariants whose cubic factors:
   *
   * <ul>
   * <li><code>g3 == 0</code> - the lemniscatic case
   * <code>4*t^3-g2*t == 4*t*(t-Sqrt(g2)/2)*(t+Sqrt(g2)/2)</code>, with the roots
   * <code>{Sqrt(g2)/2, 0, -Sqrt(g2)/2}</code>
   * <li><code>g2 == 0</code> - the equianharmonic case, where the roots are the cube roots
   * <code>c*{1, (-1+I*Sqrt(3))/2, (-1-I*Sqrt(3))/2}</code> of <code>c^3 == g3/4</code>
   * </ul>
   *
   * <p>
   * For all other invariants the roots are the general solutions of a cubic equation, which are too
   * unwieldy to expand into; this method returns {@link F#NIL} for them.
   *
   * @param g2 the first Weierstrass invariant
   * @param g3 the second Weierstrass invariant
   * @return <code>F.NIL</code> if the roots of <code>4*t^3-g2*t-g3</code> aren't available in
   *         closed form
   */
  private static IAST weierstrassJacobiParameters(IExpr g2, IExpr g3) {
    if (g2.isZero() && g3.isZero()) {
      // all three roots are 0 - WeierstrassP() and WeierstrassPPrime() are rational functions of z
      // here and are already rewritten in their evaluate() step
      return F.NIL;
    }
    if (g3.isZero()) {
      IExpr sqrtG2 = F.Sqrt(g2);
      return F.list(F.Times(F.CN1D2, sqrtG2), sqrtG2, F.C1D2);
    }
    if (g2.isZero()) {
      IExpr c = F.Power(F.Times(F.C1D4, g3), F.C1D3);
      return F.list(//
          // e3 == (-1-I*Sqrt(3))/2*c
          F.Times(F.C1D2, F.Plus(F.CN1, F.Times(F.CNI, F.CSqrt3)), c), //
          // e1-e3 == (3+I*Sqrt(3))/2*c
          F.Times(F.C1D2, F.Plus(F.C3, F.Times(F.CI, F.CSqrt3)), c), //
          // (e2-e3)/(e1-e3) == (1+I*Sqrt(3))/2
          F.Times(F.C1D2, F.Plus(F.C1, F.Times(F.CI, F.CSqrt3))));
    }
    return F.NIL;
  }

  /**
   * Whittaker &amp; Watson, Section 22.351:
   * <code>WeierstrassP(z,{g2,g3}) == e3+(e1-e3)/JacobiSN(Sqrt(e1-e3)*z,m)^2</code>.
   *
   * @param z
   * @param parameters the <code>{e3, e1-e3, m}</code> of
   *        {@link #weierstrassJacobiParameters(IExpr, IExpr)}
   */
  private static IExpr weierstrassPJacobi(IExpr z, IAST parameters) {
    IExpr e3 = parameters.arg1();
    IExpr e13 = parameters.arg2();
    IExpr m = parameters.arg3();
    return F.Plus(e3, F.Times(e13, F.Power(F.JacobiSN(F.Times(F.Sqrt(e13), z), m), F.CN2)));
  }

  /**
   * The derivative of {@link #weierstrassPJacobi(IExpr, IAST)}:
   * <code>WeierstrassPPrime(z,{g2,g3}) ==
   * -2*(e1-e3)^(3/2)*JacobiCN(w,m)*JacobiDN(w,m)/JacobiSN(w,m)^3</code> with
   * <code>w == Sqrt(e1-e3)*z</code>.
   *
   * @param z
   * @param parameters the <code>{e3, e1-e3, m}</code> of
   *        {@link #weierstrassJacobiParameters(IExpr, IExpr)}
   */
  private static IExpr weierstrassPPrimeJacobi(IExpr z, IAST parameters) {
    IExpr e13 = parameters.arg2();
    IExpr m = parameters.arg3();
    IExpr w = F.Times(F.Sqrt(e13), z);
    return F.Times(F.CN2, F.Power(e13, F.C3D2), F.JacobiCN(w, m), F.JacobiDN(w, m),
        F.Power(F.JacobiSN(w, m), F.CN3));
  }

  private static class WeierstrassP extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList2()) {
        IAST list = (IAST) ast.arg2();
        IAST parameters = weierstrassJacobiParameters(list.arg1(), list.arg2());
        if (parameters.isPresent()) {
          return weierstrassPJacobi(ast.arg1(), parameters);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr u = ast.arg1();
      if (u.isZero()) {
        return F.CComplexInfinity;
      }
      if (ast.arg2().isList2()) {
        IAST list = (IAST) ast.arg2();
        IExpr g2 = list.arg1();
        IExpr g3 = list.arg2();
        return weierstrassP(u, g2, g3).eval(engine);
      }
      return F.NIL;
    }

    private static IExpr weierstrassP(IExpr u, IExpr g2, IExpr g3) {
      if (g2.isZero() && g3.isZero()) {
        return F.Power(u, F.CN2);
      }
      if (g2.isNumEqualInteger(F.C3) && g3.isOne()) {
        // 1 + (3/2) Cot(Sqrt(3/2)*u)^2
        return F.Plus(F.C1, F.Times(F.C3D2, F.Sqr(F.Cot(F.Times(F.Sqrt(F.C3D2), u)))));
      }
      // numeric mode isn't set here
      if (u.isInexactNumber() && g2.isNumber() && g3.isNumber()) {
        try {
          return F
              .complexNum(EllipticFunctionsJS.weierstrassP(u.evalfc(), g2.evalfc(), g3.evalfc()));
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.WeierstrassP, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class WeierstrassPPrime extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList2()) {
        IAST list = (IAST) ast.arg2();
        IAST parameters = weierstrassJacobiParameters(list.arg1(), list.arg2());
        if (parameters.isPresent()) {
          return weierstrassPPrimeJacobi(ast.arg1(), parameters);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr u = ast.arg1();
      if (u.isZero()) {
        return F.CComplexInfinity;
      }
      if (ast.arg2().isList2()) {
        IAST list = (IAST) ast.arg2();
        IExpr g2 = list.arg1();
        IExpr g3 = list.arg2();
        return weierstrassPPrime(u, g2, g3).eval(engine);
      }
      return F.NIL;
    }

    private static IExpr weierstrassPPrime(IExpr u, IExpr g2, IExpr g3) {
      if (g2.isZero() && g3.isZero()) {
        return F.Times(F.CN2, F.Power(u, F.CN3));
      }
      if (g2.isNumEqualInteger(F.C3) && g3.isOne()) {
        // -3 * Sqrt(3/2) * Cot(Sqrt(3/2)*u) * Csc(Sqrt(3/2)*u)^2
        return F.Times(F.CN3, F.Sqrt(F.C3D2), F.Cot(F.Times(F.Sqrt(F.C3D2), u)),
            F.Sqr(F.Csc(F.Times(F.Sqrt(F.C3D2), u))));
      }
      // numeric mode isn't set here
      if (u.isInexactNumber() && g2.isNumber() && g3.isNumber()) {
        try {
          return F.complexNum(
              EllipticFunctionsJS.weierstrassPPrime(u.evalfc(), g2.evalfc(), g3.evalfc()));
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.WeierstrassPPrime, rex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private EllipticIntegrals() {}
}
