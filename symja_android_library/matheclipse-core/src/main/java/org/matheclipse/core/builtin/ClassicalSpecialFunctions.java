package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.WorkingPrecision;
import org.matheclipse.external.apfloat.ApcomplexAppellMath;
import org.matheclipse.external.apfloat.ApcomplexMittagLefflerMath;

/**
 * Special functions which are written with others or summed as power series:
 * <code>MittagLefflerE</code>, <code>QGamma</code>, the Neville theta functions,
 * <code>InverseEllipticNomeQ</code>, <code>CarlsonRE</code>, <code>AppellF2</code>,
 * <code>AppellF3</code>, <code>AppellF4</code>, <code>CoulombH1</code>, <code>CoulombH2</code>,
 * <code>FresnelF</code>, <code>FresnelG</code> and <code>CardinalBSplineBasis</code>.
 *
 * <p>
 * The power series of <code>MittagLefflerE</code> and of the Appell functions are summed with
 * apfloat in {@link ApcomplexMittagLefflerMath} and {@link ApcomplexAppellMath}; the other
 * functions are evaluated through the functions they are written with.
 */
public final class ClassicalSpecialFunctions {

  private static class Initializer {

    private static void init() {
      S.MittagLefflerE.setEvaluator(new MittagLefflerE());
      S.QGamma.setEvaluator(new QGamma());
      S.NevilleThetaS.setEvaluator(new NevilleTheta(S.NevilleThetaS));
      S.NevilleThetaC.setEvaluator(new NevilleTheta(S.NevilleThetaC));
      S.NevilleThetaD.setEvaluator(new NevilleTheta(S.NevilleThetaD));
      S.NevilleThetaN.setEvaluator(new NevilleTheta(S.NevilleThetaN));
      S.InverseEllipticNomeQ.setEvaluator(new InverseEllipticNomeQ());
      S.CarlsonRE.setEvaluator(new CarlsonRE());
      S.AppellF2.setEvaluator(new Appell(S.AppellF2, 7));
      S.AppellF3.setEvaluator(new Appell(S.AppellF3, 7));
      S.AppellF4.setEvaluator(new Appell(S.AppellF4, 6));
      S.CoulombH1.setEvaluator(new CoulombH(true));
      S.CoulombH2.setEvaluator(new CoulombH(false));
      S.FresnelF.setEvaluator(new FresnelAuxiliary(true));
      S.FresnelG.setEvaluator(new FresnelAuxiliary(false));
      S.CardinalBSplineBasis.setEvaluator(new CardinalBSplineBasis());
    }
  }

  /**
   * Whether all arguments are numbers which are evaluated numerically: one of them is inexact, or
   * the engine evaluates numerically.
   */
  private static boolean isNumeric(IAST ast, EvalEngine engine) {
    boolean inexact = false;
    for (IExpr arg : ast) {
      if (!arg.isNumber()) {
        return false;
      }
      inexact |= arg.isInexactNumber();
    }
    return inexact || engine.isNumericMode();
  }

  /**
   * <code>MittagLefflerE(a, z)</code>, <code>MittagLefflerE(a, b, z)</code> - the Mittag-Leffler
   * function <code>Sum(z^k/Gamma(a k + b), {k, 0, Infinity})</code>, with <code>b = 1</code> by
   * default.
   */
  private static final class MittagLefflerE extends AbstractFunctionEvaluator {

