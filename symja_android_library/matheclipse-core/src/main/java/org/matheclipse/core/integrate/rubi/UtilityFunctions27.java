package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions27 {
  public static IAST RULES =
      List(
          ISetDelayed(475,
              SimplifyAntiderivative(
                  ArcTan(Plus(a_DEFAULT, Times(Tan(u_), b_DEFAULT),
                      Times(Sqr(Tan(u_)), c_DEFAULT))),
                  x_Symbol),
              Condition(If(EvenQ(Denominator(NumericFactor(Together(u)))),
                  ArcTan(NormalizeTogether(Times(
                      Plus(a, c, CN1, Times(Subtract(Subtract(a, c), C1), Cos(Times(C2, u))),
                          Times(b, Sin(Times(C2, u)))),
                      Power(Plus(a, c, C1, Times(Plus(a, Negate(c), C1), Cos(Times(C2, u))),
                          Times(b, Sin(Times(C2, u)))), CN1)))),
                  ArcTan(NormalizeTogether(Times(
                      Plus(c, Times(Subtract(Subtract(a, c), C1), Sqr(Cos(u))),
                          Times(b, Cos(u), Sin(u))),
                      Power(Plus(c, Times(Plus(a, Negate(c), C1), Sqr(Cos(u))),
                          Times(b, Cos(u), Sin(u))), CN1))))),
                  And(FreeQ(List(a, b, c), x), ComplexFreeQ(u)))),
          ISetDelayed(476,
              SimplifyAntiderivative(
                  ArcTan(Plus(a_DEFAULT,
                      Times(b_DEFAULT, Plus(d_DEFAULT, Times(Tan(u_), e_DEFAULT))), Times(c_DEFAULT,
                          Sqr(Plus(f_DEFAULT, Times(Tan(u_), g_DEFAULT)))))),
                  x_Symbol),
              Condition(
                  SimplifyAntiderivative(
                      ArcTan(
                          Plus(a, Times(b, d), Times(c, Sqr(f)),
                              Times(Plus(Times(b, e),
                                  Times(C2, c, f, g)), Tan(u)),
                              Times(c, Sqr(g), Sqr(Tan(u))))),
                      x),
                  And(FreeQ(List(a, b, c), x), ComplexFreeQ(u)))),
          ISetDelayed(477,
              SimplifyAntiderivative(ArcTan(Plus(a_DEFAULT, Times(Sqr(Tan(u_)), c_DEFAULT))),
                  x_Symbol),
              Condition(
                  If(EvenQ(Denominator(NumericFactor(Together(u)))),
                      ArcTan(NormalizeTogether(Times(
                          Plus(a, c, CN1, Times(Subtract(Subtract(a, c), C1), Cos(Times(C2, u)))),
                          Power(Plus(a, c, C1, Times(Plus(a, Negate(c), C1), Cos(Times(C2, u)))),
                              CN1)))),
                      ArcTan(NormalizeTogether(
                          Times(Plus(c, Times(Subtract(Subtract(a, c), C1), Sqr(Cos(u)))),
                              Power(Plus(c, Times(Plus(a, Negate(c), C1), Sqr(Cos(u)))), CN1))))),
                  And(FreeQ(List(a, c), x), ComplexFreeQ(u)))),
          ISetDelayed(478,
              SimplifyAntiderivative(
                  ArcTan(
                      Plus(a_DEFAULT, Times(c_DEFAULT,
                          Sqr(Plus(f_DEFAULT, Times(Tan(u_), g_DEFAULT)))))),
                  x_Symbol),
              Condition(
                  SimplifyAntiderivative(
                      ArcTan(Plus(a, Times(c, Sqr(f)), Times(C2, c, f, g, Tan(u)),
                          Times(c, Sqr(g), Sqr(Tan(u))))),
                      x),
                  And(FreeQ(List(a, c), x), ComplexFreeQ(u)))),
          ISetDelayed(479, SimplifyAntiderivative(u_, x_Symbol),
              If(FreeQ(u, x), C0, If(LogQ(u), Log(RemoveContent(Part(u, C1), x)),
                  If(SumQ(u),
                      SimplifyAntiderivativeSum(Map(Function(SimplifyAntiderivative(Slot1, x)), u),
                          x),
                      u)))),
          ISetDelayed(480,
              SimplifyAntiderivativeSum(
                  Plus(
                      Times(Log(
                          Plus(a_, Times(Power(Tan(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(Cos(u_)), B_DEFAULT), v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(ASymbol,
                          Log(RemoveContent(Plus(Times(a, Power(Cos(u), n)),
                              Times(b, Power(Sin(u), n))), x)))),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x), IntegerQ(n),
                      EqQ(Subtract(Times(n, ASymbol), BSymbol), C0)))),
          ISetDelayed(481,
              SimplifyAntiderivativeSum(
                  Plus(
                      Times(Log(
                          Plus(a_, Times(Power(Cot(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(Sin(u_)), B_DEFAULT), v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(ASymbol,
                          Log(RemoveContent(Plus(Times(a, Power(Sin(u), n)),
                              Times(b, Power(Cos(u), n))), x)))),
                  And(FreeQ(List(a, b, ASymbol,
                      BSymbol), x), IntegerQ(
                          n),
                      EqQ(Subtract(Times(n, ASymbol), BSymbol), C0)))),
          ISetDelayed(482,
              SimplifyAntiderivativeSum(
                  Plus(Times(Log(Plus(a_, Times(Power(Tan(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(Plus(c_, Times(Power(Tan(u_), n_DEFAULT), d_DEFAULT))),
                          B_DEFAULT),
                      v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(
                          ASymbol, Log(
                              RemoveContent(
                                  Plus(Times(a, Power(Cos(u), n)), Times(b, Power(Sin(u), n))),
                                  x))),
                      Times(BSymbol,
                          Log(RemoveContent(
                              Plus(Times(c, Power(Cos(u), n)), Times(d, Power(Sin(u), n))), x)))),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol), x), IntegerQ(n),
                      EqQ(Plus(ASymbol, BSymbol), C0)))),
          ISetDelayed(483,
              SimplifyAntiderivativeSum(
                  Plus(Times(Log(Plus(a_, Times(Power(Cot(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(Plus(c_, Times(Power(Cot(u_), n_DEFAULT), d_DEFAULT))),
                          B_DEFAULT),
                      v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(
                          ASymbol, Log(
                              RemoveContent(
                                  Plus(Times(b, Power(Cos(u), n)), Times(a, Power(Sin(u), n))),
                                  x))),
                      Times(
                          BSymbol, Log(
                              RemoveContent(
                                  Plus(Times(d, Power(Cos(u), n)),
                                      Times(c, Power(Sin(u), n))),
                                  x)))),
                  And(FreeQ(List(a, b, c, d, ASymbol,
                      BSymbol), x), IntegerQ(
                          n),
                      EqQ(Plus(ASymbol, BSymbol), C0)))),
          ISetDelayed(484,
              SimplifyAntiderivativeSum(
                  Plus(Times(Log(Plus(a_, Times(Power(Tan(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(Plus(c_, Times(Power(Tan(u_), n_DEFAULT), d_DEFAULT))), B_DEFAULT),
                      Times(Log(Plus(e_, Times(Power(Tan(u_), n_DEFAULT), f_DEFAULT))),
                          C_DEFAULT),
                      v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(ASymbol,
                          Log(RemoveContent(Plus(Times(a, Power(Cos(u), n)),
                              Times(b, Power(Sin(u), n))), x))),
                      Times(BSymbol,
                          Log(RemoveContent(Plus(Times(c, Power(Cos(u), n)),
                              Times(d, Power(Sin(u), n))), x))),
                      Times(
                          CSymbol, Log(
                              RemoveContent(
                                  Plus(Times(e, Power(Cos(u), n)), Times(f, Power(Sin(u), n))),
                                  x)))),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol,
                      CSymbol), x), IntegerQ(
                          n),
                      EqQ(Plus(ASymbol, BSymbol, CSymbol), C0)))),
          ISetDelayed(485,
              SimplifyAntiderivativeSum(
                  Plus(
                      Times(Log(
                          Plus(a_, Times(Power(Cot(u_), n_DEFAULT), b_DEFAULT))), A_DEFAULT),
                      Times(Log(
                          Plus(c_, Times(Power(Cot(u_), n_DEFAULT), d_DEFAULT))), B_DEFAULT),
                      Times(Log(Plus(e_, Times(Power(Cot(u_), n_DEFAULT), f_DEFAULT))), C_DEFAULT),
                      v_DEFAULT),
                  x_Symbol),
              Condition(
                  Plus(SimplifyAntiderivativeSum(v, x),
                      Times(ASymbol,
                          Log(RemoveContent(
                              Plus(Times(b, Power(Cos(u), n)), Times(a, Power(Sin(u), n))), x))),
                      Times(BSymbol,
                          Log(RemoveContent(
                              Plus(Times(d, Power(Cos(u), n)), Times(c, Power(Sin(u), n))), x))),
                      Times(CSymbol,
                          Log(RemoveContent(
                              Plus(Times(f, Power(Cos(u), n)), Times(e, Power(Sin(u), n))), x)))),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol), x), IntegerQ(n),
                      EqQ(Plus(ASymbol, BSymbol, CSymbol), C0)))),
          ISetDelayed(486, SimplifyAntiderivativeSum(u_, x_Symbol), u),
          ISetDelayed(487, RectifyTangent(u_, a_, b_, x_Symbol), If(
              MatchQ(Together(a), Times(d_DEFAULT, Complex(C0, c_))),
              Module(List(Set(c, Times(a, Power(CI, CN1))), e), If(LtQ(c, C0),
                  RectifyTangent(u, Negate(a), Negate(b), x),
                  If(EqQ(c, C1), If(EvenQ(Denominator(NumericFactor(Together(u)))),
                      Times(CI, b, C1D2, ArcTanh(Sin(Times(C2, u)))),
                      Times(CI, b, C1D2, ArcTanh(Times(C2, Cos(u), Sin(u))))),
                      CompoundExpression(Set(e, SmartDenominator(c)), Set(c, Times(c, e)), Subtract(
                          Times(CI, b, C1D2,
                              Log(RemoveContent(Plus(Times(e, Cos(u)), Times(c, Sin(u))), x))),
                          Times(CI, b, C1D2, Log(RemoveContent(
                              Subtract(Times(e, Cos(u)), Times(c, Sin(u))), x)))))))),
              If(LtQ(a, C0), RectifyTangent(u, Negate(a), Negate(b), x),
                  If(EqQ(a, C1), Times(b, SimplifyAntiderivative(u, x)),
                      Module(
                          List(c, $s("numr"), $s("denr")), If(
                              EvenQ(Denominator(
                                  NumericFactor(Together(u)))),
                              CompoundExpression(
                                  Set(c, Simplify(Times(Plus(C1, a), Power(Subtract(C1, a), CN1)))),
                                  Set($s("numr"), SmartNumerator(c)),
                                  Set($s("denr"), SmartDenominator(
                                      c)),
                                  Subtract(
                                      Times(b, SimplifyAntiderivative(u,
                                          x)),
                                      Times(b,
                                          ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Sin(Times(C2, u)),
                                              Power(
                                                  Plus($s("numr"),
                                                      Times($s("denr"), Cos(Times(C2, u)))),
                                                  CN1))))))),
                              If(GtQ(a, C1),
                                  CompoundExpression(
                                      Set(c, Simplify(Power(Subtract(a, C1),
                                          CN1))),
                                      Set($s("numr"), SmartNumerator(c)),
                                      Set($s("denr"), SmartDenominator(
                                          c)),
                                      Plus(Times(b, SimplifyAntiderivative(u, x)),
                                          Times(b, ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Cos(u), Sin(u),
                                              Power(
                                                  Plus($s("numr"), Times($s("denr"), Sqr(Sin(u)))),
                                                  CN1))))))),
                                  CompoundExpression(
                                      Set(c, Simplify(Times(a, Power(Subtract(C1, a), CN1)))),
                                      Set($s("numr"), SmartNumerator(c)),
                                      Set($s("denr"), SmartDenominator(
                                          c)),
                                      Subtract(
                                          Times(b, SimplifyAntiderivative(u,
                                              x)),
                                          Times(b, ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Cos(u), Sin(u),
                                              Power(
                                                  Plus($s("numr"), Times($s("denr"), Sqr(Cos(u)))),
                                                  CN1)))))))))))))),
          ISetDelayed(
              488, RectifyTangent(u_, a_, b_, r_, x_Symbol), If(
                  And(MatchQ(Together(a), Times(d_DEFAULT,
                      Complex(C0, c_))), MatchQ(Together(b),
                          Times(d_DEFAULT, Complex(C0, c_)))),
                  Module(
                      List(Set(c, Times(a,
                          Power(CI, CN1))), Set(d,
                              Times(b, Power(CI, CN1))),
                          e),
                      If(LtQ(d, C0), RectifyTangent(u, Negate(a), Negate(b), Negate(r), x),
                          CompoundExpression(
                              Set(e, SmartDenominator(Together(
                                  Plus(c, Times(d, x))))),
                              Set(c, Times(c, e)), Set(d, Times(d,
                                  e)),
                              If(EvenQ(Denominator(NumericFactor(Together(u)))),
                                  Subtract(
                                      Times(
                                          CI, r, C1D4, Log(
                                              RemoveContent(
                                                  Plus(Simplify(Plus(Sqr(Plus(c, e)), Sqr(d))),
                                                      Times(
                                                          Simplify(
                                                              Subtract(Sqr(Plus(c, e)), Sqr(d))),
                                                          Cos(Times(C2, u))),
                                                      Times(
                                                          Simplify(Times(C2, Plus(c, e), d)), Sin(
                                                              Times(C2, u)))),
                                                  x))),
                                      Times(CI, r, C1D4,
                                          Log(RemoveContent(
                                              Plus(Simplify(Plus(Sqr(Subtract(c, e)), Sqr(d))),
                                                  Times(Simplify(
                                                      Subtract(Sqr(Subtract(c, e)), Sqr(d))),
                                                      Cos(Times(C2, u))),
                                                  Times(
                                                      Simplify(Times(C2, Subtract(c, e), d)), Sin(
                                                          Times(C2, u)))),
                                              x)))),
                                  Subtract(
                                      Times(
                                          CI, r, C1D4, Log(
                                              RemoveContent(
                                                  Subtract(
                                                      Plus(Simplify(Sqr(Plus(c, e))),
                                                          Times(Simplify(Times(C2, Plus(c, e), d)),
                                                              Cos(u), Sin(u))),
                                                      Times(
                                                          Simplify(
                                                              Subtract(Sqr(Plus(c, e)), Sqr(d))),
                                                          Sqr(Sin(u)))),
                                                  x))),
                                      Times(CI, r, C1D4,
                                          Log(RemoveContent(Subtract(
                                              Plus(
                                                  Simplify(Sqr(Subtract(c, e))),
                                                  Times(Simplify(Times(C2, Subtract(c, e), d)),
                                                      Cos(u), Sin(u))),
                                              Times(Simplify(Subtract(Sqr(Subtract(c, e)), Sqr(d))),
                                                  Sqr(Sin(u)))),
                                              x)))))))),
                  If(LtQ(b, C0), RectifyTangent(u, Negate(a), Negate(b), Negate(r), x), If(
                      EvenQ(Denominator(NumericFactor(Together(
                          u)))),
                      Plus(
                          Times(r, SimplifyAntiderivative(u,
                              x)),
                          Times(r,
                              ArcTan(Simplify(Times(
                                  Subtract(Times(C2, a, b, Cos(Times(C2, u))),
                                      Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)), Sin(Times(C2, u)))),
                                  Power(Plus(Sqr(a), Sqr(Plus(C1, b)),
                                      Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)), Cos(Times(C2, u))),
                                      Times(C2, a, b, Sin(Times(C2, u)))), CN1)))))),
                      Subtract(Times(r, SimplifyAntiderivative(u, x)),
                          Times(r,
                              ArcTan(ActivateTrig(Simplify(Times(
                                  Plus(Times(a, b), Times(CN1, C2, a, b, Sqr($($s("§cos"), u))),
                                      Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)), $($s("§cos"), u),
                                          $($s("§sin"), u))),
                                  Power(
                                      Plus(Times(b, Plus(C1, b)),
                                          Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)),
                                              Sqr($($s("§cos"), u))),
                                          Times(C2, a, b, $($s("§cos"), u), $($s("§sin"), u))),
                                      CN1))))))))))),
          ISetDelayed(489, RectifyCotangent(u_, a_, b_, x_Symbol), If(
              MatchQ(Together(a), Times(d_DEFAULT, Complex(C0, c_))),
              Module(List(Set(c, Times(a, Power(CI, CN1))), e), If(LtQ(c, C0),
                  RectifyCotangent(u, Negate(a), Negate(b), x),
                  If(EqQ(c, C1), If(EvenQ(Denominator(NumericFactor(Together(u)))),
                      Times(CN1, CI, b, C1D2, ArcTanh(Sin(Times(C2, u)))),
                      Times(CN1, CI, b, C1D2, ArcTanh(Times(C2, Cos(u), Sin(u))))),
                      CompoundExpression(Set(e, SmartDenominator(c)), Set(c, Times(c, e)), Plus(
                          Times(CN1, CI, b, C1D2,
                              Log(RemoveContent(Plus(Times(c, Cos(u)), Times(e, Sin(u))), x))),
                          Times(CI, b, C1D2, Log(RemoveContent(
                              Subtract(Times(c, Cos(u)), Times(e, Sin(u))), x)))))))),
              If(LtQ(a, C0), RectifyCotangent(u, Negate(a), Negate(b), x),
                  If(EqQ(a, C1), Times(b, SimplifyAntiderivative(u, x)),
                      Module(
                          List(c, $s("numr"), $s("denr")), If(
                              EvenQ(Denominator(
                                  NumericFactor(Together(u)))),
                              CompoundExpression(
                                  Set(c, Simplify(Times(Plus(C1, a), Power(Subtract(C1, a), CN1)))),
                                  Set($s("numr"), SmartNumerator(c)),
                                  Set($s("denr"), SmartDenominator(c)),
                                  Plus(Times(b, SimplifyAntiderivative(u, x)),
                                      Times(b,
                                          ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Sin(Times(C2, u)),
                                              Power(
                                                  Subtract($s("numr"),
                                                      Times($s("denr"), Cos(Times(C2, u)))),
                                                  CN1))))))),
                              If(GtQ(a, C1),
                                  CompoundExpression(Set(c, Simplify(Power(Subtract(a, C1), CN1))),
                                      Set($s("numr"), SmartNumerator(c)), Set($s(
                                          "denr"), SmartDenominator(c)),
                                      Subtract(Times(b, SimplifyAntiderivative(u, x)),
                                          Times(b, ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Cos(u), Sin(u),
                                              Power(
                                                  Plus($s("numr"), Times($s("denr"), Sqr(Cos(u)))),
                                                  CN1))))))),
                                  CompoundExpression(
                                      Set(c, Simplify(Times(a, Power(Subtract(C1, a), CN1)))),
                                      Set($s("numr"), SmartNumerator(c)),
                                      Set($s("denr"), SmartDenominator(
                                          c)),
                                      Plus(
                                          Times(b, SimplifyAntiderivative(u,
                                              x)),
                                          Times(b, ArcTan(NormalizeLeadTermSigns(Times($s("denr"),
                                              Cos(u), Sin(u),
                                              Power(
                                                  Plus($s("numr"), Times($s("denr"), Sqr(Sin(u)))),
                                                  CN1)))))))))))))),
          ISetDelayed(
              490, RectifyCotangent(u_, a_, b_, r_, x_Symbol), If(
                  And(MatchQ(Together(a), Times(d_DEFAULT,
                      Complex(C0, c_))), MatchQ(Together(b),
                          Times(d_DEFAULT, Complex(C0, c_)))),
                  Module(
                      List(Set(c, Times(a,
                          Power(CI, CN1))), Set(d,
                              Times(b, Power(CI, CN1))),
                          e),
                      If(LtQ(d, C0), RectifyTangent(u, Negate(a), Negate(b), Negate(r), x),
                          CompoundExpression(
                              Set(e, SmartDenominator(Together(
                                  Plus(c, Times(d, x))))),
                              Set(c, Times(c, e)), Set(d, Times(d,
                                  e)),
                              If(EvenQ(Denominator(NumericFactor(Together(u)))),
                                  Subtract(Times(CI, r, C1D4, Log(
                                      RemoveContent(
                                          Plus(Simplify(Plus(Sqr(Plus(c, e)), Sqr(d))), Times(CN1,
                                              Simplify(Subtract(Sqr(Plus(c, e)), Sqr(d))), Cos(
                                                  Times(C2, u))),
                                              Times(Simplify(Times(C2, Plus(c, e), d)),
                                                  Sin(Times(C2, u)))),
                                          x))),
                                      Times(CI, r, C1D4,
                                          Log(RemoveContent(
                                              Plus(Simplify(Plus(Sqr(Subtract(c, e)), Sqr(d))),
                                                  Times(CN1,
                                                      Simplify(Subtract(Sqr(Subtract(c, e)),
                                                          Sqr(d))),
                                                      Cos(Times(C2, u))),
                                                  Times(
                                                      Simplify(Times(C2, Subtract(c, e),
                                                          d)),
                                                      Sin(Times(C2, u)))),
                                              x)))),
                                  Subtract(
                                      Times(
                                          CI, r, C1D4, Log(
                                              RemoveContent(
                                                  Plus(Simplify(Sqr(Plus(c, e))),
                                                      Times(CN1,
                                                          Simplify(
                                                              Subtract(Sqr(Plus(c, e)), Sqr(d))),
                                                          Sqr(Cos(u))),
                                                      Times(Simplify(Times(C2, Plus(c, e), d)),
                                                          Cos(u), Sin(u))),
                                                  x))),
                                      Times(CI, r, C1D4, Log(RemoveContent(Plus(
                                          Simplify(Sqr(Subtract(c, e))),
                                          Times(CN1,
                                              Simplify(Subtract(Sqr(Subtract(c, e)), Sqr(d))),
                                              Sqr(Cos(u))),
                                          Times(
                                              Simplify(Times(C2, Subtract(c, e), d)), Cos(u),
                                              Sin(u))),
                                          x)))))))),
                  If(LtQ(b, C0), RectifyCotangent(u, Negate(a), Negate(b), Negate(r), x), If(
                      EvenQ(Denominator(
                          NumericFactor(Together(u)))),
                      Subtract(Times(CN1, r, SimplifyAntiderivative(u, x)),
                          Times(r, ArcTan(Simplify(Times(
                              Plus(Times(C2, a, b, Cos(Times(C2, u))),
                                  Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)), Sin(Times(C2, u)))),
                              Power(
                                  Plus(Sqr(a), Sqr(Plus(C1, b)),
                                      Times(CN1, Subtract(Plus(C1, Sqr(a)), Sqr(b)),
                                          Cos(Times(C2, u))),
                                      Times(C2, a, b, Sin(Times(C2, u)))),
                                  CN1)))))),
                      Subtract(Times(CN1, r, SimplifyAntiderivative(u, x)),
                          Times(r,
                              ArcTan(ActivateTrig(Simplify(Times(
                                  Plus(Times(a, b), Times(CN1, C2, a, b, Sqr($($s("§sin"), u))),
                                      Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)), $($s("§cos"), u),
                                          $($s("§sin"), u))),
                                  Power(
                                      Plus(Times(b, Plus(C1, b)),
                                          Times(Subtract(Plus(C1, Sqr(a)), Sqr(b)),
                                              Sqr($($s("§sin"), u))),
                                          Times(C2, a, b, $($s("§cos"), u), $($s("§sin"), u))),
                                      CN1))))))))))),
          ISetDelayed(491, SmartNumerator(Power(u_, n_)),
              Condition(SmartDenominator(Power(u, Negate(n))), And(RationalQ(n), Less(n, C0)))),
          ISetDelayed(492, SmartNumerator(Times(u_, v_)),
              Times(SmartNumerator(u), SmartNumerator(v))),
          ISetDelayed(493, SmartNumerator(u_), Numerator(u)));
}
