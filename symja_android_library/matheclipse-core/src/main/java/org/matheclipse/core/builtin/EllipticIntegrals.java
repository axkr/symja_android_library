package org.matheclipse.core.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.complex.Complex;
import org.hipparchus.special.elliptic.carlson.CarlsonEllipticIntegral;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.EllipticFunctionsJS;
import org.matheclipse.core.builtin.functions.EllipticIntegralsJS;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.JacobiSCRules;
import org.matheclipse.core.reflection.system.rules.KleinInvariantJRules;

public class EllipticIntegrals {
  private static final Logger LOGGER = LogManager.getLogger();

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
      S.EllipticF.setEvaluator(new EllipticF());
      S.EllipticK.setEvaluator(new EllipticK());
      S.EllipticPi.setEvaluator(new EllipticPi());
      S.EllipticTheta.setEvaluator(new EllipticTheta());

      // S.InverseWeierstrassP.setEvaluator(new InverseWeierstrassP());
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
   * <pre><code>CarlsonRC(x, y)
   * </code></pre>
   *
   * <p>returns the Carlson RC function..
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   *       symmetric form</a>
   * </ul>
   */
  private static class CarlsonRC extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      if (x.equals(y)) {
        IExpr reCondition = S.LessEqual.of(engine, F.Re(x), F.C0);
        IExpr imCondition = S.Equal.of(engine, F.Im(x), F.C0);
        if (reCondition.isTrue() && imCondition.isTrue()) {
          return F.CComplexInfinity;
        }
        if (reCondition.isFalse() || imCondition.isFalse()) {
          // 1 / Sqrt(x)
          return F.Power.of(engine, x, F.CN1D2);
        }
        return F.Piecewise(
            F.List(
                F.List(
                    F.CComplexInfinity, F.And(F.LessEqual(F.Re(x), F.C0), F.Equal(F.Im(x), F.C0)))),
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
        double xd = Double.NaN;
        double yd = Double.NaN;
        try {
          xd = x.evalDouble();
          yd = y.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(xd) || Double.isNaN(yd)) {
          Complex xc = x.evalComplex();
          Complex yc = y.evalComplex();
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
   * <pre><code>CarlsonRD(x, y, z)
   * </code></pre>
   *
   * <p>returns the Carlson RD function.
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   *       symmetric form</a>
   * </ul>
   */
  private static class CarlsonRD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      if (engine.isDoubleMode()) {
        double xd = Double.NaN;
        double yd = Double.NaN;
        double zd = Double.NaN;
        try {
          xd = x.evalDouble();
          yd = y.evalDouble();
          zd = z.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
          Complex xc = x.evalComplex();
          Complex yc = y.evalComplex();
          Complex zc = z.evalComplex();
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
   * <pre><code>CarlsonRF(x, y, z)
   * </code></pre>
   *
   * <p>returns the Carlson RF function.
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   *       symmetric form</a>
   * </ul>
   */
  private static class CarlsonRF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      if (x.equals(y) && x.equals(z)) {
        // 1 / Sqrt(x)
        return F.Power.of(engine, x, F.CN1D2);
      }
      if (engine.isDoubleMode()) {
        double xd = Double.NaN;
        double yd = Double.NaN;
        double zd = Double.NaN;
        try {
          xd = x.evalDouble();
          yd = y.evalDouble();
          zd = z.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
          Complex xc = x.evalComplex();
          Complex yc = y.evalComplex();
          Complex zc = z.evalComplex();
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
   * <pre><code>CarlsonRG(x, y, z)
   * </code></pre>
   *
   * <p>returns the Carlson RG function.
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   *       symmetric form</a>
   * </ul>
   */
  private static class CarlsonRG extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      if (x.equals(y)) {
        if (x.equals(z)) {
          // 1 / Sqrt(x)
          return F.Power.of(engine, x, F.CN1D2);
        }
        // (1/2) * (Sqrt(z) + x*CarlsonRF(x,x,z))
        return S.Times.of(engine, F.C1D2, F.Plus(F.Sqrt(z), F.Times(x, F.CarlsonRF(x, x, z))));
      }
      if (engine.isDoubleMode()) {
        try {
          double xd = Double.NaN;
          double yd = Double.NaN;
          double zd = Double.NaN;
          try {
            xd = x.evalDouble();
            yd = y.evalDouble();
            zd = z.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd)) {
            Complex xc = x.evalComplex();
            Complex yc = y.evalComplex();
            Complex zc = z.evalComplex();
            return F.complexNum(EllipticIntegralsJS.carlsonRG(xc, yc, zc));
          } else {
            return F.num(EllipticIntegralsJS.carlsonRG(xd, yd, zd));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>CarlsonRJ(x, y, z, p)
   * </code></pre>
   *
   * <p>returns the Carlson RJ function.
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Carlson_symmetric_form">Wikipedia - Carlson
   *       symmetric form</a>
   * </ul>
   */
  private static class CarlsonRJ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      IExpr y = ast.arg2();
      IExpr z = ast.arg3();
      IExpr p = ast.arg4();
      if (engine.isDoubleMode()) {
        try {
          double xd = Double.NaN;
          double yd = Double.NaN;
          double zd = Double.NaN;
          double pd = Double.NaN;
          try {
            xd = x.evalDouble();
            yd = y.evalDouble();
            zd = z.evalDouble();
            pd = p.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(xd) || Double.isNaN(yd) || Double.isNaN(zd) || Double.isNaN(pd)) {
            Complex xc = x.evalComplex();
            Complex yc = y.evalComplex();
            Complex zc = z.evalComplex();
            Complex pc = p.evalComplex();
            return F.complexNum(EllipticIntegralsJS.carlsonRJ(xc, yc, zc, pc));
          } else {
            return F.num(EllipticIntegralsJS.carlsonRJ(xd, yd, zd, pd));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
  /**
   *
   *
   * <pre>
   * EllipticE(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the complete elliptic integral of the second kind.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href=
   *       "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_second_kind">Wikipedia
   *       - Elliptic integral - Complete elliptic integral of the second kind)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EllipticE(5/4,1)
   * Sin(5/4)
   * </pre>
   */
  private static class EllipticE extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (ast.isAST2()) {
        IExpr m = ast.arg2();
        if (m.isZero()) {
          return z;
        }
        if (z.isZero()) {
          return F.C0;
        }
        if (m.isOne()) {
          // Abs(Re(z)) <= Pi/2
          if (engine.evalTrue(F.LessEqual(F.Abs(F.Re(z)), F.CPiHalf))) {
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

        if (engine.isDoubleMode()) {
          try {
            double zDouble = Double.NaN;
            double mDouble = Double.NaN;
            try {
              zDouble = z.evalDouble();
              mDouble = m.evalDouble();
            } catch (ValidateException ve) {
            }
            if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
              Complex zc = z.evalComplex();
              Complex mc = m.evalComplex();
              return F.complexNum(EllipticIntegralsJS.ellipticE(zc, mc));
            } else {
              return F.complexNum(EllipticIntegralsJS.ellipticE(zDouble, mDouble));
            }
          } catch (ValidateException ve) {
            if (Config.SHOW_STACKTRACE) {
              ve.printStackTrace();
            }
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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

      if (z.isZero()) {
        // Pi/2
        return F.CPiHalf;
      }
      if (z.isOne()) {
        return F.C1;
      }
      if (z.isNumEqualRational(F.C1D2)) {
        // (Pi^2 + 2 Gamma(3/4)^4)/(4*Sqrt(Pi)*Gamma(3/4)^2)
        return F.Times(
            F.C1D4,
            F.Power(S.Pi, F.CN1D2),
            F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
            F.Plus(F.Sqr(S.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
      }
      if (z.isMinusOne()) {
        // (Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2)*Sqrt(Pi)*Gamma(3/4)^2)
        return F.Times(
            F.C1D2,
            F.C1DSqrt2,
            F.Power(S.Pi, F.CN1D2),
            F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
            F.Plus(F.Sqr(S.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
      }

      if (engine.isDoubleMode()) {
        try {
          double zDouble = Double.NaN;
          try {
            zDouble = z.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(zDouble)) {
            Complex zc = z.evalComplex();
            return F.complexNum(EllipticIntegralsJS.ellipticE(new Complex(Math.PI / 2.0), zc));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticE(Math.PI / 2.0, zDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
        }
      }

      if (z.isInfinity() || z.isNegativeInfinity() || z.isComplexInfinity()) {
        return F.CComplexInfinity;
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

  /**
   *
   *
   * <pre>
   * EllipticF(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the incomplete elliptic integral of the first kind.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href=
   *       "https://en.wikipedia.org/wiki/Elliptic_integral#Incomplete_elliptic_integral_of_the_first_kind">Wikipedia
   *       - Elliptic integral - Incomplete elliptic integral of the first kind)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EllipticF(17/2*Pi, m)
   * 17*EllipticK(m)
   * </pre>
   */
  private static class EllipticF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
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
        if (S.LessEqual.ofQ(engine, temp, F.CPiHalf)) {
          // Log(Sec(z) + Tan(z))
          return F.Log(F.Plus(F.Sec(z), F.Tan(z)));
        }
        if (S.Greater.ofQ(engine, temp, F.CPiHalf)) {
          return F.CComplexInfinity;
        }
      }

      if (engine.isDoubleMode()) {
        try {
          double zDouble = Double.NaN;
          double mDouble = Double.NaN;
          try {
            zDouble = z.evalDouble();
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            Complex zc = z.evalComplex();
            Complex mc = m.evalComplex();
            return F.complexNum(EllipticIntegralsJS.ellipticF(zc, mc));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticF(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
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

  /**
   *
   *
   * <pre>
   * EllipticK(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the complete elliptic integral of the first kind.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href=
   *       "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_first_kind">Wikipedia
   *       - Elliptic integral - Complete elliptic integral of the first kind)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Table(EllipticK(x+I), {x,-1.0, 1.0, 1/4})
   * {1.26549+I*0.16224,1.30064+I*0.18478,1.33866+I*0.21305,1.37925+I*0.24904,1.42127+I*0.29538,1.46203+I*0.35524,1.49611+I*0.43136,1.51493+I*0.52354,1.50924+I*0.62515}
   * </pre>
   */
  private static class EllipticK extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      if (m.isInfinity()
          || m.isNegativeInfinity()
          || m.isDirectedInfinity(F.CI)
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

      if (engine.isDoubleMode()) {
        try {
          double mDouble = Double.NaN;
          try {
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(mDouble)) {
            Complex mComplex = m.evalComplex();
            return F.complexNum(EllipticIntegralsJS.ellipticK(mComplex));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticK(mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
        }
      }
      if (m.isNumber()) {
        // EllipticK(m_) := Pi/(2*ArithmeticGeometricMean(1,Sqrt(1-m)))
        return F.Times(
            F.C1D2,
            S.Pi,
            F.Power(F.ArithmeticGeometricMean(F.C1, F.Sqrt(F.Plus(F.C1, F.Negate(m)))), -1));
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

  /**
   *
   *
   * <pre>
   * EllipticPi(n, m)
   * </pre>
   *
   * <p>or
   *
   * <pre>
   * EllipticPi(n, m, z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the complete elliptic integral of the third kind.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href=
   *       "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_third_kind">Wikipedia
   *       - Elliptic integral - Complete elliptic integral of the third kind</a>
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

        if (engine.isDoubleMode()) {
          try {
            double nDouble = Double.NaN;
            double zDouble = Double.NaN;
            double mDouble = Double.NaN;
            try {
              nDouble = n.evalDouble();
              zDouble = z.evalDouble();
              mDouble = m.evalDouble();
            } catch (ValidateException ve) {
            }
            if (Double.isNaN(nDouble) || Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
              Complex nc = n.evalComplex();
              Complex zc = z.evalComplex();
              Complex mc = m.evalComplex();
              return F.complexNum(EllipticIntegralsJS.ellipticPi(nc, zc, mc));
            } else {
              return F.complexNum(EllipticIntegralsJS.ellipticPi(nDouble, zDouble, mDouble));
            }
          } catch (ValidateException ve) {
            if (Config.SHOW_STACKTRACE) {
              ve.printStackTrace();
            }
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
            return F.NIL;
          }
        }
        if (z.equals(F.CPiHalf)) {
          if (n.isZero()) {
            // EllipticPi(0,Pi/2,z) = EllipticK(z)
            return F.EllipticK(ast.arg3());
          }
          if (n.equals(ast.arg3())) {
            // EllipticPi(n,Pi/2,n) = EllipticE(n)/(1-n)
            return F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), -1), F.EllipticE(n));
          }
          return F.EllipticPi(n, ast.arg3());
        }
        if (n.isZero()) {
          return F.EllipticF(z, ast.arg3());
        }
        return F.NIL;
      }
      IExpr m = ast.arg2();
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

      if (engine.isDoubleMode()) {
        try {
          double nDouble = Double.NaN;
          double mDouble = Double.NaN;
          try {
            nDouble = n.evalDouble();
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(nDouble) || Double.isNaN(mDouble)) {
            Complex nc = n.evalComplex();
            Complex mc = m.evalComplex();
            return F.complexNum(EllipticIntegralsJS.ellipticPi(nc, new Complex(Math.PI / 2.0), mc));
          } else {
            return F.complexNum(EllipticIntegralsJS.ellipticPi(nDouble, Math.PI / 2.0, mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
          if (x.isReal() && m.isReal()) {
            try {
              return F.complexNum(
                  EllipticFunctionsJS.jacobiTheta(a, x.evalDouble(), m.evalDouble()));
            } catch (RuntimeException rex) {
              LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
            }
          } else if (x.isInexactNumber() && m.isInexactNumber()) {
            try {
              return F.complexNum(
                  EllipticFunctionsJS.jacobiTheta(a, x.evalComplex(), m.evalComplex()));
            } catch (RuntimeException rex) {
              LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
            }
          }
        }
        return F.NIL;
      }

      IExpr m = ast.arg2();
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
        if (m.isReal()) {
          try {
            return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, 0.0, m.evalDouble()));
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        } else if (m.isInexactNumber()) {
          try {
            return F.complexNum(
                EllipticFunctionsJS.jacobiTheta(
                    a, org.hipparchus.complex.Complex.ZERO, m.evalComplex()));
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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

  /**
   *
   *
   * <pre><code>JacobiAmplitude(x, m)
   * </code></pre>
   *
   * <p>returns the amplitude <code>am(x, m)</code> for Jacobian elliptic function.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.16">NIST - Jacobi’s Amplitude (am) Function</a>
   * </ul>
   */
  private static class JacobiAmplitude extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return z;
      }
      if (m.isOne()) {
        return F.Plus(F.CNPiHalf, F.Times(2, F.ArcTan(F.Power(S.E, z))));
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (F.EllipticK(m).equals(z)) {
        return F.CPiHalf;
      }
      if (z.isInexactNumber() && m.isInexactNumber()) {
        try {
          if (z.isReal() && m.isReal()) {
            return F.complexNum(
                EllipticFunctionsJS.jacobiAmplitude(z.evalDouble(), m.evalDouble()));
          }
          return F.complexNum(
              EllipticFunctionsJS.jacobiAmplitude(z.evalComplex(), m.evalComplex()));
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
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
   * <pre><code>JacobiCD(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>cd(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiCD(10.0,1/3)
   * -0.945268
   * </code></pre>
   */
  private static class JacobiCD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.Cos(z);
      }
      if (m.isOne() || z.isZero()) {
        return F.C1;
      }
      try {
        double zDouble = Double.NaN;
        double mDouble = Double.NaN;
        try {
          zDouble = z.evalDouble();
          mDouble = m.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.jacobiCD(z.evalComplex(), m.evalComplex()));
        } else {
          return F.num(EllipticFunctionsJS.jacobiCD(zDouble, mDouble));
        }
      } catch (ValidateException ve) {
        if (Config.SHOW_STACKTRACE) {
          ve.printStackTrace();
        }
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>JacobiCN(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>cn(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiCN(10.0,1/3)
   * -0.92107
   * </code></pre>
   */
  private static class JacobiCN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.Cos(z);
      }
      if (m.isOne()) {
        return F.Sech(z);
      }
      if (z.isZero()) {
        return F.C1;
      }
      try {
        double zDouble = Double.NaN;
        double mDouble = Double.NaN;
        try {
          zDouble = z.evalDouble();
          mDouble = m.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.jacobiCN(z.evalComplex(), m.evalComplex()));
        } else {
          return F.num(EllipticFunctionsJS.jacobiCN(zDouble, mDouble));
        }
      } catch (ValidateException ve) {
        if (Config.SHOW_STACKTRACE) {
          ve.printStackTrace();
        }
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>JacobiDN(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>dn(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiDN(10.0,1/3)
   * 0.974401
   * </code></pre>
   */
  private static class JacobiDN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.C1;
      }
      if (m.isOne()) {
        return F.Sech(z);
      }
      if (z.isZero()) {
        return F.C1;
      }
      try {
        double zDouble = Double.NaN;
        double mDouble = Double.NaN;
        try {
          zDouble = z.evalDouble();
          mDouble = m.evalDouble();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
          return F.complexNum(EllipticFunctionsJS.jacobiDN(z.evalComplex(), m.evalComplex()));
        } else {
          return F.num(EllipticFunctionsJS.jacobiDN(zDouble, mDouble));
        }
      } catch (ValidateException ve) {
        if (Config.SHOW_STACKTRACE) {
          ve.printStackTrace();
        }
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>JacobiSC(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>sc(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiSC(10.0,1/3)
   * -0.422766
   * </code></pre>
   */
  private static class JacobiSC extends AbstractFunctionEvaluator implements JacobiSCRules {
    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.Tan(z);
      }
      if (m.isOne()) {
        return F.Sinh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (engine.isDoubleMode()) {
        try {
          double zDouble = Double.NaN;
          double mDouble = Double.NaN;
          try {
            zDouble = z.evalDouble();
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSC(z.evalComplex(), m.evalComplex()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSC(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>JacobiSD(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>sd(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiSD(10.0,1/3)
   * 0.399627
   * </code></pre>
   */
  private static class JacobiSD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.Sin(z);
      }
      if (m.isOne()) {
        return F.Sinh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (engine.isDoubleMode()) {
        try {
          double zDouble = Double.NaN;
          double mDouble = Double.NaN;
          try {
            zDouble = z.evalDouble();
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSD(z.evalComplex(), m.evalComplex()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSD(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
   * <pre><code>JacobiSN(x, m)
   * </code></pre>
   *
   * <p>returns the Jacobian elliptic function <code>sn(x, m)</code>.
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Jacobi_elliptic_functions">Wikipedia - Jacobi
   *       elliptic functions</a>
   *   <li><a href="https://dlmf.nist.gov/22.5">NIST - Jacobian elliptic functions</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre><code>&gt;&gt; JacobiSN(10.0,1/3)
   * 0.389397
   * </code></pre>
   */
  private static class JacobiSN extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();
      if (m.isZero()) {
        return F.Sin(z);
      }
      if (m.isOne()) {
        return F.Tanh(z);
      }
      if (z.isZero()) {
        return F.C0;
      }
      if (engine.isDoubleMode()) {
        try {
          double zDouble = Double.NaN;
          double mDouble = Double.NaN;
          try {
            zDouble = z.evalDouble();
            mDouble = m.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(zDouble) || Double.isNaN(mDouble)) {
            return F.complexNum(EllipticFunctionsJS.jacobiSN(z.evalComplex(), m.evalComplex()));
          } else {
            return F.num(EllipticFunctionsJS.jacobiSN(zDouble, mDouble));
          }
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
        if (engine.evalTrue(F.LessEqual(F.Abs(F.Re(z)), F.CPiHalf))) {
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

  private static class KleinInvariantJ extends AbstractFunctionEvaluator
      implements KleinInvariantJRules {
    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr t = ast.arg1();
      IExpr im = S.Im.of(engine, t);

      if (im.isZero()) {
        return F.NIL;
      }
      if (im.isOne()) {
        IExpr re = S.Re.of(engine, t);
        if (re.isInteger()) {
          // KleinInvariantJ(re+I) = 1 and re is Integer
          return F.C1;
        }
        int r = re.toIntDefault();
        if (r != Integer.MIN_VALUE) {
          // KleinInvariantJ(re+I) = 1 and re is Integer
          return F.C1;
        }
      }
      if (engine.isDoubleMode()) {
        try {
          double tDouble = Double.NaN;
          try {
            tDouble = t.evalDouble();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(tDouble)) {
            Complex tComplex = t.evalComplex();
            return F.complexNum(EllipticIntegralsJS.kleinJ(tComplex));
          } else {
            return F.complexNum(EllipticIntegralsJS.kleinJ(tDouble));
          }
        } catch (ArithmeticException ae) {
          // unevaluated
          return F.NIL;
        } catch (ValidateException ve) {
          if (Config.SHOW_STACKTRACE) {
            ve.printStackTrace();
          }
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
        if (g2.isInexactNumber() && g3.isInexactNumber()) {
          try {
            org.hipparchus.complex.Complex[] invariants =
                EllipticFunctionsJS.weierstrassHalfPeriods(g2.evalComplex(), g3.evalComplex());
            return Object2Expr.convertComplex(false, invariants);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
        if (g2.isInexactNumber() && g3.isInexactNumber()) {
          try {
            org.hipparchus.complex.Complex[] invariants =
                EllipticFunctionsJS.weierstrassInvariants(g2.evalComplex(), g3.evalComplex());
            return Object2Expr.convertComplex(false, invariants);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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

  private static class WeierstrassP extends AbstractFunctionEvaluator {

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
        if (g2.isZero() && g3.isZero()) {
          return F.Power(u, F.CN2);
        }
        if (g2.isNumEqualInteger(F.C3) && g3.isOne()) {
          // 1 + (3/2) Cot(Sqrt(3/2)*u)^2
          return F.Plus(F.C1, F.Times(F.C3D2, F.Sqr(F.Cot(F.Times(F.Sqrt(F.C3D2), u)))));
        }
        if (u.isInexactNumber() && g2.isInexactNumber() && g3.isInexactNumber()) {
          try {
            return F.complexNum(
                EllipticFunctionsJS.weierstrassP(
                    u.evalComplex(), g2.evalComplex(), g3.evalComplex()));
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
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

  private static class WeierstrassPPrime extends AbstractFunctionEvaluator {

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
        if (g2.isZero() && g3.isZero()) {
          return F.Times(F.CN2, F.Power(u, F.CN3));
        }
        if (g2.isNumEqualInteger(F.C3) && g3.isOne()) {
          // -3 * Sqrt(3/2) * Cot(Sqrt(3/2)*u) * Csc(Sqrt(3/2)*u)^2
          return F.Times(
              F.CN3,
              F.Sqrt(F.C3D2),
              F.Cot(F.Times(F.Sqrt(F.C3D2), u)),
              F.Sqr(F.Csc(F.Times(F.Sqrt(F.C3D2), u))));
        }
        if (u.isInexactNumber() && g2.isInexactNumber() && g3.isInexactNumber()) {
          try {
            return F.complexNum(
                EllipticFunctionsJS.weierstrassPPrime(
                    u.evalComplex(), g2.evalComplex(), g3.evalComplex()));
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
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
