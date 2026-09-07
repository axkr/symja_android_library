package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.sympy.physics.Wigner;
import org.matheclipse.parser.client.ParserConfig;

public class QuantumPhysicsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ClebschGordan.setEvaluator(new ThreeJ(S.ClebschGordan, true));
      S.ThreeJSymbol.setEvaluator(new ThreeJ(S.ThreeJSymbol, false));
      S.SixJSymbol.setEvaluator(new SixJSymbol());
      S.WignerD.setEvaluator(new WignerD());
    }
  }

  /**
   * <code>ThreeJSymbol({j1,m1},{j2,m2},{j3,m3})</code> and
   * <code>ClebschGordan({j1,m1},{j2,m2},{j3,m3})</code>. The Clebsch-Gordan coefficient is
   *
   * <pre>
   * (-1)^(j1-j2+m3) * Sqrt(2*j3+1) * ThreeJSymbol({j1,m1},{j2,m2},{j3,-m3})
   * </pre>
   */
  private static final class ThreeJ extends AbstractFunctionEvaluator {
    private final IBuiltInSymbol head;
    private final boolean clebschGordan;

    ThreeJ(IBuiltInSymbol head, boolean clebschGordan) {
      this.head = head;
      this.clebschGordan = clebschGordan;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (!arg1.isList2() || !arg2.isList2() || !arg3.isList2()) {
        return F.NIL;
      }
      IExpr j1 = arg1.first();
      IExpr j2 = arg2.first();
      IExpr j3 = arg3.first();
      IExpr m1 = arg1.second();
      IExpr m2 = arg2.second();
      IExpr m3 = arg3.second();

      if (j1.isReal() && j2.isReal() && j3.isReal()) {
        IExpr symbolic = symbolicProjection(j1, j2, j3, m1, m2, m3, ast, engine);
        if (symbolic.isPresent()) {
          return symbolic;
        }
        if (m1.isReal() && m2.isReal() && m3.isReal()) {
          try {
            IExpr[] values = {j1, j2, j3, m1, m2, m3};
            IExpr value = value(values);
            // NIL when the quantum numbers are too large to evaluate
            return value.isNIL() ? F.NIL : numericIfInexact(value, values, engine);
          } catch (Wigner.TriangularException e) {
            Errors.printMessage(head, "tri", F.List(ast), engine);
            return F.C0;
          } catch (Wigner.NotPhysicalException e) {
            Errors.printMessage(head, "phy", F.List(ast), engine);
            return F.C0;
          }
        }
      }
      if (clebschGordan) {
        return F.Times(F.Power(F.CN1, F.Plus(j1, F.Negate(j2), m3)),
            F.Sqrt(F.Plus(F.C1, F.Times(F.C2, j3))),
            F.ThreeJSymbol(F.list(j1, m1), F.list(j2, m2), F.list(j3, F.Negate(m3))));
      }
      return F.NIL;
    }

    /**
     * The exact value for the real quantum numbers <code>{j1, j2, j3, m1, m2, m3}</code>.
     *
     * @return {@link F#NIL} if a value is well defined but too large to evaluate
     * @throws Wigner.TriangularException if a <code>j</code> is not an integer or half-integer, or
     *         the <code>j</code>s do not couple
     * @throws Wigner.NotPhysicalException if an <code>m</code> is not an integer or half-integer, or
     *         violates a selection rule
     */
    private IExpr value(IExpr[] values)
        throws Wigner.TriangularException, Wigner.NotPhysicalException {
      int[] d = Wigner.doubled(values);
      if (d == null) {
        if (Wigner.isTooLarge(values)) {
          return F.NIL;
        }
        // a projection which is not an integer or half-integer violates |m| <= j or the parity of
        // j-m, which is a selection rule and not a failure of the triangle relation
        IExpr[] projections = {values[3], values[4], values[5]};
        if (Wigner.doubled(projections) == null) {
          throw new Wigner.NotPhysicalException();
        }
        throw new Wigner.TriangularException();
      }
      if (!clebschGordan) {
        return Wigner.wigner3j(d[0], d[1], d[2], d[3], d[4], d[5]);
      }
      IExpr threeJ = Wigner.wigner3j(d[0], d[1], d[2], d[3], d[4], -d[5]);
      if (threeJ.isZero()) {
        return F.C0;
      }
      // j1-j2+m3 is an integer whenever the 3j symbol does not vanish
      int exponent = (d[0] - d[1] + d[5]) / 2;
      IInteger phase = exponent % 2 == 0 ? F.C1 : F.CN1;
      return F.Times(phase, F.Sqrt(F.ZZ(d[2] + 1)), threeJ);
    }

    /**
     * When exactly one of the projections <code>m1, m2, m3</code> is a bare symbol and the other
     * two are real numbers, the selection rule (<code>m1+m2+m3 == 0</code>, or
     * <code>m1+m2 == m3</code> for a Clebsch-Gordan coefficient) forces its value. The symbol is
     * evaluated for that value and reported as a <code>Piecewise</code> which vanishes elsewhere.
     *
     * @return the <code>Piecewise</code> (or <code>0</code>), or {@link F#NIL} if the pattern does
     *         not apply
     */
    private IExpr symbolicProjection(IExpr j1, IExpr j2, IExpr j3, IExpr m1, IExpr m2, IExpr m3,
        IAST ast, EvalEngine engine) {
      IExpr[] m = {m1, m2, m3};
      int index = -1;
      for (int i = 0; i < m.length; i++) {
        if (m[i].isSymbol()) {
          if (index >= 0) {
            return F.NIL;
          }
          index = i;
        } else if (!m[i].isReal()) {
          return F.NIL;
        }
      }
      if (index < 0) {
        return F.NIL;
      }
      IExpr variable = m[index];
      IExpr forced = forcedProjection(index, m);
      m[index] = forced;
      IExpr[] values = {j1, j2, j3, m[0], m[1], m[2]};
      IExpr value;
      try {
        value = value(values);
      } catch (Wigner.TriangularException e) {
        // independent of the projection
        Errors.printMessage(head, "tri", F.List(ast), engine);
        return F.C0;
      } catch (Wigner.NotPhysicalException e) {
        // the forced value violates |m| <= j or the parity of j-m: identically 0
        return F.C0;
      }
      if (value.isNIL()) {
        // too large to evaluate
        return F.NIL;
      }
      if (value.isZero()) {
        return F.C0;
      }
      value = numericIfInexact(value, values, engine);
      return F.Piecewise(F.list(F.list(value, F.Equal(variable, forced))), F.C0);
    }

    /** The value of <code>m[index]</code> which the selection rule forces. */
    private IExpr forcedProjection(int index, IExpr[] m) {
      if (!clebschGordan) {
        // m1 + m2 + m3 == 0
        return m[(index + 1) % 3].plus(m[(index + 2) % 3]).negate();
      }
      // m1 + m2 == m3
      switch (index) {
        case 0:
          return m[2].plus(m[1].negate());
        case 1:
          return m[2].plus(m[0].negate());
        default:
          return m[0].plus(m[1]);
      }
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

  /** <code>SixJSymbol({j1,j2,j3},{j4,j5,j6})</code> */
  private static final class SixJSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (!arg1.isList3() || !arg2.isList3()) {
        return F.NIL;
      }
      IAST list1 = (IAST) arg1;
      IAST list2 = (IAST) arg2;
      IExpr[] values = {list1.arg1(), list1.arg2(), list1.arg3(), //
          list2.arg1(), list2.arg2(), list2.arg3()};
      if (list1.forAll(IExpr::isReal) && list2.forAll(IExpr::isReal)) {
        try {
          int[] d = Wigner.doubled(values);
          if (d == null) {
            if (Wigner.isTooLarge(values)) {
              // well defined, but too large to evaluate
              return F.NIL;
            }
            throw new Wigner.TriangularException();
          }
          IExpr value = Wigner.wigner6j(d[0], d[1], d[2], d[3], d[4], d[5]);
          return numericIfInexact(value, values, engine);
        } catch (Wigner.TriangularException e) {
          Errors.printMessage(S.SixJSymbol, "tri", F.List(ast), engine);
          return F.C0;
        }
      }
      if (list1.forAll(x -> x.isNumericFunction()) && list2.forAll(x -> x.isNumericFunction())) {
        Errors.printMessage(S.SixJSymbol, "tri", F.List(ast), engine);
        return F.C0;
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

  /**
   * <code>WignerD({j,m1,m2},alpha,beta,gamma)</code>, <code>WignerD({j,m1,m2},beta,gamma)</code>
   * and <code>WignerD({j,m1,m2},beta)</code>
   */
  private static final class WignerD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isList3()) {
        return F.NIL;
      }
      IAST jmm = (IAST) arg1;
      int[] d = Wigner.doubled(jmm.arg1(), jmm.arg2(), jmm.arg3());
      if (d == null) {
        return F.NIL;
      }
      int dj = d[0];
      int dm1 = d[1];
      int dm2 = d[2];
      if (dj < 0 || Math.abs(dm1) > dj || Math.abs(dm2) > dj //
          || (dj - dm1) % 2 != 0 || (dj - dm2) % 2 != 0) {
        // TODO not physical; could be evaluated numerically
        return F.NIL;
      }

      IExpr alpha = F.C0;
      IExpr beta;
      IExpr gamma = F.C0;
      switch (ast.argSize()) {
        case 2:
          beta = ast.arg2();
          break;
        case 3:
          beta = ast.arg2();
          gamma = ast.arg3();
          break;
        default:
          alpha = ast.arg2();
          beta = ast.arg3();
          gamma = ast.arg4();
          break;
      }
      try {
        return Wigner.wignerD(dj, dm1, dm2, alpha, beta, gamma, engine);
      } catch (ValidateException ve) {
        return Errors.printMessage(S.WignerD, ve, engine);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }
  }

  /**
   * Evaluates the exact result numerically if one of the (real) input values is an inexact
   * number, at the highest precision among those inputs.
   */
  private static IExpr numericIfInexact(IExpr exact, IExpr[] values, EvalEngine engine) {
    long precision = -1;
    for (IExpr value : values) {
      if (value.isInexactNumber()) {
        precision = Math.max(precision, ((INum) value).precision());
      }
    }
    if (precision < 0) {
      return exact;
    }
    if (precision > ParserConfig.MACHINE_PRECISION) {
      return engine.evalN(exact, precision);
    }
    return F.num(exact.evalfNaN());
  }

  public static void initialize() {
    Initializer.init();
  }

  private QuantumPhysicsFunctions() {}

}
