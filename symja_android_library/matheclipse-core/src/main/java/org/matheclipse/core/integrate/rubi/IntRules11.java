package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C1DSqrt3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C6;
import static org.matheclipse.core.expression.F.C8;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.CN1D4;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CSqrt2;
import static org.matheclipse.core.expression.F.CSqrt3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.EllipticF;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules11 {
  public static IAST RULES =
      List(
          IIntegrate(221,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1D2), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          EllipticF(ArcSin(Times(Rt(Negate(b), C4), x, Power(Rt(a, C4), CN1))),
                              CN1),
                          Power(Times(Rt(a, C4), Rt(Negate(b), C4)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(Times(b, Power(a, CN1))), GtQ(a, C0)))),
          IIntegrate(222,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1D2), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, b), C2))),
                      Condition(
                          Simp(
                              Times(Sqrt(Plus(Negate(a), Times(q, Sqr(x)))),
                                  Sqrt(Times(Plus(a, Times(q, Sqr(x))), Power(q, CN1))),
                                  EllipticF(ArcSin(Times(x,
                                      Power(Times(Plus(a, Times(q, Sqr(x))),
                                          Power(Times(C2, q), CN1)), CN1D2))),
                                      C1D2),
                                  Power(
                                      Times(
                                          CSqrt2, Sqrt(Negate(a)), Sqrt(Plus(a,
                                              Times(b, Power(x, C4))))),
                                      CN1)),
                              x),
                          IntegerQ(q))),
                  And(FreeQ(List(a, b), x), LtQ(a, C0), GtQ(b, C0)))),
          IIntegrate(223,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1D2), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, b), C2))),
                      Simp(
                          Times(
                              Sqrt(
                                  Times(
                                      Subtract(a, Times(q,
                                          Sqr(x))),
                                      Power(Plus(a, Times(q, Sqr(x))), CN1))),
                              Sqrt(Times(Plus(a, Times(q, Sqr(x))), Power(q, CN1))),
                              EllipticF(ArcSin(Times(x,
                                  Power(Times(Plus(a, Times(q, Sqr(x))), Power(Times(C2, q), CN1)),
                                      CN1D2))),
                                  C1D2),
                              Power(
                                  Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C4)))),
                                      Sqrt(Times(a, Power(Plus(a, Times(q, Sqr(x))), CN1)))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b), x), LtQ(a, C0), GtQ(b, C0)))),
          IIntegrate(224,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C4))),
                  CN1D2), x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(b, Power(x, C4), Power(a, CN1)))),
                          Power(Plus(a, Times(b, Power(x, C4))), CN1D2)),
                      Integrate(Power(Plus(C1, Times(b, Power(x, C4), Power(a, CN1))), CN1D2),
                          x),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(Times(b, Power(a, CN1))), Not(GtQ(a, C0))))),
          IIntegrate(225,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C6))),
                  CN1D2), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, $($s("§numer"), Rt(Times(b, Power(a, CN1)), C3))), Set(s, $(
                              $s("§denom"), Rt(Times(b, Power(a, CN1)), C3)))),
                      Simp(
                          Times(
                              x, Plus(s, Times(r,
                                  Sqr(x))),
                              Sqrt(
                                  Times(
                                      Plus(Sqr(s), Times(CN1, r, s, Sqr(x)), Times(Sqr(r), Power(x,
                                          C4))),
                                      Power(Plus(s, Times(Plus(C1, CSqrt3), r, Sqr(x))), CN2))),
                              EllipticF(
                                  ArcCos(
                                      Times(
                                          Plus(s, Times(Subtract(C1, CSqrt3), r, Sqr(x))),
                                          Power(Plus(s, Times(Plus(C1, CSqrt3), r, Sqr(x))), CN1))),
                                  Times(C1D4, Plus(C2, CSqrt3))),
                              Power(
                                  Times(C2, Power(C3, C1D4), s,
                                      Sqrt(Plus(a, Times(b, Power(x, C6)))),
                                      Sqrt(
                                          Times(r, Sqr(x), Plus(s, Times(r, Sqr(x))),
                                              Power(Plus(s, Times(Plus(C1, CSqrt3), r, Sqr(x))),
                                                  CN2)))),
                                  CN1)),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(226,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C8))),
                  CN1D2), x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(Subtract(C1, Times(Rt(Times(b, Power(a, CN1)), C4), Sqr(x))),
                                  Power(Plus(a, Times(b, Power(x, C8))), CN1D2)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(Times(Plus(C1, Times(Rt(Times(b, Power(a, CN1)), C4), Sqr(x))),
                              Power(Plus(a, Times(b, Power(x, C8))), CN1D2)), x),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(
              227, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D4), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(C2, x, Power(Plus(a, Times(b, Sqr(
                          x))), CN1D4)), x),
                      Dist(a, Integrate(Power(Plus(a, Times(b, Sqr(x))), QQ(-5L, 4L)), x), x)),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), PosQ(Times(b, Power(a, CN1)))))),
          IIntegrate(
              228, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D4), x_Symbol),
              Condition(
                  Simp(
                      Times(C2,
                          EllipticE(Times(C1D2, C1,
                              ArcSin(Times(Rt(Times(CN1, b, Power(a, CN1)), C2), x))), C2),
                          Power(Times(Power(a, C1D4), Rt(Times(CN1, b, Power(a, CN1)), C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), NegQ(Times(b, Power(a, CN1)))))),
          IIntegrate(
              229, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D4), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))),
                          C1D4), Power(Plus(a, Times(b, Sqr(x))), CN1D4)),
                      Integrate(Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))), CN1D4), x), x),
                  And(FreeQ(List(a, b), x), PosQ(a)))),
          IIntegrate(
              230, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D4), x_Symbol),
              Condition(
                  Dist(
                      Times(C2, Sqrt(Times(CN1, b, Sqr(x),
                          Power(a, CN1))), Power(Times(b, x),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Sqr(x), Power(Subtract(C1, Times(Power(x, C4), Power(a, CN1))),
                                      CN1D2)),
                              x),
                          x, Power(Plus(a, Times(b, Sqr(x))), C1D4)),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(a)))),
          IIntegrate(231,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-3L,
                      4L)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C2,
                          EllipticF(
                              Times(C1D2, C1,
                                  ArcTan(Times(Rt(Times(b, Power(a, CN1)), C2), x))),
                              C2),
                          Power(Times(Power(a, QQ(3L, 4L)), Rt(Times(b, Power(a, CN1)), C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), PosQ(Times(b, Power(a, CN1)))))),
          IIntegrate(232,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-3L,
                      4L)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C2,
                          EllipticF(
                              Times(C1D2, C1, ArcSin(
                                  Times(Rt(Times(CN1, b, Power(a, CN1)), C2), x))),
                              C2),
                          Power(Times(Power(a, QQ(3L, 4L)), Rt(Times(CN1, b, Power(a, CN1)), C2)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), NegQ(Times(b, Power(a, CN1)))))),
          IIntegrate(233,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-3L,
                      4L)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))), QQ(3L,
                              4L)),
                          Power(Plus(a, Times(b, Sqr(x))), QQ(-3L, 4L))),
                      Integrate(Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))), QQ(-3L,
                          4L)), x),
                      x),
                  And(FreeQ(List(a, b), x), PosQ(a)))),
          IIntegrate(234,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-3L,
                      4L)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(C2, Sqrt(Times(CN1, b, Sqr(x), Power(a, CN1))), Power(Times(b, x),
                          CN1)),
                      Subst(
                          Integrate(Power(Subtract(C1, Times(Power(x, C4), Power(a, CN1))),
                              CN1D2), x),
                          x, Power(Plus(a, Times(b, Sqr(x))), C1D4)),
                      x),
                  And(FreeQ(List(a, b), x), NegQ(a)))),
          IIntegrate(
              235, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  CN1D3), x_Symbol),
              Condition(
                  Dist(
                      Times(C3, Sqrt(Times(b,
                          Sqr(x))), Power(Times(C2, b, x),
                              CN1)),
                      Subst(Integrate(Times(x, Power(Plus(Negate(a), Power(x, C3)), CN1D2)), x), x,
                          Power(Plus(a, Times(b, Sqr(x))), C1D3)),
                      x),
                  FreeQ(List(a, b), x))),
          IIntegrate(236,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-2L,
                      3L)),
                  x_Symbol),
              Condition(
                  Dist(Times(C3, Sqrt(Times(b, Sqr(x))), Power(Times(C2, b, x), CN1)),
                      Subst(Integrate(Power(Plus(Negate(a), Power(x, C3)), CN1D2), x), x,
                          Power(Plus(a, Times(b, Sqr(x))), C1D3)),
                      x),
                  FreeQ(List(a, b), x))),
          IIntegrate(237,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Power(x_, C4))), QQ(-3L,
                      4L)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(x, C3),
                          Power(Plus(C1, Times(a,
                              Power(Times(b, Power(x, C4)), CN1))), QQ(3L,
                                  4L)),
                          Power(Plus(a, Times(b, Power(x, C4))), QQ(-3L, 4L))),
                      Integrate(
                          Power(
                              Times(Power(x, C3),
                                  Power(
                                      Plus(C1, Times(a,
                                          Power(Times(b, Power(x, C4)), CN1))),
                                      QQ(3L, 4L))),
                              CN1),
                          x),
                      x),
                  FreeQ(List(a, b), x))),
          IIntegrate(238,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  QQ(-1L, 6L)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              C3, x, Power(Times(C2, Power(Plus(a, Times(b, Sqr(x))), QQ(1L, 6L))),
                                  CN1)),
                          x),
                      Dist(
                          Times(C1D2, a), Integrate(Power(Plus(a, Times(b,
                              Sqr(x))), QQ(-7L,
                                  6L)),
                              x),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(239,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))),
                  CN1D3), x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              ArcTan(Times(
                                  Plus(C1,
                                      Times(C2, Rt(b, C3), x,
                                          Power(Plus(a, Times(b, Power(x, C3))), CN1D3))),
                                  C1DSqrt3)),
                              Power(Times(CSqrt3, Rt(b, C3)), CN1)),
                          x),
                      Simp(
                          Times(
                              Log(Subtract(Power(Plus(a, Times(b, Power(x, C3))), C1D3),
                                  Times(Rt(b, C3), x))),
                              Power(Times(C2, Rt(b, C3)), CN1)),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(240, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(
                  Dist(Power(a, Plus(p, Power(n, CN1))),
                      Subst(
                          Integrate(Power(Power(Subtract(C1, Times(b, Power(x, n))),
                              Plus(p, Power(n, CN1), C1)), CN1), x),
                          x,
                          Times(x,
                              Power(Power(Plus(a, Times(b, Power(x, n))), Power(n, CN1)), CN1))),
                      x),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), LtQ(CN1, p, C0), NeQ(p, Negate(C1D2)),
                      IntegerQ(Plus(p, Power(n, CN1)))))));
}
