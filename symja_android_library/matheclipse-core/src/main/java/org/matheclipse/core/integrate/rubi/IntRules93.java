package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN7;
import static org.matheclipse.core.expression.F.CSqrt3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules93 {
  public static IAST RULES =
      List(
          IIntegrate(1861,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), C3))), Set(s,
                          Denominator(Rt(Times(CN1, a, Power(b, CN1)), C3)))),
                      Subtract(
                          Dist(
                              Times(
                                  r, Plus(Times(BSymbol, r),
                                      Times(ASymbol, s)),
                                  Power(Times(C3, a, s), CN1)),
                              Integrate(Power(Subtract(r, Times(s, x)), CN1), x), x),
                          Dist(
                              Times(r, Power(Times(C3, a, s),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Subtract(
                                          Times(r,
                                              Subtract(Times(BSymbol, r), Times(C2, ASymbol, s))),
                                          Times(s, Plus(Times(BSymbol, r), Times(ASymbol, s)), x)),
                                      Power(Plus(Sqr(r), Times(r, s, x), Times(Sqr(s), Sqr(x))),
                                          CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(a, Power(BSymbol, C3)), Times(b, Power(ASymbol, C3))), C0),
                      NegQ(Times(a, Power(b, CN1)))))),
          IIntegrate(1862,
              Integrate(
                  Times($p("§p2"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                      Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          Negate(Dist(Times(Sqr(CSymbol), Power(b, CN1)),
                              Integrate(Power(Subtract(BSymbol, Times(CSymbol, x)), CN1), x), x)),
                          And(EqQ(Subtract(Sqr(BSymbol), Times(ASymbol, CSymbol)), C0),
                              EqQ(Plus(Times(b, Power(BSymbol, C3)), Times(a, Power(CSymbol, C3))),
                                  C0)))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1863,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol,
                              Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(List(Set(q, Times(Power(a, C1D3), Power(b, CN1D3)))),
                              Plus(
                                  Dist(Times(CSymbol, Power(b, CN1)),
                                      Integrate(Power(Plus(q, x), CN1), x), x),
                                  Dist(Times(Plus(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(
                                          Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1), x),
                                      x))),
                          EqQ(Subtract(
                              Subtract(Times(ASymbol, Power(b, QQ(2L, 3L))),
                                  Times(Power(a, C1D3), Power(b, C1D3), BSymbol)),
                              Times(C2, Power(a, QQ(2L, 3L)), CSymbol)), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1864,
              Integrate(Times($p("§p2"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(With(
                  List(Set(ASymbol, Coeff($s("§p2"), x, C0)), Set(BSymbol, Coeff($s("§p2"), x, C1)),
                      Set(CSymbol, Coeff($s("§p2"), x, C2))),
                  Condition(
                      With(List(Set(q, Times(Power(Negate(a), C1D3), Power(Negate(b), CN1D3)))),
                          Plus(
                              Dist(Times(CSymbol, Power(b, CN1)),
                                  Integrate(Power(Plus(q, x), CN1), x), x),
                              Dist(Times(Plus(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                  Integrate(Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1), x),
                                  x))),
                      EqQ(Subtract(
                          Subtract(
                              Times(ASymbol, Power(Negate(b), QQ(2L,
                                  3L))),
                              Times(Power(Negate(a), C1D3), Power(Negate(b), C1D3), BSymbol)),
                          Times(C2, Power(Negate(a), QQ(2L, 3L)), CSymbol)), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1865,
              Integrate(Times($p("§p2"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                      Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(List(Set(q, Times(Power(Negate(a), C1D3), Power(b, CN1D3)))),
                              Plus(
                                  Negate(
                                      Dist(Times(CSymbol, Power(b, CN1)),
                                          Integrate(Power(Subtract(q, x), CN1), x), x)),
                                  Dist(Times(Subtract(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1), x),
                                      x))),
                          EqQ(Subtract(Plus(
                              Times(ASymbol, Power(b, QQ(2L, 3L))), Times(Power(Negate(a), C1D3),
                                  Power(b, C1D3), BSymbol)),
                              Times(C2, Power(Negate(a), QQ(2L, 3L)), CSymbol)), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1866,
              Integrate(Times($p("§p2"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                      Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(List(Set(q, Times(Power(a, C1D3), Power(Negate(b), CN1D3)))),
                              Plus(
                                  Negate(
                                      Dist(Times(CSymbol, Power(b, CN1)),
                                          Integrate(Power(Subtract(q, x), CN1), x), x)),
                                  Dist(Times(Subtract(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1), x),
                                      x))),
                          EqQ(Subtract(
                              Plus(
                                  Times(ASymbol, Power(Negate(b),
                                      QQ(2L, 3L))),
                                  Times(Power(a, C1D3), Power(Negate(b), C1D3), BSymbol)),
                              Times(C2, Power(a, QQ(2L, 3L)), CSymbol)), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1867,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(ASymbol, Coeff($s(
                              "§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x,
                              C1)),
                          Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(
                              List(Set(q,
                                  Power(Times(a, Power(b, CN1)), C1D3))),
                              Plus(
                                  Dist(
                                      Times(CSymbol, Power(b,
                                          CN1)),
                                      Integrate(Power(Plus(q, x), CN1), x), x),
                                  Dist(Times(Plus(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1),
                                          x),
                                      x))),
                          EqQ(Subtract(
                              Subtract(ASymbol, Times(Power(Times(a, Power(b, CN1)), C1D3),
                                  BSymbol)),
                              Times(C2, Power(Times(a, Power(b, CN1)), QQ(2L, 3L)), CSymbol)),
                              C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1868,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x,
                              C1)),
                          Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(
                              List(Set(q,
                                  Rt(Times(a, Power(b, CN1)), C3))),
                              Plus(
                                  Dist(Times(CSymbol, Power(b, CN1)),
                                      Integrate(Power(Plus(q, x), CN1), x), x),
                                  Dist(Times(Plus(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1),
                                          x),
                                      x))),
                          EqQ(Subtract(
                              Subtract(ASymbol, Times(Rt(Times(a, Power(b, CN1)), C3), BSymbol)),
                              Times(C2, Sqr(Rt(Times(a, Power(b, CN1)), C3)), CSymbol)), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1869,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(ASymbol, Coeff($s(
                              "§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x,
                              C1)),
                          Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(
                              List(Set(q,
                                  Power(Times(CN1, a, Power(b, CN1)), C1D3))),
                              Plus(
                                  Negate(
                                      Dist(
                                          Times(CSymbol, Power(b, CN1)), Integrate(
                                              Power(Subtract(q, x), CN1), x),
                                          x)),
                                  Dist(Times(Subtract(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1), x),
                                      x))),
                          EqQ(Subtract(
                              Plus(
                                  ASymbol, Times(Power(Times(CN1, a, Power(b, CN1)), C1D3),
                                      BSymbol)),
                              Times(C2, Power(Times(CN1, a, Power(b, CN1)), QQ(2L, 3L)), CSymbol)),
                              C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1870,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                      Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(
                          With(List(Set(q, Rt(Times(CN1, a, Power(b, CN1)), C3))),
                              Plus(
                                  Negate(
                                      Dist(Times(CSymbol, Power(b, CN1)),
                                          Integrate(Power(Subtract(q, x), CN1), x), x)),
                                  Dist(Times(Subtract(BSymbol, Times(CSymbol, q)), Power(b, CN1)),
                                      Integrate(Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1), x),
                                      x))),
                          EqQ(Subtract(Plus(ASymbol, Times(Rt(Times(CN1, a, Power(b, CN1)), C3),
                              BSymbol)), Times(C2, Sqr(Rt(Times(CN1, a, Power(b, CN1)), C3)),
                                  CSymbol)),
                              C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1871,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(ASymbol, Coeff($s(
                              "§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol,
                              Coeff($s("§p2"), x, C2))),
                      Condition(
                          Plus(
                              Integrate(Times(Plus(ASymbol, Times(BSymbol, x)),
                                  Power(Plus(a, Times(b, Power(x, C3))), CN1)), x),
                              Dist(CSymbol,
                                  Integrate(
                                      Times(Sqr(x), Power(Plus(a, Times(b, Power(x, C3))), CN1)),
                                      x),
                                  x)),
                          Or(EqQ(Subtract(Times(a, Power(BSymbol, C3)),
                              Times(b, Power(ASymbol, C3))), C0), Not(
                                  RationalQ(Times(a, Power(b, CN1))))))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1872,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                      Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                      Condition(With(List(Set(q, Power(Times(a, Power(b, CN1)), C1D3))),
                          Dist(Times(Sqr(q), Power(a, CN1)),
                              Integrate(
                                  Times(Plus(ASymbol, Times(CSymbol, q, x)),
                                      Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1)),
                                  x),
                              x)),
                          EqQ(Plus(ASymbol,
                              Times(CN1, BSymbol, Power(Times(a,
                                  Power(b, CN1)), C1D3)),
                              Times(CSymbol, Power(Times(a, Power(b, CN1)), QQ(2L, 3L)))),
                              C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1873,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(With(List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                  Set(BSymbol, Coeff($s("§p2"), x, C1)), Set(CSymbol, Coeff($s("§p2"), x, C2))),
                  Condition(
                      With(
                          List(Set(q, Power(Times(CN1, a, Power(b, CN1)), C1D3))),
                          Dist(
                              Times(q, Power(a, CN1)),
                              Integrate(Times(
                                  Plus(Times(ASymbol, q),
                                      Times(Plus(ASymbol, Times(BSymbol, q)), x)),
                                  Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1)), x),
                              x)),
                      EqQ(Plus(ASymbol, Times(BSymbol, Power(Times(CN1, a, Power(b, CN1)), C1D3)),
                          Times(CSymbol, Power(Times(CN1, a, Power(b, CN1)), QQ(2L, 3L)))), C0))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2)))),
          IIntegrate(1874,
              Integrate(
                  Times($p("§p2"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x, C1)),
                          Set(CSymbol, Coeff($s("§p2"), x, C2)), Set(q,
                              Power(Times(a, Power(b, CN1)), C1D3))),
                      Condition(
                          Plus(Dist(Times(q,
                              Plus(ASymbol, Times(CN1, BSymbol, q),
                                  Times(CSymbol, Sqr(q))),
                              Power(Times(C3, a), CN1)), Integrate(Power(Plus(q, x), CN1), x), x),
                              Dist(Times(q, Power(Times(C3, a), CN1)),
                                  Integrate(Times(
                                      Subtract(
                                          Times(q,
                                              Subtract(Plus(Times(C2, ASymbol), Times(BSymbol, q)),
                                                  Times(CSymbol, Sqr(q)))),
                                          Times(Subtract(Subtract(ASymbol, Times(BSymbol, q)),
                                              Times(C2, CSymbol, Sqr(q))), x)),
                                      Power(Plus(Sqr(q), Times(CN1, q, x), Sqr(x)), CN1)), x),
                                  x)),
                          And(NeQ(
                              Subtract(Times(a, Power(BSymbol, C3)), Times(b, Power(ASymbol, C3))),
                              C0),
                              NeQ(Plus(ASymbol, Times(CN1, BSymbol, q), Times(CSymbol, Sqr(q))),
                                  C0)))),
                  And(FreeQ(List(a,
                      b), x), PolyQ($s("§p2"), x,
                          C2),
                      GtQ(Times(a, Power(b, CN1)), C0)))),
          IIntegrate(1875,
              Integrate(Times($p("§p2"),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)), x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p2"), x, C0)),
                          Set(BSymbol, Coeff($s("§p2"), x, C1)),
                          Set(CSymbol, Coeff($s("§p2"), x, C2)),
                          Set(q, Power(Times(CN1, a, Power(b, CN1)), C1D3))),
                      Condition(Plus(
                          Dist(Times(q, Plus(ASymbol, Times(BSymbol, q), Times(CSymbol, Sqr(q))),
                              Power(Times(C3, a), CN1)), Integrate(Power(Subtract(q, x), CN1), x),
                              x),
                          Dist(
                              Times(q, Power(Times(C3, a),
                                  CN1)),
                              Integrate(Times(
                                  Plus(
                                      Times(q,
                                          Subtract(Subtract(Times(C2, ASymbol), Times(BSymbol, q)),
                                              Times(CSymbol, Sqr(q)))),
                                      Times(Subtract(Plus(ASymbol, Times(BSymbol, q)),
                                          Times(C2, CSymbol, Sqr(q))), x)),
                                  Power(Plus(Sqr(q), Times(q, x), Sqr(x)), CN1)), x),
                              x)),
                          And(NeQ(
                              Subtract(Times(a, Power(BSymbol, C3)),
                                  Times(b, Power(ASymbol, C3))),
                              C0),
                              NeQ(Plus(ASymbol, Times(BSymbol, q), Times(CSymbol, Sqr(q))), C0)))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p2"), x, C2),
                      LtQ(Times(a, Power(b, CN1)), C0)))),
          IIntegrate(1876,
              Integrate(
                  Times($p("§pq"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(v,
                              Sum(Times(
                                  Power(x, $s(
                                      "ii")),
                                  Plus(Coeff($s("§pq"), x, $s("ii")),
                                      Times(Coeff($s("§pq"), x, Plus(Times(C1D2, n), $s("ii"))),
                                          Power(x, Times(C1D2, n)))),
                                  Power(Plus(a, Times(b, Power(x, n))), CN1)),
                                  List($s("ii"), C0, Subtract(Times(C1D2, n), C1))))),
                      Condition(Integrate(v, x), SumQ(v))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), IGtQ(Times(C1D2, n), C0),
                      Less(Expon($s("§pq"), x), n)))),
          IIntegrate(1877,
              Integrate(Times(
                  Plus(c_, Times(d_DEFAULT, x_)),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2)), x_Symbol),
              Condition(With(
                  List(Set(
                      r, $($s("§numer"), Simplify(Times(Subtract(C1, CSqrt3), d, Power(c, CN1))))),
                      Set(s, $(
                          $s("§denom"), Simplify(Times(Subtract(C1, CSqrt3), d, Power(c, CN1)))))),
                  Subtract(
                      Simp(
                          Times(
                              C2, d, Power(s, C3), Sqrt(Plus(a, Times(b, Power(x, C3)))),
                              Power(
                                  Times(a, Sqr(r), Plus(Times(Plus(C1,
                                      CSqrt3), s), Times(r,
                                          x))),
                                  CN1)),
                          x),
                      Simp(Times(Power(C3, C1D4), Sqrt(Subtract(C2, CSqrt3)), d, s,
                          Plus(s, Times(r, x)),
                          Sqrt(
                              Times(Plus(Sqr(s), Times(CN1, r, s, x), Times(Sqr(r), Sqr(x))), Power(
                                  Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)), CN2))),
                          EllipticE(
                              ArcSin(
                                  Times(
                                      Plus(Times(Subtract(C1,
                                          CSqrt3), s), Times(r,
                                              x)),
                                      Power(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)), CN1))),
                              Subtract(CN7, Times(C4, CSqrt3))),
                          Power(
                              Times(Sqr(r), Sqrt(Plus(a, Times(b, Power(x, C3)))),
                                  Sqrt(Times(s, Plus(s, Times(r, x)),
                                      Power(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)), CN2)))),
                              CN1)),
                          x))),
                  And(FreeQ(List(a, b, c,
                      d), x), PosQ(a), EqQ(
                          Subtract(
                              Times(b, Power(c, C3)), Times(C2, Subtract(C5, Times(C3, CSqrt3)), a,
                                  Power(d, C3))),
                          C0)))),
          IIntegrate(1878,
              Integrate(
                  Times(
                      Plus(c_, Times(d_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, $($s("§numer"), Rt(Times(b, Power(a, CN1)), C3))), Set(s,
                              $($s("§denom"), Rt(Times(b, Power(a, CN1)), C3)))),
                      Plus(
                          Dist(Times(Subtract(Times(c, r),
                              Times(Subtract(C1, CSqrt3), d, s)), Power(r, CN1)), Integrate(
                                  Power(Plus(a, Times(b, Power(x, C3))), CN1D2), x),
                              x),
                          Dist(Times(d, Power(r, CN1)),
                              Integrate(
                                  Times(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                      Power(Plus(a, Times(b, Power(x, C3))), CN1D2)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), PosQ(a),
                      NeQ(Subtract(
                          Times(b, Power(c, C3)), Times(C2, Subtract(C5, Times(C3, CSqrt3)), a,
                              Power(d, C3))),
                          C0)))),
          IIntegrate(1879,
              Integrate(
                  Times(
                      Plus(c_, Times(d_DEFAULT, x_)), Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, $($s("§numer"),
                              Simplify(Times(Plus(C1, CSqrt3), d, Power(c, CN1))))),
                          Set(s,
                              $($s("§denom"),
                                  Simplify(Times(Plus(C1, CSqrt3), d, Power(c, CN1)))))),
                      Plus(
                          Simp(
                              Times(
                                  C2, d, Power(s, C3), Sqrt(Plus(a,
                                      Times(b, Power(x, C3)))),
                                  Power(
                                      Times(
                                          a, Sqr(r), Plus(Times(Subtract(C1,
                                              CSqrt3), s), Times(r, x))),
                                      CN1)),
                              x),
                          Simp(Times(Power(C3, C1D4), Sqrt(Plus(C2, CSqrt3)), d, s,
                              Plus(s, Times(r, x)),
                              Sqrt(
                                  Times(Plus(Sqr(s), Times(CN1, r, s, x), Times(Sqr(r), Sqr(x))),
                                      Power(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                          CN2))),
                              EllipticE(
                                  ArcSin(
                                      Times(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)),
                                          Power(
                                              Plus(Times(Subtract(C1, CSqrt3), s),
                                                  Times(r, x)),
                                              CN1))),
                                  Plus(CN7, Times(C4, CSqrt3))),
                              Power(Times(Sqr(r), Sqrt(Plus(a, Times(b, Power(x, C3)))),
                                  Sqrt(Times(CN1, s, Plus(s, Times(r, x)),
                                      Power(Plus(Times(Subtract(C1, CSqrt3), s), Times(r, x)),
                                          CN2)))),
                                  CN1)),
                              x))),
                  And(FreeQ(List(a, b, c,
                      d), x), NegQ(a), EqQ(
                          Subtract(
                              Times(b, Power(c,
                                  C3)),
                              Times(C2, Plus(C5, Times(C3, CSqrt3)), a, Power(d, C3))),
                          C0)))),
          IIntegrate(1880,
              Integrate(
                  Times(
                      Plus(c_, Times(d_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, $($s("§numer"),
                              Rt(Times(b, Power(a, CN1)), C3))),
                          Set(s, $($s("§denom"), Rt(Times(b, Power(a, CN1)), C3)))),
                      Plus(
                          Dist(
                              Times(Subtract(Times(c, r), Times(Plus(C1, CSqrt3), d, s)),
                                  Power(r, CN1)),
                              Integrate(Power(Plus(a, Times(b, Power(x, C3))), CN1D2), x), x),
                          Dist(Times(d, Power(r, CN1)),
                              Integrate(Times(Plus(Times(Plus(C1, CSqrt3), s), Times(r, x)),
                                  Power(Plus(a, Times(b, Power(x, C3))), CN1D2)), x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), NegQ(a), NeQ(Subtract(Times(b, Power(c, C3)),
                      Times(C2, Plus(C5, Times(C3, CSqrt3)), a, Power(d, C3))), C0)))));
}
