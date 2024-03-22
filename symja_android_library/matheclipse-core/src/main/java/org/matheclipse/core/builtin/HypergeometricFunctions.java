package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.ApfloatRuntimeException;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.HypergeometricJS;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ResultException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class HypergeometricFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AppellF1.setEvaluator(new AppellF1());
      S.CosIntegral.setEvaluator(new CosIntegral());
      S.CoshIntegral.setEvaluator(new CoshIntegral());
      S.ExpIntegralE.setEvaluator(new ExpIntegralE());
      S.ExpIntegralEi.setEvaluator(new ExpIntegralEi());
      S.FresnelC.setEvaluator(new FresnelC());
      S.FresnelS.setEvaluator(new FresnelS());
      S.GegenbauerC.setEvaluator(new GegenbauerC());
      S.Hypergeometric0F1.setEvaluator(new Hypergeometric0F1());
      S.Hypergeometric0F1Regularized.setEvaluator(new Hypergeometric0F1Regularized());
      S.Hypergeometric1F1.setEvaluator(new Hypergeometric1F1());
      S.Hypergeometric1F1Regularized.setEvaluator(new Hypergeometric1F1Regularized());
      S.Hypergeometric2F1.setEvaluator(new Hypergeometric2F1());
      S.Hypergeometric2F1Regularized.setEvaluator(new Hypergeometric2F1Regularized());
      S.HypergeometricPFQ.setEvaluator(new HypergeometricPFQ());
      S.HypergeometricU.setEvaluator(new HypergeometricU());
      S.LogIntegral.setEvaluator(new LogIntegral());
      S.SinIntegral.setEvaluator(new SinIntegral());
      S.SinhIntegral.setEvaluator(new SinhIntegral());
      S.WhittakerM.setEvaluator(new WhittakerM());
      S.WhittakerW.setEvaluator(new WhittakerW());
    }
  }

  private static class AppellF1 extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b1 = ast.arg2();
      IExpr b2 = ast.arg3();
      IExpr c = ast.arg4();
      IExpr z1 = ast.arg5();
      IExpr z2 = ast.get(6);
      // // https://functions.wolfram.com/HypergeometricFunctions/AppellF1/04/03/02/0001/
      // if (b1.compareTo(b2) > 0) {
      // // MMA doesn't swap args
      // // permutation symmetry
      // return F.AppellF1(a, b2, b1, c, z1, z2);
      // }
      // if (z1.compareTo(z2) > 0) {
      // // MMA doesn't swap args
      // // permutation symmetry
      // return F.AppellF1(a, b1, b2, c, z2, z1);
      // }
      if (z1.isZero()) {
        if (z2.isZero()) {
          return F.C1;
        }
        return F.Hypergeometric2F1(a, b2, c, z2);
      }
      if (z2.isZero()) {
        return F.Hypergeometric2F1(a, b1, c, z1);
      }
      if (z2.isOne()) {
        return F.Times(F.Hypergeometric2F1(a, b1, F.Subtract(c, b2), z1),
            F.Hypergeometric2F1(a, b2, c, F.C1));
      }

      // avoid isPossibleZero for Rubi evaluation
      if (z1.subtract(z2).isZero()) {
        // Hypergeometric2F1(a, b1 + b2, c, z1)
        return F.Hypergeometric2F1(a, F.Plus(b1, b2), c, z1);
      }
      if (b1.subtract(b2).isZero() && z1.plus(z2).isZero()) {
        // HypergeometricPFQ({1/2+a/2,a/2,b1},{1/2+c/2,c/2},z1^2)
        return F.HypergeometricPFQ(F.list(F.Plus(F.C1D2, F.Divide(a, F.C2)), F.Divide(a, F.C2), b1), //
            F.list(F.Plus(F.C1D2, F.Divide(c, F.C2)), F.Divide(c, F.C2)), F.Sqr(z1));
      }
      if (b1.plus(b2).subtract(c).isZero()) {
        // Hypergeometric2F1(a, b1, b1 + b2, (z1 - z2)/(1 - z2)) / (1 - z2)^a
        return F.Times( //
            F.Hypergeometric2F1(a, //
                b1, F.Plus(b1, b2), F.Divide(F.Subtract(z1, z2), F.Subtract(F.C1, z2))),
            F.Power(F.Subtract(F.C1, z2), a));
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_6_6;
    }
  }

  private static class CosIntegral extends AbstractFunctionEvaluator { // implements INumeric,
                                                                       // DoubleUnaryOperator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr temp = basicRewrite(z);
      if (temp.isPresent()) {
        return temp;
      }
      return F.NIL;
    }

    private static IExpr basicRewrite(IExpr z) {
      if (z.isZero()) {
        return F.CNInfinity;
      }
      if (z.isInfinity()) {
        return F.C0;
      }
      if (z.isNegativeInfinity()) {
        return F.Times(F.CI, S.Pi);
      }
      if (z.isDirectedInfinity(F.CI) || z.isDirectedInfinity(F.CNI)) {
        return F.CInfinity;
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.cosIntegral();
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

  private static class CoshIntegral extends AbstractFunctionEvaluator { // implements INumeric,
                                                                        // DoubleUnaryOperator {

    // @Override
    // public double applyAsDouble(double z) {
    // if (F.isZero(z)) {
    // return Double.NEGATIVE_INFINITY;
    // }
    // // 1/4*(2*(ExpIntegralEi(-z)+ExpIntegralEi(z))+Log(-1/z)+Log(1/z)-Log(-z)+3*Log(z))
    // return 0.25 * (2.0 * (ExpIntegralEi.CONST.applyAsDouble(-z) +
    // ExpIntegralEi.CONST.applyAsDouble(z))
    // + Math.log(-1 / z) + Math.log(1 / z) - Math.log(-z) + 3 * Math.log(z));
    // }
    //
    // @Override
    // public IExpr e1DblArg(final double arg1) {
    // if (F.isZero(arg1)) {
    // return F.CNInfinity;
    // }
    // return F.num(applyAsDouble(arg1));
    // }
    //
    // @Override
    // public double evalReal(final double[] stack, final int top, final int size) {
    // if (size != 1) {
    // throw new UnsupportedOperationException();
    // }
    // return applyAsDouble(stack[top]);
    // }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr temp = basicRewrite(z);
      if (temp.isPresent()) {
        return temp;
      }
      // if (engine.isDoubleMode()) {
      // try {
      // double zDouble = Double.NaN;
      // try {
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(zDouble)) {
      // Complex zc = z.evalfc();
      // return F.complexNum(GammaJS.coshIntegral(zc));
      // } else {
      // if (F.isZero(zDouble)) {
      // return F.CNInfinity;
      // }
      // return F.complexNum(GammaJS.coshIntegral(new Complex(zDouble)));
      // }
      //
      // } catch (ThrowException te) {
      // LOGGER.debug("CoshIntegral.evaluate() failed", te);
      // return te.getValue();
      // } catch (ValidateException ve) {
      // return Errors.printMessage(ast.topHead(), ve, engine);
      // } catch (RuntimeException rex) {
      // LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      // }
      // }
      return F.NIL;
    }

    private static IExpr basicRewrite(IExpr z) {
      if (z.isZero()) {
        return F.CNInfinity;
      }
      if (z.isInfinity()) {
        return F.CInfinity;
      }
      if (z.isNegativeInfinity()) {
        return F.CInfinity;
      }
      if (z.isDirectedInfinity(F.CI)) {
        return F.Times(F.CPiHalf, F.CI);
      }
      if (z.isDirectedInfinity(F.CNI)) {
        return F.Times(F.CNPiHalf, F.CI);
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.coshIntegral();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ExpIntegralE extends AbstractFunctionEvaluator implements IFunctionExpand {

    @Override
    public IExpr functionExpand(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr v = ast.arg1();
        IExpr z = ast.arg2();
        if (v.isFraction() && ((IFraction) v).times(F.C2).isInteger()) {
          IExpr n = ((IFraction) v).subtract(F.C1D2);
          ISymbol k = F.Dummy("k");
          // https://functions.wolfram.com/GammaBetaErf/ExpIntegralE/03/01/02/0014/
          // (1-Erf(Sqrt(z))*Gamma(1/2-n)+Sum(z^(1/2+k)/Pochhammer(1/2-n,1+k+n),{k,0,-1-n})/E^z-Sum(z^(1/2+k)/Pochhammer(1/2-n,1+k+n),{k,-n,-1})/E^z)/z^(1/2-n)
          return F
              .Times(
                  F.Power(z, F.Plus(F.CN1D2,
                      n)),
                  F.Plus(
                      F.Times(F.Subtract(F.C1, F.Erf(F.Sqrt(z))), F
                          .Gamma(F.Subtract(F.C1D2, n))),
                      F.Times(
                          F.Power(F
                              .Exp(z), F.CN1),
                          F.Sum(F.Times(F.Power(z, F.Plus(F.C1D2, k)),
                              F.Power(F.Pochhammer(F.Subtract(F.C1D2, n), F.Plus(F.C1, k, n)),
                                  F.CN1)),
                              F.list(k, F.C0, F.Subtract(F.CN1, n)))),
                      F.Times(
                          F.CN1, F.Power(F
                              .Exp(z), F.CN1),
                          F.Sum(
                              F.Times(F.Power(z, F.Plus(F.C1D2, k)), F.Power(
                                  F.Pochhammer(F.Subtract(F.C1D2, n), F.Plus(F.C1, k, n)), F.CN1)),
                              F.list(k, F.Negate(n), F.CN1)))));
        }
        // Gamma(1-v,z)/z^(1-v)
        return F.Times(F.Power(z, F.Plus(F.CN1, v)), F.Gamma(F.Subtract(F.C1, v), z));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr z = ast.arg2();
      return basicRewrite(n, z);
    }

    private static IExpr basicRewrite(IExpr n, IExpr z) {
      if (n.isZero()) {
        // 1/(E^z*z)
        return F.Power(F.Times(z, F.Power(S.E, z)), -1);
      }
      if (z.isZero()) {
        IExpr nRe = n.re();
        if (nRe.greaterThan(F.C1).isTrue()) {
          // 1/(n-1)
          return F.Power(F.Plus(n, F.CN1), -1);
        }
        if (nRe.lessThan(F.C1).isTrue()) {
          return F.CComplexInfinity;
        }
      }
      // if (engine.isDoubleMode()) {
      // try {
      // double nDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // nDouble = n.evalf();
      // zDouble = z.evalf();
      // return F.complexNum(GammaJS.expIntegralE(new Complex(nDouble), new Complex(zDouble)));
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(nDouble) || Double.isNaN(zDouble)) {
      // Complex nc = n.evalfc();
      // Complex zc = z.evalfc();
      // return F.complexNum(GammaJS.expIntegralE(nc, zc));
      // }
      //
      // } catch (ThrowException te) {
      // LOGGER.debug("ExpIntegralE.evaluate() failed", te);
      // return te.getValue();
      // } catch (ValidateException ve) {
      // return Errors.printMessage(ast.topHead(), ve, engine);
      // } catch (RuntimeException rex) {
      // LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber n = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();
        IExpr temp = basicRewrite(n, z);
        if (temp.isPresent()) {
          return temp;
        }
        return n.expIntegralE(z);
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

  private static class ExpIntegralEi extends AbstractFunctionEvaluator { // implements INumeric,
                                                                         // DoubleUnaryOperator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      return basicRewrite(z);
    }

    private static IExpr basicRewrite(IExpr z) {
      if (z.isZero()) {
        return F.CNInfinity;
      }
      if (z.isInfinity()) {
        return F.CInfinity;
      }
      if (z.isNegativeInfinity()) {
        return F.C0;
      }
      if (z.isDirectedInfinity(F.CI)) {
        return F.Times(F.CI, S.Pi);
      }
      if (z.isDirectedInfinity(F.CNI)) {
        return F.Times(F.CNI, S.Pi);
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.expIntegralEi();
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

  private static class FresnelC extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNumber()) {
        if (arg1.isZero()) {
          return F.C0;
        }
        if (engine.isNumericMode()) {
          return arg1.fresnelC();
        }
      }
      if (arg1.isInfinity()) {
        return F.C1D2;
      }
      if (arg1.isNegativeInfinity()) {
        return F.CN1D2;
      }
      if (arg1.equals(F.CIInfinity)) {
        return F.Divide(F.CI, F.C2);
      }
      if (arg1.equals(F.CNIInfinity)) {
        return F.Divide(F.CNI, F.C2);
      }
      if (arg1.equals(F.CComplexInfinity)) {
        return S.Indeterminate;
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Negate(F.FresnelC(negExpr));
      }
      IExpr restExpr = AbstractFunctionEvaluator.extractImaginaryUnit(arg1);
      if (restExpr.isPresent()) {
        return F.Times(F.CI, F.FresnelC(restExpr));
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

  private static class FresnelS extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNumber()) {
        if (arg1.isZero()) {
          return F.C0;
        }
        if (engine.isNumericMode()) {
          return arg1.fresnelS();
        }
      }
      if (arg1.isInfinity()) {
        return F.C1D2;
      }
      if (arg1.isNegativeInfinity()) {
        return F.CN1D2;
      }
      if (arg1.equals(F.CIInfinity)) {
        return F.Divide(F.CNI, F.C2);
      }
      if (arg1.equals(F.CNIInfinity)) {
        return F.Divide(F.CI, F.C2);
      }
      if (arg1.equals(F.CComplexInfinity)) {
        return S.Indeterminate;
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Negate(F.FresnelS(negExpr));
      }
      IExpr restExpr = AbstractFunctionEvaluator.extractImaginaryUnit(arg1);
      if (restExpr.isPresent()) {
        return F.Times(F.CNI, F.FresnelS(restExpr));
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

  private static final class GegenbauerC extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();

      if (ast.size() == 4) {
        // GegenbauerC(n, l, z)
        IExpr l = ast.arg2();
        IExpr z = ast.arg3();
        if (l.isNumber() && n.isNumber()) {
          if (z.isZero()) {
            // https://functions.wolfram.com/Polynomials/GegenbauerC3/03/01/01/0001/
            // (2^n*Sqrt(Pi)*Gamma(n/2+l))/(Gamma(1/2*(1-n))*Gamma(n+1)*Gamma(l))
            return F.Times(F.Power(F.C2, n), F.CSqrtPi,
                F.Power(F.Times(F.Gamma(F.Times(F.C1D2, F.Subtract(F.C1, n))),
                    F.Gamma(F.Plus(n, F.C1)), F.Gamma(l)), F.CN1),
                F.Gamma(F.Plus(F.Times(F.C1D2, n), l)));
          }
          if (z.isOne()) {
            // https://functions.wolfram.com/Polynomials/GegenbauerC3/03/01/01/0002/
            // Gamma(2*l+n)/(Gamma(2*l)*Gamma(1+n))
            IExpr v1 = F.Times(F.C2, l);
            return F.Times(F.Power(F.Gamma(F.Plus(F.C1, n)), F.CN1), F.Power(F.Gamma(v1), F.CN1),
                F.Gamma(F.Plus(n, v1)));
          }
          if (z.isMinusOne()) {
            if (((INumber) l).re().isLT(F.C1D2)) {
              // https://functions.wolfram.com/Polynomials/GegenbauerC3/03/01/01/0003/
              // (Cos[Pi*(l+n)]*Gamma[2*l+n]*Sec[Pi*l])/(Gamma[2*l]*Gamma[1+n])
              IExpr v1 = F.Times(F.C2, l);
              return F.Times(F.Cos(F.Times(F.Plus(l, n), F.Pi)),
                  F.Power(F.Gamma(F.Plus(F.C1, n)), F.CN1), F.Power(F.Gamma(v1), F.CN1),
                  F.Gamma(F.Plus(n, v1)), F.Sec(F.Times(l, F.Pi)));
            } else if (((INumber) l).re().isGT(F.C1D2)) {
              // https://functions.wolfram.com/Polynomials/GegenbauerC3/03/01/01/0004/
              return F.CComplexInfinity;
            }
          }
        }

        if (l.isNumEqualRational(F.C1D2)) {
          return F.LegendreP(n, z);
        }

        int lInt = l.toIntDefault();
        if (lInt >= 0) {
          switch (lInt) {
            case 0:
              return F.C0;
            case 1:
              return F.ChebyshevU(n, z);
            case 2:
              // ((-2 - n)*ChebyshevU(n, z) + z*(1 + n)*ChebyshevU(1 + n, z))/(2*(-1 + z^2))
              return F.Times(F.C1D2, F.Power(F.Plus(F.CN1, F.Sqr(z)), -1),
                  F.Plus(F.Times(F.Plus(F.CN2, F.Negate(S.n)), F.ChebyshevU(n, z)),
                      F.Times(F.Plus(F.C1, n), z, F.ChebyshevU(F.Plus(F.C1, n), z))));
          }
        }
        if (n.isZero()) {
          return F.C1;
        }
        int nInt = n.toIntDefault();
        if (nInt > 0) {
          // Sum(((-1)^k*Pochhammer(l, n - k)*(2*z)^(n - 2*k))/(k!*(n - 2*k)!), {k, 0, Floor(n/2)})
          return F.sum(k -> F.Times(F.Power(F.CN1, k),
              F.Power(F.Times(F.C2, z), F.Plus(F.Times(F.CN2, k), n)), F.Power(F.Factorial(k), -1),
              F.Power(F.Factorial(F.Plus(F.Times(F.CN2, k), n)), -1),
              F.Pochhammer(l, F.Plus(F.Negate(k), n))), 0, nInt / 2);
        }
        return F.NIL;
      }
      // GegenbauerC(n, z)
      IExpr z = ast.arg2();
      int nInt = n.toIntDefault();
      if (nInt > Integer.MIN_VALUE) {
        if (nInt == 0) {
          Errors.printMessage(S.GegenbauerC, "infy", F.List(F.Divide(F.C1, F.C0)), engine);
          return F.CComplexInfinity;
        }
        if (nInt == 1) {
          // 2*z
          return F.Times(F.C2, z);
        }
        if (nInt == 2) {
          // 2*z^2 - 1
          return F.Plus(F.CN1, F.Times(F.C2, F.Sqr(z)));
        }
        if (nInt > 2) {
          // (2^n/n)*z^n + Sum(((-1)^k*(n - k - 1)!*(2*z)^(n - 2*k))/(k! * (n -
          // 2*k)!), {k, 1, Floor(n/2)})
          int floorND2 = nInt / 2;
          return Plus(Times(Power(C2, n), Power(n, -1), Power(z, n)),
              F.sum(k -> Times(Power(CN1, k), Power(Times(C2, z), Plus(Times(F.CN2, k), n)),
                  Power(Times(Factorial(k), Factorial(Plus(Times(F.CN2, k), n))), -1),
                  Factorial(Plus(CN1, Negate(k), n))), 1, floorND2));
        }
      }

      int zInt = z.toIntDefault();
      if (zInt > Integer.MIN_VALUE) {
        if (zInt == 0) {
          // 2 * (1/v) * Cos(1/2*Pi*v)
          return F.Times(F.C2, F.Power(n, F.CN1), F.Cos(F.Times(C1D2, S.Pi, n)));
        }
        if (zInt == 1) {
          // 2 / v
          return F.Divide(F.C2, n);
        }
        if (zInt == -1) {
          // (2/v)*Cos(Pi*v)
          return F.Times(F.C2, F.Power(n, F.CN1), F.Cos(F.Times(S.Pi, n)));
        }
      }

      if (n.equals(F.C1D2)) {
        // 4*Sqrt((1+z)/2)
        return F.Times(F.C4, F.Sqrt(F.Times(F.C1D2, F.Plus(F.C1, z))));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(n);
      if (negExpr.isPresent()) {
        return F.GegenbauerC(negExpr, z).negate();
      }
      if (n.isInteger() && n.isPositive()) {
        negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
        if (negExpr.isPresent()) {
          return F.Times(F.Power(F.CN1, n), F.GegenbauerC(n, negExpr));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.isAST2() || ast.isAST3()) {
        final IInexactNumber n = (IInexactNumber) ast.arg1();
        final IInexactNumber l;
        final IInexactNumber z;
        IExpr temp = F.NIL;
        if (ast.isAST2()) {
          l = F.CD0;
          z = (IInexactNumber) ast.arg2();
          temp = n.gegenbauerC(z);
        } else {
          l = (IInexactNumber) ast.arg2();
          z = (IInexactNumber) ast.arg3();
          temp = n.gegenbauerC(l, z);
        }
        if (temp.isPresent()) {
          return temp;
        }
      }
      // if (ast.argSize() == 2) {
      // IInexactNumber n = (IInexactNumber) ast.arg1();
      // if (n.isZero()) {
      // Errors.printMessage(S.GegenbauerC, "infy", F.List(F.Divide(F.C1, F.C0)), engine);
      // return F.CComplexInfinity;
      // }
      // IInexactNumber z = (IInexactNumber) ast.arg2();
      // // (2*Cos(n*ArcCos(z)))/n
      // IInexactNumber cos = n.times(z.acos()).cos();
      // return cos.plus(cos).times(n.reciprocal());
      // }
      // if (ast.argSize() == 3) {
      // IInexactNumber n = (IInexactNumber) ast.arg1();
      // if (n.isZero()) {
      // return F.C1;
      // }
      // IInexactNumber l = (IInexactNumber) ast.arg2();
      // IInexactNumber z = (IInexactNumber) ast.arg3();
      // // https://functions.wolfram.com/HypergeometricFunctions/GegenbauerC3General/26/04/01/0001/
      // // Pochhammer(2*l,n)/Pochhammer(l+1/2,n)*JacobiP(n,-1/2+l,-1/2+l,z)
      // INumber v1 = l.plus(F.CN1D2);
      // IAST gegenbauerC = F.Times(F.JacobiP(n, v1, v1, z),
      // F.Power(F.Pochhammer(F.Plus(F.C1D2, l), n), F.CN1), F.Pochhammer(F.Times(F.C2, l), n));
      // return engine.evaluate(gegenbauerC);
      // }

      return F.NIL;
    }

    // (2*Cos(n*ArcCos(x)))/n
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

  private static class Hypergeometric0F1 extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr b = ast.arg1();
      IExpr z = ast.arg2();
      if (z.isZero()) {
        return F.C1;
      }
      if (z.isInfinity()) {
        return F.CComplexInfinity;
      }
      if (engine.isNumericMode()) {
        try {
          IExpr res = b.hypergeometric0F1(z);
          if (res.isNumber()) {
            return res;
          }
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
        }
      }
      // if (engine.isDoubleMode()) {
      // try {
      // double bDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // bDouble = b.evalf();
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
      // Complex bc = b.evalfc();
      // Complex zc = z.evalfc();
      //
      // return F.complexNum(HypergeometricJS.hypergeometric0F1(bc, zc));
      //
      // } else {
      // return F.num(HypergeometricJS.hypergeometric0F1(bDouble, zDouble));
      // }
      //
      // } catch (ValidateException ve) {
      // return Errors.printMessage(ast.topHead(), ve, engine);
      // } catch (RuntimeException rex) {
      // LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      // }
      // }
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

  private static class Hypergeometric0F1Regularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr b = ast.arg1();
      IExpr z = ast.arg2();
      return basicRewrite(b, z);
    }

    private IExpr basicRewrite(IExpr b, IExpr z) {
      if (z.isZero()) {
        // 1/Gamma(b)
        return F.Power(F.Gamma(b), F.CN1);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber a = (IInexactNumber) ast.arg1();
        IInexactNumber z = (IInexactNumber) ast.arg2();
        IExpr temp = basicRewrite(a, z);
        if (temp.isPresent()) {
          return temp;
        }
        try {
          return a.hypergeometric0F1Regularized(z);
        } catch (ArithmeticException | ApfloatRuntimeException e) {
          // java.lang.ArithmeticException: Gamma of zero
          Errors.printMessage(S.Hypergeometric0F1, e, engine);
        }
        // // z^(1/2*(1-a))*BesselI(-1+a,2*Sqrt(z))
        // return F.Times(F.Power(z, F.Times(F.C1D2, F.Subtract(F.C1, a))),
        // F.BesselI(F.Plus(F.CN1, a), F.Times(F.C2, F.Sqrt(z))));
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

  private static class Hypergeometric1F1 extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      if (a.isZero()) {
        return F.C1;
      }
      IExpr b = ast.arg2();
      IExpr z = ast.arg3();
      if (b.isZero()) {
        if (z.isZero()) {
          return S.Indeterminate;
        }
        return F.CComplexInfinity;
      }
      if (z.isZero()) {
        return F.C1;
      }

      if (a.isInteger() && b.isInteger() && a.isNegative() && b.isNegative()
          && ((IInteger) b).isGT((IInteger) a)) {
        return F.CComplexInfinity;
      }
      try {
        IExpr bPlus1 = engine.evaluate(b.plus(F.C1));
        if (a.equals(bPlus1)) {
          // (E^z * (-1 + a + z)) / (-1 + a)
          return F.Times(F.Power(S.E, z), F.Divide(F.Plus(F.CN1, a, z), F.Plus(a, F.CN1)));
        }
        if (engine.isNumericMode()) {
          try {
            IExpr res = a.hypergeometric1F1(b, z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
          // } else if (engine.isDoubleMode()) {
          //
          // double aDouble = Double.NaN;
          // double bDouble = Double.NaN;
          // double zDouble = Double.NaN;
          // try {
          // aDouble = a.evalf();
          // bDouble = b.evalf();
          // zDouble = z.evalf();
          // } catch (ValidateException ve) {
          // }
          // if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
          // Complex ac = a.evalfc();
          // Complex bc = b.evalfc();
          // Complex zc = z.evalfc();
          //
          // return F.complexNum(HypergeometricJS.hypergeometric1F1(ac, bc, zc));
          //
          // } else {
          // return F.num(HypergeometricJS.hypergeometric1F1(aDouble, bDouble, zDouble));
          // }
        }
        if (a.equals(b)) {
          // E^z
          return F.Power(S.E, z);
        }
        if (a.isOne()) {
          // (-1 + b)*E^z*z^(1 - b)*(Gamma(-1 + b) - Gamma(-1 + b, z))
          return F.Times(F.Plus(F.CN1, b), F.Power(S.E, z), F.Power(z, F.Plus(F.C1, F.Negate(b))),
              F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z))));
        }
        if (a.isMinusOne()) {
          // 1 - z/b
          return F.Subtract(F.C1, F.Divide(z, b));
        }
        if (a.isNumEqualInteger(F.C2)) {
          // (-1 + b)*(1 + (2 - b)*E^z*z^(1 - b)* (Gamma(-1 + b) - Gamma(-1 + b, z)) + E^z*z^(2 -
          // b)*(Gamma(-1
          // +
          // b] - Gamma(-1 + b, z)))
          return F.Times(F.Plus(F.CN1, b),
              F.Plus(F.C1,
                  F.Times(F.Plus(F.C2, F.Negate(b)), F.Power(S.E, z),
                      F.Power(z, F.Plus(F.C1, F.Negate(b))),
                      F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z)))),
                  F.Times(F.Power(S.E, z), F.Power(z, F.Plus(F.C2, F.Negate(b))),
                      F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z))))));
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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

  private static class Hypergeometric1F1Regularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr z = ast.arg3();
      return basicRewrite(a, b, z);
    }

    private static IExpr basicRewrite(IExpr a, IExpr b, IExpr z) {
      if (z.isZero()) {
        // 1/Gamma(b)
        return F.Power(F.Gamma(b), F.CN1);
      }
      if (a.equals(b)) {
        // E^z/Gamma(a)
        return F.Times(F.Exp(z), F.Power(F.Gamma(a), F.CN1));
      }
      // if (engine.isNumericMode()) {
      // // Hypergeometric1F1(a,b,z)/Gamma(b)
      // return F.Times(F.Power(F.Gamma(b), F.CN1), F.Hypergeometric1F1(a, b, z));
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 3) {
        IInexactNumber a = (IInexactNumber) ast.arg1();
        IInexactNumber b = (IInexactNumber) ast.arg2();
        IInexactNumber z = (IInexactNumber) ast.arg3();
        IExpr temp = basicRewrite(a, b, z);
        if (temp.isPresent()) {
          return temp;
        }
        try {
          return a.hypergeometric1F1Regularized(b, z);
        } catch (ArithmeticException | ApfloatRuntimeException e) {
          // java.lang.ArithmeticException: Gamma of zero
          Errors.printMessage(S.Hypergeometric1F1, e, engine);
          if (e.getMessage().equals("Division by zero")) {
            return F.ComplexInfinity;
          }

        }
        // // Hypergeometric1F1(a,b,z)/Gamma(b)
        // return F.Times(F.Power(F.Gamma(b), F.CN1), F.Hypergeometric1F1(a, b, z));
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

  private static class Hypergeometric2F1 extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IExpr z = ast.arg4();
      if (a.isZero() || b.isZero() || z.isZero()) {
        return F.C1;
      }
      if (a.compareTo(b) > 0) {
        // permutation symmetry
        return F.Hypergeometric2F1(b, a, c, z);
      }
      if (c.isInteger() && c.isNegative()) {
        if (a.isNumber() && b.isNumber()) {
          return F.CComplexInfinity;
        }
      }
      if (a.isInteger()) {
        if (a.isNegative() && z.isOne()) {
          IInteger n = (IInteger) a.negate();
          // Pochhammer(c-b, n) / Pochhammer(c, n)
          return F.Divide(F.Expand(F.Pochhammer(F.Subtract(c, b), n)), F.Pochhammer(c, n));
        }
        // if (a.isOne() && c.isNumEqualInteger(F.C2)) {
        // return
        // // [$ ((1-z)^(1-b)-1)/((-1 + b)*z) $]
        // F.Times(F.Plus(F.CN1, F.Power(F.Subtract(F.C1, z), F.Subtract(F.C1, b))),
        // F.Power(F.Times(F.Plus(F.CN1, b), z), F.CN1)); // $$;
        // }
      }

      if (a.equals(c)) {
        return
        // [$ (1 - z)^(-b) $]
        F.Power(F.Subtract(F.C1, z), F.Negate(b)); // $$;
      }
      if (b.equals(c)) {
        return
        // [$ (1 - z)^(-a) $]
        F.Power(F.Subtract(F.C1, z), F.Negate(a)); // $$;
      }

      if (a.equals(b.plus(F.C1D2))//
          && c.equals(b.times(F.C2))) {
        // Hypergeometric2F1(b+1/2, b, 2*b, z) :=
        // (1+Sqrt(1-z))^(1-2*b)/(2^(1-2*b)*Sqrt(1-z))
        return F.Times(F.Power(F.C2, F.Plus(F.CN1, F.Times(F.C2, b))),
            F.Power(F.Plus(F.C1, F.Sqrt(F.Subtract(F.C1, z))), F.Plus(F.C1, F.Times(F.CN2, b))),
            F.Power(F.Subtract(F.C1, z), F.CN1D2));
      }

      try {
        if (engine.isNumericMode()) {
          try {
            IExpr res = a.hypergeometric2F1(b, c, z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        }
        // if (engine.isDoubleMode()) {
        //
        // double aDouble = Double.NaN;
        // double bDouble = Double.NaN;
        // double cDouble = Double.NaN;
        // double zDouble = Double.NaN;
        // try {
        // aDouble = a.evalf();
        // bDouble = b.evalf();
        // cDouble = c.evalf();
        // zDouble = z.evalf();
        // } catch (ValidateException ve) {
        // }
        // if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(cDouble)
        // || Double.isNaN(zDouble) || (zDouble > 1.0) || (zDouble == -1.0)) {
        // Complex ac = a.evalfc();
        // Complex bc = b.evalfc();
        // Complex cc = c.evalfc();
        // Complex zc = z.evalfc();
        //
        // return F.complexNum(HypergeometricJS.hypergeometric2F1(ac, bc, cc, zc));
        //
        // } else {
        // return F.num(HypergeometricJS.hypergeometric2F1(aDouble, bDouble, cDouble, zDouble));
        // }
        // }
      } catch (ResultException te) {
        LOGGER.debug("Hypergeometric2F1.evaluate() failed", te);
        return te.getValue();
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class Hypergeometric2F1Regularized extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      IExpr z = ast.arg4();
      return basicRewrite(a, b, c, z);
    }

    private static IExpr basicRewrite(IExpr a, IExpr b, IExpr c, IExpr z) {
      if (a.isZero() || b.isZero() || z.isZero()) {
        if (!c.isPossibleZero(true)) {
          // 1/Gamma(c)
          return F.Power(F.Gamma(c), F.CN1);
        }
      }
      if (a.compareTo(b) > 0) {
        // permutation symmetry
        return F.Hypergeometric2F1Regularized(b, a, c, z);
      }

      if (c.isZero() && a.equals(b)) {
        // Hypergeometric2F1Regularized(a_, a_, 0, z_) := a^2*z*Hypergeometric2F1(1 + a, 1 + a, 2,
        // z)
        return F.Times(F.Sqr(a), z, F.Hypergeometric2F1(F.Plus(F.C1, a), F.Plus(F.C1, a), F.C2, z));
      }
      // if (engine.isNumericMode()) {
      // // TODO regularize Hypergeometric2F1 for negative integer values of the third parameter
      // if (a.isNumber() //
      // && b.isNumber() //
      // && (c.isNumber() //
      // && (!((INumber) c).fractionalPart().isZero() || c.isNonNegativeResult()))//
      // && z.isNumber()) {
      // return F.Divide(F.Hypergeometric2F1(a, b, c, z), F.Gamma(c));
      // }
      // }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 4) {
        IInexactNumber a = (IInexactNumber) ast.arg1();
        IInexactNumber b = (IInexactNumber) ast.arg2();
        IInexactNumber c = (IInexactNumber) ast.arg3();
        IInexactNumber z = (IInexactNumber) ast.arg4();
        IExpr temp = basicRewrite(a, b, c, z);
        if (temp.isPresent()) {
          return temp;
        }
        try {
          return a.hypergeometric2F1Regularized(b, c, z);
        } catch (ArithmeticException e) {
          Errors.printMessage(S.Hypergeometric2F1, e, engine);
          if (e.getMessage().equals("Division by zero")) {
            return F.ComplexInfinity;
          }
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
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class HypergeometricPFQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr c = ast.arg3();
      if (c.isList()) {
        // thread elementwise over list in arg3
        return c.mapThread(ast.setAtCopy(3, F.Slot1), 3);
      }
      if (c.isZero() && a.isList() && b.isList()) {
        return F.C1;
      }
      if (a.isVector() > 0) {
        IAST aVector = (IAST) a.normal(false);
        if (!aVector.isEvalFlagOn(IAST.IS_SORTED)) {
          IASTMutable aResult = aVector.copy();
          if (EvalAttributes.sortWithFlags(aResult)) {
            return F.HypergeometricPFQ(aResult, b, c);
          }
          aVector.addEvalFlags(IAST.IS_SORTED);
        }
        a = aVector;
      }
      if (b.isVector() > 0) {
        IAST bVector = (IAST) b.normal(false);
        if (!bVector.isEvalFlagOn(IAST.IS_SORTED)) {
          IASTMutable bResult = bVector.copy();
          if (EvalAttributes.sortWithFlags(bResult)) {
            return F.HypergeometricPFQ(a, bResult, c);
          }
          bVector.addEvalFlags(IAST.IS_SORTED);
        }
        b = bVector;
      }

      // numeric mode isn't set here

      if (c.isInexactNumber() || a.isInexactVector() > 0 || b.isInexactVector() > 0) {
        return numericHypergeometricPFQ(a, b, c, ast, engine);
      }

      return F.NIL;
    }

    private IExpr numericHypergeometricPFQ(IExpr a, IExpr b, IExpr c, IAST ast, EvalEngine engine) {
      try {
        double A[] = a.toDoubleVector();
        double B[] = b.toDoubleVector();
        double cDouble = Double.NaN;
        try {
          cDouble = c.evalf();
        } catch (ValidateException ve) {
        }
        if (A == null || B == null || Double.isNaN(cDouble)) {
          Complex AC[] = a.toComplexVector();
          Complex BC[] = b.toComplexVector();
          if (AC != null && BC != null) {
            return F.complexNum(
                HypergeometricJS.hypergeometricPFQ(AC, BC, c.evalfc(), Config.DOUBLE_TOLERANCE));
          }
        } else {
          INum result = F.num(HypergeometricJS.hypergeometricPFQ(A, B, cDouble));

          return result;
        }

      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class HypergeometricU extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr z = ast.arg3();
      if (a.isZero()) {
        return F.C0;
      }
      if (z.isZero()) {
        IExpr bRe = b.re();
        if (bRe.greaterThan(F.C1).isTrue()) {
          return F.CComplexInfinity;
        }
        if (bRe.lessThan(F.C1).isTrue()) {
          // Gamma(1-b) / Gamma(a-b+1)
          return F.Divide(F.Gamma(F.Subtract(F.C1, b)), F.Gamma(F.Plus(a, F.Negate(b), F.C1)));
        }
      }
      if (a.equals(b)) {
        // b==a ==> E^z*Gamma(1-a,z)
        return F.Times(F.Exp(z), F.Gamma(F.Subtract(F.C1, a), z));
      }
      try {
        {
          IExpr n = engine.evaluate(F.Subtract(b, a));
          if (n.isInteger()) {
            if (n.isOne()) {
              // b==a+1 ==> z^(-a)
              return F.Power(z, a.negate());
            }
            int nInt = n.toIntDefault();
            if (nInt > 0) {
              int nMinus1 = nInt - 1;
              // Sum((Binomial(-1+n, -1-k+n)*Pochhammer(a, k))/z^k, {k, 0, n-1}) / z^a
              return F.Times(F.Power(z, a.negate()), //
                  F.intSum( //
                      k -> F.Times(F.Binomial(nMinus1, nMinus1 - k), F.Pochhammer(a, F.ZZ(k)),
                          F.Power(z, -k)), //
                      0, nMinus1));
            }
          }
        }
        if (engine.isArbitraryMode()) {
          try {
            IExpr res = a.hypergeometricU(b, z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        } else if (engine.isDoubleMode()) {
          try {
            IExpr res = a.hypergeometricU(b, z);
            if (res.isNumber()) {
              return res;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }

          // double aDouble = Double.NaN;
          // double bDouble = Double.NaN;
          // double zDouble = Double.NaN;
          // try {
          // aDouble = a.evalf();
          // bDouble = b.evalf();
          // zDouble = z.evalf();
          // return F.complexNum(HypergeometricJS.hypergeometricU(new Complex(aDouble),
          // new Complex(bDouble), new Complex(zDouble)));
          // } catch (ValidateException ve) {
          // Errors.printMessage(ast.topHead(), ve, engine);
          // }
          // Complex ac = a.evalfc();
          // Complex bc = b.evalfc();
          // Complex zc = z.evalfc();
          // return F.complexNum(HypergeometricJS.hypergeometricU(ac, bc, zc));
        }

        if (a.isInteger() && a.isPositive() && (!b.isNumber() || !z.isNumber())) {
          IInteger n = (IInteger) a;
          ISymbol k = F.Dummy("k");
          // (Gamma(-1+b,z)*z^(1-b)*E^z*LaguerreL(-1+n,1-b,-z)-Sum(LaguerreL(-1-k+n,-b+k+1,-z)/k*LaguerreL(-
          // 1+k,-1+b-k,z),{k,1,-1+n}))/Pochhammer(2-b,-1+n)
          return F.Times(F.Power(F.Pochhammer(F.Subtract(F.C2, b), F.Plus(F.CN1, n)), F.CN1),
              F.Subtract(
                  F.Times(F.Gamma(F.Plus(F.CN1, b), z), F.Power(z, F.Subtract(F.C1, b)), F.Exp(z),
                      F.LaguerreL(F.Plus(F.CN1, n), F.Subtract(F.C1, b), F.Negate(z))),
                  F.Sum(
                      F.Times(F.Power(k, F.CN1),
                          F.LaguerreL(F.Plus(F.CN1, F.Negate(k), n), F.Plus(F.Negate(b), k, F.C1),
                              F.Negate(z)),
                          F.LaguerreL(F.Plus(F.CN1, k), F.Plus(F.CN1, b, F.Negate(k)), z)),
                      F.list(k, F.C1, F.Plus(F.CN1, n)))));
        }
      } catch (ThrowException te) {
        LOGGER.debug("HypergeometricU.evaluate() failed", te);
        return te.getValue();
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
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

  private static class LogIntegral extends AbstractFunctionEvaluator { // implements INumeric,

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      return basicRewrite(z);
    }

    private static IExpr basicRewrite(IExpr arg1) {
      if (arg1.isZero()) {
        return F.C0;
      }
      if (arg1.isOne()) {
        return F.CNInfinity;
      }
      if (arg1.isInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isComplexInfinity()) {
        return F.CComplexInfinity;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.logIntegral();
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

  private static class SinIntegral extends AbstractFunctionEvaluator { // implements INumeric,
                                                                       // DoubleUnaryOperator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr temp = basicRewrite(z);
      if (temp.isPresent()) {
        return temp;
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return Negate(F.SinIntegral(negExpr));
      }
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // I * SinhIntegral(-I*arg1)
        return F.Times(S.I, F.SinhIntegral(F.Times(F.CNI, z)));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(z);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.SinhIntegral(imPart));
      }
      return F.NIL;
    }

    private static IExpr basicRewrite(IExpr z) {
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isInfinity()) {
        return F.CPiHalf;
      }
      if (z.isNegativeInfinity()) {
        return F.CNPiHalf;
      }
      if (z.isDirectedInfinity(F.CI)) {
        return z;
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.sinIntegral();
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

  private static class SinhIntegral extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      IExpr temp = basicRewrite(z);
      if (temp.isPresent()) {
        return temp;
      }
      // if (engine.isDoubleMode()) {
      // try {
      // double zDouble = Double.NaN;
      // try {
      // zDouble = z.evalf();
      // } catch (ValidateException ve) {
      // }
      // if (Double.isNaN(zDouble)) {
      // Complex zc = z.evalfc();
      // return F.complexNum(GammaJS.sinhIntegral(zc));
      //
      // } else {
      // if (F.isZero(zDouble)) {
      // return F.C0;
      // }
      // return F.complexNum(GammaJS.sinhIntegral(new Complex(zDouble)));
      // }
      //
      // } catch (ThrowException te) {
      // LOGGER.debug("SinhIntegral.evaluate() failed", te);
      // return te.getValue();
      // } catch (ValidateException ve) {
      // return Errors.printMessage(ast.topHead(), ve, engine);
      // } catch (RuntimeException rex) {
      // LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      // return F.NIL;
      // }
      // }
      if (z.isTimes() && z.first().isComplex() && z.first().re().isZero()) {
        // I * SinIntegral(-I*arg1)
        return F.Times(S.I, F.SinIntegral(F.Times(F.CNI, z)));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negExpr.isPresent()) {
        return Negate(F.SinhIntegral(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(z);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.SinIntegral(imPart));
      }
      return F.NIL;
    }

    private static IExpr basicRewrite(IExpr z) {
      if (z.isZero()) {
        return F.C0;
      }
      if (z.isInfinity()) {
        return F.CInfinity;
      }
      if (z.isNegativeInfinity()) {
        return F.CNInfinity;
      }
      if (z.isDirectedInfinity(F.CI)) {
        return F.Times(F.CI, F.CPiHalf);
      }
      if (z.isComplexInfinity()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        IExpr temp = basicRewrite(z);
        if (temp.isPresent()) {
          return temp;
        }
        return z.sinhIntegral();
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

  private static class WhittakerM extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr k = ast.arg1();
      IExpr m = ast.arg2();
      IExpr z = ast.arg3();

      if (engine.isDoubleMode()) {
        try {
          double kDouble = Double.NaN;
          double mDouble = Double.NaN;
          double zDouble = Double.NaN;
          try {
            kDouble = k.evalf();
            mDouble = m.evalf();
            zDouble = z.evalf();
            return F.complexNum(HypergeometricJS.whittakerM(new Complex(kDouble),
                new Complex(mDouble), new Complex(zDouble)));
          } catch (ValidateException ve) {
            Errors.printMessage(ast.topHead(), ve, engine);
          }
          Complex kc = k.evalfc();
          Complex mc = m.evalfc();
          Complex zc = z.evalfc();
          return F.complexNum(HypergeometricJS.whittakerM(kc, mc, zc));

        } catch (ThrowException te) {
          LOGGER.debug("WhittakerM.evaluate() failed", te);
          return te.getValue();
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
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
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class WhittakerW extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr k = ast.arg1();
      IExpr m = ast.arg2();
      IExpr z = ast.arg3();
      // TODO
      // if (engine.isDoubleMode()) {
      // try {
      // double kDouble = Double.NaN;
      // double mDouble = Double.NaN;
      // double zDouble = Double.NaN;
      // try {
      // kDouble = k.evalDouble();
      // mDouble = m.evalDouble();
      // zDouble = z.evalDouble();
      // return F.complexNum(HypergeometricJS.whittakerW(new Complex(kDouble), new Complex(mDouble),
      // new Complex(zDouble)));
      // } catch (ValidateException ve) {
      // LOGGER.debug("WhittakerW.evaluate() failed", ve);
      // }
      // Complex kc = k.evalComplex();
      // Complex mc = m.evalComplex();
      // Complex zc = z.evalComplex();
      // return F.complexNum(HypergeometricJS.whittakerW(kc, mc, zc));
      //
      // } catch (ThrowException te) {
      // LOGGER.debug("WhittakerW.evaluate() failed", te);
      // return te.getValue();
      // } catch (ValidateException ve) {
      // LOGGER.debug("WhittakerW.evaluate() failed", ve);
      // } catch (RuntimeException rex) {
      // LOGGER.error("WhittakerW.evaluate() failed", rex);
      // return engine.printMessage(ast.topHead(), rex);
      // }
      // }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
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

  public static void initialize() {
    Initializer.init();
  }

  private HypergeometricFunctions() {}
}
