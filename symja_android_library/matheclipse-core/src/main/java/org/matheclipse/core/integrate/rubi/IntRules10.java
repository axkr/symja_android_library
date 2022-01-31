package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class IntRules10 {
  public static IAST RULES =
      List(
          IIntegrate(201,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Module(
                      List(Set(r, Numerator(Rt(Times(a, Power(b, CN1)), n))), Set(s,
                          Denominator(Rt(Times(a, Power(b, CN1)), n))), k, u),
                      Simp(
                          CompoundExpression(
                              Set(u,
                                  Integrate(Times(
                                      Subtract(r,
                                          Times(s,
                                              Cos(Times(Subtract(Times(C2, k), C1), Pi,
                                                  Power(n, CN1))),
                                              x)),
                                      Power(
                                          Plus(Sqr(r),
                                              Times(CN1, C2, r, s,
                                                  Cos(Times(Subtract(Times(C2, k), C1), Pi,
                                                      Power(n, CN1))),
                                                  x),
                                              Times(Sqr(s), Sqr(x))),
                                          CN1)),
                                      x)),
                              Plus(
                                  Times(r, Integrate(Power(Plus(r, Times(s, x)), CN1), x),
                                      Power(Times(a, n), CN1)),
                                  Dist(
                                      Times(C2, r, Power(Times(a, n), CN1)), Sum(u,
                                          List(k, C1, Times(C1D2, Subtract(n, C1)))),
                                      x))),
                          x)),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D2, Subtract(n, C3)), C0),
                      PosQ(Times(a, Power(b, CN1)))))),
          IIntegrate(202,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Module(
                      List(Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), n))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), n))), k, u),
                      Simp(
                          CompoundExpression(
                              Set(u,
                                  Integrate(Times(
                                      Plus(r,
                                          Times(s,
                                              Cos(Times(
                                                  Subtract(Times(C2, k), C1), Pi, Power(n, CN1))),
                                              x)),
                                      Power(
                                          Plus(Sqr(r),
                                              Times(C2, r, s,
                                                  Cos(Times(Subtract(Times(C2, k), C1), Pi,
                                                      Power(n, CN1))),
                                                  x),
                                              Times(Sqr(s), Sqr(x))),
                                          CN1)),
                                      x)),
                              Plus(
                                  Times(r, Integrate(Power(Subtract(r, Times(s, x)), CN1), x),
                                      Power(Times(a, n), CN1)),
                                  Dist(Times(C2, r, Power(Times(a, n), CN1)),
                                      Sum(u, List(k, C1, Times(C1D2, Subtract(n, C1)))), x))),
                          x)),
                  And(FreeQ(List(a,
                      b), x), IGtQ(Times(C1D2, Subtract(n, C3)),
                          C0),
                      NegQ(Times(a, Power(b, CN1)))))),
          IIntegrate(
              203, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          C1, ArcTan(Times(Rt(b, C2), x,
                              Power(Rt(a, C2), CN1))),
                          Power(Times(Rt(a, C2), Rt(b, C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), PosQ(Times(a, Power(b, CN1))),
                      Or(GtQ(a, C0), GtQ(b, C0))))),
          IIntegrate(
              204, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              ArcTan(Times(Rt(Negate(b), C2), x,
                                  Power(Rt(Negate(a), C2), CN1))),
                              Power(Times(Rt(Negate(a), C2), Rt(Negate(b), C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b), x), PosQ(Times(a, Power(b, CN1))),
                      Or(LtQ(a, C0), LtQ(b, C0))))),
          IIntegrate(205, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1), x_Symbol),
              Condition(
                  Simp(
                      Times(Rt(Times(a, Power(b, CN1)), C2),
                          ArcTan(Times(x, Power(Rt(Times(a, Power(b, CN1)), C2), CN1))), Power(a,
                              CN1)),
                      x),
                  And(FreeQ(List(a, b), x), PosQ(Times(a, Power(b, CN1)))))),
          IIntegrate(
              206, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          C1, ArcTanh(Times(Rt(Negate(b), C2), x, Power(Rt(a, C2), CN1))), Power(
                              Times(Rt(a, C2), Rt(Negate(b), C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(Times(a,
                      Power(b, CN1))), Or(GtQ(a, C0),
                          LtQ(b, C0))))),
          IIntegrate(
              207, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              ArcTanh(Times(Rt(b, C2), x, Power(Rt(Negate(a), C2), CN1))), Power(
                                  Times(Rt(Negate(a), C2), Rt(b, C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b), x), NegQ(Times(a, Power(b, CN1))),
                      Or(LtQ(a, C0), GtQ(b, C0))))),
          IIntegrate(
              208, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Rt(Times(CN1, a, Power(b,
                              CN1)), C2),
                          ArcTanh(Times(x, Power(Rt(Times(CN1, a, Power(b, CN1)), C2), CN1))),
                          Power(a, CN1)),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(Times(a, Power(b, CN1)))))),
          IIntegrate(209,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Module(
                      List(
                          Set(r, Numerator(
                              Rt(Times(a, Power(b, CN1)), n))),
                          Set(s, Denominator(Rt(Times(a, Power(b, CN1)), n))), k, u, v),
                      Simp(
                          CompoundExpression(
                              Set(u,
                                  Plus(
                                      Integrate(
                                          Times(
                                              Subtract(r, Times(s, Cos(Times(Subtract(Times(C2, k),
                                                  C1), Pi, Power(n, CN1))), x)),
                                              Power(Plus(Sqr(r),
                                                  Times(
                                                      CN1, C2, r, s, Cos(
                                                          Times(Subtract(Times(C2, k), C1), Pi,
                                                              Power(n, CN1))),
                                                      x),
                                                  Times(Sqr(s), Sqr(x))),
                                                  CN1)),
                                          x),
                                      Integrate(Times(
                                          Plus(
                                              r,
                                              Times(s,
                                                  Cos(Times(Subtract(Times(C2, k), C1), Pi,
                                                      Power(n, CN1))),
                                                  x)),
                                          Power(Plus(Sqr(r),
                                              Times(C2, r, s,
                                                  Cos(Times(Subtract(Times(C2, k), C1), Pi,
                                                      Power(n, CN1))),
                                                  x),
                                              Times(Sqr(s), Sqr(x))), CN1)),
                                          x))),
                              Plus(
                                  Times(C2, Sqr(r),
                                      Integrate(Power(Plus(Sqr(r), Times(Sqr(s), Sqr(x))), CN1), x),
                                      Power(Times(a, n), CN1)),
                                  Dist(
                                      Times(C2, r, Power(Times(a, n), CN1)), Sum(u,
                                          List(k, C1, Times(C1D4, Subtract(n, C2)))),
                                      x))),
                          x)),
                  And(FreeQ(List(a,
                      b), x), IGtQ(Times(C1D4, Subtract(n, C2)),
                          C0),
                      PosQ(Times(a, Power(b, CN1)))))),
          IIntegrate(210,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Module(
                      List(
                          Set(r, Numerator(
                              Rt(Times(CN1, a, Power(b, CN1)), n))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), n))), k, u),
                      Simp(
                          CompoundExpression(
                              Set(u,
                                  Plus(
                                      Integrate(
                                          Times(
                                              Subtract(r,
                                                  Times(s, Cos(Times(C2, k, Pi, Power(n, CN1))),
                                                      x)),
                                              Power(
                                                  Plus(
                                                      Sqr(r),
                                                      Times(CN1, C2, r, s,
                                                          Cos(Times(C2, k, Pi, Power(n, CN1))), x),
                                                      Times(Sqr(s), Sqr(x))),
                                                  CN1)),
                                          x),
                                      Integrate(Times(
                                          Plus(r,
                                              Times(s, Cos(Times(C2, k, Pi, Power(n, CN1))), x)),
                                          Power(Plus(Sqr(r),
                                              Times(C2, r, s, Cos(Times(C2, k, Pi, Power(n, CN1))),
                                                  x),
                                              Times(Sqr(s), Sqr(x))), CN1)),
                                          x))),
                              Plus(
                                  Times(C2, Sqr(r),
                                      Integrate(Power(Subtract(Sqr(r), Times(Sqr(s), Sqr(x))), CN1),
                                          x),
                                      Power(Times(a, n), CN1)),
                                  Dist(
                                      Times(C2, r, Power(Times(a, n), CN1)), Sum(u,
                                          List(k, C1, Times(C1D4, Subtract(n, C2)))),
                                      x))),
                          x)),
                  And(FreeQ(List(a,
                      b), x), IGtQ(Times(C1D4, Subtract(n, C2)),
                          C0),
                      NegQ(Times(a, Power(b, CN1)))))),
          IIntegrate(211,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(Rt(Times(a, Power(b, CN1)), C2))), Set(s,
                              Denominator(Rt(Times(a, Power(b, CN1)), C2)))),
                      Plus(
                          Dist(Power(Times(C2, r), CN1),
                              Integrate(Times(Subtract(r, Times(s, Sqr(x))),
                                  Power(Plus(a, Times(b, Power(x, C4))), CN1)), x),
                              x),
                          Dist(Power(Times(C2, r), CN1),
                              Integrate(
                                  Times(Plus(r, Times(s, Sqr(x))),
                                      Power(Plus(a, Times(b, Power(x, C4))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a,
                      b), x), Or(
                          GtQ(Times(a,
                              Power(b, CN1)), C0),
                          And(PosQ(Times(a, Power(b, CN1))),
                              AtomQ(SplitProduct($rubi("SumBaseQ"), a)),
                              AtomQ(SplitProduct($rubi("SumBaseQ"), b))))))),
          IIntegrate(212,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), C2))), Set(s,
                              Denominator(Rt(Times(CN1, a, Power(b, CN1)), C2)))),
                      Plus(
                          Dist(
                              Times(r, Power(Times(C2, a),
                                  CN1)),
                              Integrate(Power(Subtract(r, Times(s, Sqr(x))), CN1), x), x),
                          Dist(
                              Times(r, Power(Times(C2, a),
                                  CN1)),
                              Integrate(Power(Plus(r, Times(s, Sqr(x))), CN1), x), x))),
                  And(FreeQ(List(a, b), x), Not(GtQ(Times(a, Power(b, CN1)), C0))))),
          IIntegrate(213,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(a, Power(b, CN1)), C4))),
                          Set(s, Denominator(Rt(Times(a, Power(b, CN1)), C4)))),
                      Plus(Dist(Times(r, Power(Times(C2, CSqrt2, a), CN1)),
                          Integrate(Times(
                              Subtract(Times(CSqrt2, r), Times(s, Power(x, Times(C1D4, n)))),
                              Power(Plus(Sqr(r), Times(CN1, CSqrt2, r, s, Power(x, Times(C1D4, n))),
                                  Times(Sqr(s), Power(x, Times(C1D2, n)))), CN1)),
                              x),
                          x),
                          Dist(Times(r, Power(Times(C2, CSqrt2, a), CN1)),
                              Integrate(
                                  Times(Plus(Times(CSqrt2, r), Times(s, Power(x, Times(C1D4, n)))),
                                      Power(
                                          Plus(Sqr(r),
                                              Times(CSqrt2, r, s, Power(x, Times(C1D4, n))),
                                              Times(Sqr(s), Power(x, Times(C1D2, n)))),
                                          CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D4,
                      n), C1), GtQ(Times(a, Power(b, CN1)),
                          C0)))),
          IIntegrate(214,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(CN1, a, Power(b, CN1)), C2))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), C2)))),
                      Plus(
                          Dist(Times(r, Power(Times(C2, a), CN1)),
                              Integrate(Power(Subtract(r, Times(s, Power(x, Times(C1D2, n)))), CN1),
                                  x),
                              x),
                          Dist(Times(r, Power(Times(C2, a), CN1)),
                              Integrate(Power(Plus(r, Times(s, Power(x, Times(C1D2, n)))), CN1), x),
                              x))),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D4, n), C1),
                      Not(GtQ(Times(a, Power(b, CN1)), C0))))),
          IIntegrate(215, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1D2), x_Symbol),
              Condition(
                  Simp(Times(ArcSinh(Times(Rt(b, C2), x, Power(a, CN1D2))), Power(Rt(b, C2), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), PosQ(b)))),
          IIntegrate(
              216, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D2), x_Symbol),
              Condition(
                  Simp(Times(ArcSin(Times(Rt(Negate(b), C2), x, Power(a, CN1D2))),
                      Power(Rt(Negate(b), C2), CN1)), x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), NegQ(b)))),
          IIntegrate(
              217, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D2), x_Symbol),
              Condition(
                  Subst(
                      Integrate(Power(Subtract(C1, Times(b,
                          Sqr(x))), CN1), x),
                      x, Times(x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))),
                  And(FreeQ(List(a, b), x), Not(GtQ(a, C0))))),
          IIntegrate(218,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))),
                  CN1D2), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, $($s("§numer"),
                              Rt(Times(b, Power(a, CN1)), C3))),
                          Set(s, $($s("§denom"), Rt(Times(b, Power(a, CN1)), C3)))),
                      Simp(Times(C2, Sqrt(Plus(C2, CSqrt3)), Plus(s, Times(r, x)),
                          Sqrt(
                              Times(Plus(Sqr(s), Times(CN1, r, s, x), Times(Sqr(r), Sqr(x))), Power(
                                  Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)), CN2))),
                          EllipticF(
                              ArcSin(
                                  Times(
                                      Plus(Times(Subtract(C1,
                                          CSqrt3), s), Times(r,
                                              x)),
                                      Power(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)), CN1))),
                              Subtract(CN7, Times(C4, CSqrt3))),
                          Power(
                              Times(Power(C3, C1D4), r, Sqrt(Plus(a, Times(b, Power(x, C3)))),
                                  Sqrt(
                                      Times(s, Plus(s, Times(r, x)),
                                          Power(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)),
                                              CN2)))),
                              CN1)),
                          x)),
                  And(FreeQ(List(a, b), x), PosQ(a)))),
          IIntegrate(219,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2), x_Symbol),
              Condition(With(List(
                  Set(r, $($s("§numer"), Rt(Times(b, Power(a, CN1)), C3))),
                  Set(s, $($s("§denom"), Rt(Times(b, Power(a, CN1)), C3)))),
                  Simp(
                      Times(
                          C2, Sqrt(Subtract(C2, CSqrt3)), Plus(s, Times(r,
                              x)),
                          Sqrt(
                              Times(Plus(Sqr(s), Times(CN1, r, s, x), Times(Sqr(r),
                                  Sqr(x))), Power(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                      CN2))),
                          EllipticF(
                              ArcSin(
                                  Times(
                                      Plus(Times(Plus(C1, CSqrt3), s), Times(r,
                                          x)),
                                      Power(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                          CN1))),
                              Plus(CN7, Times(C4, CSqrt3))),
                          Power(
                              Times(Power(C3, C1D4), r, Sqrt(Plus(a, Times(b, Power(x, C3)))),
                                  Sqrt(Times(CN1, s, Plus(s, Times(r, x)),
                                      Power(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                          CN2)))),
                              CN1)),
                      x)),
                  And(FreeQ(List(a, b), x), NegQ(a)))),
          IIntegrate(220,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))), CN1D2), x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Times(b, Power(a, CN1)), C4))),
                      Simp(Times(Plus(C1, Times(Sqr(q), Sqr(x))),
                          Sqrt(Times(Plus(a, Times(b, Power(x, C4))),
                              Power(Times(a, Sqr(Plus(C1, Times(Sqr(q), Sqr(x))))), CN1))),
                          EllipticF(Times(C2, ArcTan(Times(q, x))), C1D2),
                          Power(Times(C2, q, Sqrt(Plus(a, Times(b, Power(x, C4))))), CN1)), x)),
                  And(FreeQ(List(a, b), x), PosQ(Times(b, Power(a, CN1)))))));
}
