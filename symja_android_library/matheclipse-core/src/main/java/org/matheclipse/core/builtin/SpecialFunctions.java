package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;
import static org.matheclipse.core.expression.S.Pi;
import java.math.BigDecimal;
import java.util.function.DoubleUnaryOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.BesselJS;
import org.matheclipse.core.builtin.functions.GammaJS;
import org.matheclipse.core.builtin.functions.ZetaJS;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

public class SpecialFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Beta.setEvaluator(new Beta());
      S.BetaRegularized.setEvaluator(new BetaRegularized());
      S.DirichletEta.setEvaluator(new DirichletEta());
      S.Erf.setEvaluator(new Erf());
      S.Erfc.setEvaluator(new Erfc());
      S.Erfi.setEvaluator(new Erfi());
      S.GammaRegularized.setEvaluator(new GammaRegularized());
      S.HurwitzLerchPhi.setEvaluator(new HurwitzLerchPhi());
      S.HurwitzZeta.setEvaluator(new HurwitzZeta());
      S.HypergeometricPFQRegularized.setEvaluator(new HypergeometricPFQRegularized());
      S.InverseErf.setEvaluator(new InverseErf());
      S.InverseErfc.setEvaluator(new InverseErfc());
      S.InverseBetaRegularized.setEvaluator(new InverseBetaRegularized());
      S.InverseGammaRegularized.setEvaluator(new InverseGammaRegularized());
      S.LerchPhi.setEvaluator(new LerchPhi());
      S.LogGamma.setEvaluator(new LogGamma());
      S.MeijerG.setEvaluator(new MeijerG());
      S.PolyGamma.setEvaluator(new PolyGamma());
      S.PolyLog.setEvaluator(new PolyLog());
      S.ProductLog.setEvaluator(new ProductLog());
      S.StieltjesGamma.setEvaluator(new StieltjesGamma());
      S.StruveH.setEvaluator(new StruveH());
      S.StruveL.setEvaluator(new StruveL());
      S.Zeta.setEvaluator(new Zeta());
    }
  }

  private static class Beta extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 4) {
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr b = ast.arg3();
        try {
          if (engine.isDoubleMode()) {
            double aDouble = Double.NaN;
            double bDouble = Double.NaN;
            double zDouble = Double.NaN;
            try {
              zDouble = z.evalf();
              aDouble = a.evalf();
              bDouble = b.evalf();
            } catch (ValidateException ve) {
            }
            if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
              Complex zc = z.evalfc();
              Complex ac = a.evalfc();
              Complex bc = b.evalfc();

              return F.complexNum(GammaJS.beta(zc, ac, bc));

            } else {
              return GammaJS.incompleteBeta(zDouble, aDouble, bDouble);
            }
          }

          int bInt = b.toIntDefault();
          if (bInt > 0) {
            IInteger n = F.ZZ(bInt);
            if (a.isOne()) {
              return
              // [$ ( (1/n)*(1 - (1 - z)^n) ) $]
              F.Times(F.Power(n, F.CN1), F.Subtract(F.C1, F.Power(F.Subtract(F.C1, z), n))); // $$;
            }
            // if (bInt <= 1) {
            // ISymbol k = F.Dummy("k");
            // return
            // // [$ (Beta(a,n)*z^a*Sum((Pochhammer(a, k)*(1-z)^k)/k!, {k, 0, n - 1})) $]
            // F.Times(F.Beta(a, n), F.Power(z, a),
            // F.Sum(F.Times(F.Power(F.Subtract(F.C1, z), k), F.Power(F.Factorial(k), F.CN1),
            // F.Pochhammer(a, k)), F.list(k, F.C0, F.Plus(F.CN1, n)))); // $$;
            // }
          }
        } catch (ThrowException te) {
          LOGGER.debug("Beta.evaluate() failed", te);
          return te.getValue();
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
          // LOGGER.debug("Beta.evaluate() failed", ve);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
        }
        return F.NIL;
      }
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      if (a.isZero() || b.isZero()) {
        return F.CComplexInfinity;
      }
      try {
        if (engine.isDoubleMode()) {

          double aDouble = Double.NaN;
          double bDouble = Double.NaN;
          try {
            aDouble = a.evalf();
            bDouble = b.evalf();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(aDouble) || Double.isNaN(bDouble)) {
            Complex ac = a.evalfc();
            Complex bc = b.evalfc();

            return F.complexNum(GammaJS.beta(ac, bc));

          } else {
            return F.num(GammaJS.beta(aDouble, bDouble));
          }
        }
        if (a.isNumber() && b.isNumber()) {
          if (a.isInteger() && a.isPositive() && b.isInteger() && b.isPositive()) {
            // http://fungrim.org/entry/082a69/
            return Times(Factorial(Plus(CN1, a)), Factorial(Plus(CN1, b)),
                Power(Factorial(Plus(CN1, a, b)), -1));
          }
          // http://fungrim.org/entry/888581/
          return F.Times(F.Gamma(a), F.Gamma(b), F.Power(F.Gamma(F.Plus(a, b)), -1));
        }
        IExpr s = a.inc().subtract(b);
        if (s.isZero()) {
          return F.Power(F.Times(a, b, F.CatalanNumber(a)), -1);
        }
        IExpr sum = a.plus(b);
        if (sum.isInteger() && sum.isNegative()) {
          return F.C0;
        }
      } catch (ThrowException te) {
        LOGGER.debug("Beta.evaluate() failed", te);
        return te.getValue();
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
        // LOGGER.debug("Beta.evaluate() failed", ve);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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

  private static class BetaRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST3()) {
        return betaRegularized3(ast, engine);
      }
      if (ast.argSize() == 4) {
        return betaRegularized4(ast, engine);
      }
      return F.NIL;
    }

    private static IExpr betaRegularized3(final IAST ast, EvalEngine engine) {

      try {
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr n = ast.arg3();
        if (a.isZero() || (a.isInteger() && a.isNegative())) {
          if (n.isZero() || (n.isInteger() && n.isNegative())) {
            return S.Indeterminate;
          }
          return F.C1;
        }
        if (n.isZero() || (n.isInteger() && n.isNegative())) {
          return F.C0;
        }
        if (z.isZero()) {
          if (engine.evalGreater(F.Re(a), F.C0)) {
            return F.C0;
          }
          if (engine.evalLess(F.Re(a), F.C0)) {
            return F.CComplexInfinity;
          }
        } else if (z.isOne()) {
          if (engine.evalGreater(F.Re(n), F.C0)) {
            return F.C1;
          }
        }
        if (engine.isDoubleMode()) {
          try {
            double zn = engine.evalDouble(z);
            double an = engine.evalDouble(a);
            double nn = engine.evalDouble(n);
            int iterationLimit = EvalEngine.get().getIterationLimit();
            int aInt = (int) an;
            if (aInt > iterationLimit && iterationLimit > 0) {
              IterationLimitExceeded.throwIt(aInt, ast.topHead());
            }
            int nInt = (int) nn;
            if (nInt > iterationLimit && iterationLimit > 0) {
              IterationLimitExceeded.throwIt(nInt, ast.topHead());
            }
            // TODO improve with regularizedIncompleteBetaFunction() ???
            // https://github.com/haifengl/smile/blob/master/math/src/main/java/smile/math/special/Beta.java
            return F.num(GammaJS.betaRegularized(zn, an, nn));
          } catch (ValidateException ve) {
            // from org.matheclipse.core.eval.EvalEngine.evalDouble()
          }
        }
        int ni = n.toIntDefault();
        if (ni != Integer.MIN_VALUE) {
          if (ni < 0) {
            // for n>=0; BetaRegularized(z, a, -n)=0
            return F.C0;
          }
          if (ni > Config.MAX_POLYNOMIAL_DEGREE) {
            PolynomialDegreeLimitExceeded.throwIt(ni);
          }
          IASTAppendable sum = F.PlusAlloc(ni);
          // {k, 0, n - 1}
          for (int k = 0; k < ni; k++) {
            // (Pochhammer(a, k)*(1 - z)^k)/k!
            IInteger kk = F.ZZ(k);
            sum.append(F.Times(F.Power(F.Plus(F.C1, F.Negate(z)), kk), F.Power(F.Factorial(kk), -1),
                F.Pochhammer(a, kk)));
          }
          // z^a * sum
          return F.Times(F.Power(z, a), sum);
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("BetaRegularized.betaRegularized3() failed", rex);
      }
      return F.NIL;
    }

    /**
     * Evaluate for 4 arguments
     *
     * @param ast
     * @param engine
     * @return
     */
    private static IExpr betaRegularized4(final IAST ast, EvalEngine engine) {
      try {
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr n = ast.arg3();
        IExpr w = ast.arg4();
        if (engine.isDoubleMode()) {
          try {
            double zn = engine.evalDouble(z);
            double an = engine.evalDouble(a);
            double nn = engine.evalDouble(n);
            double wn = engine.evalDouble(w);
            int iterationLimit = EvalEngine.get().getIterationLimit();
            int aInt = (int) an;
            if (aInt > iterationLimit && iterationLimit > 0) {
              IterationLimitExceeded.throwIt(aInt, ast.topHead());
            }
            int nInt = (int) nn;
            if (nInt > iterationLimit && iterationLimit > 0) {
              IterationLimitExceeded.throwIt(nInt, ast.topHead());
            }
            return F.num(GammaJS.betaRegularized(zn, an, nn, wn));
          } catch (IllegalArgumentException | ValidateException e) {
            // IAE: from de.lab4inf.math.functions.IncompleteBeta.checkParameters()
            // ValidateException: from org.matheclipse.core.eval.EvalEngine.evalDouble()
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("BetaRegularized.betaRegularized4() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class DirichletEta extends AbstractArg1 {

    @Override
    public IExpr e1DblArg(final double d) {
      // if (F.isEqual(1.0, d)) {
      // return F.num(Math.log(2.0));
      // }
      // return e1ComplexArg(Complex.valueOf(d));
      return F.complexNum(ZetaJS.dirichletEta(d));
    }

    @Override
    public IExpr e1ComplexArg(final Complex c) {
      // Complex zeta;
      // if (F.isEqual(c.getReal(), 1.0) && F.isZero(c.getImaginary())) {
      // zeta = Complex.valueOf(Math.log(2.0), 0.0);
      // } else {
      // de.lab4inf.math.Complex x =
      // new de.lab4inf.math.sets.ComplexNumber(c.getReal(), c.getImaginary());
      // x = de.lab4inf.math.functions.Zeta.zeta(x);
      // zeta = Complex.valueOf(x.real(), x.imag());
      // }
      // Complex dirichletEta =
      //
      // zeta.multiply(Complex.ONE.subtract(Complex.valueOf(2.0).pow(Complex.ONE.subtract(c))));
      // return F.complex(dirichletEta.getReal(), dirichletEta.getImaginary());
      return F.complexNum(ZetaJS.dirichletEta(c));
    }

    @Override
    public IExpr e1ObjArg(final IExpr arg1) {
      if (arg1.isMinusOne()) {
        return F.C1D4;
      }
      if (arg1.isZero()) {
        return F.C1D2;
      }
      if (arg1.isOne()) {
        return F.Log(F.C2);
      }
      return NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Returns the error function.
   *
   * @see org.matheclipse.core.reflection.system.InverseErf
   */
  private static final class Erf extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

    @Override
    public IExpr e1ComplexArg(Complex c) {
      return F.complexNum(GammaJS.erf(c));
    }

    @Override
    public double applyAsDouble(double operand) {
      return de.lab4inf.math.functions.Erf.erf(operand);
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      try {
        return Num.valueOf(de.lab4inf.math.functions.Erf.erf(arg1));
        // return Num.valueOf(org.hipparchus.special.Erf.erf(arg1));
      } catch (final MathIllegalStateException e) {
      }
      return F.NIL;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      try {
        return de.lab4inf.math.functions.Erf.erf(stack[top]);
        // return org.hipparchus.special.Erf.erf(stack[top]);
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr z, EvalEngine engine) {
      if (z.isZero()) {
        return F.C0;
      }
      if (z.equals(CInfinity)) {
        return F.C1;
      }
      if (z.equals(CNInfinity)) {
        return F.CN1;
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      if (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI)) {
        return z;
      }
      if (z.isAST(S.InverseErf, 2)) {
        return z.first();
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return Negate(Erf(negExpr));
      }
      IExpr complexExpr = AbstractFunctionEvaluator.getComplexExpr(z, F.CNI);
      if (complexExpr.isPresent()) {
        return F.Times(F.CI, F.Erfi(complexExpr));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 3) {
        return F.Subtract(F.Erf(ast.arg2()), F.Erf(ast.arg1()));
      }
      return super.evaluate(ast, engine);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        if (engine.isDoubleMode()) {
          try {
            if (z.isComplexNumeric()) {
              return F.complexNum(GammaJS.erf(z.evalfc()));
            }
            return Num.valueOf(de.lab4inf.math.functions.Erf.erf(z.evalf()));
          } catch (final MathIllegalStateException e) {
            return Errors.printRuntimeException(S.Erf, e, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class Erfc extends AbstractTrigArg1 implements INumeric {

    @Override
    public IExpr e1DblArg(final double arg1) {
      try {
        return Num.valueOf(de.lab4inf.math.functions.Erf.erfc(arg1));
        // if (arg1 >= 0. && arg1 <= 2.0) {
        // return Num.valueOf(org.hipparchus.special.Erf.erfc(arg1));
        // }
      } catch (final MathIllegalStateException e) {
      }

      return F.NIL;
    }

    @Override
    public IExpr e1ComplexArg(Complex c) {
      return F.complexNum(GammaJS.erfc(c));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      try {
        double arg1 = stack[top];
        return de.lab4inf.math.functions.Erf.erfc(arg1);
        // if (arg1 >= 0. && arg1 <= 2.0) {
        // return org.hipparchus.special.Erf.erfc(arg1);
        // }
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr z, EvalEngine engine) {
      if (z.isReal()) {
        if (z.isZero()) {
          return F.C1;
        }
        if (z.equals(CInfinity)) {
          return F.C0;
        }
        if (z.equals(CNInfinity)) {
          return F.C2;
        }
        if (z.isComplexInfinity()) {
          return S.Indeterminate;
        }
        if (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI)) {
          return z.negate();
        }
      } else if (z.isAST(S.InverseErfc, 2)) {
        return z.first();
      }
      // don't transform negative arg:
      // IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      // if (negExpr.isPresent()) {
      // return F.Subtract(F.C2, F.Erfc(negExpr));
      // }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Returns the error function.
   *
   * @see org.matheclipse.core.reflection.system.InverseErf
   */
  private static final class Erfi extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isInfinity()) {
        return F.CInfinity;
      }
      if (z.isNegativeInfinity()) {
        return F.CNInfinity;
      }
      if (z.equals(F.CIInfinity)) {
        return F.CI;
      }
      if (z.equals(F.CNIInfinity)) {
        return F.CNI;
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      if (engine.isDoubleMode() && z.isNumber()) {
        try {
          Complex zc = z.evalfc();
          return F.complexNum(GammaJS.erfi(zc));
        } catch (ValidateException ve) {
          LOGGER.debug("Erfi.evaluate() failed", ve);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
        }
      }
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // I * Erf(-I*z)
        return F.Times(S.I, F.Erf(F.Times(F.CNI, z)));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return Negate(F.Erfi(negExpr));
      }
      IExpr complexExpr = AbstractFunctionEvaluator.getComplexExpr(z, F.CNI);
      if (complexExpr.isPresent()) {
        return F.Times(F.CI, F.Erf(complexExpr));
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

  private static class GammaRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr a = ast.arg1();
        IExpr z1 = ast.arg2();
        if (ast.isAST3()) {
          IExpr z2 = ast.arg3();
          return gammaRegularzed3(a, z1, z2, ast, engine);
        }
        return gammaRegularized2(a, z1, ast, engine);
      } catch (MathIllegalArgumentException miae) {
        return Errors.printMessage(S.GammaRegularized, "argillegal",
            F.list(F.stringx(miae.getMessage()), ast), engine);
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.GammaRegularized, "argillegal",
            F.list(F.stringx(rex.getMessage()), ast), engine);
      }
    }

    private static IExpr gammaRegularized2(IExpr a, IExpr z1, IAST ast, EvalEngine engine) {
      if (a.isZero()) {
        return F.C0;
      } else if (a.isNumEqualRational(F.C1D2)) {
        // Erfc(Sqrt(z))
        return F.Erfc(F.Sqrt(z1));
      } else if (a.isOne()) {
        // E^(-z)
        return F.Power(S.E, F.Negate(z1));
      } else if (a.isInteger() && a.isNegative()) {
        return F.C0;
      }
      if (engine.isDoubleMode()) {
        try {
          double aDouble = Double.NaN;
          double z1Double = Double.NaN;
          try {
            aDouble = a.evalf();
            z1Double = z1.evalf();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(aDouble) || Double.isNaN(z1Double)) {
            // TODO complex numbers
          } else {
            return F.num(GammaJS.gammaRegularized(aDouble, z1Double));
          }
        } catch (ThrowException te) {
          LOGGER.debug("GammaRegularized.gammaRegularized2() failed", te);
          return te.getValue();
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
        }
      }
      if (z1.isZero()) {
        IExpr temp = a.re();
        if (temp.isPositive()) {
          return F.C1;
        }
        if (temp.isNegative()) {
          return F.CComplexInfinity;
        }
      } else if (z1.isMinusOne()) {
        // (E/Gamma[a])*Subfactorial(a - 1)
        return F.Times(S.E, F.Power(F.Gamma(a), -1), F.Subfactorial(F.Plus(F.CN1, a)));
      }
      return F.NIL;
    }

    private static IExpr gammaRegularzed3(IExpr a, IExpr z1, IExpr z2, final IAST ast,
        EvalEngine engine) {
      if (engine.isDoubleMode()) {
        try {
          double aDouble = Double.NaN;
          double z1Double = Double.NaN;
          double z2Double = Double.NaN;
          try {
            aDouble = a.evalf();
            z1Double = z1.evalf();
            z2Double = z2.evalf();
          } catch (ValidateException ve) {
          }
          if (Double.isNaN(aDouble) || Double.isNaN(z1Double) || Double.isNaN(z2Double)) {
            // TODO
            // Complex sc = s.evalComplex();
            // Complex ac = a.evalComplex();
            // return F.complexNum(ZetaJS.hurwitzZeta(sc, ac));
          } else {
            return F.num(GammaJS.gammaRegularized(aDouble, z1Double, z2Double));
          }
        } catch (ThrowException te) {
          LOGGER.debug("GammaRegularized.gammaRegularzed3() failed", te);
          return te.getValue();
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          return F.NIL;
        }
      }

      if (a.isOne()) {
        // E^(-arg2)-E^(-arg3)
        return F.Subtract(F.Power(S.E, F.Negate(z1)), F.Power(S.E, F.Negate(z2)));
      }
      if (a.isInteger() && a.isNegative()) {
        return F.C0;
      }
      // TODO add more rules
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

  private static class HypergeometricPFQRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
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
  private static class HurwitzLerchPhi extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr s = ast.arg2();
      IExpr a = ast.arg3();
      if (z.isZero() && s.isOne() && a.isOne()) {
        // special for numeric values
        return F.C1;
      }

      // if (engine.isDoubleMode()) {
      // }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class HurwitzZeta extends AbstractFunctionEvaluator {

    public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.zeta(a1.apfloatValue(), a2.apfloatValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr s = ast.arg1();
      IExpr a = ast.arg2();
      if (s.isNumber()) {
        if (s.isZero()) {
          // http://fungrim.org/entry/d99808/
          return F.Subtract(F.C1D2, a);
        }
        if (s.isOne()) {
          // http://fungrim.org/entry/532f31/
          return F.CComplexInfinity;
        }
      }
      if (a.isNumber()) {
        if (a.isZero() && s.isInteger() && s.isNegative()) {
          // http://fungrim.org/entry/7dab87/
          return F.Times(F.CN1,
              F.Divide(F.BernoulliB(F.Plus(1, s.negate())), F.Plus(1, s.negate())));
        }
        if (a.isOne()) {
          // http://fungrim.org/entry/af23f7/
          return F.Zeta(s);
        }
        if (a.isNumEqualInteger(F.C2)) {
          // http://fungrim.org/entry/b721b4/
          return F.Plus(F.CN1, F.Zeta(s));
        }
        if (a.isNumEqualRational(F.C1D2)) {
          // http://fungrim.org/entry/af7d3d/
          return F.Times(F.Plus(F.CN1, F.Power(F.C2, s)), F.Zeta(s));
        }
        if (a.isNumEqualRational(F.C3D4)) {
          if (a.isNumEqualRational(F.C3D4)) {
            // http://fungrim.org/entry/951f86/
            return F.Plus(F.Times(F.CN8, S.Catalan), F.Sqr(Pi));
          }
        }
        if (s.isInteger() && a.isInteger() && a.isPositive()) {
          IInteger sInt = (IInteger) s;
          if (sInt.isNegative() || sInt.isEven()) {
            // http://fungrim.org/entry/6e69fc/
            int n = a.toIntDefault();
            int sNegate = sInt.negate().toIntDefault();
            if (n > Integer.MIN_VALUE && sNegate > Integer.MIN_VALUE) {
              return F.Subtract(F.Zeta(s), F.sum(k -> {
                return k.power(sNegate);
              }, 1, n - 1));
            }
          }
        }
      }

      if (engine.isDoubleMode()) {
        try {
          double sDouble = Double.NaN;
          double aDouble = Double.NaN;
          try {
            sDouble = s.evalf();
            aDouble = a.evalf();
          } catch (ValidateException ve) {
          }
          if (aDouble < 0.0 || Double.isNaN(sDouble) || Double.isNaN(aDouble)) {
            Complex sc = s.evalfc();
            Complex ac = a.evalfc();
            return F.complexNum(ZetaJS.hurwitzZeta(sc, ac));
          } else {
            if (aDouble >= 0 && sDouble != 1.0) {
              return F.num(ZetaJS.hurwitzZeta(sDouble, aDouble));
            }
          }
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (ThrowException te) {
          LOGGER.debug("HurwitzZeta.evaluate() failed", te);
          return te.getValue();
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
        }
      }
      if (engine.isArbitraryMode()) {
        if (s instanceof ApfloatNum && a instanceof ApfloatNum) {
          return e2ApfloatArg(((ApfloatNum) s), ((ApfloatNum) a));
        }
        if (s instanceof ApcomplexNum && a instanceof ApcomplexNum) {
          return e2ApcomplexArg((ApcomplexNum) s, (ApcomplexNum) a);
        }
      }
      return NIL;
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
   * Returns the inverse erf.
   *
   * @see org.matheclipse.core.reflection.system.Erf
   */
  private static final class InverseErf extends AbstractTrigArg1 implements INumeric {

    @Override
    public IExpr e1DblArg(final double arg1) {
      try {
        if (arg1 >= -1.0 && arg1 <= 1.0) {
          return Num.valueOf(org.hipparchus.special.Erf.erfInv(arg1));
        }
      } catch (final MathIllegalStateException e) {
      }
      return F.NIL;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      try {
        double arg1 = stack[top];
        if (arg1 >= -1.0 && arg1 <= 1.0) {
          return org.hipparchus.special.Erf.erfInv(arg1);
        }
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isList()) {
        return ((IAST) arg1).mapThread(x -> F.InverseErf(x));
      }
      if (arg1.isReal()) {
        if (arg1.isZero()) {
          return F.C0;
        }
        if (arg1.isOne()) {
          return F.CInfinity;
        }
        if (arg1.isMinusOne()) {
          return F.CNInfinity;
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Returns the inverse erf.
   *
   * @see org.matheclipse.core.reflection.system.Erf
   */
  private static final class InverseErfc extends AbstractTrigArg1 implements INumeric {

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (arg1 >= 0. && arg1 <= 2.0) {
        try {
          return Num.valueOf(org.hipparchus.special.Erf.erfcInv(arg1));
        } catch (final MathIllegalStateException e) {
        }
      }
      return F.NIL;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      try {
        double arg1 = stack[top];
        if (arg1 >= 0. && arg1 <= 2.0) {
          return org.hipparchus.special.Erf.erfcInv(arg1);
        }
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isList()) {
        return ((IAST) arg1).mapThread(x -> F.InverseErfc(x));
      }
      if (arg1.isReal()) {
        IReal z = (IReal) arg1;
        if (z.isZero()) {
          return F.CInfinity;
        }
        if (z.isOne()) {
          return F.C0;
        }
        if (z.equals(F.C2)) {
          return F.CNInfinity;
        }
        // if (z.isLessThan(F.C2) && z.isGreaterThan(F.C1)) {
        // return F.InverseErfc(F.Subtract(F.C1, z));
        // }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseBetaRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST3()) {
          IExpr z = ast.arg1();
          IExpr a = ast.arg2();
          IExpr b = ast.arg3();
          if (a.isPositiveResult()) {
            if (z.isZero()) {
              return F.C0;
            }
            if (z.isOne()) {
              return F.C1;
            }
          }
          if (engine.isDoubleMode() && (z.isNumericFunction(true) && a.isNumericFunction(true)
              && b.isNumericFunction(true))) {
            org.hipparchus.distribution.continuous.BetaDistribution beta = //
                new org.hipparchus.distribution.continuous.BetaDistribution(a.evalf(), b.evalf());
            return F.num(beta.inverseCumulativeProbability(z.evalf()));
          }
        } else {
          IExpr z1 = ast.arg1();
          IExpr z2 = ast.arg2();
          if (z2.isZero()) {
            return z1;
          }
          IExpr a = ast.arg3();
          IExpr b = ast.arg4();
          if (z1.isZero()) {
            return F.InverseBetaRegularized(z2, a, b);
          }
        }
      } catch (MathIllegalArgumentException miae) {
        return Errors.printMessage(S.InverseBetaRegularized, "argillegal",
            F.list(F.stringx(miae.getMessage()), ast), engine);
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.InverseBetaRegularized, "argillegal",
            F.list(F.stringx(rex.getMessage()), ast), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class InverseGammaRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();

      if (ast.isAST3()) {
        IExpr z1 = ast.arg2();
        IExpr z2 = ast.arg3();
        if (z1.isInfinity()) {
          // (a,Infinity,z2) => InverseGammaRegularized(a, -z2))
          return F.InverseGammaRegularized(a, z2.negate());
        }
      } else {
        IExpr z = ast.arg2();
        if (a.isPositiveResult()) {
          if (z.isZero()) {
            return S.Infinity;
          } else if (z.isOne()) {
            return F.C0;
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
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class LerchPhi extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr s = ast.arg2();
      IExpr a = ast.arg3();
      if (s.isZero()) {
        return F.Power(F.Subtract(F.C1, z), F.CN1);
      }

      if (a.isZero()) {
        return F.PolyLog(s, z);
      }
      if (a.isOne()) {
        // PolyLog(s, z)/z
        return F.Times(F.PolyLog(s, z), F.Power(z, F.CN1));
      }

      if (z.isZero()) {
        if (s.isOne()) {
          return F.Power(F.Power(a, 2), F.CN1D2);
        }
        // (a^2)^(-s/2)
        return F.Power(F.Power(a, F.C2), F.Times(F.CN1D2, s));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class LogGamma extends AbstractTrigArg1 implements INumeric {

    @Override
    public IExpr e1ComplexArg(final Complex c) {
      return F.complexNum(GammaJS.logGamma(c));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      try {
        if (F.isZero(arg1)) {
          return F.CInfinity;
        }
        if (arg1 > 0.0) {
          return Num.valueOf(GammaJS.logGamma(arg1));
        }
      } catch (final MathIllegalStateException e) {
      }
      return F.NIL;
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      try {
        if (F.isZero(stack[top])) {
          return Double.POSITIVE_INFINITY;
        }
        return GammaJS.logGamma(stack[top]);
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInfinity() || arg1.isZero()) {
        return F.CInfinity;
      }
      if (arg1.isNegativeInfinity() || arg1.isDirectedInfinity(F.CI)
          || arg1.isDirectedInfinity(F.CNI) || arg1.isComplexInfinity()) {
        return F.CComplexInfinity;
      }
      if (arg1.isPositive()) {
        if (arg1.isInteger()) {
          return F.Log(F.Factorial(arg1.dec()));
        }
        if (arg1.isFraction() && ((IFraction) arg1).denominator().equals(F.C2)) {
          IInteger n = ((IFraction) arg1).numerator();
          //
          return
          // [$ Log(2^(1 - n)*Sqrt(Pi)*Gamma(n)/Gamma((n+1)/2)) $]
          F.Log(F.Times(F.Power(F.C2, F.Subtract(F.C1, n)), F.Sqrt(S.Pi),
              F.Power(F.Gamma(F.Times(F.C1D2, F.Plus(n, F.C1))), F.CN1), F.Gamma(n))); // $$;
        }
      } else if (arg1.isNegative()) {
        if (arg1.isInteger()) {
          return F.CInfinity;
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class MeijerG extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 4) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        IExpr z = ast.arg3();
        if (z.isList()) {
          return z.mapThread(ast.setAtCopy(3, F.Slot1), 3);
        }
        if (arg1.isList() && arg2.isList()) {
          IAST list1 = (IAST) arg1;
          IAST list2 = (IAST) arg2;
          if (list1.size() == 3 && list1.arg1().isList() && list1.arg2().isList() && //
              list2.size() == 3 && list2.arg1().isList() && list2.arg2().isList()) {
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
                // 0
                switch (p) {
                  case 0:
                    // 0,0
                    switch (m) {
                      case 0:
                        switch (q) {
                          case 0:
                            // 0,0,0,0
                            LOGGER.log(engine.getLogLevel(), "MeijerG: {} not available.", ast);
                            return F.NIL;
                        }
                        break;
                      case 1:
                        IExpr b1 = l1.arg1();
                        switch (q) {
                          case 1:
                            // 0,0,1,1
                            IExpr b2 = l2.arg1();
                            return
                            // [$ z^(b1 + (1/2)*(-b1 + b2))*BesselJ(b1 - b2, 2*Sqrt(z)) $]
                            F.Times(
                                F.Power(z, F.Plus(b1, F.Times(F.C1D2, F.Plus(F.Negate(b1), b2)))),
                                F.BesselJ(F.Subtract(b1, b2), F.Times(F.C2, F.Sqrt(z)))); // $$;
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
                break;
              case 1:
                // 1
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
                            return
                            // [$ z^(-1 + a1)/E^z^(-1) $]
                            F.Times(F.Power(F.Exp(F.Power(z, F.CN1)), F.CN1),
                                F.Power(z, F.Plus(F.CN1, a1))); // $$;
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
                          case 1:
                            // 1,0,1,1
                            IExpr b2 = l2.arg1();
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
                            F.Times(
                                F.Power(z,
                                    F.Plus(F.CN1, a1, F.Times(F.C1D2, F.Plus(F.Negate(a1), a2)))),
                                F.BesselJ(F.Plus(F.Negate(a1), a2),
                                    F.Times(F.C2, F.Power(z, F.CN1D2)))); // $$;
                          case 1:
                            // 1,1,0,1
                            IExpr b2 = l2.arg1();
                            return
                            // [$ (z^(-1 + a1)*Hypergeometric1F1Regularized(1 - a1 + b2, 1 - a1 +
                            // a2,
                            // 1/z))/Gamma(a1 - b2) $]
                            F.Times(F.Power(z, F.Plus(F.CN1, a1)),
                                F.Power(F.Gamma(F.Subtract(a1, b2)), F.CN1),
                                F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b2),
                                    F.Plus(F.C1, F.Negate(a1), a2), F.Power(z, F.CN1))); // $$;
                        }
                        break;
                      case 1:
                        IExpr b1 = l1.arg1();
                        switch (q) {
                          case 0:
                            // 1,1,1,0
                            return
                            // [$ z^(-1 + a1)*Gamma(1 - a1 + b1)*Hypergeometric1F1Regularized(1 - a1
                            // + b1, 1
                            // - a1 + a2, -(1/z)) $]
                            F.Times(F.Power(z, F.Plus(F.CN1, a1)),
                                F.Gamma(F.Plus(F.C1, F.Negate(a1), b1)),
                                F.Hypergeometric1F1Regularized(F.Plus(F.C1, F.Negate(a1), b1),
                                    F.Plus(F.C1, F.Negate(a1), a2), F.Negate(F.Power(z, F.CN1)))); // $$;
                        }
                        break;
                    }
                    break;
                }
                break;
            }
          }
        }
      }
      return F.NIL;
    }

    // @Override
    // public IAST getRuleAST() {
    // return RULES;
    // }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class PolyGamma extends AbstractFunctionEvaluator implements IFunctionExpand {

    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.digamma(arg1));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.digamma(arg1));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr arg1 = ast.arg1();
        IExpr z = ast.arg2();
        if (arg1.isMathematicalIntegerNonNegative()) {
          int n = arg1.toIntDefault();
          if (n != Integer.MIN_VALUE) {
            // https://github.com/sympy/sympy/blob/b64cfcdb640975706c71f305d99a8453ea5e46d8/sympy/functions/special/gamma_functions.py#L790

            // if (z.isPlus()) {
            // IExpr coeffExpr = z.first();
            // if (coeffExpr.isInteger()) {
            // int coeff = coeffExpr.toIntDefault();
            // int e = -(n + 1);
            // IExpr tail;
            // if (coeff > 0) {
            // tail = F.intSum(i -> F.Power(F.Subtract(z, i), e), 1, coeff + 1);
            // } else {
            // tail = F.Negate(F.intSum(i -> F.Power(F.Plus(i, z), e), 0, -coeff));
            // }
            // return F.Plus(F.PolyGamma(n, F.Subtract(z, coeffExpr)),
            // F.Times(F.Power(F.CN1, n), F.Factorial(n), tail));
            // }
            // }

            if (n == 0) {
              if (z.isRational()) {
                IRational zr = (IRational) z;
                IInteger numerator = zr.numerator();
                int p = numerator.toIntDefault();
                if (p > 0) {
                  IInteger denominator = zr.denominator();
                  int q = denominator.toIntDefault();
                  if (q > 0 && p < q) {
                    // https://functions.wolfram.com/GammaBetaErf/PolyGamma/03/01/0015/

                    // Sum(2*Cos((2*Pi*p*k)/q)*(Log(Sin((Pi*k)/q), {k, 1, Floor((q-1)/2)})
                    IExpr kSum = F.intSum(//
                        k -> F.Times(F.C2, //
                            F.Cos(F.Times(zr.multiply(2 * k), S.Pi)),
                            F.Log(F.Sin(F.Times(F.QQ(k, q), S.Pi))))//
                        , 1, (q - 1) / 2);
                    return engine
                        .evaluate(F.Plus(kSum, F.Times(F.CN1D2, S.Pi, F.Cot(F.Times(zr, S.Pi))),
                            F.Negate(F.Log(2 * q)), F.Negate(S.EulerGamma)));
                  }
                }
              }

              IExpr zNegated = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
              if (zNegated.isPresent()) {
                // https://functions.wolfram.com/GammaBetaErf/PolyGamma/17/02/01/0001/
                return F.Plus(F.PolyGamma(0, zNegated), F.Divide(1, zNegated),
                    F.Times(S.Pi, F.Cot(F.Times(S.Pi, zNegated))));
              }
            }
          }
        }

      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isMinusOne()) {
          return F.CComplexInfinity;
        }
        return F.PolyGamma(F.C0, arg1);
      }
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg1.isMinusOne()) {
          return F.LogGamma(arg2);
        }
        if (arg2.isOne()) {
          int n = arg1.toIntDefault();
          if (n > 0 && (n & 0x0001) == 0x0001) {
            return F.Times(Factorial(F.ZZ(n)), F.Zeta(F.ZZ(n + 1)));
          }
        }
        if (arg2.isIntegerResult() && arg2.isNegativeResult()) {
          IExpr nu = arg1.re();
          if (nu.isReal() && ((IReal) nu).isGT(F.CN1)) {
            return F.CComplexInfinity;
          }
        }
        if (engine.isDoubleMode()) {
          try {
            int n = arg1.toIntDefault();
            if (n >= 0) {
              double xDouble = Double.NaN;
              try {
                xDouble = arg2.evalf();
              } catch (ValidateException ve) {
              }
              if (Double.isNaN(xDouble)) {
                // Complex xc = arg2.evalComplex();
                //
                // return
              } else {
                if (n == 0) {
                  return F.num(GammaJS.polyGamma(xDouble));
                }
                return F.num(GammaJS.polyGamma(n, xDouble));
              }
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (ThrowException te) {
            LOGGER.debug("PolyGamma.evaluate() failed", te);
            return te.getValue();
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        }
        if (arg1.isZero() && engine.isArbitraryMode()) {
          if (arg2 instanceof ApfloatNum) {
            return e1ApfloatArg(((ApfloatNum) arg2).apfloatValue());
          }
          if (arg2 instanceof ApcomplexNum) {
            return e1ApcomplexArg(((ApcomplexNum) arg2).apcomplexValue());
          }
        }
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

  private static class PolyLog extends AbstractFunctionEvaluator {

    /**
     * See <a href=
     * "https://github.com/sympy/sympy/blob/master/sympy/functions/special/zeta_functions.py">Sympy
     * - zeta_functions.py</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr x = ast.arg2();
      IExpr temp = polyLogSymbolic(n, x);
      if (temp.isPresent()) {
        return temp;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();

        IExpr temp = polyLogSymbolic(n, z);
        if (temp.isPresent()) {
          return temp;
        }

        if (engine.isDoubleMode()) {
          try {
            double nDouble = Double.NaN;
            double xDouble = Double.NaN;
            try {
              nDouble = n.evalf();
              xDouble = z.evalf();
            } catch (ValidateException ve) {
            }

            if (Double.isNaN(nDouble) || Double.isNaN(xDouble)) {
              Complex nComplex = n.evalfc();
              Complex xComplex = z.evalfc();
              return F.complexNum(ZetaJS.polyLog(nComplex, xComplex));
            } else {
              return F.complexNum(ZetaJS.polyLog(nDouble, xDouble));
            }

          } catch (RuntimeException rex) {
            Errors.printMessage(S.PolyLog, rex, engine);
          }
        }
      }
      return F.NIL;
    }

    private IExpr polyLogSymbolic(IExpr n, IExpr x) {
      if (x.isZero()) {
        return F.C0;
      }
      if (x.isOne()) {
        if (n.isOne()) {
          return F.CInfinity;
        }
        IExpr temp = n.re();
        if (temp.isReal()) {
          IReal num = (IReal) temp;
          if (num.isOne()) {
            return S.Indeterminate;
          } else if (num.isGT(F.C1)) {
            return F.Zeta(n);
          } else {
            return F.CComplexInfinity;
          }
        }
      } else if (x.isMinusOne()) {
        // (2^(1-arg1)-1)*Zeta(arg1)
        return Times(Plus(CN1, Power(C2, Plus(C1, Negate(n)))), Zeta(n));
      }

      if (n.isReal()) {
        if (n.isZero()) {
          // arg2/(1 - arg2)
          return Times(x, Power(Plus(C1, Negate(x)), -1));
        } else if (n.isOne()) {
          // -Log(1 - arg2))
          return Negate(Log(Plus(C1, Negate(x))));
        } else if (n.isMinusOne()) {
          // arg2/(arg2 - 1)^2
          return Times(x, Power(Plus(C1, Negate(x)), -2));
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
   * Lambert W function See: <a href="http://en.wikipedia.org/wiki/Lambert_W_function">Wikipedia -
   * Lambert W function</a>
   */
  private static final class ProductLog extends AbstractArg12 {

    @Override
    public IExpr e1DblArg(final INum d) {
      if (d.isZero()) {
        return d;
      }
      try {
        return F.num(ApfloatMath.w(new Apfloat(d.doubleValue())).doubleValue());
      } catch (Exception ce) {

      }
      Apcomplex c = ApcomplexMath.w(new Apfloat(d.doubleValue()));
      return F.complexNum(c.real().doubleValue(), c.imag().doubleValue());
    }

    @Override
    public IExpr e1DblComArg(IComplexNum arg1) {
      if (arg1.isZero()) {
        return arg1;
      }
      Apcomplex c = new Apcomplex(
          new Apfloat(new BigDecimal(arg1.getRealPart()), ParserConfig.MACHINE_PRECISION),
          new Apfloat(new BigDecimal(arg1.getImaginaryPart()), ParserConfig.MACHINE_PRECISION));
      // if (Config.FUZZ_TESTING) {
      // LOGGER.error(c);
      // }
      c = ApcomplexMath.w(c);
      return F.complexNum(c.real().doubleValue(), c.imag().doubleValue());
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        return F.C0;
      }
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.w(arg1));
      } catch (Exception ce) {

      }
      return F.complexNum(h.w(arg1, 0));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        return F.C0;
      }
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.complexNum(h.w(arg1, 0));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      if (o.isZero()) {
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ObjArg(IExpr k, IExpr z) {
      int ki = Integer.MIN_VALUE;
      if (z.isZero()) {
        // ProductLog(k_?NumberQ,0) := -Infinity/;k!=0
        if (k.isNonZeroComplexResult()) {
          return F.CNInfinity;
        }
      }
      if (k.isNumber()) {
        ki = k.toIntDefault();
        if (ki == Integer.MIN_VALUE) {
          // Machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.ProductLog, "intm", F.list(F.ProductLog(k, z), F.C1),
              EvalEngine.get());
        }
        // ProductLog(0,z_) := ProductLog(z)
        if (ki == 0) {
          if (z.isZero()) {
            return F.C0;
          }
          return F.ProductLog(z);
        }
        if (ki == (-1)) {
          if (z.equals(F.CNPiHalf)) {
            // ProductLog(-1, -(Pi/2)) := -((I*Pi)/2)
            return F.Times(F.CC(0L, 1L, -1L, 2L), S.Pi);
          }
          // ProductLog(-1, -(1/E)) := -1
          if (z.equals(F.Negate(F.Power(S.E, -1)))) {
            return F.CN1;
          }
        }
        if (z.isNumber()) {
          if (z instanceof IComplexNum) {
            if (z instanceof ApcomplexNum) {
              FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
              ApcomplexNum acn = (ApcomplexNum) z;
              return F.complexNum(h.w(acn.apcomplexValue(), ki));
            }
            if (z instanceof ComplexNum) {
              ComplexNum cn = (ComplexNum) z;
              Apcomplex c =
                  new Apcomplex(new Apfloat(cn.getRealPart()), new Apfloat(cn.getImaginaryPart()));
              c = ApcomplexMath.w(c, ki);
              return F.complexNum(c.real().doubleValue(), c.imag().doubleValue());
            }
          }
          if (z instanceof ApfloatNum) {
            FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
            ApfloatNum an = (ApfloatNum) z;
            return F.complexNum(h.w(an.apfloatValue(), ki));
          }
          if (z instanceof Num) {
            Num n = (Num) z;
            Apcomplex c = ApcomplexMath.w(new Apfloat(n.doubleValue()), ki);
            return F.complexNum(c.real().doubleValue(), c.imag().doubleValue());
          }
        }
      }

      return super.e2ObjArg(k, z);
    }
  }

  private static class StieltjesGamma extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      if ((n.isNumber() && !n.isInteger()) || n.isNegativeResult()) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.StieltjesGamma, "intnm", F.List(ast, F.C1), engine);
      }
      if (ast.isAST2()) {
        IExpr a = ast.arg2();

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

  private static final class StruveH extends AbstractFunctionEvaluator {

    // public IExpr e2DblArg(final INum d0, final INum d1) {
    // double v = d0.reDoubleValue();
    // double z = d1.reDoubleValue();
    // try {
    // final double iterationSum = 100;
    // double fraction = 0;
    // double fractionFactor = Math.pow((0.5 * z), v + 1);
    // for (int i = 0; i < iterationSum; ++i) {
    // double fractionTopPart = Math.pow(-1, i) * Math.pow(0.5 * z, 2.0 * i);
    // double fractionBottomPart = Gamma.gamma(i + 1.5) * Gamma.gamma(i + v + 1.5);
    // fraction = fraction + (fractionTopPart / fractionBottomPart);
    // }
    // return F.num(fractionFactor * fraction);
    // } catch (Exception e) {
    // throw e;
    // }
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      try {
        if (arg2.isZero()) {
          IExpr re = arg1.re();
          if (re.isMinusOne()) {
            // StruveH(n_,0):=Indeterminate/;Re(n)==(-1)
            return S.Indeterminate;
          }
          IExpr temp = re.greaterThan(F.CN1);
          if (temp.isTrue()) {
            // StruveH(n_,0):=0/;Re(n)>(-1)
            return F.C0;
          }
          if (temp.isFalse()) {
            // StruveH(n_,0):=ComplexInfinity/;Re(n)<(-1)
            return F.CComplexInfinity;
          }
        } else if (arg1 instanceof INum && arg2 instanceof INum) {
          return F.num(BesselJS.struveH(arg1.evalf(), arg2.evalf()));
          // return e2DblArg((INum) arg1, (INum) arg2);
        } else if (arg1.isComplexNumeric() || arg2.isComplexNumeric()) {
          return F.complexNum(BesselJS.struveH(arg1.evalfc(), arg2.evalfc()));
        } else {
          IExpr negArg2 = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
          if (negArg2.isPresent()) {
            // StruveH(n_, arg2_)) := ((-(arg2)^n) StruveH(n,
            // negArg2))/negArg2^n
            return F.Times(F.CN1, F.Power(arg2, arg1), F.Power(negArg2, F.Negate(arg1)),
                F.StruveH(arg1, negArg2));
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("StruveH.evaluate() failed", rex);
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

  private static final class StruveL extends AbstractFunctionEvaluator {

    // public IExpr e2DblArg(final INum d0, final INum d1) {
    // double v = d0.reDoubleValue();
    // double z = d1.reDoubleValue();
    // try {
    // final int iterationSum = 100;
    // double fraction = 0;
    // double fractionFactor = Math.pow((0.5 * z), v + 1);
    // for (int i = 0; i < iterationSum; ++i) {
    // double fractionTopPart = 1 * Math.pow((0.5 * z), (2.0 * i));
    // double fractionBottomPart = Gamma.gamma (i + (1.5)) * Gamma.gamma(i + v + (1.5));
    // fraction = fraction + (fractionTopPart / fractionBottomPart);
    // }
    // return F.num(fractionFactor * fraction);
    // } catch (Exception e) {
    // throw e;
    // }
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      try {
        if (arg2.isZero()) {
          IExpr re = arg1.re();
          if (re.isMinusOne()) {
            // StruveL(n_,0):=Indeterminate/;Re(n)==(-1)
            return S.Indeterminate;
          }
          IExpr temp = re.greaterThan(F.CN1);
          if (temp.isTrue()) {
            // StruveL(n_,0):=0/;Re(n)>(-1)
            return F.C0;
          }
          if (temp.isFalse()) {
            // StruveL(n_,0):=ComplexInfinity/;Re(n)<(-1)
            return F.CComplexInfinity;
          }
        } else if (arg1 instanceof INum && arg2 instanceof INum) {
          return F.num(BesselJS.struveL(arg1.evalf(), arg2.evalf()));
          // return e2DblArg((INum) arg1, (INum) arg2);
        } else if (arg1.isComplexNumeric() || arg2.isComplexNumeric()) {
          return F.complexNum(BesselJS.struveL(arg1.evalfc(), arg2.evalfc()));
        } else {
          IExpr negArg2 = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
          if (negArg2.isPresent()) {
            // StruveL(n_, arg2_)) := ((-(arg2)^n) StruveL(n,
            // negArg2))/negArg2^n
            return F.Times(F.CN1, F.Power(arg2, arg1), F.Power(negArg2, F.Negate(arg1)),
                F.StruveL(arg1, negArg2));
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("StruveL.evaluate() failed", rex);
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

  private static final class Zeta extends AbstractArg12 {

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.zeta(arg1));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(arg1));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.zeta(a1.apfloatValue(), a2.apfloatValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        //
      }
      return F.NIL;
    }



    @Override
    public IExpr e1DblArg(INum num) {
      double d = de.lab4inf.math.functions.Zeta.zeta(num.doubleValue());
      return F.num(d);
    }

    @Override
    public IExpr e1DblComArg(IComplexNum cNum) {
      de.lab4inf.math.Complex c =
          new de.lab4inf.math.sets.ComplexNumber(cNum.reDoubleValue(), cNum.imDoubleValue());
      c = de.lab4inf.math.functions.Zeta.zeta(c);
      return F.complexNum(c.real(), c.imag());
    }

    @Override
    public IExpr e1ObjArg(final IExpr arg1) {
      if (arg1.isZero()) {
        return CN1D2;
      }
      if (arg1.isOne()) {
        return CComplexInfinity;
      }
      if (arg1.isMinusOne()) {
        // -1/12
        return QQ(-1, 12);
      }
      if (arg1.isInteger()) {
        IInteger n = (IInteger) arg1;

        if (!n.isPositive()) {
          if (n.isEven()) {
            return F.C0;
          }
          // http://fungrim.org/entry/51fd98/
          // Zeta(-n) := ((-1)^n/(n + 1))*BernoulliB(n + 1)
          n = n.negate();
          IExpr n1 = n.add(C1);
          return Times(Power(CN1, n), Power(n1, -1), BernoulliB(n1));
        }
        if (n.isEven()) {
          // http://fungrim.org/entry/72ccda/
          // Zeta(2*n) := ((((-1)^(n-1)*2^(-1+2*n)*Pi^(2*n))/(2*n)!)*BernoulliB(2*n)
          n = n.shiftRight(1);
          return Times(Power(CN1, Plus(CN1, n)), Power(C2, Plus(CN1, Times(C2, n))),
              Power(Pi, Times(C2, n)), Power(Factorial(Times(C2, n)), -1),
              BernoulliB(Times(C2, n)));
        }

      } else if (arg1.isInfinity()) {
        return C1;
      }
      return NIL;
    }

    @Override
    public IExpr e2ObjArg(IExpr s, IExpr a) {
      if (a.isZero()) {
        return Zeta(s);
      }
      if (a.isMinusOne()) {
        return Plus(C1, Zeta(s));
      }
      if (s.isInteger() && a.isInteger()) {
        if (!s.isPositive() || ((IInteger) s).isEven()) {
          int nInt = ((IInteger) a).toIntDefault(0);
          if (nInt < 0) {
            nInt *= -1;
            // Zeta(s, -n) := Zeta(s) + Sum(1/k^s, {k, 1, n})
            return Plus(F.sum(k -> Power(Power(k, s), -1), 1, nInt), Zeta(s));
          }
        }
      }
      if (a.isNumEqualRational(C2)) {
        return Plus(CN1, Zeta(s));
      }
      if (a.isNumEqualRational(C1D2)) {
        return Times(Plus(CN1, Sqr(s)), Zeta(s));
      }
      return NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SpecialFunctions() {}
}
