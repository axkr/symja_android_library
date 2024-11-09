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
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;
import static org.matheclipse.core.expression.S.Pi;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
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
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.IMatch;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class SpecialFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Beta.setEvaluator(new Beta());
      S.BetaRegularized.setEvaluator(new BetaRegularized());
      S.DirichletBeta.setEvaluator(new DirichletBeta());
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
      S.PolyGamma.setEvaluator(new PolyGamma());
      S.PolyLog.setEvaluator(new PolyLog());
      S.ProductLog.setEvaluator(new ProductLog());
      S.StieltjesGamma.setEvaluator(new StieltjesGamma());
      S.StruveH.setEvaluator(new StruveH());
      S.StruveL.setEvaluator(new StruveL());
      S.Zeta.setEvaluator(new Zeta());
    }
  }

  private static class Beta extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      switch (argSize) {
        case 4: {
          // generalized incomplete Beta(z1, z2, a, b)
          IExpr z1 = ast.arg1();
          IExpr z2 = ast.arg2();
          IExpr a = ast.arg3();
          IExpr b = ast.arg4();
          // -Beta(z1,a,b)+Beta(z2,a,b)
          return F.Plus(F.Negate(functionExpandOriginal(F.Beta(z1, a, b), engine)),
              functionExpandOriginal(F.Beta(z2, a, b), engine));
        }
        case 3: {

          IExpr z = ast.arg1();
          IExpr a = ast.arg2();
          IExpr b = ast.arg3();
          if (a.isInteger() && a.isPositive()) {
            // https://functions.wolfram.com/GammaBetaErf/Beta3/03/01/02/0003/
            IInteger n = (IInteger) a;
            int ni = a.toIntDefault();
            // Beta(n,b)*(1-(1-z)^b*Sum((z^k*Pochhammer(b,k))/k!,{k,0,-1+n}))
            IExpr sum = F.sum(
                k -> F.Times(F.Power(z, k), F.Power(F.Factorial(k), F.CN1), F.Pochhammer(b, k)), 0,
                ni - 1);
            return F.Times(functionExpandOriginal(F.Beta(n, b), engine),
                F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Subtract(F.C1, z), b), sum)));
          }
        }
          break;

        case 2: {
          IExpr a = ast.arg1();
          IExpr b = ast.arg2();

          if (a.isInteger() && a.isPositive()) {
            IInteger n = (IInteger) a;
            int ni = n.toIntDefault();
            IASTAppendable timsAST = F.TimesAlloc(ni + 1);
            for (int i = 0; i < ni; i++) {
              timsAST.append(F.Plus(F.ZZ(i), b));
            }
            return F.Divide(F.Factorial(F.Plus(F.CN1, n)), timsAST);
          }
          // (Gamma(a)*Gamma(b))/Gamma(a+b)
          return F.Times(F.Gamma(a), F.Gamma(b), F.Power(F.Gamma(F.Plus(a, b)), F.CN1));
        }
      }
      return F.NIL;

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 4) {
        // generalized incomplete Beta(z1, z2, a, b)
        IExpr z1 = ast.arg1();
        IExpr z2 = ast.arg2();
        IExpr a = ast.arg3();
        IExpr b = ast.arg4();

        return generalizedIncompleteBeta(z1, z2, a, b);
      }
      if (ast.isAST3()) {
        // incomplete Beta(z, a, b)
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr b = ast.arg3();
        return incompleteBeta(z, a, b);
      }
      if (ast.isAST2()) {
        // Beta(a,b)
        IExpr a = ast.arg1();
        IExpr b = ast.arg2();
        return beta(a, b);
      }
      return F.NIL;
    }

    /**
     * Beta(a,b) function.
     * 
     * @param a
     * @param b
     * @param ast
     * @param engine
     * 
     * @return
     */
    private static IExpr beta(IExpr a, IExpr b) {
      if (a.isZero() || b.isZero()) {
        return F.CComplexInfinity;
      }
      EvalEngine engine = EvalEngine.get();
      try {
        // if (engine.isDoubleMode()) {
        //
        // double aDouble = Double.NaN;
        // double bDouble = Double.NaN;
        // try {
        // aDouble = a.evalf();
        // bDouble = b.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(aDouble) || Double.isNaN(bDouble)) {
        // Complex ac = a.evalfc();
        // Complex bc = b.evalfc();
        //
        // return F.complexNum(GammaJS.beta(ac, bc));
        //
        // } else {
        // return F.num(GammaJS.beta(aDouble, bDouble));
        // }
        // }
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
        Errors.printMessage(S.Beta, te, engine);
        return te.getValue();
      } catch (ValidateException ve) {
        return Errors.printMessage(S.Beta, ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Beta, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Incomplete Beta(z, a, b)
     * 
     * @param z
     * @param a
     * @param b
     * 
     * @return
     */
    private static IExpr incompleteBeta(IExpr z, IExpr a, IExpr b) {
      EvalEngine engine = EvalEngine.get();
      try {
        if (z.isZero()) {
          IExpr re = a.re();
          if (re.isPositiveResult()) {
            return F.C0;
          }
          if (re.isNegativeResult()) {
            return F.CComplexInfinity;
          }
        }

        // if (engine.isDoubleMode()) {
        // double aDouble = Double.NaN;
        // double bDouble = Double.NaN;
        // double zDouble = Double.NaN;
        // try {
        // zDouble = z.evalf();
        // aDouble = a.evalf();
        // bDouble = b.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
        // Complex zc = z.evalfc();
        // Complex ac = a.evalfc();
        // Complex bc = b.evalfc();
        //
        // return F.complexNum(GammaJS.beta(zc, ac, bc));
        //
        // } else {
        // return GammaJS.incompleteBeta(zDouble, aDouble, bDouble);
        // }
        // }

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
        Errors.printMessage(S.Beta, te, engine);
        return te.getValue();
      } catch (ValidateException ve) {
        return Errors.printMessage(S.Beta, ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Beta, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Generalized incomplete Beta(z1, z2, a, b)
     * 
     * @param z1
     * @param z2
     * @param a
     * @param b
     * @return
     */
    private static IExpr generalizedIncompleteBeta(IExpr z1, IExpr z2, IExpr a, IExpr b) {
      if (z2.isZero()) {
        IExpr aRe = a.re();
        if (aRe.isPositive()) {
          // https://functions.wolfram.com/GammaBetaErf/Beta4/03/01/03/0001/
          return F.Negate(F.Beta(z1, a, b));
        }
        if (aRe.isNegative()) {
          // https://functions.wolfram.com/GammaBetaErf/Beta4/03/01/03/0002/
          return F.CComplexInfinity;
        }
      }

      if (z2.isOne()) {
        IExpr bRe = b.re();
        if (bRe.isPositive()) {
          // https://functions.wolfram.com/GammaBetaErf/Beta4/03/01/03/0003/
          return F.Subtract(F.Beta(a, b), F.Beta(z1, a, b));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2()) {
        // beta:
        IInexactNumber a = (IInexactNumber) ast.arg1();
        IInexactNumber b = (IInexactNumber) ast.arg2();
        return a.beta(b);
      } else if (ast.isAST3()) {
        // incomplete beta:
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IInexactNumber a = (IInexactNumber) ast.arg2();
        IInexactNumber b = (IInexactNumber) ast.arg3();
        return z.beta(a, b);
      } else if (ast.argSize() == 4) {
        // generalized incomplete beta:
        IInexactNumber z0 = (IInexactNumber) ast.arg1();
        IInexactNumber z1 = (IInexactNumber) ast.arg2();
        IInexactNumber a = (IInexactNumber) ast.arg3();
        IInexactNumber b = (IInexactNumber) ast.arg4();
        return z0.beta(z1, a, b);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class BetaRegularized extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      switch (argSize) {
        case 4: {
          // generalized incomplete Beta(z1, z2, a, b)
          IExpr z1 = ast.arg1();
          IExpr z2 = ast.arg2();
          IExpr a = ast.arg3();
          IExpr b = ast.arg4();
          // ((-Beta(z1,a,b)+Beta(z2,a,b))*Gamma(a+b))/(Gamma(a)*Gamma(b))
          return F.Times(F.Plus(F.Negate(F.Beta(z1, a, b)), F.Beta(z2, a, b)),
              F.Power(F.Times(F.Gamma(a), F.Gamma(b)), F.CN1), F.Gamma(F.Plus(a, b)));
        }
        case 3: {
          IExpr z = ast.arg1();
          IExpr a = ast.arg2();
          IExpr b = ast.arg3();
          // (Beta(z,a,b)*Gamma(a+b))/(Gamma(a)*Gamma(b))
          return F.Times(F.Beta(z, a, b), F.Power(F.Times(F.Gamma(a), F.Gamma(b)), F.CN1),
              F.Gamma(F.Plus(a, b)));
        }

      }
      return F.NIL;

    }

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

    private IExpr betaRegularized3(final IAST ast, EvalEngine engine) {

      try {
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr b = ast.arg3();
        if (a.isZero() || (a.isInteger() && a.isNegative())) {
          if (b.isZero() || (b.isInteger() && b.isNegative())) {
            return S.Indeterminate;
          }
          return F.C1;
        }
        if (b.isZero() || (b.isInteger() && b.isNegative())) {
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
          if (engine.evalGreater(F.Re(b), F.C0)) {
            return F.C1;
          }
        }
        int bi = b.toIntDefault();
        if (bi != Integer.MIN_VALUE) {
          if (bi < 0) {
            // https://functions.wolfram.com/GammaBetaErf/BetaRegularized/03/01/01/0002/
            return F.C0;
          }
        }
        if (engine.isNumericMode()) {
          if (engine.isDoubleMode()) {
            try {
              double zd = engine.evalDouble(z);
              double ad = engine.evalDouble(a);
              double bd = engine.evalDouble(b);
              int iterationLimit = EvalEngine.get().getIterationLimit();
              int aInt = (int) ad;
              if (aInt > iterationLimit && iterationLimit > 0) {
                IterationLimitExceeded.throwIt(aInt, ast.topHead());
              }
              int nInt = (int) bd;
              if (nInt > iterationLimit && iterationLimit > 0) {
                IterationLimitExceeded.throwIt(nInt, ast.topHead());
              }
              // TODO improve with regularizedIncompleteBetaFunction() ???
              // https://github.com/haifengl/smile/blob/master/math/src/main/java/smile/math/special/Beta.java
              return F.num(GammaJS.betaRegularized(zd, ad, bd));
            } catch (ValidateException ve) {
              // from org.matheclipse.core.eval.EvalEngine.evalDouble()
            }
          }
          if (z.isNumber() && a.isNumber() && b.isNumber()) {
            return functionExpand(ast, engine);
          }
        }
        if (bi != Integer.MIN_VALUE) {
          if (bi > Config.MAX_POLYNOMIAL_DEGREE) {
            PolynomialDegreeLimitExceeded.throwIt(bi);
          }
          // https://functions.wolfram.com/GammaBetaErf/BetaRegularized/03/01/01/0001/

          // ((1-z)^k*Pochhammer(a,k))/k!
          IExpr sum = F.sum(k -> F.Times(F.Power(F.Subtract(F.C1, z), k),
              F.Power(F.Factorial(k), F.CN1), F.Pochhammer(a, k)), 0, bi - 1);
          // z^a * sum
          return F.Times(F.Power(z, a), sum);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.BetaRegularized, rex, engine);
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
    private IExpr betaRegularized4(final IAST ast, EvalEngine engine) {
      try {
        IExpr z1 = ast.arg1();
        IExpr z2 = ast.arg2();
        IExpr a = ast.arg3();
        IExpr b = ast.arg4();
        int bi = b.toIntDefault();
        if (bi != Integer.MIN_VALUE) {
          if (bi < 0) {
            // https://functions.wolfram.com/GammaBetaErf/BetaRegularized4/03/01/01/0001/
            return F.C0;
          }
        }
        if (engine.isNumericMode()) {
          if (z1.isNumber() && z2.isNumber() && a.isNumber() && b.isNumber()) {
            return functionExpand(ast, engine);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.BetaRegularized, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class DirichletBeta extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // (Zeta(z,1/4)/2^z-Zeta(z,3/4)/2^z)/2^z
        IExpr z = ast.arg1();
        IExpr v1 = F.Power(F.Power(F.C2, z), F.CN1);
        return F.Times(v1,
            F.Plus(F.Times(v1, F.Zeta(z, F.C1D4)), F.Times(F.CN1, v1, F.Zeta(z, F.QQ(3L, 4L)))));
      }
      return F.NIL;
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isMinusOne()) {
        return F.C0;
      }
      if (z.isZero()) {
        return F.C1D2;
      }
      if (z.isOne()) {
        return F.CPiQuarter;
      }
      if (engine.isNumericMode()) {
        return functionExpand(ast, engine);
      }
      return NIL;
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

  private static final class DirichletEta extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // (1-2^(1-z))*Zeta(z)
        IExpr z = ast.arg1();
        return F.Times(F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, z))), F.Zeta(z));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isMinusOne()) {
        return F.C1D4;
      }
      if (z.isZero()) {
        return F.C1D2;
      }
      if (z.isOne()) {
        return F.Log(F.C2);
      }
      if (engine.isNumericMode() || z.isInteger()) {
        return functionExpand(ast, engine);
      }
      return NIL;
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
   * Returns the error function.
   *
   * @see org.matheclipse.core.reflection.system.InverseErf
   */
  private static final class Erf extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        // Erf(z2) - Erf(z1)
        return F.Subtract(F.Erf(ast.arg2()), F.Erf(ast.arg1()));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr z0 = ast.arg1();
        IExpr z1 = ast.arg2();
        if (z0.isZero()) {
          return F.Erf(z1);
        }
        if (z1.isZero()) {
          return F.Negate(F.Erf(z0));
        }
        if (z0.isNegativeInfinity() && z1.isInfinity()) {
          return F.C2;
        }
        return F.NIL;
      }

      IExpr z = ast.arg1();
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isInfinity()) {
        return F.C1;
      }
      if (z.isNegativeInfinity()) {
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.erf();
      } else if (ast.argSize() == 2) {
        IInexactNumber z1 = (IInexactNumber) ast.arg1();
        IInexactNumber z2 = (IInexactNumber) ast.arg2();
        IExpr erf1 = z1.erf();
        IExpr erf2 = z2.erf();
        if (erf1.isPresent() && erf2.isPresent()) {
          return erf2.subtract(erf1);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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

  private static final class Erfc extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
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
        return F.NIL;
      } else if (z.isAST(S.InverseErfc, 2)) {
        return z.first();
      }
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // https://functions.wolfram.com/GammaBetaErf/Erf/16/01/01/0002/
        return F.Times(S.I, F.Erfi(F.Times(F.CNI, z)));
      }
      // don't transform negative arg:
      // IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      // if (negExpr.isPresent()) {
      // return F.Subtract(F.C2, F.Erfc(negExpr));
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.erfc();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
   * Returns the imaginary error function.
   *
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
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // https://functions.wolfram.com/GammaBetaErf/Erfi/16/01/01/0002/
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.erfi();
      }
      // if (ast.argSize() == 1) {
      // IInexactNumber z = (IInexactNumber) ast.arg1();
      // // -I * Erf(I*z)
      // IExpr t = z.times(F.CI).erf();
      // if (t.isPresent()) {
      // return F.CNI.times(t);
      // }
      //
      // }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        Errors.rethrowsInterruptException(rex);
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

      if (engine.isNumericMode()) {
        if (a.isNumber() && z1.isNumber()) {
          // Gamma(a,z1)/Gamma(a)
          return F.Times(F.Power(F.Gamma(a), F.CN1), F.Gamma(a, z1));
        }
      }
      return F.NIL;
    }

    private static IExpr gammaRegularzed3(IExpr a, IExpr z1, IExpr z2, final IAST ast,
        EvalEngine engine) {
      if (a.isOne()) {
        // E^(-arg2)-E^(-arg3)
        return F.Subtract(F.Power(S.E, F.Negate(z1)), F.Power(S.E, F.Negate(z2)));
      }
      if (a.isZero()) {
        return F.C0;
      }
      if (a.isInteger() && a.isNegative()) {
        return F.C0;
      }
      if (engine.isNumericMode()) {
        if (a.isNumber() && z1.isNumber() && z2.isNumber()) {
          // Gamma(a,z1)/Gamma(a)-Gamma(a,z2)/Gamma(a)
          IExpr v1 = F.Power(F.Gamma(a), F.CN1);
          return F.Plus(F.Times(v1, F.Gamma(a, z1)), F.Times(F.CN1, v1, F.Gamma(a, z2)));
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class HurwitzLerchPhi extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match4(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return HurwitzLerchPhiRules.match4(ast, engine);
    }


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
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
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
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);

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
      if (a.isMathematicalIntegerNegative()) {
        return F.CComplexInfinity;
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
          Errors.printMessage(S.HurwitzZeta, te, engine);
          return te.getValue();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.HurwitzZeta, rex, engine);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
  private static final class InverseErf extends AbstractFunctionEvaluator implements INumeric {

    // public IExpr e1DblArg(final double arg1) {
    // try {
    // if (arg1 >= -1.0 && arg1 <= 1.0) {
    // return Num.valueOf(org.hipparchus.special.Erf.erfInv(arg1));
    // }
    // } catch (final MathIllegalStateException e) {
    // }
    // return F.NIL;
    // }

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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        // if (arg1.isList()) {
        // return ((IAST) arg1).mapThread(x -> F.InverseErf(x));
        // }
        if (arg1.isZero()) {
          return F.C0;
        }
        if (arg1.isOne()) {
          return F.CInfinity;
        }
        if (arg1.isMinusOne()) {
          return F.CNInfinity;
        }
        return F.NIL;
      }
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg1.isZero()) {
          return F.InverseErf(arg2);
        } else if (arg1.isInfinity()) {
          return F.InverseErfc(arg2.negate());
        }
        if (arg2.isZero()) {
          if (arg1.isOne()) {
            return F.C1;
          }
          if (arg1.isMinusOne()) {
            return F.CN1;
          }
          return arg1;
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.inverseErf();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.inverseErfc();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        Errors.rethrowsInterruptException(rex);
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class LerchPhi extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match4(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return LerchPhiRules.match4(ast, engine);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr s = ast.arg2();
      IExpr a = ast.arg3();
      if (s.isZero()) {
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/02/01/0007/
        return F.Power(F.Subtract(F.C1, z), F.CN1);
      }

      if (z.isZero()) {
        if (s.isOne() && a.isZero()) {
          // LerchPhi(0,1,0)
          return F.C0;
        }
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/03/01/0002/
        // (a^2)^(-s/2)
        return F.Power(F.Power(a, 2), s.isOne() ? F.CN1D2 : F.Times(F.CN1D2, s));
      }
      if (z.isMinusOne()) {
        if (s.isOne() && a.isZero()) {
          // LerchPhi(-1,1,0)
          // -Log(2)
          return F.Negate(F.Log(F.C2));
        }
        if (s.isNumEqualInteger(F.C2) && a.isRationalValue(F.C1D2)) {
          // LerchPhi(-1,2,1/2)
          // 4*Catalan
          return F.Times(F.C4, F.Catalan);
        }
        if (a.isOne()) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/05/01/
          // (1-2^(1-s))*Zeta(s)
          return F.Times(F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, s))), F.Zeta(s));
        }
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/03/01/0001/
        // Zeta(s,a/2)/2^s-Zeta(s,1/2*(1+a))/2^s
        return F.Plus(F.Times(F.Power(F.Power(F.C2, s), F.CN1), F.Zeta(s, F.Times(F.C1D2, a))),
            F.Times(F.CN1, F.Power(F.Power(F.C2, s), F.CN1),
                F.Zeta(s, F.Times(F.C1D2, F.Plus(F.C1, a)))));
      }
      if (z.isOne()) {
        if (s.isOne() && a.isNumber()) {
          return F.CInfinity;
        }
        if (s.isNumEqualInteger(F.C2) && a.isOne()) {
          // LerchPhi(1, 2, 1)
          // Pi^2/6
          return F.Times(F.QQ(1L, 6L), F.Sqr(F.Pi));
        }


        if (a.isOne()) {
          IExpr re = s.re();
          if (re.isReal() && ((IReal) re).isGT(F.C1)) {
            // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/05/01/0001/
            return F.Zeta(s);
          }
        }
      } else if (z.isNumEqualInteger(F.C2)) {
        if (s.isOne() && a.isZero()) {
          // LerchPhi(2, 1, 0)
          // -I*Pi
          F.Times(F.CNI, F.Pi);
        }
      } else if (z.equals(F.CC(1, 2, -1, 2))) {
        if (s.isNumEqualInteger(F.C2) && a.isOne()) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/02/01/0003/
          // LerchPhi(1/2-1/2*I, 2, 1)
          // (1+I)*PolyLog(2,1/2-I/2)
          return F.Times(F.Plus(F.C1, F.CI), F.PolyLog(F.C2, F.CC(1, 2, -1, 2)));
        }

      }

      int n = a.toIntDefault();
      if (n != Integer.MIN_VALUE) {
        IExpr polyLog = engine.evaluate(F.PolyLog(s, z));
        if (n <= 0) {
          n = -n;
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/01/01/0002/
          // z^n*(PolyLog(s,z)+Sum(1/(z^k*k^s),{k,1,n}))
          return F.Times(F.Power(z, n), //
              F.Plus(polyLog, //
                  F.sum(k -> F.Power(F.Times(F.Power(z, k), F.Power(k, s)), F.CN1), 1, n)));
        } else {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/01/01/0009/
          // (PolyLog(s,z)-Sum(z^k/k^s,{k,1,-1+n}))/z^n
          return F.Times(F.Power(z, -n), //
              F.Subtract(polyLog, //
                  F.sum(k -> F.Times(F.Power(F.Power(k, s), F.CN1), F.Power(z, k)), 1, n - 1)));
        }
      }


      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.logGamma();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }



  private static class PolyGamma extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return PolyGammaRules.match3(ast, engine);
    }

    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.digamma(arg1));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);

      }
      return F.NIL;
    }

    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.digamma(arg1));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);

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
        if (arg2.isZero() && arg1.isNumber()) {
          IReal realPart = ((INumber) arg1).re();
          if (realPart.isLT(F.CN1)) {
            // https://github.com/mtommila/apfloat/issues/54
            // https://functions.wolfram.com/GammaBetaErf/PolyGamma2/03/01/01/0002/
            return F.C0;
          }
        }
        if (engine.isNumericMode()) {
          if (arg1.isZero()) {
            return arg2.digamma();
          }
          long n = arg1.toLongDefault();
          if (n != Long.MIN_VALUE && arg2.isNumber()) {
            return arg2.polyGamma(n);
          }
        }
        // if (engine.isDoubleMode()) {
        // try {
        // int n = arg1.toIntDefault();
        // if (n >= 0) {
        // double xDouble = Double.NaN;
        // try {
        // xDouble = arg2.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(xDouble)) {
        // // Complex xc = arg2.evalComplex();
        // //
        // // return
        // } else {
        // if (n == 0) {
        // return F.num(GammaJS.polyGamma(xDouble));
        // }
        // return F.num(GammaJS.polyGamma(n, xDouble));
        // }
        // }
        // } catch (ValidateException ve) {
        // return Errors.printMessage(ast.topHead(), ve, engine);
        // } catch (ThrowException te) {
        // Errors.printMessage(S.PolyGamma, te, engine);
        // return te.getValue();
        // } catch (RuntimeException rex) {
        // Errors.printMessage(S.PolyGamma, rex, engine);
        // }
        // }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class PolyLog extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return PolyLogRules.match3(ast, engine);
    }


    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/PolyLog2/03/01/0002/
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        if (n.isNumEqualInteger(F.C2) && z.isPower() && z.base().isE() && z.exponent().isTimes2()) {
          // PolyLog(2, E^( exponent_ ))
          IAST timesAST = (IAST) z.exponent();
          if (timesAST.arg1().isComplex() && timesAST.arg2().isPi()) {
            IComplex c1 = (IComplex) timesAST.arg1();
            if (c1.getRealPart().isZero()) {
              IRational imag = c1.getImaginaryPart();
              IInteger p = imag.numerator();
              if (p.isEven() && p.isPositive()) {
                p = p.div(2);
                IInteger q = imag.denominator();
                int pInt = p.toIntDefault();
                int qInt = q.toIntDefault();
                if (pInt > 0 && qInt > 0 && pInt <= qInt) {
                  // Pi^2/(6*q^2)+Sum(E^((2*Pi*I*k*p)/q)*PolyGamma(1,k/q),{k,1,-1+q})/q^2
                  IFraction qR1 = F.QQ(1, qInt);
                  IFraction qR2 = F.QQ(F.C1, q.multiply(q));
                  return F.Plus(F.Times(F.QQ(1L, 6L), F.Sqr(F.Pi), F.Power(q, F.CN2)), F.Times(qR2, //
                      F.sum(
                          k -> F.Times(F.Exp(F.Times(c1, F.Pi, k)),
                              F.PolyGamma(F.C1, F.Times(k, qR1))), //
                          1, --qInt, 1)));
                }
              }
            }
          }
        }
      }
      return F.NIL;
    }

    /**
     * See <a href=
     * "https://github.com/sympy/sympy/blob/master/sympy/functions/special/zeta_functions.py">Sympy
     * - zeta_functions.py</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr x = ast.arg2();
      if (ast.isAST3()) {
        IExpr z = ast.arg3();
        IExpr temp = polyLogSymbolic(n, x, z);
        if (temp.isPresent()) {
          return temp;
        }
        return F.NIL;
      }
      IExpr temp = polyLogSymbolic(n, x);
      if (temp.isPresent()) {
        return temp;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber v = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();

        IExpr temp = polyLogSymbolic(v, z);
        if (temp.isPresent()) {
          return temp;
        }
        // issue #929
        return v.polyLog(z);
      }
      return F.NIL;
    }

    private IExpr polyLogSymbolic(IExpr n, IExpr z) {
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isOne()) {
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
      } else if (z.isMinusOne()) {
        // (2^(1-arg1)-1)*Zeta(arg1)
        return Times(Plus(CN1, Power(C2, Plus(C1, Negate(n)))), Zeta(n));
      }

      if (n.isReal()) {
        if (z.isInfinity() || z.isNegativeInfinity()) {
          if (n.isZero() || n.isMathematicalIntegerNegative()) {
            return S.Indeterminate;
          }
          if (n.isMathematicalIntegerNonNegative()) {
            return F.CNInfinity;
          }
        }
        if (n.isZero()) {
          // arg2/(1 - arg2)
          return Times(z, Power(Plus(C1, Negate(z)), -1));
        } else if (n.isOne()) {
          // -Log(1 - arg2))
          return Negate(Log(Plus(C1, Negate(z))));
        } else if (n.isMinusOne()) {
          // arg2/(arg2 - 1)^2
          return Times(z, Power(Plus(C1, Negate(z)), -2));
        }
      }
      return F.NIL;
    }

    private IExpr polyLogSymbolic(IExpr n, IExpr p, IExpr z) {
      if (z.isZero()) {
        return F.C0;
      }
      if (n.isOne()) {
        int pInt = p.toIntDefault();
        if (pInt > 1) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/PolyLog3/03/01/03/0002/
          IExpr v4 = F.CN1.pow(pInt);
          IExpr sum = F.sum(k -> F.Times(F.Power(F.Factorial(F.Plus(F.CN1, F.Negate(k), p)), F.CN1),
              F.Power(F.CN1, k), F.Power(F.Log(F.Subtract(F.C1, z)), F.Plus(F.CN1, F.Negate(k), p)),
              F.PolyLog(F.Plus(k, F.C2), F.Subtract(F.C1, z))), 0, pInt - 1);
          return F
              .Plus(
                  F.Times(v4, F.Power(F.Factorial(p), F.CN1),
                      F.Power(F.Log(F.Subtract(F.C1, z)), p), F.Log(z)),
                  F.Times(v4, sum), F.Zeta(F.Plus(p, F.C1)));
        }
      }
      if (p.isOne()) {
        return F.PolyLog(n.inc(), z);
      }

      if (n.isReal()) {
        if (n.isZero()) {
          // (-Log[1-z])^p/Gamma[p+1]
          return F.Times(F.Power(F.Gamma(F.Plus(p, F.C1)), F.CN1),
              F.Power(F.Negate(F.Log(F.Subtract(F.C1, z))), p));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
        Apcomplex ac = d.apcomplexValue();
        Apcomplex productLog = h.w(ac);
        return F.complexNum(productLog.real().doubleValue(), productLog.imag().doubleValue());
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);

      }
      Apcomplex c = ApcomplexMath.w(new Apfloat(d.doubleValue()));
      return F.complexNum(c.real().doubleValue(), c.imag().doubleValue());
    }

    @Override
    public IExpr e1DblComArg(IComplexNum arg1) {
      if (arg1.isZero()) {
        return arg1;
      }
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
      Apcomplex ac = arg1.apcomplexValue();
      Apcomplex productLog = h.w(ac);
      return F.complexNum(productLog.real().doubleValue(), productLog.imag().doubleValue());
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
        Errors.rethrowsInterruptException(ce);

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
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      IExpr temp = functionExpandLogArg(o);
      if (temp.isPresent()) {
        return temp;
      }
      if (o.equals(F.C0) || o.equals(F.CD0)) {
        return F.C0;
      }
      return F.NIL;
    }

    private static IExpr functionExpandLogArg(final IExpr o) {
      if (o.isTimes() && o.first().isFraction() && o.argSize() == 3) {
        // ProductLog(Rational(k_,n_)*b_^Rational(c_,n_)*Log(b_)) :=
        // Module( {a, v},
        // a = N( (n*ProductLog((b^(c/n)*k*Log(b))/n))/Log(b) );
        // v = Rationalize(a);
        // v*Log(b)/n
        // /; IntegerQ(v) && v >= 1 && PossibleZeroQ( (((-b^(c/n))*k + b^(v/n)*v)*Log(b))/n ))
        IAST times = (IAST) o;
        if (times.arg2().isPower() && times.arg3().isLog()) {
          IAST power = (IAST) times.arg2();
          if (power.base().isInteger() && power.base().equals(times.arg3().first())
              && power.exponent().isFraction()) {
            IInteger b = (IInteger) times.arg3().first();
            IFraction arg1 = (IFraction) times.arg1();
            IInteger k = arg1.numerator();
            IInteger n = arg1.denominator();
            IFraction powExponent = (IFraction) power.exponent();
            if (n.equals(powExponent.denominator())) {
              EvalEngine engine = EvalEngine.get();
              IExpr a = engine.evalNumericFunction(
                  F.Times(n, F.ProductLog(F.Times(arg1, F.Power(b, powExponent), F.Log(b))),
                      F.Power(F.Log(b), F.CN1)));
              IExpr v = engine.evaluate(F.Rationalize(a));
              if (v.isInteger() && ((IInteger) v).isGE(F.C1)) {
                IFraction resultFactor = F.QQ((IInteger) v, n);
                IExpr isZero = engine.evaluate(F.Plus(F.Times(k.negate(), F.Power(b, powExponent)),
                    F.Times(v, F.Power(b, resultFactor))));
                if (isZero.isZero()) {
                  return engine.evaluate(F.Times(resultFactor, F.Log(b)));
                }
              }
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ObjArg(IExpr k, IExpr z) {
      int ki = Integer.MIN_VALUE;
      if (z.equals(F.C0) || z.equals(F.CD0)) {
        // ProductLog(k_?NumberQ,0) := -Infinity/;k!=0
        if (k.isNonZeroComplexResult()) {
          return F.CNInfinity;
        }
      }
      if (k.isNumber()) {
        ki = k.toIntDefault();
        EvalEngine engine = EvalEngine.get();
        if (ki == Integer.MIN_VALUE) {
          // Machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.ProductLog, "intm", F.list(F.ProductLog(k, z), F.C1),
              engine);
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
          if (engine.isArbitraryMode()) {
            if (z instanceof ApcomplexNum) {
              FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
              Apcomplex ac = ((ApcomplexNum) z).apcomplexValue();
              Apcomplex productLog = h.w(ac, ki);
              return F.complexNum(productLog);
            } else if (z instanceof ApfloatNum) {
              FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
              Apcomplex ac = ((ApfloatNum) z).apcomplexValue();
              Apcomplex productLog = h.w(ac, ki);
              return F.complexNum(productLog);
            }
          }

          if (z instanceof ComplexNum) {
            FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble(engine);
            Apcomplex ac = ((ComplexNum) z).apcomplexValue();
            Apcomplex productLog = h.w(ac, ki);
            return F.complexNum(productLog.real().doubleValue(), productLog.imag().doubleValue());
          } else if (z instanceof Num) {
            FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble(engine);
            Apcomplex ac = ((Num) z).apcomplexValue();
            Apcomplex productLog = h.w(ac, ki);
            return F.complexNum(productLog.real().doubleValue(), productLog.imag().doubleValue());
          }
        }
      }

      return super.e2ObjArg(k, z);
    }
  }


  private static class StieltjesGamma extends AbstractFunctionEvaluator implements IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return StieltjesGammaRules.match3(ast, engine);
    }


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      if ((n.isNumber() && !n.isInteger()) || n.isNegativeResult()) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.StieltjesGamma, "intnm", F.List(ast, F.C1), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class StruveH extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return StruveHRules.match3(ast, engine);
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        IExpr restExpr = AbstractFunctionEvaluator.extractImaginaryUnit(z);
        if (restExpr.isPresent()) {
          // ((I*z)^(1+n)*StruveL(n,z))/z^(1+n)
          return F.Times(F.Power(F.Times(F.CI, restExpr), F.Plus(F.C1, n)),
              F.Power(restExpr, F.Subtract(F.CN1, n)), F.StruveL(n, restExpr));
        }
      }
      return F.NIL;
    }
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
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.StruveH, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class StruveL extends AbstractFunctionEvaluator
      implements IFunctionExpand, IMatch {
    @Override
    public IExpr match3(IAST ast, EvalEngine engine) {
      return F.NIL;
      // return StruveLRules.match3(ast, engine);
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr n = ast.arg1();
        IExpr z = ast.arg2();
        IExpr restExpr = AbstractFunctionEvaluator.extractImaginaryUnit(z);
        if (restExpr.isPresent()) {
          // ((I*z)^(1+n)*StruveH(n,z))/z^(1+n)
          return F.Times(F.Power(F.Times(F.CI, restExpr), F.Plus(F.C1, n)),
              F.Power(restExpr, F.Subtract(F.CN1, n)), F.StruveH(n, restExpr));
        }
      }
      return F.NIL;
    }

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
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.StruveL, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
        Errors.rethrowsInterruptException(ce);

      }
      return F.NIL;
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(arg1));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e1DblArg(INum num) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
      try {
        Apcomplex zeta = h.zeta(num.apfloatValue());
        return F.num(zeta.doubleValue());
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e1DblComArg(IComplexNum cNum) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloatDouble();
      try {
        Apcomplex zeta = h.zeta(cNum.apcomplexValue());
        return F.complexNum(zeta.real().doubleValue(), zeta.imag().doubleValue());
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApfloatArg(ApfloatNum a1, ApfloatNum a2) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      try {
        return F.num(h.zeta(a1.apfloatValue(), a2.apfloatValue()));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ApcomplexArg(ApcomplexNum a1, ApcomplexNum a2) {
      FixedPrecisionApcomplexHelper h = EvalEngine.getApfloat();
      try {
        return F.complexNum(h.zeta(a1.apcomplexValue(), a2.apcomplexValue()));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);
      }
      return F.NIL;
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
      int sInt = s.toIntDefault();
      if (sInt != Integer.MIN_VALUE) {
        if (sInt <= 0 || (sInt % 2) == 0) {
          int aInt = a.toIntDefault(0);
          if (aInt < 0) {
            aInt *= -1;
            // Zeta(s, -n) := Zeta(s) + Sum(1/k^s, {k, 1, n})
            return Plus(F.sum(k -> Power(Power(k, s), -1), 1, aInt), Zeta(s));
          }
        }

      }
      if (a.isNumEqualRational(C2)) {
        return Plus(CN1, Zeta(s));
      }
      if (a.isNumEqualRational(C1D2)) {
        return Times(Plus(CN1, F.Power(F.C2, s)), Zeta(s));
      }
      EvalEngine engine = EvalEngine.get();
      if (engine.isDoubleMode()) {
        if (a.re().isPositive()) {
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
            return Errors.printMessage(S.Zeta, ve, engine);
          } catch (ThrowException te) {
            Errors.printMessage(S.Zeta, te, engine);
            return te.getValue();
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            Errors.printMessage(S.Zeta, rex, engine);
          }
        }
      }
      if (engine.isArbitraryMode()) {
        if (a.re().isPositive()) {
          if (s instanceof ApfloatNum && a instanceof ApfloatNum) {
            return e2ApfloatArg(((ApfloatNum) s), ((ApfloatNum) a));
          }
          if (s instanceof ApcomplexNum && a instanceof ApcomplexNum) {
            return e2ApcomplexArg((ApcomplexNum) s, (ApcomplexNum) a);
          }
        }
      }
      return NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