    /**
     * <code>MittagLefflerE(n, z)</code> for an integer <code>n</code>: the mean of
     * <code>Exp(w*r)</code> over the <code>n</code>-th roots of unity <code>r</code>, with
     * <code>w = z^(1/n)</code>. A pair of conjugate roots <code>Exp(±2*Pi*I*k/n)</code> gives the
     * real term <code>2*Exp(w*Cos(2*Pi*k/n))*Cos(w*Sin(2*Pi*k/n))</code>.
     */
    private static IExpr integerOrder(int n, IExpr z) {
      IExpr w = F.Power(z, F.QQ(1, n));
      IASTAppendable sum = F.PlusAlloc(n);
      sum.append(F.Exp(w));
      if (n % 2 == 0) {
        sum.append(F.Exp(F.Negate(w)));
      }
      for (int k = 1; 2 * k < n; k++) {
        IExpr angle = F.Times(F.QQ(2 * k, n), S.Pi);
        sum.append(F.Times(F.C2, F.Exp(F.Times(w, F.Cos(angle))),
            F.Cos(F.Times(w, F.Sin(angle)))));
      }
      return F.Times(F.QQ(1, n), sum);
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr a = ast.arg1();
      final IExpr b = ast.isAST3() ? ast.arg2() : F.C1;
      final IExpr z = ast.last();
      if (z.isZero()) {
        return F.Power(F.Gamma(b), F.CN1);
      }
      if (b.isOne()) {
        if (a.isZero()) {
          return F.Power(F.Subtract(F.C1, z), F.CN1);
        }
        if (a.isOne()) {
          return F.Exp(z);
        }
        if (a.equals(F.C2)) {
          return F.Cosh(F.Sqrt(z));
        }
        if (a.equals(F.C1D2)) {
          return F.Times(F.Exp(F.Sqr(z)), F.Erfc(F.Negate(z)));
        }
        int n = a.toIntDefault();
        if (n >= 3 && n <= 6 && !z.isInexactNumber()) {
          return integerOrder(n, z);
        }
      } else if (a.isOne() && b.equals(F.C2)) {
        return F.Times(F.Subtract(F.Exp(z), F.C1), F.Power(z, F.CN1));
      } else if (a.isOne() && b.isRational() && !(b.isInteger() && !b.isPositive())
          && !z.isInexactNumber()) {
        // z^(1-b)*E^z*(1 - GammaRegularized(b-1, z)) with the regularized incomplete Gamma function
        return F.Times(F.Power(z, F.Subtract(F.C1, b)), F.Exp(z),
            F.Subtract(F.C1, F.GammaRegularized(F.Subtract(b, F.C1), z)));
      }
      if (ast.isAST3() && b.isOne()) {
        return F.binaryAST2(S.MittagLefflerE, a, z);
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      IAST arguments = F.ternaryAST3(S.List, a, b, z);
      return WorkingPrecision.evaluate(arguments, engine, true,
          v -> ApcomplexMittagLefflerMath.mittagLefflerE(v[0], v[1], v[2]));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>QGamma(z, q)</code> - the q-gamma function
   * <code>(1-q)^(1-z) QPochhammer(q, q)/QPochhammer(q^z, q)</code>; for a positive integer
   * <code>z</code> the q-factorial <code>Product((1-q^k)/(1-q), {k, 1, z-1})</code>.
   */
  private static final class QGamma extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr z = ast.arg1();
      final IExpr q = ast.arg2();
      if (z.isInteger()) {
        int n = z.toIntDefault();
        if (n <= 0) {
          return n == Integer.MIN_VALUE ? F.NIL : F.CComplexInfinity;
        }
        if (n > 1000 || (!q.isNumber() && n > 3)) {
          return F.NIL;
        }
        // Product((1 - q^k)/(1 - q)) = Product(1 + q + ... + q^(k-1))
        IASTAppendable product = F.TimesAlloc(n);
        for (int k = 1; k < n; k++) {
          IASTAppendable sum = F.PlusAlloc(k);
          for (int j = 0; j < k; j++) {
            sum.append(F.Power(q, F.ZZ(j)));
          }
          product.append(sum);
        }
        return n == 1 ? (q.isInexactNumber() ? F.CD1 : F.C1) : engine.evaluate(F.Expand(product));
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      return F.Times(F.Power(F.Subtract(F.C1, q), F.Subtract(F.C1, z)),
          F.binaryAST2(S.QPochhammer, q, q),
          F.Power(F.binaryAST2(S.QPochhammer, F.Power(q, z), q), F.CN1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>NevilleThetaS(z, m)</code>, <code>NevilleThetaC(z, m)</code>,
   * <code>NevilleThetaD(z, m)</code>, <code>NevilleThetaN(z, m)</code> - the Neville theta
   * functions, the Jacobi theta functions of <code>v = Pi z/(2 EllipticK(m))</code> and the nome
   * <code>q = EllipticNomeQ(m)</code>, normalized such that <code>NevilleThetaS(z, m)</code> is
   * <code>z + O(z^3)</code> and the others are <code>1</code> at <code>z = 0</code>.
   */
  private static final class NevilleTheta extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol function;

    NevilleTheta(IBuiltInSymbol function) {
      this.function = function;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr z = ast.arg1();
      final IExpr m = ast.arg2();
      final boolean s = function == S.NevilleThetaS;
      if (z.isZero()) {
        return s ? F.C0 : F.C1;
      }
      if (m.isZero()) {
        if (s) {
          return F.Sin(z);
        }
        return function == S.NevilleThetaC ? F.Cos(z) : F.C1;
      }
      if (m.isOne()) {
        if (s) {
          return F.Sinh(z);
        }
        return function == S.NevilleThetaN ? F.Cosh(z) : F.C1;
      }
      IExpr negated = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
      if (negated.isPresent()) {
        // NevilleThetaS is odd, the others are even
        IExpr value = F.binaryAST2(function, negated, m);
        return s ? F.Negate(value) : value;
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      IExpr k = F.EllipticK(m);
      IExpr q = F.EllipticNomeQ(m);
      IExpr v = F.Times(F.Pi, z, F.Power(F.Times(F.C2, k), F.CN1));
      if (s) {
        // (2 K/Pi) theta1(v)/theta1'(0), theta1'(0) = theta2(0) theta3(0) theta4(0)
        return F.Times(F.C2, k, F.Power(F.Pi, F.CN1), F.EllipticTheta(F.C1, v, q),
            F.Power(F.Times(F.EllipticTheta(F.C2, F.C0, q), F.EllipticTheta(F.C3, F.C0, q),
                F.EllipticTheta(F.C4, F.C0, q)), F.CN1));
      }
      IExpr a = function == S.NevilleThetaC ? F.C2 : function == S.NevilleThetaD ? F.C3 : F.C4;
      return F.Times(F.EllipticTheta(a, v, q), F.Power(F.EllipticTheta(a, F.C0, q), F.CN1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>InverseEllipticNomeQ(q)</code> - the parameter <code>m</code> whose nome
   * <code>EllipticNomeQ(m)</code> is <code>q</code>:
   * <code>(EllipticTheta(2, 0, q)/EllipticTheta(3, 0, q))^4</code>.
   */
  private static final class InverseEllipticNomeQ extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr q = ast.arg1();
      if (q.isZero()) {
        return F.C0;
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      return F.Power(F.Times(F.EllipticTheta(F.C2, F.C0, q),
          F.Power(F.EllipticTheta(F.C3, F.C0, q), F.CN1)), F.C4);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>CarlsonRE(x, y)</code> - the complete elliptic integral
   * <code>4/Pi CarlsonRG(0, x, y)</code>; <code>Sqrt(x)</code> for <code>x = y</code>.
   */
  private static final class CarlsonRE extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr x = ast.arg1();
      final IExpr y = ast.arg2();
      if (x.equals(y)) {
        return F.Sqrt(x);
      }
      if (x.isZero() || y.isZero()) {
        // RG(0, 0, y) = Sqrt(y)/2
        return F.Times(F.C2, F.Sqrt(x.isZero() ? y : x), F.Power(F.Pi, F.CN1));
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      return F.Times(F.C4, F.Power(F.Pi, F.CN1), F.CarlsonRG(F.C0, x, y));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION, Attribute.ORDERLESS);
    }
  }

  /**
   * <code>AppellF2(a, b1, b2, c1, c2, x, y)</code>, <code>AppellF3(a1, a2, b1, b2, c, x, y)</code>
   * and <code>AppellF4(a, b, c1, c2, x, y)</code> - Appell's hypergeometric functions of two
   * variables. With one variable <code>0</code> they are <code>Hypergeometric2F1</code> functions;
   * numbers inside of the region of convergence are summed as double series.
   */
  private static final class Appell extends AbstractFunctionEvaluator {

    private final IBuiltInSymbol function;

    private final int argSize;

    Appell(IBuiltInSymbol function, int argSize) {
      this.function = function;
      this.argSize = argSize;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr x = ast.get(argSize - 1);
      final IExpr y = ast.get(argSize);
      if (x.isZero() && y.isZero()) {
        return F.C1;
      }
      if (x.isZero() || y.isZero()) {
        return reduced(ast, y.isZero());
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      return WorkingPrecision.evaluate(ast, engine, true, v -> {
        if (function == S.AppellF2) {
          return ApcomplexAppellMath.appellF2(v[0], v[1], v[2], v[3], v[4], v[5], v[6]);
        }
        if (function == S.AppellF3) {
          return ApcomplexAppellMath.appellF3(v[0], v[1], v[2], v[3], v[4], v[5], v[6]);
        }
        return ApcomplexAppellMath.appellF4(v[0], v[1], v[2], v[3], v[4], v[5]);
      });
    }

    /**
     * The <code>Hypergeometric2F1</code> function in the first variable if <code>first</code>,
     * otherwise in the second one, when the other variable is <code>0</code>.
     */
    private IExpr reduced(IAST ast, boolean first) {
      if (function == S.AppellF2) {
        // F2(a; b1, b2; c1, c2; x, 0) = 2F1(a, b1; c1; x)
        return first ? F.Hypergeometric2F1(ast.arg1(), ast.arg2(), ast.arg4(), ast.get(6))
            : F.Hypergeometric2F1(ast.arg1(), ast.arg3(), ast.arg5(), ast.get(7));
      }
      if (function == S.AppellF3) {
        // F3(a1, a2; b1, b2; c; x, 0) = 2F1(a1, b1; c; x)
        return first ? F.Hypergeometric2F1(ast.arg1(), ast.arg3(), ast.arg5(), ast.get(6))
            : F.Hypergeometric2F1(ast.arg2(), ast.arg4(), ast.arg5(), ast.get(7));
      }
      // F4(a, b; c1, c2; x, 0) = 2F1(a, b; c1; x)
      return first ? F.Hypergeometric2F1(ast.arg1(), ast.arg2(), ast.arg3(), ast.arg5())
          : F.Hypergeometric2F1(ast.arg1(), ast.arg2(), ast.arg4(), ast.get(6));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return new int[] {argSize, argSize};
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>CoulombH1(l, eta, rho)</code> and <code>CoulombH2(l, eta, rho)</code> - the outgoing and
   * incoming Coulomb wave functions <code>CoulombG(l, eta, rho) + I CoulombF(l, eta, rho)</code>
   * and <code>CoulombG(l, eta, rho) - I CoulombF(l, eta, rho)</code>.
   */
  private static final class CoulombH extends AbstractFunctionEvaluator {

    private final boolean outgoing;

    CoulombH(boolean outgoing) {
      this.outgoing = outgoing;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      IExpr f = F.ternaryAST3(S.CoulombF, ast.arg1(), ast.arg2(), ast.arg3());
      IExpr g = F.ternaryAST3(S.CoulombG, ast.arg1(), ast.arg2(), ast.arg3());
      return F.Plus(g, F.Times(outgoing ? F.CI : F.CNI, f));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>FresnelF(z)</code> and <code>FresnelG(z)</code> - the auxiliary Fresnel functions
   * <code>(1/2 - FresnelS(z)) Cos(Pi z^2/2) - (1/2 - FresnelC(z)) Sin(Pi z^2/2)</code> and
   * <code>(1/2 - FresnelC(z)) Cos(Pi z^2/2) + (1/2 - FresnelS(z)) Sin(Pi z^2/2)</code>.
   */
  private static final class FresnelAuxiliary extends AbstractFunctionEvaluator {

    private final boolean f;

    FresnelAuxiliary(boolean f) {
      this.f = f;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr z = ast.arg1();
      if (z.isZero()) {
        return F.C1D2;
      }
      if (z.isInfinity()) {
        return F.C0;
      }
      if (!isNumeric(ast, engine)) {
        return F.NIL;
      }
      IExpr angle = F.Times(F.C1D2, F.Pi, F.Sqr(z));
      IExpr s = F.Subtract(F.C1D2, F.FresnelS(z));
      IExpr c = F.Subtract(F.C1D2, F.FresnelC(z));
      return f ? F.Subtract(F.Times(s, F.Cos(angle)), F.Times(c, F.Sin(angle)))
          : F.Plus(F.Times(c, F.Cos(angle)), F.Times(s, F.Sin(angle)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>CardinalBSplineBasis(d, x)</code> - the centered cardinal B-spline of degree
   * <code>d</code>: <code>1/d! Sum((-1)^k Binomial(d+1, k) Max(x + (d+1)/2 - k, 0)^d)</code>,
   * which is <code>0</code> outside of <code>-(d+1)/2 &lt; x &lt; (d+1)/2</code>.
   */
  private static final class CardinalBSplineBasis extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int d = ast.arg1().toIntDefault();
      final IExpr x = ast.arg2();
      if (d < 0 || d > 1000 || !x.isReal()) {
        return F.NIL;
      }
      IExpr half = F.QQ(d + 1, 2);
      if (x.greaterThan(half).isTrue() || x.lessThan(F.Negate(half)).isTrue()) {
        return F.C0;
      }
      IASTAppendable sum = F.PlusAlloc(d + 2);
      for (int k = 0; k <= d + 1; k++) {
        IExpr shifted = engine.evaluate(F.Plus(x, half, F.ZZ(-k)));
        if (!shifted.isPositive()) {
          continue;
        }
        IExpr term = F.Times(F.Binomial(F.ZZ(d + 1), F.ZZ(k)), F.Power(shifted, F.ZZ(d)));
        sum.append(k % 2 == 0 ? term : F.Negate(term));
      }
      return F.Times(F.Power(F.Factorial(F.ZZ(d)), F.CN1), sum);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ClassicalSpecialFunctions() {}
}
