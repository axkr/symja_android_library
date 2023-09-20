package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class QuantumPhysicsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ClebschGordan.setEvaluator(new ClebschGordan());
      S.ThreeJSymbol.setEvaluator(new ThreeJSymbol());
      S.SixJSymbol.setEvaluator(new SixJSymbol());
    }
  }

  private static class ClebschGordan extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (arg1.isList2() && arg2.isList2() && arg3.isList2()) {
        IExpr j1 = arg1.first();
        IExpr j2 = arg2.first();
        IExpr j3 = arg3.first();
        IExpr m1 = arg1.second();
        IExpr m2 = arg2.second();
        IExpr m3 = arg3.second();
        // if (j1.isNumber() && j2.isNumber() && j3.isNumber() && m1.isNumber() && m2.isNumber()
        // && m3.isNumber()) {
        // // see wigner_3j from sympy
        // INumber j_1 = (INumber) j1;
        // INumber j_2 = (INumber) j2;
        // INumber j_3 = (INumber) j3;
        // INumber m_1 = (INumber) m1;
        // INumber m_2 = (INumber) m2;
        // INumber m_3 = (INumber) m3;

        IExpr m3Negate = m3.negate();
        IExpr threeJSymbol = threeJSymbol(j1, j2, j3, m1, m2, m3Negate, //
            S.ClebschGordan, F.ThreeJSymbol(arg1, arg2, F.List(j3, m3Negate)), engine);
        if (threeJSymbol.isPresent()) {
          // res = (-1) ** sympify(j_1 - j_2 + m_3) * sqrt(2 * j_3 + 1) * \
          // wigner_3j(j_1, j_2, j_3, m_1, m_2, -m_3)
          IExpr res = F.Times(F.Power(F.CN1, F.Plus(j1, F.Negate(j2), m3)),
              F.Sqrt(F.Plus(F.Times(F.C2, j3), F.C1)), threeJSymbol);
          return res;
        }
        // }
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

  }

  private static class ThreeJSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if (arg1.isList2() && arg2.isList2() && arg3.isList2()) {
        IExpr j1 = arg1.first();
        IExpr j2 = arg2.first();
        IExpr j3 = arg3.first();
        IExpr m1 = arg1.second();
        IExpr m2 = arg2.second();
        IExpr m3 = arg3.second();
        return threeJSymbol(j1, j2, j3, m1, m2, m3, //
            S.ThreeJSymbol, ast, engine);
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


  }

  private static class SixJSymbol extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList3() && arg2.isList3()) {
        IAST list1 = (IAST) arg1;
        IAST list2 = (IAST) arg2;
        IExpr j1 = list1.arg1();
        IExpr j2 = list1.arg2();
        IExpr j3 = list1.arg3();
        IExpr m1 = list2.arg1();
        IExpr m2 = list2.arg2();
        IExpr m3 = list2.arg3();
        // return sixJSymbol(j1, j2, j3, m1, m2, m3, //
        // S.SixJSymbol, ast, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.NO_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }


  }

  private static IExpr threeJSymbol(IExpr j1, IExpr j2, IExpr j3, IExpr m1, IExpr m2, IExpr m3,
      final IBuiltInSymbol head, final IAST ast, EvalEngine engine) {
    if (j1.isNumber() && j2.isNumber() && j3.isNumber() && m1.isNumber() && m2.isNumber()
        && m3.isNumber()) {
      // https://functions.wolfram.com/HypergeometricFunctions/ThreeJSymbol/02/
      INumber j_1 = (INumber) j1;
      INumber j_2 = (INumber) j2;
      INumber j_3 = (INumber) j3;
      INumber m_1 = (INumber) m1;
      INumber m_2 = (INumber) m2;
      INumber m_3 = (INumber) m3;
      if (!j_1.times(F.C2).isInteger() //
          || !j_2.times(F.C2).isInteger() //
          || !j_3.times(F.C2).isInteger()) {
        return zeroNotPhysical(head, ast, engine);
      }
      if (!m_1.times(F.C2).isInteger() //
          || !m_2.times(F.C2).isInteger() //
          || !m_3.times(F.C2).isInteger()) {
        return zeroNotPhysical(head, ast, engine);
      }
      if (!m_1.plus(m_2).plus(m_3).isZero()) {
        return zeroNotPhysical(head, ast, engine);
      }
      // IExpr prefid = engine
      // .evaluate(F.IntegerPart(F.Power(F.CN1, F.Floor(j_1.subtract(j_2).subtract(m_3)))));
      m_3 = m_3.negate();
      INumber a1 = j_1.plus(j_2).subtract(j_3);
      if (a1.less(F.C0).isTrue()) {
        return zeroNotPhysical(head, ast, engine);
      }
      INumber a2 = j_1.subtract(j_2).plus(j_3);
      if (a2.less(F.C0).isTrue()) {
        return zeroNotPhysical(head, ast, engine);
      }
      INumber a3 = j_1.negate().plus(j_2).plus(j_3);
      if (a3.less(F.C0).isTrue()) {
        return zeroNotPhysical(head, ast, engine);
      }

      if (m_1.abs().greater(j_1).isTrue() //
          || m_2.abs().greater(j_2).isTrue() //
          || m_3.abs().greater(j_3).isTrue() //
      ) {
        return zeroNotPhysical(head, ast, engine);
      }

      // use symbolic expressions here (m_3 is negated!)
      return threeJSymbol(j1, j2, j3, m1, m2, m3, engine);

      // // maxfact = max(j_1 + j_2 + j_3 + 1, j_1 + abs(m_1), j_2 + abs(m_2),
      // // j_3 + abs(m_3))
      // // _calc_factlist(int(maxfact))
      // //
      //
      // IAST argsqrt = F.Times(F.Factorial(F.Floor(j_1.plus(j_2).subtract(j_3))), //
      // F.Factorial(F.Floor(j_1.subtract(j_2).plus(j_3))), //
      // F.Factorial(F.Floor(j_1.negate().plus(j_2).plus(j_3))), //
      // F.Factorial(F.Floor(j_1.subtract(m_1))), //
      // F.Factorial(F.Floor(j_1.plus(m_1))), //
      // F.Factorial(F.Floor(j_2.subtract(m_2))), //
      // F.Factorial(F.Floor(j_2.plus(m_2))), //
      // F.Factorial(F.Floor(j_3.subtract(m_3))), //
      // F.Factorial(F.Floor(j_3.plus(m_3))), //
      // F.Power(F.Factorial(F.Floor(j_1.plus(j_2).plus(j_3).plus(F.C1))), -1));
      // //
      // IExpr ressqrt = engine.evaluate(F.Sqrt(argsqrt));
      // // if ressqrt.is_complex or ressqrt.is_infinite:
      // // ressqrt = ressqrt.as_real_imag()[0]
      // //
      // IExpr imin = engine.evaluate(F.Floor(
      // F.Max(j_3.negate().plus(j_1).plus(m_2), j_3.negate().plus(j_2).subtract(m_1), F.C0)));
      // IExpr imax = engine.evaluate(
      // F.Floor(F.Min(j_2.plus(m_2), j_1.subtract(m_1), j_1.plus(j_2).subtract(j_3))));
      // int start = imin.toIntDefault();
      // int end = imax.toIntDefault() + 1;
      // if (start >= 0 && end >= start) {
      // IExpr sumres = F.C0;
      // for (int i = start; i < end; i++) {
      // IInteger ii = F.ZZ(i);
      // IExpr denominator = F.Times(F.Factorial(ii), //
      // F.Factorial(F.Floor(ii.plus(j_3).subtract(j_1).subtract(m_2))), //
      // F.Factorial(F.Floor(j_2.plus(m_2).subtract(ii))), //
      // F.Factorial(F.Floor(j_1.subtract(ii).subtract(m_1))), //
      // F.Factorial(F.Floor(ii.plus(j_3).subtract(j_2).plus(m_1))), //
      // F.Factorial(F.Floor(j_1.plus(j_2).subtract(j_3).subtract(ii))));
      // sumres = engine.evaluate(F.Divide(F.Plus(sumres, F.Power(F.CN1, i)), denominator));
      // }
      // // for ii in range(int(imin), int(imax) + 1):
      // // den = _Factlist[ii] * \
      // // _Factlist[int(ii + j_3 - j_1 - m_2)] * \
      // // _Factlist[int(j_2 + m_2 - ii)] * \
      // // _Factlist[int(j_1 - ii - m_1)] * \
      // // _Factlist[int(ii + j_3 - j_2 + m_1)] * \
      // // _Factlist[int(j_1 + j_2 - j_3 - ii)]
      // // sumres = sumres + Integer((-1) ** ii) / den
      // //
      // return F.Times(ressqrt, sumres, prefid);
      // }
    }
    return F.NIL;
  }

  /**
   * Return <code>0</code> and print the message
   * &quot;<code>ast.toString() is not physical</code>&quot;
   */
  private static IExpr zeroNotPhysical(final IBuiltInSymbol head, final IAST ast,
      EvalEngine engine) {
    // `1` is not physical.
    Errors.printMessage(head, "phy", F.List(ast), engine);
    return F.C0;
  }

  /**
   * Symbolic computation of the <code>ThreeJSymbol[{j1,m1},{j1,m1},{j1,m1}]</code>
   * 
   * @param j1
   * @param j2
   * @param j3
   * @param m1
   * @param m2
   * @param m3
   * @return
   */
  private static IExpr threeJSymbol(IExpr j1, IExpr j2, IExpr j3, IExpr m1, IExpr m2, IExpr m3,
      EvalEngine engine) {
    // (Sqrt((j1+j2-j3)!)*Sqrt((j1-j2+j3)!)*Sqrt((-j1+j2+j3)!)*Sqrt((j1-m1)!)*Sqrt((j1+m1)!)*Sqrt((j2-m2)!)*Sqrt((j2+m2)!)*Sqrt((j3-m3)!)*Sqrt((j3+m3)!)*KroneckerDelta(m1+m2,-m3)*
    // Sum((-1)^k/((j1+j2-j3-k)!*k!*(j1-k-m1)!*(-j2+j3+k+m1)!*(-j1+j3+k-m2)!*(j2-k+m2)!),
    // {k,Min(j1-m1,j2+m2),Max(0,j2-j3-m1,j1-j3+m2)}
    // )) / ((-1)^(j1-j2-m3)*Sqrt((1+j1+j2+j3)!))
    ISymbol k = F.Dummy('k');
    return engine.evaluate(//
        F.Times(F.Power(F.CN1, F.Plus(F.Negate(j1), j2, m3)),
            F.Sqrt(F.Factorial(F.Plus(j1, j2, F.Negate(j3)))),
            F.Sqrt(F.Factorial(F.Plus(j1, F.Negate(j2), j3))),
            F.Sqrt(F.Factorial(F.Plus(F.Negate(j1), j2, j3))),
            F.Power(F.Factorial(F.Plus(F.C1, j1, j2, j3)), F.CN1D2),
            F.Sqrt(F.Factorial(F.Subtract(j1, m1))), F.Sqrt(F.Factorial(F.Plus(j1, m1))),
            F.Sqrt(F.Factorial(F.Subtract(j2, m2))), F.Sqrt(F.Factorial(F.Plus(j2, m2))),
            F.Sqrt(F.Factorial(F.Subtract(j3, m3))), F.Sqrt(F.Factorial(F.Plus(j3, m3))),
            F.KroneckerDelta(F.Plus(m1, m2), F.Negate(m3)),
            F.Sum(F.Times(F.Power(F.CN1, k),
                F.Power(F.Times(F.Factorial(F.Plus(j1, j2, F.Negate(j3), F.Negate(k))),
                    F.Factorial(k), F.Factorial(F.Plus(j1, F.Negate(k), F.Negate(m1))),
                    F.Factorial(F.Plus(F.Negate(j2), j3, k, m1)),
                    F.Factorial(F.Plus(F.Negate(j1), j3, k, F.Negate(m2))),
                    F.Factorial(F.Plus(j2, F.Negate(k), m2))), F.CN1)),
                F.list(k, //
                    F.Max(F.C0, F.Plus(j2, F.Negate(j3), F.Negate(m1)),
                        F.Plus(j1, F.Negate(j3), m2)), //
                    F.Min(F.Subtract(j1, m1), F.Plus(j2, m2)))))//
    );

  }

  private static IExpr sixJSymbol(IExpr j1, IExpr j2, IExpr j3, IExpr j4, IExpr j5, IExpr j6,
      final IBuiltInSymbol head, final IAST ast, EvalEngine engine) {
    if (j1.isNumericFunction() && j2.isNumericFunction() && j3.isNumericFunction()
        && j4.isNumericFunction() && j5.isNumericFunction() && j6.isNumericFunction()) {
      if (j1.isNumber() && j2.isNumber() && j3.isNumber() && j4.isNumber() && j5.isNumber()
          && j6.isNumber()) {
        // https://functions.wolfram.com/HypergeometricFunctions/SixJSymbol/02/
        INumber j_1 = (INumber) j1;
        INumber j_2 = (INumber) j2;
        INumber j_3 = (INumber) j3;
        INumber m_1 = (INumber) j4;
        INumber m_2 = (INumber) j5;
        INumber m_3 = (INumber) j6;
        if (!j_1.times(F.C2).isInteger() //
            || !j_2.times(F.C2).isInteger() //
            || !j_3.times(F.C2).isInteger()) {
          return zeroNotPhysical(head, ast, engine);
        }
        if (!m_1.times(F.C2).isInteger() //
            || !m_2.times(F.C2).isInteger() //
            || !m_3.times(F.C2).isInteger()) {
          return zeroNotPhysical(head, ast, engine);
        }
        // https://en.wikipedia.org/wiki/3-j_symbol#Relation_to_Racah_V-coefficients
        // return F.Times(F.Power(F.CN1, j1.subtract(j2).subtract(j3)), //
        // F.ThreeJSymbol(F.List(j1, j4), F.List(j2, j5), F.List(j3, j6)));

        if (j1.isInteger() && j2.isInteger() && j3.isInteger() && j4.isInteger() && j5.isInteger()
            && j6.isInteger()) {
          return sixJSymbol(j1, j2, j3, j4, j5, j6, engine);
        }

      } else {
        return zeroNotPhysical(head, ast, engine);
      }
    }
    return F.NIL;
  }

  private static IExpr sixJSymbol(IExpr j1, IExpr j2, IExpr j12, IExpr j3, IExpr j, IExpr j23,
      EvalEngine engine) {
    // (Sqrt((j1+j2-j3)!)*Sqrt((j1-j2+j3)!)*Sqrt((-j1+j2+j3)!)*Sqrt((j1-m1)!)*Sqrt((j1+m1)!)*Sqrt((j2-m2)!)*Sqrt((j2+m2)!)*Sqrt((j3-m3)!)*Sqrt((j3+m3)!)*KroneckerDelta(m1+m2,-m3)*
    // Sum((-1)^k/((j1+j2-j3-k)!*k!*(j1-k-m1)!*(-j2+j3+k+m1)!*(-j1+j3+k-m2)!*(j2-k+m2)!),
    // {k,Min(j1-m1,j2+m2),Max(0,j2-j3-m1,j1-j3+m2)}
    // )) / ((-1)^(j1-j2-m3)*Sqrt((1+j1+j2+j3)!))
    ISymbol m = F.Dummy("m");
    m.assignValue(F.C0);
    ISymbol m1 = F.Dummy("m1");
    ISymbol m12 = F.Dummy("m12");
    ISymbol m2 = F.Dummy("m2");
    ISymbol m23 = F.Dummy("m23");
    ISymbol m3 = F.Dummy("m3");
    IExpr sum = engine.evalQuiet(F.Sum(
        F.Times(F.ClebschGordan(F.list(j12, m12), F.list(j3, m3), F.list(j, m)),
            F.ClebschGordan(F.list(j1, m1), F.list(j2, m2), F.list(j12, m12)),
            F.ClebschGordan(F.list(j1, m1), F.list(j23, m23), F.list(j, m)),
            F.ClebschGordan(F.list(j2, m2), F.list(j3, m3), F.list(j23, m23))),
        F.list(m1, j1.negate(), j1), F.list(m2, j2.negate(), j2), F.list(m3, j3.negate(), j3),
        F.list(m12, j12.negate(), j12), F.list(m23, j23.negate(), j23)));
    if (sum.isPresent()) {
      return engine.evalQuiet(//
          F.Together( //
              F.Times(F.Power(F.CN1, F.Plus(j1, j2, j3, j)),
                  F.Power(
                      F.Times(F.Plus(F.Times(F.C2, j12), F.C1), F.Plus(F.Times(F.C2, j23), F.C1)),
                      F.CN1D2), //
                  sum)) //
      );
    }
    return F.NIL;
  }

  public static void initialize() {
    Initializer.init();
  }

  private QuantumPhysicsFunctions() {}
}
