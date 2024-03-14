package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcCsc;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.CD1;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;
import static org.matheclipse.core.expression.S.Pi;
import java.util.function.DoubleUnaryOperator;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.complex.Complex;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.IReciprocalTrigonometricFunction;
import org.matheclipse.core.eval.interfaces.IRewrite;
import org.matheclipse.core.eval.interfaces.ITrigonometricFunction;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
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
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.PoleError;
import org.matheclipse.core.sympy.exception.ValueError;
import com.google.common.math.DoubleMath;

public class ExpTrigsFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AngleVector.setEvaluator(new AngleVector());
      S.ArcCos.setEvaluator(new ArcCos());
      S.ArcCosh.setEvaluator(new ArcCosh());
      S.ArcCot.setEvaluator(new ArcCot());
      S.ArcCoth.setEvaluator(new ArcCoth());
      S.ArcCsc.setEvaluator(new ArcCsc());
      S.ArcCsch.setEvaluator(new ArcCsch());
      S.ArcSec.setEvaluator(new ArcSec());
      S.ArcSech.setEvaluator(new ArcSech());
      S.ArcSin.setEvaluator(new ArcSin());
      S.ArcSinh.setEvaluator(new ArcSinh());
      S.ArcTan.setEvaluator(new ArcTan());
      S.ArcTanh.setEvaluator(new ArcTanh());
      S.CirclePoints.setEvaluator(new CirclePoints());
      S.Cos.setEvaluator(new Cos());
      S.Cosh.setEvaluator(new Cosh());
      S.Cot.setEvaluator(new Cot());
      S.Coth.setEvaluator(new Coth());
      S.Csc.setEvaluator(new Csc());
      S.Csch.setEvaluator(new Csch());
      S.Exp.setEvaluator(new Exp());
      S.Gudermannian.setEvaluator(new Gudermannian());
      S.Haversine.setEvaluator(new Haversine());
      S.InverseGudermannian.setEvaluator(new InverseGudermannian());
      S.InverseHaversine.setEvaluator(new InverseHaversine());
      S.LambertW.setEvaluator(new LambertW());
      S.Log.setEvaluator(new Log());
      S.LogisticSigmoid.setEvaluator(new LogisticSigmoid());
      S.Log10.setEvaluator(new Log10());
      S.Log2.setEvaluator(new Log2());
      S.Sec.setEvaluator(new Sec());
      S.Sech.setEvaluator(new Sech());
      S.Sin.setEvaluator(new Sin());
      S.Sinc.setEvaluator(new Sinc());
      S.Sinh.setEvaluator(new Sinh());
      S.Tan.setEvaluator(new Tan());
      S.Tanh.setEvaluator(new Tanh());
    }
  }

  private static final class AngleVector extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.first();
      IExpr phi;
      if (ast.isAST2()) {
        IExpr arg2 = ast.second();

        if (arg1.isAST(S.List, 3)) {
          IExpr x = arg1.first();
          IExpr y = arg1.second();
          if (arg2.isAST(S.List, 3)) {
            // AngleVector({x_, y_}, {r_, phi_}) := {x + r * Cos(phi), y + r * Sin(phi)}
            IExpr r = arg2.first();
            phi = arg2.second();
            return F.list(F.Plus(x, F.Times(r, F.Cos(phi))), F.Plus(y, F.Times(r, F.Sin(phi))));
          } else {
            phi = arg2;
          }
          // AngleVector({x_, y_}, phi_) := {x + Cos(phi), y + Sin(phi)}
          return F.list(F.Plus(x, F.Cos(phi)), F.Plus(y, F.Sin(phi)));
        }
        return F.NIL;
      }

      if (arg1.isAST(S.List, 3)) {
        // AngleVector({r_, phi_}) := {r * Cos(phi), r * Sin(phi)}
        IExpr r = ((IAST) arg1).arg1();
        phi = ((IAST) arg1).arg2();
        return F.list(F.Times(r, F.Cos(phi)), F.Times(r, F.Sin(phi)));
      } else {
        phi = arg1;
      }
      // AngleVector(phi_) := {Cos(phi), Sin(phi)}
      return F.list(F.Cos(phi), F.Sin(phi));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      // don't set NUMERICFUNCTION
    }
  }

  /**
   * Arccosine
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" >
   * Inverse_trigonometric functions</a>
   */
  private static final class ArcCos extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.acos(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.acos(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      if (arg1.compareTo(Apfloat.ONE) == 1 || arg1.compareTo(new Apint(-1)) == -1) {
        return F.complexNum(ApcomplexMath.acos(new Apcomplex(arg1)));
      }
      return F.num(ApfloatMath.acos(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.acos());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      // https://github.com/Hipparchus-Math/hipparchus/issues/128
      if (arg1 > 1.0 || arg1 < -1.0) {
        return F.complexNum(Complex.valueOf(arg1).acos());
      }
      return F.num(Math.acos(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.acos(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      // don't check negative argument here - doesn't work with Rubi/MMA pattern matching
      // IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, false);
      // if (negExpr.isPresent()) {
      // return Subtract( Pi , F.ArcCos(negExpr));
      // }

      if (arg1.isCos() && arg1.first().isPositive()) {
        // ArcCos(Cos(z))
        return arcCosCos(arg1);
      }
      if (arg1.isInterval()) {
        return IntervalSym.arccos((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      return ast.argSize() == 1 ? ast.arg1().acos() : F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Pi/2 + I*Log(I*arg1 + Sqrt(1 - arg1^2)) $]
      F.Plus(F.CPiHalf,
          F.Times(F.CI, F.Log(F.Plus(F.Times(F.CI, arg1), F.Sqrt(F.Subtract(F.C1, F.Sqr(arg1))))))); // $$;
    }
  }

  /**
   * Inverse hyperbolic cosine
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcCosh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return FastMath.acosh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.acosh(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      double re = arg1.getReal();
      double im = arg1.getImaginary();
      Complex temp = new Complex(re * re - im * im - 1, 2 * re * im).sqrt();
      return F.complexNum(new Complex(temp.getReal() + re, temp.getImaginary() + im).log());
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.acosh(arg1));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      double val = FastMath.acosh(arg1);
      if (Double.isNaN(val)) {
        return e1ComplexArg(new Complex(arg1));
      }
      return F.num(val);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return FastMath.acosh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInterval()) {
        return IntervalSym.arccosh((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Log(arg1 + Sqrt(-1 + arg1)*Sqrt(1 + arg1)) $]
      F.Log(F.Plus(arg1, F.Times(F.Sqrt(F.Plus(F.CN1, arg1)), F.Sqrt(F.Plus(F.C1, arg1))))); // $$;
    }
  }

  /**
   * Arccotangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" >
   * Inverse_trigonometric functions</a>
   */
  private static final class ArcCot extends AbstractTrigArg1
      implements IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      if (F.isZero(operand)) {
        // Pi / 2
        return Math.PI / 2.0;
      }
      return Math.atan(1 / operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      // I/arg1
      Apcomplex ac = Apcomplex.I.divide(arg1);

      // (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
      Apcomplex result = Apcomplex.I.divide(new Apfloat(2)).multiply(ApcomplexMath
          .log(Apcomplex.ONE.subtract(ac)).subtract(ApcomplexMath.log(Apcomplex.ONE.add(ac))));
      return F.complexNum(result);
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        // Pi / 2
        return F.num(ApfloatMath.pi(arg1.precision()).divide(new Apfloat(2)));
      }
      return F.num(ApfloatMath.atan(ApfloatMath.inverseRoot(arg1, 1)));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      // I/arg1
      Complex c = Complex.I.divide(arg1);

      // (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
      Complex result = Complex.I.divide(new Complex(2.0))
          .multiply(Complex.ONE.subtract(c).log().subtract(Complex.ONE.add(c).log()));
      return F.complexNum(result);
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (F.isZero(arg1)) {
        // Pi / 2
        return F.num(Math.PI / 2.0);
      }
      return F.num(Math.atan(1 / arg1));
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Plus(Negate(ArcCot(negExpr)));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.ArcCoth(imPart));
      }
      if (arg1.isAST(S.Cot, 2) && arg1.first().isPositive()) {
        // ArcCot(Cot(z))
        return arcTanArcCotInverse(arg1);
      }

      if (arg1.isInterval()) {
        return IntervalSym.arccot((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ (1/2)*I*Log(1 - I/arg1) - (1/2)*I*Log(1 + I/arg1) $]
      F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, F.Power(arg1, F.CN1))))),
          F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, F.Power(arg1, F.CN1)))))); // $$;
    }
  }

  /**
   * Arccotangent hyperbolic
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcCoth extends AbstractTrigArg1
      implements IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      if (F.isZero(operand)) {
        return Double.NaN;
      }
      double c = 1.0 / operand;
      return (Math.log(1.0 + c) - Math.log(1.0 - c)) / 2.0;
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      // 1/arg1
      Apcomplex c = h.inverseRoot(arg1, 1);

      // (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
      Apcomplex result =
          h.divide(h.subtract(h.log(Apcomplex.ONE.add(c)), h.log(Apcomplex.ONE.subtract(c))),
              new Apfloat(2));
      return F.complexNum(result);
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      if (arg1.equals(Apcomplex.ZERO)) {
        // I*Pi / 2
        return F.complexNum(h.divide(new Apcomplex(Apcomplex.ZERO, h.pi()), new Apfloat(2)));
      }
      return F.num(h.atanh(h.inverseRoot(arg1, 1)));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      // 1/arg1
      Complex c = arg1.reciprocal();

      // (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
      Complex result = new Complex(0.5)
          .multiply(Complex.ONE.add(c).log().subtract(Complex.ONE.subtract(c).log()));
      return F.complexNum(result);
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (F.isZero(arg1)) {
        // I*Pi / 2
        return F.complexNum(new Complex(0.0, Math.PI).divide(new Complex(2.0)));
      }
      if (F.isZero(arg1)) {
        return e1ComplexArg(new Complex(arg1));
      }
      double c = 1.0 / arg1;
      double val = (Math.log(1.0 + c) - Math.log(1.0 - c)) / 2.0;
      if (Double.isNaN(val)) {
        return e1ComplexArg(new Complex(arg1));
      }
      return F.num(val);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcCoth(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.ArcCot(imPart));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ (-(1/2))*Log(1 - 1/arg1) + (1/2)*Log(1 + 1/arg1) $]
      F.Plus(F.Times(F.CN1D2, F.Log(F.Subtract(F.C1, F.Power(arg1, F.CN1)))),
          F.Times(F.C1D2, F.Log(F.Plus(F.C1, F.Power(arg1, F.CN1))))); // $$;
    }
  }

  /**
   * Inverse hyperbolic tangent
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Inverse_trigonometric_functions">Inverse
   * trigonometric functions</a>
   */
  private static final class ArcCsc extends AbstractTrigArg1 implements IRewrite {
    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      if (arg1.equals(Complex.ZERO)) {
        return F.CComplexInfinity;
      }
      return F.complexNum(Complex.ONE.divide(arg1).asin());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (F.isZero(arg1)) {
        return F.CComplexInfinity;
      }
      return F.num(Math.asin(1 / arg1));
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.ArcCsch(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcCsc(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CNI, F.ArcCsch(imPart));
      // }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ (-I)*Log(Sqrt(1 - 1/arg1^2) + I/arg1) $]
      F.Times(F.CNI, F.Log(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(arg1, F.CN2))),
          F.Times(F.CI, F.Power(arg1, F.CN1))))); // $$;
    }
  }

  /**
   * Inverse hyperbolic tangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcCsch extends AbstractTrigArg1 implements IRewrite {

    @Override
    public IExpr e1DblArg(final double d) {
      // log(1+Sqrt(1+d^2) / d)
      return F.num(Math.log((1 + Math.sqrt(1 + d * d)) / d));
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.ArcCsc(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcCsch(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CNI, F.ArcCsc(imPart));
      // }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Log(Sqrt(1 + 1/arg1^2) + 1/arg1) $]
      F.Log(F.Plus(F.Sqrt(F.Plus(F.C1, F.Power(arg1, F.CN2))), F.Power(arg1, F.CN1))); // $$;
    }
  }

  /**
   * Inverse hyperbolic secant
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcSech extends AbstractTrigArg1 implements IRewrite {

    @Override
    public IExpr e1DblArg(final double d) {
      // log(1+Sqrt(1-d^2) / d)
      if (F.isZero(d)) {
        return S.Indeterminate;
      }
      return F.num(Math.log((1 + Math.sqrt(1 - d * d)) / d));
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Log(Sqrt(-1 + 1/arg1)*Sqrt(1 + 1/arg1) + 1/arg1) $]
      F.Log(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, F.Power(arg1, F.CN1))),
          F.Sqrt(F.Plus(F.C1, F.Power(arg1, F.CN1)))), F.Power(arg1, F.CN1))); // $$;
    }
  }

  private static final class ArcSec extends AbstractTrigArg1 implements IRewrite {
    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      if (arg1.equals(Complex.ZERO)) {
        return F.CComplexInfinity;
      }
      return F.complexNum(Complex.ONE.divide(arg1).acos());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (F.isZero(arg1)) {
        return F.CComplexInfinity;
      }
      return F.num(Math.acos(1 / arg1));
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Pi/2 + I*Log(Sqrt(1 - 1/arg1^2) + I/arg1) $]
      F.Plus(F.CPiHalf, F.Times(F.CI, F.Log(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(arg1, F.CN2))),
          F.Times(F.CI, F.Power(arg1, F.CN1)))))); // $$;
    }
  }

  /**
   * Arcsine
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" >
   * Inverse_trigonometric functions</a>
   */
  private static final class ArcSin extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.asin(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.asin(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      if (arg1.compareTo(Apfloat.ONE) == 1 || arg1.compareTo(new Apint(-1)) == -1) {
        return F.complexNum(ApcomplexMath.asin(new Apcomplex(arg1)));
      }
      return F.num(ApfloatMath.asin(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.asin());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      // https://github.com/Hipparchus-Math/hipparchus/issues/128
      if (arg1 > 1.0) {
        return F.complexNum(Complex.valueOf(arg1, -0.0).asin());
      } else if (arg1 < -1.0) {
        return F.complexNum(Complex.valueOf(arg1, 0.0).asin());
      }
      return F.num(Math.asin(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.asin(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.ArcSinh(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcSin(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.ArcSinh(imPart));
      // }
      if (arg1.isSin() && arg1.first().isPositive()) {
        // ArcSin(Sin(z))
        return arcSinSin(arg1);
      }

      if (arg1.isInterval()) {
        return IntervalSym.arcsin((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      return ast.argSize() == 1 ? ast.arg1().asin() : F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ -I*Log(I*arg1+Sqrt(1-arg1^2)) $]
      F.Times(F.CNI, F.Log(F.Plus(F.Times(F.CI, arg1), F.Sqrt(F.Subtract(F.C1, F.Sqr(arg1)))))); // $$;
    }
  }

  /**
   * Arcsin hyperbolic
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcSinh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return FastMath.asinh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.asinh(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.asinh(arg1));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      double val = FastMath.asinh(arg1);
      return F.num(val);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return FastMath.asinh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInterval()) {
        return IntervalSym.arcsinh((IAST) arg1);
      }
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.ArcSin(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcSinh(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.ArcSin(imPart));
      // }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ Log(arg1 + Sqrt(1 + arg1^2)) $]
      F.Log(F.Plus(arg1, F.Sqrt(F.Plus(F.C1, F.Sqr(arg1))))); // $$;
    }

    @Override
    public IExpr evalAsLeadingTerm(IAST ast, ISymbol x, IExpr logx, int cdir) {
      IExpr arg = ast.arg1();
      EvalEngine engine = EvalEngine.get();
      IExpr x0 = engine.evaluate(arg.subs(x, F.C0)).cancel();
      if (x0.isZero()) {
        return arg.asLeadingTerm(x);
      }
      // Handling branch points (-I*oo, -I) U (I, I*oo)
      if (x0.isImaginaryUnit() || x0.isNegativeImaginaryUnit() || x0.isComplexInfinity()) {
        return rewriteLog(arg, engine).eval(engine).evalAsLeadingTerm(x, logx, cdir);
      }
      if (cdir != 0) {
        // cdir = arg.dir(x, cdir)
      }
      if (cdir > 0 && x0.re().isZero() && x0.im().less(F.CN1).isTrue()) {//
        // S.Less.ofQ(engine, x0.im(), F.CN1)) {
        return F.Subtract(F.ArcSinh(x0).negate(), F.Times(F.CI, S.Pi));
      }
      if (cdir < 0 && x0.re().isZero() && x0.im().greater(F.C1).isTrue()) {
        // S.Greater.ofQ(engine, x0.im(), F.C1)) {
        return F.Plus(F.ArcSinh(x0).negate(), F.Times(F.CI, S.Pi));
      }
      return F.ArcSinh(x0);
    }
  }

  /**
   * Arctangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions" >
   * Inverse_trigonometric functions</a>
   */
  private static final class ArcTan extends AbstractArg12 implements INumeric, IRewrite {

    @Override
    public IExpr e1ObjArg(final IExpr arg1) {
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.ArcTanh(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcTan(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.ArcTanh(imPart));
      // }
      if (arg1.isTan() && arg1.first().isPositive()) {
        // ArcTan(Tan(z))
        return arcTanArcCotInverse(arg1);
      }

      if (arg1.isInterval()) {
        return IntervalSym.arctan((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ObjArg(IExpr x, IExpr y) {
      if (x.isZero() && y.isRealResult()) {
        if (y.isZero()) {
          return S.Indeterminate;
        }
        if (y.isPositiveResult()) {
          return F.CPiHalf;
        }
        if (y.isNegativeResult()) {
          return F.CNPiHalf;
        }
        return F.NIL;
      }
      if (y.isZero() && x.isNumericFunction(true) && !x.isZero()) {
        return F.Times(F.Subtract(F.C1, x.unitStep()), S.Pi);
      }
      IExpr yUnitStep = y.unitStep();
      if (x.isNumericFunction(true) && yUnitStep.isInteger()) {
        if (x.re().isNegative()) {
          return F.Plus(F.ArcTan(F.Divide(y, x)),
              F.Times(F.Subtract(F.Times(F.C2, yUnitStep), F.C1), S.Pi));
        }
        IExpr argX = x.complexArg();
        // -Pi/2 < Arg(x) <= Pi/2
        if (argX.isPresent()
            && F.evalTrue(F.And(F.Less(F.CNPiHalf, argX), F.LessEqual(argX, F.CPiHalf)))) {
          return F.ArcTan(F.Divide(y, x));
        }
      }
      if (x.isInfinity()) {
        return F.C0;
      }
      if (x.isNegativeInfinity()) {
        return F.Times(F.Subtract(F.Times(F.C2, F.UnitStep(F.Re(y))), F.C1), S.Pi);
      }
      EvalEngine engine = EvalEngine.get();
      if (engine.isNumericMode()) {
        if (engine.isArbitraryMode()) {
          x = engine.evalN(x);
          y = engine.evalN(y);

          if (x.isReal() && y.isReal()) {
            // long precision = engine.getNumericPrecision();
            Apfloat xa = ((IReal) x).apfloatValue();
            Apfloat ya = ((IReal) y).apfloatValue();
            return F.num(engine.apfloatHelper().atan2(ya, xa));
          }
          if (x.isNumber() && y.isNumber()) {
            // long precision = engine.getNumericPrecision();
            x = ((INumber) x).apcomplexNumValue();
            y = ((INumber) y).apcomplexNumValue();
            return y.atan2(x);
          }
          return F.NIL;
        }
        double xd = Double.NaN;
        double yd = Double.NaN;
        try {
          xd = x.evalf();
          yd = y.evalf();
        } catch (ValidateException ve) {
        }
        if (Double.isNaN(xd) || Double.isNaN(yd)) {
          Complex xc = x.evalfc();
          Complex yc = y.evalfc();
          // the first argument of atan2 is y and the second is x.
          return F.complexNum(yc.atan2(xc));
        } else {
          // the first argument of atan2 is y and the second is x.
          return F.num(Math.atan2(yd, xd));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr e1DblArg(INum arg1) {
      return F.num(Math.atan(arg1.getRealPart()));
    }

    @Override
    public IExpr e1DblComArg(final IComplexNum val) {
      if (val instanceof ApcomplexNum) {
        return F.complexNum(ApcomplexMath.atan(((ApcomplexNum) val).apcomplexValue()));
      }
      ComplexNum z = (ComplexNum) val;
      if (z.isNaN()) {
        return S.Indeterminate;
      }

      IComplexNum temp = ComplexNum.I.add(z).divide(ComplexNum.I.subtract(z));
      IComplexNum logValue = log(temp.complexNumValue());
      return ComplexNum.I.multiply(logValue).divide(ComplexNum.valueOf(2.0, 0.0)).complexNumValue();
    }

    @Override
    public IExpr e2DblArg(final INum x, final INum y) {
      // the first argument of atan2 is y and the second is x.
      return F.num(Math.atan2(y.getRealPart(), x.getRealPart()));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.atan(arg1));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.atan(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1 && size != 2) {
        throw new UnsupportedOperationException();
      }
      if (size == 2) {
        // the first argument of atan2 is y and the second is x.
        return Math.atan2(stack[top], stack[top - 1]);
      }

      return Math.atan(stack[top]);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ (1/2)*I*Log(1 - I*arg1) - (1/2)*I*Log(1 + I*arg1) $]
      F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, arg1)))),
          F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, arg1))))); // $$;
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return
      // [$ (-I)*Log((arg1 + I*arg2)/Sqrt(arg1^2 + arg2^2)) $]
      F.Times(F.CNI, F.Log(F.Times(F.Plus(arg1, F.Times(F.CI, arg2)),
          F.Power(F.Plus(F.Sqr(arg1), F.Sqr(arg2)), F.CN1D2)))); // $$;
    }
  }

  /**
   * Inverse hyperbolic tangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function">Inverse hyperbolic
   * functions</a>
   */
  private static final class ArcTanh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return FastMath.atanh(operand);
    }

    @Override
    public IExpr e1ComplexArg(final Complex z) {
      // 1/2 * Ln( (1+z) / (1-z) )
      Complex temp = Complex.ONE.add(z).divide(Complex.ONE.subtract(z)).log();
      return F.complexNum(new Complex(temp.getReal() / 2, temp.getImaginary() / 2));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.atanh(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      try {
        return F.num(ApfloatMath.atanh(arg1));
      } catch (ArithmeticException ae) {
        // Logarithm of negative number; result would be complex
        return F.complexNum(ApcomplexMath.atanh(arg1));
      }
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      double val = FastMath.atanh(arg1);
      if (Double.isNaN(val)) {
        return e1ComplexArg(new Complex(arg1));
      }
      return F.num(val);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return FastMath.atanh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isInterval()) {
        return IntervalSym.arctanh((IAST) arg1);
      }
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.ArcTan(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(ArcTanh(negExpr));
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.ArcTan(imPart));
      // }
      return F.NIL;
    }


    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteLog(IExpr arg1, EvalEngine engine) {
      return
      // [$ (-(1/2))*Log(1 - arg1) + (1/2)*Log(1 + arg1) $]
      F.Plus(F.Times(F.CN1D2, F.Log(F.Subtract(F.C1, arg1))),
          F.Times(F.C1D2, F.Log(F.Plus(F.C1, arg1)))); // $$;
    }
  }

  private static class CirclePoints extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      if (arg1.isReal()) {
        if (arg1.isNegative()) {
          // Argument `1` should be a real non-negative number.
          return Errors.printMessage(ast.topHead(), "noneg", F.list(arg1), engine);
        }
        int i = arg1.toIntDefault();
        if (i > 0) {
          // Pi/i - Pi/2
          final IExpr start =
              engine.evaluate(F.Plus(F.Times(F.QQ(1, i), S.Pi), F.Times(F.CN1D2, S.Pi)));
          // (2/i)*Pi
          final IExpr angle = engine.evaluate(F.Times(F.QQ(2, i), S.Pi));
          return F.mapRange(0, i, j -> F.AngleVector(F.Plus(start, F.ZZ(j).times(angle))));
        } else if (i == 0) {
          return F.CEmptyList;
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

  /**
   * Cosine function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   * and <a href="http://en.wikipedia.org/wiki/Exact_trigonometric_constants"> Wikipedia - Exact
   * trigonometric constants</a>
   */
  private static final class Cos extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, ITrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, F.C2Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return Math.cos(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.cos(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.cos(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.cos());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.cos(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.cos(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0) || F.C2.isLE(t)) {
              // Cos(arg1-2*Pi*Floor(1/2*t))
              return F.Cos(F.Plus(arg1, F.Times(F.CN2, S.Pi, F.Floor(F.Times(F.C1D2, t)))));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              IExpr temp = engine.evaluate(arg1.subtract(t.times(S.Pi)));
              IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(temp);
              if (negExpr.isPresent()) {
                // Sin(1/2*Pi - arg1)
                return F.Sin(F.Subtract(F.Times(F.C1D2, S.Pi), arg1));
              }
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -Sin(arg1-1/2*Pi)
              return F.Negate(F.Sin(F.Plus(arg1, F.Times(F.CN1D2, S.Pi))));
            } else if (t.isLT(F.C3D2)) { // t < 3/2
              // -Cos(arg1-Pi)
              return F.Negate(F.Cos(F.Subtract(arg1, S.Pi)));
            } else {
              // Sin(arg1-3/2*Pi)
              return F.Sin(F.Plus(arg1, F.Times(F.CN3D2, S.Pi)));
            }
          } else if (k.isIntegerResult()) {
            // (-1)^k * Cos( arg1 - k*Pi )
            return F.Times(F.Power(F.CN1, k), F.Cos(F.Subtract(arg1, F.Times(k, S.Pi))));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.cos((IAST) arg1);
      }
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Cosh(imPart);
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Cos(negExpr);
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.isLT(F.C1)) { // t < 1
            // -Cos((1-t)*Pi)
            return F.Negate(F.Cos(F.Times(F.Subtract(F.C1, t), S.Pi)));
          } else if (t.isLT(F.C3D2)) { // t < 3/2
            // -Cos((t-1)*Pi)
            return F.Negate(F.Cos(F.Times(F.Subtract(t, F.C1), S.Pi)));
          } else if (t.isLT(F.C2)) { // t < 2
            // Cos((2-t)*Pi)
            return F.Cos(F.Times(F.Subtract(F.C2, t), S.Pi));
          }
          // Cos((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Cos(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }

        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          // 1/2 * t
          IExpr temp = F.timesDistribute(F.C1D2, t, engine);
          if (temp.isIntegerResult()) {
            return F.C1;
          }
          // 1/2 * t - 1/2
          temp = engine.evaluate(F.Subtract(temp, F.C1D2));
          if (temp.isIntegerResult()) {
            return F.CN1;
          }

          if (t.isIntegerResult()) {
            return F.Power(F.CN1, t);
          }

          // t - 1/2
          temp = engine.evaluate(F.Subtract(t, F.C1D2));
          if (temp.isIntegerResult()) {
            return F.C0;
          }
        }
      }

      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Cosh(imPart);
      // }

      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      return ast.argSize() == 1 ? ast.arg1().cos() : F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      // if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
      // arg1 = arg1.rewrite(ID.Exp);
      // }
      return
      // [$ Exp(arg1*I)/ 2 + Exp(-arg1*I)/ 2 $]
      F.Plus(F.Times(F.C1D2, F.Exp(F.Times(arg1, F.CI))),
          F.Times(F.C1D2, F.Exp(F.Times(F.CN1, arg1, F.CI)))); // $$;
    }
  }

  /**
   * Hyperbolic cotangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  private static final class Coth extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.cosh(operand) / Math.sinh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.cosh(arg1).divide(ApcomplexMath.sinh(arg1)));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.cosh(arg1).divide(ApfloatMath.sinh(arg1)));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.cosh().divide(arg1.sinh()));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.cosh(arg1) / Math.sinh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.cosh(stack[top]) / Math.sinh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Coth(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.Cot(imPart));
      }
      if (arg1.isZero()) {
        return F.CComplexInfinity;
      }

      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Coth(arg1-I*Pi*IntegerPart(t) + I*Pi )
              return F
                  .Coth(F.Plus(arg1, F.Times(F.CNI, S.Pi, t.integerPart()), F.Times(F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // Tanh(arg1-1/2*I*Pi)
              return F.Tanh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi)));
            }
            // Coth( arg1 - I*Pi*IntegerPart(t) )
            return F.Coth(F.Plus(arg1, F.Times(F.CNI, S.Pi, t.integerPart())));
          } else if (k.isIntegerResult()) {
            // Coth( arg1 - list.arg2() )
            return F.Coth(F.Subtract(arg1, list.arg2()));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.coth((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg, EvalEngine engine) {
      IAST negexp = F.Exp(F.Times(F.CN1, arg));
      IAST posexp = F.Exp(arg);
      return
      // [$ (posexp + negexp)/(posexp - negexp) $]
      F.Times(F.Plus(posexp, negexp), F.Power(F.Plus(F.Negate(negexp), posexp), F.CN1)); // $$;
    }
  }

  /**
   * Cosecant function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   */
  private static final class Csc extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, IReciprocalTrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, F.C2Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return 1.0D / Math.sin(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.sin(arg1), 1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.inverseRoot(ApfloatMath.sin(arg1), 1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.sin().reciprocal());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(1.0D / Math.sin(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return 1.0D / Math.sin(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Csc(arg1 - 2*Pi*IntegerPart(1/2*t) + 2*Pi)
              return F
                  .Csc(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t))), F.C2Pi));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // Sec(arg1-1/2*Pi)
              return F.Sec(F.Plus(arg1, F.Times(F.CN1D2, S.Pi)));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Csc(arg1-Pi)
              return F.Negate(F.Csc(F.Plus(arg1, F.CNPi)));
            }
            // Csc(arg1 - 2*Pi*IntegerPart(1/2*t) )
            return F.Csc(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Csc( arg1 - k*Pi )
            return F.Times(F.Power(F.CN1, k), F.Csc(F.Subtract(arg1, F.Times(k, S.Pi))));
          }
        }
      }

      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.Csch(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Csc(negExpr));
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.isLT(F.C1)) { // t < 1
            // Csc((1-t)*Pi)
            return F.Csc(F.Times(F.Subtract(F.C1, t), S.Pi));
          } else if (t.isLT(F.C2)) { // t < 2
            // -Csc((2-t)*Pi)
            return F.Negate(F.Csc(F.Times(F.Subtract(F.C2, t), S.Pi)));
          }
          // Csc((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Csc(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }

        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          if (t.isIntegerResult()) {
            return F.CComplexInfinity;
          }

          // 1/2 * t
          IExpr temp1 = F.timesDistribute(F.C1D2, t, engine);
          // 1/2 * t - 1/4
          IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D4));
          if (temp2.isIntegerResult()) {
            return F.C1;
          }
          // 1/2 * t + 1/4
          temp2 = engine.evaluate(F.Plus(temp1, F.C1D4));
          if (temp2.isIntegerResult()) {
            return F.CN1;
          }
          // t - 1/2
          temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
          if (temp2.isIntegerResult()) {
            // I^(-1+2*t)
            return F.Power(F.CI, F.Plus(F.CN1, F.Times(F.C2, t)));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.csc((IAST) arg1);
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CNI, F.Csch(imPart));
      // }

      return F.NIL;
    }


    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr z = ast.arg1();
        if (z.isZero()) {
          return F.CComplexInfinity;
        }
        return z.sin().reciprocal();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ -((2*I)/(E^((-I)*arg1) - E^(I*arg1))) $]
      F.Times(F.CN1, F.C2, F.CI,
          F.Power(F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
    }
  }

  /**
   * Hyperbolic cosine
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  private static final class Cosh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.cosh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.cosh(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.cosh(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.cosh());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.cosh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.cosh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Cosh(negExpr);
      }
      if (arg1.isZero()) {
        return F.C1;
      }
      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Cosh(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
              return F
                  .Cosh(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
                      F.Times(F.C2, F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // I * Sinh(arg1-1/2*I*Pi)
              return F.Times(F.CI, F.Sinh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi))));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Cosh(arg1 - I*Pi)
              return F.Negate(F.Cosh(F.Subtract(arg1, F.Times(F.CI, S.Pi))));
            }
            // Cosh(arg1-2*I*Pi*IntegerPart(1/2*t) )
            return F
                .Cosh(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Cosh( arg1 - list.arg2() )
            return F.Times(F.Power(F.CN1, k), F.Cosh(F.Subtract(arg1, list.arg2())));
          }
        }
      }

      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Cos(imPart);
      }
      if (arg1.isInterval()) {
        return IntervalSym.cosh((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ((IInexactNumber) ast.arg1()).cosh();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ 1/(E^arg1*2) + E^arg1/2 $]
      F.Plus(F.Power(F.Times(F.Exp(arg1), F.C2), F.CN1), F.Times(F.C1D2, F.Exp(arg1))); // $$;
    }
  }

  /**
   * Cotangent function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   */
  private static final class Cot extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, ITrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, S.Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return 1.0D / Math.tan(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      Apcomplex denominator = ApcomplexMath.sin(arg1);
      if (denominator.equals(Apcomplex.ZERO)) {
        return F.CComplexInfinity;
      }
      return F.complexNum(ApcomplexMath.cos(arg1).divide(denominator));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      Apfloat denominator = ApfloatMath.sin(arg1);
      if (denominator.equals(Apcomplex.ZERO)) {
        return F.CComplexInfinity;
      }
      return F.num(ApfloatMath.cos(arg1).divide(denominator));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      Complex denominator = arg1.sin();
      if (denominator.equals(Complex.ZERO)) {
        return F.CComplexInfinity;
      }
      return F.complexNum(arg1.cos().divide(denominator));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      double denom = Math.sin(arg1);
      if (F.isZero(denom)) {
        return F.CComplexInfinity;
      }
      return F.num(Math.cos(arg1) / denom);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return 1.0D / Math.tan(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) { // t < 0
              // Cot(arg1 + Pi - Pi*IntegerPart(t))
              return F.Cot(F.Plus(arg1, S.Pi, F.Times(F.CN1, S.Pi, t.integerPart())));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -Tan(arg1-1/2*Pi)
              return F.Negate(F.Tan(F.Plus(arg1, F.Times(F.CN1D2, S.Pi))));
            } else {
              // Cot(arg1 - Pi*IntegerPart(t))
              return F.Cot(F.Plus(arg1, F.Times(F.CN1, S.Pi, t.integerPart())));
            }
          } else if (k.isIntegerResult()) {
            // Cot( arg1 - k*Pi )
            return F.Cot(F.Subtract(arg1, F.Times(k, S.Pi)));
          }
        }
      }

      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return Times(CNI, Coth(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Cot(negExpr));
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.isLT(F.C1)) { // t < 1
            // -Cot((1-t)*Pi)
            return F.Negate(F.Cot(F.Times(F.Subtract(F.C1, t), S.Pi)));
          } else if (t.isLT(F.C2)) { // t < 2
            // Cot((t-1)*Pi)
            return F.Cot(F.Times(F.Subtract(t, F.C1), S.Pi));
          }
          // Cot((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Cot(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }
        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          if (t.isIntegerResult()) {
            return F.CComplexInfinity;
          }
          // t - 1/2
          IExpr temp = engine.evaluate(F.Plus(t, F.CN1D2));
          if (temp.isIntegerResult()) {
            return F.C0;
          }
        }
      }

      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return Times(CNI, Coth(imPart));
      // }

      if (arg1.isInterval()) {
        return IntervalSym.cot((IAST) arg1);
      }

      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ -((I*(E^((-I)*arg1) + E^(I*arg1)))/(E^((-I)*arg1) - E^(I*arg1))) $]
      F.Times(F.CN1, F.CI, F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))),
          F.Power(F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
    }
  }

  /**
   * Hyperbolic Cosecant function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
   */
  private static final class Csch extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return 1.0D / Math.sinh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.sinh(arg1), 1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.inverseRoot(ApfloatMath.sinh(arg1), 1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.sinh().reciprocal());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(1.0D / Math.sinh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return 1.0D / Math.sinh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Csch(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CNI, F.Csc(imPart));
      }
      if (arg1.isZero()) {
        return F.CComplexInfinity;
      }
      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Csch(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
              return F
                  .Csch(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
                      F.Times(F.C2, F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -I * Sech(arg1-1/2*I*Pi)
              return F.Times(F.CNI, F.Sech(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi))));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Csch(arg1 - I*Pi)
              return F.Negate(F.Csch(F.Subtract(arg1, F.Times(F.CI, S.Pi))));
            }
            // Csch(arg1-2*I*Pi*IntegerPart(1/2*t) )
            return F
                .Csch(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Csch( arg1 - list.arg2() )
            return F.Times(F.Power(F.CN1, k), F.Csch(F.Subtract(arg1, list.arg2())));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.csch((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ 2/(-E^(-arg1) + E^arg1) $]
      F.Times(F.C2, F.Power(F.Plus(F.Negate(F.Exp(F.Negate(arg1))), F.Exp(arg1)), F.CN1)); // $$;
    }
  }

  private static final class Exp extends AbstractArg1 implements INumeric {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      return Power(S.E, o);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.exp(stack[top]);
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class Gudermannian extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      return F.NIL;
    }


    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {

      if (ast.argSize() == 1) {
        try {
          IInexactNumber z = (IInexactNumber) ast.arg1();
          if (z.isNumber()) {
            // see https://github.com/paulmasson/math/issues/24

            // Re(z)>0 || (Re(z)==0&&Im(z)>=0)
            if (z.complexSign() >= 0) {
              // (1/2)*(Pi - 4*ArcCot(E^z))
              return engine.evaluate(
                  F.Times(F.C1D2, F.Subtract(S.Pi, F.Times(F.C4, F.ArcCot(F.Power(S.E, z))))));
            }

            // 2*ArcTan( Tanh( z/2 )
            return z.times(F.C1D2).tanh().atan().times(F.C2);
          }
        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          return Errors.printMessage(ast.topHead(), rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class Haversine extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr z = ast.arg1();
      if (z.isNumericFunction()) {
        IExpr cos = S.Cos.ofNIL(engine, z);
        if (cos.isPresent()) {
          // 1/2 * (1-Cos(x))
          return F.C1D2.times(F.C1.subtract(cos));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      // 1/2 * (1-Cos(x))
      return ast.argSize() == 1 ? F.C1.subtract(ast.arg1().cos()).multiply(F.C1D2) : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class InverseGudermannian extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {

      if (ast.argSize() == 1) {
        try {
          IInexactNumber z = (IInexactNumber) ast.arg1();
          // see https://github.com/paulmasson/math/issues/24

          // 2*ArcTanh( Tan( z/2 )
          return z.times(F.C1D2).tan().atanh().times(F.C2);

        } catch (ValidateException ve) {
          return Errors.printMessage(ast.topHead(), ve, engine);
        } catch (RuntimeException rex) {
          return Errors.printMessage(ast.topHead(), rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class InverseHaversine extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr z = ast.arg1();
      if (z.isNumericFunction()) {
        IExpr arcSin = S.ArcSin.ofNIL(engine, F.Sqrt(z));
        if (arcSin.isPresent()) {
          // 2*ArcSin(Sqrt(z))
          return F.C2.times(arcSin);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      // 2*ArcSin(Sqrt(z))
      return ast.argSize() == 1 ? ast.arg1().sqrt().asin().multiply(2) : F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static final class LambertW extends AbstractArg1 {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      return F.ProductLog(o);
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /** See <a href="http://en.wikipedia.org/wiki/Logarithm">Wikipedia - Logarithm</a> */
  private static final class Log extends AbstractArg12 implements INumeric, IRewrite {

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.log(arg1));
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.log(arg1));
    }

    @Override
    public IExpr e1DblArg(final INum arg1) {
      return Num.valueOf(Math.log(arg1.getRealPart()));
    }

    @Override
    public IExpr e1FraArg(final IFraction f) {
      if (f.isPositive() && f.isLT(F.C1)) {
        return F.Negate(F.Log(f.inverse()));
      }
      return F.NIL;
    }

    /**
     * Use swapped args in call to {@link FixedPrecisionApfloatHelper#log(Apfloat, Apfloat)}
     */
    @Override
    public IExpr e2ApfloatArg(final ApfloatNum base, final ApfloatNum z) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.complexNum(h.log(z.apfloatValue(), base.apfloatValue()));
    }

    /**
     * Use swapped args in call to {@link FixedPrecisionApfloatHelper#log(Apcomplex, Apcomplex)}
     */
    @Override
    public IExpr e2ApcomplexArg(final ApcomplexNum base, final ApcomplexNum z) {
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
      return F.complexNum(h.log(z.apcomplexValue(), base.apcomplexValue()));
    }

    @Override
    public IExpr e1DblComArg(final IComplexNum arg1) {
      return log(arg1);
    }

    @Override
    public IExpr e2DblArg(final INum arg1, final INum arg2) {
      return Num.valueOf(Math.log(arg2.getRealPart()) / Math.log(arg1.getRealPart()));
    }

    @Override
    public IExpr e2IntArg(final IInteger arg1, final IInteger arg2) {
      return baseBLog(arg1, arg2);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.log(stack[top]);
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IInexactNumber num = (IInexactNumber) ast.arg1();
        if (num.isZero()) {
          return F.Indeterminate;
        }
        if (num.isNegative()) {
          return engine.evaluate(F.Plus(num.negate().log(), F.Times(CI, S.Pi)));
        }
        return num.log();
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr e1ObjArg(IExpr arg1) {
      if (arg1.isPower()) {
        IExpr base = arg1.base();
        IExpr exponent = arg1.exponent();
        if (exponent.isFraction()) {
          IFraction frac = (IFraction) exponent;
          if (frac.numerator().isOne()) {
            IInteger denominator = frac.denominator();
            if (!denominator.isMinusOne() && !denominator.isZero()) {
              // Log(base^(1 / denominator)) /; denominator <> -1 && denominator <> 0
              return F.Divide(F.Log(base), denominator);
            }
          }
        }
        // arg2*Log(arg1)
        IExpr temp = F.eval(Times(exponent, F.Log(base)));
        IExpr imTemp = F.eval(F.Im(temp));
        if (imTemp.isReal()) {
          if (((IReal) imTemp).isGT(F.num(-1 * Math.PI)) && ((IReal) imTemp).isLT(F.num(Math.PI))) {
            // Log(arg1 ^ arg2) == arg2*Log(arg1) ||| -Pi <
            // Im(arg2*Log(arg1)) < Pi
            return temp;
          }
        }
        if (AbstractAssumptions.assumePositive(base) && exponent.isRealResult()) {
          // Log(arg1 ^ arg2) == arg2*Log(arg1) ||| arg1 > 0 && arg2 is real
          return temp;
        }
      } else if (arg1.isTimes()) {
        EvalEngine engine = EvalEngine.get();
        IAST timesAST = (IAST) arg1;
        // Log(a*z) == Log(a) + Log(z) /; a > 0
        for (int i = 1; i < timesAST.size(); i++) {
          IExpr a = timesAST.get(i);
          if (a.isRealResult() && a.isPositive()) {// engine.evalTrue(F.Greater(a, F.C0))) {
            IExpr temp = engine.evaluate(F.Log(a));
            if (temp.isFree(S.Log, true)) {
              return F.Plus(temp, F.Log(timesAST.removeAtCopy(i)));
            }
          }
        }
      }
      if (arg1.isNegativeResult()) {
        return F.Plus(F.Log(F.Negate(arg1)), F.Times(CI, S.Pi));
      }
      if (arg1.isInterval()) {
        return IntervalSym.log((IAST) arg1);
      }
      if (arg1.isAST(S.Overflow, 1)) {
        return F.Overflow();
      }
      if (arg1.isAST(S.Underflow, 1)) {
        return F.Underflow();
      }
      if (arg1.isNumericFunction()) {
        // arg1.re()^2 + arg1.im()^2
        EvalEngine engine = EvalEngine.get();
        IExpr temp = engine.evaluate(F.Plus(F.Sqr(arg1.re()), F.Sqr(arg1.im())));
        if (temp.isOne()) {
          // Log(x + I*y) == (1/2)*Log(x^2 + y^2) + I*Arg(x + I y) with Log(1) == 0
          // ==> I*Arg(x + I y)
          return F.Times(F.CI, F.Arg(arg1));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr e2ObjArg(IExpr o0, IExpr o1) {
      if (o0.isZero()) {
        if (o1.isZero()) {
          return S.Indeterminate;
        }
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public IExpr evalAsLeadingTerm(IAST ast, ISymbol x, IExpr logx, int cdir) {
      // Symja Log(b,z) instead of sympy log(z,b) convention is used
      // https://github.com/sympy/sympy/blob/master/sympy/functions/elementary/exponential.py#L1077
      final IExpr arg0;
      if (ast.isAST2()) {
        arg0 = ast.arg2().together();
      } else {
        arg0 = ast.arg1().together();
      }

      // STEP 1
      ISymbol t = F.Dummy("t");
      if (cdir == 0) {
        cdir = 1;
      }
      IExpr z = arg0.subs(x, t.times(F.ZZ(cdir)));

      EvalEngine engine = EvalEngine.get();
      // STEP 2
      IExpr c = F.C1;
      IExpr e = F.C0;
      try {
        IPair leadTerm = z.leadTerm(t, logx, cdir);
        c = leadTerm.first();
        e = leadTerm.second();
      } catch (ValueError ve) {
        IExpr arg = arg0.asLeadingTerm(x, logx, cdir);
        return F.Log(arg);
      }
      if (c.has(t)) {
        if (!e.isZero()) {
          throw new PoleError("Cannot expand " + this + " around 0");
        }
        c = c.subs(x, t.divide(F.ZZ(cdir)));
        return F.Log(c);
      }

      // STEP3
      if (c.isOne() && e.isZero()) {
        return arg0.subtract(F.C1).asLeadingTerm(x, logx, 0);
      }
      // STEP 4
      // res = log(c) - e*log(cdir)
      IASTAppendable res = F.Plus(F.Subtract(F.Log(c), F.Times(e, F.Log(cdir))));

      // logx = log(x) if logx is None else logx
      if (logx.isNIL()) {
        logx = F.Log(x);
      }
      // res += e*logx
      res.append(F.Times(e, logx));

      // STEP 5
      if (c.isNegative() && !z.im().isZero()) {
        int i = 0;
        IExpr term = F.NIL;
        while (i < 5) {
          // term in enumerate(z.lseries(t)):
          // if not term.is_real or i == 5:
          // break
          i++;
        }
        if (i < 5) {
          // coeff, _ = term.as_coeff_exponent(t)
          // res += -2*I*S.Pi*Heaviside(-im(coeff), 0)
          IExpr coeff = term.asCoeffExponent(t).first();
          res.append(F.Times(F.CN2, F.CI, S.Pi, F.heaviside(coeff.im().negate(), F.C0, engine)));
        }
      }
      return res;
    }
  }

  /**
   * Logistic function
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Logistic_function">Wikipedia: Logistic function</a>
   */
  private static final class LogisticSigmoid extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isInfinity()) {
        return F.C1;
      }
      if (z.isNegativeInfinity()) {
        return F.C0;
      }
      if (z.isNumericFunction(true) || z.isInterval()) {
        if (z.isZero()) {
          return F.C1D2;
        }
        if (z.equals(F.Times(F.CI, S.Pi))) {
          return F.NIL;
        }
        IExpr exp = engine.evaluate(F.Power(S.E, F.Times(F.CN1, z)));
        if (exp.isNumber() || exp.isInterval() || engine.isDoubleMode()
            || engine.isArbitraryMode()) {
          // 1 / (1 + Exp(-z))
          return F.Power.of(engine, F.Plus(F.C1, exp), F.CN1);
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
    }
  }

  private static final class Log10 extends AbstractArg1 implements INumeric {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      return F.Log(F.C10, o);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.log10(stack[top]);
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static final class Log2 extends AbstractArg1 implements INumeric {

    @Override
    public IExpr e1ObjArg(final IExpr o) {
      return F.Log(F.C2, o);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.log(stack[top] / 2.0d);
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   * Secant function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   */
  private static final class Sec extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, IReciprocalTrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, F.C2Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return 1.0D / Math.cos(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.cos(arg1), 1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.inverseRoot(ApfloatMath.cos(arg1), 1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.cos().reciprocal());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(1.0D / Math.cos(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return 1.0D / Math.cos(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Sec(arg1 - 2*Pi*IntegerPart(1/2*t) + 2*Pi)
              return F
                  .Sec(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t))), F.C2Pi));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -Csc(arg1-1/2*Pi)
              return F.Negate(F.Csc(F.Plus(arg1, F.Times(F.CN1D2, S.Pi))));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Sec(arg1-Pi)
              return F.Negate(F.Sec(F.Plus(arg1, F.CNPi)));
            }
            // Sec(arg1 - 2*Pi*IntegerPart(1/2*t) )
            return F.Sec(F.Plus(arg1, F.Times(F.CN2Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Sec( arg1 - k*Pi )
            return F.Times(F.Power(F.CN1, k), F.Sec(F.Subtract(arg1, F.Times(k, S.Pi))));
          }
        }
      }

      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Sech(imPart);
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Sec(negExpr);
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.isLT(F.C1)) { // t < 1
            // -Sec((1-t)*Pi)
            return F.Negate(F.Sec(F.Times(F.Subtract(F.C1, t), S.Pi)));
          } else if (t.isLT(F.C2)) { // t < 2
            // Sec((2-t)*Pi)
            return F.Sec(F.Times(F.Subtract(F.C2, t), S.Pi));
          }
          // Sec((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Sec(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }

        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          // 1/2 * t
          IExpr temp1 = F.timesDistribute(F.C1D2, t, engine);
          if (temp1.isIntegerResult()) {
            return F.C1;
          }
          // 1/2 * t - 1/2
          IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D2));
          if (temp2.isIntegerResult()) {
            return F.CN1;
          }
          if (t.isIntegerResult()) {
            return F.Power(F.CN1, t);
          }

          // t - 1/2
          temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
          if (temp2.isIntegerResult()) {
            return F.CComplexInfinity;
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.sec((IAST) arg1);
      }
      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Sech(imPart);
      // }

      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr z = ast.arg1();
        if (z.equals(F.CPiHalf)) {
          return F.CComplexInfinity;
        }
        return z.cos().reciprocal();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ 2/(E^((-I)*arg1) + E^(I*arg1)) $]
      F.Times(F.C2,
          F.Power(F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
    }
  }

  /**
   * Hyperbolic Secant function
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
   */
  private static final class Sech extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return 1.0D / Math.cosh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.inverseRoot(ApcomplexMath.cosh(arg1), 1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.inverseRoot(ApfloatMath.cosh(arg1), 1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.cosh().reciprocal());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(1.0D / Math.cosh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return 1.0D / Math.cosh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Sech(negExpr);
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Sec(imPart);
      }
      if (arg1.isZero()) {
        return F.C0;
      }
      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Sech(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
              return F
                  .Sech(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
                      F.Times(F.C2, F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -I * Csch(arg1-1/2*I*Pi)
              return F.Times(F.CNI, F.Csch(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi))));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Sech(arg1 - I*Pi)
              return F.Negate(F.Sech(F.Subtract(arg1, F.Times(F.CI, S.Pi))));
            }
            // Sech(arg1-2*I*Pi*IntegerPart(1/2*t) )
            return F
                .Sech(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Sech( arg1 - list.arg2() )
            return F.Times(F.Power(F.CN1, k), F.Sech(F.Subtract(arg1, list.arg2())));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.sech((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ 2/(E^(-arg1) + E^arg1) $]
      F.Times(F.C2, F.Power(F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1)), F.CN1)); // $$;
    }
  }

  /**
   * Sine function.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   */
  private static final class Sin extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, ITrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, F.C2Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return Math.sin(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.sin(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.sin(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.sin());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return num(Math.sin(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.sin(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0) || F.C2.isLE(t)) {
              // Sin(arg1-2*Pi*Floor(1/2*t))
              return F.Sin(F.Plus(arg1, F.Times(F.CN2, S.Pi, F.Floor(F.Times(F.C1D2, t)))));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              // (arg1-t*Pi)
              IExpr temp = engine.evaluate(arg1.subtract(t.times(S.Pi)));
              IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(temp);
              if (negExpr.isPresent()) {
                // Cos(1/2*Pi - arg1)
                return F.Cos(F.Subtract(F.Times(F.C1D2, S.Pi), arg1));
              }

            } else if (t.isLT(F.C1)) { // t < 1
              // Cos(arg1-1/2*Pi)
              return F.Cos(F.Plus(arg1, F.Times(F.CN1D2, S.Pi)));
            } else if (t.isLT(F.C3D2)) { // t < 3/2
              // -Sin(arg1-Pi)
              return F.Negate(F.Sin(F.Subtract(arg1, S.Pi)));
            } else { // t < 2
              // -Cos(arg1-3/2*Pi)
              return F.Negate(F.Cos(F.Plus(arg1, F.Times(F.CN3D2, S.Pi))));
            }
          } else if (k.isIntegerResult()) {
            // (-1)^k * Sin( arg1 - k*Pi )
            return F.Times(F.Power(F.CN1, k), F.Sin(F.Subtract(arg1, F.Times(k, S.Pi))));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.sin((IAST) arg1);
      }
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.Sinh(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Sin(negExpr));
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.isLT(F.C1)) { // t < 1
            // Sin((1-t)*Pi)
            return F.Sin(F.Times(F.Subtract(F.C1, t), S.Pi));
          } else if (t.isLT(F.C3D2)) { // t < 3/2
            // -Sin((t-1)*Pi)
            return F.Negate(F.Sin(F.Times(F.Subtract(t, F.C1), S.Pi)));
          } else if (t.isLT(F.C2)) { // t < 2
            // -Sin((2-t)*Pi)
            return F.Negate(F.Sin(F.Times(F.Subtract(F.C2, t), S.Pi)));
          }
          // Sin((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Sin(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }

        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          if (t.isIntegerResult()) {
            return F.C0;
          }

          // 1/2 * t
          IExpr temp1 = F.timesDistribute(F.C1D2, t, engine);
          // 1/2 * t - 1/4
          IExpr temp2 = engine.evaluate(F.Plus(temp1, F.CN1D4));
          if (temp2.isIntegerResult()) {
            return F.C1;
          }
          // 1/2 * t + 1/4
          temp2 = engine.evaluate(F.Plus(temp1, F.C1D4));
          if (temp2.isIntegerResult()) {
            return F.CN1;
          }
          // t - 1/2
          temp2 = engine.evaluate(F.Plus(t, F.CN1D2));
          if (temp2.isIntegerResult()) {
            // I^(-1+2*t)
            return F.Power(F.CI, F.Plus(F.CN1, F.Times(F.C2, t)));
          }
        }
      }

      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.Sinh(imPart));
      // }

      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ((IInexactNumber) ast.arg1()).sin();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      // if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
      // arg1 = arg1.rewrite(ID.Exp);
      // }
      return
      // [$ Exp(arg1*I) / (2*I) - Exp(-arg1*I) / (2*I) $]
      F.Plus(F.Times(F.CN1, F.Power(F.Times(F.C2, F.CI), F.CN1), F.Exp(F.Times(F.CN1, arg1, F.CI))),
          F.Times(F.Power(F.Times(F.C2, F.CI), F.CN1), F.Exp(F.Times(arg1, F.CI)))); // $$;
    }
  }

  /**
   * Sinc function.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Sinc_function">Sinc function</a>
   */
  private static class Sinc extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      if (F.isZero(operand)) {
        return 1.0;
      }
      return new org.hipparchus.analysis.function.Sinc(false).value(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        return F.num(Apcomplex.ONE);
      }
      return F.complexNum(ApcomplexMath.sin(arg1).divide(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      if (arg1.equals(Apcomplex.ZERO)) {
        return F.num(Apcomplex.ONE);
      }
      return F.num(ApfloatMath.sin(arg1).divide(arg1));
    }

    @Override
    public IExpr e1ComplexArg(Complex arg1) {
      if (arg1.equals(Complex.ZERO)) {
        return F.complexNum(Complex.ONE);
      }
      return F.complexNum(arg1.sin().divide(arg1));
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      if (F.isZero(arg1)) {
        return CD1;
      }
      return num(new org.hipparchus.analysis.function.Sinc(false).value(arg1));
      // return num(Math.sin(arg1) / arg1);
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      double a1 = stack[top];
      if (a1 == 0.0) {
        return 1.0;
      }
      return new org.hipparchus.analysis.function.Sinc(false).value(a1);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isZero()) {
        return F.C1;
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Sinc(negExpr);
      }
      IExpr sin = S.Sin.ofNIL(engine, arg1);
      if (sin.isPresent() && !sin.isSin()) {
        return sin.divide(arg1);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  // public static BinaryFunctorImpl<IExpr> integerLogFunction() {
  // return new BinaryFunctorImpl<IExpr>() {
  // /**
  // * Evaluate if Log(arg2)/Log(arg1) has an integer number result
  // */
  // @Override
  // public IExpr apply(IExpr arg1, IExpr arg2) {
  // if (arg1.isInteger() && arg2.isInteger()) {
  // return baseBLog((IInteger) arg2, (IInteger) arg1);
  // }
  // return F.NIL;
  // }
  // };
  // }

  /**
   * Hyperbolic sine
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  private static final class Sinh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.sinh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.sinh(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.sinh(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.sinh());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.sinh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.sinh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Sinh(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.Sin(imPart));
      }
      if (arg1.isZero()) {
        return F.C0;
      }
      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Sinh(arg1-2*I*Pi*IntegerPart(1/2*t) + 2*I*Pi)
              return F
                  .Sinh(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t))),
                      F.Times(F.C2, F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // I * Cosh(arg1-1/2*I*Pi)
              return F.Times(F.CI, F.Cosh(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi))));
            } else if (t.isLT(F.C2)) { // t < 2
              // -Sinh(arg1 - I*Pi)
              return F.Negate(F.Sinh(F.Subtract(arg1, F.Times(F.CI, S.Pi))));
            }
            // Sinh(arg1-2*I*Pi*IntegerPart(1/2*t) )
            return F
                .Sinh(F.Plus(arg1, F.Times(F.CN2, F.CI, S.Pi, F.IntegerPart(F.Times(F.C1D2, t)))));
          } else if (k.isIntegerResult()) {
            // (-1)^k * Sinh( arg1 - list.arg2() )
            return F.Times(F.Power(F.CN1, k), F.Sinh(F.Subtract(arg1, list.arg2())));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.sinh((IAST) arg1);
      }
      return F.NIL;
    }

    // private static IExpr evalInterval(final IExpr arg1) {
    // IExpr l = arg1.lower();
    // IExpr u = arg1.upper();
    // if (l.isReal() || l.isNegativeInfinity()) {
    // l = F.Sinh.of(l);
    // if (l.isReal() || l.isNegativeInfinity()) {
    // if (u.isReal() || u.isInfinity()) {
    // u = F.Sinh.of(u);
    // if (u.isReal() || u.isInfinity()) {
    // return F.Interval(F.List(l, u));
    // }
    // }
    // }
    // }
    // return F.NIL;
    // }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ((IInexactNumber) ast.arg1()).sinh();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ -(1/(E^arg1*2)) + E^arg1/2 $]
      F.Plus(F.Negate(F.Power(F.Times(F.Exp(arg1), F.C2), F.CN1)), F.Times(F.C1D2, F.Exp(arg1))); // $$;
    }
  }

  /**
   * Tan
   * 
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
   */
  private static final class Tan extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator, ITrigonometricFunction {

    @Override
    public IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
      return _period(self, S.Pi, symbol);
    }

    @Override
    public double applyAsDouble(double operand) {
      return Math.tan(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.tan(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.tan(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.tan());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.tan(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.tan(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {

      if (arg1.isPlus()) {
        IExpr k = AbstractFunctionEvaluator.peelOffPlusRational((IAST) arg1, engine);
        if (k != null) {
          // arg1 == x + k*Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) { // t < 0
              // Tan(arg1 + Pi - Pi*IntegerPart(t))
              return F.Tan(F.Plus(arg1, S.Pi, F.Times(F.CNPi, t.integerPart())));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // -Cot(arg1-1/2*Pi)
              return F.Negate(F.Cot(F.Plus(arg1, F.Times(F.CN1D2, S.Pi))));
            }
            // Tan(arg1 - Pi*IntegerPart(t))
            return F.Tan(F.Plus(arg1, F.Times(F.CNPi, t.integerPart())));
          } else if (k.isIntegerResult()) {
            // Tan( arg1 - k*Pi )
            return F.Tan(F.Subtract(arg1, F.Times(k, S.Pi)));
          }
        }
      }

      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1, F.CNI);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.Tanh(imPart));
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Tan(negExpr));
      }

      if (arg1.isTimes()) {
        IAST timesAST = (IAST) arg1;
        if (timesAST.size() == 3 && timesAST.arg1().isRational() && timesAST.arg2().isPi()) {
          // t should be positive here!
          IRational t = (IRational) timesAST.arg1();
          if (t.isLT(F.C1D2)) { // t < 1/2
            return F.NIL;
          } else if (t.equals(F.C1D2)) { // t == 1/2
            return F.CComplexInfinity;
          } else if (t.isLT(F.C1)) { // t < 1
            // -Tan((1-t)*Pi)
            return F.Negate(F.Tan(F.Times(F.Subtract(F.C1, t), S.Pi)));
          } else if (t.isLT(F.C2)) { // t < 2
            // Tan((t-1)*Pi)
            return F.Tan(F.Times(F.Subtract(t, F.C1), S.Pi));
          }
          // Tan((t-2*Quotient(IntegerPart(t),2))*Pi)
          return F.Tan(F.Times(S.Pi, F.Plus(t, ExpTrigsFunctions.integerPartFolded2(t))));
        }
        IExpr t = AbstractFunctionEvaluator.peelOfTimes(timesAST, Pi);
        if (t.isPresent() && t.im().isZero()) {
          if (t.isIntegerResult()) {
            return F.C0;
          }
          // t - 1/2
          IExpr temp = engine.evaluate(F.Plus(t, F.CN1D2));
          if (temp.isIntegerResult()) {
            return F.CComplexInfinity;
          }
        }
      }

      if (arg1.isInterval()) {
        return IntervalSym.tan((IAST) arg1);
      }

      // IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      // if (imPart.isPresent()) {
      // return F.Times(F.CI, F.Tanh(imPart));
      // }

      return F.NIL;
    }


    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ((IInexactNumber) ast.arg1()).tan();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      // if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
      // arg1 = arg1.rewrite(ID.Exp);
      // }
      return
      // [$ (I*(E^((-I)*arg1) - E^(I*arg1)))/(E^((-I)*arg1) + E^(I*arg1)) $]
      F.Times(F.CI, F.Subtract(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))),
          F.Power(F.Plus(F.Exp(F.Times(F.CNI, arg1)), F.Exp(F.Times(F.CI, arg1))), F.CN1)); // $$;
    }
  }

  /**
   * Hyperbolic tangent
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
   */
  private static final class Tanh extends AbstractTrigArg1
      implements INumeric, IRewrite, DoubleUnaryOperator {

    @Override
    public double applyAsDouble(double operand) {
      return Math.tanh(operand);
    }

    @Override
    public IExpr e1ApcomplexArg(Apcomplex arg1) {
      return F.complexNum(ApcomplexMath.tanh(arg1));
    }

    @Override
    public IExpr e1ApfloatArg(Apfloat arg1) {
      return F.num(ApfloatMath.tanh(arg1));
    }

    @Override
    public IExpr e1ComplexArg(final Complex arg1) {
      return F.complexNum(arg1.tanh());
    }

    @Override
    public IExpr e1DblArg(final double arg1) {
      return F.num(Math.tanh(arg1));
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.tanh(stack[top]);
    }

    @Override
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return Negate(Tanh(negExpr));
      }
      IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
      if (imPart.isPresent()) {
        return F.Times(F.CI, F.Tan(imPart));
      }
      if (arg1.isZero()) {
        return F.C0;
      }

      if (arg1.isPlus()) {
        IAST list = AbstractFunctionEvaluator.peelOffPlusI((IAST) arg1, engine);
        if (list.isPresent()) {
          IExpr k = list.arg1();
          // arg1 == x - k/I * Pi
          if (k.isRational()) {
            IRational t = (IRational) k;
            if (t.isLT(F.C0)) {
              // Tanh(arg1-I*Pi*IntegerPart(t) + I*Pi )
              return F
                  .Tanh(F.Plus(arg1, F.Times(F.CNI, S.Pi, t.integerPart()), F.Times(F.CI, S.Pi)));
            } else if (t.isLT(F.C1D2)) { // t < 1/2
              return F.NIL;
            } else if (t.isLT(F.C1)) { // t < 1
              // Coth(arg1-1/2*I*Pi)
              return F.Coth(F.Plus(arg1, F.Times(F.CN1D2, F.CI, S.Pi)));
            }
            // Tanh( arg1 - I*Pi*IntegerPart(t) )
            return F.Tanh(F.Plus(arg1, F.Times(F.CNI, S.Pi, t.integerPart())));
          } else if (k.isIntegerResult()) {
            // Tanh( arg1 - list.arg2() )
            return F.Tanh(F.Subtract(arg1, list.arg2()));
          }
        }
      }
      if (arg1.isInterval()) {
        return IntervalSym.tanh((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 1) {
        return ((IInexactNumber) ast.arg1()).tanh();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }

    @Override
    public IExpr rewriteExp(IExpr arg1, EvalEngine engine) {
      return
      // [$ -(1/(E^arg1*(E^(-arg1) + E^arg1))) + E^arg1/(E^(-arg1) + E^arg1) $]
      F.Plus(
          F.Negate(
              F.Power(F.Times(F.Exp(arg1), F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1))), F.CN1)),
          F.Times(F.Exp(arg1), F.Power(F.Plus(F.Exp(F.Negate(arg1)), F.Exp(arg1)), F.CN1))); // $$;
    }
  }

  /**
   * Try simplifying <code>ArcTan(Tan(z))</code> or <code>ArcCot(Cot(z))</code>
   *
   * @param arg1 is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
   * @return
   */
  private static IExpr arcTanArcCotInverse(final IExpr arg1) {
    IExpr z = arg1.first();
    IExpr zRe = z.re();
    IExpr k = S.Quotient.of(F.Subtract(zRe, F.CPiHalf), S.Pi).inc();
    if (k.isInteger()) {
      // -Pi/2 + k*Pi
      IExpr min = S.Times.of(F.Plus(F.CN1D2, k), Pi);
      // Pi/2 + k*Pi
      IExpr max = S.Times.of(F.Plus(F.C1D2, k), Pi);
      // z - k*Pi
      IAST result = F.Subtract(z, F.Times(k, S.Pi));
      // (-(Pi/2) + k*Pi < Re(z) < Pi/2 + k*Pi
      if (S.Less.ofQ(min, zRe) && max.greater(zRe).isTrue()) { // S.Greater.ofQ(max, zRe)) {
        // z - k*Pi
        return result;
      }

      IExpr zIm = z.im();
      // (Re(z) == -(Pi/2) + k*Pi && Im(z) < 0)
      if (zIm.isNegativeResult() && S.Equal.ofQ(zRe, min)) {
        // z - k*Pi
        return result;
      }

      if (arg1.isTan()) { // Tan(z)
        // (Re(z) == Pi/2 + k*Pi && Im(z) > 0)
        if (zIm.isPositiveResult() && S.Equal.ofQ(zRe, max)) {
          // z - k*Pi
          return result;
        }
      } else { // Cot(z)
        // (Re(z) == Pi/2 + k*Pi && Im(z) >= 0)
        if (zIm.isNonNegativeResult() && S.Equal.ofQ(zRe, max)) {
          // z - k*Pi
          return result;
        }
      }
    }
    return F.NIL;
  }

  private static IComplexNum log(final IComplexNum val) {
    if (val instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(ApcomplexMath.log(((ApcomplexNum) val).apcomplexValue()));
    }
    ComplexNum z = (ComplexNum) val;
    if (z.isNaN()) {
      return ComplexNum.NaN;
    }
    return ComplexNum.valueOf(Math.log(z.dabs()), Math.atan2(z.imDoubleValue(), z.reDoubleValue()));
  }

  /**
   * Try simplifying <code>ArcSin(Sin(z))</code>
   *
   * @param arg1 is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
   * @return
   */
  private static IExpr arcSinSin(final IExpr arg1) {
    IExpr z = arg1.first();
    IExpr zRe = z.re();
    IExpr k = S.Quotient.of(F.Subtract(zRe, F.CPiHalf), S.Pi).inc();
    if (k.isInteger()) {
      // -Pi/2 + k*Pi
      IExpr min = S.Times.of(F.Plus(F.CN1D2, k), Pi);
      // Pi/2 + k*Pi
      IExpr max = S.Times.of(F.Plus(F.C1D2, k), Pi);
      // (-1)^k * (z - Pi*k)
      IAST result = F.Times(F.Power(F.CN1, k), F.Subtract(z, F.Times(k, S.Pi)));
      // (-(Pi/2) + k*Pi < Re(z) < Pi/2 + k*Pi
      if (S.Less.ofQ(min, zRe) && max.greater(zRe).isTrue()) {// S.Greater.ofQ(max, zRe)) {
        // (-1)^k * (z - Pi*k)
        return result;
      }

      IExpr zIm = z.im();
      // (Re(z) == -(Pi/2) + k*Pi && Im(z) < 0)
      if (zIm.isNonNegativeResult() && S.Equal.ofQ(zRe, min)) {
        // (-1)^k * (z - Pi*k)
        return result;
      }

      // (Re(z) == Pi/2 + k*Pi && Im(z) > 0)
      if ((zIm.isNegativeResult() || zIm.isZero()) && S.Equal.ofQ(zRe, max)) {
        // (-1)^k * (z - Pi*k)
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * Try simplifying <code>ArcCos(Cos(z))</code>
   *
   * @param arg1 is assumed to be <code>Tan(z)</code> or <code>Cot(z)</code>
   * @return
   */
  private static IExpr arcCosCos(final IExpr arg1) {
    IExpr z = arg1.first();
    IExpr zRe = z.re();
    IExpr k = S.Quotient.of(F.Subtract(zRe, S.Pi), S.Pi).inc();
    if (k.isInteger()) {
      // k*Pi
      IExpr min = S.Times.of(k, Pi);
      // (k+1)*Pi
      IExpr max = S.Times.of(k.inc(), Pi);
      // (-1)^k * (z - Pi*k - Pi/2) + Pi/2
      IAST result = F.Plus(
          F.Times(F.Power(F.CN1, k), F.Plus(z, F.Times(F.CN1, k, S.Pi), F.CNPiHalf)), F.CPiHalf);

      // (k*Pi < Re(z) < (k + 1)*Pi
      if (S.Less.ofQ(min, zRe) && max.greater(zRe).isTrue()) { // S.Greater.ofQ(max, zRe)) {
        return result;
      }

      IExpr zIm = z.im();
      // (Re(z) == k*Pi && Im(z) >= 0)
      if (zIm.isNonNegativeResult() && S.Equal.ofQ(zRe, min)) {
        return result;
      }

      // (Re(z) == (k + 1)*Pi && Im(z) <= 0)
      if ((zIm.isNegativeResult() || zIm.isZero()) && S.Equal.ofQ(zRe, max)) {
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * Integer logarithm of <code>arg</code> for base <code>b</code>. Gives Log <sub>b</sub>(arg) or
   * <code>Log(arg)/Log(b)</code>.
   *
   * @param b the base of the logarithm
   * @param arg
   * @return
   */
  public static IExpr baseBLog(final IInteger b, final IInteger arg) {
    try {
      long l1 = b.toLong();
      long l2 = arg.toLong();
      if (l1 > 0L && l2 > 0L) {
        boolean inverse = false;
        if (l1 > l2) {
          long t = l2;
          l2 = l1;
          l1 = t;
          inverse = true;
        }
        double numericResult = Math.log(l2) / Math.log(l1);
        if (F.isNumIntValue(numericResult)) {
          long symbolicResult = DoubleMath.roundToLong(numericResult, Config.ROUNDING_MODE);
          if (inverse) {
            if (b.equals(arg.powerRational(symbolicResult))) {
              // cross checked result
              return F.QQ(1L, symbolicResult);
            }
          } else {
            if (arg.equals(b.powerRational(symbolicResult))) {
              // cross checked result
              return F.ZZ(symbolicResult);
            }
          }
        }
      }
    } catch (ArithmeticException ae) {
      // toLong() method failed
    }
    return F.NIL;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ExpTrigsFunctions() {}

  /**
   * <code>-2 * IntegerPart( IntegerPart(r) / 2 )</code>
   *
   * @param r a rational number
   * @return
   */
  private static IRational integerPartFolded2(IRational r) {
    return r.integerPart().multiply(F.C1D2).integerPart().multiply(F.CN2);
  }
}
