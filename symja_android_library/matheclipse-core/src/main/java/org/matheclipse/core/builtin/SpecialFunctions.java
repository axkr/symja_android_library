package org.matheclipse.core.builtin;

import org.matheclipse.external.apfloat.ApcomplexParabolicCylinderMath;
import org.matheclipse.external.apfloat.ApcomplexCoulombMath;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import org.matheclipse.core.numerics.functions.CarlsonArbitraryPrecision;
import org.matheclipse.external.apfloat.ApcomplexZetaMath;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.PolynomialDegreeLimitExceeded;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.DMath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
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
import org.matheclipse.core.numerics.functions.GammaJS;
import org.matheclipse.core.numerics.functions.HurwitzZetaKernel;
import org.matheclipse.core.numerics.functions.InverseGammaBetaJS;
import org.matheclipse.external.apfloat.ApcomplexMathieuMath;
import org.matheclipse.external.apfloat.ApcomplexAsymptoticMath;
import org.matheclipse.external.apfloat.ApcomplexStruveMath;
import org.matheclipse.external.apfloat.ApfloatOwenMath;
import org.matheclipse.core.numerics.functions.WorkingPrecision;

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
      S.DirichletLambda.setEvaluator(new DirichletLambda());
      S.Erf.setEvaluator(new Erf());
      S.Erfc.setEvaluator(new Erfc());
      S.Erfi.setEvaluator(new Erfi());
      S.GammaRegularized.setEvaluator(new GammaRegularized());
      S.HurwitzLerchPhi.setEvaluator(new HurwitzLerchPhi());
      S.HypergeometricPFQRegularized.setEvaluator(new HypergeometricPFQRegularized());
      S.CoulombF.setEvaluator(new Numeric(3,
          a -> ApcomplexCoulombMath.coulombF(a[0], a[1], a[2])));
      S.CoulombG.setEvaluator(new Numeric(3,
          a -> ApcomplexCoulombMath.coulombG(a[0], a[1], a[2])));
      S.ParabolicCylinderD.setEvaluator(new Numeric(2,
          a -> ApcomplexParabolicCylinderMath.parabolicCylinderD(a[0], a[1])));
      S.ScorerGi.setEvaluator(new Numeric(1, a -> ApcomplexParabolicCylinderMath.scorerGi(a[0])));
      S.ScorerHi.setEvaluator(new Numeric(1, a -> ApcomplexParabolicCylinderMath.scorerHi(a[0])));
      S.InverseErf.setEvaluator(new InverseErf());
      S.InverseErfc.setEvaluator(new InverseErfc());
      S.InverseBetaRegularized.setEvaluator(new InverseBetaRegularized());
      S.InverseGammaRegularized.setEvaluator(new InverseGammaRegularized());
      S.LerchPhi.setEvaluator(new LerchPhi());
      S.LogGamma.setEvaluator(new LogGamma());
      S.OwenT.setEvaluator(new OwenT());
      S.MathieuC.setEvaluator(new Mathieu(MathieuKind.C));
      S.MathieuCharacteristicA.setEvaluator(new MathieuCharacteristicValue(false));
      S.MathieuCharacteristicB.setEvaluator(new MathieuCharacteristicValue(true));
      S.MathieuCharacteristicExponent.setEvaluator(new MathieuCharacteristicExponent());
      S.MathieuCPrime.setEvaluator(new Mathieu(MathieuKind.C_PRIME));
      S.MathieuS.setEvaluator(new Mathieu(MathieuKind.S));
      S.MathieuSPrime.setEvaluator(new Mathieu(MathieuKind.S_PRIME));
      S.PolyGamma.setEvaluator(new PolyGamma());
      S.PolyLog.setEvaluator(new PolyLog());
      S.ProductLog.setEvaluator(new ProductLog());
      S.StieltjesGamma.setEvaluator(new StieltjesGamma());
      S.StruveH.setEvaluator(new StruveH());
      S.StruveL.setEvaluator(new StruveL());
    }
  }

  private static class Beta extends AbstractFunctionEvaluator implements IFunctionExpand {

    /** Largest argument magnitude handed to the arbitrary precision routine. */
    private static final double MAX_BETA_ARGUMENT = 1.0e6;

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
    public boolean evalIsReal(IAST ast) {
      int argSize = ast.argSize();
      switch (argSize) {
        case 2:
          return (ast.arg1().isRealResult() && ast.arg2().isRealResult());
        case 3:
          return (ast.arg1().isRealResult() && ast.arg2().isRealResult()
              && ast.arg3().isRealResult());
        case 4:
          return (ast.arg1().isRealResult() && ast.arg2().isRealResult()
              && ast.arg3().isRealResult() && ast.arg3().isRealResult());
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr betaSymbolic = F.NIL;
        int argSize = ast.argSize();
        switch (argSize) {
          case 2:
          // Beta(a,b)
          {
            IExpr a = ast.arg1();
            IExpr b = ast.arg2();
            betaSymbolic = beta(a, b);
          }
            break;
          case 3:
          // incomplete Beta(z, a, b)
          {
            IExpr z = ast.arg1();
            IExpr a = ast.arg2();
            IExpr b = ast.arg3();
            betaSymbolic = incompleteBeta(z, a, b);
          }
            break;
          case 4:
          // generalized incomplete Beta(z1, z2, a, b)
          {
            IExpr z1 = ast.arg1();
            IExpr z2 = ast.arg2();
            IExpr a = ast.arg3();
            IExpr b = ast.arg4();

            betaSymbolic = generalizedIncompleteBeta(z1, z2, a, b);
          }
            break;
          default:
            break;
        }
        if (betaSymbolic.isPresent()) {
          // CPD-OFF closed-form half-integer Struve formulas
          return engine.evaluate(betaSymbolic);
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
    // CPD-ON
      if (F.isExactZero(a) || F.isExactZero(b)) {
        return F.CComplexInfinity;
      }
      // EvalEngine engine = EvalEngine.get();

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
    private IExpr incompleteBeta(IExpr z, IExpr a, IExpr b) {
      EvalEngine engine = EvalEngine.get();
      try {
        if (F.isExactZero(z)) {
          IExpr re = a.re();
          if (re.isPositiveResult()) {
            return F.C0;
          }
          if (re.isNegativeResult()) {
            return F.CComplexInfinity;
          }
        }
        if (z.isOne()) {
          IExpr res = beta(a, b);
          if (res.isPresent()) {
            return F.Beta(a, b);
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
        }
        if (a.isNumber() && b.isNumber() && z.isNumber()) {
          // CPD-OFF closed-form half-integer Struve formulas
          return functionExpand(F.Beta(z, a, b), engine);
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
    private IExpr generalizedIncompleteBeta(IExpr z1, IExpr z2, IExpr a, IExpr b) {
      if (F.isExactZero(z2)) {
      // CPD-ON
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
      if (z1.isNumber() && z2.isNumber() && a.isNumber() && b.isNumber()) {
        return functionExpand(F.Beta(z1, z2, a, b), EvalEngine.get());
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2() && ast.arg1() instanceof Num && ast.arg2() instanceof Num) {
        final double a = ((Num) ast.arg1()).doubleValue();
        final double b = ((Num) ast.arg2()).doubleValue();
        if (a > 0.0 && b > 0.0 && Math.max(a, b) >= 20.0) {
          // machine numbers of any size: the Stirling series, no arbitrary precision routine
          return F.num(DMath.beta(a, b));
        }
      }
      for (int i = 1; i < ast.size(); i++) {
        IExpr argument = ast.get(i);
        if (argument.isReal() && Math.abs(argument.evalf()) > MAX_BETA_ARGUMENT) {
          // The arbitrary precision routine works through the magnitude of its arguments: 10^6
          // takes under three seconds, 10^12 does not return, and the 1.7976931348623157*10^308
          // the fuzzer found never did.
          return F.NIL;
        }
      }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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

        if (a.isOne()) {
          // BetaRegularized(z, 1, b) -> 1 - (1 - z)^b
          return F.Subtract(F.C1, F.Power(F.Subtract(F.C1, z), b));
        }

        if (b.isOne()) {
          // BetaRegularized(z, a, 1) -> z^a
          return F.Power(z, a);
        }

        int bi = b.toIntDefault();
        if (F.isPresent(bi)) {
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
        if (F.isPresent(bi)) {
          if (bi > Config.MAX_POLYNOMIAL_DEGREE) {
            PolynomialDegreeLimitExceeded.throwIt(bi);
          }
          // https://functions.wolfram.com/GammaBetaErf/BetaRegularized/03/01/01/0001/

          IExpr oneMinusZ = F.C1.subtract(z);
          // ((1-z)^k*Pochhammer(a,k))/k!
          IExpr sum = F.sum(k -> F.Times(F.Power(oneMinusZ, k), F.Power(F.Factorial(k), F.CN1),
              F.Pochhammer(a, k)), 0, bi - 1);
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
        if (F.isPresent(bi)) {
          if (bi < 0) {
            // https://functions.wolfram.com/GammaBetaErf/BetaRegularized4/03/01/01/0001/
            return F.C0;
          }
        }
        if (z1.isNumber() && z2.isNumber() && a.isNumber() && b.isNumber()) {
          return functionExpand(ast, engine);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class DirichletEta extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return functionExpand(ast.arg1());
      }
      return F.NIL;
    }

    private static IExpr functionExpand(final IExpr z) {
      // (1-2^(1-z))*Zeta(z)
      return F.Times(F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, z))), F.Zeta(z));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      return dirirchletEta(z, engine.isNumericMode()).eval(engine);
    }

    private static IExpr dirirchletEta(IExpr z, boolean numericMode) {
      if (z.isMinusOne()) {
        return F.C1D4;
      }
      if (z.isZero()) {
        return F.C1D2;
      }
      if (z.isOne()) {
        return F.Log(F.C2);
      }
      if (numericMode || z.isInteger()) {
        return functionExpand(z);
      }
      return NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class DirichletLambda extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr s = ast.arg1();
      if (s.isZero()) {
        return F.C0;
      }
      if (s.isOne()) {
        return F.CComplexInfinity;
      }
      // ((-1+2^s)*Zeta(s))/2^s
      IExpr v1 = F.Power(F.C2, s);
      return F.Times(F.Power(v1, F.CN1), F.Plus(F.CN1, v1), F.Zeta(s));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
    public boolean evalIsReal(IAST ast) {
      int argSize = ast.argSize();
      if (argSize == 1) {
        return ast.arg1().isRealResult();
      }
      return false;
    }

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
        if (F.isExactZero(z0)) {
          return F.Erf(z1);
        }
        if (F.isExactZero(z1)) {
          return F.Negate(F.Erf(z0));
        }
        if (z0.isNegativeInfinity() && z1.isInfinity()) {
          return F.C2;
        }
        return F.NIL;
      }

      IExpr z = ast.arg1();
      if (F.isExactZero(z)) {
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
        if (z1.isReal() && z2.isReal()) {
          // on one side of the origin the difference of the two Erf() cancels: Erfc() doesn't
          if (z1.isPositive() && z2.isPositive()) {
            IExpr erfc1 = z1.erfc();
            IExpr erfc2 = z2.erfc();
            if (erfc1 instanceof INum && erfc2 instanceof INum) {
              return ((INum) erfc1).subtract((INum) erfc2);
            }
          } else if (z1.isNegative() && z2.isNegative()) {
            IExpr erfc1 = z1.negate().erfc();
            IExpr erfc2 = z2.negate().erfc();
            if (erfc1 instanceof INum && erfc2 instanceof INum) {
              return ((INum) erfc2).subtract((INum) erfc1);
            }
          }
        }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Owen's T function <code>OwenT(h, a) = 1/(2*Pi)*Integrate(E^(-h^2*(1+t^2)/2)/(1+t^2), {t,0,a})</code>.
   */
  private static final class OwenT extends AbstractFunctionEvaluator {

    @Override
    public boolean evalIsReal(IAST ast) {
      return ast.argSize() == 2 && ast.arg1().isRealResult() && ast.arg2().isRealResult();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr h = ast.arg1();
      IExpr a = ast.arg2();
      if (a.isZero()) {
        return F.C0;
      }
      if (h.isZero()) {
        // ArcTan(a)/(2*Pi)
        return F.Times(F.C1D2, F.Power(S.Pi, F.CN1), F.ArcTan(a));
      }
      if (a.isOne()) {
        // 1/8*Erfc(-h/Sqrt(2))*Erfc(h/Sqrt(2))
        IExpr z = F.Times(F.C1DSqrt2, h);
        return F.Times(F.QQ(1, 8), F.Erfc(F.Negate(z)), F.Erfc(z));
      }
      if (h.isInfinity() || h.isNegativeInfinity()) {
        return F.C0;
      }
      if (a.isInfinity() || a.isNegativeInfinity()) {
        // 1/4*Erfc(Abs(h)/Sqrt(2))
        IExpr value = F.Times(F.C1D4, F.Erfc(F.Times(F.C1DSqrt2, F.Abs(h))));
        return a.isInfinity() ? value : F.Negate(value);
      }
      IExpr negH = AbstractFunctionEvaluator.getNormalizedNegativeExpression(h);
      if (negH.isPresent()) {
        // OwenT is even in h
        return F.binaryAST2(S.OwenT, negH, a);
      }
      IExpr negA = AbstractFunctionEvaluator.getNormalizedNegativeExpression(a);
      if (negA.isPresent()) {
        // and odd in a
        return F.Negate(F.binaryAST2(S.OwenT, h, negA));
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() != 2 || !(ast.arg1() instanceof INum) || !(ast.arg2() instanceof INum)) {
        return F.NIL;
      }
      INum h = (INum) ast.arg1();
      INum a = (INum) ast.arg2();
      try {
        if (h instanceof ApfloatNum || a instanceof ApfloatNum) {
          long precision = Math.min(h.apfloatValue().precision(), a.apfloatValue().precision());
          if (precision == Apfloat.INFINITE) {
            precision = engine.getNumericPrecision();
          }
          return F.num(ApfloatOwenMath.owenT(h.apfloatValue(), a.apfloatValue(), precision));
        }
        return F.num(ApfloatOwenMath
            .owenT(new Apfloat(h.getRealPart()), new Apfloat(a.getRealPart()), 20).doubleValue());
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        Errors.rethrowsInterruptException(ex);
        if (!(h instanceof ApfloatNum) && !(a instanceof ApfloatNum)) {
          // the value is below E^(-100000), far below the smallest machine number
          return F.CD0;
        }
        return F.NIL;
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  // CPD-OFF closed-form half-integer Struve formulas
  private static final class Erfc extends AbstractFunctionEvaluator {

    @Override
    public boolean evalIsReal(IAST ast) {
      int argSize = ast.argSize();
      if (argSize == 1) {
        return ast.arg1().isRealResult();
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (F.isExactZero(z)) {
        return F.C1;
        // CPD-ON
      }
      // the DirectedInfinity cases must be tested OUTSIDE an isReal() guard: Infinity is the AST
      // DirectedInfinity(1), for which isReal() is false. Left inside, Erfc(Infinity) stays
      // unevaluated and e.g. Erfc(2*Infinity)/Erfc(Infinity) cancels structurally to 1.
      if (z.isInfinity()) {
        return F.C0;
      }
      if (z.isNegativeInfinity()) {
        return F.C2;
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      if (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI)) {
        // Erfc(z) = 1 - Erf(z) and Erf(+-I*Infinity) = +-I*Infinity
        return z.negate();
      }
      if (z.isReal()) {
        return F.NIL;
      }
      if (z.isAST(S.InverseErfc, 2)) {
        return z.first();
      }
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // Erfc(z) == 1 - Erf(z) and Erf(z) == I*Erfi(-I*z)
        // (https://functions.wolfram.com/GammaBetaErf/Erf/16/01/01/0002/), so
        // Erfc(z) == 1 - I*Erfi(-I*z). This used to return Erf's I*Erfi(-I*z) without the 1 - in
        // front, which made Gamma(1/2, z) and Hypergeometric1F1(1, 3/2, z) wrong for negative z.
        return F.Subtract(F.C1, F.Times(S.I, F.Erfi(F.Times(F.CNI, z))));
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Returns the imaginary error function.
   *
   */
  // CPD-OFF closed-form half-integer Struve formulas
  private static final class Erfi extends AbstractFunctionEvaluator {
    @Override
    public boolean evalIsReal(IAST ast) {
      int argSize = ast.argSize();
      if (argSize == 1) {
        return ast.arg1().isRealResult();
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (F.isExactZero(z)) {
        return F.C0;
      }
      // CPD-ON
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class GammaRegularized extends AbstractFunctionEvaluator
      implements IFunctionExpand {

    @Override
    public IExpr functionExpand(IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr a = ast.arg1();
        IExpr z = ast.arg2();
        // Gamma(a,z )/Gamma(a)
        return F.Times(F.Power(F.Gamma(a), F.CN1), F.Gamma(a, z));
      }
      if (ast.isAST3()) {
        IExpr a = ast.arg1();
        IExpr z1 = ast.arg2();
        IExpr z2 = ast.arg3();
        // Gamma(a,z1)/Gamma(a)-Gamma(a,z2)/Gamma(a)
        IExpr v1 = F.Power(F.Gamma(a), F.CN1);
        return F.Plus(F.Times(v1, F.Gamma(a, z1)), F.Times(F.CN1, v1, F.Gamma(a, z2)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr a = ast.arg1();
        IExpr z1 = ast.arg2();
        if (ast.isAST3()) {
          IExpr z2 = ast.arg3();
          if (z1.equals(z2) && !z1.isInexactNumber()) {
            // the integral from z to z
            return F.C0;
          }
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
      } else if (a.isMathematicalIntegerNegative()) {
        // Gamma(a) has a pole at every negative integer, so the quotient is zero there. Tested on
        // the value and not on the type because numeric mode has already turned the integer into
        // -2.147483648*^9 by now, and Gamma(a,z)/Gamma(a) sends apfloat off for minutes to reach
        // this same zero - it arrives quickly only while |a| is small.
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
      } else if (a.isInteger()) {
        // https://functions.wolfram.com/GammaBetaErf/GammaRegularized/03/01/02/0007/
        int n = a.toIntDefault();
        if (n > 0) {
          IExpr sum = F.sum(k -> F.Times(F.Power(z1, k), F.Power(F.Factorial(k), F.CN1)), 0, n - 1);
          return F.Times(F.Power(S.E, F.Negate(z1)), sum);
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
      if (a.isMathematicalIntegerNegative()) {
        // as in the two argument case: a pole of Gamma(a) at either end of the interval
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

  }

  /**
   * <code>CoulombF, CoulombG, ParabolicCylinderD, ScorerGi, ScorerHi</code>: functions which are
   * evaluated for numbers only.
   */
  private static class Numeric extends AbstractFunctionEvaluator {
    private final int arguments;
    private final Function<Apcomplex[], Apcomplex> routine;

    Numeric(int arguments, Function<Apcomplex[], Apcomplex> routine) {
      this.arguments = arguments;
      this.routine = routine;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.last();
      if (z.isZero() && ast.head() != S.CoulombF && ast.head() != S.CoulombG) {
        IExpr value;
        if (ast.head() == S.ParabolicCylinderD) {
          // 2^(v/2)*Sqrt(Pi)/Gamma((1-v)/2)
          IExpr v = ast.arg1();
          value = F.Times(F.Power(F.C2, F.Times(F.C1D2, v)), F.Sqrt(S.Pi),
              F.Power(F.Gamma(F.Times(F.C1D2, F.Subtract(F.C1, v))), F.CN1));
        } else {
          // ScorerGi(0) == 1/(3^(7/6)*Gamma(2/3)) and ScorerHi(0) == 2*ScorerGi(0)
          value = F.Times(ast.head() == S.ScorerHi ? F.C2 : F.C1,
              F.Power(F.Times(F.Power(F.C3, F.QQ(7, 6)), F.Gamma(F.QQ(2, 3))), F.CN1));
        }
        return z.isInexactNumber() ? engine.evalN(value) : value;
      }
      // a positive argument of a Coulomb function, and any real one of the others
      boolean realResult = arguments < 3 || z.isPositive();
      return WorkingPrecision.evaluate(ast, engine, realResult, routine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return new int[] {arguments, arguments};
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class HypergeometricPFQRegularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || !ast.arg2().isList() || !ast.arg3().isNumber()) {
        return F.NIL;
      }
      IAST a = (IAST) ast.arg1();
      IAST b = (IAST) ast.arg2();
      IExpr z = ast.arg3();
      if (!a.forAll(x -> x.isNumber()) || !b.forAll(x -> x.isNumber())) {
        return F.NIL;
      }
      if (!engine.isNumericMode() && !z.isInexactNumber() && !a.exists(x -> x.isInexactNumber())
          && !b.exists(x -> x.isInexactNumber())) {
        return F.NIL;
      }
      // the lower parameter -m with the largest m: the first m+1 terms of the series vanish
      int position = -1;
      int m = -1;
      for (int j = 1; j <= b.argSize(); j++) {
        if (b.get(j).isNumIntValue()) {
          int value = b.get(j).toIntDefault();
          if (value != Integer.MIN_VALUE && value <= 0 && -value > m) {
            m = -value;
            position = j;
          }
        }
      }
      IExpr expr;
      if (position < 0) {
        // HypergeometricPFQ(a,b,z)/(Gamma(b1)*...*Gamma(bq))
        IASTAppendable times = F.TimesAlloc(b.argSize() + 1);
        times.append(F.HypergeometricPFQ(a, b, z));
        b.forEach(x -> times.append(F.Power(F.Gamma(x), F.CN1)));
        expr = times;
      } else {
        // z^(m+1)*Pochhammer(a,m+1)/((m+1)!*Gamma(b+m+1))*HypergeometricPFQ(a+m+1,{m+2,b+m+1},z)
        final IExpr shift = F.ZZ(m + 1);
        final int skip = position;
        IASTAppendable upper = F.ListAlloc(a.argSize());
        IASTAppendable lower = F.ListAlloc(b.argSize());
        IASTAppendable times = F.TimesAlloc(a.argSize() + b.argSize() + 3);
        times.append(F.Power(z, shift));
        times.append(F.Power(F.Factorial(shift), F.CN1));
        a.forEach(x -> {
          times.append(F.Pochhammer(x, shift));
          upper.append(F.Plus(x, shift));
        });
        lower.append(F.ZZ(m + 2));
        for (int j = 1; j <= b.argSize(); j++) {
          if (j != skip) {
            IExpr shifted = F.Plus(b.get(j), shift);
            times.append(F.Power(F.Gamma(shifted), F.CN1));
            lower.append(shifted);
          }
        }
        times.append(F.HypergeometricPFQ(upper, lower, z));
        expr = times;
      }
      IExpr value = engine.evaluate(expr);
      return value.isNumber() ? value : F.NIL;
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
      newSymbol.setAttributes(Attribute.NUMERICFUNCTION);
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
      return numericFunction(ast, engine);
    }

    /**
     * The sum of <code>z^k/(k+a)^s</code> with the principal value of the powers, which is complex
     * for a negative <code>k+a</code> where {@link LerchPhi} is real.
     */
    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 3) {
        return WorkingPrecision.evaluate(ast, engine, false, arguments -> ApcomplexZetaMath
            .lerchPhi(arguments[0], arguments[1], arguments[2]));
      }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
    public boolean evalIsReal(IAST ast) {
      return false;
    }

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
      // if (arg1.isList()) {
      // return ((IAST) arg1).mapThread(x -> F.InverseErfc(x));
      // }
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
        if (z.isReal()) {
          INumber number = z;
          if (number.less(F.C2).isTrue() && number.greater(F.C1).isTrue()) {
            // InverseErfc(x) = -InverseErfc(2 - x)
            return F.Negate(F.InverseErfc(F.Subtract(F.C2, z)));
          }
        }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  /**
   * Test for the value <code>0</code> without the {@link Config#DOUBLE_TOLERANCE} slack that
   * {@link IExpr#isZero()} applies to machine numbers. A probability as small as
   * <code>10^-16</code> is a perfectly good argument of the inverse regularized functions and must
   * not collapse the result to <code>0</code>.
   *
   * @param z the expression to test
   * @return <code>true</code> if <code>z</code> is exactly zero
   */
  private static boolean isExactlyZero(IExpr z) {
    if (z.isInexactNumber()) {
      return z.isReal() && ((IReal) z).apfloatValue().signum() == 0;
    }
    return z.isZero();
  }

  /**
   * Test for the value <code>1</code> without the {@link Config#DOUBLE_TOLERANCE} slack that
   * {@link IExpr#isOne()} applies to machine numbers.
   *
   * @param z the expression to test
   * @return <code>true</code> if <code>z</code> is exactly one
   */
  private static boolean isExactlyOne(IExpr z) {
    if (z.isInexactNumber()) {
      return z.isReal() && ((IReal) z).apfloatValue().compareTo(Apfloat.ONE) == 0;
    }
    return z.isOne();
  }


  private static class InverseBetaRegularized extends AbstractFunctionEvaluator
      implements INumeric {

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      try {
        if (size == 3) {
          return InverseGammaBetaJS.invRegularizedBeta(stack[top], stack[top + 1], stack[top + 2]);
        }
        if (size == 4) {
          return InverseGammaBetaJS.invRegularizedBeta(stack[top], stack[top + 1], stack[top + 2],
              stack[top + 3]);
        }
      } catch (ArgumentTypeException ate) {
        // arguments out of range
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST3()) {
        IExpr z = ast.arg1();
        IExpr a = ast.arg2();
        IExpr b = ast.arg3();
        if (a.isPositiveResult()) {
          if (isExactlyZero(z)) {
            return engine.isNumericMode() ? F.CD0 : F.C0;
          }
          if (isExactlyOne(z)) {
            return engine.isNumericMode() ? F.CD1 : F.C1;
          }
        }
        try {
          return numericInverseBeta(engine, z, a, b);
        } catch (MathIllegalStateException mise) {
          return Errors.printMessage(S.InverseBetaRegularized, mise);
        }
      }

      IExpr z1 = ast.arg1();
      IExpr z2 = ast.arg2();
      IExpr a = ast.arg3();
      IExpr b = ast.arg4();
      if (isExactlyZero(z2)) {
        return z1;
      }
      if (isExactlyZero(z1)) {
        return F.InverseBetaRegularized(z2, a, b);
      }
      try {
        return numericInverseBeta(engine, z1, z2, a, b);
      } catch (MathIllegalStateException mise) {
        return Errors.printMessage(S.InverseBetaRegularized, mise);
      }
    }

    /** <code>InverseBetaRegularized(z, a, b)</code> for real numbers. */
    private static IExpr numericInverseBeta(EvalEngine engine, IExpr z, IExpr a, IExpr b) {
      if (!engine.isNumericMode() || !z.isReal() || !a.isReal() || !b.isReal()) {
        return F.NIL;
      }
      try {
        if (engine.isArbitraryMode()) {
          long precision = engine.getNumericPrecision();
          return F.num(InverseGammaBetaJS.invRegularizedBeta(((IReal) z).apfloatValue(),
              ((IReal) a).apfloatValue(), ((IReal) b).apfloatValue(), precision));
        }
        return F.num(InverseGammaBetaJS.invRegularizedBeta(((IReal) z).doubleValue(),
            ((IReal) a).doubleValue(), ((IReal) b).doubleValue()));
      } catch (ArgumentTypeException | ArithmeticException | ApfloatRuntimeException rex) {
        // arguments outside of the domain - WMA leaves the expression unevaluated
      }
      return F.NIL;
    }

    /** <code>InverseBetaRegularized(z1, z2, a, b)</code> for real numbers. */
    private static IExpr numericInverseBeta(EvalEngine engine, IExpr z1, IExpr z2, IExpr a,
        IExpr b) {
      if (!engine.isNumericMode() || !z1.isReal() || !z2.isReal() || !a.isReal() || !b.isReal()) {
        return F.NIL;
      }
      try {
        if (engine.isArbitraryMode()) {
          long precision = engine.getNumericPrecision();
          return F.num(InverseGammaBetaJS.invRegularizedBeta(((IReal) z1).apfloatValue(),
              ((IReal) z2).apfloatValue(), ((IReal) a).apfloatValue(), ((IReal) b).apfloatValue(),
              precision));
        }
        return F.num(InverseGammaBetaJS.invRegularizedBeta(((IReal) z1).doubleValue(),
            ((IReal) z2).doubleValue(), ((IReal) a).doubleValue(), ((IReal) b).doubleValue()));
      } catch (ArgumentTypeException | ArithmeticException | ApfloatRuntimeException rex) {
        // arguments outside of the domain - WMA leaves the expression unevaluated
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class InverseGammaRegularized extends AbstractFunctionEvaluator
      implements INumeric {

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      try {
        if (size == 2) {
          return InverseGammaBetaJS.invRegularizedGammaQ(stack[top], stack[top + 1]);
        }
        if (size == 3) {
          return InverseGammaBetaJS.invRegularizedGamma(stack[top], stack[top + 1], stack[top + 2]);
        }
      } catch (ArgumentTypeException ate) {
        // arguments out of range
      }
      throw new UnsupportedOperationException();
    }

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
        return numericInverseGamma(engine, a, z1, z2);
      }

      IExpr z = ast.arg2();
      if (a.isPositiveResult()) {
        if (isExactlyZero(z)) {
          return F.CInfinity;
        } else if (isExactlyOne(z)) {
          return F.C0;
        }
      }
      if (a.isOne() && z.isRealResult()) {
        if (a.isOne() && z.isReal()) {
          // fast path
          IReal q = (IReal) z;
          if (q.isGT(F.C0) && q.isLT(F.C1)) {
            return F.Negate(F.Log(z));
          }
        }
        // Clamp the real domain: if q >= 1, it returns 0.
        if (engine.evalTrue(F.GreaterEqual(z, F.C1))) {
          return F.C0;
        }

      }
      return numericInverseGamma(engine, a, z);
    }

    /** <code>InverseGammaRegularized(a, z)</code> - the upper tail - for real numbers. */
    private static IExpr numericInverseGamma(EvalEngine engine, IExpr a, IExpr z) {
      if (!engine.isNumericMode() || !a.isReal() || !z.isReal()) {
        return F.NIL;
      }
      try {
        if (engine.isArbitraryMode()) {
          long precision = engine.getNumericPrecision();
          return F.num(InverseGammaBetaJS.invRegularizedGammaQ(((IReal) a).apfloatValue(),
              ((IReal) z).apfloatValue(), precision));
        }
        return F.num(InverseGammaBetaJS.invRegularizedGammaQ(((IReal) a).doubleValue(),
            ((IReal) z).doubleValue()));
      } catch (ArgumentTypeException | ArithmeticException | ApfloatRuntimeException rex) {
        // arguments outside of the domain - WMA leaves the expression unevaluated
      }
      return F.NIL;
    }

    /** <code>InverseGammaRegularized(a, z1, z2)</code> for real numbers. */
    private static IExpr numericInverseGamma(EvalEngine engine, IExpr a, IExpr z1, IExpr z2) {
      if (!engine.isNumericMode() || !a.isReal() || !z1.isReal() || !z2.isReal()) {
        return F.NIL;
      }
      try {
        if (engine.isArbitraryMode()) {
          long precision = engine.getNumericPrecision();
          return F.num(InverseGammaBetaJS.invRegularizedGamma(((IReal) a).apfloatValue(),
              ((IReal) z1).apfloatValue(), ((IReal) z2).apfloatValue(), precision));
        }
        return F.num(InverseGammaBetaJS.invRegularizedGamma(((IReal) a).doubleValue(),
            ((IReal) z1).doubleValue(), ((IReal) z2).doubleValue()));
      } catch (ArgumentTypeException | ArithmeticException | ApfloatRuntimeException rex) {
        // arguments outside of the domain - WMA leaves the expression unevaluated
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class LerchPhi extends AbstractFunctionEvaluator {

    /** Highest {@code n} in {@code LerchPhi(z,-n,a)} expanded for a numeric {@code a}. */
    private static final int MAX_NUMERIC_LERCH_ORDER = 256;

    /** Highest {@code n} expanded when {@code a} is symbolic, where the growth is far steeper. */
    private static final int MAX_SYMBOLIC_LERCH_ORDER = 10;

    /** Largest integer {@code a} for which the sum of {@code Abs(a)} terms is written down. */
    private static final int MAX_INTEGER_LERCH_SHIFT = 1000;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr s = ast.arg2();
      IExpr a = ast.arg3();
      return evaluateSymbolic(z, s, a, engine);
    }

    private static IExpr evaluateSymbolic(IExpr z, IExpr s, IExpr a, EvalEngine engine) {
      if (s.isZero()) {
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/02/01/0007/
        return F.Power(F.Subtract(F.C1, z), F.CN1);
      }

      if (z.isZero()) {
        if (s.isOne() && a.isZero()) {
          // LerchPhi(0, 1, 0)
          return F.C0;
        }
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/03/01/0002/
        // (a^2)^(-s/2)
        return F.Power(F.Power(a, 2), s.isOne() ? F.CN1D2 : F.Times(F.CN1D2, s));
      }
      if (z.isMinusOne()) {
        if (s.isOne() && a.isZero()) {
          // LerchPhi(-1, 1, 0)
          // -Log(2)
          return F.Negate(F.Log(F.C2));
        }
        if (s.isNumEqualInteger(F.C2) && a.isRationalValue(F.C1D2)) {
          // LerchPhi(-1, 2, 1/2)
          // 4*Catalan
          return F.Times(F.C4, F.Catalan);
        }
        if (a.isOne()) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/05/01/
          // (1-2^(1-s))*Zeta(s)
          return F.Times(F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, s))), F.Zeta(s));
        }
        // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/03/01/0001/
        // Zeta(s, a/2)/2^s - Zeta(s, 1/2*(1+a))/2^s
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
          return F.Times(F.CNI, F.Pi);
        }
      } else if (z.equals(F.CC(1, 2, -1, 2))) {
        if (s.isNumEqualInteger(F.C2) && a.isOne()) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/02/01/0003/
          // LerchPhi(1/2-1/2*I, 2, 1)
          // (1+I)*PolyLog(2, 1/2-I/2)
          return F.Times(F.Plus(F.C1, F.CI), F.PolyLog(F.C2, F.CC(1, 2, -1, 2)));
        }
      }

      if (s.isInteger() && ((IInteger) s).isNegative()) {
        try {
          int n = -((IInteger) s).toInt();
          // The loop below runs n times and calls Together() on the result of each step, so what
          // it costs is decided by how fast that rational function grows - which depends on a.
          if (a.isNumber()) {
            if (n > MAX_NUMERIC_LERCH_ORDER) {
              // the coefficients stay numbers, so this is merely slow rather than explosive:
              // n = 101 takes about three seconds, n = 1009 does not finish
              return F.NIL;
            }
          } else if (!a.isSymbol() && !a.isAST()) {
            // A string, or any other atom that is not algebraic. Together() still tries polynomial
            // arithmetic on it, gets nowhere - "+x-*x is not a polynomial" - and does it again on
            // a larger expression every step. LerchPhi(-13,-2,"") already never returned.
            return F.NIL;
          } else if (n > MAX_SYMBOLIC_LERCH_ORDER) {
            // a is symbolic, so every step raises the degree in a as well as in x and the
            // expression grows on both axes: n = 10 takes under a second, n = 15 does not finish
            return F.NIL;
          }
          // LerchPhi(z, -n, a) = (z d/dz + a)^n (1/(1-z))
          ISymbol x = F.Dummy("x");
          // Base case n=0: 1 / (1 - x)
          IExpr expr = F.Power(F.Subtract(F.C1, x), F.CN1);
          for (int i = 0; i < n; i++) {
            // Apply operator recursively: x * D(expr, x) + a * expr
            IExpr derivative = engine.evaluate(F.D(expr, x));
            IExpr step = F.Plus(F.Times(x, derivative), F.Times(a, expr));

            // Wrap in Together to maintain a single rational fraction at each step
            expr = engine.evaluate(F.Together(step));
          }
          // Substitute the dummy variable with the original evaluation target
          return engine.evaluate(F.subst(expr, F.Rule(x, z)));
        } catch (ArithmeticException e) {
          // Fall back if the integer is too large to handle iteratively
        }
      }

      int n = a.toIntDefault();
      if (F.isPresent(n) && Math.abs(n) <= MAX_INTEGER_LERCH_SHIFT) {
        IExpr polyLog = engine.evaluate(F.PolyLog(s, z));
        if (n <= 0) {
          n = -n;
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/01/01/0002/
          // z^n*(PolyLog(s, z)+Sum(1/(z^k*k^s), {k, 1, n}))
          return F.Times(F.Power(z, n), //
              F.Plus(polyLog, //
                  F.sum(k -> F.Power(F.Times(F.Power(z, k), F.Power(k, s)), F.CN1), 1, n)));
        } else {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/LerchPhi/03/01/01/01/0009/
          // (PolyLog(s, z)-Sum(z^k/k^s, {k, 1, -1+n}))/z^n
          return F.Times(F.Power(z, -n), //
              F.Subtract(polyLog, //
                  F.sum(k -> F.Times(F.Power(F.Power(k, s), F.CN1), F.Power(z, k)), 1, n - 1)));
        }
      }

      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 3 && ast.arg1().isMachineNumber() && ast.arg2().isMachineNumber()
          && ast.arg3().isMachineNumber()) {
        // the series in machine arithmetic for |z| < 1 and Re(a) > 0
        Complex machine = HurwitzZetaKernel.lerchPhi(ast.arg1().evalfc(), ast.arg2().evalfc(),
            ast.arg3().evalfc());
        if (machine == null && ast.arg2().isReal() && ast.arg3().isReal()) {
          // outside the unit disk: the integral representation
          machine = HurwitzZetaKernel.lerchPhiOutside(ast.arg1().evalfc(), ast.arg2().evalf(),
              ast.arg3().evalf());
        }
        if (machine != null) {
          if (ast.arg1().isReal() && ast.arg2().isReal() && ast.arg3().isReal()) {
            return F.num(machine.getReal());
          }
          return F.complexNum(machine);
        }
      }
      if (ast.argSize() == 3 && ast.arg1().isNumber() && ast.arg2().isNumber()
          && ast.arg3().isNumber()) {
        // for complex arguments and any precision
        boolean arbitrary = false;
        boolean real = true;
        for (int i = 1; i <= 3; i++) {
          arbitrary |= ast.get(i) instanceof ApfloatNum || ast.get(i) instanceof ApcomplexNum;
          real &= ast.get(i).isReal();
        }
        if (real && !((IReal) ast.arg1()).isLT(F.C1)) {
          // on the branch cut from 1 to infinity
          real = false;
        }
        final int digits = CarlsonArbitraryPrecision.digits(arbitrary, engine);
        try {
          // the sum of z^k/((k+a)^2)^(s/2): the terms with a negative k+a have the base -(k+a)
          Apcomplex value = ApcomplexZetaMath.lerchPhiOfSquares(
              CarlsonArbitraryPrecision.apcomplex(ast.arg1(), digits),
              CarlsonArbitraryPrecision.apcomplex(ast.arg2(), digits),
              CarlsonArbitraryPrecision.apcomplex(ast.arg3(), digits));
          return CarlsonArbitraryPrecision.toNumber(value, real || value.imag().signum() == 0,
              arbitrary);
        } catch (ArithmeticException | ApfloatRuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
        }
        if (arbitrary) {
          return F.NIL;
        }
      }
      if (ast.argSize() == 3) {
        double z = ast.arg1().evalfNaN();
        double s = ast.arg2().evalfNaN();
        double a = ast.arg3().evalfNaN();
        // Fallback if inputs cannot be evaluated to machine-precision doubles
        if (!Double.isNaN(z) && !Double.isNaN(s) && a > 0.0) {
          // The series expansion only converges for |z| < 1 or (z == 1 and s > 1)
          if (Math.abs(z) < 1.0 || (Math.abs(z - 1.0) < 1e-14 && s > 1.0)) {
            return F.num(lerchPhiNumeric(z, s, a));
          }
        }
      }
      return F.NIL;
    }

    /**
     * Compute LerchPhi numerically via series: Sum(z^k / (k+a)^s)
     */
    private double lerchPhiNumeric(double z, double s, double a) {
      // For z = 1, this is the Hurwitz zeta: Sum(1/(k+a)^s)
      // Use Euler-Maclaurin to add tail correction for better convergence
      int nTerms = Math.abs(z - 1.0) < 1e-14 ? 200 : 1000;
      double sum = 0.0;
      double zPow = 1.0; // z^k

      for (int k = 0; k < nTerms; k++) {
        double denom = Math.pow(k + a, s);
        if (Math.abs(denom) > 1e-300) {
          double term = zPow / denom;
          sum += term;
          if (Math.abs(term) < 1e-15 * Math.abs(sum) && k > 5) {
            return sum;
          }
        }
        zPow *= z;
        if (Math.abs(zPow) < 1e-300) {
          return sum;
        }
      }

      // For z=1 (Hurwitz zeta), add Euler-Maclaurin tail to extend the partial sum:
      // Sum(f(k), {k, N, Infinity}) ~ Integrate(f(t), {t, N, Infinity}) + f(N)/2 - f'(N)/12 +
      // f'''(N)/720 - ...
      // with f(t) = 1/(t+a)^s.
      if (Math.abs(z - 1.0) < 1e-14 && s > 1.0) {
        double n = nTerms;
        double na = n + a;
        double tail = Math.pow(na, 1.0 - s) / (s - 1.0);
        double f_n = 1.0 / Math.pow(na, s);
        // -f'(N)/12 = s / (12 (N+a)^(s+1))
        double em2 = s / (12.0 * Math.pow(na, s + 1.0));
        // f'''(N)/720 = -s(s+1)(s+2) / (720 (N+a)^(s+3))
        double em3 = -s * (s + 1.0) * (s + 2.0) / (720.0 * Math.pow(na, s + 3.0));
        sum += tail + f_n / 2.0 + em2 + em3;
      }

      return sum;
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class LogGamma extends AbstractTrigArg1 implements INumeric {

    /** The largest exact argument whose gamma function is written out: 10000! has 35660 digits. */
    private static final int MAX_EXACT_LOG_GAMMA = 10000;

    @Override
    public boolean evalIsReal(IAST ast) {
      return (ast.argSize() == 1 && ast.arg1().isNonNegativeResult());
    }

    @Override
    public IExpr e1ComplexArg(final Complex c) {
      return F.complexNum(GammaJS.logGamma(c));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      try {
        if (arg1 == 0.0) {
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
        if (stack[top] == 0.0) {
          return Double.POSITIVE_INFINITY;
        }
        return GammaJS.logGamma(stack[top]);
      } catch (final MathIllegalStateException e) {
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInfinity() || F.isExactZero(arg1)) {
        return F.CInfinity;
      }
      if (arg1.isNegativeInfinity() || arg1.isDirectedInfinity(F.CI)
          || arg1.isDirectedInfinity(F.CNI) || arg1.isComplexInfinity()) {
        return F.CComplexInfinity;
      }
      if (arg1.isPositive() && ((IReal) arg1).isLT(F.ZZ(MAX_EXACT_LOG_GAMMA))) {
        // the logarithm of an integer or of a closed form, as long as that can be written down
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }



  private static class PolyGamma extends AbstractFunctionEvaluator implements IFunctionExpand {

    /**
     * <code>PolyGamma(n, x)</code> for <code>n &gt;= 1</code> at a fraction: a half-integer is
     * brought to <code>1/2</code> by the recurrence
     * <code>PolyGamma(n, x+1) == PolyGamma(n, x) + (-1)^n*n!/x^(n+1)</code>, and another negative
     * fraction is reflected to <code>1-x</code>:
     * <code>PolyGamma(n, x) == (-1)^n*PolyGamma(n, 1-x) - Pi*D(Cot(Pi*x), {x, n})</code>.
     *
     * @return {@link F#NIL} for a positive fraction which is no half-integer, and for 1/2
     */
    private static IExpr polyGammaAtFraction(int n, IFraction x, EvalEngine engine) {
      IExpr sign = (n & 1) == 0 ? F.C1 : F.CN1;
      if (x.denominator().equals(F.C2)) {
        // x == 1/2 + m
        int m = x.subtract(F.C1D2).toIntDefault();
        if (m == 0 || !F.isPresent(m) || Math.abs(m) > 1000) {
          return F.NIL;
        }
        IASTAppendable sum = F.PlusAlloc(Math.abs(m));
        for (int k = 0; k < Math.abs(m); k++) {
          // the steps between 1/2 and x
          IRational step = m > 0 ? F.C1D2.add(F.ZZ(k)) : F.C1D2.subtract(F.ZZ(k + 1));
          sum.append(F.Power(step, F.ZZ(-(n + 1))));
        }
        IExpr steps = F.Times(sign, F.Factorial(F.ZZ(n)), sum);
        return engine.evaluate(F.Plus(F.PolyGamma(F.ZZ(n), F.C1D2),
            m > 0 ? steps : F.Times(F.CN1, steps)));
      }
      if (!x.isNegative()) {
        return F.NIL;
      }
      ISymbol t = F.Dummy("t");
      IExpr derivative = engine.evaluate(F.D(F.Cot(F.Times(S.Pi, t)), F.list(t, F.ZZ(n))));
      if (!derivative.isFree(S.D, true) || !derivative.isFree(S.Derivative, true)) {
        return F.NIL;
      }
      return engine.evaluate(F.Plus(F.Times(sign, F.PolyGamma(F.ZZ(n), F.C1.subtract(x))),
          F.Times(F.CN1, S.Pi, F.subst(derivative, F.Rule(t, x)))));
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

    /**
     * True if <code>z</code> is syntactically the negation of another expression, so that negating
     * it is a normalization and not merely a re-ordering.
     * <p>
     * {@link AbstractFunctionEvaluator#getNormalizedNegativeExpression(IExpr)} answers the weaker
     * question whether <code>z</code> is <em>canonically</em> negative, and for a {@link S#Plus} it
     * decides that on the leading summand's sign alone. That is too weak to gate the reflection
     * formula <code>psi(z) = psi(-z) + 1/(-z) + Pi*Cot(Pi*(-z))</code>: it rewrites
     * <code>x - 1</code> onto <code>1 - x</code> while leaving <code>1 - x</code> alone - the same
     * shape, opposite behavior, decided by term ordering - and it reflects
     * <code>Log(x) - 1/(2*x)</code>, stored as <code>-1/(2*x) + Log(x)</code>, onto an argument
     * diverging to <code>-Infinity</code> plus a <code>Cot(Pi*divergent)</code> that is strictly
     * worse than the input. A <code>Plus</code> therefore only counts when EVERY summand is
     * negative (<code>-1-x</code>), not just the first.
     */
    private static boolean isWhollyNegated(IExpr z) {
      if (z.isPlus()) {
        return ((IAST) z).forAll(
            term -> AbstractFunctionEvaluator.getNormalizedNegativeExpression(term).isPresent());
      }
      return true;
    }

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr arg1 = ast.arg1();
        IExpr z = ast.arg2();
        if (arg1.isMathematicalIntegerNonNegative()) {
          int n = arg1.toIntDefault();
          if (F.isPresent(n)) {
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
            // F.Times(F.Power(-11, n), F.Factorial(n), tail));
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

              if (isWhollyNegated(z)) {
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

      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isMathematicalIntegerNegative()) {
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
        if (arg2.isMathematicalIntegerNegative()) {
          IExpr nu = arg1.re();
          if (nu.isReal() && ((IReal) nu).isGT(F.CN1)) {
            // https://functions.wolfram.com/GammaBetaErf/PolyGamma2/03/01/01/0018/
            return F.CComplexInfinity;
          }
          if (nu.isReal() && nu.isMinusOne()) {
            // https://functions.wolfram.com/GammaBetaErf/PolyGamma2/03/01/01/0019/
            return S.Indeterminate;
          }
        }
        if (F.isExactZero(arg2) && arg1.isNumber()) {
          IReal realPart = ((INumber) arg1).re();
          if (realPart.isLT(F.CN1)) {
            // https://github.com/mtommila/apfloat/issues/54
            // https://functions.wolfram.com/GammaBetaErf/PolyGamma2/03/01/01/0002/
            return F.C0;
          }
        }
        if (arg2.isInfinity()) {
          if (arg1.isZero()) {
            // https://functions.wolfram.com/GammaBetaErf/PolyGamma2/03/02/0001/
            return F.CInfinity;
          }
          if (arg1.isMathematicalIntegerNonNegative()) {
            // PolyGamma(n, x) ~ (-1)^(n-1)*(n-1)!/x^n -> 0 for n >= 1
            return F.C0;
          }
        }
        if (engine.isNumericMode()) {
          if (arg1.isZero()) {
            return arg2.digamma();
          }
          long n = arg1.toLongDefault();
          if (F.isPresent(n) && arg2.isNumber()) {
            return arg2.polyGamma(n);
          }
        }
        int order = arg1.toIntDefault();
        if (order >= 1 && order <= 20 && arg2.isFraction()) {
          IExpr closedForm = polyGammaAtFraction(order, (IFraction) arg2, engine);
          if (closedForm.isPresent()) {
            return closedForm;
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static class PolyLog extends AbstractFunctionEvaluator implements IFunctionExpand {

    /**
     * <code>PolyLog(n, z)</code> for an integer <code>n &gt;= 2</code> and a root of unity
     * <code>z = E^(2*Pi*I*x)</code> with a rational <code>0 &lt; x &lt; 1</code>.
     *
     * <p>
     * <code>PolyLog(n, z) + (-1)^n*PolyLog(n, Conjugate(z)) == -(2*Pi*I)^n*BernoulliB(n, x)/n!</code>
     * gives the real part for an even <code>n</code> and the imaginary part for an odd one. The
     * other part is a multiple of <code>Zeta(n)</code> for an odd <code>n</code> and
     * <code>x</code> with the denominator 2, 3, 4 or 6, <code>Catalan</code> for
     * <code>PolyLog(2, I)</code>, and otherwise the sum
     * <code>q^-n*Sum(Cos(2*Pi*k*x)*Zeta(n, k/q), {k, 1, q})</code> (or with <code>Sin</code>) of
     * Hurwitz zeta values, which is written with <code>PolyGamma</code>.
     */
    private static IExpr polyLogAtRootOfUnity(IExpr n, IExpr z, EvalEngine engine) {
      int order = n.toIntDefault();
      if (order < 2 || order > 64 || !z.isNumericFunction() || z.isReal()) {
        return F.NIL;
      }
      if (!engine.evalTrue(F.Equal(F.Abs(z), F.C1))) {
        return F.NIL;
      }
      IExpr r = engine.evaluate(F.Divide(F.Arg(z), F.Times(F.C2, S.Pi)));
      if (!r.isRational()) {
        return F.NIL;
      }
      IRational x = (IRational) r;
      if (x.isNegative()) {
        x = x.add(F.C1);
      }
      if (!x.isPositive() || !x.isLT(F.C1)) {
        return F.NIL;
      }
      final int q = x.denominator().toIntDefault();
      // the other part is a sum of q terms
      if (q < 2 || q > 1024) {
        return F.NIL;
      }
      // -(2*Pi*I)^n*BernoulliB(n, x)/(2*n!)
      IExpr bernoulli = F.Times(F.CN1D2, F.Power(F.Times(F.C2, S.Pi, F.CI), n),
          F.BernoulliB(n, x), F.Power(F.Factorial(n), F.CN1));
      IExpr other;
      boolean odd = (order & 1) == 1;
      IExpr zeta = F.Zeta(n);
      // z == -1 is real and has its own rule, so q == 2 only remains for an even n
      if (odd && q == 3) {
        other = F.Times(F.C1D2, F.Plus(F.Power(F.C3, F.Subtract(F.C1, n)), F.CN1), zeta);
      } else if (odd && q == 4) {
        other = F.Times(F.Power(F.C2, F.Negate(n)), F.Plus(F.Power(F.C2, F.Subtract(F.C1, n)), F.CN1),
            zeta);
      } else if (odd && q == 6) {
        other = F.Times(F.C1D2, F.Plus(F.Power(F.C2, F.Subtract(F.C1, n)), F.CN1),
            F.Plus(F.Power(F.C3, F.Subtract(F.C1, n)), F.CN1), zeta);
      } else if (!odd && q == 2) {
        other = F.C0;
      } else if (order == 2 && q == 4) {
        // Catalan's constant is the imaginary part of PolyLog(2, I)
        other = F.Times(x.isLT(F.C1D2) ? F.CI : F.CNI, S.Catalan);
      } else {
        // q^-n*Sum(Cos(2*Pi*k*x)*Zeta(n, k/q), {k, 1, q}) or I*q^-n*Sum(Sin(...)*Zeta(n, k/q))
        IASTAppendable sum = F.PlusAlloc(q);
        for (int k = 1; k <= q; k++) {
          IExpr angle = F.Times(F.C2, S.Pi, F.ZZ(k), x);
          IExpr weight = odd ? F.Cos(angle) : F.Sin(angle);
          IExpr hurwitz = k == q ? zeta
              : F.Times(F.Power(F.CN1, n), F.PolyGamma(F.Subtract(n, F.C1), F.QQ(k, q)),
                  F.Power(F.Factorial(F.Subtract(n, F.C1)), F.CN1));
          sum.append(F.Times(weight, hurwitz));
        }
        other = F.Times(odd ? F.C1 : F.CI, F.Power(F.ZZ(q), F.Negate(n)), sum);
      }
      return engine.evaluate(F.Plus(bernoulli, other));
    }

    /**
     * Beyond this the Eulerian numbers themselves are the cost: the table is <code>n</code>
     * integers whose sum is <code>n!</code>, built in <code>n^2</code> steps. Measured against
     * <code>PolyLog(-n, -3/2)</code>: 2 ms at 128, 213 ms at 1000, 1.9 s at 2000. The line is drawn
     * at the largest order Mathematica was seen to answer; past it <code>PolyLog(-n, z)</code> is
     * left unevaluated rather than begun.
     */
    private static final int MAX_NEGATIVE_POLYLOG_ORDER = 1000;

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        return polyLogAtRootOfUnity(ast.arg1(), ast.arg2(), engine);
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
          return temp.eval(engine);
        }
        return nielsenNumeric(n, x, z);
      }
      IExpr temp = polyLogSymbolic(n, x);
      if (temp.isPresent()) {
        return temp.eval(engine);
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
          return temp.eval(engine);
        }
        // issue #929
        return v.polyLog(z);
      }
      return F.NIL;
    }

    private IExpr polyLogSymbolic(IExpr n, IExpr z) {
      if (F.isExactZero(z)) {
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
        } else if (z.isDirectedInfinity() && z.isAST1() && n.isMathematicalIntegerNonNegative()
            && !n.isZero()) {
          // PolyLog(n, z) ~ -Log(-z)^n/n! for |z| -> Infinity: the power of Log(|z|) outgrows
          // the part which depends on the direction
          return F.CNInfinity;
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
        if (n.isInteger() && n.isNegative()) {
          int order = n.negate().toIntDefault();
          if (order > 1 && order <= MAX_NEGATIVE_POLYLOG_ORDER) {
            return polyLogNegativeIntegerOrder(order, z);
          }
        }
      }
      return F.NIL;
    }

    /**
     * <code>PolyLog(-n, z)</code> for an integer <code>n > 1</code>: the Eulerian numbers
     * <code>A(n,k)</code> as the coefficients of a polynomial over <code>(1-z)^(n+1)</code>.
     *
     * <p>
     * The same closed form the <code>PolyLogRules.m</code> rule writes, and the reason it is here:
     * the rule reaches each Eulerian number through the explicit double sum, which is a power of a
     * big integer per term built as an expression, and then leaves <code>Together</code> a
     * polynomial of degree <code>n</code> over a rational to cancel.
     * <code>PolyLog(-128, -3/2)</code> did not finish. The recurrence below is the same arithmetic
     * without the expressions.
     */
    private static IExpr polyLogNegativeIntegerOrder(int order, IExpr z) {
      BigInteger[] eulerian = new BigInteger[order];
      java.util.Arrays.fill(eulerian, BigInteger.ZERO);
      eulerian[0] = BigInteger.ONE;
      for (int row = 2; row <= order; row++) {
        // A(n,k) = (k+1)*A(n-1,k) + (n-k)*A(n-1,k-1), downwards so that A(n-1,k-1) is still there
        for (int k = row - 1; k > 0; k--) {
          eulerian[k] = BigInteger.valueOf(k + 1L).multiply(eulerian[k])
              .add(BigInteger.valueOf(row - (long) k).multiply(eulerian[k - 1]));
        }
      }
      IASTAppendable numerator = F.PlusAlloc(order);
      for (int k = 0; k < order; k++) {
        numerator.append(F.Times(F.ZZ(eulerian[k]), F.Power(z, F.ZZ(order - k))));
      }
      return F.Times(numerator, F.Power(F.Subtract(F.C1, z), F.ZZ(-(order + 1L))));
    }

    /**
     * <code>PolyLog(n, p, z)</code> for machine numbers with real <code>n, p &gt; 0</code> and at
     * least one inexact argument, by the integral representation.
     */
    private static IExpr nielsenNumeric(IExpr n, IExpr p, IExpr z) {
      if (!n.isReal() || !p.isReal() || !z.isNumber()
          || !(n.isInexactNumber() || p.isInexactNumber() || z.isInexactNumber())) {
        return F.NIL;
      }
      if (n.isInexactNumber() && !(n instanceof INum) || p.isInexactNumber() && !(p instanceof INum)
          || z.isInexactNumber() && !(z instanceof INum || z instanceof IComplexNum)) {
        // arbitrary precision is not supported here
        return F.NIL;
      }
      try {
        Complex zc = z.evalfc();
        if (!(n.isInteger() || n.isNumIntValue()) || !(p.isInteger() || p.isNumIntValue())) {
          // a non-integer order only on the closed unit disk
          if (zc.norm() > 1.0) {
            return F.NIL;
          }
        }
        Complex value =
            org.matheclipse.core.numerics.functions.NielsenPolyLog.value(n.evalf(), p.evalf(), zc);
        if (value == null) {
          return F.NIL;
        }
        // an imaginary part at the level of the rounding errors comes from the complex powers of
        // the integrand, whose phases cancel for a real value
        if (Math.abs(value.getImaginary()) <= 1.0e-14 * Math.abs(value.getReal())) {
          return F.num(value.getReal());
        }
        return F.complexNum(value);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
    }

    private IExpr polyLogSymbolic(IExpr n, IExpr p, IExpr z) {
      if (F.isExactZero(z)) {
        return F.C0;
      }
      if (n.isOne()) {
        int pInt = p.toIntDefault();
        if (z.isOne() && p.isReal() && p.isPositive()) {
          // the sum of 1/k^(p+1)
          return F.Zeta(F.Plus(p, F.C1));
        }
        if (pInt > 1) {
          // https://functions.wolfram.com/ZetaFunctionsandPolylogarithms/PolyLog3/03/01/03/0002/
          IExpr v4 = F.CN1.pow(pInt);
          IExpr sum = F.sum(k -> F.Times(F.Power(F.Factorial(F.Plus(F.CN1, F.Negate(k), p)), F.CN1),
              F.Power(-1, k), F.Power(F.Log(F.Subtract(F.C1, z)), F.Plus(F.CN1, F.Negate(k), p)),
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  /**
   * Lambert W function See: <a href="http://en.wikipedia.org/wiki/Lambert_W_function">Wikipedia -
   * Lambert W function</a>
   */
  private static final class ProductLog extends AbstractArg12 {
    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 1) {
        // ProductLog(x_) is real if x >= 0 or x >= -1/E
        return ast.arg1().isNonNegativeResult() //
            || ast.arg1().greaterEqual(F.Times(F.Power(S.E, F.CN1), F.CN1)).isTrue();
      }
      if (ast.argSize() == 2) {
        IExpr x = ast.arg1();
        IExpr y = ast.arg2();
        if (x.isMinusOne()) {
          // ProductLog(-1, y_) is real if y <= 0 && y >= -1/E
          return (y.isNegativeResult() || y.isZero())//
              && y.greaterEqual(F.Times(F.Power(S.E, F.CN1), F.CN1)).isTrue();
        }
        if (x.isZero()) {
          // ProductLog(x_, 0) is real if x == 0 or y >= -1/E
          return y.isNonNegativeResult() //
              || y.greaterEqual(F.Times(F.Power(S.E, F.CN1), F.CN1)).isTrue();
        }
      }
      return false;
    }


    /**
     * The machine number of a value: a real number on the real branches, where the imaginary part
     * is zero.
     */
    private static IExpr realOrComplexNum(Apcomplex productLog) {
      return CarlsonArbitraryPrecision.toNumber(productLog, productLog.imag().signum() == 0,
          false);
    }

    /**
     * The arbitrary precision number of a value: a real number on the real branches, where the
     * imaginary part is zero.
     */
    private static IExpr realOrComplexApfloat(Apcomplex productLog) {
      if (productLog.imag().signum() == 0) {
        return F.num(productLog.real());
      }
      return F.complexNum(productLog);
    }

    @Override
    public IExpr e1DblArg(final INum d) {
      if (d.isZero()) {
        return d;
      }
      try {
        FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
        Apcomplex ac = d.apcomplexValue();
        return realOrComplexNum(h.w(ac));
      } catch (Exception ce) {
        Errors.rethrowsInterruptException(ce);

      }
      return realOrComplexNum(ApcomplexMath.w(new Apfloat(d.doubleValue())));
    }

    @Override
    public IExpr e1DblComArg(IComplexNum arg1) {
      if (arg1.isZero()) {
        return arg1;
      }
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
      Apcomplex ac = arg1.apcomplexValue();
      return realOrComplexNum(h.w(ac));
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
      return realOrComplexApfloat(h.w(arg1, 0));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        return F.C0;
      }
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return realOrComplexApfloat(h.w(arg1, 0));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
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
        if (F.isNotPresent(ki)) {
          // Machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.ProductLog, "intm", F.list(F.ProductLog(k, z), F.C1));
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
          EvalEngine engine = EvalEngine.get();
          if (engine.isArbitraryMode()) {
            if (z instanceof ApcomplexNum) {
              FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
              Apcomplex ac = ((ApcomplexNum) z).apcomplexValue();
              return realOrComplexApfloat(h.w(ac, ki));
            } else if (z instanceof ApfloatNum) {
              FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
              Apcomplex ac = ((ApfloatNum) z).apcomplexValue();
              return realOrComplexApfloat(h.w(ac, ki));
            }
          }

          if (z instanceof ComplexNum) {
            FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
            Apcomplex ac = ((ComplexNum) z).apcomplexValue();
            return realOrComplexNum(h.w(ac, ki));
          } else if (z instanceof Num) {
            FixedPrecisionApfloatHelper h = EvalEngine.getApfloatDouble();
            Apcomplex ac = ((Num) z).apcomplexValue();
            return realOrComplexNum(h.w(ac, ki));
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
      int index = n.toIntDefault();
      IExpr a = ast.isAST2() ? ast.arg2() : F.C1;
      if (index >= 0 && a.isReal() && a.isPositive()
          && (a.isInexactNumber() || engine.isNumericMode())) {
        // the integral representation, at machine precision or the precision of the engine
        final boolean arbitrary =
            a instanceof ApfloatNum || (engine.isArbitraryMode() && !a.isInexactNumber());
        final int digits = CarlsonArbitraryPrecision.digits(arbitrary, engine);
        try {
          Apfloat value = ApcomplexZetaMath.stieltjesGamma(index,
              CarlsonArbitraryPrecision.apcomplex(a, digits).real());
          return arbitrary ? F.num(value) : F.num(value.doubleValue());
        } catch (ArithmeticException | ApfloatRuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
        }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDFIRST, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  /** Which of the four Mathieu functions an evaluator stands for. */
  private enum MathieuKind {
    C, C_PRIME, S, S_PRIME;

    Apcomplex apply(Apcomplex a, Apcomplex q, Apcomplex z, long precision) {
      switch (this) {
        case C:
          return ApcomplexMathieuMath.mathieuC(a, q, z, precision);
        case C_PRIME:
          return ApcomplexMathieuMath.mathieuCPrime(a, q, z, precision);
        case S:
          return ApcomplexMathieuMath.mathieuS(a, q, z, precision);
        default:
          return ApcomplexMathieuMath.mathieuSPrime(a, q, z, precision);
      }
    }
  }

  /**
   * A numeric routine that answers at the precision of <code>h</code>, or declines with null or an
   * <code>ArithmeticException</code>.
   */
  @FunctionalInterface
  private interface WorkingPrecisionRoutine {
    Apcomplex apply(Apcomplex[] args, FixedPrecisionApcomplexHelper h);
  }

  /**
   * <code>routine</code> at machine precision if all arguments of <code>ast</code> are machine
   * numbers and at the engine's precision otherwise, returned as a real number when its imaginary
   * part is zero.
   */
  private static IExpr atWorkingPrecision(IAST ast, WorkingPrecisionRoutine routine) {
    boolean machinePrecision = true;
    Apcomplex[] args = new Apcomplex[ast.argSize()];
    for (int i = 1; i <= ast.argSize(); i++) {
      if (ast.get(i) instanceof ApfloatNum || ast.get(i) instanceof ApcomplexNum) {
        machinePrecision = false;
      }
    }
    FixedPrecisionApcomplexHelper h =
        machinePrecision ? EvalEngine.getApfloatDouble() : EvalEngine.getApfloat();
    Apcomplex value;
    try {
      for (int i = 1; i <= ast.argSize(); i++) {
        args[i - 1] = ((IInexactNumber) ast.get(i)).apcomplexValue();
      }
      value = routine.apply(args, h);
    } catch (ArithmeticException | ArgumentTypeException | ApfloatRuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      return F.NIL;
    }
    if (value == null) {
      return F.NIL;
    }
    return CarlsonArbitraryPrecision.toNumber(value, value.imag().signum() == 0,
        !machinePrecision);
  }

  /**
   * <code>MathieuCharacteristicA(r,q)</code> and <code>MathieuCharacteristicB(r,q)</code>: the
   * values of <code>a</code> for which <code>y''+(a-2*q*Cos(2*z))*y==0</code> has an even, or an
   * odd, solution <code>Exp(I*r*z)*p(z)</code> with a <code>2*Pi</code> periodic <code>p</code>.
   */
  private static final class MathieuCharacteristicValue extends AbstractFunctionEvaluator {
    private final boolean odd;

    MathieuCharacteristicValue(boolean odd) {
      this.odd = odd;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        return atWorkingPrecision(ast,
            (x, h) -> odd ? ApcomplexMathieuMath.mathieuCharacteristicB(x[0], x[1], h.precision())
                : ApcomplexMathieuMath.mathieuCharacteristicA(x[0], x[1], h.precision()));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr r = ast.arg1();
      IExpr q = ast.arg2();
      if (q.isZero()) {
        return F.Sqr(r);
      }
      // even in r
      IExpr negR = AbstractFunctionEvaluator.getNormalizedNegativeExpression(r);
      if (negR.isPresent()) {
        return F.binaryAST2(ast.head(), negR, q);
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * <code>MathieuCharacteristicExponent(a,q)</code>: the <code>r</code> for which the Mathieu
   * equation has the solution <code>Exp(I*r*z)*p(z)</code> with a <code>2*Pi</code> periodic
   * <code>p</code>.
   */
  private static final class MathieuCharacteristicExponent extends AbstractFunctionEvaluator {

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        return atWorkingPrecision(ast,
            (x, h) -> ApcomplexMathieuMath.mathieuCharacteristicExponent(x[0], x[1],
                h.precision()));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isZero()) {
        return F.Sqrt(ast.arg1());
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * <code>MathieuC(a,q,z)</code>, <code>MathieuS(a,q,z)</code> and their derivatives with respect to
   * <code>z</code>: the even and the odd solution of <code>y''+(a-2*q*Cos(2*z))*y==0</code>.
   */
  private static final class Mathieu extends AbstractFunctionEvaluator {
    private final MathieuKind kind;

    Mathieu(MathieuKind kind) {
      this.kind = kind;
    }

    private boolean isEven() {
      return kind == MathieuKind.C || kind == MathieuKind.S_PRIME;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 3) {
        return atWorkingPrecision(ast,
            (x, h) -> kind.apply(x[0], x[1], x[2], h.precision()));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr q = ast.arg2();
      IExpr z = ast.arg3();
      if (q.isZero()) {
        // the equation has constant coefficients
        IExpr sqrtA = F.Sqrt(a);
        switch (kind) {
          case C:
            return F.Cos(F.Times(sqrtA, z));
          case C_PRIME:
            return F.Times(F.CN1, sqrtA, F.Sin(F.Times(sqrtA, z)));
          case S:
            return F.Sin(F.Times(sqrtA, z));
          default:
            return F.Times(sqrtA, F.Cos(F.Times(sqrtA, z)));
        }
      }
      if (z.isZero()) {
        // the odd ones; the even ones depend on the normalization
        return isEven() ? F.NIL : F.C0;
      }
      IExpr negZ = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negZ.isPresent()) {
        IExpr mirrored = F.ternaryAST3(ast.head(), a, q, negZ);
        return isEven() ? mirrored : F.Negate(mirrored);
      }
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Whether a Struve function of arbitrary precision numbers, which the routines of
   * {@link ApcomplexStruveMath} have declined, is out of reach of the series of the library too:
   * its terms grow to <code>E^Abs(z)</code>, and the sum of them doesn't end in a reasonable time
   * for an argument beyond the limit of those routines.
   */
  private static boolean isBeyondStruveSeries(IInexactNumber n, IInexactNumber z) {
    if (n instanceof ApfloatNum || n instanceof ApcomplexNum || z instanceof ApfloatNum
        || z instanceof ApcomplexNum) {
      return !(z.abs().evalf() <= 3000.0);
    }
    return false;
  }

  /**
   * <code>StruveH(nu, 0)</code> and <code>StruveL(nu, 0)</code>, from their series
   * <code>Sum((z/2)^(2*k+nu+1)/(Gamma(k+3/2)*Gamma(k+nu+3/2)), k)</code> (with <code>(-1)^k</code>
   * for <code>StruveH</code>).
   *
   * @return {@link F#NIL} if the real part of <code>nu</code> can't be compared with
   *         <code>-1</code>, and for <code>nu == -1</code>
   */
  /**
   * <code>StruveH(n, I*z)</code> or <code>StruveL(n, I*z)</code> in terms of the other function of
   * <code>z</code>, <code>((I*z)^(1+n)*partner(n,z))/z^(1+n)</code>.
   */
  private static IExpr struveImaginaryArgument(IAST ast, BinaryOperator<IExpr> partner) {
    if (ast.isAST2()) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      IExpr restExpr = AbstractFunctionEvaluator.extractImaginaryUnit(z);
      if (restExpr.isPresent()) {
        return F.Times(F.Power(F.Times(F.CI, restExpr), F.Plus(F.C1, n)),
            F.Power(restExpr, F.Subtract(F.CN1, n)), partner.apply(n, restExpr));
      }
    }
    return F.NIL;
  }

  /**
   * <code>StruveH(nu, z)</code> or <code>StruveL(nu, z)</code> of a negative <code>z</code> in terms
   * of the value at <code>-z</code>, <code>-z^nu*(-z)^(-nu)*head(nu,-z)</code>.
   *
   * @return {@link F#NIL} if <code>z</code> is not of negative form
   */
  private static IExpr struveReflection(ISymbol head, IExpr nu, IExpr z) {
    IExpr negZ = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
    if (negZ.isPresent()) {
      return F.Times(F.CN1, F.Power(z, nu), F.Power(negZ, F.Negate(nu)),
          F.binaryAST2(head, nu, negZ));
    }
    return F.NIL;
  }

  /**
   * The numeric value of <code>StruveH</code> or <code>StruveL</code>: the machine routine for two
   * machine reals, the working precision routine, and last the series of the library.
   */
  private static IExpr struveNumeric(IAST ast, DoubleBinaryOperator machineFunction,
      WorkingPrecision.Routine routine,
      BiFunction<IInexactNumber, IInexactNumber, IExpr> series) {
    if (ast.argSize() == 2) {
      IInexactNumber n = (IInexactNumber) ast.arg1();
      IInexactNumber z = (IInexactNumber) ast.arg2();
      if (n instanceof Num && z instanceof Num) {
        try {
          double machine =
              machineFunction.applyAsDouble(((Num) n).doubleValue(), ((Num) z).doubleValue());
          if (!Double.isNaN(machine)) {
            return F.num(machine);
          }
        } catch (ArithmeticException | ApfloatRuntimeException ex) {
          // the arbitrary precision routines below
        }
      }
      // the expansion for a large argument, or the series with the digits which it cancels
      IExpr value = WorkingPrecision.evaluate(n, z, z.isReal() && !z.isNegative(), routine);
      if (value.isPresent()) {
        // on the negative real axis the value is real for some orders only
        return value.isNumber() && !value.isReal() && value.im().isZero() ? value.re() : value;
      }
      if (isBeyondStruveSeries(n, z)) {
        return F.NIL;
      }
      return series.apply(n, z);
    }
    return F.NIL;
  }

  private static IExpr struveAtZero(IExpr nu) {
    if (nu.isFraction() && ((IFraction) nu).denominator().equals(F.C2) && nu.isNegative()) {
      // nu = -n-1/2: the terms with a pole of Gamma(k+nu+3/2) are zero, the first other one has
      // the power (z/2)^(n+1/2)
      return F.C0;
    }
    if (nu.isMinusOne()) {
      return F.NIL;
    }
    IExpr re = nu.re();
    if (re.isMinusOne()) {
      // the factor (z/2)^(I*Im(nu)) has no value at 0
      return S.Indeterminate;
    }
    IExpr greater = re.greaterThan(F.CN1);
    if (greater.isTrue()) {
      return F.C0;
    }
    if (greater.isFalse()) {
      return F.CComplexInfinity;
    }
    return F.NIL;
  }

  private static final class StruveH extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      return struveImaginaryArgument(ast, F::StruveL);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      return struveNumeric(ast, DMath::struveH,
          (nu, argument, precision) -> ApcomplexStruveMath.struveH(nu, argument),
          IInexactNumber::struveH);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr result = struveH(arg1, arg2, engine);
      return result.isPresent() ? result : F.NIL;
    }

    // CPD-OFF closed-form half-integer Struve formulas
    private IExpr struveH(IExpr arg1, IExpr arg2, EvalEngine engine) {
      try {
        if (F.isExactZero(arg2)) {
          return struveAtZero(arg1);
        }
        if (arg1.isFraction()) {
          IFraction frac = (IFraction) arg1;
          if (frac.denominator().equals(F.C2)) {
            int num = frac.numerator().toIntDefault();
            // as the half-integer orders of the Bessel functions: the sum of an order beyond has
            // hundreds of terms, which cancel when they are evaluated
            if (num != Integer.MIN_VALUE && Math.abs(num) < 42) {
              if (num < 0) {
                // H_{-n-1/2}(z) = (-1)^n * J_{n+1/2}(z)
                int n = (-num - 1) / 2;
                IExpr besselJ = engine.evaluate(F.BesselJ(F.Plus(F.ZZ(n), F.C1D2), arg2));
                // CPD-ON
                IExpr result = (n % 2 == 0) ? besselJ : F.Negate(besselJ);
                return engine.evaluate(F.Together(result));
              } else {
                // H_{n+1/2}(z) = Y_{n+1/2}(z) + Sum
                int n = (num - 1) / 2;
                IExpr sum = F.sum(
                    k -> F.Times(F.Power(F.CN1, F.Plus(k, F.ZZ(-n))),
                        // CPD-OFF closed-form half-integer Struve formulas
                        F.Power(F.Gamma(F.Plus(k, F.C1)), F.CN1),
                        F.Power(F.Gamma(F.Plus(k, F.ZZ(-n), F.C1D2)), F.CN1), F.Power(
                            F.Times(arg2, F.C1D2), F.Plus(F.Times(F.C2, k), F.ZZ(-n), F.CN1D2))),
                    0, n);
                IExpr result = F.Plus(F.BesselY(arg1, arg2), sum);
                // CPD-ON
                return engine.evaluate(F.Together(result));
              }
            }
          }
        }
        return struveReflection(S.StruveH, arg1, arg2);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.StruveH, rex);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class StruveL extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      return struveImaginaryArgument(ast, F::StruveH);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      return struveNumeric(ast, DMath::struveL,
          (nu, argument, precision) -> ApcomplexStruveMath.struveL(nu, argument),
          IInexactNumber::struveL);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr result = struveL(arg1, arg2, engine);
      return result.isPresent() ? result : F.NIL;
    }

    // CPD-OFF closed-form half-integer Struve formulas
    private static IExpr struveL(IExpr arg1, IExpr arg2, EvalEngine engine) {
      try {
        if (F.isExactZero(arg2)) {
          return struveAtZero(arg1);
        }
        if (arg1.isFraction()) {
          IFraction frac = (IFraction) arg1;
          if (frac.denominator().equals(F.C2)) {
            int num = frac.numerator().toIntDefault();
            if (num != Integer.MIN_VALUE && Math.abs(num) < 42) {
              if (num < 0) {
                // L_{-n-1/2}(z) = I_{n+1/2}(z)
                int n = (-num - 1) / 2;
                IExpr besselI = engine.evaluate(F.BesselI(F.Plus(F.ZZ(n), F.C1D2), arg2));
                return engine.evaluate(F.Together(besselI));
              } else {
              // CPD-ON
                // L_{n+1/2}(z) = I_{-n-1/2}(z) - Sum
                int n = (num - 1) / 2;
                IExpr sum = F.sum(
                    // CPD-OFF closed-form half-integer Struve formulas
                    k -> F.Times(F.Power(F.Gamma(F.Plus(k, F.C1)), F.CN1),
                        F.Power(F.Gamma(F.Plus(k, F.ZZ(-n), F.C1D2)), F.CN1), F.Power(
                            F.Times(arg2, F.C1D2), F.Plus(F.Times(F.C2, k), F.ZZ(-n), F.CN1D2))),
                    0, n);
                IExpr result = F.Subtract(F.BesselI(F.Negate(arg1), arg2), sum);
                // CPD-ON
                return engine.evaluate(F.Together(result));
              }
            }
          }
        }
        if (!arg2.isZero()) {
          return struveReflection(S.StruveL, arg1, arg2);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.StruveL, rex);
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
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  public static void initialize() {
    Initializer.init();
  }

  private SpecialFunctions() {}
}
