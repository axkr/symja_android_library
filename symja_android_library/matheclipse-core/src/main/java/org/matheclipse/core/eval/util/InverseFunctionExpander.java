package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ExprAnalyzer;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Expand the dummy {@link ExprAnalyzer#$InverseFunction} placeholder into the complete (periodic)
 * inverse function representation using {@link S#ConditionalExpression} and integer constants
 * <code>C[n]</code>.
 *
 * <p>
 * This is a neutral, low-level utility so that both {@link org.matheclipse.core.reflection.system.Solve}
 * and {@link org.matheclipse.core.reflection.system.Reduce} can reuse the same periodic inversion
 * without creating a dependency on the <code>Solve</code> evaluator.
 */
public class InverseFunctionExpander {

  /**
   * Build the {@link ExprAnalyzer#$InverseFunction} placeholder for the given periodic function
   * <code>head</code> and its <code>arg</code> and expand it into the complete (periodic) inverse
   * representation.
   *
   * @param head a periodic/invertible function head like {@link S#Sin}, {@link S#Cos},
   *        {@link S#Tan}, ...
   * @param arg the right-hand side value the function should be equal to
   * @return a {@link S#ConditionalExpression} or a {@link S#List} of
   *         {@link S#ConditionalExpression} branches, or {@link F#NIL} if <code>head</code> is not
   *         supported
   */
  public static IExpr expandPeriodicInverse(IBuiltInSymbol head, IExpr arg) {
    return substitute$InverseFunction(F.binaryAST2(ExprAnalyzer.$InverseFunction, head, arg));
  }

  /**
   * Substitute the dummy {@link ExprAnalyzer#$InverseFunction} in the <code>expr</code> with the
   * inverse function associated with the <code>symbol</code>.
   *
   * @param expr
   * @return
   */
  public static IExpr substitute$InverseFunction(IExpr expr) {
    if (expr.isAST2() && expr.head() == ExprAnalyzer.$InverseFunction) {
      EvalEngine engine = EvalEngine.get();
      IAST c_n = F.C(engine.incConstantCounter());
      try {
        IAST c1Integers = F.Element(c_n, S.Integers);
        IBuiltInSymbol symbol = (IBuiltInSymbol) expr.first();
        IExpr arg = expr.second();
        int headID = symbol.ordinal();
        switch (headID) {
          case ID.ArcCos:
            return F.ConditionalExpression(//
                F.Cos(arg), //
                F.Or(F.And(F.Equal(F.Re(arg), F.C0), F.GreaterEqual(F.Im(arg), F.C0)),
                    F.Less(F.C0, F.Re(arg), F.Pi),
                    F.And(F.Equal(F.Re(arg), F.Pi), F.LessEqual(F.Im(arg), F.C0))));
          case ID.ArcCot:
            return F.ConditionalExpression(//
                F.Cot(arg), //
                F.Or(F.And(F.Equal(F.Re(arg), F.CNPiHalf), F.Less(F.Im(arg), F.C0)),
                    F.And(F.Less(F.CNPiHalf, F.Re(arg), F.CPiHalf), F.Unequal(arg, F.C0)),
                    F.And(F.Equal(F.Re(arg), F.CPiHalf), F.GreaterEqual(F.Im(arg), F.C0))));
          case ID.ArcSin:
            return F.ConditionalExpression(//
                F.Sin(arg), //
                F.Or(F.And(F.Equal(F.Re(arg), F.CNPiHalf), F.GreaterEqual(F.Im(arg), F.C0)),
                    F.Less(F.CNPiHalf, F.Re(arg), F.CPiHalf),
                    F.And(F.Equal(F.Re(arg), F.CPiHalf), F.LessEqual(F.Im(arg), F.C0))));
          case ID.ArcTan:
            return F.ConditionalExpression(//
                F.Tan(arg), //
                F.Or(F.And(F.Equal(F.Re(arg), F.CNPiHalf), F.Less(F.Im(arg), F.C0)),
                    F.Less(F.CNPiHalf, F.Re(arg), F.CPiHalf),
                    F.And(F.Equal(F.Re(arg), F.CPiHalf), F.Greater(F.Im(arg), F.C0))));
          case ID.Cos:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Negate(F.ArcCos(arg)), F.Times(2, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcCos(arg), F.Times(2, S.Pi, c_n)), //
                    c1Integers));
          case ID.Cosh:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Negate(F.ArcCosh(arg)), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcCosh(arg), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers));
          case ID.Cot:
            return F.ConditionalExpression(//
                F.Plus(F.ArcCot(arg), F.Times(S.Pi, c_n)), //
                c1Integers);
          case ID.Coth:
            return F.ConditionalExpression(//
                F.Plus(F.ArcCoth(arg), F.Times(F.CI, S.Pi, c_n)), //
                c1Integers);
          case ID.Csc:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(S.Pi, F.Negate(F.ArcSin(F.Power(arg, F.CN1))), F.Times(2, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcSin(F.Power(arg, F.CN1)), F.Times(2, S.Pi, c_n)), //
                    c1Integers));
          case ID.Csch:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Times(F.CI, S.Pi), F.Negate(F.ArcSinh(F.Power(arg, F.CN1))),
                        F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcSinh(F.Power(arg, F.CN1)), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers));
          case ID.Log:
            IExpr imArg = F.Im(arg);
            return F.ConditionalExpression(//
                F.Power(S.E, arg), //
                F.And(F.Less(F.CNPi, imArg), F.LessEqual(imArg, S.Pi)));
          case ID.Sec:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Negate(F.ArcCos(F.Power(arg, F.CN1))), F.Times(2, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcCos(F.Power(arg, F.CN1)), F.Times(2, S.Pi, c_n)), //
                    c1Integers));
          case ID.Sech:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Negate(F.ArcCosh(F.Power(arg, F.CN1))), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcCosh(F.Power(arg, F.CN1)), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers));
          case ID.Sin:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(S.Pi, F.Negate(F.ArcSin(arg)), F.Times(2, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcSin(arg), F.Times(2, S.Pi, c_n)), //
                    c1Integers));
          case ID.Sinh:
            return F.List(//
                F.ConditionalExpression(//
                    F.Plus(F.Times(F.CI, S.Pi), F.Negate(F.ArcSinh(arg)),
                        F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers), //
                F.ConditionalExpression(//
                    F.Plus(F.ArcSinh(arg), F.Times(2, F.CI, S.Pi, c_n)), //
                    c1Integers));
          case ID.Tan:
            return F.ConditionalExpression(//
                F.Plus(F.ArcTan(arg), F.Times(S.Pi, c_n)), //
                c1Integers);
          case ID.Tanh:
            return F.ConditionalExpression(//
                F.Plus(F.ArcTanh(arg), F.Times(F.CI, S.Pi, c_n)), //
                c1Integers);
        }
      } finally {
        engine.decConstantCounter();
      }
    }
    return F.NIL;
  }
}
